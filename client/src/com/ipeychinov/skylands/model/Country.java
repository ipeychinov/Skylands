package com.ipeychinov.skylands.model;

public class Country {

	private long id;
	private long userId;
	private String username;
	private String name;
	private String anthem;
	private String userrole;
	private String political_system;
	private String religion;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAnthem() {
		return anthem;
	}

	public void setAnthem(String anthem) {
		this.anthem = anthem;
	}

	public String getUserRole() {
		return userrole;
	}

	public void setUserRole(String userRole) {
		this.userrole = userRole;
	}

	public String getPoliticalSystem() {
		return political_system;
	}

	public void setPoilticalSystem(String politicalSystem) {
		this.political_system = politicalSystem;
	}

	public String getReligion() {
		return religion;
	}

	public void setReligion(String religion) {
		this.religion = religion;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}
