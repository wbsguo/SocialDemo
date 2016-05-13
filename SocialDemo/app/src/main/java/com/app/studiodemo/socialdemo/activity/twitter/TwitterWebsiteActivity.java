package com.app.studiodemo.socialdemo.activity.twitter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.studiodemo.socialdemo.R;


public class TwitterWebsiteActivity extends Activity {
	private static final String TAG = "TwitterWebsiteActivity";
	private Context ctx;
	private WebView webView;
	private CustomDialog progressDialog = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_website);
		initProgressDialog();
		views();
	}
	private void views(){
		ctx = this;
		initProgressDialog();
		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setAllowFileAccess(true);// 设置允许访问文件数据
		webView.getSettings().setBuiltInZoomControls(false);// 设置支持缩放
		webView.getSettings().setDefaultZoom(ZoomDensity.FAR);
		webView.getSettings().setSavePassword(false); // 设置是否保存密码
		// 设置WebView属性，能够执行Javascript脚本
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setInitialScale(100);
		// 设置Web视图
		webView.setWebViewClient(new MyWebViewClient());
		String url = getIntent().getStringExtra("url");
		Log.e(TAG, url);
		webView.loadUrl(url);
	}
	private void initProgressDialog() {
		progressDialog = CustomDialog.createDialog(this,
				new OnDialogCreateListener() {
					@Override
					public void onDialogCreate(CustomDialog dialog) {
						dialog.setContentView(R.layout.waiting_dialog);
					}
				});
	}
	
	private class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url != null && url.contains(TwitterConstant.OAUTH_VERIFIER)) {
				String oauth_verifier = Uri.parse(url).getQueryParameter(
						TwitterConstant.OAUTH_VERIFIER);
				Log.e(TAG, "oauth_verifier = " + oauth_verifier);
				Intent intent = new Intent();
				intent.putExtra("oauth_verifier", oauth_verifier);
				setResult(RESULT_OK, intent);
				TwitterWebsiteActivity.this.finish();
				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.removeAllCookie();
				return true;
			}
			Log.e(TAG, url);
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			progressDialog.show();

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			progressDialog.dismiss();

		}

	}
}
