package com.xc.admin.job;

import com.xc.pojo.quartz.XcQuartzEntity;
import com.xc.service.quartz.BaseQuartzJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AdminTestJob implements BaseQuartzJob {
    private static final Logger log = LoggerFactory.getLogger(AdminTestJob.class);
    @Override
    public boolean executorQuartz(XcQuartzEntity entity) {
        log.info("执行任务:"+entity.getName());
        return true;
    }
}
