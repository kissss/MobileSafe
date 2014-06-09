package com.mobilesafe.ui;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.utils.MD5Encrypt;
import com.mobilesafe.utils.ViewUtils;

public class LostProtectedActivity extends Activity implements OnClickListener {

	private static final String TAG = "LostProtectedActivity";
	private SharedPreferences sp;
	private Dialog dialog;
	private EditText editTextPwd, editTextRePwd, normalEditTextPwd;
	private TextView lost_protect_tv_protectNumber;
	private Button lost_protect_btn_reSetup;
	private CheckBox lost_protect_cb_isProtect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 判断用户是否已经设置密码
		if (isPwdSetUp()) {
			Log.i(TAG, "表示设置了密码 进入正常的对话框");
			showNormalEntryDialog();

		} else {
			Log.i(TAG, "表示没有设置密码进入第一次设置密码的对话框");
			showFirstEntryDialog();
		}
	}

	/**
	 * 正常登录的对话框
	 */
	private void showNormalEntryDialog() {
		dialog = new Dialog(this, R.style.MyDialog);
		View view = View.inflate(this, R.layout.phoneprotect_normal_entry_dialog, null);
		dialog.setContentView(view);

		normalEditTextPwd = (EditText) view.findViewById(R.id.normal_entry_dialog_et_rePwd);

		Button normal_Confirm = (Button) view.findViewById(R.id.normal_entry_dialog_btn_confirm);
		Button normal_Cancel = (Button) view.findViewById(R.id.normal_entry_dialog_btn_cancel);

		normal_Confirm.setOnClickListener(this);
		normal_Cancel.setOnClickListener(this);
		dialog.show();
	}

	/**
	 * 第一次进入时候的界面
	 */
	private void showFirstEntryDialog() {
		dialog = new Dialog(this, R.style.MyDialog);
		// 这样的方法 不方便拿到view 里面的窗体
		// dialog.setContentView(R.layout.first_entry_dialog);
		View view = View.inflate(this, R.layout.phoneprotect_first_entry_dialog, null);

		editTextPwd = (EditText) view.findViewById(R.id.first_entry_dialog_et_pwd);
		editTextRePwd = (EditText) view.findViewById(R.id.first_entry_dialog_et_rePwd);

		Button btnConfirm = (Button) view.findViewById(R.id.first_entry_dialog_btn_confirm);
		Button btnCancel = (Button) view.findViewById(R.id.first_entry_dialog_btn_cancel);

		btnConfirm.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		dialog.setContentView(view);
		dialog.show();
	}

	/**
	 * 判断用户是否已经设置了密码
	 * 
	 * @return true 表示已经设置了密码
	 */
	private boolean isPwdSetUp() {
		String password = sp.getString("password", null);
		if (password != null && !"".equals(password)) {
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 第一次登录确认按钮
		case R.id.first_entry_dialog_btn_confirm:
			String pwd = editTextPwd.getText().toString().trim();
			String rePwd = editTextRePwd.getText().toString().trim();
			if ("".equals(pwd) || "".equals(rePwd)) {
				ViewUtils.showToastShort(getApplicationContext(), "密码不能为空");
				return;
			} else {
				if (pwd.equals(rePwd)) {
					Editor editor = sp.edit();
					editor.putString("password", MD5Encrypt.encrypt(pwd));
					editor.commit();
				} else {
					ViewUtils.showToastShort(getApplicationContext(), "密码不一致");
					return;
				}
			}
			dialog.dismiss();
			finish();
			break;

		// 第一次登录框的确认
		case R.id.first_entry_dialog_btn_cancel:
			dialog.dismiss();
			finish();
			break;
		// 正常登录的 确认
		case R.id.normal_entry_dialog_btn_confirm:
			String normalPwd = normalEditTextPwd.getText().toString().trim();
			if ("".equals(normalPwd)) {
				ViewUtils.showToastShort(getApplicationContext(), "密码不能为空");
				return;
			} else {
				String password = sp.getString("password", null);
				if (MD5Encrypt.encrypt(normalPwd).equals(password)) {
					Log.i(TAG, "判断用户是否进行向导设置");
					// 是否进行向导设置
					// true 表示已经进行过向导设置
					if (isSetUp()) {
						Log.i(TAG, "手机加载防盗界面");
						setContentView(R.layout.lost_protect);
						// 显示保护的号码
						lost_protect_tv_protectNumber = (TextView) this.findViewById(R.id.lost_protect_tv_protectNumber);
						// 重新进行向导设置
						lost_protect_btn_reSetup = (Button) this.findViewById(R.id.lost_protect_btn_reSetup);
						// 是否开启保护的勾选框
						lost_protect_cb_isProtect = (CheckBox) this.findViewById(R.id.lost_protect_cb_isProtect);
						// 进行空间的初始化
						// 安全号码
						String safeNumber = sp.getString("safeNumber", "");
						lost_protect_tv_protectNumber.setText("手机的安全号码为：" + safeNumber);
						// 手机选择框的初始化
						boolean isSetProtect = sp.getBoolean("isSetupProtect", false);
						
						if(isSetProtect){
							lost_protect_cb_isProtect.setText(R.string.setupguide4_cb_content_on);
							lost_protect_cb_isProtect.setChecked(true);
						}

						// 重新进行向导设置的点击事件
						lost_protect_btn_reSetup.setOnClickListener(this);
						lost_protect_cb_isProtect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								if (isChecked) {
									lost_protect_cb_isProtect.setText(R.string.setupguide4_cb_content_on);
									Editor editor = sp.edit();
									editor.putBoolean("isSetupProtect", true);
									editor.commit();
								} else {
									lost_protect_cb_isProtect.setText(R.string.setupguide4_cb_content_off);
									Editor editor = sp.edit();
									editor.putBoolean("isSetupProtect", false);
									editor.commit();

								}
							}
						});

					} else {
						Log.i(TAG, "进行向导设置");
						finish();
						Intent intent = new Intent(LostProtectedActivity.this, SetupGuideActivity1.class);
						startActivity(intent);
					}

				} else {
					ViewUtils.showToastShort(getApplicationContext(), "密码不正确");
					Log.i(TAG, "手机密码不正确");
					return;
				}
			}
			dialog.dismiss();
			break;
		// 正常登录的取消
		case R.id.normal_entry_dialog_btn_cancel:
			dialog.dismiss();
			finish();
			break;
		// 重新设置的 点击按钮
		case R.id.lost_protect_btn_reSetup:
			Log.i(TAG, "重新设置向导");
			finish();
			Intent intent = new Intent(LostProtectedActivity.this, SetupGuideActivity1.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	/**
	 * 判断用户是否进行过设置向导
	 * 
	 * @return true 表示已经进行过设置
	 */
	private boolean isSetUp() {
		return sp.getBoolean("isSetUp", false);
	}

}
