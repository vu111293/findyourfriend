package com.sgu.findyourfriend.model;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;


public class Friend {

	// attribute
	private User userInfo;
	private ArrayList<String> numberLogin;
	private boolean isAvailable;
	private boolean isAccepted;
	private boolean isShare;
	
	private int accurency = 15; // not available
	private LatLng lastLocation;
	private ArrayList<History> steps;

	public Friend(User userInfo, ArrayList<String> numberLogin, boolean isAvailable,
			boolean isAccepted, boolean isShare, LatLng lastLocation,
			ArrayList<History> steps) {
		super();
		this.userInfo = userInfo;
		this.numberLogin = numberLogin;
		this.isAvailable = isAvailable;
		this.isAccepted = isAccepted;
		this.isShare = isShare;
		this.lastLocation = lastLocation;
		this.steps = steps;
	}

	public User getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(User userInfo) {
		this.userInfo = userInfo;
	}

	public ArrayList<String> getNumberLogin() {
		return numberLogin;
	}

	public void setNumberLogin(ArrayList<String> numberLogin) {
		this.numberLogin = numberLogin;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public boolean isAccepted() {
		return isAccepted;
	}

	public void setAccepted(boolean isAccepted) {
		this.isAccepted = isAccepted;
	}

	public boolean isShare() {
		return isShare;
	}

	public void setShare(boolean isShare) {
		this.isShare = isShare;
	}

	public LatLng getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(LatLng lastLocation) {
		this.lastLocation = lastLocation;
	}

	public ArrayList<History> getSteps() {
		return steps;
	}

	public void setSteps(ArrayList<History> steps) {
		this.steps = steps;
	}

	public int getAccurency() {
		return accurency;
	}
	
	public void setAccurency(int accurency) {
		this.accurency = accurency;
	}
}
