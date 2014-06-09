package com.mobilesafe.test;

import android.test.AndroidTestCase;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.mobilesafe.R;
import com.mobilesafe.db.dao.BlackNumberDao;
import com.mobilesafe.domain.UpdateInfo;
import com.mobilesafe.engine.AppInfoProvider;
import com.mobilesafe.engine.ContactInfoService;
import com.mobilesafe.engine.SmsInfoService;
import com.mobilesafe.engine.UpdateInfoService;

public class AndroidTest extends AndroidTestCase {

	public void test() throws Exception {
		UpdateInfo u = new UpdateInfoService(getContext()).getUpdateInfo(R.string.updateUrl);
		Log.e("", JSON.toJSONString(u));
	}

	public void test1() throws Exception {
		ContactInfoService service = new ContactInfoService(getContext());
		Log.e("", JSON.toJSONString(service.getContactInfoList()));
	}

	public void test3() throws Exception {
		AppInfoProvider ap = new AppInfoProvider(getContext());
		ap.getAllAppInfo();
	}

	public void test2() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());

		for (int i = 0; i < 100; i++) {

			dao.insertNumber((int) (Math.random() * 100) + "");
		}

		dao.deleteNumber("123");
		// dao.updateNumber("123", "234");
		// dao.find("123");

		Log.e("", JSON.toJSONString(dao.findAll()));

	}
}
