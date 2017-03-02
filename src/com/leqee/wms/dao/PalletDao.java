package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.Pallet;


/**
 * @author mao
 * @date 2016-3-24
 * @version 1.0.0
 */
public interface PalletDao {
	
	public List<Map> selectPalletShipmentList(Map<String, Object> map);

	public List<Integer> selectPalletIdListNeedToShipmentByPage(Map searchMap);

	public List<Map> selectPalletShipmentListV2(@Param("list")List<Integer> palletNoList);

	public void updatePalletPrintCount(@Param("list")List<String> codeList);

	public Pallet selectPalletBySnForUpdate(String palletSn);

	public int selectPalletShipmentCount(Map<String, Object> map);

}
