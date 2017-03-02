package com.leqee.wms.biz.impl;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.LocationBiz;
import com.leqee.wms.biz.ReplenishmentBiz;
import com.leqee.wms.biz.ReplenishmentUrgentBiz;
import com.leqee.wms.dao.BatchTaskDao;
import com.leqee.wms.dao.InventoryDao;
import com.leqee.wms.dao.LabelReplenishmentDao;
import com.leqee.wms.dao.LocationDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.ProductLocationDetailDao;
import com.leqee.wms.dao.ReplenishmentDao;
import com.leqee.wms.dao.ReplenishmentUrgentDao;
import com.leqee.wms.dao.TaskDao;
import com.leqee.wms.entity.BatchTask;
import com.leqee.wms.entity.ConfigReplenishment;
import com.leqee.wms.entity.ConfigReplenishmentUrgent;
import com.leqee.wms.entity.LabelReplenishment;
import com.leqee.wms.entity.Location;
import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.entity.ProductLocationDetail;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.SequenceUtil;
import com.leqee.wms.util.WorkerUtil;

@Service
public class ReplenishmentUrgentBizImpl implements ReplenishmentUrgentBiz {

	private Logger logger = Logger.getLogger(ReplenishmentUrgentBizImpl.class);
	@Autowired
	ReplenishmentUrgentDao replenishmentUrgentDao;
	@Override
	public List<Map<String, Object>> selectByMap(Map<String, Object>map){
		return replenishmentUrgentDao.selectByMapByPage(map);
	}
	
	
	@Override
	
	public void deleteReplenishmentUrgent(Integer customer_id,Integer physical_warehouse_id){
		if(replenishmentUrgentDao.deleteReplenishmentUrgent(customer_id, physical_warehouse_id)<=0){
			throw new RuntimeException("updateReplenishment删除失败,受影响行数为0");
		}
		 
	}
	
	
	
	@Override
	public List<ConfigReplenishmentUrgent> selectConfigReplenishmentUrgentByCustomerId(Map<String, Object>map){
		return replenishmentUrgentDao.selectConfigReplenishmentUrgentByCustomerId(map);
	}
	
	
	@Override
	public Map<String,Object> updateReplenishmentUrgentByUpdateReplenishmentMap(Map<String, Object> updateReplenishmentMap,Map<String, Object> searchMap){

	Map<String,Object> resMap = new HashMap<String,Object>();
	
		updateReplenishmentMap.put("created_user", searchMap.get("created_user"));
		updateReplenishmentMap.put("last_updated_user", searchMap.get("last_updated_user"));
		updateReplenishmentMap.put("created_time", new Date());
		updateReplenishmentMap.put("last_updated_time",new Date());
		updateReplenishmentMap.put("physical_warehouse_id", searchMap.get("physical_warehouse_id"));
		
		replenishmentUrgentDao.updateReplenishmentUrgentByUpdateReplenishmentMap(updateReplenishmentMap);
	
	resMap.put("result", "success");
	resMap.put("note", "更新成功");
	return resMap;
	}
	
	@Override
	public ConfigReplenishmentUrgent selectReplenishmentUrgentIsExist(Integer customerId,Integer physical_warehouse_id){
		return replenishmentUrgentDao.selectReplenishmentUrgentIsExist(customerId,physical_warehouse_id);
	}
	
	@Override
	public Map<String,Object> insertPartConfigReplenishmentUrgentByMap(Map<String, Object> updateReplenishmentMap,Map<String, Object> searchMap){
	
		Map<String,Object> resMap = new HashMap<String,Object>();
      
			updateReplenishmentMap.put("created_user", searchMap.get("created_user"));
			updateReplenishmentMap.put("last_updated_user", searchMap.get("last_updated_user"));
			updateReplenishmentMap.put("created_time", new Date());
			updateReplenishmentMap.put("last_updated_time",new Date());
			updateReplenishmentMap.put("physical_warehouse_id", searchMap.get("physical_warehouse_id"));
			
			replenishmentUrgentDao.insertConfigReplenishmentUrgentByMap(updateReplenishmentMap);
        
			resMap.put("result", "success");
			resMap.put("note", "插入成功");
		
		return resMap;
	}
	
	@Override
	public Map<String,Object> insertConfigReplenishmentUrgentByMap(Map<String, Object> updateReplenishmentMap,Map<String, Object> searchMap){
	
		Map<String,Object> resMap = new HashMap<String,Object>();
		 
			updateReplenishmentMap.put("created_user", searchMap.get("created_user"));
			updateReplenishmentMap.put("last_updated_user", searchMap.get("last_updated_user"));
			updateReplenishmentMap.put("created_time", new Date());
			updateReplenishmentMap.put("last_updated_time",new Date());
			updateReplenishmentMap.put("physical_warehouse_id", searchMap.get("physical_warehouse_id"));
			
			replenishmentUrgentDao.insertConfigReplenishmentUrgentByMap(updateReplenishmentMap);
		 
			resMap.put("result", "success");
			resMap.put("note", "插入成功");
		
		return resMap;
	}
	
}