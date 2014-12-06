package com.example.wochacha.exception;

/**
 * 
 * TODO: we may need status code <-> message mappings.
 * 
 * FileName: BaseException.java
 * 
 * Description:
 * 
 * Author: qyu (Qiyong Yu)
 * 
 * Created Date: Nov 18, 2014 3:16:29 PM
 * 
 * 
 */
@SuppressWarnings("serial")
public class BaseException extends Exception {

	protected boolean isHandled = false;	

	public BaseException(String message) {
		super(message);		
	}

	public void handled() {
		isHandled = true;
	}

	public boolean isHandled() {
		return isHandled;
	}

}
