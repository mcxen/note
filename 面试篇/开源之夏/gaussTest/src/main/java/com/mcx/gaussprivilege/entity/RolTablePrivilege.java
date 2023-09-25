package com.mcx.gaussprivilege.entity;

import lombok.Data;

@Data
public class RolTablePrivilege {
    private String rolName;
    private String tableName;
    private String privileges;
}
