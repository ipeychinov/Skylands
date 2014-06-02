package com.ipeychinov.skylands.model;

public class TradeRequest {

	private long id;
	private long sender;
	private String senderName;
	private long reciever;
	private int offer;
	private int whitecloud;
	private int thundercloud;
	private int answer;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSenderId() {
		return sender;
	}

	public void setSenderId(long senderId) {
		this.sender = senderId;
	}

	public long getRecieverId() {
		return reciever;
	}

	public void setRecieverId(long recieverId) {
		this.reciever = recieverId;
	}

	public int getOffer() {
		return offer;
	}

	public void setOffer(int offer) {
		this.offer = offer;
	}

	public int getWhitecloudEssence() {
		return whitecloud;
	}

	public void setWhitecloudEssence(int whitecloudEssence) {
		this.whitecloud = whitecloudEssence;
	}

	public int getThundercloudEssence() {
		return thundercloud;
	}

	public void setThundercloudEssence(int thundercloudEssence) {
		this.thundercloud = thundercloudEssence;
	}

	public int getAnswer() {
		return answer;
	}

	public void setAnswer(int answer) {
		this.answer = answer;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

}
