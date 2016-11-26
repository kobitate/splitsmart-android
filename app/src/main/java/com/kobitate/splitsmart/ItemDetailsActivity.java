package com.kobitate.splitsmart;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ItemDetailsActivity extends AppCompatActivity {

	EditText inputName;
	EditText inputPrice;

	ImageView thumb;

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

		inputName = (EditText) findViewById(R.id.input_item_name);
		inputPrice = (EditText) findViewById(R.id.input_item_price);
		thumb = (ImageView) findViewById(R.id.item_thumb);

		if (getIntent().hasExtra("scanned_item_name") && getIntent().hasExtra("scanned_item_price")) {
			inputName.setText(getIntent().getStringExtra("scanned_item_name"));
			inputPrice.setText(String.valueOf(getIntent().getDoubleExtra("scanned_item_price",0)));
		}

		if (getIntent().hasExtra("scanned_item_image")) {
			Picasso.with(this).load(getIntent().getStringExtra("scanned_item_image")).into(thumb);
			thumb.setMinimumHeight(100);
			thumb.setMinimumWidth(100);
		}

	}
}
