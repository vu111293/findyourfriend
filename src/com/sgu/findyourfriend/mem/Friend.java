package com.sgu.findyourfriend.mem;

public class Friend {
	private String name;
	private String email;
	private String phoneNumber;
	private String regId;
	private String avatarLink;
	private boolean isAvailable;
	
	public Friend() {
	}
	
	public Friend(String name, String email, String regId) {
		this.setName(name);
		this.setEmail(email);
		this.setRegId(regId);
		this.isAvailable = false;
	}

	public Friend(String name, String email, String phoneNumber, String regId, boolean available) {
		this.setName(name);
		this.setEmail(email);
		this.setPhoneNumber(phoneNumber);
		this.setRegId(regId);
		this.setAvailable(available);
	}
	
	public Friend(String name, String email, String phoneNumber, 
			String avatarLink, String regId, boolean available) {
		this.setName(name);
		this.setEmail(email);
		this.setPhoneNumber(phoneNumber);
		this.setAvatarLink(avatarLink);
		this.setRegId(regId);
		this.setAvailable(available);
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
	
}
