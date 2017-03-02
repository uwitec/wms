package com.leqee.wms.api.util;

public class DataUtil {
	public static String objToStr(Object emailUserName)
	{
		if(null==emailUserName||"".equals(emailUserName))
		{
			return "";
		}
		else
		{
			return emailUserName.toString();
		}
	}
}
