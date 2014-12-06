package com.example.wochacha.exception;

@SuppressWarnings("serial")
public class ServerAuthException extends BaseException {
	
	public ServerAuthException()
	{
		super("It's login required.");
	}
	
	public ServerAuthException(String msg)
	{
		super(msg);
	}
}
