package com.example.wochacha.ui;

import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.wochacha.R;
import com.example.wochacha.util.DensityUtil;
import com.example.wochacha.util.ToastMessageHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.view.ViewfinderView;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends FragmentBase implements
		SurfaceHolder.Callback {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	private HomeFragmentCallback callback;
	private ViewfinderView viewfinderView;
	private SurfaceView surfaceView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private CaptureActivityHandler handler;
	private boolean vibrate;
	private ImageView iv_rfid;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static HomeFragment newInstance(int sectionNumber) {
		HomeFragment fragment = new HomeFragment();
		/*
		 * Bundle args = new Bundle(); args.putInt(ARG_SECTION_NUMBER,
		 * sectionNumber); fragment.setArguments(args);
		 */
		return fragment;
	}

	public HomeFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		CameraManager.init(getActivity().getApplication());

		CameraManager.get().setTopOffset(getScanViewTopOffset());

		super.onActivityCreated(savedInstanceState);
	}

	private int getScanViewTopOffset() {
		int scanInfoHeight = getResources().getDimensionPixelSize(
				R.dimen.scan_info_area_height);
		int actionBarHeight = DensityUtil
				.getActionBarHeight(this.getActivity());
		int statusBarHeight = DensityUtil.getStatusBarHeight1(this
				.getActivity());
		return scanInfoHeight + actionBarHeight + statusBarHeight;

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_scan, container,
				false);
		viewfinderView = (ViewfinderView) rootView
				.findViewById(R.id.viewfinder_view);
		surfaceView = (SurfaceView) rootView.findViewById(R.id.preview_view);

		iv_rfid = (ImageView) rootView.findViewById(R.id.iv_rfid);
		iv_rfid.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeFragment.this.getActivity(),
						RFIDWaitingActivity.class);
				startActivity(intent);

			}
		});

		hasSurface = false;
		// inactivityTimer = new InactivityTimer(this.getActivity());

		return rootView;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_scan) {
			if (callback != null) {
				callback.onDrawerToogleClicked();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (visible) {
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			if (hasSurface) {
				initCamera(surfaceHolder);
			} else {
				surfaceHolder.addCallback(this);
				surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			}
			decodeFormats = null;
			characterSet = null;
			vibrate = true;

		}

	}

	@Override
	public void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	public void handleDecode(Result result, Bitmap barcode) {
		// inactivityTimer.onActivity();
		playVibrate();
		String resultString = result.getText();
		if (resultString.equals("")) {
			ToastMessageHelper.showErrorMessage(this.getActivity(),
					R.string.scan_failed, false);

		} else {
			Intent resultIntent = new Intent(this.getActivity(),
					ScanResultActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(ScanResultActivity.IntentKey.RESULT, resultString);
			// bundle.putParcelable(ScanResultActivity.IntentKey.THUMBNAIL,
			// barcode);
			resultIntent.putExtras(bundle);
			CameraManager.get().setThumbnail(barcode);
			startActivity(resultIntent);

		}

	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playVibrate() {
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getActivity().getSystemService(
					Context.VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof HomeFragmentCallback) {
			callback = (HomeFragmentCallback) activity;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// inactivityTimer.shutdown();
	}

	public interface HomeFragmentCallback {
		public void onDrawerToogleClicked();
	}

	@Override
	protected String getTitle() {
		return getString(R.string.tab_scan);
	}

}