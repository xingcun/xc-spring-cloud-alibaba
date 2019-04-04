package com.xc.service.user;

import com.xc.pojo.user.User;
import com.xc.service.BaseService;
import com.xc.vo.ModelVo;

public interface UserService extends BaseService<User, String> {
	
	public ModelVo getUsers(ModelVo pageVo,String userId);
	
	
	public ModelVo saveUser(User user,String userId,String...filters);
}
