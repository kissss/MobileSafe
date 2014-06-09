package com.mobilesafe.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.domain.ContactInfo;
import com.mobilesafe.engine.ContactInfoService;

public class SelectContactActivity extends Activity {

	private ListView lv;
	List<ContactInfo> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupguide3_selectcontact);
		
		list = new ContactInfoService(this).getContactInfoList();
		
		lv = (ListView) this.findViewById(R.id.selectcontact_lv_contactList);
		lv.setAdapter(new MyContactListAdapt());


		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String number = list.get(position).getNumber();
				Intent intent = new Intent();
				intent.putExtra("number", number);
				setResult(0, intent);
				finish();
			}
		});

	}

	private class MyContactListAdapt extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ContactInfo info = list.get(position);
			LinearLayout linearLayout = new LinearLayout(SelectContactActivity.this);
			linearLayout.setOrientation(LinearLayout.VERTICAL);

			TextView tvName = new TextView(SelectContactActivity.this);
			tvName.setText("姓名：" + info.getName());
			
			TextView tvPhone = new TextView(SelectContactActivity.this);
			tvPhone.setText("电话：" + info.getNumber());

			linearLayout.addView(tvName);
			linearLayout.addView(tvPhone);
			return linearLayout;
		}

	}

}
