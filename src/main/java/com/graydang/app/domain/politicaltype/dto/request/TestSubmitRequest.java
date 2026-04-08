package com.graydang.app.domain.politicaltype.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/** 검사 답변 제출 요청. */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "검사 답변 제출 요청")
public class TestSubmitRequest {

    @Valid
    @NotEmpty(message = "답변 목록은 비어 있을 수 없습니다.")
    @Schema(description = "문항별 응답 리스트")
    private List<AnswerItem> answers;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Schema(description = "개별 문항 응답")
    public static class AnswerItem {

        @NotNull(message = "문항 ID는 필수입니다.")
        @Schema(description = "문항 ID", example = "1")
        private Long questionId;

        @NotNull(message = "응답 점수는 필수입니다.")
        @Min(value = 1, message = "응답 점수는 1 이상이어야 합니다.")
        @Max(value = 5, message = "응답 점수는 5 이하여야 합니다.")
        @Schema(description = "응답 점수 (1~5 리커트 척도)", example = "4")
        private Integer score;
    }
}
