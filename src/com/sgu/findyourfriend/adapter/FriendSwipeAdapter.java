package com.sgu.findyourfriend.adapter;

import java.util.ArrayList;
import java.util.List;

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
	int LayoutResID;
	Context ctx;
	List<Friend> DataList = new ArrayList<Friend>();
	
	private int itemIdOldHightLight = -1;
	private int itemIdHightLight = -1;

	public FriendSwipeAdapter(Context context, int resource,
			List<Friend> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		ctx = context;
		LayoutResID = resource;
		DataList = objects;

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

	@Override
	public Friend getItem(int position) {
		return DataList.get(position);
	}

	public List<Friend> getData() {
		return DataList;
	}
	
	// return -1 if not found
	public int getPositionByFriendID(int friendID) {
		for (int i = 0; i < DataList.size(); ++i) {
			if (DataList.get(i).getUserInfo().getId() == friendID) 
				return i;
		}
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Friend friend = this.getItem(position);

		convertView = LayoutInflater.from(ctx).inflate(LayoutResID, parent,
				false);
		ImageView imgAvatar = (ImageView) convertView
				.findViewById(R.id.imgAvatar);

		Log.i("IMAGE PATH", friend.getUserInfo().getAvatar());

		imgAvatar.setImageDrawable(FriendManager.getInstance().hmImageP
				.get(friend.getUserInfo().getId()));
		
		if (friend.isShare()) {
			((ImageView) convertView.findViewById(R.id.imgNotShare)).setVisibility(View.GONE);
			((ImageView) convertView.findViewById(R.id.imgWait)).setVisibility(View.GONE);
		} else {
			
			if (friend.getAcceptState() == Friend.ACCEPT_STATE) {
				((ImageView) convertView.findViewById(R.id.imgNotShare)).setVisibility(View.VISIBLE);
				((ImageView) convertView.findViewById(R.id.imgWait)).setVisibility(View.GONE);
			} else {
				((ImageView) convertView.findViewById(R.id.imgNotShare)).setVisibility(View.GONE);
				((ImageView) convertView.findViewById(R.id.imgWait)).setVisibility(View.VISIBLE);
			}
		}

		if (itemIdOldHightLight > 0 && itemIdOldHightLight == position) 
			convertView.setBackgroundColor(0x000000);
		
		if (itemIdHightLight == position)
			convertView.setBackgroundColor(0xff2F497A);
			// convertView.setBackgroundColor(0xff7f0700);

		return convertView;
	}

}
