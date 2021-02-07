package io.recruitment.assessment.api.controllers;

import io.recruitment.assessment.api.controller.ProductsApiDelegate;
import io.recruitment.assessment.api.model.Product;
import io.recruitment.assessment.api.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductsApiDelegateImpl implements ProductsApiDelegate {

    private final ProductRepository repository;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductsApiDelegateImpl(ProductRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(repository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, Product.class))
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<Product> getProduct(Integer id) {
        return ResponseEntity.ok(modelMapper.map(repository.findById(id), Product.class));
    }

    @Override
    public ResponseEntity<List<Product>> searchProduct(String query) {
        return ResponseEntity.ok(repository.findBySearchText(query)
                .stream()
                .map(entity -> modelMapper.map(entity, Product.class))
                .collect(Collectors.toList()));
    }

}
