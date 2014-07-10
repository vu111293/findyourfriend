package com.sgu.findyourfriend;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

public class ProfileInfo {

	public static ProfileInfo instance = null; // = new ProfileInfo();
	public static String KEY_SHAREPREFERENCES = "com.sgu.findyourfriend";

	
	// get from server
	public static String userId = "101";
	public static String gcmMyId;
	public static String name;
	public static String email;
	public static String password;

	public static LatLng myLocation;

	private SharedPreferences prefs;
	private Context context;

	public ProfileInfo(Context context) {
		this.context = context;
		prefs = context.getSharedPreferences(KEY_SHAREPREFERENCES,
				Context.MODE_PRIVATE);
		load();
	}
	
	public void load() {

		// get my location
		myLocation = new LatLng(prefs.getFloat("myLatitute", 0f),
				prefs.getFloat("mylongitute", 0f));

		// get my gcmid

		// get username

		// get email and password

	}

	public void saveMyLocation(LatLng latLng) {
		prefs.edit().putFloat("myLatitute", (float) latLng.latitude).commit();
		prefs.edit().putFloat("myLongitute", (float) latLng.latitude).commit();
	}

	public void saveUserName() {

	}

	public void saveGcmId() {

	}

}
