package com.ipeychinov.skylands.model;

public class Building {

	private long id;
	private int currentLevel;
	private int maxLevel;
	private boolean inProgress;
	private String endTime;
	private int nextLevelEssence;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getNextLevelResource() {
		return nextLevelEssence;
	}

	public void setNextLevelResource(int nextLevelResource) {
		this.nextLevelEssence = nextLevelResource;
	}

}
