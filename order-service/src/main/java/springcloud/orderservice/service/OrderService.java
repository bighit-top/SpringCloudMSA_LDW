package springcloud.orderservice.service;

import springcloud.orderservice.dto.OrderDto;
import springcloud.orderservice.entity.OrderEntity;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDetails);
    OrderDto getOrderByOrderId(String orderId);
    Iterable<OrderEntity> getOrdersByUserId(String userId);
}
