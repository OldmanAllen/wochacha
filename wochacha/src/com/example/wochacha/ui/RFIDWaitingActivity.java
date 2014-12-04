package com.example.wochacha.ui;

import com.example.wochacha.R;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.ImageView;

public class RFIDWaitingActivity extends Activity {

	
	private AnimationDrawable rfidAnimationDrawable;
	private Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rfid);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);

		getActionBar().setTitle("RFID");
		
		ImageView rfidImage = (ImageView)findViewById(R.id.iv_rfid_animation);
		rfidAnimationDrawable = (AnimationDrawable)rfidImage.getBackground();
		
		handler = new Handler();
		
	}
	
	@Override
	protected void onResume() {
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				rfidAnimationDrawable.start();
			}
		}, 500);
		
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		if (rfidAnimationDrawable.isRunning())
			rfidAnimationDrawable.stop();
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
