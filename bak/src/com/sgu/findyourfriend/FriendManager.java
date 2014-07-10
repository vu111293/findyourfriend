package com.sgu.findyourfriend;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.net.PostManager;
import com.sgu.findyourfriend.utils.FriendJSONParser;

public class FriendManager {

	public static String TAG = "FRIEND MANAGER";

	public static FriendManager instance = null;
	public static List<Friend> friends; // = new ArrayList<Friend>();
	public Context context;
	
	private LatLng[] randLocations = {
			new LatLng(10.973881272186989, 106.5906023606658),
			new LatLng(10.973487272186989, 108.5906023606658),
			new LatLng(10.973007272186989, 105.5906023606658),
			new LatLng(11.973887272186989, 107.5906023606658),
			new LatLng(12.073887272186989, 106.5946023606658),
			new LatLng(10.900087272186989, 106.5996023606658),
			new LatLng(10.173887272186989, 106.5106023606658),
			new LatLng(10.673887272186989, 106.5906023606658),
			new LatLng(10.373887272186989, 106.5901023606658),
			new LatLng(13.073887272186989, 106.1006023606658)	
	};

	public FriendManager(Context context) {
		this.context = context;

		// get friends
		
		// not connect to server
		// getFriendList();
		
		friends = new ArrayList<Friend>();
		
		// add me
		friends.add(new Friend(new User("me", 1, "HCM", "me@gmail.com", "", "123456789", new Timestamp(System.currentTimeMillis())), "101", 1));
		
		
		friends.add(new Friend(new User("Hong Quan", 1, "HCM", "quan@gmail.com", "", "123456789", new Timestamp(System.currentTimeMillis())), "1", 1));
		friends.add(new Friend(new User("Tra Binh", 1, "HN", "binh@gmail.com", "", "321456789", new Timestamp(System.currentTimeMillis())), "2", 1));
		friends.add(new Friend(new User("Quoc Phat", 1, "Vinh", "phat@gmail.com", "", "123654789", new Timestamp(System.currentTimeMillis())), "3", 1));
		
		for(Friend fr : friends) {
			fr.getUserInfo().setLastLocation(randLocations[ (int)(Math.random() * 10)]);
		}
		
		// new RetrieveFriendListTask().execute();
	}

	public void getFriendList() {
		friends = new ArrayList<Friend>();
		String serverUrl = "http://truongtoan.uphero.com/getfriendlist.php";
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", String.valueOf(ProfileInfo.userId));
		String jsonResponse = PostManager.tryPostWithJsonResult(context,
				serverUrl, params);
		if (!jsonResponse.equals("")) {
			// parse json response
			String[] jssplit = jsonResponse
					.split("<!-- Hosting24 Analytics Code -->");
			jsonResponse = jssplit[0];
			// List<Friend> userlist = new ArrayList<Friend>();
			try {
				JSONObject json = new JSONObject(jsonResponse);
				List<Friend> list = FriendJSONParser.parse(json);
				Log.i(TAG, "Parse complete " + list.size());

				for (Friend fr : list) {
					Log.i(TAG, "user info");
					Log.i(TAG, fr.getNumberLogin() + " # " + fr.getShare());
					Log.i(TAG, fr.getUserInfo().getName() + " # " + fr.getUserInfo().getAvatar() + " # " + fr.getUserInfo().getEmail()
							+ " # time: " + fr.getUserInfo().getLastestlogin().toLocaleString() + " # lat: " + fr.getUserInfo().getLastLocation().latitude 
							+ " # lng: " + fr.getUserInfo().getLastLocation().longitude);
					
					
					// / userlist.add(fr);
					
					
					// random location
					
					fr.getUserInfo().setLastLocation(randLocations[ (int)(Math.random() * 10)]);
					
					friends.add(fr);
				}
			} catch (Exception e) {
				Log.d(TAG, "Parsed json error: " + e.getMessage());
			}

		} else {
		}
	}

	
	
	
	
	private class RetrieveFriendListTask extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... vd) {
			friends = new ArrayList<Friend>();
			String serverUrl = "http://truongtoan.uphero.com/getfriendlist.php";
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", String.valueOf(ProfileInfo.userId));
			String jsonResponse = PostManager.tryPostWithJsonResult(context,
					serverUrl, params);
			if (!jsonResponse.equals("")) {
				// parse json response
				String[] jssplit = jsonResponse
						.split("<!-- Hosting24 Analytics Code -->");
				jsonResponse = jssplit[0];
				// List<Friend> userlist = new ArrayList<Friend>();
				try {
					JSONObject json = new JSONObject(jsonResponse);
					List<Friend> list = FriendJSONParser.parse(json);
					Log.i(TAG, "Parse complete " + list.size());

					for (Friend fr : list) {
						// / userlist.add(fr);
						friends.add(fr);
					}
				} catch (Exception e) {
					Log.d(TAG, "Parsed json error: " + e.getMessage());
				}

			} else {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			/*for (Friend f : FriendManager.instance.friends) {
				Log.i(TAG, f.getUserInfo().getName());
			}*/
		}
	}

}
