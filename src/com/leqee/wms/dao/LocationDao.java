package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.Location;

/**
 * @author hchen1
 * @CreatedDate 2016.06.23
 *
 */
public interface LocationDao {
	
	/**
	 *根据库位信息去查询location_id
	 * */
	public Location selectLocationById(Integer locationId);
	
	public Location selectLocationIdByLocationBarCode(
			@Param("physical_warehouse_id") Integer physical_warehouse_id,
			@Param("location_barcode") String location_barcode,
			@Param("is_empty") String is_empty);
	
	public List<Map> selectLocationIdByLocationBarCodeV2(
			@Param("physical_warehouse_id") Integer physical_warehouse_id,
			@Param("location_barcode") String location_barcode);
	
	public int updateLocationNotEmptyByLocationId(Integer locationId);

	public Map<String, Object> selectLocationIdByLocationBarcode(List<String> LocationBarcodeList);
	public int updateLocationBarByLocationId (Integer locationId,String locationBar);
	public List<Map<String, Object>> selectByLocationId(Map<String, Object>map);
	
	public List<Map<String, Object>> selectByLocationMap(Map<String, Object>map);
	
	public List<Location> selectAllLocationList(@Param("physical_warehouse_id")Integer physical_warehouse_id,
			@Param("location_type")String location_type);

	public void insertBHcode(Location location);
	
	public int updateLocationIsEmpty(Integer locationId);
	public int updateLocationIsEmpty1(Integer locationId);
	
	public Location selectLocationByLocationType(Integer physical_warehouse_id,String location_type);
	

	public List<Location> checkLocationTypeByBarcode(@Param("locationBarcodeList")List<String> locationBarcodeList,@Param("physicalWarehouseId")Integer physicalWarehouseId);

	public int getLocationIdInVariance(@Param("physical_warehouse_id")Integer physical_warehouse_id,
			@Param("location_type")String location_type);

	public int getLocationIdInVarianceNum(@Param("physical_warehouse_id")Integer physical_warehouse_id,
			@Param("location_type")String locationTypeVarianceMimus);

	public void insert(Location location);

	public Location getCountLocationsByPhysicalAndId(@Param("physical_warehouse_id")int physical_warehouse_id,
			@Param("location_barcode")String location_barcode);

	public List<Integer> selectLocationIdForTest(@Param("physical_warehouse_id")int physical_warehouse_id);
}
