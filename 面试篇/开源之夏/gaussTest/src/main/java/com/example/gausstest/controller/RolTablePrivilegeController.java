package com.example.gausstest.controller;

import com.example.gausstest.entity.RolTablePrivilege;
import com.example.gausstest.service.RolTablePrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RolTablePrivilegeController {
    @Autowired
    RolTablePrivilegeService rolTablePrivilegeService;
    @GetMapping("/rolTablePrivileges")
    public List<RolTablePrivilege> getAllRolTablePrivileges() {
        return rolTablePrivilegeService.getAllRolTablePrivileges();
    }
}
