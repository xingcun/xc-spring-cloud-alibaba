package com.xc.vo;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class MessageVo implements Serializable {
    /**
     * 主题，用于区分事件的类型
     */
    private String subject;
    /**
     * 接收的内容
     */
    private JSONObject content;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }
}
