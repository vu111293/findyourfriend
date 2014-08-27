package com.sgu.findyourfriend.mgr;

import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.Message;
import com.sgu.findyourfriend.model.TempMessage;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.screen.MainActivity;
import com.sgu.findyourfriend.utils.MessagesDataSource;
import com.sgu.findyourfriend.utils.TempMessagesDataSource;
import com.sgu.findyourfriend.utils.Utility;
import com.sgu.findyourfriend.utils.Utility.RReply;

public class MessageManager {

	public static String TAG = "MessageManager";
	private static MessageManager instance;

	// public static int numNewShareMessage = 0;
	// public static int numNewFriendMessage = 0;
	private List<Message> messages;

	private IMessage iMessage;

	// access database
	private MessagesDataSource dataSource;
	private Context context;

	private static TempMessagesDataSource tempDataSource;

	// private MainActivity mainActivity;

	public void init(MainActivity mainActivity) {
		// this.mainActivity = mainActivity;
		this.context = mainActivity.getApplicationContext();

		this.context.registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Config.DISPLAY_MESSAGE_ACTION));
		this.context.registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Config.LOCAL_MESSAGE_ACTION));

		// setup database
		dataSource = new MessagesDataSource(context);
		dataSource.open();
		// messages = dataSource.getAllMessages();
	}

	public void quickSaveTempMessage(Context ctx, String message) {
		tempDataSource = new TempMessagesDataSource(ctx);
		tempDataSource.open();
		tempDataSource.createMessage(message);
		tempDataSource.close();
	}

	public List<TempMessage> quickGetAllTempMessage(Context ctx) {
		tempDataSource = new TempMessagesDataSource(ctx);
		tempDataSource.open();
		try {

			return tempDataSource.getAllMessages();
		} finally {
			tempDataSource.deleteAllMessage();
			tempDataSource.close();
		}

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
		return dataSource.getAllMessages();
	}

	public List<Message> filterMessage() {

		return messages;
	}

	public void sendMessage(String msg, List<Integer> addrs) {

		for (int addr : addrs) {
			Message sms = new Message(msg, true, MyProfileManager.getInstance()
					.getMyID(), MyProfileManager.getInstance().getMyName(),
					addr, FriendManager.getInstance().hmMemberFriends.get(addr)
							.getUserInfo().getName(), new Date(
							System.currentTimeMillis()));
			sms = dataSource.createMessage(sms);
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
			PostData.sendMessage(context, regIdFrom, regIdTo, message);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// addNewMessage(new Message(text.getText().toString(), false));
			// text.setText("");
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

			// Toast.makeText(context, "Got Message: " + newMessage,
			// Toast.LENGTH_LONG).show();

			// Releasing wake lock
			Utility.releaseWakeLock();
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

					Utility.generateNotification(context,
							"Một yêu cầu kết bạn từ "
									+ fr.getUserInfo().getName(), "");

				} else if (reply.getType().equals(Utility.SHARE)) {
					// send broadcast update ui vs msg
					intentUpdate.putExtra(Config.UPDATE_TYPE, Utility.SHARE);

					intentUpdate
							.putExtra(Config.UPDATE_ACTION, Utility.REQUEST);

					Friend fr = FriendManager.getInstance().hmMemberFriends
							.get(reply.getFromId());
					fr.setAcceptState(Friend.REQUESTED_SHARE);

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
						FriendManager.getInstance().addFriendMember(fr);
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

		// ************************************************************************
		// message normal > from

		int idxPattern = newMessage.indexOf(Config.PARTERN_GET_MESSAGE);
		int senderId;
		String pureMessage;
		String name;

		if (idxPattern > 0) {
			senderId = Integer.parseInt(newMessage.substring(0, idxPattern));
			pureMessage = newMessage.substring(idxPattern
					+ Config.PARTERN_GET_MESSAGE.length());
		} else {
			senderId = 0;
			pureMessage = newMessage;
		}

		name = senderId == 0 ? Config.ADMIN_NAME
				: FriendManager.getInstance().hmMemberFriends.get(senderId)
						.getUserInfo().getName();

		Message sms = new Message(pureMessage, false, senderId, name,
				MyProfileManager.getInstance().getMyID(), MyProfileManager
						.getInstance().getMyName(), new Date(
						System.currentTimeMillis()));

		// save to database
		sms = dataSource.createMessage(sms);

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

		Utility.generateNotification(context, "Bạn có một tin nhắn mới từ "
				+ name, pureMessage);

	}
}
