package com.graydang.app.domain.politicaltype.model;

import com.graydang.app.domain.politicaltype.model.enums.MetricType;
import com.graydang.app.global.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

/**
 * 정치 유형 검사 문항.
 * 각 문항은 하나의 측정 지표(MetricType)에 소속되며, 역코딩 여부를 가진다.
 * displayOrder가 화면 표시 순서이자 문항 번호 역할을 겸한다.
 */
@Entity
@Table(name = "political_type_question")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PoliticalTypeQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    @Comment("문항 텍스트")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Comment("소속 측정 지표")
    private MetricType metricType;

    @Column(nullable = false)
    @Comment("역코딩 여부 (true면 6-점수로 변환)")
    @Builder.Default
    private Boolean reverseScored = false;

    @Column(nullable = false)
    @Comment("화면 표시 순서 (1부터 시작, 문항 번호 겸용)")
    private Integer displayOrder;

    /** 문항 내용 수정. */
    public void update(String content, MetricType metricType, Boolean reverseScored) {
        this.content = content;
        this.metricType = metricType;
        this.reverseScored = reverseScored;
    }

    /** displayOrder 변경. */
    public void updateDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
