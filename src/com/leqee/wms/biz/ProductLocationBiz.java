package com.leqee.wms.biz;

import java.util.List;

import com.leqee.wms.entity.ProductLocation;


public interface ProductLocationBiz {

	public void cancelOrderProductLocationReservation(Integer order_id);
	public void cancelOrderPrepackageProductLocationReservation(Integer order_id,Integer prepackage_product_id,String type );

}
