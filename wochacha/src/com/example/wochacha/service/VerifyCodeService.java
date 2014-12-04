package com.example.wochacha.service;

import org.json.JSONObject;

import com.example.wochacha.entity.ScanRequest;
import com.example.wochacha.exception.LocalGeneralException;
import com.example.wochacha.exception.NetworkNotAvailableException;
import com.example.wochacha.exception.ServerAuthException;
import com.example.wochacha.exception.ServerGeneralException;
import com.example.wochacha.network.RequestManager;

public class VerifyCodeService extends DataServiceImpl {
	///scan/45dewdS96UWe6YlQ6tDBmg
	private static final String VERIFY_URL = "/scan";
	private String code;
	private ScanRequest request;

	public VerifyCodeService(String code, ScanRequest request) {
		this.code = code;
		this.request = request;
	}

	@Override
	protected JSONObject method() throws ServerAuthException,
			ServerGeneralException, LocalGeneralException,
			NetworkNotAvailableException, Exception {
		String url = VERIFY_URL + "/" + code;

		JSONObject object = RequestManager.Post(url, request.toJsonString());	
		return object;
	}

}
