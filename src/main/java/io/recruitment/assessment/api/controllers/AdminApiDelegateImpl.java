package io.recruitment.assessment.api.controllers;

import io.recruitment.assessment.api.controller.AdminApiDelegate;
import io.recruitment.assessment.api.controller.NewsApiDelegate;
import io.recruitment.assessment.api.model.News;
import io.recruitment.assessment.api.model.Product;
import io.recruitment.assessment.api.repository.NewsRepository;
import io.recruitment.assessment.api.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminApiDelegateImpl implements AdminApiDelegate {

    private final ProductRepository repository;
    private final ModelMapper modelMapper;

    @Autowired
    public AdminApiDelegateImpl(ProductRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public ResponseEntity<Void> addProduct(UUID X_API_KEY, Product product) {
        //repository.save(modelMapper.map(product, Product.class))
        return ResponseEntity.ok().build();

    }

    @Transactional
    public ResponseEntity<Void> deleteProduct(UUID X_API_KEY,Integer id) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    @Override
    @Transactional
    public ResponseEntity<Void> updateProduct(UUID X_API_KEY, Product product) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
