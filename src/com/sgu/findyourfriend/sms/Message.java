package com.sgu.findyourfriend.sms;

import java.util.Date;

public class Message {
	/**
	 * The content of the message
	 */
	private long id;
	private String message;
	private String gcmIdSender;
	private String gcmIdReceiver;
	private Date smsTime;
	
	/**
	 * boolean to determine, who is sender of this message
	 */
	boolean isMine;
	
	/**
	 * boolean to determine, whether the message is a status message or not. it
	 * reflects the changes/updates about the sender is writing, have entered
	 * text etc
	 */
	boolean isStatusMessage;

	public Message(String message, boolean isMine) {
		super();
		this.message = message;
		this.isMine = isMine;
		this.isStatusMessage = false;
	}

	public Message(String message, boolean isMine, String gcmIdSender, 
			String gcmIdReceiver, Date smsTime) {
		super();
		this.message = message;
		this.isMine = isMine;
		this.isStatusMessage = false;
		this.setGcmIdSender(gcmIdSender);
		this.setGcmIdReceiver(gcmIdReceiver);
		this.setSmsTime(smsTime);
	}	
	
	public Message(long id, String message, boolean isMine, String gcmIdSender, 
			String gcmIdReceiver, Date smsTime) {
		super();
		this.id  = id;
		this.message = message;
		this.isMine = isMine;
		this.isStatusMessage = false;
		this.setGcmIdSender(gcmIdSender);
		this.setGcmIdReceiver(gcmIdReceiver);
		this.setSmsTime(smsTime);
	}	
	
	/**
	 * Constructor to make a status Message object consider the parameters are
	 * swaped from default Message constructor, not a good approach but have to
	 * go with it.
	 */
	public Message(boolean status, String message) {
		super();
		this.message = message;
		this.isMine = false;
		this.isStatusMessage = status;
	}

	public Message(String message, boolean isMine, boolean isStatusMessage) {
		this.message = message;
		this.isMine = isMine;
		this.isStatusMessage = isStatusMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isMine() {
		return isMine;
	}

	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}

	public boolean isStatusMessage() {
		return isStatusMessage;
	}

	public void setStatusMessage(boolean isStatusMessage) {
		this.isStatusMessage = isStatusMessage;
	}

	public String getGcmIdSender() {
		return gcmIdSender;
	}

	public void setGcmIdSender(String gcmIdSender) {
		this.gcmIdSender = gcmIdSender;
	}

	public String getGcmIdReceiver() {
		return gcmIdReceiver;
	}

	public void setGcmIdReceiver(String gcmIdReceiver) {
		this.gcmIdReceiver = gcmIdReceiver;
	}

	public Date getSmsTime() {
		return smsTime;
	}

	public void setSmsTime(Date smsTime) {
		this.smsTime = smsTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
