package com.mobilesafe.engine;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import com.alibaba.fastjson.JSON;
import com.mobilesafe.domain.SmsInfo;

public class SmsInfoService {

	private static final String TAG = "SmsInfoService";
	Context context;

	public SmsInfoService(Context context) {
		super();
		this.context = context;
	}

	/**
	 * 从手机中得到 短信 放入 list中
	 * 
	 * @return
	 */
	public List<SmsInfo> getSmsInfos() {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms");
		Cursor cursor = resolver.query(uri, new String[] { "_id", "address", "date", "type", "body" }, null, null, "date desc");
		List<SmsInfo> list = new ArrayList<SmsInfo>();

		SmsInfo smsInfo;
		while (cursor.moveToNext()) {
			smsInfo = new SmsInfo();
			smsInfo.setId(cursor.getString(0));
			smsInfo.setAddress(cursor.getString(1));
			smsInfo.setDate(cursor.getString(2));
			smsInfo.setType(cursor.getString(3));
			smsInfo.setBody(cursor.getString(4));
			list.add(smsInfo);
			smsInfo = null;
		}
		Log.i(TAG, "所有的短信内容为:" + JSON.toJSONString(list));
		return list;
	}

	/**
	 * 还原手机备份的短信
	 * 
	 * @param path
	 *            备份短信xml 的位置
	 * @throws Exception
	 */
	public void restoreSMS(String fileName, ProgressDialog pd) throws Exception {
		File file = new File(Environment.getExternalStorageDirectory(), fileName);
		FileInputStream fis = new FileInputStream(file);
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(fis, "utf-8");
		int type = parser.getEventType();

		// 备份短信所用到的
		ContentValues contentValues = null;
		// 当前备份短信 完成的数目
		int currentCount = 0;
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("count".equals(parser.getName())) {
					String count = parser.nextText();
					pd.setMax(Integer.parseInt(count));
				}

				if ("sms".equals(parser.getName())) {
					contentValues = new ContentValues();
				} else if ("address".equals(parser.getName())) {
					contentValues.put("address", parser.nextText());
				} else if ("body".equals(parser.getName())) {
					contentValues.put("body", parser.nextText());
				} else if ("type".equals(parser.getName())) {
					contentValues.put("type", parser.nextText());
				} else if ("date".equals(parser.getName())) {
					contentValues.put("date", parser.nextText());
				}

				break;
			case XmlPullParser.END_TAG:
				if ("sms".equals(parser.getName())) {
					ContentResolver resolver = context.getContentResolver();
					resolver.insert(Uri.parse("content://sms"), contentValues);
					contentValues = null;
					currentCount++;
					pd.setProgress(currentCount);
				}
				break;

			default:
				break;
			}
			type = parser.next();
		}

	}
}
