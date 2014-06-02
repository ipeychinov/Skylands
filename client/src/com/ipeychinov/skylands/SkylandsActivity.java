package com.ipeychinov.skylands;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ipeychinov.skylands.model.Island;
import com.ipeychinov.skylands.model.Resources;
import com.ipeychinov.skylands.service.AsyncService;
import com.ipeychinov.skylands.service.AsyncServiceCallback;

public class SkylandsActivity extends Activity implements OnClickListener {

	private AsyncService service;

	private TextView whitecloudEssenceText;
	private TextView thundercloudEssenceText;
	private TextView goldText;

	private ImageView mountainImg;
	private ImageView quarryImg;
	private ImageView villageImg;
	private ImageView towerImg;
	private ImageView harborImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_skylands);

		init();
		loadData();
	}

	private void init() {
		this.whitecloudEssenceText = (TextView) findViewById(R.id.res_whitecloud);
		this.thundercloudEssenceText = (TextView) findViewById(R.id.res_thundercloud);
		this.goldText = (TextView) findViewById(R.id.res_gold);

		this.service = new AsyncService(this);

		this.mountainImg = (ImageView) findViewById(R.id.mountain_img);
		this.mountainImg.setOnClickListener(this);
		this.quarryImg = (ImageView) findViewById(R.id.quarry_img);
		this.quarryImg.setOnClickListener(this);
		this.villageImg = (ImageView) findViewById(R.id.village_img);
		this.villageImg.setOnClickListener(this);
		this.towerImg = (ImageView) findViewById(R.id.tower_img);
		this.towerImg.setOnClickListener(this);
		this.harborImg = (ImageView) findViewById(R.id.harbor_img);
		this.harborImg.setOnClickListener(this);

	}

	protected void startHarborActivity() {
		finish();

		Intent intent = new Intent(this, HarborActivity.class);
		startActivity(intent);
	}

	protected void startVillageActivity() {
		finish();

		Intent intent = new Intent(this, VillageActivity.class);
		startActivity(intent);
	}

	protected void startTowerActivity() {
		finish();

		Intent intent = new Intent(this, TowerActivity.class);
		startActivity(intent);
	}

	protected void startQuarryActivity() {
		finish();

		Intent intent = new Intent(this, QuarryActivity.class);
		startActivity(intent);
	}

	protected void startMountainActivity() {
		finish();

		Intent intent = new Intent(this, MountainActivity.class);
		startActivity(intent);
	}

	private void loadData() {
		Map<String, String> paramMap = new HashMap<String, String>(1);
		paramMap.put("id", "3");

		Gson gson = new GsonBuilder().create();
		final String request = gson.toJson(paramMap);

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				service.doRequest(request, new AsyncServiceCallback() {

					@Override
					public void onResult(String content) {
						update(content);
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

	protected void update(String content) {
		Island response = parseResponse(content);
		Resources resources = response.getResources();

		int whitecloudEssence = resources.getWhitecloudEssence();
		String whitecloudString = String.valueOf(whitecloudEssence);
		this.whitecloudEssenceText.setText(whitecloudString);

		int thundercloudEssence = resources.getThundercloudEssence();
		String thundercloudString = String.valueOf(thundercloudEssence);
		this.thundercloudEssenceText.setText(thundercloudString);

		int gold = resources.getGold();
		String goldString = String.valueOf(gold);
		this.goldText.setText(goldString);

	}

	private Island parseResponse(String content) {
		Gson gson = new GsonBuilder().create();
		Island response = gson.fromJson(content, Island.class);
		return response;
	}

	public void onClickLogout(View v) {

		Map<String, String> paramMap = new HashMap<String, String>(1);
		paramMap.put("id", "2");

		Gson gson = new GsonBuilder().create();
		String request = gson.toJson(paramMap);

		service.doRequest(request, new AsyncServiceCallback() {

			@Override
			public void onResult(String content) {
				startLoginActivity();
			}

			@Override
			public void onError(String message) {
				notifyError("Failed to logout");
			}

		});
	}

	public void onClickLoadMap(View v) {
		startMapActivity();
	}

	private void startMapActivity() {
		finish();

		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}

	protected void startLoginActivity() {
		finish();

		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		View v = new View(this);
		onClickLogout(v);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mountain_img:
			startMountainActivity();
			break;
		case R.id.quarry_img:
			startQuarryActivity();
			break;
		case R.id.village_img:
			startVillageActivity();
			break;
		case R.id.tower_img:
			startTowerActivity();
			break;
		case R.id.harbor_img:
			startHarborActivity();
			break;
		}
	}

}
