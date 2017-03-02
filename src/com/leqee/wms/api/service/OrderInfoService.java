package com.leqee.wms.api.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leqee.wms.api.LeqeeError;
import com.leqee.wms.api.biz.OrderInfoApiBiz;
import com.leqee.wms.api.request.AdjustPriceRequest;
import com.leqee.wms.api.request.CancelOrderPrePackRequest;
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
import com.leqee.wms.api.response.AdjustPriceResponse;
import com.leqee.wms.api.response.CancelOrderResponse;
import com.leqee.wms.api.response.GetOrderListResponse;
import com.leqee.wms.api.response.GetOrderPrepacksResponse;
import com.leqee.wms.api.response.GetOrderShipmentListResponse;
import com.leqee.wms.api.response.GetPurchaseOrderResponse;
import com.leqee.wms.api.response.GetRmaOrderResponse;
import com.leqee.wms.api.response.GetSaleOrderResponse;
import com.leqee.wms.api.response.GetVarianceOrderResponse;
import com.leqee.wms.api.response.SyncOrderPrepackResponse;
import com.leqee.wms.api.response.SyncPurchaseOrderResponse;
import com.leqee.wms.api.response.SyncRmaOrderResponse;
import com.leqee.wms.api.response.SyncSaleOrderResponse;
import com.leqee.wms.api.response.SyncVarianceOrderResponse;
import com.leqee.wms.api.response.TerminalOrderResponse;
import com.leqee.wms.api.response.domain.AdjustPriceResDomain;
import com.leqee.wms.api.response.domain.OmsOrderTransferCodeResDomain;
import com.leqee.wms.api.response.domain.OrderGoodsResDomain;
import com.leqee.wms.api.response.domain.OrderInfoResDomain;
import com.leqee.wms.api.response.domain.OrderPackboxResDomain;
import com.leqee.wms.api.response.domain.OrderPrepackResDomain;
import com.leqee.wms.api.response.domain.OrderShipmentResDomain;
import com.leqee.wms.api.response.domain.ShipmentResDomain;
import com.leqee.wms.api.util.WorkerUtil;
import com.leqee.wms.biz.OrderInfoBiz;
import com.leqee.wms.biz.OrderPrepackBiz;
import com.leqee.wms.entity.OrderPrepack;
import com.leqee.wms.util.JacksonJsonUtil;

/**
 * 订单相关API
 * @author qyyao
 * @date 2016-2-23
 * @version 1.0
 */
@Controller
@RequestMapping(value="/api/order") 
public class OrderInfoService {
	private Logger logger = Logger.getLogger(OrderInfoService.class);
	@Autowired
	OrderInfoApiBiz orderInfoApiBiz;
	
	@Autowired
	OrderInfoBiz orderInfoBiz;
	
	@Autowired
	OrderPrepackBiz orderPrepackBiz;
	private final static byte[] createSyncPrepackLock = new byte[0];
	/**
	 * 采购入库单订单同步
	 * @author hzhang1
	 * @return
	 */
	@RequestMapping(value="/syncpurchaseorder", method = RequestMethod.POST)
	@ResponseBody
	public Object syncpurchaseorder(HttpServletRequest request){
		logger.info("Enter the api of syncpurchaseorder...");
		
		SyncPurchaseOrderResponse purchaseOrderResponse = new SyncPurchaseOrderResponse();
		String data = (String) request.getAttribute("data");
		String app_key = (String) request.getAttribute("app_key");
		SyncPurchaseOrderRequest syncPurchaseOrderRequest = null;
		
		// 1.检查app_key生成customer_id...
		Integer customerId = orderInfoApiBiz.getcustomerIdByAppKey(app_key);
		
		// 2.将request转换成对象
		try {
			syncPurchaseOrderRequest = (SyncPurchaseOrderRequest) JacksonJsonUtil.jsonToBean(data, SyncPurchaseOrderRequest.class);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			//System.out.println(errorStr.substring(0, errorStr.indexOf("(")));
			
			//logger.error("purchaseOrderResponse error:", e);
			purchaseOrderResponse.setResult("failure");
			purchaseOrderResponse.setNote("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			purchaseOrderResponse.setErrors(errorsList);
			return purchaseOrderResponse;
		}
		
		// 3.调用API Biz层同步商品方法
		Map<String,Object> resMap = new HashMap<String, Object>();
		try {   
			resMap = orderInfoApiBiz.syncPurchaseOrder(syncPurchaseOrderRequest,customerId);
		}catch (Exception e) {
			logger.error("syncPurchaseOrder异常", e);
			e.printStackTrace();
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
		}
		
		
		
		if("success".equals(resMap.get("result").toString())){
			purchaseOrderResponse.setResult("success");
			purchaseOrderResponse.setNote("success");
			// 返回order_id 和  oms_order_sn
			Integer wmsOrderId = (Integer) resMap.get("orderId");
			String omsOrderSn = resMap.get("omsOrderSn").toString();
			purchaseOrderResponse.setWms_order_id(wmsOrderId);
			purchaseOrderResponse.setOms_order_sn(omsOrderSn);
			purchaseOrderResponse.setErrors(null);
		}else{
			purchaseOrderResponse.setResult("failure");
			purchaseOrderResponse.setNote(resMap.get("msg").toString());
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			purchaseOrderResponse.setErrors(errorsList);
		}
		
		// 4.返回orderInfoInCreateResponse
		return purchaseOrderResponse;
	}
	
	/**
	 * -V订单同步
	 * @author hzhang1
	 * @return
	 */
	@RequestMapping(value="/syncvarianceorder", method = RequestMethod.POST)
	@ResponseBody
	public Object syncvarianceorder(HttpServletRequest request){
		logger.info("Enter the api of syncvarianceorder...");
		SyncVarianceOrderResponse varianceOrderResponse = new SyncVarianceOrderResponse();
		
		String data = (String) request.getAttribute("data");
		String app_key = (String) request.getAttribute("app_key");
		SyncVarianceOrderRequest syncVarianceOrderRequest = null;
		// 1.检查app_key生成customer_id...
		Integer customerId = orderInfoApiBiz.getcustomerIdByAppKey(app_key);
		
		// 2.将request转换成对象
		try {
			syncVarianceOrderRequest = (SyncVarianceOrderRequest) JacksonJsonUtil.jsonToBean(data, SyncVarianceOrderRequest.class);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			//System.out.println(errorStr.substring(0, errorStr.indexOf("(")));
			
			//logger.error("purchaseOrderResponse error:", e);
			varianceOrderResponse.setResult("failure");
			varianceOrderResponse.setNote("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			varianceOrderResponse.setErrors(errorsList);
			return varianceOrderResponse;
		}
		
		// 3.调用API Biz层同步方法
		Map<String,Object> resMap = new HashMap<String, Object>();
		try {   
			resMap = orderInfoApiBiz.syncVarianceOrder(syncVarianceOrderRequest,customerId);
		}catch (Exception e) {
			logger.error("syncVarianceOrder异常", e);
			e.printStackTrace();
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
		}
		if("success".equals(resMap.get("result").toString())){
			varianceOrderResponse.setResult("success");
			varianceOrderResponse.setNote("success");
			varianceOrderResponse.setOms_order_sn(resMap.get("omsOrderSn").toString());
			varianceOrderResponse.setWms_order_id((Integer)resMap.get("orderId"));
			// 返回order_id 和  oms_order_sn
			varianceOrderResponse.setErrors(null);
		}else{
			varianceOrderResponse.setResult("failure");
			varianceOrderResponse.setNote(resMap.get("msg").toString());
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			varianceOrderResponse.setErrors(errorsList);
		}
		
		// 4.返回orderInfoInCreateResponse
		return varianceOrderResponse;
	}
	
	/**
	 * @xhchen
	 * @param request
	 * @return
	 */
	@RequestMapping("/syncprepacktask")
	@ResponseBody
	public Object syncPrepackOrder(HttpServletRequest request){
		logger.info("Enter the api of syncprepackorder");
		SyncOrderPrepackResponse prepackResponse = new SyncOrderPrepackResponse();
		SyncOrderPrepackRequest prepackRequest = null;
		
		String data = (String) request.getAttribute("data");
		String appKey = (String) request.getAttribute("app_key");
		// 1.检查app_key生成customer_id...
		Integer customerId = orderInfoApiBiz.getcustomerIdByAppKey(appKey);
		
		// 2.将request转换成对象
		try {
			prepackRequest = (SyncOrderPrepackRequest) JacksonJsonUtil.jsonToBean(data, SyncOrderPrepackRequest.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			//System.out.println(errorStr.substring(0, errorStr.indexOf("(")));
			
			logger.error("syncSaleOrderResponse error:", e);
			prepackResponse.setResult("failure");
			prepackResponse.setNote("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			prepackResponse.setErrors(errorsList);
			return prepackResponse;
		}
		
		// 3.调用API Biz层发货单同步方法
		HashMap<String,Object> resMap = new HashMap<String, Object>();
		
		try {   
			synchronized (createSyncPrepackLock) {
				resMap = orderInfoApiBiz.syncPrepackTask(prepackRequest,customerId);
			}
		}catch (Exception e) {
			logger.error("syncSaleOrder异常", e);
			e.printStackTrace();
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
		}
		if("success".equals(resMap.get("result").toString())){
			prepackResponse.setResult("success");
			prepackResponse.setNote(resMap.get("msg").toString());
			// 返回order_id 和  oms_order_sn
			String wms_order_sn = resMap.get("wmsOrderSn").toString();
			String oms_task_sn = resMap.get("omsTaskSn").toString();
			prepackResponse.setOms_task_sn(oms_task_sn);
			prepackResponse.setWms_order_sn(wms_order_sn);
			prepackResponse.setErrors(null);
		}else{
			prepackResponse.setResult("failure");
			prepackResponse.setNote(resMap.get("msg").toString());
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			prepackResponse.setErrors(errorsList);
		}
		
		// 4.返回orderInfoInCreateResponse
		return prepackResponse;
		
	}

	/**
	 * 发货单同步
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/syncsaleorder", method = RequestMethod.POST)
	@ResponseBody
	public Object syncsaleorder(HttpServletRequest request){
		logger.info("Enter the api of syncsaleorder...");
		
		SyncSaleOrderResponse syncSaleOrderResponse = new SyncSaleOrderResponse();
		SyncSaleOrderRequest orderInfoRequest = null;
		
		String data = (String) request.getAttribute("data");
		String appKey = (String) request.getAttribute("app_key");
		
		
		// 1.检查app_key生成customer_id...
		Integer customerId = orderInfoApiBiz.getcustomerIdByAppKey(appKey);
		
		// 2.将request转换成对象
		try {
			orderInfoRequest = (SyncSaleOrderRequest) JacksonJsonUtil.jsonToBean(data, SyncSaleOrderRequest.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			//System.out.println(errorStr.substring(0, errorStr.indexOf("(")));
			
			logger.error("syncSaleOrderResponse error:", e);
			syncSaleOrderResponse.setResult("failure");
			syncSaleOrderResponse.setNote("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			syncSaleOrderResponse.setErrors(errorsList);
			return syncSaleOrderResponse;
		}
		
		// 3.调用API Biz层发货单同步方法
		HashMap<String,Object> resMap = new HashMap<String, Object>();
		try {   
			resMap = orderInfoApiBiz.syncSaleOrder(orderInfoRequest,customerId);
		}catch (Exception e) {
			logger.error("syncSaleOrder异常", e);
			e.printStackTrace();
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
		}
		if("success".equals(resMap.get("result").toString())){
			syncSaleOrderResponse.setResult("success");
			syncSaleOrderResponse.setNote("success");
			// 返回order_id 和  oms_order_sn
			String wmsOrderId = resMap.get("orderId").toString();
			String omsOrderSn = resMap.get("omsOrderSn").toString();
			syncSaleOrderResponse.setOms_order_sn(omsOrderSn);
			syncSaleOrderResponse.setWms_order_id(Integer.parseInt(wmsOrderId));
			syncSaleOrderResponse.setErrors(null);
		}else{
			syncSaleOrderResponse.setResult("failure");
			syncSaleOrderResponse.setNote(resMap.get("msg").toString());
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			syncSaleOrderResponse.setErrors(errorsList);
		}
		
		// 4.返回orderInfoInCreateResponse
		return syncSaleOrderResponse;
	}
	
	/**
	 * 销售退货单同步
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/syncrmaorder", method = RequestMethod.POST)
	@ResponseBody
	public Object syncrmaorder(HttpServletRequest request){
		logger.info("Enter the api of syncrmaorder...");
		
		SyncRmaOrderResponse syncRmaOrderResponse = new SyncRmaOrderResponse();
		SyncRmaOrderRequest syncRmaOrderRequest = null;
		
		String data = (String) request.getAttribute("data");
		String appKey = (String) request.getAttribute("app_key");
		
		
		// 1.检查app_key生成customer_id...
		Integer customerId = orderInfoApiBiz.getcustomerIdByAppKey(appKey);
		
		// 2.将request转换成对象
		try {
			syncRmaOrderRequest = (SyncRmaOrderRequest) JacksonJsonUtil.jsonToBean(data, SyncRmaOrderRequest.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			//System.out.println(errorStr.substring(0, errorStr.indexOf("(")));
			
			logger.error("syncRmaOrderResponse error:", e);
			syncRmaOrderResponse.setResult("failure");
			syncRmaOrderResponse.setNote("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			syncRmaOrderResponse.setErrors(errorsList);
			return syncRmaOrderResponse;
		}
		
		// 3.调用API Biz层销售退货单同步方法
		HashMap<String,Object> resMap = new HashMap<String, Object>();
		try {   
			resMap = orderInfoApiBiz.syncRmaOrder(syncRmaOrderRequest,customerId);
		}catch (Exception e) {
			logger.error("syncRmaOrder异常", e);
			e.printStackTrace();
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
		}
	    
		if("success".equals(resMap.get("result").toString())){
			syncRmaOrderResponse.setResult("success");
			syncRmaOrderResponse.setNote("success");
			// 返回order_id 和  oms_order_sn
			String wmsOrderId = resMap.get("orderId").toString();
			String omsOrderSn = resMap.get("omsOrderSn").toString();
			syncRmaOrderResponse.setOms_order_sn(omsOrderSn);
			syncRmaOrderResponse.setWms_order_id(Integer.parseInt(wmsOrderId));
			syncRmaOrderResponse.setErrors(null);
		}else{
			syncRmaOrderResponse.setResult("failure");
			syncRmaOrderResponse.setNote(resMap.get("msg").toString());
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			syncRmaOrderResponse.setErrors(errorsList);
		}
		
		// 4.返回orderInfoInCreateResponse
		return syncRmaOrderResponse;
	}
	
	
	
	/**
	 * 取消订单接口
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/cancelorder", method = RequestMethod.POST)
	@ResponseBody
	public Object cancelorder(HttpServletRequest request){
		
		logger.info("Enter the api of cancelorder...");
		
		CancelOrderResponse syncRmaOrderResponse = new CancelOrderResponse();
		CancelOrderRequest cancelOrderRequest = null;
		
		String data = (String) request.getAttribute("data");
		String appKey = (String) request.getAttribute("app_key");
		
		
		// 1.检查app_key生成customer_id...
		Integer customerId = orderInfoApiBiz.getcustomerIdByAppKey(appKey);
		
		// 2.将request转换成对象
		try {
			cancelOrderRequest = (CancelOrderRequest) JacksonJsonUtil.jsonToBean(data, CancelOrderRequest.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			//System.out.println(errorStr.substring(0, errorStr.indexOf("(")));
			
			logger.error("syncRmaOrderResponse error:", e);
			syncRmaOrderResponse.setResult("failure");
			syncRmaOrderResponse.setNote("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			syncRmaOrderResponse.setErrors(errorsList);
			return syncRmaOrderResponse;
		}
		
		// 3.调用API Biz层取消订单方法
		
		HashMap<String, Object> resMap = new HashMap<String, Object>();
		try {
			resMap = orderInfoApiBiz.cancelOrder(cancelOrderRequest,customerId);
		} catch (Exception e) {
			logger.error("cancelOrder异常", e);
			e.printStackTrace();
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
			
		}
		
		if("success".equals(resMap.get("result").toString())){
			syncRmaOrderResponse.setResult("success");
			syncRmaOrderResponse.setNote("success");
			syncRmaOrderResponse.setErrors(null);
		}else{
			syncRmaOrderResponse.setResult("failure");
			syncRmaOrderResponse.setNote(resMap.get("msg").toString());
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			syncRmaOrderResponse.setErrors(errorsList);
		}
		
		// 4.返回orderInfoInCreateResponse
		return syncRmaOrderResponse;
	}
	
	/**
	 * 取消预打包接口
	 * @param request
	 * data中直接放的是oms_task_sn
	 * @return
	 */
	@RequestMapping("/cancelprepackorder")
	@ResponseBody
	public Object cancelPrePackOrder(HttpServletRequest request){
		
		logger.info("start cancel prepack order");
		
		CancelOrderResponse cancelOrderResponse = new CancelOrderResponse();
		CancelOrderPrePackRequest cancelOrderRequest = null;
		
		String data = (String) request.getAttribute("data");
		logger.info("data="+data);
		Map<String, Object> resMap = new HashMap<String, Object>();
		// 2.将request转换成对象
		try {
			cancelOrderRequest = (CancelOrderPrePackRequest) JacksonJsonUtil.jsonToBean(data, CancelOrderPrePackRequest.class);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			cancelOrderResponse.setResult("failure");
			cancelOrderResponse.setNote("取消失败，Json对象转化失败");
			return cancelOrderResponse;
		}
		
		String oms_task_sn=cancelOrderRequest.getOms_task_sn();
		
		OrderPrepack orderPrepack = orderPrepackBiz.selectOrderPrepackByOmsTaskSn(oms_task_sn);
		if(WorkerUtil.isNullOrEmpty(orderPrepack)){
			cancelOrderResponse.setResult("failure");
			cancelOrderResponse.setNote("取消失败，任务在oms中的状态与wms中的状态不一致，请联系ERP");
			return cancelOrderResponse;
		}
		//未处理状态和分配失败的状态可以直接取消
		if(OrderPrepack.ORDER_PREPACK_STATUS_INIT.equals(orderPrepack.getStatus())
				|| OrderPrepack.ORDER_PREPACK_STATUS_RESERVE_FAILED.equals(orderPrepack.getStatus())){
			resMap.put("result", "success");
			resMap.put("msg", "取消成功");
			resMap.put("error_code", "000000");
		}else if(OrderPrepack.ORDER_PREPACK_STATUS_RESERVED.equals(orderPrepack.getStatus())) {  
			//已分配状态，可取消  需hchen修改
			try {
				resMap = orderInfoApiBiz.cancelOrderPrepack(orderPrepack);
				resMap.put("result", "success");
				resMap.put("msg", "取消成功");
				resMap.put("error_code", "000000");
			} catch (Exception e) {
				logger.error("cancelOrder异常", e);
				resMap.put("result", "failure");
				resMap.put("msg", e.getMessage());
				resMap.put("error_code", "410010");
			}
		}else{
			resMap.put("result", "failure");
			resMap.put("msg", "取消失败，该状态："+orderPrepack.getStatus()+"不能取消");
			resMap.put("error_code", "000001");
		}
		
		if("success".equals(resMap.get("result"))){
			orderPrepack.setStatus("CANCEL");
			orderPrepackBiz.updateStatus(orderPrepack);
			cancelOrderResponse.setResult("success");
			cancelOrderResponse.setNote(resMap.get("msg").toString());
		}
		if("failure".equals(resMap.get("result"))){
			cancelOrderResponse.setResult("failure");
			cancelOrderResponse.setNote(resMap.get("msg").toString());
		}
		return cancelOrderResponse;
	}
	
	
	/**
	 * 采购订单查询接口
	 * @author hzhang1
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getpurchaseorder", method = RequestMethod.POST)
	@ResponseBody
	public Object getPurchaseOrder(HttpServletRequest request){
		
		logger.info("Enter the api of getPurchaseOrder...");
		
		String data = (String) request.getAttribute("data");
		String appKey = (String) request.getAttribute("app_key");
		
		GetPurchaseOrderResponse getPurchaseOrderResponse = new GetPurchaseOrderResponse();
		GetPurchaseOrderRequest getPurchaseOrderRequest = new GetPurchaseOrderRequest();
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		// 1.检查app_key生成customer_id...
		Integer customerId = orderInfoApiBiz.getcustomerIdByAppKey(appKey);
				
		// 2.将request转换为对应的对象
		try {
			getPurchaseOrderRequest = (GetPurchaseOrderRequest) JacksonJsonUtil.jsonToBean(data, GetPurchaseOrderRequest.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			logger.error("getPurchaseOrder error:", e);
			getPurchaseOrderResponse.setResult("failure");
			getPurchaseOrderResponse.setNote("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			getPurchaseOrderResponse.setErrors(errorsList);
			return getPurchaseOrderResponse;
		}
		
		// 3.调用biz方法
		try {
			resMap = orderInfoApiBiz.getPurchaseOrder(getPurchaseOrderRequest,customerId);
		} catch (Exception e) {
			logger.error("getPurchaseOrder异常", e);
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
			e.printStackTrace();
		}
		
		if("success".equals(resMap.get("result").toString())){
			getPurchaseOrderResponse.setResult("success");
			getPurchaseOrderResponse.setNote("success");
			getPurchaseOrderResponse.setErrors(null);
			getPurchaseOrderResponse.setWms_order_id(resMap.get("wmsOrderId").toString());
			getPurchaseOrderResponse.setWms_order_status(resMap.get("wmsOrderStatus").toString());
			getPurchaseOrderResponse.setWarehouse_id(Integer.parseInt(resMap.get("warehouseId").toString()));
			getPurchaseOrderResponse.setOms_order_sn(resMap.get("omsOrderSn").toString());
			List<OrderGoodsResDomain> orderGoodsResDomainList = (List<OrderGoodsResDomain>) resMap.get("orderGoodsResDomainList");
			getPurchaseOrderResponse.setOrderGoodsResDomainList(orderGoodsResDomainList);
		}else{
			getPurchaseOrderResponse.setResult("failure");
			getPurchaseOrderResponse.setNote(resMap.get("msg").toString());
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			getPurchaseOrderResponse.setErrors(errorsList);
		}
		
		// 4.返回getPurchaseOrderResponse
		return getPurchaseOrderResponse;
	}
	
	/**
	 * -V订单查询接口
	 * @author hzhang1
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getvarianceorder", method = RequestMethod.POST)
	@ResponseBody
	public Object getVarianceOrder(HttpServletRequest request){
		
		logger.info("Enter the api of getVarianceOrder...");
		
		String data = (String) request.getAttribute("data");
		String appKey = (String) request.getAttribute("app_key");
		
		GetVarianceOrderResponse getVarianceOrderResponse = new GetVarianceOrderResponse();
		GetVarianceOrderRequest getVarianceOrderRequest = new GetVarianceOrderRequest();
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		// 1.检查app_key生成customer_id...
		Integer customerId = orderInfoApiBiz.getcustomerIdByAppKey(appKey);
				
		// 2.将request转换为对应的对象
		try {
			getVarianceOrderRequest = (GetVarianceOrderRequest) JacksonJsonUtil.jsonToBean(data, GetVarianceOrderRequest.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			logger.error("getVarianceOrder error:", e);
			getVarianceOrderResponse.setResult("failure");
			getVarianceOrderResponse.setNote("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			getVarianceOrderResponse.setErrors(errorsList);
			return getVarianceOrderResponse;
		}
		
		// 3.调用biz方法
		try {
			resMap = orderInfoApiBiz.getVarianceOrder(getVarianceOrderRequest,customerId);
		} catch (Exception e) {
			logger.error("getVarianceOrder异常", e);
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
			e.printStackTrace();
		}
		
		if("success".equals(resMap.get("result").toString())){
			getVarianceOrderResponse.setResult("success");
			getVarianceOrderResponse.setNote("success");
			getVarianceOrderResponse.setErrors(null);
			getVarianceOrderResponse.setWms_order_id(resMap.get("wmsOrderId").toString());
			getVarianceOrderResponse.setWms_order_status(resMap.get("wmsOrderStatus").toString());
			getVarianceOrderResponse.setWarehouse_id(Integer.parseInt(resMap.get("warehouseId").toString()));
			getVarianceOrderResponse.setOms_order_sn(resMap.get("omsOrderSn").toString());
			List<OrderGoodsResDomain> orderGoodsResDomainList = (List<OrderGoodsResDomain>) resMap.get("orderGoodsResDomainList");
			getVarianceOrderResponse.setOrderGoodsResDomainList(orderGoodsResDomainList);
		}else{
			getVarianceOrderResponse.setResult("failure");
			getVarianceOrderResponse.setNote(resMap.get("msg").toString());
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			getVarianceOrderResponse.setErrors(errorsList);
		}
		
		// 4.返回getVarianceOrderResponse
		return getVarianceOrderResponse;
	}
	
	/**
	 * 调整供价接口
	 * @author hzhang1
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/adjustprice", method = RequestMethod.POST)
	@ResponseBody
	public Object adjustPrice(HttpServletRequest request){
		
		logger.info("Enter the api of adjustPrice...");
		
		String data = (String) request.getAttribute("data");
		String appKey = (String) request.getAttribute("app_key");
		
		AdjustPriceResponse adjustPriceResponse = new AdjustPriceResponse();
		AdjustPriceRequest adjustPriceRequest = new AdjustPriceRequest();
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		// 1.检查app_key生成customer_id...
		Integer customerId = orderInfoApiBiz.getcustomerIdByAppKey(appKey);
				
		// 2.将request转换为对应的对象
		try {
			adjustPriceRequest = (AdjustPriceRequest) JacksonJsonUtil.jsonToBean(data, AdjustPriceRequest.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			logger.error("adjustprice error:", e);
			adjustPriceResponse.setResult("failure");
			adjustPriceResponse.setNote("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			adjustPriceResponse.setErrors(errorsList);
			return adjustPriceResponse;
		}
		
		// 3.调用biz方法
		try {
			resMap = orderInfoApiBiz.adjustprice(adjustPriceRequest,customerId);
		} catch (Exception e) {
			logger.error("adjustPrice异常", e);
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
			e.printStackTrace();
		}
		
		if("success".equals(resMap.get("result").toString())){
			adjustPriceResponse.setResult("success");
			adjustPriceResponse.setNote("success");
			adjustPriceResponse.setErrors(null);
			if(!WorkerUtil.isNullOrEmpty(resMap.get("adjustPriceResDomainList"))){
				List<AdjustPriceResDomain> adjustPriceResDomainList = (List<AdjustPriceResDomain>) resMap.get("adjustPriceResDomainList");
				adjustPriceResponse.setAdjustPriceResDomainList(adjustPriceResDomainList);
			}
			if(!WorkerUtil.isNullOrEmpty(resMap.get("pre_arrival_time"))
					&& !WorkerUtil.isNullOrEmpty(resMap.get("post_arrival_time"))){
				adjustPriceResponse.setPre_arrival_time( (Date)(resMap.get("pre_arrival_time")));
				adjustPriceResponse.setPost_arrival_time( (Date)(resMap.get("post_arrival_time")));
			}
			
		}else{
			adjustPriceResponse.setResult("failure");
			adjustPriceResponse.setNote(resMap.get("msg").toString());
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			adjustPriceResponse.setErrors(errorsList);
		}
		
		// 4.返回adjustPriceResponse
		return adjustPriceResponse;
	}
	
	
	/**
	 * 销售退货订单查询接口
	 * @author qyyao
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getrmaorder", method = RequestMethod.POST)
	@ResponseBody
	public Object getRmaOrder(HttpServletRequest request){
		
		logger.info("Enter the api of getrmaorder...");
		
		String data = (String) request.getAttribute("data");
		String appKey = (String) request.getAttribute("app_key");
		
		GetRmaOrderResponse getRmaOrderResponse = new GetRmaOrderResponse();
		GetRmaOrderRequest getRmaOrderRequest = new GetRmaOrderRequest();
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		// 1.检查app_key生成customer_id...
		Integer customerId = orderInfoApiBiz.getcustomerIdByAppKey(appKey);
				
		// 2.将request转换为对应的对象
		try {
			getRmaOrderRequest = (GetRmaOrderRequest) JacksonJsonUtil.jsonToBean(data, GetRmaOrderRequest.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			logger.error("getRmaOrder error:", e);
			getRmaOrderResponse.setResult("failure");
			getRmaOrderResponse.setNote("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			getRmaOrderResponse.setErrors(errorsList);
			return getRmaOrderResponse;
		}
		
		// 3.调用biz方法
		try {
			resMap = orderInfoApiBiz.getRmaOrder(getRmaOrderRequest,customerId);
		} catch (Exception e) {
			logger.error("getRmaOrder异常", e);
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
			e.printStackTrace();
		}
		
		if("success".equals(resMap.get("result").toString())){
			getRmaOrderResponse.setResult("success");
			getRmaOrderResponse.setNote("success");
			getRmaOrderResponse.setErrors(null);
			getRmaOrderResponse.setWms_order_id((Integer)resMap.get("wmsOrderId"));
			getRmaOrderResponse.setWms_order_status( (String)resMap.get("wmsOrderStatus") );
			getRmaOrderResponse.setWarehouse_id((Integer)(resMap.get("warehouseId")));
			getRmaOrderResponse.setOms_order_sn((String)resMap.get("omsOrderSn"));
			getRmaOrderResponse.setShipping_code((String)resMap.get("shippingCode"));
			getRmaOrderResponse.setTracking_number((String)resMap.get("trackingNumber"));
			List<OrderGoodsResDomain> orderGoodsResDomainList = (List<OrderGoodsResDomain>) resMap.get("orderGoodsResDomainList");
			getRmaOrderResponse.setOrderGoodsResDomainList(orderGoodsResDomainList);
		}else{
			getRmaOrderResponse.setResult("failure");
			getRmaOrderResponse.setNote(resMap.get("msg").toString());
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			getRmaOrderResponse.setErrors(errorsList);
		}
		
		// 4.返回getRmaOrderResponse
		return getRmaOrderResponse;
	}
	
	
	/**
	 * 发货单查询接口
	 * @author qyyao
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getsaleorder", method = RequestMethod.POST)
	@ResponseBody
	public Object getSaleOrder(HttpServletRequest request){
		
		logger.info("Enter the api of getsaleorder...");
		
		String data = (String) request.getAttribute("data");
		String appKey = (String) request.getAttribute("app_key");
		
		GetSaleOrderResponse getSaleOrderResponse = new GetSaleOrderResponse();
		GetSaleOrderRequest getSaleOrderRequest = new GetSaleOrderRequest();
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		// 1.检查app_key生成customer_id...
		Integer customerId = orderInfoApiBiz.getcustomerIdByAppKey(appKey);
				
		// 2.将request转换为对应的对象
		try {
			getSaleOrderRequest = (GetSaleOrderRequest) JacksonJsonUtil.jsonToBean(data, GetSaleOrderRequest.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			logger.error("getSaleOrder error:", e);
			getSaleOrderResponse.setResult("failure");
			getSaleOrderResponse.setNote("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			getSaleOrderResponse.setErrors(errorsList);
			return getSaleOrderResponse;
		}
		
		// 3.调用biz方法
		try {
			resMap = orderInfoApiBiz.getSaleOrder(getSaleOrderRequest,customerId);
		} catch (Exception e) {
			logger.error("getSaleOrder异常", e);
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
			e.printStackTrace();
		}
		
		if("success".equals(resMap.get("result").toString())){
			getSaleOrderResponse.setResult("success");
			getSaleOrderResponse.setNote("success");
			getSaleOrderResponse.setErrors(null);
			getSaleOrderResponse.setWms_order_id((Integer)resMap.get("wmsOrderId"));
			getSaleOrderResponse.setWms_order_status( (String)resMap.get("wmsOrderStatus") );
			getSaleOrderResponse.setWarehouse_id((Integer)(resMap.get("warehouseId")));
			getSaleOrderResponse.setOms_order_sn((String)resMap.get("omsOrderSn"));
			getSaleOrderResponse.setShipping_code((String)resMap.get("shippingCode"));
			getSaleOrderResponse.setTracking_number((String)resMap.get("trackingNumber"));
			getSaleOrderResponse.setWeight((BigDecimal) resMap.get("weight"));
			List<OrderGoodsResDomain> orderGoodsResDomainList = (List<OrderGoodsResDomain>) resMap.get("orderGoodsResDomainList");
			getSaleOrderResponse.setOrderGoodsResDomainList(orderGoodsResDomainList);
			getSaleOrderResponse.setOrderPackboxResDomainList((List<OrderPackboxResDomain>) resMap.get("orderPackboxResDomainList"));
			getSaleOrderResponse.setShipmentResDomainList((List<ShipmentResDomain>) resMap.get("shipmentResDomainList"));
			getSaleOrderResponse.setShippingCodeResDomainList((List<OmsOrderTransferCodeResDomain>) resMap.get("shippingCodeResDomainList"));
		}else{
			getSaleOrderResponse.setResult("failure");
			getSaleOrderResponse.setNote(resMap.get("msg").toString());
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			getSaleOrderResponse.setErrors(errorsList);
		}
		
		// 4.返回getSaleOrderResponse
		return getSaleOrderResponse;
	}
	
	
	/**
	 * 订单列表查询接口
	 * @author qyyao
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/getorderlist", method = RequestMethod.POST)
	@ResponseBody
	public Object getOrderList(HttpServletRequest request){
		
		logger.info("Enter the api of getorderlist...");
		
		String data = (String) request.getAttribute("data");
		String appKey = (String) request.getAttribute("app_key");
		
		GetOrderListResponse getOrderListResponse = new GetOrderListResponse();
		GetOrderListRequest getOrderListRequest = new GetOrderListRequest();
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		// 1.检查app_key生成customer_id...
		Integer customerId = orderInfoApiBiz.getcustomerIdByAppKey(appKey);
				
		// 2.将request转换为对应的对象
		try {
			getOrderListRequest = (GetOrderListRequest) JacksonJsonUtil.jsonToBean(data, GetOrderListRequest.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			logger.error("getSaleOrder error:", e);
			getOrderListResponse.setResult("failure");
			getOrderListResponse.setNote("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:"+errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			getOrderListResponse.setErrors(errorsList);
			return getOrderListResponse;
		}
		
		// 3.调用biz方法
		try {
			resMap = orderInfoApiBiz.getOrderList(getOrderListRequest,customerId);
		} catch (Exception e) {
			logger.error("getOrderList异常", e);
			e.printStackTrace();
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
		}
		
		
		
		if("success".equals(resMap.get("result").toString())){
			getOrderListResponse.setResult("success");
			getOrderListResponse.setNote("success");
			getOrderListResponse.setErrors(null);
			List<OrderInfoResDomain> orderInfoResDomainList = (List<OrderInfoResDomain>) resMap.get("orderInfoResDomainList");
			getOrderListResponse.setTotal_count( (Integer)resMap.get("total_count") );
			getOrderListResponse.setOrderInfoResDomainList(orderInfoResDomainList);
			
		}else{
			getOrderListResponse.setResult("failure");
			getOrderListResponse.setNote(resMap.get("msg").toString());
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			getOrderListResponse.setErrors(errorsList);
		}
		
		// 4.返回getSaleOrderResponse
		return getOrderListResponse;
	}
	
	/**
	 * 查询未出库的耗材的list
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getordershipmentlist", method = RequestMethod.POST)
	@ResponseBody
	public Object getOrderShipmentList(HttpServletRequest request) {
		logger.info("Enter the api of getordershipmentlist...");

		String data = (String) request.getAttribute("data");
		String appKey = (String) request.getAttribute("app_key");

		GetOrderShipmentListResponse getOrderShipmentListResponse = new GetOrderShipmentListResponse();
		GetOrderShipmentListRequest getOrderShipmentListRequest = new GetOrderShipmentListRequest();
		Map<String, Object> resMap = new HashMap<String, Object>();

		// 1.检查app_key生成customer_id...
		Integer customerId =orderInfoApiBiz.getcustomerIdByAppKey(appKey);

		// 2.将json转换为对应的对象
		try {
			getOrderShipmentListRequest = (GetOrderShipmentListRequest) JacksonJsonUtil.jsonToBean(data, GetOrderShipmentListRequest.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			logger.error("getSaleOrder error:", e);
			getOrderShipmentListResponse.setResult("failure");
			getOrderShipmentListResponse.setNote("json转换成对象失败:" + errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:" + errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			getOrderShipmentListResponse.setErrors(errorsList);
			return getOrderShipmentListResponse;
		}
		
		// 3.调用biz方法
		try {
			//获取查询响应的结果，耗材的出库列表信息
			resMap = orderInfoApiBiz.getOrderShipmentList(getOrderShipmentListRequest, customerId);
		} catch (Exception e) {
			logger.error("getOrderList异常", e);
			e.printStackTrace();
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
		}

		if ("success".equals(resMap.get("result").toString())) {
			getOrderShipmentListResponse.setResult("success");
			getOrderShipmentListResponse.setNote("success");
			getOrderShipmentListResponse.setErrors(null);
			List<OrderShipmentResDomain> orderShipmentResDomains = (List<OrderShipmentResDomain>) resMap
					.get("orderShipmentResDomains");
			getOrderShipmentListResponse.setTotal_count((Integer) resMap.get("total_count"));
			getOrderShipmentListResponse.setOrderShipmentResDomains(orderShipmentResDomains);

		} else {
			getOrderShipmentListResponse.setResult("failure");
			getOrderShipmentListResponse.setNote(resMap.get("msg").toString());
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			getOrderShipmentListResponse.setErrors(errorsList);
		}

		// 4.返回getSaleOrderResponse
		return getOrderShipmentListResponse;
	}
	
	/**
	 * @author xhchen
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/terminate", method = RequestMethod.POST)
	@ResponseBody
	public Object terminate(HttpServletRequest request){
		logger.info("Enter the api of getordershipmentlist...");

		String data = (String) request.getAttribute("data");
		String appKey = (String) request.getAttribute("app_key");

		TerminalOrderResponse terminalOrderResponse = new TerminalOrderResponse();
		TerminalOrderRequest terminalOrderRequest = new TerminalOrderRequest();
		Map<String, Object> resMap = new HashMap<String, Object>();

		// 1.检查app_key生成customer_id...
		Integer customerId =orderInfoApiBiz.getcustomerIdByAppKey(appKey);

		// 2.将json转换为对应的对象
		try {
			terminalOrderRequest = (TerminalOrderRequest) JacksonJsonUtil.jsonToBean(data, TerminalOrderRequest.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			logger.error("getSaleOrder error:", e);
			terminalOrderResponse.setResult("failure");
			terminalOrderResponse.setNote("json转换成对象失败:" + errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:" + errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			terminalOrderResponse.setErrors(errorsList);
			return terminalOrderResponse;
		}
		
		// 3.调用biz方法
		try {
			//获取查询响应的结果，耗材的出库列表信息
			resMap = orderInfoApiBiz.terminalOrder(terminalOrderRequest, customerId);
		} catch (Exception e) {
			logger.error("terminalOrder异常", e);
			e.printStackTrace();
			resMap.put("result", "failure");
			resMap.put("msg", e.getMessage());
			resMap.put("error_code", "410010");
		}

		if ("success".equals(resMap.get("result").toString())) {
			terminalOrderResponse.setResult("success");
			terminalOrderResponse.setNote("success");
			terminalOrderResponse.setErrors(null);
		} else {
			terminalOrderResponse.setResult("failure");
			terminalOrderResponse.setNote(resMap.get("msg").toString());
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode(resMap.get("error_code").toString());
			error.setErrorInfo(resMap.get("msg").toString());
			errorsList.add(error);
			terminalOrderResponse.setErrors(errorsList);
		}

		// 4.返回getSaleOrderResponse
		return terminalOrderResponse;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getprepacklist", method = RequestMethod.POST)
	@ResponseBody
	public Object getPrepackOrderList(HttpServletRequest request){
		logger.info("Enter the api of getPrepackOrderList...");

		String data = (String) request.getAttribute("data");
		String appKey = (String) request.getAttribute("app_key");

		GetOrderPrepacksRequest prepacksRequest = new GetOrderPrepacksRequest();
		GetOrderPrepacksResponse prepacksResponse = new GetOrderPrepacksResponse();
		List<OrderPrepackResDomain> prepackResDomains = new ArrayList<OrderPrepackResDomain>();
		Map<String, Object> resMap = new HashMap<String, Object>();

		// 1.检查app_key生成customer_id...
		Integer customerId =orderInfoApiBiz.getcustomerIdByAppKey(appKey);

		// 2.将json转换为对应的对象
		try {
			prepacksRequest = (GetOrderPrepacksRequest) JacksonJsonUtil.jsonToBean(data, GetOrderPrepacksRequest.class);
		} catch (Exception e) {
			logger.error(e.getMessage());
			String errorStr = e.getMessage().toString();
			logger.error("getSaleOrder error:", e);
			prepacksResponse.setResult("failure");
			prepacksResponse.setNote("json转换成对象失败:" + errorStr.substring(0, errorStr.indexOf("(")));
			List<LeqeeError> errorsList = new ArrayList<LeqeeError>();
			LeqeeError error = new LeqeeError();
			error.setErrorCode("40009");
			error.setErrorInfo("json转换成对象失败:" + errorStr.substring(0, errorStr.indexOf("(")));
			errorsList.add(error);
			prepacksResponse.setErrors(errorsList);
			return prepacksResponse;
		}
		
		try {
			List<OrderPrepack> orderPrepacks = orderInfoApiBiz.getPrepackList(prepacksRequest, customerId);
			for(OrderPrepack orderPrepack : orderPrepacks){
				OrderPrepackResDomain sDomain = new OrderPrepackResDomain();
				sDomain.setOms_task_sn(orderPrepack.getOms_task_sn());
				sDomain.setQty_actual(orderPrepack.getQty_actual());
				sDomain.setQty_need(orderPrepack.getQty_need());
				sDomain.setQty_used(orderPrepack.getQty_used());
				prepackResDomains.add(sDomain);
			}
			prepacksResponse.setResult("success");
			prepacksResponse.setNote("success");
			prepacksResponse.setErrors(null);
			prepacksResponse.setPrepackResDomains(prepackResDomains);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			prepacksResponse.setResult("failure");
			prepacksResponse.setNote("获取任务列表失败");
			prepacksResponse.setErrors(null);
		}

		// 4.返回getSaleOrderResponse
		return prepacksResponse;
	}
	
}
