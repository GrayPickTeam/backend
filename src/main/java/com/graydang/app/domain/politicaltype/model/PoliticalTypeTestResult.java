package com.graydang.app.domain.politicaltype.model;

import com.graydang.app.domain.politicaltype.model.enums.ActionStyle;
import com.graydang.app.domain.user.model.User;
import com.graydang.app.global.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

/**
 * 유저 정치 유형 검사 결과.
 * 유저당 최신 1건만 유지하며, 재검사 시 기존 결과를 덮어쓴다.
 */
@Entity
@Table(name = "political_type_test_result")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PoliticalTypeTestResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @Comment("검사를 수행한 유저")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Comment("행동유형 (FLYING/JUMPING/LYING_DOWN)")
    private ActionStyle actionStyle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    @Comment("동물유형 (FK)")
    private PoliticalTypeAnimal animal;

    @Column(nullable = false)
    @Comment("참여도 평균 점수")
    private Double participationScore;

    @Column(nullable = false)
    @Comment("변화선호 평균 점수")
    private Double changePreferenceScore;

    @Column(nullable = false)
    @Comment("가치지향 평균 점수")
    private Double valueOrientationScore;

    /**
     * 재검사 시 결과를 갱신한다.
     */
    public void updateResult(ActionStyle actionStyle, PoliticalTypeAnimal animal,
                             double participationScore, double changePreferenceScore,
                             double valueOrientationScore) {
        this.actionStyle = actionStyle;
        this.animal = animal;
        this.participationScore = participationScore;
        this.changePreferenceScore = changePreferenceScore;
        this.valueOrientationScore = valueOrientationScore;
    }
}
