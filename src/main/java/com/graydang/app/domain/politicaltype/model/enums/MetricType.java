package com.graydang.app.domain.politicaltype.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 정치 유형 검사 측정 지표.
 * 15문항을 3개 그룹(각 5문항)으로 분류하며, 점수 계산 시 그룹핑 기준으로 사용된다.
 */
@Getter
@RequiredArgsConstructor
public enum MetricType {

    PARTICIPATION("참여도", "정치에 대한 관심과 실제 행동 수준"),
    CHANGE_PREFERENCE("변화선호", "사회 변화와 개혁을 얼마나 선호하는지"),
    VALUE_ORIENTATION("가치지향", "진보–보수 중 어떤 가치에 가까운지");

    private final String korean;
    private final String description;
}
