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

	private String newPhone;

	public PhoneNumberManagerDialog(Context context) {
		super(context, R.style.full_screen_dialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.account_manager);
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

		btnAddNewPhone = (Button) findViewById(R.id.btnAddPhone);
		btnAddNewPhone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				newPhone = editNewPhone.getText().toString().trim();
				editNewPhone.setText("");
				InputMethodManager imm = (InputMethodManager) getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editNewPhone.getWindowToken(), 0);

				if (!Utility.isPhoneNumberCorrect(newPhone)) {
					editNewPhone.setBackgroundDrawable(getContext()
							.getResources().getDrawable(
									R.drawable.edit_text_wrong));

					Utility.showAlertDialog(getContext(), "Cảnh báo",
							"Số điện thoại không đúng", false);
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

							Utility.showAlertDialog(getContext(), "Thông báo",
									"Thêm số điện thoại thành công", false);

							dataP.add(newPhone);
							adapter.notifyDataSetChanged();

							MyProfileManager.getInstance().addMyPhoneNumber(newPhone);

						} else if (result == Config.PHONE_REGISTED) {
							Utility.showAlertDialog(getContext(), "Cảnh báo",
									"Số điện thoại này đã được đăng kí", false);
						} else if (result == Config.ERROR) {
							Utility.showAlertDialog(getContext(), "Cảnh báo",
									"Thêm thất bại", false);
						}
					}

				}).execute();

			}
		});

	}

}
