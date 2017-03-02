package com.leqee.wms.biz.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.biz.CountTaskBiz;
import com.leqee.wms.dao.BatchTaskCountDao;
import com.leqee.wms.dao.CountTaskDao;
import com.leqee.wms.dao.LocationDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.ProductLocationDetailDao;
import com.leqee.wms.dao.ScheduleQueueCountDao;
import com.leqee.wms.dao.TaskImproveDao;
import com.leqee.wms.entity.BatchTaskCount;
import com.leqee.wms.entity.Location;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.entity.ProductLocationDetail;
import com.leqee.wms.entity.ScheduleQueueCount;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.TaskCount;
import com.leqee.wms.entity.TaskImprove;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.LocationBarcodeTools;
import com.leqee.wms.util.SequenceUtil;
import com.leqee.wms.util.WorkerUtil;

@Service
public class CountTaskBizImpl implements CountTaskBiz {

	private Logger logger = Logger.getLogger(CountTaskBizImpl.class);
	
	@Autowired
	ScheduleQueueCountDao scheduleQueueCountDao;
	@Autowired
	CountTaskDao countTaskDao;
	@Autowired
	BatchTaskCountDao batchTaskCountDao;
	@Autowired
	ProductLocationDao productLocationDao;
	@Autowired
	TaskImproveDao taskImproveDao;
	@Autowired
	LocationDao locationDao;
	@Autowired
	ProductDao productDao;
	@Autowired
	ProductLocationDetailDao productLocationDetailDao;
	/**
	 * 执行盘点工作
	 * @author dlyao
	 * @param sqc 盘点工作
	 */
	@Override
	public Map<String, Object> doStartJob(ScheduleQueueCount sqc) {
		// 初始化
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", true);
    	resultMap.put("message", "这条盘点工作已成功执行");
		
		// 返回店铺名称
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();

		String created_user = ((SysUser) session.getAttribute("currentUser"))
				.getUsername();

		int hide_batch_sn = sqc.getHide_batch_sn();
		int hide_real_num = sqc.getHide_real_num();
		int customer_id = sqc.getCustomer_id(); // 0表示不限货主 需要在查询时单独处理
		int physical_warehouse_id = sqc.getPhysical_warehouse_id();

		String task_type = sqc.getTask_type(); // 标准盘点 "NORMAL"

		String location_type = sqc.getLocation_type();
		String from_location_barcode = sqc.getFrom_location_barcode();
		String to_location_barcode = sqc.getTo_location_barcode();
		String barcode=sqc.getBarcode();


		List<Map>list=scheduleQueueCountDao.getProductLoactionByCount(physical_warehouse_id,customer_id,location_type,hide_batch_sn,from_location_barcode,to_location_barcode,barcode);
        if(list==null||list.size()==0){
        	
        	scheduleQueueCountDao.updateSqcStatus(sqc.getQueue_id(),ScheduleQueueCount.STATUS_FULFILLED,0);
        	return resultMap;
        }
        
        List<TaskCount> taskCountList = new ArrayList<TaskCount>();
        List<TaskCount> taskCountList2 = new ArrayList<TaskCount>();
        for(Map m:list){
        	TaskCount tc=new TaskCount();
        	tc.setPhysical_warehouse_id(Integer.parseInt(m.get("physical_warehouse_id").toString()));
        	tc.setWarehouse_id(Integer.parseInt(m.get("warehouse_id").toString()));
        	tc.setCustomer_id(Integer.parseInt(m.get("customer_id").toString()));
        	tc.setTask_type(task_type);
        	tc.setHide_real_num(hide_real_num);
        	tc.setHide_batch_sn(sqc.getHide_batch_sn());
        	tc.setCount_sn(sqc.getCount_sn());
        	tc.setProduct_id(Integer.parseInt(m.get("product_id").toString()));
            tc.setCreated_user(created_user);
            if(Integer.parseInt(m.get("qty_total").toString())<=0){
            	continue;
            }
            tc.setNum_real(Integer.parseInt(m.get("qty_total").toString()));
            tc.setProduct_status(m.get("product_status").toString());
            tc.setStatus("INIT");
            tc.setMark(0);
            tc.setBatch_sn(m.get("batch_sn").toString());
            tc.setLocation_id(Integer.parseInt(m.get("location_id").toString()));
            if(sqc.getHide_batch_sn()!=0){
            	String validity="1970-01-01 00:00:00";
            	try{
            	validity=m.get("validity").toString()==null?"1970-01-01 00:00:00":m.get("validity").toString();
            	}
            	catch (Exception e){
            		validity="1970-01-01 00:00:00";
            	}
            	tc.setValidity(validity);
            }
            if(tc.getValidity().equals("")){
            	taskCountList.add(tc);
            }else{
            	taskCountList2.add(tc);
            }
            
        }
       if(!WorkerUtil.isNullOrEmpty(taskCountList)){
        	countTaskDao.batchInsert(taskCountList);
        }
       if(!WorkerUtil.isNullOrEmpty(taskCountList2)){
        	countTaskDao.batchInsert2(taskCountList2);
        }
        
        scheduleQueueCountDao.updateSqcStatus(sqc.getQueue_id(),ScheduleQueueCount.STATUS_FULFILLED,taskCountList.size()+taskCountList2.size());
        resultMap.put("num", taskCountList.size()+taskCountList2.size());
		return resultMap;
	}

	/**
	 * 取消盘点工作
	 * @author dlyao
	 * @param sqc 盘点工作
	 */
	@Override
	public Map<String, Object> doCancelJob(ScheduleQueueCount sqc) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", true);
    	

		//盘点工作已经取消
		if(sqc.getStatus().equalsIgnoreCase(ScheduleQueueCount.STATUS_CANCEL)){
			resultMap.put("message", "这条盘点工作已处于取消状态");
		}
		//盘点工作未执行
		else if(sqc.getStatus().equalsIgnoreCase(ScheduleQueueCount.STATUS_INIT)){
			scheduleQueueCountDao.updateSqcStatus(sqc.getQueue_id(),ScheduleQueueCount.STATUS_CANCEL,0);
			resultMap.put("message", "这条盘点工作已成功取消");
		}
		//盘点工作已经执行，并生成了盘点任务
		else{
			//此工作创建的任务总数
			int num=sqc.getNum();
			if(num==0){
				scheduleQueueCountDao.updateSqcStatus(sqc.getQueue_id(),ScheduleQueueCount.STATUS_CANCEL,0);
				resultMap.put("message", "这条盘点工作已成功取消");
			}else{
				//找到所有未开始的任务
				int myNum=countTaskDao.getCountTaskCount(sqc.getCount_sn());
				if(num!=myNum){
					resultMap.put("success", false);
					resultMap.put("message", "这条盘点工作正在盘点中，不能被取消");
				}else{
					countTaskDao.cancelTaskCountByCountSn(sqc.getCount_sn());
					scheduleQueueCountDao.updateSqcStatus(sqc.getQueue_id(),ScheduleQueueCount.STATUS_CANCEL,num);
					resultMap.put("message", "这条盘点工作已成功取消");
				}
			}
			
			
		}
		
		return resultMap;
	}
	
	
	@Override
	public Map<String, Object> searchCountSnForPrint(String countSn,
			Integer physicalWarehouseId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String,Object>> taskList = countTaskDao.searchCountSnForPrint(countSn,physicalWarehouseId,"",0);
		if(WorkerUtil.isNullOrEmpty(taskList)){
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", "根据盘点任务号未找到任何(未完成未取消)盘点任务！");
		}else{
			Map<String,Object> taskStatusMap = countTaskDao.searchCountSnForPrintGroup(countSn,physicalWarehouseId);
			String taskType = String.valueOf(taskStatusMap.get("group_task_type"));
			String status = String.valueOf(taskStatusMap.get("group_status"));
			
			String[] statusArr = status.split(",");
			char couldPrint = 'Y';
			if(statusArr.length==1){
				List<String> singleStatus = new ArrayList<String>();
				singleStatus.add("INIT");
				singleStatus.add("OVER_FIRST");
				singleStatus.add("OVER_SECOND");
				if(!singleStatus.contains(statusArr[0])){
					couldPrint = 'N';
				}
			}else{
				couldPrint = 'N';
			}
			resultMap.put("result", Response.SUCCESS);
			resultMap.put("taskType", taskType);
			resultMap.put("taskNum", taskList.size());
			resultMap.put("couldPrint", couldPrint);
			resultMap.put("taskList", taskList);
		}
		return resultMap;
	}

	@Override
	public HashMap<String, Object> bindUser(Integer bind_user_id, String username,
			String batch_task_sn) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		BatchTaskCount btc = batchTaskCountDao.getBatchTaskCount(batch_task_sn);

		if (WorkerUtil.isNullOrEmpty(btc)) {

			resultMap.put("error", "盘点任务编号不存在！" + batch_task_sn);
			resultMap.put("error_id", 2);
			resultMap.put("success", false);

			logger.info("CountTaskBizImpl bindUser batch_task_sn is not existing");
			return resultMap;
		}

		if ((!WorkerUtil.isNullOrEmpty(btc.getBinded_user_id()))&&btc.getBinded_user_id()!=0) {

			resultMap.put("error", "盘点任务编号已经绑定！");
			resultMap.put("error_id", 3);
			resultMap.put("success", false);

			logger.info("CountTaskBizImpl bindUser batch_task_sn is already  binding by another");
			return resultMap;
		}

		logger.info("CountTaskBizImpl bindUser UserId" + bind_user_id
				+ " batch_task_id" + btc.getBatch_task_id());

		batchTaskCountDao.updateBindbatchTaskCount(bind_user_id,
				btc.getBatch_task_id());

		resultMap.put("success", true);

		return resultMap;
	}

	
	/**
	 * 根据盘点任务号&每页打印数量 确定盘点任务编号
	 * by ytchen
	 */
	@Override
	public List<Map<String, Object>> createBatchTaskCount(String countSn,
			Integer taskNumPerPage) {
		List<Map<String,Object>> batchStockTaskList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> taskStatusMap = countTaskDao.searchCountSnForBatch(countSn);
		Assert.isTrue(!WorkerUtil.isNullOrEmpty(taskStatusMap),"盘点任务号没有查到任务记录！");
		String countSnStatus = String.valueOf(taskStatusMap.get(0).get("status"));
		List<String> singleStatus = new ArrayList<String>();
		singleStatus.add("INIT");
		singleStatus.add("OVER_FIRST");
		singleStatus.add("OVER_SECOND");
		Assert.isTrue(
				taskStatusMap.size()==1 && 
				singleStatus.contains(countSnStatus),
				"盘点任务号下任务状态不允许操作打印！");
		

		Map<Integer,String> MARK_STATUS_MAP = new HashMap<Integer,String>();
		MARK_STATUS_MAP.put(1, "ON_FIRST");
		MARK_STATUS_MAP.put(2, "ON_SECOND");
		MARK_STATUS_MAP.put(3, "ON_THIRD");
		Integer taskNum = Integer.parseInt(String.valueOf(taskStatusMap.get(0).get("task_count")));
		Integer batchNum = taskNum/taskNumPerPage+(taskNum%taskNumPerPage==0?0:1); //总共分几个盘点任务编号
		Integer batchMark = 0;
		if("INIT".equals(countSnStatus)){
			batchMark = 1;
		}else if("OVER_FIRST".equals(countSnStatus)){
			batchMark = 2;
		}else if("OVER_SECOND".equals(countSnStatus)){
			batchMark = 3; 
		} 
		for (int i = 1; i <=batchNum; i++) {
			String batchCountSn = countSn+"-"+batchMark+"-"+i;
			List<Integer> taskIdList = countTaskDao.searchCountTaskByNum(countSn,countSnStatus,taskNumPerPage);
			Integer col = countTaskDao.batchUpdateMark(batchCountSn,batchMark,taskIdList,String.valueOf(MARK_STATUS_MAP.get(batchMark)));
			Assert.isTrue(col>0,"查找盘点任务失败！");
			Subject subject = SecurityUtils.getSubject();
	        Session session = subject.getSession();
	        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
	        BatchTaskCount batchTaskCount = new BatchTaskCount();
	        batchTaskCount.setBatch_task_sn(batchCountSn);
	        batchTaskCount.setCount_sn(countSn);
	        batchTaskCount.setMark(batchMark);
	        batchTaskCount.setCreated_time(new Date());
	        batchTaskCount.setCreated_user(sysUser.getUsername());
			col = batchTaskCountDao.insertBatchTaskCount(batchTaskCount);
			Assert.isTrue(col>0,"生成盘点任务编号失败！");
			Map<String,Object> batchStockTaskMap = new HashMap<String,Object>();
			List<Map<String,Object>> taskList = countTaskDao.searchCountSnForPrint(countSn,0,batchCountSn,batchMark);
			
			batchStockTaskMap.put("batchTaskSn",batchCountSn);
			batchStockTaskMap.put("taskNum",taskNum);
			batchStockTaskMap.put("taskType",taskList.get(0).get("task_type"));
			batchStockTaskMap.put("taskList",taskList);
			batchStockTaskMap.put("mark", batchMark==1?"初盘":(batchMark==2?"复盘":"三盘"));
			batchStockTaskList.add(batchStockTaskMap);
		}
		return batchStockTaskList;
	}

	@Override
	public void updateList(List<TaskCount> list, int mark,String user_name) {
		countTaskDao.updateList(list,mark,user_name);		
	}

	@Override
	public void doCreateProductLocationChange(List<Integer> productIdList, int physical_warehouse_id,
			String user_name, int customer_id, List<ProductLocation> addList,
			List<ProductLocation> minusList,String task_improve_sn, int warehouse_id) {
		List<ProductLocation> productlocationAddList = new ArrayList<ProductLocation>();// 盘盈区
		List<ProductLocation> productlocationMinusList = new ArrayList<ProductLocation>();// 盘亏区
		List<ProductLocation> productlocationAddStockList = new ArrayList<ProductLocation>();// 盘盈调整池
		List<ProductLocation> productlocationMinusStockList = new ArrayList<ProductLocation>();// 盘亏调整池
		
		Map<String,ProductLocation> map1=new HashMap<String,ProductLocation>();
		Map<String,ProductLocation> map2=new HashMap<String,ProductLocation>();
		
		int addLocationIdNum=locationDao.getLocationIdInVarianceNum(physical_warehouse_id,Location.LOCATION_TYPE_VARIANCE_STOCK_ADD);
		int minusLocationIdNum=locationDao.getLocationIdInVarianceNum(physical_warehouse_id,Location.LOCATION_TYPE_VARIANCE_STOCK_MIMUS);
		//有生产日期
		List<ProductLocation> insertList=new ArrayList<ProductLocation>();
		List<ProductLocation> insertList2=new ArrayList<ProductLocation>();
		int addLocationId=0;
		if(addLocationIdNum!=0){
			addLocationId=locationDao.getLocationIdInVariance(physical_warehouse_id,Location.LOCATION_TYPE_VARIANCE_STOCK_ADD);
		}else{
			Location l=new Location();
			l.setLocation_type(Location.LOCATION_TYPE_VARIANCE_STOCK_ADD);
			l.setLocation_barcode(WorkerUtil.generatorSequence(SequenceUtil.KEY_NAME_OTHER,"PKS",true));
			l.setPhysical_warehouse_id(physical_warehouse_id);
			l.setIs_delete("N");
			l.setIs_empty("N");
			l.setCreated_user("SYSTEM");
			locationDao.insert(l);
			
			addLocationId=l.getLocation_id();
		}
		int minusLocationId=0;
		if(minusLocationIdNum!=0){
			minusLocationId=locationDao.getLocationIdInVariance(physical_warehouse_id,Location.LOCATION_TYPE_VARIANCE_STOCK_MIMUS);
		}else{
			Location l=new Location();
			l.setLocation_type(Location.LOCATION_TYPE_VARIANCE_STOCK_MIMUS);
			l.setLocation_barcode(WorkerUtil.generatorSequence(SequenceUtil.KEY_NAME_OTHER,"PKS",true));
			l.setPhysical_warehouse_id(physical_warehouse_id);
			l.setIs_delete("N");
			l.setIs_empty("N");
			l.setCreated_user("SYSTEM");
			locationDao.insert(l);
			
			minusLocationId=l.getLocation_id();
		}
		
		productlocationAddList = productLocationDao
				.selectProductlocationListV5(physical_warehouse_id,
						productIdList, Location.LOCATION_TYPE_VARIANCE_ADD,warehouse_id);
		
		productlocationMinusList = productLocationDao
				.selectProductlocationListV5(physical_warehouse_id,
						productIdList, Location.LOCATION_TYPE_VARIANCE_MIMUS,warehouse_id);
		
		productlocationAddStockList = productLocationDao
				.selectProductlocationListV5(physical_warehouse_id,
						productIdList, Location.LOCATION_TYPE_VARIANCE_STOCK_ADD,warehouse_id);
		
		productlocationMinusStockList = productLocationDao
				.selectProductlocationListV5(physical_warehouse_id,
						productIdList, Location.LOCATION_TYPE_VARIANCE_STOCK_MIMUS,warehouse_id);
		
		for(ProductLocation pl:productlocationAddStockList){
			StringBuilder   sb=new StringBuilder("");
			sb.append(pl.getPhysical_warehouse_id()).append("_").append(pl.getProduct_id()).append("_").append(pl.getStatus()).append("_").append(pl.getValidity()).append("_").append(pl.getBatch_sn());
			pl.setQty_total(0);
			map1.put(sb.toString(), pl);
		}
		
		for(ProductLocation pl:productlocationMinusStockList){
			StringBuilder   sb=new StringBuilder("");
			sb.append(pl.getPhysical_warehouse_id()).append("_").append(pl.getProduct_id()).append("_").append(pl.getStatus()).append("_").append(pl.getValidity()).append("_").append(pl.getBatch_sn());
			pl.setQty_total(0);
			map2.put(sb.toString(), pl);
		}
		
		
		List<ProductLocation> updateList=new ArrayList<ProductLocation>();
		List<TaskImprove> tiList=new ArrayList<TaskImprove>();
		
		List<ProductLocationDetail> pldList=new  ArrayList<ProductLocationDetail>();
		
		if(!WorkerUtil.isNullOrEmpty(addList)){
			for(ProductLocation p:addList){
				
				TaskImprove ti =new TaskImprove();
				ti.setTask_improve_sn(task_improve_sn);
				ti.setProduct_id(p.getProduct_id());
				ti.setPhysical_warehouse_id(physical_warehouse_id);
				ti.setProduct_status(p.getStatus());
				ti.setCreated_user(user_name);
				ti.setStatus("INIT");
				ti.setNum(p.getQty_total());
				ti.setWarehouse_id(warehouse_id);
				tiList.add(ti);				
				
				int num=p.getQty_total();
				int sum=0;
				for(ProductLocation pl:productlocationAddList){
					
					if(p.getProduct_id()-pl.getProduct_id()==0&&p.getStatus().equals(pl.getStatus())){
						StringBuilder   key=new StringBuilder("");
						key.append(pl.getPhysical_warehouse_id()).append("_").append(pl.getProduct_id()).append("_").append(pl.getStatus()).append("_").append(pl.getValidity()).append("_").append(pl.getBatch_sn());
						if(num>sum+pl.getQty_total()){
							//减少盘盈区库存
							ProductLocation plUpdate=new ProductLocation();
							sum+=pl.getQty_total();
							plUpdate.setPl_id(pl.getPl_id());
							plUpdate.setQty_total(0-pl.getQty_total());//差异值 是负数
							
							ProductLocationDetail productLocationDetail = new ProductLocationDetail();
							productLocationDetail.setPl_id(pl.getPl_id());
							productLocationDetail.setChange_quantity(0-pl.getQty_total());
							productLocationDetail.setTask_count_id(0);
							productLocationDetail.setDescription("月末调整盘盈区库存");
							productLocationDetail.setCreated_user(user_name);
							productLocationDetail.setLast_updated_user(user_name);
							productLocationDetail.setCount_sn(task_improve_sn);
							pldList.add(productLocationDetail);
							
							//增加盘盈池库存
							if(null!=map1.get(key.toString())){
								ProductLocation plAddStock=map1.get(key.toString());
								plAddStock.setQty_total(plAddStock.getQty_total()+pl.getQty_total());
								
								ProductLocationDetail productLocationDetail2 = new ProductLocationDetail();
								productLocationDetail2.setPl_id(plAddStock.getPl_id());
								productLocationDetail2.setChange_quantity(pl.getQty_total());
								productLocationDetail2.setTask_count_id(0);
								productLocationDetail2.setDescription("月末调整盘盈池库存");
								productLocationDetail2.setCreated_user(user_name);
								productLocationDetail2.setLast_updated_user(user_name);
								productLocationDetail2.setCount_sn(task_improve_sn);
								pldList.add(productLocationDetail2);
							}else{
								ProductLocation pl2=new ProductLocation();
								pl2.setSerial_number(pl.getSerial_number());
								pl2.setQty_available(pl.getQty_total());
								pl2.setQty_total(pl.getQty_total());
								pl2.setPhysical_warehouse_id(physical_warehouse_id);
								pl2.setProduct_id(pl.getProduct_id());
								pl2.setStatus(pl.getStatus());
								pl2.setValidity(pl.getValidity());
								pl2.setBatch_sn(pl.getBatch_sn());
								pl2.setLocation_id(addLocationId);
								pl2.setCreated_user(user_name);
								pl2.setProduct_location_status("NORMAL");
								pl2.setWarehouse_id(warehouse_id);
								if(pl.getValidity().equals("")){
									pl2.setValidity("1970-01-01 00:00:00");
									insertList.add(pl2);
								}else{
									insertList.add(pl2);
								}
								
								
								map1.put(key.toString(), pl2);
							}
							
							updateList.add(plUpdate);
							
						}else{
							ProductLocation plUpdate=new ProductLocation();
							
							plUpdate.setPl_id(pl.getPl_id());
							plUpdate.setQty_total(sum-num);//差异值 是负数
							
							ProductLocationDetail productLocationDetail = new ProductLocationDetail();
							productLocationDetail.setPl_id(pl.getPl_id());
							productLocationDetail.setChange_quantity(sum-num);
							productLocationDetail.setTask_count_id(0);
							productLocationDetail.setDescription("月末调整盘盈区库存");
							productLocationDetail.setCreated_user(user_name);
							productLocationDetail.setLast_updated_user(user_name);
							productLocationDetail.setCount_sn(task_improve_sn);
							pldList.add(productLocationDetail);
							
							//增加盘盈池库存
							if(null!=map1.get(key.toString())){
								ProductLocation plAddStock=map1.get(key.toString());
								plAddStock.setQty_total(plAddStock.getQty_total()+num-sum);
								
								ProductLocationDetail productLocationDetail2 = new ProductLocationDetail();
								productLocationDetail2.setPl_id(plAddStock.getPl_id());
								productLocationDetail2.setChange_quantity(num-sum);
								productLocationDetail2.setTask_count_id(0);
								productLocationDetail2.setDescription("月末调整盘盈池库存");
								productLocationDetail2.setCreated_user(user_name);
								productLocationDetail2.setLast_updated_user(user_name);
								productLocationDetail2.setCount_sn(task_improve_sn);
								pldList.add(productLocationDetail2);
							}else{
								ProductLocation pl2=new ProductLocation();
								pl2.setSerial_number(pl.getSerial_number());
								pl2.setQty_available(num-sum);
								pl2.setQty_total(num-sum);
								pl2.setPhysical_warehouse_id(physical_warehouse_id);
								pl2.setProduct_id(pl.getProduct_id());
								pl2.setStatus(pl.getStatus());
								pl2.setValidity(pl.getValidity());
								pl2.setBatch_sn(pl.getBatch_sn());
								pl2.setLocation_id(addLocationId);
								pl2.setCreated_user(user_name);
								pl2.setProduct_location_status("NORMAL");
								pl2.setWarehouse_id(warehouse_id);
								if(pl.getValidity().equals("")){
									pl2.setValidity("1970-01-01 00:00:00");
									insertList.add(pl2);
								}else{
									insertList.add(pl2);
								}
								
								
								map1.put(key.toString(), pl2);
							}
							
							updateList.add(plUpdate);
							
							break;
						}
					}
				}
			}
		}
		
		if(!WorkerUtil.isNullOrEmpty(minusList)){
			for(ProductLocation p:minusList){
				
				TaskImprove ti =new TaskImprove();
				ti.setTask_improve_sn(task_improve_sn);
				ti.setProduct_id(p.getProduct_id());
				ti.setPhysical_warehouse_id(physical_warehouse_id);
				ti.setProduct_status(p.getStatus());
				ti.setCreated_user(user_name);
				ti.setStatus("INIT");
				ti.setNum(0-p.getQty_total());
				ti.setWarehouse_id(warehouse_id);
				tiList.add(ti);
				
				int num=p.getQty_total();
				int sum=0;
				for(ProductLocation pl:productlocationMinusList){
					StringBuilder   key=new StringBuilder("");
					key.append(pl.getPhysical_warehouse_id()).append("_").append(pl.getProduct_id()).append("_").append(pl.getStatus()).append("_").append(pl.getValidity()).append("_").append(pl.getBatch_sn());
					
					if(p.getProduct_id()-pl.getProduct_id()==0&&p.getStatus().equals(pl.getStatus())){
						if(num>sum+pl.getQty_total()){
							ProductLocation plUpdate=new ProductLocation();
							sum+=pl.getQty_total();
							plUpdate.setPl_id(pl.getPl_id());
							plUpdate.setQty_total(0-pl.getQty_total());//差异值 是负数
							
							ProductLocationDetail productLocationDetail = new ProductLocationDetail();
							productLocationDetail.setPl_id(pl.getPl_id());
							productLocationDetail.setChange_quantity(0-pl.getQty_total());
							productLocationDetail.setTask_count_id(0);
							productLocationDetail.setDescription("月末调整盘亏区库存");
							productLocationDetail.setCreated_user(user_name);
							productLocationDetail.setLast_updated_user(user_name);
							productLocationDetail.setCount_sn(task_improve_sn);
							pldList.add(productLocationDetail);
							
							//增加盘盈池库存
							if(null!=map2.get(key.toString())){
								ProductLocation plMinusStock=map2.get(key.toString());
								plMinusStock.setQty_total(plMinusStock.getQty_total()+pl.getQty_total());
								
								ProductLocationDetail productLocationDetail2 = new ProductLocationDetail();
								productLocationDetail2.setPl_id(plMinusStock.getPl_id());
								productLocationDetail2.setChange_quantity(pl.getQty_total());
								productLocationDetail2.setTask_count_id(0);
								productLocationDetail2.setDescription("月末调整盘亏池库存");
								productLocationDetail2.setCreated_user(user_name);
								productLocationDetail2.setLast_updated_user(user_name);
								productLocationDetail2.setCount_sn(task_improve_sn);
								pldList.add(productLocationDetail2);
							}else{
								ProductLocation pl2=new ProductLocation();
								pl2.setSerial_number(pl.getSerial_number());
								pl2.setQty_available(pl.getQty_total());
								pl2.setQty_total(pl.getQty_total());
								pl2.setPhysical_warehouse_id(physical_warehouse_id);
								pl2.setProduct_id(pl.getProduct_id());
								pl2.setStatus(pl.getStatus());
								pl2.setValidity(pl.getValidity());
								pl2.setBatch_sn(pl.getBatch_sn());
								pl2.setLocation_id(minusLocationId);
								pl2.setCreated_user(user_name);
								pl2.setProduct_location_status("NORMAL");
								pl2.setWarehouse_id(warehouse_id);
								if(pl.getValidity().equals("")){
									pl2.setValidity("1970-01-01 00:00:00");
									insertList2.add(pl2);
								}else{
									insertList2.add(pl2);
								}
								map2.put(key.toString(), pl2);
							}
							
							updateList.add(plUpdate);
							
							
							
						}else{
							ProductLocation plUpdate=new ProductLocation();
							
							plUpdate.setPl_id(pl.getPl_id());
							plUpdate.setQty_total(sum-num);//差异值  是负数
							
							ProductLocationDetail productLocationDetail = new ProductLocationDetail();
							productLocationDetail.setPl_id(pl.getPl_id());
							productLocationDetail.setChange_quantity(sum-num);
							productLocationDetail.setTask_count_id(0);
							productLocationDetail.setDescription("月末调整盘亏区库存");
							productLocationDetail.setCreated_user(user_name);
							productLocationDetail.setLast_updated_user(user_name);
							productLocationDetail.setCount_sn(task_improve_sn);
							pldList.add(productLocationDetail);
							
							
							//增加盘亏池库存
							if(null!=map2.get(key.toString())){
								ProductLocation plMinusStock=map2.get(key.toString());
								plMinusStock.setQty_total(plMinusStock.getQty_total()+num-sum);
								
								ProductLocationDetail productLocationDetail2 = new ProductLocationDetail();
								productLocationDetail2.setPl_id(plMinusStock.getPl_id());
								productLocationDetail2.setChange_quantity(num-sum);
								productLocationDetail2.setTask_count_id(0);
								productLocationDetail2.setDescription("月末调整盘亏池库存");
								productLocationDetail2.setCreated_user(user_name);
								productLocationDetail2.setLast_updated_user(user_name);
								productLocationDetail2.setCount_sn(task_improve_sn);
								pldList.add(productLocationDetail2);
							}else{
								ProductLocation pl2=new ProductLocation();
								pl2.setQty_available(num-sum);
								pl2.setQty_total(num-sum);
								pl2.setSerial_number(pl.getSerial_number());
								pl2.setPhysical_warehouse_id(physical_warehouse_id);
								pl2.setProduct_id(pl.getProduct_id());
								pl2.setStatus(pl.getStatus());
								pl2.setValidity(pl.getValidity());
								pl2.setBatch_sn(pl.getBatch_sn());
								pl2.setLocation_id(minusLocationId);
								pl2.setCreated_user(user_name);
								pl2.setProduct_location_status("NORMAL");
								pl2.setWarehouse_id(warehouse_id);
								if(pl.getValidity().equals("")){
									pl2.setValidity("1970-01-01 00:00:00");
									insertList2.add(pl2);
								}else{
									insertList2.add(pl2);
								}
								
								
								map2.put(key.toString(), pl2);
							}
							
							updateList.add(plUpdate);
							
							break;
						}
					}
				}
			}
		}
		if(!WorkerUtil.isNullOrEmpty(insertList)){
			for(ProductLocation pl:insertList){
				
				
				ProductLocationDetail productLocationDetail2 = new ProductLocationDetail();
				productLocationDetail2.setChange_quantity(pl.getQty_total());
				productLocationDetail2.setTask_count_id(0);
				productLocationDetail2.setDescription("月末调整盘盈池库存");
				productLocationDetail2.setCreated_user(user_name);
				productLocationDetail2.setLast_updated_user(user_name);
				productLocationDetail2.setCount_sn(task_improve_sn);
				
				productLocationDao.insert(pl);
				productLocationDetail2.setPl_id(pl.getPl_id());
				
				pldList.add(productLocationDetail2);
			}
			
		}
		
		if(!WorkerUtil.isNullOrEmpty(insertList2)){
			for(ProductLocation pl:insertList2){
				
				ProductLocationDetail productLocationDetail2 = new ProductLocationDetail();
				productLocationDetail2.setChange_quantity(pl.getQty_total());
				productLocationDetail2.setTask_count_id(0);
				productLocationDetail2.setDescription("月末调整盘亏池库存");
				productLocationDetail2.setCreated_user(user_name);
				productLocationDetail2.setLast_updated_user(user_name);
				productLocationDetail2.setCount_sn(task_improve_sn);
				
				productLocationDao.insert(pl);
				productLocationDetail2.setPl_id(pl.getPl_id());
				
				pldList.add(productLocationDetail2);
			}
			
		}
		
		//更新盘盈盘亏区库存记录
		updateList.addAll(productlocationAddStockList);
		updateList.addAll(productlocationMinusStockList);
		if(!WorkerUtil.isNullOrEmpty(updateList)){
			productLocationDao.updateProductLocationV5(updateList);
		}
		
		//更新盘盈盘亏区库存记录
		if(!WorkerUtil.isNullOrEmpty(pldList)){
			productLocationDetailDao.insertList(pldList);
		}
				
		//更新盘盈盘亏区库存记录
		if(!WorkerUtil.isNullOrEmpty(tiList)){
			taskImproveDao.batchInsert(tiList);
		}
		
		
	}

	@Override
	public Map<String, Object> doTaskCountByHand(int physical_warehouse_id,Integer warehouse_id,
			String barcode, String location_barcode, Integer customer_id,
			int num_real, String count_sn, String userName, String status) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		if((!LocationBarcodeTools.checkLoactionBarcode(location_barcode))&&(location_barcode.length()!=0)){
			resultMap.put("success", false);
			resultMap.put("message", "请输入正确的库位");
			return resultMap;
		}
		
		Location location=locationDao.getCountLocationsByPhysicalAndId(physical_warehouse_id,location_barcode);
		
		if(null==location){
			resultMap.put("success", false);
			resultMap.put("message", "库位不存在");
			return resultMap;
		}
		
		
		if(!(location.getLocation_type().equalsIgnoreCase(Location.LOCATION_TYPE_BOX_PICK)
				||location.getLocation_type().equalsIgnoreCase(Location.LOCATION_TYPE_PIECE_PICK)
				||location.getLocation_type().equalsIgnoreCase(Location.LOCATION_TYPE_QUALITY_CHECK)
				||location.getLocation_type().equalsIgnoreCase(Location.LOCATION_TYPE_RETURN)
				||location.getLocation_type().equalsIgnoreCase(Location.LOCATION_TYPE_STOCK)
				||location.getLocation_type().equalsIgnoreCase(Location.LOCATION_TYPE_PACKBOX)
				||location.getLocation_type().equalsIgnoreCase(Location.LOCATION_TYPE_DEFECTIVE)
				)){
			resultMap.put("success", false);
			resultMap.put("message", "此库位类型不能盘点");
			return resultMap;
		}
		
		Product product=productDao.selectProductByBarcodeCustomer(barcode, customer_id);
		
		if(null==product){
			resultMap.put("success", false);
			resultMap.put("message", "商品条码输错了");
			return resultMap;
		}else if("PREPACKAGE".equalsIgnoreCase(product.getProduct_type())){
			resultMap.put("success", false);
			resultMap.put("message", "套餐商品不支持盘点");
			return resultMap;
		}else if (product.getCustomer_id().intValue()==27){
			resultMap.put("success", false);
			resultMap.put("message", "geox渠道上线前，不允许盘点");
			return resultMap;
		}
		
		int otherWarehouseSum=scheduleQueueCountDao.getProductLoactionByCountByHandNotIn(physical_warehouse_id,customer_id,location_barcode,barcode,status,warehouse_id);
		if(otherWarehouseSum>0){
			resultMap.put("success", false);
			resultMap.put("message", "此库位此商品属于其他渠道");
			return resultMap;
		}
		
		
		List<Map>list=scheduleQueueCountDao.getProductLoactionByCountByHand(physical_warehouse_id,customer_id,location_barcode,barcode,status,warehouse_id);
		List<TaskCount> taskCountList = new ArrayList<TaskCount>();
		if(WorkerUtil.isNullOrEmpty(list)){
			//没有库存记录  需要插入一条
			ProductLocation pl2=new ProductLocation();
			pl2.setQty_available(0);
			pl2.setQty_total(0);
			pl2.setPhysical_warehouse_id(physical_warehouse_id);
			pl2.setProduct_id(product.getProduct_id());
			pl2.setStatus(status);
			pl2.setValidity(DateUtils.getStringTime(new Date()));
			pl2.setLocation_id(location.getLocation_id());
			pl2.setCreated_user(userName);
			pl2.setProduct_location_status("NORMAL");
			pl2.setSerial_number("");
			pl2.setWarehouse_id(warehouse_id);
			
			productLocationDao.insert(pl2);
			
			//创建一个job
			ScheduleQueueCount sqc= new ScheduleQueueCount();
			sqc.setPhysical_warehouse_id(physical_warehouse_id);
			sqc.setCreated_user(userName);
			sqc.setCreated_time(new Date());
			sqc.setLocation_type(location.getLocation_type());
			sqc.setFrom_location_barcode(location_barcode);
			sqc.setTo_location_barcode(location_barcode);
			sqc.setTask_type("NORMAL");
			sqc.setCount_sn(count_sn);
			sqc.setStatus("FULFILLED");
			sqc.setHide_batch_sn(0);
			sqc.setHide_real_num(0);
			sqc.setCustomer_id(customer_id);
			sqc.setBarcode(barcode);
			sqc.setNum(1);
			
			scheduleQueueCountDao.insert(sqc);
			
			TaskCount tc=new TaskCount();
        	tc.setPhysical_warehouse_id(physical_warehouse_id);
        	tc.setCustomer_id(customer_id);
        	tc.setTask_type("NORMAL");
        	tc.setHide_real_num(0);
        	tc.setHide_batch_sn(0);
        	
        	tc.setCount_sn(sqc.getCount_sn());
        	tc.setProduct_id(product.getProduct_id());
            tc.setCreated_user(userName);
            tc.setNum_real(0);
            tc.setProduct_status(status);
            tc.setStatus("INIT");
            tc.setMark(0);
            tc.setWarehouse_id(warehouse_id);
            tc.setLocation_id(location.getLocation_id());
            
        	tc.setValidity(DateUtils.getStringTime(new Date()));
        	
           
        	taskCountList.add(tc);
			
		}else{
			//创建一个job
			ScheduleQueueCount sqc= new ScheduleQueueCount();
			sqc.setPhysical_warehouse_id(physical_warehouse_id);
			sqc.setCreated_user(userName);
			sqc.setCreated_time(new Date());
			sqc.setLocation_type(location.getLocation_type());
			sqc.setFrom_location_barcode(location_barcode);
			sqc.setTo_location_barcode(location_barcode);
			sqc.setTask_type("NORMAL");
			sqc.setCount_sn(count_sn);
			sqc.setStatus("FULFILLED");
			sqc.setHide_batch_sn(0);
			sqc.setHide_real_num(0);
			sqc.setCustomer_id(customer_id);
			sqc.setBarcode(barcode);
			sqc.setNum(1);
			
			scheduleQueueCountDao.insert(sqc);
			
			//创建一个归属于该job的盘点任务
			
			Map m=list.get(0);
			TaskCount tc=new TaskCount();
        	tc.setPhysical_warehouse_id(physical_warehouse_id);
        	tc.setCustomer_id(customer_id);
        	tc.setTask_type("NORMAL");
        	tc.setHide_real_num(0);
        	tc.setHide_batch_sn(0);
        	tc.setCount_sn(sqc.getCount_sn());
        	tc.setProduct_id(product.getProduct_id());
            tc.setCreated_user(userName);
            tc.setNum_real(Integer.parseInt(m.get("qty_total").toString()));
            tc.setProduct_status(m.get("product_status").toString());
            tc.setStatus("INIT");
            tc.setMark(0);
            tc.setWarehouse_id(warehouse_id);
            tc.setLocation_id(Integer.parseInt(m.get("location_id").toString()));
        	
        	tc.setValidity(DateUtils.getStringTime(new Date()));

        	taskCountList.add(tc);
		}
		
		if(!WorkerUtil.isNullOrEmpty(taskCountList)){
        	countTaskDao.batchInsert2(taskCountList);
        }
		
		resultMap.put("success", true);
		return resultMap;
	}
}
