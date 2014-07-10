package com.sgu.findyourfriend.model;

import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

public class StepHistory {
	private LatLng latLng;
	private Date timeReceived;
	
	public StepHistory(LatLng latlng, Date timeReceived) {
		this.setLatLng(latlng);
		this.setTimeReceived(timeReceived);
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	public Date getTimeReceived() {
		return timeReceived;
	}

	public void setTimeReceived(Date timeReceived) {
		this.timeReceived = timeReceived;
	}
}
