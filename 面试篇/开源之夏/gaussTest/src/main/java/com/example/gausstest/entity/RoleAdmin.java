package com.example.gausstest.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleAdmin {
    private String rolname;
    private boolean rolsuper;
    private boolean rolcreaterole;
    private boolean rolsystemadmin;
    private boolean rolauditadmin;
}
