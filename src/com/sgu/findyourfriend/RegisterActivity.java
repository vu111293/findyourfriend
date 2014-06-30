package com.sgu.findyourfriend;

import com.google.android.gcm.GCMRegistrar;
import com.sgu.findyourfriend.screen.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	// UI elements
	private EditText txtName;
	private EditText txtEmail;

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

		// Get Global Controller Class object (see application tag in AndroidManifest.xml)
		aController = (Controller) getApplicationContext();

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

			ProfileInfo.gcmMyId = GCMRegistrar.getRegistrationId(this);
			
			// Launch Main Activity
			Intent i = new Intent(getApplicationContext(),
					com.sgu.findyourfriend.screen.MainActivity.class);
			startActivity(i);
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

	private void accountRegist(final String name, final String email) {
		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		// reference to ProfileInfo class
		ProfileInfo.gcmMyId = regId;

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
						Intent i = new Intent(getApplicationContext(),
								com.sgu.findyourfriend.screen.MainActivity.class);
						startActivity(i);
					}

				};

				// execute AsyncTask
				mRegisterTask.execute(null, null, null);
			}
		}

	}
}
