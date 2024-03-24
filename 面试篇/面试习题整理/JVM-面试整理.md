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

### 1. Minor GC和Full GC的区别？

Minor GC主要发生在新生代（Young Generation）中，负责清理不再存活的对象，通常采用复制算法，速度较快且频繁。而Full GC则发生在整个堆空间中，包括新生代和老年代（Old Generation），用于清理整个堆中的垃圾对象，速度较慢且可能导致较大的应用停顿。因此，Minor GC和Full GC的主要区别在于它们发生的区域和影响范围。

> 堆空间是JVM中用于存储对象实例的区域，它通常被划分为新生代和老年代两个主要部分，其中新生代又包括Eden区和两个Survivor区。

### 2. 永久代存储什么？

JDK7之前的叫做永久代，存储的是静态变量，类的元数据，包括类的信息，常量池信息，字符串常量池，Java 8开始，被原空间取代，避免了原来的永久代的一些问题。

### 3. JVM内存结构

![image-20240320101807784](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20240320101807784.png)

### 

## 垃圾回收

### 1.CMS 算法 - Concurrent Mark Sweep GC并行标记垃圾回收算法



流程为：

- **初始标记**：仅仅只是标记一下GC Roots能直接关联到的对象，速度很快，需要“Stop The World”。
- **并发标记**：进行GC Roots Tracing的过程，在整个过程中耗时最长。
- **重新标记**：为了修正并发标记期间因用户程序继续运作而导致标记产生变动的那一部分对象的标记记录，这个阶段的停顿时间一般会比初始标记阶段稍长一些，但远比并发标记的时间短。此阶段也需要“Stop The World”。
- **并发清除**。