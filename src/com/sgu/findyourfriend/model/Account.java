package com.sgu.findyourfriend.model;

public class Account {

	// attributes
	private String number;
	private String password;

	// methods
	public Account(String number, String password) {
		this.setNumber(number);
		this.setPassword(password);
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getNumber() {
		return this.number;
	}

	public void setPassword(String passowrd) {
		this.password = passowrd;
	}

	public String getPassword() {
		return this.password;
	}
}
