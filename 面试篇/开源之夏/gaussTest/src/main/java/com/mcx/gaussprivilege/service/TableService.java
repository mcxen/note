package com.mcx.gaussprivilege.service;

import com.mcx.gaussprivilege.dao.TableMapper;
import com.mcx.gaussprivilege.entity.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableService {
    private final TableMapper tableMapper;

    @Autowired
    public TableService(TableMapper tableMapper) {
        this.tableMapper = tableMapper;
    }

    public List<Table> getAllTables() {
        return tableMapper.getAllTables();
    }
}