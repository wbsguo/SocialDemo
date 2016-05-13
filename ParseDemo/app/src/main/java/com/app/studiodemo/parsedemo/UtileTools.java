package com.app.studiodemo.parsedemo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class UtileTools {
	private static final String TAG = "UtileTools";
	public static UtileTools utileTools = null;

	public static UtileTools getInstance() {
		synchronized (UtileTools.class) {
			if (utileTools == null) {
				utileTools = new UtileTools();
			}
		}
		return utileTools;
	}
	/** 是否接入了网络 */
	public boolean checkNetWorkStatus(Context context) {
		boolean result;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		if (netinfo != null && netinfo.isConnected()) {
			result = true;
			MyLog.i("NetStatus", "The net was connected");
		} else {
			result = false;
			MyLog.i("NetStatus", "The net was bad!");
		}
		return result;
	}
}
