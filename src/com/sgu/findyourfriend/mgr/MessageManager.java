package com.sgu.findyourfriend.mgr;

import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.widget.Toast;

import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.Message;
import com.sgu.findyourfriend.screen.MainActivity;
import com.sgu.findyourfriend.utils.Controller;
import com.sgu.findyourfriend.utils.MessagesDataSource;

public class MessageManager {

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

	// public Message sendMessage(String msg, List<Friend> friendsId, int k) {
	// Message sms = new Message(msg, true,
	// MyProfileManager.getInstance().mine.getId(),
	// MyProfileManager.getInstance().mine.getGcmId(),
	// MyProfileManager.getInstance().mine.getGcmId(), new Date(
	// System.currentTimeMillis()));
	// sms = dataSource.createMessage(sms);
	// iMessage.addNewMessage(sms);
	// new SendMessage().execute(msg);
	// return sms;
	// }

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
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(
					Config.EXTRA_MESSAGE);

			// check message here !
			// update num msg for widget
			SettingManager.getInstance().init(context);

			if (newMessage.startsWith(Config.PREFIX)) {
				SettingManager.getInstance().setNoNewRequest(
						SettingManager.getInstance().getNoNewRequest() + 1);
				mainActivity.newRequestNotify(true);
			} else {
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

			int idSender = 2; // get from message content

			Message sms = new Message(newMessage, false, idSender,
					MyProfileManager.getInstance().mine.getId(), new Date(
							System.currentTimeMillis()));

			// save to database
			sms = dataSource.createMessage(sms);

			// Display message on the screen
			if (iMessage != null)
				iMessage.addNewMessage(sms);

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
	}

}
