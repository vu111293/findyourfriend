package com.sgu.findyourfriend.mgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.net.PostData;

public class FriendManager {

	public static String TAG = "FRIEND MANAGER";

	private static FriendManager instance;
	private List<Friend> friends; // = new ArrayList<Friend>();
	private HashMap<Integer, Friend> hmFriends; // all user

	public List<Friend> requestFriends;
	public HashMap<Integer, Friend> hmRequestFriends; // not friend

	public List<Friend> memberFriends;
	public HashMap<Integer, Friend> hmMemberFriends; // friend share and not
														// share

	public HashMap<Integer, Drawable> hmImageP;
	public Context context;

	public boolean isReady = false;

	// private LatLng[] randLocations = {
	// new LatLng(10.973881272186989, 106.5906023606658),
	// new LatLng(10.973487272186989, 108.5906023606658),
	// new LatLng(10.973007272186989, 105.5906023606658),
	// new LatLng(11.973887272186989, 107.5906023606658),
	// new LatLng(12.073887272186989, 106.5946023606658),
	// new LatLng(10.900087272186989, 106.5996023606658),
	// new LatLng(10.173887272186989, 106.5106023606658),
	// new LatLng(10.673887272186989, 106.5906023606658),
	// new LatLng(10.373887272186989, 106.5901023606658),
	// new LatLng(13.073887272186989, 106.1006023606658)
	// };

	// private String[] drawDemo = {
	// "drawable://" + R.drawable.img1,
	// "drawable://" + R.drawable.img2,
	// "drawable://" + R.drawable.img3,
	// "drawable://" + R.drawable.img4
	// };

	public void init(Context context) {
		this.context = context;
		friends = new ArrayList<Friend>();
		friends = PostData.friendGetFriendList(context, MyProfileManager.getInstance().mine.getId());
	}

	public synchronized static FriendManager getInstance() {
		if (instance == null) {
			instance = new FriendManager();
		}
		return instance;
	}

	public void loadFriend() {
		friends.clear();
		friends.add(MyProfileManager.getInstance().mineF);
		friends.addAll(PostData.friendGetFriendList(context, MyProfileManager.getInstance().mine.getId()));
	}

	synchronized public void setupAfterLoading() {

		Log.i("DEBUG friend size", friends.size() + "");

		hmFriends.clear();

		for (Friend f : friends) {
			hmFriends.put(f.getUserInfo().getId(), f);
		}

		Log.i("DEBUG hm size", hmFriends.size() + "");

		hmImageP.clear();
		// setup drawable icon
		for (Friend f : friends) {
			hmImageP.put(f.getUserInfo().getId(),
					Drawable.createFromPath(f.getUserInfo().getAvatar()));
		}

		hmMemberFriends.clear();
		memberFriends.clear();

		hmRequestFriends.clear();
		requestFriends.clear();

		// filter request friend
		for (Friend f : friends) {
			if (f.getAcceptState() == Friend.FRIEND_RELATIONSHIP
					|| f.getAcceptState() == Friend.REQUEST_SHARE
					|| f.getAcceptState() == Friend.REQUESTED_SHARE
					|| f.getAcceptState() == Friend.SHARE_RELATIONSHIP) {
				hmMemberFriends.put(f.getUserInfo().getId(), f);
				memberFriends.add(f);
			}

			if (f.getAcceptState() == Friend.REQUESTED_FRIEND) {
				hmRequestFriends.put(f.getUserInfo().getId(), f);
				requestFriends.add(f);
			}
		}

	}

	@SuppressLint("UseSparseArrays")
	public void setup() {
		// add mine
		// friends.add(0, new Friend(MyProfileManager.getInstance().mine,
		// MyProfileManager.getInstance().numberLogin,
		// true, true, true, MyProfileManager.getInstance().myLocation, null));

		friends.add(0, MyProfileManager.getInstance().mineF);

		// for (Friend fr : friends) {
		// Log.i(TAG, "user info");
		// Log.i(TAG, fr.getUserInfo().getId() + " # ");
		// Log.i(TAG, fr.getNumberLogin() + " # " + fr.isShare());
		// Log.i(TAG, fr.getUserInfo().getName() + " # "
		// + fr.getUserInfo().getAvatar() + " # "
		// + fr.getUserInfo().getEmail() + " # time: "
		// + fr.getUserInfo().getLastestlogin().toLocaleString());
		// }

		hmFriends = new HashMap<Integer, Friend>();

		for (Friend f : friends) {
			hmFriends.put(f.getUserInfo().getId(), f);
		}

		// for (Friend fr : friends) {
		// Log.i(TAG, "user info");
		// Log.i(TAG, fr.getUserInfo().getId() + " # ");
		// Log.i(TAG, fr.getNumberLogin() + " # " + fr.isShare());
		// Log.i(TAG, fr.getUserInfo().getName() + " # "
		// + fr.getUserInfo().getAvatar() + " # "
		// + fr.getUserInfo().getEmail() + " # time: "
		// + fr.getUserInfo().getLastestlogin().toLocaleString());
		// }

		// setup drawable icon
		hmImageP = new HashMap<Integer, Drawable>();
		for (Friend f : friends) {
			hmImageP.put(f.getUserInfo().getId(),
					Drawable.createFromPath(f.getUserInfo().getAvatar()));
		}

		requestFriends = new ArrayList<Friend>();
		hmRequestFriends = new HashMap<Integer, Friend>();

		memberFriends = new ArrayList<Friend>();
		hmMemberFriends = new HashMap<Integer, Friend>();

		// filter request friend
		for (Friend f : friends) {
			if (f.getAcceptState() == Friend.FRIEND_RELATIONSHIP
					|| f.getAcceptState() == Friend.REQUEST_SHARE
					|| f.getAcceptState() == Friend.REQUESTED_SHARE
					|| f.getAcceptState() == Friend.SHARE_RELATIONSHIP) {
				hmMemberFriends.put(f.getUserInfo().getId(), f);
				memberFriends.add(f);
			}

			if (f.getAcceptState() == Friend.REQUESTED_FRIEND) {
				hmRequestFriends.put(f.getUserInfo().getId(), f);
				requestFriends.add(f);
			}
		}

	}

	public Friend getMine() {
		return friends.get(0);
	}

	private List<Friend> getFriendList() {
		return this.friends;
	}

	public void updateFriend(Friend friend) {

		int key = friend.getUserInfo().getId();

		for (Friend f : friends) {
			if (f.getUserInfo().getId() == key) {
				f = friend;
				break;
			}
		}

		if (hmFriends.containsKey(key))
			hmFriends.put(key, friend);

		for (Friend f : requestFriends) {
			if (f.getUserInfo().getId() == key) {
				f = friend;
				break;
			}
		}

		if (hmRequestFriends.containsKey(key))
			hmRequestFriends.put(key, friend);

		for (Friend f : memberFriends) {
			if (f.getUserInfo().getId() == key) {
				f = friend;
				break;
			}
		}

		if (hmMemberFriends.containsKey(key))
			hmMemberFriends.put(key, friend);

	}

	public void removeFriendRequest(Friend fr) {
		requestFriends.remove(fr);
		hmRequestFriends.remove(fr.getUserInfo().getId());
	}

	// public void updateFriendsState() {
	// friends.clear();
	// friends = PostData.friendGetFriendList(context, 7);
	// }

	// private class RetrieveFriendListTask extends AsyncTask<Void, Void, Void>
	// {
	//
	// protected Void doInBackground(Void... vd) {
	// friends = PostData.friendGetFriendList(context, 1);
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Void result) {
	// hmFriends = new HashMap<Integer, Friend>();
	//
	// for(Friend f : friends) {
	// hmFriends.put(f.getNumberLogin(), f);
	// }
	//
	// for (Friend fr : friends) {
	// Log.i(TAG, "user info");
	// Log.i(TAG, fr.getUserInfo().getId() + " # ");
	// Log.i(TAG, fr.getNumberLogin() + " # " + fr.isShare());
	// Log.i(TAG, fr.getUserInfo().getName() + " # " +
	// fr.getUserInfo().getAvatar() + " # " + fr.getUserInfo().getEmail()
	// + " # time: " + fr.getUserInfo().getLastestlogin().toLocaleString());
	// }
	//
	// }
	// }

}
