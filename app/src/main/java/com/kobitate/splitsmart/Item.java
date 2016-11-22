package com.kobitate.splitsmart;

/**
 * Created by kobi on 10/26/16.
 */

public class Item {

	private double price;
	private String name;
	private int upc;


	public Item(String name, double price) {
		this.name = name;
		this.price = price;
	}


	public Item(String name, double price, int upc) {
		this.price = price;
		this.name = name;
		this.upc = upc;
	}


	public double getPrice() {
		return price;
	}

	public String getName() {
		return name;
	}

	public int getUpc() {
		return upc;
	}

}
