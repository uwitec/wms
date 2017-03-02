package com.leqee.wms.api.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tools {

	public static String list2String(List<Integer> list0, String separator) {
		StringBuffer sb=new StringBuffer("");
		for(Integer order_id:list0)
		{
			sb.append(order_id).append(separator);
		}
		
		return (sb.toString().substring(0, sb.toString().length()-1));
	}
	
	
	/**
	 * 
	 * @param str  String
	 * @param last 从后面忽略的字符数
	 * @return
	 */
	public static List<String> changeStringToList(String str, int last) {
		String[] s = str.substring(0, str.length() - last).split(",");

		return Arrays.asList(s);
	}

	
	
	/**
	 * 将List<String>  --> to   List<Integer>
	 * @param strList  List<String>
	 * @return
	 */
	public static List<Integer> changeStringListToIntegerList(List<String> strList) {
		List<Integer> integerList=new ArrayList<Integer>();
		for(int i=0;i<strList.size();i++){
			integerList.add(Integer.parseInt(strList.get(i)));
		}
		return integerList;
	}
	
	
	/**
	 * 将List<Integer>  --> to   List<String>
	 * @param strList  List<String>
	 * @return
	 */
	public static List<String> changeIntegerListToStringList(List<Integer> list) {
		List<String> strList=new ArrayList<String>();
		for(int i=0;i<list.size();i++){
			strList.add(list.get(i).intValue()+"");
		}
		return strList;
	}
}
