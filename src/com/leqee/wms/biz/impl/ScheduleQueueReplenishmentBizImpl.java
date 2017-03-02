package com.leqee.wms.biz.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.leqee.wms.biz.ScheduleQueueReplenishmentBiz;
import com.leqee.wms.dao.ScheduleQueueReplenishmentDao;
import com.leqee.wms.dao.TaobaoShopConfDao;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ScheduleQueueReplenishment;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.WorkerUtil;

@Service
public class ScheduleQueueReplenishmentBizImpl implements ScheduleQueueReplenishmentBiz {
	
	private Logger logger = Logger.getLogger(ScheduleQueueReplenishmentBizImpl.class);
	
	@Autowired
	ScheduleQueueReplenishmentDao scheduleQueueReplenishmentDao;
	

	@Override
	public Map<String,Object> batchInsertScheduleQueueReplenishment(
			Integer physicalWarehouseId, List<Integer> customerIdList,
			String boxPiece, Integer taskLevel, Integer productId,String localUser) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Integer insertCount = 0 ;
		for (Integer customerId : customerIdList) {
			List<Integer> list =
				scheduleQueueReplenishmentDao.getInitJobByRequest(physicalWarehouseId,customerId,boxPiece,taskLevel,productId);
			if(!WorkerUtil.isNullOrEmpty(list) && list.contains(0)){
				resultMap.put("result", Response.FAILURE);
				resultMap.put("note", "任务中存在未执行“同级或更高级”任务，不需再次创建！");
				return resultMap;
			}else if(!WorkerUtil.isNullOrEmpty(list)){
				continue;
			}else{
				try{
					ScheduleQueueReplenishment scheduleQueueReplenishment = new ScheduleQueueReplenishment();
					scheduleQueueReplenishment.setPhysical_warehouse_id(physicalWarehouseId);
					scheduleQueueReplenishment.setCustomer_id(customerId);
					scheduleQueueReplenishment.setBox_piece(boxPiece);
					scheduleQueueReplenishment.setTask_level(taskLevel);
					scheduleQueueReplenishment.setProduct_id(productId);
					scheduleQueueReplenishment.setQueue_status("INIT");
					scheduleQueueReplenishment.setCreated_user(localUser);
					scheduleQueueReplenishment.setCreated_time(new Date());
					scheduleQueueReplenishment.setLast_updated_time(new Date());
					scheduleQueueReplenishmentDao.insertJobByRequest(scheduleQueueReplenishment);
				}catch(RuntimeException e){
					logger.info("插入手动补货任务时出错！"+e.getMessage());
					resultMap.put("result", Response.FAILURE);
					resultMap.put("note", "插入手动补货任务时出错！");
					return resultMap;
				}
				insertCount++;
			}
		}
		if(insertCount>0){
			resultMap.put("result",Response.SUCCESS);
			resultMap.put("note", "成功设置"+insertCount+"条任务");
		}else{
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "任务中存在未执行“同级或更高级”任务，不需再次创建！");
		}
		return resultMap;
	}

	@Override
	public List<Map> selectManulReplenishByPage(Map<String, Object> searchMap) {
		List<Map> list = scheduleQueueReplenishmentDao.selectManulReplenishByPage(searchMap);
		return list;
	}

	@Override
	public List<ScheduleQueueReplenishment> selectManulPieceReplenish(
			Integer quantity) {
		return scheduleQueueReplenishmentDao.selectManulPieceReplenish(quantity);
	}

	

}
