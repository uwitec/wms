package com.leqee.wms.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 产品销售出库明细，用于记录出库的商品明细
 * @author qyyao
 *
 */
public class SaleProductDelDetail implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer orderGoodsId;     // 
	private Integer productId;        // 产品ID
    private Integer goodsNumber;      // 数量
	private List<String> serialNos = new ArrayList<String>();   // 串号
	
	public Integer getOrderGoodsId() {
		return orderGoodsId;
	}
	public void setOrderGoodsId(Integer orderGoodsId) {
		this.orderGoodsId = orderGoodsId;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public Integer getGoodsNumber() {
		return goodsNumber;
	}
	public void setGoodsNumber(Integer goodsNumber) {
		this.goodsNumber = goodsNumber;
	}
	public List<String> getSerialNos() {
		return serialNos;
	}
	public void setSerialNos(List<String> serialNos) {
		this.serialNos = serialNos;
	}
}
