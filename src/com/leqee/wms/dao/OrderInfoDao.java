package com.leqee.wms.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.api.response.domain.OrderInfoResDomain;
import com.leqee.wms.entity.BatchPick;
import com.leqee.wms.entity.OmsOrderTransferCode;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.SameDetails;
import com.leqee.wms.entity.Shipment;
import com.leqee.wms.entity.Shop;

public interface OrderInfoDao {
     
	public int insert(OrderInfo orderInfo);
	
	public OrderInfo selectByPrimaryKey(int order_id);
	
	/**
	 * @author Jarvis
	 * @Description 根据主键ID获取订单列表并锁住（为了更新用）
	 * 
	 * @param orderIdList a List<Integer>: 主键ID列表
	 * 
	 * @return orderInfoList a List<OrderInfo>: 订单列表
	 * */
	public List<OrderInfo> selectOrderInfoListByIdForUpdate(List<Integer> orderIdList);

	/**
	 * @author Jarvis
	 * @Description 更新订单状态
	 * 
	 * @param paramsForOrderInfoUpdate a Map: 参数列表
	 * 
	 * @return effectRows an int: 影响行数
	 * */
	public int updateOrderStatusByOrderId(Map<String, Object> paramsForOrderInfoUpdate);
	public int updateOrderStatusShippingByOrderId(Map<String, Object> paramsForOrderInfoUpdate);
	
	public OrderInfo selectByOmsOrderSn(String oms_order_sn);
	
	public OrderInfo selectTerminalOrderByOmsOrderSn(String oms_order_sn);
	
	public List<Map> selectByBatchOrderSn(Map map);
 
	/**
	 * @author Jarvis
	 * @Description 根据不同条件查询退货订单
	 * 
	 * @param conditions a Map: 参数列表
	 * 
	 * @return returnOrderInfoList a List<OrderInfo>: 退货订单列表
	 * */
	public List<OrderInfo> selectReturnOrderInfoListByPage(Map<String, Object> conditions);

	/**
	 * @author Jarvis
	 * @param warehouseIds 
	 * @Description 获取待入库退货订单数量
	 * 
	 * @return returnOrderInfoList an int: 待入库退货订单数量
	 * */
	public int selectCountUnfulfilledReturnOrderInfo(@Param("list")List<Integer> warehouseIds,@Param("customerString")String customerString);

	/**
	 * @author Jarvis
	 * @Description 根据父订单号查询退货订单
	 * 
	 * @param orderId an Integer: 参数列表
	 * 
	 * @return returnOrderInfoList a List<OrderInfo>: 退货订单列表
	 * */
	public List<OrderInfo> selectReturnOrderListByParentOrderId(Integer parentOrderId);

	public Integer updateOrderInfoForReserve(Integer order_id, String reserve_stauts);

	/**
	 * 通过修改时间、订单类型等获取OrderInfoResDomain列表
	 * @param paramsMap
	 * @return
	 */
	public List<OrderInfoResDomain> selectOrderInfoResDomainList(
			Map<String, Object> paramsMap);
	
	public List<Map> selectVarianceOrderByPage(Map map);
	
	public Map selectVarianceOrderByOrderGoodsId(Integer orderGoodsId);
	
	public Map selectInventoryItemType(Map map);
	
	public List<Map> selectOrderGoodsByOmsOrderSn(String omsOrderSn);
	
	public List<Map> selectOrderGoodsByOmsOrderSnV2(String omsOrderSn);

	public List<Map> selectSaleOrderListByOrderIdByPage(Map map);
	
	public List<Map> selectSaleOrderListByBatchPickSnByPage(Map map);
	
	public List<Map> selectSaleOrderListByPage(Map map);
	
	public List<Map> selectSaleOrderIdBatchPickSnMap(@Param("saleOrderList") List<Map> saleOrderList);
	
	public List<Map> selectSaleOrderListForExport(Map map);
	
	public List<Map> selectSaleOrderById(Map map);
	
	public List<Map> selectSaleOrderGoodsById(Map map);
	
	public List<Map> selectSaleOrderGoodsGroupByProductId(Map map);
	
	public List<Map> selectSaleOrderGoodsByIdV2(Map map);
	
	public List<Map> selectSaleOrderActionById(Map map);

	
	/**
	 * @author dlyao
	 * @param order_id
	 * @return hashMap
	 */
	public Map selectOrderGoodsByOrderId(int order_id);



	
	/**
	 * 波次单相关接口
	 * @author hzhang1
	 * @param map
	 * @return
	 */
	public BatchPick selectBatchPickBySn(String batchPickSn);
	public List<Map> selectPickOrderListByPage(Map map);
	public List<Map> selectBatchPickOrderListV2(Integer batchPickId);

	public int updateBatchPickPrintCount(Integer batchPickId);
	
	/**
	 * 打印波次单
	 * @param batchPickId
	 * @return
	 */
	public List<Map> selectBatchPickOrderList(Map map);
	public List<Map> selectBatchPickOrderGoodsList(Integer orderId);
	
	public Map selectDispatchBillByShop(Map map);
	public Map selectDispatchBillByCustomer(Integer customerId);
	public List<String> selectAllShopName(int customer_id);
	/**
	 * @author dlyao
	 * @param order_id 
	 * @return 返回和指定订单号上商品完全相同的订单号
	 */
	public List<Integer> selectOrderIdWithSameOrderGoods(int order_id);

	/*
	 * 获取相同规则集
	 */
	public List<SameDetails> selectOrderInfoListForBatchPick(Map<String, Object> parmMap);
	public List<SameDetails> selectOrderInfoListForBatchPickByShipment(Map<String, Object> parmMap);	
	public List<SameDetails> selectOrderInfoListForBatchPickV3(Map<String, Object> parmMap);
	
	
	public List<Integer> batchPickAllOrderListByParm(Map<String, Object> parmMap);
	public List<Integer> batchPickAllOrderListByParmByShipment(Map<String, Object> parmMap);
	public List<Integer> batchPickAllOrderListByParmV3(Map<String, Object> parmMap);
	public List<Integer> batchPickAllLastOrderListByParm(Map<String, Object> parmMap);

	/**
	 * 在创建批捡单时更新订单信息
	 * @param parmMap 订单信息
	 */
	public void updateOrderInfoForBatchPick(HashMap<String, Object> parmMap);
	
	public void updateTrackingNumber(Integer orderId, String trackingNumber);

	/**
	 * 在缓存为空时需要重新查询
	 * @param parmMap 参数
	 * @return 该货主 30天里的商店名称
	 */
	public List<String> selectShopNameNullCache(Map<String, Object> parmMap);

	/**
	 * 在缓存为空时需要重新查询
	 * @param parmMap 参数
	 * @return 该货主 30天里的商店名称
	 */
	public List<Shop> selectShopNameNullCacheV2(Map<String, Object> parmMap);
	public List<Shop> selectShopNameNullCacheV3(Map<String, Object> parmMap);
	/**
	 * 根据批次号和商品条码找到对应的订单
	 * @param paramMap
	 * @return 
	 */
	public OrderInfo selectOrderBatchSnBarCode(HashMap<String, Object> paramMap);

	public OrderGoods selectOrderGoodsBatchSnBarCode(
			HashMap<String, Object> paramMap);

	public Integer selectProductIdBatchSnBarCode(
			HashMap<String, Object> paramMap);

	public BigDecimal getShipmentOrderAmount(Integer orderId);

	public List<String> getLeqeeElecEduInsurance(Integer orderId);

	public int selectCountNumsHasInwareHouse(String oms_order_sn);

	public int selectSumNumsHasInwareHouse(String oms_order_sn);

	public Integer updateOrderProcessStatusByOrderId(
			Map<String, Object> paramsForOrderInfoUpdate);

	public void updateOrderInfo24Hours(String time24String);

	public void updateOrderInfo36Hours(String time36String);

	public List<Integer> getOrderLevel3(@Param("physical_warehouse_id")int physical_warehouse_id,
			@Param("customer_id")Integer customer_id, @Param("shop_name")List<String> shop_name, @Param("is2B")String is2B);
	
	public List<Integer> getOrderLevel3New(@Param("physical_warehouse_id")int physical_warehouse_id,
			@Param("customer_id")Integer customer_id, @Param("warehouse_id")int warehouse_id, @Param("list")List<String> oms_shop_id, @Param("is2B")String is2B);

	public List<OrderInfo> getSingleOrderByShopByPage(Map<String, Object> parmMap);


	public void updateOrderInfoListForBatchPick(HashMap<String, Object> parmMap);

	public List<Map> getUnbindGoodsInfo(Integer orderId);

	public Map<String, Object> queryBatchPickByOrder_id(@Param("order_id")int parseInt);

	public int getCountOrdersByBatchPickId(int batch_pick_id);

	public String getUserNameByBP(@Param("batch_pick_id")int parseInt);

	public OrderInfo selectByPrimaryKeyForUpdate(Integer orderId);

	public List<Integer> getSingleOrderIdByOrderIds(@Param("orderIdList")List<Integer> orderIdList);

	public List<Map<String, Object>> getShippingAppIdList(@Param("orderIdList")List<Integer> orderIdList,
			String shippingCode);

	public List<Map<String, Object>> getShippingAppOrders(
			@Param("orderIdList")List<Integer> orderIdList, @Param("warehouseId")Integer warehouseId, @Param("shippingId")Integer shippingId);

	public Map<String, Object> getIsSerialAndWarranty(@Param("batch_sn")String batch_sn,
			@Param("barcode")String barcode);

	public int getCountOrdersByBatchSnBarCode(@Param("batch_sn")String batch_sn, @Param("barcode")String barcode);
	
	public int batchUpdateOrderStatusByOrderId(
			@Param("paramsForOrderInfoUpdateList")List<Map<String, Object>> paramsForOrderInfoUpdateList);

	public int batchUpdateOrderStatusByOrderId(@Param("orderIdList")List<Integer> orderIdList,
			@Param("orderStatus")String orderStatus, @Param("lastUpdatedUser")String localUser);

	public int updateOrderInfoInProcess(@Param("oms_order_sn")String oms_order_sn, @Param("action_user")String action_user, @Param("status")String status);

	public int updateOrderInfo2InProcess(@Param("oms_order_sn")String oms_order_sn, @Param("action_user")String action_user, @Param("status")String status);

	public List<Map> selectPickOrderGoodsListSum(@Param("batchPickList")List<Integer> batchPickList);
	
	public List<Map> selectPickOrderGoodsList(Integer batchPickId);
	
	public List<Map> selectPickOrderGoodsList2(@Param("batchPickSn") String batchPickSn);
	
	public List<Map> selectPickOrdersList2(@Param("batchPickSn") String batchPickSn);
	
	public List<Map> selectPickOrderGoodsListByList(@Param("batchPickList")List<Integer> batchPickList);

	public List<Integer> getGtOrderIdList(@Param("status_id")String status_id,
			@Param("physical_warehouse_id")Integer physical_warehouse_id, @Param("customer_id")Integer customer_id, @Param("warehouse_id")int warehouse_id);

	public List<Map> selectGtOrderGoodsListSum(@Param("gtOrderIdList")List<Integer> gtOrderIdList);

	public List<Map> selectGtOrderGoodsList(@Param("gtOrderIdList")List<Integer> gtOrderIdList);

	public List<Map> selectGtOrderGoodsListV5(@Param("gtOrderIdList")List<Integer> orderIdList);

	public List<Map> selectSaleOrderGoodsListV5(@Param("list")List<Integer> orderIdList);
	
	public void updateReserveStatus(@Param("gtYList")List<Integer> gtYList, @Param("status")String status);


	/**
	 * @author xhchen
	 * @param map
	 * @return
	 * 查询wms已出库，oms未出库的订单列表
	 */
	public List<Shipment> selectOrderShipmentList(Map<String, Object> map);

	/**
	 * 根据orderId更新到货时间
	 * @param orderId
	 * @param arrivalTime
	 * @return
	 */
	int updateArrivalTimeByOrderId(@Param("orderId") Integer orderId, @Param("arrivalTime") Date arrivalTime);

	public List<Map> selectPickOrderGoodsListByBatchPickSn(@Param("batch_pick_sn")String batch_pick_sn);

	public List<Map> selectPickOrderGoodsListByBatchPickId(@Param("batch_pick_id")Integer batch_pick_id);
	
	public List<OrderInfo> selectAllVarianceMinusOrderIdsForUpdate(@Param("physical_warehouse_id")int p_id,
			@Param("customer_id")int c_id, @Param("order_type")String order_type, @Param("is_reserved")String is_reserved, @Param("warehouse_id")int warehouse_id);

	public List<Map> selectVarianceMinusImproveOrderGoodsMap(
			@Param("orderIdList")List<Integer> varianceMinusImproveOrderIds);

	public void updateBatchOrderInfos(@Param("list")List<Integer> orderIdList, @Param("status")String status);

	public void saveTransferCodeForOrder(OmsOrderTransferCode omsOrderTransferCode);

	public List<String> selectIsNeedTransferCode(Integer orderId);

	public void deleteTransferCodeByOrder(Integer orderId);

	public void updateForKickOutOrdersStatus(@Param("list")List<Integer> orderIdList);

	public List<Map> selectGtOrderGoodsListByOmsOrderSn(@Param("oms_order_sn")String oms_order_sn);

	public List<Map> getNoPlOrderListByProductIdList(
			@Param("list")List<Integer> noPlProductIdList,@Param("physical_warehouse_id")int physical_warehouse_id, @Param("warehouse_id")int warehouse_id);

	public void updateOrderInfoLevelUp(@Param("physical_warehouse_id")int p_id, @Param("customer_id")int c_id, @Param("level")Integer level,
			@Param("start")String start, @Param("end")String end);

	public void updateReserveResultList(@Param("list")List<Integer> orderYList, @Param("is_reserved")String is_reserved);

	public int selectMiddleLevelTime();

	public int selectHighLevelTime();

	public List<OrderInfo> getSingleOrderByShopV2ByPage(
			Map<String, Object> parmMap);

	public List<OrderGoods> getOrderGoodsList(@Param("list")List<Integer> orderIdList);

	public List<Shop> getShopListFromTableOrderInfo(@Param("start")String time);

	public List<Map> selectCustomerShopCache(@Param("customerId")Integer customerId);

	public List<Map<String, Object>> getOrdersByShipByPage(
			Map<String, Object> searchMap);

	public List<OrderInfo> selectBatchOrderByIdForUpdate(Integer[] ids);

	public int batchUpdateShipId(@Param(value="updatelist")List<Integer> updatelist, @Param(value="shippingId")Integer shippingId,
			@Param(value="username")String username);

	public void updateBatchPickRePrintCount(Integer batchPickId);

	public int getWarehouseIdBySn(@Param(value="oms_order_sn")String oms_order_sn);

}
