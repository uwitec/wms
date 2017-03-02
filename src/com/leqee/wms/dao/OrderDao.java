/**
 * 订单相关（包括订单数据查询、订单数据修改等）
 */
package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.InventoryItem;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.entity.OrderInfo;
/**
 * @author Jarvis
 * @CreatedDate 2016.02.02
 * @version 0.1
 *
 */
public interface OrderDao {
	
	public OrderInfo selectOrderInfoByOrderId(Integer orderId);
	
	public List<OrderGoods> selectOrderGoodsByOrderId(Integer orderId);
	
	public List<OrderGoods> selectOrderGoodsByOrderIdV1(Integer orderId);
	
	// eg. 根据orderStatus查询订单信息
	public List<Map> selectOrderInfoListByOrderStatus(String orderStatus);

	/**
     * 获取销售订单商品的Map
     * @param orderId
     * @return
     */
	public List<Map> selectSaleOrderGoodsMapByOrderId(Integer orderId);

	/**
	 * 获取订单并锁住（为了更新用）
	 * @param orderId
	 * @return
	 */
	public OrderInfo selectOrderInfoByIdForUpdate(Integer orderId);
	
	// 对某一个订单加锁 for update 
	public OrderInfo getOderInfoByIdForUpdate(Integer orderId);
	
	/**
	 * 查询采购订单列表
	 * @author hzhang1
	 * @return
	 */
	public List<Map> selectPurchaseOrderListByPage(Map map);
	
	public List<Map> selectPurchaseBatchOrderSn(Map map);
	
	public List<Map> selectPurchaseOrderGoodsListByOrderId(Integer orderId);
	
	public List<Map> selectPurchaseIsGrouping(Integer orderId);
	
	public void deletePurchaseLabel(Map map);
	
	public Map selectPurchaseAcceptNum(Map map);
	
	/**
	 * 查询采购订单已入库数量
	 * @param orderIdList
	 * @return
	 */
	public List<Map> selectInOutNumByList(List<Integer> orderIdList);
	
	/**
	 * 查询采购订单的商品
	 * @param map
	 * @return
	 */
	public Map selectOrderInfoByGoods(Map map);
	/**
	 * 查询退货订单的商品
	 * @param map
	 * @return
	 */
	public Map<String, Object> selectCancelOrderInfoByGoods(Map<String, Object> map);
	
	/**
	 * 查询打印快递单信息
	 * @param order_id
	 * @return
	 */
	public Map<String, Object> selectPrintInfoByOrderId(Integer order_id);
	
	public List<Map> selectPurchaseIsOver(Integer order_id);
	
	public List<Map> selectPurchaseIsOver_yanhuo(Integer order_id);

	public Map selectSendInfoByOrderId(Integer orderId);

	/**
	 * @author zjli
	 * 查询待调度出库销售订单
	 * */
	public List<Integer> selectToDeliverInvSaleOrderIdList(
			Map<String, Object> paramsMap);
	
	public List<Integer> selectToDeliverInvSaleOrderIdListFor1111(
			Map<String, Object> paramsMap);
	
	public List<Integer> selectToAutoPurchaseAcceptOrderIdList(
			Map<String, Object> paramsMap);
	
	public List<Integer> selectTestToAutoPurchaseAcceptOrderIdList(
			Map<String, Object> paramsMap);
	
	public List<Integer> selectToDeliverInvOrderIdList(
			Map<String, Object> paramsMap);
	
	public List<Integer> selectToDeliverInvPackBoxShipmentIdList(
			Map<String, Object> paramsMap);
	
	public List<Integer> selectToDeliverInvPackBoxShipmentIdListV2(
			Map<String, Object> paramsMap);
	
	public OrderInfo selectByOrderIdOrOrderSn(Map<String, Object> map);

	public List<String> selectIsNeedTransferCode(Integer orderId);
	
	public List<Map<String, Object>> selectOrderGoods(Integer order_id,String barcode,Integer customer_id);
	
	
	public List<Map<String, Object>> selectOrderGoodsV1(@Param("orderIdList") List<Integer> orderIdList,@Param("customerString")String customerString);
}
