package com.ht.project.mapper.second;

import com.ht.project.mapper.BaseMapper;
import com.ht.project.pojo.User;

import java.util.List;

public interface SecondUserMapper extends BaseMapper<User> {
    List<User> getPageInfo(User user);
}
