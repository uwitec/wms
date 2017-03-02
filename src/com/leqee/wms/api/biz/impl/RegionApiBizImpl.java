package com.leqee.wms.api.biz.impl;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;





import com.leqee.wms.api.biz.ProductApiBiz;
import com.leqee.wms.api.biz.RegionApiBiz;
import com.leqee.wms.api.convert.ProductRequest2ProductConvert;
import com.leqee.wms.api.request.SyncProductRequest;
import com.leqee.wms.api.request.SyncRegionRequest;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.RegionDao;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.Region;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.util.WorkerUtil;

/**
 * @author hzhang1
 * @date 2016-2-26
 * @version 1.0.0
 */
@Service
public class RegionApiBizImpl implements RegionApiBiz {
	private Logger logger = Logger.getLogger(RegionApiBizImpl.class);
	@Autowired
	RegionDao regionDao;
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;
	
	@Override
	public HashMap<String,Object> syncRegion(SyncRegionRequest regionRequest) {
		HashMap<String,Object> resMap = new HashMap<String,Object>();
		int col;
		Region region =new Region();
		// 1.将SyncRegionRequest 转换为 region
		try {
			region.setRegion_id(regionRequest.getRegion_id().shortValue());
			region.setRegion_name(regionRequest.getRegion_name());
			region.setParent_id(regionRequest.getParent_id().shortValue());
			region.setRegion_type(regionRequest.getRegion_type().shortValue());
			region.setAgency_id((short) 0);
			
		} catch (Exception e) {
			logger.error("syncregion err：", e);
			String errorStr = e.getMessage().toString();
			String msg = errorStr.substring(0, errorStr.indexOf("("));
			resMap.put("result", "failure");
			resMap.put("error_code", "40005");
			resMap.put("msg", "json转换成对象失败:"+msg);
			return resMap;
		}
		
		if(WorkerUtil.isNullOrEmpty(regionDao.selectRegionByRegionId(region.getRegion_id()))){
			 col = regionDao.insertRegion(region);
		}else{
			 col = regionDao.updateRegion(region);
		}
		if(col<=0){
			resMap.put("result", "failure");
		}else{
			resMap.put("result", "success");
			resMap.put("regionId", region.getRegion_id());
			resMap.put("regionName", region.getRegion_name());
			}
		
		return resMap;
	}
	
	
	public Integer getcustomerIdByAppKey(String app_key){
		// 1.验证app_key
		//System.out.println("app_key"+app_key);
		Integer customerId=0;
		WarehouseCustomer warehouseCustomer = warehouseCustomerDao.selectByAppKey(app_key);
		if(!WorkerUtil.isNullOrEmpty(warehouseCustomer)){
			customerId = warehouseCustomer.getCustomer_id();
		}
		return customerId;
	}
}
