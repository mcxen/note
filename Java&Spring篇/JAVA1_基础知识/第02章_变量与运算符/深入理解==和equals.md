# 两个对象 hashcode 相同，他们就一定相等吗？

> 来自：
>
> [rebooters博客](https://rebooters.github.io/2020/02/25/String-valueOf-%E6%96%B9%E6%B3%95/)

结合 String 不变的特性，从 String.valueOf 所引发的一个小 bug 出发，再次探索一下 == 和 equals 的区别 ，加深理解。

## 前言

Java 中 String 类提供了一系列 valueOf 方法，方便开发者非常方便的将意义对象转换为 String。

<img src="https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/valueof.png" alt="img" style="zoom: 33%;" />

## == vs equals

```java
private static void testOnStringValueOf() {
    char a = 'a';
    char b = 'a';

    System.out.println("a==b " + (a == b));

    String ab = "aa";
    String a1 = String.valueOf(ab.charAt(0));
    String a2 = String.valueOf(ab.charAt(1));

    System.out.println("a1==a2 " + (a1 == a2));//a1和a2的内存地址不一样，String valueOf是新建的
    System.out.println("a1.equals(a2) " + (a1.equals(a2)));

    System.out.println("1==1 ? " + (String.valueOf(1) == String.valueOf(1)));
    System.out.println("1eq1 ? " + (String.valueOf(1).equals(String.valueOf(1))));

    String s1 = "a";
    String s2 = "a";

    System.out.println("s1==s2 " + (s1 == s2));
    System.out.println("s1.equals(s2) " + (s1.equals(s2)));
}
```

可以事先在大脑中模拟输出一下结果

实际输出：

```shell
a==b true
a1==a2 false
a1.equals(a2) true
1==1 ? false
1eq1 ? true
s1==s2 true
s1.equals(s2) true
```

a1 a2 和 s1 s2 到底有什么区别，为什么比较结果不一致呢？

我们回顾一下 == 和 equals 的区别

- 对于基本类型的数据，我们用 >,>=, <,<=, == 进行相等相等性的比较。 比如

  ```
  int a = 4;
  int b = 5;
  boolean result = a >= 5;
  result = a == b
  ```

- 对于对象(或者说是引用)类型的数据，当我们用 == 进行相等性比较时，其实是在比较对象在内存中地址，因此指向堆上两个不同对象的引用（或者说是指针）的大小一定是不同的。

~~~java
public class People {
    private String name;

    public People(String name) {
        this.name = name;
    }
}

Object obj1 = new People("mike"); 
Object obj2 = new People("mike");
Object obj3 = obj2;

System.out.println("====" + obj1.hashCode());
System.out.println("====" + obj2.hashCode());
System.out.println("====" + obj3.hashCode());

System.out.println("obj1 == obj2 ? =" + (obj1 == obj2));
System.out.println("obj3 == obj2 ? =" + (obj3 == obj2));
``` 

结果 

```shell
====1625635731
====1580066828
====1580066828
obj1 == obj2 ? =false
obj3 == obj2 ? =true
~~~

obj1 和 obj2 是通过 new 操作符创建的两个完全不同的对象，因此他们再内存中的地址必然是不一样的，因此直接进行 **==** 结果肯定是完全不相同。而 obj3 通过 **=** 赋值操作符，相等于和 obj2 指向了完全相同的地址，因此他们是相等的。

~~这里内存地址的说法似乎比较玄学，我们就理解为对象的 hashCode 好了，至于这个值是怎么计算的，暂时不展开了。~~

- 对于对象类型的数据，当我们用 equals() 方法进行比较时，就是比较其**内容**是否真的相等了。那么内容相等是什么意思呢？别着急，先看个代码

```java
System.out.println("obj1.equals(obj2) ? ==" +(obj1.equals(obj2)));
System.out.println("obj3.equals(obj2) ? ==" +(obj3.equals(obj2)));
```

结果：

```
obj1.equals(obj2) ? ==false
obj3.equals(obj2) ? ==true
```

结果似乎和 **==** 比较的结果一样啊。是的，默认就是一样的。因为Java 中超级父类 Object 进行 equals 比较时，默认进行的就是 == 比较。

```
public boolean equals(Object obj) {
    return (this == obj);
}
```

🤣🤣🤣🤣🤣🤣🤣🤣🤣🤣

现在来说**内容相同**这件事。什么叫内容相同呢？其实就是看定义类的作者如何去限定。比如这里的 People，当两个对象的 name 字段相同时我们便认为二者相等。

```java
public class People {
    private String name;

    public People(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        People other = (People) obj;
        return this.name.equals(other.name);
    }
}
```

这时候，再运行刚才的 equals 比较代码，就可以得到两个对象相同的结果了。当然，对一个复杂的对象，有时候我们可能还需要根据其他字段综合决定。但是一般情况下我们都会根据某个特定的字段决定，比如商品 id ,或订单 id 这样符合实际意义的字段确定。

对于一般的相等性比较场景，覆写 equals 似乎已经足够了。但是你一定看到过这种说法**覆写 equals()方法的同时， 一般需要覆写 hashcode()方法**。 为什么一定要覆写 hashcode() 方法呢。因为所有以散列表（或者说 哈希表）为结构的数据集合（或者说是数据容器），其中 key 的相等性比较涉及 hashcode() 方法。比如这里的 People 对象，由于我们没有覆写 hashcode ,当我们用 People 对象作为哈希表的 key 时，两个相等的对象会被当做不相等的对象。因此，这里可以简单处理，按照 equals 比较中内容的 hashcode 决定。

```
@Override
public int hashCode() {
    return this.name.hashCode();
}
```

话说回来，大部分情况我们都会用 String 作为 HashMap 的 key。这又是为何呢？

## String

看一下 String 类的 equals 方法和 hashcode 方法

```java
public boolean equals(Object anObject) {
    //equals给了两次机会，一次是内存地址是不是相同的。
      if (this == anObject) {
          return true;
      }
    //一次是根据char一个一个对比的
      if (anObject instanceof String) {
          String anotherString = (String)anObject;
          int n = value.length;
          if (n == anotherString.value.length) {
              char v1[] = value;
              char v2[] = anotherString.value;
              int i = 0;
              while (n-- != 0) {
                  if (v1[i] != v2[i])
                      return false;
                  i++;
              }
              return true;
          }
      }
      return false;
  }

  public int hashCode() {
      int h = hash;
      if (h == 0 && value.length > 0) {
          char val[] = value;

          for (int i = 0; i < value.length; i++) {
              h = 31 * h + val[i];
          }
          hash = h;
      }
      return h;
}
```

**注意，这里的 value 是一个 char[],它存储的就是字符串的实际值**

- equals() 可以看到字符串相等性比较，首先会进行 **==** 比较，也就是**先比较两个 String 的内存地址，在内存地址不相等的情况下**，是**严格按照字符串中的每一个 char 进行精确比较的。**
- hashcode() 和字符串的实际值强相关，同时从算法也可以看出，两个字符串如果内容完全一致，那么他们的 hashcode 一定是相等的，而如果连个字符串哪怕相差一个字符，二者的 hashcode 可能会有天壤之别。

再来回顾一下最开始的代码：

```java
String ab = "aa";
String a1 = String.valueOf(ab.charAt(0));
String a2 = String.valueOf(ab.charAt(1));
//这里其实a1和a2都是新建的String，当前内存地址就不一样了
System.out.println("a1 hashcode = "+(a1.hashCode()));
System.out.println("a2 hashcode = "+(a2.hashCode()));
System.out.println("a1 == a2 " + (a1 == a2)); //内存地址不一样，肯定就是false
System.out.println("a1.equals(a2) " + (a1.equals(a2)));

String s1 = "a";
String s2 = "a";//这里其实内存地址不变，因为还是同一个“a"

System.out.println("s1 hashcode = "+(s1.hashCode()));
System.out.println("s2 hashcode = "+(s2.hashCode()));
System.out.println("s1 == s2 " + (s1 == s2));
System.out.println("s1.equals(s2) " + (s1.equals(s2)));
```

结果：

```
a1 hashcode = 97
a2 hashcode = 97
a1 == a2 false
a1.equals(a2) true
s1 hashcode = 97
s2 hashcode = 97
s1 == s2 true
s1.equals(s2) true
```

**注意 a1,a2,s1,s2 这个四个对象的 hashcode 是相同的，至于为什么是 97，按照上面的算法很容易就看出来了**，所以 hashcode 不一定是一长串内容，有时候可能很简单。

a1.equals(a2) 和 s1.equals(s2) 的结果没有什么好说的，这里看一下 **==** 比较的差异从何而来。

这里首先看 s1 和 s2 两个不同的引用，但是 ”a” 在内存中的地址是唯一的，因此他们其实是指向了同一块内存区域，也就是这两个指针是相等的。因此 s1 == s2 结果为 true 。再看一下 a1 和 a2 ，同样的内容 a 发生了什么呢？看一下 String.valueOf() 的实现。

```java
public static String valueOf(char c) {
    char data[] = {c};
    return new String(data, true);
}
```

可以看到这是<mark>完全返回了一个新的对象</mark>，因此 a1 和 a2 这两个引用完全指向了不同的对象。因此 a1 == a2 的结果就必然为 false 了。

**其实，由于 String 是 final 的，因此关于 String 的所有操作都会创建新对象，== 的比较结果必然是 false**