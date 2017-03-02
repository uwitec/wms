package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import com.leqee.wms.entity.Region;

public interface RegionDao {
   
	
	public Region selectByName(String name);

	public List<Region> selectAllProvince();

	public Region selectLikeName(String name);

	public Region selectByNameAndParentId(Map<String, Object> mapParams);
	
	public Region selectRegionByRegionId(short region_id);
	
	public int insertRegion(Region region);
	
	public int updateRegion(Region region);
}
