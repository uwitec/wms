package com.leqee.wms.entity;

import java.util.Comparator;

public class ProductLocationNumValidityComparator implements Comparator<ProductLocation> {

	@Override
	public int compare(ProductLocation o1, ProductLocation o2) {
		if(o1.getQty_available() - o2.getQty_available()==0){
			return o1.getValidity().compareTo(o2.getValidity());
		}
		else {
			return o2.getQty_available()-o1.getQty_available();
		}
	}

}
