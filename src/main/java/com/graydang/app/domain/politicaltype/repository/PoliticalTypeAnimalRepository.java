package com.graydang.app.domain.politicaltype.repository;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeAnimal;

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
}
