package com.example.gausstest.controller;

import com.example.gausstest.entity.PgUser;
import com.example.gausstest.service.PgUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
