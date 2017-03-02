package com.leqee.wms.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

	/**
	 * 发送POST 请求
	 * 
	 * @param url
	 *            请求地址
	 * @param charset
	 *            编码格式
	 * @param params
	 *            请求参数
	 * @return 响应
	 * @throws IOException
	 */
	public static String post(String url, String charset,
			Map<String, Object> params) throws IOException {
		HttpURLConnection conn = null;
		OutputStreamWriter out = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		StringBuffer result = new StringBuffer();
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Accept-Charset", charset);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			out = new OutputStreamWriter(conn.getOutputStream(), charset);
			out.write(buildQuery(params, charset));
			out.flush();
			inputStream = conn.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream,"utf-8");
			reader = new BufferedReader(inputStreamReader);
			String tempLine = null;
			while ((tempLine = reader.readLine()) != null) {
				result.append(tempLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
			if (reader != null) {
				reader.close();
			}
			if (inputStreamReader != null) {
				inputStreamReader.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return result.toString();
	}

	/**
	 * 将map转换为请求字符串
	 * <p>
	 * data=xxx&msg_type=xxx
	 * </p>
	 * 
	 * @param params
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String buildQuery(Map<String, Object> params, String charset)
			throws IOException {
		if (params == null || params.isEmpty()) {
			return null;
		}

		StringBuffer data = new StringBuffer();
		boolean flag = false;

		for (Entry<String, Object> entry : params.entrySet()) {
			if (flag) {
				data.append("&");
			} else {
				flag = true;
			}
			data.append(entry.getKey())
					.append("=")
					.append(URLEncoder.encode(entry.getValue().toString(),
							charset));
		}

		return data.toString();

	}
	
	
	
	/**
	 * 申通发送POST 请求
	 * 
	 * @param url
	 *            请求地址
	 * @param charset
	 *            编码格式
	 * @param params
	 *            请求参数
	 * @return 响应
	 * @throws IOException
	 */
	public static String StoPost(String url,List<NameValuePair> list) throws IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		String str = "";
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(list,"utf-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		try {
			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if(entity!=null) {
			str = new String(EntityUtils.toString(entity));
			//System.out.println(str);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
		
	}

	
	public static String get(String url) throws IOException {
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
        HttpResponse response = httpclient.execute(request);
        
        
        BufferedReader bufferedReader=null; 
		bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			result.append(line);
		}
		
		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();    
		byte[] b = decoder.decodeBuffer(result.toString()); 
		String resStr = new String(b);
		
		return resStr;
		
	}


}