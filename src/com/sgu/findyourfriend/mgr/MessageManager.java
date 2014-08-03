package com.sgu.findyourfriend.mgr;

import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.Message;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.screen.MainActivity;
import com.sgu.findyourfriend.utils.Controller;
import com.sgu.findyourfriend.utils.MessagesDataSource;
import com.sgu.findyourfriend.utils.Utility;

public class MessageManager {

	public static String TAG = "MessageManager";
	private static MessageManager instance;

	public static int numNewShareMessage = 0;
	public static int numNewFriendMessage = 0;
	private Controller aController;
	public List<Message> messages;

	IMessage iMessage;

	// access database
	private MessagesDataSource dataSource;
	private Context context;
	private MainActivity mainActivity;

	public void init(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
		this.context = mainActivity.getApplicationContext();

		aController = (Controller) context;
		this.context.registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Config.DISPLAY_MESSAGE_ACTION));

		// setup database
		dataSource = new MessagesDataSource(context);
		dataSource.open();
		messages = dataSource.getAllMessages();
	}

	public synchronized static MessageManager getInstance() {
		if (instance == null) {
			instance = new MessageManager();
		}
		return instance;
	}

	public void setMessageListener(IMessage iMessage) {
		this.iMessage = iMessage;
	}

	public List<Message> getAllMessage() {
		return messages;
	}

	public List<Message> filterMessage() {

		return messages;
	}

	public void sendMessage(String msg, List<Integer> addrs) {

		for (int addr : addrs) {
			Message sms = new Message(msg, true,
					MyProfileManager.getInstance().mine.getId(), addr,
					new Date(System.currentTimeMillis()));
			sms = dataSource.createMessage(sms);
			iMessage.addNewMessage(sms);
			(new SendMessage()).execute(msg, addr + "");
		}
	}

	private class SendMessage extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			String regIdFrom = MyProfileManager.getInstance().mine.getId() + "";
			String regIdTo = params[1];
			String message = params[0];

			aController.sendMessage(context, regIdFrom, regIdTo, message);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// addNewMessage(new Message(text.getText().toString(), false));
			// text.setText("");
		}
	}

	public void sendUpdateMessageWidget() {
		// ui update
		mainActivity.newMessageNotify(false);

		Intent intent = new Intent(Config.UPDATE_MESSAGE_WIDGET_ACTION);
		// Send Broadcast to Broadcast receiver with message
		context.sendBroadcast(intent);
	}

	public void sendUpdateRequestWidget() {
		// ui update
		mainActivity.newRequestNotify(false);

		Intent intent = new Intent(Config.UPDATE_MESSAGE_WIDGET_ACTION);
		// Send Broadcast to Broadcast receiver with message
		context.sendBroadcast(intent);
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

			// send broadcast notify
			final Utility.RReply reply;
			final Intent intentUpdate;

			if (Utility.verifyRequest(newMessage)) {

				reply = Utility.getRequest(newMessage);
				intentUpdate = new Intent(Config.UPDATE_UI);

				(new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						if (reply.getType().equals(Utility.FRIEND)) {
							// send broadcast update ui vs msg
							intentUpdate.putExtra(Config.UPDATE_TYPE,
									Utility.FRIEND);

							intentUpdate.putExtra(Config.UPDATE_ACTION,
									Utility.REQUEST);
							
							Friend fr = PostData.friendGetFriend(context,
									reply.getFromId());
							FriendManager.getInstance().addFriendRequest(fr);

						} else if (reply.getType().equals(Utility.SHARE)) {
							// send broadcast update ui vs msg
							intentUpdate.putExtra(Config.UPDATE_TYPE,
									Utility.SHARE);

							intentUpdate.putExtra(Config.UPDATE_ACTION,
									Utility.REQUEST);

							Friend fr = FriendManager.getInstance().hmMemberFriends
									.get(reply.getFromId());
							fr.setAcceptState(Friend.REQUESTED_SHARE);
 
							FriendManager.getInstance().updateChangeMemberFriend(fr);
						}

						return null;
					}

					protected void onPostExecute(Void result) {
						context.sendBroadcast(intentUpdate);

						SettingManager.getInstance()
								.setNoNewRequest(
										SettingManager.getInstance()
												.getNoNewRequest() + 1);
					};

				}).execute();

				// mainActivity.newRequestNotify(true);
			} else if (Utility.verifyResponse(newMessage)) {
				Log.i(TAG, "response");
// response ---------------------------------------------------------------
				reply = Utility.getResponse(newMessage);
				intentUpdate = new Intent(Config.UPDATE_UI);

				(new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						if (reply.getType().equals(Utility.FRIEND)) {
							Log.i(TAG, "response friend");
							// send broadcast update ui vs msg
							intentUpdate.putExtra(Config.UPDATE_TYPE,
									Utility.FRIEND);
							
							if (reply.isRes()) {
								Log.i(TAG, "response friend yes");
								// response yes
								intentUpdate.putExtra(Config.UPDATE_ACTION,
										Utility.RESPONSE_YES);
								Friend fr = PostData.friendGetFriend(context,
										reply.getFromId());
								FriendManager.getInstance().addFriendMember(fr);
							} else {
								Log.i(TAG, "response friend no");
								// response no
								intentUpdate.putExtra(Config.UPDATE_ACTION,
										Utility.RESPONSE_NO);
							}

						} else if (reply.getType().equals(Utility.SHARE)) {
							
							intentUpdate.putExtra(Config.UPDATE_TYPE,
									Utility.SHARE);
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

						SettingManager.getInstance()
								.setNoNewRequest(
										SettingManager.getInstance()
												.getNoNewRequest() + 1);
					};

				}).execute();

			} else {
				// update msg ui
				intentUpdate = new Intent(Config.UPDATE_UI);
				intentUpdate.putExtra(Config.UPDATE_TYPE, Utility.MESSAGE);

				// ************************************************************************
				// message normal > from
				Message sms = new Message(newMessage, false, 2,
						MyProfileManager.getInstance().mine.getId(), new Date(
								System.currentTimeMillis()));

				// save to database
				sms = dataSource.createMessage(sms);

				// Display message on the screen
				if (iMessage != null)
					iMessage.addNewMessage(sms);

				context.sendBroadcast(intentUpdate);

				SettingManager.getInstance().setNoNewMessage(
						SettingManager.getInstance().getNoNewMesssage() + 1);
				mainActivity.newMessageNotify(true);
			}

			// update by intent broadcast
			Intent intentBC = new Intent(Config.UPDATE_MESSAGE_WIDGET_ACTION);
			// Send Broadcast to Broadcast receiver with message
			context.sendBroadcast(intentBC);

			// Waking up mobile if it is sleeping
			aController.acquireWakeLock(context);

			Toast.makeText(context, "Got Message: " + newMessage,
					Toast.LENGTH_LONG).show();

			// Releasing wake lock
			aController.releaseWakeLock();
		}
	};

	public void deleteMessage(Message message) {
		dataSource.deleteMessage(message);
	}

	public Message createMessage(Message msg) {
		return dataSource.createMessage(msg);
	}

	public void destroy() {
		context.unregisterReceiver(mHandleMessageReceiver);
		dataSource.close();
	}

}
