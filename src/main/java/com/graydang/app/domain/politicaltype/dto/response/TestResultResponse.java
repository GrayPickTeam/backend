package com.graydang.app.domain.politicaltype.dto.response;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeAnimal;
import com.graydang.app.domain.politicaltype.model.PoliticalTypeTestResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** 검사 결과 응답. */
@Getter
@Builder
@Schema(description = "검사 결과")
public class TestResultResponse {

    @Schema(description = "행동유형 코드 (FLYING/JUMPING/LYING_DOWN)", example = "FLYING")
    private final String actionStyleCode;

    @Schema(description = "행동유형 한글 (수식어)", example = "날아다니는")
    private final String actionStyleName;

    @Schema(description = "동물유형 정보")
    private final AnimalInfo animal;

    @Schema(description = "참여도 평균 점수", example = "4.2")
    private final Double participationScore;

    @Schema(description = "변화선호 평균 점수", example = "3.8")
    private final Double changePreferenceScore;

    @Schema(description = "가치지향 평균 점수", example = "4.0")
    private final Double valueOrientationScore;

    @Schema(description = "결과 저장 여부 (비회원이면 false)", example = "true")
    private final Boolean saved;

    @Getter
    @Builder
    @Schema(description = "동물유형 상세 정보")
    public static class AnimalInfo {

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

        @Schema(description = "키워드", example = "[\"자유\", \"급진\", \"적극참여\"]")
        private final List<String> keywords;

        @Schema(description = "대표 인물", example = "[\"000\", \"000\"]")
        private final List<String> representativeFigures;

        @Schema(description = "이미지 URL")
        private final String imageUrl;

        @Schema(description = "잘 맞는 유형")
        private final CompatibleAnimalInfo compatibleAnimal;

        @Schema(description = "안 맞는 유형")
        private final CompatibleAnimalInfo incompatibleAnimal;

        public static AnimalInfo from(PoliticalTypeAnimal animal) {
            return AnimalInfo.builder()
                    .code(animal.getCode())
                    .name(animal.getName())
                    .subtitle(animal.getSubtitle())
                    .oneLiner(animal.getOneLiner())
                    .description(animal.getDescription())
                    .keywords(animal.getKeywords())
                    .representativeFigures(animal.getRepresentativeFigures())
                    .imageUrl(animal.getImageUrl())
                    .compatibleAnimal(toCompatibleInfo(animal.getCompatibleAnimal()))
                    .incompatibleAnimal(toCompatibleInfo(animal.getIncompatibleAnimal()))
                    .build();
        }

        private static CompatibleAnimalInfo toCompatibleInfo(PoliticalTypeAnimal animal) {
            if (animal == null) return null;
            return CompatibleAnimalInfo.builder()
                    .code(animal.getCode())
                    .name(animal.getName())
                    .imageUrl(animal.getImageUrl())
                    .build();
        }
    }

    @Getter
    @Builder
    @Schema(description = "상성 동물 간략 정보")
    public static class CompatibleAnimalInfo {

        @Schema(description = "동물 코드", example = "FLYING_SQUIRREL")
        private final String code;

        @Schema(description = "동물 이름", example = "날다람쥐")
        private final String name;

        @Schema(description = "이미지 URL")
        private final String imageUrl;
    }

    /** Entity → DTO 변환. */
    public static TestResultResponse from(PoliticalTypeTestResult result) {
        return TestResultResponse.builder()
                .actionStyleCode(result.getActionStyle().name())
                .actionStyleName(result.getActionStyle().getKorean())
                .animal(AnimalInfo.from(result.getAnimal()))
                .participationScore(result.getParticipationScore())
                .changePreferenceScore(result.getChangePreferenceScore())
                .valueOrientationScore(result.getValueOrientationScore())
                .saved(true)
                .build();
    }
}
