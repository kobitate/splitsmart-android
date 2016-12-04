package com.kobitate.splitsmart;

/**
 * Created by kobi on 10/26/16.
 */

public class Item {

	private double price;
	private String name;
	private int dbID;


	public Item(int dbID, String name, double price) {
		this.dbID = dbID;
		this.name = name;
		this.price = price;
	}


	public double getPrice() {
		return price;
	}

	public String getName() {
		return name;
	}

}
