package com.example.wochacha.manager;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.example.wochacha.entity.Message;
import com.example.wochacha.entity.ScanResult.Manufacturer;
import com.example.wochacha.util.Constants;
import com.example.wochacha.util.StringHelper;

import android.R.array;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class MessageManager extends BaseManager {

	private static MessageManager manager;
	private Context context;
	private List<Message> scannedManufacturers = new ArrayList<>();

	private MessageManager(Context context) {
		this.context = context;
	}

	public static synchronized MessageManager getInstance() {

		return manager;
	}

	public static synchronized MessageManager initializeIntance(
			Context appContext) {

		if (manager == null) {
			manager = new MessageManager(appContext);
		}
		return manager;
	}

	public void restore() {
		SharedPreferences preferences = context.getSharedPreferences(
				Constants.Preference.PREF_DEMO_MANUFACTURERS,
				Context.MODE_PRIVATE);
		String data = preferences.getString(
				Constants.Preference.KEY_DEMO_MANUFACTURER_DATA, "");
		if (!StringHelper.isStringNullOrEmpty(data)) {

			JSONArray array;
			try {
				array = new JSONArray(data);
				for (int i = 0; i < array.length(); i++) {
					Message message = new Message();
					message.populate(array.getJSONObject(i));
					scannedManufacturers.add(message);

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void applyMessage(Message message) {
		scannedManufacturers.add(message);
		syncToPreference();
	}

	public Message getMessageByManufacturerId(int id) {
		for (Message message : scannedManufacturers) {
			if (message.getCompanyId() == id) {
				return message;
			}
		}
		return null;
	}

	public void syncToPreference() {

		JSONArray array = new JSONArray();
		for (Message message : scannedManufacturers) {
			array.put(message.toJsonObject());
		}

		Editor editor = context.getSharedPreferences(
				Constants.Preference.PREF_DEMO_MANUFACTURERS,
				Context.MODE_PRIVATE).edit();

		editor.putString(Constants.Preference.KEY_DEMO_MANUFACTURER_DATA,
				array.toString());
		editor.commit();

	}

	public void registerMessageNotification(onMessageNofify listener) {
		
		addListener(onMessageNofify.class.getSimpleName(), listener);
	}

	public void unRegisterMessageNotificaiton(onMessageNofify listener) {
		removeListener(onMessageNofify.class.getSimpleName(), listener);
	}

	public interface onMessageNofify {
		public void onMessageNotified();

		public void onAllMessageReaded();
	}

	public void pushNewNotification(Message message) {

		applyMessage(message);
	
		List<Object> listener = getListenersOfOneType(onMessageNofify.class
				.getSimpleName());
		for (Object object : listener) {
		
			((onMessageNofify) object).onMessageNotified();
		}
	}

	public boolean hasUnreadMessage() {
		int count = 0;
		for (Message message : scannedManufacturers) {
			count = count + message.getNewMessageCount();
		}

		return count > 0;
	}

	public void pushReadNotification(int manufacturerId) {
		Message message = getMessageByManufacturerId(manufacturerId);
		if (message.getNewMessageCount() != 0) {
			message.setNewMessageCount(0);
			syncToPreference();
		}

		if (!hasUnreadMessage()) {
			List<Object> listener = getListenersOfOneType(onMessageNofify.class
					.getSimpleName());
			for (Object object : listener) {
				((onMessageNofify) object).onAllMessageReaded();
			}
		}

	}

	public void updateMessageWithManufacturerInfo(Message message, Manufacturer manufacturer) {
		boolean shouldSync = false;
		if (!manufacturer.getImageUri().equalsIgnoreCase(message.getIconUrl())) {
			message.setIconUrl(manufacturer.getImageUri());
			shouldSync = true;
		}
		if (!manufacturer.getName().equalsIgnoreCase(message.getCompanyName())) {
			message.setCompanyName(manufacturer.getName());
			shouldSync = true;
		}
		if (shouldSync) {
			syncToPreference();
		}

	}
	
	public List<Message> getScannedManufacturers() {
		return scannedManufacturers;
	}
	
	

}
