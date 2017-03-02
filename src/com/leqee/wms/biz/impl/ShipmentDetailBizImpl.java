package com.leqee.wms.biz.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.ShipmentDetailBiz;
import com.leqee.wms.dao.ShipmentDetailDao;
import com.leqee.wms.entity.ShipmentDetail;
import com.leqee.wms.response.Response;
import com.leqee.wms.response.ResponseFactory;
import com.leqee.wms.util.WorkerUtil;

@Service
public class ShipmentDetailBizImpl implements ShipmentDetailBiz {
	Logger logger = Logger.getLogger(ShipmentDetailBizImpl.class);
	
	@Autowired
	ShipmentDetailDao shipmentDetailDao;

	@Override
	public Response createShipmentDetail(ShipmentDetail shipmentDetail) {
		// 初始化
		Response response = new Response(Response.OK, "创建ShipmentDetail成功!");
		try {
			shipmentDetailDao.insert(shipmentDetail);
			if(shipmentDetail.getShipment_detail_id() <= 0){
				response = ResponseFactory.createErrorResponse("插入ShipmentDetail表数据失败！");
			}
		} catch (Exception e) {
			logger.error("创建ShipmentDetail时发生异常，异常信息：" + e.getMessage(),e);
			throw new RuntimeException("创建ShipmentDetail时发生异常，异常信息：" + e.getMessage());
		}
		return response;
	}

	@Override
	public int updateShipmentDetail(ShipmentDetail shipmentDetail) {
		return shipmentDetailDao.update(shipmentDetail);
	}

	@Override
	public List<ShipmentDetail> findByShipmentIdAndOrderGoodsId(Integer shipmentId,
			Integer orderGoodsId) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("shipmentId", shipmentId);
		paramsMap.put("orderGoodsId", orderGoodsId);
		return shipmentDetailDao.selectByShipmentIdAndOrderGoodsId(paramsMap);
	}

	@Override
	public boolean isSerialNumberBinded(String serialNumber) {
		List<ShipmentDetail> shipmentDetails = shipmentDetailDao.selectBySerialNumberNotDelivered(serialNumber);
		if(!WorkerUtil.isNullOrEmpty(shipmentDetails) && shipmentDetails.size() > 0){
			return true;
		}
		return false;
	}

	@Override
	public List<ShipmentDetail> findByOrderId(Integer orderId) {
		return shipmentDetailDao.selectByOrderId(orderId);
	}


}
