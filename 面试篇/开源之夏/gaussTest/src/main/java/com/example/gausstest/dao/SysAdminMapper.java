package com.example.gausstest.dao;

import com.example.gausstest.entity.RoleAdmin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysAdminMapper {
    @Select("SELECT rolname, rolsuper, rolcreaterole, rolsystemadmin, rolauditadmin FROM pg_roles WHERE rolsystemadmin = 'true'")
    List<RoleAdmin> getSysAdminList();
}
