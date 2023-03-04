### TODOS

**3天的时间复习Java部分**

---

#### Java基础

- 八大基本类型
- String
- StringBuilder / StringBuffer
- 封装继承多态

#### Java常用类

- 查看码云

#### Java高级之集合

- 查看码云

#### Java高级之锁

- 查看码云

#### Java高级之多线程

- 查看码云

#### Java高级之IO

- 字符流、字节流
- 输入流、输出流
- BIO 阻塞io
- NIO JDK4 新特性 非阻塞IO
- AIO JDK7 异步非阻塞IO

#### Java高级之异常

- 非运行时异常 （受检测异常）
  - java.lang.FileNotFoundException
  - java.lang.SQLExceprion
  - java.lang.ClassNotFoundException
  - java.lang.IOExcetion
  - java.lang.NoSuchMethodExcetion
  - java.lang.CloneNotSupportedException
- 运行时异常 （不受检测异常）
  - java.lang.ArrayIndexofBoundsException
  - java.lang.ClassCastException
  - java.lang.NumberFormatException
  - java.lang.IndexOutofBoundsException
  - java.lang.NullPointerException
- 不常见的异常：OOM异常
  - java.lang.OutofMemeoryError:Java heap space 
    - Java堆溢出
  - java.lang.StackOverflowError
    - 虚拟机栈溢出异常，因为栈的深度是有限的。 或者是栈帧容量太小，定义了大量的局部变量
  - java.lang.OutOfMemaryError: PermGen space
    - 方法区异常，永久代异常。JDK6才能出现的异常。
  - java.lang.OutOfMemoryError -  at sun.misc.Unsafe.allocateMemory(Native Method)
    - 直接内存导致的内存溢出
  - java.lang.StackOverflowError
  - java.lang.OutOfMemoryError: Java heap space
  - java.lang.OutOfMemoryError: GC overhead limit exceeded
    - 执行了垃圾回收，但是回收的很少
  - java.lang.OutOfMemoryError-->Metaspace
  - java.lang.OutOfMemoryError: Direct buffer memory
  - java.lang.OutOfMemoryError: unable to create new native thread
    - 没有足够的内存分配线程
  - java.lang.OutOfMemoryError：Metaspace
  - java.lang.OutOfMemoryError: Requested array size exceeds VM limit
    - JVM限制了数组的最大长度 通常为 Integer.MAX_VALUE-2 
    - 也就是创建数组的时候，首先会寻找是否由这么大的连续地址可分配
  - java.lang.OutOfMemoryError: Out of swap space
  - java.lang.OutOfMemoryError：Kill process or sacrifice child
  - 面试官问：说一说内存泄漏和内存溢出
    - 内存溢出（out of memory），是指程序在申请内存的时候，没有足够的内存空间供其使用，出现了out of memory，比如申请了一个Integer，但是给他存了Long才能存取的数，这就是内存溢出
    - 内存泄漏（memory leak），是指程序申请内存之后，无法释放已申请的内存空间，一次内存泄漏危害可以忽略，但是多次内存堆积，就会导致最终内存不够用 从而出现 out of memory异常

#### JavaWeb

- C/S架构和B/S架构
- 常见的Servlet容器
  - Tomcat、JBoss、Jetty
- Tomcat的常见配置
- 什么是Servlet
  - Servlet是sun公司提供的一门专门开发动态web资源的技术
  - sun公司在api中提供了Servlet接口，如果想使用Servlet进行开发，首先先编写一个类基础Servlet，然后将其放到Servlet容器中执行
- Servlet的执行流程 / 生命周期 / 请求流程
  - 首先检测是否加载了Servlet
  - 如果没有加载Servlet，就先加载Servlet，创建一个Servlet实例
  - 然后调用init方法对Servlet进行初始化
  - 然后调用Service方法执行业务流程 service可以是doGet、goPost
  - 当其执行完毕之后，调用 destroy 方法对Servlet卸载之前的处理
- Servlet的初始化参数可以在web.xml 中定义，也可以使用Servlet中使用注解定义
- HttpServletRequest常用的方法
  - request.getSession()
  - request.getAttraxxx
  - requset.setAttrxxx
  - request.getLocalAddr
  - request.getLocalName
- Serlvet线程安全问题了解吗
  - Servlet是单例的 是线程安全的，但是如果定义了静态变量，其就可能不是线程安全的。所以在使用Servlet编程的时候要使用局部变量，才可以保证线程安全
- Http协议无状态带来的问题知道吗？如何解决？
  - Http是无状态的，也就是说，http不知道上一个页面和下一个页面是哪个
  - 解决：使用参数传递机制，就是每个页面都把数据传递过去，在另外一个页面接收，然后再传递过去
  - 解决：使用Cookie
  - 解决：使用Session
- Session的原理
  - 浏览器第一次请求网站， 服务端生成 Session ID。
  - 把生成的 Session ID 保存到服务端存储中。
  - 把生成的 Session ID 返回给浏览器，通过 set-cookie。
  - 浏览器收到 Session ID， 在下一次发送请求时就会带上这个 Session ID。
  - 服务端收到浏览器发来的 Session ID，从 Session 存储中找到用户状态数据，会话建立。
  - 此后的请求都会交换这个 Session ID，进行有状态的会话。
- Servlet和JSP
  - JSP本身就是一个Servlet，在JSP编译之后，会生成 class 文件
  - 其位置会在 work\Catalina\localhost\web\org\apache\jsp  目录下
  - JSP只在第一次访问的时候编译成class文件，所以第一次访问的速度比较慢，
- Servlet的三大作用域
  - request 在一次请求中有效
  - session 在一次会话中有效
  - application 在整个应用中有效
- JSP指令
  -  三个编译指令为：page、include、taglib。
  -  七个动作指令为：jsp:forward、jsp:param、jsp:include、jsp:plugin、jsp:useBean、jsp:setProperty、jsp:getProperty。 
- JSP九大内置对象
  - request
  - reponse
  - session
  - application
  - out
  - pageContext
  - config
  - page
  - exception
- JSP四大作用域
  - page 当前页面
  - request 一次请求
  - session 一次会话
  - application 整个应用
- EL表达式
- JSTL表达式
- JavaBean的内省机制
  - Javabean
    - 所有的成员变量私有化
    - 有空参构造方法
    - 属性由get/set方法获取和设置
  - 内省机制
    - 让后台的model对象直接统一接收表单传递过来的参数

- 转发和重定向区别
  - 请求转发
    - 通过 request调用
    - request.getRequestDispacher("xxx").forward(request,reponse);
  - 重定向
    - reponse.sendRedirect("xxx");
  - 区别
    - 转发是服务器行为，重定向是客户端行为
    - 转发请求路径不变，重定向请求路径改变
    - 转发只有一次请求，而重定向由两次请求
    - 转发的网址必须是本站的网址，而重定向的网址可以值站外的网址
- 拦截器
  - 进入到Servlet之后，的拦截处理
- 监听器
  - 整个应用中只初始化一次，可以监听整个应用，随着应用的销毁而销毁
- 过滤器
  - 对请求资源进行过滤，执行时机是先于拦截器的，其作用时机在进入Servlet之前
- 简单理解
  - 过滤器（Filter）：当你有一堆东西的时候，你只希望选择符合你要求的某一些东西。定义这些要求的工具，就是过滤器。
  - 拦截器（Interceptor）：在一个流程正在进行的时候，你希望干预它的进展，甚至终止它进行，这是拦截器做的事情。
  - 监听器（Listener）：当一个事件发生的时候，你希望获得这个事件发生的详细信息，而并不想干预这个事件本身的进程，这就要用到监听器。 

#### JVM

- 查看码云

#### JDK新特性

- JDK4
  - XML
  - Logging api
  - 支持ipv6
  - 增加断言
  - nio
  - 链式异常处理：也就是可以使用一个异常去处理另外一个异常
- JDK5
  - 包装类型
  - 枚举
  - 泛型
  - 可变长参数
  - 注解
  - foreach
  - 增强for循环
  - 静态导入
  - 类似C语言的格式化输出
  - JUC - 阻塞队列等数据结构
  - 线程池
  - Arrays 工具类/StringBuilder 线程不安全的拼接 StringBuffer是线程安全的，是在JDK1.0出现的
- JDK6
  - JSR223 脚本引擎
  - JDBC4.0规范
  - 其他
- JDK7
  - suppress异常
  - 支持捕获多个异常
  - try-with-resource 可以在try(资源) 中释放资源
  - JSR292与InvokeDynamic 支持在JVM上运行动态类型语言
  - JDBC4.1规范
  - Path接口：重要接口变更
  - Files工具类
  - Jcmd命令行工具 为了代替jps 包含jps大部分功能和拓展功能
  - fork/join框架 并行任务框架 把任务分隔为小任务去执行，然后再进行合并
  - 增强泛型推断，也就是后面不需要再写泛型类型
  - 二进制支持
  - 大数字的下划线支持，仅仅支持数字，会在编译期进行处理
  - Switch添加String类型，但是底层使用的是String的Hash，而Hash本身就是 int 类型，所以是支持的
- JDK8
  - 增加线程安全的日期类
  - 增加Steam流式计算
  - 增加Lambda表达式 代替繁多的匿名内部类 - 必须是函数式接口
  - 增加函数式接口
  - HashMap底层数据结构优化
  - 方法引用和构造函数调用
  - optional 容器 - 为了处理空指针异常
  - 接口中可以增加默认方法和静态方法
- JDK9
  - 模块化系统
  - String 底层使用final数组，从char数组更改为byte数组
  - StrringBuffer和StringBuilder也做了和String一样的变更
  - 增加了Jshell
  - 接口中可以定义私有方法
  - 改进了 try-with-resource 在JDK7和JDK8中，只能在try(resources) 中定义资源并自动释放，在JDK9之后，可以在外部进行初始化，在括号内引用，也可以自动释放资源
  - 改进钻石操作符
  - 限制使用单独下划线操作符 _
  - 快速创建只读集合
  - 增强Stream API
- JDK10
  - var 声明变量，是指一种自动类型推断
  - 移除 javah
  - Full GC 的 G1垃圾收集器
- JDK11
  - HttpClient转正
  - 密码学的改进
  - java命令执行java文件
  - 更加灵活的String
  - 更方便的io
- JDK12
  - switch语句升级，也可以作为语句
  - 数字转字符串 NumberFormat
  - String 的成员函数很多
- JDK13
  - switch优化更新，可返回信息
  - 文本块升级，也就是之前需要换行，转义的字符串很麻烦，现在使用 """ xxx """ 三个引号包起来即可
  - SQL变化，也相当于是文本块的升级了
- JDK14
  - switch最终版本
  - 垃圾回收器
  - CMS被永久删除
  - instanceof 模式匹配
  - record 修饰符 ，相当于增加了 Lombok的注解，@Data @Allxxx @Noxxx
- JDK15
  - ZGC转正
    - Java11引入
    - JDK9之后默认的是G1
    - 但是JDK15默认的还是G1
    - 文本块功能转正 TextBlocks
  - instanceof 可以不必强转 
    - if (obj instanceof String str)

#### 设计模式

- 单例设计模式
- 工厂模式

#### MySQL

- 查看MySQL的码云笔记

#### SpringMVC

- **执行流程**
  - 当用户访问请求的时候，首先经过DispatcherServlet进行初始化
  - 然后执行HandlerMapping找到对应的Controller映射，如果没找到抛出异常，如果找到了继续下一步
  - 执行HanderAdapter，对接口参数和请求传递的参数进行校验，如果数据校验失败，抛出异常，成功则继续进行
  - 然后controller返回ModelAndView对象，提交给DispatcherServlet，进行视图解析，如果没有找到视图，抛出异常，否则继续
  - 找到了对应的视图，跳转到视图，然后解析数据渲染页面给用户
- 常用注解
  - @Controller
  - @RestController
  - @RequestMapping
  - @GetMapping
  - @PostMapping
  - @ReponseBody
  - @RequestBody
  - @PathVariable

#### Spring

- 组成部分
  - core
  - aop
  - dao
  - orm
  - web
  - java ee

- 就是为了简化Java的开发、替代重量级Java企业级开发框架

- Spring的核心是IoC和DI 和AOP
- IoC容器是 对象的容器，在JavaWeb阶段的时候，我们创建对象需要手动去new对象。代码之间的耦合度很高，但是有了IoC容器之后，我们把创建对象的权力交给了IoC容器，让容器去帮我们管理所有的bean。也就是我们需要什么对象，IoC就自动把我们需要的bean拿过来（太监 - 皇上 - 后宫的例子）也就是把对象交给别人去创建
- 依赖注入
  - 构造器
  - set方法
  - 注解
    - @Autowired
  - 命名空间
  - 自动装配
    - 根据名字装配
    - 根据类型装配
- AOP - 面向切面编程
  - JDK代理
    - 只能面向接口
  - Cglib代理
    - 可以面向类
  - 切⼊点表达式，指定拦截哪些类的哪些⽅法； 给指定的类在运⾏的时候植⼊切⾯类代码。
    - @Aspect 指定为切面类
    - @Before 前置通知，目标方法执行之前执行 里面的值为切入点表达式
    - @After 后置通知，目标方法执行之后执行
    - @AfterReturning   返回后通知（异常不执行）
    - @AfterThrowing 异常通知：出现异常的时候执行
    - @Around  环绕通知：环绕目标方法执行
    - @PointCut 指定切入点表达式
- 事务
  - 编程式事务
  - 声明式事务
    - service调用异常上抛到controller。如果式编译时异常不会自动回滚，如果是运行时异常，会回滚
    - 当前类下使⽤⼀个没有事务的⽅法去调⽤⼀个有事务的⽅法。没有事务的存在。如果是在本类中没有事务的⽅法来调⽤标注注解 @Transactional ⽅法，最后的结论是没有事务的
  - **事务传播机制**
    - 基于接⼝代理(JDK代理)
      - 基于接⼝代理，凡是类的⽅法⾮public修饰，或者⽤了static关键字修饰，那这些⽅法都不能被Spring AOP增强
    - 基于CGLib代理(⼦类代理)
      - 基于⼦类代理，凡是类的⽅法使⽤了private、static、final修饰，那这些⽅法都不能被
        Spring AOP增强
- Bean的生命周期

![]( https://upload-images.jianshu.io/upload_images/3131012-0fdb736b21c8cc31.png )

- ApplicationContext容器中，Bean的生命周期流程如上图所示，流程大致如下：

  1.首先容器启动后，会对scope为singleton且非懒加载的bean进行实例化，

  2.按照Bean定义信息配置信息，注入所有的属性，

  3.如果Bean实现了BeanNameAware接口，会回调该接口的setBeanName()方法，传入该Bean的id，此时该Bean就获得了自己在配置文件中的id，

  4.如果Bean实现了BeanFactoryAware接口,会回调该接口的setBeanFactory()方法，传入该Bean的BeanFactory，这样该Bean就获得了自己所在的BeanFactory，

  5.如果Bean实现了ApplicationContextAware接口,会回调该接口的setApplicationContext()方法，传入该Bean的ApplicationContext，这样该Bean就获得了自己所在的ApplicationContext，

  6.如果有Bean实现了BeanPostProcessor接口，则会回调该接口的postProcessBeforeInitialzation()方法，

  7.如果Bean实现了InitializingBean接口，则会回调该接口的afterPropertiesSet()方法，

  8.如果Bean配置了init-method方法，则会执行init-method配置的方法，

  9.如果有Bean实现了BeanPostProcessor接口，则会回调该接口的postProcessAfterInitialization()方法，

  10.经过流程9之后，就可以正式使用该Bean了,对于scope为singleton的Bean,Spring的ioc容器中会缓存一份该bean的实例，而对于scope为prototype的Bean,每次被调用都会new一个新的对象，期生命周期就交给调用方管理了，不再是Spring容器进行管理了

  11.容器关闭后，如果Bean实现了DisposableBean接口，则会回调该接口的destroy()方法，

  12.如果Bean配置了destroy-method方法，则会执行destroy-method配置的方法，至此，整个Bean的生命周期结束

#### Mybatis

- Mybatis是一个apache的开源的半自动的ORM框架
  - 在2010年从ibatis更名为mybatis
- 无论是Mybatis还是SpringDataJPA、还是Hibernate，都是对JDBC的再一层封装
- Mybatis是一个ORM框架，也就是 Object Relation Mapper 对象关系映射

- 支持自定义SQL 支持动态SQL - 核心处理类 AbstractSQL
- Mybatis的核心流程
  - 加载配置文件，使用Reader读取
  - 使用SqlSessionFactoryBuilder创建SqlSessionFactory对象
  - 然后获取SqlSession对象
  - 事务默认开启
  - 通过SqlSession读取映射文件
  - 完成相应的CRUD操作
  - 完成之后提交事务
  - 然后关闭资源
- Mybatis分页
  - 使用第三方分页插件实现
  - 自己实现分页，也就是传递参数
  - 底层原理还是使用的是 limit 语句
- Mybatis的一大特点就是动态SQL
  - 也就是从之前的手动拼接SQL，到现在Mybatis帮我们拼接SQL
  - 动态SQL
    - where if  <if test="username!=null"> and username = #{username} </>
    - set <if test="username!=null">  username = #{username} ,</>
    - delete xxx from xxx where id in <foreach collections="array" open="(" close=")" separator="," item="ids"> #{ids}</foreach>
- Mybatis的占位符
  - #{} 是需要解析传递过来的参数的
  - ${} 是把传递过来的参数直接填充到SQL语句中的
- 主键生成策略
  - 在insert标签 中可以获取自动生成的主键 useGeneratedKeys
- resultMap 和 resultType
  - resultMap 输出的是结果集的映射，里面可以是复杂的映射关系
  - resultType 输出的是简单对象
- 使⽤association和collection完成⼀对⼀和⼀对多⾼级映射。
  - association 是一对一映射，其实就是返回一个POJO对象封装到这个结果集中
  - collection 是一对多映射，其实就是返回一个POJO对象集合封装到结果集中
- 配置别名
  - typeAliases别名
- 引入外部配置文件
- 懒加载
  - 也就是说一个对象中有另外一个对象，那么这个对象只有用到的时候才会去查询
- 可以配置扫描mapper映射包
- Mybatis缓存
  - 意义：将用户经常查询的SQL存放在缓存中，这样用户每次查询都不需要在进行磁盘IO，提高了效率
  - 默认是开启一级缓存的
    - Mybatis的一级缓存是SqlSession级别的，SqlSession只能访问自己的一级缓存的数据
    - 用户第一次发送一个查询语句，将其放到缓存中，数据结构是一个Map 
    - key：hashcode+sql+sql输⼊参数+输出参数（sql的唯⼀标识）
    - 如果用户执行了增加、删除、修改触发了Commit操作，那么一级缓存就会全部被清空
    - 此时再次进行查询，就是从数据库中进行查询，然后再进行缓存
    - 其每次查询都会先从缓存中查询，如果没有查询的到，就执行数据库查询，然后把数据放在缓存中
    - Mybatis默认是支持一级缓存的
    - 但是再和Spring整合的过程中，最后会关闭SqlSession，这就意味着缓存失效了
  - 二级缓存
    - 而二级缓存是Mapper级别的，是可以跨SqlSession的
    - mapper同一个命名空间
    - 每次查询会看是不是已经开启了二级缓存，如果有就从二级缓存中取数据，如果没有就走数据库
    - 对于变化频率较高的缓存，可以去除掉 使用 useCache="false"
    - 再特定情况下，可以配置刷新缓存的策略。因为缓存只是为查询服务的  flushCache="flase" 默认是ture
- Mapper代理模式
- 面试题
  - #{}和${}的区别
    - ${}直接拼接传递的数据，有SQL注入的风险
    - #{}是预编译处理
  - 实体类熟悉名和数据库字段名不一致，怎么解决
    - 开启驼峰映射
    - 在SQL语句起别名
    - 使用ResultMap处理映射
  - 如何获取主键
    - useGeneratedKeys="true"
  - 在Mapper中如何传递多个参数
    - 使用#{0}，#{1}，#{2} ....
    - 使用Map映射
    - 使用传递参数 @Param指定参数的名字
  - Mybatis的动态SQL是干什么的？有哪些动态SQL？原理是什么
    - 是用来拼接SQL的，其会根据传递的参数进行判断
    - trim | where | if | set | foreach | where | choose | otherwise | bind 
    - 在Mybatis的基础模块JDBC模块，有一个AbstractSQL 里面有所有的处理动态SQL的方法定义，然后配合OGNL从SQL中计算值，进行拼接SQL处理
  - Mybatis中，不同的xml文件，id是否可以重复？
    - 可以的，只有保证 namespace+id是唯一的就可以了，因为namespace+id可以定位绑定配置
  - 为什么说Mybatis是半自动的ORM框架
    - 因为类似于Hibernate这样的全自动框架来说，我们是不需要编写SQL的
    - 而对于Mybatis来说，我们可以编写SQL，也就是核心业务由我们手动编写，其他的处理结果集都已经帮我们做好了
  -  通常⼀个Xml映射⽂件，都会写⼀个Dao接⼝与之对应，请问，这个Dao接⼝的⼯作原理是什么？Dao接⼝⾥的⽅法，参数不同时，⽅法能重载吗？
    - 工作原理是动态代理，Mybatis运行的时候会生成动态代理对象。来执行拦截到的SQL，然后返回数据
    - 此方法不可以进行重载，因为xml文件的映射标识是 namespace+方法名为一个key来唯一定位 MappedStatement
  - Mybatis相对于ibatis的改进是什么
    - 动态SQL从原来的节点改为OGNL配置
    - 实现了Dao接口和XML的动态绑定
    - 在resultMap的一对一，一对多，增加了两个配置方法
  - 接口绑定有几种方式
    - 两种方式
    - 第一种使用注解 @Select、@Update、@Delete、@Delete
    - 第二种方式是使用XML配置文件的方式实现
  - Mybatis怎么实现分页的
    - RowBounds对象进行内存分页，这是逻辑分页
    - 还有的就是一个物理分页，把SQL的执行地点直接定位到指定地点执行
  - Mybatis插件运行原理
    - Mybatis仅可以编写针对ParameterHandler、ResultSetHandler、StatementHandler、Executor这4种接⼝的插件，Mybatis使⽤JDK的动态代理，为需要拦截的接⼝⽣成代理对象以实现接⼝⽅法拦截功能，每当执⾏这4种接⼝对象的⽅法时，就会进⼊拦截⽅法，具体就是InvocationHandler的invoke()⽅法，当然，只会拦截那些你指定需要拦截的⽅法。实现Mybatis的Interceptor接⼝并复写intercept()⽅法，然后在给插件编写注解，指定要拦截哪⼀个接⼝的哪些⽅法即可，记住，别忘了在配置⽂件中配置你编写的插件。
  - Mybatis是否⽀持延迟加载？如果⽀持，它的实现原理是什么？
    - Mybatis仅⽀持association关联对象和collection关联集合对象的延迟加载，association指的就是⼀对⼀，collection指的就是⼀对多查询。在Mybatis配置⽂件中，可以配置是否启⽤延迟加载lazyLoadingEnabled=true|false。它的原理是，使⽤CGLIB创建⽬标对象的代理对象，当调⽤⽬标⽅法时，进⼊拦截器⽅法，⽐如调⽤a.getB().getName()，拦截器invoke()⽅法发现a.getB()是null值，那么就会单独发送事先保存好的查询关联B对象的sql，把B查询上来，然后调⽤a.setB(b)，于是a的对象b属性就有值了，接着完成a.getB().getName()⽅法的调⽤。这就是延迟加载的基本原理。当然了，不光是Mybatis，⼏乎所有的包括Hibernate，⽀持延迟加载的原理都是⼀样的。
  - Mybatis都有哪些Executor执⾏器？它们之间的区别是什么？
    - Mybatis有三种基本的Executor执⾏器，SimpleExecutor、ReuseExecutor、BatchExecutor。
    - SimpleExecutor：每执⾏⼀次update或select，就开启⼀个Statement对象，⽤完⽴刻关闭
      Statement对象。
    - ReuseExecutor：执⾏update或select，以sql作为key查找Statement对象，存在就使⽤，不存在
      就创建，⽤完后，不关闭Statement对象，⽽是放置于Map<String, Statement>内，供下⼀次使
      ⽤。简⾔之，就是重复使⽤Statement对象。
    - BatchExecutor：执⾏update（没有select，JDBC批处理不⽀持select），将所有sql都添加到批处
      理中（addBatch()），等待统⼀执⾏（executeBatch()），它缓存了多个Statement对象，每个
      Statement对象都是addBatch()完毕后，等待逐⼀执⾏executeBatch()批处理。与JDBC批处理
      相同。
    - 作⽤范围：Executor的这些特点，都严格限制在SqlSession⽣命周期范围内
  - Mybatis和Hibernate的区别
    - Mybatis和hibernate不同，它不完全是⼀个ORM框架，因为MyBatis需要程序员⾃⼰编写Sql语句，不过mybatis可以通过XML或注解⽅式灵活配置要运⾏的sql语句，并将java对象和sql语句映射⽣最终执⾏的sql，最后将sql执⾏的结果再映射⽣成java对象。
    - Mybatis学习⻔槛低，简单易学，程序员直接编写原⽣态sql，可严格控制sql执⾏性能，灵活度⾼，⾮常适合对关系数据模型要求不⾼的软件开发，例如互联⽹软件、企业运营类软件等，因为这类软件需求变化频繁，⼀但需求变化要求成果输出迅速。但是灵活的前提是mybatis⽆法做到数据库⽆关性，如果需要实现⽀持多种数据库的软件则需要⾃定义多套sql映射⽂件，⼯作量⼤。
    - Hibernate对象/关系映射能⼒强，数据库⽆关性好，对于关系模型要求⾼的软件（例如需求固定的定制化软件）如果⽤hibernate开发可以节省很多代码，提⾼效率。但是Hibernate的缺点是学习⻔槛⾼，要精通⻔槛更⾼，⽽且怎么设计O/R映射，在性能和对象模型之间如何权衡，以及怎样⽤Hibernate需要具有很强的经验和能⼒才⾏。总之，按照⽤户的需求在有限的资源环境下只要能做出维护性、扩展性良好的软件架构都是好架构，所以框架只有适合才是最好

#### SpringBoot

- SpringBoot是对Spring的封装。原本我们使用Spring进行开发的时候需要进行很多xml配置和导入很多依赖，这样就导致很多繁杂的配置。而使用SpringBoot之后，它已经为我们做好了自动配置
- 它是简化Spring开发的一个框架，整个Spring技术栈的一个大整合，J2EE开发的一站式解决方案
- 开箱即用，内嵌式容器简化Web项目
- 没有冗余代码生成和XML配置
- **SpringBoot自动装配原理**
  - pom.xml
  - spring-boot-dependencies：核心依赖在父工程中
  - 在引入一些SpringBoot依赖的时候，不需要指定版本，就有这些仓库
  - **启动器：就是SpringBoot的启动场景**
  - 比如：spring-boot-starter-web 就会自动导入web环境所有的依赖
  - spring-boot会将所有的功能场景，都编写成一个一个的启动器
  - 我们需要什么功能，就只要找到对应的启动器就可以了

**主程序**

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBoot01Application {
    public static void main(String[] args) {
        SpringApplication.run(SpringBoot01Application.class, args);
    }
}
```

- `@SpringBootApplication` 标注这个类是一个SpringBoot的应用
- `SpringApplication.run(SpringBootApplication.class, args);` 启动SpringBoot应用

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
// 其实就是 @Configuration注解 标志就是一个配置类的注解
@SpringBootConfiguration
// 自动配置的核心
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {
}
```

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
public @interface SpringBootConfiguration {
}
```

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {
}
```

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {

    // 标记会自动注入以 spring.boot.enableautoconfiguration 开头的配置
	String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";
	Class<?>[] exclude() default {};
	String[] excludeName() default {};

}
```

```java
public final class SpringFactoriesLoader {

    // 从配置文件中找到自动配置
    // 位置在 META-INF/spring.factories
	public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";

}
```



-  结论：SpringBoot所有的自动配置都是在启动的时候扫描并加载，`spring.factories`所有的自动配置类都在这里面，但是不一定生效，要判断条件是否成立，只要导入了对应的start，就有对应的启动器了，有了启动器，自动装配就会生效，然后就配置成功了 

- 1、SpringBoot 在启动的时候，从类路径下`/META-INF/spring-factories`获取指定的值，

  2、然后将自动配置的类导入容器，自动配置就会生效，帮我们自动配置

  3、以前需要自动配置的时候，现在SpringBoot帮我们做了整合JavaEE，解决方案和自动配置的东西都在 `spring-boot-autoconfigure-2.3.0.RELEASE.jar`下

  4、他会把所有需要导入的组件，以类名的形式返回，这些组件就会被添加到容器

  5、容器中也有很多的 xxxAutoConfiguration的文件，就是这类给容器中导入了这个场景很多的组件，并自动配置 @Configuration

  6、有了自动配置类，就免去了我们自己手写编写配置文件的工作`	

- **run（）方法 SpringBoot的启动类 做了4个事情**

  - 推断应用的类型是普通的项目还是Web项目
  - 查找并加载所有的可能的初始化器，设置到 initializers属性中
  - 找出所有的应用程序监听器，设置到listeners属性中
  - 推断并设置main方法的定义类，找到运行的主类
  - @ConditionalOnXXX 在什么条件下生效

- **精髓**

  - SpringBoot启动会加载大量的自动配置类
  - 我们看我们需要的功能有没有在SpringBoot默认写好的自动配置类当中
  - 我们再看这个自动配置类到底配置了哪些组件 (只要组件在其中，我们就不需要配置了)
  - 给容器中自动配置类添加组件的时候，会从properties类中获取某些属性，我们只需要在配置文件中指定这些属性的值即可
  - xxxAutoConfiguration：自动配置类，给容器中添加组件
  - xxxProperties：封装配置文件中相关的属性

  ---

  - 自动配置的原理是一样的，SpringBoot在自动配置的时候，先看容器中有没有用户自己配置的，如果有用户自己配置的bean，那么就用用户配置的，如果没有，就用自动配置的。如果组件可以存在多个，就将用户配置和自己的默认组合起来 
  - `@EnableAutoConfiguration`自动配置的魔法骑士就变成了：从classpath中搜寻所有的`META-INF/spring.factories`配置文件，并将其中`org.springframework.boot.autoconfigure.EnableutoConfiguration`对应的配置项通过反射（Java Refletion）实例化为对应的标注了`@Configuration`的JavaConfig形式的IoC容器配置类，然后汇总为一个并加载到IoC容器。

#### SpringData Jpa & Mybatis Plus & 通用Mapper

- SpringData JPA是Spring团队开发一个ORM框架，用于对象持久化的API
- JPA是Hibernate的一个抽象
- JPA本质就是一个ORM规范
- MP和通用Mapper是对Mybatis的增强

#### Dubbo & Zookeeper

- 架构设计的本质
  - 降低成本、增加效益
  - 快速迭代、持续交付

- 分布式系统即使由一组通过网络通信、为了完成共同的任务而协调工作的计算机节点组成的系统。分布式系统的出现是为了用廉价的、普通的机器完成单个计算机无法完成的计算、存储任务。其目的是 **利用更多的机器、处理更多的数据**
- 分布式系统是若干个独立计算机的集合。其思想类似集群，如我们常见的Nginx+Tomcat集群。其实就是担心某一Tomcat不可用，但是整个应用不受影响。但是分布式应用并不仅仅只解决了这个问题。它还解决了开发上的相对便利，也就是说不同的服务之间如果更新，而其他的模块不需要重新打包部署。
- 首先明确的是，只有单个节点的处理能力无法满足日益增长的计算、存储任务的时候，且硬件的提升（加内存、加磁盘、使用更好的CPU）高昂到得不偿失的时候，应用程序也不能进一步优化的时候，才需要考虑到分布式系统。因为分布式系统解决的问题本身是和单系统一样的，而由于分布式系统的多节点，又会引出更多的机制与问题
- Dubbo是阿里出品的一套RPC框架，现在属于Apache的顶级项目
- RPC：进程之间通信
- **Dubbo**
  - Dubbo是一款高性能、轻量级的开源RPC框架，提供服务自动注册、自动发现等高效服务治理方案，可以和Spring无缝集成
- Zookeeper 注册中心
  - 作用用来服务注册与发现和进行负载均衡

#### Spring Cloud & Spring Cloud Alibaba

- Spring Cloud 是一整套微服务治理解决方案
  - Eureka：服务注册
  - Ribbon：负载均衡
  - Feign：负载均衡
  - Hystrix：服务熔断与降级
  - Zuul：路由网关
  - Config：配置中心
- Spring Cloud Alibaba 是阿里开源 ，是Alibaba结合自身的微服务实践开源的一套微服务全家桶 ，SpringCloud Alibaba是依赖SpringCloud相关的标准实现的一套微服务的全家桶 
  - Nacos (配置中心与服务注册与发现)
  - Sentinel (分布式流控)
  - RocketMQ (消息队列)
  - Seata  (分布式事物)
  - GateWay （路由网关）
  - **等等很多组件**

#### Spring Security & Shiro

- Spring Security是一个Spring家族中的权限认证框架，主要包含认证和授权两大安全模块。和Shiro相比，它具有更加强大的功能，Spring Security也可以轻松自定义扩展以满足各种要求，并且对场景的Web安全攻击提供了防护支持
- **认证：你是谁；授权：你能做什么?**
- Spring Security的核心就是filter，Spring Security包含了众多的过滤器。
- Shiro是Apache基金会的安全框架， 三个核心组件：Subject, SecurityManager 和 Realms. 
- Subject：当前操作用户。在Shiro中，Subject这一概念并不仅仅指的是人，也可以是第三方进程、后台账户。它仅仅意味着 “当前和软件交互的东西”
- SecurityManager：是Shiro的核心，典型的Facade模式，Shiro通过SecurityManager来管理内部组件实例，并通过它来提供安全管理的各种服务
- Realm：Realm充当了Shiro与应用数据安全的桥梁 
- 权限认证：用户有多个角色，角色有多个权限，权限有多个路径。还有就是页面对应的权限和界面

#### Redis

- **缓存的语义规定**
  - 做缓存，就要遵循缓存的语义规定：
  - 读：读缓存redis，没有，读mysql，并将mysql的值写入到redis。
  - 写：写mysql，成功后，更新或者失效掉缓存redis中的值。

- Redis是一个k-v结构的非关系型数据库，它是一个缓存中间件和数据库

- 为什么需要使用缓存，因为大部分的网站的操作都是读，而每次都去查询数据库如MySQL会十分消耗性能，每次读写数据库都会进行磁盘IO。数据库的本质：读和写

- 而缓存是基于内存的，速度会比直接查询更快

- NoSQL - Not only SQL 不仅仅是SQL。很多类型的信息如：用户的个人信息、社交网络、地理位置。这些数据的格式不需要一个固定的格式，不需要多余的操作就可以横向拓展

- NoSQL有很多特点、

  - 方便拓展 数据之间没有数据。
  - 大数据高性能（Redis一秒可以写8万次，读取11万次，NoSQL的缓存记录级，是一种细粒度的缓存）
  - 数据类型是多样的，不需要固定数据的数据库设计，随取随用
  - 传统的RDBMS和NoSQL
    - **传统的RDBMS**
    - 结构化组织数据
    - SQL
    - 数据和关系都存储在单独的表中
    - 操作语言：数据定义语言
    - 严格的一致性
    - 基础的事务
    - **NoSQL**
    - 不仅仅是数据库
    - 没有固定的查询语言
    - 键值对、列存储、文档存储、图形存储
    - 最终一致性
    - CAP原理和BASE（异地多活）
    - 高性能、高可用、高可拓

- - **3V 主要是用来描述问题的**
  - 海量 Velume
  - 多样 Variety
  - 实时 Velocity
  - **3高 主要是对程序的要求**
  - 高性能 （保证用户体验）
  - 高可用  - > 高并发
  - 高可拓 （随时可以对系统进行水平拆分或者垂直拆分）

- Redis是单线程的 - 基于IO多路复用模型 - 速度极快 而且CPU不是Redis性能的瓶颈 Redis的性能瓶颈是根据机器的内存和网络带宽决定

- 面试题：为什么Redis是单线程的还那么快？

  - 误区1：高性能的饿服务器一定是多线程的
  - 误区2：多线程（CPU会进行上下文切换） 一定比单线程效率高
  - Redis是把所有的数据都存储在内存中，所以使用单线程效率是最高的。多线程存在CPU的上下文切换，上下文切换的代价时非常昂贵的，对于内存系统来说，如果没有上下文切换性能是最高的。多次读写都是在一个CPU上的，在内存情况下，这个就是最佳方案

- 面试题：Redis的五大数据类型

  - 字符串 strings
  - 散列 hashes   hash可以存储一些变更的数据，尤其是用户信息的保存，和经常变动的信息，hash更加适合存储对象 ，而String更加适合字符串的存储 
  - 列表 list （底层是链表）
  - 集合 sets （内容是不可以重复的）
  - 有序集合 sorted sets （Zset）

- 三种特殊的数据类型

  - geospatial 地理位置 朋友的定位，附近的人，打车举例计算
  - hyperloglog 基数 （不重复的元素）
  - bitmaps 位存储  统计用户信息，活跃，不活跃！登录，未登录 

- Redis的事务

  - 要么同时成功、要么同时失败
  - redis的单条命令是保持原子性的，但是事务不保持原子性
  - redis事务的本质：一个事务中的所有的命令都会被序列化，在事务执行的过程中，会按照顺序执行，一次性、顺序性、排他性
  - redis没有隔离级别的概念
  - **开启事务 （multi）**
  - 命令入队
  - **执行职务（exec）**

- Redis 的配置文件

- 持久化规则

  - 持久化操作，在规定的时间之内，执行了多少次操作，就会持久化到文件 .rdb 和 .aof 文件

  - ```
    # 如果在60秒之内，如果如果至少有 10000 个key进行了修改，就会执行持久化操作
    save 60 10000
    ```

- **RDB**

  - 会存储所有的数据
  - Redis会单独创建（fork）一个子进程来进程持久化。首先，它会将数据写到一个临时文件中去，等待持久化过程结束了，在使用这个临时文件替换上次持久化好的文件，整个过程中，主进程是不执行任何IO操作的，这就保证了极高的性能。如果需要让进行大规模数据恢复，且对于数据库完整性不是特别敏感，那么RDB模式比AOF模式更加高效。RDB的缺点是最后一次持久化的数据可能丢失。默认的情况下就是RDB
  - 优点：适合大规模的数据恢复。对数据库的完整性要求不高
  - 缺点：需要一定的时间间隔操作，如果Redis意外挂机了，数据可能会丢失，最后一次的修改数据可能就没了
  - fork进程的时候，会占用一些内存空间

- **AOF**

  - 会存储所有的执行的命令，在恢复的时候，会把这个文件全部在执行一遍
  - 以日志的形式记录每个写操作，将Redis执行过的命令都记录下来（读操作不记录），只可以追加文件但是不可以修改文件。Redis启动之初，会读取这个文件重建数据。换言之，Redis重启的话，会根据日志文件的内容重新将指令执行一次以完成数据的恢复工作
  - Redis保存的是 appendonly.aof 文件  默认是不开启的
  - aof默认是无限制增加的，可以配置其重写 此时会fork一个新的进程来重写
  - 注意：如果aof被破坏，那就恢复不了了。此时可以进行aof文件恢复 
  - 优点：每次修改都会同步，文件的完整性较好。默认是每秒同步一次。从不同步效率最高
  - 缺点：相对于数据文件来说，aof远大于rdb，因为需要保存所有的操作，但是rdb只保存数据。修复的效率比rdb慢，aof运行的效率也比rdb慢

- Redis订阅发布

  - 消息队列
  - 实时聊天
  - 订阅、关注系统

- **Redis主从复制**

  - 就是将一台Redis服务器的数据，复制到其他的Redis服务器，前着为主节点，后面为从节点。数据的复制是单向的，只能由主节点到从节点。Master以写为主，而Slave以读为主
  - 主从复制的作用
    - 数据冗余：实现数据的热备份，持久化之外的一种冗余方式
    - 故障恢复：当主节点gg之后，可以选择从节点提供服务，实现快速的故障恢复，实际上是一种服务的冗余
    - 负载均衡：在主从复制的基础上，配合读写分离，由主节点提供写服务，从节点提供读服务。分担服务器负载，提高Redis服务器的并发
    - 高可用基础（集群）：主从模式是哨兵模式和集群能够实施的基础，主从复制是高可用的基础
    - 一般，项目中只使用一台Redis是不可以的
    - 从结构上，单个Redis服务容易发生单点故障，并发一台服务器需要处理所有的请求，压力较大
    - 从容量上，单个Redis服务器的内存容量有限，就算一台Redis服务器容量为256G，但是也不能把所有的内存都用来存储数据，一般来说，单个机器Redis最大的内存不应该超过20G
  - 最低配置  一主二从
    - 只需要配置从库，因为默认情况下，每个节点都是主节点，所以设置从节点认主节点即可
  - 主从复制的原理
    - 从节点启动成功连接到 master 后会发生一条 sync 同步指令
    - master接到指令，启动后台的存盘进程，同时收集到所有的修改数据集命令，在后台进程执行完毕之后，master 将 **整个数据文件** 传递到从节点，并完成 一次同步
      - 全量复制：从节点在接收到数据库文件之后，将其存盘加载在内存中
      - 增量复制：主节点继续将所有收集的修改命令一次传给从节点，实现同步
    - 但是只要重新连接主节点，一次完全同步将会被执行，我们的数据会在从节点中看到
    - 一主二从的模式 ：
      - A（主）
        - B（从）
        - C（从）
      - A（主）
        - B（从）
          - C（从）
      - 也就是说，第一种吗，从节点都挂在一个主机上，第二种，是从节点的从节点，都不会使用
  - 如果主机宕机 可以手动切换主机
  
- **哨兵模式**

  - 为了解决主节点宕机
  - 哨兵模式是一种特殊的模式。Redis提供一个独立的进程，作为进程会独立运行
  - **原理：哨兵线程通过发送命令，等待Redis服务器响应，从而监控多个Redis实例。如果发现主节点宕机，就选举从节点为主节点。从而保证主从可用**

- **Redis缓存穿透**

  - 用户想要查询一个数据，发现缓存中也没有，于是继续向持久层数据库查询，发现没有，于是本次查询失败，当用户很多的时候，缓存没有命中，都是请求数据库，这就会给持久层带来很大的压力
  - 解决方案：
    - **布隆过滤器**，对可能查询的参数以hash的形式存储。在控制层先进行校验，符合则通过，不符合就丢弃
    - **缓存空对象** 当存储层中没有命中的时候，即使返回的是空对象也将其缓存起来，同时会设置一个过期时间，之后再访问这个数据就会从缓存中获取，就保护了后端数据源
    - 但是缓存空对象会有新的问题：如果空值可以被缓存起来，那么就意外着存在更多的空间存储控制
    - 即使空指设置了过期时间，还是会存在缓存层的数据会有一段时间窗口不一致，这对于保证一致性的业务会有影响

- **Redis缓存击穿**

  - 缓存的数据量太大。然乎缓存同时过期，然后又大量请求，全部访问数据库查询数据
  - **解决方案**
    - 设置热点数据永不过期
    - 加互斥锁

- **Redis缓存雪崩**

  - 由于缓存层承载着大量请求，有效的保护了存储层，但是如果缓存层由于某些原型整体不能提供服务，于是所有的请求都会到达存储层，存储层的调用会暴增，造成存储层宕机
  - **解决方案**
    - 保证缓存层服务器高可用。即使个别节点、个别机器、甚至是机房宕机，依然可以提供服务，如Redis Sentinel 和 Redis Cluster 都实现了高可用
    - 依赖隔离组件为后端限流并降级：缓存失效之后，通过加锁或者队列来空指读数据库中写缓存的线程数量，比如一个key只允许一个线程查询数据和写缓存，其他线程等待
    - 数据预热：可以通过缓存reload机制，预先更新缓存，再即将发生大并发访问之前缓存不同的key，设置不同的过期时间，让缓存失效的时间点尽量均匀

- **Redis缓存并发**

  - 缓存并发指的是高并发场景下大量查询过期的key、最后查询数据库将缓存结果写回到缓存、造成数据库压力过大

- **分布式锁**

  - 在缓存更新或者过期的情况下，先获取锁，在进行更新或者从数据库中获取数据之后，再释放锁，需要一定的时间等待，就可以从缓存中继续获取数据

- Redis用作分布式锁的应用

- Redis配合RabbitMQ实现削峰和限流降级

- Redis集群实现高可用

- **布隆过滤器**

  - 是bit数组，由0和1组成。如果需要多个不同的hash函数生成多个hash只，并对hash值指定的bit设置为1，但是存在覆盖的风险，所以并不是完全准确的，但是有前人经过测试：0.81%的错误率，还是可以接受的

- **Redis使用场景**

  | 类型   | 适用场景                                    |
  | ------ | ------------------------------------------- |
  | String | 缓存，限流，计数器，分布式锁，分布式session |
  | Hash   | 存储用户信息，用户主页访问量，组合查询      |
  | List   | 微博关注人时间轴列表，简单队列              |
  | Set    | 赞，踩，标签，好友关系                      |
  | Zset   | 排行榜                                      |

  - 或者简单消息队列，发布订阅实施消息系统等等

- **Redis和数据库同步**

  - 如果仅仅是读数据，没有这个问题
  - 如果是新增数据，也没有这个问题
  - 当数据需要更新的时候，如何保证缓存和数据库的双写一致性
  - **三种更新策略**
    - 先更新数据库，再更新缓存
    - 先删除缓存，再更新数据库
    - 先更新数据库，再删除缓存
  - 方案1：并发的时候，执行顺序无法保证，可能A先更新数据库，但是B后更新数据库但是先更新缓存。加锁的话，可以避免，但是这样会导致吞吐量下降，可以根据业务场景考虑
  - 方案2：该方案会导致不一致的原因是。同时有一个请求A进行更新操作。另外一个B进行查询操作，那么会出现以下场景：
    - （1）请求A进行写操作，删除缓存
    - （2）请求B查询发现缓存不存在
    - （3）请求B去数据库查询得到旧值
    - （4）请求B将旧值写入缓存
    - （5）请求A将新的值写入数据库。 
    - **延时双删策略** 。因此采用 延时双删测控，即进入逻辑旧删除key，执行完操作，延时再删除key
  - 方案3：更新数据库 - 删除缓存 - 可能出现的问题场景 
    - （1）缓存刚刚好失效
    - （2）请求A查询数据库，得到一个旧值
    - （3）请求B将新值写入数据库
    - （4）请求B删除缓存
    - （5）请求A将查到的旧值写入缓存
    - 先天条件要求：请求第二步的读取操作耗时要大于更新操作，条件比较苛刻
    - 但是他如果真的发生怎么办？
      - A：给键设置合理的过期时间
      - B：异步延时删除key

- **如何保证Redis数据库都是热点数据？**

  - A：可以通过手工的方式，加载热点数据
  - B：可以使用Redis自带的数据淘汰策略
  - Redis内存数据大小上升到一定大小的时候，就会实施数据淘汰策略（回收策略）
  - Redis提供的6大数据淘策略
    - volatile-lru：从已设置过期时间的数据集（server.db[i].expires）中挑选最近最少使用的数据进行淘汰
    - volatile-ttl：从已设置过期时间的数据集（server.db[i].expires）中挑选即将要过期的数据进行淘汰
    - volatile-random：从已设置过期时间的数据集（server.db[i].expires）中选择任意数据进行淘汰
    - allkeys-lru：从数据集中选择（server.db[i].dict）中挑选最近最少使用的数据淘汰
    - allkeys-random：从数据集中选择（server.db[i].dict）中挑选任意数据淘汰
    - no-enviction（驱逐）：禁止驱逐数据
  
- Redis单线程使用异步多路复用IO

  - NIO 是利用了单线程轮询事件的机制，通过高效地定位就绪的 Channel，来决定做什么，仅仅 select 阶段是阻塞的，可以有效避免大量客户端连接时，频繁线程切换带来的问题，应用的扩展能力有了非常大的提高。 

#### MongoDB

- 它是一个基于分布式存储的NoSQL数据库
- 它提出的文档、集合的概念。使用BSON（类似JSON）作为其存储模型结构，其结构是面向对象而不是二维 表，使用着用的数据模型，使得ta能够在生产环境中提供高读写能力，吞吐量较MySQL等SQL数据库有很大的提升
- 容易伸缩，自动故障转移。易伸缩指的是提供了分片能力，能对数据进行分片。数据的存储压力分摊给多台服务器。自动故障转移是副本集的概念
- 因为数据模型是面向对象的，所以其可以表示丰富的、有层级的数据结构，比如博客系统中可以把评论直接放在文章的文档中，而不必像MySQL一样使用多张表来进行存储
- 它主要是用来存储文档，如文件的数据、评论、文章等等
- 保存了关系型数据库的即时查询能力，底层使用的是B Tree。而Redis则没有这个优点
- 它提供副本集使得数据可以分布在不同的机器上，目的是可以提供故障转移
- 速度，它实现了一个驱动语义，当通过驱动调用写入的时候，可以立即返回得到成功的结果（即使是报错），这样让写入的速度更加的快，当然有一定的不安全性，完全依赖网络
- 持久性，它提供了类似MySQL的bin-log日志，当需要插入的时候，先向日志中写入记录，在执行插入操作，这样如果停电，进程突然中断的情况下，可以保障数据不会错误，可以使用修复功能读取日志进行修复
- 数据拓展，它使用分片技术对数据进行拓展，它支持数据分片、自动转移分片里面的数据块、让每一个服务器中存储的数据大小一致
- 概念：数据库、集合、文档
- 因为mongodb是没有模式结构的，所以可以使用mongoose为文档创建一个模式结构，也就是说定义文档的数据对象 - 其包含Schema（模式对象） - Model 相当于它的Collection - Document 表示集合中的具体的文档 

#### ElasticSearch

- **功能**
  - 分布式的搜索引擎和数据分析引擎 
  - 全文检索，结构化检索，数据分析 
  - 对海量的数据进行接近实时的处理
- **特点**
  - 可以作为一个大型分布式集群（数百台服务器）技术，处理PB级别的数据，服务大型公司，也可以运行在单机上，服务小公司
  - ES是将全文检索、数据分析、以及分布式技术整合在了一起，
  - 但是功能不是很完善

- 分布式搜索引擎，ElasticSearch是基于 **Lucene** 基础之上的搜索引擎
- 同类产品有 Lucene 、Solr 这两个都是Apache开源的
- ES是开源的高拓展的分布式全文检索的搜索引擎，它可以近乎实时的存储、检索数据、本身扩展性很好。可以扩展到上百台服务器，通过简单的Restful API 来隐藏Lucene 作为核心
- ElasticSearch一般配合 Logstash 、Kibana使用  其中 Kibana是开源的分析和可视化平台
- Logstash  是一个开源的服务器端数据处理管道，可以同时从多个数据源获取数据，并对其进行转换，然后将其发送到你最喜欢的“存储” 
- ElasticSearch和Solr的总结
  - ES开箱即用，非常简单，Solr安装比较复杂
  - Solr利用Zookeeper进行分部署管理，而ES自带分布式协调功能
  - Solr提供更多的数据形式。如：json、xml、csv、而ES只能处理json文件
  - Solr官方提供的功能更多，而ES本身更加注重核心功能，高级功能由第三方提供
  - Solr查询快，但是更新索引很慢（也就是插入删除很慢）
  - ES进里索引快，Solr是传统搜索引擎的解决方案。ES是新兴的实时搜索引擎应用
  - Solr有成熟的用户、开发和开源社区。但是ES的开发和维护者比较少，更新的也比较快。学习成本比较高
- ES是面向文档的

#### RabbitMQ

- JMS - Java Message Server
- JMS是一种与厂商无关的API 用来访问消息。收发系统消息，类似JDBC
- 核心应用场景
  - 解耦合：订单系统 -> 物流系统
  - 异步任务：用户注册 -> 发送邮件，初始化信息
  - 削峰：秒杀、日志处理
  - 解耦，生产端和消费端不需要相互依赖
  - 异步，生产端不需要等待消费端响应，直接返回，提高了响应时间和吞吐量
  - 削峰，打平高峰期的流量，消费端可以以自己的速度处理，同时也无需在高峰期增加太多资源，提高资源利用率
  - 提高消费端性能。消费端可以利用buffer等机制，做批量处理，提高效率。
  - **小明和快递员和X巢的例子**
- **消息队列选择问题：Apache ActiveMQ、Kafka、RabbitMQ、RocketMQ**
  - ActiveMQ：http://activemq.apache.org/
    - Apache出品，历史悠久，支持多种语言的客户端和协议，支持多种语言Java, .NET, C++ 等，基于JMS Provider的实现
    - 缺点：吞吐量不高，多队列的时候性能下降，存在消息丢失的情况，比较少大规模使用
  - Kafka：http://kafka.apache.org/
    - 是由Apache软件基金会开发的一个开源流处理平台，由Scala和Java编写。Kafka是一种高吞吐量的分布式发布订阅消息系统，它可以处理大规模的网站中的所有动作流数据(网页浏览，搜索和其他用户的行动)，副本集机制，实现数据冗余，保障数据尽量不丢失；支持多个生产者和消费者
    - 缺点：不支持批量和广播消息，运维难度大，文档比较少, 需要掌握Scala，二次开发难度大
  - RabbitMQ：http://www.rabbitmq.com/
    - 是一个开源的AMQP实现，服务器端用Erlang语言编写，支持多种客户端，如：Python、Ruby、.NET、Java、JMS、C、用于在分布式系统中存储转发消息，在易用性、扩展性、高可用性等方面表现不错
    - 缺点：使用Erlang开发，阅读和修改源码难度大
  - RocketMQ：http://rocketmq.apache.org/
    - 阿里开源的一款的消息中间件, 纯Java开发，具有高吞吐量、高可用性、适合大规模分布式系统应用的特点, 性能强劲(零拷贝技术)，支持海量堆积, 支持指定次数和时间间隔的失败消息重发,支持consumer端tag过滤、延迟消息等，在阿里内部进行大规模使用，适合在电商，互联网金融等领域使用
    - 缺点：部分实现不是按照标准JMS规范，有些系统要迁移或者引入队列需要修改代码

#### 密码学

- 简介：主要是用来编制密码和破译密码的学科
- 目的：研究如何隐藏信息并把信息传递出去的一个学科
- 应用在网络安全、信息安全、区块链等学科的基础
- 密码学分为三个阶段
  - 古典密码学 - 类似古代史
  - 近代密码学 - 类似近代史
  - 现代密码学 - 类似现代史
- 古典密码学加密的方式：替换法、移位法
  - 替换法：简单的单表替换加密 复杂的多表替换加密
  - 移位法：凯撒加密
  - 破解方式：暴力破解+概率统计破解 = 频度分析法（原理：假设在英文文章中，字母出现的顺序从高向低进行排序 s a o p ... 然后拿到密文对密文数据进行字母出现的次数进行排序。然后进行替换）
- 近代密码学
  - 替换法、移位法
  - 恩尼格玛密码机
    - 被人工之父图灵破解
- 现代密码学
  - 散列函数
    - MD5
    - SHA-1
    - SHA-25
    - SHA512
    - 散列函数加密的话一般是不可逆的。如果想要破解的话，可以使用hash碰撞的方式进行
  - 对称加密
    - 核心原理：使用的加密方式和解密方式是同一把密钥
    - 流加密、块加密：流加密如：123456 对1，2，3，4，5，6 分别加密然后发送 ，块加密如：123，456 加密，然后拼接成密文
    - DES加密解密
    - 特点：
      - 加密速度块，可以加密大文件
      - 密文不可逆，一旦密钥文件泄漏，就会导致数据泄漏
      - 加密后编码表找不到相应的字符，出现乱码
      - 一般结合Base64使用
    - 密钥key 必须是8个字节
    - Base64 不是加密算法，而是可读性算法，因为对称加密之后会出现乱码，所以通过Base64进行解读
  - AES加密解密
    - 密钥必须是16个字节
  - 加密模式
    - ECB
    - CBC
  - 填充模式
  - 消息摘要
    - 又称为数字摘要
    - 它是唯一一个对应一个消息或者文本的固定长度的值，由一个单向hash加密函数
  - 非对称加密
    - 公钥
    - 私钥
    - 其中SSL / RSA算法
    - 特点
      - 使用公钥加密，必须使用私钥解密
      - 使用私钥加密，必须使用公钥解密

#### 计算机网络 

-  https://www.jianshu.com/p/4ba0d706ee7c 
- 时延
  - 发送时延
  - 传播时延
- 计算机网路的分类
  - 局域网（LAN）
  - 城域网（MAN）
  - 广域网（WAN）
- 信道复用
  - 时分复用
  - 频分复用
  - 统计时分复用
- 有了 IP 地址，为什么还要用 MAC 地址？
  - 历史原因：以太网诞生于因特网之前，在IP地址之前MAC地址就已经在使用了。两者结合使用是为了不影响已经存在的协议
  - 分层实现：对网络协议进行分层之后，数据链路层不需要考虑数据之间的转发，网络层实现不需要考虑数据链路层的影响
  - 分工合作：ip地址会跟随主机接入网络的不同而发生改变的，而MAC一般不一会改变，这样的话，我们可以使用IP地址进行寻址，当数据报和目的主机处于同一网络的时候，就使用MAC地址进行交互
- 子网掩码和IP地址
- 拥塞控制
  - 慢启动
  - 拥塞避免
-  快重传
   - 快重传要求接收方在收到一个失序的报文段后就立即发出重复确认（为的是使发送方及早知道有报文段没有到达对方，可提高网络吞吐量约20%）而不要等到自己发送数据时捎带确认。快重传算法规定，发送方只要一连收到三个重复确认就应当立即重传对方尚未收到的报文段，而不必继续等待设置的重传计时器时间到期。 
-  快恢复
   -  快重传配合使用的还有快恢复算法 
-  拥塞避免、快重传、快恢复 https://zhuanlan.zhihu.com/p/37379780 
- Https和Http的区别
- UDP和TCP的区别
  - 1、TCP面向连接（如打电话要先拨号建立连接）;UDP是无连接的，即发送数据之前不需要建立连接
  - 2、TCP提供可靠的服务。也就是说，通过TCP连接传送的数据，无差错，不丢失，不重复，且按序到达;UDP尽最大努力交付，即不保证可靠交付
  - 3、TCP面向字节流，实际上是TCP把数据看成一连串无结构的字节流;UDP是面向报文的 UDP没有拥塞控制，因此网络出现拥塞不会使源主机的发送速率降低（对实时应用很有用，如IP电话，实时视频会议等）
  - 4、每一条TCP连接只能是点到点的;UDP支持一对一，一对多，多对一和多对多的交互通信
  - 5、TCP首部开销20字节;UDP的首部开销小，只有8个字节
  - 6、TCP的逻辑通信信道是全双工的可靠信道，UDP则是不可靠信道 
- get和post的区别、Cookie和Session的区别
- TCP滑动窗口机制
- TCP长连接心跳机制
- 常用Http方法
- 滑动窗口机制下的粘包问题

#### 计算机组成原理

- **略**

#### 操作系统

- 进程和线程
- 进程的互斥和同步
  - 并发和并行
  - 进程之间有什么关系 （竞争和协作）
  - 为什么线程互斥也是一种同步
  - 解释死锁和饥饿，并说明两者关系
    - 死锁是多个线程竞争共享资源而进入永久等待状态
    - 而饥饿是一个可运行的线程，始终有其他的线程优先于它，而线程调度无限期延期不让其指向
    - 死锁和饥饿都会导致因为进程之间的竞争而无法执行。前着因为...后者因为...
  - 什么是临界区？如何解决进程对临界资源的访问冲突
    - 并发进程中和共享变量有关的程序段称为临界区，共享变量所代表的资源称为临界资源
    - 遵循临界区调度的三个原则
      - 一次至多只有一个进程进入临界区执行
      - 如果已经有进程进入临界区执行，那么其他的进程等待
      - 进入临界区内的进程应在有限的时间内退出，让等待队列中的一个进程进入
    - 总结：互斥使用，有空让进，忙则等待，优先等待，则一而入，算法可行
  - 信号量的物理意义是什么
    - 信号量表示物理资源的实体，是一个与队列有关的整形变量。主要用于封锁临界区、进程同步即维护资源计数
- 同步算法
  - 哲学家就餐问题
  - 读写者问题
  - 生产者消费者问题
- 死锁
  - 什么是死锁，死锁是多个线程竞争资源而造成的一种僵局，如果没有外力作用，这些进行都无法向前推进。
  - 死锁产生的原因 系统资源的竞争导致系统资源不足以及分配不当，导致死锁
  - 进行运行推进的顺序不合适导致死锁、
  - 死锁的四个必要条件
    - 互斥条件：一个资源只能被一个进行使用，再一段时间之内，某资源被一个进程占用，此时如果其他进行请求该资源，则请求进行只能等待
    - 请求与保持条件：进程已经保持了至少一个资源，但是又提出了新的资源请求，而该资源已经被其他进程占有，此时请求线程阻塞，但是对自己的持有的资源不释放
    - 不可剥夺条件：进行所占有的资源在为使用完毕之前，不能被其他的进程夺走，只能是持有资源的线程主动释放
    - 循环等待条件：若干进程之间形成首尾相连结相互等待的关系
    - **这四个条件是产生死锁的必要条件，只要有一个不满足，死锁就不成立**
  - 死锁的避免：死锁避免的基本思想，系统对进程发出的每一个指令进行检查，检查是否偶有足够的资源进行分配，如果有足够的资源进行分配，那就给分配，但是如果分配之后可能导致死锁，就不进行分配
  - 安全状态：如果操作系统能够保证所有的进行在有有限的时间之内得到所有的需要的资源，系统就处于安全状态，否则就不是安装状态 安装状态是指，操作能够把进程执行的顺序进行排列和分配资源，得到一个可以满足所有进程在有限时间之内完成。这样的一个进程执行的序列，就是安全的序列
  - 死锁的预防：我们可以通过破坏死锁的4个必要条件来预防死锁，但是互斥条件不能被破坏，所以只能破坏请求与保持条件（在其等待期间，它所占有的资源将被隐式的释放重新加入到系统的资源列表中，可以被其他进程使用，当等待的线程重新获取自己释放的资源和需要获取的资源之后，才能被执行）、不可剥夺条件（第一种方法在进程开始执行之前就静态分配好进程所需要的资源，第二个技术动态分配资源）、循环等待条件（使用资源的分配，找出一个安全状态，让其去执行进程即可）
  - 进程的任务调度算法
    - 先来先服务：哪个进程先来，就先执行哪个进程。不会考虑等待时间和执行之间，会产生饥饿现象，属于非抢占式作业调度算法。优点是简单、实现简单，缺点是不利于短作业执行
    - 短作业优先：会去寻找执行时间最短的去执行，但是会导致饥饿现象。优点是平均等待时间少
    - 高响应比优先：根据  **响应比 =（进程执行时间+进程等待时间）/ 进程执行时间** 这个算法完成任务的响应计算，其在等待时间相同的情况下，进程执行时间越快，响应比就越高
    - 时间片轮转：分时系统，给每个进程固定的执行时间，根据进程先后达到的顺序让进程在单位时间片执行，执行完之后进行下一个进程的执行
    - 多级反馈队列：将时间片轮转与优先级调度结合。把进程按照优先级分层不同的队列，先按照优先级调度，优先级相同的按照时间片轮转进调度。优点事兼顾长短作业，有较号的响应时间，适用于各种作业环境
    - 优先级调度：在等待队列中选择优先级最高的来执行
- 线程之间的通信
  - 管道
  - FIFO
  - 消息队列
  - 信号量
  - 共享内存
  - UNXI域套接字
  - 套接字（Socket）
- 文件系统
  - 主要是做什么：管理操作系统的文件
  - 常见的几种内存管理机制
    - 块式管理：把主存分为一大块一大块的，当所需的程序片段不再主存的时候，就分配一块主存空间，把程序片段load入主存，就算所需的程序片段只有几个字节也必须把这一大块分配给它。这样会造成很大的浪费。
    - 页式管理：把主存分为一页一页的。每一页的空间比一块一块的空间要小很多，这种方法的空间利用率比块式高很多
    - 段式管理：把主存分为一段一段的，每一段的空间比一页一页的空间小很多，这种方法在空间利用率上比页式管理高很多，但是也有另外一个缺点。一个程序片段可能被分为几十段，这样很多时间就会被浪费在空间寻址上
    - 段页式管理：结合段式管理和页式管理的优点，把主存分为若干页，每一页又分为若干段
  - 快表和多级页表
    - 页表：页面尺寸为4k，而地址是32位的。有2^20个页面，32位 总空间 [0~4G] 大部分
    - 多级页表：类似书的章节目录 逻辑地址只需要保存页目录号-页号-页偏移即可
    - 快表：多级页表提高了空间效率，但是时间上还是差点意思。因为多级页表增加了访问和存储的次数，尤其是64位操作系统。快表基于寄存器是 一组相连快速存储
  - 分页机制和分段机制的共同点和区别
    - 都是把主存分为更小的内存去使用，提高空间利用率
    -  https://blog.csdn.net/zhongyangtony/article/details/80879425 
  - 逻辑地址和物理地址
    - 逻辑地址属于相对地址，其作用是寻找直接地址的存放的地点。
    - 物理地址直接指向存储器中的数据
  - CPU寻址了解吗？为什么需要虚拟寻址空间
    - CPU寻址： https://www.cnblogs.com/Joezzz/p/9796685.html 
    - CPU寻址： 现代处理器使用的是一种称为**虚拟寻址**的寻址方式。使用虚拟寻址，CPU需要将虚拟地址翻译成物理地址，这样才能访问到真实的物理内存。实际上完成虚拟地址转换为物理地址转换的硬件是CPU中含有一个被称为**内存管理单元**的硬件。 
    - 为什么需要虚拟寻址空间：先从没有虚拟地址空间说起吧，没有虚拟地址空间的时候，程序都是直接访问和操作物理内存。但会有一些问题：
      - 用户可以访问任意的内存，寻址内存的每个字节，这样容易破坏操作系统，造成操作系统崩溃。
      -  想要同时运行多个程序特别困难。
      -  总结来说：**如果直接把物理地址暴露出来会带来严重的问题，比如可能对操作系统造成伤害以及给同时运行多个程序造成困难。** 
    - 通过虚拟地址访问内存有一下优势 
      - 程序可以使用一系列相邻的虚拟地址来访问物理内存中不相邻的大内存缓冲区。
      - 程序可以使用一系列虚拟地址来访问大于可用物理内存的内存缓冲区。当物理内存的供应量变小时，内存管理器会将物理内存页保存到磁盘文件。
      - 不同进程使用的虚拟地址彼此隔离。一个进程中的代码无法更改由另一进程使用的物理内存。
- 虚拟内存
  - 什么是虚拟内存
    - 它使得应用程序认为它拥有连续的可用的内存（一个连续的地址空间）实际上，它是被分隔成得多个物理内存碎片。还有部分临时存储在外部磁盘存储器上
  - 局部性原理了解吗？
    -  局部性原理是指CPU访问[存储器](https://baike.baidu.com/item/存储器/1583185)时，无论是存取指令还是存取数据，所访问的[存储单元](https://baike.baidu.com/item/存储单元/8727749)都趋于聚集在一个较小的连续区域中。 
    - 时间局部性
    - 空间局部性
    - 顺序局部性
    - **局部性的简单原则**
      - 重复引用同一个变量的程序有良好的时间局部性。
      - 做顺序访问的程序具有良好的空间局部性。
      - 循环结构具有良好的空间局部性和时间局部性。循环体越小，循环迭代次数越多，局部性越好。
  - 虚拟存储器知道吗？
  - 虚拟内存技术的实现知道吗？
    - 请求分页管理
    - 请求分段存储管理
    - 请求段页式管理
  - 缺页中断
    -  缺页中断就是要访问的页不在主存，需要操作系统将其调入主存后再进行访问。在这个时候，被内存[映射](https://baike.baidu.com/item/映射)的文件实际上成了一个分页交换文件。 
  - 缺页置换算法就是处理缺页中断的算法
  - 页面置换算法知道吗？ https://www.jianshu.com/p/18285ecffbfb 
    - OPT页面置换算法
    - FIFO页面置换算法
    - LRU最久未使用算法
    - LFU最少使用页面算法

#### Linux

**基本命令**

- top 用来监听用户线程的的占用和内存消耗
- cd
- ls
- ll
- shutdown
- reboot
- pwd
- cp 
- rm -rf 
- move 
- chown
- chmod
- 硬链接
  - 硬链接允许一个文件拥有多个有效的访问路径，可以有效防止误删
- 软连接
  - 软连接相当于windows平台的快捷方式
- vi / vim 编辑器
  - i
  - esc
  - q!
  - wq
  - wq!
  - /xxx 查找xxx
- 账户管理
  - useradd
  - userdel
  - usermod 
  - su username

#### 数据结构和算法

- 略

#### DevOps

- Jenkins  自动化部署
- Gitlab  类似于Github、码云的代码托管软件
- Git 分布式分支管理系统
- Maven java项目的一种组成方式
- Nexus 私服，用来放内部的jar等信息
  - 寻找jar的流程，当有一个仓库地址的时候，首先先去本地仓库去找、如果没有找到就去私服上去找，还没有。就再去中央仓库去找。
- MySQL 关系型数据库
- Sonarqube  代码审查工具

#### Nginx

- Nginx是一个高性能的由C编写的[HTTP] [反向代理]服务器

- 功能
  - 反向代理搭建负载均衡集群，如：Nginx+Tomcat集群
  
  - 负载均衡策略
  
    - 节点轮询（默认）
    - weight 权重配置（数字越大分配得到的流量就越大）适用于服务器性能差异很大的情况
    - ip_hash （固定分发） 根据请求按访问ip的hash结果分配，这样每个用户就可以固定访问一个后端服务器 
    -  upstream还可以为每个节点设置状态值  如果为down 就暂时不参与负载均衡 为backup的时候，其他全down 才会去访问此节点
  
  - 节点高可用
  
    - 如果某个应用挂了，不应该在继续发送出去
    - max_fails 允许请求失败的次数 默认为1，超过最大次数就不会再请求
    - fail_timeout 在 max_fails之后，暂停访问此节点的时间 默认 fail_timeout 为10S
  
  - Upstream配置、后端节点高可用探测、全局异常兜底数据测试
  
  - 搭建静态文件服务器
  
  - 静态文件压缩（时间换空间 - 服务端需要牺牲时间进行压缩以减少响应数据的大小）
  
  - 挖掘accessLog日志
    - 统计站点访问ip来源，某个时间段得访问频率
    - 查看访问最频繁得页面、HTTP响应状态码、接口访问的时间
    - 接口秒级的访问量、分钟访问量、小时和天访问量
    
    ```nginx
    122.70.148.18 - - [04/Aug/2020:14:46:48 +0800] "GET /user/api/v1/product/order/query_state?product_id=1&token=xdclasseyJhbGciOJE HTTP/1.1" 200 48 "https://xdclass.net/" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36"
    ```
    
    ```shell
    $remote_addr 对应的是真实日志里的122.70.148.18，即客户端的IP。
    
    $remote_user 对应的是第二个中杠“-”，没有远程用户，所以用“-”填充。
    
    ［$time_local］对应的是[04/Aug/2020:14:46:48 +0800]。
    
    "$request"对应的是"GET /user/api/v1/product/order/query_state?product_id=1&token=xdclasseyJhbGciOJE HTTP/1.1"。
    
    $status对应的是200状态码，200表示正常访问。
    
    $body_bytes_sent对应的是48字节，即响应body的大小。
    
    "$http_referer" 对应的是”https://xdclass.net/“，若是直接打开域名浏览的时，referer就会没有值，为"-"。
    
    "$http_user_agent" 对应的是”Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:56.0) Gecko/20100101 Firefox/56.0”。
    
    "$http_x_forwarded_for" 对应的是”-“或者空。
    ```
    
    - Nginx统计站点访问量、高频url统计
  
  - 封禁ip
  - 跨域
  - nginx 的location规则 - 支持正则表达式
  - nginx地址重定向 rewrite 规则
  - 缓存
    - 数据库缓存
    - 应用程序缓存
    - **Nginx网关缓存**
    - 前端缓存
  
- HTTPS流程

  - 密钥交换使用非对称加密、内存传输使用对称加密的方式

- Nginx整合 Openresty 实现内网访问显示、文件资源下载限速

- Nginx高可用解决LVS+KeepAlived多节点

- 配置文件解读

```nginx
# 指定进程运行以及用户组
user  root;
worker_processes  1;

# 错误日志的存放路径
#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

# nginx的进程id 存放路径
#pid        logs/nginx.pid;

# 指定IO模型
events {
    # 定义每个Nginx的最大连接数
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;
	# 自定义服务日志
    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for" $request_time';

    # 自定义服务日志的地址
    access_log  logs/icanci_access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    # 配置缓存
    proxy_cache_path /root/cache levels=1:2 keys_zone=xd_cache:10m max_size=1g inactive=60m use_temp_path=off;

    # 负载均衡的策咯配置
    upstream lbs {
        # 使用hash分配
        ip_hash;
        # 从节点 最大失败次数2次，最大超时时间60秒
        server 39.101.135.225:8380 max_fails=2 fail_timeout=60s;
        server 39.101.135.225:8381 max_fails=2 fail_timeout=60s;
        server 39.101.135.225:8382 max_fails=2 fail_timeout=60s;
    }

    # 客户端保持活动连接的超时时间，超过这个时间，服务器会关闭连接
    keepalive_timeout  65;

    # 此配置可以包含其他的配置文件
    #include /usr/local/nginx/conf/conf.d/*.conf;

    #开启gzip,减少我们发送的数据量
    gzip on;
    gzip_min_length 1k;

    #4个单位为16k的内存作为压缩结果流缓存
    gzip_buffers 4 16k;

    #gzip压缩比，可在1~9中设置，1压缩比最小，速度最快，9压缩比最大，速度最慢，消耗CPU
    gzip_comp_level 4;

    #压缩的类型
    gzip_types application/javascript text/plain text/css application/json application/xml    text/javascript; 

    #给代理服务器用的，有的浏览器支持压缩，有的不支持，所以避免浪费不支持的也压缩，所以根据客户端的HTTP头来判断，是否需要压缩
    gzip_vary on;

    #禁用IE6以下的gzip压缩，IE某些版本对gzip的压缩支持很不好
    gzip_disable "MSIE [1-6].";

    # 主服务Server配置
    server {
        # 监听80端口
        listen       80;
        #server_name  *.icanci.cn;
        # 需要监听的服务路径
        server_name  icanci.cn www.icanci.cn tomcat.icanci.cn jenkins.icanci.cn sonar.icanci.cn laoxu.icanci.cn test.icanci.cn api.icanci.cn ladybird.icanci.cn blog.icanci.cn iclass.icanci.cn jmeter.icanci.cn oos.icanci.cn  ;

        # 将Http地址转为Https
	    return 301 https://$host$request_uri;
        #charset koi8-r;
       
        # 日志地址
        access_log  logs/icanci_host.access.log  main;
		
        # 默认访问路径
        location / {
            root   /usr/local/nginx/icanci;
            index  index.html index.htm;
            proxy_cache xd_cache;
            proxy_cache_valid 200 304 10m;
            proxy_cache_valid 404 1m; 
            proxy_cache_key $host$uri$is_args$args;
            add_header Nginx-Cache "$upstream_cache_status";
        }

        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        # 错误页面
        error_page  404 500 502 503 504  =200  /default_api;
        location = /default_api {
            default_type application/json;
            return 200 '{"code":"-1","msg":"invoke fail, not found "}';
        }
    }

    # ladybird.icanci.cn
    server {
        # 监听443端口
        listen       443 ssl;
        # 请求的域名为 ladybird.icanci.cn
        server_name  ladybird.icanci.cn;

        # 配置SSL证书
        ssl_certificate      /usr/local/nginx/keys/ladybird/1_ladybird.icanci.cn_bundle.crt;
        ssl_certificate_key  /usr/local/nginx/keys/ladybird/2_ladybird.icanci.cn.key;

        # 缓存1分钟
        ssl_session_cache    shared:SSL:1m;
        # 超时5分钟
        ssl_session_timeout  5m;
	
	
        ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
        ssl_prefer_server_ciphers  on;
	ssl_protocols TLSv1 TLSv1.1 TLSv1.2;

        location / {
            # 集群节点
            proxy_pass http://lbs;
            # 存放用户的真实ip
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;  
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;  
            
            proxy_next_upstream error timeout http_503 non_idempotent;

            #开启错误拦截配置,一定要开启
            proxy_intercept_errors on;
        }
    }
    
    # 静态文件服务器地址
    # oos.icanci.cn
    server {
        listen       443 ssl;
        server_name  oos.icanci.cn;

        ssl_certificate      /usr/local/nginx/keys/oos/1_oos.icanci.cn_bundle.crt;
        ssl_certificate_key  /usr/local/nginx/keys/oos/2_oos.icanci.cn.key;

        ssl_session_cache    shared:SSL:1m;
        ssl_session_timeout  5m;
	
	
        ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
        ssl_prefer_server_ciphers  on;
	ssl_protocols TLSv1 TLSv1.1 TLSv1.2;

        #location / {
        #    proxy_pass http://127.0.0.1:8403;
        #    proxy_http_version 1.1;
        #    proxy_set_header Host $host;
        #    proxy_set_header X-Real-IP $remote_addr;
        #    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        #    proxy_set_header   X-Forwarded-Proto $scheme;
        #}
        
        # 服务路径
         location /app/static {
            # 指向的文件路径
          	alias /usr/local/oos/;
            # 开启跨域访问
	  		add_header Access-Control-Allow-Origin *;
        }
    }
}

```



#### Tomcat

- Tomcat是一个Servlet容器
- 核心配置文件在 conf/server.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- 就是说这个端口负责监听关闭tomcat的请求 -->
<Server port="8005" shutdown="SHUTDOWN">
    <GlobalNamingResources>
        <Resource auth="Container" description="User database that can be updated and saved" factory="org.apache.catalina.users.MemoryUserDatabaseFactory" name="UserDatabase" pathname="conf/tomcat-users.xml" type="org.apache.catalina.UserDatabase"/>
    </GlobalNamingResources>
    <Service name="Catalina">
        <!-- 连接超时时间 20000 ms HTTP协议版本 HTTP/1.1 端口号 80 -->
        <Connector connectionTimeout="20000" port="80" protocol="HTTP/1.1" redirectPort="8443" URIEncoding="UTF-8"/>
        <!-- Nginx、Apache等反向代理tomcat时就可以使用ajp协议反向代理到该端口。 -->
        <Connector port="8009" protocol="AJP/1.3" redirectPort="8443"/>
        <Engine defaultHost="localhost" name="Catalina">
            <!-- 授权 -->
            <Realm className="org.apache.catalina.realm.LockOutRealm">
                <Realm className="org.apache.catalina.realm.UserDatabaseRealm" resourceName="UserDatabase"/>
            </Realm>
            <!-- Host 虚拟主机 -->
            <Host appBase="webapps" autoDeploy="true" name="localhost" unpackWARs="true">
                <!-- 日志打印的格式 -->
                <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs" pattern="%h %l %u %t &quot;%r&quot; %s %b" prefix="localhost_access_log." suffix=".txt"/>
                <!-- 手动部署项目 -->
                <Context docBase="H:\EclipseHome\icanci-version-1.11\webapps" path="/"/>  
            </Host>
        </Engine>
    </Service>
</Server>
```

---

```xml
<!-- 属性说明
port:指定一个端口，这个端口负责监听关闭Tomcat的请求
shutdown:向以上端口发送的关闭服务器的命令字符串
-->
<Server port="8005" shutdown="SHUTDOWN">

  <Listener className="org.apache.catalina.core.AprLifecycleListener" />
  <Listener className="org.apache.catalina.mbeans.ServerLifecycleListener" />
  <Listener className="org.apache.catalina.mbeans.GlobalResourcesLifecycleListener" />
  <Listener className="org.apache.catalina.storeconfig.StoreConfigLifecycleListener"/>

  <GlobalNamingResources>
    <Environment name="simpleValue" type="java.lang.Integer" value="30"/>
    <Resource name="UserDatabase" auth="Container"
        type="org.apache.catalina.UserDatabase"
        description="User database that can be updated and saved"
        factory="org.apache.catalina.users.MemoryUserDatabaseFactory"
        pathname="conf/tomcat-users.xml" />
  </GlobalNamingResources>

  <Service name="Catalina">
    <!--
      Connector 元素:
      由 Connector 接口定义.<Connector> 元素代表与客户程序实际交互的组件,它负责接收客户请求,以及向客户返回响应结果.

      属性说明:
        port:服务器连接器的端口号,该连接器将在指定端口侦听来自客户端的请求。
        enableLookups:如果为 true，则可以通过调用 request.getRemoteHost() 进行 DNS 查询来得到远程客户端的实际主机名；若为 false 则不进行DNS查询，而是返回其ip地址。
        redirectPort:服务器正在处理http请求时收到了一个SSL传输请求后重定向的端口号。
        acceptCount:当所有可以使用的处理请求的线程都被用光时,可以放到处理队列中的请求数,超过这个数的请求将不予处理，而返回Connection refused错误。
        connectionTimeout:等待超时的时间数（以毫秒为单位）。
        maxThreads:设定在监听端口的线程的最大数目,这个值也决定了服务器可以同时响应客户请求的最大数目.默认值为200。
        protocol:必须设定为AJP/1.3协议。
        address:如果服务器有两个以上IP地址,该属性可以设定端口监听的IP地址,默认情况下,端口会监听服务器上所有IP地址。
        minProcessors:服务器启动时创建的处理请求的线程数，每个请求由一个线程负责。
        maxProcessors:最多可以创建的处理请求的线程数。
        minSpareThreads:最小备用线程 。
        maxSpareThreads:最大备用线程。
        debug:日志等级。
        disableUploadTimeout:禁用上传超时,主要用于大数据上传时。
    -->
    <Connector port="8080" maxHttpHeaderSize="8192"
               maxThreads="150" minSpareThreads="25" maxSpareThreads="75"
               enableLookups="false" redirectPort="8443" acceptCount="100"
               connectionTimeout="20000" disableUploadTimeout="true" />


    <!-- 负责和其他 HTTP 服务器建立连接。在把 Tomcat 与其他 HTTP 服务器集成时就需要用到这个连接器。 -->
    <Connector port="8009" 
               enableLookups="false" redirectPort="8443" protocol="AJP/1.3" />


    <!-- 
      每个Service元素只能有一个Engine元素.元素处理在同一个<Service>中所有<Connector>元素接收到的客户请求
      属性说明:
        name:对应$CATALINA_HOME/config/Catalina 中的 Catalina ;
        defaultHost: 对应Host元素中的name属性,也就是和$CATALINA_HOME/config/Catalina/localhost中的localhost，缺省的处理请求的虚拟主机名，它至少与其中的一个Host元素的name属性值是一样的
        debug:日志等级
    -->
    <Engine name="Catalina" defaultHost="localhost">

      <Realm className="org.apache.catalina.realm.UserDatabaseRealm"
             resourceName="UserDatabase"/>
      <!--
        由 Host 接口定义.一个 Engine 元素可以包含多个<Host>元素.
        每个<Host>的元素定义了一个虚拟主机.它包含了一个或多个Web应用.

        属性说明：
          name:在此例中一直被强调为$CATALINA_HOME/config/Catalina/localhost中的localhost虚拟主机名
          debug:是日志的调试等级 
          appBase:默认的应用路径,也就是把应用放在一个目录下,并在autoDeploy为true的情况下,可自动部署应用此路径相对于$CATALINA_HOME/ (web applications的基本目录)
          unpackWARs:设置为true,在Web应用为*.war是,解压此WAR文件. 如果为true,则tomcat会自动将WAR文件解压;否则不解压,直接从WAR文件中运行应用程序.
          autoDeploy:默认为true,表示如果有新的WEB应用放入appBase 并且Tomcat在运行的情况下,自动载入应用
      -->
      <Host name="localhost" appBase="webapps"
         unpackWARs="true" autoDeploy="true"
         xmlValidation="false" xmlNamespaceAware="false">
        <!-- 
          属性说明：
            path:访问的URI,如:http://localhost/是我的应用的根目录,访问此应用将用:http://localhost/demm进行操作,此元素必须，
                表示此web application的URL的前缀，用来匹配一个Context。请求的URL形式为http://localhost:8080/path/*
            docBase:WEB应用的目录,此目录必须符合Java WEB应用的规范，web application的文件存放路径或者是WAR文件存放路径。
            debug:日志等级 
            reloadable:是否在程序有改动时重新载入,设置成true会影响性能,但可自动载入修改后的文件，
                如果为true，则Tomcat将支持热部署，会自动检测web application的/WEB-INF/lib和/WEB-INF/classes目录的变化，
                自动装载新的JSP和Servlet，我们可以在不重起Tomcat的情况下改变web application
        -->
        <Context path="/demm" docBase="E:\\projects\\demm\\WebRoot" debug="0" reloadable="true"></Context>
      </Host>
    </Engine>
  </Service>
</Server>
```



#### Docker & K8S

- Docker是一个容器化技术 - 也可以说是极限压榨服务器的资源 - 极大程度的降低服务器成本
- 之前安装Tomcat、JDK等软件，需要先找安装包，然后解压，安装，配置环境，现在不需要了
- 以前安装Linux操作系统，无论是在物理机上还是在云服务器上，其有其他的运行，会消耗很多的额外资源，而使用Docker容器，仅仅包含其内核，能够支持应用即可，大大节省了资源
- 而K8S是一个容器编排技术，它支持分布式集群
-  服务的发现与负载的均衡； 
- 容器的恢复和自动装箱
- 实际上是使用K8S管理Docker集群 - 打个不恰当的比喻 使用Javascript操作Dom和使用Jquery操作Dom
- docker是一个开源的应用容器引擎，开发者可以打包他们的应用以及依赖到一个容器中，发布到流行的liunx系统上，或者实现虚拟化。
- 可以多Docker容器进行调度 - 分配合理的资源给此容器
- k8s是一个开源的容器集群管理系统，可以实现容器集群的自动化部署、自动扩缩容、维护等~

#### Maven

- Maven是一个jar包管理工具
- 常用的命令
- mvn clean
- mvn install
- mvn clean install -DskipTests=true
- 其他

#### Git

- 分布式版本控制系统
- Git分支管理
- 常用命令
- git add .
- git remote add origin 地址
- git commit -m "内容"
- git push origin master
- git pull origin master
- git branch  查看当前分支
- git branch v1 创建v1分支
- git chenkout v1  切换到v1
- git clone  xxx
- git log 查看历史
- git status
- git tag
- git rm --xxx
- git diff xxxx

#### IDEA & Eclipse

- 开发工具
- Eclipse

#### 测试

- Junit单元测试
- 黑盒测试
- 白盒测试
- Jmeter 压力测试

#### 前端基础

- CSS3
- HTML5
- JavaScript
- Ajax
- JSON
- JQuery

#### 前端开源框架

- Layui
- Bootstrap
- AdminLTE

#### Node.js & Vue

- 了解Nodejs
  - 运行在服务器端的JavaScript
  - 是一个基于Chrome JavaScript 运行时建立的一个平台
  - 是一个事件驱动/IO服务端JavaScript环境，基于Google的V8引擎
  - Nodejs集成了包管理，这个类似于Java的Maven仓库，是为了简便打包、搜索自己需要的组件
  - 而且使用Nodejs可以很简单的操作数据库，这使得前端也可以单独部署服务
- 了解模块化开发Vue
  - 在之前的开发中，基于MVC开发模型，无论是使用JSP还是使用模板引擎，后端代码和前端代码都有很多的耦合性，也就是说这样在后端进行修改的时候，也需要进行修改前端代码，很不方便，而且如果不使用第三方组件化开发框架如：Vue、React、Angular 。如果想完成前后端分离，需要写大量的Ajax代码。这样会耗费很多开发的时间。
  - 而在使用如Vue这种前端模块化开发组件的时候，这样的单页面应用，在首次加载的时候比较慢。但是在之后的加载速度是很块的，而且前端和后端使用Axios通信。使用JJWT进行身份校验、盐值公钥私钥加密登录，也可以保证数据的安全，而且前后端分离开发，只要前端和后端工程师对接接口和开发规范即可，单独开发。耦合度和开发效率上升很多
  - 而且前端组件化开发，每个地方都是一个简单的模块组成。通过路由跳转页面，大大提升了复用效果

#### MVC思想

- M：Model 数据对象 如DAO、DTO、VO、BO
- V：View 视图层 用来显示页面和数据的
- C：Controller  控制层 控制页面跳转
- 分层开发也是为了降低系统之间的耦合度

#### MVVM思想

- M：Model 
- V：View
- VM：View-Model 数据绑定组件 如：Vue、React、Angular
- MVVM是基于MVC开发模型升级而来，目的是为了把View中的UI页面和业务逻辑分离开。更一步把业务进行拆分成更小的粒度开发

#### 瀑布流开发模型

- 典型的预见性的方法，严格遵循预先计划的需求分析、设计、编码、集成、测试、维护的步骤进行
- 步骤成果作为衡量进度的方法、
- 缺点：因为前期就已经确定好了需求，在需求不明确的情况下是致命的

#### 敏捷开发模型

- **人和交互** 重于过程和工具。
- **可以工作的软件** 重于求全而完备的文档。
- **客户协作**重于合同谈判。
- **随时应对变化**重于循规蹈矩。
- 作为一个整体工作；按照迭代周期工作；每次迭代交付一些成果；关注业务优先级；可以快速的检查和调整
- 缺点：随着业务和系统规模的增长，针对项目的面对面沟通就更加困难
- 敏捷开发就是快，客户参与-以人为本。强调的产品是软件而非文档。文档是为了开发软件服务的，而不是开发的主体，设计周密是为了最终的软件的质量，但是不表明设计比实现更重要。经常迭代 小版本更新

#### DevOps开发模型

- 为了更好的配合敏捷开发模型。
- 之前我们写代码然后测试，然后再打包。上传服务器。启动运行。测试
- 这种方式有很大的弊端，假如只是稍微的代码改动。那也需要再执行上面的操作。
- 所以：DevOps开发的持续交付就为了解决这种问题。通过自动化构建软件如：Jenkins配合Git、Gitlab、码云、Github实现提交触发项目构建，自动部署
- 如果做服务集群自动化，那么新的解决方案就是Docker技术+K8S

#### 思维导图的绘画

- Xmind
- MindMaster
- Draw.io
- Visio

#### 项目

- 票虫网
- iclass

#### 其他

- 阿里架构演进： https://www.jianshu.com/p/f4a907fe1485 

#### 参考文章

- 操作系统（内存管理）： https://www.cnblogs.com/peterYong/p/6556619.html#_label0 
- Redis查漏补缺： https://www.jianshu.com/p/53997406c273 
- Redis夺命11问： https://www.jianshu.com/p/2a17957cefe7 
- Spring Security 深度解析第二版： https://www.jianshu.com/p/c334b2a0a1eb 
- Spring Security 讲解： https://www.cnblogs.com/zyly/p/12286285.html 
- SpringBoot启动原理： https://www.jianshu.com/p/5da873d1c37c 
- Spring如何解决循环依赖： https://juejin.im/post/6844903715602694152 
- SpringBean生命周期： https://www.jianshu.com/p/3944792a5fff 
- 你对Spring的理解： https://www.zhihu.com/question/48427693?sort=created 
- Mybatis常见面试题1： https://www.jianshu.com/p/3ef3b405c9ac 
- Mybatis常见面试题2：  https://www.jianshu.com/p/6d00418df586 
- JDK新特性： https://my.oschina.net/mdxlcj/blog/3107021 
- JDK15新特性： https://xie.infoq.cn/article/ab7ab97999d51950774d43e54 
- JDK15新特性2： https://xie.infoq.cn/article/ab7ab97999d51950774d43e54 
- HttpServletRequest常用方法： https://blog.csdn.net/lyt_7cs1dn9/article/details/72730535 
- JSP九大内置对象： https://www.cnblogs.com/leirenyuan/p/6016063.html 
- 拦截器、监听器、过滤器： https://zhuanlan.zhihu.com/p/69060111 
- 深入理解BIO、NIO、AIO： https://zhuanlan.zhihu.com/p/51453522 
- **为什么要使用Redis： https://zhuanlan.zhihu.com/p/81195864** 