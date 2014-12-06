package com.example.wochacha.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Message implements JSONEntity {

	private String companyName;
	private long companyId;
	private int newMessageCount;
	private String iconUrl;

	public String getIconUrl() {
		return iconUrl;
	}

	public long getCompanyId() {
		return companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public int getNewMessageCount() {
		return newMessageCount;
	}

	@Override
	public String toJsonString() throws JSONException {
		return "";

	}

	public void read() {
		newMessageCount = 0;
	}

	@Override
	public void populate(JSONObject object) {
		this.companyId = object.optLong("company_id");
		this.newMessageCount = object.optInt("message_count");
		this.iconUrl = object.optString("image_url");
		this.companyName = object.optString("company_name");
	}

}
