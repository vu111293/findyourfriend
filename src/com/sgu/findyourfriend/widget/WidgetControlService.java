package com.sgu.findyourfriend.widget;

import java.util.List;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.Config;
import com.sgu.findyourfriend.utils.Controller;
import com.sgu.findyourfriend.utils.GpsPosition;

public class WidgetControlService extends Service {

	public static final String EXTRA_EMERGENCY = "com.sgu.findyourfriend.widget.emergency";

	public static final String ACTION_START_EMERGENCY = "com.sgu.findyourfriend.widget.startemergency";
	public static final String ACTION_START_UPDATE = "com.sgu.findyourfriend.widget.startupdate";

	private static final String APP_TAG = WidgetControlService.class.toString();

	private LocationManager manager = null;
	private LocationListener listener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

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
		
		// if (intent.getAction().equals(Config.DISPLAY_MESSAGE_ACTION)) {
			
			
		//}
		
		waitForGPSCoorinates();
		// AppWidgetManager widgetManager = AppWidgetManager.getInstance(this
		// .getApplicationContext());

		// fetch widgets to be updated
		int[] widgetIds = intent
				.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
		if (widgetIds.length > 0) {
			// fetching timeout, setting status
			for (int widgetId : widgetIds) {
				// RemoteViews remoteViews = new RemoteViews(getPackageName(),
				// R.layout.main_widget_ui);
				if (intent.hasExtra(EXTRA_EMERGENCY)) {
					Log.d(APP_TAG, "intent has extra enableLock");
					if (intent.getBooleanExtra(EXTRA_EMERGENCY, true)) {
						onEmergency(getApplicationContext());
					}
				} else {
					Log.d(APP_TAG, "intent has no extra enableLock");
				}
				// // printStatus(getApplicationContext(), remoteViews);
				// widgetManager.updateAppWidget(widgetId, remoteViews);
				// }
				// stopSelf();
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
		String info = "";

		Log.i(APP_TAG, "update location");

		try {
			addresses = coder.getFromLocation(latitude, longitude, 2);

			if (null != addresses && addresses.size() > 0) {
				int addressCount = addresses.get(0).getMaxAddressLineIndex();

				if (-1 != addressCount) {
					for (int index = 0; index <= addressCount; ++index) {
						info += addresses.get(0).getAddressLine(index);

						if (index < addressCount)
							info += ", ";
					}
				} else {
					info += addresses.get(0).getFeatureName() + ", "
							+ addresses.get(0).getSubAdminArea() + ", "
							+ addresses.get(0).getAdminArea();
				}
			}

			Log.i(APP_TAG, addresses.get(0).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		coder = null;
		addresses = null;

		if (info.length() <= 0) {
			info = "lat " + latitude + ", lon " + longitude;
		} else {
			info += ("\n" + "(lat " + latitude + ", lon " + longitude + ")");
		}

		RemoteViews views = new RemoteViews(getPackageName(),
				R.layout.main_widget_ui);

		views.setTextViewText(R.id.txtMyAddress, info);

		ComponentName thisWidget = new ComponentName(this, SimpleWidget.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		manager.updateAppWidget(thisWidget, views);
	}

//	private void onUpdate(Context context, RemoteViews remoteViews,
//			String mAddress) {
//		String address = (new GpsPosition(context).getMyAddressNow());
//		// String address = (new GpsWidget().getNow());
//		remoteViews.setTextViewText(R.id.txtMyAddress, address);
//		Toast.makeText(context, "Update me " + mAddress, Toast.LENGTH_SHORT)
//				.show();
//	}

	private void onEmergency(final Context context) {
		
		(new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				Toast.makeText(context, "Emergency! sending", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				(new Controller()).sendMessage(context, Config.GCM_ID, Config.GCM_ID, "emergency");
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				Toast.makeText(context, "Emergency! sent!", Toast.LENGTH_SHORT).show();
			}
			
		}).execute();
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
