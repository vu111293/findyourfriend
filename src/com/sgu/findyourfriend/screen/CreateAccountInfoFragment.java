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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.utils.Utility;

public class CreateAccountInfoFragment extends BaseFragment {
	public static final String TAG = CreateAccountInfoFragment.class
			.getSimpleName();
	public static int count = 0;

	private ProgressDialogCustom progress;

	private Button btnSignin;
	private EditText editPhone, editPassword;
	private Context ctx;

	private String phoneNumber;
	private String passwd;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		progress = new ProgressDialogCustom(ctx);

		View rootView = inflater.inflate(R.layout.fragment_create_account,
				container, false);

		editPhone = (EditText) rootView.findViewById(R.id.editPhoneNumber);
		editPassword = (EditText) rootView.findViewById(R.id.editPassword);
		btnSignin = (Button) rootView.findViewById(R.id.btnSignin);

		btnSignin.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				if (Utility.checkConnectToNetworkContinue(getActivity())) {

					final Dialog dialog = new Dialog(getActivity());

					boolean isEmpty = false;

					if (editPhone.getText().toString().trim().length() > 0
							&& isPhoneValidate(editPhone.getText().toString()
									.trim())) {
						editPhone.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.edit_text));
					} else {
						editPhone.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.edit_text_wrong));
						isEmpty = true;
					}

					if (editPassword.getText().toString().trim().length() > 0
							&& editPassword.getText().toString().trim()
									.length() >= Config.MIN_PASSWORD_LENGHT) {
						editPassword.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.edit_text));
					} else {
						editPassword.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.edit_text_wrong));
						isEmpty = true;
					}

					if (isEmpty) {
						Utility.showDialog(Utility.ERROR, dialog,
								"Thiếu thông tin",
								"Nhập các thông tin yêu cầu", "Đóng",
								new OnClickListener() {

									@Override
									public void onClick(View v) {
										dialog.dismiss();
									}
								});
						return;
					}

					(new AsyncTask<Void, Void, Integer>() {

						private String gcmId = "";
						private boolean success = false;

						protected void onPreExecute() {
							progress.show();
							phoneNumber = editPhone.getText().toString().trim();
							passwd = editPassword.getText().toString().trim();
						};

						@Override
						protected Integer doInBackground(Void... params) {

							if (PostData.isPhoneRegisted(getActivity(),
									phoneNumber))
								return Config.PHONE_REGISTED;

							User mine = MyProfileManager.getInstance().mTemp
									.getUserInfo();

							String renameFile = "temp_"
									+ System.currentTimeMillis() + ".png";
							// String selectedImagePath = mine.getAvatar();

							if (!mine.getAvatar().equals("")) {
								PostData.uploadFile(ctx, mine.getAvatar(),
										renameFile);
							} else {
								renameFile = "ic_no_imgprofile.png";
							}

							int id = PostData.userCreate(ctx, mine.getName(),
									PostData.IMAGE_HOST + renameFile, gcmId,
									mine.getGender(), mine.getAddress(), mine
											.getBirthday().toString(), mine
											.getSchool(), mine.getWorkplace(),
									mine.getEmail(), mine.getFblink(), passwd,
									mine.isPublic(), phoneNumber);

							mine.setId(id);
							MyProfileManager.getInstance().mTemp
									.setUserInfo(mine);

							// wait to receive response
							if (id > 0) {
								SettingManager.getInstance()
										.saveLastAccountIdLogin(id);

								return Config.SUCCESS;
							}

							return Config.ERROR;
						}

						@Override
						protected void onPostExecute(Integer result) {
							progress.dismiss();
							if (result == Config.SUCCESS) {

								// save info
								SettingManager.getInstance()
										.savePhoneAutoLogin(phoneNumber);
								SettingManager.getInstance()
										.savePasswordAutoLogin(passwd);

								Utility.showDialog(
										Utility.CONFIRM,
										dialog,
										"Tạo tài khoản thành công",
										"Bạn có muốn tự động đăng nhập vào lần sau",
										"Có", new OnClickListener() {

											@Override
											public void onClick(View v) {
												SettingManager.getInstance()
														.setAutoLogin(true);
												replaceFragment(
														new VerifyEmailFragment(),
														true);
												dialog.dismiss();
											}
										}, "Không", new OnClickListener() {

											@Override
											public void onClick(View v) {
												SettingManager.getInstance()
														.setAutoLogin(false);
												replaceFragment(
														new VerifyEmailFragment(),
														true);
												dialog.dismiss();
											}
										});

							} else if (result == Config.PHONE_REGISTED) {
								editPhone
										.setBackgroundDrawable(getResources()
												.getDrawable(
														R.drawable.edit_text_wrong));

								Utility.showDialog(Utility.ERROR, dialog,
										"Số điện thoại đã sử dụng",
										"Chọn số điện thoại khác.", "Đóng",
										new OnClickListener() {

											@Override
											public void onClick(View v) {
												dialog.dismiss();
											}
										});
							} else {

								Utility.showDialog(Utility.ERROR, dialog,
										"Lỗi",
										"Xãy ra lỗi trong khi tạo tải khoản.",
										"Đóng", new OnClickListener() {

											@Override
											public void onClick(View v) {
												dialog.dismiss();
											}
										});
							}
						}

					}).execute();
				}
			}
		});

		return rootView;
	}

	protected boolean isPhoneValidate(String trim) {
		return true;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		ctx = activity;
	}
}
