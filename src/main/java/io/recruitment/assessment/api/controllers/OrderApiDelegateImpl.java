package io.recruitment.assessment.api.controllers;

import io.recruitment.assessment.api.UserValidator;
import io.recruitment.assessment.api.controller.OrderApiDelegate;
import io.recruitment.assessment.api.domain.OrderzEntity;
import io.recruitment.assessment.api.domain.OrderzProductEntity;
import io.recruitment.assessment.api.domain.ProductEntity;
import io.recruitment.assessment.api.domain.User;
import io.recruitment.assessment.api.model.Order;
import io.recruitment.assessment.api.model.OrderProduct;
import io.recruitment.assessment.api.model.Product;
import io.recruitment.assessment.api.repository.OrderzProductRepository;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
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
    private final OrderzProductRepository orderzProductRepository;

    private final UserValidator userValidator;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderApiDelegateImpl(OrderzRepository orderzRepository, ProductRepository productRepository, OrderzProductRepository orderzProductRepository, UserValidator userValidator, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.orderzRepository = orderzRepository;
        this.orderzProductRepository = orderzProductRepository;
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
                return saveAndGetExistingOrderResponseEntity(orderProduct, orderById, optionalAdminUser.get().getId());
            } else {
                return ResponseEntity.notFound().build();
            }
        }
        Optional<User> optionalUser = userValidator.validateUserRole(X_API_KEY, CUSTOMER);
        if(optionalUser.isPresent()) {
            Optional<OrderzEntity> orderById = orderzRepository.findById(id);
            if(orderById.isPresent()) {
                if(optionalUser.get().getId().equals(orderById.get().getUserId())) {
                    return saveAndGetExistingOrderResponseEntity(orderProduct, orderById, optionalUser.get().getId());
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
    public ResponseEntity<Order> placeFreshOrder(UUID X_API_KEY,
                                                 Order order,
                                                 Integer userId) {
        Optional<User> optionalAdminUser = userValidator.validateUserRole(X_API_KEY, ADMIN);
        if(optionalAdminUser.isPresent()) {
            return saveAndGetFreshOrderResponseEntity(order, userId, optionalAdminUser.get().getId());
        }
        Optional<User> optionalUser = userValidator.validateUserRole(X_API_KEY, CUSTOMER);
        if(optionalUser.isPresent()) {
            return saveAndGetFreshOrderResponseEntity(order, optionalUser.get().getId(), optionalUser.get().getId());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private ResponseEntity<Order> saveAndGetExistingOrderResponseEntity(OrderProduct orderProduct, Optional<OrderzEntity> orderById, Integer userId) {
        OrderzEntity orderzEntity = orderById.get();
        orderzProductRepository.save(mapOrderProductToOrderzProductEntity(orderProduct, userId, orderzEntity));
        orderzProductRepository.flush();
        return ResponseEntity.ok(mapOrder(orderzRepository.findById(orderzEntity.getId()).get()));
    }

    private ResponseEntity<Order> saveAndGetFreshOrderResponseEntity(Order order, Integer id, Integer apiUserId) {
        OrderzEntity orderzEntity = mapFreshOrderEntity(order, id, apiUserId);
        orderzRepository.saveAndFlush(orderzEntity);

        List<OrderzProductEntity> orderzProductEntities = order.getOrderProducts().stream()
                .map(op -> mapOrderProductToOrderzProductEntity(op, apiUserId, orderzEntity))
                .collect(Collectors.toList());
        orderzProductEntities.forEach(orderzProductRepository::saveAndFlush);
        return ResponseEntity.ok(mapOrder(orderzRepository.findById(orderzEntity.getId()).get()));
    }

    private Order mapOrder(OrderzEntity orderzEntity) {
        if (isNull(orderzEntity)) {
            return null;
        }
        Order order = new Order();
        order.setId(orderzEntity.getId());
        order.setName(orderzEntity.getName());
        order.setUserId(orderzEntity.getUserId());
        List<OrderzProductEntity> orderProductEntities = orderzProductRepository.findByOrderId(orderzEntity.getId());
        order.getOrderProducts().addAll(
                orderProductEntities.stream()
                .map(e -> mapOrderzProductEntityToOrderProduct(e))
                .collect(Collectors.toList()));
        order.setTotalPrice(
                orderProductEntities.stream()
                .map(e -> e.getProductEntity().getPrice().multiply(new BigDecimal(e.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return order;
    }

    private OrderzEntity mapFreshOrderEntity(Order order, Integer userId, Integer apiUserId) {
        if (isNull(order)) {
            return null;
        }
        return OrderzEntity.builder()
                .name(order.getName())
                .userId(userId)
                .createdBy(apiUserId)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .updatedBy(apiUserId)
                .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();
    }

    private OrderProduct mapOrderzProductEntityToOrderProduct(OrderzProductEntity entity) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(modelMapper.map(entity.getProductEntity(), Product.class));
        orderProduct.setQuantity(entity.getQuantity());
        return orderProduct;
    }

    private OrderzProductEntity mapOrderProductToOrderzProductEntity(OrderProduct orderProduct, Integer userId, OrderzEntity orderzEntity) {
        return OrderzProductEntity.builder()
                .orderzEntity(orderzEntity)
                .productEntity(productRepository.findById(orderProduct.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Product Id is invalid")))
                .quantity(orderProduct.getQuantity())
                .createdBy(userId)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .updatedBy(userId)
                .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();
    }
}
