package com.leqee.wms.api.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leqee.oms.commons.log.LogContext;
import com.leqee.oms.commons.log.LogListener;
import com.leqee.oms.commons.log.db.SpringMethodContextAop;
import com.leqee.oms.commons.log.sal.AggregationCostLogListener;
import com.leqee.wms.api.BaseLeqeeRequest;
import com.leqee.wms.entity.Shop;

/**
 * 控制各个控制器的转发
 * @author qyyao
 * @date 2016-3-7
 * @version 1.0
 */
@Controller
@RequestMapping(value="/api")
public class ForwardService {
	private Logger logger = Logger.getLogger(ForwardService.class);
	@Autowired
	OrderInfoService orderInfoService;
	@Autowired
	InventoryService inventoryService;
	@Autowired
	ProductService productService;
	@Autowired
	RegionService regionService;
	@Autowired
	ShopService shopService;
	
	private LogListener logListener = new AggregationCostLogListener();
	
	@RequestMapping(value="/forward", method = RequestMethod.POST)
	@ResponseBody
	public Object forward(HttpServletRequest request){
		
		logger.info("Enter the api of forward...");
		
		String apiMethodName = (String) request.getAttribute("api_method_name");
		LogContext logContext = SpringMethodContextAop.startContext("ForwardService:" + apiMethodName, logListener);
		try {
			Object object = null;
			if( BaseLeqeeRequest.METHOD_ADJUST_PRICE_REQUEST.equals(apiMethodName)){
				object = orderInfoService.adjustPrice(request); 
			}
			else if(BaseLeqeeRequest.METHOD_CANCEL_ORDER_REQUEST.equals(apiMethodName)){
				object = orderInfoService.cancelorder(request); 
			}
			else if(BaseLeqeeRequest.METHOD_GET_INVENTORY_REQUEST.equals(apiMethodName)){
				object = inventoryService.getinventory(request); 
			}
			else if(BaseLeqeeRequest.METHOD_SYNC_PRODUCT_REQUEST.equals(apiMethodName)){
				object = productService.syncproduct(request); 
			}
			else if(BaseLeqeeRequest.METHOD_GET_PURCHASE_ORDER_REQUEST.equals(apiMethodName)){
				object = orderInfoService.getPurchaseOrder(request); 
			}
			else if(BaseLeqeeRequest.METHOD_GET_RMA_ORDER_REQUEST.equals(apiMethodName)){
				object = orderInfoService.getRmaOrder(request); 
			}
			else if(BaseLeqeeRequest.METHOD_GET_SALE_ORDER_REQUEST.equals(apiMethodName)){
				object = orderInfoService.getSaleOrder(request); 
			}
			else if(BaseLeqeeRequest.METHOD_GET_VARIANCE_ORDER_REQUEST.equals(apiMethodName)){
				object = orderInfoService.getVarianceOrder(request); 
			}
			else if(BaseLeqeeRequest.METHOD_SYNC_PURCHASE_ORDER_REQUEST.equals(apiMethodName)){
				object = orderInfoService.syncpurchaseorder(request); 
			}
			else if(BaseLeqeeRequest.METHOD_SYNC_RMA_ORDER_REQUEST.equals(apiMethodName)){
				object = orderInfoService.syncrmaorder(request); 
			}
			else if(BaseLeqeeRequest.METHOD_SYNC_SALE_ORDER_REQUEST.equals(apiMethodName)){
				object = orderInfoService.syncsaleorder(request); 
			}
			else if(BaseLeqeeRequest.METHOD_SYNC_VARIANCE_ORDER_REQUEST.equals(apiMethodName)){
				object = orderInfoService.syncvarianceorder(request); 
			}
			else if(BaseLeqeeRequest.METHOD_GET_ORDER_LIST_REQUEST.equals(apiMethodName)){
				object = orderInfoService.getOrderList(request); 
			}
			else if(BaseLeqeeRequest.METHOD_GET_ORDER_SHIPMENT_LIST_REQUEST.equals(apiMethodName)){
				object = orderInfoService.getOrderShipmentList(request);
			}
			else if(BaseLeqeeRequest.METHOD_SYNC_REGION_REQUEST.equals(apiMethodName)){
				object = regionService.syncRegion(request);
			}
			else if(BaseLeqeeRequest.METHOD_SYNC_SHOP_REQUEST.equals(apiMethodName)){
				object = shopService.syncShop(request);
			}
			else if(BaseLeqeeRequest.METHOD_TERMINAL_ORDER_REQUEST.equals(apiMethodName)){
				object = orderInfoService.terminate(request);
			}
			else if(BaseLeqeeRequest.METHOD_GET_FROZEN_REQUEST.equals(apiMethodName)){
				object = inventoryService.getfrozen(request);
			}
			else if(BaseLeqeeRequest.METHOD_GET_VARIANCE_IMPROVE_TASK_LIST_REQUEST.equals(apiMethodName)){
				object = inventoryService.getvarianceimprovetasklist(request);
			}else if(BaseLeqeeRequest.METHOD_CANCEL_ORDERPREPACK_REQUEST.equals(apiMethodName)){
				object = orderInfoService.cancelPrePackOrder(request);
			}else if(BaseLeqeeRequest.METHOD_SYNC_ORDERPREPACK_REQUEST.equals(apiMethodName)){
				object = orderInfoService.syncPrepackOrder(request);
			}else if(BaseLeqeeRequest.METHOD_GET_ORDERPREPACK_REQUEST.equals(apiMethodName)){
				object = orderInfoService.getPrepackOrderList(request);
			}else {
				logger.info("illegal request api method name " + apiMethodName );
				// TODO 需要去初始化一个object
			}
			
			// 4.返回getInventoryResponse
			return object;
		} finally {
			SpringMethodContextAop.endContext(logContext);
		}
	}
	
	
	
	
	
}
