package com.mobilesafe.adapt;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.domain.AppInfo;

public class AppManagerListAdapt extends BaseAdapter {

	private static final String TAG = "AppManagerListAdapt";
	List<AppInfo> list;
	Context context;
	private static ImageView iv_icon;
	private static TextView tv_appNmae;

	public AppManagerListAdapt(List<AppInfo> list, Context context) {
		super();
		this.list = list;
		this.context = context;
	}

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

	/**
	 * convertView 转换的view 对象 历史view 对象的缓存 就是拖动的时候被回收的view 对象
	 * 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AppInfo info = list.get(position);
		View view;
		if (convertView == null) {
			Log.i(TAG, "通过资源文件创建view 对象");
			view = View.inflate(context, R.layout.app_manager_item, null);
		} else {
			Log.i(TAG, "使用listview 缓存");
			view = convertView;

		}

		iv_icon = (ImageView) view.findViewById(R.id.app_manager_item_iv_icon);
		tv_appNmae = (TextView) view.findViewById(R.id.app_manager_item_tv_appName);

		iv_icon.setImageDrawable(info.getIcon());
		tv_appNmae.setText(info.getAppName());
		return view;
	}
}
