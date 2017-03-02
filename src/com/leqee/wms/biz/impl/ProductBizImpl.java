package com.leqee.wms.biz.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.ProductBiz;
import com.leqee.wms.dao.InventoryDao;
import com.leqee.wms.dao.LocationDao;
import com.leqee.wms.dao.OrderPrepackDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.ProductLocationDetailDao;
import com.leqee.wms.dao.ProductPrepackageDao;
import com.leqee.wms.dao.ReplenishmentDao;
import com.leqee.wms.dao.TaskDao;
import com.leqee.wms.dao.UserActionOrderPrepackDao;
import com.leqee.wms.dao.UserActionTaskDao;
import com.leqee.wms.entity.Location;
import com.leqee.wms.entity.OrderPrepack;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.entity.ProductLocationDetail;
import com.leqee.wms.entity.Task;
import com.leqee.wms.entity.UserActionOrderPrepack;
import com.leqee.wms.entity.UserActionTask;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.SequenceUtil;
import com.leqee.wms.util.WorkerUtil;
@SuppressWarnings("unchecked")
@Service
public class ProductBizImpl implements ProductBiz {

	private Logger logger  = Logger.getLogger(ProductBizImpl.class);
	
	@Autowired
	ProductDao productDao;
	
	@Autowired
	InventoryDao inventoryDao;
	
	@Autowired
	LocationDao locationDao;
	
	@Autowired
	ReplenishmentDao replenishmentDao;
	
	@Autowired
	TaskDao taskDao;
	
	@Autowired
	UserActionTaskDao userActionTaskDao;
	
	@Autowired
	ProductLocationDao productLocationDao;
	
	@Autowired
	ProductLocationDetailDao productLocationDetailDao;
	
	@Autowired
	UserActionOrderPrepackDao userActionOrderPrepackDao;
	@Autowired
	ProductPrepackageDao productPrepackageDao;
	
	@Autowired
	OrderPrepackDao orderPrepackDao;
	
	@Override
	public String selectProductNameByBarcodeCustomer(String barcode,String customer_id){ 
		return productDao.selectProductNameByBarcodeCustomer(barcode, customer_id);
	}
	
	@Override
	public Integer selectProductIdByBarcodeCustomerId(String barcode,Integer customerId) {
		return productDao.selectProductIdByBarcodeCustomer(barcode,customerId);
	}

	@Override
	public List<Map> getProductList(Map map){
		return productDao.selectProductListByPage(map);
	}
	
	public Map updateProduct(Product product){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		int col = productDao.updateProductInfo(product);
		if(col > 0){
			returnMap.put("result", Response.SUCCESS);
			returnMap.put("note", "商品信息更新成功");
		}else{
			throw new RuntimeException("商品基本信息更新失败");
		}
		return returnMap;
	}
	
	@SuppressWarnings("rawtypes")
	private static ArrayList productArray = null;
	static{
		productArray = new ArrayList();
		productArray.add(0,"customer_id");
		productArray.add(1, "barcode");
		productArray.add(2, "product_name");
		productArray.add(3, "weight");
		productArray.add(4, "length");
		productArray.add(5, "width");
		productArray.add(6, "height");
		productArray.add(7, "spec");
		productArray.add(8, "box_pick_start_number");
		productArray.add(9, "hi");
		productArray.add(10, "ti");
		productArray.add(11, "is_maintain_guarantee");
		productArray.add(12, "receive_rule");
		productArray.add(13, "replenishment_rule");
		productArray.add(14, "stock_allocation_rule");
		productArray.add(15, "is_fragile");
		productArray.add(16, "is_three_c");
		productArray.add(17, "is_serial");
		productArray.add(18, "is_maintain_virtual_stock");
		productArray.add(19, "virtual_stock");
	}
	
	public Map uploadProduct(InputStream input,boolean isE2007,String actionUser){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Map<String,Object> customersMap = new HashMap<String,Object>();
        List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
        
        for(WarehouseCustomer wc:customers){
        	customersMap.put(wc.getName(), wc.getCustomer_id().toString());
        }
        
		Map<String,Object> returnMap = new HashMap<String,Object>();
		int count = 0;
		String msg = "";
		try {
			Workbook wb  = null; //根据文件格式(2003或者2007)来初始化
			if(isE2007)
				wb = new XSSFWorkbook(input);
			else
				wb = new HSSFWorkbook(input);
			Sheet sheet = wb.getSheetAt(0);		//获得第一个表单
			Iterator<Row> rows = sheet.rowIterator();	//获得第一个表单的迭代器
			while (rows.hasNext()) {
				Row row = rows.next();	//获得行数据
				if(row.getRowNum() == 0) 
					continue;
				//System.out.println("Row #" + row.getRowNum());	//获得行号从0开始
				Iterator<Cell> cells = row.cellIterator();	//获得第一行的迭代器
				JSONObject productJson = new JSONObject();
				while (cells.hasNext()) {
					Cell cell = cells.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					//System.out.print("Cell #" + cell.getColumnIndex()+" -  "+cell.getCellType()+" - "+cell.getStringCellValue());
					switch (cell.getCellType()) {	//根据cell中的类型来输出数据
						case HSSFCell.CELL_TYPE_STRING:
							if(cell.getColumnIndex() == 0){
								productJson.put(productArray.get(cell.getColumnIndex()), customersMap.get(cell.getStringCellValue().toString().trim()));
							}else{
								productJson.put(productArray.get(cell.getColumnIndex()), cell.getStringCellValue());
							}
							break;
						case HSSFCell.CELL_TYPE_NUMERIC:
							productJson.put(productArray.get(cell.getColumnIndex()), cell.getNumericCellValue());
							break;
						case HSSFCell.CELL_TYPE_BOOLEAN:
							productJson.put(productArray.get(cell.getColumnIndex()), cell.getBooleanCellValue());
							break;
						case HSSFCell.CELL_TYPE_FORMULA:
							productJson.put(productArray.get(cell.getColumnIndex()), cell.getStringCellValue());
							break;
						default:
							productJson.put(productArray.get(cell.getColumnIndex()), "");
							break;
					}
					
					
				}
				Product product = (Product) JSONObject.toBean(productJson,Product.class);
				if(WorkerUtil.isNullOrEmpty(product.getCustomer_id()) && WorkerUtil.isNullOrEmpty(product.getBarcode())){
					continue;
				}
				if(WorkerUtil.isNullOrEmpty(product.getCustomer_id())){
					msg += product.getBarcode()+"没有设置货主或没权限，不能更新数据";
					throw new RuntimeException("第"+(count+1)+"条数据没有设置货主，不能更新数据");
				}
				if(WorkerUtil.isNullOrEmpty(product.getBarcode())){
					msg += "没有设置商品条码，不能更新数据";
					throw new RuntimeException("第"+(count+1)+"条没有设置商品条码，不能更新数据");
				}
				productDao.updateProductInfo(product);
				//msg += "货主："+product.getCustomer_id()+",barcode："+product.getBarcode()+"更新数据成功";
				count ++;
			}
		} catch (Exception ex) {
			logger.error("UploadLocationV2 error:", ex);
			returnMap.put("result", "failure");
			returnMap.put("note", "已经导入"+count+"个商品信息，第"+(count+1)+"个错误原因"+ex.getMessage()+msg);
			returnMap.put("excel_note", "已经导入"+count+"个商品信息，第"+(count+1)+"个错误原因"+ex.getMessage()+msg);
		}
		returnMap.put("result", "success");
		returnMap.put("note", "已经导入"+count+"个商品信息"+msg);
		returnMap.put("excel_note", "已经导入"+count+"个商品信息"+msg);
		return returnMap;
	}
	
	
	public List<Map> getPrePackProductList(Map searchMap){
		List<Map> prePackProductList = productDao.selectPrePackProductListByPage(searchMap);
		List<Map> newPrePackProductList = new ArrayList<Map>();
		Integer last_product_id = 0;
		boolean flag = true;
		int count = 0;
		for(Map map : prePackProductList){
			Integer order_id = Integer.parseInt(map.get("order_id").toString());
			List<Map> tempList = productDao.selectPrePackProductByOne(order_id);
			for(Map map_ : tempList){
				Integer product_id = Integer.parseInt(map_.get("product_id").toString());
				Integer count_num = productDao.sumPrePackProductNum(product_id);
				if(count_num > 1){
					if(flag){
						map_.put("row_num", count_num);
						flag = false;
					}else{
						map_.put("row_num", 0);
					}
					count++;
				}else if(count_num == 1){
					map_.put("row_num", count_num);
					count = 0;
				}
	
				if(count == count_num){
					flag = true;
					count = 0;
				}
				newPrePackProductList.add(map_);
			}
			flag = true;
		}
		return newPrePackProductList;
	}
	
	public Map printPrePackProduct(List<String> orderIdList,String type,Integer physical_warehouse_id,String actionUser){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		
		List<Map> printPrepackGoodsList = productDao.selectPrintPrePackList(physical_warehouse_id,type,orderIdList);
		List<Map> newPrintPrepackGoodsList = new ArrayList<Map>();
		List<Map> newPrepackGoodsList = new ArrayList<Map>();
		String last_warehouse_name = "",last_prepackage_product_id = "",last_product_barcode = "",last_packbox_product_id = "",last_note = "",last_tc_name = "",last_tc_barcode = "",last_contain_way = "",last_prePackCode = "",last_name = "";
		Integer last_qty_need = 0,last_order_id = 0;
		List<Integer> orderList = new ArrayList<Integer>();
		int index = 1;
		for(Map map : printPrepackGoodsList){
			String product_barcode = map.get("barcode").toString();
			String prepackage_product_id = map.get("prepackage_product_id").toString();
			String packbox_product_id = map.get("packbox_product_id").toString();
			String tc_name = map.get("product_name").toString();
			String name = map.get("name").toString();
			String note = map.get("note").toString();
			String warehouse_name = map.get("warehouse_name").toString();
			String tc_barcode = map.get("barcode").toString();
			Integer qty_need = Integer.parseInt(map.get("qty_need").toString()) - Integer.parseInt(map.get("qty_actual").toString());
			//String contain_way = map.get("barcode2").toString()+"*"+map.get("number").toString();
			Integer order_id = Integer.parseInt(map.get("order_id").toString());
			String order_sn = WorkerUtil.isNullOrEmpty(map.get("order_sn"))?"":map.get("order_sn").toString();
			String prePackCode = "";
			if(WorkerUtil.isNullOrEmpty(order_sn)){
				if(!orderList.contains(order_id)){
					prePackCode = WorkerUtil.generatorSequence(SequenceUtil.KEY_NAME_PREPACK,"J",true);
					productDao.updatePrintPrePackStatus(physical_warehouse_id,order_id,prePackCode);
					orderList.add(order_id);
				}else{
					productDao.updatePrintPrePackStatus(physical_warehouse_id,order_id,"");
				}
			}else{
				prePackCode = order_sn;
				productDao.updatePrintPrePackStatus(physical_warehouse_id,order_id,"");
			}
			
			if("".equals(last_product_barcode)){
				newPrepackGoodsList.add(map);
				if( 1 == printPrepackGoodsList.size()){
					Map<String,Object> tempMap = new HashMap<String,Object>();
					tempMap.put("printGoodsList", newPrepackGoodsList);
					String contain_way = "";
					List<Map> tempList = productDao.selectComponentProduct(Integer.parseInt(prepackage_product_id));
					for(Map temp :tempList){
						contain_way += temp.get("barcode").toString()+"*"+temp.get("number").toString()+"+";
					}
					
					tempMap.put("contain_way", contain_way.subSequence(0, contain_way.length()-1));
					tempMap.put("tc_name", tc_name);
					tempMap.put("tc_barcode", tc_barcode);
					tempMap.put("qty_need", qty_need);
					tempMap.put("prePackCode", prePackCode);
					tempMap.put("name", name);
					tempMap.put("note", note);
					tempMap.put("warehouse_name", warehouse_name);
					Product product = productDao.selectByPrimaryKey(Integer.parseInt(packbox_product_id));
					tempMap.put("hc_barcode", WorkerUtil.isNullOrEmpty(product)?"":product.getBarcode());
					tempMap.put("hc_name", WorkerUtil.isNullOrEmpty(product)?"":product.getProduct_name());
					newPrintPrepackGoodsList.add(tempMap);
				}
			}else if(product_barcode.equals(last_product_barcode) && order_id.equals(last_order_id)){
				//contain_way = last_contain_way+"+"+contain_way;
				newPrepackGoodsList.add(map);
				if( index == printPrepackGoodsList.size()){
					Map<String,Object> tempMap = new HashMap<String,Object>();
					tempMap.put("printGoodsList", newPrepackGoodsList);
					String contain_way = "";
					List<Map> tempList = productDao.selectComponentProduct(Integer.parseInt(last_prepackage_product_id));
					for(Map temp :tempList){
						contain_way += temp.get("barcode").toString()+"*"+temp.get("number").toString()+"+";
					}
					tempMap.put("contain_way", contain_way.subSequence(0, contain_way.length()-1));
					tempMap.put("tc_name", tc_name);
					tempMap.put("tc_barcode", tc_barcode);
					tempMap.put("qty_need", qty_need);
					tempMap.put("prePackCode", prePackCode);
					tempMap.put("name", name);
					tempMap.put("note", note);
					tempMap.put("warehouse_name", warehouse_name);
					Product product = productDao.selectByPrimaryKey(Integer.parseInt(packbox_product_id));
					tempMap.put("hc_barcode", WorkerUtil.isNullOrEmpty(product)?"":product.getBarcode());
					tempMap.put("hc_name", WorkerUtil.isNullOrEmpty(product)?"":product.getProduct_name());
					newPrintPrepackGoodsList.add(tempMap);
					break;
				}
			}else if(!product_barcode.equals(last_product_barcode) || !order_id.equals(last_order_id)){
				Map<String,Object> tempMap = new HashMap<String,Object>();
				tempMap.put("printGoodsList", newPrepackGoodsList);
				String contain_way = "";
				List<Map> tempList = productDao.selectComponentProduct(Integer.parseInt(last_prepackage_product_id));
				for(Map temp :tempList){
					contain_way += temp.get("barcode").toString()+"*"+temp.get("number").toString()+"+";
				}
				tempMap.put("contain_way", contain_way.subSequence(0, contain_way.length()-1));
				tempMap.put("tc_name", last_tc_name);
				tempMap.put("tc_barcode", last_tc_barcode);
				tempMap.put("qty_need", last_qty_need);
				tempMap.put("prePackCode", last_prePackCode);
				tempMap.put("name", last_name);
				tempMap.put("note", last_note);
				tempMap.put("warehouse_name", last_warehouse_name);
				Product product = productDao.selectByPrimaryKey(Integer.parseInt(last_packbox_product_id));
				tempMap.put("hc_barcode", WorkerUtil.isNullOrEmpty(product)?"":product.getBarcode());
				tempMap.put("hc_name", WorkerUtil.isNullOrEmpty(product)?"":product.getProduct_name());
				newPrintPrepackGoodsList.add(tempMap);
				newPrepackGoodsList = new ArrayList<Map>();
				newPrepackGoodsList.add(map);
				tempMap = new HashMap<String,Object>();
				if( index == printPrepackGoodsList.size()){
					tempMap.put("printGoodsList", newPrepackGoodsList);
					contain_way = "";
					tempList = productDao.selectComponentProduct(Integer.parseInt(prepackage_product_id));
					for(Map temp :tempList){
						contain_way += temp.get("barcode").toString()+"*"+temp.get("number").toString()+"+";
					}
					tempMap.put("contain_way", contain_way.subSequence(0, contain_way.length()-1));
					tempMap.put("tc_name", tc_name);
					tempMap.put("tc_barcode", tc_barcode);
					tempMap.put("qty_need", qty_need);
					tempMap.put("prePackCode", prePackCode);
					tempMap.put("name", name);
					tempMap.put("note", note);
					tempMap.put("warehouse_name", warehouse_name);
					product = productDao.selectByPrimaryKey(Integer.parseInt(packbox_product_id));
					tempMap.put("hc_barcode", WorkerUtil.isNullOrEmpty(product)?"":product.getBarcode());
					tempMap.put("hc_name", WorkerUtil.isNullOrEmpty(product)?"":product.getProduct_name());
					newPrintPrepackGoodsList.add(tempMap);
					break;
				}
			}
			last_product_barcode = product_barcode;
			//last_contain_way = contain_way;
			last_prepackage_product_id = prepackage_product_id;
			last_tc_name = tc_name;
			last_tc_barcode = tc_barcode;
			last_qty_need = qty_need;
			last_prePackCode = prePackCode;
			last_name = name;
			last_packbox_product_id = packbox_product_id;
			last_note = note;
			last_order_id = order_id;
			last_warehouse_name = warehouse_name;
			index ++ ;
			
			UserActionOrderPrepack userActionOrder =  new UserActionOrderPrepack();
			userActionOrder.setOrder_id(order_id);
			userActionOrder.setOrder_status("IN_PROCESS");
			userActionOrder.setAction_type("PRINT_PREPACK");
			userActionOrder.setAction_note("预打包任务单打印");
			userActionOrder.setCreated_user(actionUser);
			userActionOrder.setCreated_time(new Date());
			userActionOrderPrepackDao.insert(userActionOrder);
		}
		
		
		resMap.put("printPrepackGoodsList", newPrintPrepackGoodsList);
		resMap.put("result", Response.SUCCESS);
		resMap.put("note", "打印预打包任务单成功");
		return resMap;
	}
	
	// 完结预打包任务状态
	public Map endPrePackProduct(Integer orderId,Integer physical_warehouse_id){
		Map<String,Object> resMap = new HashMap<String,Object>();
		Map<String,Object> tempMap = new HashMap<String,Object>();
		List<Map> tempList1 = productDao.selectPrePackPickTaskQuantity(physical_warehouse_id, orderId);
		List<Map> tempList2 = productDao.selectPrePackOutQuantity(physical_warehouse_id, orderId);
		
		
		if(!WorkerUtil.isNullOrEmpty(productDao.checkOrderPrepackIsGrouding(physical_warehouse_id, orderId))){
			resMap.put("result", Response.FAILURE);
			resMap.put("note", "请先把该预打包任务内的标签上架完成");
			return resMap;
		}
		OrderPrepack orderPrepack = orderPrepackDao.selectOrderPrepackByOrderIdForUpdate(orderId);
//		for(Map map1 : tempList1){
//			if(tempMap1.containsKey(map1.get("task_id")+"_"+map1.get("from_pl_id")+"_"+map1.get("product_id"))){
//				String qty = tempMap1.get(map1.get("task_id")+"_"+map1.get("from_pl_id").toString()+"_"+map1.get("product_id").toString()).toString();
//				tempMap1.put(map1.get("task_id")+"_"+map1.get("from_pl_id")+"_"+map1.get("product_id"), Integer.parseInt(map1.get("quantity").toString())+Integer.parseInt(qty));
//			}else{
//				tempMap1.put(map1.get("task_id")+"_"+map1.get("from_pl_id")+"_"+map1.get("product_id"), (map1.get("quantity")));
//			}
//		}
//		for(Map map2 : tempList2){
//			String temp = map2.get("task_id")+"_"+map2.get("from_pl_id")+"_"+map2.get("product_id");
//			Integer tempQty = Integer.parseInt(map2.get("quantity").toString());
//			Integer quantity = tempMap1.containsKey(temp)?Integer.parseInt(tempMap1.get(temp).toString()):0;
//			if(quantity !=0 && quantity > tempQty){
//				String arr [] = temp.split("_");
//				productLocationDao.updateProductLocationForAddAvailable(quantity - tempQty, Integer.parseInt(arr[1]));
//			}
//		}
		if("IN_PROCESS".equals(orderPrepack.getStatus()) || "PART_FULFILLED".equals(orderPrepack.getStatus())){
			for (Map map2 : tempList2) {  // by product_location 将已经出库的数量计入Map中
				tempMap.put(map2.get("pl_id")+"", map2.get("quantity_used"));
			}
			for (Map map1 : tempList1) {  // by product_location 进行处理
				Integer quantity1 = Integer.parseInt(map1.get("quantity_reserved").toString()); // 被预定的数量
				Integer quantity2 = tempMap.containsKey(map1.get("pl_id")+"")? Integer.parseInt(tempMap.get(map1.get("pl_id")+"").toString()):0;  // 已经出库的数量
				if(quantity1 > quantity2){
					productLocationDao.updateProductLocationForAddAvailable(quantity1 - quantity2, Integer.parseInt(map1.get("pl_id").toString()));
				}
			}
		}
		if(orderPrepack.getQty_need() >= orderPrepack.getQty_actual()){
			int col = productDao.updateOrderPrepackStatus(physical_warehouse_id, "FULFILLED", null,orderId);
			if(col <= 0){
				resMap.put("result", Response.FAILURE);
				resMap.put("note", "完结预打包任务单失败");
				throw new RuntimeException("endPrePackProduct完结预打包错误");
			}else{
				resMap.put("result", Response.SUCCESS);
				resMap.put("note", "完结预打包任务单成功");
			}
			UserActionOrderPrepack userActionOrderPrepack =  new UserActionOrderPrepack(); 
			userActionOrderPrepack.setOrder_id(orderId);
			userActionOrderPrepack.setAction_note("预打包任务单手动完结");
			userActionOrderPrepack.setOrder_status("FULFILLED");
			userActionOrderPrepack.setAction_type("FULFILLED");
			userActionOrderPrepack.setCreated_user("system");
			userActionOrderPrepack.setCreated_time(new Date());
			userActionOrderPrepackDao.insert(userActionOrderPrepack);
		}
		return resMap;
	}
	
	
	// 扫描预打包上架标签
	public Map scanPrepackGrouding(String location_barcode,Integer physical_warehouse_id){
		Map<String,Object> resMap = new HashMap<String,Object>();
		Map map = productDao.selectPrepackByLocationBarcode(physical_warehouse_id,location_barcode);
		// 推荐库位
		if(WorkerUtil.isNullOrEmpty(map)){
			resMap.put("result", "failure");
			resMap.put("note", "预打包标签搜索信息为空");
			return resMap;
		}
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("physical_warehouse_id", physical_warehouse_id);
		searchMap.put("location_type", Location.LOCATION_TYPE_PIECE_PICK);
		List<String> locationList = inventoryDao.selectLocationByEmpty(searchMap);
		if (!WorkerUtil.isNullOrEmpty(locationList)) {
			map.put("new_location_barcode", locationList.get(0).toString());
		} else {
			map.put("new_location_barcode", "空");
		}
		map.put("result", "success");
		map.put("note", "预打包标签搜索信息获取成功");
		return map;
	}
	
	// 预打包标签上架
	public Map prepackSubmitGrouding(String location_barcode,Integer physical_warehouse_id,String new_location_barcode,String actionUser){
		
		Map<String,Object> resMap = new HashMap<String,Object>();
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map map = productDao.selectPrepackByLocationBarcode(physical_warehouse_id,location_barcode);
		Integer quantity = Integer.parseInt(map.get("quantity").toString());
		Integer productId = Integer.parseInt(map.get("product_id").toString());
		Integer orderId = Integer.parseInt(map.get("order_id").toString());
		Integer plId = Integer.parseInt(map.get("pl_id").toString());
		Integer warehouseId = Integer.parseInt(map.get("warehouse_id").toString());
		String batchSn = map.get("batch_sn").toString();
		String validity_ = WorkerUtil.isNullOrEmpty(map.get("validity"))?"":map.get("validity").toString();
		String toStatusId = map.get("status").toString();
		
		Location toLocation = locationDao.selectLocationIdByLocationBarCode(physical_warehouse_id,new_location_barcode,"");
		if(WorkerUtil.isNullOrEmpty(toLocation)){
			returnMap.put("result", Response.FAILURE);
			returnMap.put("success", Boolean.FALSE);
			returnMap.put("note", "目标库位不存在");
			return returnMap;
		}
		Map<String,Object> paramsForSelectTaskMap = new HashMap<String,Object>();
		paramsForSelectTaskMap.put("locationId", toLocation.getLocation_id());
		List<Map> isMixMapList = replenishmentDao.selectLocationIsCanMix(paramsForSelectTaskMap);
		if(WorkerUtil.isNullOrEmpty(isMixMapList)){
			returnMap.put("result", Response.FAILURE);
			returnMap.put("success", Boolean.FALSE);
			returnMap.put("note", "目标库位有误");
			return returnMap;
		}else{
			boolean flag = true;
			for(Map isMixMap :isMixMapList){
				String product_id2 = WorkerUtil.isNullOrEmpty(isMixMap.get("product_id"))?"":isMixMap.get("product_id").toString();
				String validity2 = WorkerUtil.isNullOrEmpty(isMixMap.get("validity"))?"":isMixMap.get("validity").toString();
				String batchSn2 = WorkerUtil.isNullOrEmpty(isMixMap.get("batch_sn")) ? "" : isMixMap.get("batch_sn").toString();
				Integer warehouseId2 = WorkerUtil.isNullOrEmpty(isMixMap.get("warehouse_id")) ? null : Integer.parseInt(isMixMap.get("warehouse_id").toString());
				String status2 = WorkerUtil.isNullOrEmpty(isMixMap.get("status"))?"":isMixMap.get("status").toString();
				String qty_total = WorkerUtil.isNullOrEmpty(isMixMap.get("qty_total"))?"":isMixMap.get("qty_total").toString();
				if(!"".equals(product_id2) && !"0".equals(qty_total)){
					String can_mix_product = !isMixMap.containsKey("can_mix_product")?"0":isMixMap.get("can_mix_product").toString();
					String can_mix_batch = !isMixMap.containsKey("can_mix_batch")?"0":isMixMap.get("can_mix_batch").toString();
					if("0".equals(can_mix_product) && !product_id2.equals(map.get("product_id").toString())){
						returnMap.put("result", Response.FAILURE);
						returnMap.put("success", Boolean.FALSE);
						returnMap.put("note", "目标库位不允许混放商品");
						flag = false;
						break;
					}
					if ("0".equals(can_mix_batch) && productId.toString().equals(product_id2) &&
							 (!validity_.equals(validity2) || !status2.equals(toStatusId) || !batchSn2.equals(batchSn))) {
						returnMap.put("result", Response.FAILURE);
						returnMap.put("success", Boolean.FALSE);
						returnMap.put("note", "上架库位不允许混放批次");
						flag = false;
						break;
					} 
					if(product_id2.equals(map.get("product_id").toString()) && !warehouseId2.equals(warehouseId)){
						returnMap.put("result", Response.FAILURE);
						returnMap.put("success", Boolean.FALSE);
						returnMap.put("note", new_location_barcode + "库位商品渠道不同不允许存放！请重新输入！");
						flag = false;
						break;
					}
				}
			}
			
			if(Boolean.FALSE.equals(flag)){
				return returnMap;
			}
		}
		
		// 扣减上架标签上的库存
		int col = productLocationDao.updateProductLocationTotalMinus(quantity, plId);
		if(col < 1){
			throw new RuntimeException("prepackSubmitGrouding扣减预打包上架标签库存失败");
		}else{
			ProductLocationDetail productLocationDetail = new ProductLocationDetail(); 
			productLocationDetail.setPl_id(plId);
			productLocationDetail.setChange_quantity(-quantity);
			productLocationDetail.setTask_id(Integer.parseInt(map.get("task_id").toString()));
			productLocationDetail.setDescription("预打包商品从标签扣减");
			productLocationDetail.setCreated_user(actionUser);
			productLocationDetail.setLast_updated_user(actionUser);
			productLocationDetailDao.insert(productLocationDetail);
		}
		Map<String,Object> paramsForPlMap = new HashMap<String,Object>();
		List<ProductLocation> productLocationList = productLocationDao.selectProductLocationByLocation2Product(physical_warehouse_id,warehouseId, toLocation.getLocation_id(),productId,"NORMAL");
		ProductLocation productLocation = new ProductLocation();
		if(WorkerUtil.isNullOrEmpty(productLocationList)){
			productLocation.setLocation_id(toLocation.getLocation_id());
			productLocation.setProduct_id(productId);
			productLocation.setQty_total(quantity);
			productLocation.setQty_reserved(quantity);
			productLocation.setQty_available(quantity);
			productLocation.setProduct_location_status("NORMAL");
			productLocation.setQty_freeze(0);
			productLocation.setStatus("NORMAL");
			productLocation.setValidity("1970-01-01 00:00:01");
			productLocation.setBatch_sn(batchSn);
			productLocation.setWarehouse_id(warehouseId);
			productLocation.setSerial_number("");
			productLocation.setCreated_user(actionUser);
			productLocation.setLast_updated_user(actionUser);
			
			productLocationDao.insert(productLocation);
			plId = productLocation.getPl_id();
		}else{
			if(!"NORMAL".equals(productLocation.getProduct_location_status())){
				throw new RuntimeException("上架失败,原因：该库位库存不可用！");
			}
			plId = productLocationList.get(0).getPl_id();
			paramsForPlMap.put("plId", plId);
			paramsForPlMap.put("quantity", quantity);
			replenishmentDao.updateReplenishmentFromProductLocationAdd(paramsForPlMap);
		}
		
		locationDao.updateLocationNotEmptyByLocationId(toLocation.getLocation_id());
		
		ProductLocationDetail productLocationDetail = new ProductLocationDetail(); 
		productLocationDetail.setPl_id(plId);
		productLocationDetail.setChange_quantity(quantity);
		productLocationDetail.setTask_id(Integer.parseInt(map.get("task_id").toString()));
		productLocationDetail.setDescription("预打包商品上架");
		productLocationDetail.setCreated_user(actionUser);
		productLocationDetail.setLast_updated_user(actionUser);
		productLocationDetailDao.insert(productLocationDetail);
					
		Map<String,Object> paramsForUpdateMap = new HashMap<String,Object>();
		paramsForUpdateMap.put("taskId", map.get("task_id"));
		paramsForUpdateMap.put("locationId", toLocation.getLocation_id());
		paramsForUpdateMap.put("actionUser", actionUser);
		paramsForUpdateMap.put("taskStatus", Task.TASK_STATUS_FULFILLED);
		col = taskDao.updateTaskById(paramsForUpdateMap);
		if(col < 1){
			throw new RuntimeException("预打包更新任务状态失败");
		}
		boolean flag = true;
		UserActionOrderPrepack userActionOrder =  new UserActionOrderPrepack();
		OrderPrepack orderPrepack = orderPrepackDao.selectOrderPrepackByOrderIdForUpdate(orderId);
		List<Map> tempList = productDao.checkOrderPrepackIsOver(physical_warehouse_id, orderId);
		Integer sum1 = 0,sum2 = 0;
		for(Map map_ :tempList){
			
			String task_type = map_.get("task_type").toString();
			String task_status = map_.get("task_status").toString();
			if("PREPACK_PICK".equals(task_type)){
				sum1 += Integer.parseInt(map_.get("quantity").toString());
			}else if("PREPACK_PUT_AWAY".equals(task_type) && "FULFILLED".equals(task_status)){
				sum2 += Integer.parseInt(map_.get("quantity").toString());
			}
			
		}
		
		Integer count_num = productDao.sumPrePackProductNumber(orderPrepack.getPrepackage_product_id());
		if(orderPrepack.getType().equals("PACK")){
			if(!sum1.equals(sum2 * count_num)){
				col = productDao.updateOrderPrepackStatus(physical_warehouse_id, "PART_FULFILLED", null,orderId);
				userActionOrder.setOrder_status("PART_FULFILLED");
				userActionOrder.setAction_type("PART_FULFILLED");
			}else{
				col = productDao.updateOrderPrepackStatus(physical_warehouse_id, "FULFILLED", null,orderId);
				userActionOrder.setOrder_status("FULFILLED");
				userActionOrder.setAction_type("FULFILLED");
			}
		}else if(orderPrepack.getType().equals("UNPACK")){
			if(!sum2.equals(sum1*count_num)){
				col = productDao.updateOrderPrepackStatus(physical_warehouse_id, "PART_FULFILLED", null,orderId);
				userActionOrder.setOrder_status("PART_FULFILLED");
				userActionOrder.setAction_type("PART_FULFILLED");
			}else{
				col = productDao.updateOrderPrepackStatus(physical_warehouse_id, "FULFILLED", null,orderId);
				userActionOrder.setOrder_status("FULFILLED");
				userActionOrder.setAction_type("FULFILLED");
			}
		}
		
		UserActionTask userActionTask = new UserActionTask();
		userActionTask.setTask_id(Integer.parseInt(map.get("task_id").toString()));
		userActionTask.setTask_status("PREPACK_PUT_AWAY");
		userActionTask.setAction_type("PREPACK_PUT_AWAY");
		userActionTask.setAction_note("预打包商品上架");
		userActionTask.setCreated_time(new Date());
		userActionTask.setCreated_user(actionUser);
		userActionTaskDao.insert(userActionTask);
		
		userActionOrder.setOrder_id(orderId);
		userActionOrder.setAction_note("预打包任务单完成上架");
		userActionOrder.setCreated_user(actionUser);
		userActionOrder.setCreated_time(new Date());
		userActionOrderPrepackDao.insert(userActionOrder);
		resMap.put("result", Response.SUCCESS);
		resMap.put("note", "上架预打包任务单成功");
		return resMap;
	}

	@Override
	public List<Map>  getProductList22(Map<String, Object> searchMap) {
		return productDao.selectProductList22ByPage(searchMap);
	}
	
	@Override
	public List<Map>  getProductListExport(Map<String, Object> searchMap) {
		return productDao.selectProductListExport(searchMap);
	}
	
	@Override
	public List<Map<String,String>> selectProductsBySkuCode(String sku_code) {
		Product product = productDao.selectBySkuCode(sku_code);
		return productDao.selectProductByProductId(product.getProduct_id());
	}
}
