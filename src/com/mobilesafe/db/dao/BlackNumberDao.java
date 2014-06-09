package com.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BlackNumberDao {
	Context context;
	BlackNumberDBHelper dbHelper;

	public BlackNumberDao(Context context) {
		this.context = context;
		dbHelper = new BlackNumberDBHelper(context);
	}

	/**
	 * 查询此号码是否存在数据库中
	 * 
	 * @param number
	 * @return
	 */
	public boolean find(String number) {
		boolean result = false;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select number from blacknumber where number = ?", new String[] { number });
			if (cursor.moveToNext()) {
				result = true;
			}
			cursor.close();
			db.close();
		}
		return result;
	}

	/**
	 * 增加黑名单
	 * 
	 * @param number
	 */
	public void insertNumber(String number) {
		if (find(number)) {
			// 如果存在此数据库就返回
			return;
		} else {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			if (db.isOpen()) {
				db.execSQL("insert into blacknumber(number) values(?)", new Object[] { number });
				db.close();
			}
		}
	}

	public void deleteNumber(String number) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from blacknumber where number = ?", new Object[] { number });
			db.close();
		}
	}

	/**
	 * 修改黑名单
	 * 
	 * @param number
	 */
	public void updateNumber(String oldNumber, String newNumber) {
		if (!find(oldNumber)) {
			// 如果存在此数据库就返回
			return;
		} else {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			if (db.isOpen()) {
				db.execSQL("update blacknumber set number = ?  where number = ?", new Object[] { newNumber, oldNumber });
				db.close();
			}
		}
	}

	public List<String> findAll() {
		List<String> list = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select number from blacknumber", null);
			while (cursor.moveToNext()) {
				list.add(cursor.getString(0));
			}
			cursor.close();
			db.close();
		}
		return list;
	}

}
