package com.leqee.wms.api.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	
	public static String getDateString(int days,String formate,String time)
	{
		java.util.Date now = new java.util.Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		c.set(Calendar.DATE, c.get(Calendar.DATE) - days); //修改天数  0表示今天  1表示昨天  -1表示明天
		SimpleDateFormat dateFormat = new SimpleDateFormat(formate);// 可以方便地修改日期 :  "yyyy-MM-dd"

		return dateFormat.format(c.getTime()) + time;//" 00:00:00"
	}
	
	public static String getMonthString(int months,String formate,String time)
	{
		java.util.Date now = new java.util.Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) - months); //修改天数  0表示当月  1表示上个月  -1表示下个月
		SimpleDateFormat dateFormat = new SimpleDateFormat(formate);// 可以方便地修改日期 :  "yyyy-MM-dd"

		return dateFormat.format(c.getTime()) + time;//" 00:00:00"
	}
	
	public static String getDateString(int days,String formate)
	{
		java.util.Date now = new java.util.Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		c.set(Calendar.DATE, c.get(Calendar.DATE) - days); //修改天数  0表示今天  1表示昨天  -1表示明天
		SimpleDateFormat dateFormat = new SimpleDateFormat(formate);// 可以方便地修改日期 :  "yyyy-MM-dd HH:mm:ss"

		return dateFormat.format(c.getTime());//" 00:00:00"
	}
	
	/**
	 * 
	 * @param years  当前时间向前的年数   去年为1  明年为-1
	 * @param months  当前时间向前的月数
	 * @param days
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @return
	 */
	public static String getDateString(int years,int months,int days,int hours,int minutes,int seconds)
	{
		java.util.Date now = new java.util.Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 可以方便地修改日期 
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		//修改天数  0表示今天  1表示昨天  -1表示明天
		c.set(Calendar.YEAR, c.get(Calendar.YEAR) - years);
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) - months);
		c.set(Calendar.DATE, c.get(Calendar.DATE) - days); 
		c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) - hours);
		c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) - minutes);
		c.set(Calendar.SECOND, c.get(Calendar.SECOND) - seconds);
		return dateFormat.format(c.getTime());
	}
	
	/**
	 * 返回字符串類型的日期
	 * @param time 時間戳
	 * @return
	 */
	public static String getStringTime(long time){
		java.util.Date now = new java.util.Date(time);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 可以方便地修改日期 
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		//修改天数
		c.set(Calendar.YEAR, c.get(Calendar.YEAR));
		c.set(Calendar.MONTH, c.get(Calendar.MONTH));
		c.set(Calendar.DATE, c.get(Calendar.DATE)); 
		c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.get(Calendar.SECOND));
		return dateFormat.format(c.getTime());
	}
	
	
	/**
	 * 返回字符串类型的日期
	 * @param time 时间戳
	 * @return
	 */
	public static String getStringTime(Date now){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 可以方便地修改日期 
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		//修改天数
		c.set(Calendar.YEAR, c.get(Calendar.YEAR));
		c.set(Calendar.MONTH, c.get(Calendar.MONTH));
		c.set(Calendar.DATE, c.get(Calendar.DATE)); 
		c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
		c.set(Calendar.SECOND, c.get(Calendar.SECOND));
		return dateFormat.format(c.getTime());
	}
	
	/**
	 * 返回当前的小时
	 * @param time 時間戳
	 * @return
	 */
	public static int getNowHours(){
		java.util.Date now = new java.util.Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 可以方便地修改日期 
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		return c.get(Calendar.HOUR_OF_DAY);
	}
}
