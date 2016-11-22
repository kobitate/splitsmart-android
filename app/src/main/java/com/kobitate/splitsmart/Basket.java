package com.kobitate.splitsmart;

import java.util.ArrayList;

/**
 * Created by kobi on 10/26/16.
 */

public class Basket extends ArrayList<Item> {
    private Shopper owner;
    private double sum;

    public Basket(Shopper owner) {
        super();
        this.owner = owner;
        sum = 0;
    }

    @Override
    public boolean add(Item item) {
        sum += item.getPrice();
        return super.add(item);
    }

    @Override
    public void clear() {
        sum = 0;
        super.clear();
    }

    public double getSum() {
        return sum;
    }

    public Shopper getOwner() {
        return owner;
    }
}
