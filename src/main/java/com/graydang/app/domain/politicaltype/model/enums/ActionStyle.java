package com.graydang.app.domain.politicaltype.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 행동유형 — 참여도(PARTICIPATION) 평균 점수 기준으로 판정.
 * 최종 결과에서 동물유형 앞에 수식어로 붙는다. (예: "날아다니는 돌고래")
 */
@Getter
@RequiredArgsConstructor
public enum ActionStyle {

    FLYING("날아다니는", "정치 참여도가 높고 적극적으로 행동하는 유형", 3.5),
    JUMPING("뛰어다니는", "정치에 적당한 관심을 가지고 가끔 행동하는 유형", 2.5),
    LYING_DOWN("누워있는", "정치에 관심이 적고 소극적인 유형", 0.0);

    private final String korean;
    private final String description;
    private final double minScore;

    /**
     * 참여도 평균 점수로 행동유형을 판정한다.
     *
     * @param participationAverage 참여도 지표의 평균 점수 (1.0 ~ 5.0)
     * @return 해당하는 ActionStyle
     */
    public static ActionStyle fromScore(double participationAverage) {
        if (participationAverage >= FLYING.minScore) {
            return FLYING;
        }
        if (participationAverage >= JUMPING.minScore) {
            return JUMPING;
        }
        return LYING_DOWN;
    }
}
