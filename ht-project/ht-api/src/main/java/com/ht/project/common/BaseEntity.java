package com.ht.project.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.ht.project.util.CommonUtil;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.UUID;

/**
 * @param <ID>
 */
@MappedSuperclass
public abstract class BaseEntity<ID extends Serializable> implements Serializable {

	@Column(name = "create_time")
	private Long createTime;

	@Column(name = "update_time")
	private Long updateTime;

	@Column(name = "create_user_id")
	private String createUserId;

	@Column(name = "update_user_id")
	private String updateUserId;

	@JSONField(serialize=false) 
	@Column(name = "delete_status")
	private Boolean deleteStatus;

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public String getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	public Boolean getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(Boolean deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public void initDate(){
		if(getId()==null ){
			Type t = this.getClass().getGenericSuperclass();
			if (((ParameterizedType) t).getActualTypeArguments()[0].getTypeName().equals(String.class.getTypeName())) {
				setId((ID) UUID.randomUUID().toString().replaceAll("-",""));
			}
		}
		setCreateTime(CommonUtil.getLongTimeStamp());
		setUpdateTime(getCreateTime());
		this.setDeleteStatus(false);
	}
	
	public void initUser(String id){
		setCreateUserId(id);
		setUpdateUserId(id);
	}

	public void updateUser(String id){
		if(CommonUtil.isNotNull(id)){
			setUpdateUserId(id);
		}else{
			this.initUser(id);
		}

	}
	
	public void setUpdateTime(){
		setUpdateTime(CommonUtil.getLongTimeStamp());
	}

	public abstract ID getId();

	/**
	 * Sets the id of the entity.
	 *
	 * @param id
	 *            the id to set
	 */
	public abstract void setId(final ID id);

}
