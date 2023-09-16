package com.example.gausstest.service;

import com.example.gausstest.dao.AuditAdminMapper;
import com.example.gausstest.dao.SysAdminMapper;
import com.example.gausstest.entity.RoleAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysAdminService {
    @Autowired
    SysAdminMapper sysAdminMapper;
    public List<RoleAdmin> getSysAdminList() {
        return sysAdminMapper.getSysAdminList();
    }
}
