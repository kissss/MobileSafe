package com.mobilesafe.utils;

import android.os.Environment;

public class AndroidUtils {

	/**
	 * 判断sd卡是否可用
	 * 
	 * @return true 表示可用
	 */
	public static boolean sdCardaVailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
}
