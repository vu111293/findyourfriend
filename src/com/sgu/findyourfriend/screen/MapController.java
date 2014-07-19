package com.sgu.findyourfriend.screen;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.mgr.FriendManager;
import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.History;
import com.sgu.findyourfriend.net.PostData;
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

	private ProgressBar pbOnMap;

	public MapController(Context context, Fragment f, GoogleMap map,
			ProgressBar pbOnMap) {
		this.context = context;
		this.baseFragment = f;
		this.mMap = map;
		this.pbOnMap = pbOnMap;

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

		ArrayList<String> phs = FriendManager.getInstance().friends.get(
				friendId).getNumberLogin();

		if (phs.size() == 0) {
			Toast.makeText(context, "Not phone number", Toast.LENGTH_LONG)
					.show();
			return;
		}

		final String[] phonenumbers = new String[phs.size()];

		for (int i = 0; i < phs.size(); ++i) {
			phonenumbers[i] = phs.get(i);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Chọn số cần gọi:");
		builder.setItems(phonenumbers, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				callIntent.setData(Uri.parse("tel:" + phonenumbers[which]));
				context.startActivity(callIntent);
			}
		}).setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});

		builder.show();
	}

	public void drawHistoryTask(final int friendId) {
		// clear route
		gpsDirection.clearRoute();

		// clear marker before add new
		clearnMarkerHistory();

		mLoadLocationTask = new AsyncTask<Void, Void, Void>() {
			Friend f;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pbOnMap.setVisibility(View.VISIBLE);
			}

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

				f = FriendManager.getInstance().friends.get(friendId);
				if (null == f.getSteps())
					f.setSteps(PostData.historyGetUserHistory(context, f
							.getUserInfo().getId()));

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mLoadLocationTask = null;

				List<LatLng> latlngs = new ArrayList<LatLng>();

				// draw on map
				int sn = f.getSteps().size();
				while (sn > 0) {
					History hp = f.getSteps().get(sn - 1);
					Log.i("LOC",
							hp.getLocation().latitude + " # "
									+ hp.getLocation().longitude);

					createHistoryPoint(hp, sn);
					latlngs.add(hp.getLocation());
					sn--;
				}

				// hide progress bar
				pbOnMap.setVisibility(View.GONE);

				// set bound zoom
				zoomBoundPosition(latlngs);
			}
		};

		// execute AsyncTask
		mLoadLocationTask.execute(null, null, null);
	}

	public void updatePositionTask(int friendId) {
		clearnMarkerHistory();
		gpsDirection.clearRoute();

	}

	public void routeTask(int friendId) {
		// clear marker history before
		clearnMarkerHistory();

		LatLng dest = FriendManager.getInstance().friends.get(friendId)
				.getLastLocation();
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

	// --------------- utilities methods -------------------- //

	private void createHistoryPoint(History hisP, int prio) {
		MarkerOptions opt = new MarkerOptions();
		opt.position(hisP.getLocation());
		opt.title(getAddress(hisP.getLocation()));
		opt.snippet(hisP.getTimest().toGMTString());
		opt.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(
				R.drawable.ic_position_history, prio + "")));
		Marker m = mMap.addMarker(opt);
		opt.title(hisP.getTimest().toGMTString());
		hisMarkerList.add(m);
	}
	
	
	private void clearnMarkerHistory() {
		for (Marker m : hisMarkerList) {
			m.remove();
		}
		hisMarkerList.clear();
	}

	// ------------------- zoom control ----------------------- //

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

	// ---------------bitmap untilities ------------------------ //

	private Bitmap writeTextOnDrawable(int drawableId, String text) {

		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				drawableId).copy(Bitmap.Config.ARGB_8888, true);

		Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setTypeface(tf);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(convertToPixels(context, 18));

		Rect textRect = new Rect();
		paint.getTextBounds(text, 0, text.length(), textRect);

		Canvas canvas = new Canvas(bm);

		// If the text is bigger than the canvas , reduce the font size
		if (textRect.width() >= (canvas.getWidth() - 4))
			paint.setTextSize(convertToPixels(context, 7));

		// Calculate the positions
		int xPos = (canvas.getWidth() / 2) - 2;

		// baseline to the center.
		int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint
				.ascent()) / 2));

		canvas.drawText(text, xPos, yPos, paint);

		return bm;
	}

	public static int convertToPixels(Context context, int nDP) {
		final float conversionScale = context.getResources()
				.getDisplayMetrics().density;

		return (int) ((nDP * conversionScale) + 0.5f);

	}

	// --------------- getaddress utilities ---------------------- //
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
