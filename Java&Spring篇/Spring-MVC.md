# Spring-MVC



<img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/mcxen/note">



# 一、SpringMVC简介

### 1、什么是MVC

MVC是一种软件架构的思想，将软件按照模型、视图、控制器来划分

M：Model，模型层，指工程中的JavaBean，作用是处理数据

JavaBean分为两类：

- 一类称为实体类Bean：专门存储业务数据的，如 Student、User 等
- 一类称为业务处理 Bean：指 Service 或 Dao 对象，专门用于处理业务逻辑和数据访问。

V：View，视图层，指工程中的html或jsp等页面，作用是与用户进行交互，展示数据

C：Controller，控制层，指工程中的servlet，作用是接收请求和响应浏览器

MVC的工作流程：
用户通过视图层发送请求到服务器，在服务器中请求被Controller接收，Controller调用相应的Model层处理请求，处理完毕将结果返回到Controller，Controller再根据请求处理的结果找到相应的View视图，渲染数据后最终响应给浏览器

### 2、什么是SpringMVC

SpringMVC是Spring的一个后续产品，是Spring的一个子项目

SpringMVC 是 Spring 为表述层开发提供的一整套完备的解决方案。在表述层框架历经 Strust、WebWork、Strust2 等诸多产品的历代更迭之后，目前业界普遍选择了 SpringMVC 作为 Java EE 项目表述层开发的**首选方案**。

> 注：三层架构分为表述层（或表示层）、业务逻辑层、数据访问层，表述层表示前台页面和后台servlet
>
> VIEW，controller，Model
>
> ![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAU3NobV82NjY=,size_20,color_FFFFFF,t_70,g_se,x_16.png)
>
> 
>
> **Spring MVC 官方给出的执行过程**
>
> ![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAU3NobV82NjY=,size_20,color_FFFFFF,t_70,g_se,x_16-20230418210806998.png)



### 3、SpringMVC的特点

- **Spring 家族原生产品**，与 IOC 容器等基础设施无缝对接
- **基于原生的Servlet**，通过了功能强大的**前端控制器DispatcherServlet**，对请求和响应进行统一处理
- 表述层各细分领域需要解决的问题**全方位覆盖**，提供**全面解决方案**
- **代码清新简洁**，大幅度提升开发效率
- 内部组件化程度高，可插拔式组件**即插即用**，想要什么功能配置相应组件即可
- **性能卓著**，尤其适合现代大型、超大型互联网项目要求

# 二、HelloWorld

### 1、开发环境

IDE：idea 2019.2

构建工具：maven3.5.4

服务器：tomcat7

Spring版本：5.3.1

### 2、创建maven工程

##### a>添加web模块

##### b>打包方式：war

##### c>引入依赖

```xml
<dependencies>
    <!-- SpringMVC -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId>
        <version>5.3.1</version>
    </dependency>

    <!-- 日志 -->
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.2.3</version>
    </dependency>

    <!-- ServletAPI -->
    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>javax.servlet-api</artifactId>
        <version>3.1.0</version>
        <scope>provided</scope>
    </dependency>

    <!-- Spring5和Thymeleaf整合包 -->
    <dependency>
        <groupId>org.thymeleaf</groupId>
        <artifactId>thymeleaf-spring5</artifactId>
        <version>3.0.12.RELEASE</version>
    </dependency>
</dependencies>
123456789101112131415161718192021222324252627282930
```

注：由于 Maven 的传递性，我们不必将所有需要的包全部配置依赖，而是配置最顶端的依赖，其他靠传递性导入。

还挺神奇的，导入一个mvc的包，这里面jar包就都导入成功了。

![image-20230418220704235](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230418220704235.png)



### 3、配置web.xml

注册SpringMVC的前端控制器DispatcherServlet

##### a>默认配置方式web.xml

此配置作用下，SpringMVC的配置文件默认位于WEB-INF下，默认名称为web.xml，例如，以下配置所对应SpringMVC的配置文件位于WEB-INF下，文件名为web.xml

```xml
<!-- 配置SpringMVC的前端控制器，对浏览器发送的请求统一进行处理 -->
<servlet>
    <servlet-name>springMVC</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>springMVC</servlet-name>
    <!--
        设置springMVC的核心控制器所能处理的请求的请求路径
        /所匹配的请求可以是/login或.html或.js或.css方式的请求路径
        但是/不能匹配.jsp请求路径的请求
    -->
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

##### b>扩展配置方式web.xml

可通过init-param标签设置SpringMVC配置文件的位置和名称，通过load-on-startup标签设置SpringMVC前端控制器DispatcherServlet的初始化时间

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">
    <!-- 配置SpringMVC的前端控制器，对浏览器发送的请求统一进行处理 -->
    <servlet>
        <servlet-name>springMVC</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <!-- 通过初始化参数指定SpringMVC配置文件的位置和名称 -->
        <init-param>
            <!-- contextConfigLocation为固定值 -->
            <param-name>contextConfigLocation</param-name>
            <!-- 使用classpath:表示从类路径查找配置文件，例如maven工程中的src/main/resources -->
            <param-value>classpath:applicationContext.xml</param-value>
        </init-param>
        <!--
             作为框架的核心组件，在启动过程中有大量的初始化操作要做
            而这些操作放在第一次请求时才执行会严重影响访问速度
            因此需要通过此标签将启动控制DispatcherServlet的初始化时间提前到服务器启动时
        -->
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>springMVC</servlet-name>
        <!--
            设置springMVC的核心控制器所能处理的请求的请求路径
            /所匹配的请求可以是/login或.html或.js或.css方式的请求路径
            但是/不能匹配.jsp请求路径的请求
        -->
        <url-pattern>/</url-pattern>
    </servlet-mapping>

    <!--配置springMVC的编码过滤器-->
    <filter>
        <filter-name>CharacterEncodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
        <init-param>
            <param-name>forceResponseEncoding</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>CharacterEncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

</web-app>
```

> 注：
>
> <url-pattern>标签中使用/和/*的区别：
>
> /所匹配的请求可以是/login或.html或.js或.css方式的请求路径，但是/不能匹配.jsp请求路径的请求
>
> 因此就可以避免在访问jsp页面时，该请求被DispatcherServlet处理，从而找不到相应的页面
>
> /*则能够匹配所有请求，例如在使用过滤器时，若需要对所有请求进行过滤，就需要使用/*的写法



### 4、创建请求控制器

由于前端控制器对浏览器发送的请求进行了统一的处理，但是具体的请求有不同的处理过程，因此需要创建处理具体请求的类，即请求控制器

请求控制器中每一个处理请求的方法成为控制器方法

因为SpringMVC的控制器由一个POJO（普通的Java类）担任，因此需要通过@Controller注解将其标识为一个控制层组件，交给Spring的IoC容器管理，此时SpringMVC才能够识别控制器的存在

```java
package mcxgroup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
    @GetMapping("/t") //localhost/t
    @ResponseBody //直接相应输出字符串数据，不跳转数据
    public String test(){
        return "SUCCESS";
    }
}


```

### 5、创建springMVC的配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/aop
       https://www.springframework.org/schema/aop/spring-aop.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/mvc
       http://www.springframework.org/schema/mvc/spring-mvc.xsd">
    <context:component-scan base-package="mcxgroup"></context:component-scan>
    <mvc:annotation-driven>
        <mvc:message-converters>
            <bean class="org.springframework.http.converter.StringHttpMessageConverter">
                <!--                对相应的文本消息转换-->
                <property name="supportedMediaTypes">
                    <list>
                        <!--                        在servlet里面这么设置的
                                                    response.setContentType-->
                        <value>text/html;charset=utf-8</value>
                    </list>
                </property>
            </bean>
        </mvc:message-converters>
    </mvc:annotation-driven>
    <!--    启动mvc注解开发-->
    <mvc:default-servlet-handler/>
    <!--    这一个是排除静态资源的处理，也就是图片或者视频之类的。-->
</beans>
```

### 6、测试HelloWorld

##### a>实现对t的访问

在请求控制器中创建处理请求的方法

```java
package mcxgroup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
    @GetMapping("/t") //localhost/t
    @ResponseBody //直接相应输出字符串数据，不跳转数据
    public String test(){
        return "SUCCESS";
    }
}

```

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230418222503759.png" alt="image-20230418222503759" style="zoom:50%;" />



>  运行前记得调整tomcat。这里好像是要在下面选择jar包，不要直接选这个文件夹，
>
> ![image-20230418222749238](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230418222749238.png)







### 7、总结

浏览器发送请求，若请求地址符合前端控制器的`url-pattern`:t，该请求就会被前端控制器`DispatcherServlet`处理。前端控制器会读取SpringMVC的核心配置文件，通过扫描组件找到控制器，将请求地址和控制器中`@GetMapping`注解的value属性值进行匹配，若匹配成功，该注解所标识的控制器方法就是处理请求的方法。处理请求的方法需要返回一个字符串。

> 注：
>
> 1、对于处理指定请求方式的控制器方法，SpringMVC中提供了@RequestMapping的派生注解
>
> 处理get请求的映射–>@GetMapping
>
> 处理post请求的映射–>@PostMapping
>
> 处理put请求的映射–>@PutMapping
>
> 处理delete请求的映射–>@DeleteMapping
>
> 2、常用的请求方式有get，post，put，delete
>
> 但是目前浏览器只支持get和post，若在form表单提交时，为method设置了其他请求方式的字符串（put或delete），则按照默认的请求方式get处理
>
> 若要发送put和delete请求，则需要通过spring提供的过滤器HiddenHttpMethodFilter，在RESTful部分会讲到

# 三、@RequestMapping注解

@RequestMapping进行数据绑定。

URL Mapping，Url与Controller方法绑定，

### 1、@RequestMapping注解的功能

从注解名称上我们可以看到，@RequestMapping注解的作用就是将请求和处理请求的控制器方法关联起来，建立映射关系。

SpringMVC 接收到指定的请求，就会来找到在映射关系中对应的控制器方法来处理这个请求。

### 2、@RequestMapping注解的位置

@RequestMapping标识一个类：设置映射请求的请求路径的初始信息，相当于加一个前缀

> ```java
> @RequestMapping("/rm")
> @Controller
> public class TestController {
>     @GetMapping("/t") //localhost/t
>     @ResponseBody //直接相应输出字符串数据，不跳转数据
>     public String test(){
>         return "SUCCESS";
>     }
> 
>     @PostMapping("/p")
>     @ResponseBody
>     public String postMapping(){
>         return "This is Post";
>     }
> }
> ```
>
> <img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230419212711933.png" alt="image-20230419212711933" style="zoom:50%;" />

@RequestMapping标识一个方法：设置映射请求请求路径的具体信息，**不再区分Get还是Post请求了**



```java
@Controller
@RequestMapping("/test")
public class RequestMappingController {

	//此时请求映射所映射的请求的请求路径为：/test/testRequestMapping
    @RequestMapping("/testRequestMapping")
    public String testRequestMapping(){
        return "success";
    }

}
1234567891011
```

### 3、@RequestMapping注解的value属性 - 设定地址映射

@RequestMapping注解的value属性通过请求的请求地址匹配请求映射

@RequestMapping注解的value属性是一个字符串类型的数组，表示该请求映射能够匹配多个请求地址所对应的请求

@RequestMapping注解的value属性必须设置，至少通过请求地址匹配请求映射

```html
<a th:href="@{/testRequestMapping}">测试@RequestMapping的value属性-->/testRequestMapping</a><br>
<a th:href="@{/test}">测试@RequestMapping的value属性-->/test</a><br>
12
@RequestMapping(
        value = {"/testRequestMapping", "/test"}
)
public String testRequestMapping(){
    return "success";
}
123456
```

### 4、@RequestMapping注解的method属性 - 设定为Post还是Get请求

@RequestMapping注解的method属性**通过请求的请求方式（get或post）匹配请求映射**，这个太麻烦了，我们还是使用GetMapping和PostMapping方便

@RequestMapping注解的method属性是一个RequestMethod类型的数组，表示该请求映射能够匹配多种请求方式的请求

若当前请求的请求地址满足请求映射的value属性，但是请求方式不满足method属性，则浏览器报错405：Request method ‘POST’ not supported

```html
<a th:href="@{/test}">测试@RequestMapping的value属性-->/test</a><br>
<form th:action="@{/test}" method="post">
    <input type="submit">
</form>
1234
@RequestMapping(
        value = {"/testRequestMapping", "/test"},
        method = {RequestMethod.GET, RequestMethod.POST}
)
public String testRequestMapping(){
    return "success";
}
1234567
```



### 5、@RequestMapping注解的params属性 - 获取请求参数



@RequestMapping注解的params属性通过请求的请求参数匹配请求映射

@RequestMapping注解的params属性是一个字符串类型的数组，可以通过四种表达式设置请求参数和请求映射的匹配关系

“param”：要求请求映射所匹配的请求必须携带param请求参数

“!param”：要求请求映射所匹配的请求必须不能携带param请求参数

“param=value”：要求请求映射所匹配的请求必须携带param请求参数且param=value

“param!=value”：要求请求映射所匹配的请求必须携带param请求参数但是param!=value

```html
<a th:href="@{/test(username='admin',password=123456)">测试@RequestMapping的params属性-->/test</a><br>
1
@RequestMapping(
        value = {"/testRequestMapping", "/test"}
        ,method = {RequestMethod.GET, RequestMethod.POST}
        ,params = {"username","password!=123456"}
)
public String testRequestMapping(){
    return "success";
}
12345678
```

> 注：
>
> 若当前请求满足@RequestMapping注解的value和method属性，但是不满足params属性，此时页面回报错400：Parameter conditions “username, password!=123456” not met for actual request parameters: username={admin}, password={123456}

### 6、@RequestMapping注解的headers属性（了解）

@RequestMapping注解的headers属性通过请求的请求头信息匹配请求映射

@RequestMapping注解的headers属性是一个字符串类型的数组，可以通过四种表达式设置请求头信息和请求映射的匹配关系

“header”：要求请求映射所匹配的请求必须携带header请求头信息

“!header”：要求请求映射所匹配的请求必须不能携带header请求头信息

“header=value”：要求请求映射所匹配的请求必须携带header请求头信息且header=value

“header!=value”：要求请求映射所匹配的请求必须携带header请求头信息且header!=value

若当前请求满足@RequestMapping注解的value和method属性，但是不满足headers属性，此时页面显示404错误，即资源未找到

### 7、SpringMVC支持ant风格的路径

？：表示任意的单个字符

*：表示任意的0个或多个字符

**：表示任意的一层或多层目录

注意：在使用**时，只能使用/**/xxx的方式

### 8、SpringMVC支持路径中的占位符（重点）

原始方式：/deleteUser?id=1

rest方式：/deleteUser/1

SpringMVC路径中的占位符常用于RESTful风格中，当请求路径中将某些数据通过路径的方式传输到服务器中，就可以在相应的@RequestMapping注解的value属性中通过占位符{xxx}表示传输的数据，在通过@PathVariable注解，将占位符所表示的数据赋值给控制器方法的形参

```html
<a th:href="@{/testRest/1/admin}">测试路径中的占位符-->/testRest</a><br>
1
@RequestMapping("/testRest/{id}/{username}")
public String testRest(@PathVariable("id") String id, @PathVariable("username") String username){
    System.out.println("id:"+id+",username:"+username);
    return "success";
}
//最终输出的内容为-->id:1,username:admin
123456
```

# 四、SpringMVC获取请求参数

### 1、通过ServletAPI获取

将HttpServletRequest作为控制器方法的形参，此时HttpServletRequest类型的参数表示封装了当前请求的请求报文的对象

```java
@RequestMapping("/testParam")
public String testParam(HttpServletRequest request){
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    System.out.println("username:"+username+",password:"+password);
    return "success";
}
1234567
```

### 2、通过控制器COntroller 方法的形参获取请求参数





在控制器方法的形参位置，设置和请求参数同名的形参，当浏览器发送请求，匹配到请求映射时，在DispatcherServlet中就会将请求参数赋值给相应的形参

```html
<a th:href="@{/testParam(username='admin',password=123456)}">测试获取请求参数-->/testParam</a><br>
1
@RequestMapping("/testParam")
public String testParam(String username, String password){
    System.out.println("username:"+username+",password:"+password);
    return "success";
}
12345
```

> 注：
>
> 若请求所传输的请求参数中有多个同名的请求参数，此时可以在控制器方法的形参中设置字符串数组或者字符串类型的形参接收此请求参数
>
> 若使用字符串数组类型的形参，此参数的数组中包含了每一个数据
>
> 若使用字符串类型的形参，此参数的值为每个数据中间使用逗号拼接的结果



直接就

```java
@PostMapping("/p")
@ResponseBody
public String postMapping(String username,String password){
    return "This is Post : "+username+" : "+password;
}
```

在这个里面调用就可以了。

> ```html
> <form action="/p" method="post">
>     <input name="username">
>     <br/>
>     <input name="password">
>     <br/>
>     <input type="submit" value="提交">
> </form>
> ```

问题：如果传进来的参数是: manager_name，java里面没有这种命名方式，我们就使用注解：`@RequestParam`

```java
@GetMapping("/t") //localhost/t
@ResponseBody //直接相应输出字符串数据，不跳转数据
public String test(@RequestParam("manager_name") String managerName){
    return "SUCCESS";
    //RequestParam这个就是来设定传进来的参数
}
```

这样就可以动态注入.

问题：有一百个输入的项目怎么办。需要使用Java Bean的方法

可以直接把输入的转化成Bean

新建：User.java

```java
public class User {
    private String username;
    private String password;

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```

测试：

```java
//    测试Java Bean直接构造的方法
    @PostMapping("/p")
    @ResponseBody
    public String postMappingUser(User user){
        return user.toString();
    }
```



同时不用担心丢失参数，就算是：public String postMappingUser(User user String username){ 这个username也会自动赋值





### 3、@RequestParam以及@RequestBody

@RequestParam是将请求参数和控制器方法的形参创建映射关系

@RequestParam注解一共有三个属性：

value：指定为形参赋值的请求参数的参数名

required：设置是否必须传输此请求参数，默认值为true

若设置为true时，则当前请求必须传输value所指定的请求参数，若没有传输该请求参数，且没有设置defaultValue属性，则页面报错400：Required String parameter ‘xxx’ is not present；若设置为false，则当前请求不是必须传输value所指定的请求参数，若没有传输，则注解所标识的形参的值为null

defaultValue：不管required属性值为true或false，当value所指定的请求参数没有传输或传输的值为""时，则使用默认值为形参赋值

**@RequestParam以及@RequestBody**区别

@RequestParam和@RequestBody是Spring MVC框架中常用的两个注解，它们的主要区别在于它们处理请求参数的方式不同。

@RequestParam注解用于从请求URL中获取单个或多个查询参数或表单参数。当使用@RequestParam时，需要指定参数的名称、数据类型和是否必填等信息。例如：

```java
@GetMapping("/user")
public User getUser(@RequestParam("id") Long userId) {
    // 根据userId查询用户信息
}
```

上面的代码中，@RequestParam注解用于从请求URL中获取名为"id"的查询参数，并将其转换为Long类型的userId参数。

而@RequestBody注解用于从请求体中获取请求参数，适用于POST、PUT等请求方法。当使用@RequestBody时，Spring会自动将请求体中的JSON、XML等数据格式转换为Java对象。例如：

```java
@PostMapping("/user")
public User createUser(@RequestBody User user) {
    // 处理用户创建请求
}
```

上面的代码中，@RequestBody注解用于将请求体中的JSON格式数据转换成User对象，并传递给createUser方法。

因此，@RequestParam注解适用于获取URL中的请求参数，而@RequestBody注解适用于获取请求体中的请求参数。

> ![截屏2023-04-25 22.09.04](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-04-25%2022.09.04.png)post很难在浏览器做请求，因此我们一般使用表单或者postman来请求Post
>
> > ```java
> >     @PostMapping("/p1")
> > //    Post传参数一般比较多，所以封装成了类，所以就是RequestBody
> > //    当使用@RequestBody时，Spring会自动将请求体中的JSON、XML等数据格式转换为Java对象。
> >     public String postRequest(@RequestBody Student student){
> >         return "我收到了参数："+student;
> >     }
> > ```
>
> 记得在Student里面set和get方法以及构造函数

### 4、@RequestHeader

@RequestHeader是将请求头信息和控制器方法的形参创建映射关系

@RequestHeader注解一共有三个属性：value、required、defaultValue，用法同@RequestParam

### 5、@CookieValue

@CookieValue是将cookie数据和控制器方法的形参创建映射关系

@CookieValue注解一共有三个属性：value、required、defaultValue，用法同@RequestParam

### 6、通过POJO获取请求参数

可以在控制器方法的形参位置设置一个实体类类型的形参，此时若浏览器传输的请求参数的参数名和实体类中的属性名一致，那么请求参数就会为此属性赋值

```html
<form th:action="@{/testpojo}" method="post">
    用户名：<input type="text" name="username"><br>
    密码：<input type="password" name="password"><br>
    性别：<input type="radio" name="sex" value="男">男<input type="radio" name="sex" value="女">女<br>
    年龄：<input type="text" name="age"><br>
    邮箱：<input type="text" name="email"><br>
    <input type="submit">
</form>

@RequestMapping("/testpojo")
public String testPOJO(User user){
    System.out.println(user);
    return "success";
}
//最终结果-->User{id=null, username='张三', password='123', age=23, sex='男', email='123@qq.com'}

```

> 时间格式转换
>
> ```sh
> 附时间格式：
> 
>  Symbol  Meaning                      Presentation  Examples
>  ------  -------                      ------------  -------
>  G       era                          text          AD
>  C       century of era (>=0)         number        20
>  Y       year of era (>=0)            year          1996
> 
>  x       weekyear                     year          1996
>  w       week of weekyear             number        27
>  e       day of week                  number        2
>  E       day of week                  text          Tuesday; Tue
> 
>  y       year                         year          1996
>  D       day of year                  number        189
>  M       month of year                month         July; Jul; 07
>  d       day of month                 number        10
> 
>  a       halfday of day               text          PM
>  K       hour of halfday (0~11)       number        0
>  h       clockhour of halfday (1~12)  number        12
> 
>  H       hour of day (0~23)           number        0
>  k       clockhour of day (1~24)      number        24
>  m       minute of hour               number        30
>  s       second of minute             number        55
>  S       fraction of second           millis        978
> 
>  z       time zone                    text          Pacific Standard Time; PST
>  Z       time zone offset/id          zone          -0800; -08:00; America/Los_Angeles
> 
>  '       escape for text              delimiter
>  ''      single quote                 literal       '
> 
> ```
>
> 
>
> - 只要加上注解`@DateTimeFormat(pattern = "yyyy-MM-dd")`就可以了。
>
> ![image-20230420221341179](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230420221341179.png)
>
> ```java
> @DateTimeFormat(pattern = "yyyy-MM-dd")
> private Date creatDate;//creatdate
> ```
>
> 也可以：
>
> # converter
>
> Spring 的Converter是可以将一种类型转换成另外一种类型的一个对象。 
>
> 举个例子，用户输入的日期格式可能有许多种，比如“January 10,2018”、“10/01/2018”、“2018-01-10”,这些都表示同一个日期。 默认情况下，Spring会将期待用户输入的日期样式和当前语言区域的日期样式相同。
>
> 比如US用户，就是月/日/年的格式。 如果希望Spring在将输入的日期字符串绑定到Date时使用不同的日期格式，则需要编写一个Converter，才能将字符串转换成日期。
>
> #### Step 1 实现Converter接口
>
>  为了创建自定义的Converter需要实现 org.springframework.core.convert.converter.Converter接口
>
> 我们来看下该接口
>
> ```javascript
> package org.springframework.core.convert.converter;
> 
> /**
>  * A converter converts a source object of type {@code S} to a target of type {@code T}.
>  *
>  * Implementations of this interface are thread-safe and can be shared.
>  *
>  * 
> ```
>
> 
>
> 这里的泛型 S表示源类型， 泛型T表示目标类型。 比如为了创建一个可以将String转为Date的Converter，可以像下面这样声明
>
> ```javascript
> public class MyConverter implements Converter<String, Date> {
> 
> }
> ```
>
> 
>
> 当然了要重写convert方法。
>
> ```javascript
> @Override
>     public Date convert(String source){
> 
> }
> ```
>
> 
>
> 该例中的代码如下
>
> MyConverter.java
>
> ```javascript
> package com.artisan.converter;
> 
> import java.text.ParseException;
> import java.text.SimpleDateFormat;
> import java.util.Date;
> 
> import org.springframework.core.convert.converter.Converter;
> /**
>  * 
> * @ClassName: MyConverter  
> * @Description: 自定义Converter,将String类型的数据转换为指定格式的Date
> * @author Mr.Yang  
> * @date 2018年2月10日  
> *
>  */
> public class MyConverter implements Converter<String, Date> {
> 
>     private String datePattern;
>     private Date targetFormateDate;
> 
>     /**
>      * 
>      * 创建一个新的实例 MyConverter. 默认构造函数
>      *
>      */
>     public MyConverter() {
>         super();
>     }
> 
>     /**
>      * 
>      * 创建一个新的实例 MyConverter. 实例化时指定日期格式
>      * 
>      * @param datePattern
>      */
>     public MyConverter(String datePattern) {
>         super();
>         this.datePattern = datePattern;
>     }
> 
> 
>     /**
>      * 重写convert方法
>      */
>     @Override
>     public Date convert(String source) {
>         try {
>             SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
>             // 是否严格解析日期,设置false禁止SimpleDateFormat的自动计算功能
>             sdf.setLenient(false);
>             targetFormateDate = sdf.parse(source);
>         } catch (ParseException e) {
>             // the error message will be displayed when using 
>             e.printStackTrace();
>             throw new IllegalArgumentException("invalid date format. Please use this pattern\"" + datePattern + "\"");
>         }
>         return targetFormateDate;
>     }
> 
> }
> 
> /**
>  * 如果设置为true,假设你输入的日期不合法,它会先进行一定的计算.计算出能有合法的值,就以计算后的值为真正的值.
>  * 
>  * 比如说当你使用的时候有2012-02-31,2012-14-03这样数据去format,
>  * 如果setLenient(true).那么它就会自动解析为2012-03-02和2013-02-03这样的日期.
>  * 如果setLenient(false),2012-14-03就会出现解析异常,因为去掉了计算,而这样的数据又是不合法的
>  * 
>  */
> ```
>
> 
>
> #### Step 2 SpringMVC配置文件中配置bean及设置conversion-service属性
>
> 为了在Spring MVC中使用自定义的Converter，需要在SpringMVC的配置文件中配置一个conversionService ，该Bean的名字必须为  org.springframework.context.support.ConversionServiceFactoryBean 。同时必须包含一个converters属性，它将列出要在应用程序中使用的所有定制的Converter. 
>
> 紧接着要给annotation-driven元素的conversion-service属性赋bean名称。
>
> 完整配置文件如下：
>
> ```javascript
> <beans xmlns="http://www.springframework.org/schema/beans"
>     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
>     xmlns:p="http://www.springframework.org/schema/p"
>     xmlns:mvc="http://www.springframework.org/schema/mvc"
>     xmlns:context="http://www.springframework.org/schema/context"
>     xsi:schemaLocation="
>         http://www.springframework.org/schema/beans
>         http://www.springframework.org/schema/beans/spring-beans.xsd
>         http://www.springframework.org/schema/mvc
>         http://www.springframework.org/schema/mvc/spring-mvc.xsd     
>         http://www.springframework.org/schema/context
>         http://www.springframework.org/schema/context/spring-context.xsd">
> 
> 
>     
>     <context:component-scan base-package="com.artisan.controller"/>
> 
> 
>     
> 
>     
>     <mvc:annotation-driven  conversion-service="conversionService"/>
>     <mvc:resources mapping="/css/**" location="/css/"/>
>     <mvc:resources mapping="/*.jsp" location="/"/>
> 
>     
>     <bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
>         <property name="prefix" value="/WEB-INF/jsp/"/>
>         <property name="suffix" value=".jsp"/>
>     bean>
> 
>     
>     <bean id="conversionService" 
>             class="org.springframework.context.support.ConversionServiceFactoryBean">
>         <property name="converters">
>             <list>
>                 <bean class="com.artisan.converter.MyConverter">
>                     <constructor-arg type="java.lang.String" value="MM-dd-yyyy"/>
>                 bean>
>             list>
>         property>
>     bean>
> 
> 
> beans>
> ```





### 7、解决获取请求参数的乱码问题

老版本的Tomcat默认时ISO-959-1，属于西欧字符集，英文，拉丁，标准符号

三种配置：

- Get乱码：Tomcat中的conf -> serverl.xml增加URIEncoding属性

```xml
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443" URIEncoding="UTF-8"/>
```

- Post乱码：web.xml 配置CharacterEncodingFilter

  使用SpringMVC提供的编码过滤器CharacterEncodingFilter，但是必须在web.xml中进行注册

```xml
<!--配置springMVC的编码过滤器-->
<filter>
    <filter-name>CharacterEncodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
    </init-param>
    <init-param>
        <param-name>forceResponseEncoding</param-name>
        <param-value>true</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>CharacterEncodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

```

> 注：
>
> SpringMVC中处理编码的过滤器一定要配置到其他过滤器之前，否则无效

- Response乱码：Spring配置applicationContext.xml StringHeepMessageConverter

```xml
<mvc:annotation-driven>
        <mvc:message-converters>
            <bean class="org.springframework.http.converter.StringHttpMessageConverter">
<!--                对相应的文本消息转换-->
                <property name="supportedMediaTypes">
                    <list>
<!--                        在servlet里面这么设置的
                            response.setContentType-->
                        <value>text/html;charset=utf-8</value>
                    </list>
                </property>
            </bean>
        </mvc:message-converters>
    </mvc:annotation-driven>
```

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230420223937785.png" alt="image-20230420223937785" style="zoom:50%;" />



# 五、域对象共享数据

#### Spring相应产生文本：

- @ResponseBody 相应文本

- 产生MVC视图



### 1、使用ServletAPI向request域对象共享数据

```java
@RequestMapping("/testServletAPI")
public String testServletAPI(HttpServletRequest request){
    request.setAttribute("testScope", "hello,servletAPI");
    return "success";
}
12345
```

### 2、使用ModelAndView向request域对象共享数据

> 这个是请求转发的方法，mdv共享的一个request的对象。

```java
@GetMapping("/view")
    public ModelAndView showView(Integer useId){

//        返回值是不一样的，这里是跳转页面的
        ModelAndView mdv = new ModelAndView("/view.jsp");
        User user = new User();
        if (useId ==1){
            user.setUsername("Lily");
        }else {
            user.setUsername("lucy");
        }
        mdv.addObject("u",user);//添加了数据，将界面了数据绑定
        return mdv;
    }
```

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Hello</title>
</head>
<body>
<h2> View Page </h2>
<hr>
<h2> Username: ${u.username}</h2>
</body>
</html>
```

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230421145638325.png" alt="image-20230421145638325" style="zoom: 50%;" />

> 想要使用请求转发的话就加一个
>
> ModelAndView mdv = new ModelAndView("redirect:/view.jsp");

### 3、使用Model向request域对象共享数据

```java
@RequestMapping("/testModel")
public String testModel(Model model){
    model.addAttribute("testScope", "hello,Model");
    return "success";
}
12345
```

### 4、使用map向request域对象共享数据

```java
@RequestMapping("/testMap")
public String testMap(Map<String, Object> map){
    map.put("testScope", "hello,Map");
    return "success";
}
12345
```

### 5、使用ModelMap向request域对象共享数据* 这方法用的较多

```java
@GetMapping("/view")
    public String showView(Integer useId, ModelMap modelMap){

//        返回值是不一样的，这里是跳转页面的
        String view = "/view.jsp"
        User user = new User();
        if (useId ==1){
            user.setUsername("Lily");
        }else {
            user.setUsername("lucy");
        }
        modelMap.addAttribute("u",user);
        return view;
    }
```

### 6、Model、ModelMap、Map的关系

Model、ModelMap、Map类型的参数其实本质上都是 BindingAwareModelMap 类型的

```
public interface Model{}
public class ModelMap extends LinkedHashMap<String, Object> {}
public class ExtendedModelMap extends ModelMap implements Model {}
public class BindingAwareModelMap extends ExtendedModelMap {}
1234
```

### 7、向session域共享数据

```java
@RequestMapping("/testSession")
public String testSession(HttpSession session){
    session.setAttribute("testSessionScope", "hello,session");
    return "success";
}
12345
```

### 8、向application域共享数据

```java
@RequestMapping("/testApplication")
public String testApplication(HttpSession session){
	ServletContext application = session.getServletContext();
    application.setAttribute("testApplicationScope", "hello,application");
    return "success";
}
123456
```

# 六、SpringMVC的视图

SpringMVC中的视图是View接口，视图的作用渲染数据，将模型Model中的数据展示给用户

SpringMVC视图的种类很多，默认有转发视图和重定向视图

当工程引入jstl的依赖，转发视图会自动转换为JstlView

若使用的视图技术为Thymeleaf，在SpringMVC的配置文件中配置了Thymeleaf的视图解析器，由此视图解析器解析之后所得到的是ThymeleafView

### 1、ThymeleafView

当控制器方法中所设置的视图名称没有任何前缀时，此时的视图名称会被SpringMVC配置文件中所配置的视图解析器解析，视图名称拼接视图前缀和视图后缀所得到的最终路径，会通过转发的方式实现跳转

```java
@RequestMapping("/testHello")
public String testHello(){
    return "hello";
}
1234
```

![在这里插入图片描述](https://res.mowangblog.top/img/2021/10/781f6b299e6b41a8b006866ecbcb76ba.png)

### 2、转发视图

SpringMVC中默认的转发视图是InternalResourceView

SpringMVC中创建转发视图的情况：

当控制器方法中所设置的视图名称以"forward:"为前缀时，创建InternalResourceView视图，此时的视图名称不会被SpringMVC配置文件中所配置的视图解析器解析，而是会将前缀"forward:"去掉，剩余部分作为最终路径通过转发的方式实现跳转

例如"forward:/"，“forward:/employee”

```java
@RequestMapping("/testForward")
public String testForward(){
    return "forward:/testHello";
}
1234
```

![在这里插入图片描述](https://res.mowangblog.top/img/2021/10/71526c269bbb447b8701d906b2859965.png)

### 3、重定向视图

SpringMVC中默认的重定向视图是RedirectView

当控制器方法中所设置的视图名称以"redirect:"为前缀时，创建RedirectView视图，此时的视图名称不会被SpringMVC配置文件中所配置的视图解析器解析，而是会将前缀"redirect:"去掉，剩余部分作为最终路径通过重定向的方式实现跳转

例如"redirect:/"，“redirect:/employee”

```java
@RequestMapping("/testRedirect")
public String testRedirect(){
    return "redirect:/testHello";
}
1234
```

![在这里插入图片描述](https://res.mowangblog.top/img/2021/10/ec5b5371ea804cafb27e2751231df362.png)

> 注：
>
> 重定向视图在解析时，会先将redirect:前缀去掉，然后会判断剩余部分是否以/开头，若是则会自动拼接上下文路径

### 4、视图控制器view-controller

当控制器方法中，仅仅用来实现页面跳转，即只需要设置视图名称时，可以将处理器方法使用view-controller标签进行表示

```xml
<!--
	path：设置处理的请求地址
	view-name：设置请求地址所对应的视图名称
-->
<mvc:view-controller path="/testView" view-name="success"></mvc:view-controller>
12345
```

> 注：
>
> 当SpringMVC中设置任何一个view-controller时，其他控制器中的请求映射将全部失效，此时需要在SpringMVC的核心配置文件中设置开启mvc注解驱动的标签：
>
> <mvc:annotation-driven />

# 七、RESTful

> 传统的开发：
>
> ![image-20230421151712863](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230421151712863.png)

### 1、RESTful简介

REST：**Re**presentational **S**tate **T**ransfer，表现层资源状态转移。

##### a>资源

资源是一种看待服务器的方式，即，将服务器看作是由很多离散的资源组成。每个资源是服务器上一个可命名的抽象概念。因为资源是一个抽象的概念，所以它不仅仅能代表服务器文件系统中的一个文件、数据库中的一张表等等具体的东西，可以将资源设计的要多抽象有多抽象，只要想象力允许而且客户端应用开发者能够理解。与面向对象设计类似，资源是以名词为核心来组织的，首先关注的是名词。一个资源可以由一个或多个URI来标识。URI既是资源的名称，也是资源在Web上的地址。对某个资源感兴趣的客户端应用，可以通过资源的URI与其进行交互。

##### b>资源的表述

资源的表述是一段对于资源在某个特定时刻的状态的描述。可以在客户端-服务器端之间转移（交换）。资源的表述可以有多种格式，例如HTML/XML/JSON/纯文本/图片/视频/音频等等。资源的表述格式可以通过协商机制来确定。请求-响应方向的表述通常使用不同的格式。

##### c>状态转移

状态转移说的是：在客户端和服务器端之间转移（transfer）代表资源状态的表述。通过转移和操作资源的表述，来间接实现操作资源的目的。

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230421153747785.png" alt="image-20230421153747785" style="zoom:50%;" />

### 2、RESTful的实现

具体说，就是 HTTP 协议里面，四个表示操作方式的动词：GET、POST、PUT、DELETE。

它们分别对应四种基本操作：GET 用来获取资源，POST 用来新建资源，PUT 用来更新资源，DELETE 用来删除资源。

REST 风格**提倡 URL 地址使用统一的风格设计**，从前到后各个单词使用斜杠分开，不使用问号键值对方式携带请求参数，而是将要发送给服务器的数据作为 URL 地址的一部分，以保证整体风格的一致性。

| 操作     | 传统方式         | REST风格               |
| -------- | ---------------- | ---------------------- |
| 查询操作 | getUserById?id=1 | user/1–>get请求方式    |
| 保存操作 | saveUser         | user–>post请求方式     |
| 删除操作 | deleteUser?id=1  | user/1–>delete请求方式 |
| 更新操作 | updateUser       | user–>put请求方式      |

### 3、HiddenHttpMethodFilter

由于浏览器只支持发送get和post方式的请求，那么该如何发送put和delete请求呢？

SpringMVC 提供了 **HiddenHttpMethodFilter** 帮助我们**将 POST 请求转换为 DELETE 或 PUT 请求**

**HiddenHttpMethodFilter** 处理put和delete请求的条件：

a>当前请求的请求方式必须为post

b>当前请求必须传输请求参数_method

满足以上条件，**HiddenHttpMethodFilter** 过滤器就会将当前请求的请求方式转换为请求参数_method的值，因此请求参数_method的值才是最终的请求方式

在web.xml中注册**HiddenHttpMethodFilter**

```xml
<filter>
    <filter-name>HiddenHttpMethodFilter</filter-name>
    <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>HiddenHttpMethodFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
12345678
```

> 注：
>
> 目前为止，SpringMVC中提供了两个过滤器：CharacterEncodingFilter和HiddenHttpMethodFilter
>
> 在web.xml中注册时，必须先注册CharacterEncodingFilter，再注册HiddenHttpMethodFilter
>
> 原因：
>
> - 在 CharacterEncodingFilter 中通过 request.setCharacterEncoding(encoding) 方法设置字符集的
>
> - request.setCharacterEncoding(encoding) 方法要求前面不能有任何获取请求参数的操作
>
> - 而 HiddenHttpMethodFilter 恰恰有一个获取请求方式的操作：
>
> - ```
>   String paramValue = request.getParameter(this.methodParam);
>   1
>   ```

# 八、RESTful案例

### 1、准备工作

和传统 CRUD 一样，实现对员工信息的增删改查。

- 搭建环境

- 准备实体类

  ```java
  package com.atguigu.mvc.bean;
  
  public class Employee {
  
     private Integer id;
     private String lastName;
  
     private String email;
     //1 male, 0 female
     private Integer gender;
     
     public Integer getId() {
        return id;
     }
  
     public void setId(Integer id) {
        this.id = id;
     }
  
     public String getLastName() {
        return lastName;
     }
  
     public void setLastName(String lastName) {
        this.lastName = lastName;
     }
  
     public String getEmail() {
        return email;
     }
  
     public void setEmail(String email) {
        this.email = email;
     }
  
     public Integer getGender() {
        return gender;
     }
  
     public void setGender(Integer gender) {
        this.gender = gender;
     }
  
     public Employee(Integer id, String lastName, String email, Integer gender) {
        super();
        this.id = id;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
     }
  
     public Employee() {
     }
  }
  123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354
  ```

- 准备dao模拟数据

  ```java
  package com.atguigu.mvc.dao;
  
  import java.util.Collection;
  import java.util.HashMap;
  import java.util.Map;
  
  import com.atguigu.mvc.bean.Employee;
  import org.springframework.stereotype.Repository;
  
  
  @Repository
  public class EmployeeDao {
  
     private static Map<Integer, Employee> employees = null;
     
     static{
        employees = new HashMap<Integer, Employee>();
  
        employees.put(1001, new Employee(1001, "E-AA", "aa@163.com", 1));
        employees.put(1002, new Employee(1002, "E-BB", "bb@163.com", 1));
        employees.put(1003, new Employee(1003, "E-CC", "cc@163.com", 0));
        employees.put(1004, new Employee(1004, "E-DD", "dd@163.com", 0));
        employees.put(1005, new Employee(1005, "E-EE", "ee@163.com", 1));
     }
     
     private static Integer initId = 1006;
     
     public void save(Employee employee){
        if(employee.getId() == null){
           employee.setId(initId++);
        }
        employees.put(employee.getId(), employee);
     }
     
     public Collection<Employee> getAll(){
        return employees.values();
     }
     
     public Employee get(Integer id){
        return employees.get(id);
     }
     
     public void delete(Integer id){
        employees.remove(id);
     }
  }
  
  ```

### 2、功能清单

| 功能                | URL 地址    | 请求方式 |
| ------------------- | ----------- | -------- |
| 访问首页√           | /           | GET      |
| 查询全部数据√       | /employee   | GET      |
| 删除√               | /employee/2 | DELETE   |
| 跳转到添加数据页面√ | /toAdd      | GET      |
| 执行保存√           | /employee   | POST     |
| 跳转到更新数据页面√ | /employee/2 | GET      |
| 执行更新√           | /employee   | PUT      |

### 3、具体功能：访问首页

##### a>配置view-controller

```xml
<mvc:view-controller path="/" view-name="index"/>
1
```

##### b>创建页面

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8" >
    <title>Title</title>
</head>
<body>
<h1>首页</h1>
<a th:href="@{/employee}">访问员工信息</a>
</body>
</html>
1234567891011
```

### 4、具体功能：查询所有员工数据

##### a>控制器方法

```java
@RequestMapping(value = "/employee", method = RequestMethod.GET)
public String getEmployeeList(Model model){
    Collection<Employee> employeeList = employeeDao.getAll();
    model.addAttribute("employeeList", employeeList);
    return "employee_list";
}
123456
```

##### b>创建employee_list.html

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Employee Info</title>
    <script type="text/javascript" th:src="@{/static/js/vue.js}"></script>
</head>
<body>

    <table border="1" cellpadding="0" cellspacing="0" style="text-align: center;" id="dataTable">
        <tr>
            <th colspan="5">Employee Info</th>
        </tr>
        <tr>
            <th>id</th>
            <th>lastName</th>
            <th>email</th>
            <th>gender</th>
            <th>options(<a th:href="@{/toAdd}">add</a>)</th>
        </tr>
        <tr th:each="employee : ${employeeList}">
            <td th:text="${employee.id}"></td>
            <td th:text="${employee.lastName}"></td>
            <td th:text="${employee.email}"></td>
            <td th:text="${employee.gender}"></td>
            <td>
                <a class="deleteA" @click="deleteEmployee" th:href="@{'/employee/'+${employee.id}}">delete</a>
                <a th:href="@{'/employee/'+${employee.id}}">update</a>
            </td>
        </tr>
    </table>
</body>
</html>
123456789101112131415161718192021222324252627282930313233
```

### 5、具体功能：删除

##### a>创建处理delete请求方式的表单

```html
<!-- 作用：通过超链接控制表单的提交，将post请求转换为delete请求 -->
<form id="delete_form" method="post">
    <!-- HiddenHttpMethodFilter要求：必须传输_method请求参数，并且值为最终的请求方式 -->
    <input type="hidden" name="_method" value="delete"/>
</form>
12345
```

##### b>删除超链接绑定点击事件

引入vue.js

```html
<script type="text/javascript" th:src="@{/static/js/vue.js}"></script>
1
```

删除超链接

```html
<a class="deleteA" @click="deleteEmployee" th:href="@{'/employee/'+${employee.id}}">delete</a>
1
```

通过vue处理点击事件

```html
<script type="text/javascript">
    var vue = new Vue({
        el:"#dataTable",
        methods:{
            //event表示当前事件
            deleteEmployee:function (event) {
                //通过id获取表单标签
                var delete_form = document.getElementById("delete_form");
                //将触发事件的超链接的href属性为表单的action属性赋值
                delete_form.action = event.target.href;
                //提交表单
                delete_form.submit();
                //阻止超链接的默认跳转行为
                event.preventDefault();
            }
        }
    });
</script>
123456789101112131415161718
```

##### c>控制器方法

```java
@RequestMapping(value = "/employee/{id}", method = RequestMethod.DELETE)
public String deleteEmployee(@PathVariable("id") Integer id){
    employeeDao.delete(id);
    return "redirect:/employee";
}
12345
```

### 6、具体功能：跳转到添加数据页面

##### a>配置view-controller

```xml
<mvc:view-controller path="/toAdd" view-name="employee_add"></mvc:view-controller>
1
```

##### b>创建employee_add.html

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Add Employee</title>
</head>
<body>

<form th:action="@{/employee}" method="post">
    lastName:<input type="text" name="lastName"><br>
    email:<input type="text" name="email"><br>
    gender:<input type="radio" name="gender" value="1">male
    <input type="radio" name="gender" value="0">female<br>
    <input type="submit" value="add"><br>
</form>

</body>
</html>
123456789101112131415161718
```

### 7、具体功能：执行保存

##### a>控制器方法

```java
@RequestMapping(value = "/employee", method = RequestMethod.POST)
public String addEmployee(Employee employee){
    employeeDao.save(employee);
    return "redirect:/employee";
}
12345
```

### 8、具体功能：跳转到更新数据页面

##### a>修改超链接

```html
<a th:href="@{'/employee/'+${employee.id}}">update</a>
1
```

##### b>控制器方法

```java
@RequestMapping(value = "/employee/{id}", method = RequestMethod.GET)
public String getEmployeeById(@PathVariable("id") Integer id, Model model){
    Employee employee = employeeDao.get(id);
    model.addAttribute("employee", employee);
    return "employee_update";
}
123456
```

##### c>创建employee_update.html

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Update Employee</title>
</head>
<body>

<form th:action="@{/employee}" method="post">
    <input type="hidden" name="_method" value="put">
    <input type="hidden" name="id" th:value="${employee.id}">
    lastName:<input type="text" name="lastName" th:value="${employee.lastName}"><br>
    email:<input type="text" name="email" th:value="${employee.email}"><br>
    <!--
        th:field="${employee.gender}"可用于单选框或复选框的回显
        若单选框的value和employee.gender的值一致，则添加checked="checked"属性
    -->
    gender:<input type="radio" name="gender" value="1" th:field="${employee.gender}">male
    <input type="radio" name="gender" value="0" th:field="${employee.gender}">female<br>
    <input type="submit" value="update"><br>
</form>

</body>
</html>
123456789101112131415161718192021222324
```

### 9、具体功能：执行更新

##### a>控制器方法

```java
@RequestMapping(value = "/employee", method = RequestMethod.PUT)
public String updateEmployee(Employee employee){
    employeeDao.save(employee);
    return "redirect:/employee";
}
12345
```

# 八、HttpMessageConverter

HttpMessageConverter，报文信息转换器，将请求报文转换为Java对象，或将Java对象转换为响应报文

HttpMessageConverter提供了两个注解和两个类型：

@RequestBody，

@ResponseBody，



RequestEntity，

ResponseEntity

### 1、@RequestBody

@RequestBody可以获取请求体，需要在控制器方法设置一个形参，使用@RequestBody进行标识，当前请求的请求体就会为当前注解所标识的形参赋值

```html
<form th:action="@{/testRequestBody}" method="post">
    用户名：<input type="text" name="username"><br>
    密码：<input type="password" name="password"><br>
    <input type="submit">
</form>
12345
@RequestMapping("/testRequestBody")
public String testRequestBody(@RequestBody String requestBody){
    System.out.println("requestBody:"+requestBody);
    return "success";
}
12345
```

输出结果：

requestBody:username=admin&password=123456

### 2、RequestEntity

RequestEntity封装请求报文的一种类型，需要在控制器方法的形参中设置该类型的形参，当前请求的请求报文就会赋值给该形参，可以通过getHeaders()获取请求头信息，通过getBody()获取请求体信息

```java
@RequestMapping("/testRequestEntity")
public String testRequestEntity(RequestEntity<String> requestEntity){
    System.out.println("requestHeader:"+requestEntity.getHeaders());
    System.out.println("requestBody:"+requestEntity.getBody());
    return "success";
}
123456
```

输出结果：
requestHeader:[host:“localhost:8080”, connection:“keep-alive”, content-length:“27”, cache-control:“max-age=0”, sec-ch-ua:"" Not A;Brand";v=“99”, “Chromium”;v=“90”, “Google Chrome”;v=“90"”, sec-ch-ua-mobile:"?0", upgrade-insecure-requests:“1”, origin:“http://localhost:8080”, user-agent:“Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36”]
requestBody:username=admin&password=123

### 3、@ResponseBody

@ResponseBody用于标识一个控制器方法，可以将该方法的返回值直接作为响应报文的响应体响应到浏览器

```java
@RequestMapping("/testResponseBody")
@ResponseBody
public String testResponseBody(){
    return "success";
}
12345
```

结果：浏览器页面显示success

### 4、SpringMVC处理json



a>导入jackson的依赖

```xml
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>2.9.9</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>2.9.9</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.9.9</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.62</version>
        </dependency>
```

b>直接用Spring的Controller配合一大堆注解写REST太麻烦了，因此，Spring还额外提供了一个`@RestController`注解，使用`@RestController`替代`@Controller`后，每个方法自动变成API接口方法。

c>将Java对象直接作为控制器方法的返回值返回**，就会自动转换为Json格式的字符**串

```java
package mcxgroup.controller;

import mcxgroup.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Restfulcontroller {
    @GetMapping("/req")
//    @ResponseBody//加了body就是作为字符串输出，不加就是作为页面跳转，
    public String deGetRequest(){
//        restful就是一种风格。
        return "{\"message\":\"返回查询结果\"}";//转意输出，
    }

    @GetMapping("/user")
    public User doRest(){
        User user = new User();
        user.setId(1);
        user.setName("啊啊啊啊");
        return user;
    }
}

```



编写REST接口只需要定义`@RestController`，然后，每个方法都是一个API接口，输入和输出只要能被Jackson序列化或反序列化为JSON就没有问题。我们用浏览器测试GET请求，可直接显示JSON响应：

浏览器的页面中展示的结果：

![image-20230424222724099](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20230424222724099.png)

### 5、SpringMVC处理ajax

a>请求超链接：

```html
<div id="app">
	<a th:href="@{/testAjax}" @click="testAjax">testAjax</a><br>
</div>
123
```

b>通过vue和axios处理点击事件：

```html
<script type="text/javascript" th:src="@{/static/js/vue.js}"></script>
<script type="text/javascript" th:src="@{/static/js/axios.min.js}"></script>
<script type="text/javascript">
    var vue = new Vue({
        el:"#app",
        methods:{
            testAjax:function (event) {
                axios({
                    method:"post",
                    url:event.target.href,
                    params:{
                        username:"admin",
                        password:"123456"
                    }
                }).then(function (response) {
                    alert(response.data);
                });
                event.preventDefault();
            }
        }
    });
</script>
12345678910111213141516171819202122
```

c>控制器方法：

```java
@RequestMapping("/testAjax")
@ResponseBody
public String testAjax(String username, String password){
    System.out.println("username:"+username+",password:"+password);
    return "hello,ajax";
}
123456
```

### 6、@RestController注解

@RestController注解是springMVC提供的一个复合注解，标识在控制器的类上，就相当于为类添加了@Controller注解，并且为其中的每个方法添加了@ResponseBody注解

### 7、ResponseEntity

ResponseEntity用于控制器方法的返回值类型，该控制器方法的返回值就是响应到浏览器的响应报文

### 跨域访问

假设你的公司内部有一个应用程序，它运行在 `http://localhost:8080` 上。现在你想要从这个应用程序中发起一个 Ajax 请求去访问另一个运行在 `http://example.com` 上的服务器资源。

由于这两个域名不同，所以默认情况下，浏览器会拒绝该请求，并抛出跨域安全错误。这就像是你的公司门卫只允许进入本公司的员工，而不允许外来人士进入。

为了解决这个问题，可以在服务端添加 CORS 头信息，告诉浏览器允许来自 `http://localhost:8080` 的跨域请求。这就像是你的公司门卫特别开通了一个通道，允许某些特定的外来人士进入大楼。

Spring 的 `@CrossOrigin` 注解就提供了这样的功能，它可以设置允许跨域请求的来源、允许的 HTTP 方法、允许的头信息、缓存时间等相关参数，从而实现跨域请求的控制。

#### @crossOrigin -controller跨域注解

其中@CrossOrigin中的2个参数：

**origins** ： 允许可访问的域列表

**maxAge**：准备响应前的缓存持续的最大时间（以秒为单位）。

```java
@CrossOrigin(origins = {"http://127.0.0.1:8080"})
@CrossOrigin(origins = "*")
@CrossOrigin(maxAge = 3600)
```

`maxAge` 参数用于指定预检请求（preflight request）的缓存时间。预检请求是在实际请求之前发出的一种 HTTP OPTIONS 请求，它用于检查实际请求是否安全以及是否可以被服务器接受。浏览器通常会对预检请求进行缓存，以减少网络流量和延迟。

在跨域请求中，如果服务器需要发送 CORS 响应头信息，则会自动发送预检请求。如果服务器对预检请求的响应中包含了 `Access-Control-Max-Age` 头部，并设置了一个非零值，那么浏览器将会缓存此次预检请求的响应结果，并在下一次同源请求时使用该响应结果，从而避免再次发送预检请求。

例如，以下代码示例设置 `maxAge` 为 3600 秒：

```java
@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@RestController
public class UserController {
    // ...
}
```

这表示在收到来自 `http://localhost:8080` 的预检请求时，服务器会返回一个 `Access-Control-Max-Age: 3600` 的响应头，告诉浏览器可以将预检请求结果缓存 3600 秒，即一个小时。注意：单位是秒，如果不是整数值，则会被舍去小数部分。

值得注意的是，`maxAge` 参数只有在预检请求的响应中才会生效，对于实际请求是没有作用的。

#### <mvc:cors>xml中配置

小程序啥的都不行，都不能跨域访问。

在 Spring 中，可以使用 `CorsConfigurationSource` 和 `CorsFilter` 类实现跨域访问控制。以下是在 applicationContext.xml 文件中配置跨域访问的示例：

1. 首先，在 applicationContext.xml 文件中添加一个 `CorsConfigurationSource` 的 bean，并设置允许跨域请求的来源、HTTP 方法、头信息等相关参数：

   ```xml
   <bean id="corsConfiguration" class="org.springframework.web.cors.CorsConfiguration">
       <property name="allowedOrigins" value="http://localhost:8080"/>
       <property name="allowedMethods" value="GET,POST,PUT"/>
       <property name="allowedHeaders" value="Content-Type,Authorization"/>
       <property name="exposedHeaders" value="Content-Type,Authorization"/>
       <property name="maxAge" value="3600"/>
       <property name="allowCredentials" value="true"/>
   </bean>
   
   <bean id="corsConfigurationSource" class="org.springframework.web.cors.UrlBasedCorsConfigurationSource">
       <property name="corsConfigurations">
           <map>
               <entry key="/**" value-ref="corsConfiguration"/>
           </map>
       </property>
   </bean>
   ```

2. 然后，在 applicationContext.xml 文件中添加一个 `CorsFilter` 的 bean，并将上述创建的 `CorsConfigurationSource` 对象注入到其中：

   ```xml
   <bean id="corsFilter" class="org.springframework.web.filter.CorsFilter">
       <constructor-arg ref="corsConfigurationSource"/>
   </bean>
   ```

这样，在每次发起的跨域请求时，Spring 会先检查该请求是否允许跨域访问。如果允许，就会添加相应的 CORS 头信息，并返回响应；否则，将阻止该请求，并抛出跨域安全错误。

需要注意的是，在配置文件中使用 `CorsFilter` 后，您的应用程序将处理所有的跨域请求，因此请务必确保您充分了解这些请求的来源和目的。



Spring MVC 提供了 `<mvc:cors>` 配置元素，可以用于在 XML 中配置跨域访问。使用 `<mvc:cors>` 配置元素，您可以设置允许跨域请求的来源、HTTP 方法、头信息等相关参数。

以下是一个示例，在 `<mvc:cors>` 配置元素中设置允许来自 `http://localhost:8080` 的 GET 和 POST 请求：

```xml
<mvc:cors>
  <mvc:mapping path="/**"
    allowed-origins="http://localhost:8080"
    allowed-methods="GET,POST"
  />
</mvc:cors>
```

您还可以设置其他属性，例如 `allowed-headers`、`exposed-headers`、`max-age` 和 `allow-credentials` 等。

需要注意的是，如果您同时在 XML 和 Java 代码中配置跨域访问，则应该确保两种配置方式不会相互冲突。优先级高的配置方式将覆盖优先级低的配置方式，会优先选择注解的。因此，建议只使用其中一种配置方式，以避免不必要的麻烦和混淆。

当使用 `<mvc:cors>` 配置元素配置跨域访问时，还可以设置其他属性。以下是一些常用的属性及其对应的配置示例：

- `allowed-methods`: 允许的 HTTP 方法。例如，`GET, POST, PUT, DELETE`。示例：

  ```xml
  <mvc:mapping path="/**"
    allowed-origins="http://localhost:8080"
    allowed-methods="GET,POST,PUT,DELETE"
  />
  ```

- `allowed-headers`: 允许的 HTTP 头信息。例如，`Content-Type, Authorization`。示例：

  ```xml
  <mvc:mapping path="/**"
    allowed-origins="http://localhost:8080"
    allowed-headers="Content-Type,Authorization"
  />
  ```

- `exposed-headers`: 允许客户端访问的响应头信息。例如，`Content-Type, Authorization`。示例：

  ```xml
  <mvc:mapping path="/**"
    allowed-origins="http://localhost:8080"
    exposed-headers="Content-Type,Authorization"
  />
  ```

- `max-age`: 缓存预检请求的时间（单位为秒）。例如，`3600`。示例：

  ```xml
  <mvc:mapping path="/**"
    allowed-origins="http://localhost:8080"
    max-age="3600"
  />
  ```

- `allow-credentials`: 是否允许发送凭据信息（如 Cookie 和认证头）进行身份验证。例如，`true` 或 `false`。示例：

  ```xml
  <mvc:mapping path="/**"
    allowed-origins="http://localhost:8080"
    allow-credentials="true"
  />
  ```

需要注意的是，这些属性都是可选的，并且您可以根据需要自由组合它们。另外，当同时设置多个配置元素时，Spring MVC 将按照它们在 XML 文件中的顺序依次处理它们，因此后面的配置元素可能会覆盖前面的配置元素的设置。

# 九、文件上传和下载

### 1、文件下载

使用ResponseEntity实现下载文件的功能

```java
@RequestMapping("/testDown")
public ResponseEntity<byte[]> testResponseEntity(HttpSession session) throws IOException {
    //获取ServletContext对象
    ServletContext servletContext = session.getServletContext();
    //获取服务器中文件的真实路径
    String realPath = servletContext.getRealPath("/static/img/1.jpg");
    //创建输入流
    InputStream is = new FileInputStream(realPath);
    //创建字节数组
    byte[] bytes = new byte[is.available()];
    //将流读到字节数组中
    is.read(bytes);
    //创建HttpHeaders对象设置响应头信息
    MultiValueMap<String, String> headers = new HttpHeaders();
    //设置要下载方式以及下载文件的名字
    headers.add("Content-Disposition", "attachment;filename=1.jpg");
    //设置响应状态码
    HttpStatus statusCode = HttpStatus.OK;
    //创建ResponseEntity对象
    ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes, headers, statusCode);
    //关闭输入流
    is.close();
    return responseEntity;
}
123456789101112131415161718192021222324
```

### 2、文件上传

文件上传要求form表单的请求方式必须为post，并且添加属性enctype=“multipart/form-data”

SpringMVC中将上传的文件封装到MultipartFile对象中，通过此对象可以获取文件相关信息

上传步骤：

a>添加依赖：

```xml
<!-- https://mvnrepository.com/artifact/commons-fileupload/commons-fileupload -->
<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.3.1</version>
</dependency>
123456
```

b>在SpringMVC的配置文件中添加配置：

```xml
<!--必须通过文件解析器的解析才能将文件转换为MultipartFile对象-->
<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver"></bean>
12
```

c>控制器方法：

```java
@RequestMapping("/testUp")
public String testUp(MultipartFile photo, HttpSession session) throws IOException {
    //获取上传的文件的文件名
    String fileName = photo.getOriginalFilename();
    //处理文件重名问题
    String hzName = fileName.substring(fileName.lastIndexOf("."));
    fileName = UUID.randomUUID().toString() + hzName;
    //获取服务器中photo目录的路径
    ServletContext servletContext = session.getServletContext();
    String photoPath = servletContext.getRealPath("photo");
    File file = new File(photoPath);
    if(!file.exists()){
        file.mkdir();
    }
    String finalPath = photoPath + File.separator + fileName;
    //实现上传功能
    photo.transferTo(new File(finalPath));
    return "success";
}
12345678910111213141516171819
```

# 十、拦截器 Interceptor

Filter和Interceptor都是用来拦截请求并进行处理的组件，它们之间的区别如下：

1. 作用范围不同：Filter是基于Servlet规范实现的，其作用范围是在HttpServletRequest被Servlet容器处理之前或之后。而Interceptor是Spring MVC框架中的概念，其作用范围是在请求进入Spring MVC控制器之前或之后。
2. API不同：Filter是Servlet API定义的，它与Servlet容器有关；而Interceptor是Spring MVC框架内部定义的，它与Spring框架有关。
3. 处理方式不同：Filter可以对请求和响应进行过滤处理，包括修改请求数据、修改响应数据等。而Interceptor通常用于处理请求和响应之前或之后的逻辑，如日志记录、权限检查、异常处理等。
4. 配置方式不同：Filter需要在web.xml文件中声明，并按照指定顺序配置多个Filter；而Interceptor可以通过实现HandlerInterceptor接口，将其注册到Spring MVC框架中，也可以通过注解的方式来配置。

需要注意的是，Filter属于Servlet规范的一部分，是在请求进入Servlet容器之前或之后进行处理的，因此它可以拦截所有的请求，包括静态资源请求；而Interceptor是Spring MVC框架中的概念，只能拦截到经过DispatcherServlet处理的请求，因此它不能拦截静态资源请求。



依赖于Spring AOP，相当于环绕通知

导入pom.xml

```xml
       <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>3.1.0</version>
            <scope>provided</scope>
<!--            tomcat带的api和spring自带，不需要的这个-->
        </dependency>
```

### 1、拦截器的配置

SpringMVC中的拦截器用于拦截控制器方法的执行

SpringMVC中的拦截器需要实现HandlerInterceptor

SpringMVC的拦截器必须在SpringMVC的配置文件中进行配置：

```xml
    <mvc:interceptors>
        <mvc:interceptor>
<!--            对所有的请求-->
            <mvc:mapping path="/**"/>
            <bean class="mcxgroup.interceptor.myInterceptro"/>
        </mvc:interceptor>
    </mvc:interceptors>
```

### 2、拦截器的三个抽象方法

SpringMVC中的拦截器有三个抽象方法：

```java
public class myInterceptro implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println(request.getRequestURL()+"--准备执行");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println(request.getRequestURL()+"--目标处理成功");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println(request.getRequestURL()+"--响应完成");
    }
}
```

preHandle：控制器方法执行之前执行preHandle()，其boolean类型的返回值表示是否拦截或放行，返回true为放行，即调用控制器方法；返回false表示拦截，即不调用控制器方法

postHandle：控制器方法执行之后执行postHandle()，但是还没有响应之前、

afterComplation：处理完视图和模型数据，渲染视图完毕之后执行afterComplation()。

这三个方法分别代表了拦截器在请求处理前、后以及整个请求完成后所执行的操作。

假如设置了controller方法：

```java
@RestController
//@CrossOrigin(origins = {"http://127.0.0.1:8080"})
public class Restfulcontroller {
    @GetMapping("/user")
    public User doRest(){
        User user = new User();
        user.setId(1);
        user.setName("啊啊啊啊");
        System.out.println("return user啦");
        return user;
    }
}
```

测试：

```sh
C:\Users\mcxen>curl http://127.0.0.1:8080/user
{"id":1,"name":"啊啊啊啊"}
C:\Users\mcxen>


http://127.0.0.1:8080/user--准备执行
return user啦
http://127.0.0.1:8080/user--目标处理成功
http://127.0.0.1:8080/user--响应完成
```



### 3、多个拦截器的执行顺序

a>若每个拦截器的preHandle()都返回true

此时多个拦截器的执行顺序和拦截器在SpringMVC的配置文件的配置顺序有关：

preHandle()会按照配置的顺序执行，而postHandle()和afterComplation()会按照配置的反序执行

b>若某个拦截器的preHandle()返回了false

preHandle()返回false和它之前的拦截器的preHandle()都会执行，postHandle()都不执行，返回false的拦截器之前的拦截器的afterComplation()会执行



### 排除静态资源：

```xml
    <mvc:interceptors>
        <mvc:interceptor>
<!--            对所有的请求-->
            <mvc:mapping path="/**"/>
            <mvc:exclude-mapping path="/**.ico"/>
            <mvc:exclude-mapping path="/**.js"/>
<!--            配置一些排除的资源-->
            <bean class="mcxgroup.interceptor.myInterceptro"/>
        </mvc:interceptor>

    </mvc:interceptors>
```

也可以通过目录区分，

![image-20230425151704602](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20230425151704602.png)



也可以排除某一个请求（比如：/restful/** 下

```xml
    <mvc:interceptors>
        <mvc:interceptor>
<!--            对所有的请求-->
            <mvc:mapping path="/restful/**"/>
            <mvc:mapping path="/webapi/**"/>
            <mvc:exclude-mapping path="/**.ico"/>
            <mvc:exclude-mapping path="/**.js"/>
<!--            配置一些排除的资源-->
            <bean class="mcxgroup.interceptor.myInterceptro"/>
        </mvc:interceptor>

    </mvc:interceptors>
```



> 在Spring MVC中，多个拦截器的执行顺序与它们在Spring的配置文件中声明的顺序相关。按照声明顺序，先声明的拦截器会先被执行，后声明的拦截器会后被执行。
>
> 例如，声明了两个拦截器A和B，将它们都加入到Spring MVC的配置文件中，则A会先被执行，然后再执行B。如果需要改变拦截器的执行顺序，可以通过调整它们在配置文件中的声明顺序来实现。
>
> 需要注意的是，如果一个拦截器的preHandle方法返回false，那么后面的拦截器将不会被执行，请求也不会被发送到控制器。同时，如果一个拦截器的afterCompletion方法抛出了异常，那么前面的拦截器的afterCompletion方法将不会被执行，这可能会对应用程序产生一些影响。因此，在编写拦截器时，需要特别注意这些情况，并进行适当的处理。

### 实现一个用户样貌记录器

导入logback：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<configuration>
    <appender name = "console" class = "ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>
                <!--                下面的thread对应的就是main线程  logger 是哪个类10表示太长进行压缩  %msg%n是具体的内容-->
                [%thread]  %d{HH:mm:ss.SSS} %level %logger{10} - %msg%n
            </pattern>
        </encoder>
    </appender>
    <appender name="accessHistoryLog" class="ch.qos.logback.core.rolling.RollingFileAppender">
<!--        生成每一天的自动的输出文件-->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>
                d:/logs/history.%d.log
            </fileNamePattern>
        </rollingPolicy>
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
<!--        <root> 标签表示默认的日志记录器。它是所有其他日志记录器的祖先，如果没有为特定的类或包指定日志记录器，则使用 <root> 标签的配置。-->
        <appender-ref ref = "console"/>
<!--         属性引用了一个名为 "console" 的 appender，将日志事件输出到控制台(console)。-->
    </root>
<!--    logger 标签用于配置日志记录器。-->
<!--    name：指定日志记录器的名称，通常使用类的全限定名作为名称。-->
    <logger name = "mcxgroup.interceptor.AccessInterceptor" level = "INFO" additivity = "false">
<!--        additivity 属性被设置为 false，表示日志事件不会被传递到父级日志记录器。-->
        <appender-ref ref="accessHistoryLog"/>
    </logger>
</configuration>
```







# 十一、异常处理器

### 1、基于配置的异常处理

SpringMVC提供了一个处理控制器方法执行过程中所出现的异常的接口：HandlerExceptionResolver

HandlerExceptionResolver接口的实现类有：DefaultHandlerExceptionResolver和SimpleMappingExceptionResolver

SpringMVC提供了自定义的异常处理器SimpleMappingExceptionResolver，使用方式：

```xml
<bean class="org.springframework.web.servlet.handler.SimpleMappingExceptionResolver">
    <property name="exceptionMappings">
        <props>
        	<!--
        		properties的键表示处理器方法执行过程中出现的异常
        		properties的值表示若出现指定异常时，设置一个新的视图名称，跳转到指定页面
        	-->
            <prop key="java.lang.ArithmeticException">error</prop>
        </props>
    </property>
    <!--
    	exceptionAttribute属性设置一个属性名，将出现的异常信息在请求域中进行共享
    -->
    <property name="exceptionAttribute" value="ex"></property>
</bean>
123456789101112131415
```

### 2、基于注解的异常处理

```java
//@ControllerAdvice将当前类标识为异常处理的组件
@ControllerAdvice
public class ExceptionController {

    //@ExceptionHandler用于设置所标识方法处理的异常
    @ExceptionHandler(ArithmeticException.class)
    //ex表示当前请求处理中出现的异常对象
    public String handleArithmeticException(Exception ex, Model model){
        model.addAttribute("ex", ex);
        return "error";
    }

}
12345678910111213
```

# 十二、注解配置SpringMVC

使用配置类和注解代替web.xml和SpringMVC配置文件的功能

### 1、创建初始化类，代替web.xml

在Servlet3.0环境中，容器会在类路径中查找实现javax.servlet.ServletContainerInitializer接口的类，如果找到的话就用它来配置Servlet容器。
Spring提供了这个接口的实现，名为SpringServletContainerInitializer，这个类反过来又会查找实现WebApplicationInitializer的类并将配置的任务交给它们来完成。Spring3.2引入了一个便利的WebApplicationInitializer基础实现，名为AbstractAnnotationConfigDispatcherServletInitializer，当我们的类扩展了AbstractAnnotationConfigDispatcherServletInitializer并将其部署到Servlet3.0容器的时候，容器会自动发现它，并用它来配置Servlet上下文。

```java
public class WebInit extends AbstractAnnotationConfigDispatcherServletInitializer {

    /**
     * 指定spring的配置类
     * @return
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{SpringConfig.class};
    }

    /**
     * 指定SpringMVC的配置类
     * @return
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class};
    }

    /**
     * 指定DispatcherServlet的映射规则，即url-pattern
     * @return
     */
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    /**
     * 添加过滤器
     * @return
     */
    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceRequestEncoding(true);
        HiddenHttpMethodFilter hiddenHttpMethodFilter = new HiddenHttpMethodFilter();
        return new Filter[]{encodingFilter, hiddenHttpMethodFilter};
    }
}
123456789101112131415161718192021222324252627282930313233343536373839404142
```

### 2、创建SpringConfig配置类，代替spring的配置文件

```java
@Configuration
public class SpringConfig {
	//ssm整合之后，spring的配置信息写在此类中
}
1234
```

### 3、创建WebConfig配置类，代替SpringMVC的配置文件

```java
@Configuration
//扫描组件
@ComponentScan("com.atguigu.mvc.controller")
//开启MVC注解驱动
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    //使用默认的servlet处理静态资源
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    //配置文件上传解析器
    @Bean
    public CommonsMultipartResolver multipartResolver(){
        return new CommonsMultipartResolver();
    }

    //配置拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        FirstInterceptor firstInterceptor = new FirstInterceptor();
        registry.addInterceptor(firstInterceptor).addPathPatterns("/**");
    }
    
    //配置视图控制
    
    /*@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }*/
    
    //配置异常映射
    /*@Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
        Properties prop = new Properties();
        prop.setProperty("java.lang.ArithmeticException", "error");
        //设置异常映射
        exceptionResolver.setExceptionMappings(prop);
        //设置共享异常信息的键
        exceptionResolver.setExceptionAttribute("ex");
        resolvers.add(exceptionResolver);
    }*/

    //配置生成模板解析器
    @Bean
    public ITemplateResolver templateResolver() {
        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        // ServletContextTemplateResolver需要一个ServletContext作为构造参数，可通过WebApplicationContext 的方法获得
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(
                webApplicationContext.getServletContext());
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        return templateResolver;
    }

    //生成模板引擎并为模板引擎注入模板解析器
    @Bean
    public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }

    //生成视图解析器并未解析器注入模板引擎
    @Bean
    public ViewResolver viewResolver(SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setCharacterEncoding("UTF-8");
        viewResolver.setTemplateEngine(templateEngine);
        return viewResolver;
    }


}
12345678910111213141516171819202122232425262728293031323334353637383940414243444546474849505152535455565758596061626364656667686970717273747576777879
```

### 4、测试功能

```java
@RequestMapping("/")
public String index(){
    return "index";
}
1234
```

# 十三、SpringMVC执行流程

### 1、SpringMVC常用组件

- DispatcherServlet：**前端控制器**，不需要工程师开发，由框架提供

作用：统一处理请求和响应，整个流程控制的中心，由它调用其它组件处理用户的请求

- HandlerMapping：**处理器映射器**，不需要工程师开发，由框架提供

作用：根据请求的url、method等信息查找Handler，即控制器方法

- Handler：**处理器**，需要工程师开发

作用：在DispatcherServlet的控制下Handler对具体的用户请求进行处理

- HandlerAdapter：**处理器适配器**，不需要工程师开发，由框架提供

作用：通过HandlerAdapter对处理器（控制器方法）进行执行

- ViewResolver：**视图解析器**，不需要工程师开发，由框架提供

作用：进行视图解析，得到相应的视图，例如：ThymeleafView、InternalResourceView、RedirectView

- View：**视图**

作用：将模型数据通过页面展示给用户

### 2、DispatcherServlet初始化过程

DispatcherServlet 本质上是一个 Servlet，所以天然的遵循 Servlet 的生命周期。所以宏观上是 Servlet 生命周期来进行调度。

[外链图片转存失败,源站可能有防盗链机制,建议将图片保存下来直接上传(img-kphToPhs-1627992919015)(img/img005.png)]

##### a>初始化WebApplicationContext

所在类：org.springframework.web.servlet.FrameworkServlet

```java
protected WebApplicationContext initWebApplicationContext() {
    WebApplicationContext rootContext =
        WebApplicationContextUtils.getWebApplicationContext(getServletContext());
    WebApplicationContext wac = null;

    if (this.webApplicationContext != null) {
        // A context instance was injected at construction time -> use it
        wac = this.webApplicationContext;
        if (wac instanceof ConfigurableWebApplicationContext) {
            ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext) wac;
            if (!cwac.isActive()) {
                // The context has not yet been refreshed -> provide services such as
                // setting the parent context, setting the application context id, etc
                if (cwac.getParent() == null) {
                    // The context instance was injected without an explicit parent -> set
                    // the root application context (if any; may be null) as the parent
                    cwac.setParent(rootContext);
                }
                configureAndRefreshWebApplicationContext(cwac);
            }
        }
    }
    if (wac == null) {
        // No context instance was injected at construction time -> see if one
        // has been registered in the servlet context. If one exists, it is assumed
        // that the parent context (if any) has already been set and that the
        // user has performed any initialization such as setting the context id
        wac = findWebApplicationContext();
    }
    if (wac == null) {
        // No context instance is defined for this servlet -> create a local one
        // 创建WebApplicationContext
        wac = createWebApplicationContext(rootContext);
    }

    if (!this.refreshEventReceived) {
        // Either the context is not a ConfigurableApplicationContext with refresh
        // support or the context injected at construction time had already been
        // refreshed -> trigger initial onRefresh manually here.
        synchronized (this.onRefreshMonitor) {
            // 刷新WebApplicationContext
            onRefresh(wac);
        }
    }

    if (this.publishContext) {
        // Publish the context as a servlet context attribute.
        // 将IOC容器在应用域共享
        String attrName = getServletContextAttributeName();
        getServletContext().setAttribute(attrName, wac);
    }

    return wac;
}
123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354
```

##### b>创建WebApplicationContext

所在类：org.springframework.web.servlet.FrameworkServlet

```java
protected WebApplicationContext createWebApplicationContext(@Nullable ApplicationContext parent) {
    Class<?> contextClass = getContextClass();
    if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
        throw new ApplicationContextException(
            "Fatal initialization error in servlet with name '" + getServletName() +
            "': custom WebApplicationContext class [" + contextClass.getName() +
            "] is not of type ConfigurableWebApplicationContext");
    }
    // 通过反射创建 IOC 容器对象
    ConfigurableWebApplicationContext wac =
        (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);

    wac.setEnvironment(getEnvironment());
    // 设置父容器
    wac.setParent(parent);
    String configLocation = getContextConfigLocation();
    if (configLocation != null) {
        wac.setConfigLocation(configLocation);
    }
    configureAndRefreshWebApplicationContext(wac);

    return wac;
}
1234567891011121314151617181920212223
```

##### c>DispatcherServlet初始化策略

FrameworkServlet创建WebApplicationContext后，刷新容器，调用onRefresh(wac)，此方法在DispatcherServlet中进行了重写，调用了initStrategies(context)方法，初始化策略，即初始化DispatcherServlet的各个组件

所在类：org.springframework.web.servlet.DispatcherServlet

```java
protected void initStrategies(ApplicationContext context) {
   initMultipartResolver(context);
   initLocaleResolver(context);
   initThemeResolver(context);
   initHandlerMappings(context);
   initHandlerAdapters(context);
   initHandlerExceptionResolvers(context);
   initRequestToViewNameTranslator(context);
   initViewResolvers(context);
   initFlashMapManager(context);
}
1234567891011
```

### 3、DispatcherServlet调用组件处理请求

##### a>processRequest()

FrameworkServlet重写HttpServlet中的service()和doXxx()，这些方法中调用了processRequest(request, response)

所在类：org.springframework.web.servlet.FrameworkServlet

```java
protected final void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    long startTime = System.currentTimeMillis();
    Throwable failureCause = null;

    LocaleContext previousLocaleContext = LocaleContextHolder.getLocaleContext();
    LocaleContext localeContext = buildLocaleContext(request);

    RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
    ServletRequestAttributes requestAttributes = buildRequestAttributes(request, response, previousAttributes);

    WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
    asyncManager.registerCallableInterceptor(FrameworkServlet.class.getName(), new RequestBindingInterceptor());

    initContextHolders(request, localeContext, requestAttributes);

    try {
		// 执行服务，doService()是一个抽象方法，在DispatcherServlet中进行了重写
        doService(request, response);
    }
    catch (ServletException | IOException ex) {
        failureCause = ex;
        throw ex;
    }
    catch (Throwable ex) {
        failureCause = ex;
        throw new NestedServletException("Request processing failed", ex);
    }

    finally {
        resetContextHolders(request, previousLocaleContext, previousAttributes);
        if (requestAttributes != null) {
            requestAttributes.requestCompleted();
        }
        logResult(request, response, failureCause, asyncManager);
        publishRequestHandledEvent(request, response, startTime, failureCause);
    }
}
123456789101112131415161718192021222324252627282930313233343536373839
```

##### b>doService()

所在类：org.springframework.web.servlet.DispatcherServlet

```java
@Override
protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
    logRequest(request);

    // Keep a snapshot of the request attributes in case of an include,
    // to be able to restore the original attributes after the include.
    Map<String, Object> attributesSnapshot = null;
    if (WebUtils.isIncludeRequest(request)) {
        attributesSnapshot = new HashMap<>();
        Enumeration<?> attrNames = request.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String attrName = (String) attrNames.nextElement();
            if (this.cleanupAfterInclude || attrName.startsWith(DEFAULT_STRATEGIES_PREFIX)) {
                attributesSnapshot.put(attrName, request.getAttribute(attrName));
            }
        }
    }

    // Make framework objects available to handlers and view objects.
    request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
    request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, this.localeResolver);
    request.setAttribute(THEME_RESOLVER_ATTRIBUTE, this.themeResolver);
    request.setAttribute(THEME_SOURCE_ATTRIBUTE, getThemeSource());

    if (this.flashMapManager != null) {
        FlashMap inputFlashMap = this.flashMapManager.retrieveAndUpdate(request, response);
        if (inputFlashMap != null) {
            request.setAttribute(INPUT_FLASH_MAP_ATTRIBUTE, Collections.unmodifiableMap(inputFlashMap));
        }
        request.setAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
        request.setAttribute(FLASH_MAP_MANAGER_ATTRIBUTE, this.flashMapManager);
    }

    RequestPath requestPath = null;
    if (this.parseRequestPath && !ServletRequestPathUtils.hasParsedRequestPath(request)) {
        requestPath = ServletRequestPathUtils.parseAndCache(request);
    }

    try {
        // 处理请求和响应
        doDispatch(request, response);
    }
    finally {
        if (!WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted()) {
            // Restore the original attribute snapshot, in case of an include.
            if (attributesSnapshot != null) {
                restoreAttributesAfterInclude(request, attributesSnapshot);
            }
        }
        if (requestPath != null) {
            ServletRequestPathUtils.clearParsedRequestPath(request);
        }
    }
}
123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354
```

##### c>doDispatch()

所在类：org.springframework.web.servlet.DispatcherServlet

```java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
    HttpServletRequest processedRequest = request;
    HandlerExecutionChain mappedHandler = null;
    boolean multipartRequestParsed = false;

    WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

    try {
        ModelAndView mv = null;
        Exception dispatchException = null;

        try {
            processedRequest = checkMultipart(request);
            multipartRequestParsed = (processedRequest != request);

            // Determine handler for the current request.
            /*
            	mappedHandler：调用链
                包含handler、interceptorList、interceptorIndex
            	handler：浏览器发送的请求所匹配的控制器方法
            	interceptorList：处理控制器方法的所有拦截器集合
            	interceptorIndex：拦截器索引，控制拦截器afterCompletion()的执行
            */
            mappedHandler = getHandler(processedRequest);
            if (mappedHandler == null) {
                noHandlerFound(processedRequest, response);
                return;
            }

            // Determine handler adapter for the current request.
           	// 通过控制器方法创建相应的处理器适配器，调用所对应的控制器方法
            HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

            // Process last-modified header, if supported by the handler.
            String method = request.getMethod();
            boolean isGet = "GET".equals(method);
            if (isGet || "HEAD".equals(method)) {
                long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
                if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
                    return;
                }
            }
			
            // 调用拦截器的preHandle()
            if (!mappedHandler.applyPreHandle(processedRequest, response)) {
                return;
            }

            // Actually invoke the handler.
            // 由处理器适配器调用具体的控制器方法，最终获得ModelAndView对象
            mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

            if (asyncManager.isConcurrentHandlingStarted()) {
                return;
            }

            applyDefaultViewName(processedRequest, mv);
            // 调用拦截器的postHandle()
            mappedHandler.applyPostHandle(processedRequest, response, mv);
        }
        catch (Exception ex) {
            dispatchException = ex;
        }
        catch (Throwable err) {
            // As of 4.3, we're processing Errors thrown from handler methods as well,
            // making them available for @ExceptionHandler methods and other scenarios.
            dispatchException = new NestedServletException("Handler dispatch failed", err);
        }
        // 后续处理：处理模型数据和渲染视图
        processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
    }
    catch (Exception ex) {
        triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
    }
    catch (Throwable err) {
        triggerAfterCompletion(processedRequest, response, mappedHandler,
                               new NestedServletException("Handler processing failed", err));
    }
    finally {
        if (asyncManager.isConcurrentHandlingStarted()) {
            // Instead of postHandle and afterCompletion
            if (mappedHandler != null) {
                mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
            }
        }
        else {
            // Clean up any resources used by a multipart request.
            if (multipartRequestParsed) {
                cleanupMultipart(processedRequest);
            }
        }
    }
}
123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293
```

##### d>processDispatchResult()

```java
private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,
                                   @Nullable HandlerExecutionChain mappedHandler, @Nullable ModelAndView mv,
                                   @Nullable Exception exception) throws Exception {

    boolean errorView = false;

    if (exception != null) {
        if (exception instanceof ModelAndViewDefiningException) {
            logger.debug("ModelAndViewDefiningException encountered", exception);
            mv = ((ModelAndViewDefiningException) exception).getModelAndView();
        }
        else {
            Object handler = (mappedHandler != null ? mappedHandler.getHandler() : null);
            mv = processHandlerException(request, response, handler, exception);
            errorView = (mv != null);
        }
    }

    // Did the handler return a view to render?
    if (mv != null && !mv.wasCleared()) {
        // 处理模型数据和渲染视图
        render(mv, request, response);
        if (errorView) {
            WebUtils.clearErrorRequestAttributes(request);
        }
    }
    else {
        if (logger.isTraceEnabled()) {
            logger.trace("No view rendering, null ModelAndView returned.");
        }
    }

    if (WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted()) {
        // Concurrent handling started during a forward
        return;
    }

    if (mappedHandler != null) {
        // Exception (if any) is already handled..
        // 调用拦截器的afterCompletion()
        mappedHandler.triggerAfterCompletion(request, response, null);
    }
}
12345678910111213141516171819202122232425262728293031323334353637383940414243
```

### 4、SpringMVC的执行流程

1. 用户向服务器发送请求，请求被SpringMVC 前端控制器 DispatcherServlet捕获。
2. DispatcherServlet对请求URL进行解析，得到请求资源标识符（URI），判断请求URI对应的映射：

a) 不存在

i. 再判断是否配置了mvc:default-servlet-handler

ii. 如果没配置，则控制台报映射查找不到，客户端展示404错误

![在这里插入图片描述](https://res.mowangblog.top/img/2021/10/8df6a30e77184eb0bc1547dae5838f65.png)

iii. 如果有配置，则访问目标资源（一般为静态资源，如：JS,CSS,HTML），找不到客户端也会展示404错误

![在这里插入图片描述](https://res.mowangblog.top/img/2021/10/67d25736b3b74dfe9145acedb0e8656d.png)

b) 存在则执行下面的流程

1. 根据该URI，调用HandlerMapping获得该Handler配置的所有相关的对象（包括Handler对象以及Handler对象对应的拦截器），最后以HandlerExecutionChain执行链对象的形式返回。
2. DispatcherServlet 根据获得的Handler，选择一个合适的HandlerAdapter。
3. 如果成功获得HandlerAdapter，此时将开始执行拦截器的preHandler(…)方法【正向】
4. 提取Request中的模型数据，填充Handler入参，开始执行Handler（Controller)方法，处理请求。在填充Handler的入参过程中，根据你的配置，Spring将帮你做一些额外的工作：

a) HttpMessageConveter： 将请求消息（如Json、xml等数据）转换成一个对象，将对象转换为指定的响应信息

b) 数据转换：对请求消息进行数据转换。如String转换成Integer、Double等

c) 数据格式化：对请求消息进行数据格式化。 如将字符串转换成格式化数字或格式化日期等

d) 数据验证： 验证数据的有效性（长度、格式等），验证结果存储到BindingResult或Error中

1. Handler执行完成后，向DispatcherServlet 返回一个ModelAndView对象。
2. 此时将开始执行拦截器的postHandle(…)方法【逆向】。
3. 根据返回的ModelAndView（此时会判断是否存在异常：如果存在异常，则执行HandlerExceptionResolver进行异常处理）选择一个适合的ViewResolver进行视图解析，根据Model和View，来渲染视图。
4. 渲染视图完毕执行拦截器的afterCompletion(…)方法【逆向】。
5. 将渲染结果返回给客户端。
