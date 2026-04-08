package com.graydang.app.domain.politicaltype.repository.jpa;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeTestResult;
import com.graydang.app.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PoliticalTypeTestResultJpaRepository extends JpaRepository<PoliticalTypeTestResult, Long> {

    Optional<PoliticalTypeTestResult> findByUser(User user);

    /**
     * 동물유형별 검사 결과 집계 (랭킹용).
     * animal_id와 count를 함께 반환한다.
     */
    @Query("SELECT r.animal.id, COUNT(r) FROM PoliticalTypeTestResult r GROUP BY r.animal.id ORDER BY COUNT(r) DESC")
    List<Object[]> countByAnimalGroupBy();
}
