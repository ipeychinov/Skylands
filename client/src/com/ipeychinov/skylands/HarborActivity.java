package com.ipeychinov.skylands;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
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
import com.ipeychinov.skylands.model.Resources;
import com.ipeychinov.skylands.model.Response;
import com.ipeychinov.skylands.model.ResponseError;
import com.ipeychinov.skylands.model.TradeRequest;
import com.ipeychinov.skylands.service.AsyncService;
import com.ipeychinov.skylands.service.AsyncServiceCallback;

public class HarborActivity extends ListActivity {

	private AsyncService service;

	private TextView goldText;
	private TextView whitecloudEssenceText;
	private TextView thundercloudEssenceText;

	private Button upgradeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_harbor);

		init();
		loadData();
		loadRequests();
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
				final TradeRequest tradeRequest = (TradeRequest) list
						.getItemAtPosition(position);

				new AlertDialog.Builder(HarborActivity.this)
						.setTitle(R.string.answer_request_title)
						.setMessage(R.string.answer_request_label)
						.setPositiveButton(R.string.confirm_label,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										answerTradeRequest(
												tradeRequest.getId(), 1);
									}
								})
						.setNegativeButton(R.string.deny_label,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										answerTradeRequest(
												tradeRequest.getId(), 2);
									}
								}).show();
			}

		});
	}

	protected void answerTradeRequest(long requestId, int answer) {
		Map<String, String> paramMap = new HashMap<String, String>(2);
		paramMap.put("id", "12");

		String requestIdStr = String.valueOf(requestId);
		paramMap.put("request_id", requestIdStr);

		String answerStr = String.valueOf(answer);
		paramMap.put("answer", answerStr);

		Gson gson = new GsonBuilder().create();
		final String params = gson.toJson(paramMap);

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				service.doRequest(params, new AsyncServiceCallback() {

					@Override
					public void onResult(String content) {
						Response response = new Response();
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

	private void loadData() {
		Map<String, String> paramMap = new HashMap<String, String>(2);
		paramMap.put("id", "14");
		paramMap.put("building_id", "5");

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
								upgradeButton
										.setText("Upgrade in process");
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

	private void loadRequests() {
		Map<String, String> paramMap = new HashMap<String, String>(1);
		paramMap.put("id", "13");

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

	protected void notifyError(String error) {
		Toast.makeText(this, error, Toast.LENGTH_LONG).show();
	}

	protected void update(String content) {
		List<TradeRequest> requestList = parseResponse(content);

		ArrayAdapter<TradeRequest> requestAdapter = new ArrayAdapter<TradeRequest>(
				this, R.layout.activity_harbor, R.id.sender_name, requestList) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = getLayoutInflater().inflate(
							R.layout.list_item_request, null);
				}

				TradeRequest tr = getItem(position);

				TextView senderNameText = (TextView) convertView
						.findViewById(R.id.sender_name);
				senderNameText.setText(tr.getSenderName());

				TextView offerField = (TextView) convertView
						.findViewById(R.id.offer);
				String goldOffer = String.valueOf(tr.getOffer());
				offerField.setText(goldOffer);

				TextView demandWhitecloudText = (TextView) convertView
						.findViewById(R.id.demand_whitecloud);
				String whitecloudDemand = String.valueOf(tr
						.getWhitecloudEssence());
				demandWhitecloudText.setText(whitecloudDemand);

				TextView demandThundercloudText = (TextView) convertView
						.findViewById(R.id.demand_thundercloud);
				String thundercloudDemand = String.valueOf(tr
						.getThundercloudEssence());
				demandThundercloudText.setText(thundercloudDemand);

				return convertView;
			}
		};
		setListAdapter(requestAdapter);

	}

	private List<TradeRequest> parseResponse(String content) {
		Gson gson = new GsonBuilder().create();
		Type listType = new TypeToken<ArrayList<TradeRequest>>() {
		}.getType();
		List<TradeRequest> response = gson.fromJson(content, listType);
		return response;
	}

	public void onClickUpgrade(View v) {
		Map<String, String> paramMap = new HashMap<String, String>(2);
		paramMap.put("id", "4");
		paramMap.put("building_id", "5");

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
							Toast.makeText(HarborActivity.this,
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
