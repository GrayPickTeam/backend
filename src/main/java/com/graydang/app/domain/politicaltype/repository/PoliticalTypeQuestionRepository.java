package com.graydang.app.domain.politicaltype.repository;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeQuestion;

import java.util.List;
import java.util.Optional;

/**
 * 정치 유형 검사 문항 Repository 인터페이스.
 */
public interface PoliticalTypeQuestionRepository {

    PoliticalTypeQuestion save(PoliticalTypeQuestion question);

    Optional<PoliticalTypeQuestion> findById(Long id);

    List<PoliticalTypeQuestion> findAllByOrderByDisplayOrderAsc();

    List<PoliticalTypeQuestion> findAll();

    void deleteById(Long id);

    void deleteAllById(List<Long> ids);
}
