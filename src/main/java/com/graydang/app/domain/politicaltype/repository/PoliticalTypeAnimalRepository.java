package com.graydang.app.domain.politicaltype.repository;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeAnimal;
import com.graydang.app.domain.politicaltype.model.enums.ScoreLevel;

import java.util.List;
import java.util.Optional;

/**
 * 동물 유형 콘텐츠 Repository 인터페이스.
 */
public interface PoliticalTypeAnimalRepository {

    PoliticalTypeAnimal save(PoliticalTypeAnimal animal);

    Optional<PoliticalTypeAnimal> findById(Long id);

    List<PoliticalTypeAnimal> findAll();

    void deleteById(Long id);

    /** 변화선호 × 가치지향 매트릭스 조합으로 동물유형 조회. */
    Optional<PoliticalTypeAnimal> findByChangePreferenceLevelAndValueOrientationLevel(
            ScoreLevel changePreferenceLevel, ScoreLevel valueOrientationLevel);

    /** 동물 코드(DOLPHIN 등)로 조회. */
    Optional<PoliticalTypeAnimal> findByCode(String code);
}
