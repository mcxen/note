# Java

## 一、基础

### 1. int和Integer的区别

**数据类型不同**：int 是基础数据类型，而 Integer 是包装数据类型；

**默认值不同**：int 的默认值是 0，而 Integer 的默认值是 null；

**内存中存储的方式不同**：int 在内存中直接存储的是数据值，而 Integer 实际存储的是对象引用，当 new 一个 Integer 时实际上是生成一个指针指向此对象；

**实例化方式不同**：Integer 必须实例化才可以使用，而 int 不需要；

**变量的比较方式不同**：int 可以使用 == 来对比两个变量是否相等，而 Integer 一定要使用 equals 来比较两个变量是否相等；

**泛型使用不同**：Integer 能用于泛型定义，而 int 类型却不行。

### 2. int 和 Integer 的典型使用场景如下：

- **Integer 典型使用场景**：在 Spring Boot 接收参数的时候，通常会使用 Integer 而非 int，因为 Integer 的默认值是 null，而 int 的默认值是 0。如果接收参数使用 int 的话，那么前端如果忘记传递此参数，程序就会报错（提示 500 内部错误）。因为前端不传参是 null，null 不能被强转为 0，所以使用 int 就会报错。但如果使用的是 Integer 类型，则没有这个问题，程序也不会报错，所以 Spring Boot 中 Controller 接收参数时，通常会使用 Integer。
- **int 典型使用场景**：int 常用于定义类的属性类型，因为属性类型，不会 int 不会被赋值为 null（编译器会报错），所以这种场景下，使用占用资源更少的 int 类型，程序的执行效率会更高。

### 3. 基本数据类型

基本数据类型是指不可再分的原子数据类型，**内存中直接存储此类型的值，通过内存地址即可直接访问到数据**，并且此内存区域只能存放这种类型的值。

在 Java 中，一共有 8 种基本类型（primitive type），其中有 4 种整型、2 种浮点类型、1 种用于表示 Unicode 编码的字符类型 char 和 1 种用于表示真假值的 boolean 类型。

1. 4 种整型：int、short、long、byte
2. 2 种浮点类型：float、double
3. 字符类型：char
4. 真假类型：boolean

> **引用数据类型**：包括`数组`、 `类`、`接口`、`枚举`、`注解`、`记录`。 

![image-20240301092112165](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20240301092112165.png)

### 4. 抽象类和接口的区别

在 Java 中，抽象类和接口是两种不同的类类型。它们都不能直接实例化，并且它们都是用来定义一些基本的属性和方法的，但它们有以下几点不同：

1. **定义不同**：定义的关键字不同，抽象类是 abstract，而接口是 interface。
2. **方法实现**：抽象类可以包含抽象方法和具体方法，而接口只能包含方法声明（抽象方法）。
3. **方法访问控制符不同**：抽象类无限制，只是抽象类中的抽象方法不能被 private 修饰；而接口有限制，接口默认的是 public 控制符。
4. **实现/继承数量不同**：一个类只能继承一个抽象类，但可以实现多个接口。
5. **包含变量不同**：抽象类可以包含实例变量和静态变量，而接口只能包含常量。
6. **构造函数不同**：抽象类可以有构造函数，而接口不能有构造函数。

![image-20240229171549457](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20240229171549457.png)

> 1. 抽象类**不能创建对象**，如果创建，编译无法通过而报错。只能创建其非抽象子类的对象。
>
>    > 理解：假设创建了抽象类的对象，调用抽象的方法，而抽象方法没有具体的方法体，没有意义。
>    >
>    > 抽象类是用来被继承的，抽象类的子类必须重写父类的抽象方法，并提供方法体。若没有重写全部的抽象方法，仍为抽象类。
>    >
>    > ![image-20240229165747139](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20240229165747139.png)
>
> 2. 抽象类中，也有构造方法，是供子类创建对象时，初始化父类成员变量使用的。
>
>    > 理解：子类的构造方法中，有默认的super()或手动的super(实参列表)，需要访问父类构造方法。
>
> 3. **抽象类中，不一定包含抽象方法，但是有抽象方法的类必定是抽象类。**
>
>    > 理解：未包含抽象方法的抽象类，目的就是不想让调用者创建该类对象，通常用于某些特殊的类结构设计。
>
> 4. 抽象类的子类，<mark>必须重写抽象父类中**所有的**抽象方法</mark>，否则，编译无法通过而报错。除非该子类也是抽象类。 
>
>    > 理解：假设不重写所有抽象方法，则类中可能包含抽象方法。那么创建对象后，调用抽象的方法，没有意义。



### 5. 方法重写和方法重载的区别？

方法重写和方法重载是Java中多态性的不同表现。

- 重写是指子类中的方法与父类中继承的方法有完全相同的返回值类型、方法名、参数个数以及参数类型，这样就可以实现对父类方法的覆盖。
- 方法重载发生在同一个类中，方法名相同，参数列表不同。方法返回类型、权限修饰符不作为方法重载的依据。

### 6. String和StringBuilder、StringBuffer的用法区别

- String是不可变的字符串，效率很低
- Buffer可以进行字符串拼接，线程安全的。
- Builder和buffer的方法差不多，但是线程不安全，但是效率就更高了。

**解析：**： 可以从：可变性，线程安全，性能三个方面来进行说明

**参考回答：**

> **可变性**：String 是不可变的（后面会详细分析原因）。StringBuilder 与 StringBuffer 都继承自 AbstractStringBuilder 类，在 AbstractStringBuilder 中也是使用字符数组保存字符串，不过没有使用 final 和 private 关键字修饰，最关键的是这个 AbstractStringBuilder 类还提供了很多修改字符串的方法比如 append 方法。
>
> **线程安全性** :String中的对象是不可变的，也就可以理解为常量，线程安全。AbstractStringBuilder 是 StringBuilder 与 StringBuffer 的公共父类，定义了一些字符串的基本操作，如 expandCapacity、append、insert、indexOf 等公共方法。StringBuffer 对方法加了同步锁或者对调用的方法加了同步锁，所以是线程安全的。StringBuilder 并没有对方法进行加同步锁，所以是非线程安全的。
>
> **性能差异**:每次对 String 类型进行改变的时候，都会生成一个新的 String 对象，然后将指针指向新的 String 对象。StringBuffer 每次都会对 StringBuffer 对象本身进行操作，而不是生成新的对象并改变对象引用。相同情况下使用 StringBuilder 相比使用 StringBuffer 仅能获得 10%~15% 左右的性能提升，但却要冒多线程不安全的风险。

### 7. String常用方法有哪些？

String 类的常见方法有以下这些：

- length()：返回字符串的长度。
- charAt(int index)：返回指定索引处的字符。
- indexOf(String str)：返回指定字符串在当前字符串中第一次出现的位置，如果未找到则返回 -1。
- substring(int beginIndex)：返回从指定索引开始的子字符串。
- substring(int beginIndex, int endIndex)：返回从指定索引开始到指定索引结束的子字符串。
- toLowerCase()：将所有字符转换为小写。
- toUpperCase()：将所有字符转换为大写。
- trim()：去除字符串两端的空格。

### 8. HashMap的问题-红黑树

**为啥不用红黑树？退化成链表**

红黑树插入操作效率低，需要自平衡，综合考虑数据比较少就会退化成链表。

**为啥要用红黑树**

数据较多，链表查询效率低，红黑树有O(logn)效率高。

**负载因子：**

负载因子的默认值为 0.75，当负载因子设置比较大的时候，扩容的门槛就被提高了，扩容发生的频率比较低，占用的空间会比较小，但此时发生 Hash 冲突的几率就会提升，因此需要更复杂的数据结构来存储元素，这样对元素的操作时间就会增加，运行效率也会因此降低.

### 9. String为啥不可变

**解析：**： 注意：很多资料直接说"String 类中使用 final 关键字修饰字符数组来保存字符串，所以String 对象是不可变的" 这是不准确的，因为 final修饰的Array数组 value 是不可变，也只是value这个“引用地址”不可变。挡不住Array数组是可变的事实，其实“私有 pribvate”可能也是不可变的重要原因之一

**参考回答：**

1.保存字符串的数组被 **final** 修饰且为**私有**的，并且String 类没有提供/暴露修改这个字符串的方法。

2.String 类被 final 修饰导致其不能被继承，进而避免了子类破坏 String 不可变。

### 10. 在循环内使用“+”进行字符串拼接的话会有什么问题？

**解析：**： 属于Java String相关常考面试题之一，需要掌握

**参考回答：**

字符串对象通过“+”的字符串拼接方式，实际上是通过 StringBuilder 调用 append() 方法实现的，拼接完成之后调用 toString() 得到一个 String 对象 。不过，在循环内使用“+”进行字符串的拼接的话，存在比较明显的缺陷：编译器不会创建单个 StringBuilder 以复用，会导致创建过多的 StringBuilder 对象。

扩展：不过，使用 “+” 进行字符串拼接会产生大量的临时对象的问题在 JDK9 中得到了解决。在 JDK9 当中，字符串相加 “+” 改为了用动态方法 makeConcatWithConstants() 来实现，而不是大量的 StringBuilder 了.

### 11. Boolean是几个字节

- boolean类型会使用`int`类型来代替，所以1个boolean类型变量使用了4个字节(32位)
- boolean数组会使用`byte数组`来代替，所以1个boolean类型元素在数组中使用了1个字节(8位)

> 整形：byte 1个字节，short：2个字节，int ： 4个字节，long : 8个字节，2的0次，1次，2次，3次
>
> 浮点型：float: 4个字节，Double 是 8个字节。也是 2 4 8 的规律
>
> 字符型：char: 2个字节

### 12. ConcurrentHashMap如何实现线程安全

**如何实现线程安全的？**

- JDK 1.7中的`ConcurrentHashMap`通过分段锁（`Segmentation`）实现线程安全。它将整个哈希表分成多个段（Segment），每个段都是一个小的哈希表，并且拥有自己的锁。这样，多个线程可以并发地访问不同的段，从而减少了锁的竞争，提高了并发性能。
- JDK 1.8中的`ConcurrentHashMap`则采用了完全不同的设计。它摒弃了分段锁的概念，转而使用了一种更细粒度的锁策略，结合CAS（Compare-and-Swap）无锁算法来实现线程安全。在JDK 1.8中，ConcurrentHashMap将整个哈希表看作一个整体，不再进行分段。而是通过数组+链表+红黑树的结构来存储数据，并使用Synchronized和CAS来协调并发访问。





## 二、JUC Java并发编程

### 1. 创建线程池的方法

在Java中，创建线程池有两种常见的方式：

ThreadPoolExecutor

　　1.通过 Executors 工具类提供的静态方法创建线程池。

　　2.通过 ThreadPoolExecutor 构造函数自定义线程池。

> 下面是两种方式的代码示例：
>
> 1. 通过` Executors` 工具类提供的静态方法创建线程池：
>
> ```java
> import java.util.concurrent.ExecutorService;
> import java.util.concurrent.Executors;
> 
> public class ThreadPoolExample {
> 
>     public static void main(String[] args) {
>         
>         // 创建固定大小的线程池
>         ExecutorService executor = Executors.newFixedThreadPool(5);
> 
>         // 提交任务给线程池
>         for (int i = 0; i < 10; i++) {
>             executor.execute(new Task());
>         }
> 
>         // 关闭线程池
>         executor.shutdown();
>     }
> 
>     static class Task implements Runnable {
>         public void run() {
>             System.out.println("Executing task on thread: " + Thread.currentThread().getName());
>         }
>     }
> }
> ```
>
> 　　2.通过` ThreadPoolExecutor `构造函数自定义线程池：
>
> ```java
> import java.util.concurrent.ExecutorService;
> import java.util.concurrent.Executors;
> import java.util.concurrent.ThreadPoolExecutor;
> 
> public class ThreadPoolExample {
> 
>     public static void main(String[] args) {
>         
>         // 自定义线程池
>         ThreadPoolExecutor executor = new ThreadPoolExecutor(
>             2, // 核心线程数
>             5, // 最大线程数
>             1, // 空闲线程存活时间
>             TimeUnit.SECONDS, // 存活时间单位
>             new LinkedBlockingQueue<Runnable>() // 任务队列
>         );
> 
>         // 提交任务给线程池
>         for (int i = 0; i < 10; i++) {
>             executor.execute(new Task());
>         }
> 
>         // 关闭线程池
>         executor.shutdown();
>     }
> 
>     static class Task implements Runnable {
>         public void run() {
>             System.out.println("Executing task on thread: " + Thread.currentThread().getName());
>         }
>     }
> }
> ```
>
> 　　在上面的示例中，我们创建了两个线程池，一个是固定大小的线程池，一个是自定义线程池。在两个线程池中，我们都提交了 10 个任务给线程池执行。每个任务都只是打印当前线程的名称。最后，我们调用了线程池的 shutdown() 方法关闭线程池。

### 2. 线程池有哪些核心参数？各自的作用？项目中如何使用的？

核心线程数,最大线程数,等待队列,拒绝策略,空闲县城存活时间,存活时间单位

- corePoolSize（核心线程数）：这是线程池的基本大小，即线程池中始终保持的线程数量，不受空闲时间的影响。当有新任务提交时，如果线程池中的线程数小于corePoolSize，即便存在空闲线程，线程池也会创建一个新线程来执行任务。
- maximumPoolSize（最大线程数）：线程池允许创建的最大线程数量。当线程池中的线程数超过corePoolSize，但小于maximumPoolSize时，只有在队列满的情况下，才会创建新的线程来处理任务。
- keepAliveTime（空闲线程存活时间）：当线程池中的线程数超过corePoolSize时，多余的线程在空闲时间超过keepAliveTime后会被销毁，直到线程池中的线程数不超过corePoolSize。这有助于在系统空闲时节省资源。
- unit（时间单位）：这是keepAliveTime的时间单位，如秒、毫秒等。它定义了如何解释keepAliveTime的值。
- workQueue（工作队列）：这是一个用于存储等待执行的任务的队列。当线程池中的线程数达到corePoolSize，且这些线程都在执行任务时，新提交的任务会被放入workQueue中等待执行。
- threadFactory（线程工厂）：这是一个用于创建新线程的对象。通过自定义线程工厂，我们可以控制新线程的创建方式，例如设置线程的名字、是否是守护线程等。
- handler（拒绝策略）：当所有线程都在繁忙，且workQueue也已满时，新提交的任务会被拒绝。此时，就需要一个拒绝策略来处理这种情况。常见的拒绝策略有抛出异常、丢弃当前任务、丢弃队列中最旧的任务或者由调用线程直接执行该任务等。

在实际生产环境中，我们可能更倾向于直接使用ThreadPoolExecutor类来创建线程池，因为这样可以更精细地控制线程池的参数和行为。

### 3. Java的线程和进程间数据交互

#### 线程间数据交互

Java线程共享同一个进程的内存空间，这意味着它们可以直接访问相同的对象和变量。线程间的数据交互通常通过以下几种方式实现：

1. **共享变量**：线程可以访问和修改同一对象或变量。为了确保数据的一致性和线程安全，通常需要使用同步机制，如`synchronized`关键字或`java.util.concurrent`包中的高级同步结构（如`ReentrantLock`, `Semaphore`等）。
2. **等待/通知机制**：通过`Object`类的`wait()`、`notify()`和`notifyAll()`方法，一个线程可以等待特定条件的满足，而另一个线程在条件满足时通知等待的线程。这些方法必须在同步代码块或同步方法中调用。
3. **并发数据结构**：`java.util.concurrent`包提供了一系列线程安全的数据结构，如`ConcurrentHashMap`, `CopyOnWriteArrayList`等，它们通过内部同步机制允许多个线程并发访问和修改数据，而不需要开发者显式同步。

#### 进程间数据交互

Java进程运行在各自独立的内存空间中，因此它们不能直接共享数据。进程间通信（IPC）通常需要操作系统提供的机制或通过网络进行。Java支持的一些进程间通信方法包括：

1. **套接字通信（Sockets）**：两个进程可以通过网络套接字进行数据交换。这可以是在同一台机器上的两个进程之间（使用本地套接字），也可以是不同机器上的进程之间。
2. **文件共享**：进程可以通过读写共享文件来交换数据。这需要确保合适的文件访问同步，以防止数据损坏。
3. **管道（Pipes）**：特别是在Unix和Linux系统中，进程可以通过管道（匿名管道或命名管道）传输数据。Java通过`java.io.PipedInputStream`和`java.io.PipedOutputStream`提供对管道的访问。
4. **共享内存**：虽然这不是Java原生支持的机制，但通过JNI（Java Native Interface）可以实现进程间通过共享内存交换数据。
5. **消息队列、信号量和共享内存（通过JNI或第三方库）**：这些是更低级的IPC机制，通常需要操作系统特定的API或通过JNI在Java中使用。

选择适合的数据交互方法取决于具体的应用需求、性能考虑以及开发时间等因素。对于需要高度并发处理的应用，合理使用线程和进程间的通信机制是非常重要的。

### 4. 进程、线程和协程的区别和作用？

1. **进程（Process）**:
   - 定义：进程是操作系统进行资源分配和调度的一个独立单位，是应用程序的一次执行过程。
   - 特点：每个进程都有自己独立的地址空间，进程间的切换需要较大的开销。
   - 作用：进程是资源分配的基本单位，可以提供应用程序运行的独立环境。
2. **线程（Thread）**:
   - 定义：线程是进程中的一个执行流程，是操作系统调度执行的最小单位。
   - 特点：线程共享其所属进程的地址空间和资源，线程间的切换开销小于进程间切换。
   - 作用：线程是CPU调度的基本单位，利用多线程可以提高程序的并发执行能力，有效利用CPU资源。
3. **协程（Coroutine）**:
   - 定义：协程是一种用户态的轻量级线程，协程的调度完全由用户控制。
   - 特点：协程拥有极小的切换开销，不需要操作系统参与，可以实现高效的异步编程。
   - 作用：协程可以有效地提高程序在I/O操作和高并发处理上的性能，使得编写非阻塞代码更加容易。

### 5. volatile的作用

`volatile`关键字在多线程编程中的作用是告诉编译器，所修饰的变量可能会被多个线程同时访问并修改，因此编译器不应该对该变量进行优化或缓存。这样可以确保每次访问该变量都是从内存中读取，而不是从缓存中读取，从而避免了多线程并发访问时可能出现的意外行为或数据不一致性问题。

> 双重锁定代码
>
> 双重检查锁定代码示例
>
> ```JAVA
> public class Singleton {  
>     private volatile static Singleton instance = null;  
>   
>     private Singleton() {  
>         // 私有构造函数，防止其他类实例化此单例  
>     }  
>   
>     public static Singleton getInstance() {  
>         if (instance == null) { // 第一次检查  
>             synchronized (Singleton.class) {  
>                 if (instance == null) { // 第二次检查  
>                     instance = new Singleton();  
>                 }  
>             }  
>         }  
>         return instance;  
>     }  
> }
> ```
>
> 

### 6. **锁升级及实现原理？**

Java 中的锁可以根据竞争情况和线程数量进行升级，主要有偏向锁、轻量级锁和重量级锁三种。这些锁的升级过程是为了在保证并发性的同时尽量减少性能开销。

1. **偏向锁（Biased Locking）**：当只有一个线程访问同步块时，会给这个锁对象加一个偏向锁，这样后续同一个线程再次进入同步块时，就不需要再竞争锁了，直接获取锁。这样可以减少不必要的竞争和同步操作。当有其他线程访问时，偏向锁会升级为轻量级锁。
2. **轻量级锁（Lightweight Locking）**：如果有多个线程竞争同一个锁，会使用轻量级锁来代替偏向锁。轻量级锁会尝试使用CAS操作来获取锁，如果成功，则获得锁；如果失败，表示有竞争，升级为重量级锁。
3. **重量级锁（Heavyweight Locking）**：当轻量级锁竞争失败时，升级为重量级锁。重量级锁会使得其他线程进入阻塞状态，直到持有锁的线程释放锁。

锁的升级过程主要是为了在保证多线程安全的前提下，尽量减少锁竞争的情况，提高并发性能。这种升级机制在 Java 中的实现是通过在对象头中标记锁状态来实现的。



### 7. **synchronized的底层实现原理**

**作用**：synchronized是Java中的一个关键字，主要用于给对象加锁，保证同一时刻只有一个线程可以执行某个方法或某个代码块，从而实现线程同步。

**synchronized的底层实现原理**：是基于Monitor（监视器）的。在Java中，**每个对象都与一个Monitor关联**，Monitor包含一个互斥锁和一个等待队列。当一个线程进入synchronized代码块时，会尝试获取对象的Monitor锁，如果锁被其他线程持有，则该线程会被放入等待队列中阻塞。如果成功获取锁，线程执行同步代码，并在退出时释放锁，同时可能会唤醒等待队列中的其他线程。这个过程由JVM内部机制管理，确保多线程访问时的同步和互斥。



### 8. 怎么判断线程池的任务是否都执行完了

1. 使用 isTerminated 方法判断：通过判断线程池的完成状态来实现，需要关闭线程池，一般情况下不建议使用。
2. 使用 getCompletedTaskCount 方法判断：通过计划执行总任务量和已经完成总任务量，来判断线程池的任务是否已经全部执行，如果相等则判定为全部执行完成。但因为线程个体和状态都会发生改变，所以得到的是一个大致的值，可能不准确。
3. 使用 CountDownLatch 判断：相当于一个线程安全的单次计数器，使用比较简单，且不需要关闭线程池，是**比较常用的判断方法**。
4. 使用 CyclicBarrier 判断：相当于一个线程安全的重复计数器，但使用较为复杂，所以日常项目中使用的较少。

### 9. Java 锁类型

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/7f749fc8.png)
