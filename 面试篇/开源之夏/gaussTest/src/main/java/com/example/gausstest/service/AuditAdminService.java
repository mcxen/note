package com.example.gausstest.service;

import com.example.gausstest.dao.AuditAdminMapper;
import com.example.gausstest.dao.CreateRoleAdminMapper;
import com.example.gausstest.entity.RoleAdmin;
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
