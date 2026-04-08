package com.graydang.app.domain.politicaltype.dto.response;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeQuestion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/** 사용자용 문항 응답. metricType 등 내부 정보는 제외. */
@Getter
@Builder
@Schema(description = "검사 문항 (사용자용)")
public class QuestionResponse {

    @Schema(description = "문항 ID", example = "1")
    private final Long id;

    @Schema(description = "문항 번호", example = "1")
    private final Integer questionNumber;

    @Schema(description = "문항 내용", example = "약자를 보호하는 정책이 경제 효율보다 우선되어야 한다.")
    private final String content;

    public static QuestionResponse from(PoliticalTypeQuestion question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .questionNumber(question.getDisplayOrder())
                .content(question.getContent())
                .build();
    }
}
