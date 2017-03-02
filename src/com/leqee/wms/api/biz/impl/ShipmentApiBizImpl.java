package com.leqee.wms.api.biz.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.api.biz.ShipmentApiBiz;
import com.leqee.wms.dao.ShipmentDao;
import com.leqee.wms.dao.ShipmentDetailDao;
import com.leqee.wms.entity.Shipment;
import com.leqee.wms.util.WorkerUtil;

/**
 * 发货单API业务类
 * @author qyyao
 * @date 2016-3-1
 * @version 1.0
 */
@Service
public class ShipmentApiBizImpl implements ShipmentApiBiz {

	@Autowired
	ShipmentDao shipmentDao;
	@Autowired
	ShipmentDetailDao shipmentDetailDao;
	
	@Override
	public List<Shipment> selectByOrderIdWithDetail(Integer orderId) {
		List<Shipment> shipmentList = shipmentDao.selectByOrderId(orderId);
		
		if(!WorkerUtil.isNullOrEmpty(shipmentList)){
			for (Shipment shipment : shipmentList) {
				shipment.setShipmentDetailList(shipmentDetailDao.selectByShipmentId(shipment.getShipment_id()));
			}
		}
		
		return shipmentList;
	}

}
