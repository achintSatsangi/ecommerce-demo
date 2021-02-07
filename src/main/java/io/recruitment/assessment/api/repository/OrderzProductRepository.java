package io.recruitment.assessment.api.repository;

import io.recruitment.assessment.api.domain.OrderzProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderzProductRepository extends JpaRepository<OrderzProductEntity, Integer> { }
