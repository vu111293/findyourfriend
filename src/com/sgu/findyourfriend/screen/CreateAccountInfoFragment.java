package com.sgu.findyourfriend.screen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
	public static final String TAG = "CreateAccountProfilrFragment";
	public static int count = 0;

	private ProgressDialogCustom progress;

	private Button btnSignin;
	private EditText editPhone, editPassword;
	private Context ctx;

	private String phoneNumber;
	private String passwd;

	protected Dialog alertDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		progress = new ProgressDialogCustom(ctx);

		View rootView = inflater.inflate(R.layout.create_account, container,
				false);

		editPhone = (EditText) rootView.findViewById(R.id.editPhoneNumber);
		editPassword = (EditText) rootView.findViewById(R.id.editPassword);
		btnSignin = (Button) rootView.findViewById(R.id.btnSignin);

		btnSignin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				boolean isEmpty = false;

				if (editPhone.getText().toString().trim().length() > 0
						&& isPhoneValidate(editPhone.getText().toString()
								.trim())) {
					editPhone.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.edit_text));
				} else {
					editPhone.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.edit_text_wrong));
					isEmpty = true;
				}

				if (editPassword.getText().toString().trim().length() > 0
						&& editPassword.getText().toString().trim().length() >= Config.MIN_PASSWORD_LENGHT) {
					editPassword.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.edit_text));
				} else {
					editPassword.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.edit_text_wrong));
					isEmpty = true;
				}

				if (isEmpty) {
					Utility.showAlertDialog(ctx, "",
							"Nhập các thông tin yêu cầu", false);
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

						if (PostData
								.isPhoneRegisted(getActivity(), phoneNumber))
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
								PostData.IMAGE_HOST + renameFile, gcmId, mine
										.getGender(), mine.getAddress(), mine
										.getBirthday().toString(), mine
										.getSchool(), mine.getWorkplace(), mine
										.getEmail(), mine.getFblink(), passwd,
								mine.isPublic());

						mine.setId(id);
						MyProfileManager.getInstance().mTemp.setUserInfo(mine);

						// wait to receive response
						if (id > 0) {
							SettingManager.getInstance()
									.saveLastAccountIdLogin(id);
							success = PostData.accountCreate(ctx, id,
									phoneNumber);

							return success ? Config.SUCCESS : Config.ERROR;
						}

						return Config.ERROR;
					}

					@Override
					protected void onPostExecute(Integer result) {
						progress.dismiss();
						if (result == Config.SUCCESS) {
							showFinishDialog(ctx);
						} else if (result == Config.PHONE_REGISTED) {
							editPhone.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.edit_text_wrong));
							Utility.showAlertDialog(ctx, "Cảnh báo",
									"Số điện thoại này đã được đăng kí", false);
						} else {
							Utility.showAlertDialog(ctx, "Cảnh báo",
									"Xãy ra lỗi trong khi tạo tải khoản.",
									false);
						}
					}

				}).execute();
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

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Check if Internet Connection present
		if (!Utility.isConnectingToInternet(getActivity())) {
			Utility.showAlertDialog(ctx, "Lỗi kết nối mạng",
					"Kiểm tra lại kết nối mạng và quay lại sau.", false);
			return;
		}
	}

	@SuppressWarnings("deprecation")
	public void showFinishDialog(Context context) {
		// save info
		SettingManager.getInstance().savePhoneAutoLogin(phoneNumber);
		SettingManager.getInstance().savePasswordAutoLogin(passwd);

		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Set Dialog Message
		alertDialog
				.setMessage("Tạo tài khoản thành công. Bạn có muốn tự động đăng nhập vào lần sau?");

		alertDialog.setButton("Có", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				SettingManager.getInstance().setAutoLogin(true);
				replaceFragment(new VerifyEmailFragment(), true);
			}
		});

		alertDialog.setButton2("Không", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				SettingManager.getInstance().setAutoLogin(false);
				replaceFragment(new VerifyEmailFragment(), true);
			}
		});

		// Show Alert Message
		alertDialog.show();
	}
}
