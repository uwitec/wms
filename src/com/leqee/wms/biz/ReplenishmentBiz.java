package com.leqee.wms.biz;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.leqee.wms.entity.ConfigReplenishment;
import com.leqee.wms.entity.Location;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ProductLocation;

/**
 * 补货相关的业务层方法
 * @author hzhang1
 * @date 2016-6-22
 * @version 1.0.0
 */
public interface ReplenishmentBiz {
	
	
	List<Map<String, Object>> selectByMap(Map<String, Object> map);
	
	void insertConfigReplenishment(List<ConfigReplenishment> configReplenishmentList);
	
	Map<String,Object> insertConfigReplenishmentByMap(Map<String, Object> updateReplenishmentMap,Map<String, Object> searchMap);
	
	public List<Map<String,Object>> uploadLocationV2(InputStream input,boolean isE2007,String actionUser);
	
	ConfigReplenishment selectReplenishmentByCustomerId( Integer product_id,Integer physical_warehouse_id);
	
	ConfigReplenishment selectReplenishmentIsExist(Map<String, Object>map);
	
	Map<String,Object> updateReplenishmentByUpdateReplenishmentMap(Map<String, Object> updateReplenishmentMap,Map<String, Object> searchMap);
	
    void deleteReplenishment (String customer_id, Integer product_id,Integer physical_warehouse_id );
   
    public Location checkLocationBarcode(Integer physicalWarehouseId,String locationBarcode);

	public List<Map> getReplenishmentTask(Map map); // 得到补货任务列表

	public HashMap<String, Object> getTrayBarcodeForBH(Integer number);
	
	public Map printReplenishmentTask(String temp,String actionUser); // 打印补货任务
	
	public Map updateReplenishment(String taskId,String fromLocationBarcode,String toLocationBarcode,String quantity,Integer physicalWarehouseId,String actionUser);  //更新补货任务
	
	public Map finishReplenishmentTask(String taskId,Integer physicalWarehouseId,String actionUser); // 完成补货任务
	
	public Map cancelReplenishmentTask(String taskId,String fromLocationBarcode,String cancelReason,String actionUser); // 取消补货任务
	
	
	// 以下方法为RF补货相关方法
	public Map getReplenishmentTaskFromRF(String customerId,String taskLevel,Integer physicalWarehouseId,String taskType); // 得到一条RF的补货任务
	
	public Map getReplenishmentProductFromRF(String fromLocationBarcode,Integer physicalWarehouseId,String taskType,String taskId,String actionUser); // 得到源库位上商品信息
	
	public Map offShelfReplenishmentFromRF(String taskId,Integer warehouseId,String locationBarcode,String toLocationBarcode,String productId,String status,String validity,String batchSn,String serialNumber,Integer physicalWarehouseId,Integer quantity,String actionUser); // RF补货下架
	
	public Map getReplenishmentInfoByCodeFromRF(String bhCode,Integer physicalWarehouseId); // 根据补货标签得到补货信息
	
	public Map onShelfReplenishmentFromRF(String bhCode,String barcode,Integer quantity,String toLocationBarcode,Integer physicalWarehouseId,String actionUser,Integer userId);  // RF补货上架
	
	//根据物理仓 筛选 需要遍历的customer
	List<Integer> selectGeneralCustomerByPhysicalWarehouseId(Integer physicalWarehouseId);

	String lockReplenishJobByWarehouseCustomer(Integer productId, String string,
			String string2, List<Integer> batchPickList, Integer warehouse_id,
			Integer customer_id, List<Map> pickOrderGoodsSum,
			Map<String, List<Map>> pickOrderGoodMap, List<Integer> goodsIdList,
			Map<String, ConfigReplenishment> configReplenishmentMap, int level, 
			List<Product> productList, Integer plId, Integer moveNum, Integer toLocationId);

	Map<String, Object> printReplenishmentTaskByBatchTaskSn(String batchTaskSn, String username, Integer userId);

	List<Map> getReplenishmentTaskByBatchPickSn(Map<String, Object> searchMap);
	
}
