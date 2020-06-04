package com.ht.project.mapper;

import com.alibaba.fastjson.JSON;
import com.ht.project.mapper.first.UserMapper;
import com.ht.project.mapper.second.SecondUserMapper;
import com.ht.project.pojo.User;
import com.ht.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SecondUserServiceImpl {



    @Autowired
    private SecondUserMapper secondUserMapper;


    @Transactional(transactionManager = "secondTransactionManager")
    public void saveUser() {
        User query = new User();
        query.setPhone("13450351838");


        List<User>  userList = secondUserMapper.getPageInfo(query);

        if(userList.size()>0) {
            User user = userList.get(0);
            user.setNickName("名"+ UUID.randomUUID().toString().substring(0,4));
            secondUserMapper.updateByPrimaryKey(user);
        }


    }


    public List<User> getUser(String phone) {
        User query = new User();
        query.setPhone(phone);

        System.out.println(JSON.toJSONString(secondUserMapper.select(query)));
        System.out.println(JSON.toJSONString(secondUserMapper.getPageInfo(query)));



        return null;
    }
}
