package com.mcx.gaussprivilege.entity;

import lombok.Data;

@Data
public class PgUser {
    private Integer usesysid;
    private String usename;
    private Boolean usesuper;

    // getter and setter methods
}
