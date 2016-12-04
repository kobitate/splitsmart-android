package com.kobitate.splitsmart;

/**
 * Derived from: https://github.com/dm77/barcodescanner/blob/master/zxing-sample/src/main/java/me/dm7/barcodescanner/zxing/sample/CustomViewFinderScannerActivity.java
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class AddItemActivity extends BaseScannerActivity implements ZXingScannerView.ResultHandler {

	private ZXingScannerView scannerView;
	private Button toManualButton;
	private ViewGroup scannerFrame;

	private final int PERMISSION_CAMERA = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_item);
		setupToolbar();

		scannerFrame = (ViewGroup) findViewById(R.id.scanner_frame);
		toManualButton = (Button) findViewById(R.id.manual_entry);

		scannerView = new ZXingScannerView(this) {
			@Override
			protected IViewFinder createViewFinderView(Context context) {
				return new ViewFinderView(context);
			}
		};
		scannerFrame.addView(scannerView);

		toManualButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(view.getContext(), ItemDetailsActivity.class));
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		scannerView.setResultHandler(this);
		scannerView.startCamera();
	}

	@Override
	public void onPause() {
		super.onPause();
		scannerView.stopCamera();
	}

	@Override
	public void handleResult(final Result result) {
		final Context context = this;

		final ProgressDialog dialog = ProgressDialog.show(context, "Please wait...", "Searching for item", true);
		dialog.setCancelable(false);

		final RequestQueue queue = Volley.newRequestQueue(context);

		new Thread(new Runnable() {
			@Override
			public void run() {
				Log.d(getString(R.string.app_name), "Thread started");
				String url = "http://search.mobile.walmart.com/search?query=" + result.getText() + "&store=2858";

				JsonObjectRequest request = new JsonObjectRequest(
						Request.Method.GET,
						url,
						null,
						new Response.Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {

								boolean foundItem = false;
								Intent toDetails = new Intent(context, ItemDetailsActivity.class);

								Log.d(getString(R.string.app_name), "Response received");
								try {
									Log.d(getString(R.string.app_name), "Parsing response...");

									JSONArray results = response.getJSONArray("results");

									if (results.length() == 0) {
										final AlertDialog.Builder notFoundDialog = new AlertDialog.Builder(context);
										notFoundDialog.setTitle("Item not found")
												.setMessage("Sorry, this item was not found in the Walmart API. Would you like to add it manually?")
												.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
													@Override
													public void onClick(DialogInterface dialogInterface, int i) {
														startActivity(new Intent(context, AddItemActivity.class));
													}
												})
												.setPositiveButton("Enter Manually", new DialogInterface.OnClickListener() {
													@Override
													public void onClick(DialogInterface dialogInterface, int i) {
														startActivity(new Intent(context, ItemDetailsActivity.class));
													}
												})
												.show();
									}
									else {

										JSONObject item = results.getJSONObject(0);

										double price = (double) item.getJSONObject("price").getInt("priceInCents");
										price = price / 100;

										foundItem = true;

										String itemImage = item.getJSONObject("images").getString("largeUrl");

										toDetails.putExtra("scanned_item_name", item.getString("name"));
										toDetails.putExtra("scanned_item_price", price);
										toDetails.putExtra("scanned_item_image", itemImage);
									}

								} catch (JSONException e) {
									Log.e(getString(R.string.app_name), "Response parsing failed. " + e.getMessage());
									Toast.makeText(context, "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
								}
								finally {
									dialog.dismiss();
								}

								if (foundItem) {
									startActivity(toDetails);
								}

							}

						}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								Log.e(getString(R.string.app_name), "Response Error: " + error.toString());
								Toast.makeText(context, "Error calling Walmart servers", Toast.LENGTH_LONG).show();
								startActivity(new Intent(context, AddItemActivity.class));
							}

						}

				);

				queue.add(request);
				Log.d(getString(R.string.app_name), "Request sent");
			}
		}).start();
	}
}
