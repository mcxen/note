# OOP基础知识
类只允许继承一个对象，不允许继承多个对象，
public class a extends c,d(){}

这是错误的，继承关系也最好在三层以内。

java多态
 - 方法的多态：重载OverLoading 和 覆写 override
 - 对象的多态：向上转型，向下转型，

# 反射
常用函数代码
```java
Class personClass = person.class;
person p = personClass.newInstance();

Properties pro = new Properties();
//1.2加载配置文件，转换为一个集合（双列map集合，Properties本身就是map的子类）
//1.2.1获取Class目录下的配置文件（用类加载器完成）
//获取字节码文件对应的类加载器：类加载器把字节码文件加载进内存，返回一个ClassLoader
ClassLoader classLoader = ReflectTest.class.getClassLoader(); 
//用ClassLoader找配置文件字节流文件
InputStream is = classLoader.getResourceAsStream("pro.properties");
//把字节流传进去即完成加载，抛出异常
pro.load(is);·

//3.加载该类进内存
Class cls = Class.forName(className);
//4.创建对象
Object obj = cls.newInstance();

//class.newInstance在Java9之后已经被弃用了 
clazz.getDeclaredConstructor().newInstance();
//5.获取方法对象
Method method = cls.getMethod(methodName);
//6.执行方法
method.invoke(obj);
```

# 常见API
导包，创建，使用
``` java
import java.util.Scanner
//Scanner输入需要传参数，参数可以是键盘也可以是互联网输入
Scanner scan= = new Scanner(System.in);


```
## STring
字符串常量池，程序当中直接写上的双引号字符串，就在字符串常量池。
```java
String st1="abc";
String st2="abc";
System.out.println(st1==st2);//true!!
//这里就是想等的。
//对于基本类型 ==是对于数值的比较
//对于引用类型，==是对于地址的比较
```
![](https://gcore.jsdelivr.net/gh/mcxen/image@main/20220816190356.png)

### 对于String的方法：equals

- 区分大小写的
- equalsIgnoreCase就可以胡咧

建议使用"hello".equals(str2);这种的，不建议str2.equals("hello");这样可以避免str2没有初始化的时候java编译器报错NullPointerException。
### concat 拼接字符串

### charAt

### indexOf(String str)
查找参数字符串在本字符串首次出现的索引位置，如果没有就返回-1值。


## ArrayList-- 可变长度数组
ArrayList的范型必须是引用类型比如类类型，不能是基本类型，比如ArrayList<int>这样是错的。

’ 
我们已经知道，Java的数据类型分两种：

基本类型：byte，short，int，long，boolean，float，double，char

引用类型：所有class和interface类型

引用类型可以赋值为null，表示空，但基本类型不能赋值为null：
```java
String s = null;
int n = null; // compile error!
那么，如何把一个基本类型视为对象（引用类型）？
```
比如，想要把int基本类型变成一个引用类型，我们可以定义一个Integer类，它只包含一个实例字段int，这样，Integer类就可以视为int的包装类（Wrapper Class）：

ArrayList 基本类型没有地址值，所以存不了，要使用基本类型需要使用包装类。

(包装类是属于java.lang.*里面的，所以不用导入这个包就可以直接使用)
| 基本数据类型 | 包装类    |
|--------------|-----------|
| byte         | Byte      |
| short        | Short     |
| int          | Integer   |
| long         | Long      |
| float        | Float     |
| double       | Double    |
| char         | Character |
| boolean      | Boolean   |

Auto Boxing
因为int和Integer可以互相转换：
```java
int i = 100;
Integer n = Integer.valueOf(i);
int x = n.intValue();
//所以，Java编译器可以帮助我们自动在int和Integer之间转型：

Integer n = 100; // 编译器自动使用Integer.valueOf(int)
int x = n; // 编译器自动使用Integer.intValue()
//这种直接把int变为Integer的赋值写法，称为自动装箱（Auto Boxing），反过来，把Integer变为int的赋值写法，称为自动拆箱（Auto Unboxing）。

```



``` java
ArrayList<Interger> list = new ArrayList<>();
System.out.println(list)
//直接输出的不是地址值，而是内容，内容是空的话，输出的是空的中括号:[]
//add方法，添加数据
list.add(3);
//获取 read and NOT remove
list.get();
list.remove();//返回对应位置并且删除

```
ArrayList作者进行了tostring方法处理，



# java
## 一、基本数据类型：
byte：Java中最小的数据类型，在内存中占8位(bit)，即1个字节，取值范围-128~127，默认值0

short：短整型，在内存中占16位，即2个字节，取值范围-32768~32767，默认值0

int：整型，用于存储整数，在内在中占32位，即4个字节，取值范围-2147483648~2147483647，默认值0

long：长整型，在内存中占64位，即8个字节-263~263-1，默认值0L

float：浮点型，在内存中占32位，即4个字节，用于存储带小数点的数字（与double的区别在于float类型有效小数点只有6~7位），默认值0
double：双精度浮点型，用于存储带有小数点的数字，在内存中占64位，即8个字节，默认值0
char：字符型，用于存储单个字符，占16位，即2个字节，取值范围0~65535，默认值为空
boolean：布尔类型，占1个字节，用于判断真或假（仅有两个值，即true、false），默认值false

## 二、引用数据类型：
类、接口类型、数组类型、枚举类型、注解类型。
区别：

基本数据类型在被创建时，在栈上给其划分一块内存，将数值直接存储在栈上。

引用数据类型在被创建时，首先要在栈上给其引用（句柄）分配一块内存，而对象的具体信息都存储在堆内存上，然后由栈上面的引用指向堆中对象的地址。

相关知识：
静态区： 保存自动全局变量和 static 变量（包括 static 全局和局部变量）。静态区的内容在总个程序的生命周期内都存在，由编译器在编译的时候分配。

堆区： 一般由程序员分配释放，由 malloc 系列函数或 new 操作符分配的内存，其生命周期由 free 或 delete 决定。在没有释放之前一直存在，直到程序结束，由OS释放。其特点是使用灵活，空间比较大，但容易出错

栈区： 由编译器自动分配释放，保存局部变量，栈上的内容只在函数的范围内存在，当函数运行结束，这些内容也会自动被销毁，其特点是效率高，但空间大小有限

文字常量区： 常量字符串就是放在这里的。 程序结束后由系统释放。