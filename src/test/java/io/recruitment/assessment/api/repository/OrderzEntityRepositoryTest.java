package io.recruitment.assessment.api.repository;

import io.recruitment.assessment.api.domain.OrderzEntity;
import io.recruitment.assessment.api.domain.OrderzProductEntity;
import io.recruitment.assessment.api.domain.ProductEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
class OrderzEntityRepositoryTest {

    @Autowired
    private OrderzRepository repository;

    @Autowired ProductRepository productRepository;
    @Autowired OrderzProductRepository orderzProductRepository;

    @BeforeAll
    void setup()  {
        ProductEntity productEntity = productRepository.findById(1).get();
        OrderzEntity orderzEntity = OrderzEntity.builder()
                .id(1)
                .name("test")
                .createdBy(1)
                .userId(2)
                .build();
        repository.save(orderzEntity);
        orderzProductRepository.save(OrderzProductEntity.builder()
                .id(1)
                .quantity(1)
                .productEntity(productEntity)
                .orderzEntity(orderzEntity)
                .createdBy(1)
                .build());
    }

    @Test
    void should_find_all() {
        List<OrderzEntity> orderzEntity = repository.findAll();
        assertThat(orderzEntity).hasSize(1);
        assertThat(orderzEntity.get(0).getOrderzProductEntities()).hasSize(1);
    }

    @AfterAll
    void destroy() {
        repository.deleteAll();
    }
}