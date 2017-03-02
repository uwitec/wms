package com.leqee.wms.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import com.leqee.wms.api.util.JacksonJsonUtil;
import com.leqee.wms.api.util.LeqeeUtils;
import com.leqee.wms.api.util.StringUtils;
import com.leqee.wms.api.util.WorkerUtil;
import com.leqee.wms.api.util.WebUtils.TrustAllTrustManager;

/**
 * 乐其默认实现的Client，用于调用wms接口用
 * @author qyyao
 * @date 2016-3-4
 * @version 1.0
 */
public class LeqeeDefaultClient implements LeqeeClient {

	private String serverUrl;  // 要调用的服务的url（serverUrl后面要加上时间戳参数，避免请求缓存的影响）
	private String appKey;   //appkey 应用的唯一标识，对应一个业务组
	private String appSecret;  //appSecret 该业务组的授权密钥
	
	// Request连接时间设置
	private int connectTimeout = 3000;
	private int readTimeout = 15000;
	

	public LeqeeDefaultClient(String serverUrl, String appKey, String appSecret) {
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.serverUrl = serverUrl;
	}





	@SuppressWarnings("unchecked")
	@Override
	public <T extends LeqeeResponse> T execute(LeqeeRequest<T> request)
			throws ApiException {
		
		// 1、检查serverUrl、appkey、appsert参数是否有空
		if( !WorkerUtil.hasNoEmpty(this.serverUrl, this.appKey, this.appSecret) ){
			throw new ApiException(ApiException.ERR_CODE_EMPTY_APP_PARAMS, ApiException.ERR_MSG_EMPTY_APP_PARAMS);
		}
		
		
		// 2、Request及自身参数检查
		if(WorkerUtil.isNullOrEmpty(request)){
			throw new ApiException(ApiException.ERR_CODE_EMPTY_REQUEST, ApiException.ERR_MSG_EMPTY_REQUEST);
		}
		request.check();  //检查出错时，会抛出异常
		
		
		// 3、初始化data和msgId
		String data = ""; 
		try {
			data = JacksonJsonUtil.beanToJson(request);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApiException( ApiException.ERR_CODE_ILLEGAL_REQUEST_PARAMS,ApiException.ERR_MSG_ILLEGAL_REQUEST_PARAMS + " " + e.getMessage() );
		}
		String msgId = System.currentTimeMillis()+ "" ;
		
		
		// 4、生成sign
		TreeMap<String, String> requestParamsMap = new TreeMap<String, String>();  
		
		requestParamsMap.put("app_key", appKey);
		requestParamsMap.put("app_secret", appSecret);
		requestParamsMap.put("msg_id", msgId);
		requestParamsMap.put("api_method_name", request.getApiMethodName());
		requestParamsMap.put("data", data);
		
		requestParamsMap.put("sign", LeqeeUtils.createSign(requestParamsMap) );
		
		// 5、组装Request请求报文和url(url添加时间戳参数)
		String requestMessage = "";
		String url = serverUrl +  "?timestamp=" + msgId ;
		try {
			requestMessage =  JacksonJsonUtil.beanToJson(requestParamsMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApiException( ApiException.ERR_CODE_ILLEGAL_REQUEST_PARAMS,ApiException.ERR_MSG_ILLEGAL_REQUEST_PARAMS + " " + e.getMessage() );
		}
		//System.out.println("requestMessage: " + requestMessage);
		
		
		
		// 6、发送报文并获取响应的报文串
		
		String rspStr = null;
		try {
			rspStr = doPost( url, requestMessage);
		} catch (UnsupportedEncodingException e) {
			throw new ApiException("doPost has a unsupport encoding in response", e);
		} catch (IOException e) {
			throw new ApiException("doPost has a ioexception in response", e);
		}
	    
		
		
		// 7、将返回封装成对应的Response
		if(WorkerUtil.isNullOrEmpty(rspStr)){
			throw new ApiException(ApiException.ERR_CODE_EMPTY_RESPONSE, ApiException.ERR_MSG_EMPTY_RESPONSE );
		}
		
		T leqeeResponse = null;
		try {
			leqeeResponse =  (T) JacksonJsonUtil.jsonToBean( rspStr , request.getResponseClass());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApiException( ApiException.ERR_CODE_ILLEGAL_RESPONSE_PARAMS,ApiException.ERR_MSG_ILLEGAL_RESPONSE_PARAMS + " " + e.getMessage() );
		}
		
		return leqeeResponse;
	}





	/**
	 * 发送POST请求，并返回Response的字符串
	 * @param url
	 * @param requestMessage 
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private String doPost(String url, String requestMessage)
			throws UnsupportedEncodingException, IOException {
		// >> a 初始化HttpURLConnection报文参数
		String rspStr = null;
		String charset = "UTF-8";      //指定发送数组类型
		String ctype = "application/json;charset=" + charset;    //发送数据类型，需要指定为json类型
		String method = "POST";     //指定发送的方法
		Map<String, String> headerMap = new HashMap<String, String>();
		byte[] messageContent = new byte[0];
		if (requestMessage != null) {
			messageContent = requestMessage.getBytes(charset);
		}
		HttpURLConnection conn = null;
		OutputStream out = null;

		// >> b 获取连接&发送报文&获取响应报文
		try {
			conn = getConnection(new URL(url), method, ctype,
					headerMap);
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
			out = conn.getOutputStream();
			out.write(messageContent);
			rspStr = getResponseAsString(conn);
		} catch (IOException e) {
			throw e;
		} finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return rspStr;
	}


	/**
	 * 获取一个HttpURLconnection
	 * @param url
	 * @param method
	 * @param ctype
	 * @param headerMap
	 * @return
	 * @throws IOException
	 */
	private static HttpURLConnection getConnection(URL url, String method,
			String ctype, Map<String, String> headerMap) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		if ((conn instanceof HttpsURLConnection)) {
			HttpsURLConnection connHttps = (HttpsURLConnection) conn;
			try {
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(null,
						new TrustManager[] { new TrustAllTrustManager() },
						new SecureRandom());
				connHttps.setSSLSocketFactory(ctx.getSocketFactory());
				connHttps.setHostnameVerifier(new HostnameVerifier() {
					public boolean verify(String hostname,
							SSLSession session) {
						return true;
					}
				});
			} catch (Exception e) {
				throw new IOException(e);
			}
			
			conn = connHttps;
		}
		conn.setRequestMethod(method);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Host", url.getHost());
		conn.setRequestProperty("Accept", "application/json,text/javascript");
		conn.setRequestProperty("User-Agent", "top-sdk-java");
		conn.setRequestProperty("Content-Type", ctype);
		if (headerMap != null) {
			for (Map.Entry<String, String> entry : headerMap.entrySet()) {
				conn.setRequestProperty((String) entry.getKey(),
						(String) entry.getValue());
			}
		}
		return conn;
	}
	
	/**
	 * 获取连接的响应Response报文并作为String返回
	 * @param conn
	 * @return
	 * @throws IOException
	 */
	protected static String getResponseAsString(HttpURLConnection conn)
			throws IOException {
		String charset = getResponseCharset(conn.getContentType());
		InputStream es = conn.getErrorStream();
		if (es == null) {
			return getStreamAsString(conn.getInputStream(), charset);
		}
		String msg = getStreamAsString(es, charset);
		if (StringUtils.isEmpty(msg)) {
			throw new IOException(conn.getResponseCode() + ":"
					+ conn.getResponseMessage());
		}
		throw new IOException(msg);
	}

	/**
	 * 获取流作为String返回
	 * @param stream
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String getStreamAsString(InputStream stream, String charset)
			throws IOException {
		try {
			Reader reader = new InputStreamReader(stream, charset);
			StringBuilder response = new StringBuilder();

			char[] buff = new char[1024];
			int read = 0;
			while ((read = reader.read(buff)) > 0) {
				response.append(buff, 0, read);
			}
			return response.toString();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	/**
	 * 得到响应报文的编码类型
	 * @param ctype
	 * @return
	 */
	public static String getResponseCharset(String ctype) {
		String charset = "UTF-8";
		if (!StringUtils.isEmpty(ctype)) {
			String[] params = ctype.split(";");
			for (String param : params) {
				param = param.trim();
				if (param.startsWith("charset")) {
					String[] pair = param.split("=", 2);
					if ((pair.length != 2) || (StringUtils.isEmpty(pair[1]))) {
						break;
					}
					charset = pair[1].trim();
					break;
				}
			}
		}
		return charset;
	}
	
	
	
	

}
