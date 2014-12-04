package com.example.wochacha.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.wochacha.exception.BaseException;
import com.example.wochacha.exception.LocalGeneralException;
import com.example.wochacha.exception.NetworkNotAvailableException;
import com.example.wochacha.exception.ServerAuthException;
import com.example.wochacha.exception.ServerGeneralException;
import com.example.wochacha.network.RequestManager;

public abstract class DataServiceImpl implements Runnable {

	public interface DataServiceDelegate {
		public void onRequestSucceeded(DataServiceImpl service, JSONObject data, boolean isCached);

		public void onRequestFailed(DataServiceImpl service, BaseException exception);
	}

	protected DataServiceDelegate delegate;

	public void start() {
		ServiceExecutor.getInstance().execute(this);
	}

	public void cancel() {
		ServiceExecutor.getInstance().cancel(this);
	}

	protected boolean isCached() {
		return false;
	}

	protected JSONObject cacheMethod() throws JSONException {
		return null;
	}

	protected abstract JSONObject method() throws ServerAuthException, ServerGeneralException, LocalGeneralException,
			NetworkNotAvailableException, Exception;

	public void setDelegate(DataServiceDelegate delegate) {
		this.delegate = delegate;
	}

	/**
	 * Please do not call this method directly. It is only used in thread pool.
	 */
	@Override
	public void run() {

		JSONObject data;
		try {
			if (isCached()) {
				data = cacheMethod();
				if (data != null) {
					delegate.onRequestSucceeded(this, data, true);
				}
			}
			data = method();
			delegate.onRequestSucceeded(this, data,false);
		} catch (ServerAuthException | ServerGeneralException | LocalGeneralException | NetworkNotAvailableException e) {
			delegate.onRequestFailed(this, e);
		} catch (Exception e) {
			delegate.onRequestFailed(this, new LocalGeneralException(e.getMessage()));
		}
	}

}
