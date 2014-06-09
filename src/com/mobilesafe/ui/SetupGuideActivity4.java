package com.mobilesafe.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.mobilesafe.R;
import com.mobilesafe.receiver.MyAdmin;
import com.mobilesafe.utils.ViewUtils;

public class SetupGuideActivity4 extends Activity implements OnClickListener {

	private static final String TAG = "SetupGuideActivity4";
	private Button btn_previous, btn_finish;
	private CheckBox cb_protect;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.setupguide4);
		super.onCreate(savedInstanceState);

		sp = getSharedPreferences("config", MODE_PRIVATE);

		boolean isSetupProtect = sp.getBoolean("isSetupProtect", false);

		btn_previous = (Button) this.findViewById(R.id.setupguide4_btn_previous);
		btn_finish = (Button) this.findViewById(R.id.setupguide4_btn_finish);
		cb_protect = (CheckBox) this.findViewById(R.id.setupguide4_cb_protectSetUp);

		btn_previous.setOnClickListener(this);
		btn_finish.setOnClickListener(this);

		// 表示已经进行过设置
		if (isSetupProtect) {
			cb_protect.setText(R.string.setupguide4_cb_content_on);
			cb_protect.setChecked(true);
		}

		cb_protect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_protect.setText(R.string.setupguide4_cb_content_on);
					Editor editor = sp.edit();
					editor.putBoolean("isSetupProtect", true);
					editor.commit();
				} else {
					cb_protect.setText(R.string.setupguide4_cb_content_off);
					Editor editor = sp.edit();
					editor.putBoolean("isSetupProtect", false);
					editor.commit();

				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setupguide4_btn_previous:
			ViewUtils.changeActivity(this, SetupGuideActivity3.class);
			break;
		case R.id.setupguide4_btn_finish:

			// checkbox 被选中
			if (cb_protect.isChecked()) {
				finishSetUp();
				finish();
			} else {
				AlertDialog.Builder builder = new Builder(SetupGuideActivity4.this);
				builder.setTitle("警告");
				builder.setMessage("强烈建议您开启手机防盗，是否完成设置");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						finishSetUp();
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				builder.create().show();
				return;
			}

			break;

		default:
			break;
		}
	}

	private void finishSetUp() {
		Editor editor = sp.edit();
		editor.putBoolean("isSetUp", true);
		editor.commit();
		DevicePolicyManager manager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		ComponentName mAdminName = new ComponentName(this, MyAdmin.class);
		// 如果没有激活这个权限
		if (!manager.isAdminActive(mAdminName)) {
			Log.i(TAG, "激活管理员");
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
			startActivity(intent);
		}

	}

}
