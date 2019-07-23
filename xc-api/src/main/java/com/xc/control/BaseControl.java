package com.xc.control;

import com.xc.annotation.LoginUserId;
import com.xc.pojo.BaseEntity;
import com.xc.service.BaseService;
import com.xc.util.LoginUserHolder;
import com.xc.vo.BaseModelVo.Code;
import com.xc.vo.ModelVo;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

public abstract class BaseControl<BS extends BaseService, Entity extends BaseEntity, ID extends Serializable> {


	private BS baseService;

	@RequestMapping(value = "/save")
	@ResponseBody
	public ModelVo save(@RequestBody Entity obj, @LoginUserId String loginUserId) {
		ModelVo modelVo = new ModelVo();
		modelVo = getBaseService().saveObject(obj, loginUserId);
		return modelVo;
	}

	@GetMapping(value = "/get")
	@ResponseBody
	public ModelVo get(ID id) {
		ModelVo modelVo = new ModelVo();

		modelVo = getBaseService().getObject(id);

		return modelVo;
	}

	@RequestMapping(value = "/delete")
	@ResponseBody
	public ModelVo delete(ID id, @LoginUserId String loginUserId) {
		ModelVo modelVo = new ModelVo();
		modelVo = getBaseService().deleteStatus(id,loginUserId);
		return modelVo;
	}

	@PostMapping(value = "/page")
	@ResponseBody
	public ModelVo getPage(@RequestBody ModelVo pageVo) {
		return getBaseService().getPageResult(pageVo);
	}

	public abstract BS getBaseService();



}
