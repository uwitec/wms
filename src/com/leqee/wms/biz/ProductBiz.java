package com.leqee.wms.biz;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import com.leqee.wms.entity.Product;

public interface ProductBiz {

	
	public Integer selectProductIdByBarcodeCustomerId(String barcode,Integer customerId);
	
	public List<Map> getProductList(Map map);
	
	public Map updateProduct(Product product);
	
	public Map uploadProduct(InputStream input,boolean isE2007,String actionUser);
	
	public String selectProductNameByBarcodeCustomer(String barcode,String customer_id);
	
	public List<Map> getPrePackProductList(Map map);
	
	public Map printPrePackProduct(List<String> orderIdList,String type,Integer physical_warehouse_id,String actionUser);

	public Map endPrePackProduct(Integer orderId,Integer physical_warehouse_id);
	
	public Map scanPrepackGrouding(String location_barcode,Integer physical_warehouse_id);
	
	public Map prepackSubmitGrouding(String location_barcode,Integer physical_warehouse_id,String new_location_barcode,String actionUser);

	public Object getProductList22(Map<String, Object> searchMap);
	
	public List<Map> getProductListExport(Map<String, Object> searchMap);
	
	public List<Map<String,String>> selectProductsBySkuCode(String sku_code);
}