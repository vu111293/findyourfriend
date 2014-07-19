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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
import android.widget.ProgressBar;

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
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.adapter.FriendSwipeAdapter;
import com.sgu.findyourfriend.ctr.ControlOptions;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.mgr.MyProfileManager;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.utils.Controller;
import com.sgu.findyourfriend.utils.GpsPosition;
import com.sgu.findyourfriend.utils.Utility;

public class MapFragment extends Fragment {

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
	private int idFriendCur = -1;

	private Controller aController;

	// GPS Controller
	GpsPosition gpsPosition;

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

	private boolean isMapStarted = false;

	// state marker hide/show
	private boolean isShowMarker;

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
		// init
		friendManager = FriendManager.getInstance();
		isMapStarted = false;
		isShowMarker = true;

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

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity.getApplicationContext();
		myContext = (FragmentActivity) activity;
	}

	// ----------------- init variable and setup methods ----------- //
	private void afterViewCreate(View view) {
		// set actionbar
		View bar = getActivity().getActionBar().getCustomView();
		bar.findViewById(R.id.grpItemControl).setVisibility(View.VISIBLE);
		bar.findViewById(R.id.txtSend).setVisibility(View.GONE);

		// get Gps position
		gpsPosition = new GpsPosition(context);

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

		// get progress bar
		pbOnMap = (ProgressBar) view.findViewById(R.id.pbOnMap);
		pbOnMap.setVisibility(View.GONE);

		// setup control layout and avatar list view
		inc = view.findViewById(R.id.inc_horixontal_container);

		// setup friend list
		setupFriendList();

		// setup send location to server
		gpsPosition.startRecording();

		// setup map
		setupMap();

		// init + setup mapController
		mapController = new MapController(this.getParentFragment()
				.getActivity(), this, mMap, pbOnMap);
		setupControlView();

		// load friends position after 1 mins
		loadFriendsPosition(60000);
		updateFriendsPosition(60000);

		// generateLocation(NEWARK);

		// test friend list
		for (Friend f : friendManager.friends) {
			Log.i(TAG, f.getUserInfo().getName());
		}

		// check require from sliding friend event
		if (ControlOptions.getInstance().isRequire()) {
			Log.i("REQUIRE", "####################################");
			int fId = Integer.parseInt(ControlOptions.getInstance().getHashMap(
					"friendId"));
			mapController.zoomToPosition(friendManager.getInstance().friends
					.get(fId).getLastLocation());
			ControlOptions.getInstance().finish();
		}

		// check bundle from detail info fragment
		Bundle bundle = getArguments();

		if (null != bundle) {
			idFriendCur = bundle.getInt("friendId");
			String task = bundle.getString("task");

			if (task.equals("route")) {
				hideControl();
				hideMarker(friendManager.getInstance().friends.get(0),
						friendManager.getInstance().friends
								.get(idFriendCur));
				mapController.routeTask(idFriendCur);
			} else if (task.equals("history")) {
				hideControl();
				hideMarker();
				mapController.drawHistoryTask(idFriendCur);
			}
		}
	}

	// ---------------- setup map ----------------------- //
	@SuppressWarnings("deprecation")
	private void setupMap() {
		FragmentManager myFM = myContext.getSupportFragmentManager();
		SupportMapFragment mySpFrg = (SupportMapFragment) myFM
				.findFragmentById(R.id.mapFragment);

		mMap = mySpFrg.getMap();
		mMap.setMyLocationEnabled(true);

		// load setup map from setting activity
		mMap.setMapType(SettingManager.getInstance().getMapType());

		mMap.getUiSettings().setZoomControlsEnabled(false);

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
								mapController.getAddress(marker.getPosition()));
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
				Log.i(TAG, "update! " + markerManager.size());
				Friend f = friendManager.friends.get(0);
				LatLng latlng = new LatLng(location.getLatitude(), location
						.getLongitude());
				MyProfileManager.getInstance().myLocation = latlng;
				f.setLastLocation(latlng);

				updatePositionOf(f);

				if (!isMapStarted) {
					mapController.zoomToPosition(location);
					isMapStarted = true;
				}
			}
		});

		// setup image loader
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		imageLoader = ImageLoader.getInstance();
		// File cacheDir = StorageUtils.getCacheDirectory(context);
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

	// ---------------- setup friend list ------------------ //
	private void setupFriendList() {
		avatarListView = (HorizontalListView) view
				.findViewById(R.id.avatarListView);

		swipeAdapter = new FriendSwipeAdapter(context,
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

						mapController.zoomToPosition(friendManager.friends.get(
								position).getLastLocation());
					}
				} else {
					showControl();
					view.setBackgroundColor(resource
							.getColor(R.color.selector_color));

					mapController.zoomToPosition(friendManager.friends.get(
							position).getLastLocation());
				}

				viewSelected = view;

			}
		});
	}

	
	// ----------------- setup controll at bottom screen ----------- //
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
					hideMarker();
					mapController.drawHistoryTask(idFriendCur);
				}
			}
		});

		btnUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (maskEn[3] == 1) {
					hideControl();
					showMarker();
					mapController.updatePositionTask(idFriendCur);
				}
			}
		});

		btnRoute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (maskEn[4] == 1) {
					hideControl();
					hideMarker(friendManager.getInstance().friends.get(0),
							friendManager.getInstance().friends
									.get(idFriendCur));
					mapController.routeTask(idFriendCur);
				}
			}
		});

		btnRequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (maskEn[5] == 1) {
					hideControl();
					showMarker();
					mapController.requestTask(idFriendCur);
				}
			}
		});
	}

	
	/// --------------------- utilities methods --------------------- ///
	
	// ---------- hide/show marker on map ----------------- //
	private void hideMarker() {
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

	// ------------------ hide/show view on control console --------------------- //
	private void disableView(int idControlView) {
		((LinearLayout) ((ViewGroup) getView().findViewById(idControlView)
				.getParent())).setBackgroundColor(Color.GRAY);

	}

	private void enableView(int idControlView) {
		((LinearLayout) ((ViewGroup) getView().findViewById(idControlView)
				.getParent())).setBackgroundColor(Color.TRANSPARENT);

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

	// --------------------- changle color marker --------------------- //
	private void changleSelectMarker(int idFriendCur, boolean isSelected) {
		Marker m = markerManager.get(friendManager.friends.get(idFriendCur)
				.getUserInfo().getId());
		Friend f = friendManager.friends.get(idFriendCur);

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

	//  -------------------- should load with timer --------------- //
	private void loadFriendsPosition(final long interval) {
		mMap.clear();

		// update for friend
		for (int i = 0; i < friendManager.friends.size(); ++i) {
			Friend f = friendManager.friends.get(i);

			if (f.getLastLocation() == null)
				continue;

			createMakerOnMap(f);
		}
	}

	// -------------------------- update position ------------------------ //
	
	private void updateFriendsPosition(final long interval) {

//		for (int key : markerManager.keySet()) {
//			// PostData.updateLastLoation(friendManager.hmFriends.get(key),
//			// markerManager.get(key));
//		}

	}

	private void updatePositionOf(Friend f) {
		Marker m = markerManager.get(f.getUserInfo().getId());
		if (m != null) {
			m.setPosition(f.getLastLocation());
			m.setTitle(mapController.getAddress(f.getLastLocation()));
		}
	}
	
	//  ----------------------- setup iconview on map ---------------- //
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
				R.drawable.ic_no_imgprofile), brbitmap);
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


	// ----------------------- create map's marker ----------------- //
	private void createMakerOnMap(Friend f) {
		MarkerOptions options = new MarkerOptions();
		options.position(f.getLastLocation());
		options.title(mapController.getAddress(f.getLastLocation()));
		options.snippet("cập nhật "
				+ Utility.convertMicTimeToString(System.currentTimeMillis()
						- f.getUserInfo().getLastestlogin().getTime())
				+ "trước - phạm vi sai lệch " + f.getAccurency() + "m.");

		options.icon(combileLocationIcon(f, false));
		Marker m = mMap.addMarker(options);

		markerManager.put(f.getUserInfo().getId(), m);
	}

	

}
