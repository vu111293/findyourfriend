package com.sgu.findyourfriend.mgr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Display;

import com.google.android.gms.maps.GoogleMap;
import com.sgu.findyourfriend.utils.PreferenceKeys;

public class SettingManager {

	public static final double STRAGNER_DISTANCE = 200000; // km

	private static SettingManager instance;

	public static String SHARE_PREFERENCES_KEY = "com.sgu.findyourfriend.sharepreferences.11007";
	public static String MESSAGE_COUNTER_KEY = "newMessage";
	public static String REQUEST_COUNTER_KEY = "newRequest";
	
	public static int WIDTH_SCREEN;
	public static int HEIGHT_SCREEN;

	public static long TIME_UPDATE_DEFAULT = 90000;
	
	private SharedPreferences prefs;

	public void init(Context context) {
		prefs = context.getSharedPreferences(
				"com.sgu.findyourfriend.sharepreferences.11007",
				Context.MODE_PRIVATE);
		
		
//		Display display = ((Activity)context).getWindowManager().getDefaultDisplay(); 
//		WIDTH_SCREEN = display.getWidth();
//		HEIGHT_SCREEN = display.getHeight();
	}

	public SettingManager() {

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
		return prefs.getInt(PreferenceKeys.accuracy, 1000);
	}

	public long getIntervalUpdateFriend() {
		return prefs.getInt(PreferenceKeys.timeToUpdateFriend, 2) * 60000;
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
		return prefs.getBoolean(PreferenceKeys.sms, false);
	}

//	public Set<String> getFriendsWarning() {
//
//		for (String id : prefs.getStringSet(PreferenceKeys.friendsWarning,
//				new HashSet<String>())) {
//			Log.i("share:", id);
//		}
//
//		return prefs.getStringSet(PreferenceKeys.friendsWarning,
//				new HashSet<String>());
//	}

	public String getDefaultMsg() {
		return prefs.getString(PreferenceKeys.defaultMsg, "HELP");
	}

	public boolean isAutoLogin() {
		return prefs.getBoolean(PreferenceKeys.AUTO_LOGIN, false);
	}
	
	public boolean setAutoLogin(boolean autoLogin) {
		return prefs.getBoolean(PreferenceKeys.AUTO_LOGIN, autoLogin);
	}

	public String getPhoneAutoLogin() {
		return prefs.getString(PreferenceKeys.PHONENUMBER_AUTO_LOGIN, "0979742144");
	}

	public String getPasswordAutoLogin() {
		return prefs.getString(PreferenceKeys.PASSWORD_AUTO_LOGIN, "111111");
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
		return prefs.getInt(PreferenceKeys.lastAccountIdLogin, 0);
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
	
	
}
