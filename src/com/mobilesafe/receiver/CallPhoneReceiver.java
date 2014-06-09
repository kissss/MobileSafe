package com.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mobilesafe.ui.LostProtectedActivity;

public class CallPhoneReceiver extends BroadcastReceiver {

	private static final String TAG = "CallPhoneReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String number = getResultData();
		if ("2580".equals(number)) {
			Log.i(TAG, "进入快捷功能显示");
			Intent lostProtectIntent = new Intent(context, LostProtectedActivity.class);
			// 指定激活的Activity 在自己的任务栈里面执行
			lostProtectIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(lostProtectIntent);
			// 终止这个电话 不能用abortBroadcast();
			setResultData(null);
			
		}
	}

}
