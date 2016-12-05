package com.kobitate.splitsmart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by kobi on 12/3/16.
 * Class adapted from: https://guides.codepath.com/android/Local-Databases-with-SQLiteOpenHelper
 */

public class AppDB extends SQLiteOpenHelper {

	private static AppDB instance;

	public static synchronized AppDB getInstance(Context context) {
		if (instance == null) {
			instance = new AppDB(context.getApplicationContext());
		}
		return instance;
	}

	private AppDB(Context context) {
		super(context, "SPLITSMART_ITEMS", null, 3);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS ITEMS (" +
				"ITEM_ID INTEGER PRIMARY KEY," +
				"NAME TEXT," +
				"PRICE NUMERIC" +
				")");
		db.execSQL("CREATE TABLE IF NOT EXISTS SETTINGS (" +
				"SETTING_ID INTEGER PRIMARY KEY," +
				"NAME TEXT," +
				"VALUE NUMBER" +
				")");
		Log.v("SplitSmart", "Created DB");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int i, int i1) {
		if (i != i1) {
			db.execSQL("DROP TABLE IF EXISTS ITEMS");
			db.execSQL("DROP TABLE IF EXISTS SETTINGS");
			onCreate(db);
			Log.v("SplitSmart", "Upgraded DB");
		}
	}

	public ArrayList<Item> getItems() throws SQLiteException {

		Log.v("SplitSmart", "Connecting to DB");
		SQLiteDatabase db = getReadableDatabase();

		db.beginTransaction();

		ArrayList<Item> items = new ArrayList<>();

		String query = "SELECT * FROM ITEMS";
		Cursor c = db.rawQuery(query, null);
		Log.v("SplitSmart", "Running query");
		if (c.moveToFirst()) {
			do {
				int id = c.getInt(c.getColumnIndex("ITEM_ID"));
				String name = c.getString(c.getColumnIndex("NAME"));
				double price = c.getDouble(c.getColumnIndex("PRICE"));

				Log.v("SplitSmart", "Found item " + id + " " + name + " " + price);

				Item newItem = new Item(id, name, price);

				items.add(newItem);

			} while(c.moveToNext());
		}

		if (!c.isClosed()) {
			c.close();

			Log.v("SplitSmart", "closing DB");
		}

		return items;
	}

	public void addItem(String name, double price) throws SQLiteException {
		Log.v("SplitSmart", "Adding item");
		SQLiteDatabase db = getWritableDatabase();

		db.beginTransaction();

		ContentValues values = new ContentValues();
		values.put("NAME", name);
		values.put("PRICE", price);

		db.insertOrThrow("ITEMS", null, values);
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public void setSetting(String name, int value) throws SQLiteException {
		SQLiteDatabase db = getWritableDatabase();

		db.beginTransaction();

		ContentValues values = new ContentValues();
		values.put("NAME", name);
		values.put("VALUE", value);

		db.insertWithOnConflict("SETTINGS", null, values, SQLiteDatabase.CONFLICT_REPLACE);
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public boolean getSetting(String name, int defaultValue) throws SQLiteException {
		SQLiteDatabase db = getReadableDatabase();
		String query = "SELECT VALUE FROM SETTINGS WHERE NAME='"+name+"' LIMIT 1";
		Cursor c = db.rawQuery(query, null);

		c.moveToFirst();

		if (c.getCount() > 0) {
			boolean result = c.getInt(0) == 1;
			c.close();
			return result;
		}
		else {
			setSetting(name, defaultValue);
			c.close();
			return getSetting(name, defaultValue);
		}

	}
}
