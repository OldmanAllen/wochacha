package com.example.wochacha.manager;

import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.wochacha.R;
import com.example.wochacha.entity.UserLocation;
import com.example.wochacha.network.NetworkManager;
import com.example.wochacha.util.Constants;
import com.example.wochacha.util.StringHelper;
import com.example.wochacha.util.ToastMessageHelper;

public class DeviceGeoLocationManager extends BaseManager implements LocationListener {
	private static String TAG = DeviceGeoLocationManager.class.getSimpleName();
	private static DeviceGeoLocationManager deviceGeoLocationManager;

	private LocationManager locationManager;

	private Location location;

	private String geoLocationInfo = Constants.NA;
	private Geocoder mGeoCoder;
	private UserLocation lastKnownLocation;
	private final int TIMEOUT_GET_LOCATION = 5; // 10s

	private int time_count = 0;
	private boolean isLocationChanged = false;

	private Handler timeoutHandler = new Handler();

	private DeviceGeoLocationManager(Context appContext) {
		context = appContext;
		locationManager = (LocationManager)appContext.getSystemService(Context.LOCATION_SERVICE);
		mGeoCoder = new Geocoder(appContext, Locale.getDefault());
		lastKnownLocation = new UserLocation(0, 0);

	}

	public static synchronized DeviceGeoLocationManager initializeIntance(Context appContext) {

		if (deviceGeoLocationManager == null) {
			deviceGeoLocationManager = new DeviceGeoLocationManager(appContext);
		}
		return deviceGeoLocationManager;
	}

	public static DeviceGeoLocationManager getInstance() {
		if (deviceGeoLocationManager == null) {
			Log.d(TAG, "deviceGeoLocationManager has not been initialized");
		}
		return deviceGeoLocationManager;
	}

	public void addGeoLocationUpdatedListener(GeoLocationUpdated listener) {
		addListener(GeoLocationUpdated.class.getSimpleName(), listener);
	}

	public void removeGeoLocationUpdatedListener(GeoLocationUpdated listener) {
		removeListener(GeoLocationUpdated.class.getSimpleName(), listener);
	}
	
	

	public void restore() {
		SharedPreferences preferences = context.getSharedPreferences(Constants.Preference.PREF_LAST_KNOWN_LOCATION,
				Context.MODE_PRIVATE);
		String locString = preferences.getString(Constants.Preference.KEY_LAST_LOCATION, "");
		if (!StringHelper.isStringNullOrEmpty(locString)) {
			try {
				lastKnownLocation.populate(new JSONObject(locString));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void saveLastKnownLocation(UserLocation loc) {

		try {
			Editor editor = context.getSharedPreferences(Constants.Preference.PREF_LAST_KNOWN_LOCATION,
					Context.MODE_PRIVATE).edit();
			editor.putString(Constants.Preference.KEY_LAST_LOCATION, loc.toJsonString());
			editor.commit();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public boolean isLocationServiceEnabled() {
		boolean isLocationServiceEnabled = false;
		List<String> enabledProviders = locationManager.getProviders(true);
		if (enabledProviders != null && enabledProviders.size() > 0) {
			for (String provider : enabledProviders) {
				if (provider.equals(LocationManager.GPS_PROVIDER) || provider.equals(LocationManager.NETWORK_PROVIDER)) {
					isLocationServiceEnabled = true;
					break;
				}
			}
		}
		return isLocationServiceEnabled;
	}

	public void registerLocationUpdate() {

		List<String> enabledProviders = locationManager.getProviders(true);
		if (enabledProviders == null || enabledProviders.size() == 0 || time_count != 0) {
			notifyGEOLocationDone(getCurrentLocation());
			return;
		}

		if (!NetworkManager.getInstance().isNetworkConnected()) {
			ToastMessageHelper.showErrorMessage(context, context.getString(R.string.network_not_available), true);
			notifyGEOLocationDone(getCurrentLocation());
			return;
		}

		// Only check gps and network, ignore the passive provider.
		for (String provider : enabledProviders) {
			if (provider.equals(LocationManager.GPS_PROVIDER) || provider.equals(LocationManager.NETWORK_PROVIDER)) {
				locationManager.requestLocationUpdates(provider, 0, 0, this);
			}
		}
		isLocationChanged = false;
		timeoutHandler.postDelayed(timeoutRunnable, 1000);

	}

	public void unregisterLocationUpdate() {
		locationManager.removeUpdates(this);
	}

	public UserLocation getCurrentLocation() {
		return lastKnownLocation;
	}

	public String getGeoLocationInfo() {
		if (location == null) {
			return null;
		}
		return geoLocationInfo;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				//UserLocation l = (UserLocation)msg.obj;
				double lat = location.getLatitude();
				double lng = location.getLongitude();
				if (lastKnownLocation.getLatitude() != lat || lastKnownLocation.getLongitude() != lng) {
					lastKnownLocation.setLatitude(lat);
					lastKnownLocation.setLongitude(lng);

					saveLastKnownLocation(lastKnownLocation);
				}
				notifyGEOLocationDone(getCurrentLocation());
				break;
			case 2:
				notifyGEOLocationDone(getCurrentLocation());
				break;
			default:
				break;
			}
		}

	};

	private void notifyGEOLocationDone(UserLocation loc) {
		for (Object sl : getListenersOfOneType(GeoLocationUpdated.class.getSimpleName())) {
			((GeoLocationUpdated)sl).onUpdated(loc);
		}
	}

	protected void refreshGeoLocationInfo() {
		geoLocationInfo = String.format("{\"Longitude\":%s,\"Latitude\":%s}", location.getLongitude(),
				location.getLatitude());
		
		
		// TODO: to get location info like city and state.
		// onGeoCoding(this.location.getLatitude(), this.location.getLongitude());		
		handler.sendEmptyMessage(1);
	}

	private Runnable timeoutRunnable = new Runnable() {
		@Override
		public void run() {

			time_count++;
			if (time_count > TIMEOUT_GET_LOCATION) {
				time_count = 0;
				unregisterLocationUpdate();
				handler.sendEmptyMessage(2);
				return;
			}
			if (!isLocationChanged)
				timeoutHandler.postDelayed(this, 1000);
			else {
				time_count = 0;
			}
		}
	};

	@Override
	public void onLocationChanged(Location newLocation) {
		String name = newLocation.getProvider();
		Log.d(TAG, name);
		isLocationChanged = true;
		if (isBetterLocation(newLocation, this.location)) {
			this.location = newLocation;
			refreshGeoLocationInfo();
		} else {
			notifyGEOLocationDone(getCurrentLocation());
		}
		unregisterLocationUpdate();
	}

	@SuppressWarnings("unused")
	private void onGeoCoding(final double lat, final double lon) {

		new Thread() {

			@Override
			public void run() {
				int code = 1;
				UserLocation location = new UserLocation();
				String name = String.format("(%.3f, %.3f)", lat, lon);
				try {
					List<Address> addresses = mGeoCoder.getFromLocation(lat, lon, 1);
					if (addresses != null && addresses.size() > 0) {

						String locality = addresses.get(0).getLocality();
						String adminArea = addresses.get(0).getAdminArea();
						location.setCity(locality);
						location.setState(adminArea);
					}
				} catch (Exception ex) {
					Log.e(TAG, ex.getMessage());
					code = 2;
				}
				Message message = Message.obtain();
				message.what = code;
				message.obj = location;
				handler.sendMessage(message);
			}

		}.start();

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	/**
	 * Determines whether one Location reading is better than the current Location fix * @param location The new
	 * Location that you want to evaluate * @param currentBestLocation The current Location fix, to which you want to
	 * compare the new one
	 */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		final int TWO_MINUTES = 1000 * 60 * 2;

		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}
		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;
		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}
		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int)(location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;
		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());
		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	public interface GeoLocationUpdated {
		void onUpdated(UserLocation loc);
	}

}
