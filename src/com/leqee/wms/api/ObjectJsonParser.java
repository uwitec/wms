package com.leqee.wms.api;



public class ObjectJsonParser<T extends LeqeeResponse>
  implements LeqeeParser<T>
{
  private Class<T> clazz;
  private boolean simplify;
  
  public ObjectJsonParser(Class<T> clazz)
  {
    this.clazz = clazz;
  }
  
  public ObjectJsonParser(Class<T> clazz, boolean simplify)
  {
    this.clazz = clazz;
    this.simplify = simplify;
  }
  
  public T parse(String rsp)
    throws ApiException
  {
    return toResponse(rsp, this.clazz);
  }
  
  private T toResponse(String rsp, Class<T> clazz2) {
	// TODO Auto-generated method stub
	return null;
}

public Class<T> getResponseClass()
  {
    return this.clazz;
  }
}
