package com.mcx.gaussprivilege.entity;

import lombok.Data;

import java.util.List;

@Data
public class RolDatPrivilege {
    private String rolname;
    private String datname;
    private String privileges;
}
