## SpringBoot 是如何通过jar包启动的

得益于SpringBoot的封装，我们可以只通过jar -jar一行命令便启动一个web项目。再也不用操心搭建tomcat等相关web容器。那么，你是否探究过SpringBoot是如何达到这一操作的呢？只有了解了底层实现原理，才能更好的掌握该项技术带来的好处以及性能调优。本篇文章带大家聊一探究竟。

### java -jar做了什么

先要弄清楚java -jar命令做了什么，在oracle官网找到了该命令的描述：

If the -jar option is specified, its argument is the name of the JAR file containing class and resource files for the application. The startup class must be indicated by the Main-Class manifest header in its source code.

使用-jar参数时，后面的参数是的jar文件名(本例中是springbootstarterdemo-0.0.1-SNAPSHOT.jar)；
该jar文件中包含的是class和资源文件；
在manifest文件中有Main-Class的定义；
Main-Class的源码中指定了整个应用的启动类；(in its source code) .

小结一下：
java -jar会去找jar中的manifest文件，在那里面找到真正的启动类。

疑惑出现
在MANIFEST.MF文件中有这么一行内容：

`Start-Class: com.test.Application`
前面的java官方文档中，只提到过Main-Class ，并没有提到Start-Class；

Start-Class的值是com.test.Application，这是我们的java代码中的唯一类，也只真正的应用启动类；
所以问题就来了：理论上看，执行java -jar命令时JarLauncher类会被执行，但实际上是com.test.Application被执行了，这其中发生了什么呢？为什么要这么做呢？

Java没有提供任何标准的方式来加载嵌套的jar文件（即，它们本身包含在jar中的jar文件）。

### Jar包的打包插件及核心方法

Spring Boot项目的pom.xml文件中默认使用如下插件进行打包：

```
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

得到：

spring-learn-0.0.1-SNAPSHOT.jar spring-learn-0.0.1-SNAPSHOT.jar.original

### Jar包

```java
spring-boot-learn-0.0.1-SNAPSHOT
├── META-INF
│   └── MANIFEST.MF
├── BOOT-INF
│   ├── classes
│   │   └── 应用程序类
│   └── lib
│       └── 第三方依赖jar
└── org
    └── springframework
        └── boot
            └── loader
                └── springboot启动程序
```

#### META-INF内容

在上述目录结构中，META-INF记录了相关jar包的基础信息，包括入口程序等。

```
Manifest-Version: 1.0
Implementation-Title: spring-learn
Implementation-Version: 0.0.1-SNAPSHOT
Start-Class: com.tulingxueyuan.Application
Spring-Boot-Classes: BOOT-INF/classes/
Spring-Boot-Lib: BOOT-INF/lib/
Build-Jdk-Spec: 1.8
Spring-Boot-Version: 2.1.5.RELEASE
Created-By: Maven Archiver 3.4.0
Main-Class: org.springframework.boot.loader.JarLauncher
```

可以看到有Main-Class是org.springframework.boot.loader.JarLauncher ，这个是jar启动的Main函数。
还有一个Start-Class是com.tulingxueyuan.Application，这个是我们应用自己的Main函数。

## Spring Boot的Jar应用启动流程总结

总结一下Spring Boot应用的启动流程：
（1）Spring Boot应用打包之后，生成一个Fat jar，包含了应用依赖的jar包和Spring Boot loader相关的类。
（2）Fat jar的启动Main函数是JarLauncher，它负责创建一个LaunchedURLClassLoader来加载/lib下面的jar，并以一个新线程启动应用的Main函数。

JarLauncher通过加载BOOT-INF/classes目录及BOOT-INF/lib目录下jar文件，实现了fat jar的启动。
SpringBoot通过扩展JarFile、JarURLConnection及URLStreamHandler，实现了jar in jar中资源的加载。
SpringBoot通过扩展URLClassLoader–LauncherURLClassLoader，实现了jar in jar中class文件的加载。

WarLauncher通过加载WEB-INF/classes目录及WEB-INF/lib和WEB-INF/lib-provided目录下的jar文件，实现了war文件的直接启动及web容器中的启动。

