package com.ipeychinov.skylands.model;

public class Response {

	private long id;
	private ResponseError error;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ResponseError getError() {
		return error;
	}

	public void setError(ResponseError error) {
		this.error = error;
	}

}
