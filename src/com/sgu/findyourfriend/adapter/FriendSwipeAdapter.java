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
package com.sgu.findyourfriend.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.model.Friend;

public class FriendSwipeAdapter extends ArrayAdapter<Friend> {

	private int LayoutResID;
	private Context ctx;
	private List<Friend> DataList = new ArrayList<Friend>();

	@SuppressLint("UseSparseArrays")
	private Map<Integer, View> myViews = new HashMap<Integer, View>();

	private int itemIdOldHightLight = -1;
	private int itemIdHightLight = -1;

	public FriendSwipeAdapter(Context context, int resource,
			List<Friend> objects) {
		super(context, resource, objects);
		ctx = context;
		LayoutResID = resource;
		DataList = objects;

	}

	public void highLightItemWithId(int friendID) {
		int pos = getPositionByFriendID(friendID);
		if (pos >= 0)
			hightLightItem(pos);
	}

	public void hightLightItem(int pos) {
		itemIdOldHightLight = itemIdHightLight;
		itemIdHightLight = pos;
		notifyDataSetChanged();
	}

	public void unHightLightItem() {
		itemIdHightLight = -1;
		notifyDataSetChanged();
	}

	public void selectItem(int pos) {
		notifyDataSetChanged();
	}

	public void unSelectItem(int pos) {
		notifyDataSetChanged();
	}

	public void setShowStatus(boolean isShow) {
		if (!isShow) {
			DataList.remove(DataList.size() - 1);
			notifyDataSetChanged();
		}
	}

	@Override
	public Friend getItem(int position) {
		return DataList.get(position);
	}

	// return -1 if not found
	public int getPositionByFriendID(int friendID) {
		for (int i = 0; i < DataList.size(); ++i) {
			if (DataList.get(i).getUserInfo().getId() == friendID)
				return i;
		}
		return -1;
	}

	public View getViewByFriendId(int friendId) {

		for (int i = 0; i < DataList.size(); ++i) {
			if (DataList.get(i).getUserInfo().getId() == friendId) {

				return myViews.get(i);
			}
		}

		return null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		

		View view = myViews.get(position);
		ViewHolder holder;
		Friend friend = this.getItem(position);

		Log.i("SWIP", "up state " + friend.getAcceptState());
		
		if (null == view) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(LayoutResID, null);

			holder = new ViewHolder();

			holder.imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
			holder.imgWait = (ImageView) view.findViewById(R.id.imgWaitAlert);
			holder.imgRequest = (ImageView) view
					.findViewById(R.id.imgRequestAlert);
			holder.imgResponse = (ImageView) view
					.findViewById(R.id.imgResponseAlert);

			view.setTag(holder);
			myViews.put(position, view);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.imgAvatar.setImageDrawable(FriendManager.getInstance().hmImageP
				.get(friend.getUserInfo().getId()));
		holder.imgWait.setVisibility(View.GONE);
		holder.imgRequest.setVisibility(View.GONE);
		holder.imgResponse.setVisibility(View.GONE);

		if (friend.getAcceptState() == Friend.REQUEST_SHARE) {
			holder.imgWait.setVisibility(View.VISIBLE);
		} else if (friend.getAcceptState() == Friend.REQUESTED_SHARE) {
			holder.imgRequest.setVisibility(View.VISIBLE);
		} else if (friend.getAcceptState() == Friend.FRIEND_RELATIONSHIP) {
			holder.imgResponse.setVisibility(View.VISIBLE);
		}

		if (itemIdOldHightLight >= 0 && itemIdOldHightLight == position)
			holder.imgAvatar.setBackgroundDrawable(getContext().getResources()
					.getDrawable(R.drawable.border_bkg_noneselect));

		if (itemIdHightLight == position)
			holder.imgAvatar.setBackgroundDrawable(getContext().getResources()
					.getDrawable(R.drawable.border_bkg_selected));

		return view;
	}

	class ViewHolder {
		public ImageView imgAvatar;
		public ImageView imgWait;
		public ImageView imgResponse;
		public ImageView imgRequest;
	}

}
