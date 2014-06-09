package com.mobilesafe.ui;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilesafe.R;
import com.mobilesafe.domain.UpdateInfo;
import com.mobilesafe.engine.DownLoadFileTask;
import com.mobilesafe.engine.UpdateInfoService;
import com.mobilesafe.utils.AndroidUtils;
import com.mobilesafe.utils.ViewUtils;

public class SplashActivity extends Activity {

	private static final String TAG = "SplashActivity";
	TextView splash_tv_version;
	LinearLayout splash_ll_main;
	UpdateInfo updateInfo;
	ProgressDialog progressDialog;
	// 当前系统的版本号
	String version;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 判断服务器的版本号 和客户端的版本号是否相同 表示需要升级
			if (isNeedUpdate(version)) {
				showUpdateDialog();
			} else {
				Log.e(TAG, "服务器版本相同 进入主界面 ");
				loadMainUI();
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 取消标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		splash_ll_main = (LinearLayout) this.findViewById(R.id.splash_ll_main);
		splash_tv_version = (TextView) this.findViewById(R.id.splash_tv_version);

		progressDialog = new ProgressDialog(this);
		// 水平格式的进度条
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMessage("正在下载.....");

		version = getVersion();
		// 让当前的Activity 睡眠2秒钟
		new Thread() {
			@Override
			public void run() {
				try {
					sleep(2000);
					handler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();

		AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(2000);
		splash_ll_main.setAnimation(animation);

		splash_tv_version.setText(version);

	}

	/**
	 * 显示更新的对话框
	 */
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setIcon(R.drawable.icon5);
		builder.setTitle("升级提醒");
		builder.setMessage(updateInfo.getDescription());
		// 用户不能取消对话框
		builder.setCancelable(false);
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "点击确定 下载 apk   " + updateInfo.getApkUrl());

				// 如果SD卡可用
				if (AndroidUtils.sdCardaVailable()) {
					DownLoadFileThreadTask downTask = new DownLoadFileThreadTask(updateInfo.getApkUrl(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/new.apk");
					// 开启一个 进度条
					progressDialog.show();
					// 开始一个新的线程
					new Thread(downTask).start();

				} else {
					ViewUtils.showToastShort(getApplicationContext(), "SD卡不可用");
					loadMainUI();
				}

			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				loadMainUI();
				Log.i(TAG, "点击取消 程序进入主界面");

			}
		});
		builder.create().show();
	}

	/**
	 * 当前客户端的版本信息
	 * 
	 * @param version
	 * @return
	 */
	private boolean isNeedUpdate(String version) {
		UpdateInfoService updateInfoService = new UpdateInfoService(this);
		try {
			updateInfo = updateInfoService.getUpdateInfo(R.string.updateUrl);
			// 服务器端的版本
			String currentVersion = updateInfo.getVersion();
			if (currentVersion.equals(version)) {
				Log.i(TAG, "版本相同，无需升级，进入主界面");
				loadMainUI();
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			Toast.makeText(this, "获取信息异常", Toast.LENGTH_SHORT).show();
			Log.i(TAG, "获取信息异常");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 得到服务器的版本
	 * 
	 * @return
	 */
	private String getVersion() {
		PackageManager manage = getPackageManager();
		try {
			PackageInfo info = manage.getPackageInfo(getPackageName(), 0);
			Log.e(TAG, "当前软件的版本为 ：" + info.versionName);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "版本号未知";
	}

	/**
	 * 显示主界面
	 */
	private void loadMainUI() {
		Log.e(TAG, "加载主界面");
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		// 结束当前的logo 显示界面
		finish();

	}

	public void installAPk(File file) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		startActivity(intent);
		// 结束当前的Activity
		finish();
	}

	private class DownLoadFileThreadTask implements Runnable {
		// 服务器文件的url
		private String url;
		// SD卡中的文件路径
		private String filePath;

		public DownLoadFileThreadTask(String url, String filePath) {
			this.url = url;
			this.filePath = filePath;
		}

		@Override
		public void run() {
			try {
				Log.i(TAG, "下载APK文件");
				File file = DownLoadFileTask.getFile(url, filePath, progressDialog);
				// 安装下载好的APK
				installAPk(file);

				Log.i(TAG, "APK文件下载成功");
			} catch (Exception e) {
				e.printStackTrace();
				ViewUtils.showToastShort(getApplicationContext(), "文件下载失败");
				loadMainUI();
			}
			// 进度条销毁
			progressDialog.dismiss();

		}
	}
}