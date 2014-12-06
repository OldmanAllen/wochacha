package com.example.wochacha.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class EnterpriseHomeItem implements JSONEntity {

	private int iconResourceId;
	private String actionUrl;
	private int itemId;
	private String title;
	private int newMessageCount;

	public String getActionUrl() {
		return actionUrl;
	}

	public void setActionUrl(String actionUrl) {
		this.actionUrl = actionUrl;
	}

	public int getIconResourceId() {
		return iconResourceId;
	}

	public void setIconResourceId(int iconResourceId) {
		this.iconResourceId = iconResourceId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getNewMessageCount() {
		return newMessageCount;
	}

	public void setNewMessageCount(int newMessageCount) {
		this.newMessageCount = newMessageCount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toJsonString() throws JSONException {
		return null;
	}

	@Override
	public void populate(JSONObject object) {
		// TODO Auto-generated method stub

	}

}
