package com.leqee.wms.biz;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Param;
import org.springframework.core.annotation.Order;

import com.leqee.wms.entity.BatchPick;
import com.leqee.wms.entity.OrderPrepack;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ProductPrepackage;
import com.leqee.wms.response.Response;

public interface OrderPrepackBiz {

	
	
	public Map<String, Object> loadGoodsForOrder(String order_sn,String type);
	
	public Map<String, Object> getCheckOrderGoodsConfirm(Integer check_order_id,String packbox_product_barcode);
	
	public void updateStatus(OrderPrepack orderPrepack);
	
	public Map<String, Object> groundingPrint(Integer order_id,Integer packbox_product_id,String userName,Integer preNumber,Integer prePackageNumber);
	
	public Map<String, Object> UngroundingPrint(Integer order_id,String userName,Integer preNumber,Integer prePackageNumber);

	public List<OrderPrepack> selectMeetConditionOrderPrepack(Date payTime,
			List<String> groupCodeList , Integer physicalWarehouseId);
	
	public OrderPrepack selectOrderPrepackByOmsTaskSn(String oms_task_sn); //add by xhchen
	
	public void insertOrUpdate(OrderPrepack orderPrepack);  //add by xhchen

	public void updateQtyUsed(Integer orderId, Integer qtyNeedThisTime);
	
	public void save(OrderPrepack orderPrepack,Product product,List<ProductPrepackage> prepackages);
	
	public void initOrderPrepackProductPrepackageList(OrderPrepack orderPrepack);
	
	public Map<String, Object> getPrepackByBarcode(String barcode,Integer physcial_warehouse_id);
	
	public void releaseQtyUsed(Integer order_id);
	
	
	
}
