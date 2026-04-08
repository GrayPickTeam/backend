package com.graydang.app.domain.politicaltype.service;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeQuestion;
import com.graydang.app.domain.politicaltype.model.enums.ActionStyle;
import com.graydang.app.domain.politicaltype.model.enums.MetricType;
import com.graydang.app.domain.politicaltype.model.enums.ScoreLevel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 정치 유형 검사 점수 계산기. */
@Component
public class PoliticalTypeCalculator {

    private static final int REVERSE_SCORE_BASE = 6;

    /**
     * 지표별 평균 점수를 계산한다. 역코딩 적용 후 MetricType별 그룹핑.
     *
     * @param answerMap  문항ID → 응답 점수
     * @param questions  전체 문항 목록
     * @return 지표별 평균 점수
     */
    public Map<MetricType, Double> calculateMetricScores(Map<Long, Integer> answerMap,
                                                         List<PoliticalTypeQuestion> questions) {
        return questions.stream()
                .collect(Collectors.groupingBy(
                        PoliticalTypeQuestion::getMetricType,
                        Collectors.averagingDouble(q -> applyReverseScoring(q, answerMap.get(q.getId())))
                ));
    }

    /** 참여도 평균 점수로 행동유형을 판정한다. */
    public ActionStyle determineActionStyle(double participationAverage) {
        return ActionStyle.fromScore(participationAverage);
    }

    /** 지표 평균 점수를 3단계 레벨(HIGH/MID/LOW)로 변환한다. */
    public ScoreLevel determineScoreLevel(double average) {
        return ScoreLevel.fromScore(average);
    }

    /** 역코딩 적용. reverseScored 문항은 REVERSE_SCORE_BASE - rawScore. */
    private int applyReverseScoring(PoliticalTypeQuestion question, int rawScore) {
        if (Boolean.TRUE.equals(question.getReverseScored())) {
            return REVERSE_SCORE_BASE - rawScore;
        }
        return rawScore;
    }
}
