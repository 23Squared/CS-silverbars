package com.twentyThreeSquared.silverbars.persistence;

import java.util.Map;
import java.util.UUID;

import com.twentyThreeSquared.silverbars.persistence.entity.Order;

public interface Database {

    void saveOrUpdate(Order order);

    Order get(UUID orderId);

    Map<Integer, Map<UUID, Order>> getAll();

    void delete(UUID orderId);
}
