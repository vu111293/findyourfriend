package com.sgu.findyourfriend.model;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;


public class FriendBak2 {

	// attribute
	private User userInfo;
	private String numberLogin;

	private boolean isAvailable;
	private boolean isAccepted;
	private boolean isShare;
	private LatLng lastLocation;
	private List<History> steps;
	
	// methods
	public FriendBak2(User user, String numberLogin, boolean avaiable, boolean accepted, boolean share, LatLng lastLocation) {
		this.setUserInfo(user);
		this.setShare(share);
		this.setNumberLogin(numberLogin);
		this.setAvailable(avaiable);
		this.setAccepted(accepted);
		this.setLastLocation(lastLocation);
		this.setSteps(null);
	}


	public void setUserInfo(User user) {
		this.userInfo = user;
	}

	public User getUserInfo() {
		return this.userInfo;
	}

	public String getNumberLogin() {
		return numberLogin;
	}

	public void setNumberLogin(String numberLogin) {
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

	
	public void addStep(History step) {
		steps.add(step);
	}
	
	public List<History> getSteps() {
		return steps;
	}

	public void setSteps(List<History> steps) {
		this.steps = steps;
	}
}
