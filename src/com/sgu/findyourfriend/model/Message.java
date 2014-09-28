/*
 * Copyright (C) 2014 Tubor Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sgu.findyourfriend.model;

import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

public class Message {
	

	private static String PATTERN_LOCATION = "Gần ";
	private static String PATTERN_NO_LOCATION = "Vị trí không được xác định.";
	
	private long id;
	private String message;
	private int idSender;
	private String senderName;
	private int idReceiver;
	private String receiverName;
	private LatLng location;
	
	private Date smsTime;
	private boolean isMine;

	/**
	 * boolean to determine, whether the message is a status message or not. it
	 * reflects the changes/updates about the sender is writing, have entered
	 * text etc
	 */
	boolean isStatusMessage;


	public Message(String message, boolean isMine, int idSender, String senderName,
			int idReceiver, String receiverName, LatLng location, Date smsTime) {
		this.setMessage(message);
		this.setMine(isMine);
		this.setIdSender(idSender);
		this.setIdReceiver(idReceiver);
		this.setSenderName(senderName);
		this.setReceiverName(receiverName);
		this.setLocation(location);
		this.setSmsTime(smsTime);
	}

	public Message(long id, String message, boolean isMine, int idSender, String senderName,
			int idReceiver, String receiverName, LatLng location, Date smsTime) {
		this.setId(id);
		this.setMessage(message);
		this.setMine(isMine);
		this.setIdSender(idSender);
		this.setIdReceiver(idReceiver);
		this.setSenderName(senderName);
		this.setReceiverName(receiverName);
		this.setLocation(location);
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

	public String getExpendMsg() {
		if (null != location) 
			return PATTERN_LOCATION + "lat:" + location.latitude + ", lng:" + location.longitude;
		return PATTERN_NO_LOCATION;
	}

	public LatLng getLocation() {
		return location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}

}
