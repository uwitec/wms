package com.leqee.wms.response;

public  class ResponseFactory {
	
	
	/** 返回Response */
	public static Response createResponse(   String code, String msg ) {
		return new Response(code, msg);
	}
	public static Response createResponse(   String code, String msg , String subMsg ) {
		return new Response(code, msg, subMsg);
	}
	public static Response createResponse(   String code, String msg , String subCode, String subMsg ) {
		return new Response(code, msg, subCode, subMsg);
	}
	
	
	/** 返回指定type的Response子类 */
	public static Response createResponse(  int type, String code, String msg ) {
		Response  response = null;
		response = getResponseByType(type, code, msg);
		
		return response;
	}
	
	public static Response createResponse(  int type, String code, String msg , String subMsg ) {
		Response  response = null;
		response = getResponseByType(type, code, msg ,subMsg);
		
		return response;
	}
	public static Response createResponse(  int type, String code, String msg , String subCode, String subMsg ) {
		
		return  getResponseByType(type, code, msg , subCode ,subMsg);
	}
	
	/** 返回指定code的Response */
	public static Response createOkResponse(   String msg  ) {
		
		return new Response(Response.OK, msg);
	}
	public static Response createErrorResponse(  String msg  ) {
		return new Response(Response.ERROR, msg);
	}
	public static Response createExceptionResponse(   String msg ) {
		return new Response(Response.EXCEPTION, msg);
	}
	
	
    
	public static Response createOkResponse(    String msg  , String subMsg ) {
		return new Response(Response.OK, msg ,subMsg);
	}
	public static Response createErrorResponse(   String msg , String subMsg  ) {
		return new Response(Response.ERROR, msg ,subMsg);
	}
	public static Response createExceptionResponse(   String msg , String subMsg  ) {
		return new Response(Response.EXCEPTION, msg ,subMsg);
	}
	
	
	public static Response createOkResponse(  String msg  ,String subCode,  String subMsg ) {
		return new Response(Response.OK, msg , subCode , subMsg);
	}
	public static Response createErrorResponse(   String msg , String subCode,  String subMsg  ) {
		return new Response(Response.ERROR, msg , subCode , subMsg);
	}
	public static Response createExceptionResponse(   String msg , String subCode,  String subMsg  ) {
		return new Response(Response.EXCEPTION , msg , subCode , subMsg);
	}
	
	
	
	/** 返回指定type和code的Response */
	public static Response createOkResponse(  int type,  String msg  ) {
		return getResponseByType(type, Response.OK , msg);
	}
	public static Response createErrorResponse(  int type,   String msg  ) {
		return getResponseByType(type, Response.ERROR , msg);
	}
	public static Response createExceptionResponse(  int type, String msg ) {
		return getResponseByType(type, Response.EXCEPTION , msg);
	}
	
	public static Response createOkResponse(  int type,    String msg  , String subMsg ) {
		return getResponseByType(type, Response.OK , msg , subMsg);
	}
	public static Response createErrorResponse(  int type,   String msg , String subMsg  ) {
		return getResponseByType(type, Response.ERROR , msg , subMsg);
	}
	public static Response createExceptionResponse( int type,    String msg , String subMsg  ) {
		return getResponseByType(type, Response.EXCEPTION , msg , subMsg);
	}
	
	
	public static Response createOkResponse(  int type,   String msg  ,String subCode,  String subMsg ) {
		return getResponseByType(type, Response.OK , msg , subCode, subMsg);
	}
	public static Response createErrorResponse( int type,   String msg , String subCode,  String subMsg  ) {
		return getResponseByType(type, Response.ERROR , msg , subCode, subMsg);
	}
	public static Response createExceptionResponse( int type,   String msg , String subCode,  String subMsg  ) {
		return getResponseByType(type, Response.EXCEPTION , msg , subCode, subMsg);
	}
	
	
	

	/** ****************** 辅助方法 *****************/
	
	private static Response getResponseByType(int type, String code, String msg) {
		Response response = null;
		switch (type) {
		case Response.RS_TYPE_SYNC_TAOBAO_ORDER:
//			response = new SyncTaobaoOrderResponse(code, msg);
			break;
		case Response.RS_TYPE_SYNC_TAOBAO_FENXIAO_ORDER:
//			response = new SyncTaobaoFenxiaoOrderResponse(code, msg);
			break;
		default:
			response = null;
			break;
		}
		return response;
	}
    
	private static Response getResponseByType(int type, String code, String msg , String subMsg) {
		Response response = null;
		switch (type) {
		case Response.RS_TYPE_SYNC_TAOBAO_ORDER:
//			response = new SyncTaobaoOrderResponse(code, msg,subMsg);
			break;
		case Response.RS_TYPE_SYNC_TAOBAO_FENXIAO_ORDER:
//			response = new SyncTaobaoFenxiaoOrderResponse(code, msg,subMsg);
			break;
		default:
			response = null;
			break;
		}
		return response;
	}
	
	private static Response getResponseByType(int type, String code, String msg , String subCode , String subMsg) {
		Response response = null;
		switch (type) {
		case Response.RS_TYPE_SYNC_TAOBAO_ORDER:
//			response = new SyncTaobaoOrderResponse(code, msg, subCode, subMsg);
			break;
		case Response.RS_TYPE_SYNC_TAOBAO_FENXIAO_ORDER:
//			response = new SyncTaobaoFenxiaoOrderResponse(code, msg, subCode, subMsg);
			break;
		default:
			response = null;
			break;
		}
		return response;
	}
	

}
