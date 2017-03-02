package com.leqee.wms.biz;

/**
 * 
 *                          
 */
public interface ModifyInventorySummaryBiz {
    
	
	/**
	 * 修复inventory_summary中实际库存数
	 */
	public void modifyStockQuantiry();
	
	/**
	 * 修复inventory_summary中可预订库存数
	 */
	public void modifyReserveQuantiry();
	
	
}
