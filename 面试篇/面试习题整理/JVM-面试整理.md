# JVM

## JVM内存结构

### 1. Minor GC和Full GC的区别？

Minor GC主要发生在新生代（Young Generation）中，负责清理不再存活的对象，通常采用复制算法，速度较快且频繁。而Full GC则发生在整个堆空间中，包括新生代和老年代（Old Generation），用于清理整个堆中的垃圾对象，速度较慢且可能导致较大的应用停顿。因此，Minor GC和Full GC的主要区别在于它们发生的区域和影响范围。

> 堆空间是JVM中用于存储对象实例的区域，它通常被划分为新生代和老年代两个主要部分，其中新生代又包括Eden区和两个Survivor区。

### 2. 永久代存储什么？

JDK7之前的叫做永久代，存储的是静态变量，类的元数据，包括类的信息，常量池信息，字符串常量池，Java 8开始，被原空间取代，避免了原来的永久代的一些问题。

### 3. JVM内存结构

![image-20240320101807784](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20240320101807784.png)