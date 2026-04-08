package com.graydang.app.domain.politicaltype.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** 유형별 랭킹 응답. */
@Getter
@Builder
@Schema(description = "유형별 랭킹")
public class RankingResponse {

    @Schema(description = "전체 참여자 수", example = "20000")
    private final Long totalParticipants;

    @Schema(description = "유형별 순위 목록 (내림차순)")
    private final List<RankingItem> rankings;

    @Getter
    @Builder
    @Schema(description = "랭킹 항목")
    public static class RankingItem {

        @Schema(description = "순위", example = "1")
        private final Integer rank;

        @Schema(description = "동물 코드", example = "DOLPHIN")
        private final String animalCode;

        @Schema(description = "동물 이름", example = "돌고래")
        private final String animalName;

        @Schema(description = "이미지 URL")
        private final String imageUrl;

        @Schema(description = "해당 유형 인원수", example = "5000")
        private final Long count;

        @Schema(description = "비율 (%)", example = "25.0")
        private final Double percentage;
    }
}
