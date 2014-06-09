package com.mobilesafe.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.engine.NumberAddressService;

public class QueryNmberActivity extends Activity {
	TextView tv_phoneAddress;
	EditText et_phoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.atools_query_phone);
		tv_phoneAddress = (TextView) this.findViewById(R.id.atools_queryPhone_tv_phoneNumberAddress);
		et_phoneNumber = (EditText) this.findViewById(R.id.atools_queryPhone_et_phoneNumber);
	}

	public void query(View view) {
		// 先判断手机号码是否为空
		String number = et_phoneNumber.getText().toString().trim();
		// 如果 电话号为空
		if (TextUtils.isEmpty(number)) {
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_phoneNumber.startAnimation(shake);
		} else {
			// 如果手机号码不为空 打开数据库 查看手机归属地
			String city = NumberAddressService.getAddress(number);
			tv_phoneAddress.setText("归属地信息为：" + city);
		}

	}
}
