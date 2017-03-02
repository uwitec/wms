package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.Product;
import com.leqee.wms.entity.SaleItems;


/**
 * @author hzhang1
 * @date 2016-2-26
 * @version 1.0.0
 */
public interface ProductDao {
     
	   int insert(Product record);
	   
	   Product selectByPrimaryKey(Integer product_id);
	    
	   public Product selectBySkuCode(String skuCode);
	   
	   int update(Product product);
	   
	   int delete(String skuCode);
	   
	   Integer selectProductIdByBarcodeCustomer(String barcode,Integer customerId);

	   Product selectProductByBarcodeCustomer(String barcode,Integer customerId);
	   
	   List<Product> selectProductList(String name);
	   
	   List<Product> selectAllProductList();
	   
	   List<Integer> selectProductIdList(Map map);

	   Map selectByBarodeForConsume(String barcode,String customerId);
	   
	   List<Product> selectByBarodeV2(@Param("barcode") String barcode,@Param("customerId") Integer customerId);
	   
	   List<Map> selectProductListByPage(Map map);
	   
	   int updateProductInfo(Product product);

	   List<Product> selectAllProductListByKeyList(@Param("productList")List<Integer> goodsIdList);
	   
	   String selectProductNameByBarcodeCustomer(String barcode,String customer_id);
	   
	   String selectProductNameByBarcode(String barcode);

	   Map<String, Object> selectCountUnCheckSpec(Integer customerIdx);

	   List<Product> getNullSpecProductList();

	List<SaleItems> getSaleNums(@Param("physical_warehouse_id")Integer physical_warehouse_id,
			@Param("group_id")Integer groupId, @Param("customer_id")Integer customerId, @Param("start")String start, @Param("end")String end);

	List<SaleItems> getSaleOrderNums(@Param("physical_warehouse_id")Integer physical_warehouse_id,
			@Param("group_id")Integer groupId, @Param("customer_id")Integer customerId, @Param("start")String start, @Param("end")String end);

	
	// 预打包相关SQL
	List<Map> selectPrePackProductListByPage(Map map);
	
	List<Map> selectPrePackProductByOne(@Param("order_id") Integer order_id);
	
	Integer sumPrePackProductNum(Integer productId);
	
	Integer sumPrePackProductNumber(Integer productId);
	
	// 预打包打印
	int updatePrintPrePackStatus(@Param("physical_warehouse_id")Integer physical_warehouse_id,@Param("order_id") Integer order_id,@Param("order_sn")String order_sn);
	
	List<Map> selectPrintPrePackList(@Param("physical_warehouse_id")Integer physical_warehouse_id,@Param("type")String type,@Param("order_id_list")List<String> orderIdList);
	
	int updateOrderPrepackStatus(@Param("physical_warehouse_id")Integer physical_warehouse_id,@Param("status")String status,@Param("limit_status")String limit_status,@Param("order_id")Integer order_id);
	
	Product selectProductByBarcode(String barcode);
	
	Map selectPrepackByLocationBarcode(@Param("physical_warehouse_id")Integer physical_warehouse_id,@Param("location_barcode")String location_barcode);

	List<Map>  selectProductList22ByPage(Map<String, Object> map);
	   
	List<Map>  selectProductListExport(Map<String, Object> map);
	
	//add by xhchen  通过任务productid查询product
	List<Map<String,String>> selectProductByProductId(Integer productId);
	
	List<Map> selectComponentProduct(@Param("product_id")Integer product_id);
	
	List<Map> selectPrePackPickTaskQuantity(@Param("physical_warehouse_id")Integer physical_warehouse_id,@Param("order_id")Integer order_id);

	List<Map> selectPrePackOutQuantity(@Param("physical_warehouse_id")Integer physical_warehouse_id,@Param("order_id")Integer order_id);
	
	List<Map> checkOrderPrepackIsOver(@Param("physical_warehouse_id")Integer physical_warehouse_id,@Param("order_id")Integer order_id);

	List<Map> checkOrderPrepackIsGrouding(@Param("physical_warehouse_id")Integer physical_warehouse_id,@Param("order_id")Integer order_id);
	
	Product selectProductBySkuCode(String skuCode);
	
	List<Map<String, Object>> selectProductLists (@Param("physical_warehouse_id")Integer physical_warehouse_id,@Param("customer_id")Integer customer_id,@Param("con")String con);
	
}
