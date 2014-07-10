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
