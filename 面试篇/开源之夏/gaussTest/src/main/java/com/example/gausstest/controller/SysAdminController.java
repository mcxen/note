package com.example.gausstest.controller;

import com.example.gausstest.entity.RoleAdmin;
import com.example.gausstest.service.AuditAdminService;
import com.example.gausstest.service.SysAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class SysAdminController {
    @Autowired
    private SysAdminService sysAdminService;
    /*
    SELECT rolname,rolsuper,rolcreaterole,rolsystemadmin,rolauditadmin FROM pg_roles WHERE rolcreaterole = 'true';，
    执行结果为：[{"rolname":"omm","rolsuper":"t","rolcreaterole":"t","rolsystemadmin":"t","rolauditadmin":"t"},
    {"rolname":"safeadmin","rolsuper":"f","rolcreaterole":"t","rolsystemadmin":"f","rolauditadmin":"f"}]
    。设计SpringBoot的restful接口，使用mybtais负责dao，编写entity层，entity层的名字叫createRoleAdmin，
    编写service层给出controller，编写对应的网页的layui风格的使用这个接口的表格
     */
    @GetMapping("/sysadmin")
    public List<RoleAdmin> getSysAdminList(HttpSession httpSession) {
        return sysAdminService.getSysAdminList();
    }
    @GetMapping("/sysadminerr")
    public String getCreateRoleAdminErr(){
        List<RoleAdmin> roleAdminList = sysAdminService.getSysAdminList();
        String msg = "";
        msg ="该数据库的系统管理员权限正常";
        for (RoleAdmin admin : roleAdminList) {
            if (!admin.getRolname().equals("omm")&&(admin.isRolcreaterole()|| admin.isRolsuper()|| admin.isRolauditadmin())){
                msg = "如果三权分立开关已经开启，那么该数据库的系统管理违规持有权限";
            }
        }
        System.out.println("msg = " + msg);
        return msg;
    }




}