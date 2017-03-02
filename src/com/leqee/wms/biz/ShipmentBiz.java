package com.leqee.wms.biz;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import com.leqee.wms.entity.Shipment;

@WebService
public interface ShipmentBiz {
     
	
	public Shipment getShipmentByTrackingNumber(String trackingNumber);
	public String getOrderStatusByTrackingNumber(String trackingNumber);

	void setShipmentPackageWeight(BigDecimal shipping_wms_weight,
			String tracking_number,String barcode);
	
	//散单复核
	public Map<String, Object> checkOrderForRecheck(int parseInt);
	public Map<String, Object> loadGoodsForOrder(Integer orderId);
	public Map<String,Object> bindConsume(Integer shipmentId, String barcode,Integer orderId);
	//散单 追加面单
	public Map<String, Object> addTrackingNumber(Integer orderId);
	
	//mao
	List<Map> getPalletShipmentList(Map<String, Object> map); 
	
	public HashMap<String, Object> getUnbindShipmentInfoByTrackingNo2(
			String tracking_no);
	public Map selectPrintInfo(Integer orderId,String trackingNumber);
	//问题单处理-删除面单
	public Map<String, Object> loadGoodsForDeleteByTn(String trackingNumber);
	public Map<String, Object> loadGoodsForDeleteByOrderId(Integer orderId);
	public Map<String, Object> loadBatchInfosForDeleteByBatchPickSn(String batchPickSn);
	public Map<String, Object> cleanOrderTns(Integer orderId);
	
	public Map<String, Object> queryOrderByTrackingNo(String no);
	public List<Shipment> selectByOrderIdAndStatus(Integer orderId,
			String status);

	//批量单 复核 加载商品
	public Map<String, Object> getAllOrderShipmentInfoByBatchOrderSn(String batchPickSn);
	public Map<String, Object> loadOrderGoodsForBatchPick(String batchPickSn);
	public Map<String, Object> applyShipmentTn(Integer orderId, String applyType);

	public Map<String, Object> queryAllShipment(String batch_pick_sn);


	// 批量称重
	public Map getBatchPickIsRechecked(String batchPickSn); //批量称重(检查波次单号)
	public Map checkBatchTrackingNumber(String batchPickSn,String trackingNumber); //批量称重(检查快递单号)
	public Map updateBatchWeight(String batchPickSn,String trackingNumber,String weight,String actionUser); //批量称重(批量更新同一波次包裹重量)
	public void batchApplyShipmentTn(List<Map<String, Object>> ztoShippingAppOrders, String string);
	public List<Map<String, Object>> findShipmentOrderGoodsInfoList(
			Integer batchPickId, Integer maxShipmentCount, Integer product_id);
	public Map<String, Object> batchBindConsume(Integer batchPickId,Integer maxShipmentCount, String barcode);
	public Map batchSelectPrintInfo(String batchPickId, Integer startNum, Integer endNum);
	public Map<String, Object> cleanBatchPickOrderTns(String batchPickSn);
	//杂单波次转回批量波次判断
	public void checkBatchPickForRecheck(String batchPickSn);
	//批量波次走对应流程 for recheck
	public void checkBatchPickFlowStatus(String batchPickSn,String flowStatus);
	//批量波次走大后门，check & update &insert
	public Integer loadTrickBatchPickSn(String batchPickSn);
	//补打印快递面单走大后门，check & insert 
	public Integer reLoadTrickBatchPickSn(String batchPickSn);
	//首次申请面单for batchPick applyType=BATCH
	public void batchApplyShipmentTnForFirstShipment(
			List<Map<String, Object>> ztoShippingAppOrders, String string);
	//首次申请面单 for batchPick applyType=SINGLE
	public Map<String, Object> applyShipmentTnForFirstShipment(
			Integer otherOrderId);
	public void updatePalletPrintCount(List<String> codeList);
	//取消订单自匹配（首个）面单号
	public void applyFirstShipmentForCancelOrder(Integer batchPickId);
	//取消订单自匹配面单号（批量复核）
	public void applyShipmentTnForCancelOrder(Integer orderId);
	//补打印快递面单（根据波次和起止页数）
	public Map<String, Object> reLoadBatchPickSnForRePrint(String batchPickSn);
	//批量波次查询面单序号 
	public Map<String, Object> searchTnsSequence(String batchPickSn);
	//根据配置config与波次中商品属性判断操作性
	public void checkRecheckBatchForPickSn(String batchPickSn);
	//查询订单状态
	public String selectOrderStatusByOrderOrTrackingNumber(Integer orderId, String trackingNumber);

}