package com.twentyThreeSquared.silverbars.util;

import java.util.UUID;

import com.twentyThreeSquared.silverbars.persistence.entity.Order;
import org.assertj.core.api.AbstractAssert;

public class OrderAssert extends AbstractAssert<OrderAssert, Order> {

    public OrderAssert(Order actual) {
        super(actual, OrderAssert.class);
    }

    public static OrderAssert assertThat(Order actual) {
        return new OrderAssert(actual);
    }

    public OrderAssert isEqualTo(UUID userId, float quantity, int gbpPerKilogram, Order.OrderType orderType) {
        isNotNull();

        if (!actual.getUserId().equals(userId)) {
            failWithMessage("Expected userId to be <%s> but was <%s>", userId, actual.getUserId());
        }

        if (!(actual.getQuantity() == quantity)) {
            failWithMessage("Expected quantity to be <%s> but was <%s>", quantity, actual.getQuantity());
        }

        if (!(actual.getGbpPerKilogram() == gbpPerKilogram)) {
            failWithMessage("Expected GBP per Kg to be <%s> but was <%s>", gbpPerKilogram, actual.getGbpPerKilogram());
        }

        if (!actual.getOrderType().equals(orderType)) {
            failWithMessage("Expected order type to be <%s> but was <%s>", orderType, actual.getOrderType());
        }

        return this;
    }
}
