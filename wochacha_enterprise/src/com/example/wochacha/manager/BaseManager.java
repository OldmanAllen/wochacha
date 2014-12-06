package com.example.wochacha.manager;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

import com.example.wochacha.exception.BaseException;
import com.example.wochacha.exception.LocalGeneralException;
import com.example.wochacha.exception.ServerAuthException;
import com.example.wochacha.exception.ServerGeneralException;
import com.example.wochacha.util.ToastMessageHelper;

public class BaseManager {

	protected Context context;

	protected HashMap<String, ArrayList<Object>> listeners = new HashMap<String, ArrayList<Object>>();

	protected void HandleException(BaseException ex) {
		String errorMsg = ex.getMessage();
		if (ex instanceof ServerAuthException) {
			SessionManager.getInstance().reLogin();
			ex.handled();
			return;
		} else if (ex instanceof ServerGeneralException) {
			ToastMessageHelper.showErrorMessage(context, errorMsg, false);
		} else if (ex instanceof LocalGeneralException) {
			ToastMessageHelper.showErrorMessage(context, errorMsg, false);
		} else {
			ToastMessageHelper.showErrorMessage(context, errorMsg, false);
		}
	}

	protected void addListener(String listenerName, Object listener) {
		if (listener == null)
			return;
		ArrayList<Object> listListener = listeners.get(listenerName);
		if (listListener == null) {
			ArrayList<Object> listListenerNew = new ArrayList<Object>();
			listListenerNew.add(listener);
			listeners.put(listenerName, listListenerNew);
		} else {
			listListener.add(listener);
		}
	}

	protected void removeListener(String listenerName, Object listener) {
		if (listener == null || listeners.get(listenerName) == null)
			return;
		listeners.get(listenerName).remove(listener);
	}
	
	protected ArrayList<Object> getListenersOfOneType(String listenerName) {
		ArrayList<Object> listener = listeners.get(listenerName);
		if (listener == null)
			return new ArrayList<Object>();
		return listener;
	}
	

}
