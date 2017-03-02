package com.leqee.wms.api.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public abstract class StringUtils
{
  public static boolean isEmpty(String value)
  {
    int strLen;
    if ((value == null) || ((strLen = value.length()) == 0)) {
      return true;
    }
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(value.charAt(i))) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isNumeric(Object obj)
  {
    if (obj == null) {
      return false;
    }
    char[] chars = obj.toString().toCharArray();
    int length = chars.length;
    if (length < 1) {
      return false;
    }
    int i = 0;
    if ((length > 1) && (chars[0] == '-')) {}
    for (i = 1; i < length; i++) {
      if (!Character.isDigit(chars[i])) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean areNotEmpty(String... values)
  {
    boolean result = true;
    if ((values == null) || (values.length == 0)) {
      result = false;
    } else {
      for (String value : values) {
        result &= !isEmpty(value);
      }
    }
    return result;
  }
  
  public static String unicodeToChinese(String unicode)
  {
    StringBuilder out = new StringBuilder();
    if (!isEmpty(unicode)) {
      for (int i = 0; i < unicode.length(); i++) {
        out.append(unicode.charAt(i));
      }
    }
    return out.toString();
  }
  
  public static String toUnderlineStyle(String name)
  {
    StringBuilder newName = new StringBuilder();
    for (int i = 0; i < name.length(); i++)
    {
      char c = name.charAt(i);
      if (Character.isUpperCase(c))
      {
        if (i > 0) {
          newName.append("_");
        }
        newName.append(Character.toLowerCase(c));
      }
      else
      {
        newName.append(c);
      }
    }
    return newName.toString();
  }
  
  public static String convertString(byte[] data, int offset, int length)
  {
    if (data == null) {
      return null;
    }
    try
    {
      return new String(data, offset, length, "UTF-8");
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }
  
  public static byte[] convertBytes(String data)
  {
    if (data == null) {
      return null;
    }
    try
    {
      return data.getBytes("UTF-8");
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }
  
  public static Date parseDateTime(String str)
  {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    try
    {
      return format.parse(str);
    }
    catch (ParseException e)
    {
      throw new RuntimeException(e);
    }
  }
}

