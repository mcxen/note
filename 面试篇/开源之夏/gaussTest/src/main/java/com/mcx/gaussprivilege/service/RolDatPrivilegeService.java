package com.mcx.gaussprivilege.service;
import com.mcx.gaussprivilege.dao.RolDatPrivilegeMapper;
import com.mcx.gaussprivilege.entity.RolDatPrivilege;
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