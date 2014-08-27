package com.sgu.findyourfriend.model;

public class SimpleUserAndLocation {
	private int id;
	private Double lat, lng;
	public SimpleUserAndLocation(int id, Double lat, Double lng) {
		super();
		this.id = id;
		this.lat = lat;
		this.lng = lng;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLng() {
		return lng;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}
}
