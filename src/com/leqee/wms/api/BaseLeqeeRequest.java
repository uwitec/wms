package com.leqee.wms.api;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.leqee.wms.api.util.LeqeeHashMap;

public abstract class BaseLeqeeRequest<T extends LeqeeResponse>
implements LeqeeRequest<T>
{

	public static final String METHOD_SYNC_SALE_ORDER_REQUEST = "sync.sale.order.request";
	public static final String METHOD_SYNC_RMA_ORDER_REQUEST = "sync.rma.order.request";
	public static final String METHOD_SYNC_PURCHASE_ORDER_REQUEST = "sync.purchase.order.request";
	public static final String METHOD_SYNC_VARIANCE_ORDER_REQUEST = "sync.variance.order.request";
	public static final String METHOD_GET_PURCHASE_ORDER_REQUEST = "get.purchase.order.request";
	public static final String METHOD_SYNC_PRODUCT_REQUEST = "sync.product.request";
	public static final String METHOD_GET_VARIANCE_ORDER_REQUEST = "get.variance.order.request";
	public static final String METHOD_GET_SALE_ORDER_REQUEST = "get.sale.order.request";
	public static final String METHOD_GET_RMA_ORDER_REQUEST = "get.rma.order.request";
	public static final String METHOD_GET_INVENTORY_REQUEST = "get.inventory.request";
	public static final String METHOD_GET_FROZEN_REQUEST = "get.frozen.request";
	public static final String METHOD_GET_ORDER_LIST_REQUEST = "get.order.list.request";
	public static final String METHOD_GET_ORDER_SHIPMENT_LIST_REQUEST = "get.order.shipment.list.request";
	public static final String METHOD_GET_VARIANCE_IMPROVE_TASK_LIST_REQUEST = "get.variance.improve.task.list.request";

	public static final String METHOD_CANCEL_ORDER_REQUEST = "cancel.order.request";
	public static final String METHOD_ADJUST_PRICE_REQUEST = "adjust.price.request";
	public static final String METHOD_SYNC_REGION_REQUEST = "sync.region.request";
	public static final String METHOD_SYNC_SHOP_REQUEST = "sync.shop.request";
	public static final String METHOD_TERMINAL_ORDER_REQUEST="terminal.order.request";
	public static final String METHOD_CANCEL_ORDERPREPACK_REQUEST="cancel.orderprepack.request";
	public static final String METHOD_SYNC_ORDERPREPACK_REQUEST="sync.orderpack.request";
	public static final String METHOD_GET_ORDERPREPACK_REQUEST="get.orderprepack.request";


	
protected Map<String, String> headerMap;
protected LeqeeHashMap udfParams;
protected Long timestamp;
protected String targetAppKey;

@JsonIgnore
protected String topMixParams;

public void putOtherTextParam(String key, String value)
{
  if (this.udfParams == null) {
    this.udfParams = new LeqeeHashMap();
  }
  this.udfParams.put(key, value);
}

public Map<String, String> getHeaderMap()
{
  if (this.headerMap == null) {
    this.headerMap = new LeqeeHashMap();
  }
  return this.headerMap;
}

public void setHeaderMap(Map<String, String> headerMap)
{
  this.headerMap = headerMap;
}

public void addHeaderMap(String key, String value)
{
  getHeaderMap().put(key, value);
}

public Long getTimestamp()
{
  return this.timestamp;
}

public void setTimestamp(Long timestamp)
{
  this.timestamp = timestamp;
}

public String getTargetAppKey()
{
  return this.targetAppKey;
}

public void setTargetAppKey(String targetAppKey)
{
  this.targetAppKey = targetAppKey;
}

public String getTopMixParams()
{
  return this.topMixParams;
}

public void setTopMixParams(String topMixParams)
{
  this.topMixParams = topMixParams;
}
}
