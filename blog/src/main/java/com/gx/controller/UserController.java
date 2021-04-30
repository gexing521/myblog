package com.gx.controller;


import com.gx.common.lang.Result;
import com.gx.entity.User;
import com.gx.service.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 关注公众号：MarkerHub
 * @since 2021-01-01
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @RequiresAuthentication
    @GetMapping("/{id}")
    public Object test(@PathVariable("id") Long id) {
        User user=userService.getById(id);
        return Result.succ(user);
    }
    @GetMapping("/save")
    //@Validated @RequestBody入参校验
    public Object save(@Validated @RequestBody User user) {

        return Result.succ(user);
    }
}
