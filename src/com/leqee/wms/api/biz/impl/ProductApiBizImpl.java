package com.leqee.wms.api.biz.impl;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;





import com.leqee.wms.api.biz.ProductApiBiz;
import com.leqee.wms.api.convert.ProductRequest2ProductConvert;
import com.leqee.wms.api.request.SyncProductRequest;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.util.WorkerUtil;

/**
 * @author hzhang1
 * @date 2016-2-26
 * @version 1.0.0
 */
@Service
public class ProductApiBizImpl implements ProductApiBiz {
	private Logger logger = Logger.getLogger(ProductApiBizImpl.class);
	@Autowired
	ProductDao productDao;
	@Autowired
	ProductRequest2ProductConvert productRequest2Product;
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;
	
	@Override
	public HashMap<String,Object> syncProduct(SyncProductRequest productRequest,Integer customerId) {
		HashMap<String,Object> resMap = new HashMap<String,Object>();
		Product product = new Product();
		// 1.将productRequest 转换为 product
		try {
			product = productRequest2Product.covertToTargetEntity(productRequest);
			product.setCustomer_id(customerId);
		} catch (Exception e) {
			logger.error("syncproduct err：", e);
			String errorStr = e.getMessage() + "";
			String msg = errorStr.substring(0, errorStr.indexOf("("));
			resMap.put("result", "failure");
			resMap.put("error_code", "40005");
			resMap.put("msg", "json转换成对象失败:"+msg);
			return resMap;
		}
		
		// 2.商品验证 & 判断需要做哪种操作
		if(WorkerUtil.isNullOrEmpty(product.getBarcode())){
			resMap.put("result", "failure");
			resMap.put("error_code", "40006");
			resMap.put("msg", "商品没有扫描码");
			return resMap;
		}

		HashMap<String,Object> returnMap = checkProduct(product);
		
		// 3.根据返回执行不同的操作
		if("failure".equals(returnMap.get("result").toString())){
			return returnMap;
		}else if("insert".equals(returnMap.get("result").toString())){
			productDao.insert(product);
		}else if("update".equals(returnMap.get("result").toString())){
			logger.info( "is_serial: " + product.getIs_serial());
			product.setProduct_id(Integer.parseInt(returnMap.get("productId").toString()));
			productDao.update(product);
		}
		
		logger.info("The id return from insert table product："+product.getProduct_id());
		resMap.put("result", "success");
		resMap.put("productId", product.getProduct_id());
		resMap.put("skuCode", product.getSku_code());
		return resMap;
	}
	
	@Override
	public HashMap<String, Object> checkProduct(Product product) {
		
		HashMap<String,Object> map = new HashMap<String,Object>();
		
		
		String skuCode = product.getSku_code();
		// 1.先对product进行参数判断
		if(WorkerUtil.isNullOrEmpty(skuCode)){
			map.put("result", "failure");
			map.put("error_code", "40001");
			map.put("msg", "商品skuCode为空");
			return map;
		}
		
		if(WorkerUtil.isNullOrEmpty(product.getProduct_name())){
			map.put("result", "failure");
			map.put("error_code", "40002");
			map.put("msg", "商品名称为空");
			return map;
		}
		
		if(WorkerUtil.isNullOrEmpty(product.getIs_delete())){
			map.put("result", "failure");
			map.put("error_code", "40003");
			map.put("msg", "操作商品是否删除字段为空");
			return map;
		}
		
		// 2.根据传进来的product的skuCode来检查是否已存在商品
		Product product2 = productDao.selectBySkuCode(skuCode);
		if(WorkerUtil.isNullOrEmpty(product2)){
			map.put("result", "insert");
		}else{
			map.put("result", "update");
			map.put("productId", product2.getProduct_id());
		}
		
		// 3.返回结果map结果
		return map;
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
