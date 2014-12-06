package com.example.wochacha.network;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.util.Log;

import com.example.wochacha.exception.ServerAuthException;
import com.example.wochacha.exception.ServerGeneralException;
import com.example.wochacha.manager.DeviceManager;
import com.example.wochacha.manager.SessionManager;
import com.example.wochacha.network.RestClient.RequestMethod;
import com.example.wochacha.util.Constants;

public class RequestManager {

	private final static String TAG = RequestManager.class.getSimpleName();

	public static JSONObject Post(String url, String postJsonString) throws ServerAuthException, ServerGeneralException,
			Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		Log.d(TAG, "Request started: at " + format.format(new Date()));
		JSONObject jsonObject = null;

		RestClient restClient = new RestClient(Constants.SERVER_URL + url);
		restClient.SetIsJsonContent(true);

		SetRequestHeader(restClient);
		restClient.AddParam("content", postJsonString);

		jsonObject = restClient.Execute(RequestMethod.POST);
		Log.d(TAG, "Request end: at " + format.format(new Date()));
		return HandleResponseResult(jsonObject);
	}

	public static JSONObject PostByFile(String url, String sFilePath) throws ServerAuthException, ServerGeneralException,
			Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		Log.d(TAG, "Request started: at " + format.format(new Date()));
		JSONObject jsonObject = null;

		RestClient restClient = new RestClient(Constants.SERVER_URL + url);
		restClient.SetIsJsonContent(false);
		restClient.setIsFilePost(true);

		SetRequestHeader(restClient);
		restClient.AddParam("content", sFilePath);

		jsonObject = restClient.Execute(RequestMethod.POST);
		Log.d(TAG, "Request end: at " + format.format(new Date()));
		return HandleResponseResult(jsonObject);
	}

	public static JSONObject PUT(String url, String postJsonString) throws ServerAuthException, ServerGeneralException,
			Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		Log.d(TAG, "Request started: at " + format.format(new Date()));
		JSONObject jsonObject = null;

		RestClient restClient = new RestClient(Constants.SERVER_URL + url);
		restClient.SetIsJsonContent(true);

		SetRequestHeader(restClient);
		restClient.AddParam("content", postJsonString);

		jsonObject = restClient.Execute(RequestMethod.PUT);
		Log.d(TAG, "Request end: at " + format.format(new Date()));
		return HandleResponseResult(jsonObject);
	}

	public static JSONObject Get(String url, List<NameValuePair> nameValuePairs, Boolean... forceRefresh)
			throws ServerAuthException, ServerGeneralException, Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		Log.d(TAG, "Request started: at " + format.format(new Date()));
		JSONObject jsonObject = null;
		String query = Constants.EmptyString;

		if (nameValuePairs != null && nameValuePairs.size() > 0) {
			query = URLEncodedUtils.format(nameValuePairs, HTTP.UTF_8);
		}
		RestClient restClient = new RestClient(Constants.SERVER_URL + url
				+ (query == Constants.EmptyString ? Constants.EmptyString : Constants.QuestionMark + query));

		if (forceRefresh != null && forceRefresh.length > 0 && forceRefresh[0]) {
			restClient.setForceToRefresh(true);
		}
		restClient.SetIsJsonContent(true);
		SetRequestHeader(restClient);
		jsonObject = restClient.Execute(RequestMethod.GET);
		Log.d(TAG, "Request end: at " + format.format(new Date()));
		return HandleResponseResult(jsonObject);
	}

	public static JSONObject GetWithoutToken(String url, List<NameValuePair> nameValuePairs, Boolean... forceRefresh)
			throws ServerAuthException, ServerGeneralException, Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		Log.d(TAG, "Request started: at " + format.format(new Date()));
		JSONObject jsonObject = null;
		String query = Constants.EmptyString;

		if (nameValuePairs != null && nameValuePairs.size() > 0) {
			query = URLEncodedUtils.format(nameValuePairs, HTTP.UTF_8);
		}
		RestClient restClient = new RestClient(Constants.SERVER_URL + url
				+ (query == Constants.EmptyString ? Constants.EmptyString : Constants.QuestionMark + query));

		if (forceRefresh != null && forceRefresh.length > 0 && forceRefresh[0]) {
			restClient.setForceToRefresh(true);
		}
		restClient.SetIsJsonContent(true);
		SetRequestHeaderWithoutToken(restClient);
		jsonObject = restClient.Execute(RequestMethod.GET);
		Log.d(TAG, "Request end: at " + format.format(new Date()));
		return HandleResponseResult(jsonObject);
	}

	public static JSONObject GetWithURL(String url) throws ServerAuthException, ServerGeneralException, Exception {
		JSONObject jsonObject = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		Log.d(TAG, "Request started: at " + format.format(new Date()));
		RestClient restClient = new RestClient(url);
		restClient.SetIsJsonContent(true);
		SetRequestHeader(restClient);
		jsonObject = restClient.Execute(RequestMethod.GET);
		Log.d(TAG, "Request end: at " + format.format(new Date()));
		return HandleResponseResult(jsonObject);
	}

	private static JSONObject HandleResponseResult(JSONObject jsonObject) {
		return jsonObject;
	}

	private static void SetRequestHeaderWithoutToken(RestClient restClient) {
		if (restClient.isFilePost()) {
			// restClient.AddHeader(HTTP.CONTENT_TYPE, "binary/octet-stream");
			restClient.AddHeader(HTTP.CONTENT_TYPE, "text/plain; charset=utf-8");
		} else {
			restClient.AddHeader(HTTP.CONTENT_TYPE, "application/json; charset=utf-8");
		}

		AddHeaderWithDeviceInfo(restClient);
	}

	private static void AddHeaderWithDeviceInfo(RestClient restClient) {
		DeviceManager deviceManager = DeviceManager.getInstance();
		restClient.AddHeader("DeviceModel", deviceManager.getDeviceModel());
		// restClient.AddHeader("SecondsFromGMT", deviceManager.getDeviceSecondsFromGMT());
		restClient.AddHeader("ConnectionType", deviceManager.getDeviceConnectionType());
		restClient.AddHeader("SDKVersion", deviceManager.getDeviceSDKVersion());
		restClient.AddHeader("GeoLocation", deviceManager.getDeviceGeoLocationInfo());
		restClient.AddHeader("ClientVersion", deviceManager.getAppVersion());
	}

	private static void SetRequestHeader(RestClient restClient) {
		SetRequestHeaderWithoutToken(restClient);
		restClient.AddHeader("Token", SessionManager.getInstance().getToken());
	}
}
