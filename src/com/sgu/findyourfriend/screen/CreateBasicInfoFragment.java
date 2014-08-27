package com.sgu.findyourfriend.screen;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.utils.Utility;

public class CreateBasicInfoFragment extends BaseFragment {

	public static final String TAG = "CreateAccountProfilrFragment";
	private static final int TAKE_PHOTO = 0;
	private static final int SELECT_PICTURE = 1;
	public static int count = 0;

	// private ProgressDialogCustom progress;
	private ImageView imgProfile;
	private EditText editName, editProvice, editEmail;
	private Spinner sprCity;
	private Button btnNext;
	// private RadioButton Male;
	private RadioGroup sexGrp;
	private Context ctx;
	private String selectedImagePath = "";
	private String selectedImageName = "";

	protected Dialog alertDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.create_basic_info_account,
				container, false);

		imgProfile = (ImageView) rootView.findViewById(R.id.imgAvatar);
		editName = (EditText) rootView.findViewById(R.id.editName);
		editProvice = (EditText) rootView.findViewById(R.id.editProvice);
		editEmail = (EditText) rootView.findViewById(R.id.editEmail);
		btnNext = (Button) rootView.findViewById(R.id.btnNext);
		sprCity = (Spinner) rootView.findViewById(R.id.spr_country);
		sexGrp = (RadioGroup) rootView.findViewById(R.id.radioSex);

		imgProfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showTakeImage();
			}

		});

		btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				boolean isEmpty = false;

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
					Utility.showAlertDialog(ctx, "Cảnh báo",
							"Nhập các thông tin yêu cầu", false);
					return;
				}

				if (null == MyProfileManager.getInstance().mTemp)
					MyProfileManager.getInstance().mTemp = new Friend(null,
							null, false, 0, null, null);

				int sex = (sexGrp.getCheckedRadioButtonId() == R.id.radioMale ? 1
						: 2);
				
				User mine = new User(0);
				mine.setName(editName.getText().toString().trim());
				mine.setGender(sex);
				mine.setAddress(editProvice.getText().toString().trim() 
						+ ", " + sprCity.getSelectedItem().toString());
				mine.setEmail(editEmail
								.getText().toString().trim());
				mine.setAvatar(selectedImagePath);
				
				MyProfileManager.getInstance().mTemp.setUserInfo(mine);

				replaceFragment(new CreateOptionInfoFragment(), true);
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
		if (!Utility.isConnectingToInternet(getActivity())) {
			Utility.showAlertDialog(ctx, "Cảnh báo",
					"Kiểm tra lại kết nối mạng và quay lại sau.", false);
			return;
		}
	}

	public void showTakeImage() {
		final Dialog dialog = new Dialog(ctx);
		
		Window W= dialog.getWindow();
		W.setGravity(Gravity.CENTER_VERTICAL| Gravity.CENTER_HORIZONTAL);
		W.setLayout(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();

				selectedImagePath = Utility.savebitmap(
						Utility.dropRectBitmap(BitmapFactory.decodeFile(Utility
								.getPath(getActivity(), selectedImageUri))))
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
				selectedImagePath = Utility
						.savebitmap(
								Utility.dropRectBitmap(Utility.rotateBitmap(
										photo, 270))).getPath();

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

}
