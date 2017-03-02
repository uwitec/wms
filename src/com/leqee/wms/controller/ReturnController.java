package com.leqee.wms.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.leqee.wms.biz.InventoryBiz;
import com.leqee.wms.biz.OrderBiz;
import com.leqee.wms.dao.OrderDao;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.page.PageParameter;
import com.leqee.wms.response.Response;
import com.leqee.wms.response.ResponseFactory;
import com.leqee.wms.util.WorkerUtil;
import com.leqee.wms.vo.ReturnAcceptVO;

/**
 * @author Jarvis
 * @version 0.1
 * @CreatedDate 2016.02.01
 * 
 * */
@Controller
@RequestMapping(value="/return")  //指定根路径
public class ReturnController {
	private Logger logger = Logger.getLogger(ReturnController.class);
	
	@Autowired
	OrderBiz orderBiz;
	@Autowired
	InventoryBiz inventoryBiz;
	
	@Autowired
	OrderDao orderDao;
	@InitBinder
	protected void initBinder(WebDataBinder binder){
	binder.setAutoGrowNestedPaths(true);
	binder.setAutoGrowCollectionLimit(10000);
	} 
	
	/**
	 * @author Jarvis
	 * @throws UnsupportedEncodingException 
	 * @CreatedDate 2016.02.02
	 * 
	 * @Description 退货入库页面数据展示
	 * 
	 * */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/accept")
	public String accept(HttpServletRequest req, HashMap<String,Object> model) throws UnsupportedEncodingException {
		
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		//用于限制不限的货主
		List<WarehouseCustomer> warehouseCustomerList = (List<WarehouseCustomer>)session.getAttribute("userCustomers");
		String customerString ="(";
		for (WarehouseCustomer warehouseCustomer : warehouseCustomerList) {
			customerString  = customerString+warehouseCustomer.getCustomer_id()+",";
		}
		customerString = customerString.substring(0, customerString.length()-1);
		customerString  = customerString+")";
		logger.info(customerString);
		
		List<Integer> orderIdList  =new ArrayList<Integer>();
		List<Integer> orderIdList1  =new ArrayList<Integer>();
		List<Integer> orderGoodsIdList  =new ArrayList<Integer>();
		List<Map<String, Object>> orderInfoFulfilledList1 =new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> orderInfoNeedList =new ArrayList<Map<String,Object>>();
		
		Integer i =0;
	
		// 拥有权限的逻辑仓库列表
        List<Warehouse> userLogicWarehouses = (List<Warehouse>) session.getAttribute("userLogicWarehouses");
        List<Integer> userLogicWarehouseIds = new ArrayList<Integer>();
        if(!WorkerUtil.isNullOrEmpty(userLogicWarehouses)){
        	for (Warehouse warehouse : userLogicWarehouses) {
				userLogicWarehouseIds.add(warehouse.getWarehouse_id());
			}
        }
		
		// 初始化
		HashMap<String, Object> conditions = new HashMap<String, Object>();  // 查询条件
		HashMap<String, Object> conditionMap = new HashMap<String, Object>();  // 返回给View的查询条件封装
		int countUnfulfilledReturnOrderInfo = 0;  // 待入库退货订单数量
		
		// 获取传递过来的条件参数
		String order_status = req.getParameter("order_status");  // 订单状态
		String search_type = req.getParameter("search_type");    // 查询类型
		String search_text = req.getParameter("search_text");    // 查询字符串
		if(!WorkerUtil.isNullOrEmpty(search_text))
			search_text =  new String(search_text.getBytes("iso8859-1"), "UTF-8");
		logger.info("order_status: " + order_status + ", search_type: " + search_type + ", search_text: " + search_text);
		
		// 获取传递过来的信息参数
		String msg = "";
		if(!WorkerUtil.isNullOrEmpty(req.getParameter("msg")))
			msg =  URLDecoder.decode(req.getParameter("msg"),"UTF-8");
		
		// 封装查询条件参数
		if(!WorkerUtil.isNullOrEmpty(order_status)) {
			conditions.put("order_status", order_status);
			conditionMap.put("order_status", order_status);
		}
		// 表单查询条件
		if(!WorkerUtil.isNullOrEmpty(search_type) && !WorkerUtil.isNullOrEmpty(search_text)){
			// 条件继续返回给前端
			conditionMap.put("search_type", search_type);
			conditionMap.put("search_text", search_text);
			
			if("order_id".equals(search_type))
				conditions.put("order_id", search_text);
			else if("oms_order_sn".equals(search_type))
				conditions.put("oms_order_sn", search_text);
			else if("receive_name".equals(search_type))
				conditions.put("receive_name", search_text);
			else if("phone_number".equals(search_type))
				conditions.put("phone_number", search_text);
			else if("tms_shipping_no".equals(search_type))
				conditions.put("tms_shipping_no", search_text);
		}
		// 默认查询条件: order_status = ACCEPT
		if(WorkerUtil.isNullOrEmpty(conditions))
			conditions.put("order_status", OrderInfo.ORDER_STATUS_ACCEPT);
		
		// 数据分页
		PageParameter page = null;
		if(WorkerUtil.isNullOrEmpty(req.getParameter("currentPage"))){
			page = new PageParameter(1,5);
		}else{
			page = new PageParameter(Integer.valueOf(req.getParameter("currentPage")),Integer.valueOf(req.getParameter("pageSize")));
		}
		conditions.put("page", page);
		conditions.put("warehouseIds", userLogicWarehouseIds);
		conditions.put("customerString", customerString);
		
//		conditions.put("customer_id", currentCustomer.getCustomer_id());
		
		// 获取订单（包括订单商品）数据列表,若无任何仓库权限，直接返回空
		List<OrderInfo> orderInfoList = WorkerUtil.isNullOrEmpty(userLogicWarehouses) ? new ArrayList<OrderInfo>() : orderBiz.getReturnOrderInfoList(conditions);
		// 获取待入库退货订单数量,若无任何仓库权限，直接返回0
		countUnfulfilledReturnOrderInfo = WorkerUtil.isNullOrEmpty(userLogicWarehouses) ? 0 : orderBiz.getCountOfUnfulfilledReturnOrderInfo(userLogicWarehouseIds,customerString);
		//退货页面显示修改
	    //新显示逻辑
		Map<String, Object> tiaojianHashMap1 = new HashMap<String, Object>();
		Integer flag =0;
		String statusString  = "";
		for (OrderInfo orderInfo : orderInfoList) {
			statusString =orderInfo.getOrder_status();
			break;
		}
		if(OrderInfo.ORDER_STATUS_FULFILLED.equalsIgnoreCase(statusString)){
			for (OrderInfo orderInfo : orderInfoList) {
				orderIdList.add(orderInfo.getOrder_id());
			}
			List<Map<String, Object>> orderInfoFulfilledList  = orderDao.selectOrderGoodsV1(orderIdList,customerString);
			for (Map<String, Object> map : orderInfoFulfilledList) {

				if(i>=1){
					if(!orderIdList1.contains(Integer.parseInt(map.get("order_id").toString()))||
							!orderGoodsIdList.contains(Integer.parseInt(map.get("order_goods_id").toString()))){
						if(flag==1){
							orderInfoNeedList.add(tiaojianHashMap1);
							mergeList(orderInfoNeedList,orderInfoFulfilledList1);
							orderInfoNeedList.clear();
							flag=0;
						}else {
							orderInfoFulfilledList1.add(tiaojianHashMap1);
						}
					}else {
						flag =1;
						orderInfoNeedList.add(tiaojianHashMap1);
						if(i==orderInfoFulfilledList.size()-1){
							orderInfoNeedList.add(map);
							mergeList(orderInfoNeedList,  orderInfoFulfilledList1);	
							orderInfoNeedList.clear();
						}
					}
				}
				tiaojianHashMap1 = new HashMap<String, Object>();
				tiaojianHashMap1 =map;
				orderIdList1.add(Integer.parseInt(map.get("order_id").toString()));
				orderGoodsIdList.add(Integer.parseInt(map.get("order_goods_id").toString()));
				i++;
				if(flag==0){
					if(i==orderInfoFulfilledList.size()){
						orderInfoFulfilledList1.add(map);
					}
				}
			}
			
		}

		// 封装返回给视图的数据
		model.put("msg", msg);
		model.put("orderInfoFulfilledList", orderInfoFulfilledList1);
		model.put("page", page);
		model.put("conditionMap", conditionMap);
		model.put("orderInfoList", orderInfoList);
		model.put("countUnfulfilledReturnOrderInfo", countUnfulfilledReturnOrderInfo);
		
		return "/return/accept";
	}
	
	/**
	 * @author Jarvis
	 * @CreatedDate 2016.02.03
	 * 
	 * @Description 退货商品扫描（ajax调用）
	 * 
	 * */
	@RequestMapping(value = "/goodScan", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> goodScan(@RequestParam(value = "key", required = false, defaultValue = "") String key, 
			@RequestParam(value = "orderId", required = false, defaultValue = "") int orderId){
		logger.info("inParam key: " + key);
		logger.info("inParam orderId: " + orderId);
		
		Map<String, Object> result = orderBiz.getReturnOrderGoodsScanResult(key, orderId);
		
		return result;
	}
	
	/**
	 * @author Jarvis
	 * @CreatedDate 2016.02.04
	 * 
	 * @Description 退货入库
	 * 
	 * @param returnGoodList a ReturnAcceptVO（自定义）: 表单传输数据封装
	 * @param orderId a String: 退货订单ID
	 * @return returnPath a String: 返回跳转路径
	 * @throws UnsupportedEncodingException 
	 * 
	 * */
	@RequestMapping(value="/inStock", method = RequestMethod.POST)
	public String inStock(ReturnAcceptVO returnAcceptVO, Map<String,Object> model) throws UnsupportedEncodingException{
		Response response = new Response();
		
		
		// 获取操作人用户名
		Subject subject = SecurityUtils.getSubject();
		String actionUser = (String)subject.getPrincipal();
		
		try {
			Integer orderId = returnAcceptVO.getOrderId();
			logger.info("退货订单orderId: " + orderId.toString());
			
			// 操作入库
			response = inventoryBiz.returnNewAcceptInventory(orderId, returnAcceptVO.mergeReturnProductAccDetails(), actionUser);
//			response = inventoryBiz.returnAcceptInventory(orderId, returnAcceptVO.mergeReturnProductAccDetails(), actionUser);
			logger.info(Response.OK.equals(response.getCode()) ? "操作退货入库成功!" : "操作退货入库失败!"+response.getMsg());
			
		} catch (Exception e) {
			response = ResponseFactory.createExceptionResponse("退货入库失败!" + e.getMessage());
			logger.error("退货入库时发生异常，异常信息：" + e.getMessage(), e);
		}
		
//		model.put("code", response.getCode());
		model.put("msg", URLEncoder.encode(response.getMsg(), "UTF-8"));
		return "redirect:/return/accept";
	}
	
	
	
	public void mergeList (List<Map<String, Object>> orderInfoNeedList,List<Map<String, Object>> orderInfoFulfilledList1){
		StringBuffer stringValidity =new StringBuffer();
	    StringBuffer stringBatchSn =new StringBuffer();
	    StringBuffer stringStatus =new StringBuffer();
	    Map<String, Object> tiaojianHashMap = new HashMap<String, Object>();
		Map<String, Object> validityMap = new HashMap<String, Object>();
		 Map<String, Object> batchMap = new HashMap<String, Object>();
		 Map<String, Object> statusMap = new HashMap<String, Object>();
		for (Map<String, Object> map1 : orderInfoNeedList) {
//			  validityMap = new HashMap<String, Object>();
//			  batchMap = new HashMap<String, Object>();
//			  statusMap = new HashMap<String, Object>();
			 if(WorkerUtil.isNullOrEmpty(tiaojianHashMap)){
				 tiaojianHashMap=map1;
			 }
			  
			 if (WorkerUtil.isNullOrEmpty(validityMap)) {
				 if(!WorkerUtil.isNullOrEmpty(map1.get("batch_sn").toString())){
					 batchMap.put(map1.get("batch_sn").toString(), map1.get("totalnum").toString());
				 }
				 
				 //String validityString= map1.get("validity").toString()+"*"+validityTotal;
				 statusMap.put(map1.get("status").toString(), map1.get("totalnum").toString());
				 validityMap.put(map1.get("validity").toString(), map1.get("totalnum").toString());
			}else {
				//判断生产日期
				if(validityMap.containsKey(map1.get("validity").toString())){
					//String validityString= map1.get("validity").toString()+"*"+validityTotal;
					 validityMap.put(map1.get("validity").toString(), Integer.parseInt(map1.get("totalnum").toString())+Integer.parseInt(validityMap.get(map1.get("validity").toString()).toString()));
				}else {
					validityMap.put(map1.get("validity").toString(), map1.get("totalnum").toString());
				}
				
				//判断批次号
				if(batchMap.containsKey(map1.get("batch_sn").toString())){
					//String validityString= map1.get("validity").toString()+"*"+validityTotal;
					batchMap.put(map1.get("batch_sn").toString(), Integer.parseInt(map1.get("totalnum").toString())+Integer.parseInt(batchMap.get(map1.get("batch_sn").toString()).toString()));
				}else {
					 if(!WorkerUtil.isNullOrEmpty(map1.get("batch_sn").toString())){
						 batchMap.put(map1.get("batch_sn").toString(), map1.get("totalnum").toString());
					 }
				}
				
				//判断全新二手
				
				if(statusMap.containsKey(map1.get("status").toString())){
					//String validityString= map1.get("validity").toString()+"*"+validityTotal;
					statusMap.put(map1.get("status").toString(), Integer.parseInt(map1.get("totalnum").toString())+Integer.parseInt(statusMap.get(map1.get("status").toString()).toString()));
				}else {
					statusMap.put(map1.get("status").toString(), map1.get("totalnum").toString());
				}

			}
		}
			for(Entry<String, Object> entry: batchMap.entrySet()){ 
				stringBatchSn.append(entry.getKey()+"*"+entry.getValue()+" "); 
			} 
			for(Entry<String, Object> entry: validityMap.entrySet()){ 
				stringValidity.append(entry.getKey()+"*"+entry.getValue()+" "); 
			} 
			for(Entry<String, Object> entry: statusMap.entrySet()){ 
				stringStatus.append((entry.getKey().equals("NORMAL")?"全新":"二手")+"*"+entry.getValue()+" "); 
			} 
			tiaojianHashMap.put("stringBatchSn", WorkerUtil.isNullOrEmpty(stringBatchSn)?"":stringBatchSn);
			tiaojianHashMap.put("stringValidity", WorkerUtil.isNullOrEmpty(stringValidity) ?"":stringValidity);
			tiaojianHashMap.put("stringStatus", stringStatus);
			orderInfoFulfilledList1.add(tiaojianHashMap);
			//return orderInfoFulfilledList1;
	}
	
	

}