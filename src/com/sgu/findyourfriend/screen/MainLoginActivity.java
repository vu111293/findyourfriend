package com.sgu.findyourfriend.screen;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gcm.GCMRegistrar;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.utils.Controller;

public class MainLoginActivity extends Activity {

	private Controller mController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_login_layout);

		// init Setting here
		SettingManager.getInstance().init(getApplicationContext());

		mController = (Controller) getApplicationContext();

		// Check if Internet Connection present
		if (!mController.isConnectingToInternet()) {
			mController.showAlertDialog(MainLoginActivity.this,
					"Lỗi kết nối mạng",
					"Kiểm tra lại kết nối mạng và quay lại sau.", false);
			return;
		}

		// setScreen login waitting
		if (SettingManager.getInstance().isAutoLogin()) {
			// try login;
			(new AsyncTask<Void, Void, Boolean>() {

				private String phoneNumber;
				private String password;
				private String gcmId = "";

				@Override
				protected void onPreExecute() {
					phoneNumber = SettingManager.getInstance()
							.getPhoneAutoLogin();
					password = SettingManager.getInstance()
							.getPasswordAutoLogin();
					if (GCMRegistrar
							.isRegisteredOnServer(getApplicationContext())) {
						gcmId = GCMRegistrar
								.getRegistrationId(getApplicationContext());
					}
				}

				@Override
				protected Boolean doInBackground(Void... arg0) {

					if (gcmId.equals("")) {
						GCMRegistrar.register(getApplicationContext(),
								Config.GOOGLE_SENDER_ID);
					} else if (!GCMRegistrar
							.isRegisteredOnServer(getApplicationContext())) {
						mController.register(getApplicationContext(),
								phoneNumber, password, gcmId);
					}

					// login
					int id = PostData.login(getApplicationContext(),
							phoneNumber, password);

					if (id >= 0) {
						MyProfileManager.getInstance().init(
								getApplicationContext(), id);
						return true;
					}

					return false;

				}

				@Override
				protected void onPostExecute(Boolean isOk) {

					if (isOk) {
						Intent i = new Intent(
								getApplicationContext(),
								com.sgu.findyourfriend.screen.MainActivity.class);
						startActivity(i);
						finish();
					} else {
						// show error
						LoginFragment fragment = new LoginFragment();
						Bundle bundle = new Bundle();
						bundle.putBoolean("error", true);
						fragment.setArguments(bundle);
						showScreen(fragment);
					}
				}

			}).execute();
		} else {
			showScreen(new LoginFragment());
		}
	}

	private void showScreen(Fragment fragment) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.container_framelayout, fragment);
		ft.commit();
	}

}
