package com.mcx.gaussprivilege.controller;

import com.mcx.gaussprivilege.entity.RoleAdmin;
import com.mcx.gaussprivilege.service.AuditAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class AuditAdminController {
    @Autowired
    private AuditAdminService auditAdminService;
    /*
    SELECT rolname,rolsuper,rolcreaterole,rolsystemadmin,rolauditadmin FROM pg_roles WHERE rolcreaterole = 'true';，
    执行结果为：[{"rolname":"omm","rolsuper":"t","rolcreaterole":"t","rolsystemadmin":"t","rolauditadmin":"t"},
    {"rolname":"safeadmin","rolsuper":"f","rolcreaterole":"t","rolsystemadmin":"f","rolauditadmin":"f"}]
    。设计SpringBoot的restful接口，使用mybtais负责dao，编写entity层，entity层的名字叫createRoleAdmin，
    编写service层给出controller，编写对应的网页的layui风格的使用这个接口的表格
     */
    @GetMapping("/auditadmin")
    public List<RoleAdmin> getAuditAdminList(HttpSession httpSession) {
        List<RoleAdmin> roleAdminList = auditAdminService.getAuditAdminList();

        return roleAdminList;
    }
    @GetMapping("/auditadminerr")
    public String getAuditAdminErr(){
        List<RoleAdmin> roleAdminList = auditAdminService.getAuditAdminList();
        String msg = "";
        msg ="该数据库的审计管理员权限正常";
        for (RoleAdmin admin : roleAdminList) {
            if (!admin.getRolname().equals("omm")&&(admin.isRolcreaterole()|| admin.isRolsuper()|| admin.isRolsystemadmin())){
                msg = "如果三权分立开关已经开启，那么该数据库的审计管理违规持有权限";
            }
        }
        System.out.println("msg = " + msg);
        return msg;
    }




}