package com.sgu.findyourfriend.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.mgr.SettingManager;
import com.sgu.findyourfriend.net.PostData;

public class ServiceUpdatePosition extends Service {

	private static String TAG = ServiceUpdatePosition.class.toString();
	private WakeLock mWakeLock;
	private static boolean isStop = false;

	private static Location beforeLocation = null;
	private static Location lastLocation = null;
	private LocationManager manager = null;
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
			lastLocation = location;
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private final Handler handler = new Handler();
	private final Runnable refresher = new Runnable() {
		public void run() {
			new PollTask().execute();

			if (!isStop)
				handler.postDelayed(refresher, 600000);
		}
	};

	private class PollTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Log.i("Service", "backgorund");

			if (null != lastLocation) {
				if (null == beforeLocation
						|| !beforeLocation.equals(lastLocation)) {
					beforeLocation = lastLocation;

					if (SettingManager.getInstance().getLastAccountIdLogin() > 0) {
						Log.i(TAG, "send location");
						PostData.historyCreate(getApplicationContext(),
								SettingManager.getInstance()
										.getLastAccountIdLogin(), new LatLng(
										lastLocation.getLatitude(),
										lastLocation.getLongitude()));
					}
				}

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

		}

	}

	@Override
	public void onCreate() {
		super.onCreate();
		SettingManager.getInstance().init(getApplicationContext());
		manager = (LocationManager) getSystemService(LOCATION_SERVICE);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// handleIntent(intent);

		if (intent.hasExtra("isStop")) {
			if (intent.getBooleanExtra("isStop", true)) {
				isStop = true;
			} else {
				isStop = false;
				handler.removeMessages(Config.MESSAGE_SERVICE);
			}

			if (!handler.hasMessages(Config.MESSAGE_SERVICE)) {
				handler.sendEmptyMessage(Config.MESSAGE_SERVICE);
				handler.post(refresher);
			}
		} else {
			// boot
			isStop = SettingManager.getInstance().isUploadMyPosition();
			handler.sendEmptyMessage(Config.MESSAGE_SERVICE);
			handler.post(refresher);
		}

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		waitForGPSCoorinates();
		if (intent.hasExtra("isStop")) {
			if (intent.getBooleanExtra("isStop", true)) {
				isStop = true;
			} else {
				isStop = false;
				handler.removeMessages(Config.MESSAGE_SERVICE);
			}

			if (!handler.hasMessages(Config.MESSAGE_SERVICE)) {
				handler.sendEmptyMessage(Config.MESSAGE_SERVICE);
				handler.post(refresher);
			}
		} else {
			// boot
			isStop = SettingManager.getInstance().isUploadMyPosition();
			handler.sendEmptyMessage(Config.MESSAGE_SERVICE);
			handler.post(refresher);

		}
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mWakeLock.release();
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
		// Log.i(APP_TAG, "start listening");
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

}
