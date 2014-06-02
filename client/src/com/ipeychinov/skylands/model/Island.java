package com.ipeychinov.skylands.model;

import java.util.List;

public class Island {

	private long id;
	private Resources resources;
	private List<Building> buildings;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Resources getResources() {
		return resources;
	}

	public void setResources(Resources resources) {
		this.resources = resources;
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	public void setBuildings(List<Building> buildings) {
		this.buildings = buildings;
	}

}
