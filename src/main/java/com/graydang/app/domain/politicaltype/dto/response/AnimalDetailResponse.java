package com.graydang.app.domain.politicaltype.dto.response;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeAnimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** 동물유형 상세 조회 응답. */
@Getter
@Builder
@Schema(description = "동물유형 상세 정보")
public class AnimalDetailResponse {

    @Schema(description = "동물 코드", example = "DOLPHIN")
    private final String code;

    @Schema(description = "동물 이름", example = "돌고래")
    private final String name;

    @Schema(description = "부제", example = "열정가득 이상과 변화를 쫓는 행동가형")
    private final String subtitle;

    @Schema(description = "한줄 소개", example = "바꿔야 한다면, 지금이야!")
    private final String oneLiner;

    @Schema(description = "상세 설명")
    private final String description;

    @Schema(description = "키워드")
    private final List<String> keywords;

    @Schema(description = "대표 인물")
    private final List<String> representativeFigures;

    @Schema(description = "이미지 URL")
    private final String imageUrl;

    @Schema(description = "잘 맞는 유형")
    private final SimpleAnimalInfo compatibleAnimal;

    @Schema(description = "안 맞는 유형")
    private final SimpleAnimalInfo incompatibleAnimal;

    @Getter
    @Builder
    @Schema(description = "상성 동물 간략 정보")
    public static class SimpleAnimalInfo {
        @Schema(description = "동물 코드", example = "FLYING_SQUIRREL")
        private final String code;

        @Schema(description = "동물 이름", example = "날다람쥐")
        private final String name;

        @Schema(description = "이미지 URL")
        private final String imageUrl;
    }

    public static AnimalDetailResponse from(PoliticalTypeAnimal animal) {
        return AnimalDetailResponse.builder()
                .code(animal.getCode())
                .name(animal.getName())
                .subtitle(animal.getSubtitle())
                .oneLiner(animal.getOneLiner())
                .description(animal.getDescription())
                .keywords(animal.getKeywords())
                .representativeFigures(animal.getRepresentativeFigures())
                .imageUrl(animal.getImageUrl())
                .compatibleAnimal(toSimpleInfo(animal.getCompatibleAnimal()))
                .incompatibleAnimal(toSimpleInfo(animal.getIncompatibleAnimal()))
                .build();
    }

    private static SimpleAnimalInfo toSimpleInfo(PoliticalTypeAnimal animal) {
        if (animal == null) return null;
        return SimpleAnimalInfo.builder()
                .code(animal.getCode())
                .name(animal.getName())
                .imageUrl(animal.getImageUrl())
                .build();
    }
}
