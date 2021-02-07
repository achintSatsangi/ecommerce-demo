package io.recruitment.assessment.api;

import io.recruitment.assessment.api.domain.RoleEntity;
import io.recruitment.assessment.api.domain.User;
import io.recruitment.assessment.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserValidatorTest {

    private UserValidator userValidator;
    private UserRepository repository;

    @BeforeEach
    void setup() {
        repository = mock(UserRepository.class);
        userValidator = new UserValidator(repository);
    }

    @Test
    void should_return_empty_if_repository_returns_empty() {
        when(repository.findByApiKey(anyString())).thenReturn(Optional.empty());
        assertThat(userValidator.validateUserRole(UUID.randomUUID(), RoleEntity.RoleType.ADMIN)).isEmpty();
    }

    @Test
    void should_return_empty_if_repository_returns_but_role_doesnot_match() {
        User mockUser = mock(User.class);
        when(mockUser.getRoleType()).thenReturn(RoleEntity.RoleType.CUSTOMER);
        when(repository.findByApiKey(anyString())).thenReturn(Optional.of(mockUser));
        assertThat(userValidator.validateUserRole(UUID.randomUUID(), RoleEntity.RoleType.ADMIN)).isEmpty();
    }

    @Test
    void should_return_user_if_repository_returns_and_role_match() {
        User mockUser = mock(User.class);
        when(mockUser.getRoleType()).thenReturn(RoleEntity.RoleType.ADMIN);
        when(repository.findByApiKey(anyString())).thenReturn(Optional.of(mockUser));
        assertThat(userValidator.validateUserRole(UUID.randomUUID(), RoleEntity.RoleType.ADMIN)).isPresent();
    }
}