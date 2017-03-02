package com.leqee.wms.service;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import com.leqee.wms.response.Response;

@WebService
public interface ExpressService {
	/*获取面单号段*/
	public void batchApplyTrackingNumber(Integer shipping_app_id) throws Exception; 
	
	/*获取大头笔*/
	public void batchUpdateMark(String shippingCode)throws Exception;
	/**
	 * 获取系统当前（热敏批量）快递，仓库，资源数信息
	 * @return
	 */
	public List<Map> getAllThermalInfo();

	public List<Map> getWarehouseShippingThermal();

	public List<Integer> selectNeedApplyShippingAppId();

	public List<Map> selectShippingInfoList(String shippingCode, Double hours);

	public void reportTrackingNumber(Map shippingInfo, String shippingCode)throws Exception;

	public Integer releaseTrackingNumber();

	public Response batchAddFirstShipmentAndMark(Integer physicalWarehouseId, String batchPickGroupIds,Double hours, String endDate);

	public List<Map<String, Object>> getThermalRepositoryList();

	
}