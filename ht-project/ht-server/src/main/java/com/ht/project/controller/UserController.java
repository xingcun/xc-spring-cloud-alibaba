package com.ht.project.controller;

import com.ht.project.common.JsonResult;
import com.ht.project.service.UserService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/getUser")
    public JsonResult getUser(String phone){
        JsonResult result = new JsonResult();
        userService.getUser(phone);
        return result;
    }

    @RequestMapping(value = "/saveUser")

    public JsonResult saveUser(String phone){
        JsonResult result = new JsonResult();
        userService.saveUser();
        return result;
    }
}
