package com.sgu.findyourfriend.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.devsmart.android.ui.HorizontalListView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.FriendSwipeAdapter;
import com.sgu.findyourfriend.ctr.ControlOptions;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MyLocationChangeListener;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.utils.Controller;
import com.sgu.findyourfriend.utils.GpsPosition;
import com.sgu.findyourfriend.utils.Utility;

public class MapFragment extends Fragment implements MyLocationChangeListener {

	// point to itself
	private MapFragment mThis = this;

	private Context context;

	// view replace
	private static View view;
	private View inc; // include layout for horizontal friend list

	// map for app
	private GoogleMap mMap;
	private ArrayList<LatLng> markerPoints;

	private HashMap<Integer, Marker> markerManager;

	private FragmentActivity myContext;

	// view in map fragment
	private HorizontalListView avatarListView;
	private FriendSwipeAdapter swipeAdapter;

	// friend's id current
	// private int idFriendCur = -1;
	private int fIDCurrent = -1;

	private Controller aController;

	// GPS Controller
	private GpsPosition gpsPosition;

	private List<LatLng> locationsHis = null;

	private View viewSelected = null;

	// control button
	private Button btnMessage;
	private Button btnCall;
	private Button btnHistory;
	private Button btnUpdate;
	private Button btnRoute;
	private Button btnRequest;

	private ProgressBar pbOnMap;
	
	private FrameLayout frWaitLayout;

	private boolean isMapStarted = false;

	// state marker hide/show
	private boolean isShowMarker;

	// masks check control
	private int[] maskEn = new int[6];

	private MapController mapController;

	// management
	private Resources resource;
	private FriendManager friendManager;

	// bi map resource for control view icon
	private HashMap<Integer, Integer> hmToCap20;
	private HashMap<Integer, Integer> hmFromCap20;

	// vibrator
	private Vibrator vibrator;

	public static final String TAG = "MAP FRAGMENT";

	public MapFragment() {
		// init
		friendManager = FriendManager.getInstance();
		isMapStarted = false;
		isShowMarker = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.i(TAG,
				"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ start view");

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

		
		if (!FriendManager.getInstance().isReady) {
			(new AsyncTask<Void, Void, Void>() {

				@Override
				protected void onPreExecute() {
					// get progress bar
					frWaitLayout = (FrameLayout) view.findViewById(R.id.frWaitLayout);
					frWaitLayout.setVisibility(View.VISIBLE);
//					pbOnMap = (ProgressBar) view.findViewById(R.id.pbOnMap);
//					pbOnMap.setVisibility(View.VISIBLE);
				}
				
				@Override
				protected Void doInBackground(Void... arg0) {
					FriendManager.getInstance().loadFriend();
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					FriendManager.getInstance().setupAfterLoading();
					swipeAdapter.notifyDataSetChanged();
					FriendManager.getInstance().isReady = true;
					
					
					((MainActivity) myContext).notifyDataChange();
					
					
					loadFriendsPosition(10000);
					frWaitLayout.setVisibility(View.GONE);
					
					Toast.makeText(getActivity(), "loaded!", Toast.LENGTH_SHORT).show();
				}

			}).execute();

		}
		
		
		
		
		afterViewCreate(view);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity.getApplicationContext();
		myContext = (FragmentActivity) activity;
		
		
		
		Log.i("context 1", activity.getApplication().toString());
		
		
	}

	// ----------------- init variable and setup methods ----------- //
	private void afterViewCreate(View view) {
		// set actionbar
		View bar = getActivity().getActionBar().getCustomView();
		bar.findViewById(R.id.grpItemControl).setVisibility(View.VISIBLE);
		bar.findViewById(R.id.imgSend).setVisibility(View.GONE);

		// ((Button) view.findViewById(R.id.btnMessage)).setAlpha(0.5f);

		// get progress bar
		pbOnMap = (ProgressBar) view.findViewById(R.id.pbOnMap);
		pbOnMap.setVisibility(View.GONE);
		
		
		// get Gps position
		gpsPosition = new GpsPosition(context, this);

		// get resources
		resource = getActivity().getResources();

		// get controller
		aController = (Controller) context;

		// Check if Internet present
		if (!aController.isConnectingToInternet()) {

			// Internet Connection is not present
			aController.showAlertDialog(context, "Internet Connection Error",
					"Please connect to Internet connection", false);
			// stop executing code by return
			return;
		}

		// initialize
		markerPoints = new ArrayList<LatLng>();
		markerManager = new HashMap<Integer, Marker>();

		

		// setup control item hashmap
		setupForSelectionControl();

		// setup control layout and avatar list view
		inc = view.findViewById(R.id.inc_horixontal_container);

		// setup friend list
		setupFriendList();

		// setup map
		setupMap();

		// init + setup mapController
		mapController = new MapController(this.getParentFragment()
				.getActivity(), this, mMap, pbOnMap);
		setupControlView();

		// setup send location to server
		gpsPosition.startRecording();

		// load friends position after 1 mins
		loadFriendsPosition(60000);
		updateFriendsPosition(60000);

		// check require from sliding friend event
		if (ControlOptions.getInstance().isRequire()) {
			Log.i("REQUIRE", "####################################");
			int fId = ControlOptions.getInstance().getHashMap("friendId");
			mapController
					.zoomToPosition(friendManager.getInstance().hmMemberFriends
							.get(fId).getLastLocation());
			ControlOptions.getInstance().finish();
		}

		// check bundle from detail info fragment
		Bundle bundle = getArguments();

		Log.i(TAG,
				"################################################################################# start");

		if (null != bundle) {
			fIDCurrent = bundle.getInt("friendId");
			String task = bundle.getString("task", "");

			if (task.equals("route")) {

				final Handler mUpdateHandler = new Handler();
				Runnable mUpdateRunnable = new Runnable() {
					public void run() {

						hideControl();
						hideMarker(friendManager.getInstance().getMine(),
								friendManager.getInstance().hmMemberFriends
										.get(fIDCurrent));
						mapController.routeTask(fIDCurrent);
					}
				};

				mUpdateHandler.postDelayed(mUpdateRunnable, 50);

			} else if (task.equals("history")) {
				Log.i(TAG, "map history!!! " + markerManager.size());

				final Handler mUpdateHandler = new Handler();
				Runnable mUpdateRunnable = new Runnable() {
					public void run() {

						hideControl();
						hideMarker();
						mapController.drawHistoryTask(fIDCurrent);
					}
				};

				mUpdateHandler.postDelayed(mUpdateRunnable, 50);

			}

			bundle.clear();
		}

		Log.i(TAG,
				"################################################################################# end");

		// vibrator setup
		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);

		// hide control console
		hideControl();
	}

	public void tickVibrator() {
		vibrator.vibrate(100);
	}

	// ---------------- setup map ----------------------- //
	@SuppressWarnings("deprecation")
	private void setupMap() {
		FragmentManager myFM = myContext.getSupportFragmentManager();
		SupportMapFragment mySpFrg = (SupportMapFragment) myFM
				.findFragmentById(R.id.mapFragment);

		mMap = mySpFrg.getMap();
		mMap.setMyLocationEnabled(false);

		// load setup map from setting activity
		mMap.setMapType(SettingManager.getInstance().getMapType());

		mMap.getUiSettings().setZoomControlsEnabled(false);

		// hide control my center
		// mMap.getUiSettings().setMyLocationButtonEnabled(false);

		// demo overlay
		final LatLng NEWARK = new LatLng(40.714086, -74.228697);

		BitmapDescriptor image = BitmapDescriptorFactory
				.fromResource(R.drawable.ic_launcher);
		GroundOverlayOptions groundOverlay = new GroundOverlayOptions()
				.image(image).position(NEWARK, 500f).transparency(0.5f);
		mMap.addGroundOverlay(groundOverlay);

		// setup info window event
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(final Marker marker) {

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

						PositionDetailFragment fragment = new PositionDetailFragment(
								mThis);

						Bundle bundle = new Bundle();
						bundle.putInt("friendId", fIDCurrent);
						bundle.putString("address",
								mapController.getAddress(marker.getPosition()));
						bundle.putDouble(
								"myLat",
								MyProfileManager.getInstance().myLocation.latitude);
						bundle.putDouble(
								"myLng",
								MyProfileManager.getInstance().myLocation.longitude);

						fragment.setArguments(bundle);

						hideControl();
						((BaseContainerFragment) getParentFragment())
								.replaceFragment(fragment, true);

					}
				};

				mUpdateHandler.postDelayed(mUpdateRunnable, 50);

			}
		});

		mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(final Marker marker) {
				Log.i(TAG, "marker on click!");

				for (int k : markerManager.keySet()) {
					if (marker.equals(markerManager.get(k))) {
						if (fIDCurrent != k) {
							changleSelectMarker(fIDCurrent, false);
							fIDCurrent = k;
							changleSelectMarker(fIDCurrent, true);
							
							// setup control
							swipeAdapter.hightLightItem(swipeAdapter.getPositionByFriendID(k));
							changeVisibilityView();
							showControl();
						}
						
						marker.setTitle(mapController.getAddress(
								FriendManager.getInstance().hmMemberFriends.get(k).getLastLocation()));						
						
						break;
					}
				}
				return false;
			}
		});

		// setup onclick on map
		mMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				Log.i("POS", point.latitude + " # " + point.longitude);

				swipeAdapter.unHightLightItem();
				hideControl();
				changleSelectMarker(fIDCurrent, false);
//				
//				
//				if (viewSelected != null) {
//					hideControl();
//					changleSelectMarker(fIDCurrent, false);
//					fIDCurrent = -1;
//				}
			}
		});

//		// zoom to my location
//		mMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
//
//			@Override
//			public void onMyLocationChange(Location location) {
//
//			}
//		});
	}

	// ---------------- setup friend list ------------------ //
	private void setupFriendList() {
		avatarListView = (HorizontalListView) view
				.findViewById(R.id.avatarListView);

		swipeAdapter = new FriendSwipeAdapter(context,
				R.layout.item_friend_accepted,
				friendManager.getInstance().memberFriends);
		avatarListView.setAdapter(swipeAdapter);

		
		avatarListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.i(TAG, "click at " + position);

				if (fIDCurrent >= 0)
					changleSelectMarker(fIDCurrent, false);

				fIDCurrent = swipeAdapter.getItem(position).getUserInfo()
						.getId();

				changleSelectMarker(fIDCurrent, true);

				// dis/en visible on control view
				changeVisibilityView();

				
				
				swipeAdapter.hightLightItem(position);
				showControl();
				
				if (swipeAdapter.getItem(position).isShare())
					mapController.zoomToPosition(swipeAdapter.getItem(
							position).getLastLocation());
				
//				
//				if (viewSelected != null) {
//					if (!viewSelected.equals(view)) {
//						viewSelected.setBackgroundColor(0x00);
//						view.setBackgroundColor(resource
//								.getColor(R.color.selector_color));
//
//						if (swipeAdapter.getItem(position).isShare())
//							mapController.zoomToPosition(swipeAdapter.getItem(
//									position).getLastLocation());
//					}
//				} else {
//					showControl();
//					view.setBackgroundColor(resource
//							.getColor(R.color.selector_color));
//					if (swipeAdapter.getItem(position).isShare())
//						mapController.zoomToPosition(swipeAdapter.getItem(
//								position).getLastLocation());
//				}
//
//				viewSelected = view;

			}
		});
	}

	// ----------------- setup control at bottom screen ----------- //
	private void setupControlView() {
		inc = view.findViewById(R.id.inc_horixontal_container);
		btnMessage = (Button) inc.findViewById(R.id.btnMessage);
		btnCall = (Button) inc.findViewById(R.id.btnCall);
		btnHistory = (Button) inc.findViewById(R.id.btnHistory);
		btnUpdate = (Button) inc.findViewById(R.id.btnUpdate);
		btnRoute = (Button) inc.findViewById(R.id.btnRoute);
		btnRequest = (Button) inc.findViewById(R.id.btnRequest);

		btnMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (maskEn[0] == 1) {
					tickVibrator();
					hideControl();
					mapController.sendMessageTask(fIDCurrent);
				}
			}
		});

		btnCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (maskEn[1] == 1) {
					tickVibrator();
					hideControl();
					mapController.callTask(fIDCurrent);
				}
			}
		});

		btnHistory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (maskEn[2] == 1) {
					tickVibrator();
					hideControl();
					hideMarker();
					mapController.drawHistoryTask(fIDCurrent);
				}
			}
		});

		btnUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (maskEn[3] == 1) {
					tickVibrator();
					hideControl();
					showMarker();
					mapController.updatePositionTask(fIDCurrent);
				}
			}
		});

		btnRoute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (maskEn[4] == 1) {
					tickVibrator();
					hideControl();
					hideMarker(friendManager.getInstance().getMine(),
							friendManager.getInstance().hmMemberFriends
									.get(fIDCurrent));
					mapController.routeTask(fIDCurrent);
				}
			}
		});

		btnRequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (maskEn[5] == 1) {
					tickVibrator();
					hideControl();
					showMarker();
					mapController.requestTask(fIDCurrent);
				}
			}
		});
	}

	// / --------------------- utilities methods --------------------- ///

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

	private void setupForSelectionControl() {
		// allways enable
		// maskEn[2] = maskEn[3] = 1;

		hmToCap20 = new HashMap<Integer, Integer>(6);
		hmToCap20.put(R.id.btnMessage, R.drawable.ic_message_cap20);
		hmToCap20.put(R.id.btnCall, R.drawable.ic_call_cap20);
		hmToCap20.put(R.id.btnRequest, R.drawable.ic_request_cap20);
		hmToCap20.put(R.id.btnRoute, R.drawable.ic_route_cap20);
		hmToCap20.put(R.id.btnHistory, R.drawable.ic_history_cap20);
		hmToCap20.put(R.id.btnUpdate, R.drawable.ic_update_cap20);

		hmFromCap20 = new HashMap<Integer, Integer>(6);
		hmFromCap20.put(R.id.btnMessage, R.drawable.ic_message);
		hmFromCap20.put(R.id.btnCall, R.drawable.ic_call);
		hmFromCap20.put(R.id.btnRequest, R.drawable.ic_request);
		hmFromCap20.put(R.id.btnRoute, R.drawable.ic_route);
		hmFromCap20.put(R.id.btnHistory, R.drawable.ic_history);
		hmFromCap20.put(R.id.btnUpdate, R.drawable.ic_update);

	}

	public void disableView(View rView, int idRaw) {
		((LinearLayout) ((ViewGroup) rView.findViewById(idRaw).getParent()))
				.getChildAt(1).setAlpha(0.2f);

		((Button) rView.findViewById(idRaw)).setBackgroundDrawable(resource
				.getDrawable(hmToCap20.get(idRaw)));
	}

	public void enableView(View rView, int idRaw) {
		((LinearLayout) ((ViewGroup) rView.findViewById(idRaw).getParent()))
				.getChildAt(1).setAlpha(1f);

		((Button) rView.findViewById(idRaw)).setBackgroundDrawable(resource
				.getDrawable(hmFromCap20.get(idRaw)));

	}

	private void changeVisibilityView() {
		View rView = getView();
		// check view visiable
		if (fIDCurrent == MyProfileManager.getInstance().mine.getId()) {
			disableView(rView, R.id.btnMessage);
			maskEn[0] = 0;

			disableView(rView, R.id.btnCall);
			maskEn[1] = 0;

			enableView(rView, R.id.btnHistory);
			maskEn[2] = 1;

			enableView(rView, R.id.btnUpdate);
			maskEn[3] = 1;

			disableView(rView, R.id.btnRoute);
			maskEn[4] = 0;

			disableView(rView, R.id.btnRequest);
			maskEn[5] = 0;

		} else {

			Friend f = FriendManager.getInstance().hmMemberFriends
					.get(fIDCurrent);
			if (f.isShare()) {
				// full options
				enableView(rView, R.id.btnMessage);
				maskEn[0] = 1;

				enableView(rView, R.id.btnCall);
				maskEn[1] = 1;

				enableView(rView, R.id.btnHistory);
				maskEn[2] = 1;

				enableView(rView, R.id.btnUpdate);
				maskEn[3] = 1;

				enableView(rView, R.id.btnRoute);
				maskEn[4] = 1;

				enableView(rView, R.id.btnRequest);
				maskEn[5] = 1;

			} else {
				if (f.getAcceptState() == Friend.ACCEPT_STATE) {
					disableView(rView, R.id.btnMessage);
					maskEn[0] = 0;

					disableView(rView, R.id.btnCall);
					maskEn[1] = 0;

					disableView(rView, R.id.btnHistory);
					maskEn[2] = 0;

					enableView(rView, R.id.btnUpdate);
					maskEn[3] = 1;

					disableView(rView, R.id.btnRoute);
					maskEn[4] = 0;

					enableView(rView, R.id.btnRequest);
					maskEn[5] = 1;
				} else {
					// Friend.WAIT_STATE
					disableView(rView, R.id.btnMessage);
					maskEn[0] = 0;

					disableView(rView, R.id.btnCall);
					maskEn[1] = 0;

					disableView(rView, R.id.btnHistory);
					maskEn[2] = 0;

					enableView(rView, R.id.btnUpdate);
					maskEn[3] = 1;

					disableView(rView, R.id.btnRoute);
					maskEn[4] = 0;

					disableView(rView, R.id.btnRequest);
					maskEn[5] = 0;
				}

			}

			// enableView(rView, R.id.btnMessage);
			// maskEn[0] = 1;
			//
			// enableView(rView, R.id.btnCall);
			// maskEn[1] = 1;
			//
			// enableView(rView, R.id.btnRoute);
			// maskEn[4] = 1;
			//
			// if (friendManager.hmMemberFriends.get(fIDCurrent).isShare()) {
			// disableView(rView, R.id.btnRequest);
			// maskEn[5] = 0;
			// } else {
			// enableView(rView, R.id.btnRequest);
			// maskEn[5] = 1;
			// }
		}
	}

	// --------------------- changle color marker --------------------- //
	private void changleSelectMarker(int idFriendCur, boolean isSelected) {
		Marker m = markerManager.get(idFriendCur);
		Friend f = friendManager.hmMemberFriends.get(idFriendCur);

		if (m != null) {
			if (isSelected)
				m.setIcon(combileLocationIcon(f, true));
			else
				m.setIcon(combileLocationIcon(f, false));
		}
	}

	// -------------- show/hide control console ---------------- //
	private void showControl() {
		inc.setAlpha(1.0f);
		ValueAnimator anim = ObjectAnimator.ofFloat(inc, "translationY",
				-inc.getHeight());
		anim.setDuration(500);
		anim.start();
	}

	private void hideControl() {
		ValueAnimator anim = ObjectAnimator.ofFloat(inc, "translationY",
				inc.getHeight());
		anim.setDuration(500);
		anim.start();

		if (viewSelected != null) {
			viewSelected.setBackgroundColor(0x00);
			viewSelected = null;
		}
	}

	// -------------------- should load with timer --------------- //
	private void loadFriendsPosition(final long interval) {
		mMap.clear();

		// update for friend
		for (Friend f : swipeAdapter.getData()) {
			if (!f.isShare() || f.getLastLocation() == null)
				continue;

			createMakerOnMap(f);
		}
	}

	// -------------------------- update position ------------------------ //
	private void updateFriendsPosition(final long interval) {

		// for (int key : markerManager.keySet()) {
		// // PostData.updateLastLoation(friendManager.hmFriends.get(key),
		// // markerManager.get(key));
		// }

	}

	private void updatePositionOf(Friend f) {
		Marker m = markerManager.get(f.getUserInfo().getId());
		if (m != null) {
			m.setPosition(f.getLastLocation());
			m.setTitle(mapController.getAddress(f.getLastLocation()));
		}
	}

	// ----------------------- setup iconview on map ---------------- //
	private BitmapDescriptor combileLocationIcon(final Friend f,
			boolean isSelected) {
		final Bitmap brbitmap;

		if (isSelected)
			brbitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.boder_location_selected);
		else
			brbitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.boder_location_unselected);

		return combileBorder(drawableToBitmap(Drawable.createFromPath(f
				.getUserInfo().getAvatar())), brbitmap);
	}

	private BitmapDescriptor combileBorder(Bitmap bmAvatar, Bitmap brbitmap) {
		int w = bmAvatar.getWidth();
		int h = bmAvatar.getHeight();

		int newWidth = 70;
		int newHeight = 65;

		float scaleWidth = ((float) newWidth) / w;
		float scaleHeight = ((float) newHeight) / h;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap bmAvatarResize = Bitmap.createBitmap(bmAvatar, 0, 0, w, h,
				matrix, false);
		return BitmapDescriptorFactory.fromBitmap(overlay(brbitmap,
				bmAvatarResize));
	}

	private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
		Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(),
				bmp1.getHeight(), bmp1.getConfig());
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(bmp1, new Matrix(), null);
		canvas.drawBitmap(bmp2, (canvas.getWidth() - bmp2.getWidth()) / 2,
				(canvas.getWidth() - bmp2.getWidth()) / 2, null);
		return bmOverlay;
	}

	private Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	// ----------------------- create map's marker ----------------- //
	private void createMakerOnMap(Friend f) {
		MarkerOptions options = new MarkerOptions();
		options.position(f.getLastLocation());
		// options.title(mapController.getAddress(f.getLastLocation()));
		options.snippet("cập nhật "
				+ Utility.convertMicTimeToString(System.currentTimeMillis()
						- f.getUserInfo().getLastestlogin().getTime())
				+ "trước - phạm vi sai lệch " + f.getAccurency() + "m.");

		options.icon(combileLocationIcon(f, false));
		Marker m = mMap.addMarker(options);

		markerManager.put(f.getUserInfo().getId(), m);
	}

	@Override
	public void onMyLocationChanged(Location location) {

		Log.i(TAG, "update! " + markerManager.size());
		Friend f = friendManager.getMine();
		LatLng latlng = new LatLng(location.getLatitude(),
				location.getLongitude());
		MyProfileManager.getInstance().myLocation = latlng;
		f.setLastLocation(latlng);

		updatePositionOf(f);

		if (!isMapStarted) {
			mapController.zoomToPosition(location);
			isMapStarted = true;
		}

	}

}
