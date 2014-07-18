package com.sgu.findyourfriend.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sgu.findyourfriend.MyProfileManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.net.PostData;

public class FriendJSONParserBak2 {

	private static String TAG = FriendJSONParser.class.getName();

	public static ArrayList<Friend> parse(JSONObject jObject) {
		JSONArray jFriends = null;
		try {
			jFriends = jObject.getJSONArray("friends");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return getFriends(jFriends);
	}

	private static ArrayList<Friend> getFriends(JSONArray jFriends) {
		int countryCount = jFriends.length();
		ArrayList<Friend> FriendList = new ArrayList<Friend>();
		Friend Friend = null;

		// Taking each country, parses and adds to list object
		for (int i = 0; i < countryCount; i++) {
			try {
				// Call getCountry with country JSON object to parse the country
				Friend = getFriend((JSONObject) jFriends.get(i));
				FriendList.add(Friend);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return FriendList;
	}

	// Parsing the Country JSON object
	private static Friend getFriend(JSONObject jFriend) {
		Friend f = null;
		User u = null;
		String numberLogin = "";
		int share = 0;

		try {
			u = UserJSONParser.getUser(jFriend.getJSONObject("user"));
			// Log.v("HTSGU", jFriend.getJSONObject("user").toString());
			share = jFriend.getInt("share");
			numberLogin = jFriend.getString("number");
//			f = new Friend(u, numberLogin, PostData.userGetUserState(
//					MyProfileManager.instance.context, u.getId()) == 1, PostData.userGetUserState(MyProfileManager.instance.context, u.getId())==1, share == 3, PostData.historyGetLastUserLacation(MyProfileManager.instance.context, u.getId()));
			// f = new Friend(u, numberLogin, true, true, share == 3, PostData.historyGetLastUserLacation(MyProfileManager.instance.context, u.getId()), null);
			if(f.getLastLocation()!=null)
				Log.i(TAG, u.getName() + "\n" + u.getId() + "\n" + f.getNumberLogin() + "\n" + share + "\n" + f.isAvailable() + "\n" + f.getLastLocation().latitude + "," + f.getLastLocation().longitude);
			else
				Log.i(TAG, u.getName() + "\n" + f.getNumberLogin() + "\n" + share + "\n" + f.isAvailable() + "\n");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return f;
	}
}
