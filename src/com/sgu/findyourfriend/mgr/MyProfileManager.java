package com.sgu.findyourfriend.mgr;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.net.PostData;

public class MyProfileManager {

	public static MyProfileManager instance;
	public static String KEY_SHAREPREFERENCES = "com.sgu.findyourfriend";

	private SharedPreferences prefs;

	// get from server
	// public static String userId = "101";
	// public static String gcmMyId;
	// public static String name;
	// public static String email;
	public ArrayList<String> numberLogins;
	public String password;

	// public static LatLng myLocation;

	public User mine;
	public LatLng myLocation;

	public Context context;

	public void init(Context context) {
		this.context = context;
		prefs = context.getSharedPreferences(KEY_SHAREPREFERENCES,
				Context.MODE_PRIVATE);
		load();
	}

	public synchronized static MyProfileManager getInstance() {
		if (instance == null) {
			instance = new MyProfileManager();
		}
		return instance;
	}

	public void load() {
		// load from server
		mine = PostData.userGetUserById(context, 7);
		mine.setGcmid(GCMRegistrar.getRegistrationId(context));

		myLocation = PostData.historyGetLastUserLacation(context, 7);

		// mine = new User(7, "admin", 1, "NewQork", "admin@gmail.com",
		// "http://png.findicons.com/files/icons/175/halloween_avatar/128/scream.png",
		// "APA91bGZdyFkSv-T5A2s_ulCjB45bVM6tPU2uGSAf2jmaOxVwSDoqNtF8_h78qZoAeTFJUmSAWfynNqEktOTpLeIS6otCGpP9xlkSGX87mc4qH63LIpeiEPHs3U2C5lUh__z3XgHyudRG5E5by7vF7X59xVyo9KMjg",
		// new Timestamp(System.currentTimeMillis()));

		myLocation = PostData.historyGetLastUserLacation(context, 7);

		// myLocation = new LatLng(10.973881272186989, 106.5906023606658);

		numberLogins = new ArrayList<String>();
		numberLogins.add("0979742138");
		password = "admin";

		// get my location
		// myLocation = new LatLng(prefs.getFloat("myLatitute", 0f),
		// prefs.getFloat("mylongitute", 0f));

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
