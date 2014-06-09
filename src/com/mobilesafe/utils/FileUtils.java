package com.mobilesafe.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {
	/**
	 * @param is
	 * @param filePath
	 *            存储文件的位置
	 * @throws Exception
	 */
	public static File writeFile(InputStream is, String filePath) throws Exception {
		File file = new File(filePath);
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			fos.write(buffer, 0, len);
		}
		is.close();
		fos.close();
		fos.flush();
		return file;
	}
}
