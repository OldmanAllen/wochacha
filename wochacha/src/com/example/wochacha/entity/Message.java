package com.example.wochacha.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Message implements JSONEntity {

	private String companyName;
	private int companyId;
	private int newMessageCount;
	private String iconUrl;

	public String getIconUrl() {
		return iconUrl;
	}

	public int getCompanyId() {
		return companyId;
	}
	
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
	
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public void setNewMessageCount(int newMessageCount) {
		this.newMessageCount = newMessageCount;
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

	public JSONObject toJsonObject() {
		JSONObject object = new JSONObject();
		try {
			object.put("company_id", this.companyId);
			object.put("message_count", this.newMessageCount);
			object.put("image_url", this.iconUrl);
			object.put("company_name", this.companyName);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return object;
	}

	@Override
	public void populate(JSONObject object) {
		this.companyId = object.optInt("company_id");
		this.newMessageCount = object.optInt("message_count");
		this.iconUrl = object.optString("image_url");
		this.companyName = object.optString("company_name");
	}

}
