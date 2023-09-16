package com.example.gausstest.controller;
import com.example.gausstest.entity.RolDatPrivilege;
import com.example.gausstest.service.RolDatPrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 展示用户数据库权限表
 */
@RestController
public class RolDatPrivilegeController {
    private final RolDatPrivilegeService rolDatPrivilegeService;

    @Autowired
    public RolDatPrivilegeController(RolDatPrivilegeService rolDatPrivilegeService) {
        this.rolDatPrivilegeService = rolDatPrivilegeService;
    }

    @GetMapping("/rol-dat-privileges")
    public List<RolDatPrivilege> getRolDatPrivileges() {
        return rolDatPrivilegeService.getRolDatPrivileges();
    }
}
