package com.leqee.wms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.ProductLocationDetail;

public interface ProductLocationDetailDao {
	
	public int insert(ProductLocationDetail productLocationDetail);
	
	public Integer  selectProductLocationDetailByOrderId(Integer order_id,String barcode,Integer customer_id);
	
	public Integer  selectProductLocationDetailByOrderIdV1(Integer order_id,Integer order_goods_id);

	public void insertList(@Param("list")List<ProductLocationDetail> pldList);

	public void batchInsert(@Param("list")List<ProductLocationDetail> pldList);
	
	public Integer  selectPackageProductLocationDetailByOrderId(Integer order_id);
	
	public Integer  selectUnPackageProductLocationDetailByOrderId(Integer order_id);
	
	public List<ProductLocationDetail> selectProductDetailByOrderId(Integer order_id);
	
	public Integer  selectSumTotalByOrderId(Integer order_id,Integer order_goods_id);

	public void batchInsert2(@Param("list")List<ProductLocationDetail> pldList);
	
	public void batchInsert3(@Param("list")List<ProductLocationDetail> pldList);
	
}
