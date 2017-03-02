package com.leqee.wms.api.util;

import java.util.HashMap;
import java.util.Map;

public class RequestParametersHolder
{
  private String requestUrl;
  private String responseBody;
  private LeqeeHashMap protocalMustParams;
  private LeqeeHashMap protocalOptParams;
  private LeqeeHashMap applicationParams;
  
  public String getRequestUrl()
  {
    return this.requestUrl;
  }
  
  public void setRequestUrl(String requestUrl)
  {
    this.requestUrl = requestUrl;
  }
  
  public String getResponseBody()
  {
    return this.responseBody;
  }
  
  public void setResponseBody(String responseBody)
  {
    this.responseBody = responseBody;
  }
  
  public LeqeeHashMap getProtocalMustParams()
  {
    return this.protocalMustParams;
  }
  
  public void setProtocalMustParams(LeqeeHashMap protocalMustParams)
  {
    this.protocalMustParams = protocalMustParams;
  }
  
  public LeqeeHashMap getProtocalOptParams()
  {
    return this.protocalOptParams;
  }
  
  public void setProtocalOptParams(LeqeeHashMap protocalOptParams)
  {
    this.protocalOptParams = protocalOptParams;
  }
  
  public LeqeeHashMap getApplicationParams()
  {
    return this.applicationParams;
  }
  
  public void setApplicationParams(LeqeeHashMap applicationParams)
  {
    this.applicationParams = applicationParams;
  }
  
  public Map<String, String> getAllParams()
  {
    Map<String, String> params = new HashMap();
    if ((this.protocalMustParams != null) && (!this.protocalMustParams.isEmpty())) {
      params.putAll(this.protocalMustParams);
    }
    if ((this.protocalOptParams != null) && (!this.protocalOptParams.isEmpty())) {
      params.putAll(this.protocalOptParams);
    }
    if ((this.applicationParams != null) && (!this.applicationParams.isEmpty())) {
      params.putAll(this.applicationParams);
    }
    return params;
  }
}

