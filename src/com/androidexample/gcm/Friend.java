package com.androidexample.gcm;

public class Friend {
	private String name;
	private String email;
	private String regId;
	
	public Friend() {
	}
	
	public Friend(String name, String email, String regId) {
		this.setName(name);
		this.setEmail(email);
		this.setRegId(regId);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	
	
}
