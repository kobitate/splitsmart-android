package com.kobitate.splitsmart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by kobi on 10/26/16.
 */

public class Cart {
	private Basket basket1;
    private Basket basket2;
	private ArrayList<Item> items;

	public Cart(Basket basket1, Basket basket2) {
		this.basket1 = basket1;
        this.basket2 = basket2;
		this.items = new ArrayList<Item>();
	}

    public void add(Item item) {
        items.add(item);
    }

	public void distributeItems() {
        // sort items descending
		Collections.sort(items, new Comparator<Item>() {
			@Override
			public int compare(Item item1, Item item2) {
				if (item1.getPrice() < item2.getPrice()) {
					return 1;
				}
				if (item1.getPrice() > item2.getPrice()) {
					return -1;
				}
				return 0;
			}
		});

		for (Item item : items) {
			if (basket1.size() == 0) {
                basket1.add(item);
            }
            else if (basket2.size() == 0) {
                basket2.add(item);
            }
            else {
                if (basket1.getSum() < basket2.getSum()) {
                    basket1.add(item);
                }
                else {
                    basket2.add(item);
                }
            }
		}

	}

}
