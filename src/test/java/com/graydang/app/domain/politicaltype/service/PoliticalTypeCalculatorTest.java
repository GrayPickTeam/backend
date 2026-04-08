package com.graydang.app.domain.politicaltype.service;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeQuestion;
import com.graydang.app.domain.politicaltype.model.enums.ActionStyle;
import com.graydang.app.domain.politicaltype.model.enums.MetricType;
import com.graydang.app.domain.politicaltype.model.enums.ScoreLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PoliticalTypeCalculatorTest {

    private final PoliticalTypeCalculator calculator = new PoliticalTypeCalculator();

    private PoliticalTypeQuestion createQuestion(Long id, MetricType type, boolean reverse) {
        PoliticalTypeQuestion q = PoliticalTypeQuestion.builder()
                .content("테스트 문항")
                .metricType(type)
                .reverseScored(reverse)
                .displayOrder(id.intValue())
                .build();
        setId(q, id);
        return q;
    }

    @Nested
    @DisplayName("calculateMetricScores 지표별 평균 점수 계산 시")
    class CalculateMetricScoresTest {

        @Test
        @DisplayName("역코딩 없는 문항들의 평균을 정확히 계산한다")
        void calculate_normalScoring_shouldReturnCorrectAverages() {
            List<PoliticalTypeQuestion> questions = List.of(
                    createQuestion(1L, MetricType.PARTICIPATION, false),
                    createQuestion(2L, MetricType.PARTICIPATION, false),
                    createQuestion(3L, MetricType.CHANGE_PREFERENCE, false),
                    createQuestion(4L, MetricType.CHANGE_PREFERENCE, false),
                    createQuestion(5L, MetricType.VALUE_ORIENTATION, false),
                    createQuestion(6L, MetricType.VALUE_ORIENTATION, false)
            );

            // 참여도: (5+3)/2=4.0, 변화선호: (4+2)/2=3.0, 가치지향: (1+5)/2=3.0
            Map<Long, Integer> answers = Map.of(1L, 5, 2L, 3, 3L, 4, 4L, 2, 5L, 1, 6L, 5);

            Map<MetricType, Double> result = calculator.calculateMetricScores(answers, questions);

            assertThat(result.get(MetricType.PARTICIPATION)).isEqualTo(4.0);
            assertThat(result.get(MetricType.CHANGE_PREFERENCE)).isEqualTo(3.0);
            assertThat(result.get(MetricType.VALUE_ORIENTATION)).isEqualTo(3.0);
            System.out.println("✅ 역코딩 없는 평균 계산 테스트 통과 - 참여도:4.0, 변화선호:3.0, 가치지향:3.0");
        }

        @Test
        @DisplayName("역코딩 문항은 6-점수로 변환 후 평균을 계산한다")
        void calculate_reverseScoring_shouldApplyReverseFormula() {
            List<PoliticalTypeQuestion> questions = List.of(
                    createQuestion(1L, MetricType.PARTICIPATION, false),
                    createQuestion(2L, MetricType.PARTICIPATION, true)   // 역코딩
            );

            // Q1: 5 (그대로), Q2: 1 → 역코딩 → 6-1=5. 평균 = (5+5)/2 = 5.0
            Map<Long, Integer> answers = Map.of(1L, 5, 2L, 1);

            Map<MetricType, Double> result = calculator.calculateMetricScores(answers, questions);

            assertThat(result.get(MetricType.PARTICIPATION)).isEqualTo(5.0);
            System.out.println("✅ 역코딩 적용 테스트 통과 - Q2(1→5), 평균 5.0");
        }

        @Test
        @DisplayName("역코딩 중간값(3)은 변환해도 3이다")
        void calculate_reverseMiddleScore_shouldRemainSame() {
            List<PoliticalTypeQuestion> questions = List.of(
                    createQuestion(1L, MetricType.VALUE_ORIENTATION, true)
            );

            // 점수 3 → 역코딩 → 6-3 = 3
            Map<Long, Integer> answers = Map.of(1L, 3);

            Map<MetricType, Double> result = calculator.calculateMetricScores(answers, questions);

            assertThat(result.get(MetricType.VALUE_ORIENTATION)).isEqualTo(3.0);
            System.out.println("✅ 역코딩 중간값(3→3) 테스트 통과");
        }
    }

    @Nested
    @DisplayName("determineActionStyle 행동유형 판정 시")
    class DetermineActionStyleTest {

        @Test
        @DisplayName("3.5 이상이면 FLYING이다")
        void determine_highScore_shouldReturnFlying() {
            assertThat(calculator.determineActionStyle(4.2)).isEqualTo(ActionStyle.FLYING);
            assertThat(calculator.determineActionStyle(3.5)).isEqualTo(ActionStyle.FLYING);
            System.out.println("✅ FLYING 판정 테스트 통과 - 4.2, 3.5(경계값)");
        }

        @Test
        @DisplayName("2.5 이상 3.5 미만이면 JUMPING이다")
        void determine_midScore_shouldReturnJumping() {
            assertThat(calculator.determineActionStyle(3.0)).isEqualTo(ActionStyle.JUMPING);
            assertThat(calculator.determineActionStyle(2.5)).isEqualTo(ActionStyle.JUMPING);
            System.out.println("✅ JUMPING 판정 테스트 통과 - 3.0, 2.5(경계값)");
        }

        @Test
        @DisplayName("2.5 미만이면 LYING_DOWN이다")
        void determine_lowScore_shouldReturnLyingDown() {
            assertThat(calculator.determineActionStyle(2.4)).isEqualTo(ActionStyle.LYING_DOWN);
            assertThat(calculator.determineActionStyle(1.0)).isEqualTo(ActionStyle.LYING_DOWN);
            System.out.println("✅ LYING_DOWN 판정 테스트 통과 - 2.4, 1.0");
        }
    }

    @Nested
    @DisplayName("determineScoreLevel 레벨 판정 시")
    class DetermineScoreLevelTest {

        @Test
        @DisplayName("3.5 이상이면 HIGH이다")
        void determine_highLevel() {
            assertThat(calculator.determineScoreLevel(4.0)).isEqualTo(ScoreLevel.HIGH);
            assertThat(calculator.determineScoreLevel(3.5)).isEqualTo(ScoreLevel.HIGH);
            System.out.println("✅ HIGH 레벨 판정 테스트 통과");
        }

        @Test
        @DisplayName("2.5 이상 3.5 미만이면 MID이다")
        void determine_midLevel() {
            assertThat(calculator.determineScoreLevel(3.0)).isEqualTo(ScoreLevel.MID);
            assertThat(calculator.determineScoreLevel(2.5)).isEqualTo(ScoreLevel.MID);
            System.out.println("✅ MID 레벨 판정 테스트 통과");
        }

        @Test
        @DisplayName("2.5 미만이면 LOW이다")
        void determine_lowLevel() {
            assertThat(calculator.determineScoreLevel(2.0)).isEqualTo(ScoreLevel.LOW);
            System.out.println("✅ LOW 레벨 판정 테스트 통과");
        }
    }

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
