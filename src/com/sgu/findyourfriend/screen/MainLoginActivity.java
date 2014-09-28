/*
 * Copyright (C) 2014 Tubor Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sgu.findyourfriend.screen;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.google.android.gcm.GCMRegistrar;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SQLiteDatabaseManager;
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

		// init sqlite manager
		SQLiteDatabaseManager.getInstance().init(getApplicationContext());

		// Check if Internet Connection present
		// Utility.checkConnectToNetworkForceQuit(this);

		// option dialog online or offline
		if (Utility.isConnectingToInternet(this)) {

			// setScreen login waitting
			if (SettingManager.getInstance().isAutoLogin()
					&& !getIntent().hasExtra("fromLogout")) {

				if (Config.MODE_OFFLINE) {

					MyProfileManager.getInstance().init(
							getApplicationContext(), 38, true);

					// start main app
					Intent i = new Intent(getApplicationContext(),
							com.sgu.findyourfriend.screen.MainActivity.class);
					startActivity(i);

					finish();
				} else {
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
								SettingManager.getInstance()
										.saveLastAccountIdLogin(id);
								if (GCMRegistrar
										.isRegistered(getApplicationContext())) {
									gcmId = GCMRegistrar
											.getRegistrationId(getApplicationContext());
									PostData.updateGcmId(
											getApplicationContext(), id, gcmId);
									// // init sqlite manager
									// SQLiteDatabaseManager.getInstance().init(getApplicationContext());

									MyProfileManager.getInstance().init(
											getApplicationContext(), id, true);
									Log.i(TAG, "registed with  " + gcmId);
								} else {
									Log.i(TAG, "empty gcmid");

									// // init sqlite manager
									// SQLiteDatabaseManager.getInstance().init(getApplicationContext());

									MyProfileManager.getInstance().init(
											getApplicationContext(), id, true);
									GCMRegistrar.register(
											getApplicationContext(),
											Config.GOOGLE_SENDER_ID);
								}

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
								// showAlertManualLogin();
								Utility.showDialog(Utility.CONFIRM, new Dialog(
										MainLoginActivity.this), "Lỗi",
										"Không thể đăng nhập tự động.",
										"Đăng nhập thủ công",
										new OnClickListener() {

											@Override
											public void onClick(View v) {
												showScreen(new LoginFragment());
											}
										});
							}
						}

					}).execute();
				}

			} else {
				showScreen(new LoginFragment());
			}
		} else {
			// show option dialog
			optionOfflineDialog();
		}

	}

	private void optionOfflineDialog() {
		final Dialog dialog = new Dialog(MainLoginActivity.this);

		// if (SQLiteDatabaseManager.getInstance().isDataBaseExist())
		Utility.showDialog(
				Utility.WARNING,
				dialog,
				"Không có kết nối mạng",
				"Thiết bị chưa được kết nối mạng. Bạn có muốn kiểm tra cái đặt wifi?",
				"Đồng ý", new OnClickListener() {

					@Override
					public void onClick(View v) {
						showScreen(new LoginFragment());
						startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
						dialog.dismiss();
						// finish();
					}
				}, "Thoát", new OnClickListener() {

					@Override
					public void onClick(View v) {

						dialog.dismiss();
						finish();
					}
				});
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
}
