package com.mcx.gaussprivilege.service;

import com.mcx.gaussprivilege.dao.CreateRoleAdminMapper;
import com.mcx.gaussprivilege.entity.RoleAdmin;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateRoleAdminService {
    private final CreateRoleAdminMapper createRoleAdminMapper;

    public CreateRoleAdminService(CreateRoleAdminMapper createRoleAdminMapper) {
        this.createRoleAdminMapper = createRoleAdminMapper;
    }

    public List<RoleAdmin> getCreateRoleAdminList() {
        return createRoleAdminMapper.getCreateRoleAdminList();
    }
}
