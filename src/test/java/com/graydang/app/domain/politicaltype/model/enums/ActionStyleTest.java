package com.graydang.app.domain.politicaltype.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActionStyleTest {

    @Nested
    @DisplayName("fromScore로 참여도 평균 점수를 행동유형으로 변환한다")
    class FromScoreTest {

        @Test
        @DisplayName("3.5 이상이면 FLYING을 반환한다")
        void fromScore_shouldReturnFlying() {
            assertThat(ActionStyle.fromScore(3.5)).isEqualTo(ActionStyle.FLYING);
            assertThat(ActionStyle.fromScore(5.0)).isEqualTo(ActionStyle.FLYING);

            System.out.println("✅ fromScore_shouldReturnFlying 테스트 통과 - 3.5, 5.0 → FLYING");
        }

        @Test
        @DisplayName("2.5 이상 3.5 미만이면 JUMPING을 반환한다")
        void fromScore_shouldReturnJumping() {
            assertThat(ActionStyle.fromScore(2.5)).isEqualTo(ActionStyle.JUMPING);
            assertThat(ActionStyle.fromScore(3.4)).isEqualTo(ActionStyle.JUMPING);

            System.out.println("✅ fromScore_shouldReturnJumping 테스트 통과 - 2.5, 3.4 → JUMPING");
        }

        @Test
        @DisplayName("2.5 미만이면 LYING_DOWN을 반환한다")
        void fromScore_shouldReturnLyingDown() {
            assertThat(ActionStyle.fromScore(2.4)).isEqualTo(ActionStyle.LYING_DOWN);
            assertThat(ActionStyle.fromScore(1.0)).isEqualTo(ActionStyle.LYING_DOWN);

            System.out.println("✅ fromScore_shouldReturnLyingDown 테스트 통과 - 2.4, 1.0 → LYING_DOWN");
        }
    }
}
