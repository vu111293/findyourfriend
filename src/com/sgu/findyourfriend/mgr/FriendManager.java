/*
 * 	 This file is part of Find Your Friend.
 *
 *   Find Your Friend is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Find Your Friend is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Find Your Friend.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sgu.findyourfriend.mgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.History;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.net.PostData;

public class FriendManager extends BaseManager {

	public static String TAG = "FRIEND MANAGER";

	private static FriendManager instance;

	private List<Friend> tempFriends;

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
		tempFriends = new ArrayList<Friend>();

		if (Config.MODE_OFFLINE) {

			ArrayList<History> hisCommon = new ArrayList<History>();
			hisCommon
					.add(new History(null, new LatLng(10.014108, 106.5529177)));

			ArrayList<String> phoneNumber = new ArrayList<String>();
			phoneNumber.add("123456789");
			phoneNumber.add("0979742138");

			User userCommon = new User(48, "machinev4", "", "", null, 1,
					"machine address", null, "", "", "", "", false);

			tempFriends.add(new Friend(userCommon.clone(), phoneNumber, true,
					Friend.SHARE_RELATIONSHIP, new LatLng(10.014108,
							106.5529177), hisCommon));

			userCommon.setId(9);
			userCommon.setName("user 9");
			tempFriends.add(new Friend(userCommon.clone(), phoneNumber, true,
					Friend.FRIEND_RELATIONSHIP, null, null));

			userCommon.setId(2);
			userCommon.setName("user 2");
			tempFriends.add(new Friend(userCommon.clone(), phoneNumber, true,
					Friend.FRIEND_RELATIONSHIP, null, null));

			userCommon.setId(13);
			userCommon.setName("user 13");
			tempFriends.add(new Friend(userCommon.clone(), phoneNumber, true,
					Friend.FRIEND_RELATIONSHIP, null, null));

			userCommon.setId(15);
			userCommon.setName("user 15");
			tempFriends.add(new Friend(userCommon.clone(), phoneNumber, true,
					Friend.FRIEND_RELATIONSHIP, null, null));

			userCommon.setId(20);
			userCommon.setName("user 20");
			tempFriends.add(new Friend(userCommon.clone(), phoneNumber, true,
					Friend.REQUEST_FRIEND, new LatLng(10.906605624487236,
							107.2491654753685), null));

			userCommon.setId(36);
			userCommon.setName("user 36");
			tempFriends.add(new Friend(userCommon.clone(), phoneNumber, true,
					Friend.SHARE_RELATIONSHIP, new LatLng(11.5141075,
							105.5529176), null));

			userCommon.setId(50);
			userCommon.setName("user 50");
			tempFriends.add(new Friend(userCommon.clone(), phoneNumber, true,
					Friend.SHARE_RELATIONSHIP, new LatLng(10.014108,
							107.5529177), null));

			userCommon.setId(55);
			userCommon.setName("user 55");
			tempFriends.add(new Friend(userCommon.clone(), phoneNumber, true,
					Friend.FRIEND_RELATIONSHIP, null, null));
		} else {
			tempFriends = PostData.friendGetFriendList(context,
					MyProfileManager.getInstance().getMyID());
		}
	}

	public synchronized static FriendManager getInstance() {
		if (instance == null) {
			instance = new FriendManager();
		}
		return instance;
	}

	public void loadFriend() {
		if (!Config.MODE_OFFLINE) {
			tempFriends.clear();
			tempFriends.addAll(PostData.friendGetFriendList(context,
					MyProfileManager.getInstance().getMyID()));
		}
	}

	public void setupAfterLoading() {
		if (isStop)
			return;
		Log.i("DEBUG friend size", tempFriends.size() + "");

		pureFriends.clear();

		if (null != MyProfileManager.getInstance().getMineInstance())
			tempFriends.add(MyProfileManager.getInstance().getMineInstance());

		hmImageP.clear();

		@SuppressWarnings("unchecked")
		Iterator<Friend> it = (new ArrayList(tempFriends)).iterator();

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
		Iterator<Friend> it2 = (new ArrayList(tempFriends)).iterator();
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

		hmStrangers = new HashMap<Integer, LatLng>();
		hmInvited = new HashMap<Integer, Friend>();

		pureFriends = new ArrayList<Friend>();
		friends = new ArrayList<Friend>();

		if (null != MyProfileManager.getInstance().getMineInstance()) {
			tempFriends
					.add(0, MyProfileManager.getInstance().getMineInstance());

			mID = MyProfileManager.getInstance().getMyID();
		}

		// setup drawable icon
		hmImageP = new HashMap<Integer, Drawable>();

		@SuppressWarnings("unchecked")
		Iterator<Friend> it = (new ArrayList(tempFriends)).iterator();

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
		Iterator<Friend> it2 = (new ArrayList(tempFriends)).iterator();
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
		if (Config.MODE_OFFLINE) {
			hmStrangers.put(100, new LatLng(11.574108, 107.8529177));
			hmStrangers.put(101, new LatLng(10.214108, 106.41529177));
			hmStrangers.put(102, new LatLng(10.414108, 106.9529177));
		}

		// setup friends
		friends = new ArrayList<Friend>(pureFriends);
		friends.add(0, MyProfileManager.getInstance().getMineInstance());
	}

	public void updateFriend(Friend friend) {

		int key = friend.getUserInfo().getId();

		for (Friend f : tempFriends) {
			if (f.getUserInfo().getId() == key) {
				f = friend;
				break;
			}
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
		hmRequestFriends.remove(key);

		hmMemberFriends.put(key, fr);

		pureFriends.add(fr);

		friends.add(friends.size(), fr);
	}

	public void removeFriendRequest(Friend fr) {
		hmRequestFriends.remove(fr.getUserInfo().getId());
	}

	public void addFriendRequest(Friend fr) {
		hmRequestFriends.put(fr.getUserInfo().getId(), fr);

		if (!fr.getUserInfo().getAvatar().equals(""))
			hmImageP.put(fr.getUserInfo().getId(),
					Drawable.createFromPath(fr.getUserInfo().getAvatar()));
		else
			hmImageP.put(fr.getUserInfo().getId(), context.getResources()
					.getDrawable(R.drawable.ic_no_imgprofile));
	}

	public void addFriendMember(Friend fr) {
		Log.i("ADDMEMBER", fr.getUserInfo().getId() + "");

		hmMemberFriends.put(fr.getUserInfo().getId(), fr);

		if (!fr.getUserInfo().getAvatar().equals(""))
			hmImageP.put(fr.getUserInfo().getId(),
					Drawable.createFromPath(fr.getUserInfo().getAvatar()));
		else
			hmImageP.put(fr.getUserInfo().getId(), context.getResources()
					.getDrawable(R.drawable.ic_no_imgprofile));

		pureFriends.add(fr);

		// updateSwipeFriends();
		friends.add(friends.size(), fr);
	}

	public void updateChangeMemberFriend(Friend fr) {
		int key = fr.getUserInfo().getId();
		hmMemberFriends.remove(key);
		hmMemberFriends.put(key, fr);

		
		int i = 0;
		int size = friends.size();
		while (i < size && friends.get(i).getUserInfo().getId() != fr.getUserInfo().getId()) {
			i++;
		}

		if (i < size) {
			friends.get(i).setAcceptState(fr.getAcceptState());
			Log.i(TAG, "updated into updateChangleMember");
		}
		
		for (Friend f : friends) {
			Log.i(TAG, "friend " + f.getUserInfo().getName() + ", state: " + f.getAcceptState());
		}
		
//		ArrayList<Friend> fs = new ArrayList<Friend>(friends);
//		friends.clear();
//		friends.addAll(fs);
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
		for (Friend f : tempFriends) {
			if (f.getUserInfo().getId() == idFriend)
				return f.getUserInfo().getName();
		}
		return "Không có";
	}
}
