package com.sgu.findyourfriend.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
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
				user = getUser((JSONObject) jUsers.get(i), false);
				UserList.add(user);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return UserList;

	}

	// Parsing the Country JSON object
	public static User getUser(JSONObject jUser, boolean force) {

		Log.d("USER JSON", jUser.toString());

		User user = null;
		int id;
		String name = "";
		String avatar = "";
		String internetImageLink = "";
		String gcmId = "";
		Timestamp lastLogin = new Timestamp(0);
		int gender = 0;
		String address = "";
		Date birthday = new Date(0);
		String school = "";
		String workplace = "";
		String email = "";
		String fblink = "";
		boolean isPublic = false;

		try {
			id = jUser.getInt("id");
			name = jUser.getString("name");
			internetImageLink = jUser.getString("avatar");
			avatar = catcheProfileImage(id, internetImageLink);
			// Log.i("AVARTA", avatar + " # " + jUser.getString("avatar"));
			gcmId = jUser.getString("gcmid");
			lastLogin = Timestamp.valueOf(jUser.getString("lastlogin"));
			isPublic=(jUser.getInt("ispublic") == 1);
			if(isPublic || force){
				gender = jUser.getInt("gender");
				address = jUser.getString("address");
				birthday = Date.valueOf(jUser.getString("birthday"));
				school = jUser.getString("school");
				workplace = jUser.getString("workplace");
				email = jUser.getString("email");
				fblink = jUser.getString("fblink");
			}
			user = new User(id, name, avatar, gcmId, lastLogin, gender,
					address, birthday, school, workplace, email, fblink,
					isPublic);
			
			user.setInternetImageLink(internetImageLink);
			// Log.i("JSON Parser", id+ name+ avatar+ gcmId+ lastLogin+ gender+
			// address+ birthday+ school+ workplace+ email+ fblink+ isPublic);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return user;
	}

	private static String catcheProfileImage(int id, String http) {
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/findyourfriend");
		myDir.mkdirs();
		String fname = id + ".png";
		File file = new File(myDir, fname);
		if (file.exists()) {
			file.delete();
			file = new File(myDir, fname);
		}
			//return file.getPath();
		try {
			URL url = new URL(http);
			Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());

			bitmap.compress(CompressFormat.PNG, 100, new FileOutputStream(file));

			// FileOutputStream out = new FileOutputStream(file);
			// finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			// out.flush();
			// out.close();
		} catch (Exception e) {
			e.printStackTrace();

			return "";
		}

		return file.getPath();
	}
}