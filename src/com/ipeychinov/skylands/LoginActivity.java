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

public class LoginActivity extends Activity {

	private AsyncService service;

	private EditText usernameField;
	private EditText passwordField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		init();
	}

	private void init() {
		this.service = new AsyncService(this);
		
		this.usernameField = (EditText) findViewById(R.id.username_login);
		this.passwordField = (EditText) findViewById(R.id.password_login);
	}

	public void onClickLogin(View v) {
		final String params = createRequest();

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
							startSkylands();
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

	public void onClickRegister(View v) {
		startRegister();
	}

	protected void startSkylands() {
		finish();
		Intent intent = new Intent(this, SkylandsActivity.class);
		startActivity(intent);
	}

	private void startRegister() {
		finish();
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}

	private String createRequest() {
		Map<String, String> paramMap = new HashMap<String, String>(3);
		paramMap.put("id", "1");

		String username = this.usernameField.getText().toString();
		paramMap.put("username", username);
		String password = this.passwordField.getText().toString();
		paramMap.put("password", password);

		Gson gson = new GsonBuilder().create();

		String params = gson.toJson(paramMap);
		return params;
	}

	protected void notifyError(String error) {
		Toast.makeText(this, error, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onBackPressed() {
		finish();
		System.exit(0);
	}

}
