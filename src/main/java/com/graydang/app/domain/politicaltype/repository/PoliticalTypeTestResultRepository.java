package com.graydang.app.domain.politicaltype.repository;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeTestResult;
import com.graydang.app.domain.user.model.User;

import java.util.List;
import java.util.Optional;

/**
 * 유저 검사 결과 Repository 인터페이스.
 */
public interface PoliticalTypeTestResultRepository {

    PoliticalTypeTestResult save(PoliticalTypeTestResult result);

    /** 유저의 검사 결과 조회. 유저당 최대 1건. */
    Optional<PoliticalTypeTestResult> findByUser(User user);

    /** 전체 검사 참여자 수 (인트로 화면 표시용). */
    long count();

    /** 동물유형별 참여자 집계 (랭킹용). Object[0]=animalId(Long), Object[1]=count(Long). */
    List<Object[]> countByAnimalGroupBy();
}
