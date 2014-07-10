package com.sgu.findyourfriend.utils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class GpsPosition {
	
	public static final String TAG = "GPS POSITION";
	private Context context;
	private Location lastLocation = null;
	private Timer gpsTimer = new Timer();
	private long lastprovidertimestamp = 0;
	private LocationManager myLocationManager = null;
	
	public GpsPosition(Context conext) {
		this.context = conext;
		lastprovidertimestamp = 0;
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
		if (getLastLocation() != null) {
			float distance = l.distanceTo(getLastLocation());
			Log.d(TAG, "Distance to last: " + distance);
			if (l.distanceTo(getLastLocation()) < minDistance && !force) {
				Log.d(TAG, "Position didn't change");
				return;
			}
			if (l.getAccuracy() >= getLastLocation().getAccuracy()
					&& l.distanceTo(getLastLocation()) < l.getAccuracy() && !force) {
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

		setLastLocation(l);

		if (getLastLocation() != null) {

			double lat = getLastLocation().getLatitude();
			double lng = getLastLocation().getLongitude();

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

	public Location getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

}
