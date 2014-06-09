package com.mobilesafe.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.mobilesafe.R;
import com.mobilesafe.utils.ViewUtils;

public class SetupGuideActivity3 extends Activity implements OnClickListener {

	private Button btn_previous, btn_next, btn_selectContact;
	private EditText et_selectContact;
	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.setupguide3);
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		btn_previous = (Button) this.findViewById(R.id.setupguide3_btn_previous);
		btn_next = (Button) this.findViewById(R.id.setupguide3_btn_next);
		btn_selectContact = (Button) this.findViewById(R.id.setupguide3_btn_selectContact);

		et_selectContact = (EditText) this.findViewById(R.id.setupgguide3_et_number);

		btn_previous.setOnClickListener(this);
		btn_next.setOnClickListener(this);
		btn_selectContact.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setupguide3_btn_next:
			String number = et_selectContact.getText().toString().trim();
			// 号码为空
			if ("".equals(number)) {
				ViewUtils.showToastShort(this, "号码不能为空");
				return;
			} else {
				//保存安全号码
				Editor editor = sp.edit();
				editor.putString("safeNumber", number);
				editor.commit();
				ViewUtils.changeActivity(this, SetupGuideActivity4.class);
			}
			break;
		case R.id.setupguide3_btn_previous:
			ViewUtils.changeActivity(this, SetupGuideActivity2.class);
			break;
		case R.id.setupguide3_btn_selectContact:
			Intent intent = new Intent(SetupGuideActivity3.this, SelectContactActivity.class);
			// 激活一个带返回值的界面
			startActivityForResult(intent, 0);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data != null) {
			String number = data.getStringExtra("number");
			et_selectContact.setText(number);
		}
	}

}
