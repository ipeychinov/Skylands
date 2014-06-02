package com.ipeychinov.skylands;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ipeychinov.skylands.model.Country;
import com.ipeychinov.skylands.service.AsyncService;
import com.ipeychinov.skylands.service.AsyncServiceCallback;

public class OtherlandActivity extends Activity {

	public static final String LAND_COORDINATES_ID = "";

	private AsyncService service;

	private TextView nameText;
	private TextView anthemText;
	private TextView roleText;
	private TextView systemText;
	private TextView religionText;

	private long recieverId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_otherland);

		init();
		loadCountryData();
	}

	private void init() {
		this.service = new AsyncService(this);

		this.nameText = (TextView) findViewById(R.id.otherland_name);
		this.anthemText = (TextView) findViewById(R.id.otherland_anthem);
		this.roleText = (TextView) findViewById(R.id.otherland_role);
		this.systemText = (TextView) findViewById(R.id.otherland_system);
		this.religionText = (TextView) findViewById(R.id.otherland_religion);
	}

	private void loadCountryData() {
		Map<String, String> paramMap = new HashMap<String, String>(2);
		paramMap.put("id", "7");
		Intent intent = getIntent();
		paramMap.put("coordinates_id",
				intent.getStringExtra(LAND_COORDINATES_ID));

		Gson gson = new GsonBuilder().create();
		final String params = gson.toJson(paramMap);

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				service.doRequest(params, new AsyncServiceCallback() {

					@Override
					public void onResult(String content) {
						update(content);
					}

					@Override
					public void onError(String message) {
						notifyError("Failed to load country info");
					}

				});

			}

		});
		thread.run();
	}

	protected void notifyError(String error) {
		Toast.makeText(this, error, Toast.LENGTH_LONG).show();
	}

	protected void update(String content) {
		Country otherland = parseResponse(content);

		this.nameText.setText(otherland.getName());
		this.anthemText.setText(otherland.getAnthem());
		this.roleText.setText(otherland.getUserRole());
		this.systemText.setText(otherland.getPoliticalSystem());
		this.religionText.setText(otherland.getReligion());

		this.recieverId = otherland.getId();
	}

	private Country parseResponse(String content) {
		Gson gson = new GsonBuilder().create();
		Country response = gson.fromJson(content, Country.class);
		return response;
	}

	public void onClickTrade(View v) {
		startTradeActivity(this.recieverId);
	}

	private void startTradeActivity(long recieverId) {
		finish();
		Intent intent = new Intent(this, TradeActivity.class);
		String recieverIdStr = String.valueOf(recieverId);
		intent.putExtra(TradeActivity.RECIEVER_ID, recieverIdStr);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		startMapActivity();
	}

	private void startMapActivity() {
		finish();
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}
}
