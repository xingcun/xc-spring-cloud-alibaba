package com.xc.service.quartz;

import com.xc.pojo.quartz.XcQuartzEntity;
import com.xc.vo.ModelVo;

public interface XcQuartzEntityService  {

    public ModelVo getPageResult(ModelVo vo);

    public ModelVo getObject(String id) ;

    public ModelVo saveObject(XcQuartzEntity obj, String userId, String... filters);

    public ModelVo deleteObject(String id,String userId) ;
}
