package com.leqee.wms.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.core.annotation.Order;

import com.leqee.wms.entity.BatchPick;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.OrderPrepack;
import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.ProductPrepackage;
import com.leqee.wms.entity.Task;

/**
 * @author Jarvis
 * @CreatedDate 2016.02.02
 *
 */
public interface OrderPrepackDao {
	
	public List<OrderPrepack> selectOrderPrepackByCustomerId(@Param("customer_id")String customerId);
	
	public Map<String, Object> selectOrderPrepackByOrderSn(Integer order_sn);
	
	public Map<String, Object> selectOrderPrepackByOrderSnV3(String order_sn);
	
	public List<Map<String, Object>> selectOrderPrepackByOrderSnV1(String order_sn,Integer order_id);
	
	public List<Map<String, Object>> selectOrderPrepackByOrderSnV2(String order_sn);
	
	public Map<String, Object> selectOrderPrepackByOrderSn(@Param("order_sn")String order_sn,@Param("type")String type);
	
	public List<Map<String, Object>>  selectProductPrePackage(Integer prepackage_product_id);
	
	public List<Map<String, Object>> selectPrePackageList(Integer order_id);
	
	public List<Map<String, Object>> selectPrePackageListV1(Integer order_id);
	
	public List<Task> getTaskIdByOrderId (Integer order_id,Integer product_id);
	
	public Map<String, Object> selectOrderPrepackByOrderId(Integer order_id);
	
	public OrderPrepack selectOrderPrepackByOrderIdForUpdate(Integer order_id);
	
	public List<Task> selectUnPackageList(Integer order_id,Integer product_id);
	
	public OrderPrepack selectOrderPrepackByOmsTaskSn(@Param("oms_task_sn")String oms_task_sn); //add by xhchen
	
	public int insertOrUpdate(OrderPrepack orderPrepack);  //add by xhchen

	public List<Map> getAllPackProduct(@Param("physical_warehouse_id")Integer physical_warehouse_id,
			@Param("customer_id")Integer customer_id, @Param("warehouse_id")Integer warehouse_id, @Param("type")String type);

	public void batchUpdateStatus(@Param("list")List<Integer> orderPrepackYList, @Param("status")String status);

	public List<OrderPrepack> selectProductPrePackageForUpdate(
			@Param("list")List<Integer> orderIdList);

	public List<OrderPrepack> selectOrderPrePackageForUpdate(@Param("physical_warehouse_id")Integer physical_warehouse_id,
			@Param("customer_id")Integer customer_id, @Param("warehouse_id")Integer warehouse_id, @Param("type")String type);

	public List<OrderPrepack> selectMeetConditionOrderPrepack(
			Map<String, Object> paramMap);

	public List<ProductPrepackage> selectProductPrepackageByPrepackageProductId(
			Integer prepackage_product_id);
	
	public List<OrderPrepack> selectOrderPrepackByPrepackageProductId(
			Integer prepackage_product_id);

	public int updateQtyUsed(@Param("orderId")Integer orderId, @Param("qtyNeedThisTime")Integer qtyNeedThisTime);

	//add by xhchen 2016-08-30
	public List<OrderPrepack> selectOrderPrepackByParam(@Param("customerId")Integer customerId,@Param("startTime")Date startTime,@Param("endTime")Date endTime);

	public List<Map> getNeedToOutPrepackMap(@Param("physical_warehouse_id")Integer physical_warehouse_id,
			@Param("customer_id")Integer customer_id,@Param("start")String start);

	
	public void updateQtyActual(Integer order_id,Integer qty_actual);
	
	public Integer selectSumTotal(Integer order_id,Integer product_id);
	
	public Integer selectMaxOrderId();  //获取最大的orderid用于生成ordersn
	
	public void updateStatus(@Param("oms_task_sn")String oms_task_sn,@Param("status")String status);
	
	public List<ProductPrepackage> selectOrderPrepacks(Integer component_product_id);
	
    public Integer  selectQtyUseNumber(Integer prepackage_product_id,Integer physical_warehouse_id);
    
    public Integer  selectQtyActualNumber(Integer prepackage_product_id,Integer physical_warehouse_id);
    
    public Integer  selectUnpackNumber(Integer prepackage_product_id,Integer physical_warehouse_id);

	public List<OrderPrepack> selectOrderPrePackageForUpdateById(@Param("orderId")Integer orderId);

	public List<OrderPrepack> selectOrderPrePackageByIdListForUpdate(
			@Param("list")List<OrderPrepack> orderPrepackForUpdatelist);

	public List<Map> selectOrderPrePackagePackById(@Param("orderId")Integer orderId);
    
    public List<OrderPrepack> selectOrderPrepackByPrepackageProductIdAndPayTime(Map<String,Object> map);
    
    public Map<String, Object> getPrepackByBarcode(String barcode,Integer physcial_warehouse_id);
    
   
    public List<Map<String, Object>> getPrepackByOrderId(Integer order_id);
}
