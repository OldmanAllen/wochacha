package com.example.wochacha.gov.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.example.wochacha.gov.R;

public class SplashActivity extends android.app.Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageView imageView = new ImageView(this);
		imageView.setImageResource(R.drawable.welcome);

		setContentView(imageView);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this, GovActivity.class);
				startActivity(intent);
				finish();

			}
		}, 1000);

		

	}
}
