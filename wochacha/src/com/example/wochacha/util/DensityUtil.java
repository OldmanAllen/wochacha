package com.example.wochacha.util;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Window;

/**
 * DensityUtil makes transformations from dip to px and from px to dip, given the context.
 * 
 * @author zyan
 * @Version
 * 
 */
public class DensityUtil {
	private static int statusBarHeight = -1;
	private static int titleBarHeight = -1;

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;

		return (int)(dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;

		return (int)(pxValue / scale + 0.5f);
	}

	public static int sp2px(Context context, float spValue) {
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;

		return (int)(spValue * scale + 0.5f);
	}

	public static int[] getScreenHeightAndWidth(Activity activity) {
		int[] result = new int[2];

		try {
			DisplayMetrics displayMetrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			result[0] = displayMetrics.widthPixels;
			result[1] = displayMetrics.heightPixels;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static int getStatusBarHeight(Activity activity) {

		if (statusBarHeight > 0) {
			return statusBarHeight;
		}
		Rect rectgle = new Rect();
		Window window = activity.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
		statusBarHeight = rectgle.top;
		return statusBarHeight;

	}

	public static int getStatusBarHeight1(Activity activity) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = activity.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	// Calculate ActionBar height
	public static int getActionBarHeight(Activity activity) {
		int actionBarHeight = 0;
		TypedValue tv = new TypedValue();
		if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources()
					.getDisplayMetrics());
		}
		return actionBarHeight;
	}

	public static int getTitleBarHeight(Activity activity) {

		if (titleBarHeight > 0) {
			return titleBarHeight;
		}
		Rect rectgle = new Rect();
		Window window = activity.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
		int statusBarHeight = rectgle.top;
		int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		return contentViewTop - statusBarHeight;
	}

	

}
