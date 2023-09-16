package com.example.gausstest.dao;

import com.example.gausstest.entity.RolTablePrivilege;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RolTablePrivilegeMapper {
    @Select("SELECT grantee AS rol_name, table_name, string_agg(privilege_type, ', ') AS privileges FROM information_schema.role_table_grants GROUP BY grantee,table_name")
    List<RolTablePrivilege> getAllRolTablePrivileges();
}
