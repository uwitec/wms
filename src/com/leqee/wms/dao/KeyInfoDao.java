package com.leqee.wms.dao;

import java.util.Map;

public interface KeyInfoDao {

	/**
	 * 通过keyName查找keyValue(用于查找键名的最大值)
	 * @param keyName
	 * @return
	 */
	public Long selectKeyValueByKeyName(Map map);

	/**
	 * 更新keyName键名的最大值
	 * @param map
	 */
	public void updateKeyValueByKeyName(Map<String, Object> map);
	
	
	/**
	 * 插入记录
	 * @param map
	 */
	public void insertKeyName(String keyName);

}
