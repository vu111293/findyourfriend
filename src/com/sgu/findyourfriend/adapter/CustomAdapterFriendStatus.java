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
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.model.Friend;

public class CustomAdapterFriendStatus extends ArrayAdapter<Friend> {
	private int LayoutResID;
	private Context ctx;
	private List<Friend> DataList = new ArrayList<Friend>();

	public CustomAdapterFriendStatus(Context context, int resource,
			List<Friend> objects) {
		super(context, resource, objects);
		ctx = context;
		LayoutResID = resource;
		DataList = objects;
	}

	@Override
	public Friend getItem(int position) {
		return DataList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
		convertView = inflater.inflate(LayoutResID, null);
		TextView name = (TextView) convertView.findViewById(R.id.txtName);
		TextView phoneNumber = (TextView) convertView
				.findViewById(R.id.txtPhoneNumber);
		ImageView imgProfile = (ImageView) convertView
				.findViewById(R.id.imgProfile);
		ImageView imgStatusBar = (ImageView) convertView
				.findViewById(R.id.imgStatusBar);
		TextView txtStatusText = (TextView) convertView
				.findViewById(R.id.txtStatusText);
		ImageView imgShare = (ImageView) convertView
				.findViewById(R.id.imgShare);

		Friend fr = DataList.get(position);

		name.setText(fr.getUserInfo().getName());

		if (null != fr.getNumberLogin() && fr.getNumberLogin().size() > 0)
			phoneNumber.setText(fr.getNumberLogin().get(0));
		else
			phoneNumber.setText("chưa có");

		imgProfile.setImageDrawable(FriendManager.getInstance().hmImageP.get(fr
				.getUserInfo().getId()));

		// set img
		if (fr.getAcceptState() == Friend.SHARE_RELATIONSHIP) {
			// share image
			imgShare.setVisibility(View.VISIBLE);
		} else {
			// don't share
			imgShare.setVisibility(View.GONE);
		}

		if (fr.isAvailable()) {
			imgStatusBar.setBackgroundColor(ctx.getResources().getColor(
					R.color.online));
			txtStatusText.setText("trực tuyến");
		} else {
			imgStatusBar.setBackgroundColor(ctx.getResources().getColor(
					R.color.offine));
			txtStatusText.setText("ẩn");
		}
		return convertView;
	}
}
