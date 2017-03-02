
package com.leqee.wms.api.biz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.leqee.wms.api.request.AdjustPriceRequest;
import com.leqee.wms.api.request.CancelOrderRequest;
import com.leqee.wms.api.request.GetOrderListRequest;
import com.leqee.wms.api.request.GetOrderPrepacksRequest;
import com.leqee.wms.api.request.GetOrderShipmentListRequest;
import com.leqee.wms.api.request.GetPurchaseOrderRequest;
import com.leqee.wms.api.request.GetRmaOrderRequest;
import com.leqee.wms.api.request.GetSaleOrderRequest;
import com.leqee.wms.api.request.GetVarianceOrderRequest;
import com.leqee.wms.api.request.SyncOrderPrepackRequest;
import com.leqee.wms.api.request.SyncPurchaseOrderRequest;
import com.leqee.wms.api.request.SyncRmaOrderRequest;
import com.leqee.wms.api.request.SyncSaleOrderRequest;
import com.leqee.wms.api.request.SyncVarianceOrderRequest;
import com.leqee.wms.api.request.TerminalOrderRequest;
import com.leqee.wms.api.request.domain.OrderGoodsReqDomain;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.OrderPrepack;

/**
 * @author hzhang1
 * @date 2016-2-25
 * @version 1.0.0
 */
public interface OrderInfoApiBiz {
	/**
	 * API 同步采购订单接口
	 * @param syncPurchaseOrderRequest
	 * @param customerId
	 * @return
	 */
	public Map<String,Object> syncPurchaseOrder(SyncPurchaseOrderRequest syncPurchaseOrderRequest,Integer customerId);
	
	/**
	 * API 同步-V订单接口
	 * @param syncVarianceOrderRequest
	 * @param customerId
	 * @return
	 */
	public Map<String,Object> syncVarianceOrder(SyncVarianceOrderRequest syncVarianceOrderRequest,Integer customerId);
	
	/**
	 * API 公用验证参数方法
	 * @author hzhang1
	 */
	public Map<String,Object> checkOrderInfoRequest(Integer warehouseId , String omsOrderSn , List<OrderGoodsReqDomain> orderGoodsList);
	
	/**
	 * 根据app_key得到customer_id
	 * @param app_key
	 * @return
	 */
	public Integer getcustomerIdByAppKey(String app_key);

	/**
	 * API 发货单创建接口
	 * @param orderInfoRequest
	 * @param customerId
	 * @return
	 */
	public HashMap<String, Object> syncSaleOrder(
			SyncSaleOrderRequest orderInfoRequest, Integer customerId);

	/**
	 * API 销售退货单创建接口
	 * @param orderInfoRequest
	 * @param customerId
	 * @return
	 */
	public HashMap<String, Object> syncRmaOrder(
			SyncRmaOrderRequest orderInfoRequest, Integer customerId);

	/**
	 * API 取消订单接口
	 * @param cancelOrderRequest
	 * @param customerId
	 * @return
	 */
	public HashMap<String, Object> cancelOrder(
			CancelOrderRequest cancelOrderRequest, Integer customerId);
	/**
	 * API 加工单取消
	 * @param orderPrepack
	 * @return
	 */
	public Map<String, Object> cancelOrderPrepack(
			OrderPrepack orderPrepack);
	
	
	/**
	 * API根据omOrderSn & customerId去查找采购订单
	 * @param omsOrderSn
	 * @param customerId
	 * @return
	 */
	public Map<String, Object> getPurchaseOrder(GetPurchaseOrderRequest getPurchaseOrderRequest,Integer customerId);
	
	
	/**
	 * API根据omOrderSn & customerId去查找-V订单
	 * @param omsOrderSn
	 * @param customerId
	 * @return
	 */
	public Map<String, Object> getVarianceOrder(GetVarianceOrderRequest getVarianceOrderRequest,Integer customerId);
	
	/**
	 * API 调整供价
	 * @param adjustPriceRequest
	 * @param customerId
	 * @return
	 */
	public Map<String, Object> adjustprice(
			AdjustPriceRequest adjustPriceRequest, Integer customerId);
	
	/**
	 * API根据getRmaOrderRequest & customerId去查找销售退货订单
	 * @param getRmaOrderRequest
	 * @param customerId
	 * @return
	 */
	public Map<String, Object> getRmaOrder(
			GetRmaOrderRequest getRmaOrderRequest, Integer customerId);

	/**
	 * API根据getSaleOrderRequest & customerId去查找发货订单
	 * @param getSaleOrderRequest
	 * @param customerId
	 * @return
	 */
	public Map<String, Object> getSaleOrder(
			GetSaleOrderRequest getSaleOrderRequest, Integer customerId);

	/**
	 * API根据getOrderListRequest & customerId去查询订单列表
	 * @param getOrderListRequest
	 * @param customerId
	 * @return
	 */
	public Map<String, Object> getOrderList(
			GetOrderListRequest getOrderListRequest, Integer customerId);
	
	
	/**
	 * API根据getOrderShipmentListRequest & customerID查询订单列表
	 * 
	 */
	public Map<String, Object> getOrderShipmentList(GetOrderShipmentListRequest getOrderShipmentListRequest,Integer customerId);
	
	
	public Map<String, Object> terminalOrder(
			TerminalOrderRequest terminalOrderRequest,Integer customerId);
	
	/**
	 * 推送任务
	 * @author xhchen
	 * @param request
	 * @param customerId
	 * @return
	 */
	public HashMap<String, Object> syncPrepackTask(SyncOrderPrepackRequest request,Integer customerId);
	
	public List<OrderPrepack> getPrepackList(GetOrderPrepacksRequest request,Integer customerId);
}
