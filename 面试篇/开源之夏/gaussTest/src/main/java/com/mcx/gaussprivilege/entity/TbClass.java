package com.mcx.gaussprivilege.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_class")
public class TbClass {
    private String c_id;
    private String c_name;
    private String c_age;
}
