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
