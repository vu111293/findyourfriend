package com.sgu.findyourfriend.model;

import java.util.Date;

public class Message {
	/**
	 * The content of the message
	 */
	private long id;
	private String message;
	private int idSender;
	private String senderName;
	private int idReceiver;
	private String receiverName;
	
	private Date smsTime;
	private boolean isMine;

	/**
	 * boolean to determine, whether the message is a status message or not. it
	 * reflects the changes/updates about the sender is writing, have entered
	 * text etc
	 */
	boolean isStatusMessage;


	public Message(String message, boolean isMine, int idSender, String senderName,
			int idReceiver, String receiverName, Date smsTime) {
		this.setMessage(message);
		this.setMine(isMine);
		this.setIdSender(idSender);
		this.setIdReceiver(idReceiver);
		this.setSenderName(senderName);
		this.setReceiverName(receiverName);
		this.setSmsTime(smsTime);
	}

	public Message(long id, String message, boolean isMine, int idSender, String senderName,
			int idReceiver, String receiverName, Date smsTime) {
		this.setId(id);
		this.setMessage(message);
		this.setMine(isMine);
		this.setIdSender(idSender);
		this.setIdReceiver(idReceiver);
		this.setSenderName(senderName);
		this.setReceiverName(receiverName);
		this.setSmsTime(smsTime);
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

	public int getIdSender() {
		return idSender;
	}

	public void setIdSender(int idSender) {
		this.idSender = idSender;
	}

	public int getIdReceiver() {
		return idReceiver;
	}

	public void setIdReceiver(int idReceiver) {
		this.idReceiver = idReceiver;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

}
