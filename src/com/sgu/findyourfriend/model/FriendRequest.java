package com.sgu.findyourfriend.model;

public class FriendRequest {
	
	private int id;
	private String name;
	private String imgProfile;
	private boolean isNotNow; 

	public FriendRequest(int id, String name, String imgProfile, boolean isNotNow) {
		this.setId(id);
		this.setName(name);
		this.setImgProfile(imgProfile);
		this.setNotNow(isNotNow);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImgProfile() {
		return imgProfile;
	}

	public void setImgProfile(String imgProfile) {
		this.imgProfile = imgProfile;
	}

	public boolean isNotNow() {
		return isNotNow;
	}

	public void setNotNow(boolean isNotNow) {
		this.isNotNow = isNotNow;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
