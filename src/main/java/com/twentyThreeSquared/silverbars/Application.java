package com.twentyThreeSquared.silverbars;

import com.twentyThreeSquared.silverbars.persistence.PersistentStorage;
import com.twentyThreeSquared.silverbars.service.OrderBoard;

public class Application {

    public static void main(String[] args) {
        OrderBoard orderBoard = new OrderBoard(new PersistentStorage());
    }
}
