package com.xc.admin.control.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.xc.util.CacheFactory;
import com.xc.util.CommonUtil;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.xc.control.BaseControl;
import com.xc.pojo.user.User;
import com.xc.service.user.UserService;
import com.xc.util.LoginUserHolder;
import com.xc.vo.BaseModelVo.Code;
import com.xc.vo.ModelVo;

import java.util.Date;

@RestController
@RequestMapping(value = "/user")
public class UserControl extends BaseControl<UserService, User, String>{

	@Reference
	private UserService userService;
	/**
	 * 用户列表
	 * @param request
	 * @param response
	 * @param pageVo
	 * @return
	 */
	@PostMapping(value = "/getPage")
	@ResponseBody
	public ModelVo getUsers(HttpServletRequest request,HttpServletResponse response,
			@RequestBody ModelVo pageVo) {
		ModelVo modelVo = new ModelVo();
		System.out.println(LoginUserHolder.getLoginUser().getId());
		try {
			modelVo = userService.getUsers(pageVo,LoginUserHolder.getLoginUser().getId());
			modelVo.setCodeEnum(Code.SUCCESS);

		} catch (Exception e) {
			e.printStackTrace();
			modelVo.setCode(Code.ERROR);
			modelVo.setMessage("error:"+e.getMessage());
		}

		return modelVo;
	}



	/**
	 * 修改和新增用户
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveUser")
	@ResponseBody
	public ModelVo saveUser(@RequestBody User user){
        ModelVo modelVo = new ModelVo();
		try {
			modelVo = userService.saveUser(user,LoginUserHolder.getLoginUser().getId());
		} catch (Exception e) {
			e.printStackTrace();
			modelVo.setCode(Code.ERROR);
			modelVo.setMessage("error:"+e.getMessage());
		}
		return modelVo;
	}


	@RequestMapping(value = "/login")
	@ResponseBody
	public ModelVo login(String userName,String password) {
		return userService.login(userName, password, 0,"web");
	}

	@RequestMapping(value = "/loginMobile")
	@ResponseBody
	public ModelVo loginMobile(String mobile,String code) {
		ModelVo vo = checkCode(code,mobile,"login");

		if(vo.getCode()==Code.SUCCESS.getCode()) {
			vo = userService.login(mobile, null, 1,"web");
		}

		return vo;
	}


	private ModelVo checkCode(String code,String codeId,String type) {
		JSONObject object =	CacheFactory.getInstance().getCodeCache().get(codeId);
		ModelVo vo = new ModelVo();

		if(object!=null) {
			if(type.equals(object.getString("type"))){
				if(CommonUtil.isNotNull(code)){
					if(code.equals(object.getString("code"))) {
						vo.setCodeEnum(Code.SUCCESS);
					}else{
						vo.setCodeEnum(Code.ERROR,"请输入正确的验证码");
					}
				}else{
					vo.setCodeEnum(Code.ERROR,"请输入验证码");
				}


			}else {
				vo.setCodeEnum(Code.ERROR,type+"验证不存在,请重新获取验证码");
			}

		}else {
			vo.setCodeEnum(Code.ERROR,"验证不存在,请重新获取验证码");
		}

		return vo;
	}

	@RequestMapping(value = "/regist")
	@ResponseBody
	public ModelVo regist(String mobile,String password,String nickName,Integer source,String code) {
		ModelVo vo = checkCode(code,mobile,"regist");

		if(vo.getCode()==Code.SUCCESS.getCode()) {
			User user = new User();
			user.setUserName(mobile);
			user.setMobile(mobile);
			user.setPassword(password);
			user.setNickName(nickName);
			user.setValid(true);
			user.setSource(source==null?0:source);
			vo = userService.saveUser(user,null);
		}

		return vo;
	}

	@RequestMapping(value = "/getCode")
	@ResponseBody
	public ModelVo getCode(String mobile,String type,HttpServletRequest request) {
		ModelVo vo = new ModelVo();
		if(CommonUtil.isNotNull(type)) {
			JSONObject object = new JSONObject();
			object.put("type",type);
			String code = CommonUtil.randomInt(4);
			object.put("code",code);
			object.put("time",new Date().getTime());
			CacheFactory.getInstance().getCodeCache().put(mobile,object);

			vo.setCode(Code.SUCCESS);
			vo.getResult().put("code",code);
		}else {
			vo.setCodeEnum(Code.ERROR,"类型不能为空");
		}

		return vo;
	}

	@RequestMapping(value = "/getLoginUser")
	@ResponseBody
	public ModelVo getLoginUser(){
		User user = userService.findOne(LoginUserHolder.getLoginUser().getId());
		user.setPassword(null);
		ModelVo vo = new ModelVo();
		vo.setCodeEnum(Code.SUCCESS);
		vo.getResult().put("obj",user);
		return vo;
	}

	@Override
	public UserService getBaseService() {
		return this.userService;
	}
}
