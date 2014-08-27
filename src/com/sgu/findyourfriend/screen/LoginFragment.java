package com.sgu.findyourfriend.screen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.MyProfileManager;
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

				if (ID.getText().toString().trim().length() > 0
						&& Password.getText().toString().trim().length() > 0) {

					if (Config.MODE_OFFLINE) {
						
						MyProfileManager.getInstance().init(ctx,
								38, true);
						
						// start main app
						Intent i = new Intent(
								ctx,
								com.sgu.findyourfriend.screen.MainActivity.class);
						startActivity(i);
						
						getActivity().finish();
						return;
					}
					
					
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
							int id = PostData.login(ctx, phoneNumber, password);

							Log.i(TAG, "Loginnnnnnnnnnnnnnnnn: " + id);

							if (id > 0) {
								SettingManager.getInstance()
										.saveLastAccountIdLogin(id);

								if (GCMRegistrar.isRegistered(ctx)) {
									gcmId = GCMRegistrar.getRegistrationId(ctx);
									PostData.updateGcmId(ctx, id, gcmId);
									MyProfileManager.getInstance().init(ctx,
											id, true);
									Log.i(TAG, "registed with  " + gcmId);
								} else {
									Log.i(TAG, "empty gcmid");
									MyProfileManager.getInstance().init(ctx,
											id, true);
									GCMRegistrar.register(ctx,
											Config.GOOGLE_SENDER_ID);
								}

								// if (!gcmId.equals("")) {
								// Log.i(TAG, "update gcmid");
								// PostData.updateGcmId(ctx, id, gcmId);
								// }

								// MyProfileManager.getInstance().init(ctx, id,
								// true);
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
								Utility.showAlertDialog(
										ctx,
										"Cảnh báo",
										"Tài khoản chưa được kích hoạt, xin kiểm tra mail để kích hoạt",
										false);

							} else {
								// show error
								Utility.showAlertDialog(ctx, "Cảnh báo",
										"Thông tin không chính xác", false);
							}
							progress.dismiss();
						}

					}).execute();
				} else {
					// truong rong
					Utility.showMessage(ctx, "Xin nhập đủ thông tin");
				}

			}
		});

		Signin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				replaceFragment(new CreateBasicInfoFragment(), true);
			}
		});

		Forgetpassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				replaceFragment(new ForgetPasswordFragment(), true);
			}
		});

		// // hide keyboard
		// InputMethodManager imm = (InputMethodManager) getActivity()
		// .getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(ID.getWindowToken(), 0);

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		ctx = activity;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Bundle bundle = this.getArguments();
		// if (null != bundle && bundle.getBoolean("error", false)) {
		// Utility.showAlertDialog(getActivity(), "Lỗi đăng nhập",
		// "Không thể đăng nhập tự động.", true);
		//
		// }

		// Check if Internet Connection present
		// if (!Utility.isConnectingToInternet(getActivity())) {
		// Utility.showAlertDialog(ctx, "Lỗi kết nối mạng",
		// "Kiểm tra lại kết nối mạng và quay lại sau.", false);
		// return;
		// }

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
