package com.example.wochacha.gov.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wochacha.gov.R;
import com.example.wochacha.util.ToastMessageHelper;

public class RFIDWaitingActivity extends Activity {

	private AnimationDrawable rfidAnimationDrawable;
	private Handler handler;
	private NfcAdapter nfcAdapter;
	private static boolean S_IsNfcSupported = false;
	
	private TextView tv_nfc_action_desc;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rfid);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);

		getActionBar().setTitle("RFID");

		if (!IsNFCSupported(this)) {
			ToastMessageHelper.showErrorMessage(this, R.string.nfc_not_supported, false);
			this.finish();
		}
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);

		ImageView rfidImage = (ImageView)findViewById(R.id.iv_rfid_animation);
		rfidAnimationDrawable = (AnimationDrawable)rfidImage.getBackground();

		tv_nfc_action_desc = (TextView)findViewById(R.id.tv_nfc_action_desc);
		
		handler = new Handler();

		

	}

	public static boolean IsNFCSupported(Activity activity) {
		S_IsNfcSupported = activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC);

		return S_IsNfcSupported;
	}

	public void enableNFC() {
		if (!S_IsNfcSupported) {
			return;
		}

		if (nfcAdapter.isEnabled()) {
			return;
		}
		if (this.isFinishing()) {
			return;
		}
		Dialog dialog = new AlertDialog.Builder(this).setMessage(R.string.nfc_enable_message)
				.setTitle(R.string.nfc_enable_title)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (android.os.Build.VERSION.SDK_INT >= 16) {
							startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
						} else {
							startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
						}

					}
				}).setNegativeButton(R.string.no, null).create();
		dialog.show();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (android.os.Build.VERSION.SDK_INT >= 18) {
			this.registerReceiver(nfcReceiver, new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED));
		}
		if (nfcAdapter.isEnabled()) {
			tv_nfc_action_desc.setText(R.string.rfid_tip);
			startAnimation();
		} else {
			tv_nfc_action_desc.setText(R.string.rfid_enable);
			enableNFC();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (android.os.Build.VERSION.SDK_INT >= 18) {
			unregisterReceiver(nfcReceiver);
		}
	}

	// receiver
	private BroadcastReceiver nfcReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)) {

				RFIDWaitingActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (nfcAdapter.isEnabled()) {
							tv_nfc_action_desc.setText(R.string.rfid_tip);
							startAnimation();
						} else {
							tv_nfc_action_desc.setText(R.string.rfid_enable);
							stopAnimation();
						}
					}
				});
			}
		}
	};

	private void startAnimation() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				rfidAnimationDrawable.start();
			}
		}, 500);
	}

	private void stopAnimation() {
		if (rfidAnimationDrawable.isRunning())
			rfidAnimationDrawable.stop();
	}

	@Override
	protected void onStop() {
		stopAnimation();
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
