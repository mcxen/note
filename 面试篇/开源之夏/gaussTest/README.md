## 2 SpringBoot连接数据库

使用postgresql驱动和MyBatis连接OpenGauss，识别数据库三权分立的管理员权限，识别用户或角色对数据库和表格的操作权限，识别所有用户列表。

### 2.1 工程布局

```sh
F:\项目\note\面试篇\开源之夏\gaussTest>tree /F
卷 work 的文件夹 PATH 列表
卷序列号为 D4FA-828F
F:.
│  .gitignore
│  pom.xml
├─lib
│  └─openGauss-3.1.0-JDBC
│          opengauss-jdbc-3.1.0.jar
│          postgresql.jar
│
├─src
│  ├─main
│  │  ├─java
│  │  │  └─com
│  │  │      └─example
│  │  │          └─gausstest
│  │  │              │  GaussTestApplication.java
│  │  │              │
│  │  │              ├─controller
│  │  │              │      AuditAdminController.java
│  │  │              │      CreateRoleAdminController.java
│  │  │              │      PgUserController.java
│  │  │              │      RolDatPrivilegeController.java
│  │  │              │      RolTablePrivilegeController.java
│  │  │              │      SysAdminController.java
│  │  │              │      TestController.java
│  │  │              │
│  │  │              ├─dao
│  │  │              │      AuditAdminMapper.java
│  │  │              │      CreateRoleAdminMapper.java
│  │  │              │      PgUserMapper.java
│  │  │              │      RolDatPrivilegeMapper.java
│  │  │              │      RolTablePrivilegeMapper.java
│  │  │              │      SysAdminMapper.java
│  │  │              │      TbClassMapper.java
│  │  │              │
│  │  │              ├─entity
│  │  │              │      PgUser.java
│  │  │              │      RolDatPrivilege.java
│  │  │              │      RoleAdmin.java
│  │  │              │      RolTablePrivilege.java
│  │  │              │      TbClass.java
│  │  │              │
│  │  │              ├─service
│  │  │              │      AuditAdminService.java
│  │  │              │      CreateRoleAdminService.java
│  │  │              │      PgUserService.java
│  │  │              │      RolDatPrivilegeService.java
│  │  │              │      RolTablePrivilegeService.java
│  │  │              │      SysAdminService.java
│  │  │              │      TbClassService.java
│  │  │              │
│  │  │              └─test
│  │  │                      jdbcDriverTest.java
│  │  │
│  │  └─resources
│  │      │  application.properties
│  │      │
│  │      ├─JDBCLIB
│  │      │      opengauss-jdbc-3.1.0.jar
│  │      │      postgresql.jar
│  │      │
│  │      └─static
│  │          │  creat.html
│  │          │  index.html
│  │          │  indexv1.html
│  │          │
│  │          ├─image
│  │          │      三权分立-安全.drawio.svg
│  │          │      三权分立-审计.drawio.svg
              │      三权分立-系统.drawio.svg
              │      三权分立.drawio
              │      三权分立.png
              │
              └─layui
```



### 2.2 项目配置文件

pom.xml文件如下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>gaussTest</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>gaussTest</name>
    <description>gaussTest</description>
    <properties>
        <java.version>1.8</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <spring-boot.version>2.4.2</spring-boot.version>
    </properties>
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
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.5.3</version>
        </dependency>

        <dependency>
        <groupId>org.bouncycastle</groupId>
        <artifactId>bcprov-jdk15on</artifactId>
        <version>1.70</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.20</version>
            <scope>provided</scope>
        </dependency>


    </dependencies>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring-boot.version}</version>
                <configuration>
                    <mainClass>com.example.gausstest.GaussTestApplication</mainClass>
                    <skip>true</skip>
                </configuration>
                <executions>
                    <execution>
                        <id>repackage</id>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

</project>

```

手动导入postgresql和opengauss的jar包。

![image-20230918171808500](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/image-20230918171808500.png)

application.properties

```properties
server.port=8080

#spring.datasource.url=jdbc:postgresql://103.91.210.232:11343/db_department
spring.datasource.url=jdbc:postgresql://192.168.161.18:5432/db_department
spring.datasource.username=admin_department
spring.datasource.password=OpenGauss@123
spring.datasource.driver-class-name=org.postgresql.Driver

mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
mybatis-plus.configuration.map-underscore-to-camel-case=true
```

### 2.3 创建实体类

![image-20230918163959285](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/image-20230918163959285.png)

举例：

在entitys目录下创建“RolTablePrivilege.java”实体类

```java
package com.example.gausstest.entity;

import lombok.Data;

@Data
public class RolTablePrivilege {
    private String rolName;
    private String tableName;
    private String privileges;
}

```

### 2.4 创建Mapper

![image-20230918164037128](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/image-20230918164037128.png)

在dao目录下创建“RolTablePrivilegeMapper.java”

```java
package com.example.gausstest.dao;

import com.example.gausstest.entity.RolTablePrivilege;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RolTablePrivilegeMapper {
    @Select("SELECT grantee AS rol_name, table_name, string_agg(privilege_type, ', ') AS privileges FROM information_schema.role_table_grants GROUP BY grantee,table_name")
    List<RolTablePrivilege> getAllRolTablePrivileges();
}

```

### 2.5 创建Service

![image-20230918164111898](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/image-20230918164111898.png)

在service目录下创建“RolTablePrivilegeService.java”文件。

```java
package com.example.gausstest.service;

import com.example.gausstest.dao.RolTablePrivilegeMapper;
import com.example.gausstest.entity.RolTablePrivilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolTablePrivilegeService {

    @Autowired
    RolTablePrivilegeMapper rolTablePrivilegeMapper;

    public List<RolTablePrivilege> getAllRolTablePrivileges() {
        return rolTablePrivilegeMapper.getAllRolTablePrivileges();
    }
}

```

### 2.6 创建Controller

![image-20230918164148839](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/image-20230918164148839.png)

在controller目录下创建“RolTablePrivilege.java"文件。

```java
package com.example.gausstest.controller;

import com.example.gausstest.entity.RolTablePrivilege;
import com.example.gausstest.service.RolTablePrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RolTablePrivilegeController {
    @Autowired
    RolTablePrivilegeService rolTablePrivilegeService;
    @GetMapping("/rolTablePrivileges")
    public List<RolTablePrivilege> getAllRolTablePrivileges() {
        return rolTablePrivilegeService.getAllRolTablePrivileges();
    }
}

```

### 2.7 创建Application

```java
package com.example.gausstest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GaussTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaussTestApplication.class, args);
    }

}

```

### 2.8 测试

浏览器中访问： localhost:8080

![](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/image-20230918165423593.png)



接口调试：

IDEA中打印的日志

![在这里插入图片描述](https://raw.githubusercontent.com/52chen/imagebed2023/main/c1bb59d192f1489381428d1dbfb687c0.png)

### 2.9 8080端口转发

> 这里设置了一个端口转发：
>
> 访问： [权限扫描仪表盘 (cpolar.cn)](https://a0a4901.r2.cpolar.cn/)

转发自：https://www.cnblogs.com/probezy/p/16742417.html

在没有公网IP的情况下，想要实现外网访问内网，可以通过cpolar内网穿透来解决，只需要简单安装客户端，就可以创建隧道，将本地端口映射到公网上，生成公网地址。

> - 支持永久免费使用（随机域名、1M带宽、4条免费隧道）
> - 支持http/https/tcp协议
> - 不限制流量

比如将本地8080端口下的web服务发布到公网可访问：

> cpolar下载地址：https://www.cpolar.com/

注册并安装cpolar内网穿透。
浏览器访问http://localhost:9200/，登录cpolar web UI管理界面
左侧仪表盘——隧道管理——创建隧道
隧道名称：可自定义
协议：http
本地地址：8080（填写你想要映射的本地端口号）
域名类型：免费选择“随机域名”，付费用户可选择配置固定的二级子域名或者自定义域名
地区：选择合适的
![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/25691-20220929173410026-283141293.png)

隧道创建成功后，可在状态——在线隧道列表查看到所映射的公网地址，公网用户访问该公网地址，就可以访问到在本地内网搭建的web服务。
![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/25691-20220929173415845-554180780.png)

