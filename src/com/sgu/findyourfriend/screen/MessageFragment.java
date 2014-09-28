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
package com.sgu.findyourfriend.screen;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.MessageAdapter;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.IMessage;
import com.sgu.findyourfriend.mgr.MessageManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.model.Message;
import com.sgu.findyourfriend.utils.Utility;

public class MessageFragment extends Fragment implements IMessage {

	private static String TAG = "MESSAGE FRAGMENT";
	private static boolean isRegister; // = false;
	private List<Message> messages;
	private MessageAdapter adapter;
	private TextView text;

	private Context context;
	private Activity activity;

	private ListView smsListView;

	private MessageFragment mThis = this;

	// private ProgressDialogCustom progress;

	public MessageFragment() {
		isRegister = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// check and notify
		if (SettingManager.getInstance().getNoNewMesssage() > 0) {

			// broadcast update widget
			SettingManager.getInstance().setNoNewMessage(0);

			Intent intent = new Intent(Config.UPDATE_MESSAGE_WIDGET_ACTION);
			// Send Broadcast to Broadcast receiver with message
			context.sendBroadcast(intent);

			// send broadcast hide notify
			Intent intent2 = new Intent(Config.NOTIFY_UI);
			intent2.putExtra(Config.MESSAGE_NOTIFY, Config.HIDE);
			context.sendBroadcast(intent2);

		}

		View rootView = inflater.inflate(R.layout.fragment_message, container,
				false);
		MessageManager.getInstance().setMessageListener(this);

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity.getApplicationContext();
		this.activity = activity;
		if (!isRegister) {
			activity.registerReceiver(mHandleMessageReceiver, new IntentFilter(
					com.sgu.findyourfriend.mgr.Config.UPDATE_UI));
			activity.registerReceiver(mHandleMessageReceiver, new IntentFilter(
					com.sgu.findyourfriend.mgr.Config.MAIN_ACTION));
			isRegister = true;
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		text = (TextView) view.findViewById(R.id.text);

		text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				openEditMessageScene(-1);
			}
		});

		smsListView = (ListView) view.findViewById(R.id.listview);

		messages = MessageManager.getInstance().getAllMessage();
		adapter = new MessageAdapter(context, messages);

		smsListView.setAdapter(adapter);

		smsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {

				final Message msgtemp = adapter.getItem(position);
				ArrayList<String> data = new ArrayList<String>();
				data.add("Gửi mới");

				if (null != msgtemp.getLocation())
					data.add("Xem bản đồ");

				data.add("Xóa");

				final Dialog dialog = new Dialog(getActivity());
				Utility.showListDialog(Utility.CONFIRM, dialog,
						"Tùy chọn với tin nhắn", data,
						new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int position, long arg3) {

								switch (position) {
								case 0:
									// reply
									int id;
									if (msgtemp.isMine()) {
										id = msgtemp.getIdReceiver();
									} else {
										id = msgtemp.getIdSender();
									}

									Log.i("Message ",
											"sender: "
													+ adapter.getItem(position)
															.getIdSender()
													+ ", receiver: "
													+ adapter.getItem(position)
															.getIdReceiver()
													+ ", con: "
													+ msgtemp.getMessage()
													+ ", # "
													+ msgtemp.getIdSender()
													+ ", # rc"
													+ msgtemp.getIdReceiver());

									if (id == 0) {
										Utility.showMessage(activity,
												"Tạm thời chưa hỗ trợ trực tiếp từ quản trị.");
									} else {
										openEditMessageScene(id);
									}
									break;
								case 1:
									if (null != msgtemp.getLocation()) {
										// map view
										Intent i = new Intent(context,
												MapViewActivity.class);

										i.putExtra("latitude",
												msgtemp.getLocation().latitude);
										i.putExtra("longitude",
												msgtemp.getLocation().longitude);
										i.putExtra("name",
												msgtemp.getSenderName());

										i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										context.startActivity(i);
										break;
									}
								case 2:
									// delete message
									MessageManager.getInstance().deleteMessage(
											msgtemp);
									messages.remove(msgtemp);
									adapter.notifyDataSetChanged();
									smsListView.setSelection(messages.size() - 1);
									break;
								}

								dialog.dismiss();
							}
						});
			}
		});

		setBottomSMS();
		// smsListView.setSelection(messages.size() - 1);
		// progress.dismiss();

	}

	public void setBottomSMS() {
		smsListView.setSelection(messages.size() - 1);
	}

	@Override
	public void onResume() {
		super.onResume();
		// smsListView.setSelection(messages.size() - 1);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (isRegister)
			getActivity().unregisterReceiver(mHandleMessageReceiver);
	}

	// public void onDestroyView() {
	// super.onDestroyView();
	// }

	public void addNewMessage(Message msg) {
		messages.add(msg);
		adapter.notifyDataSetChanged();
		smsListView.setSelection(messages.size() - 1);
	}

	private void openEditMessageScene(int friendId) {
		MessageSendFragment fragment = new MessageSendFragment(mThis);

		Bundle bundle = new Bundle();
		bundle.putInt("friendId", friendId);
		fragment.setArguments(bundle);

		((BaseContainerFragment) mThis.getParentFragment()).replaceFragment(
				fragment, true);
	}

	// --------------handle message ----------------------------------
	// handle message
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context ctx, Intent intent) {
			String action = intent.getAction();

			if (action.equals(Config.MAIN_ACTION)) {
				if (intent.hasExtra(Config.EDIT_MESSAGE_ACTION)) {
					int fID = intent
							.getIntExtra(Config.EDIT_MESSAGE_ACTION, -1);
					Log.i("MESSAGE", fID + "");
					if (fID >= 0) {

						openEditMessageScene(fID);
					}
				}

			} else if (action.equals(Config.UPDATE_UI)) {
				if (intent.getStringExtra(
						(com.sgu.findyourfriend.mgr.Config.UPDATE_TYPE))
						.equals(Utility.MESSAGE)) {

					adapter.notifyDataSetChanged();
					smsListView.setSelection(messages.size() - 1);
				}

			}

		}
	};

}
