package com.xc.control;

import com.xc.pojo.quartz.XcQuartzEntity;
import com.xc.service.quartz.BaseQuartzJob;
import com.xc.util.CommonUtil;
import com.xc.util.SpringUtils;
import com.xc.vo.BaseModelVo;
import com.xc.vo.ModelVo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

public class BaseQuartzControl {

    @RequestMapping(value = "/xc/executorQuartz")
    @ResponseBody
    public ModelVo executorQuartz(@RequestBody XcQuartzEntity obj) {
        ModelVo modelVo = new ModelVo();

        if(CommonUtil.isNotNull(obj.getRunJobClass())){
            try{
             Class cl  = Class.forName(obj.getRunJobClass());
             BaseQuartzJob  quartz = (BaseQuartzJob) SpringUtils.getBean(cl);
             boolean flag = quartz.executorQuartz(obj);
             modelVo.setCodeEnum(flag? BaseModelVo.Code.SUCCESS:BaseModelVo.Code.ERROR);
            }catch (Exception e){
                e.printStackTrace();
                modelVo.setCodeEnum(BaseModelVo.Code.ERROR,"获取实现类不存在");
            }
        }

        return modelVo;
    }
}
