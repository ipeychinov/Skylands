package com.ipeychinov.skylands.model;

public class Research {

	private long id;
	private String name;
	private int thundercloud_essence;
	private boolean isCompleted;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getResearchResource() {
		return thundercloud_essence;
	}

	public void setResearchResource(int researchResource) {
		this.thundercloud_essence = researchResource;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompletion(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
}
