package io.recruitment.assessment.api.controllers;

import io.recruitment.assessment.api.UserValidator;
import io.recruitment.assessment.api.controller.OrderApiDelegate;
import io.recruitment.assessment.api.domain.OrderzEntity;
import io.recruitment.assessment.api.domain.OrderzProductEntity;
import io.recruitment.assessment.api.domain.User;
import io.recruitment.assessment.api.model.Order;
import io.recruitment.assessment.api.model.OrderProduct;
import io.recruitment.assessment.api.model.Product;
import io.recruitment.assessment.api.repository.OrderzRepository;
import io.recruitment.assessment.api.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.recruitment.assessment.api.domain.RoleEntity.RoleType.ADMIN;
import static io.recruitment.assessment.api.domain.RoleEntity.RoleType.CUSTOMER;
import static java.util.Objects.isNull;

@Service
@Slf4j
public class OrderApiDelegateImpl implements OrderApiDelegate {

    private final OrderzRepository orderzRepository;
    private final ProductRepository productRepository;

    private final UserValidator userValidator;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderApiDelegateImpl(OrderzRepository orderzRepository, ProductRepository productRepository, UserValidator userValidator, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.orderzRepository = orderzRepository;
        this.userValidator = userValidator;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseEntity<Order> getOrder(Integer id,
                                           UUID X_API_KEY) {
        Optional<User> optionalAdminUser = userValidator.validateUserRole(X_API_KEY, ADMIN);
        if(optionalAdminUser.isPresent()) {
            Optional<OrderzEntity> orderById = orderzRepository.findById(id);
            if(orderById.isPresent()) {
                return ResponseEntity.ok(mapOrder(orderById.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        }
        Optional<User> optionalUser = userValidator.validateUserRole(X_API_KEY, CUSTOMER);
        if(optionalUser.isPresent()) {
            Optional<OrderzEntity> orderById = orderzRepository.findById(id);
            if(orderById.isPresent()) {
                if(optionalUser.get().getId().equals(orderById.get().getUserId())) {
                    return ResponseEntity.ok(mapOrder(orderById.get()));
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Order> addToExistingOrder(UUID X_API_KEY,
                                                     Integer id,
                                                     OrderProduct orderProduct) {
        Optional<User> optionalAdminUser = userValidator.validateUserRole(X_API_KEY, ADMIN);
        if(optionalAdminUser.isPresent()) {
            Optional<OrderzEntity> orderById = orderzRepository.findById(id);
            if(orderById.isPresent()) {
                return saveAndGetExistingOrderResponseEntity(orderProduct, orderById);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
        Optional<User> optionalUser = userValidator.validateUserRole(X_API_KEY, CUSTOMER);
        if(optionalUser.isPresent()) {
            Optional<OrderzEntity> orderById = orderzRepository.findById(id);
            if(orderById.isPresent()) {
                if(optionalUser.get().getId().equals(orderById.get().getUserId())) {
                    return saveAndGetExistingOrderResponseEntity(orderProduct, orderById);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Order> postFreshOrder(UUID X_API_KEY,
                                                 Order order,
                                                 Integer userId) {
        Optional<User> optionalAdminUser = userValidator.validateUserRole(X_API_KEY, ADMIN);
        if(optionalAdminUser.isPresent()) {
            return saveAndGetFreshOrderResponseEntity(order, userId);
        }
        Optional<User> optionalUser = userValidator.validateUserRole(X_API_KEY, CUSTOMER);
        if(optionalUser.isPresent()) {
            return saveAndGetFreshOrderResponseEntity(order, optionalUser.get().getId());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private ResponseEntity<Order> saveAndGetExistingOrderResponseEntity(OrderProduct orderProduct, Optional<OrderzEntity> orderById) {
        OrderzEntity orderzEntity = orderById.get();
        orderzEntity.getOrderzProductEntities().add(mapOrderProductToOrderzProductEntity(orderProduct));
        orderzRepository.saveAndFlush(orderzEntity);
        return ResponseEntity.ok(mapOrder(orderzEntity));
    }

    private ResponseEntity<Order> saveAndGetFreshOrderResponseEntity(Order order, Integer id) {
        OrderzEntity orderzEntity = mapOrderEntity(order, id);
        orderzRepository.saveAndFlush(orderzEntity);
        return ResponseEntity.ok(mapOrder(orderzEntity));
    }

    private Order mapOrder(OrderzEntity orderzEntity) {
        if (isNull(orderzEntity)) {
            return null;
        }
        Order order = new Order();
        order.setId(orderzEntity.getId());
        order.setName(orderzEntity.getName());
        order.setUserId(orderzEntity.getUserId());
        order.getOrderProducts().addAll(
        orderzEntity.getOrderzProductEntities().stream()
                .map(e -> mapOrderzProductEntityToOrderProduct(e))
                .collect(Collectors.toList()));
        order.setTotalPrice(
                orderzEntity.getOrderzProductEntities().stream()
                .map(e -> e.getProductEntity().getPrice().multiply(new BigDecimal(e.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return order;
    }

    private OrderzEntity mapOrderEntity(Order order, Integer userId) {
        if (isNull(order)) {
            return null;
        }
        return OrderzEntity.builder()
                .name(order.getName())
                .userId(userId)
                .orderzProductEntities(
                        order.getOrderProducts().stream()
                        .map(op -> mapOrderProductToOrderzProductEntity(op))
                        .collect(Collectors.toList())).build();
    }

    private OrderProduct mapOrderzProductEntityToOrderProduct(OrderzProductEntity entity) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(modelMapper.map(entity.getProductEntity(), Product.class));
        orderProduct.setQuantity(entity.getQuantity());
        return orderProduct;
    }

    private OrderzProductEntity mapOrderProductToOrderzProductEntity(OrderProduct orderProduct) {
        return OrderzProductEntity.builder()
                .productEntity(productRepository.findById(orderProduct.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Product Id is invalid")))
                .quantity(orderProduct.getQuantity())
                .build();
    }
}
