package com.sgu.findyourfriend.model;

import java.util.Date;

public class TempMessage {
	private long id;
	private String message;
	private Date smsTime;
	
	public TempMessage(String message, Date smsTime) {
		this.message = message;
		this.smsTime = smsTime;
	}
	
	public TempMessage(long id, String message, Date smsTime) {
		this.id = id;
		this.message = message;
		this.smsTime = smsTime;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getSmsTime() {
		return smsTime;
	}
	public void setSmsTime(Date smsTime) {
		this.smsTime = smsTime;
	}
}
