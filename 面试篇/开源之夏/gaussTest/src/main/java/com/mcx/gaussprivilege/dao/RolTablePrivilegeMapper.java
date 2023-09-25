package com.mcx.gaussprivilege.dao;

import com.mcx.gaussprivilege.entity.RolTablePrivilege;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RolTablePrivilegeMapper {
    @Select("SELECT grantee AS rol_name, table_name, string_agg(privilege_type, ', ') AS privileges FROM information_schema.role_table_grants GROUP BY grantee,table_name")
    List<RolTablePrivilege> getAllRolTablePrivileges();
}
