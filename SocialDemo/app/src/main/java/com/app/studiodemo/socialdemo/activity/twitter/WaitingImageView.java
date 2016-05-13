package com.app.studiodemo.socialdemo.activity.twitter;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class WaitingImageView extends ImageView {
	private float mAngle;
	
	private boolean mAttached = false;
	
	private float mWidth, mHeight;
	
	private class Rotation implements Runnable {

		@Override
		public void run() {
			mAngle += 4;
			
			if(mAngle >= 360) {
				mAngle = 0;
			}
			//开启cache
			setDrawingCacheEnabled(true);
			
			invalidate();
			
			if(mAttached) {
				post(this);
			}
		}
	}
	
	private Runnable mRotation = new Rotation();
	
	public WaitingImageView(Context context) {
		super(context);
	}

	public WaitingImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WaitingImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		//当他出现在窗口上执行线程
		mAttached = true;
		
		post(mRotation);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		//当他消失的时候停止线程
		mAttached = false;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//计算他的宽度和高度
		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		mHeight = MeasureSpec.getSize(heightMeasureSpec);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//以(mWidth/2,mHeight/2)为圆心，每次以mAngle的角度进行旋转
		canvas.rotate(mAngle, mWidth / 2, mHeight / 2);
		super.onDraw(canvas);
	}
}
