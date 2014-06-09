package com.mobilesafe.utils;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

public class HttpClientUtils {

	public static InputStream getInputStreamByGetMethod(String httpUrl) throws Exception {
		InputStream is = null;
		HttpClient httpclient = new DefaultHttpClient();

		// 请求超时
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
		// 读取超时
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
		HttpGet httpGet = new HttpGet(httpUrl);
		HttpResponse response = httpclient.execute(httpGet);
		// 说明链接请求成功
		if (response.getStatusLine().getStatusCode() == 200) {
			is = response.getEntity().getContent();
		}
		return is;
	}
}
