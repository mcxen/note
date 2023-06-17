# 简介
这个项目是用作测试Mybatis-plus的基本使用。还包括

- CodeGenerator，代码生成器
- 链接和使用链接lambdaQuery
- slf4j

测试结果：能够获取User信息。


![image-20230617163803263](https://raw.githubusercontent.com/52chen/imagebed2023/main/image-20230617163803263.png)

# 实例配置
- SpringBoot 由starter.aliyun.com 构建，版本为2.4.2
- Mybatis版本为3.5.2
- 日期：2023年6月17日

## SQL 代码
```sql
CREATE DATABASE IF NOT EXISTS pos default charset utf8 COLLATE utf8_general_ci;
SET FOREIGN_KEY_CHECKS=0;
USE pos;

-- 用户表
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id`                        INT(11) PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `username`                  VARCHAR(32) NOT NULL COMMENT '账号',
  `name`                      VARCHAR(16) DEFAULT '' COMMENT '名字',
  `password`                  VARCHAR(128) DEFAULT '' COMMENT '密码',
  `salt`                      VARCHAR(64) DEFAULT '' COMMENT 'md5密码盐',
  `phone`                     VARCHAR(32) DEFAULT '' COMMENT '联系电话',
  `tips`                      VARCHAR(255) COMMENT '备注',
  `state`                     TINYINT(1) DEFAULT 1 COMMENT '状态 1:正常 2:禁用',
  `created_time`              DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time`              DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用户表';
INSERT INTO `t_user` VALUES (1,'admin','系统管理员','123456','www', '17890908889', '系统管理员', 1, '2017-12-12 09:46:12', '2017-12-12 09:46:12');
INSERT INTO `t_user` VALUES (2,'aix','张三','123456','eee', '17859569358', '', 1, '2017-12-12 09:46:12', '2017-12-12 09:46:12');

```

## pom文件依赖：

```xml

<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.25</version>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.5.3</version>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-generator</artifactId>
            <version>3.5.3</version>
        </dependency>
        <dependency>
            <groupId>org.apache.velocity</groupId>
            <artifactId>velocity-engine-core</artifactId>
            <version>2.3</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.24</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.23</version>
        </dependency>

        <dependency>
            <groupId>io.swagger</groupId>
            <artifactId>swagger-annotations</artifactId>
            <version>1.5.20</version>
        </dependency>
    </dependencies>

```