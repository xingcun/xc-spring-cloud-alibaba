package com.xc.vo;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 接口转换类
 *
 */
public class BaseModelVo implements Serializable {

	/**
	 * 成功／失败代码
	 */
	private int code;

	/**
	 * 成功／失败代码说明
	 */
	private String message;

	/**
	 * 接口返回内容
	 */
	private JSONObject result = new JSONObject();

	@JSONField(serialize=false)
	private JSONObject input = new JSONObject();

	@JSONField(serialize=false)
	private List<String> orderBys;
	/**
	 * 以下三个值,为分页查看数据的时候使用
	 * pageNo,pageSize,total
	 */
	/**
	 * 当前页
	 */
	private Integer pageNo;

	/**
	 * 每页多少数
	 */
	private Integer pageSize;


	/**
	 * 总条数
	 */
	private Long total;

	/**
	 * 总页数
	 */
	private Integer totalPage;


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}

	public BaseModelVo(){
	}

	public JSONObject getResult() {
		return result;
	}


	public void setResult(JSONObject result) {
		this.result = result;
	}


	public Integer getPageNo() {
		return pageNo;
	}


	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}


	public Integer getPageSize() {
		return pageSize;
	}


	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}


	public Long getTotal() {
		return total;
	}


	public void setTotal(long total) {
		this.total = total;
		if(total>0 && pageSize!=null && pageSize.intValue()>0){
			totalPage = (int) (total/pageSize+(total%pageSize==0?0:1));
		}
	}


	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setCode(Code code) {
		this.code = code.getCode();
	}

	public void setCodeEnum(Code code) {
		this.code = code.code;
		this.message = code.message;

	}

	public void setCodeEnum(Code code,String message) {
		this.code = code.code;
		this.message = message;
	}

	public Integer getTotalPage() {
		return totalPage;
	}


	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}


	public JSONObject getInput() {
		return input;
	}


	public void setInput(JSONObject input) {
		this.input = input;
	}


	public enum Code {
		SUCCESS(1,"成功"),
		INIT(0,"初始化"),
		ERROR(-1,"系统异常"),
		LOGINERROR(-2,"登录失败"),
		RESULT_ERROR(-3,"返回结果不符合要求"),
		PARAMETER_ERROR(-400,"入参错误"),
		SUPER_EXCEPTION(-99,"超级异常")
		,USER_NO_LOGIN(99,"用户未登录")
		,WAITE_RESULT(10000,"等待结果")
		,FORBIDDEN(403,"没权限")
		,NO_FOUND(404,"无法找到接口")
		/**
		 * 以100开头的，属于任务执行返回执行的操作
		 */
		,UPLOAD_FILE(101,"需要上传文件")
		,SAVE_BUT_ERROR(102,"保存成功但核保失败")
		,ALREAD_RENEWAL(110,"已经投保")

		,NEED_V_USER(-201,"需要绑定V盟帐号")

		;


	    private int code;

	    private String message;

	    private Code(int code,String message){
	    	this.code=code;
	    	this.message = message;
	    }


	    public int getCode(){
	    	return code;
	    }

	}


	public List<String> getOrderBys() {
		return orderBys;
	}


	public void setOrderBys(List<String> orderBys) {
		this.orderBys = orderBys;
	}



}
