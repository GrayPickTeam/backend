package com.graydang.app.domain.politicaltype.dto.response;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeAnimal;
import com.graydang.app.domain.politicaltype.model.enums.ScoreLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "동물유형 관리자 응답")
public class AnimalAdminResponse {

    @Schema(description = "동물유형 ID", example = "1")
    private Long id;

    @Schema(description = "동물 유형 코드", example = "DOLPHIN")
    private String code;

    @Schema(description = "동물 이름", example = "돌고래")
    private String name;


    @Schema(description = "부제", example = "정의로운 혁명가")
    private String subtitle;

    @Schema(description = "한줄 요약", example = "바꿔야 한다면, 지금이야!")
    private String oneLiner;

    @Schema(description = "상세 설명")
    private String description;

    @Schema(description = "키워드 목록")
    private List<String> keywords;

    @Schema(description = "대표 인물 목록")
    private List<String> representativeFigures;

    @Schema(description = "이미지 URL")
    private String imageUrl;

    @Schema(description = "원본 이미지 파일명")
    private String originalImageName;

    @Schema(description = "변화선호 레벨", example = "HIGH")
    private ScoreLevel changePreferenceLevel;

    @Schema(description = "가치지향 레벨", example = "HIGH")
    private ScoreLevel valueOrientationLevel;

    @Schema(description = "잘 맞는 유형 ID")
    private Long compatibleAnimalId;

    @Schema(description = "잘 맞는 유형 이름")
    private String compatibleAnimalName;

    @Schema(description = "안 맞는 유형 ID")
    private Long incompatibleAnimalId;

    @Schema(description = "안 맞는 유형 이름")
    private String incompatibleAnimalName;

    public static AnimalAdminResponse from(PoliticalTypeAnimal animal) {
        return AnimalAdminResponse.builder()
                .id(animal.getId())
                .code(animal.getCode())
                .name(animal.getName())
                .subtitle(animal.getSubtitle())
                .oneLiner(animal.getOneLiner())
                .description(animal.getDescription())
                .keywords(animal.getKeywords())
                .representativeFigures(animal.getRepresentativeFigures())
                .imageUrl(animal.getImageUrl())
                .originalImageName(animal.getOriginalImageName())
                .changePreferenceLevel(animal.getChangePreferenceLevel())
                .valueOrientationLevel(animal.getValueOrientationLevel())
                .compatibleAnimalId(animal.getCompatibleAnimal() != null ? animal.getCompatibleAnimal().getId() : null)
                .compatibleAnimalName(animal.getCompatibleAnimal() != null ? animal.getCompatibleAnimal().getName() : null)
                .incompatibleAnimalId(animal.getIncompatibleAnimal() != null ? animal.getIncompatibleAnimal().getId() : null)
                .incompatibleAnimalName(animal.getIncompatibleAnimal() != null ? animal.getIncompatibleAnimal().getName() : null)
                .build();
    }
}
