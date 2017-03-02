package com.leqee.wms.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leqee.wms.biz.ShipmentBiz;
import com.leqee.wms.biz.impl.ProductBizImpl;
import com.leqee.wms.biz.impl.ShipmentBizImpl;
import com.leqee.wms.dao.ConfigDao;
import com.leqee.wms.dao.PalletDao;
import com.leqee.wms.dao.ShipmentDao;
import com.leqee.wms.dao.ShippingDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.Pallet;
import com.leqee.wms.entity.Shipment;
import com.leqee.wms.entity.Shipping;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.page.PageParameter;
import com.leqee.wms.util.WorkerUtil;

@Controller
@RequestMapping(value = "/shippment")
// 指定根路径
public class ShippmentController {

	private Logger logger = Logger.getLogger(ShippmentController.class);

	@Autowired
	ShipmentBizImpl shipmentBizImpl;

	@Autowired
	ProductBizImpl productBizImpl;

	@Autowired
	ShipmentBiz shipmentBiz;

	@Autowired
	ShippingDao shippingDao;
	@Autowired
	ShipmentDao shipmentDao;
	@Autowired
	PalletDao palletDao;
	@Autowired
	WarehouseDao warehouseDao;
	@Autowired
	ConfigDao configDao;
	
	private Cache<String, String> isWeighPackBoxCache;

	@RequiresPermissions("delivery:weighing:*")
	@RequestMapping(value = "/weighing")
	public String accept(HttpServletRequest req, HashMap<String, Object> model) {

		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
		logger.info("shippment weighing");
		RealmSecurityManager securityManager = (RealmSecurityManager) SecurityUtils
				.getSecurityManager();
		CacheManager cacheManager = securityManager.getCacheManager();
		isWeighPackBoxCache = cacheManager.getCache("isWeighPackBoxCache");
		String isWeighPackBox=isWeighPackBoxCache.get(currentPhysicalWarehouse.getPhysical_warehouse_id().toString());
		if(isWeighPackBox == null){
			isWeighPackBox = configDao.getConfigValueByFrezen(currentPhysicalWarehouse.getPhysical_warehouse_id(), 0, "IS_WEIGH_PACKBOX");
			isWeighPackBoxCache.put(currentPhysicalWarehouse.getPhysical_warehouse_id().toString(), isWeighPackBox);
		}
		// 获取传递过来的条件参数
		model.put("isWeighPackBox", isWeighPackBox);
		return "shippment/shipment";
	}

	/**
	 * 检测称重时订单状态
	 * 
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/checkOrderStatus")
	@ResponseBody
	public Map checkOrderStatus(HttpServletRequest req) {
		logger.info("shippment /checkOrderStatus");

		Map<String, Object> resultMap = new HashMap<String, Object>();
		String tracking_number = (String) req.getParameter("tracking_number");
		try{
			String orderStatus = shipmentBiz.getOrderStatusByTrackingNumber(tracking_number);
			resultMap.put("result", "success");
			resultMap.put("order_status", orderStatus);
		}catch (Exception e) {
			resultMap.put("result", "failure");
			resultMap.put("note", e.getMessage());
		}
		return resultMap;
	}

	/**
	 * 检测称重时输入的重量是否合理
	 * 
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/checkWeighing")
	@ResponseBody
	public Map checkWeighing(HttpServletRequest req) {

		logger.info("shippment /checkWeighing");

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String tracking_number = (String) req.getParameter("tracking_number");
		String weight = (String) req.getParameter("weight");
		String barcode = WorkerUtil.isNullOrEmpty(req.getParameter("barcode"))?"":req.getParameter("barcode").toString().trim();
		
		BigDecimal shipping_wms_weight = new BigDecimal(weight)
				.multiply(new BigDecimal(1000));
		logger.info("shippment checkWeighing params tracking_number:"
				+ tracking_number + " weight:" + weight + " barcode:" + barcode);

		try {
			shipmentBizImpl.setShipmentPackageWeight(shipping_wms_weight,
					tracking_number,barcode);
			resultMap.put("weighing_check", 0);
		} catch (Exception e) {
			logger.error(" checkWeighing error", e);
			resultMap.put("weighing_check", 1);
			resultMap.put("note", e.getMessage());
		
		}


		return resultMap;
	}


	/**
	 * 记录重量
	 * 
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/updateLeqeeWeight")
	@ResponseBody
	public Map updateLeqeeWeight(HttpServletRequest req) {

		logger.info("shippment /updateLeqeeWeight");

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String tracking_number = (String) req.getParameter("tracking_number");
		String weight = (String) req.getParameter("weight");
		String barcode = WorkerUtil.isNullOrEmpty(req.getParameter("barcode"))?"":req.getParameter("barcode").toString().trim();
		BigDecimal shipping_wms_weight = new BigDecimal(weight)
				.multiply(new BigDecimal(1000));
		logger.info("shippment updateLeqeeWeight params tracking_number:"
				+ tracking_number + " weight:" + shipping_wms_weight + " barcode:" + barcode);

		try {
			shipmentBizImpl.setShipmentPackageWeight(shipping_wms_weight,
					tracking_number,barcode);
			resultMap.put("success", 1);
		} catch (Exception e) {
			logger.error(" updateLeqeeWeight error", e);
			resultMap.put("success", 0);
			resultMap.put("note", e.getMessage());
		}


		return resultMap;
	}

	/**
	 * 码托页面
	 * 
	 * @param req
	 * @return
	 */
	@RequiresPermissions("delivery:printMT:*")
	@RequestMapping(value = "/printMT")
	public String printMT(HttpServletRequest req, HashMap<String, Object> model) {

		logger.info("shippment /printMT");
		return "/shippment/print_MTCode";
	}

	/**
	 * 获取托盘条码
	 * 
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/getTrayBarcodes")
	@ResponseBody
	public Map getTrayBarcodes(HttpServletRequest req) {

		logger.info("shippment /getTrayBarcodes");

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		Integer number = Integer.parseInt(req.getParameter("number").trim());

		if (WorkerUtil.isNullOrEmpty(number)) {
			resultMap.put("error", "number is null or empty");

			logger.info("shippment getTrayBarcodes number is null or empty");
			return resultMap;
		}

		logger.info("shippment getTrayBarcodes params number:" + number);

		// 获取码托 并且插入记录
		try {
			resultMap = shipmentBizImpl.getTrayBarcodeForMT(number);
		} catch (Exception e) {
			logger.error("getTrayBarcodeForMT Failed! Exception: " + e.getMessage(), e);
			resultMap.put("error", "生成码托条码异常,请刷新后重试");
			resultMap.put("success", false);
		}

		return resultMap;
	}

	/**
	 * 码托绑定页面
	 * 
	 * @param req
	 * @return
	 */
	@RequiresPermissions("delivery:bindMT:*")
	@RequestMapping(value = "/bindMT")
	public String bindMT(HttpServletRequest req, HashMap<String, Object> model) {

		logger.info("shippment /bindMT");
		return "/shippment/pallet_bind";
	}

	/**
	 * 获取托盘条码
	 * 
	 * @param req
	 * @return
	 */	
	@RequiresPermissions("delivery:bindMT:*")
	@RequestMapping(value = "/checkPalletSn")
	@ResponseBody
	public Map checkPalletSn(HttpServletRequest req) {

		logger.info("shippment /checkPalletSn");

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String pallet_sn = req.getParameter("pallet_sn");

		if (WorkerUtil.isNullOrEmpty(pallet_sn)) {
			resultMap.put("error", "number is null or empty");
			resultMap.put("success", false);
			logger.info("shippment getTrayBarcodes pallet_sn is null or empty");
			return resultMap;
		}

		Pallet pallet = shipmentBizImpl.selectPalletByPalletSn(pallet_sn);

		if (WorkerUtil.isNullOrEmpty(pallet)) {

			resultMap.put("error", "该托盘条码不存在");
			resultMap.put("success", false);
			logger.info("shippment getTrayBarcodes pallet_sn is not existing");
			return resultMap;

		}

		if (pallet.getShip_status().equals("SHIPPED")) {

			resultMap.put("error", "该托盘条码已经发货");
			resultMap.put("success", false);
			logger.info("shippment getTrayBarcodes pallet_sn is shipped");
			return resultMap;
		}

		List<Map> bindList = shipmentBizImpl.getBindListByByPalletSn(pallet_sn);

		if (WorkerUtil.isNullOrEmpty(bindList) || bindList.size() == 0) {

			resultMap.put("success", true);
			resultMap.put("num", 0);
			logger.info("shippment getTrayBarcodes pallet_sn Bindnum is 0");
			return resultMap;

		}
		resultMap.put("success", true);
		resultMap.put("num", bindList.size());
		resultMap.put("way", bindList.get(0).get("shipping_name"));
		resultMap.put("ship", bindList.toArray());

		logger.info("shippment getTrayBarcodes params :" + pallet_sn + " size:"
				+ bindList.size());

		return resultMap;
	}

	/**
	 * 检测运单号
	 * 
	 * @param req
	 * @return
	 */
	@RequiresPermissions("delivery:bindMT:*")
	@RequestMapping(value = "/checkTrackingSn")
	@ResponseBody
	public Map checkTrackingSn(HttpServletRequest req) {

		logger.info("shippment /checkTrackingSn");

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String pallet_sn = req.getParameter("pallet_no");

		String tracking_no = req.getParameter("tracking_no");
				
		
		//查看运单对应订单取消的数目
		int count = shipmentDao.queryCancelOrderByTrackingNo(tracking_no);
		
		if(count>0){
			resultMap.put("error", "运单对应的订单已经取消！");
			resultMap.put("success", false);
			resultMap.put("error_id", 1);
			
			logger.info("shippment /checkTrackingSn 运单对应的订单已经取消");

			return resultMap;

		}

		logger.info("checkTrackingSn params pallet_sn:" + pallet_sn
				+ " tracking_no:" + tracking_no);

		
		String batch_pick_type=shipmentDao.getBatchPickTypeByTrackingNo(tracking_no);
		
		if("BATCH".equalsIgnoreCase(batch_pick_type)){
			logger.info("shippment /checkTrackingSn 该运单属于波此单！，请走批量单流程");
			resultMap.put("error", "该运单属于波此单！请走批量单流程");
			resultMap.put("success", false);
			resultMap.put("error_id", 1);
			return resultMap;

		}
		
		
		Pallet pallet = shipmentBizImpl.selectPalletByPalletSn(pallet_sn);

		if (WorkerUtil.isNullOrEmpty(pallet)
				|| WorkerUtil.isNullOrEmpty(pallet_sn)) {

			resultMap.put("error", "码托条码不存在！");
			resultMap.put("success", false);
			resultMap.put("error_id", 1);

			logger.info("shippment /checkTrackingSn tracking_no is null or cant't get the pallet by pallet_sn");

			return resultMap;

		}

		if ("SHIPPED".equals(pallet.getShip_status())) {

			resultMap.put("error", "该托盘条码已经发货");
			resultMap.put("success", false);
			resultMap.put("error_id", 1);

			logger.info("shippment getTrayBarcodes pallet_sn is shipped");
			return resultMap;
		}

		Map orderStatus = shipmentBizImpl
				.getOrderStautsByTrackingNo(tracking_no);

		if (WorkerUtil.isNullOrEmpty(orderStatus)) {

			resultMap.put("error", "运单号不存在！");
			resultMap.put("success", false);
			resultMap.put("error_id", 3);

			return resultMap;
		}

		int opstatus=Integer.parseInt(orderStatus.get("opstatus").toString());
		
//		when 'ACCEPT' then 10
//		when 'BATCH_PICK' then 20
//		when 'PICKING' then 30
//		when 'RECHECKED' then 40
//		when 'WEIGHED' then 50
//		when 'DELIVERED' then 60
//		when 'IN_PROCESS' then 15
//		when 'ON_SHIP' then 25
//		when 'FULFILLED' then 70
//		when 'EXCEPTION' then '异常' 9
//		when 'CANCEL' then '取消' 8
//		else 7 end 
		
		if(opstatus<10){
			resultMap.put("error", "绑定失败，该运单号对应的订单不可绑定！");
			resultMap.put("success", false);
			resultMap.put("error_id", 2);

			return resultMap;
		}else if(opstatus<40){
			resultMap.put("error", "绑定失败，该运单号对应的订单未复核！");
			resultMap.put("success", false);
			resultMap.put("error_id", 2);

			return resultMap;
		}else if(opstatus>50){
			resultMap.put("error", "绑定失败，该运单号对应的订单已经发货！");
			resultMap.put("success", false);
			resultMap.put("error_id", 2);

			return resultMap;
		}
		
		List<Map> list = shipmentDao
				.getPalletShipmentMappingByTrackingNo(tracking_no);
		
		if(!WorkerUtil.isNullOrEmpty(list)){
			Map mappingMap = list.get(0);

			// Map mappingMap =
			// shipmentDao.getPalletShipmentMappingByTrackingNo(tracking_no);

			if (!WorkerUtil.isNullOrEmpty(mappingMap)
					&& mappingMap.get("bind_status").equals("BINDED")) {

				resultMap.put(
						"error",
						"运单号" + tracking_no + "已经与"
								+ mappingMap.get("pallet_no") + "绑定！");
				resultMap.put("success", false);
				resultMap.put("error_id", 1);

				return resultMap;
			}
			
			for (Map map : list) {
				if (!WorkerUtil.isNullOrEmpty(map)
						&& map.get("bind_status").equals("UNBINDED")
						&& map.get("pallet_no").equals(pallet_sn)) {

					resultMap.put("error", "运单号" + "tracking_no" + "已经与此码拖"
							+ mappingMap.get("pallet_no")
							+ "解绑，不能在同一个码托上解绑再绑定！");
					resultMap.put("success", false);
					resultMap.put("error_id", 1);

					return resultMap;
				}
			}
		}
		
		
//		if ((!orderStatus.get("opstatus").equals("RECHECKED"))&&(!orderStatus.get("opstatus").equals(OrderInfo.ORDER_STATUS_WEIGHED))) {
//			resultMap.put("error", "绑定失败，该运单号对应的订单未复核或者已经发货！");
//			resultMap.put("success", false);
//			resultMap.put("error_id", 2);
//
//			return resultMap;
//		}
		if (!orderStatus.get("status").equals("WEIGHED")) {

			resultMap.put("error", "该包裹需要先称重！");
			resultMap.put("success", false);
			resultMap.put("error_id", 4);

			return resultMap;
		}

		resultMap = shipmentBizImpl.getPackageBindMapping(tracking_no,
				pallet_sn);

		return resultMap;
	}

	/**
	 * 码托解绑页面
	 * 
	 * @param req
	 * @return
	 */
	@RequiresPermissions("delivery:unbindMT:*")
	@RequestMapping(value = "/unbindMT")
	public String unbindMT(HttpServletRequest req, HashMap<String, Object> model) {

		logger.info("shippment /unbindMT");
		return "/shippment/pallet_unbind";
	}

	@RequestMapping(value = "/checkTrackingUnbind")
	@ResponseBody
	public Map checkTrackingUnbind(HttpServletRequest req) {

		logger.info("shippment /checkTrackingUnbind");

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String tracking_no = req.getParameter("tracking_no");

		logger.info("checkTrackingUnbind params tracking_no:" + tracking_no);

		Map infoMap = shipmentBizImpl
				.getShippingInfoByTrackingNoForUnBind(tracking_no);

		if (WorkerUtil.isNullOrEmpty(infoMap)) {
			resultMap.put("error", "该运单号不存在或者已经解绑！");
			resultMap.put("success", false);
			return resultMap;
		}

		if (infoMap.get("ship_status").equals("SHIPPED")) {
			resultMap.put("error", "该运单对应码托已经发货！");
			resultMap.put("success", false);
			return resultMap;
		}

		resultMap = shipmentBizImpl
				.getUnbindShipmentInfoByTrackingNo(tracking_no);

		return resultMap;
	}

	/**
	 * 输入运单号展示码拖标签
	 * @author dlyao
	 * @param req
	 * @return
	 */
	@RequiresPermissions("delivery:unbindMT:*")
	@RequestMapping(value = "/showMtByTracking")
	@ResponseBody
	public Map showMtByTracking(HttpServletRequest req) {

		logger.info("shippment /showMtByTracking");

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String tracking_no = req.getParameter("tracking_no");

		logger.info("showMtByTracking params tracking_no:" + tracking_no);

		Map infoMap = shipmentBizImpl
				.getShippingInfoByTrackingNoForUnBind(tracking_no);

		if (WorkerUtil.isNullOrEmpty(infoMap)) {
			resultMap.put("error", "该运单号不存在或者已经解绑！");
			resultMap.put("success", false);
			return resultMap;
		}

		if (infoMap.get("ship_status").equals("SHIPPED")) {
			resultMap.put("error", "该运单对应码托已经发货！");
			resultMap.put("success", false);
			return resultMap;
		}

		resultMap = shipmentBizImpl
				.getUnbindShipmentInfoByTrackingNo2(tracking_no);

		return resultMap;
	}
	
	/**
	 * 输入运单号和码拖标签进行码拖解绑
	 * @author dlyao
	 * @param req
	 * @return
	 */
	@RequiresPermissions("delivery:unbindMT:*")
	@RequestMapping(value = "/mtUnBind")
	@ResponseBody
	public Map mtUnBind(HttpServletRequest req) {

		logger.info("shippment /mtUnBind");

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String tracking_no = req.getParameter("tracking_no");
		String pallet_sn = req.getParameter("pallet_sn");

		logger.info("mtUnBind params tracking_no:" + tracking_no+"  pallet_no:"+pallet_sn);

		Map infoMap = shipmentBizImpl
				.getShippingInfoByTrackingNoForUnBind(tracking_no);

		if (WorkerUtil.isNullOrEmpty(infoMap)) {
			resultMap.put("error", "该运单号不存在或者已经解绑！");
			resultMap.put("success", false);
			return resultMap;
		}

		if (infoMap.get("ship_status").equals("SHIPPED")) {
			resultMap.put("error", "该运单对应码托已经发货！");
			resultMap.put("success", false);
			return resultMap;
		}

		resultMap = shipmentBizImpl
				.getUnbindShipmentInfoByTrackingNo(tracking_no);

		return resultMap;
	}
	
	/**
	 * 根据码拖条码查询
	 * @author dlyao
	 * @param req
	 * @return
	 */
	@RequiresPermissions("delivery:bindMT:*")
	@RequestMapping(value = "/queryByPalletSn")
	@ResponseBody
	public Map queryByPalletSn(HttpServletRequest req) {

		logger.info("shippment /queryByPalletSn");

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String pallet_sn = req.getParameter("pallet_sn");

		logger.info("queryByPalletSn params pallet_sn:"+pallet_sn);

		resultMap = shipmentBizImpl.queryByPalletSn(pallet_sn);

		return resultMap;
	}
	
	/**
	 * 根据运单号查询
	 * @author dlyao
	 * @param req
	 * @return
	 */
	@RequiresPermissions("delivery:bindMT:*")
	@RequestMapping(value = "/queryByTrackingNo")
	@ResponseBody
	public Map queryByTrackingNo(HttpServletRequest req) {

		logger.info("shippment /queryByTrackingNo");

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		String tracking_no = req.getParameter("tracking_no");

		logger.info("queryByTrackingNo params tracking_no:"+tracking_no);

		resultMap = shipmentBizImpl.queryByTrackingNo(tracking_no);

		return resultMap;
	}
	
	
	
	
	/**
	 * 码托发货页面
	 * 
	 * @param req
	 * @return
	 */
	@RequiresPermissions("delivery:palletShipment:*")
	@RequestMapping(value = "/palletShipment")
	public String palletShipment(HttpServletRequest req,
			HashMap<String, Object> model) {

		logger.info("shippment /palletShipment");
		return "/shippment/pallet_shipment";
	}

	/**
	 * 加载码托信息
	 * 
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/loadPallet")
	public String loadPallet(HttpServletRequest req,
			HashMap<String, Object> model) {

		logger.info("shippment /loadPallet");

		String pallet_sn = req.getParameter("pallet_no");
		Pallet pallet = shipmentBizImpl.selectPalletByPalletSn(pallet_sn);

		if (WorkerUtil.isNullOrEmpty(pallet)) {
			model.put("error", "找不到码托，请删除重新输入");
			model.put("message", "");
			return "/shippment/pallet_shipment";
		}
		model.put("pallet_no", pallet_sn);

		List<Map<String,Object>>list=new ArrayList<Map<String,Object>>();
		
		list=shipmentDao.getCancelOrderByPalletSn(pallet_sn);
		if(!WorkerUtil.isNullOrEmpty(list)){
			StringBuffer sb=new StringBuffer();
			for(Map m:list){
				sb.append("</br>").append("[运单号为").append(m.get("tracking_number")).append("的包裹，所属订单为：").append(m.get("order_id")).append(']');
			}
			model.put("error", "以下已经取消,请解绑后再操作"+sb.toString());
			model.put("message", "");
			return "/shippment/pallet_shipment";
		}
		
		if (pallet.getShip_status().equals("SHIPPED")) {
			model.put("has_shipped", true);
			return "/shippment/pallet_shipment";
		}

		List<Map> bindList = shipmentBizImpl.getBindListByByPalletSn(pallet_sn);
		if (WorkerUtil.isNullOrEmpty(bindList)) {
			model.put("error", "码托未绑定运单号");
			model.put("message", "");
			return "/shippment/pallet_shipment";
		}

		ArrayList<String> deliverList = new ArrayList<String>();
		for (Map map : bindList) {
			deliverList.add((String) map.get("tracking_number"));
		}

		model.put("pallet_shipping_name", bindList.get(0).get("shipping_name"));
		model.put("ok_num", bindList.size());
		model.put("delivery_numbers", deliverList.toString());
		model.put("can_ship", true);

		return "/shippment/pallet_shipment";
	}

	/**
	 * 码拖发货
	 * 
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/deliveryPallet")
	public String deliveryPallet(HttpServletRequest req,
			HashMap<String, Object> model) {

		logger.info("shippment /deliveryPallet");

		String pallet_sn = req.getParameter("pallet_no");
		logger.info("deliveryPallet params pallet_sn:" + pallet_sn);

		Pallet pallet = shipmentBizImpl.selectPalletByPalletSn(pallet_sn);
		
		if (WorkerUtil.isNullOrEmpty(pallet)) {
			model.put("error", "找不到码托，请删除重新输入");
			model.put("message", "");
			return "/shippment/pallet_shipment";
		}
		
		if (pallet.getShip_status().equals("SHIPPED")) {
			model.put("error", "码托已发货");
			model.put("message", "");
			return "/shippment/pallet_shipment";
		}
		
		List<Map> bindList = shipmentBizImpl.getBindListByByPalletSn(pallet_sn);
		if (WorkerUtil.isNullOrEmpty(bindList)) {
			model.put("error", "码托未绑定运单号");
			model.put("message", "");
			return "/shippment/pallet_shipment";
		}

        List<Map<String,Object>>list=new ArrayList<Map<String,Object>>();
		
		list=shipmentDao.getCancelOrderByPalletSn(pallet_sn);
		if(!WorkerUtil.isNullOrEmpty(list)){
			StringBuffer sb=new StringBuffer();
			for(Map m:list){
				sb.append("</br>").append("[运单号为").append(m.get("tracking_number")).append("的包裹，所属订单为：").append(m.get("order_id")).append(']');
			}
			model.put("error", "以下已经取消,请解绑后再操作"+sb.toString());
			model.put("message", "");
			return "/shippment/pallet_shipment";
		}
		
		List<String> wrongStatuslList = shipmentBizImpl
				.getWrongStatuslListForPalletShip(pallet_sn);
		if (!WorkerUtil.isNullOrEmpty(wrongStatuslList)) {
			model.put("error", "以下运单号对应订单不是复核状态，请解绑");
			model.put("message", wrongStatuslList.toString());
			return "/shippment/pallet_shipment";
		}
		try {
			Boolean success = shipmentBizImpl.deliverPallet(pallet_sn);

			if (success == true) {

				model.put("shipped", true);

			}
		} catch (Exception e) {
			logger.info(e.getMessage());
			logger.info(e.getStackTrace());
			model.put("error", "码托发货异常");
			model.put("message", e.getMessage());
			return "/shippment/pallet_shipment";
		}

		return "/shippment/pallet_shipment";
	}

	
	/**
	 * 快递交接单查询,打印
	 * @author mao
	 * @date 2016-3-22
	 **/
	@RequestMapping(value = "/palletShipmentList")
	@SuppressWarnings("rawtypes")
	public String transferListV2(HttpServletRequest req, Map<String, Object> model) {
		// 获取前端输入数据
		// 1.获得session
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
		// 快递方式
		List<Shipping> shippingList = shippingDao.selectAllShipping();
		//List<Warehouse> warehouseList = warehouseDao.selectPhysicalWarehouseList();
		model.put("shippingList", shippingList);
		//model.put("warehouseList", warehouseList);
		return "shippment/palletShipmentList";
	}
	
//	@RequestMapping(value = "/palletShipmentList")
//	@SuppressWarnings("rawtypes")
//	public String transferList(HttpServletRequest req, Map<String, Object> model) {
//		// 获取前端输入数据
//		// 1.获得session
//		Subject subject = SecurityUtils.getSubject();
//        Session session = subject.getSession();
//        
//        // 2.获取当前物理仓
//        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
//		        
//		Map<String, Object> searchListMap = new HashMap<String, Object>();
//		String shippingId = req.getParameter("shipping_id");
//		String startTime = req.getParameter("delivery_start_time");
//		String endTime = req.getParameter("delivery_end_time");
//		String palletNo = req.getParameter("pallet_no");
//		String physicalWarehouseId = currentPhysicalWarehouse.getPhysical_warehouse_id()+"";
//		String act = req.getParameter("act");
//
//		if (!WorkerUtil.isNullOrEmpty(shippingId)) {
//			searchListMap.put("shippingId", shippingId);
//			model.put("shipping_id", shippingId);
//		}
//		if(!WorkerUtil.isNullOrEmpty(physicalWarehouseId)){
//			searchListMap.put("physicalWarehouseId", physicalWarehouseId);
//			model.put("physical_warehouse_id", physicalWarehouseId);
//		}
//		if (!WorkerUtil.isNullOrEmpty(startTime)) {
//			searchListMap.put("startTime", startTime);
//			model.put("delivery_start_time", startTime);
//		}else{
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			String startDate = sdf.format(new Date(System.currentTimeMillis() - 3600000 *240 ));
//			searchListMap.put("startTime", startDate);
//			model.put("delivery_start_time", startDate);
//		}
//		if (!WorkerUtil.isNullOrEmpty(endTime)) {
//			searchListMap.put("endTime", endTime);
//			model.put("delivery_end_time", endTime);
//		}
//		if (!WorkerUtil.isNullOrEmpty(palletNo)) {
//			searchListMap.put("palletNo", palletNo);
//			model.put("pallet_no", palletNo);
//		}
//
//		
//		// 2.分页信息设置
//		PageParameter page = null;
//		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
//			page = new PageParameter(1,20);
//		}else{
//			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
//		}
//		
//		searchListMap.put("page", page);
//		
//		// 查找特定条件下的交接单
//		List<Map> palletShipmentList =shipmentBiz.getPalletShipmentList(searchListMap);
//		if("search".equals(act)){
//			model.put("palletShipmentList", palletShipmentList);
//		}else{
//			model.put("palletShipmentList", null);
//		}
//
//		// 快递方式
//		List<Shipping> shippingList = shippingDao.selectAllShipping();
//		//List<Warehouse> warehouseList = warehouseDao.selectPhysicalWarehouseList();
//		model.put("shippingList", shippingList);
//		//model.put("warehouseList", warehouseList);
//		return "shippment/palletShipmentList";
//	}

	
	
	
	/**
	 * 快递交接单查询,打印
	 * @author mao
	 * @date 2016-3-22
	 **/
	@RequestMapping(value = "/queryPalletShipmentList")
	@SuppressWarnings("rawtypes")
	@ResponseBody
	public Map<String, Object> transferList(HttpServletRequest req) {
		//返回值
		Map<String, Object> model=new HashMap<String, Object>();
		// 获取前端输入数据
		// 1.获得session
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        
        // 2.获取当前物理仓
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
		        
		Map<String, Object> searchListMap = new HashMap<String, Object>();
		String shippingId = req.getParameter("shipping_id");
		String startTime = req.getParameter("delivery_start_time");
		String endTime = req.getParameter("delivery_end_time");
		String palletNo = req.getParameter("pallet_no");
		Integer print =Integer.parseInt(req.getParameter("print_count"));
		String physicalWarehouseId = currentPhysicalWarehouse.getPhysical_warehouse_id()+"";
		String act = req.getParameter("act");

		searchListMap.put("print", print);
		
		if (!WorkerUtil.isNullOrEmpty(shippingId)) {
			searchListMap.put("shippingId", shippingId);
			model.put("shipping_id", shippingId);
		}
		if(!WorkerUtil.isNullOrEmpty(physicalWarehouseId)){
			searchListMap.put("physicalWarehouseId", physicalWarehouseId);
			model.put("physical_warehouse_id", physicalWarehouseId);
		}
		if (!WorkerUtil.isNullOrEmpty(startTime)) {
			searchListMap.put("startTime", startTime);
			model.put("delivery_start_time", startTime);
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String startDate = sdf.format(new Date(System.currentTimeMillis() - 3600000 *240 ));
			searchListMap.put("startTime", startDate);
			model.put("delivery_start_time", startDate);
		}
		if (!WorkerUtil.isNullOrEmpty(endTime)) {
			searchListMap.put("endTime", endTime);
			model.put("delivery_end_time", endTime);
		}
		if (!WorkerUtil.isNullOrEmpty(palletNo)) {
			searchListMap.put("palletNo", palletNo);
			model.put("pallet_no", palletNo);
		}

		
		// 2.分页信息设置
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,20);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		
		int shipmentCount = palletDao.selectPalletShipmentCount(searchListMap);
		
		searchListMap.put("page", page);
		
		// 查找特定条件下的交接单
		List<Map> palletShipmentList =shipmentBiz.getPalletShipmentList(searchListMap);
		if("search".equals(act)){
			model.put("palletShipmentList", palletShipmentList);
			model.put("count", shipmentCount);
		}else{
			model.put("palletShipmentList", null);
		}

		// 快递方式
		List<Shipping> shippingList = shippingDao.selectAllShipping();
		//List<Warehouse> warehouseList = warehouseDao.selectPhysicalWarehouseList();
		model.put("shippingList", shippingList);
		model.put("physicalWarehouse", currentPhysicalWarehouse);
		model.put("page", page);
		//model.put("warehouseList", warehouseList);
		return model;
	}

	/**
	 * 打印
	 * 
	 * @return
	 */
	@RequestMapping(value = "/print")
	@ResponseBody
	public HashMap<String, Object> print(HttpServletRequest req) {

		HashMap<String, Object> model=new HashMap<String, Object>();
		
		String mtCode = req.getParameter("mt_code");

		String code[] = mtCode.split(",");

		List<String> codeList = new ArrayList<String>();
		for (String str : code) {
			codeList.add(str);
		}

		shipmentBiz.updatePalletPrintCount(codeList);
		model.put("codeList", codeList);
		model.put("result", "success");
		
		return model;
	}

	/**
	 * 打印码托条码（生成码托条码时的打印）
	 * @param req
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "printV0")
	public String printV0(HttpServletRequest req, HashMap<String, Object> model) {

		String mtCode = req.getParameter("mt_code");

		String code[] = mtCode.split(",");

		List<String> codeList = new ArrayList<String>();
		for (String str : code) {
			codeList.add(str);
		}

		model.put("codeList", codeList);

		return "/shippment/print";
	}
}