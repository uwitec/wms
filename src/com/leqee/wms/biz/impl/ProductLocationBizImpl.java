package com.leqee.wms.biz.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.BatchTaskBiz;
import com.leqee.wms.biz.ProductLocationBiz;
import com.leqee.wms.dao.BatchTaskDao;
import com.leqee.wms.dao.OrderPrepackDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.ProductLocationDetailDao;
import com.leqee.wms.dao.TaskDao;
import com.leqee.wms.dao.UserActionTaskDao;
import com.leqee.wms.entity.OrderPrepack;
import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.entity.Task;
import com.leqee.wms.entity.UserActionTask;
import com.leqee.wms.util.WorkerUtil;

@Service
public class ProductLocationBizImpl implements ProductLocationBiz {
	private Logger logger = Logger.getLogger(ProductLocationBizImpl.class);
	
    @Autowired
    ProductLocationDao productLocationDao;
    
    @Autowired
    ProductLocationDetailDao productLocationDetailDao;
    
    @Autowired
    TaskDao taskDao;
    @Autowired
    OrderPrepackDao orderPrepackDao;
    
    //-gt订单取消接口回滚
    @Override
	public void cancelOrderProductLocationReservation(Integer order_id){
		logger.info("cancelOrderProductLocationReservation order_id:"+order_id);
		List<Map<String, Object>>cancelList = taskDao.selectCancelProductLocationByOrderId(order_id);
		if(WorkerUtil.isNullOrEmpty(cancelList)){
			logger.info("没有需要可以调整的取消订单记录"+order_id);
		}else{
			for (Map<String, Object> map : cancelList) {
				ProductLocation productLocation = productLocationDao.getFromProduLocations(Integer.parseInt(map.get("task_id").toString()), Integer.parseInt(map.get("from_pl_id").toString()));
				if(WorkerUtil.isNullOrEmpty(productLocation)){
					logger.info("找不到对应的productLocation表记录"+order_id);
					throw new RuntimeException("找不到对应的productLocation表记录"+order_id);
				}else{
					int col = productLocationDao.updateProductLocationForAddAvailable(Integer.parseInt(map.get("quantity").toString()), productLocation.getPl_id());
					if(col<=0){
						throw new RuntimeException("更新库存记录失败"+order_id);
					}
				}
			}
		}
	}
	
	//加工单取消接口回滚
    @Override
	public void cancelOrderPrepackageProductLocationReservation(Integer order_id,Integer prepackage_product_id,String type){
    	List<Map<String, Object>> prePackageList = orderPrepackDao.selectPrePackageListV1(order_id);
    	List<Task> taskList = new ArrayList<Task>();
    	if(WorkerUtil.isNullOrEmpty(prePackageList)){
    		logger.info("没有需要可以调整的取消预加工订单记录"+order_id);
		}else {
			if(OrderPrepack.ORDER_PREPACK_TYPE.equalsIgnoreCase(type)){
				 taskList = orderPrepackDao.getTaskIdByOrderId(order_id,prepackage_product_id);
				 if(WorkerUtil.isNullOrEmpty(taskList)){
						logger.info("找不到对应的taskList表记录"+order_id);
						throw new RuntimeException("找不到对应的taskList表记录"+order_id);
					}
				 for (Task task : taskList) {
						ProductLocation productLocation = productLocationDao.getFromProduLocations(task.getTask_id(),task.getFrom_pl_id());
						if(WorkerUtil.isNullOrEmpty(productLocation)){
							logger.info("找不到对应的productLocation表记录"+order_id);
							throw new RuntimeException("找不到对应的productLocation表记录"+order_id);
						}else{
							int col = productLocationDao.updateProductLocationForAddAvailable(task.getQuantity(), productLocation.getPl_id());
							if(col<=0){
								throw new RuntimeException("更新库存记录失败"+order_id);
							}else {
								logger.info("取消加工单释放库存成功");
								}
							}
						}
			}else {
				for (Map<String, Object> map2 : prePackageList) {
					taskList = orderPrepackDao.getTaskIdByOrderId(order_id,Integer.valueOf(map2.get("component_product_id").toString()));
					if(WorkerUtil.isNullOrEmpty(taskList)){
						logger.info("找不到对应的taskList表记录"+order_id);
						throw new RuntimeException("找不到对应的taskList表记录"+order_id);
					}
					for (Task task : taskList) {
						ProductLocation productLocation = productLocationDao.getFromProduLocations(task.getTask_id(),task.getFrom_pl_id());
						if(WorkerUtil.isNullOrEmpty(productLocation)){
							logger.info("找不到对应的productLocation表记录"+order_id);
							throw new RuntimeException("找不到对应的productLocation表记录"+order_id);
						}else{
							int col = productLocationDao.updateProductLocationForAddAvailable(task.getQuantity(), productLocation.getPl_id());
							if(col<=0){
								throw new RuntimeException("更新库存记录失败"+order_id);
							}else {
								logger.info("取消加工单释放库存成功");
								}
							}
						}
				
				}
			}
	
		}
		

    }
}