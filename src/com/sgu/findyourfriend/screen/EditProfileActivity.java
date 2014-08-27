package com.sgu.findyourfriend.screen;

import java.sql.Date;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
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
	private EditText editName, editProvice, editEmail;
	private Spinner sprCity;
	private Button btnComplete;
	// private RadioButton Male;
	private RadioGroup sexGrp;
	private String selectedImagePath = "";
	private String selectedImageName = "";

	private TextView txtBirthday;
	private EditText editLearningPlace;
	private EditText editWorkplace;
	private EditText editFacebookLink;
	private RadioGroup publicGrp;

	private Date date;
	final Calendar c = Calendar.getInstance();

	private boolean isChangedImg;

	protected Dialog alertDialog;
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
		setContentView(R.layout.edit_profile);

		isChangedImg = false;

		svMain = (ScrollView) findViewById(R.id.svMain);
		imgProfile = (ImageView) findViewById(R.id.imgAvatar);
		editName = (EditText) findViewById(R.id.editName);
		editProvice = (EditText) findViewById(R.id.editProvice);
		editEmail = (EditText) findViewById(R.id.editEmail);
		btnComplete = (Button) findViewById(R.id.btnUpdate);
		sprCity = (Spinner) findViewById(R.id.spr_country);
		sexGrp = (RadioGroup) findViewById(R.id.radioSex);

		imgProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showTakeImage();
			}

		});

		txtBirthday = (TextView) findViewById(R.id.txtBirthday);
		editLearningPlace = (EditText) findViewById(R.id.editLearningPlace);
		editWorkplace = (EditText) findViewById(R.id.editWorkplace);
		editFacebookLink = (EditText) findViewById(R.id.editFacebookName);
		publicGrp = (RadioGroup) findViewById(R.id.radioPublic);

		// load data
		User mine = MyProfileManager.getInstance().getMineInstance()
				.getUserInfo();

		imgProfile.setImageDrawable(FriendManager.getInstance().hmImageP
				.get(mine.getId()));
		editName.setText(mine.getName());

		// editProvice.setText(mine.getAddress());
		editEmail.setText(mine.getEmail());
		// sprCity.setSelection(1);
		// Log.i("ADDRESS", (mine.getAddress().substring(
		// mine.getAddress().lastIndexOf(",") + 1)));
		//

		if (mine.getAddress().lastIndexOf(",") > -1) {
			sprCity.setSelection(((ArrayAdapter) sprCity.getAdapter())
					.getPosition(mine.getAddress()
							.substring(mine.getAddress().lastIndexOf(",") + 1)
							.trim()));

			editProvice.setText(mine.getAddress().subSequence(0,
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

		if (mine.isPublic()) {
			((RadioButton) findViewById(R.id.radioYes)).setSelected(true);
			((RadioButton) findViewById(R.id.radioNo)).setSelected(false);
		} else {
			((RadioButton) findViewById(R.id.radioYes)).setSelected(false);
			((RadioButton) findViewById(R.id.radioNo)).setSelected(true);
		}

		date = mine.getBirthday();
		date.setMonth(date.getMonth() + 1);

		txtBirthday.setText(date.getDate() + "/" + date.getMonth() + "/"
				+ (date.getYear() + 1900));
		txtBirthday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				DatePickerDialog dpd = new DatePickerDialog(mThis,
						new DatePickerDialog.OnDateSetListener() {

							@SuppressWarnings("deprecation")
							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {

								Log.i("DATE", year + " # " + (monthOfYear + 1)
										+ " # " + dayOfMonth);

								txtBirthday.setText(dayOfMonth + "/"
										+ (monthOfYear + 1) + "/" + year);

								date = new Date(year - 1900, monthOfYear,
										dayOfMonth);
							}
						}, date.getYear() + 1900, date.getMonth(), date
								.getDate());
				dpd.show();

			}
		});

		btnComplete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				boolean isEmpty = false;

				// validate basic information
				if (editName.getText().toString().trim().length() > 0) {
					editName.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.edit_text));
				} else {
					editName.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.edit_text_wrong));
					isEmpty = true;
				}

				if (sprCity.getSelectedItemPosition() > 0) {
					sprCity.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.edit_text));
				} else {
					sprCity.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.edit_text_wrong));
					isEmpty = true;
				}

				if (editProvice.getText().toString().trim().length() > 0) {
					editProvice.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.edit_text));
				} else {
					editProvice.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.edit_text_wrong));
					isEmpty = true;
				}

				if (Utility.isValidEmailAddress(editEmail.getText().toString()
						.trim())) {
					editEmail.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.edit_text));
				} else {
					editEmail.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.edit_text_wrong));
					isEmpty = true;
				}

				if (isEmpty) {
					svMain.scrollTo(0, -svMain.getBottom());
					Utility.showAlertDialog(mThis, "Cảnh báo",
							"Nhập các thông tin yêu cầu", false);
					return;
				}

				// validate option information
				if ((System.currentTimeMillis() - date.getTime()) < 0) {
					Log.i("DATE", date.toLocaleString());
					txtBirthday.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.edit_text_wrong));
					Utility.showAlertDialog(mThis, "Cảnh báo",
							"Ngày sinh không hợp lệ", false);
					return;
				} else {
					txtBirthday.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.edit_text));
				}

				int sex = (sexGrp.getCheckedRadioButtonId() == R.id.radioMale ? 1
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
						mineEdited.setAddress(editProvice.getText().toString()
								.trim()
								+ ", " + sprCity.getSelectedItem().toString());
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
						mineEdited.setBirthday(date);
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
							showFinishDialog();
						} else {
							Utility.showAlertDialog(mThis, "Cảnh báo",
									"Xãy ra lỗi trong quá trình cập nhật",
									false);
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
				//
				// selectedImagePath = getPath(selectedImageUri);
				selectedImageName = selectedImagePath.split("/")[(selectedImagePath
						.split("/").length) - 1];
				Utility.showMessage(mThis, "path: " + selectedImagePath);

				imgProfile.setImageDrawable(Drawable
						.createFromPath(selectedImagePath));
			} else if (requestCode == TAKE_PHOTO) {
				Bitmap photo = (Bitmap) data.getExtras().get("data");
				selectedImagePath = Utility
						.savebitmap(
								Utility.dropRectBitmap(Utility.rotateBitmap(
										photo, 270))).getPath();

				// wrong
				// selectedImagePath = data.getStringExtra("path");
				selectedImageName = selectedImagePath.split("/")[(selectedImagePath
						.split("/").length) - 1];
				Utility.showMessage(mThis, "path: " + selectedImagePath);
				imgProfile.setImageDrawable(Drawable
						.createFromPath(selectedImagePath));
			}
		}
	}

	public void showTakeImage() {
		final Dialog dialog = new Dialog(getApplicationContext());

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

	@SuppressWarnings("deprecation")
	public void showFinishDialog() {
		AlertDialog alertDialog = new AlertDialog.Builder(mThis).create();

		alertDialog.setMessage("Cập nhật thông tin thành công");

		alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		alertDialog.show();
	}
}
