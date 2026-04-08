package com.graydang.app.domain.politicaltype.service;

import com.graydang.app.domain.politicaltype.dto.request.QuestionSyncRequest;
import com.graydang.app.domain.politicaltype.dto.request.QuestionSyncRequest.QuestionSyncItem;
import com.graydang.app.domain.politicaltype.dto.request.QuestionUpdateRequest;
import com.graydang.app.domain.politicaltype.dto.response.QuestionAdminResponse;
import com.graydang.app.domain.politicaltype.model.PoliticalTypeQuestion;
import com.graydang.app.domain.politicaltype.model.enums.MetricType;
import com.graydang.app.domain.politicaltype.repository.PoliticalTypeQuestionRepository;
import com.graydang.app.global.common.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PoliticalTypeQuestionAdminServiceTest {

    @InjectMocks
    private PoliticalTypeQuestionAdminService questionAdminService;

    @Mock
    private PoliticalTypeQuestionRepository questionRepository;

    private PoliticalTypeQuestion createQuestion(Long id, String content, MetricType type, int order) {
        PoliticalTypeQuestion q = PoliticalTypeQuestion.builder()
                .content(content)
                .metricType(type)
                .reverseScored(false)
                .displayOrder(order)
                .build();
        // 리플렉션으로 id 설정 (JPA 미사용 환경)
        setField(q, "id", id);
        return q;
    }

    @Nested
    @DisplayName("getQuestion 문항 단건 조회 시")
    class GetQuestionTest {

        @Test
        @DisplayName("존재하는 ID면 문항을 반환한다")
        void getQuestion_shouldReturnQuestion() {
            PoliticalTypeQuestion q = createQuestion(1L, "테스트 문항", MetricType.PARTICIPATION, 1);
            given(questionRepository.findById(1L)).willReturn(Optional.of(q));

            QuestionAdminResponse response = questionAdminService.getQuestion(1L);

            assertThat(response.getContent()).isEqualTo("테스트 문항");
            assertThat(response.getDisplayOrder()).isEqualTo(1);
            System.out.println("✅ getQuestion 테스트 통과 - 단건 조회 성공");
        }

        @Test
        @DisplayName("존재하지 않는 ID면 BusinessException을 던진다")
        void getQuestion_shouldThrowWhenNotFound() {
            given(questionRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> questionAdminService.getQuestion(999L))
                    .isInstanceOf(BusinessException.class);

            System.out.println("✅ getQuestion_shouldThrowWhenNotFound 테스트 통과");
        }
    }

    @Nested
    @DisplayName("updateQuestion 문항 단건 수정 시")
    class UpdateQuestionTest {

        @Test
        @DisplayName("내용만 수정하고 displayOrder는 변경하지 않는다")
        void updateQuestion_shouldUpdateContentOnly() {
            PoliticalTypeQuestion q = createQuestion(1L, "원본", MetricType.PARTICIPATION, 1);
            given(questionRepository.findById(1L)).willReturn(Optional.of(q));

            QuestionUpdateRequest request = new QuestionUpdateRequest();
            setField(request, "content", "수정된 문항");
            setField(request, "metricType", MetricType.CHANGE_PREFERENCE);
            setField(request, "reverseScored", true);

            QuestionAdminResponse response = questionAdminService.updateQuestion(1L, request);

            assertThat(response.getContent()).isEqualTo("수정된 문항");
            assertThat(response.getMetricType()).isEqualTo(MetricType.CHANGE_PREFERENCE);
            assertThat(response.getDisplayOrder()).isEqualTo(1);
            System.out.println("✅ updateQuestion 테스트 통과 - 내용만 수정, displayOrder 유지");
        }
    }

    @Nested
    @DisplayName("deleteQuestion 문항 삭제 시")
    class DeleteQuestionTest {

        @Test
        @DisplayName("삭제 후 나머지 문항 displayOrder가 재정렬된다")
        void deleteQuestion_shouldReorderRemaining() {
            PoliticalTypeQuestion q1 = createQuestion(1L, "Q1", MetricType.PARTICIPATION, 1);
            PoliticalTypeQuestion q2 = createQuestion(2L, "Q2", MetricType.CHANGE_PREFERENCE, 2);
            PoliticalTypeQuestion q3 = createQuestion(3L, "Q3", MetricType.VALUE_ORIENTATION, 3);

            given(questionRepository.findById(2L)).willReturn(Optional.of(q2));
            // 삭제 후 남은 문항 (q1, q3)
            given(questionRepository.findAllByOrderByDisplayOrderAsc())
                    .willReturn(new ArrayList<>(List.of(q1, q3)));

            questionAdminService.deleteQuestion(2L);

            verify(questionRepository).deleteById(2L);
            // q3의 displayOrder가 3 → 2로 변경되어야 함
            assertThat(q3.getDisplayOrder()).isEqualTo(2);
            System.out.println("✅ deleteQuestion 테스트 통과 - displayOrder 재정렬 확인 (q3: 3→2)");
        }
    }

    @Nested
    @DisplayName("syncQuestions 벌크 동기화 시")
    class SyncQuestionsTest {

        @Test
        @DisplayName("신규 생성, 수정, 삭제를 한 번에 처리한다")
        void syncQuestions_shouldCreateUpdateDelete() {
            // 기존 DB: Q1(id=1), Q2(id=2)
            PoliticalTypeQuestion q1 = createQuestion(1L, "Q1", MetricType.PARTICIPATION, 1);
            PoliticalTypeQuestion q2 = createQuestion(2L, "Q2", MetricType.CHANGE_PREFERENCE, 2);
            given(questionRepository.findAll()).willReturn(new ArrayList<>(List.of(q1, q2)));

            // 요청: [Q1 수정, 신규] → Q2는 삭제됨
            QuestionSyncItem item1 = new QuestionSyncItem();
            setField(item1, "id", 1L);
            setField(item1, "content", "Q1 수정됨");
            setField(item1, "metricType", MetricType.PARTICIPATION);

            QuestionSyncItem newItem = new QuestionSyncItem();
            setField(newItem, "content", "새 문항");
            setField(newItem, "metricType", MetricType.VALUE_ORIENTATION);

            QuestionSyncRequest request = new QuestionSyncRequest();
            setField(request, "questions", List.of(item1, newItem));

            PoliticalTypeQuestion savedNew = createQuestion(3L, "새 문항", MetricType.VALUE_ORIENTATION, 2);
            given(questionRepository.save(any(PoliticalTypeQuestion.class))).willReturn(savedNew);

            List<QuestionAdminResponse> results = questionAdminService.syncQuestions(request);

            // Q2 삭제 확인
            verify(questionRepository).deleteAllById(List.of(2L));
            // Q1 수정 확인
            assertThat(q1.getContent()).isEqualTo("Q1 수정됨");
            assertThat(q1.getDisplayOrder()).isEqualTo(1);
            // 결과 2건
            assertThat(results).hasSize(2);
            System.out.println("✅ syncQuestions 테스트 통과 - 생성/수정/삭제 일괄 처리");
        }

        @Test
        @DisplayName("순서만 변경하면 displayOrder만 업데이트된다")
        void syncQuestions_reorderOnly_shouldUpdateDisplayOrder() {
            // 기존: Q1(order=1), Q2(order=2)
            PoliticalTypeQuestion q1 = createQuestion(1L, "Q1", MetricType.PARTICIPATION, 1);
            PoliticalTypeQuestion q2 = createQuestion(2L, "Q2", MetricType.CHANGE_PREFERENCE, 2);
            given(questionRepository.findAll()).willReturn(new ArrayList<>(List.of(q1, q2)));

            // 요청: [Q2, Q1] → 순서 뒤집기
            QuestionSyncItem item2 = new QuestionSyncItem();
            setField(item2, "id", 2L);
            setField(item2, "content", "Q2");
            setField(item2, "metricType", MetricType.CHANGE_PREFERENCE);

            QuestionSyncItem item1 = new QuestionSyncItem();
            setField(item1, "id", 1L);
            setField(item1, "content", "Q1");
            setField(item1, "metricType", MetricType.PARTICIPATION);

            QuestionSyncRequest request = new QuestionSyncRequest();
            setField(request, "questions", List.of(item2, item1));

            List<QuestionAdminResponse> results = questionAdminService.syncQuestions(request);

            assertThat(q2.getDisplayOrder()).isEqualTo(1);
            assertThat(q1.getDisplayOrder()).isEqualTo(2);
            assertThat(results).hasSize(2);
            System.out.println("✅ syncQuestions 순서변경만 테스트 통과 - Q2→1, Q1→2");
        }

        @Test
        @DisplayName("빈 DB에서 전부 신규 생성한다")
        void syncQuestions_emptyDb_shouldCreateAll() {
            given(questionRepository.findAll()).willReturn(new ArrayList<>());

            QuestionSyncItem newItem1 = new QuestionSyncItem();
            setField(newItem1, "content", "새 문항 1");
            setField(newItem1, "metricType", MetricType.PARTICIPATION);

            QuestionSyncItem newItem2 = new QuestionSyncItem();
            setField(newItem2, "content", "새 문항 2");
            setField(newItem2, "metricType", MetricType.CHANGE_PREFERENCE);

            QuestionSyncRequest request = new QuestionSyncRequest();
            setField(request, "questions", List.of(newItem1, newItem2));

            PoliticalTypeQuestion saved1 = createQuestion(1L, "새 문항 1", MetricType.PARTICIPATION, 1);
            PoliticalTypeQuestion saved2 = createQuestion(2L, "새 문항 2", MetricType.CHANGE_PREFERENCE, 2);
            given(questionRepository.save(any(PoliticalTypeQuestion.class)))
                    .willReturn(saved1, saved2);

            List<QuestionAdminResponse> results = questionAdminService.syncQuestions(request);

            assertThat(results).hasSize(2);
            System.out.println("✅ syncQuestions 전부 신규 테스트 통과 - 2건 생성");
        }

        @Test
        @DisplayName("빈 리스트 전송 시 기존 문항 전부 삭제한다")
        void syncQuestions_emptyList_shouldDeleteAll() {
            PoliticalTypeQuestion q1 = createQuestion(1L, "Q1", MetricType.PARTICIPATION, 1);
            PoliticalTypeQuestion q2 = createQuestion(2L, "Q2", MetricType.CHANGE_PREFERENCE, 2);
            given(questionRepository.findAll()).willReturn(new ArrayList<>(List.of(q1, q2)));

            QuestionSyncRequest request = new QuestionSyncRequest();
            setField(request, "questions", List.of());

            List<QuestionAdminResponse> results = questionAdminService.syncQuestions(request);

            verify(questionRepository).deleteAllById(List.of(1L, 2L));
            assertThat(results).isEmpty();
            System.out.println("✅ syncQuestions 전부 삭제 테스트 통과 - 빈 리스트 → 2건 삭제");
        }

        @Test
        @DisplayName("존재하지 않는 ID가 요청에 있으면 BusinessException을 던진다")
        void syncQuestions_invalidId_shouldThrow() {
            given(questionRepository.findAll()).willReturn(new ArrayList<>());

            QuestionSyncItem invalidItem = new QuestionSyncItem();
            setField(invalidItem, "id", 999L);
            setField(invalidItem, "content", "존재하지 않는 문항");
            setField(invalidItem, "metricType", MetricType.PARTICIPATION);

            QuestionSyncRequest request = new QuestionSyncRequest();
            setField(request, "questions", List.of(invalidItem));

            assertThatThrownBy(() -> questionAdminService.syncQuestions(request))
                    .isInstanceOf(BusinessException.class);

            System.out.println("✅ syncQuestions 존재하지 않는 ID 테스트 통과 - 예외 발생");
        }
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException e) {
            // 부모 클래스 탐색
            try {
                var field = target.getClass().getSuperclass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
