package com.leqee.wms.response;

import java.io.Serializable;

/**
 * 通过webservice返回类（适用于返回为void的webservice方法改造）
 * subCode和subMsg依据业务逻辑是否复杂给予返回，一般情况下返回code和msg就足够了
 * subCode和subMsg的值和类型在Response的各个子类中定义（msg也可以在其子类中定义）
 * @author qyyao
 *
 */
public  class Response implements Serializable {


	
	
	
	// code类型
	public static final String OK = "00000";
	public static final String ERROR = "00001";
	public static final String EXCEPTION = "00002";
	
	// (API)result类型
	public static final String SUCCESS = "success";
	public static final String FAILURE = "failure";
	
	
	// Response的子类类型
	public static final int  RS_TYPE_SYNC_TAOBAO_ORDER = 0;
	public static final int  RS_TYPE_SYNC_TAOBAO_FENXIAO_ORDER = 1;
	
	
	// msg类型
	public static final String EXC_MSG_API = "platform api exception";  //平台API异常
	public static final String EXC_MSG_SQL = "system sql exception";  //数据库异常
	public static final String EXC_MSG_RUNTIME = "jvm runtime exception";  //运行时异常
	public static final String EXC_MSG_UNKNOWN = "jvm unknow exception";  //未知异常
	
	
	protected String code;  //返回代码
	protected String msg;   //返回信息
	protected String subCode;  //返回代码
	protected String subMsg;   //返回详情信息
	
	
	public Response(){
		super();
	}
	
	public Response(String code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}


	public Response(String code, String msg, String subMsg) {
		super();
		this.code = code;
		this.msg = msg;
		this.subMsg = subMsg;
	}

	public Response(String code, String msg, String subCode, String subMsg) {
		this.code = code;
		this.msg = msg;
		this.subCode = subCode;
		this.subMsg = subMsg;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSubMsg() {
		return subMsg;
	}

	public void setSubMsg(String subMsg) {
		this.subMsg = subMsg;
	}

	public String getSubCode() {
		return subCode;
	}

	public void setSubCode(String subCode) {
		this.subCode = subCode;
	}
	
	@Override
	public String toString() {
		return "Response [code=" + code + ", msg=" + msg + ", subCode="
				+ subCode + ", subMsg=" + subMsg + "]";
	}
	
	
}
