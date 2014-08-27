package com.sgu.findyourfriend.screen;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devsmart.android.ui.HorizontalListView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.FriendSwipeAdapter;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MyLocationChangeListener;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.SimpleUserAndLocation;
import com.sgu.findyourfriend.net.PostData;
import com.sgu.findyourfriend.screen.MapWrapperLayout.OnDragListener;
import com.sgu.findyourfriend.utils.GpsPosition;
import com.sgu.findyourfriend.utils.Utility;

public class MapFragment extends BaseContainerFragment implements
		MyLocationChangeListener {

	// contants define
	private static final int CALL = 0;
	private static final int MESSAGE = 1;
	private static final int HISTORIES = 2;
	private static final int UPDATE = 3;
	private static final int ROUTE = 4;
	private static final int REQUEST = 5;
	private static final int ACCEPT = 6;
	private static final int UNFRIEND = 7;
	private static final int REMOVE_HISTORIES = 8;
	private static final int STOP_SHARE = 9;
	private static final int NOT_POSITION = 10;

	private static final int TASK_NUM = 11;

	// point to itself
	private MapFragment mThis = this;

	private static float startDegree = 0f;
	private static float radius = 130f;

	private static boolean isRegister;

	private Context context;

	// view replace
	private static View view;
	private FrameLayout inc; // include layout for horizontal friend list

	// map for app
	private GoogleMap mMap;

	private HashMap<Integer, Marker> markerManager;

	// marker Stranger and invited
	private HashMap<Integer, Marker> markerSIn;

	private HashMap<Integer, View> hmViewOnHoz;

	private FragmentActivity myContext;

	// view in map fragment
	private HorizontalListView avatarListView;
	private FriendSwipeAdapter swipeAdapter;

	// friend's id current
	private int fIDCurrent = -1;

	// GPS Controller
	private GpsPosition gpsPosition;

	private View viewSelected = null;

	// // control button
	private ImageButton[] imgButtons = new ImageButton[TASK_NUM];
	private TextView textViewAddress;

	private ImageButton btnShow;
	private ImageButton btnZoomIn;
	private ImageButton btnZoomOut;

	private float zoomLevel; // min 2.0 max 21.0

	private boolean toggleBtnValue = true;

	private ProgressBar pbOnMap;

	private boolean isMapStarted = false;

	private boolean isSetupTasker = false;

	// state marker hide/show
	private boolean isShowMarker;

	// masks check control
	private int[] maskEn = new int[TASK_NUM];

	private MapController mapController;

	// management
	private Resources resource;

	// vibrator
	private Vibrator vibrator;

	private AlphaAnimation alphaIn;
	private AlphaAnimation alphaOut;

	private boolean isControllerShowed = true;

	// update time
	private long intervalUpdateFriend;

	public static final String TAG = "MAP FRAGMENT";

	public MapFragment() {
		// init
		isMapStarted = false;
		isShowMarker = true;
		isRegister = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}

		try {
			view = inflater.inflate(R.layout.fragment_map, container, false);
		} catch (InflateException e) {
			Log.i(TAG, e.getMessage());

		}

		afterViewCreate(view);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Log.i(TAG, "onActivityCreated");
	}

	@Override
	public void onResume() {
		super.onResume();
		// update setup map
		getmMap().setMapType(SettingManager.getInstance().getMapType());

		// check recording
		gpsPosition.setUploadMyPosition(SettingManager.getInstance()
				.isUploadMyPosition());

		// set time and accuracy update
		gpsPosition.setCheckInterval(SettingManager.getInstance()
				.getIntervalUpdatePosition());
		gpsPosition.setMinDistance(SettingManager.getInstance()
				.getAccuracyUpdatePosition());

		// set interval to update friend
		intervalUpdateFriend = SettingManager.getInstance()
				.getIntervalUpdateFriend();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity.getApplicationContext();
		myContext = (FragmentActivity) activity;

		if (!isRegister) {
			activity.registerReceiver(mHandleMessageReceiver, new IntentFilter(
					com.sgu.findyourfriend.mgr.Config.UPDATE_UI));
			activity.registerReceiver(mHandleMessageReceiver, new IntentFilter(
					com.sgu.findyourfriend.mgr.Config.MAIN_ACTION));
			isRegister = true;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (isRegister)
			getActivity().unregisterReceiver(mHandleMessageReceiver);
	}

	// ----------------- init variable and setup methods ----------- //
	@SuppressLint("UseSparseArrays")
	private void afterViewCreate(View view) {

		// setup alpha animation
		alphaIn = new AlphaAnimation(0F, 1F);
		alphaIn.setDuration(500);
		alphaIn.setFillAfter(true);

		alphaOut = new AlphaAnimation(1F, 0F);
		alphaOut.setDuration(500);
		alphaOut.setFillAfter(true);

		// get button show
		btnShow = (ImageButton) view.findViewById(R.id.btnShow);
		btnZoomIn = (ImageButton) view.findViewById(R.id.btnZoomIn);
		btnZoomOut = (ImageButton) view.findViewById(R.id.btnZoomOut);

		// get progress bar
		setPbOnMap((ProgressBar) view.findViewById(R.id.pbOnMap));
		getPbOnMap().setVisibility(View.GONE);

		// get Gps position
		gpsPosition = new GpsPosition(context, this);

		// get resources
		resource = getActivity().getResources();

		// Check if Internet present
		if (!Utility.isConnectingToInternet(getActivity())) {

			// Internet Connection is not present
			Utility.showAlertDialog(context, "Internet Connection Error",
					"Please connect to Internet connection", false);
			// stop executing code by return
			return;
		}

		// initialize
		markerManager = new HashMap<Integer, Marker>();
		markerSIn = new HashMap<Integer, Marker>();

		hmViewOnHoz = new HashMap<Integer, View>();

		// setup control layout and avatar list view
		setupHalfCirle();

		// setup friend list
		setupFriendList();

		// setup map
		setupMap();

		// init + setup mapController
		mapController = new MapController(mThis);

		setupControlView();

		// setup send location to server
		gpsPosition.startRecording();

		// load friends position after 1 mins
		loadFriendsPosition();
		updateFriendsInfo();

		// load strangers
		// loadStrangers(2000000);

		// load invited member
		// loadInvitedMember();

		// vibrator setup
		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);

		// hide control console
		hideControl();

		// scrollForFriendList();
	}

	private void loadInvitedMember() {
		// load on map
		for (int id : FriendManager.getInstance().hmInvited.keySet()) {
			createInvitedMakerOnMap(id,
					FriendManager.getInstance().hmInvited.get(id));
		}

	}

	private void loadStrangers(final double radius) {

		getmMap().addCircle(
				(new CircleOptions())
						.center(MyProfileManager.getInstance().getMyPosition())
						.radius(radius).strokeWidth(2).strokeColor(0x55ff0000));

		(new AsyncTask<Void, Void, ArrayList<SimpleUserAndLocation>>() {

			@Override
			protected ArrayList<SimpleUserAndLocation> doInBackground(
					Void... params) {

				if (FriendManager.getInstance().hmStrangers.size() == 0) {
					// try get
					return PostData
							.findInDistance(context, MyProfileManager
									.getInstance().getMyPosition().latitude,
									MyProfileManager.getInstance()
											.getMyPosition().longitude, radius);

				}
				return null;
			}

			@Override
			protected void onPostExecute(ArrayList<SimpleUserAndLocation> result) {
				if (null != result) {
					FriendManager.getInstance().hmStrangers.clear();
					for (SimpleUserAndLocation sp : result) {
						FriendManager.getInstance().hmStrangers.put(sp.getId(),
								new LatLng(sp.getLat(), sp.getLng()));

					}

				}

				// load on map
				for (int id : FriendManager.getInstance().hmStrangers.keySet()) {
					createStrangerMakerOnMap(id,
							FriendManager.getInstance().hmStrangers.get(id));
				}

			}

		}).execute();

	}

	public void tickVibrator() {
		vibrator.vibrate(100);
	}

	// ---------------- setup map ----------------------- //
	private void setupMap() {
		FragmentManager myFM = myContext.getSupportFragmentManager();
		CustomMapFragment mySpFrg = (CustomMapFragment) myFM
				.findFragmentById(R.id.mapFragment);

		setmMap(mySpFrg.getMap());
		getmMap().setMyLocationEnabled(false);

		// load setup map from setting activity
		// mMap.setMapType(SettingManager.getInstance().getMapType());

		getmMap().getUiSettings().setZoomControlsEnabled(false);

		zoomLevel = 10.0f;
		getmMap().animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));

		// hide control my center
		// mMap.getUiSettings().setMyLocationButtonEnabled(false);

		// setup info window event
		getmMap().setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(final Marker marker) {

				for (int k : markerSIn.keySet()) {
					// if ok return;

				}

				// int friendId = 0;
				for (int k : markerManager.keySet()) {
					if (markerManager.get(k).equals(marker)) {
						fIDCurrent = k;
						break;
					}
				}

				// warning
				if (fIDCurrent == -1) {
					Log.i(TAG, "id not found!");
					return;
				}

				marker.hideInfoWindow();
				final Handler mUpdateHandler = new Handler();
				Runnable mUpdateRunnable = new Runnable() {
					public void run() {
						// hide maker before display new screen
						hideMarker();

						PositionDetailFragment fragment = new PositionDetailFragment(
								mThis);

						Bundle bundle = new Bundle();
						bundle.putInt("friendId", fIDCurrent);
						bundle.putString("address",
								mapController.getAddress(marker.getPosition()));
						bundle.putDouble("myLat", MyProfileManager
								.getInstance().getMyPosition().latitude);
						bundle.putDouble("myLng", MyProfileManager
								.getInstance().getMyPosition().longitude);

						fragment.setArguments(bundle);

						hideControl();
						((BaseContainerFragment) getParentFragment())
								.replaceFragment(fragment, true);

					}
				};

				mUpdateHandler.postDelayed(mUpdateRunnable, 50);

			}
		});

		getmMap().setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(final Marker marker) {
				Log.i(TAG, "marker on click!");

				// int disBefore, disAfter;

				// check marker manager
				for (int k : markerManager.keySet()) {
					if (marker.equals(markerManager.get(k))) {
						// disBefore =
						// swipeAdapter.getDistaceToItem(fIDCurrent);
						
						if (fIDCurrent != k) {
							changleSelectMarker(fIDCurrent, false);
							fIDCurrent = k;
							

							// setup control
//							swipeAdapter.hightLightItem(swipeAdapter
//									.getPositionByFriendID(k));
//							changeVisibilityView();
//							showControl();

							// disAfter =
							// swipeAdapter.getDistaceToItem(fIDCurrent);

							scrollForFriendList(fIDCurrent);

							
//							textViewAddress.setText(mapController.getAddress(FriendManager
//									.getInstance().hmMemberFriends.get(k)
//									.getLastLocation()));
//							
//							marker.setTitle(mapController.getAddress(FriendManager
//									.getInstance().hmMemberFriends.get(k)
//									.getLastLocation()));
						}

						changleSelectMarker(fIDCurrent, true);
						
						// setup control
						swipeAdapter.hightLightItem(swipeAdapter
								.getPositionByFriendID(k));
						changeVisibilityView();
						showControl();
						
						break;
					}
				}

				// check marker sin
				for (int k : markerSIn.keySet()) {
					if (marker.equals(markerSIn.get(k))) {
						if (FriendManager.getInstance().hmStrangers
								.containsKey(k)) {
							marker.setTitle("Kết bạn");

						} else if (FriendManager.getInstance().hmInvited
								.containsKey(k)) {
							marker.setTitle(mapController
									.getAddress(FriendManager.getInstance().hmInvited
											.get(k).getLastLocation()));
						}
						break;
					}
				}
				return false;
			}
		});

		// setup onclick on map
		getmMap().setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				Log.i("POS", point.latitude + " # " + point.longitude);
				// swipeAdapter.unHightLightItem();
				swipeAdapter.hightLightItem(-1);
				hideControl();
				changleSelectMarker(fIDCurrent, false);
			}
		});

		
		mySpFrg.setOnDragListener(new OnDragListener() {
			
			@Override
			public void onDrag(MotionEvent motionEvent) {
				// Log.d("ON_DRAG", "OK");
				if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) 
					hideControl();
			}
		});
		
	}

	// ---------------- setup friend list ------------------ //
	private void setupFriendList() {
		avatarListView = (HorizontalListView) view
				.findViewById(R.id.avatarHListView);

		swipeAdapter = new FriendSwipeAdapter(context,
				R.layout.item_friend_accepted, new ArrayList<Friend>(
						FriendManager.getInstance().hmMemberFriends.values()));
		avatarListView.setAdapter(swipeAdapter);

		avatarListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.i(TAG, "click at " + position);

				//  unmarke 
				if (fIDCurrent >= 0) {
					changleSelectMarker(fIDCurrent, false);
					// swipeAdapter.unHightLightItem();
				}

				// change id selected
				fIDCurrent = swipeAdapter.getItem(position).getUserInfo()
						.getId();

				
				if (fIDCurrent == -1) {
					// add friend
					hideControl();
					showOptionsContacts();
					return;
				}
				
				showMarker();
				
				// mark
				changleSelectMarker(fIDCurrent, true);

				// dis/en visible on control view
				changeVisibilityView();

				swipeAdapter.hightLightItem(position);
				showControl();

				if (swipeAdapter.getItem(position).getAcceptState() == Friend.SHARE_RELATIONSHIP
						&& null != swipeAdapter.getItem(position)
								.getLastLocation())
					mapController.zoomToPosition(swipeAdapter.getItem(position)
							.getLastLocation());
				else {
					// Utility.showMessageOnCenter(context,
					// "Vị trí hiện tại không có sẵn");
				}
			}
		});
	}

	// ----------------- setup control at bottom screen ----------- //
	private void setupControlView() {

		btnShow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				toggleBtnValue = !toggleBtnValue;

				if (toggleBtnValue) {
					showMarker();
					btnShow.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_show));
				} else {
					hideMarker();
					btnShow.setImageDrawable(getResources().getDrawable(
							R.drawable.ic_hide));
				}
			}

		});

		btnShow.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				mapController.clearnMarkerHistory();
				return true;
			}
		});

		btnZoomIn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hideControl();
				zoomLevel += 2.0f;
				if (zoomLevel > 21.0f)
					zoomLevel = 21.0f;
				mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));

			}
		});

		btnZoomOut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				hideControl();
				zoomLevel -= 2.0f;
				if (zoomLevel < 2.0f)
					zoomLevel = 2.0f;
				mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));

			}
		});
	}

	private void setupHalfCirle() {
		inc = (FrameLayout) view.findViewById(R.id.task_inc);

		textViewAddress = (TextView) view.findViewById(R.id.txtToastAddress);
		textViewAddress.setVisibility(View.GONE);
		
		imgButtons = new ImageButton[inc.getChildCount() - 1];
		for (int i = 0; i < TASK_NUM; ++i) {
			imgButtons[i] = (ImageButton) inc.getChildAt(i);
			imgButtons[i].setVisibility(View.GONE);
		}

	}

	private void scrollForFriendList(final int toId) {
		avatarListView.postDelayed(new Runnable() {

			@Override
			public void run() {

				// View v1 = swipeAdapter.getViewByFriendId(formId);
//				View v2 = swipeAdapter.getViewByFriendId(toId);
//
//				avatarListView.scrollTo(0);
//
//				int disBefore = (v2.getLeft() + v2.getRight()) / 2
//						- (avatarListView.getWidth() / 2);
//				int disAfter = disBefore;
//				if (disBefore < 0
//						&& swipeAdapter.getDistaceToItem(toId) < avatarListView
//								.getWidth())
//					disAfter += avatarListView.getWidth();
//				else if (disBefore > 0
//						&& swipeAdapter.getDistaceToItem(toId) > (swipeAdapter
//								.getWidth() - avatarListView.getWidth()))
//					disAfter -= avatarListView.getWidth();
//
//				avatarListView.scrollBy(
//						disBefore * disAfter > 0 ? disAfter : 0, 0);
//
//				Log.i(TAG, "disbefore " + disBefore + "#disaftert " + disAfter);
			}
		}, 100);
	}

	// ---------- hide/show marker on map ----------------- //
	private void hideMarker() {
		Log.i(TAG, "----------------------------------------hide marker");
		for (int k : markerManager.keySet()) {
			markerManager.get(k).setVisible(false);
		}
		isShowMarker = false;
	}

	private void showMarker() {
		if (isShowMarker)
			return;
		for (int k : markerManager.keySet()) {
			markerManager.get(k).setVisible(true);
		}
		isShowMarker = true;
	}

	private void hideMarker(Friend f1, Friend f2) {
		for (int k : markerManager.keySet()) {
			markerManager.get(k).setVisible(false);
		}
		markerManager.get(f1.getUserInfo().getId()).setVisible(true);
		markerManager.get(f2.getUserInfo().getId()).setVisible(true);
		isShowMarker = false;
	}

	// ------------------ hide/show view on control console ---------- //

	private void changeVisibilityView() {

		if (fIDCurrent <= 0)
			return;

		View rView = getView();

		if (null == rView)
			return;

		
		boolean isToastPosition = false; // is show address 
		
		
		// accept = 6, unfriend = 7, remove = 8, stop share = 9, task nopos = 10
		for (int i = 0; i < TASK_NUM; ++i)
			maskEn[i] = 1;

		if (fIDCurrent == MyProfileManager.getInstance().getMyID()) {
			maskEn[CALL] = 0;
			maskEn[MESSAGE] = 0;
			maskEn[ROUTE] = 0;
			maskEn[REQUEST] = 0;
			maskEn[ACCEPT] = 0;
			maskEn[UNFRIEND] = 0;
			maskEn[NOT_POSITION] = 0;
			isToastPosition = true;
		} else {

			Friend f = FriendManager.getInstance().hmMemberFriends
					.get(fIDCurrent);
			if (f.getAcceptState() == Friend.SHARE_RELATIONSHIP) {
				maskEn[REQUEST] = 0;
				maskEn[ACCEPT] = 0;
				maskEn[REMOVE_HISTORIES] = 0;

				if (null != f.getLastLocation()) {
					maskEn[NOT_POSITION] = 0;
					isToastPosition = true;
				}
				
			} else if (f.getAcceptState() == Friend.REQUEST_SHARE) {
				// wait accept
				maskEn[HISTORIES] = 0;
				maskEn[ROUTE] = 0;
				maskEn[REQUEST] = 0;
				maskEn[ACCEPT] = 0;
				maskEn[REMOVE_HISTORIES] = 0;
				maskEn[STOP_SHARE] = 0;
			} else if (f.getAcceptState() == Friend.REQUESTED_SHARE) {
				// accept text
				maskEn[HISTORIES] = 0;
				maskEn[ROUTE] = 0;
				maskEn[REQUEST] = 0;
				maskEn[REMOVE_HISTORIES] = 0;
				maskEn[STOP_SHARE] = 0;
			} else {
				// friend
				maskEn[HISTORIES] = 0;
				maskEn[ROUTE] = 0;
				maskEn[ACCEPT] = 0;
				maskEn[REMOVE_HISTORIES] = 0;
				maskEn[STOP_SHARE] = 0;
			}
		}

		int numOfButton = 0; // inc.getChildCount();

		if (maskEn[CALL] == 1) {
			imgButtons[numOfButton].setImageDrawable(getResources()
					.getDrawable(R.drawable.ic_call));
			imgButtons[numOfButton].setOnClickListener(new OnCallClick());
			numOfButton++;
		}

		if (maskEn[MESSAGE] == 1) {
			imgButtons[numOfButton].setImageDrawable(getResources()
					.getDrawable(R.drawable.ic_message));
			imgButtons[numOfButton].setOnClickListener(new OnMessageClick());
			numOfButton++;
		} else {
		}

		if (maskEn[HISTORIES] == 1) {

			imgButtons[numOfButton].setImageDrawable(getResources()
					.getDrawable(R.drawable.ic_history));
			imgButtons[numOfButton].setOnClickListener(new OnHistoryClick());
			numOfButton++;
		}

		if (maskEn[UPDATE] == 1) {
			imgButtons[numOfButton].setImageDrawable(getResources()
					.getDrawable(R.drawable.ic_update));
			imgButtons[numOfButton].setOnClickListener(new OnUpdateClick());
			numOfButton++;
		}

		if (maskEn[ROUTE] == 1) {

			imgButtons[numOfButton].setImageDrawable(getResources()
					.getDrawable(R.drawable.ic_route));
			imgButtons[numOfButton].setOnClickListener(new OnRouteClick());
			numOfButton++;
		}

		if (maskEn[REQUEST] == 1) {

			imgButtons[numOfButton].setImageDrawable(getResources()
					.getDrawable(R.drawable.ic_request));
			imgButtons[numOfButton].setOnClickListener(new OnRequestClick());
			numOfButton++;
		}

		// accept request
		if (maskEn[ACCEPT] == 1) {

			imgButtons[numOfButton].setImageDrawable(getResources()
					.getDrawable(R.drawable.ic_accept));
			imgButtons[numOfButton].setOnClickListener(new OnAcceptClick());
			numOfButton++;
		}

		// unfriend
		if (maskEn[UNFRIEND] == 1) {

			imgButtons[numOfButton].setImageDrawable(getResources()
					.getDrawable(R.drawable.ic_unfriend));
			imgButtons[numOfButton].setOnClickListener(new OnUnFriendClick());
			numOfButton++;
		}

		// remove histories
		if (maskEn[REMOVE_HISTORIES] == 1) {

			imgButtons[numOfButton].setImageDrawable(getResources()
					.getDrawable(R.drawable.ic_remove_histories));
			imgButtons[numOfButton]
					.setOnClickListener(new OnRemoveHistoryClick());
			numOfButton++;
		}

		// stop share
		if (maskEn[STOP_SHARE] == 1) {

			imgButtons[numOfButton].setImageDrawable(getResources()
					.getDrawable(R.drawable.ic_stop_share));
			imgButtons[numOfButton].setOnClickListener(new OnStopShareClick());
			numOfButton++;
		}

		// not position
		if (maskEn[NOT_POSITION] == 1) {

			imgButtons[numOfButton].setImageDrawable(getResources()
					.getDrawable(R.drawable.ic_notposition));
			imgButtons[numOfButton].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Utility.showMessage(context, "Not available");
				}
			});
			// not add
		}

		for (int i = 0; i < inc.getChildCount() - 1; ++i) {
			if (i < numOfButton)
				imgButtons[i].setVisibility(View.VISIBLE);
			else
				imgButtons[i].setVisibility(View.GONE);

		}

		float dimenTaksButton = getActivity().getResources().getDimension(
				R.dimen.size_task_button);

		int cor1[] = new int[2];
		inc.getLocationOnScreen(cor1);
		Log.i(TAG, "top" + cor1[0] + " # " + cor1[1]);

		float xCenter = (inc.getWidth() - dimenTaksButton) / 2f + cor1[0];
		float yCenter = (inc.getHeight() - dimenTaksButton) / 2f - 50f;

		Log.i(TAG, xCenter + " # " + yCenter);

		float alpha = 360f / (numOfButton);

		for (int i = 0; i < numOfButton; ++i) {
			imgButtons[i] = (ImageButton) inc.getChildAt(i);
			imgButtons[i].setX((int) (xCenter + (radius * Math.cos(Math
					.toRadians(alpha * i + startDegree)))));
			imgButtons[i].setY((int) (yCenter - (radius * Math.sin(Math
					.toRadians(alpha * i + startDegree)))));

			Log.i(TAG, imgButtons[i].toString() + " # " + imgButtons[i].getX()
					+ " @ " + imgButtons[i].getY());
		}

		// set position for button not position
		if (maskEn[NOT_POSITION] == 1) {
			imgButtons[numOfButton] = (ImageButton) inc.getChildAt(numOfButton);
			imgButtons[numOfButton].setX(xCenter);
			imgButtons[numOfButton].setY(yCenter);
			imgButtons[numOfButton].setVisibility(View.VISIBLE);
		}
		
		
		// show Toast
		if (isToastPosition) {
			FrameLayout.LayoutParams layoutPrms = (FrameLayout.LayoutParams) textViewAddress.getLayoutParams();
			
			layoutPrms.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
			layoutPrms.bottomMargin = 100;
			textViewAddress.setLayoutParams(layoutPrms);
			textViewAddress.setText("đang tính...");
			textViewAddress.setVisibility(View.VISIBLE);
			
			
			String address = mapController.getAddress(FriendManager
					.getInstance().hmMemberFriends.get(fIDCurrent)
					.getLastLocation());
			
			if (null == address)
				textViewAddress.setText("Vị trí không có sẵn");
			else
				textViewAddress.setText(address);
			
			
			textViewAddress.setOnClickListener(new OnToastClick());
			
		} else {
			textViewAddress.setVisibility(View.GONE);
		}

	}

	// --------------------- changle color marker --------------------- //
	private void changleSelectMarker(int idFriendCur, boolean isSelected) {
		Marker m = markerManager.get(idFriendCur);
		Friend f = FriendManager.getInstance().hmMemberFriends.get(idFriendCur);

		if (m != null) {
			if (isSelected)
				m.setIcon(Utility.combileLocationIcon(getActivity(), f, true));
			else
				m.setIcon(Utility.combileLocationIcon(getActivity(), f, false));
		}
	}

	// -------------- show/hide control console ---------------- //
	private void showControl() {
		inc.setVisibility(View.VISIBLE);
		inc.startAnimation(alphaIn);
		isControllerShowed = true;
	}

	private void hideControl() {
		if (isControllerShowed) {
			inc.startAnimation(alphaOut);
			isControllerShowed = false;
			inc.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					inc.setVisibility(View.GONE);
					
					for (int i = 0; i < TASK_NUM; ++i) {
						imgButtons[i].setX(-100);
						imgButtons[i].setY(-100);
					}
					
				}
			}, 500);
		}

	}

	// -------------------- should load with timer --------------- //
	private void loadFriendsPosition() {
		getmMap().clear();

		// update for friend
		for (Friend f : swipeAdapter.getData()) {
			if (f.getAcceptState() == Friend.SHARE_RELATIONSHIP
					&& null != f.getLastLocation())
				createMakerOnMap(f);
		}
	}

	private void updateFriendPosition(int friendId) {
		if (markerManager.containsKey(friendId)) {
			LatLng latlng = FriendManager.getInstance().hmMemberFriends.get(
					friendId).getLastLocation();

			markerManager.get(friendId).setPosition(latlng);

		}
	}

	private void updateAllPosition() {
		for (int k : FriendManager.getInstance().hmMemberFriends.keySet()) {
			updateFriendPosition(k);
		}
	}

	// -------------------------- update position ------------------------ //
	private void updateFriendsInfo() {
		intervalUpdateFriend = SettingManager.getInstance()
				.getIntervalUpdateFriend();
		final Handler mUpdateHandler = new Handler();
		Runnable mUpdateRunnable = new Runnable() {

			public void run() {

				(new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						FriendManager.getInstance().loadFriend();
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						FriendManager.getInstance().setupAfterLoading();
						// updateUI();

						Intent intentUpdate = new Intent(
								com.sgu.findyourfriend.mgr.Config.UPDATE_UI);
						intentUpdate.putExtra(
								com.sgu.findyourfriend.mgr.Config.UPDATE_TYPE,
								Utility.FRIEND);
						intentUpdate
								.putExtra(
										com.sgu.findyourfriend.mgr.Config.UPDATE_ACTION,
										Utility.RESPONSE_NO);
						context.sendBroadcast(intentUpdate);

						// Toast.makeText(getActivity(), "updateUI!",
						// Toast.LENGTH_SHORT).show();
					}

				}).execute();

				mUpdateHandler.postDelayed(this, intervalUpdateFriend);
			}
		};
		mUpdateHandler.postDelayed(mUpdateRunnable, intervalUpdateFriend);

	}

	private void updatePositionOf(Friend f) {
		Marker m = markerManager.get(f.getUserInfo().getId());
		if (m != null) {
			m.setPosition(f.getLastLocation());
			m.setTitle(mapController.getAddress(f.getLastLocation()));
		}
	}

	// ----------------------- create map's marker ----------------- //

	private void createInvitedMakerOnMap(int id, Friend f) {
		Log.i(TAG, "create on map");
		if (null == f.getLastLocation())
			return;

		MarkerOptions options = new MarkerOptions();
		options.position(f.getLastLocation());

		options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
				.decodeResource(getResources(), R.drawable.ic_invited_32)));

		Marker m = getmMap().addMarker(options);
		markerSIn.put(f.getUserInfo().getId(), m);

	}

	private void createStrangerMakerOnMap(int id, LatLng latlng) {
		Log.i(TAG, "create stranger on map");

		if (markerSIn.containsKey(id))
			return;

		MarkerOptions options = new MarkerOptions();
		options.position(latlng);

		options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
				.decodeResource(getResources(), R.drawable.ic_stranger_32)));

		Marker m = getmMap().addMarker(options);
		markerSIn.put(id, m);
	}

	private void createMakerOnMap(Friend f) {
		Log.i(TAG, "create on map");

		MarkerOptions options = new MarkerOptions();
		options.position(f.getLastLocation());
		options.snippet(Utility.convertMicTimeToString(System
				.currentTimeMillis() - f.getUserInfo().getLastLogin().getTime())
				+ " trước - sai lệch " + f.getAccurency() + "m.");

		options.icon(Utility.combileLocationIcon(getActivity(), f, false));

		Marker m = getmMap().addMarker(options);
		markerManager.put(f.getUserInfo().getId(), m);
	}

	@Override
	public void onMyLocationChanged(Location location) {
		if (null == location)
			return;
		Log.i(TAG,
				"update! " + location.getLatitude() + " # "
						+ location.getLongitude());
		LatLng latlng = new LatLng(location.getLatitude(),
				location.getLongitude());
		MyProfileManager.getInstance().setMyPosition(latlng);
		Friend f = MyProfileManager.getInstance().getMineInstance();

		updatePositionOf(f);

		if (!isMapStarted) {
			mapController.zoomToPosition(location);
			isMapStarted = true;
		}

	}

	public void updatePositionTask(int fID) {
		mapController.updatePositionTask(fID);
	}

	public GoogleMap getmMap() {
		return mMap;
	}

	public void setmMap(GoogleMap mMap) {
		this.mMap = mMap;
	}

	public ProgressBar getPbOnMap() {
		return pbOnMap;
	}

	public void setPbOnMap(ProgressBar pbOnMap) {
		this.pbOnMap = pbOnMap;
	}

	public void showOptionsContacts() {
		final Dialog dialog = new Dialog(myContext);

		Window W = dialog.getWindow();
		W.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		W.setLayout(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		W.requestFeature(Window.FEATURE_NO_TITLE);
		W.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		dialog.setContentView(R.layout.options_invite_layout);

		((Button) dialog.findViewById(R.id.btnInvite))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						InviteFromContactsDialog contactsdialog = new InviteFromContactsDialog(
								myContext);
						contactsdialog.show();
						dialog.dismiss();
					}
				});

		((Button) dialog.findViewById(R.id.btnSearch))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						SearchFromServerDialog searchdialog = new SearchFromServerDialog(
								myContext);
						searchdialog.show();
						dialog.dismiss();
					}
				});

		dialog.show();
	}

	// handle message
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context ctx, Intent intent) {
			String action = intent.getAction();

			if (action.equals(com.sgu.findyourfriend.mgr.Config.MAIN_ACTION)) {
				if (intent
						.hasExtra(com.sgu.findyourfriend.mgr.Config.ZOOM_POSITION_ACTION)) {
					int fID = intent
							.getIntExtra(
									com.sgu.findyourfriend.mgr.Config.ZOOM_POSITION_ACTION,
									-1);
					if (fID >= 0) {
						Friend f = FriendManager.getInstance().hmMemberFriends
								.get(fID);
						if (f.getAcceptState() == Friend.SHARE_RELATIONSHIP)
							mapController.zoomToPosition(f.getLastLocation());
						else
							Utility.showMessageOnCenter(context,
									"Vị trí không có sẵn");
					}
				} else if (intent
						.hasExtra(com.sgu.findyourfriend.mgr.Config.ROUTE_ACTION)) {
					hideControl();
					fIDCurrent = intent.getIntExtra(
							com.sgu.findyourfriend.mgr.Config.ROUTE_ACTION, -1);
					mapController.routeTask(fIDCurrent);
				} else if (intent
						.hasExtra(com.sgu.findyourfriend.mgr.Config.HISTORY_ACTION)) {

					hideControl();
					fIDCurrent = intent.getIntExtra(
							com.sgu.findyourfriend.mgr.Config.HISTORY_ACTION,
							-1);
					mapController.drawHistoryTask(fIDCurrent);
				}

			} else if (action
					.equals(com.sgu.findyourfriend.mgr.Config.UPDATE_UI)) {
				if (intent.getStringExtra(
						(com.sgu.findyourfriend.mgr.Config.UPDATE_TYPE))
						.equals(Utility.SHARE)) {

					swipeAdapter.notifyDataSetChanged();
					((MainActivity) myContext).notifyDataChange();
					changeVisibilityView();

				} else if (intent.getStringExtra(
						(com.sgu.findyourfriend.mgr.Config.UPDATE_TYPE))
						.equals(Utility.FRIEND)) {
					if (intent.getStringExtra(
							(com.sgu.findyourfriend.mgr.Config.UPDATE_ACTION))
							.equals(Utility.REQUEST)) {
						// update friend

					} else if (intent.getStringExtra(
							(com.sgu.findyourfriend.mgr.Config.UPDATE_ACTION))
							.equals(Utility.RESPONSE_YES)) {

						Log.i(TAG, "friend response yes");
						// add new friend to map
						for (int id : FriendManager.getInstance().hmMemberFriends
								.keySet()) {
							Friend fr = FriendManager.getInstance().hmMemberFriends
									.get(id);

							if (null != fr.getLastLocation()
									&& fr.getAcceptState() == Friend.SHARE_RELATIONSHIP
									&& !markerManager.containsKey(id)) {
								Log.i(TAG, "ok");
								createMakerOnMap(fr);
							}
						}

					} else {
						// response no

					}

					Log.i("UPDATE AFTER", "OK");
					swipeAdapter.notifyDataSetChanged();
					((MainActivity) myContext).notifyDataChange();
					changeVisibilityView();
				}
			}
		}

	};

	// ---------- private class
	class OnCallClick implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			if (maskEn[CALL] == 1 && isControllerShowed) {
				tickVibrator();
				hideControl();
				mapController.callTask(fIDCurrent);
			}
		}
	}

	class OnMessageClick implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			if (maskEn[MESSAGE] == 1 && isControllerShowed) {
				tickVibrator();
				hideControl();
				mapController.sendMessageTask(fIDCurrent);
			}
		}
	}

	class OnHistoryClick implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			if (maskEn[HISTORIES] == 1
					&& null != FriendManager.getInstance().hmMemberFriends.get(
							fIDCurrent).getLastLocation() && isControllerShowed) {
				tickVibrator();
				hideControl();
				hideMarker();
				mapController.drawHistoryTask(fIDCurrent);
			} else {

				Utility.showMessageOnCenter(context,
						"Vị trí  hiện tại không có sẵn");
			}

		}
	}

	class OnUpdateClick implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			if (maskEn[UPDATE] == 1 && isControllerShowed) {
				tickVibrator();
				hideControl();
				showMarker();

				mapController.updatePositionTask(fIDCurrent);
			}
		}
	}

	class OnRouteClick implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			if (maskEn[ROUTE] == 1
					&& null != FriendManager.getInstance().hmMemberFriends.get(
							fIDCurrent).getLastLocation() && isControllerShowed) {
				tickVibrator();
				hideControl();
				hideMarker(MyProfileManager.getInstance().getMineInstance(),
						FriendManager.getInstance().hmMemberFriends
								.get(fIDCurrent));
				mapController.routeTask(fIDCurrent);
			} else {
				// Utility.showMessageOnCenter(context,
				// "Vị trí  hiện tại không có sẵn");
			}

		}
	}

	class OnRequestClick implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			if (maskEn[REQUEST] == 1 && isControllerShowed) {
				tickVibrator();
				hideControl();
				showMarker();
				Friend f = FriendManager.getInstance().hmMemberFriends
						.get(fIDCurrent);

				if (f.getAcceptState() == Friend.REQUESTED_SHARE)
					mapController.acceptTask(fIDCurrent);
				else
					mapController.requestTask(fIDCurrent);

				// update ui
			}

		}
	}

	class OnAcceptClick implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			if (maskEn[ACCEPT] == 1 && isControllerShowed) {
				tickVibrator();
				hideControl();
				showMarker();
				Friend f = FriendManager.getInstance().hmMemberFriends
						.get(fIDCurrent);

				mapController.requestTask(fIDCurrent);

				// update ui
			}
		}
	}

	class OnUnFriendClick implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

		}
	}

	class OnRemoveHistoryClick implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

		}
	}

	class OnStopShareClick implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

		}
	}
	
	class OnToastClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// hideMarker();

			PositionDetailFragment fragment = new PositionDetailFragment(
					mThis);

			Bundle bundle = new Bundle();
			bundle.putInt("friendId", fIDCurrent);
			bundle.putString("address", textViewAddress.getText().toString());
			bundle.putDouble("myLat", MyProfileManager
					.getInstance().getMyPosition().latitude);
			bundle.putDouble("myLng", MyProfileManager
					.getInstance().getMyPosition().longitude);

			fragment.setArguments(bundle);

			hideControl();
			((BaseContainerFragment) getParentFragment())
					.replaceFragment(fragment, true);
			
		}
		
	}

}