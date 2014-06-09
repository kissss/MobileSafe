package com.mobilesafe.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.adapt.GridViewAdapt;
import com.mobilesafe.utils.ViewUtils;

public class MainActivity extends Activity implements OnItemClickListener {
	private static final String TAG = "MainActivity";
	GridView gv_main;
	// 需要持久化一些配置信息
	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_mainscreen);

		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		gv_main = (GridView) this.findViewById(R.id.mainscreen_gv_main);
		gv_main.setAdapter(new GridViewAdapt(this));
		// 设置girdView 的点击事件
		gv_main.setOnItemClickListener(this);

		// 设置girdView 的 长点击事件
		gv_main.setOnItemLongClickListener(new OnItemLongClickListener() {
			/**
			 * 长按按钮的点击事件
			 */
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
				if (position == 0) {

					AlertDialog.Builder builder = new Builder(MainActivity.this);
					builder.setTitle("设置");
					builder.setMessage(" 请输入要更改的名称");
					final EditText editText = new EditText(MainActivity.this);
					editText.setHint("请输入文本");
					builder.setView(editText);
					builder.setPositiveButton("确定", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String name = editText.getText().toString().trim();
							if ("".equals(name)) {
								ViewUtils.showToastShort(getApplicationContext(), "更改的名称不能为空");
							} else {
								Editor editor = sp.edit();
								editor.putString("lost_name", name);
								// 一定要完成数据的提交
								editor.commit();
								// 修改图标里面的值
								TextView tv = (TextView) view.findViewById(R.id.mainscreen_item_tv_name);
								tv.setText(name);
							}
						}
					});
					builder.setNegativeButton("取消", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					builder.create().show();
				}
				return false;
			}
		});

	}

	/**
	 * 当 gridadapt 被调用时 对应的回调 parent gridView view 当前被点击的条目 的linerlaylout
	 * position 被点击对应条目的位置
	 * 
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.e(TAG, position + "");
		switch (position) {
		case 0:// 表示手机防盗的功能
			Log.e(TAG, "进入防盗");
			Intent lostIntent = new Intent(MainActivity.this, LostProtectedActivity.class);
			startActivity(lostIntent);
			break;
		case 1:// 手机通讯卫士
			Log.e(TAG, "进入手机通讯卫士");
			Intent safeIntent = new Intent(MainActivity.this, CallAndSmsSecurityActivity.class);
			startActivity(safeIntent);
			break;
		case 2:// 软件管家
			Log.e(TAG, "进入手机软件管家");
			Intent appManagerIntent = new Intent(MainActivity.this, AppManagerActivity.class);
			startActivity(appManagerIntent);
			break;
		case 7:// 手机高级工具功能
			Log.e(TAG, "进入高级工具菜单");
			Intent atoolesIntent = new Intent(MainActivity.this, AtoolsActivity.class);
			startActivity(atoolesIntent);
			break;

		default:
			break;
		}
	}

}
