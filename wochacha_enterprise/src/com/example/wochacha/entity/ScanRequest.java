package com.example.wochacha.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class ScanRequest implements JSONEntity {

	private String deviceId;
	private String deviceModel;
	private double lat;
	private double lng;
	private String osVersion;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	@Override
	public String toJsonString() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("deviceId", this.deviceId);
		object.put("deviceModel", this.deviceModel);

		JSONObject latLng = new JSONObject();
		latLng.put("longitude", this.lng);
		latLng.put("latitude", this.lat);

		object.put("coordinate", latLng);
		object.put("osVersion", this.osVersion);

		return object.toString();

	}

	@Override
	public void populate(JSONObject object) {
		
	}

}
