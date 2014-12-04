package com.example.wochacha.util;

public class StringHelper {

	public static boolean isStringNullOrEmpty(String str)
	{
		return str == null || str.trim().length() == 0;			
	}
	
	public static int[] getLastThreeSplashPos(String sUrl){
		String sGetUrl = sUrl;
		int arIdx[] = new int[3];
		for(int i=0;i<3;i++){
			arIdx[i] = sGetUrl.lastIndexOf("/");
			sGetUrl = sGetUrl.substring(0, arIdx[i]);
		}
		return arIdx;
	}
	
	public static int parseStringToInt(String sContent, int iDefaultValue){
		try{
			int iRes = Integer.parseInt(sContent);
			return iRes;
		}
		catch(NumberFormatException e){
			e.printStackTrace();
			return iDefaultValue;
		}
	}
	
	public static int getSortIDFromUrl(String sUrl, int iDefaultValue){
		int arIdx[] = getLastThreeSplashPos(sUrl);	
		return parseStringToInt(sUrl.substring(arIdx[2] + 1, arIdx[1]), iDefaultValue);
	}
}
