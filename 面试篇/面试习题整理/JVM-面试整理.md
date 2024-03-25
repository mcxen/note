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

### 1.CMS 算法 - Concurrent Mark Sweep GC并行标记垃圾回收算法



流程为：

- **初始标记**：仅仅只是标记一下GC Roots能直接关联到的对象，速度很快，需要“Stop The World”。
- **并发标记**：进行GC Roots Tracing的过程，在整个过程中耗时最长。
- **重新标记**：为了修正并发标记期间因用户程序继续运作而导致标记产生变动的那一部分对象的标记记录，这个阶段的停顿时间一般会比初始标记阶段稍长一些，但远比并发标记的时间短。此阶段也需要“Stop The World”。
- **并发清除**。



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
