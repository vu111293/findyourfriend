package com.sgu.findyourfriend.screen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.google.android.gcm.GCMRegistrar;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.utils.Controller;
import com.sgu.findyourfriend.utils.Utility;

public class CreateAccountProfileFragment extends BaseFragment {

	private static final int SELECT_PICTURE = 1;

	private ImageView imgProfile;
	private EditText phone, name, address, email, password;
	private Button BtnCreateAccount;
	private RadioButton Male, Female;
	private Context ctx;
	private String selectedImagePath;
	private String selectedImageName;

	private Controller mController;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mController = (Controller) ctx.getApplicationContext();

		View rootView = inflater.inflate(R.layout.activity_create_account,
				container, false);

		imgProfile = (ImageView) rootView.findViewById(R.id.imgAvatar);

		phone = (EditText) rootView.findViewById(R.id.editPhoneNumber);
		password = (EditText) rootView.findViewById(R.id.editPassword);
		name = (EditText) rootView.findViewById(R.id.editName);
		address = (EditText) rootView.findViewById(R.id.editAddress);
		email = (EditText) rootView.findViewById(R.id.editEmail);
		BtnCreateAccount = (Button) rootView.findViewById(R.id.btnSignin);
		Male = (RadioButton) rootView.findViewById(R.id.radioMale);

		// TelephonyManager telemamanger = (TelephonyManager) ctx
		// .getSystemService(Activity.TELEPHONY_SERVICE);
		// String getSimNumber = telemamanger.getLine1Number();
		// if (getSimNumber != null && getSimNumber != "") {
		// phone.setText(getSimNumber);
		// }

		imgProfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showTakeImage();
			}

		});

		BtnCreateAccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (phone.getText().toString().trim().length() > 0
						&& name.getText().toString().trim().length() > 0
						&& email.getText().toString().trim().length() > 0
						&& address.getText().toString().trim().length() > 0
						&& password.getText().toString().trim().length() > 0) {

					(new AsyncTask<Void, Void, Boolean>() {
						private String phoneNumber;
						private String passwd;
						private String gcmId = "";
						private boolean success = false;

						protected void onPreExecute() {

							phoneNumber = phone.getText().toString().trim();
							passwd = password.getText().toString().trim();

							if (GCMRegistrar.isRegisteredOnServer(ctx)) {
								gcmId = GCMRegistrar.getRegistrationId(ctx);
							}

						};

						@Override
						protected Boolean doInBackground(Void... params) {

							// upload avatar

							// get gcmid
							if (gcmId.equals("")) {
								GCMRegistrar.register(ctx,
										Config.GOOGLE_SENDER_ID);
							} else if (!GCMRegistrar.isRegisteredOnServer(ctx)) {
								mController.register(ctx, name.getText()
										.toString().trim(), passwd, gcmId);
							}

							PostData.uploadFile(selectedImagePath);

							int id = PostData.userCreate(ctx, name.getText()
									.toString().trim(), Male.isChecked() ? 1
									: 2, address.getText().toString().trim(),
									email.getText().toString().trim(),
									PostData.IMAGE_HOST + selectedImageName,
									gcmId);

							Log.i("ACCOUT", id + "");
							
							// wait to receive response
							if (id >= 0) {
								success = PostData.accountCreate(ctx, id,
										phoneNumber);
								
								Log.i("ACCOUT", success + "");
								success &= PostData.userChangePassword(ctx, id,
										"123456", passwd);
								Log.i("ACCOUT", success + "");
							}

							if (success) {
								MyProfileManager.getInstance().init(ctx, id);
								return true;
							}

							return false;
						}

						@Override
						protected void onPostExecute(Boolean isOk) {

							if (isOk) {
								// save info
								SettingManager.getInstance()
										.savePhoneAutoLogin(phoneNumber);
								SettingManager.getInstance()
										.savePasswordAutoLogin(passwd);

								// start main app
								Intent i = new Intent(
										ctx,
										com.sgu.findyourfriend.screen.MainActivity.class);
								startActivity(i);

							} else {
								// show error
								Utility.showMessage(ctx,
										"Lỗi trong quá trình tạo tài khoản!");
							}
						}

					}).execute();
				} else {
					// truong rong
					Utility.showMessage(ctx, "Xin nhập đủ thông tin");

				}
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

		// Check if Internet Connection present
		if (!mController.isConnectingToInternet()) {
			mController.showAlertDialog(ctx, "Lỗi kết nối mạng",
					"Kiểm tra lại kết nối mạng và quay lại sau.", false);
			return;
		}
	}

	private void showTakeImage() {
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(ctx);

		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ctx,
				android.R.layout.select_dialog_item);
		arrayAdapter.add("Chụp từ camera");
		arrayAdapter.add("Chọn tron bộ sưu tập");
		builderSingle.setNegativeButton("cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builderSingle.setAdapter(arrayAdapter,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							Utility.showMessage(ctx, "Tính năng chưa sẵn sàng.");
						} else {
							// in onCreate or any event where your want the user
							// to
							// select a file
							Intent intent = new Intent();
							intent.setType("image/*");
							intent.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(Intent.createChooser(intent,
									"Select Picture"), SELECT_PICTURE);
						}

					}
				});
		builderSingle.show();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();

				selectedImagePath = getPath(selectedImageUri);
				selectedImageName = selectedImagePath.split("/")[(selectedImagePath
						.split("/").length) - 1];
				Utility.showMessage(ctx, "path: " + selectedImagePath);
				
				imgProfile.setImageDrawable(Drawable.createFromPath(selectedImagePath));
			}
		}
	}

	public String getPath(Uri uri) {
		// just some safety built in
		if (uri == null) {
			// TODO perform some logging or show user feedback
			return null;
		}
		// try to retrieve the image from the media store first
		// this will only work for images selected from gallery
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = ((Activity) ctx).managedQuery(uri, projection, null,
				null, null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		// this is our fell back here
		return uri.getPath();
	}
}
