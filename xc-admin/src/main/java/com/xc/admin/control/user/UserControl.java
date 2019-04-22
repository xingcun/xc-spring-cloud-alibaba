package com.xc.admin.control.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xc.control.BaseControl;
import com.xc.pojo.user.User;
import com.xc.service.user.UserService;
import com.xc.util.LoginUserHolder;
import com.xc.vo.BaseModelVo.Code;
import com.xc.vo.ModelVo;

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
	 * @param reqParam
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveUser")
	@ResponseBody
	@Override
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
	public ModelVo login(String username,String password) {
		return userService.login(username, password, "ADMIN");
	}
	
	@RequestMapping(value = "/regist")
	@ResponseBody
	public ModelVo regist(@RequestBody User user) {
		return userService.regist(user);
	}



	@Override
	public UserService getBaseService() {
		return this.userService;
	}
}
