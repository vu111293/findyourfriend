package com.sgu.findyourfriend.model;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;


public class Friend {

	public static final int REQUEST_FRIEND = 0;
	public static final int REQUESTED_FRIEND = 1;
	public static final int FRIEND_RELATIONSHIP = 2;
	public static final int REQUEST_SHARE = 3;
	public static final int REQUESTED_SHARE = 4;
	public static final int SHARE_RELATIONSHIP = 5;
	
//	public static final int SHARE_STATE = 3;
//	public static final int ACCEPT_STATE = 2;
//	public static final int WAIT_STATE = 1;
//	public static final int DONT_ACCEPT_STATE = 0;
	
	// attribute
	private User userInfo;
	private ArrayList<String> numberLogin;
	private boolean isAvailable;
	private int acceptState;
//	private boolean isShare;
	
	private int accurency = 15; // not available
	private LatLng lastLocation;
	private ArrayList<History> steps;

	public Friend(User userInfo, ArrayList<String> numberLogin, boolean isAvailable,
			int acceptState, LatLng lastLocation,
			ArrayList<History> steps) {
		super();
		this.userInfo = userInfo;
		this.numberLogin = numberLogin;
		this.isAvailable = isAvailable;
		this.acceptState = acceptState;
//		this.isShare = isShare;
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

	public int getAcceptState() {
		return acceptState;
	}

	public void setAcceptState(int acceptState) {
		this.acceptState = acceptState;
	}

//	public boolean isShare() {
//		return isShare;
//	}
//
//	public void setShare(boolean isShare) {
//		this.isShare = isShare;
//	}

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
