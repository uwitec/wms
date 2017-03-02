package com.leqee.wms.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;


import com.leqee.wms.entity.ConfigReplenishment;
import com.leqee.wms.entity.Hardware;
import com.leqee.wms.entity.ProductLocation;

public interface ReplenishmentDao {

	List<Map<String, Object>> selectByMapByPage(Map<String, Object> map);
	
	ConfigReplenishment selectReplenishmentIsExist(Map<String, Object> map);
	
	int insertConfigReplenishment(List<ConfigReplenishment> configReplenishmentList);
	
	ConfigReplenishment selectReplenishmentByCustomerId(Integer product_id,Integer physical_warehouse_id);
	
	int updateReplenishmentByUpdateReplenishmentMap(Map<String, Object>map);
	
	int insertConfigReplenishmentByMap(Map<String, Object> map);

	public int getSameBHCodeNum(@Param("codelist") ArrayList<String> codelist);
	
	public Map updateReplenishmentFromProductLocation(Map map);
	
	public int deleteReplenishment (String customer_id, Integer product_id,Integer physical_warehouse_id);
	
	public List<Map> selectReplenishmentTaskByPage(Map map); // WEB端获取打印补货任务列表
	
	public Map selectReplenishmentTaskById(Map map); // 根据taskId获取补货任务
	
	public List<Map> selectLocationIsCanMix(Map map); // 判断库位是否可以混放商品
	
	public ProductLocation selectProductLocation(Map map); // RF补货下架时根据商品信息查找ProductLocation
	
	public Map selectProductLocationByLocation(Map map); // RF补货上架时根据目标库位查找ProductLocation
	
	public int updateReplenishmentFromProductLocationMinus(Map map); // RF补货下架时更新ProductLocation
	
	public int updateReplenishmentFromProductLocationAdd(Map map); // RF补货上架时更新ProductLocation
	
	public Map selectReplenishmentByBhCode(Map map); // RF补货上架时根据补货标签加载标签内商品信息
	
	List<ConfigReplenishment> selectReplenishmentListByProductIdList(@Param("customer_id")Integer customer_id,
			@Param("product_id_list")List<Integer> goodsIdList, @Param("physical_warehouse_id")Integer physical_warehouse_id);
	List<Integer> selectGeneralCustomerByPhysicalWarehouseId(Integer physicalWarehouseId);

	Integer getAvailableQtyByLocationType(@Param("physicalWarehouseId") Integer physicalWarehouseId,@Param("customerId") Integer customerId,@Param("productIdReplenish") Integer productIdReplenish,@Param("locationType")String locationType);

	Integer getReplenishQtyByProductLocationType(@Param("physicalWarehouseId")Integer physicalWarehouseId,@Param("taskType")String taskType, @Param("productIdReplenish")Integer productIdReplenish, @Param("locationType")String locationType);

	void updateScheduleQueueReplenish(@Param("queueId")Integer queueId,@Param("status") String status,@Param("taskIdStr") String taskIdStr, @Param("taskCount")Integer taskCount);

	List<Map<String, Object>> selectGeneralBestLocation(@Param("fromLocationBarcode")String fromLocationBarcode,@Param("toLocationBarcode") String toLocationBarcode,
			@Param("physicalWarehouseId") Integer physicalWarehouseId,@Param("productId") Integer productId,
			@Param("locationType") String locationType);

	Map<String, Object> getInitReplenishTask(@Param("locationType")String locationType,
			@Param("productId")Integer productIdReplenish, @Param("physicalWarehouseId")Integer physicalWarehouseId,
			@Param("customerId")Integer customerId);

	List<Map> getReplenishmentTaskByBatchPickSnByPage(
			Map<String, Object> searchMap);

	
}
