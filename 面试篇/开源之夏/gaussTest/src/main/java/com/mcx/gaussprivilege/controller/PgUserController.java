package com.mcx.gaussprivilege.controller;

import com.mcx.gaussprivilege.entity.PgUser;
import com.mcx.gaussprivilege.service.PgUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PgUserController {
    @Autowired
    private PgUserService userService;

    @GetMapping("/pgusers")
    public List<PgUser> getAllUsers() {
        return userService.getAllUsers();
    }
}
