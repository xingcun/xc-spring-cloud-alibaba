package com.xc.base;

import java.util.List;
import java.util.Map;

/**
 * 基础数据层, 具体数据层继承BaseMapper即可
 * @author hyh
 * @version 0.3
 */
public interface BaseMapper<T> {
    int deleteByPrimaryKey(Long id);

    int insert(T record);

    int insertSelective(T record);

    T selectByPrimaryKey(Long id);

    List<T> selectByMap(Map<String, Object> params);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);

    int updateByMap(Map<String, Object> params);
}
