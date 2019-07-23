package com.xc.service.impl.quartz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xc.base.RepositoryHelper;
import com.xc.dao.quartz.XcQuartzEntityDao;
import com.xc.pojo.quartz.XcQuartzEntity;
import com.xc.quartz.DynamicJob;
import com.xc.service.quartz.XcQuartzEntityService;
import com.xc.util.CommonUtil;
import com.xc.vo.BaseModelVo;
import com.xc.vo.ModelVo;
import org.apache.dubbo.config.annotation.Service;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;

@Service
@org.springframework.stereotype.Service
@Transactional
public class XcQuartzEntityServiceImpl implements XcQuartzEntityService {
    @Autowired
    private RepositoryHelper repositoryHelper;

    @Autowired
    private XcQuartzEntityDao xcQuartzEntityDao;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private final String jobGroup = "xc";
    @Override
    public ModelVo getPageResult(ModelVo vo) {
        JSONObject input = vo.getInput();
        XcQuartzEntity m = input.toJavaObject(XcQuartzEntity.class);

        Pageable page = vo.getPage();
        if(vo.getOrders()==null || vo.getOrders().isEmpty()) {
            page = vo.getPage(Sort.Direction.DESC, "createTime");
        }
        JSONObject m_obj = (JSONObject) JSON.toJSON(m);
        Page<XcQuartzEntity> pages =  xcQuartzEntityDao.findAll((root, query, cb)->{

            List<Predicate> predicates = new ArrayList();

            Iterator<Map.Entry<String, Object>> keys = m_obj.entrySet().iterator();

            while(keys.hasNext()) {
                Map.Entry<String, Object> entry = keys.next();
                if(input.containsKey(entry.getKey())) {
                    Object obj = entry.getValue();
                    if(obj instanceof String) {
                        predicates.add(cb.like(root.get(entry.getKey()), "%"+obj+"%"));
                    }else {
                        predicates.add(cb.equal(root.get(entry.getKey()), obj));
                    }
                }
            }
            if (input.getDate("_beginTime") != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime"),
                        CommonUtil.formatDateStartPlus(input.getDate("_beginTime"))));
            }

            if (input.getDate("_endTime") != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime"),
                        CommonUtil.formatDatePlus(input.getDate("_endTime"))));
            }

            if(!input.containsKey("deleteStatus")) {
                predicates.add(cb.equal(root.get("deleteStatus"), false));
            }
            query.where(predicates.toArray(new Predicate[predicates.size()]));
            return null;
        }, page);
        ModelVo modelVo = new ModelVo();
        modelVo.setPageObje(pages);
        modelVo.getResult().put("list", pages.getContent());
        modelVo.setCodeEnum(BaseModelVo.Code.SUCCESS);

        return modelVo;
    }

    @Override
    public ModelVo getObject(String id) {
        ModelVo vo = new ModelVo();
        Optional<XcQuartzEntity> m =xcQuartzEntityDao.findById(id);
        XcQuartzEntity obj = m.isPresent() ? m.get() : null;
        if (obj == null) {
            vo.setCodeEnum(BaseModelVo.Code.ERROR, "查找不到数据");
        } else {
            vo.getResult().put("obj", obj);
            vo.setCode(BaseModelVo.Code.SUCCESS);
        }
        return vo;
    }

    @Override
    public ModelVo saveObject(XcQuartzEntity obj, String userId, String... filters) {
        ModelVo vo = new ModelVo();
        vo.setCode(BaseModelVo.Code.SUCCESS);
        XcQuartzEntity dbObj = null;
        if (userId != null && userId.trim().length() == 0) {
            userId = null;
        }
        if (obj.getId() == null) {
            obj.setId( UUID.randomUUID().toString());
            obj.initDate();
            obj.initUser(userId);
            dbObj = obj;

        } else {
            dbObj = xcQuartzEntityDao.findById(obj.getId()).get();
            CommonUtil.mergeObject(obj, dbObj, true, filters);
            dbObj.setUpdateTime();
            dbObj.setUpdateUserId(userId);

        }
        if(dbObj.getIsLocalProject()!=null && dbObj.getIsLocalProject()){
            dbObj.setUrl(null);
        }
        xcQuartzEntityDao.save(dbObj);

        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try{
            JobKey jobKey = JobKey.jobKey(dbObj.getId(),jobGroup);
            JobDetail detail= scheduler.getJobDetail(jobKey);
            if(detail!=null) {
                scheduler.pauseJob(jobKey);
                scheduler.unscheduleJob(TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup()));
                scheduler.deleteJob(jobKey);
            }

        }catch (SchedulerException e){
            e.printStackTrace();
        }

        if(dbObj.getState()==1) {
            try{
                scheduler.scheduleJob(getJobDetail(dbObj),getTrigger(dbObj));
            }catch (SchedulerException e){
                throw  new RuntimeException(e.getMessage());
            }
        }
        vo.getResult().put("id", dbObj.getId());

        return vo;
    }


    private JobDetail getJobDetail(XcQuartzEntity entity) {
        if(entity.getIsLocalProject()!=null && entity.getIsLocalProject()){
            try {
                Class cl  = Class.forName(entity.getRunJobClass());
            } catch (ClassNotFoundException e) {
                throw  new RuntimeException(e.getMessage());
            }
        }else if(!CommonUtil.isNotNull(entity.getUrl())){
            throw  new RuntimeException("非本地任务,url不能为空");
        }
        JobDataMap map = new JobDataMap();
        map.putAll((JSONObject)JSON.toJSON(entity));

        return JobBuilder.newJob(DynamicJob.class)
                .withIdentity(JobKey.jobKey(entity.getId(),jobGroup))
                .withDescription(entity.getDescription())
                .setJobData(map)
                .storeDurably()
                .build();
    }
    //获取Trigger (Job的触发器,执行规则)
    private Trigger getTrigger(XcQuartzEntity entity) {
        TriggerBuilder tb = TriggerBuilder.newTrigger()
                .withIdentity(entity.getId(), jobGroup);
        if(entity.getStartDate()!=null){
            if(entity.getStartDate().before(new Date())){
                throw  new RuntimeException("启动时间不能比当前时间早");
            }
            tb.startAt(entity.getStartDate());
        }else{
            if(CommonUtil.isNotNull(entity.getCron())){
                tb.withSchedule(CronScheduleBuilder.cronSchedule(entity.getCron()));
            }else{
                throw  new RuntimeException("cron不能为空");
            }
        }

        return tb.build();
    }
    @Override
    public ModelVo deleteObject(String id,String userId) {
        ModelVo vo = new ModelVo();
        Optional<XcQuartzEntity> m =xcQuartzEntityDao.findById(id);
        XcQuartzEntity  obj= m.isPresent() ? m.get() : null;
        if (obj != null) {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            try{
                JobKey jobKey = JobKey.jobKey(obj.getId(),jobGroup);
                JobDetail detail= scheduler.getJobDetail(jobKey);
                if(detail!=null) {
                    scheduler.pauseJob(jobKey);
                    scheduler.unscheduleJob(TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup()));
                    scheduler.deleteJob(jobKey);
                }

            }catch (SchedulerException e){
                vo.setCodeEnum(BaseModelVo.Code.ERROR, "删除失败,"+e.getMessage());
               return vo;
            }

            obj.setDeleteStatus(true);
            obj.setUpdateTime();
            obj.setUpdateUserId(userId);
            xcQuartzEntityDao.save(obj);
            vo.setCode(BaseModelVo.Code.SUCCESS);
        } else {
            vo.setCodeEnum(BaseModelVo.Code.ERROR, "删除失败,实体不存在");
        }
        return vo;

    }
}
