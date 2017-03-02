package com.leqee.wms.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.api.util.Constants;
import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.api.util.MailUtil;
import com.leqee.wms.biz.SysUserBiz;
import com.leqee.wms.dao.ConfigMailDao;
import com.leqee.wms.dao.ExpressDao;
import com.leqee.wms.dao.InventoryDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.ReportDao;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.SaleItems;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.response.Response;
import com.leqee.wms.response.ResponseFactory;
import com.leqee.wms.service.CommonScheduleService;
import com.leqee.wms.service.ReportService;
import com.leqee.wms.service.SlaveReportService;
import com.leqee.wms.util.PoiExcelUtil;
import com.leqee.wms.util.WorkerUtil;

@Service
public class SlaveReportServiceImpl extends CommonScheduleService implements
		SlaveReportService {
	private Logger logger = Logger.getLogger(SlaveReportServiceImpl.class);
	
	@Autowired
	ConfigMailDao configMailDao;
	@Autowired
	SysUserBiz sysUserBiz;
	@Autowired
	WarehouseDao warehouseDao;
		
	private ReportDao reportDaoSlave;
	
	@Resource(name = "sqlSessionSlave")
	public void setReportDaoSlave(SqlSession sqlSession) {
		this.reportDaoSlave = sqlSession.getMapper(ReportDao.class);
	}


	@Override
	public Response performanceReport(Integer physicalWarehouseId,String type) {
		// 初始化数据
		Response response = new Response();
		//报表查询日期
		String key = "";
		String startTime = "";
		String endTime = "";
		
		if("month".equalsIgnoreCase(type)){
			key = DateUtils.getMonthString(1, "MM", "")+"月";
			startTime = DateUtils.getMonthString(1, "yyyy-MM", "")+"-01 00:00:00";
			endTime = DateUtils.getMonthString(0, "yyyy-MM", "")+"-01 00:00:00";
		}else{
			key = DateUtils.getDateString(1, "yyyyMMdd", "");
			startTime = DateUtils.getDateString(1, "yyyy-MM-dd", "");
			endTime = DateUtils.getDateString(0, "yyyy-MM-dd", "");
		}
		
		// 仓库名
		String warehouseName = warehouseDao.selectByWarehouseId(physicalWarehouseId).getWarehouse_name();
		//userName_roles map
		Map<String,String> userRolesMap = new HashMap<String,String>();
		String path = this.getClass().getClassLoader().getResource("/").getPath();
		try {
			logger.info("【Leqee WMS "+warehouseName+"员工绩效报表"+key+"】 email start");
			// 拣货绩效
			List<Map<String, Object>> pickList = reportDaoSlave.getPickList(startTime,endTime,physicalWarehouseId);
			// 复核绩效
			List<Map<String, Object>> recheckList = reportDaoSlave.getRecheckList(startTime,endTime,physicalWarehouseId);
			// 补货绩效
			List<Map<String, Object>> replenishmentList = reportDaoSlave.getReplenishmentList(startTime,endTime,physicalWarehouseId);
			// 盘点绩效
			List<Map<String, Object>> countTaskList = reportDaoSlave.getCountTaskList(startTime,endTime,physicalWarehouseId);
			// 移库绩效
			List<Map<String, Object>> moveList = reportDaoSlave.getMovekList(startTime,endTime,physicalWarehouseId);
			
			MailUtil mu = new MailUtil();
			mu.setSubject("【Leqee WMS 报表】 "+warehouseName+"员工绩效报表"+key);
			StringBuilder sb = new StringBuilder();
			sb.append("<p style='font-size:20px;color:black;'>各任务绩效数据请看附件</p>");
	        Map<String, String> attachments = new HashMap<String, String>();   
			Map<String,Object> excelMap = new HashMap<String,Object>();
			excelMap.put("sheetType","reportPerformance");
			excelMap.put("sheetNum",5);
	        List<Map<String,Object>> sheetList = new ArrayList<Map<String,Object>>();
	        
	        Map<String,Object> sheetMap = new HashMap<String,Object>(); 
	        sheetMap.put("sheetName", "拣货");
	        List<Map<String,Object>> rowPickList = new ArrayList<Map<String,Object>>();
			if(!WorkerUtil.isNullOrEmpty(pickList)){
				Map<String,Object> rowHeaderMap = new HashMap<String,Object>();
				rowHeaderMap.put("type","操作");
				rowHeaderMap.put("roles","用户角色");
				rowHeaderMap.put("username","员工工号");
				rowHeaderMap.put("realname","姓名");
				rowHeaderMap.put("customerName","货主");
				rowHeaderMap.put("orderTaskNum","订单数");
				rowHeaderMap.put("skuNum","SKU商品数");
				rowHeaderMap.put("productNum","总件数");
				rowPickList.add(rowHeaderMap);
				 
				for (Map<String, Object> pickMap : pickList) {
					Map<String,Object> rowBodyMap = new HashMap<String,Object>();
					String userName = String.valueOf(pickMap.get("username"));
					String roles = getRolesByUserName(userName);
					if(WorkerUtil.isNullOrEmpty(userRolesMap) || !userRolesMap.containsKey(userName)){
						userRolesMap.put(userName, roles);
					}	
					rowBodyMap.put("type", String.valueOf(pickMap.get("type")));
					rowBodyMap.put("roles", roles);
					rowBodyMap.put("username",userName );
					rowBodyMap.put("realname", String.valueOf(pickMap.get("realname")));
					rowBodyMap.put("customerName", String.valueOf(pickMap.get("name")));
					rowBodyMap.put("orderTaskNum", String.valueOf(pickMap.get("order_num")));
					rowBodyMap.put("skuNum", String.valueOf(pickMap.get("goods_sku_num")));
					rowBodyMap.put("productNum", String.valueOf(pickMap.get("goods_num")));
					rowPickList.add(rowBodyMap);
				}
			}
			sheetMap.put("list", rowPickList);
			sheetList.add(sheetMap);
			
			sheetMap = new HashMap<String,Object>(); 
	        sheetMap.put("sheetName", "复核");
	        List<Map<String,Object>> rowRecheckList = new ArrayList<Map<String,Object>>();
			if(!WorkerUtil.isNullOrEmpty(recheckList)){
				Map<String,Object> rowHeaderMap = new HashMap<String,Object>();
				rowHeaderMap.put("type","操作");
				rowHeaderMap.put("roles","用户角色");
				rowHeaderMap.put("username","员工工号");
				rowHeaderMap.put("realname","姓名");
				rowHeaderMap.put("customerName","货主");
				rowHeaderMap.put("orderTaskNum","订单数");
				rowHeaderMap.put("skuNum","SKU商品数");
				rowHeaderMap.put("productNum","总件数");
				rowRecheckList.add(rowHeaderMap);
				 
				for (Map<String, Object> recheckMap : recheckList) {
					Map<String,Object> rowBodyMap = new HashMap<String,Object>();
					String userName = String.valueOf(recheckMap.get("username"));
					String roles = getRolesByUserName(userName);
					if(WorkerUtil.isNullOrEmpty(userRolesMap) || !userRolesMap.containsKey(userName)){
						userRolesMap.put(userName, roles);
					}	
					rowBodyMap.put("type", String.valueOf(recheckMap.get("type")));
					rowBodyMap.put("roles", roles);
					rowBodyMap.put("username",userName );
					rowBodyMap.put("realname", String.valueOf(recheckMap.get("realname")));
					rowBodyMap.put("customerName", String.valueOf(recheckMap.get("name")));
					rowBodyMap.put("orderTaskNum", String.valueOf(recheckMap.get("order_num")));
					rowBodyMap.put("skuNum", String.valueOf(recheckMap.get("goods_sku_num")));
					rowBodyMap.put("productNum", String.valueOf(recheckMap.get("goods_num")));
					rowRecheckList.add(rowBodyMap);
				}
			}
			sheetMap.put("list", rowRecheckList);
			sheetList.add(sheetMap);
			
			sheetMap = new HashMap<String,Object>(); 
	        sheetMap.put("sheetName", "补货");
	        List<Map<String,Object>> rowReplenishmentList = new ArrayList<Map<String,Object>>();
			if(!WorkerUtil.isNullOrEmpty(replenishmentList)){
				Map<String,Object> rowHeaderMap = new HashMap<String,Object>();
				rowHeaderMap.put("type","操作");
				rowHeaderMap.put("roles","用户角色");
				rowHeaderMap.put("username","员工工号");
				rowHeaderMap.put("realname","姓名");
				rowHeaderMap.put("customerName","货主");
				rowHeaderMap.put("orderTaskNum","订单数");
				rowHeaderMap.put("skuNum","SKU商品数");
				rowHeaderMap.put("productNum","总件数");
				rowReplenishmentList.add(rowHeaderMap);
				for (Map<String, Object> replenishmentMap : replenishmentList) {
					Map<String,Object> rowBodyMap = new HashMap<String,Object>();
					String userName = String.valueOf(replenishmentMap.get("username"));
					String roles = getRolesByUserName(userName);
					if(WorkerUtil.isNullOrEmpty(userRolesMap) || !userRolesMap.containsKey(userName)){
						userRolesMap.put(userName, roles);
					}	
					rowBodyMap.put("type", String.valueOf(replenishmentMap.get("type")));
					rowBodyMap.put("roles", roles);
					rowBodyMap.put("username",userName );
					rowBodyMap.put("realname", String.valueOf(replenishmentMap.get("realname")));
					rowBodyMap.put("customerName", String.valueOf(replenishmentMap.get("name")));
					rowBodyMap.put("orderTaskNum", String.valueOf(replenishmentMap.get("task_sum_num")));
					rowBodyMap.put("skuNum", String.valueOf(replenishmentMap.get("product_sku_num")));
					rowBodyMap.put("productNum", String.valueOf(replenishmentMap.get("goods_sum_num")));
					rowReplenishmentList.add(rowBodyMap);
				}
			}
			sheetMap.put("list", rowReplenishmentList);
			sheetList.add(sheetMap);
			
			sheetMap = new HashMap<String,Object>(); 
	        sheetMap.put("sheetName", "盘点");
	        List<Map<String,Object>> rowCountTaskList = new ArrayList<Map<String,Object>>();
			if(!WorkerUtil.isNullOrEmpty(countTaskList) && Integer.parseInt(countTaskList.get(0).get("product_sku_num").toString())>0){
				Map<String,Object> rowHeaderMap = new HashMap<String,Object>();
				rowHeaderMap.put("type","操作");
				rowHeaderMap.put("roles","用户角色");
				rowHeaderMap.put("username","员工工号");
				rowHeaderMap.put("realname","姓名");
				rowHeaderMap.put("customerName","货主");
				rowHeaderMap.put("orderTaskNum","订单数");
				rowHeaderMap.put("skuNum","SKU商品数");
				rowCountTaskList.add(rowHeaderMap);
				 
				for (Map<String, Object> countTaskMap : countTaskList) {
					Map<String,Object> rowBodyMap = new HashMap<String,Object>();
					String userName = String.valueOf(countTaskMap.get("username"));
					String roles = getRolesByUserName(userName);
					if(WorkerUtil.isNullOrEmpty(userRolesMap) || !userRolesMap.containsKey(userName)){
						userRolesMap.put(userName, roles);
					}	
					rowBodyMap.put("type", String.valueOf(countTaskMap.get("type")));
					rowBodyMap.put("roles", roles);
					rowBodyMap.put("username",userName );
					rowBodyMap.put("realname", String.valueOf(countTaskMap.get("realname")));
					rowBodyMap.put("customerName", String.valueOf(countTaskMap.get("name")));
					rowBodyMap.put("orderTaskNum", String.valueOf(countTaskMap.get("task_sum_num")));
					rowBodyMap.put("skuNum", String.valueOf(countTaskMap.get("product_sku_num"))); 
					rowCountTaskList.add(rowBodyMap);
				}
			}
			sheetMap.put("list", rowCountTaskList);
			sheetList.add(sheetMap);
			
			sheetMap = new HashMap<String,Object>(); 
	        sheetMap.put("sheetName", "移库");
	        List<Map<String,Object>> rowMoveList = new ArrayList<Map<String,Object>>();
			if(!WorkerUtil.isNullOrEmpty(moveList) && Integer.parseInt(moveList.get(0).get("goods_sku_num").toString())>0){
				Map<String,Object> rowHeaderMap = new HashMap<String,Object>();
				rowHeaderMap.put("type","操作");
				rowHeaderMap.put("roles","用户角色");
				rowHeaderMap.put("username","员工工号");
				rowHeaderMap.put("realname","姓名");
				rowHeaderMap.put("customerName","货主");
				rowHeaderMap.put("orderTaskNum","订单数");
				rowHeaderMap.put("skuNum","SKU商品数");
				rowHeaderMap.put("productNum","总件数");
				rowMoveList.add(rowHeaderMap);
				 
				for (Map<String, Object> moveMap : moveList) {
					Map<String,Object> rowBodyMap = new HashMap<String,Object>();
					String userName = String.valueOf(moveMap.get("username"));
					String roles = getRolesByUserName(userName);
					if(WorkerUtil.isNullOrEmpty(userRolesMap) || !userRolesMap.containsKey(userName)){
						userRolesMap.put(userName, roles);
					}	
					rowBodyMap.put("type", String.valueOf(moveMap.get("type")));
					rowBodyMap.put("roles", roles);
					rowBodyMap.put("username",userName );
					rowBodyMap.put("realname", String.valueOf(moveMap.get("realname")));
					rowBodyMap.put("customerName", String.valueOf(moveMap.get("name")));
					rowBodyMap.put("orderTaskNum", String.valueOf(moveMap.get("task_num")));
					rowBodyMap.put("skuNum", String.valueOf(moveMap.get("goods_sku_num")));
					rowBodyMap.put("productNum", String.valueOf(moveMap.get("goods_num")));
					rowMoveList.add(rowBodyMap);
				}
			}
			sheetMap.put("list", rowMoveList);
			sheetList.add(sheetMap);
			excelMap.put("sheetList",sheetList);
			
			PoiExcelUtil.createxlsExcel(excelMap, path + "../../upload/performance_"+physicalWarehouseId+"_"+key+".xlsx");
			attachments.put(warehouseName+"员工绩效报表"+key+".xlsx", path + "../../upload/performance_"+physicalWarehouseId+"_"+key+".xlsx");
			
			mu.setContent(sb.toString());
			mu.setAttachments(attachments); 
			String toEmails = configMailDao.getToMailsByType(
					Constants.performanceReport, physicalWarehouseId, 0);
			mu.setToEmails(toEmails);
			mu.sendEmail();
			
			logger.info("【Leqee WMS "+warehouseName+"员工绩效报表"+key+"】 email sended");
		} catch (Exception e) {
			response = ResponseFactory.createExceptionResponse("【Leqee WMS "+warehouseName+"员工绩效报表"+key+"】 email exception: " + e.getMessage());
			logger.error("【Leqee WMS "+warehouseName+"员工绩效报表"+key+"】 email exception: " + e.getMessage(),e);
		}
		return response;
	}

	
	
	private String getRolesByUserName(String userName){
		String rolesX = "";
		Set<String> roles = sysUserBiz.findRoles(userName);
		for (String role : roles) {
			rolesX = rolesX+role+",";
		}
		return rolesX;
	}



	@Override
	public Response fulfilledReplenishmentReport() {
		// 初始化数据
		Response response = new Response();
		//报表查询日期
		String key = DateUtils.getDateString(1, "yyyyMMdd", "");
		String startTime = DateUtils.getDateString(1, "yyyy-MM-dd", "");
		String endTime = DateUtils.getDateString(0, "yyyy-MM-dd", "");
		
		try {
			logger.info("【Leqee WMS 补货任务完成情况报表】 email start");
			// 获取缺货耗材清单
			List<Map<String, Object>> fulfilledReplenishmentList = reportDaoSlave.getFulfilledReplenishmentList(startTime,endTime);
			MailUtil mu = new MailUtil();
			mu.setSubject("【Leqee WMS 报表】补货任务完成情况报表"+key);
			StringBuilder sb = new StringBuilder();
			if (!WorkerUtil.isNullOrEmpty(fulfilledReplenishmentList)) {
				// 遍历清单构建邮件展示页面
				sb.append("<table border=\"1\"><thead><tr><th style=\"width:10%\">物理仓</th><th style=\"width:15%\">货主</th><th style=\"width:15%\">补货类型</th><th style=\"width:10%\">完成任务数</th><th style=\"width:30%\">补货总件数</th></tr></thead><tbody>");
				for (Map<String, Object> fulfilledReplenishmentItem : fulfilledReplenishmentList) {
					Integer taskLevel = Integer.parseInt(String.valueOf(fulfilledReplenishmentItem.get("task_level")));
					sb.append("<tr><td>" + fulfilledReplenishmentItem.get("warehouse_name")+ "</td>");
					sb.append("<td>" + fulfilledReplenishmentItem.get("name")+ "</td>");
					sb.append("<td>" + (taskLevel.equals(1)?"一般补货":"紧急补货")+ "</td>");
					sb.append("<td>"+ fulfilledReplenishmentItem.get("count_task")+ "</td>");
					sb.append("<td>"+ fulfilledReplenishmentItem.get("sum_num")+ "</td></tr>");
				}
				sb.append("</tbody></table>");
			}else{
				sb.append("<span>所有仓未进行任何补货操作</span>");
			}
			mu.setContent(sb.toString());

			String toEmails = configMailDao.getToMailsByType(
					Constants.fulfilledReplenishment, 0, 0);
			mu.setToEmails(toEmails);
			mu.sendEmail();
			logger.info("【Leqee WMS 补货任务完成情况报表】 email sended");
		} catch (Exception e) {
			response = ResponseFactory.createExceptionResponse("【Leqee WMS 补货任务完成情况报表】 email exception: " + e.getMessage());
			logger.error("【Leqee WMS 补货任务完成情况报表】 email exception: " + e.getMessage(),e);
		}
		
		return response;
	}


	@Override
	public Response selectStockGoodsDeleted() {
		// 初始化数据
		Response response = new Response();
		try {
			logger.info("【Leqee WMS 有库存商品被删除报警】 email start");
			// 获取缺货耗材清单
			List<Map<String, Object>> list = reportDaoSlave.selectStockGoodsDeletedList();
			if (!WorkerUtil.isNullOrEmpty(list)) {
				MailUtil mu = new MailUtil();
				mu.setSubject("【Leqee WMS 报警】有库存商品被删除报警");
				StringBuilder sb = new StringBuilder();
				// 遍历清单构建邮件展示页面
				sb.append("<table border=\"1\"><thead><tr><th style=\"width:10%\">物理仓</th><th style=\"width:10%\">货主</th>" +
						"<th style=\"width:10%\">商品类型</th><th style=\"width:10%\">商家编码</th><th style=\"width:10%\">商品条码</th>" +
						"<th style=\"width:30%\">商品名称</th><th style=\"width:10%\">商品属性</th><th style=\"width:10%\">库存数量</th></tr></thead><tbody>");
				for (Map<String, Object> map : list) {
					sb.append("<tr><td>" + map.get("warehouse_name")+ "</td>");
					sb.append("<td>" + map.get("name")+ "</td>");
					sb.append("<td>" + map.get("type")+ "</td>");
					sb.append("<td>"+ map.get("sku_code")+ "</td>");
					sb.append("<td>" + map.get("barcode")+ "</td>");
					sb.append("<td>" + map.get("product_name")+ "</td>");
					sb.append("<td>"+ map.get("p_status")+ "</td>");
					sb.append("<td>"+ map.get("total")+ "</td></tr>");
				}
				sb.append("</tbody></table>");
				mu.setContent(sb.toString());

				String toEmails = configMailDao.getToMailsByType(
						Constants.stockGoodsDeleted, 0, 0);
				if(!WorkerUtil.isNullOrEmpty(toEmails) && !"".equalsIgnoreCase(toEmails)){
					mu.setToEmails(toEmails);
					mu.sendEmail();
					logger.info("【Leqee WMS 报警】有库存商品被删除报警 email sended");
				}else{
					logger.info("【Leqee WMS 报警】有库存商品被删除报警 email send failed! toEmail is null !");
				}
			}
		} catch (Exception e) {
			response = ResponseFactory.createExceptionResponse("【Leqee WMS 报警】有库存商品被删除报警 email exception: " + e.getMessage());
			logger.error("【Leqee WMS 报警】有库存商品被删除报警  email exception: " + e.getMessage(),e);
		}
		
		return response;
	}
	
	
	
}
