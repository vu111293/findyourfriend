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

public class FriendRequest {
	
	private int id;
	private String name;
	private String imgProfile;
	private boolean isNotNow; 

	public FriendRequest(int id, String name, String imgProfile, boolean isNotNow) {
		this.setId(id);
		this.setName(name);
		this.setImgProfile(imgProfile);
		this.setNotNow(isNotNow);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImgProfile() {
		return imgProfile;
	}

	public void setImgProfile(String imgProfile) {
		this.imgProfile = imgProfile;
	}

	public boolean isNotNow() {
		return isNotNow;
	}

	public void setNotNow(boolean isNotNow) {
		this.isNotNow = isNotNow;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
