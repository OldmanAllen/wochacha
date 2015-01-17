package com.example.wochacha.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wochacha.R;
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
			ToastMessageHelper.showErrorMessage(this,
					R.string.nfc_not_supported, false);
			this.finish();
		}
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);

		ImageView rfidImage = (ImageView) findViewById(R.id.iv_rfid_animation);
		rfidAnimationDrawable = (AnimationDrawable) rfidImage.getBackground();
		tv_nfc_action_desc = (TextView) findViewById(R.id.tv_nfc_action_desc);
		handler = new Handler();

	}

	public static boolean IsNFCSupported(Activity activity) {
		S_IsNfcSupported = activity.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_NFC);

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
		Dialog dialog = new AlertDialog.Builder(this)
				.setMessage(R.string.nfc_enable_message)
				.setTitle(R.string.nfc_enable_title)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (android.os.Build.VERSION.SDK_INT >= 16) {
									startActivity(new Intent(
											Settings.ACTION_NFC_SETTINGS));
								} else {
									startActivity(new Intent(
											Settings.ACTION_WIRELESS_SETTINGS));
								}

							}
						}).setNegativeButton(R.string.no, null).create();
		dialog.show();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (nfcAdapter == null) {
			return;
		}
		if (android.os.Build.VERSION.SDK_INT >= 18) {
			this.registerReceiver(nfcReceiver, new IntentFilter(
					NfcAdapter.ACTION_ADAPTER_STATE_CHANGED));
		}
		if (nfcAdapter.isEnabled()) {
			tv_nfc_action_desc.setText(R.string.rfid_tip);
			startAnimation();
		} else {
			tv_nfc_action_desc.setText(R.string.rfid_enable);
			enableNFC();
			return;
		}
		enableForegroundDispatch();
	}

	@Override
	protected void onNewIntent(Intent intent) {

		super.onNewIntent(intent);

		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if (tagFromIntent != null) {
			parseTag(tagFromIntent);
		}
	}

	private void parseTag(Tag tag) {
		ReadTagTask task = new ReadTagTask();
		task.execute(tag);
	}

	public void enableForegroundDispatch() {

		/*
		 * if (android.os.Build.VERSION.SDK_INT < 19) { IntentFilter[] filters =
		 * new IntentFilter[] { new
		 * IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED), new
		 * IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED), new
		 * IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED) }; PendingIntent
		 * pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
		 * this.getClass()) .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		 * nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters,
		 * null); } else {
		 * 
		 * Bundle extras = new Bundle();
		 * extras.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 5000);
		 * 
		 * nfcAdapter.enableReaderMode(this, new NfcAdapter.ReaderCallback() {
		 * 
		 * @Override public void onTagDiscovered(Tag tag) {
		 * 
		 * parseTag(tag); }
		 * 
		 * }, 0, extras); }
		 */
		IntentFilter[] filters = new IntentFilter[] {
				new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
				new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
				new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED) };
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, this.getClass())
						.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);

	}

	private void disableForegroundDispatch() {
		nfcAdapter.disableForegroundDispatch(this);
		/*
		 * if (android.os.Build.VERSION.SDK_INT < 19) {
		 * nfcAdapter.disableForegroundDispatch(this); } else {
		 * nfcAdapter.disableReaderMode(this); }
		 */
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (nfcAdapter == null) {
			return;
		}
		if (android.os.Build.VERSION.SDK_INT >= 18) {
			unregisterReceiver(nfcReceiver);
		}
		disableForegroundDispatch();
	}

	// receiver
	private BroadcastReceiver nfcReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)) {

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

	class ReadTagTask extends AsyncTask<Tag, Void, String> {

		private final static int SUCCEEDED = 0;
		private final static int FAILED = 1;

		@Override
		protected String doInBackground(Tag... params) {

			try {
				Tag tag = params[0];

				List<String> techList = new ArrayList<String>();
				StringBuilder techListString = new StringBuilder();
				int index = 0;
				int length = tag.getTechList().length;
				for (String tech : tag.getTechList()) {
					techList.add(tech);
					techListString.append(tech);
					if (index < length - 1) {
						techListString.append("|");
					}
					index++;
				}

				if (techList.contains("android.nfc.tech.Ndef")) {
					Ndef ndef = Ndef.get(tag);
					ndef.connect();
					NdefMessage message = ndef.getNdefMessage();
					byte[] array = message.getRecords()[0].getPayload();
					ndef.close();
					if (array != null) {
						return new String(array);
					}
				}

				return techListString.toString();

			} catch (Exception e) {
				// TODO: handle exception
			}

			return "not support";

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			startAnimation();
			ToastMessageHelper.showErrorMessage(RFIDWaitingActivity.this,
					result, false);

		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			stopAnimation();
		}

	}
}
