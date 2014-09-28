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

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.utils.PreferenceKeys;

public class SettingManager extends BaseManager {

	public static final double STRAGNER_DISTANCE = 2000000; // km
	public static final long INERVAL_UPDATE_FRIEND = 120000; // 2 mins
	public static final long AGE_LIMIT =  157896000000l;

	public static final LatLng centerVNLatLng = new LatLng(16.229700866878492, 107.07352973520756);
	
	public static String SHARE_PREFERENCES_KEY = "com.sgu.findyourfriend.sharepreferences.11007";
	public static String MESSAGE_COUNTER_KEY = "newMessage";
	public static String REQUEST_COUNTER_KEY = "newRequest";
	
	public static int WIDTH_SCREEN;
	public static int HEIGHT_SCREEN;

	public static long TIME_UPDATE_DEFAULT = 90000;
	
	private static SettingManager instance;
	
	private SharedPreferences prefs;

	@Override
	public void init(Context context) {
		super.init(context);
		prefs = context.getSharedPreferences(
				"com.sgu.findyourfriend.sharepreferences.11007",
				Context.MODE_PRIVATE);
	}

	public synchronized static SettingManager getInstance() {
		if (instance == null) {
			instance = new SettingManager();
		}
		return instance;
	}

	public int getMapType() {
		String type = prefs.getString(PreferenceKeys.mapType, "normal");

		if (type.equals("normal"))
			return GoogleMap.MAP_TYPE_NORMAL;
		else if (type.equals("terrain"))
			return GoogleMap.MAP_TYPE_TERRAIN;
		else
			return GoogleMap.MAP_TYPE_HYBRID;
	}

	public int getNoNewMesssage() {
		return prefs.getInt(MESSAGE_COUNTER_KEY, 0);
	}

	public void setNoNewMessage(int num) {
		prefs.edit().putInt(MESSAGE_COUNTER_KEY, num).commit();
	}

	public int getNoNewRequest() {
		return prefs.getInt(REQUEST_COUNTER_KEY, 0);
	}

	public void setNoNewRequest(int num) {
		prefs.edit().putInt(REQUEST_COUNTER_KEY, num).commit();
	}

	public boolean isUploadMyPosition() {
		return prefs.getBoolean(PreferenceKeys.runBackground, true);
	}

	public int getIntervalUpdatePosition() {
		return prefs.getInt(PreferenceKeys.timeToUpdateLocation, 60) * 1000;
	}

	public int getAccuracyUpdatePosition() {
		return prefs.getInt(PreferenceKeys.accuracy, 50);
	}

	public boolean isAlertRingtone() {
		return prefs.getBoolean(PreferenceKeys.isAlertRingTone, true);
	}

	public boolean isMessageRingtone() {
		return prefs.getBoolean(PreferenceKeys.isMessageRingTone, false);
	}

	public String getRingtoneUri() {
		return prefs.getString(PreferenceKeys.ringTone, "");
	}

	public boolean isVibrate() {
		return prefs.getBoolean(PreferenceKeys.vibrate, true);
	}

	public boolean isEmailWarning() {
		return prefs.getBoolean(PreferenceKeys.email, false);
	}

	public boolean isMessageWarning() {
		return prefs.getBoolean(PreferenceKeys.sms, true);
	}

	public String getDefaultMsg() {
		return prefs.getString(PreferenceKeys.defaultMsg, "Tôi cần trợ giúp!");
	}

	public boolean isAutoLogin() {
		return prefs.getBoolean(PreferenceKeys.AUTO_LOGIN, false);
	}
	
	public boolean setAutoLogin(boolean autoLogin) {
		return prefs.getBoolean(PreferenceKeys.AUTO_LOGIN, autoLogin);
	}

	public String getPhoneAutoLogin() {
		return prefs.getString(PreferenceKeys.PHONENUMBER_AUTO_LOGIN, "");
	}

	public String getPasswordAutoLogin() {
		return prefs.getString(PreferenceKeys.PASSWORD_AUTO_LOGIN, "");
	}
	
	public void savePhoneAutoLogin(String phoneNumber) {
		prefs.edit().putString(PreferenceKeys.PHONENUMBER_AUTO_LOGIN, phoneNumber).commit();
	}

	public void savePasswordAutoLogin(String password) {
		prefs.edit().putString(PreferenceKeys.PASSWORD_AUTO_LOGIN, password).commit();
	}
	
	
	public long  getTimeUpdateOnlineStatus() {
		return prefs.getLong(PreferenceKeys.timeUpdateOnlineStatus, TIME_UPDATE_DEFAULT);
	}
	
	// warning with default value
	public int getLastAccountIdLogin() {
		return prefs.getInt(PreferenceKeys.lastAccountIdLogin, -1);
	}
	
	public void saveLastAccountIdLogin(int id) {
		prefs.edit().putInt(PreferenceKeys.lastAccountIdLogin, id).commit();
	}

	public void setUploadMyPosition(boolean checked) {
		prefs.edit().putBoolean(PreferenceKeys.runBackground, checked).commit();
		
	}

	public void setDefaultWarning(HashSet<String> hset) {
		prefs.edit().putStringSet(PreferenceKeys.friendsWarning, hset).commit();
	}
	
	public Set<String> getDefaultWarning() {
		return prefs.getStringSet(PreferenceKeys.friendsWarning, new HashSet<String>());
	}

	public void setDefaultMsg(String text) {
		prefs.edit().putString(PreferenceKeys.defaultMsg, text).commit();
	}
	
	public boolean isFristStartApp() {
		if (prefs.getBoolean("firstStartApp", true)) {
			prefs.edit().putBoolean("firstStartApp", false).commit();
			return true;
		} else 
			return false;
	}
	
	
}
