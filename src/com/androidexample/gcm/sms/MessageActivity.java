package com.androidexample.gcm.sms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidexample.gcm.Config;
import com.androidexample.gcm.Controller;
import com.androidexample.gcm.Friend;
import com.androidexample.gcm.R;
import com.google.android.gcm.GCMRegistrar;

/**
 * MessageActivity is a main Activity to show a ListView containing Message
 * items
 * 
 * @author Adil Soomro
 * 
 */
public class MessageActivity extends ListActivity {
	/** Called when the activity is first created. */

	ArrayList<Message> messages;
	AwesomeAdapter adapter;
	EditText text;
	static Random rand = new Random();
	static String sender;

	private Controller aController;
	private String regId;
	private Context context;

	private AsyncTask<Void, Void, Void> mSendTask;
	private Spinner spiFriends;
	private ArrayAdapter<String> adapterFNames;
	private List<Friend> friends;
	private List<String> friendNames = new ArrayList<String>();

	// your's friend address
	private String toAddr;
	private AsyncTask<Void, Void, Void> mGetFriendsTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messager);
		context = getApplicationContext();
		text = (EditText) this.findViewById(R.id.text);

		aController = (Controller) getApplicationContext();

		// Check if Internet present
		if (!aController.isConnectingToInternet()) {

			// Internet Connection is not present
			aController.showAlertDialog(MessageActivity.this,
					"Internet Connection Error",
					"Please connect to Internet connection", false);
			// stop executing code by return
			return;
		}

		// Register custom Broadcast receiver to show messages on activity
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Config.DISPLAY_MESSAGE_ACTION));

		// Get GCM registration id
		regId = GCMRegistrar.getRegistrationId(this);

		sender = Utility.sender[rand.nextInt(Utility.sender.length - 1)];
		this.setTitle(sender);
		messages = new ArrayList<Message>();
		messages.add(new Message("", false, true));
		messages.add(new Message("", false, true));
		messages.add(new Message("", false, true));
		messages.add(new Message("", false, true));
		messages.add(new Message("", false, true));
		messages.add(new Message("", false, true));
		messages.add(new Message("", false, true));
		
		adapter = new AwesomeAdapter(this, messages);
		setListAdapter(adapter);

		// setup spinner for friend list
		spiFriends = (Spinner) findViewById(R.id.spiFriends);

		adapterFNames = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_spinner_item, friendNames);

		spiFriends.setAdapter(adapterFNames);

		spiFriends.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// set address for your's friend want send to
				toAddr = friends.get(pos).getRegId();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
		
		// load friend list
		setupGetFriends();
	}

	private void setupGetFriends() {
		final Context context = this;

		mGetFriendsTask = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				// Register on our server
				// On server creates a new user
				friends = aController.getFriendsName(context);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mGetFriendsTask = null;
				friendNames.clear();
				for (Friend f : friends) {
					friendNames.add(f.getName());
				}

				adapterFNames = new ArrayAdapter<String>(
						getApplicationContext(),
						android.R.layout.simple_spinner_item, friendNames);

				spiFriends.setAdapter(adapterFNames);

			}

		};

		// execute AsyncTask
		mGetFriendsTask.execute(null, null, null);

	}

	public void sendMessage(View v) {
		String newMessage = text.getText().toString().trim();
		if (newMessage.length() > 0) {
			text.setText("");
			addNewMessage(new Message(newMessage, true));
			new SendMessage().execute(newMessage);
		}
	}

	private class SendMessage extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			String regIdFrom = regId;
			String regIdTo = regId;
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
		getListView().setSelection(messages.size() - 1);
	}

	// Create a broadcast receiver to get message and show on screen
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String newMessage = intent.getExtras().getString(
					Config.EXTRA_MESSAGE);

			// Waking up mobile if it is sleeping
			aController.acquireWakeLock(getApplicationContext());

			// Display message on the screen
			addNewMessage(new Message(newMessage, false));

			Toast.makeText(getApplicationContext(),
					"Got Message: " + newMessage, Toast.LENGTH_LONG).show();

			// Releasing wake lock
			aController.releaseWakeLock();
		}
	};

	protected void onDestroy() {
		unregisterReceiver(mHandleMessageReceiver);
		super.onDestroy();

	}

}