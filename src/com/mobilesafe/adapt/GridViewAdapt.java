package com.mobilesafe.adapt;

import com.mobilesafe.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewAdapt extends BaseAdapter {
	private static String[] names = { "手机防盗", "通信卫士", "软件管理", "任务管理", "流量管理 ", "手机杀毒 ", "系统优化 ", "高级工具", "设置中心" };
	private static int[] icons = { R.drawable.widget05, R.drawable.widget02, R.drawable.widget01, R.drawable.widget07, R.drawable.widget05, R.drawable.widget04, R.drawable.widget06, R.drawable.widget03, R.drawable.widget08, };
	private static final String TAG = null;
	private SharedPreferences sp;

	private static ImageView iv_icon;
	private static TextView tv_name;

	Context context;
	LayoutInflater inflater;

	public GridViewAdapt(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		// sharepreference 的初始化
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
	}

	@Override
	public int getCount() {
		return names.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.e(TAG, "调用 " + position);
		View view = inflater.inflate(R.layout.main_mainscreen_item, null);
		// 优化内存 使用静态的变量引用
		iv_icon = (ImageView) view.findViewById(R.id.mainscreen_item_iv_image);
		tv_name = (TextView) view.findViewById(R.id.mainscreen_item_tv_name);
		iv_icon.setImageResource(icons[position]);
		tv_name.setText(names[position]);
		if (position == 0) {
			String name = sp.getString("lost_name", null);
			if (name != null) {
				tv_name.setText(name);
			}
		}
		return view;
	}

}
