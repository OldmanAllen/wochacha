package com.example.wochacha.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.GetChars;
import android.util.EventLogTags.Description;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.wochacha.R;
import com.example.wochacha.entity.Message;
import com.example.wochacha.entity.ScanRequest;
import com.example.wochacha.entity.ScanResult;
import com.example.wochacha.entity.ScanResult.Manufacturer;
import com.example.wochacha.entity.ScanResult.ScanProduct;
import com.example.wochacha.entity.UserLocation;
import com.example.wochacha.exception.BaseException;
import com.example.wochacha.exception.NetworkNotAvailableException;
import com.example.wochacha.fragment.ProductInfoFragment;
import com.example.wochacha.fragment.ProductPathInfoFragment;
import com.example.wochacha.fragment.ProductScanInfoFragment;
import com.example.wochacha.manager.DeviceGeoLocationManager;
import com.example.wochacha.manager.DeviceManager;
import com.example.wochacha.manager.MessageManager;
import com.example.wochacha.network.ImageManager;
import com.example.wochacha.network.ImageViewInfo;
import com.example.wochacha.service.DataServiceImpl;
import com.example.wochacha.service.DataServiceImpl.DataServiceDelegate;
import com.example.wochacha.service.VerifyCodeService;
import com.example.wochacha.util.DensityUtil;
import com.example.wochacha.util.ToastMessageHelper;
import com.example.wochacha.view.MyScrollView;
import com.viewpagerindicator.TabPageIndicator;

public class ScanResultActivity extends Activity implements DataServiceDelegate {

	ImageView iv_thumbnail;
	TextView tv_company_name;

	ImageView iv_product_icon;
	ImageView iv_result_status;
	TextView tv_scan_status;
	TextView tv_scan_desc;

	// detail
	TextView tv_product_barcode_value;
	TextView tv_product_name_value;
	TextView tv_product_desc_value;

	// View rl_scan_manufacture_detail;
	View ll_scan_details;

	ProgressDialog progressDialog;
	TabPageIndicator indicator;
	TabPageIndicator indicator2;
	ViewPager vp_scan_details;
	MyScrollView sv_content;
	LinearLayout ll_content;

	VerifyCodeService service;

	private static HashMap<String, Integer> statusIconLookupHashMap = new HashMap<>();

	private ScanResult scanResult = null;

	public static class IntentKey {
		public final static String RESULT = "result";
		public final static String THUMBNAIL = "thumbnail";
	}

	static {
		// TODO: hash mapping
		statusIconLookupHashMap.put("NORMAL", R.drawable.ic_zhengpin);
		statusIconLookupHashMap.put("FAKE", R.drawable.ic_jiahuo);
		statusIconLookupHashMap.put("RISK", R.drawable.ic_jinggao);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setTitle(R.string.scan_result);

		setContentView(R.layout.activity_scan_result);

		progressDialog = new ProgressDialog(this);

		progressDialog.setCanceledOnTouchOutside(false);
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
		sv_content = (MyScrollView) findViewById(R.id.sv_content);
		
		ll_content = (LinearLayout)findViewById(R.id.ll_content);

		iv_thumbnail = (ImageView) findViewById(R.id.iv_thumbnail);
		tv_company_name = (TextView) findViewById(R.id.tv_company_name);

		iv_product_icon = (ImageView) findViewById(R.id.iv_product_icon);
		iv_result_status = (ImageView) findViewById(R.id.iv_result_status);
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		tv_scan_desc = (TextView) findViewById(R.id.tv_scan_desc);
		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator2 = (TabPageIndicator) findViewById(R.id.indicatorTop);
		
		
		vp_scan_details = (ViewPager) findViewById(R.id.vp_scan_details);

		Intent intent = getIntent();
		if (intent == null || (intent.getStringExtra(IntentKey.RESULT) == null)) {
			finish();
			return;
		}

		String result = intent.getStringExtra(IntentKey.RESULT);
		VerfiyCode(result);

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
		findViewById(R.id.sv_content).setVisibility(View.GONE);
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
		/*
		 * if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
		 * webView.goBack(); return true; }
		 */
		return super.onKeyDown(keyCode, event);
	}

	protected void refreshUI() {
		if (scanResult.isDataValid()) {
			findViewById(R.id.sv_content).setVisibility(View.VISIBLE);
			MessageManager manager = MessageManager.getInstance();
			Manufacturer manufacturer = scanResult.getManufacturer();
			int manufacturerId = manufacturer.getId();
			Message message = manager
					.getMessageByManufacturerId(manufacturerId);
			if (message != null) {
				manager.updateMessageWithManufacturerInfo(message, manufacturer);
			} else {

				Message newMessage = new Message();
				newMessage.setCompanyName(manufacturer.getName());
				newMessage.setCompanyId(manufacturer.getId());
				newMessage.setIconUrl(manufacturer.getImageUri());
				newMessage.setNewMessageCount(1);
				MessageManager.getInstance().pushNewNotification(newMessage);
			}

			setupManufacturerUI();
			setupResultUI();

		} else {
			ToastMessageHelper.showErrorMessage(ScanResultActivity.this,
					R.string.load_failed, true);
			ScanResultActivity.this.finish();
		}

	}

	private void setupWebView() {
		/*
		 * String url = scanResult.getUrl();
		 * 
		 * if (!StringHelper.isStringNullOrEmpty(url)) {
		 * webView.setVisibility(View.VISIBLE); webView.loadUrl(url); } else {
		 * webView.setVisibility(View.GONE); }
		 */

	}

	private void setupResultUI() {
		ScanProduct product = scanResult.getProduct();

		Integer resourceId = statusIconLookupHashMap.get(product
				.getProductStatusCode());

		// TODO: we need image url from product type
		// product.getProductType().getImageUri();
		iv_product_icon.setImageResource(R.drawable.demo_scan);
		iv_result_status
				.setImageResource((resourceId == null ? R.drawable.ic_jinggao
						: resourceId.intValue()));
		tv_scan_status.setText(product.getProductStatusDescription());
		tv_scan_desc.setText(product.getProductInformation());
		setupResultDetail(scanResult);

	}

	private void setupResultDetail(ScanResult scanResult) {

		List<Fragment> list = new ArrayList<Fragment>();

		ProductInfoFragment infoFragment = new ProductInfoFragment();
		infoFragment.setData(scanResult.getProduct());
		list.add(infoFragment);

		ProductScanInfoFragment scanFragment = new ProductScanInfoFragment();
		scanFragment.setData(scanResult.getScanRecord());
		list.add(scanFragment);

		ProductPathInfoFragment pathFragment = new ProductPathInfoFragment();
		pathFragment.setData(scanResult.getPathList());
		list.add(pathFragment);

		final FragmentPagerAdapter adapter = new SectionsPagerAdapter(
				getFragmentManager(), list);

		vp_scan_details.setAdapter(adapter);
		indicator2.setViewPager(vp_scan_details, 0);
		indicator.setViewPager(vp_scan_details, 0);
		indicator.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				indicator2.setCurrentItem(arg0);
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		sv_content.setFixedPosition(DensityUtil.dip2px(this, 240));
		sv_content.setFixedHeaderView(indicator2);
		

	}

	private void setupManufacturerUI() {
		Manufacturer manufacturer = scanResult.getManufacturer();
		String manufacturerImageUri = manufacturer.getImageUri();
		ImageViewInfo info = new ImageViewInfo(manufacturerImageUri, 0);
		iv_thumbnail.setTag(info);
		ImageManager.getInstance().displayImage(manufacturerImageUri, this,
				iv_thumbnail);

		tv_company_name.setText(manufacturer.getName());

		if (!manufacturer.isVerified()) {
			findViewById(R.id.v_divider).setVisibility(View.INVISIBLE);
			findViewById(R.id.rl_verify_group).setVisibility(View.GONE);
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
	public void onRequestFailed(DataServiceImpl service,
			final BaseException exception) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO if the status is 404, then make a ui change.
				// Log.e("onRequestFailed", exception.getMessage());
				progressDialog.dismiss();
				if (exception instanceof NetworkNotAvailableException) {
					ToastMessageHelper.showErrorMessage(
							ScanResultActivity.this,
							R.string.network_not_available, true);
					ScanResultActivity.this.finish();
				} else {
					ToastMessageHelper
							.showErrorMessage(ScanResultActivity.this,
									R.string.load_failed, true);
					ScanResultActivity.this.finish();
				}

			}
		});

	}

	public void setViewPagerHeight(int height) {

		int contentViewSize = DensityUtil.getScreenHeightAndWidth(this)[1]
				- DensityUtil.getActionBarHeight(this)
				- DensityUtil.getStatusBarHeight1(this);

		int otherHeight = contentViewSize - DensityUtil.dip2px(this, 40);
		int contentHeight = height + DensityUtil.dip2px(this, 240);
		//ll_content.getLayoutParams().height = contentHeight;
		if (height < otherHeight)
		{
			height = otherHeight;
		}
		
		ViewGroup.LayoutParams params = vp_scan_details.getLayoutParams();
		params.height = height;

	}

	private class SectionsPagerAdapter extends
			android.support.v13.app.FragmentPagerAdapter {

		private List<Fragment> fragmentList;
		int[] titleRes = new int[] { R.string.product_detail_basic,
				R.string.product_scan_info, R.string.product_tracking };

		public SectionsPagerAdapter(FragmentManager fm,
				Collection<Fragment> list) {
			super(fm);
			fragmentList = new ArrayList<>();
			Iterator<Fragment> iterator = list.iterator();
			while (iterator.hasNext()) {
				Fragment fragment = (Fragment) iterator.next();
				fragmentList.add(fragment);
			}

		}

		@Override
		public Fragment getItem(int position) {
			return fragmentList.get(position % 3);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getString(titleRes[position % 3]);
		}

		@Override
		public int getCount() {

			return fragmentList.size();
		}
	}

}
