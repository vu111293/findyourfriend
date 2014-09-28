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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

public class CustomMapFragment extends SupportMapFragment {
	private View mOriginalView;
	private MapWrapperLayout mMapWrapperLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mOriginalView = super.onCreateView(inflater, container,
				savedInstanceState);

		mMapWrapperLayout = new MapWrapperLayout(getActivity());
		mMapWrapperLayout.addView(mOriginalView);

		return mMapWrapperLayout;
	}

	@Override
	public View getView() {
		return mOriginalView;
	}

	public void setOnDragListener(MapWrapperLayout.OnDragListener onDragListener) {
		mMapWrapperLayout.setOnDragListener(onDragListener);
	}
}
