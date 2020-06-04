package com.ht.project.service;


import com.ht.project.pojo.User;

import java.util.List;

public interface UserService   {

    public List<User> getUser(String phone);


    public void saveUser();
}
