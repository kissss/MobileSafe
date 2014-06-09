package com.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {
	private static final String TAG = "BootCompleteReceiver";
	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "手机重启完毕");
		// 1.判断手机是否处于保护状态
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean isSetupProtect = sp.getBoolean("isSetupProtect", false);
		// 如果进行手机保护
		if (isSetupProtect) {
			TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String currentSerialNumber = manager.getSimSerialNumber();
			// 序列号
			String simSerialNumber = sp.getString("simSerialNumber", null);
			// sim 卡串号不相同
			if (!currentSerialNumber.equals(simSerialNumber)) {
				String safaNumber = sp.getString("safeNumber", "");
				// 发送报警信息
				Log.i(TAG, "发送警报短信, 报警手机号" + safaNumber);
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(safaNumber, null, "SIM卡发生了改变 可能被盗", null, null);
			}
		}

	}
}
