package com.mobilesafe.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mobilesafe.R;

public class ViewUtils {

	public static void showToastShort(Context context, CharSequence text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void showToastLong(Context context, CharSequence text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	/**
	 * Activity 做跳转
	 * 
	 * @param activity
	 *            当前Activity
	 * @param cls
	 *            要跳转的Activity
	 */
	public static void changeActivity(Activity activity, Class<?> cls) {
		Intent intent = new Intent(activity, cls);
		// 一定要从当前的任务栈中移除这个Activity
		activity.finish();
		activity.startActivity(intent);
		// 设置Activity 切换时候的动画效果
		activity.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
	}
}
