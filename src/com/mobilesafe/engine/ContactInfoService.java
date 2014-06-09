package com.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.mobilesafe.domain.ContactInfo;

public class ContactInfoService {

	Context context;

	public ContactInfoService(Context context) {
		super();
		this.context = context;
	}

	public List<ContactInfo> getContactInfoList() {
		List<ContactInfo> contactInfo = new ArrayList<ContactInfo>();

		ContentResolver resolver = context.getContentResolver();
		// 1.根据联系人的id 获取联系人的姓名
		// 2.根据联系人的id 和数据的type 获取相对应的数据（电话，email，姓名)
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");
		Cursor cursor = resolver.query(uri, null, null, null, null);
		while (cursor.moveToNext()) {
			// 联系人的ID
			String id = cursor.getString(cursor.getColumnIndex("_id"));
			System.out.println("联系人的ID 为  ： " + id);
			Cursor dataCursor = resolver.query(dataUri, null, "raw_contact_id=?", new String[] { id }, null);
			ContactInfo info = new ContactInfo();

			while (dataCursor.moveToNext()) {
				String data = dataCursor.getString(dataCursor.getColumnIndex("data1"));
				String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
				// mimetype
				if ("vnd.android.cursor.item/name".equals(type)) {
					info.setName(data);
				} else if ("vnd.android.cursor.item/phone_v2".equals(type)) {
					info.setNumber(data);
				}
			}
			dataCursor.close();
			contactInfo.add(info);

		}
		cursor.close();
		return contactInfo;
	}
}
