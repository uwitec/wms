package com.leqee.wms.util;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.leqee.wms.entity.KeyInfo;


/**
 * 生成Sequence主键辅助类
 * @author qyyao
 *
 */
public class SequenceUtil {
	 
    private static SequenceUtil instance = new SequenceUtil(); 
    private Map<String, KeyInfo> keyMap = new HashMap<String, KeyInfo>(20); //sequence载体容器 
    private static final int POOL_SIZE = 1000;      //sequence值缓存大小
    
	private Logger logger = Logger.getLogger(SequenceUtil.class);

	public static final String KEY_NAME_SHIPMENT = "shipment_id";
	public static final String KEY_NAME_ERPSYNC = "erpsync_unique_key";
	public static final String KEY_NAME_TAGCODE = "tag_code";
	public static final String KEY_NAME_MTCODE = "mt_code";
	public static final String KEY_NAME_BPCODE = "bp_code";
	public static final String KEY_NAME_PDCODE = "pd_code";
	public static final String KEY_NAME_BATCHCODE = "batch_code";
	public static final String KEY_NAME_BHCODE = "bh_code";
	public static final String KEY_NAME_OTHER = "other_code";
	public static final String KEY_NAME_PREPACK = "prepack_code";
    
    /** 
     * 禁止外部实例化 
     */ 
    private SequenceUtil() { 
    } 

    /** 
     * 获取SequenceUtil的单例对象 
     * @return SequenceUtil的单例对象 
     */ 
    public static SequenceUtil getInstance() { 
        return instance; 
    } 

    /** 
     * 获取下一个Sequence键值 
     * @param keyName Sequence名称 
     * @return 下一个Sequence键值 
     */ 
    public synchronized Long getNextKeyValue(String keyName,Boolean flag) { 
        KeyInfo keyInfo = null; 
        Long keyObject = null; 
        try { 
            if (keyMap.containsKey(keyName)) { 
                keyInfo = keyMap.get(keyName); 
            } else { 
                keyInfo = new KeyInfo(keyName, POOL_SIZE); 
                keyMap.put(keyName, keyInfo); 
            } 
            keyObject = keyInfo.getNextKey(flag); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
            logger.error("getNextKeyValue SQLException keyName"+ keyName +": " + e.getMessage());
        } 
        return keyObject; 
    }
    
    
}
