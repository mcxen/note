# MyBatis

2023-03-27 20:08 华科大LAB 晴朗☀️  - 基本mybatis单独使用笔记







## 1、简介

### 1.1、什么是Mybatis

MyBatis 是一款优秀的持久层框架，它支持自定义 SQL、存储过程以及高级映射。MyBatis 免除了几乎所有的 JDBC 代码以及设置参数和获取结果集的工作。MyBatis 可以通过简单的 XML 或注解来配置和映射原始类型、接口和 Java POJO（Plain Old Java Objects，普通老式 Java 对象）为数据库中的记录。 

如何获得MyBatis：

* Maven仓库

  ~~~xml
  <!-- https://mvnrepository.com/artifact/org.mybatis/mybatis -->
  <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis</artifactId>
      <version>3.5.9</version>
  </dependency>
  
  ~~~

  

* GitHub：https://github.com/mybatis/mybatis-3/releases

### 1.2、持久化

数据持久化

* 持久化就是将程序的数据在持久状态和瞬时状态转化的过程
* 内存：**断电即失**
* 数据库（jdbc），io文件持久化。

**为什么需要持久化？**

有一些数据我们不能让它丢失。

### 1.3、持久层

Dao层，Service层，Controller层

* 完成持久化工作的代码块
* 层是界限明显的

### 1.4、为什么需要MyBatis？

**技术没有高低之分！**

* 帮助程序员将数据存储到数据库中
* 传统的JDBC代码太复杂了，需要简化成框架
* 用的人多，其实不用这个也行！
* 因为他有以下优点：
  * 简单易学：本身就很小且简单。没有任何第三方依赖，最简单安装只要两个jar文件+配置几个sql映射文件。易于学习，易于使用。通过文档和源代码，可以比较完全的掌握它的设计思路和实现。
  * 灵活：mybatis不会对应用程序或者数据库的现有设计强加任何影响。 sql写在xml里，便于统一管理和优化。通过sql语句可以满足操作数据库的所有需求。
  * 解除sql与程序代码的耦合：通过提供DAO层，将业务逻辑和数据访问逻辑分离，使系统的设计更清晰，更易维护，更易单元测试。sql和代码的分离，提高了可维护性。
  * 提供映射标签，支持对象与数据库的orm字段关系映射。
  * 提供对象关系映射标签，支持对象关系组建维护。
  * 提供xml标签，支持编写动态sql。

### 1.5、mybatis一般开发流程

引入依赖，常见配置文件，创建pojo，创建mapper映射文件，初始化SessionFactory，创建SqlSession对象

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x5b25saXlhbmc=,size_16,color_FFFFFF,t_70.png" alt="MyBatis执行流程"  />

### 1.6、mybatis预防sql注入

mybatis 中使用 sqlMap 进行 sql 查询时，经常需要动态传递参数，例如我们需要根据用户的姓名来筛选用户时，sql 如下：

```pgsql
select * from user where name = "ruhua";
```

上述 sql 中，我们希望 name 后的参数 "ruhua" 是动态可变的，即不同的时刻根据不同的姓名来查询用户。在 sqlMap 的 xml 文件中使用如下的 sql 可以实现动态传递参数 name：

```pgsql
select * from user where name = #{name};
```

或者

```pgsql
select * from user where name = '${name}';
```

对于上述这种查询情况来说，使用 #{ } 和 ${ } 的结果是相同的，但是在某些情况下，我们只能使用二者其一。



#### **快速总结：**

- ```#{}```解析传递进来的参数数据
- ```${}```对传递进来的参数原样拼接在SQL中
- ```#{}```是预编译处理，```${}```是字符串替换，也就是字符串拼接
- 使用```#{}```可以有效的防止SQL注入，提高系统安全性。

比如说：

```sh
Map param = new HashMap();
param.put("titile","''or 1 = 1 or title = 'yes'");
```

`${}`这里就直接被注入了



**详细总结：**

**动态 SQL** 是 mybatis 的强大特性之一，也是它优于其他 ORM 框架的一个重要原因。mybatis 在对 sql 语句进行预编译之前，会对 sql 进行动态解析，解析为一个 BoundSql 对象，也是在此处对动态 SQL 进行处理的。

在动态 SQL 解析阶段， #{ } 和 ${ } 会有不同的表现：

> **#{ } 解析为一个 JDBC 预编译语句（prepared statement）的参数标记符。**

例如，sqlMap 中如下的 sql 语句

```pgsql
select * from user where name = #{name};
```

解析为：

```pgsql
select * from user where name = ?;
```

一个 #{ } 被解析为一个参数占位符 `?` 。

而，

> **${ } 仅仅为一个纯碎的 string 替换，在动态 SQL 解析阶段将会进行变量替换**

例如，sqlMap 中如下的 sql

```pgsql
select * from user where name = '${name}';
```

当我们传递的参数为 "ruhua" 时，上述 sql 的解析为：

```pgsql
select * from user where name = "ruhua";
```

预编译之前的 SQL 语句已经不包含变量 name 了。

综上所得， ${ } 的变量的替换阶段是在动态 SQL 解析阶段，而 #{ }的变量的替换是在 DBMS 中。

#### 用法 tips

> 1、能使用 #{ } 的地方就用 `#{ }`

首先这是为了性能考虑的，相同的预编译 sql 可以重复利用。

其次，**${ } 在预编译之前已经被变量替换了，这会存在 sql 注入问题**。例如，如下的 sql，

```pgsql
select * from ${tableName} where name = #{name} 
```

假如，我们的参数 tableName 为 `user; delete user; --`，那么 SQL 动态解析阶段之后，预编译之前的 sql 将变为

```sql
select * from user; delete user; -- where name = ?;
```

`--` 之后的语句将作为注释，不起作用，因此本来的一条查询语句偷偷的包含了一个删除表数据的 SQL！

> 2、表名作为变量时，必须使用 `${ }`

这是因为，表名是字符串，使用  `#{tableName}`替换字符串时会带上单引号 `''`，这会导致 sql 语法错误，例如：

```pgsql
select * from #{tableName} where name = #{name};
```

预编译之后的sql 变为：

```pgsql
select * from ? where name = ?;
```

假设我们传入的参数为 tableName = "user" , name = "hua"，那么在占位符进行变量替换后，sql 语句变为

```pgsql
select * from 'user' where name='hua';
```

上述 sql 语句是存在语法错误的，表名不能加单引号 `''`（注意，反引号 ``是可以的）。

#### sql预编译

### 定义

> sql 预编译指的是数据库驱动在发送 sql 语句和参数给 DBMS 之前对 sql 语句进行编译，这样 DBMS 执行 sql 时，就不需要重新编译。

### 为什么需要预编译

JDBC 中使用对象 PreparedStatement 来抽象预编译语句，使用预编译

1. **预编译阶段可以优化 sql 的执行**。
   预编译之后的 sql 多数情况下可以直接执行，DBMS 不需要再次编译，越复杂的sql，编译的复杂度将越大，预编译阶段可以合并多次操作为一个操作。
2. **预编译语句对象可以重复利用**。
   把一个 sql 预编译后产生的 PreparedStatement 对象缓存下来，下次对于同一个sql，可以直接使用这个缓存的 PreparedState 对象。

mybatis 默认情况下，将对所有的 sql 进行预编译。





## 2、第一个MyBatis查询程序



思路：搭建环境 -> 导入MyBatis -> 编写代码 -> 测试！

### 2.1、搭建环境-数据库

搭建数据库

~~~mysql
create database mybatis;
use mybatis;
create table user(
	`id` int primary key auto_increment,
	`name` varchar(20),
	`password` varchar(20)
)engine=innodb default charset=utf8;

insert into user(`name`,`password`) values
('Suk1','123456'),
('Suk2','123456');
~~~

新建项目

1. 新建一个普通Maven项目

2. 创建父工程，删除src

3. 导入Maven依赖

   ~~~xml
   <!--导入依赖-->
       <dependencies>
           <!-- MySQL驱动 -->
           <dependency>
               <groupId>mysql</groupId>
               <artifactId>mysql-connector-java</artifactId>
               <version>5.1.49</version>
           </dependency>
           <!-- Mybatis -->
           <dependency>
               <groupId>org.mybatis</groupId>
               <artifactId>mybatis</artifactId>
               <version>3.5.9</version>
           </dependency>
           <!-- junit -->
           <dependency>
               <groupId>junit</groupId>
               <artifactId>junit</artifactId>
               <version>4.13</version>
           </dependency>
       </dependencies>
   ~~~



Mysql8.0的时候的依赖：

```xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <version>8.0.11</version>
</dependency>


```



修改为阿里云maven仓库

```xml
<repositories>
        <repository>
            <id>alimaven</id>
            <name>aliyun maven</name>
            <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>
```





### 2.2、创建模块

* 编写MyBatis的核心配置文件

* Mybatis-config.xml

  这里的configuration就是必须是根结点

  ~~~xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE configuration
          PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <!--configuration核心配置文件-->
  <configuration>
      <!--环境-->
   
    <!--    deafult其实就是默认用这个dev开头的环境配置，还可以新建别的id，可以增加多个环境信息，-->
      <environments default="development">
          <environment id="development">
              <!--事务管理,commit/rollback-->
              <transactionManager type="JDBC"/>
              <dataSource type="POOLED">
                
                  <property name="driver" value="com.mysql.jdbc.Driver"/>
  <!--                下面的mybatis指的就是连接的哪一个database，这里是mybatis数据库-->
                  <property name="url" value="jdbc:mysql:///mybatis?useSSL=true&amp;userUnicode=true&amp;characterEncoding=UTF-8"/>
                  <property name="username" value="root"/>
                  <property name="password" value="Mysql2486"/>
              </dataSource>
          </environment>
      </environments>
  </configuration>
  ~~~

  >mysql8.0的时候
  >
  >mybatis.config.xml
  >
  >```xml
  ><?xml version="1.0" encoding="UTF-8" ?>
  ><!DOCTYPE configuration
  >        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  >        "http://mybatis.org/dtd/mybatis-3-config.dtd">
  ><!--configuration核心配置文件-->
  ><configuration>
  >    <!--环境-->
  ><!--    deafult其实就是默认用这个dev开头的环境配置，还可以新建别的id，可以增加多个环境信息，-->
  >    <environments default="development">
  >        <environment id="development">
  >            <!--事务管理-->
  >            <transactionManager type="JDBC"/>
  >            <dataSource type="POOLED">
  >                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
  ><!--                下面的mybatis指的就是连接的哪一个database，这里是mybatis数据库-->
  ><!--                <property name="url" value="jdbc:mysql:///mybatis?useSSL=true&amp;userUnicode=true&amp;characterEncoding=UTF-8"/>-->
  >                <property name="url" value="jdbc:mysql://localhost:3306/mybatis?characterEncoding=utf8&amp;useSSL=false&amp;serverTimezone=UTC&amp;rewriteBatchedStatements=true
  >"/>
  >                <property name="username" value="root"/>
  >                <property name="password" value="Mysql2486"/>
  >            </dataSource>
  >        </environment>
  >    </environments>
  ></configuration>
  >```



### 2.3、编写SqlSession代码

有了mybatis-config.xml之后，可以编写测试类，测试sqlSession的运行结果

**注意：**

<dataSource type="POOLED">为POOLED，如果sqlSession.close就是放回连接池，如果是UNPOOLED就是Connection.close了-**

```java
 public void test() throws IOException {
   //Reader家在xml核心配置
     Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
     //        初始化SqlSessionFactory解析xml文件。
     SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
     SqlSession sqlSession = sqlSessionFactory.openSession();//SqlSession是jDBC的扩展类
     Connection connection = sqlSession.getConnection();
   //<!-- POOLED，如果sqlSession.close就是放回连接池，如果是UNPOOLED就是Connection.close了-->
     System.out.println(connection);
 }
```

![image-20230323205127712](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230323205127712.png)

实际上，我们sqlsession都不会需要的。



为了保证sqlSessionFactory全局唯一，我们会需要构造MyBatisUtils

MyBatisUtils.java

```java
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class MyBatisUtils {
    private static SqlSessionFactory sqlSessionFactory = null;
    //唯一性
    static {
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("mybatis-config.xml");
            sqlSessionFactory =  new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);//在类初始化的时候就抛出异常。
        }
    }
//    设置为静态，直接类调用openSession
    public static SqlSession openSession(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession;
    }
    //    设置为静态，直接类调用closeSession
    public static void closeSession(SqlSession sqlSession){
        if (sqlSession!=null)
            sqlSession.close();

    }

}
```





### 2.4、编写Mapper代码

* 实体类

  ~~~java
  @Data
  public class User {
      private Integer id;
      private String name;
      private String password;
  }
  ~~~

  

* 接口实现类mapper.xml

  ![image-20230323214531910](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230323214531910.png)
  
  ~~~xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <!--namespace类似与包-->
  <mapper namespace="User">
  <!--    id就是名字，User下的selectAll就可以找到这个方法了-->
      <select id="selectAll" resultType="User">
          select * from user
      </select>
  </mapper>
  ~~~

> 这样也可以，不然就直接sqlSession调用方法，

```java
public interface UserMapper {
    List<User> selectAll();
}


UserMapper mapper = sqlSession.getMapper(UserMapper.class);
```

​	 在mybatis-config.xml里面添加  

```xml
<mappers>
    <mapper resource="mappers/mapper.xml"/>
</mappers>
```



* MapperTest - Junit测试

  ~~~java
  import org.apache.ibatis.session.SqlSession;
  import org.junit.Test;
  
  import java.sql.Connection;
  import java.util.List;
  
  public class MapperTest {
      @Test
      public void test() {
          // 获取SqlSession
          SqlSession sqlSession = null;
          try {
              sqlSession = MyBatisUtils.getSqlSession();
              Connection connection = sqlSession.getConnection();
              System.out.println(connection);
  //            User就是xml的名字，
              List<User> users = sqlSession.selectList("User.selectAll");
              for (User user : users) {
                  System.out.println("user1 = " + user1);
              }
          } catch (Exception e) {
              throw new RuntimeException(e);
          } finally {
              MyBatisUtils.closeSession(sqlSession);
          }
  //
      }
  }
  
  ~~~



### 2.5、最终代码

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230323215531084.png" alt="image-20230323215531084" style="zoom:50%;" />





```java
//User.java
public class User {
    private Integer id;
    private String name;
    private String password;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```





```java
//MyBatisUtils.java
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class MyBatisUtils {
    private static SqlSessionFactory sqlSessionFactory = null;
    //唯一性
    static {
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("mybatis-config.xml");
            sqlSessionFactory =  new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);//在类初始化的时候就抛出异常。
        }
    }
//    设置为静态，直接类调用openSession
    public static SqlSession getSqlSession(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession;
    }
    //    设置为静态，直接类调用closeSession
    public static void closeSession(SqlSession sqlSession){
        if (sqlSession!=null)
            sqlSession.close();

    }

}
```





```java
//MapperTest.java
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

public class MapperTest {
    @Test
    public void test() {
        // 获取SqlSession
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            Connection connection = sqlSession.getConnection();
            System.out.println(connection);
//            User就是xml的名字，
            List<User> user = sqlSession.selectList("User.selectAll");
            for (User user1 : user) {
                System.out.println("user1 = " + user1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
//
    }
}
```





mapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--namespace类似与包-->
<mapper namespace="User">
<!--    id就是名字，User下的selectAll就可以找到这个方法了-->
    <select id="selectAll" resultType="User">
        select * from user
    </select>
</mapper>
```



Mybatis-config.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<!--configuration核心配置文件-->
<configuration>
    <!--环境-->
<!--    deafult其实就是默认用这个dev开头的环境配置，还可以新建别的id，可以增加多个环境信息，-->
    <environments default="development">
        <environment id="development">
            <!--事务管理-->
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
<!--                下面的mybatis指的就是连接的哪一个database，这里是mybatis数据库-->
<!--                <property name="url" value="jdbc:mysql:///mybatis?useSSL=true&amp;userUnicode=true&amp;characterEncoding=UTF-8"/>-->
                <property name="url" value="jdbc:mysql://localhost:3306/mybatis?characterEncoding=utf8&amp;useSSL=false&amp;serverTimezone=UTC&amp;rewriteBatchedStatements=true
"/>
                <property name="username" value="root"/>
                <property name="password" value="Mysql2486"/>
            </dataSource>
        </environment>
    </environments>

    <mappers>
        <mapper resource="mappers/mapper.xml"/>
    </mappers>
</configuration>
```





pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.mcx</groupId>
    <artifactId>JedisTest</artifactId>
    <version>1.0-SNAPSHOT</version>
    <repositories>
        <repository>
            <id>alimaven</id>
            <name>aliyun maven</name>
            <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>
    <dependencies>
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>2.9.0</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.11</version>
        </dependency>
        <!-- Mybatis -->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.9</version>
        </dependency>
    </dependencies>
    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    <build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>true</filtering>
            </resource>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>true</filtering>
            </resource>
        </resources>
    </build>
</project>
```





## 3、CRUD

SqlSession来实现CRUD

### 3.1、namespace

namespace中的包名要和dao/mapper中的包名一致

### 3.2、select、insert、update、delete

* id就是对应namespace接口中的方法名

* resultType就是SQL语句执行的返回值

* parameterType就是参数的类型

  ~~~xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="UserMapper">
      <select id="selectAll" resultType="User">
          select *
          from user
      </select>
    	
      <select id="selectByid" resultType="User" parameterType="Integer">
          select *
          from user
          where id = #{id}
      </select>
  
      <insert id="insert" parameterType="User">
          insert into user(name, password)
          values (#{name}, #{password})
        -- 查询插入的
          <selectKey resultType="Integer" keyProperty="id" order="AFTER">
              select last_insert_id()
  			-- 这里last_insert_id是mysql自带的函数,System.out.println(yes.getId());//查询结果为0
          </selectKey>
      </insert>
  
      <update id="updateUser" parameterType="User">
          update user
          set name    = #{name},
              password=#{password}
          where id = #{id};
      </update>
  
      <delete id="deleteUser" parameterType="int">
          delete
          from user
          where id = #{id};
      </delete>
  </mapper>
  ~~~
  
  注意点：
  
  * 增删改需要提交事务
  * last_insert_id，将返回插入的那条记录在表中自增的那个字段的值，一般我们都给那个自增字段命名为ID。这样就可以返回刚插入的记录的ID值了。
  
  sqlSession调用查询就是：
  
  ```java
  @Test
      public void test() {
          // 获取SqlSession
          SqlSession sqlSession = null;
          try {
              sqlSession = MyBatisUtils.getSqlSession();
              Connection connection = sqlSession.getConnection();
              System.out.println(connection);
  //          UserMapper就是xml的namesapce，
              List<User> user = sqlSession.selectList("UserMapper.selectAll");
              User user2 = sqlSession.selectOne("UserMapper.selectByid",2);
              //上面是带参数查询
  
  //            for (User user1 : user) {
  //                System.out.println("user1 = " + user1);
  //            }
            	User yes = new User(10, "yes", "12121");
              int insert = sqlSession.insert("UserMapper.insert", yes);
              //insert返回类型，代表插入的总数
              sqlSession.commit();
            
              System.out.println(user2);
          } catch (Exception e) {
            if (sqlSession!=null)
                  sqlSession.rollback();
            //如果出现意外就要回滚
              throw new RuntimeException(e);
          } finally {
              MyBatisUtils.closeSession(sqlSession);
          }
  //
      }
  ```







### 3.3、万能的Map-多参数的传递

假设我们的实体类或者数据库中的表参数过多，我们应该考虑到Map！

~~~xml
<!--添加用户-->
    <insert id="insert" parameterType="java.util.map">
        insert into user (id, name, password)
        values (#{id}, #{username}, #{password});
    </insert>
~~~

```java
@Test
public void testMap(){
    try {
      // 获取SqlSession
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        Connection connection = sqlSession.getConnection();
        System.out.println("connection = " + connection);
        Map param =  new HashMap();
        param.put("id",8);
        param.put("name","aaa");
        param.put("password","1212121");
//            User就是xml的名字，,insert就是mapper.xml里main的名字
        sqlSession.insert("User.insert",param);
        User one = sqlSession.selectOne("User.selectByid", 8);            
      //上面是带参数查询
        System.out.println(one);
        sqlSession.commit();
    } catch (Exception e) {
        throw new RuntimeException(e);
    } finally {
    }
}
```

commit之后才会保存

```sh
mysql> select * from user;
+----+------+----------+
| id | name | password |
+----+------+----------+
|  1 | Suk1 | 123456   |
|  2 | Suk2 | 123456   |
|  3 | NULL | 23333    |
|  8 | aaa  | 1212121  |
| 90 | aaa  | 1212121  |
+----+------+----------+
5 rows in set (0.00 sec)
```



map取key，【parameterType="map"】

对象取属性名  【parameterType="Object"】

只有一个基本类型的时候，随便写，parameterType不用写

多个参数用map或者**注解**



> mapper.xml文件
>
> ```xml
> <?xml version="1.0" encoding="UTF-8" ?>
> <!DOCTYPE mapper
>         PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
>         "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
> <!--namespace类似与包-->
> <mapper namespace="User">
> <!--    id就是名字，User下的selectAll就可以找到这个方法了-->
>     <select id="selectAll" resultType="User">
>         select * from user
>     </select>
>     <select id="selectByid" parameterType="Integer" resultType="User">
>         select * from user where id = #{value}
>     </select>
>     <insert id="insert" parameterType="java.util.Map">
>         insert into user values (#{id},#{name},#{password})
>     </insert>
> </mapper>
> ```





### 3.4、Map作为resultType



```xml
<!--Mapz作为结果集-->
    <select id="selectAllMap" resultType="java.util.Map">
        select * from user
    </select>
```



运行代码：

```java
@Test
public void testMap(){
    try {
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        Connection connection = sqlSession.getConnection();
        System.out.println("connection = " + connection);
        List<Map> maps = sqlSession.selectList("User.selectAllMap");
        for (Map map : maps) {
            System.out.println("map = " + map);
        }
    } catch (Exception e) {
        throw new RuntimeException(e);
    } finally {
    }
}
```



Debug，可以看到返回值中的map的key是混乱的，数字会识别成integer。HashMap是按照hash值排序的，所以与table的值不一致

![image-20230327133450258](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230327133450258.png)



LinkedHashMap是按照插入的顺序来保存的，这时候就正常了。

```xml
<!--Mapz作为结果集-->
    <select id="selectAllMap" resultType="java.util.LinkedHashMap">
        select * from user
    </select>
```



```sh
connection = com.mysql.cj.jdbc.ConnectionImpl@21a947fe
map = {id=1, name=Suk1, password=123456}
map = {id=2, name=Suk2, password=123456}
map = {id=3, password=23333}
map = {id=8, name=aaa, password=1212121}
map = {id=90, name=aaa, password=1212121}

Process finished with exit code 0
```

Map灵活，但是开发的时候无法看到中间的结构,一般用来多表查询



### 3.5、ResultMap



在`Mybatis`中，有一个强大的功能元素`resultMap`。当我们希望将`JDBC ResultSets`中的数据，转化为合理的Java对象时，你就能感受到它的非凡之处。正如其官方所述的那样：

> `resultMap`元素是 `MyBatis` 中最重要最强大的元素。它可以让你从 90% 的 `JDBC ResultSets` 数据提取代码中解放出来，并在一些情形下允许你进行一些 JDBC 不支持的操作。实际上，在为一些比如连接的复杂语句编写映射代码的时候，一份 `resultMap` 能够代替实现同等功能的长达数千行的代码。`ResultMap` 的设计思想是，对于简单的语句根本不需要配置显式的结果映射，而对于复杂一点的语句只需要描述它们的关系就行了。

基本结构：

```xml
<resultMap>
        <constructor>
            <idArg/>
            <arg/>
        </constructor>
        <id/>
        <result/>
        <association property=""/>
        <collection property=""/>
        <discriminator javaType="">
            <case value=""></case>
        </discriminator>
</resultMap>


```

```sh
constructor – 类在实例化时，用来注入参数值到构造方法中。
idArg – ID 参数；标记结果作为 ID 可以帮助提高整体效能。
arg – 注入到构造方法的一个普通参数。
id – 一个 ID 结果； 标记结果作为 ID 可以帮助提高整体效能。
result – 注入到字段或 JavaBean 属性的普通参数。
association – 一个复杂的类型关联；许多结果将包成这种类型。
collection – 复杂类型的集合。
discriminator – 使用参数值来决定使用哪个参数来进行映射。
case – 基于某些值的结果映射。

```

比如，我们有一个`User`类：

```java
public class User {
    private String id;
    private String username;
    private String password;
    private String address;
    private String email;
}
```

如果数据库中表的字段与`User`类的属性名称一致，我们就可以使用`resultType`来返回。

```xml
<select id="getUsers" resultType="User">
	SELECT
		u.id,
		u.username,
		u.password,
		u.address,
		u.email
	FROM
		USER u
</select>
```

当然，这是理想状态下，属性和字段名都完全一致的情况。但事实上，不一致的情况是有的，这时候我们的`resultMap`就要登场了。

如果`User`类保持不变，但`SQL`语句发生了变化，将`id`改成了`uid`。

```xml
<select id="getUsers" resultType="User">
	SELECT
		u.id as uid,
		u.username,
		u.password,
		u.address,
		u.email
	FROM
		USER u
</select>
```

那么，在结果集中，我们将会丢失`id`数据。这时候我们就可以定义一个`resultMap`，来映射不一样的字段。

```xml
<!--    resultMap来统计结果-->
    <resultMap id="rmUser" type="POJO.User">
        <id property="id" column="id"/>
        <result property="name" column="name"/>
        <result property="password" column="password"/>
    </resultMap>
    <select id="selectAllMap" resultMap="rmUser">
        select * from user
    </select>
```

然后，我们把上面的`select`语句中的`resultType`修改为`resultMap="rmUser"`。

这里面`column`对应的是数据库的列名或别名；`property`对应的是结果集的字段或属性。

这就是`resultMap`最简单，也最基础的用法：字段映射。

```java
    @Test
    public void testMapperImple(){
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            Connection connection = sqlSession.getConnection();
            System.out.println("connection = " + connection);
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            List<User> users = mapper.selectAllMap();
//            List<User> users = sqlSession.selectList("UserMapper.selectAllMap");
            for (User user : users) {
                System.out.println("user = " + user);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

// 下面是接口UserMapper.java
/*
public interface UserMapper {
    public List<User> selectAll();
    public List<User> selectAllMap();
}
*/
```

> 我靠，烦死了这个bug，对于同一个查询语句只能设定一个id，不然就会报错
>
> <img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230327190459457.png" alt="image-20230327190459457" style="zoom: 67%;" />
>
> 我特么一直以为这个是我上面写的有问题，结果删了我的下面的，结果还是报错，结果发现是不能对同一个句子写两个id。
>
> 对了，这个private和public都是对的。-.-
>
> ```java
> public class User {
>     private Integer id;
>     private String name;
>     private String password;
> }
> ```
>
> 

### 3.6、批处理插入：

batchInsert速度最快，但是不能获得插入的数据的id，Sql太长了，有可能是服务器会被拒绝。

```xml
  <insert id="batchInsert" parameterType="java.util.List">
        INSERT INTO blog
        VALUES
            <foreach collection="list" item="item" index="index" separator=",">
                (#{item.id},#{item.title},#{item.author},#{item.createTime},#{item.views})
            </foreach>
    </insert>
<!--    &#45;&#45;             list是mybtais指定的的，外面传进来的, ,分割器就是我们插入的时候value(1,2,32),(12,23423,232)这里的分割器-->
```

结果：

```java
    @Test
    public void testBatch(){
//        批处理
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        sqlSession.getConnection();
        ArrayList<Blog> blogs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Blog blog = new Blog();
            blog.setId(i+19);
            blog.setTitle("测试"+i);
            blog.setAuthor("测试"+i);
            blog.setViews(i+19);
            blog.setCreateTime(new Date());
            blogs.add(blog);
        }
        sqlSession.insert("BlogMapper.batchInsert",blogs);

        sqlSession.commit();
        HashMap<String, Object> map = new HashMap<>();
//        map.put("title","yes");
        List<Blog> blogss = sqlSession.selectList("BlogMapper.findBlogsByBlog",map);
        for (Blog blog : blogss) {
            System.out.println("blog = " + blog);
        }
    }
```

```sh
mysql> select * from blog
    -> ;
+----+-------+---------+---------------------+-------+
| id | title | author  | create_time         | views |
+----+-------+---------+---------------------+-------+
| 1  | yes   | sas     | 2020-12-12 00:00:00 |    12 |
| 2  | ysa   | sadsada | 2020-12-12 00:00:00 |    12 |
| 19 | 测试0 | 测试0   | 2023-03-30 06:41:38 |    19 |
| 20 | 测试1 | 测试1   | 2023-03-30 06:41:38 |    20 |
| 21 | 测试2 | 测试2   | 2023-03-30 06:41:38 |    21 |
| 22 | 测试3 | 测试3   | 2023-03-30 06:41:38 |    22 |
| 23 | 测试4 | 测试4   | 2023-03-30 06:41:38 |    23 |
+----+-------+---------+---------------------+-------+
7 rows in set (0.00 sec)
```



## 4、配置解析

### 4.1、核心配置文件

* mybatis-config.xml

* MyBatis 的配置文件包含了会深深影响 MyBatis 行为的设置和属性信息。 

  ~~~
  configuration（配置）
  properties（属性）
  settings（设置）
  typeAliases（类型别名）
  typeHandlers（类型处理器）
  objectFactory（对象工厂）
  plugins（插件）
  environments（环境配置）
  environment（环境变量）
  transactionManager（事务管理器）
  dataSource（数据源）
  databaseIdProvider（数据库厂商标识）
  mappers（映射器）
  ~~~

### 4.2、环境配置（environments）

**尽管可以配置多个环境，但每个 SqlSessionFactory 实例只能选择一种环境。** 

MyBatis默认事务管理器是JDBC，连接池：POOLED

### 4.3、属性（properties）

这些属性可以在外部进行配置，并可以进行动态替换。你既可以在典型的 Java 属性文件中配置这些属性，也可以在 properties 元素的子元素中设置。

```xml
<!--引入外部配置文件-->
<properties resource="db.properties"/>
```

~~~properties
driver=com.mysql.jdbc.Driver
url=jdbc:mysql:///mybatis?useSSL=true&userUnicode=true&characterEncoding=UTF-8
username=root
password=root
~~~

* 可以直接引入文件
* 可以在其中增加配置
* 如果字段相同，优先使用外部

### 4.4、类型别名（typeAliases）

1. 类型别名可为 Java 类型设置一个缩写名字。 它仅用于 XML 配置，意在降低冗余的全限定类名书写。 
2. 也可以指定一个包名，MyBatis 会在包名下面搜索需要的 Java Bean 

~~~java
@Alias("User")
public class User {
    ...
}
~~~

~~~xml
<!--设置别名-->
<typeAliases>
    <typeAlias type="site.whatsblog.pojo.User" alias="User"/><!--1-->
    <package name="site.whatsblog.pojo"/><!--2-->
</typeAliases>
~~~

如果类少的时候，使用第一种

如果类多，使用第二种

第一种可以DIY别名，第二种不行，如果要改，使用注解或者单独设置typeAliases

#### 4.4.1、Java 类型内建的类型别名

下面是一些为常见的 Java 类型内建的类型别名。它们都是不区分大小写的，注意，为了应对原始类型的命名重复，采取了特殊的命名风格。

| 别名       | 映射的类型 |
| ---------- | ---------- |
| _byte      | byte       |
| _long      | long       |
| _short     | short      |
| _int       | int        |
| _integer   | int        |
| _double    | double     |
| _float     | float      |
| _boolean   | boolean    |
| string     | String     |
| byte       | Byte       |
| long       | Long       |
| short      | Short      |
| int        | Integer    |
| integer    | Integer    |
| double     | Double     |
| float      | Float      |
| boolean    | Boolean    |
| date       | Date       |
| decimal    | BigDecimal |
| bigdecimal | BigDecimal |
| object     | Object     |
| map        | Map        |
| hashmap    | HashMap    |
| list       | List       |
| arraylist  | ArrayList  |
| collection | Collection |
| iterator   | Iterator   |

### 4.5、设置

| cacheEnabled             | 全局性地开启或关闭所有映射器配置文件中已配置的任何缓存。     | true \| false | true  |
| ------------------------ | ------------------------------------------------------------ | ------------- | ----- |
| lazyLoadingEnabled       | 延迟加载的全局开关。当开启时，所有关联对象都会延迟加载。 特定关联关系中可通过设置 `fetchType` 属性来覆盖该项的开关状态。 | true \| false | false |
| mapUnderscoreToCamelCase | 是否开启驼峰命名自动映射，即从经典数据库列名 A_COLUMN 映射到经典 Java 属性名 aColumn。 | true \| false | False |

### 4.6、其他配置

* [类型处理器](https://mybatis.org/mybatis-3/zh/configuration.html#typeHandlers)
* [对象工厂](https://mybatis.org/mybatis-3/zh/configuration.html#objectFactory)
* plugins插件
  * [mybatis-generator-core](https://mvnrepository.com/artifact/org.mybatis.generator/mybatis-generator-core) 
  * [mybatis-plus](https://mvnrepository.com/artifact/com.baomidou/mybatis-plus) 
  * 通用mapper

### 4.7、映射器（mappers）

Mapper Registry

既然 MyBatis 的行为已经由上述元素配置完了，我们现在就要来定义 SQL 映射语句了。 但首先，我们需要告诉 MyBatis 到哪里去找到这些语句。 在自动查找资源方面，Java 并没有提供一个很好的解决方案，所以最好的办法是直接告诉 MyBatis 到哪里去找映射文件。 你可以使用相对于类路径的资源引用，或完全限定资源定位符（包括 `file:///` 形式的 URL），或类名和包名等。例如： 

```xml
<!-- 使用相对于类路径的资源引用 -->
<mappers>
  <mapper resource="org/mybatis/builder/AuthorMapper.xml"/>
  <mapper resource="org/mybatis/builder/BlogMapper.xml"/>
  <mapper resource="org/mybatis/builder/PostMapper.xml"/>
</mappers>

<!-- 使用映射器接口实现类的完全限定类名 -->
<mappers>
  <mapper class="org.mybatis.builder.AuthorMapper"/>
  <mapper class="org.mybatis.builder.BlogMapper"/>
  <mapper class="org.mybatis.builder.PostMapper"/>
</mappers>

<!-- 将包内的映射器接口实现全部注册为映射器 -->
<mappers>
  <package name="org.mybatis.builder"/>
</mappers>
```

这些配置会告诉 MyBatis 去哪里找映射文件，剩下的细节就应该是每个 SQL 映射文件了，也就是接下来我们要讨论的。 

### 4.8、作用域（Scope）和生命周期

> 不同作用域和生命周期类别是至关重要的，因为错误的使用会导致非常严重的并发问题。 

**SqlSessionFactoryBuilder**：

* 一旦创建了 SqlSessionFactory，就不再需要它了
* 局部变量

**SqlSessionFactory**：

* 一旦被创建就应该在应用的运行期间一直存在，没有任何理由丢弃它或重新创建另一个实例。（多次重建 SqlSessionFactory 被视为一种代码“坏习惯”）
* SqlSessionFactory 的最佳作用域是应用作用域 
* 最简单的就是使用单例模式或者静态单例模式 

**SqlSession**：

>  如果你现在正在使用一种 Web 框架，考虑将 SqlSession 放在一个和 HTTP 请求相似的作用域中。 换句话说，每次收到 HTTP 请求，就可以打开一个 SqlSession，返回一个响应后，就关闭它。 这个关闭操作很重要，为了确保每次都能执行关闭操作，你应该把这个关闭操作放到 finally 块中。 

* 连接到数据库的一个线程，连接到连接池的一个请求
* SqlSession 的实例不是线程安全的，因此是不能被共享的，所以它的最佳的作用域是请求或**方法作用域** 
* 用完之后赶紧关闭，否则资源会被占用

### 4.9、配置连接池 - c3p0





```java
//c3p0DataSourceFactory.java
public class c3p0DataSourceFactory extends UnpooledDataSourceFactory {
    public c3p0DataSourceFactory() {
        //this的这个是父类提供的，Combo是c3p0，就是有c3p0，其他的数据源也是继承这个Unpoll这个数据源
        this.dataSource = new ComboPooledDataSource();
    }
}
```

```xml

<dependency>
    <groupId>com.mchange</groupId>
    <artifactId>c3p0</artifactId>
    <version>0.9.5.5</version>
</dependency>
```

更改mybatis-config.xml

```xml
<dataSource type="dataSource.c3p0DataSourceFactory">
<!--                注意：这些属性的名称和c3p0的要求不符-->
                <property name="driverClass" value="com.mysql.cj.jdbc.Driver"/>
                <!--                下面的mybatis指的就是连接的哪一个database，这里是mybatis数据库-->
                <!--                <property name="url" value="jdbc:mysql:///mybatis?useSSL=true&amp;userUnicode=true&amp;characterEncoding=UTF-8"/>-->
                <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/mybatis?characterEncoding=utf8&amp;useSSL=false&amp;serverTimezone=UTC&amp;rewriteBatchedStatements=true
"/>
                <property name="user" value="root"/>
                <property name="password" value="root"/>
                <property name="initialPoolSize" value="5"/>
            </dataSource>
```

调用测试：

```java
    @Test
    public void testDynamicSql(){
//        在LabPc可以测试这个
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        sqlSession.getConnection();
        HashMap<String, Object> map = new HashMap<>();
//        map.put("title","yes");
        List<Blog> blogs = sqlSession.selectList("BlogMapper.findBlogsByBlog",map);
        for (Blog blog : blogs) {
            System.out.println("blog = " + blog);
        }
    }
```

## 6、日志



### 6.1、日志工厂

如果一个数据库操作出现了异常，我们需要拍错。所以说日志就是最好的助手！

曾经：sout，debug

现在：日志工厂

![HbBgbj.png](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/HbBgbj.png)

* SLF4J
* **LOG4J**
* LOG4J2
* JDK_LOGGING
* COMMONS_LOGGING
* **STDOUT_LOGGING**
* NO_LOGGING 

SLF4J COMMONS_LOGGING是门面，具体实现是：LOG4J，logback，jul java.util.logging 等

SLF4J Simple Logging Faceade for JAVA

logback是主流，log4j和logback同一个开发者开发的。



在Mybatis中具体使用哪个日志实现，在设置中设定！

STDOUT_LOGGING标准日志输出

~~~xml
<!--设置-->
<settings>
    <setting name="logImpl" value="STDOUT_LOGGING"/>
</settings>
~~~

### 6.2、Logback

#### 导入jar包依赖

```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.7</version>
</dependency>
```

![image-20230328203506997](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230328203506997.png)

左边可以看到自动导入了**slf4j这个门面**



之后就会自动出现运行的结果信息：

> 配置
>
> ```xml
> <!--                下面的thread对应的就是main线程  logger 是哪个类  %msg%n是具体的内容-->
>                 %d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
> ```
>
> ![image-20230328204624286](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230328204624286.png)
>
> **o.i.a.i.d就是简写**

![image-20230328203707952](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230328203707952.png)

#### 自定义输出

在resource里面Logback.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<configuration>
    <appender name = "console" class = "ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>
<!--                下面的thread对应的就是main线程  logger 是哪个类  %msg%n是具体的内容-->
                %d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
            </pattern>
        </encoder>
    </appender>
<!--    level代表了日志的输出的级别
        error 错误，
        warn 警告
        info 一般
        debug 调试信息
        trace 程序运行跟踪信息
        开发时改为debug，一般时使用info
-->
    <root level = "debug">
        <appender-ref ref = "console"/>
    </root>
</configuration>
```



### 6.3、Log4j

什么是Log4j：

* Log4j是[Apache](https://baike.baidu.com/item/Apache/8512995)的一个开源项目，通过使用Log4j，我们可以控制日志信息输送的目的地是[控制台](https://baike.baidu.com/item/%E6%8E%A7%E5%88%B6%E5%8F%B0/2438626)、文件、[GUI](https://baike.baidu.com/item/GUI)组件 
* 可以控制每一条日志的输出格式；
* 通过定义每一条日志信息的级别 ，更加细致地控制日志的生成过程 
* 通过一个[配置文件](https://baike.baidu.com/item/%E9%85%8D%E7%BD%AE%E6%96%87%E4%BB%B6/286550)来灵活地进行配置，而不需要修改应用的代码 

如何使用：

1. 先导包

   ~~~xml
   <!-- https://mvnrepository.com/artifact/log4j/log4j -->
   <dependency>
       <groupId>log4j</groupId>
       <artifactId>log4j</artifactId>
       <version>1.2.17</version>
   </dependency>
   ~~~

2. log4j.properties

   ~~~properties
   #将等级为DEBUG的日志信息输出到console和file这两个目的地，console和file的定义在下面的代码
   log4j.rootLogger=DEBUG,console,file
   
   #控制台输出的相关设置
   log4j.appender.console = org.apache.log4j.ConsoleAppender
   log4j.appender.console.Target = System.out
   log4j.appender.console.Threshold=DEBUG
   log4j.appender.console.layout = org.apache.log4j.PatternLayout
   log4j.appender.console.layout.ConversionPattern=[%c]-%m%n
   
   #文件输出的相关设置
   log4j.appender.file = org.apache.log4j.RollingFileAppender
   log4j.appender.file.File=./log/whatsblog.log
   log4j.appender.file.MaxFileSize=10mb
   log4j.appender.file.Threshold=DEBUG
   log4j.appender.file.layout=org.apache.log4j.PatternLayout
   log4j.appender.file.layout.ConversionPattern=[%p][%d{yy-MM-dd}][%c]%m%n
   
   #日志输出级别
   log4j.logger.org.mybatis=DEBUG
   log4j.logger.java.sql=DEBUG
   log4j.logger.java.sql.Statement=DEBUG
   log4j.logger.java.sql.ResultSet=DEBUG
   log4j.logger.java.sql.PreparedStatement=DEBUG
   ~~~

   

3. 配置log4j为日志的实现

   ~~~xml
   <settings>
       <setting name="logImpl" value="LOG4J"/>
   </settings>
   ~~~

   

4. 直接运行就可以

**简单使用**

1. 在需要使用log4j的类中，导包org.apache.log4j.Logger

2. 日志对象，加载参数为当前类的字节码文件

   ~~~java
   static Logger logger = Logger.getLogger(UserMapperTest.class);
   ~~~

3. 设置三种简单的提示级别

   ~~~java
   logger.info("info:进入了testLog4j");
   logger.debug("debug:进入了testLog4j");
   logger.error("error:进入了testLog4j");
   ~~~

   

4. 完毕

## 7、分页

**为什么需要分页？**

* 减少数据的处理量

### 7.1、使用limit分页

~~~sql
select * from table_name limit startindex,pagesize;
~~~

使用MyBatis实现分页

1. 接口

   ~~~java
       /**
        * 分页
        * @param map
        * @return
        */
       List<User> getUserByLimit(Map<String, Integer> map);
   ~~~

2. Mapper.xml

   ~~~xml
   <select id="getUserByLimit" parameterType="map" resultType="User">
       select * from user limit #{startIndex},#{pageSize};
   </select>
   ~~~

3. 测试

   ~~~java
   @Test
   public void testPage() {
       SqlSession session = MyBatisUtils.getSqlSession();
       UserMapper mapper = session.getMapper(UserMapper.class);
       Map<String, Integer> map = new HashMap<String, Integer>();
       map.put("startIndex",1);
       map.put("pageSize",2);
       List<User> user = mapper.getUserByLimit(map);
       System.out.println(user);
       session.close();
   }
   ~~~

### 7.2、使用RowBounds实现分页

不再使用SQL实现分页

1. 接口

   ~~~java
       /**
        * 分页2
        * @param map
        * @return
        */
       List<User> getUserByRowBounds(Map<String, Integer> map);
   ~~~

2. mapper.xml

   ~~~xml
   <select id="getUserByRowBounds" parameterType="map" resultType="User">
       select * from user
   </select>
   ~~~

3. 测试

   ~~~java
   @Test
   public void testPageRowBounds() {
       SqlSession session = MyBatisUtils.getSqlSession();
       //RowBounds实现
       RowBounds rowBounds = new RowBounds(1, 2);
       List<User> users = session.selectList("site.whatsblog.dao.UserMapper.getUserByRowBounds",null,rowBounds);
       System.out.println(users);
       session.close();
   }
   ~~~

### 7.3、分页插件 PageHelper

[PageHelper文档](https://pagehelper.github.io/docs/)

在 pom.xml 中添加PageHelper以及jsqlparser(pagehelper调用了jsqlparser，自动解析sql)依赖：

```xml
<dependency>
  <groupId>com.github.pagehelper</groupId>
  <artifactId>pagehelper</artifactId>
  <version>5.1.10</version>
</dependency>
<dependency>
  <groupId>com.github.jsqlparser</groupId>
  <artifactId>jsqlparser</artifactId>
  <version>2.0</version>
</dependency>
```

在mybatis-config.xml增加plugin配置

```xml
    <plugins>
        <plugin interceptor="com.github.pagehelper.PageInterceptor">
            <property name="helperDialect" value="mysql"/>
<!--            这里是不同的数据库，分页的方式是不同的，mysql是limit分野。所以这里设置helperDialect-->
            <property name="reasonable" value="true"/>
        </plugin>
    </plugins>
```

> 1. `reasonable`：分页合理化参数，默认值为`false`。当该参数设置为 `true` 时，`pageNum<=0` 时会查询第一页， `pageNum>pages`（超过总数时），会查询最后一页。默认`false` 时，直接根据参数进行查询。
> 2. `helperDialect`：分页插件会自动检测当前的数据库链接，自动选择合适的分页方式。 你可以配置`helperDialect`属性来指定分页插件使用哪种方言。配置时，可以使用下面的缩写值：
>    `oracle`,`mysql`,`mariadb`,`sqlite`,`hsqldb`,`postgresql`,`db2`,`sqlserver`,`informix`,`h2`,`sqlserver2012`,`derby`
>    **特别注意：**使用 SqlServer2012 数据库时，需要手动指定为 `sqlserver2012`，否则会使用 SqlServer2005 的方式进行分页。



比如设置查询的mapper：

```xml
<select id="selectPage" resultType="POJO.User">
    select * from user where id &lt; 5
</select>
```

> &lt ; 为转意符

```java
    @Test
//    测试分页查询
    public void testPageHelper(){
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.getSqlSession();
            Connection connection = sqlSession.getConnection();
            System.out.println("connection = " + connection);
            sqlSession.commit();
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            PageHelper.startPage(1,2);
//            范型指名为User对象
            Page<User> page = (Page) mapper.selectPage();
            System.out.println(page.getPages());
            List<User> users = page.getResult();
//            getResult是获取当前页面的结果
            for (User user : users) {
                System.out.println("user = " + user);
            }
        } catch (Exception e) {
            if (sqlSession!=null)
                sqlSession.rollback();
            throw new RuntimeException(e);
        }
    }
```

运行结果：

注意看他自动增加了查询，先查了`count(*)`

```sh
[UserMapper.selectPage_COUNT]-==>  Preparing: SELECT count(0) FROM user WHERE id < 5
[UserMapper.selectPage_COUNT]-==> Parameters: 
[UserMapper.selectPage_COUNT]-<==      Total: 1
[UserMapper.selectPage]-==>  Preparing: select * from user where id < 5 LIMIT ?
[UserMapper.selectPage]-==> Parameters: 2(Integer)
[UserMapper.selectPage]-<==      Total: 2
```



SQL Server 2000"

```sql
select top 3 * from table
where 
	id not in
	(select top 15 id from table);
	
```

> 就是查询不在前15条数据的前3条数据，也就是第16条数据后面的三条数据

Sql server 2012后面就是使用offest



## 8、注解开发

### 8.1、面向接口编程

- 大家之前都学过面向对象编程，也学习过接口，但在真正的开发中，很多时候我们会选择面向接口编程
- **根本原因：==解耦==，可拓展，提高复用，分层开发中，上层不用管具体的实现，大家都遵守共同的标准，使得开发变得更容易，规范性更好**
- 在一个面向对象的系统中，系统的各种功能是由许许多多的不同对象协作完成的。在这种情况下，各个对象内部是如何实现自己的，对系统设计人员来讲就不那么重要了；
- 而各个对象之前的协作关系则成为系统设计的关键，小到不同类之间的通信，大到各模块之间的交互，在系统设计之初都是要着重考虑的，这也是系统设计的主要工作内容，面向接口编程就是指按照这种思想来编程。

**关于接口的理解**

- 接口从更深层次的理解，应是定义（规范，约束）与实现（名实分离）的分离。
- 接口的本身反映了系统设计人员对系统的抽象理解。
- 接口应有两类：

  - 第一类是对一个个体的抽象，它可对应为一个抽象体(abstract class);
  - 第二类是对一个个体某一方面的抽象，即形成一个抽象面(interface)；
- 一个体有可能有多个抽象面，抽象体与抽象面是有区别的

**三个面向区别**

- 面向对象是指，我们考虑问题时，以对象为单位，考虑它的属性及方法
- 面向过程是指，我们考虑问题时，以一个具体的流程(事务过程)为单位，考虑它的实现。
- 接口设计与非接口设计是针对复用技术而言的，与面向对象(过程)不是一个问题，更多的体现就是对系统整体的架构

### 8.2、使用注解开发｜*｜

注解开发，构建DAO接口来开发。注解开发不需要Mapper文件了

1、实现interface DAO

```java
public interface blogDAO {
    @Select("select * from blog")
    public List<Blog> selectAll();
		//注解开发，select语句写在                                                                            注解里，不需要xml文件了
    @Select("select * from blog where id < #{max}")
    public List<Blog> selectAllUnder(@Param("max") int max);
    
  
  @Insert("INSERT INTO blog(id,title,author,create_time,views) values(#{id},#{title},#{author},#{createTime},#{views})")
    @SelectKey(statement = "select last_insert_id()", before = false, keyProperty = "id",resultType = Integer.class)
    public int insertBlog(Blog blog);
}
```

2、mybatis-config 配置注解开发，调用接口

```xml
    <mappers>
        <mapper resource="mappers/mapper.xml"/>
        <mapper resource="mappers/BlogMapper.xml"/>
        <mapper resource="mappers/OneToManyMapper.xml"/>
<!--&lt;!&ndash;        注解开发方法1 放一个类的名字&ndash;&gt;-->
<!--        <mapper class="DAO.blogDAO"/>-->
<!--        -->
<!--        注解开发方法2 直接放一个包名-->
        <package name="DAO"/>
    </mappers>
```

3、测试结果

```java
    @Test
    public void testAnno(){
//        测试注解开发
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        sqlSession.getConnection();
//        这里的insert select之类的都是基于xml方式的调用
        blogDAO blogdao = sqlSession.getMapper(blogDAO.class);
        List<Blog> blogs = blogdao.selectAllUnder(5);
        for (Blog blog : blogs) {
            System.out.println("blog = " + blog);
        }
    }
```



```sh
21:20:05.143 [main] DEBUG o.a.i.t.jdbc.JdbcTransaction - Setting autocommit to false on JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@5542c4ed]
21:20:05.168 [main] DEBUG DAO.blogDAO.selectAllUnder - ==>  Preparing: select * from blog where id < ?
21:20:05.206 [main] DEBUG DAO.blogDAO.selectAllUnder - ==> Parameters: 5(Integer)
21:20:05.270 [main] DEBUG DAO.blogDAO.selectAllUnder - <==      Total: 2
blog = Blog{id=1, title='yes', author='sas', createTime=null, views=12}
blog = Blog{id=2, title='ysa', author='sadsada', createTime=null, views=12}
```



z注解插入：

```java
@Test
    public void testAnno(){
//        测试注解开发
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        sqlSession.getConnection();
//        这里的insert select之类的都是基于xml方式的调用

        blogDAO blogdao = sqlSession.getMapper(blogDAO.class);
        List<Blog> blogs = blogdao.selectAllUnder(5);
        for (Blog blog : blogs) {
            System.out.println("blog = " + blog);
        }

        Blog blog = new Blog();
        blog.setId(93);
        blog.setTitle("测试");
        blog.setAuthor("测试");
        blog.setViews(19);
        blog.setCreateTime(new Date());
        int insertResult = blogdao.insertBlog(blog);
        sqlSession.commit();
        System.out.println("insertResult = " + insertResult);
    }
```

## 9、Lombook

> Project Lombok is a java library that automatically plugs into your editor and build tools, spicing up your java. Never write another getter or equals method again, with one annotation your class has a fully featured builder, Automate your logging variables, and much more. 

使用步骤：

1. 安装Lombok插件

2. 在项目中导入Lombok的jar包

   ~~~xml
   <!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
   <dependency>
       <groupId>org.projectlombok</groupId>
       <artifactId>lombok</artifactId>
       <version>1.18.20</version>
   </dependency>
   ~~~

3. 在实体类上加注解即可

   ~~~java
   @Data
   @AllArgsConstructor
   @NoArgsConstructor
   public class User {
       ...
   }
   ~~~

## 10、多对一处理association

在项目中，某些实体类之间肯定有关键关系，比如一对一，一对多等。在hibernate 中用`one to one`和`one to many`，而mybatis 中就用`association`和`collection`。
 **association:  一对一关联(has one)**
 **collection:一对多关联(has many)**
 注意，只有在做select查询时才会用到这两个标签，都有三种用法，且用法类似。

多对一：

* 多个学生对应一个老师
* 对于学生而言，要使用关联，多个学生关联一个老师**【多对一】**
* 对于老师而言，就是一对多的关系，一个老师有很多学生**【一对多】**

SQL:

~~~sql
CREATE TABLE `teacher` (
  `id` INT(10) NOT NULL,
  `name` VARCHAR(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

INSERT INTO teacher(`id`, `name`) VALUES (1, '秦老师'); 

CREATE TABLE `student` (
  `id` INT(10) NOT NULL,
  `name` VARCHAR(30) DEFAULT NULL,
  `tid` INT(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fktid` (`tid`),
  CONSTRAINT `fktid` FOREIGN KEY (`tid`) REFERENCES `teacher` (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

INSERT INTO `student` (`id`, `name`, `tid`) VALUES ('1', '小明', '1'); 
INSERT INTO `student` (`id`, `name`, `tid`) VALUES ('2', '小红', '1'); 
INSERT INTO `student` (`id`, `name`, `tid`) VALUES ('3', '小张', '1'); 
INSERT INTO `student` (`id`, `name`, `tid`) VALUES ('4', '小李', '1'); 
INSERT INTO `student` (`id`, `name`, `tid`) VALUES ('5', '小王', '1');
~~~

![HbBWan.png](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/HbBWan.png)

```java
public class Student {
    private int id;
    private String name;
    private Teacher teacher;
}
public class Teacher {
    private int id;
    private String name;
}
```

StudentMapper.java 定义了两个查询方法，基于不同的实现方式。

```java
public interface StudentMapper {
    List<Student> getStudent();
    List<Student> getStudent2();
}

public interface TeacherMapper {

}
```



### 10.1、Collection和Association

```xml
<resultMap id="TeacherAndStudents" type="Teacher">
    <result property="id" column="tid"/>
    <result property="name" column="tname"/>
    <collection property="students" ofType="Student">
        <result property="id" column="sid"/>
        <result property="name" column="sname"/>
        <result property="tid" column="stid"/>
    </collection>
</resultMap>
```



```xml
<!--复杂的属性需要单独处理，对象：association；集合：collection-->
         <!--association关联属性  property属性名 javaType属性类型 column在多的一方的表（即学生表）中的列名-->
<association property="teacher" javaType="Teacher" column="tid" select="getTeacher" />
```



而association中的结构，则和resultMap无异了，同样是id和result，但是仍然有两个需要注意的点：

- id中的column属性，其值应该尽量使用外键列名，主要是对于重名的处理，避免映射错误
- 同样的，对于result中的column属性的值，也要避免重名带来的映射错误，例如学生id和教师的id

说 association 中结构和resultMap无异，事实上我们也可以直接引用其他的resultMap

```xml
<association property="teacher" resultMap = "teacherResultMap"/>
```



### 10.2、按照Select查询嵌套处理-并不提倡

~~~xml
<!--
        思路：
            1. 查询所有学生信息
            2. 根据查询出来的学生tid，寻找对应的老师
    -->

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="StudentMapper">

    <select id="getStudent" resultMap="studentTeacher">
        select * from student
    </select>

     <resultMap id="studentTeacher" type="student">
         <id property="id" column="id" />
         <result property="name" column="name" />
         <!--复杂的属性需要单独处理，对象：association；集合：collection-->
         <!--association关联属性  property属性名 javaType属性类型 column在多的一方的表（即学生表）中的列名-->
         <association property="teacher" javaType="Teacher" column="tid" select="getTeacher" />
     </resultMap>

    <!--
       这里传递过来的id，只有一个属性的时候，下面可以写任何值
       association中column多参数配置：
       column="{key=value,key=value}"
       其实就是键值对的形式，key是传给下个sql的取值名称，value是片段一中sql查询的字段名。
   -->
     <select id="getTeacher" resultType="teacher">
         select * from teacher where id = #{tid}
     </select>

</mapper>

~~~

```java
    @Test
    public void getStudent(){
        SqlSession sqlSession = MybatisUtil.getSqlSession();

        StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
        List<Student> students = studentMapper.getStudent();
        for (Student student : students) {
            System.out.println(student);
        }
        sqlSession.close();
    }
```

```sh
Student(id=1, name=张三, teacher=Teacher(id=1, name=hresh))
Student(id=2, name=李四, teacher=Teacher(id=1, name=hresh))
Student(id=3, name=王武, teacher=Teacher(id=1, name=hresh))
Student(id=4, name=张散散, teacher=Teacher(id=1, name=hresh))

```





### 10.3、直接按照结果嵌套处理

~~~xml
<?xml version="1.0" encoding="UTF8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="OneToMany">
    <select id="getStudent" resultMap="rmStudentDetail">
        select s.id id,s.name name,t.id tid,t.name tname
        from student s,teacher t
        where s.tid = t.id
    </select>
    
    <resultMap id="rmStudentDetail" type="POJO.Student">
        <id property="id" column="id"/>
        <result property="name" column="name"/>
        <!--    这里就是和resultMap一样的，association-->
        <association property="teacher" javaType="POJO.Teacher">
            <result property="id" column="tid"/>
            <result property="name" column="tname"/>
        </association>
    </resultMap>
</mapper>
~~~

```java
    @Test
    public void testOneToMany(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        sqlSession.getConnection();
//        map.put("title","yes");
        List<Student> stus = sqlSession.selectList("OneToMany.getStudent");
        for (Student student : stus) {
            System.out.println("student = " + student);
        }
    }
```



结果：

```sh
student = Student{id=1, name='小明', teacher=Teacher{id=1, name='秦老师'}}
student = Student{id=2, name='小红', teacher=Teacher{id=1, name='秦老师'}}
student = Student{id=3, name='小张', teacher=Teacher{id=1, name='秦老师'}}
student = Student{id=4, name='小李', teacher=Teacher{id=1, name='秦老师'}}
student = Student{id=5, name='小王', teacher=Teacher{id=1, name='秦老师'}}

Process finished with exit code 0

```



### 10.4  、按照resultMap

StuentTeacher2 是一个包括了Teacher的对象

```xml

<select id="findAllStudent3" resultType="StuentTeacher2">
    select s.id sid,s.name sname,t.id tid,t.name tname
    from student s,teacher t
    where s.tid=t.id
</select>

<resultMap id="StuentTeacher2" type="Student">
    <result property="id" column="sid"/>
    <result property="name" column="sname"/>
    <!--association引用resultMap-->
    <association property="teacher" resultMap="rmStudentTeacher"/>
</resultMap>
<resultMap id = "rmStudentTeacher" type = "Teacher">
    <result property="id" column="tid"/>
    <result property="name" column="tname"/>
</resultMap>
```



## 11、一对多处理Collection

### 11.1、测试环境搭建



1. 新建实体类Teacher和student
2. 新建Mapper接口
3. 新建Mapper.xml
4. 在核心配置中绑定注册mapper接口或者文件
5. 测试查询能否成功

```java
public class Teacher {
    private int id;
    private String name;
    private List<Student> students;
}
```



### 11.2、按照查询嵌套处理

~~~xml
<select id="findTeacherById2" resultMap="TeacherAndStudents2">
    select * from teacher where id = #{id}
</select>

<resultMap id="TeacherAndStudents2" type="Teacher">
    <collection property="students" ofType="Student" column="id" select="findStudentsByTid"/>
     <!--collection调用select结果来进行查询嵌套-->
</resultMap>

<select id="findStudentsByTid" resultType="Student">
    select * from student where tid = #{id}
</select>
~~~

### 11.3、按照结果嵌套处理

~~~xml

<select id="findAllTeachers" resultType="POJO.Teacher">
    select * from teacher
</select>

<select id="findTeacherById" resultMap="TeacherAndStudents">
    select s.id sid,s.name sname,s.tid stid,t.id tid,t.name tname
    from student s,teacher t
    where s.tid=t.id and t.id = #{id}
</select>

<!-- 按结果嵌套查询 Collection放在map里面 -->
<resultMap id="TeacherAndStudents" type="Teacher">
    <result property="id" column="tid"/>
    <result property="name" column="tname"/>
    <collection property="students" ofType="Student">
        <result property="id" column="sid"/>
        <result property="name" column="sname"/>
        <result property="tid" column="stid"/>
    </collection>
</resultMap>
~~~

### 11.4、小结

1. 关联association【多对一】
2. 集合collection【一对多】
3. javaType & ofType
   1. javaType用于指定实体类中属性的类型
   2. ofType用来指定映射到List或者集合中的POJO类型，【泛型中的约束类型】

注意点：

* 保证SQL的可读性
* 注意一对多和多对一中属性名和字段的问题
* 如果问题不好排查错误，可以使用日志，建议使用Log4j

需要学习的问题

* MySQL引擎
* InnoDB底层原理
* 索引
* 索引优化！

## 12、动态SQL

> **什么是动态SQL？**
>
> ​					**动态SQL就是根据不同的条件生成不同的SQL语句**
>
> 如果你之前用过 JSTL 或任何基于类 XML 语言的文本处理器，你对动态 SQL 元素可能会感觉似曾相识。在 MyBatis 之前的版本中，需要花时间了解大量的元素。借助功能强大的基于 OGNL 的表达式，MyBatis 3 替换了之前的大部分元素，大大精简了元素种类，现在要学习的元素种类比原来的一半还要少。
>
> - if
> - choose (when, otherwise)
> - trim (where, set)
> - foreach

### 12.1、搭建环境

~~~sql
CREATE TABLE `blog`(
`id` VARCHAR(50) NOT NULL COMMENT '博客id',
`title` VARCHAR(100) NOT NULL COMMENT '博客标题',
`author` VARCHAR(30) NOT NULL COMMENT '博客作者',
`create_time` DATETIME NOT NULL COMMENT '创建时间',
`views` INT(30) NOT NULL COMMENT '浏览量'
)ENGINE=INNODB DEFAULT CHARSET=utf8;
~~~

创建一个基础工程

1. 导包

2. 编写配置文件

3. 编写实体类

   ~~~java
   @Data
   public class Blog {
       private int id;
       private String title;
       private String author;
       private Date createTime;
       private int views;
   }
   ~~~

4. 编写实体类对应的Mapper接口和Mapper.xml文件

   ~~~java
   public interface BlogMapper {
       /**
        * 插入文章
        * @param blog
        * @return
        */
       int addBlog(Blog blog);
   }
   ~~~

   ~~~xml
   <?xml version="1.0" encoding="UTF8" ?>
   <!DOCTYPE mapper
           PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
   <mapper namespace="site.whatsblog.dao.BlogMapper">
       <insert id="addBlog" parameterType="Blog">
           insert into blog (id, title, author, create_time, views)
           values (#{id}, #{title}, #{author}, #{createTime}, #{views});
       </insert>
   </mapper>
   ~~~

### 12.2、if

> *where* 元素只会在子元素返回任何内容的情况下才插入 “WHERE” 子句。而且，若子句的开头为 “AND” 或 “OR”，*where* 元素也会将它们去除。 

数据准备：

```sh
mysql> insert into blog values(1,"yes","sas","2020-12-12",12);
mysql> insert into blog values(2,"ysa","sadsada","2020-12-12",12);
mysql> select * from blog;
+----+-------+---------+---------------------+-------+
| id | title | author  | create_time         | views |
+----+-------+---------+---------------------+-------+
| 1  | yes   | sas     | 2020-12-12 00:00:00 |    12 |
| 2  | ysa   | sadsada | 2020-12-12 00:00:00 |    12 |
+----+-------+---------+---------------------+-------+
2 rows in set (0.01 sec)
```



```xml
<mappers>
    <mapper resource="mappers/mapper.xml"/>
    <mapper resource="mappers/BlogMapper.xml"/>
</mappers>
```



**！！！！注意resultType要加POJO包名**

mappers/BlogMapper.xml"

~~~xml
<?xml version="1.0" encoding="UTF8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="BlogMapper">
    <insert id="addBlog" parameterType="POJO.Blog">
        insert into blog (id, title, author, create_time, views)
        values (#{id}, #{title}, #{author}, #{createTime}, #{views});
    </insert>

    <select id="findBlogsByBlog" parameterType="map" resultType="POJO.Blog">
        select * from blog
        <where>
            <if test="title!=null">
                and title = #{title}
            </if>
            <if test="author!=null">
                and author = #{author}
            </if>
            <if test="createTime!=null">
                and create_time = #{createTime}
            </if>
            <if test="views!=null">
                and views = #{views}
            </if>
        </where>
    </select>
</mapper>
~~~

where可以保证语法正确。如果你不想加上where就得,

-- 1=1是占位符
            1=1

    <select id="findBlogsByBlog" parameterType="map" resultType="POJO.Blog">
        select * from blog
         where 1=1
            <if test="title!=null">
                and title = #{title}
            </if>
            <if test="author!=null">
                and author = #{author}
            </if>
            <if test="createTime!=null">
                and create_time = #{createTime}
            </if>
            <if test="views!=null">
                and views = #{views}
            </if>
    </select>

测试testDynamicSql：

~~~java
    @Test
    public void testDynamicSql(){
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        sqlSession.getConnection();
        HashMap<String, Object> map = new HashMap<>();
        map.put("title","yes");
        List<Blog> blogs = sqlSession.selectList("BlogMapper.findBlogsByBlog",map);
        for (Blog blog : blogs) {
            System.out.println("blog = " + blog);
        }
    }
~~~

> 注意日志文件：
>
> ```sh
> [BlogMapper.findBlogsByBlog]-==>  Preparing: select * from blog WHERE title = ?
> [BlogMapper.findBlogsByBlog]-==> Parameters: yes(String)
> [BlogMapper.findBlogsByBlog]-<==      Total: 1
> blog = Blog{id=1, title='yes', author='sas', createTime=null, views=12}
> ```
>
> 
>
> ···

### 12.3、choose、when、otherwise

> choose类似于switch，when类似于case，otherwise类似于default
>
> 只选择一个出口，只满足一个条件，假如title和author都不为空，只是用title条件，假如title都为空，就使用views

~~~xml
<select id="findBlogsChoose" parameterType="map" resultType="Blog">
    select * from blog
    <where>
        <choose>
            <when test="title!=null">
                and title = #{title}
            </when>
            <when test="author!=null">
                and author = #{author}
            </when>
            <otherwise>
                and views = #{views}
            </otherwise>
        </choose>
    </where>
</select>
~~~

### 12.4、trim、where、set

~~~xml
<update id="updateBlog" parameterType="map">
    update blog
    <set>
        <if test="title!=null">
            title = #{title},
        </if>
        <if test="author!=null">
            author = #{author},
        </if>
        <if test="createTime!=null">
            create_time = #{createTime},
        </if>
        <if test="views!=null">
            views = #{views},
        </if>
    </set>
    <where>
        <choose>
            <when test="id!=null">
                id=#{id}
            </when>
            <otherwise>
                id='0'
            </otherwise>
        </choose>
    </where>
</update>
~~~

~~~java
public void testUpdateBlog() {
    SqlSession session = MyBatisUtils.getSqlSession();
    BlogMapper mapper = session.getMapper(BlogMapper.class);
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("id","c96c5a4b431e492fb47b5b6a3feffc13");
    map.put("title","Hello World!!");
    map.put("author","Suk");
    map.put("views","1");
    int i  = 0;
    try {
        i = mapper.updateBlog(map);
        if (i>0){
            session.commit();
        }
    } catch (Exception e) {
        e.printStackTrace();
        session.rollback();
    } finally {
        session.close();
    }
    System.out.println(i);
}
~~~

*where* 元素只会在子元素返回任何内容的情况下才插入 “WHERE” 子句。而且，若子句的开头为 “AND” 或 “OR”，*where* 元素也会将它们去除。

如果 *where* 元素与你期望的不太一样，你也可以通过自定义 trim 元素来定制 *where* 元素的功能。比如，和 *where* 元素等价的自定义 trim 元素为：

```xml
<trim prefix="WHERE" prefixOverrides="AND |OR ">
  ...
</trim>
```

*prefixOverrides* 属性会忽略通过管道符分隔的文本序列（注意此例中的空格是必要的）。上述例子会移除所有 *prefixOverrides* 属性中指定的内容，并且插入 *prefix* 属性中指定的内容。

用于动态更新语句的类似解决方案叫做 *set*。*set* 元素可以用于动态包含需要更新的列，忽略其它不更新的列。比如：

```xml
<update id="updateAuthorIfNecessary">
  update Author
    <set>
      <if test="username != null">username=#{username},</if>
      <if test="password != null">password=#{password},</if>
      <if test="email != null">email=#{email},</if>
      <if test="bio != null">bio=#{bio}</if>
    </set>
  where id=#{id}
</update>
```

这个例子中，*set* 元素会动态地在行首插入 SET 关键字，并会删掉额外的逗号（这些逗号是在使用条件语句给列赋值时引入的）。

来看看与 *set* 元素等价的自定义 *trim* 元素吧：

```xml
<trim prefix="SET" suffixOverrides=",">
  ...
</trim>
```

注意，我们覆盖了后缀值设置，并且自定义了前缀值。

> 所谓的动态SQL，本质上还是SQL语句，只是我们可以在SQL层面去执行一些逻辑代码

### 12.5、SQL片段

有的时候，我们可能会将一些公共的部分抽取出来，方便复用

1. 使用sql标签抽取公共部分
2. 在需要使用的地方使用include标签引用即可

~~~xml
<sql id="cases">
    <if test="title!=null">
        and title = #{title}
    </if>
    <if test="author!=null">
        and author = #{author}
    </if>
    <if test="createTime!=null">
        and create_time = #{createTime}
    </if>
    <if test="views!=null">
        and views = #{views}
    </if>
</sql>
<select id="findBlogsByBlog" parameterType="map" resultType="Blog">
    select * from blog
    <where>
        <include refid="cases"/>
    </where>
</select>
~~~

### 12.6、foreach

动态 SQL 的另一个常见使用场景是对集合进行遍历（尤其是在构建 IN 条件语句的时候）。比如：

```xml
<select id="selectPostIn" resultType="domain.blog.Post">
  SELECT *
  FROM POST P
  <where>
    <foreach item="item" index="index" collection="list"
        open="ID in (" separator="," close=")" nullable="true">
          #{item}
    </foreach>
  </where>
</select>
```

*foreach* 元素的功能非常强大，它允许你指定一个集合，声明可以在元素体内使用的集合项（item）和索引（index）变量。它也允许你指定开头与结尾的字符串以及集合项迭代之间的分隔符。这个元素也不会错误地添加多余的分隔符，看它多智能！

提示 你可以将任何可迭代对象（如 List、Set 等）、Map 对象或者数组对象作为集合参数传递给 *foreach*。当使用可迭代对象或者数组时，index 是当前迭代的序号，item 的值是本次迭代获取到的元素。当使用 Map 对象（或者 Map.Entry 对象的集合）时，index 是键，item 是值。

> 动态SQL就是在拼接SQL语句，我们只需要保证SQL的正确性，按照SQL的格式去排列组合就可以了！

## 13、缓存

### 13.1、缓存简介

> 查询：连接数据库，耗资源
>
> 解决办法：一次查询的结果，给他暂存在一个可以取到的地方-->内存：缓存
>
> 当我们再次查询相同数据的时候，直接走缓存就不用走数据库了

1. 什么是缓存？
   * 存在内存中的临时数据
   * 将用户经常查询的诗句放在缓存中，用户查询数据就不用去磁盘上查询，从而提高查询效率，解决了高并发系统的性能问题
2. 为什么使用缓存？
   * 减少和数据库的交互次数，减少系统开销，提高系统效率
3. 什么样的数据能使用缓存？
   * 经常查询而不改变的数据

### 13.2、MyBatis缓存

* MyBatis包含一个非常强大的查询缓存特性，它可以非常方便地定制和配置缓存，缓存可以极大地提升查询销量
* MyBatis系统中默认定义了两级缓存，**一级缓存**和**二级缓存**
  * **默认情况下，只有一级缓存开启** SqlSession会话
  * 二级缓存需要手动开启和配置，基于namespace Mapper的命名空间
  * 为了提高扩展性，MyBatis定义了缓存接口Cache。我们可以通过实现Cache接口来定义二级缓存

### 13.3、一级缓存

* 一级缓存也叫本地缓存：SqlSession级别
  * 与数据库同一次会话期间查询到的护具会放在本地缓存中
  * 以后如果需要获取相同的数据，直接从缓存中拿，没必须再去查询数据库

测试步骤：

1. 开启日志

2. 测试在一个session中查询两次相同的记录

3. 查看日志输出

   ~~~properties
   com.intellij.rt.junit.JUnitStarter -ideVersion5 -junit3 site.whatsblog.dao.UserMapperTest,testFindUserById
   Logging initialized using 'class org.apache.ibatis.logging.stdout.StdOutImpl' adapter.
   Class not found: org.jboss.vfs.VFS
   JBoss 6 VFS API is not available in this environment.
   Class not found: org.jboss.vfs.VirtualFile
   VFS implementation org.apache.ibatis.io.JBoss6VFS is not valid in this environment.
   Using VFS adapter org.apache.ibatis.io.DefaultVFS
   Find JAR URL: file:/D:/Professional%20Files/Java%20Project/Mybatis-Study/Mybatis-Study/MyBatis-09/target/classes/site/whatsblog/pojo
   Not a JAR: file:/D:/Professional%20Files/Java%20Project/Mybatis-Study/Mybatis-Study/MyBatis-09/target/classes/site/whatsblog/pojo
   Reader entry: User.class
   Listing file:/D:/Professional%20Files/Java%20Project/Mybatis-Study/Mybatis-Study/MyBatis-09/target/classes/site/whatsblog/pojo
   Find JAR URL: file:/D:/Professional%20Files/Java%20Project/Mybatis-Study/Mybatis-Study/MyBatis-09/target/classes/site/whatsblog/pojo/User.class
   Not a JAR: file:/D:/Professional%20Files/Java%20Project/Mybatis-Study/Mybatis-Study/MyBatis-09/target/classes/site/whatsblog/pojo/User.class
   Reader entry: xxxxx
   Checking to see if class site.whatsblog.pojo.User matches criteria [is assignable to Object]
   PooledDataSource forcefully closed/removed all connections.
   PooledDataSource forcefully closed/removed all connections.
   PooledDataSource forcefully closed/removed all connections.
   PooledDataSource forcefully closed/removed all connections.
   Opening JDBC Connection
   Created connection 2015781843.
   Setting autocommit to false on JDBC Connection [com.mysql.jdbc.JDBC4Connection@782663d3]
   ==>  Preparing: select * from user where id = ?
   ==> Parameters: 1(Integer)
   <==    Columns: id, name, password
   <==        Row: 1, Suk1, 123456
   <==      Total: 1
   User(id=1, name=Suk1, password=123456)
   User(id=1, name=Suk1, password=123456)
   true
   Resetting autocommit to true on JDBC Connection [com.mysql.jdbc.JDBC4Connection@782663d3]
   Closing JDBC Connection [com.mysql.jdbc.JDBC4Connection@782663d3]
   Returned connection 2015781843 to pool.
   
   Process finished with exit code 0
   ~~~

缓存的生命周期：

- 映射语句文件中的所有 select 语句的结果将会被缓存。
- 映射语句文件中的**所有 insert、update 和 delete 语句会刷新缓存**。
- 缓存会使用最近最少使用算法（LRU, Least Recently Used）算法来清除不需要的缓存。
- 缓存不会定时进行刷新（也就是说，没有刷新间隔）。
- 缓存会保存列表或对象（无论查询方法返回哪种）的 1024 个引用。
- 缓存会被视为读/写缓存，这意味着获取到的对象并不是共享的，可以安全地被调用者修改，而不干扰其他调用者或线程所做的潜在修改。

缓存失效的情况：

1. 查询不同的东西

2. 增删改操作，可能会改变原来的数据

3. 查询不同的Mapper.xml

4. 手动清理缓存

   ~~~java
   sqlSession.clearCache();
   ~~~

> 一级缓存默认是开启的，只在一次sqlsession中有效，也就是拿到连接到关闭连接这个区间，也关不掉
>
> 也就是换一个sqlsession，或者说新建了一个sqlsession之后，就会重新查询。

### 13.4、二级缓存

* 二级缓存也叫全局缓存，一级缓存作用域太低了，所以产生了二级缓存
* 基于namespace级别的缓存，一个命名空间对应一个二级缓存
* 工作机制：
  * 一个会话查询一条数据，这个数据就会被放在当前会话的一级缓存中；
  * 如果当前会话关闭了，这个会话对应的一级缓存就没了，我们希望的是会话关闭，一级缓存中的数据会被保存到二级缓存中
  * 新的会话查询信息，就可以从二级缓存汇总获取内容
  * 不同的mapper查出的数据会放在自己对应的缓存中
  * 写操作commit之后就会对该namspace的缓存强制晴空
  * useCache = false 可以不用缓存
  * flushCache = true 代表强制清空缓存

默认情况下，只启用了本地的会话缓存，它仅仅对一个会话中的数据进行缓存。 要启用全局的二级缓存，只需要在你的 SQL 映射文件中添加一行：

```xml
<cache/>
```

==提示==缓存只作用于 cache 标签所在的映射文件中的语句。如果你混合使用 Java API 和 XML 映射文件，在共用接口中的语句将不会被默认缓存。你需要使用` @CacheNamespaceRef `注解指定缓存作用域。

这些属性可以通过 cache 元素的属性在`mapper.xml`中 来修改。比如：

```xml
<cache
  eviction="FIFO"
  flushInterval="60000"
  size="512"
  readOnly="true"/>
```

这个更高级的配置创建了一个 FIFO 缓存，`flushInterval`间隔 每隔 60 秒刷新，最多可以存储结果对象或列表的 512 个引用，而且返回的对象被认为是只读的，因此对它们进行修改可能会在不同线程中的调用者产生冲突。

可用的清除策略有：

- `LRU` – 最近最久最少使用：移除最长时间不被使用的对象。
- `FIFO` – 先进先出：按对象进入缓存的顺序来移除它们。
- `SOFT` – 软引用：JVM中的基于垃圾回收器状态和**软引用规则移除对象。**
- `WEAK` – 弱引用：更积极地基于垃圾收集器状态和**弱引用规则移除对象。**

默认的清除策略是 LRU。

`LFU` 最近最少使用，

flushInterval（刷新间隔）属性可以被设置为任意的正整数，设置的值应该是一个以毫秒为单位的合理时间量。 默认情况是不设置，也就是没有刷新间隔，缓存仅仅会在调用语句时刷新。

size（引用数目）属性可以被设置为任意正整数，要注意欲缓存对象的大小和运行环境中可用的内存资源。默认值是 1024。list集合看成是一个对象，一个Blog也是 一个对象，不建议对list进行二级缓存保存，list的命中率比较小

readOnly（只读）属性可以被设置为 true 或 false。只读的缓存会给所有调用者返回缓存对象的相同实例。 因此这些对象不能被修改。这就提供了可观的性能提升。而可读写的缓存会（通过序列化）返回**缓存对象的拷贝**。返回的是副本， 速度上会慢一些，但是更安全，因此默认值是 false。



```xml
<!--useCache这里表示不适用二级缓存-->
    <select id="findBlogsByBlog" parameterType="map" resultType="POJO.Blog" useCache="false">
        select * from blog
        <where>
            <if test="title!=null">
                and title = #{title}
            </if>
            <if test="author!=null">
                and author = #{author}
            </if>
            <if test="createTime!=null">
                and create_time = #{createTime}
            </if>
            <if test="views!=null">
                and views = #{views}
            </if>
        </where>
    </select>
```

```xml
<!--插入操作强制开启缓存刷新-->
    <insert id="addBlog" parameterType="POJO.Blog" flushCache="true" >
        insert into blog (id, title, author, create_time, views)
        values (#{id}, #{title}, #{author}, #{createTime}, #{views});
    </insert>
```

小结：

* 只要开启了二级缓存，在同一个Mapper下就有效
* 所有的数据都会先放在一级缓存中
* **只有当会话提交，或者关闭的时候，才会提交到二级缓存中**！

### 13.5、自定义缓存-ehcache

> Ehcache是一种广泛使用的开源Java分布式缓存。 

要在程序中使用ehcache，先要导包

在mapper的cache type属性中指定EhcacheCache

~~~xml
<?xml version="1.0" encoding="UTF-8"?>
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
         updateCheck="false">

    <diskStore path="./tmpdir/Tmp_EhCache"/>

    <defaultCache
            eternal="false"
            maxElementsInMemory="10000"
            overflowToDisk="false"
            diskPersistent="false"
            timeToIdleSeconds="1800"
            timeToLiveSeconds="259200"
            memoryStoreEvictionPolicy="LRU"/>

    <cache
            name="cloud_user"
            eternal="false"
            maxElementsInMemory="5000"
            overflowToDisk="false"
            diskPersistent="false"
            timeToIdleSeconds="1800"
            timeToLiveSeconds="1800"
            memoryStoreEvictionPolicy="LRU"/>
</ehcache>
<!--
        diskStore
        diskStore元素：制定一个路径，当EHCache把数据写到硬盘上的时候，
        会把数据写到该目录下。user.home - 用户主目录；user.dir - 用户当前工作目录；
        java.io.tmpdir - 默认临时文件路径。

        defaultCache
        设定缓存的默认数据过期策略。

        cache
        设定具体的命名缓存的数据过期策略。

        name
        缓存名称。通常为缓存对象的类名；

        maxElementsInMemory
        设置基于内存的缓存可存放对象的最大数目；

        maxElementOnDisk
        设置基于硬盘的缓存可存放对象的最大数目；

        eternal
        如果为true，表示对象永远不会过期，此时会忽略tiemToldleSeconds和timeToLiveSeconds属性
        默认为false。

        timeToldleSeconds
        设置允许对象处于空闲状态的最长时间，以秒为单位。
        当对象最近一次被访问后，如果处于空闲状态的时间超过了
        timeToldleSeconds属性值，这个对象就会过期。
        当对象过期，EHCache将把它从缓存中清空。只有当eternal属性为false.
        该属性才有效。如果该属性的值为0，那么就表示该对象可以无限期地存于缓存中。
        即缓存被创建后，最后一次访问时间到缓存失效之时，两者之间的间隔，单位为秒(s)

        timeToLiveSeconds
        必须大于timeToldleSeconds属性，才有意义；
        当对象自从被存放到缓存中后，如果处于缓存中的时间超过了 timeToLiveSeconds属性值,
        这个对象就会过期，EHCache将把它从缓存中清除；
        即缓存自创建日期起能够存活的最长时间，单位为秒(s)

        overflowToDisk
        如果为true，表示当基于内存的缓存中的对象数目达到了maxElementsInMemory界限后
        会把溢出的对象写到基于硬盘的缓存中。
        注意，如果缓存的对象要写入到硬盘中的话，则该对象必须实现了Serializable接口才行（也就是序列化）；

        memoryStoreEvictionPolicy
        缓存对象清除策略。
        有三种：

        FIFO：first in first out
        先进先出。

        LFU：Less Frequently Used
        一直以来最少被使用策略，缓存元素有一个hit属性，hit(命中)值最小的将会被清除出缓存。

        LRU：least Recenly used
        最近最少被使用，缓存的元素有一个时间戳，当缓存的容量满了，
        而又需要腾出地方来缓存新的元素的时候，
        那么现有缓存元素中时间戳离当前时间最远的元素将被清出缓存。

        diskSpoolBufferSizeMB
        写入磁盘的缓冲区大小。
        由于diskSpoolBufferSizeMB在内部实际是以字节为单位，
        所以最大值是Integer的最大值即2047.99…M，反正不到2G。
        所以如果配置的超过2G，将会导致diskSpoolBufferSizeMB为负数，
        在put时ehcache误以为磁盘缓存队列已满，每次都执行都会阻塞。

        maxElementsOnDisk
        在DiskStore(磁盘存储)中的最大对象数量，如为0，则没有限制

        diskPersistent
        是否disk store在虚拟机启动时持久化。默认为false

        diskExpiryThreadIntervalSeconds
        Ehcache后台线程专门做Ellment失效监测以及清除工作。
        此值不宜设置过低，否则会导致清理线程占用大量CPU资源。
        默认值是120秒。

        clearOnFlush
        当调用flush()是否清除缓存，默认是。

        maxEntriesLocalHeap
        堆内存中最大缓存对象数，0没有限制

        defaultCache说明
        defaultCache是ehcache中系统自带的一个默认cache，
        其name默认是"default" 并且，手动创建（或者读取使用）"default"的cache时会报错，
        其作用是用于在程序中创建新的cache时，可以使用defaultCache中的默认配置。
        即用addCache时会默认使用defaultcache中的配置.
        所以xml其余的cache配置不会继承defaultCache的配置-->
~~~
