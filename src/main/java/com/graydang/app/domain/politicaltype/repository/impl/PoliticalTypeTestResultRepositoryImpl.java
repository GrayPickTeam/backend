package com.graydang.app.domain.politicaltype.repository.impl;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeTestResult;
import com.graydang.app.domain.politicaltype.repository.PoliticalTypeTestResultRepository;
import com.graydang.app.domain.politicaltype.repository.jpa.PoliticalTypeTestResultJpaRepository;
import com.graydang.app.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PoliticalTypeTestResultRepositoryImpl implements PoliticalTypeTestResultRepository {

    private final PoliticalTypeTestResultJpaRepository jpaRepository;

    @Override
    public PoliticalTypeTestResult save(PoliticalTypeTestResult result) {
        return jpaRepository.save(result);
    }

    @Override
    public Optional<PoliticalTypeTestResult> findByUser(User user) {
        return jpaRepository.findByUser(user);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public List<Object[]> countByAnimalGroupBy() {
        return jpaRepository.countByAnimalGroupBy();
    }
}
