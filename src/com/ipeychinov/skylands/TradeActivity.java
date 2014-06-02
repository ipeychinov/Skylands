package com.ipeychinov.skylands;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ipeychinov.skylands.model.Response;
import com.ipeychinov.skylands.model.ResponseError;
import com.ipeychinov.skylands.service.AsyncService;
import com.ipeychinov.skylands.service.AsyncServiceCallback;

public class TradeActivity extends Activity {

	public static final String RECIEVER_ID = "reciever";

	private AsyncService service;

	private EditText offerField;
	private EditText demandWhitecloud;
	private EditText demandThundercloud;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_trade);

		init();
	}

	private void init() {
		this.service = new AsyncService(this);

		this.offerField = (EditText) findViewById(R.id.offer_field);
		this.demandWhitecloud = (EditText) findViewById(R.id.demand_whitecloud);
		this.demandThundercloud = (EditText) findViewById(R.id.demand_thundercloud);
	}

	public void onClickSendRequest(View v) {
		final String params = createRequest();

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

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

	protected void notifyError(String error) {
		Toast.makeText(this, error, Toast.LENGTH_LONG).show();
	}

	protected void startSkylandsActivity() {
		finish();

		Intent intent = new Intent(this, SkylandsActivity.class);
		startActivity(intent);
	}

	private String createRequest() {
		Map<String, String> paramMap = new HashMap<String, String>(5);
		paramMap.put("id", "11");

		Intent intent = getIntent();

		paramMap.put("reciever_id", intent.getStringExtra(RECIEVER_ID));

		String offer = this.offerField.getText().toString();
		paramMap.put("offer", offer);

		String demandWhitecloud = this.demandWhitecloud.getText().toString();
		paramMap.put("demand_whitecloud", demandWhitecloud);

		String demandThundercloud = this.demandThundercloud.getText()
				.toString();
		paramMap.put("demand_thundercloud", demandThundercloud);

		Gson gson = new GsonBuilder().create();

		String params = gson.toJson(paramMap);
		return params;
	}

	@Override
	public void onBackPressed() {
		startSkylandsActivity();
	}

}
