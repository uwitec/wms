package com.leqee.wms.api.biz.impl;

import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.Now;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;





import com.leqee.wms.api.biz.ProductApiBiz;
import com.leqee.wms.api.biz.RegionApiBiz;
import com.leqee.wms.api.biz.ShopApiBiz;
import com.leqee.wms.api.convert.ProductRequest2ProductConvert;
import com.leqee.wms.api.request.SyncProductRequest;
import com.leqee.wms.api.request.SyncRegionRequest;
import com.leqee.wms.api.request.SyncShopRequest;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.RegionDao;
import com.leqee.wms.dao.ShopDao;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.Region;
import com.leqee.wms.entity.Shop;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.util.WorkerUtil;

/**
 * @author hzhang1
 * @date 2016-2-26
 * @version 1.0.0
 */
@Service
public class ShopApiBizImpl implements ShopApiBiz {
	private Logger logger = Logger.getLogger(ShopApiBizImpl.class);
	@Autowired
	ShopDao shopDao;
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;
	
	@Override
	public HashMap<String,Object> syncShop(SyncShopRequest shopRequest) {
		HashMap<String,Object> resMap = new HashMap<String,Object>();
		int col = 0;
		Shop shop = new Shop();
		// 1.将SyncShopRequest 转换为 shop
		try {
			shop.setOms_shop_id(shopRequest.getOms_shop_id());
			shop.setCustomer_id(shopRequest.getCustomer_id());
			shop.setShop_name(shopRequest.getShop_name());
			shop.setCreated_user(shopRequest.getCreated_user());
			shop.setLast_updated_user(shopRequest.getLast_updated_user());
			shop.setCreated_time(new Date());
			shop.setLast_updated_time(new Date());
			
		} catch (Exception e) {
			logger.error("syncshop err：", e);
			String errorStr = e.getMessage().toString();
			String msg = errorStr.substring(0, errorStr.indexOf("("));
			resMap.put("result", "failure");
			resMap.put("error_code", "40005");
			resMap.put("msg", "json转换成对象失败:"+msg);
			return resMap;
		}
		
		if(WorkerUtil.isNullOrEmpty(shopDao.selectByShopId(shopRequest.getOms_shop_id()))){
			 col = shopDao.insertShop(shop);
		}else {
			if(WorkerUtil.isNullOrEmpty(shopDao.selectByShopName(shopRequest.getOms_shop_id(),shopRequest.getShop_name()))){
				 col = shopDao.updateShop(shop);
			}
		}
		if(col<=0){
			resMap.put("result", "failure");
			resMap.put("error_code", "40009");
			resMap.put("msg", "无需更新店铺信息");
		
		}else{
			resMap.put("result", "success");
			resMap.put("shopId", shop.getOms_shop_id());
			resMap.put("shopName", shop.getShop_name());
			}
		
		return resMap;
	}
	
	
}
