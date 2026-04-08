package com.graydang.app.domain.politicaltype.dto.request;

import com.graydang.app.domain.politicaltype.model.enums.MetricType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "검사 문항 수정 요청")
public class QuestionUpdateRequest {

    @NotBlank(message = "문항 내용은 필수입니다")
    @Schema(description = "문항 텍스트", example = "나는 정치 뉴스를 자주 챙겨 본다")
    private String content;

    @NotNull(message = "측정 지표는 필수입니다")
    @Schema(description = "소속 측정 지표", example = "PARTICIPATION")
    private MetricType metricType;

    @Schema(description = "역코딩 여부", example = "false")
    private Boolean reverseScored = false;
}
