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

### 4. IOC和AOP介绍一下

IOC，即控制反转，是Spring框架的核心特性之一。在传统的程序设计中，我们**直接在对象内部通过new关键字**来创建依赖的对象，这种方式会导致代码之间高度耦合，不利于测试和维护。而IoC的思想是，将这些对象的创建权交给Spring容器来管理，**由Spring容器负责创建对象并注入依赖**。这样，对象与对象之间的依赖关系就交由Spring来管理了，实现了程序的解耦合。在项目中，我们可以通**过Spring的配置文件或者注解来定义Bean**，然后由Spring容器来创建和管理这些Bean。**在Mybatis中，我们可以将SqlSessionFactory、Mapper**等对象的创建和依赖关系交由Spring来管理。

AOP，即面向切面编程，是Spring提供的另一个重要特性。它允许开发者在不修改原有业务逻辑的情况下，增强或扩展程序的功能。AOP通过预定义的切点（例如方法执行前、方法执行后等）来织入切面逻辑，从而实现对原有功能的增强。在项目中，我们可以使用AOP来实现日志记录、事务管理、安全检查等功能。在Mybatis中，我们可以使用AOP来实现对数据库操作的日志记录、性能监控等功能。

### 5. 事务传播行为

在项目中，选择使用特定的事务传播行为是基于业务需求和操作复杂性的考虑。例如，对于需要保证多个操作原子性的场景，会选择`PROPAGATION_REQUIRED`来确保所有操作在同一个事务中完成。而在需要独立处理某个操作，不受其他事务影响时，可能会选择`PROPAGATION_REQUIRES_NEW`来创建一个新的事务。同时，也会根据性能优化的需要，选择合适的事务传播行为来减少事务的开销和资源的占用。总之，选择事务传播行为时需要综合考虑业务需求、数据一致性、系统性能和资源利用等多个方面。

> 正确的事务传播行为可能的值如下:
>
> **1.`TransactionDefinition.PROPAGATION_REQUIRED`**
>
> 使用的最多的一个事务传播行为，我们平时经常使用的`@Transactional`注解默认使用就是这个事务传播行为。如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务。
>
> **`2.TransactionDefinition.PROPAGATION_REQUIRES_NEW`**
>
> 创建一个新的事务，如果当前存在事务，则把当前事务挂起。也就是说不管外部方法是否开启事务，`Propagation.REQUIRES_NEW`修饰的内部方法会新开启自己的事务，且开启的事务相互独立，互不干扰。
>
> **3.`TransactionDefinition.PROPAGATION_NESTED`**
>
> 如果当前存在事务，则创建一个事务作为当前事务的嵌套事务来运行；如果当前没有事务，则该取值等价于`TransactionDefinition.PROPAGATION_REQUIRED`。
>
> **4.`TransactionDefinition.PROPAGATION_MANDATORY`**
>
> 如果当前存在事务，则加入该事务；如果当前没有事务，则抛出异常。（mandatory：强制性）
>
> 这个使用的很少。
>
> 若是错误的配置以下 3 种事务传播行为，事务将不会发生回滚：
>
> - **`TransactionDefinition.PROPAGATION_SUPPORTS`**: 如果当前存在事务，则加入该事务；如果当前没有事务，则以非事务的方式继续运行。
> - **`TransactionDefinition.PROPAGATION_NOT_SUPPORTED`**: 以非事务方式运行，如果当前存在事务，则把当前事务挂起。
> - **`TransactionDefinition.PROPAGATION_NEVER`**: 以非事务方式运行，如果当前存在事务，则抛出异常。

### 7. Bean的生命周期

- Bean 容器找到配置文件中 Spring Bean 的定义。
- Bean 容器利用 Java Reflection API 创建一个 Bean 的实例。
- 如果涉及到一些属性值 利用 `set()`方法设置一些属性值。
- 如果 Bean 实现了 `BeanNameAware` 接口，调用 `setBeanName()`方法，传入 Bean 的名字。
- 如果 Bean 实现了 `BeanClassLoaderAware` 接口，调用 `setBeanClassLoader()`方法，传入 `ClassLoader`对象的实例。
- 如果 Bean 实现了 `BeanFactoryAware` 接口，调用 `setBeanFactory()`方法，传入 `BeanFactory`对象的实例。
- 与上面的类似，如果实现了其他 `*.Aware`接口，就调用相应的方法。
- 如果有和加载这个 Bean 的 Spring 容器相关的 `BeanPostProcessor` 对象，执行`postProcessBeforeInitialization()` 方法
- 如果 Bean 实现了`InitializingBean`接口，执行`afterPropertiesSet()`方法。
- 如果 Bean 在配置文件中的定义包含 init-method 属性，执行指定的方法。
- 如果有和加载这个 Bean 的 Spring 容器相关的 `BeanPostProcessor` 对象，执行`postProcessAfterInitialization()` 方法
- 当要销毁 Bean 的时候，如果 Bean 实现了 `DisposableBean` 接口，执行 `destroy()` 方法。
- 当要销毁 Bean 的时候，如果 Bean 在配置文件中的定义包含 destroy-method 属性，执行指定的方法。

图示：

![img](https://images.xiaozhuanlan.com/photo/2019/24bc2bad3ce28144d60d9e0a2edf6c7f.jpg)

Spring Bean 生命周期

### 8.Spring 三级缓存作用

Spring 解决循环依赖的核心就是提前暴露对象，而提前暴露的对象就是放置于第二级缓存中。下表是三级缓存的说明：

| 名称                  | 描述                                                         |
| --------------------- | ------------------------------------------------------------ |
| singletonObjects      | 一级缓存，存放完整的 Bean。                                  |
| earlySingletonObjects | 二级缓存，存放提前暴露的Bean，Bean 是不完整的，未完成属性注入和执行 init 方法。 |
| singletonFactories    | 三级缓存，存放的是 Bean 工厂，主要是生产 Bean，存放到二级缓存中。 |



### 8. Spring是如何解决的循环依赖？

> Spring解决循环依赖是有前置条件的
>
> 1. **出现循环依赖的Bean必须要是单例**
> 2. **依赖注入的方式不能全是构造器注入**的方式

答：

**Spring通过三级缓存解决了循环依赖**，其中一级缓存为单例池（`singletonObjects`）,二级缓存为早期曝光对象`earlySingletonObjects`，三级缓存为早期曝光对象工厂（`singletonFactories`）。

当A、B两个类发生循环引用时，在A完成实例化后，就使用实例化后的对象去创建一个对象工厂，并添加到三级缓存中，如果A被AOP代理，那么通过这个工厂获取到的就是A代理后的对象，如果A没有被AOP代理，那么这个工厂获取到的就是A实例化的对象。

当A进行属性注入时，会去创建B，同时B又依赖了A，所以创建B的同时又会去调用getBean(a)来获取需要的依赖，此时的getBean(a)会从缓存中获取：

第一步，先获取到三级缓存中的工厂；
第二步，调用对象工工厂的getObject方法来获取到对应的对象A，得到这个对象A后将其注入到B中。
紧接着B会走完它的生命周期流程，包括初始化、后置处理器等。当B创建完后，会将B再注入到A中，此时A再完成它的整个生命周期。

![image-20200706171514327](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/format,png.png)

> 面试官：”为什么要使用三级缓存呢？二级缓存能解决循环依赖吗？“

二级缓存可以解决循环依赖，但是会破坏AOP代理的原则

答：如果要使用二级缓存解决循环依赖，意味着**所有Bean在实例化后就要完成AOP代理，这样违背了Spring设计的原则**，Spring在设计之初就是通过`AnnotationAwareAspectJAutoProxyCreator`这个后置处理器来在Bean生命周期的最后一步来完成AOP代理，而不是在实例化后就立马进行AOP代理。

假设不使用三级缓存，直接在二级缓存中

![image-20200706172523258](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/format,png-20240323173054839.png)

上面两个流程的唯一区别在于为A对象创建代理的时机不同，在使用了三级缓存的情况下为A创建代理的时机是在B中需要注入A的时候，而不使用三级缓存的话在A实例化后就需要马上为A创建代理然后放入到二级缓存中去。

### 9.什么情况下循环依赖可以被处理？

在回答这个问题之前首先要明确一点，Spring解决循环依赖是有前置条件的

1. **出现循环依赖的Bean必须要是单例**
2. **依赖注入的方式不能全是构造器注入**的方式（很多博客上说，只能解决setter方法的循环依赖，这是错误的）

其中第一点应该很好理解，第二点：不能全是构造器注入是什么意思呢？我们还是用代码说话

```java
@Component
public class A {
//    @Autowired
//    private B b;
    public A(B b) {

    }
}

@Component
public class B {

//    @Autowired
//    private A a;

    public B(A a){

    }
}
```

在上面的例子中，A中注入B的方式是通过构造器，B中注入A的方式也是通过构造器，这个时候循环依赖是无法被解决，如果你的项目中有两个这样相互依赖的Bean，在启动时就会报出以下错误：

```
Caused by: org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'a': Requested bean is currently in creation: Is there an unresolvable circular reference?
```

为了测试循环依赖的解决情况跟注入方式的关系，我们做如下四种情况的测试

| 依赖情况               | 依赖注入方式                                       | 循环依赖是否被解决 |
| ---------------------- | -------------------------------------------------- | ------------------ |
| AB相互依赖（循环依赖） | 均采用setter方法注入                               | 是                 |
| AB相互依赖（循环依赖） | 均采用构造器注入                                   | 否                 |
| AB相互依赖（循环依赖） | A中注入B的方式为setter方法，B中注入A的方式为构造器 | 是                 |
| AB相互依赖（循环依赖） | B中注入A的方式为setter方法，A中注入B的方式为构造器 | 否                 |

具体的测试代码跟简单，我就不放了。从上面的测试结果我们可以看到，不是只有在setter方法注入的情况下循环依赖才能被解决，即使存在构造器注入的场景下，循环依赖依然被可以被正常处理掉。

参考： https://www.panziye.com/java/4710.html

