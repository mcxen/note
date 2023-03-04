### Java基础

#### JDK、JRE、JVM的区别

- JDK：Java Development Kit 是 Javva开发工具包，包含一些命令行工具，Windows下包含一些图形化界面
- JRE：Java Runtime Environment Java运行时环境，是用来运行Java程序和代码的
- JVM：Java Virtual Machine Java虚拟机，是跨平台的最核心的部分， .class 文件会在JVM上运行，JVM会解释给操作系统执行。JVM只关注被编译的 .class 文件，而不关心 .java 文件
- JDK包含JRE，而JRE包含JVM

#### 接口和抽象类的区别与应用

- 接口和抽象类的区别
  - 抽象类中可以有普通方法和静态方法，抽象类中的抽象方法可以使用public、protected、default修饰，接口在JDK8之前只有抽象方法：默认以 public abstract final 修饰，在JDK8之后，接口中可以有默认方法和静态方法。默认方法使用 default修饰。在JDK9之后，接口中可以有私有方法
  - 抽象类中的成员变量可以和普通类一样，但是接口中之只能是常量，使用 public static final 修饰

- 抽象类一定有抽象方法吗
  - 不一定

- 区别： 抽象类是对一种事物的抽象，即对类抽象，而接口是对行为的抽象。抽象类是对整个类整体进行抽象，包括属性、行为，但是接口却是对类局部（行为）进行抽象 

- 接口
  - 也就是每个部分都可以分开定义
  - 通用Mapper
- 抽象类
  - 也就是子类的实现方式几乎一样
  - 应用：StringBuilder 和 StringBuffer 都继承了实现类 AbstractStringBuilder。AbstractStringBuilder 是在JDK5和StringBuilder一起出现的。JDK9之后，这三者都是从char数组改为byte数组

#### String的常用方法

- equals
- equalsIgnoreCase
- concat 
- compareTo
- compareToIgnoreCase
- indexOf
- lastIndexOf
- startsWith
- endsWith
- isEmpty
- replcae
- replaceAll
- contains
- join 
- split
- getCharArray
- toLowerCase
- toUpperCase
- trim
- charAt

#### Java中的访问修饰符

- public修饰的类，可以在任何地方访问，但是需要注意的是一个java文件中只能有一个public类，并且java文件要和这个public修饰的类一样。修饰的成员和方法，在任意地方都可以访问
- protected 修饰符修饰的成员和方法，在其同包下可以访问，也可以在其子类中访问
- 默认的修饰符，可以在同包中访问
- private 的修饰符，只能在本类中访问

#### throw和throws的区别

- throw作用在方法内部，用来程序员手动抛出异常，如果抛出的异常不是RuntimeException，则需要在方法的形参列表后面 throws 声明抛出的异常，或者使用 try-catch 中进行捕获。可以单独使用
- throws只能作用在方法上，也就是形参列表上，不可以单独使用。将方法抛出的异常上抛给调用者

#### try-catch-finally中哪个部分可以省略？

- catch或者finally可以同时省略一个

#### 异常的继承体系

- Throwable
  - Exception
    - SQLException
    - IOException
    - DateFotmatException
    - RuntimeException
      - StringIndexOutOfBoundsException
      - ClassCastExcetion
      - NullPointerException
      - OOM

#### Java内部类

- 成员内部类
  - 编译之后生成两个class文件
- 方法内部类
  - 编译之后生成两个class文件
- 静态内部类
- 匿名内部类
  - 继承式
    - 编译之后生成两个class文件
  - 接口式
    - 编译之后生成三个class文件
  - 参数式
    - 编译之后生成三个class文件
- 内部类和外部类无关，可以继承其他的类或者实现其他的接口。相对而言弥补了Java单继承的不足
- 内部类也是一个单独的类，在编译之后也会生成class文件，只不过前面加$
- 内部类不能使用普通的方式访问，但是内部类可以访问外部类所有的成员
- 静态内部类不能直接访问外部的非静态方法

#### Java的反射

- 使用场景
  - 在编译的时候无法知道，某些对象或者类属于哪些类。在代码运行的过程中获取类的信息
- 作用
  - 通过反射可以获取到JVM加载中的内部信息，获取已经装在的信息、方法信息
- 优点
  - 提升了Java程序的灵活性和拓展性、降低耦合性、提高代码的适应性
  - 允许程序创建和控制除了枚举类型的类。无需硬编码
  - 应用在框架
- 缺点
  - 反射是一种解释操作，慢于直接代码
  - 增加了程序的复杂性
  - 破坏了封装
  - 反射绕过了源代码。使得程序更加复杂

- Java的反射，就是在运行状态中
  - 获取类的名称，包的信息。属性，方法，注解，类加载器，访问修饰符，父类，实现接口。等所有的类型的信息
  - 获取任意对象的属性，改变对象的任意属性
  - 调用对象的任意方法，判断一个对象所属于的类
  - 实例化任意一个类的对象，但是不包括枚举
- Java的动态体现在反射。反射可以获取和修改非枚举类的任意信息。降低代码的耦合度。反射破坏了封装，过度的使用反射会影响到系统的性能
- 反射的应用
  - 反射是框架设计的灵魂，
  - 使用JDBC驱动，加载驱动就是使用到了反射。Class.forName("")
  - 使用XML配置文件来装配bean。Mybatis中使用反射创建对象，通过读取Mybatis的配置文件。这个时候会有全限定类名，可以获取其对象
  - ①我们在使用JDBC连接数据库时使用Class.forName()通过反射加载数据库的驱动程序；
  - ②Spring框架也用到很多反射机制，最经典的就是xml的配置模式。Spring 通过 XML 配置模式装载 Bean 的过程：1) 将程序内所有 XML 或 Properties 配置文件加载入内存中; 2)Java类里面解析xml或properties里面的内容，得到对应实体类的字节码字符串以及相关的属性信息; 3)使用反射机制，根据这个字符串获得某个类的Class实例; 4)动态配置实例的属性。 

#### 动态代理

- 在运行中，创建目标类，可以调用拓展方法
- Java中实现动态代理的方式
  - JDK代理-只能代理接口
  - Cglib代理-可以代理类
- 应用场景
  - Mybatis的分页插件
  - Spring中的AOP切面
  - 统一的日志输出
  - 接口访问耗时

#### 序列化和反序列化

- 序列化是为了使Java对象可以完成网络的传输，将Java对象转换为文件的形式来实现网络传输
- 反序列化就是将序列化的文件进行解析为Java对象
- 如果一个类的对象可以序列化，其必须实现  Serializable 接口
- static类型的变量属于类，不能被序列化
- transient 修饰的变量是临时变量。不能被序列化
- 注意：序列化的顺序和反序列化的顺序要保持一致
- 这个地方可能出现ClassNotFoundException异常，也就是本地没有这个序列化的这个类，就会抛出这个异常

#### 什么场景需要克隆

- 方法需要 return 一个对象，但是不希望自己持有的引用类型里面的数据被修改
- 有的时候需要保证引用类型的参数不被其他的方法修改，就使用克隆的对象进行传递
- Java中的克隆有浅克隆和深克隆
- 浅克隆指的是A对象里面包含B对象的实例，在对A对象进行克隆的时候，除了克隆A对象普通变量之外，其B对象知识克隆了引用，此时修改B的引用里面的值，数据还是会发生改变。此时就是浅克隆
- 一个对象能否克隆，其类必须实现Cloneable接口，否则会抛出  **CloneNotSupportedException**  异常
- 深拷贝的方式
  - 自己覆写clone方法
  - 使用序列化的方式
- Java跨平台的原理
  - 既然都是Java文件，为什么不使用Java文件跨平台呢，因为JVM不能保证所有的Java文件都是正确的，也就是符合其规范的

#### Java的强大之处

- 使用引用替换了指针，
- 拥有一套异常处理机制
- 强制类型转换需要符合一定的规则
- 强类型语言
- 字节码传输使用了加密
- 不用程序员手动释放内存，使用JVM来释放内存
- 运行环境保障机制：字节码校验器->类装载器->运行时内存布局->文件访问限制

#### Java中有哪些基本数据类型？各自占用多少字节

- byte 1字节。8位
- char 2字节。16位
- short 2字节。16位
- int 4字节。32位
- long 8字节 。 64位
- float 4字节。32位
- double 8字节。64位
- boolean JVM 厂商没有定义。可以理解位1

#### 基本类型转换规则

-  char 与 short，char 与 byte 之间需要强转，因为 char 是无符号类型 
- 其他的就是平常的转换规则

#### Java数组的特征

- 在内存申请一块完成的内存空间
- 数组下标从0开始，为什么从0开始，是因为如果不从0开始，每次进行数据下标进行寻找的时候，会找到对应的内存地址偏移，这样的每次都要多进行一次减法计算，这样的就消耗了性能
- 数组开辟之后有默认值的 默认位 0 0.0 boolean，null
- 数组的类型一旦确定，就不能修改
- 数组的长度一旦确定，就不能修改

#### 可变参数

- JDK5新特性
- 可变参数在编译成字节码的时候，就编译成了数组
- 可变参数必须是最后一个参数
- 可变参数可以传入数组
- 注意点

```java
public class Test {
    public static void main(String[] args) {
        t1(12, 12);
        // 打印 t2
    }


    public static void t1(int a, int as) {
        System.out.println("Test.t2");
    }

    public static void t1(int a, int... as) {
        System.out.println("Test.t1");
    }
}

```

#### java.lang.Object 的常用方法

- getClass
- equals
- hashCode
- wait()
- wait(Long xxx)
- wait(xxx,na)
- toString
- clone
- notify
- notifyAll
- finalize

#### 封装继承多态

- 多态。同一个接口有不同的实现，每个不同的实现有不同的属性和行为
- 多态的三个条件：继承或者实现。子类重写父类方法。父类调用子类

#### 内存泄漏和内存溢出

- 内存泄漏：指的是程序在申请内存之后，无法释放已经申请的内存 （Momory Leak） 其最终会导致 内存溢出
- 内存溢出：指的是程序在申请内存之后，无法为其分配足够的内存 （Out Of Memory）

#### 匿名内部类可以继承或者实现接口吗

- 不可以。 匿名内部类本质上是对父类方法的重写 或 接口的方法的实现 
- 匿名内部类创建的时候从语法角度看，是无法进行声明继承或者实现的
- 匿名内部类没有名字，没有名字就没有构造函数，所以匿名内部类的创建必须通过父类的构造函数

#### 静态与非静态成员变量区别？

- 静态成员是属于类的，随着类的加载而加载并且赋值和初始化
- 非静态成员是属于对象的，在创建对象的时候才会去加载。
- 非静态变量存储在堆内存的对象中，JDK6静态变量存在于方法区，JDK7之后存在于堆中

#### switch

- 在JDK5（不包含JDK5）可以是 byte char short int
- 在JDK5，引入了枚举，可以是枚举类型，引入了自动装箱拆箱。可以是  Character、Byte、Short、Integer、Enum
- 在JDK7，还可以是String类型，因为使用的是String的HashCode和equals

#### 如何跳出循环

- 使用标签标注循环

#### final

- 修饰类
- 修饰常量
- 修饰方法
- 修饰形式参数

#### GB2312编码的字符串如何转换为ISO-8859-1编码？

```java
String s = new String ("甜".getBytes("GB2312"),"ISO-8859-1");
```

#### Java反射的实现类有哪些

- java.lang.Class ：一个类
- java.lang.reflect.Field ：类的成员变量(属性)
- java.lang.reflect.Method ：类的成员方法
- java.lang.reflect.Constructor ：类的构造方法
- java.lang.reflect.Array ：提供了静态方法动态创建数组，访问数组的元素
- **反射的应用**
  - 用 IoC 来注入和组装 bean
  - 动态代理、面向切面、bean 对象中的方法替换与增强，也使用了反射
  - 定义的注解，也是通过反射查找

#### Class类的作用是什么？如何获取Class对象

- Class对象是Java反射的入口和起源，用于获取和类相关的信息：类的名字、属性、方法、构造方法、父类、接口
- 对象名.getClass()
- 对象名.getSuperClass()
- Class.forName();
- 类名.class

#### 面向对象的七大设计原则

- 单一职责原则：也就是说一个类只负责一个作用
- 里氏替换原则： 何基类可以出现的地方，子类一定可以出现 
- 开闭原则：一个类对修改关闭，对拓展开启
- 接口隔离原则：把接口的作用单独描述
- 依赖注入原则：针对接口编程，
- 迪米特法则：最少知道法则，一个类应该尽量少的和其他类有耦合关系
- 组合/聚合复用原则： 尽量使用合成/聚合的方式，而不是使用继承 

#### String能否被继承

- 不可以，String使用final修饰类，因为String在Java语言中被广泛使用，所以必须保证String的安全性和正确性。不可以被继承也不能被重写其方法

#### 类的实例化方法调用顺序

- **类加载器实例化时进行的操作步骤：加载 -> 连接 -> 初始化** 

- 代码书写顺序加载父类静态变量和父类静态代码块
- 代码书写顺序加载子类静态变量和子类静态代码块
- 父类非静态变量（父类实例成员变量）
- 父类构造函数
- 子类非静态变量（子类实例成员变量）
- 子类构造函数

#### JDK8中Stream接口的常用方法

- **中间操作：**
  - filter：过滤元素
  - map：映射，将元素转换成其他形式或提取信息
  - flatMap：扁平化流映射
  - limit：截断流，使其元素不超过给定数量
  - skip：跳过指定数量的元素
  - sorted：排序
  - distinct：去重
- **终端操作：**
  - anyMatch：检查流中是否有一个元素能匹配给定的谓词
  - allMatch：检查谓词是否匹配所有元素
  - noneMatch：检查是否没有任何元素与给定的谓词匹配
  - findAny：返回当前流中的任意元素（用于并行的场景）
  - findFirst：查找第一个元素
  - collect：把流转换成其他形式，如集合 List、Map、Integer
  - forEach：消费流中的每个元素并对其应用 Lambda，返回 void
  - reduce：归约，如：求和、最大值、最小值
  - count：返回流中元素的个数

#### 什么是泛型？为什么要使用泛型？

- Java中的泛型是JDK5中的新特性
- 泛型的本质是把参数的类型参数化，也就是所操作的数据类型被指定为一个参数，这种参数类型可以用在类、接口和方法中。 
- 使用泛型编写的程序代码，要比使用 Object 变量再进行强制类型转换的代码，具有更好的安全性和可读性。
- 多种数据类型执行相同的代码使用泛型可以复用代码。
-  比如集合类使用泛型，取出和操作元素时无需进行类型转换，避免出现 java.lang.ClassCastException 异常 

#### 参考文章

- final等修饰符的作用： https://blog.csdn.net/l1394049664/article/details/81629608 
- 面试题： https://www.javanav.com/interview/93b0069472fd479393006c0e73043fc4.html 
- Java中时间日期转换： https://www.javanav.com/interview/df651b0d3c014ec8a66c406964de5a22.html 
- fianl、finally、finalize： https://mp.weixin.qq.com/s?__biz=MzU2NDg0OTgyMA==&mid=2247495478&idx=1&sn=017e57562cb25b00a3392fe3aa584e3b&chksm=fc4612c5cb319bd353c4406a63e346b02ede8f7c04eabc07e23e52626e16af763378d7858b80&mpshare=1&scene=23&srcid=1102vVoO5JV8q8X3AupFurUn&sharer_sharetime=1604287550517&sharer_shareid=a038ee0eff4e337e2aa4fd50729c3e51#rd 