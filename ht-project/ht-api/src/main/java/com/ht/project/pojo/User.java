package com.ht.project.pojo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.ht.project.common.BaseEntity;
import com.ht.project.typehandler.JsonHandler;
import tk.mybatis.mapper.annotation.ColumnType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * 用户基本信息
 *
 */
@Entity
@javax.persistence.Table(name="base_user")
public class User {

    /**
     *
     */
    private static final long serialVersionUID = 2648791661461435852L;

    @Id
    @Column(unique=true,nullable=false)
    private String id;


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
     * 用于存放user的其它额外属性，如V盟的帐号密码
     */
    @ColumnType(typeHandler = JsonHandler.class)
    private JSONObject attrs;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public JSONObject getAttrs() {
        return attrs;
    }

    public void setAttrs(JSONObject attrs) {
        this.attrs = attrs;
    }
}