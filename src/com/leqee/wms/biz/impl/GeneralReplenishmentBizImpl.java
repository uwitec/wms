package com.leqee.wms.biz.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.biz.GeneralReplenishmentBiz;
import com.leqee.wms.dao.ConfigReplenishmentDao;
import com.leqee.wms.dao.ProductDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.ReplenishmentDao;
import com.leqee.wms.dao.TaskDao;
import com.leqee.wms.dao.UserActionTaskDao;
import com.leqee.wms.entity.ConfigReplenishment;
import com.leqee.wms.entity.Location;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.entity.Task;
import com.leqee.wms.entity.User;
import com.leqee.wms.entity.UserActionTask;
import com.leqee.wms.response.Response;
import com.leqee.wms.util.WorkerUtil;

@Service
public class GeneralReplenishmentBizImpl implements GeneralReplenishmentBiz {

	private Logger logger = Logger.getLogger(GeneralReplenishmentBizImpl.class);
	
	@Autowired
	ReplenishmentDao replenishmentDao;
	@Autowired
	TaskDao taskDao;
	@Autowired
	ProductDao productDao;
	@Autowired
	ProductLocationDao productLocationDao;
	@Autowired
	UserActionTaskDao userActionTaskDao;
	@Autowired
	ConfigReplenishmentDao configReplenishmentDao;
	
	/**
	 * 根据 物理仓，货主，箱拣/件拣，指定商品 等创建 一般补货任务
	 * 相关规约：
	 * 1. 补货任务列表中的任务状态“已结束”意义：此任务已经执行过一次
	 * 2. 补货任务生成时需要过滤登记异常的库位库存记录
	 * 3. 设置目标库区为件拣货区，如果触发生成的任务为箱拣货区任务，关联的任务数量需要+1
	 * 4. 如果箱拣货区已经低于最低存量，但存储区数量不能满足补到最高存量所需数量，则将所有可补数量生成补货任务
	 * 5. 补货数量 以“箱”向上取整
	 * 6. 补货规则中 合并取消 ，只考虑同级合并
	 * 7. 计算现有库区数量时，“在途（同级）补货任务数量”作为其中一部分（在途：未开始，已分配，进行中，已下架）； 已分配数量不计算在内 ==>available_qty + sum(同级replenish_num)
	 * 8. 一般补货任务生成过程中，如果箱拣货位库存不满足件拣货所需数量&箱拣货区未维护最高存量 ，生成箱拣货补货任务，补货数量取件拣货区最高存量
	 * 9. 若补货库区配置范围非空，目标库位 优先级：
	 * 		9.1 商品与批次完全一致，有相同SKU库存
	 * 		9.2 当前有SKU库存 ，允许混放批次的库位 （不考虑当前库位是否允许混放商品，只要此库位目前有此sku；不考虑此商品属性是否为批次维护）
	 * 		9.3 空库位
	 * 		9.4 溢出库位 0 
	 * 10. 若补货库区配置范围为空 或 未配置，目标库位为0
	 * by ytchen
	 */
	@Override
	public String generalReplenishTask(
			Integer physicalWarehouseId, Integer customerId,
			String boxPiece, int productId) {
		String loggerBefore = "physicalWarehouseId_"+physicalWarehouseId+",customerId_"+customerId+",boxPiece_"+boxPiece;
		logger.info("generalReplenishTask start ...."+loggerBefore+",productId: "+productId);
//		List<Map> replenishmentProductList = new ArrayList<Map>();
		String taskIdStrAdd = ""; // 如果queueId!=0，且生成目标库位在件拣货区的任务，需要更新taskId到任务列表
		if(boxPiece.equalsIgnoreCase("PIECE") || boxPiece.equalsIgnoreCase("BOX_PIECE")){
			//查询 物理仓货主确定下， available_qty+在途一般补货数量 < 最低存量 商品
			//1. 获取配置规则中最低水位>0 & 最高水位>0 
			List<ConfigReplenishment> configReplenishmentList = configReplenishmentDao.selectConfigByJob(physicalWarehouseId,customerId,productId,"PIECE");
			if(configReplenishmentList.size()>0){
//				logger.info("需要生成件拣货区补货任务&配置过最低存量&spec维护的商品数量为："+configReplenishmentList.size());
				for (ConfigReplenishment configReplenishment : configReplenishmentList) {
					//2. 获取 总可用库存
					Integer availableQty = replenishmentDao.getAvailableQtyByLocationType(physicalWarehouseId,customerId,configReplenishment.getProduct_id(),Location.LOCATION_TYPE_PIECE_PICK);
					//3. 获取在途数量+在途任务
					Integer replenishQty = replenishmentDao.getReplenishQtyByProductLocationType(physicalWarehouseId,"General",configReplenishment.getProduct_id(),Location.LOCATION_TYPE_BOX_PICK);
					if(availableQty+replenishQty<configReplenishment.getPiece_location_min_quantity()){
//						logger.info("可用库存+在途库存<最低存量！");
						String taskIdStr = this.generalPieceReplenishTask(configReplenishment.getProduct_id(),configReplenishment.getPiece_location_max_quantity(),
								availableQty,replenishQty,physicalWarehouseId,customerId);
						if(taskIdStr!="")taskIdStrAdd = taskIdStrAdd+taskIdStr;	
					}else{
//						logger.info(loggerBefore+" productId:"+configReplenishment.getProduct_id()+" 件拣货区库存足够！");
						continue;
					}
				}
			}else{
				logger.info(loggerBefore+"不存在一般补货规则设置商品");
			}
		}
		if(boxPiece.equalsIgnoreCase("BOX") || boxPiece.equalsIgnoreCase("BOX_PIECE")){
			//查询 物理仓货主确定下， available_qty+在途一般补货数量 < 最低存量 商品
			//1. 获取配置规则中最低水位>0 & 最高水位>0 
			List<ConfigReplenishment> configReplenishmentList = configReplenishmentDao.selectConfigByJob(physicalWarehouseId,customerId,productId,"BOX");
			if(configReplenishmentList.size()>0){
				for (ConfigReplenishment configReplenishment : configReplenishmentList) {
					//2. 获取 总可用库存
					Integer availableQty = replenishmentDao.getAvailableQtyByLocationType(physicalWarehouseId,customerId,configReplenishment.getProduct_id(),Location.LOCATION_TYPE_BOX_PICK);
					//3. 获取在途数量+在途任务
					Integer replenishQty = replenishmentDao.getReplenishQtyByProductLocationType(physicalWarehouseId,"General",configReplenishment.getProduct_id(),Location.LOCATION_TYPE_STOCK);
					if(availableQty+replenishQty<configReplenishment.getBox_location_min_quantity()){
						String taskIdStr = this.generalBoxReplenishTask(configReplenishment.getProduct_id(),configReplenishment.getBox_location_max_quantity(),
								availableQty,replenishQty,physicalWarehouseId,customerId);
						if(taskIdStr!="")taskIdStrAdd = taskIdStrAdd+taskIdStr;	
					}else{
//						logger.info(loggerBefore+" productId:"+configReplenishment.getProduct_id()+" 箱拣货区库存足够！");
						continue;
					}
				}
			}else{
				logger.info(loggerBefore+"不存在一般补货规则设置商品");
			}
			
		}
		return taskIdStrAdd;
		
	}

	/**
	 * 具体一般箱拣待补货数量计算
	 * by ytchen
	 */
	private String generalBoxReplenishTask(Integer productIdReplenish,Integer boxLocationMaxQuantity,
			Integer sumBoxAvailable,Integer sumBoxReplenish, Integer physicalWarehouseId,
			Integer customerId) {
		String taskIdStr = "";
		Map<String,Object> initBoxTaskMap = replenishmentDao.getInitReplenishTask(Location.LOCATION_TYPE_STOCK,productIdReplenish,physicalWarehouseId,customerId);
		Integer initBoxReplenish = Integer.parseInt(String.valueOf(initBoxTaskMap.get("init_replenish")));// 箱拣货库区在途中状态为“未开始”的任务商品数量 (BU)
		
		Integer sumStockAvailable =replenishmentDao.getAvailableQtyByLocationType(physicalWarehouseId,customerId,productIdReplenish,Location.LOCATION_TYPE_STOCK);//存储区总available  (SA)
		/**
		 * 箱拣货区新增补货数量(BW) = MAX-BA-BL 
		 * 1. SA=0 ：现有箱拣货区可用数量为空， 不处理  ==> 此情况意味INIT，未生成task
		 * 2. SA<=BW : 现有存储区可用库存<箱拣货新增补货数量，则取消未开始的补货任务，生成新的补货任务，新补货任务数量=MAX-SA+BU  ==> 此情况意味件拣货任务成功
		 * 3. SA>BW : 现有存储区可用库存>箱拣货新增补货数量，则取消未开始的补货任务，生成新的补货任务，新补货任务数量=MAX-BA-BL+BU  ==> 此情况意味件拣货任务成功
		 */
		Integer replenishQuantity = boxLocationMaxQuantity-sumBoxAvailable-sumBoxReplenish;
		if(sumStockAvailable.equals(0)){
//			logger.info("存储区可用库存不足");
		}else if(sumStockAvailable<=replenishQuantity){
//			logger.info("存储区可用库存不够箱拣补足到最高存量需要数量！");
			if(!initBoxReplenish.equals(0)){
				String initTaskIdStr = String.valueOf(initBoxTaskMap.get("init_task"));
				replenishQuantity = boxLocationMaxQuantity-sumStockAvailable+initBoxReplenish;
				String[] initTaskIdArr = initTaskIdStr.split(","); 
				for (String initTaskId : initTaskIdArr) {
					this.cancelMergeReplenishmentTask(initTaskId, 1, "SYSTEM");
				}
			}
			String locationType =Location.LOCATION_TYPE_BOX_PICK;
//			logger.info("productIdReplenish:"+productIdReplenish + " ;sumStockAvailable:"+sumStockAvailable+" ; locationType："+locationType); 
			taskIdStr = this.createGeneralReplenishTask(physicalWarehouseId,customerId,productIdReplenish,sumStockAvailable,locationType);
		}else if(sumStockAvailable>replenishQuantity){
			if(!initBoxReplenish.equals(0)){
				String initTaskIdStr = String.valueOf(initBoxTaskMap.get("init_task"));
				replenishQuantity = boxLocationMaxQuantity-sumBoxAvailable-sumBoxReplenish+initBoxReplenish;
				
				String[] initTaskIdArr = initTaskIdStr.split(","); 
				for (String initTaskId : initTaskIdArr) {
					this.cancelMergeReplenishmentTask(initTaskId, 1, "SYSTEM");
				}
			}
			String locationType =Location.LOCATION_TYPE_BOX_PICK;
			taskIdStr = this.createGeneralReplenishTask(physicalWarehouseId,customerId,productIdReplenish,replenishQuantity,locationType);
		}
		return taskIdStr;
	}
	/**
	 * 具体一般件拣待补货数量计算
	 * by ytchen
	 */
	private String generalPieceReplenishTask(Integer productIdReplenish,Integer pieceLocationMaxQuantity,
			Integer sumPieceAvailable,Integer sumPieceReplenish, Integer physicalWarehouseId,
			Integer customerId) {
		String log = "physicalWarehouseId_"+physicalWarehouseId+";customerId_"+customerId+";productIdReplenish_"+productIdReplenish;
		String taskIdStr = "";
		
		Integer sumBoxAvailable = replenishmentDao.getAvailableQtyByLocationType(physicalWarehouseId,customerId,productIdReplenish,Location.LOCATION_TYPE_BOX_PICK);//箱拣货区总available  (BA)
		Integer sumBoxReplenish = replenishmentDao.getReplenishQtyByProductLocationType(physicalWarehouseId,"General",productIdReplenish,Location.LOCATION_TYPE_STOCK);//箱拣货区在途数量 (BL)
		/**
		 * 件拣货区新增补货数量(PW) = MAX-PA-PL 
		 * 1. BA>=PW : 现有箱拣货区可用库存>件拣货新增补货数量，则取消未开始的补货任务，生成新的补货任务，新补货任务数量=MAX-PA-PL+PU  
		 * 2. BA+BL>PW>BA ：现有箱拣货区可用数量与箱拣货在途数量>件拣货新增补货数量>现有箱拣货区可用数量， 不处理 
		 * 3. BA+BL<=PW : 现有箱拣货区可用数量与箱拣货在途数量<件拣货新增补货数量 :
		 * 	3.1  如果存储区数量>0,生成新的箱补货任务，补货数量以箱拣最高存量为准，如果没有设置取件拣货区最高存量 
		 * 	3.2 如果存储区数量=0，生成件补货任务，补货数量取 箱
		 */
		Integer replenishQuantity = pieceLocationMaxQuantity-sumPieceAvailable-sumPieceReplenish;
		if(sumBoxAvailable>=replenishQuantity){
			Map<String,Object> initPieceTaskMap = replenishmentDao.getInitReplenishTask(Location.LOCATION_TYPE_BOX_PICK,productIdReplenish,physicalWarehouseId,customerId);
			Integer initPieceReplenish = Integer.parseInt(String.valueOf(initPieceTaskMap.get("init_replenish")));// 件拣货库区在途中状态为“未开始”的任务商品数量 (PU)
			if(!initPieceReplenish.equals(0)){
				String initTaskIdStr = String.valueOf(initPieceTaskMap.get("init_task"));
				replenishQuantity = pieceLocationMaxQuantity-sumPieceAvailable-sumPieceReplenish+initPieceReplenish;
				String[] initTaskIdArr = initTaskIdStr.split(","); 
				for (String initTaskId : initTaskIdArr) {
					this.cancelMergeReplenishmentTask(initTaskId,1, "SYSTEM");
				}
			}
			String locationType =Location.LOCATION_TYPE_PIECE_PICK;
			taskIdStr = this.createGeneralReplenishTask(physicalWarehouseId,customerId,productIdReplenish,replenishQuantity,locationType);
		}else if(sumBoxAvailable+sumBoxReplenish>replenishQuantity && replenishQuantity>sumBoxAvailable){
		}else if(sumBoxAvailable+sumBoxReplenish<=replenishQuantity){
//			logger.info(log+"箱拣货区库存+在途库存<件需库存！");
			//先判断存储区是否有库存，有则继续。否则直接生成件件货区补货任务，数量取sumBoxAvailable
			Integer sumStockAvailable =replenishmentDao.getAvailableQtyByLocationType(physicalWarehouseId,customerId,productIdReplenish,Location.LOCATION_TYPE_STOCK);
			List<ConfigReplenishment> configReplenishmentList = configReplenishmentDao.selectConfigByJob(physicalWarehouseId,customerId,productIdReplenish,"BOX");
			if(sumStockAvailable.equals(0) && !sumBoxAvailable.equals(0)){//存储区无库存 
//				logger.info(log+"存储区库存空，箱拣区还有库存，继续生成件拣货区补货任务！");
				//创建件件货区补货任务
				String locationType =Location.LOCATION_TYPE_PIECE_PICK;
				taskIdStr = this.createGeneralReplenishTask(physicalWarehouseId,customerId,productIdReplenish,sumBoxAvailable,locationType);
			}else if(sumStockAvailable.equals(0) && sumBoxAvailable.equals(0)){
				// 若箱拣区也没库存，不生成任务
//				logger.info(log+"存储区库存空，箱拣区无库存，不生成补货任务！");
			}else if(configReplenishmentList.size()>0){
//				logger.info(log+"存储区有库存，生成箱拣补货任务！");
				for (ConfigReplenishment configReplenishment : configReplenishmentList) {
					taskIdStr = generalBoxReplenishTask(productIdReplenish,configReplenishment.getBox_location_max_quantity(),
							sumBoxAvailable,sumBoxReplenish,physicalWarehouseId,customerId);
				}
			}else{
//				logger.info(log+"存储区有库存，但箱拣未设置最高存量，生成箱拣任务，数量取件拣最高存量！");
				//如果箱拣没有设置最高存量或者最高存量=0，则生成箱拣任务，数量取件拣货区最高存量pieceLocationMaxQuantity
				String locationType =Location.LOCATION_TYPE_BOX_PICK;
				taskIdStr = this.createGeneralReplenishTask(physicalWarehouseId,customerId,productIdReplenish,pieceLocationMaxQuantity,locationType);//generalBoxReplenishTaskByPieceMax(physicalWarehouseId,customerId,productIdReplenish,pieceLocationMaxQuantity);
			}
			
		}
		return taskIdStr;
	}
	
	
	/**
	 * 根据商品+数量+源库区+仓库+货主 查找足量的peoductLocation库存信息
	 * 1. 生产日期 靠前
	 * 2. 最少拣货库位 
	 */
	private List<ProductLocation> getFromProductLocation(String productStatus,String locationType,Integer physicalWarehouseId, Integer customerId,Integer productId) {
		return productLocationDao.getFromProductLocation(productStatus,locationType,physicalWarehouseId,customerId,productId);
	}
	/**
	 * 根据源库位 生产日期+商品sku+数量 找最优目标库位
	 * 1. 没有配置/配置为空，直接取溢出库位0
	 * 2. 配置范围内查找：
	 * 	2.1 商品与批次完全一致，有相同SKU库存
	 * 	2.2 当前有SKU库存 ，允许混放批次的库位 （不考虑当前库位是否允许混放商品，只要此库位目前有此sku；不考虑此商品属性是否为批次维护）
	 * 	2.3 空库位
	 * 	2.4 溢出库位 0
	 * 规约：如果该商品在一个库位上任意一个批次登记异常，那么这个库位都不会被考虑
	 */
	private Integer getBestToLocationIdByProductId(Integer physicalWarehouseId,
			Integer customerId, Integer productId,
			Integer replenishNum, String validaty, String locationType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("physical_warehouse_id", physicalWarehouseId);
		map.put("customer_id", customerId);
		map.put("product_id", productId);
		//1. 查找配置信息
		ConfigReplenishment configReplenishment = replenishmentDao.selectReplenishmentIsExist(map);
		String fromLocationBarcode = "";
		String toLocationBarcode = "";
		if(WorkerUtil.isNullOrEmpty(configReplenishment)){
//			logger.info("getBestToLocationIdByProductId productId:"+productId+"," +
//					"physicalWarehouseId:"+physicalWarehouseId+" hava not config_replenish!");
			return 0;
		}else if(locationType.equals(Location.LOCATION_TYPE_PIECE_PICK)){//目标库位 为 件拣货库位
			if(WorkerUtil.isNullOrEmpty(configReplenishment.getFrom_piece_location_barcode()) || 
					WorkerUtil.isNullOrEmpty(configReplenishment.getTo_piece_location_barcode())){
//				logger.info("getBestToLocationIdByProductId productId:"+productId+"," +
//						"physicalWarehouseId:"+physicalWarehouseId+" config_replenish is empty for "+locationType+" !");
				return 0;
			}
			fromLocationBarcode = configReplenishment.getFrom_piece_location_barcode();
			toLocationBarcode = configReplenishment.getTo_piece_location_barcode();
			if(fromLocationBarcode.compareTo(toLocationBarcode)>0){
				String tempBarcode = toLocationBarcode;
				toLocationBarcode = fromLocationBarcode;
				fromLocationBarcode = tempBarcode;
			}
			
		}else if(locationType.equals(Location.LOCATION_TYPE_BOX_PICK)){// 目标库位为箱拣货库位
			if(WorkerUtil.isNullOrEmpty(configReplenishment.getFrom_box_location_barcode()) || 
					WorkerUtil.isNullOrEmpty(configReplenishment.getTo_box_location_barcode())){
//				logger.info("getBestToLocationIdByProductId productId:"+productId+"," +
//						"physicalWarehouseId:"+physicalWarehouseId+" config_replenish is empty for "+locationType+" !");
				return 0;
			}
			fromLocationBarcode = configReplenishment.getFrom_box_location_barcode();
			toLocationBarcode = configReplenishment.getTo_box_location_barcode();
			if(fromLocationBarcode.compareTo(toLocationBarcode)>0){
				String tempBarcode = toLocationBarcode;
				toLocationBarcode = fromLocationBarcode;
				fromLocationBarcode = tempBarcode;
			}
		}
		List<Map<String,Object>> productLocationList = 
				replenishmentDao.selectGeneralBestLocation(fromLocationBarcode,toLocationBarcode,physicalWarehouseId,productId,locationType);
		if(WorkerUtil.isNullOrEmpty(productLocationList)){
//			logger.info("getBestToLocationIdByProductId productId:"+productId+"," +
//						"physicalWarehouseId:"+physicalWarehouseId+" have not empty location!");
			return 0;
		}else{
			Integer i = 0;
			Integer locationId = 0;
			String locationBarcode = "";
			while(i<=2 && locationId.equals(0)){
				for (int j = 0; j < productLocationList.size(); j++) {
					Date date = new Date();
					try{
						date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(validaty);
					}catch (Exception e) {
					}
					long unixTimestamp = date.getTime()/1000;
					Map<String,Object> productLocation = productLocationList.get(j);
					if(i==0 && (String.valueOf(productLocation.get("product_validity_group")).contains(productId+"_0_"+unixTimestamp)
							|| String.valueOf(productLocation.get("product_validity_group")).contains(productId+"_1_"+unixTimestamp))){
						locationId = Integer.parseInt(String.valueOf(productLocation.get("location_id")));
						locationBarcode = String.valueOf(productLocation.get("location_barcode"));
//						logger.info("getBestToLocationIdByProductId productId:"+productId+"," +
//								"physicalWarehouseId:"+physicalWarehouseId+" get best toLocationBarcode:" + locationBarcode +" by type 1");
						break;
					}else if(i==1 && String.valueOf(productLocation.get("product_validity_group")).contains(productId+"_1_")){
						locationId = Integer.parseInt(String.valueOf(productLocation.get("location_id")));
						locationBarcode = String.valueOf(productLocation.get("location_barcode"));
//						logger.info("getBestToLocationIdByProductId productId:"+productId+"," +
//								"physicalWarehouseId:"+physicalWarehouseId+" get best toLocationBarcode:" + locationBarcode +" by type 2");
						break;
					}else if(i==2 && WorkerUtil.isNullOrEmpty((productLocation.get("product_validity_group")))){
						locationId = Integer.parseInt(String.valueOf(productLocation.get("location_id")));
						locationBarcode = String.valueOf(productLocation.get("location_barcode"));
//						logger.info("getBestToLocationIdByProductId productId:"+productId+"," +
//								"physicalWarehouseId:"+physicalWarehouseId+" get best toLocationBarcode:" + locationBarcode +" by type 3");
						break;
					}
				}
				i++;
			}
			return locationId;
		}
	}
	/**
	 * 创建一般拣货任务
	 * 1. 需要数量 /相容，向上取整 
	 * 2. 根据sku与quantity获取源库位 生产日期靠前 的最少源数量库位组合
	 * 3. 最优目标库位查找
	 */
	private String createGeneralReplenishTask(Integer physicalWarehouseId,
			Integer customerId, Integer productIdReplenish,
			Integer replenishQuantity,String locationType) {
		String toLocationType=locationType;
		String fromLocationType = "";
		if(Location.LOCATION_TYPE_BOX_PICK.equals(locationType)){
			fromLocationType = Location.LOCATION_TYPE_STOCK;
		}else{
			fromLocationType = Location.LOCATION_TYPE_BOX_PICK;
		}
		String taskIdStr = "";
		//1. 查询商品 箱容信息
		Product product = productDao.selectByPrimaryKey(productIdReplenish);
		Integer spec = product.getSpec();
		//2. 查询源库位
		List<ProductLocation> fromProductLocationList = this.getFromProductLocation("NORMAL",fromLocationType,physicalWarehouseId,customerId,productIdReplenish);
		if(WorkerUtil.isNullOrEmpty(fromProductLocationList)){
//			logger.info("创建新任务时源库位库存不足（被抢占）了");
			return taskIdStr;
		}
		boolean flag = true;
		//3. 选择源库位 : 包含数量为min(需求，from_qty)
		for (ProductLocation fromProductLocation : fromProductLocationList) {
			if(replenishQuantity>0){
				//4. 根据源库位+生产日期+数量 找目标库位
				Integer spacNum = replenishQuantity/spec+(replenishQuantity%spec==0?0:1);
				Integer availableQty = fromProductLocation.getQty_available();
				Integer replenishNum = (availableQty/spec>=spacNum)?(spacNum*spec):availableQty/spec*spec; //此次补货数量
				if(replenishNum.equals(0)){
//					logger.info("源库区不是整箱货物，不生成补货任务！");
					flag = false;
					break;
				}
				replenishQuantity = replenishQuantity-replenishNum; //待补货数量
				String validaty = fromProductLocation.getValidity();
				Integer fromPlId = fromProductLocation.getPl_id();
				Integer toLocationId = this.getBestToLocationIdByProductId(physicalWarehouseId,customerId,productIdReplenish,replenishNum,validaty,toLocationType);
				if(toLocationId.equals(0) && Location.LOCATION_TYPE_BOX_PICK.equals(toLocationType)){
					toLocationId = this.getBestToLocationIdByProductId(physicalWarehouseId,customerId,productIdReplenish,replenishNum,validaty,Location.LOCATION_TYPE_PIECE_PICK);
				}
				java.util.Date now = new java.util.Date();
				String createTime = DateUtils.getStringTime(now);
				
				//5.1 插入task
				Task task = new Task();
				task.setPhysical_warehouse_id(physicalWarehouseId);
				task.setCustomer_id(customerId);
				task.setTask_type(Task.TASK_TYPE_REPLENISHMENT);
				task.setTask_status(Task.TASK_STATUS_INIT);
				task.setTask_level(1);
				task.setFrom_pl_id(fromPlId);
				task.setTo_location_id(toLocationId);
				task.setTo_pl_id(0);
				task.setProduct_id(productIdReplenish);
				task.setQuantity(replenishNum);
				task.setOperate_platform("WEB");
				task.setCreated_user("SYSTEM");
				task.setCreated_time(createTime);
				task.setLast_updated_user("SYSTEM");
				task.setLast_updated_time(createTime);
				taskDao.insertTask(task); 
				Integer taskId =task.getTask_id();
				if(taskId>0){
					//5.2 减去pl available
					Integer col = productLocationDao.updateProductLocationForMinusAvailable(replenishNum, fromPlId);
					//5.3 插入action
					UserActionTask userActionTask = new UserActionTask();
					userActionTask.setTask_id(taskId);
					userActionTask.setAction_note("一般拣货任务生成");
					userActionTask.setAction_type(Task.TASK_STATUS_INIT);
					userActionTask.setTask_status(task.getTask_status());
					userActionTask.setCreated_user("SYSTEM");
					userActionTask.setCreated_time(new Date());
					int col3 = userActionTaskDao.insert(userActionTask);
					if(col>0 && col3>0){
						taskIdStr = taskIdStr +taskId+",";
					}else{
//						logger.info("create taskAction & minus pl_available failed!");
//						logger.info("创建一般拣货任务失败！");
						break;
					}
				}else{
//					logger.info("create general failed! from_pl:"+fromPlId+";tolocation_id:"
//							+toLocationId+";productId:"+productIdReplenish+";number:"+replenishNum);
//					logger.info("创建一般拣货任务失败！");
					break;
				}
			}else{
				break;
			}
		}
		if(replenishQuantity>0 && flag){
			logger.info("执行时，发现待补货任务所需数量源库位数据不足了！");
		}
		return taskIdStr;
	}

	/**
	 * 合并取消拣货任务
	 * @param taskId
	 * @param cancelReason
	 * @param actionUser
	 * @return
	 */
	public Map cancelMergeReplenishmentTask(String taskId,Integer cancelReason,String actionUser){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> paramsForCancelMap = new HashMap<String,Object>();
		//1. 加锁
		Task task = taskDao.selectByTaskIdForUpdate(Integer.parseInt(taskId));
		//2. task 更新
		paramsForCancelMap.put("taskId", task.getTask_id());
		paramsForCancelMap.put("cancelReason", cancelReason);
		paramsForCancelMap.put("actionUser", actionUser);
		paramsForCancelMap.put("taskStatus", Task.TASK_STATUS_CANCEL);//合并取消，状态修改为“已取消”
		int col = taskDao.updateTaskCancel(paramsForCancelMap);
		//3. pl加回available
		int col2 = productLocationDao.updateProductLocationForAddAvailable(task.getQuantity(), task.getFrom_pl_id());
		//4. action 
		UserActionTask userActionTask = new UserActionTask();
		userActionTask.setTask_id(Integer.parseInt(taskId));
		userActionTask.setAction_note("合并任务取消");
		userActionTask.setAction_type(Task.TASK_STATUS_CANCEL);
		userActionTask.setTask_status(task.getTask_status());
		userActionTask.setCreated_user(actionUser);
		userActionTask.setCreated_time(new Date());
		int col3 = userActionTaskDao.insert(userActionTask);
		if(col > 0 && col2 >0 && col3> 0){
			returnMap.put("result", Response.SUCCESS);
			returnMap.put("success", Boolean.TRUE);
			returnMap.put("note", "取消补货任务成功!");
		}else{
			throw new RuntimeException("合并取消补货任务失败");
		}
		return returnMap;
	}
	

}