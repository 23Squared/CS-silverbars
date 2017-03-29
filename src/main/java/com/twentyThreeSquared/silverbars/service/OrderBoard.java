package com.twentyThreeSquared.silverbars.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import com.twentyThreeSquared.silverbars.dto.OrderDto;
import com.twentyThreeSquared.silverbars.persistence.Database;
import com.twentyThreeSquared.silverbars.persistence.entity.Order;

import static com.twentyThreeSquared.silverbars.persistence.entity.Order.OrderType.BUY;
import static com.twentyThreeSquared.silverbars.persistence.entity.Order.OrderType.SELL;

public class OrderBoard {

    private final Database database;

    public OrderBoard(Database database) {
        this.database = database;
    }

    public UUID registerOrder(UUID userId, float quantity, int gbpPerKilogram, Order.OrderType orderType) {
        Order order = new Order(UUID.randomUUID(), userId, quantity, gbpPerKilogram, orderType);
        database.saveOrUpdate(order);

        return order.getOrderId();
    }

    public void cancelOrder(UUID orderId) {
        database.delete(orderId);
    }

    public Set<OrderDto> getOrderBoard() {
        Map<Integer, Map<UUID, Order>> orders = database.getAll();
        Set<OrderDto> orderBoard = new TreeSet<>();

        orders.forEach((price, ordersAtPrice) -> {

            // BUY and SELL orders need to be aggregated separately
            List<Order> sellOrders = ordersAtPrice.values().stream()
                    .filter(order -> SELL.equals(order.getOrderType()))
                    .collect(Collectors.toList());

            if(!sellOrders.isEmpty()) {
                OrderDto orderDto = aggregateOrders(sellOrders, price);
                orderBoard.add(orderDto);
            }

            List<Order> buyOrders = ordersAtPrice.values().stream()
                    .filter(order -> BUY.equals(order.getOrderType()))
                    .collect(Collectors.toList());

            if(!buyOrders.isEmpty()) {
                OrderDto orderDto = aggregateOrders(buyOrders, price);
                orderBoard.add(orderDto);
            }
        });

        return orderBoard;
    }

    private OrderDto aggregateOrders(Collection<Order> orders, int price) {
        OrderDto.Builder orderDtoBuilder = new OrderDto.Builder().withPrice(price);

        orders.stream().forEach(sellOrder -> orderDtoBuilder
                        .addOrderId(sellOrder.getOrderId())
                        .addQuantity(sellOrder.getQuantity())
                        .withOrderType(sellOrder.getOrderType()));

        return orderDtoBuilder.build();
    }
}
