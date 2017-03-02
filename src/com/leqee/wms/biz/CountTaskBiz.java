package com.leqee.wms.biz;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.entity.ScheduleQueueCount;
import com.leqee.wms.entity.TaskCount;

public interface CountTaskBiz {

	Map<String, Object> doStartJob(ScheduleQueueCount sqc);

	Map<String, Object> searchCountSnForPrint(String countSn,Integer physicalWarehouseId);

	List<Map<String, Object>> createBatchTaskCount(String countSn,Integer taskNumPerPage);

	Map<String, Object> doCancelJob(ScheduleQueueCount sqc);

	HashMap<String, Object> bindUser(Integer bind_user_id, String username,
			String batch_task_sn);

	void updateList(List<TaskCount> list, int mark, String user_name);

	void doCreateProductLocationChange(List<Integer> productIdList, int physical_warehouse_id,
			String user_name, int customer_id, List<ProductLocation> addList,
			List<ProductLocation> minusList, String task_improve_sn, int warehouse_id);

	Map<String, Object> doTaskCountByHand(int physical_warehouse_id,
			Integer warehouse_id, String barcode, String location_barcode, Integer customer_id,
			int num_real, String count_sn, String userName, String status);
	
}
