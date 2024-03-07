# Spring

## Spring

### 1. Spring事务有哪两种？

Spring 提供了两种事务：

1. 编程式事务
2. 声明式事务

它们的实现如下。

1. 编程式事务

```java
@RestController
public class UserController {
    // 事务管理器
    @Resource
    private DataSourceTransactionManager dataSourceTransactionManager;
    // 定义事务属性
    @Resource
    private TransactionDefinition transactionDefinition;
    @Resource
    private UserService userService;

    @RequestMapping("/sava")
    public Object save(User user) {
        // 开启事务
        TransactionStatus transactionStatus = dataSourceTransactionManager
                .getTransaction(transactionDefinition);
        // 插入数据库
        int result = userService.save(user);
        // 提交事务
        dataSourceTransactionManager.commit(transactionStatus);
//        // 回滚事务
//        dataSourceTransactionManager.rollback(transactionStatus);
        return result;
    }
}
```

2 声明式事务

声明式事务的实现非常简单，只需要给类或方法上添加 @Transactional 注解即可，如下代码所示：

```java
@RequestMapping("/save")
@Transactional // 添加此注解就是声明式事务
public Object save(User user) {
  int result = userService.save(user);
  return result;
}
```



### 2. @Transactional实现原理？

@Transactional 注解的实现原理是基于 Spring AOP，Spring AOP 又是基于动态代理（模式）的实现。

在 Spring 中，@Transactional 注解会通过 AOP 机制生成一个代理 connection 对象，并将其放入 DataSource 实例的某个与 DataSourceTransactionManager 相关的某处容器中。这样，当开始执行目标方法之前先开启事务，如果方法正常执行完成则提交事务，如果执行中出现了异常，则会回滚事务。



### 3. 拦截器和过滤器的区别

拦截器和过滤器的区别主要体现在以下 5 点：

1. **出身不同**：过滤器来自于 Servlet，而拦截器来自于 Spring 框架；
2. **触发时机不同**：请求的执行顺序是：请求进入容器 > 进入过滤器 > 进入 Servlet > 进入拦截器 > 执行控制器（Controller），所以过滤器和拦截器的执行时机，是过滤器会先执行，然后才会执行拦截器，最后才会进入真正的要调用的方法；
3. **底层实现不同**：过滤器是基于方法回调实现的，拦截器是基于动态代理（底层是反射）实现的；
4. **支持的项目类型不同**：过滤器是 Servlet 规范中定义的，所以过滤器要依赖 Servlet 容器，它只能用在 Web 项目中；而拦截器是 Spring 中的一个组件，因此拦截器既可以用在 Web 项目中，同时还可以用在 Application 或 Swing 程序中；
5. **使用的场景不同**：因为拦截器更接近业务系统，所以拦截器主要用来实现项目中的业务判断的，**比如：登录判断、权限判断、日志记录等业务；**而过滤器通常是用来实现通用功能过滤的，比如：敏感词过滤、字符集编码设置、响应数据压缩等功能。





### 3. Spring 和 Spring MVC 和 Spring Boot的区别

Spring Boot 和 Spring MVC 都是 Spring Framework 的组件，用于开发基于 Java 的 Web 应用程序，但它们有不同的关注点和应用场景。

- Spring MVC：Spring MVC 是 Spring Framework 的一部分，用于构建基于 MVC（Model-View-Controller）模式的 Web 应用程序。它提供了处理 HTTP 请求、路由、视图渲染等功能，使开发人员能够更轻松地构建 Web 应用程序。

  特点和用途：

  - 基于传统的 MVC 模式，将应用程序分为模型（数据）、视图（UI）和控制器（处理逻辑）三个层次。
  - 使用注解和配置来定义请求映射、参数绑定、视图解析等。
  - 支持多种视图技术，如 JSP、Thymeleaf 等。
  - 可以与其他 Spring 组件集成，如 Spring Data、Spring Security 等。
  - 更适合传统的 Web 应用程序开发，适合需要精细控制请求处理流程的场景。

- Spring Boot：Spring Boot 是一个用于简化 Spring 应用程序开发的框架，它通过提供预配置的默认设置，减少了开发人员在配置上的工作量。Spring Boot 的目标是让开发者能够快速创建独立的、基于 Spring 的应用程序。

  特点和用途：

  - 提供了约定大于配置的原则，通过自动配置和默认设置，减少了开发者的配置工作。
  - 集成了内嵌的 Web 服务器，如 Tomcat、Jetty 等，使得构建可独立运行的 Web 应用程序更加简单。
  - 内置了一些常用的功能，如健康检查、度量指标、配置管理等。
  - 提供了丰富的插件和 starter（启动器），简化了集成各种第三方库和框架的流程。
  - 更适合快速构建微服务、RESTful API 等应用，适用于迅速开发和部署的场景。

总之，Spring MVC 主要关注 Web 应用程序的请求处理和页面渲染，适用于传统的 MVC 架构。而 Spring Boot 旨在简化整个 Spring 应用程序的开发和部署过程，适合快速构建现代化的 Web 应用、微服务等。它们可以一起使用，Spring Boot 中也包含了 Spring MVC 的功能，从而在开发过程中能够更加高效地使用 Spring 框架。
