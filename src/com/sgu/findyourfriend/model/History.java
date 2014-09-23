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
package com.sgu.findyourfriend.model;

import java.sql.Timestamp;

import com.google.android.gms.maps.model.LatLng;

public class History {

	private Timestamp timest;
	private LatLng location;

	public History(Timestamp timest, LatLng location) {
		this.setTimest(timest);
		this.setLocation(location);
	}

	public Timestamp getTimest() {
		return timest;
	}

	public void setTimest(Timestamp timest) {
		this.timest = timest;
	}

	public LatLng getLocation() {
		return location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}
}
