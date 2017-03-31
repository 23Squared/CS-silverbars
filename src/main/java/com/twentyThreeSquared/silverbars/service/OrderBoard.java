package com.twentyThreeSquared.silverbars.service;

import com.twentyThreeSquared.silverbars.dto.OrderDto;
import com.twentyThreeSquared.silverbars.persistence.Database;
import com.twentyThreeSquared.silverbars.persistence.entity.Order;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.twentyThreeSquared.silverbars.persistence.entity.Order.OrderType.BUY;

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

        Predicate<Order> isBuyOrder = order -> order.getOrderType().equals(BUY);

        orders.forEach((price, ordersAtPrice) -> {
            Map<Boolean, List<Order>> partitionedOrders = ordersAtPrice.values().stream()
                    .collect(Collectors.partitioningBy(isBuyOrder));

            List<Order> sellOrders = partitionedOrders.get(true);
            if(!sellOrders.isEmpty()) {
                OrderDto orderDto = aggregateOrders(sellOrders, price);
                orderBoard.add(orderDto);
            }

            List<Order> buyOrders = partitionedOrders.get(false);
            if(!buyOrders.isEmpty()) {
                OrderDto orderDto = aggregateOrders(buyOrders, price);
                orderBoard.add(orderDto);
            }
        });

        return orderBoard;
    }

    private OrderDto aggregateOrders(Collection<Order> orders, int price) {
        OrderDto.Builder orderDtoBuilder = new OrderDto.Builder().withPrice(price);

        orders.forEach(sellOrder -> orderDtoBuilder
                        .addOrderId(sellOrder.getOrderId())
                        .addQuantity(sellOrder.getQuantity())
                        .withOrderType(sellOrder.getOrderType()));

        return orderDtoBuilder.build();
    }
}
