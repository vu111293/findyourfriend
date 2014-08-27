package com.sgu.findyourfriend.screen;

import java.sql.Date;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.utils.Utility;

public class CreateOptionInfoFragment extends BaseFragment {

	private Button btnNext;
	private TextView txtBirthday;
	private EditText editLearningPlace;
	private EditText editWorkplace;
	private EditText editFacebookLink;
	private RadioGroup publicGrp;

	private Date date;
	final Calendar c = Calendar.getInstance();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.create_option_into_account,
				container, false);

		btnNext = (Button) rootView.findViewById(R.id.btnNext);
		txtBirthday = (TextView) rootView.findViewById(R.id.txtBirthday);
		editLearningPlace = (EditText) rootView
				.findViewById(R.id.editLearningPlace);
		editWorkplace = (EditText) rootView.findViewById(R.id.editWorkplace);
		editFacebookLink = (EditText) rootView.findViewById(R.id.editFacebookName);
		publicGrp = (RadioGroup) rootView.findViewById(R.id.radioPublic);

		date = new Date(System.currentTimeMillis());
		

		txtBirthday.setText( c.get(Calendar.DAY_OF_MONTH)
				+ "/" + c.get(Calendar.MONTH)
				+ "/" + c.get(Calendar.YEAR));
		txtBirthday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DatePickerDialog dpd = new DatePickerDialog(getActivity(),
						new DatePickerDialog.OnDateSetListener() {

							@SuppressWarnings("deprecation")
							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {

								txtBirthday.setText(dayOfMonth + "/"
										+ monthOfYear + "/" + year);

								 date = new Date(year - 1900, monthOfYear,
								 dayOfMonth);
							}
						}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
								.get(Calendar.DAY_OF_MONTH));
				dpd.show();

			}
		});

		btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Log.i("BTN", System.currentTimeMillis()  + "#" +  date.getTime());
				if ((System.currentTimeMillis() - date.getTime())
						< 0) {
					Log.i("DATE", date.toLocaleString());
					txtBirthday.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.edit_text_wrong));
					Utility.showAlertDialog(getActivity(),
							"Cảnh báo",
							"Ngày sinh không hợp lệ", false);
					return;
				} else {
					txtBirthday.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.edit_text));
				}

				// save to my profile
				User mine = MyProfileManager.getInstance().mTemp.getUserInfo();
				mine.setWorkplace(editWorkplace.getText().toString().trim());
				mine.setSchool(editLearningPlace.getText().toString().trim());
				mine.setFblink(editFacebookLink.getText().toString().trim());
				mine.setBirthday(date);
				mine.setPublic(publicGrp.getCheckedRadioButtonId() == R.id.radioYes ? true : false);
				MyProfileManager.getInstance().mTemp.setUserInfo(mine);
				
				replaceFragment(new CreateAccountInfoFragment(), true);
			}
		});

		return rootView;
	}

}
