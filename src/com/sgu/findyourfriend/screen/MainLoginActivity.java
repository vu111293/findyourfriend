package com.sgu.findyourfriend.screen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;

import com.google.android.gcm.GCMRegistrar;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.utils.Utility;

public class MainLoginActivity extends FragmentActivity {

	private static String TAG = "MainLoginActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.main_auto_login);

		// init Setting here
		SettingManager.getInstance().init(getApplicationContext());

		// Check if Internet Connection present
		if (!Utility.isConnectingToInternet(getApplicationContext())) {
			showAlertDialogNotConnectNetwork();
		}
		//
		// if
		// (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext())
		// != ConnectionResult.SUCCESS) {
		// showAlertNotService();
		// }

		// setScreen login waitting
		if (SettingManager.getInstance().isAutoLogin()
				&& !getIntent().hasExtra("fromLogout")) {
			
			if (Config.MODE_OFFLINE) {
				
				MyProfileManager.getInstance().init(getApplicationContext(),
						38, true);
				
				// start main app
				Intent i = new Intent(
						getApplicationContext(),
						com.sgu.findyourfriend.screen.MainActivity.class);
				startActivity(i);
				
				finish();
				return;
			}
			
			
			
			
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
				}

				@Override
				protected Boolean doInBackground(Void... arg0) {

					// login
					int id = PostData.login(getApplicationContext(),
							phoneNumber, password);

					if (id > 0) {
						SettingManager.getInstance().saveLastAccountIdLogin(id);
						if (GCMRegistrar.isRegistered(getApplicationContext())) {
							gcmId = GCMRegistrar
									.getRegistrationId(getApplicationContext());
							PostData.updateGcmId(getApplicationContext(), id,
									gcmId);
							MyProfileManager.getInstance().init(
									getApplicationContext(), id, true);
							Log.i(TAG, "registed with  " + gcmId);
						} else {
							Log.i(TAG, "empty gcmid");
							MyProfileManager.getInstance().init(
									getApplicationContext(), id, true);
							GCMRegistrar.register(getApplicationContext(),
									Config.GOOGLE_SENDER_ID);
						}

//						if (!gcmId.equals("")) {
//							Log.i(TAG, "update gcmid");
//							PostData.updateGcmId(getApplicationContext(), id,
//									gcmId);
//						}

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
						showAlertManualLogin();
						LoginFragment fragment = new LoginFragment();
						Bundle bundle = new Bundle();
						bundle.putBoolean("error", true);
						fragment.setArguments(bundle);
						showScreen(fragment);
					}
				}

			}).execute();
		} else {
			// //
			// (new AsyncTask<Void, Void, Void> () {
			//
			// @Override
			// protected Void doInBackground(Void... params) {
			// // MyProfileManager.getInstance().init(getApplicationContext(),
			// 47);
			// PostData.friendGetFriend(getApplicationContext(), 41);
			// // PostData.userGetUserById(getApplicationContext(), 47);
			// return null;
			// }
			//
			// }).execute();

			showScreen(new LoginFragment());
		}
	}

	@SuppressWarnings("deprecation")
	private void showAlertManualLogin() {
		AlertDialog alertDialog = new AlertDialog.Builder(
				MainLoginActivity.this).create();

		alertDialog.setTitle("Cảnh báo");
		alertDialog
				.setMessage("Không thể đăng nhập tự động. Đăng nhập thủ công");

		// Set OK Button
		alertDialog.setButton("Đồng ý", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				showScreen(new LoginFragment());
			}
		});

		// Show Alert Message
		alertDialog.show();
	}

	@SuppressWarnings("deprecation")
	private void showAlertNotService() {
		AlertDialog alertDialog = new AlertDialog.Builder(
				MainLoginActivity.this).create();

		alertDialog.setTitle("Cảnh báo");
		alertDialog.setMessage("Thiết bị không được hỗ trợ");

		// Set OK Button
		alertDialog.setButton("Thoát", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		// Show Alert Message
		alertDialog.show();
	}

	private void showScreen(Fragment fragment) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.container_framelayout, fragment);
		ft.commit();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@SuppressWarnings("deprecation")
	private void showAlertDialogNotConnectNetwork() {
		AlertDialog alertDialog = new AlertDialog.Builder(
				MainLoginActivity.this).create();

		alertDialog.setTitle("Lỗi kết nối mạng");
		alertDialog.setMessage("Kiểm tra lại kết nối mạng và quay lại sau.");

		// Set OK Button
		alertDialog.setButton("Thoát", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		// Show Alert Message
		alertDialog.show();
	}
}
