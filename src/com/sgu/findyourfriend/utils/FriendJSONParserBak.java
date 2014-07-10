package com.sgu.findyourfriend.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendJSONParserBak {

	public List<HashMap<String, Object>> parse(JSONObject jObject) {
		JSONArray jFriends = null;
		try {
			jFriends = jObject.getJSONArray("items");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return getFriends(jFriends);
	}

	private List<HashMap<String, Object>> getFriends(JSONArray jFriends) {
		int countryCount = jFriends.length();
		List<HashMap<String, Object>> friendList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> friend = null;

		// Taking each country, parses and adds to list object
		for (int i = 0; i < countryCount; i++) {
			try {
				// Call getCountry with country JSON object to parse the country
				friend = getFriend((JSONObject) jFriends.get(i));
				friendList.add(friend);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return friendList;

	}
	
	/*
	 * fs.add(new Friend(o.getString("name"),
							o.getString("email"), o.getString("gcm_regid")));
	 * */

	// Parsing the Country JSON object
	private HashMap<String, Object> getFriend(JSONObject jFriend) {

		HashMap<String, Object> friend = new HashMap<String, Object>();
		String name = "";
		String email = "";
		String phoneNumber = "";
		String avatarLink = "";
		String gcmId = "";
		Boolean isAvailable;

		try {
			name = jFriend.getString("name");
			email = jFriend.getString("email");
			phoneNumber = "09879797997"; //jFriend.getString("phoneNumber");
			avatarLink = jFriend.getString("avartarLink");
			gcmId = jFriend.getString("gcm_regid");
			isAvailable = true; // jFriend.getBoolean("status");
			
			friend.put("name", name);
			friend.put("email", email);
			friend.put("phoneNumber", phoneNumber);
			friend.put("avatarLink", avatarLink);
			friend.put("gcmId", gcmId);
			friend.put("isAvailable", isAvailable);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return friend;
	}
}
