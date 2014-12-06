package com.example.wochacha.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class EnterpriseAccount implements JSONEntity {

	private long accountId;
	private String iconUrl;
	private String name;
	private String token;

	public long getAccountId() {
		return accountId;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public String getName() {
		return name;
	}

	public String getToken() {
		return token;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toJsonString() throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void populate(JSONObject object) {
		// TODO Auto-generated method stub

	}

	public boolean isAuthorized() {		
		return false;
	}

}
