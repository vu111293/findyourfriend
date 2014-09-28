/*
 * 	 This file is part of Find Your Friend.
 *
 *   Find Your Friend is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Find Your Friend is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Find Your Friend.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sgu.findyourfriend.screen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.MessageContactAdapter;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.service.ServiceUpdatePosition;
import com.sgu.findyourfriend.utils.PreferenceKeys;
import com.sgu.findyourfriend.utils.Utility;

public class SettingFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private final String TAG = SettingFragment.class.getName();

	private Preference preferLogout;
	private Preference preferChangePassword;
	private Preference preferHelp;
	private Preference preferDevInfo;
	private Preference preferAccountManager;
	private Preference preferEditProfile;
	private CheckBoxPreference switchRunBkg;
	private Preference preferenceWarningDefault;
	private Preference preferDefaultMessage;
	private Preference preferInstructor;
	private Preference preferShare;

	public SettingFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getPreferenceManager().setSharedPreferencesName(
				PreferenceKeys.SHARE_PREFERENCES_KEY);

		addPreferencesFromResource(R.xml.setting);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setupPrefs();

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	private void setupPrefs() {

		switchRunBkg = (CheckBoxPreference) getPreferenceScreen()
				.findPreference(PreferenceKeys.runBackground);

		// friend list setup
		preferenceWarningDefault = (Preference) getPreferenceScreen()
				.findPreference(PreferenceKeys.friendsWarning);

		preferenceWarningDefault
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						(new FriendSelectDialog(getActivity())).show();
						return false;
					}
				});

		// default message setting
		preferDefaultMessage = (Preference) getPreferenceScreen()
				.findPreference(PreferenceKeys.defaultMsg);
		preferDefaultMessage
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						setDefaultMessageDialog();
						return false;
					}
				});

		// prefer logout
		preferLogout = (Preference) getPreferenceScreen().findPreference(
				PreferenceKeys.preferLogout);
		preferLogout
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						MyProfileManager.getInstance().clear();

						Log.i(TAG, "logout here --------------------------");
						Intent intent = new Intent(getActivity(),
								MainLoginActivity.class);

						intent.putExtra("fromLogout", true);
						getActivity().startActivity(intent);
						getActivity().finish();
						return false;
					}
				});

		// prefer change password
		preferChangePassword = (Preference) getPreferenceScreen()
				.findPreference(PreferenceKeys.preferChangePassword);
		preferChangePassword
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						changePasswordDialog();
						return false;
					}
				});

		// prefer help
		preferHelp = (Preference) getPreferenceScreen().findPreference(
				PreferenceKeys.preferHelp);
		preferHelp
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// (new HelpInfoDialog(getActivity())).show();
						Utility.showScrollerDialog(Utility.CONFIRM, new Dialog(
								getActivity()), "Trợ giúp", getResources()
								.getString(R.string.help));
						return false;
					}
				});

		// prefer develop infomation
		preferDevInfo = (Preference) getPreferenceScreen().findPreference(
				PreferenceKeys.preferDevInfo);
		preferDevInfo
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						devInfoDialog();
						return false;
					}
				});

		// prefer develop information
		preferAccountManager = (Preference) getPreferenceScreen()
				.findPreference(PreferenceKeys.preferAccountManager);
		preferAccountManager
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						(new PhoneNumberManagerDialog(getActivity())).show();
						return false;
					}
				});

		// prefer edit profile
		preferEditProfile = (Preference) getPreferenceScreen().findPreference(
				PreferenceKeys.preferEditProfile);
		preferEditProfile
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						Intent intent = new Intent(getActivity(),
								EditProfileActivity.class);
						getActivity().startActivity(intent);
						return false;
					}
				});

		// prefer manual
		preferInstructor = (Preference) getPreferenceScreen().findPreference(
				PreferenceKeys.preferInstructor);
		preferInstructor
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						getActivity().startActivity(
								new Intent(getActivity(),
										InstructioActivity.class));
						return false;
					}
				});

		// prefer facebook
		preferShare = (Preference) getPreferenceScreen().findPreference(
				PreferenceKeys.preferShare);
		preferShare
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						onShareClick();
						return false;
					}
				});

	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		if (key.equals(PreferenceKeys.runBackground)) {
			SettingManager.getInstance().setUploadMyPosition(
					switchRunBkg.isChecked());
			if (switchRunBkg.isChecked()) {
				Intent i = new Intent(getActivity(),
						ServiceUpdatePosition.class);
				i.putExtra("isStop", false);
				getActivity().startService(i);

			} else {
				Intent i = new Intent(getActivity(),
						ServiceUpdatePosition.class);
				i.putExtra("isStop", true);
				getActivity().startService(i);

			}

			SettingManager.getInstance().getMapType();
		}

		Log.i(TAG, "Setting: " + key);
	}

	private String newPassword;
	private String renewPassword;
	private ProgressDialogCustom progressDialog;

	@SuppressWarnings("deprecation")
	private void changePasswordDialog() {

		final EditText mNewPassword;
		final EditText mReNewPassword;
		final Button mAcceptBtn;
		final Button mCancelBtn;

		final Dialog mdialog = new Dialog(getActivity());
		Window W = mdialog.getWindow();
		W.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		W.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		W.requestFeature(Window.FEATURE_NO_TITLE);
		W.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mdialog.setContentView(R.layout.dialog_change_password);

		((TextView) mdialog.findViewById(R.id.title)).setText("Đổi mật khẩu");
		mNewPassword = (EditText) mdialog.findViewById(R.id.pass_new);
		mReNewPassword = (EditText) mdialog.findViewById(R.id.pass_renewpass);

		mAcceptBtn = (Button) mdialog.findViewById(R.id.acceptBtn);
		mAcceptBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				newPassword = mNewPassword.getText().toString().trim();
				renewPassword = mReNewPassword.getText().toString().trim();

				final Dialog dialog = new Dialog(getActivity());

				mNewPassword.setBackgroundDrawable(getActivity().getResources()
						.getDrawable(R.drawable.edit_text));

				mReNewPassword.setBackgroundDrawable(getActivity()
						.getResources().getDrawable(R.drawable.edit_text));

				if (newPassword.equals("")) {
					mNewPassword.setBackgroundDrawable(getActivity()
							.getResources().getDrawable(
									R.drawable.edit_text_wrong));

					Utility.showDialog(Utility.ERROR, dialog,
							"Thiếu thông tin", "Nhập mật khẩu mới", "Đóng",
							new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									dialog.dismiss();
								}
							});
					return;
				} else if (newPassword.length() < Config.MIN_PASSWORD_LENGHT) {
					mNewPassword.setBackgroundDrawable(getActivity()
							.getResources().getDrawable(
									R.drawable.edit_text_wrong));

					Utility.showDialog(Utility.ERROR, dialog,
							"Thiếu thông tin", "Nhập mật khẩu mới ít nhất "
									+ Config.MIN_PASSWORD_LENGHT + " ký tự",
							"Đóng", new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									dialog.dismiss();
								}
							});

					return;
				} else if (!renewPassword.equals(newPassword)) {

					mReNewPassword.setBackgroundDrawable(getActivity()
							.getResources().getDrawable(
									R.drawable.edit_text_wrong));

					Utility.showDialog(Utility.ERROR, dialog,
							"Mật khẩu không trùng khớp",
							"Mật khẩu phải tương tự mật khẩu mới.", "Đóng",
							new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									dialog.dismiss();
								}
							});

					return;
				}

				(new AsyncTask<Void, Void, Boolean>() {

					@Override
					protected void onPreExecute() {
						progressDialog = new ProgressDialogCustom(getActivity());
						progressDialog.show();
					}

					@Override
					protected Boolean doInBackground(Void... params) {
						return PostData.userChangePassword(getActivity(),
								MyProfileManager.getInstance().getMyID(),
								SettingManager.getInstance().getPasswordAutoLogin(), newPassword);
					}

					@Override
					protected void onPostExecute(Boolean result) {

						progressDialog.dismiss();
						if (result) {
							Utility.showDialog(Utility.CONFIRM, dialog,
									"Đổi mật khẩu thành công",
									"Mật khẩu của bạn đã được thay đổi.",
									"Đóng", new OnClickListener() {

										@Override
										public void onClick(View arg0) {
											dialog.dismiss();
											mdialog.dismiss();
										}
									});

							SettingManager.getInstance().savePasswordAutoLogin(
									newPassword);
						} else {

							Utility.showDialog(
									Utility.ERROR,
									dialog,
									"Đổi mật khẩu thất bại",
									"Xãy ra lỗi trong quá trình đổi mật khẩu. Xin thử lại sau.",
									"Đóng", new OnClickListener() {

										@Override
										public void onClick(View arg0) {
											dialog.dismiss();
											mdialog.dismiss();
										}
									});
						}

					}

				}).execute();

			}
		});

		mCancelBtn = (Button) mdialog.findViewById(R.id.cancelBtn);

		mCancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mdialog.dismiss();
			}
		});

		mdialog.show();
	}

	private void deleteAccountDialog() {

		final Dialog dialog = new Dialog(getActivity());

		Utility.showDialog(Utility.WARNING, dialog, "Xóa tài khoản",
				"Bạn có chắc muốn xóa tài khoản?", "Đồng ý",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						MyProfileManager.getInstance().clear();
						getActivity().finish();
						Intent intent = new Intent(getActivity(),
								MainLoginActivity.class);
						getActivity().startActivity(intent);
					}
				}, "Thôi", new OnClickListener() {

					@Override
					public void onClick(View v) {

					}
				});

		dialog.show();
	}

	private void devInfoDialog() {
		Utility.showScrollerDialog(Utility.CONFIRM, new Dialog(getActivity()),
				"Thông tin ứng dụng", getResources().getString(R.string.about));
	}

	private void setDefaultMessageDialog() {
		final Dialog dialog = new Dialog(getActivity());
		Window W = dialog.getWindow();
		W.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		W.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		W.requestFeature(Window.FEATURE_NO_TITLE);
		W.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.setContentView(R.layout.dialog_default_message_setting);

		((TextView) dialog.findViewById(R.id.title))
				.setText("Tin nhắn mặc định");
		((TextView) dialog.findViewById(R.id.content)).setText(SettingManager
				.getInstance().getDefaultMsg());

		((Button) dialog.findViewById(R.id.btnLeft))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						SettingManager.getInstance().setDefaultMsg(
								((TextView) dialog.findViewById(R.id.content))
										.getText().toString());
						dialog.dismiss();
					}
				});

		((Button) dialog.findViewById(R.id.btnRight))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

		dialog.show();

	}

	class FriendSelectDialog extends Dialog {

		private Context ctx;
		private ArrayList<Friend> data;
		private MessageContactAdapter adapter;
		private ListView lv;
		private HashSet<String> ids; // selected before

		private Button btnSelect;
		private Button btnAccept;
		private boolean isSelectMode; // = false;

		public FriendSelectDialog(Context context) {
			super(context, R.style.full_screen_dialog);
			this.ctx = context;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.friend_default_warning_layout);
			getWindow().setLayout(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);

			// setup view
			lv = (ListView) findViewById(R.id.list);

			data = new ArrayList<Friend>(
					FriendManager.getInstance().pureFriends);

			ids = (HashSet<String>) SettingManager.getInstance()
					.getDefaultWarning();

			for (Friend f : data) {
				if (ids.contains(f.getUserInfo().getId() + ""))
					f.setCheck(true);
				else
					f.setCheck(false);
			}

			adapter = new MessageContactAdapter(ctx,
					R.layout.custom_contact_addfriend, data);

			lv.setAdapter(adapter);

			btnSelect = (Button) findViewById(R.id.btnSelectAll);

			if (isSelectAll()) {
				isSelectMode = false;
				btnSelect.setText("Bỏ chọn");
			} else {
				isSelectMode = true;
				btnSelect.setText("Chọn tất cả");
			}

			btnSelect.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (isSelectMode) {
						adapter.checkAll();
						btnSelect.setText("Bỏ chọn");
					} else {
						adapter.unCheckAll();
						btnSelect.setText("Chọn tất cả");
					}

					isSelectMode = !isSelectMode;
				}
			});

			btnAccept = (Button) findViewById(R.id.btnAccept);
			btnAccept.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					HashSet<String> hset = new HashSet<String>();
					for (Friend f : data) {
						if (f.isCheck()) {
							hset.add(f.getUserInfo().getId() + "");
						}
					}

					SettingManager.getInstance().setDefaultWarning(hset);

					dismiss();

				}
			});

		}

		private boolean isSelectAll() {
			for (Friend f : data) {
				if (!f.isCheck())
					return false;
			}
			return true;
		}
	}

	private void onShareClick() {
		Resources resources = getResources();

		Intent emailIntent = new Intent();
		emailIntent.setAction(Intent.ACTION_SEND);
		// Native email client doesn't currently support HTML, but it doesn't
		// hurt to try in case they fix it
		emailIntent.putExtra(Intent.EXTRA_TEXT,
				Html.fromHtml(resources.getString(R.string.share_content)));
		emailIntent.putExtra(Intent.EXTRA_SUBJECT,
				resources.getString(R.string.share_title));
		emailIntent.setType("message/rfc822");

		PackageManager pm = getActivity().getPackageManager();
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.setType("text/plain");

		Intent openInChooser = Intent.createChooser(emailIntent,
				resources.getString(R.string.share_title));

		List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
		List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
		for (int i = 0; i < resInfo.size(); i++) {
			// Extract the label, append it, and repackage it in a LabeledIntent
			ResolveInfo ri = resInfo.get(i);
			String packageName = ri.activityInfo.packageName;
			if (packageName.contains("android.email")) {
				emailIntent.setPackage(packageName);
			} else if (packageName.contains("twitter")
					|| packageName.contains("facebook")
					|| packageName.contains("mms")
					|| packageName.contains("android.gm")) {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName(packageName,
						ri.activityInfo.name));
				intent.setAction(Intent.ACTION_SEND);
				intent.setType("text/plain");
				if (packageName.contains("twitter")) {
					intent.putExtra(Intent.EXTRA_TEXT,
							resources.getString(R.string.share_title));
				} else if (packageName.contains("facebook")) {
					intent.putExtra(Intent.EXTRA_TEXT,
							resources.getString(R.string.share_title));
				} else if (packageName.contains("mms")) {
					intent.putExtra(Intent.EXTRA_TEXT,
							resources.getString(R.string.share_title));
				} else if (packageName.contains("android.gm")) {
					intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(resources
							.getString(R.string.share_content)));
					intent.putExtra(Intent.EXTRA_SUBJECT,
							resources.getString(R.string.share_title));
					intent.setType("message/rfc822");
				}

				intentList.add(new LabeledIntent(intent, packageName, ri
						.loadLabel(pm), ri.icon));
			}
		}

		// convert intentList to array
		LabeledIntent[] extraIntents = intentList
				.toArray(new LabeledIntent[intentList.size()]);

		openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
		startActivity(openInChooser);
	}

}
