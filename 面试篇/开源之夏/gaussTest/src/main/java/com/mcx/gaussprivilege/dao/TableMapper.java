package com.mcx.gaussprivilege.dao;

import com.mcx.gaussprivilege.entity.Table;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TableMapper {
    @Select("SELECT table_catalog AS database, table_schema, table_name " +
            "FROM information_schema.tables " +
            "WHERE table_type = 'BASE TABLE' " +
            "AND table_schema NOT IN ('pg_catalog', 'information_schema','dbe_pldeveloper','db4ai')")
    List<Table> getAllTables();
}
