package com.mcx.gaussprivilege.entity;

import lombok.Data;

@Data
public class Table {
    private String database;
    private String tableSchema;
    private String tableName;
}
