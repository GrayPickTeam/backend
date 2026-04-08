package com.graydang.app.domain.politicaltype.dto.response;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeQuestion;
import com.graydang.app.domain.politicaltype.model.enums.MetricType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "검사 문항 관리자 응답")
public class QuestionAdminResponse {

    @Schema(description = "문항 ID", example = "1")
    private Long id;

    @Schema(description = "문항 텍스트", example = "나는 정치 뉴스를 자주 챙겨 본다")
    private String content;

    @Schema(description = "소속 측정 지표", example = "PARTICIPATION")
    private MetricType metricType;

    @Schema(description = "역코딩 여부", example = "false")
    private Boolean reverseScored;

    @Schema(description = "표시 순서 (= 문항 번호)", example = "1")
    private Integer displayOrder;

    public static QuestionAdminResponse from(PoliticalTypeQuestion question) {
        return QuestionAdminResponse.builder()
                .id(question.getId())
                .content(question.getContent())
                .metricType(question.getMetricType())
                .reverseScored(question.getReverseScored())
                .displayOrder(question.getDisplayOrder())
                .build();
    }
}
