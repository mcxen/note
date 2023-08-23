

# SpringBoot连接OpenGauss数据库

## 1 在OpenGauss中创建数据库、用户和表

⚠️ 注意：假设先创建用户A，切换用户A后创建数据库DB，则数据库DB属于用户A；

### 1.1 登录OpenGauss

```
# 进入容器
docker exec -it opengauss /bin/bash

# 切换用户
su omm

# 进入OpenGauss
gsql -d postgres -p 5432


```

### 1.2 创建数据库

```sql
# 创建数据库，不指定表空间，默认创建的数据库都属于用户“omm"，可以使用“OWNER”设置所有者用户;
CREATE DATABASE db_department;

# 切换数据库，切换后前缀会变成“db_department=#”
\c db_department;

# 切换后变成如下
openGauss=# \c db_department;
Non-SSL connection (SSL connection is recommended when requiring high-security)
You are now connected to database "db_department" as user "omm".
db_department=# 

# 删除数据库的方法
DROP DATABASE db_department;

# 查看数据库
\l

# 查看表空间
\db

```

![截屏2023-08-02 20.09.17](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-08-02%2020.09.17.png)

### 1.3 创建用户

注意：用户和数据库是有一定关联的

```sql
# 先切换到“db_department”数据库，如果已经切换跳过
\c db_department

# 将会在“db_department”数据库下在创建用户“admin_department”
CREATE USER admin_department WITH Sysadmin IDENTIFIED BY 'OpenGauss@123';
# 创建成功后后，会出现以下提示
NOTICE:  The encrypted password contains MD5 ciphertext, which is not secure.
CREATE ROLE

# 给用户授权
GRANT SELECT,INSERT,UPDATE,DELETE ON ALL TABLES IN SCHEMA public TO admin_department;
GRANT USAGE,SELECT ON ALL SEQUENCES IN SCHEMA admin_department TO admin_department;

# 删除用户的方法如下，使用CASCADE表示删除级联的信息，一般不要使用CASCADE；
DROP USER admin_department CASCADE;

# 查看用户
\du

# 切换用户的方法
\c - admin_department

# 退出
\q

# 使用“db_department”数据库和“admin_department”用户登录
gsql -d db_department -U admin_department -p 5432

```

### 1.4 创建表并插入数据

```sql
# 先切换到“db_department”数据库，如果已经切换跳过
\c db_department

# 切换到”admin_department“用户，如果已经切换跳过，密码是”OpenGauss@123“
# 如果不切换用户默认创建在”public“下
\c - admin_department

# 创建学生表
CREATE TABLE tb_class ( c_id INTEGER NOT NULL, c_name VARCHAR(40), c_age INTEGER );

# 插入数据
INSERT INTO tb_class (c_id, c_name, c_age) VALUES (1, '张三', 20), (2, '李四', 30), (3, 'wangwu', 40);

```

### 1.5 Data Studio连接OpenGauss

**连接OpenGauss**

> 配置一下：
>
> ```sh
> root@ab4f29ac64c8:/# su - omm --连接了docker终端后，切换用户
> omm@ab4f29ac64c8:~$ gsql -d postgres -p 5432 --连接到数据库5432端口
> gsql ((openGauss 5.0.0 build a07d57c3) compiled at 2023-03-29 03:09:38 commit 0 last mr  )
> Non-SSL connection (SSL connection is recommended when requiring high-security)
> Type "help" for help.
> 
> openGauss=# CREATE USER jack PASSWORD 'Test@123';  --创建jack用户
> openGauss=# gs_guc set -N all -I all -h "host all admin_department 127.0.0.1/32 sha256" --分配访问ip地址
> ```
>
> 

> ```properties
> ALTER USER admin_department SET ip TO '127.0.0.1/32';
> gs_guc set -N all -I all -h "host all admin_department 127.0.0.1/32 sha256";
> gs_guc set -I all -c "listen_addresses='localhost'"
> 用户：admin_department
> 密码：OpenGauss@123
> 数据库：db_department
> ```
>
> 

![在这里插入图片描述](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/ed8ee36753f6477d9a5812897c3ba60c-20230802203549050.png)

**连接成功**

![在这里插入图片描述](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/19a1286e3f4d4b469a1cf5d02d8049e4.png)

**查看数据**

![在这里插入图片描述](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/b9cb46e411e041ee875e5ccbd7eccc17.png)

## 2 SpringBoot连接数据库

使用postgresql驱动和MyBatis-Plus连接OpenGauss；

### 2.1 工程布局

![在这里插入图片描述](https://img-blog.csdnimg.cn/2e61ce568978484b88c493e005a00606.png)

### 2.2 pom.xml文件

pom.xml文件如下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.2.5.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.example</groupId>
	<artifactId>test</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>demo</name>
	<description>Test project for Spring Boot</description>
	<properties>
		<java.version>11</java.version>
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
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<version>42.5.1</version>
		</dependency>

		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<version>1.18.20</version>
			<scope>provided</scope>
		</dependency>

	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>

```

application.yml

```yaml
spring:
  datasource:
    # config openGauss
    url: jdbc:postgresql://192.168.108.200:5432/db_department
    username: admin_department
    password: OpenGauss@123
    driver-class-name: org.postgresql.Driver


# print logs
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    # 关闭数据库下划线自动转驼峰
    map-underscore-to-camel-case: false

```

### 2.3 创建实体类

在entitys目录下创建“TbClass.java”实体类

```java
package com.example.test.entitys;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
注意：下面的数据库字段和实体字段对应；
（1）如果，两个字段完全一样，则必须关闭application.yml中的map-underscore-to-camel-case属性；
    mybatis-plus:
        configuration:
            # 关闭数据库下划线自动转驼峰
            map-underscore-to-camel-case: false

（2）如果，数据库字段属性是下划线，实体字段是驼峰式，则必须开启application.yml中的map-underscore-to-camel-case属性（默认开启）
*/


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_class")
public class TbClass {
    private String c_id;
    private String c_name;
    private String c_age;
}

```

### 2.4 创建Mapper

在dao目录下的mpmapper目录下创建“TbClassMapper.java”

```java
package com.example.test.dao.mpmapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.test.entitys.TbClass;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbClassMapper extends BaseMapper<TbClass> {
}


```

### 2.5 创建Service

在service目录下创建“TbClassService.java”文件。

```java
package com.example.test.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.test.dao.mpmapper.TbClassMapper;
import com.example.test.entitys.TbClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TbClassService {

    @Autowired
    private TbClassMapper tbClassMapper;

    public List<TbClass> query(){
        QueryWrapper<TbClass> wrapper = new QueryWrapper<>();
        return this.tbClassMapper.selectList(wrapper);
    }
}


```

### 2.6 创建Controller

在controller目录下创建“TestController.java"文件。

```java
package com.example.test.controller;


import com.example.test.entitys.TbClass;
import com.example.test.service.TbClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/gadb")
public class TestController {

    @Autowired
    TbClassService tbClassService;

    @GetMapping("/query")
    public List<TbClass> queryData(){
        return tbClassService.query();
    }

}


```

### 2.7 创建Application

```java
package com.example.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

```

### 2.8 测试

浏览器中访问： http://localhost:8080/gadb/query

![在这里插入图片描述](https://img-blog.csdnimg.cn/e85a278a85054613814bdd39a5440b18.png)

IDEA中打印的日志

![在这里插入图片描述](https://img-blog.csdnimg.cn/c1bb59d192f1489381428d1dbfb687c0.png)

