package com.graydang.app.domain.politicaltype.service;

import com.graydang.app.domain.politicaltype.dto.request.QuestionSyncRequest;
import com.graydang.app.domain.politicaltype.dto.request.QuestionSyncRequest.QuestionSyncItem;
import com.graydang.app.domain.politicaltype.dto.request.QuestionUpdateRequest;
import com.graydang.app.domain.politicaltype.dto.response.QuestionAdminResponse;
import com.graydang.app.domain.politicaltype.model.PoliticalTypeQuestion;
import com.graydang.app.domain.politicaltype.repository.PoliticalTypeQuestionRepository;
import com.graydang.app.global.common.exception.BusinessException;
import com.graydang.app.global.common.model.enums.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@PreAuthorize("hasRole('ADMIN')")
public class PoliticalTypeQuestionAdminService {

    private final PoliticalTypeQuestionRepository questionRepository;

    /** 전체 문항 조회 (displayOrder 순). */
    public List<QuestionAdminResponse> getAllQuestions() {
        return questionRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(QuestionAdminResponse::from)
                .toList();
    }

    /** 문항 단건 조회. */
    public QuestionAdminResponse getQuestion(Long id) {
        PoliticalTypeQuestion question = findQuestionOrThrow(id);
        return QuestionAdminResponse.from(question);
    }

    /** 문항 단건 수정 (내용만, 순서 변경 없음). */
    @Transactional
    public QuestionAdminResponse updateQuestion(Long id, QuestionUpdateRequest request) {
        PoliticalTypeQuestion question = findQuestionOrThrow(id);

        question.update(
                request.getContent(),
                request.getMetricType(),
                request.getReverseScored()
        );

        return QuestionAdminResponse.from(question);
    }

    /**
     * 문항 단건 삭제 + 나머지 문항 displayOrder 재정렬.
     * 삭제된 문항 이후의 문항들을 앞으로 당긴다.
     */
    @Transactional
    public void deleteQuestion(Long id) {
        PoliticalTypeQuestion target = findQuestionOrThrow(id);
        int deletedOrder = target.getDisplayOrder();

        questionRepository.deleteById(id);

        // 삭제된 순서 이후 문항들 displayOrder -1
        List<PoliticalTypeQuestion> remainingQuestions = questionRepository.findAllByOrderByDisplayOrderAsc();
        for (PoliticalTypeQuestion q : remainingQuestions) {
            if (q.getDisplayOrder() > deletedOrder) {
                q.updateDisplayOrder(q.getDisplayOrder() - 1);
            }
        }
    }

    /**
     * 문항 벌크 동기화.
     * - 배열에 id=null인 항목 → 신규 생성
     * - 배열에 id가 있는 항목 → 내용 업데이트
     * - DB에 있지만 배열에 없는 항목 → 삭제
     * - 배열 순서(index) → displayOrder 자동 부여
     */
    @Transactional
    public List<QuestionAdminResponse> syncQuestions(QuestionSyncRequest request) {
        List<QuestionSyncItem> items = request.getQuestions();

        // 기존 문항 Map
        Map<Long, PoliticalTypeQuestion> existingMap = questionRepository.findAll().stream()
                .collect(Collectors.toMap(PoliticalTypeQuestion::getId, q -> q));

        // 요청에 포함된 기존 ID 수집
        Set<Long> requestedIds = items.stream()
                .map(QuestionSyncItem::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        // DB에만 있고 요청에 없는 문항 → 삭제
        List<Long> toDelete = existingMap.keySet().stream()
                .filter(id -> !requestedIds.contains(id))
                .toList();
        if (!toDelete.isEmpty()) {
            questionRepository.deleteAllById(toDelete);
        }

        // 생성/수정 + displayOrder 부여
        List<QuestionAdminResponse> results = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            QuestionSyncItem item = items.get(i);
            int displayOrder = i + 1;

            if (item.getId() == null) {
                // 신규 생성
                PoliticalTypeQuestion newQuestion = PoliticalTypeQuestion.builder()
                        .content(item.getContent())
                        .metricType(item.getMetricType())
                        .reverseScored(item.getReverseScored() != null ? item.getReverseScored() : false)
                        .displayOrder(displayOrder)
                        .build();
                results.add(QuestionAdminResponse.from(questionRepository.save(newQuestion)));
            } else {
                // 기존 수정
                PoliticalTypeQuestion existing = existingMap.get(item.getId());
                if (existing == null) {
                    throw new BusinessException(BaseResponseStatus.POLITICAL_TYPE_QUESTION_NOT_FOUND);
                }
                existing.update(item.getContent(), item.getMetricType(),
                        item.getReverseScored() != null ? item.getReverseScored() : false);
                existing.updateDisplayOrder(displayOrder);
                results.add(QuestionAdminResponse.from(existing));
            }
        }

        return results;
    }

    private PoliticalTypeQuestion findQuestionOrThrow(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BaseResponseStatus.POLITICAL_TYPE_QUESTION_NOT_FOUND));
    }
}
