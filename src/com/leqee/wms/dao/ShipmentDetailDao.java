package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.ShipmentDetail;

public interface ShipmentDetailDao {
   
	/**
	 * 通过shipmentId查询shipmentDetail
	 * @param shipmentId
	 * @return
	 */
	public List<ShipmentDetail> selectByShipmentId(Integer shipmentId);

	/**
	 * 插入
	 * */
	public int insert(ShipmentDetail shipmentDetail);

	/**
	 * 更新
	 * */
	public int update(ShipmentDetail shipmentDetail);

	/**
	 * 通过shipmentId和orderGoodsId查询shipmentDetail
	 * @param shipmentId
	 * @param orderGoodsId
	 * @return shipmentDetails
	 */
	public List<ShipmentDetail> selectByShipmentIdAndOrderGoodsId(
			Map<String, Object> paramsMap);
	
	/**
	 * 根据串号查找已经绑定Shipment但还未出库的记录
	 * @param serialNumber
	 * @return shipmentDetail
	 */
	public List<ShipmentDetail> selectBySerialNumberNotDelivered(String serialNumber);

	/**
	 * 通过orderId查询shipmentDetail
	 * @param orderId
	 * @return shipmentDetails
	 */
	public List<ShipmentDetail> selectByOrderId(Integer orderId);

	public void batchInsert(@Param("shipmentDetailList")List<ShipmentDetail> shipmentDetailList);

	public void batchUpdateGoodsNumber(@Param("shipmentList")List<Integer> shipmentList,@Param("orderGoodsIdList") List<Integer> orderGoodsIdList);

	public void deleteShipmentDetail(Integer shipment_id);

	public void deleteBatchShipmentDetail(@Param("shipmentIdList")List<Integer> shipmentIds);

	public Integer checkDetailNumberByOrderGoodsId(Integer orderGoodsId);

	/**
	 * 批量绑定商品包裹信息（超级复核）
	 * @param shipmentDetailList
	 */
	public void batchCreateShipmentDetail(List<ShipmentDetail> shipmentDetailList);
	
	public ShipmentDetail selectShipmentDetailForPackBox(@Param("shipment_id")Integer shipment_id);
	
	public int updateShipmentDetailForPackBox(@Param("shipment_detail_id")Integer shipment_detail_id,@Param("goods_name")String goods_name,@Param("product_id")Integer product_id);

}
