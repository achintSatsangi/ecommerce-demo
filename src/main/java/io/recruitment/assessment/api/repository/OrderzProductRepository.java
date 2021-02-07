package io.recruitment.assessment.api.repository;

import io.recruitment.assessment.api.domain.OrderzProductEntity;
import io.recruitment.assessment.api.domain.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderzProductRepository extends JpaRepository<OrderzProductEntity, Integer> {

    @Query("select c from OrderzProductEntity c "
            + "where c.orderzEntity.id = :orderId")
    List<OrderzProductEntity> findByOrderId(@Param("orderId") Integer orderId);
}
