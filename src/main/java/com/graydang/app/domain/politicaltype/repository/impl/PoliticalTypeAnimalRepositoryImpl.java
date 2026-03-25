package com.graydang.app.domain.politicaltype.repository.impl;

import com.graydang.app.domain.politicaltype.repository.PoliticalTypeAnimalRepository;
import com.graydang.app.domain.politicaltype.repository.jpa.PoliticalTypeAnimalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PoliticalTypeAnimalRepositoryImpl implements PoliticalTypeAnimalRepository {

    private final PoliticalTypeAnimalJpaRepository jpaRepository;
}
