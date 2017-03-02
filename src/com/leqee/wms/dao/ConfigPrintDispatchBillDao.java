package com.leqee.wms.dao;

import java.util.List;

import com.leqee.wms.entity.ConfigPrintDispatch;

public interface ConfigPrintDispatchBillDao {

	/**
	 * 获取店铺列表的特殊店铺数目
	 * @param shop_name
	 * @return
	 */
	int getCountRecord(List<String> shop_name);

	/**
	 * 获取店铺列表的特殊发货单数目
	 * @param shop_name
	 * @return
	 */
	int getCountPrintDispatchBill(List<String> shop_name);

	/**
	 * 获取店铺列表的发货单
	 * @param shop_name
	 * @return
	 */
	String getFileName(List<String> shop_name);

	/**
	 * 获取业务组的发货单
	 * @param customer_id
	 * @return
	 */
	String getCustomerFileName(int customer_id);

	/**
	 * 根据店铺名称找到所有的打印文件信息
	 * @param shop_name
	 * @return
	 */
	List<ConfigPrintDispatch> getConfigPrintDispatch(List<String> shop_name);

	List<String> getDistinctConfigPrintDispatch(
			List<String> shop_name);

	Integer getOrderDispatchNum(Integer orderId);

}
