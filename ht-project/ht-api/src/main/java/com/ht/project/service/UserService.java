package com.ht.project.service;


import com.ht.project.pojo.User;

import java.util.List;

public interface UserService   {

    public List<User> getUser(String phone);


    public void saveUser();

    public List<User> getShardingUsers(String phone,Integer source);

    public void saveShardingUser(User user);
}
