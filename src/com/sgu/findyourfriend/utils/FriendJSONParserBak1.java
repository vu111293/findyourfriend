package com.sgu.findyourfriend.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.User;

public class FriendJSONParserBak1 {

	public static List<Friend> parse(JSONObject jObject) {
		JSONArray jFriends = null;
		try {
			jFriends = jObject.getJSONArray("friends");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return getFriends(jFriends);
	}

	private static List<Friend> getFriends(JSONArray jFriends) {
		int countryCount = jFriends.length();
		List<Friend> FriendList = new ArrayList<Friend>();
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
		String numberLogin="";
		int share = 0;

		try {
			u = UserJSONParser.getUser(jFriend.getJSONObject("user"));
			// Log.v("HTSGU", jFriend.getJSONObject("user").toString());
			share = jFriend.getInt("share");
			numberLogin=jFriend.getString("number");
			// f = new Friend(u, numberLogin, share);
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return f;
	}
}
