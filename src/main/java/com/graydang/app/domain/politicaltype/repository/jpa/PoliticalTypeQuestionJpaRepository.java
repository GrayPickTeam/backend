package com.graydang.app.domain.politicaltype.repository.jpa;

import com.graydang.app.domain.politicaltype.model.PoliticalTypeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PoliticalTypeQuestionJpaRepository extends JpaRepository<PoliticalTypeQuestion, Long> {

    List<PoliticalTypeQuestion> findAllByOrderByDisplayOrderAsc();
}
