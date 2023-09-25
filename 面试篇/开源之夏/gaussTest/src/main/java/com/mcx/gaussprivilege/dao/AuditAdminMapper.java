package com.mcx.gaussprivilege.dao;

import com.mcx.gaussprivilege.entity.RoleAdmin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AuditAdminMapper {
    @Select("SELECT rolname, rolsuper, rolcreaterole, rolsystemadmin, rolauditadmin FROM pg_roles WHERE rolauditadmin = 'true'")
    List<RoleAdmin> getAuditAdminList();
}
