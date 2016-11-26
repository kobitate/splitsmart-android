package com.kobitate.splitsmart;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

	private ListView itemList;
	private ItemAdapter itemAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(view.getContext(), AddItemActivity.class));
			}
		});

		Item[] test = {new Item("Thing", 1.99), new Item("Thing", 1.99), new Item("Thing", 1.99)};

		itemAdapter = new ItemAdapter(this, R.layout.listitem, test);

		itemList = (ListView) findViewById(R.id.items);
		itemList.setAdapter(itemAdapter);
	}

}
