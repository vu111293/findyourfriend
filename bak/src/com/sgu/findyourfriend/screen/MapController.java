package com.sgu.findyourfriend.screen;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.internal.fo;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sgu.findyourfriend.FriendManager;
import com.sgu.findyourfriend.model.History;
import com.sgu.findyourfriend.utils.GpsDirection;

public class MapController {

	private Fragment baseFragment;
	private Context context;
	private GoogleMap mMap;
	private GpsDirection gpsDirection;

	private AsyncTask<Void, Void, Void> mLoadLocationTask;

	private List<History> historyList;
	private List<Marker> hisMarkerList;

	private boolean isRouting;

	public MapController(Context context, Fragment f, GoogleMap map) {
		this.context = context;
		this.baseFragment = f;
		this.mMap = map;

		isRouting = false;

		// get Gps direction after map is setup
		gpsDirection = new GpsDirection(context, mMap);

		// init variable
		historyList = new ArrayList<History>();
		hisMarkerList = new ArrayList<Marker>();
	}

	public void sendMessageTask(int friendId) {
		SendMessageFragment fragment = new SendMessageFragment();

		Bundle bundle = new Bundle();
		bundle.putInt("friendId", friendId);
		fragment.setArguments(bundle);

		((BaseContainerFragment) baseFragment.getParentFragment())
				.replaceFragment(fragment, true);

	}

	public void callTask(int friendId) {
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		callIntent.setData(Uri.parse("tel:"
				+ FriendManager.instance.friends.get(friendId).getUserInfo()
						.getPhoneNumber()));
		context.startActivity(callIntent);
	}

	public void drawHistoryTask(final int friendId) {

		// clear marker before add new
		clearnMarkerHistory();

		mLoadLocationTask = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// locationsHis = aController.getLocationHistory(context);

				// get list latlng from server
				historyList.add(new History(new Timestamp(System
						.currentTimeMillis() - 10000), new LatLng(
						13.073887272186989, 106.1006023606658)));
				historyList.add(new History(new Timestamp(System
						.currentTimeMillis() - 8000), new LatLng(
						13.973887272186989, 106.9006023606658)));
				historyList.add(new History(new Timestamp(System
						.currentTimeMillis() - 6000), new LatLng(
						14.573887272186989, 106.8006023606658)));
				historyList.add(new History(new Timestamp(System
						.currentTimeMillis() - 4000), new LatLng(
						13.273887272186989, 106.5006023606658)));
				historyList.add(new History(new Timestamp(System
						.currentTimeMillis() - 1000), new LatLng(
						13.173887272186989, 107.1006023606658)));
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mLoadLocationTask = null;

				List<LatLng> latlngs = new ArrayList<LatLng>();

				// draw on map
				for (History p : historyList) {
					MarkerOptions opt = new MarkerOptions();
					opt.position(p.getLocation());

					Marker m = mMap.addMarker(opt);
					hisMarkerList.add(m);
					
					latlngs.add(p.getLocation());
				}

				// set bound zoom
				zoomBoundPosition(latlngs);
			}

		};

		// execute AsyncTask
		mLoadLocationTask.execute(null, null, null);
	}

	public void updatePositionTask(int friendId) {

	}

	public void routeTask(int friendId) {
		LatLng dest = FriendManager.instance.friends.get(friendId)
				.getUserInfo().getLastLocation();
		Location myLocation = mMap.getMyLocation();
		LatLng myLatLng = new LatLng(myLocation.getLatitude(),
				myLocation.getLongitude());

		gpsDirection.excuteDirection(myLatLng, dest, true);
		isRouting = true;

		List<LatLng> latlngs = new ArrayList<LatLng>();
		latlngs.add(myLatLng);
		latlngs.add(dest);
		zoomBoundPosition(latlngs);
	}

	public void requestTask(int friendId) {

	}

	private void clearnMarkerHistory() {
		for (Marker m : hisMarkerList) {
			m.remove();
		}
		hisMarkerList.clear();
	}

	private void zoomBoundPosition(List<LatLng> latlngs) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		for (LatLng latlng : latlngs) {
			builder.include(latlng);
		}

		LatLngBounds bounds = builder.build();

		int padding = 100; // offset from edges of the map in pixels
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

		mMap.animateCamera(cu);
	}
	
	public void zoomToPosition(LatLng latLng) {
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 8);
		mMap.animateCamera(cu);
	}
	
	public void zoomToPosition(Location location) {
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(
				new LatLng(location.getLatitude(), location.getLongitude()), 8);
		mMap.animateCamera(cu);
	}
}
