/**
 * 订单相关（包括订单数据查询、订单数据修改等）
 */
package com.leqee.wms.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.leqee.wms.entity.BatchPick;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.WarehouseCustomer;

/**
 * @author Jarvis
 * @CreatedDate 2016.02.02
 * @version 0.1
 *
 */
public interface OrderBiz {
	
	public OrderInfo getOrderInfoByOrderId(Integer orderId);
	
	public List<OrderGoods> getOrderGoodsByOrderId(Integer orderId);
	
	// eg. 根据orderStatus查询订单信息
	public List<Map> selectOrderInfoListByOrderStatus(String orderStatus);
	
	/**
	 * 获取销售订单商品的Map
	 * @param parseInt
	 * @return
	 * */
	public List<Map> selectSaleOrderGoodsMapByOrderId(Integer orderId);
	

	/**
	 * @author Jarvis
	 * @CreatedDate 2016.02.02
	 * 
	 * @Description 根据不同条件查询待退货入库退货订单列表
	 * 
	 * */
	public List<OrderInfo> getReturnOrderInfoList(HashMap<String, Object> conditions);

	/**
	 * @author Jarvis
	 * @param warehouseIds 
	 * @CreatedDate 2016.02.02
	 * 
	 * @Description 获取待入库退货订单数量
	 * 
	 * */
	public int getCountOfUnfulfilledReturnOrderInfo(List<Integer> warehouseIds,String customerString);

	/**
	 * @author Jarvis
	 * @CreatedDate 2016.02.04
	 * 
	 * @Description 获取退货商品扫描结果
	 * 
	 * */
	public Map<String, Object> getReturnOrderGoodsScanResult(String key, int orderId);
	
	public List<Map> getVarianceOrder(Map map);
	
	
	public Map<String, Object> selectOrderGoodsByOmsOrderSn(String omsOrderSn,String orderStatus);
	
	public 	Map resetPurchaseOrder(Integer orderId,String tagList,String actionUser);
	
	public int updateOrderInfoStatus(Integer orderId,String orderStatus,String actionUser);
	public int updateOrderProcessStatus(Integer orderId,String orderStatus);
	public void updateOrderInfoStatusV2(Integer orderId,String orderStatus,String actionUser); //添加操作日志
	public void insertUserActionOrder(Integer orderId,String orderStatus,String actionUser);
	public Map insertGroupTask(Integer orderId,String orderStatus,Integer physicalWarerhouseId,Integer customerId,String tagList,String actionUser);
	
	public OrderInfo selectOrderByOmsOrderSn(String omsOrderSn);
	
	public List<Map> selectOrdersByBatchOrderSn(String batchOrderSn);

	/**
	 * @Description 销售订单复核扫描商品
	 * @author Jarvis
	 * @CreatedDate 2016.03.11
	 * 
	 * @param shipmentId an Integer: 
	 * @param orderGoodsId an Integer: 
	 * @param serialNumber a String: 串号
	 * @return resultMap a Map<String, Object>: 扫描结果
	 * */
	public Map<String, Object> getSaleOrderGoodsScanResult(Integer shipmentId,
			Integer orderGoodsId, String serialNumber, String batchSn, Integer time);
	
	
	/**
	 * 订单详情页的信息获取
	 * @author hzhang1
	 * @param map
	 * @return
	 */
	public List<Map> getSaleOrderList(Map map,String param);
	public Map<String, Object> getSaleOrderInfoById(Map map);
	public List<Map> getSaleOrderById(Map map);
	public List<Map> getSaleOrderGoodsById(OrderInfo orderInfo);
	public List<Map> getSaleOrderActionById(Map map);
	
	
	/**
	 * 打印波次单相关SQL
	 * @author hzhang1
	 * @date 2016-03-24
	 */
	public List<Map> getPickOrderList(Map map);
	
	public BatchPick getBatchPickBySn(String batchPickSn);
	
	public Map<String,Object> dealPickOrder(String batchPickIdList[],String createdUser);
	
	public Map<String,Object> printBatchPickTag(String batchPickId);
	
	/**
	 * 打印发货单
	 * @author hzhang1
	 * @param batchPickId
	 * @return
	 */
	public List<Map> getBatchPickOrderList(String batchPickSn2OrderId);
	
	public List<Map> getDispatchBillInfo(Integer orderId);
	
	public Map getPrintDispatchInfo(String [] batchPickIdArr);

	/**
	 * 通过批次号和订单号查询
	 * @param batch_sn  批次号
	 * @param barcode  订单号
	 * @return
	 */
	public OrderInfo selectOrderBatchSnBarCode(String batch_sn, String barcode);

	/**
	 *  通过批次号和订单号查询
	 * @param batch_sn  批次号
	 * @param barcode   订单号
	 * @return
	 */
	public OrderGoods selectOrderGoodsBatchSnBarCode(String batch_sn,
			String barcode);

	public Integer selectProductIdBatchSnBarCode(String batch_sn, String barcode);

	public Map selectSendInfoByOrderId(Integer orderId);

	// to do sf 保价费用
//	public BigDecimal selectSFInsurance(Integer orderId, Integer customerId);

	public int getSumNumsHasInwareHouse(String oms_order_sn);

	public Map<String, Object> queryBatchPickByOrder_id(String no);

	/**
	 * 根据业务组查找需要调度出库的销售订单
	 * @param cutomerId 货主ID
	 * @param startDateTime 开始时间(shipping_time)
	 * @param endDateTime 结束时间(shipping_time)
	 * 
	 * @return orderIdList 销售订单ID列表
	 * */
	public List<Integer> getToDeliverInvSaleOrderIdList(Integer customerId,
			Date startDateTime, Date endDateTime);
	
	public List<Integer> getToDeliverInvSaleOrderIdListFor1111(Integer customerId,
			Date startDateTime, Date endDateTime);
	
	public List<Integer> getToAutoPurchaseAccept(Integer customerId , Integer physicalWarehouseId,Date startDateTime, Date endDateTime);
	
	public List<Integer> getTestToAutoPurchaseAccept(Integer customerId , Integer physicalWarehouseId,Date startDateTime, Date endDateTime);
	
	public List<Integer> getToDeliverInvOrderIdList(Integer customerId,
			Date startDateTime, Date endDateTime);
	
	public List<Integer> getToDeliverInvPackBoxShipmentIdList(Integer customerId,
			Date startDateTime, Date endDateTime);
	
	public List<Integer> getToDeliverInvPackBoxShipmentIdListV2(Integer customerId,
			Date startDateTime, Date endDateTime);

	public Map<String, Object> getBatchSaleOrderGoodsScanResult(Integer batchPickId, Integer orderGoodsId, Integer maxShipmentCount);

	public List<Integer> getSingleOrderIdByOrderIds(List<Integer> orderIdList);

	public List<Map<String, Object>> getShippingAppOrders(List<Integer> orderIdList,String shippingCode);

	public Map<String, Object> getIsSerialAndWarranty(String batch_sn,
			String barcode);

	public int getCountOrdersByBatchSnBarCode(String batch_sn, String barcode);

	public List<Map> doQueryGoods(String batch_pick_sn);
	
	public Map<String, Object> getCheckOrderGoodsScanResult(Integer check_order_id, String key);

	public Map<String, Object> saveTransferCodeForOrder(Integer orderGoodsId,
			String transferCode);

	public Map<String, Object> doKickOut(HttpServletRequest req);

	public void doBatchKickOut(Integer bpId, int physical_warehouse_id,
			List<WarehouseCustomer> customers, String userName);
	
	public List<Map> selectPickProductLocationByBatchPickSn(String batchPickSn);
	
	public List<Map> selectPickOrdersByBatchPickSn(String batchPickSn);
	
	public Map updatePickProductLocationByBatchPickSn(Integer taskId,Integer goodsNumber,Integer plId,Integer newPlId,Integer customerId,Integer physicalWarehouseId);

	public Map deleteOrderByBatchPickSn(Integer orderId,Integer batchPickId,Integer physicalWarehouseId);

	public void updateOrderProcessBatchTrickStatus(Integer orderId,String status);

	public List<Map<String, Object>> getOrdersByShip(
			Map<String, Object> searchMap);

	public String batchChangeShip(List<Integer> customerIdList, Integer[] ids, Integer shippingId, String username);
}
