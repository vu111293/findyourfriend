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
package com.sgu.findyourfriend.mgr;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.net.PostData;

public class MyProfileManager extends BaseManager {

	private static MyProfileManager instance;

	private Friend mineF;
	public Friend mTemp;

	public void init(Context context, int mId, boolean isForce) {
		super.init(context);

		if (isForce || !isLoaded())
			load(mId);
	}

	public synchronized static MyProfileManager getInstance() {
		if (instance == null) {
			instance = new MyProfileManager();
		}
		return instance;
	}

	public void load(int mId) {
		// load from server

		ArrayList<String> numLogins = new ArrayList<String>();

		if (Config.MODE_OFFLINE) {

			numLogins.add("123456789");
			numLogins.add("987654321");

			User mineOff = new User(38, "admin", "", "", new Timestamp(
					System.currentTimeMillis()), 1, "address", new Date(1993,
					10, 12), "saigon", "uni", "email@gmail.com", "", true);

			mineF = new Friend(mineOff, numLogins, true,
					Friend.SHARE_RELATIONSHIP, new LatLng(10.906605,
							107.2491654), null);

			return;
		}

		numLogins.addAll(PostData.accountGetNumbersById(context, mId));

		User mineUser = PostData.userGetMyProfile(context, mId);

		mineF = new Friend(mineUser, numLogins, true,
				Friend.SHARE_RELATIONSHIP, PostData.historyGetLastUserLocation(
						context, mId), null);
	}

	public void clear() {
		mineF = null;
	}

	@Override
	public void setup() {
		mineF.setAcceptState(Friend.SHARE_RELATIONSHIP);
	}

	public boolean isLoaded() {
		return null != mineF;
	}

	public Friend getMineInstance() {
		return mineF;
	}

	public int getMyID() {
		if (null == mineF || null == mineF.getUserInfo())
			return SettingManager.getInstance().getLastAccountIdLogin();
		return mineF.getUserInfo().getId();
	}

	public String getMyName() {
		return mineF.getUserInfo().getName();
	}

	public void addMyPhoneNumber(String num) {
		mineF.getNumberLogin().add(num);
	}

	public void removeMyPhoneNumber(String num) {
		mineF.getNumberLogin().remove(num);
	}

	public ArrayList<String> getMyPhoneNumberNotCurrent() {
		ArrayList<String> nums = new ArrayList<String>();
		for (String num : mineF.getNumberLogin()) {
			if (!num.equals(getMyPhoneLogin()))
				nums.add(num);
		}

		return nums;
	}

	public LatLng getMyPosition() {
		if (null != mineF)
			return mineF.getLastLocation();
		return null;
	}

	public String getMyGCMID() {
		return mineF.getUserInfo().getGcmId();
	}

	public void setMyGCMID(String gcmID) {
		if (null != mineF)
			mineF.getUserInfo().setGcmId(gcmID);
	}

	public void setMyPosition(LatLng position) {
		if (null != mineF)
			mineF.setLastLocation(position);

	}

	public String getImage() {
		return mineF.getUserInfo().getAvatar();
	}

	public String getImageLink() {
		return mineF.getUserInfo().getInternetImageLink();
	}

	public CharSequence getMyPhoneLogin() {
		return SettingManager.getInstance().getPhoneAutoLogin();
	}

	// public void loadOffline() {
	// mineF = SQLiteDatabaseManager.getInstance().getMyProfile();
	//
	// if (null == mineF)
	// Log.i("MyProfileManager", "mine null");
	//
	// }
}
