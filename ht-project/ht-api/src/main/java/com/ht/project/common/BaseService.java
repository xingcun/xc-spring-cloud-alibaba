package com.ht.project.common;

import com.github.pagehelper.PageInfo;

import java.io.Serializable;
import java.util.List;

public interface BaseService<M, ID extends Serializable> extends Serializable {

    public <P> PageInfo<P>  getPageInfo(HtPage page, Class<P> pClass) ;

    public <P> List<P>  getAllInfo(P p) ;

    public M selectOne(M entity);


    public M selectById(ID id);


    public List<M> selectList(M entity);


    public List<M> selectListAll();


    public Long selectCount(M entity);


    public JsonResult insert(M entity);


    public JsonResult insertSelective(M entity);




    public void deleteById(ID id,String userId);


    public void updateById(M entity);


    public void updateSelectiveById(M entity);

    public JsonResult saveObj(M entity);

    public List<M> selectByExample(Object example);

    public int selectCountByExample(Object example);

}
