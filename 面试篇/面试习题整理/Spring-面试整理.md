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
