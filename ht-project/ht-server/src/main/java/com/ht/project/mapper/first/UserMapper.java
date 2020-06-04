package com.ht.project.mapper.first;

import com.ht.project.mapper.BaseMapper;
import com.ht.project.pojo.User;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    List<User> getPageInfo(User user);
}
