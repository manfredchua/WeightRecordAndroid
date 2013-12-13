package com.squad22.fit.utils;

import com.squad22.fit.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.View;

@SuppressLint("ViewConstructor")
public class ClockView extends View {
	// Original bitmap of dial,hour,minute,second
	Bitmap mBmpDial;
	Bitmap mBmpHour;
	Bitmap mBmpMinute;
	Bitmap mBmpSecond;

	// Drawable of dial,hour,minute,second
	BitmapDrawable bmdHour;
	BitmapDrawable bmdMinute;
	BitmapDrawable bmdDial;

	Paint mPaint;// Paint draw on canvas

	Handler tickHandler;

	int mWidth; // Dial width
	int mHeigh; // Dial height
	int mTempWidth; // Hour/Minute/Second width
	int mTempHeigh; // Hour/Minute/Second height
	int centerX; // Hour/Minute/Second picture center x
	int centerY; // Hour/Minute/Second picture center y

	int availableWidth = 100;// Available width of the dial
	int availableHeight = 100;// Available height of the dial

	private int hourStr;
	private int minuteStr;
	private int height;

	@SuppressWarnings("deprecation")
	public ClockView(Context context, int hourStr, int minuteStr, int heigh) {
		super(context);

		this.height = heigh;
		this.hourStr = hourStr;
		this.minuteStr = minuteStr;

		mBmpHour = BitmapFactory.decodeResource(getResources(),
				R.drawable.clock_hour);
		bmdHour = new BitmapDrawable(mBmpHour);

		mBmpMinute = BitmapFactory.decodeResource(getResources(),
				R.drawable.clock_minute);
		bmdMinute = new BitmapDrawable(mBmpMinute);

		// mBmpSecond = BitmapFactory.decodeResource(getResources(),
		// R.drawable.clock_minute);
		// bmdSecond = new BitmapDrawable(mBmpSecond);

		mBmpDial = BitmapFactory.decodeResource(getResources(),
				R.drawable.clock_dial);
		bmdDial = new BitmapDrawable(mBmpDial);
		mWidth = heigh;
		mHeigh = heigh;
		centerX = availableWidth / 2;
		centerY = availableHeight / 2;

		mPaint = new Paint();
		mPaint.setColor(Color.BLUE);
		// run();//Begin to send update event ,how to sync?
	}

	public void run() {
		tickHandler = new Handler();
		tickHandler.post(tickRunnable);
	}

	private Runnable tickRunnable = new Runnable() {
		public void run() {
			postInvalidate();
			tickHandler.postDelayed(tickRunnable, 1000);
		}
	};

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float hourRotate = hourStr * 30.0f + minuteStr / 60.0f * 30.0f;
		float minuteRotate = minuteStr * 6.0f;

		// Log.d(TAG, "hour is "+hour+" minute is "+minute);
		// Log.d(TAG,
		// "hourRotate is "+hourRotate+" minuteRotate is "+minuteRotate+" secondRotate is "+secondRotate);
		boolean scaled = false;

		if (availableWidth < mWidth || availableHeight < mHeigh) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) mWidth,
					(float) availableHeight / (float) mHeigh);
			canvas.save();
			canvas.scale(scale, scale, centerX, centerY);
		}

		// Log.d(TAG,"Canvas's height is "+canvas.getHeight()
		// +" width is "+canvas.getWidth());

		// Draw dial on view

		bmdDial.setBounds(centerX - (mWidth / 2), centerY - (mHeigh / 2),
				centerX + (mWidth / 2), centerY + (mHeigh / 2));

		bmdDial.draw(canvas);
		// Draw hour on view
		mTempWidth = bmdHour.getIntrinsicWidth();
		mTempHeigh = bmdHour.getIntrinsicHeight();
		canvas.save();// Save non rotated canvas
		canvas.rotate(hourRotate, centerX, centerY);
		if (height == 256) {
			bmdHour.setBounds(centerX - (mTempWidth), centerY - (mTempHeigh),
					centerX + (mTempWidth), centerY + (mTempHeigh));
		} else {
			bmdHour.setBounds(centerX - (mTempWidth / 4), centerY
					- (mTempHeigh / 4), centerX + (mTempWidth / 4), centerY
					+ (mTempHeigh / 4));
		}

		bmdHour.draw(canvas);

		canvas.restore(); // restore canvas to last saved state

		// Draw Minute on view
		mTempWidth = bmdMinute.getIntrinsicWidth();
		mTempHeigh = bmdMinute.getIntrinsicHeight();
		canvas.save();
		canvas.rotate(minuteRotate, centerX, centerY);
		if (height == 256) {
			bmdMinute.setBounds(centerX - mTempWidth, centerY - mTempHeigh,
					centerX + mTempWidth, centerY + mTempHeigh);
		} else {
			bmdMinute.setBounds(centerX - (mTempWidth / 4), centerY
					- (mTempHeigh / 4), centerX + (mTempWidth / 4), centerY
					+ (mTempHeigh / 4));
		}
		bmdMinute.draw(canvas);

		canvas.restore();

		if (scaled) {
			canvas.restore();
		}
	}
}
