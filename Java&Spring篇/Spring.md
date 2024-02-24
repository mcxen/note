# Spring 基础笔记

2023-04-18 20:35 华科大LAB 多雨🌧️  - Spring基础完结🎉



# Spring简介

![image](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/1745215-20200715181123141-722382173.png)

Spring框架包含的功能大约由20个小模块组成。这些模块按组可分为核心容器(Core Container)、数据访问/集成(Data Access/Integration)、Web、面向切面编程(AOP和Aspects)、设备(Instrumentation)、消息(Messaging)和测试(Test)。如下图所示：

![image](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/1745215-20200715183321528-138974993.png)\*\**\***

下面对各个模块进行详细介绍：(这些模块我们也可以在Spring的GitHub上查看到：https://github.com/spring-projects/spring-framework)

**(1) 核心容器(Core Container)——Beans、Core、Context、Expression**

该层由4个模块组成：spring-beans spring-core spring-context spring-expression(spring expression Language,SpEl) 。它们对应的**jar包**如下：

1. spring-core：该模块是依赖注入IoC与DI的最基本实现。
2. spring-beans：该模块是Bean工厂与bean的装配。
3. spring-context：该模块构架于核心模块之上，它扩展了 BeanFactory，为它添加了 Bean 生命周期控制、框架事件体系以及资源加载透明化等功能。ApplicationContext 是该模块的核心接口，它的超类是 BeanFactory。与BeanFactory 不同，ApplicationContext 容器实例化后会自动对所有的单实例 Bean 进行实例化与依赖关系的装配，使之处于待用状态。
4. spring-context-indexer：该模块是 Spring 的类管理组件和 Classpath 扫描。
5. spring-context-support：该模块是对 Spring IOC 容器的扩展支持，以及 IOC 子容器。
6. spring-expression：该模块是Spring表达式语言块是统一表达式语言（EL）的扩展模块，可以查询、管理运行中的对象，同时也方便的可以调用对象方法、操作数组、集合等。



**(2)  数据访问与集成(Data Access/Integration)——Jdbc、Orm、Oxm、Jms、Transactions**

该层由spring-jdbc、spring-tx、spring-orm、spring-jms 和 spring-oxm 5 个模块组成。它们对应的jar包如下：

1. spring-jdbc：该模块提供了 JDBC抽象层，它消除了冗长的 JDBC 编码和对数据库供应商特定错误代码的解析。
2. spring-tx：该模块支持编程式事务和声明式事务，可用于实现了特定接口的类和所有的 POJO 对象。编程式事务需要自己写beginTransaction()、commit()、rollback()等事务管理方法，声明式事务是通过注解或配置由 spring 自动处理，编程式事务粒度更细。
3. spring-orm：该模块提供了对流行的对象关系映射 API的集成，包括 JPA、JDO 和 Hibernate 等。通过此模块可以让这些 ORM 框架和 spring 的其它功能整合，比如前面提及的事务管理。
4. spring-oxm：该模块提供了对 OXM 实现的支持，比如JAXB、Castor、XML Beans、JiBX、XStream等。
5. spring-jms：该模块包含生产（produce）和消费（consume）消息的功能。从Spring 4.1开始，集成了 spring-messaging 模块。

***\*(3)\** Web——Web、Webmvc、WebFlux、Websocket**

该层由 spring-web、spring-webmvc、spring-websocket 和 spring-webflux 4 个模块组成。它们对应的jar包如下：

1. spring-web：该模块为 Spring 提供了最基础 Web 支持，主要建立于核心容器之上，通过 Servlet 或者 Listeners 来初始化 IOC 容器，也包含一些与 Web 相关的支持。
2. spring-webmvc：该模块众所周知是一个的 Web-Servlet 模块，实现了 Spring MVC（model-view-Controller）的 Web 应用。
3. spring-websocket：该模块主要是与 Web 前端的全双工通讯的协议。
4. spring-webflux：该模块是一个新的非堵塞函数式 Reactive Web 框架，可以用来建立异步的，非阻塞，事件驱动的服务，并且扩展性非常好。

***\*(4)\** 面向切面编程——AOP，Aspects**

该层由spring-aop和spring-aspects 2个模块组成。它们对应的jar包如下：

1. spring-aop：该模块是Spring的另一个核心模块，是 AOP 主要的实现模块**。**
2. spring-aspects：该模块提供了对 AspectJ 的集成，主要是为 Spring AOP提供多种 AOP 实现方法，如前置方法后置方法等。

***\*(5)\** 设备(Instrumentation)——Instrmentation**

spring-instrument：该模块是基于JAVA SE 中的"java.lang.instrument"进行设计的，应该算是 AOP的一个支援模块，主要作用是在 JVM 启用时，生成一个代理类，程序员通过代理类在运行时修改类的字节，从而改变一个类的功能，实现 AOP 的功能。

***\*(6)\** 消息(Messaging)——Messaging**

spring-messaging：该模块是从 Spring4 开始新加入的一个模块，主要职责是为 Spring 框架集成一些基础的报文传送应用。

***\*(7)\** 测试（Test）——Test**

spring-test：该模块主要为测试提供支持的，通过 JUnit 和 TestNG 组件支持单元测试和集成测试。它提供了一致性地加载和缓存 Spring 上下文，也提供了用于单独测试代码的模拟对象（mock object）。





# IOC 和 DI 的概述

## IOC(Inversion of Controll)

**思想是反转资源获取的方向**，传统的资源查找方式要求组件向容器发起请求查找资源。作为回应，容器适时的返回资源。而应用了IOC之后，则是**容器主动的将资源推送给它所管理的组件，组件所要做的仅是选择一种合适的方式来接收资源**。

在Spring框架中实现控制反转的是**Spring IoC容器**，其**具体就是由容器来控制对象的生命周期和业务对象之间的依赖关系，而不是像传统方式(new 对象)中由代码来直接控制。**程序中所有的对象都会在Spring IoC容器中登记，告诉容器你是个什么，你需要什么，然后IoC容器会在系统运行到适当的时候，把你要的对象主动给你，同时也把你交给其它需要你的对象。也就是说控制对象生存周期的不再是引用它的对象，而是由Spring IoC容器来控制所有对象的创建、销毁。对于某个具体的对象而言，以前是它控制其它对象，现在是所有对象都被Spring IoC容器所控制，所以这叫控制反转。

<mark>控制反转最直观的表达就是，IoC容器让对象的创建不用去new了，而是由Spring自动生产</mark>，使用java的反射机制，根据配置文件在运行时动态的去创建对象以及管理对象，并调用对象的方法。**控制反转的本质是控制权由应用代码转到了外部容器(IoC容器)，控制权的转移即是所谓的反转。**控制权的转移带来的好处就是降低了业务对象之间的依赖程度，即实现了解耦。即然控制反转中提到了反转，那么肯定有正转，正转和反转有什么区别呢？我曾经在博客上看到有人在面试的时候被问到Spring IoC知识点：什么是反转、正转？

- 正转：如果我们要使用某个对象，就需要自己负责对象的创建。
- 反转：如果要使用某个对象，只需要从Spring 容器中获取需要使用的对象，不关心对象的创建过程，也就是把创建对象的控制权反转给了Spring框架。

## DI(Dependency Injection)

是IOC的另一种表述方式，即**组件以一些预先定义好的方式(如：getter方法)来接收来自容器的资源注入**。

DI就是利用JAVA反射来注入。reflection

依赖注入：即应用程序在运行时依赖IoC容器来动态注入对象需要的外部资源。：

　　●谁依赖于谁：当然是应用程序依赖于IoC容器；

　　●为什么需要依赖：应用程序需要IoC容器来提供对象需要的外部资源；

　　●谁注入谁：很明显是IoC容器注入应用程序某个对象，应用程序依赖的对象；

　　●注入了什么：就是注入某个对象所需要的外部资源（包括对象、资源、常量数据）。

**综合上述，我们可以用一句话来概括：所谓Spring IoC/DI，就是由 Spring 容器来负责对象的生命周期和对象之间的依赖关系。**



> 注意：IoC和AOP这些概念并不是Spring提出来的，它们在Spring出来之前就已经存在了，只是之前更多的是偏向于理论，没有产品很好的实现，直到Spring框架将这些概念进行了很好的实现。



# HelloSpring

> ### jar包和war包区别
>
> jar是java普通项目打包，通常是开发时要引用通用类，打成jar包便于存放管理。当你使用某些功能时就需要这些jar包的支持，需要导入jar包。war是java web项目打包，web网站完成后，打成war包部署到服务器，目的是为了节省资源，提供效率。
>
> - jar文件（扩展名为. Jar，Java Application Archive）包含Java类的普通库、资源（resources）、辅助文件（auxiliary files）等。通常是开发时要引用的通用类，打成包便于存放管理。简单来说，jar包就是别人已经写好的一些类，然后对这些类进行打包。可以将这些jar包引入到你的项目中，可以直接使用这些jar包中的类和属性，这些jar包一般放在lib目录下。
>
> - war文件（扩展名为.War,Web Application Archive）包含全部Web应用程序。在这种情形下，一个Web应用程序被定义为单独的一组文件、类和资源，用户可以对jar文件进行封装，并把它作为小型服务程序（servlet）来访问。 **war包是一个可以直接运行的web模块，通常用于网站，打成包部署到容器中。**以Tomcat来说，**将war包放置在其\webapps\目录下,然后启动Tomcat，**这个包就会自动解压，就相当于发布了。war包是Sun提出的一种web应用程序格式，与jar类似，是很多文件的压缩包。war包中的文件按照一定目录结构来组织。根据其根目录下包含有html和jsp文件，或者包含有这两种文件的目录，另外还有WEB-INF目录。**通常在WEB-INF目录下含有一个web.xml文件和一个classes目录，web.xml是这个应用的配置文件，而classes目录下则包含编译好的servlet类和jsp，或者servlet所依赖的其他类（如JavaBean）。通常这些所依赖的类也可以打包成jar包放在WEB-INF下的lib目录下。**
>
> - Ear文件（扩展名为.Ear,Enterprise Application Archive）包含全部企业应用程序。在这种情形下，一个企业应用程序被定义为多个jar文件、资源、类和Web应用程序的集合。
>
> **SpringBoot项目既可以打成war包发布，也可以找成jar包发布。**
>
> - jar包：直接通过内置Tomcat运行，不需要额外安装Tomcat。如需修改内置Tomcat的配置，只需要在SpringBoot的配置文件中配置。内置Tomcat没有自己的日志输出，全靠jar包应用输出日志。但是比较方便，快速，比较简单。
> - war包：传统的应用交付方式，需要安装Tomcat，然后放到wabapps目录下运行war包，可以灵活选择Tomcat版本，可以直接修改Tomcat的配置，有自己的Tomcat日志输出，可以灵活配置安全策略,相对打成jar包来说没那么快速方便。



![截屏2023-04-13 19.03.19](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-04-13%2019.03.19.png)

这里的左边是最基础的实现IOC的jar包。

在resource里配置`applicationContext.xml`，新建entity people

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <!-- applicationContext.xml 配置bean -->
    <!-- class: bean的全类名，通过反射的方式在IOC容器中创建bean，所以要求bean中必须有无参构造器 -->
    <bean id="Redpeople" class="entity.People">
        <property name="name" value="红富士"/>
<!--        这是配置文件，不需要重新编译就可以生效，spring会自动实例，自动配置-->
    </bean>
</beans>
```

`People.java`

```java
public class People {
    private String name;

    public People() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "People{" +
                "name='" + name + '\'' +
                '}';
    }
}
```

配置测试文件：

`SpringApplication.java`



```java
import entity.People;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringApplication {
    public static void main(String[] args) {
        ApplicationContext context =
                new ClassPathXmlApplicationContext(
                        "classpath:applicationContext.xml");
        People people = context.getBean("people", People.class);//相当于从一个筒里面取出来。
        System.out.println("people = " + people);
    }
}
```

> `ApplicationContext`有多种实现方法，这是一种`ClassPathXmlApplicationContext`

![截屏2023-04-13 19.16.12](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-04-13%2019.16.12.png)

# Spring配置

在SpringIOC容器读取bean配置创建bean实例之前，必须对它进行实例化。只有在容器实例化后，才可以从IOC容器里获取bean实例并使用

## 两种类型的IOC容器实现

Spring提供了两种类型的IOC容器实现

+ **BeanFactory：IOC容器的基本实现，在调用getBean()方法时才会实例化对象**
+ **ApplicationContext：提供了更多的高级特性，在加载配置文件后就会实例化对象。是BeanFactory的子接口**

`BeanFactory`是Spring框架的基础设施，面向Spring本身

`ApplicationContext`面向使用Spring框架的开发者，几乎所有的应用场合都直接使用`ApplicationContext`而非底层的`BeanFactory`

1.BeanFactroy采用的是延迟加载形式来注入Bean的，即只有在使用到某个Bean时(调用getBean())，才对该Bean进行加载实例化，**这样，我们就不能发现一些存在的Spring的配置问题。而ApplicationContext则相反，它是在容器启动时，一次性创建了所有的Bean。这样，在容器启动时，我们就可以发现Spring中存在的配置错误。** 相对于基本的BeanFactory，ApplicationContext 唯一的不足是占用内存空间。当应用程序配置Bean较多时，程序启动较慢。

BeanFacotry延迟加载,如果Bean的某一个属性没有注入，BeanFacotry加载后，直至第一次使用调用getBean方法才会抛出异常；而ApplicationContext则在初始化自身是检验，这样有利于检查所依赖属性是否注入；所以通常情况下我们选择使用 ApplicationContext。
应用上下文则会在上下文启动后预载入所有的单实例Bean。通过预载入单实例bean ,确保当你需要的时候，你就不用等待，因为它们已经创建好了。

2.BeanFactory和ApplicationContext都支持BeanPostProcessor、BeanFactoryPostProcessor的使用，但两者之间的区别是：BeanFactory需要手动注册，而ApplicationContext则是自动注册。（Applicationcontext比 beanFactory 加入了一些更好使用的功能。而且 beanFactory 的许多功能需要通过编程实现而 Applicationcontext 可以通过配置实现。比如后处理 bean ， Applicationcontext 直接配置在配置文件即可而 beanFactory 这要在代码中显示的写出来才可以被容器识别。 ）

3.beanFactory主要是面对与 spring 框架的基础设施，面对 spring 自己。而 Applicationcontex 主要面对与 spring 使用的开发者。基本都会使用 Applicationcontex 并非 beanFactory 。

**无论使用何种方式，配置文件时都是相同的**

```xml
<!-- applicationContext.xml 配置bean -->
<!-- class: bean的全类名，通过反射的方式在IOC容器中创建bean，所以要求bean中必须有无参构造器 -->
<bean id="people" class="learningspring.ioc.examples.People">
    <property name="name" value="Chen"/>
</bean>
```

```java
public class People {
    private String name;

    public People() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "People{" +
                "name='" + name + '\'' +
                '}';
    }
}
```

```java
@Test
public void test(){
    // 创建IOC容器
    ApplicationContext ctx = new FileSystemXmlApplicationContext("applicationContext.xml");

    // 从IOC容器中获取bean实例
    People people = (People) ctx.getBean("people");

    System.out.println(people);
}
```

## ApplicationContext

`ApplicationContext`有两个实现类：

+ `ClassPathXmlApplicationContext`：加载**类路径**里的配置文件

> 原来如此，ClassPath 就是类的路径，FileSys就是文件系统，**ClassPath还可以加载字符串数组的方式加载多个配置文件。**
>
> 路径表达式
>
> 

+ `FileSystemXmlApplicationContext`：加载文件系统里的配置文件

ApplicationContext 是 Spring 应用程序中的中央接口，用于向应用程序提供配置信息 它继承了 BeanFactory 接口，所以 ApplicationContext 包含 BeanFactory 的所有功能以及更多功能！它的主要功能是支持大型的业务应用的创建 **特性：**

- Bean instantiation/wiring
- Bean 的实例化/串联
- 自动的 BeanPostProcessor 注册
- 自动的 BeanFactoryPostProcessor 注册
- 方便的 MessageSource 访问（i18n）
- ApplicationEvent 的发布 与 BeanFactory 懒加载的方式不同，它是预加载，所以，每一个 bean 都在 ApplicationContext 启动之后实例化 这里是 ApplicationContext 的使用例子：

```java
package com.zoltanraffai;  
import org.springframework.core.io.ClassPathResource;  
import org.springframework.beans.factory.InitializingBean; 
import org.springframework.beans.factory.xml.XmlBeanFactory; 
public class HelloWorldApp{ 
   public static void main(String[] args) { 
      ApplicationContext context=new ClassPathXmlApplicationContext("beans.xml"); 
      HelloWorld obj = (HelloWorld) context.getBean("helloWorld");    
      obj.getMessage();    
   }
}
```

###  常用的获取ApplicationContext

FileSystemXmlApplicationContext：从文件系统或者url指定的xml配置文件创建，参数为配置文件名或文件名数组，有相对路径与绝对路径。

```java
ApplicationContext factory=new FileSystemXmlApplicationContext("src/applicationContext.xml");
ApplicationContext factory=new FileSystemXmlApplicationContext("E:/Workspaces/MyEclipse 8.5/Hello/src/applicationContext.xml");
```

ClassPathXmlApplicationContext：从classpath的xml配置文件创建，可以从jar包中读取配置文件。ClassPathXmlApplicationContext 编译路径总有三种方式：

```java
ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
ApplicationContext factory = new ClassPathXmlApplicationContext("applicationContext.xml"); 
ApplicationContext factory = new ClassPathXmlApplicationContext("file:E:/Workspaces/MyEclipse 8.5/Hello/src/applicationContext.xml");
```

XmlWebApplicationContext：从web应用的根目录读取配置文件，需要先在web.xml中配置，可以配置监听器或者servlet来实现

```xml
<listener>
<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

#### 总结

> ApplicationContext 包含 BeanFactory 的所有特性，通常推荐使用前者。但是也有一些限制情形，比如移动应用内存消耗比较严苛，在那些情景中，使用更轻量级的 BeanFactory 是更合理的。然而，在大多数企业级的应用中，ApplicationContext 是你的首选。

## ApplicationContext中Bean的相关配置

### bean标签的id和name的配置

+ `id`：使用了约束中的唯一约束。不能有特殊字符**（默认）**
+ `name`：没有使用约束中的唯一约束（理论上可以重复，但是实际开发中不能出现），可以有特殊字符

> name，可以这么搞
>
> ```xml
> <bean id="dog1,dog2" class="entity.Dog"></bean>
> ```
>
> 然后在context中`getBean("dog1");`也可以`getBean("dog2");`也可以，但是这样没有意义，设置多个bean标识，没有意义，所以我们一般就设置id。

### bean的生命周期的配置

+ `init-method`：bean被初始化的时候执行的方法
+ `destroy-method`：bean被销毁的时候执行的方法，前提是bean是单例的，工厂关闭

### bean的作用范围的配置-scope

+ `scope`：bean的作用范围
  + **singleton：单例模式，默认的作用域。**getBean就是唯一的。
  + **prototype：多例模式。** getBean就会得到新的。
  + request：应用在Web项目中，Spring创建这个类后，将这个类存入到request范围中。
  + session：应用在Web项目中，Spring创建这个类后，将这个类存入到session范围中。
  + globalsession：应用在Web项目中，必须在porlet环境下使用。但是如果没有这种环境，相当于session。

## Spring的Bean管理配置

### Spring的Bean的实例化方式

#### 无参构造方式（默认）

```java
/**
 * //TODO
 *
 * @author Chen Rui
 * @version 1.0
 **/
public class Dog {

    private String name;
    private Integer length;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "name='" + name + '\'' +
                ", length=" + length +
                '}';
    }
}

```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- Spring Bean的实例化方式-->

    <!-- 无参构造的方式 -->
    <bean id="dog" class="entity.Dog">
       
    </bean>
</beans>
```



#### 带参构造方式

```java
package entity;

public class Dog {

    private String name;
    private Integer length;

    public Dog(String name, Integer length) {
        this.name = name;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "name='" + name + '\'' +
                ", length=" + length +
                '}';
    }
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- Spring Bean的实例化方式-->

    <!-- 带参构造的方式 -->
    <bean id="dog" class="entity.Dog">
        <constructor-arg name="name" value="HAVAL"/>
        <constructor-arg name="length" value="5"/>
      <!-- 5是自动转换的。-->
    </bean>
</beans>
```

>也可以使用
>
>```xml
><constructor-arg index="1" value="HAVAL"/>
>```
>
>指名位置，第几个参数

测试代码：`SpringApplication.java`

```java
import entity.Dog;
import entity.People;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringApplication {
    public static void main(String[] args) {
        ApplicationContext context =
                new ClassPathXmlApplicationContext(
                        "classpath:applicationContext.xml");
        People people = context.getBean("people", People.class);//相当于从一个筒里面取出来。
        System.out.println("people = " + people);
        Dog dog = context.getBean("dog", Dog.class);
        System.out.println("dog = " + dog);
    }
}
```

```sh
dog = Dog{name='HAVAL', length=5}
```





#### 静态工厂实例化方式

工厂模式可以隐藏一些生成对象的细节。



```java
//car实体
public class Car {
    private String name;
    private Double price;

    public Car() {
    }

    public Car(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Car{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
```

```java
//car工厂
public class CarFactory {

    public static Car createCar(){
      //这里是static
        return new Car();
    }
}
```

```xml

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- Spring Bean的实例化方式-->
    <!-- 静态工厂的方式 -->
    <bean id="car" class="CarFactory" factory-method="createCar"/>

</beans>
```



#### 实例工厂实例化方式

IOC容器堆Dog进行实例化

```java

public class Dog {

    private String name;
    private Integer length;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "name='" + name + '\'' +
                ", length=" + length +
                '}';
    }
}

```

```java

public class DogFactory {
    public Dog createDog(){
        return new Dog();
    }
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- Spring Bean的实例化方式-->

    <!-- 实例工厂的方式 -->
    <bean id="dogFactory" class="DogFactory"/>
    <bean id="dog2" factory-bean="dogFactory" factory-method="createDog"/>
</beans>
```

> 最大区别就是static和不带static，其他区别不大。

### Spring的属性注入方式

#### 带参构造方法方式的属性注入

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <!--构造方法方式的属性注入-->
    <bean id="car" class="learningspring.ioc.examples.demo3.Car">
        <constructor-arg name="name" value="BWM"/>
        <constructor-arg name="price" value="800000"/>
    </bean>
</beans>
```

#### Set方法方式的属性注入

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--Set方法方式的属性注入-->
    <bean id="dog" class="learningspring.ioc.examples.demo3.Dog">
        <property name="name" value="Golden"/>
        <property name="length" value="100"/>
    </bean>
</beans>
```

#### 为Bean注入引用类型的数据

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <!--带参构造方法方式的属性注入-->
    <bean id="car" class="learningspring.ioc.examples.demo3.Car">
        <constructor-arg name="name" value="BWM"/>
        <constructor-arg name="price" value="800000"/>
    </bean>

    <!--Set方法方式的属性注入-->
    <bean id="dog" class="learningspring.ioc.examples.demo3.Dog">
        <property name="name" value="Golden"/>
        <property name="length" value="100"/>
    </bean>

    <!--为Bean注入对象属性-->
    <!--构造方法方式一样可行-->
    <bean id="employee" class="learningspring.ioc.examples.demo3.Employee">
        <property name="name" value="Chen"/>
        <property name="car" ref="car"/>
        <property name="dog" ref="dog"/>
    </bean>
</beans>
```

#### P名称空间的属性注入（Spring2.5）

+ 通过引入p名称空间完成属性注入
  + 普通属性：p:属性名=“值”
  + 对象属性：p:属性名-ref=“值”

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--P名称空间的属性注入-->
    <bean id="cat" class="learningspring.ioc.examples.demo3.Cat" p:name="Orange" p:length="100"/>
    
    <!--为Bean注入对象属性-->
    <bean id="employee" class="learningspring.ioc.examples.demo3.Employee" p:cat-ref="cat">
        <property name="name" value="Chen"/>
        <property name="car" ref="car"/>
        <property name="dog" ref="dog"/>
    </bean>
</beans>
```

#### SpEL方式的属性注入（Spring3）

SpEL：Spring Expresssion Language  的表达式语言

语法：#{表达式}

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--SpEL表达式注入-->
    <bean id="cat2" class="learningspring.ioc.examples.demo4.Cat">
        <!--字符串要加单引号-->
        <!--也可以通过#{beanName.属性名或方法名}来通过其他bean的属性或者方法来注入-->
        <property name="name" value="#{'Orange'}"/>
        <property name="length" value="#{101}"/>
    </bean>
</beans>
```



### 注入集合类型的数据

```java
/**
 * 注入集合类型的数据测试
 *
 * @author Chen Rui
 * @version 1.0
 **/
public class CollectionBean {

    private String[] strs;
    private List<String> list;
    private Set<String> set;
    private Map<String, String> map;

    public void setStrs(String[] strs) {
        this.strs = strs;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public void setSet(Set<String> set) {
        this.set = set;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "CollectionBean{" +
                "strs=" + Arrays.toString(strs) +
                ", list=" + list +
                ", set=" + set +
                ", map=" + map +
                '}';
    }
}

```



```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--Spring的集合属性的注入-->
    <!--注入数组类型-->
    <bean id="collectionBean" class="learningspring.ioc.examples.demo4.CollectionBean">
        <!-- 注入数组类型 -->
        <property name="strs">
            <list>
                <value>Tom</value>
                <value>Jack</value>
            </list>
        </property>

        <!-- 注入List集合 -->
        <property name="list">
            <list>
                <value>Lucy</value>
                <value>Lily</value>
            </list>
        </property>

        <!-- 注入Set集合 -->
        <property name="set">
            <set>
                <value>aaa</value>
                <value>bbb</value>
                <value>ccc</value>
            </set>
        </property>

        <!-- 注入Map集合 -->
        <property name="map">
            <map>
                <entry key="a" value="1"/>
                <entry key="b" value="2"/>
            </map>
        </property>
    </bean>
</beans>
```

### Spring分模块开发的配置

+ 加载配置文件时，直接加载多个配置文件

```java
ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext1.xml", "applicationContext2.xml");
```

+ 在一个配置文件中引入多个配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--引入配置文件-->
	<import resource="applicationContext2.xml"/>
    
</beans>
```

# Spring开发中的常用注解



![image.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/6d562a58242d4f479a2ea42407be6336~tplv-k3u1fbpfcp-zoom-in-crop-mark:4536:0:0:0.awebp?)

三类：

- 组件类型注解

- 自动装配注解

- 元数据注解



>  使用注解需要开启组件扫描：设置XML配置，组件扫描ComponentScan的basePackage, 比如
>
> `<context:component-scan base-package='tech.pdai.springframework'>`,
>
>  或者`@ComponentScan("tech.pdai.springframework")`注解，或者 `new AnnotationConfigApplicationContext("tech.pdai.springframework")`指定扫描的basePackage.



## @Component-衍生注解：Controller,Service,Respository

该注解在类上使用，使用该注解就相当于在配置文件中配置了一个Bean，例如：

```java
@Component("userDao")
public class UserDaoImpl implements UserDao {
    @Override
    public void save() {
        System.out.println("UserDaoImpl.save");
    }
}
```

相当于以下内容：

```xml
<bean id="userDao" class="UserDaoImpl"></bean>
```

该注解有3个衍生注解：

+ **@Controller：修饰Web 层类**
+ **@Service：修饰Service层类**
+ **@Repository：修饰Dao层类**

## @Value-属性注入

>  意义：其实是可以从配置文件中导入，所以显得比直接赋值更加灵活

该注解用于给属性注入值，有2种用法

+ 如果有set方法，则需要将该注解添加到set方法上
+ 如果没有set方法，则需要将该注解添加到属性上

```java

@Component("dog")
public class Dog {
    private String name;

    @Value("100") // 注入属性值
    private Double length;

    public Dog() {
    }

    public Dog(String name, Double length) {
        this.name = name;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    @Value("Golden") // 注入属性值
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "name='" + name + '\'' +
                ", length=" + length +
                '}';
    }
}

```

进阶用法：

使用方式有:

- @Value(“常量”)  常量,包括字符串,网址,文件路径等
- @Value(“${}”` : default_value`)    执行SpEl表达式，读取配置文件
- @Value(“#{}”`? : default_value`)    读取注入bean的属性

> 说明:
>
> 读取配置文件方式中, 可添加默认值,即前面变量不存在或读取失败时,时候默认值.
>
> 读取注入bean属性,也可添加默认值,效果同上述一样.
>
> #### 注入SPEL表达式结果
>
> ```java
> @Value("#{ T(java.lang.Math).random() * 100.0 }")
> private double randomNumber; //注入表达式结果
> ```
>
> - `@Value`支持SPEL表达式，它的具体用法参考：[cloud.tencent.com/developer/a…](https://link.juejin.cn?target=https%3A%2F%2Fcloud.tencent.com%2Fdeveloper%2Farticle%2F1676200)
>
> 
>
> #### 注入系统属性
>
> ```java
> @Value("#{systemProperties['os.name']}")
>  private String systemPropertiesName; // 注入操作系统属性
> ```
>
> - 可以通过`System.getProperties()`方法获取操作系统有哪些属性
>
> 
>
> #### 注入其他Bean的属性
>
> ```
> @Value("#{person.birth}")
>  private Date personBirth;
> ```
>
> - person是其他bean的名称

还可以使用properties。

## @Autowired-自动装配对象



`@Value` 通常用于普通属性的注入。

`@Autowired` 通常用于为**对象类型的属性注入值**，但是按照**类型**完成属性注入

习惯是按照**名称**完成属性注入，所以必须让`@Autowired`注解和`@Qualifier`注解**一同使用**。



我们可以在**类的属性、setter方法以及构造函数**上使用*@Autowired* 注解。

###  在属性上使用*@Autowired*

*@Autowired*可以直接应用到类的属性上，即使该属性的类型不是public、同时也没有为其定义setter方法，也不会影响自动装配的功能。

为了演示自动装配功能，我们首先定义一个*FooFormatter* bean:

```java
@Component
public class FooFormatter {
    public String format() {
        return "foo";
    }
}
```

接着便可以在属性上应用`@Autowired`来完成自动装配：

```java
@Component
public class FooService {  
    @Autowired
    private FooFormatter fooFormatter;
}
```

此时，Spring将自动为`FooService`中的`FooFormatter`属性提供`fooFormatter` bean。我们也可以认为是自动(auto)将`FooFormatter`与`FooFormatter`连接(wire)了起来，所以此项功能叫做Autowired。

### 在setter方法上使用*@Autowired*

还可以在setter方法上使用*@Autowired*，比如3.1中的代码还可改写为：

```java
@Component
public class FooService {
    private FooFormatter fooFormatter;
    @Autowired
    public void setFooFormatter(FooFormatter fooFormatter) {
        this.fooFormatter = fooFormatter;
    }
}
```

与在属性上使用*@Autowired*来完成自动装配相比较，在setter上使用*@Autowired*没有什么明显的不 。当我们在装配某个属性的同时还希望执行某些逻辑操作时，往往会这么做。



### 在构造函数上使用*@Autowired*-最佳

最后，还可以在构造函数上使用*@Autowired*，这也是官方推荐的一种使用方式，比如3.2的代码还可以改写为：

```java
@Component
public class FooService {
    private FooFormatter fooFormatter;
    @Autowired
    public FooService(FooFormatter fooFormatter) {
        this.fooFormatter = fooFormatter;
    }
}
```

值得注意的是，在较新的Spring版本中完全可以省略在构造函数上声明的*@Autowired*注解。也就是说以下代码同样可以完成自动装配功能：

```java
@Component
public class FooService {
    private FooFormatter fooFormatter;

    public FooService(FooFormatter fooFormatter) {
        this.fooFormatter = fooFormatter;
    }
}
```

### @Qualifier

qualifier译为限定符、限定语。

@qualifier可以解决多个对象实现同一个接口问题，比如我们当前有两个实现了*Message* 接口的bean:

```java
public interface Message {
    String hello();
}

@Component
public class FooMessage implements Message {

    @Override
    public String hello() {
        return "foo";
    }
}

@Component
public class BarMessage implements Message {

    @Override
    public String hello() {
        return "bar";
    }
}
```

此时若直接使用*@Autowired* 注解尝试注入*Message*:

```java
@Service
public class MessageService {
    @Autowired
    Message message;
}
```

由于*FooMessage* 和*BarMessage*都实现了*Message*接口，所以Spring容器中现在有两个同样是实现了*Message*接口的bean，此时若使用*@Autowired* 尝试注入*Message*, Spring便不知道应该是注入*FooMessage* 还是*BarMessage*，所以发生NoUniqueBeanDefinitionException异常。

NoUniqueBeanDefinitionException的字面意思也很好的说明了这个问题：定义的bean不唯一。

当同一类型存在多个bean时，可以使用*@Qualifier* 来显示指定bean的名称，从而解决了Spring按类型注入时不知道应该注入哪个bean的尴尬情况。

```java
@Service
public class MessageService {
    @Autowired
    @Qualifier("fooMessage")
    
    Message message;
}
```

默认情况下Spring自动为每个bean添加一个唯一的名称，该名称的命名原则为：将类的名称由大驼峰变为小驼峰。所以*FooMessage* 的bean名为：fooMessage；*BarMessage* 的bean名为: barMessage。除此以外，我们还可以自定义bean的名称，比如将*BarMessage* 的bean名称定义为customBarMessage：

```java
@Component("customBarMessage")
public class BarMessage implements Message {
```

*@Qualifier* 将根据声明的fooFormatter来完成对Formatter的注入。此时*FooService* 中被注入的*Formatter* 为*FooFormatter* 而非*BarFormatter* 。

也可以加一个@Primary，

```java
@Component
@Primary
public class FooMessage implements Message {

    @Override
    public String hello() {
        return "foo";
    }
}
```

就锁定了这个FooMessage

## @Resource自动装配对象

该注解也可以用于属性注入，通常情况下使用**@Resource注解**，默认按照**名称**完成属性注入。

该注解由J2EE提供，需要导入包`javax.annotation.Resource`。

`@Resource`有两个重要的属性：`name`和`type`，而Spring将`@Resource`注解的`name`属性解析为bean的名字，而`type`属性则解析为bean的类型。所以，如果使用`name`属性，则使用byName的自动注入策略，而使用`type`属性时则使用byType自动注入策略。如果既不制定`name`也不制定`type`属性，这时将通过反射机制使用byName自动注入策略。

```java
@Controller("userController")
public class UserController {
    
    @Resource(name = "userService")
    //上也可以不写userService，写的时候的原因是防止这个属性的名字出现奇怪的东西
    private UserService userService; 
    
}
```

```java
@Service("userService")
public class UserServiceImpl implements UserService {

	@Resource(name = "userDao")
    private UserDao userDao;

    @Override
    public void save() {
        System.out.println("UserServiceImpl.save");
        userDao.save();
    }
}

```

```java
@Component("userDao")
public class UserDaoImpl implements UserDao {
    @Override
    public void save() {
        System.out.println("UserDaoImpl.save");
    }
}

```

## @PostConstruct 和 @PreDestroy

`@PostConstruct`和`@PreDestroy`注解，前者为Bean生命周期相关的注解，在配置文件中，使用到了`<init-method>`参数来配置Bean初始化之前要执行的方法，该注解也是同样的作用。将该注解添加到想在初始化之前执行的目标方法上，就可以实现该功能，而后者则是Bean生命周期中`<destroy-method>`目标方法，使用该注解在指定方法上，即可实现在Bean销毁之前执行该方法。

```java
/**
 * UserDao实现类
 * @author Chen Rui
 * @version 1.0
 **/

@Component("userDao")
public class UserDaoImpl implements UserDao {
    
    @PostConstruct
    public void init(){
        System.out.println("UserDaoImpl.init");
    }
    
    @Override
    public void save() {
        System.out.println("UserDaoImpl.save");
    }
    
    @PreDestroy
    public void destroy(){
        System.out.println("UserDaoImpl.destroy");
    }
}
```

## @Scope

Bean的作用范围的注解，默认为singleton（单例）

可选值：

+ singleton
+ prototype
+ request
+ session
+ globalsession

```java
@Component("userDao")
@Scope // 默认为singleton
public class UserDaoImpl implements UserDao {

    @PostConstruct
    public void init(){
        System.out.println("UserDaoImpl.init");
    }

    @Override
    public void save() {
        System.out.println("UserDaoImpl.save");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("UserDaoImpl.destroy");
    }
}
```

## 基于XML配置和基于注解配置的对比

|                | 基于XML的配置                                                | 基于注解的配置                                               |
| -------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Bean的定义     | \<bean id="Bean的id" class="类的全路径"/>                    | @Component或衍生注解@Controller，@Service和@Repository       |
| Bean的名称     | 通过id或name指定                                             | @Component(“Bean的id”)                                       |
| Bean的属性注入 | \<property>或者通过p命名空间                                 | 通过注解@Autowired 按类型注入<br />通过@Qualifier按名称注入  |
| Bean的生命周期 | init-method指定Bean初始化前执行的方法，destroy-method指定Bean销毁前执行的方法 | @PostConstruct 对应于int-method<br />@PreDestroy 对应于destroy-method |
| Bean的作用域   | 在bean标签中配置scope属性                                    | @Scope, 默认是singleton<br />配置多例可以在目标类上使用@Scope(“prototype”) |
| 使用场景       | Bean来自第三方，可以使用在任何场景                           | Bean的实现类由自己维护                                       |

XML可以适用于任何场景，就算Bean来自第三方也可以适用XML方式来管理。而注解方式就无法在此场景下使用，注解方式可以让开发的过程更加方便，但前提是Bean由自己维护，这样才能在源码中添加注解。

所以可以使用**两者混合**的方式来开发项目，使用**XML配置文件来管理Bean，使用注解来进行属性注入**



## Java Config核心注解

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5448c2d20ba74c48bba766a507f5530b~tplv-k3u1fbpfcp-zoom-in-crop-mark:4536:0:0:0.awebp?)

### @Configuration

@Configuration用于定义配置类，可替换xml配置文件，被注解的类内部包含有一个或多个被@Bean注解的方法，这些方法将会被AnnotationConfigApplicationContext或AnnotationConfigWebApplicationContext类进行扫描，并用于构建bean定义，初始化Spring容器。

即：

```java
public static void main(String[] args) {
    // @Configuration注解的spring容器加载方式，用AnnotationConfigApplicationContext替换ClassPathXmlApplicationContext
    ApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
    //获取bean
    TestBean tb = (TestBean) context.getBean("testBean");
    tb.sayHello();
}
```



注意：@Configuration注解的配置类有如下要求：

- @Configuration不可以是final类型；
- @Configuration不可以是匿名类；
- 嵌套的@Configuration必须是静态类。

@Bean标注在方法上(返回某个实例的方法)，等价于spring的xml配置文件中的，作用为：注册bean对象

@ComponentScan



# Spring AOP

## AOP的概述

即**面向切面编程**，通过**预编译**方式和运行期动态代理实现程序功能的统一维护的一种技术。利用AOP可以对业务逻辑的各个部分进行**隔离**，从而使得业务逻辑各部分之间的**耦合度降低**，提高程序的**可重用性**，同时提高了开发的效率。

## AOP的案例（应用场景）

背景：某项目已经写好了保存到数据库的方法。假设现在需要添加一个新的功能，例如权限校验，在保存到数据库之前要对用户权限进行校验。

```java
public class UserDaoImpl implements UserDao {
    @Override
    public void save(){
        ...
    }
}
```

现在需要多加一个需求，在用户将数据保存到数据库之前，进行权限校验。

此时通常就会在该方法中添加一个方法来进行权限校验然后在save方法中调用。

```java
public class UserDaoImpl implements UserDao {
    @Override
    public void save(){
        checkPri();
        // 保存到数据库
    }
    
    private void checkPri(){
        // 权限校验
    }
}
```

用这样的方法来实现，弊端就是只能在这一个类中使用，通常一个项目中有许多的方法都可能需要执行权限校验，**此时就要在每个类中编写同样的代码，所以该方法并不科学。**

此时就有了一个更好的方法，即**纵向继承**。

定义一个通用的Dao，在通用的Dao中编写权限校验的方法。

```java
public class BaseDao{
    public void checkPri(){
        // 权限校验
    }
}
```

然后每一个不同的类都去继承这个类，再调用该方法

```java
public class UserDaoImpl extends BaseDao implements UserDao{
    @Override
    public void save(){
        checkPri();
        // 保存到数据库
    }
}
```

此时就只需要维护`BaseDao`中的一份代码就可以，大大减轻了工作量，提高了效率。

但AOP的思想更高一步，**不采用纵向继承父类对象的方法**，而采用**横向抽取**来取代

```java
public class UserDaoImpl implements UserDao{
    @Override 
    public void save(){
        // 保存到数据库
    }
}
```

横向抽取机制实质上就是**代理机制**，通过创建`UserDaoImpl`类的代理类，通过代理类来调用权限校验的方法。

## AOP底层实现-动态代理

代理是设计模式的一种，代理类为委托类提供消息预处理，消息转发，事后消息处理等功能。Java中的代理分为三种角色： `代理类、委托类、接口`。

为了保持行为的一致性，代理类和委托类通常会实现相同的接口，所以在访问者看来两者没有丝毫的区别。通过代理类这中间一层，能有效控制对委托类对象的直接访问，也可以很好地隐藏和保护委托类对象，同时也为实施不同控制策略预留了空间，从而在设计上获得了更大的灵活性。Java 动态代理机制以巧妙的方式近乎完美地实践了代理模式的设计理念。

Java中的代理按照代理类生成时机不同又分为`静态代理`和`动态代理`：

- **静态代理**：静态代理的特点是, 为每一个业务增强都提供一个代理类, 由代理类来创建代理对象. 下面我们通过静态代理来实现对转账业务进行身份验证.
- **动态代理**：静态代理会为每一个业务增强都提供一个代理类, 由代理类来创建代理对象, 而动态代理并不存在代理类, 代理对象直接由代理生成工具动态生成.

静态代理是自己手写proxy类，动态是自动生成proxy，运行时生成。

AOP的实现使用了动态代理技术，动态代理分为两种

+ JDK动态代理：只能对实现了接口的类产生代理
+ Cglib动态代理（类似于javassist的第三方代理技术）：对没有实现接口的类产生代理对象，即生成子类对象。



### JDK动态代理

JDK动态代理是使用 java.lang.reflect 包下的代理类来实现. **JDK动态代理动态代理必须要有接口.**

#### JDK动态代理案例

该案例实现一个计算器的日志功能

首先创建一个接口`Calculator`

```java
/**
 * 计算器接口
 *
 * @author Chen Rui
 * @version 1.0
 **/
public interface Calculator {

    /**
     * 加法
     * @param a 实数
     * @param b 实数
     * @return 相加结果
     */
    int add(int a, int b);

    /**
     * 减法
     * @param a 实数,被减数
     * @param b 实数,减数
     * @return 相减结果
     */
    int sub(int a, int b);

    /**
     * 乘法
     * @param a 实数
     * @param b 实数
     * @return 相乘结果
     */
    int mul(int a, int b);

    /**
     * 除法
     * @param a 实数,被除数
     * @param b 实数,除数
     * @return 相除结果
     */
    int div(int a, int b);
}
```

接着创建一个类`CalculatorImpl`来实现该接口并重写方法

```java
/**
 * 计算器实现类
 *
 * @author Chen Rui
 * @version 1.0
 **/
public class CalculatorImpl implements Calculator {

    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int sub(int a, int b) {
        return a - b;
    }

    @Override
    public int mul(int a, int b) {
        return a * b;
    }

    @Override
    public int div(int a, int b) {
        if (b == 0){
            System.out.println("除数不能为0");
            return 0;
        }
        return  a / b;
    }
}

```

在测试类中测试该计算器代码

```java
/**
 * @author Chen Rui
 * @version 1.0
 **/
public class AppTest {
    
    @Test
    public void test() {
        Calculator target = new CalculatorImpl();
        int a = 10;
        int b = 10;
        System.out.println("res --> " + target.add(a, b));

        System.out.println("res --> " + target.mul(a, b));

        System.out.println("res --> " + target.sub(a, b));

        System.out.println("res --> " + target.div(a, b));
    }
}
```

此时控制台的输出结果为：

```
res --> 20
res --> 100
res --> 0
res --> 1
```

现在为该计算器增加**打印日志**的功能

创建一个计算器的代理类`CalculatorLoggingProxy`，在类中首先定义被代理的目标对象target，并通过构造函数进行赋值。

```java
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Date;

/**
 * 计算器代理类
 * 实现扩展打印日志功能
 *
 * @author Chen Rui
 * @version 1.0
 **/
public class CalculatorProxy {
    /**
     * 被代理的对象
     */
    private Calculator target;

    public CalculatorProxy(Calculator target) {
        this.target = target;
    }

    public Calculator createProxy(){
        Calculator proxy;

        ClassLoader classLoader = target.getClass().getClassLoader();

        Class[] interfaces = new Class[]{Calculator.class};

        InvocationHandler handler = new InvocationHandler() {
            /**
             * @param proxy     正在返回的代理对象，一般在invoke方法中都不使用该对象
             *                  如果使用该对象，则会引发栈内存溢出。因为会循环调用invoke方法。
             * @param method    正在被调用的方法
             * @param args      调用方式时传入的参数
             * @return
             * @throws Throwable
             */
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 获取方法名
                String methodName = method.getName();
                // 输出日志逻辑
                System.out.println(new Date() + ": The method " + methodName + " begins with " + Arrays.asList(args));
                // 执行方法
                Object result = method.invoke(target, args);
                // 输出日志逻辑
                System.out.println(new Date() + ": The method " + methodName + " ends with " + result);
                return result;
            }
        };

        proxy = (Calculator) Proxy.newProxyInstance(classLoader,interfaces,handler);

        return proxy;
    }
}

```

此时重新编写测试方法

```java
/**
 * @author Chen Rui
 * @version 1.0
 **/
public class AppTest {
    
    @Test
    public void test() {
        Calculator target = new CalculatorImpl();
        // 创建代理对象
        Calculator proxy = new CalculatorProxy(target).createProxy();
        int a = 10;
        int b = 10;
        System.out.println("res --> " + proxy.add(a, b));

        System.out.println("res --> " + proxy.mul(a, b));

        System.out.println("res --> " + proxy.sub(a, b));

        System.out.println("res --> " + proxy.div(a, b));
    }
}
```

到此就完成了在不改变`CalculatorImpl`类的源代码的情况下，**实现对计算器的功能增加，**实现了日志打印的功能。此时控制台的打印内容为

```
Sun Mar 17 20:36:26 CST 2019: The method add begins with [10, 10]
Sun Mar 17 20:36:26 CST 2019: The method add ends with 20
res --> 20
Sun Mar 17 20:36:26 CST 2019: The method mul begins with [10, 10]
Sun Mar 17 20:36:26 CST 2019: The method mul ends with 100
res --> 100
Sun Mar 17 20:36:26 CST 2019: The method sub begins with [10, 10]
Sun Mar 17 20:36:26 CST 2019: The method sub ends with 0
res --> 0
Sun Mar 17 20:36:26 CST 2019: The method div begins with [10, 10]
Sun Mar 17 20:36:26 CST 2019: The method div ends with 1
res --> 1
```



### Cglib动态代理

JDK动态代理必须要有接口, 但如果要代理一个没有接口的类该怎么办呢? 这时我们可以使用CGLIB动态代理. CGLIB动态代理的原理是生成目标类的子类, 这个子类对象就是代理对象, 代理对象是被增强过的.

注意: 不管有没有接口都可以使用CGLIB动态代理, 而不是只有在无接口的情况下才能使用.

#### Cglib动态代理案例

同样来实现一个对计算器来增加打印日志功能

首先创建计算器类`Calculator`

```java
/**
 * 计算器类
 *
 * @author Chen Rui
 * @version 1.0
 **/
public class Calculator {

    public int add(int a, int b) {
        return a + b;
    }

    public int sub(int a, int b) {
        return a - b;
    }

    public int mul(int a, int b) {
        return a * b;
    }

    public int div(int a, int b) {
        if (b == 0){
            System.out.println("除数不能为0");
            return 0;
        }
        return  a / b;
    }
}

```

此时需要导入cglib的jar包，在maven中添加依赖

```xml
<dependency>
    <groupId>cglib</groupId>
    <artifactId>cglib</artifactId>
    <version>2.2.2</version>
</dependency>
```

接着创建计算器的代理类`CalculatorProxy`并且实现`MethodInterceptor`接口并重写`intercept`方法。

在类中首先定义被代理的目标对象，并通过构造函数进行赋值。然后创建`createProxy()`方法返回被增强的计算器对象。

```java
/**
 * 计算器代理类
 *
 * @author Chen Rui
 * @version 1.0
 **/
public class CalculatorProxy implements MethodInterceptor {

    /**
     * 被代理的对象
     */
    private Calculator target;

    public CalculatorProxy(Calculator target) {
        this.target = target;
    }

    public Calculator createProxy(){

        // 1.创建cglib的核心类对象
        Enhancer enhancer = new Enhancer();

        // 2.设置父类
        enhancer.setSuperclass(target.getClass());

        // 3.设置回调（类似于jdk动态代理中的InvocationHandler对象）
        enhancer.setCallback(this);

        // 4.创建代理对象
        Calculator proxy = (Calculator) enhancer.create();

        return proxy;
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        // 获取方法名
        String methodName = method.getName();
        // 输出日志逻辑
        System.out.println(new Date() + ": The method " + methodName + " begins with " + Arrays.asList(args));
        // 执行方法
        Object result = methodProxy.invokeSuper(proxy, args);
        // 输出日志逻辑
        System.out.println(new Date() + ": The method " + methodName + " ends with " + result);
        return result;
    }
}

```



| 动态代理         | cglib                                                        | jdk                                                          |
| ---------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 是否提供子类代理 | 是                                                           | 否                                                           |
| 是否提供接口代理 | 是                                                           | 是                                                           |
| 区别             | 必须依赖于CGLib的类库，但是它需要类来实现任何接口代理的是指定的类生成一个子类，覆盖其中的方法 | 实现InvocationHandler，使用Proxy.newProxyInstance产生代理对象，被代理的对象必须要实现接口 |



**Spring如何选择是用JDK还是cglib？**

1、当bean实现接口时，会用JDK代理模式
2、当bean没有实现接口，用cglib实现
3、可以强制使用cglib（在spring配置中加入<aop:aspectj-autoproxy proxyt-target-class=”true”/>）

如图：

![image-20230417175542747](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230417175542747.png)



## Spring中的AOP实现——AspectJ

```xml
 <dependency>
<!--            这是AOP的底层依赖。-->
   <groupId>org.aspectj</groupId>
   <artifactId>aspectjweaver</artifactId>
   <version>1.9.7</version>
</dependency>
```

### AspectJ开发中的相关术语

```java
public class UserDao{
    public void save(){}
    
    public void query(){}
    
    public void update(){}
    
    public void delete(){}
}
```

- joinpoint(连接点) ： 可以被拦截到的点。save(), query(),update(),delete()方法都可以增强，这些方法就可以称为连接点。
- pointcut(切入点)：真正被拦截到的点。在实际开发中，可以只对save()方法进行增强，那么save()方法就是切入点。
- advice(增强)：方法层面的增强，现在可以对save()方法进行权限校验，权限校验(checkPri())的方法称为增强。
- introduction(引介)：类层面的增强。
- target(目标)：被增强的对象。
- weaving(织入)：将增强(advice)应用到目标(target)的过程
- proxy(代理)：代理对象，被增强以后的代理对象
- aspect(切面)：多个增强(advice)和多个切入点(pointcut)的组合

> ## AspectJ和Spring AOP的区别？
>
> 相信作为Java开发者我们都很熟悉Spring这个框架，在spring框架中有一个主要的功能就是AOP，提到AOP就往往会想到AspectJ，下面我对AspectJ和Spring AOP作一个简单的比较：
>
> ### Spring AOP
>
> 1、基于动态代理来实现，默认如果使用接口的，用JDK提供的动态代理实现，如果是方法则使用CGLIB实现
>
> 2、Spring AOP需要依赖IOC容器来管理，并且只能作用于Spring容器，使用纯Java代码实现
>
> 3、在性能上，由于Spring AOP是基于动态代理来实现的，在容器启动时需要生成代理实例，在方法调用上也会增加栈的深度，使得Spring AOP的性能不如AspectJ的那么好
>
> ### AspectJ
>
> - AspectJ来自于Eclipse基金会
> - AspectJ属于静态织入，通过修改代码来实现，有如下几个织入的时机：
>
>  1、编译期织入（Compile-time weaving）： 如类 A 使用 AspectJ 添加了一个属性，类 B 引用了它，这个场景就需要编译期的时候就进行织入，否则没法编译类 B。
>
>  2、编译后织入（Post-compile weaving）： 也就是已经生成了 .class 文件，或已经打成 jar 包了，这种情况我们需要增强处理的话，就要用到编译后织入。
>
>  3、类加载后织入（Load-time weaving）： 指的是在加载类的时候进行织入，要实现这个时期的织入，有几种常见的方法。1、自定义类加载器来干这个，这个应该是最容易想到的办法，在被织入类加载到 JVM 前去对它进行加载，这样就可以在加载的时候定义行为了。2、在 JVM 启动的时候指定 AspectJ 提供的 agent：`-javaagent:xxx/xxx/aspectjweaver.jar`。
>
> - AspectJ可以做Spring AOP干不了的事情，它是AOP编程的完全解决方案，Spring AOP则致力于解决企业级开发中最普遍的AOP（方法织入）。而不是成为像AspectJ一样的AOP方案
> - 因为AspectJ在实际运行之前就完成了织入，所以说它生成的类是没有额外运行时开销的

### AspectJ的XML配置案例1

目录：

```sh
.
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── DAO
│   │   │   │   └── UserDao.java
│   │   │   ├── Service
│   │   │   │   └── UserService.java
│   │   │   ├── SpringApplication.java
│   │   │   ├── aspect
│   │   │   │   └── MethodAspect.java
│   │   │   └── entity
│   │   │       ├── Apple.java
│   │   │       ├── Dog.java
│   │   │       └── People.java
│   │   └── resources
│   │       └── applicationContext.xml
│   └── test
│       └── java
│           └── testAnno.java
```



**设计MethodAspect**

```java
package aspect;

import org.aspectj.lang.JoinPoint;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class MethodAspect {
    public void printTime(JoinPoint jointPoint){
        //切面，拓展功能，jointPoint是连接点，目标类或者目标方法
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        String format = sdf.format(new Date());
//        System.out.println(format);
        String name = jointPoint.getTarget().getClass().getName();//获取目标类的名字
        String nameMethod = jointPoint.getSignature().getName();//获取目标的方法的名称
        System.out.println("-----> formatTime = " + format);
        System.out.println("nameMethod = " + nameMethod+" : "+name);

    }
}
```

**Dao和service**

```java
public class UserDao {

    public void insert() {
        System.out.println("UserDao插入");
    }
}
///////////////////////////////////
package Service;

import DAO.UserDao;

public class UserService {
    UserDao userDao;
    public void createUser(){
        System.out.println("执行创建逻辑～");
        userDao.insert();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}

```



**配置applicationContext.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop" xmlns:asp="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/aop https://www.springframework.org/schema/aop/spring-aop.xsd">
    <!-- applicationContext.xml 配置bean -->
<!--    尚敏啊的aop是一个切面编程-->
    <bean id="people" class="entity.People">
        <property name="name" value="红富士"/>
<!--        这是配置文件，不需要重新编译就可以生效，spring会自动实例，自动配置-->
    </bean>

    <bean id="dog" class="entity.Dog">
        <constructor-arg name="name" value="HAVAL"/>
        <constructor-arg name="length" value="5"/>
    </bean>
    <bean id="userDao" class="DAO.UserDao"></bean>
    <bean id="userService" class="Service.UserService">
        <property name="userDao" ref="userDao"></property>
    </bean>
    <bean id="methodAspect" class="aspect.MethodAspect"></bean>
    <asp:config>
<!--        这个是有aop提供的-->
        <aop:pointcut id="pointCut" expression="execution(* Service.UserService.createUser(..))"/>
<!--        定义切面类-->
        <aop:aspect ref="methodAspect">
            <aop:before method="printTime" pointcut-ref="pointCut"/>
        </aop:aspect>
    </asp:config>
</beans>
```



```java
import Service.UserService;
import entity.Dog;
import entity.People;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringApplication {
    public static void main(String[] args) {
        ApplicationContext context =
                new ClassPathXmlApplicationContext(
                        "classpath:applicationContext.xml");
//        测试AOP，
        UserService userService = context.getBean("userService", UserService.class);
        userService.createUser();
//        需求：create之前打印时间，AOP编程

    }
```

**测试结果：**

![截屏2023-04-17 15.35.43](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-04-17%2015.35.43.png)

### AspectJ的XML配置案例2

首先创建一个接口`ProductDao`，在里面定义添加商品，查询商品，修改商品，删除商品方法。

```java
/**
 * ProductDao
 *
 * @author Chen Rui
 * @version 1.0
 **/
public interface ProductDao {

    /**
     * 添加商品
     */
    void save();

    /**
     * 删除商品
     */
    void delete();

    /**
     * 修改商品
     */
    void modify();

    /**
     * 查询商品
     */
    void query();
}

```

接着创建一个类`ProductDaoImpl`来实现该接口

```java
/**
 * ProductDao的实现类
 *
 * @author Chen Rui
 * @version 1.0
 **/
public class ProductDaoImpl implements ProductDao {

    @Override
    public void save() {
        System.out.println("添加商品");
    }

    @Override
    public void delete() {
        System.out.println("删除商品");
    }
    
    @Override
    public void modify() {
        System.out.println("修改商品");
    }
    
    @Override
    public void query() {
        System.out.println("查询商品");
    }
    
}

```

现在目的就是给`save()`方法进行增强，使得在调用`save()`方法前进行权限校验。

要实现此功能，先创建一个**增强类**，或者叫**切面类**。里面编写要增强的功能，例如权限校验。

创建增强类`ProductEnhancer`

```java
/**
 * ProductDao的增强类(切面类)
 **/
public class ProductEnhancer {

    public void checkPri(){
        System.out.println("【前置增强】权限校验");
    }

}

```

然后创建配置文件`aspectj-xml.xml`来配置，该文件名此案例仅用于演示，实际开发中不要采取此名，依据实际需求编写。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/aop
    http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!-- 配置目标对象，即被增强的对象 -->
    <bean id="productDao" class="learningspring.aop.aspectj.xml.demo2.ProductDaoImpl"/>

    <!-- 将增强类(切面类)交给Spring管理 -->
    <bean id="productEnhancer" class="learningspring.aop.aspectj.xml.demo2.ProductEnhancer"/>
    
    <!-- 通过对AOP的配置完成对目标对象产生代理 -->
    <aop:config>
        <!-- 表达式配置哪些类的哪些方法需要进行增强 -->
        <!-- 对ProductDaoImpl类中的save方法进行增强 -->
        <!--
        “*” 表示任意返回值类型
        “..” 表示任意参数
        -->
        <aop:pointcut id="pointcut1" expression="execution(* learningspring.aop.aspectj.xml.demo2.ProductDaoImpl.save(..))"/>

        <!-- 配置切面 -->
        <aop:aspect ref="productEnhancer">
            <!-- 前置增强 -->
            <!-- 实现在调用save方法之前调用checkPri方法来进行权限校验-->
            <aop:before method="checkPri" pointcut-ref="pointcut1"/>
        </aop:aspect>
    </aop:config>
    
</beans>
```

至此切入点及切面都已配置完成，编写测试类和方法

```java
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * AspectJ的XML方式配置测试类
 *
 * @author Chen Rui
 * @version 1.0
 **/

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:aspectj-xml.xml")
public class AppTest {

    @Resource(name = "productDao")
    private ProductDao productDao;

    @Test
    public void test(){
        // 对save方法进行增强
        productDao.save();

        productDao.delete();
        
        productDao.modify();
        
        productDao.query();
    }
}

```

运行`test()`方法，控制台打印结果如下：

```
【前置增强】权限校验
添加商品
删除商品
修改商品
查询商品
```

至此就实现了在不修改`ProductDaoImpl`类的情况下，对其中的`save()`方法进行增强。

### JointPoint

JoinPoint对象封装了SpringAop中切面方法的信息,在切面方法中添加JoinPoint参数,就可以获取到封装了该方法信息的JoinPoint对象.

### 常用API

| 方法名                      | 功能                                                         |
| --------------------------- | ------------------------------------------------------------ |
| Signature `getSignature();` | 获取封装了署名信息的对象,在该对象中可以获取到目标方法名,所属类的Class等信息 |
| Object[] `getArgs();`       | 获取传入目标方法的参数对象                                   |
| Object `getTarget();`       | 获取被代理的对象，然后getClass就可以获得类                   |
| Object  `getThis();`        | 获取代理对象                                                 |

> ```java
> String name = jointPoint.getTarget().getClass().getName();//获取目标类的名字
> String nameMethod = jointPoint.getSignature().getName();//获取目标的方法的名称
> ```
>
> 

### Spring中常用的增强类型

按照增加在目标类方法连接点的位置可以将增强划分为以下五类：
前置增强   (org.springframework.aop.BeforeAdvice)   表示在目标方法执行前来实施增强
后置增强   (org.springframework.aop.AfterReturningAdvice)   表示在目标方法执行后来实施增强
环绕增强   (org.aopalliance.intercept.MethodInterceptor)   表示在目标方法执行前后同时实施增强
异常抛出增强   (org.springframework.aop.ThrowsAdvice)   表示在目标方法抛出异常后来实施增强
引介增强   (org.springframework.aop.introductioninterceptor)   表示在目标类中添加一些新的方法和属性

#### 前置通知-advice

在目标方法执行之前执行，可以获得切入点的信息

修改之前的`ProductEnhancer`类的`checkPri()`方法的参数。

```java
import org.aspectj.lang.JoinPoint;

public class ProductEnhancer {

    public void checkPri(JoinPoint joinPoint){
        System.out.println("【前置增强】权限校验" + joinPoint);
    }

}
```

执行测试方法，控制台输出

```
【前置增强】权限校验execution(void learningspring.aop.aspectj.xml.demo2.ProductDao.save())
添加商品
删除商品
修改商品
查询商品
```

#### 后置通知

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/aop
    http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!-- 配置目标对象，即被增强的对象 -->
    <bean id="productDao" class="learningspring.aop.aspectj.xml.demo2.ProductDaoImpl"/>

    <!-- 将增强类(切面类)交给Spring管理 -->
    <bean id="productEnhancer" class="learningspring.aop.aspectj.xml.demo2.ProductEnhancer"/>
    
    <!-- 通过对AOP的配置完成对目标对象产生代理 -->
    <aop:config>
        <!-- 表达式配置哪些类的哪些方法需要进行增强 -->
        <!-- 对ProductDaoImpl类中的save方法进行增强 -->
        <!--
        “*” 表示任意返回值类型
        “..” 表示任意参数
        -->
        <!-- 后置通知切入点配置 -->
        <aop:pointcut id="pointcut1" expression="execution(* learningspring.aop.aspectj.xml.demo2.ProductDaoImpl.save(..))"/>

        <!-- 配置切面 -->
        <aop:aspect ref="productEnhancer">
            <!-- 后置通知 -->
            <!-- 实现在调用save方法之后调用checkPri方法来进行权限校验-->
            <aop:after method="checkPri" pointcut-ref="pointcut1"/>
        
        </aop:aspect>
    </aop:config>

</beans>
```



#### 后置增强-返回后通知，就会将返回的值得到

在目标方法执行之后执行，可以获得方法的返回值

首先修改`ProductDao`中的`delete()`方法的返回值类型，改成String

```java

public interface ProductDao {


    void save();
    String delete();
    void modify();
    void query();
}

```

再修改`ProductDaoImpl`中的`delete()`方法

```java
/**
 * ProductDao的实现类
 *
 * @author Chen Rui
 * @version 1.0
 **/
public class ProductDaoImpl implements ProductDao {

    @Override
    public void save() {
        System.out.println("添加商品");
    }

    @Override
    public String delete() {
        System.out.println("删除商品");
        return new Date().toString();
    }

    @Override
    public void modify() {
        System.out.println("修改商品");
    }

    @Override
    public void query() {
        System.out.println("查询商品");
    }
}

```

修改`ProductEnhancer`类，添加`writeLog()`方法，实现写日志功能

```java
/**
 * ProductDao的增强类(切面类)
 *
 * @author Chen Rui
 * @version 1.0
 **/
public class ProductEnhancer {

    /**
     * 前置增强案例
     * 在调用save方法之前进行权限校验
     * @param joinPoint 切入点对象
     */
    public void checkPri(JoinPoint joinPoint){
        System.out.println("【前置增强】权限校验" + joinPoint);
    }

    /**
     * 后置增强案例
     * 在调用delete方法之后，写入日志记录操作时间
     * @param result 目标方法返回的对象
     */
    public void writeLog(Object result){
        System.out.println("【后置增强】写入日志 操作时间：" + result.toString());
    }
}
```

然后修改`aspectj.xml`配置文件，配置新的**切入点**和**切面**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/aop
    http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!-- 配置目标对象，即被增强的对象 -->
    <bean id="productDao" class="learningspring.aop.aspectj.xml.demo2.ProductDaoImpl"/>

    <!-- 将增强类(切面类)交给Spring管理 -->
    <bean id="productEnhancer" class="learningspring.aop.aspectj.xml.demo2.ProductEnhancer"/>
    
    <!-- 通过对AOP的配置完成对目标对象产生代理 -->
    <aop:config>
        <!-- 表达式配置哪些类的哪些方法需要进行增强 -->
        <!-- 对ProductDaoImpl类中的save方法进行增强 -->
        <!--
        “*” 表示任意返回值类型
        “..” 表示任意参数
        -->
        <!-- 前置增强的切入点配置 -->
        <aop:pointcut id="pointcut1" expression="execution(* learningspring.aop.aspectj.xml.demo2.ProductDaoImpl.save(..))"/>
        
        <!-- 后置增强的切入点配置 -->
        <aop:pointcut id="pointcut2" expression="execution(* learningspring.aop.aspectj.xml.demo2.ProductDaoImpl.delete(..))"/>

        <!-- 配置切面 -->
        <aop:aspect ref="productEnhancer">
            <!-- 前置增强 -->
            <!-- 实现在调用save方法之前调用checkPri方法来进行权限校验-->
            <aop:before method="checkPri" pointcut-ref="pointcut1"/>
            
            <!-- 后置增强 -->
            <!-- returning里面的值必须和writeLog()方法里的参数名相同，本案例为result-->
            <aop:after-returning method="writeLog" returning="result" pointcut-ref="pointcut2"/>
        </aop:aspect>
    </aop:config>

</beans>
```

执行测试方法，控制台打印结果

```
【前置增强】权限校验execution(void learningspring.aop.aspectj.xml.demo2.ProductDao.save())
添加商品
删除商品
【后置增强】写入日志 操作时间：Tue Mar 19 15:59:48 CST 2019
修改商品
查询商品
```

#### 环绕增强

在目标方法执行之前和之后都执行

利用环绕增强来实现在调用`modify()`方法前后进行性能监控

首先修改`ProductEnhancer`类，添加`monitor()`方法

```java
/**
 * ProductDao的增强类(切面类)
 *
 * @author Chen Rui
 * @version 1.0
 **/
public class ProductEnhancer {

    /**
     * 前置增强案例
     * 在调用save方法之前进行权限校验
     * @param joinPoint 切入点对象
     */
    public void checkPri(JoinPoint joinPoint){
        System.out.println("【前置增强】权限校验" + joinPoint);
    }

    /**
     * 后置增强案例
     * 在调用delete方法之后，写入日志记录操作时间
     * @param result 目标方法返回的对象
     */
    public void writeLog(Object result){
        System.out.println("【后置增强】写入日志 操作时间：" + result.toString());
    }

    /**
     * 环绕增强
     * 在调用modify方法前后，显示性能参数
     * @param joinPoint 切入点对象
     * @throws Throwable 可抛出的异常
     */
    public Object monitor(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("【环绕增强】当前空闲内存" + Runtime.getRuntime().freeMemory()/(1024 * 1024) + "MB");
        Object obj = joinPoint.proceed();
        System.out.println("【环绕增强】当前空闲内存" + Runtime.getRuntime().freeMemory()/(1024 * 1024) + "MB");
        return obj;
    }
}

```

然后再修改`aspectj.xml`配置文件，添加新的**切入点**和**切面**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/aop
    http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!-- 配置目标对象，即被增强的对象 -->
    <bean id="productDao" class="learningspring.aop.aspectj.xml.demo2.ProductDaoImpl"/>

    <!-- 将增强类(切面类)交给Spring管理 -->
    <bean id="productEnhancer" class="learningspring.aop.aspectj.xml.demo2.ProductEnhancer"/>
    
    <!-- 通过对AOP的配置完成对目标对象产生代理 -->
    <aop:config>
        <!-- 表达式配置哪些类的哪些方法需要进行增强 -->
        <!-- 对ProductDaoImpl类中的save方法进行增强 -->
        <!--
        “*” 表示任意返回值类型
        “..” 表示任意参数
        -->
        <!-- 前置增强的切入点配置 -->
        <aop:pointcut id="pointcut1" expression="execution(* learningspring.aop.aspectj.xml.demo2.ProductDaoImpl.save(..))"/>

        <!-- 后置增强的切入点配置 -->
        <aop:pointcut id="pointcut2" expression="execution(* learningspring.aop.aspectj.xml.demo2.ProductDaoImpl.delete(..))"/>

        <!-- 环绕增强的切入点配置 -->
        <aop:pointcut id="pointcut3" expression="execution(* learningspring.aop.aspectj.xml.demo2.ProductDaoImpl.modify(..))"/>

        <!-- 配置切面 -->
        <aop:aspect ref="productEnhancer">
            <!-- 前置增强 -->
            <!-- 实现在调用save方法之前调用checkPri方法来进行权限校验-->
            <aop:before method="checkPri" pointcut-ref="pointcut1"/>

            <!-- 后置增强 -->
            <!-- returning里面的值必须和writeLog()方法里的参数名相同，本案例为result-->
            <aop:after-returning method="writeLog" returning="result" pointcut-ref="pointcut2"/>

            <!-- 环绕增强 -->
            <aop:around method="monitor" pointcut-ref="pointcut3"/>

        </aop:aspect>
    </aop:config>

</beans>
```

运行测试方法，控制台打印结果：

```
【前置增强】权限校验execution(void learningspring.aop.aspectj.xml.demo2.ProductDao.save())
添加商品
删除商品
【后置增强】写入日志 操作时间：Tue Mar 19 15:58:49 CST 2019
【环绕增强】当前空闲内存185MB
修改商品
【环绕增强】当前空闲内存185MB
查询商品
```

#### 异常抛出增强

在程序出现异常时执行

利用异常抛出增强来实现获取异常信息的功能

首先修改`ProductDaoImpl`中的`query()`方法，使该方法抛出异常

```java
/**
 * ProductDao的实现类
 *
 * @author Chen Rui
 * @version 1.0
 **/
public class ProductDaoImpl implements ProductDao {

    @Override
    public void save() {
        System.out.println("添加商品");
    }

    @Override
    public void query() {
        System.out.println("查询商品");
        int a = 1/0;
    }

    @Override
    public void modify() {
        System.out.println("修改商品");
    }

    @Override
    public String delete() {
        System.out.println("删除商品");
        return new Date().toString();
    }
}
```

接着修改`ProductEnhancer`类，添加`exception()`方法

```java
/**
 * ProductDao的增强类(切面类)
 *
 * @author Chen Rui
 * @version 1.0
 **/
public class ProductEnhancer {

    /**
     * 前置增强案例
     * 在调用save方法之前进行权限校验
     * @param joinPoint 切入点对象
     */
    public void checkPri(JoinPoint joinPoint){
        System.out.println("【前置增强】权限校验" + joinPoint);
    }

    /**
     * 后置增强案例
     * 在调用delete方法之后，写入日志记录操作时间
     * @param result 目标方法返回的对象
     */
    public void writeLog(Object result){
        System.out.println("【后置增强】写入日志 操作时间：" + result.toString());
    }

    /**
     * 环绕增强
     * 在调用modify方法前后，显示性能参数
     * @param joinPoint 切入点对象
     * @throws Throwable 可抛出的异常
     */
    public Object monitor(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("【环绕增强】当前空闲内存" + Runtime.getRuntime().freeMemory()/(1024 * 1024) + "MB");
        Object obj = joinPoint.proceed();
        System.out.println("【环绕增强】当前空闲内存" + Runtime.getRuntime().freeMemory()/(1024 * 1024) + "MB");
        return obj;
    }

    /**
     * 异常抛出增强
     * 在调用query时若抛出异常则打印异常信息
     * @param ex 异常对象
     */
    public void exception(Throwable ex){
        System.out.println("【异常抛出增强】" + "异常信息：" +ex.getMessage());
    }
}

```

然后再修改`aspectj-xml.xml`配置文件，添加新的**切入点**和**切面**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/aop
    http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!-- 配置目标对象，即被增强的对象 -->
    <bean id="productDao" class="learningspring.aop.aspectj.xml.demo2.ProductDaoImpl"/>

    <!-- 将增强类(切面类)交给Spring管理 -->
    <bean id="productEnhancer" class="learningspring.aop.aspectj.xml.demo2.ProductEnhancer"/>
    
    <!-- 通过对AOP的配置完成对目标对象产生代理 -->
    <aop:config>
        <!-- 表达式配置哪些类的哪些方法需要进行增强 -->
        <!-- 对ProductDaoImpl类中的save方法进行增强 -->
        <!--
        “*” 表示任意返回值类型
        “..” 表示任意参数
        -->
        <!-- 前置增强的切入点配置 -->
        <aop:pointcut id="pointcut1" expression="execution(* learningspring.aop.aspectj.xml.demo2.ProductDaoImpl.save(..))"/>

        <!-- 后置增强的切入点配置 -->
        <aop:pointcut id="pointcut2" expression="execution(* learningspring.aop.aspectj.xml.demo2.ProductDaoImpl.delete(..))"/>

        <!-- 环绕增强的切入点配置 -->
        <aop:pointcut id="pointcut3" expression="execution(* learningspring.aop.aspectj.xml.demo2.ProductDaoImpl.modify(..))"/>

        <!-- 异常抛出增强的切入点配置 -->
        <aop:pointcut id="pointcut4" expression="execution(* learningspring.aop.aspectj.xml.demo2.ProductDaoImpl.query(..))"/>

        <!-- 配置切面 -->
        <aop:aspect ref="productEnhancer">
            <!-- 前置增强 -->
            <!-- 实现在调用save方法之前调用checkPri方法来进行权限校验-->
            <aop:before method="checkPri" pointcut-ref="pointcut1"/>

            <!-- 后置增强 -->
            <!-- returning里面的值必须和writeLog()方法里的参数名相同，本案例为result-->
            <aop:after-returning method="writeLog" returning="result" pointcut-ref="pointcut2"/>

            <!-- 环绕增强 -->
            <aop:around method="monitor" pointcut-ref="pointcut3"/>

            <!-- 异常抛出增强 -->
            <aop:after-throwing method="exception" throwing="ex" pointcut-ref="pointcut4"/>
        </aop:aspect>
    </aop:config>

</beans>
```

最后执行测试方法，控制台输出结果：

```
【前置增强】权限校验execution(void learningspring.aop.aspectj.xml.demo2.ProductDao.save())
添加商品
删除商品
【后置增强】写入日志 操作时间：Tue Mar 19 15:58:16 CST 2019
【环绕增强】当前空闲内存183MB
修改商品
【环绕增强】当前空闲内存183MB
查询商品
【异常抛出增强】异常信息：/ by zero
```

#### 最终增强

无论代码是否有异常最终都会执行

继续在异常抛出增强的代码修改，实现无论是否抛出异常都会打印当前时间信息

首先修改`ProductEnhancer`类，添加`finallyAdvice()`方法

```java
/**
 * ProductDao的增强类(切面类)
 *
 * @author Chen Rui
 * @version 1.0
 **/
public class ProductEnhancer {

    /**
     * 前置增强案例
     * 在调用save方法之前进行权限校验
     * @param joinPoint 切入点对象
     */
    public void checkPri(JoinPoint joinPoint){
        System.out.println("【前置增强】权限校验" + joinPoint);
    }

    /**
     * 后置增强案例
     * 在调用delete方法之后，写入日志记录操作时间
     * @param result 目标方法返回的对象
     */
    public void writeLog(Object result){
        System.out.println("【后置增强】写入日志 操作时间：" + result.toString());
    }

    /**
     * 环绕增强
     * 在调用modify方法前后，显示性能参数
     * @param joinPoint 切入点对象
     * @throws Throwable 可抛出的异常
     */
    public Object monitor(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("【环绕增强】当前空闲内存" + Runtime.getRuntime().freeMemory()/(1024 * 1024) + "MB");
        Object obj = joinPoint.proceed();
        System.out.println("【环绕增强】当前空闲内存" + Runtime.getRuntime().freeMemory()/(1024 * 1024) + "MB");
        return obj;
    }

    /**
     * 异常抛出增强
     * 在调用query时若抛出异常则打印异常信息
     * @param ex 异常对象
     */
    public void exception(Throwable ex){
        System.out.println("【异常抛出增强】" + "异常信息：" +ex.getMessage());
    }

    /**
     * 最终增强
     * 无论query方法是否抛出异常都打印当前时间
     */
    public void finallyAdvice(){
        System.out.println("【最终增强】" + new Date().toString());
    }
}

```

修改`aspectj.xml`配置文件，添加新的**切面**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/aop
    http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!-- 配置目标对象，即被增强的对象 -->
    <bean id="productDao" class="learningspring.aop.aspectj.xml.demo2.ProductDaoImpl"/>

    <!-- 将增强类(切面类)交给Spring管理 -->
    <bean id="productEnhancer" class="learningspring.aop.aspectj.xml.demo2.ProductEnhancer"/>
    
    <!-- 通过对AOP的配置完成对目标对象产生代理 -->
    <aop:config>
        <!-- 表达式配置哪些类的哪些方法需要进行增强 -->
        <!-- 对ProductDaoImpl类中的save方法进行增强 -->
        <!--
        “*” 表示任意返回值类型
        “..” 表示任意参数
        -->
        <!-- 前置增强的切入点配置 -->
        <aop:pointcut id="pointcut1" expression="execution(* learningspring.aop.aspectj.xml.demo2.ProductDaoImpl.save(..))"/>

        <!-- 后置增强的切入点配置 -->
        <aop:pointcut id="pointcut2" expression="execution(* learningspring.aop.aspectj.xml.demo2.ProductDaoImpl.delete(..))"/>

        <!-- 环绕增强的切入点配置 -->
        <aop:pointcut id="pointcut3" expression="execution(* learningspring.aop.aspectj.xml.demo2.ProductDaoImpl.modify(..))"/>

        <!-- 异常抛出增强的切入点配置 -->
        <aop:pointcut id="pointcut4" expression="execution(* learningspring.aop.aspectj.xml.demo2.ProductDaoImpl.query(..))"/>

        <!-- 配置切面 -->
        <aop:aspect ref="productEnhancer">
            <!-- 前置增强 -->
            <!-- 实现在调用save方法之前调用checkPri方法来进行权限校验-->
            <aop:before method="checkPri" pointcut-ref="pointcut1"/>

            <!-- 后置增强 -->
            <!-- returning里面的值必须和writeLog()方法里的参数名相同，本案例为result-->
            <aop:after-returning method="writeLog" returning="result" pointcut-ref="pointcut2"/>

            <!-- 环绕增强 -->
            <aop:around method="monitor" pointcut-ref="pointcut3"/>

            <!-- 异常抛出增强 -->
            <aop:after-throwing method="exception" throwing="ex" pointcut-ref="pointcut4"/>

            <!-- 最终增强 -->
            <aop:after method="finallyAdvice" pointcut-ref="pointcut4"/>
        </aop:aspect>
    </aop:config>

</beans>
```

最后运行测试代码，控制台输出结果：

```
【前置增强】权限校验execution(void learningspring.aop.aspectj.xml.demo2.ProductDao.save())
添加商品
删除商品
【后置增强】写入日志 操作时间：Tue Mar 19 15:57:01 CST 2019
【环绕增强】当前空闲内存183MB
修改商品
【环绕增强】当前空闲内存183MB
查询商品
【最终增强】Tue Mar 19 15:57:01 CST 2019
【异常抛出增强】异常信息：/ by zero
```

### AOP切入点表达式语法 expression

AOP切入点表达式是基于execution的函数完成的

语法：**[访问修饰符] 方法返回值 包名.类名.方法名(参数)**

“*” 表示任意返回值类型
“..” 表示任意参数

+ `public void learningspring.aop.aspectj.xml.demo2.ProductDaoImpl.save(..) `：具体到某个增强的方法

+ `* *.*.*.*Dao.save(..) `：所有包下的所有以Dao结尾的类中的save方法都会被增强

+ `* learningspring.aop.aspectj.xml.demo2.ProductDaoImpl+.save(..) `：ProductDaoImpl及其子类的save方法都会被增强

+ `* learningspring.aop.aspectj.xml..*.*(..)`：xml包及其子包的所有类的方法都会被增强

  举例

```sh
任意公共方法的执行：
execution(public * *(..))
任何一个以“set”开始的方法的执行：
execution(* set*(..))
AccountService 接口的任意方法的执行：
execution(* com.xyz.service.AccountService.*(..))
定义在service包里的任意方法的执行：
execution(* com.xyz.service.*.*(..))
定义在service包和所有子包里的任意类的任意方法的执行：
execution(* com.xyz.service..*.*(..))
定义在pointcutexp包和所有子包里的JoinPointObjP2类的任意方法的执行：
execution(* com.test.spring.aop.pointcutexp..JoinPointObjP2.*(..))")
***> 最靠近(..)的为方法名,靠近.*(..))的为类名或者接口名,如上例的JoinPointObjP2.*(..))

pointcutexp包里的任意类.
within(com.test.spring.aop.pointcutexp.*)
pointcutexp包和所有子包里的任意类.
within(com.test.spring.aop.pointcutexp..*)
实现了MyInterface接口的所有类,如果MyInterface不是接口,限定MyInterface单个类.
this(com.test.spring.aop.pointcutexp.MyInterface)
***> 当一个实现了接口的类被AOP的时候,用getBean方法必须cast为接口类型,不能为该类的类型.

带有@MyTypeAnnotation标注的所有类的任意方法.
@within(com.elong.annotation.MyTypeAnnotation)
@target(com.elong.annotation.MyTypeAnnotation)
带有@MyTypeAnnotation标注的任意方法.
@annotation(com.elong.annotation.MyTypeAnnotation)
***> @within和@target针对类的注解,@annotation是针对方法的注解

参数带有@MyMethodAnnotation标注的方法.
@args(com.elong.annotation.MyMethodAnnotation)
参数为String类型(运行是决定)的方法.
args(String)
```



### AspectJ的注解开发案例

```sh
.
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── SpringApplication.java
│   │   │   └── mcxgroup
│   │   │       ├── DAO
│   │   │       │   └── UserDao.java
│   │   │       ├── Service
│   │   │       │   └── UserService.java
│   │   │       ├── aspect
│   │   │            └── MethodAspect.java
│   │   └── resources
│   │       └── applicationContext.xml
│   └── test
│       └── java
│           └── testAnno.java
```



xml中配置组件扫描：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:asp="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/aop https://www.springframework.org/schema/aop/spring-aop.xsd
http://www.springframework.org/schema/context  http://www.springframework.org/schema/context/spring-context.xsd">
<!--    扫描组建-->
    <context:component-scan base-package="mcxgroup.*"/>
<!--    启动spring aop注解模式-->
    <aop:aspectj-autoproxy/>
</beans>
```



首先也是创建一个接口`userDao`

```java
package mcxgroup.DAO;

import org.springframework.stereotype.Repository;

@Repository("userDao")

public class UserDao {

    public void insert() {
        System.out.println("UserDao插入");
    }
}

```

然后创建一个Dao实现类`Service`

```java
package mcxgroup.Service;

import mcxgroup.DAO.UserDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("userService")
public class UserService {
    @Resource
    UserDao userDao;
    public void createUser(){
        System.out.println("执行创建逻辑～");
        userDao.insert();
    }
    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}

```

接着创建**增强类**`MethodAspect`，在该类里面使用注解

```java
package mcxgroup.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
@Component //标记为组件。
@Aspect //标记为切面类

public class MethodAspect {
//    环绕通知
    @Before("execution(* mcxgroup.Service.UserService.createUser(..))")
    public void printTime(JoinPoint jointPoint){
        //切面，拓展功能，jointPoint是连接点，目标类或者目标方法
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        String format = sdf.format(new Date());
//        System.out.println(format);
        String name = jointPoint.getTarget().getClass().getName();//获取目标类的名字
        String nameMethod = jointPoint.getSignature().getName();//获取目标的方法的名称
        System.out.print("-----> formatTime = " + format);
        System.out.println(" nameMethod = " + nameMethod+" : "+name);
    }
}
```



`@Before`：前置增强

`@AfterReturning`：后置增强，其中的returning的值必须和方法传入的参数名相同

`@Around`：环绕增强

`@AfterThrowing`：异常抛出增强，其中的throwing的值必须和方法传入的参数名相同

`@After`：最终增强



```java

@Aspect
public class ProductEnhancer {

    /**
     * 切入点配置
     * 对ProductDaoImpl里的方法都增强
     */
    @Pointcut(value = "execution(* learningspring.aop.aspectj.annotation.demo2.ProductDaoImpl.*(..))")
    private void pointcut1(){}

    /**
     * 前置增强案例
     * 在调用save方法之前进行权限校验
     * @param joinPoint 切入点对象
     */
    @Before(value = "execution(* learningspring.aop.aspectj.annotation.demo2.ProductDaoImpl.save())")
    public void checkPri(JoinPoint joinPoint){
        System.out.println("【前置增强】权限校验" + joinPoint);
    }

    /**
     * 后置增强案例
     * 在调用delete方法之后，写入日志记录操作时间
     * @param result 目标方法返回的对象
     */
    @AfterReturning(returning = "result", value = "execution(* learningspring.aop.aspectj.annotation.demo2.ProductDaoImpl.delete())")
    public void writeLog(Object result){
        System.out.println("【后置增强】写入日志 操作时间：" + result.toString());
    }

    /**
     * 环绕增强
     * 在调用modify方法前后，显示性能参数
     * @param joinPoint 切入点对象
     * @throws Throwable 可抛出的异常
     */
    @Around(value = "execution(* learningspring.aop.aspectj.annotation.demo2.ProductDaoImpl.modify())")
    public Object monitor(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("【环绕增强】当前空闲内存" + Runtime.getRuntime().freeMemory()/(1024 * 1024) + "MB");
        Object obj = joinPoint.proceed();
        System.out.println("【环绕增强】当前空闲内存" + Runtime.getRuntime().freeMemory()/(1024 * 1024) + "MB");
        return obj;
    }

    /**
     * 异常抛出增强
     * 在调用query时若抛出异常则打印异常信息
     * @param ex 异常对象
     */
    @AfterThrowing(throwing = "ex", value = "execution(* learningspring.aop.aspectj.annotation.demo2.ProductDaoImpl.query())")
    public void exception(Throwable ex){
        System.out.println("【异常抛出增强】" + "异常信息：" +ex.getMessage());
    }

    /**
     * 最终增强
     * 无论ProductDaoImpl里的每个方法是否抛出异常都打印当前时间
     */
    @After(value = "pointcut1()")
    public void finallyAdvice(){
        System.out.println("【最终增强】" + new Date().toString());
    }
}

```

编写测试方法

```java
import mcxgroup.Service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringApplication {
    public static void main(String[] args) {
        ApplicationContext context =
                new ClassPathXmlApplicationContext(
                        "classpath:applicationContext.xml");
//        测试AOP，
        UserService userService = context.getBean("userService", UserService.class);
        userService.createUser();
//        需求：create之前打印时间，AOP编程
    }
}

```

运行，控制台输出

```
-----> formatTime = 2023-04-17 16:57:32 087 nameMethod = createUser : mcxgroup.Service.UserService
执行创建逻辑～
UserDao插入
```





# Spring JDBC Template

类似于Apache的DBUtils，简化了JDBC API，简化开发工作量

Spring提供了提供了多种持久层技术的模板类

| ORM持久化技术   | 模板类                                               |
| --------------- | ---------------------------------------------------- |
| JDBC            | org.springframework.jdbc.core.JdbcTemplate           |
| Hibernate3.0    | org.springframework.orm.hibernate3.HibernateTemplate |
| IBatis(Mybatis) | org.springframework.orm.ibatis.SqlMapClientTemplate  |
| JPA             | org.springframework.orm.jpa.JpaTemplate              |

- 学习配置JDBC
- 学习JDBC Template

> 封装程度较低，简单封装，性能较高。大厂使用Spring JDBC再二次封装，Spring JDBC是介于二者之间

## JDBC实例配置

- **Maven依赖**

- applicationContext.xml配置DataSource数据源

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:asp="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/aop https://www.springframework.org/schema/aop/spring-aop.xsd
http://www.springframework.org/schema/context  http://www.springframework.org/schema/context/spring-context.xsd">
<!--    扫描组建-->
    <context:component-scan base-package="mcxgroup.*"/>
<!--    启动spring aop注解模式-->
    <aop:aspectj-autoproxy/>
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
<!--        当前数据源的设置的参数-->
        <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"></property>
        <property name="url" value="jdbc:mysql:///mybatis?useSSL=true&amp;userUnicode=true&amp;characterEncoding=UTF-8"></property>
        <property name="username" value="root"/>
        <property name="password" value="Mysql2486"/>
    </bean>
    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
<!--        首要核心-->
        <property name="dataSource" ref="dataSource"></property>
    </bean>
</beans>
```

- **Dao注入JdbcTemplate**

```java
package mcxgroup.DAO;

import mcxgroup.entity.Employee;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public class EmployeeDao {
//    导入jdbc

    private JdbcTemplate jdbcTemplate;

    public Employee findById(Integer eno){
        String sql = "select * from employee where eno = ?";
        //查询单条数据
        //这里就是BeanPropertyRowMapper完成数据库数据对实体对象的转化。
        Employee employee = jdbcTemplate.queryForObject(sql, new Object[]{eno}, new BeanPropertyRowMapper<Employee>(Employee.class));
        return employee;
    }


    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
```



JDBC初步测试：

```java
public class SpringApplication {
    public static void main(String[] args) {
        ApplicationContext context =
                new ClassPathXmlApplicationContext(
                        "classpath:applicationContext.xml");
//        测试JDBC Tempkate
        EmployeeDao dao = context.getBean("employeeDao", EmployeeDao.class);
        Employee employee = dao.findById(3308);
        System.out.println("employee = " + employee);

    }
}
```

## JDBC Template 单元测试准备

#### RunWith

```java
//testJDBCTemplate.java

import mcxgroup.DAO.EmployeeDao;
import mcxgroup.entity.Employee;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
//这里是把JUNIT的控制权交给Spring，自动出实话
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
//load configureationo
public class testJDBCTemplate {
    @Resource
    private EmployeeDao employeeDao;
    @Test
    public void testFindById(){
        Employee daoById = employeeDao.findById(3308);
        System.out.println("daoById = " + daoById);
    }
}
```

> 前提：开启组件扫描+导入spring-test jar包，导入版本需要匹配。

## JDBC Template的入门

首先引入jar包，在`pom.xml`文件中加入`spring-jdbc`，`spring-tx`，`mysql-connector-java`（本案例使用的是MySQL8）三个依赖。

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>4.3.14.RELEASE</version>
</dependency>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
    <version>4.3.14.RELEASE</version>
</dependency>

<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.15</version>
</dependency>
```

然后创建数据库表，本例使用的MySQL8

```mysql
create table account
(
	id int auto_increment
		primary key,
	name varchar(8) not null,
	money double default 0
)
comment '账户表';
```

### 基本使用

最基本的使用，不依赖于Spring 的管理，手动创建对象，采用硬编码的方式进行属性注入。不推荐使用该方法。

```java
public class AppTest {
    /**
     * 硬编码
     */
    @Test
    public void test1(){
        // 创建连接池
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/springjdbc?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false ");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        // 创建JDBC Template
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int i = jdbcTemplate.update("INSERT INTO account VALUES (null ,?,?)", "Tom", 20000d);
        if (i > 0){
            System.out.println("Update Successful");
        }
    }
}
```

接下来使用第二种方法，把连接池对象和模板(Template)都交给Spring来管理

创建`spring-jdbc.xml`该文件用来管理Bean

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- 配置数据库连接池 -->
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/springjdbc?useUnicode=true&amp;characterEncoding=utf-8&amp;serverTimezone=Asia/Shanghai&amp;useSSL=false"/>
        <property name="username" value="root"/>
        <property name="password" value="123456"/>
    </bean>

    <!-- 配置Spring JDBC Template -->
    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dataSource"/>
    </bean>

</beans>
```

在测试类中加入相应的注解，以及配置文件信息，编写新的测试方法

```java
/**
 * Spring JDBC Template的使用
 *
 * @author Chen Rui
 * @version 1.0
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-jdbc.xml")
public class AppTest {
    /**
     * Spring 配置文件方式
     * 把连接池和模板(Template)都交给spring管理
     * 日志信息：Loaded JDBC driver: com.mysql.cj.jdbc.Driver
     * 是使用的默认的连接池
     */
    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Test
    public void test2(){
        int i = jdbcTemplate.update("INSERT INTO account VALUES (null ,?,?)", "Jack", 30000d);
        if (i > 0){
            System.out.println("Update Successful");
        }
    }
```

通过`@Resource`注解从IOC容器中获取到模板对象，然后通过该模板对象来操作数据库。

这样就完成了Spring JDBC Template的最基本使用

### 数据库连接池

在实际开发中，可能并不会使用默认的连接池，而是去使用一些开源的数据库连接池，在该例中介绍两种数据库连接池DBCP和C3P0

#### DBCP连接池的配置

首先创建连接数据库的配置文件`db.properties`，注意，不同的MySQL版本可能url信息会不同，比如MySQL8就需要添加`serverTimezone`参数。

```properties
jdbc.driverClassName=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/springjdbc?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false 
jdbc.username=root
jdbc.password=123456
```

接着创建一个新的配置文件`spring-dbcp.xml`和前面的配置文件做区分。

利用`context:property-placeholder`标签引入`db.properties`配置文件，通过`${key}`的方式来获取对应的value。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 引入数据库配置文件 -->
    <context:property-placeholder location="db.properties"/>

    <!-- 配置DBCP连接池 -->
    <bean id="dataSource" class="org.apache.commons.dbcp2.BasicDataSource">
        <property name="driverClassName" value="${jdbc.driverClassName}"/>
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>

    <!-- 配置Spring JDBC Template -->
    <bean id="jdbcTemplateDBCP" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dataSource"/>
    </bean>
</beans>
```

编写测试方法

```java
/**
 * Spring JDBC Template的使用
 *
 * @author Chen Rui
 * @version 1.0
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-dbcp.xml")
public class AppTest {
    //使用开源的数据库连接池进行配置

    /**
     * 使用DBCP连接池
     */
    @Resource(name = "jdbcTemplateDBCP")
    private JdbcTemplate jdbcTemplateDBCP;

    @Test
    public void test3(){
        int i = jdbcTemplateDBCP.update("INSERT INTO account VALUES (null ,?,?)", "Lucy", 40000d);
        if (i > 0){
            System.out.println("Update Successful");
        }
    }
}
```

#### C3P0连接池配置

同样是创建一个新的配置文件`spring-c3p0.xml`，以作区分，同时也要引入数据库配置文件`db.properties`

要注意`property`标签的`name`属性和前面的配置文件**稍有不同**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans.xsd
	http://www.springframework.org/schema/context
	http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 引入数据库配置文件 -->
    <context:property-placeholder location="db.properties"/>

    <!-- 配置C3P0连接池 -->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="${jdbc.driverClassName}"/>
        <property name="jdbcUrl" value="${jdbc.url}"/>
        <property name="user" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>

    <!-- 配置Spring JDBC Template -->
    <bean id="jdbcTemplateC3P0" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dataSource"/>
    </bean>
</beans>
```

编写测试方法

```java
/**
 * Spring JDBC Template的使用
 *
 * @author Chen Rui
 * @version 1.0
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-*.xml")
public class AppTest {
    //使用开源的数据库连接池进行配置

    /**
     * 使用C3P0连接池
     */
    @Resource(name = "jdbcTemplateC3P0")
    private JdbcTemplate jdbcTemplateC3P0;

    @Test
    public void test4(){
        int i = jdbcTemplateC3P0.update("INSERT INTO account VALUES (null ,?,?)", "Lily", 50000d);
        if (i > 0){
            System.out.println("Update Successful");
        }
    }
}

```

### C3P0完成基本的CRUD操作

以下内容都是使用的**C3P0连接池**，并且通过`@Resource`注解从IOC容器中获取了`jdbcTemplateC3P0`对象

#### 插入操作

```java
/**
 * 插入操作
 */
@Test
public void test(){
    int i = jdbcTemplateC3P0.update("INSERT INTO account VALUES (null ,?,?)", "Lily", 50000d);
    if (i > 0){
        System.out.println("Update Successful");
    }
}
```

#### 修改操作

```java
/**
 * 修改操作
 */
@Test
public void test(){
    int i = jdbcTemplateC3P0.update("UPDATE account SET name = ? WHERE id = ?", "Bob", 1);
    if (i > 0){
        System.out.println("Update Successful");
    }
}
```

#### 删除操作

```java
/**
 * 删除操作
 */
@Test
public void test(){
    int i = jdbcTemplateC3P0.update("DELETE FROM account WHERE id = ?", 2);
    if (i > 0){
        System.out.println("Delete Successful");
    }
}
```

### 默认数据库连接池完成基本的CRUD操作



更新操作：



#### Query

##### 查询返回单个对象

```java
public Employee findById(Integer eno){
    String sql = "select * from employee where eno = ?";
    //查询单条数据
    //这里就是BeanPropertyRowMapper完成数据库数据对实体对象的转化。
    Employee employee = jdbcTemplate.queryForObject(sql, new Object[]{eno}, new BeanPropertyRowMapper<Employee>(Employee.class));
    return employee;
}
```

##### 查询返回对象集合

要实现查询返回对象集合依然需要自定义类实现`RowMapper<T>`接口，调用的是`query`方法

```java
    public List<Employee> findByDname(String dname){
        String sql = "select * from employee where dname = ?";
        //查询复合数据
        List<Employee> list = jdbcTemplate.query(sql, 
                                                 new Object[]{dname}, 
                                                 new BeanPropertyRowMapper<Employee>
                                                 (Employee.class));
        return list;
    }
```

##### 查询返回Map对象,存储数据

```java
public List<Map<String, Object>> findMapByDname(String dname){
    //无法实体属性映射
    String sql = "select eno as empno , salary as s from employee where dname = ?";
    //将查询结果作为Map进行封装
    List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, new Object[]{dname});
    return maps;
}
```



测试方法：

```java
@RunWith(SpringJUnit4ClassRunner.class)
//这里是把JUNIT的控制权交给Spring，自动出实话
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
//load configureationo
public class testJDBCTemplate {
    @Resource
    private EmployeeDao employeeDao;
    @Test
    public void testFindById(){
        Employee daoById = employeeDao.findById(3308);
        System.out.println("daoById = " + daoById);
    }

    @Test
    public void testByname(){
//        列表查找
        List<Employee> byDname = employeeDao.findByDname("市场部");
        for (Employee employee : byDname) {
            System.out.println("employee = " + employee);
        }
    }
  //map存书数据
    @Test
    public void testByDname(){
        List<Map<String, Object>> maps = employeeDao.findMapByDname("市场部");
        for (Map<String, Object> map : maps) {
            System.out.println("map = " + map);
        }
    }
}
```

#### Insert：

```java
public void insert(Employee employee){
    String sql = "insert into employee(eno,ename,salary,dname,hiredate) values(?,?,?,?,?)";
    //利用update方法实现数据写入操作
    jdbcTemplate.update(sql,new Object[]{
       employee.getEno() , employee.getEname(),employee.getSalary(),employee.getDname() , employee.getHiredate()
    });
}
```

> 这个是默认就commit的？

```java
@Test
public void testInsert(){
    Employee employee = new Employee();
    employee.setDname("市场部");
    employee.setEname("NANCY");
    employee.setEno(8989);
    employee.setHiredate(new Date());
    employee.setSalary((float)1213);
    employeeDao.insert(employee);

}
```

#### Update：

```java
public int update(Employee employee){
    String sql = "UPDATE employee SET ename = ?, salary = ?, dname = ?, hiredate = ? WHERE eno = ?";
    int count = jdbcTemplate.update(sql, new Object[]{employee.getEname(), employee.getSalary(), employee.getDname(), employee.getHiredate(), employee.getEno()});
    return count;
}
```

**TestUpdate code:**

```java
@Test
public void testUpdate(){
    Employee employee = employeeDao.findById(8888);
    System.out.println("employee = " + employee);
    employee.setSalary(employee.getSalary()+1000);
    int count = employeeDao.update(employee);
    System.out.println("本次更新： " + count+"条数据");
}
```

**Result:**

```sh
employee = Employee{eno=8888, ename='NANCY', salary=8888.0, dname='市场部', hiredate=2023-04-18 00:00:00.0}
本次更新： 1条数据
```



#### Delete

```java
public int delete(Integer eno){
    String sql = "delete from employee where eno = ?";
    return jdbcTemplate.update(sql, new Object[]{eno});
}
```



# Spring事务管理

## 什么是事务

事务：逻辑上的一组操作，组成这组操作的各个单元，要么全部成功，要么全部失败。

### 事务的特性

+ 原子性：事务不可分割
+ 一致性：事务执行前后数据完整性保持一致
+ 隔离性：一个事务的执行不应该受到其他事务的干扰
+ 持久性：一旦事务结束，数据就持久化到数据库

### 不考虑隔离性引发的安全性问题

+ 读问题
  + 脏读：A事务读到B事务未提交的数据
  + 不可重复读：B事务在A事务两次读取数据之间，修改了数据，导致A事务两次读取结果不一致
  + 幻读/虚读：B事务在A事务批量修改数据时，插入了一条新的数据，导致数据库中仍有一条数据未被修改。
+ 写问题
  + 丢失更新：

### 解决读问题

+ 设置事务的隔离级别
  + `Read uncommitted`：未提交读，任何读问题都解决不了
  + `Read committed`：已提交读，解决脏读，但是不可重复读和幻读有可能发生
  + `Repeatable read`：重复读，解决脏读和不可重复读，但是幻读有可能发生
  + `Serializable`：解决所有读问题，因为禁止并行执行

### 事物测试代码：

```java
public void batchImport(){
        for (int i = 0; i < 11; i++) {
            Employee employee = new Employee();
            employee.setDname("市场部");
            employee.setEname("员工00"+i);
            employee.setEno(8000+i);
            employee.setHiredate(new Date());
            employee.setSalary((float)1213);
            employeeDao.insert(employee);
        }
    }
```

JDBC连接日志：可以看到产生了十次数据库连接,十次提交，是在十个事物。

```sh
17:17:18.393 [main] DEBUG org.springframework.jdbc.core.JdbcTemplate - Executing prepared SQL statement [insert into employee(eno,ename,salary,dname,hiredate) values(?,?,?,?,?)]
17:17:18.393 [main] DEBUG org.springframework.jdbc.datasource.DataSourceUtils - Fetching JDBC Connection from DataSource
17:17:18.393 [main] DEBUG org.springframework.jdbc.datasource.DriverManagerDataSource - Creating new JDBC DriverManager Connection to [jdbc:mysql:///imooc?useSSL=true&userUnicode=true&characterEncoding=UTF-8]
17:17:18.425 [main] DEBUG org.springframework.jdbc.core.JdbcTemplate - SQLWarning ignored: SQL state '22007', error code '1292', message [Incorrect date value: '2023-04-18 17:17:18.393' for column 'hiredate' at row 1]
17:17:18.425 [main] DEBUG org.springframework.jdbc.core.JdbcTemplate - Executing prepared SQL update
```

引入事物，**对这个整体控制。**

### Spring 支持两种方式的事务管理

#### 编程式事务管理

通过 `TransactionTemplate`或者`TransactionManager`手动管理事务，实际应用中很少使用，但是对于你理解 Spring 事务管理原理有帮助。

#### 声明式事务管理

推荐使用（代码侵入性最小），实际是通过 AOP 实现（基于`@Transactional` 的全注解方式使用最多）。

使用 `@Transactional`注解进行事务管理的示例代码如下：

```java
@Transactional(propagation=propagation.PROPAGATION_REQUIRED)
public void aMethod {
  //do something
  B b = new B();
  C c = new C();
  b.bMethod();
  c.cMethod();
}
```



## Spring事务管理API

+ `PlatformTransactionManager`：平台事务管理器

  + `DataSourceTransactionManager`：底层使用JDBC管理事务

+ `TransactionDefinition`：事务定义信息

  ​	用于定义事务相关的信息，隔离级别，超时信息，传播行为，是否只读……

+ `TransactionStatus`：事务的状态

  ​	用于记录在事务管理过程中，事务的状态

API的关系：

Spring在进行事务管理的时候，首先**平台事务管理器**根据**事务定义信息**进行事务的管理，在事务管理过程中，产生各种状态，将这些状态信息记录到**事务状态对象**

## Spring事务的传播行为

首先假设一个背景，Service1里的x()方法已经定义了一个事务，Service2里的y()方法也有一个事务，但现在新增一行代码在Service2的y()方法中要先调用Service1里的x()方法然后再执行本身的方法。这时就涉及到**事务的传播行为**。

![](https://blogpictrue-1251547651.cos.ap-chengdu.myqcloud.com/blog/20190321110709.png)

Spring中提供了7种传播行为

**假设x()方法称为A，y()方法称为B**

+ 保证多个操作在同一个事务中
  + **`PROPAGATION_REQUIRED`**(\*)：Spring事务隔离级别的默认值。如果A中有事务，则使用A中的事务。如果没有，则创建一个新的事务，将操作包含进来。也就是A虽然完成了，还是会回滚。
  + `PROPAGATION_SUPPORTS`：支持事务。如果A中有事务，使用A中的事务。如果A没有事务，则不使用事务。
  + `PROPAGATION_MANDATORY`：如果A中有事务，使用A中的事务。如果没有事务，则抛出异常。
+ 保证多个事务不在同一个事务中
  + **`PROPAGATION_REQUIRES_NEW`**(\*)：如果A中有事务，将A的事务挂起，创建新事务，只包含自身操作。如果A中没有事务，创建一个新事物，包含自身操作。
  + `PROPAGATION_NOT_SUPPORTED`：如果A中有事务，将A的事务挂起，不使用事务。
  + `PROPAGATION_NEVER`：如果A中有事务，则抛出异常。
+ 嵌套式事务
  + **`PROPAGATION_NESTED`**(\*)：嵌套事务，如果A中有事务，则按照A的事务执行，执行完成后，设置一个保存点，再执行B中的操作，如果无异常，则执行通过，如果有异常，则可以选择回滚到初始位置或者保存点。

## Spring事务管理案例——转账情景

### 转账情景实现

首先创建接口`AccountDao`，定义两个方法分别是`out`和`in`

```java
/**
 * AccountDao
 *
 * @author Chen Rui
 * @version 1.0
 **/

public interface AccountDao {

    /**
     * 转出
     *
     * @param from  转出账户
     * @param money 转出金额
     */
    void out(String from, double money);

    /**
     * 转入
     *
     * @param to    转入账户
     * @param money 转入金额
     */
    void in(String to, double money);
}
```

接着创建实现类`AccountDaoImpl`实现`out`和`in`方法并且继承`JdbcSupport`类。这样就可以直接使用父类的`JDBCTemplate`对象。

```java
/**
 * AccountDao实现类
 *
 * @author Chen Rui
 * @version 1.0
 **/
public class AccountDaoImpl extends JdbcDaoSupport implements AccountDao {

    @Override
    public void out(String from, double money) {
        this.getJdbcTemplate().update("UPDATE account SET money = money - ? WHERE name = ?", money, from);
    }

    @Override
    public void in(String to, double money) {
        this.getJdbcTemplate().update("UPDATE account SET money = money + ? WHERE name = ?", money, to);
    }
}

```

然后创建接口`AccountrService`，定义`transfer`方法

```java
/**
 * AccountService
 *
 * @author Chen Rui
 * @version 1.0
 **/
public interface AccountService {

    /**
     * 转账
     * @param from 转出账户
     * @param to 转入账户
     * @param money 交易金额
     */
    void transfer(String from, String to, Double money);
}

```

再创建类`AccountServiceImpl`实现该接口，并声明`AccountDao`引用并创建`set`方法

```java
/**
 * AccountService实现类
 *
 * @author Chen Rui
 * @version 1.0
 **/
public class AccountServiceImpl implements AccountService {

    private AccountDao accountDao;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public void transfer(String from, String to, Double money) {
        accountDao.out(from, money);
        accountDao.in(to, money);
    }
}
```

最后创建配置文件`spring-tx-programmatic.xml`，用来管理Bean。

引入数据库连接文件，配置数据源，创建Bean对象`accountDao`将数据源`dataSource`注入到`accountDao`中，再创建Bean对象`accountService`，将`accountDao`注入。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="                                             
            http://www.springframework.org/schema/beans  
            http://www.springframework.org/schema/beans/spring-beans.xsd  
            http://www.springframework.org/schema/context   
            http://www.springframework.org/schema/context/spring-context.xsd  
            http://www.springframework.org/schema/tx 
            http://www.springframework.org/schema/tx/spring-tx.xsd
            http://www.springframework.org/schema/aop
            http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!-- 编程式事务管理配置文件 -->

    <!-- 配置Service -->
    <bean id="accountService" class="learningspring.transaction.programmatic.AccountServiceImpl">
        <property name="accountDao" ref="accountDao"/>
    </bean>

    <!-- 配置Dao -->
    <bean id="accountDao" class="learningspring.transaction.programmatic.AccountDaoImpl">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- 引入数据库配置文件 -->
    <context:property-placeholder location="db.properties"/>

    <!-- 配置C3P0连接池 -->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="${jdbc.driverClassName}"/>
        <property name="jdbcUrl" value="${jdbc.url}"/>
        <property name="user" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>
</beans>
```

到此一个转账模拟业务就实现了，数据库表依然使用前面创建的`account`表，先查询当前数据库的数据。

![](https://blogpictrue-1251547651.cos.ap-chengdu.myqcloud.com/blog/20190321124514.png)

编写测试方法，实现让姓名为Bob的账户向Jack转账1000元。

```java
/**
 * 编程式事务测试类
 *
 * @author Chen Rui
 * @version 1.0
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:spring-tx-programmatic.xml")
public class AppTest {

    @Resource(name = "accountService")
    private AccountService accountService;

    @Test
    public void test(){
        accountService.transfer("Bob","Jack",1000d);
    }
}
```

运行结果

![](https://blogpictrue-1251547651.cos.ap-chengdu.myqcloud.com/blog/20190321124630.png)

现在对类`AccountServiceImpl`里的`transfer`方法进行修改，让其发生异常，再观察结果

```java
/**
 * AccountService实现类
 *
 * @author Chen Rui
 * @version 1.0
 **/
public class AccountServiceImpl implements AccountService {

    private AccountDao accountDao;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public void transfer(String from, String to, Double money) {
        accountDao.out(from, money);
        // 抛出异常
        int i = 1/0;
        accountDao.in(to, money);
    }

}

```

查询数据库数据

![](https://blogpictrue-1251547651.cos.ap-chengdu.myqcloud.com/blog/20190321125027.png)

这时Bob账户的钱就少了1000元，而Jack账户也没有增加1000元。

所以就需要事务来进行管理。

### 编程式事务

所谓编程式事务，就是要在源码中编写事务相关的代码。实现编程式事务，首先要在`AccountServiceImpl`中声明`TransactionTemplate`对象，并创建set方法。然后修改`transfer`参数列表所有参数都用`final`(因为使用了匿名内部类)修饰，并修改方法体内容。

```java
/**
 * AccountService实现类
 *
 * @author Chen Rui
 * @version 1.0
 **/
public class AccountServiceImpl implements AccountService {

    private AccountDao accountDao;

    private TransactionTemplate transactionTemplate;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void transfer(final String from, final String to, final Double money) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                accountDao.out(from, money);
                // 抛出异常
                int i = 1/0;
                accountDao.in(to,money);
            }
        });
    }
}
```

然后修改`spring-tx-programmatic.xml`文件，创建Bean对象`transactionManager`和`transactionTemplate`，并将`transactionTemplate`注入到`accountService`中。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="                                             
            http://www.springframework.org/schema/beans  
            http://www.springframework.org/schema/beans/spring-beans.xsd  
            http://www.springframework.org/schema/context   
            http://www.springframework.org/schema/context/spring-context.xsd  
            http://www.springframework.org/schema/tx 
            http://www.springframework.org/schema/tx/spring-tx.xsd
            http://www.springframework.org/schema/aop
            http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!-- 编程式事务管理配置文件 -->

    <!-- 配置Service -->
    <bean id="accountService" class="learningspring.transaction.programmatic.AccountServiceImpl">
        <property name="accountDao" ref="accountDao"/>
       <!-- 配置transactionTemplate -->
        <property name="transactionTemplate" ref="transactionTemplate"/>
    </bean>

    <!-- 配置Dao -->
    <bean id="accountDao" class="learningspring.transaction.programmatic.AccountDaoImpl">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- 引入数据库配置文件 -->
    <context:property-placeholder location="db.properties"/>

    <!-- 配置C3P0连接池 -->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="${jdbc.driverClassName}"/>
        <property name="jdbcUrl" value="${jdbc.url}"/>
        <property name="user" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>

    <!-- 配置事务管理器 -->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- 配置模板 -->
    <bean id="transactionTemplate" class="org.springframework.transaction.support.TransactionTemplate">
        <property name="transactionManager" ref="transactionManager"/>
    </bean>
</beans>
```

此时异常依然存在，数据库数据仍然是上次执行的结果状态

![](https://blogpictrue-1251547651.cos.ap-chengdu.myqcloud.com/blog/20190321125027.png)

再次运行测试方法，并查询结果，观察是否发生变化

![](https://blogpictrue-1251547651.cos.ap-chengdu.myqcloud.com/blog/20190321130039.png)

现在就实现了编程式事务，当出现异常时，数据库的数据就不会被修改。

### 声明式事务

**类似于环绕通知；**

配置方法：

配置事物管理器

配置事物通知和属性

绑定pointcut切入点



#### XML配置方式

声明式事务即通过配置文件实现，利用的就是Spring的AOP的环绕通知

advice

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/aop
       https://www.springframework.org/schema/aop/spring-aop.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/tx
       http://www.springframework.org/schema/tx/spring-tx.xsd">
  
  
<!--    扫描组建-->
    <context:component-scan base-package="mcxgroup.*"/>
<!--    启动spring aop注解模式-->
    <aop:aspectj-autoproxy/>
  
  
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
<!--        当前数据源的设置的参数-->
        <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"></property>
        <property name="url" value="jdbc:mysql:///imooc?useSSL=true&amp;userUnicode=true&amp;characterEncoding=UTF-8"></property>
        <property name="username" value="root"/>
        <property name="password" value="Mysql2486"/>
    </bean>
    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
<!--        首要核心-->
        <property name="dataSource" ref="dataSource"></property>
    </bean>
    <bean id="employeeDao" class="mcxgroup.DAO.EmployeeDao">
<!--        注入jdbcTemplate属性-->
        <property name="jdbcTemplate" ref="jdbcTemplate"></property>
    </bean>
  
  
  
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
<!--        事物管理器-->
    </bean>
<!--    事物通州配置-->
    <tx:advice id="txAdvice" transaction-manager="transactionManager">
        <tx:attributes>
<!--            哪些方法使用事务，以及传播行为，一般使用REQUIRED-->
            <tx:method name="batchImport" propagation="REQUIRED"/>
<!--            执行成功会自动提交，失败会自动回滚-->
        </tx:attributes>
    </tx:advice>
    <aop:config>
<!--        设置了切入点，设置作用范围-->
        <aop:pointcut id="point" expression="execution(* mcxgroup.Service.EmployeeService.batchImport(..))"/>
        <aop:advisor advice-ref="txAdvice" pointcut-ref="point"/>
    </aop:config>
</beans>
```



```java
package mcxgroup.Service;

import mcxgroup.DAO.EmployeeDao;
import mcxgroup.entity.Employee;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class EmployeeService {
    @Resource
    private EmployeeDao employeeDao;
    public void batchImport(){
        for (int i = 0; i < 11; i++) {
            Employee employee = new Employee();
            employee.setDname("市场部");
            employee.setEname("员工00"+i);
            employee.setEno(8000+i);
            employee.setHiredate(new Date());
            employee.setSalary((float)1213);
            employeeDao.insert(employee);
            if (i==3){
                throw new RuntimeException("意料之外的异常");
            }
        }
    }
    public EmployeeDao getEmployeeDao() {
        return employeeDao;
    }

    public void setEmployeeDao(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }
}

```



测试结果：

可以看到这里的

```sh
19:51:11.828 [main] DEBUG o.s.j.d.DataSourceTransactionManager - Creating new transaction with name [mcxgroup.Service.EmployeeService.batchImport]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
19:51:11.828 [main] DEBUG o.s.j.d.DriverManagerDataSource - Creating new JDBC DriverManager Connection to [jdbc:mysql:///imooc?useSSL=true&userUnicode=true&characterEncoding=UTF-8]
19:51:12.589 [main] DEBUG o.s.j.d.DataSourceTransactionManager - Acquired Connection [com.mysql.cj.jdbc.ConnectionImpl@45be7cd5] for JDBC transaction
19:51:12.591 [main] DEBUG o.s.j.d.DataSourceTransactionManager - Switching JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@45be7cd5] to manual commit
19:51:12.627 [main] DEBUG o.s.jdbc.core.JdbcTemplate - Executing prepared SQL update
```

这里是在创建事务， Creating new transaction with name [mcxgroup.Service.EmployeeService.batchImport]:。





> ```sh
> 19:51:11.828 [main] DEBUG o.s.j.d.DataSourceTransactionManager - Creating new transaction with name [mcxgroup.Service.EmployeeService.batchImport]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
> 19:51:11.828 [main] DEBUG o.s.j.d.DriverManagerDataSource - Creating new JDBC DriverManager Connection to [jdbc:mysql:///imooc?useSSL=true&userUnicode=true&characterEncoding=UTF-8]
> 19:51:12.589 [main] DEBUG o.s.j.d.DataSourceTransactionManager - Acquired Connection [com.mysql.cj.jdbc.ConnectionImpl@45be7cd5] for JDBC transaction
> 19:51:12.591 [main] DEBUG o.s.j.d.DataSourceTransactionManager - Switching JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@45be7cd5] to manual commit
> 19:51:12.627 [main] DEBUG o.s.jdbc.core.JdbcTemplate - Executing prepared SQL update
> 19:51:12.630 [main] DEBUG o.s.jdbc.core.JdbcTemplate - Executing prepared SQL statement [insert into employee(eno,ename,salary,dname,hiredate) values(?,?,?,?,?)]
> 19:51:12.692 [main] DEBUG o.s.jdbc.core.JdbcTemplate - SQLWarning ignored: SQL state '22007', error code '1292', message [Incorrect date value: '2023-04-18 19:51:12.624' for column 'hiredate' at row 1]
> 19:51:12.695 [main] DEBUG o.s.jdbc.core.JdbcTemplate - Executing prepared SQL update
> 19:51:12.695 [main] DEBUG o.s.jdbc.core.JdbcTemplate - Executing prepared SQL statement [insert into employee(eno,ename,salary,dname,hiredate) values(?,?,?,?,?)]
> 19:51:12.697 [main] DEBUG o.s.jdbc.core.JdbcTemplate - SQLWarning ignored: SQL state '22007', error code '1292', message [Incorrect date value: '2023-04-18 19:51:12.695' for column 'hiredate' at row 1]
> 19:51:12.698 [main] DEBUG o.s.jdbc.core.JdbcTemplate - Executing prepared SQL update
> 19:51:12.698 [main] DEBUG o.s.jdbc.core.JdbcTemplate - Executing prepared SQL statement [insert into employee(eno,ename,salary,dname,hiredate) values(?,?,?,?,?)]
> 19:51:12.700 [main] DEBUG o.s.jdbc.core.JdbcTemplate - SQLWarning ignored: SQL state '22007', error code '1292', message [Incorrect date value: '2023-04-18 19:51:12.698' for column 'hiredate' at row 1]
> 19:51:12.700 [main] DEBUG o.s.jdbc.core.JdbcTemplate - Executing prepared SQL update
> 19:51:12.700 [main] DEBUG o.s.jdbc.core.JdbcTemplate - Executing prepared SQL statement [insert into employee(eno,ename,salary,dname,hiredate) values(?,?,?,?,?)]
> 19:51:12.701 [main] DEBUG o.s.jdbc.core.JdbcTemplate - SQLWarning ignored: SQL state '22007', error code '1292', message [Incorrect date value: '2023-04-18 19:51:12.7' for column 'hiredate' at row 1]
> 19:51:12.701 [main] DEBUG o.s.j.d.DataSourceTransactionManager - Initiating transaction rollback
> 19:51:12.701 [main] DEBUG o.s.j.d.DataSourceTransactionManager - Rolling back JDBC transaction on Connection [com.mysql.cj.jdbc.ConnectionImpl@45be7cd5]
> 19:51:12.703 [main] DEBUG o.s.j.d.DataSourceTransactionManager - Releasing JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@45be7cd5] after transaction
> 19:51:12.713 [main] DEBUG o.s.t.c.c.DefaultCacheAwareContextLoaderDelegate - Retrieved ApplicationContext [572191680] from cache with key [[MergedContextConfiguration@d6da883 testClass = testJDBCTemplate, locations = '{classpath:applicationContext.xml}', classes = '{}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{}', contextCustomizers = set[[empty]], contextLoader = 'org.springframework.test.context.support.DelegatingSmartContextLoader', parent = [null]]]
> 19:51:12.713 [main] DEBUG o.springframework.test.context.cache - Spring test ApplicationContext cache statistics: [DefaultContextCache@1fb19a0 size = 1, maxSize = 32, parentContextCount = 0, hitCount = 4, missCount = 1]
> 19:51:12.715 [main] DEBUG o.s.t.c.c.DefaultCacheAwareContextLoaderDelegate - Retrieved ApplicationContext [572191680] from cache with key [[MergedContextConfiguration@d6da883 testClass = testJDBCTemplate, locations = '{classpath:applicationContext.xml}', classes = '{}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{}', contextCustomizers = set[[empty]], contextLoader = 'org.springframework.test.context.support.DelegatingSmartContextLoader', parent = [null]]]
> 19:51:12.715 [main] DEBUG o.springframework.test.context.cache - Spring test ApplicationContext cache statistics: [DefaultContextCache@1fb19a0 size = 1, maxSize = 32, parentContextCount = 0, hitCount = 5, missCount = 1]
> 19:51:12.717 [main] DEBUG o.s.t.c.s.AbstractDirtiesContextTestExecutionListener - After test method: context [DefaultTestContext@5cdd8682 testClass = testJDBCTemplate, testInstance = testJDBCTemplate@4148db48, testMethod = testBatch@testJDBCTemplate, testException = java.lang.RuntimeException: 意料之外的异常, mergedContextConfiguration = [MergedContextConfiguration@d6da883 testClass = testJDBCTemplate, locations = '{classpath:applicationContext.xml}', classes = '{}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{}', contextCustomizers = set[[empty]], contextLoader = 'org.springframework.test.context.support.DelegatingSmartContextLoader', parent = [null]], attributes = map[[empty]]], class annotated with @DirtiesContext [false] with mode [null], method annotated with @DirtiesContext [false] with mode [null].
> 
> java.lang.RuntimeException: 意料之外的异常
> ```
>
> 

---





#### 注解配置方式





Spring的事务配置仍然支持注解配置

继续沿用`spring-tx-declarative.xml`文件，把事务增强和AOP相关的配置注释，并开启注解事务。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
            http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.springframework.org/schema/context
            http://www.springframework.org/schema/context/spring-context.xsd
            http://www.springframework.org/schema/tx
            http://www.springframework.org/schema/tx/spring-tx.xsd
            http://www.springframework.org/schema/aop
            http://www.springframework.org/schema/aop/spring-aop.xsd">


    <!-- 声明式事务管理配置文件 -->

    <!-- 配置Service -->
    <bean id="accountService" class="learningspring.transaction.declarative.AccountServiceImpl">
        <property name="accountDao" ref="accountDao"/>
    </bean>

    <!-- 配置Dao -->
    <bean id="accountDao" class="learningspring.transaction.declarative.AccountDaoImpl">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- 引入数据库配置文件 -->
    <context:property-placeholder location="db.properties"/>

    <!-- 配置C3P0连接池 -->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="${jdbc.driverClassName}"/>
        <property name="jdbcUrl" value="${jdbc.url}"/>
        <property name="user" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>

    <!-- 配置事务管理器 -->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>
    
    <!-- 配置事务的增强 -->
    <!--<tx:advice id="txAdvice" transaction-manager="transactionManager">-->
        <!--<tx:attributes>-->
            <!-- 配置事务的规则 -->
            <!--<tx:method name="*" propagation="REQUIRED"/>-->
        <!--</tx:attributes>-->
    <!--</tx:advice>-->

    <!-- AOP的配置 -->
    <!--<aop:config>-->
        <!--<aop:pointcut id="pointcut1" expression="execution(* learningspring.transaction.declarative.AccountServiceImpl.*(..))"/>-->
        <!--<aop:advisor advice-ref="txAdvice" pointcut-ref="pointcut1"/>-->
    <!--</aop:config>-->
    
    <tx:annotation-driven transaction-manager="transactionManager"/>
</beans>
```

接下来就可以在业务层类上使用事务管理的注解了。修改`AccountServiceImpl`类，添加`@Transactional`注解

```java
/**
 * AccountService实现类
 *
 * @author Chen Rui
 * @version 1.0
 **/
@Transactional(rollbackFor = Exception.class)
public class AccountServiceImpl implements AccountService{

    private AccountDao accountDao;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }


    @Override
    public void transfer(String from, String to, Double money) {
        accountDao.out(from, money);
        int i = 1/0;
        accountDao.in(to,money);

    }
}
```

再次运行测试方法，数据库也不会发生改变。



### @Transactional注解

#### **事物传播行为介绍:** 

　　@Transactional(propagation=Propagation.REQUIRED) ：如果有事务, 那么加入事务, 没有的话新建一个(默认情况下)
　　@Transactional(propagation=Propagation.NOT_SUPPORTED) ：容器不为这个方法开启事务
　　@Transactional(propagation=Propagation.REQUIRES_NEW) ：不管是否存在事务,都创建一个新的事务,原来的挂起,新的执行完毕,继续执行老的事务
　　@Transactional(propagation=Propagation.MANDATORY) ：必须在一个已有的事务中执行,否则抛出异常
　　@Transactional(propagation=Propagation.NEVER) ：必须在一个没有的事务中执行,否则抛出异常(与Propagation.MANDATORY相反)
　　@Transactional(propagation=Propagation.SUPPORTS) ：如果其他bean调用这个方法,在其他bean中声明事务,那就用事务.如果其他bean没有声明事务,那就不用事务.

#### **事物超时设置:**

　　@Transactional(timeout=30) //默认是30秒

#### **事务隔离级别:**

　　@Transactional(isolation = Isolation.READ_UNCOMMITTED)：读取未提交数据(会出现脏读, 不可重复读) 基本不使用
　　@Transactional(isolation = Isolation.READ_COMMITTED)：读取已提交数据(会出现不可重复读和幻读)
　　@Transactional(isolation = Isolation.REPEATABLE_READ)：可重复读(会出现幻读)
　　@Transactional(isolation = Isolation.SERIALIZABLE)：串行化

　　MYSQL: 默认为REPEATABLE_READ级别
　　SQLSERVER: 默认为READ_COMMITTED

**脏读** : 一个事务读取到另一事务未提交的更新数据
**不可重复读** : 在同一事务中, 多次读取同一数据返回的结果有所不同, 换句话说, 
后续读取可以读到另一事务已提交的更新数据. 相反, "可重复读"在同一事务中多次
读取数据时, 能够保证所读数据一样, 也就是后续读取不能读到另一事务已提交的更新数据
**幻读** : 一个事务读到另一个事务已提交的insert数据

#### **注意的几点:**

​	 1、@Transactional 只能被应用到public方法上, 对于其它非public的方法,如果标记了@Transactional也不会报错,但方法没有事务功能.

  	2、用 spring 事务管理器,由spring来负责数据库的打开,提交,回滚.默认遇到运行期例外(throw new RuntimeException("注释");)会回滚，即遇到不受检查（unchecked）的例外时回滚；而遇到需要捕获的例外(throw new Exception("注释");)不会回滚,即遇到受检查的例外（就是非运行时抛出的异常，编译器会检查到的异常叫受检查例外或说受检查异常）时，需我们指定方式来让事务回滚要想所有异常都回滚,要加上 @Transactional( rollbackFor={Exception.class,其它异常}) .如果让unchecked例外不回滚： @Transactional(notRollbackFor=RunTimeException.class)

　3、@Transactional 注解应该只被应用到 public 可见度的方法上。 如果你在 protected、private 或者 package-visible 的方法上使用 @Transactional 注解，它也不会报错， 但是这个被注解的方法将不会展示已配置的事务设置。

　　4、@Transactional 注解可以被应用于接口定义和接口方法、类定义和类的 public 方法上。然而，请注意仅仅 @Transactional 注解的出现不足于开启事务行为，它仅仅 是一种元数据，能够被可以识别 @Transactional 注解和上述的配置适当的具有事务行为的beans所使用。上面的例子中，其实正是 元素的出现 开启 了事务行为。

　　5、Spring团队的建议是你在具体的类（或类的方法）上使用 @Transactional 注解，而不要使用在类所要实现的任何接口上。你当然可以在接口上使用 @Transactional 注解，但是这将只能当你设置了基于接口的代理时它才生效。因为注解是不能继承的，这就意味着如果你正在使用基于类的代理时，那么事务的设置将不能被基于类的代理所识别，而且对象也将不会被事务代理所包装（将被确认为严重的）。因此，请接受Spring团队的建议并且在具体的类上使用 @Transactional 注解。



#### **@Transactional注解中常用参数说明**

| **参数名称**               | **功能描述**                                                 |
| -------------------------- | ------------------------------------------------------------ |
| **readOnly**               | 该属性用于设置当前事务是否为只读事务，设置为true表示只读，false则表示可读写，默认值为false。例如：@Transactional(readOnly=true) |
| **rollbackFor**            | 该属性用于设置需要进行回滚的异常类数组，当方法中抛出指定异常数组中的异常时，则进行事务回滚。例如：指定单一异常类：@Transactional(rollbackFor=RuntimeException.class)指定多个异常类：@Transactional(rollbackFor={RuntimeException.class, Exception.class}) |
| **rollbackForClassName**   | 该属性用于设置需要进行回滚的异常类名称数组，当方法中抛出指定异常名称数组中的异常时，则进行事务回滚。例如：指定单一异常类名称：@Transactional(rollbackForClassName="RuntimeException")指定多个异常类名称：@Transactional(rollbackForClassName={"RuntimeException","Exception"}) |
| **noRollbackFor**          | 该属性用于设置不需要进行回滚的异常类数组，当方法中抛出指定异常数组中的异常时，不进行事务回滚。例如：指定单一异常类：@Transactional(noRollbackFor=RuntimeException.class)指定多个异常类：@Transactional(noRollbackFor={RuntimeException.class, Exception.class}) |
| **noRollbackForClassName** | 该属性用于设置不需要进行回滚的异常类名称数组，当方法中抛出指定异常名称数组中的异常时，不进行事务回滚。例如：指定单一异常类名称：@Transactional(noRollbackForClassName="RuntimeException")指定多个异常类名称：@Transactional(noRollbackForClassName={"RuntimeException","Exception"}) |
| **propagation**            | 该属性用于设置事务的传播行为，具体取值可参考表6-7。例如：@Transactional(propagation=Propagation.NOT_SUPPORTED,readOnly=true) |
| **isolation**              | 该属性用于设置底层数据库的事务隔离级别，事务隔离级别用于处理多事务并发的情况，通常使用数据库的默认隔离级别即可，基本不需要进行设置 |
| **timeout**                | 该属性用于设置事务的超时秒数，默认值为-1表示永不超时         |

 

## Spring XML配置文件

下面配置文件：开启了AOP，开启了context上下文管理，实现了容器DI，开启了事物管理。开启组件扫描，aop代理模式自动注解。开启事物管理。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/aop
       https://www.springframework.org/schema/aop/spring-aop.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/tx
       http://www.springframework.org/schema/context/spring-tx.xsd">
  
  
<!--    扫描组建-->
    <context:component-scan base-package="mcxgroup.*"/>
<!--    启动spring aop注解模式-->
    <aop:aspectj-autoproxy/>
  
  
  
  
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
<!--        当前数据源的设置的参数-->
        <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"></property>
        <property name="url" value="jdbc:mysql:///imooc?useSSL=true&amp;userUnicode=true&amp;characterEncoding=UTF-8"></property>
        <property name="username" value="root"/>
        <property name="password" value="Mysql2486"/>
    </bean>
    
  	<bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
<!--        首要核心-->
        <property name="dataSource" ref="dataSource"></property>
    </bean>
    
  	<bean id="employeeDao" class="mcxgroup.DAO.EmployeeDao">
<!--        注入jdbcTemplate属性-->
        <property name="jdbcTemplate" ref="jdbcTemplate"></property>
    </bean>
    
  
  
  
  	<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
<!--        事物管理器-->
    </bean>
<!--    事物通州配置-->
<!--    事物通州配置-->
    <tx:advice id="txAdvice" transaction-manager="transactionManager">
        <tx:attributes>
<!--            哪些方法使用事务，以及传播行为，一般使用REQUIRED-->
            <tx:method name="batchImport" propagation="REQUIRED"/>
<!--            执行成功会自动提交，失败会自动回滚-->
        </tx:attributes>
    </tx:advice>
    <aop:config>
<!--        设置了切入点，设置作用范围-->
        <aop:pointcut id="point" expression="execution(* mcxgroup.Service.EmployeeService.batchImport(..))"/>
        <aop:advisor advice-ref="txAdvice" pointcut-ref="point"/>
    </aop:config>
</beans>
```





```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>TestSpring</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.2.6.RELEASE</version>
        </dependency>
        <dependency>
<!--            这是AOP的底层依赖。-->
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.7</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>5.2.6.RELEASE</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>5.2.6.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.32</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.2.7</version>
        </dependency>
    </dependencies>
</project>
```