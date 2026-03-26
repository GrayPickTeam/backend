package com.graydang.app.domain.politicaltype.model;

import com.graydang.app.domain.politicaltype.model.enums.ScoreLevel;
import com.graydang.app.global.common.converter.StringListConverter;
import com.graydang.app.global.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * 동물 유형 콘텐츠 (9종).
 * 변화선호 × 가치지향 매트릭스로 결정되며, 관리자가 콘텐츠를 수정할 수 있도록 DB로 관리한다.
 */
@Entity
@Table(name = "political_type_animal",
        uniqueConstraints = @UniqueConstraint(columnNames = {"change_preference_level", "value_orientation_level"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PoliticalTypeAnimal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    @Comment("동물 유형 코드 (예: DOLPHIN, OWL)")
    private String code;

    @Column(nullable = false, length = 20)
    @Comment("동물 이름 (예: 돌고래)")
    private String name;


    @Column(length = 100)
    @Comment("부제 (예: 정의로운 혁명가)")
    private String subtitle;

    @Column(nullable = false, length = 100)
    @Comment("한줄 요약 (예: 바꿔야 한다면, 지금이야!)")
    private String oneLiner;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Comment("상세 설명")
    private String description;

    @Convert(converter = StringListConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    @Comment("키워드 (JSON 배열)")
    @Builder.Default
    private List<String> keywords = new ArrayList<>();

    @Convert(converter = StringListConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    @Comment("대표 인물 (JSON 배열)")
    @Builder.Default
    private List<String> representativeFigures = new ArrayList<>();

    @Column(length = 500)
    @Comment("동물 캐릭터 이미지 URL")
    private String imageUrl;

    @Column(length = 255)
    @Comment("원본 이미지 파일명")
    private String originalImageName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Comment("변화선호 레벨 (HIGH/MID/LOW)")
    private ScoreLevel changePreferenceLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Comment("가치지향 레벨 (HIGH/MID/LOW)")
    private ScoreLevel valueOrientationLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compatible_animal_id")
    @Comment("잘 맞는 유형")
    private PoliticalTypeAnimal compatibleAnimal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incompatible_animal_id")
    @Comment("안 맞는 유형")
    private PoliticalTypeAnimal incompatibleAnimal;

    public void update(String code, String name, String subtitle, String oneLiner,
                       String description, List<String> keywords, List<String> representativeFigures,
                       ScoreLevel changePreferenceLevel, ScoreLevel valueOrientationLevel) {
        this.code = code;
        this.name = name;
        this.subtitle = subtitle;
        this.oneLiner = oneLiner;
        this.description = description;
        this.keywords = keywords;
        this.representativeFigures = representativeFigures;
        this.changePreferenceLevel = changePreferenceLevel;
        this.valueOrientationLevel = valueOrientationLevel;
    }

    /** 이미지 교체. 기존 S3 삭제는 Service에서 처리. */
    public void updateImage(String imageUrl, String originalImageName) {
        this.imageUrl = imageUrl;
        this.originalImageName = originalImageName;
    }

    public void updateCompatibility(PoliticalTypeAnimal compatible, PoliticalTypeAnimal incompatible) {
        this.compatibleAnimal = compatible;
        this.incompatibleAnimal = incompatible;
    }
}
