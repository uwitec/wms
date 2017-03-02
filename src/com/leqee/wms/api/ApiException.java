package com.leqee.wms.api;


public class ApiException
  extends Exception
{
  private static final long serialVersionUID = -238091758285157331L;
  private String errCode;
  private String errMsg;
  
  
  // errCode定义
  public static final String ERR_CODE_EMPTY_APP_PARAMS = "EMPTY_APP_PARAMS" ;  //空的应用参数
  public static final String ERR_CODE_EMPTY_REQUEST = "EMPTY_REQUEST" ;  //空的request
  public static final String ERR_CODE_EMPTY_RESPONSE = "EMPTY_RESPONSE" ;  //空的response
  public static final String ERR_CODE_ILLEGAL_REQUEST_PARAMS = "ILLEGAL_REQUEST_PARAMS" ;  //request中含非法的参数
  public static final String ERR_CODE_ILLEGAL_RESPONSE_PARAMS = "ILLEGAL_RESPONSE_PARAMS" ;  //response中含非法的参数
  public static final String ERR_CODE_ILLEGAL_APP_PARAMS = "ILLEGAL_APP_PARAMS" ; //空的应用参数
  
  // errMsg定义
  public static final String ERR_MSG_EMPTY_APP_PARAMS = "there are some empty params in serverUrl, appKey, appSercet" ;  //空的应用参数
  public static final String ERR_MSG_EMPTY_REQUEST = "request is empty" ;  //空的request
  public static final String ERR_MSG_EMPTY_RESPONSE = "response is empty" ;  //空的response
  public static final String ERR_MSG_ILLEGAL_REQUEST_PARAMS = "there are some illegal params in request" ;  //request中含非法的参数
  public static final String ERR_MSG_ILLEGAL_RESPONSE_PARAMS = "there are some illegal params in response" ;  //response中含非法的参数
  public static final String ERR_MSG_ILLEGAL_APP_PARAMS = "illegal app params, appKey、 " ; //空的应用参数
  
  
  public ApiException() {}
  
  public ApiException(String message, Throwable cause)
  {
    super(message, cause);
  }
  
  public ApiException(String message)
  {
    super(message);
  }
  
  public ApiException(Throwable cause)
  {
    super(cause);
  }
  
  public ApiException(String errCode, String errMsg)
  {
    super(errCode + ":" + errMsg);
    this.errCode = errCode;
    this.errMsg = errMsg;
  }
  
  public String getErrCode()
  {
    return this.errCode;
  }
  
  public String getErrMsg()
  {
    return this.errMsg;
  }
}
