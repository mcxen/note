# SpringBoot 基础篇

## Spring简介

为了降低Java开发的复杂性，Spring采用了一下4种关键策略

- 基于POJO的轻量级和最小侵入性编程
- 通过IOC，依赖注入(DI)和面向接口实现松耦合
- 基于切面(AOP)和惯例进行声名式编程
- 通过切面和模板减少样式代码

## SpringBoot简介

>简化Spring应用开发的一个框架；
>
>整个Spring技术栈的一个大整合；
>
>J2EE开发的一站式解决方案；

所有的技术框架的发展都遵循了一条规律：从一个复杂的应用场景衍生出一种规范框架，人们只需要进行各种配置即可，即约定大于配置。

**优点：**

- 为所有的Spring开发者更快入门
- 开箱即用，提供各种默认配置来简化项目配置
- 内嵌式容器简化Web项目
- 没有冗余代码生成和XML的配置

程序 = 数据结构 + 算法：程序员

程序 = 面向对象 + 框架：码农

## 创建SpringBoot项目

- 使用官网创建  ` https://start.spring.io/ `
  - ![1594437685697](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/1594437685697.png)
- 使用IDEA创建
  - 使用官方的链接 ` https://start.spring.io/ ` 因为是外网，导致连接失败
  - 这里推荐使用阿里的连接 `https://start.aliyun.com`
  - ![1594437403621](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/1594437403621.png)
  - ![1594437494644](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/1594437494644.png)
  - ![1594437527798](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/1594437527798.png)
  - ![1594437561791](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/1594437561791.png)
  - ![1594437632066](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/1594437632066.png)
  - 到此，SpringBoot项目创建成功
  - ![截屏2023-04-25 21.47.33](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-04-25%2021.47.33.png)
  
  ```java
  @RestController
  //返回就是json的格式
  public class ParaController {
  //    参数
      @GetMapping("/f1")
      public String first(){
          return "Hello SpringBoot 我的第一个SpringBoot借口";
      }
  }
  ```
  
  这里测试一个接口，
  
  点击`public class TestSpringBootApplication {`左侧的绿色➡️就可以直接运行，不需要Tomcat了，这里内置了tomcat.
  
  

### 简单配置properties



```properties
server.port=8081
spring.application.name=first-spring-boot
server.servlet.context-path=/first
#相当于就是requestMapping，加一个前缀
```



## SpringBoot 自动装配原理

**pom.xml**

- spring-boot-dependencies ：核心依赖在父工程中
- 在引入一些SpringBoot依赖的时候，不需要指定版本， 就有这些版本仓库

**启动器**

- spring-boot-starter 是SpringBoot默认的启动器，必须要有的

- ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter</artifactId>
      <version>2.3.0.RELEASE</version>
      <scope>compile</scope>
  </dependency>
  <!-- 其他启动器 -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
      <!-- <artifactId>spring-boot-starter-xxx</artifactId> -->
  </dependency>
  ```

- 启动器：就是SpringBoot的启动场景

- 比如：`spring-boot-starter-web` 就会自动导入web环境所有的依赖

- SpringBoot会将所有的场景功能，都编变成一个一个的启动器

- 我们要使用什么功能，就只要找到相应的启动器就可以了

**主程序**

- ```java
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

- `SpringApplication.run(SpringBoot01Application.class, args);` 启动SpringBoot应用

- 注解

  - ```java
    @SpringBootConfiguration：SpringBoot的配置
    	@Configuration：Spring的配置类
        	@Component：是Spring的一个组件
        
    @EnableAutoConfiguration：自动配置
        @AutoConfigurationPackage：自动配置包
        	@Import(AutoConfigurationPackages.Registrar.class)：自动配置包注册
        @Import(AutoConfigurationImportSelector.class)：自动配置导入选择
        
    // 获取所有的配置
    List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
    ```

  - 获取候选的配置

  - ```java
    protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
    		List<String> configurations = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),
    				getBeanClassLoader());
    		Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you "
    				+ "are using a custom packaging, make sure that file is correct.");
    		return configurations;
    }
    ```

  - ![1594444510613](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/1594444510613.png)

  - 上述图片说明，启动类下的所有的资源被导入

  - ```java
    Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you "
    				+ "are using a custom packaging, make sure that file is correct.");
    ```

  - 上述代码断言，非空，是否有这个目录的配置文件 `META-INF/spring.factories` 这是自动配置的核心文件，如下

  - ![1594444703223](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/1594444703223.png)

  - ![1594444996085](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/1594444996085.png)

  - ```java
    Properties properties = PropertiesLoaderUtils.loadProperties(resource);
    所有资源加载到配置类中
    ```

  - 结论：SpringBoot所有的自动配置都是在启动的时候扫描并加载，`spring.factories`所有的自动配置类都在这里面，但是不一定生效，要判断条件是否成立，只要导入了对应的start，就有对应的启动器了，有了启动器，自动装配就会生效，然后就配置成功了

  - > 1、SpringBoot 在启动的时候，从类路径下`/META-INF/spring-factories`获取指定的值，
    >
    > 2、然后将自动配置的类导入容器，自动配置就会生效，帮我们自动配置
    >
    > 3、以前需要自动配置的时候，现在SpringBoot帮我们做了整合JavaEE，解决方案和自动配置的东西都在 `spring-boot-autoconfigure-2.3.0.RELEASE.jar`下
    >
    > 4、他会把所有需要导入的组件，以类名的形式返回，这些组件就会被添加到容器
    >
    > 5、容器中也有很多的 xxxAutoConfiguration的文件，就是这类给容器中导入了这个场景很多的组件，并自动配置 @Configuration
    >
    > 6、有了自动配置类，就免去了我们自己手写编写配置文件的工作

- SpringBoot的启动类 做了4个事情
  - 推断应用的类型是普通的项目还是Web项目
  - 查找并加载所有的可能的初始化器，设置到 initializers属性中
  - 找出所有的应用程序监听器，设置到listeners属性中
  - 推断并设置main方法的定义类，找到运行的主类
- SpringBoot，自己的理解
  - 自动装配
  - run方法

## Yaml语法

https://www.toyaml.com/index.html转换器

![image-20230425224510923](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230425224510923.png)

SpringBoot使用一个全局的配置文件，名称是固定的：`application.properties` `application.yml`` application.yaml`

语法结构：

`application.properties`： key=value

`application.yml`  `application.yaml` key: value

配置文件的作用：修啊给iSpringBoot自动配置的默认值，因为SpringBoot在底层为我们配置好了

```yml
# k: v

name: icanci

# 对象
student:
  name: ic
  age: 17

# 行内写法
student2: {name: icanci,age: 18}

# 数组
pets:
  - cat
  - dog
  - pig
  
# 行内写法
pets2: [cat,dog,pig]
```

yml可以直接给实体类赋值

例子：

```java
@Component
@ConfigurationProperties(prefix = "person")
public class Person {
    private String name;
    private Integer age;
    private Boolean happy;
    private Date birthDate;
    private Map<String,Object> maps;
    private List<Object> lists;
    private Dog dog;
	
    // 省略 getter、setter
}

@Component
public class Dog {
    @Value("ic")
    private String name;
    @Value("15")
    private Integer age;

    // 省略 getter、setter
}
```

```yml
person:
  name: ic
  age: 18
  happy: false
  birthDate: 2011/11/02
  maps: {k1: v1,k2: v2}
  lists: [ic1,ic2,ic3]
  dog:
    name: dog
    age: 3
```

```xml
<!-- 可以导入相关的处理器 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

也可以使用 Properties 去实现，但是不推荐

如果在某个业务中，只需要获取配日志文件中的某个值，可以使用以下@value

如果专门编写了一个JavaBean和配置文件进行映射，就直接使用 `@ConfigurationProperties(prefix = "entityName")`

**JSR303数据校验**

一些博客上说JSR303数据校验已经集成在 stater-web模块了 但是我2.3的版本还是没有，所以需要自己导入依赖

包是有的，但是好像不能直接引入，那么就使用 import 手动引入也可 如下

```
import javax.validation.constraints.NotNull;
```

或者添加依赖

```xml
 <!--jsr 303-->
<dependency>
    <groupId>javax.validation</groupId>
    <artifactId>validation-api</artifactId>
    <version>1.1.0.Final</version>
</dependency>
<!-- hibernate validator-->
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>5.2.0.Final</version>
</dependency>
```

| 代码           | 说明                                           |
| :------------- | :--------------------------------------------- |
| @Null          | 被注解的元素必须为null                         |
| @NotNull       | 被注解的元素必须不为null                       |
| @AssertTrue    | 被注解的元素必须为true                         |
| @AssertFalse   | 被注解的元素必须为false                        |
| @Min(value)    | 被注解的元素必须为数字，其值必须大于等于最小值 |
| @Max(value)    | 被注解的元素必须为数字，其值必须小于等于最小值 |
| @Size(max,min) | 被注解的元素的大小必须在指定范围内             |
| @Past          | 被注解的元素必须为过去的一个时间               |
| @Future        | 被注解的元素必须为未来的一个时间               |
| @Pattern       | 被注解的元素必须符合指定的正则表达式           |

## 配置文件的位置以及原理

**优先级依次降低**

项目下的 `config/application.yml` 

项目下的 `application.yml` 

项目classpath路径下的 `config/application.yml`

项目classpath路径下的 `application.yml`

**多环境配置**

```yml
# application.yml

# 配置激活的配置
spring:
  profiles:
    active: dev

# application-dev.yml

# application-pro.yml
 
# 也可以使用 三条 --- 来实现配置的分割 如下

spring:
  profiles:
    active: dev
server:
  port: 8080
---
server:
  port: 8181
spring:
  profiles: dev
---
server:
  port: 8282
spring:
  profiles: pro
---
server:
  port: 8383
spring:
  profiles: test
```

**配置的原理**

配置文件到底能写什么 `yml 配置文件和 factories 文件有直接挂钩`

如下代码

```java
@ConfigurationProperties(prefix = "spring.jackson")
public class JacksonProperties {
}
```

因为 `JacksonProperties` 使用`@ConfigurationProperties(prefix = "spring.jackson")`注解标识

会自动去找配置文件中 `spring.jackson`为前缀的配置，找到了就覆盖默认的配置

所以能配置哪些内容，取决于 `xxxProperties`类的属性 

**能够配置哪些内容，可以通过 yml 配置文件 Ctrl + 鼠标左键点进该配置文件 **

xxxAutoConfiguration：默认值   xxxProperties  和 配置文件绑定，就可以使用自定义的配置

**精髓**

- SpringBoot启动会加载大量的自动配置类
- 我们看我们需要的功能有没有在SpringBoot默认写好的自动配置类当中
- 我们再看这个自动配置类到底配置了哪些组件 (只要组件在其中，我们就不需要配置了)
- 给容器中自动配置类添加组件的时候，会从properties类中获取某些属性，我们只需要在配置文件中指定这些属性的值即可
- xxxAutoConfiguration：自动配置类，给容器中添加组件
- xxxProperties：封装配置文件中相关的属性

**@Conditional**

自动配置类必须在一定的条件下才会生效

参考文章： https://blog.csdn.net/xcy1193068639/article/details/81517456?utm_medium=distribute.pc_relevant_t0.none-task-blog-BlogCommendFromMachineLearnPai2-1.edu_weight&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-BlogCommendFromMachineLearnPai2-1.edu_weight 

可以通过配置：`debug: true`查看哪些生效了

## SpringBoot Web开发

### 如何放置静态资源文件

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    if (!this.resourceProperties.isAddMappings()) {
        logger.debug("Default resource handling disabled");
        return;
    }
    Duration cachePeriod = this.resourceProperties.getCache().getPeriod();
    CacheControl cacheControl = this.resourceProperties.getCache().getCachecontrol().toHttpCacheControl();
    if (!registry.hasMappingForPattern("/webjars/**")) {
        customizeResourceHandlerRegistration(registry.addResourceHandler("/webjars/**")
                                             .addResourceLocations("classpath:/META-INF/resources/webjars/")
                                             .setCachePeriod(getSeconds(cachePeriod)).setCacheControl(cacheControl));
    }
    String staticPathPattern = this.mvcProperties.getStaticPathPattern();
    if (!registry.hasMappingForPattern(staticPathPattern)) {
        customizeResourceHandlerRegistration(registry.addResourceHandler(staticPathPattern)
                                             .addResourceLocations(getResourceLocations(this.resourceProperties.getStaticLocations()))
                                             .setCachePeriod(getSeconds(cachePeriod)).setCacheControl(cacheControl));
    }
}
```

**什么是WebJars**

可以是用 用 maven 的方式引入 静态资源 ： https://www.webjars.org/ 

```xml
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>jquery</artifactId>
    <version>3.5.1</version>
</dependency>
```

![1594455001346](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/1594455001346.png)

```java
public class ResourceProperties {
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",                                                    "classpath:/resources/", "classpath:/static/", "classpath:/public/" };
}
// 还有一个 /**
```

优先级：resources > static > public

**总结：**

在SpringBoot中，可以使用以下方式处理静态资源

- webjars
- public static resources /** 映射到 `localhost:8080`
- 注意：放在 templates下的所有的界面只能使用Controller来跳转，需要模板引擎的支持

### Thymeleaf

可以查看：`SpringBoot 整合 Thymeleaf 模板引擎.md`

所有的HTML元素都可以用Thymeleaf接管

一些配置文件查看官网即可

### SpringMVC配置原理

```java
// 如果想自己定义一些定制功能，只要写这个组件，
// 然后将它交给SpringBoot，SpringBoot就会帮我们自动装配
// 拓展MVC，官方建议这样去做
// 拓展 MVC
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    public static class MyViewResolver implements ViewResolver {

        @Bean
        public ViewResolver myViewResolver(){
            return new MyViewResolver();
        }

        @Override
        public View resolveViewName(String viewName, Locale locale) throws Exception {
            return null;
        }
    }
}
```

自动配置的原理是一样的，SpringBoot在自动配置的时候，先看容器中有没有用户自己配置的，如果有用户自己配置的bean，那么就用用户配置的，如果没有，就用自动配置的。如果组件可以存在多个，就将用户配置和自己的默认组合起来

## 基于SpringBoot和模板引擎的项目实战

和尚硅谷的一样

记得使用`Lombok`的时候,不仅仅是 `@Data`注解，也要标注 `@AllArgsConstructor`  `@NoArgsConstructor` 为有参构造和无参构造

