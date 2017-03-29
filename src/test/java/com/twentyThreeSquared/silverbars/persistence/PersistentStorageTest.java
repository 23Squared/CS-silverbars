package com.twentyThreeSquared.silverbars.persistence;

import java.util.Collection;
import java.util.UUID;

import com.twentyThreeSquared.silverbars.persistence.entity.Order;
import org.junit.Before;
import org.junit.Test;

import static com.twentyThreeSquared.silverbars.persistence.entity.Order.OrderType.BUY;
import static org.assertj.core.api.Assertions.assertThat;

public class PersistentStorageTest {

    private PersistentStorage persistentStorage;

    @Before
    public void setUp() {
        persistentStorage = new PersistentStorage();
    }

    @Test
    public void shouldsaveOrUpdateAndRetrieveOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID userID = UUID.randomUUID();
        float quantity = 1.5F;
        int gbpPerKilogram = 100;

        Order order = new Order(orderId, userID, quantity, gbpPerKilogram, BUY);

        persistentStorage.saveOrUpdate(order);
        assertThat(persistentStorage.get(orderId)).isEqualTo(order);
    }

    @Test
    public void shouldSaveAndRetrieveAllOrders() throws Exception {
        UUID order1Id = UUID.randomUUID();
        UUID userID = UUID.randomUUID();
        float quantity = 1.5F;
        int gbpPerKilogram = 100;

        Order order1 = new Order(order1Id, userID, quantity, gbpPerKilogram, BUY);

        UUID order2Id = UUID.randomUUID();
        Order order2 = new Order(order2Id, userID, quantity, gbpPerKilogram, BUY);

        UUID order3Id = UUID.randomUUID();
        Order order3 = new Order(order3Id, userID, quantity, gbpPerKilogram, BUY);

        persistentStorage.saveOrUpdate(order1);
        persistentStorage.saveOrUpdate(order2);
        persistentStorage.saveOrUpdate(order3);

        Collection<Order> orders = persistentStorage.getAll().get(100).values();

        assertThat(orders).isNotNull();
        assertThat(orders).extracting(Order::getOrderId).containsExactlyInAnyOrder(order1Id, order2Id, order3Id);
    }

    @Test
    public void shouldSaveAndDeleteOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID userID = UUID.randomUUID();
        float quantity = 1.5F;
        int gbpPerKilogram = 100;

        Order order = new Order(orderId, userID, quantity, gbpPerKilogram, BUY);

        persistentStorage.saveOrUpdate(order);
        assertThat(persistentStorage.get(orderId)).isEqualTo(order);

        persistentStorage.delete(orderId);
        assertThat(persistentStorage.get(orderId)).isNull();
    }
}
