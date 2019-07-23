package com.xc.quartz;

import com.alibaba.fastjson.JSON;
import com.xc.pojo.quartz.XcQuartzEntity;
import com.xc.service.quartz.BaseQuartzJob;
import com.xc.service.quartz.XcQuartzEntityService;
import com.xc.util.CommonUtil;
import com.xc.util.SpringUtils;
import com.xc.vo.BaseModelVo;
import com.xc.vo.ModelVo;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * :@DisallowConcurrentExecution : 此标记用在实现Job的类上面,意思是不允许并发执行.
 * :注意org.quartz.threadPool.threadCount线程池中线程的数量至少要多个,否则@DisallowConcurrentExecution不生效
 * :假如Job的设置时间间隔为3秒,但Job执行时间是5秒,设置@DisallowConcurrentExecution以后程序会等任务执行完毕以后再去执行,否则会在3秒时再启用新的线程执行
 */
@DisallowConcurrentExecution
@Component
public class DynamicJob implements Job {
    @Autowired
    LoadBalancerClient loadBalancerClient;
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    XcQuartzEntityService xcQuartzEntityService;

    private static final Logger log = LoggerFactory.getLogger(DynamicJob.class);

    @Override
    public void execute(JobExecutionContext executorContext) throws JobExecutionException {
        long startTime = System.currentTimeMillis();
        JobDataMap map = executorContext.getMergedJobDataMap();
        ModelVo modelVo = new ModelVo();
        XcQuartzEntity entity = JSON.parseObject(JSON.toJSONString(map),XcQuartzEntity.class);
        if(entity.getIsLocalProject()!=null && entity.getIsLocalProject()) {
            try {
                Class cl  = Class.forName(entity.getRunJobClass());
                BaseQuartzJob quartz = (BaseQuartzJob) SpringUtils.getBean(cl);
                boolean flag = quartz.executorQuartz(entity);
                modelVo.setCodeEnum(flag? BaseModelVo.Code.SUCCESS:BaseModelVo.Code.ERROR);
            } catch (ClassNotFoundException e) {
               e.printStackTrace();
                modelVo.setCodeEnum(BaseModelVo.Code.ERROR,e.getMessage());
            }


        }else{
            String url = entity.getUrl();
            if(!url.startsWith("http")){
                ServiceInstance serviceInstance = loadBalancerClient.choose(url);
                if(serviceInstance!=null && CommonUtil.isNotNull(serviceInstance.getHost())){
                    url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/xc/executorQuartz";
                }else{
                    modelVo.setCodeEnum(BaseModelVo.Code.ERROR,url+"获取不到可用服务");
                }

            }
            if(modelVo.getCode()!= BaseModelVo.Code.ERROR.getCode()){
                log.info(url);
                modelVo =  restTemplate.postForObject(url, entity,ModelVo.class);
            }
        }
        if(entity.getStartDate()!=null) {
            entity.setState(0);
            xcQuartzEntityService.saveObject(entity,null);
        }
        long endTime = System.currentTimeMillis();
        log.info(JSON.toJSONString(modelVo));
        log.info("Running Job time : {}ms\n ", (endTime - startTime));

    }


}
