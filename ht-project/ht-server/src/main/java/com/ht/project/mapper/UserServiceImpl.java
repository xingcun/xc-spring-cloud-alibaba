package com.ht.project.mapper;

import com.alibaba.fastjson.JSON;
import com.ht.project.mapper.first.UserMapper;
import com.ht.project.mapper.second.SecondUserMapper;
import com.ht.project.pojo.User;
import com.ht.project.service.UserService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SecondUserServiceImpl secondUserService;

    @Override
    @Transactional(transactionManager = "firstTransactionManager")
    @GlobalTransactional
    public void saveUser() {
        User query = new User();
        query.setMobile("13800138000");
        List<User>  users = userMapper.select(query);
        if(users.size()>0) {
            User user = users.get(0);
            user.setNickName("名"+ UUID.randomUUID().toString().substring(0,4));
            userMapper.updateByPrimaryKey(user);
        }

       secondUserService.saveUser();

        int i=1/0;
    }

    @Override
    public List<User> getUser(String phone) {
        User query = new User();
        query.setMobile(phone);
        System.out.println(JSON.toJSONString(userMapper.select(query)));

        System.out.println(JSON.toJSONString(userMapper.getPageInfo(query)));

        secondUserService.getUser(phone);



        return null;
    }
}
