package com.example.wochacha.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class UserLocation implements JSONEntity {

	private double latitude;
	private double longitude;

	private String city;
	private String state;
	//private String zip;
	//private String address;
	//private String street;

	public UserLocation() {
	}

	public UserLocation(double lat, double lng) {
		this.latitude = lat;
		this.longitude = lng;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getCity() {
		return city;
	}
	
	public String getState() {
		return state;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	

	@Override
	public String toJsonString() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("lat", latitude);
		object.put("lng", this.longitude);
		return object.toString();
	}

	@Override
	public void populate(JSONObject object) {
		this.latitude = object.optDouble("lat");
		this.longitude = object.optDouble("lng");
	}

}
