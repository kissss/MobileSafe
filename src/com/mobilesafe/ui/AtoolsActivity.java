package com.mobilesafe.ui;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.engine.DownLoadFileTask;
import com.mobilesafe.engine.SmsInfoService;
import com.mobilesafe.service.AddressService;
import com.mobilesafe.service.BackupSmsService;
import com.mobilesafe.utils.ViewUtils;

public class AtoolsActivity extends Activity implements OnClickListener {

	protected static final int ERROR = 0;
	protected static final int SUCCESS = 1;
	private Button btn_phoneQuery;
	private ProgressDialog pd;
	private TextView atools_tv_phoneService, tv_locationStyle, tv_changeLocation, tv_smsBackup, tv_smsRestore;
	private CheckBox atools_cb_phoneService;
	private Intent serviceIntent;
	private SharedPreferences sp;
	SmsInfoService service;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == ERROR) {
				ViewUtils.showToastLong(getApplicationContext(), "下载数据库失败");
			} else if (msg.what == SUCCESS) {
				ViewUtils.showToastLong(getApplicationContext(), "下载数据库成功");
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.atools);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 手机归属查询功能
		atools_tv_phoneService = (TextView) this.findViewById(R.id.atools_tv_phoneService);
		atools_cb_phoneService = (CheckBox) this.findViewById(R.id.atools_cb_phoneService);
		btn_phoneQuery = (Button) this.findViewById(R.id.atools_btn_phoneQuery);

		// 开启手机归属地的 那个服务
		serviceIntent = new Intent(this, AddressService.class);

		// 展示手机归属地的风格
		tv_locationStyle = (TextView) this.findViewById(R.id.atools_tv_locationStyle);

		// 改变手机归属地的位置
		tv_changeLocation = (TextView) this.findViewById(R.id.atools_tv_changeLocation);

		// 短信的备份和还原
		tv_smsBackup = (TextView) this.findViewById(R.id.atools_tv_sms_backup);
		tv_smsRestore = (TextView) this.findViewById(R.id.atools_tv_sms_restore);

		atools_cb_phoneService.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					startService(serviceIntent);
					atools_tv_phoneService.setText(R.string.atools_tv_text_service_on);
					atools_tv_phoneService.setTextColor(R.color.green);
				} else {
					stopService(serviceIntent);
					atools_tv_phoneService.setText(R.string.atools_tv_text_service_off);
					atools_tv_phoneService.setTextColor(R.color.red);
				}
			}
		});

		btn_phoneQuery.setOnClickListener(this);
		tv_locationStyle.setOnClickListener(this);
		tv_changeLocation.setOnClickListener(this);
		tv_smsBackup.setOnClickListener(this);
		tv_smsRestore.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.atools_btn_phoneQuery:
			// 判断来电归属地是否存在 如果不存在提示用户去下载数据库
			// 数据库存在
			if (isDBExist()) {
				Intent intent = new Intent(this, QueryNmberActivity.class);
				startActivity(intent);
			} else {
				// 提示用户下载数据库
				pd = new ProgressDialog(this);
				pd.setMessage("正在下载数据库");
				// 如果progress 没有显示 是因为没有指定 progressdialog 的样式
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.show();

				new Thread() {
					@Override
					public void run() {
						try {
							String url = getResources().getString(R.string.addressDBUrl);
							String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/address.db";
							DownLoadFileTask.getFile(url, filePath, pd);
							Message message = new Message();
							message.what = SUCCESS;
							handler.sendMessage(message);
							pd.dismiss();
						} catch (Exception e) {
							Message message = new Message();
							message.what = ERROR;
							handler.sendMessage(message);
							pd.dismiss();
							e.printStackTrace();
						}
					}
				}.start();
			}
			break;

		case R.id.atools_tv_locationStyle:// 手机归属地 风格的展示
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("设置手机归属地显示风格");
			String[] items = { "半透明", "活力橙", "苹果绿" };
			// 对应item 的点击事件
			builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Editor editor = sp.edit();
					editor.putInt("phoneLocationBackground", which);
					editor.commit();
				}
			});
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			builder.create().show();
			break;
		case R.id.atools_tv_changeLocation: // 手机归属地显示的 位置
			Intent intent = new Intent(this, DragViewActivity.class);
			startActivity(intent);
			break;
		case R.id.atools_tv_sms_backup: // 手机短信的备份
			Intent smsBackupintent = new Intent(this, BackupSmsService.class);
			startService(smsBackupintent);
			break;
		case R.id.atools_tv_sms_restore: // 手机短信还原
			// 读取备份中的xml文件 解析里面的数据 插入到数据库中
			service = new SmsInfoService(this);
			try {
				final ProgressDialog pd = new ProgressDialog(this);
				pd.setMessage("正在还原短信");
				pd.setCancelable(false);
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.show();
				new Thread() {
					@Override
					public void run() {
						try {
							service.restoreSMS("smsback.xml", pd);
							pd.dismiss();
							Looper.prepare();
							ViewUtils.showToastShort(getApplicationContext(), "短信已经恢复 ");
							Looper.loop();
						} catch (Exception e) {
							pd.dismiss();
							e.printStackTrace();
							Looper.prepare();
							ViewUtils.showToastShort(getApplicationContext(), "短信恢复出错 ");
							Looper.loop();
						}
					}

				}.start();
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		default:
			break;
		}
	}

	/**
	 * 判断数据库是否存在 如果存在 则返回true
	 * 
	 * @return
	 */
	private boolean isDBExist() {
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/address.db");
		return file.exists();
	}

}
