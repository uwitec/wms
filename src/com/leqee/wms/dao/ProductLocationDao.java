package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.entity.ProductPrepackage;
import com.leqee.wms.entity.TaskAllotProductlocation;
import com.leqee.wms.page.PageParameter;
import com.sun.org.apache.bcel.internal.generic.Select;

public interface ProductLocationDao {

	ProductLocation selectProduLocationById (Integer pl_id );
	
	int selectSumProductLoaction(@Param("location_type")String location_type,@Param("physical_warehouse_id")int physical_warehouse_id,@Param("product_id") int product_id);

	List<ProductLocation> selectProductlocationList(@Param("physical_warehouse_id")int physical_warehouse_id,@Param("product_id") int product_id);



	int insert(ProductLocation productLocation);

	void updateProductLocationByAllotJob(@Param("list")List<ProductLocation> productlocationListUpdate);

	int updateProductLocationAdd(@Param("quantity")Integer quantity,@Param("pl_id")Integer pl_id);
	
	int updateProductLocation(@Param("quantity")Integer quantity,@Param("pl_id")Integer pl_id);
	
	int updateProductLocationTotalAvaliable(@Param("quantity1")Integer quantity1,@Param("quantity2")Integer quantity2,@Param("pl_id")Integer pl_id);
	
	int updateProductLocationV2(@Param("quantity")Integer quantity,@Param("pl_id")Integer pl_id);
	
	void lockProductlocationForAllotJob(@Param("list")List<Integer> lockProductLocationIdList);

	List<ProductLocation> getFromProductLocation(@Param("productStatus")String productStatus,@Param("locationType")String locationType,
			@Param("physicalWarehouseId")Integer physicalWarehouseId, @Param("customerId") Integer customerId,@Param("productId")  Integer productId);
	
	List<ProductLocation> selectExceptionList(@Param("list")List<Integer> goodsIdList);

	Integer updateProductLocationForMinusAvailable(Integer replenishNum,Integer fromPlId);

	int updateProductLocationForAddAvailable(int quantity, int from_pl_id);
	
	ProductLocation selectByMap(Map<String,Object> map);

	
	
	List<ProductLocation> selectProductlocationListV2(@Param("physical_warehouse_id")Integer physical_warehouse_id,
			@Param("warehouse_id")Integer warehouse_id, @Param("goodsIdList")List<Integer> goodsIdList, @Param("location_type")String location_type, @Param("status")String status);
	
	
	List<ProductLocation> selectProductlocationListV3(@Param("physical_warehouse_id")Integer physical_warehouse_id,
			@Param("warehouse_id")int warehouse_id, @Param("goodsIdList")List<Integer> goodsIdList, @Param("location_type")String location_type, @Param("status")String status);
	
	List<ProductLocation> selectProductlocationListV4(@Param("physical_warehouse_id")Integer warehouse_id,
			@Param("goodsIdList")List<Integer> goodsIdList, @Param("location_type")String location_type, @Param("status")String status,@Param("warehouse_id") int warehouse_id2);
	
	List<ProductLocation> selectProductlocationListV5(
			@Param("physical_warehouse_id")int physical_warehouse_id, @Param("productIdList")List<Integer> productIdList,
			@Param("location_type")String locationType, @Param("warehouse_id")int warehouse_id);

	List<ProductLocation> selectProductlocationListV6(@Param("physical_warehouse_id")Integer physical_warehouse_id,
			@Param("warehouse_id")Integer warehouseId, @Param("goodsIdList")List<Integer> goodsIdList, @Param("location_type")String location_type, @Param("status")String status);
	
	
	List<ProductLocation> selectProductlocationListV9(@Param("physical_warehouse_id")Integer physical_warehouse_id,@Param("warehouse_id")Integer warehouseId,
			@Param("goodsIdList")List<Integer> goodsIdList, @Param("location_type")String location_type, @Param("status")String status);
	
	Integer selectProductLocationQtyByLocationId(int locationId); // 通过库位ID来查找库位上商品总数

	List<Map<String,Object>> selectAllProductLocationByLocationId(@Param("locationId")Integer locationId, @Param("type")String type, @Param("barcode")String goodsBarcode);

	Map<String, Object> selectMapFromId(Integer plId);
	
	ProductLocation getproduLocations (Integer taskId,Integer to_pl_id );
	ProductLocation getFromProduLocations (Integer taskId,Integer from_pl_id);
	
	int updateProductLocationTotal(Integer num,Integer pl_id);
	int updateProductLocationTotalMinus(Integer num,Integer pl_id);
	Map<String, Object> selectValidityByOrderId(Map<String, Object>map);

	List<Map> productExceptionLocation(@Param("physical_warehouse_id")Integer physcial_warehouse_id);
	
	Integer updateProductLocationForFreezeAvailable(
			@Param("quantity") Integer quantity,
			@Param("pl_id") Integer pl_id);   // 冻结available值

	Integer updateProductLocationForReleaseAvailable(
			@Param("quantity") Integer quantity,
			@Param("pl_id") Integer pl_id);  // 解冻available值

	Integer selectMapFromLocationId(Integer locationId);

	List<ProductLocation> getAllImproveProductLocation(
			@Param("physical_warehouse_id")Integer physical_warehouse_id, @Param("productIdList")List<Integer> productIdList, @Param("locationIdList")List<Integer> locationIdList,@Param("warehouse_id") Integer warehouse_id);

	void updateProductLocationByImproveJob(
			@Param("list")List<ProductLocation> updateProductLoactionList);

	List<ProductLocation> getImproveProductLocationInVariance(
			@Param("physical_warehouse_id")Integer physical_warehouse_id,@Param("productIdList")List<Integer> productIdList,
			@Param("location_type")String location_type, @Param("warehouse_id")Integer warehouse_id);

	void batchInsert(@Param("list")List<ProductLocation> insertList);
	void batchInsert2(@Param("list")List<ProductLocation> insertList);

	
	public List<Map<String, Object>> selectNotEnoughProductItemByCustomerId(@Param("physicalWarehouseId")Integer physical_warehouse_id,@Param("customerId")Integer customer_id,@Param("con")String con,@Param("con1")String con1);

	List<ProductLocation> selectProductlocationListForBatchPick(
			@Param("list")List<Integer> productIdList, @Param("physical_warehouse_id")int physical_warehouse_id, @Param("warehouse_id")int warehouse_id);

	List<ProductLocation> selectProductlocationListForGT(
			@Param("list")List<Integer> productIdList, @Param("physical_warehouse_id")int physical_warehouse_id, @Param("warehouse_id") int warehouse_id);
	
	List<ProductLocation> selectAll(@Param("physical_warehouse_id")int physical_warehouse_id,@Param("customer_id") int customer_id, @Param("location_type")String location_type);


	void updateProductLocationV5(@Param("list")List<ProductLocation> updateList);
	
	void updateProductLocationV6(@Param("list")List<ProductLocation> updateList);

	List<ProductLocation> selectAllByPage(@Param("physical_warehouse_id")int physical_warehouse_id,
			@Param("customer_id")int customer_id, @Param("page")PageParameter page);

	void updateProductLocationByVariance(@Param("list")List<ProductLocation> updatePlList);
	
	List<ProductLocation> selectProductLocationByLocation2Product(@Param("physical_warehouse_id")int physical_warehouse_id,@Param("warehouse_id")int warehouse_id,@Param("location_id")int location_id,@Param("product_id")int product_id,@Param("status")String status);

	void updateProductLocationByPackOut(
			@Param("list")List<ProductLocation> updateProductlocationList);

	List<ProductLocation> selectAllVarinanceProductLocation(
			@Param("physical_warehouse_id")int physical_warehouse_id, @Param("productIdList") List<Integer> productIdList, @Param("warehouse_id")int warehouse_id);

	List<ProductLocation> selectAllByLocationTypeList(
			@Param("physical_warehouse_id")int physical_warehouse_id,@Param("customer_id")int customer_id,
			@Param("list")List<String> locationTypeList);

	List<ProductLocation> selectAllExceptionProductLocationList(
			@Param("physical_warehouse_id")int physical_warehouse_id, @Param("customer_id")int customer_id, @Param("location_type")String location_type);

	List<ProductLocation> selectAllCancelOutShelfProductLocationList(
			@Param("physical_warehouse_id")int physical_warehouse_id, @Param("customer_id")int customer_id);

	List<ProductPrepackage> selectAllProductPrepackagelist(
			@Param("list")List<Integer> prepackIdList);

	void updateProductLocationByImproveSingle(@Param("pl")ProductLocation pl2);
	
	List<Map<String, Object>> selectNeedTaggedByPage(@Param("locationTypeList")List<String> locationTypelist,@Param("validityStatuslist")List<String> validityStatuslist, @Param("page")PageParameter page);

	Integer batchUpdateValidityStatus(@Param("validityStatus") String validityStatus, @Param("list") List<Long> list);

	void updateLocationEmptyStatusByPlId(@Param("pl_id")int pl_id);

	void updateLocationEmptyStatusByPlIdMinus(@Param("pl_id")int pl_id, @Param("num")int num);

	List<TaskAllotProductlocation> selectTaskPlByBatchPickId(@Param("batch_pick_id")Integer batch_pick_id);

	void addQtyAvailableByTaskAllotProductlocationList(@Param("list")List<TaskAllotProductlocation> taskListPl);

	void minusQuantityByTaskAllotProductlocationList(@Param("list")List<TaskAllotProductlocation> taskListPl);
	

}
