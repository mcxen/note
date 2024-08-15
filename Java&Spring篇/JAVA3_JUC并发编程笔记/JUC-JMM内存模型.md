## JMM内存模型（Java 线程内存模型）

其实类似于CPU读取内存，需要设计缓存的模型。

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20240326233814347.png" alt="image-20240326233814347" style="zoom: 33%;" />

`volatile是Java虚拟机提供的轻量级的同步机制`，volatile相当于是轻量级的synchronized。如果一个变量使用volatile，则它比使用synchronized的成本更加低，因为`它不会引起线程上下文的切换和调度`。

通俗点讲就是说一个变量如果用volatile修饰了，则Java可以确保所有线程看到这个变量的值是一致的，如果某个线程对volatile修饰的共享变量进行更新，那么其他线程可以立马看到这个更新，这就是所谓的`线程可见性(内存可见性)`。

![image-20240326234010373](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20240326234010373.png)

首先要将变量从主内存拷贝的自己的工作内存空间，然后对变量进行操作，操作完成后再将变量写回主内存，不能直接操作主内存中的变量，工作内存中存储着主内存中的变量副本拷贝(注意此处的变量与Java中常见变量定义的区别)。
注意这里的“变量”，通常指实例对象的变量和数组引用。如果是类的静态成员变量仍然这样理解，将会出错。

![截屏2024-03-26 23.11.00](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2024-03-26%2023.11.00.png)

比如这里的`InitFlag`，是一个共享变量，但是第一个线程恢复不了了，因为每一个线程都是搞了一个副本再跑。

这里加了`Volatile`就能解决这个问题，保证多线程之间的可见性。

![截屏2024-03-26 23.13.49](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2024-03-26%2023.13.49.png)



![image-20240326233510129](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20240326233510129.png)

之前Volatile是总线加锁（性能太低）

cpu从主内存读取数据到高速缓存，会在总线对这个数据加锁，这样其它cpu没法去读或写这个数据，直到这个cpu使用完数据释放锁之后其它cpu才能读取该数据。



现在是MESI缓存一致性协议

多个cpu从主内存读取同一个数据到各自的高速缓存，当其中某个cpu修改了缓存里的数据，该数据会马上同步回主内存，其它cpu通过**总线嗅探机制**（类似于消息队列监听）可以感知到数据的变化从而将自己缓存里的数据失效。

![image-20240326233544834](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20240326233544834.png)