package com.sgu.findyourfriend.screen;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.ContactAdapter;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.model.ContactBean;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.utils.Utility;

public class InviteFromContactsDialog extends Dialog {
	private ArrayList<ContactBean> contactData;
	private ArrayList<ContactBean> temptContactData;

	private ContactAdapter contactAdapter;
	private ListView listView;
	private Button btnNext;
	private ImageButton btnClose;
	private EditText editSearch;
	private Button btnSelectAll;
	private Context ctx;
	private ProgressBar pbLoader;
	private boolean isSelectMode = true;

	public InviteFromContactsDialog(Context context) {
		super(context, R.style.full_screen_dialog);
		this.ctx = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.invite_from_contact_dialog_custom);
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		pbLoader = (ProgressBar) findViewById(R.id.pbLoader);
		btnClose = (ImageButton) findViewById(R.id.imgClose);
		editSearch = (EditText) findViewById(R.id.editSearch);
		btnSelectAll = (Button) findViewById(R.id.btnSelectAll);
		btnNext = (Button) findViewById(R.id.btnNext);
		listView = (ListView) findViewById(R.id.list);

		contactData = new ArrayList<ContactBean>();
		Cursor phones = ctx.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		while (phones.moveToNext()) {
			String name = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

			String phoneNumber = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

			contactData.add(new ContactBean(name, phoneNumber, false));
		}
		phones.close();

		temptContactData = (ArrayList<ContactBean>) contactData.clone();
		contactAdapter = new ContactAdapter(ctx,
				R.layout.custom_contact_addfriend, temptContactData);
		listView.setAdapter(contactAdapter);

		btnClose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});

		editSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence textChanged, int arg1,
					int arg2, int arg3) {

				temptContactData.clear();
				for (ContactBean cb : contactData) {
					if (cb.getName().contains(textChanged))
						temptContactData.add(cb);
				}

				contactAdapter.notifyDataSetChanged();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

		btnSelectAll.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isSelectMode) {
					contactAdapter.checkAll();
					btnSelectAll.setText("Bỏ chọn");
				} else {
					contactAdapter.unCheckAll();
					btnSelectAll.setText("Chọn tất cả");
				}
				
				isSelectMode = !isSelectMode;
			}
		});

		btnNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// arraylist phone select
				final ArrayList<ContactBean> phoneSelected = getContactsSelected(temptContactData);

				if (phoneSelected.size() == 0) {
					Utility.showMessage(ctx, "Chọn ít nhất 1 người bạn.");
					return;
				}

				(new AsyncTask<Void, Void, HashMap<String, Integer>>() {

					private ArrayList<String> phoneNumbers = new ArrayList<String>();

					@Override
					protected void onPreExecute() {
						pbLoader.setVisibility(View.VISIBLE);
						for (ContactBean cb : phoneSelected) {
							phoneNumbers.add(cb.getPhoneNo());
						}
					}

					@Override
					protected HashMap<String, Integer> doInBackground(
							Void... params) {
						return PostData.userGetUserListWithoutFriend(ctx,
								MyProfileManager.getInstance().getMyID(),
								phoneNumbers);
					}

					@Override
					protected void onPostExecute(
							final HashMap<String, Integer> result) {

						Log.i("CONTACT", result.size() + "");
						for (ContactBean cb : phoneSelected) {
							if (result.containsKey(cb.getPhoneNo()))
								phoneSelected.remove(cb);
						
						}

						if (result.size() > 0) {

							(new AsyncTask<Void, Void, Void>() {

								@Override
								protected Void doInBackground(Void... params) {

									for (String kPhone : result.keySet()) {
										PostData.sendFriendRequest(
												ctx,
												MyProfileManager.getInstance().getMyID(), result
														.get(kPhone));
									}

									return null;
								}

								protected void onPostExecute(Void rs) {
									Utility.showMessage(
											ctx,
											"Đã gửi lời mời đến "
													+ result.size() + " bạn đang dùng ứng dụng");
									showAlertSendByMessage(phoneSelected);
								}

							}).execute();
						} else {
							showAlertSendByMessage(phoneSelected);
						}
						pbLoader.setVisibility(View.GONE);
						dismiss();
					}

				}).execute();
			}
		});
	}

	protected ArrayList<ContactBean> getContactsSelected(
			ArrayList<ContactBean> temptContactData) {
		ArrayList<ContactBean> tempt = new ArrayList<ContactBean>();

		for (ContactBean cb : temptContactData) {
			if (cb.isCheck()) {
				String formatPhoneNumber = cb.getPhoneNo();
				formatPhoneNumber = formatPhoneNumber.replace("+84", "0");
				formatPhoneNumber =  formatPhoneNumber.replace("-", "");
				formatPhoneNumber =  formatPhoneNumber.trim();

				Log.i("TRIM", formatPhoneNumber);

				tempt.add(new ContactBean(cb.getName(), formatPhoneNumber, true));
			}
		}

		return tempt;
	}

	private void showAlertSendByMessage(ArrayList<ContactBean> phoneSelected) {
		if (phoneSelected.size() == 0) {
			dismiss();
			return;
		}
		
		StringBuilder addresses = new StringBuilder();
		for (ContactBean cb : phoneSelected) {
			addresses.append(cb.getPhoneNo());
			addresses.append(",");
		}
		
		Intent smsIntent = new Intent(Intent.ACTION_VIEW);
		smsIntent.setType("vnd.android-dir/mms-sms");
		smsIntent.putExtra("address", addresses.toString());
		smsIntent.putExtra("sms_body", Utility.INVITE_MESSAGE);
		ctx.startActivity(smsIntent);
		
		dismiss();
	};
}
