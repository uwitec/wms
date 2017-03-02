package com.leqee.wms.controller;


import com.leqee.wms.biz.InventoryBiz;
import com.leqee.wms.biz.OrderBiz;
import com.leqee.wms.biz.impl.ShipmentBizImpl;
import com.leqee.wms.dao.OrderDao;
import com.leqee.wms.dao.ProductLocationDetailDao;
import com.leqee.wms.dao.ShipmentDao;
import com.leqee.wms.dao.TaskDao;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.Pallet;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.WorkerUtil;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RF 控制器
 * @author hzhang1
 * @date 2016-7-18
 * @version 1.0.0
 */
@Controller
@RequestMapping(value="/RF")
public class RFController {
	
	private Logger logger = Logger.getLogger(RFController.class);
	
	@Autowired
	InventoryBiz inventoryBiz;
	
	@Autowired
	OrderBiz orderBiz;
	
	@Autowired
	OrderDao orderDao;
	
	@Autowired
	TaskDao taskDao;
	
	@Autowired
	ProductLocationDetailDao productLocationDetailDao;
	
	@Autowired
	ShipmentBizImpl shipmentBizImpl;
	
	@Autowired
	ShipmentDao shipmentDao;
	
	// 1.RF根据SKU查询库存
	@RequestMapping(value="/getSkuInventory")
	@ResponseBody
	public Object skuInventory(HttpServletRequest req){
		Map<String,Object> resMap = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		Integer customerId = WorkerUtil.isNullOrEmpty(req.getParameter("customer_id"))?null:Integer.parseInt(req.getParameter("customer_id").toString());
		String barcode = WorkerUtil.isNullOrEmpty(req.getParameter("barcode"))?null:req.getParameter("barcode").toString();
		String flag = WorkerUtil.isNullOrEmpty(req.getParameter("flag"))?null:req.getParameter("flag").toString();
		
		logger.info("RF根据SKU获取查询库存,customerId:"+customerId+",barcode:"+barcode+",flag:"+flag);
		resMap = inventoryBiz.getSkuInventory(currentPhysicalWarehouse.getPhysical_warehouse_id(),customerId, barcode, flag);
		return resMap;
	}

	// 2.RF根据库位条码查询库存
	@RequestMapping(value="/getLocationInventory")
	@ResponseBody
	public Object locationInventory(HttpServletRequest req){
		Map<String,Object> resMap = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		String locationBarcode = req.getParameter("location_barcode").toString();
		String flag = WorkerUtil.isNullOrEmpty(req.getParameter("flag"))?null:req.getParameter("flag").toString();
		logger.info("RF根据LOCATION获取查询库存,locationBarcode:"+locationBarcode+",flag:"+flag);
		
		resMap = inventoryBiz.getLocationInventory(currentPhysicalWarehouse.getPhysical_warehouse_id(),locationBarcode,flag);
		return resMap;
	}
	
	
	// 3.RF根据SKU冻结库存
	@RequestMapping(value="/freezeSkuInventory")
	@ResponseBody
	public Object freezeSkuInventory(HttpServletRequest req){
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
		Integer customerId = WorkerUtil.isNullOrEmpty(req.getParameter("customer_id"))?null:Integer.parseInt(req.getParameter("customer_id").toString());
		String barcode = WorkerUtil.isNullOrEmpty(req.getParameter("barcode"))?null:req.getParameter("barcode").toString();
		Integer plId = WorkerUtil.isNullOrEmpty(req.getParameter("pl_id"))?null:Integer.parseInt(req.getParameter("pl_id").toString());
		Integer quantity = WorkerUtil.isNullOrEmpty(req.getParameter("quantity"))?null:Integer.parseInt(req.getParameter("quantity").toString());
		
		logger.info("RF根据SKU冻结库存,customerId:" + customerId + ",barcode:"
				+ barcode + ",plId:" + plId + ",quantity:" + quantity);
		
		try {
			resMap = inventoryBiz.freezeSkuInventory(currentPhysicalWarehouse.getPhysical_warehouse_id(),customerId,barcode,plId,quantity,sysUser.getUsername());
		} catch (Exception e) {
			logger.error("RF根据SKU冻结库存失败", e);
			resMap.put("result", Response.FAILURE);
			resMap.put("success", Boolean.FALSE);
			resMap.put("note", "根据SKU冻结库存失败");
		}
		return resMap;
	}
	
	// 4.RF根据LOCATION冻结库存
	@RequestMapping(value="/freezeLocationInventory")
	@ResponseBody
	public Object freezeLocationInventory(HttpServletRequest req){
		Map<String,Object> resMap = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        Integer customerId = WorkerUtil.isNullOrEmpty(req.getParameter("customer_id"))?null:Integer.parseInt(req.getParameter("customer_id").toString());
		//String locationBarcode = req.getParameter("location_barcode").toString();
		Integer plId = WorkerUtil.isNullOrEmpty(req.getParameter("pl_id"))?null:Integer.parseInt(req.getParameter("pl_id").toString());
		Integer quantity = WorkerUtil.isNullOrEmpty(req.getParameter("quantity"))?null:Integer.parseInt(req.getParameter("quantity").toString());
		
		logger.info("RF根据LOCATION冻结库存,plId:"+plId+",quantity:"+quantity);
		try {
			resMap = inventoryBiz.freezeLocationInventory(
					currentPhysicalWarehouse.getPhysical_warehouse_id(),
					customerId, null, plId, quantity,
					sysUser.getUsername());
		} catch (Exception e) {
			logger.error("RF根据LOCATION冻结库存失败", e);
			resMap.put("result", Response.FAILURE);
			resMap.put("success", Boolean.FALSE);
			resMap.put("note", "根据LOCATION冻结库存失败");
		}
		return resMap;
	}
	
	// 5.RF解冻SKU库存
	@RequestMapping(value="/releaseFreezeSkuInventory")
	@ResponseBody
	public Object releaseFreezeSkuInventory(HttpServletRequest req){
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
		Integer customerId = WorkerUtil.isNullOrEmpty(req.getParameter("customer_id"))?null:Integer.parseInt(req.getParameter("customer_id").toString());
		Integer plId = WorkerUtil.isNullOrEmpty(req.getParameter("pl_id"))?null:Integer.parseInt(req.getParameter("pl_id").toString());
		Integer quantity = WorkerUtil.isNullOrEmpty(req.getParameter("quantity"))?null:Integer.parseInt(req.getParameter("quantity").toString());
		logger.info("RF解冻SKU冻结库存,customerId:"+customerId+",plId:" + plId + ",quantity:" + quantity);
		try {
			resMap = inventoryBiz.releaseFreezeInventory(
					currentPhysicalWarehouse.getPhysical_warehouse_id(),
					customerId, plId, quantity, sysUser.getUsername());
		} catch (Exception e) {
			logger.error("RF解冻SKU库存失败", e);
			resMap.put("result", Response.FAILURE);
			resMap.put("success", Boolean.FALSE);
			resMap.put("note", "RF解冻SKU库存失败");
		}
		return resMap;
	}
	
	// 6.RF解冻LOCATION库存
	@RequestMapping(value="/releaseFreezeLocationInventory")
	@ResponseBody
	public Object releaseFreezeLocationInventory(HttpServletRequest req){
		Map<String,Object> resMap = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		Integer customerId = WorkerUtil.isNullOrEmpty(req.getParameter("customer_id"))?null:Integer.parseInt(req.getParameter("customer_id").toString());
		Integer plId = WorkerUtil.isNullOrEmpty(req.getParameter("pl_id"))?null:Integer.parseInt(req.getParameter("pl_id").toString());
		Integer quantity = WorkerUtil.isNullOrEmpty(req.getParameter("quantity"))?null:Integer.parseInt(req.getParameter("quantity").toString());
		logger.info("RF解冻LOCATION库存,customerId:"+customerId+",plId:" + plId + ",quantity:" + quantity);
		
		try {
			resMap = inventoryBiz.releaseFreezeInventory(
					currentPhysicalWarehouse.getPhysical_warehouse_id(),
					customerId, plId, quantity, sysUser.getUsername());
		} catch (Exception e) {
			logger.error("RF解冻LOCATION库存失败", e);
			resMap.put("result", Response.FAILURE);
			resMap.put("success", Boolean.FALSE);
			resMap.put("note", "RF解冻LOCATION库存失败");
		}
		return resMap;
	}
	
	
	//7.RF退货上架入库
			/**
			 * 根据商品条码显示商品信息
			 * @param req
			 * @return
			 */
		
			@RequestMapping(value="search_barcode")
			@ResponseBody
			@SuppressWarnings("unused")
			public Object searchBarcode( HttpServletRequest req ){
				
				Subject subject = SecurityUtils.getSubject();
		        Session session = subject.getSession();
		        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
				HashMap<String,Object> resMap = new HashMap<String,Object>();
				HashMap<String,Object> orderMap = new HashMap<String,Object>();
				HashMap<String,Object> searchMap = new HashMap<String,Object>();
				// 1.接收前端变量值
				String orderSn = req.getParameter("orderSn");
				if(!WorkerUtil.isNullOrEmpty(orderSn)){
					orderMap.put("orderSn", orderSn);
				}
				String orderId = req.getParameter("orderId");
				if(!WorkerUtil.isNullOrEmpty(orderId)){
					orderMap.put("orderId", orderId);
				}
				String barcode  = req.getParameter("barcode");
				Integer physical_warehouse_id = currentPhysicalWarehouse.getPhysical_warehouse_id();
				// 2.搜索该商品的信息
				try {
				OrderInfo orderInfo = orderDao.selectByOrderIdOrOrderSn(orderMap);
				if(WorkerUtil.isNullOrEmpty(orderInfo)){
					resMap.put("success",false);
					resMap.put("note","不存在的订单号");
					return resMap;
				}
				if(!orderInfo.ORDER_STATUS_CANCEL.equals(orderInfo.getOrder_status())){
					resMap.put("success",false);
					resMap.put("note","订单不是取消订单");
					return resMap;
				}
				Map map1 = inventoryBiz.selectBybarcode(orderInfo.getOrder_id(),barcode,orderInfo.getCustomer_id(),physical_warehouse_id);
				if(Boolean.TRUE.equals((map1.get("success"))))
				{
					resMap.put("success",true); 
					resMap.put("map1",map1);
				}else{
					resMap.put("success",false);
					resMap.put("map1",map1);
				}
				}catch (Exception e) {
					resMap.put("success",false);
					resMap.put("note","数据异常");
					e.printStackTrace();
				}
				// 3.返回信息
				return resMap;
			}
			
			
			/**
			 * 退货上架入库操作
			 * @param req
			 * @return
			 */
			@RequestMapping(value="grounding_submit")
			@ResponseBody
			@SuppressWarnings("unchecked")
			public Object groundingSubmitCancelOrder(HttpServletRequest req){
				
				// 1.从session中获取相关信息
				Subject subject = SecurityUtils.getSubject();
		        Session session = subject.getSession();
		        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		        
		        // 2.获取前端传来的值
		        String barcode = req.getParameter("barcode");
		        String orderId = req.getParameter("order_id");
		        String status = req.getParameter("status");
		        String validity = req.getParameter("validity");
		        String batchSn = req.getParameter("batchSn");
		        String orderGoodsId = req.getParameter("order_goods_id");
		        String locationBarcode = req.getParameter("locationBarcode").trim();
				String actionUser = sysUser.getUsername();
				Integer quantity = Integer.parseInt(req.getParameter("quantity").trim());
				Map<Object,Object> searchMap = new HashMap<Object,Object>();
				searchMap.put("barcode", barcode);
				searchMap.put("validity",validity);
				searchMap.put("orderId", orderId);
				searchMap.put("status", status);
				searchMap.put("orderGoodsId", orderGoodsId);
				searchMap.put("locationBarcode", locationBarcode);
				searchMap.put("actionUser", actionUser);
				searchMap.put("quantity", quantity);
				searchMap.put("batchSn", batchSn);
				searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
				
				logger.info("order_id:" + orderId + " order_goods_id:" + orderGoodsId
						+ " barcode:" + barcode + " location_barcode:"
						+ locationBarcode  + " action_user:" + actionUser + " status:" + status);
				
				// 结果返回Map
				Map<String,Object> resultMap = new HashMap<String,Object>(); 
				try {
					resultMap = inventoryBiz.createReturnAccept(searchMap);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("上架失败", e);
					resultMap.put("success", false);
					resultMap.put("result", "failure");
					resultMap.put("note", "上架失败，失败原因："+e.getMessage());
				}

				logger.info(">>>GroundingSubmitCancelOrder resultMap:"+resultMap);
				return resultMap;
			}
		
	// 7. RF移库之库存移动 -- 源库位检查
	@RequestMapping(value="/stockMoveSourceLocationCheck")
	@ResponseBody
	public Object stockMoveSourceLocationCheck(HttpServletRequest req){
		Map<String,Object> resMap = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
		String locationBarcode = req.getParameter("from_location_barcode").toString();
		logger.info("RF移库之库存移动 ,fromLocationBarcode:"+locationBarcode);
		resMap = inventoryBiz.checkStockMoveSourceLocation(locationBarcode,currentPhysicalWarehouse.getPhysical_warehouse_id());
		return resMap;
	}
	
	// 8. RF移库之库存移动 -- 目标库位检查 & 完成移库
	@RequestMapping(value="/stockMove")
	@ResponseBody
	public Object stockMove(HttpServletRequest req){
		Map<String,Object> resMap = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
		String fromLocationBarcode = req.getParameter("from_location_barcode").toString();
		String toLocationBarcode = req.getParameter("to_location_barcode").toString();
		Integer plId = Integer.parseInt(String.valueOf(req.getParameter("pl_id")));
		Integer moveNum = Integer.parseInt(String.valueOf(req.getParameter("move_num")));
		logger.info("RF移库之库存移动 ,fromLocationBarcode:"+fromLocationBarcode
				+", toLocationBarcode:"+toLocationBarcode+", plId:"+plId+", moveNum:"+moveNum);
		try{
			resMap = inventoryBiz.stockMove(fromLocationBarcode,toLocationBarcode,plId,moveNum,currentPhysicalWarehouse.getPhysical_warehouse_id());
		}catch (Exception e) {
			resMap.put("success", false);
			resMap.put("error", e.getMessage());
		}
		return resMap;
	}
	
	// 9. RF移库之库存转移 -- 源库位检查
	@RequestMapping(value="/stockTransferSourceLocationCheck")
	@ResponseBody
	public Object stockTransferSourceLocationCheck(HttpServletRequest req){
		Map<String,Object> resMap = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
        String locationBarcode = req.getParameter("from_location_barcode").toString();
		logger.info("RF移库之库存转移 ,fromLocationBarcode:"+locationBarcode);
		resMap = inventoryBiz.checkStockTransferSourceLocation(locationBarcode,currentPhysicalWarehouse.getPhysical_warehouse_id());
		return resMap;
	}
	
	// 10. RF移库之库存转移-- 目标库位检查 & 完成移库
	@RequestMapping(value="/stockTransfer")
	@ResponseBody
	public Object stockTransfer(HttpServletRequest req){
		Map<String,Object> resMap = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
        String fromLocationBarcode = req.getParameter("from_location_barcode").toString();
		String toLocationBarcode = req.getParameter("to_location_barcode").toString();
		Integer plId = Integer.parseInt(String.valueOf(req.getParameter("pl_id")));
		Integer moveNum = Integer.parseInt(String.valueOf(req.getParameter("move_num")));
		logger.info("RF移库之库存转移 ,fromLocationBarcode:"+fromLocationBarcode
				+", toLocationBarcode:"+toLocationBarcode+", plId:"+plId+", moveNum:"+moveNum);
		try{
			resMap = inventoryBiz.stockTransfer(fromLocationBarcode,toLocationBarcode,plId,moveNum,currentPhysicalWarehouse.getPhysical_warehouse_id());
		}catch (Exception e) {
			resMap.put("success", false);
			resMap.put("error", e.getMessage());
		}
		
		return resMap;
	}
	
	
	
	//7.RF取消订单一键上架
		/**
		 * 根据wms订单号显示商品信息
		 * @param req
		 * @return
		 */

		@RequestMapping(value="search_orderId")
		@ResponseBody
		@SuppressWarnings("unused")
		public Object searchOrderId( HttpServletRequest req ){
			
			Subject subject = SecurityUtils.getSubject();
	        Session session = subject.getSession();
	        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
			HashMap<String,Object> resMap = new HashMap<String,Object>();
			HashMap<String,Object> orderMap = new HashMap<String,Object>();
			HashMap<String,Object> searchMap = new HashMap<String,Object>();
			// 1.接收前端变量值
			String orderId = req.getParameter("orderId");
			if(!WorkerUtil.isNullOrEmpty(orderId)){
				orderMap.put("orderId", orderId);
			}
			Integer physical_warehouse_id = currentPhysicalWarehouse.getPhysical_warehouse_id();
			// 2.搜索该商品的信息
			try {
			OrderInfo orderInfo = orderDao.selectByOrderIdOrOrderSn(orderMap);
			if(WorkerUtil.isNullOrEmpty(orderInfo)){
				resMap.put("success",false);
				resMap.put("note","不存在的订单号");
				return resMap;
			}
			if(!orderInfo.ORDER_STATUS_CANCEL.equals(orderInfo.getOrder_status())){
				resMap.put("success",false);
				resMap.put("note","订单不是取消订单");
				return resMap;
			}
			Map map1 = inventoryBiz.selectByOrderId(orderInfo.getOrder_id(), physical_warehouse_id);
			if(Boolean.TRUE.equals((map1.get("success"))))
			{
				resMap.put("success",true); 
				resMap.put("goodsCategory",map1.get("goodsCategory"));
				resMap.put("goodsNumber",map1.get("goodsNumber"));
				resMap.put("location_barcode_normal",map1.get("location_barcode_normal"));
			}else{
				resMap.put("success",false);
				resMap.put("map1",map1);
			}
			}catch (Exception e) {
				resMap.put("success",false);
				resMap.put("note","数据异常");
				e.printStackTrace();
			}
			// 3.返回信息
			return resMap;
		}
		
		/**
		 * 取消订单一键上架
		 * @param req
		 * @return
		 */
		@RequestMapping(value="grounding_allsubmit")
		@ResponseBody
		@SuppressWarnings("unchecked")
		public Object groundingAllSubmitCancelOrder(HttpServletRequest req){
			
			// 1.从session中获取相关信息
			Subject subject = SecurityUtils.getSubject();
	        Session session = subject.getSession();
	        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
	        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
	        
	        // 2.获取前端传来的值
	        String orderId = req.getParameter("orderId");
	        String locationBarcode = req.getParameter("locationBarcode").trim();
			String actionUser = sysUser.getUsername();
			Map<Object,Object> searchMap = new HashMap<Object,Object>();
			
			searchMap.put("orderId", orderId);
			searchMap.put("locationBarcode", locationBarcode);
			searchMap.put("actionUser", actionUser);
			searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
			
			logger.info("order_id:" + orderId +" location_barcode:"
					+ locationBarcode  + " action_user:" + actionUser );
			
			// 结果返回Map
			Map<String,Object> resultMap = new HashMap<String,Object>(); 
			try {
				resultMap = inventoryBiz.createAllReturnAccept(searchMap);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("上架失败", e);
				resultMap.put("success", false);
				resultMap.put("result", "failure");
				resultMap.put("note", "上架失败，失败原因："+e.getMessage());
			}

			logger.info(">>>GroundingSubmitCancelOrder resultMap:"+resultMap);
			return resultMap;
		}
		
		
		
		/**
		 * WEB取消订单一键上架
		 * @param req
		 * @return
		 */
		@RequestMapping(value="/web_grounding_allsubmit")
		@ResponseBody
		@SuppressWarnings("unchecked")
		public Object groundingAllWebSubmitCancelOrder(HttpServletRequest req){
			
			// 1.从session中获取相关信息
			Subject subject = SecurityUtils.getSubject();
	        Session session = subject.getSession();
	        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
	        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
	        // 结果返回Map
	     	Map<String,Object> resultMap = new HashMap<String,Object>(); 
	        // 2.获取前端传来的值
	        //String orderId = req.getParameter("orderId");
	        String locationBarcode = req.getParameter("locationBarcode").trim();
			String actionUser = sysUser.getUsername();
			List<Integer> orderIdList = new ArrayList<Integer>();
			Map<Object,Object> searchMap = new HashMap<Object,Object>();
			String [] order_array =  req.getParameter("orderIdList").split(",");
			 for (int i = 0 ; i <order_array.length ; i++ ) {
				 orderIdList.add(Integer.valueOf(order_array[i]));
			    } 
			
			for (Integer orderId : orderIdList) {
			searchMap.put("orderId", orderId);
			searchMap.put("locationBarcode", locationBarcode);
			searchMap.put("actionUser", actionUser);
			searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
			
			logger.info("orderId"+orderId+" location_barcode:"
					+ locationBarcode  + " action_user:" + actionUser );

			try {
				resultMap = inventoryBiz.createAllReturnAccept(searchMap);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("上架失败", e);
				resultMap.put("success", false);
				resultMap.put("result", "failure");
				resultMap.put("note", "上架失败，失败原因："+e.getMessage());
			}
			}
			logger.info(">>>GroundingSubmitCancelOrder resultMap:"+resultMap);
			return resultMap;
		}
	
	//12. RF移库之库存移动by库位&商品条码 -- 源库位&商品条码检查  (实际移动通用stockMove方法)
	@RequestMapping(value="/stockMoveLocationGoodsCheck")
	@ResponseBody
	public Object stockMoveLocationGoodsCheck(HttpServletRequest req){
		Map<String,Object> resMap = new HashMap<String,Object>();
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
		String locationBarcode = req.getParameter("from_location_barcode").toString();
		String Goodsbarcode = req.getParameter("barcode").toString();
		logger.info("RF移库之库存移动by库位&商品条码,fromLocationBarcode:"+locationBarcode+",Goodsbarcode:"+Goodsbarcode);
		resMap = inventoryBiz.stockMoveLocationGoodsCheck(locationBarcode,currentPhysicalWarehouse.getPhysical_warehouse_id(),Goodsbarcode);
		return resMap;
	}
	
	/**
	 * RF交接发货，加载码托信息
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/loadPallet")
	@ResponseBody
	public Object loadPallet(HttpServletRequest req){
		Map<String,Object> resMap = new HashMap<String,Object>();
		logger.info("RF/loadPallet");
		
		String pallet_sn = req.getParameter("pallet_no");
		Pallet pallet = shipmentBizImpl.selectPalletByPalletSn(pallet_sn);

		if (WorkerUtil.isNullOrEmpty(pallet)) {
			resMap.put("success", false);
			resMap.put("error", "找不到码托，请删除重新输入");
			return resMap;
		}

		List<Map<String,Object>>list=new ArrayList<Map<String,Object>>();
		
		list=shipmentDao.getCancelOrderByPalletSn(pallet_sn);
		if(!WorkerUtil.isNullOrEmpty(list)){
			ArrayList<String> cancelList = new ArrayList<String>();
			for (Map map : list) {
				cancelList.add((String) map.get("tracking_number"));
			}
			resMap.put("error", "以下运单已经取消,请解绑后再操作"+cancelList.toString());
			resMap.put("success", false);
			return resMap;
		}
		
		if (pallet.getShip_status().equals("SHIPPED")) {
			resMap.put("error", "该码托已发货");
			resMap.put("success", false);
			return resMap;
		}

		List<Map> bindList = shipmentBizImpl.getBindListByByPalletSn(pallet_sn);
		if (WorkerUtil.isNullOrEmpty(bindList)) {
			resMap.put("error", "码托未绑定运单号");
			resMap.put("success", false);
			return resMap;
		}

		resMap.put("success", true);
		resMap.put("pallet_shipping_name", bindList.get(0).get("shipping_name"));
		resMap.put("ok_num", bindList.size());

		return resMap;
	}
	
	/**
	 * RF交接发货
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/deliveryPallet")
	@ResponseBody
	public Object deliveryPallet(HttpServletRequest req){
		logger.info("RF/deliveryPallet");
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		String pallet_sn = req.getParameter("pallet_no");
		logger.info("deliveryPallet params pallet_sn:" + pallet_sn);
		Pallet pallet = shipmentBizImpl.selectPalletByPalletSn(pallet_sn);
		
		if (WorkerUtil.isNullOrEmpty(pallet)) {
			resMap.put("error", "找不到码托，请删除重新输入");
			resMap.put("success", false);
			return resMap;
		}
		
		if (pallet.getShip_status().equals("SHIPPED")) {
			resMap.put("error", "该码托已发货");
			resMap.put("success", false);
			return resMap;
		}

		List<Map> bindList = shipmentBizImpl.getBindListByByPalletSn(pallet_sn);
		if (WorkerUtil.isNullOrEmpty(bindList)) {
			resMap.put("error", "码托未绑定运单号");
			resMap.put("success", false);
			return resMap;
		}

        List<Map<String,Object>>list=new ArrayList<Map<String,Object>>();
		
		list=shipmentDao.getCancelOrderByPalletSn(pallet_sn);
		if(!WorkerUtil.isNullOrEmpty(list)){
			ArrayList<String> cancelList = new ArrayList<String>();
			for (Map map : list) {
				cancelList.add((String) map.get("tracking_number"));
			}
			resMap.put("error", "以下已经取消,请解绑后再操作"+cancelList.toString());
			resMap.put("success", false);
			return resMap;
		}
		
		List<String> wrongStatuslList = shipmentBizImpl
				.getWrongStatuslListForPalletShip(pallet_sn);
		if (!WorkerUtil.isNullOrEmpty(wrongStatuslList)) {
			resMap.put("success", false);
			resMap.put("error", "以下运单号对应订单不是复核状态，请解绑"+wrongStatuslList.toString());
			return resMap;
		}
		try {
			Boolean success = shipmentBizImpl.deliverPallet(pallet_sn);

			if (success == true) {

				resMap.put("success", true);

			}
		} catch (Exception e) {
			logger.info(e.getMessage());
			logger.info(e.getStackTrace());
			resMap.put("error", "码托发货异常"+e.getMessage());
			resMap.put("success", false);
			return resMap;
		}

		return resMap;
	}
}
