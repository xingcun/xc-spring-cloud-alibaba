package com.xc.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.domain.Persistable;
import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.xc.util.JsonbType;

/**
 * 
 * @author Administrator
 *
 * @param <ID>
 */
@MappedSuperclass
@TypeDefs({ @TypeDef(name = "jsonb", typeClass = JsonbType.class) })
public abstract class BaseEntity<ID extends Serializable> implements
		Serializable, Persistable<ID> {

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	private Date createTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time")
	private Date updateTime;

	@Column(name = "create_user_id")
	private String createUserId;

	@Column(name = "update_user_id")
	private String updateUserId;

	@JSONField(serialize=false) 
	@Column(name = "delete_status")
	private boolean deleteStatus;

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public boolean isDeleteStatus() {
		return this.deleteStatus;
	}

	public boolean getDeleteStatus() {
		return this.deleteStatus;
	}

	public void setDeleteStatus(boolean deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
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

	public abstract ID getId();

	/**
	 * Sets the id of the entity.
	 *
	 * @param id
	 *            the id to set
	 */
	public abstract void setId(final ID id);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.domain.Persistable#isNew()
	 */
	@JSONField(serialize=false) 
	public boolean isNew() {

		return null == getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!getClass().equals(obj.getClass())) {
			return false;
		}

		BaseEntity that = (BaseEntity) obj;
		return null == this.getId() ? false : this.getId().equals(that.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		int hashCode = 17;

		hashCode += null == getId() ? 0 : getId().hashCode() * 31;

		return hashCode;
	}

	@Override
	public String toString() {
		// return ReflectionToStringBuilder.toString(this);
		return super.toString();
	}

	
	public void initDate(){
		setCreateTime(new Date());
		setUpdateTime(getCreateTime());
	}
	
	public void initUser(String id){
		setCreateUserId(id);
		setUpdateUserId(id);
	}
	
	public void setUpdateTime(){
		setUpdateTime(new Date());
	}
}
