package com.sgu.findyourfriend.screen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.devsmart.android.ui.HorizontalListView;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sgu.findyourfriend.Controller;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.gps.DirectionsJSONParser;
import com.sgu.findyourfriend.gps.SwipeAdapter;
import com.sgu.findyourfriend.model.Friend;

public class MapFragment extends Fragment {

	private Context context;
	private Activity activity;

	// view replace
	private static View view;
	private ListView maskView;
	
	private GoogleMap mMap;
	private ArrayList<LatLng> markerPoints;

	private FragmentActivity myContext;
	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);

	private LatLng clickLocation;

	// private TextView txtLatitute;
	// private TextView txtLongtitute;
	private Button btnUpload;

	private HorizontalScrollView controlLayout;
	private HorizontalListView avatarListView;
	private SwipeAdapter swipeAdapter;

	// state of controller
	private Boolean isOpenCrt = false;

	// friend's id current
	private int idFriendCur = -1;

	private Controller aController;
	private AsyncTask<Void, Void, Void> mSendLocationTask;
	private AsyncTask<Void, Void, Void> mLoadLocationTask;
	private String regId;

	private Location lastLocation = null;
	private Timer gpsTimer = new Timer();

	private long lastprovidertimestamp = 0;
	// private boolean gps_recorder_running = false;
	private LocationManager myLocationManager = null;
	private List<LatLng> locationsHis = null;

	private View viewSelected = null;
	
	// control button
	private Button btnMessage;
	private Button btnCall;
	private Button btnHistory;
	private Button btnUpdate;
	private Button btnRoute;
	private Button btnRequest;
	

	public static final String TAG = "Gps";

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

		// View rootView = inflater.inflate(R.layout.fragment_map, container,
		// false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// initialize
		markerPoints = new ArrayList<LatLng>();
		
		// setup control view
		btnMessage = (Button) view.findViewById(R.id.btnMessage);
		btnCall = (Button) view.findViewById(R.id.btnCall);
		btnHistory = (Button) view.findViewById(R.id.btnHistory);
		btnUpdate = (Button) view.findViewById(R.id.btnUpdate);
		btnRoute = (Button) view.findViewById(R.id.btnRoute);
		btnRequest = (Button) view.findViewById(R.id.btnRequest);

		btnMessage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// onMessageExcute(userIndex);
				
			}
		});
		
		btnCall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// onCallExcute(userIndex);
			}
		});
		
		btnHistory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// onHistoryExcute(userIndex);
			}
		});

		btnUpdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		btnRoute.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		btnRequest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		// setup control layout and avatar list view
		controlLayout = (HorizontalScrollView) view.findViewById(R.id.controlLayout);
		
		avatarListView = (HorizontalListView) view
				.findViewById(R.id.avatarListView);
		swipeAdapter = new SwipeAdapter(context);

		// set adapter for friend list
		Friend f1 = new Friend("Hoàng", "hoang@gmail.com", "01223457841", "", "545645464", true);
		f1.setAccepted(true);
		Friend f2 = new Friend("Minh", "hoang@gmail.com", "01223457841", "", "545645464", true);
		f2.setAccepted(false);
		Friend f3 = new Friend("Thanh", "hoang@gmail.com", "01223457841", "", "545645464", true);
		f3.setAccepted(true);
		
		swipeAdapter.addItem(f1);
		swipeAdapter.addItem(f2);
		swipeAdapter.addItem(f3);

		avatarListView.setAdapter(swipeAdapter);

		avatarListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.i(TAG, "click at " + position);

				if (viewSelected != null) {
					if (!viewSelected.equals(view)) {
						viewSelected.setBackgroundColor(0x00);
						view.setBackgroundColor(0xff086EBC);
						// view.setBackgroundColor(Color.RED);
					}
				} else {
					view.setBackgroundColor(0xff086EBC);
					// view.setBackgroundColor(Color.RED);
				}

				viewSelected = view;

				if (idFriendCur == position) {
					if (!isOpenCrt) {
						// open
						showControl();
						isOpenCrt = true;
					} else {
						// close
						hideControl();
						isOpenCrt = false;
						viewSelected.setBackgroundColor(0x00);
					}
				} else {
					// open
					showControl();
					isOpenCrt = true;
					idFriendCur = position;
				}
			}
		});

		// setup map
		FragmentManager myFM = myContext.getSupportFragmentManager();
		SupportMapFragment mySpFrg = (SupportMapFragment) myFM
				.findFragmentById(R.id.mapFragment);

		mMap = mySpFrg.getMap();
		mMap.setMyLocationEnabled(true);
		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		mMap.getUiSettings().setZoomControlsEnabled(false);

		final LatLng NEWARK = new LatLng(40.714086, -74.228697);

		BitmapDescriptor image = BitmapDescriptorFactory
				.fromResource(R.drawable.ic_launcher);
		GroundOverlayOptions groundOverlay = new GroundOverlayOptions()
				.image(image).position(NEWARK, 500f).transparency(0.5f);
		mMap.addGroundOverlay(groundOverlay);

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
			public void onInfoWindowClick(Marker marker) {
				// TODO Auto-generated method stub

			}
		});

		// setup onclick on map
		mMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				/*
				 * mMap.clear();
				 * 
				 * txtLatitute.setText(String.valueOf(point.latitude));
				 * txtLongtitute.setText(String.valueOf(point.longitude));
				 * 
				 * MarkerOptions markerOptions = new MarkerOptions();
				 * 
				 * markerOptions.position(point);
				 * 
				 * mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
				 * 
				 * Marker marker = mMap.addMarker(markerOptions);
				 * 
				 * marker.showInfoWindow();
				 */

				// Marker m = mMap.addMarker(new
				// MarkerOptions().position(point));

				/*
				 * mMap.clear(); MarkerOptions mOptions = new MarkerOptions();
				 * mOptions.position(point); mOptions.title(getAddress(point));
				 * mOptions.snippet("update: " + (new
				 * Date(lastLocation.getTime())).toLocaleString() + " - acc: " +
				 * lastLocation.getAccuracy() + " km");
				 * 
				 * mMap.addMarker(mOptions);
				 */
				clickLocation = point;

				// mMap.clear();
				// mMap.addMarker((new MarkerOptions()).position(NEWARK));

				// on direct
				onDirect(point);
			}
		});

		setupHistoryPosition();

		aController = (Controller) context;

		// Check if Internet present
		if (!aController.isConnectingToInternet()) {

			// Internet Connection is not present
			aController.showAlertDialog(context, "Internet Connection Error",
					"Please connect to Internet connection", false);
			// stop executing code by return
			return;
		}

		regId = GCMRegistrar.getRegistrationId(context);

		lastprovidertimestamp = 0;
		startRecording();

		setupLocationsHistory();
		
		
		// removeMaskView();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity.getApplicationContext();
		myContext = (FragmentActivity) activity;
		this.activity = activity;
	}

	private void showControl() {
		// addMaskView();
		controlLayout.setAlpha(1.0f);
		ValueAnimator anim = ObjectAnimator.ofFloat(controlLayout,
				"translationY", -controlLayout.getHeight());
		anim.setDuration(500);
		anim.start();
	}

	private void hideControl() {
		ValueAnimator anim = ObjectAnimator.ofFloat(controlLayout,
		 "translationY", controlLayout.getHeight());
		anim.setDuration(500);
		anim.start();
	}

	private void onDirect(LatLng dest) {
		mMap.clear();
		Location myLocation = mMap.getMyLocation();
		LatLng myLatLng = new LatLng(myLocation.getLatitude(),
				myLocation.getLongitude());

		markerPoints.add(myLatLng);
		markerPoints.add(dest);

		MarkerOptions startOptions = new MarkerOptions();
		startOptions.position(myLatLng);
		startOptions.title(getAddress(myLatLng));
		mMap.addMarker(startOptions);

		// Creating MarkerOptions
		MarkerOptions destOptions = new MarkerOptions();

		// Setting the position of the marker
		destOptions.position(dest);
		destOptions.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		destOptions.title(getAddress(dest));

		// Add new marker to the Google Map Android API V2
		mMap.addMarker(destOptions);

		// Getting URL to the Google Directions API
		String url = getDirectionsUrl(myLatLng, dest);

		Log.i("URL Direct", url);

		DownloadTask downloadTask = new DownloadTask();

		// Start downloading json data from Google Directions API
		downloadTask.execute(url);
	}

	private void setupLocationsHistory() {
		mLoadLocationTask = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				locationsHis = aController.getLocationHistory(context);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mLoadLocationTask = null;

				// draw on map
				for (LatLng p : locationsHis) {
					CircleOptions cirOpt = new CircleOptions().center(p)
							.radius(20).fillColor(0xaaff0000).strokeWidth(0)
							.visible(true);

					mMap.addCircle(cirOpt);

					mMap.addMarker(new MarkerOptions().position(p));
				}
			}

		};

		// execute AsyncTask
		mLoadLocationTask.execute(null, null, null);
	}

	private void setupHistoryPosition() {
		/*
		 * CircleOptions cirOpt = new CircleOptions() .center(new LatLng(37.4,
		 * 15.6)).radius(100000)
		 * .strokeColor(Color.RED).strokeWidth(20).visible(true);
		 * 
		 * Circle circle = mMap.addCircle(cirOpt);
		 */
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

	public void startRecording() {
		gpsTimer.cancel();
		gpsTimer = new Timer();
		long checkInterval = getGPSCheckMillisFromPrefs();
		long minDistance = getMinDistanceFromPrefs();
		// receive updates
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		for (String s : locationManager.getAllProviders()) {
			locationManager.requestLocationUpdates(s, checkInterval,
					minDistance, new LocationListener() {

						@Override
						public void onStatusChanged(String provider,
								int status, Bundle extras) {
						}

						@Override
						public void onProviderEnabled(String provider) {
						}

						@Override
						public void onProviderDisabled(String provider) {
						}

						@Override
						public void onLocationChanged(Location location) {
							// if this is a gps location, we can use it
							if (location.getProvider().equals(
									LocationManager.GPS_PROVIDER)) {
								Toast.makeText(context, "Updating location",
										Toast.LENGTH_SHORT).show();

								doLocationUpdate(location, true);
							}
						}
					});

			// //Toast.makeText(this, "GPS Service STARTED",
			// Toast.LENGTH_LONG).show();
			// gps_recorder_running = true;
		}
		// start the gps receiver thread
		gpsTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				Location location = getBestLocation();
				doLocationUpdate(location, false);
			}
		}, 0, checkInterval);
	}

	private long getGPSCheckMillisFromPrefs() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		int checkminutes = 5000;
		try {
			checkminutes = Integer.parseInt(prefs.getString(
					"gps_check_interval", "30000"));
		} catch (NumberFormatException e) {
		}

		return checkminutes;
	}

	private long getMinDistanceFromPrefs() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		int minDist = 1000;
		try {
			minDist = Integer.parseInt(prefs.getString("gps_min_distance",
					"1000"));
		} catch (NumberFormatException e) {
		}
		return minDist;
	}

	public void doLocationUpdate(Location l, boolean force) {
		long minDistance = getMinDistanceFromPrefs();
		Log.d(TAG, "update received:" + l);
		if (l == null) {
			Log.d(TAG, "Empty location");
			if (force)
				Toast.makeText(context, "Current location not available",
						Toast.LENGTH_SHORT).show();
			return;
		}
		if (lastLocation != null) {
			float distance = l.distanceTo(lastLocation);
			Log.d(TAG, "Distance to last: " + distance);
			if (l.distanceTo(lastLocation) < minDistance && !force) {
				Log.d(TAG, "Position didn't change");
				return;
			}
			if (l.getAccuracy() >= lastLocation.getAccuracy()
					&& l.distanceTo(lastLocation) < l.getAccuracy() && !force) {
				Log.d(TAG, "Accuracy got worse and we are still "
						+ "within the accuracy range.. Not updating");
				return;
			}
			if (l.getTime() <= lastprovidertimestamp && !force) {
				Log.d(TAG, "Timestamp not never than last");
				return;
			}
		}
		// upload/store your location here

		lastLocation = l;

		if (lastLocation != null) {

			double lat = lastLocation.getLatitude();
			double lng = lastLocation.getLongitude();

			// show lat and lng on view
			// txtLatitute.setText("Lat: " + String.valueOf(lat));
			// txtLongtitute.setText("Lng: " + String.valueOf(lng));
		}
	}

	private Location getBestLocation() {
		Location gpslocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
		Location networkLocation = getLocationByProvider(LocationManager.NETWORK_PROVIDER);
		// if we have only one location available, the choice is easy
		if (gpslocation == null) {
			Log.d(TAG, "No GPS Location available.");
			return networkLocation;
		}
		if (networkLocation == null) {
			Log.d(TAG, "No Network Location available");
			return gpslocation;
		}
		// a locationupdate is considered 'old' if its older than the configured
		// update interval. this means, we didn't get a
		// update from this provider since the last check
		long old = System.currentTimeMillis() - getGPSCheckMillisFromPrefs();
		boolean gpsIsOld = (gpslocation.getTime() < old);
		boolean networkIsOld = (networkLocation.getTime() < old);
		// gps is current and available, gps is better than network
		if (!gpsIsOld) {
			Log.d(TAG, "Returning current GPS Location");
			return gpslocation;
		}
		// gps is old, we can't trust it. use network location
		if (!networkIsOld) {
			Log.d(TAG, "GPS is old, Network is current, returning network");
			return networkLocation;
		}
		// both are old return the newer of those two
		if (gpslocation.getTime() > networkLocation.getTime()) {
			Log.d(TAG, "Both are old, returning gps(newer)");
			return gpslocation;
		} else {
			Log.d(TAG, "Both are old, returning network(newer)");
			return networkLocation;
		}
	}

	private Location getLocationByProvider(String provider) {
		Location location = null;
		if (!isProviderSupported(provider)) {
			return null;
		}
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		try {
			if (locationManager.isProviderEnabled(provider)) {
				location = locationManager.getLastKnownLocation(provider);
			}
		} catch (IllegalArgumentException e) {
			Log.d(TAG, "Cannot acces Provider " + provider);
		}
		return location;
	}

	public boolean isProviderSupported(String in_Provider) {
		LocationManager locationManager = getLocationManager();
		int lv_N;
		List lv_List;

		// isProviderEnabled should throw a IllegalArgumentException if
		// provider is not
		// supported
		// But in sdk 1.1 the exception is catched by isProviderEnabled itself.
		// Therefore check out the list of providers instead (which indeed does
		// not
		// report a provider it does not exist in the device) Undocumented is
		// that
		// this call can throw a SecurityException
		try {
			lv_List = locationManager.getAllProviders();
		} catch (Throwable e) {
			return false;
		}

		// scan the list for the specified provider
		for (lv_N = 0; lv_N < lv_List.size(); ++lv_N)
			if (in_Provider.equals((String) lv_List.get(lv_N)))
				return true;

		// not supported
		return false;
	}

	public LocationManager getLocationManager() {
		if (myLocationManager == null) {
			myLocationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
		}
		return myLocationManager;
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

	// utils class
	// ---------------------------------------------------------------------

	private String getDirectionsUrl(LatLng origin, LatLng dest) {

		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String> {

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);

		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			// MarkerOptions markerOptions = new MarkerOptions();
			String distance = "";
			String duration = "";
			String startAddress = "";
			String endAddress = "";

			if (result.size() < 1) {
				Toast.makeText(context, "No Points", Toast.LENGTH_SHORT).show();
				return;
			}

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					if (j == 0) { // Get distance from the list
						distance = (String) point.get("distance");
						continue;
					} else if (j == 1) { // Get duration from the list
						duration = (String) point.get("duration");
						continue;
					} else if (j == 2) {
						startAddress = (String) point.get("startAddress");
						endAddress = (String) point.get("endAddress");
						continue;
					}

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(2);
				lineOptions.color(Color.RED);

			}

			// tvDistanceDuration.setText("Distance:" + distance + ", Duration:"
			// + duration);

			Log.i("DIRECT INFO: ", "Distance:" + distance + ", Duration:"
					+ duration);
			Log.i("ADDRESS INFO: ", "Start:" + startAddress + ", End:"
					+ endAddress);

			// Drawing polyline in the Google Map for the i-th route
			mMap.addPolyline(lineOptions);
		}
	}
}
