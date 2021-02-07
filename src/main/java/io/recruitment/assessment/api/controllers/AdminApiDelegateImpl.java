package io.recruitment.assessment.api.controllers;

import io.recruitment.assessment.api.UserValidator;
import io.recruitment.assessment.api.controller.AdminApiDelegate;
import io.recruitment.assessment.api.domain.NewsEntity;
import io.recruitment.assessment.api.domain.ProductEntity;
import io.recruitment.assessment.api.domain.RoleEntity;
import io.recruitment.assessment.api.domain.User;
import io.recruitment.assessment.api.model.News;
import io.recruitment.assessment.api.model.Product;
import io.recruitment.assessment.api.repository.NewsRepository;
import io.recruitment.assessment.api.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AdminApiDelegateImpl implements AdminApiDelegate {

    private final ProductRepository productRepository;
    private final NewsRepository newsRepository;
    private final UserValidator userValidator;

    private static final RoleEntity.RoleType REQUIRED_ROLE = RoleEntity.RoleType.ADMIN;

    @Autowired
    public AdminApiDelegateImpl(ProductRepository productRepository, NewsRepository newsRepository, UserValidator userValidator) {
        this.productRepository = productRepository;
        this.newsRepository = newsRepository;
        this.userValidator = userValidator;
    }

    @Override
    @Transactional
    public ResponseEntity<Void> addProduct(UUID X_API_KEY, Product product) {
        Optional<User> optionalUser = userValidator.validateUserRole(X_API_KEY, REQUIRED_ROLE);
        if (optionalUser.isPresent()) {
            ProductEntity productEntity = mapProduct(product, optionalUser.get());
            productRepository.save(productEntity);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Transactional
    public ResponseEntity<Void> deleteProduct(Integer id, UUID X_API_KEY) {
        Optional<User> optionalUser = userValidator.validateUserRole(X_API_KEY, REQUIRED_ROLE);
        if (optionalUser.isPresent()) {
            try {
                productRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } catch (EmptyResultDataAccessException ex) {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Void> updateProduct(UUID X_API_KEY, Product product) {
        Optional<User> optionalUser = userValidator.validateUserRole(X_API_KEY, REQUIRED_ROLE);
        if (optionalUser.isPresent()) {
            ProductEntity productEntity = mapProduct(product, optionalUser.get());
            productRepository.save(productEntity);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Void> addNews(UUID X_API_KEY,
                                         News news) {
        Optional<User> optionalUser = userValidator.validateUserRole(X_API_KEY, REQUIRED_ROLE);
        if (optionalUser.isPresent()) {
            newsRepository.save(mapNews(news, optionalUser.get()));
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteNews(Integer id,
                                            UUID X_API_KEY) {
        Optional<User> optionalUser = userValidator.validateUserRole(X_API_KEY, REQUIRED_ROLE);
        if (optionalUser.isPresent()) {
            try {
                newsRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } catch (EmptyResultDataAccessException ex) {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Void> updateNews(UUID X_API_KEY,
                                            News news) {
        Optional<User> optionalUser = userValidator.validateUserRole(X_API_KEY, REQUIRED_ROLE);
        if (optionalUser.isPresent()) {
            newsRepository.save(mapNews(news, optionalUser.get()));
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private ProductEntity mapProduct(Product product, User user) {
        return ProductEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(new BigDecimal(product.getPrice()))
                .createdBy(user.getId())
                .updatedBy(user.getId())
                .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();
    }

    private NewsEntity mapNews(News news, User user) {
        return NewsEntity.builder()
                .id(news.getId())
                .title(news.getTitle())
                .shortDescription(news.getShortDescription())
                .longDescription(news.getShortDescription())
                .imageUrl(news.getImageUrl())
                .createdBy(user.getId())
                .updatedBy(user.getId())
                .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();
    }
}
