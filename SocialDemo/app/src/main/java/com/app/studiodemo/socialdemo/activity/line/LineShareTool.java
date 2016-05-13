package com.app.studiodemo.socialdemo.activity.line;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.ClipboardManager;
import android.util.Log;
import android.widget.Toast;

import com.app.studiodemo.socialdemo.R;


public class LineShareTool {
	private static final String TAG = "LineShareTool";
	private final String LINE_PACKAGENAME = "jp.naver.line.android";
	private final String LINE_LINK = "http://line.naver.jp/R/msg/text/?";
	private static LineShareTool tool;
	private LineShareTool() {

	}
	public static LineShareTool getInstance() {
		if (tool == null) {
			tool = new LineShareTool();
		}
		return tool;
	}
	/** 通过Line应用分享到line */
	public void shareToLineByApp(Context context, String message,String noLineTosat) {
		if (!apkInstalled(context, LINE_PACKAGENAME)) {
			Log.e(TAG, "未安装Line客户端");
			Toast.makeText(context, noLineTosat, Toast.LENGTH_SHORT).show();
			return;
		}
		ClipboardManager clipboard = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setText(message);
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		intent.setPackage(LINE_PACKAGENAME);
		intent.putExtra(Intent.EXTRA_SUBJECT,
				context.getText(R.string.app_name)); // 分享的主题
		intent.putExtra(Intent.EXTRA_TEXT, message); // 分享的内容
		context.startActivity(Intent.createChooser(intent,
				context.getText(R.string.app_name)));
	}
	/**
	 * 判断本机是否安装了指定包名对应的应用
	 *
	 * @param packageName 指定包名
	 */
	public boolean apkInstalled(Context context, String packageName) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
			if (packageInfo != null) {
				packageInfo = null;
				return true;
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			packageInfo = null;
		}
		return false;
	}
	/** 通过网页分享到line */
	public void shareToLineByWeb(Context context, String message) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		if ("-10".equals(message)) {
			message = "";
		}
		Uri content_url = Uri.parse(LINE_LINK + message);
		intent.setData(content_url);
		context.startActivity(intent);
	}
}
