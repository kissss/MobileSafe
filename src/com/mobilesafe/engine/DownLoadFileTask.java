package com.mobilesafe.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.app.ProgressDialog;

import com.mobilesafe.utils.FileUtils;
import com.mobilesafe.utils.HttpClientUtils;

public class DownLoadFileTask {

	/**
	 * @param path
	 *            服务器文件的url
	 * @param filePath
	 *            本地文件的路径
	 * @return
	 * @throws Exception
	 */
	public static File getFile(String path, String filePath) throws Exception {
		InputStream is = HttpClientUtils.getInputStreamByGetMethod(path);
		// 将服务器的文件 写入SD卡中
		File file = FileUtils.writeFile(is, filePath);
		return file;
	}

	/**
	 * @param path 文件的url 地址
	 * @param filePath 报错文件的位置
	 * @param pd
	 *            带下载进度条
	 * @return
	 * @throws Exception
	 */
	public static File getFile(String path, String filePath, ProgressDialog pd) throws Exception {
		InputStream is = null;
		HttpClient httpclient = new DefaultHttpClient();
		// 请求超时
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
		// 读取超时
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 1000);

		HttpGet httpGet = new HttpGet(path);
		HttpResponse response = httpclient.execute(httpGet);
		// 说明链接请求成功
		if (response.getStatusLine().getStatusCode() == 200) {
			is = response.getEntity().getContent();
		}
		pd.setMax((int) response.getEntity().getContentLength());
		int process = 0;
		File file = new File(filePath);
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			Thread.sleep(50);
			fos.write(buffer, 0, len);
			process += len;
			pd.setProgress(process);
		}
		is.close();
		fos.close();
		fos.flush();
		return file;
	}
}
