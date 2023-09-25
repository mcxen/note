package com.mcx.gaussprivilege.controller;

import com.mcx.gaussprivilege.entity.RoleAdmin;
import com.mcx.gaussprivilege.service.CreateRoleAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class CreateRoleAdminController {
    @Autowired
    private final CreateRoleAdminService createRoleAdminService;

    public CreateRoleAdminController(CreateRoleAdminService createRoleAdminService) {
        this.createRoleAdminService = createRoleAdminService;
    }
    /*



     */
    @GetMapping("/creroladmin")
    public List<RoleAdmin> getCreateRoleAdminList(HttpSession httpSession) {
        List<RoleAdmin> roleAdminList = createRoleAdminService.getCreateRoleAdminList();

        return roleAdminList;
    }
    @GetMapping("/creroladminerr")
    public String getCreateRoleAdminErr(){
        List<RoleAdmin> roleAdminList = createRoleAdminService.getCreateRoleAdminList();
        String msg = "";
        msg ="该数据库的安全管理员权限正常";
        for (RoleAdmin admin : roleAdminList) {
            if (!admin.getRolname().equals("omm")&&(admin.isRolauditadmin()|| admin.isRolsuper()|| admin.isRolsystemadmin())){
                msg = "如果三权分立开关已经开启，那么该数据库的安全管理违规持有权限";
            }
        }
        System.out.println("msg = " + msg);
        return msg;
    }




}