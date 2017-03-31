package com.twentyThreeSquared.silverbars.persistence;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.twentyThreeSquared.silverbars.persistence.entity.Order;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class PersistentStorage implements Database {

    Table<Integer, UUID, Order> store = HashBasedTable.create();

    public void saveOrUpdate(Order order) {
        store.put(order.getGbpPerKilogram(), order.getOrderId(), order);
    }

    public Order get(UUID orderId) {
        Map<Integer, Order> column = store.column(orderId);

        int size = column.size();

        if(size <= 0) {
            return null;
        }

        return new ArrayList<>(column.values()).get(0);
    }

    public Map<Integer, Map<UUID, Order>> getAll() {
        return store.rowMap();
    }

    public void delete(UUID orderId) {
        // TODO - Think there might be a way to store data so that it's
        // TODO - indexed and available via id or price
        int price = get(orderId).getGbpPerKilogram();
        store.remove(price, orderId);
    }

}
