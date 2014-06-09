package com.mobilesafe.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.mobilesafe.R;

public class DragViewActivity extends Activity {

	protected static final String TAG = "DragViewActivity";
	private TextView tv_title;
	private ImageView iv_drag;
	// 图片第一次的坐标位置
	private int startX, startY;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		sp = getSharedPreferences("config", MODE_PRIVATE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.atools_drag_view);

		// 加载上次的imageView
		tv_title = (TextView) this.findViewById(R.id.atools_drag_view_tv_text);
		iv_drag = (ImageView) this.findViewById(R.id.atools_drag_view_iv_drag);

		// 图片的触摸事件
		iv_drag.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 如果手机放在图片上面触摸
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: // 手机按下
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_MOVE: // 手机没有离开屏幕 在屏幕上面移动
					int x = (int) event.getRawX();
					int y = (int) event.getRawY();

					if (y < 240) {
						// 设置文本框在 图片的下面
						tv_title.layout(tv_title.getLeft(), 260, tv_title.getRight(), 280);
					} else {
						tv_title.layout(tv_title.getLeft(), 60, tv_title.getRight(), 80);

					}

					// 获取手指移动的距离
					int dx = x - startX;
					int dy = y - startY;

					int l = iv_drag.getLeft();
					int t = iv_drag.getTop();
					int r = iv_drag.getRight();
					int b = iv_drag.getBottom();

					iv_drag.layout(l + dx, t + dy, r + dx, b + dy);

					// 获取移动后的位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP: // 手机离开屏幕对应的事件
					Log.i(TAG, "手机离开屏幕");
					int lastX = iv_drag.getLeft();
					int lastY = iv_drag.getTop();

					Editor editor = sp.edit();
					editor.putInt("phoneAddressLocationX", lastX);
					editor.putInt("phoneAddressLocationY", lastY);
					editor.commit();

					break;
				}
				// 不会中断触摸事件的返回
				return true;
			}
		});

	}

	@Override
	protected void onResume() {
		// 重新加载这个view
		int x = sp.getInt("phoneAddressLocationX", 0);
		int y = sp.getInt("phoneAddressLocationY", 0);
		// iv_drag.layout(iv_drag.getLeft() + x, iv_drag.getTop() + y,
		// iv_drag.getRight() + x, iv_drag.getBottom() + y);
		// 重新渲染这个控件
		// iv_drag.invalidate();

		Log.i(TAG, x + "   " + y);
		// 通过布局 去更改 iv_drag 中的位置
		LayoutParams params = (LayoutParams) iv_drag.getLayoutParams();
		params.leftMargin = x;
		params.topMargin = y;
		iv_drag.setLayoutParams(params);
		super.onResume();
	}
}
