package com.example.wochacha.enterprise.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.wochacha.enterprise.R;

public class EnterpriseTodoActivity extends Activity {
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
		dialog.show();

		webView = (WebView)findViewById(R.id.wv_content);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					dialog.dismiss();
				}
			}

		});
		webView.loadUrl(url);
	}

	@Override
	protected void onStop() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
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
