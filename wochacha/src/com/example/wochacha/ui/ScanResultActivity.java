package com.example.wochacha.ui;

import java.util.HashMap;

import org.json.JSONObject;
import org.w3c.dom.Text;

import android.R.string;
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
import com.example.wochacha.entity.ScanResult.Manufacturer;
import com.example.wochacha.entity.ScanResult.ScanProduct;
import com.example.wochacha.entity.ScanResult.ScanProductType;
import com.example.wochacha.entity.UserLocation;
import com.example.wochacha.exception.BaseException;
import com.example.wochacha.manager.DeviceGeoLocationManager;
import com.example.wochacha.manager.DeviceManager;
import com.example.wochacha.network.ImageManager;
import com.example.wochacha.network.ImageViewInfo;
import com.example.wochacha.service.DataServiceImpl;
import com.example.wochacha.service.VerifyCodeService;
import com.example.wochacha.service.DataServiceImpl.DataServiceDelegate;
import com.example.wochacha.util.StringHelper;
import com.example.wochacha.util.ToastMessageHelper;

public class ScanResultActivity extends Activity implements DataServiceDelegate {

	ImageView iv_thumbnail;
	TextView tv_company_name;

	ImageView iv_result_icon;
	TextView tv_scan_status;
	TextView tv_scan_desc;

	// detail
	TextView tv_product_barcode_value;
	TextView tv_product_name_value;
	TextView tv_product_desc_value;

	// View rl_scan_manufacture_detail;
	View ll_scan_details;
	WebView webView;
	ProgressDialog progressDialog;
	VerifyCodeService service;

	private static HashMap<String, Integer> statusIconLookupHashMap = new HashMap<>();

	private ScanResult scanResult = null;

	public static class IntentKey {
		public final static String RESULT = "result";
		public final static String THUMBNAIL = "thumbnail";
	}

	static {
		// TODO: hash mapping
		statusIconLookupHashMap.put("NORMAL", R.drawable.icon_accept);
		statusIconLookupHashMap.put("Fake", R.drawable.icon_reject);
		statusIconLookupHashMap.put("RISK", R.drawable.icon_alert);
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
		progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (service != null) {
					service.cancel();
				}
			}
		});

		iv_thumbnail = (ImageView)findViewById(R.id.iv_thumbnail);
		tv_company_name = (TextView)findViewById(R.id.tv_company_name);

		iv_result_icon = (ImageView)findViewById(R.id.iv_result_icon);
		tv_scan_status = (TextView)findViewById(R.id.tv_scan_status);
		tv_scan_desc = (TextView)findViewById(R.id.tv_scan_desc);

		// rl_scan_manufacture_detail = findViewById(R.id.rl_scan_manufacture_detail);
		ll_scan_details = findViewById(R.id.ll_scan_details);
		tv_product_barcode_value = (TextView)findViewById(R.id.tv_product_barcode_value);
		tv_product_name_value = (TextView)findViewById(R.id.tv_product_name_value);
		tv_product_desc_value = (TextView)findViewById(R.id.tv_product_desc_value);

		webView = (WebView)findViewById(R.id.wv_content);
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

		webView.setVisibility(View.GONE);

	}

	private void VerfiyCode(String result) {
		ScanRequest request = new ScanRequest();
		DeviceManager deviceMgr = DeviceManager.getInstance();
		request.setDeviceId(deviceMgr.getDeviceId());
		request.setDeviceModel(deviceMgr.getDeviceModel());
		request.setOsVersion(deviceMgr.getDeviceSDKVersion());

		DeviceGeoLocationManager geoMgr = DeviceGeoLocationManager.getInstance();
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

		// int id = view.getId();

		toggle(ll_scan_details);

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
		if (scanResult.isDataValid()) {
			setupManufacturerUI();
			setupResultUI();
			setupWebView();

		} else {
			ToastMessageHelper.showErrorMessage(ScanResultActivity.this, R.string.load_failed, true);
			ScanResultActivity.this.finish();
		}

	}

	private void setupWebView() {
		String url = scanResult.getUrl();

		if (!StringHelper.isStringNullOrEmpty(url)) {
			webView.setVisibility(View.VISIBLE);
			webView.loadUrl(url);
		} else {
			webView.setVisibility(View.GONE);
		}

	}

	private void setupResultUI() {
		ScanProduct product = scanResult.getProduct();

		iv_result_icon.setImageResource(statusIconLookupHashMap.get(product.getProductStatus()));
		tv_scan_status.setText(R.string.demo_scan_result);
		tv_scan_desc.setText(product.getProductStatusDescription());
		setupResultDetail(product);
	}

	private void setupResultDetail(ScanProduct product) {
		ScanProductType productType = product.getProductType();
		tv_product_barcode_value.setText(productType.getBarCode());
		tv_product_name_value.setText(productType.getProductName());
		tv_product_desc_value.setText(productType.getDescription());
	}

	private void setupManufacturerUI() {
		Manufacturer manufacturer = scanResult.getManufacturer();
		String manufacturerImageUri = manufacturer.getImageUri();
		ImageViewInfo info = new ImageViewInfo(manufacturerImageUri, 0);
		iv_thumbnail.setTag(info);
		ImageManager.getInstance().displayImage(manufacturerImageUri, this, iv_thumbnail);

		tv_company_name.setText(manufacturer.getName());

		if (!manufacturer.isVerified()) {
			findViewById(R.id.v_divider).setVisibility(View.INVISIBLE);
			findViewById(R.id.rl_verify_group).setVisibility(View.GONE);
		}
	}

	@Override
	public void onRequestSucceeded(DataServiceImpl service, final JSONObject data, boolean isCached) {
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
				//TODO if the status is 404, then make a ui change.
				progressDialog.dismiss();
				ToastMessageHelper.showErrorMessage(ScanResultActivity.this, R.string.load_failed, true);
				ScanResultActivity.this.finish();

			}
		});

	}

}
