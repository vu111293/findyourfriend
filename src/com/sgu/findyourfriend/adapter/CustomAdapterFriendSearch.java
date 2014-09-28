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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.screen.ProgressDialogCustom;
import com.sgu.findyourfriend.utils.Utility;

public class CustomAdapterFriendSearch extends ArrayAdapter<User> {

	private Context context;
	private ArrayList<User> data = new ArrayList<User>();
	private int layoutResID;
	private ProgressDialogCustom progress;

	public CustomAdapterFriendSearch(Context context, int resource,
			ArrayList<User> data) {
		super(context, resource, data);
		this.context = context;
		this.layoutResID = resource;
		this.data = data;

		progress = new ProgressDialogCustom(context);
	}

	public void setData(ArrayList<User> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		final View convertView = inflater.inflate(layoutResID, null);
		final User ur = data.get(position);

		if (!ur.getAvatar().equals(""))
			((ImageView) convertView.findViewById(R.id.imgProfile))
					.setImageDrawable(Drawable.createFromPath(ur.getAvatar()));

		((TextView) convertView.findViewById(R.id.txtName)).setText(ur
				.getName());

		if (ur.getAddress().lastIndexOf(",") > -1)
			((TextView) convertView.findViewById(R.id.txtProvince)).setText(ur
					.getAddress()
					.substring(ur.getAddress().lastIndexOf(",") + 1).trim());
		else
			((TextView) convertView.findViewById(R.id.txtProvince)).setText(ur
					.getAddress());

		((Button) convertView.findViewById(R.id.btnInvite))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// request to server
						(new AsyncTask<Void, Void, Void>() {

							Friend fr;
									
							@Override
							protected void onPreExecute() {
								progress.show();
							}

							@Override
							protected Void doInBackground(Void... params) {
								PostData.sendFriendRequest(context,
										MyProfileManager.getInstance()
												.getMyID(), ur.getId());

								fr = PostData.friendGetFriend(context, ur.getId());
								
								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								data.remove(ur);
								notifyDataSetChanged();
								Toast.makeText(context, "đã gửi yêu cầu",
										Toast.LENGTH_SHORT).show();

								// remove friend in strangers hasmap
								FriendManager
										.getInstance()
										.addFriendInvited(fr);

								FriendManager.getInstance()
										.changeStrangerToInvited(ur.getId());

								// update with broadcast
								Intent intentUpdate = new Intent(
										Config.UPDATE_UI);
								intentUpdate.putExtra(Config.UPDATE_TYPE,
										Utility.FRIEND);
								intentUpdate.putExtra(Config.UPDATE_ACTION,
										Utility.RESPONSE_YES);

								progress.dismiss();
							}

						}).execute();

					}
				});

		return convertView;

	}

}
