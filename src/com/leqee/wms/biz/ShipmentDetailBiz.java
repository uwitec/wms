package com.leqee.wms.biz;

import java.util.List;

import com.leqee.wms.entity.ShipmentDetail;
import com.leqee.wms.response.Response;

public interface ShipmentDetailBiz {

	/**
	 * 创建
	 * @param shipmentDetail a ShipmentDetail
	 * @return response a Response
	 * */
	public Response createShipmentDetail(ShipmentDetail shipmentDetail);
	
	/**
	 * 更新
	 * @param shipmentDetail a ShipmentDetail
	 * @return effectRows an int: 受影响行数
	 * */
	public int updateShipmentDetail(ShipmentDetail shipmentDetail);

	/**
	 * 根据shipmentId和orderGoodsId进行查找
	 * @param shipmentId an Integer
	 * @param orderGoodsId an Integer
	 * @return shipmentDetails a List<ShipmentDetail>
	 * */
	public List<ShipmentDetail> findByShipmentIdAndOrderGoodsId(Integer shipmentId,
			Integer orderGoodsId);

	/**
	 * 判断是否已经被绑定到其他面单上面
	 * @param serialNumber
	 * @return 
	 * */
	public boolean isSerialNumberBinded(String serialNumber);

	/**
	 * 根据orderId进行查找
	 * @param orderId an Integer
	 * @return shipmentDetails a List<ShipmentDetail>
	 * */
	public List<ShipmentDetail> findByOrderId(Integer orderId);

	
}
