package com.ht.project.mapper.sharding;

import com.ht.project.mapper.BaseMapper;
import com.ht.project.pojo.User;

import java.util.List;

public interface ShardingUserMapper extends BaseMapper<User> {
    List<User> getPageInfo(User user);
}
