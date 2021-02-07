package io.recruitment.assessment.api.repository;

import io.recruitment.assessment.api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("select c from User c "
            + "where c.apiKey = :apiKey")
    Optional<User> findByApiKey(@Param("apiKey") String apiKey);
}
