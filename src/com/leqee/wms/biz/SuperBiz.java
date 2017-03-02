package com.leqee.wms.biz;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;


@WebService
public interface SuperBiz {

	//（超级复核） 加载商品
	public Map<String, Object> loadGoodsForOrder(Integer orderId);
	//（超级复核）绑定耗材
	public Map<String,Object> bindConsume(Integer shipmentId, String barcode,Integer orderId);
	//（超级复核）打印
	public Map selectPrintInfo(Integer orderId,String trackingNumber);
	// （超级复核）根据订单号获取发货单张数
	public Integer getOrderDispatchNum(Integer orderId);
	// 线下维护面单号段
	public Integer addFromToTN(Integer parseInt, String shipping_code,
			String from_tracking_number, String to_tracking_number);
	//导出线下回传号段
	public List<Map> exportTNsForBack(Integer physicalWarehouseId,String shippingCode);

}