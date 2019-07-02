package com.xc.service.user;

import com.xc.pojo.user.User;
import com.xc.service.BaseService;
import com.xc.vo.ModelVo;

public interface UserService extends BaseService<User, String> {
	
	public ModelVo getUsers(ModelVo pageVo,String userId);
	
	
	public ModelVo saveUser(User user,String userId,String...filters);

	/**
	 *
	 * @param username
	 * @param password
	 * @param loginType 0,用户密码登录,1手机号验证码登录
	 * @param loginSystem
	 * @return
	 */
	public ModelVo login(String username,String password,int loginType,String loginSystem);
	
	public ModelVo getJwtUserPubKey();
	
}
