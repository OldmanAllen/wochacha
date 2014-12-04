package com.example.wochacha.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import com.example.wochacha.R;

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
				Intent intent = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				finish();

			}
		}, 1000);

		

	}
}
