package com.graydang.app.domain.politicaltype.repository.impl;

import com.graydang.app.domain.politicaltype.repository.PoliticalTypeQuestionRepository;
import com.graydang.app.domain.politicaltype.repository.jpa.PoliticalTypeQuestionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PoliticalTypeQuestionRepositoryImpl implements PoliticalTypeQuestionRepository {

    private final PoliticalTypeQuestionJpaRepository jpaRepository;
}
