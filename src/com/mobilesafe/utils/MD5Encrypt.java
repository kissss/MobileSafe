package com.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encrypt {
	/**
	 * MD5 加密
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String str) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bytes = digest.digest(str.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < bytes.length; i++) {
				String s = Integer.toHexString(0xff & bytes[i]);
				if (s.length() == 1) {
					s = "0" + s;
				}
				sb.append(s);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
}
