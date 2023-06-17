package com.mcxgroup.testmybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author c
 * @since 2023-06-17
 */
@Getter
@Setter
@TableName("t_user")
@ApiModel(value = "TUser对象", description = "用户表")
public class TUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("账号")
    private String username;

    @ApiModelProperty("名字")
    private String name;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("md5密码盐")
    private String salt;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("备注")
    private String tips;

    @ApiModelProperty("状态 1:正常 2:禁用")
    private Boolean state;

    @ApiModelProperty("创建时间")
    private LocalDateTime createdTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updatedTime;

    @Override
    public String toString() {
        return "TUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", phone='" + phone + '\'' +
                ", tips='" + tips + '\'' +
                ", state=" + state +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }
}
