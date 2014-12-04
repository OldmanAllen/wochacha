package com.example.wochacha.exception;

@SuppressWarnings("serial")
public class ServerGeneralException extends BaseException {

	protected int statusCode;

	public ServerGeneralException() {
		super("Unknown error.");
		this.statusCode = -1;
	}

	public ServerGeneralException(String msg, int statusCode) {
		super(msg);
		this.statusCode = statusCode;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
}
