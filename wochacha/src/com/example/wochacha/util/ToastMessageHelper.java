package com.example.wochacha.util;

import android.content.Context;
import android.widget.Toast;

public class ToastMessageHelper {
	public static void showErrorMessage(Context context, String message, boolean shortDuration) {
		Toast.makeText(context, message, shortDuration ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
	}

	public static void showErrorMessage(Context context, int resId, boolean shortDuration) {
		Toast.makeText(context, resId, shortDuration ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
	}
	public static void showErrorMessage(Context context, int resId, int duration) {
		Toast.makeText(context, resId, duration).show();
	}
}
