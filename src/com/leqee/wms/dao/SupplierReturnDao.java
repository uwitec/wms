package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface SupplierReturnDao {

	List<Map<String, Object>> selectSupplierReturnByPage(Map<String, Object> searchMap);

	List<Map<String, Object>> selectTaskOrderGoodsList(@Param("taskIdList")List<Integer> taskIdList);

	List<Map<String, Object>> selectInventoryItemByOrderId(String orderId);

	Integer selectOutDoneNum(String orderId);

	List<Map> checkGoodsInventoryNum(String orderId);

	List<Map> checkGoodsProductLocationNum(String orderId);
	
}
