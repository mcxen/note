package com.example.gausstest.entity;

import lombok.Data;

@Data
public class RolTablePrivilege {
    private String rolName;
    private String tableName;
    private String privileges;
}
