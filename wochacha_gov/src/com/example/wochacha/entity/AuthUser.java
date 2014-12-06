package com.example.wochacha.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthUser implements JSONEntity {

	private long id;
	private String phone;
	private String name;
	private String imageUrl;
	private String token;
	private boolean isAuthorized;

	public long getId() {
		return id;
	}

	public String getPhone() {
		return phone;
	}

	public String getName() {
		return name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getToken() {
		return token;
	}

	public boolean isAuthorized() {
		return isAuthorized;
	}

	public void setAuthorized(boolean isAuthorized) {
		this.isAuthorized = isAuthorized;
	}

	public void populate(JSONObject object) {
		id = object.optLong("id");
		phone = object.optString("phone");
		name = object.optString("name");
		imageUrl = object.optString("imageUrl");
		isAuthorized = true;
	}

	public String toJsonString() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("id", id);
		object.put("phone", phone);
		object.put("name", name);
		object.put("token", token);
		return object.toString();
	}

}
