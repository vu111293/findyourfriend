package com.sgu.findyourfriend.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sgu.findyourfriend.model.User;

public class UserJSONParser {

	public static List<User> parse(JSONObject jObject) {
		JSONArray jUsers = null;
		try {
			jUsers = jObject.getJSONArray("users");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return getUsers(jUsers);
	}

	public static List<User> getUsers(JSONArray jUsers) {
		int countryCount = jUsers.length();
		List<User> UserList = new ArrayList<User>();
		User user = null;

		// Taking each country, parses and adds to list object
		for (int i = 0; i < countryCount; i++) {
			try {
				// Call getCountry with country JSON object to parse the country
				user = getUser((JSONObject) jUsers.get(i));
				UserList.add(user);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return UserList;

	}

	// Parsing the Country JSON object
	public static User getUser(JSONObject jUser) {

		Log.d("USER JSON", jUser.toString());
		
		User user = null;
		int id;
		String name = "";
		int gender = 0;
		String province = "";
		String email = "";
		String avatar = "";
		String gcmId = "";
		Timestamp lastestLogin = new Timestamp(0);

		try {
			id = jUser.getInt("id");
			name = jUser.getString("name");
			gender = jUser.getInt("gender");
			province = jUser.getString("province");
			email = jUser.getString("email");
			avatar = jUser.getString("avatar");
			gcmId = jUser.getString("gcmid");
			lastestLogin = Timestamp.valueOf(jUser.getString("lastestlogin"));
			user = new User(id, name, gender, province, email, avatar, gcmId,
					lastestLogin);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return user;
	}
}
