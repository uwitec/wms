package com.leqee.wms.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.biz.SupplierReturnBiz;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.page.PageParameter;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.WorkerUtil;


@Controller
@RequestMapping(value="/supplierReturn")  //指定根路径
public class SupplierReturnController  {

	
	private Logger logger = Logger.getLogger(SupplierReturnController.class);
	@Autowired
	SupplierReturnBiz supplierReturnBiz;

	@SuppressWarnings("unchecked")
	@RequestMapping(value="goodsReturn")
	public String goodsReturn(HttpServletRequest request , Map<String,Object> model){
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<Warehouse> warehouse_list = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
		List<WarehouseCustomer> customer_list = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		String start_time = DateUtils.getDateString(7, "yyyy-MM-dd", "");
		String end_time = DateUtils.getDateString(0, "yyyy-MM-dd", "");
		String order_status = "RESERVED";
		
		model.put("startTime",start_time);
		model.put("endTime", end_time);
		model.put("orderStatus",order_status);
		model.put("customerList", customer_list);
		model.put("warehouseList",warehouse_list);
		// 3.返回订单
		return "/supplierReturns/search";
	}
	
	@RequestMapping(value="/supplierReturnSearch")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object supplierReturnSearch(HttpServletRequest request) {
		Map<String,Object> searchMap = new HashMap<String,Object>();
		// 1.获取从前端传来的数据
		String customerId = request.getParameter("customer_id");
		String barcode = request.getParameter("barcode");
		String orderStatus = request.getParameter("order_status");
		String startTime = request.getParameter("start_time");
		String endTime = request.getParameter("end_time");
		String omsOrderSn = request.getParameter("oms_order_sn");
		String warehouseId = request.getParameter("warehouse_id");
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		List<WarehouseCustomer> customerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
        List<Warehouse> warehouseList = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
		
		List<Integer> customerIdList = new ArrayList<Integer>();
		if(!WorkerUtil.isNullOrEmpty(customerId)){
			customerIdList.add(Integer.valueOf(customerId));
			searchMap.put("customerIdList", customerIdList);
		}else{
			for (WarehouseCustomer customer : customerList) {
	        	customerIdList.add(customer.getCustomer_id());
			}
			searchMap.put("customerIdList", customerIdList);
		}

		List<Integer> warehouseIdList = new ArrayList<Integer>();
		if(!WorkerUtil.isNullOrEmpty(warehouseId)){
			warehouseIdList.add(Integer.valueOf(warehouseId));
		}else{
			for (Warehouse warehouse : warehouseList) {
	        	warehouseIdList.add(warehouse.getWarehouse_id());
			}
		}
		searchMap.put("warehouseIdList", warehouseIdList);
		if(!WorkerUtil.isNullOrEmpty(barcode)){
			searchMap.put("barcode", barcode);
		}

		if(!WorkerUtil.isNullOrEmpty(startTime)){
			searchMap.put("startTime", startTime);
		}else if (!WorkerUtil.isNullOrEmpty(endTime)) {
			searchMap.put("endTime", endTime);
			searchMap.put("startTime", DateUtils.getDateString(10, "yyyy-MM-dd",endTime));
		}

		if(!WorkerUtil.isNullOrEmpty(orderStatus) && !"ALL".equalsIgnoreCase(orderStatus)){
			searchMap.put("orderStatus", orderStatus);
		}
		
		if(!WorkerUtil.isNullOrEmpty(omsOrderSn)){
			searchMap.put("omsOrderSn", omsOrderSn);
		}

		// 2.分页信息设置
		PageParameter page = null;
		if (WorkerUtil.isNullOrEmpty(request.getParameter("currentPage"))) {
			page = new PageParameter();
		} else {
			page = new PageParameter(Integer.valueOf(request
					.getParameter("currentPage")), Integer.valueOf(request
					.getParameter("pageSize")));
		}
		searchMap.put("page", page);
		
		List<Map<String,Object>> returnGoodsList = supplierReturnBiz.supplierReturnSearch(searchMap);
		
		searchMap.put("warehouseList",warehouseList);
		searchMap.put("customerList", customerList);
		searchMap.put("returnGoodsList", returnGoodsList);
		return searchMap;
	}
	
	
	/**
	 * 打印出库单 
	 */
	@RequestMapping(value="supplierReturnPrint")
	public String supplierReturnPrint(HttpServletRequest req, Map<String,Object> model){
		String orderIds = req.getParameter("order_ids");
		List<Map<String,Object>> goodsReturnPrintList = supplierReturnBiz.getGoodsReturnPrintList(orderIds);
		model.put("goodsReturnPrintList", goodsReturnPrintList);
		return "/supplierReturns/supplierReturnPrint";
	}
	

	
	@RequestMapping(value="/supplierReturnEdit")
	public String supplierReturnEdit(HttpServletRequest request,Map<String,Object> model){
		
		if(WorkerUtil.isNullOrEmpty(request.getParameter("order_id"))){
			model.put("result", Response.FAILURE);
			model.put("note", "没有接收到订单信息！");
			return "/supplierReturns/search";
		}
		String orderId= String.valueOf(request.getParameter("order_id"));		
		List<Map<String,Object>> goodsReturnList = supplierReturnBiz.getGoodsReturnPrintList(orderId);
		model.put("result", Response.SUCCESS);
		model.put("note", "");
		model.put("goodsReturnList", goodsReturnList);
		return "/supplierReturns/supplierReturnEdit";    
	}
	
	@RequestMapping(value="/supplierReturnOut")
	@ResponseBody
	@SuppressWarnings("unchecked")
	public Object supplierReturnOut(HttpServletRequest request){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String orderId = request.getParameter("order_id");
		String taskQty= request.getParameter("task_out_qty");
		
		try {
			returnMap = supplierReturnBiz.outGoods(orderId,taskQty);				
		} catch (Exception e) {
			returnMap.put("note", "供应商退货失败！错误信息："+e.getMessage());
			returnMap.put("result", Response.FAILURE);
		}
		return returnMap;    
	}
	
}