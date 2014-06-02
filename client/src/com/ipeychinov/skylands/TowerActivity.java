package com.ipeychinov.skylands;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ipeychinov.skylands.model.Building;
import com.ipeychinov.skylands.model.Island;
import com.ipeychinov.skylands.model.Research;
import com.ipeychinov.skylands.model.Resources;
import com.ipeychinov.skylands.model.Response;
import com.ipeychinov.skylands.model.ResponseError;
import com.ipeychinov.skylands.service.AsyncService;
import com.ipeychinov.skylands.service.AsyncServiceCallback;

public class TowerActivity extends ListActivity {

	private AsyncService service;

	private TextView whitecloudEssenceText;
	private TextView thundercloudEssenceText;
	private TextView goldText;

	private Button upgradeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tower);

		init();
		loadData();
		loadResearchData();
	}

	private void init() {
		this.service = new AsyncService(this);

		this.whitecloudEssenceText = (TextView) findViewById(R.id.res_whitecloud);
		this.thundercloudEssenceText = (TextView) findViewById(R.id.res_thundercloud);
		this.goldText = (TextView) findViewById(R.id.res_gold);

		this.upgradeButton = (Button) findViewById(R.id.upgrade_button);

		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> list, View v, int position,
					long id) {
				Research research = (Research) list.getItemAtPosition(position);

				if (!research.isCompleted()) {
					int researchId = (int) research.getId();

					upgradeResearch(researchId);
				}

			}

		});
	}

	protected void upgradeResearch(int researchId) {
		Map<String, String> paramMap = new HashMap<String, String>(2);
		paramMap.put("id", "5");

		String researchIdText = String.valueOf(researchId);
		paramMap.put("research_id", researchIdText);

		Gson gson = new GsonBuilder().create();
		final String params = gson.toJson(paramMap);

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				service.doRequest(params, new AsyncServiceCallback() {

					@Override
					public void onResult(String content) {
						Gson gson = new GsonBuilder().create();
						Response response = gson.fromJson(content,
								Response.class);
						ResponseError error = response.getError();
						if (error == null) {
							startSkylandsActivity();
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
	}

	protected void startSkylandsActivity() {
		finish();

		Intent intent = new Intent(this, SkylandsActivity.class);
		startActivity(intent);
	}

	private void loadResearchData() {
		Map<String, String> paramMap = new HashMap<String, String>(1);
		paramMap.put("id", "10");

		Gson gson = new GsonBuilder().create();
		final String params = gson.toJson(paramMap);

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				service.doRequest(params, new AsyncServiceCallback() {

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

	private void loadData() {
		Map<String, String> paramMap = new HashMap<String, String>(2);
		paramMap.put("id", "14");
		paramMap.put("building_id", "3");

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

	protected void update(String content) {
		List<Research> researchList = parseResponse(content);

		ArrayAdapter<Research> researchAdapter = new ArrayAdapter<Research>(
				this, R.layout.activity_tower, R.id.research_name, researchList) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = getLayoutInflater().inflate(
							R.layout.list_item_research, null);
				}

				Research research = getItem(position);

				TextView researchNameText = (TextView) convertView
						.findViewById(R.id.research_name);
				researchNameText.setText(research.getName());

				TextView researchCostText = (TextView) convertView
						.findViewById(R.id.research_cost);

				if (!research.isCompleted()) {
					String researchCost = String.valueOf(research
							.getResearchResource());
					researchCostText.setText(researchCost);
				} else {
					TextView researchCostLabel = (TextView) convertView
							.findViewById(R.id.research_cost_label);
					researchCostLabel.setText("Completed");
				}

				return convertView;
			}
		};
		setListAdapter(researchAdapter);
	}

	private List<Research> parseResponse(String content) {
		Gson gson = new GsonBuilder().create();
		Type listType = new TypeToken<ArrayList<Research>>() {
		}.getType();
		List<Research> response = gson.fromJson(content, listType);
		return response;
	}

	public void onClickUpgrade(View v) {
		Map<String, String> paramMap = new HashMap<String, String>(2);
		paramMap.put("id", "4");
		paramMap.put("building_id", "3");

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
							Toast.makeText(TowerActivity.this,
									"The building has been upgraded",
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
	}

	@Override
	public void onBackPressed() {
		startSkylandsActivity();
	}

}
