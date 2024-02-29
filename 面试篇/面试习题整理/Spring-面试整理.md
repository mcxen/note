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
