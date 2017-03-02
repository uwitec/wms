package com.leqee.wms.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leqee.wms.biz.InventoryBiz;
import com.leqee.wms.biz.OrderBiz;
import com.leqee.wms.dao.OrderDao;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.page.PageParameter;
import com.leqee.wms.util.SequenceUtil;
import com.leqee.wms.util.WorkerUtil;


/**
 * 采购相关的逻辑代码
 * @author hzhang1
 * @date 2016-3-4
 * @version 1.0.0
 */
@Controller
@RequestMapping(value="/purchase")  //指定根路径
public class PurchaseController {
	
	private Logger logger = Logger.getLogger(PurchaseController.class);
	
	@Autowired
	InventoryBiz inventoryBiz;
	
	@Autowired
	OrderBiz orderBiz;
	@Autowired
	OrderDao orderDao;
	
	/**
	 * 展示采购订单的列表
	 * 涉及数据库操作：查询
	 * @author hzhang1
	 * @date 2016-3-4
	 **/
	@RequestMapping(value="/accept") 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String showPurchaseOrder( HttpServletRequest req , Map<String,Object> model ){
		
		// 1.获得session
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        // 2.获取当前物理仓
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
        // 3.获取货主
        List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        
        // 4.获取逻辑仓
        List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
        List<Integer> warehouseIdList = new ArrayList<Integer>();
        
        
		// 5.获得REQUEST参数
		String order_sn = req.getParameter("order_sn");
		String warehouse_id = req.getParameter("warehouse_id");
		String start = req.getParameter("start");
		String end = req.getParameter("end");
		String arrive_time_start = req.getParameter("arrive_time_start");
		String arrive_time_end = req.getParameter("arrive_time_end");
		String order_status = req.getParameter("order_status");
		String provider_name = req.getParameter("provider_name");
		String batch_order_sn = req.getParameter("batch_order_sn");
		String customer_id = req.getParameter("customer_id");
		
		logger.info(">>>showPurchaseOrder select condition: order_sn:" + order_sn + " warehouse_id:" + warehouse_id
				+ " start:" + start + " end:" + end + " arrive_time_start:"
				+ arrive_time_start + " arrive_time_end:"+arrive_time_end+" order_status:" + order_status
				+ " provider_name:" + provider_name + " batch_order_sn:"
				+ batch_order_sn+" customer_id:"+customer_id+",physical_warehouse_id:"+currentPhysicalWarehouse.getPhysical_warehouse_id());
		
		// 6.将参数放入MAP
		HashMap<String,Object> searchMap = new HashMap<String,Object>();
		searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		if(!WorkerUtil.isNullOrEmpty(customer_id)){
			searchMap.put("customer_id", customer_id);
			model.put("customer_id", customer_id );
		}else{
			searchMap.put("customers", customers);
		}
		if(!WorkerUtil.isNullOrEmpty(order_sn)){
			searchMap.put("order_sn", order_sn);
			model.put("order_sn", order_sn );
		}
		if(!WorkerUtil.isNullOrEmpty(warehouse_id)){
			warehouseIdList.add(Integer.valueOf(warehouse_id));
			//searchMap.put("warehouse_id", warehouse_id);
			model.put("warehouse_id", warehouse_id );
		}else{
	        for (Warehouse warehouse : warehouseList) {
	        	warehouseIdList.add(warehouse.getWarehouse_id());
			}
		}
		searchMap.put("warehouseIdList", warehouseIdList);
		
		if(!WorkerUtil.isNullOrEmpty(start)){
			searchMap.put("start", start);
			model.put("start", start );
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String startDate = sdf.format(new Date(System.currentTimeMillis()));
			searchMap.put("start", startDate);
			model.put("start", startDate );
		}
		if(!WorkerUtil.isNullOrEmpty(end)){
			searchMap.put("end", end);
			model.put("end", end );
		}
		if(!WorkerUtil.isNullOrEmpty(arrive_time_start)){
			searchMap.put("arrive_time_start", arrive_time_start);
			model.put("arrive_time_start", arrive_time_start );
		}
		if(!WorkerUtil.isNullOrEmpty(arrive_time_end)){
			searchMap.put("arrive_time_end", arrive_time_end);
			model.put("arrive_time_end", arrive_time_end );
		}
		if(!WorkerUtil.isNullOrEmpty(order_status)){
			searchMap.put("order_status", order_status);
			model.put("order_status", order_status );
		}
		if(!WorkerUtil.isNullOrEmpty(provider_name)){
			searchMap.put("provider_name", provider_name);
			model.put("provider_name", provider_name );
		}
		if(!WorkerUtil.isNullOrEmpty(batch_order_sn)){
			searchMap.put("batch_order_sn", batch_order_sn);
			model.put("batch_order_sn", batch_order_sn );
		}
		
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,200);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		
		// 7.筛选得到需要入库的采购订单
		List<Map> purchaseOrderList = inventoryBiz.selectPurchaseOrderList(searchMap);
		logger.info(">>>Get the purchaseOrderList size:"+purchaseOrderList.size());

		
		// 8.将purchaseOrderList传到前端VIEW
		model.put("purchaseOrderList", purchaseOrderList ); 
		model.put("warehouseList", warehouseList );
		model.put("customers", customers);
		model.put("page", page);
		return "purchase/accept";  
	}
	
	@RequestMapping(value="/accept_ajax") 
	@ResponseBody
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object showPurchaseOrder_ajax( HttpServletRequest req ){
		
		
		Map<String,Object> model = new HashMap<String,Object>();
		// 1.获得session
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        // 2.获取当前物理仓
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
        // 3.获取货主
        List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        
        // 4.获取逻辑仓
        List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
        List<Integer> warehouseIdList = new ArrayList<Integer>();
        
        
		// 5.获得REQUEST参数
		String order_sn = req.getParameter("order_sn");
		String warehouse_id = req.getParameter("warehouse_id");
		String start = req.getParameter("start");
		String end = req.getParameter("end");
		String arrive_time_start = req.getParameter("arrive_time_start");
		String arrive_time_end = req.getParameter("arrive_time_end");
		String order_status = req.getParameter("order_status");
		String provider_name = req.getParameter("provider_name");
		String batch_order_sn = req.getParameter("batch_order_sn");
		String customer_id = req.getParameter("customer_id");
		
		logger.info(">>>showPurchaseOrder select condition: order_sn:" + order_sn + " warehouse_id:" + warehouse_id
				+ " start:" + start + " end:" + end + " arrive_time_start:"
				+ arrive_time_start + " arrive_time_end:"+arrive_time_end+" order_status:" + order_status
				+ " provider_name:" + provider_name + " batch_order_sn:"
				+ batch_order_sn+" customer_id:"+customer_id+",physical_warehouse_id:"+currentPhysicalWarehouse.getPhysical_warehouse_id());
		
		// 6.将参数放入MAP
		HashMap<String,Object> searchMap = new HashMap<String,Object>();
		searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
		if(!WorkerUtil.isNullOrEmpty(customer_id)){
			searchMap.put("customer_id", customer_id);
		}else{
			searchMap.put("customers", customers);
		}
		if(!WorkerUtil.isNullOrEmpty(order_sn)){
			searchMap.put("order_sn", order_sn);
		}
//		if(!WorkerUtil.isNullOrEmpty(warehouse_id)){
//			warehouseIdList.add(Integer.valueOf(warehouse_id));
//		}else{
//	        for (Warehouse warehouse : warehouseList) {
//	        	warehouseIdList.add(warehouse.getWarehouse_id());
//			}
//		}
//		searchMap.put("warehouseIdList", warehouseIdList);
		
		if(!WorkerUtil.isNullOrEmpty(start)){
			searchMap.put("start", start);
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String startDate = sdf.format(new Date(System.currentTimeMillis()));
			searchMap.put("start", startDate);
		}
		if(!WorkerUtil.isNullOrEmpty(end)){
			searchMap.put("end", end);
		}
		if(!WorkerUtil.isNullOrEmpty(arrive_time_start)){
			searchMap.put("arrive_time_start", arrive_time_start);
		}
		if(!WorkerUtil.isNullOrEmpty(arrive_time_end)){
			searchMap.put("arrive_time_end", arrive_time_end);
		}
		if(!WorkerUtil.isNullOrEmpty(order_status)){
			searchMap.put("order_status", order_status);
		}
		if(!WorkerUtil.isNullOrEmpty(provider_name)){
			searchMap.put("provider_name", provider_name);
		}
		if(!WorkerUtil.isNullOrEmpty(batch_order_sn)){
			searchMap.put("batch_order_sn", batch_order_sn);
		}
		if(!WorkerUtil.isNullOrEmpty(warehouse_id)){
			searchMap.put("warehouse_id", warehouse_id );
		}
		
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter();
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		
		// 7.筛选得到需要入库的采购订单
		List<Map> purchaseOrderList = inventoryBiz.selectPurchaseOrderList(searchMap);
		logger.info(">>>Get the purchaseOrderList size:"+purchaseOrderList.size());

		
		// 8.将purchaseOrderList传到前端VIEW
		model.put("purchaseOrderList", purchaseOrderList ); 
		model.put("warehouseList", warehouseList );
		model.put("customers", customers);
		model.put("page", page);
		return model;  
	}

	/**
	 * 点击采购单链接跳转采购详情页
	 * 涉及数据库操作：查询
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="edit")
	@SuppressWarnings("rawtypes")
	public String editPurchaseOrder( HttpServletRequest req , Map<String,Object> model ){
		
		// 1.获得请求的oms_order_sn
		String omsOrderSn = req.getParameter("oms_order_sn");
		
		// 2.根据omsOrderSn获得orderInfo & orderGoods信息
		OrderInfo orderInfo = orderBiz.selectOrderByOmsOrderSn(omsOrderSn);
		
		// 3.根据状态返回相应的数据
		Map<String, Object> resMap = orderBiz.selectOrderGoodsByOmsOrderSn(omsOrderSn,orderInfo.getOrder_status());
		model.put("orderGoodsList", (List<Map>)resMap.get("orderGoodsList") );
		model.put("orderGoodsList2", (List<Map>)resMap.get("orderGoodsList2") );
		
		// 4.返回视图
		model.put("provider_name", orderInfo.getProvider_name()); 
		model.put("order_status", orderInfo.getOrder_status()); 
		model.put("oms_order_sn", omsOrderSn);
		model.put("order_id", orderInfo.getOrder_id());
		model.put("warehouse_id", orderInfo.getWarehouse_id());
		model.put("customer_id", orderInfo.getCustomer_id());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String arriveTime = !WorkerUtil.isNullOrEmpty(orderInfo.getArrival_time())?sdf.format(orderInfo.getArrival_time()):"";
		model.put("arrive_time", arriveTime);
		return "purchase/edit";
	}
	
	@RequestMapping(value="edit_batch_sn")
	@SuppressWarnings("rawtypes")
	public String editPurchaseBatchSn( HttpServletRequest req , Map<String,Object> model ){
		
		// 1.获得请求的oms_order_sn
		String batchOrderSn = req.getParameter("batch_order_sn");
		
		// 2.查询
		List<Map> purchaseOrderInfoList = orderBiz.selectOrdersByBatchOrderSn(batchOrderSn);
		
		// 3.返回视图
		model.put("batch_order_sn", batchOrderSn);
		model.put("purchase_order_info", purchaseOrderInfoList);
		model.put("num", purchaseOrderInfoList.size());
		return "purchase/purchaseBatchOrder";
	}
	
	@RequestMapping(value="reset")
	@SuppressWarnings("unchecked")
	@ResponseBody
	public Object reset( HttpServletRequest req , Map<String,Object> model ){
		
		Map<String,Object> resMap = new HashMap<String,Object>(); 
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        
		Integer orderId = Integer.parseInt(req.getParameter("order_id"));
		String tagList = req.getParameter("tag_list");
		
		resMap = orderBiz.resetPurchaseOrder(orderId, tagList,sysUser.getUsername());
		return resMap;
	}
	
	/**
	 * 开始验货操作，修改订单状态
	 * 涉及数据库操作：查询、更新订单状态
	 * @author hzhang1
	 * @param order_id
	 * @return
	 */
	@RequestMapping(value="start")
	@ResponseBody
	public Object startPurchaseOrder(@RequestParam("order_id") Integer order_id){
		
		// 1.返回结果JSON格式的MAP
		HashMap<String,Object> resMap = new HashMap<String,Object>(); 
		
		// 2.从SESSION获取当前用户
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		
		// 3.判断影响行数确定更新成功/失败
		try {
			OrderInfo orderInfo = orderBiz.getOrderInfoByOrderId(order_id);
			if(OrderInfo.ORDER_STATUS_CANCEL.equals(orderInfo.getOrder_status())){
				resMap.put("result", "failure");
				resMap.put("note", "此订单已经取消，不能开始收获！");
				return resMap;
			}
			if(OrderInfo.ORDER_STATUS_FULFILLED.equals(orderInfo.getOrder_status())){
				resMap.put("result", "failure");
				resMap.put("note", "此订单已经上架，不能开始收获！");
				return resMap;
			}
			orderBiz.updateOrderInfoStatusV2(order_id, "IN_PROCESS", sysUser.getUsername());
			resMap.put("result", "success");
			resMap.put("note", "更新订单状态成功");
		} catch (Exception e) {
			resMap.put("result", "failure");
			resMap.put("note", "更新订单状态失败，"+e.getMessage());
			logger.error("更新订单状态失败，"+e.getMessage(), e);
		}
		
		// 4.返回resMap
		return resMap;
	}

	
	@RequestMapping(value="start_batch")
	@ResponseBody
	public Object startPurchaseBatchOrder(HttpServletRequest req){
		
		// 1.返回结果JSON格式的MAP
		HashMap<String,Object> resMap = new HashMap<String,Object>(); 
		
		// 2.从SESSION获取当前用户
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		
        String orderList = req.getParameter("order_list");
        String orderArr[] = orderList.split(",");
		
        // 3.判断影响行数确定更新成功/失败
		try {
			for(String str:orderArr){
				OrderInfo orderInfo = orderBiz.getOrderInfoByOrderId(Integer.parseInt(str));
				if(OrderInfo.ORDER_STATUS_CANCEL.equals(orderInfo.getOrder_status())){
					resMap.put("result", "failure");
					resMap.put("note", "此订单已经取消，不能开始收获！");
					return resMap;
				}
				if(OrderInfo.ORDER_STATUS_FULFILLED.equals(orderInfo.getOrder_status())){
					resMap.put("result", "failure");
					resMap.put("note", "此订单已经上架，不能开始收获！");
					return resMap;
				}
				if(OrderInfo.ORDER_STATUS_ABORTED.equals(orderInfo.getOrder_status())){
					resMap.put("result", "failure");
					resMap.put("note", "此订单已中断不能重复上架");
					return resMap;
				}
				orderBiz.updateOrderInfoStatusV2(Integer.parseInt(str), "IN_PROCESS", sysUser.getUsername());
			}
			resMap.put("result", "success");
			resMap.put("note", "更新订单状态成功");
		} catch (Exception e) {
			resMap.put("result", "failure");
			resMap.put("note", "更新订单状态失败，"+e.getMessage());
			logger.error("更新订单状态失败，"+e.getMessage(), e);
		}
		
		// 4.返回resMap
		return resMap;
	}

	/**
	 * 打印验收单，采购订单需要打印发货单才能进行下面的操作
	 * 涉及数据库操作：查询
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="print_receipt")
	@SuppressWarnings("rawtypes")
	public String print_receipt(HttpServletRequest req, Map<String,Object> model) throws ParseException{
		
		// 1.获得请求的oms_order_sn
		String omsOrderSnList = req.getParameter("oms_order_sn");
		String omsOrderSnArr [] = omsOrderSnList.split(",");
		
		// 2.从SESSION获取当前用户
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		List<Map> mapList = new ArrayList<Map>();
        for(String omsOrderSn:omsOrderSnArr){
        	Map<String,Object> map = new HashMap<String,Object>();
			// 3.根据omsOrderSn获得orderInfo & orderGoods信息
			OrderInfo orderInfo = orderBiz.selectOrderByOmsOrderSn(omsOrderSn);
			
			// 4.根据状态返回相应的数据
			Map<String, Object> resMap = orderBiz.selectOrderGoodsByOmsOrderSn(omsOrderSn,orderInfo.getOrder_status());
			
			// 5.返回视图
			map.put("orderGoodsList", (List<Map>)resMap.get("orderGoodsList") );
			map.put("print_time", new Date());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String arriveTime = sdf.format(orderInfo.getArrival_time());
			map.put("arrive_time", arriveTime);
			map.put("action_user", sysUser.getRealname());
			map.put("provider_name", orderInfo.getProvider_name()); 
			map.put("order_status", orderInfo.getOrder_status()); 
			map.put("oms_order_sn", omsOrderSn);
			map.put("order_id", orderInfo.getOrder_id());
			map.put("batch_order_sn", orderInfo.getBatch_order_sn());
			mapList.add(map);
        }
        model.put("mapList", mapList);
        model.put("actionUser", sysUser.getRealname());
		return "purchase/print_receipt";
	}
	
	
	/**
	 * 打印入库单，为入库的最后一步操作
	 * 涉及数据库操作：查询
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="print_inventory")
	@SuppressWarnings("rawtypes")
	public String print_inventory(HttpServletRequest req, Map<String,Object> model){
		
		// 1.获得请求的oms_order_sn
		String omsOrderSn = req.getParameter("oms_order_sn");
		
		// 2.从SESSION获取当前用户，并且更新订单状态
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		        
		// 3.根据omsOrderSn获得orderInfo & orderGoods信息
		OrderInfo orderInfo = orderBiz.selectOrderByOmsOrderSn(omsOrderSn);
		
		// 4.根据状态返回相应的数据
		Map<String, Object> resMap = orderBiz.selectOrderGoodsByOmsOrderSn(omsOrderSn,orderInfo.getOrder_status());
		
		// 5.返回视图
		model.put("orderGoodsList", (List<Map>)resMap.get("orderGoodsList2") );
		model.put("print_time", new Date());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String arriveTime = sdf.format(orderInfo.getArrival_time());
		model.put("arrive_time", arriveTime);
		model.put("action_user", sysUser.getUsername());
		model.put("provider_name", orderInfo.getProvider_name()); 
		model.put("order_status", orderInfo.getOrder_status()); 
		model.put("oms_order_sn", omsOrderSn);
		model.put("order_id", orderInfo.getOrder_id());
		return "purchase/print_inventory";
	}
	
	
	/**
	 * 生成托盘标签条码
	 * 涉及数据库操作：向数据表location插入标签记录
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="generate_tag")
	@ResponseBody
	public Object generate_tag( HttpServletRequest req , Map<String,Object> model){
		
		HashMap<String,Object> resMap = new HashMap<String,Object>(); 
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        //WarehouseCustomer customer = (WarehouseCustomer) session.getAttribute("currentCustomer");
        
		// 1.得到参数
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		Integer physicalWarehouseId = currentPhysicalWarehouse.getPhysical_warehouse_id();
		Integer customerId = Integer.parseInt(req.getParameter("customer_id"));
		String num = req.getParameter("num");
		
		List<String> locationBarcodeList = new ArrayList<String>();
		
		if(!WorkerUtil.isNullOrEmpty(num)){
			for(int i=0;i<Integer.parseInt(num);i++){
				// 2.生成序列号
				String locationBarcode = WorkerUtil.generatorSequence(SequenceUtil.KEY_NAME_TAGCODE,"C",true);
				
				// 3.插入location表
				inventoryBiz.generateLocation(locationBarcode,physicalWarehouseId,sysUser.getUsername(),customerId);
				locationBarcodeList.add(locationBarcode);
				logger.info(">>>Generate locationBarcode："+locationBarcode);
			}
		}else{
			// 2.生成序列号
			String locationBarcode = WorkerUtil.generatorSequence(SequenceUtil.KEY_NAME_TAGCODE,"C",true);
			
			// 3.插入location表
			inventoryBiz.generateLocation(locationBarcode,physicalWarehouseId,sysUser.getUsername(),customerId);
			locationBarcodeList.add(locationBarcode);
			logger.info(">>>Generate locationBarcode："+locationBarcode);
		}
		
		resMap.put("locationBarcode", locationBarcodeList);
		return resMap;
	}
	
	
	/**
	 * 打印标签，跳出打印页面
	 * 涉及数据库操作：无
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="print_tag2")
	@SuppressWarnings("rawtypes")
	public String print_tag2( HttpServletRequest req , Map<String,Object> model){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		
		String reqStr = req.getParameter("tagList");
		String list = "";
		try {
			list = URLDecoder.decode(reqStr, "UTF-8");//new String(reqStr.getBytes("iso8859-1"),"UTF-8");
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(list);
            list = m.replaceAll("");
		} catch (UnsupportedEncodingException e) {
			logger.error("打印标签失败", e);
		}
		
		JSONArray  tagList =  JSONArray.fromObject(list.substring(1, list.length()-1));
		List<Map> tagPrintList = new ArrayList<Map>();
		
		int index = 0;
		while(index < tagList.size()){
			JSONObject tagJson = tagList.getJSONObject(index);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("location_barcode", tagJson.getString("location_barcode"));
			map.put("normal_number", tagJson.getString("normal_number"));
			map.put("arrive_time", tagJson.getString("arrive_time"));
			map.put("batch_sn", tagJson.containsKey("batch_sn")?tagJson.getString("batch_sn"):"");
			map.put("defective_number", tagJson.getString("defective_number"));
			map.put("validity", tagJson.getString("validity"));
			map.put("order_goods_id", tagJson.getString("order_goods_id"));
			map.put("physical_warehouse_id", tagJson.getString("physical_warehouse_id"));
			map.put("oms_order_sn", tagJson.getString("oms_order_sn"));
			map.put("goods_name", tagJson.getString("goods_name"));
			map.put("barcode", tagJson.getString("barcode"));
			map.put("action_user", sysUser.getRealname());
			tagPrintList.add(map);
			index++;
		}
		model.put("tagList", tagPrintList);
		return "purchase/print_tag";
	}
	
	
	/**
	 * 打印标签,打印标签会把信息保存到数据表
	 * 涉及数据库操作：报存标签内商品信息于数据表inventory_location/inventory_location_detail
	 * @param tagList
	 * @return
	 */
	@RequestMapping(value="print_tag")
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object print_tag(@RequestBody String[] tagList){
		
		HashMap<String,Object> resMap = new HashMap<String,Object>(); 
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        // 1.获取当前用户及当前货主
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		List<Map> tagPrintList = new ArrayList<Map>();

		// 2.调用biz层插入数据
		Map<String, Object> returnMap = null;
		try {
			returnMap = inventoryBiz.printTag(tagList,sysUser.getUsername());
		} catch (Exception e) {
			logger.error("print_tag error:", e);
			resMap.put("result", "failure" );
			resMap.put("note", "打印失败，"+e.getMessage());
			return resMap;
		}
		
		// 3.返回前端
		if("success".equals(returnMap.get("result").toString())){
			tagPrintList = (List<Map>) returnMap.get("tagPrintList");
			resMap.put("tagList", tagPrintList);
			resMap.put("result", "success" );
			resMap.put("note", "打印成功");
		}else if("failure".equals(returnMap.get("result").toString())){
			return returnMap;
		}
		
		return resMap;
	}
	
	
	/**
	 * 验收完成方法
	 * 涉及数据库操作：修改订单状态
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="finish")
	@ResponseBody
	@SuppressWarnings("rawtypes")
	public Object finishPurchaseOrder(HttpServletRequest req ,Map<String,Object> model){
		
		Integer orderId = Integer.parseInt(req.getParameter("order_id"));//purchaseAcceptVO.getOrder_id();
		String tagList = req.getParameter("tag_list");
		Map<String,Object> resMap = new HashMap<String,Object>(); 
		// 1.从session获取信息
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");		
		
		// 2.判断验货成功的结果是否为成功
		try {
			OrderInfo orderInfo = orderBiz.getOrderInfoByOrderId(orderId);
			if(OrderInfo.ORDER_STATUS_CANCEL.equals(orderInfo.getOrder_status())){
				resMap.put("result", "failure");
				resMap.put("note", "此订单已经取消，不能验收完成！");
				return resMap;
			}
			orderBiz.insertGroupTask(orderId, "ON_SHELF",currentPhysicalWarehouse.getPhysical_warehouse_id(), orderInfo.getCustomer_id(), tagList, sysUser.getUsername());
			logger.info("finishPurchaseOrder success...");
			resMap.put("success", "success");
			resMap.put("note", "验货成功");
		} catch (Exception e) {
			resMap.put("result", "failure");
			resMap.put("note", "验货完成插入数据出错"+e.getMessage());
			logger.error("finishPurchaseOrder error:",e);
		}
		
		// 4.根据状态返回相应的数据
		OrderInfo orderInfo = orderBiz.getOrderInfoByOrderId(orderId);
		Map<String, Object> returnMap = orderBiz.selectOrderGoodsByOmsOrderSn(orderInfo.getOms_order_sn(),orderInfo.getOrder_status());
		resMap.put("orderGoodsList", (List<Map>)returnMap.get("orderGoodsList") );
		resMap.put("orderGoodsList2", (List<Map>)returnMap.get("orderGoodsList2") );
		
		// 5.返回视图
		resMap.put("provider_name", orderInfo.getProvider_name()); 
		resMap.put("order_status", orderInfo.getOrder_status()); 
		resMap.put("oms_order_sn", orderInfo.getOms_order_sn());
		resMap.put("order_id", orderInfo.getOrder_id());
		return resMap;
	}
	
	
	/**
	 * 预览采购单详情页
	 * 涉及数据库操作：查询
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value="preview")
	public Object previewPurchaseOrder( HttpServletRequest req ){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		// 1.获得请求的oms_order_sn
		String omsOrderSn = req.getParameter("oms_order_sn");
		
		// 2.根据omsOrderSn获得orderInfo & orderGoods信息
		OrderInfo orderInfo = orderBiz.selectOrderByOmsOrderSn(omsOrderSn);
		
		// 3.根据状态返回相应的数据
		@SuppressWarnings("rawtypes")
		Map<String, Object> returnMap = orderBiz.selectOrderGoodsByOmsOrderSn(omsOrderSn,orderInfo.getOrder_status());
		resMap.put("orderGoodsList", (List<Map>)returnMap.get("orderGoodsList") );
		
		// 4.返回视图
		resMap.put("provider_name", orderInfo.getProvider_name()); 
		resMap.put("order_status", orderInfo.getOrder_status()); 
		resMap.put("oms_order_sn", omsOrderSn);
		resMap.put("order_id", orderInfo.getOrder_id());
		return resMap;
	}
	
	
	/***************************************************************************************************************************
	 * 										分割线，以下方法为上架部分方法
	 ***************************************************************************************************************************/
	/**
	 * 采购上架入口页面
	 * @param req
	 * @return
	 **/
	@RequestMapping(value="grounding")
	@SuppressWarnings("unchecked")
	public String groundingPurchaseOrder(HttpServletRequest req,Map<String,Object> model ){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
		model.put("customers", customers);
		return "purchase/grouping";
	}
	
	
	/**
	 * 根据托盘标签搜索入库商品相关信息
	 * @param req
	 * @return
	 */
	@RequiresPermissions("accept:grounding:*")
	@RequestMapping(value="search_tag")
	@ResponseBody
	@SuppressWarnings("unused")
	public Object search_tag( HttpServletRequest req ){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		HashMap<String,Object> resMap = new HashMap<String,Object>(); 
		
		// 1.接收前端变量值 
		String location_barcode = req.getParameter("q");
		Integer physical_warehouse_id = currentPhysicalWarehouse.getPhysical_warehouse_id();
		
		// 2.搜索该托盘标签的信息
		@SuppressWarnings("rawtypes")
		List<Map> map = inventoryBiz.selectByTagCode(physical_warehouse_id,location_barcode);
		if(!WorkerUtil.isNullOrEmpty(map))
		{
			resMap.put("success",true);
			resMap.put("map",map);
		}else
		{
			resMap.put("success",false);
			resMap.put("map",map);
		}
		// 3.返回信息
		return resMap;
	}
	
	
	/**
	 * 采购上架，最终步骤
	 * 涉及数据库操作：插入，更新数据
	 * @param req
	 * @return
	 */
	@RequiresPermissions("accept:grounding:*")
	@RequestMapping(value="grounding_submit")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object groundingSubmitPurchaseOrder(HttpServletRequest req){
		
		// 1.从session中获取相关信息
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        //WarehouseCustomer customer = (WarehouseCustomer) session.getAttribute("currentCustomer");
        
        // 2.获取前端传来的值
		Integer orderId = Integer.parseInt(req.getParameter("order_id"));
        Integer orderGoodsId = Integer.parseInt(req.getParameter("order_goods_id"));
        Integer warehouseId = Integer.parseInt(req.getParameter("warehouse_id"));
        Integer physicalWarehouseId = ((Warehouse) session.getAttribute("currentPhysicalWarehouse")).getPhysical_warehouse_id();;
        Integer goodsNumber = Integer.parseInt(req.getParameter("goods_number").trim());
        String tagCode = req.getParameter("tag_code").trim();
        String locationBarcode = req.getParameter("location_barcode").trim();
        Integer customerId = Integer.parseInt(req.getParameter("customer_id").toString());
		String actionUser = sysUser.getUsername();
        
		logger.info("order_id:" + orderId + " order_goods_id:" + orderGoodsId
				+ " warehouse_id:" + warehouseId + " physical_warehouse_id:"
				+ physicalWarehouseId + " goods_number:" + goodsNumber
				+ " tag_code:" + tagCode + " location_barcode:"
				+ locationBarcode + " customer_id:" + customerId
				+ " action_user:" + actionUser);
		
		// 结果返回Map
		Map<String,Object> resultMap = new HashMap<String,Object>(); 
		OrderInfo orderInfo = orderBiz.getOrderInfoByOrderId(Integer.valueOf(orderId));
		if(OrderInfo.ORDER_STATUS_CANCEL.equals(orderInfo.getOrder_status())){
			resultMap.put("success", false);
			resultMap.put("result", "failure");
			resultMap.put("note", "此订单已经取消不能上架");
			return resultMap;
		}else if(OrderInfo.ORDER_STATUS_FULFILLED.equals(orderInfo.getOrder_status())){
			resultMap.put("success", false);
			resultMap.put("result", "failure");
			resultMap.put("note", "此订单已经上架上架成功不能重复上架");
			return resultMap;
		}else if(OrderInfo.ORDER_STATUS_ABORTED.equals(orderInfo.getOrder_status())){
			resultMap.put("success", false);
			resultMap.put("result", "failure");
			resultMap.put("note", "此订单已中断不能重复上架");
			return resultMap;
		}
		
		locationBarcode = locationBarcode.toUpperCase().replace("-","");
		// 3.采购入库接口
		Map<String, Object> returnMap = null;
		try {
			returnMap = inventoryBiz.createPurchaseAccept(
					customerId, orderId, orderGoodsId, warehouseId,
					physicalWarehouseId, tagCode, locationBarcode, actionUser);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("上架失败", e);
			resultMap.put("success", false);
			resultMap.put("result", "failure");
			resultMap.put("note", "上架失败，失败原因："+e.getMessage());
		}

		logger.info(">>>GroundingSubmitPurchaseOrder resultMap:"+returnMap);
		return returnMap;
	}
	
	
	/**
	 * 根据批次号和商品号获取采购订单和采购商品的信息
	 * @author dlyao
	 * @param req
	 * @return
	 */
	@RequiresPermissions("accept:view:*")
	@RequestMapping(value="/search_batch")
	@ResponseBody
	public Object search_batch( HttpServletRequest req ){
		
		logger.info("purchase search_batch");
		HashMap<String,Object> resMap = new HashMap<String,Object>(); 
		
		String batch_sn=req.getParameter("batch_sn");
		String barcode=req.getParameter("barcode");
		// 2.根据omsOrderSn获得orderInfo & orderGoods信息
		int count=orderBiz.getCountOrdersByBatchSnBarCode(batch_sn,barcode);
		if(count==0){
			resMap.put("success", false);
			resMap.put("message", "未找到对应的入库商品，请核实您的输入");
			return resMap;
		}
		if(count>1){
			resMap.put("success", false);
			resMap.put("message", "该批次单有多个该商品的采购单");
			return resMap;
		}
		OrderInfo orderInfo = orderBiz.selectOrderBatchSnBarCode(batch_sn,barcode);
		if(orderInfo.getOrder_status().equalsIgnoreCase(OrderInfo.ORDER_STATUS_CANCEL)){
			resMap.put("success", false);
			resMap.put("message", "该采购单已经取消");
			return resMap;
		}else if(orderInfo.getOrder_status().equalsIgnoreCase(OrderInfo.ORDER_STATUS_FULFILLED)){
			resMap.put("success", false);
			resMap.put("message", "该采购单已经完结");
			return resMap;
		}
		//锁住该采购单  不让erp的完结和取消订单生效
		orderDao.getOderInfoByIdForUpdate(orderInfo.getOrder_id());
		OrderGoods ordergoods = orderBiz.selectOrderGoodsBatchSnBarCode(batch_sn,barcode);
		if(WorkerUtil.isNullOrEmpty(orderInfo)){
			resMap.put("success", false);
			resMap.put("message", "未找到对应的入库商品，请核实您的输入");
			return resMap;
		}
		
//		if(orderInfo.getOrder_status().equals(OrderInfo.ORDER_STATUS_ON_SHIP)){
//			resMap.put("success", false);
//			resMap.put("message", "该采购单已经停止收货，等待上架");
//			return resMap;
//		}
		
		else if(orderInfo.getOrder_status().equals(OrderInfo.ORDER_STATUS_FULFILLED)){
			resMap.put("success", false);
			resMap.put("message", "该采购单已经完结");
			return resMap;
		}
		
		int sum=orderBiz.getSumNumsHasInwareHouse(orderInfo.getOms_order_sn());
		Map<String,Object> map = orderBiz.getIsSerialAndWarranty(batch_sn,barcode);
		
		
		
		inventoryBiz.updateOrderInfoInProcess(orderInfo.getOms_order_sn());
	
		resMap.putAll(map);
		resMap.put("orderInfo", orderInfo);
		resMap.put("ordergoods", ordergoods);
		resMap.put("sum", sum);
		resMap.put("success", true);
		
		// 3.返回信息
		return resMap;
	}
	
	
	/**
	 * 根据批次号和商品号获取采购订单和采购商品的信息
	 * @author dlyao
	 * @param req
	 * @return
	 */
	@RequiresPermissions("accept:view:*")
	@RequestMapping(value="/create_tag")
	@ResponseBody
	public Object create_tag( HttpServletRequest req ){
		
		logger.info("purchase create_tag");
		HashMap<String,Object> resMap = new HashMap<String,Object>();
		String batch_sn=req.getParameter("batch_sn");
		String barcode=req.getParameter("barcode");
		OrderInfo orderInfo = orderBiz.selectOrderBatchSnBarCode(batch_sn,barcode);
		if(orderInfo.getOrder_status().equalsIgnoreCase(OrderInfo.ORDER_STATUS_CANCEL)){
			resMap.put("success", false);
			resMap.put("message", "该采购单已经取消");
			return resMap;
		}else if(orderInfo.getOrder_status().equalsIgnoreCase(OrderInfo.ORDER_STATUS_FULFILLED)){
			resMap.put("success", false);
			resMap.put("message", "该采购单已经完结");
			return resMap;
		}
		//锁住该采购单  不让erp的完结和取消订单生效
		orderDao.getOderInfoByIdForUpdate(orderInfo.getOrder_id());

		resMap=inventoryBiz.create_tag(req);
		// 3.返回信息
		return resMap;
	}
	
	/**
	 * 根据批次号和商品号获取采购订单和采购商品的信息
	 * @author dlyao
	 * @param req
	 * @return
	 */
	@RequiresPermissions("accept:view:*")
	@RequestMapping(value="/send_sure")
	@ResponseBody
	public Object send_sure( HttpServletRequest req ){
		
		logger.info("purchase send_sure");
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");	
		
		HashMap<String,Object> resMap = new HashMap<String,Object>(); 
		
//		String batch_sn=req.getParameter("batch_sn");
//		String barcode=req.getParameter("barcode");
//		OrderInfo orderInfo = orderBiz.selectOrderBatchSnBarCode(batch_sn,barcode);
//		OrderGoods ordergoods = orderBiz.selectOrderGoodsBatchSnBarCode(batch_sn,barcode);
//		if(WorkerUtil.isNullOrEmpty(orderInfo)){
//			resMap.put("success", false);
//			resMap.put("message", "未找到对应的入库商品，请核实您的输入");
//			return resMap;
//		}
//		
//		int count = inventoryBiz.send_sure(currentPhysicalWarehouse.getPhysical_warehouse_id(),sysUser.getUsername(),orderInfo,ordergoods);
//		if(count!=1){
//			resMap.put("success", false);
//			resMap.put("message", "更新失败");
//			return resMap;
//		}
		// 3.返回信息
		resMap.put("success", true);
		return resMap;
	}
	
	
}
