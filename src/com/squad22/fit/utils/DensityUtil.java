package com.squad22.fit.utils;

import android.content.Context;


/**
 * 
 * 不同屏幕之间的单位换算
 * 
 * <功能详细描述>
 * 
 * @author user
 */
public class DensityUtil {

	/**
	 * 将dip转化为px
	 * 
	 * @param dip
	 * @return
	 * @author bypan
	 */
	public static int dip2px(Context context, float dip) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));

	}


	/**
	 * 将px转化为dip
	 * 
	 * @param px
	 * @return
	 * @author bypan
	 */
	public static int px2dip(Context context, float px) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (px / density + 0.5f * (px >= 0 ? 1 : -1));
	}
}
