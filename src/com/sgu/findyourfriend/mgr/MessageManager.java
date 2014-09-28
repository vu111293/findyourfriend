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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.Message;
import com.sgu.findyourfriend.model.TempMessage;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.utils.Utility;
import com.sgu.findyourfriend.utils.Utility.RReply;

public class MessageManager extends BaseManager {

	public static String TAG = "MessageManager";
	private static MessageManager instance;
	private IMessage iMessage;
	private Context context;

	@Override
	public void init(Context context) {
		this.context = context;

		this.context.registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Config.DISPLAY_MESSAGE_ACTION));
		this.context.registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Config.LOCAL_MESSAGE_ACTION));
	}

	public synchronized static MessageManager getInstance() {
		if (instance == null) {
			instance = new MessageManager();
		}
		return instance;
	}

	public void setMessageListener(IMessage iMessage) {

		Log.i("Message fragment", "set listener");
		this.iMessage = iMessage;
	}

	public List<Message> getAllMessage() {
		// return dataSource.getAllMessages();
		return SQLiteDatabaseManager.getInstance().getAllMessage();
	}

	public void sendMessage(String msg, List<Integer> addrs) {

		for (int addr : addrs) {
			Message sms = new Message(msg, true, MyProfileManager.getInstance()
					.getMyID(), MyProfileManager.getInstance().getMyName(),
					addr, FriendManager.getInstance().hmMemberFriends.get(addr)
							.getUserInfo().getName(), MyProfileManager
							.getInstance().getMyPosition(), new Date(
							System.currentTimeMillis()));
			sms = SQLiteDatabaseManager.getInstance().saveMessage(sms);
			if (null != iMessage)
				iMessage.addNewMessage(sms);
			(new SendMessage()).execute(msg, addr + "");
		}
	}

	private class SendMessage extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			int regIdFrom = MyProfileManager.getInstance().getMyID();
			int regIdTo = Integer.parseInt(params[1]);

			String message = regIdFrom + Config.PARTERN_GET_MESSAGE + params[0];

			if (null != MyProfileManager.getInstance().getMyPosition())
				message += Config.PREFIX_LOCATION_IN_MESSAGE
						+ MyProfileManager.getInstance().getMyPosition().latitude
						+ " "
						+ MyProfileManager.getInstance().getMyPosition().longitude;

			PostData.sendMessage(context, regIdFrom, regIdTo, message);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
		}
	}

	// Create a broadcast receiver to get message and show on screen
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, Intent intent) {

			String newMessage = intent.getExtras().getString(
					Config.EXTRA_MESSAGE);

			// check message here !
			// update num msg for widget
			SettingManager.getInstance().init(context);

			if (Utility.verifyRequest(newMessage)) {
				notifyRequestTask(newMessage);
			} else if (Utility.verifyResponse(newMessage)) {
				notifyResponseTask(newMessage);
			} else {
				notifyNormalMessageTask(newMessage);
			}

			// update by intent broadcast
			Intent intentBC = new Intent(Config.UPDATE_MESSAGE_WIDGET_ACTION);
			// Send Broadcast to Broadcast receiver with message
			context.sendBroadcast(intentBC);

			// Waking up mobile if it is sleeping
			Utility.acquireWakeLock(context);

			// Releasing wake lock
			Utility.releaseWakeLock();
		}
	};

	public void deleteMessage(Message message) {
		SQLiteDatabaseManager.getInstance().removeMessage(message);
	}

	public Message createMessage(Message msg) {
		return SQLiteDatabaseManager.getInstance().saveMessage(msg);
	}

	public void destroy() {
		// remove all new message notify
		SettingManager.getInstance().setNoNewMessage(0);
		SettingManager.getInstance().setNoNewRequest(0);

		context.unregisterReceiver(mHandleMessageReceiver);
	}

	private void notifyRequestTask(String newMessage) {
		final RReply reply = Utility.getRequest(newMessage);
		final Intent intentUpdate = new Intent(Config.UPDATE_UI);

		(new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				if (reply.getType().equals(Utility.FRIEND)) {
					// send broadcast update ui vs msg
					intentUpdate.putExtra(Config.UPDATE_TYPE, Utility.FRIEND);

					intentUpdate
							.putExtra(Config.UPDATE_ACTION, Utility.REQUEST);

					Friend fr = PostData.friendGetFriend(context,
							reply.getFromId());
					FriendManager.getInstance().addFriendRequest(fr);

					// intent notify friend request
					Intent intent2 = new Intent(Config.NOTIFY_UI);
					intent2.putExtra(Config.FRIEND_REQUEST_NOTIFY, Config.SHOW);
					context.sendBroadcast(intent2);

//					if (!isLocalMessage)
//						Utility.generateNotification(context,
//								"Một yêu cầu kết bạn từ "
//										+ fr.getUserInfo().getName(), "");

				} else if (reply.getType().equals(Utility.SHARE)) {
					// send broadcast update ui vs msg
					intentUpdate.putExtra(Config.UPDATE_TYPE, Utility.SHARE);

					intentUpdate
							.putExtra(Config.UPDATE_ACTION, Utility.REQUEST);

					Friend fr = FriendManager.getInstance().hmMemberFriends
							.get(reply.getFromId());
					fr.setAcceptState(Friend.REQUESTED_SHARE);

					FriendManager.getInstance().updateChangeMemberFriend(fr);

//					if (!isLocalMessage)
//						Utility.generateNotification(context,
//								"Một yêu cầu chia sẻ từ "
//										+ fr.getUserInfo().getName(), "");
				}

				return null;
			}

			protected void onPostExecute(Void result) {
				context.sendBroadcast(intentUpdate);

				SettingManager.getInstance().setNoNewRequest(
						SettingManager.getInstance().getNoNewRequest() + 1);
			};

		}).execute();

	}

	private void notifyResponseTask(String newMessage) {
		final RReply reply = Utility.getResponse(newMessage);
		final Intent intentUpdate = new Intent(Config.UPDATE_UI);

		(new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				if (reply.getType().equals(Utility.FRIEND)) {
					Log.i(TAG, "response friend");
					// send broadcast update ui vs msg
					intentUpdate.putExtra(Config.UPDATE_TYPE, Utility.FRIEND);

					if (reply.isRes()) {
						Log.i(TAG, "response friend yes");
						// response yes
						intentUpdate.putExtra(Config.UPDATE_ACTION,
								Utility.RESPONSE_YES);
												
						Friend fr = PostData.friendGetFriend(context,
								reply.getFromId());
						fr.setAcceptState(Friend.FRIEND_RELATIONSHIP);
						FriendManager.getInstance().addFriendMember(fr);
						
						// remove stranger or invited on map
						FriendManager.getInstance().removeStranger(fr.getUserInfo().getId());
						
						intentUpdate.putExtra(Config.FRIEND_ID, fr.getUserInfo().getId());
						
					} else {
						Log.i(TAG, "response friend no");
						// response no
						intentUpdate.putExtra(Config.UPDATE_ACTION,
								Utility.RESPONSE_NO);
					}

				} else if (reply.getType().equals(Utility.SHARE)) {

					intentUpdate.putExtra(Config.UPDATE_TYPE, Utility.SHARE);
					Friend fr = FriendManager.getInstance().hmMemberFriends
							.get(reply.getFromId());

					if (reply.isRes()) {
						// response yes
						intentUpdate.putExtra(Config.UPDATE_ACTION,
								Utility.RESPONSE_YES);
						fr.setAcceptState(Friend.SHARE_RELATIONSHIP);
					} else {
						// response no
						intentUpdate.putExtra(Config.UPDATE_ACTION,
								Utility.RESPONSE_NO);

						fr.setAcceptState(Friend.FRIEND_RELATIONSHIP);
					}
					FriendManager.getInstance().updateChangeMemberFriend(fr);
				}

				return null;
			}

			protected void onPostExecute(Void result) {
				context.sendBroadcast(intentUpdate);

				SettingManager.getInstance().setNoNewRequest(
						SettingManager.getInstance().getNoNewRequest() + 1);
			};

		}).execute();
	}

	

	private void notifyNormalMessageTask(String newMessage) {
		// update msg ui
		Intent intentUpdate = new Intent(Config.UPDATE_UI);
		intentUpdate.putExtra(Config.UPDATE_TYPE, Utility.MESSAGE);

		Message sms = SQLiteDatabaseManager.getInstance().saveMessage(
				Utility.parseMessage(newMessage));
		
		Log.i("get message: ", sms.getIdSender() + " # rc" + sms.getIdReceiver());
		

		// Display message on the screen
		if (iMessage != null)
			iMessage.addNewMessage(sms);
		else {

			Log.i(TAG, "iMessage null!");

		}

		context.sendBroadcast(intentUpdate);

		SettingManager.getInstance().setNoNewMessage(
				SettingManager.getInstance().getNoNewMesssage() + 1);

		// intent notify normal message
		Intent intent2 = new Intent(Config.NOTIFY_UI);
		intent2.putExtra(Config.MESSAGE_NOTIFY, Config.SHOW);
		context.sendBroadcast(intent2);

//		if (!isLocalMessage)
//			Utility.generateNotification(context, "Bạn có một tin nhắn mới từ "
//					+ sms.getSenderName(), sms.getMessage());

	}
	
	
}
