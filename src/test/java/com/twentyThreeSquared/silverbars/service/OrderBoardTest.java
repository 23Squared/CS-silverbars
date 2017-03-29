package com.twentyThreeSquared.silverbars.service;

import java.util.Set;
import java.util.UUID;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.twentyThreeSquared.silverbars.dto.OrderDto;
import com.twentyThreeSquared.silverbars.persistence.Database;
import com.twentyThreeSquared.silverbars.persistence.entity.Order;
import com.twentyThreeSquared.silverbars.util.OrderAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.twentyThreeSquared.silverbars.persistence.entity.Order.OrderType.BUY;
import static com.twentyThreeSquared.silverbars.persistence.entity.Order.OrderType.SELL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderBoardTest {

    @Mock
    private Database database;

    @InjectMocks
    private OrderBoard orderBoard;

    // We don't need to test registering multiple orders because there is no bulk save -
    // we'd just be calling the register() method multiple times successively.
    // This functionality is proved by the test below...
    @Test
    public void shouldRegisterSingleOrder() {
        UUID userId = UUID.randomUUID();

        UUID orderId = orderBoard.registerOrder(userId, 1.5F, 100, SELL);
        assertThat(orderId).isNotNull();

        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
        verify(database).saveOrUpdate(orderArgumentCaptor.capture());

        Order savedOrder = orderArgumentCaptor.getValue();
        OrderAssert.assertThat(savedOrder).isEqualTo(userId, 1.5F, 100, SELL);
    }

    @Test
    public void shouldCancelOrderForNonAggregatedOrders() {
        UUID userId = UUID.randomUUID();

        // Need to call register to get the UUID for the orders
        UUID orderId = orderBoard.registerOrder(userId, 1.5F, 100, SELL);

        orderBoard.cancelOrder(orderId);

        verify(database).delete(orderId);
    }

    @Test
    public void shouldCancelOrderForAggregatedOrders() {
        UUID user1Id = UUID.randomUUID();

        // Need to call register to get the UUID for the orders
        UUID order1Id = orderBoard.registerOrder(user1Id, 1.5F, 100, SELL);

        UUID user2Id = UUID.randomUUID();
        UUID order2Id = orderBoard.registerOrder(user2Id, 2.5F, 100, SELL);

        orderBoard.cancelOrder(order1Id);

        verify(database).delete(order1Id);
        verify(database, never()).delete(order2Id);
    }

    @Test
    public void shouldDisplayOrderBoardWithCorrectOrderingForSingleItem() {
        Table<Integer, UUID, Order> orders = HashBasedTable.create();
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), 1.5F, 100, SELL);
        orders.put(order.getGbpPerKilogram(), order.getOrderId(), order);

        when(database.getAll()).thenReturn(orders.rowMap());

        Set<OrderDto> orderBoardResponse = orderBoard.getOrderBoard();
        assertThat(orderBoardResponse.size()).isEqualTo(1);

        OrderDto expectedOrderDto = new OrderDto.Builder()
                .addOrderId(order.getOrderId())
                .withOrderType(SELL)
                .addQuantity(1.5F)
                .withPrice(100)
                .build();

        assertThat(orderBoardResponse).containsExactly(expectedOrderDto);
    }

    @Test
    public void shouldDisplayOrderBoardWithCorrectOrderingForMultipleItemsWithoutAggregation() {
        Order order1 = new Order(UUID.randomUUID(), UUID.randomUUID(), 1.5F, 100, SELL);
        Order order2 = new Order(UUID.randomUUID(), UUID.randomUUID(), 3.5F, 150, SELL);
        Order order3 = new Order(UUID.randomUUID(), UUID.randomUUID(), 1.0F, 80, SELL);

        Order order4 = new Order(UUID.randomUUID(), UUID.randomUUID(), 0.5F, 10, BUY);
        Order order5 = new Order(UUID.randomUUID(), UUID.randomUUID(), 1.5F, 400, BUY);
        Order order6 = new Order(UUID.randomUUID(), UUID.randomUUID(), 9.5F, 550, BUY);

        Table<Integer, UUID, Order> orders = HashBasedTable.create();
        orders.put(order1.getGbpPerKilogram(), order1.getOrderId(), order1);
        orders.put(order2.getGbpPerKilogram(), order2.getOrderId(), order2);
        orders.put(order3.getGbpPerKilogram(), order3.getOrderId(), order3);

        orders.put(order4.getGbpPerKilogram(), order4.getOrderId(), order4);
        orders.put(order5.getGbpPerKilogram(), order5.getOrderId(), order5);
        orders.put(order6.getGbpPerKilogram(), order6.getOrderId(), order6);

        when(database.getAll()).thenReturn(orders.rowMap());

        // Convert to a list to make it easier to verify the ordering of elements
        Set<OrderDto> orderBoardResponse = orderBoard.getOrderBoard();
        assertThat(orderBoardResponse.size()).isEqualTo(6);

        // SELL by price lowest to highest, BUY by price highest to lowest
        assertThat(orderBoardResponse).extracting(OrderDto::getPrice).containsExactly(80, 100, 150, 550, 400, 10);
    }

    @Test
    public void shouldDisplayOrderBoardWithCorrectOrderingForMultipleItemsWithAggregation() {
        Order order1 = new Order(UUID.randomUUID(), UUID.randomUUID(), 1.5F, 50, SELL);
        Order order2 = new Order(UUID.randomUUID(), UUID.randomUUID(), 3.5F, 50, SELL);
        Order order3 = new Order(UUID.randomUUID(), UUID.randomUUID(), 1.0F, 100, SELL);

        Order order4 = new Order(UUID.randomUUID(), UUID.randomUUID(), 0.5F, 200, BUY);
        Order order5 = new Order(UUID.randomUUID(), UUID.randomUUID(), 1.5F, 200, BUY);
        Order order6 = new Order(UUID.randomUUID(), UUID.randomUUID(), 9.5F, 100, BUY);

        Table<Integer, UUID, Order> orders = HashBasedTable.create();
        orders.put(order1.getGbpPerKilogram(), order1.getOrderId(), order1);
        orders.put(order2.getGbpPerKilogram(), order2.getOrderId(), order2);
        orders.put(order3.getGbpPerKilogram(), order3.getOrderId(), order3);

        orders.put(order4.getGbpPerKilogram(), order4.getOrderId(), order4);
        orders.put(order5.getGbpPerKilogram(), order5.getOrderId(), order5);
        orders.put(order6.getGbpPerKilogram(), order6.getOrderId(), order6);

        when(database.getAll()).thenReturn(orders.rowMap());

        Set<OrderDto> orderBoardResponse = orderBoard.getOrderBoard();
        assertThat(orderBoardResponse.size()).isEqualTo(4);

        // SELL by price lowest to highest, BUY by price highest to lowest
        assertThat(orderBoardResponse).extracting(OrderDto::getPrice).containsExactly(50, 100, 200, 100);
        assertThat(orderBoardResponse).extracting(OrderDto::getQuantity).containsExactly(5.0F, 1.0F, 2.0F, 9.5F);
    }
}
