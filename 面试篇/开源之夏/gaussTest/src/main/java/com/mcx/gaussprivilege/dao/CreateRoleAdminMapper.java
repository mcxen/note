package com.mcx.gaussprivilege.dao;

import com.mcx.gaussprivilege.entity.RoleAdmin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CreateRoleAdminMapper {
    @Select("SELECT rolname, rolsuper, rolcreaterole, rolsystemadmin, rolauditadmin FROM pg_roles WHERE rolcreaterole = 'true'")
    List<RoleAdmin> getCreateRoleAdminList();
}
