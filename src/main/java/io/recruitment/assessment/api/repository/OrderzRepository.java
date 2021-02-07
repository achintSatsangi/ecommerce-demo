package io.recruitment.assessment.api.repository;

import io.recruitment.assessment.api.domain.Orderz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderzRepository extends JpaRepository<Orderz, Integer> { }
