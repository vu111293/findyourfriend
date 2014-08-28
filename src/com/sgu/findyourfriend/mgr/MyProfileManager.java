package com.sgu.findyourfriend.mgr;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.net.PostData;

public class MyProfileManager {

	public static MyProfileManager instance;

	// public ArrayList<String> numberLogins;
	// public String password;

	private Friend mineF;

	public Friend mTemp;
	// public User mine;
	// public LatLng myLocation;

	public Context context;

	public void init(Context context, int mId, boolean isForce) {
		this.context = context;

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

			User mineOff = new User(138, "admin", "", "", new Timestamp(
					System.currentTimeMillis()), 1, "address", new Date(1993,
					10, 12), "saigon", "uni", "email@gmail.com", "", true);

			mineF = new Friend(mineOff, numLogins, true, Friend.SHARE_RELATIONSHIP,
					new LatLng(10.906605, 107.2491654),null);
			
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
		return mineF.getLastLocation();
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
}
