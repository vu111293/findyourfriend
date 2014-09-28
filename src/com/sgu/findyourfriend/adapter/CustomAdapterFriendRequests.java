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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
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
import com.sgu.findyourfriend.mgr.Config.AppState;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.screen.ProgressDialogCustom;
import com.sgu.findyourfriend.screen.ViewProfileDialog;
import com.sgu.findyourfriend.utils.Utility;

public class CustomAdapterFriendRequests extends ArrayAdapter<Friend> {

	private Context context;
	private List<Friend> Data = new ArrayList<Friend>();
	private int LayoutResID;
	private ProgressDialogCustom process;

	public CustomAdapterFriendRequests(Context context, int resource,
			List<Friend> objects) {
		super(context, resource, objects);
		this.context = context;
		this.LayoutResID = resource;
		this.Data = objects;
		this.process = new ProgressDialogCustom(context);
	}

	public int getItemCount() {
		return Data.size();
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		final View convertView = inflater.inflate(LayoutResID, null);
		final Friend fr = Data.get(position);

		((ImageView) convertView.findViewById(R.id.imgProfile))
				.setImageDrawable(FriendManager.getInstance().hmImageP.get(fr
						.getUserInfo().getId()));

		((TextView) convertView.findViewById(R.id.txtName)).setText(fr
				.getUserInfo().getName());

		((ImageView) convertView.findViewById(R.id.imgNotNow))
				.setVisibility(View.GONE);
		((Button) convertView.findViewById(R.id.btnNotNow))
				.setVisibility(View.VISIBLE);

		((ImageView) convertView.findViewById(R.id.imgProfile))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						(new ViewProfileDialog(context, getItem(position)
								.getUserInfo().getId())).show();

					}
				});

		((Button) convertView.findViewById(R.id.btnConfirm))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// request to server
						(new AsyncTask<Void, Void, Boolean>() {

							@Override
							protected void onPreExecute() {
								process.show();
							}

							@Override
							protected Boolean doInBackground(Void... params) {
								return PostData.sendFriendAccept(context,
										MyProfileManager.getInstance()
												.getMyID(), fr.getUserInfo()
												.getId());
							}

							@Override
							protected void onPostExecute(Boolean isOk) {
								if (isOk) {
									Data.remove(fr);
									notifyDataSetChanged();

									// warning
									fr.setAcceptState(Friend.FRIEND_RELATIONSHIP);
									FriendManager.getInstance()
											.updateChangeRequestToMember(fr);

									// update with broadcast intent
									Intent intentUpdate = new Intent(
											Config.UPDATE_UI);
									intentUpdate.putExtra(Config.UPDATE_TYPE,
											Utility.FRIEND);
									intentUpdate.putExtra(Config.UPDATE_ACTION,
											Utility.RESPONSE_YES);
									context.sendBroadcast(intentUpdate);

									Toast.makeText(context, "đã chấp nhận",
											Toast.LENGTH_SHORT).show();
									
									
									
									final Dialog dialog = new Dialog(context);
									Utility.showDialog(Utility.CONFIRM, dialog,
											"Thêm bạn", "Bạn có muốn thêm người bạn này vào danh sách trợ giúp khi cần thiết?",
											"Có", new OnClickListener() {
												
												@Override
												public void onClick(View v) {
													
													// add supporter
													Set<String> emerIDs = SettingManager.getInstance().getDefaultWarning();
													emerIDs.add(fr.getUserInfo().getId() + "");
													SettingManager.getInstance().setDefaultWarning(new HashSet<String>(emerIDs));
													Utility.showMessage(context, "Đã thêm vào danh sách hỗ trợ");
													
													dialog.dismiss();
												}
											}, "Không", new OnClickListener() {
												
												@Override
												public void onClick(View v) {
													dialog.dismiss();
												}
											});
								} else {
									Toast.makeText(context, "thử lại sau",
											Toast.LENGTH_SHORT).show();
								}

								process.dismiss();
							}

						}).execute();

					}
				});

		((Button) convertView.findViewById(R.id.btnNotNow))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// request to server
						(new AsyncTask<Void, Void, Boolean>() {

							@Override
							protected void onPreExecute() {
								process.show();
							}

							@Override
							protected Boolean doInBackground(Void... params) {
								return PostData.sendFriendNotAccept(context,
										MyProfileManager.getInstance()
												.getMyID(), fr.getUserInfo()
												.getId());
							}

							@Override
							protected void onPostExecute(Boolean isOk) {
								if (isOk) {
									Data.remove(fr);
									notifyDataSetChanged();

									FriendManager.getInstance()
											.removeFriendRequest(fr);
								} else {
									Toast.makeText(context, "thử lại sau",
											Toast.LENGTH_SHORT).show();
								}

								process.dismiss();
							}

						}).execute();
					}
				});

		return convertView;
	}
}
