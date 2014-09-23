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
