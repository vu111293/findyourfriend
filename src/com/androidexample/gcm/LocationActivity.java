package com.androidexample.gcm;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.internal.dp;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationActivity extends FragmentActivity {

	private GoogleMap mMap;
	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);

	private LatLng clickLocation;

	private TextView txtLatitute;
	private TextView txtLongtitute;
	private Button btnUpload;

	private Controller aController;
	private AsyncTask<Void, Void, Void> mSendLocationTask;
	private AsyncTask<Void, Void, Void> mLoadLocationTask;
	private String regId;

	private Location lastLocation = null;
	private Timer gpsTimer = new Timer();

	private long lastprovidertimestamp = 0;
	private boolean gps_recorder_running = false;
	private LocationManager myLocationManager = null;

	public static final String TAG = "Gps";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_gps);

		// setup map
		FragmentManager myFM = getSupportFragmentManager();
		SupportMapFragment mySpFrg = (SupportMapFragment) myFM
				.findFragmentById(R.id.mapFragment);

		mMap = mySpFrg.getMap();
		mMap.setMyLocationEnabled(true);
		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		// mySpFrg.getMap().addMarker(options);

		// setup onclick on map
		mMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				txtLatitute.setText(String.valueOf(point.latitude));
				txtLongtitute.setText(String.valueOf(point.longitude));

				Marker m = mMap.addMarker(new MarkerOptions().position(point));

				clickLocation = point;
			}
		});

		setupHistoryPosition();

		// setup view control
		txtLatitute = (TextView) findViewById(R.id.txtLatitute);
		txtLongtitute = (TextView) findViewById(R.id.txtLongtitute);
		btnUpload = (Button) findViewById(R.id.btnUpload);

		btnUpload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (lastLocation != null && regId != null) {

					// send my location
					// setupSendLocation(lastLocation.getLatitude(),
					// lastLocation.getLongitude(), regId);

					// send click location

					if (clickLocation != null)
						setupSendLocation(clickLocation.latitude,
								clickLocation.longitude, regId);
				} else {
					Toast.makeText(getApplicationContext(),
							"Location not avaiable", Toast.LENGTH_LONG).show();
				}

			}
		});

		aController = (Controller) getApplicationContext();

		// Check if Internet present
		if (!aController.isConnectingToInternet()) {

			// Internet Connection is not present
			aController.showAlertDialog(LocationActivity.this,
					"Internet Connection Error",
					"Please connect to Internet connection", false);
			// stop executing code by return
			return;
		}

		regId = GCMRegistrar.getRegistrationId(this);

		lastprovidertimestamp = 0;
		startRecording();

		setupLocationsHistory();
	}

	private List<LatLng> locationsHis = null;

	private void setupLocationsHistory() {
		mLoadLocationTask = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				locationsHis = aController
				 		.getLocationHistory(getApplicationContext());
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mLoadLocationTask = null;

				// draw on map
				for (LatLng p : locationsHis) {
					CircleOptions cirOpt = new CircleOptions()
							.center(p).radius(20)
							.fillColor(0xaaff0000)
							.strokeWidth(0)
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
		 * Marker hamburg = mMap.addMarker( new
		 * MarkerOptions().position(HAMBURG).title("Hamburg")); Marker kiel =
		 * mMap.addMarker( new MarkerOptions().position(KIEL).title("Kiel")
		 * .snippet("Kiel is cool")
		 * .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
		 * 
		 * mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));
		 * 
		 * mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
		 */

		CircleOptions cirOpt = new CircleOptions()
				.center(new LatLng(37.4, 15.6)).radius(100000)
				.strokeColor(Color.RED).strokeWidth(20).visible(true);

		Circle circle = mMap.addCircle(cirOpt);

	}

	private void setupSendLocation(final double latitute,
			final double longtitute, final String regId) {
		final Context context = this;

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
		LocationManager locationManager = (LocationManager) getApplicationContext()
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
								Toast.makeText(getApplicationContext(),
										"Updating location", Toast.LENGTH_SHORT)
										.show();

								doLocationUpdate(location, true);
							}
						}
					});
			// //Toast.makeText(this, "GPS Service STARTED",
			// Toast.LENGTH_LONG).show();
			gps_recorder_running = true;
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
				.getDefaultSharedPreferences(this);
		int checkminutes = 5000;
		try {
			checkminutes = Integer.parseInt(prefs.getString(
					"gps_check_interval", "5000"));
		} catch (NumberFormatException e) {
		}

		return checkminutes;
	}

	private long getMinDistanceFromPrefs() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
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
				Toast.makeText(this, "Current location not available",
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
			txtLatitute.setText("Lat: " + String.valueOf(lat));
			txtLongtitute.setText("Lng: " + String.valueOf(lng));
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
		LocationManager locationManager = (LocationManager) getApplicationContext()
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
		/* locals */
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
			myLocationManager = (LocationManager) getApplicationContext()
					.getSystemService(Context.LOCATION_SERVICE);
		}
		return myLocationManager;
	}

}
