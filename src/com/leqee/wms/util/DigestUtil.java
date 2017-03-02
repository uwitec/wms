package com.leqee.wms.util;

import java.lang.reflect.Method;
import java.security.MessageDigest;

import sun.misc.BASE64Encoder;

//import sun.misc.BASE64Encoder;

/**
 * 生成摘要工具类
 */
public class DigestUtil {
    public static final String UTF8="UTF-8";
    
    public final static char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'}; 

    /**
     * base64
     * @param md5
     * @return
     * @throws Exception
     */
    public static String encryptBASE64(String data) throws Exception {
    	String base64 = (new BASE64Encoder()).encodeBuffer(data.getBytes(UTF8)).trim();
    	base64 = base64.replaceAll("\r\n", "");
        return base64;
    }

    /**
     * MD5
     * @param data
     * @return
     * @throws Exception
     */
    public static String encryptMD5(String data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data.getBytes(UTF8));
        byte[] b = md5.digest();
        // 把密文转换成十六进制的字符串形式
        int j = b.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = b[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }
    
    /**
	 * 32 字符十六进制数 --> 16 字符二进制格式
	 * 雷同于php md5("") --> md5("",true), 亦同pack()
	 */
	public static byte[] toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return baKeyword;
	}

	/***
	 * encode by Base64
	 */
	public static String encodeBase64(byte[] input) throws Exception {
		Class clazz = Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
		Method mainMethod = clazz.getMethod("encode", byte[].class);
		mainMethod.setAccessible(true);
		Object retObj = mainMethod.invoke(null, new Object[] { input });
		return (String) retObj;
	}
    
    /**
     * 摘要生成
     * @param data 请求数据
     * @param sign 签名秘钥(key或者parternID)
     * @param charset 编码格式
     * @return 摘要
     * @throws Exception
     */
    public static String digest(String partner,String dataTime,String data,String pass) throws Exception {
        return encryptMD5((partner+dataTime+data+pass));
    }
    
    
    /**
     * 摘要生成
     * @param data 请求数据
     * @param sign 签名秘钥(key或者parternID)
     * @param charset 编码格式
     * @return 摘要
     * @throws Exception
     */
    public static String byte2StringAscii(byte[] array){
        
    	StringBuffer sb =new StringBuffer("");
    	if(array==null||array.length==0)
    	{
    		return "";
    	}
    	for(int i=0;i<array.length;i++)
    	{
    		char c=(char) array[i];
    		sb.append(c);
    	}
    	
    	return sb.toString();
    }
    
    
    public static  void main(String[] args)
    {
    	String s="<Response><logisticProviderID>YTO</logisticProviderID><success>true</success><orderMessage><clientID>K21000119</clientID><customerId>K21000119</customerId><txLogisticID>K21000119185</txLogisticID><tradeNo>1604154647723</tradeNo><mailNo>800542423533</mailNo><bigPen>闽清 582-065</bigPen><totalServiceFee>0</totalServiceFee><codSplitFee>0</codSplitFee><orderType>1</orderType><serviceType>0</serviceType><flag>1</flag><sender><name>雀巢官方旗舰店</name><postCode>0</postCode><phone>13162753921</phone><mobile>0</mobile><prov>浙江省</prov><city>嘉兴市</city><address>惠民街道松海路88号晋亿物流集团2号仓库</address></sender><receiver><name>周米娜</name><postCode>0</postCode><phone>13867363332</phone><prov>福建</prov><city>福州,闽清县</city><address>白中镇宇龙陶瓷有限公司</address></receiver><items><item><itemName>苹果米粉225g</itemName><number>1</number><itemValue>0</itemValue></item></items><insuranceValue>0</insuranceValue><special>0</special><remark>0</remark></orderMessage></Response>";
    	
    	//System.out.println(s.subSequence(s.indexOf("<success>")+9, s.indexOf("</success>")));
    }
}