package io.recruitment.assessment.api.repository;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
class ProductEntityRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @Test
    void should_find_all() {
        assertThat(repository.findAll()).hasSize(3);
    }

    @Test
    void should_do_like_search_on_name() {
        assertThat(repository.findBySearchText("pan")).hasSize(1)
        .extracting("name", "description")
        .contains(Tuple.tuple("Spanner", "Good quality spanner"));
    }

}