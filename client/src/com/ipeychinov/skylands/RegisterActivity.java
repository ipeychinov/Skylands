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

public class RegisterActivity extends Activity {

	private AsyncService service;

	private EditText usernameField;
	private EditText passwordField;
	private EditText confirmPasswordField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);

		init();
	}

	private void init() {
		this.service = new AsyncService(this);

		this.usernameField = (EditText) findViewById(R.id.username_register);
		this.passwordField = (EditText) findViewById(R.id.password_register);
		this.confirmPasswordField = (EditText) findViewById(R.id.password_confirm);
	}

	public void onClickDoRegister(View v) {
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
							startSetupCountry();
						} else {
							notifyError();
						}
					}

					@Override
					public void onError(String message) {
						notifyError();
					}

				});

			}

		});
		thread.run();
	}

	protected void notifyError() {
		Toast.makeText(this, "Registration failed", Toast.LENGTH_LONG).show();
	}

	protected void startSetupCountry() {
		finish();
		Intent intent = new Intent(this, MountainActivity.class);
		startActivity(intent);
	}

	private String createRequest() {
		Map<String, String> paramMap = new HashMap<String, String>(3);
		paramMap.put("id", "8");

		String username = this.usernameField.getText().toString();
		paramMap.put("username", username);
		String password = this.passwordField.getText().toString();
		String confirm = this.confirmPasswordField.getText().toString();
		if (password.equals(confirm)) {
			paramMap.put("password", password);
		} else {
			notifyError();
		}

		Gson gson = new GsonBuilder().create();

		String params = gson.toJson(paramMap);
		return params;
	}

	@Override
	public void onBackPressed() {
		startLoginActivity();
	}

	private void startLoginActivity() {
		finish();
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
}
