package com.example.wochacha.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.example.wochacha.gov.R;
import com.example.wochacha.util.Constants;
import com.example.wochacha.util.KeyHelper;

public class ImageManager {

	public static final int FIT_TO_SCREEN = 1;

	// Just using a hashmap for the cache. SoftReferences would
	// be better, to avoid potential OutOfMemory exceptions
	private ImageHashMap imageMap = new ImageHashMap();

	private File cacheDir;
	private ImageQueue imageQueue = new ImageQueue();
	private Thread imageLoaderThread = new Thread(new ImageQueueManager());

	private static ImageManager imageManager = null;

	private Bitmap loadingBitmap = null;

	public ImageManager(Context context) {
		initImageManager(context);
	}

	public File getCacheDir() {
		return cacheDir;
	}

	public void recycleBitmapByUrl(String sUrl, String sActivityName, boolean bRecycleBitmap) {
		imageMap.remove(sUrl, sActivityName, bRecycleBitmap);
	}

	public void loadBitmap(String sUrl, String sActivityName) {
		String filename = KeyHelper.getMD5String(sUrl);
		File f = new File(cacheDir, filename);

		if (f.exists()) {
			BitmapInc inc = imageMap.get(sUrl);
			if (inc != null && inc.bitmap != null && !inc.bitmap.isRecycled())
				return;

			Bitmap bitmap = BitmapFactory.decodeFile(f.getPath());
			if (bitmap != null) {
				imageMap.addRef(sUrl, sActivityName);
				imageMap.put(sUrl, bitmap);
			}
		}

	}

	private void initImageManager(Context context) {
		// Make background thread low priority, to avoid affecting UI
		// performance
		imageLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
		String packageName = context.getPackageName();
		// Find the dir to save cached images
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();
			cacheDir = new File(sdDir, packageName + Constants.Cache.CACHE_SDCARD_IMAGE_PATH);
		} else
			cacheDir = context.getCacheDir();

		try {
			if (!cacheDir.exists()) {
				boolean bCreateSucc = cacheDir.mkdirs();
				Log.d("Debug", "Create the directory " + bCreateSucc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		loadingBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.loading);
	}

	public synchronized static ImageManager initializeIntance(Context context) {
		if (imageManager == null) {
			imageManager = new ImageManager(context);
		}
		return imageManager;
	}

	public static ImageManager getInstance() {
		if (imageManager == null) {
			Log.w("SessionManager", "SessionManager instance has not been initialized");
		}
		return imageManager;
	}

	public void displayImage(String url, Activity activity, ImageView imageView) {
		imageMap.addRef(url, activity.getLocalClassName());

		BitmapInc inc = imageMap.get(url);
		if (inc != null && inc.bitmap != null && !inc.bitmap.isRecycled()) {
			imageView.setImageBitmap(inc.bitmap);
		} else {
			queueImage(url, activity, imageView);
			imageView.setImageResource(R.drawable.loading);
		}
	}

	public void displayImage(String url, Activity activity, ImageView imageView, int fitStyle) {
		BitmapInc inc = imageMap.get(url);
		imageMap.addRef(url, activity.getLocalClassName());

		if (inc != null && inc.bitmap != null && !inc.bitmap.isRecycled()) {
			imageView.setImageBitmap(inc.bitmap);
			if (fitStyle == FIT_TO_SCREEN) {
				// ZoomImageView zoomImageView = (ZoomImageView)imageView;
				// zoomImageView.setLoadingFinished();
				// zoomImageView.initZoomImageParams(inc.bitmap);
			}
		} else {
			queueImage(url, activity, imageView, fitStyle);
			imageView.setImageResource(R.drawable.loading);
			if (fitStyle == FIT_TO_SCREEN) {
				// ZoomImageView zoomImageView = (ZoomImageView)imageView;
				// zoomImageView.initZoomImageParams(loadingBitmap);
			}
		}
	}

	private void queueImage(String url, Activity activity, ImageView imageView) {
		// This ImageView might have been used for other images, so we clear
		// the queue of old tasks before starting.
		synchronized (imageQueue.imageRefs) {
			imageQueue.Clean(imageView);
			ImageRef p = new ImageRef(url, imageView);
			imageQueue.imageRefs.push(p);
			imageQueue.imageRefs.notifyAll();
		}

		// Start thread if it's not started yet
		if (imageLoaderThread.getState() == Thread.State.NEW)
			imageLoaderThread.start();
	}

	private void queueImage(String url, Activity activity, ImageView imageView, int fitstyle) {
		// This ImageView might have been used for other images, so we clear
		// the queue of old tasks before starting.
		synchronized (imageQueue.imageRefs) {
			imageQueue.Clean(imageView);
			ImageRef p = new ImageRef(url, imageView, fitstyle);
			imageQueue.imageRefs.push(p);
			imageQueue.imageRefs.notifyAll();
		}

		// Start thread if it's not started yet
		if (imageLoaderThread.getState() == Thread.State.NEW)
			imageLoaderThread.start();
	}

	private Bitmap getBitmap(String url) {
		InputStream input = null;
		try {
			String filename = KeyHelper.getMD5String(url);
			File f = new File(cacheDir, filename);
			Bitmap bitmap = null;

			// Is the bitmap in our cache?
			/*
			 * if (!f.exists() ) { f.createNewFile(); // Nope, have to download it } else { bitmap =
			 * BitmapFactory.decodeFile(f.getPath()); if (bitmap != null) return bitmap; } input = getImageByURL(url);
			 * bitmap = BitmapFactory.decodeStream(input); writeFile(bitmap, f);
			 */
			if (f.exists()) {
				bitmap = BitmapFactory.decodeFile(f.getPath());
				if (bitmap != null)
					return bitmap;
			}
			input = getImageByURL(url);
			bitmap = BitmapFactory.decodeStream(input);
			if (!f.exists())
				f.createNewFile();
			writeFile(bitmap, f);
			return bitmap;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}

	private static InputStream getImageByURL(String url) throws MalformedURLException, IOException {
		HttpGet httpRequest = new HttpGet(URI.create(url));
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = (HttpResponse)httpclient.execute(httpRequest);
		HttpEntity entity = response.getEntity();
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		InputStream instream = bufHttpEntity.getContent();

		return instream;
	}

	private void writeFile(Bitmap bmp, File f) {
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(f);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception ex) {
			}
		}
	}

	/** Classes **/

	private class ImageRef {
		public String url;
		public ImageView imageView;
		public int fitStyle;

		public ImageRef(String u, ImageView i) {
			url = u;
			imageView = i;
			fitStyle = 0;
		}

		public ImageRef(String u, ImageView i, int f) {
			url = u;
			imageView = i;
			fitStyle = f;
		}
	}

	// stores list of images to download
	private class ImageQueue {
		private Stack<ImageRef> imageRefs = new Stack<ImageRef>();

		// removes all instances of this ImageView
		public void Clean(ImageView view) {

			for (int i = 0; i < imageRefs.size();) {
				if (imageRefs.get(i).imageView == view)
					imageRefs.remove(i);
				else
					++i;
			}
		}
	}

	public static class BitmapInc {
		public Bitmap bitmap;
		// How many activities are showing this image
		public List<String> arRefActivities = new LinkedList<String>();
	}

	private static class ImageHashMap {
		// private int mSize;
		private WeakHashMap<String, BitmapInc> mMap = new WeakHashMap<String, BitmapInc>();

		// private ArrayList<String> mUrls = new ArrayList<String>();
		/*
		 * public ImageHashMap(int count) { mSize = count; }
		 */

		public void clearUnusingBitmaps() {
			// mUrls.clear();
			synchronized (mMap) {
				List<String> lstRemoveKeys = new LinkedList<String>();
				for (Map.Entry<String, BitmapInc> entry : mMap.entrySet()) {
					String sUrl = entry.getKey();
					BitmapInc inc = entry.getValue();
					if (inc.arRefActivities.size() == 0 // No activity uses the url now
							&& inc.bitmap != null && !inc.bitmap.isRecycled()) {
						inc.bitmap.recycle();
						lstRemoveKeys.add(sUrl);
						// mMap.remove(sUrl);
					}
				}
				for (String url : lstRemoveKeys) {
					mMap.remove(url);
				}
				lstRemoveKeys.clear();
			}
		}

		/*
		 * public boolean containsKey(String key) { return mMap.containsKey(key); }
		 */

		public BitmapInc get(String key) {
			synchronized (mMap) {
				return mMap.get(key);
			}
		}

		public void addRef(String key, String sActivityName) {
			synchronized (mMap) {
				BitmapInc inc = mMap.get(key);
				if (inc == null) {
					inc = new BitmapInc();
					mMap.put(key, inc);
				}
				if (!inc.arRefActivities.contains(sActivityName))
					inc.arRefActivities.add(sActivityName);
			}
		}

		public void put(String key, Bitmap value) {
			synchronized (mMap) {
				BitmapInc inc = mMap.get(key);
				if (inc == null) {
					inc = new BitmapInc();
					mMap.put(key, inc);
				}
				inc.bitmap = value;
			}
		}

		public void remove(String key, String sActivityName, boolean bRecycleBitmap) {
			synchronized (mMap) {
				BitmapInc inc = mMap.get(key);
				if (inc != null) {
					// if(bRemoveRef){
					inc.arRefActivities.remove(sActivityName);
					if (inc.arRefActivities.size() == 0) {
						if (bRecycleBitmap && inc.bitmap != null && !inc.bitmap.isRecycled()) {
							inc.bitmap.recycle();
							mMap.remove(key);
						}
					}
					/*
					 * } else{ if(inc.arRefActivities.size() == 1){ String sRefAct = inc.arRefActivities.get(0);
					 * if(sRefAct.equals(sActivityName)){ if(bRecycleBitmap && inc.bitmap != null &&
					 * !inc.bitmap.isRecycled()) inc.bitmap.recycle(); } } }
					 */
				}
			}
		}
	}

	private class ImageQueueManager implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					// The following commented code is not strict, so replace it by this
					ImageRef imageToLoad;
					synchronized (imageQueue.imageRefs) {
						while (imageQueue.imageRefs.size() == 0) {
							imageQueue.imageRefs.wait();
						}
						imageToLoad = imageQueue.imageRefs.pop();
					}

					/*
					 * // Thread waits until there are images in the // queue to be retrieved if
					 * (imageQueue.imageRefs.size() == 0) { synchronized (imageQueue.imageRefs) {
					 * imageQueue.imageRefs.wait(); } }
					 * 
					 * // When we have images to be loaded if (imageQueue.imageRefs.size() != 0) { ImageRef imageToLoad;
					 * 
					 * synchronized (imageQueue.imageRefs) { imageToLoad = imageQueue.imageRefs.pop(); }
					 */
					Bitmap bmp = getBitmap(imageToLoad.url);
					if (bmp != null) {
						BitmapInc inc = imageMap.get(imageToLoad.url);
						// The activity is destroyed before the image is got, then delete the bitmap
						ImageViewInfo tag = (ImageViewInfo)imageToLoad.imageView.getTag();

						if (inc != null && inc.arRefActivities.size() > 0 &&
						// Make sure we have the right view - thread safety
								tag != null && ((String)tag.url).equals(imageToLoad.url)) {
							BitmapDisplayer bmpDisplayer = new BitmapDisplayer(bmp, imageToLoad.imageView,
									imageToLoad.fitStyle, imageToLoad.url);

							Activity a = (Activity)imageToLoad.imageView.getContext();

							a.runOnUiThread(bmpDisplayer);
						} else {
							bmp.recycle();
						}
					}
					// }

					if (Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
			}
		}
	}

	// Used to display bitmap in the UI thread
	private class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageView imageView;
		int fitStyle;
		String picUrl;

		public BitmapDisplayer(Bitmap b, ImageView i, int f, String s) {
			bitmap = b;
			imageView = i;
			fitStyle = f;
			picUrl = s;
		}

		public void run() {
			if (bitmap != null && !bitmap.isRecycled()) {
				// Maybe we could add something here ...
				imageView.setImageBitmap(bitmap);
				if (fitStyle == FIT_TO_SCREEN) {
					//ZoomImageView zoomImageView = (ZoomImageView)imageView;
					//zoomImageView.setLoadingFinished();
					//zoomImageView.initZoomImageParams(bitmap);
				}
				imageMap.put(picUrl, bitmap);
			} else {
				imageView.setImageResource(R.drawable.loading);
				// imageView.setImageMatrix(getImageMatrix(imageView.getWidth(),
				// imageView.getHeight()));
			}
		}

		// According to the restrained size, create a new bitmap

		/*
		 * public Bitmap createAFitSizeBitmap(Bitmap bitmap){ Activity activity = (Activity)imageView.getContext();
		 * Display display = activity.getWindowManager().getDefaultDisplay();
		 * 
		 * //Bitmap newBitmap = bitmap; if(m_iFitStyle == FIT_TO_SCREEN){ int iMaxWidth = display.getWidth(); int
		 * iMaxHeight = display.getHeight();
		 * 
		 * int iBitmapWidth = bitmap.getWidth(); int iBitmapHeight = bitmap.getHeight();
		 * 
		 * int iIdealHeight = (int)1.0*iFitWidth*iBitmapHeight/iBitmapWidth; iBitmapHeight = (iIdealHeight >
		 * iFitHeight)?iFitHeight:iIdealHeight; iBitmapWidth = iFitWidth; newBitmap = Bitmap.createScaledBitmap(bitmap,
		 * iBitmapWidth, iBitmapHeight, false); } else{
		 * 
		 * } return newBitmap; }
		 */
	}
	
}