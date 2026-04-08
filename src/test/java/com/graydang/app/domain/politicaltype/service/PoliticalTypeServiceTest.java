package com.graydang.app.domain.politicaltype.service;

import com.graydang.app.domain.politicaltype.dto.request.TestSubmitRequest;
import com.graydang.app.domain.politicaltype.dto.response.AnimalDetailResponse;
import com.graydang.app.domain.politicaltype.dto.response.QuestionResponse;
import com.graydang.app.domain.politicaltype.dto.response.RankingResponse;
import com.graydang.app.domain.politicaltype.dto.response.TestResultResponse;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PoliticalTypeServiceTest {

    @InjectMocks
    private PoliticalTypeService politicalTypeService;

    @Mock
    private PoliticalTypeQuestionRepository questionRepository;

    @Mock
    private PoliticalTypeAnimalRepository animalRepository;

    @Mock
    private PoliticalTypeTestResultRepository resultRepository;

    @Mock
    private PoliticalTypeCalculator calculator;

    // ── 공통 테스트 헬퍼 ──────────────────────────────────────

    private PoliticalTypeQuestion createQuestion(Long id, MetricType type) {
        PoliticalTypeQuestion q = PoliticalTypeQuestion.builder()
                .content("테스트 문항 " + id)
                .metricType(type)
                .reverseScored(false)
                .displayOrder(id.intValue())
                .build();
        setId(q, id);
        return q;
    }

    private PoliticalTypeAnimal createAnimal(Long id, String code, String name) {
        PoliticalTypeAnimal animal = PoliticalTypeAnimal.builder()
                .code(code)
                .name(name)
                .subtitle("테스트 부제")
                .oneLiner("테스트 한줄")
                .description("테스트 설명")
                .keywords(List.of("키워드1"))
                .representativeFigures(List.of("인물1"))
                .changePreferenceLevel(ScoreLevel.HIGH)
                .valueOrientationLevel(ScoreLevel.HIGH)
                .build();
        setId(animal, id);
        return animal;
    }

    private List<PoliticalTypeQuestion> createQuestions(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> createQuestion((long) i, MetricType.values()[(i - 1) % 3]))
                .toList();
    }

    private TestSubmitRequest createSubmitRequest(int count) {
        List<TestSubmitRequest.AnswerItem> answers = java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> new TestSubmitRequest.AnswerItem((long) i, 4))
                .toList();
        return new TestSubmitRequest(answers);
    }

    // ── Tests ────────────────────────────────────────────────

    @Nested
    @DisplayName("getQuestions 문항 조회 시")
    class GetQuestionsTest {

        @Test
        @DisplayName("displayOrder 순으로 정렬된 문항을 반환한다")
        void getQuestions_shouldReturnSortedQuestions() {
            List<PoliticalTypeQuestion> questions = createQuestions(3);
            given(questionRepository.findAllByOrderByDisplayOrderAsc()).willReturn(questions);

            List<QuestionResponse> result = politicalTypeService.getQuestions();

            assertThat(result).hasSize(3);
            assertThat(result.get(0).getQuestionNumber()).isEqualTo(1);
            System.out.println("✅ 문항 조회 테스트 통과 - " + result.size() + "건 반환");
        }
    }

    @Nested
    @DisplayName("submitTest 검사 제출 시")
    class SubmitTestTest {

        @Test
        @DisplayName("비회원이 제출하면 결과만 반환하고 저장하지 않는다")
        void submitTest_guest_shouldReturnResultWithoutSaving() {
            List<PoliticalTypeQuestion> questions = createQuestions(3);
            TestSubmitRequest request = createSubmitRequest(3);
            PoliticalTypeAnimal dolphin = createAnimal(1L, "DOLPHIN", "돌고래");

            given(questionRepository.findAllByOrderByDisplayOrderAsc()).willReturn(questions);
            given(calculator.calculateMetricScores(any(), any()))
                    .willReturn(Map.of(
                            MetricType.PARTICIPATION, 4.2,
                            MetricType.CHANGE_PREFERENCE, 3.8,
                            MetricType.VALUE_ORIENTATION, 4.0));
            given(calculator.determineActionStyle(4.2)).willReturn(ActionStyle.FLYING);
            given(calculator.determineScoreLevel(3.8)).willReturn(ScoreLevel.HIGH);
            given(calculator.determineScoreLevel(4.0)).willReturn(ScoreLevel.HIGH);
            given(animalRepository.findByChangePreferenceLevelAndValueOrientationLevel(ScoreLevel.HIGH, ScoreLevel.HIGH))
                    .willReturn(Optional.of(dolphin));

            TestResultResponse result = politicalTypeService.submitTest(request, null);

            assertThat(result.getSaved()).isFalse();
            assertThat(result.getActionStyleCode()).isEqualTo("FLYING");
            assertThat(result.getAnimal().getCode()).isEqualTo("DOLPHIN");
            verify(resultRepository, never()).save(any());
            System.out.println("✅ 비회원 제출 테스트 통과 - 저장 안 됨, FLYING 돌고래");
        }

        @Test
        @DisplayName("회원이 제출하면 결과를 저장한다 (첫 검사)")
        void submitTest_member_shouldSaveResult() {
            List<PoliticalTypeQuestion> questions = createQuestions(3);
            TestSubmitRequest request = createSubmitRequest(3);
            PoliticalTypeAnimal dolphin = createAnimal(1L, "DOLPHIN", "돌고래");
            User user = mock(User.class);

            given(questionRepository.findAllByOrderByDisplayOrderAsc()).willReturn(questions);
            given(calculator.calculateMetricScores(any(), any()))
                    .willReturn(Map.of(
                            MetricType.PARTICIPATION, 4.2,
                            MetricType.CHANGE_PREFERENCE, 3.8,
                            MetricType.VALUE_ORIENTATION, 4.0));
            given(calculator.determineActionStyle(4.2)).willReturn(ActionStyle.FLYING);
            given(calculator.determineScoreLevel(3.8)).willReturn(ScoreLevel.HIGH);
            given(calculator.determineScoreLevel(4.0)).willReturn(ScoreLevel.HIGH);
            given(animalRepository.findByChangePreferenceLevelAndValueOrientationLevel(ScoreLevel.HIGH, ScoreLevel.HIGH))
                    .willReturn(Optional.of(dolphin));
            given(resultRepository.findByUser(user)).willReturn(Optional.empty());

            TestResultResponse result = politicalTypeService.submitTest(request, user);

            assertThat(result.getSaved()).isTrue();
            verify(resultRepository).save(any(PoliticalTypeTestResult.class));
            System.out.println("✅ 회원 첫 검사 테스트 통과 - 결과 저장됨");
        }

        @Test
        @DisplayName("회원이 재검사하면 기존 결과를 업데이트한다")
        void submitTest_retest_shouldUpdateExistingResult() {
            List<PoliticalTypeQuestion> questions = createQuestions(3);
            TestSubmitRequest request = createSubmitRequest(3);
            PoliticalTypeAnimal dolphin = createAnimal(1L, "DOLPHIN", "돌고래");
            User user = mock(User.class);
            PoliticalTypeTestResult existingResult = mock(PoliticalTypeTestResult.class);

            given(questionRepository.findAllByOrderByDisplayOrderAsc()).willReturn(questions);
            given(calculator.calculateMetricScores(any(), any()))
                    .willReturn(Map.of(
                            MetricType.PARTICIPATION, 4.2,
                            MetricType.CHANGE_PREFERENCE, 3.8,
                            MetricType.VALUE_ORIENTATION, 4.0));
            given(calculator.determineActionStyle(4.2)).willReturn(ActionStyle.FLYING);
            given(calculator.determineScoreLevel(3.8)).willReturn(ScoreLevel.HIGH);
            given(calculator.determineScoreLevel(4.0)).willReturn(ScoreLevel.HIGH);
            given(animalRepository.findByChangePreferenceLevelAndValueOrientationLevel(ScoreLevel.HIGH, ScoreLevel.HIGH))
                    .willReturn(Optional.of(dolphin));
            given(resultRepository.findByUser(user)).willReturn(Optional.of(existingResult));

            TestResultResponse result = politicalTypeService.submitTest(request, user);

            assertThat(result.getSaved()).isTrue();
            verify(existingResult).updateResult(eq(ActionStyle.FLYING), eq(dolphin), eq(4.2), eq(3.8), eq(4.0));
            verify(resultRepository, never()).save(any());
            System.out.println("✅ 재검사 테스트 통과 - 기존 결과 업데이트됨");
        }

        @Test
        @DisplayName("답변 수가 문항 수와 다르면 예외를 던진다")
        void submitTest_invalidAnswerCount_shouldThrow() {
            List<PoliticalTypeQuestion> questions = createQuestions(3);
            TestSubmitRequest request = createSubmitRequest(2); // 문항 3개인데 답변 2개

            given(questionRepository.findAllByOrderByDisplayOrderAsc()).willReturn(questions);

            assertThatThrownBy(() -> politicalTypeService.submitTest(request, null))
                    .isInstanceOf(BusinessException.class);
            System.out.println("✅ 답변 수 불일치 예외 테스트 통과");
        }
    }

    @Nested
    @DisplayName("getMyResult 내 결과 조회 시")
    class GetMyResultTest {

        @Test
        @DisplayName("결과가 존재하면 반환한다")
        void getMyResult_exists_shouldReturnResult() {
            User user = mock(User.class);
            PoliticalTypeAnimal dolphin = createAnimal(1L, "DOLPHIN", "돌고래");
            PoliticalTypeTestResult testResult = PoliticalTypeTestResult.builder()
                    .user(user)
                    .actionStyle(ActionStyle.FLYING)
                    .animal(dolphin)
                    .participationScore(4.2)
                    .changePreferenceScore(3.8)
                    .valueOrientationScore(4.0)
                    .build();

            given(resultRepository.findByUser(user)).willReturn(Optional.of(testResult));

            TestResultResponse result = politicalTypeService.getMyResult(user);

            assertThat(result.getActionStyleCode()).isEqualTo("FLYING");
            assertThat(result.getAnimal().getName()).isEqualTo("돌고래");
            System.out.println("✅ 내 결과 조회 테스트 통과 - 날아다니는 돌고래");
        }

        @Test
        @DisplayName("결과가 없으면 예외를 던진다")
        void getMyResult_notExists_shouldThrow() {
            User user = mock(User.class);
            given(resultRepository.findByUser(user)).willReturn(Optional.empty());

            assertThatThrownBy(() -> politicalTypeService.getMyResult(user))
                    .isInstanceOf(BusinessException.class);
            System.out.println("✅ 결과 없음 예외 테스트 통과");
        }
    }

    @Nested
    @DisplayName("getRanking 랭킹 조회 시")
    class GetRankingTest {

        @Test
        @DisplayName("동물유형별 집계 결과를 반환한다")
        void getRanking_shouldReturnRankings() {
            PoliticalTypeAnimal dolphin = createAnimal(1L, "DOLPHIN", "돌고래");
            PoliticalTypeAnimal owl = createAnimal(2L, "OWL", "부엉이");

            given(resultRepository.count()).willReturn(100L);
            given(resultRepository.countByAnimalGroupBy())
                    .willReturn(List.of(new Object[]{1L, 60L}, new Object[]{2L, 40L}));
            given(animalRepository.findAll()).willReturn(List.of(dolphin, owl));

            RankingResponse result = politicalTypeService.getRanking();

            assertThat(result.getTotalParticipants()).isEqualTo(100L);
            assertThat(result.getRankings()).hasSize(2);
            assertThat(result.getRankings().get(0).getRank()).isEqualTo(1);
            assertThat(result.getRankings().get(0).getAnimalName()).isEqualTo("돌고래");
            assertThat(result.getRankings().get(0).getPercentage()).isEqualTo(60.0);
            System.out.println("✅ 랭킹 조회 테스트 통과 - 1위 돌고래 60%");
        }
    }

    @Nested
    @DisplayName("getAnimalDetail 유형 상세 조회 시")
    class GetAnimalDetailTest {

        @Test
        @DisplayName("코드로 동물유형 상세를 반환한다")
        void getAnimalDetail_exists_shouldReturn() {
            PoliticalTypeAnimal dolphin = createAnimal(1L, "DOLPHIN", "돌고래");
            given(animalRepository.findByCode("DOLPHIN")).willReturn(Optional.of(dolphin));

            AnimalDetailResponse result = politicalTypeService.getAnimalDetail("DOLPHIN");

            assertThat(result.getCode()).isEqualTo("DOLPHIN");
            assertThat(result.getName()).isEqualTo("돌고래");
            System.out.println("✅ 유형 상세 조회 테스트 통과 - DOLPHIN");
        }

        @Test
        @DisplayName("존재하지 않는 코드면 예외를 던진다")
        void getAnimalDetail_notExists_shouldThrow() {
            given(animalRepository.findByCode("INVALID")).willReturn(Optional.empty());

            assertThatThrownBy(() -> politicalTypeService.getAnimalDetail("INVALID"))
                    .isInstanceOf(BusinessException.class);
            System.out.println("✅ 존재하지 않는 유형 예외 테스트 통과");
        }
    }

    @Nested
    @DisplayName("getParticipantCount 참여자 수 조회 시")
    class GetParticipantCountTest {

        @Test
        @DisplayName("전체 참여자 수를 반환한다")
        void getParticipantCount_shouldReturnCount() {
            given(resultRepository.count()).willReturn(20000L);

            long result = politicalTypeService.getParticipantCount();

            assertThat(result).isEqualTo(20000L);
            System.out.println("✅ 참여자 수 조회 테스트 통과 - 20000명");
        }
    }

    // ── Reflection helper ────────────────────────────────────

    private void setId(Object target, Long id) {
        try {
            var field = target.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(target, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
