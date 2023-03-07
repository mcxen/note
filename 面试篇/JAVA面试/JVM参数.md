### JVM调优参数

- -Xms:2m 内存初始化大小
- -Xmx:2m 内存最大大小
- -XX:maxHeapSize=xx 最大堆大小
- -XX:initialHeapSize=xx 初始化堆大小
- -XX:+HeapDumpOnOutOfMemoryError
- -XX:HeapDumpPath 
- -XX:+PrintGC
- -XX:+PrintGcDetails

#### 参考文章

- JVM参数设置、分析： https://www.cnblogs.com/redcreen/archive/2011/05/04/2037057.html 

- Tomcat破坏双亲委派机制： https://www.cnblogs.com/fanguangdexiaoyuer/p/10213324.html 