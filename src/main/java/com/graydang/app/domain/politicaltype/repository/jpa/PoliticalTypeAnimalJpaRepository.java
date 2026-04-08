package com.graydang.app.domain.politicaltype.repository.jpa;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeAnimal;
import com.graydang.app.domain.politicaltype.model.enums.ScoreLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PoliticalTypeAnimalJpaRepository extends JpaRepository<PoliticalTypeAnimal, Long> {

    Optional<PoliticalTypeAnimal> findByChangePreferenceLevelAndValueOrientationLevel(
            ScoreLevel changePreferenceLevel, ScoreLevel valueOrientationLevel);

    Optional<PoliticalTypeAnimal> findByCode(String code);
}
