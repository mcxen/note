package com.example.gausstest.service;

import com.example.gausstest.dao.CreateRoleAdminMapper;
import com.example.gausstest.entity.RoleAdmin;
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
