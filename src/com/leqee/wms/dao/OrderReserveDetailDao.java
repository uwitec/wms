package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.OrderReserveDetail;

public interface OrderReserveDetailDao {

	List<OrderReserveDetail> selectReserveDetailByOrderId(Integer orderId);

	void updateReserveDetailInfo(Integer order_goods_id, int req, String status);

	List<OrderReserveDetail> selectOrderReserveDetailByOrderId(Integer orderId);

//	void updateorderReserveDetail(Integer order_goods_id, String string);

	Integer updateReserveStatusByOrderid(Integer orderId,String stauts);
	
	void updateReserveDetailInfoStatus(Integer order_goods_id, String status);

	Integer deleteOrderReserveDetailByOrderId(@Param("order_id")Integer order_id);
	
	Integer insert(OrderReserveDetail orderReserveDetail);
	
	List<OrderReserveDetail> selectOrderReserveDetail(Map<String, Object> map);

	void batchInsert(@Param("list")List<OrderReserveDetail> ordInsertList);

}
