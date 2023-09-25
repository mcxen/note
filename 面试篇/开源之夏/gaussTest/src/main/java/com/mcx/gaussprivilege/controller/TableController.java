package com.mcx.gaussprivilege.controller;

import com.mcx.gaussprivilege.entity.Table;
import com.mcx.gaussprivilege.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tables")
public class TableController {
    private final TableService tableService;

    @Autowired
    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public List<Table> getAllTables() {
        return tableService.getAllTables();
    }
}
