package com.app.studiodemo.socialdemo.activity.twitter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

public class CustomDialog extends Dialog {

	private Window mWindow;
	private ViewGroup mDecorView;
	
	private View mContentView;
	
	private OnDialogCreateListener mListener;
	
	public static CustomDialog createDialog(Context context, OnDialogCreateListener listener) {
		return new CustomDialog(context, listener);
	}

	private CustomDialog(Context context, OnDialogCreateListener listener) {
		super(context);
		mWindow = getWindow();
		mDecorView = (ViewGroup) mWindow.getDecorView();//最底层的view
		mDecorView.setBackgroundColor(Color.TRANSPARENT);//设置背景为透明
		mDecorView.removeAllViews();
		mDecorView.setPadding(0, 0, 0, 0);

		//将整个view填满窗口，让他不被点击他以外的任意地方消失，防止被取消
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.height = WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(params);
		
		mListener = listener;
	}

	@Override
	final public void setContentView(int layoutResID) {
		mContentView = getLayoutInflater().inflate(layoutResID, mDecorView, true);
	}

	@Override
	final public void setContentView(View view) {
	}

	@Override
	final public void setContentView(View view, LayoutParams params) {
	}
	
	@Override
	public View findViewById(int id) {
		if(mContentView != null) {
			return mContentView.findViewById(id);
		}
		
		return null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mListener.onDialogCreate(this);
	}
	
	public View getContentView(){
		return mContentView;
	}
}
