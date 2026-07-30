package re.edu.orderservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import re.edu.orderservice.client.InventoryClient;
import re.edu.orderservice.dto.InventoryResponse;
import re.edu.orderservice.dto.OrderRequest;
import re.edu.orderservice.dto.OrderResponse;
import re.edu.orderservice.model.Order;
import re.edu.orderservice.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final ModelMapper modelMapper;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating order for productId: {}, quantity: {}", request.getProductId(), request.getQuantity());

        InventoryResponse inventory = inventoryClient.getInventory(request.getProductId());

        if (inventory == null) {
            throw new EntityNotFoundException("Product not found with id: " + request.getProductId());
        }

        if (inventory.getQuantity() < request.getQuantity()) {
            throw new IllegalStateException("Insufficient stock. Available: " + inventory.getQuantity() +
                    ", Requested: " + request.getQuantity());
        }

        inventoryClient.deductInventory(request.getProductId(), request.getQuantity());

        BigDecimal totalPrice = inventory.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        Order order = Order.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .status(Order.OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {}", savedOrder.getId());

        return mapToResponse(savedOrder, inventory.getProductName(), inventory.getPrice());
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponseWithProductName)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        return mapToResponseWithProductName(order);
    }

    private OrderResponse mapToResponseWithProductName(Order order) {
        try {
            InventoryResponse inventory = inventoryClient.getInventory(order.getProductId());
            return mapToResponse(order, inventory.getProductName(), inventory.getPrice());
        } catch (Exception e) {
            log.warn("Could not fetch product name for order {}", order.getProductId());
            return mapToResponse(order, "Unknown Product", BigDecimal.ZERO);
        }
    }

    private OrderResponse mapToResponse(Order order, String productName, BigDecimal price) {
        return OrderResponse.builder()
                .id(order.getId())
                .productId(order.getProductId())
                .productName(productName)
                .quantity(order.getQuantity())
                .price(price)
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
