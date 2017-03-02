package com.leqee.wms.biz;

import java.util.List;
import java.util.Map;

import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ScheduleQueueReplenishment;

public interface ScheduleQueueReplenishmentBiz {

	Map<String,Object> batchInsertScheduleQueueReplenishment(Integer physical_warehouse_id,
			List<Integer> customerIdList, String boxPiece, Integer taskLevel,Integer productId,String localUser);

	List<Map> selectManulReplenishByPage(Map<String, Object> searchMap);

	List<ScheduleQueueReplenishment> selectManulPieceReplenish(Integer quantity);

}
