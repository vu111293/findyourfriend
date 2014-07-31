package com.sgu.findyourfriend.mgr;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.net.PostData;

public class MyProfileManager {

	public static MyProfileManager instance;

	public ArrayList<String> numberLogins;
	public String password;


	public Friend mineF;
	public User mine;
	public LatLng myLocation;

	public Context context;

	public void init(Context context, int mId) {
		this.context = context;
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
		mine = PostData.userGetUserById(context, mId);
		mine.setGcmid(GCMRegistrar.getRegistrationId(context));
		myLocation = PostData.historyGetLastUserLocation(context, mId);

		numberLogins = new ArrayList<String>();
		numberLogins.add(SettingManager.getInstance().getPhoneAutoLogin());
		password = SettingManager.getInstance().getPhoneAutoLogin();

		
		// modify
		mineF = new Friend(mine, numberLogins, true,
				Friend.SHARE_RELATIONSHIP, myLocation, null);

	}
}
