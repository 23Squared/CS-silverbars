package com.twentyThreeSquared.silverbars.persistence.entity;

import java.util.UUID;

public class Order {

    public enum OrderType {
        BUY,
        SELL
    }

    private final UUID orderId;
    private final UUID userId;
    private final float quantity;
    private final int gbpPerKilogram;
    private final OrderType orderType;

    public Order(UUID orderId, UUID userId, float quantity, int gbpPerKilogram, OrderType orderType) {
        this.orderId = orderId;
        this.userId = userId;
        this.quantity = quantity;
        this.gbpPerKilogram = gbpPerKilogram;
        this.orderType = orderType;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public float getQuantity() {
        return quantity;
    }

    public int getGbpPerKilogram() {
        return gbpPerKilogram;
    }

    public OrderType getOrderType() {
        return orderType;
    }
}
