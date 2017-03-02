package com.leqee.wms.api.biz.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.api.biz.InventoryItemApiBiz;
import com.leqee.wms.api.request.GetFrozenRequest;
import com.leqee.wms.api.request.GetInventoryRequest;
import com.leqee.wms.api.request.GetVarianceImproveTaskListRequest;
import com.leqee.wms.api.response.domain.FrozenDetailResDomain;
import com.leqee.wms.api.response.domain.InventoryDetailResDomain;
import com.leqee.wms.api.response.domain.InventorySummaryResDomain;
import com.leqee.wms.api.response.domain.OrderInfoResDomain;
import com.leqee.wms.api.response.domain.VarianceImproveTaskResDomain;
import com.leqee.wms.dao.ConfigDao;
import com.leqee.wms.dao.InventoryItemDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.TaskImproveDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.entity.ProductPrepackage;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.util.WorkerUtil;

/**
 * 库存子项API业务实现方法
 * @author qyyao
 * @date 2016-3-1
 * @version 1.0
 */
@SuppressWarnings("serial")
@Service
public class InventoryItemApiBizImpl implements InventoryItemApiBiz {
	private Logger logger = Logger.getLogger(InventoryItemApiBizImpl.class);

	public static List<String>  getInventoryTypes =  new ArrayList<String>(){{add("SUMMARY"); add("DETAIL"); }} ;
	
	@Autowired
	InventoryItemDao inventoryItemDao;
	@Autowired
	ProductDao productDao;
	
	@Autowired
	ProductLocationDao productLcoationDao;
	@Resource(name = "sqlSessionSlave")
	public void setProductLcoationDaoSlave(SqlSession sqlSession) {
	  this.productLcoationDao = sqlSession.getMapper(ProductLocationDao.class);
	}
	@Autowired
	WarehouseDao warehouseDao;
	
	private ConfigDao configDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setconfigDaoSlave(SqlSession sqlSession) {
	  this.configDaoSlave = sqlSession.getMapper(ConfigDao.class);
	}
	
	@Autowired
	TaskImproveDao taskImproveDao;
	
	
	// 默认页码和页数
	public static final int DEFALUT_PAGE_NO = 1;  
	public static final int DEFALUT_PAGE_SIZE = 100;

	@Override
	public Map<String, Object> getInventory(
			GetInventoryRequest getInventoryRequest, Integer customerId) {
		//返回Map
		Map<String,Object> resMap = new HashMap<String,Object>();
		String skuCode = getInventoryRequest.getSku_code();
		Integer warehouseId = getInventoryRequest.getWarehouse_id();
		String type = getInventoryRequest.getType();
		
		//1、Request参数校验
		if(WorkerUtil.isNullOrEmpty(type)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40001");
			resMap.put("msg", "查询库存类型type为空");
			return resMap;
		}
		if( !getInventoryTypes.contains(type) ){
			resMap.put("result", "failure");
			resMap.put("error_code", "40002");
			resMap.put("msg", "查询库存类型错误，类型只能为"+getInventoryTypes);
			return resMap;
		}
		
		Product product = null;
		Warehouse warehouse = null;
				
		if(!WorkerUtil.isNullOrEmpty(skuCode)){
			product = productDao.selectBySkuCode(skuCode);
			if(WorkerUtil.isNullOrEmpty(product)){
				resMap.put("result", "failure");
				resMap.put("error_code", "40003");
				resMap.put("msg", "wms不存在sku_code为"+skuCode+"的商品");
				return resMap;
			}
		}
		
		if(!WorkerUtil.isNullOrEmpty(warehouseId)){
			warehouse = warehouseDao.selectByWarehouseId(warehouseId);
			if(WorkerUtil.isNullOrEmpty(product)){
				resMap.put("result", "failure");
				resMap.put("error_code", "40004");
				resMap.put("msg", "wms不存在warehouse_Id为"+warehouseId+"的仓库");
				return resMap;
			}
		}
		
		
		// 2，根据查询类型查询库存并返回
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customerId", customerId );
		paramsMap.put("skuCode", skuCode );
		paramsMap.put("warehouseId", warehouseId);
		
		//查询类型为SUMMARY时
		if( GetInventoryRequest.TYPE_SUMMARY.equals(getInventoryRequest.getType()) ){
			List<InventorySummaryResDomain> inventorySummaryResDomains = inventoryItemDao.selectInventorySummarys(paramsMap);
			resMap.put("inventorySummaryResDomains", inventorySummaryResDomains);
		}
		//查询类型为DETAIL时
		if( GetInventoryRequest.TYPE_DETAIL.equals(getInventoryRequest.getType()) ){
			List<InventoryDetailResDomain> inventoryDetailResDomains = inventoryItemDao.selectDetailResDomains(paramsMap);
			resMap.put("inventoryDetailResDomains", inventoryDetailResDomains);
		}
		
		resMap.put("result", "success");
		
		return resMap;
	}

	@Override
	public List<ProductLocation> getNeedFreezeProductLocation(int physical_warehouse_id,int customer_id) {
		List<ProductLocation> list=new ArrayList<ProductLocation>();
		
		list=productLcoationDao.selectAll(physical_warehouse_id,customer_id,"('QUALITY_CHECK_LOCATION','VARIANCE_ADD_LOCATION','VARIANCE_MINUS_LOCATION')");
		Map<String,ProductLocation> map=new HashMap<String,ProductLocation>();
		//合并 同一物理仓 同属性的  同商品  盘盈-盘亏-质检
		for(ProductLocation pl:list){
			String key=pl.getPhysical_warehouse_id()+"_"+pl.getProduct_id()+"_"+pl.getStatus();
			int num=pl.getQty_total();
			//盘盈区要去掉
			if(!pl.getLocation_type().equalsIgnoreCase("VARIANCE_ADD_LOCATION")){
				num=0-num;
			}
			ProductLocation plnew=new ProductLocation();
			if(null!=map.get(key)){
				plnew=map.get(key);
				plnew.setQty_total(plnew.getQty_total()+num);
			}
			else {
				plnew.setPhysical_warehouse_id(pl.getPhysical_warehouse_id());
				plnew.setProduct_id(pl.getProduct_id());
				plnew.setStatus(pl.getStatus());
				plnew.setSku_code(pl.getSku_code());
				plnew.setLocation_type(pl.getLocation_type());
				plnew.setQty_total(num);
				plnew.setCustomer_id(pl.getCustomer_id());
				map.put(key,plnew);
			}			
		}
		//只保留最终为负数的
		List<ProductLocation> list2=new ArrayList<ProductLocation>();
		for(ProductLocation pl:map.values()){
			if(pl.getQty_total()<0){
				list2.add(pl);
			}
		}
		return list2;
	}

	@Override
	public List<ProductLocation> getNeedFreezeProductLocation2(int physical_warehouse_id,int customer_id) {
		List<ProductLocation> list=new ArrayList<ProductLocation>();
		List<ProductLocation> listAfterChange=new ArrayList<ProductLocation>();
		
		List<ProductLocation> listException = new ArrayList<ProductLocation>();
		List<ProductLocation> listCancelOutShelf = new ArrayList<ProductLocation>();
		
		String frezenQuality=configDaoSlave.getConfigValueByFrezen(physical_warehouse_id,customer_id,"FREZEN_QUALITY");
		
		String frezenReturnNormal=configDaoSlave.getConfigValueByFrezen(physical_warehouse_id,customer_id,"FREZEN_RETURN_NORMAL");
		
		String frezenReturn=configDaoSlave.getConfigValueByFrezen(physical_warehouse_id,customer_id,"FREZEN_RETURN");
		
		//库位上冻结的库存：为了防止重复计算  就只考虑拣货区域（件拣货区，箱拣货区，存储区，return区）
		String frezenException=configDaoSlave.getConfigValueByFrezen(physical_warehouse_id,customer_id,"FREZEN_EXCEPTION");
		
		String frezenCancelOutShelf=configDaoSlave.getConfigValueByFrezen(physical_warehouse_id,customer_id,"FREZEN_CANCEL_OUT_SHELF");
		
		List<String> locationTypeList=new ArrayList<String>();
		locationTypeList.add("VARIANCE_ADD_LOCATION");
		locationTypeList.add("VARIANCE_MINUS_LOCATION");
		
		if("1".equalsIgnoreCase(frezenQuality)){
			locationTypeList.add("QUALITY_CHECK_LOCATION");
		}

		if("1".equalsIgnoreCase(frezenReturn)){
			locationTypeList.add("RETURN_LOCATION");
		}
		
		if("1".equalsIgnoreCase(frezenReturnNormal)){
			locationTypeList.add("RETURN_NORMAL_LOCATION");
		}
		
		if ("1".equalsIgnoreCase(frezenException)){
			//为了其他的计算  将数字从qty_frezen  -->（转移到）  qty_total  字段 
			listException = productLcoationDao.selectAllExceptionProductLocationList(physical_warehouse_id, customer_id,"('PIECE_PICK_LOCATION','RETURN_LOCATION','BOX_PICK_LOCATION','STOCK_LOCATION')");
		}
		
		if ("1".equalsIgnoreCase(frezenCancelOutShelf)){
			//为了其他的计算  将数字从qty_frezen  -->（转移到）  qty_total  字段 
			listCancelOutShelf = productLcoationDao.selectAllCancelOutShelfProductLocationList(physical_warehouse_id, customer_id);
		}
		
		list=productLcoationDao.selectAllByLocationTypeList(physical_warehouse_id,customer_id,locationTypeList);
		
		list.addAll(listException);
		list.addAll(listCancelOutShelf);
		
		//list=productLcoationDao.selectAll(physical_warehouse_id,customer_id,"('QUALITY_CHECK_LOCATION','VARIANCE_ADD_LOCATION','VARIANCE_MINUS_LOCATION')");
		Map<String,ProductLocation> map=new HashMap<String,ProductLocation>();
		//合并 同一物理仓 同属性的  同商品  盘盈-盘亏-质检
		
		
		//记录套餐商品id
		List<Integer> prepackIdList=new ArrayList<Integer>();
		for(ProductLocation pl:list){
			if("PREPACKAGE".equalsIgnoreCase(pl.getProduct_type())){
				prepackIdList.add(pl.getProduct_id());
			}
		}
		
		//查出对应的套餐组成信息
		List<ProductPrepackage> productPrepackageList = new ArrayList<ProductPrepackage>();
		
		if(!WorkerUtil.isNullOrEmpty(prepackIdList)){
			productPrepackageList=productLcoationDao.selectAllProductPrepackagelist(prepackIdList);
		}
		
		//组成  套餐id-productList(各个组件商品的信息)的Map
		Map<Integer,List<ProductPrepackage>> productPrepackageMap =new HashMap<Integer,List<ProductPrepackage>>();
		if(!WorkerUtil.isNullOrEmpty(productPrepackageList)){
			
			for(ProductPrepackage pp:productPrepackageList){
				if(null==productPrepackageMap.get(pp.getPrepackage_product_id())){
					List<ProductPrepackage> temp = new ArrayList<ProductPrepackage>();
					temp.add(pp);
					productPrepackageMap.put(pp.getPrepackage_product_id(), temp);
				}else{
					List<ProductPrepackage> temp = productPrepackageMap.get(pp.getPrepackage_product_id());
					temp.add(pp);
				}
			}
			
		}
		
		for(ProductLocation pl:list){
			if("PREPACKAGE".equalsIgnoreCase(pl.getProduct_type())){
				if(null!=productPrepackageMap.get(pl.getProduct_id())){
					List<ProductPrepackage> temp = productPrepackageMap.get(pl.getProduct_id());
					//进行套餐转换成单品
					for(ProductPrepackage pp:temp){
						ProductLocation plnew=new ProductLocation();
						plnew.setPhysical_warehouse_id(pl.getPhysical_warehouse_id());
						plnew.setCustomer_id(pl.getCustomer_id());
						plnew.setQty_total(pl.getQty_total()*pp.getNumber());
						plnew.setStatus(pl.getStatus());
						plnew.setSku_code(pp.getSku_code());
						plnew.setBarcode(pp.getBarcode());
						plnew.setProduct_id(pp.getComponent_product_id());
						plnew.setProduct_name(pp.getProduct_name());
						plnew.setLocation_type(pl.getLocation_type());
						plnew.setWarehouse_id(pl.getWarehouse_id());
						listAfterChange.add(plnew);
					}
				}
				else{
					//仍然传递套餐
					listAfterChange.add(pl);
				}
				
			}
			else{
				//单品不需转换
				listAfterChange.add(pl);
			}
		}
		
		
		for(ProductLocation pl:listAfterChange){
			String key=pl.getPhysical_warehouse_id()+"_"+pl.getProduct_id()+"_"+pl.getStatus()+"_"+pl.getWarehouse_id();
			int num=pl.getQty_total();
			//盘盈区要去掉
			if(!pl.getLocation_type().equalsIgnoreCase("VARIANCE_ADD_LOCATION")){
				num=0-num;
			}
			ProductLocation plnew=new ProductLocation();
			if(null!=map.get(key)){
				plnew=map.get(key);
				plnew.setQty_total(plnew.getQty_total()+num);
			}
			else {
				plnew.setPhysical_warehouse_id(pl.getPhysical_warehouse_id());
				plnew.setProduct_id(pl.getProduct_id());
				plnew.setStatus(pl.getStatus());
				plnew.setSku_code(pl.getSku_code());
				plnew.setLocation_type(pl.getLocation_type());
				plnew.setQty_total(num);
				plnew.setCustomer_id(pl.getCustomer_id());
				plnew.setWarehouse_id(pl.getWarehouse_id());
				map.put(key,plnew);
			}			
		}
		
		
		//只保留最终为负数的
		List<ProductLocation> list2=new ArrayList<ProductLocation>();
		for(ProductLocation pl:map.values()){
				if(pl.getQty_total()<0 ){
					list2.add(pl);
			}
		}
		return list2;
	}

	
	@Override
	public Map<String, Object> getFrozen(GetFrozenRequest getFrozenRequest,
			Integer customerId) {
		
		
		//返回Map
		Map<String,Object> resMap = new HashMap<String,Object>();
		String skuCode = getFrozenRequest.getSku_code();
		Integer physicalWarehouseId = getFrozenRequest.getPhysical_warehouse_id();
		
		//1、Request参数校验
		
		Product product = null;
		Warehouse warehouse = null;
				
		if(!WorkerUtil.isNullOrEmpty(skuCode)){
			product = productDao.selectBySkuCode(skuCode);
			if(WorkerUtil.isNullOrEmpty(product)){
				resMap.put("result", "failure");
				resMap.put("error_code", "40003");
				resMap.put("msg", "wms不存在sku_code为"+skuCode+"的商品");
				return resMap;
			}
		}
		
		if(!WorkerUtil.isNullOrEmpty(physicalWarehouseId)){
			warehouse = warehouseDao.selectByWarehouseId(physicalWarehouseId);
			if(WorkerUtil.isNullOrEmpty(warehouse)){
				resMap.put("result", "failure");
				resMap.put("error_code", "40004");
				resMap.put("msg", "wms不存在physical_warehouse_id为"+physicalWarehouseId+"的仓库");
				return resMap;
			}
		}else{
			physicalWarehouseId = 0;
		}
		
		
		// 2，查询冻结并返回
		List<FrozenDetailResDomain> frozenDetailResDomains = new ArrayList<FrozenDetailResDomain>();
		List<ProductLocation> productLocationList= getNeedFreezeProductLocation2(physicalWarehouseId,customerId);
		
		if(!WorkerUtil.isNullOrEmpty(productLocationList)){
			for(ProductLocation productLocation : productLocationList ){
				if(!WorkerUtil.isNullOrEmpty(skuCode)){
					if(skuCode.equals( productLocation.getSku_code())){
						FrozenDetailResDomain frozenDetailResDomain = new FrozenDetailResDomain();
						frozenDetailResDomain.setCustomer_id(productLocation.getCustomer_id());
						frozenDetailResDomain.setPhysical_warehouse_id(productLocation.getPhysical_warehouse_id());
	                    frozenDetailResDomain.setWarehouse_id(productLocation.getWarehouse_id());
						frozenDetailResDomain.setQuantity(  - productLocation.getQty_total());
						frozenDetailResDomain.setSku_code(productLocation.getSku_code());
						frozenDetailResDomain.setStatus_id(productLocation.getStatus());
						frozenDetailResDomains.add(frozenDetailResDomain);
					}
				}else{
					FrozenDetailResDomain frozenDetailResDomain = new FrozenDetailResDomain();
					frozenDetailResDomain.setCustomer_id(productLocation.getCustomer_id());
					frozenDetailResDomain.setPhysical_warehouse_id(productLocation.getPhysical_warehouse_id());
                    frozenDetailResDomain.setWarehouse_id(productLocation.getWarehouse_id());
					frozenDetailResDomain.setQuantity(- productLocation.getQty_total());
					frozenDetailResDomain.setSku_code(productLocation.getSku_code());
					frozenDetailResDomain.setStatus_id(productLocation.getStatus());
					frozenDetailResDomains.add(frozenDetailResDomain);
				}
			}
		}
		resMap.put("frozenDetailResDomains", frozenDetailResDomains);
		resMap.put("result", "success");
		
		return resMap;
		
	}

//	getVarianceImproveTaskListRequest
	@Override
	public Map<String, Object> getVarianceImproveTaskList(
			GetVarianceImproveTaskListRequest getVarianceImproveTaskListRequest,
			Integer customerId) {
		// 1.初始化订单列表查询的相关变量
		Map<String,Object> resMap = new HashMap<String,Object>();
		Date startModifiedTime = getVarianceImproveTaskListRequest.getStart_modified_time();
		Date endModifiedTime = getVarianceImproveTaskListRequest.getEnd_modified_time();
		Integer pageNo = getVarianceImproveTaskListRequest.getPage_no();
		Integer pageSize = getVarianceImproveTaskListRequest.getPage_size();
		
		
		// 2.验证各个参数的合法性
		if(WorkerUtil.isNullOrEmpty(startModifiedTime)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40001");
			resMap.put("msg", "start_modified_time起始时间为空" );
			return resMap;
		}
		if(WorkerUtil.isNullOrEmpty(endModifiedTime)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40002");
			resMap.put("msg", "end_modified_time结束时间为空" );
			return resMap;
		}
		
		long intervalModified = (endModifiedTime.getTime() - startModifiedTime.getTime()) / (1000l * 3600l * 24l );
		if(intervalModified > 30){
			resMap.put("result", "failure");
			resMap.put("error_code", "40003");
			resMap.put("msg", "end_modified_time和start_modified_time的时间间隔不能超过30天" );
			return resMap;
		}
		
	
		
		if(WorkerUtil.isNullOrEmpty(pageNo)){
			pageNo = DEFALUT_PAGE_NO;
		}
		if(WorkerUtil.isNullOrEmpty(pageSize)){
			pageSize = DEFALUT_PAGE_SIZE;
		}
		
		
		//3.获取响应订单列表
		int offset = pageSize * ( pageNo - 1);
		
		Map<String,Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customerId", customerId);
		paramsMap.put("startModifiedTime", startModifiedTime);
		paramsMap.put("endModifiedTime", endModifiedTime);
		paramsMap.put("offset", offset);
		paramsMap.put("rows", pageSize);
		
		List<VarianceImproveTaskResDomain>  varianceImproveTaskResDomainList = taskImproveDao.selectVarianceImproveTaskResDomainList(paramsMap);
		
		//4.返回订单列表
		resMap.put("total_count", varianceImproveTaskResDomainList != null ? varianceImproveTaskResDomainList.size(): 0 );
		resMap.put("varianceImproveTaskResDomainList", varianceImproveTaskResDomainList);
		
		resMap.put("result", "success");
		return resMap;
	}
	
}
