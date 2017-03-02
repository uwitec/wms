package com.leqee.wms.api;

public abstract interface LeqeeParser<T extends LeqeeResponse>
{
  public abstract T parse(String paramString)
    throws ApiException;
  
  public abstract Class<T> getResponseClass()
    throws ApiException;
}
