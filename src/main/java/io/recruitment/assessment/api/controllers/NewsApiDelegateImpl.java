package io.recruitment.assessment.api.controllers;

import io.recruitment.assessment.api.controller.NewsApiDelegate;
import io.recruitment.assessment.api.controller.ProductsApiDelegate;
import io.recruitment.assessment.api.model.News;
import io.recruitment.assessment.api.model.Product;
import io.recruitment.assessment.api.repository.NewsRepository;
import io.recruitment.assessment.api.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewsApiDelegateImpl implements NewsApiDelegate {

    private final NewsRepository repository;
    private final ModelMapper modelMapper;

    @Autowired
    public NewsApiDelegateImpl(NewsRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseEntity<List<News>> getAllNews() {
        return ResponseEntity.ok(repository.findAll()
                .stream()
                .map(entity -> modelMapper.map(entity, News.class))
                .collect(Collectors.toList()));
    }

}
