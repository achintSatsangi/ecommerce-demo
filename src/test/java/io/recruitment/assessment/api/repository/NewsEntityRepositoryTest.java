package io.recruitment.assessment.api.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
class NewsEntityRepositoryTest {

    @Autowired
    private NewsRepository repository;

    @Test
    void should_find_all() {
        assertThat(repository.findAll()).hasSize(2);
    }

}