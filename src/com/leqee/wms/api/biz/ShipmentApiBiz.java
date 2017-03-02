package com.leqee.wms.api.biz;

import java.util.List;

import com.leqee.wms.entity.Shipment;

/**
 * 发货单业务接口
 * @author qyyao
 * @date 2016-3-1
 * @version 1.0
 */
public interface ShipmentApiBiz {

	List<Shipment> selectByOrderIdWithDetail(Integer orderId);

}
