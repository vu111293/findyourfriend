package com.sgu.findyourfriend.screen;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.CustomAdapterFriendStatus;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MessageManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.mgr.SoundManager;
import com.sgu.findyourfriend.model.TempMessage;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.service.ServiceUpdatePosition;
import com.sgu.findyourfriend.utils.Utility;

public class MainActivity extends SherlockFragmentActivity {

	public static String TAG = "MAIN ACTIVITY";
	public static final String MAP_TAG = "map_fragment";
	public static final String MESSAGE_TAG = "message_fragment";
	public static final String REQUEST_TAG = "request_fragment";
	public static final String CATEGORIES_TAG = "categories_fragment";

	private FragmentTabHost mTabHost;
	private MainActivity mMain = this;

	// private int currentTab;

	private DrawerLayout mDrawerLayout;
	private CustomAdapterFriendStatus adapter;

	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private TextView TitleTextView;

	private View mRootView;

	private ProgressBar pbLoader;

	private int backButtonCount;

	private AlertDialog alertDialog = null;
	private Handler handler;

	private boolean isRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "onCreate main app");

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		ActionBar mAcionBar = getSupportActionBar();

		mAcionBar.setDisplayShowHomeEnabled(false);
		mAcionBar.setDisplayShowTitleEnabled(false);
		mAcionBar.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(R.color.bar_color)));

		getApplicationContext().registerReceiver(mHandleMessageReceiver,
				new IntentFilter(com.sgu.findyourfriend.mgr.Config.NOTIFY_UI));

		(new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				setContentView(R.layout.loadingscreen);

				backButtonCount = 1;
				LayoutInflater mInflater = LayoutInflater
						.from(getApplicationContext());
				mRootView = mInflater.inflate(R.layout.activity_main, null);

				// get loader
				pbLoader = (ProgressBar) findViewById(R.id.pbLoader);
				pbLoader.setVisibility(View.VISIBLE);
			}

			@Override
			protected Void doInBackground(Void... arg0) {

				// inti profile
				// MyProfileManager.getInstance().init(getApplicationContext());

				// init friend manager
				FriendManager.getInstance().init(getApplicationContext());

				// demo for virtual device test
				// FriendManager.getInstance().loadFriend();

				// init message manager
				MessageManager.getInstance().init(mMain);

				// init sound manager
				SoundManager.getInstance().init(getApplicationContext());

				// init setting
				// SettingManager.getInstance().init(getApplicationContext());

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				FriendManager.getInstance().setup();
				initView();

				// refs to context
				pbLoader.setVisibility(View.GONE);
				setContentView(mRootView);

				isRegister = true;

				// precheck new notify
				preCheckNewNotify();

				checkStartUpdateService();
			}

		}).execute();

		// setup update online status
		handler = new Handler();
		final long timeUpdate = SettingManager.getInstance()
				.getTimeUpdateOnlineStatus();

		final Runnable r = new Runnable() {
			public void run() {
				(new StatusUpdate()).execute();
				handler.postDelayed(this, timeUpdate);
			}
		};

		handler.postDelayed(r, timeUpdate);

	}

	protected void checkStartUpdateService() {
		if (SettingManager.getInstance().isUploadMyPosition()) {
			Log.i(TAG, "start update---------------------------------");
			Intent i = new Intent(this, ServiceUpdatePosition.class);
			i.putExtra("isStop", false);
			startService(i);
		} else {
			Log.i(TAG, "start stop");
		}

	}

	private void preCheckNewNotify() {

		if (SettingManager.getInstance().getNoNewMesssage() > 0) {
			Intent intent = new Intent(Config.NOTIFY_UI);
			intent.putExtra(Config.MESSAGE_NOTIFY, Config.SHOW);
			getApplicationContext().sendBroadcast(intent);

			List<TempMessage> tempMessages = MessageManager.getInstance()
					.quickGetAllTempMessage(getApplicationContext());

			// broad cast local message
			for (TempMessage tmp : tempMessages) {

				Log.i(TAG, "broadcast " + tmp.getMessage());
				Intent intent3 = new Intent(Config.LOCAL_MESSAGE_ACTION);
				intent3.putExtra(Config.EXTRA_MESSAGE, tmp.getMessage());
				getApplicationContext().sendBroadcast(intent3);
			}

		}

		if (SettingManager.getInstance().getNoNewRequest() > 0) {
			Intent intent2 = new Intent(Config.NOTIFY_UI);
			intent2.putExtra(Config.FRIEND_REQUEST_NOTIFY, Config.SHOW);
			getApplicationContext().sendBroadcast(intent2);
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void initView() {

		mTabHost = (FragmentTabHost) mRootView
				.findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		if (GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext()) == ConnectionResult.SUCCESS)
			mTabHost.addTab(
					mTabHost.newTabSpec(MAP_TAG).setIndicator("",
							getResources().getDrawable(R.drawable.ic_location)),
					MapContainerFragment.class, null);

		mTabHost.addTab(
				mTabHost.newTabSpec(MESSAGE_TAG).setIndicator("",
						getResources().getDrawable(R.drawable.ic_grp)),
				MessageContainerFragment.class, null);

		mTabHost.addTab(
				mTabHost.newTabSpec(REQUEST_TAG).setIndicator("",
						getResources().getDrawable(R.drawable.ic_usercheck)),
				FriendRequestsContainerFragment.class, null);

		mTabHost.addTab(
				mTabHost.newTabSpec(CATEGORIES_TAG).setIndicator(
						"",
						getResources().getDrawable(
								R.drawable.ic_action_settings)),
				CategoriesContainerFragment.class, null);

		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {

				backButtonCount = 1;

				if (!mTabHost.getCurrentTabTag().equals(MESSAGE_TAG)) {
					InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					keyboard.hideSoftInputFromWindow(
							mTabHost.getApplicationWindowToken(), 0);
				}
			}
		});

		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
			final TextView tv = (TextView) mTabHost.getTabWidget()
					.getChildAt(i).findViewById(android.R.id.title);
			if (tv == null)
				continue;
			else
				tv.setTextSize(10);
		}

		// setup sliding bar
		ActionBar mActionBar = getSupportActionBar();

		// android.app.ActionBar mActionBar = mMain.getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(R.color.bar_color)));
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);

		mDrawerLayout = (DrawerLayout) mRootView
				.findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) mRootView.findViewById(R.id.drawer_list_right);

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		adapter = new CustomAdapterFriendStatus(this,
				R.layout.custom_friend_status,
				FriendManager.getInstance().pureFriends);
		mDrawerList.setAdapter(adapter);

		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int pos, long arg3) {

				Log.i(TAG, "on item click");

				String[] items = { "Xem trên bản đồ", "Gọi", "Nhắn tin", "Khác" };
				AlertDialog.Builder builder = new AlertDialog.Builder(mMain);
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							// view position on map
							mTabHost.setCurrentTabByTag(MAP_TAG);
							Intent intent = new Intent(Config.MAIN_ACTION);
							intent.putExtra(Config.ZOOM_POSITION_ACTION,
									adapter.getItem(pos).getUserInfo().getId());
							getApplicationContext().sendBroadcast(intent);
							break;
						case 1:
							callTask(adapter.getItem(pos).getUserInfo().getId());
//							
//							
//							
//							
//							Intent callIntent = new Intent(Intent.ACTION_CALL);
//							callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//							callIntent.setData(Uri.parse("tel:"
//									+ adapter.getItem(pos).getNumberLogin()
//											.get(0)));
//							mTabHost.getContext().startActivity(callIntent);
							break;

						case 2:
							mTabHost.setCurrentTabByTag(MESSAGE_TAG);

							Handler handler = new Handler();

							final Runnable r = new Runnable() {
								public void run() {

									Intent intent2 = new Intent(
											Config.MAIN_ACTION);
									intent2.putExtra(
											Config.EDIT_MESSAGE_ACTION, adapter
													.getItem(pos).getUserInfo()
													.getId());
									getApplicationContext().sendBroadcast(
											intent2);
								}
							};

							handler.postDelayed(r, 10);

							break;

						case 3:
							break;
						}

						mDrawerLayout.closeDrawer(mDrawerList);
					}
				}).setNegativeButton("Quay lại",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});

				builder.show();
			}
		});

		getSupportActionBar().setCustomView(mCustomView);

		// mMain.getActionBar().setCustomView(mCustomView);
		//
		mActionBar.setDisplayShowCustomEnabled(true);

		// set listener for item control
		mCustomView.findViewById(R.id.imgFriendList).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
							mDrawerLayout.closeDrawer(mDrawerList);
						} else {
							mDrawerLayout.openDrawer(mDrawerList);

						}
					}
				});

		mCustomView.findViewById(R.id.imgAlert).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						EmergenceDialog dialog = new EmergenceDialog(mMain);
						dialog.show();
					}
				});

//		mCustomView.findViewById(R.id.imgContacts).setOnClickListener(
//				new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						// showOptionsContacts();
//					}
//				});
	}

	public void notifyDataChange() {
		adapter.notifyDataSetChanged();
	}

	public void callTask(int friendId) {

		ArrayList<String> phs = FriendManager.getInstance().hmMemberFriends
				.get(friendId).getNumberLogin();

		if (phs.size() == 0) {
			Toast.makeText(mMain, "Không có số điện thoại", Toast.LENGTH_LONG)
					.show();
			return;
		}

		if (phs.size() == 1) {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			callIntent.setData(Uri.parse("tel:" + phs.get(0)));
			mTabHost.getContext().startActivity(callIntent);
			return;
		}
		
		final String[] phonenumbers = new String[phs.size()];

		for (int i = 0; i < phs.size(); ++i) {
			phonenumbers[i] = phs.get(i);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(mMain);
		builder.setTitle("Chọn số cần gọi");
		builder.setItems(phonenumbers, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				callIntent.setData(Uri.parse("tel:" + phonenumbers[which]));
				mTabHost.getContext().startActivity(callIntent);
			}
		}).setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});

		builder.show();
	}
	
	
	@Override
	public void onBackPressed() {
		boolean isPopFragment = false;

		String currentTabTag = mTabHost.getCurrentTabTag();
		if (currentTabTag.equals(MAP_TAG)) {
			isPopFragment = ((BaseContainerFragment) getSupportFragmentManager()
					.findFragmentByTag(MAP_TAG)).popFragment();
		} else if (currentTabTag.equals(MESSAGE_TAG)) {
			isPopFragment = ((BaseContainerFragment) getSupportFragmentManager()
					.findFragmentByTag(MESSAGE_TAG)).popFragment();
		} else if (currentTabTag.equals(REQUEST_TAG)) {
			isPopFragment = ((BaseContainerFragment) getSupportFragmentManager()
					.findFragmentByTag(REQUEST_TAG)).popFragment();
		} else if (currentTabTag.equals(CATEGORIES_TAG)) {
			isPopFragment = ((BaseContainerFragment) getSupportFragmentManager()
					.findFragmentByTag(CATEGORIES_TAG)).popFragment();
		}

		if (!isPopFragment) {
			if (backButtonCount < 1) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			} else {
				Utility.showMessage(getApplicationContext(),
						"Nhấn nút trở lại thêm lần nữa nếu bạn thật sự muốn thoát.");
				backButtonCount--;
			}
		}
	}

	public void gotoTabByTagName(String tabTag) {
		mDrawerLayout.closeDrawer(mDrawerList);
		mTabHost.setCurrentTabByTag(tabTag);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MessageManager.getInstance().destroy();
		if (isRegister)
			getApplicationContext().unregisterReceiver(mHandleMessageReceiver);
	}


	class StatusUpdate extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			PostData.login(getApplicationContext(), SettingManager
					.getInstance().getPhoneAutoLogin(), SettingManager
					.getInstance().getPasswordAutoLogin());
			return null;
		}

	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context ctx, Intent intent) {
			String action = intent.getAction();

			if (action.equals(Config.NOTIFY_UI)) {

				if (intent.hasExtra(Config.MESSAGE_NOTIFY)) {
					if (intent.getIntExtra(Config.MESSAGE_NOTIFY, -1) == Config.SHOW) {

						if (!mTabHost.getCurrentTabTag().equals(MESSAGE_TAG))
							((ImageView) mTabHost.getTabWidget()
									.getChildTabViewAt(1)
									.findViewById(android.R.id.icon))
									.setImageDrawable(getResources()
											.getDrawable(
													R.drawable.ic_grp_trigger));
					} else {
						((ImageView) mTabHost.getTabWidget()
								.getChildTabViewAt(1)
								.findViewById(android.R.id.icon))
								.setImageDrawable(getResources().getDrawable(
										R.drawable.ic_grp));
					}

				} else if (intent.hasExtra(Config.FRIEND_REQUEST_NOTIFY)) {
					if (intent.getIntExtra(Config.FRIEND_REQUEST_NOTIFY, -1) == Config.SHOW) {
						if (!mTabHost.getCurrentTabTag().equals(REQUEST_TAG))
							((ImageView) mTabHost.getTabWidget()
									.getChildTabViewAt(2)
									.findViewById(android.R.id.icon))
									.setImageDrawable(getResources()
											.getDrawable(
													R.drawable.ic_usercheck_trigger));
					} else {
						((ImageView) mTabHost.getTabWidget()
								.getChildTabViewAt(2)
								.findViewById(android.R.id.icon))
								.setImageDrawable(getResources().getDrawable(
										R.drawable.ic_usercheck));
					}
				}

			}

		}
	};

}
