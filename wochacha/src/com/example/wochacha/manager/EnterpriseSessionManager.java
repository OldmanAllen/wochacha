package com.example.wochacha.manager;

import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.example.wochacha.R;
import com.example.wochacha.entity.AuthUser;
import com.example.wochacha.entity.EnterpriseAccount;
import com.example.wochacha.util.Constants;

/**
 * 
 * SessionManager is used for handling user related operations including: Login / logout, auth user info.
 * 
 * FileName: SessionManager.java
 * 
 * Description:
 * 
 * Author: qyu (Qiyong Yu)
 * 
 * Created Date: Nov 18, 2014 6:04:48 PM
 * 
 * 
 * Copyright (C) MicroStrategy Incorporated 2014 All Rights Reserved
 */

public class EnterpriseSessionManager extends BaseManager {

	private static EnterpriseSessionManager sessionManager;
	private EnterpriseAccount authUser;
	private WeakReference<Activity> topActivityReference;

	public static EnterpriseSessionManager initializeIntance(Context context) {

		if (sessionManager == null) {
			synchronized (EnterpriseSessionManager.class) {
				if (sessionManager == null) {
					sessionManager = new EnterpriseSessionManager();
					sessionManager.context = context;
				}
			}
		}
		return sessionManager;
	}

	private EnterpriseSessionManager() {
		authUser = new EnterpriseAccount();
	}

	public static EnterpriseSessionManager getInstance() {
		if (sessionManager == null) {
			Log.w("EnterpriseSessionManager", "EnterpriseSessionManager instance has not been initialized");
		}
		return sessionManager;
	}

	public EnterpriseAccount getAuthUser() {
		return authUser;
	}

	// this should be in the base activity which can handle the login action.
	// when credential is required.
	public void pushActivity(Activity activity) {
		topActivityReference = new WeakReference<Activity>(activity);
	}

	public void restore() {

		SharedPreferences preferences = context.getSharedPreferences(Constants.Preference.PREF_ENTERPRISE_LOGIN,
				Context.MODE_PRIVATE);

		String authUserInfo = preferences.getString(Constants.Preference.KEY_ENTERPRISE_ACCOUNT_INFO, null);
		if (authUserInfo != null) {
			try {
				JSONObject objectUser = new JSONObject(authUserInfo);
				if (authUser == null) {
					authUser = new EnterpriseAccount();
				}
				authUser.populate(objectUser);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			authUser.setAccountId(1234567);
			authUser.setIconUrl("");
			authUser.setName(context.getString(R.string.enterprise_demo_title));
			authUser.setToken("toekn112332");
		}

	}

	private void saveLoginCredential() {

		try {
			Editor editor = context.getSharedPreferences(Constants.Preference.PREF_ENTERPRISE_LOGIN, Context.MODE_PRIVATE).edit();
			editor.putString(Constants.Preference.KEY_ENTERPRISE_ACCOUNT_INFO, authUser.toJsonString());
			editor.commit();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void clearLoginCredential() {
		//TODO
		Editor editor = context.getSharedPreferences(Constants.Preference.PREF_ENTERPRISE_LOGIN, Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
		authUser = new EnterpriseAccount();
	}

	public void logout() {

	}

	private void login(String phone, String pwd) {
		// Login service to call
		// LoginService service = new LoginService(phone, pwd);
		// service.setDelegate(this);
		// service.start();

	}

	public synchronized void reLogin() {
		/*
		 * if (mReloginHandling) return;
		 * 
		 * if (mFacebook == null) { mFacebook = new Facebook(Constant.Emma.EMMA_APP_ID); } if (topActivityReference !=
		 * null && topActivityReference.get() != null) { mFacebook.authorize(topActivityReference.get(),
		 * Constant.Facebook.PERMISSION, mReLoginListener); mReloginHandling = true; }
		 */
	}

	/*
	 * @Override public void onRequestSuccess(Object result, Object data) { if (data.equals(TASK_CALL_GRAPH)) {
	 * mFBUserEmail = ((String[]) result)[0]; mFBUserId = ((String[]) result)[1]; mFBUserName = ((String[]) result)[2];
	 * saveLoginCredential(); loginEmma(TASK_LOGIN_EMMA); } else if (data.equals(TASK_LOGIN_EMMA)) { //
	 * Congratulations!! we complete all the login task, notify user // step into maintabs authUser = (AuthUser) result;
	 * = mSessionUser.AuthToken; saveLoginCredential(); notifyLoginSuccess();
	 * 
	 * EmmaActivityTaskData taskData = UserSettingsManager.getInstance().getTaskData(Task.TASK_GET_USER_SETTINGS,
	 * Login.class); UserSettingsManager.getInstance().getUserSettings(taskData,true);
	 * 
	 * } else if (data.equals(TASK_LOGOUT_EMMA)) { // logoutFacebook(); clearLoginCredential(); clearLoginInfo();
	 * mIsLogout = true; logoutFacebook(); } else if (data.equals(TASK_SHARE_EMMA)) { mSessionUser = (AuthUser) result;
	 * = mSessionUser.AuthToken; saveLoginCredential(); notifyGrantPermissionLogicSuccess(); } else if
	 * (data.equals(TASK_RELOGIN_EMMA)) { mSessionUser = (AuthUser) result; = mSessionUser.AuthToken;
	 * saveLoginCredential(); if (mTopActivityListener != null && mTopActivityListener.get() != null)
	 * mTopActivityListener.get().onLoginComplete(true); } else if (data.equals(TASK_PREPARE_LOGIN)) { for (Object sl :
	 * getListenersOfOneType(Session_Prepare_Login_Listener)) { ((SessionPauseListener) sl).onPauseComplete(); } } }
	 * 
	 * @Override public void onRequestFailed(Object result, Object data) { if (data.equals(TASK_CALL_GRAPH)) {
	 * 
	 * } else if (data.equals(TASK_LOGIN_EMMA)) { notifyLoginFail(); } else if (data.equals(TASK_LOGOUT_EMMA)) { //
	 * logoutFacebook(); notifyLogoutCompleted(false); } else if (data.equals(TASK_RELOGIN_EMMA)) { if
	 * (mTopActivityListener != null && mTopActivityListener.get() != null)
	 * mTopActivityListener.get().onLoginComplete(false); } }
	 */
	public String getId() {
		EnterpriseAccount user = getAuthUser();
		if (!user.isAuthorized()) {
			return "account_" +DeviceManager.getInstance().getDeviceId();
		} else {
			return String.valueOf(user.getAccountId());
		}
	}

	public String getToken() {
		return getAuthUser().getToken();
	}

}
