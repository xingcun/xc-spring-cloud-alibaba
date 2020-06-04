package com.ht.project.pojo;

import com.ht.project.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "com_user")
public class User  implements Serializable {

    /**
     *
     */
    @Column(name = "id",table = "com_user",columnDefinition = "")
    @Id
    private Integer id;

    /**
     * 用户编号
     */

    @Column(name = "user_number",table = "com_user",columnDefinition = "用户编号")
    private String userNumber;

    /**
     * 登陆账号
     */

    @Column(name = "phone",table = "com_user",columnDefinition = "登陆账号")
    private String phone;

    /**
     * 登陆密码
     */

    @Column(name = "password",table = "com_user",columnDefinition = "登陆密码")
    private String password;

    /**
     * 用户昵称
     */

    @Column(name = "nick_name",table = "com_user",columnDefinition = "用户昵称")
    private String nickName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
