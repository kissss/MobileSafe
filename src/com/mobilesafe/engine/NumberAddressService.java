package com.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.mobilesafe.db.dao.AddressDao;

public class NumberAddressService {

	/**
	 * 得到手机的归属地
	 * 
	 * @param number
	 * @return
	 */
	public static String getAddress(String number) {
		String phonePatten = "^1[3458]\\d{9}";
		SQLiteDatabase db = AddressDao.getAddressDB(Environment.getExternalStorageDirectory().getAbsolutePath() + "/address.db");
		String city = null;
		// 查询的是一个手机号
		if (number.matches(phonePatten)) {
			if (db.isOpen()) {
				Cursor cursor = db.rawQuery("SELECT city FROM info WHERE mobileprefix = ?", new String[] { number.substring(0, 7) });
				if (cursor.moveToNext()) {
					city = cursor.getString(0);
				}
				cursor.close();
			}
			db.close();
		} else {
			// 固定电话
			int len = number.length();
			switch (len) {
			case 4:// 模拟器
				city = "模拟器";
				break;
			case 7:// 本地号码
				city = "本地号码";
				break;
			case 8:// 本地号码
				city = "本地号码";
				break;
			case 10:// 3位 区号 + 7 位电话号 4位 区号 + 6 位电话号
				String areaCode = getCity(number, 4, db);

				if (areaCode != null) {
					city = areaCode;
				} else {
					areaCode = getCity(number, 3, db);
					if (areaCode != null) {
						city = areaCode;
					} else {
						city = "本地号码";
					}
				}
				break;
			case 11: // 4位 区号 + 7 位电话号
				city = getCity(number, 4, db);
				break;
			case 12: // 4位 区号 + 7 位电话号
				city = getCity(number, 4, db);
				break;
			default:
				city = "数据库中没有";
				break;
			}
		}
		db.close();
		return city;
	}

	private static String getCity(String number, int areaCode, SQLiteDatabase db) {
		String city = "没有找到";
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("SELECT city FROM info WHERE area = ? LIMIT 1", new String[] { number.substring(0, areaCode) });
			if (cursor.moveToNext()) {
				city = cursor.getString(0);
			}
			cursor.close();
		}
		db.close();
		return city;
	}
}
