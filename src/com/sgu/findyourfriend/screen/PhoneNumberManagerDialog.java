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

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.AccountAdapter;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.utils.Utility;

public class PhoneNumberManagerDialog extends Dialog {

	private ListView lvPhoneNumbers;
	private AccountAdapter adapter;
	private TextView txtPhoneNumberCurrent;
	private Button btnAddNewPhone;
	private EditText editNewPhone;
	private ArrayList<String> dataP;
	private ProgressDialogCustom progress;
	private Button btnExit;

	private String newPhone;

	public PhoneNumberManagerDialog(Context context) {
		super(context, R.style.full_screen_dialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_account_manager);
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		dataP = MyProfileManager.getInstance().getMyPhoneNumberNotCurrent();

		lvPhoneNumbers = (ListView) findViewById(R.id.lvPhoneNumbers);
		adapter = new AccountAdapter(getContext(), R.layout.custom_phonenumber,
				dataP);

		lvPhoneNumbers.setAdapter(adapter);

		txtPhoneNumberCurrent = (TextView) findViewById(R.id.txtPhoneNumberCurrent);
		txtPhoneNumberCurrent.setText(MyProfileManager.getInstance()
				.getMyPhoneLogin());

		editNewPhone = (EditText) findViewById(R.id.editNewPhone);

		btnExit = (Button) findViewById(R.id.btnExit);
		btnExit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		btnAddNewPhone = (Button) findViewById(R.id.btnAddPhone);
		btnAddNewPhone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (!Utility.checkConnectToNetworkContinue(getContext()))
					return;

				final Dialog dialog = new Dialog(getContext());

				newPhone = editNewPhone.getText().toString().trim();
				editNewPhone.setText("");
				InputMethodManager imm = (InputMethodManager) getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editNewPhone.getWindowToken(), 0);

				if (!Utility.isPhoneNumberCorrect(newPhone)) {
					editNewPhone.setBackgroundDrawable(getContext()
							.getResources().getDrawable(
									R.drawable.edit_text_wrong));

					Utility.showDialog(Utility.ERROR, dialog,
							"Số điện thoại không hợp lệ",
							"Kiểm tra lại hoặc chọn số điện thoại khác.");
					return;
				}

				editNewPhone.setBackgroundDrawable(getContext().getResources()
						.getDrawable(R.drawable.edit_text));

				(new AsyncTask<Void, Void, Integer>() {

					@Override
					protected void onPreExecute() {
						progress = new ProgressDialogCustom(getContext());
						progress.show();
					}

					@Override
					protected Integer doInBackground(Void... params) {

						if (PostData.isPhoneRegisted(getContext(), newPhone))
							return Config.PHONE_REGISTED;

						if (PostData.accountCreate(getContext(),
								MyProfileManager.getInstance().getMyID(),
								newPhone))
							return Config.SUCCESS;

						return Config.ERROR;
					}

					@Override
					protected void onPostExecute(Integer result) {
						progress.dismiss();
						if (result == Config.SUCCESS) {

							Utility.showDialog(Utility.CONFIRM, dialog,
									"Thêm thành công",
									"Số điện thoại mới đã được thêm vào tài khoản.");

							dataP.add(newPhone);
							adapter.notifyDataSetChanged();

							MyProfileManager.getInstance().addMyPhoneNumber(
									newPhone);

						} else if (result == Config.PHONE_REGISTED) {

							Utility.showDialog(Utility.ERROR, dialog,
									"Số điện thoại dẵ được đăng kí",
									"Chọn số điện thoại khác.");
						} else if (result == Config.ERROR) {

							Utility.showDialog(Utility.ERROR, dialog, "Lỗi",
									"Thêm thất bại. Xin thử lại sau.");
						}
					}

				}).execute();

			}
		});

	}

}
