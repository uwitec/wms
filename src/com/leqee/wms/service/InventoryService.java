package com.leqee.wms.service;

import com.leqee.wms.response.Response;

public interface InventoryService {

	/**
	 * 销售订单调度出库
	 * @param groupId
	 * @param customerId
	 * @param hours
	 * @param endDate
	 * 
	 * @return response
	 */
	Response saleInventoryOut(Integer groupId, Integer customerId,
			Double hours, String endDate);
	
	Response saleInventoryOutFor1111(Integer groupId, Integer customerId,
			Double hours, String endDate);
	
	/**
	 * 耗材出库
	 * @param groupId
	 * @param customerId
	 * @param hours
	 * @param endDate
	 * @return
	 */
	Response packBoxInventoryOut(Integer groupId, Integer customerId,
			Double hours, String endDate);
	
	Response packBoxInventoryOutV2(Integer groupId, Integer customerId,
			Double hours, String endDate);

	/**
	 * 仓库内导入库位库存
	 * @param customerId
	 * @param physicalWarehouId
	 * @return
	 */
	Response importProductLocation(Integer customerId , Integer physicalWarehouseId);
	
	
	/**
	 * 调拨采购单自动入库
	 * @param customerId
	 * @param physicalWarehouseId
	 * @return
	 */
	Response autoPurchaseAccept(Integer customerId , Integer physicalWarehouseId,Double hours, String endDate);
	
	/**
	 * 测试采购单自动收货上架
	 * @param customerId
	 * @param physicalWarehouseId
	 * @param hours
	 * @param endDate
	 * @return
	 */
	Response testAutoPurchaseAcceptAndGrouding(Integer groupId,Integer customerId , Integer physicalWarehouseId,Double hours, String endDate);

	Response packBoxInventoryOutForPrePack(Integer physical_warehouse_id, Integer customer_id,
			String endDate);
	
	
}
