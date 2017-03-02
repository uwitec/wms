/**
 * 库存相关（出入库、库存查询等）
 */
package com.leqee.wms.biz;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.leqee.wms.vo.PurchaseAcceptVO;
import com.leqee.wms.vo.ReturnProductAccDetail;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.leqee.wms.entity.InventoryItem;
import com.leqee.wms.entity.Location;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.response.Response;

public interface InventoryBiz {

	/**
	 * 销售订单出库
	 * @param orderId
	 * @param actionUser
	 * @return
	 */
	public Response saleDeliverInventory(Integer orderId, String actionUser);
	
	public Response saleDeliverInventoryFor1111(Integer orderId, String actionUser);

	/**
	 * 耗材出库
	 * @param orderId
	 * @param actionUser
	 * @return
	 */
	public Response packBoxDeliverInventory(Integer shipmentId,String actionUser);
	
	
	/**
	 * 订单出库扣减item数量
	 * @param orderId
	 * @param orderGoodsId
	 * @return
	 */
	public Response deliverOrderInventory(String orderType,Integer orderId,Integer orderGoodsId);
	
	/**
	 * 订单出库扣减product_location
	 * @param orderId
	 * @return
	 */
	public Response deliverOrderProductLocation(Integer orderId);
	
	/**
	 * @author Jarvis
	 * @CreatedDate 2016.02.04
	 * 
	 * @Description 查找退货订单相对应的销售订单出库的商品串号
	 * 
	 * @param orderId an int: 退货订单ID
	 * @param productId an int: 商品ID
	 * @return outSerialNumbers the List<String>: 商品串号列表
	 * 
	 * */
	public List<String> getOutSerialNumbers(int orderId, int productId);

	/**
	 * @author Jarvis
	 * @CreatedDate 2016.02.04
	 * 
	 * @Description 判断串号商品是否已经在库存中
	 * 
	 * @param serialNumber a String: 串号
	 * @return isInStock a boolean: 是否在库
	 * 
	 * */
	public boolean isSerialGoodInStock(String serialNumber);

	/**
	 * @author Jarvis
	 * @CreatedDate 2016.02.06
	 * 
	 * @Description 退货入库
	 * 
	 * @param orderId an Integer: 退货订单订单ID
	 * @param returnProductAccDetails a List<ReturnProductAccDetail>: 退货商品清单
	 * @param actionUser a String: 操作人
	 * @return response a Response: 反馈信息
	 * */
	public Response returnAcceptInventory(Integer orderId,
			List<ReturnProductAccDetail> returnProductAccDetails, String actionUser);

	
	 /**
	  * 
	  * @param isSerial  判断是否串号(如果是串号，那么商品数量为1，并且对应一个串号)
	  * @param serialNo  串号
	  * @param statusId  商品呢库存的新旧状态
	  * @param productId 仓库商品编号
	  * @param amount    出库数量
	  * @param orderId   订单号
	  * @param orderGoodsId  订单商品表主键
	  * @param actionUser 操作人
	  * @param facilityId  仓库编码
	  * @param unitPrice   单价
	  * @param batchSn    批次号
	  * @param returnSupplierId  退给的供应商号
	  * 提供批量出库接口(供应商退货出库和-v出库操作)
	  */
	public boolean dealBatchDelivery(
			Boolean isSerial,
			String serialNo,
			String statusId,
			String productId,
			BigDecimal amount,			
			String orderId,
			String orderGoodsId,
			String actionUser,			
			String facilityId,
			BigDecimal unitPrice,
			String batchSn,
			String returnSupplierId,
			String customerId);

	/***************************
	 * 采购订单入库
	 * by hzhang1
	 ***************************/
	// 查找采购订单
	public List<Map> selectPurchaseOrderList(Map map); 
	
	// 生成标签
	public int generateLocation(String locationBarcode,Integer physicalWarehouseId,String actionUser,Integer customerId);
	
	// 生成容器
	public Map<String,Object> printTag(String[] tagList,String actionUser);
	
	// 容器 & 库位管理
	public List<Map> getLocation(Map map);
	
	public boolean selectPurchaseIsOver(Integer order_id);
	
	// 采购入库上托盘
	public Map insertInventoryLocation(PurchaseAcceptVO purchaseAcceptVO,Integer orderId ,String createdUser,Integer customerId);
	
	// 根据标签搜索
	public List<Map> selectByTagCode(Integer customer_id,String location_barcode);
	
	// 处理库位
	public Map dealLocation(String act,Integer customerId,Integer warehouseId,String locationBarcode,Integer locationId,String locationType,String actionUser);
	
	// 更新invenotryLocation信息
	public void updateInventortLocation(Integer customerId,Integer physicalWarehouseId,String tagCode, 
			String locationBarcode,
			Integer orderId, Integer orderGoodsId, Integer productId,
			Integer quantity, String actionUser);
	
	
	// 根据orderId 和 orderGoodsId 查找需要入库的单子
	public Map createPurchaseAccept(Integer customerId, Integer orderId,
			Integer orderGoodsId, Integer warehouseId,Integer physicalWarehouseId, String tagCode,
			String locationBarcode, String actionUser);

	
	
	/**
	 * API 方法(-V调整库存)
	 * @author hzhang1
	 */
	public Map<String, Object> deleteViranceOrder(
			Integer orderId, String actionUser);
	
	public Map<String,Object> deliverInventoryViranceOrderInventory(Integer orderGoodsId , String serialNumber ,String actionUser);

	public Map<String,Object> createInventoryItemVarianceByProductId(Integer productId,
			String inventoryItemAcctTypeName, String inventoryItemTypeName,
			String statusId, String serialNumber, Integer quantity,
			BigDecimal unitCost, Integer warehouseId, String comment,
			Integer orderId, Integer orderGoodsId, String batchSn,
			Integer customerId,String actionUser,String providerId);
	
	
	/**
	 * 冻结库存
	 * @author hzhang1
	 * @param map
	 * @return
	 */
	public List<Map> getFreezeGoods(Map map);
	public List<Map<String,Object>> selectByOrdersn( Map map);
//	public List<String> selectHouseName();
	
	public Map<String,Object> dealFreeze(String act,Integer warehouse_id,Integer ori_warehouse_id,Integer reserve_number,String freeze_reason,Integer mapping_id,Integer customer_id,Integer product_id);

	/**
	 * 库存查询
	 * @author hzhang1
	 * @param map
	 * @return
	 */
	public List<Map> getInventoryGoods(Map map);
	
	
	/**
	 * 修改库位库存生产日期
	 * @param plId
	 * @param validity
	 * @return
	 */
	public Map editProductLocationValidity(Integer plId,String validity,String actionUser);
	
	public Map editProductLocationBatchSn(Integer plId,String batchSn,String actionUser);
	
	/**
	 * 根据serialNumber查找一条有库存的InventoryItem记录，若库存数量大于1，抛出异常
	 * @param serialNumber
	 * @return
	 * */
	public InventoryItem findOneInventoryItemBySerialNumber(String serialNumber);

	/**
	 * 出库前检测传入的数据是否规范
	 * @param isSerial
	 * @param serialNums
	 * @param status
	 * @param productId
	 * @param amount
	 * @param orderId
	 * @param orderGoodsId
	 * @param warehouse_id
	 * @param unitPrice
	 * @param batchSn
	 * @param returnSupplierId
	 * @return
	 */
	HashMap<String, Object> checkDealBatchDelivery(Boolean isSerial,
			String serialNums, String status, String productId,
			BigDecimal amount, String orderId, String orderGoodsId,
			String warehouse_id, BigDecimal unitPrice, String batchSn,
			String returnSupplierId,String customer_id);

	public HashMap<String, Object> create_tag(HttpServletRequest req);

	/**
	 * 搜索库位（二期）
	 * @param map
	 * @return
	 */
	public List<Map> getLocationV2(Map map);
	
	public Map insertLocationV2(Location location,String type);
	
	public Map deleteLocationV2(int loc_id);
	
	public Map recoverLocationV2(int loc_id);
	
	public Map uploadLocationV2(InputStream input,boolean isE2007,String actionUser,Map physicalWarehouseMap,Map customersMap);
	
	public Map uploadInventory(InputStream input,boolean isE2007,String actionUser,Map physicalWarehouseMap,Map customersMap);

	public int send_sure(Integer physical_warehouse_id, String action_user, OrderInfo orderInfo, OrderGoods ordergoods);

	public void updateOrderInfoInProcess(String oms_order_sn);
	
	public Map<String,Object> createReturnAccept(Map<Object,Object> map);
	
	public Map<String,Object> selectBybarcode(Integer order_id,String barcode,Integer customer_id,Integer physical_warehouse_id);
	
	public Map<String,Object> selectByOrderId(Integer order_id,Integer physical_warehouse_id);
	
	
	public Map<String,Object> createAllReturnAccept(Map<Object,Object> map);
	
	
	
	// 以下方法针对的是RF关于库存的方法 by hzhang1
	public Map<String,Object> getSkuInventory(Integer physicalWarehouseId,Integer customerId,String barcode,String flag); // 根据SKU查询库存
	
	public Map<String,Object> getLocationInventory(Integer physicalWarehouseId,String locationBarcode,String flag); // 根据LOCATION查询库存
	
	public Map<String,Object> freezeSkuInventory(Integer physicalWarehouseId,Integer customerId,String barcode,Integer plId,Integer quantity,String actionUser); // 根据SKU冻结库存
	
	public Map<String,Object> freezeLocationInventory(Integer physicalWarehouseId,Integer customerId,String locationBarcode,Integer plId,Integer quantity,String actionUser); // 根据LOCATION冻结库存
	
	public Map<String,Object> releaseFreezeInventory(Integer physicalWarehouseId,Integer customerId,Integer plId,Integer quantity,String actionUser); // 释放冻结库存
	
	public Map<String, Object> checkStockMoveSourceLocation(String locationBarcode, Integer physicalWarehouseId); // RF移库之库存移动 -- 源库位检查

	public Map<String, Object> stockMove(String fromLocationBarcode,String toLocationBarcode, Integer plId, Integer moveNum,Integer physicalWarehouseId); // RF移库之库存移动 -- 目标库位检查 & 完成移库

	public Map<String, Object> checkStockTransferSourceLocation(String locationBarcode, Integer physicalWarehouseId); // RF移库之库存转移-- 源库位检查

	public Map<String, Object> stockTransfer(String fromLocationBarcode,String toLocationBarcode, Integer plId, Integer moveNum, Integer physicalWarehouseId);// RF移库之库存转移-- 目标库位检查 & 完成移库
	
	public Map<String, Object> importProductLocation(Integer customerId,Integer physicalWarehouseId); // 仓库内的item转向product_location
	
	public Map<String, Object> autoPurchaseAccept(Integer orderId,Integer customerId,Integer physicalWarehouseId); // 调拨单自动入库
	
	public Map<String, Object> testAutoPurchaseAccept(Integer orderId,Integer customerId,Integer physicalWarehouseId); // 调拨单自动入库
	

	public Map<String,Object> getPurchaseLableInfo(String purchaseLable,Integer physicalWarehouseId);
	
	public Map<String,Object> getOrderCancelList(Map<String,Object> map);
	
	public Map<String,Object> getMoveList(Map<String,Object> map);
	
	public Map<String,Object> getRecheckList(Map<String,Object> map);
	public Map<String,Object> getPickList(Map<String,Object> map);
	public Map<String,Object> getBhList(Map<String,Object> map);
	public Map<String,Object> getRukuList(Map<String,Object> map);
	public Map<String,Object> getGroundingList(Map<String,Object> map);
	public Map<String,Object> getMaintainList(Map<String,Object> map);
	
	

	public void doVarianceMinus(int physical_warehouse_id, int customer_id,
			Map<String, List<Map>> varianceMinusImproveOrderGoodsMap, List<Integer> productIdList, List<Integer> orderIdList, List<Integer> orderGoodIdList, int warehouse_id);  //盘亏调整

	public void doVarianceAdd(int physical_warehouse_id, int customer_id,
			Map<String, List<Map>> varianceAddImproveOrderGoodsMap, List<Integer> productIdList, List<Integer> orderIdList, List<Integer> orderGoodIdList, int warehouse_id);  //盘盈调整

	public Response PrePackBoxOut(Integer physical_warehouse_id,
			Integer customer_id, int order_id, String order_sn,
			int label_prepack_id, int packbox_product_id, int packbox_need_out, int warehouse_id);

	public Map<String, Object> stockMoveLocationGoodsCheck(
			String locationBarcode, Integer physical_warehouse_id,
			String goodsbarcode);
	
	/**
	 * hchen1
	 * 新退货入库
	 * @param orderId
	 * @param returnProductAccDetails
	 * @param actionUser
	 * @return
	 */
	public Response returnNewAcceptInventory(Integer orderId,
			List<ReturnProductAccDetail> returnProductAccDetails, String actionUser);

	/**
	 * 出库资料查询 by ytchen
	 * @param searchMap
	 * @return
	 */
	public Map<String, Object> getStockOutList(Map<String, Object> searchMap);

	public List<Map<String, Object>> getStockOutListForExport(Map<String, Object> searchMap);

}
