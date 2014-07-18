package com.sgu.findyourfriend.screen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gcm.GCMRegistrar;
import com.sgu.findyourfriend.Config;
import com.sgu.findyourfriend.FriendManager;
import com.sgu.findyourfriend.MyProfileManager;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.utils.Controller;

public class RegisterActivity extends Activity {

	// UI elements
	private EditText txtName;
	private EditText txtEmail;

	// private FrameLayout fwait;

	// creates a ViewSwitcher object, to switch between Views
	private ViewSwitcher viewSwitcher;

	// Async task
	private AsyncTask<Void, Void, Void> mRegisterTask;

	// Controller
	private Controller aController;

	// Register button
	private Button btnRegister;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		// Get Global Controller Class object (see application tag in
		// AndroidManifest.xml)
		aController = (Controller) getApplicationContext();
		// fwait = (FrameLayout) findViewById(R.id.fwait);

		// Check if Internet Connection present
		if (!aController.isConnectingToInternet()) {

			// Internet Connection is not present
			aController.showAlertDialog(RegisterActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);

			// stop executing code by return
			return;
		}

		// Check if GCM configuration is set
		if (Config.YOUR_SERVER_URL == null || Config.GOOGLE_SENDER_ID == null
				|| Config.YOUR_SERVER_URL.length() == 0
				|| Config.GOOGLE_SENDER_ID.length() == 0) {

			// GCM sernder id / server url is missing
			aController.showAlertDialog(RegisterActivity.this,
					"Configuration Error!",
					"Please set your Server URL and GCM Sender ID", false);

			// stop executing code by return
			return;
		}

		// check register
		if (GCMRegistrar.isRegisteredOnServer(this)) {
			new PrepareDataTask().execute();
		}

		txtName = (EditText) findViewById(R.id.txtName);
		txtEmail = (EditText) findViewById(R.id.txtEmail);
		btnRegister = (Button) findViewById(R.id.btnRegister);

		// Click event on Register button
		btnRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Get data from EditText
				String name = txtName.getText().toString();
				String email = txtEmail.getText().toString();

				// Check if user filled the form
				if (name.trim().length() > 0 && email.trim().length() > 0) {

					// save info
//					MyProfileManager.name = name;
//					MyProfileManager.email = email;

					// setup account
					accountRegist(name, email);

					finish();

				} else {
					// user doen't filled that data
					aController.showAlertDialog(RegisterActivity.this,
							"Registration Error!", "Please enter your details",
							false);
				}
			}
		});
	}

	private class PrepareDataTask extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... vd) {
			
			// new LoadViewTask().execute();
			
			// fwait.setVisibility(View.VISIBLE);
			// init profile
			/* ProfileInfo.instance = new ProfileInfo(getApplicationContext());
			ProfileInfo.gcmMyId = GCMRegistrar
					.getRegistrationId(getApplicationContext());

			// init friend manager
			FriendManager.instance = new FriendManager(getApplicationContext()); */
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// fwait.setVisibility(View.GONE);
			
			// Launch Main Activity
			Intent i = new Intent(getApplicationContext(),
					com.sgu.findyourfriend.screen.MainActivity.class);
			startActivity(i);
		}
	}

	private void accountRegist(final String name, final String email) {
		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		// reference to ProfileInfo class
//		MyProfileManager.gcmMyId = regId;

		Log.i("GCM", regId);

		// Check if regid already presents
		if (regId.equals("")) {

			// Register with GCM
			GCMRegistrar.register(this, Config.GOOGLE_SENDER_ID);

			Log.i("GCM", "registing");

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

						// start main activity
						Intent i = new Intent(
								getApplicationContext(),
								com.sgu.findyourfriend.screen.MainActivity.class);
						startActivity(i);
					}

				};

				// execute AsyncTask
				mRegisterTask.execute(null, null, null);
			}
		}

	}

	// To use the AsyncTask, it must be subclassed
	private class LoadViewTask extends AsyncTask<Void, Integer, Void> {
		// A TextView object and a ProgressBar object
		private TextView tv_progress;
		private ProgressBar pb_progressBar;

		// Before running code in the separate thread
		@Override
		protected void onPreExecute() {
			// Initialize the ViewSwitcher object
			viewSwitcher = new ViewSwitcher(RegisterActivity.this);
			/*
			 * Initialize the loading screen with data from the
			 * 'loadingscreen.xml' layout xml file. Add the initialized View to
			 * the viewSwitcher.
			 */
			viewSwitcher.addView(ViewSwitcher.inflate(
					RegisterActivity.this, R.layout.loadingscreen, null));

			// Initialize the TextView and ProgressBar instances - IMPORTANT:
			// call findViewById() from viewSwitcher.
			tv_progress = (TextView) viewSwitcher
					.findViewById(R.id.tv_progress);
			pb_progressBar = (ProgressBar) viewSwitcher
					.findViewById(R.id.pb_progressbar);
			// Sets the maximum value of the progress bar to 100
			pb_progressBar.setMax(100);

			// Set ViewSwitcher instance as the current View.
			setContentView(viewSwitcher);
		}

		// The code to be executed in a background thread.
		@Override
		protected Void doInBackground(Void... params) {
			/*
			 * This is just a code that delays the thread execution 4 times,
			 * during 850 milliseconds and updates the current progress. This is
			 * where the code that is going to be executed on a background
			 * thread must be placed.
			 */
			try {
				// Get the current thread's token
				synchronized (this) {
					// Initialize an integer (that will act as a counter) to
					// zero
					int counter = 0;
					// While the counter is smaller than four
					while (counter <= 4) {
						// Wait 850 milliseconds
						this.wait(850);
						// Increment the counter
						counter++;
						// Set the current progress.
						// This value is going to be passed to the
						// onProgressUpdate() method.
						publishProgress(counter * 25);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		// Update the TextView and the progress at progress bar
		@Override
		protected void onProgressUpdate(Integer... values) {
			// Update the progress at the UI if progress value is smaller than
			// 100
			if (values[0] <= 100) {
				tv_progress.setText("Progress: " + Integer.toString(values[0])
						+ "%");
				pb_progressBar.setProgress(values[0]);
			}
		}

		// After executing the code in the thread
		@Override
		protected void onPostExecute(Void result) {
			/*
			 * Initialize the application's main interface from the 'main.xml'
			 * layout xml file. Add the initialized View to the viewSwitcher.
			 */
			viewSwitcher.addView(ViewSwitcher.inflate(
					RegisterActivity.this, R.layout.activity_register, null));
			// Switch the Views
			viewSwitcher.showNext();
		}

	}

}
