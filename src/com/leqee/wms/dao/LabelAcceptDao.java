package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.LabelAccept;

/**
 * 标签接收
 * @author qyyao
 * @date 2016-5-17
 * @version 1.0
 */
public interface LabelAcceptDao {

	/**
	 * 根据orderId获取LocationKwBarcode不为空的订单
	 * @param orderId
	 * @return
	 */
	List<LabelAccept> selectByOrderIdWithLocationKwBarcode(Integer orderId);
	
	Map selectLabelAcceptByLocationBarcode(Map map);

	int updateLabelAcceptForTaskId(@Param("taskId") Integer taskId,@Param("inventoryLocationId") Integer inventoryLocationId);
}
