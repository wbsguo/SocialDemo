package com.app.studiodemo.parsedemo;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Map;

public class ParseApplication extends Application {
	private static final String TAG="WightApplication";
	private Map<String, String> name = new HashMap<String, String>();
	@Override
	public void onCreate() {
		super.onCreate();
		parseInit();
	}

	private void parseInit() {
		// Initialize Crash Reporting.
		ParseCrashReporting.enable(this);
		// Enable Local Datastore.
		Parse.enableLocalDatastore(this);
		// Add your initialization code here
		Parse.initialize(this, Constant.YOUR_APPLICATION_ID,
				Constant.YOUR_CLIENT_KEY);
		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		// Optionally enable public read access.
		// defaultACL.setPublicReadAccess(true);
		ParseACL.setDefaultACL(defaultACL, true);
		ParseFacebookUtils.initialize(this);
		ParseTwitterUtils.initialize(Constant.consumerKey,
				Constant.consumerSecret);
	}
}
