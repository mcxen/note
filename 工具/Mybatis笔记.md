# MyBatis-2.14

[mybatis – MyBatis 3 | 简介](https://mybatis.org/mybatis-3/zh/index.html) 


环境：

* JDK 1.8
* MySQL 5.7
* maven 3.6.1
* IDEA

回顾：

* JDBC
* MySQL
* Java SE
* Maven
* Junit

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
  <mapper namespace="User">
      <select id="selectAll" resultType="User">
          select *
          from user
      </select>
    	
      <select id="selectByid" resultType="User" parameterType="Integer">
          select *
          from user
          where id = #{id}
      </select>
  
      <insert id="addUser" parameterType="User">
          insert into user(name, password)
          values (#{name}, #{password})
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
  
  
  
  调用就是：
  
  ```java
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
  
              User user2 = sqlSession.selectOne("User.selectByid",2);
              //上面是带参数查询
  
  //            for (User user1 : user) {
  //                System.out.println("user1 = " + user1);
  //            }
              System.out.println(user2);
          } catch (Exception e) {
              throw new RuntimeException(e);
          } finally {
              MyBatisUtils.closeSession(sqlSession);
          }
  //
      }
  ```

#### ```#{}```和```${}```的区别是什么？

在Mybatis中，有两种占位符

- ```#{}```解析传递进来的参数数据
- ```${}```对传递进来的参数原样拼接在SQL中
- ```#{}```是预编译处理，```${}```是字符串替换。
- 使用```#{}```可以有效的防止SQL注入，提高系统安全性。



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

#### DTO数据传输对象



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

## 5、解决属性名和字段名不一致的问题（ResultMap）

数据库中的字段

![HbBRVs.png](https://s4.ax1x.com/2022/02/19/HbBRVs.png)

解决方法：

1. 起别名

2. ResultMap

   ~~~xml
   <resultMap id="uu" type="User">
       <id property="id" column="id" />
       <result property="username" column="name"/>
       <result property="password" column="password"/>
   </resultMap>
   ~~~

   * `resultMap` 元素是 MyBatis 中最重要最强大的元素。
   *  ResultMap 的设计思想是，对简单的语句做到零配置，对于复杂一点的语句，只需要描述语句之间的关系就行了。 
   * 只需要指定property和column不一样的部分，其他部分不需要指定
   * 如果这个世界总是这么简单就好了。 

## 6、日志

### 6.1、日志工厂

如果一个数据库操作出现了异常，我们需要拍错。所以说日志就是最好的助手！

曾经：sout，debug

现在：日志工厂

![HbBgbj.png](https://s4.ax1x.com/2022/02/19/HbBgbj.png)

* SLF4J
* **LOG4J**
* LOG4J2
* JDK_LOGGING
* COMMONS_LOGGING
* **STDOUT_LOGGING**
* NO_LOGGING 

在Mybatis中具体使用哪个日志实现，在设置中设定！

STDOUT_LOGGING标准日志输出

~~~xml
<!--设置-->
<settings>
    <setting name="logImpl" value="STDOUT_LOGGING"/>
</settings>
~~~

### 6.2、Log4j

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

### 7.3、分页插件

[PageHelper文档](https://pagehelper.github.io/docs/)

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

### 8.2、使用注解开发

1. 注解直接在接口的方法上实现

   ~~~java
   @Select("select * from user")
   List<User> getUsers();
   ~~~

2. 绑定接口

   ~~~xml
   <mappers>
       <mapper class="site.whatsblog.dao.UserMapper"/>
   </mappers>
   ~~~

3. 测试

本质：反射机制

底层：动态代理

需要知道：**MyBatis详细的执行流程！**

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

## 10、多对一处理

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
) ENGINE=INNODB DEFAULT CHARSET=utf8

INSERT INTO teacher(`id`, `name`) VALUES (1, '秦老师'); 

CREATE TABLE `student` (
  `id` INT(10) NOT NULL,
  `name` VARCHAR(30) DEFAULT NULL,
  `tid` INT(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fktid` (`tid`),
  CONSTRAINT `fktid` FOREIGN KEY (`tid`) REFERENCES `teacher` (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8INSERT INTO `student` (`id`, `name`, `tid`) VALUES ('1', '小明', '1'); 
INSERT INTO `student` (`id`, `name`, `tid`) VALUES ('2', '小红', '1'); 
INSERT INTO `student` (`id`, `name`, `tid`) VALUES ('3', '小张', '1'); 
INSERT INTO `student` (`id`, `name`, `tid`) VALUES ('4', '小李', '1'); 
INSERT INTO `student` (`id`, `name`, `tid`) VALUES ('5', '小王', '1');
~~~

![HbBWan.png](https://s4.ax1x.com/2022/02/19/HbBWan.png)

### 10.1、测试环境搭建

1. 导入lombok
2. 新建实体类Teacher和student
3. 新建Mapper接口
4. 新建Mapper.xml
5. 在核心配置中绑定注册mapper接口或者文件
6. 测试查询能否成功

### 10.2、按照查询嵌套处理

~~~xml
<!--
        思路：
            1. 查询所有学生信息
            2. 根据查询出来的学生tid，寻找对应的老师
    -->
<select id="findAllStudent" resultMap="StuentTeacher">
    select * from student
</select>

<resultMap id="StuentTeacher" type="Student">
    <id property="id" column="id"/>
    <id property="name" column="name"/>
    <!--
            复杂的属性，我们需要单独处理
                对象：association
                集合：collection
        -->
    <association property="teacher" column="tid" javaType="Teacher" select="findTeacherByTid"/>
</resultMap>

<select id="findTeacherByTid" resultType="Teacher">
    select * from teacher where id = #{id}
</select>
~~~

### 10.3、按照结果嵌套处理

~~~xml
<!--按照结果嵌套处理-->
<select id="findAllStudent2" resultType="StuentTeacher2">
    select s.id sid,s.name sname,t.id tid,t.name tname
    from student s,teacher t
    where s.tid=t.id
</select>

<resultMap id="StuentTeacher2" type="Student">
    <result property="id" column="sid"/>
    <result property="name" column="sname"/>
    <association property="teacher" javaType="Teacher">
        <result property="id" column="tid"/>
        <result property="name" column="tname"/>
    </association>
</resultMap>
~~~

## 11、一对多处理

### 11.1、测试环境搭建

1. 导入lombok
2. 新建实体类Teacher和student
3. 新建Mapper接口
4. 新建Mapper.xml
5. 在核心配置中绑定注册mapper接口或者文件
6. 测试查询能否成功

### 11.2、按照查询嵌套处理

~~~xml
<select id="findTeacherById2" resultMap="TeacherAndStudents2">
    select * from teacher where id = #{id}
</select>
<resultMap id="TeacherAndStudents2" type="Teacher">
    <collection property="students" ofType="Student" column="id" select="findStudentsByTid"/>
</resultMap>
<select id="findStudentsByTid" resultType="Student">
    select * from student where tid = #{id}
</select>
~~~

### 11.3、按照结果嵌套处理

~~~xml
<!-- 按结果嵌套查询 -->
<select id="findAllTeachers" resultType="Teacher">
    select * from teacher
</select>

<select id="findTeacherById" resultMap="TeacherAndStudents">
    select s.id sid,s.name sname,s.tid stid,t.id tid,t.name tname
    from student s,teacher t
    where s.tid=t.id and t.id = #{id}
</select>

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

5. 测试

   ~~~java
   public void testAddBlog() {
       SqlSession session = MyBatisUtils.getSqlSession();
       BlogMapper mapper = session.getMapper(BlogMapper.class);
       try {
           mapper.addBlog(new Blog(IDUtils.getUUID(),"MyBatis第一天!","Suk",new Date(),0));
           mapper.addBlog(new Blog(IDUtils.getUUID(),"MyBatis第二天!","Suk",new Date(),0));
           mapper.addBlog(new Blog(IDUtils.getUUID(),"MyBatis第三天!","Suk",new Date(),0));
           mapper.addBlog(new Blog(IDUtils.getUUID(),"MyBatis第四天!","Suk",new Date(),0));
           session.commit();
       } catch (Exception e) {
           e.printStackTrace();
           session.rollback();
       }finally {
           session.close();
       }
   }
   ~~~

### 12.2、if

> *where* 元素只会在子元素返回任何内容的情况下才插入 “WHERE” 子句。而且，若子句的开头为 “AND” 或 “OR”，*where* 元素也会将它们去除。 

~~~xml
<select id="findBlogsByBlog" parameterType="map" resultType="Blog">
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
~~~

~~~java
public void testFindTest() {
    SqlSession session = MyBatisUtils.getSqlSession();
    BlogMapper mapper = session.getMapper(BlogMapper.class);
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("title","MyBatis第二天!");
    map.put("author","Suk");

    List<Blog> blogs = mapper.findBlogsByBlog(map);
    for (Blog blog : blogs) {
        System.out.println(blog);
    }
}
~~~

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
  * 默认情况下，只有一级缓存开启
  * 二级缓存需要手动开启和配置，基于namespace
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

### 13.4、二级缓存

* 二级缓存也叫全局缓存，一级缓存作用域太低了，所以产生了二级缓存
* 基于namespace级别的缓存，一个命名空间对应一个二级缓存
* 工作机制：
  * 一个会话查询一条数据，这个数据就会被放在当前会话的一级缓存中；
  * 如果当前会话关闭了，这个会话对应的一级缓存就没了，我们希望的是会话关闭，一级缓存中的数据会被保存到二级缓存中
  * 新的会话查询信息，就可以从二级缓存汇总获取内容
  * 不同的mapper查出的数据会放在自己对应的缓存中

默认情况下，只启用了本地的会话缓存，它仅仅对一个会话中的数据进行缓存。 要启用全局的二级缓存，只需要在你的 SQL 映射文件中添加一行：

```xml
<cache/>
```

==提示==缓存只作用于 cache 标签所在的映射文件中的语句。如果你混合使用 Java API 和 XML 映射文件，在共用接口中的语句将不会被默认缓存。你需要使用` @CacheNamespaceRef `注解指定缓存作用域。

这些属性可以通过 cache 元素的属性来修改。比如：

```xml
<cache
  eviction="FIFO"
  flushInterval="60000"
  size="512"
  readOnly="true"/>
```

这个更高级的配置创建了一个 FIFO 缓存，每隔 60 秒刷新，最多可以存储结果对象或列表的 512 个引用，而且返回的对象被认为是只读的，因此对它们进行修改可能会在不同线程中的调用者产生冲突。

可用的清除策略有：

- `LRU` – 最近最少使用：移除最长时间不被使用的对象。
- `FIFO` – 先进先出：按对象进入缓存的顺序来移除它们。
- `SOFT` – 软引用：基于垃圾回收器状态和软引用规则移除对象。
- `WEAK` – 弱引用：更积极地基于垃圾收集器状态和弱引用规则移除对象。

默认的清除策略是 LRU。

flushInterval（刷新间隔）属性可以被设置为任意的正整数，设置的值应该是一个以毫秒为单位的合理时间量。 默认情况是不设置，也就是没有刷新间隔，缓存仅仅会在调用语句时刷新。

size（引用数目）属性可以被设置为任意正整数，要注意欲缓存对象的大小和运行环境中可用的内存资源。默认值是 1024。

readOnly（只读）属性可以被设置为 true 或 false。只读的缓存会给所有调用者返回缓存对象的相同实例。 因此这些对象不能被修改。这就提供了可观的性能提升。而可读写的缓存会（通过序列化）返回缓存对象的拷贝。 速度上会慢一些，但是更安全，因此默认值是 false。

步骤：

1. 开启全局缓存

   ~~~xml
   <!--显式开启缓存-->
   <setting name="cacheEnabled" value="true"/>
   ~~~

2. 在Mapper.xml中加入cache标签

3. 测试

   ~~~java
   public void testUpdateUser() {
       SqlSession sqlSession = MyBatisUtils.getSqlSession();
       UserMapper mapper = sqlSession.getMapper(UserMapper.class);
       User user = mapper.findUserById(1);
       System.out.println(user);
       sqlSession.close();
       SqlSession sqlSession2 = MyBatisUtils.getSqlSession();
       UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);
       User user2 = mapper2.findUserById(1);
       System.out.println(user2);
       System.out.println(user==user2);
       sqlSession2.close();
   }
   ~~~

输出：

~~~verilog
......
Cache Hit Ratio [site.whatsblog.dao.UserMapper]: 0.5
User(id=1, name=Suk1, password=123456)
true

Process finished with exit code 0

~~~

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
