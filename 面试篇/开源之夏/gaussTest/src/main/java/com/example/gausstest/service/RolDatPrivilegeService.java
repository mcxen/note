package com.example.gausstest.service;
import com.example.gausstest.dao.RolDatPrivilegeMapper;
import com.example.gausstest.entity.RolDatPrivilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolDatPrivilegeService {
    private final RolDatPrivilegeMapper rolDatPrivilegeMapper;

    @Autowired
    public RolDatPrivilegeService(RolDatPrivilegeMapper rolDatPrivilegeMapper) {
        this.rolDatPrivilegeMapper = rolDatPrivilegeMapper;
    }

    public List<RolDatPrivilege> getRolDatPrivileges() {
        return rolDatPrivilegeMapper.getRolDatPrivileges();
    }
}