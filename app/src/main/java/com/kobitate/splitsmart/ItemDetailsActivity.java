package com.kobitate.splitsmart;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ItemDetailsActivity extends AppCompatActivity {

	EditText inputName;
	EditText inputPrice;

	ImageView thumb;

	FloatingActionButton submitFAB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_details);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		final ActionBar ab = getSupportActionBar();
		if(ab != null) {
			ab.setDisplayHomeAsUpEnabled(true);
		}

		setupToolbar();

		inputName = (EditText) findViewById(R.id.input_item_name);
		inputPrice = (EditText) findViewById(R.id.input_item_price);

		thumb = (ImageView) findViewById(R.id.item_thumb);

		submitFAB = (FloatingActionButton) findViewById(R.id.details_submit);

		if (getIntent().hasExtra("scanned_item_name") && getIntent().hasExtra("scanned_item_price")) {
			inputName.setText(getIntent().getStringExtra("scanned_item_name"));
			inputPrice.setText(String.valueOf(getIntent().getDoubleExtra("scanned_item_price",0)));
		}

		if (getIntent().hasExtra("scanned_item_image")) {
			Picasso.with(this).load(getIntent().getStringExtra("scanned_item_image")).into(thumb);
			thumb.setMinimumHeight(100);
			thumb.setMinimumWidth(100);
		}


		submitFAB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (inputName.getText().length() == 0 || inputPrice.getText().length() == 0) {
					Toast.makeText(ItemDetailsActivity.this, "Please enter the item name and price to continue.", Toast.LENGTH_SHORT).show();
				}
				else {
					AppDB db = AppDB.getInstance(view.getContext());
					try {
						db.addItem(inputName.getText().toString(), Double.valueOf(inputPrice.getText().toString()));
					}
					catch (SQLiteException e) {
						Log.e(getString(R.string.app_name), "SQLite Error: " + e.getMessage());
						Toast.makeText(view.getContext(), "Error adding item to database", Toast.LENGTH_SHORT).show();
					}

					startActivity(new Intent(view.getContext(), MainActivity.class));
				}

			}
		});

	}

	public void setupToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		final ActionBar ab = getSupportActionBar();
		if(ab != null) {
			ab.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
