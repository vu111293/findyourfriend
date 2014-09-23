/*
 * 	 This file is part of Find Your Friend.
 *
 *   Find Your Friend is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Find Your Friend is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Find Your Friend.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sgu.findyourfriend.widget;

import java.util.List;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.screen.EmergenceActivity;
import com.sgu.findyourfriend.utils.PreferenceKeys;

public class WidgetControlService extends Service {

	private static final String APP_TAG = WidgetControlService.class.toString();
	private LocationManager manager = null;
	private static String lastAddress = "";

	private LocationListener listener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			Log.i(APP_TAG, "loation changed");
			updateCoordinates(location.getLatitude(), location.getLongitude());
			stopSelf();
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(APP_TAG, "create service");

		manager = (LocationManager) getSystemService(LOCATION_SERVICE);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(APP_TAG, "start!");

		super.onStart(intent, startId);

		waitForGPSCoorinates();

		// fetch widgets to be updated
		int[] widgetIds = intent
				.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
		if (widgetIds.length > 0) {
			for (int widgetId : widgetIds) {
				if (intent.hasExtra(PreferenceKeys.EXTRA_EMERGENCY)) {
					Log.d(APP_TAG, "intent has extra enableLock");
					if (intent.getBooleanExtra(PreferenceKeys.EXTRA_EMERGENCY,
							true)) {
						onEmergency();
					}
				} else {
					Log.d(APP_TAG, "intent has no extra enableLock");
				}
			}
		}
	}

	public void onDestroy() {

		stopListening();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		waitForGPSCoorinates();
		return super.onStartCommand(intent, flags, startId);
	}

	private void stopListening() {
		try {
			if (manager != null && listener != null) {
				manager.removeUpdates(listener);
			}

			manager = null;
		} catch (final Exception ex) {

		}

	}

	private void waitForGPSCoorinates() {
		startListening();
	}

	private void startListening() {
		Log.i(APP_TAG, "start listening");
		final Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		final String bestProvider = manager.getBestProvider(criteria, true);

		if (bestProvider != null && bestProvider.length() > 0) {
			manager.requestLocationUpdates(bestProvider, 500, 10, listener);
		} else {
			final List<String> providers = manager.getProviders(true);

			for (final String provider : providers) {
				manager.requestLocationUpdates(provider, 500, 10, listener);
			}
		}
	}

	protected void updateCoordinates(double latitude, double longitude) {

		Geocoder coder = new Geocoder(this);
		List<Address> addresses = null;
		StringBuilder address = new StringBuilder();
		String latlng = "";

		Log.i(APP_TAG, "update location");

		try {
			addresses = coder.getFromLocation(latitude, longitude, 2);

			if (null != addresses && addresses.size() > 0) {
				int addressCount = addresses.get(0).getMaxAddressLineIndex();

				if (-1 != addressCount) {
					for (int index = 0; index <= addressCount; ++index) {
						address.append(addresses.get(0).getAddressLine(index));

						if (index < addressCount)
							address.append(", ");
					}
				} else {
					address.append(addresses.get(0).getFeatureName());
					address.append(", ");
					address.append(addresses.get(0).getSubAdminArea());
					address.append(", ");
					address.append(addresses.get(0).getAdminArea());
				}
			}

			Log.i(APP_TAG, addresses.get(0).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		coder = null;
		addresses = null;

		latlng = "lat " + latitude + ", lng " + longitude;

		RemoteViews views = new RemoteViews(getPackageName(),
				R.layout.main_widget_ui);

		views.setTextViewText(
				R.id.txtMyAddress,
				address.toString().equals("") ? "không có sẵn" : address
						.toString());
		views.setTextViewText(R.id.txtLatLng, latlng);

		lastAddress = address.toString() + "\n" + latlng;

		ComponentName thisWidget = new ComponentName(this, SimpleWidget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		manager.updateAppWidget(thisWidget, views);
	}

	private void onEmergency() {
		Intent i = new Intent(getApplicationContext(), EmergenceActivity.class);
		i.putExtra("latlng", lastAddress);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplicationContext().startActivity(i);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
