package com.graydang.app.domain.politicaltype.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 점수 레벨 — 변화선호/가치지향 평균 점수를 3단계로 분류.
 * Animal 매트릭스 조회 시 행(변화선호)과 열(가치지향)의 키로 사용된다.
 */
@Getter
@RequiredArgsConstructor
public enum ScoreLevel {

    HIGH("높음", 3.5),
    MID("중간", 2.5),
    LOW("낮음", 0.0);

    private final String korean;
    private final double minScore;

    /**
     * 평균 점수를 3단계 레벨로 변환한다.
     *
     * @param average 지표 평균 점수 (1.0 ~ 5.0)
     * @return 해당하는 ScoreLevel
     */
    public static ScoreLevel fromScore(double average) {
        if (average >= HIGH.minScore) {
            return HIGH;
        }
        if (average >= MID.minScore) {
            return MID;
        }
        return LOW;
    }
}
