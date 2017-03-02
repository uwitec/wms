/**
 * 波次相关
 * */
package com.leqee.wms.biz.impl;

import java.lang.reflect.UndeclaredThrowableException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.api.util.Tools;
import com.leqee.wms.biz.BatchPickBiz;
import com.leqee.wms.biz.OrderPrepackBiz;
import com.leqee.wms.controller.SaleController;
import com.leqee.wms.dao.BatchPickDao;
import com.leqee.wms.dao.BatchPickTaskDao;
import com.leqee.wms.dao.ConfigPrintDispatchBillDao;
import com.leqee.wms.dao.InventoryDao;
import com.leqee.wms.dao.LabelPrepackDao;
import com.leqee.wms.dao.LocationDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.OrderPrepackDao;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.ProductLocationDetailDao;
import com.leqee.wms.dao.ProductPrepackageDao;
import com.leqee.wms.dao.RegionDao;
import com.leqee.wms.dao.ShippingDao;
import com.leqee.wms.dao.SysUserDao;
import com.leqee.wms.dao.TaskDao;
import com.leqee.wms.dao.UserActionBatchPickDao;
import com.leqee.wms.dao.UserActionOrderDao;
import com.leqee.wms.dao.UserActionOrderPrepackDao;
import com.leqee.wms.dao.UserActionTaskDao;
import com.leqee.wms.dao.UserActionWarehouseLoadDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.dao.WarehouseLoadDao;
import com.leqee.wms.entity.BatchPick;
import com.leqee.wms.entity.BatchPickTask;
import com.leqee.wms.entity.ConfigReplenishment;
import com.leqee.wms.entity.LabelPrepack;
import com.leqee.wms.entity.Location;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.OrderPrepack;
import com.leqee.wms.entity.OrderProcess;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.entity.ProductLocationDetail;
import com.leqee.wms.entity.ProductPrepackage;
import com.leqee.wms.entity.SameDetails;
import com.leqee.wms.entity.ScheduleJob;
import com.leqee.wms.entity.Task;
import com.leqee.wms.entity.UserActionBatchPick;
import com.leqee.wms.entity.UserActionOrder;
import com.leqee.wms.entity.UserActionOrderPrepack;
import com.leqee.wms.entity.UserActionTask;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.response.Response;
import com.leqee.wms.response.ResponseFactory;
import com.leqee.wms.util.SequenceUtil;
import com.leqee.wms.util.WorkerUtil;

@Service
public class OrderPrepackBizImpl implements OrderPrepackBiz {
	private Logger logger = Logger.getLogger(OrderPrepackBizImpl.class);
	
	@Autowired 
	OrderPrepackDao orderPrepackDao;
	@Autowired
	ProductDao productDao;
	@Autowired
	TaskDao taskDao;
	@Autowired
	ProductLocationDao productLocationDao;
	@Autowired
	ProductLocationDetailDao productLocationDetailDao;
	@Autowired
	InventoryDao inventoryDao;
	@Autowired
	LocationDao locationDao;
	@Autowired
	UserActionOrderDao userActionOrderDao;
	@Autowired
	UserActionTaskDao userActionTaskDao;
	@Autowired
	LabelPrepackDao labelPrepackDao;
	@Autowired
	UserActionOrderPrepackDao userActionOrderPrepackDao;
	@Autowired
	ProductPrepackageDao productPrepackageDao;
	
	

	@Override
	public Map<String, Object> loadGoodsForOrder(String order_sn,String type){
		String con =""; 
		Integer taskQuantity =0;
		List<Map<String, Object>> waitcheckList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> finishcheckList = new ArrayList<Map<String,Object>>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if("0".equals(type)){
			type="PACK";
		}else {
			type="UNPACK";
		}
		Map<String, Object> preCheckMap = orderPrepackDao.selectOrderPrepackByOrderSn(order_sn,type);
		if(WorkerUtil.isNullOrEmpty(preCheckMap)){
			resultMap.put("note", "不存在的扫描任务单号或者状态已完成");
			resultMap.put("result",Response.FAILURE);
			return resultMap;
		}
		List<Map<String, Object>> productPrePackageList = orderPrepackDao.selectProductPrePackage(Integer.parseInt(preCheckMap.get("prepackage_product_id").toString()));
		if(WorkerUtil.isNullOrEmpty(productPrePackageList)){
			resultMap.put("note", "不存在的组合商品信息");
			resultMap.put("result",Response.FAILURE);
			return resultMap;
		}
		List<Task> unPackageList = orderPrepackDao.selectUnPackageList(Integer.parseInt(preCheckMap.get("order_id").toString()),Integer.parseInt(preCheckMap.get("prepackage_product_id").toString()));
		for (Task task : unPackageList) {
			taskQuantity = taskQuantity+task.getQuantity();
		}
		for (Map<String, Object> map : productPrePackageList) {
			con =con+map.get("barcode").toString()+"*"+map.get("number").toString()+"+";
			Map<String, Object> productMap = new HashMap<String, Object>();
			productMap.put("barcode", map.get("barcode"));
			productMap.put("product_name", map.get("product_name"));
			productMap.put("number", Integer.parseInt(map.get("number").toString()));
			waitcheckList.add(productMap);
			finishcheckList.add(productMap);
			
		}
		logger.info("组合方式："+con);
		resultMap.put("waitcheckList", waitcheckList);
		resultMap.put("pack_type", preCheckMap.get("pack_type").toString());
		resultMap.put("order_id", preCheckMap.get("order_id"));
		resultMap.put("finishcheckList", finishcheckList);
		resultMap.put("unionType", con.substring(0, con.length()-1));
		resultMap.put("comboBarcode", preCheckMap.get("barcode"));
		resultMap.put("comboName", preCheckMap.get("product_name"));
		if("PACK".equals(type)){
			resultMap.put("prePackageNumber", Integer.parseInt(preCheckMap.get("qty_need").toString())-Integer.parseInt(preCheckMap.get("qty_actual").toString()));
		}else {
			resultMap.put("prePackageNumber",taskQuantity-Integer.parseInt(preCheckMap.get("qty_actual").toString()));
		}
		
		resultMap.put("result",Response.SUCCESS);
		return resultMap;
	}
	@Override
	public Map<String, Object> getCheckOrderGoodsConfirm(Integer check_order_id,String packbox_product_barcode){
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if("WMS888888888".equals(packbox_product_barcode)) {
			resultMap.put("result", Response.SUCCESS);
			resultMap.put("note", "该耗材商品无需记录");
			resultMap.put("packbox_product_id",0);
			return resultMap;
		}
		Product product = productDao.selectProductByBarcode(packbox_product_barcode);
		if(WorkerUtil.isNullOrEmpty(product)){
			resultMap.put("note", "该耗材商品不存在");
			resultMap.put("result", Response.FAILURE);
		}else {
			resultMap.put("result", Response.SUCCESS);
			resultMap.put("note", "耗材扫描成功");
			resultMap.put("packbox_product_id", product.getProduct_id());
		}
		
		return resultMap;
		
		
	}
	/**
	 * 复核加工上架
	 */
	@Override
	public Map<String, Object> groundingPrint(Integer order_id,Integer packbox_product_id,String userName,Integer preNumber,Integer prePackageNumber){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> printMap = new HashMap<String, Object>();
		if(WorkerUtil.isNullOrEmpty(order_id)){
			resultMap.put("note", "订单号不存在");
			logger.info("预打包复核失败");
			return resultMap;
		}
		List<Map<String, Object>> prePackageList = orderPrepackDao.selectPrePackageList(order_id);
		if(WorkerUtil.isNullOrEmpty(prePackageList)){
			resultMap.put("note", "预打包商品不存在");
			logger.info("预打包复核失败");
			return resultMap;
		}
		//对预打包数量进行校验
		Integer changeQuantity = productLocationDetailDao.selectPackageProductLocationDetailByOrderId(order_id);
		if(WorkerUtil.isNullOrEmpty(changeQuantity)){
			changeQuantity = 0;
		}
		Map<String, Object> preCheckMap = orderPrepackDao.selectOrderPrepackByOrderId(order_id);
		if(OrderInfo.ORDER_STATUS_FULFILLED.equals(preCheckMap.get("status").toString()) ||
				OrderInfo.ORDER_STATUS_CANCEL.equals(preCheckMap.get("status").toString())){
			resultMap.put("note", "该订单已取消或者已完结");
			logger.info("预打包复核失败");
			return resultMap;
		}
		if(preNumber+changeQuantity>Integer.parseInt(preCheckMap.get("qty_need").toString())){
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note","该订单商品上架数量已超过该订单的商品数量");
			logger.info("预打包复核失败");
			return resultMap;
		}
		try {
		//开始扣减预打包商品库存
		String validity = "2999-01-01";
		//对task任务进行拆分扣减
		for (Map<String, Object> map2 : prePackageList) {
		int num1 =preNumber*Integer.valueOf(map2.get("number").toString());
		int inNum=0;
		Integer task_id = null;
		Integer productlocationdetailtotal=0;
		productlocationdetailtotal = orderPrepackDao.selectSumTotal(order_id, Integer.valueOf(map2.get("component_product_id").toString()));
		if(WorkerUtil.isNullOrEmpty(productlocationdetailtotal)){
			productlocationdetailtotal=0;
		}
		logger.info("该商品已入数量:"+Integer.valueOf(map2.get("component_product_id").toString())+"productlocationdetailtotal:"+productlocationdetailtotal+"inNum:"+inNum);
		List<Task> taskList = orderPrepackDao.getTaskIdByOrderId(order_id,Integer.valueOf(map2.get("component_product_id").toString()));
		for (Task task : taskList) {
			ProductLocation productLocation2 = productLocationDao.getFromProduLocations(task.getTask_id(), task.getFrom_pl_id());
			validity = productLocation2.getValidity();
			Integer qty_total = task.getQuantity()+productlocationdetailtotal;
			if(qty_total >0){
				productlocationdetailtotal= 0;
			}
			if(qty_total <=0){
				productlocationdetailtotal=qty_total;
				continue;
			}
			else if(num1<=0){
				break;
			}
			else if(num1<=qty_total){
				inNum = -1*num1;
				num1 = 0;
			}else{
				inNum = -1*qty_total;
				num1 = num1-qty_total;
			}
			task_id = task.getTask_id();
			logger.info("开始扣减预打包商品库存");
			
			if(validity.compareTo(productLocation2.getValidity())>0){
				validity = productLocation2.getValidity();
			}
			task_id = task.getTask_id();
			//更新中转区库存值
			int col =productLocationDao.updateProductLocationTotal(inNum, productLocation2.getPl_id());
			if(col<1){
				throw new RuntimeException("更新库存失败");
			}
			ProductLocationDetail productLocationDetail = new ProductLocationDetail();
			productLocationDetail.setPl_id(productLocation2.getPl_id());
			productLocationDetail.setChange_quantity(inNum);
			productLocationDetail.setTask_id(task_id);
			productLocationDetail.setDescription("预打包扣减原库存商品总数");
			productLocationDetail.setCreated_user(userName);
			productLocationDetail.setLast_updated_user(userName);
			productLocationDetail.setOrder_id(order_id);
			//productLocationDetail.setOrder_goods_id(Integer.parseInt(map.get("orderGoodsId").toString()));
			productLocationDetailDao.insert(productLocationDetail);
		}
		if (num1!=0) {
			throw new RuntimeException("该商品没有全部扣减");
		}
		}
	
		// 生成打印标签
		String locationBarcode = WorkerUtil.generatorSequence(SequenceUtil.KEY_NAME_TAGCODE,"C",true);
		
		//插入location表
		logger.info(order_id+"插入location表");
		Location location = new Location();
		location.setPhysical_warehouse_id(Integer.parseInt(preCheckMap.get("physical_warehouse_id").toString()));
		location.setLocation_barcode(locationBarcode);
		location.setLocation_type("PREPACK_SEQ");
		location.setIs_delete("N");
		location.setCreated_user(userName);
		location.setCustomer_id(Integer.parseInt(preCheckMap.get("customer_id").toString()));
		inventoryDao.insertLocation(location);
		logger.info(">>>Generate locationBarcode："+locationBarcode);
		
		//预打包商品上架
		logger.info(order_id+"预打包商品上架");
		ProductLocation productLocation = new ProductLocation();
		productLocation.setProduct_id(Integer.parseInt(preCheckMap.get("prepackage_product_id").toString()));
		productLocation.setLocation_id(location.getLocation_id());
		productLocation.setQty_total(preNumber);
		productLocation.setQty_available(0);
		productLocation.setProduct_location_status("NORMAL");
		//productLocation.setQty_exception(0);
		productLocation.setStatus("NORMAL");
		if (!WorkerUtil.isNullOrEmpty(validity)) {
			productLocation.setValidity(validity.toString());
		}
		//productLocation.setBatch_sn(batchSn);
		productLocation.setSerial_number("");
		productLocation.setCreated_user(userName);
		productLocation.setLast_updated_user(userName);
		productLocation.setWarehouse_id(Integer.parseInt(preCheckMap.get("warehouse_id").toString()));
		productLocationDao.insert(productLocation);
		
		//创建上架任务
		logger.info(order_id+"创建上架任务");
		Task task = new Task();
		task.setPhysical_warehouse_id(Integer.parseInt(preCheckMap.get("physical_warehouse_id").toString()));
		task.setCustomer_id(Integer.parseInt(preCheckMap.get("customer_id").toString()));
		task.setTask_type(Task.TASK_TYPE_PREPACK_PUT_AWAY);
		task.setTask_status(Task.TASK_STATUS_INIT);
		task.setTask_level(1);
		task.setOrder_id(order_id);
		task.setFrom_pl_id(productLocation.getPl_id());
		task.setProduct_id(Integer.parseInt(preCheckMap.get("prepackage_product_id").toString()));
		task.setQuantity(preNumber);
		task.setOperate_platform("WEB");
		task.setCreated_user(userName);
		task.setLast_updated_user(userName);
		taskDao.insertTask(task);
		//创建UserActionTask表
		logger.info(order_id+"创建UserActionTask表");
		this.insertUserActionTask(userName,task,"WEB预打包商品生成上架任务");
	
		ProductLocationDetail productLocationDetail = new ProductLocationDetail();
		productLocationDetail.setPl_id(productLocation.getPl_id());
		productLocationDetail.setChange_quantity(preNumber);
		productLocationDetail.setTask_id(task.getTask_id());
		productLocationDetail.setDescription("预打包上架");
		productLocationDetail.setCreated_user(userName);
		productLocationDetail.setLast_updated_user(userName);
		productLocationDetail.setOrder_id(order_id);
		//productLocationDetail.setOrder_goods_id(Integer.parseInt(map.get("orderGoodsId").toString()));
		productLocationDetailDao.insert(productLocationDetail);
		if("Y".equals(location.getIs_empty())){
			locationDao.updateLocationIsEmpty1(location.getLocation_id());
		}
		//更新order_prepack表中的实际打包数量
		orderPrepackDao.updateQtyActual(order_id, preNumber);
		//创建预打包上架标签表
		logger.info(order_id+"创建预打包上架标签表");
		this.insertLabelPrepack(userName,location, order_id, task,packbox_product_id,preNumber);
		//封装打印标签数据
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		resultMap.put("location_barcode", location.getLocation_barcode());
		resultMap.put("barcode", preCheckMap.get("barcode"));
		resultMap.put("product_name", preCheckMap.get("product_name"));
		resultMap.put("total_number", preNumber);
		resultMap.put("create_time", sdf.format(new Date()));
		resultMap.put("username", userName);

		}catch (Exception e) {
			e.printStackTrace();
			logger.error("复核失败", e);
			throw new RuntimeException("复核失败,原因" + e.getMessage());
		}
		resultMap.put("result", "success");
		return resultMap;
	}
	
	/**
	 * 拆解上架
	 */
	public Map<String, Object> UngroundingPrint(Integer order_id,String userName,Integer preNumber,Integer prePackageNumber){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> printList = new ArrayList<Map<String,Object>>();
		Integer taskQuantity=0;
		try {
		//基本信息校验
		if(WorkerUtil.isNullOrEmpty(order_id)){
			resultMap.put("note", "订单号不存在");
			logger.info("预打包复核失败");
			return resultMap;
		}
		Integer changeQuantity = productLocationDetailDao.selectUnPackageProductLocationDetailByOrderId(order_id);
		if(WorkerUtil.isNullOrEmpty(changeQuantity)){
			changeQuantity = 0;
		}
		
		
		Map<String, Object> preCheckMap = orderPrepackDao.selectOrderPrepackByOrderId(order_id);
		if(OrderInfo.ORDER_STATUS_FULFILLED.equals(preCheckMap.get("status").toString()) ||
				OrderInfo.ORDER_STATUS_CANCEL.equals(preCheckMap.get("status").toString())){
			resultMap.put("note", "该订单已取消或者已完结");
			logger.info("预打包复核失败");
			return resultMap;
		}
		List<Task> unPackageList = orderPrepackDao.selectUnPackageList(order_id,Integer.parseInt(preCheckMap.get("prepackage_product_id").toString()));
		for (Task task : unPackageList) {
			taskQuantity = taskQuantity+task.getQuantity();
		}
		if(preNumber+(-1)*changeQuantity>taskQuantity){
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note","该订单商品拆解数量已超过该订单的商品数量");
			logger.info("预打包复核失败");
			return resultMap;
		}
		if(WorkerUtil.isNullOrEmpty(unPackageList)){
			resultMap.put("note", "预拆解商品不存在");
			logger.info("预打包复核失败");
			return resultMap;
		}
		//开始扣减拆解单套餐商品库存
		String validity = null;
		Integer task_id = null;
		int num = preNumber; // 本商品需拆解总数量
		logger.info("preNumber:"+preNumber+"order_id"+order_id);
		int inNum = 0; // 每次需入库数量
		Integer chaijietotalnumber =0;
		chaijietotalnumber = orderPrepackDao.selectSumTotal(order_id, Integer.parseInt(preCheckMap.get("prepackage_product_id").toString()));
		if(WorkerUtil.isNullOrEmpty(chaijietotalnumber)){
			chaijietotalnumber=0;
		}
		for (Task task : unPackageList) {
		logger.info("开始扣减拆解单套餐商品库存");
		ProductLocation productLocation1 = productLocationDao.getFromProduLocations(task.getTask_id(),task.getFrom_pl_id());
		validity = productLocation1.getValidity();
		Integer qty_total = task.getQuantity()+chaijietotalnumber;
		if(qty_total >0){
			chaijietotalnumber= 0;
		}
		if(qty_total <=0){
			chaijietotalnumber=qty_total;
			continue;
		}
		else if(num<=0){
			break;
		}
		else if(num<=qty_total){
			inNum = -1*num;
			num = 0;
		}else{
			inNum = -1*qty_total;
			num = num-qty_total;
		}
		task_id = task.getTask_id();
		//扣减套餐库位库存
		productLocationDao.updateProductLocationTotal(inNum, productLocation1.getPl_id());
		
		ProductLocationDetail productLocationDetail = new ProductLocationDetail();
		productLocationDetail.setPl_id(productLocation1.getPl_id());
		productLocationDetail.setChange_quantity(inNum);
		productLocationDetail.setTask_id(task_id);
		productLocationDetail.setDescription("拆解单扣减套餐商品库存");
		productLocationDetail.setCreated_user(userName);
		productLocationDetail.setLast_updated_user(userName);
		productLocationDetail.setOrder_id(order_id);
		//productLocationDetail.setOrder_goods_id(Integer.parseInt(map.get("orderGoodsId").toString()));
		productLocationDetailDao.insert(productLocationDetail);
		}
		logger.info("num"+num+"order_id"+order_id);
		//判断C商品是否全部被扣减
		if (num!=0) {
			throw new RuntimeException("该商品没有全部扣减");
		}
		//更新order_prepack表中的实际打包数量
		orderPrepackDao.updateQtyActual(order_id, preNumber);
				
		List<Map<String, Object>> prePackageList = orderPrepackDao.selectPrePackageList(order_id);
		for (Map<String, Object> map : prePackageList) {
			
			
			// 生成打印标签
			String locationBarcode = WorkerUtil.generatorSequence(SequenceUtil.KEY_NAME_TAGCODE,"C",true);
			
			//插入location表
			logger.info("插入location表");
			Location location = new Location();
			location.setPhysical_warehouse_id(Integer.parseInt(preCheckMap.get("physical_warehouse_id").toString()));
			location.setLocation_barcode(locationBarcode);
			location.setLocation_type("PREPACK_SEQ");
			location.setIs_delete("N");
			location.setCreated_user(userName);
			location.setCustomer_id(Integer.parseInt(preCheckMap.get("customer_id").toString()));
			inventoryDao.insertLocation(location);
			logger.info(">>>Generate locationBarcode："+locationBarcode);
			
			//拆解单商品上架
			logger.info("拆解单商品上架");
			ProductLocation productLocation = new ProductLocation();
			productLocation.setProduct_id(Integer.parseInt(map.get("component_product_id").toString()));
			productLocation.setLocation_id(location.getLocation_id());
			productLocation.setQty_total(preNumber*Integer.parseInt(map.get("number").toString()));
			productLocation.setQty_available(0);
			productLocation.setProduct_location_status("NORMAL");
			//productLocation.setQty_exception(0);
			productLocation.setStatus("NORMAL");
			if (!WorkerUtil.isNullOrEmpty(validity)) {
				productLocation.setValidity(validity.toString());
			}
			//productLocation.setBatch_sn(batchSn);
			productLocation.setSerial_number("");
			productLocation.setCreated_user(userName);
			productLocation.setLast_updated_user(userName);
			productLocation.setWarehouse_id(Integer.parseInt(preCheckMap.get("warehouse_id").toString()));
			productLocationDao.insert(productLocation);
			//创建上架任务
			logger.info("创建上架任务");
			Task task = new Task();
			
			task.setPhysical_warehouse_id(Integer.parseInt(preCheckMap.get("physical_warehouse_id").toString()));
			task.setCustomer_id(Integer.parseInt(preCheckMap.get("customer_id").toString()));
			task.setTask_type(Task.TASK_TYPE_PREPACK_PUT_AWAY);
			task.setTask_status(Task.TASK_STATUS_INIT);
			task.setTask_level(1);
			task.setOrder_id(order_id);
			task.setFrom_pl_id(productLocation.getPl_id());
			task.setProduct_id(Integer.parseInt(map.get("component_product_id").toString()));
			task.setQuantity(preNumber*Integer.parseInt(map.get("number").toString()));
			task.setOperate_platform("WEB");
			task.setCreated_user(userName);
			task.setLast_updated_user(userName);
			taskDao.insertTask(task);
			//创建UserActionTask表
			logger.info("创建UserActionTask表");
			this.insertUserActionTask(userName,task,"WEB拆解订单生成上架任务");
	
			ProductLocationDetail productLocationDetail = new ProductLocationDetail();
			productLocationDetail.setPl_id(productLocation.getPl_id());
			productLocationDetail.setChange_quantity(preNumber*Integer.parseInt(map.get("number").toString()));
			productLocationDetail.setTask_id(task.getTask_id());
			productLocationDetail.setDescription("拆解单上架");
			productLocationDetail.setCreated_user(userName);
			productLocationDetail.setLast_updated_user(userName);
			productLocationDetail.setOrder_id(order_id);
			//productLocationDetail.setOrder_goods_id(Integer.parseInt(map.get("orderGoodsId").toString()));
			productLocationDetailDao.insert(productLocationDetail);
			if("Y".equals(location.getIs_empty())){
				locationDao.updateLocationIsEmpty1(location.getLocation_id());
			}
			//创建拆解单上架标签表
			logger.info("创建拆解单上架标签表");
			this.insertLabelPrepack(userName,location, order_id, task,0,0);
			
			//封装打印标签数据
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object> printMap = new HashMap<String, Object>();
			printMap.put("location_barcode", location.getLocation_barcode());
			printMap.put("barcode", map.get("barcode"));
			printMap.put("product_name", map.get("product_name"));
			printMap.put("total_number", preNumber*Integer.parseInt(map.get("number").toString()));
			printMap.put("create_time", sdf.format(new Date()));
			printMap.put("username", userName);
			printList.add(printMap);
			
		}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("上架失败", e);
			resultMap.put("result", Response.FAILURE);
			resultMap.put("note", e.getMessage());
			throw new RuntimeException("上架失败,原因" + e.getMessage());
		}
		resultMap.put("result", "success");
		resultMap.put("printList", printList);
		
		
		return resultMap;
		
	}
	@Override
	public OrderPrepack selectOrderPrepackByOmsTaskSn(String oms_task_sn) {
		return orderPrepackDao.selectOrderPrepackByOmsTaskSn(oms_task_sn);
	}
	@Override
	public void insertOrUpdate(OrderPrepack orderPrepack) {
		orderPrepackDao.insertOrUpdate(orderPrepack);
	}
	@Override
	public List<OrderPrepack> selectMeetConditionOrderPrepack(Date payTime,
			List<String> groupCodeList , Integer physicalWarehouseId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("payTime", payTime);
		paramMap.put("groupCodeList", groupCodeList);
		paramMap.put("physicalWarehouseId", physicalWarehouseId);
		List<OrderPrepack> orderPrePackList = orderPrepackDao.selectMeetConditionOrderPrepack(paramMap); 
		
		if(!WorkerUtil.isNullOrEmpty(orderPrePackList)){
			for(OrderPrepack orderPrepack :orderPrePackList){
				List<ProductPrepackage> productPrepackageList = orderPrepackDao.selectProductPrepackageByPrepackageProductId(orderPrepack.getPrepackage_product_id());
				orderPrepack.setProductPrepackageList(productPrepackageList);
			}
		}
		return orderPrePackList;
		
	}
	
	@Override
	public void initOrderPrepackProductPrepackageList(OrderPrepack orderPrepack){
		List<ProductPrepackage> productPrepackageList = orderPrepackDao.selectProductPrepackageByPrepackageProductId(orderPrepack.getPrepackage_product_id());
		orderPrepack.setProductPrepackageList(productPrepackageList);
	}

	public void updateStatus(OrderPrepack orderPrepack){
		orderPrepackDao.updateStatus(orderPrepack.getOms_task_sn(),orderPrepack.getStatus());
	}
	
	@Override
	public void save(OrderPrepack orderPrepack, Product product, List<ProductPrepackage> prepackages) {
		Integer productId;
		//1.保存product
		Product p = productDao.selectBySkuCode(product.getSku_code());
		if(WorkerUtil.isNullOrEmpty(p)){
			productDao.insert(product);
			productId=product.getProduct_id();
			//2.保存预包裹和商品关系表
			for (ProductPrepackage productPrepackage : prepackages) {
				productPrepackage.setPrepackage_product_id(productId);
				productPrepackageDao.insert(productPrepackage);
			}
		}else
			productId = p.getProduct_id();
		
		
		//3.保存任务
		orderPrepack.setPrepackage_product_id(productId);
		orderPrepackDao.insertOrUpdate(orderPrepack);
		
	}
	
	
	private void insertLabelPrepack(String userName,Location location,Integer order_id,Task task,Integer packbox_product_id,Integer preNumber){
		LabelPrepack labelPrepack = new LabelPrepack();
		labelPrepack.setTask_id(task.getTask_id());
		labelPrepack.setStatus("INIT");
		labelPrepack.setLocation_id(location.getLocation_id());
		labelPrepack.setLocation_barcode(location.getLocation_barcode());
		labelPrepack.setOrder_id(order_id);
		labelPrepack.setPackbox_need_out(preNumber);
		labelPrepack.setPackbox_num(preNumber);
		labelPrepack.setPackbox_product_id(packbox_product_id);
		labelPrepack.setCreated_user(userName);
		labelPrepack.setCreated_time(new Date());
		labelPrepack.setLast_updated_user(userName);
		labelPrepack.setLast_updated_time(new Date());
		labelPrepackDao.insert(labelPrepack);
	}
	@Override
	public void updateQtyUsed(Integer orderId, Integer qtyNeedThisTime) {
		if(orderPrepackDao.updateQtyUsed(orderId,qtyNeedThisTime) <= 0) {
			throw new RuntimeException("更新任务的使用数量失败：orderPrepackDao.updateQtyUsed(orderId,qtyNeedThisTime)影响行数为0");
		}
		
	}
	private void insertUserActionTask(String userName,Task task,String note){
		UserActionTask userActionTask = new UserActionTask();
		userActionTask.setTask_id(task.getTask_id());
		userActionTask.setAction_note(note);
		userActionTask.setAction_type(Task.TASK_TYPE_PREPACK_PUT_AWAY);
		userActionTask.setTask_status(Task.TASK_TYPE_PREPACK_PUT_AWAY);
		userActionTask.setCreated_user(userName);
		userActionTask.setCreated_time(new Date());
		userActionTaskDao.insert(userActionTask);
	}
	
	@Override
	public Map<String, Object> getPrepackByBarcode(String barcode,Integer physcial_warehouse_id){
			Map<String, Object>resultMap = new HashMap<String, Object>();
			Map<String, Object> prepackMap = orderPrepackDao.getPrepackByBarcode(barcode,physcial_warehouse_id);
			if(WorkerUtil.isNullOrEmpty(prepackMap)){
				resultMap.put("success", false);
				resultMap.put("note", "该商品条码不是套餐商品");
			}else {
				resultMap.put("success", true);
				resultMap.put("note", "查询成功");
				resultMap.put("prepackMap", prepackMap);
			}
			return resultMap;
	}
	
	@Override
	public void releaseQtyUsed(Integer order_id){
	    List<Map<String, Object>> salePrepackList = orderPrepackDao.getPrepackByOrderId(order_id);
	    if(WorkerUtil.isNullOrEmpty(salePrepackList)){
			logger.info( "该销售订单中没有套餐商品");
	    }else {
			for (Map<String, Object> map : salePrepackList) {
				Integer goods_number = Integer.parseInt(map.get("goods_number").toString());
				Integer inNum = 0;
				List<OrderPrepack> orderPrepacksList =orderPrepackDao.selectOrderPrepackByPrepackageProductId(Integer.parseInt(map.get("product_id").toString()));
				for (OrderPrepack orderPrepack : orderPrepacksList) {
					Integer qty_used = orderPrepack.getQty_used();
					if(qty_used <=0){
						continue;
					}
					else if(goods_number<=0){
						break;
					}
					else if(goods_number<=qty_used){
						inNum = -1*goods_number;
						goods_number = 0;
					}else{
						inNum = -1*qty_used;
						goods_number = goods_number-qty_used;
					}
					int col =orderPrepackDao.updateQtyUsed(orderPrepack.getOrder_id(), inNum);
					if(col<=0){
						throw new RuntimeException("order_id"+order_id+"扣减qty_used失败");
					}
				}
				if(goods_number!=0){
					throw new RuntimeException("order_id"+order_id+"扣减qty_used失败");
				}
			}
		}
	   
	}
		
	
}