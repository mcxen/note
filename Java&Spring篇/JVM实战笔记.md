## 00 基础：

### 工具：安装Visual VM & Visual GC

**查看jdk安装目录**

1、打开终端，输入：/usr/Libexec/java_home -V

注意：输入命令参数区分大小写(-v是不对的，必须是-V）

如图：3个红框内依次为：输入命令；当前Mac已安装idk目录；Mac默认使用的jdk版本；

![截屏2023-06-19 20.23.16](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-06-19%2020.23.16.png)

**下载visual VM**

启动的时候提示找不到idk引用，于是到包内部的visualvm.conf中修改jdk引用地址。

visualvm.conf的路径

```sh
vim /Applications/VisualVM.app/Contents/Resources/visualvm/etc/visualvm.conf
```

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230619202936429.png" alt="image-20230619202936429" style="zoom:50%;" />

将`#visualvm_jdkhome ="path/to/jdk" `修改为类似以下的路径就可以了 

```sh
visualvm_jdkhome="/Library/Java/JavaVirtualMachines/jdk1.8.0_311.jdk/Contents/Home"
```







## 04 Java 字节码技术：不积细流，无以成江河

Java 中的字节码，英文名为 `bytecode`, 是 Java 代码编译后的中间代码格式。JVM 需要读取并解析字节码才能执行相应的任务。

**从技术人员的角度看**，Java 字节码是 JVM 的指令集。JVM 加载字节码格式的 class 文件，校验之后通过 JIT 编译器转换为本地机器代码执行。 简单说字节码就是我们编写的 Java 应用程序大厦的每一块砖，如果没有字节码的支撑，大家编写的代码也就没有了用武之地，无法运行。也可以说，Java 字节码就是 JVM 执行的指令格式。

那么我们为什么需要掌握它呢？

不管用什么编程语言，对于卓越而有追求的程序员，都能深入去探索一些技术细节，在需要的时候，可以在代码被执行前解读和理解中间形式的代码。对于 Java 来说，中间代码格式就是 Java 字节码。 了解字节码及其工作原理，对于编写高性能代码至关重要，对于深入分析和排查问题也有一定作用，所以我们要想深入了解 JVM 来说，了解字节码也是夯实基础的一项基本功。同时对于我们开发人员来时，不了解平台的底层原理和实现细节，想要职业进阶绝对不是长久之计，毕竟我们都希望成为更好的程序员， 对吧？

任何有实际经验的开发者都知道，业务系统总不可能没有 BUG，了解字节码以及 Java 编译器会生成什么样的字节码，才能说具备扎实的 JVM 功底，会在排查问题和分析错误时非常有用，也能更好地解决问题。

而对于工具领域和程序分析来说, 字节码就是必不可少的基础知识了，通过修改字节码来调整程序的行为是司空见惯的事情。想了解分析器(Profiler)，Mock 框架，AOP 等工具和技术这一类工具，则必须完全了解 Java 字节码。

### 4.1 Java 字节码简介

有一件有趣的事情，就如名称所示, `Java bytecode` 由单字节(`byte`)的指令组成，理论上最多支持 `256` 个操作码(opcode)。实际上 Java 只使用了 200 左右的操作码， 还有一些操作码则保留给调试操作。

操作码， 下面称为 `指令`, 主要由`类型前缀`和`操作名称`两部分组成。

> 例如，'`i`' 前缀代表 ‘`integer`’，所以，'`iadd`' 很容易理解, 表示对整数执行加法运算。

根据指令的性质，主要分为四个大类：

1. 栈操作指令，包括与局部变量交互的指令
2. 程序流程控制指令
3. 对象操作指令，包括方法调用指令
4. 算术运算以及类型转换指令

此外还有一些执行专门任务的指令，比如同步(synchronization)指令，以及抛出异常相关的指令等等。下文会对这些指令进行详细的讲解。

### 4.2 获取字节码清单

可以用 `javap` 工具来获取 class 文件中的指令清单。 `javap` 是标准 JDK 内置的一款工具, 专门用于反编译 class 文件。

让我们从头开始, 先创建一个简单的类，后面再慢慢扩充。

```java
package demo.jvm0104;

public class HelloByteCode {
    public static void main(String[] args) {
        HelloByteCode obj = new HelloByteCode();
    }
}
```

代码很简单, main 方法中 new 了一个对象而已。然后我们编译这个类:

```shell
javac demo/jvm0104/HelloByteCode.java
```

使用 javac 编译 ，或者在 IDEA 或者 Eclipse 等集成开发工具自动编译，基本上是等效的。只要能找到对应的 class 即可。

> javac 不指定 `-d` 参数编译后生成的 `.class` 文件默认和源代码在同一个目录。
>
> 注意: `javac` 工具默认开启了优化功能, 生成的字节码中没有局部变量表(LocalVariableTable)，相当于局部变量名称被擦除。如果需要这些调试信息, 在编译时请加上 `-g` 选项。有兴趣的同学可以试试两种方式的区别，并对比结果。
>
> JDK 自带工具的详细用法, 请使用: `javac -help` 或者 `javap -help` 来查看; 其他类似。

然后使用 `javap` 工具来执行反编译, 获取字节码清单：

```shell
javap -c demo.jvm0104.HelloByteCode
# 或者: 
javap -c demo/jvm0104/HelloByteCode
javap -c demo/jvm0104/HelloByteCode.class
```

javap 还是比较聪明的, 使用包名或者相对路径都可以反编译成功, 反编译后的结果如下所示：

```java
Compiled from "HelloByteCode.java"
public class demo.jvm0104.HelloByteCode {
  public demo.jvm0104.HelloByteCode();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return

  public static void main(java.lang.String[]);
    Code:
       0: new           #2                  // class demo/jvm0104/HelloByteCode
       3: dup
       4: invokespecial #3                  // Method "<init>":()V
       7: astore_1
       8: return
}
```

OK，我们成功获取到了字节码清单, 下面进行简单的解读。

### 4.3 解读字节码清单

可以看到，反编译后的代码清单中, 有一个默认的构造函数 `public demo.jvm0104.HelloByteCode()`, 以及 `main` 方法。

刚学 Java 时我们就知道， 如果不定义任何构造函数，就会有一个默认的无参构造函数，这里再次验证了这个知识点。好吧，这比较容易理解！我们通过查看编译后的 class 文件证实了其中存在默认构造函数，所以这是 Java 编译器生成的， 而不是运行时JVM自动生成的。

自动生成的构造函数，其方法体应该是空的，但这里看到里面有一些指令。为什么呢？

再次回顾 Java 知识, 每个构造函数中都会先调用 `super` 类的构造函数对吧？ 但这不是 JVM 自动执行的, 而是由程序指令控制，所以默认构造函数中也就有一些字节码指令来干这个事情。

基本上，这几条指令就是执行 `super()` 调用；

```java
  public demo.jvm0104.HelloByteCode();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return
```

至于其中解析的 `java/lang/Object` 不用说, 默认继承了 Object 类。这里再次验证了这个知识点，而且这是在编译期间就确定了的。

继续往下看 c,

```cpp
  public static void main(java.lang.String[]);
    Code:
       0: new           #2                  // class demo/jvm0104/HelloByteCode
       3: dup
       4: invokespecial #3                  // Method "<init>":()V
       7: astore_1
       8: return
```

main 方法中创建了该类的一个实例， 然后就 return 了， 关于里面的几个指令， 稍后讲解。

### 4.4 查看 class 文件中的常量池信息

`常量池` 大家应该都听说过, 英文是 `Constant pool`。这里做一个强调: 大多数时候指的是 `运行时常量池`。但运行时常量池里面的常量是从哪里来的呢? 主要就是由 class 文件中的 `常量池结构体` 组成的。

要查看常量池信息, 我们得加一点魔法参数:

```shell
javap -c -verbose demo.jvm0104.HelloByteCode
```

在反编译 class 时，指定 `-verbose` 选项, 则会 `输出附加信息`。

结果如下所示:

```java
Classfile /XXXXXXX/demo/jvm0104/HelloByteCode.class
  Last modified 2019-11-28; size 301 bytes
  MD5 checksum 542cb70faf8b2b512a023e1a8e6c1308
  Compiled from "HelloByteCode.java"
public class demo.jvm0104.HelloByteCode
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref #4.#13 // java/lang/Object."<init>":()V
   #2 = Class #14 // demo/jvm0104/HelloByteCode
   #3 = Methodref #2.#13 // demo/jvm0104/HelloByteCode."<init>":()V
   #4 = Class #15 // java/lang/Object
   #5 = Utf8 <init>
   #6 = Utf8 ()V
   #7 = Utf8 Code
   #8 = Utf8 LineNumberTable
   #9 = Utf8 main
  #10 = Utf8 ([Ljava/lang/String;)V
  #11 = Utf8 SourceFile
  #12 = Utf8 HelloByteCode.java
  #13 = NameAndType #5:#6 // "<init>":()V
  #14 = Utf8 demo/jvm0104/HelloByteCode
  #15 = Utf8 java/lang/Object
{
  public demo.jvm0104.HelloByteCode();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1 // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 3: 0

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=2, args_size=1
         0: new #2 // class demo/jvm0104/HelloByteCode
         3: dup
         4: invokespecial #3 // Method "<init>":()V
         7: astore_1
         8: return
      LineNumberTable:
        line 5: 0
        line 6: 8
}
SourceFile: "HelloByteCode.java"
```

其中显示了很多关于 class 文件信息： 编译时间， MD5 校验和， 从哪个 `.java` 源文件编译得来，符合哪个版本的 Java 语言规范等等。

还可以看到 `ACC_PUBLIC` 和 `ACC_SUPER` 访问标志符。 `ACC_PUBLIC` 标志很容易理解：这个类是 `public` 类，因此用这个标志来表示。

但 `ACC_SUPER` 标志是怎么回事呢？ 这就是历史原因, JDK 1.0 的 BUG 修正中引入 `ACC_SUPER` 标志来修正 `invokespecial` 指令调用 super 类方法的问题，从 Java 1.1 开始， 编译器一般都会自动生成`ACC_SUPER` 标志。

有些同学可能注意到了， 好多指令后面使用了 `#1, #2, #3` 这样的编号。

这就是对常量池的引用。 那常量池里面有些什么呢?

```java
Constant pool:
   #1 = Methodref #4.#13 // java/lang/Object."<init>":()V
   #2 = Class #14 // demo/jvm0104/HelloByteCode
   #3 = Methodref #2.#13 // demo/jvm0104/HelloByteCode."<init>":()V
   #4 = Class #15 // java/lang/Object
   #5 = Utf8 <init>
......
```

这是摘取的一部分内容, 可以看到常量池中的常量定义。还可以进行组合, 一个常量的定义中可以引用其他常量。

比如第一行: `#1 = Methodref #4.#13 // java/lang/Object."<init>":()V`, 解读如下:

- `#1` 常量编号, 该文件中其他地方可以引用。
- `=` 等号就是分隔符.
- `Methodref` 表明这个常量指向的是一个方法；具体是哪个类的哪个方法呢? 类指向的 `#4`, 方法签名指向的 `#13`; 当然双斜线注释后面已经解析出来可读性比较好的说明了。

同学们可以试着解析其他的常量定义。 自己实践加上知识回顾，能有效增加个人的记忆和理解。

总结一下，常量池就是一个常量的大字典，使用编号的方式把程序里用到的各类常量统一管理起来，这样在字节码操作里，只需要引用编号即可。

### 4.5 查看方法信息

在 `javap` 命令中使用 `-verbose` 选项时， 还显示了其他的一些信息。 例如， 关于 `main` 方法的更多信息被打印出来：

```java
  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=2, args_size=1
```

可以看到方法描述: `([Ljava/lang/String;)V`：

- 其中小括号内是入参信息/形参信息；
- 左方括号表述数组；
- `L` 表示对象；
- 后面的`java/lang/String`就是类名称；
- 小括号后面的 `V` 则表示这个方法的返回值是 `void`；
- 方法的访问标志也很容易理解 `flags: ACC_PUBLIC, ACC_STATIC`，表示 public 和 static。

还可以看到执行该方法时需要的栈(stack)深度是多少，需要在局部变量表中保留多少个槽位, 还有方法的参数个数: `stack=2, locals=2, args_size=1`。把上面这些整合起来其实就是一个方法：

> public static void main(java.lang.String[]);
>
> 注：实际上我们一般把一个方法的修饰符+名称+参数类型清单+返回值类型，合在一起叫“方法签名”，即这些信息可以完整的表示一个方法。

稍微往回一点点，看编译器自动生成的无参构造函数字节码:

```java
  public demo.jvm0104.HelloByteCode();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1 // Method java/lang/Object."<init>":()V
         4: return
```

你会发现一个奇怪的地方, 无参构造函数的参数个数居然不是 0: `stack=1, locals=1, args_size=1`。 这是因为在 Java 中, 如果是静态方法则没有 `this` 引用。 对于非静态方法， `this` 将被分配到局部变量表的第 0 号槽位中, 关于局部变量表的细节,下面再进行介绍。

> 有反射编程经验的同学可能比较容易理解: `Method#invoke(Object obj, Object... args)`; 有JavaScript编程经验的同学也可以类比: `fn.apply(obj, args) && fn.call(obj, arg1, arg2);`

### 4.6 线程栈与字节码执行模型

想要深入了解字节码技术，我们需要先对字节码的执行模型有所了解。

JVM 是一台基于栈的计算机器。每个线程都有一个独属于自己的线程栈(JVM stack)，用于存储`栈帧`(Frame)。每一次方法调用，JVM都会自动创建一个栈帧。`栈帧` 由 `操作数栈`， `局部变量数组` 以及一个`class 引用`组成。`class 引用` 指向当前方法在运行时常量池中对应的 class)。

我们在前面反编译的代码中已经看到过这些内容。

![c0463778-bb4c-43ab-9660-558d2897b364.jpg](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/y6bxd.jpg)

`局部变量数组` 也称为 `局部变量表`(LocalVariableTable), 其中包含了方法的参数，以及局部变量。 局部变量数组的大小在编译时就已经确定: 和局部变量+形参的个数有关，还要看每个变量/参数占用多少个字节。操作数栈是一个 LIFO 结构的栈， 用于压入和弹出值。 它的大小也在编译时确定。

有一些操作码/指令可以将值压入“操作数栈”； 还有一些操作码/指令则是从栈中获取操作数，并进行处理，再将结果压入栈。操作数栈还用于接收调用其他方法时返回的结果值。

### 4.7 方法体中的字节码解读

看过前面的示例，细心的同学可能会猜测，方法体中那些字节码指令前面的数字是什么意思，说是序号吧但又不太像，因为他们之间的间隔不相等。看看 main 方法体对应的字节码：

```java
         0: new #2 // class demo/jvm0104/HelloByteCode
         3: dup
         4: invokespecial #3 // Method "<init>":()V
         7: astore_1
         8: return
```

间隔不相等的原因是, 有一部分操作码会附带有操作数, 也会占用字节码数组中的空间。

例如， `new` 就会占用三个槽位: 一个用于存放操作码指令自身，两个用于存放操作数。

因此，下一条指令 `dup` 的索引从 `3` 开始。

如果将这个方法体变成可视化数组，那么看起来应该是这样的：

![2087a5ff-61b1-49ab-889e-698a73ceb41e.jpg](https://learn.lianglianglee.com/专栏/JVM 核心技术 32 讲（完）/assets/2wcmu.jpg)

每个操作码/指令都有对应的十六进制(HEX)表示形式， 如果换成十六进制来表示，则方法体可表示为HEX字符串。例如上面的方法体百世成十六进制如下所示：

![b75bd86b-45c4-4b05-9266-1b7151c7038f.jpg](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/76qr6.jpg)

甚至我们还可以在支持十六进制的编辑器中打开 class 文件，可以在其中找到对应的字符串：

![9f8bf31f-e936-47c6-a3d1-f0c0de0fc898.jpg](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/poywn.jpg)（此图由开源文本编辑软件Atom的hex-view插件生成）

粗暴一点，我们可以通过 HEX 编辑器直接修改字节码，尽管这样做会有风险， 但如果只修改一个数值的话应该会很有趣。

其实要使用编程的方式，方便和安全地实现字节码编辑和修改还有更好的办法，那就是使用 ASM 和 Javassist 之类的字节码操作工具，也可以在类加载器和 Agent 上面做文章，下一节课程会讨论 `类加载器`，其他主题则留待以后探讨。

### 4.8 对象初始化指令：new 指令, init 以及 clinit 简介

我们都知道 `new`是 Java 编程语言中的一个关键字， 但其实在字节码中，也有一个指令叫做 `new`。 当我们创建类的实例时, 编译器会生成类似下面这样的操作码：

```java
         0: new #2 // class demo/jvm0104/HelloByteCode
         3: dup
         4: invokespecial #3 // Method "<init>":()V
```

当你同时看到 `new, dup` 和 `invokespecial` 指令在一起时，那么一定是在创建类的实例对象！

为什么是三条指令而不是一条呢？这是因为：

- `new` 指令只是创建对象，但没有调用构造函数。
- `invokespecial` 指令用来调用某些特殊方法的, 当然这里调用的是构造函数。
- `dup` 指令用于复制栈顶的值。

由于构造函数调用不会返回值，所以如果没有 dup 指令, 在对象上调用方法并初始化之后，操作数栈就会是空的，在初始化之后就会出问题, 接下来的代码就无法对其进行处理。

这就是为什么要事先复制引用的原因，为的是在构造函数返回之后，可以将对象实例赋值给局部变量或某个字段。因此，接下来的那条指令一般是以下几种：

- `astore {N}` or `astore_{N}` – 赋值给局部变量，其中 `{N}` 是局部变量表中的位置。
- `putfield` – 将值赋给实例字段
- `putstatic` – 将值赋给静态字段

在调用构造函数的时候，其实还会执行另一个类似的方法 `<init>` ，甚至在执行构造函数之前就执行了。

还有一个可能执行的方法是该类的静态初始化方法 `<clinit>`， 但 `<clinit>` 并不能被直接调用，而是由这些指令触发的： `new`, `getstatic`, `putstatic` or `invokestatic`。

也就是说，如果创建某个类的新实例， 访问静态字段或者调用静态方法，就会触发该类的静态初始化方法【如果尚未初始化】。

实际上，还有一些情况会触发静态初始化， 详情请参考 JVM 规范： [http://docs.oracle.com/javase/specs/jvms/se8/html/]

### 4.9 栈内存操作指令

有很多指令可以操作方法栈。 前面也提到过一些基本的栈操作指令： 他们将值压入栈，或者从栈中获取值。 除了这些基础操作之外也还有一些指令可以操作栈内存； 比如 `swap` 指令用来交换栈顶两个元素的值。下面是一些示例：

最基础的是 `dup` 和 `pop` 指令。

- `dup` 指令复制栈顶元素的值。
- `pop` 指令则从栈中删除最顶部的值。

还有复杂一点的指令：比如，`swap`, `dup_x1` 和 `dup2_x1`。

- 顾名思义，`swap` 指令可交换栈顶两个元素的值，例如A和B交换位置(图中示例4)；
- `dup_x1` 将复制栈顶元素的值，并在栈顶插入两次(图中示例5)；
- `dup2_x1` 则复制栈顶两个元素的值，并插入第三个值(图中示例6)。

![9d1a9509-c0ca-4320-983c-141257b0ddf5.jpg](https://learn.lianglianglee.com/专栏/JVM 核心技术 32 讲（完）/assets/kg99w.jpg)

`dup_x1` 和 `dup2_x1` 指令看起来稍微有点复杂。而且为什么要设置这种指令呢? 在栈中复制最顶部的值？

请看一个实际案例：怎样交换 2 个 double 类型的值？

需要注意的是，一个 double 值占两个槽位，也就是说如果栈中有两个 double 值，它们将占用 4 个槽位。

要执行交换，你可能想到了 `swap` 指令，但问题是 `swap` 只适用于单字(one-word, 单字一般指 32 位 4 个字节，64 位则是双字)，所以不能处理 double 类型，但 Java 中又没有 swap2 指令。

怎么办呢? 解决方法就是使用 `dup2_x2` 指令，将操作数栈顶部的 double 值，复制到栈底 double 值的下方， 然后再使用 `pop2` 指令弹出栈顶的 double 值。结果就是交换了两个 double 值。 示意图如下图所示:

![17ee9537-a42f-4a49-bb87-9a03735ab83a.jpg](https://learn.lianglianglee.com/专栏/JVM 核心技术 32 讲（完）/assets/yttg7.jpg)

#### `dup`、`dup_x1`、`dup2_x1` 指令补充说明

指令的详细说明可参考 [JVM 规范](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html)：

**dup 指令**

官方说明是：复制栈顶的值，并将复制的值压入栈。

操作数栈的值变化情况（方括号标识新插入的值）：

```css
..., value →
..., value [,value]
```

**dup_x1 指令**

官方说明是：复制栈顶的值，并将复制的值插入到最上面 2 个值的下方。

操作数栈的值变化情况（方括号标识新插入的值）：

```css
..., value2, value1 →
..., [value1,] value2, value1
```

**dup2_x1 指令**

官方说明是：复制栈顶 1 个 64 位/或 2 个 32 位的值, 并将复制的值按照原始顺序，插入原始值下面一个 32 位值的下方。

操作数栈的值变化情况（方括号标识新插入的值）：

```bash
# 情景 1: value1, value2, and value3 都是分组 1 的值(32 位元素)
..., value3, value2, value1 →
..., [value2, value1,] value3, value2, value1

# 情景 2: value1 是分组 2 的值(64 位,long 或double), value2 是分组 1 的值(32 位元素)
..., value2, value1 →
..., [value1,] value2, value1
```

> [Table 2.11.1-B 实际类型与 JVM 计算类型映射和分组](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.11.1)

| 实际类型      | JVM 计算类型  | 类型分组 |
| :------------ | :------------ | :------- |
| boolean       | int           | 1        |
| byte          | int           | 1        |
| char          | int           | 1        |
| short         | int           | 1        |
| int           | int           | 1        |
| float         | float         | 1        |
| reference     | reference     | 1        |
| returnAddress | returnAddress | 1        |
| long          | long          | 2        |
| double        | double        | 2        |

### 4.10 局部变量表

`stack` 主要用于执行指令，而局部变量则用来保存中间结果，两者之间可以直接交互。

让我们编写一个复杂点的示例：

第一步，先编写一个计算移动平均数的类:

```java
package demo.jvm0104;
//移动平均数
public class MovingAverage {
    private int count = 0;
    private double sum = 0.0D;
    public void submit(double value){
        this.count ++;
        this.sum += value;
    }
    public double getAvg(){
        if(0 == this.count){ return sum;}
        return this.sum/this.count;
    }
}
```

第二步，然后写一个类来调用:

```java
package demo.jvm0104;
public class LocalVariableTest {
    public static void main(String[] args) {
        MovingAverage ma = new MovingAverage();
        int num1 = 1;
        int num2 = 2;
        ma.submit(num1);
        ma.submit(num2);
        double avg = ma.getAvg();
    }
}
```

其中 main 方法中向 `MovingAverage` 类的实例提交了两个数值，并要求其计算当前的平均值。

然后我们需要编译（还记得前面提到, 生成调试信息的 `-g` 参数吗）。

```shell
javac -g demo/jvm0104/*.java
```

然后使用 javap 反编译:

```shell
javap -c -verbose demo/jvm0104/LocalVariableTest
```

看 main 方法对应的字节码：

```java
  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=3, locals=6, args_size=1
         0: new           #2                  // class demo/jvm0104/MovingAverage
         3: dup
         4: invokespecial #3                  // Method demo/jvm0104/MovingAverage."<init>":()V
         7: astore_1
         8: iconst_1
         9: istore_2
        10: iconst_2
        11: istore_3
        12: aload_1
        13: iload_2
        14: i2d
        15: invokevirtual #4                  // Method demo/jvm0104/MovingAverage.submit:(D)V
        18: aload_1
        19: iload_3
        20: i2d
        21: invokevirtual #4                  // Method demo/jvm0104/MovingAverage.submit:(D)V
        24: aload_1
        25: invokevirtual #5                  // Method demo/jvm0104/MovingAverage.getAvg:()D
        28: dstore        4
        30: return
      LineNumberTable:
        line 5: 0
        line 6: 8
        line 7: 10
        line 8: 12
        line 9: 18
        line 10: 24
        line 11: 30
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      31     0  args   [Ljava/lang/String;
            8      23     1    ma   Ldemo/jvm0104/MovingAverage;
           10      21     2  num1   I
           12      19     3  num2   I
           30       1     4   avg   D
```

------

- 编号 `0` 的字节码 `new`, 创建 `MovingAverage` 类的对象;
- 编号 `3` 的字节码 `dup` 复制栈顶引用值。
- 编号 `4` 的字节码 `invokespecial` 执行对象初始化。
- 编号 `7` 开始, 使用 `astore_1` 指令将引用地址值(addr.)存储(store)到编号为`1`的局部变量中： `astore_1` 中的 `1` 指代 LocalVariableTable 中`ma`对应的槽位编号，
- 编号8开始的指令： `iconst_1` 和 `iconst_2` 用来将常量值`1`和`2`加载到栈里面， 并分别由指令 `istore_2` 和 `istore_3` 将它们存储到在 LocalVariableTable 的槽位 2 和槽位 3 中。

```java
         8: iconst_1
         9: istore_2
        10: iconst_2
        11: istore_3
```

请注意，store 之类的指令调用实际上从栈顶删除了一个值。 这就是为什么再次使用相同值时，必须再加载(load)一次的原因。

例如在上面的字节码中，调用 `submit` 方法之前， 必须再次将参数值加载到栈中：

```java
        12: aload_1
        13: iload_2
        14: i2d
        15: invokevirtual #4                  // Method demo/jvm0104/MovingAverage.submit:(D)V
```

调用 `getAvg()` 方法后，返回的结果位于栈顶，然后使用 `dstore` 将 `double` 值保存到本地变量`4`号槽位，这里的`d`表示目标变量的类型为`double`。

```java
        24: aload_1
        25: invokevirtual #5                  // Method demo/jvm0104/MovingAverage.getAvg:()D
        28: dstore        4
```

关于 `LocalVariableTable` 有个有意思的事情，就是最前面的槽位会被方法参数占用。

在这里，因为 `main` 是静态方法，所以槽位0中并没有设置为 `this` 引用的地址。 但是对于非静态方法来说， `this` 会将分配到第 0 号槽位中。

> 再次提醒: 有过反射编程经验的同学可能比较容易理解: `Method#invoke(Object obj, Object... args)`; 有JavaScript编程经验的同学也可以类比: `fn.apply(obj, args) && fn.call(obj, arg1, arg2);`![1e17af1a-6b6b-4992-a75c-9eac959bc467.jpg](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/te9bw.jpg)

理解这些字节码的诀窍在于:

给局部变量赋值时，需要使用相应的指令来进行 `store`，如 `astore_1`。`store` 类的指令都会删除栈顶值。 相应的 `load` 指令则会将值从局部变量表压入操作数栈，但并不会删除局部变量中的值。

### 4.11 流程控制指令

流程控制指令主要是分支和循环在用, 根据检查条件来控制程序的执行流程。

一般是 `If-Then-Else` 这种三元运算符(ternary operator)， Java中的各种循环，甚至异常处的理操作码都可归属于 程序流程控制。

然后，我们再增加一个示例，用循环来提交给 MovingAverage 类一定数量的值：

```java
package demo.jvm0104;
public class ForLoopTest {
    private static int[] numbers = {1, 6, 8};
    public static void main(String[] args) {
        MovingAverage ma = new MovingAverage();
        for (int number : numbers) {
            ma.submit(number);
        }
        double avg = ma.getAvg();
    }
}
```

同样执行编译和反编译:

```shell
javac -g demo/jvm0104/*.java
javap -c -verbose demo/jvm0104/ForLoopTest
```

因为 `numbers` 是本类中的 `static` 属性， 所以对应的字节码如下所示:

```yaml
         0: new           #2                  // class demo/jvm0104/MovingAverage
         3: dup
         4: invokespecial #3                  // Method demo/jvm0104/MovingAverage."<init>":()V
         7: astore_1
         8: getstatic     #4                  // Field numbers:[I
        11: astore_2
        12: aload_2
        13: arraylength
        14: istore_3
        15: iconst_0
        16: istore        4
        18: iload         4
        20: iload_3
        21: if_icmpge     43
        24: aload_2
        25: iload         4
        27: iaload
        28: istore        5
        30: aload_1
        31: iload         5
        33: i2d
        34: invokevirtual #5                  // Method demo/jvm0104/MovingAverage.submit:(D)V
        37: iinc          4, 1
        40: goto          18
        43: aload_1
        44: invokevirtual #6                  // Method demo/jvm0104/MovingAverage.getAvg:()D
        47: dstore_2
        48: return
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
           30       7     5 number   I
            0      49     0  args   [Ljava/lang/String;
            8      41     1    ma   Ldemo/jvm0104/MovingAverage;
           48       1     2   avg   D
```

位置 [8~16] 的指令用于循环控制。 我们从代码的声明从上往下看, 在最后面的LocalVariableTable 中:

- `0` 号槽位被 main 方法的参数 `args` 占据了。
- `1` 号槽位被 `ma` 占用了。
- `5` 号槽位被 `number` 占用了。
- `2` 号槽位是for循环之后才被 `avg` 占用的。

那么中间的 `2`,`3`,`4` 号槽位是谁霸占了呢? 通过分析字节码指令可以看出，在 `2`,`3`,`4` 槽位有 3 个匿名的局部变量(`astore_2`, `istore_3`, `istore 4`等指令)。

- `2`号槽位的变量保存了 numbers 的引用值，占据了 `2`号槽位。
- `3`号槽位的变量, 由 `arraylength` 指令使用, 得出循环的长度。
- `4`号槽位的变量, 是循环计数器， 每次迭代后使用 `iinc` 指令来递增。

> 如果我们的 JDK 版本再老一点, 则会在 `2`,`3`,`4` 槽位发现三个源码中没有出现的变量： `arr$`, `len$`, `i$`， 也就是循环变量。

循环体中的第一条指令用于执行 循环计数器与数组长度 的比较：

```yaml
        18: iload         4
        20: iload_3
        21: if_icmpge     43
```

这段指令将局部变量表中 `4`号槽位 和 `3`号槽位的值加载到栈中，并调用 `if_icmpge` 指令来比较他们的值。

【`if_icmpge` 解读: if, integer, compare, great equal】, 如果一个数的值大于或等于另一个值，则程序执行流程跳转到`pc=43`的地方继续执行。

在这个例子中就是， 如果`4`号槽位的值 大于或等于 `3`号槽位的值, 循环就结束了，这里 43 位置对于的是循环后面的代码。如果条件不成立，则循环进行下一次迭代。

在循环体执行完，它的循环计数器加 1，然后循环跳回到起点以再次验证循环条件：

```cpp
        37: iinc          4, 1   // 4号槽位的值加1
        40: goto          18     // 跳到循环开始的地方
```

### 4.12 算术运算指令与类型转换指令

Java 字节码中有许多指令可以执行算术运算。实际上，指令集中有很大一部分表示都是关于数学运算的。对于所有数值类型(`int`, `long`, `double`, `float`)，都有加，减，乘，除，取反的指令。

那么 `byte` 和 `char`, `boolean` 呢? JVM 是当做 `int` 来处理的。另外还有部分指令用于数据类型之间的转换。

> 算术操作码和类型![30666bbb-50a0-4114-9675-b0626fd0167b.jpg](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/58uua.jpg)

当我们想将 `int` 类型的值赋值给 `long` 类型的变量时，就会发生类型转换。

> 类型转换操作码![e8c82cb5-6e86-4d52-90cc-40cde0fabaa0.jpg](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/yzjfe.jpg)

在前面的示例中， 将 `int` 值作为参数传递给实际上接收 `double` 的 `submit()` 方法时，可以看到, 在实际调用该方法之前，使用了类型转换的操作码：

```yaml
        31: iload         5
        33: i2d
        34: invokevirtual #5                  // Method demo/jvm0104/MovingAverage.submit:(D)V
```

也就是说, 将一个 int 类型局部变量的值, 作为整数加载到栈中，然后用 `i2d` 指令将其转换为 `double` 值，以便将其作为参数传给`submit`方法。

唯一不需要将数值load到操作数栈的指令是 `iinc`，它可以直接对 `LocalVariableTable` 中的值进行运算。 其他的所有操作均使用栈来执行。

### 4.13 方法调用指令和参数传递

前面部分稍微提了一下方法调用： 比如构造函数是通过 `invokespecial` 指令调用的。

这里列举了各种用于方法调用的指令：

- `invokestatic`，顾名思义，这个指令用于调用某个类的静态方法，这也是方法调用指令中最快的一个。
- `invokespecial`, 我们已经学过了, `invokespecial` 指令用来调用构造函数，但也可以用于调用同一个类中的 private 方法, 以及可见的超类方法。
- `invokevirtual`，如果是具体类型的目标对象，`invokevirtual`用于调用公共，受保护和打包私有方法。
- `invokeinterface`，当要调用的方法属于某个接口时，将使用 `invokeinterface` 指令。

> 那么 `invokevirtual` 和 `invokeinterface` 有什么区别呢？这确实是个好问题。 为什么需要 `invokevirtual` 和 `invokeinterface` 这两种指令呢? 毕竟所有的接口方法都是公共方法, 直接使用 `invokevirtual` 不就可以了吗？

这么做是源于对方法调用的优化。JVM 必须先解析该方法，然后才能调用它。

- 使用 `invokestatic` 指令，JVM 就确切地知道要调用的是哪个方法：因为调用的是静态方法，只能属于一个类。
- 使用 `invokespecial` 时， 查找的数量也很少， 解析也更加容易， 那么运行时就能更快地找到所需的方法。

使用 `invokevirtual` 和 `invokeinterface` 的区别不是那么明显。想象一下，类定义中包含一个方法定义表， 所有方法都有位置编号。下面的示例中：A 类包含 method1 和 method2 方法； 子类B继承A，继承了 method1，覆写了 method2，并声明了方法 method3。

> 请注意，method1 和 method2 方法在类 A 和类 B 中处于相同的索引位置。

```java
class A
    1: method1
    2: method2
class B extends A
    1: method1
    2: method2
    3: method3
```

那么，在运行时只要调用 method2，一定是在位置 2 处找到它。

现在我们来解释`invokevirtual` 和 `invokeinterface` 之间的本质区别。

假设有一个接口 X 声明了 methodX 方法, 让 B 类在上面的基础上实现接口 X：

```java
class B extends A implements X
    1: method1
    2: method2
    3: method3
    4: methodX
```

新方法 methodX 位于索引 4 处，在这种情况下，它看起来与 method3 没什么不同。

但如果还有另一个类 C 也实现了 X 接口，但不继承 A，也不继承 B：

```java
class C implements X
    1: methodC
    2: methodX
```

类 C 中的接口方法位置与类 B 的不同，这就是为什么运行时在 `invokinterface` 方面受到更多限制的原因。 与 `invokinterface` 相比， `invokevirtual` 针对具体的类型方法表是固定的，所以每次都可以精确查找，效率更高（具体的分析讨论可以参见参考材料的第一个链接）。

### 4.14 JDK7 新增的方法调用指令 invokedynamic

Java 虚拟机的字节码指令集在 JDK7 之前一直就只有前面提到的 4 种指令（invokestatic，invokespecial，invokevirtual，invokeinterface）。随着 JDK 7 的发布，字节码指令集新增了`invokedynamic`指令。这条新增加的指令是实现“动态类型语言”（Dynamically Typed Language）支持而进行的改进之一，同时也是 JDK 8 以后支持的 lambda 表达式的实现基础。

为什么要新增加一个指令呢？

我们知道在不改变字节码的情况下，我们在 Java 语言层面想调用一个类 A 的方法 m，只有两个办法：

- 使用`A a=new A(); a.m()`，拿到一个 A 类型的实例，然后直接调用方法；
- 通过反射，通过 A.class.getMethod 拿到一个 Method，然后再调用这个`Method.invoke`反射调用；

这两个方法都需要显式的把方法 m 和类型 A 直接关联起来，假设有一个类型 B，也有一个一模一样的方法签名的 m 方法，怎么来用这个方法在运行期指定调用 A 或者 B 的 m 方法呢？这个操作在 JavaScript 这种基于原型的语言里或者是 C# 这种有函数指针/方法委托的语言里非常常见，Java 里是没有直接办法的。Java 里我们一般建议使用一个 A 和 B 公有的接口 IC，然后 IC 里定义方法 m，A 和 B 都实现接口 IC，这样就可以在运行时把 A 和 B 都当做 IC 类型来操作，就同时有了方法 m，这样的“强约束”带来了很多额外的操作。

而新增的 invokedynamic 指令，配合新增的方法句柄（Method Handles，它可以用来描述一个跟类型 A 无关的方法 m 的签名，甚至不包括方法名称，这样就可以做到我们使用方法 m 的签名，但是直接执行的时候调用的是相同签名的另一个方法 b），可以在运行时再决定由哪个类来接收被调用的方法。在此之前，只能使用反射来实现类似的功能。该指令使得可以出现基于 JVM 的动态语言，让 jvm 更加强大。而且在 JVM 上实现动态调用机制，不会破坏原有的调用机制。这样既很好的支持了 Scala、Clojure 这些 JVM 上的动态语言，又可以支持代码里的动态 lambda 表达式。

RednaxelaFX 评论说：

> 简单来说就是以前设计某些功能的时候把做法写死在了字节码里，后来想改也改不了了。 所以这次给 lambda 语法设计翻译到字节码的策略是就用 invokedynamic 来作个弊，把实际的翻译策略隐藏在 JDK 的库的实现里（metafactory）可以随时改，而在外部的标准上大家只看到一个固定的 invokedynamic。

### 参考材料

- Why Should I Know About Java Bytecode: https://jrebel.com/rebellabs/rebel-labs-report-mastering-java-bytecode-at-the-core-of-the-jvm/
- 轻松看懂Java字节码: https://juejin.im/post/5aca2c366fb9a028c97a5609
- invokedynamic指令：https://www.cnblogs.com/wade-luffy/p/6058087.html
- Java 8的Lambda表达式为什么要基于invokedynamic？：https://www.zhihu.com/question/39462935
- Invokedynamic：https://www.jianshu.com/p/ad7d572196a8
- JVM之动态方法调用：invokedynamic： https://ifeve.com/jvm%E4%B9%8B%E5%8A%A8%E6%80%81%E6%96%B9%E6%B3%95%E8%B0%83%E7%94%A8%EF%BC%9Ainvokedynamic/

[上一页](https://learn.lianglianglee.com/专栏/JVM 核心技术 32 讲（完）/04 JVM 基础知识：不积跬步，无以至千里.md)

[下一页](https://learn.lianglianglee.com/专栏/JVM 核心技术 32 讲（完）/06 Java 类加载器：山不辞土，故能成其高.md)

© 2019 - 2023 [Liangliang Lee](mailto:lll941107@gmail.com). Powered by [Vert.x](https://vertx.io/) and [hexo-theme-book](https://github.com/kaiiiz/hexo-theme-book).



## 5 Java 类加载器：山不辞土，故能成其高

前面我们学习了 Java 字节码，写好的代码经过编译变成了字节码，并且可以打包成 Jar 文件。

然后就可以让 JVM 去加载需要的字节码，变成持久代/元数据区上的 Class 对象，接着才会执行我们的程序逻辑。

我们可以用 Java 命令指定主启动类，或者是 Jar 包，通过约定好的机制，JVM 就会自动去加载对应的字节码（可能是 class 文件，也可能是 Jar 包）。

我们知道 Jar 包打开后实际上就等价于一个文件夹，里面有很多 class 文件和资源文件，但是为了方便就打包成 zip 格式。 当然解压了之后照样可以直接用 java 命令来执行。

```shell
$ java Hello
```

或者把 Hello.class 和依赖的其他文件一起打包成 jar 文件:

> 示例 1: 将 class 文件和 java 源文件归档到一个名为 hello.jar 的档案中: `jar cvf hello.jar Hello.class Hello.java` 示例 2: 归档的同时，通过 `e` 选项指定 jar 的启动类 `Hello`: `jar cvfe hello.jar Hello Hello.class Hello.java`

然后通过 `-jar` 选项来执行jar包:

```shell
$ java -jar hello.jar
```

当然我们回过头来还可以把 jar 解压了，再用上面的 java 命令来运行。

运行 java 程序的第一步就是加载 class 文件/或输入流里面包含的字节码。

1. 类的生命周期和加载过程
2. 类加载时机
3. 类加载机制
4. 自定义类加载器示例
5. 一些实用技巧

- 如何排查找不到 Jar 包的问题？
- 如何排查类的方法不一致的问题？
- 怎么看到加载了哪些类，以及加载顺序？
- 怎么调整或修改 ext 和本地加载路径？
- 怎么运行期加载额外的 jar 包或者 class 呢？

按照 Java 语言规范和 Java 虚拟机规范的定义, 我们用 “`类加载`(Class Loading)” 来表示: 将 class/interface 名称映射为 Class 对象的一整个过程。 这个过程还可以划分为更具体的阶段: 加载，链接和初始化(loading, linking and initializing)。

那么加载 class 的过程中到底发生了些什么呢？我们来详细看看。

### 5.1 类的生命周期和加载过程

![3de64ff2-77de-4468-af3a-c61bbb8cd944.png](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/6zd6i.png)

一个类在 JVM 里的生命周期有 7 个阶段，分别是加载（Loading）、验证（Verification）、准备（Preparation）、解析（Resolution）、初始化（Initialization）、使用（Using）、卸载（Unloading）。

其中前五个部分（加载，验证，准备，解析，初始化）统称为类加载，下面我们就分别来说一下这五个过程。

1）**加载** 加载阶段也可以称为“装载”阶段。 这个阶段主要的操作是： 根据明确知道的 **class 完全限定名, 来获取二进制 classfile 格式的字节流，**简单点说就是找到*文件系统中*/*jar 包*中/或存在于任何地方的“`class 文件`”。 如果找不到二进制表示形式，则会抛出 `NoClassDefFound` 错误。

装载阶段并不会检查 classfile 的语法和格式。 类加载的整个过程主要由 JVM 和 Java 的类加载系统共同完成， 当然具体到 loading 阶段则是由 JVM 与具体的某一个类加载器（java.lang.classLoader）协作完成的。

2）**校验** 链接过程的第一个阶段是 `校验`，确保 class 文件里的字节流信息符合当前虚拟机的要求，不会危害虚拟机的安全。

校验过程检查 classfile 的语义，判断常量池中的符号，并执行类型检查， 主要目的是判断字节码的合法性，比如 magic number, 对版本号进行验证。 这些检查过程中可能会抛出 `VerifyError`， `ClassFormatError` 或 `UnsupportedClassVersionError`。

因为 classfile 的验证属是链接阶段的一部分，所以这个过程中可能需要加载其他类，在某个类的加载过程中，JVM 必须加载其所有的超类和接口。

如果类层次结构有问题（例如，该类是自己的超类或接口,死循环了），则 JVM 将抛出 `ClassCircularityError`。 而如果实现的接口并不是一个 interface，或者声明的超类是一个 interface，也会抛出 `IncompatibleClassChangeError`。

3）**准备**

然后进入准备阶段，这个阶段将会创建静态字段, 并将其初始化为标准默认值(比如`null`或者`0 值`)，并分配方法表，即在方法区中分配这些变量所使用的内存空间。

请注意，准备阶段并未执行任何 Java 代码。

例如：

> public static int i = 1；

在准备阶段`i`的值会被初始化为 0，后面在类初始化阶段才会执行赋值为 1；但是下面如果使用 final 作为静态常量，某些 JVM 的行为就不一样了：

> `public static final int i = 1；` 对应常量 i，在准备阶段就会被赋值 1，其实这样还是比较 puzzle，例如其他语言（C#）有直接的常量关键字 const，让告诉编译器在编译阶段就替换成常量，类似于宏指令，更简单。

4）**解析** 然后进入可选的解析符号引用阶段。 也就是解析常量池，主要有以下四种：类或接口的解析、字段解析、类方法解析、接口方法解析。

简单的来说就是我们编写的代码中，当一个变量引用某个对象的时候，这个引用在 `.class` 文件中是以符号引用来存储的（相当于做了一个索引记录）。

在解析阶段就需要将其解析并链接为直接引用（相当于指向实际对象）。如果有了直接引用，那引用的目标必定在堆中存在。

加载一个 class 时, 需要加载所有的 super 类和 super 接口。

5）**初始化** JVM 规范明确规定, 必须在类的首次“主动使用”时才能执行类初始化。

初始化的过程包括执行：

- 类构造器方法
- static 静态变量赋值语句
- static 静态代码块

如果是一个子类进行初始化会先对其父类进行初始化，保证其父类在子类之前进行初始化。所以其实在 java 中初始化一个类，那么必然先初始化过 `java.lang.Object` 类，因为所有的 java 类都继承自 java.lang.Object。

> 只要我们尊重语言的语义，在执行下一步操作之前完成 装载，链接和初始化这些步骤，如果出错就按照规定抛出相应的错误，类加载系统完全可以根据自己的策略，灵活地进行符号解析等链接过程。 为了提高性能，HotSpot JVM 通常要等到类初始化时才去装载和链接类。 因此，如果 A 类引用了 B 类，那么加载 A 类并不一定会去加载 B 类（除非需要进行验证）。 主动对 B 类执行第一条指令时才会导致 B 类的初始化，这就需要先完成对 B 类的装载和链接。

### 5.2 类加载时机

了解了类的加载过程，我们再看看类的初始化何时会被触发呢？JVM 规范枚举了下述多种触发情况：

- 当虚拟机启动时，初始化用户指定的主类，就是启动执行的 main 方法所在的类；
- 当遇到用以新建目标类实例的 new 指令时，初始化 new 指令的目标类，就是 new 一个类的时候要初始化；
- 当遇到调用静态方法的指令时，初始化该静态方法所在的类；
- 当遇到访问静态字段的指令时，初始化该静态字段所在的类；
- 子类的初始化会触发父类的初始化；
- 如果一个接口定义了 default 方法，那么直接实现或者间接实现该接口的类的初始化，会触发该接口的初始化；
- 使用反射 API 对某个类进行反射调用时，初始化这个类，其实跟前面一样，反射调用要么是已经有实例了，要么是静态方法，都需要初始化；
- 当初次调用 MethodHandle 实例时，初始化该 MethodHandle 指向的方法所在的类。

同时以下几种情况不会执行类初始化：

- 通过子类引用父类的静态字段，只会触发父类的初始化，而不会触发子类的初始化。
- 定义对象数组，不会触发该类的初始化。
- 常量在编译期间会存入调用类的常量池中，本质上并没有直接引用定义常量的类，不会触发定义常量所在的类。
- 通过类名获取 Class 对象，不会触发类的初始化，Hello.class 不会让 Hello 类初始化。
- 通过 Class.forName 加载指定类时，如果指定参数 initialize 为 false 时，也不会触发类初始化，其实这个参数是告诉虚拟机，是否要对类进行初始化。Class.forName(“jvm.Hello”)默认会加载 Hello 类。
- 通过 ClassLoader 默认的 loadClass 方法，也不会触发初始化动作（加载了，但是不初始化）。

示例: 诸如 Class.forName(), classLoader.loadClass() 等 Java API, 反射API, 以及 JNI_FindClass 都可以启动类加载。 JVM 本身也会进行类加载。 比如在 JVM 启动时加载核心类，java.lang.Object, java.lang.Thread 等等。

### 5.3 类加载器机制

类加载过程可以描述为“通过一个类的全限定名 a.b.c.XXClass 来获取描述此类的 Class 对象”，这个过程由“类加载器（ClassLoader）”来完成。这样的好处在于，子类加载器可以复用父加载器加载的类。系统自带的类加载器分为三种：

- 启动类加载器（BootstrapClassLoader）
- 扩展类加载器（ExtClassLoader）(JDK 8 的是这个，JDK9更新的是平台类加载器)
- 应用类加载器（AppClassLoader）

**一般启动类加载器是由 JVM 内部实现的，在 Java 的 API 里无法拿到，**直接调用getClassLocader会显示为null，但是我们可以侧面看到和影响它（后面的内容会演示）。后 2 种类加载器在 Oracle Hotspot JVM 里，都是在中`sun.misc.Launcher`定义的，扩展类加载器和应用类加载器一般都继承自`URLClassLoader`类，这个类也默认实现了从各种不同来源加载 class 字节码转换成 Class 的方法。

![c32f4986-0e72-4268-a90a-7451e1931161.png](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/esz0u.png)

1. 启动类加载器（bootstrap class loader）: 它用来加载 Java 的核心类，是用原生 C++ 代码来实现的，并不继承自 java.lang.ClassLoader（负责加载JDK中jre/lib/rt.jar里所有的class）。**它可以看做是 JVM 自带的，我们再代码层面无法直接获取到启动类加载器的引用，所以不允许直接操作它， 如果打印出来就是个 `null`。**举例来说，java.lang.String 是由启动类加载器加载的，所以 String.class.getClassLoader() 就会返回 null。但是后面可以看到可以通过命令行参数影响它加载什么。
2. 扩展类加载器（extensions class loader）：它负责加载 JRE 的扩展目录，lib/ext 或者由 java.ext.dirs 系统属性指定的目录中的 JAR 包的类，代码里直接获取它的父类加载器为 null（因为无法拿到启动类加载器）。
3. 应用类加载器（app class loader）：它负责在 JVM 启动时加载来自 Java 命令的 -classpath 或者 -cp 选项、java.class.path 系统属性指定的 jar 包和类路径。在应用程序代码里可以通过 ClassLoader 的静态方法 getSystemClassLoader() 来获取应用类加载器。如果没有特别指定，则在没有使用自定义类加载器情况下，用户自定义的类都由此加载器加载。

此外还可以自定义类加载器。如果用户自定义了类加载器，则自定义类加载器都以应用类加载器作为父加载器。应用类加载器的父类加载器为扩展类加载器。这些类加载器是有层次关系的，启动加载器又叫根加载器，是扩展加载器的父加载器，但是直接从 ExClassLoader 里拿不到它的引用，同样会返回 null。

![8a806e88-cd41-4a28-b552-76efb0a1fdba.png](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/csrk7-20230610072849805.png)

类加载机制有三个特点：

1. 双亲委托：当一个自定义类加载器需要加载一个类，比如 java.lang.String，它很懒，不会一上来就直接试图加载它，**而是先委托自己的父加载器去加载，父加载器如果发现自己还有父加载器，会一直往前找**，这样只要上级加载器，比如启动类加载器已经加载了某个类比如 java.lang.String，所有的子加载器都不需要自己加载了。如果几个类加载器都没有加载到指定名称的类，那么会抛出 ClassNotFountException 异常。
2. 负责依赖：如果一个加载器在加载某个类的时候，发现这个类依赖于另外几个类或接口，也会去尝试加载这些依赖项。
3. 缓存加载：为了提升加载效率，消除重复加载，一旦某个类被一个类加载器加载，那么它会缓存这个加载结果，不会重复加载。

### 5.4 自定义类加载器示例

同时我们可以自行实现类加载器来加载其他格式的类，对加载方式、加载数据的格式进行自定义处理，只要能通过 classloader 返回一个 Class 实例即可。这就大大增强了加载器灵活性。比如我们试着实现一个可以用来处理简单加密的字节码的类加载器，用来保护我们的 class 字节码文件不被使用者直接拿来破解。

我们先来看看我们希望加载的一个 Hello 类：

```csharp
package jvm;

public class Hello {
    static {
        System.out.println("Hello Class Initialized!");
    }
}
```

这个 Hello 类非常简单，就是在自己被初始化的时候，打印出来一句“Hello Class Initialized!”。假设这个类的内容非常重要，我们不想把编译到得到的 Hello.class 给别人，但是我们还是想别人可以调用或执行这个类，应该怎么办呢？一个简单的思路是，我们把这个类的 class 文件二进制作为字节流先加密一下，然后尝试通过自定义的类加载器来加载加密后的数据。为了演示简单，我们使用 jdk 自带的 Base64 算法，把字节码加密成一个文本。在下面这个例子里，我们实现一个 HelloClassLoader，它继承自 ClassLoader 类，但是我们希望它通过我们提供的一段 Base64 字符串，来还原出来，并执行我们的 Hello 类里的打印一串字符串的逻辑。

```java
package jvm;

import java.util.Base64;

public class HelloClassLoader extends ClassLoader {

    public static void main(String[] args) {
        try {
            new HelloClassLoader().findClass("jvm.Hello").newInstance(); // 加载并初始化Hello类
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        String helloBase64 = "yv66vgAAADQAHwoABgARCQASABMIABQKABUAFgcAFwcAGAEABjxpbml0PgEAAygpVgEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUBABJMb2N" +
                "hbFZhcmlhYmxlVGFibGUBAAR0aGlzAQALTGp2bS9IZWxsbzsBAAg8Y2xpbml0PgEAClNvdXJjZUZpbGUBAApIZWxsby5qYXZhDAAHAAgHABkMABoAGwEAGEhlb" +
                "GxvIENsYXNzIEluaXRpYWxpemVkIQcAHAwAHQAeAQAJanZtL0hlbGxvAQAQamF2YS9sYW5nL09iamVjdAEAEGphdmEvbGFuZy9TeXN0ZW0BAANvdXQBABVMamF2" +
                "YS9pby9QcmludFN0cmVhbTsBABNqYXZhL2lvL1ByaW50U3RyZWFtAQAHcHJpbnRsbgEAFShMamF2YS9sYW5nL1N0cmluZzspVgAhAAUABgAAAAAAAgABAAcACA" +
                "ABAAkAAAAvAAEAAQAAAAUqtwABsQAAAAIACgAAAAYAAQAAAAMACwAAAAwAAQAAAAUADAANAAAACAAOAAgAAQAJAAAAJQACAAAAAAAJsgACEgO2AASxAAAAAQAK" +
                "AAAACgACAAAABgAIAAcAAQAPAAAAAgAQ";

        byte[] bytes = decode(helloBase64);
        return defineClass(name,bytes,0,bytes.length);
    }

    public byte[] decode(String base64){
        return Base64.getDecoder().decode(base64);
    }

}
```

直接执行这个类：

> $ java jvm.HelloClassLoader Hello Class Initialized!

可以看到达到了我们的目的，成功执行了Hello类的代码，但是完全不需要有Hello这个类的class文件。此外，需要说明的是两个没有关系的自定义类加载器之间加载的类是不共享的（只共享父类加载器，兄弟之间不共享），这样就可以实现不同的类型沙箱的隔离性，我们可以用多个类加载器，各自加载同一个类的不同版本，大家可以相互之间不影响彼此，从而在这个基础上可以实现类的动态加载卸载，热插拔的插件机制等，具体信息大家可以参考OSGi等模块化技术。

### 5.5 一些实用技巧

#### 1）如何排查找不到Jar包的问题？

有时候我们会面临明明已经把某个jar加入到了环境里，可以运行的时候还是找不到。那么我们有没有一种方法，可以直接看到各个类加载器加载了哪些jar，以及把哪些路径加到了classpath里？答案是肯定的，代码如下：

```java
package jvm;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

public class JvmClassLoaderPrintPath {

    public static void main(String[] args) {

        // 启动类加载器
        URL[] urls = sun.misc.Launcher.getBootstrapClassPath().getURLs();
        System.out.println("启动类加载器");
        for(URL url : urls) {
            System.out.println(" ==> " +url.toExternalForm());
        }

        // 扩展类加载器
        printClassLoader("扩展类加载器", JvmClassLoaderPrintPath.class.getClassLoader().getParent());

        // 应用类加载器
        printClassLoader("应用类加载器", JvmClassLoaderPrintPath.class.getClassLoader());

    }

    public static void printClassLoader(String name, ClassLoader CL){
        if(CL != null) {
            System.out.println(name + " ClassLoader -> " + CL.toString());
            printURLForClassLoader(CL);
        }else{
            System.out.println(name + " ClassLoader -> null");
        }
    }

    public static void printURLForClassLoader(ClassLoader CL){

        Object ucp = insightField(CL,"ucp");
        Object path = insightField(ucp,"path");
        ArrayList ps = (ArrayList) path;
        for (Object p : ps){
            System.out.println(" ==> " + p.toString());
        }
    }

    private static Object insightField(Object obj, String fName) {
        try {
            Field f = null;
            if(obj instanceof URLClassLoader){
                f = URLClassLoader.class.getDeclaredField(fName);
            }else{
                f = obj.getClass().getDeclaredField(fName);
            }
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
```

代码执行结果如下：

```bash
启动类加载器
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/resources.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/rt.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/sunrsasign.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/jsse.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/jce.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/charsets.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/jfr.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/classes

扩展类加载器 ClassLoader -> sun.misc.Launcher$ExtClassLoader@15db9742
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/access-bridge-64.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/cldrdata.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/dnsns.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/jaccess.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/jfxrt.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/localedata.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/nashorn.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/sunec.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/sunjce_provider.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/sunmscapi.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/sunpkcs11.jar
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/ext/zipfs.jar

应用类加载器 ClassLoader -> sun.misc.Launcher$AppClassLoader@73d16e93
   ==> file:/D:/git/studyjava/build/classes/java/main/
   ==> file:/D:/git/studyjava/build/resources/main
```

从打印结果，我们可以看到三种类加载器各自默认加载了哪些 jar 包和包含了哪些 classpath 的路径。

#### 2）如何排查类的方法不一致的问题？

假如我们确定一个 jar 或者 class 已经在 classpath 里了，但是却总是提示`java.lang.NoSuchMethodError`，这是怎么回事呢？很可能是加载了错误的或者重复加载了不同版本的 jar 包。这时候，用前面的方法就可以先排查一下，加载了具体什么 jar，然后是不是不同路径下有重复的 class 文件，但是版本不一样。

#### 3）怎么看到加载了哪些类，以及加载顺序？

还是针对上一个问题，假如有两个地方有 Hello.class，一个是新版本，一个是旧的，怎么才能直观地看到他们的加载顺序呢？也没有问题，我们可以直接打印加载的类清单和加载顺序。

只需要在类的启动命令行参数加上`-XX:+TraceClassLoading` 或者 `-verbose` 即可，注意需要加载 Java 命令之后，要执行的类名之前，不然不起作用。例如：

```csharp
$ java -XX:+TraceClassLoading jvm.HelloClassLoader 
[Opened D:\Program Files\Java\jre1.8.0_231\lib\rt.jar]
[Loaded java.lang.Object from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar]
[Loaded java.io.Serializable from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar]
[Loaded java.lang.Comparable from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar]
[Loaded java.lang.CharSequence from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar]
[Loaded java.lang.String from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar]
[Loaded java.lang.reflect.AnnotatedElement from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar]
[Loaded java.lang.reflect.GenericDeclaration from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar]
[Loaded java.lang.reflect.Type from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar]
[Loaded java.lang.Class from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar]
[Loaded java.lang.Cloneable from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar]
[Loaded java.lang.ClassLoader from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar]
[Loaded java.lang.System from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar]
// .......  此处省略了100多条类加载信息
[Loaded jvm.Hello from __JVM_DefineClass__]
[Loaded java.util.concurrent.ConcurrentHashMap$ForwardingNode from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar]
Hello Class Initialized!
[Loaded java.lang.Shutdown from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar]
[Loaded java.lang.Shutdown$Lock from D:\Program Files\Java\jre1.8.0_231\lib\rt.jar]
```

上面的信息，可以很清楚的看到类的加载先后顺序，以及是从哪个 jar 里加载的，这样排查类加载的问题非常方便。

#### 4）怎么调整或修改 ext 和本地加载路径？

从前面的例子我们可以看到，假如什么都不设置，直接执行 java 命令，默认也会加载非常多的 jar 包，怎么可以自定义加载哪些 jar 包呢？比如我的代码很简单，只加载 rt.jar 行不行？答案是肯定的。

```bash
$ java -Dsun.boot.class.path="D:\Program Files\Java\jre1.8.0_231\lib\rt.jar" -Djava.ext.dirs= jvm.JvmClassLoaderPrintPath

启动类加载器
   ==> file:/D:/Program%20Files/Java/jdk1.8.0_231/jre/lib/rt.jar
扩展类加载器 ClassLoader -> sun.misc.Launcher$ExtClassLoader@15db9742
应用类加载器 ClassLoader -> sun.misc.Launcher$AppClassLoader@73d16e93
   ==> file:/D:/git/studyjava/build/classes/java/main/
   ==> file:/D:/git/studyjava/build/resources/main
```

我们看到启动类加载器只加载了 rt.jar，而扩展类加载器什么都没加载，这就达到了我们的目的。

其中命令行参数`-Dsun.boot.class.path`表示我们要指定启动类加载器加载什么，最基础的东西都在 rt.jar 这个包了里，所以一般配置它就够了。需要注意的是因为在 windows 系统默认 JDK 安装路径有个空格，所以需要把整个路径用双引号括起来，如果路径没有空格，或是 Linux/Mac 系统，就不需要双引号了。

参数`-Djava.ext.dirs`表示扩展类加载器要加载什么，一般情况下不需要的话可以直接配置为空即可。

#### 5）怎么运行期加载额外的 jar 包或者 class 呢？

有时候我们在程序已经运行了以后，还是想要再额外的去加载一些 jar 或类，需要怎么做呢？

简单说就是不使用命令行参数的情况下，怎么用代码来运行时改变加载类的路径和方式。假如说，在`d:/app/jvm`路径下，有我们刚才使用过的 Hello.class 文件，怎么在代码里能加载这个 Hello 类呢？

两个办法，一个是前面提到的自定义 ClassLoader 的方式，还有一个就是直接在当前的应用类加载器里，使用 URLClassLoader 类的方法 addURL，不过这个方法是 protected 的，需要反射处理一下，然后又因为程序在启动时并没有显示加载 Hello 类，所以在添加完了 classpath 以后，没法直接显式初始化，需要使用 Class.forName 的方式来拿到已经加载的Hello类（Class.forName("jvm.Hello")默认会初始化并执行静态代码块）。代码如下：

```java
package jvm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class JvmAppClassLoaderAddURL {

    public static void main(String[] args) {

        String appPath = "file:/d:/app/";
        URLClassLoader urlClassLoader = (URLClassLoader) JvmAppClassLoaderAddURL.class.getClassLoader();
        try {
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
            URL url = new URL(appPath);
            addURL.invoke(urlClassLoader, url);
            Class.forName("jvm.Hello"); // 效果跟Class.forName("jvm.Hello").newInstance()一样
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

执行以下，结果如下：

> $ java JvmAppClassLoaderAddURL Hello Class Initialized!

结果显示 Hello 类被加载，成功的初始化并执行了其中的代码逻辑。



## 06Java 内存模型：海不辞水，故能成其深

了解计算机历史的同学应该知道，计算机刚刚发明的时候，是没有内存这个概念的，速度慢到无法忍受。 直到冯诺依曼提出了一个天才的设计才解决了这个问题，没错，这个设计就是加了内存，所以现代的电子计算机又叫做“冯诺依曼机”。

JVM 是一个完整的计算机模型，所以自然就需要有对应的内存模型，这个模型被称为 “`Java 内存模型`”，对应的英文是“`Java Memory Model`”，简称 `JMM`。

Java 内存模型规定了 JVM 应该如何使用计算机内存（RAM）。 广义来讲， Java 内存模型分为两个部分：

- JVM 内存结构
- JMM 与线程规范

其中，JVM 内存结构是底层实现，也是我们理解和认识 JMM 的基础。 大家熟知的堆内存、栈内存等运行时数据区的划分就可以归为 JVM 内存结构。

就像很多神书讲 JVM 开篇就讲怎么编译 JVM 一样，讲 JMM 一上来就引入 CPU 寄存器的同步机制。虽然看起来高大上、显得高深莫测，但是大家很难理解。

所以我们这节课先从基础讲起，避开生涩的一些过于底层的术语，学习基本的 JVM 内存结构。理解了这些基本的知识点，然后再来学习 JMM 和线程相关的知识。

### 6.1 JVM 内存结构

我们先来看看 JVM 整体的内存概念图：

JVM 内部使用的 Java 内存模型， 在逻辑上将内存划分为 `线程栈`（thread stacks）和`堆内存` （heap）两个部分。 如下图所示：

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/4pajs.jpg" alt="6f0f8921-0768-4d1d-8811-f27a8a6608a8.jpg" style="zoom:50%;" />

JVM 中，每个正在运行的线程，都有自己的线程栈。 线程栈包含了当前正在执行的方法链/调用链上的所有方法的状态信息。

所以线程栈又被称为“`方法栈`”或“`调用栈`”（call stack）。线程在执行代码时，调用栈中的信息会一直在变化。

线程栈里面保存了调用链上正在执行的所有方法中的局部变量。

- 每个线程都只能访问自己的线程栈。
- 每个线程都不能访问(看不见)其他线程的局部变量。

即使两个线程正在执行完全相同的代码，但每个线程都会在自己的线程栈内创建对应代码中声明的局部变量。 所以每个线程都有一份自己的局部变量副本。

- 所有原生类型的局部变量都存储在线程栈中，因此对其他线程是不可见的。
- 线程可以将一个原生变量值的副本传给另一个线程，但不能共享原生局部变量本身。
- 堆内存中包含了 Java 代码中创建的所有对象，不管是哪个线程创建的。 其中也涵盖了包装类型（例如`Byte`，`Integer`，`Long`等）。
- 不管是创建一个对象并将其赋值给局部变量， 还是赋值给另一个对象的成员变量， 创建的对象都会被保存到堆内存中。

下图演示了线程栈上的调用栈和局部变量，以及存储在堆内存中的对象：

![91015fe2-53dc-477d-ba6d-fd0fe5e864e0.jpg](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/ksq1n.jpg)

- 如果是原生数据类型的局部变量，那么它的内容就全部保留在线程栈上。
- 如果是对象引用，则栈中的局部变量槽位中保存着对象的引用地址，而实际的对象内容保存在堆中。
- 对象的成员变量与对象本身一起存储在堆上, 不管成员变量的类型是原生数值，还是对象引用。
- 类的静态变量则和类定义一样都保存在堆中。

总结一下：**原始数据类型和对象引用地址在栈上；对象、对象成员与类定义、静态变量在堆上**。

堆内存又称为“`共享堆`”，**堆中的所有对象，可以被所有线程访问, 只要他们能拿到对象的引用地址。**

- 如果一个线程可以访问某个对象时，也就可以访问该对象的成员变量。
- 如果两个线程同时调用某个对象的同一方法，则它们都可以访问到这个对象的成员变量，但每个线程的局部变量副本是独立的。

示意图如下所示：

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/6j9fe.jpg" alt="5eb89250-e803-44bb-8553-a2ae74fd01ba.jpg" style="zoom: 33%;" />

总结一下：虽然各个线程自己使用的局部变量都在自己的栈上，但是大家可以共享堆上的对象，特别地各个不同线程访问同一个对象实例的基础类型的成员变量，会给每个线程一个变量的副本。

### 6.2 栈内存的结构

根据以上内容和对 JVM 内存划分的理解，制作了几张逻辑概念图供大家参考。

先看看栈内存(Stack)的大体结构：

![dd71b714-e026-4679-b589-52c8b9226b6f.jpg](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/6yhj7.jpg)

每启动一个线程，JVM 就会在栈空间栈分配对应的**线程栈**, 比如 1MB 的空间（`-Xss1m`）。

线程栈也叫做 Java 方法栈。 如果使用了 JNI 方法，则会分配一个单独的本地方法栈(Native Stack)。

线程执行过程中，一般会有多个方法组成调用栈(Stack Trace), 比如 A 调用 B，B 调用 C……每执行到一个方法，就会创建对应的**栈帧**(Frame)。

![6f9940a3-486f-4137-9420-123c9ae0826c.jpg](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/ze6dq.jpg)

栈帧是一个逻辑上的概念，具体的大小在一个方法编写完成后基本上就能确定。

比如 `返回值` 需要有一个空间存放吧，每个`局部变量`都需要对应的地址空间，此外还有给指令使用的 `操作数栈`，以及 class 指针(标识这个栈帧对应的是哪个类的方法, 指向非堆里面的 Class 对象）。

### 6.3 堆内存的结构

Java 程序除了栈内存之外，最主要的内存区域就是堆内存了。

![706185c0-d264-4a7c-b0c3-e23184ab20b7.jpg](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/u0xac.jpg)

堆内存是所有线程共用的内存空间，理论上大家都可以访问里面的内容。

但 JVM 的具体实现一般会有各种优化。比如将逻辑上的 Java 堆,划分为`堆(Heap)`和`非堆(Non-Heap)`两个部分.。这种划分的依据在于，**我们编写的 Java 代码，基本上只能使用 Heap 这部分空间，发生内存分配和回收的主要区域也在这部分**，所以有一种说法，这里的 Heap 也叫 GC 管理的堆(GC Heap)。

GC 理论中有一个重要的思想，叫做分代。 经过研究发现，程序中分配的对象，要么用过就扔，要么就能存活很久很久。

因此，JVM 将 Heap 内存分为年轻代（Young generation）和老年代（Old generation, 也叫 Tenured）两部分。

年轻代还划分为 3 个内存池，新生代(Eden space)和存活区(Survivor space), 在大部分 GC 算法中有 2 个存活区(S0, S1)，在我们可以观察到的任何时刻，S0 和 S1 总有一个是空的, 但一般较小，也不浪费多少空间。

具体实现对新生代还有优化，那就是 TLAB(Thread Local Allocation Buffer), **给每个线程先划定一小片空间，你创建的对象先在这里分配，满了再换。**这能极大降低并发资源锁定的开销。

Non-Heap 本质上还是 Heap，只是一般不归 GC 管理，里面划分为 3 个内存池。

- Metaspace, 以前叫持久代(永久代, Permanent generation), Java8 换了个名字叫 Metaspace. Java8 将方法区移动到了 Meta 区里面，而方法又是class的一部分和 CCS 交叉了?
- CCS, Compressed Class Space, 存放 class 信息的，和 Metaspace 有交叉。
- Code Cache, 存放 JIT 编译器编译后的本地机器代码。

JVM 的内存结构大致如此。 掌握了这些基础知识，我们再来看看 JMM。

### 6.4 CPU 指令

我们知道，计算机按支持的指令大致可以分为两类：

- `精简指令集计算机(RISC)`, 代表是如今大家熟知的 ARM 芯片，功耗低，运算能力相对较弱。
- `复杂指令集计算机(CISC)`, 代表作是 Intel 的 X86 芯片系列，比如奔腾，酷睿，至强，以及 AMD 的 CPU。特点是性能强劲，功耗高。（实际上从奔腾 4 架构开始，对外是复杂指令集，内部实现则是精简指令集，所以主频才能大幅度提高）

写过程序的人都知道，同样的计算，可以有不同的实现方式。 硬件指令设计同样如此，比如说我们的系统需要实现某种功能，那么复杂点的办法就是在 CPU 中封装一个逻辑运算单元来实现这种的运算，对外暴露一个专用指令。

当然也可以偷懒，不实现这个指令，而是由程序编译器想办法用原有的那些基础的，**通用指令来模拟和拼凑出这个功能。那么随着时间的推移，实现专用指令的 CPU 指令集就会越来越复杂, ，被称为复杂指令集。** 而偷懒的 CPU 指令集相对来说就会少很多，甚至砍掉了很多指令，所以叫精简指令集计算机。

不管哪一种指令集，CPU 的实现都是采用流水线的方式。如果 CPU 一条指令一条指令地执行，那么很多流水线实际上是闲置的。简单理解，可以类比一个 KFC 的取餐窗口就是一条流水线。于是硬件设计人员就想出了一个好办法： “`指令乱序`”。 CPU 完全可以根据需要，通过内部调度把这些指令打乱了执行，充分利用流水线资源，只要最终结果是等价的，那么程序的正确性就没有问题。但这在如今多 CPU 内核的时代，随着复杂度的提升，并发执行的程序面临了很多问题。

![af56a365-b03b-46f6-94d0-2983ec2259d8.jpg](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/l13o1.jpg)

CPU 是多个核心一起执行，同时 JVM 中还有多个线程在并发执行，这种多对多让局面变得异常复杂，稍微控制不好，程序的执行结果可能就是错误的。

### 6.5 JMM 背景

目前的 JMM 规范对应的是 “[JSR-133. Java Memory Model and Thread Specification](https://jcp.org/en/jsr/detail?id=133)” ，这个规范的部分内容润色之后就成为了《Java语言规范》的 [$17.4. Memory Model章节](https://docs.oracle.com/javase/specs/jls/se8/html/jls-17.html#jls-17.4)。可以看到，JSR133 的最终版修订时间是在 2014 年，这是因为之前的 Java 内存模型有些坑，所以在 Java 1.5 版本的时候进行了重新设计，并一直沿用到今天。

JMM 规范明确定义了不同的线程之间，通过哪些方式，在什么时候可以看见其他线程保存到共享变量中的值；以及在必要时，如何对共享变量的访问进行同步。这样的好处是屏蔽各种硬件平台和操作系统之间的内存访问差异，实现了 Java 并发程序真正的跨平台。

随着 Java 在 Web 领域的大规模应用，为了充分利用多核的计算能力，多线程编程越来越受欢迎。这时候就出现很多线程安全方面的问题。想要真正掌握并发程序设计，则必须要理解 Java 内存模型。可以说，我们在 JVM 内存结构中学过的`堆内存`、`栈内存`等知识，以及 Java 中的同步、锁、线程等等术语都和JMM 有非常大的关系。

### 6.6 JMM 简介

JVM 支持程序多线程执行，每个线程是一个 `Thread`，如果不指定明确的同步措施，那么多个线程在访问同一个共享变量时，就看会发生一些奇怪的问题，比如 A 线程读取了一个变量 a=10，想要做一个只要大于9就减2的操作，同时 B 线程先在 A 线程操作前设置 a=8，其实这时候已经不满足 A 线程的操作条件了，但是 A 线程不知道，依然执行了 a-2，最终 a=6；实际上 a 的正确值应该是 8，这个没有同步的机制在多线程下导致了错误的最终结果。

这样一来，就需要 JMM 定义多线程执行环境下的一些语义问题，也就是定义了哪些方式是允许的。

下面我们简要介绍一下 JMM 规范里有些什么内容。

> 给定一个程序和该程序的一串执行轨迹，内存模型描述了该执行轨迹是否是该程序的一次合法执行。对于 Java，内存模型检查执行轨迹中的每次读操作，然后根据特定规则，检验该读操作观察到的写是否合法。 内存模型描述了某个程序的可能行为。JVM 实现可以自由地生成想要的代码，只要该程序所有最终执行产生的结果能通过内存模型进行预测。这为大量的代码转换提供了充分的自由，包括动作（action）的重排序以及非必要的同步移除。 内存模型的一个高级、非正式的表述"显示其是一组规则，规定了一个线程的写操作何时会对另一个线程可见"。通俗地说，读操作 r 通常能看到任何写操作 w 写入的值，意味着 w 不是在 r 之后发生，且 w 看起来没有被另一个写操作 w' 覆盖掉（从 r 的角度看）。

JMM 定义了一些术语和规定，大家略有了解即可。

- 能被多个线程共享使用的内存称为“`共享内存`”或“`堆内存`”。
- 所有的对象(包括内部的实例成员变量)，static 变量，以及数组，都必须存放到堆内存中。
- 局部变量，方法的形参/入参，异常处理语句的入参不允许在线程之间共享，所以不受内存模型的影响。
- 多个线程同时对一个变量访问时【读取/写入】，这时候只要有某个线程执行的是写操作，那么这种现象就称之为“冲突”。
- 可以被其他线程影响或感知的操作，称为线程间的交互行为， 可分为： 读取、写入、同步操作、外部操作等等。 其中同步操作包括：对 volatile 变量的读写，对管程(monitor)的锁定与解锁，线程的起始操作与结尾操作，线程启动和结束等等。 外部操作则是指对线程执行环境之外的操作，比如停止其他线程等等。

JMM 规范的是线程间的交互操作，而不管线程内部对局部变量进行的操作。

> 有兴趣的同学可参阅: ifeve 翻译的: [JSR133 中文版.pdf](http://ifeve.com/wp-content/uploads/2014/03/JSR133中文版.pdf)

### 6.7 内存屏障简介

前面提到了CPU会在合适的时机，按需要对将要进行的操作重新排序，但是有时候这个重排机会导致我们的代码跟预期不一致。

怎么办呢？JMM 引入了内存屏障机制。

内存屏障可分为`读屏障`和`写屏障`，用于控制可见性。 常见的 `内存屏障` 包括：

```bash
#LoadLoad
#StoreStore
#LoadStore
#StoreLoad
```

这些屏障的主要目的，是用来短暂屏蔽 CPU 的指令重排序功能。 和 CPU 约定好，看见这些指令时，就要保证这个指令前后的相应操作不会被打乱。

- 比如看见 `#LoadLoad`, 那么屏障前面的 Load 指令就一定要先执行完，才能执行屏障后面的 Load 指令。
- 比如我要先把 a 值写到 A 字段中，然后再将 b 值写到 B 字段对应的内存地址。如果要严格保障这个顺序,那么就可以在这两个 Store 指令之间加入一个 `#StoreStore` 屏障。
- 遇到 `#LoadStore` 屏障时, CPU 自废武功，短暂屏蔽掉指令重排序功能。
- `#StoreLoad` 屏障, 能确保屏障之前执行的所有 store 操作，都对其他处理器可见; 在屏障后面执行的 load 指令, 都能取得到最新的值。换句话说, 有效阻止屏障之前的 store 指令，与屏障之后的 load 指令乱序 、即使是多核心处理器，在执行这些操作时的顺序也是一致的。

代价最高的是 `#StoreLoad` 屏障, 它同时具有其他几类屏障的效果，可以用来代替另外三种内存屏障。

如何理解呢?

就是只要有一个 CPU 内核收到这类指令，就会做一些操作，同时发出一条广播, 给某个内存地址打个标记，其他 CPU 内核与自己的缓存交互时，就知道这个缓存不是最新的，需要从主内存重新进行加载处理。



## 07JVM 启动参数详解

JVM 作为一个通用的虚拟机，我们可以通过启动 Java 命令时指定不同的 JVM 参数，让 JVM 调整自己的运行状态和行为，内存管理和垃圾回收的 GC 算法，添加和处理调试和诊断信息等等。本节概括地讲讲 JVM 参数，对于 GC 相关的详细参数将在后续的 GC 章节说明和分析。

直接通过命令行启动 Java 程序的格式为:

```shell
java [options] classname [args]

java [options] -jar filename [args]
```

其中:

- `[options]` 部分称为 "JVM 选项",对应 IDE 中的 VM options, 可用 `jps -v` 查看。
- `[args]` 部分是指 "传给main函数的参数", 对应 IDE 中的 Program arguments, 可用 `jps -m` 查看。

如果是使用 Tomcat 之类自带 startup.sh 等启动脚本的程序，我们一般把相关参数都放到一个脚本定义的 JAVA_OPTS 环境变量中，最后脚本启动 JVM 时会把 JAVA_OPTS 变量里的所有参数都加到命令的合适位置。

如果是在 IDEA 之类的 IDE 里运行的话，则可以在“Run/Debug Configurations”里看到 VM 选项和程序参数两个可以输入参数的地方，直接输入即可。

![73146375.png](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/dwoev.png)

上图输入了两个 VM 参数，都是环境变量，一个是指定文件编码使用 UTF-8，一个是设置了环境变量 a 的值为 1。

Java 和 JDK 内置的工具，指定参数时都是一个 `-`，不管是长参数还是短参数。有时候，JVM 启动参数和 Java 程序启动参数，并没必要严格区分，大致知道都是一个概念即可。

JVM 的启动参数, 从形式上可以简单分为：

- 以`-`开头为标准参数，所有的 JVM 都要实现这些参数，并且向后兼容。
- 以`-X`开头为非标准参数， 基本都是传给 JVM 的，默认 JVM 实现这些参数的功能，但是并不保证所有 JVM 实现都满足，且不保证向后兼容。
- 以`-XX:`开头为非稳定参数, 专门用于控制 JVM 的行为，跟具体的 JVM 实现有关，随时可能会在下个版本取消。
- `-XX:+-Flags` 形式, `+-` 是对布尔值进行开关。
- `-XX:key=value` 形式, 指定某个选项的值。

实际上，直接在命令行输入 java，然后回车，就会看到 java 命令可以其使用的参数列表说明：

```ruby
$ java
用法: java [-options] class [args...]
           (执行类)
   或 java [-options] -jar jarfile [args...]
           (执行 jar 文件)
其中选项包括:
    -d32 使用 32 位数据模型 (如果可用)
    -d64 使用 64 位数据模型 (如果可用)
    -server 选择 "server" VM
                  默认 VM 是 server,
                  因为您是在服务器类计算机上运行。
    -cp <目录和 zip/jar 文件的类搜索路径>
    -classpath <目录和 zip/jar 文件的类搜索路径>
                  用 : 分隔的目录, JAR 档案
                  和 ZIP 档案列表, 用于搜索类文件。
    -D<名称>=<值>
                  设置系统属性
    -verbose:[class|gc|jni]
                  启用详细输出
    -version 输出产品版本并退出
    -version:<值>
                  警告: 此功能已过时, 将在
                  未来发行版中删除。
                  需要指定的版本才能运行
    -showversion 输出产品版本并继续
    -jre-restrict-search | -no-jre-restrict-search
                  警告: 此功能已过时, 将在
                  未来发行版中删除。
                  在版本搜索中包括/排除用户专用 JRE
    -? -help 输出此帮助消息
    -X 输出非标准选项的帮助
    -ea[:<packagename>...|:<classname>]
    -enableassertions[:<packagename>...|:<classname>]
                  按指定的粒度启用断言
    -da[:<packagename>...|:<classname>]
    -disableassertions[:<packagename>...|:<classname>]
                  禁用具有指定粒度的断言
    -esa | -enablesystemassertions
                  启用系统断言
    -dsa | -disablesystemassertions
                  禁用系统断言
    -agentlib:<libname>[=<选项>]
                  加载本机代理库 <libname>, 例如 -agentlib:hprof
                  另请参阅 -agentlib:jdwp=help 和 -agentlib:hprof=help
    -agentpath:<pathname>[=<选项>]
                  按完整路径名加载本机代理库
    -javaagent:<jarpath>[=<选项>]
                  加载 Java 编程语言代理, 请参阅 java.lang.instrument
    -splash:<imagepath>
                  使用指定的图像显示启动屏幕
有关详细信息, 请参阅 http://www.oracle.com/technetwork/java/javase/documentation/index.html。
```

### 7.1 设置系统属性

当我们给一个 Java 程序传递参数，最常用的方法有两种：

- 系统属性，有时候也叫环境变量，例如直接给 JVM 传递指定的系统属性参数，需要使用 `-Dkey=value` 这种形式，此时如果系统的环境变量里不管有没有指定这个参数，都会以这里的为准。
- 命令行参数，直接通过命令后面添加的参数，比如运行 Hello 类，同时传递 2 个参数 kimm、king：`java Hello kimm king`，然后在Hello类的 main 方法的参数里可以拿到一个字符串的参数数组，有两个字符串，kimm 和 king。

比如我们常见的设置 $JAVA_HOME 就是一个环境变量，只要在当前命令执行的上下文里有这个环境变量，就可以在启动的任意程序里，通过相关 API 拿到这个参数，比如 Java 里：

`System.getProperty("key")`来获取这个变量的值，这样就可以做到多个不同的应用进程可以共享这些变量，不用每个都重复设置，也可以实现简化 Java 命令行的长度（想想要是配置了 50 个参数多恐怖，放到环境变量里，可以简化启动输入的字符）。此外，由于环境变量的 key-value 的形式，所以不管是环境上下文里配置的，还是通过运行时`-D`来指定，都可以不在意参数的顺序，而命令行参数就必须要注意顺序，顺序错误就会导致程序错误。

例如指定随机数熵源(Entropy Source)，示例:

```shell
JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"
```

此外还有一些常见设置：

```ini
 -Duser.timezone=GMT+08  // 设置用户的时区为东八区
 -Dfile.encoding=UTF-8      // 设置默认的文件编码为UTF-8
```

查看默认的所有系统属性，可以使用命令：

```cpp
$ java -XshowSettings:properties -version
Property settings:
    awt.toolkit = sun.lwawt.macosx.LWCToolkit
    file.encoding = UTF-8
    file.encoding.pkg = sun.io
    file.separator = /
    gopherProxySet = false
    java.awt.graphicsenv = sun.awt.CGraphicsEnvironment
    java.awt.printerjob = sun.lwawt.macosx.CPrinterJob
    java.class.path = .
    java.class.version = 52.0
...... 省略了几十行
```

同样可以查看 VM 设置：

```yaml
$ java -XshowSettings:vm -version
VM settings:
    Max. Heap Size (Estimated): 1.78G
    Ergonomics Machine Class: server
    Using VM: Java HotSpot(TM) 64-Bit Server VM
......
```

查看当前 JDK/JRE 的默认显示语言设置：

```java
java -XshowSettings:locale -version
Locale settings:
    default locale = 中文
    default display locale = 中文 (中国)
    default format locale = 英文 (中国)

    available locales = , ar, ar_AE, ar_BH, ar_DZ, ar_EG, ar_IQ, ar_JO,
        ar_KW, ar_LB, ar_LY, ar_MA, ar_OM, ar_QA, ar_SA, ar_SD,
 ......
```

还有常见的，我们使用 mvn 脚本去执行编译的同时，如果不想编译和执行单元测试代码：

> $ mvn package -Djava.test.skip=true

或者

> $ mvn package -DskipTests

等等，很多地方会用设置系统属性的方式去传递数据给Java程序，而不是直接用程序参数的方式。

### 7.2 Agent 相关的选项

Agent 是 JVM 中的一项黑科技, 可以通过无侵入方式来做很多事情，比如注入 AOP 代码，执行统计等等，权限非常大。这里简单介绍一下配置选项，详细功能在后续章节会详细讲。

设置 agent 的语法如下:

- `-agentlib:libname[=options]` 启用native方式的agent, 参考 `LD_LIBRARY_PATH` 路径。
- `-agentpath:pathname[=options]` 启用native方式的agent。
- `-javaagent:jarpath[=options]` 启用外部的agent库, 比如 `pinpoint.jar` 等等。
- `-Xnoagent` 则是禁用所有 agent。

以下示例开启 CPU 使用时间抽样分析:

```shell
JAVA_OPTS="-agentlib:hprof=cpu=samples,file=cpu.samples.log"
```

其中 hprof 是 JDK 内置的一个性能分析器。`cpu=samples` 会抽样在各个方法消耗的时间占比, Java 进程退出后会将分析结果输出到文件。

### 7.3 JVM 运行模式

JVM 有两种运行模式：

- `-server`：设置 jvm 使 server 模式，特点是启动速度比较慢，但运行时性能和内存管理效率很高，适用于生产环境。在具有 64 位能力的 jdk 环境下将默认启用该模式，而忽略 -client 参数。
- `-client` ：JDK1.7 之前在 32 位的 x86 机器上的默认值是 `-client` 选项。设置 jvm 使用 client 模式，特点是启动速度比较快，但运行时性能和内存管理效率不高，通常用于客户端应用程序或者PC应用开发和调试。

此外，我们知道 JVM 加载字节码后，可以解释执行，也可以编译成本地代码再执行，所以可以配置 JVM 对字节码的处理模式：

- `-Xint`：在解释模式（interpreted mode）下，-Xint 标记会强制 JVM 解释执行所有的字节码，这当然会降低运行速度，通常低 10 倍或更多。
- `-Xcomp`：-Xcomp 参数与 -Xint 正好相反，JVM 在第一次使用时会把所有的字节码编译成本地代码，从而带来最大程度的优化。
- `-Xmixed`：-Xmixed 是混合模式，将解释模式和变异模式进行混合使用，有 JVM 自己决定，这是 JVM 的默认模式，也是推荐模式。 我们使用 `java -version` 可以看到 `mixed mode` 等信息。

示例:

```shell
JAVA_OPTS="-server"
```

### 7.4 设置堆内存

JVM 的内存设置是最重要的参数设置，也是 GC 分析和调优的重点。

> JVM 总内存=堆+栈+非堆+堆外内存。

相关的参数：

- `-Xmx`, 指定最大堆内存。 如 `-Xmx4g`. 这只是限制了 Heap 部分的最大值为 4g。这个内存不包括栈内存，也不包括堆外使用的内存。
- `-Xms`, 指定堆内存空间的初始大小。 如 `-Xms4g`。 而且指定的内存大小，并不是操作系统实际分配的初始值，而是 GC 先规划好，用到才分配。 专用服务器上需要保持 `-Xms`和`-Xmx`一致，否则应用刚启动可能就有好几个 FullGC。当两者配置不一致时，堆内存扩容可能会导致性能抖动。
- `-Xmn`, 等价于 `-XX:NewSize`，使用 G1 垃圾收集器 **不应该** 设置该选项，在其他的某些业务场景下可以设置。官方建议设置为 `-Xmx` 的 `1/2 ~ 1/4`。
- `-XX:MaxPermSize=size`, 这是 JDK1.7 之前使用的。Java8 默认允许的 Meta 空间无限大，此参数无效。
- `-XX:MaxMetaspaceSize=size`, Java8 默认不限制 Meta 空间, 一般不允许设置该选项。
- `XX:MaxDirectMemorySize=size`，系统可以使用的最大堆外内存，这个参数跟`-Dsun.nio.MaxDirectMemorySize`效果相同。
- `-Xss`, 设置每个线程栈的字节数。 例如 `-Xss1m` 指定线程栈为 1MB，与`-XX:ThreadStackSize=1m`等价

这里要特别说一下堆外内存，也就是说不在堆上的内存，我们可以通过jconsole，jvisualvm 等工具查看。

RednaxelaFX 提到：

> 一个 Java 进程里面，可以分配 native memory 的东西有很多，特别是使用第三方 native 库的程序更是如此。

但在这里面除了

- GC heap = Java heap + Perm Gen（JDK <= 7）
- Java thread stack = Java thread count * Xss
- other thread stack = other thread count * stack size
- CodeCache 等东西之外

还有诸如 HotSpot VM 自己的 StringTable、SymbolTable、SystemDictionary、CardTable、HandleArea、JNIHandleBlock 等许多数据结构是常驻内存的，外加诸如 JIT 编译器、GC 等在工作的时候都会额外临时分配一些 native memory，这些都是 HotSpot VM自己所分配的 native memory；在 JDK 类库实现中也有可能有些功能分配长期存活或者临时的 native memory。

然后就是各种第三方库的 native 部分分配的 native memory。

“Direct Memory”，一般来说是 Java NIO 使用的 Direct-X-Buffer（例如 DirectByteBuffer）所分配的 native memory，这个地方如果我们使用 netty 之类的框架，会产生大量的堆外内存。

示例:

```shell
JAVA_OPTS="-Xms28g -Xmx28g"
```

### 最佳实践

#### 配置多少 xmx 合适

从上面的分析可以看到，系统有大量的地方使用堆外内存，远比我们常说的 xmx 和 xms 包括的范围要广。所以我们需要在设置内存的时候留有余地。

实际上，我个人比较推荐配置系统或容器里可用内存的 70-80% 最好。比如说系统有 8G 物理内存，系统自己可能会用掉一点，大概还有 7.5G 可以用，那么建议配置

> -Xmx6g 说明：xmx : 7.5G*0.8 = 6G，如果知道系统里有明确使用堆外内存的地方，还需要进一步降低这个值。

举个具体例子，我在过去的几个不同规模，不同发展时期，不同研发成熟度的公司研发团队，都发现过一个共同的 JVM 问题，就是线上经常有JVM实例突然崩溃，这个过程也许是三天，也可能是 2 周，异常信息也很明确，就是内存溢出 OOM。

运维人员不断加大堆内存或者云主机的物理内存，也无济于事，顶多让这个过程延缓。

大家怀疑内存泄露，但是看 GC 日志其实一直还挺正常，系统在性能测试环境也没什么问题，开发和运维还因此不断地发生矛盾和冲突。

其中有个运维同事为了缓解问题，通过一个多月的观察，持续地把一个没什么压力的服务器从 2 台逐渐扩展了 15 台，因为每天都有几台随机崩溃，他需要在系统通知到他去处理的这段时间，保证其他机器可以持续提供服务。

大家付出了很多努力，做了一些技术上的探索，还想了不少的歪招，但是没有解决问题，也就是说没有创造价值。

后来我去深入了解一下，几分钟就解决了问题，创造了技术的价值，把服务器又压缩回 2 台就可以保证系统稳定运行，业务持续可用了，降低成本带来的价值，也得到业务方和客户认可。

那么实际问题出在哪儿呢？一台云主机 4G 或 8G 内存，为了让 JVM 最大化的使用内存，服务部署的同事直接配置了xmx4g 或 xmx8g。因为他不知道 xmx 配置的内存和 JVM 可能使用的最大内存是不相等的。我让他把 8G 内存的云主机，设置 xmx6g，再也没出过问题，而且让他观察看到在 Java 进程最多的时候 JVM 进程使用了 7G 出头的内存（堆最多用 6g， java 进程自身、堆外空间都需要使用内存，这些内存不在 xmx 的范围内），而不包含 xmx 设置的 6g 内存内。

#### xmx 和 xms 是不是要配置成一致的

一般情况下，我们的服务器是专用的，就是一个机器（也可能是云主机或 docker 容器）只部署一个 Java 应用，这样的时候建议配置成一样的，好处是不会再动态去分配，如果内存不足（像上面的情况）上来就知道。

### 7.5 GC 日志相关的参数

在生产环境或性能压测环境里，我们用来分析和判断问题的重要数据来源之一就是 GC 日志，JVM 启动参数为我们提供了一些用于控制 GC 日志输出的选项。

- `-verbose:gc` ：和其他 GC 参数组合使用, 在 GC 日志中输出详细的GC信息。 包括每次 GC 前后各个内存池的大小，堆内存的大小，提升到老年代的大小，以及消耗的时间。此参数支持在运行过程中动态开关。比如使用 jcmd, jinfo， 以及使用 JMX 技术的其他客户端。
- `-XX:+PrintGCDetails` 和 `-XX:+PrintGCTimeStamps`：打印 GC 细节与发生时间。请关注我们后续的 GC 课程章节。
- `-Xloggc:file`：与`-verbose:gc`功能类似，只是将每次 GC 事件的相关情况记录到一个文件中，文件的位置最好在本地，以避免网络的潜在问题。若与 verbose:gc 命令同时出现在命令行中，则以 -Xloggc 为准。

示例:

```shell
export JAVA_OPTS="-Xms28g -Xmx28g -Xss1m \
-verbosegc -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/usr/local/"
```

### 7.6 指定垃圾收集器相关参数

垃圾回收器是 JVM 性能分析和调优的核心内容之一，也是近几个 JDK 版本大力发展和改进的地方。通过不同的 GC 算法和参数组合，配合其他调优手段，我们可以把系统精确校验到性能最佳状态。

以下参数指定具体的垃圾收集器，详细情况会在第二部分讲解：

- `-XX:+UseG1GC`：使用 G1 垃圾回收器
- `-XX:+UseConcMarkSweepGC`：使用 CMS 垃圾回收器
- `-XX:+UseSerialGC`：使用串行垃圾回收器
- `-XX:+UseParallelGC`：使用并行垃圾回收器

### 7.7 特殊情况执行脚本的参数

除了上面介绍的一些 JVM 参数，还有一些用于出现问题时提供诊断信息之类的参数。

- `-XX:+-HeapDumpOnOutOfMemoryError` 选项, 当 `OutOfMemoryError` 产生，即内存溢出(堆内存或持久代)时，自动 Dump 堆内存。 因为在运行时并没有什么开销, 所以在生产机器上是可以使用的。 示例用法: `java -XX:+HeapDumpOnOutOfMemoryError -Xmx256m ConsumeHeap`

```makefile
java.lang.OutOfMemoryError: Java heap space
Dumping heap to java_pid2262.hprof ...
......
```

- `-XX:HeapDumpPath` 选项, 与`HeapDumpOnOutOfMemoryError`搭配使用, 指定内存溢出时 Dump 文件的目录。 如果没有指定则默认为启动 Java 程序的工作目录。 示例用法: `java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/usr/local/ ConsumeHeap` 自动 Dump 的 hprof 文件会存储到 `/usr/local/` 目录下。
- `-XX:OnError` 选项, 发生致命错误时(fatal error)执行的脚本。 例如, 写一个脚本来记录出错时间, 执行一些命令, 或者 curl 一下某个在线报警的url. 示例用法: `java -XX:OnError="gdb - %p" MyApp` 可以发现有一个 `%p` 的格式化字符串，表示进程 PID。
- `-XX:OnOutOfMemoryError` 选项, 抛出 OutOfMemoryError 错误时执行的脚本。
- `-XX:ErrorFile=filename` 选项, 致命错误的日志文件名，绝对路径或者相对路径。

本节只简要的介绍一下 JVM 参数，其实还有大量的参数跟 GC 垃圾收集器有关系，将会在第二部分进行详细的解释和分析。

## 08 JDK 内置命令行工具：工欲善其事，必先利其器

很多情况下，JVM 运行环境中并没有趁手的工具，所以掌握基本的内置工具是一项基本功。

JDK 自带的工具和程序可以分为 2 大类型：

1. 开发工具
2. 诊断分析工具

### JDK 内置的开发工具

写过 Java 程序的同学，对 JDK 中的开发工具应该比较熟悉。 下面列举常用的部分：

| 工具        | 简介                                                         |
| :---------- | :----------------------------------------------------------- |
| java        | Java 应用的启动程序                                          |
| javac       | JDK 内置的编译工具                                           |
| javap       | 反编译 class 文件的工具                                      |
| **javadoc** | 根据 Java 代码和标准注释，自动生成相关的 API 说明文档        |
| **javah**   | JNI 开发时，根据 Java 代码生成需要的 .h 文件。               |
| extcheck    | 检查某个 jar 文件和运行时扩展 jar 有没有版本冲突，很少使用   |
| jdb         | Java Debugger 可以调试本地和远端程序，属于 JPDA 中的一个 Demo 实现，供其他调试器参考。开发时很少使用 |
| jdeps       | 探测 class 或 jar 包需要的依赖                               |
| jar         | 打包工具，可以将文件和目录打包成为 .jar 文件；.jar 文件本质上就是 zip 文件，只是后缀不同。使用时按顺序对应好选项和参数即可。 |
| keytool     | 安全证书和密钥的管理工具（支持生成、导入、导出等操作）       |
| jarsigner   | jar 文件签名和验证工具                                       |
| policytool  | 实际上这是一款图形界面工具，管理本机的 Java 安全策略         |

开发工具此处不做详细介绍，有兴趣的同学请参考文末的链接。

下面介绍诊断和分析工具。

### 命令行诊断和分析工具

JDK 内置了各种命令行工具，条件受限时我们可以先用命令行工具快速查看 JVM 实例的基本情况。

> macOS X、Windows 系统的某些账户权限不够，有些工具可能会报错/失败，假如出问题了请排除这个因素。

### JPS 工具简介

我们知道，操作系统提供一个工具叫做 ps，用于显示进程状态（Process Status）。

Java也 提供了类似的命令行工具，叫做 JPS，用于展示 Java 进程信息（列表）。

需要注意的是，JPS 展示的是当前用户可看见的 Java 进程，如果看不见某些进程可能需要 sudo、su 之类的命令来切换权限。

查看帮助信息：

> $ `jps -help`

```shell
usage: jps [-help]
       jps [-q] [-mlvV] [<hostid>]
Definitions:
    <hostid>:      <hostname>[:<port>]
```

可以看到， 这些参数分为了多个组，`-help`、`-q`、`-mlvV`， 同一组可以共用一个 `-`。

常用参数是小写的 `-v`，显示传递给 JVM 的启动参数。

> $ `jps -v`

```javascript
15883 Jps -Dapplication.home=/usr/local/jdk1.8.0_74 -Xms8m
6446 Jstatd -Dapplication.home=/usr/local/jdk1.8.0_74 -Xms8m
        -Djava.security.policy=/etc/java/jstatd.all.policy
32383 Bootstrap -Xmx4096m -XX:+UseG1GC -verbose:gc
        -XX:+PrintGCDateStamps -XX:+PrintGCDetails
        -Xloggc:/xxx-tomcat/logs/gc.log
        -Dcatalina.base=/xxx-tomcat -Dcatalina.home=/data/tomcat
```

看看输出的内容，其中最重要的信息是前面的进程 ID（PID）。

其他参数不太常用：

- `-q`：只显示进程号。
- `-m`：显示传给 main 方法的参数信息
- `-l`：显示启动 class 的完整类名，或者启动 jar 的完整路径
- `-V`：大写的 V，这个参数有问题，相当于没传一样。官方说的跟 `-q` 差不多。
- `<hostid>`：部分是远程主机的标识符，需要远程主机启动 `jstatd` 服务器支持。

可以看到，格式为 `<hostname>[:<port>]`，不能用 IP，示例：`jps -v sample.com:1099`。

知道 JVM 进程的 PID 之后，就可以使用其他工具来进行诊断了。

### jstat 工具简介

jstat 用来监控 JVM 内置的各种统计信息，主要是内存和 GC 相关的信息。

查看 jstat 的帮助信息，大致如下：

> $ `jstat -help`

```xml
Usage: jstat -help|-options
       jstat -<option> [-t] [-h<lines>] <vmid> [<interval> [<count>]]

Definitions:
  <option>      可用的选项，查看详情请使用 -options
  <vmid>        虚拟机标识符，格式：<lvmid>[@<hostname>[:<port>]]
  <lines>       标题行间隔的频率.
  <interval>    采样周期，<n>["ms"|"s"]，默认单位是毫秒 "ms"
  <count>       采用总次数
  -J<flag>      传给jstat底层JVM的 <flag> 参数
```

再来看看 `<option>` 部分支持哪些选项：

> $ `jstat -options`

```diff
-class
-compiler
-gc
-gccapacity
-gccause
-gcmetacapacity
-gcnew
-gcnewcapacity
-gcold
-gcoldcapacity
-gcutil
-printcompilation
```

简单说明这些选项，不感兴趣可以跳着读。

- `-class`：类加载（Class loader）信息统计。
- `-compiler`：JIT 即时编译器相关的统计信息。
- `-gc`：GC 相关的堆内存信息，用法：`jstat -gc -h 10 -t 864 1s 20`。
- `-gccapacity`：各个内存池分代空间的容量。
- `-gccause`：看上次 GC、本次 GC（如果正在 GC 中）的原因，其他输出和 `-gcutil` 选项一致。
- `-gcnew`：年轻代的统计信息（New = Young = Eden + S0 + S1）。
- `-gcnewcapacity`：年轻代空间大小统计。
- `-gcold`：老年代和元数据区的行为统计。
- `-gcoldcapacity`：old 空间大小统计。
- `-gcmetacapacity`：meta 区大小统计。
- `-gcutil`：GC 相关区域的使用率（utilization）统计。
- `-printcompilation`：打印 JVM 编译统计信息。

实例：

```shell
jstat -gcutil -t 864
```

`-gcutil` 选项是统计 GC 相关区域的使用率（utilization），结果如下：

| Timestamp  | S0   | S1    | E     | O     | M     | CCS   | YGC    | YGCT    | FGC  | FGCT  | GCT     |
| :--------- | :--- | :---- | :---- | :---- | :---- | :---- | :----- | :------ | :--- | :---- | :------ |
| 14251645.5 | 0.00 | 13.50 | 55.05 | 71.91 | 83.84 | 69.52 | 113767 | 206.036 | 4    | 0.122 | 206.158 |

`-t` 选项的位置是固定的，不能在前也不能在后。可以看出是用于显示时间戳，即 JVM 启动到现在的秒数。

简单分析一下：

- Timestamp 列：JVM 启动了 1425 万秒，大约 164 天。
- S0：就是 0 号存活区的百分比使用率。0% 很正常，因为 S0 和 S1 随时有一个是空的。
- S1：就是 1 号存活区的百分比使用率。
- E：就是 Eden 区，新生代的百分比使用率。
- O：就是 Old 区，老年代。百分比使用率。
- M：就是 Meta 区，元数据区百分比使用率。
- CCS：压缩 class 空间（Compressed class space）的百分比使用率。
- YGC（Young GC）：年轻代 GC 的次数。11 万多次，不算少。
- YGCT 年轻代 GC 消耗的总时间。206 秒，占总运行时间的万分之一不到，基本上可忽略。
- FGC：FullGC 的次数，可以看到只发生了 4 次，问题应该不大。
- FGCT：FullGC 的总时间，0.122 秒，平均每次 30ms 左右，大部分系统应该能承受。
- GCT：所有 GC 加起来消耗的总时间，即 YGCT + FGCT。

可以看到，`-gcutil` 这个选项出来的信息不太好用，统计的结果是百分比，不太直观。

再看看 `-gc` 选项，GC 相关的堆内存信息。

```shell
jstat -gc -t 864 1s
jstat -gc -t 864 1s 3
jstat -gc -t -h 10 864 1s 15
```

其中的 `1s` 占了 `<interval>` 这个槽位，表示每 1 秒输出一次信息。

`1s 3` 的意思是每秒输出 1 次，最多 3 次。

如果只指定刷新周期，不指定 `<count>` 部分，则会一直持续输出。 退出输出按 `CTRL+C` 即可。

`-h 10` 的意思是每 10 行输出一次表头。

结果大致如下：

| Timestamp  | S0C    | S1C    | S0U   | S1U  | EC     | EU     | OC      | OU     | MC      | MU      | YGC    | YGCT    | FGC  | FGCT  |
| :--------- | :----- | :----- | :---- | :--- | :----- | :----- | :------ | :----- | :------ | :------ | :----- | :------ | :--- | :---- |
| 14254245.3 | 1152.0 | 1152.0 | 145.6 | 0.0  | 9600.0 | 2312.8 | 11848.0 | 8527.3 | 31616.0 | 26528.6 | 113788 | 206.082 | 4    | 0.122 |
| 14254246.3 | 1152.0 | 1152.0 | 145.6 | 0.0  | 9600.0 | 2313.1 | 11848.0 | 8527.3 | 31616.0 | 26528.6 | 113788 | 206.082 | 4    | 0.122 |
| 14254247.3 | 1152.0 | 1152.0 | 145.6 | 0.0  | 9600.0 | 2313.4 | 11848.0 | 8527.3 | 31616.0 | 26528.6 | 113788 | 206.082 | 4    | 0.122 |

上面的结果是精简过的，为了排版去掉了 GCT、CCSC、CCSU 这三列。看到这些单词可以试着猜一下意思，详细的解读如下：

- Timestamp 列：JVM 启动了 1425 万秒，大约 164 天。
- S0C：0 号存活区的当前容量（capacity），单位 kB。
- S1C：1 号存活区的当前容量，单位 kB。
- S0U：0 号存活区的使用量（utilization），单位 kB。
- S1U：1 号存活区的使用量，单位 kB。
- EC：Eden 区，新生代的当前容量，单位 kB。
- EU：Eden 区，新生代的使用量，单位 kB。
- OC：Old 区，老年代的当前容量，单位 kB。
- OU：Old 区，老年代的使用量，单位 kB。 （需要关注）
- MC：元数据区的容量，单位 kB。
- MU：元数据区的使用量，单位 kB。
- CCSC：压缩的 class 空间容量，单位 kB。
- CCSU：压缩的 class 空间使用量，单位 kB。
- YGC：年轻代 GC 的次数。
- YGCT：年轻代 GC 消耗的总时间。 （重点关注）
- FGC：Full GC 的次数
- FGCT：Full GC 消耗的时间。 （重点关注）
- GCT：垃圾收集消耗的总时间。

最重要的信息是 GC 的次数和总消耗时间，其次是老年代的使用量。

在没有其他监控工具的情况下， jstat 可以简单查看各个内存池和 GC 的信息，可用于判别是否是 GC 问题或者内存溢出。

### jmap 工具 *

面试最常问的就是 jmap 工具了。jmap 主要用来 Dump 堆内存。当然也支持输出统计信息。

官方推荐使用 JDK 8 自带的 jcmd 工具来取代 jmap，但是 jmap 深入人心，jcmd 可能暂时取代不了。

查看 jmap 帮助信息：

> $ `jmap -help`

```vbnet
Usage:
    jmap [option] <pid>
        (连接到本地进程)
    jmap [option] <executable <core>
        (连接到 core file)
    jmap [option] [server_id@]<remote-IP-hostname>
        (连接到远程 debug 服务)

where <option> is one of:
    <none>               等同于 Solaris 的 pmap 命令
    -heap                打印 Java 堆内存汇总信息
    -histo[:live]        打印 Java 堆内存对象的直方图统计信息
                         如果指定了 "live" 选项则只统计存活对象，强制触发一次 GC
    -clstats             打印 class loader 统计信息
    -finalizerinfo       打印等待 finalization 的对象信息
    -dump:<dump-options> 将堆内存 dump 为 hprof 二进制格式
                         支持的 dump-options：
                           live         只 dump 存活对象，不指定则导出全部。
                           format=b     二进制格式(binary format)
                           file=<file>  导出文件的路径
                         示例：jmap -dump:live,format=b,file=heap.bin <pid>
    -F                   强制导出，若 jmap 被 hang 住不响应，可断开后使用此选项。
                         其中 "live" 选项不支持强制导出。
    -h | -help           to print this help message
    -J<flag>             to pass <flag> directly to the runtime system
```

常用选项就 3 个：

- `-heap`：打印堆内存（/内存池）的配置和使用信息。
- `-histo`：看哪些类占用的空间最多，直方图。
- `-dump:format=b,file=xxxx.hprof`：Dump 堆内存。

示例：看堆内存统计信息。

> $ `jmap -heap 4524`

输出信息：

```sql
Attaching to process ID 4524, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.65-b01

using thread-local object allocation.
Parallel GC with 4 thread(s)

Heap Configuration:
   MinHeapFreeRatio         = 0
   MaxHeapFreeRatio         = 100
   MaxHeapSize              = 2069889024 (1974.0MB)
   NewSize                  = 42991616 (41.0MB)
   MaxNewSize               = 689963008 (658.0MB)
   OldSize                  = 87031808 (83.0MB)
   NewRatio                 = 2
   SurvivorRatio            = 8
   MetaspaceSize            = 21807104 (20.796875MB)
   CompressedClassSpaceSize = 1073741824 (1024.0MB)
   MaxMetaspaceSize         = 17592186044415 MB
   G1HeapRegionSize         = 0 (0.0MB)

Heap Usage:
PS Young Generation
Eden Space:
   capacity = 24117248 (23.0MB)
   used     = 11005760 (10.49591064453125MB)
   free     = 13111488 (12.50408935546875MB)
   45.63439410665761% used
From Space:
   capacity = 1048576 (1.0MB)
   used     = 65536 (0.0625MB)
   free     = 983040 (0.9375MB)
   6.25% used
To Space:
   capacity = 1048576 (1.0MB)
   used     = 0 (0.0MB)
   free     = 1048576 (1.0MB)
   0.0% used
PS Old Generation
   capacity = 87031808 (83.0MB)
   used     = 22912000 (21.8505859375MB)
   free     = 64119808 (61.1494140625MB)
   26.32600715361446% used

12800 interned Strings occupying 1800664 bytes.
```

- Attached，连着；
- Detached，分离。

可以看到堆内存和内存池的相关信息。当然，这些信息有多种方式可以得到，比如 JMX。

看看直方图：

> $ `jmap -histo 4524`

结果为：

```yaml
 num     #instances         #bytes  class name
----------------------------------------------
   1:         52214       11236072  [C
   2:        126872        5074880  java.util.TreeMap$Entry
   3:          5102        5041568  [B
   4:         17354        2310576  [I
   5:         45258        1086192  java.lang.String
......
```

简单分析，其中 `[C` 占用了 11MB 内存，没占用什么空间。

`[C` 表示 `chat[]`，`[B` 表示 `byte[]`，`[I` 表示 `int[]`，其他类似。这种基础数据类型很难分析出什么问题。

Java 中的大对象、巨无霸对象，一般都是长度很大的数组。

Dump 堆内存：

```shell
cd $CATALINA_BASE
jmap -dump:format=b,file=3826.hprof 3826
```

导出完成后，dump 文件大约和堆内存一样大。可以想办法压缩并传输。

分析 hprof 文件可以使用 jhat 或者 [mat](https://www.eclipse.org/mat/) 工具。

### jcmd 工具

诊断工具：jcmd 是 JDK 8 推出的一款本地诊断工具，只支持连接本机上同一个用户空间下的 JVM 进程。

查看帮助：

> $ `jcmd -help`

```shell
Usage: jcmd <pid | main class> <command ...|PerfCounter.print|-f file>
   or: jcmd -l                                                   
   or: jcmd -h                                                   

  command 必须是指定 JVM 可用的有效 jcmd 命令。     
  可以使用 "help" 命令查看该 JVM 支持哪些命令。  
  如果指定 pid 部分的值为 0，则会将 commands 发送给所有可见的 Java 进程。  
  指定 main class 则用来匹配启动类。可以部分匹配。（适用同一个类启动多实例）。                       
  If no options are given, lists Java processes (same as -p).    

  PerfCounter.print 命令可以展示该进程暴露的各种计数器
  -f  从文件读取可执行命令                 
  -l  列出（list）本机上可见的 JVM 进程                    
  -h  this help                          
```

查看进程信息：

```shell
jcmd
jcmd -l
jps -lm

11155 org.jetbrains.idea.maven.server.RemoteMavenServer
```

这几个命令的结果差不多。可以看到其中有一个 PID 为 11155 的进程，下面看看可以用这个 PID 做什么。

给这个进程发一个 help 指令：

```shell
jcmd 11155 help
jcmd RemoteMavenServer help
```

pid 和 main-class 输出信息是一样的：

```bash
11155:
The following commands are available:
VM.native_memory
ManagementAgent.stop
ManagementAgent.start_local
ManagementAgent.start
GC.rotate_log
Thread.print
GC.class_stats
GC.class_histogram
GC.heap_dump
GC.run_finalization
GC.run
VM.uptime
VM.flags
VM.system_properties
VM.command_line
VM.version
help
```

可以试试这些命令。查看 VM 相关的信息：

```shell
# JVM 实例运行时间
jcmd 11155 VM.uptime
9307.052 s

#JVM 版本号
jcmd 11155 VM.version
OpenJDK 64-Bit Server VM version 25.76-b162
JDK 8.0_76

# JVM 实际生效的配置参数
jcmd 11155 VM.flags
11155:
-XX:CICompilerCount=4 -XX:InitialHeapSize=268435456
-XX:MaxHeapSize=536870912 -XX:MaxNewSize=178782208
-XX:MinHeapDeltaBytes=524288 -XX:NewSize=89128960
-XX:OldSize=179306496 -XX:+UseCompressedClassPointers
-XX:+UseCompressedOops -XX:+UseParallelGC

# 查看命令行参数
jcmd 11155 VM.command_line
VM Arguments:
jvm_args: -Xmx512m -Dfile.encoding=UTF-8
java_command: org.jetbrains.idea.maven.server.RemoteMavenServer
java_class_path (initial): ...(xxx省略)...
Launcher Type: SUN_STANDARD

# 系统属性
jcmd 11155 VM.system_properties
...
java.runtime.name=OpenJDK Runtime Environment
java.vm.version=25.76-b162
java.vm.vendor=Oracle Corporation
user.country=CN
```

GC 相关的命令，统计每个类的实例占用字节数。

> $ `jcmd 11155 GC.class_histogram`

```yaml
 num     #instances         #bytes  class name
----------------------------------------------
   1:         11613        1420944  [C
   2:          3224         356840  java.lang.Class
   3:           797         300360  [B
   4:         11555         277320  java.lang.String
   5:          1551         193872  [I
   6:          2252         149424  [Ljava.lang.Object;
```

Dump 堆内存：

> $`jcmd 11155 help GC.heap_dump`

```shell
Syntax : GC.heap_dump [options] <filename>
Arguments: filename :  Name of the dump file (STRING, no default value)
Options:  -all=true 或者 -all=false (默认)

# 两者效果差不多; jcmd 需要指定绝对路径； jmap 不能指定绝对路径
jcmd 11155 GC.heap_dump -all=true ~/11155-by-jcmd.hprof
jmap -dump:file=./11155-by-jmap.hprof 11155
```

jcmd 坑的地方在于，必须指定绝对路径，否则导出的 hprof 文件就以 JVM 所在的目录计算。（因为是发命令交给 JVM 执行的）

其他命令用法类似，必要时请参考官方文档。

### jstack 工具

命令行工具、诊断工具：jstack 工具可以打印出 Java 线程的调用栈信息（Stack Trace）。一般用来查看存在哪些线程，诊断是否存在死锁等。

这时候就看出来给线程（池）命名的必要性了（开发不规范，整个项目都是坑），具体可参考阿里巴巴的 Java 开发规范。

看看帮助信息：

> $`jstack -help`

```shell
Usage:
    jstack [-l] <pid>
        (to connect to running process)
    jstack -F [-m] [-l] <pid>
        (to connect to a hung process)
    jstack [-m] [-l] <executable> <core>
        (to connect to a core file)
    jstack [-m] [-l] [server_id@]<remote server IP or hostname>
        (to connect to a remote debug server)

Options:
    -F  to force a thread dump. Use when jstack <pid> does not respond (process is hung)
    -m  to print both java and native frames (mixed mode)
    -l  long listing. Prints additional information about locks
    -h or -help to print this help message
```

选项说明：

- `-F`：强制执行 Thread Dump，可在 Java 进程卡死（hung 住）时使用，此选项可能需要系统权限。
- `-m`：混合模式（mixed mode），将 Java 帧和 native 帧一起输出，此选项可能需要系统权限。
- `-l`：长列表模式，将线程相关的 locks 信息一起输出，比如持有的锁，等待的锁。

常用的选项是 `-l`，示例用法。

```shell
jstack 4524
jstack -l 4524
```

死锁的原因一般是锁定多个资源的顺序出了问题（交叉依赖）， 网上示例代码很多，比如搜索“Java 死锁 示例”。

在 Linux 和 macOS 上，`jstack pid` 的效果跟 `kill -3 pid` 相同。

### jinfo 工具

诊断工具：jinfo 用来查看具体生效的配置信息以及系统属性，还支持动态增加一部分参数。

看看帮助信息：

> $ `jinfo -help`

```vbnet
Usage:
    jinfo [option] <pid>
        (to connect to running process)
    jinfo [option] <executable <core>
        (to connect to a core file)
    jinfo [option] [server_id@]<remote-IP-hostname>
        (to connect to remote debug server)

where <option> is one of:
    -flag <name>         to print the value of the named VM flag
    -flag [+|-]<name>    to enable or disable the named VM flag
    -flag <name>=<value> to set the named VM flag to the given value
    -flags               to print VM flags
    -sysprops            to print Java system properties
    <no option>          to print both of the above
    -h | -help           to print this help message
```

使用示例：

```undefined
jinfo 36663
jinfo -flags 36663
```

不加参数过滤，则打印所有信息。

jinfo 在 Windows 上比较稳定。在 macOS 上需要 root 权限，或是需要在提示下输入当前用户的密码。

![56345767.png](https://learn.lianglianglee.com/%E4%B8%93%E6%A0%8F/JVM%20%E6%A0%B8%E5%BF%83%E6%8A%80%E6%9C%AF%2032%20%E8%AE%B2%EF%BC%88%E5%AE%8C%EF%BC%89/assets/780663e0-2608-11ea-aa89-e1cacb5bf377)

然后就可以看到如下信息：

```javascript
jinfo 36663
Attaching to process ID 36663, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.131-b11
Java System Properties:

java.runtime.name = Java(TM) SE Runtime Environment
java.vm.version = 25.131-b11
sun.boot.library.path = /Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/jre/lib
// 中间省略了几十行
java.ext.dirs = /Users/kimmking/Library/Java/Extensions:/Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/jre/lib/ext:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java
sun.boot.class.path = /Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/jre/lib/resources.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/jre/lib/rt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/jre/lib/sunrsasign.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/jre/lib/jsse.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/jre/lib/jce.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/jre/lib/charsets.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/jre/lib/jfr.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/jre/classes
java.vendor = Oracle Corporation
maven.home = /Users/kimmking/tools/apache-maven-3.5.0
file.separator = /
java.vendor.url.bug = http://bugreport.sun.com/bugreport/
sun.io.unicode.encoding = UnicodeBig
sun.cpu.endian = little
sun.cpu.isalist =

VM Flags:
Non-default VM flags: -XX:CICompilerCount=3 -XX:InitialHeapSize=134217728 -XX:MaxHeapSize=2147483648 -XX:MaxNewSize=715653120 -XX:MinHeapDeltaBytes=524288 -XX:NewSize=44564480 -XX:OldSize=89653248 -XX:+TraceClassLoading -XX:+TraceClassUnloading -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseFastUnorderedTimeStamps -XX:+UseParallelGC
Command line: -Dclassworlds.conf=/Users/kimmking/tools/apache-maven-3.5.0/bin/m2.conf -Dmaven.home=/Users/kimmking/tools/apache-maven-3.5.0 -Dmaven.multiModuleProjectDirectory=/Users/kimmking/gateway/spring-cloud-gateway-demo/netty-server
```

可以看到所有的系统属性和启动使用的 VM 参数、命令行参数。非常有利于我们排查问题，特别是去排查一个已经运行的 JVM 里问题，通过 jinfo 我们就知道它依赖了哪些库，用了哪些参数启动。

如果在 Mac 和 Linux 系统上使用一直报错，则可能是没有权限，或者 jinfo 版本和目标 JVM 版本不一致的原因，例如：

```vbnet
Error attaching to process:
  sun.jvm.hotspot.runtime.VMVersionMismatchException:
    Supported versions are 25.74-b02. Target VM is 25.66-b17
```

### jrunscript 和 jjs 工具

jrunscript 和 jjs 工具用来执行脚本，只要安装了 JDK 8+，就可以像 shell 命令一样执行相关的操作了。这两个工具背后，都是 JDK 8 自带的 JavaScript 引擎 Nashorn。

执行交互式操作：

```shell
$ jrunscript
nashorn> 66+88
154
```

或者：

```shell
$ jjs
jjs> 66+88
154
```

按 CTRL+C 或者输入 exit() 回车，退出交互式命令行。

其中 jrunscript 可以直接用来执行 JS 代码块或 JS 文件。比如类似 curl 这样的操作：

```shell
jrunscript -e "cat('http://www.baidu.com')"
```

或者这样：

```shell
jrunscript -e "print('hello,kk.jvm'+1)"
```

甚至可以执行 JS 脚本：

```bash
jrunscript -l js -f /XXX/XXX/test.js
```

而 jjs 则只能交互模式，但是可以指定 JavaScript 支持的 ECMAScript 语言版本，比如 ES5 或者 ES6。

这个工具在某些情况下还是有用的，还可以在脚本中执行 Java 代码，或者调用用户自己的 jar 文件或者 Java 类。详细的操作说明可以参考：

> [jrunscript - command line script shell](https://docs.oracle.com/javase/7/docs/technotes/tools/share/jrunscript.html)

如果是 JDK 9 及以上的版本，则有一个更完善的 REPL 工具——JShell，可以直接解释执行 Java 代码。

而这些性能诊断工具官方并不提供技术支持，所以如果碰到报错信息，请不要着急，可以试试其他工具。不行就换 JDK 版本。

