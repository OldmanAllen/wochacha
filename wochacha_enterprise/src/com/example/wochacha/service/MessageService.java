package com.example.wochacha.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.wochacha.exception.LocalGeneralException;
import com.example.wochacha.exception.NetworkNotAvailableException;
import com.example.wochacha.exception.ServerAuthException;
import com.example.wochacha.exception.ServerGeneralException;
import com.example.wochacha.network.CacheService;
import com.example.wochacha.util.StringHelper;

public class MessageService extends DataServiceImpl {

	private static final String MESSAGE_URL = "/message";
	private int pageIdx;

	public MessageService(int pageIndex) {
		pageIdx = pageIndex;
	}

	@Override
	protected JSONObject method() throws ServerAuthException,
			ServerGeneralException, LocalGeneralException,
			NetworkNotAvailableException, Exception {
		String url = MESSAGE_URL + "/";

		// JSONObject object = RequestManager.GetWithURL(url);

		Thread.currentThread().sleep(1000);

		
		JSONObject object = new JSONObject();
		String dataString = CacheService.getCacheServiceInstance()
				.getObjectFromCache(url, true);
		if (StringHelper.isStringNullOrEmpty(dataString)) {
			String jsontest = "[{\"company_id\":1, \"message_count\":1,\"company_name\":\"任志强公�?�1\", \"image_url\":\"http://tp2.sinaimg.cn/1182389073/50/1283203476/1\"},{\"company_id\":2, \"message_count\":1,\"company_name\":\"任志强公�?�2\", \"image_url\":\"http://tp2.sinaimg.cn/1182389073/50/1283203476/1\"}]";

			JSONArray array = new JSONArray(jsontest);

			
			object.put("array", array);

			CacheService.getCacheServiceInstance().refreshCache(url, object);
		}	
		
		return object;
	}

	@Override
	protected boolean isCached() {
		return true;
	}

	@Override
	protected JSONObject cacheMethod() throws JSONException {
		String url = MESSAGE_URL + "/" + pageIdx;
		JSONObject object = null;
		String dataString = CacheService.getCacheServiceInstance()
				.getObjectFromCache(url, true);
		if (StringHelper.isStringNullOrEmpty(dataString)) {
			return null;
		}
		object = new JSONObject(dataString);
		return object;
	}

}
