package com.sgu.findyourfriend.screen;

import java.util.HashSet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.sgu.findyourfriend.R;
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

	private MultiSelectListPreference contactOfFiends;
	private Preference preferLogout;
	private Preference preferChangePassword;
	private Preference preferDeleteAccount;
	private Preference preferHelp;
	private Preference preferDevInfo;
	private Preference preferAccountManager;
	private Preference preferEditProfile;
	private SwitchPreference switchRunBkg;

	public SettingFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getPreferenceManager().setSharedPreferencesName(
				PreferenceKeys.SHARE_PREFERENCES_KEY);

		addPreferencesFromResource(R.xml.setting);
		// getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		// setupPrefs();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setupPrefs();

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	private void setupPrefs() {

		switchRunBkg = (SwitchPreference) getPreferenceScreen().findPreference(
				PreferenceKeys.runBackground);

		int numFriend = FriendManager.getInstance().hmMemberFriends.size();
		String friendNames[] = new String[numFriend];
		HashSet<String> idOfFriends = new HashSet<String>();

		int i = 0;
		for (int k : FriendManager.getInstance().hmMemberFriends.keySet()) {
			Friend f = FriendManager.getInstance().hmMemberFriends.get(k);
			friendNames[i] = f.getUserInfo().getName();
			idOfFriends.add(f.getUserInfo().getId() + "");
			i++;
		}

		// setup check all
		friendNames[0] = "Tất cả";

		contactOfFiends = (MultiSelectListPreference) getPreferenceScreen()
				.findPreference(PreferenceKeys.friendsWarning);
		contactOfFiends.setEntries(friendNames);
		contactOfFiends.setValues(idOfFriends);

		// prefer logout
		preferLogout = (Preference) getPreferenceScreen().findPreference(
				PreferenceKeys.preferLogout);
		preferLogout
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						MyProfileManager.getInstance().clear();
						FriendManager.getInstance().setStop(true);
						getActivity().finish();
						Intent intent = new Intent(getActivity(),
								MainLoginActivity.class);
						getActivity().startActivity(intent);
						return false;
					}
				});

		// prefer help
		preferLogout = (Preference) getPreferenceScreen().findPreference(
				PreferenceKeys.preferLogout);
		preferLogout
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						MyProfileManager.getInstance().clear();
						getActivity().finish();
						Intent intent = new Intent(getActivity(),
								MainLoginActivity.class);
						getActivity().startActivity(intent);
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

		// prefer delete account
		preferDeleteAccount = (Preference) getPreferenceScreen()
				.findPreference(PreferenceKeys.preferDeleteAccount);
		preferDeleteAccount
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						Utility.showMessage(getActivity(),
								"Tính năng chưa hỗ trợ");
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
						(new HelpInfoDialog(getActivity())).show();
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

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
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

	private String oldPassword;
	private String newPassword;
	private ProgressDialogCustom progressDialog;

	@SuppressWarnings("deprecation")
	private void changePasswordDialog() {

		final EditText mCurrentPassword;
		final EditText mNewPassword;
		final CheckBox mShowPassword;
		final Button mAcceptBtn;
		final Button mCancelBtn;

		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.create();

		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.layout_changepassword, null);

		alertDialog.setView(view);
		alertDialog.setTitle("Đổi mật khẩu");

		mCurrentPassword = (EditText) view.findViewById(R.id.pass_current);
		mNewPassword = (EditText) view.findViewById(R.id.pass_newpass);
		mShowPassword = (CheckBox) view.findViewById(R.id.chbx_showpass);

		mShowPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					mNewPassword
							.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
				} else {
					mNewPassword.setInputType(129);
				}
			}
		});

		mAcceptBtn = (Button) view.findViewById(R.id.acceptBtn);
		mAcceptBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				oldPassword = mCurrentPassword.getText().toString().trim();
				newPassword = mNewPassword.getText().toString().trim();

				mCurrentPassword.setBackgroundDrawable(getActivity()
						.getResources().getDrawable(R.drawable.edit_text));

				mNewPassword.setBackgroundDrawable(getActivity().getResources()
						.getDrawable(R.drawable.edit_text));

				if (oldPassword.equals("")) {
					mCurrentPassword.setBackgroundDrawable(getActivity()
							.getResources().getDrawable(
									R.drawable.edit_text_wrong));
					Utility.showAlertDialog(getActivity(), "",
							"Nhập mật khẩu hiện tại", false);
					return;
				} else if (!oldPassword.equals(SettingManager.getInstance()
						.getPasswordAutoLogin())) {
					mCurrentPassword.setBackgroundDrawable(getActivity()
							.getResources().getDrawable(
									R.drawable.edit_text_wrong));
					Utility.showAlertDialog(getActivity(), "",
							"Nhập mật không chính xác", false);
					return;
				}

				if (newPassword.equals("")) {
					mNewPassword.setBackgroundDrawable(getActivity()
							.getResources().getDrawable(
									R.drawable.edit_text_wrong));
					Utility.showAlertDialog(getActivity(), "",
							"Nhập mật khẩu mới", false);
					return;
				} else if (newPassword.length() < Config.MIN_PASSWORD_LENGHT) {
					mNewPassword.setBackgroundDrawable(getActivity()
							.getResources().getDrawable(
									R.drawable.edit_text_wrong));
					Utility.showAlertDialog(getActivity(), "",
							"Nhập mật khẩu mới ít nhất "
									+ Config.MIN_PASSWORD_LENGHT + " ký tự",
							false);
					return;
				}

				if (oldPassword.equals(newPassword)) {
					mNewPassword.setBackgroundDrawable(getActivity()
							.getResources().getDrawable(
									R.drawable.edit_text_wrong));
					Utility.showAlertDialog(getActivity(), "",
							"Nhập mật phải khác mật khẩu hiện tại", false);
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
								oldPassword, newPassword);
					}

					@Override
					protected void onPostExecute(Boolean result) {

						progressDialog.dismiss();
						if (result) {
							Utility.showAlertDialog(getActivity(), "",
									"Đổi mật khẩu thành công", false);
							SettingManager.getInstance().savePasswordAutoLogin(
									newPassword);
						} else {
							Utility.showAlertDialog(getActivity(), "",
									"Đổi mật khẩu thất bại", false);
						}

						alertDialog.dismiss();
					}

				}).execute();

			}
		});

		mCancelBtn = (Button) view.findViewById(R.id.cancelBtn);

		mCancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});

		// Show Alert Message
		alertDialog.show();
	}

	@SuppressWarnings("deprecation")
	private void deleteAccountDialog() {
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.create();

		alertDialog.setTitle("Xóa tài khoản");
		alertDialog.setMessage("Bạn có chắc muốn xóa tài khoản?");

		alertDialog.setButton("Đồng ý", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				MyProfileManager.getInstance().clear();
				getActivity().finish();
				Intent intent = new Intent(getActivity(),
						MainLoginActivity.class);
				getActivity().startActivity(intent);
			}
		});

		alertDialog.setButton2("Thôi", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		// Show Alert Message
		alertDialog.show();
	}

	@SuppressWarnings("deprecation")
	private void devInfoDialog() {
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.create();

		alertDialog.setTitle("Thông tin nhà phát triển");
		alertDialog.setMessage("Sgu Coporation 2014");

		alertDialog.setButton("Thoát", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		// Show Alert Message
		alertDialog.show();
	}

}
