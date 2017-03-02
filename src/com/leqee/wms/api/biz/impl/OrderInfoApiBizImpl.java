package com.leqee.wms.api.biz.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.crypto.Data;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.leqee.wms.api.biz.OrderInfoApiBiz;
import com.leqee.wms.api.biz.ShipmentApiBiz;
import com.leqee.wms.api.convert.SyncPurchaseOrderRequest2OrderInfoConvert;
import com.leqee.wms.api.convert.SyncRmaOrderRequest2OrderInfoConvert;
import com.leqee.wms.api.convert.SyncSaleOrderRequest2OrderInfoConvert;
import com.leqee.wms.api.convert.SyncVarianceOrderRequest2OrderInfoConvert;
import com.leqee.wms.api.request.AdjustPriceRequest;
import com.leqee.wms.api.request.CancelOrderRequest;
import com.leqee.wms.api.request.GetOrderListRequest;
import com.leqee.wms.api.request.GetOrderPrepacksRequest;
import com.leqee.wms.api.request.GetOrderShipmentListRequest;
import com.leqee.wms.api.request.GetPurchaseOrderRequest;
import com.leqee.wms.api.request.GetRmaOrderRequest;
import com.leqee.wms.api.request.GetSaleOrderRequest;
import com.leqee.wms.api.request.GetVarianceOrderRequest;
import com.leqee.wms.api.request.SyncOrderPrepackRequest;
import com.leqee.wms.api.request.SyncPurchaseOrderRequest;
import com.leqee.wms.api.request.SyncRmaOrderRequest;
import com.leqee.wms.api.request.SyncSaleOrderRequest;
import com.leqee.wms.api.request.SyncVarianceOrderRequest;
import com.leqee.wms.api.request.TerminalOrderRequest;
import com.leqee.wms.api.request.domain.OrderGoodsReqDomain;
import com.leqee.wms.api.request.domain.PrepackReqDomain;
import com.leqee.wms.api.request.domain.TerminalGoodsReqDomain;
import com.leqee.wms.api.response.domain.AdjustPriceResDomain;
import com.leqee.wms.api.response.domain.OmsOrderTransferCodeResDomain;
import com.leqee.wms.api.response.domain.OrderGoodsResDomain;
import com.leqee.wms.api.response.domain.OrderInfoResDomain;
import com.leqee.wms.api.response.domain.OrderPackboxResDomain;
import com.leqee.wms.api.response.domain.OrderShipmentResDomain;
import com.leqee.wms.api.response.domain.PackBoxResDomain;
import com.leqee.wms.api.response.domain.ShipmentDetailResDomain;
import com.leqee.wms.api.response.domain.ShipmentResDomain;
import com.leqee.wms.api.util.WorkerUtil;
import com.leqee.wms.biz.OrderPrepackBiz;
import com.leqee.wms.biz.ProductBiz;
import com.leqee.wms.biz.ProductLocationBiz;
import com.leqee.wms.biz.ReserveOrderInventoryBiz;
import com.leqee.wms.biz.ShipmentBiz;
import com.leqee.wms.dao.ConfigDao;
import com.leqee.wms.dao.InventoryDao;
import com.leqee.wms.dao.InventoryItemDao;
import com.leqee.wms.dao.InventoryItemDetailDao;
import com.leqee.wms.dao.InventoryItemValueHistoryDao;
import com.leqee.wms.dao.LabelAcceptDao;
import com.leqee.wms.dao.OmsOrderTransferCodeDao;
import com.leqee.wms.dao.OrderGoodsDao;
import com.leqee.wms.dao.OrderGoodsOmsDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.OrderPrepackDao;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.ProductPrepackageDao;
import com.leqee.wms.dao.ShipmentDao;
import com.leqee.wms.dao.ShippingDao;
import com.leqee.wms.dao.ShippingWarehouseMappingDao;
import com.leqee.wms.dao.UserActionOrderDao;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.Config;
import com.leqee.wms.entity.InventoryItem;
import com.leqee.wms.entity.InventoryItemDetail;
import com.leqee.wms.entity.InventoryItemValueHistory;
import com.leqee.wms.entity.LabelAccept;
import com.leqee.wms.entity.OmsOrderTransferCode;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.entity.OrderGoodsOms;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.OrderPrepack;
import com.leqee.wms.entity.OrderProcess;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ProductPrepackage;
import com.leqee.wms.entity.Shipment;
import com.leqee.wms.entity.ShipmentDetail;
import com.leqee.wms.entity.Shipping;
import com.leqee.wms.entity.ShippingWarehouseMapping;
import com.leqee.wms.entity.UserActionOrder;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;

/**
 * @author hzhang1
 * @date 2016-2-25
 * @version 1.0.0
 */
@SuppressWarnings("serial")
@Service
public class OrderInfoApiBizImpl implements OrderInfoApiBiz{
	private Logger logger = Logger.getLogger(OrderInfoApiBizImpl.class);
	//Dao
	@Autowired
	ProductDao productDao;
	@Autowired
	OrderInfoDao orderInfoDao;
	
	
	ShipmentDao shipmentDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setShipmentDaoSlave(SqlSession sqlSession) {
		this.shipmentDaoSlave = sqlSession.getMapper(ShipmentDao.class);
	}
	OrderInfoDao orderInfoDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setOrderInfoDaoSlave(SqlSession sqlSession) {
	  this.orderInfoDaoSlave = sqlSession.getMapper(OrderInfoDao.class);
	}
	
	OrderGoodsDao orderGoodsDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setOrderGoodsDaoSlave(SqlSession sqlSession) {
	  this.orderGoodsDaoSlave = sqlSession.getMapper(OrderGoodsDao.class);
	}
	
	OrderGoodsOmsDao orderGoodsOmsDaoSlave; 
	@Resource(name = "sqlSessionSlave")
	public void setOrderGoodsOmsDaoSlave(SqlSession sqlSession) {
		this.orderGoodsOmsDaoSlave = sqlSession.getMapper(OrderGoodsOmsDao.class);
	}
	
	ShippingDao shippingDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setShippingDaoSlave(SqlSession sqlSession) {
		this.shippingDaoSlave = sqlSession.getMapper(ShippingDao.class);
	}
	
	ProductDao productDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setProductDaoSlave(SqlSession sqlSession) {
		this.productDaoSlave = sqlSession.getMapper(ProductDao.class);
	}
	
	InventoryItemDetailDao inventoryItemDetailDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setInventoryItemDetailDaoSlave(SqlSession sqlSession) {
		this.inventoryItemDetailDaoSlave = sqlSession.getMapper(InventoryItemDetailDao.class);
	}
	
	InventoryItemDao inventoryItemDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setInventoryItemDaoSlave(SqlSession sqlSession) {
		this.inventoryItemDaoSlave = sqlSession.getMapper(InventoryItemDao.class);
	}
	
	OmsOrderTransferCodeDao omsOrderTransferCodeDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setOmsOrderTransferCodeDaoSlave(SqlSession sqlSession) {
		this.omsOrderTransferCodeDaoSlave = sqlSession.getMapper(OmsOrderTransferCodeDao.class);
	}
	
	OrderProcessDao orderProcessDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setOrderProcessDaoSlave(SqlSession sqlSession) {
		this.orderProcessDaoSlave = sqlSession.getMapper(OrderProcessDao.class);
	}
	
	
	@Autowired
	OrderGoodsDao orderGoodsDao;
	@Autowired
	WarehouseDao warehouseDao;
	@Autowired
	OrderProcessDao orderProcessDao;
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;
	@Autowired
	InventoryItemDao inventoryItemDao;
	@Autowired
	InventoryItemDetailDao inventoryItemDetailDao;
	@Autowired
	InventoryDao inventoryDao;
	@Autowired
	ShippingDao shippingDao;
	@Autowired
	InventoryItemValueHistoryDao inventoryItemValueHistoryDao;
	@Autowired
	UserActionOrderDao userActionOrderDao;
	@Autowired
	ShippingWarehouseMappingDao shippingWarehouseMappingDao;
	@Autowired
	ShipmentDao shipmentDao;
	@Autowired
	OmsOrderTransferCodeDao omsOrderTransferCodeDao;
	@Autowired
	OrderPrepackDao orderPrepackDao;
	@Autowired
	ProductPrepackageDao productPrepackageDao;
	
	//Biz
	@Autowired
	ShipmentApiBiz shipmentApiBiz;
	@Autowired
	OrderPrepackBiz orderPrepackBiz;
	@Autowired
	ProductBiz productBiz;
	
	@Autowired
	ReserveOrderInventoryBiz reserveOrderInventoryBiz;
	
	@Autowired
	ShipmentBiz shipmentBiz;
	
	@Autowired
	ProductLocationBiz productLocationBiz;
	//Convert
	@Autowired
	SyncPurchaseOrderRequest2OrderInfoConvert syncPurchaseOrderRequest2OrderInfoConvert;
	@Autowired
	SyncSaleOrderRequest2OrderInfoConvert syncSaleOrderRequest2OrderInfoConvert;
	@Autowired
	SyncRmaOrderRequest2OrderInfoConvert syncRmaOrderRequest2OrderInfoConvert;
	@Autowired
	SyncVarianceOrderRequest2OrderInfoConvert syncVarianceOrderRequest2OrderInfoConvert;
	@Autowired
	LabelAcceptDao labelAcceptDao;
	@Autowired
	OrderGoodsOmsDao orderGoodsOmsDao;
	@Autowired
	ConfigDao configDao;
	
	
	public  static List<String>  synvarianceOrderTypes =  new ArrayList<String>(){{add("VARIANCE_ADD"); add("VARIANCE_MINUS"); }} ;
	public  static List<String>  syncSaleOrderTypes =  new ArrayList<String>(){{add("SALE");  add("SUPPLIER_RETURN"); }} ;
	public  static List<String>  syncStatusIds =  new ArrayList<String>(){{add("NORMAL"); add("DEFECTIVE"); }} ;  
	public  static List<String>  cancelOrderTypes =  new ArrayList<String>(){{add(OrderInfo.ORDER_TYPE_SALE);  add(OrderInfo.ORDER_TYPE_PURCHASE);  add(OrderInfo.ORDER_TYPE_SUPPLIER_RETURN);add(OrderInfo.ORDER_TYPE_RETURN);   }} ;
	public  static List<String>  CannotCancelOrderStatusesSale =  new ArrayList<String>(){{ add(OrderInfo.ORDER_STATUS_DELEVERED); add(OrderInfo.ORDER_STATUS_FULFILLED);  }} ;
	public  static List<String>  CannotCancelOrderStatusesPurchase =  new ArrayList<String>(){{ add(OrderInfo.ORDER_STATUS_FULFILLED);add(OrderInfo.ORDER_STATUS_ABORTED); }} ;
	public  static List<String>  CannotCancelOrderStatusesSupplierReturn =  new ArrayList<String>(){{ add(OrderInfo.ORDER_STATUS_FULFILLED); }} ;
	public  static List<String>  CannotCancelOrderStatusesReturn =  new ArrayList<String>(){{ add(OrderInfo.ORDER_STATUS_FULFILLED); }} ;
	public  static List<String>  getSaleOrderTypes =  new ArrayList<String>(){{add("SALE"); add("SUPPLIER_RETURN"); }} ;
	public  static List<String>  getOrderListOrderTypes =  new ArrayList<String>(){
		{
			add(OrderInfo.ORDER_TYPE_SALE); 
			add(OrderInfo.ORDER_TYPE_RETURN);
			add(OrderInfo.ORDER_TYPE_PURCHASE);
			add(OrderInfo.ORDER_TYPE_SUPPLIER_RETURN);
			add(OrderInfo.ORDER_TYPE_VARIANCE_ADD);
			add(OrderInfo.ORDER_TYPE_VARIANCE_MINUS);
		}
	} ;
	public  static List<String>  getOrderListOrderStatuses =  new ArrayList<String>(){
		{
			add(OrderInfo.ORDER_STATUS_ACCEPT); 
			add(OrderInfo.ORDER_STATUS_BATCH_PICK); 
			add(OrderInfo.ORDER_STATUS_CANCEL); 
			add(OrderInfo.ORDER_STATUS_DELEVERED); 
			add(OrderInfo.ORDER_STATUS_ABORTED); 
			add(OrderInfo.ORDER_STATUS_EXCEPTION); 
			add(OrderInfo.ORDER_STATUS_FULFILLED); 
			add(OrderInfo.ORDER_STATUS_IN_PROCESS); 
			add(OrderInfo.ORDER_STATUS_NORMAL); 
			add(OrderInfo.ORDER_STATUS_ON_SHIP); 
			add(OrderInfo.ORDER_STATUS_PICKING); 
			add(OrderInfo.ORDER_STATUS_RECHECKED); 
		}
	} ;
	public static List<String> terminalOrderStatus = new ArrayList<String>(){
		{
			add(OrderInfo.ORDER_STATUS_ACCEPT);
			add(OrderInfo.ORDER_STATUS_IN_PROCESS);
			add(OrderInfo.ORDER_STATUS_ON_SHIP);
//			add(OrderInfo.ORDER_STATUS_FULFILLED);    //已完结不能中止
		}
	};
	
	// 默认页码和页数
	public static final int DEFALUT_PAGE_NO = 1;  
	public static final int DEFALUT_PAGE_SIZE = 100;
	
	/************************************************************************
	 * API 采购订单接口入库专用方法
	 * @author hzhang1
	 ***********************************************************************/
	public Map<String,Object> syncPurchaseOrder(SyncPurchaseOrderRequest syncPurchaseOrderRequest,Integer customerId){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		// 1.验证数据的合理性
		Integer warehouseId = syncPurchaseOrderRequest.getWarehouse_id();
		String omsOrderSn = syncPurchaseOrderRequest.getOms_order_sn();
		Map<String,Object> returnMap = checkOrderInfoRequest(warehouseId,omsOrderSn,syncPurchaseOrderRequest.getOrderGoodsReqDomainList());
		
		if("failure".equals(returnMap.get("result").toString())){
			return returnMap;
		}
		
		// 2.将OrderInfoRequest 转换为 OrderInfo 对象
		OrderInfo orderInfo = syncPurchaseOrderRequest2OrderInfoConvert.covertToTargetEntity(syncPurchaseOrderRequest);
		orderInfo.setCustomer_id(customerId);
		orderInfo.setPhysical_warehouse_id(Integer.parseInt(returnMap.get("physical_warehouse_id").toString()));
		
		// 3.验证订单其他数据
		/**xhchen 2016-08-16 新的-dt -dc订单没有这三个字段，所以注释掉
		if(WorkerUtil.isNullOrEmpty(orderInfo.getProvider_code())){
			resMap.put("result", "failure");
			resMap.put("error_code", "40006");
			resMap.put("msg", "采购订单供应商id为空");
			return resMap;
		}
		if(WorkerUtil.isNullOrEmpty(orderInfo.getProvider_name())){
			resMap.put("result", "failure");
			resMap.put("error_code", "40007");
			resMap.put("msg", "采购订单供应商名称为空");
			return resMap;
		}
		if(WorkerUtil.isNullOrEmpty(orderInfo.getProvider_order_type())){
			resMap.put("result", "failure");
			resMap.put("error_code", "40008");
			resMap.put("msg", "采购订单供应商供货类型为空");
			return resMap;
		}
		*/
		
		if(!WorkerUtil.isNullOrEmpty(orderInfo.getOrderGoodsList())){
			for(OrderGoods orderGoods:orderInfo.getOrderGoodsList()){
				//TODO 采供批次号是否需要判断，待确认
//				if(WorkerUtil.isNullOrEmpty(orderGoods.getBatch_sn())){
//					resMap.put("result", "failure");
//					resMap.put("error_code", "40010");
//					resMap.put("msg", "批次号batch_sn为空");
//					return resMap;
//				}
				if(WorkerUtil.isNullOrEmpty(orderGoods.getStatus_id())){
					resMap.put("result", "failure");
					resMap.put("error_code", "40011");
					resMap.put("msg", "新旧状态维护有误");
					return resMap;
				}
				
				if(!syncStatusIds.contains(orderGoods.getStatus_id())){
					resMap.put("result", "failure");
					resMap.put("error_code", "40011");
					resMap.put("msg", "新旧状态维护有误");
					return resMap;
				}
			}
		}
		
		// 4.插入数据库
		orderInfoDao.insert(orderInfo);
		Integer orderId = orderInfo.getOrder_id();
		List<OrderGoods> orderGoodsList = orderInfo.getOrderGoodsList();
		for(OrderGoods orderGood:orderGoodsList){
			orderGood.setOrder_id(orderId);
			orderGood.setWarehouse_id(orderInfo.getWarehouse_id());
			orderGood.setCustomer_id(customerId);
			orderGood.setStatus_id("NORMAL");
			orderGoodsDao.insert(orderGood);
		}
		resMap.put("orderId", orderId);
		resMap.put("omsOrderSn", orderInfo.getOms_order_sn());
		resMap.put("result", "success");
		return resMap;
	}
	
	
	/**
	 * -V订单同步
	 * @author hzhang1
	 */
	public Map<String,Object> syncVarianceOrder(SyncVarianceOrderRequest syncVarianceOrderRequest,Integer customerId){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		// 1.验证数据的合理性
		Integer warehouseId = syncVarianceOrderRequest.getWarehouse_id();
		String omsOrderSn = syncVarianceOrderRequest.getOms_order_sn();
		Map<String,Object> returnMap = checkOrderInfoRequest(warehouseId,omsOrderSn,syncVarianceOrderRequest.getOrderGoodsReqDomainList());
		
		if("failure".equals(returnMap.get("result").toString())){
			return returnMap;
		}
		
		// 2.将OrderInfoRequest 转换为 OrderInfo 对象
		OrderInfo orderInfo = syncVarianceOrderRequest2OrderInfoConvert.covertToTargetEntity(syncVarianceOrderRequest);
		orderInfo.setCustomer_id(customerId);
		orderInfo.setPhysical_warehouse_id(Integer.parseInt(returnMap.get("physical_warehouse_id").toString()));
		
		// 3.验证订单其他数据
		if(WorkerUtil.isNullOrEmpty(orderInfo.getOrder_type())){
			resMap.put("result", "failure");
			resMap.put("error_code", "40006");
			resMap.put("msg", "调整库存类型错误");
			return resMap;
		}
		if(!synvarianceOrderTypes.contains(orderInfo.getOrder_type())){
			resMap.put("result", "failure");
			resMap.put("error_code", "40006");
			resMap.put("msg", "调整库存类型错误");
			return resMap;
		}
		
		
		// 4.验证订单商品的信息
		if(!WorkerUtil.isNullOrEmpty(orderInfo.getOrderGoodsList())){
			for(OrderGoods orderGoods:orderInfo.getOrderGoodsList()){
				//TODO -v 暂时不验证批次号
//				if(WorkerUtil.isNullOrEmpty(orderGoods.getBatch_sn())){
//					resMap.put("result", "failure");
//					resMap.put("error_code", "40007");
//					resMap.put("msg", "批次号batch_sn为空");
//					return resMap;
//				}
				
				if(WorkerUtil.isNullOrEmpty(orderGoods.getStatus_id())){
					resMap.put("result", "failure");
					resMap.put("error_code", "40008");
					resMap.put("msg", "新旧状态Status_id错误");
					return resMap;
				}
				
				if(!syncStatusIds.contains(orderGoods.getStatus_id())){
					resMap.put("result", "failure");
					resMap.put("error_code", "40008");
					resMap.put("msg", "新旧状态Status_id错误");
					return resMap;
				}
				
			}
		}
		
		List<OrderGoods> sortOrderGoodsList =  new ArrayList<OrderGoods>();  //排序的订单商品列表
		StringBuffer productKeySb = new StringBuffer("");
		StringBuffer productNumSb = new StringBuffer("");
		Integer hasSerialGoods=0;
		Integer sumOrderGoods=0;
		String is2B="N";
		
		// 5.插入数据库
		orderInfoDao.insert(orderInfo);
		Integer orderId = orderInfo.getOrder_id();
		List<OrderGoods> orderGoodsList = orderInfo.getOrderGoodsList();
		for(OrderGoods orderGood:orderGoodsList){
			orderGood.setOrder_id(orderId);
			orderGood.setWarehouse_id(orderInfo.getWarehouse_id());
			orderGood.setCustomer_id(customerId);
			orderGoodsDao.insert(orderGood);
			
			//构造排序数组
			if(!WorkerUtil.isNullOrEmpty(sortOrderGoodsList)){
				int index = 0;
				for(int i = 0; i < sortOrderGoodsList.size() ;  i ++ ){
					if(orderGood.getProduct_id().compareTo(sortOrderGoodsList.get(i).getProduct_id()) < 0 ){
						index = i;
						break;
					}else if(orderGood.getProduct_id().compareTo( sortOrderGoodsList.get(i).getProduct_id() ) == 0){
						if( orderGood.getGoods_number().compareTo(sortOrderGoodsList.get(i).getGoods_number()) <= 0  ){
							index = i;
						}else {
							index = i + 1;
						}
						break;
					}else{
						if(i == sortOrderGoodsList.size() - 1){
							index = i + 1 ;
						}
					}
				}
				sortOrderGoodsList.add(index, orderGood);
			}else{
				sortOrderGoodsList.add(orderGood);
			}
			
			//判断是否有串号商品
			if("Y".equals( orderGood.getIs_serial()) && hasSerialGoods == 0 ){
				hasSerialGoods = 1; 
			}
			
			//统计订单商品总数量
			sumOrderGoods = sumOrderGoods +  (orderGood.getGoods_number() == null ? 0: orderGood.getGoods_number());
		}
		
		//初始化productKeySb、productNumSb
		for(int i = 0; i <  sortOrderGoodsList.size(); i ++  ){
			if(i == 0){
				productKeySb.append(sortOrderGoodsList.get(i).getProduct_id());
				productNumSb.append(sortOrderGoodsList.get(i).getGoods_number());
			}else{
				productKeySb.append(",").append(sortOrderGoodsList.get(i).getProduct_id());
				productNumSb.append(",").append(sortOrderGoodsList.get(i).getGoods_number());
			}
		}
		
		// 5.对于盘亏（出库）的订单还需要插入order_process表
		if("VARIANCE_MINUS".equals(orderInfo.getOrder_type())){
			OrderProcess orderProcess = new OrderProcess();
			orderProcess.setBatch_pick_id(0);
			orderProcess.setCreated_time(new Date());
			orderProcess.setCreated_user("system");
			orderProcess.setCustomer_id(customerId);
			orderProcess.setInventory_out_status("");
			orderProcess.setInventory_out_time(new Date(0));
			orderProcess.setOms_order_sn(orderInfo.getOms_order_sn());
			orderProcess.setOrder_batch_group("");
			orderProcess.setOrder_batch_sequence_number(0);
			orderProcess.setOrder_id(orderInfo.getOrder_id());
			orderProcess.setOrder_type(orderInfo.getOrder_type());
			orderProcess.setRecheck_status("N");
			orderProcess.setRecheck_time(new Date(0));
			
			orderProcess.setRecheck_time(new Date(0));
			orderProcess.setReserve_status("N");
			orderProcess.setReserve_time(new Date(0));
			orderProcess.setShipping_time(new Date(0));
			orderProcess.setStatus(orderInfo.getOrder_status());
			orderProcess.setWarehouse_id(orderInfo.getWarehouse_id());
			orderProcess.setProduct_key(productKeySb.toString());
			orderProcess.setProduct_num(productNumSb.toString());
			orderProcess.setHas_serial_goods(hasSerialGoods);
			orderProcess.setIssf(0);
			orderProcess.setIs2B(is2B);
			
			orderProcessDao.insert(orderProcess);
		}
		
		resMap.put("orderId", orderId);
		resMap.put("omsOrderSn", orderInfo.getOms_order_sn());
		resMap.put("result", "success");
		return resMap;
	}
	
	/**
	 * API OrderInfoRequest请求参数验证公用方法
	 * @author hzhang1
	 */
	public HashMap<String,Object> checkOrderInfoRequest(Integer warehouseId , String omsOrderSn , List<OrderGoodsReqDomain> orderGoodsList){
		HashMap<String,Object> map = new HashMap<String,Object>();
		
		// 1.检查仓库是否合理
		Warehouse warehouse = warehouseDao.selectByWarehouseId(warehouseId);
		if(WorkerUtil.isNullOrEmpty(warehouse)){
			map.put("result", "failure");
			map.put("error_code", "40001");
			map.put("msg", "未匹配到指定仓库");
			return map;
		}else{
			map.put("physical_warehouse_id", warehouse.getPhysical_warehouse_id());
		}
		
		// 2.检查订单号是否合理
		OrderInfo orderInfo = orderInfoDao.selectByOmsOrderSn(omsOrderSn);
		if(!WorkerUtil.isNullOrEmpty(orderInfo)){
			map.put("result", "failure");
			map.put("error_code", "40002");
			map.put("msg", "wms已存在此订单");
			return map;
		}
		
		// 3.验证订单商品数量  & skuCode
		if(!WorkerUtil.isNullOrEmpty(orderGoodsList)){
			for (OrderGoodsReqDomain orderGood : orderGoodsList) {
				String skuCode = orderGood.getSku_code();
				Integer goodsNumber = orderGood.getGoods_number();
				
				if(WorkerUtil.isNullOrEmpty(skuCode)){
					map.put("result", "failure");
					map.put("error_code", "40003");
					map.put("msg", "订单商品skuCode为空");
					return map;
				}
				
				Product product = productDao.selectBySkuCode(skuCode);
				if(WorkerUtil.isNullOrEmpty(product)){
					map.put("result", "failure");
					map.put("error_code", "40004");
					map.put("msg", "wms不存在skuCode为"+skuCode+"的商品");
					return map;
				}
				if(goodsNumber <= 0){
					map.put("result", "failure");
					map.put("error_code", "40005");
					map.put("msg", "订单商品为skuCode"+skuCode+"的商品数量有误");
					return map;
				}
			}
			
		}

		// 程序进行到这里说明检查通过，返回成功
		map.put("result", "success");
		map.put("msg", "success");
		return map;
	}
	
	public Integer getcustomerIdByAppKey(String app_key){
		// 1.验证app_key
		Integer customerId=0;
		WarehouseCustomer warehouseCustomer = warehouseCustomerDao.selectByAppKey(app_key);
		if(!WorkerUtil.isNullOrEmpty(warehouseCustomer)){
			customerId = warehouseCustomer.getCustomer_id();
		}
		return customerId;
	}


	@Override
	public HashMap<String, Object> syncSaleOrder( SyncSaleOrderRequest syncSaleOrderRequest, Integer customerId) {
		logger.info("syncSaleOrder-----------------------------------------------");
		HashMap<String,Object> resMap = new HashMap<String,Object>();
		// 1.验证数据的合理性
		Integer warehouseId = syncSaleOrderRequest.getWarehouse_id();
		String omsOrderSn = syncSaleOrderRequest.getOms_order_sn();
		HashMap<String,Object> returnMap = checkOrderInfoRequest(warehouseId,omsOrderSn,syncSaleOrderRequest.getOrderGoodsReqDomainList());
		
		if("failure".equals(returnMap.get("result").toString())){
			return returnMap;
		}
		// 2.将OrderInfoRequest 转换为 OrderInfo 对象
		logger.info("before-----------------------------------------------");
		OrderInfo orderInfo = syncSaleOrderRequest2OrderInfoConvert.covertToTargetEntity(syncSaleOrderRequest );
		logger.info("after-----------------------------------------------");
		orderInfo.setPhysical_warehouse_id(Integer.parseInt(returnMap.get("physical_warehouse_id").toString()));
		orderInfo.setCustomer_id(customerId);
		// 3.验证订单其他数据
		
		if(WorkerUtil.isNullOrEmpty(orderInfo.getOrder_type())){
			resMap.put("result", "failure");
			resMap.put("error_code", "40006");
			resMap.put("msg", "发货订单类型order_type为空");
			return resMap;
		}
		
		if(!(syncSaleOrderTypes.contains(orderInfo.getOrder_type())) ){
			resMap.put("result", "failure");
			resMap.put("error_code", "40007");
			resMap.put("msg", "发货订单类型order_type不为SALE、SUPPLIER_RETURN的类型");
			return resMap;
		}
		//销售订单校验
		if(OrderInfo.ORDER_TYPE_SALE.equals(orderInfo.getOrder_type())){
		
			if(WorkerUtil.isNullOrEmpty(orderInfo.getReceive_name())){
				resMap.put("result", "failure");
				resMap.put("error_code", "40008");
				resMap.put("msg", "收件人姓名receive_name为空");
				return resMap;
			}
			if(WorkerUtil.isNullOrEmpty(orderInfo.getProvince_name())){
				resMap.put("result", "failure");
				resMap.put("error_code", "40009");
				resMap.put("msg", "收货地址省名称province_name为空");
				return resMap;
			}
			if(WorkerUtil.isNullOrEmpty(orderInfo.getCity_name())){
				resMap.put("result", "failure");
				resMap.put("error_code", "40010");
				resMap.put("msg", "收货地址市名称city_name为空");
				return resMap;
			}
			if(WorkerUtil.isNullOrEmpty(orderInfo.getShipping_address())){
				resMap.put("result", "failure");
				resMap.put("error_code", "40011");
				resMap.put("msg", "收货详细地址shipping_address为空");
				return resMap;
			}
			if(WorkerUtil.isNullOrEmpty(orderInfo.getShipping_id())){
				resMap.put("result", "failure");
				resMap.put("error_code", "40012");
				resMap.put("msg", "快递方式shipping_code为空或wms没有对应的快递方式");
				return resMap;
			}
			
			
			
			if(WorkerUtil.isNullOrEmpty(orderInfo.getProvince_id())){
				resMap.put("result", "failure");
				resMap.put("error_code", "40013");
				resMap.put("msg", "收货地址省名称province_name在wms中不存在");
				return resMap;
			}
			if(WorkerUtil.isNullOrEmpty(orderInfo.getCity_id())){
				resMap.put("result", "failure");
				resMap.put("error_code", "40014");
				resMap.put("msg", "收货地址市名称city_name在wms中不存在或不是对应的省份");
				return resMap;
			}
			if(!WorkerUtil.isNullOrEmpty(orderInfo.getDistrict_id())
					&& orderInfo.getDistrict_id() == -1){ //当区域为-1表明有问题，非必需
				resMap.put("result", "failure");
				resMap.put("error_code", "40015");
				resMap.put("msg", "收货地址区名称district_name在wms中不存在或不是对应的市");
				return resMap;
			}
			
			if(!checkShippingWarehouseMapping(orderInfo.getShipping_id() , orderInfo.getWarehouse_id())){
				resMap.put("result", "failure");
				resMap.put("error_code", "40016");
				resMap.put("msg", "该仓库warehouse_id" + orderInfo.getWarehouse_id() + "不支持该种快递，shipping_code" + syncSaleOrderRequest.getShipping_code());
				return resMap;
			}
			
		}
		//-gt订单校验
		if(OrderInfo.ORDER_TYPE_SUPPLIER_RETURN.equals(orderInfo.getOrder_type())){
			
			//-gt订单校验
			//TODO 待确认
			/**xhchen 2016-08-16 新的-dt -dc订单没有这一部分字段，推单会报错，所以注释掉
			if(WorkerUtil.isNullOrEmpty(orderInfo.getProvider_code())){
				resMap.put("result", "failure");
				resMap.put("error_code", "40016");
				resMap.put("msg", "供应商编码provider_code为空");
				return resMap;
			}
			if(WorkerUtil.isNullOrEmpty(orderInfo.getProvider_name())){
				resMap.put("result", "failure");
				resMap.put("error_code", "40017");
				resMap.put("msg", "供应商订单类型provider_name为空");
				return resMap;
			}
			if(WorkerUtil.isNullOrEmpty(orderInfo.getProvider_order_type())){
				resMap.put("result", "failure");
				resMap.put("error_code", "40018");
				resMap.put("msg", "供应商名称类型provider_order_type为空");
				return resMap;
				
			}
			*/
//			if(WorkerUtil.isNullOrEmpty(orderInfo.getShipping_id())){
//				resMap.put("result", "failure");
//				resMap.put("error_code", "40012");
//				resMap.put("msg", "快递方式shipping_code为空或wms没有对应的快递方式");
//				return resMap;
//			}
			
			//-gt订单商品校验
			if(!WorkerUtil.isNullOrEmpty(orderInfo.getOrderGoodsList())){
				for(OrderGoods orderGoods:orderInfo.getOrderGoodsList()){
					//TODO 批次号不要验证
//					if(WorkerUtil.isNullOrEmpty(orderGoods.getBatch_sn())){
//						resMap.put("result", "failure");
//						resMap.put("error_code", "40018");
//						resMap.put("msg", "批次号batch_sn为空");
//						return resMap;
//					}
					
					if(WorkerUtil.isNullOrEmpty(orderGoods.getStatus_id())){
						resMap.put("result", "failure");
						resMap.put("error_code", "40019");
						resMap.put("msg", "新旧状态Status_id为空");
						return resMap;
					}
					
					if(!syncStatusIds.contains(orderGoods.getStatus_id())){
						resMap.put("result", "failure");
						resMap.put("error_code", "40020");
						resMap.put("msg", "新旧状态Status_id类型错误");
						return resMap;
					}
					
					
					
				}
			}
		}
		
		// 4.保存orderInfo
		orderInfoDao.insert(orderInfo);
		Integer orderId = orderInfo.getOrder_id();
		List<OrderGoods> orderGoodsList = orderInfo.getOrderGoodsList();
		
		
		List<OrderGoods> sortOrderGoodsList =  new ArrayList<OrderGoods>();  //排序的订单商品列表
		StringBuffer productKeySb = new StringBuffer("");
		StringBuffer productNumSb = new StringBuffer("");
		Integer hasSerialGoods = 0;
		Integer issf = 0;
		String is2B = "N" ; //是否是2B订单
		
		if(Shipping.SHIPPING_CODE_SF.equals(syncSaleOrderRequest.getShipping_code()) 
				||  Shipping.SHIPPING_CODE_SFLY.equals(syncSaleOrderRequest.getShipping_code()) ){
			issf = 1;
		}
		/** is2B的优先级  X>Y>W*/
		for(OrderGoods orderGood:orderGoodsList){
			
			orderGood.setOrder_id(orderId);
			orderGood.setWarehouse_id(orderInfo.getWarehouse_id());
			orderGood.setCustomer_id(customerId);
			orderGood.getProduct_id();
			orderGood.getGoods_number();
			orderGoodsDao.insert(orderGood);

			
			//构造排序数组
			if(!WorkerUtil.isNullOrEmpty(sortOrderGoodsList)){
				int index = 0;
				for(int i = 0; i < sortOrderGoodsList.size() ;  i ++ ){
					if(orderGood.getProduct_id().compareTo(sortOrderGoodsList.get(i).getProduct_id()) < 0 ){
						index = i;
						break;
					}else if(orderGood.getProduct_id().compareTo( sortOrderGoodsList.get(i).getProduct_id() ) == 0){
						if( orderGood.getGoods_number().compareTo(sortOrderGoodsList.get(i).getGoods_number()) <= 0  ){
							index = i;
						}else {
							index = i + 1;
						}
						break;
					}else{
						if(i == sortOrderGoodsList.size() - 1){
							index = i + 1 ;
						}
					}
				}
				sortOrderGoodsList.add(index, orderGood);
			}else{
				sortOrderGoodsList.add(orderGood);
			}
			
			
			//add by xhchen 2016-08-25
			Product product = productDao.selectByPrimaryKey(orderGood.getProduct_id());
			if(!WorkerUtil.isNullOrEmpty(product)){
				if(product.getIs_need_transfer_code().equals("Y")){
					is2B="W";
				}
			}
			//判断是否有串号商品
			if("Y".equals( product.getIs_serial()) && hasSerialGoods == 0 ){
				hasSerialGoods = 1; 
			}
			
			//统计订单商品总数量
			int numOrderGoods = orderGood.getGoods_number() == null ? 0: orderGood.getGoods_number();
			if(numOrderGoods>=50){
				is2B = "Y";
			}
			
		}
		
		
		
		//初始化productKeySb、productNumSb
		for(int i = 0; i <  sortOrderGoodsList.size(); i ++  ){
			if(i == 0){
				productKeySb.append(sortOrderGoodsList.get(i).getProduct_id());
				productNumSb.append(sortOrderGoodsList.get(i).getGoods_number());
			}else{
				productKeySb.append(",").append(sortOrderGoodsList.get(i).getProduct_id());
				productNumSb.append(",").append(sortOrderGoodsList.get(i).getGoods_number());
			}
		}
		if((!WorkerUtil.isNullOrEmpty(orderInfo.getNote()))&&orderInfo.getNote().contains("最新批次")){
			is2B = "X";
		}
		
		
		
		//只有销售保存orderGoodsOms
		if(OrderInfo.ORDER_TYPE_SALE.equals(orderInfo.getOrder_type())){
			for (OrderGoodsOms orderGoodsOms : orderInfo.getOrderGoodsOmsList()) {
				orderGoodsOms.setOrder_id(orderId);
				orderGoodsOms.setWarehouse_id(orderInfo.getWarehouse_id());
				orderGoodsOms.setCustomer_id(customerId);
				orderGoodsOmsDao.insert(orderGoodsOms);
			}
		}
		
		
		
		
		// 5. 创建order_process
		//TODO 一些字段的默认值需要确认
		OrderProcess orderProcess = new OrderProcess();
		orderProcess.setBatch_pick_id(0);
		orderProcess.setCreated_time(new Date());
		orderProcess.setCreated_user("system");
		orderProcess.setCustomer_id(customerId);
		orderProcess.setInventory_out_status("");
		orderProcess.setInventory_out_time(new Date(0));
		orderProcess.setOms_order_sn(orderInfo.getOms_order_sn());
		orderProcess.setOrder_batch_group("");
		orderProcess.setOrder_batch_sequence_number(0);
		orderProcess.setOrder_id(orderInfo.getOrder_id());
		orderProcess.setOrder_type(orderInfo.getOrder_type());
		orderProcess.setRecheck_status("N");
		orderProcess.setRecheck_time(new Date(0));
		
		orderProcess.setReserve_status("N");
		orderProcess.setReserve_time(new Date(0));
		orderProcess.setShipping_time(new Date(0));
		orderProcess.setStatus(orderInfo.getOrder_status());
		orderProcess.setWarehouse_id(orderInfo.getWarehouse_id());
		orderProcess.setProduct_key(productKeySb.toString());
		orderProcess.setProduct_num(productNumSb.toString());
		orderProcess.setHas_serial_goods(hasSerialGoods);
		orderProcess.setIssf(issf);
		orderProcess.setIs2B(is2B);
		orderProcessDao.insert(orderProcess);
		
		
		//6. 创建user_order_action
		UserActionOrder userActionOrder = new UserActionOrder();
		userActionOrder.setAction_note("OMS订单推送");
		userActionOrder.setAction_type(orderInfo.getOrder_status());
		userActionOrder.setCreated_time(new Date());
		userActionOrder.setCreated_user("system");
		userActionOrder.setOrder_goods_id(null);
		userActionOrder.setOrder_id(orderInfo.getOrder_id());
		userActionOrder.setOrder_status(orderInfo.getOrder_status());
		userActionOrderDao.insert(userActionOrder);
		resMap.put("orderId", orderId);
		resMap.put("omsOrderSn", orderInfo.getOms_order_sn());
		resMap.put("result", "success");
		return resMap;
	}


	/**
	 * 检查该快递方式
	 * @param shipping_id
	 * @param warehouse_id
	 * @return
	 */
	private boolean checkShippingWarehouseMapping(Integer shipping_id,
			Integer warehouse_id) {
		
		ShippingWarehouseMapping shippingWarehouseMapping = shippingWarehouseMappingDao.selectByShippingWarehouseId(shipping_id, warehouse_id);
		
		if( WorkerUtil.isNullOrEmpty(shippingWarehouseMapping) ){
			return false;
		}
		
		return true;
	}


	@Override
	public HashMap<String, Object> syncRmaOrder(
			SyncRmaOrderRequest syncRmaOrderRequest, Integer customerId) {

		HashMap<String,Object> resMap = new HashMap<String,Object>();
		// 1.验证数据的合理性
		Integer warehouseId = syncRmaOrderRequest.getWarehouse_id();
		String omsOrderSn = syncRmaOrderRequest.getOms_order_sn();
		HashMap<String,Object> returnMap = checkOrderInfoRequest(warehouseId,omsOrderSn,syncRmaOrderRequest.getOrderGoodsReqDomainList());
		
		if("failure".equals(returnMap.get("result").toString())){
			return returnMap;
		}
		
		// 2.将OrderInfoRequest 转换为 OrderInfo 对象
		OrderInfo orderInfo = syncRmaOrderRequest2OrderInfoConvert.covertToTargetEntity(syncRmaOrderRequest);
		orderInfo.setPhysical_warehouse_id(Integer.parseInt(returnMap.get("physical_warehouse_id").toString()));
		orderInfo.setCustomer_id(customerId);

		
		// 3.验证订单其他数据
		
//		if(WorkerUtil.isNullOrEmpty(orderInfo.getParent_order_id())){
//			resMap.put("result", "failure");
//			resMap.put("error_code", "40006");
//			resMap.put("msg", "销售退货订单parent_order_sn为空或wms不存在对应的父订单");
//			return resMap;
//		}
		
		// 不校验退货订单root_order_sn
//		if(WorkerUtil.isNullOrEmpty(orderInfo.getRoot_order_id())){
//			resMap.put("result", "failure");
//			resMap.put("error_code", "40007");
//			resMap.put("msg", "销售退货订单root_order_sn为空或wms不存在对应的根订单");
//			return resMap;
//		}
		
		//收件人信息校验
		//TODO （是否需要校验，待确认）先暂时注释掉
//		if(WorkerUtil.isNullOrEmpty(orderInfo.getReceive_name())){
//			resMap.put("result", "failure");
//			resMap.put("error_code", "40008");
//			resMap.put("msg", "收件人姓名receive_name为空");
//			return resMap;
//		}
//		if(WorkerUtil.isNullOrEmpty(orderInfo.getProvince_name())){
//			resMap.put("result", "failure");
//			resMap.put("error_code", "40009");
//			resMap.put("msg", "收货地址省名称province_name为空");
//			return resMap;
//		}
//		if(WorkerUtil.isNullOrEmpty(orderInfo.getCity_name())){
//			resMap.put("result", "failure");
//			resMap.put("error_code", "40010");
//			resMap.put("msg", "收货地址市名称city_name为空");
//			return resMap;
//		}
//		if(WorkerUtil.isNullOrEmpty(orderInfo.getShipping_address())){
//			resMap.put("result", "failure");
//			resMap.put("error_code", "40011");
//			resMap.put("msg", "收货详细地址shipping_address为空");
//			return resMap;
//		}
		if(WorkerUtil.isNullOrEmpty(orderInfo.getShipping_id())){
			resMap.put("result", "failure");
			resMap.put("error_code", "40012");
			resMap.put("msg", "快递方式shipping_code为空或wms没有对应的快递方式");
			return resMap;
		}
		
		
		
		
		// 4.插入数据库
		orderInfoDao.insert(orderInfo);
		Integer orderId = orderInfo.getOrder_id();
		List<OrderGoods> orderGoodsList = orderInfo.getOrderGoodsList();
		for(OrderGoods orderGood:orderGoodsList){
			orderGood.setOrder_id(orderId);
			orderGood.setWarehouse_id(orderInfo.getWarehouse_id());
			orderGood.setCustomer_id(customerId);
			orderGoodsDao.insert(orderGood);
		}
		
		
		// 5. 创建order_process（不需要创建order_process）
//		//TODO 一些字段的默认值需要确认
//		OrderProcess orderProcess = new OrderProcess();
//		orderProcess.setBatch_pick_id(0);
//		orderProcess.setCreated_time(new Date());
//		orderProcess.setCreated_user("system");
//		orderProcess.setCustomer_id(customerId);
//		orderProcess.setInventory_out_status("");
//		orderProcess.setInventory_out_time(new Date(0));
//		orderProcess.setOms_order_sn(orderInfo.getOms_order_sn());
//		orderProcess.setOrder_batch_group("");
//		orderProcess.setOrder_batch_sequence_number(0);
//		orderProcess.setOrder_id(orderInfo.getOrder_id());
//		orderProcess.setOrder_type(orderInfo.getOrder_type());
//		orderProcess.setRecheck_status("N");
//		orderProcess.setRecheck_time(new Date(0));
//		
//		orderProcess.setReserve_status("N");
//		orderProcess.setReserve_time(new Date(0));
//		orderProcess.setShipping_time(new Date(0));
//		orderProcess.setStatus(orderInfo.getOrder_status());
//		orderProcess.setWarehouse_id(orderInfo.getWarehouse_id());
//		
//		orderProcessDao.insert(orderProcess);
		
		
		
		//6. 创建user_order_action
		UserActionOrder userActionOrder = new UserActionOrder();
		userActionOrder.setAction_note("OMS订单推送");
		userActionOrder.setAction_type(orderInfo.getOrder_status());
		userActionOrder.setCreated_time(new Date());
		userActionOrder.setCreated_user("system");
		userActionOrder.setOrder_goods_id(null);
		userActionOrder.setOrder_id(orderInfo.getOrder_id());
		userActionOrder.setOrder_status(orderInfo.getOrder_status());
		userActionOrderDao.insert(userActionOrder);
		
		
		
		
		resMap.put("orderId", orderId);
		resMap.put("omsOrderSn", orderInfo.getOms_order_sn());
		resMap.put("result", "success");
		return resMap;
	
	}


	/**
	 * API 取消订单
	 */
	@Override
	public HashMap<String, Object> cancelOrder(
			CancelOrderRequest cancelOrderRequest, Integer customerId) {
		
		HashMap<String,Object> resMap = new HashMap<String,Object>();
		
		Integer orderId = cancelOrderRequest.getOrder_id();
		String orderType = cancelOrderRequest.getOrder_type();
		Integer warehouseId = cancelOrderRequest.getWarehouse_id();
		
		
		if(WorkerUtil.isNullOrEmpty(orderId)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40001");
			resMap.put("msg", "order_id为空");
			return resMap;
		}
		
		if(WorkerUtil.isNullOrEmpty(orderType)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40002");
			resMap.put("msg", "order_type为空");
			return resMap;
		}
		
		if(WorkerUtil.isNullOrEmpty(warehouseId)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40003");
			resMap.put("msg", "warehouse_id为空");
			return resMap;
		}
		
		if(!cancelOrderTypes.contains(orderType)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40004");
			resMap.put("msg", "order_type类型错误，不能取消这种订单");
		}
		
		
		OrderInfo orderInfo = orderInfoDao.selectByPrimaryKeyForUpdate(orderId);
		OrderProcess orderProcess = orderProcessDao.selectByPrimaryKeyForUpdate(orderId);
		String isOutSource = configDao.getConfigValueByFrezen(orderInfo.getPhysical_warehouse_id(), orderInfo.getCustomer_id(), "IS_OUTSOURCE_PHYSICAL");
		
		logger.info("order_id"+orderId+"isOutSource"+isOutSource);
		if(WorkerUtil.isNullOrEmpty(orderInfo)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40005");
			resMap.put("msg", "不存在该order_id对应的订单不存在");
			return resMap;
		}
		
		if( customerId.compareTo(orderInfo.getCustomer_id()) != 0 ){
			resMap.put("result", "failure");
			resMap.put("error_code", "40007");
			resMap.put("msg", "customerId与wms不一致");
			return resMap;
		}
		
		if( !orderType.equals( orderInfo.getOrder_type())){
			resMap.put("result", "failure");
			resMap.put("error_code", "40006");
			resMap.put("msg", "order_type与wms不一致");
			return resMap;
		}
		
		if( !warehouseId.equals( orderInfo.getWarehouse_id())){
			resMap.put("result", "failure");
			resMap.put("error_code", "40007");
			resMap.put("msg", "warehouse_id与wms不一致");
			return resMap;
		}
		
		
		//TODO 不能取消的条件判断，待确认、补充和完善
		if( OrderInfo.ORDER_TYPE_SALE.equals(  orderInfo.getOrder_type() )){
			if("1".equals(isOutSource)){
				if ((!OrderInfo.ORDER_STATUS_ACCEPT.equalsIgnoreCase(orderInfo.getOrder_status()))
						|| (!OrderInfo.ORDER_STATUS_ACCEPT.equalsIgnoreCase(orderProcess.getStatus())) ||
						(!OrderProcess.BATCH_TRICK_STATUS_INIT.equalsIgnoreCase(orderProcess.getBatch_trick_status()))) {
					resMap.put("result", "failure");
					resMap.put("error_code", "40011");
					resMap.put("msg", "外包仓不能取消的订单状态order_status:" + orderInfo.getOrder_status() );
					return resMap;
				}
			}else{
				if (CannotCancelOrderStatusesSale.contains(orderInfo.getOrder_status())
						|| CannotCancelOrderStatusesSale.contains(orderProcess.getStatus())  
						||(!OrderProcess.BATCH_TRICK_STATUS_INIT.equalsIgnoreCase(orderProcess.getBatch_trick_status()))) {
					resMap.put("result", "failure");
					resMap.put("error_code", "40008");
					resMap.put("msg", "不能取消的订单状态order_status:" + orderInfo.getOrder_status() );
					return resMap;
				}
				List<Shipment> shipmentList = shipmentBiz.selectByOrderIdAndStatus( orderInfo.getOrder_id(), Shipment.STATUS_SHIPPED );
				if( !WorkerUtil.isNullOrEmpty(shipmentList) ){
					resMap.put("result", "failure");
					resMap.put("error_code", "40010");
					resMap.put("msg", "该销售订单已有包裹发货"  );
					return resMap;
				}
			}
			
		}
		else if( OrderInfo.ORDER_TYPE_SUPPLIER_RETURN.equals(  orderInfo.getOrder_type() )){
			if(CannotCancelOrderStatusesSupplierReturn.contains(orderInfo.getOrder_status()) ){
				resMap.put("result", "failure");
				resMap.put("error_code", "40008");
				resMap.put("msg", "不能取消的订单状态order_status:" + orderInfo.getOrder_status() );
				return resMap;
			}
			
		}
		//支持退货取消
		else if( OrderInfo.ORDER_TYPE_RETURN.equals(  orderInfo.getOrder_type() )){
			if(CannotCancelOrderStatusesReturn.contains(orderInfo.getOrder_status()) ){
				resMap.put("result", "failure");
				resMap.put("error_code", "40008");
				resMap.put("msg", "不能取消的订单状态order_status:" + orderInfo.getOrder_status() );
				return resMap;
			}
		}
		else{
			if(CannotCancelOrderStatusesPurchase.contains(orderInfo.getOrder_status()) ){
				resMap.put("result", "failure");
				resMap.put("error_code", "40008");
				resMap.put("msg", "不能取消的订单状态order_status:" + orderInfo.getOrder_status() );
				return resMap;
			}
			
			List<LabelAccept> labelAcceptList = labelAcceptDao.selectByOrderIdWithLocationKwBarcode(orderInfo.getOrder_id());
			if( !WorkerUtil.isNullOrEmpty(labelAcceptList) ){
				resMap.put("result", "failure");
				resMap.put("error_code", "40009");
				resMap.put("msg", "该采购订单已部分或全部上架"  );
				return resMap;
				
			}
			
		}
		
		//6. 创建user_order_action
		UserActionOrder userActionOrder = new UserActionOrder();
		userActionOrder.setAction_note("OMS取消订单");
		userActionOrder.setAction_type(orderInfo.getOrder_status());
		userActionOrder.setCreated_time(new Date());
		userActionOrder.setCreated_user("system");
		userActionOrder.setOrder_goods_id(null);
		userActionOrder.setOrder_id(orderInfo.getOrder_id());
		userActionOrder.setOrder_status(OrderInfo.ORDER_STATUS_CANCEL);
		userActionOrderDao.insert(userActionOrder);
		
		//增加inventory_summary表的库存
		if( OrderInfo.ORDER_TYPE_SALE.equals(  orderInfo.getOrder_type() ) ||
				 OrderInfo.ORDER_TYPE_SUPPLIER_RETURN.equals(orderInfo.getOrder_type())
				){
			//取消预定
			reserveOrderInventoryBiz.cancelOrderInventoryReservation(orderInfo.getOrder_id());
		}
		//增加product_location表的库存
		if(OrderInfo.ORDER_TYPE_SUPPLIER_RETURN.equals(orderInfo.getOrder_type())
				){
			//取消预定
			productLocationBiz.cancelOrderProductLocationReservation(orderInfo.getOrder_id());
		}
		//预打包销售订单释放qty_used
				if( OrderInfo.ORDER_TYPE_SALE.equals(orderInfo.getOrder_type())){
					
					orderPrepackBiz.releaseQtyUsed(orderInfo.getOrder_id());
				}
		
		Map<String, Object> mapParams = new HashMap<String, Object>();
		mapParams.put("orderId", orderId);
		mapParams.put("orderStatus", OrderInfo.ORDER_STATUS_CANCEL );
		mapParams.put("lastUpdatedUser", "system");
		mapParams.put("lastUpdatedTime", new Date());
		int effectRows = orderInfoDao.updateOrderStatusByOrderId(mapParams);
		if( effectRows <= 0 ){
			throw new RuntimeException("更新订单状态失败，order_id=" + orderId +" , orderType="+orderType + " , warehouseId="+ warehouseId  );
		}
		
		
		// 5. 创建order_process（不需要创建order_process）
		//TODO 一些字段的默认值需要确认
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderId", orderId);
		paramMap.put("status", OrderInfo.ORDER_STATUS_CANCEL );
		
		orderProcessDao.updateOrderStatusByOrderId(paramMap) ;
		
		resMap.put("result", "success");
		return resMap;
		
	}
	
	
	/**
	 * API 查询采购订单
	 * @author hzhang1
	 */
	public Map<String, Object> getPurchaseOrder(GetPurchaseOrderRequest getPurchaseOrderRequest,Integer customerId){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		String omsOrderSn = getPurchaseOrderRequest.getOms_order_sn();
		
		// 1.根据omsOrderSn找到对应的orderInfo对象
		OrderInfo orderInfo = orderInfoDao.selectByOmsOrderSn(omsOrderSn);
		if(WorkerUtil.isNullOrEmpty(orderInfo)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40001");
			resMap.put("msg", "wms获取不到没有该订单" );
			return resMap;
		}
		Integer orderId = orderInfo.getOrder_id();
		List<OrderGoods> orderGoodsList = orderGoodsDao.selectByOrderId(orderId);
		if(WorkerUtil.isNullOrEmpty(orderGoodsList)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40002");
			resMap.put("msg", "wms中该订单的商品信息为空" );
			return resMap;
		}
		orderInfo.setOrderGoodsList(orderGoodsList);
		
		// 2.验证对象
		if(customerId.compareTo(orderInfo.getCustomer_id()) != 0){
			resMap.put("result", "failure");
			resMap.put("error_code", "40003");
			resMap.put("msg", "该订单与所在customerId不一致" );
			return resMap;
		}
		if(!"PURCHASE".equals(orderInfo.getOrder_type())){
			resMap.put("result", "failure");
			resMap.put("error_code", "40004");
			resMap.put("msg", "该订单不是采购订单" );
			return resMap;
		}
		
		resMap.put("wmsOrderId", orderId);
		resMap.put("wmsOrderStatus", orderInfo.getOrder_status());
		resMap.put("warehouseId", orderInfo.getWarehouse_id());
		resMap.put("omsOrderSn", orderInfo.getOms_order_sn());
		
		// 3.组装orderGoodsMap
		Map<String,Object> orderGoodsMap = new HashMap<String,Object>();
		for(OrderGoods orderGood : orderGoodsList){
			orderGoodsMap.put(orderGood.getOrder_goods_id()+"", orderGood);
		}
		
		// 4.inventoryItem信息
		List<InventoryItemDetail> inventoryItemDetailList = inventoryItemDetailDao.selectByOrderId(orderId);
		List<OrderGoodsResDomain> orderGoodsResDomainList = new ArrayList<OrderGoodsResDomain>();
		for(InventoryItemDetail inventoryItemDetail : inventoryItemDetailList){
			OrderGoodsResDomain orderGoodsResDomain = new OrderGoodsResDomain();
			Integer orderGoodsId = inventoryItemDetail.getOrder_goods_id();
			OrderGoods orderGood = (OrderGoods) orderGoodsMap.get(orderGoodsId.toString());
			orderGoodsResDomain.setOms_order_goods_sn(orderGood.getOms_order_goods_sn());
			orderGoodsResDomain.setInventory_item_id(inventoryItemDetail.getInventory_item_id());
			
			Product product = productDao.selectByPrimaryKey(orderGood.getProduct_id());
			orderGoodsResDomain.setSku_code(product.getSku_code());
			orderGoodsResDomain.setGoods_name(orderGood.getGoods_name());
			orderGoodsResDomain.setGoods_number(orderGood.getGoods_number());
			orderGoodsResDomain.setQuantity(inventoryItemDetail.getChange_quantity());
			
			
			InventoryItem inventoryItem = inventoryItemDao.selectByPrimaryKey(inventoryItemDetail.getInventory_item_id());
			orderGoodsResDomain.setValidity(inventoryItem.getValidity());
			orderGoodsResDomain.setBatch_sn(inventoryItem.getBatch_sn());
			orderGoodsResDomain.setStatus_id(inventoryItem.getStatus());
			orderGoodsResDomain.setSerial_number(inventoryItem.getSerial_number());
			orderGoodsResDomain.setUnit_cost(inventoryItem.getUnit_cost());
			orderGoodsResDomain.setProvider_code(inventoryItem.getProvider_code());
			orderGoodsResDomainList.add(orderGoodsResDomain);
		}
		
		// 5.返回resMap
		resMap.put("result", "success");
		resMap.put("orderGoodsResDomainList", orderGoodsResDomainList);
		return resMap;
	}
	
	/**
	 * API 查询-V订单
	 * @param omsOrderSn
	 * @param customerId
	 * @return
	 */
	public Map<String, Object> getVarianceOrder(GetVarianceOrderRequest getVarianceOrderRequest,Integer customerId){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		String omsOrderSn = getVarianceOrderRequest.getOms_order_sn();
		
		// 1.根据omsOrderSn找到对应的orderInfo对象
		OrderInfo orderInfo = orderInfoDao.selectByOmsOrderSn(omsOrderSn);
		
		if(WorkerUtil.isNullOrEmpty(orderInfo)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40001");
			resMap.put("msg", "wms获取不到没有该订单" );
			return resMap;
		}
		Integer orderId = orderInfo.getOrder_id();
		List<OrderGoods> orderGoodsList = orderGoodsDao.selectByOrderId(orderId);
		if(WorkerUtil.isNullOrEmpty(orderGoodsList)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40002");
			resMap.put("msg", "wms中该订单的商品信息为空" );
			return resMap;
		}
		orderInfo.setOrderGoodsList(orderGoodsList);
		
		// 2.验证对象
		if(customerId.compareTo(orderInfo.getCustomer_id()) != 0){
			resMap.put("result", "failure");
			resMap.put("error_code", "40003");
			resMap.put("msg", "该订单与所在customerId不一致" );

			return resMap;
			
		}
		if(!"VARIANCE_MINUS".equals(orderInfo.getOrder_type()) && !"VARIANCE_ADD".equals(orderInfo.getOrder_type())){
			resMap.put("result", "failure");
			resMap.put("error_code", "40004");
			resMap.put("msg", "该订单不是调整库存订单" );
			logger.info(orderInfo.getOrder_type());
			return resMap;
		}
		
		resMap.put("wmsOrderId", orderId);
		resMap.put("wmsOrderStatus", orderInfo.getOrder_status());
		resMap.put("warehouseId", orderInfo.getWarehouse_id());
		resMap.put("omsOrderSn", orderInfo.getOms_order_sn());
		
		// 3.组装orderGoodsMap
		Map<String,Object> orderGoodsMap = new HashMap<String,Object>();
		for(OrderGoods orderGood : orderGoodsList){
			orderGoodsMap.put(orderGood.getOrder_goods_id()+"", orderGood);
		}
		
		// 4.inventoryItem信息
		List<InventoryItemDetail> inventoryItemDetailList = inventoryItemDetailDao.selectByOrderId(orderId);
		List<OrderGoodsResDomain> orderGoodsResDomainList = new ArrayList<OrderGoodsResDomain>();
		for(InventoryItemDetail inventoryItemDetail : inventoryItemDetailList){
			OrderGoodsResDomain orderGoodsResDomain = new OrderGoodsResDomain();
			Integer orderGoodsId = inventoryItemDetail.getOrder_goods_id();
			OrderGoods orderGood = (OrderGoods) orderGoodsMap.get(orderGoodsId.toString());
			orderGoodsResDomain.setOms_order_goods_sn(orderGood.getOms_order_goods_sn());
			orderGoodsResDomain.setInventory_item_id(inventoryItemDetail.getInventory_item_id());
			
			Product product = productDao.selectByPrimaryKey(orderGood.getProduct_id());
			orderGoodsResDomain.setSku_code(product.getSku_code());
			orderGoodsResDomain.setGoods_name(orderGood.getGoods_name());
			orderGoodsResDomain.setGoods_number(orderGood.getGoods_number());
			orderGoodsResDomain.setQuantity(inventoryItemDetail.getChange_quantity());
			
			InventoryItem inventoryItem = inventoryItemDao.selectByPrimaryKey(inventoryItemDetail.getInventory_item_id());
			orderGoodsResDomain.setValidity(inventoryItem.getValidity());
			orderGoodsResDomain.setBatch_sn(inventoryItem.getBatch_sn());
			orderGoodsResDomain.setStatus_id(inventoryItem.getStatus());
			orderGoodsResDomain.setSerial_number(inventoryItem.getSerial_number());
			orderGoodsResDomain.setUnit_cost(inventoryItem.getUnit_cost());
			orderGoodsResDomain.setProvider_code(inventoryItem.getProvider_code());
			orderGoodsResDomainList.add(orderGoodsResDomain);
		}
		
		// 5.返回resMap
		resMap.put("result", "success");
		resMap.put("orderGoodsResDomainList", orderGoodsResDomainList);
		return resMap;
		
	}
	
	/**
	 * API 调整供价
	 * @param adjustPriceRequest
	 * @param customerId
	 * @return
	 */
	public Map<String, Object> adjustprice(AdjustPriceRequest adjustPriceRequest, Integer customerId){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		String omsOrderSn = adjustPriceRequest.getOms_order_sn();
		String skuCode = adjustPriceRequest.getSku_code();
		BigDecimal unitCost = adjustPriceRequest.getUnit_cost();
		String providerCode = adjustPriceRequest.getProvider_code();  // 供应商code
		Date arrivalTime = adjustPriceRequest.getArrival_time();  //到货时间
		Integer omsOrderGoodsSn = adjustPriceRequest.getOms_order_goods_sn();
		
		
		
		// 1.根据omsOrderSn找到对应的orderInfo对象
		OrderInfo orderInfo = orderInfoDao.selectByOmsOrderSn(omsOrderSn);
		if(WorkerUtil.isNullOrEmpty(orderInfo)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40001");
			resMap.put("msg", "wms获取不到没有该订单" );
			return resMap;
		}
		Integer orderId = orderInfo.getOrder_id();
		
		// 定义更新哪种类型，默认为false
		boolean isUpdateUnitCost = false;
		boolean isUpdateProviderCode = false;
		boolean isUpdateArrivalTime = false;
		
		//2.如果供价或供应商需要调整，需要走下面的验证
		if(! WorkerUtil.isNullOrEmpty( unitCost)
				||  !WorkerUtil.isNullOrEmpty( providerCode) ){
			//>> a.根据orderId和omsOrderGoodsSn得到唯一一条orderGoods
			Map<String,Object> goodsMap = new HashMap<String,Object>();
			goodsMap.put("orderId", orderId);
			goodsMap.put("omsOrderGoodsSn", omsOrderGoodsSn);
			OrderGoods orderGood = orderGoodsDao.selectByOrderIdAndOrderGoodsId(goodsMap);
			
			//>> b.判断
			if(!orderInfo.getOms_order_type().equals("PURCHASE")){
				resMap.put("result", "failure");
				resMap.put("error_code", "40005");
				resMap.put("msg", "此种类型订单不能进行供价调整");
				return resMap;
			}
			
			
			if(WorkerUtil.isNullOrEmpty(orderGood)){
				resMap.put("result", "failure");
				resMap.put("error_code", "40002");
				resMap.put("msg", "wms获取不到该订单商品信息" );
				return resMap;
			}
			
			if(orderGood.getOrder_goods_id() < 1){
				resMap.put("result", "failure");
				resMap.put("error_code", "40003");
				resMap.put("msg", "传入omsOrderGoodsSn错误" );
				return resMap;
			}
			
			// 如果是调整供价的验证
			if(!WorkerUtil.isNullOrEmpty(unitCost)){
				if(unitCost.compareTo(BigDecimal.ZERO) == -1){
					resMap.put("result", "failure");
					resMap.put("error_code", "40004");
					resMap.put("msg", "新供价有误" );
					return resMap;
				}
				isUpdateUnitCost = true;
			}
			
			// 如果是调整供应商的验证
			if(!WorkerUtil.isNullOrEmpty(providerCode)){
				try{
					Integer.parseInt(providerCode);
				}catch(Exception e){
					resMap.put("result", "failure");
					resMap.put("error_code", "40004");
					resMap.put("msg", "供应商ID类型有误，目前支持整形，providerCode="+ providerCode );
					return resMap;
				}
				isUpdateProviderCode = true;
			}
			
		
			//>> c.获取rootItemList列表
			Map<String,Object> searchMap = new HashMap<String,Object>();
			searchMap.put("orderId", orderId);
			searchMap.put("productId", orderGood.getProduct_id());
			searchMap.put("orderGoodsId", orderGood.getOrder_goods_id());
			List<String> ItemList = inventoryDao.selectRootInventoryItemId(searchMap);
			List<AdjustPriceResDomain> adjustPriceResDomainList = new ArrayList<AdjustPriceResDomain>();
			
			if(WorkerUtil.isNullOrEmpty(ItemList)){
				resMap.put("result", "failure");
				resMap.put("error_code", "40005");
				resMap.put("msg", "没有可修改供价或供应商的记录" );
				return resMap;
			}
			
			// d.循环遍历列表，更新供价&插入历史记录
			for(String ItemId:ItemList){
//				List<AdjustPriceResDomain> resList = updateUnitCostByRootItem(ItemId, unitCost,omsOrderSn,omsOrderGoodsSn,skuCode);
				List<AdjustPriceResDomain> resList = updateUnitCostAndProviderCodeByRootItem(ItemId, unitCost, providerCode,   omsOrderSn,omsOrderGoodsSn,skuCode);
				adjustPriceResDomainList.addAll(resList);
			}
			resMap.put("adjustPriceResDomainList", adjustPriceResDomainList);
		}
		
		//3.如果到货时间需要调整，需要走下面的验证
		if(!WorkerUtil.isNullOrEmpty(arrivalTime)){
			if(orderInfoDao.updateArrivalTimeByOrderId(orderId, arrivalTime) <= 0  ){
				throw new RuntimeException("updateArrivalTimeByOrderId(orderId, arrivalTime)影响行数0");
			}
			resMap.put("pre_arrival_time", orderInfo.getArrival_time());
			resMap.put("post_arrival_time", arrivalTime);
			
			isUpdateArrivalTime = true;
		}
		
		
		if(!isUpdateUnitCost
				&& !isUpdateProviderCode
				&& !isUpdateArrivalTime ){
			resMap.put("result", "failure");
			resMap.put("error_code", "40006");
			resMap.put("msg", "三个可选参数UnitCost、ProviderCode、ArrivalTime必须要有一个不为空" );
			return resMap;
		}
		
		
		logger.info("isUpdateUnitCost=" + isUpdateUnitCost + ",isUpdateProviderCode="+ isUpdateProviderCode+ ",isUpdateArrivalTime="+ isUpdateArrivalTime );
		
		
		// 4.返回resMap
		resMap.put("result", "success");
		return resMap;
	}


	

	@Override
	public Map<String, Object> getRmaOrder(
			GetRmaOrderRequest getRmaOrderRequest, Integer customerId) {
		Map<String,Object> resMap = new HashMap<String,Object>();
		String omsOrderSn = getRmaOrderRequest.getOms_order_sn();
		
		// 1.根据omsOrderSn找到对应的orderInfo对象
		OrderInfo orderInfo = orderInfoDao.selectByOmsOrderSn(omsOrderSn);
		Integer orderId = orderInfo.getOrder_id();
		List<OrderGoods> orderGoodsList = orderGoodsDao.selectByOrderId(orderId);
		orderInfo.setOrderGoodsList(orderGoodsList);
		
		// 2.验证对象
		if(customerId.compareTo(orderInfo.getCustomer_id()) != 0){
			resMap.put("result", "failure");
			resMap.put("error_code", "40001");
			resMap.put("msg", "该订单与所在customerId不一致" );
			return resMap;
		}
		if(!OrderInfo.ORDER_TYPE_RETURN.equals(orderInfo.getOrder_type())){
			resMap.put("result", "failure");
			resMap.put("error_code", "40002");
			resMap.put("msg", "该订单不是销售退货订单" );
			return resMap;
		}
		
		
		Shipping shipping = shippingDao.selectByPrimaryKey(orderInfo.getShipping_id());
		resMap.put("wmsOrderId", orderId);
		resMap.put("omsOrderSn", orderInfo.getOms_order_sn());
		resMap.put("wmsOrderStatus", orderInfo.getOrder_status());
		resMap.put("warehouseId", orderInfo.getWarehouse_id());
		resMap.put("shippingCode", shipping.getShipping_code());
		resMap.put("trackingNumber", orderInfo.getTracking_number());
		
		//TODO orderInfo.getTracking_number没有初始化？？
		
		// 3.组装orderGoodsMap
		Map<String,Object> orderGoodsMap = new HashMap<String,Object>();
		for(OrderGoods orderGood : orderGoodsList){
			orderGoodsMap.put(orderGood.getOrder_goods_id()+"", orderGood);
		}
		
		// 4.inventoryItem信息
		List<InventoryItemDetail> inventoryItemDetailList = inventoryItemDetailDao.selectByOrderId(orderId);
		List<OrderGoodsResDomain> orderGoodsResDomainList = new ArrayList<OrderGoodsResDomain>();
		for(InventoryItemDetail inventoryItemDetail : inventoryItemDetailList){
			OrderGoodsResDomain orderGoodsResDomain = new OrderGoodsResDomain();
			Integer orderGoodsId = inventoryItemDetail.getOrder_goods_id();
			OrderGoods orderGood = (OrderGoods) orderGoodsMap.get(orderGoodsId.toString());
			orderGoodsResDomain.setOms_order_goods_sn(orderGood.getOms_order_goods_sn());
			orderGoodsResDomain.setInventory_item_id(inventoryItemDetail.getInventory_item_id());
			
			Product product = productDao.selectByPrimaryKey(orderGood.getProduct_id());
			orderGoodsResDomain.setSku_code(product.getSku_code());
			orderGoodsResDomain.setGoods_name(orderGood.getGoods_name());
			orderGoodsResDomain.setGoods_number(orderGood.getGoods_number());
			orderGoodsResDomain.setQuantity(inventoryItemDetail.getChange_quantity());
			
			InventoryItem inventoryItem = inventoryItemDao.selectByPrimaryKey(inventoryItemDetail.getInventory_item_id());
			orderGoodsResDomain.setValidity(inventoryItem.getValidity());
			orderGoodsResDomain.setBatch_sn(inventoryItem.getBatch_sn());
			orderGoodsResDomain.setStatus_id(inventoryItem.getStatus());
			orderGoodsResDomain.setSerial_number(inventoryItem.getSerial_number());
			orderGoodsResDomain.setUnit_cost(inventoryItem.getUnit_cost());
			orderGoodsResDomain.setProvider_code(inventoryItem.getProvider_code());
			orderGoodsResDomainList.add(orderGoodsResDomain);
		}
		resMap.put("result", "success");
		resMap.put("orderGoodsResDomainList", orderGoodsResDomainList);
		return resMap;
	}


	@Override
	public Map<String, Object> getSaleOrder(
			GetSaleOrderRequest getSaleOrderRequest, Integer customerId) {
		

		Map<String,Object> resMap = new HashMap<String,Object>();
		String omsOrderSn = getSaleOrderRequest.getOms_order_sn();
		
		// 1.根据omsOrderSn找到对应的orderInfo对象
		OrderInfo orderInfo = orderInfoDaoSlave.selectByOmsOrderSn(omsOrderSn);
		if(WorkerUtil.isNullOrEmpty(orderInfo)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40001");
			resMap.put("msg", "该订单未推送到WMS" );
			return resMap;
		}
		Integer orderId = orderInfo.getOrder_id();
		logger.info("开始获取order_id="+orderId+";的商品信息");
		List<OrderGoods> orderGoodsList = orderGoodsDaoSlave.selectByOrderId(orderId);
		List<OrderGoodsOms> orderGoodsOmsList = orderGoodsOmsDaoSlave.selectByOrderId(orderId);
		orderInfo.setOrderGoodsList(orderGoodsList);
		orderInfo.setOrderGoodsOmsList(orderGoodsOmsList);
		
		// 2.验证对象
		if(customerId.compareTo(orderInfo.getCustomer_id()) != 0){
			resMap.put("result", "failure");
			resMap.put("error_code", "40001");
			resMap.put("msg", "该订单与所在customerId不一致" );
			return resMap;
		}
		if(!getSaleOrderTypes.contains(orderInfo.getOrder_type())   ){
			resMap.put("result", "failure");
			resMap.put("error_code", "40002");
			resMap.put("msg", "该订单不是发货单订单类型" );
			return resMap;
		}
		resMap.put("wmsOrderId", orderId);
		resMap.put("omsOrderSn", orderInfo.getOms_order_sn());
		resMap.put("wmsOrderStatus", orderInfo.getOrder_status());
		resMap.put("warehouseId", orderInfo.getWarehouse_id());
		if(!OrderInfo.ORDER_TYPE_SUPPLIER_RETURN.equals(orderInfo.getOrder_type())){
		
			Shipping shipping = shippingDaoSlave.selectByPrimaryKey(orderInfo.getShipping_id());
			resMap.put("shippingCode", shipping.getShipping_code());
			resMap.put("trackingNumber", orderInfo.getTracking_number());
			resMap.put("shippingWmsWeight", orderInfo.getShipping_wms_weight());
		}
		
		// 3.组装orderGoodsMap & 初始化耗材列表
		List<OrderPackboxResDomain> orderPackboxResDomainList = null ;
		Map<String,Object> orderGoodsMap = new HashMap<String,Object>();
		Map<String,Object> orderGoodsOmsMap = new HashMap<String,Object>();
		for(OrderGoods orderGood : orderGoodsList){
			// >> a 初始化耗材列表（如果是耗材）
			if(OrderGoods.ORDER_GOODS_TYPE_PACKBOX.equals(orderGood.getOrder_goods_type()) ){
				if( orderPackboxResDomainList == null ){  //需要用时才进行初始化
					orderPackboxResDomainList = new ArrayList<OrderPackboxResDomain>();
				}
				
				OrderPackboxResDomain orderPackboxResDomain = new OrderPackboxResDomain();
				//留着测试
				Product product = productDaoSlave.selectByPrimaryKey(orderGood.getProduct_id());
				if(!WorkerUtil.isNullOrEmpty(product)){
					orderPackboxResDomain.setSku_code(product.getSku_code());
					orderPackboxResDomain.setGoods_number(orderGood.getGoods_number());
					orderPackboxResDomainList.add(orderPackboxResDomain);
				}
				
			}
			// >> b 组装orderGoodsMap
			orderGoodsMap.put(orderGood.getOrder_goods_id()+"", orderGood);
			
		}
		resMap.put("orderPackboxResDomainList", orderPackboxResDomainList);

		
		if(!WorkerUtil.isNullOrEmpty(orderGoodsOmsList)){
			for(OrderGoodsOms orderGoodsOms : orderGoodsOmsList ){
				orderGoodsOmsMap.put(orderGoodsOms.getOrder_goods_id() +"", orderGoodsOms);
			}
		}
		
		
		// 4.inventoryItem信息
		List<InventoryItemDetail> inventoryItemDetailList = inventoryItemDetailDaoSlave.selectByOrderId(orderId);
		List<OrderGoodsResDomain> orderGoodsResDomainList = new ArrayList<OrderGoodsResDomain>();
		for(InventoryItemDetail inventoryItemDetail : inventoryItemDetailList){
			Integer orderGoodsId = inventoryItemDetail.getOrder_goods_id();
			Integer orderGoodsOmsId = inventoryItemDetail.getOrder_goods_oms_id();
			
//			String goodsName  = 
//			Inte
//			
//			if(){
//				
//			}else{
//				
//			}
			OrderGoods orderGood = null;
			if(! WorkerUtil.isNullOrEmpty(orderGoodsId)
					&& orderGoodsId.intValue() != 0 ){
				orderGood =(OrderGoods) orderGoodsMap.get(orderGoodsId.toString());
			}
			OrderGoodsOms orderGoodOms = null;
			if(! WorkerUtil.isNullOrEmpty(orderGoodsOmsId)
					&& orderGoodsOmsId.intValue() != 0 ){
				orderGoodOms =(OrderGoodsOms) orderGoodsOmsMap.get(orderGoodsOmsId.toString());
			}
			
			
			
			// 过滤掉耗材，用于耗材和普通销售商品分离  add by qyyao at 2016-07-07
			if("N".equals(getSaleOrderRequest.getPackbox_order_goods())
					&& !WorkerUtil.isNullOrEmpty(orderGood)
					&& OrderGoods.ORDER_GOODS_TYPE_PACKBOX.equals(orderGood.getOrder_goods_type())){
				continue;
			}
			
			
			OrderGoodsResDomain orderGoodsResDomain = new OrderGoodsResDomain();
			//预包装上线，销售要取inventory_item_detail中的记录
			if(! WorkerUtil.isNullOrEmpty(orderGoodOms)){
				orderGoodsResDomain.setOms_order_goods_sn(inventoryItemDetail.getOms_order_goods_sn());
				Product product = productDaoSlave.selectByPrimaryKey(orderGoodOms.getProduct_id());
				orderGoodsResDomain.setSku_code(product.getSku_code());
				orderGoodsResDomain.setGoods_name(orderGoodOms.getGoods_name());
				orderGoodsResDomain.setGoods_number(orderGoodOms.getGoods_number());
			}else if(! WorkerUtil.isNullOrEmpty(orderGood)){
				orderGoodsResDomain.setOms_order_goods_sn(orderGood.getOms_order_goods_sn());
				Product product = productDaoSlave.selectByPrimaryKey(orderGood.getProduct_id());
				orderGoodsResDomain.setSku_code(product.getSku_code());
				orderGoodsResDomain.setGoods_name(orderGood.getGoods_name());
				orderGoodsResDomain.setGoods_number(orderGood.getGoods_number());
			}else{
				resMap.put("result", "failure");
				resMap.put("error_code", "40022");
				resMap.put("msg", "WMS中inventoryItemDetailId="+ inventoryItemDetail.getInventory_item_detail_id()+"中既不存在orderGoodsId也不存在orderGoodsOmsId对应记录" );
				return resMap;
			}
			
			orderGoodsResDomain.setInventory_item_id(inventoryItemDetail.getInventory_item_id());
			
			orderGoodsResDomain.setQuantity(inventoryItemDetail.getChange_quantity());
			
			InventoryItem inventoryItem = inventoryItemDaoSlave.selectByPrimaryKey(inventoryItemDetail.getInventory_item_id());
			orderGoodsResDomain.setValidity(inventoryItem.getValidity());
			orderGoodsResDomain.setBatch_sn(inventoryItem.getBatch_sn());
			orderGoodsResDomain.setStatus_id(inventoryItem.getStatus());
			orderGoodsResDomain.setSerial_number(inventoryItem.getSerial_number());
			orderGoodsResDomain.setUnit_cost(inventoryItem.getUnit_cost());
			orderGoodsResDomain.setProvider_code(inventoryItem.getProvider_code());
			orderGoodsResDomainList.add(orderGoodsResDomain);
			
			
		}
		
		for( OrderGoodsResDomain orderGoodsResDomain :orderGoodsResDomainList){
			logger.info("orderGoodsResDomain-> "+ orderGoodsResDomain.getOms_order_goods_sn());
			
		}
		
		resMap.put("orderGoodsResDomainList", orderGoodsResDomainList);
		
		// 5.shipment信息
		if(!OrderInfo.ORDER_TYPE_SUPPLIER_RETURN.equals(orderInfo.getOrder_type())){
			List<Shipment> shipmentList =  shipmentApiBiz.selectByOrderIdWithDetail(orderInfo.getOrder_id());
			List<ShipmentResDomain> shipmentResDomainList = null;
			if( !WorkerUtil.isNullOrEmpty(shipmentList) ){
			if( shipmentResDomainList == null ){
				shipmentResDomainList = new ArrayList<ShipmentResDomain>();
			}
			
			for (Shipment shipment : shipmentList) {
				ShipmentResDomain shipmentResDomain = new ShipmentResDomain();
				
				Shipping ship = shippingDaoSlave.selectByPrimaryKey(shipment.getShipping_id());
				Assert.notNull(ship ,"ship不能为null");
				shipmentResDomain.setShipping_code(ship.getShipping_code());
				shipmentResDomain.setTracking_number(shipment.getTracking_number());
				shipmentResDomain.setStatus(shipment.getStatus());
				shipmentResDomain.setShipping_wms_weight(shipment.getShipping_wms_weight() == null ? BigDecimal.ZERO : shipment.getShipping_wms_weight());
				
				List<ShipmentDetailResDomain> shipmentDetailResDomainList  = null;
				if( !WorkerUtil.isNullOrEmpty(shipment.getShipmentDetailList())){
					if( shipmentDetailResDomainList == null ){
						shipmentDetailResDomainList  = new ArrayList<ShipmentDetailResDomain>();
					}
					
					for (ShipmentDetail shipmentDetail : shipment.getShipmentDetailList()) {
						ShipmentDetailResDomain shipmentDetailResDomain = new ShipmentDetailResDomain();
						
						OrderGoods orderGood = (OrderGoods) orderGoodsMap.get(shipmentDetail.getOrder_goods_id()== null ? "":shipmentDetail.getOrder_goods_id().toString());
						if(!WorkerUtil.isNullOrEmpty(orderGood)){
							Product product = productDaoSlave.selectByPrimaryKey(orderGood.getProduct_id());
							if(!WorkerUtil.isNullOrEmpty(product)){
								shipmentDetailResDomain.setOms_order_goods_sn(orderGood.getOms_order_goods_sn());
								shipmentDetailResDomain.setGoods_number(shipmentDetail.getGoods_number());
								shipmentDetailResDomain.setSku_code(product.getSku_code());
								shipmentDetailResDomain.setSerial_number(shipmentDetail.getSerial_number());
								shipmentDetailResDomain.setWms_order_goods_id(orderGood.getOrder_goods_id());
								
								shipmentDetailResDomainList.add(shipmentDetailResDomain);
							}
						}
					}
					shipmentResDomain.setShipmentDetailResDomainList(shipmentDetailResDomainList);
				}
				
				
				shipmentResDomainList.add(shipmentResDomain);
			}
		}
		resMap.put("shipmentResDomainList", shipmentResDomainList);
		}
		
		
		/**物流码信息*/
		//add by xhchen 2016-08-25
		List<OmsOrderTransferCodeResDomain> shippingCodeResDomainList = new ArrayList<OmsOrderTransferCodeResDomain>();
		OrderProcess orderProcess = orderProcessDaoSlave.selectByPrimaryKey(orderInfo.getOrder_id());
		if(!WorkerUtil.isNullOrEmpty(orderProcess)){
			if(orderProcess.getIs2B().equals("W")){
				List<OmsOrderTransferCode> orderTransferCodes = omsOrderTransferCodeDaoSlave.selectByOrderId(orderInfo.getOrder_id()+"");
				for (OmsOrderTransferCode omsOrderTransferCode : orderTransferCodes) {
					OmsOrderTransferCodeResDomain resDomain = new OmsOrderTransferCodeResDomain();
					resDomain.setOms_order_sn(orderInfo.getOms_order_sn());
					resDomain.setShipping_code(omsOrderTransferCode.getTransfer_code());
					resDomain.setSku_code(omsOrderTransferCode.getSku_code());
					shippingCodeResDomainList.add(resDomain);
				}
				resMap.put("shippingCodeResDomainList", shippingCodeResDomainList);
			}
		}
		resMap.put("result", "success");
		return resMap;
		
	}

	

	
	private List<AdjustPriceResDomain> updateUnitCostAndProviderCodeByRootItem(
			String itemId, BigDecimal newUnitCost, String newProviderCode,
			String omsOrderSn, Integer omsOrderGoodsSn, String skuCode) {
		List<AdjustPriceResDomain> adjustPriceResDomainList = new ArrayList<AdjustPriceResDomain>();
		List<InventoryItem> inventoryItemList = inventoryItemDao.selectByItemId(itemId);
		
		for(InventoryItem inventoryItem : inventoryItemList){
			AdjustPriceResDomain adjustPriceResDomain = new AdjustPriceResDomain();
			
			if(!WorkerUtil.isNullOrEmpty(newUnitCost)){
				adjustPriceResDomain.setPre_unit_cost(inventoryItem.getUnit_cost());
				adjustPriceResDomain.setPost_unit_cost(newUnitCost);
				inventoryItem.setUnit_cost(newUnitCost);
			}
			if(!WorkerUtil.isNullOrEmpty(newProviderCode)){
				adjustPriceResDomain.setPre_provider_code(inventoryItem.getProvider_code());
				adjustPriceResDomain.setPost_provider_code(newProviderCode);
				inventoryItem.setProvider_code(newProviderCode);
			}
			inventoryItem.setLast_updated_time(new Date());
			inventoryItemDao.updateInventoryItem(inventoryItem);
			
			adjustPriceResDomain.setOms_order_sn(omsOrderSn);
			adjustPriceResDomain.setOms_order_goods_sn(omsOrderGoodsSn);
			adjustPriceResDomain.setSku_code(skuCode);
			adjustPriceResDomainList.add(adjustPriceResDomain);
			
			if(!WorkerUtil.isNullOrEmpty(newUnitCost)){
				InventoryItemValueHistory inventoryItemValueHistory = new InventoryItemValueHistory();
				inventoryItemValueHistory.setInventory_item_id(inventoryItem.getInventory_item_id());
				inventoryItemValueHistory.setUnit_cost(newUnitCost);
				inventoryItemValueHistory.setDate_time(new Date());
				inventoryItemValueHistory.setCreated_user("system");
				inventoryItemValueHistory.setCreated_time(new Date());
				inventoryItemValueHistory.setLast_updated_time(new Date());
				inventoryItemValueHistoryDao.insert(inventoryItemValueHistory);
			}
		}
		
		return adjustPriceResDomainList;
	}

	


	/**
	 * 支持供价调整的方法，暂时被废弃
	 * @param ItemId
	 * @param new_unitCost
	 * @param omsOrderSn
	 * @param omsOrderGoodsSn
	 * @param skuCode
	 * @return
	 */
	@Deprecated
	private List<AdjustPriceResDomain> updateUnitCostByRootItem(String ItemId,
			BigDecimal new_unitCost, String omsOrderSn, int omsOrderGoodsSn,
			String skuCode) {
		
		List<AdjustPriceResDomain> adjustPriceResDomainList = new ArrayList<AdjustPriceResDomain>();
		List<InventoryItem> inventoryItemList = inventoryItemDao.selectByItemId(ItemId);
		
		for(InventoryItem inventoryItem : inventoryItemList){
			AdjustPriceResDomain adjustPriceResDomain = new AdjustPriceResDomain();
			adjustPriceResDomain.setPre_unit_cost(inventoryItem.getUnit_cost());
			
			inventoryItem.setUnit_cost(new_unitCost);
			inventoryItem.setLast_updated_time(new Date());
			inventoryItemDao.updateInventoryItem(inventoryItem);
			
			adjustPriceResDomain.setOms_order_sn(omsOrderSn);
			adjustPriceResDomain.setOms_order_goods_sn(omsOrderGoodsSn);
			adjustPriceResDomain.setSku_code(skuCode);
			adjustPriceResDomain.setPost_unit_cost(new_unitCost);
			adjustPriceResDomainList.add(adjustPriceResDomain);
			
			InventoryItemValueHistory inventoryItemValueHistory = new InventoryItemValueHistory();
			inventoryItemValueHistory.setInventory_item_id(inventoryItem.getInventory_item_id());
			inventoryItemValueHistory.setUnit_cost(new_unitCost);
			inventoryItemValueHistory.setDate_time(new Date());
			inventoryItemValueHistory.setCreated_user("system");
			inventoryItemValueHistory.setCreated_time(new Date());
			inventoryItemValueHistory.setLast_updated_time(new Date());
			inventoryItemValueHistoryDao.insert(inventoryItemValueHistory);
		}
		
		return adjustPriceResDomainList;
	}


	@Override
	public Map<String, Object> getOrderList(
			GetOrderListRequest getOrderListRequest, Integer customerId) {
		
		
		// 1.初始化订单列表查询的相关变量
		Map<String,Object> resMap = new HashMap<String,Object>();
		Date startModifiedTime = getOrderListRequest.getStart_modified_time();
		Date endModifiedTime = getOrderListRequest.getEnd_modified_time();
		String wmsOrderType = getOrderListRequest.getWms_order_type();
		String wmsOrderStatus = getOrderListRequest.getWms_order_status();
		Integer pageNo = getOrderListRequest.getPage_no();
		Integer pageSize = getOrderListRequest.getPage_size();
		
		
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
		
		if(WorkerUtil.isNullOrEmpty(wmsOrderType)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40004");
			resMap.put("msg", "wms_order_type订单类型为空" );
			return resMap;
		}
		if(!getOrderListOrderTypes.contains(wmsOrderType) ){
			resMap.put("result", "failure");
			resMap.put("error_code", "40005");
			resMap.put("msg", "wms不存在该订单类型(wms_order_type):" + wmsOrderType );
			return resMap;
		}
		if(!WorkerUtil.isNullOrEmpty(wmsOrderStatus)
				&& !getOrderListOrderStatuses.contains(wmsOrderStatus) ){
			resMap.put("result", "failure");
			resMap.put("error_code", "40006");
			resMap.put("msg", "wms不存在该订单状态(wms_order_status):" + wmsOrderStatus  );
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
		paramsMap.put("orderType", wmsOrderType);
		paramsMap.put("orderStatus", wmsOrderStatus);
		paramsMap.put("offset", offset);
		paramsMap.put("rows", pageSize);
		
		List<OrderInfoResDomain>  orderInfoResDomainList = orderInfoDaoSlave.selectOrderInfoResDomainList(paramsMap);
		
		//4.返回订单列表
		resMap.put("total_count", orderInfoResDomainList != null ? orderInfoResDomainList.size(): 0 );
		resMap.put("orderInfoResDomainList", orderInfoResDomainList);
		
		resMap.put("result", "success");
		return resMap;
	}
	
	
	/**
	 * @author xhchen
	 * 获取发货的耗材信息
	 */
	@Override
	public Map<String, Object> getOrderShipmentList(GetOrderShipmentListRequest getOrderShipmentListRequest,
			Integer customerId) {
		
		
		// 1.初始化订单列表查询的相关变量
		Map<String,Object> resMap = new HashMap<String,Object>();
		Date startModifiedTime = getOrderShipmentListRequest.getStart_modified_time();
		Date endModifiedTime = getOrderShipmentListRequest.getEnd_modified_time();
		String wmsOrderType = getOrderShipmentListRequest.getWms_order_type();
		String wmsOrderStatus = getOrderShipmentListRequest.getWms_order_status();
		Integer pageNo = getOrderShipmentListRequest.getPage_no();
		Integer pageSize = getOrderShipmentListRequest.getPage_size();
		String isInOut = getOrderShipmentListRequest.getIs_in_out();
		
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
		
		if(WorkerUtil.isNullOrEmpty(wmsOrderType)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40004");
			resMap.put("msg", "wms_order_type订单类型为空" );
			return resMap;
		}
		if(!getOrderListOrderTypes.contains(wmsOrderType) ){
			resMap.put("result", "failure");
			resMap.put("error_code", "40005");
			resMap.put("msg", "wms不存在该订单类型(wms_order_type):" + wmsOrderType );
			return resMap;
		}
		if(!WorkerUtil.isNullOrEmpty(wmsOrderStatus)
				&& !getOrderListOrderStatuses.contains(wmsOrderStatus) ){
			resMap.put("result", "failure");
			resMap.put("error_code", "40006");
			resMap.put("msg", "wms不存在该订单状态(wms_order_status):" + wmsOrderStatus  );
			return resMap;
		}
		
		if(WorkerUtil.isNullOrEmpty(pageNo)){
			pageNo = DEFALUT_PAGE_NO;
		}
		if(WorkerUtil.isNullOrEmpty(pageSize)){
			pageSize = DEFALUT_PAGE_SIZE;
		}
		//默认为已出库的耗材
		if(WorkerUtil.isNullOrEmpty(isInOut)){
			isInOut="Y";
		}
		
		//3.获取响应订单列表
		int offset = pageSize * ( pageNo - 1);
		
		Map<String,Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customerId", customerId);
		paramsMap.put("startModifiedTime", startModifiedTime);
		paramsMap.put("endModifiedTime", endModifiedTime);
		paramsMap.put("orderType", wmsOrderType);
		paramsMap.put("orderStatus", wmsOrderStatus);
		paramsMap.put("isInOut", isInOut);
		paramsMap.put("offset", offset);
		paramsMap.put("rows", pageSize);
		
		//查询wms已出库，oms未出库的shipment列表
		List<Shipment> shipments=orderInfoDaoSlave.selectOrderShipmentList(paramsMap);
		
		List<OrderShipmentResDomain> orderShipmentResDomains=convertShipmentToOrderShipmentResDomain(shipments);
		//4.返回订单列表
		resMap.put("total_count", orderShipmentResDomains != null ? orderShipmentResDomains.size(): 0 );
		resMap.put("orderShipmentResDomains", orderShipmentResDomains);
		
		resMap.put("result", "success");
		return resMap;
	}
	
	/**
	 * 将shipments转化为需要的数据结构
	 * @param shipments
	 * @return
	 */
	private List<OrderShipmentResDomain> convertShipmentToOrderShipmentResDomain(List<Shipment> shipments){
		logger.info("start convertShipmentToOrderShipmentResDomain");
		Map<Integer, List<PackBoxResDomain>> map = new HashMap<Integer , List<PackBoxResDomain>>();
		for (Shipment shipment : shipments) {
			PackBoxResDomain packBoxResDomain = new PackBoxResDomain();
			String skuCode=shipmentDaoSlave.getPackBoxSkuCodeByPid(shipment.getPackbox_product_id());
			packBoxResDomain.setSku_code(skuCode);
			packBoxResDomain.setTracking_number(shipment.getTracking_number());
			packBoxResDomain.setIs_in_out(shipment.getPackbox_is_out());
			if(WorkerUtil.isNullOrEmpty(map.get(shipment.getOrder_id()))){
				List<PackBoxResDomain> list = new ArrayList<PackBoxResDomain>();
				map.put(shipment.getOrder_id(), list);
			}
			map.get(shipment.getOrder_id()).add(packBoxResDomain);
		}
		List<OrderShipmentResDomain> list = new ArrayList<OrderShipmentResDomain>();
		for (Map.Entry entry : map.entrySet()) {
			OrderShipmentResDomain orderShipmentResDomain=new OrderShipmentResDomain();
			String key = entry.getKey().toString();
			orderShipmentResDomain.setOrder_id(key);
			orderShipmentResDomain.setOms_order_sn(orderInfoDaoSlave.selectByPrimaryKey(Integer.valueOf(key)).getOms_order_sn());
		    List<PackBoxResDomain> packBoxResDomains = (List) entry.getValue();
		    for (PackBoxResDomain value : packBoxResDomains) {
		    	orderShipmentResDomain.getPackBoxResDomains().add(value);
		    }
		    list.add(orderShipmentResDomain);
		}
		return list;
	}
	
	@Override
	public Map<String, Object> terminalOrder(TerminalOrderRequest terminalOrderRequest, Integer customerId) {
		Map<String,Object> resMap = new HashMap<String, Object>();
		OrderInfo orderInfo = orderInfoDao.selectTerminalOrderByOmsOrderSn(terminalOrderRequest.getOms_order_sn());
		if(WorkerUtil.isNullOrEmpty(orderInfo)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40004");
			resMap.put("msg", "oms_order_sn订单不存在" );
			return resMap;
		}
		
		for(TerminalGoodsReqDomain terminalGoodsReqDomain:terminalOrderRequest.getGoodsReqDomains()){
			if(WorkerUtil.isNullOrEmpty(orderGoodsDao.selectByOmsOrderGoodsSn(terminalGoodsReqDomain.getOms_order_goods_sn()))){
				resMap.put("result", "failure");
				resMap.put("error_code", "40004");
				resMap.put("msg", "oms_order_goods_sn"+terminalGoodsReqDomain.getOms_order_goods_sn()+",商品不存在" );
				return resMap;
			}
		}
		
		/**
		 * 比较入库数量是否相等
		 */
		for(TerminalGoodsReqDomain terminalGoodsReqDomain:terminalOrderRequest.getGoodsReqDomains()){
			OrderGoods orderGoods = orderGoodsDao.selectByOmsOrderGoodsSn(terminalGoodsReqDomain.getOms_order_goods_sn());
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("order_id", orderInfo.getOrder_id());
			paramMap.put("order_goods_id", orderGoods.getOrder_goods_id());
			List<InventoryItemDetail> inventoryItemDetails = inventoryItemDetailDao.selectByOrderIdAndGoodsId(paramMap);
			int quatitySum = 0;
			if(!WorkerUtil.isNullOrEmpty(inventoryItemDetails)){
				for (InventoryItemDetail inventoryItemDetail : inventoryItemDetails) {
					quatitySum+=inventoryItemDetail.getChange_quantity();
				}
			}
			if(quatitySum!=terminalGoodsReqDomain.getQuatity()){
				resMap.put("result", "failure");
				resMap.put("error_code", "40004");
				resMap.put("msg", "oms_order_goods_sn"+terminalGoodsReqDomain.getOms_order_goods_sn()+",商品入库数量不相同" );
				return resMap;
			}
			
		}
		
		if(terminalOrderStatus.contains(orderInfo.getOrder_status())){
			if(orderInfoDao.updateOrderInfoInProcess(terminalOrderRequest.getOms_order_sn(), "wmsclient", OrderInfo.ORDER_STATUS_ABORTED)<=0){
				resMap.put("result", "failure");
				resMap.put("error_code", "40004");
				resMap.put("msg", "更新状态失败" );
				return resMap;
			}
			resMap.put("result", "success");
			resMap.put("error_code", "S01");
			resMap.put("msg", "订单"+terminalOrderRequest.getOms_order_sn()+"终止成功" );
			return resMap;
		}else {
			resMap.put("result", "failure");
			resMap.put("error_code", "40004");
			resMap.put("msg", "当前订单状态不允许终止,status=" + orderInfo.getOrder_status() );
			return resMap;
		}
	}
	
	//加工单取消
	@Override
	public Map<String, Object> cancelOrderPrepack(OrderPrepack orderPrepack){
		Map<String,Object> resMap = new HashMap<String, Object>();
		if(WorkerUtil.isNullOrEmpty(orderPrepack)){
			resMap.put("result", "failure");
			resMap.put("msg", "orderPrepack不存在" );
			return resMap;
		}
		orderPrepack = orderPrepackDao.selectOrderPrepackByOrderIdForUpdate(orderPrepack.getOrder_id());
		if(OrderInfo.ORDER_STATUS_CANCEL.equalsIgnoreCase(orderPrepack.getStatus())){
			resMap.put("result", "failure");
			resMap.put("msg", "orderPrepack已取消");
			return resMap;
		}
		productLocationBiz.cancelOrderPrepackageProductLocationReservation(orderPrepack.getOrder_id(),orderPrepack.getPrepackage_product_id(),orderPrepack.getType());
		resMap.put("result", "success");
		return resMap;
	}
	
	/**
	 * @author xhchen
	 * 推送任务
	 */
	@Override
	public HashMap<String, Object> syncPrepackTask(SyncOrderPrepackRequest request, Integer customerId) {
		HashMap<String,Object> resMap = new HashMap<String,Object>();
		OrderPrepack orderPrepackExist=orderPrepackDao.selectOrderPrepackByOmsTaskSn(request.getJob_id()+"");
		if(!WorkerUtil.isNullOrEmpty(orderPrepackExist)){
			resMap.put("result", "success");
			resMap.put("error_code", "00000");
			resMap.put("wmsOrderSn", orderPrepackExist.getOrder_sn());
			resMap.put("omsTaskSn", orderPrepackExist.getOms_task_sn());
			resMap.put("msg", "jobId="+request.getJob_id()+"；已经被推送");
			logger.info("jobId="+request.getJob_id()+"；已经被推送");
			return resMap;
		}
		//单品预打包任务的特殊处理：如果sku_code是xxxxxx_x的形式，后面再添加"_p"；如果sku_code是xxxxxx的形式，后面添加"_0_p"。
		if(!request.getTc_code().startsWith("TC")){
			if(!request.getTc_code().contains("_")){
				request.setTc_code(request.getTc_code()+"_0_p");
			}else{
				request.setTc_code(request.getTc_code()+"_p");
			}
			
		}
		Product product1 = productDao.selectBySkuCode(request.getTc_code());
		
		if(!WorkerUtil.isNullOrEmpty(product1)){
			List<Map<String,String>> mapList = productBiz.selectProductsBySkuCode(request.getTc_code());
			if(mapList.size()!=request.getPrepackReqDomain().size()){
				resMap.put("result", "failure");
				resMap.put("error_code", "40006");
				resMap.put("msg", "套餐商品详情和以前的不一样,可能多了商品/少了商品");
				logger.info("套餐商品详情和以前的不一样,可能多了商品/少了商品");
				return resMap;
			}
			
			for (PrepackReqDomain prepackReqDomain : request.getPrepackReqDomain()) {
				boolean status =false;//标记是否存在相同的skucode
				String g_s_n=prepackReqDomain.getSku_code()+"_"+prepackReqDomain.getGoods_number();
				for (Map map : mapList) {
					logger.info("g_s_n="+g_s_n+"||map="+map.get("sku_code")+"_"+map.get("number").toString());
					if (g_s_n.equals(map.get("sku_code")+"_"+map.get("number").toString())) {
						status = true;
					}
				}
				/* 
				logger.info(prepackReqDomain.getGoods_number().equals(map.get("number").toString()));
				
				if (prepackReqDomain.getGoods_number()!=Integer.valueOf((map.get("number").toString()))) {
					logger.info("skucode="+prepackReqDomain.getSku_code()+"||number="+map.get("number")+"||goods_number="+prepackReqDomain.getGoods_number());
					resMap.put("result", "failure");
					resMap.put("error_code", "40006");
					resMap.put("msg", "套餐内的商品sku_code="+prepackReqDomain.getSku_code()+",数量不相同"+prepackReqDomain.getGoods_number()+"-"+map.get("number"));
					logger.info("套餐内的商品sku_code="+prepackReqDomain.getSku_code()+",数量不相同"+prepackReqDomain.getGoods_number()+"-"+map.get("number"));
					return resMap;
				}
				 */
				
				if(!status){
					resMap.put("result", "failure");
					resMap.put("error_code", "40006");
					resMap.put("msg", "存在和以前套餐不同的商品"+prepackReqDomain.getSku_code()+"，请检查");
					logger.info("存在和以前套餐不同的商品"+prepackReqDomain.getSku_code()+"，请检查");
					return resMap;
				}
			}
		}
		
		
		
		
		
		List<ProductPrepackage> prepackages = new ArrayList<ProductPrepackage>();
		//1.将遇打包任务作为一个商品
		WarehouseCustomer warehouseCustomer = warehouseCustomerDao.selectByCustomerId(customerId);
		if(WorkerUtil.isNullOrEmpty(warehouseCustomer)){
			resMap.put("result", "failure");
			resMap.put("error_code", "40006");
			resMap.put("msg", "customerId在wms中不存在");
			return resMap;
		}
		
		
		Integer packbox_product_id = 0;  //初始值为0
		// 校验耗材，并初始化耗材的productId
		if(!WorkerUtil.isNullOrEmpty(request.getPackbox_sku_code())){
			
			Product packboxProduct = productDao.selectBySkuCode(request.getPackbox_sku_code());  //耗材商品
			if(WorkerUtil.isNullOrEmpty(packboxProduct)){
				resMap.put("result", "failure");
				resMap.put("error_code", "40010");
				resMap.put("msg", "耗材packbox_sku_code " +request.getPackbox_sku_code()+ " 在wms中不存在对应的商品");
				return resMap;
			}else{
				packbox_product_id = packboxProduct.getProduct_id();
			}
		}
		
		
		
		Product product = new Product();
		product.setBarcode(request.getTc_barcode());
		product.setBrand_name(warehouseCustomer.getName());
		product.setSku_code(request.getTc_code());
		product.setProduct_name(request.getTc_name());
		product.setCat_name(warehouseCustomer.getName());
		product.setCreated_time(new Date());
		product.setCreated_user("system");
		product.setCustomer_id(customerId);
		product.setLast_updated_time(new Date());
		product.setSpec(1);
		Integer min_validity=Integer.MAX_VALUE;
		for(PrepackReqDomain prepackReqDomain:request.getPrepackReqDomain()){
			logger.info("prepackReqDomain="+prepackReqDomain.getSku_code());
			
			Product temp = productDao.selectBySkuCode(prepackReqDomain.getSku_code());//sku_code = goods_id + style_id
			
			if(WorkerUtil.isNullOrEmpty(temp)){
				resMap.put("result", "failure");
				resMap.put("error_code", "40006");
				resMap.put("msg", "sku_code="+prepackReqDomain.getSku_code()+",对应的商品信息不存在");
				return resMap;
			}
			
			if(temp.getValidity()<min_validity)
				min_validity=temp.getValidity();
			logger.info("min_validity="+min_validity+"||temp.productId="+temp.getProduct_id());
			//保存中间表,预打包商品组件ID在保存时更新
			ProductPrepackage productPrepackage = new ProductPrepackage();
			productPrepackage.setNumber(prepackReqDomain.getGoods_number());
			productPrepackage.setComponent_product_id(temp.getProduct_id());
			prepackages.add(productPrepackage);
		}
		product.setValidity(min_validity);//保质期取商品中最小的一个
		product.setProduct_type("PREPACKAGE");//预打包商品
		product.setIs_delete("N");
		product.setIs_maintain_weight("N");
		product.setIs_maintain_batch_sn("N");
		product.setIs_contraband("N");
		product.setIs_serial("N");
		
		
		OrderPrepack orderPrepack = new OrderPrepack();
		if(!WorkerUtil.isNullOrEmpty(request.getEnd_time())){
			orderPrepack.setActivity_end_time(request.getEnd_time());
		}else {
			orderPrepack.setActivity_end_time(new Date());
		}
		if(!WorkerUtil.isNullOrEmpty(request.getStart_time())){
			orderPrepack.setActivity_start_time(request.getStart_time());
		}else {
			orderPrepack.setActivity_start_time(new Date());
		}
		orderPrepack.setCreated_time(new Date());
		orderPrepack.setCreated_user("system");
		orderPrepack.setCustomer_id(customerId);
		orderPrepack.setLast_updated_time(new Date());
		orderPrepack.setStatus("INIT");
		orderPrepack.setType(request.getJob_type());
		orderPrepack.setOms_task_sn(request.getJob_id()+"");
		orderPrepack.setOrder_sn(getWmsTaskOrderSn(customerId+""));
		orderPrepack.setQty_need(request.getQuantity());
		orderPrepack.setIgnore_qty_used(request.getIgnore_qty_user());
		orderPrepack.setPhysical_warehouse_id(request.getPhysical_facility_id());
		orderPrepack.setWarehouse_id(request.getWarehouse_id());
		orderPrepack.setPackbox_product_id(packbox_product_id);  //添加设置耗材商品ID
		orderPrepack.setNote(request.getNote());
		orderPrepack.setQty_actual(0);
		orderPrepack.setQty_used(0);
		orderPrepack.setPack_type(request.getPack_type());
		//保存预打包任务信息
		orderPrepackBiz.save(orderPrepack, product, prepackages);
		resMap.put("result", "success");
		resMap.put("error_code", "00000");
		resMap.put("msg", "保存成功");
		resMap.put("wmsOrderSn", orderPrepack.getOrder_sn());
		resMap.put("omsTaskSn", orderPrepack.getOms_task_sn());
		return resMap;
	}
	
	private String getWmsTaskOrderSn(String customerId){
		SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
		String dateString=format.format(new Date());
		DecimalFormat df=new DecimalFormat("000000");
		String dString=df.format(orderPrepackDao.selectMaxOrderId()+1);
		return "J"+dateString+dString;
	}
	
	@Override
	public List<OrderPrepack> getPrepackList(GetOrderPrepacksRequest request, Integer customerId) {
		List<OrderPrepack> orderPrepacks = orderPrepackDao.selectOrderPrepackByParam(customerId, request.getStartTime(), request.getEndTime());
		return orderPrepacks;
	}
	
	
	
}
