package com.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.mobilesafe.domain.AppInfo;

public class AppInfoProvider {
	private static final String TAG = "AppInfoProvider";
	Context context;
	PackageManager packageManager;

	public AppInfoProvider(Context context) {
		super();
		this.context = context;
		packageManager = context.getPackageManager();
	}

	public List<AppInfo> getAllAppInfo() {
		List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		List<AppInfo> list = new ArrayList<AppInfo>();
		for (PackageInfo packageInfo : packageInfoList) {
			AppInfo appinfo = new AppInfo();

			String packageNmae = packageInfo.packageName;
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;

			Drawable drawable = applicationInfo.loadIcon(packageManager);
			String appLabel = applicationInfo.loadLabel(packageManager).toString();
			// 为true表示的是第三方应用
			if (filterApp(applicationInfo)) {
				Log.i(TAG, "三方应用");
				appinfo.setSystemApp(false);
			} else {
				Log.i(TAG, "系统应用");
				appinfo.setSystemApp(true);
			}
			appinfo.setAppName(appLabel);
			appinfo.setIcon(drawable);
			appinfo.setPackName(packageNmae);
			list.add(appinfo);
		}
		Log.i(TAG, "系统软件:" + JSON.toJSONString(list));
		return list;
	}

	private boolean filterApp(ApplicationInfo info) {
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			return true;
		}
		return false;
	}
}
