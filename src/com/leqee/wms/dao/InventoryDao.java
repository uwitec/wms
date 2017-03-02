/**
 * 库存相关（出入库、库存查询等）
 */
package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.InventoryGoodsFreeze;
import com.leqee.wms.entity.InventoryItem;
import com.leqee.wms.entity.InventoryItemDetail;
import com.leqee.wms.entity.InventoryLocationDetail;
import com.leqee.wms.entity.LabelAccept;
import com.leqee.wms.entity.Location;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.OrderReserveInventoryMapping;
import com.leqee.wms.entity.ProductLocation;

/**
 * @author Jarvis
 * @CreatedDate 2016.02.02
 * @version 0.1
 *
 */
public interface InventoryDao {
   
   //--检查订单商品出库数和未出库数 
   public Map checkOrderProductOutNumber(Map map);
   
   public List<Map> getInventoryItemsForDeliver(Map map);
	OrderInfo selectOrderById(int orderId);
	Map selectOrderInfoByGoods(Map map);
	
	List<Map<String, Object>> getNeedOutNumByOrder(Integer orderId);
	
	Map<String, Object> getNeedOutPackBoxNumByOrder(Integer shipmentId);

	
	List<InventoryItem> getInventoryItemsForDelivery(
			Map<String, Object> paramsMap);
	
	// 查询已经被预定-v，-gt预定的耗材数量
	List<Map> inventoryItemHasReserved(@Param("productId") Integer productId,@Param("warehouseId") Integer warehouseId);

	List<InventoryItem> getInventoryItemsForTransferForV(
			Map<String, Object> paramsMap);
	
	/**
	 * 删减某个InventoryItem的Quantity
	 * @param paramsMap
	 * @return
	 */
	int subQuantityByInventoryItemId(Map<String, Object> paramsMap);

   //对某一个inventory_item加锁 for update
   public InventoryItem getInventoryItemByIdForUpdate(Integer inventoryItemId);
   
   //增加或者减少库存
   public Integer updateInventoryItemInOrOut(Map map);
   
   
   // inventoryItem表信息插入
   int insert(InventoryItem record);
   
   int updateRootItemId(Integer itemId);
   
   public List<String> selectSerialNoIsExist(List<String> serialNoList);
   
   // 更新供价接口，筛选inventory_item
   List<String> selectRootInventoryItemId(Map map);
   
   
   // 获得冻结库存信息
   List<Map> getFreezeGoodsListByPage(Map map);
   int getNowNumber(Map map);
   Map getIsExistFreezeGood(Map map);
   int insertFreeze(InventoryGoodsFreeze inventoryGoodsFreeze);
   void updateFreeze(InventoryGoodsFreeze inventoryGoodsFreeze);
   void deleteFreeze(int mapping_id);

   public List<InventoryGoodsFreeze> selectInventoryProductFreeze(
		Integer product_id, Integer warehouse_id);
   
    
    // 查询库存
    List<Map> getSaleGoodsInventory(Map map);
    List<Map> getFreezeGoodsInventory(Map map);
    List<Map> getSupplyGoodsInventory(Map map);
    List<Map> getVarianceGoodsInventory(Map map);
    List<Map> getPurchaseGoodsInventory(Map map);
    List<Map> getPackBoxInventory(Map map);
    List<Map> getInventoryLocation(Map map);
    
    // 生成容器Location
    int insertLocation(Location location);
    
    List<Location> selectLocationIsExist(Map map);
    
    Map selectByTagCode(Map map);
    Map selectByTagCodeV2(String location_barcode);
    
    // 库位管理
    //List<Map> selectLocationInfoByPage(Map map);
    
    // 更新库位信息
    int updateLocation(Map map);
    
    // 更新inventoryLocation信息
    int updateInventoryLocation(Map map);
    
    LabelAccept selectInventoryLocation(Map map);
    
    Location selectLocationByCode(Map map);
    
    ProductLocation selectProductLocation(Map map);
    
    // 推荐库位信息
    List<String> selectLocationByProduct(Map map);
    List<String> selectLocationByEmpty(Map map);
    List<String> selectLocationByEmptyV1(Map map);
    
    // 插入inventoryLocation
    int insertInventoryLocation(LabelAccept inventoryLocation);
    
    // 插入inventoryLocationDetail
    int insertInventoryLocationDetail(InventoryLocationDetail inventoLocationDetail);
    
    // 查询商品的库存
    int selectInventoryNumByOne(Map map);
    
    Integer selectInventoryReserveNumByOne(Map map);
    
    public List<Map<String,Object>> selectByOrdersnByPage( Map map);
    public List<String> selectHouseName();

	public InventoryItemDetail getLastedInstorageInventoryByCustomerId(
			Integer customerId);
	
	
	public List<Map> selectLocationV2ByPage(Map map);
	
	public List<Map> selectLocationV2(Map map);
	
	public Map selectSequenceIsExist(Map map);
	
	public int insertLocationV2(Location location);
	
	public int updateLocationV2(Location location);
	
	public int updateLocationV3(Location location);
	
	public int deleteLocationV2(Integer loc_id);
	
	public int recoverLocationV2(Integer loc_id);
	
	public List<String> selectKwBarcodeInPick(Map map); // 在波次时推荐库位
	
	public List<Integer> selectProductLocationNumWhenPick(Map map);
	
	public int updateProductLocationNumWhenPick(Map map);
	
	public int insertProductLocationWhenPick(Map map);
	
	public int insertProductLocationDetailWhenPick(Map map);
	
	// 获取缺货耗材清单
	public List<Map<String, Object>> getNotEnoughPackboxList();
	
	// 根据订单获取预定记录中待出库的记录
	public List<Map> selectDeliverOrderReserverInfo(@Param("orderId") Integer orderId,@Param("orderGoodsId") Integer orderGoodsId);

	// 根据预定信息扣减item数量
	public int updateInventoryItemByReserveMapping(@Param("inventoryItemId") Integer inventoryItemId,@Param("quantity") Integer quantity);

	// 根据订单号查找拣货任务
	public List<Map> selectSaleOrderPickTask(Integer orderId);
	
	// 扣减product_location
	public int updateProductLocation(@Param("plId") Integer plId,@Param("quantity") Integer quantity);
	
	public int updateProductLocationForPackBox(@Param("plId") Integer plId,@Param("quantity") Integer quantity);

	public List<LabelAccept> getLabelAcceptListByOrderId(@Param("order_id")Integer order_id);

	public void updateTaskIdByList(@Param("list")List<LabelAccept> list);
	
	
	public Map<String,Object> selectBybarcode(Map<String, Object> map);
	
	// 以下接口针对RF查询
	public List<Map> selectSkuInventory(@Param("physicalWarehouseId") Integer physicalWarehouseId,@Param("customerId") Integer customerId,@Param("barcode") String barcode,@Param("flag") String flag); // 根据SKU查询库存
	
	public List<Map> selectSkuInventoryInTransit(@Param("physicalWarehouseId") Integer physicalWarehouseId,@Param("customerId") Integer customerId,@Param("barcode") String barcode,@Param("flag") String flag); // 根据SKU查询库存
	
	public List<Map> selectLocationInventory(@Param("physicalWarehouseId") Integer physicalWarehouseId,@Param("locationBarcode") String locationBarcode,@Param("flag") String flag); // 根据LOCATION查询库存
	
	
	// 以下方法针对库存查询新逻辑方法
	public List<Map> selectProductLocationForInventory(Map map);

	public List<Map> selectProductLocationForTrasitInventory(Map map);
	
	public List<Map> selectProductLocationForTrasitInventoryOrderCancel(Map map);
	
	public int updateProductLocationValidity(@Param("plId") Integer plId,@Param("validity") String validity,@Param("validity_status") String validity_status,@Param("actionUser") String actionUser);
	
	public int updateProductLocationBatchSn(@Param("plId") Integer plId,@Param("batch_sn") String batch_sn,@Param("actionUser") String actionUser);
	
	public List<String> selectLocationByProductValidityNormal(Map<String,Object> map);
	
	public List<String> selectLocationByProductValidityDefective(Map<String,Object> map);
	
	public List<String> selectLocationByCanMixBatchNormal(Integer physical_warehouse_id,String product_id);
	
	public List<String> selectLocationByCanMixBatchDefective(Integer physical_warehouse_id,String product_id);
	
	public List<Map<String, Object>> selectDifferentItemByCustomerId(@Param("physicalWarehouseId")Integer physical_warehouse_id,@Param("customerId")Integer customer_id,@Param("con")String con,@Param("con1")String con1);
	
	public List<Map> selectImportProductLocation(@Param("customerId") Integer customerId,@Param("physicalWarehouseId") Integer physicalWarehouseId,@Param("flag") String flag);

	public List<InventoryItem> selectItemByProductId(@Param("productId") Integer productId,@Param("physicalWarehouseId") Integer physicalWarehouseId,@Param("status") String status);

	public List<Map<String,Object>> selectTmpProductLocationByTmpList(@Param("list")List<Map> list);
	
	public List<Map<String,Object>> selectItemByCustomerId2PhysicalWarehouseId(@Param("customerId") Integer customerId,@Param("physicalWarehouseId") Integer physicalWarehouseId);
	
	public List<Map> selectProductLocationByProductId(@Param("productId") Integer productId,@Param("physicalWarehouseId") Integer physicalWarehouseId,@Param("status") String status);

	public Map<String,Object> selectPurchaseLableInfo(@Param("purchaseLable") String purchaseLable,@Param("physicalWarehouseId") Integer physicalWarehouseId);
	
	public List<Map<String,Object>> getOrderCancelList(Map<String,Object> map);
	
	public List<Map<String,Object>> getOrderCancelListByOrderId(Integer order_id);

	public List<InventoryItem> selectAllInventoryItem(@Param("list")List<Integer> productIdList);

	public List<OrderReserveInventoryMapping> selectAllReservedMapper(@Param("list")List<Integer> orderGoodIdList);

	public void updateInventoryItemByVariance(@Param("list")List<InventoryItem> updateItList);


	public List<InventoryItem> getInventoryItemsByCustomerId(@Param("customer_id")Integer customer_id);

	//hchen1
	 Location selectLocationByCodeV1(Map map);
	 
	public List<Map> selectReservedQty2OrderGoodsNumber(@Param("order_goods_id")Integer order_goods_id);
		
	public List<Map> selectReservedQty2OrderGoodsOmsNumber(@Param("order_goods_id")Integer order_goods_id);
	//资料查询页面分页查询
	public List<Map<String,Object>> selectMoveListByPage(Map<String,Object> map);
	
	public List<Map<String,Object>> selectRecheckListByPage(Map<String,Object> map);
	
	public List<Map<String,Object>> selectPickListByPage(Map<String,Object> map);
	
	public List<Map<String,Object>> selectBhListByPage(Map<String,Object> map);
	
	public List<Map<String,Object>> selectRukuListByPage(Map<String,Object> map);
	
	public List<Map<String,Object>> selectGroundingListByPage(Map<String,Object> map);
	public List<Map<String,Object>> selectGroundingPurchaseListByPage(Map<String,Object> map);
	public List<Map<String,Object>> selectGroundingReturnListByPage(Map<String,Object> map);
	public List<Map<String,Object>> selectGroundingCancelListByPage(Map<String,Object> map);
	
	public List<Map<String,Object>> selectMaintainListByPage(Map<String,Object> map);
	
	//资料查询页面导出功能
	public List<Map<String,Object>> selectMoveList(Map<String,Object> map);
	
	public List<Map<String,Object>> selectRecheckList(Map<String,Object> map);
	
	public List<Map<String,Object>> selectPickList(Map<String,Object> map);
	
	public List<Map<String,Object>> selectBhList(Map<String,Object> map);
	
	public List<Map<String,Object>> selectRukuList(Map<String,Object> map);
	
	public List<Map<String,Object>> selectGroundingList(Map<String,Object> map);
	public List<Map<String,Object>> selectGroundingPurchaseList(Map<String,Object> map);
	public List<Map<String,Object>> selectGroundingReturnList(Map<String,Object> map);
	public List<Map<String,Object>> selectGroundingCancelList(Map<String,Object> map);
	
	public List<Map<String,Object>> selectMaintainList(Map<String,Object> map);

	/**
	 * 查询出库资料
	 */
	public List<Map<String, Object>> selectStockOutListByPage(Map<String, Object> map);

	public List<Map<String, Object>> selectStockOutList(Map<String, Object> map);
	
	

}
