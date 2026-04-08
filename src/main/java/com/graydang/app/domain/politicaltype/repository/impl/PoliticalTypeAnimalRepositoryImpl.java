package com.graydang.app.domain.politicaltype.repository.impl;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeAnimal;
import com.graydang.app.domain.politicaltype.model.enums.ScoreLevel;
import com.graydang.app.domain.politicaltype.repository.PoliticalTypeAnimalRepository;
import com.graydang.app.domain.politicaltype.repository.jpa.PoliticalTypeAnimalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PoliticalTypeAnimalRepositoryImpl implements PoliticalTypeAnimalRepository {

    private final PoliticalTypeAnimalJpaRepository jpaRepository;

    @Override
    public PoliticalTypeAnimal save(PoliticalTypeAnimal animal) {
        return jpaRepository.save(animal);
    }

    @Override
    public Optional<PoliticalTypeAnimal> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<PoliticalTypeAnimal> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<PoliticalTypeAnimal> findByChangePreferenceLevelAndValueOrientationLevel(
            ScoreLevel changePreferenceLevel, ScoreLevel valueOrientationLevel) {
        return jpaRepository.findByChangePreferenceLevelAndValueOrientationLevel(
                changePreferenceLevel, valueOrientationLevel);
    }

    @Override
    public Optional<PoliticalTypeAnimal> findByCode(String code) {
        return jpaRepository.findByCode(code);
    }
}
