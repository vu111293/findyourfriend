package com.sgu.findyourfriend.screen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class MainActivity extends FragmentActivity {

	public static String TAG = "MAIN ACTIVITY";
	public static final String MAP_TAG = "map_fragment";
	public static final String MESSAGE_TAG = "message_fragment";
	public static final String REQUEST_TAG = "request_fragment";
	public static final String CATEGORIES_TAG = "categories_fragment";

	private FragmentTabHost mTabHost;
	private MainActivity mMain = this;

	private int currentTab;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	TextView TitleTextView;

	private View mRootView;

	// private int counter; // delay time = counter * 0.25s
	private ProgressBar pbLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadingscreen);

		LayoutInflater mInflater = LayoutInflater.from(this);
		mRootView = mInflater.inflate(R.layout.activity_main, null);

		// get loader
		pbLoader = (ProgressBar) findViewById(R.id.pbLoader);
		pbLoader.setVisibility(View.VISIBLE);

		(new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... arg0) {

				// inti profile
				MyProfileManager.getInstance().init(getApplicationContext());

				// init friend manager
				FriendManager.getInstance().init(getApplicationContext());

				// init message manager
				MessageManager.getInstance().init(getApplicationContext());

				// init sound manager
				SoundManager.getInstance().init(getApplicationContext());

				// init setting
				SettingManager.getInstance().init(getApplicationContext());

				// PostData.historyCreate(getApplicationContext(), 5, new
				// LatLng(18.073887272186989, 120.1006023606658));

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				// Log.d("MINE", MyProfileManager.getInstance().mine.getName());
				// Log.d("MINE",
				// MyProfileManager.getInstance().mine.getGcmId());
				// Log.d("MINE", MyProfileManager.getInstance().mine.getId() +
				// "");
				// Log.d("MINE",
				// MyProfileManager.getInstance().mine.getLastestlogin().toGMTString());
				// Log.d("MINE",
				// MyProfileManager.getInstance().myLocation.toString());

				FriendManager.getInstance().setup();
				initView();

				// refs to context
				// FriendManager.getInstance().context = mTabHost.getContext();
				pbLoader.setVisibility(View.GONE);
				setContentView(mRootView);
			}

		}).execute();

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
				mTabHost.newTabSpec(MAP_TAG).setIndicator(
						"",
						getResources().getDrawable(
								R.drawable.ic_location_silver)),
				MapContainerFragment.class, null);

		mTabHost.addTab(
				mTabHost.newTabSpec(MESSAGE_TAG).setIndicator("",
						getResources().getDrawable(R.drawable.ic_grp_silver)),
				MessageContainerFragment.class, null);

		mTabHost.addTab(
				mTabHost.newTabSpec(REQUEST_TAG).setIndicator(
						"",
						getResources()
								.getDrawable(R.drawable.ic_request_silver)),
				FriendRequestsContainerFragment.class, null);

		mTabHost.addTab(
				mTabHost.newTabSpec(CATEGORIES_TAG).setIndicator(
						"",
						getResources().getDrawable(
								R.drawable.ic_action_settings)),
				CategoriesContainerFragment.class, null);

		currentTab = mTabHost.getCurrentTab();

		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {

				// View currentView = mTabHost.getCurrentView();
				// if (mTabHost.getCurrentTab() > currentTab) {
				// currentView.setAnimation(inFromRightAnimation());
				// } else {
				// currentView.setAnimation(outToRightAnimation());
				// }

				currentTab = mTabHost.getCurrentTab();

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
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);

		mDrawerLayout = (DrawerLayout) mRootView
				.findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) mRootView.findViewById(R.id.drawer_list_right);

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		CustomAdapterFriendStatus adapter = new CustomAdapterFriendStatus(this,
				R.layout.custom_friend_status,
				FriendManager.getInstance().friends);
		mDrawerList.setAdapter(adapter);

		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int pos, long arg3) {
				String[] items = { "Xem trên bản đồ", "Gọi", "Nhắn tin", "Khác" };
				AlertDialog.Builder builder = new AlertDialog.Builder(mTabHost
						.getContext());
				builder.setTitle("Your choices are:");
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							ControlOptions.getInstance().edit();
							ControlOptions.getInstance().putHashMap("friendId",
									pos + "");

							mTabHost.setCurrentTabByTag(CATEGORIES_TAG);
							mTabHost.setCurrentTabByTag(MAP_TAG);
							break;
						case 1:
							Intent callIntent = new Intent(Intent.ACTION_CALL);
							callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							callIntent.setData(Uri.parse("tel:"
									+ FriendManager.getInstance().friends
											.get(pos).getUserInfo()
											.getPhoneNumber()));
							mTabHost.getContext().startActivity(callIntent);
							break;

						case 2:
							ControlOptions.getInstance().edit();
							ControlOptions.getInstance().putHashMap("friendId",
									pos + "");

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
						ImagenceDialog dialog = new ImagenceDialog(mTabHost
								.getContext());
						dialog.show();
					}
				});
	}

	// public Animation inFromRightAnimation() {
	// Animation inFromRight = new TranslateAnimation(
	// Animation.RELATIVE_TO_PARENT, +1.0f,
	// Animation.RELATIVE_TO_PARENT, 0.0f,
	// Animation.RELATIVE_TO_PARENT, 0.0f,
	// Animation.RELATIVE_TO_PARENT, 0.0f);
	// inFromRight.setDuration(240);
	// inFromRight.setInterpolator(new AccelerateInterpolator());
	// return inFromRight;
	// }
	//
	// public Animation outToRightAnimation() {
	// Animation outtoLeft = new TranslateAnimation(
	// Animation.RELATIVE_TO_PARENT, -1.0f,
	// Animation.RELATIVE_TO_PARENT, 0.0f,
	// Animation.RELATIVE_TO_PARENT, 0.0f,
	// Animation.RELATIVE_TO_PARENT, 0.0f);
	// outtoLeft.setDuration(240);
	// outtoLeft.setInterpolator(new AccelerateInterpolator());
	// return outtoLeft;
	// }

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
			finish();
		}

	}

	public void gotoTabByTagName(String tabTag) {
		mDrawerLayout.closeDrawer(mDrawerList);
		mTabHost.setCurrentTabByTag(tabTag);
	}

}
