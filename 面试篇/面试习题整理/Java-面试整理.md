# Java

## 基础

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

### 8. HashMap的问题

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

## JUC Java并发编程

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

