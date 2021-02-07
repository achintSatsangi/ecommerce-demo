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
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.recruitment.assessment.api.domain.RoleEntity.RoleType.ADMIN;
import static io.recruitment.assessment.api.domain.RoleEntity.RoleType.CUSTOMER;

@Service
@Slf4j
public class OrderApiDelegateImpl implements OrderApiDelegate {

    private final OrderzRepository repository;
    private final UserValidator userValidator;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderApiDelegateImpl(OrderzRepository repository, UserValidator userValidator, ModelMapper modelMapper) {
        this.repository = repository;
        this.userValidator = userValidator;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseEntity<Order> getOrder(Integer id,
                                           UUID X_API_KEY) {
        Optional<User> optionalAdminUser = userValidator.validateUserRole(X_API_KEY, ADMIN);
        if(optionalAdminUser.isPresent()) {
            Optional<OrderzEntity> orderById = repository.findById(id);
            if(orderById.isPresent()) {
                return ResponseEntity.ok(mapOrder(orderById.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        }
        Optional<User> optionalUser = userValidator.validateUserRole(X_API_KEY, CUSTOMER);
        if(optionalUser.isPresent()) {
            Optional<OrderzEntity> orderById = repository.findById(id);
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

    private Order mapOrder(OrderzEntity orderzEntity) {
        if (Objects.isNull(orderzEntity)) {
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

    private OrderProduct mapOrderzProductEntityToOrderProduct(OrderzProductEntity entity) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(modelMapper.map(entity.getProductEntity(), Product.class));
        orderProduct.setQuantity(entity.getQuantity());
        return orderProduct;
    }
}
