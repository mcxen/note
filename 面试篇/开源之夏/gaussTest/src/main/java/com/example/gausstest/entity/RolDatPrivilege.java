package com.example.gausstest.entity;

import lombok.Data;

import java.util.List;

@Data
public class RolDatPrivilege {
    private String rolname;
    private String datname;
    private String privileges;
}
