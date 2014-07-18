package com.sgu.findyourfriend.screen;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.sgu.findyourfriend.FriendManager;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.FriendSwipeAdapter2;
import com.sgu.findyourfriend.ctr.ControlOptions;
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

	private HashMap<Integer, Marker> markerManager;

	private FragmentActivity myContext;

	// view in map fragment
	// private HorizontalScrollView controlLayout;
	private HorizontalListView avatarListView;
	private FriendSwipeAdapter2 swipeAdapter;

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
	// private List<Friend> friendManager.friends;

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

	// image loader
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	// management
	private Resources resource;
	private FriendManager friendManager;

	public static final String TAG = "MAP FRAGMENT";

	public MapFragment() {
		// init management
		friendManager = FriendManager.getInstance();

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

		// init resource
		resource = getActivity().getResources();

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
		markerManager = new HashMap<Integer, Marker>();

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

		loadFriendsPosition(30000);
		updateFriendsPosition(30000);

		// generateLocation(NEWARK);

		// test friend list
		for (Friend f : friendManager.friends) {
			Log.i(TAG, f.getUserInfo().getName());
		}
		
		// check require from sliding friend event
		if (ControlOptions.getInstance().isRequire()) {
			Log.i("REQUIRE", "####################################");
			int fId = Integer.parseInt(ControlOptions.getInstance().getHashMap("friendId"));
			mapController.zoomToPosition(friendManager.getInstance().friends.get(fId).getLastLocation());
			ControlOptions.getInstance().finish();
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
		 * getActivity().getLayoutInflater().inflate(
		 * R.layout.custom_infowindow, null);
		 * 
		 * 
		 * 
		 * mapController.loadViewDirectionInfo( new
		 * LatLng(mMap.getMyLocation().getLatitude(),
		 * mMap.getMyLocation().getLongitude()), marker.getPosition(), v);
		 * 
		 * return v; } });
		 */
		// setup info window event
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(final Marker marker) {

				Log.i(TAG, "YES");

				// markerManager.keySet()
				int friendId = 0;
				for (int k : markerManager.keySet()) {
					if (markerManager.get(k).equals(marker)) {
						friendId = k;
						break;
					}
				}

				for (int i = 0; i < friendManager.friends.size(); ++i)
					if (friendManager.friends.get(i).getUserInfo().getId() == friendId) {
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
						bundle.putDouble("myLat", mMap.getMyLocation()
								.getLatitude());
						bundle.putDouble("myLng", mMap.getMyLocation()
								.getLongitude());

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

				Friend f = friendManager.friends.get(0);
				if (!isMapStarted) {
					mapController.zoomToPosition(location);
					isMapStarted = true;

					// create image for mine
					createMakerOnMap(f);

				} else {
					// update position for mine

					updatePositionOf(f, location);
				}

			}
		});
		
//		mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
//			
//			@Override
//			public boolean onMarkerClick(Marker marker) {
//				
//				String k = "";
//				Friend f = null;
//				
//				for (String key : markerManager.keySet()) {
//					if (marker.equals(markerManager.get(key))) {
//						k = key;
//						f = FriendManager.getInstance().hmFriends.get(k);
//						break;
//					}
//				}
//				
//				
//				if (idFriendCur >= 0)
//					marker.setIcon(combileLocationIcon(f, false));
//				
//				for (int i = 0; i < FriendManager.getInstance().friends.size(); ++i) {
//					if (friendManager.getInstance().friends.get(i).getNumberLogin().equals(k)) {
//						idFriendCur = i;
//						break;
//					}
//				}
//				
//				marker.setIcon(combileLocationIcon(f, true));
//				hideControl();
//				
//				return false;
//			}
//		});

		// setup image loader
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		imageLoader = ImageLoader.getInstance();
		File cacheDir = StorageUtils.getCacheDirectory(context);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.memoryCacheExtraOptions(480, 800)
				// default = device screen dimensions

				.taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
				.taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR)
				.threadPoolSize(3)
				// default
				.threadPriority(Thread.NORM_PRIORITY - 1)
				// default
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				// default
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				// default
				.memoryCacheSize(2 * 1024 * 1024)
				.imageDownloader(new BaseImageDownloader(context)) // default
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
				.build();
		imageLoader.init(config);

	}

	private void setupFriendList() {
		avatarListView = (HorizontalListView) view
				.findViewById(R.id.avatarListView);

		// swipeAdapter = new FriendSwipeAdapter(context,
		// friendManager.friends);
		swipeAdapter = new FriendSwipeAdapter2(context,
				R.layout.item_friend_accepted, friendManager.friends);
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
						view.setBackgroundColor(resource
								.getColor(R.color.selector_color));

						mapController.zoomToPosition(friendManager.friends
								.get(position).getLastLocation());
					}
				} else {
					showControl();
					view.setBackgroundColor(resource
							.getColor(R.color.selector_color));

					mapController.zoomToPosition(friendManager.friends
							.get(position).getLastLocation());
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
					// onHistoryExcute(friendManager.friends.get(idFriendCur).getUserInfo()
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
		String numberId = friendManager.friends.get(idFriendCur)
				.getUserInfo().getId() + "";
		Marker m = markerManager.get(numberId);
		Friend f = friendManager.friends.get(idFriendCur);

		if (m != null) {

			if (isSelected)
				m.setIcon(combileLocationIcon(f, true));
			else
				m.setIcon(combileLocationIcon(f, false));
		}
		// m.setIcon(icon);
		//
		// MarkerOptions opt = new MarkerOptions();
		// opt.position(m.getPosition());
		// opt.title(m.getTitle());
		// opt.snippet(m.getSnippet());
		//
		// if (isSelected)
		// opt.icon(combileLocationIcon(idFriendCur, true));
		// else
		// opt.icon(combileLocationIcon(idFriendCur, false));
		//
		// m.remove();
		// m = mMap.addMarker(opt);
		// markerManager.put(numberId, m);
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
			if (friendManager.friends.get(idFriendCur).isShare()) {
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
	private void loadFriendsPosition(final long interval) {

		mMap.clear();

		// update for friend
		for (int i = 1; i < friendManager.friends.size(); ++i) {
			Friend f = friendManager.friends.get(i);

			if (f.getLastLocation() == null)
				continue;

			createMakerOnMap(f);
		}
	}

	private void updateFriendsPosition(final long interval) {

		for (int key : markerManager.keySet()) {
			//PostData.updateLastLoation(friendManager.hmFriends.get(key),
//					markerManager.get(key));
		}

	}

	// setup iconview on map
	private BitmapDescriptor combileLocationIcon(final Friend f,
			boolean isSelected) {
		final Bitmap brbitmap;

		if (isSelected)
			brbitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.boder_location_selected);
		else
			brbitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.boder_location_unselected);

		if (f != null) {
			imageLoader.loadImage(f.getUserInfo().getAvatar(),
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							// Do whatever you want with Bitmap

							int idUser = f.getUserInfo().getId();

							Marker m = markerManager.get(idUser);

							MarkerOptions opt = new MarkerOptions();
							opt.position(m.getPosition());
							opt.title(m.getTitle());
							opt.snippet(m.getSnippet());
							opt.icon(combileBorder(loadedImage, brbitmap));

							m.remove();

							m = mMap.addMarker(opt);
							markerManager.put(idUser, m);

							Log.i(TAG, "load ok");
						}
					});
		}

		return combileBorder(BitmapFactory.decodeResource(getResources(),
				R.drawable.loading), brbitmap);
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

	// ------------------------------ PRIVATE METHODS

	// // --------------------------- FOCUS

	// // --------------------------- UNTILITIES
	private void createMakerOnMap(Friend f) {
		MarkerOptions options = new MarkerOptions();
		options.position(f.getLastLocation());
		options.title(getAddress(f.getLastLocation()));
		options.snippet("cập nhật "
				+ Utility.convertMicTimeToString(System.currentTimeMillis()
						- f.getUserInfo().getLastestlogin().getTime())
				+ "trước - phạm vi sai lệch " + f.getAccurency()
				+ "m.");

		options.icon(combileLocationIcon(f, false));
		Marker m = mMap.addMarker(options);

		markerManager.put(f.getUserInfo().getId(), m);
	}

	private void updatePositionOf(Friend f, Location location) {
		Marker m = markerManager.get(f.getNumberLogin());
		if (m != null) {

			LatLng latlng = new LatLng(location.getLatitude(),
					location.getLongitude());

			m.setPosition(latlng);
			m.setTitle(getAddress(latlng));
		}
	}
	
	
	
}
