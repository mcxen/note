package com.mcxgroup.testmybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mcxgroup.testmybatisplus.entity.TUser;
import com.mcxgroup.testmybatisplus.service.ITUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author c
 * @since 2023-06-17
 */
@RestController
@RequestMapping("/t-user")
public class TUserController {
    @Autowired
    private ITUserService itUserService;

    @GetMapping("/get")
    public String get(){
        LambdaQueryWrapper<TUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TUser::getId,1);
        TUser one = itUserService.getOne(wrapper);
        return one.toString();
    }
}
