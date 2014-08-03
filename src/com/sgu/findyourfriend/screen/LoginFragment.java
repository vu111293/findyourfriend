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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.utils.Controller;
import com.sgu.findyourfriend.utils.Utility;

public class LoginFragment extends BaseFragment {

	private ProgressDialogCustom progress;
	private EditText ID, Password;
	private Button BtnLogin, Signin;
	private TextView Forgetpassword;
	private Context ctx;

	private Controller mController;

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

		BtnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (ID.getText().toString().trim().length() > 0
						&& Password.getText().toString().trim().length() > 0) {

					(new AsyncTask<Void, Void, Boolean>() {

						private String phoneNumber;
						private String password;
						private String gcmId = "";

						@Override
						protected void onPreExecute() {
							progress = new ProgressDialogCustom(ctx);
							progress.show();
							
							phoneNumber = ID.getText().toString().trim();
							password = Password.getText().toString().trim();
							if (GCMRegistrar.isRegisteredOnServer(ctx)) {
								gcmId = GCMRegistrar.getRegistrationId(ctx);
							}
						}

						@Override
						protected Boolean doInBackground(Void... params) {
							if (gcmId.equals("")) {
								GCMRegistrar.register(ctx,
										Config.GOOGLE_SENDER_ID);
							} else if (!GCMRegistrar.isRegisteredOnServer(ctx)) {
								mController.register(ctx, phoneNumber,
										password, gcmId);
							}

							gcmId = GCMRegistrar.getRegistrationId(ctx);
							Log.i("GCM - do in background", gcmId);
							
							// login
							int id = PostData.login(ctx, phoneNumber, password);

							if (id >= 0) {
								PostData.updateGcmId(ctx, id, gcmId);
								MyProfileManager.getInstance().init(ctx, id);
								return true;
							}

							return false;
						}

						@Override
						protected void onPostExecute(Boolean isOk) {
							progress.dismiss();
							if (isOk) {
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

							} else {
								// show error
								Utility.showMessage(ctx,
										"thong tin khong chinh xac!");
								Password.setText("");
							}

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
				replaceFragment(new CreateAccountProfileFragment(), true);
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
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Bundle bundle = this.getArguments();
		if (null != bundle && bundle.getBoolean("error", false)) {
			Utility.showAlertDialog(getActivity(), "Lỗi đăng nhập",
					"Không thể đăng nhập tự động.", true);

		}

		mController = (Controller) ctx.getApplicationContext();
		// Check if Internet Connection present
		if (!mController.isConnectingToInternet()) {
			mController.showAlertDialog(ctx, "Lỗi kết nối mạng",
					"Kiểm tra lại kết nối mạng và quay lại sau.", false);
			return;
		}

	}
	
	@Override
	public void onStop() {
		super.onStop();
		if (progress!=null) {
	        if (progress.isShowing()) {
	            progress.dismiss();       
	        }
	    }
	}

}
