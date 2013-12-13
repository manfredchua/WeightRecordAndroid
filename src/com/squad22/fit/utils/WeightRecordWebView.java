package com.squad22.fit.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.ViewFlipper;

public class WeightRecordWebView extends WebView {

	float downXValue;

	long downTime;

	private ViewFlipper flipper;

	private float lastTouchX, lastTouchY;

	private boolean hasMoved = false;
	
	private boolean isMore;
	

	public WeightRecordWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WeightRecordWebView(Context context, ViewFlipper flipper, boolean isMore) {

		super(context);

		this.flipper = flipper;
		this.isMore = isMore;
	}

	@Override
	public boolean onTouchEvent(MotionEvent evt) {

		boolean consumed = super.onTouchEvent(evt);

		if (isClickable()) {

			switch (evt.getAction()) {

			case MotionEvent.ACTION_DOWN:

				lastTouchX = evt.getX();

				lastTouchY = evt.getY();

				downXValue = evt.getX();

				downTime = evt.getEventTime();

				hasMoved = false;

				break;

			case MotionEvent.ACTION_MOVE:

				hasMoved = moved(evt);

				break;

			case MotionEvent.ACTION_UP:

				float currentX = evt.getX();

				long currentTime = evt.getEventTime();

				float difference = Math.abs(downXValue - currentX);

				long time = currentTime - downTime;

				Log.i("Touch Event:", "Distance: " + difference + "px Time: "
						+ time + "ms");

				/** X轴滑动距离大于100，并且时间小于220ms,并且向X轴右方向滑动 && (time < 220) */

				if ((downXValue < currentX)
						&& (difference > 100 && (time < 220))) {
					 /** 跳到上一页 */
					if (flipper.getDisplayedChild() != 0) {
						flipper.showPrevious();
					}
					
					
				}

				/** X轴滑动距离大于100，并且时间小于220ms,并且向X轴左方向滑动 */

				if ((downXValue > currentX) && (difference > 100)
						&& (time < 220)) {

					/** 跳到下一页 */
					if(isMore){
						if (flipper.getDisplayedChild() != 4) {
							flipper.showNext();
						}
					}else{
						if (flipper.getDisplayedChild() != 2) {
							flipper.showNext();
						}
					}
				}

				break;

			}

		}

		return consumed || isClickable();

	}

	private boolean moved(MotionEvent evt) {

		return hasMoved || Math.abs(evt.getX() - lastTouchX) > 10.0

		|| Math.abs(evt.getY() - lastTouchY) > 10.0;

	}

}
