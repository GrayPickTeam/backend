package com.graydang.app.domain.politicaltype.repository.impl;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeQuestion;
import com.graydang.app.domain.politicaltype.repository.PoliticalTypeQuestionRepository;
import com.graydang.app.domain.politicaltype.repository.jpa.PoliticalTypeQuestionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PoliticalTypeQuestionRepositoryImpl implements PoliticalTypeQuestionRepository {

    private final PoliticalTypeQuestionJpaRepository jpaRepository;

    @Override
    public PoliticalTypeQuestion save(PoliticalTypeQuestion question) {
        return jpaRepository.save(question);
    }

    @Override
    public Optional<PoliticalTypeQuestion> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<PoliticalTypeQuestion> findAllByOrderByDisplayOrderAsc() {
        return jpaRepository.findAllByOrderByDisplayOrderAsc();
    }

    @Override
    public List<PoliticalTypeQuestion> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        jpaRepository.deleteAllById(ids);
    }
}
