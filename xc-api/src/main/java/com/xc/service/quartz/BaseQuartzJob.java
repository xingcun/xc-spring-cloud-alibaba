package com.xc.service.quartz;

import com.xc.pojo.quartz.XcQuartzEntity;

/**
 * 所有需要运行的job需要实现继承的接口
 */
public interface BaseQuartzJob {

    public boolean executorQuartz(XcQuartzEntity entity);
}
