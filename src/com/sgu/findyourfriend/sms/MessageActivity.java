package com.sgu.findyourfriend.sms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.sgu.findyourfriend.Config;
import com.sgu.findyourfriend.Controller;
import com.sgu.findyourfriend.ProfileInfo;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mem.Friend;

/**
 * MessageActivity is a main Activity to show a ListView containing Message
 * items
 * 
 * @author Adil Soomro
 * 
 */
public class MessageActivity extends ListActivity implements OnItemClickListener {
	/** Called when the activity is first created. */

	private static String TAG = "MESSAGE ACTIVITY";
	
	private List<Message> messages;
	private AwesomeAdapter adapter;
	private EditText text;
	private  static Random rand = new Random();
	private static String sender;

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

	
	// access database
	private MessagesDataSource dataSource;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messager);
		
		// setup database
		dataSource = new MessagesDataSource(this);
		dataSource.open();
		
		/*for (int i = 0; i < 100; ++i) {
			Message sms = new Message("this is message test " + i,
					true, ProfileInfo.gcmMyId, ProfileInfo.gcmMyId,
					new Date(System.currentTimeMillis()));
			dataSource.createMessage(sms);
			
			Log.i(TAG, "log message " + i);
		}*/
		
		

		// set receiver
		toAddr = getIntent().getStringExtra("receiver");
		
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
		
		
		messages = dataSource.getAllMessages();
		
		/* messages = new ArrayList<Message>();
		messages.add(new Message("", false, true));
		messages.add(new Message("", false, true));
		messages.add(new Message("", false, true));
		messages.add(new Message("", false, true));
		messages.add(new Message("", false, true));
		messages.add(new Message("", false, true));
		messages.add(new Message("", false, true));
		 */
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

		
		// set onClickItem for listview
		getListView().setOnItemClickListener(this);
	}

	public void sendMessage(View v) {
		String newMessage = text.getText().toString().trim();
		if (newMessage.length() > 0) {
			text.setText("");
			
			Message sms = new Message(newMessage, true,
					ProfileInfo.gcmMyId, toAddr, new Date(System.currentTimeMillis()));
			sms = dataSource.createMessage(sms);
			addNewMessage(sms);
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

			Message sms = new Message(newMessage, true,
					"server" ,ProfileInfo.gcmMyId, new Date(System.currentTimeMillis()));
			// save to database
			sms = dataSource.createMessage(sms);
			
			// Display message on the screen
			addNewMessage(sms);

			Toast.makeText(getApplicationContext(),
					"Got Message: " + newMessage, Toast.LENGTH_LONG).show();

			// Releasing wake lock
			aController.releaseWakeLock();
		}
	};

	protected void onDestroy() {
		unregisterReceiver(mHandleMessageReceiver);
		dataSource.close();
		super.onDestroy();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose task");
		builder.setMessage("Do you want to delete this message?");
		builder.setPositiveButton("Ok", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dataSource.deleteMessage(messages.get(position));
				messages.remove(messages.get(position));
				adapter.notifyDataSetChanged();
				getListView().setSelection(messages.size() - 1);
				
			}
		});
		builder.setNegativeButton("Cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.show();
	}
	
	

}