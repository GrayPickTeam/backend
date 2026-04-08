package com.graydang.app.domain.politicaltype.service;

import com.graydang.app.domain.politicaltype.dto.request.TestSubmitRequest;
import com.graydang.app.domain.politicaltype.dto.response.*;
import com.graydang.app.domain.politicaltype.model.PoliticalTypeAnimal;
import com.graydang.app.domain.politicaltype.model.PoliticalTypeQuestion;
import com.graydang.app.domain.politicaltype.model.PoliticalTypeTestResult;
import com.graydang.app.domain.politicaltype.model.enums.ActionStyle;
import com.graydang.app.domain.politicaltype.model.enums.MetricType;
import com.graydang.app.domain.politicaltype.model.enums.ScoreLevel;
import com.graydang.app.domain.politicaltype.repository.PoliticalTypeAnimalRepository;
import com.graydang.app.domain.politicaltype.repository.PoliticalTypeQuestionRepository;
import com.graydang.app.domain.politicaltype.repository.PoliticalTypeTestResultRepository;
import com.graydang.app.domain.user.model.User;
import com.graydang.app.global.common.exception.BusinessException;
import com.graydang.app.global.common.model.enums.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** 정치유형 검사 사용자 Service. */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PoliticalTypeService {

    private final PoliticalTypeQuestionRepository questionRepository;
    private final PoliticalTypeAnimalRepository animalRepository;
    private final PoliticalTypeTestResultRepository resultRepository;
    private final PoliticalTypeCalculator calculator;

    /** 검사 문항 전체 조회 (displayOrder 순). */
    public List<QuestionResponse> getQuestions() {
        return questionRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(QuestionResponse::from)
                .toList();
    }

    /**
     * 검사 답변 제출 → 유형 결과 계산.
     * 회원이면 DB 저장(재검사 시 덮어쓰기), 비회원이면 결과만 반환.
     *
     * @param user 인증된 유저 (비회원이면 null)
     */
    @Transactional
    public TestResultResponse submitTest(TestSubmitRequest request, User user) {
        List<PoliticalTypeQuestion> questions = questionRepository.findAllByOrderByDisplayOrderAsc();

        validateAnswerCount(request, questions);

        Map<Long, Integer> answerMap = request.getAnswers().stream()
                .collect(Collectors.toMap(
                        TestSubmitRequest.AnswerItem::getQuestionId,
                        TestSubmitRequest.AnswerItem::getScore
                ));


        Map<MetricType, Double> metricScores = calculator.calculateMetricScores(answerMap, questions);

        double participationAvg = metricScores.getOrDefault(MetricType.PARTICIPATION, 0.0);
        double changePreferenceAvg = metricScores.getOrDefault(MetricType.CHANGE_PREFERENCE, 0.0);
        double valueOrientationAvg = metricScores.getOrDefault(MetricType.VALUE_ORIENTATION, 0.0);


        ActionStyle actionStyle = calculator.determineActionStyle(participationAvg);


        ScoreLevel changeLevel = calculator.determineScoreLevel(changePreferenceAvg);
        ScoreLevel valueLevel = calculator.determineScoreLevel(valueOrientationAvg);

        PoliticalTypeAnimal animal = animalRepository
                .findByChangePreferenceLevelAndValueOrientationLevel(changeLevel, valueLevel)
                .orElseThrow(() -> new BusinessException(BaseResponseStatus.POLITICAL_TYPE_ANIMAL_MAPPING_FAILED));


        boolean saved = false;
        if (user != null) {
            saveOrUpdateResult(user, actionStyle, animal, participationAvg, changePreferenceAvg, valueOrientationAvg);
            saved = true;
        }

        return buildResultResponse(actionStyle, animal, participationAvg, changePreferenceAvg, valueOrientationAvg, saved);
    }

    /** 비회원 → 로그인 후 결과 저장. 프론트 보관 답변을 재전송받아 저장. */
    @Transactional
    public TestResultResponse saveResult(TestSubmitRequest request, User user) {
        return submitTest(request, user);
    }

    /** 내 검사 결과 조회. 결과가 없으면 예외 발생. */
    public TestResultResponse getMyResult(User user) {
        PoliticalTypeTestResult result = resultRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(BaseResponseStatus.POLITICAL_TYPE_RESULT_NOT_FOUND));
        return TestResultResponse.from(result);
    }

    /** 유형별 랭킹 조회. 동물유형별 인원수와 비율 반환. */
    public RankingResponse getRanking() {
        long totalParticipants = resultRepository.count();
        List<Object[]> animalCounts = resultRepository.countByAnimalGroupBy();

        // 최대 9종이므로 전체 로딩하여 N+1 방지
        Map<Long, PoliticalTypeAnimal> animalMap = animalRepository.findAll().stream()
                .collect(Collectors.toMap(PoliticalTypeAnimal::getId, a -> a));

        List<RankingResponse.RankingItem> rankings = IntStream.range(0, animalCounts.size())
                .mapToObj(i -> {
                    Object[] row = animalCounts.get(i);
                    Long animalId = (Long) row[0];
                    Long count = (Long) row[1];
                    PoliticalTypeAnimal animal = animalMap.get(animalId);

                    return RankingResponse.RankingItem.builder()
                            .rank(i + 1)
                            .animalCode(animal != null ? animal.getCode() : "UNKNOWN")
                            .animalName(animal != null ? animal.getName() : "알 수 없음")
                            .imageUrl(animal != null ? animal.getImageUrl() : null)
                            .count(count)
                            .percentage(totalParticipants > 0
                                    ? Math.round(count * 1000.0 / totalParticipants) / 10.0
                                    : 0.0)
                            .build();
                })
                .toList();

        return RankingResponse.builder()
                .totalParticipants(totalParticipants)
                .rankings(rankings)
                .build();
    }

    /** 특정 동물유형 상세 조회. @param code 동물 코드 */
    public AnimalDetailResponse getAnimalDetail(String code) {
        PoliticalTypeAnimal animal = animalRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(BaseResponseStatus.POLITICAL_TYPE_ANIMAL_NOT_FOUND));
        return AnimalDetailResponse.from(animal);
    }

    /** 전체 참여자 수 조회. */
    public long getParticipantCount() {
        return resultRepository.count();
    }



    private void validateAnswerCount(TestSubmitRequest request, List<PoliticalTypeQuestion> questions) {
        if (request.getAnswers().size() != questions.size()) {
            throw new BusinessException(BaseResponseStatus.POLITICAL_TYPE_INVALID_ANSWER_COUNT);
        }
    }

    /** upsert: 유저당 1건 유지. */
    private void saveOrUpdateResult(User user, ActionStyle actionStyle, PoliticalTypeAnimal animal,
                                     double participation, double changePreference, double valueOrientation) {
        resultRepository.findByUser(user)
                .ifPresentOrElse(
                        existing -> existing.updateResult(actionStyle, animal, participation, changePreference, valueOrientation),
                        () -> resultRepository.save(PoliticalTypeTestResult.builder()
                                .user(user)
                                .actionStyle(actionStyle)
                                .animal(animal)
                                .participationScore(participation)
                                .changePreferenceScore(changePreference)
                                .valueOrientationScore(valueOrientation)
                                .build())
                );
    }

    private TestResultResponse buildResultResponse(ActionStyle actionStyle, PoliticalTypeAnimal animal,
                                                    double participation, double changePreference,
                                                    double valueOrientation, boolean saved) {
        return TestResultResponse.builder()
                .actionStyleCode(actionStyle.name())
                .actionStyleName(actionStyle.getKorean())
                .animal(TestResultResponse.AnimalInfo.from(animal))
                .participationScore(participation)
                .changePreferenceScore(changePreference)
                .valueOrientationScore(valueOrientation)
                .saved(saved)
                .build();
    }
}
