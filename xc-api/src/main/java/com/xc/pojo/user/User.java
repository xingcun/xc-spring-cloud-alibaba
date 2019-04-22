package com.xc.pojo.user;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Table;
import org.hibernate.annotations.Type;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.xc.pojo.BaseEntity;
import com.xc.util.JsonbType;

/**
 * 用户基本信息
 *
 */
@Entity
//@Table(appliesTo = "base_user",comment="用户基本信息")
@javax.persistence.Table(name="base_user")
public class User extends BaseEntity<String> {

	/**
	 *
	 */
	private static final long serialVersionUID = 2648791661461435852L;

	@Id
	@Column(unique=true,nullable=false)
	private String id;

	/**
	 * 用户名
	 */
	@Column(unique=true,nullable=false)
	private String username;

	/**
	 * 密码
	 */

	@JSONField(serialize = false)
	private String password;

	/**
	 * 昵称
	 */
	@Column(name = "nick_name")
	private String nickName;


	/**
	 * 手机
	 */
	@Column(unique=true,nullable=false)
	private String mobile;

	/**
	 * 用户来源：1用户注册,2后台创建
	 */
	private int source;


	/**
	 * 关联顶级机构
	 */
	@Column(name = "org_id")
	private Long orgId;

	/**
	 * 绑定的机构用户
	 */
	@Column(name = "org_ids")
	@Type(type = "jsonb",parameters={@Parameter(name = JsonbType.ARRAY_CLASS, value = "java.util.ArrayList"),@Parameter(name = JsonbType.CLASS, value = "java.lang.Long") })
	private List<Long> orgIds;
	/**
	 * 推荐人用户ID
	 */
	@Column(name = "parent_id")
	private Long parentId;

	/**
	 * 权限的类型，用于区别权限的范围
	 * UserAuthType
	 */
	private String authType;


	/**
	 * 头像URL
	 */
	private String avatar;
	

	/**
	 * 用于存放user的其它额外属性，如V盟的帐号密码
	 */
	@Type(type = "jsonb",parameters={@Parameter(name = JsonbType.CLASS, value = "com.alibaba.fastjson.JSONObject") })
	private JSONObject attrs;
	

	@Override
	public String getId() {
		return id;
	}
	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public String getPassword() {
		return this.password;
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return true;
	}


	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}



	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public List<Long> getOrgIds() {
		return orgIds;
	}

	public void setOrgIds(List<Long> orgIds) {
		this.orgIds = orgIds;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}


	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	
	public JSONObject getAttrs() {
		return attrs;
	}

	public void setAttrs(JSONObject attrs) {
		this.attrs = attrs;
	}

	
}