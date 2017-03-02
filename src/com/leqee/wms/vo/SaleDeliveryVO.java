package com.leqee.wms.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 销售出库VO
 * @author qyyao
 *
 */
public class SaleDeliveryVO implements Serializable {

	private static final long serialVersionUID = 34L;
	private Integer orderId;
	private String actionUser;
	private List<SaleProductDelDetail> productDelAcceDetails ;  //出入库明细列表

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getActionUser() {
		return actionUser;
	}

	public void setActionUser(String actionUser) {
		this.actionUser = actionUser;
	}

	public List<SaleProductDelDetail> getProductDelAcceDetails() {
		return productDelAcceDetails;
	}

	public void setProductDelAcceDetails(
			List<SaleProductDelDetail> productDelAcceDetails) {
		this.productDelAcceDetails = productDelAcceDetails;
	}

	

	
	
	
}
