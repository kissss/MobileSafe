package com.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.mobilesafe.R;
import com.mobilesafe.db.dao.BlackNumberDao;
import com.mobilesafe.engine.GPSInfoProvider;

public class SMSReceiver extends BroadcastReceiver {

	private static final String TAG = "SMSReceiver";

	BlackNumberDao dao;

	@Override
	public void onReceive(Context context, Intent intent) {
		dao = new BlackNumberDao(context);
		Object[] pbus = (Object[]) intent.getExtras().get("pdus");
		for (Object obj : pbus) {
			SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);
			String content = message.getMessageBody();
			Log.i(TAG, "短信内容为：" + content);
			// 短信的发件人
			String sender = message.getOriginatingAddress();
			// 短信拦截的黑名单
			if (dao.find(sender)) {
				// 终止广播
				abortBroadcast();
			}

			// 警报短信的提醒
			alarmSMS(context, content, sender);

		}
	}

	private void alarmSMS(Context context, String content, String sender) {
		if ("#*localhost*#".equals(content)) {
			// 终止广播
			abortBroadcast();
			GPSInfoProvider provider = GPSInfoProvider.getInstance(context);
			String location = provider.getLocation();
			if (!"".equals(location)) {
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(sender, null, location, null, null);
			}
		} else if ("#*locknow*#".equals(content)) {
			// 终止广播
			abortBroadcast();
			DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
			// manager.resetPassword("12",
			// DeviceAdminInfo.USES_POLICY_RESET_PASSWORD);
			manager.lockNow();
		} else if ("#*wipedata*#".equals(content)) {
			// 终止广播
			abortBroadcast();
			DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
			manager.wipeData(0);
		} else if ("#*alarm*#".equals(content)) {
			abortBroadcast();
			MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
			player.setVolume(1.0f, 1.0f);
			player.start();
		}
	}

}
