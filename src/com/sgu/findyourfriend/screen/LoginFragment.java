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

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SQLiteDatabaseManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.utils.Utility;

public class LoginFragment extends BaseFragment {

	private static String TAG = "LoginFragment";
	private ProgressDialogCustom progress;
	private EditText ID, Password;
	private Button BtnLogin, Signin;
	private TextView Forgetpassword;
	private Context ctx;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.activity_login, container,
				false);

		ID = (EditText) rootView.findViewById(R.id.EditText_Login_ID);
		Password = (EditText) rootView
				.findViewById(R.id.EditText_Login_Password);
		BtnLogin = (Button) rootView.findViewById(R.id.Button_Login_BtnLogin);
		Signin = (Button) rootView.findViewById(R.id.TextView_Login_Register);
		Forgetpassword = (TextView) rootView
				.findViewById(R.id.TextView_Login_ForgetPassword);

		ID.setText(SettingManager.getInstance().getPhoneAutoLogin());
		Password.setText(SettingManager.getInstance().getPasswordAutoLogin());

		BtnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!Utility.checkConnectToNetworkContinue(getActivity()))
					return;

				final Dialog dialog = new Dialog(getActivity());

				if (ID.getText().toString().trim().length() > 0
						&& Password.getText().toString().trim().length() > 0) {

					if (Config.MODE_OFFLINE) {

						MyProfileManager.getInstance().init(ctx, 38, true);

						// start main app
						Intent i = new Intent(
								ctx,
								com.sgu.findyourfriend.screen.MainActivity.class);
						startActivity(i);

						getActivity().finish();

					} else {

						(new AsyncTask<Void, Void, Integer>() {

							private String phoneNumber;
							private String password;
							private String gcmId = "";

							@Override
							protected void onPreExecute() {
								progress = new ProgressDialogCustom(ctx);
								progress.show();

								phoneNumber = ID.getText().toString().trim();
								password = Password.getText().toString().trim();
							}

							@Override
							protected Integer doInBackground(Void... params) {

								// login
								int id = PostData.login(ctx, phoneNumber,
										password);

								Log.i(TAG, "Loginnnnnnnnnnnnnnnnn: " + id);

								if (id > 0) {
									SettingManager.getInstance()
											.saveLastAccountIdLogin(id);

									if (GCMRegistrar.isRegistered(ctx)) {
										gcmId = GCMRegistrar
												.getRegistrationId(ctx);
										PostData.updateGcmId(ctx, id, gcmId);

										// // init sqlite manager
										// SQLiteDatabaseManager.getInstance().init(ctx);

										MyProfileManager.getInstance().init(
												ctx, id, true);
										Log.i(TAG, "registed with  " + gcmId);
									} else {
										Log.i(TAG, "empty gcmid");

										// // init sqlite manager
										// SQLiteDatabaseManager.getInstance().init(ctx);

										MyProfileManager.getInstance().init(
												ctx, id, true);
										GCMRegistrar.register(ctx,
												Config.GOOGLE_SENDER_ID);
									}

									return Config.SUCCESS;
								} else if (id == -1)
									return Config.ERROR_REGIST;
								return Config.ERROR_NOT_FOUND;
							}

							@Override
							protected void onPostExecute(Integer result) {
								if (result == Config.SUCCESS) {
									// save info
									SettingManager.getInstance()
											.savePhoneAutoLogin(phoneNumber);
									SettingManager.getInstance()
											.savePasswordAutoLogin(password);

									// start main app
									Intent i = new Intent(
											ctx,
											com.sgu.findyourfriend.screen.MainActivity.class);
									startActivity(i);

									getActivity().finish();

								} else if (result == Config.ERROR_REGIST) {
									// show error

									Utility.showDialog(
											Utility.WARNING,
											dialog,
											"Cảnh báo",
											"Tài khoản chưa được kích hoạt, xin kiểm tra mail để kích hoạt.",
											"Gửi", new OnClickListener() {

												@Override
												public void onClick(View v) {

													(new AsyncTask<Void, Void, Void>() {

														@Override
														protected void onPreExecute() {
															progress.show();
														}

														@Override
														protected Void doInBackground(
																Void... params) {
															PostData.resendEmail(
																	getActivity(),
																	phoneNumber);
															return null;
														}

														@Override
														protected void onPostExecute(
																Void result) {
															Utility.showMessage(
																	getActivity(),
																	"Một email đã gửi đến hộp thư của bạn.");
															progress.dismiss();
															dialog.dismiss();
														}

													}).execute();

													dialog.dismiss();
												}
											}, "Đóng", new OnClickListener() {

												@Override
												public void onClick(View v) {
													dialog.dismiss();
												}
											});

								} else {
									// show error
									Utility.showDialog(
											Utility.ERROR,
											dialog,
											"Lỗi đăng nhập",
											"Số điện thoại hoặc mật khẩu không chính xác.",
											"Đóng", new OnClickListener() {

												@Override
												public void onClick(View v) {
													dialog.dismiss();
												}
											});
								}
								progress.dismiss();
							}

						}).execute();
					}
				} else {
					// truong rong
					Utility.showDialog(Utility.ERROR, dialog,
							"Thiếu thông tin", "Xin nhập đủ thông tin.",
							"Đóng", new OnClickListener() {

								@Override
								public void onClick(View v) {
									dialog.dismiss();
								}
							});
				}

			}
		});

		Signin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				replaceFragment(new CreateBasicInfoFragment(), true);
				// replaceFragment(new VerifyEmailFragment(), true);
			}
		});

		Forgetpassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				replaceFragment(new ForgetPasswordFragment(), true);
			}
		});

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		ctx = activity;
	}

	@Override
	public void onStop() {
		super.onStop();
		if (progress != null) {
			if (progress.isShowing()) {
				progress.dismiss();
			}
		}
	}

}
