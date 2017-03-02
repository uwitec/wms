package com.leqee.wms.dao;

import java.util.List;

import com.leqee.wms.entity.OmsOrderTransferCode;

/**
 * 
 * @author xhchen
 *
 */
public interface OmsOrderTransferCodeDao {
	
	public OmsOrderTransferCode selectByProductId(String product_id);
	
	public List<OmsOrderTransferCode> selectBySkuCode(String sku_code);
	
	public List<OmsOrderTransferCode> selectByOrderId(String order_id);

}
