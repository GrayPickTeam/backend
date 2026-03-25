package com.graydang.app.domain.politicaltype.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScoreLevelTest {

    @Nested
    @DisplayName("fromScore로 평균 점수를 레벨로 변환한다")
    class FromScoreTest {

        @Test
        @DisplayName("3.5 이상이면 HIGH를 반환한다")
        void fromScore_shouldReturnHigh() {
            assertThat(ScoreLevel.fromScore(3.5)).isEqualTo(ScoreLevel.HIGH);
            assertThat(ScoreLevel.fromScore(5.0)).isEqualTo(ScoreLevel.HIGH);

            System.out.println("✅ fromScore_shouldReturnHigh 테스트 통과 - 3.5, 5.0 → HIGH");
        }

        @Test
        @DisplayName("2.5 이상 3.5 미만이면 MID를 반환한다")
        void fromScore_shouldReturnMid() {
            assertThat(ScoreLevel.fromScore(2.5)).isEqualTo(ScoreLevel.MID);
            assertThat(ScoreLevel.fromScore(3.4)).isEqualTo(ScoreLevel.MID);

            System.out.println("✅ fromScore_shouldReturnMid 테스트 통과 - 2.5, 3.4 → MID");
        }

        @Test
        @DisplayName("2.5 미만이면 LOW를 반환한다")
        void fromScore_shouldReturnLow() {
            assertThat(ScoreLevel.fromScore(2.4)).isEqualTo(ScoreLevel.LOW);
            assertThat(ScoreLevel.fromScore(1.0)).isEqualTo(ScoreLevel.LOW);

            System.out.println("✅ fromScore_shouldReturnLow 테스트 통과 - 2.4, 1.0 → LOW");
        }
    }
}
