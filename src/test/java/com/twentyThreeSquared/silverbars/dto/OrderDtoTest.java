package com.twentyThreeSquared.silverbars.dto;

import java.util.UUID;

import org.junit.Test;

import static com.twentyThreeSquared.silverbars.persistence.entity.Order.OrderType.BUY;
import static com.twentyThreeSquared.silverbars.persistence.entity.Order.OrderType.SELL;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderDtoTest {

    @Test
    public void shouldIncrementQuantityInOrder() {
        OrderDto orderDto = new OrderDto.Builder()
                .addQuantity(1.0F)
                .addQuantity(2.0F)
                .addQuantity(3.0F)
                .build();

        assertThat(orderDto.getQuantity()).isEqualTo(6.0F);
    }

    @Test
    public void shouldAddOrderIds() {
        UUID order1Id = UUID.randomUUID();
        UUID order2Id = UUID.randomUUID();
        UUID order3Id = UUID.randomUUID();

        OrderDto orderDto = new OrderDto.Builder()
                .addOrderId(order1Id)
                .addOrderId(order2Id)
                .addOrderId(order3Id)
                .build();

        assertThat(orderDto.getOrderIds()).containsExactlyInAnyOrder(order1Id, order2Id, order3Id);
    }

    @Test
    public void shouldSortBuyOrdersBelowSellOrders() {
        OrderDto sellOrderDto = getBasicOrderDtoBuilder().withOrderType(SELL).withPrice(100).build();
        OrderDto buyOrderDto = getBasicOrderDtoBuilder().withOrderType(BUY).withPrice(100).build();

        assertThat(sellOrderDto.compareTo(buyOrderDto)).isEqualTo(-1);
        assertThat(buyOrderDto.compareTo(sellOrderDto)).isEqualTo(1);
    }

    @Test
    public void shouldSortSellOrdersByPriceAscending() {
        OrderDto firstOrderDto = getBasicOrderDtoBuilder().withOrderType(SELL).withPrice(100).build();
        OrderDto secondOrderDto = getBasicOrderDtoBuilder().withOrderType(SELL).withPrice(200).build();

        assertThat(firstOrderDto.compareTo(secondOrderDto)).isEqualTo(-1);
        assertThat(secondOrderDto.compareTo(firstOrderDto)).isEqualTo(1);
    }

    @Test
    public void shouldSortBuyOrdersByPriceDescending() {
        OrderDto firstOrderDto = getBasicOrderDtoBuilder().withOrderType(BUY).withPrice(200).build();
        OrderDto secondOrderDto = getBasicOrderDtoBuilder().withOrderType(BUY).withPrice(100).build();

        assertThat(firstOrderDto.compareTo(secondOrderDto)).isEqualTo(-1);
        assertThat(secondOrderDto.compareTo(firstOrderDto)).isEqualTo(1);
    }

    @Test
    public void shouldSortEqualObjects() {
        OrderDto sellOrderDto = getBasicOrderDtoBuilder().withOrderType(SELL).withPrice(100).build();
        OrderDto buyOrderDto = getBasicOrderDtoBuilder().withOrderType(BUY).withPrice(100).build();

        assertThat(sellOrderDto.compareTo(sellOrderDto)).isEqualTo(0);
        assertThat(buyOrderDto.compareTo(buyOrderDto)).isEqualTo(0);
    }

    private OrderDto.Builder getBasicOrderDtoBuilder() {
        return new OrderDto.Builder()
                .addOrderId(UUID.randomUUID())
                .addQuantity(1.0F);
    }
}
