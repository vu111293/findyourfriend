package com.sgu.findyourfriend.model;

public class ContactBean {
	private String name;
	private String phoneNo;
	private boolean isCheck;

	public ContactBean() {
		// TODO Auto-generated constructor stub
	}

	public ContactBean(String name, String phoneNumber, boolean isCheck) {
		this.name = name;
		this.phoneNo = phoneNumber;
		this.setCheck(isCheck);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	// public boolean isUsed() {
	// return used;
	// }
	// public void setUsed(boolean used) {
	// this.used = used;
	// }

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

}
