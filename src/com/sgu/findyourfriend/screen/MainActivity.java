package com.sgu.findyourfriend.screen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.CustomAdapterFriendStatus;
import com.sgu.findyourfriend.ctr.ControlOptions;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MessageManager;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.mgr.SoundManager;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.utils.Utility;

public class MainActivity extends FragmentActivity {

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
	TextView TitleTextView;

	private View mRootView;

	private ProgressBar pbLoader;

	private int backButtonCount;

	private AlertDialog alertDialog = null;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
				// FriendManager.getInstance().context = mTabHost.getContext();
				pbLoader.setVisibility(View.GONE);
				setContentView(mRootView);
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

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void initView() {

		mTabHost = (FragmentTabHost) mRootView
				.findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

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

		// currentTab = mTabHost.getCurrentTab();

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
		android.app.ActionBar mActionBar = mMain.getActionBar();

		mActionBar.setDisplayShowHomeEnabled(true);
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
				FriendManager.getInstance().memberFriends);
		mDrawerList.setAdapter(adapter);

		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int pos, long arg3) {

				String[] items = { "Xem trên bản đồ", "Gọi", "Nhắn tin", "Khác" };
				AlertDialog.Builder builder = new AlertDialog.Builder(mMain);
				builder.setTitle("Chọn chức năng:");
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							ControlOptions.getInstance().edit();
							ControlOptions.getInstance().putHashMap("friendId",
									adapter.getItem(pos).getUserInfo().getId());

							mTabHost.setCurrentTabByTag(CATEGORIES_TAG);
							mTabHost.setCurrentTabByTag(MAP_TAG);
							break;
						case 1:
							Intent callIntent = new Intent(Intent.ACTION_CALL);
							callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							callIntent.setData(Uri.parse("tel:"
									+ adapter.getItem(pos).getUserInfo()
											.getPhoneNumber()));
							mTabHost.getContext().startActivity(callIntent);
							break;

						case 2:
							ControlOptions.getInstance().edit();
							ControlOptions.getInstance().putHashMap("friendId",
									adapter.getItem(pos).getUserInfo().getId());

							mTabHost.setCurrentTabByTag(CATEGORIES_TAG);
							mTabHost.setCurrentTabByTag(MESSAGE_TAG);
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

		mMain.getActionBar().setCustomView(mCustomView);

		mActionBar.setDisplayShowCustomEnabled(true);

		// set listener for item control
		mCustomView.findViewById(R.id.imgFriendList).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
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

		mCustomView.findViewById(R.id.imgContacts).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						showOptionsContacts();
					}
				});
	}

	public void notifyDataChange() {
		adapter.notifyDataSetChanged();
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

	public void newMessageNotify(boolean isNew) {
		if (isNew) {
			((ImageView) mTabHost.getTabWidget().getChildTabViewAt(1)
					.findViewById(android.R.id.icon))
					.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_grp_trigger));
		} else {
			((ImageView) mTabHost.getTabWidget().getChildTabViewAt(1)
					.findViewById(android.R.id.icon))
					.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_grp));
		}
	}

	public void newRequestNotify(boolean isNew) {
		if (isNew) {
			((ImageView) mTabHost.getTabWidget().getChildTabViewAt(2)
					.findViewById(android.R.id.icon))
					.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_usercheck_trigger));
		} else {
			((ImageView) mTabHost.getTabWidget().getChildTabViewAt(2)
					.findViewById(android.R.id.icon))
					.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_usercheck));
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MessageManager.getInstance().destroy();
	}

	public void showOptionsContacts() {
		LayoutInflater inflater = getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.options_invite_layout,
				null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.progess_dialog_custom);

		builder.setView(dialoglayout);

		((Button) dialoglayout.findViewById(R.id.btnInvite))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						InviteFromContactsDialog contactsdialog = new InviteFromContactsDialog(
								mMain);
						contactsdialog.show();
						alertDialog.dismiss();
					}
				});

		((Button) dialoglayout.findViewById(R.id.btnSearch))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						SearchFromServerDialog searchdialog = new SearchFromServerDialog(
								mMain);
						searchdialog.show();
						alertDialog.dismiss();
					}
				});

		alertDialog = builder.create();
		alertDialog.show();
	}

	class StatusUpdate extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			PostData.login(getApplicationContext(),
					MyProfileManager.getInstance().numberLogins.get(0),
					MyProfileManager.getInstance().password);
			return null;
		}

	}

}
