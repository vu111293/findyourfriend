package com.sgu.findyourfriend.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.History;
import com.sgu.findyourfriend.model.User;

public class FriendJSONParser {

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
	public static Friend getFriend(JSONObject jFriend) {
		Friend f = null;
		try {
			User u = null;
			ArrayList<String> numberLogin = new ArrayList<String>();
			History h = null;
			int share = 0;
			boolean state;
			u = UserJSONParser.getUser(jFriend.getJSONObject("user"), false);
			share = jFriend.getInt("share");
			if (!jFriend.isNull("lastlocation"))
				h = HistoryJSONParser.getHistory(jFriend
						.getJSONObject("lastlocation"));
			JSONArray jarr = jFriend.getJSONArray("numberlist");
			int countryCount = jarr.length();
			for (int i = 0; i < countryCount; i++) {
				try {
					numberLogin.add(jarr.getString(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			state = jFriend.getString("state").equals("1");

			Log.v(TAG, "[" + u.getId() + "]" + u.getName());
			Log.v(TAG, "Share: " + share);
			Log.v(TAG, state ? "Online" : "Offline");
			for (String string : numberLogin) {
				Log.v(TAG, string);
			}
			Log.v(TAG, "LAST: "
					+ (h == null ? "?" : h.getLocation().latitude) + ":"
					+ (h == null ? "?" : h.getLocation().longitude) + "");

			f = new Friend(u, numberLogin, state, share,
					(h == null ? null : h.getLocation()), null);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return f;
	}
	
	// 0 is don't accept
	// 1 2 3 is accept
	// 1 is wait
	// 2 is accept
}
