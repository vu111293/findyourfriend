/*
 * 	 This file is part of Find Your Friend.
 *
 *   Find Your Friend is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Find Your Friend is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Find Your Friend.  If not, see <http://www.gnu.org/licenses/>.
 */
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
