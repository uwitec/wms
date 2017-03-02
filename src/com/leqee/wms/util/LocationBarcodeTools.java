package com.leqee.wms.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocationBarcodeTools {

	public static boolean checkLoactionBarcode(String location_barcode){
		Pattern p = null;  
        Matcher m = null;  
        boolean b = false;   
        p = Pattern.compile("^[A-Z][0-9]{6}$"); // 验证手机号  
        m = p.matcher(location_barcode);  
        b = m.matches();   
        return b;  
	}
	
	public static boolean checkString(String str,Pattern p){
		//Pattern p = null;  
        Matcher m = null;  
        boolean b = false;   
       // p = Pattern.compile("^[A-Z][0-9]{6}$"); // 验证手机号  
        m = p.matcher(str);  
        b = m.matches();   
        return b;  
	}
	
	
}
