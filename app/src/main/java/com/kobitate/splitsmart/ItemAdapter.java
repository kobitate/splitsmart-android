package com.kobitate.splitsmart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

/**
 * Created by kobi on 10/31/16.
 */

public class ItemAdapter extends ArrayAdapter<Item> {


	public ItemAdapter(Context context, int resource) {
		super(context, resource);
	}

	public ItemAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public ItemAdapter(Context context, int resource, Item[] objects) {
		super(context, resource, objects);
	}

	public ItemAdapter(Context context, int resource, int textViewResourceId, Item[] objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public ItemAdapter(Context context, int resource, List<Item> objects) {
		super(context, resource, objects);
	}

	public ItemAdapter(Context context, int resource, int textViewResourceId, List<Item> objects) {
		super(context, resource, textViewResourceId, objects);
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Item item = getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem, parent, false);
		}

		TextView nameView = (TextView) convertView.findViewById(R.id.itemName);
		TextView priceView = (TextView) convertView.findViewById(R.id.itemPrice);

		assert item != null;
		nameView.setText(item.getName());
		priceView.setText(String.format(Locale.getDefault(), "$%.2f",item.getPrice()));

		return convertView;
	}
}
