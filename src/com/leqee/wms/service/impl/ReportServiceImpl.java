package com.leqee.wms.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.api.util.Constants;
import com.leqee.wms.api.util.MailUtil;

import com.leqee.wms.dao.ConfigMailDao;
import com.leqee.wms.dao.ExpressDao;
import com.leqee.wms.dao.InventoryDao;
import com.leqee.wms.dao.OrderPrepackDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.OrderPrepack;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.entity.ProductPrepackage;
import com.leqee.wms.entity.SaleItems;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.response.Response;
import com.leqee.wms.response.ResponseFactory;
import com.leqee.wms.service.CommonScheduleService;
import com.leqee.wms.service.ReportService;
import com.leqee.wms.util.PoiExcelUtil;
import com.leqee.wms.util.WorkerUtil;

@Service
public class ReportServiceImpl extends CommonScheduleService implements
		ReportService {
	private Logger logger = Logger.getLogger(ReportServiceImpl.class);
	@Autowired
	WarehouseDao warehouseDao;
	private InventoryDao inventoryDaoSlave;
    @Resource(name = "sqlSessionSlave")
	public void setInventoryDaoSlave(SqlSession sqlSession) {
	  this.inventoryDaoSlave = sqlSession.getMapper(InventoryDao.class);
	}
	private ConfigMailDao configMailDaoSlave;
    @Resource(name = "sqlSessionSlave")
	public void setConfigMailDaoSlave(SqlSession sqlSession) {
	  this.configMailDaoSlave = sqlSession.getMapper(ConfigMailDao.class);
	}
	private ProductDao productDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setProductDaoSlave(SqlSession sqlSession) {
	  this.productDaoSlave = sqlSession.getMapper(ProductDao.class);
	}
	private ProductLocationDao productLocationDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setProductLocationDaoSlave(SqlSession sqlSession) {
	  this.productLocationDaoSlave = sqlSession.getMapper(ProductLocationDao.class);
	}
	private WarehouseCustomerDao warehouseCustomerDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setWarehouseCustomerDaoSlave(SqlSession sqlSession) {
	  this.warehouseCustomerDaoSlave = sqlSession.getMapper(WarehouseCustomerDao.class);
	}
	private ExpressDao expressDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setExpressDaoSlave(SqlSession sqlSession) {
	  this.expressDaoSlave = sqlSession.getMapper(ExpressDao.class);
	}
	
	private OrderPrepackDao orderPrepackDaoSlave;
	@Resource(name = "sqlSessionSlave")
	public void setOrderPrepackDaoSlave(SqlSession sqlSession) {
	  this.orderPrepackDaoSlave = sqlSession.getMapper(OrderPrepackDao.class);
	}

	@Override
	public Response packboxNotEnough() {
		// 初始化数据
		Response response = new Response();

		try {
			logger.info("【Leqee WMS 耗材库存不足报警】 email start");
			// 获取缺货耗材清单
			List<Map<String, Object>> notEnoughPackboxList = inventoryDaoSlave
					.getNotEnoughPackboxList();

			if (!WorkerUtil.isNullOrEmpty(notEnoughPackboxList)) {
				MailUtil mu = new MailUtil();
				mu.setSubject("【Leqee WMS 报警】耗材库存不足");
				StringBuilder sb = new StringBuilder();

				// 遍历清单构建邮件展示页面
				sb.append("<table border=\"1\"><thead><tr><th style=\"width:20%\">商品条码</th><th style=\"width:20%\">商品名称</th><th style=\"width:20%\">货主</th><th style=\"width:20%\">逻辑仓库</th><th style=\"width:20%\">当前缺货数量</th></tr></thead><tbody>");
				for (Map<String, Object> notEnoughPackboxItem : notEnoughPackboxList) {
					sb.append("<tr><td>" + notEnoughPackboxItem.get("barcode")
							+ "</td>");
					sb.append("<td>" + notEnoughPackboxItem.get("product_name")
							+ "</td>");
					sb.append("<td>" + notEnoughPackboxItem.get("name")
							+ "</td>");
					sb.append("<td>"
							+ notEnoughPackboxItem.get("warehouse_name")
							+ "</td>");
					sb.append("<td>" + notEnoughPackboxItem.get("lack_number")
							+ "</td></tr>");
				}
				sb.append("</tbody></table></br><p style='font-size:20px;color:red;'>库存不足，请及时处理</p>");
				mu.setContent(sb.toString());

				String toEmails = configMailDaoSlave.getToMailsByType(
						Constants.MailTypeInventeryNotEnough, 0, 0);
				mu.setToEmails(toEmails);
				mu.sendEmail();
			}
			logger.info("【Leqee WMS 耗材库存不足报警】 email sended");
		} catch (Exception e) {
			response = ResponseFactory
					.createExceptionResponse("【Leqee WMS 耗材库存不足报警】 email exception: "
							+ e.getMessage());
			logger.error(
					"【Leqee WMS 耗材库存不足报警】 email exception: " + e.getMessage(),
					e);
		}
		return response;
	}

	@Override
	public Response productSpecNull() {
		// 初始化数据
		Response response = new Response();

		try {
			logger.info("【Leqee WMS 商品未维护箱规报警】 email start");

			// 获取缺货耗材清单
			List<Product> productList = productDaoSlave.getNullSpecProductList();

			List<WarehouseCustomer> list = warehouseCustomerDaoSlave.selectAll();

			Map<String, String> map = new HashMap<String, String>();

			for (WarehouseCustomer wh : list) {
				map.put(wh.getCustomer_id() + "", wh.getName());
			}

			if (!WorkerUtil.isNullOrEmpty(productList)) {
				MailUtil mu = new MailUtil();
				mu.setSubject("【Leqee WMS 报警】商品未维护箱规");
				StringBuilder sb = new StringBuilder();

				// 遍历清单构建邮件展示页面
				sb.append("<table border=\"1\"><thead><tr><th style=\"width:20%\">商品条码</th><th style=\"width:20%\">商品名称</th><th style=\"width:20%\">货主</th></tr></thead><tbody>");
				for (Product p : productList) {
					sb.append("<td>" + p.getBarcode() + "</td>");
					sb.append("<td>" + p.getProduct_name() + "</td>");
					sb.append("<td>" + map.get(p.getCustomer_id() + "")
							+ "</td></tr>");
				}
				sb.append("</tbody></table></br><p style='font-size:20px;color:red;'>十万火急，请立刻处理</p>");
				mu.setContent(sb.toString());

				String toEmails = configMailDaoSlave.getToMailsByType(
						Constants.ProductSpecNull, 0, 0);
				mu.setToEmails(toEmails);
				mu.sendEmail();
			}
			logger.info("【Leqee WMS 商品未维护箱规报警】 email sended");
		} catch (Exception e) {
			response = ResponseFactory
					.createExceptionResponse("【Leqee WMS 商品未维护箱规报警】 email exception: "
							+ e.getMessage());
			logger.error(
					"【Leqee WMS 商品未维护箱规报警】 email exception: " + e.getMessage(),
					e);
		}
		return response;
	}

	@Override
	public Response productExceptionLocation() {
		// 初始化数据
		Response response = new Response();

		try {
			logger.info("【Leqee WMS 库位库存异常报警】 email start");

			// 获取缺货耗材清单
			List<Map> productList = productLocationDaoSlave
					.productExceptionLocation(0);

			if (!WorkerUtil.isNullOrEmpty(productList)) {
				MailUtil mu = new MailUtil();
				mu.setSubject("【Leqee WMS 报警】库位库存异常");
				StringBuilder sb = new StringBuilder();

				// 遍历清单构建邮件展示页面
				sb.append("<p style='font-size:20px;color:red;'>十万火急，请立刻处理</p>");
				sb.append("<table border=\"1\"><thead><tr><th style=\"width:10%\">物理仓</th>") ;
				sb.append("<th style=\"width:4%\">库区</th>") ;
				sb.append("<th style=\"width:4%\">货主</th>") ;
				sb.append("<th style=\"width:8%\">库位条码</th>") ;
				sb.append("<th style=\"width:30%\">商品名</th>") ;
				sb.append("<th style=\"width:10%\">商品条码</th>") ;
				sb.append("<th style=\"width:10%\">异常类型</th>") ;
				sb.append("<th style=\"width:10%\">冻结数目</th>" ) ;
				sb.append("<th style=\"width:10%\">理论总数</th>" ) ;
				sb.append("<th style=\"width:10%\">生产日期</th></tr></thead><tbody>");
				for (Map p : productList) {
					sb.append("<td>" + p.get("warehouse_name") + "</td>");
					sb.append("<td>" + p.get("location_type") + "</td>");
					sb.append("<td>" + p.get("name") + "</td>");
					sb.append("<td>" + p.get("location_barcode") + "</td>");
					sb.append("<td>" + p.get("product_name") + "</td>");
					sb.append("<td>" + p.get("barcode") + "</td>");
					sb.append("<td>" + p.get("product_location_status")
							+ "</td>");
					sb.append("<td>" + p.get("qty_freeze") + "</td>");
					sb.append("<td>" + p.get("qty_total") + "</td>");
					sb.append("<td>" + p.get("validity") + "</td></tr>");
				}
				sb.append("</tbody></table></br>");
				mu.setContent(sb.toString());

				String toEmails = configMailDaoSlave.getToMailsByType(
						Constants.productExceptionLocation, 0, 0);
				mu.setToEmails(toEmails);
				mu.sendEmail();
			}
			logger.info("【Leqee WMS 库位库存异常报警】 email sended");
		} catch (Exception e) {
			response = ResponseFactory
					.createExceptionResponse("【Leqee WMS 库位库存异常报警】 email exception: "
							+ e.getMessage());
			logger.error(
					"【Leqee WMS 库位库存异常报警】 email exception: " + e.getMessage(),
					e);
		}
		return response;
	}
	
	
	
	@Override
	public Response productExceptionLocation(Integer physcial_warehouse_id) {
		// 初始化数据
		Response response = new Response();

		try {
			logger.info("【Leqee WMS 库位库存异常报警】 email start");

			// 获取缺货耗材清单
			List<Map> productList = productLocationDaoSlave
					.productExceptionLocation(physcial_warehouse_id);

			if (!WorkerUtil.isNullOrEmpty(productList)) {
				MailUtil mu = new MailUtil();
				mu.setSubject("【Leqee WMS 报警】库位库存异常");
				StringBuilder sb = new StringBuilder();

				// 遍历清单构建邮件展示页面
				sb.append("<p style='font-size:20px;color:red;'>十万火急，请立刻处理</p>");
				sb.append("<table border=\"1\"><thead><tr><th style=\"width:10%\">物理仓</th>") ;
				sb.append("<th style=\"width:4%\">库区</th>") ;
				sb.append("<th style=\"width:4%\">货主</th>") ;
				sb.append("<th style=\"width:8%\">库位条码</th>") ;
				sb.append("<th style=\"width:30%\">商品名</th>") ;
				sb.append("<th style=\"width:10%\">商品条码</th>") ;
				sb.append("<th style=\"width:10%\">异常类型</th>") ;
				sb.append("<th style=\"width:10%\">冻结数目</th>" ) ;
				sb.append("<th style=\"width:10%\">理论总数</th>" ) ;
				sb.append("<th style=\"width:10%\">生产日期</th></tr></thead><tbody>");
				for (Map p : productList) {
					sb.append("<td>" + p.get("warehouse_name") + "</td>");
					sb.append("<td>" + p.get("location_type") + "</td>");
					sb.append("<td>" + p.get("name") + "</td>");
					sb.append("<td>" + p.get("location_barcode") + "</td>");
					sb.append("<td>" + p.get("product_name") + "</td>");
					sb.append("<td>" + p.get("barcode") + "</td>");
					sb.append("<td>" + p.get("product_location_status")
							+ "</td>");
					sb.append("<td>" + p.get("qty_freeze") + "</td>");
					sb.append("<td>" + p.get("qty_total") + "</td>");
					sb.append("<td>" + p.get("validity") + "</td></tr>");
				}
				sb.append("</tbody></table></br>");
				mu.setContent(sb.toString());

				String toEmails = configMailDaoSlave.getToMailsByType(
						Constants.productExceptionLocation, physcial_warehouse_id, 0);
				mu.setToEmails(toEmails);
				mu.sendEmail();
			}
			logger.info("【Leqee WMS 库位库存异常报警】 email sended");
		} catch (Exception e) {
			response = ResponseFactory
					.createExceptionResponse("【Leqee WMS 库位库存异常报警】 email exception: "
							+ e.getMessage());
			logger.error(
					"【Leqee WMS 库位库存异常报警】 email exception: " + e.getMessage(),
					e);
		}
		return response;
	}
	

	@Override
	public Response thermalMailnosNotEnough() {
		// 初始化数据
		Response response = new Response();

		try {
			logger.info("【Leqee WMS 快递号段不足预警】 email start");
			List<Map<String, Object>> notEnoughThermalMailnosList = expressDaoSlave.getNotEnoughThermalMailnosList();
			
			if (!WorkerUtil.isNullOrEmpty(notEnoughThermalMailnosList)) {
				MailUtil mu = new MailUtil();
				mu.setSubject("【Leqee WMS 报警】快递号段不足预警");
				StringBuilder sb = new StringBuilder();
				String shippingName = "";
				// 遍历清单构建邮件展示页面
				sb.append("<table border=\"1\"><thead><tr><th style=\"width:10%\">系统版本</th><th style=\"width:15%\">快递方式</th><th style=\"width:15%\">快递账号</th><th style=\"width:10%\">物理仓</th><th style=\"width:30%\">逻辑仓库</th><th style=\"width:12%\">剩余数量</th><th style=\"width:15%\">已使用数量</th></tr></thead><tbody>");
				for (Map<String, Object> notEnoughThermalMailnosItem : notEnoughThermalMailnosList) {
					sb.append("<tr><td>WMS</td>");
					sb.append("<td>" + notEnoughThermalMailnosItem.get("shipping_name")
							+ "</td>");
					sb.append("<td>" + notEnoughThermalMailnosItem.get("app_key")
							+ "</td>");
					sb.append("<td>" + notEnoughThermalMailnosItem.get("physical_warehouse_name")
							+ "</td>");
					sb.append("<td>"+ notEnoughThermalMailnosItem.get("group_warehouse_name")
							+ "</td>");
					sb.append("<td>"+ notEnoughThermalMailnosItem.get("unused")
							+ "</td>");
					sb.append("<td>" + notEnoughThermalMailnosItem.get("used")
							+ "</td></tr>");
					shippingName = shippingName+notEnoughThermalMailnosItem.get("shipping_name");
				}
				sb.append("</tbody></table></br><p style='font-size:20px;color:red;'>"+shippingName+"号段不足2000，请及时处理</p>");
				mu.setContent(sb.toString());

				String toEmails = configMailDaoSlave.getToMailsByType(
						Constants.thermalMailnosNotEnough, 0, 0);
				mu.setToEmails(toEmails);
				mu.sendEmail();
			}
			logger.info("【Leqee WMS 快递号段不足预警】 email sended");
		} catch (Exception e) {
			response = ResponseFactory.createExceptionResponse("【Leqee WMS 快递号段不足预警】 email exception: " + e.getMessage());
			logger.error("【Leqee WMS 快递号段不足预警】 email exception: " + e.getMessage(),e);
		}
		return response;
	}


	@Override
	public Response getSaleNums(Map<String, Object> serviceParamsMap) {
		Integer physical_warehouse_id = serviceParamsMap.get("physical_warehouse_id") == null ? 0 : Integer.parseInt(serviceParamsMap.get("physical_warehouse_id").toString()) ; //默认为0
		Integer group_id = serviceParamsMap.get("groupId") == null ? 0 : Integer.parseInt(serviceParamsMap.get("groupId").toString()) ; //默认为0
		Integer customer_id = serviceParamsMap.get("customerId") == null ? 0 : Integer.parseInt(serviceParamsMap.get("customerId").toString()); //默认为0
		Integer days = serviceParamsMap.get("days") == null ? 0 : Integer.parseInt(serviceParamsMap.get("days").toString()); //默认为0

		int day_table=days+1;
		java.util.Date now = new java.util.Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期 
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		String end=dateFormat.format(c.getTime())+" 23:59:59";
		//c.set(Calendar.DATE, c.get(Calendar.DATE)-4);
		String start=dateFormat.format(c.getTime())+" 00:00:00";
		//还原时间

		//c.set(Calendar.DATE, c.get(Calendar.DATE)-days);//修改天数  0表示今天  1表示昨天  -1表示明天  
		c.set(Calendar.DATE, c.get(Calendar.DATE)-days);
		String start2=dateFormat.format(c.getTime())+" 00:00:00";
		// 初始化数据
		Response response = new Response();
		try {
			logger.info("【Leqee WMS 销售报表】 email start");
			logger.info("physical_warehouse_id="+physical_warehouse_id+"group_id"+group_id+"customer_id"+customer_id+"days"+days);
			
			// 获取缺货多销售量，和订单数
			List<SaleItems> salesList = productDaoSlave.getSaleNums(physical_warehouse_id,group_id,customer_id,start2,end);
			
			// 获取缺货今天销售量，和订单数
			List<SaleItems> salesListNow = productDaoSlave.getSaleNums(physical_warehouse_id,group_id,customer_id,start,end);
			
			Map<String,SaleItems> map =new HashMap<String,SaleItems>();
			
			//标识今天的数据
			for(SaleItems si:salesListNow){
				map.put(si.getWarehouse_name()+"_"+si.getName()+"_"+si.getProduct_id(), si);
			}
			
			//获取今日数据的所有排序
            Collections.sort(salesListNow);
            
        	HashMap<String,List<SaleItems>> saleItemsListMap = new HashMap<String,List<SaleItems>>();
			for(int i=0;i<salesListNow.size();i++){
				SaleItems sis=salesListNow.get(i);
				sis.setSortToday(i+1);
				
				
				String key=sis.getWarehouse_name()+"_"+sis.getName();
				List<SaleItems> list=new ArrayList<SaleItems>();
				if(saleItemsListMap.get(key)!=null){
					list=saleItemsListMap.get(key);
				}
				
				list.add(sis);
				saleItemsListMap.put(key, list);
			}
			
			for(List<SaleItems> list010 :saleItemsListMap.values()){
				Collections.sort(list010);
				for(int i=0;i<list010.size();i++){
					SaleItems sis010=list010.get(i);
					sis010.setSortInCustomerToday(i+1);
					}

				}

		
			//将7天的数据丢到今天的数据中
			for(SaleItems si:salesList){
				if(map.get(si.getWarehouse_name()+"_"+si.getName()+"_"+si.getProduct_id())!=null){
					SaleItems saleItem=map.get(si.getWarehouse_name()+"_"+si.getName()+"_"+si.getProduct_id());
					saleItem.setSalesToday(saleItem.getSales());
					saleItem.setOrdersToday(saleItem.getOrders());
					saleItem.setSales(si.getSales());
					saleItem.setOrders(si.getOrders());
				}
				

			}
			
			
			if (!WorkerUtil.isNullOrEmpty(salesListNow)) {
				MailUtil mu = new MailUtil();
				mu.setSubject("【Leqee WMS 报表】 销售报表");
				StringBuilder sb = new StringBuilder();
				sb.append("<p style='font-size:20px;color:red;'>请注意查看</p>");
				// 遍历清单构建邮件展示页面
				sb.append("<table border=\"1\"><thead><tr><th style=\"width:4%\">物理仓</th>" +
						"<th style=\"width:4%\">货主</th>" +
						"<th style=\"width:14%\">商品名称</th>" +
						"<th style=\"width:7%\">商品条码</th>" +
						"<th style=\"width:7%\">"+day_table+"天内销售数量</th>" +
						"<th style=\"width:7%\">"+day_table+"天内订单数量</th>" +
						"<th style=\"width:7%\">当天销售数量</th>" +
						"<th style=\"width:7%\">当天订单数量</th>" +
						"<th style=\"width:7%\">当天全仓排名</th>" +
						"<th style=\"width:7%\">当天货主排名</th>" +
								"</tr></thead><tbody>");
				
				for(List<SaleItems> list010 :saleItemsListMap.values()){
					for(int k=0;k<list010.size()&&k<10;k++){
						SaleItems si=list010.get(k);
						sb.append("<tr><td>" + si.getWarehouse_name()
								+ "</td>");
						sb.append("<td>" + si.getName()
								+ "</td>");
						sb.append("<td>" + si.getProduct_name()
								+ "</td>");
						sb.append("<td>"+ si.getBarcode()
								+ "</td>");
						sb.append("<td>"+ si.getSales()
								+ "</td>");
						sb.append("<td>"+ si.getOrders()
								+ "</td>");
						sb.append("<td>"+ si.getSalesToday()
								+ "</td>");
						sb.append("<td>"+ si.getOrdersToday()
								+ "</td>");
						
						if(si.getSortToday()==0){
							sb.append("<td>-</td>");
						}else{
							sb.append("<td>"+ si.getSortToday()
									+ "</td>");
						}
						if(si.getSortInCustomerToday()==0){
							sb.append("<td>-</td></tr>");
						}else{
							sb.append("<td>"+ si.getSortInCustomerToday()
									+ "</td></tr>");
						}
					}
					
					
				}
				
				sb.append("</tbody></table></br><p style='font-size:20px;color:red;'></p>");
				mu.setContent(sb.toString());

				String toEmails = configMailDaoSlave.getToMailsByType(
						Constants.saleNums, physical_warehouse_id, 0);
				mu.setToEmails(toEmails);
				mu.sendEmail();
			}
			logger.info("【Leqee WMS 销售报表】 email sended");
		} catch (Exception e) {
			response = ResponseFactory.createExceptionResponse("【Leqee WMS 销售报表】 email exception: " + e.getMessage());
			logger.error("【Leqee WMS 销售报表】 email exception: " + e.getMessage(),e);
		}
		return response;

	}

	
	public Response productSumNotEnough(Integer groupId,Integer physical_warehouse_id,Integer customerId){
		// 初始化数据
				Response response = new Response();
				String con = " ";
				String con1 = " ";
				String con2 = " ";
				if(!WorkerUtil.isNullOrEmpty(groupId)){
					
					if(groupId == 0 ){  
			        }
					 else if (groupId == 1) {      //金佰利
			            	List<Integer> customerlist = new ArrayList<Integer>();
			    			customerlist = warehouseCustomerDaoSlave.selectCustomerIdListByGroupId(groupId);
			    			for (Integer customers : customerlist) {
			    				con2 += "'"+customers+"',";
							}
			    			con = " and p.customer_id in ("+con2+") ";
							con = WorkerUtil.minusComma(con);
			            	con1 = " and imd.customer_id in ("+con2+") ";
			            	con1 = WorkerUtil.minusComma(con1);
			            }
		            else if (groupId == 2) {     //雀巢  
		            	logger.info("雀巢 "); 
		            	con += " and p.customer_id in ('65553') ";
		            	con1 += " and imd.customer_id in ('65553') ";
		            }
		            else if (groupId == 3) {   	//康贝、中粮、百事、桂格
		            	logger.info("康贝、中粮、百事、桂格  "); 
		            	con += " and p.customer_id in ('65586','65625','65608','65632') ";
		            	con1 += " and imd.customer_id in ('65586','65625','65608','65632') ";
		            }
		            else if (groupId == 4) {      //除了金佰利 雀巢  康贝  中粮  百事  桂格 乐其跨境的其它组织
		            	logger.info("除了金佰利 雀巢  康贝  中粮  百事  桂格 乐其跨境的其它组织  ");
		            	con += "  and p.customer_id not in ('65558','65553','65586','65625','65608','65632','65638') ";
		            	con1 += "  and imd.customer_id not in ('65558','65553','65586','65625','65608','65632','65638') ";
		            }
		            else if (groupId == 5) {      //乐其跨境
		               logger.info("乐其跨境  ");
		               con += " and p.customer_id in ('65638') ";
		               con1 += " and imd.customer_id in ('65638') ";
		            }
		            else if (groupId == 6){
		            	logger.info("玛氏和依云");
		                con += " and p.customer_id in ('3','5') ";
		                con1 += " and imd.customer_id in ('3','5') ";
		            }
		            else if (groupId == 7){
		            	logger.info("除了测试业务组");
		                con += " and p.customer_id not in ('2') ";
		                con1 += " and imd.customer_id not in ('2') ";
		            }
				}
			  	Date startTime = new Date();
			  	List<Map<String, Object>> productSumNotEnoughList = new ArrayList<Map<String,Object>>();
				String logPrefix = "productSumNotEnough";
				logger.info(logPrefix + " service start: " + startTime);
				try {
					productSumNotEnoughList  = productLocationDaoSlave.selectNotEnoughProductItemByCustomerId(physical_warehouse_id,customerId,con,con1);
					if (!WorkerUtil.isNullOrEmpty(productSumNotEnoughList)) {
						MailUtil mu = new MailUtil();
						mu.setSubject("【productLocation库存不足报警】");
						StringBuilder sb = new StringBuilder();

						// 遍历清单构建邮件展示页面
						sb.append("<p style='font-size:20px;color:red;'>十万火急，请立刻处理</p>");
						sb.append("<table border=\"1\"><thead><tr><th style=\"width:10%\">仓库名称</th>") ;
						sb.append("<th style=\"width:4%\">货主</th>") ;
						sb.append("<th style=\"width:8%\">商品名称</th>") ;
						sb.append("<th style=\"width:8%\">商品条码</th>") ;
						sb.append("<th style=\"width:10%\">商品状态</th>") ;
						sb.append("<th style=\"width:10%\">商品总量</th>" ) ;
						for (Map p : productSumNotEnoughList) {
							sb.append("<tr><td>" + p.get("warehouse_name") + "</td>");
							sb.append("<td>" + p.get("name") + "</td>");
							sb.append("<td>" + p.get("product_name") + "</td>");
							sb.append("<td>" + p.get("barcode") + "</td>");
							sb.append("<td>" + p.get("status")+ "</td>");
							sb.append("<td>" + p.get("qty_available_sum") + "</td>");
						}
						sb.append("</tbody></table></br>");
						mu.setContent(sb.toString());

						String toEmails = configMailDaoSlave.getToMailsByType(
								Constants.productNotEnoughLocation, physical_warehouse_id, 0);
						mu.setToEmails(toEmails);
						mu.sendEmail();
					}
					logger.info("【productLocation库存不足报警】 email sended");
					
				} catch (Exception e) {
					response = ResponseFactory
							.createExceptionResponse("【productLocation库存不足报警】 email exception: "
									+ e.getMessage());
					logger.error(
							"【productLocation库存不足报警】 email exception: " + e.getMessage(),
							e);
				}
				return response;
	}
	
	public Response maintainInventoryItemAndProductLocation(Integer groupId,Integer physical_warehouse_id,Integer customerId ){
		// 初始化数据
				Response response = new Response();
				String con = " ";
				String con1 = " ";
				String con2 = " ";
				if(customerId==0){
					customerId =null;
				}
				if(!WorkerUtil.isNullOrEmpty(groupId)){
					if(groupId == 0 ){   	
			        }
		            else if (groupId == 1) {      //金佰利
		            	List<Integer> customerlist = new ArrayList<Integer>();
		    			customerlist = warehouseCustomerDaoSlave.selectCustomerIdListByGroupId(groupId);
		    			for (Integer customers : customerlist) {
		    				con2 += "'"+customers+"',";
						}
		    			con = " and p.customer_id in ("+con2+") ";
						con = WorkerUtil.minusComma(con);
		            	con1 = " and imd.customer_id in ("+con2+") ";
		            	con1 = WorkerUtil.minusComma(con1);
		            }
		            else if (groupId == 2) {     //雀巢  
		            	logger.info("雀巢 "); 
		            	con += " and p.customer_id in ('65553') ";
		            	con1 += " and imd.customer_id in ('65553') ";
		            }
		            else if (groupId == 3) {   	//康贝、中粮、百事、桂格
		            	logger.info("康贝、中粮、百事、桂格  "); 
		            	con += " and p.customer_id in ('65586','65625','65608','65632') ";
		            	con1 += " and imd.customer_id in ('65586','65625','65608','65632') ";
		            }
		            else if (groupId == 4) {      //除了金佰利 雀巢  康贝  中粮  百事  桂格 乐其跨境的其它组织
		            	logger.info("除了金佰利 雀巢  康贝  中粮  百事  桂格 乐其跨境的其它组织  ");
		            	con += "  and p.customer_id not in ('65558','65553','65586','65625','65608','65632','65638') ";
		            	con1 += "  and imd.customer_id not in ('65558','65553','65586','65625','65608','65632','65638') ";
		            }
		            else if (groupId == 5) {      //乐其跨境
		               logger.info("乐其跨境  ");
		               con += " and p.customer_id in ('65638') ";
		               con1 += " and imd.customer_id in ('65638') ";
		            }
		            else if (groupId == 6){
		            	logger.info("玛氏和依云");
		                con += " and p.customer_id in ('3','5') ";
		                con1 += " and imd.customer_id in ('3','5') ";
		            }
				}
			  	Date startTime = new Date();
			  	List<Map<String, Object>>differentItemList = new ArrayList<Map<String,Object>>();
			  	List<Map<String, Object>>finishList = new ArrayList<Map<String,Object>>();
				String logPrefix = "maintainInventoryItemAndProductLocation";
				logger.info(logPrefix + " service start: " + startTime);
				try {
					differentItemList  = inventoryDaoSlave.selectDifferentItemByCustomerId(physical_warehouse_id,customerId,con,con1);
					if(WorkerUtil.isNullOrEmpty(differentItemList)){
						response = ResponseFactory.createErrorResponse("不存在差异");
					}else {
						for (Map<String, Object> map : differentItemList) {
							List<ProductPrepackage> orderPrepacklist= orderPrepackDaoSlave.selectOrderPrepacks(Integer.parseInt(map.get("product_id").toString()));
							if(WorkerUtil.isNullOrEmpty(orderPrepacklist)){
								logger.info("该商品不存在预打包商品");
								finishList.add(map);
							}else {
								Integer qty_used_num = 0;
								Integer qty_actual_num =  0;
								for (ProductPrepackage productPrepackage : orderPrepacklist) {
									Integer changeQuantity = orderPrepackDaoSlave.selectQtyUseNumber(productPrepackage.getPrepackage_product_id(),physical_warehouse_id);
									if(WorkerUtil.isNullOrEmpty(changeQuantity)){
										changeQuantity=0;
									}
									Integer packQuantity = orderPrepackDaoSlave.selectQtyActualNumber(productPrepackage.getPrepackage_product_id(),physical_warehouse_id);
									if(WorkerUtil.isNullOrEmpty(packQuantity)){
										packQuantity=0;
									}else {
										packQuantity= packQuantity * productPrepackage.getNumber();
									}
									Integer unpackQuantity = orderPrepackDaoSlave.selectUnpackNumber(productPrepackage.getPrepackage_product_id(),physical_warehouse_id);
									if(WorkerUtil.isNullOrEmpty(unpackQuantity)){
										unpackQuantity=0;
									}else {
										unpackQuantity = unpackQuantity * productPrepackage.getNumber();
									}
									 qty_used_num = qty_used_num +changeQuantity*productPrepackage.getNumber();
									 qty_actual_num = qty_actual_num +packQuantity ;	
									 qty_actual_num = qty_actual_num -unpackQuantity;
								}
								if(qty_actual_num -qty_used_num == Integer.parseInt(map.get("diff").toString())){
									response = ResponseFactory.createErrorResponse("不存在差异");
									
								}else {
									map.put("diff", qty_actual_num -qty_used_num-Integer.parseInt(map.get("diff").toString()));
									finishList.add(map);
								}
							}
							}	
					if(!WorkerUtil.isNullOrEmpty(finishList)){
						MailUtil mu = new MailUtil();
						mu.setSubject("【Leqee WMS 报警】库存异常");
						StringBuilder sb = new StringBuilder();

						// 遍历清单构建邮件展示页面
						sb.append("<p style='font-size:20px;color:red;'>十万火急，请立刻处理</p>");
						sb.append("<table border=\"1\"><thead><tr><th style=\"width:10%\">仓库名称</th>") ;
						sb.append("<th style=\"width:4%\">货主</th>") ;
						sb.append("<th style=\"width:8%\">商品条码</th>") ;
						sb.append("<th style=\"width:8%\">商品名称</th>") ;
						sb.append("<th style=\"width:30%\">erp库存</th>") ;
						sb.append("<th style=\"width:10%\">商品状态</th>") ;
						sb.append("<th style=\"width:10%\">wms库存</th>") ;
						sb.append("<th style=\"width:10%\">差异数量</th>" ) ;
						for (Map p : finishList) {
							sb.append("<tr><td>" + p.get("warehouse_name") + "</td>");
							sb.append("<td>" + p.get("name") + "</td>");
							sb.append("<td>" + p.get("barcode") + "</td>");
							sb.append("<td>" + p.get("product_name") + "</td>");
							sb.append("<td>" + p.get("item_sum") + "</td>");
							if("NORMAL".equals(p.get("status"))){
								sb.append("<td>" + "良品"+ "</td>");	
							}else {
								sb.append("<td>" + "不良品"+ "</td>");
							}
							if(WorkerUtil.isNullOrEmpty(p.get("qty_sum"))){
								sb.append("<td>" + '0' + "</td>");
							}else {
								sb.append("<td>" + p.get("qty_sum") + "</td>");
							}
							sb.append("<td>" + p.get("diff")+ "</td></tr>");
						}
						sb.append("</tbody></table></br>");
						mu.setContent(sb.toString());

						String toEmails = configMailDaoSlave.getToMailsByType(
								Constants.quantityExceptionLocation,physical_warehouse_id, 0);
						mu.setToEmails(toEmails);
						mu.sendEmail();
					logger.info("【Leqee WMS 两套库存异常报警】 email sended");
					}
			    }
			} catch (Exception e) {
					response = ResponseFactory
							.createExceptionResponse("【Leqee WMS 两套库存异常报警】 email exception: "
									+ e.getMessage());
					logger.error(
							"【Leqee WMS 两套库存异常报警】 email exception: " + e.getMessage(),
							e);
				}
				return response;
	}
	
	
	@Override
	public Response productValidityExpiredReport(Integer groupId,Integer physical_warehouse_id,Integer customerId){
		Response response = new Response();
		String con = " ";
		String con2 = " ";
		Date date = new Date();
		Map<String, Date> dateMap =new HashMap<String, Date>();
		List<Map<String, Object>> productValidityExpiredLists = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> productValidityExpiredFinalLists = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> sheetList = new ArrayList<Map<String,Object>>();
		Map<String,Object> excelMap = new HashMap<String,Object>();
		excelMap.put("sheetType","productValidityExpired");
		excelMap.put("sheetNum",1);
		// 仓库名
		String warehouseName = warehouseDao.selectByWarehouseId(physical_warehouse_id).getWarehouse_name();
	        
		try {
			if(customerId==0){
				customerId =null;
			}
			//拼接业务组sql
			if(!WorkerUtil.isNullOrEmpty(groupId)){
				if(groupId == 0 ){}
	            else if (groupId == 1) {      //金佰利
	            	List<Integer> customerlist = new ArrayList<Integer>();
	    			customerlist = warehouseCustomerDaoSlave.selectCustomerIdListByGroupId(groupId);
	    			for (Integer customers : customerlist) {
	    				con2 += "'"+customers+"',";
					}
	    			con = " and p.customer_id in ("+con2+") ";
					con = WorkerUtil.minusComma(con);
	            	}
			}
			//查询需要预警的商品
			List<Map<String, Object>> productlists = productDaoSlave.selectProductLists(physical_warehouse_id, customerId, con);
			
			
		    for (Map<String, Object> map : productlists) {
		    	//校验参数的合法性
		    	
		    	Boolean flag = this.validateParams((Timestamp) map.get("v2"), map.get("validity_unit").toString(), 
		    			map.get("is_maintain_warranty").toString(), 
		    			WorkerUtil.isNullOrEmpty(Integer.parseInt(map.get("warranty_warning_days").toString()))?0:Integer.parseInt(map.get("warranty_warning_days").toString()),
		    			WorkerUtil.isNullOrEmpty(Integer.parseInt(map.get("warranty_unsalable_days").toString()))?0:Integer.parseInt(map.get("warranty_unsalable_days").toString()),
		    			Integer.parseInt(map.get("v1").toString()));
		    	if(false==flag){
		    		continue;
		    	}
		    	
		    	//调用WorkerUtil里面的addDatetime  计算生产日期、到期日期、以及有效期提示
		    	dateMap = WorkerUtil.checkValidityTime(map.get("v2").toString(), Integer.parseInt(map.get("v1").toString())
		    			,map.get("validity_unit").toString(), Integer.parseInt(map.get("warranty_warning_days").toString()));
//						if(dateMap.get("validityDate").compareTo(date)<0  && dateMap.get("dateExpired").compareTo(date)>0 ){
//								  map.put("validity", WorkerUtil.formatDatetime(dateMap.get("date1")));
//								  productValidityExpiredLists.add(map);
//						}
		    	//计算到期日期与保质期比例
					//判断效期提示状态
						if(dateMap.get("validityDate").compareTo(date)<0  && dateMap.get("dateExpired").compareTo(date)<0 ){
							 map.put("chinese_validity_status", "已过期");
							 map.put("validity", WorkerUtil.formatDatetime(dateMap.get("dateExpired")));
							 productValidityExpiredLists.add(map);
						}else if (dateMap.get("validityDate").compareTo(date)<0  &&
								(dateMap.get("dateoneOfFourthExpired").compareTo(date)<0 && dateMap.get("dateExpired").compareTo(date)>0)) {
							map.put("chinese_validity_status", "临期1/4");
							map.put("validity", WorkerUtil.formatDatetime(dateMap.get("dateExpired")));
							productValidityExpiredLists.add(map);
						}else if (dateMap.get("validityDate").compareTo(date)<0  &&
								(dateMap.get("dateoneOfThreeExpiredrightNow").compareTo(date)<0 && dateMap.get("dateExpired").compareTo(date)>0)) {
							map.put("chinese_validity_status", "临期1/3");
							map.put("validity", WorkerUtil.formatDatetime(dateMap.get("dateExpired")));
							productValidityExpiredLists.add(map);
						}else if (dateMap.get("validityDate").compareTo(date)<0  &&
								(dateMap.get("dateoneMonthExpired").compareTo(date)<0 && dateMap.get("dateExpired").compareTo(date)>0)) {
							map.put("chinese_validity_status", "临期1个月内");
							map.put("validity", WorkerUtil.formatDatetime(dateMap.get("dateExpired")));
							productValidityExpiredLists.add(map);
						}
						
						
//					String	validity_status = ProductLocation.checkValidityStatus(map.get("v2").toString(),
//							Integer.parseInt(map.get("v1").toString()), map.get("validity_unit").toString(),
//							Integer.parseInt(map.get("warranty_warning_days").toString()), 
//							Integer.parseInt(map.get("warranty_unsalable_days").toString()));
			}
		    
		    //组装需要报警的商品
		    Map<String,Object> sheetMap = new HashMap<String,Object>();
		    String path = this.getClass().getResource("/").toURI().getPath();
		    Map<String, String> attachments = new HashMap<String, String>();   
	        sheetMap.put("sheetName", "生产预警报表");
	        MailUtil mu = new MailUtil();
			mu.setSubject("【Leqee WMS 报警】"+warehouseName+"生产日期预警");
			StringBuilder sb = new StringBuilder();
			if(!WorkerUtil.isNullOrEmpty(productValidityExpiredLists)){
				Map<String,Object> rowHeaderMap = new HashMap<String,Object>();
				rowHeaderMap.put("warehouse_name","仓库名称");
				rowHeaderMap.put("location_barcode","库存条码");
				rowHeaderMap.put("name","货主");
				rowHeaderMap.put("barcode","商品条码");
				rowHeaderMap.put("product_name","商品名称");
				rowHeaderMap.put("qty_total","数量");
				rowHeaderMap.put("batch_sn","批次号");
				rowHeaderMap.put("v2","生产日期");
				rowHeaderMap.put("validity","到期日期");
				rowHeaderMap.put("validity_unit","保质期");
				rowHeaderMap.put("expired_validity","离到期日天数");
				rowHeaderMap.put("chinese_validity_status","效期提示");
				productValidityExpiredFinalLists.add(rowHeaderMap);
				 
				for (Map<String, Object> productValidityExpiredMap : productValidityExpiredLists) {
					Map<String,Object> rowBodyMap = new HashMap<String,Object>();
					
					rowBodyMap.put("warehouse_name",productValidityExpiredMap.get("warehouse_name"));
					rowBodyMap.put("location_barcode",productValidityExpiredMap.get("location_barcode"));
					rowBodyMap.put("name",productValidityExpiredMap.get("name"));
					rowBodyMap.put("barcode",productValidityExpiredMap.get("barcode"));
					rowBodyMap.put("product_name",productValidityExpiredMap.get("product_name"));
					if(WorkerUtil.isNullOrEmpty(productValidityExpiredMap.get("qty_total"))){
						rowBodyMap.put("qty_total",0);
					}else {
						rowBodyMap.put("qty_total",productValidityExpiredMap.get("qty_total"));
					}
					rowBodyMap.put("batch_sn","-");
					rowBodyMap.put("v2",productValidityExpiredMap.get("v2"));
					rowBodyMap.put("validity",productValidityExpiredMap.get("validity"));
					if ("MONTH".equals(productValidityExpiredMap.get("validity_unit"))) {
						rowBodyMap.put("validity_unit",Integer.parseInt(productValidityExpiredMap.get("v1").toString())*30);
					} else if ("DAY".equals(productValidityExpiredMap.get("validity_unit"))) {
						rowBodyMap.put("validity_unit",Integer.parseInt(productValidityExpiredMap.get("v1").toString()));
					}
					rowBodyMap.put("chinese_validity_status",productValidityExpiredMap.get("chinese_validity_status") );
					productValidityExpiredFinalLists.add(rowBodyMap);
				}
			}
			sheetMap.put("list", productValidityExpiredFinalLists);
			sheetList.add(sheetMap);
			excelMap.put("sheetList",sheetList);
			
			PoiExcelUtil.createxlsExcel(excelMap, path + "../../upload/productValidity_"+physical_warehouse_id+"_"+" "+".xlsx");
			attachments.put(warehouseName+"生产日期预警报表"+" "+".xlsx", path + "../../upload/productValidity_"+physical_warehouse_id+"_"+" "+".xlsx");
			
			mu.setContent(sb.toString());
			mu.setAttachments(attachments); 
			String toEmails = configMailDaoSlave.getToMailsByType(
					Constants.productValidityExpired,physical_warehouse_id, 0);
			mu.setToEmails(toEmails);
			mu.sendEmail();
			logger.info("【Leqee WMS 报警】生产日期预警 email sended");
			response = ResponseFactory.createOkResponse("【Leqee WMS 报警】生产日期预警 email sended");
			
		   
		} catch (Exception e) {
			response = ResponseFactory
					.createExceptionResponse("商品生产日期预警 email exception: "
							+ e.getMessage());
			logger.error(
					"商品生产日期预警 email exception: " + e.getMessage(),
					e);
		}
		
		return response;

	}
	
	
	
	private boolean validateParams(Timestamp validity, String validityUnit, String isMaintainWarranty, 
			Integer warrantyWarningDays, Integer warrantyUnsalableDays, Integer validityDays) {
		if(WorkerUtil.isNullOrEmpty(validity)) {
			return false;
		}
		if(WorkerUtil.isNullOrEmpty(validityUnit) || (!"MONTH".equals(validityUnit) && !"DAY".equals(validityUnit))) {
			return false;
		}
		if(WorkerUtil.isNullOrEmpty(isMaintainWarranty) || "N".equals(isMaintainWarranty)) {
			return false;
		}
		if(warrantyWarningDays == null || warrantyWarningDays == 0) {
			return false;
		}
		
		if(warrantyUnsalableDays == null || warrantyUnsalableDays == 0) {
			return false;
		}
		
		if(validityDays == null || validityDays == 0) {
			return false;
		}
		
		return true;
	}
	
}
