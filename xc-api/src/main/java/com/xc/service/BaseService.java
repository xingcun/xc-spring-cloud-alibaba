package com.xc.service;

import java.io.Serializable;

import com.xc.pojo.BaseEntity;
import com.xc.vo.ModelVo;
/**
 * 
 * @author Administrator
 * @version 2.0
 * @param <M>
 * @param <ID>
 */
public interface BaseService<M extends BaseEntity<ID>, ID extends Serializable> extends Serializable {
    /**
     * 保存单个实体
     *
     * @param m 实体
     * @return 返回保存的实体
     */
    public boolean save(M m);
    
    public M saveObj(M m);

    /**
     * 更新单个实体
     *
     * @param m 实体
     * @return 返回更新的实体
     */
    public boolean update(M m);

    /**
     * 根据主键删除相应实体
     *
     * @param id 主键
     */
    public boolean delete(ID id);

    /**
     * 逻辑删除实体
     *
     * @param id 主键
     */
    public ModelVo deleteStatus(ID id,String userId);
    /**
     * 删除实体
     *
     * @param m 实体
     */
    public void delete(M m);



    /**
     * 按照主键查询
     *
     * @param id 主键
     * @return 返回id对应的实体
     */
    public M findOne(ID id);

    /**
     * 实体是否存在
     *
     * @param id 主键
     * @return 存在 返回true，否则false
     */
    public boolean exists(ID id);

    /**
     * 统计实体总数
     *
     * @return 实体总数
     */
    public long count();
    

    public ModelVo getObject(ID id);
    
    
	public ModelVo saveObject(M obj,String userId,String...filters);
	

}
