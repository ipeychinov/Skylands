package com.ipeychinov.skylands.service;

public interface AsyncServiceCallback {
	
	void onResult(String content);
	
	void onError(String message);

}
