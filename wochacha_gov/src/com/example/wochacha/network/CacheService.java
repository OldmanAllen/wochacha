package com.example.wochacha.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.example.wochacha.entity.JSONEntity;
import com.example.wochacha.manager.SessionManager;
import com.example.wochacha.util.Constants;
import com.example.wochacha.util.KeyHelper;

public class CacheService {
	private static CacheService m_cacheService = null;
	private File dataDir = null;
	private File downloadDir = null;

	private static final String FOLDER = "folder";

	public static CacheService getCacheServiceInstance() {
		return m_cacheService;
	}

	public static File getDownloadPath() {
		return m_cacheService.downloadDir;
	}
	private String packageName;

	public synchronized static void initializeInstance(Context context) {
		if (m_cacheService == null) {
			m_cacheService = new CacheService();
			
			m_cacheService.packageName = context.getPackageName();

			String sdState = android.os.Environment.getExternalStorageState();
			if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
				File sdDir = android.os.Environment.getExternalStorageDirectory();
				m_cacheService.downloadDir = new File(sdDir, m_cacheService.packageName + Constants.Cache.CACHE_SDCARD_DOWNLOAD_PATH);

			} else {
				m_cacheService.downloadDir = context.getCacheDir();
			}

			try {

				if (!m_cacheService.downloadDir.exists()) {
					boolean bCreateSucc = m_cacheService.downloadDir.mkdirs();
					Log.d("Debug", "Create the download directory " + bCreateSucc);
				}
				//TODO after login, then try to reset data folders.
				// put it here just for demo purpose.
				m_cacheService.resetDataDir(context);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public void resetDataDir(Context context) {
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();
			m_cacheService.dataDir = new File(sdDir, packageName + Constants.Cache.CACHE_SDCARD_DATA_PATH
					+ SessionManager.getInstance().getId());
		} else {
			m_cacheService.dataDir = context.getCacheDir();
		}
		if (!m_cacheService.dataDir.exists()) {
			m_cacheService.dataDir.mkdirs();
		}
	}

	public long getAllCacheSize() {
		File imageDir = ImageManager.getInstance().getCacheDir();
		return getFileLength(imageDir) + getFileLength(dataDir);
	}

	private long getFileLength(File dir) {
		if (dir == null || !dir.exists())
			return 0;
		String[] children = dir.list();
		long lDirSize = 0;
		for (int i = 0; i < children.length; i++) {
			File childFile = new File(dir, children[i]);
			if (childFile.isDirectory())
				lDirSize += getFileLength(childFile);
			else
				lDirSize += childFile.length();
		}
		return lDirSize;
	}

	// Clear all the related caches
	public void clearCache(String sKey) {
		// Delete the file
		String sWrappedKey = KeyHelper.getWrappedKey(sKey);
		File f = new File(dataDir, KeyHelper.getMD5String(sWrappedKey));
		if (f.exists())
			f.delete();

		// Delete the page 1, 2... sub-files.
		String sFolderKey = sWrappedKey + FOLDER;
		File dir = new File(dataDir, sFolderKey);
		if (dir.isDirectory()) {
			// Clear all the files in this folder
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				new File(dir, children[i]).delete();
			}
		}
	}

	public void clearAllCacheBefore(long lTimeSpan) {
		// data folder
		clearAllCacheInAFolderBefore(dataDir, lTimeSpan);
		// image folder
		clearAllSubFilesBefore(ImageManager.getInstance().getCacheDir(), lTimeSpan);
		// download folder
		clearAllSubFilesBefore(downloadDir, lTimeSpan);

	}

	public void clearAllSubFilesBefore(File parentDir, long lTimeSpan) {
		long lDeleteBefore = Calendar.getInstance().getTimeInMillis() - lTimeSpan;
		// File parentDir = ImageManager.getInstance().getCacheDir();
		if (parentDir == null || !parentDir.exists())
			return;
		String[] children = parentDir.list();
		for (int i = 0; i < children.length; i++) {
			File childFile = new File(parentDir, children[i]);
			long lLastModified = childFile.lastModified();
			if (lLastModified < lDeleteBefore)
				childFile.delete();
		}
	}

	private void clearAllCacheInAFolderBefore(File folder, long lTimeSpan) {
		if (folder == null || !folder.exists())
			return;
		long lDeleteBefore = Calendar.getInstance().getTimeInMillis() - lTimeSpan;
		String[] children = folder.list();
		for (int i = 0; i < children.length; i++) {
			File childFile = new File(folder, children[i]);
			if (childFile.isDirectory())
				clearAllCacheInAFolderBefore(childFile, lTimeSpan);
			else {
				long lLastModified = childFile.lastModified();
				if (lLastModified < lDeleteBefore)
					childFile.delete();
			}
		}
		if (folder != dataDir) {
			String[] childrenNew = folder.list();
			if (childrenNew.length == 0)
				folder.delete();
		}
	}

	// In fact, "refresh" is not precise, for the cache would be clear before
	// re-store.
	// You should define the "sKey" by your self, as we can't know what you want
	// to store here
	public void refreshCacheInFolder(String sFolderName, String sKey, Object obj) {
		if (obj != null) {
			try {
				File f = getFileInFolder(sFolderName, sKey, false);
				writeStringToCache(f, toJsonString(obj));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public void refreshCache(String sKey, Object obj) {
		refreshCache(sKey, obj, true);
	}

	private String toJsonString(Object obj) throws JSONException {
		String dataString = obj.toString();
		if (obj instanceof JSONEntity)
			dataString = ((JSONEntity)obj).toJsonString();
		return dataString;
	}

	public void refreshCache(String sKey, Object obj, Boolean wrapKey) {
		if (obj != null) {
			// Refresh cache file in file system
			try {

				String sKeyWithToken = wrapKey ? KeyHelper.getWrappedKey(sKey) : sKey;
				String fileName = KeyHelper.getMD5String(sKeyWithToken);
				File f = new File(dataDir, fileName);
				if (!f.exists())
					f.createNewFile();
				writeStringToCache(f, toJsonString(obj));
				// Refresh relational hash map in memory
				// m_hashCache.put(sKey, obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String getObjectFromFolder(String sFolderName, String sKey) {
		try {
			String sObj = getStringFromFolder(sFolderName, sKey);
			return sObj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	

	public String getObjectFromCache(String sKey, Boolean wrapKey) {
		// Object objCache = m_hashCache.get(sKey);

		String wrappedName = wrapKey ? KeyHelper.getWrappedKey(sKey) : sKey;
		String fileName = KeyHelper.getMD5String(wrappedName);	
		
		String sObj = getStringFromCache(new File(dataDir, fileName));
		return sObj;
	}

	

	private String getStringFromFolder(String sFolderName, String sKey) throws IOException {
		File f = getFileInFolder(sFolderName, sKey, true);
		return getStringFromCache(f);
	}

	private void writeStringToCache(File f, String sObj) throws IOException {
		FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos, "UTF8"));
		out.write(sObj);
		out.close();
	}

	private String getStringFromCache(File f) {
		try {
			if (f == null || !f.exists())
				return null;

			FileInputStream fis = new FileInputStream(f.getAbsolutePath());
			BufferedReader fin = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			

			String sLine = fin.readLine();
			String sContent = "";
			while (sLine != null) {
				sContent = sContent + "\n" + sLine;
				sLine = fin.readLine();
			}
			if (!(sContent.length() == 0))
				sContent = sContent.substring(1);
			fin.close();
			return sContent;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	private File getFileInFolder(String sFolderName, String sKey, boolean bIsGet) throws IOException {
		String wrappedString = KeyHelper.getWrappedKey(sFolderName);
		// String sFolder =
		String dirName = KeyHelper.getMD5String(wrappedString) + FOLDER;
		File dir = new File(dataDir, dirName);
		if (!dir.isDirectory()) {
			if (bIsGet)
				return null;
			else
				dir.mkdirs();
		}

		File f = new File(dir, KeyHelper.getMD5String(sKey));
		if (!f.exists()) {
			if (bIsGet)
				return null;
			else
				f.createNewFile();
		}
		return f;
	}

}
