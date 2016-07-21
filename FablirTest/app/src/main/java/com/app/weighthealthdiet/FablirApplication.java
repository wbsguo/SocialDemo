package com.app.weighthealthdiet;

import android.app.Application;

import com.app.weighthealthdiet.twitter.TwitterHelper;


public class FablirApplication extends Application {
	private static final String TAG="FablirApplication";
	@Override
	public void onCreate() {
		super.onCreate();
		twitterInit();
	}

	private void twitterInit() {
		TwitterHelper.getInstance().applicationOnCreate(this);
	}

}
