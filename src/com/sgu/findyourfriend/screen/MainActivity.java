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
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
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
import com.google.android.gms.internal.hp;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.CustomAdapterFriendStatus;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MessageManager;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SQLiteDatabaseManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.mgr.SoundManager;
import com.sgu.findyourfriend.mgr.Config.AppState;
import com.sgu.findyourfriend.model.TempMessage;
import com.sgu.findyourfriend.model.User;
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

	private View mRootView;

	private ProgressBar pbLoader;

	private int backButtonCount;

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
				// init friend manager
				FriendManager.getInstance().init(getApplicationContext());

				// must be after sqlite manager
				MessageManager.getInstance().init(getApplicationContext());

				// init sound manager
				SoundManager.getInstance().init(getApplicationContext());

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// if (Config.currentState == AppState.ONLINE) {
				FriendManager.getInstance().setup();
				// } else {
				// FriendManager.getInstance().setupOffline();
				// }

				initView();

				// refs to context
				pbLoader.setVisibility(View.GONE);
				setContentView(mRootView);

				isRegister = true;

				// precheck new notify
				preCheckNewNotify();

				if (Config.currentState == AppState.OFFLINE) {
					Intent i = new Intent(getApplicationContext(),
							ServiceUpdatePosition.class);
					i.putExtra("isStop", true);
					startService(i);
				} else
					checkStartUpdateService();
			}

		}).execute();

		// setup update online status
		if (Config.currentState == AppState.ONLINE) {
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

		// if (GooglePlayServicesUtil
		// .isGooglePlayServicesAvailable(getApplicationContext()) ==
		// ConnectionResult.SUCCESS)
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
				mTabHost.newTabSpec(CATEGORIES_TAG).setIndicator("",
						getResources().getDrawable(R.drawable.ic_setting)),
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

				final User utemp = adapter.getItem(pos).getUserInfo();
				ArrayList<String> data = new ArrayList<String>();
				data.add("Gọi");
				data.add("Nhắn tin");

				if (null != adapter.getItem(pos).getLastLocation())
					data.add("Xem bản đồ");

				final Dialog dialog = new Dialog(mMain);
				Utility.showListDialog(Utility.CONFIRM, dialog, "Tùy chọn",
						data, new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int position, long arg3) {

								switch (position) {
								case 0: // call task
									callTask(utemp.getId());
									break;
								case 1: // message reply
									mTabHost.setCurrentTabByTag(MESSAGE_TAG);

									Handler handler = new Handler();

									final Runnable r = new Runnable() {
										public void run() {

											Intent intent2 = new Intent(
													Config.MAIN_ACTION);
											intent2.putExtra(
													Config.EDIT_MESSAGE_ACTION,
													utemp.getId());
											getApplicationContext()
													.sendBroadcast(intent2);
										}
									};

									handler.postDelayed(r, 10);
									break;
								case 2:
									// view position on map
									mTabHost.setCurrentTabByTag(MAP_TAG);
									Intent intent = new Intent(
											Config.MAIN_ACTION);
									intent.putExtra(
											Config.ZOOM_POSITION_ACTION,
											utemp.getId());
									getApplicationContext().sendBroadcast(
											intent);
									break;
								}

								dialog.dismiss();
								mDrawerLayout.closeDrawer(mDrawerList);
							}
						});

				dialog.show();

			}
		});

		getSupportActionBar().setCustomView(mCustomView);

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

						if (SettingManager.getInstance().getDefaultWarning()
								.size() > 0) {

							Intent i = new Intent(getApplicationContext(),
									EmergenceActivity.class);
							i.putExtra("latlng", MyProfileManager.getInstance()
									.getMyPosition().latitude
									+ " x "
									+ MyProfileManager.getInstance()
											.getMyPosition().longitude);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							getApplicationContext().startActivity(i);
						} else {
							
							Utility.showDialog(Utility.WARNING, new Dialog(mMain),
									"Tính năng cầu cứu không khả dụng",
									"Hiện bạn chưa có bạn bè trợ giúp. Nếu bạn đã kết bạn thì vào phần cài đặt thông báo để thêm bạn bè trợ giúp. "
									+ "Nếu bạn chưa có bạn bè thì vào mục bản đồ để mời bạn mới.");
						}
					}
				});
	}

	public void notifyDataChange() {
		adapter.notifyDataSetChanged();
	}

	public void callTask(int friendId) {

		final ArrayList<String> phs = FriendManager.getInstance().hmMemberFriends
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

		// more one phone number
		final Dialog dialog = new Dialog(mMain);
		Utility.showListDialog(Utility.CONFIRM, dialog, "Chọn số cần gọi", phs,
				new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						Intent callIntent = new Intent(Intent.ACTION_CALL);
						callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						callIntent.setData(Uri.parse("tel:" + phs.get(position)));
						mTabHost.getContext().startActivity(callIntent);

						dialog.dismiss();
					}
				});
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
						"Nhấn lần nữa để thoát.");
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
