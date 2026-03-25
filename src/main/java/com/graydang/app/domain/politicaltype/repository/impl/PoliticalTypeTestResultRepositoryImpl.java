package com.graydang.app.domain.politicaltype.repository.impl;

import com.graydang.app.domain.politicaltype.repository.PoliticalTypeTestResultRepository;
import com.graydang.app.domain.politicaltype.repository.jpa.PoliticalTypeTestResultJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PoliticalTypeTestResultRepositoryImpl implements PoliticalTypeTestResultRepository {

    private final PoliticalTypeTestResultJpaRepository jpaRepository;
}
