package com.example.wochacha.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class NetworkChangeReceiver extends BroadcastReceiver {

	protected Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

		NetworkManager.getInstance().updateConnectStatus();
	}
}