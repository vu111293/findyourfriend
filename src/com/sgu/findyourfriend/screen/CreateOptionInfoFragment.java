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

import java.sql.Date;
import java.util.Calendar;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.model.User;
import com.sgu.findyourfriend.utils.Utility;

public class CreateOptionInfoFragment extends BaseFragment {

	private Button btnNext;
	// private TextView txtBirthday;
	private EditText editLearningPlace;
	private EditText editWorkplace;
	private EditText editFacebookLink;
	private RadioGroup publicGrp;
	private RadioGroup sexGrp;
	private EditText txtDay, txtMonth, txtYear;
	private AutoCompleteTextView autoTextCountry;
	private EditText txtAddress;

	private Date date;
	final Calendar c = Calendar.getInstance();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_create_option_into_account, container, false);

		btnNext = (Button) rootView.findViewById(R.id.btnNext);
		// txtBirthday = (TextView) rootView.findViewById(R.id.txtBirthday);
		editLearningPlace = (EditText) rootView
				.findViewById(R.id.editLearningPlace);
		editWorkplace = (EditText) rootView.findViewById(R.id.editWorkplace);
		editFacebookLink = (EditText) rootView
				.findViewById(R.id.editFacebookName);
		publicGrp = (RadioGroup) rootView.findViewById(R.id.radioPublic);
		sexGrp = (RadioGroup) rootView.findViewById(R.id.radioSex);

		txtDay = (EditText) rootView.findViewById(R.id.txtDay);
		txtMonth = (EditText) rootView.findViewById(R.id.txtMonth);
		txtYear = (EditText) rootView.findViewById(R.id.txtYear);

//		autoTextCountry = (AutoCompleteTextView) rootView
//				.findViewById(R.id.autoTextCountry);

		String[] countries = getResources().getStringArray(
				R.array.country_arrays);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.select_dialog_item, countries);

		autoTextCountry = (AutoCompleteTextView) rootView.findViewById(R.id.autoTextCountry);
		autoTextCountry.setThreshold(1);
		autoTextCountry.setAdapter(adapter);
//		autoTextCountry.setTextColor(Color.RED);

		txtAddress = (EditText) rootView.findViewById(R.id.editAddress);

		date = new Date(System.currentTimeMillis());
		// date.setYear(c.get(Calendar.YEAR));

		btnNext.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				final Dialog dialog = new Dialog(getActivity());
				boolean error = false;

				int sex = 0;
				int day = 0, month = 0, year = 0;

				if (sexGrp.getCheckedRadioButtonId() == R.id.radioMale)
					sex = 1;
				else if (sexGrp.getCheckedRadioButtonId() == R.id.radioFemale)
					sex = 2;

				if (txtDay.getText().toString().trim().length() >  0)
					day = Integer.parseInt(txtDay.getText().toString().trim());
				
				if (txtMonth.getText().toString().trim().length() >  0)
					month = Integer.parseInt(txtMonth.getText().toString());
				
				if (txtYear.getText().toString().trim().length() >  0)
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

					if (year >= (date.getYear() + 1900)) {
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
					Utility.showDialog(Utility.ERROR,
							new Dialog(getActivity()), "Lỗi thông tin nhập",
							"Thông tin nhập không hợp lệ, vui lòng kiểm tra lại các trường đã nhập.");

				} else {

					String fullAddress = txtAddress.getText().toString().trim()
							+ ", "
							+ autoTextCountry.getText().toString().trim();

					// save to my profile
					User mine = MyProfileManager.getInstance().mTemp
							.getUserInfo();
					mine.setGender(sex);
					mine.setAddress(fullAddress);

					if (day + month + year > 0) {
						mine.setBirthday(new Date((year - 1900), month - 1, day));
					} else {
						mine.setBirthday(date);
					}

					mine.setWorkplace(editWorkplace.getText().toString().trim());
					mine.setSchool(editLearningPlace.getText().toString()
							.trim());
					mine.setFblink(editFacebookLink.getText().toString().trim());
					mine.setPublic(publicGrp.getCheckedRadioButtonId() == R.id.radioYes ? true
							: false);
					MyProfileManager.getInstance().mTemp.setUserInfo(mine);

					replaceFragment(new CreateAccountInfoFragment(), true);
				}
			}
		});

		return rootView;
	}
}
