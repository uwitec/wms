package com.leqee.wms.biz;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.leqee.wms.entity.BatchPick;
import com.leqee.wms.response.Response;

public interface BatchPickBiz {

	/**
	 * @author Jarvis
	 * @CreatedDate 2016.02.22
	 * 
	 * @Description 绑定波次单
	 * 
	 * @param customerId an Integer: 货主ID
	 * @param warehouseId an Integer: 仓库ID
	 * @return response a Response: 返回消息
	 * 
	 * */
	Response batchPick(Integer customerId, Integer warehouseId, String actionUser);


	BatchPick selectBatchPickByBatchPickSn(String batch_pick_sn);


	void updatebatchPickForBindEmployee(Integer id, Integer batch_pick_id);


	HashMap<String, Object> bindBatchPickforUser(int bind_user_id,String username,
			String batch_pick_sn);


	public HashMap<String, Object> doSearchBatchPick2(HttpServletRequest req,
			HashMap<String, Object> model);
	
	public HashMap<String, Object> checkShopCollection(HttpServletRequest req);
	
	public HashMap<String, Object> checkWarehouseLoad(HttpServletRequest req,
			HashMap<String, Object> model);
	
	public HashMap<String, Object> createBatchPickTask(HttpServletRequest req,
			HashMap<String, Object> model);
	
	public void updateOrderInfoStatus(String batch_pick_sn);


	List<Integer> getToApplyFirstShipmentBatchPickIdList(Integer physicalWarehouseId,
			Integer cId, Date startDateTime, Date endDateTime);


	void updateFlowStatus(Integer batchPickId, String toFlowStatus);

}