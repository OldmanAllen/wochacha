package com.example.wochacha.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.example.wochacha.R;
import com.example.wochacha.ui.RFIDWaitingActivity;
import com.example.wochacha.ui.ScanResultActivity;
import com.example.wochacha.util.Constants;
import com.example.wochacha.util.DensityUtil;
import com.example.wochacha.util.StringHelper;
import com.example.wochacha.util.ToastMessageHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.camera.PlanarYUVLuminanceSource;
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
	private static final int REQUEST_IMAGE = HomeFragment.class.hashCode();

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

	private CheckBox cb_light;

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
		// inflater.inflate(R.menu.main, menu);

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

		cb_light = (CheckBox) rootView.findViewById(R.id.cb_light);
		cb_light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					CameraManager.get().enableFlashLight();
				} else {
					CameraManager.get().disableFlashLight();
				}

			}
		});

		View iv_load_image = rootView.findViewById(R.id.iv_load_image);
		iv_load_image.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getImageFromLocal();
			}
		});

		hasSurface = false;
		// inactivityTimer = new InactivityTimer(this.getActivity());

		return rootView;
	}

	protected void getImageFromLocal() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, REQUEST_IMAGE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
			// uri

			ImageDecoder decoder = new ImageDecoder(getActivity());
			decoder.execute(data.getData());
		}

	}

	private class ImageDecoder extends AsyncTask<Uri, Void, String> {

		Activity activity;
		ProgressDialog progressDialog;
		private Bitmap bitmap;

		public ImageDecoder(Activity activity) {
			this.activity = activity;
			progressDialog = new ProgressDialog(activity);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setMessage(activity
					.getString(R.string.decoding_image));
		}

		@Override
		protected String doInBackground(Uri... params) {
			try {

				Uri uri = params[0];
				InputStream in = activity.getContentResolver().openInputStream(
						uri);

				Bitmap bitmap = BitmapFactory.decodeStream(in);

				Rect rect = CameraManager.get().getFramingRect();
				Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
						rect.right - rect.left, rect.bottom - rect.top, true);
				// scaledBitmap.setConfig(Bitmap.Config.ARGB_8888);
				this.bitmap = scaledBitmap;

				int[] array = new int[scaledBitmap.getWidth()
						* scaledBitmap.getHeight()];

				scaledBitmap.getPixels(array, 0, scaledBitmap.getWidth(), 0, 0,
						scaledBitmap.getWidth(), scaledBitmap.getHeight());

				RGBLuminanceSource source = new RGBLuminanceSource(
						scaledBitmap.getWidth(), scaledBitmap.getHeight(),
						array);

				BinaryBitmap binaryBitmap = new BinaryBitmap(
						new HybridBinarizer(source));
				MultiFormatReader reader = new MultiFormatReader();
				HashMap<DecodeHintType, Object> hints = new HashMap<>();
				hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
				Result result = reader.decode(binaryBitmap, hints);
				return result.getText();

			} catch (Exception e) {

				try {
					throw e;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			progressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			progressDialog.dismiss();

			if (result != null) {
				
				CameraManager.get().stopPreview();
				handler.markAsSuccess();
				verifyCode(result, bitmap);
			} else {
				ToastMessageHelper.showErrorMessage(activity,
						R.string.decoding_failed, false);
			}

		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
		 * if (item.getItemId() == R.id.action_scan) { if (callback != null) {
		 * callback.onDrawerToogleClicked(); } }
		 */
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

		verifyCode(result.getText(), barcode);

	}

	private void verifyCode(final String result, Bitmap barcode) {
		if (StringHelper.isStringNullOrEmpty(result)) {
			return;
		}
		playVibrate();

		Log.e("qr result", result);
		if (!result.contains(Constants.CODE_BASE_URL)) {
			/*
			 * ToastMessageHelper.showErrorMessage(this.getActivity(),
			 * R.string.scan_failed, false);
			 */
			
			CameraManager.get().stopPreview();
			viewfinderView.drawResultBitmap(barcode);
			AlertDialog dialog = new AlertDialog.Builder(getActivity())
					.setMessage(result)
					.setCancelable(false)
					.setPositiveButton(R.string.copy,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									CameraManager.get().startPreview();
									handler.restartPreviewAndDecode();

									ClipboardManager clipboardManager = (ClipboardManager) getActivity()
											.getSystemService(
													Context.CLIPBOARD_SERVICE);
									clipboardManager.setText(result);
									ToastMessageHelper.showErrorMessage(
											getActivity(),
											R.string.copy_succeeded, false);

								}
							})
					.setNegativeButton(R.string.no,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									CameraManager.get().startPreview();
									handler.restartPreviewAndDecode();
								}
							}).create();
			dialog.show();

		} else {

			Intent resultIntent = new Intent(this.getActivity(),
					ScanResultActivity.class);
			Bundle bundle = new Bundle();
			// TODO demo purpose
			bundle.putString(ScanResultActivity.IntentKey.RESULT,
					result.replace(Constants.CODE_BASE_URL, ""));
			// bundle.putString(ScanResultActivity.IntentKey.RESULT,
			// resultString);
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