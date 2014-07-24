package com.sgu.findyourfriend.mgr;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.GoogleMap;

public class SettingManager {

	private static SettingManager instance;
	
	public static String SHARE_PREFERENCES_KEY = "com.sgu.findyourfriend.sharepreferences.11007";
	public static String MESSAGE_COUNTER_KEY = "newMessage";
	public static String REQUEST_COUNTER_KEY = "newRequest";
	
	private SharedPreferences prefs;
	
	public void init(Context context) {
		prefs = context.getSharedPreferences(
				"com.sgu.findyourfriend.sharepreferences.11007",
				Context.MODE_PRIVATE);
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
		return GoogleMap.MAP_TYPE_NORMAL;
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
	
}
