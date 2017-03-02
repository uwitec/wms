package com.leqee.wms.biz.impl;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.biz.GeneralReplenishmentBiz;
import com.leqee.wms.biz.LocationBiz;
import com.leqee.wms.biz.ReplenishmentBiz;
import com.leqee.wms.dao.BatchPickDao;
import com.leqee.wms.dao.BatchTaskDao;
import com.leqee.wms.dao.ConfigDao;
import com.leqee.wms.dao.CountTaskDao;
import com.leqee.wms.dao.InventoryDao;
import com.leqee.wms.dao.LabelReplenishmentDao;
import com.leqee.wms.dao.LocationDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.OrderPrepackDao;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.ProductLocationDetailDao;
import com.leqee.wms.dao.ReplenishmentDao;
import com.leqee.wms.dao.ReplenishmentUrgentDao;
import com.leqee.wms.dao.SysUserDao;
import com.leqee.wms.dao.TaskDao;
import com.leqee.wms.dao.TaskImproveDao;
import com.leqee.wms.dao.UserActionBatchPickDao;
import com.leqee.wms.dao.UserActionOrderDao;
import com.leqee.wms.dao.UserActionOrderPrepackDao;
import com.leqee.wms.dao.UserActionTaskDao;
import com.leqee.wms.dao.WarehouseCustomerDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.entity.BatchPick;
import com.leqee.wms.entity.BatchTask;
import com.leqee.wms.entity.ConfigReplenishment;
import com.leqee.wms.entity.ConfigReplenishmentUrgent;
import com.leqee.wms.entity.LabelReplenishment;
import com.leqee.wms.entity.Location;
import com.leqee.wms.entity.OrderPrepack;
import com.leqee.wms.entity.OrderProcess;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.entity.ProductLocationDetail;
import com.leqee.wms.entity.ProductLocationNumValidityComparator;
import com.leqee.wms.entity.SysUser;
import com.leqee.wms.entity.Task;
import com.leqee.wms.entity.TaskCount;
import com.leqee.wms.entity.UserActionBatchPick;
import com.leqee.wms.entity.UserActionOrder;
import com.leqee.wms.entity.UserActionOrderPrepack;
import com.leqee.wms.entity.UserActionTask;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.entity.WarehouseCustomer;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.LockUtil;
import com.leqee.wms.util.SequenceUtil;
import com.leqee.wms.util.WorkerUtil;

@Service
public class ReplenishmentBizImpl implements ReplenishmentBiz {

	private Logger logger = Logger.getLogger(ReplenishmentBizImpl.class);
	
	@Autowired
	ReplenishmentDao replenishmentDao;
	@Autowired
	TaskDao taskDao;
	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	OrderProcessDao orderProcessDao;
	@Autowired
	ProductDao productDao;
	@Autowired
	InventoryDao inventoryDao;
	@Autowired
	LocationDao locationDao;
	@Autowired
	LocationBiz locationBiz;
	@Autowired
	BatchTaskDao batchTaskDao;
	@Autowired
	ProductLocationDao productLocationDao;
	@Autowired
	ProductLocationDetailDao productLocationDetailDao;
	@Autowired
	LabelReplenishmentDao labelReplenishmentDao;
	@Autowired
	WarehouseCustomerDao warehouseCustomerDao;
	@Autowired
	WarehouseDao warehouseDao;
	@Autowired
	ReplenishmentUrgentDao replenishmentUrgentDao;
	@Autowired
	BatchPickBizImpl batchPickBizImpl;
	@Autowired
	BatchPickDao batchPickDao;
	@Autowired
	GeneralReplenishmentBiz generalReplenishmentBiz;
	@Autowired
	UserActionTaskDao userActionTaskDao;
	@Autowired
	UserActionOrderDao userActionOrderDao;
	@Autowired
	UserActionBatchPickDao userActionBatchPickDao;
	@Autowired
	SysUserDao sysUserDao;
	@Autowired
	CountTaskDao countTaskDao;
	@Autowired
	TaskImproveDao taskImproveDao;
	@Autowired
	OrderPrepackDao orderPrepackDao;
	@Autowired
	ConfigDao configDao;
	@Autowired
	UserActionOrderPrepackDao userActionOrderPrepackDao;
	private static ArrayList locArray = null;
	static{
		locArray = new ArrayList();
		locArray.add(0, "customer_id");
		locArray.add(1, "product_id");
		locArray.add(2, "product_name");
		locArray.add(3, "from_piece_location_barcode");
		locArray.add(4, "to_piece_location_barcode");
		locArray.add(5, "piece_location_max_quantity");
		locArray.add(6, "piece_location_min_quantity");
		locArray.add(7, "from_box_location_barcode");
		locArray.add(8, "to_box_location_barcode");
		locArray.add(9, "box_location_max_quantity");
		locArray.add(10, "box_location_min_quantity");
	}
		
	@Override
	public List<Map<String, Object>> selectByMap(Map<String, Object> map){
		return  replenishmentDao.selectByMapByPage(map);
	}
	@Override
	public Map<String,Object> insertConfigReplenishmentByMap(Map<String, Object> updateReplenishmentMap,Map<String, Object> searchMap){
		Map<String,Object> locationMap = new HashMap<String,Object>();
		Map<String,Object> resMap = new HashMap<String,Object>();
		Map<String,Object> locationTypeMap = new HashMap<String,Object>();
		Map<String,Object> customerIdMap = new HashMap<String,Object>();
		
		List<Map<String, Object>> LocationIdList = locationDao.selectByLocationMap(searchMap);
		for (Map<String, Object> map : LocationIdList) {
			locationMap.put((String) map.get("location_barcode"), map.get("location_id"));
			locationTypeMap.put((String) map.get("location_barcode"), map.get("location_type"));
			customerIdMap.put((String) map.get("location_barcode"), map.get("customer_id"));
			
		}
		if(!WorkerUtil.isNullOrEmpty(searchMap.get("from_piece_location_barcode")) ){
			if(!WorkerUtil.isNullOrEmpty(locationMap.get(searchMap.get("from_piece_location_barcode")))&& locationTypeMap.get(searchMap.get("from_piece_location_barcode")).equals("PIECE_PICK_LOCATION")
					){
				if(!WorkerUtil.isNullOrEmpty(customerIdMap.get(searchMap.get("from_piece_location_barcode")))){
					if(customerIdMap.get(searchMap.get("from_piece_location_barcode")).equals(searchMap.get("customer_id"))){
						updateReplenishmentMap.put("from_piece_location_barcode", searchMap.get("from_piece_location_barcode"));
					}else{
						resMap.put("error", "不存在的件拣货from库存信息");
						return resMap;
					}
				}else{
					updateReplenishmentMap.put("from_piece_location_barcode", searchMap.get("from_piece_location_barcode"));
					}
			}else{
				resMap.put("error", "不存在的件拣货from库存信息");
				return resMap;
			}
		}else{
			updateReplenishmentMap.put("from_piece_location_barcode", null);
		}
		
		if(!WorkerUtil.isNullOrEmpty(searchMap.get("to_piece_location_barcode")) ){
			if(!WorkerUtil.isNullOrEmpty(locationMap.get(searchMap.get("to_piece_location_barcode")))&& locationTypeMap.get(searchMap.get("to_piece_location_barcode")).equals("PIECE_PICK_LOCATION")
					){
				if(!WorkerUtil.isNullOrEmpty(customerIdMap.get(searchMap.get("to_piece_location_barcode")))){
					if(customerIdMap.get(searchMap.get("to_piece_location_barcode")).equals(searchMap.get("customer_id"))){
						updateReplenishmentMap.put("to_piece_location_barcode", searchMap.get("to_piece_location_barcode"));
					}else{
						resMap.put("error", "不存在的件拣货to库存信息");
						return resMap;
					}
				}else{
					updateReplenishmentMap.put("to_piece_location_barcode", searchMap.get("to_piece_location_barcode"));
					}
			}else{
				resMap.put("error", "不存在的件拣货to库存信息");
				return resMap;
			}
		}else{
			updateReplenishmentMap.put("to_piece_location_barcode", null);
		}
		
		if(!WorkerUtil.isNullOrEmpty(searchMap.get("from_box_location_barcode")) ){
			if(!WorkerUtil.isNullOrEmpty(locationMap.get(searchMap.get("from_box_location_barcode")))&& locationTypeMap.get(searchMap.get("from_box_location_barcode")).equals("BOX_PICK_LOCATION")
					){
				if(!WorkerUtil.isNullOrEmpty(customerIdMap.get(searchMap.get("from_box_location_barcode")))){
					if(customerIdMap.get(searchMap.get("from_box_location_barcode")).equals(searchMap.get("customer_id"))){
						updateReplenishmentMap.put("from_box_location_barcode", searchMap.get("from_box_location_barcode"));
					}else{
						resMap.put("error", "不存在的箱拣货from库存信息");
						return resMap;
					}
				}else{
					updateReplenishmentMap.put("from_box_location_barcode", searchMap.get("from_box_location_barcode"));
					}
			}else{
				resMap.put("error", "不存在的箱拣货from库存信息");
				return resMap;
			}
		}else{
			updateReplenishmentMap.put("from_box_location_barcode", null);
		}
		if(!WorkerUtil.isNullOrEmpty(searchMap.get("to_box_location_barcode")) ){
			if(!WorkerUtil.isNullOrEmpty(locationMap.get(searchMap.get("to_box_location_barcode")))&& locationTypeMap.get(searchMap.get("to_box_location_barcode")).equals("BOX_PICK_LOCATION")
					){
				if(!WorkerUtil.isNullOrEmpty(customerIdMap.get(searchMap.get("to_box_location_barcode")))){
					if(customerIdMap.get(searchMap.get("to_box_location_barcode")).equals(searchMap.get("customer_id"))){
						updateReplenishmentMap.put("to_box_location_barcode", searchMap.get("to_box_location_barcode"));
					}else{
						resMap.put("error", "不存在的箱拣货to库存信息");
						return resMap;
					}
				}else{
					updateReplenishmentMap.put("to_box_location_barcode", searchMap.get("to_box_location_barcode"));
					}
			}else{
				resMap.put("error", "不存在的箱拣货to库存信息");
				return resMap;
			}
		}else{
			updateReplenishmentMap.put("to_box_location_barcode", null);
		}
		updateReplenishmentMap.put("created_user", searchMap.get("created_user"));
		updateReplenishmentMap.put("last_updated_user", searchMap.get("last_updated_user"));
		updateReplenishmentMap.put("created_time", new Date());
		updateReplenishmentMap.put("last_updated_time",new Date());
		updateReplenishmentMap.put("physical_warehouse_id", searchMap.get("physical_warehouse_id"));
		
		replenishmentDao.insertConfigReplenishmentByMap(updateReplenishmentMap);
		resMap.put("result", "success");
		resMap.put("note", "插入成功");
		
		return resMap;
	}
	
	@Override
	public ConfigReplenishment selectReplenishmentByCustomerId(Integer product_id,Integer physical_warehouse_id){
		return replenishmentDao.selectReplenishmentByCustomerId(product_id,physical_warehouse_id);
	}
	@Override
	public Map<String,Object> updateReplenishmentByUpdateReplenishmentMap(Map<String, Object> updateReplenishmentMap,Map<String, Object> searchMap){
		Map<String,Object> locationMap = new HashMap<String,Object>();
		Map<String,Object> resMap = new HashMap<String,Object>();
		Map<String,Object> locationTypeMap = new HashMap<String,Object>();
		Map<String,Object> customerIdMap = new HashMap<String,Object>();
		
		List<Map<String, Object>> LocationIdList = locationDao.selectByLocationMap(searchMap);
		for (Map<String, Object> map : LocationIdList) {
			locationMap.put((String) map.get("location_barcode"), map.get("location_id"));
			locationTypeMap.put((String) map.get("location_barcode"), map.get("location_type"));
			customerIdMap.put((String) map.get("location_barcode"), map.get("customer_id"));
			
		}
		if(!WorkerUtil.isNullOrEmpty(searchMap.get("from_piece_location_barcode")) ){
			if(!WorkerUtil.isNullOrEmpty(locationMap.get(searchMap.get("from_piece_location_barcode")))&& locationTypeMap.get(searchMap.get("from_piece_location_barcode")).equals("PIECE_PICK_LOCATION")
					){
				if(!WorkerUtil.isNullOrEmpty(customerIdMap.get(searchMap.get("from_piece_location_barcode")))){
					if(customerIdMap.get(searchMap.get("from_piece_location_barcode")).equals(searchMap.get("customer_id"))){
						updateReplenishmentMap.put("from_piece_location_barcode", searchMap.get("from_piece_location_barcode"));
					}else{
						resMap.put("error", "不存在的件拣货from库存信息");
						return resMap;
					}
				}else{
					updateReplenishmentMap.put("from_piece_location_barcode", searchMap.get("from_piece_location_barcode"));
					}
			}else{
				resMap.put("error", "不存在的件拣货from库存信息");
				return resMap;
			}
		}else{
			updateReplenishmentMap.put("from_piece_location_barcode", null);
		}
		
		if(!WorkerUtil.isNullOrEmpty(searchMap.get("to_piece_location_barcode")) ){
			if(!WorkerUtil.isNullOrEmpty(locationMap.get(searchMap.get("to_piece_location_barcode")))&& locationTypeMap.get(searchMap.get("to_piece_location_barcode")).equals("PIECE_PICK_LOCATION")
					){
				if(!WorkerUtil.isNullOrEmpty(customerIdMap.get(searchMap.get("to_piece_location_barcode")))){
					if(customerIdMap.get(searchMap.get("to_piece_location_barcode")).equals(searchMap.get("customer_id"))){
						updateReplenishmentMap.put("to_piece_location_barcode", searchMap.get("to_piece_location_barcode"));
					}else{
						resMap.put("error", "不存在的件拣货to库存信息");
						return resMap;
					}
				}else{
					updateReplenishmentMap.put("to_piece_location_barcode", searchMap.get("to_piece_location_barcode"));
					}
			}else{
				resMap.put("error", "不存在的件拣货to库存信息");
				return resMap;
			}
		}else{
			updateReplenishmentMap.put("to_piece_location_barcode", null);
		}
		
		if(!WorkerUtil.isNullOrEmpty(searchMap.get("from_box_location_barcode")) ){
			if(!WorkerUtil.isNullOrEmpty(locationMap.get(searchMap.get("from_box_location_barcode")))&& locationTypeMap.get(searchMap.get("from_box_location_barcode")).equals("BOX_PICK_LOCATION")
					){
				if(!WorkerUtil.isNullOrEmpty(customerIdMap.get(searchMap.get("from_box_location_barcode")))){
					if(customerIdMap.get(searchMap.get("from_box_location_barcode")).equals(searchMap.get("customer_id"))){
						updateReplenishmentMap.put("from_box_location_barcode", searchMap.get("from_box_location_barcode"));
					}else{
						resMap.put("error", "不存在的箱拣货from库存信息");
						return resMap;
					}
				}else{
					updateReplenishmentMap.put("from_box_location_barcode", searchMap.get("from_box_location_barcode"));
					}
			}else{
				resMap.put("error", "不存在的箱拣货from库存信息");
				return resMap;
			}
		}else{
			updateReplenishmentMap.put("from_box_location_barcode", null);
		}
		if(!WorkerUtil.isNullOrEmpty(searchMap.get("to_box_location_barcode")) ){
			if(!WorkerUtil.isNullOrEmpty(locationMap.get(searchMap.get("to_box_location_barcode")))&& locationTypeMap.get(searchMap.get("to_box_location_barcode")).equals("BOX_PICK_LOCATION")
					){
				if(!WorkerUtil.isNullOrEmpty(customerIdMap.get(searchMap.get("to_box_location_barcode")))){
					if(customerIdMap.get(searchMap.get("to_box_location_barcode")).equals(searchMap.get("customer_id"))){
						updateReplenishmentMap.put("to_box_location_barcode", searchMap.get("to_box_location_barcode"));
					}else{
						resMap.put("error", "不存在的箱拣货to库存信息");
						return resMap;
					}
				}else{
					updateReplenishmentMap.put("to_box_location_barcode", searchMap.get("to_box_location_barcode"));
					}
			}else{
				resMap.put("error", "不存在的箱拣货to库存信息");
				return resMap;
			}
		}else{
			updateReplenishmentMap.put("to_box_location_barcode", null);
		}
		updateReplenishmentMap.put("created_user", searchMap.get("created_user"));
		updateReplenishmentMap.put("last_updated_user", searchMap.get("last_updated_user"));
		updateReplenishmentMap.put("last_updated_time",new Date());
		updateReplenishmentMap.put("physical_warehouse_id", searchMap.get("physical_warehouse_id"));
		replenishmentDao.updateReplenishmentByUpdateReplenishmentMap(updateReplenishmentMap);
		resMap.put("result", "success");
		resMap.put("note", "更新成功");
		return resMap;
		}
	@Override 
    public 	void insertConfigReplenishment(List<ConfigReplenishment> configReplenishmentList){
		if(replenishmentDao.insertConfigReplenishment(configReplenishmentList)<=0){
			throw new RuntimeException("insertReplenishment更新失败,受影响行数为0");
		}
	}
	@Override
	public void deleteReplenishment (String customer_id,Integer product_id,Integer physical_warehouse_id){
		if(replenishmentDao.deleteReplenishment(customer_id, product_id,physical_warehouse_id)<=0){
			throw new RuntimeException("updateReplenishment删除失败,受影响行数为0");
		}
	}
	@Override
	public ConfigReplenishment selectReplenishmentIsExist(Map<String, Object>map){
		return replenishmentDao.selectReplenishmentIsExist(map);
	}
	@Override
	public List<Map<String,Object>> uploadLocationV2(InputStream input,boolean isE2007,String actionUser){
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        Warehouse currentPhysicalWarehouse = (Warehouse) session.getAttribute("currentPhysicalWarehouse");
        
//	public Map finishReplenishmentTask(String taskId){
        Map<String,Object> returnMap2 = new HashMap<String,Object>();
		List<Map<String,Object>> returnMapList = new ArrayList<Map<String,Object>>();
		int count = 0;
		int flag = 0;
		Integer customerId=null;
		List<ConfigReplenishment> configReplenishmentList  = new ArrayList<ConfigReplenishment>();
		String msg = "";
		try{
		
			Workbook wb  = null; //根据文件格式(2003或者2007)来初始化
			if(isE2007){
				wb = new XSSFWorkbook(input);
			}
			else{ 
				wb = new HSSFWorkbook(input);
			}
			Sheet sheet = wb.getSheetAt(0);		//获得第一个表单
			Iterator<Row> rows = sheet.rowIterator();
			//获得第一个表单的迭代器
			while (rows.hasNext()) {
				String msg1 ="";
				Row row = rows.next();	//获得行数据
				if(row.getRowNum() == 0){ 
					
					continue;
				}
				//System.out.println("Row #" + row.getRowNum());	//获得行号从0开始
				Iterator<Cell> cells = row.cellIterator();	//获得第一行的迭代器
				int i = 0;
				JSONObject locJson = new JSONObject();
				Map<String, Object> searchMap1 = new HashMap<String, Object>();
				Map<String, Object> locationMap = new HashMap<String, Object>();
				Map<String, Object> locationMap1 = new HashMap<String, Object>();
				Map<String, Object> locationMap2 = new HashMap<String, Object>();
				Map<String,Object> returnMap = new HashMap<String,Object>();
				Integer productFlag = 0;
				Integer customerFlag= 0;

				while (cells.hasNext()) {
					
					Cell cell = cells.next();
					cell.setCellType(Cell.CELL_TYPE_STRING);
					//System.out.print("Cell #" + cell.getColumnIndex()+" -  "+cell.getCellType()+" - "+cell.getStringCellValue());
					
					if( cell.getColumnIndex() == 3 || cell.getColumnIndex() == 4 || cell.getColumnIndex() == 7 ||
							cell.getColumnIndex() == 8 ){
						searchMap1.put("locationbarcode", cell.getStringCellValue());
						searchMap1.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
					}
					
					List<Map<String, Object>> LocationIdList = locationBiz.selectByLocationId(searchMap1);
					for (Map<String, Object> map : LocationIdList) {
						locationMap.put((String) map.get("location_barcode"), map.get("location_id"));
					}
					for (Map<String, Object> map : LocationIdList) {
						locationMap1.put((String) map.get("location_barcode"), map.get("customer_id"));
					}
					for (Map<String, Object> map : LocationIdList) {
						locationMap2.put((String) map.get("location_barcode"), map.get("location_type"));
					}
					
					if(cell.getColumnIndex() == 5 || cell.getColumnIndex() == 6 || cell.getColumnIndex() == 9 ||cell.getColumnIndex() == 10){
						if(cell.getStringCellValue() != "" && cell.getStringCellValue() != null){
							if(WorkerUtil.isNumeric(cell.getStringCellValue()) == false){
								msg= msg+"error"+"错误行数为第"+row.getRowNum()+"行,第"+cell.getColumnIndex()+"列,最低和最高存量的数值不允许出现除数字外的字符||";
								flag=1;
							}
						}
					}
					
					switch (cell.getCellType()) {	//根据cell中的类型来输出数据
						case HSSFCell.CELL_TYPE_STRING:
							//System.out.println(i+" - "+cell.getStringCellValue());
							if( cell.getColumnIndex() == 3 || cell.getColumnIndex() == 4 || cell.getColumnIndex() == 7 ||
									cell.getColumnIndex() == 8 || cell.getColumnIndex() == 0 ||cell.getColumnIndex() == 1){
								if(cell.getColumnIndex() == 0 ){
									if(cell.getStringCellValue()!=null && cell.getStringCellValue()!=""){
										 customerId = warehouseCustomerDao.selectCustomerIdByName(cell.getStringCellValue());
										if(!WorkerUtil.isNullOrEmpty(customerId)){
										locJson.put(locArray.get(cell.getColumnIndex()),customerId);
										}else{
											msg=msg+"error"+ "错误行数为第"+row.getRowNum()+"行,第"+cell.getColumnIndex()+"列,输入的货主不存在||";
											customerFlag =1;
											flag=1;	
										}	
									}
								}
								
								if(cell.getColumnIndex() == 1){
									if(cell.getStringCellValue()!=null && cell.getStringCellValue()!=""){  
										Product product = productDao.selectProductByBarcodeCustomer(cell.getStringCellValue(),customerId);
										if(!WorkerUtil.isNullOrEmpty(product)){
											locJson.put(locArray.get(cell.getColumnIndex()),product.getProduct_id());
										}else{
											msg=msg+"error"+ "错误行数为第"+row.getRowNum()+"行,第"+cell.getColumnIndex()+"列,输入的商品条码不存在||";
										    productFlag =1;
											flag=1;	
										}	
									}
								}
								
								if(cell.getColumnIndex() == 3 ){
									if(cell.getStringCellValue()!=null && cell.getStringCellValue()!="" ){
										if(!WorkerUtil.isNullOrEmpty(locationMap.get(cell.getStringCellValue())) &&
												("PIECE_PICK_LOCATION").equals(locationMap2.get(cell.getStringCellValue()))){
											if(WorkerUtil.isNullOrEmpty(locationMap1.get(cell.getStringCellValue()))){
												locJson.put(locArray.get(cell.getColumnIndex()),cell.getStringCellValue().toUpperCase());
											}else{
												if(!locationMap1.get(cell.getStringCellValue()).toString().equals(customerId.toString())){
													msg=msg+"error"+ "错误行数为第"+row.getRowNum()+"行,第"+cell.getColumnIndex()+"列,件拣货区(from)的库位信息不存在||";
													flag=1;	
												}else{
													locJson.put(locArray.get(cell.getColumnIndex()),cell.getStringCellValue().toUpperCase());
												}
											}
										}else{
											msg=msg+"error"+ "错误行数为第"+row.getRowNum()+"行,第"+cell.getColumnIndex()+"列,件拣货区(from)的库位信息不存在||";
											flag=1;	
										}
								}else{
									locJson.put(locArray.get(cell.getColumnIndex()), "");
								}	
								}
								if(cell.getColumnIndex() == 4 ){
									if(cell.getStringCellValue()!=null && cell.getStringCellValue()!="" ){
										if(!WorkerUtil.isNullOrEmpty(locationMap.get(cell.getStringCellValue())) &&
												("PIECE_PICK_LOCATION").equals(locationMap2.get(cell.getStringCellValue()))){
											if(WorkerUtil.isNullOrEmpty(locationMap1.get(cell.getStringCellValue()))){
												locJson.put(locArray.get(cell.getColumnIndex()),cell.getStringCellValue().toUpperCase());
											}else{
												if(!locationMap1.get(cell.getStringCellValue()).toString().equals(customerId.toString())){
													msg=msg+"error"+ "错误行数为第"+row.getRowNum()+"行,第"+cell.getColumnIndex()+"列,件拣货区(to)的库位信息不存在||";
													flag=1;	
												}else{
													locJson.put(locArray.get(cell.getColumnIndex()),cell.getStringCellValue().toUpperCase());
												}
											}
										}else{
											msg=msg+"error"+ "错误行数为第"+row.getRowNum()+"行,第"+cell.getColumnIndex()+"列,件拣货区(to)的库位信息不存在||";
											flag=1;	
										}
								}else{
									locJson.put(locArray.get(cell.getColumnIndex()), "");
								}	
								}
								if(cell.getColumnIndex() == 7){
									if(cell.getStringCellValue()!=null && cell.getStringCellValue()!="" ){
										if(!WorkerUtil.isNullOrEmpty(locationMap.get(cell.getStringCellValue())) &&
												("BOX_PICK_LOCATION").equals(locationMap2.get(cell.getStringCellValue()))){
											if(WorkerUtil.isNullOrEmpty(locationMap1.get(cell.getStringCellValue()))){
												locJson.put(locArray.get(cell.getColumnIndex()),cell.getStringCellValue().toUpperCase());
											}else{
												if(!locationMap1.get(cell.getStringCellValue()).toString().equals(customerId.toString())){
													msg=msg+"error"+ "错误行数为第"+row.getRowNum()+"行,第"+cell.getColumnIndex()+"列,箱拣货区(from)的库位信息不存在||";
													flag=1;	
												}else{
													locJson.put(locArray.get(cell.getColumnIndex()),cell.getStringCellValue().toUpperCase());
												}
											}
										}else{
											msg=msg+"error"+ "错误行数为第"+row.getRowNum()+"行,第"+cell.getColumnIndex()+"列,箱拣货区(from)的库位信息不存在||";
											flag=1;	
										}
								}else{
									locJson.put(locArray.get(cell.getColumnIndex()), "");
								}	
								}
								if(cell.getColumnIndex() == 8 ){
									if(cell.getStringCellValue()!=null && cell.getStringCellValue()!="" ){
										if(!WorkerUtil.isNullOrEmpty(locationMap.get(cell.getStringCellValue())) &&
												("BOX_PICK_LOCATION").equals(locationMap2.get(cell.getStringCellValue()))){
											if(WorkerUtil.isNullOrEmpty(locationMap1.get(cell.getStringCellValue()))){
												locJson.put(locArray.get(cell.getColumnIndex()),cell.getStringCellValue().toUpperCase());
											}else{
												if(!locationMap1.get(cell.getStringCellValue()).toString().equals(customerId.toString())){
													msg=msg+"error"+ "错误行数为第"+row.getRowNum()+"行,第"+cell.getColumnIndex()+"列,箱拣货区(to)的库位信息不存在||";
													flag=1;	
												}else{
													locJson.put(locArray.get(cell.getColumnIndex()),cell.getStringCellValue().toUpperCase());
												}
											}
										}else{
											msg=msg+"error"+ "错误行数为第"+row.getRowNum()+"行,第"+cell.getColumnIndex()+"列,箱拣货区(to)的库位信息不存在||";
											flag=1;	
										}
								}else{
									locJson.put(locArray.get(cell.getColumnIndex()), "");
								}	
								}
							}else{
								locJson.put(locArray.get(cell.getColumnIndex()), cell.getStringCellValue());
							}
							break;
						case HSSFCell.CELL_TYPE_NUMERIC:												
								locJson.put(locArray.get(cell.getColumnIndex()), cell.getNumericCellValue());
							break;
						case HSSFCell.CELL_TYPE_BOOLEAN:
							locJson.put(locArray.get(cell.getColumnIndex()), cell.getBooleanCellValue());
							break;
						case HSSFCell.CELL_TYPE_FORMULA:	
							locJson.put(locArray.get(cell.getColumnIndex()), cell.getCellFormula());
							break;
						default:
							////System.out.print("unsuported sell type");
							locJson.put(locArray.get(cell.getColumnIndex()), "");
							break;
					}
				}
				
				ConfigReplenishment configReplenishment = (ConfigReplenishment)JSONObject.toBean(locJson,ConfigReplenishment.class);
				configReplenishment.setCreated_time(new Date());
				configReplenishment.setLast_updated_time(new Date());
				configReplenishment.setPhysical_warehouse_id(currentPhysicalWarehouse.getPhysical_warehouse_id());
				configReplenishment.setCreated_user(actionUser);
				configReplenishment.setLast_updated_user(actionUser);
				if(WorkerUtil.isNullOrEmpty(configReplenishment.getCustomer_id()) && WorkerUtil.isNullOrEmpty(configReplenishment.getProduct_id())){
					msg1= msg1+"error"+"错误行数为第"+row.getRowNum()+"行,货主和商品条码必须填写正确||";
					returnMap.put("result", msg1);
					returnMapList.add(returnMap);
					continue;
				}
				
				if(WorkerUtil.isNullOrEmpty(configReplenishment.getFrom_box_location_barcode()) ^ WorkerUtil.isNullOrEmpty(configReplenishment.getTo_box_location_barcode())){
					msg= msg+"error"+"错误行数为第"+row.getRowNum()+"行,箱拣货区库位to如果设置，则from也必须填写||";
					flag=1;
					}else{
						if(WorkerUtil.isNullOrEmpty(configReplenishment.getFrom_box_location_barcode()) && WorkerUtil.isNullOrEmpty(configReplenishment.getTo_box_location_barcode())){
						configReplenishment.setFrom_box_location_barcode("");
						configReplenishment.setTo_box_location_barcode("");
						}
					}
				
				if(WorkerUtil.isNullOrEmpty(configReplenishment.getFrom_piece_location_barcode()) ^ WorkerUtil.isNullOrEmpty(configReplenishment.getTo_piece_location_barcode())){
					msg= msg+"error"+"错误行数为第"+row.getRowNum()+"行,件拣货区to如果设置，则from也必须填写||";
					flag=1;
					}else{
						if(WorkerUtil.isNullOrEmpty(configReplenishment.getFrom_piece_location_barcode()) && WorkerUtil.isNullOrEmpty(configReplenishment.getTo_piece_location_barcode())){
						if(WorkerUtil.isNullOrEmpty(configReplenishment.getFrom_box_location_barcode()) && WorkerUtil.isNullOrEmpty(configReplenishment.getTo_box_location_barcode()))
						configReplenishment.setFrom_piece_location_barcode("");
						configReplenishment.setTo_piece_location_barcode("");
						}
					}
				if(WorkerUtil.isNullOrEmpty(configReplenishment.getCustomer_id()) && customerFlag ==0){
					msg= msg+"error"+"错误行数为第"+row.getRowNum()+"行,第0列,货主不能为空||";
					flag=1;
				}
				if(WorkerUtil.isNullOrEmpty(configReplenishment.getProduct_id()) && productFlag ==0){
					msg= msg+"error"+"错误行数为第"+row.getRowNum()+"行,第1列,商品条码不能为空||";
					flag=1;
				}
				if(WorkerUtil.isNullOrEmpty(configReplenishment.getBox_location_max_quantity())||WorkerUtil.isNullOrEmpty(configReplenishment.getBox_location_min_quantity())
						||WorkerUtil.isNullOrEmpty(configReplenishment.getPiece_location_max_quantity())||WorkerUtil.isNullOrEmpty(configReplenishment.getPiece_location_min_quantity())){
					msg= msg+"error"+"错误行数为第"+row.getRowNum()+"行,原因为最低和最高存量的数值不能为空||";
					flag=1;
				}else {
					if(configReplenishment.getBox_location_max_quantity()<0||configReplenishment.getBox_location_min_quantity()<0
							||configReplenishment.getPiece_location_max_quantity()<0||configReplenishment.getPiece_location_min_quantity()<0){
						msg= msg+"error"+"错误行数为第"+row.getRowNum()+"行,原因为最低和最高存量的数值不能小于0||";
						flag=1;
					}
					if(configReplenishment.getBox_location_max_quantity()!=0 || configReplenishment.getBox_location_min_quantity()!=0){
						if(configReplenishment.getBox_location_min_quantity()>=configReplenishment.getBox_location_max_quantity()){
							msg= msg+"error"+"错误行数为第"+row.getRowNum()+"行,箱拣货区最高存量不能低于最低存量||";
							flag=1;
						}
					}
					if(configReplenishment.getPiece_location_max_quantity()!=0 ||configReplenishment.getPiece_location_min_quantity()!=0){
						if(configReplenishment.getPiece_location_min_quantity()>=configReplenishment.getPiece_location_max_quantity()){
							msg= msg+"error"+"错误行数为第"+row.getRowNum()+"行,件拣货区最高存量不能低于最低存量||";
							flag=1;
						}
					}
					if(configReplenishment.getBox_location_max_quantity()!=0 ){
						if(configReplenishment.getBox_location_max_quantity()<=configReplenishment.getPiece_location_max_quantity()){
							msg= msg+"error"+"错误行数为第"+row.getRowNum()+"行,箱拣货区的最高存量不能小于件拣货区的最高存量||";
							flag=1;
						}
					}
				}
				
//				if(!WorkerUtil.isNullOrEmpty(configReplenishment.getFrom_box_location_barcode()) && !WorkerUtil.isNullOrEmpty(configReplenishment.getTo_box_location_barcode())
//						&&!WorkerUtil.isNullOrEmpty(configReplenishment.getFrom_piece_location_barcode()) && !WorkerUtil.isNullOrEmpty(configReplenishment.getTo_piece_location_barcode())){
//				if(configReplenishment.getFrom_piece_location_barcode().contains("-") ||configReplenishment.getFrom_box_location_barcode().contains("-")
//						||configReplenishment.getTo_box_location_barcode().contains("-")||configReplenishment.getTo_piece_location_barcode().contains("-")){
//					WorkerUtil.transferLocationBarcode(configReplenishment.getFrom_piece_location_barcode());
//					WorkerUtil.transferLocationBarcode(configReplenishment.getFrom_box_location_barcode());
//					WorkerUtil.transferLocationBarcode(configReplenishment.getTo_box_location_barcode());
//					WorkerUtil.transferLocationBarcode(configReplenishment.getTo_piece_location_barcode());
//				}
//				}
				if(!WorkerUtil.isNullOrEmpty(configReplenishment.getFrom_box_location_barcode()) && !WorkerUtil.isNullOrEmpty(configReplenishment.getTo_box_location_barcode())
					&&!WorkerUtil.isNullOrEmpty(configReplenishment.getFrom_piece_location_barcode()) && !WorkerUtil.isNullOrEmpty(configReplenishment.getTo_piece_location_barcode()))
				if(configReplenishment.getFrom_piece_location_barcode().compareTo(configReplenishment.getTo_piece_location_barcode())>0){
					WorkerUtil.swap(configReplenishment.getFrom_piece_location_barcode(), configReplenishment.getTo_piece_location_barcode());
				}
				if(configReplenishment.getFrom_box_location_barcode().compareTo(configReplenishment.getTo_box_location_barcode())>0){
					WorkerUtil.swap(configReplenishment.getFrom_box_location_barcode(), configReplenishment.getTo_box_location_barcode());
				}
				configReplenishmentList.add(configReplenishment);
				productFlag = 0;
				customerFlag= 0;
				Map<String, Object> searchMap = new HashMap<String, Object>();
				searchMap.put("physical_warehouse_id", currentPhysicalWarehouse.getPhysical_warehouse_id());
				searchMap.put("product_id", configReplenishment.getProduct_id());
				searchMap.put("customer_id",configReplenishment.getCustomer_id());
				if (!WorkerUtil.isNullOrEmpty(replenishmentDao.selectReplenishmentIsExist(searchMap))) {
					msg=msg+"error"+"错误行数为第"+row.getRowNum()+"行,同批导入记录,不允许出现相同商品记录||";
					flag=1;
				}
				count ++;
				returnMap.put("result", msg);
				returnMapList.add(returnMap);
				msg="";
			}
		//数据校验失败，返回错误信息;数据校验成功，返回插入成功信息。
		if(flag==1){
			returnMap2.put("result", "failure,导入失败，数据有问题");
			returnMapList.add(returnMap2);
		}else{
		
		replenishmentDao.insertConfigReplenishment(configReplenishmentList);
		returnMap2.put("result", "success,已经导入"+count+"补货规则");
		returnMapList.add(returnMap2);
		}
		}catch (Exception ex) {
			logger.error("UploadLocationV2 error:", ex);
			returnMap2.put("result", "异常,请联系erp");
			returnMapList.add(returnMap2);
		}
		return returnMapList;
	}
	
	
	/**********************************
	 * 得到补货任务列表 
	 * @author hzhang1
	 * @date 2016-06-21
	 * @param map
	 **********************************/
	public Location checkLocationBarcode(Integer physicalWarehouseId,String locationBarcode){
		return locationDao.selectLocationIdByLocationBarCode(physicalWarehouseId, locationBarcode, null);
	}
	
	public List<Map> getReplenishmentTask(Map map){
		return replenishmentDao.selectReplenishmentTaskByPage(map);
	} 
	
	@SuppressWarnings("rawtypes")
	public Map printReplenishmentTask(String temp,String actionUser){
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		List<Map> printReplenishmentTaskList = new ArrayList<Map>();
		SysUser sysUser = sysUserDao.selectByUsername(actionUser);
		if(WorkerUtil.isNullOrEmpty(sysUser)){
			throw new RuntimeException("工牌绑定人不存在");
		}
		
		String bhCode = WorkerUtil.generatorSequence(SequenceUtil.KEY_NAME_BHCODE,"",true);
		// 1.组装成taskIdIist
		String taskIdArr[] = temp.split(",");
		ArrayList<Integer> taskIdList = new ArrayList<Integer>(); 
		for(String taskId : taskIdArr){
			Task task = taskDao.selectByTaskIdForUpdate(Integer.parseInt(taskId.trim()));
			if(!Task.TASK_STATUS_CANCEL.equals(task.getTask_status()) && !Task.TASK_STATUS_FULFILLED.equals(task.getTask_status()))
				taskIdList.add(Integer.parseInt(taskId.trim()));
		}
		
		try {
			if(!WorkerUtil.isNullOrEmpty(taskIdList)){
				// 2.先往batch_task插入一条记录
				BatchTask batchTask = new BatchTask();
				batchTask.setBatch_task_sn(bhCode);
				batchTask.setTask_type(BatchTask.TASK_TYPE_REPLENISHMENT);
				batchTask.setStatus('1'); 
				batchTask.setCreated_user(actionUser);
				batchTaskDao.insert(batchTask);
			
				// 3.批量更新task状态
				Map<String,Object> paramsMap = new HashMap<String,Object>(); 
				paramsMap.put("taskStatus", "INIT");
				paramsMap.put("task_status", "IN_PROCESS");
				paramsMap.put("taskIdList", taskIdList);
				paramsMap.put("actionUser", actionUser);
				paramsMap.put("userId", sysUser.getId());
				paramsMap.put("batchTaskId", batchTask.getBatch_task_id());
				printReplenishmentTaskList = taskDao.selectTaskByIdList(paramsMap);
				
				// 4.批量更新task任务状态
				taskDao.updateTaskByIdList(paramsMap);
				
				// 5.操作日志
				List<UserActionTask> updateTaskList = new ArrayList<UserActionTask>();
				if (!WorkerUtil.isNullOrEmpty(taskIdList)) {
					for(Integer taskId:taskIdList){
						UserActionTask userActionTask = new UserActionTask();
						userActionTask.setTask_id(taskId);
						userActionTask.setAction_note("WEB打印补货任务");
						userActionTask.setAction_type(Task.TASK_STATUS_IN_PROCESS);
						userActionTask.setTask_status(Task.TASK_STATUS_IN_PROCESS);
						userActionTask.setCreated_user(actionUser);
						userActionTask.setCreated_time(new Date());
						updateTaskList.add(userActionTask);	
					}
				}
				
				if (!WorkerUtil.isNullOrEmpty(updateTaskList)) {
					userActionTaskDao.batchInsert(updateTaskList);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("打印补货批次失败", e);
			throw new RuntimeException("打印补货批次失败,失败原因："+e.getMessage());
		}
		
		// 5.返回结果
		returnMap.put("printReplenishmentTaskList", printReplenishmentTaskList);
		returnMap.put("bhCode", bhCode);
		return returnMap;
	}
	
	
	@SuppressWarnings("rawtypes")
	public Map updateReplenishment(String taskId,String fromLocationBarcode,String toLocationBarcode,String quantity,Integer physicalWarehouseId,String actionUser){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		
		Task task = taskDao.selectByTaskIdForUpdate(Integer.parseInt(taskId));
		if(!Task.TASK_OPERATE_PLATFORM_WEB.equals(task.getOperate_platform())){
			returnMap.put("result", Response.FAILURE);
			returnMap.put("success", Boolean.FALSE);
			returnMap.put("note", "手持任务进行中，WEB不允许更新操作");
			return returnMap;
		}
		if(Task.TASK_STATUS_INIT.equals(task.getTask_status())){
			returnMap.put("result", Response.FAILURE);
			returnMap.put("success", Boolean.FALSE);
			returnMap.put("note", "请先打印补货批次再完结");
			return returnMap;
		}
		if(Task.TASK_STATUS_CANCEL.equals(task.getTask_status())){
			returnMap.put("result", Response.FAILURE);
			returnMap.put("success", Boolean.FALSE);
			returnMap.put("note", "取消任务不能完结任务");
			return returnMap;
		}
		if(Task.TASK_STATUS_FULFILLED.equals(task.getTask_status())){
			returnMap.put("result", Response.FAILURE);
			returnMap.put("success", Boolean.FALSE);
			returnMap.put("note", "任务号"+taskId+"已完结");
			return returnMap;
		}
		
		int locationId = 0;
		try {
			Location fromLocation = locationDao.selectLocationIdByLocationBarCode(physicalWarehouseId,fromLocationBarcode,"");
			Location toLocation = locationDao.selectLocationIdByLocationBarCode(physicalWarehouseId,toLocationBarcode,"");
			if(WorkerUtil.isNullOrEmpty(fromLocation) || WorkerUtil.isNullOrEmpty(toLocation)){
				returnMap.put("result", Response.FAILURE);
				returnMap.put("note", "库位不存在!");
				return returnMap;
			}
			if ((Location.LOCATION_TYPE_STOCK.equals(fromLocation
					.getLocation_type()) && Location.LOCATION_TYPE_BOX_PICK
					.equals(toLocation.getLocation_type()))
					||	(Location.LOCATION_TYPE_STOCK.equals(fromLocation
							.getLocation_type()) && Location.LOCATION_TYPE_PIECE_PICK
							.equals(toLocation.getLocation_type()))
					|| (Location.LOCATION_TYPE_BOX_PICK.equals(fromLocation
							.getLocation_type()) && Location.LOCATION_TYPE_PIECE_PICK
							.equals(toLocation.getLocation_type()))) {
				;
			} else {
				returnMap.put("result", Response.FAILURE);
				returnMap.put("note", "源库位库位类型或目标库位类型有误");
				return returnMap;
			}
			locationId = toLocation.getLocation_id();
			if(WorkerUtil.isNullOrEmpty(locationId)){
				returnMap.put("result", Response.FAILURE);
				returnMap.put("note", "目标库位不存在!");
				return returnMap;
			}
			Map<String,Object> paramsForSelectTaskMap = new HashMap<String,Object>();
			paramsForSelectTaskMap.put("locationId", locationId);
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
					String status2 = WorkerUtil.isNullOrEmpty(isMixMap.get("status"))?"":isMixMap.get("status").toString();
					String qty_total = WorkerUtil.isNullOrEmpty(isMixMap.get("qty_total"))?"":isMixMap.get("qty_total").toString();
					if(!"".equals(product_id2) && !"0".equals(qty_total)){
						String can_mix_product = !isMixMap.containsKey("can_mix_product")?"0":isMixMap.get("can_mix_product").toString();
						String can_mix_batch = !isMixMap.containsKey("can_mix_batch")?"0":isMixMap.get("can_mix_batch").toString();
						if("0".equals(can_mix_product) && !product_id2.equals(task.getProduct_id().toString())){
							returnMap.put("result", Response.FAILURE);
							returnMap.put("success", Boolean.FALSE);
							returnMap.put("note", "目标库位不允许混放商品");
							flag = false;
							break;
						}
//						else if("0".equals(can_mix_batch)
//								&& (!validity2.equals(validity2) || !status2.equals(toStatusId))){
//							returnMap.put("result", Response.FAILURE);
//							returnMap.put("success", Boolean.FALSE);
//							returnMap.put("note", "目标库位不允许混放批次");
//							flag = false;
//							break;
//						}
					}
				}
				
				if(Boolean.FALSE.equals(flag)){
					return returnMap;
				}
			}
			logger.info("Web updateReplenishment , taskId:" + taskId
					+ ",fromLocationBarcode:" + fromLocationBarcode
					+ ",toLocationBarcode:" + toLocationBarcode + ",quantity:"
					+ quantity + ",physicalWarehouseId:" + physicalWarehouseId
					+ ",locationId:" + locationId);
			
			Map<String,Object> paramsForUpdateMap = new HashMap<String,Object>();
			paramsForUpdateMap.put("taskId", taskId);
			paramsForUpdateMap.put("locationId", locationId);
			//paramsForUpdateMap.put("quantity", quantity);
			paramsForUpdateMap.put("actionUser", actionUser);
			int col = taskDao.updateTaskById(paramsForUpdateMap);
			if(col > 0 ){
				UserActionTask userActionTask = new UserActionTask();
				userActionTask.setTask_id(Integer.parseInt(taskId));
				userActionTask.setTask_status(Task.TASK_STATUS_IN_PROCESS);
				userActionTask.setAction_type(Task.TASK_STATUS_IN_PROCESS);
				userActionTask.setAction_note("WEB更新补货任务");
				userActionTask.setCreated_time(new Date());
				userActionTask.setCreated_user(actionUser);
				userActionTaskDao.insert(userActionTask);
				
				returnMap = this.finishReplenishmentTask(taskId,physicalWarehouseId,actionUser);
				if("failure".equals(returnMap.get("result").toString())){
					throw new RuntimeException("更新补货任务失败"+returnMap.get("note").toString());
				}
				returnMap.put("result", Response.SUCCESS);
				returnMap.put("note", "完结补货任务成功!");
			}else{
				throw new RuntimeException("更新补货任务失败:updateTaskById error.");
			}
		} catch (Exception e) {
			logger.error("更新补货任务失败", e);
			throw new RuntimeException("更新补货任务失败"+e.getMessage());
		}
		
		return returnMap;
	}
	
	@SuppressWarnings("rawtypes")
	public Map finishReplenishmentTask(String taskId,Integer physicalWarehouseId,String actionUser){
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		// 1.查找补货任务
		Map<String,Object> paramsForSelectTaskMap = new HashMap<String,Object>();
		paramsForSelectTaskMap.put("taskId", taskId);
		paramsForSelectTaskMap.put("physicalWarehouseId", physicalWarehouseId);
		Map taskReturnMap = replenishmentDao.selectReplenishmentTaskById(paramsForSelectTaskMap);
		if(WorkerUtil.isNullOrEmpty(taskReturnMap)){
			returnMap.put("result", Response.FAILURE);
			returnMap.put("success", Boolean.FALSE);
			returnMap.put("note", "未找到该补货任务");
			return returnMap;
		}
		
		String productId = taskReturnMap.get("product_id").toString();
		Integer quantity = Integer.parseInt(taskReturnMap.get("quantity").toString());
		String status = taskReturnMap.get("status").toString();
		String validity = WorkerUtil.isNullOrEmpty(taskReturnMap.get("validity"))?"":taskReturnMap.get("validity").toString();
		Integer warehouseId = Integer.parseInt(taskReturnMap.get("warehouse_id").toString());
		String batchSn = WorkerUtil.isNullOrEmpty(taskReturnMap.get("batch_sn"))?"":taskReturnMap.get("batch_sn").toString();
		String serialNumber = WorkerUtil.isNullOrEmpty(taskReturnMap.get("serial_number"))?"":taskReturnMap.get("serial_number").toString();
		String toLocationBarcode = taskReturnMap.get("location_barcode").toString();
		String locationId = WorkerUtil.isNullOrEmpty(taskReturnMap.get("location_id"))?"0":taskReturnMap.get("location_id").toString();
		String locationId2 = WorkerUtil.isNullOrEmpty(taskReturnMap.get("location_id2"))?"0":taskReturnMap.get("location_id2").toString();
		
		logger.info("网页Web版完成捕获任务,taskId:"+taskId+",productId:" + productId + ",quantity:"
				+ quantity + ",status:" + status + ",validity:" + validity
				+ ",batchSn:" + batchSn + ",serialNumber:" + serialNumber
				+ ",toLocationBarcode:" + toLocationBarcode + ",locationId:"+locationId+",locationId2:"+locationId2);
		
		paramsForSelectTaskMap = new HashMap<String,Object>();
		paramsForSelectTaskMap.put("locationId", locationId2);
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
				String status2 = WorkerUtil.isNullOrEmpty(isMixMap.get("status"))?"":isMixMap.get("status").toString();
				String batchSn2 = WorkerUtil.isNullOrEmpty(isMixMap.get("batch_sn"))?"":isMixMap.get("batch_sn").toString();
				Integer warehouseId2 = WorkerUtil.isNullOrEmpty(isMixMap.get("warehouse_id")) ? null : Integer.parseInt(isMixMap.get("warehouse_id").toString());
				String qty_total = WorkerUtil.isNullOrEmpty(isMixMap.get("qty_total"))?"":isMixMap.get("qty_total").toString();
				if(!"".equals(product_id2) && !"0".equals(qty_total)){
					String can_mix_product = !isMixMap.containsKey("can_mix_product")?"0":isMixMap.get("can_mix_product").toString();
					String can_mix_batch = !isMixMap.containsKey("can_mix_batch")?"0":isMixMap.get("can_mix_batch").toString();
					if("0".equals(can_mix_product) && !product_id2.equals(productId)){
						returnMap.put("result", Response.FAILURE);
						returnMap.put("success", Boolean.FALSE);
						returnMap.put("note", "目标库位不允许混放商品");
						flag = false;
						break;
					}
					if("0".equals(can_mix_batch)
							&& (!validity2.equals(validity) || !status2.equals(status) || !batchSn2.equals(batchSn))){
						returnMap.put("result", Response.FAILURE);
						returnMap.put("success", Boolean.FALSE);
						returnMap.put("note", "目标库位不允许混放批次");
						flag = false;
						break;
					}
					if(product_id2.equals(productId) && !warehouseId2.equals(warehouseId)){
						returnMap.put("result", Response.FAILURE);
						returnMap.put("success", Boolean.FALSE);
						returnMap.put("note", toLocationBarcode + "库位商品渠道不同不允许存放！请重新输入！");
						flag = false;
						break;
					}
				}
			}
			
			if(Boolean.FALSE.equals(flag)){
				return returnMap;
			}
		}
		
		
		Task task = taskDao.selectByTaskIdForUpdate(Integer.parseInt(taskId));
		if(Task.TASK_STATUS_INIT.equals(task.getTask_status())){
			returnMap.put("result", Response.FAILURE);
			returnMap.put("success", Boolean.FALSE);
			returnMap.put("note", "请先打印补货批次再完结");
			return returnMap;
		}
		if(Task.TASK_OPERATE_PLATFORM_RF.equals(task.getOperate_platform())){
			returnMap.put("result", Response.FAILURE);
			returnMap.put("success", Boolean.FALSE);
			returnMap.put("note", "手持任务WEB不允许操作");
			return returnMap;
		}
		try {
			// 2.扣减库存
			Map<String,Object> paramsForPlMap = new HashMap<String,Object>();
			paramsForPlMap.put("productId", productId);
			paramsForPlMap.put("status", status);
			paramsForPlMap.put("validity", validity);
			paramsForPlMap.put("batchSn", batchSn);
			paramsForPlMap.put("quantity", quantity);
			paramsForPlMap.put("actionUser", actionUser);
			paramsForPlMap.put("serialNumber", serialNumber);
			paramsForPlMap.put("locationId", locationId);
			//ProductLocation productLocation = replenishmentDao.selectProductLocation(paramsForPlMap);
			ProductLocation productLocation = productLocationDao.selectProduLocationById(task.getFrom_pl_id());
			if(WorkerUtil.isNullOrEmpty(productLocation)){
				returnMap.put("result", Response.FAILURE);
				returnMap.put("note", "补货上架源库位未找到商品信息");
				return returnMap;
			}
			paramsForPlMap.put("plId", productLocation.getPl_id());
			replenishmentDao.updateReplenishmentFromProductLocationMinus(paramsForPlMap);
			if(quantity.equals(productLocation.getQty_total())){
				Integer total = productLocationDao.selectProductLocationQtyByLocationId(Integer.parseInt(locationId));
				if(total.equals(0)){
					locationDao.updateLocationIsEmpty(Integer.parseInt(locationId));
				}
			}
			ProductLocationDetail productLocationDetail = new ProductLocationDetail(); 
			productLocationDetail.setPl_id(productLocation.getPl_id());
			productLocationDetail.setChange_quantity(-quantity);
			productLocationDetail.setTask_id(Integer.parseInt(taskId));
			productLocationDetail.setDescription("WEB下架补货操作");
			productLocationDetail.setCreated_user(actionUser);
			productLocationDetail.setLast_updated_user(actionUser);
			productLocationDetailDao.insert(productLocationDetail);
			
			
			// 3.增加库存
			paramsForPlMap = new HashMap<String,Object>();
			paramsForPlMap.put("locationBarcode", toLocationBarcode);
			//paramsForPlMap.put("locationType", "REPLENISHMENT");
			paramsForPlMap.put("productId", productId);
			paramsForPlMap.put("status", status);
			paramsForPlMap.put("validity", validity);
			paramsForPlMap.put("batchSn", batchSn);
			paramsForPlMap.put("serialNumber", serialNumber);
			paramsForPlMap.put("warehouseId", warehouseId);
			Map toBhInfoMap = replenishmentDao.selectProductLocationByLocation(paramsForPlMap);
			Integer plId = null;
			if(WorkerUtil.isNullOrEmpty(toBhInfoMap)){
				productLocation = new ProductLocation();
				productLocation.setLocation_id(Integer.parseInt(locationId2));
				productLocation.setProduct_id(Integer.parseInt(productId));
				productLocation.setQty_total(quantity);
				productLocation.setQty_reserved(quantity);
				productLocation.setQty_available(quantity);
				productLocation.setProduct_location_status("NORMAL");
				productLocation.setQty_freeze(0);
				productLocation.setStatus(status);
				productLocation.setValidity(validity);
				productLocation.setBatch_sn(batchSn);
				productLocation.setWarehouse_id(warehouseId);
				productLocation.setSerial_number(serialNumber);
				productLocation.setCreated_user(actionUser);
				productLocation.setLast_updated_user(actionUser);
				
				Product product = productDao.selectByPrimaryKey(Integer.parseInt(productId));
				String validityStatus = "NORMAL";
				if("Y".equals(product.getIs_maintain_warranty())){
					validityStatus = productLocation.checkValidityStatus(validity, product.getValidity(), product.getValidity_unit(), product.getWarranty_warning_days(), product.getWarranty_unsalable_days());
				}
				productLocation.setValidity_status(validityStatus);
				productLocationDao.insert(productLocation);
				plId = productLocation.getPl_id();
			}else{
				if(!"NORMAL".equals(productLocation.getProduct_location_status())){
					throw new RuntimeException("上架失败,原因：该库位库存不可用！");
				}
				plId = Integer.parseInt(toBhInfoMap.get("pl_id").toString());
				paramsForPlMap.put("plId", plId);
				paramsForPlMap.put("quantity", quantity);
				replenishmentDao.updateReplenishmentFromProductLocationAdd(paramsForPlMap);
			}
			
			// 4.修改补货任务状态
			Map<String,Object> paramsForUpdateTaskMap = new HashMap<String,Object>();
			paramsForUpdateTaskMap.put("taskId", taskId);
			paramsForUpdateTaskMap.put("taskStatus", "FULFILLED");
			paramsForUpdateTaskMap.put("actionUser", actionUser);
			taskDao.updateTaskByTaskId(paramsForUpdateTaskMap);
			
			// 5.修改库位为非空
			locationDao.updateLocationNotEmptyByLocationId(Integer.parseInt(locationId2));
			
			// 6.插入日志
			productLocationDetail = new ProductLocationDetail(); 
			productLocationDetail.setPl_id(plId);
			productLocationDetail.setChange_quantity(quantity);
			productLocationDetail.setTask_id(Integer.parseInt(taskId));
			productLocationDetail.setDescription("WEB补货上架操作");
			productLocationDetail.setCreated_user(actionUser);
			productLocationDetail.setLast_updated_user(actionUser);
			productLocationDetailDao.insert(productLocationDetail);
			
			UserActionTask userActionTask = new UserActionTask();
			userActionTask.setTask_id(Integer.parseInt(taskId));
			userActionTask.setTask_status(Task.TASK_STATUS_FULFILLED);
			userActionTask.setAction_type(Task.TASK_STATUS_FULFILLED);
			userActionTask.setAction_note("WEB完成补货任务");
			userActionTask.setCreated_time(new Date());
			userActionTask.setCreated_user(actionUser);
			userActionTaskDao.insert(userActionTask);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			logger.error("网页版补货完结失败", e);
			throw new RuntimeException("网页版补货完结失败,失败原因:"+e.getMessage());
		}
		
		returnMap.put("result", Response.SUCCESS);
		returnMap.put("note", "success");
		return returnMap;
	}
	
	
	@SuppressWarnings("rawtypes")
	public Map cancelReplenishmentTask(String taskId,String fromLocationBarcode,String cancelReason,String actionUser){
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		
		Task task = taskDao.selectByTaskIdForUpdate(Integer.parseInt(taskId));
		ReentrantLock lock = LockUtil.getReentrantLock("physicalWarehouseId_"
				+ task.getPhysical_warehouse_id() + "_customerId_" + task.getCustomer_id());
		lock.lock();
		try {
			if(Task.TASK_STATUS_FULFILLED.equals(task.getTask_status())){
				returnMap.put("result", Response.FAILURE);
				returnMap.put("success", Boolean.FALSE);
				returnMap.put("note", "任务已完成，不允许取消");
				return returnMap;
			}
			
			if(Task.TASK_STATUS_CANCEL.equals(task.getTask_status())){
				returnMap.put("result", Response.FAILURE);
				returnMap.put("success", Boolean.FALSE);
				returnMap.put("note", "任务已取消，不允许再次取消");
				return returnMap;
			}
			
			Location location = locationDao.selectLocationIdByLocationBarCode(task.getPhysical_warehouse_id(), fromLocationBarcode, null);
			if(WorkerUtil.isNullOrEmpty(location)){
				returnMap.put("result", Response.FAILURE);
				returnMap.put("success", Boolean.FALSE);
				returnMap.put("note", fromLocationBarcode+"源库位不存在");
				return returnMap;
			}
			
			// 1.修改task状态和取消原因
			Map<String,Object> paramsForCancelMap = new HashMap<String,Object>();
			paramsForCancelMap.put("taskId", taskId);
			paramsForCancelMap.put("cancelReason", cancelReason);
			paramsForCancelMap.put("actionUser", actionUser);
			paramsForCancelMap.put("taskStatus", Task.TASK_STATUS_CANCEL);
			int col = taskDao.updateTaskCancel(paramsForCancelMap);
			if(col > 0 ){
				String status = "";
				if("2".equals(cancelReason)){
					status = "EXCEPTION_NUM";
					col = taskDao.updateProductLocationNumForCancel(task.getQuantity(),location.getLocation_id(), task.getProduct_id(), status);
					if(col < 1 ){
						throw new RuntimeException("取消补货任务失败,更新库位库存失败");
					}
					col = taskDao.updateProductLocationNumForCancelByPlId(task.getQuantity(),task.getFrom_pl_id());
				}else if("3".equals(cancelReason)){
					status = "EXCEPTION_QUALITY";
					col = taskDao.updateProductLocationNumForCancel(task.getQuantity(),location.getLocation_id(), task.getProduct_id(), status);
					if(col < 1 ){
						throw new RuntimeException("取消补货任务失败,更新库位库存失败");
					}
					col = taskDao.updateProductLocationNumForCancelByPlId(task.getQuantity(),task.getFrom_pl_id());
				}else if("4".equals(cancelReason)){
					status = "EXCEPTION_NOREASON";
					col = taskDao.updateProductLocationNumForCancelNoReason(task.getQuantity(),task.getFrom_pl_id());
					if(col < 1 ){
						throw new RuntimeException("取消补货任务失败,更新库位库存失败");
					}
				}
				//taskDao.addProductLocationNumForCancel(Integer.parseInt(taskId),status);
				UserActionTask userActionTask = new UserActionTask();
				userActionTask.setTask_id(Integer.parseInt(taskId));
				userActionTask.setTask_status(Task.TASK_STATUS_CANCEL);
				userActionTask.setAction_type(Task.TASK_STATUS_CANCEL);
				userActionTask.setAction_note(task.getOperate_platform()+"取消补货任务");
				userActionTask.setCreated_time(new Date());
				userActionTask.setCreated_user(actionUser);
				userActionTaskDao.insert(userActionTask);
				
				returnMap.put("result", Response.SUCCESS);
				returnMap.put("success", Boolean.TRUE);
				returnMap.put("note", "取消补货任务成功!");
			}else{
				throw new RuntimeException("取消补货任务失败");
			}
		} catch (NumberFormatException e) {
			throw new RuntimeException("取消补货任务失败"+e.getMessage());
		} finally {
			lock.unlock();
		}
		return returnMap;
	}
	
	
	@SuppressWarnings("rawtypes")
	public Map getReplenishmentTaskFromRF(String customerId,String taskLevel,Integer physicalWarehouseId,String taskType){
		
		Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        SysUser sysUser = (SysUser) session.getAttribute("currentUser");
		List<WarehouseCustomer> customers = (List<WarehouseCustomer>) session.getAttribute("userCustomers");
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> paramsForSelectMap = new HashMap<String,Object>();
		paramsForSelectMap.put("customerId", customerId);
		paramsForSelectMap.put("physicalWarehouseId", physicalWarehouseId);
		paramsForSelectMap.put("taskType", taskType);
		paramsForSelectMap.put("taskLevel", taskLevel);
		if("".equals(customerId)){
			paramsForSelectMap.put("customers", customers);
		}
		Map map = taskDao.selectTaskFromRF(paramsForSelectMap);
		if(WorkerUtil.isNullOrEmpty(map)){
			returnMap.put("result", "failure");
			returnMap.put("success", Boolean.FALSE);
			returnMap.put("note", "没有补货任务可获取到");
			returnMap.put("replenishmentTask", null);
		}else{
			Task task = taskDao.selectByTaskIdForUpdate(Integer.parseInt(map.get("task_id").toString()));
			returnMap.put("result", Response.SUCCESS);
			returnMap.put("success", Boolean.TRUE);
			returnMap.put("note", "成功获取到补货任务");
			returnMap.put("replenishmentTask", map);
			Map<String,Object> paramsForUpdateTaskMap = new HashMap<String,Object>();
			paramsForUpdateTaskMap.put("taskId", map.get("task_id"));
			paramsForUpdateTaskMap.put("taskStatus", Task.TASK_STATUS_BINDED);
			paramsForUpdateTaskMap.put("operatePlatform", "RF");
			paramsForUpdateTaskMap.put("actionUser", sysUser.getUsername());
			paramsForUpdateTaskMap.put("firstBindUserId", sysUser.getId());
			paramsForUpdateTaskMap.put("firstBindTime", new Date());
			taskDao.updateTaskByTaskId(paramsForUpdateTaskMap);
		}
		return returnMap;
		
	}
	
	
	@SuppressWarnings("rawtypes")
	public Map getReplenishmentProductFromRF(String fromLocationBarcode,Integer physicalWarehouseId,String taskType,String taskId,String actionUser){
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> paramsForSelectMap = new HashMap<String,Object>();
		paramsForSelectMap.put("fromLocationBarcode", fromLocationBarcode);
		paramsForSelectMap.put("physicalWarehouseId", physicalWarehouseId);
		paramsForSelectMap.put("taskType", taskType);
		paramsForSelectMap.put("taskId", taskId);
		Map map = taskDao.selectProductFromRF(paramsForSelectMap);
		if(WorkerUtil.isNullOrEmpty(map)){
			returnMap.put("result", "failure");
			returnMap.put("success", Boolean.FALSE);
			returnMap.put("note", "该源库位没有商品");
			returnMap.put("replenishmentProduct", null);
		}else{
			returnMap.put("result", Response.SUCCESS);
			returnMap.put("success", Boolean.TRUE);
			returnMap.put("note", "成功获取到源库位的商品");
			returnMap.put("replenishmentProduct", map);
		}
		Map<String,Object> paramsForUpdateTaskMap = new HashMap<String,Object>();
		paramsForUpdateTaskMap.put("taskId", taskId);
		paramsForUpdateTaskMap.put("taskStatus", Task.TASK_STATUS_BINDED);
		paramsForUpdateTaskMap.put("operatePlatform", "RF");
		paramsForUpdateTaskMap.put("actionUser", actionUser);
		taskDao.updateTaskByTaskId(paramsForUpdateTaskMap);
		return returnMap;
	}
	
	// RF补货下架
	@SuppressWarnings("rawtypes")
	public Map offShelfReplenishmentFromRF(String taskId,Integer warehouseId,String locationBarcode,String toLocationBarcode,String productId,String status,String validity,String batchSn,String serialNumber,Integer physicalWarehouseId,Integer quantity,String actionUser){
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		try {
			// 对task进行锁
			Task task = taskDao.selectByTaskIdForUpdate(Integer.parseInt(taskId));
			if(Task.TASK_OPERATE_PLATFORM_WEB.equals(task.getOperate_platform())){
				returnMap.put("result", Response.FAILURE);
				returnMap.put("success", Boolean.FALSE);
				returnMap.put("note", "手持不允许操作网页任务");
				return returnMap;
			}
			if(Task.TASK_STATUS_FULFILLED.equals(task.getTask_status())){
				returnMap.put("result", Response.FAILURE);
				returnMap.put("success", Boolean.FALSE);
				returnMap.put("note", "任务"+taskId+"已完成");
				return returnMap;
			}
			if(Task.TASK_STATUS_UNSHELVE.equals(task.getTask_status())){
				returnMap.put("result", Response.FAILURE);
				returnMap.put("success", Boolean.FALSE);
				returnMap.put("note", "任务"+taskId+"已下架");
				return returnMap;
			}
			if(Task.TASK_STATUS_CANCEL.equals(task.getTask_status())){
				returnMap.put("result", Response.FAILURE);
				returnMap.put("success", Boolean.FALSE);
				returnMap.put("note", "任务"+taskId+"已取消");
				return returnMap;
			}
			// 1.查找补货标签
			int location_id = 0;
			if(WorkerUtil.isNullOrEmpty(locationDao.selectLocationIdByLocationBarCode(physicalWarehouseId,toLocationBarcode,"Y"))){
				returnMap.put("result", Response.FAILURE);
				returnMap.put("success", Boolean.FALSE);
				returnMap.put("note", "补货标签不存在或已绑定补货任务!");
				return returnMap;
			}else{
				Location location = locationDao.selectLocationIdByLocationBarCode(physicalWarehouseId,toLocationBarcode,"Y");
				location_id = location.getLocation_id();
				locationDao.updateLocationNotEmptyByLocationId(location_id);
			}
			Location fromLocation = locationDao.selectLocationIdByLocationBarCode(physicalWarehouseId,locationBarcode,null);
			
			// 2.查找product_location
			Map<String,Object> paramsForPlMap = new HashMap<String,Object>();
			paramsForPlMap.put("productId", productId);
			paramsForPlMap.put("status", status);
			paramsForPlMap.put("validity", validity);
			paramsForPlMap.put("batchSn", batchSn);
			paramsForPlMap.put("quantity", quantity);
			paramsForPlMap.put("actionUser", actionUser);
			paramsForPlMap.put("serialNumber", serialNumber);
			paramsForPlMap.put("locationId", fromLocation.getLocation_id());
			//ProductLocation productLocation = replenishmentDao.selectProductLocation(paramsForPlMap);
			ProductLocation productLocation = productLocationDao.selectProduLocationById(task.getFrom_pl_id());
			if(WorkerUtil.isNullOrEmpty(productLocation)){
				returnMap.put("result", Response.FAILURE);
				returnMap.put("success", Boolean.FALSE);
				returnMap.put("note", "补货下架失败，没有找到源库位库存！");
				return returnMap;
			}
			
			Location location = new Location();
			location.setPhysical_warehouse_id(physicalWarehouseId);
			location.setLocation_barcode(WorkerUtil.generatorSequence(SequenceUtil.KEY_NAME_BHCODE,"BH",true));
			location.setLocation_type(Location.LOCATION_TYPE_TRANSIT);
			location.setIs_delete("N");
			location.setCreated_user(actionUser);
			inventoryDao.insertLocation(location);
			
			ProductLocation productLocationV2 = new ProductLocation();
			productLocationV2.setLocation_id(location.getLocation_id());
			productLocationV2.setProduct_id(Integer.parseInt(productId));
			productLocationV2.setQty_total(quantity);
			productLocationV2.setQty_reserved(quantity);
			productLocationV2.setQty_available(0);
			productLocationV2.setProduct_location_status("NORMAL");
			productLocationV2.setQty_freeze(0);
			productLocationV2.setStatus(status);
			productLocationV2.setValidity(validity);
			productLocationV2.setBatch_sn(batchSn);
			productLocationV2.setWarehouse_id(warehouseId);
			productLocationV2.setSerial_number(serialNumber);
			productLocationV2.setCreated_user(actionUser);
			productLocationV2.setLast_updated_user(actionUser);
			productLocationDao.insert(productLocationV2);
			int plId = productLocationV2.getPl_id();
			
			LabelReplenishment labelReplenishment = new LabelReplenishment();
			labelReplenishment.setTask_id(Integer.parseInt(taskId));
			labelReplenishment.setLocation_id(location_id);
			labelReplenishment.setFrom_location_barcode(locationBarcode);
			labelReplenishment.setLocation_barcode(toLocationBarcode);
//			labelReplenishment.setProduct_id(Integer.parseInt(productId));
//			labelReplenishment.setQuantity(quantity);
//			labelReplenishment.setStatus(status);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			try {
//				if(!WorkerUtil.isNullOrEmpty(validity))
//					labelReplenishment.setValidity(sdf.parse(validity));
//			} catch (Exception e) {}
//			labelReplenishment.setBatch_sn(batchSn);
//			labelReplenishment.setSerial_number(serialNumber);
			labelReplenishment.setCreated_user(actionUser);
			labelReplenishment.setLast_updated_user(actionUser);
			labelReplenishment.setPl_id(plId);
			labelReplenishmentDao.insert(labelReplenishment);
			
			
			// 2.扣减库存
			paramsForPlMap.put("plId", productLocation.getPl_id());
			replenishmentDao.updateReplenishmentFromProductLocationMinus(paramsForPlMap);
			if(quantity.equals(productLocation.getQty_total())){
				Integer total = productLocationDao.selectProductLocationQtyByLocationId(productLocation.getLocation_id());
				if(total.equals(0)){
					locationDao.updateLocationIsEmpty(productLocation.getLocation_id());
				}
			}
			
			
			// 3.修改补货任务状态
			Map<String,Object> paramsForUpdateTaskMap = new HashMap<String,Object>();
			paramsForUpdateTaskMap.put("taskId", taskId);
			paramsForUpdateTaskMap.put("taskStatus", "UNSHELVE");
			paramsForUpdateTaskMap.put("actionUser", actionUser);
			taskDao.updateTaskByTaskId(paramsForUpdateTaskMap);
			
			// 4.插入日志
			ProductLocationDetail productLocationDetail = new ProductLocationDetail(); 
			productLocationDetail.setPl_id(productLocation.getPl_id());
			productLocationDetail.setChange_quantity(-quantity);
			productLocationDetail.setTask_id(Integer.parseInt(taskId));
			productLocationDetail.setDescription("RF补货下架");
			productLocationDetail.setCreated_user(actionUser);
			productLocationDetail.setLast_updated_user(actionUser);
			productLocationDetailDao.insert(productLocationDetail);
			
			UserActionTask userActionTask = new UserActionTask();
			userActionTask.setTask_id(Integer.parseInt(taskId));
			userActionTask.setTask_status(Task.TASK_STATUS_UNSHELVE);
			userActionTask.setAction_type(Task.TASK_STATUS_UNSHELVE);
			userActionTask.setAction_note("RF补货任务下架");
			userActionTask.setCreated_time(new Date());
			userActionTask.setCreated_user(actionUser);
			userActionTaskDao.insert(userActionTask);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("RF补货下架失败", e);
			throw new RuntimeException("RF补货下架失败，失败原因："+e.getMessage());
		}
		
		returnMap.put("result", Response.SUCCESS);
		returnMap.put("success", Boolean.TRUE);
		returnMap.put("note", "补货下架成功！");
		return returnMap;
	}
	
	// 加载补货 标签信息
	@SuppressWarnings("rawtypes")
	public Map getReplenishmentInfoByCodeFromRF(String bhCode,Integer physicalWarehouseId){
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> paramsForSelectMap = new HashMap<String,Object>();
		paramsForSelectMap.put("bhCode", bhCode);
		paramsForSelectMap.put("physicalWarehouseId", physicalWarehouseId);
		@SuppressWarnings("unused")
		Map bhInfoMap = replenishmentDao.selectReplenishmentByBhCode(paramsForSelectMap);
		if(WorkerUtil.isNullOrEmpty(bhInfoMap)){
			returnMap.put("result", Response.FAILURE);
			returnMap.put("success", Boolean.FALSE);
			returnMap.put("bfInfo", null);
			returnMap.put("note", "补货标签获取到的信息为空");
		}else{
			returnMap.put("result", Response.SUCCESS);
			returnMap.put("success", Boolean.TRUE);
			returnMap.put("bhInfo", bhInfoMap);
			returnMap.put("note", "补货标签成功获取到信息");
		}
		return returnMap;
	}
		
	// RF补货上架
	@SuppressWarnings("rawtypes")
	public Map onShelfReplenishmentFromRF(String bhCode,String barcode,Integer quantity,String toLocationBarcode,Integer physicalWarehouseId,String actionUser,Integer userId){
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		try {
			// 1.根据补货标签找到商品的相关信息
			Map<String,Object> paramsForSelectMap = new HashMap<String,Object>();
			paramsForSelectMap.put("bhCode", bhCode);
			paramsForSelectMap.put("physicalWarehouseId", physicalWarehouseId);
			Map bhInfoMap = replenishmentDao.selectReplenishmentByBhCode(paramsForSelectMap);
			if(WorkerUtil.isNullOrEmpty(bhInfoMap)){
				returnMap.put("result", Response.FAILURE);
				returnMap.put("success", Boolean.FALSE);
				returnMap.put("note", "不存在该补货标签");
				return returnMap;
			}
			Integer bhInfo_quantity = Integer.parseInt(bhInfoMap.get("quantity").toString());
			Integer warehouse_id = Integer.parseInt(bhInfoMap.get("warehouse_id").toString());
			Integer bhInfo_task_id = Integer.parseInt(bhInfoMap.get("task_id").toString());
			String product_id = WorkerUtil.isNullOrEmpty(bhInfoMap.get("product_id"))?"":bhInfoMap.get("product_id").toString();
			String status = WorkerUtil.isNullOrEmpty(bhInfoMap.get("status"))?"":bhInfoMap.get("status").toString();
			String validity = WorkerUtil.isNullOrEmpty(bhInfoMap.get("validity"))?"":bhInfoMap.get("validity").toString();
			String batch_sn = WorkerUtil.isNullOrEmpty(bhInfoMap.get("batch_sn"))?"":bhInfoMap.get("batch_sn").toString();
			String serial_number = WorkerUtil.isNullOrEmpty(bhInfoMap.get("serial_number"))?"":bhInfoMap.get("serial_number").toString();
			String bhInfo_barcode = WorkerUtil.isNullOrEmpty(bhInfoMap.get("barcode"))?"":bhInfoMap.get("barcode").toString();
			//String location_id = WorkerUtil.isNullOrEmpty(bhInfoMap.get("location_id"))?"":bhInfoMap.get("location_id").toString();
			String from_location_barcode = WorkerUtil.isNullOrEmpty(bhInfoMap.get("from_location_barcode"))?"":bhInfoMap.get("from_location_barcode").toString();
			String pl_id = WorkerUtil.isNullOrEmpty(bhInfoMap.get("pl_id"))?"0":bhInfoMap.get("pl_id").toString();
			if(!barcode.equals(bhInfo_barcode)){
				returnMap.put("result", Response.FAILURE);
				returnMap.put("success", Boolean.FALSE);
				returnMap.put("note", "补货上架扫描的商品条码有误");
				return returnMap;
			}
			if(!bhInfo_quantity.equals(quantity)){
				returnMap.put("result", Response.FAILURE);
				returnMap.put("success", Boolean.FALSE);
				returnMap.put("note", "补货上架数量有误");
				return returnMap;
			}
			
			List<Map> locationMap = locationDao.selectLocationIdByLocationBarCodeV2(physicalWarehouseId,toLocationBarcode);
			if(!WorkerUtil.isNullOrEmpty(locationMap)){
				boolean flag = true;
				for(Map map : locationMap){
					String product_id2 = WorkerUtil.isNullOrEmpty(map.get("product_id")) ? "" : map.get("product_id").toString();
					String validity2 = WorkerUtil.isNullOrEmpty(map.get("validity")) ? "" : map.get("validity").toString();
					String status2 = WorkerUtil.isNullOrEmpty(map.get("status")) ? "" : map.get("status").toString();
					String batchSn2 = WorkerUtil.isNullOrEmpty(map.get("batch_sn")) ? "" : map.get("batch_sn").toString();
					Integer warehouseId2 = WorkerUtil.isNullOrEmpty(map.get("warehouse_id")) ? null : Integer.parseInt(map.get("warehouse_id").toString());
					String can_mix_product = WorkerUtil.isNullOrEmpty(map.get("can_mix_product"))?"0":map.get("can_mix_product").toString();
					String can_mix_batch = WorkerUtil.isNullOrEmpty(map.get("can_mix_batch"))?"0":map.get("can_mix_batch").toString();
					String qty_total = WorkerUtil.isNullOrEmpty(map.get("qty_total"))?"0":map.get("qty_total").toString();
					
					if (!"".equals(product_id2) && !"0".equals(qty_total)) {
						if("0".equals(can_mix_product) && !product_id.equals(product_id2)){
							returnMap.put("result", Response.FAILURE);
							returnMap.put("success", Boolean.FALSE);
							returnMap.put("note", "该库位不允许混放商品");
							flag = false;
							break;
						}
						if("0".equals(can_mix_batch) &&
								(!validity2.equals(validity) || !status2.equals(status) || !batchSn2.equals(batch_sn))){
							returnMap.put("result", Response.FAILURE);
							returnMap.put("success", Boolean.FALSE);
							returnMap.put("note", "该库位不允许混放商品批次");
							flag = false;
							break;
						}
						if(product_id.equals(product_id2) && !warehouseId2.equals(warehouse_id)){
							returnMap.put("result", Response.FAILURE);
							returnMap.put("success", Boolean.FALSE);
							returnMap.put("note", toLocationBarcode + "库位商品渠道不同不允许存放！请重新输入！");
							flag = false;
							break;
						}
					}
				}
				
				if(Boolean.FALSE.equals(flag)){
					return returnMap;
				}
//				else if(locationMap.size() == 1 && (!validity.equals(locationMap.get(0).get("validity").toString())||!status.equals(locationMap.get(0).get("status").toString()))){
//					returnMap.put("result", Response.FAILURE);
//					returnMap.put("success", Boolean.FALSE);
//					returnMap.put("note", "该库位不允许混放商品批次");
//					return returnMap;
//				}
			}
			Location fromLocation = locationDao.selectLocationIdByLocationBarCode(physicalWarehouseId,from_location_barcode,"");
			Location toLocation = locationDao.selectLocationIdByLocationBarCode(physicalWarehouseId,toLocationBarcode,"");
			if(WorkerUtil.isNullOrEmpty(toLocation)){
				returnMap.put("result", Response.FAILURE);
				returnMap.put("success", Boolean.FALSE);
				returnMap.put("note", "目标库位不存在");
				return returnMap;
			}
			if ((Location.LOCATION_TYPE_STOCK.equals(fromLocation
					.getLocation_type()) && Location.LOCATION_TYPE_BOX_PICK
					.equals(toLocation.getLocation_type()))
					||	(Location.LOCATION_TYPE_STOCK.equals(fromLocation
							.getLocation_type()) && Location.LOCATION_TYPE_PIECE_PICK
							.equals(toLocation.getLocation_type()))
					|| (Location.LOCATION_TYPE_BOX_PICK.equals(fromLocation
							.getLocation_type()) && Location.LOCATION_TYPE_PIECE_PICK
							.equals(toLocation.getLocation_type()))) {
				;
			} else {
				returnMap.put("result", Response.FAILURE);
				returnMap.put("success", Boolean.FALSE);
				returnMap.put("note", "源库位库位类型或目标库位类型有误");
				return returnMap;
			}
			Integer location_id = toLocation.getLocation_id();
			// 锁task
			Task task = taskDao.selectByTaskIdForUpdate(Integer.parseInt(bhInfo_task_id.toString()));
			// 2.增加库存
			Map<String,Object> paramsForPlMap = new HashMap<String,Object>();
			paramsForPlMap.put("locationBarcode", toLocationBarcode);
			paramsForPlMap.put("productId", product_id);
			paramsForPlMap.put("status", status);
			paramsForPlMap.put("validity", validity);
			paramsForPlMap.put("batchSn", batch_sn);
			paramsForPlMap.put("serialNumber", serial_number);
			paramsForPlMap.put("warehouseId", warehouse_id);
			paramsForPlMap.put("actionUser", actionUser);
			Map toBhInfoMap = replenishmentDao.selectProductLocationByLocation(paramsForPlMap);
			paramsForPlMap.put("plId", pl_id);
			paramsForPlMap.put("quantity", bhInfo_quantity);
			// 下架
			replenishmentDao.updateReplenishmentFromProductLocationMinus(paramsForPlMap);
			Integer plId = null;
			if(WorkerUtil.isNullOrEmpty(toBhInfoMap)){
				ProductLocation productLocation = new ProductLocation();
				productLocation.setLocation_id(location_id);
				productLocation.setProduct_id(Integer.parseInt(product_id));
				productLocation.setQty_total(bhInfo_quantity);
				productLocation.setQty_reserved(bhInfo_quantity);
				productLocation.setQty_available(bhInfo_quantity);
				productLocation.setProduct_location_status("NORMAL");
				productLocation.setQty_freeze(0);
				productLocation.setStatus(status);
				productLocation.setValidity(validity);
				productLocation.setBatch_sn(batch_sn);
				productLocation.setWarehouse_id(warehouse_id);
				productLocation.setSerial_number(serial_number);
				productLocation.setCreated_user(actionUser);
				productLocation.setLast_updated_user(actionUser);
				
				Product product = productDao.selectByPrimaryKey(Integer.parseInt(product_id));
				String validityStatus = "NORMAL";
				if("Y".equals(product.getIs_maintain_warranty())){
					validityStatus = productLocation.checkValidityStatus(validity, product.getValidity(), product.getValidity_unit(), product.getWarranty_warning_days(), product.getWarranty_unsalable_days());
				}
				productLocation.setValidity_status(validityStatus);
				productLocationDao.insert(productLocation);
				plId = productLocation.getPl_id();
			}else{
				if(!"NORMAL".equals(toBhInfoMap.get("product_location_status").toString())){
					throw new RuntimeException("上架失败,原因：该库位库存不可用！");
				}
				plId = Integer.parseInt(toBhInfoMap.get("pl_id").toString());
				paramsForPlMap.put("plId", plId);
				paramsForPlMap.put("quantity", bhInfo_quantity);
				replenishmentDao.updateReplenishmentFromProductLocationAdd(paramsForPlMap);
			}
			
			// 3.修改补货任务状态
			Map<String,Object> paramsForUpdateTaskMap = new HashMap<String,Object>();
			paramsForUpdateTaskMap.put("taskId", bhInfo_task_id);
			paramsForUpdateTaskMap.put("taskStatus", Task.TASK_STATUS_FULFILLED);
			paramsForUpdateTaskMap.put("actionUser", actionUser);
			paramsForUpdateTaskMap.put("secondBindUserId", userId);
			paramsForUpdateTaskMap.put("secondBindTime", new Date());
			paramsForUpdateTaskMap.put("toLocationId", toLocation.getLocation_id());
			taskDao.updateTaskByTaskId(paramsForUpdateTaskMap);
			
			// 4.修改库位为非空
			locationDao.updateLocationNotEmptyByLocationId(location_id);
			
			// 5.增加日志
			ProductLocationDetail productLocationDetail = new ProductLocationDetail(); 
			productLocationDetail.setPl_id(plId);
			productLocationDetail.setChange_quantity(quantity);
			productLocationDetail.setTask_id(bhInfo_task_id);
			productLocationDetail.setDescription("RF补货上架");
			productLocationDetail.setCreated_user(actionUser);
			productLocationDetail.setLast_updated_user(actionUser);
			productLocationDetailDao.insert(productLocationDetail);
			
			UserActionTask userActionTask = new UserActionTask();
			userActionTask.setTask_id(bhInfo_task_id);
			userActionTask.setTask_status(Task.TASK_STATUS_FULFILLED);
			userActionTask.setAction_type(Task.TASK_STATUS_FULFILLED);
			userActionTask.setAction_note("RF补货任务上架");
			userActionTask.setCreated_time(new Date());
			userActionTask.setCreated_user(actionUser);
			userActionTaskDao.insert(userActionTask);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("RF补货上架失败", e);
			throw new RuntimeException("RF补货上架失败，失败原因："+e.getMessage());
		}
		
		returnMap.put("result", Response.SUCCESS);
		returnMap.put("success", Boolean.TRUE);
		returnMap.put("note", "success");
		return returnMap;
	}
	
	@Override
	public HashMap<String, Object> getTrayBarcodeForBH(Integer number) {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		ArrayList<String> codelist = new ArrayList<String>();
		String username = (String) SecurityUtils.getSubject().getPrincipal();
		if (WorkerUtil.isNullOrEmpty(username)) {
			resultMap.put("error", "检查是否登录或者会话已过期，重新登录");
			resultMap.put("success", false);
			return resultMap;
		}
		for (int i = 0; i < number; i++) {
			String tag = WorkerUtil.generatorSequence(
					SequenceUtil.KEY_NAME_TAGCODE, "BH",true);
			codelist.add(tag);
			logger.info("replenishment getTrayBarcodeForBH WorkerUtil.generatorSequence tag:" + tag);
		}

		if (WorkerUtil.isNullOrEmpty(codelist) || codelist.size() != number) {
			resultMap.put("error", "生成补货标签条码异常");
			resultMap.put("success", false);
			return resultMap;
		}

		// 检查数据库中是否已经存在相同的补货标签条码
		int sameCodeNum = replenishmentDao.getSameBHCodeNum(codelist);
		if (sameCodeNum > 0) {
			resultMap.put("error", "生成补货标签码托条码异常，条码已经存在过");
			resultMap.put("success", false);
			return resultMap;
		}
		
		Warehouse currentPhysicalWarehouse = (Warehouse) SecurityUtils.getSubject().getSession().getAttribute("currentPhysicalWarehouse");
		for (String code : codelist) {
			Location location = new Location();
			location.setLocation_barcode(code);
			location.setPhysical_warehouse_id(currentPhysicalWarehouse.getPhysical_warehouse_id());
			location.setLocation_type(Location.LOCATION_TYPE_REPLENISH_SEQ);
			location.setIs_delete("N");
			location.setIs_empty("Y");
			location.setCreated_time(new Date());
			location.setCreated_user(username);
			location.setLast_updated_time(new Date());
			location.setLast_updated_user(username);
			locationDao.insertBHcode(location);
		}

		resultMap.put("result", codelist.toArray());
		resultMap.put("success", true);

		return resultMap;
	}
	
	@Override
	public List<Integer> selectGeneralCustomerByPhysicalWarehouseId(
			Integer physicalWarehouseId) {
		return replenishmentDao.selectGeneralCustomerByPhysicalWarehouseId(physicalWarehouseId);
	} 

	/**
	 * 
	 * @param queueId
	 *            queueId
	 * @param productId
	 *            productId
	 * @param boxPiece
	 *            boxPiece
	 * @param taskType
	 *            任务类型
	 * @param batchPickList
	 *            波此单号
	 * @param physical_warehouse_id
	 *            物理仓库id
	 * @param customer_id
	 *            货主id
	 * @param pickOrderGoodsSum
	 *            商品总数
	 * @param pickOrderGoodMap
	 *            每个波此单商品 集合
	 * @param goodsIdList
	 *            商品id
	 * @param level
	 *            紧急补货级别
	 * @param configReplenishmentMap
	 *            普通补货配置
	 * @param productList
	 *            商品信息
	 */
	public String lockReplenishJobByWarehouseCustomer(Integer productId, String boxPiece, String taskType,
			List<Integer> batchPickList, Integer physical_warehouse_id,
			Integer customer_id, List<Map> pickOrderGoodsSum,
			Map<String, List<Map>> pickOrderGoodMap, List<Integer> goodsIdList,
			Map<String, ConfigReplenishment> configReplenishmentMap, int level,
			List<Product> productList,Integer plId, Integer moveNum, Integer toLocationId) {
		String queueStr = "";
		ReentrantLock lock = LockUtil.getReentrantLock("physicalWarehouseId_"
				+ physical_warehouse_id + "_customerId_" + customer_id);
//		lock.lock(); //用tryLock替换掉
		if(lock.tryLock()){//tryLock()方法是有返回值的，它表示用来尝试获取锁，如果获取成功，则返回true，如果获取失败（即锁已被其他线程获取），则返回false			
			try {
				logger.info(taskType + "(physicalWarehouseId :"
						+ physical_warehouse_id + ",customerId :" + customer_id
						+ ")createReplenishJob  start ");
				if ("General".equalsIgnoreCase(taskType)) {// 一般补货
					queueStr = generalReplenishmentBiz.generalReplenishTask(physical_warehouse_id,
							customer_id, boxPiece, productId);
				}else if("Improve".equalsIgnoreCase(taskType)){
					//外面重用的Integer类型的参数  toLocation在此方法中传入参数时间为warehouse_id    里面实际为渠道
					queueStr = this.improve(physical_warehouse_id,
							customer_id,boxPiece,pickOrderGoodsSum,taskType,batchPickList,goodsIdList,toLocationId);
				}else if("MoveStock".equalsIgnoreCase(taskType)){
					queueStr = this.moveStock(physical_warehouse_id,
							customer_id,plId,moveNum,toLocationId,taskType);
				}else if("TransferStock".equalsIgnoreCase(taskType)){
					queueStr = this.moveStock(physical_warehouse_id,
							customer_id,plId,moveNum,toLocationId,taskType);
				}
				//外面重用的Integer类型的参数  toLocation在库存   分配系列功能    里面实际为渠道
				else if("PACK".equalsIgnoreCase(taskType)){
					queueStr = this.allotPackProductLocation(physical_warehouse_id,
							customer_id,productId,toLocationId);
				}
				else if("UNPACK".equalsIgnoreCase(taskType)){
					queueStr = this.allotUnPackProductLocation(physical_warehouse_id,
							customer_id,productId,toLocationId);
				}
				else if("Allot_2B11".equalsIgnoreCase(taskType)||"Allot11".equalsIgnoreCase(taskType)){
					this.allot11ProductLocation(taskType, batchPickList, 
							physical_warehouse_id, customer_id,pickOrderGoodsSum, 
							pickOrderGoodMap, goodsIdList, configReplenishmentMap, 
							level, productList,toLocationId);
				}
				else if("gt_normal11".equalsIgnoreCase(taskType)||"gt_defective11".equalsIgnoreCase(taskType)){
					this.allotGt11ProductLocation(taskType, batchPickList, 
							physical_warehouse_id, customer_id,pickOrderGoodsSum, 
							pickOrderGoodMap, goodsIdList, configReplenishmentMap, 
							level, productList,toLocationId);
				}
				else{										
					String allot2BMark =configDao.getConfigValueByFrezen(physical_warehouse_id,customer_id,"OPEN_ALLOT_BATCH");
					
					String gtMark=configDao.getConfigValueByFrezen(physical_warehouse_id,customer_id,"OPEN_GT_BATCH");
					
					if("Allot_2X".equalsIgnoreCase(taskType)){// 普通波此单："Allot_2X"
						this.allot2XProductLocation(taskType, batchPickList,
								physical_warehouse_id, customer_id, pickOrderGoodsSum,
								pickOrderGoodMap, goodsIdList, configReplenishmentMap,
								level, productList,toLocationId);
					}
					else if(("Allot_2B".equalsIgnoreCase(taskType))&&("1".equalsIgnoreCase(allot2BMark))){
						this.allotProductLocationInPhysical(taskType, batchPickList,
								physical_warehouse_id, customer_id, pickOrderGoodsSum,
								pickOrderGoodMap, goodsIdList, configReplenishmentMap,
								level, productList,toLocationId);
					}
					else if(("gt_normal".equalsIgnoreCase(taskType)||"gt_defective".equalsIgnoreCase(taskType))&&("1".equalsIgnoreCase(gtMark))){
						this.allotGtProductLocationInPhysical(taskType, batchPickList,
								physical_warehouse_id, customer_id, pickOrderGoodsSum,
								pickOrderGoodMap, goodsIdList, configReplenishmentMap,
								level, productList,toLocationId);
					}	
					else// 普通波此单："Allot"  2B波次单:"Allot_2B"  -gt订单分配库存 "gt_normal"  "gt_defective"
					{
						this.allotProductLocation(taskType, batchPickList,
								physical_warehouse_id, customer_id, pickOrderGoodsSum,
								pickOrderGoodMap, goodsIdList, configReplenishmentMap,
								level, productList,toLocationId);
					}
				}
				
				logger.info(taskType + "(physicalWarehouseId :" + physical_warehouse_id + ",customerId :" + customer_id
						+ ")createReplenishJob end");
			} 
			catch (DataIntegrityViolationException e) {
				logger.info("DataIntegrityViolationException : "+e.getMessage());
				throw new RuntimeException(e.getMessage());
			}
			catch (Exception e) {
 				logger.error("lock message : "+e.getMessage());
				logger.info(taskType + "(physicalWarehouseId :"
						+ physical_warehouse_id + ",customerId :" + customer_id
						+ ")createReplenishJob got exception:" + e.getMessage());
				throw new RuntimeException(e.getMessage());
			}  finally {
				// 释放锁
				lock.unlock();
			}
		}else{
			queueStr = "LOCKING";
		}
		return queueStr;

	}

	/**
	 * 拆包库存分配
	 * @param physical_warehouse_id
	 * @param customer_id
	 * @param orderId //按指定id进行库存分配
	 * @param warehouse_id 
	 * @return
	 */
	private String allotUnPackProductLocation(Integer physical_warehouse_id,
			Integer customer_id, Integer orderId, Integer warehouse_id) {
		logger.info("allotUnPackProductLocation start"+"(physicalWarehouseId :"
				+ physical_warehouse_id + ",customerId :" + customer_id);
		
		List<OrderPrepack> orderPrepackForUpdatelist=new ArrayList<OrderPrepack>();
		if(0-orderId.intValue()==0){
			orderPrepackForUpdatelist=orderPrepackDao.selectOrderPrePackageForUpdate(physical_warehouse_id,customer_id,warehouse_id,"UNPACK");
			if(WorkerUtil.isNullOrEmpty(orderPrepackForUpdatelist)){
				logger.info("allotUnPackProductLocation " + "(physicalWarehouseId :"
						+ physical_warehouse_id + ",customerId :" + customer_id
						+ " orderPrepackForUpdatelist null");
				return "success";
			}
			orderPrepackDao.selectOrderPrePackageByIdListForUpdate(orderPrepackForUpdatelist);
		}
		//手动触发
		else {
			orderPrepackForUpdatelist=orderPrepackDao.selectOrderPrePackageForUpdateById(orderId);
			if(WorkerUtil.isNullOrEmpty(orderPrepackForUpdatelist)){
				logger.info("0001");
				return "0001";
			}
			
			OrderPrepack orpr = orderPrepackForUpdatelist.get(0);
			if(!("RESERVE_FAILED".equalsIgnoreCase(orpr.getStatus())||"INIT".equalsIgnoreCase(orpr.getStatus()))){
				logger.info("0002");
				return "0002";
			}
		}
		
		List<Integer> productIdList=new ArrayList<Integer>();
		List<Integer> orderIdList=new ArrayList<Integer>();
		for(OrderPrepack op:orderPrepackForUpdatelist){
			if(!(op.getStatus().equalsIgnoreCase("INIT")||op.getStatus().equalsIgnoreCase("RESERVE_FAILED"))){
				logger.info("allotUnPackProductLocation " + "(physicalWarehouseId :"
						+ physical_warehouse_id + ",customerId :" + customer_id
						+op.getOrder_id() +" status error");
				return "status error";
			}
			orderIdList.add(op.getOrder_id());
			productIdList.add(op.getPrepackage_product_id());
		}
		
		List<ProductLocation> productlocationList = productLocationDao
				.selectProductlocationListV3(physical_warehouse_id,warehouse_id,
						productIdList, "('PIECE_PICK_LOCATION','BOX_PICK_LOCATION','RETURN_LOCATION','STOCK_LOCATION')","NORMAL");
		
		
		// 每个商品的库存信息

		Map<String, List<ProductLocation>> productlocationListMap = new HashMap<String, List<ProductLocation>>();

		// 求出每种商品的退货区可用数量
		Map<String, Integer> availableMap = new HashMap<String, Integer>();

		cutProductLocationByProductId(productlocationListMap,
				productIdList, productlocationList);

		calculateProductAvailable(productIdList, productlocationListMap,
				availableMap);
		
		java.util.Date now = new java.util.Date();
		String createTime = DateUtils.getStringTime(now);
		
		// 记录任务
		List<Task> taskList = new ArrayList<Task>();
		// 记录库存信息
		List<ProductLocation> productlocationListUpdate = new ArrayList<ProductLocation>();
		//记录波此单操作日志
		List<UserActionOrderPrepack> userActionOrderPrepackList = new ArrayList<UserActionOrderPrepack>();

		//记录的任务操作日志
		List<UserActionTask> userActionTaskList=new ArrayList<UserActionTask>();
		
		List<Integer> orderPrepackYList = new ArrayList<Integer>();
		List<Integer> orderPrepackEList = new ArrayList<Integer>();
		
		for(OrderPrepack op:orderPrepackForUpdatelist){
			int order_id=op.getOrder_id();
			int qty_need=op.getQty_need();
			int product_id=op.getPrepackage_product_id();
			int available = availableMap.get(product_id + "") == null ? 0
					: Integer.parseInt(availableMap.get(
							product_id + "").toString());
			
			if(qty_need<=available){
				List<ProductLocation> productlocationListTemp = productlocationListMap.get(product_id+"");

				
				ProductLocation plComparator=new ProductLocation();
				Collections.sort(productlocationListTemp,plComparator);

				availableMap.put(product_id + "", available
						- qty_need);
				doCheckQualityAllot("PACK",productlocationListTemp,
						order_id, qty_need,
						physical_warehouse_id, customer_id, createTime,
						taskList, productlocationListUpdate,product_id);
				
				orderPrepackYList.add(order_id);
				UserActionOrderPrepack userActionOrderPrepack=new UserActionOrderPrepack();
				userActionOrderPrepack.setOrder_id(order_id);
				userActionOrderPrepack.setOrder_status("RESERVED");
				userActionOrderPrepack.setCreated_user("SYSTEM");
				userActionOrderPrepack.setCreated_time(new Date());
				userActionOrderPrepack.setAction_type("UN PREPACK ALLOT");
				userActionOrderPrepack.setAction_note("UN 预打包库存分配成功");
				userActionOrderPrepackList.add(userActionOrderPrepack);
			}else if(available>0){
				List<ProductLocation> productlocationListTemp = productlocationListMap.get(product_id+"");

				
				ProductLocation plComparator=new ProductLocation();
				Collections.sort(productlocationListTemp,plComparator);

				availableMap.put(product_id + "", 0);
				doCheckQualityAllot("PACK",productlocationListTemp,
						order_id, available,
						physical_warehouse_id, customer_id, createTime,
						taskList, productlocationListUpdate,product_id);
				
				orderPrepackYList.add(order_id);
				UserActionOrderPrepack userActionOrderPrepack=new UserActionOrderPrepack();
				userActionOrderPrepack.setOrder_id(order_id);
				userActionOrderPrepack.setOrder_status("RESERVED");
				userActionOrderPrepack.setCreated_user("SYSTEM");
				userActionOrderPrepack.setCreated_time(new Date());
				userActionOrderPrepack.setAction_type("UN PREPACK ALLOT");
				userActionOrderPrepack.setAction_note("UN 预打包库存分配成功");
				userActionOrderPrepackList.add(userActionOrderPrepack);
			}else{
				orderPrepackEList.add(order_id);
				UserActionOrderPrepack userActionOrderPrepack=new UserActionOrderPrepack();
				userActionOrderPrepack.setOrder_id(order_id);
				userActionOrderPrepack.setOrder_status("RESERVE_FAILED");
				userActionOrderPrepack.setCreated_user("SYSTEM");
				userActionOrderPrepack.setCreated_time(new Date());
				userActionOrderPrepack.setAction_type("UN PREPACK ALLOT");
				userActionOrderPrepack.setAction_note("UN 预打包库存分配失败");
				userActionOrderPrepackList.add(userActionOrderPrepack);
			}
			
		}

		
		if (!WorkerUtil.isNullOrEmpty(taskList)) {
			
			for(Task task:taskList){
				taskDao.insertTask(task);
				
				UserActionTask userActionTask = new UserActionTask();
				userActionTask.setTask_id(task.getTask_id());
				userActionTask.setAction_note("创建");
				userActionTask.setAction_type(Task.TASK_STATUS_INIT);
				userActionTask.setTask_status("INIT");
				userActionTask.setCreated_user("SYSTEM");
				userActionTask.setCreated_time(new Date());
				userActionTaskList.add(userActionTask);	
			}
			
			//taskDao.insertTaskList(taskList);
		}
		
		if (!WorkerUtil.isNullOrEmpty(userActionTaskList)) {
			userActionTaskDao.insertList(userActionTaskList);
		}
		// 更新库存信息
		if (!WorkerUtil.isNullOrEmpty(productlocationListUpdate)) {
			//归并其中一部分主键相同的更新记录（使用 qty_available - ${item2.qty_available}）所以分配里面记录的变化量应该是差异值，且未正数
			Map<Integer,ProductLocation> updatePlMap =new HashMap<Integer,ProductLocation>();
			List<ProductLocation> updatePlList=new ArrayList<ProductLocation>();
			for(ProductLocation pl:productlocationListUpdate){
				//如果没有则加到记录中
				if(updatePlMap.get(pl.getPl_id())==null){
					updatePlMap.put(pl.getPl_id(), pl);
					updatePlList.add(pl);
				}else{
					ProductLocation pl2=updatePlMap.get(pl.getPl_id());
					pl2.setQty_available(pl2.getQty_available()+pl.getQty_available());
				}
			}
			
			productLocationDao
				.updateProductLocationByAllotJob(updatePlList);
		}
		
		if(!WorkerUtil.isNullOrEmpty(userActionOrderPrepackList)){
			userActionOrderPrepackDao.batchInsert(userActionOrderPrepackList);
		}
		// 更新波次单状态
		if (!WorkerUtil.isNullOrEmpty(orderPrepackEList)) {
			orderPrepackDao.batchUpdateStatus(orderPrepackEList, "RESERVE_FAILED");
		}
		if (!WorkerUtil.isNullOrEmpty(orderPrepackYList)) {
			orderPrepackDao.batchUpdateStatus(orderPrepackYList, "RESERVED");
		}
	
		logger.info("allotUnPackProductLocation end"+"(physicalWarehouseId :"
				+ physical_warehouse_id + ",customerId :" + customer_id);
		
		if(0-orderId.intValue()!=0&&(!WorkerUtil.isNullOrEmpty(orderPrepackEList))){
			return "false";
		}
		
		return "success";
	}
	/**
	 * 预打包库存分配
	 * @param physical_warehouse_id
	 * @param customer_id
	 * @param orderId 
	 * @param warehouse_id 
	 * @return
	 */
	private String allotPackProductLocation(Integer physical_warehouse_id,
			Integer customer_id, Integer orderId, Integer warehouse_id) {
		logger.info("allotPackProductLocation start"+"(physicalWarehouseId :"
				+ physical_warehouse_id + ",customerId :" + customer_id);
		//打包加工单 商品信息
		List<Map> prePacklist= new ArrayList<Map>();
		if(0-orderId.intValue()==0){
			prePacklist= orderPrepackDao.getAllPackProduct(physical_warehouse_id,customer_id,warehouse_id,"PACK"); 
			if(WorkerUtil.isNullOrEmpty(prePacklist)){
				logger.info("allotPackProductLocation " + "(physicalWarehouseId :"
						+ physical_warehouse_id + ",customerId :" + customer_id
						+ " prePacklist null  scheduleJob");
				return "success";
			}
		}
		else{
			prePacklist= orderPrepackDao.selectOrderPrePackagePackById(orderId); 
			if(WorkerUtil.isNullOrEmpty(prePacklist)){
				logger.info("allotPackProductLocation " + "(physicalWarehouseId :"
						+ physical_warehouse_id + ",customerId :" + customer_id
						+ " prePacklist null  button");
				return "0001";
			}
		}
		
		//每个打包加工单商品信息
		Map<Integer,List<Map>> prePackMap=new HashMap<Integer,List<Map>>();
		//商品信息
		List<Integer> productIdList=new ArrayList<Integer>();
		List<Integer> orderIdList=new ArrayList<Integer>();
				
		for(Map m:prePacklist){
			Integer order_id=null==m.get("order_id") ? 0:Integer.parseInt(m.get("order_id").toString());
			Integer product_id=null==m.get("product_id") ? 0:Integer.parseInt(m.get("product_id").toString());
			orderIdList.add(order_id);
			productIdList.add(product_id);
			if(null==prePackMap.get(order_id)){
				List<Map> li=new ArrayList<Map>();
				li.add(m);
				prePackMap.put(order_id, li);
			}else{
				List<Map> li=prePackMap.get(order_id);
				li.add(m);
			}
		}
		//加数据库锁  别让其他订单操作了order_prePack
		if(!WorkerUtil.isNullOrEmpty(orderIdList)){
			List<OrderPrepack> orderPrepackForUpdatelist=orderPrepackDao.selectProductPrePackageForUpdate(orderIdList);
			for(OrderPrepack op:orderPrepackForUpdatelist){
				if(!(op.getStatus().equalsIgnoreCase("INIT")||op.getStatus().equalsIgnoreCase("RESERVE_FAILED"))){
					logger.info("allotPackProductLocation " + "(physicalWarehouseId :"
							+ physical_warehouse_id + ",customerId :" + customer_id
							+op.getOrder_id() +" status error");
					return "status error";
				}
			}
		}
						
		List<ProductLocation> productlocationList = productLocationDao
				.selectProductlocationListV3(physical_warehouse_id,warehouse_id,
						productIdList, "('PIECE_PICK_LOCATION','BOX_PICK_LOCATION','RETURN_LOCATION','STOCK_LOCATION')","NORMAL");
		
		
		// 每个商品的库存信息

		Map<String, List<ProductLocation>> productlocationListMap = new HashMap<String, List<ProductLocation>>();

		// 求出每种商品的退货区可用数量
		Map<String, Integer> availableMap = new HashMap<String, Integer>();

		cutProductLocationByProductId(productlocationListMap,
				productIdList, productlocationList);

		calculateProductAvailable(productIdList, productlocationListMap,
				availableMap);
		
		java.util.Date now = new java.util.Date();
		String createTime = DateUtils.getStringTime(now);
		
		// 记录任务
		List<Task> taskList = new ArrayList<Task>();
		// 记录库存信息
		List<ProductLocation> productlocationListUpdate = new ArrayList<ProductLocation>();
		//记录波此单操作日志
		List<UserActionOrderPrepack> userActionOrderPrepackList = new ArrayList<UserActionOrderPrepack>();

		//记录的任务操作日志
		List<UserActionTask> userActionTaskList=new ArrayList<UserActionTask>();
		
		List<Integer> orderPrepackYList = new ArrayList<Integer>();
		List<Integer> orderPrepackEList = new ArrayList<Integer>();
		for(Integer order_id:prePackMap.keySet()){
			logger.info("allotPackProductLocation " + "(physicalWarehouseId :"
					+ physical_warehouse_id + ",customerId :" + customer_id +",order_id:"
					+order_id +" do calculate");
			boolean isAvailable = true;
			boolean isSpec=false;
			List<Map> li=prePackMap.get(order_id);
			for(Map m:li){
				//组数
				logger.info("allotPackProductLocation " + "(physicalWarehouseId :"
						+ physical_warehouse_id + ",customerId :" + customer_id +",order_id:"
						+order_id +" do allot");
				int qty_need= null == m.get("qty_need")?0:Integer.parseInt(m.get("qty_need").toString());
				//每个套餐需要此商品的件数
				int number = null == m.get("number")?-1:Integer.parseInt(m.get("number").toString());
				if(number<1){
					isSpec=true;
					break;
				}
				int product_id = m.get("product_id") == null ? 0 : Integer
						.parseInt(m.get("product_id").toString());
				
				int available = availableMap.get(product_id + "") == null ? 0
						: Integer.parseInt(availableMap.get(
								product_id + "").toString());
				logger.info("allotPackProductLocation " + "(physicalWarehouseId :"
						+ physical_warehouse_id + ",customerId :" + customer_id
						+order_id +" product_id:" + product_id +" ,qty_need:"+qty_need +" ,number:"+number +" ,available:"+ available);
				if(qty_need*number>available){
					logger.error("数量不够");
					isAvailable=false;
					break;
				}
			}
			if(isSpec){
				logger.error("套餐内单个商品需求不大于0");
				continue;
			}
			//可以分配
			if(isAvailable){
				for(Map m:li){
					
					logger.info("allotPackProductLocation " + "(physicalWarehouseId :"
							+ physical_warehouse_id + ",customerId :" + customer_id +",order_id:"
							+order_id +" do allot");
					
					int qty_need= null == m.get("qty_need")?0:Integer.parseInt(m.get("qty_need").toString());
					int number = null == m.get("number")?0:Integer.parseInt(m.get("number").toString());
					int product_id = m.get("product_id") == null ? 0 : Integer
							.parseInt(m.get("product_id").toString());
					
					int available = availableMap.get(product_id + "") == null ? 0
							: Integer.parseInt(availableMap.get(
									product_id + "").toString());
					
					logger.info("allotPackProductLocation " + "(physicalWarehouseId :"
							+ physical_warehouse_id + ",customerId :" + customer_id +",order_id:"
							+order_id +" product_id:" + product_id +" ,qty_need:"+qty_need +" ,number:"+number +" ,available:"+ available);
					
					
					List<ProductLocation> productlocationListTemp = productlocationListMap.get(product_id+"");
					
					
					//Collections.sort(productlocationListTemp);
					
					ProductLocation plComparator=new ProductLocation();
					Collections.sort(productlocationListTemp,plComparator);

					availableMap.put(product_id + "", available
							- qty_need*number);
					doCheckQualityAllot("PACK",productlocationListTemp,
							order_id, qty_need*number,
							physical_warehouse_id, customer_id, createTime,
							taskList, productlocationListUpdate,product_id);
					
					
				}
				orderPrepackYList.add(order_id);
				UserActionOrderPrepack userActionOrderPrepack=new UserActionOrderPrepack();
				userActionOrderPrepack.setOrder_id(order_id);
				userActionOrderPrepack.setOrder_status("RESERVED");
				userActionOrderPrepack.setCreated_user("SYSTEM");
				userActionOrderPrepack.setCreated_time(new Date());
				userActionOrderPrepack.setAction_type("PREPACK ALLOT");
				userActionOrderPrepack.setAction_note("预打包库存分配成功");
				userActionOrderPrepackList.add(userActionOrderPrepack);
				
			}
			else{
				orderPrepackEList.add(order_id);
				
				UserActionOrderPrepack userActionOrderPrepack=new UserActionOrderPrepack();
				userActionOrderPrepack.setOrder_id(order_id);
				userActionOrderPrepack.setOrder_status("RESERVE_FAILED");
				userActionOrderPrepack.setCreated_user("SYSTEM");
				userActionOrderPrepack.setCreated_time(new Date());
				userActionOrderPrepack.setAction_type("PREPACK ALLOT");
				userActionOrderPrepack.setAction_note("预打包库存分配失败");
				userActionOrderPrepackList.add(userActionOrderPrepack);
			}
		}
		
		if (!WorkerUtil.isNullOrEmpty(taskList)) {
			
			for(Task task:taskList){
				taskDao.insertTask(task);
				
				UserActionTask userActionTask = new UserActionTask();
				userActionTask.setTask_id(task.getTask_id());
				userActionTask.setAction_note("创建");
				userActionTask.setAction_type(Task.TASK_STATUS_INIT);
				userActionTask.setTask_status("INIT");
				userActionTask.setCreated_user("SYSTEM");
				userActionTask.setCreated_time(new Date());
				userActionTaskList.add(userActionTask);	
			}
			
			//taskDao.insertTaskList(taskList);
		}
		
		if (!WorkerUtil.isNullOrEmpty(userActionTaskList)) {
			userActionTaskDao.insertList(userActionTaskList);
		}
		// 更新库存信息
		if (!WorkerUtil.isNullOrEmpty(productlocationListUpdate)) {
			//归并其中一部分主键相同的更新记录（使用 qty_available - ${item2.qty_available}）所以分配里面记录的变化量应该是差异值，且未正数
			Map<Integer,ProductLocation> updatePlMap =new HashMap<Integer,ProductLocation>();
			List<ProductLocation> updatePlList=new ArrayList<ProductLocation>();
			for(ProductLocation pl:productlocationListUpdate){
				//如果没有则加到记录中
				if(updatePlMap.get(pl.getPl_id())==null){
					updatePlMap.put(pl.getPl_id(), pl);
					updatePlList.add(pl);
				}else{
					ProductLocation pl2=updatePlMap.get(pl.getPl_id());
					pl2.setQty_available(pl2.getQty_available()+pl.getQty_available());
				}
			}
			
			productLocationDao
				.updateProductLocationByAllotJob(updatePlList);
		}
		
		if(!WorkerUtil.isNullOrEmpty(userActionOrderPrepackList)){
			userActionOrderPrepackDao.batchInsert(userActionOrderPrepackList);
		}
		// 更新波次单状态
		if (!WorkerUtil.isNullOrEmpty(orderPrepackEList)) {
			orderPrepackDao.batchUpdateStatus(orderPrepackEList, "RESERVE_FAILED");
		}
		if (!WorkerUtil.isNullOrEmpty(orderPrepackYList)) {
			orderPrepackDao.batchUpdateStatus(orderPrepackYList, "RESERVED");
		}
	
		logger.info("allotPackProductLocation end"+"(physicalWarehouseId :"
				+ physical_warehouse_id + ",customerId :" + customer_id);
		
		if(0-orderId.intValue()!=0&&(!WorkerUtil.isNullOrEmpty(orderPrepackEList))){
			return "false";
		}
		
		return "success";
	}
	
	/**
	 * 盘点调整
	 * @param physical_warehouse_id
	 * @param customer_id
	 * @param user_name
	 * @param taskListMap
	 * @param taskType
	 * @param LocationIdList
	 * @param productIdList
	 * @param warehouse_id 
	 * @return
	 */
	private String improve(Integer physical_warehouse_id, Integer customer_id,
			String user_name, List<Map> taskListMap, String taskType, List<Integer> LocationIdList, List<Integer> productIdList, Integer warehouse_id) {

		Map<Integer,List<TaskCount>> map=taskListMap.get(0);
		List<TaskCount> list= map.get(customer_id);
		
		int hide_batch_sn=list.get(0).getHide_batch_sn();
		String count_sn=list.get(0).getCount_sn();
		List<ProductLocation> productLoactionList=productLocationDao.getAllImproveProductLocation(physical_warehouse_id,productIdList,LocationIdList,warehouse_id);
		
		//盘盈区
		List<ProductLocation> productLocationAddList=productLocationDao.getImproveProductLocationInVariance(physical_warehouse_id,productIdList,"VARIANCE_ADD_LOCATION",warehouse_id);
		Map<String,ProductLocation> productLocationAddMap=new HashMap<String,ProductLocation>();
		if(com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(productLocationAddList)){
			productLocationAddList=new ArrayList<ProductLocation>();
		}else{
			for(ProductLocation pl:productLocationAddList){
				StringBuffer key=new StringBuffer();
				key.append(pl.getPhysical_warehouse_id())
				//.append("_").append(pl.getCustomer_id())
				.append("_").append(pl.getProduct_id())
				.append("_").append(pl.getStatus())
				.append("_").append(pl.getValidity())
				.append("_").append(pl.getBatch_sn());
				
				productLocationAddMap.put(key.toString(), pl);
			}
		}
		int addLocationIdNum=locationDao.getLocationIdInVarianceNum(physical_warehouse_id,Location.LOCATION_TYPE_VARIANCE_ADD);
		int minusLocationIdNum=locationDao.getLocationIdInVarianceNum(physical_warehouse_id,Location.LOCATION_TYPE_VARIANCE_MIMUS);
		
		int addLocationId=0;
		if(addLocationIdNum!=0){
			addLocationId=locationDao.getLocationIdInVariance(physical_warehouse_id,Location.LOCATION_TYPE_VARIANCE_ADD);
		}else{
			Location l=new Location();
			l.setLocation_type(Location.LOCATION_TYPE_VARIANCE_ADD);
			l.setLocation_barcode(WorkerUtil.generatorSequence(SequenceUtil.KEY_NAME_OTHER,"PK",true));
			l.setPhysical_warehouse_id(physical_warehouse_id);
			l.setIs_delete("N");
			l.setIs_empty("N");
			l.setCreated_user("SYSTEM");
			locationDao.insert(l);
			
			addLocationId=l.getLocation_id();
		}
		int minusLocationId=0;
		if(minusLocationIdNum!=0){
			minusLocationId=locationDao.getLocationIdInVariance(physical_warehouse_id,Location.LOCATION_TYPE_VARIANCE_MIMUS);
		}else{
			Location l=new Location();
			l.setLocation_type(Location.LOCATION_TYPE_VARIANCE_MIMUS);
			l.setLocation_barcode(WorkerUtil.generatorSequence(SequenceUtil.KEY_NAME_OTHER,"PK",true));
			l.setPhysical_warehouse_id(physical_warehouse_id);
			l.setIs_delete("N");
			l.setIs_empty("N");
			l.setCreated_user("SYSTEM");
			locationDao.insert(l);
			
			minusLocationId=l.getLocation_id();
		}
		
		
		
		
		//盘亏区
		List<ProductLocation> productLocationMinusList=productLocationDao.getImproveProductLocationInVariance(physical_warehouse_id,productIdList,"VARIANCE_MINUS_LOCATION",warehouse_id);
		Map<String,ProductLocation> productLocationMinusMap=new HashMap<String,ProductLocation>();
		if(com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(productLocationMinusList)){
			productLocationMinusList=new ArrayList<ProductLocation>();
		}else{
			for(ProductLocation pl:productLocationMinusList){
				StringBuffer key=new StringBuffer();
				key.append(pl.getPhysical_warehouse_id())
				//.append("_").append(pl.getCustomer_id())
				.append("_").append(pl.getProduct_id())
				.append("_").append(pl.getStatus())
				.append("_").append(pl.getValidity())
				.append("_").append(pl.getBatch_sn());
				
				productLocationMinusMap.put(key.toString(), pl);
			}
		}
		
		
		List<ProductLocation> updateProductLoactionList=new ArrayList<ProductLocation>();

		
		//记录盘盈盘亏

		List<ProductLocationDetail> pldList=new  ArrayList<ProductLocationDetail>();
		if(hide_batch_sn==0){
			for(TaskCount tc:list){
				int num=tc.getNum_dif();
				//库存调整
				if(num>0){
					
					//生产日期早的在前面
					Collections.sort(productLoactionList);
					
					for(ProductLocation pl:productLoactionList){
						if(pl.getProduct_id()-tc.getProduct_id()==0&&pl.getLocation_id()-tc.getLocation_id()==0&&tc.getProduct_status().equalsIgnoreCase(pl.getStatus())&&tc.getBatch_sn().equalsIgnoreCase(pl.getBatch_sn())){
							
							//调整盘盈 start1
							ProductLocation plnew =new ProductLocation();
							plnew.setPl_id(pl.getPl_id());
							plnew.setQty_total(num);
							plnew.setQty_available(num);
							updateProductLoactionList.add(plnew);
							
							productLocationDao.updateLocationEmptyStatusByPlId(pl.getPl_id());
							
							ProductLocationDetail productLocationDetail = new ProductLocationDetail();
							productLocationDetail.setPl_id(pl.getPl_id());
							productLocationDetail.setChange_quantity(num);
							productLocationDetail.setTask_count_id(tc.getTask_id());
							productLocationDetail.setDescription("盘点调整");
							productLocationDetail.setCreated_user(user_name);
							productLocationDetail.setLast_updated_user(user_name);
							productLocationDetail.setCount_sn(tc.getCount_sn());
							pldList.add(productLocationDetail);
							
							//记录库存调整明细  end
							//调整盘盈 end1
							
							//记录盘盈 start2
							StringBuffer key2=new StringBuffer();
							key2.append(tc.getPhysical_warehouse_id())
							.append("_").append(tc.getProduct_id())
							.append("_").append(tc.getProduct_status())
							.append("_").append(pl.getValidity())
							.append("_").append(pl.getBatch_sn());
							
							if(productLocationAddMap.get(key2.toString())==null){
								ProductLocation pl2=new ProductLocation();
								pl2.setQty_available(num);
								pl2.setQty_total(num);
								pl2.setPhysical_warehouse_id(tc.getPhysical_warehouse_id());
								pl2.setProduct_id(tc.getProduct_id());
								pl2.setStatus(tc.getProduct_status());
								pl2.setValidity(pl.getValidity());
								pl2.setBatch_sn(pl.getBatch_sn());
								pl2.setLocation_id(addLocationId);
								pl2.setCreated_user(user_name);
								pl2.setProduct_location_status("NORMAL");
								pl2.setSerial_number("");
								pl2.setWarehouse_id(warehouse_id);
								if(pl.getValidity().equals("")){
									pl2.setValidity("1970-01-01 00:00:00");
								}
								
								productLocationDao.insert(pl2);
								
								//盘盈区的  product_location_detail
								ProductLocationDetail productLocationDetail0 = new ProductLocationDetail();
								productLocationDetail0.setPl_id(pl2.getPl_id());
								productLocationDetail0.setChange_quantity(pl2.getQty_available());
								productLocationDetail0.setTask_count_id(tc.getTask_id());
								productLocationDetail0.setDescription("盘点调整盘盈调整");
								productLocationDetail0.setCreated_user(user_name);
								productLocationDetail0.setLast_updated_user(user_name);
								productLocationDetail0.setCount_sn(count_sn);
								pldList.add(productLocationDetail0);
								
								productLocationAddMap.put(key2.toString(), pl2);
								
							}else{
								ProductLocation pl2=productLocationAddMap.get(key2.toString());
								pl2.setQty_total(num);
								pl2.setQty_available(num);
								
								productLocationDao.updateProductLocationByImproveSingle(pl2);
								
								//盘盈区的  product_location_detail
								ProductLocationDetail productLocationDetail0 = new ProductLocationDetail();
								productLocationDetail0.setPl_id(pl2.getPl_id());
								productLocationDetail0.setChange_quantity(pl2.getQty_available());
								productLocationDetail0.setTask_count_id(tc.getTask_id());
								productLocationDetail0.setDescription("盘点调整盘盈调整");
								productLocationDetail0.setCreated_user(user_name);
								productLocationDetail0.setLast_updated_user(user_name);
								productLocationDetail0.setCount_sn(count_sn);
								pldList.add(productLocationDetail0);
							}
							//记录盘盈 end2
							pl.setQty_available(pl.getQty_available()+num);
							pl.setQty_total(pl.getQty_total()+num);
							break;
						}
					}
				}else if(num<0){

					//生产日期晚的在前面  先扣后面的
					Collections.sort(productLoactionList);
					Collections.reverse(productLoactionList);
					
					for(ProductLocation pl:productLoactionList){
						if(pl.getQty_total()>0&&pl.getProduct_id()-tc.getProduct_id()==0&&pl.getLocation_id()-tc.getLocation_id()==0&&tc.getProduct_status().equalsIgnoreCase(pl.getStatus())&&tc.getBatch_sn().equalsIgnoreCase(pl.getBatch_sn())){
							if(pl.getQty_total()+num<0){
								
								num=num+pl.getQty_total();
								
								ProductLocation plnew =new ProductLocation();
								plnew.setPl_id(pl.getPl_id());
								plnew.setQty_total(0-pl.getQty_total());
								plnew.setQty_available(0-pl.getQty_total());
								updateProductLoactionList.add(plnew);

								productLocationDao.updateLocationEmptyStatusByPlIdMinus(pl.getPl_id(),0-num);
								
								ProductLocationDetail productLocationDetail = new ProductLocationDetail();
								productLocationDetail.setPl_id(pl.getPl_id());
								productLocationDetail.setChange_quantity(0-pl.getQty_total());
								productLocationDetail.setTask_count_id(tc.getTask_id());
								productLocationDetail.setDescription("盘点调整");
								productLocationDetail.setCreated_user(user_name);
								productLocationDetail.setLast_updated_user(user_name);
								productLocationDetail.setCount_sn(tc.getCount_sn());
								pldList.add(productLocationDetail);
								//记录库存调整明细  end
								
								//记录盘亏 start2
								StringBuffer key2=new StringBuffer();
								key2.append(tc.getPhysical_warehouse_id())
								.append("_").append(tc.getProduct_id())
								.append("_").append(tc.getProduct_status())
								.append("_").append(pl.getValidity())
								.append("_").append(pl.getBatch_sn());
								
								if(productLocationMinusMap.get(key2.toString())==null){
									ProductLocation pl2=new ProductLocation();
									pl2.setQty_available(pl.getQty_total());
									pl2.setQty_total(pl.getQty_total());
									pl2.setPhysical_warehouse_id(tc.getPhysical_warehouse_id());
									pl2.setProduct_id(tc.getProduct_id());
									pl2.setStatus(tc.getProduct_status());
									pl2.setValidity(pl.getValidity());
									pl2.setBatch_sn(pl.getBatch_sn());
									pl2.setLocation_id(minusLocationId);
									pl2.setCreated_user(user_name);
									pl2.setProduct_location_status("NORMAL");
									pl2.setSerial_number("");
									pl2.setWarehouse_id(warehouse_id);
									if(pl.getValidity().equals("")){
										pl2.setValidity("1970-01-01 00:00:00");
									}
									
									productLocationDao.insert(pl2);
									
									//盘亏区的  product_location_detail
									ProductLocationDetail productLocationDetail0 = new ProductLocationDetail();
									productLocationDetail0.setPl_id(pl2.getPl_id());
									productLocationDetail0.setChange_quantity(pl2.getQty_available());
									productLocationDetail0.setTask_count_id(tc.getTask_id());
									productLocationDetail0.setDescription("盘点调整盘亏调整");
									productLocationDetail0.setCreated_user(user_name);
									productLocationDetail0.setLast_updated_user(user_name);
									productLocationDetail0.setCount_sn(count_sn);
									pldList.add(productLocationDetail0);
									
									productLocationMinusMap.put(key2.toString(), pl2);
								}else{
									ProductLocation pl2=productLocationMinusMap.get(key2.toString());
									pl2.setQty_total(pl.getQty_total());
									pl2.setQty_available(pl.getQty_total());
									
									productLocationDao.updateProductLocationByImproveSingle(pl2);
									
									//盘亏区的  product_location_detail
									ProductLocationDetail productLocationDetail0 = new ProductLocationDetail();
									productLocationDetail0.setPl_id(pl2.getPl_id());
									productLocationDetail0.setChange_quantity(pl2.getQty_available());
									productLocationDetail0.setTask_count_id(tc.getTask_id());
									productLocationDetail0.setDescription("盘点调整盘亏调整");
									productLocationDetail0.setCreated_user(user_name);
									productLocationDetail0.setLast_updated_user(user_name);
									productLocationDetail0.setCount_sn(count_sn);
									pldList.add(productLocationDetail0);
								}
								//记录盘亏 end2
								pl.setQty_available(pl.getQty_available()-pl.getQty_total());
								pl.setQty_total(0);
								
							}else{
								ProductLocation plnew =new ProductLocation();
								plnew.setPl_id(pl.getPl_id());
								plnew.setQty_total(num);
								plnew.setQty_available(num);
								updateProductLoactionList.add(plnew);								
								
								productLocationDao.updateLocationEmptyStatusByPlIdMinus(pl.getPl_id(),num);
								
								ProductLocationDetail productLocationDetail = new ProductLocationDetail();
								productLocationDetail.setPl_id(pl.getPl_id());
								productLocationDetail.setChange_quantity(num);
								productLocationDetail.setTask_count_id(tc.getTask_id());
								productLocationDetail.setDescription("盘点调整");
								productLocationDetail.setCreated_user(user_name);
								productLocationDetail.setLast_updated_user(user_name);
								productLocationDetail.setCount_sn(tc.getCount_sn());

								pldList.add(productLocationDetail);
								//记录库存调整明细  end

								
								//记录盘亏
								StringBuffer key2=new StringBuffer();
								key2.append(tc.getPhysical_warehouse_id())
								.append("_").append(tc.getProduct_id())
								.append("_").append(tc.getProduct_status())
								.append("_").append(pl.getValidity())
								.append("_").append(pl.getBatch_sn());
								if(productLocationMinusMap.get(key2.toString())==null){
									ProductLocation pl2=new ProductLocation();
									pl2.setQty_available(0-num);
									pl2.setQty_total(0-num);
									pl2.setPhysical_warehouse_id(tc.getPhysical_warehouse_id());
									pl2.setProduct_id(tc.getProduct_id());
									pl2.setStatus(tc.getProduct_status());
									pl2.setValidity(pl.getValidity());
									pl2.setBatch_sn(pl.getBatch_sn());
									pl2.setLocation_id(minusLocationId);
									pl2.setCreated_user(user_name);
									pl2.setProduct_location_status("NORMAL");
									pl2.setSerial_number("");
									pl2.setWarehouse_id(warehouse_id);
									if(pl.getValidity().equals("")){
										pl2.setValidity("1970-01-01 00:00:00");
									}
									
									productLocationDao.insert(pl2);
									
									//盘亏区的  product_location_detail
									ProductLocationDetail productLocationDetail0 = new ProductLocationDetail();
									productLocationDetail0.setPl_id(pl2.getPl_id());
									productLocationDetail0.setChange_quantity(pl2.getQty_available());
									productLocationDetail0.setTask_count_id(tc.getTask_id());
									productLocationDetail0.setDescription("盘点调整盘亏调整");
									productLocationDetail0.setCreated_user(user_name);
									productLocationDetail0.setLast_updated_user(user_name);
									productLocationDetail0.setCount_sn(count_sn);
									pldList.add(productLocationDetail0);
									
									productLocationMinusMap.put(key2.toString(), pl2);
								}else{
									ProductLocation pl2=productLocationMinusMap.get(key2.toString());
									pl2.setQty_total(-num);
									pl2.setQty_available(-num);
									
									productLocationDao.updateProductLocationByImproveSingle(pl2);
									
									//盘亏区的  product_location_detail
									ProductLocationDetail productLocationDetail0 = new ProductLocationDetail();
									productLocationDetail0.setPl_id(pl2.getPl_id());
									productLocationDetail0.setChange_quantity(pl2.getQty_available());
									productLocationDetail0.setTask_count_id(tc.getTask_id());
									productLocationDetail0.setDescription("盘点调整盘亏调整");
									productLocationDetail0.setCreated_user(user_name);
									productLocationDetail0.setLast_updated_user(user_name);
									productLocationDetail0.setCount_sn(count_sn);
									pldList.add(productLocationDetail0);
								}
								
								pl.setQty_available(pl.getQty_available()+num);
								pl.setQty_total(pl.getQty_total()+num);
								break;
							}
						}
					}
				}				
			}

		}
		//不忽略生产日期
		else{
			for(TaskCount tc:list){
				int num=tc.getNum_dif();
				if(num>0){
					for(ProductLocation pl:productLoactionList){
						if(pl.getProduct_id()-tc.getProduct_id()==0&&pl.getLocation_id()-tc.getLocation_id()==0&&tc.getValidity().equalsIgnoreCase(pl.getValidity())&&tc.getProduct_status().equalsIgnoreCase(pl.getStatus())&&tc.getBatch_sn().equalsIgnoreCase(pl.getBatch_sn())){
							ProductLocation plnew =new ProductLocation();
							plnew.setPl_id(pl.getPl_id());
							plnew.setQty_total(num);
							plnew.setQty_available(num);
							updateProductLoactionList.add(plnew);
							
							productLocationDao.updateLocationEmptyStatusByPlId(pl.getPl_id());
							
							ProductLocationDetail productLocationDetail = new ProductLocationDetail();
							productLocationDetail.setPl_id(pl.getPl_id());
							productLocationDetail.setChange_quantity(num);
							productLocationDetail.setTask_count_id(tc.getTask_id());
							productLocationDetail.setDescription("盘点调整");
							productLocationDetail.setCreated_user(user_name);
							productLocationDetail.setLast_updated_user(user_name);
							productLocationDetail.setCount_sn(tc.getCount_sn());
							pldList.add(productLocationDetail);
							//记录库存调整明细  end
							
							//记录盘盈 start2
							StringBuffer key2=new StringBuffer();
							key2.append(tc.getPhysical_warehouse_id())
							.append("_").append(tc.getProduct_id())
							.append("_").append(tc.getProduct_status())
							.append("_").append(pl.getValidity())
							.append("_").append(pl.getBatch_sn());
							
							if(productLocationAddMap.get(key2.toString())==null){
								ProductLocation pl2=new ProductLocation();
								pl2.setQty_available(num);
								pl2.setQty_total(num);
								pl2.setPhysical_warehouse_id(tc.getPhysical_warehouse_id());
								pl2.setProduct_id(tc.getProduct_id());
								pl2.setStatus(tc.getProduct_status());
								pl2.setValidity(pl.getValidity());
								pl2.setLocation_id(addLocationId);
								pl2.setBatch_sn(pl.getBatch_sn());
								pl2.setCreated_user(user_name);
								pl2.setProduct_location_status("NORMAL");
								pl2.setSerial_number("");
								pl2.setWarehouse_id(warehouse_id);
								if(pl.getValidity().equals("")){
									pl2.setValidity("1970-01-01 00:00:00");
								}
								productLocationDao.insert(pl2);
								
								//盘盈区的  product_location_detail
								ProductLocationDetail productLocationDetail0 = new ProductLocationDetail();
								productLocationDetail0.setPl_id(pl2.getPl_id());
								productLocationDetail0.setChange_quantity(pl2.getQty_available());
								productLocationDetail0.setTask_count_id(tc.getTask_id());
								productLocationDetail0.setDescription("盘点调整盘盈调整");
								productLocationDetail0.setCreated_user(user_name);
								productLocationDetail0.setLast_updated_user(user_name);
								productLocationDetail0.setCount_sn(count_sn);
								pldList.add(productLocationDetail0);
								
								productLocationAddMap.put(key2.toString(), pl2);
								
							}else{
								ProductLocation pl2=productLocationAddMap.get(key2.toString());
								pl2.setQty_total(num);
								pl2.setQty_available(num);
								
								
								productLocationDao.updateProductLocationByImproveSingle(pl2);
								
								//盘盈区的  product_location_detail
								ProductLocationDetail productLocationDetail0 = new ProductLocationDetail();
								productLocationDetail0.setPl_id(pl2.getPl_id());
								productLocationDetail0.setChange_quantity(pl2.getQty_available());
								productLocationDetail0.setTask_count_id(tc.getTask_id());
								productLocationDetail0.setDescription("盘点调整盘盈调整");
								productLocationDetail0.setCreated_user(user_name);
								productLocationDetail0.setLast_updated_user(user_name);
								productLocationDetail0.setCount_sn(count_sn);
								pldList.add(productLocationDetail0);

							}
							//记录盘盈 end2

							pl.setQty_available(pl.getQty_available()+num);
							pl.setQty_total(pl.getQty_total()+num);
							
							break;
						}
					}
				}else if(num<0){
					for(ProductLocation pl:productLoactionList){
						if(pl.getQty_total()>0&&pl.getProduct_id()-tc.getProduct_id()==0&&pl.getLocation_id()-tc.getLocation_id()==0&&tc.getValidity().equalsIgnoreCase(pl.getValidity())&&tc.getProduct_status().equalsIgnoreCase(pl.getStatus())&&tc.getBatch_sn().equalsIgnoreCase(pl.getBatch_sn())){
							ProductLocation plnew =new ProductLocation();
							plnew.setPl_id(pl.getPl_id());
							plnew.setQty_total(num);
							plnew.setQty_available(num);
							updateProductLoactionList.add(plnew);
							
							productLocationDao.updateLocationEmptyStatusByPlIdMinus(pl.getPl_id(),0-num);
							
							ProductLocationDetail productLocationDetail = new ProductLocationDetail();
							productLocationDetail.setPl_id(pl.getPl_id());
							productLocationDetail.setChange_quantity(num);
							productLocationDetail.setTask_count_id(tc.getTask_id());
							productLocationDetail.setDescription("盘点调整");
							productLocationDetail.setCreated_user(user_name);
							productLocationDetail.setLast_updated_user(user_name);
							productLocationDetail.setCount_sn(tc.getCount_sn());
							pldList.add(productLocationDetail);
							//记录库存调整明细  end
							//记录库存调整明细  end
							
							
							//记录盘亏 start2
							StringBuffer key2=new StringBuffer();
							key2.append(tc.getPhysical_warehouse_id())
							.append("_").append(tc.getProduct_id())
							.append("_").append(tc.getProduct_status())
							.append("_").append(pl.getValidity())
							.append("_").append(pl.getBatch_sn());
							
							if(productLocationMinusMap.get(key2.toString())==null){
								ProductLocation pl2=new ProductLocation();
								pl2.setQty_available(0-num);
								pl2.setQty_total(0-num);
								pl2.setPhysical_warehouse_id(tc.getPhysical_warehouse_id());
								pl2.setProduct_id(tc.getProduct_id());
								pl2.setStatus(tc.getProduct_status());
								pl2.setValidity(pl.getValidity());
								pl2.setBatch_sn(pl.getBatch_sn());
								pl2.setLocation_id(minusLocationId);
								pl2.setCreated_user(user_name);
								pl2.setProduct_location_status("NORMAL");
								pl2.setSerial_number("");
								pl2.setWarehouse_id(warehouse_id);
								if(pl.getValidity().equals("")){
									pl2.setValidity("1970-01-01 00:00:00");
								}
								productLocationDao.insert(pl2);
								
								//盘亏区的  product_location_detail
								ProductLocationDetail productLocationDetail0 = new ProductLocationDetail();
								productLocationDetail0.setPl_id(pl2.getPl_id());
								productLocationDetail0.setChange_quantity(pl2.getQty_available());
								productLocationDetail0.setTask_count_id(tc.getTask_id());
								productLocationDetail0.setDescription("盘点调整盘亏调整");
								productLocationDetail0.setCreated_user(user_name);
								productLocationDetail0.setLast_updated_user(user_name);
								productLocationDetail0.setCount_sn(count_sn);
								pldList.add(productLocationDetail0);
								
								productLocationMinusMap.put(key2.toString(), pl2);
								
							}else{
								ProductLocation pl2=productLocationMinusMap.get(key2.toString());
								pl2.setQty_total(-num);
								pl2.setQty_available(-num);
								
								productLocationDao.updateProductLocationByImproveSingle(pl2);
								
								//盘亏区的  product_location_detail
								ProductLocationDetail productLocationDetail0 = new ProductLocationDetail();
								productLocationDetail0.setPl_id(pl2.getPl_id());
								productLocationDetail0.setChange_quantity(pl2.getQty_available());
								productLocationDetail0.setTask_count_id(tc.getTask_id());
								productLocationDetail0.setDescription("盘点调整盘亏调整");
								productLocationDetail0.setCreated_user(user_name);
								productLocationDetail0.setLast_updated_user(user_name);
								productLocationDetail0.setCount_sn(count_sn);
								pldList.add(productLocationDetail0);
							}
							//记录盘亏 end2

							pl.setQty_available(pl.getQty_available()+num);
							pl.setQty_total(pl.getQty_total()+num);
							
							break;
						}
					}
				}
				
			}			
		}
		
		
//		 for (String key : productLocationAddMap.keySet()) {
//			 ProductLocation pl=productLocationAddMap.get(key);
//			 if(pl.getQty_total()!=0){
//			 ProductLocationDetail productLocationDetail = new ProductLocationDetail();
//				productLocationDetail.setPl_id(pl.getPl_id());
//				productLocationDetail.setChange_quantity(pl.getQty_available());
//				productLocationDetail.setTask_count_id(0);
//				productLocationDetail.setDescription("盘点调整盘盈调整");
//				productLocationDetail.setCreated_user(user_name);
//				productLocationDetail.setLast_updated_user(user_name);
//				productLocationDetail.setCount_sn(count_sn);
//				pldList.add(productLocationDetail);
//			 }
//		}
//		 
//		 
//		 for (String key : productLocationMinusMap.keySet()) {
//			 ProductLocation pl=productLocationMinusMap.get(key);
//			 if(pl.getQty_total()!=0){
//				 ProductLocationDetail productLocationDetail = new ProductLocationDetail();
//					productLocationDetail.setPl_id(pl.getPl_id());
//					productLocationDetail.setChange_quantity(pl.getQty_available());
//					productLocationDetail.setTask_count_id(0);
//					productLocationDetail.setDescription("盘点调整盘亏调整");
//					productLocationDetail.setCreated_user(user_name);
//					productLocationDetail.setLast_updated_user(user_name);
//					productLocationDetail.setCount_sn(count_sn);
//					pldList.add(productLocationDetail);
//			 }
//			 
//		}
		
		// 更新库存信息
		if (!WorkerUtil.isNullOrEmpty(updateProductLoactionList)) {
			
			//归并其中一部分主键相同的更新记录（使用 qty_available - ${item2.qty_available}）所以分配里面记录的变化量应该是差异值，且未正数
			Map<Integer,ProductLocation> updatePlMap =new HashMap<Integer,ProductLocation>();
			List<ProductLocation> updatePlList=new ArrayList<ProductLocation>();
			for(ProductLocation pl:updateProductLoactionList){
				//如果没有则加到记录中
				if(updatePlMap.get(pl.getPl_id())==null){
					updatePlMap.put(pl.getPl_id(), pl);
					updatePlList.add(pl);
				}else{
					ProductLocation pl2=updatePlMap.get(pl.getPl_id());
					pl2.setQty_available(pl2.getQty_available()+pl.getQty_available());
					pl2.setQty_total(pl2.getQty_total()+pl.getQty_total());
				}
			}
			
			productLocationDao
				.updateProductLocationByImproveJob(updatePlList);
		}
		
		// 更新库存明细
		
		if (!WorkerUtil.isNullOrEmpty(pldList)) {
			productLocationDetailDao.insertList(pldList);
		}

		
//		if(!WorkerUtil.isNullOrEmpty(productLocationAddList)){
//			productLocationDao.updateProductLocationByImproveJob(productLocationAddList);
//		}
//		if(!WorkerUtil.isNullOrEmpty(productLocationMinusList)){
//			productLocationDao.updateProductLocationByImproveJob(productLocationMinusList);
//		}
		
	    if(!WorkerUtil.isNullOrEmpty(list)){
		   countTaskDao.updateImproved(list);
		}
		
		return "success";
	
	}

	/**
	 *分配库存和生成紧急补货
	 * 
	 * @param ordertype 任务类型  "Allot_2B"&& "Allot"：toB波此单 /普通波此单
	 * @param list波此单号
	 * @param physical_warehouse_id 物理仓库id
	 * @param customer_id 货主id
	 * @param toLocationId 
	 * @param pickOrderGoodsSum商品总数
	 * @param pickOrderGoodMap每个波此单商品 集合
	 * @param goodsIdList商品id
	 * @param level紧急补货级别
	 * @param configReplenishmentMap普通补货配置
	 * @param productList商品信息列表
	 */
	private void allotProductLocationInPhysical(String ordertype,
			List<Integer> list, Integer physical_warehouse_id,
			Integer customer_id, List<Map> pickOrderGoodsSum,
			Map<String, List<Map>> pickOrderGoodMap, List<Integer> goodsIdList,
			Map<String, ConfigReplenishment> configReplenishmentMap, int level,
			List<Product> productList, Integer warehouseId) {


		logger.info("allotProductLocationInPhysical physical_warehouse_id="+physical_warehouse_id+" ;customer_id="+customer_id +" ;type="+ordertype+" ;id:["+list.toString()+"] start");
		
		if(!WorkerUtil.isNullOrEmpty(list)){
			List<BatchPick> bplist = batchPickDao.selectAllBatchPickListByIdListForUpdate(list);
		}else{
			return ;
		}

		
		// 所有波此单的商品信息
		pickOrderGoodsSum = batchPickBizImpl
					.selectPickOrderGoodsListSum(list);
		
		// 所有波此单的商品id
		goodsIdList = new ArrayList<Integer>();
		for (Map m : pickOrderGoodsSum) {
					goodsIdList.add(Integer
							.parseInt(m.get("product_id").toString()));
		}
	
		productList = productDao
				.selectAllProductListByKeyList(goodsIdList);

		// 普通补货规则
		List<ConfigReplenishment> configReplenishmentList = new ArrayList<ConfigReplenishment>();
		configReplenishmentList = replenishmentDao
				.selectReplenishmentListByProductIdList(
						customer_id, goodsIdList,
						physical_warehouse_id);
		configReplenishmentMap = new HashMap<String, ConfigReplenishment>();
		for (ConfigReplenishment c : configReplenishmentList) {
			configReplenishmentMap.put(c.getProduct_id() + "", c);
		}

		// 紧急补货规则
		ConfigReplenishmentUrgent configReplenishmentUrgent = replenishmentUrgentDao
				.selectConfigReplenishmentUrgentByPhysicalCustomer(
						customer_id, physical_warehouse_id);
		level = 1;
		if (configReplenishmentUrgent != null) {
			level = configReplenishmentUrgent.getReplenishment_condition();
		}
		// 每个波此单的商品总数
		pickOrderGoodMap = new HashMap<String, List<Map>>();

		// 2.统计商品总数
		List<Map> pickOrderGoods = batchPickBizImpl
				.selectPickOrderGoodsList(list);

		// 用来存每种商品


		for (Integer batchPickId : list) {
			List<Map> pickOrderGood = new ArrayList<Map>();
			for (Map m : pickOrderGoods) {
				if (Integer.parseInt(m.get("batch_pick_id").toString())
						- batchPickId == 0) {
					pickOrderGood.add(m);
				}
			}

			pickOrderGoodMap.put("" + batchPickId, pickOrderGood);
		}
		
		// 库存信息
		List<ProductLocation> productlocationPieceList = new ArrayList<ProductLocation>();
	
		productlocationPieceList = productLocationDao
				.selectProductlocationListV3(physical_warehouse_id,warehouseId,
						goodsIdList, "('RETURN_LOCATION','PIECE_PICK_LOCATION','BOX_PICK_LOCATION','STOCK_LOCATION')","NORMAL");

		// 每个商品的库存信息
		Map<String, List<ProductLocation>> productlocationPieceListMap = new HashMap<String, List<ProductLocation>>();


		// 求出每种商品的件拣货区可用数量
		Map<String, Integer> pieceAvailableMap = new HashMap<String, Integer>();



		// 将每个库区
		cutProductLocationByProductId(productlocationPieceListMap, goodsIdList,
				productlocationPieceList);


		// 计算每个库区每个商品的可用数量
		calculateProductAvailable(goodsIdList, productlocationPieceListMap,
				pieceAvailableMap);

		java.util.Date now = new java.util.Date();
		String createTime = DateUtils.getStringTime(now);

		List<Map> batchpickOrderGood = new ArrayList<Map>();
		// 记录任务
		List<Task> taskList = new ArrayList<Task>();
		// 记录库存信息
		List<ProductLocation> productlocationListUpdate = new ArrayList<ProductLocation>();
		
		//记录波此单操作日志
		List<UserActionBatchPick> userActionBatchPickList = new ArrayList<UserActionBatchPick>();

		//记录的任务操作日志
		List<UserActionTask> userActionTaskList=new ArrayList<UserActionTask>();
		

		List<Integer> batchPickYList = new ArrayList<Integer>();
		List<Integer> batchPickEList = new ArrayList<Integer>();
		
		
		// 逐个波此单
		for (Integer id : list) {

			// 将库存最多库位放在前面
			Collections.sort(productlocationPieceList);

			//  波此单  的商品信息
			batchpickOrderGood = pickOrderGoodMap.get(""+id);
			boolean isAvailable = true;
			boolean isSpec=false;
			for (Map m : batchpickOrderGood) {

				int product_id = m.get("product_id") == null ? 0 : Integer
						.parseInt(m.get("product_id").toString());
				int goods_numbers = m.get("goods_number") == null ? 0
						: Integer.parseInt(m.get("goods_number").toString());				
				
				int spec = m.get("spec") == null ? -1 : Integer.parseInt(m.get(
						"spec").toString());
				
				if(spec<1){
					isSpec=true;
					break;
				}
				int available1 = pieceAvailableMap.get(product_id + "") == null ? 0
						: Integer.parseInt(pieceAvailableMap.get(
								product_id + "").toString());


				//判断当前sku总数够不够
				if(available1<goods_numbers){
					isAvailable = false;
					//continue;
					break;
				}
				
			}

			if(isSpec){
				continue;
			}
			// 商品总数足够，判断是否可以按最新进行库存分配
			if (isAvailable) {

				for (Map m : batchpickOrderGood) {
						
						int product_id = m.get("product_id") == null ? 0 : Integer
								.parseInt(m.get("product_id").toString());
						int spec = m.get("spec") == null ? 1 : Integer.parseInt(m
								.get("spec").toString());
						int goods_numbers = m.get("goods_number") == null ? 0
								: Integer.parseInt(m.get("goods_number")
										.toString());
						int available1 = pieceAvailableMap.get(product_id + "") == null ? 0
								: Integer.parseInt(pieceAvailableMap.get(
										product_id + "").toString());
		
						int boxes = goods_numbers / spec;
						int subnum = goods_numbers % spec;

						
						List<ProductLocation> productlocationListTemp = productlocationPieceListMap.get(product_id+"");
						// 将库存最多库位放在前面
						
						
						ProductLocation plComparator=new ProductLocation();
						Collections.sort(productlocationListTemp,plComparator);
						//Collections.sort(productlocationListTemp);
	
						pieceAvailableMap.put(product_id + "", available1
								- goods_numbers);
						doCheckQualityAllot(ordertype,productlocationListTemp,
								id, goods_numbers,
								physical_warehouse_id, customer_id, createTime,
								taskList, productlocationListUpdate,product_id);
						
					}
				
				batchPickYList.add(id);
            	UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
				userActionBatchPick.setAction_note("波次单:" + id+"分配库存成功");
				userActionBatchPick.setAction_type("ALLOTINVENTORY");
				userActionBatchPick.setCreated_time(new Date());
				userActionBatchPick.setCreated_user("System");
				userActionBatchPick.setStatus("INIT");
				userActionBatchPick.setBatch_pick_id(id);
				userActionBatchPickList.add(userActionBatchPick);

				}
			else {
					batchPickEList.add(id);
                	
                	UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
    				userActionBatchPick.setAction_note("波次单:" + id+"分配库存失败");
    				userActionBatchPick.setAction_type("ALLOTINVENTORY");
    				userActionBatchPick.setCreated_time(new Date());
    				userActionBatchPick.setCreated_user("System");
    				userActionBatchPick.setStatus("INIT");
    				userActionBatchPick.setBatch_pick_id(id);
    				userActionBatchPickList.add(userActionBatchPick);
				}

				
		}
	
		
		if (!WorkerUtil.isNullOrEmpty(taskList)) {
			
			for(Task task:taskList){
				taskDao.insertTask(task);
				
				UserActionTask userActionTask = new UserActionTask();
				userActionTask.setTask_id(task.getTask_id());
				userActionTask.setAction_note("创建");
				userActionTask.setAction_type(Task.TASK_STATUS_INIT);
				userActionTask.setTask_status("INIT");
				userActionTask.setCreated_user("SYSTEM");
				userActionTask.setCreated_time(new Date());
				userActionTaskList.add(userActionTask);	
			}
			
			//taskDao.insertTaskList(taskList);
		}
		
		if (!WorkerUtil.isNullOrEmpty(userActionTaskList)) {
			userActionTaskDao.insertList(userActionTaskList);
		}
		// 更新库存信息
		if (!WorkerUtil.isNullOrEmpty(productlocationListUpdate)) {
			//归并其中一部分主键相同的更新记录（使用 qty_available - ${item2.qty_available}）所以分配里面记录的变化量应该是差异值，且未正数
			Map<Integer,ProductLocation> updatePlMap =new HashMap<Integer,ProductLocation>();
			List<ProductLocation> updatePlList=new ArrayList<ProductLocation>();
			for(ProductLocation pl:productlocationListUpdate){
				//如果没有则加到记录中
				if(updatePlMap.get(pl.getPl_id())==null){
					updatePlMap.put(pl.getPl_id(), pl);
					updatePlList.add(pl);
				}else{
					ProductLocation pl2=updatePlMap.get(pl.getPl_id());
					pl2.setQty_available(pl2.getQty_available()+pl.getQty_available());
				}
			}
			
			productLocationDao
				.updateProductLocationByAllotJob(updatePlList);
		}
		// 更新波次单状态
		if (!WorkerUtil.isNullOrEmpty(batchPickEList)) {
			batchPickDao.updateReserveStatus(batchPickEList, "E");
		}
		if (!WorkerUtil.isNullOrEmpty(batchPickYList)) {
			batchPickDao.updateReserveStatus(batchPickYList, "Y");
		}
		
		if(!WorkerUtil.isNullOrEmpty(userActionBatchPickList)){
			userActionBatchPickDao.batchInsert(userActionBatchPickList);
		}
	}
	
	/**
	 *分配库存和生成紧急补货
	 * 
	 * @param ordertype 任务类型  "gt_normal"&& "gt_defective"：toB波此单 /普通波此单
	 * @param list波此单号
	 * @param physical_warehouse_id 物理仓库id
	 * @param customer_id 货主id
	 * @param warehouseId 
	 * @param pickOrderGoodsSum商品总数
	 * @param pickOrderGoodMap每个波此单商品 集合
	 * @param goodsIdList商品id
	 * @param level紧急补货级别
	 * @param configReplenishmentMap普通补货配置
	 * @param productList商品信息列表
	 */
	private void allotGtProductLocationInPhysical(String ordertype,
			List<Integer> list, Integer physical_warehouse_id,
			Integer customer_id, List<Map> pickOrderGoodsSum,
			Map<String, List<Map>> pickOrderGoodMap, List<Integer> goodsIdList,
			Map<String, ConfigReplenishment> configReplenishmentMap, int level,
			List<Product> productList, Integer warehouseId) {


		logger.info("allotGtProductLocationInPhysical physical_warehouse_id="+physical_warehouse_id+" ;customer_id="+customer_id +" ;type="+ordertype+" ;id:["+list.toString()+"] start");
		
		if(!WorkerUtil.isNullOrEmpty(list)){
			List<OrderProcess> oilist = orderProcessDao.selectOrdersForLock2Task(list);
		}else{
			return ;
		}

		pickOrderGoodsSum = orderInfoDao.
				selectGtOrderGoodsListSum(list);

		// 所有gt订单的商品id
		goodsIdList = new ArrayList<Integer>();
		for (Map m : pickOrderGoodsSum) {
			goodsIdList.add(Integer
					.parseInt(m.get("product_id").toString()));
		}

		productList = productDao
				.selectAllProductListByKeyList(goodsIdList);

		// 普通补货规则
		List<ConfigReplenishment> configReplenishmentList = new ArrayList<ConfigReplenishment>();
		configReplenishmentList = replenishmentDao
				.selectReplenishmentListByProductIdList(customer_id,
						goodsIdList, physical_warehouse_id);
		configReplenishmentMap = new HashMap<String, ConfigReplenishment>();
		for (ConfigReplenishment c : configReplenishmentList) {
			configReplenishmentMap.put(c.getProduct_id() + "", c);
		}

		// 紧急补货规则
		ConfigReplenishmentUrgent configReplenishmentUrgent = replenishmentUrgentDao
				.selectConfigReplenishmentUrgentByPhysicalCustomer(
						customer_id, physical_warehouse_id);
		level = 1;
		if (configReplenishmentUrgent != null) {
			level = configReplenishmentUrgent.getReplenishment_condition();
		}
		// 每个gt订单的商品总数
		pickOrderGoodMap = new HashMap<String, List<Map>>();

		// 2.统计商品总数
		List<Map> pickOrderGoods = orderInfoDao
				.selectGtOrderGoodsList(list);

		

		for (Integer order_id : list) {
			// 用来存每种商品
			List<Map> pickOrderGood = new ArrayList<Map>();
			for (Map m : pickOrderGoods) {
				if (Integer.parseInt(m.get("order_id").toString())
						- order_id == 0) {
					pickOrderGood.add(m);
				}
			}

			pickOrderGoodMap.put("" + order_id, pickOrderGood);
		}
		
		// 库存信息
		List<ProductLocation> productlocationPieceList = new ArrayList<ProductLocation>();
	
		
		if("gt_normal".equalsIgnoreCase(ordertype)){
			productlocationPieceList = productLocationDao
					.selectProductlocationListV9(physical_warehouse_id,warehouseId,
							goodsIdList, "('RETURN_LOCATION','PIECE_PICK_LOCATION','BOX_PICK_LOCATION','STOCK_LOCATION','PACKBOX_LOCATION')","NORMAL");

		}else{
			productlocationPieceList = productLocationDao
					.selectProductlocationListV6(physical_warehouse_id,warehouseId,
							goodsIdList, Location.LOCATION_TYPE_DEFECTIVE,"DEFECTIVE");
		}
		
		// 每个商品的库存信息
		Map<String, List<ProductLocation>> productlocationPieceListMap = new HashMap<String, List<ProductLocation>>();


		// 求出每种商品的件拣货区可用数量
		Map<String, Integer> pieceAvailableMap = new HashMap<String, Integer>();



		// 将每个库区
		cutProductLocationByProductId(productlocationPieceListMap, goodsIdList,
				productlocationPieceList);


		// 计算每个库区每个商品的可用数量
		calculateProductAvailable(goodsIdList, productlocationPieceListMap,
				pieceAvailableMap);

		java.util.Date now = new java.util.Date();
		String createTime = DateUtils.getStringTime(now);

		List<Map> batchpickOrderGood = new ArrayList<Map>();
		// 记录任务
		List<Task> taskList = new ArrayList<Task>();
		// 记录库存信息
		List<ProductLocation> productlocationListUpdate = new ArrayList<ProductLocation>();
		
		//记录波此单操作日志
		List<UserActionOrder> userActionOrderList = new ArrayList<UserActionOrder>();

		//记录的任务操作日志
		List<UserActionTask> userActionTaskList=new ArrayList<UserActionTask>();
		

		List<Integer> gtYList = new ArrayList<Integer>();
		List<Integer> gtEList = new ArrayList<Integer>();
		
		
		// 逐个波此单
		for (Integer id : list) {

			// 将库存最多库位放在前面
			Collections.sort(productlocationPieceList);

			//  波此单  的商品信息
			batchpickOrderGood = pickOrderGoodMap.get(""+id);
			boolean isAvailable = true;
			boolean isSpec=false;
			for (Map m : batchpickOrderGood) {

				int product_id = m.get("product_id") == null ? 0 : Integer
						.parseInt(m.get("product_id").toString());
				int goods_numbers = m.get("goods_number") == null ? 0
						: Integer.parseInt(m.get("goods_number").toString());				
				
				int spec = m.get("spec") == null ? -1 : Integer.parseInt(m.get(
						"spec").toString());
				
				if(spec<1){
					isSpec=true;
					break;
				}
				int available1 = pieceAvailableMap.get(product_id + "") == null ? 0
						: Integer.parseInt(pieceAvailableMap.get(
								product_id + "").toString());


				//判断当前sku总数够不够
				if(available1<goods_numbers){
					isAvailable = false;
					//continue;
					break;
				}
				
			}

			if(isSpec){
				continue;
			}
			// 商品总数足够，判断是否可以按最新进行库存分配
			if (isAvailable) {

				for (Map m : batchpickOrderGood) {
						
						int product_id = m.get("product_id") == null ? 0 : Integer
								.parseInt(m.get("product_id").toString());
						int spec = m.get("spec") == null ? 1 : Integer.parseInt(m
								.get("spec").toString());
						int goods_numbers = m.get("goods_number") == null ? 0
								: Integer.parseInt(m.get("goods_number")
										.toString());
						int available1 = pieceAvailableMap.get(product_id + "") == null ? 0
								: Integer.parseInt(pieceAvailableMap.get(
										product_id + "").toString());
		
						int boxes = goods_numbers / spec;
						int subnum = goods_numbers % spec;
						
						List<ProductLocation> productlocationListTemp = productlocationPieceListMap.get(product_id+"");
						// 将库存最多库位放在前面
						
						ProductLocation plComparator=new ProductLocation();
						Collections.sort(productlocationListTemp,plComparator);
						//Collections.sort(productlocationListTemp);
	
						pieceAvailableMap.put(product_id + "", available1
								- goods_numbers);
						doCheckQualityAllot(ordertype,productlocationListTemp,
								id, goods_numbers,
								physical_warehouse_id, customer_id, createTime,
								taskList, productlocationListUpdate,product_id);
						
					}
				gtYList.add(id);
            	UserActionOrder userActionOrder = new UserActionOrder();
        		userActionOrder.setAction_note("供应商退货订单"+id+"分配库存成功");
        		userActionOrder.setAction_type("RESERVED");
        		userActionOrder.setCreated_time(new Date());
        		userActionOrder.setCreated_user("system");
        		userActionOrder.setOrder_goods_id(null);
        		userActionOrder.setOrder_id(id);
        		userActionOrder.setOrder_status("RESERVED");
        		userActionOrderList.add(userActionOrder);

				}
			else {   				
					gtEList.add(id);
					UserActionOrder userActionOrder = new UserActionOrder();
            		userActionOrder.setAction_note("供应商退货订单"+id+"分配库存失败");
            		userActionOrder.setAction_type("RESERVED");
            		userActionOrder.setCreated_time(new Date());
            		userActionOrder.setCreated_user("system");
            		userActionOrder.setOrder_goods_id(null);
            		userActionOrder.setOrder_id(id);
            		userActionOrder.setOrder_status("RESERVED");
            		userActionOrderList.add(userActionOrder);
    				
				}

				
		}	
		
		if (!WorkerUtil.isNullOrEmpty(taskList)) {
			
			for(Task task:taskList){
				taskDao.insertTask(task);
				
				UserActionTask userActionTask = new UserActionTask();
				userActionTask.setTask_id(task.getTask_id());
				userActionTask.setAction_note("创建");
				userActionTask.setAction_type(Task.TASK_STATUS_INIT);
				userActionTask.setTask_status("INIT");
				userActionTask.setCreated_user("SYSTEM");
				userActionTask.setCreated_time(new Date());
				userActionTaskList.add(userActionTask);	
			}
		}
		
		if (!WorkerUtil.isNullOrEmpty(userActionTaskList)) {
			userActionTaskDao.insertList(userActionTaskList);
		}
		// 更新库存信息
		if (!WorkerUtil.isNullOrEmpty(productlocationListUpdate)) {
			//归并其中一部分主键相同的更新记录（使用 qty_available - ${item2.qty_available}）所以分配里面记录的变化量应该是差异值，且未正数
			Map<Integer,ProductLocation> updatePlMap =new HashMap<Integer,ProductLocation>();
			List<ProductLocation> updatePlList=new ArrayList<ProductLocation>();
			for(ProductLocation pl:productlocationListUpdate){
				//如果没有则加到记录中
				if(updatePlMap.get(pl.getPl_id())==null){
					updatePlMap.put(pl.getPl_id(), pl);
					updatePlList.add(pl);
				}else{
					ProductLocation pl2=updatePlMap.get(pl.getPl_id());
					pl2.setQty_available(pl2.getQty_available()+pl.getQty_available());
				}
			}
			
			productLocationDao
				.updateProductLocationByAllotJob(updatePlList);
		}
		if (!WorkerUtil.isNullOrEmpty(gtYList)) {
			logger.info("physical_warehouse_id="+physical_warehouse_id+" ;customer_id="+customer_id +" ;type="+ordertype+" ;gtYList size:["+gtYList.size()+"] end");
			orderInfoDao.updateReserveStatus(gtYList, "RESERVED");
			orderProcessDao.updateStatusToReserved(gtYList, "RESERVED");
			}
			
		if(!WorkerUtil.isNullOrEmpty(userActionOrderList)){
			logger.info("physical_warehouse_id="+physical_warehouse_id+" ;customer_id="+customer_id +" ;type="+ordertype+" ;userActionOrderList size:["+userActionOrderList.size()+"] end");
			userActionOrderDao.batchInsert(userActionOrderList);
		}
	}
	
	
	/**
	 *分配库存和生成紧急补货
	 * 
	 * @param ordertype 任务类型  "Allot_2X"：toB波此单
	 * @param list波此单号
	 * @param physical_warehouse_id 物理仓库id
	 * @param customer_id 货主id
	 * @param warehou 
	 * @param pickOrderGoodsSum商品总数
	 * @param pickOrderGoodMap每个波此单商品 集合
	 * @param goodsIdList商品id
	 * @param level紧急补货级别
	 * @param configReplenishmentMap普通补货配置
	 * @param productList商品信息列表
	 */
	private void allot2XProductLocation(String ordertype,
			List<Integer> list, Integer physical_warehouse_id,
			Integer customer_id, List<Map> pickOrderGoodsSum,
			Map<String, List<Map>> pickOrderGoodMap, List<Integer> goodsIdList,
			Map<String, ConfigReplenishment> configReplenishmentMap, int level,
			List<Product> productList, Integer warehouseId) {


		logger.info("physical_warehouse_id="+physical_warehouse_id+" ;customer_id="+customer_id +" ;type="+ordertype+" ;id:["+list.toString()+"] start");
		
		if(!WorkerUtil.isNullOrEmpty(list)){
			List<BatchPick> bplist = batchPickDao.selectAllBatchPickListByIdListForUpdate(list);
		}else{
			return ;
		}

		
		// 所有波此单的商品信息
		pickOrderGoodsSum = batchPickBizImpl
					.selectPickOrderGoodsListSum(list);
		
		// 所有波此单的商品id
		goodsIdList = new ArrayList<Integer>();
		for (Map m : pickOrderGoodsSum) {
					goodsIdList.add(Integer
							.parseInt(m.get("product_id").toString()));
		}
	
		productList = productDao
				.selectAllProductListByKeyList(goodsIdList);

		// 普通补货规则
		List<ConfigReplenishment> configReplenishmentList = new ArrayList<ConfigReplenishment>();
		configReplenishmentList = replenishmentDao
				.selectReplenishmentListByProductIdList(
						customer_id, goodsIdList,
						physical_warehouse_id);
		configReplenishmentMap = new HashMap<String, ConfigReplenishment>();
		for (ConfigReplenishment c : configReplenishmentList) {
			configReplenishmentMap.put(c.getProduct_id() + "", c);
		}

		// 紧急补货规则
		ConfigReplenishmentUrgent configReplenishmentUrgent = replenishmentUrgentDao
				.selectConfigReplenishmentUrgentByPhysicalCustomer(
						customer_id, physical_warehouse_id);
		level = 1;
		if (configReplenishmentUrgent != null) {
			level = configReplenishmentUrgent.getReplenishment_condition();
		}
		// 每个波此单的商品总数
		pickOrderGoodMap = new HashMap<String, List<Map>>();

		// 2.统计商品总数
		List<Map> pickOrderGoods = batchPickBizImpl
				.selectPickOrderGoodsList(list);

		// 用来存每种商品


		for (Integer batchPickId : list) {
			List<Map> pickOrderGood = new ArrayList<Map>();
			for (Map m : pickOrderGoods) {
				if (Integer.parseInt(m.get("batch_pick_id").toString())
						- batchPickId == 0) {
					pickOrderGood.add(m);
				}
			}

			pickOrderGoodMap.put("" + batchPickId, pickOrderGood);
		}
		
		// 库存信息
		List<ProductLocation> productlocationPieceList = new ArrayList<ProductLocation>();


		//被冻结的库位+商品
		List<ProductLocation> productlocationExceptionList = productLocationDao.selectExceptionList(goodsIdList);
		if(com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(productlocationExceptionList)){
			productlocationExceptionList=new ArrayList<ProductLocation>();
		}
		
		//记录  product_id1=location_id1|location_id2  product_id2=location_id1|location_id5|location_id6 
		Map<String,List<Integer>> productlocationExceptionMap=new HashMap<String,List<Integer>>();
		
		for(ProductLocation p:productlocationExceptionList){
			List<Integer> listTemp =new ArrayList<Integer>();
			if(productlocationExceptionMap.get(p.getProduct_id() + "") != null){
				listTemp=productlocationExceptionMap.get(p.getProduct_id() + "");
			}
			listTemp.add(p.getLocation_id());
			productlocationExceptionMap.put(p.getProduct_id()+"", listTemp);
		}
		
	
		productlocationPieceList = productLocationDao
				.selectProductlocationListV3(physical_warehouse_id,warehouseId,
						goodsIdList, "('RETURN_LOCATION','PIECE_PICK_LOCATION','BOX_PICK_LOCATION','STOCK_LOCATION')","NORMAL");
			
		// 保存每种商品的id
		Map<String, Integer> productBoxPickStartNumber = new HashMap<String, Integer>();
		for (Product p : productList) {
			productBoxPickStartNumber.put("" + p.getProduct_id(),
					p.getBox_pick_start_number());
		}

		// 每个商品的库存信息
		Map<String, List<ProductLocation>> productlocationPieceListMap = new HashMap<String, List<ProductLocation>>();


		// 求出每种商品的件拣货区可用数量
		Map<String, Integer> pieceAvailableMap = new HashMap<String, Integer>();



		// 将每个库区
		cutProductLocationByProductId(productlocationPieceListMap, goodsIdList,
				productlocationPieceList);


		// 计算每个库区每个商品的可用数量
		calculateProductAvailable(goodsIdList, productlocationPieceListMap,
				pieceAvailableMap);

		java.util.Date now = new java.util.Date();
		String createTime = DateUtils.getStringTime(now);

		List<Map> batchpickOrderGood = new ArrayList<Map>();
		// 记录任务
		List<Task> taskList = new ArrayList<Task>();
		// 记录库存信息
		List<ProductLocation> productlocationListUpdate = new ArrayList<ProductLocation>();
		
		//记录波此单操作日志
		List<UserActionBatchPick> userActionBatchPickList = new ArrayList<UserActionBatchPick>();

		//记录的任务操作日志
		List<UserActionTask> userActionTaskList=new ArrayList<UserActionTask>();
		

		List<Integer> batchPickYList = new ArrayList<Integer>();
		List<Integer> batchPickEList = new ArrayList<Integer>();
		
		
		// 逐个波此单
		for (Integer id : list) {

			// 将库存最多库位放在前面
			Collections.sort(productlocationPieceList);
			Collections.reverse(productlocationPieceList);

			//  波此单  的商品信息
			batchpickOrderGood = pickOrderGoodMap.get(""+id);
			boolean isAvailable = true;
			boolean isSpec=false;
			for (Map m : batchpickOrderGood) {

				int product_id = m.get("product_id") == null ? 0 : Integer
						.parseInt(m.get("product_id").toString());
				int goods_numbers = m.get("goods_number") == null ? 0
						: Integer.parseInt(m.get("goods_number").toString());				
				
				int spec = m.get("spec") == null ? -1 : Integer.parseInt(m.get(
						"spec").toString());
				
				if(spec<1){
					isSpec=true;
					break;
				}
				int available1 = pieceAvailableMap.get(product_id + "") == null ? 0
						: Integer.parseInt(pieceAvailableMap.get(
								product_id + "").toString());


				//判断当前sku总数够不够
				if(available1<goods_numbers){
					isAvailable = false;
					break;
				}
				
			}

			if(isSpec){
				continue;
			}
			// 商品总数足够，判断是否可以按最新进行库存分配
			if (isAvailable) {

				for (Map m : batchpickOrderGood) {
						
						int product_id = m.get("product_id") == null ? 0 : Integer
								.parseInt(m.get("product_id").toString());
						int spec = m.get("spec") == null ? 1 : Integer.parseInt(m
								.get("spec").toString());
						int goods_numbers = m.get("goods_number") == null ? 0
								: Integer.parseInt(m.get("goods_number")
										.toString());
						int available1 = pieceAvailableMap.get(product_id + "") == null ? 0
								: Integer.parseInt(pieceAvailableMap.get(
										product_id + "").toString());
		
						int boxes = goods_numbers / spec;
						int subnum = goods_numbers % spec;
						
						List<ProductLocation> productlocationListTemp = productlocationPieceListMap.get(product_id+"");
						// 将库存最多库位放在前面
						
						Collections.sort(productlocationListTemp);
						Collections.reverse(productlocationListTemp);
	
						pieceAvailableMap.put(product_id + "", available1
								- goods_numbers);
						doCheckQualityAllot(ordertype,productlocationListTemp,
								id, goods_numbers,
								physical_warehouse_id, customer_id, createTime,
								taskList, productlocationListUpdate,product_id);
						
					}
				
				batchPickYList.add(id);
            	UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
				userActionBatchPick.setAction_note("波次单:" + id+"分配库存成功");
				userActionBatchPick.setAction_type("ALLOTINVENTORY");
				userActionBatchPick.setCreated_time(new Date());
				userActionBatchPick.setCreated_user("System");
				userActionBatchPick.setStatus("INIT");
				userActionBatchPick.setBatch_pick_id(id);
				userActionBatchPickList.add(userActionBatchPick);

				}
			else {
					batchPickEList.add(id);
                	
                	UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
    				userActionBatchPick.setAction_note("波次单:" + id+"分配库存失败");
    				userActionBatchPick.setAction_type("ALLOTINVENTORY");
    				userActionBatchPick.setCreated_time(new Date());
    				userActionBatchPick.setCreated_user("System");
    				userActionBatchPick.setStatus("INIT");
    				userActionBatchPick.setBatch_pick_id(id);
    				userActionBatchPickList.add(userActionBatchPick);
				}

				
		}
	
		
		if (!WorkerUtil.isNullOrEmpty(taskList)) {
			
			for(Task task:taskList){
				taskDao.insertTask(task);
				
				UserActionTask userActionTask = new UserActionTask();
				userActionTask.setTask_id(task.getTask_id());
				userActionTask.setAction_note("创建");
				userActionTask.setAction_type(Task.TASK_STATUS_INIT);
				userActionTask.setTask_status("INIT");
				userActionTask.setCreated_user("SYSTEM");
				userActionTask.setCreated_time(new Date());
				userActionTaskList.add(userActionTask);	
			}
			
			//taskDao.insertTaskList(taskList);
		}
		
		if (!WorkerUtil.isNullOrEmpty(userActionTaskList)) {
			userActionTaskDao.insertList(userActionTaskList);
		}
		// 更新库存信息
		if (!WorkerUtil.isNullOrEmpty(productlocationListUpdate)) {
			//归并其中一部分主键相同的更新记录（使用 qty_available - ${item2.qty_available}）所以分配里面记录的变化量应该是差异值，且未正数
			Map<Integer,ProductLocation> updatePlMap =new HashMap<Integer,ProductLocation>();
			List<ProductLocation> updatePlList=new ArrayList<ProductLocation>();
			for(ProductLocation pl:productlocationListUpdate){
				//如果没有则加到记录中
				if(updatePlMap.get(pl.getPl_id())==null){
					updatePlMap.put(pl.getPl_id(), pl);
					updatePlList.add(pl);
				}else{
					ProductLocation pl2=updatePlMap.get(pl.getPl_id());
					pl2.setQty_available(pl2.getQty_available()+pl.getQty_available());
				}
			}
			
			productLocationDao
				.updateProductLocationByAllotJob(updatePlList);
		}
		// 更新波次单状态
		if (!WorkerUtil.isNullOrEmpty(batchPickEList)) {
			batchPickDao.updateReserveStatus(batchPickEList, "E");
		}
		if (!WorkerUtil.isNullOrEmpty(batchPickYList)) {
			batchPickDao.updateReserveStatus(batchPickYList, "Y");
		}
		
		if(!WorkerUtil.isNullOrEmpty(userActionBatchPickList)){
			userActionBatchPickDao.batchInsert(userActionBatchPickList);
		}
	}
	/**
	 *分配库存和生成紧急补货
	 * 
	 * @param ordertype 任务类型   "Allot":普通波此单；  "Allot_2B"：toB波此单；   "gt_normal" GT订单（良品）; "gt_defective"GT订单（二手）
	 * @param list波此单号/-Gt订单号
	 * @param physical_warehouse_id物理仓库id
	 * @param customer_id货主id
	 * @param pickOrderGoodsSum 商品总数
	 * @param pickOrderGoodMap 每个波此单商品 集合
	 * @param warehouseId 
	 * @param goodsIdList商品id
	 * @param level紧急补货级别
	 * @param configReplenishmentMap普通补货配置
	 * @param productList商品信息列表
	 */
	public void allotProductLocation(String ordertype,
			List<Integer> list, Integer physical_warehouse_id,
			Integer customer_id, List<Map> pickOrderGoodsSum,
			Map<String, List<Map>> pickOrderGoodMap, List<Integer> goodsIdList,
			Map<String, ConfigReplenishment> configReplenishmentMap, int level,
			List<Product> productList, Integer warehouseId) {
		logger.info("physical_warehouse_id="+physical_warehouse_id+" ;customer_id="+customer_id +" ;type="+ordertype+" ;id:["+list.toString()+"] start");
		
		//-C   -B  波此单
		if(ordertype.equalsIgnoreCase("Allot")||ordertype.equalsIgnoreCase("Allot_2B")){
			if(!WorkerUtil.isNullOrEmpty(list)){
				List<BatchPick> bplist = batchPickDao.selectAllBatchPickListByIdListForUpdate(list);
			}else{
				return ;
			}
		}
		//-gt  波此单
		else{
			if(!WorkerUtil.isNullOrEmpty(list)){
				List<OrderProcess> oilist = orderProcessDao.selectOrdersForLock2Task(list);
			}else{
				return ;
			}
		}
		
		if(ordertype.equalsIgnoreCase("Allot")||ordertype.equalsIgnoreCase("Allot_2B")){
				// 所有波此单的商品信息
				pickOrderGoodsSum = batchPickBizImpl
							.selectPickOrderGoodsListSum(list);
				
				// 所有波此单的商品id
				goodsIdList = new ArrayList<Integer>();
				for (Map m : pickOrderGoodsSum) {
							goodsIdList.add(Integer
									.parseInt(m.get("product_id").toString()));
				}
			
				productList = productDao
						.selectAllProductListByKeyList(goodsIdList);
	
				// 普通补货规则
				List<ConfigReplenishment> configReplenishmentList = new ArrayList<ConfigReplenishment>();
				configReplenishmentList = replenishmentDao
						.selectReplenishmentListByProductIdList(
								customer_id, goodsIdList,
								physical_warehouse_id);
				configReplenishmentMap = new HashMap<String, ConfigReplenishment>();
				for (ConfigReplenishment c : configReplenishmentList) {
					configReplenishmentMap.put(c.getProduct_id() + "", c);
				}
	
				// 紧急补货规则
				ConfigReplenishmentUrgent configReplenishmentUrgent = replenishmentUrgentDao
						.selectConfigReplenishmentUrgentByPhysicalCustomer(
								customer_id, physical_warehouse_id);
				level = 1;
				if (configReplenishmentUrgent != null) {
					level = configReplenishmentUrgent.getReplenishment_condition();
				}
				// 每个波此单的商品总数
				pickOrderGoodMap = new HashMap<String, List<Map>>();
	
				// 2.统计商品总数
				List<Map> pickOrderGoods = batchPickBizImpl
						.selectPickOrderGoodsList(list);
	
				// 用来存每种商品
	
	
				for (Integer batchPickId : list) {
					List<Map> pickOrderGood = new ArrayList<Map>();
					for (Map m : pickOrderGoods) {
						if (Integer.parseInt(m.get("batch_pick_id").toString())
								- batchPickId == 0) {
							pickOrderGood.add(m);
						}
					}
	
					pickOrderGoodMap.put("" + batchPickId, pickOrderGood);
				}
		}
		else{
			pickOrderGoodsSum = orderInfoDao.
					selectGtOrderGoodsListSum(list);

			// 所有gt订单的商品id
			goodsIdList = new ArrayList<Integer>();
			for (Map m : pickOrderGoodsSum) {
				goodsIdList.add(Integer
						.parseInt(m.get("product_id").toString()));
			}

			productList = productDao
					.selectAllProductListByKeyList(goodsIdList);

			// 普通补货规则
			List<ConfigReplenishment> configReplenishmentList = new ArrayList<ConfigReplenishment>();
			configReplenishmentList = replenishmentDao
					.selectReplenishmentListByProductIdList(customer_id,
							goodsIdList, physical_warehouse_id);
			configReplenishmentMap = new HashMap<String, ConfigReplenishment>();
			for (ConfigReplenishment c : configReplenishmentList) {
				configReplenishmentMap.put(c.getProduct_id() + "", c);
			}

			// 紧急补货规则
			ConfigReplenishmentUrgent configReplenishmentUrgent = replenishmentUrgentDao
					.selectConfigReplenishmentUrgentByPhysicalCustomer(
							customer_id, physical_warehouse_id);
			level = 1;
			if (configReplenishmentUrgent != null) {
				level = configReplenishmentUrgent.getReplenishment_condition();
			}
			// 每个gt订单的商品总数
			pickOrderGoodMap = new HashMap<String, List<Map>>();

			// 2.统计商品总数
			List<Map> pickOrderGoods = orderInfoDao
					.selectGtOrderGoodsList(list);

			

			for (Integer order_id : list) {
				// 用来存每种商品
				List<Map> pickOrderGood = new ArrayList<Map>();
				for (Map m : pickOrderGoods) {
					if (Integer.parseInt(m.get("order_id").toString())
							- order_id == 0) {
						pickOrderGood.add(m);
					}
				}

				pickOrderGoodMap.put("" + order_id, pickOrderGood);
			}
		}
		
		// 库存信息
		List<ProductLocation> productlocationPieceList = new ArrayList<ProductLocation>();// 件拣货库存
		List<ProductLocation> productlocationBoxList = new ArrayList<ProductLocation>();// 箱拣货库存
		List<ProductLocation> productlocationStockList = new ArrayList<ProductLocation>();// 存储区库存
		List<ProductLocation> productlocationReturnList = new ArrayList<ProductLocation>();// 退回区库存

		//被冻结的库位+商品
		List<ProductLocation> productlocationExceptionList = productLocationDao.selectExceptionList(goodsIdList);
		if(com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(productlocationExceptionList)){
			productlocationExceptionList=new ArrayList<ProductLocation>();
		}
		
		//记录  product_id1=location_id1|location_id2  product_id2=location_id1|location_id5|location_id6 
		Map<String,List<Integer>> productlocationExceptionMap=new HashMap<String,List<Integer>>();
		
		for(ProductLocation p:productlocationExceptionList){
			List<Integer> listTemp =new ArrayList<Integer>();
			if(productlocationExceptionMap.get(p.getProduct_id() + "") != null){
				listTemp=productlocationExceptionMap.get(p.getProduct_id() + "");
			}
			listTemp.add(p.getLocation_id());
			productlocationExceptionMap.put(p.getProduct_id()+"", listTemp);
		}
		
		if(ordertype.equalsIgnoreCase("Allot")||ordertype.equalsIgnoreCase("Allot_2B")){
		//if(!ordertype.equalsIgnoreCase("gt_defective")){
			productlocationPieceList = productLocationDao
					.selectProductlocationListV2(physical_warehouse_id,warehouseId,
							goodsIdList, Location.LOCATION_TYPE_PIECE_PICK,"NORMAL");
			productlocationBoxList = productLocationDao
					.selectProductlocationListV2(physical_warehouse_id,warehouseId,
							goodsIdList, Location.LOCATION_TYPE_BOX_PICK,"NORMAL");
			productlocationStockList = productLocationDao
					.selectProductlocationListV2(physical_warehouse_id,warehouseId,
							goodsIdList, Location.LOCATION_TYPE_STOCK,"NORMAL");
			productlocationReturnList = productLocationDao
					.selectProductlocationListV2(physical_warehouse_id,warehouseId,
							goodsIdList, Location.LOCATION_TYPE_RETURN,"NORMAL");
		}
		//-gt  良品
		else if(ordertype.equalsIgnoreCase("gt_normal")){
			productlocationPieceList = productLocationDao
					.selectProductlocationListV6(physical_warehouse_id,warehouseId,
							goodsIdList, Location.LOCATION_TYPE_PIECE_PICK,"NORMAL");
			productlocationBoxList = productLocationDao
					.selectProductlocationListV6(physical_warehouse_id,warehouseId,
							goodsIdList, Location.LOCATION_TYPE_BOX_PICK,"NORMAL");
			productlocationStockList = productLocationDao
					.selectProductlocationListV6(physical_warehouse_id,warehouseId,
							goodsIdList, Location.LOCATION_TYPE_STOCK,"NORMAL");
			
			//退回区要包含耗材区
			productlocationReturnList = productLocationDao
					.selectProductlocationListV9(physical_warehouse_id,warehouseId,
							goodsIdList, "('RETURN_LOCATION','PACKBOX_LOCATION')","NORMAL");
		}
		//-gt  二手  所有的商品都在二手区
		else{			
			productlocationReturnList = productLocationDao
					.selectProductlocationListV6(physical_warehouse_id,warehouseId,
							goodsIdList, Location.LOCATION_TYPE_DEFECTIVE,"DEFECTIVE");
		}

		// 如果是toB订单 gt_normal   gt_defective都需要忽略箱拣货区和存储区的区别
		if (ordertype.equalsIgnoreCase("gt_normal")||ordertype.equalsIgnoreCase("Allot_2B")) {

			// 如果是toB订单 将存储区当做箱拣货区用
			productlocationBoxList.addAll(productlocationStockList);
			productlocationStockList.clear();
		}

		// 保存每种商品的id
		Map<String, Integer> productBoxPickStartNumber = new HashMap<String, Integer>();
		for (Product p : productList) {
			productBoxPickStartNumber.put("" + p.getProduct_id(),
					p.getBox_pick_start_number());
		}

		// 每个商品的库存信息
		Map<String, List<ProductLocation>> productlocationPieceListMap = new HashMap<String, List<ProductLocation>>();
		Map<String, List<ProductLocation>> productlocationBoxListMap = new HashMap<String, List<ProductLocation>>();
		Map<String, List<ProductLocation>> productlocationStockListMap = new HashMap<String, List<ProductLocation>>();
		Map<String, List<ProductLocation>> productlocationReturnListMap = new HashMap<String, List<ProductLocation>>();

		// 求出每种商品的件拣货区可用数量
		Map<String, Integer> pieceAvailableMap = new HashMap<String, Integer>();
		// 求出每种商品的箱拣货区可用数量
		Map<String, Integer> boxAvailableMap = new HashMap<String, Integer>();
		// 求出每种商品的存储区可用数量
		Map<String, Integer> stockAvailableMap = new HashMap<String, Integer>();
		// 求出每种商品的退货区可用数量
		Map<String, Integer> returnAvailableMap = new HashMap<String, Integer>();
		
		
		// 求出每种商品的件拣货区可用数量
		Map<String, Integer> pieceAvailableMap1 = new HashMap<String, Integer>();
		// 求出每种商品的箱拣货区可用数量
		Map<String, Integer> boxAvailableMap1 = new HashMap<String, Integer>();
		// 求出每种商品的存储区可用数量
		Map<String, Integer> stockAvailableMap1 = new HashMap<String, Integer>();
		// 求出每种商品的退货区可用数量
		Map<String, Integer> returnAvailableMap1 = new HashMap<String, Integer>();

		// 将每个库区
		cutProductLocationByProductId(productlocationPieceListMap, goodsIdList,
				productlocationPieceList);
		cutProductLocationByProductId(productlocationBoxListMap, goodsIdList,
				productlocationBoxList);
		cutProductLocationByProductId(productlocationStockListMap, goodsIdList,
				productlocationStockList);
		cutProductLocationByProductId(productlocationReturnListMap,
				goodsIdList, productlocationReturnList);

		// 计算每个库区每个商品的可用数量
		calculateProductAvailable(goodsIdList, productlocationPieceListMap,
				pieceAvailableMap);
		calculateProductAvailable(goodsIdList, productlocationBoxListMap,
				boxAvailableMap);
		calculateProductAvailable(goodsIdList, productlocationStockListMap,
				stockAvailableMap);
		calculateProductAvailable(goodsIdList, productlocationReturnListMap,
				returnAvailableMap);

		int warehouse_id = 0;
		java.util.Date now = new java.util.Date();
		String createTime = DateUtils.getStringTime(now);

		List<Map> batchpickOrderGood = new ArrayList<Map>();
		// 记录任务
		List<Task> taskList = new ArrayList<Task>();
		// 记录库存信息
		List<ProductLocation> productlocationListUpdate = new ArrayList<ProductLocation>();
		
		//记录波此单操作日志
		List<UserActionBatchPick> userActionBatchPickList = new ArrayList<UserActionBatchPick>();
		
		//记录-gt订单操作日志
		List<UserActionOrder> userActionOrderList = new ArrayList<UserActionOrder>();

		
		// 记录每件商品缺少的件数
		Map<String, Integer> pieceMap = new HashMap<String, Integer>();
		// 记录每件商品缺少的 箱数*spec
		Map<String, Integer> boxMap = new HashMap<String, Integer>();

		// 记录每件商品 的箱容
		Map<String, Integer> specMap = new HashMap<String, Integer>();

		List<Integer> batchPickYList = new ArrayList<Integer>();
		List<Integer> batchPickEList = new ArrayList<Integer>();
		List<Integer> gtYList=new ArrayList<Integer>();
		List<Integer> gtEList=new ArrayList<Integer>();
		// 逐个波此单/-Gt订单处理
		for (Integer id : list) {

			logger.info("id="+id+";customer_id="+customer_id +"start1");
			// 将库存最多库位放在前面
			Collections.sort(productlocationPieceList);
			Collections.sort(productlocationBoxList);
			Collections.sort(productlocationReturnList);

			//  波此单||Gt订单    的信息的商品信息
			batchpickOrderGood = pickOrderGoodMap.get(id + "");
			boolean isAvailable = true;
			boolean isSpec=false;
			logger.info("dlyao-"+id+"-"+batchpickOrderGood.size());
			for (Map m : batchpickOrderGood) {

				logger.info("id="+id+";customer_id="+customer_id +"product_id="+m.get("product_id"));
				int product_id = m.get("product_id") == null ? 0 : Integer
						.parseInt(m.get("product_id").toString());
				logger.info("id="+id+";customer_id="+customer_id +"product_id="+product_id);
				int goods_numbers = m.get("goods_number") == null ? 0
						: Integer.parseInt(m.get("goods_number").toString());
				
				logger.info("spec"+m.get("spec"));
				int spec = m.get("spec") == null ? -1 : Integer.parseInt(m.get(
						"spec").toString());
				
				if(spec<1){
					logger.info("spec"+spec);
					isSpec=true;
					break;
				}
				int available1 = pieceAvailableMap.get(product_id + "") == null ? 0
						: Integer.parseInt(pieceAvailableMap.get(
								product_id + "").toString());
				int available2 = boxAvailableMap.get(product_id + "") == null ? 0
						: Integer.parseInt(boxAvailableMap.get(product_id + "")
								.toString());
				int available4 = returnAvailableMap.get(product_id + "") == null ? 0
						: Integer.parseInt(returnAvailableMap.get(
								product_id + "").toString());
				// 退货区够用
				if (goods_numbers <= available4) {
					logger.info("退货区够用");
					continue;
				}
				// 退货区不够
				else {
					goods_numbers = goods_numbers - available4; // 去掉退货区的数量
					int boxes = goods_numbers / spec;
					int subnum = goods_numbers % spec;

					int pickStartNum=(null==productBoxPickStartNumber.get("" + product_id)?9999:productBoxPickStartNumber.get("" + product_id));
					if(!"Allot".equalsIgnoreCase(ordertype)){
						pickStartNum=1;
					}
					if ((boxes >= pickStartNum 
							&& (goods_numbers > available2+available1 
									|| subnum > available1))
						|| (boxes < pickStartNum 
								&& goods_numbers > available1)) {
						isAvailable = false;
						logger.info("不能进行分配");
						break;
					}
				}

			}

			if(isSpec){
				continue;
			}
			
			// 商品足够，进行库存分配
			if (isAvailable) {

				for (Map m : batchpickOrderGood) {

					logger.info("id="+id+";customer_id="+customer_id +"Allot true");
					String goods_name = m.get("goods_name") == null ? "" : m
							.get("goods_name").toString();
					int product_id = m.get("product_id") == null ? 0 : Integer
							.parseInt(m.get("product_id").toString());
					
					String barcode = m.get("barcode") == null ? "" : m.get(
							"barcode").toString();
					int spec = m.get("spec") == null ? 1 : Integer.parseInt(m
							.get("spec").toString());
					int goods_numbers = m.get("goods_number") == null ? 0
							: Integer.parseInt(m.get("goods_number")
									.toString());
					int available1 = pieceAvailableMap.get(product_id + "") == null ? 0
							: Integer.parseInt(pieceAvailableMap.get(
									product_id + "").toString());
					int available2 = boxAvailableMap.get(product_id + "") == null ? 0
							: Integer.parseInt(boxAvailableMap.get(
									product_id + "").toString());
					int available4 = returnAvailableMap.get(product_id + "") == null ? 0
							: Integer.parseInt(returnAvailableMap.get(
									product_id + "").toString());
					// 退货区够用
					if (goods_numbers <= available4) {

						returnAvailableMap.put(product_id + "", available4
								- goods_numbers);
						doCheckQualityAllot(ordertype,productlocationReturnList,
								id, goods_numbers,
								physical_warehouse_id, customer_id, createTime,
								taskList, productlocationListUpdate,product_id);
					}
					// 退货区不够
					else {
						
						// 扣除退货区
						if(available4!=0){
							returnAvailableMap.put(product_id + "", 0);
							doCheckQualityAllot(ordertype,productlocationReturnList,
									id, available4,
									physical_warehouse_id, customer_id, createTime,
									taskList, productlocationListUpdate,product_id);
						}
						

						// 其余的在件拣货区和箱拣货区拣货
						goods_numbers = goods_numbers - available4;
						int boxes = goods_numbers / spec;
						int subnum = goods_numbers % spec;

						int pickStartNum=(null==productBoxPickStartNumber.get("" + product_id)?9999:productBoxPickStartNumber.get("" + product_id));
						if(!"Allot".equalsIgnoreCase(ordertype)){
							pickStartNum=1;
						}
						
						if (boxes >= pickStartNum) {

							//存储区和存储区
							if(boxes*spec<=available2){
								if (subnum > 0) {
									pieceAvailableMap.put(product_id + "",
											available1 - subnum);
									doCheckQualityAllot(ordertype,productlocationPieceList,
											id, subnum,
											physical_warehouse_id, customer_id,
											createTime, taskList,
											productlocationListUpdate,product_id);
								}
								boxAvailableMap.put(product_id + "", available2
										- boxes * spec);
								doCheckQualityAllot(ordertype,productlocationBoxList,
										id, boxes * spec,
										physical_warehouse_id, customer_id,
										createTime, taskList,
										productlocationListUpdate,product_id);
							}
							else{
								boxAvailableMap.put(product_id + "", available2
										- available2);
								doCheckQualityAllot(ordertype,productlocationBoxList,
										id, available2,
										physical_warehouse_id, customer_id,
										createTime, taskList,
										productlocationListUpdate,product_id);
								
								pieceAvailableMap.put(product_id + "",
										available1 - goods_numbers + available2);
								
								doCheckQualityAllot(ordertype,productlocationPieceList,
										id, goods_numbers - available2,
										physical_warehouse_id, customer_id,
										createTime, taskList,
										productlocationListUpdate,product_id);
							}
							

						} else {
							pieceAvailableMap.put(product_id + "", available1
									- goods_numbers);
							doCheckQualityAllot(ordertype,productlocationPieceList,
									id, goods_numbers,
									physical_warehouse_id, customer_id,
									createTime, taskList,
									productlocationListUpdate,product_id);
						}
					}

				}
				//区分波此单 和 -gt订单
                if(ordertype.equalsIgnoreCase("Allot")||ordertype.equalsIgnoreCase("Allot_2B")){
                	batchPickYList.add(id);
                	UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
    				userActionBatchPick.setAction_note("波次单:" + id+"分配库存成功");
    				userActionBatchPick.setAction_type("ALLOTINVENTORY");
    				userActionBatchPick.setCreated_time(new Date());
    				userActionBatchPick.setCreated_user("System");
    				userActionBatchPick.setStatus("INIT");
    				userActionBatchPick.setBatch_pick_id(id);
    				userActionBatchPickList.add(userActionBatchPick);
                }else{
                	gtYList.add(id);
                	
                	UserActionOrder userActionOrder = new UserActionOrder();
            		userActionOrder.setAction_note("供应商退货订单"+id+"分配库存成功");
            		userActionOrder.setAction_type("RESERVED");
            		userActionOrder.setCreated_time(new Date());
            		userActionOrder.setCreated_user("system");
            		userActionOrder.setOrder_goods_id(null);
            		userActionOrder.setOrder_id(id);
            		userActionOrder.setOrder_status("RESERVED");
            		userActionOrderList.add(userActionOrder);
                }
				

			}
			// 记录分配失败的波此单或者-gt订单
			else {
				
				if(ordertype.equalsIgnoreCase("Allot")||ordertype.equalsIgnoreCase("Allot_2B")){
                	batchPickEList.add(id);
                	
                	UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
    				userActionBatchPick.setAction_note("波次单:" + id+"分配库存失败");
    				userActionBatchPick.setAction_type("ALLOTINVENTORY");
    				userActionBatchPick.setCreated_time(new Date());
    				userActionBatchPick.setCreated_user("System");
    				userActionBatchPick.setStatus("INIT");
    				userActionBatchPick.setBatch_pick_id(id);
    				userActionBatchPickList.add(userActionBatchPick);
                }
				
				else{
					gtEList.add(id);
					UserActionOrder userActionOrder = new UserActionOrder();
            		userActionOrder.setAction_note("供应商退货订单"+id+"分配库存失败");
            		userActionOrder.setAction_type("RESERVED");
            		userActionOrder.setCreated_time(new Date());
            		userActionOrder.setCreated_user("system");
            		userActionOrder.setOrder_goods_id(null);
            		userActionOrder.setOrder_id(id);
            		userActionOrder.setOrder_status("RESERVED");
            		userActionOrderList.add(userActionOrder);
				}

			}

		}
		//备份数量
		pieceAvailableMap1.putAll(pieceAvailableMap);
		boxAvailableMap1.putAll(boxAvailableMap);
		returnAvailableMap1.putAll(returnAvailableMap);
		
		
		//计算总的缺货数量
		if(ordertype.equalsIgnoreCase("Allot")||ordertype.equalsIgnoreCase("Allot_2B")){
			logger.info("计算缺货数量："+ordertype+batchPickEList.toString());
			if(!WorkerUtil.isNullOrEmpty(batchPickEList)){
				calculateAllRepleshment(pickOrderGoodMap,ordertype,
						productBoxPickStartNumber, pieceAvailableMap1,
						boxAvailableMap1, returnAvailableMap1, pieceMap, boxMap,
						specMap, batchPickEList);
			}
			logger.info("计算缺货数量：calculateAllRepleshment error");
		}else{
			if(!WorkerUtil.isNullOrEmpty(gtEList)){
				calculateAllRepleshment(pickOrderGoodMap,ordertype,
						productBoxPickStartNumber, pieceAvailableMap1,
						boxAvailableMap1, returnAvailableMap1, pieceMap, boxMap,
						specMap, gtEList);
			}
		}
		
		
		
		
		
		List<Location> toLocationBoxList = locationDao.selectAllLocationList(
				physical_warehouse_id, Location.LOCATION_TYPE_BOX_PICK);// "BOX_PICK_LOCATION"
		List<Location> toLocationPieceList = locationDao.selectAllLocationList(
				physical_warehouse_id, Location.LOCATION_TYPE_PIECE_PICK);// "PIECE_PICK_LOCATION"
		
		//记录允许混放的库位
		List<Integer> toLocationMixBoxList=new ArrayList<Integer>();
		List<Integer> toLocationMixPieceList=new ArrayList<Integer>();		
		
		
		for(Location l:toLocationBoxList){
			if(l.getCan_mix_batch()==1){
				toLocationMixBoxList.add(l.getLocation_id());
			}
		}
		
		for(Location l:toLocationPieceList){
			if(l.getCan_mix_batch()==1){
				toLocationMixPieceList.add(l.getLocation_id());
			}
		}

		// 系统当前仓库的在途补货任务 参数：物理仓库 订单级别 源库位类型
		List<Task> onstartPieceTaskList = taskDao.getAllOnStartTask(
				physical_warehouse_id,goodsIdList, Task.ORDERREPLENISHMENTTASKLEVEL,
				Location.LOCATION_TYPE_BOX_PICK,Task.PIECE_REPLENISHMENT);
		List<Task> onstartBoxTaskList = taskDao.getAllOnStartTask(
				physical_warehouse_id, goodsIdList,Task.ORDERREPLENISHMENTTASKLEVEL,
				Location.LOCATION_TYPE_STOCK,Task.BOX_REPLENISHMENT);

		Map<String, Integer> onstartProductMap = new HashMap<String, Integer>();
		for (Task t : onstartPieceTaskList) {
			int p = onstartProductMap.get("piece" + t.getProduct_id()) == null ? 0
					: onstartProductMap.get("piece" + t.getProduct_id());
			onstartProductMap.put("piece" + t.getProduct_id(),
					p + t.getQuantity());
		}

		for (Task t : onstartBoxTaskList) {
			int p = onstartProductMap.get("box" + t.getProduct_id()) == null ? 0
					: onstartProductMap.get("box" + t.getProduct_id());
			onstartProductMap.put("box" + t.getProduct_id(),
					p + t.getQuantity());
		}

		logger.info("!ordertype.equalsIgnoreCase(gt_defective) ordertype:"+ordertype+!ordertype.equalsIgnoreCase("gt_defective"));
		if(!ordertype.equalsIgnoreCase("gt_defective")){
			// 更新补货任务
			//生产日期排序   数量从小到大
			ProductLocation plComparator=new ProductLocation();
			Collections.sort(productlocationPieceList,plComparator);
			Collections.sort(productlocationBoxList,plComparator);
			Collections.sort(productlocationStockList,plComparator);
			
			logger.info("pieceMap:"+pieceMap.toString());
			logger.info("boxMap:"+boxMap.toString());
			createFastReplenishmentTask(configReplenishmentMap, level,
					physical_warehouse_id, customer_id, createTime, goodsIdList,
					pieceMap, boxMap, productlocationPieceList,
					productlocationBoxList, productlocationStockList,
					pieceAvailableMap, boxAvailableMap, stockAvailableMap, specMap,
					taskList, toLocationBoxList, toLocationPieceList,toLocationMixBoxList,toLocationMixPieceList,
					productlocationListUpdate, onstartProductMap,productlocationExceptionMap);
		}
		

		// 更新拣货任务和补货任务
		List<Task> initTaskList = taskDao.getAllInitTaskForUpdate(
				physical_warehouse_id, goodsIdList,Task.ORDERREPLENISHMENTTASKLEVEL);
		List<Task> updateTaskList = new ArrayList<Task>();
		
		//需要记录的任务日志
		List<UserActionTask> userActionTaskList=new ArrayList<UserActionTask>();
		// 循环历史的未进行任务
		for (Task t : initTaskList) {
			// 循环此次任务
			if (t.getTask_status().equalsIgnoreCase("INIT")) {
				for (Task ta : taskList) {
					if (t.getFrom_pl_id() - ta.getFrom_pl_id() == 0
							&& ta.getTask_type().equalsIgnoreCase(
									"REPLENISHMENT")&&ta.getMark()==t.getMark()) {
						t.setTask_status("CANCEL");
						
						UserActionTask userActionTask = new UserActionTask();
						userActionTask.setTask_id(t.getTask_id());
						userActionTask.setAction_note("合并任务取消");
						userActionTask.setAction_type(Task.TASK_STATUS_CANCEL);
						userActionTask.setTask_status("CANCEL");
						userActionTask.setCreated_user("SYSTEM");
						userActionTask.setCreated_time(new Date());
						userActionTaskList.add(userActionTask);
						
						updateTaskList.add(t);
						ta.setQuantity(ta.getQuantity() + t.getQuantity());
					}
				}
			}
		}

		
		if (!com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(updateTaskList)) {
			
			taskDao.setTaskListCancel(updateTaskList);
			
		}

		if (!WorkerUtil.isNullOrEmpty(taskList)) {
			
			for(Task task:taskList){
				taskDao.insertTask(task);
				
				UserActionTask userActionTask = new UserActionTask();
				userActionTask.setTask_id(task.getTask_id());
				userActionTask.setAction_note("创建");
				userActionTask.setAction_type(Task.TASK_STATUS_INIT);
				userActionTask.setTask_status("INIT");
				userActionTask.setCreated_user("SYSTEM");
				userActionTask.setCreated_time(new Date());
				userActionTaskList.add(userActionTask);	
			}
			
			//taskDao.insertTaskList(taskList);
		}
		
		if (!WorkerUtil.isNullOrEmpty(userActionTaskList)) {
			userActionTaskDao.insertList(userActionTaskList);
		}
		// 更新库存信息
		if (!WorkerUtil.isNullOrEmpty(productlocationListUpdate)) {
			//归并其中一部分主键相同的更新记录（使用 qty_available - ${item2.qty_available}）所以分配里面记录的变化量应该是差异值，且未正数
			Map<Integer,ProductLocation> updatePlMap =new HashMap<Integer,ProductLocation>();
			List<ProductLocation> updatePlList=new ArrayList<ProductLocation>();
			for(ProductLocation pl:productlocationListUpdate){
				//如果没有则加到记录中
				if(updatePlMap.get(pl.getPl_id())==null){
					updatePlMap.put(pl.getPl_id(), pl);
					updatePlList.add(pl);
				}else{
					ProductLocation pl2=updatePlMap.get(pl.getPl_id());
					pl2.setQty_available(pl2.getQty_available()+pl.getQty_available());
				}
			}
			
			productLocationDao
				.updateProductLocationByAllotJob(updatePlList);
		}
		// 更新波次单状态
		if (!WorkerUtil.isNullOrEmpty(batchPickEList)) {
			batchPickDao.updateReserveStatus(batchPickEList, "E");
		}
		if (!WorkerUtil.isNullOrEmpty(batchPickYList)) {
			batchPickDao.updateReserveStatus(batchPickYList, "Y");
		}
		if (!WorkerUtil.isNullOrEmpty(gtYList)) {
		logger.info("physical_warehouse_id="+physical_warehouse_id+" ;customer_id="+customer_id +" ;type="+ordertype+" ;gtYList size:["+gtYList.size()+"] end");
		orderInfoDao.updateReserveStatus(gtYList, "RESERVED");
		orderProcessDao.updateStatusToReserved(gtYList, "RESERVED");
		}
		
		if(!WorkerUtil.isNullOrEmpty(userActionOrderList)){
			logger.info("physical_warehouse_id="+physical_warehouse_id+" ;customer_id="+customer_id +" ;type="+ordertype+" ;userActionOrderList size:["+userActionOrderList.size()+"] end");
			userActionOrderDao.batchInsert(userActionOrderList);
		}

		
		if(!WorkerUtil.isNullOrEmpty(userActionBatchPickList)){
			logger.info("physical_warehouse_id="+physical_warehouse_id+" ;customer_id="+customer_id +" ;type="+ordertype+" ;userActionBatchPickList size:["+userActionBatchPickList.size()+"] end");
			userActionBatchPickDao.batchInsert(userActionBatchPickList);
		}
		logger.info("physical_warehouse_id="+physical_warehouse_id+" ;customer_id="+customer_id +" ;type="+ordertype+" ;id:["+list.toString()+"] end");
	}

	private void calculateAllRepleshment(
			Map<String, List<Map>> pickOrderGoodMap,
			String ordertype, Map<String, Integer> productBoxPickStartNumber,
			Map<String, Integer> pieceAvailableMap,
			Map<String, Integer> boxAvailableMap,
			Map<String, Integer> returnAvailableMap,
			Map<String, Integer> pieceMap, Map<String, Integer> boxMap,
			Map<String, Integer> specMap, List<Integer> list) {
		List<Map> batchpickOrderGood;
		for(Integer id : list){

			batchpickOrderGood = pickOrderGoodMap.get(id + "");
			
			int old1 = 0;
			int old2 = 0;
//			boolean isSpec=false;
			logger.info("dlyao_"+id+"_"+batchpickOrderGood.size());
			for (Map m : batchpickOrderGood) {
				logger.info("dlyao_"+m.get("product_id"));
				int product_id = m.get("product_id") == null ? 0 : Integer
						.parseInt(m.get("product_id").toString());
				logger.info("dlyao_"+m.get("spec"));
				int spec = m.get("spec") == null ? -1 : Integer.parseInt(m
						.get("spec").toString());
				if(spec<1){
//					isSpec=true;
					logger.info("箱规未维护"+product_id);
					continue;
				}
				int goods_numbers = m.get("goods_number") == null ? 0
						: Integer.parseInt(m.get("goods_number")
								.toString());
				
				int available1 = pieceAvailableMap.get(product_id + "") == null ? 0
						: Integer.parseInt(pieceAvailableMap.get(
								product_id + "").toString());
				int available2 = boxAvailableMap.get(product_id + "") == null ? 0
						: Integer.parseInt(boxAvailableMap.get(
								product_id + "").toString());
				int available4 = returnAvailableMap.get(product_id + "") == null ? 0
						: Integer.parseInt(returnAvailableMap.get(
								product_id + "").toString());

				if (goods_numbers <= available4) {
					returnAvailableMap.put(product_id + "", available4
							- goods_numbers);
				} else {
					returnAvailableMap.put(product_id + "", 0);
					goods_numbers = goods_numbers - available4;
					int boxes = goods_numbers / spec;
					int subnum = goods_numbers % spec;

					specMap.put("" + product_id, spec);

					int pickStartNum=(null==productBoxPickStartNumber.get("" + product_id)?9999:productBoxPickStartNumber.get("" + product_id));

					//-gt  toB
					if(!"Allot".equalsIgnoreCase(ordertype)){
						pickStartNum=1;
						
						//件+箱
						if(boxes >= pickStartNum){
							//件不够+总数足够
							if(subnum>available1&&goods_numbers<=available1+available2){
								// 记录该商品缺少的数目
								pieceAvailableMap.put("" + product_id, 0);
								old1 = pieceMap.get("" + product_id) == null ? 0
										: pieceMap.get("" + product_id);
								pieceMap.put("" + product_id, old1 + subnum-available1);
								boxAvailableMap.put("" + product_id, available1+available2-goods_numbers);
							}
							//件足够+总数不够 
							else if(subnum<=available1&&goods_numbers>available1+available2){
								boxAvailableMap.put("" + product_id, 0);
								pieceAvailableMap.put("" + product_id, 0);
							}
							//件足够 总数也足够  是其他商品不足导致分配失败
							//箱够拣了
							else if(boxes*spec<=available2){
								boxAvailableMap.put("" + product_id, available2-boxes*spec);
								pieceAvailableMap.put("" + product_id, available1-subnum);
							}
							//箱不够拣
							else{
								boxAvailableMap.put("" + product_id, 0);
								pieceAvailableMap.put("" + product_id, available1+available2-goods_numbers);
							}
							
						}
						//件
						else
						{
							//不够
							if(goods_numbers>available1){
								logger.info("全从件拣货区拣货1"+product_id+"_"+available1+"_"+goods_numbers);
								pieceAvailableMap.put("" + product_id, 0);
								old1 = pieceMap.get("" + product_id) == null ? 0
										: pieceMap.get("" + product_id);
								pieceMap.put("" + product_id, old1 + goods_numbers-available1);
							}
							//足够
							else {
								logger.info("全从件拣货区拣货2"+product_id+"_"+available1+"_"+goods_numbers);
								pieceAvailableMap.put("" + product_id, available1-goods_numbers);
							}
						}

					}
					
					//toC
					else
					{
						if (boxes >= pickStartNum
								&& (available2 < boxes * spec||available1<subnum)) {
							logger.info("从箱拣货区和件拣货区拣货"+product_id);
							logger.info("subnum_available1"+subnum+"_"+available1);
							if (subnum>available1) {
								// 记录该商品缺少的数目
								pieceAvailableMap.put("" + product_id, 0);
								old1 = pieceMap.get("" + product_id) == null ? 0
										: pieceMap.get("" + product_id);
								pieceMap.put("" + product_id, old1 + subnum-available1);

							}
							else{
								pieceAvailableMap.put("" + product_id, available1-subnum);
							}
							
							logger.info("boxes * spec >available2"+boxes * spec +"_"+available2);
							if(boxes * spec >available2){
								// 记录该商品缺少的件数
								boxAvailableMap.put("" + product_id, 0);
								old2 = boxMap.get("" + product_id) == null ? 0
										: boxMap.get("" + product_id);
								boxMap.put("" + product_id, old2 + boxes * spec-available2);
							}
							else{
								boxAvailableMap.put("" + product_id, available2-boxes * spec);
							}
						} 
						
						else if(available1<goods_numbers){
							logger.info("全从件拣货区拣货1"+product_id+"_"+available1+"_"+goods_numbers);
							pieceAvailableMap.put("" + product_id, 0);
							old1 = pieceMap.get("" + product_id) == null ? 0
									: pieceMap.get("" + product_id);
							pieceMap.put("" + product_id, old1 + goods_numbers-available1);
						}else{
							logger.info("全从件拣货区拣货2"+product_id+"_"+available1+"_"+goods_numbers);
							pieceAvailableMap.put("" + product_id, available1-goods_numbers);
						}

					}					
					
				}

			}
		
		}
	}

	/**
	 * 
	 * @param configReplenishmentMap
	 *            补货规则配置
	 * @param createTime
	 *            当前时间
	 * @param customer_id
	 *            业务组
	 * @param physical_warehouse_id
	 *            物理仓库
	 * @param level
	 *            紧急补货级别
	 * @param goodsIdList
	 *            商品列表
	 * @param pieceMap
	 *            件拣货区缺货数量 集合
	 * @param boxMap
	 *            箱拣货区缺货数量 集合
	 * @param productlocationPieceList
	 *            件拣货区库存
	 * @param productlocationBoxList
	 *            箱拣货区库存
	 * @param productlocationStockList
	 *            存储区库存
	 * @param pieceAvailableMap
	 *            件拣货区可用数量
	 * @param boxAvailableMap
	 *            箱拣货区可用数量
	 * @param stockAvailableMap
	 *            存储区可用数量
	 * @param specMap
	 *            每件商品的箱容
	 * 
	 * @param taskList
	 *            任务列表
	 * @param toLocationPieceList
	 *            件拣货区库位
	 * @param toLocationBoxList
	 *            箱拣货区库位
	 * @param productlocationListUpdate
	 *            需要更新的库存信息
	 * @param onstartProductMap
	 *            仓库已经有的数目
	 * @param productlocationExceptionMap 记录每件商品有被标记为异常的区域
	 */
	private void createFastReplenishmentTask(
			Map<String, ConfigReplenishment> configReplenishmentMap,
			Integer level, Integer physical_warehouse_id, Integer customer_id,
			String createTime, List<Integer> goodsIdList,
			Map<String, Integer> pieceMap, Map<String, Integer> boxMap,
			List<ProductLocation> productlocationPieceList,
			List<ProductLocation> productlocationBoxList,
			List<ProductLocation> productlocationStockList,
			Map<String, Integer> pieceAvailableMap,
			Map<String, Integer> boxAvailableMap,
			Map<String, Integer> stockAvailableMap,
			Map<String, Integer> specMap, List<Task> taskList,
			List<Location> toLocationBoxList,
			List<Location> toLocationPieceList,List<Integer>toLocationMixBoxList,List<Integer>toLocationMixPieceList,
			List<ProductLocation> productlocationListUpdate,
			Map<String, Integer> onstartProductMap,Map<String,List<Integer>> productlocationExceptionMap) {
		
		logger.info("createFastReplenishmentTask physical_warehouse_id="+physical_warehouse_id+";customer_id="+customer_id + "createFastReplenishmentTask");
		logger.info("createFastReplenishmentTask pieceMap:{"+pieceMap.toString()+ "},createFastReplenishmentTask");
		logger.info("createFastReplenishmentTask boxMap:{"+boxMap.toString()+ "},createFastReplenishmentTask");
		int need1, need2, spec, stockAvailable, boxAvailable;
		for (Integer product_id : goodsIdList) {
			need1 = pieceMap.get(product_id + "") == null ? 0 : pieceMap
					.get(product_id + "");
			need2 = boxMap.get(product_id + "") == null ? 0 : boxMap
					.get(product_id + "");
			spec = specMap.get(product_id + "") == null ? -1 : specMap
					.get(product_id + "");
			logger.info("createFastReplenishmentTask need1:"+need1+" need2:"+need2+" spec:"+spec);
			stockAvailable = stockAvailableMap.get(product_id + "") == null ? 0
					: stockAvailableMap.get(product_id + "");
			boxAvailable = boxAvailableMap.get(product_id + "") == null ? 0
					: boxAvailableMap.get(product_id + "");
			ConfigReplenishment config = configReplenishmentMap.get(product_id
					+ "") == null ? null : configReplenishmentMap
					.get(product_id + "");
			int pieceMax = 0;
			int boxMax = 0;
			int pieceOnShelf = 0;
			int pieceInTask = 0;
			int boxOnShelf = 0;
			int boxInTask = 0;

			List<Integer> productExceptionLocationIdList=new ArrayList<Integer>();
			if(!com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(productlocationExceptionMap.get(product_id+""))){
				productExceptionLocationIdList=productlocationExceptionMap.get(product_id+"");
			}
			
			Map<String, Location> toLocationBoxMap = new HashMap<String, Location>();
			Map<String, Location> toLocationPieceMap = new HashMap<String, Location>();

			for (Location l : toLocationBoxList) {
				toLocationBoxMap.put(l.getLocation_barcode() + "", l);
			}

			for (Location l : toLocationPieceList) {
				toLocationPieceMap.put(l.getLocation_barcode() + "", l);
			}

			pieceOnShelf = pieceAvailableMap.get("" + product_id) == null ? 0
					: pieceAvailableMap.get("" + product_id);
			boxOnShelf = boxAvailableMap.get("" + product_id) == null ? 0
					: boxAvailableMap.get("" + product_id);
			
			
				
			pieceInTask = onstartProductMap.get("piece" + product_id) == null ? 0
						: onstartProductMap.get("piece" + product_id);
				
			boxInTask = onstartProductMap.get("box" + product_id) == null ? 0
						: onstartProductMap.get("box" + product_id);
				
			

			logger.info("createFastReplenishmentTask need1:"+need1+" ,pieceOnShelf:"+pieceOnShelf+" ,pieceInTask:"+pieceInTask);
			need1=need1-pieceInTask;
			if(config!=null){
				pieceMax =  config.getPiece_location_max_quantity()- pieceOnShelf - pieceInTask;
			}
			logger.info("createFastReplenishmentTask need1:"+need1);
			if (need1 > 0) {

				if (level == 2) {
					need1 = Math.max(need1, pieceMax);

				} else if (level == 3) {
					need1 = need1 + pieceMax;
					
				}
                int need3=need1;
				if (need1 % spec != 0) {
					need3 = (need1 / spec + 1) * spec;
				}
				
				
				logger.info("createFastReplenishmentTask need1:"+need1+" ,need3:"+need3+" ,boxAvailable:"+boxAvailable);
				if (need3 > boxAvailable&&boxAvailable>=0) {					
					boxAvailableMap.put(product_id + "",0);
					doCreateFastReplenishmentTask1(config,
							physical_warehouse_id, customer_id, createTime,
							boxAvailable, productlocationPieceList,
							productlocationBoxList, taskList, product_id,
							toLocationPieceList,toLocationMixPieceList, productlocationListUpdate,productExceptionLocationIdList);
					boxOnShelf=0;
					need2 = need1 + need2 - boxAvailable;
					
				}else if(need3>0){
					boxAvailableMap.put(product_id + "",boxAvailable-need1);
					doCreateFastReplenishmentTask1(config,
							physical_warehouse_id, customer_id, createTime,
							need3, productlocationPieceList,
							productlocationBoxList, taskList, product_id,
							toLocationPieceList,toLocationMixPieceList, productlocationListUpdate,productExceptionLocationIdList);
					boxOnShelf=boxAvailable-need3;
				}
				else{
					need2=need1+need2;
				}
				

			}	
			logger.info("createFastReplenishmentTask need2:"+need2+" ,boxOnShelf:"+boxOnShelf+" ,boxInTask:"+boxInTask);
			need2=need2-boxInTask;
			if(config!=null){
				boxMax =config.getBox_location_max_quantity()- boxOnShelf - boxInTask;
			}
			logger.info("createFastReplenishmentTask need2:"+need2);
			if (need2 > 0) {				
				
				if (level == 2) {
					need2 = Math.max(need2, boxMax);
				} else if (level == 3) {
					need2 = need2 + boxMax;
				}

				if (need2 % spec != 0) {
					need2 = (need2 / spec + 1) * spec;
				}
				
				logger.info("createFastReplenishmentTask need2:"+need2+" ,stockAvailable:"+stockAvailable);
				if (need2>stockAvailable&&stockAvailable>=0) {
					stockAvailableMap.put(product_id + "",0);
					doCreateFastReplenishmentTask2(config, physical_warehouse_id,
							customer_id, createTime, stockAvailable, productlocationBoxList,
							productlocationStockList, taskList, product_id,
							toLocationBoxList,toLocationMixBoxList, productlocationListUpdate,productExceptionLocationIdList,productlocationPieceList,toLocationPieceList,toLocationMixPieceList);
				}
				else if(need2>0){
					stockAvailableMap.put(product_id + "",stockAvailable-need2);
					doCreateFastReplenishmentTask2(config, physical_warehouse_id,
							customer_id, createTime, need2, productlocationBoxList,
							productlocationStockList, taskList, product_id,
							toLocationBoxList,toLocationMixBoxList, productlocationListUpdate,productExceptionLocationIdList,productlocationPieceList,toLocationPieceList,toLocationMixPieceList);
				}
				
			}

		}

	}

	/**
	 * 存储区到箱拣货区
	 * 
	 * @param config
	 * @param physical_warehouse_id
	 * @param customer_id
	 * @param createTime
	 * @param need2
	 * @param productlocationBoxList
	 * @param productlocationStockList
	 * @param taskList
	 * @param product_id
	 * @param toLocationBoxList
	 * @param productlocationListUpdate
	 * @param toLocationMixPieceList 
	 * @param toLocationPieceList 
	 * @param productlocationPieceList 
	 */
	private void doCreateFastReplenishmentTask2(ConfigReplenishment config,
			Integer physical_warehouse_id, Integer customer_id,
			String createTime, int need2,
			List<ProductLocation> productlocationBoxList,
			List<ProductLocation> productlocationStockList,
			List<Task> taskList, Integer product_id,
			List<Location> toLocationBoxList,
			List<Integer> toLocationMixBoxList,
			List<ProductLocation> productlocationListUpdate,
			List<Integer> productExceptionLocationIdList,
			List<ProductLocation> productlocationPieceList,
			List<Location> toLocationPieceList,
			List<Integer> toLocationMixPieceList) {
		// 添加箱拣货任务
		int boxNum = 0;
		logger.info("physical_warehouse_id="+physical_warehouse_id+";customer_id="+customer_id + "stock-->box");
		logger.info("product_id:{"+product_id+ "},stock-->box");
		String boxFrom = "";
		String boxTo = "";
		//新加20161012
		String pieceFrom = "";
		String pieceTo = "";
		if (null != config) {
			boxFrom = null==config.getFrom_box_location_barcode()?"":config.getFrom_box_location_barcode();
			boxTo = null==config.getTo_box_location_barcode()?"":config.getTo_box_location_barcode();
			//新加
			pieceFrom = null==config.getFrom_piece_location_barcode()?"":config.getFrom_piece_location_barcode();
			pieceTo = null==config.getTo_piece_location_barcode()?"":config.getTo_piece_location_barcode();
		}

		// 遍历源库位
		for(int index=0;index<productlocationStockList.size();index++) {
			ProductLocation pl=productlocationStockList.get(index);
		//for (ProductLocation pl : productlocationStockList) {
			if (pl.getProduct_id() - product_id == 0&&pl.getQty_available()>0) {
				ProductLocation plnew = new ProductLocation();
				plnew.setPl_id(pl.getPl_id());

				boolean isFind = false;
				Task task = new Task();
				task.setBatch_pick_id(0);
				task.setBatch_task_id(0);
				task.setPhysical_warehouse_id(physical_warehouse_id);
				task.setCustomer_id(customer_id);
				task.setTask_status("INIT");
				task.setCreated_user("SYSTEM");
				task.setCreated_time(createTime);
				task.setTask_type("REPLENISHMENT");
				task.setTask_level(3);
				task.setMark(Task.BOX_REPLENISHMENT);
				task.setProduct_id(product_id);

				if (null != config && null!=config.getFrom_box_location_barcode() &&null!=config.getTo_box_location_barcode()) {
					for (ProductLocation boxPl : productlocationBoxList) {
						// 目标库位在范围内 且 此库位此sku可以继续创建补货任务
						if ((boxFrom.compareTo(boxPl.getLocation_barcode()) <= 0)
								&& (boxTo.compareTo(boxPl.getLocation_barcode()) >= 0)
								&& (!productExceptionLocationIdList
										.contains(boxPl.getLocation_id()))) {

							if (pl.getProduct_id() - boxPl.getProduct_id() == 0) {

								if (pl.getValidity().equalsIgnoreCase(
										boxPl.getValidity())&&pl.getBatch_sn().equalsIgnoreCase(boxPl.getBatch_sn())) {
									task.setTo_location_id(boxPl
											.getLocation_id());
									// 找到推荐的库位后 跳出循环
									isFind = true;
									break;
								}

								else if (toLocationMixBoxList.contains(boxPl
										.getLocation_id())) {
									// 判断允不允许混放
									task.setTo_location_id(boxPl.getLocation_id());
									   isFind = true;
									   break;
								}

							}

						}
					}

					// 如果没找到完全一样的 找一个可以混放的
					 if(!isFind){
						// 如果没找到完全一样和可以混放的 找一个可以空库位
						for (Location l : toLocationBoxList) {
							
							// 如果此库位在目标库位内
							if (boxFrom.compareToIgnoreCase(l
									.getLocation_barcode()) <= 0
									&& boxTo.compareToIgnoreCase(l
											.getLocation_barcode()) >= 0 && l.getIs_empty().equalsIgnoreCase("Y")) {
								
								task.setTo_location_id(l.getLocation_id());
								l.setIs_empty("N");  //不修改数据库  仅仅用来标识此库位已经
								break;

							}
						}

					}

					

				}
				//如果箱拣货区没有找到就去件拣货区找一个  (因为仓库目前实际上并没有使用箱拣货区)
				if(task.getTo_location_id()-0!=0){
					logger.info("箱补货任务成功在箱拣货区找到推荐库位");
				}
				else if (null != config  && null!=config.getFrom_piece_location_barcode() &&null!=config.getTo_piece_location_barcode()){

					for (ProductLocation piecePl : productlocationPieceList) {
						// 目标库位在范围内 且 此库位此sku可以继续创建补货任务
						if ((pieceFrom.compareTo(piecePl.getLocation_barcode()) <= 0)
								&& (pieceTo.compareTo(piecePl.getLocation_barcode()) >= 0)
								&& (!productExceptionLocationIdList
										.contains(piecePl.getLocation_id()))) {

							if (pl.getProduct_id() - piecePl.getProduct_id() == 0) {

								if (pl.getValidity().equalsIgnoreCase(
										piecePl.getValidity())&&pl.getBatch_sn().equalsIgnoreCase(piecePl.getBatch_sn())) {
									task.setTo_location_id(piecePl
											.getLocation_id());
									// 找到推荐的库位后 跳出循环
									isFind = true;
									break;
								}

								else if (toLocationMixPieceList.contains(piecePl
										.getLocation_id())) {
									// 判断允不允许混放
									task.setTo_location_id(piecePl.getLocation_id());
									   isFind = true;
									   break;
								}

							}

						}
					}

					// 如果没找到完全一样的 找一个可以混放的
					 if(!isFind){
						// 如果没找到完全一样和可以混放的 找一个可以空库位
						for (Location l : toLocationPieceList) {
							
							// 如果此库位在目标库位内
							if (pieceFrom.compareToIgnoreCase(l
									.getLocation_barcode()) <= 0
									&& pieceTo.compareToIgnoreCase(l
											.getLocation_barcode()) >= 0 && l.getIs_empty().equalsIgnoreCase("Y")) {
								
								task.setTo_location_id(l.getLocation_id());
								l.setIs_empty("N");  //不修改数据库  仅仅用来标识此库位已经
								break;

							}
						}

					}

					

				
				}
				
				task.setFrom_pl_id(pl.getPl_id());
				if (pl.getQty_available() + boxNum < need2) {
					task.setQuantity(pl.getQty_available());
					plnew.setQty_available(pl.getQty_available());// 变化量
					boxNum += pl.getQty_available();
					
					pl.setQty_available(0);
					
					taskList.add(task);
					productlocationListUpdate.add(plnew);
					
				} else {

					task.setQuantity(need2 - boxNum);
					plnew.setQty_available(need2 - boxNum);
					pl.setQty_available(pl.getQty_available() - need2
							+ boxNum);// 变化量
					
					taskList.add(task);
					productlocationListUpdate.add(plnew);
					break;
				}
			}
		}
	}
	/**
	 * 箱拣货区到件拣货区
	 * 
	 * @param config
	 * @param physical_warehouse_id
	 * @param customer_id
	 * @param createTime
	 * @param need1
	 * @param productlocationPieceList
	 * @param productlocationBoxList
	 * @param taskList
	 * @param product_id
	 * @param toLocationPieceList
	 * @param productlocationListUpdate
	 */
	private void doCreateFastReplenishmentTask1(ConfigReplenishment config,
			Integer physical_warehouse_id, Integer customer_id,
			String createTime, int need1,
			List<ProductLocation> productlocationPieceList,
			List<ProductLocation> productlocationBoxList, List<Task> taskList,
			Integer product_id, List<Location> toLocationPieceList,
			List<Integer> toLocationMixPieceList,
			List<ProductLocation> productlocationListUpdate,
			List<Integer> productExceptionLocationIdList) {
		logger.info("physical_warehouse_id="+physical_warehouse_id+";customer_id="+customer_id + "box-->piece");
		logger.info("product_id:{"+product_id+ "},box-->piece");
		// 添加件拣货任务
		int pieceNum = 0;
		String pieceFrom = "";
		String pieceTo = "";
		if (config != null && config.getFrom_piece_location_barcode()!=null &&config.getTo_piece_location_barcode()!=null) {
			pieceFrom = config.getFrom_piece_location_barcode();
			pieceTo = config.getTo_piece_location_barcode();
		}

		// 遍历源库位
		for(int index=0;index<productlocationBoxList.size();index++) {
			ProductLocation pl=productlocationBoxList.get(index);
		//for (ProductLocation pl : productlocationBoxList) {
			if (pl.getProduct_id() - product_id == 0&&pl.getQty_available()>0) {
				ProductLocation plnew = new ProductLocation();
				plnew.setPl_id(pl.getPl_id());

				boolean isFind = false;
				Task task = new Task();
				task.setBatch_pick_id(0);
				task.setBatch_task_id(0);
				task.setPhysical_warehouse_id(physical_warehouse_id);
				task.setCustomer_id(customer_id);
				task.setTask_status("INIT");
				task.setCreated_user("SYSTEM");
				task.setCreated_time(createTime);
				task.setTask_type("REPLENISHMENT");
				task.setTask_level(3);
				task.setMark(Task.PIECE_REPLENISHMENT);
				task.setProduct_id(product_id);

				if (config != null && config.getFrom_piece_location_barcode()!=null &&config.getTo_piece_location_barcode()!=null) {
					for (ProductLocation piecePl : productlocationPieceList) {
						// 目标库位在范围内 且 此库位此sku可以继续创建补货任务
						if ((pieceFrom.compareTo(piecePl.getLocation_barcode()) <= 0)
								&& (pieceTo.compareTo(piecePl
										.getLocation_barcode()) >= 0)
								&& (!productExceptionLocationIdList
										.contains(piecePl.getLocation_id()))) {

							if (pl.getProduct_id() - piecePl.getProduct_id() == 0) {

								if (pl.getValidity().equalsIgnoreCase(
										piecePl.getValidity())&&pl.getBatch_sn().equalsIgnoreCase(piecePl.getBatch_sn())) {
									task.setTo_location_id(piecePl
											.getLocation_id());
									// 找到推荐的库位后 跳出循环
									isFind = true;
									break;
								}

								else if (toLocationMixPieceList
										.contains(piecePl.getLocation_id())) {
									    // 判断允不允许混放

									   task.setTo_location_id(piecePl.getLocation_id());
									   isFind = true;
									   break;
									}
								}

							}

						}

					   if(!isFind)
						{
							// 如果没找到完全一样和可以混放的 找一个可以空库位
							for (Location l : toLocationPieceList) {

								// 如果此库位在目标库位内
								if (pieceFrom.compareToIgnoreCase(l
										.getLocation_barcode()) <= 0
										&& pieceTo.compareToIgnoreCase(l
												.getLocation_barcode()) >= 0 && l.getIs_empty().equalsIgnoreCase("Y")) {
								
									task.setTo_location_id(l.getLocation_id());
									l.setIs_empty("N");
									break;
								}
							}

							

						}
					}


					task.setFrom_pl_id(pl.getPl_id());

					// 当前库位不足 全部取走
					if (pl.getQty_available() + pieceNum < need1) {

						task.setQuantity(pl.getQty_available());
						pieceNum += pl.getQty_available();
						plnew.setQty_available(pl.getQty_available());// 变化量
						pl.setQty_available(0);// 变化量
						
						taskList.add(task);
						productlocationListUpdate.add(plnew);
						
					}
					// 当前库位足够 取需要的
					else {

						task.setQuantity(need1 - pieceNum);
						plnew.setQty_available(need1 - pieceNum);// 变化量
						pl.setQty_available(pl.getQty_available() - need1
								+ pieceNum);
						
						taskList.add(task);
						productlocationListUpdate.add(plnew);
						break;
					}

				}
			}
		}
	

	/**
	 * 
	 * @param goodsIdList
	 *            商品id集合
	 * @param productlocationListMap
	 *            某区域 按商品的productlocation记录 集合
	 * @param availableMap
	 *            某区域每个商品可用数目
	 */
	private void calculateProductAvailable(List<Integer> goodsIdList,
			Map<String, List<ProductLocation>> productlocationListMap,
			Map<String, Integer> availableMap) {

		List<ProductLocation> pllist = new ArrayList<ProductLocation>();
		for (int gid : goodsIdList) {
			pllist = productlocationListMap.get(gid + "");
			int sum = 0;
			for (ProductLocation l : pllist) {
				sum += l.getQty_available();
			}
			availableMap.put(gid + "", sum);
		}

	}

	/**
	 * 
	 * @param productlocationListMap
	 *            某区域 按商品的productlocation记录 集合
	 * @param goodsIdList
	 *            商品id集合
	 * @param productlocationList
	 *            某区域所有的productlocation记录
	 */
	private void cutProductLocationByProductId(
			Map<String, List<ProductLocation>> productlocationListMap,
			List<Integer> goodsIdList, List<ProductLocation> productlocationList) {

		for (Integer pro_id : goodsIdList) {
			List<ProductLocation> productlocationListTemp = new ArrayList<ProductLocation>();
			for (ProductLocation pl : productlocationList) {
				if (pl.getProduct_id() - pro_id == 0) {
					productlocationListTemp.add(pl);
				}
			}

			productlocationListMap.put(pro_id + "", productlocationListTemp);
		}
	}

	/**
	 * 库存分配
	 * @param ordertype 
	 * 
	 * @param productlocationList
	 * @param batch_pick_id
	 * @param num
	 * @param physical_warehouse_id
	 * @param customer_id
	 * @param createTime
	 * @param taskList
	 * @param productlocationListUpdate
	 *            需要更新的库存信息
	 */
	private void doCheckQualityAllot(String ordertype,
			List<ProductLocation> productlocationList, Integer id, int num,
			Integer physical_warehouse_id, Integer customer_id,
			String createTime, List<Task> taskList,
			List<ProductLocation> productlocationListUpdate, int product_id) {
		int allotNum = 0;
		logger.info("physical_warehouse_id="+physical_warehouse_id+";customer_id="+customer_id + "id="+id+"doCheckQualityAllot");
		for(int index=0;index<productlocationList.size();index++) {
			ProductLocation pl=productlocationList.get(index);
		    //for (ProductLocation pl : productlocationList) {
			if (pl.getProduct_id() - product_id == 0&&pl.getQty_available()>0) {
				// 记录每个库存记录的变化量
				ProductLocation plnew = new ProductLocation();
				plnew.setPl_id(pl.getPl_id());

				Task task = new Task();

				// 区分波此单和gt订单任务
				if (ordertype.equalsIgnoreCase("Allot")
						|| ordertype.equalsIgnoreCase("Allot_2B") || ordertype.equalsIgnoreCase("Allot_2X")) {
					task.setBatch_pick_id(id);
				} else {
					task.setOrder_id(id);
				}

				task.setBatch_task_id(0);
				task.setPhysical_warehouse_id(physical_warehouse_id);
				task.setCustomer_id(customer_id);
				task.setTask_status("INIT");
				task.setTask_type("PICK");
				if(ordertype.equalsIgnoreCase("PACK")
						|| ordertype.equalsIgnoreCase("UNPACK")){
					task.setTask_type("PREPACK_PICK");
				}				
				task.setCreated_user("SYSTEM");
				task.setCreated_time(createTime);
				task.setTo_pl_id(0);
				task.setTo_location_id(0);
				task.setFrom_pl_id(pl.getPl_id());
				task.setProduct_id(product_id);
				task.setMark(Task.PIECE_REPLENISHMENT);
				
				if (pl.getQty_available() + allotNum < num) {

					task.setQuantity(pl.getQty_available());
					allotNum += pl.getQty_available();
					plnew.setQty_available(pl.getQty_available());// 记录库存变化量
					pl.setQty_available(0);
					
					
					taskList.add(task);
					productlocationListUpdate.add(plnew);

				} else {

					task.setQuantity(num - allotNum);
					plnew.setQty_available(num - allotNum);// 记录库存变化量
					pl.setQty_available(pl.getQty_available() - num + allotNum);
					
					taskList.add(task);
					productlocationListUpdate.add(plnew);
					break;
				}
				
				

			}

		}

	}
	
	/**
	 * RF移库
	 * @param plId
	 * @param moveNum
	 * @param toLocationBarcode
	 * @param taskType
	 * @return
	 */
	private String moveStock(Integer physicalWarehouseId,Integer customerId,Integer plId,
			Integer moveNum,Integer toLocationId, String taskType) {
		String result = "SUCCESS";
		//taskType ：MoveStock 库存移动，TransferStock 库存转移
        String localUser = (String) SecurityUtils.getSubject().getPrincipal();
		Map<String,Object> fromProductLocation = productLocationDao.selectMapFromId(plId);
		Integer productId = Integer.parseInt(fromProductLocation.get("product_id").toString());
		String status = String.valueOf(fromProductLocation.get("status"));
		String validity = String.valueOf(fromProductLocation.get("validity"));
		Integer fromLocationId = Integer.parseInt(String.valueOf(fromProductLocation.get("location_id")));
		String batchSn = String.valueOf(fromProductLocation.get("batch_sn"));
		Integer warehouseId = Integer.parseInt(fromProductLocation.get("warehouse_id").toString());
		
		//1. create task 
		Task task = new Task();
		task.setPhysical_warehouse_id(physicalWarehouseId);
		task.setFrom_pl_id(plId);
		task.setTo_location_id(toLocationId);
		task.setCustomer_id(customerId);
		task.setTask_type(Task.TASK_TYPE_MOVE);
		task.setTask_status(Task.TASK_STATUS_FULFILLED);
		task.setTask_level(1);
		task.setProduct_id(productId);
		task.setQuantity(moveNum);
		task.setOperate_platform("RF");
		task.setCreated_user(localUser);
		task.setLast_updated_user(localUser);
		SysUser sysuser = sysUserDao.selectByUsername(localUser);
		task.setFirst_bind_user_id(sysuser.getId());
		task.setFirst_bind_time(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		task.setSecond_bind_user_id(sysuser.getId());
		task.setSecond_bind_time(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		taskDao.insert(task);
		Integer taskId = task.getTask_id();
		//2. insert task_action
		
		UserActionTask userActionTask = new UserActionTask();
		userActionTask.setTask_id(taskId);
		userActionTask.setTask_status(Task.TASK_STATUS_FULFILLED);
		userActionTask.setAction_type(Task.TASK_TYPE_MOVE);
		userActionTask.setAction_note("RF移库"+taskType+"数量："+moveNum);
		userActionTask.setCreated_time(new Date());
		userActionTask.setCreated_user(localUser);
		userActionTaskDao.insert(userActionTask);
		
		//3. pl （total = total - num， available = available - num ，last_updated_time = now())
		try{
			productLocationDao.updateProductLocation(-moveNum, plId);
			ProductLocationDetail productLocationDetail = new ProductLocationDetail();
			productLocationDetail.setPl_id(plId);
			productLocationDetail.setChange_quantity(-moveNum);
			productLocationDetail.setTask_id(taskId); // 任务ID
			productLocationDetail.setDescription(taskType);
			productLocationDetail.setCreated_user(localUser);
			productLocationDetail.setLast_updated_user(localUser);
			productLocationDetailDao.insert(productLocationDetail);
		}catch (Exception e) {
			logger.info("源库位可用库存不足，message:"+e.getMessage());
			throw new RuntimeException("源库位可用库存不足！");
		}
		
		//4. insert/update to_pl  && insert pl_detail
		String toPlType = "INSERT";
		Integer toPlId = 0;
		List<Map<String,Object>> toProductLocationList = productLocationDao.selectAllProductLocationByLocationId(toLocationId,"ALL","");
		if(!WorkerUtil.isNullOrEmpty(toProductLocationList)){
			for (Map<String, Object> toProductLocationMap : toProductLocationList) {
				Integer productIdx = Integer.parseInt(toProductLocationMap.get("product_id").toString());
				String statusx = String.valueOf(toProductLocationMap.get("status"));
				String validityx = String.valueOf(toProductLocationMap.get("validity"));
				String batchSnx = String.valueOf(toProductLocationMap.get("batch_sn"));
				Integer warehouseIdx = Integer.parseInt(toProductLocationMap.get("warehouse_id").toString());
				if(productIdx.equals(productId) && warehouseIdx.equals(warehouseId) && validityx.equals(validity) && batchSnx.equalsIgnoreCase(batchSn) && 
						((statusx.equals(status) && "MoveStock".equalsIgnoreCase(taskType))|| 
								(!statusx.equals(status) && "TransferStock".equalsIgnoreCase(taskType))) ){
					toPlType = "UPDATE";
					toPlId = Integer.parseInt(String.valueOf(toProductLocationMap.get("pl_id")));
					break;
				}
			}
		}
		try{
			if(toPlType.equals("INSERT")){
				ProductLocation productLocation = new ProductLocation();
				String validityStatus = "NORMAL";
				Product product = productDao.selectByPrimaryKey(productId);
				if("Y".equals(product.getIs_maintain_warranty())){
					validityStatus = productLocation.checkValidityStatus(validity, product.getValidity(), product.getValidity_unit(), product.getWarranty_warning_days(), product.getWarranty_unsalable_days());
				}
				productLocation.setValidity_status(validityStatus);
				productLocation.setProduct_id(productId);
				productLocation.setLocation_id(toLocationId);
				productLocation.setWarehouse_id(warehouseId);
				productLocation.setQty_total(moveNum);
				productLocation.setQty_available(moveNum);
				productLocation.setProduct_location_status("NORMAL");
				productLocation.setQty_freeze(0);
				if(("TransferStock".equalsIgnoreCase(taskType) && status.equals("NORMAL")) || (!"TransferStock".equalsIgnoreCase(taskType) && !status.equals("NORMAL"))){
					productLocation.setStatus("DEFECTIVE");
				}else{
					productLocation.setStatus("NORMAL");
				}
				productLocation.setValidity(validity);
				productLocation.setSerial_number("");
				productLocation.setCreated_user(localUser);
				productLocation.setLast_updated_user(localUser);
				productLocation.setBatch_sn(batchSn);
				productLocationDao.insert(productLocation);
				toPlId = productLocation.getPl_id();
			}else{
				productLocationDao.updateProductLocation(moveNum, toPlId);
			}
			ProductLocationDetail productLocationDetail = new ProductLocationDetail();
			productLocationDetail.setPl_id(toPlId);
			productLocationDetail.setChange_quantity(moveNum);
			productLocationDetail.setTask_id(taskId); // 任务ID
			productLocationDetail.setDescription(taskType);
			productLocationDetail.setCreated_user(localUser);
			productLocationDetail.setLast_updated_user(localUser);
			productLocationDetailDao.insert(productLocationDetail);
		}catch (Exception e) {
			logger.info("目标库位库存更新失败，message:"+e.getMessage());
			throw new RuntimeException("目标库位库存更新失败，请稍后再试！");
		}
		try{
			//5. update from_lotion.is_empty = 'Y' ? 
			List<Map<String,Object>> fromProductLocationList = productLocationDao.selectAllProductLocationByLocationId(fromLocationId,"","");
			if(WorkerUtil.isNullOrEmpty(fromProductLocationList)){
				locationDao.updateLocationIsEmpty(fromLocationId);
			}
			//6. update to_location.is_empty = 'N'
			locationDao.updateLocationNotEmptyByLocationId(toLocationId);
		}catch (Exception e) {
			logger.info("更新库位使用状态失败，message:"+e.getMessage());
			throw new RuntimeException("更新库位使用状态失败！");
		}
		return result;
	}
	@Override
	public Map<String, Object> printReplenishmentTaskByBatchTaskSn(String batchTaskSn, String username,Integer userId) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		List<Map> printReplenishmentTaskList = new ArrayList<Map>();
		//1. 根据batchTaskSn查所有taskId
		List<Map<String,Object>> taskList = batchTaskDao.selectTaskIdByBatchTaskSn(batchTaskSn);
		try {
			List<Integer> taskIdList = new ArrayList<Integer>();
			List<UserActionTask> updateTaskList = new ArrayList<UserActionTask>();
			if (!WorkerUtil.isNullOrEmpty(taskList)) {
				for(Map<String,Object> taskMap:taskList){
					Integer taskId = Integer.parseInt(String.valueOf(taskMap.get("task_id")));
					UserActionTask userActionTask = new UserActionTask();
					userActionTask.setTask_id(taskId);
					userActionTask.setAction_note("WEB补打印补货任务");
					userActionTask.setAction_type(String.valueOf(taskMap.get("task_status")));
					userActionTask.setTask_status(String.valueOf(taskMap.get("task_status")));
					userActionTask.setCreated_user(username);
					userActionTask.setCreated_time(new Date());
					updateTaskList.add(userActionTask);	
					taskIdList.add(taskId);
					//System.out.println("task_id:"+taskId);
				}
			}
			//System.out.println("size:"+taskIdList.size());
			if (!WorkerUtil.isNullOrEmpty(updateTaskList)) {
				userActionTaskDao.batchInsert(updateTaskList);
				Map<String,Object> paramsMap = new HashMap<String,Object>(); 
//				paramsMap.put("taskStatus", "INIT");
				paramsMap.put("task_status", "IN_PROCESS");
				paramsMap.put("taskIdList", taskIdList);
				paramsMap.put("actionUser", username);
				paramsMap.put("userId", userId);
				paramsMap.put("batchTaskId", 0);
				printReplenishmentTaskList = taskDao.selectTaskByIdList(paramsMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("补打印补货批次失败", e);
			throw new RuntimeException("补打印补货批次失败,失败原因："+e.getMessage());
		}
		
		// 5.返回结果
		returnMap.put("printReplenishmentTaskList", printReplenishmentTaskList);
		returnMap.put("bhCode", batchTaskSn);
		return returnMap;
	}
	@Override
	public List<Map> getReplenishmentTaskByBatchPickSn(
			Map<String, Object> searchMap) {
		return replenishmentDao.getReplenishmentTaskByBatchPickSnByPage(searchMap);
	}
	
	
	
	/**
	 * 双11特殊接口-->允许区域任意分配，忽略生产日期，可拆箱，不生成补货，补货位最少
	 * 
	 * @param ordertype 任务类型   "gt_normal" GT订单（良品）; "gt_defective"GT订单（二手）
	 * @param list波此单号/-Gt订单号
	 * @param physical_warehouse_id物理仓库id
	 * @param customer_id货主id
	 * @param pickOrderGoodsSum 商品总数
	 * @param pickOrderGoodMap 每个波此单商品 集合
	 * @param warehouseId 
	 * @param goodsIdList商品id
	 * @param level紧急补货级别
	 * @param configReplenishmentMap普通补货配置
	 * @param productList商品信息列表
	 */
	public void allotGt11ProductLocation(String ordertype,
			List<Integer> list, Integer physical_warehouse_id,
			Integer customer_id, List<Map> pickOrderGoodsSum,
			Map<String, List<Map>> pickOrderGoodMap, List<Integer> goodsIdList,
			Map<String, ConfigReplenishment> configReplenishmentMap, int level,
			List<Product> productList, Integer warehouseId) {
		
		logger.info("双11 allotGt11ProductLocation：physical_warehouse_id="+physical_warehouse_id+" ;customer_id="+customer_id +" ;type="+ordertype+" ;id:["+list.toString()+"] start");
		
		if(!WorkerUtil.isNullOrEmpty(list)){
			List<OrderProcess> oilist = orderProcessDao.selectOrdersForLock2Task(list);
		}else{
			return ;
		}

		pickOrderGoodsSum = orderInfoDao.
				selectGtOrderGoodsListSum(list);

		// 所有gt订单的商品id
		goodsIdList = new ArrayList<Integer>();
		for (Map m : pickOrderGoodsSum) {
			goodsIdList.add(Integer
					.parseInt(m.get("product_id").toString()));
		}

		productList = productDao
				.selectAllProductListByKeyList(goodsIdList);

		// 普通补货规则
		List<ConfigReplenishment> configReplenishmentList = new ArrayList<ConfigReplenishment>();
		configReplenishmentList = replenishmentDao
				.selectReplenishmentListByProductIdList(customer_id,
						goodsIdList, physical_warehouse_id);
		configReplenishmentMap = new HashMap<String, ConfigReplenishment>();
		for (ConfigReplenishment c : configReplenishmentList) {
			configReplenishmentMap.put(c.getProduct_id() + "", c);
		}

		// 紧急补货规则
		ConfigReplenishmentUrgent configReplenishmentUrgent = replenishmentUrgentDao
				.selectConfigReplenishmentUrgentByPhysicalCustomer(
						customer_id, physical_warehouse_id);
		level = 1;
		if (configReplenishmentUrgent != null) {
			level = configReplenishmentUrgent.getReplenishment_condition();
		}
		// 每个gt订单的商品总数
		pickOrderGoodMap = new HashMap<String, List<Map>>();

		// 2.统计商品总数
		List<Map> pickOrderGoods = orderInfoDao
				.selectGtOrderGoodsList(list);

		

		for (Integer order_id : list) {
			// 用来存每种商品
			List<Map> pickOrderGood = new ArrayList<Map>();
			for (Map m : pickOrderGoods) {
				if (Integer.parseInt(m.get("order_id").toString())
						- order_id == 0) {
					pickOrderGood.add(m);
				}
			}

			pickOrderGoodMap.put("" + order_id, pickOrderGood);
		}
	
		
		
		// 库存信息
		List<ProductLocation> productlocationList = new ArrayList<ProductLocation>();// 可拣货库存

		//被冻结的库位+商品
		List<ProductLocation> productlocationExceptionList = productLocationDao.selectExceptionList(goodsIdList);
		if(com.leqee.wms.api.util.WorkerUtil.isNullOrEmpty(productlocationExceptionList)){
			productlocationExceptionList=new ArrayList<ProductLocation>();
		}
		
		//记录  product_id1=location_id1|location_id2  product_id2=location_id1|location_id5|location_id6 
		Map<String,List<Integer>> productlocationExceptionMap=new HashMap<String,List<Integer>>();
		
		for(ProductLocation p:productlocationExceptionList){
			List<Integer> listTemp =new ArrayList<Integer>();
			if(productlocationExceptionMap.get(p.getProduct_id() + "") != null){
				listTemp=productlocationExceptionMap.get(p.getProduct_id() + "");
			}
			listTemp.add(p.getLocation_id());
			productlocationExceptionMap.put(p.getProduct_id()+"", listTemp);
		}
		
		if(ordertype.equalsIgnoreCase("gt_normal11")){

			//退回区要包含耗材区
			productlocationList = productLocationDao
					.selectProductlocationListV9(physical_warehouse_id,warehouseId,
							goodsIdList, "('RETURN_LOCATION','PIECE_PICK_LOCATION','BOX_PICK_LOCATION','STOCK_LOCATION','PACKBOX_LOCATION')","NORMAL");
		}
		//-gt  二手  所有的商品都在二手区
		else{			
			productlocationList = productLocationDao
					.selectProductlocationListV6(physical_warehouse_id,warehouseId,
							goodsIdList, Location.LOCATION_TYPE_DEFECTIVE,"DEFECTIVE");
		}

		// 保存每种商品的id
		Map<String, Integer> productBoxPickStartNumber = new HashMap<String, Integer>();
		for (Product p : productList) {
			productBoxPickStartNumber.put("" + p.getProduct_id(),
					p.getBox_pick_start_number());
		}

		// 每个商品的库存信息
		Map<String, List<ProductLocation>> productlocationListMap = new HashMap<String, List<ProductLocation>>();
		
		// 求出每种商品的拣货区可用数量
		Map<String, Integer> availableMap = new HashMap<String, Integer>();

		// 将每个库区
		cutProductLocationByProductId(productlocationListMap, goodsIdList,
				productlocationList);
		
		// 计算每个库区每个商品的可用数量
		calculateProductAvailable(goodsIdList, productlocationListMap,
				availableMap);

		int warehouse_id = 0;
		java.util.Date now = new java.util.Date();
		String createTime = DateUtils.getStringTime(now);

		List<Map> batchpickOrderGood = new ArrayList<Map>();
		// 记录任务
		List<Task> taskList = new ArrayList<Task>();
		// 记录库存信息
		List<ProductLocation> productlocationListUpdate = new ArrayList<ProductLocation>();
		
		//记录-gt订单操作日志
		List<UserActionOrder> userActionOrderList = new ArrayList<UserActionOrder>();
		
		List<Integer> gtYList=new ArrayList<Integer>();
		List<Integer> gtEList=new ArrayList<Integer>();
		// 逐个波此单/-Gt订单处理
		for (Integer id : list) {

			logger.info("双11 allotGt11ProductLocation：id="+id+";customer_id="+customer_id +"start1");
			
			// 将库存最多库位放在前面
			Collections.sort(productlocationList);

			//  Gt订单    的信息的商品信息
			batchpickOrderGood = pickOrderGoodMap.get(id + "");
			boolean isAvailable = true;
			boolean isSpec=false;
			logger.info("双11 allotGt11ProductLocation："+id+"-"+batchpickOrderGood.size());
			for (Map m : batchpickOrderGood) {

				logger.info("id="+id+";customer_id="+customer_id +"product_id="+m.get("product_id"));
				int product_id = m.get("product_id") == null ? 0 : Integer
						.parseInt(m.get("product_id").toString());
				logger.info("id="+id+";customer_id="+customer_id +"product_id="+product_id);
				int goods_numbers = m.get("goods_number") == null ? 0
						: Integer.parseInt(m.get("goods_number").toString());
				
				logger.info("spec"+m.get("spec"));
				int spec = m.get("spec") == null ? -1 : Integer.parseInt(m.get(
						"spec").toString());
				
				if(spec<1){
					logger.info("spec"+spec);
					isSpec=true;
					break;
				}
				int available = availableMap.get(product_id + "") == null ? 0
						: Integer.parseInt(availableMap.get(
								product_id + "").toString());
				
				// 退货区够用
				if (goods_numbers <= available) {
					logger.info("双11 allotGt11ProductLocation："+product_id+"可拣货区够用");
					continue;
				}
				// 退货区不够
				else {
					
					isAvailable = false;
					logger.info("双11 allotGt11ProductLocation："+product_id+"不能进行分配");
					break;
				
				}

			}

			if(isSpec){
				continue;
			}
			
			// 商品足够，进行库存分配
			if (isAvailable) {

				for (Map m : batchpickOrderGood) {

					logger.info("id="+id+";customer_id="+customer_id +"Allot true");
					String goods_name = m.get("goods_name") == null ? "" : m
							.get("goods_name").toString();
					int product_id = m.get("product_id") == null ? 0 : Integer
							.parseInt(m.get("product_id").toString());
					
					String barcode = m.get("barcode") == null ? "" : m.get(
							"barcode").toString();
					int spec = m.get("spec") == null ? 1 : Integer.parseInt(m
							.get("spec").toString());
					int goods_numbers = m.get("goods_number") == null ? 0
							: Integer.parseInt(m.get("goods_number")
									.toString());
					int available = availableMap.get(product_id + "") == null ? 0
							: Integer.parseInt(availableMap.get(
									product_id + "").toString());
					
					availableMap.put(product_id + "", available
							- goods_numbers);
					
					List<ProductLocation> productlocationListTemp = productlocationListMap.get(product_id+"");
					
					ProductLocationNumValidityComparator plComparator=new ProductLocationNumValidityComparator();
					
					Collections.sort(productlocationListTemp,plComparator);
					
					doCheckQuality11Allot(ordertype,productlocationListTemp,
							id, goods_numbers,
							physical_warehouse_id, customer_id, createTime,
							taskList, productlocationListUpdate,product_id);
					
				
            	gtYList.add(id);
            	
            	UserActionOrder userActionOrder = new UserActionOrder();
        		userActionOrder.setAction_note("供应商退货订单"+id+"分配库存成功");
        		userActionOrder.setAction_type("RESERVED");
        		userActionOrder.setCreated_time(new Date());
        		userActionOrder.setCreated_user("system");
        		userActionOrder.setOrder_goods_id(null);
        		userActionOrder.setOrder_id(id);
        		userActionOrder.setOrder_status("RESERVED");
        		userActionOrderList.add(userActionOrder);
               
				}

			}
			// 记录分配失败的波此单或者-gt订单
			else {
				
				
				gtEList.add(id);
				UserActionOrder userActionOrder = new UserActionOrder();
        		userActionOrder.setAction_note("供应商退货订单"+id+"分配库存失败");
        		userActionOrder.setAction_type("RESERVED");
        		userActionOrder.setCreated_time(new Date());
        		userActionOrder.setCreated_user("system");
        		userActionOrder.setOrder_goods_id(null);
        		userActionOrder.setOrder_id(id);
        		userActionOrder.setOrder_status("RESERVED");
        		userActionOrderList.add(userActionOrder);
				

			}

		}
		
		//需要记录的任务日志
		List<UserActionTask> userActionTaskList=new ArrayList<UserActionTask>();

		if (!WorkerUtil.isNullOrEmpty(taskList)) {
			
			for(Task task:taskList){
				taskDao.insertTask(task);
				
				UserActionTask userActionTask = new UserActionTask();
				userActionTask.setTask_id(task.getTask_id());
				userActionTask.setAction_note("创建");
				userActionTask.setAction_type(Task.TASK_STATUS_INIT);
				userActionTask.setTask_status("INIT");
				userActionTask.setCreated_user("SYSTEM");
				userActionTask.setCreated_time(new Date());
				userActionTaskList.add(userActionTask);	
			}
			
			//taskDao.insertTaskList(taskList);
		}
		
		if (!WorkerUtil.isNullOrEmpty(userActionTaskList)) {
			userActionTaskDao.insertList(userActionTaskList);
		}
		// 更新库存信息
		if (!WorkerUtil.isNullOrEmpty(productlocationListUpdate)) {
			//归并其中一部分主键相同的更新记录（使用 qty_available - ${item2.qty_available}）所以分配里面记录的变化量应该是差异值，且未正数
			Map<Integer,ProductLocation> updatePlMap =new HashMap<Integer,ProductLocation>();
			List<ProductLocation> updatePlList=new ArrayList<ProductLocation>();
			for(ProductLocation pl:productlocationListUpdate){
				//如果没有则加到记录中
				if(updatePlMap.get(pl.getPl_id())==null){
					updatePlMap.put(pl.getPl_id(), pl);
					updatePlList.add(pl);
				}else{
					ProductLocation pl2=updatePlMap.get(pl.getPl_id());
					pl2.setQty_available(pl2.getQty_available()+pl.getQty_available());
				}
			}
			
			productLocationDao
				.updateProductLocationByAllotJob(updatePlList);
		}
		
		if (!WorkerUtil.isNullOrEmpty(gtYList)) {
		logger.info("双11 allotGt11ProductLocation：physical_warehouse_id="+physical_warehouse_id+" ;customer_id="+customer_id +" ;type="+ordertype+" ;gtYList size:["+gtYList.size()+"] end");
		orderInfoDao.updateReserveStatus(gtYList, "RESERVED");
		orderProcessDao.updateStatusToReserved(gtYList, "RESERVED");
		}
		
		if(!WorkerUtil.isNullOrEmpty(userActionOrderList)){
			logger.info("双11 allotGt11ProductLocation：   physical_warehouse_id="+physical_warehouse_id+" ;customer_id="+customer_id +" ;type="+ordertype+" ;userActionOrderList size:["+userActionOrderList.size()+"] end");
			userActionOrderDao.batchInsert(userActionOrderList);
		}
		logger.info("双11 allotGt11ProductLocation： physical_warehouse_id="+physical_warehouse_id+" ;customer_id="+customer_id +" ;type="+ordertype+" ;id:["+list.toString()+"] end");
	}
	
	/**
	 * 双11特殊接口-->允许区域任意分配，忽略生产日期，可拆箱，不生成补货，补货位最少
	 * @param ordertype
	 * @param productlocationListTemp
	 * @param id
	 * @param goods_numbers
	 * @param physical_warehouse_id
	 * @param customer_id
	 * @param createTime
	 * @param taskList
	 * @param productlocationListUpdate
	 * @param product_id
	 */
	private void doCheckQuality11Allot(String ordertype,
			List<ProductLocation> productlocationList, Integer id,
			int goods_numbers, Integer physical_warehouse_id,
			Integer customer_id, String createTime, List<Task> taskList,
			List<ProductLocation> productlocationListUpdate, int product_id) {
		
		//需要拣的库存条数总数
		int count=0;
		int sum=0;
		int need=0;
        for(int i=0;i<productlocationList.size();i++){
        	int temp=productlocationList.get(i).getQty_available();
        	count++;
        	if(sum+temp>=goods_numbers){
        		need=goods_numbers-sum;
        		break;
        	}   
        	sum+=temp;
        }
        
        //当需要的库存记录数为n  则前n-1按照  从大到小拣
        if(count>1){
        	for(int i=0;i<count-1;i++){

    			ProductLocation pl=productlocationList.get(i);
    		    //for (ProductLocation pl : productlocationList) {
    			if (pl.getProduct_id() - product_id == 0&&pl.getQty_available()>0) {
    				// 记录每个库存记录的变化量
    				ProductLocation plnew = new ProductLocation();
    				plnew.setPl_id(pl.getPl_id());

    				Task task = new Task();

    				// 区分波此单和gt订单任务
    				if (ordertype.equalsIgnoreCase("Allot11")
    						|| ordertype.equalsIgnoreCase("Allot_2B11") || ordertype.equalsIgnoreCase("Allot_2X11")) {
    					task.setBatch_pick_id(id);
    				} else {
    					task.setOrder_id(id);
    				}

    				task.setBatch_task_id(0);
    				task.setPhysical_warehouse_id(physical_warehouse_id);
    				task.setCustomer_id(customer_id);
    				task.setTask_status("INIT");
    				task.setTask_type("PICK");				
    				task.setCreated_user("SYSTEM");
    				task.setCreated_time(createTime);
    				task.setTo_pl_id(0);
    				task.setTo_location_id(0);
    				task.setFrom_pl_id(pl.getPl_id());
    				task.setProduct_id(product_id);
    				task.setMark(Task.PIECE_REPLENISHMENT);
    				task.setQuantity(pl.getQty_available());
    				
    				plnew.setQty_available(pl.getQty_available());// 记录库存变化量
    				pl.setQty_available(0);
    				
    				
    				taskList.add(task);
    				productlocationListUpdate.add(plnew);

    			}
            }
        }
        

        //当需要的库存记录数为n  则最后一条  按照  从小到大比较  遇到刚好够用的进行执行
        for(int i=productlocationList.size()-1;i>=0;i--){

			ProductLocation pl=productlocationList.get(i);
			if (pl.getProduct_id() - product_id == 0&&pl.getQty_available()>=need) {
				// 记录每个库存记录的变化量
				ProductLocation plnew = new ProductLocation();
				plnew.setPl_id(pl.getPl_id());

				Task task = new Task();

				// 区分波此单和gt订单任务
				if (ordertype.equalsIgnoreCase("Allot11")
						|| ordertype.equalsIgnoreCase("Allot_2B11") || ordertype.equalsIgnoreCase("Allot_2X11")) {
					task.setBatch_pick_id(id);
				} else {
					task.setOrder_id(id);
				}

				task.setBatch_task_id(0);
				task.setPhysical_warehouse_id(physical_warehouse_id);
				task.setCustomer_id(customer_id);
				task.setTask_status("INIT");
				task.setTask_type("PICK");				
				task.setCreated_user("SYSTEM");
				task.setCreated_time(createTime);
				task.setTo_pl_id(0);
				task.setTo_location_id(0);
				task.setFrom_pl_id(pl.getPl_id());
				task.setProduct_id(product_id);
				task.setMark(Task.PIECE_REPLENISHMENT);
				task.setQuantity(need);
				
				plnew.setQty_available(need);// 记录库存变化量

				pl.setQty_available(pl.getQty_available() - need);
				
				taskList.add(task);
				productlocationListUpdate.add(plnew);
				break;
			}
        }

	}
	
	
	/**
	 * 双11特殊接口-->允许区域任意分配，忽略生产日期，可拆箱，不生成补货，补货位最少
	 * 
	 * @param ordertype 任务类型   "Allot":普通波此单；  "Allot_2B"：toB波此单
	 * @param list波此单号/-Gt订单号
	 * @param physical_warehouse_id物理仓库id
	 * @param customer_id货主id
	 * @param pickOrderGoodsSum 商品总数
	 * @param pickOrderGoodMap 每个波此单商品 集合
	 * @param warehouse_id 
	 * @param goodsIdList商品id
	 * @param level紧急补货级别
	 * @param configReplenishmentMap普通补货配置
	 * @param productList商品信息列表
	 */
	public void allot11ProductLocation(String ordertype,
			List<Integer> list, Integer physical_warehouse_id,
			Integer customer_id, List<Map> pickOrderGoodsSum,
			Map<String, List<Map>> pickOrderGoodMap, List<Integer> goodsIdList,
			Map<String, ConfigReplenishment> configReplenishmentMap, int level,
			List<Product> productList, Integer warehouseId) {
		logger.info("physical_warehouse_id="+physical_warehouse_id+" ;customer_id="+customer_id +" ;type="+ordertype+" ;id:["+list.toString()+"] start");
		
		//-C   -B  波此单
		if(ordertype.equalsIgnoreCase("Allot")||ordertype.equalsIgnoreCase("Allot_2B")){
			if(!WorkerUtil.isNullOrEmpty(list)){
				List<BatchPick> bplist = batchPickDao.selectAllBatchPickListByIdListForUpdate(list);
			}else{
				return ;
			}
		}
		
		if(ordertype.equalsIgnoreCase("Allot11")||ordertype.equalsIgnoreCase("Allot_2B11")){
				// 所有波此单的商品信息
				pickOrderGoodsSum = batchPickBizImpl
							.selectPickOrderGoodsListSum(list);
				
				// 所有波此单的商品id
				goodsIdList = new ArrayList<Integer>();
				for (Map m : pickOrderGoodsSum) {
							goodsIdList.add(Integer
									.parseInt(m.get("product_id").toString()));
				}
			
				productList = productDao
						.selectAllProductListByKeyList(goodsIdList);
	
				// 普通补货规则
				List<ConfigReplenishment> configReplenishmentList = new ArrayList<ConfigReplenishment>();
				configReplenishmentList = replenishmentDao
						.selectReplenishmentListByProductIdList(
								customer_id, goodsIdList,
								physical_warehouse_id);
				configReplenishmentMap = new HashMap<String, ConfigReplenishment>();
				for (ConfigReplenishment c : configReplenishmentList) {
					configReplenishmentMap.put(c.getProduct_id() + "", c);
				}
	
				// 紧急补货规则
				ConfigReplenishmentUrgent configReplenishmentUrgent = replenishmentUrgentDao
						.selectConfigReplenishmentUrgentByPhysicalCustomer(
								customer_id, physical_warehouse_id);
				level = 1;
				if (configReplenishmentUrgent != null) {
					level = configReplenishmentUrgent.getReplenishment_condition();
				}
				// 每个波此单的商品总数
				pickOrderGoodMap = new HashMap<String, List<Map>>();
	
				// 2.统计商品总数
				List<Map> pickOrderGoods = batchPickBizImpl
						.selectPickOrderGoodsList(list);
	
				// 用来存每种商品
	
	
				for (Integer batchPickId : list) {
					List<Map> pickOrderGood = new ArrayList<Map>();
					for (Map m : pickOrderGoods) {
						if (Integer.parseInt(m.get("batch_pick_id").toString())
								- batchPickId == 0) {
							pickOrderGood.add(m);
						}
					}
	
					pickOrderGoodMap.put("" + batchPickId, pickOrderGood);
				}
		}
		
		// 库存信息
		List<ProductLocation> productlocationList = new ArrayList<ProductLocation>();// 件拣货库存
		
		productlocationList = productLocationDao
				.selectProductlocationListV3(physical_warehouse_id,warehouseId,
						goodsIdList, "('RETURN_LOCATION','PIECE_PICK_LOCATION','BOX_PICK_LOCATION','STOCK_LOCATION')","NORMAL");
		
		// 保存每种商品的id
		Map<String, Integer> productBoxPickStartNumber = new HashMap<String, Integer>();
		for (Product p : productList) {
			productBoxPickStartNumber.put("" + p.getProduct_id(),
					p.getBox_pick_start_number());
		}

		// 每个商品的库存信息
		Map<String, List<ProductLocation>> productlocationListMap = new HashMap<String, List<ProductLocation>>();


		// 求出每种商品的件拣货区可用数量
		Map<String, Integer> availableMap = new HashMap<String, Integer>();

		
		
		// 求出每种商品的件拣货区可用数量
		Map<String, Integer> availableMap1 = new HashMap<String, Integer>();


		// 将每个库区
		cutProductLocationByProductId(productlocationListMap, goodsIdList,
				productlocationList);
		
		// 计算每个库区每个商品的可用数量
		calculateProductAvailable(goodsIdList, productlocationListMap,
				availableMap);

		java.util.Date now = new java.util.Date();
		String createTime = DateUtils.getStringTime(now);

		List<Map> batchpickOrderGood = new ArrayList<Map>();
		// 记录任务
		List<Task> taskList = new ArrayList<Task>();
		// 记录库存信息
		List<ProductLocation> productlocationListUpdate = new ArrayList<ProductLocation>();
		
		//记录波此单操作日志
		List<UserActionBatchPick> userActionBatchPickList = new ArrayList<UserActionBatchPick>();
		
		//记录-gt订单操作日志
		List<UserActionOrder> userActionOrderList = new ArrayList<UserActionOrder>();


		List<Integer> batchPickYList = new ArrayList<Integer>();
		List<Integer> batchPickEList = new ArrayList<Integer>();
		// 逐个波此单
		for (Integer id : list) {

			logger.info("id="+id+";customer_id="+customer_id +"start1");
			// 将库存最多库位放在前面
			Collections.sort(productlocationList);

			//  波此单||Gt订单    的信息的商品信息
			batchpickOrderGood = pickOrderGoodMap.get(id + "");
			boolean isAvailable = true;
			boolean isSpec=false;
			logger.info("dlyao-"+id+"-"+batchpickOrderGood.size());
			for (Map m : batchpickOrderGood) {

				logger.info("id="+id+";customer_id="+customer_id +"product_id="+m.get("product_id"));
				int product_id = m.get("product_id") == null ? 0 : Integer
						.parseInt(m.get("product_id").toString());
				logger.info("id="+id+";customer_id="+customer_id +"product_id="+product_id);
				int goods_numbers = m.get("goods_number") == null ? 0
						: Integer.parseInt(m.get("goods_number").toString());
				
				logger.info("spec"+m.get("spec"));
				int spec = m.get("spec") == null ? -1 : Integer.parseInt(m.get(
						"spec").toString());
				
				if(spec<1){
					logger.info("spec"+spec);
					isSpec=true;
					break;
				}
				int available = availableMap.get(product_id + "") == null ? 0
						: Integer.parseInt(availableMap.get(
								product_id + "").toString());
			
				// 够用
				if (goods_numbers <= available) {
					logger.info("退货区够用");
				}
				// 不够
				else {
					isAvailable = false;
					logger.info("不能进行分配");
					break;
				}

			}

			if(isSpec){
				continue;
			}
			
			// 商品足够，进行库存分配
			if (isAvailable) {

				for (Map m : batchpickOrderGood) {

					logger.info("id="+id+";customer_id="+customer_id +"Allot true");
					String goods_name = m.get("goods_name") == null ? "" : m
							.get("goods_name").toString();
					int product_id = m.get("product_id") == null ? 0 : Integer
							.parseInt(m.get("product_id").toString());
					
					String barcode = m.get("barcode") == null ? "" : m.get(
							"barcode").toString();
					int spec = m.get("spec") == null ? 1 : Integer.parseInt(m
							.get("spec").toString());
					int goods_numbers = m.get("goods_number") == null ? 0
							: Integer.parseInt(m.get("goods_number")
									.toString());
					int available = availableMap.get(product_id + "") == null ? 0
							: Integer.parseInt(availableMap.get(
									product_id + "").toString());

					// 可拣货区够用
					if (goods_numbers <= available) {

						availableMap.put(product_id + "", available
								- goods_numbers);
						
						List<ProductLocation> productlocationListTemp = productlocationListMap.get(product_id+"");
						
						ProductLocationNumValidityComparator plComparator=new ProductLocationNumValidityComparator();
						
						Collections.sort(productlocationListTemp,plComparator);
						
						doCheckQuality11Allot(ordertype,productlocationListTemp,
								id, goods_numbers,
								physical_warehouse_id, customer_id, createTime,
								taskList, productlocationListUpdate,product_id);
					}
					

				}
				
                	batchPickYList.add(id);
                	UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
    				userActionBatchPick.setAction_note("波次单:" + id+"分配库存成功");
    				userActionBatchPick.setAction_type("ALLOTINVENTORY");
    				userActionBatchPick.setCreated_time(new Date());
    				userActionBatchPick.setCreated_user("System");
    				userActionBatchPick.setStatus("INIT");
    				userActionBatchPick.setBatch_pick_id(id);
    				userActionBatchPickList.add(userActionBatchPick);
                
				

			}
			// 记录分配失败的波此单或者-gt订单
			else {
				
                	batchPickEList.add(id);
                	
                	UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
    				userActionBatchPick.setAction_note("波次单:" + id+"分配库存失败");
    				userActionBatchPick.setAction_type("ALLOTINVENTORY");
    				userActionBatchPick.setCreated_time(new Date());
    				userActionBatchPick.setCreated_user("System");
    				userActionBatchPick.setStatus("INIT");
    				userActionBatchPick.setBatch_pick_id(id);
    				userActionBatchPickList.add(userActionBatchPick);
                
			}

		}

		
		//需要记录的任务日志
		List<UserActionTask> userActionTaskList=new ArrayList<UserActionTask>();


		if (!WorkerUtil.isNullOrEmpty(taskList)) {
			
			for(Task task:taskList){
				taskDao.insertTask(task);
				
				UserActionTask userActionTask = new UserActionTask();
				userActionTask.setTask_id(task.getTask_id());
				userActionTask.setAction_note("创建");
				userActionTask.setAction_type(Task.TASK_STATUS_INIT);
				userActionTask.setTask_status("INIT");
				userActionTask.setCreated_user("SYSTEM");
				userActionTask.setCreated_time(new Date());
				userActionTaskList.add(userActionTask);	
			}
			
			//taskDao.insertTaskList(taskList);
		}
		
		if (!WorkerUtil.isNullOrEmpty(userActionTaskList)) {
			userActionTaskDao.insertList(userActionTaskList);
		}
		// 更新库存信息
		if (!WorkerUtil.isNullOrEmpty(productlocationListUpdate)) {
			//归并其中一部分主键相同的更新记录（使用 qty_available - ${item2.qty_available}）所以分配里面记录的变化量应该是差异值，且未正数
			Map<Integer,ProductLocation> updatePlMap =new HashMap<Integer,ProductLocation>();
			List<ProductLocation> updatePlList=new ArrayList<ProductLocation>();
			for(ProductLocation pl:productlocationListUpdate){
				//如果没有则加到记录中
				if(updatePlMap.get(pl.getPl_id())==null){
					updatePlMap.put(pl.getPl_id(), pl);
					updatePlList.add(pl);
				}else{
					ProductLocation pl2=updatePlMap.get(pl.getPl_id());
					pl2.setQty_available(pl2.getQty_available()+pl.getQty_available());
				}
			}
			
			productLocationDao
				.updateProductLocationByAllotJob(updatePlList);
		}
		// 更新波次单状态
		if (!WorkerUtil.isNullOrEmpty(batchPickEList)) {
			batchPickDao.updateReserveStatus(batchPickEList, "E");
		}
		if (!WorkerUtil.isNullOrEmpty(batchPickYList)) {
			batchPickDao.updateReserveStatus(batchPickYList, "Y");
		}
		
		if(!WorkerUtil.isNullOrEmpty(userActionBatchPickList)){
			logger.info("physical_warehouse_id="+physical_warehouse_id+" ;customer_id="+customer_id +" ;type="+ordertype+" ;userActionBatchPickList size:["+userActionBatchPickList.size()+"] end");
			userActionBatchPickDao.batchInsert(userActionBatchPickList);
		}
		logger.info("physical_warehouse_id="+physical_warehouse_id+" ;customer_id="+customer_id +" ;type="+ordertype+" ;id:["+list.toString()+"] end");
	}
	
}
