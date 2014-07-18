package com.sgu.findyourfriend;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.net.PostData;

public class FriendManager {

	public static String TAG = "FRIEND MANAGER";

	private static FriendManager instance;
	public List<Friend> friends; // = new ArrayList<Friend>();
	public HashMap<Integer, Friend> hmFriends;
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

	
	private String[] drawDemo = {
			"drawable://" + R.drawable.img1,
			"drawable://" + R.drawable.img2, 
			"drawable://" + R.drawable.img3, 
			"drawable://" + R.drawable.img4
	};
	
	public void init(Context context) {
		this.context = context;

		// get friends
		
		// not connect to server
		// getFriendList();
		
		friends = new ArrayList<Friend>();
		
		
		/*
		// add me
		friends.add(new Friend(new User("me", 1, "HCM", "me@gmail.com", "drawable://" + R.drawable.img1, "123456789", new Timestamp(System.currentTimeMillis())), "101", 1));
		
		// add friend
		friends.add(new Friend(new User("Hong Quan", 1, "HCM", "quan@gmail.com", "drawable://" + R.drawable.img2, "123456789", new Timestamp(System.currentTimeMillis())), "1", 1));
		friends.add(new Friend(new User("Tra Binh", 1, "HN", "binh@gmail.com", "drawable://" + R.drawable.img3, "321456789", new Timestamp(System.currentTimeMillis())), "2", 1));
		friends.add(new Friend(new User("Quoc Phat", 1, "Vinh", "phat@gmail.com", "drawable://" + R.drawable.img4, "123654789", new Timestamp(System.currentTimeMillis())), "3", 1));
		
		for(Friend fr : friends) {
			fr.getUserInfo().setLastLocation(randLocations[ (int)(Math.random() * 10)]);
		}
		*/
		
		
//		hmFriends = new HashMap<String, Friend>();
//		
//		for(Friend f : friends) {
//			hmFriends.put(f.getNumberLogin(), f);
//		}
		
		
		
		friends = PostData.friendGetFriendList(context, 7);
		
//		friends.add(new Friend(
//				new User(2, "nguyen huy dung", 1, "Ca mau", "admin@gmail.com", "http://findicons.com/files/icons/175/halloween_avatar/128/mike.png",
//						"APA91bGZdyFkSv-T5A2s_ulCjB45bVM6tPU2uGSAf2jmaOxVwSDoqNtF8_h78qZoAeTFJUmSAWfynNqEktOTpLeIS6otCGpP9xlkSGX87mc4qH63LIpeiEPHs3U2C5lUh__z3XgHyudRG5E5by7vF7X59xVyo9KMjg",
//						new Timestamp(System.currentTimeMillis()))
//				,"016680600004", true, true, true, new LatLng(13.073887272186989, 106.1006023606658), null));
//
//		friends.add(new Friend(
//				new User(12, "tran van bi", 0, "Nui ne", "admin@gmail.com", "http://findicons.com/files/icons/1072/face_avatars/300/fh04.png",
//						"APA91bGZdyFkSv-T5A2s_ulCjB45bVM6tPU2uGSAf2jmaOxVwSDoqNtF8_h78qZoAeTFJUmSAWfynNqEktOTpLeIS6otCGpP9xlkSGX87mc4qH63LIpeiEPHs3U2C5lUh__z3XgHyudRG5E5by7vF7X59xVyo9KMjg",
//						new Timestamp(System.currentTimeMillis()))
//				,"016680600154", true, false, false, new LatLng(10.673887272186989, 106.5906023606658), null));
		// new RetrieveFriendListTask().execute();
	}

	
	public synchronized static FriendManager getInstance() {
		if (instance == null) {
			instance = new FriendManager();
		}
		return instance;
	}
	
	
	public void setup() {
		// add mine
//		friends.add(0, new Friend(MyProfileManager.getInstance().mine, MyProfileManager.getInstance().numberLogin,
//		 		true, true, true, MyProfileManager.getInstance().myLocation, null));
		
		friends.add(0, new Friend(MyProfileManager.getInstance().mine, MyProfileManager.getInstance().numberLogins,
 		true, true, true, MyProfileManager.getInstance().myLocation, null));
		
		for (Friend fr : friends) {
			Log.i(TAG, "user info");
			Log.i(TAG, fr.getUserInfo().getId() + " # ");
			Log.i(TAG, fr.getNumberLogin() + " # " + fr.isShare());
			Log.i(TAG, fr.getUserInfo().getName() + " # " + fr.getUserInfo().getAvatar() + " # " + fr.getUserInfo().getEmail()
					+ " # time: " + fr.getUserInfo().getLastestlogin().toLocaleString());
		}
		
		hmFriends = new HashMap<Integer, Friend>();
		
		int i = 0;
		for(Friend f : friends) {			
			// f.getUserInfo().setAvatar(drawDemo[i % 4]);
			hmFriends.put(f.getUserInfo().getId(), f);
			i++;
		}
		
		for (Friend fr : friends) {
			Log.i(TAG, "user info");
			Log.i(TAG, fr.getUserInfo().getId() + " # ");
			Log.i(TAG, fr.getNumberLogin() + " # " + fr.isShare());
			Log.i(TAG, fr.getUserInfo().getName() + " # " + fr.getUserInfo().getAvatar() + " # " + fr.getUserInfo().getEmail()
					+ " # time: " + fr.getUserInfo().getLastestlogin().toLocaleString());
		}
		
	}
	
//	private class RetrieveFriendListTask extends AsyncTask<Void, Void, Void> {
//
//		protected Void doInBackground(Void... vd) {
//			friends = PostData.friendGetFriendList(context, 1);
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			hmFriends = new HashMap<Integer, Friend>();
//			
//			for(Friend f : friends) {
//				hmFriends.put(f.getNumberLogin(), f);
//			}
//			
//			for (Friend fr : friends) {
//				Log.i(TAG, "user info");
//				Log.i(TAG, fr.getUserInfo().getId() + " # ");
//				Log.i(TAG, fr.getNumberLogin() + " # " + fr.isShare());
//				Log.i(TAG, fr.getUserInfo().getName() + " # " + fr.getUserInfo().getAvatar() + " # " + fr.getUserInfo().getEmail()
//						+ " # time: " + fr.getUserInfo().getLastestlogin().toLocaleString());
//			}
//			
//		}
//	}

}
