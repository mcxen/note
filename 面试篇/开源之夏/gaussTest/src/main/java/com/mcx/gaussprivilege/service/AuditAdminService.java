package com.mcx.gaussprivilege.service;

import com.mcx.gaussprivilege.dao.AuditAdminMapper;
import com.mcx.gaussprivilege.entity.RoleAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditAdminService {
    @Autowired
    AuditAdminMapper auditAdminMapper;
    public List<RoleAdmin> getAuditAdminList() {
        return auditAdminMapper.getAuditAdminList();
    }
}
