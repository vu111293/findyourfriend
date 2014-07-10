package com.sgu.findyourfriend.screen;

import java.util.Date;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sgu.findyourfriend.Config;
import com.sgu.findyourfriend.Controller;
import com.sgu.findyourfriend.ProfileInfo;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.sms.AwesomeAdapter;
import com.sgu.findyourfriend.sms.Message;
import com.sgu.findyourfriend.sms.MessagesDataSource;

public class MessageFragment extends Fragment {

	private static String TAG = "MESSAGE ACTIVITY";

	private List<Message> messages;
	private AwesomeAdapter adapter;
	private EditText text;
	private Button btnSendMessage;
	
	private static Random rand = new Random();

	private Controller aController;
	private Context context;
	private Activity activity;

	private ListView smsListView;

	// your's friend address
	private String toAddr;

	// access database
	private MessagesDataSource dataSource;

	public MessageFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_message, container,
				false);

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity.getApplicationContext();
		this.activity = activity;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// setup database
		dataSource = new MessagesDataSource(context);
		dataSource.open();

		// set receiver
		// toAddr = getIntent().getStringExtra("receiver");
		toAddr = ProfileInfo.gcmMyId;

		text = (EditText) view.findViewById(R.id.text);
		btnSendMessage = (Button) view.findViewById(R.id.btnSendMessage);
		btnSendMessage.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendMessage(v);
			}
		});

		aController = (Controller) context;

		// Check if Internet present
		if (!aController.isConnectingToInternet()) {

			// Internet Connection is not present
			aController.showAlertDialog(context,
					"Internet Connection Error",
					"Please connect to Internet connection", false);
			// stop executing code by return
			return;
		}

		// Register custom Broadcast receiver to show messages on activity
		context.registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Config.DISPLAY_MESSAGE_ACTION));

		// sender = Utility.sender[rand.nextInt(Utility.sender.length - 1)];
		// this.setTitle(sender);

		messages = dataSource.getAllMessages();
		Log.i("MESSAGE", "numbwe sms db: " + messages.size());
		

		adapter = new AwesomeAdapter(context, messages);
		
		// setup listview
		smsListView = (ListView) view.findViewById(R.id.listview);
		smsListView.setAdapter(adapter);
		smsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position,
					long id) {
				Log.i("CLICK", position + "");
				
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("Warning!");
				builder.setMessage("Do you want to delete this message?");
				builder.setPositiveButton("Ok", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dataSource.deleteMessage(messages.get(position));
						messages.remove(messages.get(position));
						adapter.notifyDataSetChanged();
						smsListView.setSelection(messages.size() - 1);
					}
				});
				builder.setNegativeButton("Cancel", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				builder.show();
			}
		});
	}


	public void sendMessage(View v) {
		String newMessage = text.getText().toString().trim();
		if (newMessage.length() > 0) {
			text.setText("");

			Message sms = new Message(newMessage, true, ProfileInfo.gcmMyId,
					toAddr, new Date(System.currentTimeMillis()));
			sms = dataSource.createMessage(sms);
			addNewMessage(sms);
			new SendMessage().execute(newMessage);
		}
	}

	private class SendMessage extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			String regIdFrom = ProfileInfo.gcmMyId;
			String regIdTo = ProfileInfo.gcmMyId;
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

	void addNewMessage(Message m) {
		messages.add(m);
		adapter.notifyDataSetChanged();
		smsListView.setSelection(messages.size() - 1);
	}

	// Create a broadcast receiver to get message and show on screen
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String newMessage = intent.getExtras().getString(
					Config.EXTRA_MESSAGE);

			// Waking up mobile if it is sleeping
			aController.acquireWakeLock(context);

			Message sms = new Message(newMessage, true, "server",
					ProfileInfo.gcmMyId, new Date(System.currentTimeMillis()));
			// save to database
			sms = dataSource.createMessage(sms);

			// Display message on the screen
			addNewMessage(sms);

			Toast.makeText(context,
					"Got Message: " + newMessage, Toast.LENGTH_LONG).show();

			// Releasing wake lock
			aController.releaseWakeLock();
		}
	};
	
	public void onDestroyView() {
		context.unregisterReceiver(mHandleMessageReceiver);
		dataSource.close();
		super.onDestroyView();
	};

}
