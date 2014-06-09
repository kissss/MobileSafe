package com.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.mobilesafe.R;
import com.mobilesafe.db.dao.BlackNumberDao;
import com.mobilesafe.engine.NumberAddressService;
import com.mobilesafe.ui.CallAndSmsSecurityActivity;

public class AddressService extends Service {
	private static final String TAG = "AddressService";
	TelephonyManager manager;
	MyPhoneListener listener;
	WindowManager windowsManager;
	View view;
	SharedPreferences sp;
	BlackNumberDao dao;
	long startRingring;// 铃响的时间
	long endRingring;// 结束时间
	boolean isExistBlackNumber = false;// 表明此号码是否已经存在黑名单中 这样就不需要再去提示用户添加了

	@Override
	public IBinder onBind(Intent intent) {
		return null;

	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 进行初始化
		sp = getSharedPreferences("config", MODE_PRIVATE);
		windowsManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		dao = new BlackNumberDao(this);

		// 进行初始化
		listener = new MyPhoneListener();
		// 注册系统电话管理服务的监听器
		manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	private class MyPhoneListener extends PhoneStateListener {
		// 电话状态发生改变的时候调用的方法
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:// 处于静止状态 表示没有呼叫
				// 如果不为空 就移除这个textview
				if (view != null) {
					windowsManager.removeView(view);
					view = null;
				}

				// 在响铃状态结束 后 在在拿到一遍电话呼叫的时间
				endRingring = System.currentTimeMillis();
				// 在2s 中挂断的电话 标识为 骚扰电话 响一声
				long calltime = endRingring - startRingring;
				Log.i(TAG, " 呼叫时间为 " + calltime);
				if (startRingring < endRingring && calltime < 2000 && calltime > 0) {
					Log.i(TAG, "响一声电话");
					startRingring = 0;
					endRingring = 0;
					// 表明已经存在 黑名单中 就不需要提示用户再去添加
					if (!isExistBlackNumber) {
						// 弹出notification 表示为一个骚扰电话
						showNotification(incomingNumber);
					}
				}

				break;
			case TelephonyManager.CALL_STATE_RINGING:// 铃响状态
				Log.i(TAG, "来电号码为：" + incomingNumber);
				// 记录铃响的 时间
				startRingring = System.currentTimeMillis();

				// 判断来电号码 是否在黑名单中
				if (isExistBlackNumber = dao.find(incomingNumber)) {
					// 表示在黑名单中 需要挂断电话
					endCall();
					// 因为日志不是立即产生 要延迟一段时间 所以不能立即删除
					// 挂断电话以后 需要删除这个通话日志
					deleteCallLog(incomingNumber);
					// 需要组成一个内容观察者 call_log
					getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, new MyObserver(new Handler(), incomingNumber));

				}

				String address = NumberAddressService.getAddress(incomingNumber);
				Log.i(TAG, "来电归属地为：" + address);
				// 显示来电归属地的位置
				showLocation(address);
				// ViewUtils.showToastShort(getApplicationContext(), "来电归属地为：" +
				// address);

				// 做黑名单系统 响铃状态获取系统的时间

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 接听状态
				Log.i(TAG, "来电号码为：" + incomingNumber);

				if (view != null) {
					windowsManager.removeView(view);
					view = null;
				}
				break;
			default:
				break;
			}
		}

	}

	/**
	 * 显示出一个notification 通知用户添加黑名单号码
	 * 
	 * @param incomingNumber
	 */
	private void showNotification(String incomingNumber) {
		// 1,得到notifaction 的服务
		NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
		// 2,实例化出来
		Notification notification = new Notification(R.drawable.notification, "发现响一声号码", System.currentTimeMillis());

		// notification.sound = Uri.fromFile(new
		// File(Environment.getExternalStorageDirectory(), "/haha.mp3"));

		// notification 点一下就会清除掉
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		// 这个flag=no_clear 是不会清除掉
		// notification.flags = Notification.FLAG_NO_CLEAR;

		// 3,打开详细信息，激活意图
		Intent intent = new Intent(this, CallAndSmsSecurityActivity.class);
		// 把响一声的的号码设置到itent对象里面
		intent.putExtra("number", incomingNumber);

		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

		notification.setLatestEventInfo(getApplicationContext(), "发现响一声号码", incomingNumber, contentIntent);

		// 4,激活notification
		manager.notify(0, notification);

	}

	private class MyObserver extends ContentObserver {
		private String incomingNumber;

		public MyObserver(Handler handler, String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			deleteCallLog(incomingNumber);

			// 当删除了通话记录后 删除这个内容观察者
			getContentResolver().unregisterContentObserver(this);
		}

	}

	/**
	 * 删除电话中的通话记录
	 * 
	 * @param incomingNumber
	 */
	private void deleteCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, null, "number = ?", new String[] { incomingNumber }, null);
		// 查询到了呼叫记录
		if (cursor.moveToNext()) {
			String id = cursor.getString(cursor.getColumnIndex("_id"));
			resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?", new String[] { id });
		}

	}

	/**
	 * 挂断电话
	 */
	private void endCall() {
		try {
			Log.i(TAG, "endPhone");
			Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
			IBinder binder = (IBinder) method.invoke(null, new Object[] { TELEPHONY_SERVICE });
			ITelephony iTelephony = ITelephony.Stub.asInterface(binder);
			// 挂断电话
			iTelephony.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示出来位置信息
	 * 
	 * @param address
	 */
	private void showLocation(String address) {
		final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.gravity = Gravity.LEFT | Gravity.TOP;
		params.x = sp.getInt("phoneAddressLocationX", 0);
		params.y = sp.getInt("phoneAddressLocationY", 0);
		params.setTitle("Toast");

		// 这是一个桌面
		view = View.inflate(getApplicationContext(), R.layout.atools_showlocation, null);

		// 这是桌面中的一个布局
		LinearLayout ll = (LinearLayout) view.findViewById(R.id.showlocation_ll_main);
		int phontLocationBackgroundId = sp.getInt("phoneLocationBackground", 0);
		switch (phontLocationBackgroundId) {
		case 0:// 半透明
			ll.setBackgroundResource(R.drawable.call_locate_blue);
			break;
		case 1: // 活力橙
			ll.setBackgroundResource(R.drawable.call_locate_orange);
			break;
		case 2:// 苹果绿
			ll.setBackgroundResource(R.drawable.call_locate_green);
			break;

		}

		TextView tv = (TextView) view.findViewById(R.id.showlocation_tv_location);
		tv.setText("归属地为：" + address);
		tv.setTextSize(24);

		windowsManager.addView(view, params);

	}

	@Override
	public void onDestroy() {
		manager.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}

}
