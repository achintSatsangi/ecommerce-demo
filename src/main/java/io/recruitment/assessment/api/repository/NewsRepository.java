package io.recruitment.assessment.api.repository;

import io.recruitment.assessment.api.domain.News;
import io.recruitment.assessment.api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Integer> { }
