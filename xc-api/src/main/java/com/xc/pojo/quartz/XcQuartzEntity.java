package com.xc.pojo.quartz;

import com.alibaba.fastjson.JSONObject;
import com.xc.pojo.BaseEntity;
import com.xc.util.PgSqlJsonbType;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@javax.persistence.Table(name="xc_quartz_entity")
public class XcQuartzEntity  extends BaseEntity<String> {

    @Id
    @Column(unique=true,nullable=false)
    private String id;
    @Column(name = "name",nullable = false)
    private String name;

    /**
     * 任务描述
     */
    private String description;
    /**
     *当起动时间存在时，优先以时间为准，cron去除使用
     */
    private Date startDate;

    private String cron;

    @Column(name = "is_local_project",nullable = false)
    private Boolean isLocalProject;

    @Column(name = "run_job_class",nullable = false)
    private String runJobClass;

    /**
     * 通过url地址为非本地项目的，进行调度任务处理
     */
    private String url;

    /**
     * 运行时可用参数
     */
    // TODO mysql8使用
//	@Type(type = "json",parameters={@Parameter(name = MySqlJsonType.CLASS, value = "com.alibaba.fastjson.JSONObject") })
    // TODO pgsql使用
    @Type(type = "jsonb",parameters={@Parameter(name = PgSqlJsonbType.CLASS, value = "com.alibaba.fastjson.JSONObject") })
    private JSONObject param;


    /**
     * 运行状态1为运行,0为暂停
     */
    private int state;



    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getLocalProject() {
        return isLocalProject;
    }

    public void setLocalProject(Boolean localProject) {
        isLocalProject = localProject;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public JSONObject getParam() {
        return param;
    }

    public void setParam(JSONObject param) {
        this.param = param;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Boolean getIsLocalProject() {
        return isLocalProject;
    }

    public void setIsLocalProject(Boolean localProject) {
        isLocalProject = localProject;
    }

    public String getRunJobClass() {
        return runJobClass;
    }

    public void setRunJobClass(String runJobClass) {
        this.runJobClass = runJobClass;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
