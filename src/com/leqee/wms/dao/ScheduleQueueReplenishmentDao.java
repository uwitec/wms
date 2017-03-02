package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ScheduleQueueReplenishment;

public interface ScheduleQueueReplenishmentDao {

	List<Integer> getInitJobByRequest(
			 @Param("physicalWarehouseId")Integer physicalWarehouseId,  @Param("customerId")Integer customerId,
			 @Param("boxPiece")String boxPiece,
			 @Param("taskLevel")Integer taskLevel, @Param("productId")Integer productId);

	void insertJobByRequest(ScheduleQueueReplenishment scheduleQueueReplenishment);

	List<Map> selectManulReplenishByPage(Map<String, Object> searchMap);

	List<ScheduleQueueReplenishment> selectManulPieceReplenish(Integer quantity);


}
