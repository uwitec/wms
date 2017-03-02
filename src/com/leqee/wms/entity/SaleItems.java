package com.leqee.wms.entity;

import java.io.Serializable;

public class SaleItems  implements Serializable,
Comparable<SaleItems> {


	private static final long serialVersionUID = 1L;
	
	//商品
	private int product_id;
	private String product_name;
	private String barcode;
	
	//物理仓
	private String warehouse_name;
	private int physical_warehouse_id;
	
	//货主
	private int customer_id;//
	private String name;
	
	//销售量
	private int sales;
	private int salesToday;
	private int orders;
	private int ordersToday;
	
	private int sortInCustomerToday;
	private int sortInCustomer;
	private int sortToday;
	private int sort;

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getWarehouse_name() {
		return warehouse_name;
	}

	public void setWarehouse_name(String warehouse_name) {
		this.warehouse_name = warehouse_name;
	}

	public int getPhysical_warehouse_id() {
		return physical_warehouse_id;
	}

	public void setPhysical_warehouse_id(int physical_warehouse_id) {
		this.physical_warehouse_id = physical_warehouse_id;
	}

	public int getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(int customer_id) {
		this.customer_id = customer_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSales() {
		return sales;
	}

	public void setSales(int sales) {
		this.sales = sales;
	}

	public int getOrders() {
		return orders;
	}

	public void setOrders(int orders) {
		this.orders = orders;
	}

	public int getSortInCustomer() {
		return sortInCustomer;
	}

	public void setSortInCustomer(int sortInCustomer) {
		this.sortInCustomer = sortInCustomer;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public int getSalesToday() {
		return salesToday;
	}

	public void setSalesToday(int salesToday) {
		this.salesToday = salesToday;
	}

	public int getOrdersToday() {
		return ordersToday;
	}

	public void setOrdersToday(int ordersToday) {
		this.ordersToday = ordersToday;
	}

	public int getSortInCustomerToday() {
		return sortInCustomerToday;
	}

	public void setSortInCustomerToday(int sortInCustomerToday) {
		this.sortInCustomerToday = sortInCustomerToday;
	}

	public int getSortToday() {
		return sortToday;
	}

	public void setSortToday(int sortToday) {
		this.sortToday = sortToday;
	}

	@Override
	public int compareTo(SaleItems o) {
		if(this.sales<o.sales){
			return 1;
		}else if(this.sales>o.sales){
			return -1;
		}
		return 0;
	}
	
}
