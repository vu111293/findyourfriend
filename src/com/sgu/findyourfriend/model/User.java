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

import java.sql.Date;
import java.sql.Timestamp;

public class User {

	// attributes
	private int id;
	private String name;
	private String avatar;
	private String internetImageLink;
	private String gcmId;
	private Timestamp lastLogin;
	private int gender;
	private String address;
	private Date birthday;
	private String school;
	private String workplace;
	private String email;
	private String fblink;
	private boolean isPublic;

	public User(int id) {
		super();
		this.id = id;
		this.name = "";
		this.avatar = "";
		this.gcmId = "";
		this.lastLogin = null;
		this.gender = 0;
		this.address = "";
		this.birthday = new Date(System.currentTimeMillis());
		this.school = "";
		this.workplace = "";
		this.email = "";
		this.fblink = "";
		this.isPublic = false;
	}
	
	public User(int id, String name, String avatar, String gcmId,
			Timestamp lastLogin, int gender, String address, Date birthday,
			String school, String workplace, String email, String fblink,
			boolean isPublic) {
		super();
		this.id = id;
		this.name = name;
		this.avatar = avatar;
		this.gcmId = gcmId;
		this.lastLogin = lastLogin == null ? new Timestamp(System.currentTimeMillis()) : lastLogin;
		this.gender = gender;
		this.address = address;
		this.birthday = birthday == null ? new Date(System.currentTimeMillis()) : birthday;
		this.school = school;
		this.workplace = workplace;
		this.email = email;
		this.fblink = fblink;
		this.isPublic = isPublic;
	}
	
	public User(User u) {
		this.id = u.id;
		this.name = u.name;
		this.avatar = u.avatar;
		this.gcmId = u.gcmId;
		this.lastLogin = u.lastLogin;
		this.gender = u.gender;
		this.address = u.address;
		this.birthday = u.birthday; 
		this.school = u.school;
		this.workplace = u.workplace;
		this.email = u.email;
		this.fblink = u.fblink;
		this.isPublic = u.isPublic;
	}

	public Timestamp getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Timestamp lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getWorkplace() {
		return workplace;
	}

	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}

	public String getFblink() {
		return fblink;
	}

	public void setFblink(String fblink) {
		this.fblink = fblink;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public void setGcmId(String gcmId) {
		this.gcmId = gcmId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getGender() {
		return this.gender;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return this.email;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getAvatar() {
		return this.avatar;
	}

	public String getGcmId() {
		return gcmId;
	}

	public String getInternetImageLink() {
		return internetImageLink;
	}

	public void setInternetImageLink(String internetImageLink) {
		this.internetImageLink = internetImageLink;
	}

	@Override
	public String toString() {
		return id + " # " + name + " # " + address + " # " + email + " # " + gcmId ;
	}
	
	public User clone() {
		return new User(this);
	}
	
}