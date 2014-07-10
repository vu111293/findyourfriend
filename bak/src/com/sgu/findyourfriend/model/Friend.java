package com.sgu.findyourfriend.model;


public class Friend {

	// attribute
	private User userInfo;
	private String numberLogin;
	private int share;

	// methods
	public Friend(User user, String numberLogin, int share) {
		this.setUserInfo(user);
		this.setShare(share);
		this.setNumberLogin(numberLogin);
	}

	public void setUserInfo(User user) {
		this.userInfo = user;
	}

	public User getUserInfo() {
		return this.userInfo;
	}

	public void setShare(int share) {
		this.share = share;
	}

	public int getShare() {
		return this.share;
	}
	
	public String getNumberLogin() {
		return numberLogin;
	}

	public void setNumberLogin(String numberLogin) {
		this.numberLogin = numberLogin;
	}
}
