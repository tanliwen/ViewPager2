package com.xunlei.tdlive.pulltorefresh.internal;

import android.content.Context;
import android.util.DisplayMetrics;

public class Utils {

	static final String LOG_TAG = "PullToRefresh";

	public static void warnDeprecation(String depreacted, String replacement) {
		android.util.Log.w(LOG_TAG, "You're using the deprecated " + depreacted + " attr, please switch over to " + replacement);
	}

	/**
	 * 输出 int 类型
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2pxI(Context context, float dpValue) {
		return (int) dip2px(context, dpValue);
	}

	public static float dip2px(Context context, float dpValue) {
		return (dpValue * density(context) + 0.5f);
	}

	private static float density(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();

		if (metrics == null) {
			return 3.0f;
		}

		return metrics.density;
	}

}
