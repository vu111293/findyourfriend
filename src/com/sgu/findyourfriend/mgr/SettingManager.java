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
package com.sgu.findyourfriend.mgr;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.GoogleMap;
import com.sgu.findyourfriend.utils.PreferenceKeys;

public class SettingManager extends BaseManager {

	public static final double STRAGNER_DISTANCE = 200000; // km
	public static final long INERVAL_UPDATE_FRIEND = 120000; // 2 mins
	public static final long AGE_LIMIT =  157896000000l;

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
