package com.mcx.gaussprivilege.dao;

import com.mcx.gaussprivilege.entity.PgUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PgUserMapper {
    @Select("SELECT usesysid, usename, usesuper FROM pg_user")
    List<PgUser> getAllUsers();
}
