package com.ipeychinov.skylands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ipeychinov.skylands.model.Building;
import com.ipeychinov.skylands.model.Island;
import com.ipeychinov.skylands.model.Resources;
import com.ipeychinov.skylands.model.Response;
import com.ipeychinov.skylands.model.ResponseError;
import com.ipeychinov.skylands.service.AsyncService;
import com.ipeychinov.skylands.service.AsyncServiceCallback;

public class VillageActivity extends Activity {

	private AsyncService service;

	private TextView goldText;
	private TextView whitecloudEssenceText;
	private TextView thundercloudEssenceText;

	private Button upgradeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_village);

		init();
		loadData();
	}

	private void init() {
		this.service = new AsyncService(this);

		this.whitecloudEssenceText = (TextView) findViewById(R.id.res_whitecloud);
		this.thundercloudEssenceText = (TextView) findViewById(R.id.res_thundercloud);
		this.goldText = (TextView) findViewById(R.id.res_gold);

		this.upgradeButton = (Button) findViewById(R.id.upgrade_button);
	}

	private void loadData() {
		Map<String, String> paramMap = new HashMap<String, String>(2);
		paramMap.put("id", "14");
		paramMap.put("building_id", "4");

		final Gson gson = new GsonBuilder().create();
		final String params = gson.toJson(paramMap);

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				service.doRequest(params, new AsyncServiceCallback() {

					@Override
					public void onResult(String content) {
						Island response = gson.fromJson(content, Island.class);
						Resources resources = response.getResources();
						List<Building> buildings = response.getBuildings();

						int whitecloudEssence = resources
								.getWhitecloudEssence();
						String whitecloudString = String
								.valueOf(whitecloudEssence);
						whitecloudEssenceText.setText(whitecloudString);

						int thundercloudEssence = resources
								.getThundercloudEssence();
						String thundercloudString = String
								.valueOf(thundercloudEssence);
						thundercloudEssenceText.setText(thundercloudString);

						int gold = resources.getGold();
						String goldString = String.valueOf(gold);
						goldText.setText(goldString);

						for (Building b : buildings) {
							if (!b.isInProgress()) {
								upgradeButton.setText("Upgrade ("
										+ String.valueOf(b
												.getNextLevelResource()) + ")");
							} else {
								upgradeButton.setText("Upgrade in process");
							}
						}
					}

					@Override
					public void onError(String message) {
						notifyError(message);
					}

				});
			}

		});
		thread.run();
	}

	protected void notifyError(String error) {
		Toast.makeText(this, error, Toast.LENGTH_LONG).show();
	}

	public void onClickUpgrade(View v) {
		Map<String, String> paramMap = new HashMap<String, String>(2);
		paramMap.put("id", "4");
		paramMap.put("building_id", "4");

		final Gson gson = new GsonBuilder().create();
		final String params = gson.toJson(paramMap);

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				service.doRequest(params, new AsyncServiceCallback() {

					@Override
					public void onResult(String content) {
						Response response = gson.fromJson(content,
								Response.class);
						ResponseError error = response.getError();
						if (error == null) {
							Toast.makeText(VillageActivity.this,
									"The building is being upgraded",
									Toast.LENGTH_LONG).show();

						} else {
							notifyError(error.getName());
						}
					}

					@Override
					public void onError(String message) {
						notifyError(message);
					}

				});
			}

		});
		thread.run();
		startSkylandsActivity();
	}

	@Override
	public void onBackPressed() {
		startSkylandsActivity();
	}

	private void startSkylandsActivity() {
		finish();
		Intent intent = new Intent(this, SkylandsActivity.class);
		startActivity(intent);
	}

}
