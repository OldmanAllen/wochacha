package com.example.wochacha.ui;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wochacha.R;
import com.example.wochacha.entity.ScanRequest;
import com.example.wochacha.entity.ScanResult;
import com.example.wochacha.entity.UserLocation;
import com.example.wochacha.exception.BaseException;
import com.example.wochacha.manager.DeviceGeoLocationManager;
import com.example.wochacha.manager.DeviceManager;
import com.example.wochacha.service.DataServiceImpl;
import com.example.wochacha.service.VerifyCodeService;
import com.example.wochacha.service.DataServiceImpl.DataServiceDelegate;
import com.example.wochacha.util.ToastMessageHelper;

public class ScanResultActivity extends Activity implements DataServiceDelegate {

	ImageView iv_thumbnail;
	TextView tv_original_info;

	View rl_scan_manufacture_detail;
	View ll_scan_details;
	WebView webView;
	ProgressDialog progressDialog;
	VerifyCodeService service;

	private ScanResult scanResult = null;

	public static class IntentKey {
		public final static String RESULT = "result";
		public final static String THUMBNAIL = "thumbnail";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setTitle(R.string.scan_result);

		setContentView(R.layout.activity_scan_result);

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getString(R.string.verify_code_loading));
		progressDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						if (service != null) {
							service.cancel();
						}
					}
				});

		iv_thumbnail = (ImageView) findViewById(R.id.iv_thumbnail);
		// tv_original_info = (TextView) findViewById(R.id.tv_original_info);
		rl_scan_manufacture_detail = findViewById(R.id.rl_scan_manufacture_detail);
		ll_scan_details = findViewById(R.id.ll_scan_details);

		webView = (WebView) findViewById(R.id.wv_content);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
	
		webView.setWebChromeClient(new WebChromeClient() {

		});

		Intent intent = getIntent();
		if (intent == null || (intent.getStringExtra(IntentKey.RESULT) == null)) {
			finish();
			return;
		}

		String result = intent.getStringExtra(IntentKey.RESULT);

		VerfiyCode(result);
		// qr code

		// Bitmap bitmap = CameraManager.get().getThumbnail();

		webView.loadUrl("http://biz.cli.im/test/CI25850");

	}

	private void VerfiyCode(String result) {
		ScanRequest request = new ScanRequest();
		DeviceManager deviceMgr = DeviceManager.getInstance();
		request.setDeviceId(deviceMgr.getDeviceId());
		request.setDeviceModel(deviceMgr.getDeviceModel());
		request.setOsVersion(deviceMgr.getDeviceSDKVersion());

		DeviceGeoLocationManager geoMgr = DeviceGeoLocationManager
				.getInstance();
		UserLocation location = geoMgr.getCurrentLocation();

		request.setLat(location.getLatitude());
		request.setLng(location.getLongitude());

		VerifyCodeService service = new VerifyCodeService(result, request);
		service.setDelegate(this);
		progressDialog.show();
		service.start();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void onExpandOrCollapseClicked(View view) {

		int id = view.getId();
		if (id == R.id.iv_expand_or_collapse) {
			toggle(rl_scan_manufacture_detail);
		} else {
			toggle(ll_scan_details);
		}

	}

	private void toggle(View view) {
		if (view.getVisibility() == View.GONE) {
			view.setVisibility(View.VISIBLE);
			//
		} else {
			view.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void refreshUI() {
		if (scanResult != null && scanResult.getManufacturer() != null) {

		}

	}

	@Override
	public void onRequestSucceeded(DataServiceImpl service,
			final JSONObject data, boolean isCached) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				scanResult = new ScanResult();
				progressDialog.dismiss();
				scanResult.populate(data);
				refreshUI();

			}
		});
	}

	@Override
	public void onRequestFailed(DataServiceImpl service, BaseException exception) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				progressDialog.dismiss();
				ToastMessageHelper.showErrorMessage(ScanResultActivity.this,
						R.string.load_failed, true);

			}
		});

	}

}
