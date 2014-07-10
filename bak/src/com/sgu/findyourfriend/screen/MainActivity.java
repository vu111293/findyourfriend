package com.sgu.findyourfriend.screen;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.gcm.GCMRegistrar;
import com.sgu.findyourfriend.FriendManager;
import com.sgu.findyourfriend.MessageManager;
import com.sgu.findyourfriend.ProfileInfo;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.CustomAdapter_FriendStatus;

public class MainActivity extends FragmentActivity {

	public static String TAG = "MAIN ACTIVITY";
	private static final String MAP_TAG = "map_fragment";
	private static final String MESSAGE_TAG = "message_fragment";
	private static final String REQUEST_TAG = "request_fragment";
	private static final String CATEGORIES_TAG = "categories_fragment";

	private FragmentTabHost mTabHost;
	private MainActivity mMain = this;

	private int currentTab;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	TextView TitleTextView;

	private View mRootView;

	private int counter; // delay time = counter * 0.25s
	private TextView tv_progress;
	private ProgressBar pb_progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadingscreen);

		LayoutInflater mInflater = LayoutInflater.from(this);
		mRootView = mInflater.inflate(R.layout.activity_main, null);

		ProfileInfo.instance = new ProfileInfo(getApplicationContext());
		ProfileInfo.gcmMyId = GCMRegistrar
				.getRegistrationId(getApplicationContext());

		// init friend manager
		FriendManager.instance = new FriendManager(getApplicationContext());

		// init message manager
		MessageManager.instance = new MessageManager(getApplicationContext());

		initView();

		tv_progress = (TextView) findViewById(R.id.tv_progress);
		pb_progressBar = (ProgressBar) findViewById(R.id.pb_progressbar);
		// Sets the maximum value of the progress bar to 100
		pb_progressBar.setMax(100);

		counter = 3;
		final int maxCounter = counter;

		// 100 / 10 * (10 - i)

		final Handler handler = new Handler();
		final Runnable r = new Runnable() {
			public void run() {
				if (counter > 0) {

					int per = (int) (100 / maxCounter) * (maxCounter - counter);
					tv_progress.setText("Progress: " + per + "%");
					pb_progressBar.setProgress(per);
					handler.postDelayed(this, 500);
					counter--;
				} else
					setContentView(mRootView);

			}
		};

		handler.postDelayed(r, 0);

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
				mTabHost.newTabSpec(MESSAGE_TAG)
						.setIndicator(
								"",
								getResources().getDrawable(
										R.drawable.ic_action_camera)),
				MessageContainerFragment.class, null);

		mTabHost.addTab(
				mTabHost.newTabSpec(MAP_TAG).setIndicator("",
						getResources().getDrawable(R.drawable.ic_action_about)),
				MapContainerFragment.class, null);

		mTabHost.addTab(
				mTabHost.newTabSpec(REQUEST_TAG).setIndicator("",
						getResources().getDrawable(R.drawable.ic_action_email)),
				FriendRequestsFragment.class, null);

		mTabHost.addTab(
				mTabHost.newTabSpec(CATEGORIES_TAG).setIndicator(
						"",
						getResources().getDrawable(
								R.drawable.ic_action_settings)),
				CategoriesFragment.class, null);

		currentTab = mTabHost.getCurrentTab();

		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {

				View currentView = mTabHost.getCurrentView();
				if (mTabHost.getCurrentTab() > currentTab) {
					currentView.setAnimation(inFromRightAnimation());
				} else {
					currentView.setAnimation(outToRightAnimation());
				}

				currentTab = mTabHost.getCurrentTab();
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
		CustomAdapter_FriendStatus adapter = new CustomAdapter_FriendStatus(
				this, R.layout.custom_friend_status,
				FriendManager.instance.friends);
		mDrawerList.setAdapter(adapter);

		mMain.getActionBar().setCustomView(mCustomView);

		mActionBar.setDisplayShowCustomEnabled(true);
		
		// set listener fot item control
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
						ImagenceDialog dialog = new ImagenceDialog(
								mTabHost.getContext());
						dialog.show();
					}
				});
	}

	public Animation inFromRightAnimation() {
		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(240);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	public Animation outToRightAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(240);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
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
			finish();
		}

	}

}
