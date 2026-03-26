package com.graydang.app.domain.politicaltype.dto.request;

import com.graydang.app.domain.politicaltype.model.enums.ScoreLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "동물유형 수정 요청")
public class AnimalUpdateRequest {

    @NotBlank(message = "동물 코드는 필수입니다")
    @Size(max = 30, message = "동물 코드는 30자 이하여야 합니다")
    @Schema(description = "동물 유형 코드", example = "DOLPHIN")
    private String code;

    @NotBlank(message = "동물 이름은 필수입니다")
    @Size(max = 20, message = "동물 이름은 20자 이하여야 합니다")
    @Schema(description = "동물 이름", example = "돌고래")
    private String name;


    @Schema(description = "부제", example = "정의로운 혁명가")
    private String subtitle;

    @NotBlank(message = "한줄 요약은 필수입니다")
    @Size(max = 100, message = "한줄 요약은 100자 이하여야 합니다")
    @Schema(description = "한줄 요약", example = "바꿔야 한다면, 지금이야!")
    private String oneLiner;

    @NotBlank(message = "상세 설명은 필수입니다")
    @Schema(description = "상세 설명")
    private String description;

    @Schema(description = "키워드 목록", example = "[\"자유\", \"급진\", \"적극참여\"]")
    private List<String> keywords;

    @Schema(description = "대표 인물 목록", example = "[\"인물A\", \"인물B\"]")
    private List<String> representativeFigures;


    @NotNull(message = "변화선호 레벨은 필수입니다")
    @Schema(description = "변화선호 레벨 (매트릭스 행)", example = "HIGH")
    private ScoreLevel changePreferenceLevel;

    @NotNull(message = "가치지향 레벨은 필수입니다")
    @Schema(description = "가치지향 레벨 (매트릭스 열)", example = "HIGH")
    private ScoreLevel valueOrientationLevel;

    @Schema(description = "잘 맞는 유형 ID (null이면 해제)", example = "2")
    private Long compatibleAnimalId;

    @Schema(description = "안 맞는 유형 ID (null이면 해제)", example = "3")
    private Long incompatibleAnimalId;
}
