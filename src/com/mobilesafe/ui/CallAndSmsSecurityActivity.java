package com.mobilesafe.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.db.dao.BlackNumberDao;
import com.mobilesafe.utils.ViewUtils;

public class CallAndSmsSecurityActivity extends Activity implements OnClickListener {

	private static final String TAG = "CallAndSmsSecurityActivity";
	Button btn_insert;
	ListView lv_content;

	BlackNumberDao dao;
	MyArrayAdapt adapter;
	List<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_sms_security);

		dao = new BlackNumberDao(this);

		btn_insert = (Button) this.findViewById(R.id.call_sms_security_btn_insert);
		lv_content = (ListView) this.findViewById(R.id.call_sms_security_lv_content);

		btn_insert.setOnClickListener(this);

		// 初始化 这个数据
		list = dao.findAll();
		// adapter = new ArrayAdapter<String>(this,
		// R.layout.call_sms_security_blacknumber_item,
		// R.id.blacknumber_item_tv_blacknumber_item, list);
		adapter = new MyArrayAdapt();
		lv_content.setAdapter(adapter);

		// 注册上下文菜单
		registerForContextMenu(lv_content);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.call_sms_security_btn_insert: // 按钮添加黑名单的操作
			showInputNumber("");
			break;

		default:
			break;
		}
	}

	/**
	 * 设置文本框的号码
	 * 
	 * @param number
	 */
	private void showInputNumber(String number) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("添加黑名单");
		final EditText et = new EditText(this);
		et.setInputType(InputType.TYPE_CLASS_PHONE);

		// 不为空时 设置它的文本框
		if (!TextUtils.isEmpty(number)) {
			et.setText(number);
		}

		final String insertNumber = et.getText().toString().trim();

		builder.setView(et);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (TextUtils.isEmpty(insertNumber)) {
					ViewUtils.showToastShort(getApplicationContext(), "黑名单号码不能为空");
					return;
				} else {
					dao.insertNumber(insertNumber);
					// 通知listview 更新数据
					// 第一种： 重新去构建listview 这样会刷新整个listview
					// btn_insert.setOnClickListener(this);
					// lv_content.setAdapter(new ArrayAdapter<String>(this,
					// R.layout.call_sms_security_blacknumber_item,
					// R.id.blacknumber_item_tv_blacknumber_item,
					// dao.findAll()));
					// 第二种 由于缓存 所以没有实现
					list = dao.findAll();
					adapter.notifyDataSetChanged();

				}

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create().show();
	}

	private class MyArrayAdapt extends BaseAdapter {

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
			View view = View.inflate(CallAndSmsSecurityActivity.this, R.layout.call_sms_security_blacknumber_item, null);
			TextView tv = (TextView) view.findViewById(R.id.blacknumber_item_tv_blacknumber_item);
			tv.setText(list.get(position));
			return view;
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.call_safe_context_menu, menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		// 当前条目的 position
		int id = (int) info.id;
		String number = list.get(id);
		switch (item.getItemId()) {
		case R.id.call_safe_context_menu_delete: // 删除黑名单
			dao.deleteNumber(number);
			// 通知listview 更新界面
			list = dao.findAll();
			adapter.notifyDataSetChanged();
			return true;
		case R.id.call_safe_context_menu_update: // 更新黑名单
			updateBlackNumber(number);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void updateBlackNumber(final String number) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("更改黑名单");
		final EditText et = new EditText(this);
		et.setInputType(InputType.TYPE_CLASS_PHONE);
		builder.setView(et);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String insertNumber = et.getText().toString().trim();

				if (TextUtils.isEmpty(insertNumber)) {
					ViewUtils.showToastShort(getApplicationContext(), "黑名单号码不能为空");
					return;
				} else {
					dao.updateNumber(number, insertNumber);
					list = dao.findAll();
					adapter.notifyDataSetChanged();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.create().show();
	}

	// 当界面变得可见的时候 调用的方法
	@Override
	protected void onStart() {
		Intent intent = getIntent();
		String number = intent.getStringExtra("number");
		if (!TextUtils.isEmpty(number)) {
			Log.i(TAG, "提示用户添加此号码进黑名单");
			showInputNumber(number);
		}
		super.onStart();
	}
}
