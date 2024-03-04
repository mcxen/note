# JVM

## JVM内存结构

### 1. Minor GC和Full GC的区别？

Minor GC主要发生在新生代（Young Generation）中，负责清理不再存活的对象，通常采用复制算法，速度较快且频繁。而Full GC则发生在整个堆空间中，包括新生代和老年代（Old Generation），用于清理整个堆中的垃圾对象，速度较慢且可能导致较大的应用停顿。因此，Minor GC和Full GC的主要区别在于它们发生的区域和影响范围。

> 堆空间是JVM中用于存储对象实例的区域，它通常被划分为新生代和老年代两个主要部分，其中新生代又包括Eden区和两个Survivor区。