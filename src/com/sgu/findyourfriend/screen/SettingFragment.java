package com.sgu.findyourfriend.screen;

import java.util.ArrayList;
import java.util.HashSet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
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

	private MultiSelectListPreference contactOfFiends;
	private Preference preferLogout;
	private Preference preferChangePassword;
	private Preference preferDeleteAccount;
	private Preference preferHelp;
	private Preference preferDevInfo;
	private Preference preferAccountManager;
	private Preference preferEditProfile;
	private CheckBoxPreference switchRunBkg;
	private Preference preferenceWarningDefault;
	private Preference preferDefaultMessage;

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

		switchRunBkg = (CheckBoxPreference) getPreferenceScreen().findPreference(
				PreferenceKeys.runBackground);

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
						FriendManager.getInstance().setStop(true);
						getActivity().finish();
						Intent intent = new Intent(getActivity(),
								MainLoginActivity.class);
						
						intent.putExtra("fromLogout", true);
						
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

		
		final Dialog dialog = new Dialog(getActivity());
		Window W = dialog.getWindow();
		W.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		W.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		W.requestFeature(Window.FEATURE_NO_TITLE);
		W.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.setContentView(R.layout.layout_changepassword);

		((TextView) dialog.findViewById(R.id.title)).setText("Đổi mật khẩu");
		mCurrentPassword = (EditText) dialog.findViewById(R.id.pass_current);
		mNewPassword = (EditText) dialog.findViewById(R.id.pass_newpass);
		mShowPassword = (CheckBox) dialog.findViewById(R.id.chbx_showpass);

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

		mAcceptBtn = (Button) dialog.findViewById(R.id.acceptBtn);
		mAcceptBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				oldPassword = mCurrentPassword.getText().toString().trim();
				newPassword = mNewPassword.getText().toString().trim();
				
				final Dialog dialog = new Dialog(getActivity());

				mCurrentPassword.setBackgroundDrawable(getActivity()
						.getResources().getDrawable(R.drawable.edit_text));

				mNewPassword.setBackgroundDrawable(getActivity().getResources()
						.getDrawable(R.drawable.edit_text));

				if (oldPassword.equals("")) {
					mCurrentPassword.setBackgroundDrawable(getActivity()
							.getResources().getDrawable(
									R.drawable.edit_text_wrong));
					
					Utility.showDialog(Utility.ERROR, dialog, "Thiếu thông tin", "Nhập mật khẩu hiện tại",
							"Đóng", new OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									dialog.dismiss();
								}
							});
					return;
				} else if (!oldPassword.equals(SettingManager.getInstance()
						.getPasswordAutoLogin())) {
					mCurrentPassword.setBackgroundDrawable(getActivity()
							.getResources().getDrawable(
									R.drawable.edit_text_wrong));
					
					Utility.showDialog(Utility.ERROR, dialog, "Sai mật khẩu", "Mật khẩu không chính xác." +
							" Nếu quên mật khẩu bạn có thể khôi phục lại ở trang đăng nhập",
							"Đóng", new OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									dialog.dismiss();
								}
							});
					
					return;
				}

				if (newPassword.equals("")) {
					mNewPassword.setBackgroundDrawable(getActivity()
							.getResources().getDrawable(
									R.drawable.edit_text_wrong));
					
					Utility.showDialog(Utility.ERROR, dialog, "Thiếu thông tin", "Nhập mật khẩu mới",
							"Đóng", new OnClickListener() {
								
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
					
					Utility.showDialog(Utility.ERROR, dialog, "Thiếu thông tin", 
							"Nhập mật khẩu mới ít nhất " + Config.MIN_PASSWORD_LENGHT + " ký tự",
							"Đóng", new OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									dialog.dismiss();
								}
							});
					
					return;
				}

				if (oldPassword.equals(newPassword)) {
					mNewPassword.setBackgroundDrawable(getActivity()
							.getResources().getDrawable(
									R.drawable.edit_text_wrong));
					
					Utility.showDialog(Utility.ERROR, dialog, "Mật khẩu không đổi", 
							"Nhập mật phải khác mật khẩu hiện tại",
							"Đóng", new OnClickListener() {
								
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
								oldPassword, newPassword);
					}

					@Override
					protected void onPostExecute(Boolean result) {

						progressDialog.dismiss();
						if (result) {
							Utility.showDialog(Utility.CONFIRM, dialog, "Đổi mật khẩu thành công",
									"Mật khẩu của bạn đã được thay đổi.",
									"Đóng", new OnClickListener() {
										
										@Override
										public void onClick(View arg0) {
											dialog.dismiss();
										}
									});
							
							SettingManager.getInstance().savePasswordAutoLogin(
									newPassword);
						} else {
							
							Utility.showDialog(Utility.ERROR, dialog, "Đổi mật khẩu thất bại",
									"Xãy ra lỗi trong quá trình đổi mật khẩu. Xin thử lại sau.",
									"Đóng", new OnClickListener() {
										
										@Override
										public void onClick(View arg0) {
											dialog.dismiss();
										}
									});
						}

					}

				}).execute();

			}
		});

		mCancelBtn = (Button) dialog.findViewById(R.id.cancelBtn);

		mCancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	@SuppressWarnings("deprecation")
	private void deleteAccountDialog() {
		
		final Dialog dialog = new Dialog(getActivity());
		
		Utility.showDialog(Utility.WARNING, dialog,
				"Xóa tài khoản", "Bạn có chắc muốn xóa tài khoản?",
				"Đồng ý", new OnClickListener() {
					
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

	@SuppressWarnings("deprecation")
	private void devInfoDialog() {
		final Dialog dialog = new Dialog(getActivity());
		Utility.showDialog(Utility.CONFIRM, dialog,
				"Thông tin nhà phát triển",
				"Sgu Coporation 2014",
				"Thoát", new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
		dialog.show();
	}

	
	private void setDefaultMessageDialog() {
		final Dialog dialog = new Dialog(getActivity());
		Window W = dialog.getWindow();
		W.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		W.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		W.requestFeature(Window.FEATURE_NO_TITLE);
		W.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.setContentView(R.layout.default_message_setting);

		
		((TextView) dialog.findViewById(R.id.title)).setText("Tin nhắn mặc định");
		((TextView) dialog.findViewById(R.id.content)).setText(SettingManager.getInstance().getDefaultMsg());

		((Button) dialog.findViewById(R.id.btnLeft))
				.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						SettingManager.getInstance().setDefaultMsg(
								((TextView) dialog.findViewById(R.id.content)).getText().toString());
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

		private Button btnSelect;
		private Button btnAccept;
		private boolean isSelectMode = false;

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

			HashSet<String> ids = (HashSet<String>) SettingManager
					.getInstance().getDefaultWarning();

			for (Friend f : data) {
				if (ids.contains(f.getUserInfo().getId() + ""))
					f.setCheck(true);
			}

			adapter = new MessageContactAdapter(ctx,
					R.layout.custom_contact_addfriend, data);

			lv.setAdapter(adapter);

			btnSelect = (Button) findViewById(R.id.btnSelectAll);
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
	}

}
