package com.mobilesafe.engine;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.mobilesafe.domain.UpdateInfo;

public class UpdateInfoParser {

	/**
	 * 解析xml
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public static UpdateInfo getUpdateInfo(InputStream is) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		UpdateInfo updateInfo = new UpdateInfo();
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("version".equals(parser.getName())) {
					String version = parser.nextText();
					updateInfo.setVersion(version);
				} else if ("description".equals(parser.getName())) {
					String description = parser.nextText();
					updateInfo.setDescription(description);
				} else if ("apkUrl".equals(parser.getName())) {
					String apkUrl = parser.nextText();
					updateInfo.setApkUrl(apkUrl);
				}
				break;
			default:
				break;
			}
			type = parser.next();
		}
		return updateInfo;
	}
}
