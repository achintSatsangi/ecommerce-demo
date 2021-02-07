package io.recruitment.assessment.api.repository;

import io.recruitment.assessment.api.domain.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

    @Query("select c from ProductEntity c "
            + "where c.name like %:query%")
    List<ProductEntity> findBySearchText(@Param("query") String query);
}
