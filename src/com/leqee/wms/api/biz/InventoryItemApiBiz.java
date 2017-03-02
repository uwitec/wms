package com.leqee.wms.api.biz;

import java.util.List;
import java.util.Map;

import com.leqee.wms.api.request.GetFrozenRequest;
import com.leqee.wms.api.request.GetInventoryRequest;
import com.leqee.wms.api.request.GetVarianceImproveTaskListRequest;
import com.leqee.wms.entity.ProductLocation;


/**
 * 库存子项API业务方法
 * @author qyyao
 * @date 2016-3-1
 * @version 1.0
 */
public interface InventoryItemApiBiz {

	/**
	 * API 查询库存方法
	 * @param getInventoryRequest
	 * @param customerId
	 * @return
	 */
	Map<String, Object> getInventory(
			GetInventoryRequest getInventoryRequest, Integer customerId);
	
	List<ProductLocation> getNeedFreezeProductLocation(int physical_warehouse_id,int customer_id);
	List<ProductLocation> getNeedFreezeProductLocation2(int physical_warehouse_id, int customer_id);
	/**
	 * API 查询冻结方法
	 * @param getFrozen
	 * @param customerId
	 * @return
	 */
	Map<String, Object> getFrozen(GetFrozenRequest getFrozenRequest,
			Integer customerId);

	/**
	 * 获取盘点任务单列表
	 * @param getVarianceImproveTaskListRequest
	 * @param customerId
	 * @return
	 */
	Map<String, Object> getVarianceImproveTaskList(
			GetVarianceImproveTaskListRequest getVarianceImproveTaskListRequest,
			Integer customerId);


	
	
}
