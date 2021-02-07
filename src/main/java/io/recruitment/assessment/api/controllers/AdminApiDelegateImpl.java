package io.recruitment.assessment.api.controllers;

import io.recruitment.assessment.api.UserValidator;
import io.recruitment.assessment.api.controller.AdminApiDelegate;
import io.recruitment.assessment.api.domain.ProductEntity;
import io.recruitment.assessment.api.domain.RoleEntity;
import io.recruitment.assessment.api.domain.User;
import io.recruitment.assessment.api.model.Product;
import io.recruitment.assessment.api.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AdminApiDelegateImpl implements AdminApiDelegate {

    private final ProductRepository repository;
    private final ModelMapper modelMapper;
    private final UserValidator userValidator;

    private static final RoleEntity.RoleType REQUIRED_ROLE = RoleEntity.RoleType.ADMIN;

    @Autowired
    public AdminApiDelegateImpl(ProductRepository repository, ModelMapper modelMapper, UserValidator userValidator) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.userValidator = userValidator;
    }

    @Override
    @Transactional
    public ResponseEntity<Void> addProduct(UUID X_API_KEY, Product product) {
        Optional<User> optionalUser = userValidator.validateUserRole(X_API_KEY, REQUIRED_ROLE);
        if (optionalUser.isPresent()) {
            ProductEntity productEntity = modelMapper.map(product, ProductEntity.class).toBuilder()
                    .createdBy(optionalUser.get().getId())
                    .build();
            repository.save(productEntity);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Transactional
    public ResponseEntity<Void> deleteProduct(UUID X_API_KEY,Integer id) {
        Optional<User> optionalUser = userValidator.validateUserRole(X_API_KEY, REQUIRED_ROLE);
        if (optionalUser.isPresent()) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Void> updateProduct(UUID X_API_KEY, Product product) {
        Optional<User> optionalUser = userValidator.validateUserRole(X_API_KEY, REQUIRED_ROLE);
        if (optionalUser.isPresent()) {
            ProductEntity productEntity = modelMapper.map(product, ProductEntity.class).toBuilder()
                    .updatedBy(optionalUser.get().getId())
                    .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                    .build();
            repository.save(productEntity);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}
