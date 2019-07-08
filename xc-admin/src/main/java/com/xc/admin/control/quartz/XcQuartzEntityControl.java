package com.xc.admin.control.quartz;

import com.xc.pojo.quartz.XcQuartzEntity;
import com.xc.service.quartz.XcQuartzEntityService;
import com.xc.util.CommonUtil;
import com.xc.util.LoginUserHolder;
import com.xc.vo.BaseModelVo;
import com.xc.vo.ModelVo;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/quartz")
public class XcQuartzEntityControl {

    @Reference
    private XcQuartzEntityService xcQuartzEntityService;

    @RequestMapping(value = "/save")
    @ResponseBody
    public ModelVo save(@RequestBody XcQuartzEntity obj) {
        ModelVo modelVo = new ModelVo();
        modelVo = xcQuartzEntityService.saveObject(obj, obj.getUpdateUserId(),"startDate");
        return modelVo;
    }

    @GetMapping(value = "/get")
    @ResponseBody
    public ModelVo get(String id) {
        ModelVo modelVo = new ModelVo();

        modelVo = xcQuartzEntityService.getObject(id);
        modelVo.setCodeEnum(BaseModelVo.Code.SUCCESS);

        return modelVo;
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public ModelVo delete(String id) {
        ModelVo modelVo = new ModelVo();
        modelVo = xcQuartzEntityService.deleteObject(id, LoginUserHolder.getLoginUser().getId());
        return modelVo;
    }

    @PostMapping(value = "/page")
    @ResponseBody
    public ModelVo getPage(@RequestBody ModelVo pageVo) {
        return xcQuartzEntityService.getPageResult(pageVo);
    }
}
