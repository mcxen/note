# JVM

## JVM基础

### 1. 设置参数

```bash
-XX:+UseParallelGC
-XX:+UseParNewGC
-XX:+PrintGC
-Xms50m
-Xmx100m
```



### 2.类加载器

从实现方式上，类加载器可以分为两种：一种是**启动类加载器**，由C++语言实现，是虚拟机自身的一部分；另一种是继承于`java.lang.ClassLoader`的类加载器，包括**扩展类加载器**、**应用程序类加载器**以及自定义类加载器。

**启动类加载器**（`Bootstrap ClassLoader`）：负责加载`<JAVA_HOME>\lib`目录中的，或者被`-Xbootclasspath`参数所指定的路径，并且是虚拟机识别的（仅按照文件名识别，如rt.jar，名字不符合的类库即使放在lib目录中也不会被加载）类库加载到虚拟机内存中。**启动类加载器无法被Java程序直接引用**，用户在编写自定义类加载器时，如果想设置`Bootstrap ClassLoader`为其`parent`，**可直接设置null**。

**扩展类加载器**（`Extension ClassLoader`）：负责加载`<JAVA_HOME>\lib\ext`目录中的，或者被`java.ext.dirs`系统变量所指定路径中的所有类库。该类加载器由`sun.misc.Launcher$ExtClassLoader`实现。扩展类加载器由启动类加载器加载，其父类加载器为启动类加载器，即`parent=null`。

**应用程序类加载器**（`Application ClassLoader`）：负责加载用户类路径（`ClassPath`）上所指定的类库，由`sun.misc.Launcher$App-ClassLoader`实现。开发者可直接通过`java.lang.ClassLoader`中的`getSystemClassLoader()`方法获取应用程序类加载器，所以也可称它为系统类加载器。应用程序类加载器也是启动类加载器加载的，但是它的父类加载器是扩展类加载器。在一个应用程序中，系统类加载器一般是默认类加载器。

### 3.双亲委派

JVM 并不是在启动时就把所有的`.class`文件都加载一遍，而是程序在运行过程中用到了这个类才去加载。除了启动类加载器外，其他所有类加载器都需要继承抽象类`ClassLoader`。

双亲委派模型的工作过程是：如果一个类加载器收到了类加载的请求，它首先不会自己去尝试加载这个类，而是把这个请求委派给父类加载器去完成，每一个层次的类加载器都是如此，因此所有的加载请求最终都应该传送到最顶层的启动类加载器中，只有当上一层类加载器反馈自己无法完成这个加载请求（它的搜索范围中没有找到这个类时，下一层类加载器才会尝试自己去加载；

**一层一层向上委派，最顶层的类加载器（启动类加载器）无法加载该类时，再一层一层向下委派给子类加载器加载**。先互相踢皮球再各司其职。

为什么需要双亲委派：

1、 确保安全，避免 Java 核心类库被修改；

2、 避免重复加载；

比如你设计了一个String类，这个类加载会反馈到BootStrap加载器，这个时候他会加载核心的类库的String，防止被你的破坏，也可以防止重复加载，牺牲性能，保证功能。

> 「Java.lang.」这个包都不可以定义

3、保证类的唯一性；

![image-20240330231427326](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20240330231427326.png)

### 3.1 如何破坏双亲委派

双亲委派模型并不是一个具有强制性约束的模型，而是 Java 设计者推荐给开发者们的类加载器实现方式。这个委派和加载顺序完全是可以被破坏的。

只有官方库`java.`的类必须由启动类加载器加载，无法破坏，扩展类加载器和应用程序类加载器的双亲委派都是可以破坏的。

破坏双亲委派有两种方式：第一种，自定义类加载器，必须重写`findClass`和`loadClass`；第二种是通过线程上下文类加载器的传递性，让父类加载器中调用子类加载器的加载动作。

如果想自定义类加载器，就需要继承`ClassLoader`，并重写`findClass`，如果想不遵循双亲委派的类加载顺序，还需要重写`loadClass`。

> 可以看看优秀的开源框架中是如何破坏双亲委派的，比如 Tomcat：
>
> ![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5b6Q5ZCM5a2m5ZGA,size_20,color_FFFFFF,t_70,g_se,x_16.png)
>
> 
>
> 
>
> Tomcat 源码就不贴了，Tomcat 中可以部署多个 web 项目，为了保证每个 web 项目互相独立，所以不能都由`AppClassLoader`加载，所以自定义了类加载器`WebappClassLoader`，`WebappClassLoader`继承自`URLClassLoader`，重写了`findClass`和`loadClass`，并且`WebappClassLoader`的父类加载器设置为`AppClassLoader`。
> `WebappClassLoader.loadClass`中会先在缓存中查看类是否加载过，没有加载，就交给`ExtClassLoader`，`ExtClassLoader`再交给`BootstrapClassLoader`加载；都加载不了，才自己加载；自己也加载不了，就遵循原始的双亲委派，交由`AppClassLoader`递归加载。

### 4.类加载的过程

类加载器，顾名思义就是一个可以将Java字节码加载为`java.lang.Class`实例的工具。这个过程包括，读取字节数组、验证、解析、初始化等。

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20240328221232900.png" alt="image-20240328221232900" style="zoom: 50%;" />

  一个类被初始化的过程？

```java
public class Hello {
 
    //静态常量 == 准备阶段就赋予自己该有的值
    public static final String staticConstantField = "静态常量";
 
    //静态变量 == 准备阶段赋予零值（int = 0 引用 = null），初始化阶段赋予自己该有的值
    public static String staticField = "静态变量";
 
    //实例变量 == 创建对象的时候赋值
    public String field = "变量";
 
    //静态初始化块 == 初始化阶段执行
    static {
        System.out.println(staticConstantField);
        System.out.println(staticField);
        System.out.println("静态初始化块");
    }
 
    //初始化块 == 创建对象的时候执行
    {
        System.out.println(field);
        System.out.println("初始化块");
    }
 
    //构造器 == 创建对象的时候执行
    public Hello() {
        System.out.println("构造器");
    }
 
    // -XX:+TraceClassLoading 监控类的加载
    public static void main(String[] args) {
        //这句话决定了是否初始化对象，决定了 实例变量 、初始化块 和 构造器是否执行
//        new Hello();
    }
 
}
```

> Java创建静态常量的方式如下
>
> ```java
> public static final int a = 1;
> ```
>
> 与创建静态变量的唯一区别就是多了个final关键字，但是jvm生成静态常量的流程稍有不同:
>  1.jvm类加载到准备阶段的时候，会直接将静态常量的值直接赋值为代码指定的值，因此，静态常量的初始化完成的比静态变量的早。
>  2.由于添加了final关键字，静态常量在赋值以后就不能修改其值，这也符合静态常量的特性。

站在Java虚拟机的角度来讲，只存在两种不同的类加载器：

- 启动类加载器（Bootstrap ClassLoader）:这个类加载器使用C++实现，是虚拟机自身的一部分
- 所有其他的类加载器：这些类加载器都由Java实现，独立于虚拟机外部，并且全部继承自抽象类java.lang.ClassLoader

站在Java开发人员的角度来看，类加载器大致可以按照以下划分：

- **启动类加载器**（Bootstrap ClassLoader）:由C++实现，没有父加载器，负责加载<JAVA_HOME>\lib目录或-Xbootclasspath参数指定路径中的类库。仅按照文件名识别，比如rt.jar，名字不符合的类库，即使放在对应目录中也不会被加载。
- **扩展类加载器**（Extension ClassLoader）：由sun.misc.Launcher$ExtClassLoader实现，负责加载<JAVA_HOME>\lib\ext目录或java.ext.dirs系统变量指定路径中的所有类库，开发者可以直使用扩展类加载器。
- **应用程序类加载器**（Application ClassLoader）：由sun.misc.Launcher$AppClassLoader实现。由于它是ClassLoader中的getSystemClassLoader()方法的返回值，所以也被称为**系统类加载器**。它负责加载用户类路径（classpath）上所指定的类库，如果应用程序中没有自定义过自己的类加载器，一般情况下这个就是程序中默认的类加载器。
- **自定义类加载器**（User ClassLoader）：可继承抽象类ClassLoader实现，按照自己的意愿加载字节码文件。

三级结构：

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2h1YW5nemhpbGluMjAxNQ==,size_16,color_FFFFFF,t_70.png" alt="img" style="zoom:50%;" />

⚠️这三个类不存在继承关系

![image-20240328230433395](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20240328230433395.png)






### 5.JVM常见的调优手段



## JVM内存结构

> 类加载器，运行时数据区，执行引擎，本地方法库，本地方法接口

**运行时数据区**包括：堆，栈，本地方法栈，方法区，程序计数器

方法区就是元空间

每个**栈帧**（Stack Frame）中存储着：

![jvm-stack-frame](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/0082zybply1gc8tjehg8bj318m0lbtbu-20240325211019431.jpg)

- 局部变量表（Local Variables）
- 操作数栈（Operand Stack）(或称为表达式栈)
- 动态链接（Dynamic Linking）：指向运行时常量池(方法区)的方法引用

![jvm-dynamic-linking](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/0082zybply1gca4k4gndgj31d20o2td0.jpg)

- 方法返回地址（Return Address）：方法正常退出或异常退出的地址
- 一些附加信息





![jvm-framework](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/0082zybply1gc6fz21n8kj30u00wpn5v.jpg)

### 1. Minor GC和Full GC的区别？

Minor GC主要发生在新生代（Young Generation）中，负责清理不再存活的对象，通常采用复制算法，速度较快且频繁。而Full GC则发生在整个堆空间中，包括新生代和老年代（Old Generation），用于清理整个堆中的垃圾对象，速度较慢且可能导致较大的应用停顿。因此，Minor GC和Full GC的主要区别在于它们发生的区域和影响范围。

> 堆空间是JVM中用于存储对象实例的区域，它通常被划分为新生代和老年代两个主要部分，其中新生代又包括Eden区和两个Survivor区。

### 2. 永久代存储什么？

JDK7之前的叫做永久代，存储的是静态变量，类的元数据，包括类的信息，常量池信息，字符串常量池，Java 8开始，被原空间取代，避免了原来的永久代的一些问题。

### 3. JVM内存结构

![image-20240320101807784](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20240320101807784.png)

## 垃圾回收

![image-20240331154602914](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20240331154602914.png)

### 1.CMS 垃圾回收器- Concurrent Mark Sweep GC并行标记垃圾回收

> CMS用的是 标记清除算法，PArNew GC和Serial GC Parallel GC 都是复制算法，来实现垃圾回收。

流程为：

- **初始标记**：仅仅只是标记一下GC Roots能直接关联到的对象，速度很快，需要“Stop The World”。
- **并发标记**：进行GC Roots Tracing的过程，在整个过程中耗时最长。
- **重新标记**：为了修正并发标记期间因用户程序继续运作而导致标记产生变动的那一部分对象的标记记录，这个阶段的停顿时间一般会比初始标记阶段稍长一些，但远比并发标记的时间短。此阶段也需要“Stop The World”。
- **并发清除**。

### 2. G1垃圾回收器

G1是一个并行回收器，<mark>他把内存分割为很多不相关的区域（Region）（物理上不连续的） 使用不同的Region来表示Eden、幸存者0区、幸存者1区、老年代</mark>。GC 有计划的避免Java堆中进行全区域的垃圾扫描。是**JDK 9以后的默认垃圾回收器**，取代了CMS回收器以及(JDK8 ) Parallel + Parallel old组合。被ORACLE官方称为**“全功能的垃圾收集器”**。CMS已经在JDK 9中被标记为废弃（deprecated）。在jdk8中还不是默认的垃圾回收器，需要使用-XX:+UseG1GC来启用。

- **G1依然属于分代型垃圾回收器**，它会区分年轻代和老年代，年轻代依然有Eden区和survivor区。但从堆的结构上看，它不要求整个Eden区、年轻代或者老年代都是连续的，也不再坚持固定大小和固定数量。
- 将**堆空间分为若干个区域（Region)，这些区域中包含了逻辑上的年轻代和老年代**。
- <mark>在小内存应用上CMS的表现大概率会优于G1，而G1在大内存应用上则发挥其优势。平衡点在6-8GB之间。</mark>

应用场景

- 面向服务端应用，针对具有大内存、多处理器的机器。(在普通大小的堆里表现并不惊喜)
- 最主要的应用是需要低Gc延迟，并具有大堆的应用程序提供解决方案;

### 2.垃圾标记算法

引用计数算法，天机引用计数器，计数器为0，则标记为垃圾，用的比较少，如果循环引用，容易误判。

可达性分析算法，通过GC Roots开始搜索，找到引用链，如果某个对象到GC Root没有引用链，标记为垃圾。

> 可以作为GC Roots的有：虚拟机栈和本地方法栈的对象，常量和类静态属性的对象。



### 3. 垃圾回收算法

**标记清楚算法**，通过标记回收对象，然后执行回收，存在空间碎片化问题，内存碎片不连续，容易触发Full GC

**标记复制算法：**将可用内存按容量划分为大小相等的两块，每次只使用其中一块。当使用的这块空间用完了，就将存活对象复制到另一块，再把已使用过的内存空间一次清理掉。主要用于进行新生代。缺点是：浪费空间

**标记整理算法：**但不直接清理可回收对象，而是让所有存活对象都向内存空间一端移动，然后清理掉边界以外的内存。

### 4.方法区什么时候溢出？

方法区用于存放 Class 的相关信息，如类名、访问修饰符、常量池、字段描述、方法描述等。在加载很多的Class时，就很有可能出现永久代的错误（内存溢出）

> ![1598138073832](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/1598138073832.png)
