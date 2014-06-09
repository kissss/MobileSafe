package com.mobilesafe.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.mobilesafe.R;
import com.mobilesafe.utils.ViewUtils;

public class SetupGuideActivity2 extends Activity implements OnClickListener {

	private Button btn_bind, btn_next, btn_previous;
	private CheckBox checkBox;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.setupguide2);
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		btn_bind = (Button) this.findViewById(R.id.setupguide2_btn_bind);
		btn_next = (Button) this.findViewById(R.id.setupguide2_btn_next);
		btn_previous = (Button) this.findViewById(R.id.setupguide2_btn_previous);
		checkBox = (CheckBox) this.findViewById(R.id.setupguide2_cb_bind);

		btn_bind.setOnClickListener(this);
		btn_next.setOnClickListener(this);
		btn_previous.setOnClickListener(this);
		// 首先初始化checkox的状态
		String sim = sp.getString("simSerialNumber", null);
		if (sim != null) {
			btn_bind.setText(R.string.setupguide2_cb_content_on);
			checkBox.setChecked(true);
		} else {
			btn_bind.setText(R.string.setupguide2_cb_content);
			checkBox.setChecked(false);
			resertSimInfo();
			
		}
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					saveSimInfo();
					btn_bind.setText(R.string.setupguide2_cb_content_on);
					checkBox.setText(R.string.setupguide2_cb_content_on);
				} else {
					//没有绑定
					btn_bind.setText(R.string.setupguide2_cb_content);
					checkBox.setText(R.string.setupguide2_cb_content);
					resertSimInfo();

				}
			}
		});
	}

	private void resertSimInfo() {
		Editor editor = sp.edit();
		editor.putString("simSerialNumber", null);
		editor.commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 绑定按钮
		case R.id.setupguide2_btn_bind:
			saveSimInfo();
			break;
		// 点击下一步
		case R.id.setupguide2_btn_next:
			ViewUtils.changeActivity(this, SetupGuideActivity3.class);

			break;
		// 点击上一步
		case R.id.setupguide2_btn_previous:
			ViewUtils.changeActivity(this, SetupGuideActivity1.class);
			break;

		default:
			break;
		}
	}

	private void saveSimInfo() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String simSerialNumber = telephonyManager.getSimSerialNumber();
		Editor editor = sp.edit();
		editor.putString("simSerialNumber", simSerialNumber);
		editor.commit();
	}

}
