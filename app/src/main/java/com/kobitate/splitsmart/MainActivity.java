package com.kobitate.splitsmart;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

	private ListView itemList;
	private ItemAdapter itemAdapter;
	private Toolbar toolbar;

	private ViewPager pager;
	private PagerAdapter pagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		Item[] test = {new Item("Thing", 1.99), new Item("Thing", 1.99), new Item("Thing", 1.99)};

		itemAdapter = new ItemAdapter(this, R.layout.listitem, test);

		itemList = (ListView) findViewById(R.id.items);
		itemList.setAdapter(itemAdapter);

	}

}
