package com.sgu.findyourfriend.screen;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.devsmart.android.ui.HorizontalListView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sgu.findyourfriend.FriendManager;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.FriendSwipeAdapter;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.StepHistory;
import com.sgu.findyourfriend.utils.Controller;
import com.sgu.findyourfriend.utils.GpsPosition;
import com.sgu.findyourfriend.utils.Utility;

public class MapFragment extends Fragment {

	private Context context;
	// private Activity activity;

	// view replace
	private static View view;
	private View inc; // include layout for horizontal friend list

	// map for app
	private GoogleMap mMap;
	private ArrayList<LatLng> markerPoints;

	private HashMap<String, Marker> markerManager;

	private FragmentActivity myContext;

	// view in map fragment
	// private HorizontalScrollView controlLayout;
	private HorizontalListView avatarListView;
	private FriendSwipeAdapter swipeAdapter;

	// state of controller
	private Boolean isOpenCrt = false;

	// friend's id current
	private int idFriendCur = -1;

	private Controller aController;
	private AsyncTask<Void, Void, Void> mSendLocationTask;
	private AsyncTask<Void, Void, Void> mLoadLocationTask;

	// GPS Controller
	GpsPosition gpsPosition;

	// GPS Direction controller
	//

	private List<LatLng> locationsHis = null;
	private List<Friend> friendList;

	private View viewSelected = null;

	// control button
	private Button btnMessage;
	private Button btnCall;
	private Button btnHistory;
	private Button btnUpdate;
	private Button btnRoute;
	private Button btnRequest;

	// state variable
	private boolean isRouting;
	
	private boolean isMapStarted = false;

	// masks check control
	private int[] maskEn = new int[6];

	private MapController mapController;

	public static final String TAG = "MAP FRAGMENT";

	public MapFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.i("TAG", "CALL one");

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

		// ((ProgressBar)
		// view.findViewById(R.id.prbLoader)).setVisibility(View.VISIBLE);

		afterViewCreate(view);

		// get bundle
		Bundle bundle = getArguments();

		if (null != bundle) {
			idFriendCur = bundle.getInt("friendId");
			String task = bundle.getString("task");

			if (task.equals("route")) {
				mapController.routeTask(idFriendCur);
			}
		}

		// ((ProgressBar)
		// view.findViewById(R.id.prbLoader)).setVisibility(View.GONE);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	// @Override
	// public void onViewCreated(View view, Bundle savedInstanceState) {
	// super.onViewCreated(view, savedInstanceState);

	private void afterViewCreate(View view) {
		// set actionbar
		View bar = getActivity().getActionBar().getCustomView();
		bar.findViewById(R.id.grpItemControl).setVisibility(View.VISIBLE);
		bar.findViewById(R.id.txtSend).setVisibility(View.GONE);

		// get Gps position
		gpsPosition = new GpsPosition(context);

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

		// set state variable
		isRouting = false;

		// initialize
		markerPoints = new ArrayList<LatLng>();
		friendList = new ArrayList<Friend>();
		markerManager = new HashMap<String, Marker>();

		// setup control layout and avatar list view
		inc = view.findViewById(R.id.inc_horixontal_container);

		// controlLayout = (HorizontalScrollView) view
		// .findViewById(R.id.controlLayout);

		// setup friend list
		setupFriendList();

		gpsPosition.startRecording();
		
		// setup map
		setupMap();

		
		// mapController.zoomToPosition(gpsPosition.getLastLocation());

		// init mapController
		mapController = new MapController(context, this, mMap);

		// setup control view
		setupControlView();

		// setupLocationsHistory();
		loadFriendPosition(30000);

		// generateLocation(NEWARK);

		// test friend list

		for (Friend f : FriendManager.instance.friends) {
			Log.i(TAG, f.getUserInfo().getName());
		}

	}

	private void setupMap() {
		FragmentManager myFM = myContext.getSupportFragmentManager();
		SupportMapFragment mySpFrg = (SupportMapFragment) myFM
				.findFragmentById(R.id.mapFragment);

		mMap = mySpFrg.getMap();
		mMap.setMyLocationEnabled(true);
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.getUiSettings().setZoomControlsEnabled(false);

		// demo overlay
		final LatLng NEWARK = new LatLng(40.714086, -74.228697);

		BitmapDescriptor image = BitmapDescriptorFactory
				.fromResource(R.drawable.ic_launcher);
		GroundOverlayOptions groundOverlay = new GroundOverlayOptions()
				.image(image).position(NEWARK, 500f).transparency(0.5f);
		mMap.addGroundOverlay(groundOverlay);

		// setup infor window on mapview
		/*
		 * mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
		 * 
		 * @Override public View getInfoWindow(Marker marker) { return null; }
		 * 
		 * @Override public View getInfoContents(Marker marker) { View v =
		 * activity.getLayoutInflater().inflate( R.layout.info_window_layout,
		 * null); LatLng latLng = marker.getPosition();
		 * 
		 * TextView tvLat = (TextView) v.findViewById(R.id.tv_lat); TextView
		 * tvLng = (TextView) v.findViewById(R.id.tv_lng);
		 * 
		 * tvLat.setText("Latitute: " + latLng.latitude);
		 * tvLng.setText("Longtitute: " + latLng.longitude); TextView tvAddress
		 * = (TextView) v.findViewById(R.id.tv_address);
		 * 
		 * tvLat.setText("Latitute: " + latLng.latitude);
		 * tvLng.setText("Longtitute: " + latLng.longitude);
		 * 
		 * String address = getAddress(latLng); if (address.equals(""))
		 * tvAddress.setText("Address: not available"); else
		 * tvAddress.setText("Address: " + address);
		 * 
		 * return v; } });
		 */

		// setup info window event
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(final Marker marker) {

				Log.i(TAG, "YES");

				// markerManager.keySet()
				String friendId = "";
				for (String k : markerManager.keySet()) {
					if (markerManager.get(k).equals(marker)) {
						friendId = k;
						break;
					}
				}

				for (int i = 0; i < friendList.size(); ++i)
					if (friendList.get(i).getNumberLogin().equals(friendId)) {
						idFriendCur = i;
						break;
					}

				if (idFriendCur == -1) {
					Log.i(TAG, "id not found!");
					return;
				}

				marker.hideInfoWindow();
				final Handler mUpdateHandler = new Handler();
				Runnable mUpdateRunnable = new Runnable() {
					public void run() {

						PositionDetailFragment fragment = new PositionDetailFragment();

						Bundle bundle = new Bundle();
						bundle.putInt("friendId", idFriendCur);
						bundle.putString("address",
								getAddress(marker.getPosition()));
						fragment.setArguments(bundle);

						hideControl();
						((BaseContainerFragment) getParentFragment())
								.replaceFragment(fragment, true);

					}
				};

				mUpdateHandler.postDelayed(mUpdateRunnable, 50);

			}
		});

		// setup onclick on map
		mMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				Log.i("POS", point.latitude + " # " + point.longitude);

				if (viewSelected != null) {
					hideControl();
					changleSelectMarker(idFriendCur, false);
					idFriendCur = -1;
				}
			}
		});
		
		
		// zoom to my location
		mMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
			
			@Override
			public void onMyLocationChange(Location location) {
				
				if (!isMapStarted) {
					mapController.zoomToPosition(location);
					isMapStarted = true;
				}
				
			}
		});
		
	}


	private void setupFriendList() {
		avatarListView = (HorizontalListView) view
				.findViewById(R.id.avatarListView);

		friendList = FriendManager.instance.friends;

		swipeAdapter = new FriendSwipeAdapter(context, friendList);
		avatarListView.setAdapter(swipeAdapter);

		avatarListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.i(TAG, "click at " + position);

				if (idFriendCur >= 0)
					changleSelectMarker(idFriendCur, false);

				idFriendCur = position;
				changleSelectMarker(idFriendCur, true);

				// dis/en visible on control view
				changeVisibilityView();

				if (viewSelected != null) {
					if (!viewSelected.equals(view)) {
						viewSelected.setBackgroundColor(0x00);
						view.setBackgroundColor(0xff086EBC);

						mapController.zoomToPosition(friendList.get(position)
								.getUserInfo().getLastLocation());
					}
				} else {
					showControl();
					view.setBackgroundColor(0xff086EBC);

					mapController.zoomToPosition(friendList.get(position)
							.getUserInfo().getLastLocation());
				}

				viewSelected = view;

			}
		});
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity.getApplicationContext();
		myContext = (FragmentActivity) activity;
		// this.activity = activity;
	}

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
					hideControl();
					mapController.sendMessageTask(idFriendCur);
				}
			}
		});

		btnCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (maskEn[1] == 1) {
					hideControl();
					mapController.callTask(idFriendCur);
				}
			}
		});

		btnHistory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (maskEn[2] == 1) {
					hideControl();
					mapController.drawHistoryTask(idFriendCur);
					// onHistoryExcute(friendList.get(idFriendCur).getUserInfo()
					// .getSteps());
				}
			}
		});

		btnUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (maskEn[3] == 1) {
					hideControl();
					mapController.updatePositionTask(idFriendCur);
				}
			}
		});

		btnRoute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (maskEn[4] == 1) {
					hideControl();
					mapController.routeTask(idFriendCur);
				}
			}
		});

		btnRequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (maskEn[5] == 1) {
					hideControl();
					mapController.requestTask(idFriendCur);
				}
			}
		});
	}

	private String generatePos(String s1) {
		char[] c1 = s1.toCharArray();

		int pos = (int) (Math.random() * (c1.length - 3)) + 3;
		int k = c1[pos];
		k = (k + (int) Math.random() * 11) % 10;

		c1[pos] = (char) (k + '0');
		return new String(c1);
	}

	private List<StepHistory> generateLocation(LatLng curLatlng) {
		List<StepHistory> lsh = new ArrayList<StepHistory>();
		String s1 = String.valueOf(curLatlng.latitude);
		String s2 = String.valueOf(curLatlng.longitude);
		int nLocation = 10;

		while (nLocation > 0) {
			lsh.add(new StepHistory(new LatLng(Double
					.parseDouble(generatePos(s1)), Double
					.parseDouble(generatePos(s2))), new Date(System
					.currentTimeMillis() - (int) Math.random() * 10000)));
			nLocation--;

		}

		for (StepHistory s : lsh) {
			Log.i("TAG", s.getLatLng().latitude + " # "
					+ s.getLatLng().longitude);
		}

		return lsh;
	}

	private void disableView(int idControlView) {
		((LinearLayout) ((ViewGroup) getView().findViewById(idControlView)
				.getParent())).setBackgroundColor(Color.GRAY);

	}

	private void enableView(int idControlView) {
		((LinearLayout) ((ViewGroup) getView().findViewById(idControlView)
				.getParent())).setBackgroundColor(Color.TRANSPARENT);

	}

	private void changleSelectMarker(int idFriendCur, boolean isSelected) {
		String numberId = friendList.get(idFriendCur).getNumberLogin();
		Marker m = markerManager.get(numberId);
		MarkerOptions opt = new MarkerOptions();
		opt.position(m.getPosition());
		opt.title(m.getTitle());
		opt.snippet(m.getSnippet());

		if (isSelected)
			opt.icon(combileLocationIcon(idFriendCur, true));
		else
			opt.icon(combileLocationIcon(idFriendCur, false));

		m.remove();
		m = mMap.addMarker(opt);
		markerManager.put(numberId, m);
	}

	private void changeVisibilityView() {
		// check view visiable
		if (idFriendCur == 0) {
			disableView(R.id.btnMessage);
			maskEn[0] = 0;
			disableView(R.id.btnCall);
			maskEn[1] = 0;
			disableView(R.id.btnRoute);
			maskEn[4] = 0;
			maskEn[2] = maskEn[3] = maskEn[5] = 1;

		} else {
			enableView(R.id.btnMessage);
			maskEn[0] = 1;
			enableView(R.id.btnCall);
			maskEn[1] = 1;
			enableView(R.id.btnRoute);
			maskEn[4] = 1;
			maskEn[2] = maskEn[3] = 1;
			if (friendList.get(idFriendCur).getShare() == 1) {
				disableView(R.id.btnRequest);
				maskEn[5] = 0;
			} else {
				enableView(R.id.btnRequest);
				maskEn[5] = 1;
			}

		}
	}

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

	// should load with timer
	private void loadFriendPosition(final long interval) {

		mMap.clear();
		for (Friend f : friendList) {
			if (f.getUserInfo().getLastLocation() == null)
				continue;
			MarkerOptions options = new MarkerOptions();
			options.position(f.getUserInfo().getLastLocation());
			options.title(getAddress(f.getUserInfo().getLastLocation()));
			options.snippet("cập nhật "
					+ Utility.convertTimeToString(System.currentTimeMillis()
							- f.getUserInfo().getLastestlogin().getTime())
					+ "trước.");

			options.icon(combileLocationIcon(idFriendCur, false));
			Marker m = mMap.addMarker(options);

			markerManager.put(f.getNumberLogin(), m);
		}

		final Handler mUpdateHandler = new Handler();
		Runnable mUpdateRunnable = new Runnable() {
			public void run() {
				Log.i("RUN", System.currentTimeMillis() + "");

				Location lastLocation = gpsPosition.getLastLocation();

				if (lastLocation != null) {
					friendList
							.get(0)
							.getUserInfo()
							.setLastLocation(
									new LatLng(lastLocation.getLatitude(),
											lastLocation.getLongitude()));

					String myId = friendList.get(0).getNumberLogin();
					Friend f = friendList.get(0);
					Marker m;

					if (markerManager.containsKey(myId)) {
						m = markerManager.get(myId);
						m.setPosition(new LatLng(lastLocation.getLatitude(),
								lastLocation.getLongitude()));
					} else {
						MarkerOptions options = new MarkerOptions();
						options.position(f.getUserInfo().getLastLocation());
						options.title(getAddress(f.getUserInfo()
								.getLastLocation()));
						options.snippet("cập nhật "
								+ Utility.convertTimeToString(lastLocation
										.getTime()
										- f.getUserInfo().getLastestlogin()
												.getTime()) + "trước.");

						options.icon(combileLocationIcon(idFriendCur, false));
						m = mMap.addMarker(options);
					}
					markerManager.put(myId, m);
				}

				/*
				 * mMap.clear(); for (Friend f : friendList) { if
				 * (f.getUserInfo().getLastLocation() == null) continue;
				 * MarkerOptions options = new MarkerOptions();
				 * options.position(f.getUserInfo().getLastLocation());
				 * options.title(getAddress(f.getUserInfo().getLastLocation()));
				 * 
				 * options.icon(combileLocationIcon()); mMap.addMarker(options);
				 * }
				 */
				mUpdateHandler.postDelayed(this, interval);
			}
		};

		mUpdateHandler.postAtTime(mUpdateRunnable, 0);
		mUpdateHandler.postDelayed(mUpdateRunnable, interval);
	}

	// setup iconview on map
	private BitmapDescriptor combileLocationIcon(int friendId,
			boolean isSelected) {
		Bitmap brbitmap;

		if (isSelected)
			brbitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.boder_location_selected);
		else
			brbitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.boder_location_unselected);

		Bitmap bmAvatar = BitmapFactory.decodeResource(getResources(),
				R.drawable.avatar3);

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

	private void setupSendLocation(final double latitute,
			final double longtitute, final String regId) {

		mSendLocationTask = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				// Register on our server
				// On server creates a new user
				aController.sendLocation(context, latitute, longtitute, regId);

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mSendLocationTask = null;
			}

		};

		// execute AsyncTask
		mSendLocationTask.execute(null, null, null);
	}

	public String getAddress(LatLng point) {
		try {
			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(context);
			if (point.latitude != 0 || point.longitude != 0) {
				addresses = geocoder.getFromLocation(point.latitude,
						point.longitude, 1);

				Address address = addresses.get(0);

				String addressText = String.format(
						"%s, %s",
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "", address
								.getCountryName());

				return addressText;
			} else {
				Toast.makeText(context, "latitude and longitude are null",
						Toast.LENGTH_LONG).show();
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
