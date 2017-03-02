package com.leqee.wms.biz;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.leqee.wms.entity.ConfigReplenishment;
import com.leqee.wms.entity.ConfigReplenishmentUrgent;

/**
 * 补货相关的业务层方法
 * @author hzhang1
 * @date 2016-6-22
 * @version 1.0.0
 */
public interface ReplenishmentUrgentBiz {
	List<Map<String, Object>> selectByMap(Map<String, Object>map);
	 
	 void deleteReplenishmentUrgent(Integer customer_id,Integer physical_warehouse_id);
	 
	List<ConfigReplenishmentUrgent> selectConfigReplenishmentUrgentByCustomerId(Map<String, Object>map);
	
	Map<String,Object> updateReplenishmentUrgentByUpdateReplenishmentMap(Map<String, Object> updateReplenishmentMap,Map<String, Object> searchMap);
	
	Map<String,Object> insertPartConfigReplenishmentUrgentByMap(Map<String, Object> updateReplenishmentMap,Map<String, Object> searchMap);
	
	Map<String,Object> insertConfigReplenishmentUrgentByMap(Map<String, Object> updateReplenishmentMap,Map<String, Object> searchMap);
	
	ConfigReplenishmentUrgent selectReplenishmentUrgentIsExist(Integer customerId,Integer physical_warehouse_id);
}
