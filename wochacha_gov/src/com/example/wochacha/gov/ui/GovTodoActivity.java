package com.example.wochacha.gov.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.wochacha.gov.R;

public class GovTodoActivity extends Activity {
	public final static class IntentKey {
		public final static String TITLE = "title";
		public final static String URL = "url";
	}

	WebView webView;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);

		Intent intent = getIntent();
		String title = intent.getStringExtra(IntentKey.TITLE);
		if (title == null) {
			title = getString(R.string.todo);
		}
		String url = intent.getStringExtra(IntentKey.URL);
		if (url == null) {
			this.finish();
			return;
		}

		getActionBar().setTitle(title);

		setContentView(R.layout.activity_enterprise_todo);

		dialog = new ProgressDialog(this);
		dialog.setMessage(getString(R.string.loading));
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		webView = (WebView) findViewById(R.id.wv_content);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setDomStorageEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		//webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		
		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					dialog.dismiss();
				}
			}

		});
	/*	webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {

				super.onPageFinished(view, url);
				
				dialog.dismiss();
			}

		});*/
		webView.loadUrl(url);
	}

	@Override
	protected void onPause() {

		super.onPause();
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		webView.stopLoading();
		webView.removeAllViews();
		webView.destroy();
		webView = null;
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
