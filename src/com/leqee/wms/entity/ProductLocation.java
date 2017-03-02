package com.leqee.wms.entity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.leqee.wms.api.util.WorkerUtil;

public class ProductLocation implements Serializable,
		Comparable<ProductLocation>,Comparator<ProductLocation>{

	private static final long serialVersionUID = 1L;
	// validity_status常量
	public static final String Validity_STATUS_NORMAL = "NORMAL";
	public static final String Validity_STATUS_WARNING = "WARNING";
	public static final String Validity_STATUS_UNSALABLE = "UNSALABLE";
	public static final String Validity_STATUS_EXPIRED = "EXPIRED";

	private int pl_id;
	private int product_id;
	private int location_id;
	private int qty_total = 0;
	private int qty_reserved = 0;
	private int qty_available = 0;
	private int qty_freeze = 0;

	private String status = "NORMAL";
	private String product_location_status = "NORMAL";
	private String validity = "1970-01-01 00:00:00";
	private String serial_number;
	private String created_user;
	private String created_time;
	private String last_updated_user;
	private String last_updated_time;
	
	private int physical_warehouse_id;
	private int customer_id;
	
	private String location_type;
	//当前库存的库位条码  来自location表
	private String location_barcode;
	
	private String barcode;
	private String product_name;
	private String sku_code;
	
	private String product_type;
	
	// 库位库存效期状态
	private String validity_status = Validity_STATUS_NORMAL;
	
	private String batch_sn="";
	
	private Integer warehouse_id ;
	
	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getLocation_type() {
		return location_type;
	}

	public void setLocation_type(String location_type) {
		this.location_type = location_type;
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



	public int getPl_id() {
		return pl_id;
	}

	public void setPl_id(int pl_id) {
		this.pl_id = pl_id;
	}

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public int getLocation_id() {
		return location_id;
	}

	public void setLocation_id(int location_id) {
		this.location_id = location_id;
	}

	public int getQty_total() {
		return qty_total;
	}

	public void setQty_total(int qty_total) {
		this.qty_total = qty_total;
	}

	public int getQty_reserved() {
		return qty_reserved;
	}

	public void setQty_reserved(int qty_reserved) {
		this.qty_reserved = qty_reserved;
	}

	public int getQty_available() {
		return qty_available;
	}

	public void setQty_available(int qty_available) {
		this.qty_available = qty_available;
	}

	public int getQty_freeze() {
		return qty_freeze;
	}

	public void setQty_freeze(int qty_freeze) {
		this.qty_freeze = qty_freeze;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProduct_location_status() {
		return product_location_status;
	}

	public void setProduct_location_status(String product_location_status) {
		this.product_location_status = product_location_status;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getSerial_number() {
		return serial_number;
	}

	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}

	public String getCreated_user() {
		return created_user;
	}

	public void setCreated_user(String created_user) {
		this.created_user = created_user;
	}

	public String getCreated_time() {
		return created_time;
	}

	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}

	public String getLast_updated_user() {
		return last_updated_user;
	}

	public void setLast_updated_user(String last_updated_user) {
		this.last_updated_user = last_updated_user;
	}

	public String getLast_updated_time() {
		return last_updated_time;
	}

	public void setLast_updated_time(String last_updated_time) {
		this.last_updated_time = last_updated_time;
	}

	
	public String getLocation_barcode() {
		return location_barcode;
	}

	public void setLocation_barcode(String location_barcode) {
		this.location_barcode = location_barcode;
	}

	public String getProduct_type() {
		return product_type;
	}

	public void setProduct_type(String product_type) {
		this.product_type = product_type;
	}

	public String getValidity_status() {
		return validity_status;
	}

	public void setValidity_status(String validity_status) {
		this.validity_status = validity_status;
	}

	public String getBatch_sn() {
		return batch_sn;
	}

	public void setBatch_sn(String batch_sn) {
		this.batch_sn = batch_sn;
	}
	
	public Integer getWarehouse_id() {
		return warehouse_id;
	}

	public void setWarehouse_id(Integer warehouse_id) {
		this.warehouse_id = warehouse_id;
	}

	//生产日期排序   数量从大道小
	//Collections.sort(productlocationPieceList);
	@Override
	public int compareTo(ProductLocation o) {
		if (this.getValidity().compareTo(o.getValidity()) != 0) {
			return this.getValidity().compareTo(o.getValidity());
		} else {
			if (this.getQty_available() > o.getQty_available()) {
				return -1;
			} else if (this.getQty_available() < o.getQty_available()) {
				return 1;
			}

			return 0;
		}
	}

	//生产日期排序   数量从小到大
	//ProductLocation plComparator=new ProductLocation();
	//Collections.sort(productlocationPieceList,plComparator);
	@Override
	public int compare(ProductLocation o1, ProductLocation o2) {
		if (o1.getValidity().compareTo(o2.getValidity()) != 0) {
			return o1.getValidity().compareTo(o2.getValidity());
		} else {
			if (o1.getQty_available() > o2.getQty_available()) {
				return 1;
			} else if (o1.getQty_available() < o2.getQty_available()) {
				return -1;
			}

			return 0;
		}
	}

	
	/**
	 * 根据生产日期，保质期，临期预警天数，禁发效期天数判断效期状态
	 * @author hzhang1
	 * @param validity
	 * @param validityDays
	 * @param validityUnit
	 * @param warrantyWarningDays
	 * @param warrantyUnsalableDays
	 * @return
	 */
	public static String checkValidityStatus(String validity,
			Integer validityDays, String validityUnit,
			Integer warrantyWarningDays, Integer warrantyUnsalableDays) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Long daysVariance = 0L;
		String validityStatus = "";
		
		warrantyWarningDays = WorkerUtil.isNullOrEmpty(warrantyWarningDays) ? 0
				: warrantyWarningDays;
		warrantyUnsalableDays = WorkerUtil.isNullOrEmpty(warrantyUnsalableDays) ? 0
				: warrantyUnsalableDays;
		
		// 1.如果效期天数小于等于0，直接返回NROMAL
		if (validityDays <= 0)
			return Validity_STATUS_NORMAL;
		
		try {
			// 2.生产日期 + 保质期计算出Day1
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(validity));
			if ("MONTH".equals(validityUnit)) {
				cal.add(Calendar.MONTH, validityDays);
			} else if ("DAY".equals(validityUnit)) {
				cal.add(Calendar.DATE, validityDays);
			} else
				return Validity_STATUS_NORMAL;
			
			// 3.获得当前时间Day2
			String day2 = sdf.format(new Date());
			
			// 4.得到Day1与Day2的天数差daysVariance
			daysVariance = (cal.getTime().getTime() - sdf.parse(day2).getTime()) / (24 * 60 * 60 * 1000);
		} catch (ParseException e) {
			e.printStackTrace();
			return validityStatus;
		}

		
		/** 5.对天数差daysVariance判断
		 *  如果daysVariance小于0，则商品已过保质期，返回EXPIRED状态；
		 *  如果daysVariance大于临期预警天数，则商品正常，返回NORMAL状态；
		 *  如果daysVariance介于临期预警天数和禁发效期，则需临期预警，返回WARNING状态；
		 *  如果daysVariance小于禁发效期，则为禁发状态，返回UNSALABLE状态
		 **/
		if (daysVariance < 0) {
			validityStatus = Validity_STATUS_EXPIRED;
		} else {
			if (daysVariance <= warrantyUnsalableDays)
				validityStatus = Validity_STATUS_UNSALABLE;
			else if (daysVariance <= warrantyWarningDays
					&& daysVariance > warrantyUnsalableDays)
				validityStatus = Validity_STATUS_WARNING;
			else if (daysVariance > warrantyWarningDays)
				validityStatus = Validity_STATUS_NORMAL;
		}
		
		// 6.返回库位库存效期状态
		return validityStatus == "" ? Validity_STATUS_NORMAL : validityStatus;
	}
}
