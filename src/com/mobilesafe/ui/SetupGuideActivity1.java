package com.mobilesafe.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mobilesafe.R;
import com.mobilesafe.utils.ViewUtils;

public class SetupGuideActivity1 extends Activity implements OnClickListener {

	private Button setupguide1_btn_next;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.setupguide1);
		super.onCreate(savedInstanceState);
		setupguide1_btn_next = (Button) this.findViewById(R.id.setupguide1_btn_next);

		setupguide1_btn_next.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setupguide1_btn_next:
			ViewUtils.changeActivity(this, SetupGuideActivity2.class);
			break;

		default:
			break;
		}
	}

}
