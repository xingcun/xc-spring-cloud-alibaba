package com.ht.project.common;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;
import java.util.Map.Entry;

/**
 * 向后台传递参数使用的分页model
 *
 * @author ljl
 *
 */
public class HtPage implements java.io.Serializable {

    private static final long serialVersionUID = -4652918180001959791L;

//    private int page = 1; // 当前页
//    private int rows = 1000; // 每页显示记录数

    private int page=0; // 当前页
    private int pageSize=12; // 每页显示记录数

    private String orderBy; // 排序

    private JSONObject input;

    private Map<String, String> sorters;

    public HtPage() {
    }

    public HtPage(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    private void setOrderBy(Map<String, String> sorters) {
        if (sorters != null && !sorters.isEmpty()) {
            StringBuffer buffer = new StringBuffer();
            int i = 0;
            for (Entry<String, String> entry : sorters.entrySet()) {
                buffer.append(" ").append(entry.getKey()).append(" ").append(entry.getValue());

                if (i < sorters.size() - 1) {
                    buffer.append(",");
                }

                i++;
            }

            orderBy = buffer.toString();
        }
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        if(pageSize>100 && pageSize<=0){
            throw new RuntimeException("分页参数不对");
        }
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Map<String, String> getSorters() {
        return sorters;
    }

    public void setSorters(Map<String, String> sorters) {
        this.sorters = sorters;
        setOrderBy(sorters);
    }

    public JSONObject getInput() {
        return input;
    }

    public void setInput(JSONObject input) {
        this.input = input;
    }
}
