package com.ipeychinov.skylands.service;

import org.apache.http.client.CookieStore;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class AsyncService {

	private static final String BASE_URL = "http://46.249.88.109/skylands.php";
	private static final String REQUEST_PARAM_NAME = "request";

	private AsyncHttpClient client;

	public AsyncService(Context context) {
		this.client = initClient(context);
	}

	private AsyncHttpClient initClient(Context context) {
		AsyncHttpClient client = new AsyncHttpClient();
		CookieStore store = new PersistentCookieStore(context);
		client.setCookieStore(store);
		
		return client;
	}

	public void doRequest(String request, final AsyncServiceCallback callback) {
		RequestParams params = new RequestParams(REQUEST_PARAM_NAME, request);
		this.client.post(BASE_URL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String content) {
				if (callback != null) {
					callback.onResult(content);
				}
			}

			@Override
			public void onFailure(Throwable e, String response) {
				if (callback != null) {
					String message = e.getMessage();
					callback.onError(message);
				}
			}
		});
	}
}
