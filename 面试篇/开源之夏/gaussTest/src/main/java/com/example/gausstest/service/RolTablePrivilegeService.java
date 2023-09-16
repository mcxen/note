package com.example.gausstest.service;

import com.example.gausstest.dao.RolTablePrivilegeMapper;
import com.example.gausstest.entity.RolTablePrivilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolTablePrivilegeService {

    @Autowired
    RolTablePrivilegeMapper rolTablePrivilegeMapper;

    public List<RolTablePrivilege> getAllRolTablePrivileges() {
        return rolTablePrivilegeMapper.getAllRolTablePrivileges();
    }
}
