package com.xc.control;

import java.io.Serializable;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xc.pojo.BaseEntity;
import com.xc.service.BaseService;
import com.xc.util.LoginUserHolder;
import com.xc.vo.BaseModelVo.Code;
import com.xc.vo.ModelVo;

public abstract class BaseControl<BS extends BaseService, Entity extends BaseEntity, ID extends Serializable> {

	
	private BS baseService;

	@RequestMapping(value = "/save")
	@ResponseBody
	public ModelVo saveUser(@RequestBody Entity obj) {
		ModelVo modelVo = new ModelVo();
		modelVo = getBaseService().saveObject(obj, LoginUserHolder.getLoginUser().getId());
		return modelVo;
	}

	@GetMapping(value = "/get")
	@ResponseBody
	public ModelVo get(ID id) {
		ModelVo modelVo = new ModelVo();

		modelVo = getBaseService().getObject(id);
		modelVo.setCodeEnum(Code.SUCCESS);

		return modelVo;
	}

	@RequestMapping(value = "/delete")
	@ResponseBody
	public ModelVo delete(ID id) {
		ModelVo modelVo = new ModelVo();
		modelVo = getBaseService().deleteStatus(id, LoginUserHolder.getLoginUser().getId());
		return modelVo;
	}

	@PostMapping(value = "/page")
	@ResponseBody
	public ModelVo getUsers(@RequestBody ModelVo pageVo) {
		return getBaseService().getPageResult(pageVo);
	}

	public abstract BS getBaseService();
	


}
