package com.sgu.findyourfriend.mgr;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

public class SettingManager {

	private static SettingManager instance;

//	private SharedPreferences prefs;

	public void init(Context context) {
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

}
