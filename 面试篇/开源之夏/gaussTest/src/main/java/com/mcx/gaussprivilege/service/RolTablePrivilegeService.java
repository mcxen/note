package com.mcx.gaussprivilege.service;

import com.mcx.gaussprivilege.dao.RolTablePrivilegeMapper;
import com.mcx.gaussprivilege.entity.RolTablePrivilege;
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
