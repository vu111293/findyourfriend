package com.sgu.findyourfriend.screen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

	private static final int TAKE_PHOTO = 0;
	private static final int SELECT_PICTURE = 1;
	public static int count = 0;

	private ProgressDialogCustom progress;
	private ImageView imgProfile;
	private EditText phone, name, address, email, password;
	private Button BtnCreateAccount;
	private RadioButton Male, Female;
	private Context ctx;
	private String selectedImagePath;
	private String selectedImageName;

	private Controller mController;

	protected Dialog alertDialog;

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
							progress = new ProgressDialogCustom(ctx);
							progress.show();
							
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

							String renameFile = "temp_"
									+ System.currentTimeMillis() + ".png";
							PostData.uploadFile(ctx, selectedImagePath,
									renameFile);

							int id = PostData.userCreate(ctx, name.getText()
									.toString().trim(), Male.isChecked() ? 1
									: 2, address.getText().toString().trim(),
									email.getText().toString().trim(),
									PostData.IMAGE_HOST + renameFile, gcmId);

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
							progress.dismiss();
							
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

		(new ProgressDialogCustom(ctx)).show();
		
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

	public void showTakeImage() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialoglayout = inflater.inflate(
				R.layout.options_take_image_layout, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.progess_dialog_custom);

		builder.setView(dialoglayout);

		((Button) dialoglayout.findViewById(R.id.btnGallery))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(
								Intent.createChooser(intent, "Select Picture"),
								SELECT_PICTURE);
						alertDialog.dismiss();
					}
				});

		((Button) dialoglayout.findViewById(R.id.btnTakePhoto))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent cameraIntent = new Intent(
								android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(cameraIntent, TAKE_PHOTO);
						alertDialog.dismiss();
					}
				});

		alertDialog = builder.create();
		alertDialog.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();

				selectedImagePath = savebitmap(
						Utility.dropRectBitmap(BitmapFactory
								.decodeFile(getPath(selectedImageUri))))
						.getPath();
				//
				// selectedImagePath = getPath(selectedImageUri);
				selectedImageName = selectedImagePath.split("/")[(selectedImagePath
						.split("/").length) - 1];
				Utility.showMessage(ctx, "path: " + selectedImagePath);

				imgProfile.setImageDrawable(Drawable
						.createFromPath(selectedImagePath));
			} else if (requestCode == TAKE_PHOTO) {
				Bitmap photo = (Bitmap) data.getExtras().get("data");
				selectedImagePath = savebitmap(
						Utility.dropRectBitmap(Utility.rotateBitmap(photo, 270)))
						.getPath();

				// wrong
				// selectedImagePath = data.getStringExtra("path");
				selectedImageName = selectedImagePath.split("/")[(selectedImagePath
						.split("/").length) - 1];
				Utility.showMessage(ctx, "path: " + selectedImagePath);
				imgProfile.setImageDrawable(Drawable
						.createFromPath(selectedImagePath));
			}
		}
	}

	private File savebitmap(Bitmap bmp) {
		String extStorageDirectory = Environment.getExternalStorageDirectory()
				.toString();
		OutputStream outStream = null;
		// String temp = null;
		File file = new File(extStorageDirectory, "temp_2014.png");
		if (file.exists()) {
			file.delete();
			file = new File(extStorageDirectory, "temp_2014.png");

		}

		try {
			outStream = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
			outStream.flush();
			outStream.close();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return file;
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
