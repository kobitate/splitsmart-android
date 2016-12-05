package com.kobitate.splitsmart;

import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kobi on 10/26/16.
 */

public class Cart {
	public Basket basket1;
    public Basket basket2;
	public ArrayList<Item> items;

	public Cart(Basket basket1, Basket basket2) {
		this.basket1 = basket1;
        this.basket2 = basket2;
		this.items = new ArrayList<Item>();
	}

    public void add(Item item) {
        items.add(item);
    }

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

	public void greedyDistribute() {
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

	public void powersetDistribute() {

		double bestDiff = 0;
		boolean startedLoop = false;

		Set<Item> bestBasket1 = null;
		Set<Item> bestBasket2 = null;

		Set<Item> itemsSet = new HashSet<>(items);

		Set<Set<Item>> powerset = Sets.powerSet(itemsSet);
		for (Set<Item> aBasket1 : powerset) {
			Set<Item> aBasket2 = Sets.difference(itemsSet, aBasket1);

			double sumBasket1 = 0;
			double sumBasket2 = 0;

			for (Item item : aBasket1) {
				sumBasket1 += item.getPrice();
			}

			for (Item item : aBasket2) {
				sumBasket2 += item.getPrice();
			}

			double aDiff = Math.abs(sumBasket1 - sumBasket2);

			if (!startedLoop || aDiff < bestDiff) {
				bestDiff = aDiff;
				bestBasket1 = aBasket1;
				bestBasket2 = aBasket2;
				startedLoop = true;
			}

		}

		assert bestBasket1 != null;
		for (Item item : bestBasket1) {
			basket1.add(item);
		}

		for (Item item : bestBasket2) {
			basket2.add(item);
		}

	}



}
