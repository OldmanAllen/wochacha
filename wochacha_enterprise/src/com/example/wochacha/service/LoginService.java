package com.example.wochacha.service;

import org.json.JSONObject;

import com.example.wochacha.exception.LocalGeneralException;
import com.example.wochacha.exception.NetworkNotAvailableException;
import com.example.wochacha.exception.ServerAuthException;
import com.example.wochacha.exception.ServerGeneralException;
import com.example.wochacha.network.RequestManager;

public class LoginService extends DataServiceImpl {

	private static final String LOGIN_URL = "/login";
	private String phone;
	private String pwd;

	public LoginService(String phone, String pwd) {
		this.phone = phone;
		this.pwd = pwd;
	}

	@Override
	protected JSONObject method() throws ServerAuthException, ServerGeneralException, LocalGeneralException,
			NetworkNotAvailableException, Exception {

		JSONObject object = new JSONObject();
		object.put("phone", this.phone);
		object.put("pwd", this.pwd);

		return RequestManager.Post(LOGIN_URL, object.toString());
	}

}
