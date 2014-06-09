package com.mobilesafe.ui;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mobilesafe.R;
import com.mobilesafe.adapt.AppManagerListAdapt;
import com.mobilesafe.domain.AppInfo;
import com.mobilesafe.engine.AppInfoProvider;

public class AppManagerActivity extends Activity {
	protected static final int GET_ALL_APP_FINISH = 10;
	ListView lv_main;
	LinearLayout ll_loading;
	List<AppInfo> list;
	AppManagerListAdapt adapt;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_ALL_APP_FINISH:
				// 设置可见
				ll_loading.setVisibility(View.INVISIBLE);
				// 把数据设置给 listview
				lv_main.setAdapter(adapt);
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_manager);
		
		lv_main = (ListView) this.findViewById(R.id.app_manager_lv_main);
		ll_loading = (LinearLayout) this.findViewById(R.id.app_manager_ll_loading);

		// 设置 不可见
		ll_loading.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {
				AppInfoProvider appInfoProvider = new AppInfoProvider(AppManagerActivity.this);
				list = appInfoProvider.getAllAppInfo();
				adapt = new AppManagerListAdapt(list, AppManagerActivity.this);
				
				Message msg = new Message();
				msg.what = GET_ALL_APP_FINISH;
				handler.sendMessage(msg);
				super.run();
			}

		}.start();

	}
}
