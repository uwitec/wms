package com.leqee.wms.api.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.TreeMap;

import com.leqee.wms.api.ApiException;

public abstract class LeqeeUtils {

	private static String getStringFromException(Throwable e) {
		String result = "";
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bos);
		e.printStackTrace(ps);
		try {
			result = bos.toString("UTF-8");
		} catch (IOException ioe) {
		}
		return result;
	}

	public static byte[] encryptMD5(String data) throws IOException {
		return encryptMD5(data.getBytes("UTF-8"));
	}

	public static byte[] encryptMD5(byte[] data) throws IOException {
		byte[] bytes = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			bytes = md.digest(data);
		} catch (GeneralSecurityException gse) {
			String msg = getStringFromException(gse);
			throw new IOException(msg);
		}
		return bytes;
	}

	public static String byte2hex(byte[] bytes) {
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex.toUpperCase());
		}
		return sign.toString();
	}
	

	/**
	 * 根据RequestMap生成map
	 * @param requestParamsMap
	 * @return
	 * @throws IOException 
	 */
	public static String createSign(TreeMap<String, String> requestParamsMap) {

		// 基本校验
		if (WorkerUtil.isNullOrEmpty(requestParamsMap)) {
			return "";
		}

		// 1、初始化相关参数
		StringBuffer originalSign = new StringBuffer();  //sign原文
		byte[] signBytes;

		Iterator<String> it = requestParamsMap.values().iterator();
		while (it.hasNext()) {
			originalSign.append(it.next());   //拼接sign的各个参数
		}

		// 2、sign进行MD5加密
		try {
			signBytes = encryptMD5(originalSign.toString());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("md5 "); 
			//md5编码异常就抛一个运行时异常
		}

		return byte2hex(signBytes);
	}
}
