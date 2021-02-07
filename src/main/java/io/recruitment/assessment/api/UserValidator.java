package io.recruitment.assessment.api;

import io.recruitment.assessment.api.domain.RoleEntity;
import io.recruitment.assessment.api.domain.User;
import io.recruitment.assessment.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserValidator {

    private final UserRepository repository;

    @Autowired
    public UserValidator(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> validateUserRole(UUID apiKey, RoleEntity.RoleType roleType) {
        Optional<User> userByApiKey = repository.findByApiKey(apiKey.toString());
        if(userByApiKey.isEmpty()) {
            return Optional.empty();
        } else if (!userByApiKey.get().getRoleType().equals(roleType)) {
            return Optional.empty();
        }
        return userByApiKey;
    }
}
