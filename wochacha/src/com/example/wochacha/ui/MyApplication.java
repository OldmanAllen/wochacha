package com.example.wochacha.ui;

import android.app.Application;
import android.content.Context;

import com.example.wochacha.manager.DeviceGeoLocationManager;
import com.example.wochacha.manager.DeviceManager;
import com.example.wochacha.manager.EnterpriseSessionManager;
import com.example.wochacha.manager.MessageManager;
import com.example.wochacha.manager.SessionManager;
import com.example.wochacha.network.CacheService;
import com.example.wochacha.network.ImageManager;
import com.example.wochacha.network.NetworkManager;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		Context appContext = this;
		DeviceManager.initializeIntance(appContext);

		NetworkManager.initializeIntance(appContext).isNetworkConnected();

		SessionManager sessionManager = SessionManager.initializeIntance(appContext);
		sessionManager.restore();
		
		
		EnterpriseSessionManager enterpriseSessionManager = EnterpriseSessionManager.initializeIntance(appContext);
		enterpriseSessionManager.restore();

		CacheService.initializeInstance(appContext);
		ImageManager.initializeIntance(appContext);
		DeviceGeoLocationManager.initializeIntance(appContext);
		DeviceGeoLocationManager.getInstance().restore();
		DeviceGeoLocationManager.getInstance().registerLocationUpdate();

		MessageManager.initializeIntance(appContext);
		MessageManager.getInstance().restore();
		
	}

}
