package com.mobilesafe.engine;

import java.io.InputStream;

import android.content.Context;

import com.mobilesafe.domain.UpdateInfo;
import com.mobilesafe.utils.HttpClientUtils;

public class UpdateInfoService {
	private Context context;

	public UpdateInfoService(Context context) {
		super();
		this.context = context;
	}

	public UpdateInfo getUpdateInfo(int urlId) throws Exception {
		String urlPath = context.getResources().getString(urlId);
		InputStream inputStream = HttpClientUtils.getInputStreamByGetMethod(urlPath);
		UpdateInfo updateInfo = UpdateInfoParser.getUpdateInfo(inputStream);
		return updateInfo;
	}
}
