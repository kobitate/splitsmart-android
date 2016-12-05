package com.kobitate.splitsmart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

	private ArrayList<Item> allItems;

	private Cart cart;
	private Basket basket1;
	private Basket basket2;

	private boolean usePowerset = true;

	private AppDB db;

	private final int PERMISSION_CAMERA = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		setupDB();
		setupViews();

		setupCart();
		buildLists();
	}

	private void setupViews() {

		try {
			usePowerset = db.getSetting("USE_POWERSET", 1);
			Log.v(getString(R.string.app_name), "USE_POWERSET is " + usePowerset);
		}
		catch (SQLiteException e) {
			Log.e(getString(R.string.app_name), "Error retrieving USE_POWERSET setting. " + e.getMessage());
			usePowerset = true;
		}

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (ContextCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions((Activity) view.getContext(), new String[]{android.Manifest.permission.CAMERA}, PERMISSION_CAMERA);
				}
				else {
					startActivity(new Intent(view.getContext(), AddItemActivity.class));
				}
			}
		});

		final MenuItem powersetOption = (MenuItem) findViewById(R.id.menu_intensive_algorithm);

		ImageButton options = (ImageButton) findViewById(R.id.options_menu);
		options.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PopupMenu menu = new PopupMenu(view.getContext(), view);
				MenuInflater inflater = menu.getMenuInflater();
				inflater.inflate(R.menu.options, menu.getMenu());
				menu.show();
				if (!usePowerset) {
					powersetOption.setChecked(false);
				}
				menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch (item.getItemId()) {
							case R.id.menu_settings:
								startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
								return true;
							case R.id.menu_intensive_algorithm:
								db.setSetting("USE_POWERSET", (!usePowerset) ? 1 : 0);
								item.setChecked(!usePowerset);
								usePowerset = false;
								startActivity(new Intent(getApplicationContext(), MainActivity.class));
								return true;
							default:
								return false;
						}
					}
				});
			}
		});
	}

	private void setupDB() {
		db = AppDB.getInstance(this);

		try {
			allItems = db.getItems();
			Log.v(getString(R.string.app_name), "Got all items from DB");
		}
		catch (SQLiteException e) {
			Log.e(getString(R.string.app_name), "SQLite Error: " + e.getMessage());
			Toast.makeText(this, "Error loading items from DB", Toast.LENGTH_SHORT).show();
		}
	}

	private void setupCart() {
		basket1 = new Basket(new Shopper("Shopper 1"));
		basket2 = new Basket(new Shopper("Shopper 2"));

		cart = new Cart(basket1, basket2);

		cart.setItems(allItems);

		if (usePowerset) {
			cart.powersetDistribute();
		}
		else {
			cart.greedyDistribute();
		}


	}

	private void buildLists() {
		ItemAdapter basket1Adapter = new ItemAdapter(this, R.id.basket1_items, cart.basket1);
		ItemAdapter basket2Adapter = new ItemAdapter(this, R.id.basket1_items, cart.basket2);

		Log.v(getString(R.string.app_name), "Adapters made");

		ListView basket1List = (ListView) findViewById(R.id.basket1_items);
		ListView basket2List = (ListView) findViewById(R.id.basket2_items);

		basket1List.setAdapter(basket1Adapter);
		basket2List.setAdapter(basket2Adapter);

		TextView basket1Total = (TextView) findViewById(R.id.basket1_total);
		TextView basket2Total = (TextView) findViewById(R.id.basket2_total);

		basket1Total.setText(String.format(Locale.getDefault(), "$%.2f", cart.basket1.getSum()));
		basket2Total.setText(String.format(Locale.getDefault(), "$%.2f", cart.basket2.getSum()));

		Log.v(getString(R.string.app_name), "Adapters set");
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String permissions[], @NonNull int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_CAMERA:
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					startActivity(new Intent(this, AddItemActivity.class));
				}
				else {
					startActivity(new Intent(this, ItemDetailsActivity.class));
				}

		}

	}



}
