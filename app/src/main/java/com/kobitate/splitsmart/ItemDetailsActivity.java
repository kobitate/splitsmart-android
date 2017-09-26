package com.kobitate.splitsmart;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class ItemDetailsActivity extends AppCompatActivity {

	final int ITEMS_SINGLE = 0;
	final int ITEMS_MULTIPLE = 1;
	final int ITEMS_WEIGHT = 3;

	EditText inputName;
	EditText inputPrice;

	ImageView thumb;

	FloatingActionButton submitFAB;

	LinearLayout weightLayout;
	LinearLayout multipleLayout;

	CheckBox dontSplitMultiple;
	EditText numItems;

	TextView weightValue;
	TextView finalPriceView;
	LinearLayout weightPriceSummary;
	BottomBar bottomBar;

	int currentMode = ITEMS_SINGLE;

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

		weightLayout = (LinearLayout) findViewById(R.id.weightEntry);
		multipleLayout = (LinearLayout) findViewById(R.id.multipleEntry);

		weightValue = (TextView) findViewById(R.id.weightValue);
		finalPriceView = (TextView) findViewById(R.id.weightPrice);
		weightPriceSummary = (LinearLayout) findViewById(R.id.poundPriceSummary);
		bottomBar = (BottomBar) findViewById(R.id.bottomBar);

		dontSplitMultiple = (CheckBox) findViewById(R.id.multipleSplitCheck);
		numItems = (EditText) findViewById(R.id.inputNumItems);

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
				if (currentMode == ITEMS_WEIGHT) {
					updateByWeightPrice();
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		};

		weightValue.addTextChangedListener(weightWatchers);
		inputPrice.addTextChangedListener(weightWatchers);

		bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
			@Override
			public void onTabSelected(@IdRes int tabId) {
				switch (tabId) {
					case R.id.tab_single:
						currentMode = ITEMS_SINGLE;
						multipleLayout.setVisibility(View.GONE);
						weightLayout.setVisibility(View.GONE);
						break;
					case R.id.tab_multiple:
						currentMode = ITEMS_MULTIPLE;
						multipleLayout.setVisibility(View.VISIBLE);
						weightLayout.setVisibility(View.GONE);
						break;
					case R.id.tab_scale:
						currentMode = ITEMS_WEIGHT;
						multipleLayout.setVisibility(View.GONE);
						weightLayout.setVisibility(View.VISIBLE);
						break;
				}

			}
		});

		submitFAB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (inputName.getText().length() == 0 ||
						inputPrice.getText().length() == 0 ||
						(currentMode == ITEMS_WEIGHT && weightValue.getText().length() == 0) ||
						(currentMode == ITEMS_MULTIPLE && numItems.getText().length() == 0)
						) {
					Toast.makeText(ItemDetailsActivity.this, "Please enter the item name and price to continue.", Toast.LENGTH_SHORT).show();
				}
				else {
					AppDB db = AppDB.getInstance(view.getContext());
					try {
						String thisItemName = inputName.getText().toString();
						double thisItemPrice = Double.valueOf(inputPrice.getText().toString());
						switch (currentMode) {
							case ITEMS_SINGLE:
								db.addItem(thisItemName, thisItemPrice);
								break;
							case ITEMS_MULTIPLE:
								int thisNumItems = Integer.valueOf(numItems.getText().toString());
								if (dontSplitMultiple.isChecked()){
									double thisTotalPrice = thisItemPrice * thisNumItems;
									db.addItem(thisItemName + " (x" + thisNumItems + ")", thisTotalPrice);
								} else {
									for (int i = 0; i < thisNumItems; i++) {
										db.addItem(thisItemName, thisItemPrice);
									}
								}
								break;
							case ITEMS_WEIGHT:
								double thisItemWeight = Double.valueOf(weightValue.getText().toString());
								db.addItem(thisItemName + " (" + thisItemWeight + " lbs)", byWeightPrice);
								break;
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
		String pricePerPoundString = inputPrice.getText().toString();
		String poundsString = weightValue.getText().toString();

		if (inputPrice.getText().length() != 0 && weightValue.getText().length() != 0 && !pricePerPoundString.equals(".") && !poundsString.equals(".")) {
			Double pricePerPound = Double.valueOf(pricePerPoundString);
			Double pounds = Double.valueOf(poundsString);
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
