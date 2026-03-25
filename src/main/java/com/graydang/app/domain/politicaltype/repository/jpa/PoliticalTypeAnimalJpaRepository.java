package com.graydang.app.domain.politicaltype.repository.jpa;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeAnimal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoliticalTypeAnimalJpaRepository extends JpaRepository<PoliticalTypeAnimal, Long> {
}
