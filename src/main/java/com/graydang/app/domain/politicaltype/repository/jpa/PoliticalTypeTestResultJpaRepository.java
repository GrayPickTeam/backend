package com.graydang.app.domain.politicaltype.repository.jpa;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeTestResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoliticalTypeTestResultJpaRepository extends JpaRepository<PoliticalTypeTestResult, Long> {
}
