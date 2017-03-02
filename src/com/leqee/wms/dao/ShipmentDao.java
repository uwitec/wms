package com.leqee.wms.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.Pallet;
import com.leqee.wms.entity.PalletShipmentMapping;
import com.leqee.wms.entity.Shipment;

public interface ShipmentDao {
   
	/**
	 * 通过orderId查询shipment
	 * @param orderId
	 * @return
	 */
	public List<Shipment> selectByOrderId(Integer orderId);

	public Shipment getShipmentByTrackingNumber(String trackingNumber);

	public void setShipmentPackageWeight(BigDecimal shipping_wms_weight,
			String tracking_number, Integer product_id,String username);

	public Map<String, Object> getOrderStatusByTrackingNumber(String tracking_number);
	
	public int getOrderPhysicalWarehouseIdByTrackingNumber(String tracking_number);
	
	public void updatePalletPhysicalWarehouseId(Map map);

	public int getSameMTCodeNum(ArrayList<String> mTCodelist);

	public void insertMTcode(Pallet pallet);

	public Pallet selectPalletByPalletSn(String pallet_sn);

	public List<Map> getBindListByByPalletSn(String pallet_sn);

	public Map getOrderStautsByTrackingNo(String tracking_no);

	public List<Map> getPalletShipmentMappingByTrackingNo(String tracking_no);

	public Integer getBindNumByPalletNo(String pallet_sn);

	public Map getShippingInfoByTrackingNo(String tracking_no);

	public void insertpalletShipmentMapping(
			PalletShipmentMapping palletShipmentMapping);

	public void updatePalletShippingIdByPalletNo(String string, String pallet_sn);

	public Integer getShippingIdByTrackingNo(String tracking_no);

	public Integer getShippingIdByPalletSn(String pallet_sn);

	public Integer getShipmentIdBytrackingNo(String tracking_no);

	public Map getShippingInfoByTrackingNoForUnBind(String tracking_no);
	
	public List selectBindShipmentGoodsInfoByOrderId(Integer orderId);

	public Map getShipmentStatusForUnbindByTrackingNo(String tracking_no);

	public void updatePalletShipMappingForUnbind(String username, int intValue);
	
	List<Map> selectSaleOrderWarehouseShippingInfo(Integer orderId);

	List<Map<String,Object>> selectAllTrackingNumberByOrderId(Integer orderId);
	
	List<Map<String,Object>> selectAllTrackingNumberByOrderIdV2(Map map);

	Map selectShipOrderPaperType(Integer orderId);
	
	public void updateTrackingNumber(Integer shipmentId, String trackingNumber, String mark);

	public int createShipment(Shipment s);

	public Integer updateShipmentStatusForShip(Integer shipment_id, String username);

	public Map getShipmentInfoById(Integer shipmentId);

	public int updatePackBoxStatus(@Param("shipmentId")Integer shipmentId, @Param("productId")String productId,
			@Param("is_out")char is_out, @Param("localTime")Date localTime, @Param("localUser")String localUser);
	
	public int updatePackBoxStatusV2(@Param("shipmentId")Integer shipmentId, 
			@Param("is_out")char is_out, @Param("actionUser")String actionUser);
	
	public Integer selectShipmentNotShipByShipmentId(Integer shipment_id);

	public Map<String, String> selectLastShipmentBindInfo(Integer orderId);

	public List<String> getWrongStatuslListForPalletShip(
			String pallet_no);

	public Integer updatePalletStatusForShip(String pallet_sn,String username);
	
	public List<String> getTrackingNoByPalletNo(String pallet_no);

	public List<Map> queryByPalletSn(String pallet_sn);

	public int findPalletSnInPallet(String pallet_sn);

	public List<Map> queryByTrackingNo(String tracking_no);

	public int findShipmentByTrackingNo(String tracking_no);

	public void updateSfShipment(Integer shipmentId, String origincode,
			String destcode);

	public Map selectReceiveInfo(Integer shipmentId);

	public Map<String, Object> getOrderInfoByTn(String trackingNumber);

	public List<Map<String, Object>> loadBindGoodsByTn(String trackingNumber);

	public Map<String, Object> loadGoodsForDeleteByOrderId(Integer orderId);
	
	public List<Map<String, Object>> loadBatchInfosForDeleteByBatchPickSn(String batchPickSn);

	public void deleteShipment(Integer shipmentId);

	public Map<String, Object> queryOrderByTrackingNo(@Param("trackingNumber")String no);

	public Map<String,Object> getTnsNotWeight(String tracking_number);

	public int queryCancelOrderByTrackingNo(@Param("trackingNumber") String tracking_no);

	public List<Map<String, Object>> getCancelOrderByPalletSn(@Param("pallet_sn")String pallet_sn);
	
	// 批量称重
	public List<Map> selectBatchPickIsRechecked(String batchPickSn); //批量称重(检查波次单号)
	
	public List<Map> selectBatchPickCancelTrackingNumber(String batchPickSn); //批量称重(显示取消订单运单号)
	
	public Map checkBatchTrackingNumber(Map map); //批量称重(检查快递单号是否在波次单内)
	
	public int updateBatchWeight(Map map); //批量称重(批量更新重量)
	
	public List<Map> selectBatchWeightIsOver(Integer orderId); //批量称重(检查是否所有包裹都称重)
	
	public int updateBatchOrderStatus(Map map); //批量称重(批量更新订单状态)
	
	public int updateBatchOrderStatusWhenPicking(Map map);
	
	public int updateBatchOrderAction(Map map); //批量称重(批量插入日志信息)
	
	/**
	 * 根据orderid和status获取满足条件的shipment
	 * @param paramMap
	 * @return
	 */
	public List<Shipment> selectByOrderIdAndStatus(Map<String, Object> paramMap);


	public List<Map<String, Object>> getAllOrderShipmentInfoByBatchOrderSn(String batchPickSn);


	public void insertMTcodeNew(Pallet pallet);

	public void insertpalletShipmentMappingList(
			@Param("list")List<PalletShipmentMapping> psmList);


	public int getShipmetsBinded(@Param("list")List<Integer> shipmentIdList);

	public String getMtcodeByshipmentIdList(@Param("list")List<Integer> shipmentIdList);

	public Integer getMaxShipmentCount(String batchPickSn);

	public List<Map<String, Object>> getShipmentCounts(String batchPickSn);

	public Map<String, Object> getPackShipmentCount(String batchPickSn);

	public int createShipmentTrackingNumber(Shipment s);

	public List<Map<String, Object>> findShipmentOrderGoodsInfoList(
			Integer batchPickId, Integer maxShipmentCount, Integer productId);

	public List<Map<String, Object>> findBatchShipmentIdByBatchOrderId(Integer batchPickId,Integer maxShipmentCount);

	public List<Map<String, Integer>> getShipmentOrderGoodsMap(
			@Param("productId")String productId, @Param("maxShipmentCount")Integer maxShipmentCount, @Param("orderIdList")List<Integer> orderIdList);

	public int batchUpdatePackBoxStatus(@Param("shipmentIdList")List<Integer> shipmentIdList, @Param("productId")String productId,
			@Param("is_out")char is_out, @Param("localTime")Date localTime, @Param("localUser")String localUser);

	public int getShipmetsBindedByBPD(@Param("batch_pick_id")int batch_pick_id);
	
	/**
	 * @author xhchen
	 * 通过shipment中的耗材id查询product表中对应的sku_code
	 * @return
	 */
	public String getPackBoxSkuCodeByPid(Integer pid);

	public String getBatchPickTypeByTrackingNo(@Param("tracking_no")String tracking_no);

	public List<Integer> selectAllShipmentIdsByOrderIds(@Param("orderList")List<Integer> orders);

	public void deleteBatchShipment(@Param("shipmentIdList")List<Integer> shipmentIds);

	public List<Map> getBindListByByPalletSnV2(String pallet_sn);

	public Map<String, Object> selectRecheckNumMap(Integer orderId);

	public int batchInsertShipmentDetail(Integer orderId);

	public int batchDeleteByOrderId(List<Integer> updatelist);

	public String selectMtCodeByTrackingNo(@Param("tracking_no")String no);
	
	public int batchUpdateShipmentStatus(Integer orderId);

	public Integer getPackNumByBatchPickId(Integer batchPickId);

	public Shipment selectShipmentByTrackingNoForUpdate(String tracking_no);

	public List<Map<String, Object>> getShipmentByshipmentIdList(@Param("shipmentIdList")List<Integer> shipmentIdList);

	public Integer selectShipmentByTrackingNumber(@Param("trackingNumber")String trackingNumber);
}
