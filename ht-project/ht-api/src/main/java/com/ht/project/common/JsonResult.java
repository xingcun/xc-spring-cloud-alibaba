package com.ht.project.common;

/**
 *
 * JSON模型
 *
 * 用户后台向前台返回的JSON对象
 *
 *
 */
public class JsonResult implements java.io.Serializable {

	private static final long serialVersionUID = -4652918180001959792L;

	private String errorMsg = "";

	private int errorCode = Code.SUCCESS.getCode();

	private Object data = null;

	public String getErrorMsg() {
		return errorMsg;
	}

	public JsonResult setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
		return this;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	private JsonResult setErrorCode(int errorCode,String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
		return this;
	}

	public JsonResult setErrorCode(Code errorCode,String errorMsg) {
		this.errorCode = errorCode.getCode();
		this.errorMsg = errorMsg;
		if(errorMsg==null){
			this.errorMsg = errorCode.getMessage();
		}
		return this;
	}
	public JsonResult setErrorCode(Code errorCode) {

		return setErrorCode(errorCode,null);
	}


	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public JsonResult(){
		this.data = null;
		this.errorMsg = "";
	}

	public JsonResult(Object data){
		this.data = data;
		this.errorMsg = "";
	}

	public JsonResult(Object data, int errorCode, String errorMsg){
		this.data = data;
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}


	public enum Code {
		SUCCESS(1, "成功"),
		ERROR(-1, "系统异常"),
		FLOW_LIMIT(-100, "系统限流,请稍后访问"),

		USER_NO_LOGIN(-999, "用户未登录"),
		SUPER_EXCEPTION(-1000,"服务接口异常");


		private int code;

		private String message;

		private Code(int code,String message){
			this.code=code;
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public int getCode(){
			return code;
		}
	}
}
