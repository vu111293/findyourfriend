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
package com.sgu.findyourfriend.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class GpsDirection {

	private Context context;
	private GoogleMap mMap;
	private Polyline polylineCurrent;
	private boolean isBeforeRemove;

	private boolean isRouted;
	private String mdurationsMode[] = new String[3];
	private String mdistance;

	private PolylineOptions polylineOptions;

	private TextView txtDistance;
	private TextView txtWalk;
	private TextView txtMoto;

	private boolean isDraw; // different for get information screen and route
							// task

	// amazing for route detection
	private boolean stopAmazing;
	private int ir = 0;

	public enum MODE {
		driving, walking
	};

	public GpsDirection(Context context, GoogleMap mMap) {
		this.context = context;
		this.mMap = mMap;
	}

	public GpsDirection(Context context) {
		this.context = context;
		this.mMap = null;
	}

	public void excuteDirection(LatLng origin, LatLng dest,
			boolean isBeforeRemove) {
		this.isBeforeRemove = isBeforeRemove;
		if (isBeforeRemove && polylineCurrent != null)
			clearRoute();

		isDraw = true;

		String url = getDirectionsUrl(origin, dest);
		DownloadTask downloadTask = new DownloadTask(MODE.driving);

		// Start downloading json data from Google Directions API
		downloadTask.execute(url);

		// start amazing
		amazingRoute(dest);
	}

	public void clearRoute() {
		if (null != polylineCurrent)
			polylineCurrent.remove();

	}

	public void loadViewDirectionInfo(TextView txtDistance, TextView txtWalk,
			TextView txtMoto, LatLng origin, LatLng dest) {

		isDraw = false;

		this.txtDistance = txtDistance;
		this.txtWalk = txtWalk;
		this.txtMoto = txtMoto;

		// prepare
		this.txtDistance.setText("...");
		this.txtWalk.setText("...");
		this.txtMoto.setText("...");

		for (MODE m : MODE.values()) {
			String url = getDirectionsUrl(origin, dest, m.toString());
			DownloadTask downloadTask = new DownloadTask(m);
			downloadTask.execute(url);
		}
	}

	public String getDirectionsUrl(LatLng origin, LatLng dest) {
		return getDirectionsUrl(origin, dest, "");
	}

	public String getDirectionsUrl(LatLng origin, LatLng dest, String fmode) {

		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		String mode = "mode=" + fmode;

		String unit = "units=metric"; // &mode=driving

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor + "&"
				+ unit + "&" + mode;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		Log.i("DIRECTION: ", url);

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

		MODE mode;

		public DownloadTask(MODE m) {
			this.mode = m;
		}

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

			ParserTask parserTask = new ParserTask(mode);

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);

		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		MODE mode;

		public ParserTask(MODE m) {
			this.mode = m;
		}

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
			String travelMode = "";

			if (null == result || result.size() < 1) {
				if (!isDraw) {
					if (mode.equals(MODE.walking))
						txtWalk.setText("không có sẵn");
					else if (mode.equals(MODE.driving)) {
						txtMoto.setText("không có sẵn");
						txtDistance.setText("không xác định");
					}
				} else {
					stopAmazing = true;
					Toast.makeText(context, "Không tìm thấy đường đi", Toast.LENGTH_SHORT).show();
				}
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
					// else if (j == 3) {
					// travelMode = (String) point.get("travel_mode");
					// Log.i("MODE", travelMode);
					// }

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(3);
				lineOptions.color(Color.RED);

			}

			// tvDistanceDuration.setText("Distance:" + distance + ", Duration:"
			// + duration);

			Log.i("DIRECT INFO: ", "Distance:" + distance + ", Duration:"
					+ duration);
			Log.i("ADDRESS INFO: ", "Start:" + startAddress + ", End:"
					+ endAddress);

			if (isDraw) {
				// Log.i("DRAW", "NUM of: " + lineOptions.getPoints().size());
				polylineCurrent = mMap.addPolyline(lineOptions);
				stopAmazing = true;
				return;
			}

			if (mode.equals(MODE.walking)) {
				if (duration.equals(""))
					duration = "0";
				Log.i("WALKING duration: ", duration);
				txtWalk.setText(Utility.convertSecTimeToString(Long
						.parseLong(duration)));

			} else if (mode.equals(MODE.driving)) {
				if (duration.equals(""))
					duration = "0";
				mdistance = distance;
				txtDistance.setText(distance);
				txtMoto.setText(Utility.convertSecTimeToString(Long
						.parseLong(duration)));

				polylineOptions = lineOptions;
				// Log.i("DISTANCE: ", distance);
				Log.i("TRAVLING duration: ", duration);
			}
		}
	}

	private void amazingRoute(LatLng latlng) {
		final long interval = 250;
		final int[] radius = new int[] { 1000, 5000, 10000, 20000, 50000 };

		CircleOptions opt = new CircleOptions();
		opt.center(latlng);
		opt.fillColor(0x882176B9);
		opt.strokeWidth(0);
		opt.radius(100);
		final Circle c = mMap.addCircle(opt);
		stopAmazing = false;

		final Handler handler = new Handler();
		final Runnable r = new Runnable() {
			public void run() {
				c.setRadius(radius[ir % 5]);
				ir++;
				if (!stopAmazing) {
					handler.postDelayed(this, interval);
				} else {
					c.remove();
				}
			}
		};

		handler.postDelayed(r, 0);
	}

}
