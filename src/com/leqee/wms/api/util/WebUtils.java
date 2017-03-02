package com.leqee.wms.api.util;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

public abstract class WebUtils
{
  private static final String DEFAULT_CHARSET = "UTF-8";
  private static final Set<String> aliDomains = new HashSet();
  private static final String METHOD_POST = "POST";
  private static final String METHOD_GET = "GET";
  private static final Certificate verisign = null;
  private static boolean ignoreSSLCheck;
  private static boolean ignoreHostCheck;
  
  static
  {
//    aliDomains.add("*.taobao.com");
//    aliDomains.add("*.alipay.com");
//    aliDomains.add("*.aliyuncs.com");
//    aliDomains.add("*.alibaba.com");
//    aliDomains.add("*.tmall.com");
//    
//    InputStream input = null;
//    try
//    {
//      CertificateFactory cf = CertificateFactory.getInstance("X.509");
//      input = WebUtils.class.getResourceAsStream("/verisign.crt");
//      verisign = cf.generateCertificate(input); return;
//    }
//    catch (Exception e)
//    {
//      throw new RuntimeException(e);
//    }
//    finally
//    {
//      if (input != null) {
//        try
//        {
//          input.close();
//        }
//        catch (IOException e) {}
//      }
//    }
  }
  
  public static class VerisignTrustManager
    implements X509TrustManager
  {
    public X509Certificate[] getAcceptedIssuers()
    {
      return null;
    }
    
    public void checkClientTrusted(X509Certificate[] chain, String authType)
      throws CertificateException
    {}
    
    public void checkServerTrusted(X509Certificate[] chain, String authType)
      throws CertificateException
    {
      X509Certificate aliCert = null;
      for (X509Certificate cert : chain)
      {
        cert.checkValidity();
        try
        {
          String dn = cert.getSubjectX500Principal().getName();
          LdapName ldapDN = new LdapName(dn);
          for (Rdn rdn : ldapDN.getRdns()) {
            if (("CN".equals(rdn.getType())) && (WebUtils.aliDomains.contains(rdn.getValue())))
            {
              aliCert = cert;
              break;
            }
          }
        }
        catch (Exception e)
        {
          throw new CertificateException(e);
        }
      }
      if (aliCert != null) {
        try
        {
          aliCert.verify(WebUtils.verisign.getPublicKey());
        }
        catch (Exception e)
        {
          throw new CertificateException(e);
        }
      } else {
        throw new CertificateException("Access to the non Alibaba Group's HTTPS services are not allowed!");
      }
    }
  }
  
  public static class TrustAllTrustManager
    implements X509TrustManager
  {
    public X509Certificate[] getAcceptedIssuers()
    {
      return null;
    }
    
    public void checkClientTrusted(X509Certificate[] chain, String authType)
      throws CertificateException
    {}
    
    public void checkServerTrusted(X509Certificate[] chain, String authType)
      throws CertificateException
    {}
  }
  
  public static void setIgnoreSSLCheck(boolean ignoreSSLCheck)
  {
    ignoreSSLCheck = ignoreSSLCheck;
  }
  
  public static void setIgnoreHostCheck(boolean ignoreHostCheck)
  {
    ignoreHostCheck = ignoreHostCheck;
  }
  
  public static String doPost(String url, Map<String, String> params, int connectTimeout, int readTimeout)
    throws IOException
  {
    return doPost(url, params, "UTF-8", connectTimeout, readTimeout);
  }
  
  public static String doPost(String url, Map<String, String> params, String charset, int connectTimeout, int readTimeout)
    throws IOException
  {
    return doPost(url, params, charset, connectTimeout, readTimeout, null);
  }
  
  public static String doPost(String url, Map<String, String> params, String charset, int connectTimeout, int readTimeout, Map<String, String> headerMap)
    throws IOException
  {
    String ctype = "application/x-www-form-urlencoded;charset=" + charset;
    String query = buildQuery(params, charset);
    byte[] content = new byte[0];
    if (query != null) {
      content = query.getBytes(charset);
    }
    return _doPost(url, ctype, content, connectTimeout, readTimeout, headerMap);
  }
  
  public static String doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout)
    throws IOException
  {
    return _doPost(url, ctype, content, connectTimeout, readTimeout, null);
  }
  
  private static String _doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout, Map<String, String> headerMap)
    throws IOException
  {
    HttpURLConnection conn = null;
    OutputStream out = null;
    String rsp = null;
    try
    {
      try
      {
        conn = getConnection(new URL(url), "POST", ctype, headerMap);
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
      }
      catch (IOException e)
      {
        Map<String, String> map = getParamsFromUrl(url);
//        TaobaoLogger.logCommError(e, url, (String)map.get("app_key"), (String)map.get("method"), content);
        throw e;
      }
      try
      {
        out = conn.getOutputStream();
        out.write(content);
        rsp = getResponseAsString(conn);
      }
      catch (IOException e)
      {
        Map<String, String> map = getParamsFromUrl(url);
//        TaobaoLogger.logCommError(e, conn, (String)map.get("app_key"), (String)map.get("method"), content);
        throw e;
      }
    }
    finally
    {
      if (out != null) {
        out.close();
      }
      if (conn != null) {
        conn.disconnect();
      }
    }
    return rsp;
  }
  
//  public static String doPost(String url, Map<String, String> params, Map<String, FileItem> fileParams, int connectTimeout, int readTimeout)
//    throws IOException
//  {
//    if ((fileParams == null) || (fileParams.isEmpty())) {
//      return doPost(url, params, "UTF-8", connectTimeout, readTimeout);
//    }
//    return doPost(url, params, fileParams, "UTF-8", connectTimeout, readTimeout);
//  }
  
//  public static String doPost(String url, Map<String, String> params, Map<String, FileItem> fileParams, String charset, int connectTimeout, int readTimeout)
//    throws IOException
//  {
//    return doPost(url, params, fileParams, charset, connectTimeout, readTimeout, null);
//  }
//  
//  public static String doPost(String url, Map<String, String> params, Map<String, FileItem> fileParams, String charset, int connectTimeout, int readTimeout, Map<String, String> headerMap)
//    throws IOException
//  {
//    if ((fileParams == null) || (fileParams.isEmpty())) {
//      return doPost(url, params, charset, connectTimeout, readTimeout, headerMap);
//    }
//    return _doPostWithFile(url, params, fileParams, charset, connectTimeout, readTimeout, headerMap);
//  }
//  
//  private static String _doPostWithFile(String url, Map<String, String> params, Map<String, FileItem> fileParams, String charset, int connectTimeout, int readTimeout, Map<String, String> headerMap)
//    throws IOException
//  {
//    String boundary = String.valueOf(System.nanoTime());
//    HttpURLConnection conn = null;
//    OutputStream out = null;
//    String rsp = null;
//    try
//    {
//      try
//      {
//        String ctype = "multipart/form-data;charset=" + charset + ";boundary=" + boundary;
//        conn = getConnection(new URL(url), "POST", ctype, headerMap);
//        conn.setConnectTimeout(connectTimeout);
//        conn.setReadTimeout(readTimeout);
//      }
//      catch (IOException e)
//      {
//        Map<String, String> map = getParamsFromUrl(url);
//        TaobaoLogger.logCommError(e, url, (String)map.get("app_key"), (String)map.get("method"), params);
//        throw e;
//      }
//      try
//      {
//        out = conn.getOutputStream();
//        byte[] entryBoundaryBytes = ("\r\n--" + boundary + "\r\n").getBytes(charset);
//        
//
//        Set<Map.Entry<String, String>> textEntrySet = params.entrySet();
//        for (Map.Entry<String, String> textEntry : textEntrySet)
//        {
//          byte[] textBytes = getTextEntry((String)textEntry.getKey(), (String)textEntry.getValue(), charset);
//          out.write(entryBoundaryBytes);
//          out.write(textBytes);
//        }
//        Set<Map.Entry<String, FileItem>> fileEntrySet = fileParams.entrySet();
//        for (Map.Entry<String, FileItem> fileEntry : fileEntrySet)
//        {
//          FileItem fileItem = (FileItem)fileEntry.getValue();
//          if (fileItem.getContent() != null)
//          {
//            byte[] fileBytes = getFileEntry((String)fileEntry.getKey(), fileItem.getFileName(), fileItem.getMimeType(), charset);
//            out.write(entryBoundaryBytes);
//            out.write(fileBytes);
//            out.write(fileItem.getContent());
//          }
//        }
//        byte[] endBoundaryBytes = ("\r\n--" + boundary + "--\r\n").getBytes(charset);
//        out.write(endBoundaryBytes);
//        rsp = getResponseAsString(conn);
//      }
//      catch (IOException e)
//      {
//        Map<String, String> map = getParamsFromUrl(url);
//        TaobaoLogger.logCommError(e, conn, (String)map.get("app_key"), (String)map.get("method"), params);
//        throw e;
//      }
//    }
//    finally
//    {
//      if (out != null) {
//        out.close();
//      }
//      if (conn != null) {
//        conn.disconnect();
//      }
//    }
//    return rsp;
//  }
  
  private static byte[] getTextEntry(String fieldName, String fieldValue, String charset)
    throws IOException
  {
    StringBuilder entry = new StringBuilder();
    entry.append("Content-Disposition:form-data;name=\"");
    entry.append(fieldName);
    entry.append("\"\r\nContent-Type:text/plain\r\n\r\n");
    entry.append(fieldValue);
    return entry.toString().getBytes(charset);
  }
  
  private static byte[] getFileEntry(String fieldName, String fileName, String mimeType, String charset)
    throws IOException
  {
    StringBuilder entry = new StringBuilder();
    entry.append("Content-Disposition:form-data;name=\"");
    entry.append(fieldName);
    entry.append("\";filename=\"");
    entry.append(fileName);
    entry.append("\"\r\nContent-Type:");
    entry.append(mimeType);
    entry.append("\r\n\r\n");
    return entry.toString().getBytes(charset);
  }
  
  public static String doGet(String url, Map<String, String> params)
    throws IOException
  {
    return doGet(url, params, "UTF-8");
  }
  
  public static String doGet(String url, Map<String, String> params, String charset)
    throws IOException
  {
    HttpURLConnection conn = null;
    String rsp = null;
    try
    {
      String ctype = "application/x-www-form-urlencoded;charset=" + charset;
      String query = buildQuery(params, charset);
      try
      {
        conn = getConnection(buildGetUrl(url, query), "GET", ctype, null);
      }
      catch (IOException e)
      {
        Map<String, String> map = getParamsFromUrl(url);
//        TaobaoLogger.logCommError(e, url, (String)map.get("app_key"), (String)map.get("method"), params);
        throw e;
      }
      try
      {
        rsp = getResponseAsString(conn);
      }
      catch (IOException e)
      {
        Map<String, String> map = getParamsFromUrl(url);
//        TaobaoLogger.logCommError(e, conn, (String)map.get("app_key"), (String)map.get("method"), params);
        throw e;
      }
    }
    finally
    {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return rsp;
  }
  
  private static HttpURLConnection getConnection(URL url, String method, String ctype, Map<String, String> headerMap)
    throws IOException
  {
    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    if ((conn instanceof HttpsURLConnection))
    {
      HttpsURLConnection connHttps = (HttpsURLConnection)conn;
      if (ignoreSSLCheck) {
        try
        {
          SSLContext ctx = SSLContext.getInstance("TLS");
          ctx.init(null, new TrustManager[] { new TrustAllTrustManager() }, new SecureRandom());
          connHttps.setSSLSocketFactory(ctx.getSocketFactory());
          connHttps.setHostnameVerifier(new HostnameVerifier()
          {
            public boolean verify(String hostname, SSLSession session)
            {
              return true;
            }
          });
        }
        catch (Exception e)
        {
          throw new IOException(e);
        }
      } else {
        try
        {
          SSLContext ctx = SSLContext.getInstance("TLS");
          ctx.init(null, new TrustManager[] { new VerisignTrustManager() }, new SecureRandom());
          connHttps.setSSLSocketFactory(ctx.getSocketFactory());
          if (ignoreHostCheck) {
            connHttps.setHostnameVerifier(new HostnameVerifier()
            {
              public boolean verify(String hostname, SSLSession session)
              {
                return true;
              }
            });
          }
        }
        catch (Exception e)
        {
          throw new IOException(e);
        }
      }
      conn = connHttps;
    }
    conn.setRequestMethod(method);
    conn.setDoInput(true);
    conn.setDoOutput(true);
    conn.setRequestProperty("Host", url.getHost());
    conn.setRequestProperty("Accept", "text/xml,text/javascript");
    conn.setRequestProperty("User-Agent", "top-sdk-java");
    conn.setRequestProperty("Content-Type", ctype);
    if (headerMap != null) {
      for (Map.Entry<String, String> entry : headerMap.entrySet()) {
        conn.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
      }
    }
    return conn;
  }
  
  private static URL buildGetUrl(String strUrl, String query)
    throws IOException
  {
    URL url = new URL(strUrl);
    if (StringUtils.isEmpty(query)) {
      return url;
    }
    if (StringUtils.isEmpty(url.getQuery()))
    {
      if (strUrl.endsWith("?")) {
        strUrl = strUrl + query;
      } else {
        strUrl = strUrl + "?" + query;
      }
    }
    else if (strUrl.endsWith("&")) {
      strUrl = strUrl + query;
    } else {
      strUrl = strUrl + "&" + query;
    }
    return new URL(strUrl);
  }
  
  public static String buildQuery(Map<String, String> params, String charset)
    throws IOException
  {
    if ((params == null) || (params.isEmpty())) {
      return null;
    }
    StringBuilder query = new StringBuilder();
    Set<Map.Entry<String, String>> entries = params.entrySet();
    boolean hasParam = false;
    for (Map.Entry<String, String> entry : entries)
    {
      String name = (String)entry.getKey();
      String value = (String)entry.getValue();
      if (StringUtils.areNotEmpty(new String[] { name, value }))
      {
        if (hasParam) {
          query.append("&");
        } else {
          hasParam = true;
        }
        query.append(name).append("=").append(URLEncoder.encode(value, charset));
      }
    }
    return query.toString();
  }
  
  protected static String getResponseAsString(HttpURLConnection conn)
    throws IOException
  {
    String charset = getResponseCharset(conn.getContentType());
    InputStream es = conn.getErrorStream();
    if (es == null)
    {
      String contentEncoding = conn.getContentEncoding();
      if ("gzip".equalsIgnoreCase(contentEncoding)) {
        return getStreamAsString(new GZIPInputStream(conn.getInputStream()), charset);
      }
      return getStreamAsString(conn.getInputStream(), charset);
    }
    String msg = getStreamAsString(es, charset);
    if (StringUtils.isEmpty(msg)) {
      throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
    }
    throw new IOException(msg);
  }
  
  public static String getStreamAsString(InputStream stream, String charset)
    throws IOException
  {
    try
    {
      Reader reader = new InputStreamReader(stream, charset);
      StringBuilder response = new StringBuilder();
      
      char[] buff = new char[1024];
      int read = 0;
      while ((read = reader.read(buff)) > 0) {
        response.append(buff, 0, read);
      }
      return response.toString();
    }
    finally
    {
      if (stream != null) {
        stream.close();
      }
    }
  }
  
  public static String getResponseCharset(String ctype)
  {
    String charset = "UTF-8";
    if (!StringUtils.isEmpty(ctype))
    {
      String[] params = ctype.split(";");
      for (String param : params)
      {
        param = param.trim();
        if (param.startsWith("charset"))
        {
          String[] pair = param.split("=", 2);
          if ((pair.length != 2) || 
            (StringUtils.isEmpty(pair[1]))) {
            break;
          }
          charset = pair[1].trim(); break;
        }
      }
    }
    return charset;
  }
  
  public static String decode(String value)
  {
    return decode(value, "UTF-8");
  }
  
  public static String encode(String value)
  {
    return encode(value, "UTF-8");
  }
  
  public static String decode(String value, String charset)
  {
    String result = null;
    if (!StringUtils.isEmpty(value)) {
      try
      {
        result = URLDecoder.decode(value, charset);
      }
      catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    }
    return result;
  }
  
  public static String encode(String value, String charset)
  {
    String result = null;
    if (!StringUtils.isEmpty(value)) {
      try
      {
        result = URLEncoder.encode(value, charset);
      }
      catch (IOException e)
      {
        throw new RuntimeException(e);
      }
    }
    return result;
  }
  
  private static Map<String, String> getParamsFromUrl(String url)
  {
    Map<String, String> map = null;
    if ((url != null) && (url.indexOf('?') != -1)) {
      map = splitUrlQuery(url.substring(url.indexOf('?') + 1));
    }
    if (map == null) {
      map = new HashMap();
    }
    return map;
  }
  
  public static Map<String, String> splitUrlQuery(String query)
  {
    Map<String, String> result = new HashMap();
    
    String[] pairs = query.split("&");
    if ((pairs != null) && (pairs.length > 0)) {
      for (String pair : pairs)
      {
        String[] param = pair.split("=", 2);
        if ((param != null) && (param.length == 2)) {
          result.put(param[0], param[1]);
        }
      }
    }
    return result;
  }
}
