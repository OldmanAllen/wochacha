package com.example.wochacha.gov.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.wochacha.gov.R;

public class TodoActivity extends Activity {
	public final static class IntentKey {
		public final static String TITLE = "title";
		public final static String RESOURCE_ID = "resource_id";
	}

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
		getActionBar().setTitle(title);

		int content_layout_id = intent.getIntExtra(IntentKey.RESOURCE_ID, R.layout.activity_todo);
		setContentView(content_layout_id);
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
