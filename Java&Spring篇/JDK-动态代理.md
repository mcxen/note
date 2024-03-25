## 1. 简介

[![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/1759273-20210926231348224-1147358468.png)](https://img2020.cnblogs.com/blog/1759273/202109/1759273-20210926231348224-1147358468.png)

代理模式的定义：为其他对象提供一种`代理`以控制对这个对象的访问。在某些情况下，一个对象不适合或者不能直接引用另一个对象，而代理对象可以在客户端和目标对象之间起到中介的作用。

比如：我们在调用底层框架方法时候，需要在调用方法的前后打印日志，或者做一些逻辑判断。此时我们无法去修改底层框架方法，那我们可以通过封装一个代理类，在代理类中实现对方法的处理，然后所有的客户端通过代理类去调用目标方法。

其中这里有几个对象：

1. `抽象角色`：通过接口或者抽象类声明真实角色实现的业务方法，尽可能的保证代理对象的内部结构和目标对象一致，这样我们对代理对象的操作最终都可以转移到目标对象上。
2. `代理角色/代理对象`：实现抽象角色，是真实角色的代理，实现对目标方法的增强。
3. `真实角色/目标对象`：实现抽象角色，定义真实角色所要实现的业务逻辑，供代理角色调用。
4. `调用角色/客户端`：调用代理对象。

## 2. 静态代理

### 2.1 业务场景

假设现在有这么个场景：`王淑芬`打算`相亲`，但是自己嘴笨，于是找到`媒婆`，希望`媒婆`帮自己找个帅哥，于是找到了`张铁牛`。

角色分析：

- 王淑芬：目标对象（被代理的人）。
- 媒婆：代理对象（代理了王淑芬，实现对目标方法的增强）。
- 张铁牛：客户端（访问代理对象，即找媒婆）。
- 抽象角色（相亲）：`媒婆`和`王淑芬`的共同目标-相亲成功。

[![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/1759273-20210926231408860-912380257.png)](https://img2020.cnblogs.com/blog/1759273/202109/1759273-20210926231408860-912380257.png)

### 2.2 代码实现

1. 相亲接口

   ```java
   /**
    * 相亲抽象类
    */
   public interface BlindDateService {
      /**
       * 聊天
       */
      void chat();
   }
   ```

2. 目标对象

   ```java
   /**
    * 王淑芬 - 目标对象
    */
   @Slf4j
   public class WangShuFen implements BlindDateService {
      @Override
      public void chat() {
         log.info("美女：王淑芬～");
      }
   }
   ```

3. 代理对象

   

   ```java
   @Slf4j
   public class WomanMatchmaker implements BlindDateService {
      private BlindDateService bs;
   
      public WomanMatchmaker() {
         this.bs = new WangShuFen();
      }
   
      @Override
      public void chat() {
         this.introduce();
          //这里就是静态代理，直接把中间需要代理交代的事情写死了
         bs.chat();
         this.praise();
      }
   
      /**
       * 介绍
       */
      private void introduce() {
         log.info("媒婆：她的工作是web前端～");
      }
   
      /**
       * 夸人
       */
      private void praise() {
         log.info("媒婆：她就是有点害羞～");
         log.info("媒婆：你看她人长的漂亮，而且温柔贤惠，上的厅堂下的厨房～");
         log.info("媒婆：而且写bug超厉害～");
      }
   }
   ```

4. 客户端

   

   ```java
   /**
    * 张铁牛 - client
    */
   public class ZhangTieNiu {
      public static void main(String[] args) {
         WomanMatchmaker wm  = new WomanMatchmaker();
         wm.chat();
      }
   }
   ```

5. 执行方法输出内容如下：

   

   ```
   22:44:51.184 [main] INFO proxy.staticp.WomanMatchmaker - 媒婆：她的工作是web前端～
   22:44:51.191 [main] INFO proxy.staticp.WangShuFen - 美女：你好，我叫王淑芬～
   22:44:51.191 [main] INFO proxy.staticp.WomanMatchmaker - 媒婆：她就是有点害羞～
   22:44:51.191 [main] INFO proxy.staticp.WomanMatchmaker - 媒婆：你看她人长的漂亮，而且温柔贤惠，上的厅堂下的厨房～
   22:44:51.191 [main] INFO proxy.staticp.WomanMatchmaker - 媒婆：而且写bug超厉害～
   ```

### 2.3 小节

好处：

1. 耦合性降低。因为加入了代理类，调用者只用关心代理类即可，降低了调用者与目标类的耦合度。
2. 指责清晰，目标对象只关心真实的业务逻辑。代理对象只负责对目标对象的增强。调用者只关心代理对象的执行结果。
3. 代理对象实现了对目标方法的增强。也就是说：**代理对象 = 增强代码 + 目标对象**。

缺陷：

每一个目标类都需要写对应的代理类。如果当前系统已经有成百上千个类，工作量大太。所以，能不能不用写那么多代理类，就能实现对目标方法的增强呢？

## 3. 动态代理

我们常见的动态代理一般有两种：`JDK动态代理`和`CGLib动态代理`，本章只讲`JDK动态代理`。

在了解`JDK动态代理`之前，先了解两个重要的类。

### 3.1 Proxy

[![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/1759273-20210926231432440-1622109540.png)](https://img2020.cnblogs.com/blog/1759273/202109/1759273-20210926231432440-1622109540.png)

从JDK的帮助文档中可知：

`Proxy`提供了创建动态代理和实例的静态方法。即：

```java
Proxy::newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h)
```

**参数：**

- `loader` - 定义代理类的类加载器
- `interfaces` - 代理类要实现的接口列表
- `h` - 指派方法调用的调用处理程序

前两个参数没啥好说的，主要还需要了解下`InvocationHandler`。

### 3.2 InvocationHandler![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/1759273-20210926231453433-1235986022.png)

从JDK的帮助文档中可知：

`InvocationHandler`是一个接口，并且是**调用处理逻辑**实现的接口。在调用代理对象方法时，实际上是调用实现接口的`invoke`方法。

```
Object InvocationHandler::invoke(Object proxy, Method method, Object[] args)
```

**参数：**

- `proxy` - 调用方法的代理实例。
- `method` - 对应于在代理实例上调用的接口方法的 `Method` 实例。 `Method` 对象的声明类将是在其中声明方法的接口，该接口可以是代理类赖以继承方法的代理接口的超接口。
- `args` - 包含传入代理实例上方法调用的参数值的对象数组，如果接口方法不使用参数，则为 `null`。基本类型的参数被包装在适当基本包装器类（如 `java.lang.Integer` 或 `java.lang.Boolean`）的实例中。

**返回值：**

从代理实例的方法调用返回的值。如果接口方法的声明返回类型是基本类型，则此方法返回的值一定是相应基本包装对象类的实例；否则，它一定是可分配到声明返回类型的类型。如果此方法返回的值为 `null` 并且接口方法的返回类型是基本类型，则代理实例上的方法调用将抛出 `NullPointerException`。否则，如果此方法返回的值与上述接口方法的声明返回类型不兼容，则代理实例上的方法调用将抛出 `ClassCastException`。

### 3.3 代码实现



```java
/**
 * 王淑芬 - 目标对象
 */
@Slf4j
public class WangShuFen implements BlindDateService {
   @Override
   public void chat() {
      log.info("美女：王淑芬～");
   }
}
```



1. 创建动态代理类

   

   ```java
   import lombok.extern.slf4j.Slf4j;
   import java.lang.reflect.InvocationHandler;
   import java.lang.reflect.Method;
   import java.lang.reflect.Proxy;
   import java.util.Arrays;
   
   /**
    * 媒婆 - 代理对象
    */
   @Slf4j
   public class ProxyTest {
   
      public static Object getProxy(final Object target) {
         return Proxy.newProxyInstance(
            //类加载器
            target.getClass().getClassLoader(),
            //让代理对象和目标对象实现相同接口
            target.getClass().getInterfaces(),
            //代理对象的方法最终都会被JVM导向它的invoke方法
            new InvocationHandler() {
               @Override
               public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                  log.info("proxy ===》{}", proxy.getClass().toGenericString());
                  log.info("method ===》{}", method.toGenericString());
                  log.info("args ===》{}", Arrays.toString(args));
                  introduce();
                  // 调用目标方法
                  Object result = method.invoke(target, args);
                  praise();
                  return result;
               }
            });
      }
   
      /**
       * 介绍
       */
      private static void introduce() {
         log.info("媒婆：她的工作是web前端～");
      }
   
      /**
       * 夸人
       */
      private static void praise() {
         log.info("媒婆：她就是有点害羞～");
         log.info("媒婆：你看她人长的漂亮，而且温柔贤惠，上的厅堂下的厨房～");
         log.info("媒婆：而且写bug超厉害～");
      }
   }
   ```

2. 客户端

   

   ```java
   /**
    * 张铁牛 - client
    */
   public class ZhangTieNiu {
      public static void main(String[] args) {
         BlindDateService proxy = (BlindDateService)ProxyTest.getProxy(new WangShuFen());
         proxy.chat();
      }
   }
   ```

3. 执行方法输出内容如下：

   

   ```bash
   21:29:22.222 [main] INFO proxy.dynamic.ProxyTest - proxy ===》public final class com.sun.proxy.$Proxy0
   21:29:22.229 [main] INFO proxy.dynamic.ProxyTest - method ===》public abstract void proxy.dynamic.BlindDateService.chat()
   21:29:22.229 [main] INFO proxy.dynamic.ProxyTest - args ===》null
   21:29:22.229 [main] INFO proxy.dynamic.ProxyTest - 媒婆：她的工作是web前端～
   21:29:22.229 [main] INFO proxy.dynamic.WangShuFen - 美女：你好，我叫王淑芬～
   21:29:22.230 [main] INFO proxy.dynamic.ProxyTest - 媒婆：她就是有点害羞～
   21:29:22.230 [main] INFO proxy.dynamic.ProxyTest - 媒婆：你看她人长的漂亮，而且温柔贤惠，上的厅堂下的厨房～
   21:29:22.230 [main] INFO proxy.dynamic.ProxyTest - 媒婆：而且写bug超厉害～
   ```