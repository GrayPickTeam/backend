package com.graydang.app.domain.politicaltype.dto.request;

import com.graydang.app.domain.politicaltype.model.enums.MetricType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "문항 벌크 동기화 요청")
public class QuestionSyncRequest {

    @NotNull(message = "문항 목록은 필수입니다")
    @Valid
    @Schema(description = "전체 문항 리스트 (배열 순서 = displayOrder)")
    private List<QuestionSyncItem> questions;

    @Getter
    @NoArgsConstructor
    @Schema(description = "개별 문항 항목")
    public static class QuestionSyncItem {

        @Schema(description = "문항 ID (null이면 신규 생성)", example = "1")
        private Long id;

        @NotBlank(message = "문항 내용은 필수입니다")
        @Schema(description = "문항 텍스트", example = "나는 정치 뉴스를 자주 챙겨 본다")
        private String content;

        @NotNull(message = "측정 지표는 필수입니다")
        @Schema(description = "소속 측정 지표", example = "PARTICIPATION")
        private MetricType metricType;

        @Schema(description = "역코딩 여부", example = "false")
        private Boolean reverseScored = false;
    }
}
