package com.sgu.findyourfriend.screen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gcm.GCMRegistrar;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.utils.Utility;

public class VerifyEmailFragment extends BaseFragment {

	private ProgressDialogCustom progress;
	private Button btnLogin;

	private String phoneNumber;
	private String password;
	private String gcmId = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.veritfy_email_fragment,
				container, false);

		btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				(new AsyncTask<Void, Void, Integer>() {

					@Override
					protected void onPreExecute() {
						progress = new ProgressDialogCustom(getActivity());
						progress.show();
					}

					@Override
					protected Integer doInBackground(Void... params) {

						phoneNumber = SettingManager.getInstance()
								.getPhoneAutoLogin();
						password = SettingManager.getInstance()
								.getPasswordAutoLogin();

						int rs = PostData.login(getActivity(), phoneNumber,
								password);

						if (rs > 0) {
							if (GCMRegistrar.isRegistered(getActivity())) {
								gcmId = GCMRegistrar
										.getRegistrationId(getActivity());
								PostData.updateGcmId(getActivity(), rs, gcmId);
								MyProfileManager.getInstance().init(
										getActivity(), rs, true);
								Log.i("Verify", "registed with  " + gcmId);
							} else {
								Log.i("Verify", "empty gcmid");
								MyProfileManager.getInstance().init(
										getActivity(), rs, true);
								GCMRegistrar.register(getActivity(),
										Config.GOOGLE_SENDER_ID);
							}

							// MyProfileManager.getInstance().init(
							// getActivity(),
							// SettingManager.getInstance().getLastAccountIdLogin(),
							// true);
						}

						return rs;
					}

					@Override
					protected void onPostExecute(Integer result) {
						progress.dismiss();

						if (result > 0) {
							Log.i("GET SUC", MyProfileManager.getInstance()
									.getMineInstance().getUserInfo().toString());
							Intent i = new Intent(
									getActivity(),
									com.sgu.findyourfriend.screen.MainActivity.class);
							startActivity(i);
							getActivity().finish();
						} else {
							Utility.showAlertDialog(
									getActivity(),
									"Thông báo",
									"Tài khoản chưa được kích hoạt, vui lòng xác nhận e-mail",
									false);

						}
					}

				}).execute();

			}
		});

		return rootView;
	}
}
