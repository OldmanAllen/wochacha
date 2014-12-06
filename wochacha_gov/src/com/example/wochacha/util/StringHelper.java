package com.example.wochacha.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

public class StringHelper {

	public static boolean isStringNullOrEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static int[] getLastThreeSplashPos(String sUrl) {
		String sGetUrl = sUrl;
		int arIdx[] = new int[3];
		for (int i = 0; i < 3; i++) {
			arIdx[i] = sGetUrl.lastIndexOf("/");
			sGetUrl = sGetUrl.substring(0, arIdx[i]);
		}
		return arIdx;
	}

	public static int parseStringToInt(String sContent, int iDefaultValue) {
		try {
			int iRes = Integer.parseInt(sContent);
			return iRes;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return iDefaultValue;
		}
	}

	public static int getSortIDFromUrl(String sUrl, int iDefaultValue) {
		int arIdx[] = getLastThreeSplashPos(sUrl);
		return parseStringToInt(sUrl.substring(arIdx[2] + 1, arIdx[1]), iDefaultValue);
	}

	public static String[] toStringArray(JSONArray array) {
		int length = array.length();
		String[] strArray = new String[length];
		try {

			for (int i = 0; i < length; i++) {
				strArray[i] = array.getString(i);
			}
		} catch (JSONException e) {
			// TODO: handle exception
		}

		return strArray;
	}

	public static Date convertStringToDate(String dateString) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss");
		Date date = null;
		try {
			date = format.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
}
