/*
 * Copyright (C) 2014 Tubor Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
