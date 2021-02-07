package io.recruitment.assessment.api.repository;

import io.recruitment.assessment.api.domain.Orderz;
import io.recruitment.assessment.api.domain.OrderzProduct;
import io.recruitment.assessment.api.domain.Product;
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
class OrderzRepositoryTest {

    @Autowired
    private OrderzRepository repository;

    @Autowired ProductRepository productRepository;
    @Autowired OrderzProductRepository orderzProductRepository;

    @BeforeAll
    void setup()  {
        Product product = productRepository.findById(1).get();
        Orderz orderz = Orderz.builder()
                .id(1)
                .name("test")
                .createdBy(1)
                .userId(2)
                .build();
        repository.save(orderz);
        orderzProductRepository.save(OrderzProduct.builder()
                .id(1)
                .quantity(1)
                .product(product)
                .orderz(orderz)
                .createdBy(1)
                .build());
    }

    @Test
    void should_find_all() {
        List<Orderz> orderz = repository.findAll();
        assertThat(orderz).hasSize(1);
        assertThat(orderz.get(0).getOrderzProducts()).hasSize(1);
    }

    @AfterAll
    void destroy() {
        repository.deleteAll();
    }
}