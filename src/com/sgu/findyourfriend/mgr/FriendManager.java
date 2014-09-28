/*
 * Copyright (C) 2014 Tubor Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sgu.findyourfriend.mgr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.internal.fd;
import com.google.android.gms.internal.hm;
import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.History;
import com.sgu.findyourfriend.model.SimpleUserAndLocation;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.net.PostData;

public class FriendManager extends BaseManager {

	public static String TAG = "FRIEND MANAGER";

	private static FriendManager instance;

	private List<Friend> tmpFriendList;
	private ArrayList<SimpleUserAndLocation> tmpStrangers;
	private List<Friend> friendList;

	// hashmap store request friends
	public HashMap<Integer, Friend> hmRequestFriends;

	// hashmap store member list
	public HashMap<Integer, Friend> hmMemberFriends;

	// hashmap strangers
	public HashMap<Integer, LatLng> hmStrangers;

	// hashmap strangers
	public HashMap<Integer, Friend> hmInvited;

	// pure friends - not mine
	public ArrayList<Friend> pureFriends;

	// friends and mine and plus
	public ArrayList<Friend> friends;

	// hashmap store drawable of friend
	public HashMap<Integer, Drawable> hmImageP;

	public Context context;

	public boolean isStop;

	@Override
	public void init(Context context) {
		this.context = context;
		isStop = false;

		tmpFriendList = new ArrayList<Friend>();
		friendList = new ArrayList<Friend>();
		tmpStrangers = new ArrayList<SimpleUserAndLocation>();
		hmStrangers = new HashMap<Integer, LatLng>();
		hmInvited = new HashMap<Integer, Friend>();

		friendList = PostData.friendGetFriendList(context, MyProfileManager
				.getInstance().getMyID());

		// load strangers
		tmpStrangers.clear();

		if (null != MyProfileManager.getInstance().getMyPosition())
			tmpStrangers.addAll(PostData.findInDistance(context,
					MyProfileManager.getInstance().getMyPosition(),
					SettingManager.STRAGNER_DISTANCE));
		else
			tmpStrangers.addAll(PostData.findInDistance(context,
					SettingManager.getInstance().centerVNLatLng,
					SettingManager.STRAGNER_DISTANCE));
	}

	public synchronized static FriendManager getInstance() {
		if (instance == null) {
			instance = new FriendManager();
		}
		return instance;
	}

	// public void loadFriend() {
	// if (!Config.MODE_OFFLINE) {
	// friendList.clear();
	// friendList.addAll(PostData.friendGetFriendList(context,
	// MyProfileManager.getInstance().getMyID()));
	//
	// tmpStrangers.clear();
	// tmpStrangers.addAll(PostData
	// .findInDistance(context, MyProfileManager
	// .getInstance().getMyPosition().latitude,
	// MyProfileManager.getInstance()
	// .getMyPosition().longitude,
	// SettingManager.STRAGNER_DISTANCE));
	//
	// }
	// }

	public void preLoadUpdate() {
		tmpFriendList.clear();
		tmpFriendList.addAll(PostData.friendGetFriendList(context,
				MyProfileManager.getInstance().getMyID()));
	}

	public void endLoadUpdate() {
		friendList.clear();
		friendList.addAll(tmpFriendList);
		setupAfterLoading();

	}

	public void setupAfterLoading() {
		if (isStop)
			return;
		Log.i("DEBUG friend size", friendList.size() + "");

		pureFriends.clear();

		// friendList.clear();
		// friendList.addAll(tmpFriendList);

		if (null != MyProfileManager.getInstance().getMineInstance())
			friendList.add(MyProfileManager.getInstance().getMineInstance());

		hmImageP.clear();

		@SuppressWarnings("unchecked")
		Iterator<Friend> it = (new ArrayList(friendList)).iterator();

		while (it.hasNext()) {
			Friend f = it.next();
			if (!f.getUserInfo().getAvatar().equals(""))
				hmImageP.put(f.getUserInfo().getId(),
						Drawable.createFromPath(f.getUserInfo().getAvatar()));
			else
				hmImageP.put(f.getUserInfo().getId(), context.getResources()
						.getDrawable(R.drawable.ic_no_imgprofile));
		}

		hmMemberFriends.clear();
		hmRequestFriends.clear();

		int mID = MyProfileManager.getInstance().getMyID();
		// filter request friend

		@SuppressWarnings("unchecked")
		Iterator<Friend> it2 = (new ArrayList(friendList)).iterator();
		while (it2.hasNext()) {
			Friend f = it2.next();
			if (f.getAcceptState() == Friend.FRIEND_RELATIONSHIP
					|| f.getAcceptState() == Friend.REQUEST_SHARE
					|| f.getAcceptState() == Friend.REQUESTED_SHARE
					|| f.getAcceptState() == Friend.SHARE_RELATIONSHIP) {
				hmMemberFriends.put(f.getUserInfo().getId(), f);

				if (f.getUserInfo().getId() != mID)
					pureFriends.add(f);

			} else if (f.getAcceptState() == Friend.REQUESTED_FRIEND) {
				hmRequestFriends.put(f.getUserInfo().getId(), f);
			} else if (f.getAcceptState() == Friend.REQUEST_FRIEND) {
				hmInvited.put(f.getUserInfo().getId(), f);

				Log.i("invite===================", f.getUserInfo().getId() + "");

			}
		}

		// setup friends
		friends = new ArrayList<Friend>(pureFriends);
		friends.add(0, MyProfileManager.getInstance().getMineInstance());
	}

	@SuppressLint("UseSparseArrays")
	@Override
	public void setup() {
		int mID = 0;

		// friendList.clear();
		// friendList.addAll(tmpFriendList);

		pureFriends = new ArrayList<Friend>();
		friends = new ArrayList<Friend>();

		if (null != MyProfileManager.getInstance().getMineInstance()) {
			friendList.add(0, MyProfileManager.getInstance().getMineInstance());

			mID = MyProfileManager.getInstance().getMyID();
		}

		// setup drawable icon
		hmImageP = new HashMap<Integer, Drawable>();

		@SuppressWarnings("unchecked")
		Iterator<Friend> it = (new ArrayList(friendList)).iterator();

		while (it.hasNext()) {
			Friend f = it.next();
			if (null == f.getUserInfo().getAvatar()
					|| f.getUserInfo().getAvatar().equals(""))
				hmImageP.put(f.getUserInfo().getId(), context.getResources()
						.getDrawable(R.drawable.ic_no_imgprofile));
			else
				hmImageP.put(f.getUserInfo().getId(),
						Drawable.createFromPath(f.getUserInfo().getAvatar()));
		}

		hmRequestFriends = new HashMap<Integer, Friend>();
		hmMemberFriends = new HashMap<Integer, Friend>();

		@SuppressWarnings("rawtypes")
		Iterator<Friend> it2 = (new ArrayList(friendList)).iterator();
		while (it2.hasNext()) {
			Friend f = it2.next();
			if (f.getAcceptState() == Friend.FRIEND_RELATIONSHIP
					|| f.getAcceptState() == Friend.REQUEST_SHARE
					|| f.getAcceptState() == Friend.REQUESTED_SHARE
					|| f.getAcceptState() == Friend.SHARE_RELATIONSHIP) {
				hmMemberFriends.put(f.getUserInfo().getId(), f);

				if (f.getUserInfo().getId() != mID)
					pureFriends.add(f);

			} else if (f.getAcceptState() == Friend.REQUESTED_FRIEND) {
				hmRequestFriends.put(f.getUserInfo().getId(), f);
			} else if (f.getAcceptState() == Friend.REQUEST_FRIEND) {
				hmInvited.put(f.getUserInfo().getId(), f);

				Log.i("DEBUG INVITED", f.getUserInfo().getId() + " # "
						+ f.getUserInfo().getName());
			}
		}

		// import stranger offline
		// if (Config.MODE_OFFLINE) {
		// hmStrangers.put(100, new LatLng(11.574108, 107.8529177));
		// hmStrangers.put(101, new LatLng(10.214108, 106.41529177));
		// hmStrangers.put(102, new LatLng(10.414108, 106.9529177));
		// }

		// setup friends
		friends = new ArrayList<Friend>(pureFriends);
		friends.add(0, MyProfileManager.getInstance().getMineInstance());

		// stranger
		hmStrangers.clear();

		for (SimpleUserAndLocation sn : tmpStrangers) {
			Log.i("STR", sn.getId() + "");

			if (MyProfileManager.getInstance().getMyID() != sn.getId()
					&& !hmInvited.containsKey(sn.getId())
					&& !hmMemberFriends.containsKey(sn.getId())
					&& !hmRequestFriends.containsKey(sn.getId()))
				hmStrangers.put(sn.getId(),
						new LatLng(sn.getLat(), sn.getLng()));
		}

	}

	public void updateFriend(Friend friend) {
		int key = friend.getUserInfo().getId();

		int size = friendList.size() - 1;
		while (size >= 0 && friendList.get(size).getUserInfo().getId() != key)
			size--;

		if (size >= 0) {
			friendList.set(size, friend);
		}

		if (hmRequestFriends.containsKey(key))
			hmRequestFriends.put(key, friend);

		if (hmMemberFriends.containsKey(key))
			hmMemberFriends.put(key, friend);

		if (hmImageP.containsKey(key))
			if (!friend.getUserInfo().getAvatar().equals(""))
				hmImageP.put(friend.getUserInfo().getId(), Drawable
						.createFromPath(friend.getUserInfo().getAvatar()));
			else
				hmImageP.put(friend.getUserInfo().getId(), context
						.getResources()
						.getDrawable(R.drawable.ic_no_imgprofile));

		// updateSwipeFriends();
		for (Friend f : friends) {
			if (f.getUserInfo().getId() == key) {
				f = friend;
				break;
			}
		}
	}

	public void updateChangeRequestToMember(Friend fr) {
		int key = fr.getUserInfo().getId();

		int size = friendList.size() - 1;
		while (size >= 0 && friendList.get(size).getUserInfo().getId() != key)
			size--;

		if (size >= 0) {
			friendList.set(size, fr);
		}

		hmRequestFriends.remove(key);
		hmMemberFriends.put(key, fr);
		pureFriends.add(fr);
		friends.add(friends.size(), fr);
	}

	public void removeFriendRequest(Friend fr) {
		int key = fr.getUserInfo().getId();

		int size = friendList.size() - 1;
		while (size >= 0 && friendList.get(size).getUserInfo().getId() != key)
			size--;

		if (size >= 0) {
			friendList.remove(size);
		}

		hmRequestFriends.remove(fr.getUserInfo().getId());
	}

	public void addFriendRequest(Friend fr) {
		int key = fr.getUserInfo().getId();

		int size = friendList.size() - 1;
		while (size >= 0 && friendList.get(size).getUserInfo().getId() != key)
			size--;

		if (size < 0) {
			friendList.add(fr);
		}

		hmRequestFriends.put(fr.getUserInfo().getId(), fr);

		if (!fr.getUserInfo().getAvatar().equals(""))
			hmImageP.put(fr.getUserInfo().getId(),
					Drawable.createFromPath(fr.getUserInfo().getAvatar()));
		else
			hmImageP.put(fr.getUserInfo().getId(), context.getResources()
					.getDrawable(R.drawable.ic_no_imgprofile));
	}

	public void addFriendMember(Friend fr) {
		int key = fr.getUserInfo().getId();

		int size = friendList.size() - 1;
		while (size >= 0 && friendList.get(size).getUserInfo().getId() != key)
			size--;

		if (size >= 0) {
			friendList.set(size, fr);
		} else {
			friendList.add(fr);
		}

		Log.i("ADDMEMBER", fr.getUserInfo().getId() + "");
		hmMemberFriends.put(fr.getUserInfo().getId(), fr);

		if (!fr.getUserInfo().getAvatar().equals(""))
			hmImageP.put(fr.getUserInfo().getId(),
					Drawable.createFromPath(fr.getUserInfo().getAvatar()));
		else
			hmImageP.put(fr.getUserInfo().getId(), context.getResources()
					.getDrawable(R.drawable.ic_no_imgprofile));

		pureFriends.add(fr);
		// friendList.add(fr);
	}

	public void updateChangeMemberFriend(Friend fr) {
		int key = fr.getUserInfo().getId();

		int size = friendList.size() - 1;
		while (size >= 0 && friendList.get(size).getUserInfo().getId() != key)
			size--;

		if (size >= 0) {
			friendList.set(size, fr);
		}

		hmMemberFriends.remove(key);
		hmMemberFriends.put(key, fr);

		int i = 0;
		size = friends.size();
		while (i < size
				&& friends.get(i).getUserInfo().getId() != fr.getUserInfo()
						.getId()) {
			i++;
		}

		if (i < size) {
			friends.get(i).setAcceptState(fr.getAcceptState());
			Log.i(TAG, "updated into updateChangleMember");
		}

		for (Friend f : friends) {
			Log.i(TAG,
					"friend " + f.getUserInfo().getName() + ", state: "
							+ f.getAcceptState());
		}

		// ArrayList<Friend> fs = new ArrayList<Friend>(friends);
		// friends.clear();
		// friends.addAll(fs);
	}

	public void setStop(boolean b) {
		this.isStop = b;

	}

	public boolean hasFriendId(int idSender) {
		for (Friend f : pureFriends) {
			if (f.getUserInfo().getId() == idSender)
				return true;
		}

		return false;
	}

	public void removeFriend(int fIDCurrent) {

		int size = friendList.size() - 1;
		while (size >= 0
				&& friendList.get(size).getUserInfo().getId() != fIDCurrent)
			size--;

		if (size >= 0) {
			friendList.remove(size);
		}

		for (Friend f : pureFriends) {
			if (f.getUserInfo().getId() == fIDCurrent) {
				pureFriends.remove(f);
				Log.i(TAG, "remove frienf from pure");
				break;
			}
		}

		for (Friend f : friends) {
			if (f.getUserInfo().getId() == fIDCurrent) {
				friends.remove(f);
				Log.i(TAG, "remove friend from friend");
				break;
			}
		}

		if (hmMemberFriends.containsKey(fIDCurrent)) {
			Log.i(TAG, "remove frienf from hmMember");
			hmMemberFriends.remove(fIDCurrent);
		}
	}

	public void stopShare(int fIDCurrent) {
		int size = friendList.size() - 1;
		while (size >= 0
				&& friendList.get(size).getUserInfo().getId() != fIDCurrent)
			size--;

		if (size >= 0) {
			Friend f = hmMemberFriends.get(fIDCurrent);
			f.setAcceptState(Friend.FRIEND_RELATIONSHIP);
			friendList.set(size, f);
		}

		for (Friend f : pureFriends) {
			if (f.getUserInfo().getId() == fIDCurrent) {
				f.setAcceptState(Friend.FRIEND_RELATIONSHIP);
				break;
			}
		}

		for (Friend f : friends) {
			if (f.getUserInfo().getId() == fIDCurrent) {
				f.setAcceptState(Friend.FRIEND_RELATIONSHIP);
				break;
			}
		}

		if (hmMemberFriends.containsKey(fIDCurrent)) {
			Friend f = hmMemberFriends.remove(fIDCurrent);
			f.setAcceptState(Friend.FRIEND_RELATIONSHIP);
			hmMemberFriends.put(fIDCurrent, f);
		}

	}

	public String getNameFriend(int idFriend) {
		for (Friend f : friendList) {
			if (f.getUserInfo().getId() == idFriend)
				return f.getUserInfo().getName();
		}
		return "Không có";
	}

	public ArrayList<Friend> getFriendsAndMine() {
		ArrayList<Friend> fs = new ArrayList<Friend>();

		fs.add(MyProfileManager.getInstance().getMineInstance());
		for (Friend f : friendList) {
			if (f.getUserInfo().getId() != MyProfileManager.getInstance()
					.getMyID()
					&& (f.getAcceptState() == Friend.FRIEND_RELATIONSHIP
							|| f.getAcceptState() == Friend.REQUEST_SHARE
							|| f.getAcceptState() == Friend.REQUESTED_SHARE || f
							.getAcceptState() == Friend.SHARE_RELATIONSHIP))
				fs.add(f);
		}

		return fs;
	}

	public void removeStranger(int id) {
		// find and remove stranger or invited
		if (hmStrangers.containsKey(id))
			hmStrangers.remove(id);

		if (hmInvited.containsKey(id))
			hmInvited.remove(id);

		int size = friendList.size() - 1;
		while (size >= 0 && friendList.get(size).getUserInfo().getId() != id)
			size--;
	}

	public void addStranger(int fIDCurrent) {
		int size = friendList.size() - 1;
		while (size >= 0
				&& friendList.get(size).getUserInfo().getId() != fIDCurrent)
			size--;

		if (size >= 0) {
			hmStrangers.put(fIDCurrent, friendList.get(size).getLastLocation());
		}

	}

	public void changeStrangerToInvited(int id) {
		// find and remove stranger or invited
		if (hmStrangers.containsKey(id))
			hmStrangers.remove(id);

	}

	public void addFriendInvited(Friend fr) {
		int key = fr.getUserInfo().getId();

		int size = friendList.size() - 1;
		while (size >= 0 && friendList.get(size).getUserInfo().getId() != key)
			size--;

		if (size < 0) {
			friendList.add(fr);
		}

		hmInvited.put(fr.getUserInfo().getId(), fr);

		if (!fr.getUserInfo().getAvatar().equals(""))
			hmImageP.put(fr.getUserInfo().getId(),
					Drawable.createFromPath(fr.getUserInfo().getAvatar()));
		else
			hmImageP.put(fr.getUserInfo().getId(), context.getResources()
					.getDrawable(R.drawable.ic_no_imgprofile));
		
	}
}
