package com.sgu.fyf;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.sgu.fyf.friends.Friend;
import com.sgu.fyf.gps.Controller;
import com.sgu.fyf.gps.LocationActivity;
import com.sgu.fyf.phonecall.PhoneCallActivity;
import com.sgu.fyf.sms.MessageActivity;

public class MainActivity extends Activity {
	// label to display gcm messages
	TextView lblMessage;
	Controller aController;

	// setup for send message
	private EditText txtMessage;
	private Button btnSend;
	private Button btnLocation;
	private Button btnRefesh;

	private Spinner spiFriends;
	private ArrayAdapter<String> adapter;

	// Async task
	private AsyncTask<Void, Void, Void> mRegisterTask;
	private AsyncTask<Void, Void, Void> mSendTask;
	private AsyncTask<Void, Void, Void> mGetFriendsTask;
	private List<Friend> friends;
	private List<String> friendNames = new ArrayList<String>();

	// your's friend address
	private String toAddr;

	public static String name;
	public static String email;

	// public static MainActivity context = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get Global Controller Class object (see application tag in
		// AndroidManifest.xml)
		aController = (Controller) getApplicationContext();

		// Check if Internet present
		if (!aController.isConnectingToInternet()) {

			// Internet Connection is not present
			aController.showAlertDialog(MainActivity.this,
					"Internet Connection Error",
					"Please connect to Internet connection", false);
			// stop executing code by return
			return;
		}

		// Getting name, email from intent
		Intent i = getIntent();

		name = i.getStringExtra("name");
		email = i.getStringExtra("email");

		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest permissions was properly set
		GCMRegistrar.checkManifest(this);

		lblMessage = (TextView) findViewById(R.id.lblMessage);

		// Register custom Broadcast receiver to show messages on activity
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Config.DISPLAY_MESSAGE_ACTION));

		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		Log.i("GCM", regId);

		// Check if regid already presents
		if (regId.equals("")) {

			// Register with GCM
			GCMRegistrar.register(this, Config.GOOGLE_SENDER_ID);

		} else {

			// Device is already registered on GCM Server
			if (GCMRegistrar.isRegisteredOnServer(this)) {

				// Skips registration.
				Toast.makeText(getApplicationContext(),
						"Already registered with GCM Server", Toast.LENGTH_LONG)
						.show();

			} else {

				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.

				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {

						// Register on our server
						// On server creates a new user
						aController.register(context, name, email, regId);

						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};

				// execute AsyncTask
				mRegisterTask.execute(null, null, null);
			}
		}

		// setup for send message
		txtMessage = (EditText) findViewById(R.id.txtMessage);
		btnSend = (Button) findViewById(R.id.btnSend);

		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get from address and to address
				// get message

				String message = txtMessage.getText().toString();
				if (!message.trim().equals("")) {
					setupSendMessage(regId, toAddr, message);
				} else {
					Toast.makeText(getApplicationContext(),
							"Please enter message before send it",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		// refesh friend list
		btnRefesh = (Button) findViewById(R.id.btnRefesh);
		btnRefesh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setupGetFriends();
			}
		});

		// go to location screen
		btnLocation = (Button) findViewById(R.id.btnLocation);
		btnLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(MainActivity.this, LocationActivity.class);
				startActivity(i);
			}
		});

		// setup spinner for friend list
		spiFriends = (Spinner) findViewById(R.id.spiFriends);

		adapter = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_spinner_item, friendNames);

		spiFriends.setAdapter(adapter);

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

	}

	private void updateFriendsName() {
		friendNames.clear();
		for (Friend f : friends) {
			friendNames.add(f.getName());
		}
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
				updateFriendsName();
				adapter = new ArrayAdapter<String>(getApplicationContext(),
						android.R.layout.simple_spinner_item, friendNames);

				spiFriends.setAdapter(adapter);

			}

		};

		// execute AsyncTask
		mGetFriendsTask.execute(null, null, null);

	}

	private void setupSendMessage(final String regIdFrom, final String regIdTo,
			final String message) {
		final Context context = this;

		mSendTask = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				// Register on our server
				// On server creates a new user
				aController.sendMessage(context, regIdFrom, regIdTo, message);

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mSendTask = null;
			}

		};

		// execute AsyncTask
		mSendTask.execute(null, null, null);
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
			lblMessage.append(newMessage + "\n");

			Toast.makeText(getApplicationContext(),
					"Got Message: Br" + newMessage, Toast.LENGTH_LONG).show();

			// Releasing wake lock
			aController.releaseWakeLock();
		}
	};

	@Override
	protected void onDestroy() {
		// Cancel AsyncTask
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			// Unregister Broadcast Receiver
			unregisterReceiver(mHandleMessageReceiver);

			// Clear internal resources.
			GCMRegistrar.onDestroy(this);

		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.smsItem:
			i = new Intent(MainActivity.this, MessageActivity.class);
			startActivity(i);
			return true;
			
		case R.id.callItem:
			i = new Intent(MainActivity.this, PhoneCallActivity.class);
			startActivity(i);
			return true;
			
		case R.id.mapItem:
			i = new Intent(MainActivity.this, LocationActivity.class);
			startActivity(i);
			return true;
			default:
				return false;
		}
	}

}
