package com.sgu.findyourfriend.model;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class FriendBak {
	private String name;
	private String email;
	private String phoneNumber;
	private String regId;
	private String avatarLink;
	private boolean isAvailable;
	private boolean isAccepted;
	private boolean isShare;
	private LatLng lastLocation;
	private List<StepHistory> steps;
	
	public FriendBak() {
	}
	
	public FriendBak(String name, String email, String regId) {
		this.setName(name);
		this.setEmail(email);
		this.setRegId(regId);
		this.isAvailable = false;
		setSteps(new ArrayList<StepHistory>());
	}

	public FriendBak(String name, String email, String phoneNumber, String regId, boolean available) {
		this.setName(name);
		this.setEmail(email);
		this.setPhoneNumber(phoneNumber);
		this.setRegId(regId);
		this.setAvailable(available);
		setSteps(new ArrayList<StepHistory>());
	}
	
	public FriendBak(String name, String email, String phoneNumber, 
			String avatarLink, String regId, boolean available) {
		this.setName(name);
		this.setEmail(email);
		this.setPhoneNumber(phoneNumber);
		this.setAvatarLink(avatarLink);
		this.setRegId(regId);
		this.setAvailable(available);
		setSteps(new ArrayList<StepHistory>());
	}
	
	public void setAvatarLink(String link) {
		this.avatarLink = link;
	}

	public String getAvatarLink() {
		return avatarLink;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
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

	
	public void addStep(StepHistory step) {
		steps.add(step);
	}
	
	public List<StepHistory> getSteps() {
		return steps;
	}

	public void setSteps(List<StepHistory> steps) {
		this.steps = steps;
	}
	
}
