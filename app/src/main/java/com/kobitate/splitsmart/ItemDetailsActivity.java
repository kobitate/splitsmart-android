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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Locale;

public class ItemDetailsActivity extends AppCompatActivity {

	EditText inputName;
	EditText inputPrice;

	ImageView thumb;

	FloatingActionButton submitFAB;

	CheckBox isWeightBox;
	LinearLayout weightLayout;
	TextView weightValue;
	TextView finalPriceView;
	LinearLayout weightPriceSummary;

	double byWeightPrice = 0;

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

		isWeightBox = (CheckBox) findViewById(R.id.perPoundCheck);
		weightLayout = (LinearLayout) findViewById(R.id.weightEntry);
		weightValue = (TextView) findViewById(R.id.weightValue);
		finalPriceView = (TextView) findViewById(R.id.weightPrice);
		weightPriceSummary = (LinearLayout) findViewById(R.id.poundPriceSummary);

		if (getIntent().hasExtra("scanned_item_name") && getIntent().hasExtra("scanned_item_price")) {
			inputName.setText(getIntent().getStringExtra("scanned_item_name"));
			inputPrice.setText(String.valueOf(getIntent().getDoubleExtra("scanned_item_price",0)));
		}

		if (getIntent().hasExtra("scanned_item_image")) {
			Picasso.with(this).load(getIntent().getStringExtra("scanned_item_image")).into(thumb);
			thumb.setMinimumHeight(100);
			thumb.setMinimumWidth(100);
		}

		TextWatcher weightWatchers = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (isWeightBox.isChecked()) {
					updateByWeightPrice();
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		};

		weightValue.addTextChangedListener(weightWatchers);
		inputPrice.addTextChangedListener(weightWatchers);

		submitFAB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (inputName.getText().length() == 0 || inputPrice.getText().length() == 0 || (isWeightBox.isChecked() && weightValue.getText().length() == 0)) {
					Toast.makeText(ItemDetailsActivity.this, "Please enter the item name and price to continue.", Toast.LENGTH_SHORT).show();
				}
				else {
					AppDB db = AppDB.getInstance(view.getContext());
					try {
						if (isWeightBox.isChecked()) {
							db.addItem(inputName.getText().toString() + " (" + weightValue.getText().toString() + " lbs)", byWeightPrice);
						} else {
							db.addItem(inputName.getText().toString(), Double.valueOf(inputPrice.getText().toString()));
						}
					}
					catch (SQLiteException e) {
						Log.e(getString(R.string.app_name), "SQLite Error: " + e.getMessage());
						Toast.makeText(view.getContext(), "Error adding item to database", Toast.LENGTH_SHORT).show();
					}

					startActivity(new Intent(view.getContext(), MainActivity.class));
				}

			}
		});

		isWeightBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					weightLayout.setVisibility(View.VISIBLE);
				} else {
					weightLayout.setVisibility(View.INVISIBLE);
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

	public void updateByWeightPrice() {
		if (inputPrice.getText().length() != 0 && weightValue.getText().length() != 0) {
			Double pricePerPound = Double.valueOf(inputPrice.getText().toString());
			Double pounds = Double.valueOf(weightValue.getText().toString());
			Double finalPrice = pricePerPound * pounds;

			finalPriceView.setText(String.format(Locale.getDefault(), "$%.2f", finalPrice));

			byWeightPrice = finalPrice;
			weightPriceSummary.setVisibility(View.VISIBLE);
		}
		else {
			weightPriceSummary.setVisibility(View.INVISIBLE);
		}

	}
}
