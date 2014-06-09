package com.mobilesafe.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.util.Xml;

import com.mobilesafe.domain.SmsInfo;
import com.mobilesafe.engine.SmsInfoService;
import com.mobilesafe.utils.ViewUtils;

public class BackupSmsService extends Service {

	SmsInfoService smsInfoService;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		smsInfoService = new SmsInfoService(this);
		super.onCreate();
		new Thread() {
			@Override
			public void run() {
				// 将得到的 list 写入sd卡中
				try {
					List<SmsInfo> list = smsInfoService.getSmsInfos();
					File file = new File(Environment.getExternalStorageDirectory(), "smsback.xml");
					XmlSerializer serializer = Xml.newSerializer();
					FileOutputStream fos = new FileOutputStream(file);
					serializer.setOutput(fos, "utf-8");
					serializer.startDocument("utf-8", true);

					serializer.startTag(null, "smss");
					//设置短信备份的条数
					serializer.startTag(null, "count");
					serializer.text(list.size() + "");
					serializer.endTag(null, "count");

					for (SmsInfo sms : list) {
						serializer.startTag(null, "sms");

						serializer.startTag(null, "id");
						serializer.text(sms.getId());
						serializer.endTag(null, "id");

						serializer.startTag(null, "address");
						serializer.text(sms.getAddress());
						serializer.endTag(null, "address");

						serializer.startTag(null, "body");
						serializer.text(sms.getBody());
						serializer.endTag(null, "body");

						serializer.startTag(null, "type");
						serializer.text(sms.getType());
						serializer.endTag(null, "type");

						serializer.startTag(null, "date");
						serializer.text(sms.getDate());
						serializer.endTag(null, "date");

						serializer.endTag(null, "sms");
					}

					serializer.endTag(null, "smss");

					serializer.endDocument();
					// 把文件缓冲区的数据写出去
					fos.flush();
					fos.close();

					Looper.prepare();
					ViewUtils.showToastShort(getApplicationContext(), "短信备份完成");
					Looper.loop();

				} catch (Exception e) {
					e.printStackTrace();
					Looper.prepare();
					ViewUtils.showToastShort(getApplicationContext(), "短信备份失败");
					Looper.loop();
				}
			}
		}.start();
	}
}
