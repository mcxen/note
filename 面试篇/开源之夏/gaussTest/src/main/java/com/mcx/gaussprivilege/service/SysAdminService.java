package com.mcx.gaussprivilege.service;

import com.mcx.gaussprivilege.dao.SysAdminMapper;
import com.mcx.gaussprivilege.entity.RoleAdmin;
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
