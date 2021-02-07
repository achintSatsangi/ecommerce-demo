package io.recruitment.assessment.api.repository;

import io.recruitment.assessment.api.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    void should_find_all() {
        assertThat(repository.findAll()).hasSize(2);
    }

    @Test
    void should_find_by_api_key() {
        User user = repository.findById(1).get();
        assertThat(repository.findByApiKey(user.getApiKey())).isPresent().get().isEqualTo(user);
    }

}