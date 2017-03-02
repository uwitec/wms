package com.leqee.wms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.Shop;

public interface ShopDao {
   
	
	public Shop selectByShopId(String oms_shop_id);
	
	public Shop selectByShopName(String oms_shop_id,String shop_name);
	
	public int insertShop(Shop shop);
	
	public int updateShop(Shop shop);


	public List<Shop> selectAll();

	public void batchInsert(@Param("list")List<Shop> shopList);

	public void batchUpdate(@Param("list")List<Shop> shopListUpdate);

	
	public List<Shop> selectShopsList();

}
