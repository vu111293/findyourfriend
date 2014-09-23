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
package com.sgu.findyourfriend.screen;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sgu.findyourfriend.R;
import com.sgu.findyourfriend.utils.GpsDirection;
import com.sgu.findyourfriend.utils.Utility;

public class MapViewActivity extends FragmentActivity {

	private GoogleMap map;
	private GpsDirection mDirection;
	private Button btnDirection;
	private Button btnQuit;

	private boolean isShow = false;

	private LatLng toLatlng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapview);

		// setup mapview
		map = ((CustomMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapFragment)).getMap();

		map.setMyLocationEnabled(true);

		// get extra
		toLatlng = new LatLng(getIntent().getDoubleExtra("latitude", 0),
				getIntent().getDoubleExtra("longitude", 0));

		// setup to point
		MarkerOptions mkrOpt = new MarkerOptions();
		mkrOpt.position(toLatlng);
		mkrOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_to_position));
		mkrOpt.title(getIntent().getStringExtra("name"));
		mkrOpt.snippet(Utility.getAddress(getApplicationContext(), toLatlng));
		// mkrOpt.icon()

		map.addMarker(mkrOpt);
		Utility.zoomToPosition(toLatlng, map);
		
		mDirection = new GpsDirection(getApplicationContext(), map);

		btnDirection = (Button) findViewById(R.id.btnDirection);
		btnDirection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isShow) {
					mDirection.clearRoute();
					btnDirection.setText("Hiện chỉ đường");
				} else {

					if (null == map.getMyLocation()) {
						Utility.showMessage(getApplicationContext(), "Đang định tuyến, thử lại sau 10s.");
						return;
					} else {
						// Log.i("NULLLLLL", map.getMyLocation().getLatitude() + "");
						
						LatLng myLocation = new LatLng(map.getMyLocation().getLatitude(),
								map.getMyLocation().getLongitude());
						
						mDirection.excuteDirection(myLocation, toLatlng, false);
						List<LatLng> grpLatlng = new ArrayList<LatLng>();
						grpLatlng.add(myLocation);
						grpLatlng.add(toLatlng);
						
						Utility.zoomBoundPosition(grpLatlng, map);
					}

					btnDirection.setText("Ẩn chỉ đường");
				}

				isShow = !isShow;

			}
		});

		btnQuit = (Button) findViewById(R.id.btnQuit);
		btnQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
