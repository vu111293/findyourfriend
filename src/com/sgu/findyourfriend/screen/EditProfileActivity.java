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

import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.utils.Utility;

public class EditProfileActivity extends Activity {

	private static final String TAG = "CreateAccountProfilrFragment";
	private static final int TAKE_PHOTO = 0;
	private static final int SELECT_PICTURE = 1;
	public static int count = 0;

	private ScrollView svMain;
	private ImageView imgProfile;
	private EditText editName, editAddress, editEmail;
	private AutoCompleteTextView autoProvice;
	private Button btnComplete;
	private Button btnExit;

	private RadioGroup sexGrp;
	private String selectedImagePath = "";
	private String selectedImageName = "";

	private EditText editLearningPlace;
	private EditText editWorkplace;
	private EditText editFacebookLink;
	private EditText txtDay, txtMonth, txtYear;
	private RadioGroup publicGrp;

	private int sex;
	private int day = 0, month = 0, year = 0;

	private Date date;
	final Calendar c = Calendar.getInstance();

	private boolean isChangedImg;

	private ProgressDialogCustom progress;

	private EditProfileActivity mThis = this;

	public EditProfileActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_edit_profile);

		isChangedImg = false;

		svMain = (ScrollView) findViewById(R.id.svMain);
		imgProfile = (ImageView) findViewById(R.id.imgAvatar);
		editName = (EditText) findViewById(R.id.editName);
		autoProvice = (AutoCompleteTextView) findViewById(R.id.autoTextCountry);
		editEmail = (EditText) findViewById(R.id.editEmail);
		btnComplete = (Button) findViewById(R.id.btnUpdate);
		btnExit = (Button) findViewById(R.id.btnExit);
		editAddress = (EditText) findViewById(R.id.editAddress);
		sexGrp = (RadioGroup) findViewById(R.id.radioSex);

		String[] countries = getResources().getStringArray(
				R.array.country_arrays);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getApplicationContext(), android.R.layout.select_dialog_item,
				countries);

		autoProvice = (AutoCompleteTextView) findViewById(R.id.autoTextCountry);
		autoProvice.setThreshold(1);
		autoProvice.setAdapter(adapter);

		imgProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showTakeImage();
			}

		});

		txtDay = (EditText) findViewById(R.id.txtDay);
		txtMonth = (EditText) findViewById(R.id.txtMonth);
		txtYear = (EditText) findViewById(R.id.txtYear);

		editLearningPlace = (EditText) findViewById(R.id.editLearningPlace);
		editWorkplace = (EditText) findViewById(R.id.editWorkplace);
		editFacebookLink = (EditText) findViewById(R.id.editFacebookName);
		publicGrp = (RadioGroup) findViewById(R.id.radioPublic);

		// load data
		User mine = MyProfileManager.getInstance().getMineInstance()
				.getUserInfo();

		if (mine.isPublic()) {
			((RadioButton) publicGrp.getChildAt(0)).setChecked(true);
		} else {
			((RadioButton) publicGrp.getChildAt(1)).setChecked(true);
		}

		imgProfile.setImageDrawable(FriendManager.getInstance().hmImageP
				.get(mine.getId()));

		editName.setText(mine.getName());

		editEmail.setText(mine.getEmail());

		if (mine.getAddress().lastIndexOf(",") > -1) {
			autoProvice.setText(mine.getAddress()
					.substring(mine.getAddress().lastIndexOf(",") + 1).trim());

			editAddress.setText(mine.getAddress().subSequence(0,
					mine.getAddress().lastIndexOf(",")));
		}

		if (mine.getGender() == 1) {
			((RadioButton) findViewById(R.id.radioMale)).setSelected(true);
			((RadioButton) findViewById(R.id.radioFemale)).setSelected(false);
		} else {
			((RadioButton) findViewById(R.id.radioMale)).setSelected(false);
			((RadioButton) findViewById(R.id.radioFemale)).setSelected(true);
		}

		editLearningPlace.setText(mine.getSchool());
		editWorkplace.setText(mine.getWorkplace());
		editFacebookLink.setText(mine.getFblink());

		date = mine.getBirthday();

//		Log.i("=======", date.getDate() + " # " + date.getMonth() + " # " +date.getYear());
//		Log.i("=======", date.toLocaleString());
//		
		
		txtDay.setText(date.getDate() + "");
		txtMonth.setText((date.getMonth() + 1) + "");
		txtYear.setText((date.getYear() + 1900) + "");

		if (mine.isPublic()) {
			((RadioButton) findViewById(R.id.radioYes)).setSelected(true);
			((RadioButton) findViewById(R.id.radioNo)).setSelected(false);
		} else {
			((RadioButton) findViewById(R.id.radioYes)).setSelected(false);
			((RadioButton) findViewById(R.id.radioNo)).setSelected(true);
		}

		btnExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btnComplete.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {

				if (!Utility
						.checkConnectToNetworkContinue(getApplicationContext()))
					return;

				final Dialog dialog = new Dialog(mThis);
				boolean error = false;

				// validate basic information
				if (editName.getText().toString().trim().length() > 0) {
					editName.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.edit_text));
				} else {
					editName.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.edit_text_wrong));
					error = true;
				}

				if (Utility.isValidEmailAddress(editEmail.getText().toString()
						.trim())) {
					editEmail.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.edit_text));
				} else {
					editEmail.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.edit_text_wrong));
					error = true;
				}

				if (sexGrp.getCheckedRadioButtonId() == R.id.radioMale)
					sex = 1;
				else if (sexGrp.getCheckedRadioButtonId() == R.id.radioFemale)
					sex = 2;

				if (txtDay.getText().toString().trim().length() > 0)
					day = Integer.parseInt(txtDay.getText().toString().trim());

				if (txtMonth.getText().toString().trim().length() > 0)
					month = Integer.parseInt(txtMonth.getText().toString());

				if (txtYear.getText().toString().trim().length() > 0)
					year = Integer.parseInt(txtYear.getText().toString());

				if (day > 0 || month > 0 || year > 0) {
					// has input

					if (day < 1 || day > 31) {
						txtDay.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.edit_text_wrong));
						error = true;
					} else {
						txtDay.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.edit_text));
					}

					if (month < 1 || month > 12) {
						txtMonth.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.edit_text_wrong));
						error = true;
					} else {
						txtMonth.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.edit_text));
					}

					if (year >= ((new Date(System.currentTimeMillis())).getYear() + 1900)) {
						txtYear.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.edit_text_wrong));
						error = true;
					} else {
						txtYear.setBackgroundDrawable(getResources()
								.getDrawable(R.drawable.edit_text));
					}

					Log.i("year", date.getYear() + "");

				}

				if (error) {
					svMain.scrollTo(0, -svMain.getBottom());

					Utility.showDialog(Utility.ERROR, dialog,
							"Thiếu thông tin", "Nhập đủ thông tin yêu cầu.");
					return;
				}

				sex = (sexGrp.getCheckedRadioButtonId() == R.id.radioMale ? 1
						: 2);

				// update information

				(new AsyncTask<Void, Void, Boolean>() {
					private User mineEdited;

					@Override
					protected void onPreExecute() {
						progress = new ProgressDialogCustom(mThis);
						progress.show();

						int sex = (sexGrp.getCheckedRadioButtonId() == R.id.radioMale ? 1
								: 2);

						mineEdited = new User(MyProfileManager.getInstance()
								.getMyID());
						mineEdited
								.setName(editName.getText().toString().trim());
						mineEdited.setGender(sex);
						mineEdited.setAddress(editAddress.getText().toString()
								.trim()
								+ ", "
								+ autoProvice.getText().toString().trim());
						mineEdited.setEmail(editEmail.getText().toString()
								.trim());

						// warning
						if (!isChangedImg)
							mineEdited.setAvatar(MyProfileManager.getInstance()
									.getImage());

						mineEdited.setWorkplace(editWorkplace.getText()
								.toString().trim());
						mineEdited.setSchool(editLearningPlace.getText()
								.toString().trim());
						mineEdited.setFblink(editFacebookLink.getText()
								.toString().trim());

						if (day + month + year > 0) {
							mineEdited.setBirthday(new Date((year - 1900), month - 1,
									day));
						} else {
							mineEdited.setBirthday(date);
						}

						mineEdited.setPublic(publicGrp
								.getCheckedRadioButtonId() == R.id.radioYes ? true
								: false);

						mineEdited.setGcmId(MyProfileManager.getInstance()
								.getMyGCMID());
						mineEdited.setInternetImageLink(MyProfileManager
								.getInstance().getImageLink());
					}

					@Override
					protected Boolean doInBackground(Void... params) {

						String renameFile = "temp_"
								+ System.currentTimeMillis() + ".png";

						if (isChangedImg) {
							Log.i("Image path", selectedImagePath);
							PostData.uploadFile(mThis, selectedImagePath,
									renameFile);
						}

						boolean success = PostData.userEdit(mThis, mineEdited
								.getId(), mineEdited.getName(),
								isChangedImg ? PostData.IMAGE_HOST + renameFile
										: mineEdited.getInternetImageLink(),
								mineEdited.getGcmId(), mineEdited.getGender(),
								mineEdited.getAddress(), mineEdited
										.getBirthday().toString(), mineEdited
										.getSchool(),
								mineEdited.getWorkplace(), mineEdited
										.getEmail(), mineEdited.getFblink(),
								mineEdited.isPublic());

						if (success) {
							MyProfileManager.getInstance().init(mThis,
									mineEdited.getId(), true);

							FriendManager.getInstance().updateFriend(
									MyProfileManager.getInstance()
											.getMineInstance());

							return true;
						}
						return false;
					}

					@Override
					protected void onPostExecute(Boolean result) {

						if (result) {
							Utility.showDialog(Utility.CONFIRM, dialog,
									"Thành công",
									"Cập nhật thông tin thành công.", "Đóng",
									new OnClickListener() {

										@Override
										public void onClick(View v) {
											dialog.dismiss();
											finish();
										}
									});
						} else {
							Utility.showDialog(Utility.ERROR, dialog, "Lỗi",
									"Xãy ra lỗi trong quá trình cập nhật. Xin thử lại sau .");
						}
						progress.dismiss();
					}

				}).execute();

			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			isChangedImg = true;

			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();

				selectedImagePath = Utility.savebitmap(
						Utility.dropRectBitmap(BitmapFactory.decodeFile(Utility
								.getPath(mThis, selectedImageUri)))).getPath();
				selectedImageName = selectedImagePath.split("/")[(selectedImagePath
						.split("/").length) - 1];
//				Utility.showMessage(mThis, "path: " + selectedImagePath);

				imgProfile.setImageDrawable(Drawable
						.createFromPath(selectedImagePath));
			} else if (requestCode == TAKE_PHOTO) {
				Bitmap photo = (Bitmap) data.getExtras().get("data");
				selectedImagePath = Utility.savebitmap(
						Utility.dropRectBitmap(Utility.rotateBitmap(
								photo, 270))).getPath();

				selectedImageName = selectedImagePath.split("/")[(selectedImagePath
						.split("/").length) - 1];
//				Utility.showMessage(mThis, "path: " + selectedImagePath);
				imgProfile.setImageDrawable(Drawable
						.createFromPath(selectedImagePath));
			}
		}
	}

	public void showTakeImage() {
		final Dialog dialog = new Dialog(mThis);

		Window W = dialog.getWindow();
		W.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		W.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		W.requestFeature(Window.FEATURE_NO_TITLE);
		W.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.setContentView(R.layout.options_take_image_layout);

		((Button) dialog.findViewById(R.id.btnGallery))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(
								Intent.createChooser(intent, "Select Picture"),
								SELECT_PICTURE);
						dialog.dismiss();
					}
				});

		((Button) dialog.findViewById(R.id.btnTakePhoto))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent cameraIntent = new Intent(
								android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(cameraIntent, TAKE_PHOTO);
						dialog.dismiss();
					}
				});

		dialog.show();
	}
}
