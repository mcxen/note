# JUC并发编程
# 线程池

线程池节省创建线程开支，一次打印1000条日期

```java
public class ThreadLocalUseage {
    public static ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        //线程一
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    String date = new ThreadLocalUseage().date(finalI+100);
                    System.out.println("date = " + date);
                }
            });
        }
        threadPool.shutdown();
    }
    //用两个线程打印生日日期
    public String date(int seconds){
        //毫秒，从1970-1-1 ：0：0:0
        Date date = new Date(1000 * seconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(date);
    }
}
```





## 线程池基本概念

**概念**：线程池主要是控制运行线程的数量，将待处理任务放到等待队列，然后创建线程执行这些任务。如果超过了最大线程数，则等待。

**优点**：

1. 线程复用：不用一直new新线程，重复利用已经创建的线程来降低线程的创建和销毁开销，节省系统资源。
2. 提高响应速度：当任务达到时，不用创建新的线程，直接利用线程池的线程。
3. 管理线程：可以控制最大并发数，控制线程的创建等。

**体系**：`Executor`→`ExecutorService`→`AbstractExecutorService`→`ThreadPoolExecutor`。`ThreadPoolExecutor`是线程池创建的核心类。类似`Arrays`、`Collections`工具类，`Executor`也有自己的**工具类`Executors`。**

## 线程池三种常用创建方式

**newFixedThreadPool**：使用`LinkedBlockingQueue`实现，

```java
public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads,
                                  0L, TimeUnit.MILLISECONDS,
                                  new LinkedBlockingQueue<Runnable>());
}
```

- `nThreads`：此线程池中可以同时执行的线程数。
- `0L`, `TimeUnit.MILLISECONDS`：代表空闲线程存活时间，这里设置为0毫秒，即只要线程池没有被关闭，空闲线程就会一直存活下去。
- `new LinkedBlockingQueue<Runnable>()`：指定一个无界队列作为任务缓存队列。



**newSingleThreadExecutor**：使用`LinkedBlockingQueue`实现，一池只有一个线程。

```java
public static ExecutorService newSingleThreadExecutor() {
    return new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>()));
}
```



**newCachedThreadPool**：使用`SynchronousQueue`实现，变长线程池。

```java
public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                 60L, TimeUnit.SECONDS,
                                 new SynchronousQueue<Runnable>());
}
```

## 线程池的行为常用方法

JUC中的线程池提供了一系列方法来管理和控制线程池的行为，其中包括：

1. `execute(Runnable task)`：将一个任务提交到线程池中执行。
2. `submit(Callable task)`：将一个Callable任务提交到线程池中执行，并返回一个Future对象，可以使用该对象来获取任务的执行结果。
3. `shutdown()`：关闭线程池，不再接受新任务，但是会处理已经提交到任务队列中的任务。
4. `shutdownNow()`：立即关闭线程池，尝试中断所有正在执行的任务并清空任务队列。
5. `isShutdown()`：判断线程池是否已经关闭。
6. `isTerminated()`：判断线程池是否已经彻底终止。
7. `awaitTermination(long timeout, TimeUnit unit)`：等待线程池终止，直到超时或者所有任务都完成。
8. `getActiveCount()`：获取当前活动的线程数。
9. `getPoolSize()`：获取当前线程池的大小。
10. `getTaskCount()`：获取已经提交到线程池的任务数量。
11. `prestartCoreThread()`：预启动一个核心线程，如果线程池中的线程数少于核心线程数，则会创建一个新的线程。
12. `prestartAllCoreThreads()`：预启动所有核心线程，如果线程池中的线程数少于核心线程数，则会创建一个或多个新的线程。

需要注意的是，在使用线程池时，需要根据具体情况选取合适的方法来管理和控制线程池的行为。例如，在关闭线程池前需要等待所有任务都执行完毕并且线程池处于终止状态才能安全地关闭线程池。



## 线程池创建的七个参数

| 参数            | 类型                     | 含义                 |
| --------------- | ------------------------ | -------------------- |
| corePoolSize    | int                      | 线程池核心线程数     |
| maximumPoolSize | int                      | 线程池最大线程数     |
| keepAliveTime   | long                     | 空闲线程存活时间     |
| unit            | TimeUnit                 | 空闲线程存活时间单位 |
| workQueue       | BlockingQueue<Runnable>  | 线程池等待队列       |
| threadFactory   | ThreadFactory            | 线程创建工厂         |
| handler         | RejectedExecutionHandler | 拒绝策略处理器       |

- `corePoolSize`：线程池的核心线程数，即线程池中保持活动状态的最小线程数。当提交任务时，线程池会创建新线程来执行任务，直到当前线程数等于核心线程数。

- `maximumPoolSize`：线程池中允许存在的最大线程数。如果当前需要执行的任务数超过了核心线程数，并且等待队列已满，则线程池可以创建更多线程来执行任务，直到达到最大线程数。

- `keepAliveTime`：当线程数大于核心线程数时，空闲线程在终止前等待新任务的最长时间。默认情况下，只有在有超过核心线程数的线程被创建时才会使用此参数。

- `unit`：用于指定`keepAliveTime`参数的时间单位。

- `workQueue`：任务等待队列，用于存放未执行的任务。当线程池中的线程数达到核心线程数时，新提交的任务将被加入到等待队列中。如果等待队列已满，则新提交的任务将由新创建的线程执行。

- `threadFactory`：用于创建新线程的工厂类。可以自定义工厂类来对线程进行定制化的设置。

- `handler`：拒绝策略处理器，用于处理无法执行的任务。当等待队列已满并且当前线程数等于最大线程数时，新提交的任务将被拒绝并交给拒绝策略处理器处理。默认情况下，线程池会使用`AbortPolicy`策略，该策略会直接抛出异常。

  

**理解**：线程池的创建参数，就像一个**银行**。

`corePoolSize`就像银行的“**当值窗口**“，比如今天有**2位柜员**在受理**客户请求**（任务）。如果超过2个客户，那么新的客户就会在**等候区**（等待队列`workQueue`）等待。当**等候区**也满了，这个时候就要开启“**加班窗口**”，让其它3位柜员来加班，此时达到**最大窗口**`maximumPoolSize`，为5个。如果开启了所有窗口，等候区依然满员，此时就应该启动”**拒绝策略**“`handler`，告诉不断涌入的客户，叫他们不要进入，已经爆满了。由于不再涌入新客户，办完事的客户增多，窗口开始空闲，这个时候就通过`keepAlivetTime`将多余的3个”加班窗口“取消，恢复到2个”当值窗口“。

## 线程池底层原理

**原理图**：上面银行的例子，实际上就是线程池的工作原理。

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/threadPool.png)

**流程图**：

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/threadPoolProcedure.png)

新任务到达→

如果正在运行的线程数小于`corePoolSize`，创建核心线程；大于等于`corePoolSize`，放入等待队列。

如果等待队列已满，但正在运行的线程数小于`maximumPoolSize`，创建非核心线程；大于等于`maximumPoolSize`，启动拒绝策略。

当一个线程无事可做一段时间`keepAliveTime`后，如果正在运行的线程数大于`corePoolSize`，则关闭非核心线程。

JUC**线程池实现线任务复用的原理**

线程池中包含一组工作线程，这些工作线程都处于等待状态，并且在任务队列中获取任务进行处理。当有新的任务被提交时，线程池会按照一定的策略从线程池中选择一个空闲的线程来执行该任务。如果所有的线程都正在执行任务或者任务队列已满，则线程池会根据预设的拒绝策略对新任务进行拒绝或抛出异常。

通过使用线程池技术，可以避免创建大量的线程并减少线程的上下文切换开销，从而提高程序的性能和稳定性。同时，线程池还可以根据应用程序的需求动态调整线程数量、任务队列长度以及其他参数，以适应不同的负载情况和性能要求。



线程池是一种用于管理和复用线程的机制，它可以提高应用程序的性能和稳定性。**线程池通常由以下几个组成部分**：

1. 任务队列：用于存储待执行的任务。线程池中的空闲线程会从任务队列中获取任务并执行。
2. 线程池管理器：负责创建、销毁和维护线程池中的线程。
3. 工作线程：实际执行任务的线程。当线程池启动时，会创建一些工作线程，并将它们加入到线程池中。
4. 任务接口：定义了任务的执行逻辑，通常是一个Runnable或Callable接口的实现类。
5. 线程池参数配置：包括线程池大小、任务队列容量、线程池最大大小、线程池最小空闲时间等参数配置。

### Executor框架

在Java中，线程池通常由Executor框架来实现。Java中的线程池框架包括以下几个类：

1. **Executor接口**：定义了一个执行任务的方法execute(Runnable)。
2. E**xecutorService接口**：继承自Executor接口，定义了提交任务、关闭线程池shutdown等操作。
3. **ThreadPoolExecutor类**：实现了ExecutorService接口，是Java中最基本的线程池实现类。
4. ScheduledExecutorService接口：继承自ExecutorService接口，定义了按照一定的时间间隔周期性地执行任务的功能。
5. ScheduledThreadPoolExecutor类：实现了ScheduledExecutorService接口，是Java中最基本的支持周期性任务的线程池实现类。

```sh
+-----------+
|  Executor |
+-----+-----+
      ^
      |
+-----+-----+
| ExecutorService |
+-----+-----+
      ^
      |
+-----+---------------------+
|    ThreadPoolExecutor |
+-----+---------------------+
      ^
      |
+-----+-----------------------------+
| ScheduledThreadPoolExecutor |
+-----------------------------------+
```



在这个继承和实现关系的流程图中，可以看到`Executor`接口作为所有执行器的基本接口，`ExecutorService`接口定义了一些更高级别的任务管理方法，并且它扩展了`Executor`接口。`ScheduledExecutorService`接口扩展了`ExecutorService`接口，支持周期性任务。`ThreadPoolExecutor`类实现了`ExecutorService`接口，是Java中最基本的线程池实现类。`ScheduledThreadPoolExecutor`类实现了`ScheduledExecutorService`接口，是Java中最基本的支持周期性任务的线程池实现类。

在这个继承和实现关系的流程图中，还可以看到`ScheduledFuture`接口表示一个可以周期性执行的延迟计算结果，并且它扩展了`Future`接口。`FutureTask`类实现了`RunnableFuture`接口，也就是同时实现了`Runnable`接口和`Future`接口，因此它既可以作为一个任务提交给线程池执行，也可以通过调用`get()`方法获取计算结果。



### **Executors工具类**

`java.util.concurrent.Executors`类是JUC中一个非常重要的工具类，它提供了一组静态方法用于创建不同类型的线程池。`Executors`类中定义了一些有用的工厂方法，可以简化线程池的创建过程并提高程序的可读性和可维护性。

下面是`Executors`类的一些常用方法：

1. `newFixedThreadPool(int n)`：创建一个固定大小的线程池，该线程池中最多同时运行n个线程。如果所有线程都处于活动状态，则新提交的任务将在队列中等待，直到有空闲线程可用。

2. `newSingleThreadExecutor()`：创建一个只有一个线程的线程池，该线程池按顺序逐个执行提交的任务。

3. `newCachedThreadPool()`：创建一个可以根据需要自动扩展或收缩线程数的线程池。该线程池中的线程在60秒内无活动时将被回收，并且新提交的任务将始终创建新线程。

4. `newScheduledThreadPool(int corePoolSize)`：创建一个支持周期性任务执行的线程池，该线程池能够以固定时间间隔周期性地执行任务。

5. `newSingleThreadScheduledExecutor()`：创建一个只有一个线程的线程池，该线程池能够以固定时间间隔周期性地执行任务。

<mark>使用`Executors`类创建线程池不仅可以大大简化线程池的创建过程，并且还能够提高程序的可读性和可维护性。</mark>当需要创建一个新的线程池时，开发人员可以选择合适的工厂方法，并使用该方法返回的线程池对象对任务进行管理和执行。



### JUC中的线程池状态



1. `RUNNING`：表示线程池处于正常运行状态，接受新任务并处理队列中的任务。
2. `SHUTDOWN`：表示线程池**正在关闭**，不再接受新的任务，但是会继续处理已经提交到任务队列中的任务。
3. `STOP`：表示线程池已经停止**，不再接受新任务，也不再处理**队列中的任务，并且会尝试中断正在执行的任务。
4. `TIDYING`：表示线程池**正在清理队列和资源**，等待所有任务都被完成。
5. `TERMINATED`：表示线程池已经**彻底终止**，所有的任务都已经完成。

这些状态是通过线程池的控**制状态位（ctl）**来进行设置和管理的。在`ThreadPoolExecutor`类中，使用了`CAS`操作来更新控制状态位，保证了状态的正确性和一致性。同时，`ThreadPoolExecutor`类还提供了一些方法用于获取当前线程池的状态，例如`isShutdown()`、`isTerminated()`等方法。

## 线程池的拒绝策略

JUC（Java Util Concurrent）线程池中的拒绝策略，是指当线程池中的执行任务队列已满，并且线程池中的线程数已达到最大线程数时，线程池需要在新的任务提交时采取的策略。

JUC线程池提供了以下四种默认的拒绝策略：

1. **AbortPolicy**（默认的拒绝策略）：当线程池中的任务队列已满，且线程池中的线程数已达到最大线程数时，新提交的任务将会被拒绝，并抛出RejectedExecutionException异常。

2. **CallerRunsPolicy**：当线程池中的任务队列已满，且线程池中的线程数已达到最大线程数时，在提交该任务的线程中直接执行该任务，而不是交给线程池中的其他线程来执行。

3. **DiscardOldestPolicy**：当线程池中的任务队列已满，且线程池中的线程数已达到最大线程数时，将队列中最老的任务从执行队列中删除，并尝试重新将该任务提交给线程池。

4. **DiscardPolicy**：当线程池中的任务队列已满，且线程池中的线程数已达到最大线程数时，直接丢弃新提交的任务，不做任何处理。

除了以上四种默认的拒绝策略，JUC线程池还支持自定义拒绝策略，通过实现RejectedExecutionHandler接口并重写其rejectedExecution()方法来实现。

## 实际生产使用哪一个线程池？-都是自定义ThreadPoolExecutor

**单一、可变、定长都不用**！原因就是`FixedThreadPool`和`SingleThreadExecutor`底层都是用`LinkedBlockingQueue`实现的，这个队列最大长度为`Integer.MAX_VALUE`，显然会导致OOM。所以实际生产一般自己通过`ThreadPoolExecutor`的7个参数，自定义线程池。

```java
ExecutorService threadPool=new ThreadPoolExecutor(2,5,
                        1L,TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>(3),
                        Executors.defaultThreadFactory(),
                        new ThreadPoolExecutor.AbortPolicy());
```

### 自定义线程池参数选择

对于CPU密集型任务(加密计算，HASH计算），最大线程数是CPU线程数的一到两倍，每个线程分配一个CPU。

对于IO密集型任务，尽量多配点，可以是CPU线程数*10。

## 钩子方法 Hook methods

在线程池的生命周期中，可以被扩展或重写的一些方法，可以在这些方法中添加自定义的逻辑，以满足特定的需求。

Java中的ThreadPoolExecutor类提供了几个可以扩展或重写的钩子方法，包括：

1. beforeExecute(Thread t, Runnable r)：在线程执行任务之前，会先调用该方法。可以在该方法中进行一些准备工作，比如记录日志、统计任务等。

2. afterExecute(Runnable r, Throwable t)：在线程执行任务之后，会调用该方法。可以在该方法中进行一些清理工作，比如释放资源、记录任务结束时间等。如果任务执行期间抛出了异常，则将异常作为参数传递给该方法。

3. terminated()：在线程池被关闭后，会调用该方法。可以在该方法中进行一些资源释放等收尾工作。

通过继承ThreadPoolExecutor类并重写这些钩子方法，我们可以针对具体业务场景做一些自定义的处理，比如在beforeExecute方法中根据任务类型来设置不同的线程优先级，在afterExecute方法中记录任务执行情况并做出统计等。

举个栗子

```java
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PauseableThreadPool extends ThreadPoolExecutor {
    private final ReentrantLock lock = new ReentrantLock();
    //ReentrantLock 是 Java 中的一个可重入锁，它允许同一个线程多次获得同一把锁，
    // 并在最后释放锁时完全释放。在多线程编程中，使用可重入锁可以避免死锁等问题，
    // 确保线程安全。
    private Condition unpaused = lock.newCondition();
//    表示线程不处于暂停状态的条件对象。
    private boolean isPause;
   //注意要重写构造方法哈，！！！！！

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        lock.lock();
        try {
            while (isPause) {
                //标记为想要暂停了，那我就等待不是暂停的信号
                unpaused.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

    }

    private void pause(){
        lock.lock();
        try{
            isPause=true;
        }finally {
            lock.unlock();
        }
    }
    public void resume(){
        lock.lock();
        try {
            isPause=false;
            unpaused.signalAll();
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        PauseableThreadPool pool = new PauseableThreadPool(10, 20, 10l, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("running");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        for (int i = 0; i < 10000; i++) {
            pool.execute(runnable);
        }
        try {
            Thread.sleep(1000);
            pool.pause();
            System.out.println("暂停了");
            Thread.sleep(1000);
            pool.resume();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
```





>ReentrantLock是Java中的一种锁机制，它允许线程在获取到锁之后再次获取同一个锁，而不会被阻塞。这种锁机制的主要作用是保护共享资源，避免多个线程同时对共享资源进行修改而导致数据不一致的情况。
>
>下面是一个使用ReentrantLock的示例代码：
>
>```java
>import java.util.concurrent.locks.ReentrantLock;
>
>public class Example {
>    private ReentrantLock lock = new ReentrantLock();
>
>    public void accessResource() {
>        lock.lock();
>        try {
>            // 访问共享资源的代码
>        } finally {
>            lock.unlock();
>        }
>    }
>}
>```
>
>在这个示例中，我们创建了一个名为Example的类，并在其中定义了一个ReentrantLock对象。然后，我们编写了一个accessResource()方法，在该方法中使用了lock()方法获得了锁对象，然后访问了共享资源。最后使用unlock()方法释放了锁。
>
>需要注意的是，在访问共享资源的代码块中可能会出现异常，如果不正确处理异常，将会导致锁无法释放，因此我们使用了try...finally语句块，确保在任何情况下锁都能够被正确地释放。
>
>此外，ReentrantLock还有其他高级特性，例如可以设置公平或非公平锁、支持可重入性、支持中断等，可以根据具体需求进行选择和配置。



>假设我们要实现一个银行账户类，该类有两个方法：一个方法用于存款，另一个方法用于取款。为了确保在多线程环境下对该账户对象进行访问时的安全性，我们需要使用锁来同步这两个方法。
>
>首先，在银行账户类中声明一个ReentrantLock对象：
>
>```java
>public class BankAccount {
>    private ReentrantLock lock = new ReentrantLock();
>    private double balance;
>
>    public BankAccount(double balance) {
>        this.balance = balance;
>    }
>
>    public void deposit(double amount) {
>        lock.lock(); // 获取锁
>        try {
>            balance += amount;
>        } finally {
>            lock.unlock(); // 释放锁
>        }
>    }
>
>    public void withdraw(double amount) {
>        lock.lock(); // 获取锁
>        try {
>            if (balance >= amount) {
>                balance -= amount;
>            } else {
>                System.out.println("Insufficient balance.");
>            }
>        } finally {
>            lock.unlock(); // 释放锁
>        }
>    }
>}
>```
>
>在BankAccount类中，deposit()和withdraw()方法都使用了lock()方法获取锁，并且在执行完相应的操作后再使用unlock()方法释放锁。这样可以确保同一时间只有一个线程能够执行这两个方法中的任意一个。
>
>接下来，我们创建两个线程，并启动它们：
>
>```java
>public class Test {
>    public static void main(String[] args) {
>        BankAccount account = new BankAccount(1000.0);
>        Runnable depositTask = new Runnable() {
>            @Override
>            public void run() {
>                account.deposit(500.0);
>            }
>        };
>
>        Thread t1 = new Thread(depositTask);
>
>        Thread t2 = new Thread(() -> account.withdraw(1500.0));
>        t1.start();
>        t2.start();
>    }
>}
>```
>
>在这段代码中，我们创建了一个BankAccount对象，并分别将deposit()和withdraw()方法封装到两个lambda表达式中，并作为参数传递给Thread的构造方法。接着，我们启动这两个线程。
>
>当t1线程执行完deposit()方法后，账户余额会增加500元，变成1500元。当t2线程执行完withdraw()方法后，由于账户余额不足，所以会输出"Insufficient balance."。
>
>在整个过程中，由于使用了ReentrantLock来对deposit()和withdraw()方法进行同步，所以可以确保在多线程环境下对BankAccount对象进行访问时的安全性。



## 关闭线程池

关闭线程池需要调用**ExecutorService**的`shutdown()`方法。这个方法会平滑地关闭线程池，不会中断正在执行的任务，但不会接受新的任务。

如果需要强制关闭线程池并中断正在执行的任务，可以调用ExecutorService的`shutdownNow()`方法。这个方法会尝试执行线程的中断操作，并返回等待执行的任务列表。

两种关闭线程池的方法示例代码如下：

```java
ExecutorService executorService = Executors.newFixedThreadPool(10);

// 关闭线程池
executorService.shutdown();

// 立即关闭线程池
executorService.shutdownNow();
```

在调用`shutdown()`方法之后，可以通过调用`awaitTermination()`方法设置等待时间，等待线程池中的任务执行完成后才能继续执行下面的代码，示例代码如下：

```java
executorService.shutdown();
try {
    if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
        executorService.shutdownNow();
    } 
} catch (InterruptedException e) {
    executorService.shutdownNow();
}
```

该示例中，先调用`executorService.shutdown()`方法关闭线程池，然后等待800毫秒，如果线程池还没有关闭，则调用`executorService.shutdownNow()`方法强制关闭线程池。如果线程池已经关闭，`awaitTermination()`方法会返回true，代码继续执行。如果中途发生中断，则**调用`executorService.shutdownNow()`方法强制关闭线程池。**

`isTerminated()`是ExecutorService接口中的一个方法，用于判断线程池是否已经全部停止。该方法返回一个boolean值，如果线程池已经停止，则返回true，否则返回false。

具体来说，当调用ExecutorService的`shutdown()`方法时，该线程池会开始关闭工作，但它不会立即退出运行。相反，它将继续处理已经提交的任务，直到所有任务都被处理完。isTerminated()方法的作用就是用于判断当前线程池是否已经处理完所有任务并停止了。

例如，如果我们使用以下代码关闭线程池后：

```java
ExecutorService executorService = Executors.newFixedThreadPool(10);
executorService.shutdown();
```

然后，我们可以使用以下代码检查线程池是否已经停止：

```java
while (!executorService.isTerminated()) {
    try {
        executorService.awaitTermination(1, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}
```

这个代码块中，我们使用ExecutorService的awaitTermination()方法等待线程池终止，然后我们使用isTerminated()方法检查它是否已经完成。如果没有完成，那么我们会继续等待，直到线程池完成所有任务并停止。



# ThreadLocal

## 面试问题

提示

请带着这些问题继续后文，会很大程度上帮助你更好的理解相关知识点。pdai

- 什么是ThreadLocal? 用来解决什么问题的?
- 说说你对ThreadLocal的理解
- ThreadLocal是如何实现线程隔离的?
- 为什么ThreadLocal会造成内存泄露? 如何解决
- 还有哪些使用ThreadLocal的应用场景?

##  ThreadLocal简介

我们在[Java 并发 - 并发理论基础]()总结过线程安全(是指广义上的共享资源访问安全性，因为线程隔离是通过副本保证本线程访问资源安全性，它不保证线程之间还存在共享关系的狭义上的安全性)的解决思路：

- 互斥同步: synchronized 和 ReentrantLock
- 非阻塞同步: CAS, AtomicXXXX
- 无同步方案: 栈封闭，本地存储(Thread Local)，可重入代码

这个章节将详细的讲讲 本地存储(Thread Local)。官网的解释是这样的：

> This class provides thread-local variables. These variables differ from their normal counterparts in that each thread that accesses one (via its {@code get} or {@code set} method) has its own, independently initialized copy of the variable. {@code ThreadLocal} instances are typically private static fields in classes that wish to associate state with a thread (e.g., a user ID or Transaction ID) 
>
> 该类提供了线程局部 (thread-local) 变量。这些变量不同于它们的普通对应物，因为访问某个变量(通过其 get 或 set 方法)的每个线程都有自己的局部变量，它独立于变量的初始化副本。ThreadLocal 实例通常是类中的 private static 字段，它们希望将状态与某一个线程(例如，用户 ID 或事务 ID)相关联。

总结而言：ThreadLocal是一个将在多线程中为每一个线程创建单独的变量副本的类; 当使用ThreadLocal来维护变量时, ThreadLocal会为每个线程创建单独的变量副本, 避免因多线程操作共享变量而导致的数据不一致的情况。

## ThreadLocal理解-举例1

### 场景一

```java

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadLocalUseage {
    public static ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        //线程一
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    String date = new ThreadLocalUseage().date(finalI+100);
                    System.out.println("date = " + date);
                }
            });
        }
        threadPool.shutdown();
    }
    //用两个线程打印生日日期
    public String date(int seconds){
        //毫秒，从1970-1-1 ：0：0:0
        Date date = new Date(1000 * seconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(date);
    }
}

```

> 问题就是想不需要sdf反复重新生成

于是我们把sdf时间格式放在前面，但是报错了。

![image-20230508170832798](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20230508170832798.png)



- 2 个线程分别用自己的 SimpleDateFormat，没有问题。
- 延伸到 10 个没有问题。
- 但是当需求变成了 1000 个，那么必然要用到**线程池**（不用到线程池将被创建1000次format）。
- 所有的线程都**共用同一个** SimpleDateFormat 对象（会发生线程不安全,出现并发问题，同一次时间出现，用 synchronized 加锁进行安全处理 => 加锁后正常，但是效率变低）。

<img src="https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/v2-c8a68a19e0f9518448dfdb6d1d592533_r.jpg" alt="img" style="zoom: 33%;" />

解决方法一：lock一下解决线程安全

<img src="https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20230508171927015.png" alt="image-20230508171927015" style="zoom:67%;" />

解决方法二：ThreadLocal一下

![image-20230508172144942](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20230508172144942.png)

```java
static class ThreadSafeFormatter{
//        这个类就是要生产出来线程安全的DateFormat
        //方法一
        public static ThreadLocal<SimpleDateFormat> dateFormatThreadLocal =
                new ThreadLocal<SimpleDateFormat>(){
                    //进行初始化函数
                    @Override
                    protected SimpleDateFormat initialValue() {
                        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    }
                };

        //方法二:Lambda 表达式
        public static ThreadLocal<SimpleDateFormat> dateFormatThreadLocal2
                = ThreadLocal.withInitial(()->new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));

    }
```





全部：

```java

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadLocalUseage {
    public static ExecutorService threadPool = Executors.newFixedThreadPool(10);

//    移到这里之后，时间会出错

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public static void main(String[] args) {
        //线程一
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    String date = new ThreadLocalUseage().date(finalI+100);
                    System.out.println("date = " + date);
                }
            });
        }
        threadPool.shutdown();
    }
    //用两个线程打印生日日期
    public String date(int seconds){
        //毫秒，从1970-1-1 ：0：0:0
        Date date = new Date(1000 * seconds);
        String format;
//        synchronized (ThreadLocalUseage.class){
//            format= sdf.format(date);
//        }
        SimpleDateFormat ssdf = ThreadSafeFormatter.dateFormatThreadLocal.get();
        format = ssdf.format(date);
        return format;
    }

    static class ThreadSafeFormatter{
//        这个类就是要生产出来线程安全的DateFormat
        //方法一
        public static ThreadLocal<SimpleDateFormat> dateFormatThreadLocal =
                new ThreadLocal<SimpleDateFormat>(){
                    //进行初始化函数
                    @Override
                    protected SimpleDateFormat initialValue() {
                        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    }
                };

        //方法二:Lambda 表达式
        public static ThreadLocal<SimpleDateFormat> dateFormatThreadLocal2
                = ThreadLocal.withInitial(()->new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));

    }
}
```



### 场景二

典型场景2：每个线程内需要保存**全局变量**（例如在拦截器中获取**用户信息**），可以让**不同方法直接使用**，**避免参数传递**的麻烦

我们的目标：每个线程内需要保存全局变量，可以让不同方法直接使用，避免参数传递的麻烦

#### 2.1 举例证明：

##### 2.1.1 当前用户信息需要被线程内所有方法共享

实例一：

- 一个比较繁琐的解决方案是把 user 作为参数**层层传递**，从 service-1() 传递到 service-2() ，再从 service-2() 传到 service-3() ，以此类推，但是这样做会导致**代码冗余**且**不易维护**

![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/v2-e5372da8fae439f6820a88860f503cf4_r.jpg)

实例二：

- 在此基础上，使用UserMap

![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/v2-046959ef3feb8bb08103f5e1a757b608_r.jpg)



- 当多线程同时工作时，我们需要保证线程安全，可以用 **synchronized**，也可以用 **ConcurrentHashMap**，但无论用什么，都会对**性能**有所影响

![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/v2-87558daf8ae03a8b1aa7a5d5df099129_r.jpg)



##### 2.1.2 采用更好的方法

更好的办法就是使用 ThreadLocal ，这样无需 **synchronized**，可以再不影响性能的情况下，也无需层层传递参数，就可达到保存当前线程对应的用户信息的目的

#### 2.2 使用方法：

- 用 ThreadLocal 保存一些业务内容（用户权限信息，从用户系统获取到的用户名，UserId等）。
- 这些信息在**同一个线程内相同**，但是不i同的线程使用的业务内容**不相同的**。
- **在线程生命周期内**，都通过这个静态 ThreadLocal 实例的 get() 方法取得自己 set 过的那个对象，**避免**了将这个对象(例如 User对象)作为**参数传递**的麻烦
- 此方法强调的是同一个请求内（同一个线程内）**不同方法**间的共享
- 不需重写 initialValue() 方法，但是必须手动调用 set() 方法

```text
//演示 ThreadLocal 用法2：避免传递参数的麻烦
public class ThreadLocalNormalUsage06 {
    public static void main(String[] args) {
        new Service1().process();
    }
}
//读取到用户信息
class Service1{
    public void process(){
        User user = new User("万机");
        UserContextHolder.holder.set(user);
        new Service2().process();
    }
}
class Service2{
    public void process(){
        UserContextHolder.holder.get();
        User user = UserContextHolder.holder.get();
        System.out.println("Service2 => " + user.name);
        new Service3().process();
    }
}
class Service3{
    public void process(){
        UserContextHolder.holder.get();
        User user = UserContextHolder.holder.get();
        System.out.println("Service3 => " + user.name);
    }
}
class UserContextHolder{
    public static ThreadLocal<User> holder = new ThreadLocal<>();
}
class User{
    String name;

    public User(String name) {
        this.name = name;
    }
}
```

### ThreadLocal 的两个作用

- 让某个需要用到的对象再**线程间隔离**（每个线程都有自己的独立的对象）
- 在任何方法中都可以**轻松获取**到该对象
- 回顾前面的两个场景，对应到这两个作用

根据共享对象的生成时机不同，选择 initialValue 或 set 来保存对象

场景一：initialValue

- 在 ThreadLocal 第一次 get 的时候把对象给初始化出来，对象的初始化时机可以**由我们控制**

场景二：set

- 如果需要保存到 ThreadLocal 里的对象的生成时机不由我们随意控制，例如拦截器生成的用户信息。
- 用 ThreadLocal.set 直接放到我们的 ThreadLocal 中去，以便后续使用。

### 使用 ThreadLocal 到来的好处

- 达到线程安全
- 不需要加锁，提高执行效率
- 更高效的利用内存，节省开销
- 相比于每个任务都新建一个 SimpleDateFormat ，显然用 ThreadLocal 可以节省内存和开销
- 免去传参的繁琐
- 不需要每次都传统样的参数
- Thread Local 是的代码耦合度更低，更优雅

### 主要方法解析

- initialValue()：初始化

- - 该方法会返回当前线程对用的“初始值”，这是一个延迟加载的方法，只有在调用 get 的时候，才会触发

  - - 不重写方法，返回值为 null

![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/v2-c68e98b47c6cf7addf2d41370e98bf0f_r.jpg)



- 当线程第一次使用 get 方法访问变量时，将调用此方法

- - 每次线程最多调用一次此方法，但如果已经调用了 remove() 后，再调用 get() ，则可以再次调用此方法
  - 如果不重写本方法，这个方法会返回 null 。一般使用匿名内部类的方法来重写 initialValue() 方法

- void set(T t)：为这个线程设置一个新值

- T get()：得到这个线程对应的 value。如果是首次调用 get() ，则会调用 initialize 来得到这个值

- void remove()：删除对应这个线程的值

```text
class Service2{
    public void process(){
        UserContextHolder.holder.get();
        User user = UserContextHolder.holder.get();
        System.out.println("Service2 => " + user.name);
        UserContextHolder.holder.remove();
        user = new User("王姐");
        UserContextHolder.holder.set(user);
        new Service3().process();
    }
}
```

## ThreadLocal理解-举例2

> 提到ThreadLocal被提到应用最多的是session管理和数据库链接管理，这里以数据访问为例帮助你理解ThreadLocal：

- 如下数据库管理类在单线程使用是没有任何问题的

```java
class ConnectionManager {
    private static Connection connect = null;

    public static Connection openConnection() {
        if (connect == null) {
            connect = DriverManager.getConnection();
        }
        return connect;
    }

    public static void closeConnection() {
        if (connect != null)
            connect.close();
    }
}
```

很显然，在多线程中使用会存在线程安全问题：第一，这里面的2个方法都没有进行同步，很可能在openConnection方法中会多次创建connect；第二，由于connect是共享变量，那么必然在调用connect的地方需要使用到同步来保障线程安全，因为很可能一个线程在使用connect进行数据库操作，而另外一个线程调用closeConnection关闭链接。

- 为了解决上述线程安全的问题，第一考虑：互斥同步

你可能会说，将这段代码的两个方法进行同步处理，并且在调用connect的地方需要进行同步处理，比如用Synchronized或者ReentrantLock互斥锁。

- 这里再抛出一个问题：这地方到底需不需要将connect变量进行共享?

事实上，是不需要的。假如每个线程中都有一个connect变量，各个线程之间对connect变量的访问实际上是没有依赖关系的，即一个线程不需要关心其他线程是否对这个connect进行了修改的。即改后的代码可以这样：

```java
class ConnectionManager {
    private Connection connect = null;

    public Connection openConnection() {
        if (connect == null) {
            connect = DriverManager.getConnection();
        }
        return connect;
    }

    public void closeConnection() {
        if (connect != null)
            connect.close();
    }
}

class Dao {
    public void insert() {
        ConnectionManager connectionManager = new ConnectionManager();
        Connection connection = connectionManager.openConnection();

        // 使用connection进行操作

        connectionManager.closeConnection();
    }
}
```

这样处理确实也没有任何问题，由于每次都是在方法内部创建的连接，那么线程之间自然不存在线程安全问题。但是这样会有一个致命的影响：导致服务器压力非常大，并且严重影响程序执行性能。由于在方法中需要频繁地开启和关闭数据库连接，这样不仅严重影响程序执行效率，还可能导致服务器压力巨大。

- 这时候ThreadLocal登场了

那么这种情况下使用ThreadLocal是再适合不过的了，因为ThreadLocal在每个线程中对该变量会创建一个副本，即每个线程内部都会有一个该变量，且在线程内部任何地方都可以使用，线程之间互不影响，这样一来就不存在线程安全问题，也不会严重影响程序执行性能。下面就是网上出现最多的例子：

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final ThreadLocal<Connection> dbConnectionLocal = new ThreadLocal<Connection>() {
        @Override
        protected Connection initialValue() {
            try {
                return DriverManager.getConnection("", "", "");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    public Connection getConnection() {
        return dbConnectionLocal.get();
    }
}
```

- 再注意下ThreadLocal的修饰符

ThreaLocal的JDK文档中说明：ThreadLocal instances are typically private static fields in classes that wish to associate state with a thread。如果我们希望通过某个类将状态(例如用户ID、事务ID)与线程关联起来，那么通常在这个类中定义private static类型的ThreadLocal 实例。

> 但是要注意，虽然ThreadLocal能够解决上面说的问题，但是由于在每个线程中都创建了副本，所以要考虑它对资源的消耗，比如内存的占用会比不使用ThreadLocal要大。

##  ThreadLocal原理

### 如何实现线程隔离

主要是用到了Thread对象中的一个ThreadLocalMap类型的变量threadLocals, 负责存储当前线程的关于Connection的对象, dbConnectionLocal(以上述例子中为例) 这个变量为Key, 以新建的Connection对象为Value; 这样的话, 线程第一次读取的时候如果不存在就会调用ThreadLocal的initialValue方法创建一个Connection对象并且返回;

具体关于为线程分配变量副本的代码如下:

```java
public T get() {
    Thread t = Thread.currentThread();
    ThreadLocalMap threadLocals = getMap(t);
    if (threadLocals != null) {
        ThreadLocalMap.Entry e = threadLocals.getEntry(this);
        if (e != null) {
            @SuppressWarnings("unchecked")
            T result = (T)e.value;
            return result;
        }
    }
    return setInitialValue();
}
```

- 首先获取当前线程对象t, 然后从线程t中获取到ThreadLocalMap的成员属性threadLocals
- 如果当前线程的threadLocals已经初始化(即不为null) 并且存在以当前ThreadLocal对象为Key的值, 则直接返回当前线程要获取的对象(本例中为Connection);
- 如果当前线程的threadLocals已经初始化(即不为null)但是不存在以当前ThreadLocal对象为Key的的对象, 那么重新创建一个Connection对象, 并且添加到当前线程的threadLocals Map中,并返回
- 如果当前线程的threadLocals属性还没有被初始化, 则重新创建一个ThreadLocalMap对象, 并且创建一个Connection对象并添加到ThreadLocalMap对象中并返回。

如果存在则直接返回很好理解, 那么对于如何初始化的代码又是怎样的呢?

```java
private T setInitialValue() {
    T value = initialValue();
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null)
        map.set(this, value);
    else
        createMap(t, value);
    return value;
}
```

- 首先调用我们上面写的重载过后的initialValue方法, 产生一个Connection对象
- 继续查看当前线程的threadLocals是不是空的, 如果ThreadLocalMap已被初始化, 那么直接将产生的对象添加到ThreadLocalMap中, 如果没有初始化, 则创建并添加对象到其中;

同时, ThreadLocal还提供了直接操作Thread对象中的threadLocals的方法

```java
public void set(T value) {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null)
        map.set(this, value);
    else
        createMap(t, value);
}
```

这样我们也可以不实现initialValue, 将初始化工作放到DBConnectionFactory的getConnection方法中:

```java
public Connection getConnection() {
    Connection connection = dbConnectionLocal.get();
    if (connection == null) {
        try {
            connection = DriverManager.getConnection("", "", "");
            dbConnectionLocal.set(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    return connection;
}
```

那么我们看过代码之后就很清晰的知道了为什么ThreadLocal能够实现变量的多线程隔离了; 其实就是用了Map的数据结构给当前线程缓存了, 要使用的时候就从本线程的threadLocals对象中获取就可以了, key就是当前线程;

当然了在当前线程下获取当前线程里面的Map里面的对象并操作肯定没有线程并发问题了, 当然能做到变量的线程间隔离了;

现在我们知道了ThreadLocal到底是什么了, 又知道了如何使用ThreadLocal以及其基本实现原理了是不是就可以结束了呢? 其实还有一个问题就是ThreadLocalMap是个什么对象, 为什么要用这个对象呢?

### ThreadLocalMap对象是什么

本质上来讲, 它就是一个Map, 但是这个ThreadLocalMap与我们平时见到的Map有点不一样

- 它没有实现Map接口;
- 它没有public的方法, 最多有一个default的构造方法, 因为这个ThreadLocalMap的方法仅仅在ThreadLocal类中调用, 属于静态内部类
- ThreadLocalMap的Entry实现继承了WeakReference<ThreadLocal<?>>
- 该方法仅仅用了一个Entry数组来存储Key, Value; Entry并不是链表形式, 而是每个bucket里面仅仅放一个Entry;

要了解ThreadLocalMap的实现, 我们先从入口开始, 就是往该Map中添加一个值:

```java
private void set(ThreadLocal<?> key, Object value) {

    // We don't use a fast path as with get() because it is at
    // least as common to use set() to create new entries as
    // it is to replace existing ones, in which case, a fast
    // path would fail more often than not.

    Entry[] tab = table;
    int len = tab.length;
    int i = key.threadLocalHashCode & (len-1);

    for (Entry e = tab[i];
         e != null;
         e = tab[i = nextIndex(i, len)]) {
        ThreadLocal<?> k = e.get();

        if (k == key) {
            e.value = value;
            return;
        }

        if (k == null) {
            replaceStaleEntry(key, value, i);
            return;
        }
    }

    tab[i] = new Entry(key, value);
    int sz = ++size;
    if (!cleanSomeSlots(i, sz) && sz >= threshold)
        rehash();
}
```

先进行简单的分析, 对该代码表层意思进行解读:

- 看下当前threadLocal的在数组中的索引位置 比如: `i = 2`, 看 `i = 2` 位置上面的元素(Entry)的`Key`是否等于threadLocal 这个 Key, 如果等于就很好说了, 直接将该位置上面的Entry的Value替换成最新的就可以了;
- 如果当前位置上面的 Entry 的 Key为空, 说明ThreadLocal对象已经被回收了, 那么就调用replaceStaleEntry
- 如果清理完无用条目(ThreadLocal被回收的条目)、并且数组中的数据大小 > 阈值的时候对当前的Table进行重新哈希 所以, 该HashMap是处理冲突检测的机制是向后移位, 清除过期条目 最终找到合适的位置;

了解完Set方法, 后面就是Get方法了:

```java
private Entry getEntry(ThreadLocal<?> key) {
    int i = key.threadLocalHashCode & (table.length - 1);
    Entry e = table[i];
    if (e != null && e.get() == key)
        return e;
    else
        return getEntryAfterMiss(key, i, e);
}
```

先找到ThreadLocal的索引位置, 如果索引位置处的entry不为空并且键与threadLocal是同一个对象, 则直接返回; 否则去后面的索引位置继续查找。

- 搞清楚 Thread，ThreadLocal 以及 ThreadLocalMap 三者之间的关系
- 每个 Thread 对象中都持有一个 ThreadLocalMap 成员变量

![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/v2-8aad71096b1ccb41786942b0579da433_r.jpg)



## ThreadLocal造成内存泄露的问题

网上有这样一个例子：

```java
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadLocalDemo {
    static class LocalVariable {
        private Long[] a = new Long[1024 * 1024];
    }

    // (1)
    final static ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5, 5, 1, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>());
    // (2)
    final static ThreadLocal<LocalVariable> localVariable = new ThreadLocal<LocalVariable>();

    public static void main(String[] args) throws InterruptedException {
        // (3)
        Thread.sleep(5000 * 4);
        for (int i = 0; i < 50; ++i) {
            poolExecutor.execute(new Runnable() {
                public void run() {
                    // (4)
                    localVariable.set(new LocalVariable());
                    // (5)
                    System.out.println("use local varaible" + localVariable.get());
                    localVariable.remove();
                }
            });
        }
        // (6)
        System.out.println("pool execute over");
    }
}
```

如果用线程池来操作ThreadLocal 对象确实会造成内存泄露, 因为对于线程池里面不会销毁的线程, 里面总会存在着`<ThreadLocal, LocalVariable>`的强引用, 因为final static 修饰的 ThreadLocal 并不会释放, 而ThreadLocalMap 对于 Key 虽然是弱引用, 但是强引用不会释放, 弱引用当然也会一直有值, 同时创建的LocalVariable对象也不会释放, 就造成了内存泄露; 如果LocalVariable对象不是一个大对象的话, 其实泄露的并不严重, `泄露的内存 = 核心线程数 * LocalVariable`对象的大小;

所以, 为了避免出现内存泄露的情况, ThreadLocal提供了一个清除线程中对象的方法, 即 remove, 其实内部实现就是调用 ThreadLocalMap 的remove方法:

```java
private void remove(ThreadLocal<?> key) {
    Entry[] tab = table;
    int len = tab.length;
    int i = key.threadLocalHashCode & (len-1);
    for (Entry e = tab[i];
         e != null;
         e = tab[i = nextIndex(i, len)]) {
        if (e.get() == key) {
            e.clear();
            expungeStaleEntry(i);
            return;
        }
    }
}
```

找到Key对应的Entry, 并且清除Entry的Key(ThreadLocal)置空, 随后清除过期的Entry即可避免内存泄露。

## ThreadLocal应用场景

除了上述的数据库管理类的例子，我们再看看其它一些应用：

### 每个线程维护了一个“序列号”

> 再回想上文说的，如果我们希望通过某个类将状态(例如用户ID、事务ID)与线程关联起来，那么通常在这个类中定义private static类型的ThreadLocal 实例。

每个线程维护了一个“序列号”

```java
public class SerialNum {
    // The next serial number to be assigned
    private static int nextSerialNum = 0;

    private static ThreadLocal serialNum = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new Integer(nextSerialNum++);
        }
    };

    public static int get() {
        return ((Integer) (serialNum.get())).intValue();
    }
}
```

### Session的管理

经典的另外一个例子：

```java
private static final ThreadLocal threadSession = new ThreadLocal();  
  
public static Session getSession() throws InfrastructureException {  
    Session s = (Session) threadSession.get();  
    try {  
        if (s == null) {  
            s = getSessionFactory().openSession();  
            threadSession.set(s);  
        }  
    } catch (HibernateException ex) {  
        throw new InfrastructureException(ex);  
    }  
    return s;  
}  
```

### [#](#在线程内部创建threadlocal) 在线程内部创建ThreadLocal

还有一种用法是在线程类内部创建ThreadLocal，基本步骤如下：

- 在多线程的类(如ThreadDemo类)中，创建一个ThreadLocal对象threadXxx，用来保存线程间需要隔离处理的对象xxx。
- 在ThreadDemo类中，创建一个获取要隔离访问的数据的方法getXxx()，在方法中判断，若ThreadLocal对象为null时候，应该new()一个隔离访问类型的对象，并强制转换为要应用的类型。
- 在ThreadDemo类的run()方法中，通过调用getXxx()方法获取要操作的数据，这样可以保证每个线程对应一个数据对象，在任何时刻都操作的是这个对象。

```java
public class ThreadLocalTest implements Runnable{
    
    ThreadLocal<Student> StudentThreadLocal = new ThreadLocal<Student>();

    @Override
    public void run() {
        String currentThreadName = Thread.currentThread().getName();
        System.out.println(currentThreadName + " is running...");
        Random random = new Random();
        int age = random.nextInt(100);
        System.out.println(currentThreadName + " is set age: "  + age);
        Student Student = getStudentt(); //通过这个方法，为每个线程都独立的new一个Studentt对象，每个线程的的Studentt对象都可以设置不同的值
        Student.setAge(age);
        System.out.println(currentThreadName + " is first get age: " + Student.getAge());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println( currentThreadName + " is second get age: " + Student.getAge());
        
    }
    
    private Student getStudentt() {
        Student Student = StudentThreadLocal.get();
        if (null == Student) {
            Student = new Student();
            StudentThreadLocal.set(Student);
        }
        return Student;
    }

    public static void main(String[] args) {
        ThreadLocalTest t = new ThreadLocalTest();
        Thread t1 = new Thread(t,"Thread A");
        Thread t2 = new Thread(t,"Thread B");
        t1.start();
        t2.start();
    }
    
}

class Student{
    int age;
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    
}
```

### [#](#java-开发手册中推荐的-threadlocal) java 开发手册中推荐的 ThreadLocal

看看阿里巴巴 java 开发手册中推荐的 ThreadLocal 的用法:

```java
import java.text.DateFormat;
import java.text.SimpleDateFormat;
 
public class DateUtils {
    public static final ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };
}
```

然后我们再要用到 DateFormat 对象的地方，这样调用：

```java
DateUtils.df.get().format(new Date());
```

------



# Java锁🔒 ☕️

Java一共分为两类锁:

* 一类是由synchornized修饰的锁

* 一类是JUC里提供的锁，核心就是ReentrantLock

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/7f749fc8.png)



## 乐观锁 vs 悲观锁

乐观锁与悲观锁是一种广义上的概念，体现了看待线程同步的不同角度。在Java和数据库中都有此概念对应的实际应用。

先说概念。对于同一个数据的并发操作，**悲观锁认为自己在使用数据的时候一定有别的线程来修改数据，因此在获取数据的时候会先加锁，确保数据不会被别的线程修改。**Java中，**synchronized关键字和Lock**的实现类都是悲观锁。

而**乐观锁认为自己在使用数据时不会有别的线程修改数据，所以不会添加锁，**只是在**更新数据**的时候去判断之前有没有别的线程更新了这个数据。如果这个数据没有被更新，当前线程将自己修改的数据成功写入。如果数据已经被其他线程更新，则根据不同的实现方式执行不同的操作（例如报错或者自动重试）。乐观锁在Java中是通过使用无锁编程来实现，最常采用的是CAS算法，Java原子类中的递增操作就通过CAS自旋实现的。



![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/c8703cd9.png)

根据从上面的概念描述我们可以发现：

- **悲观锁适合写操作多的场景**，先加锁可以保证写操作时数据正确。
- 乐观锁适合读操作多的场景，不加锁的特点能够使其读操作的性能大幅提升。

光说概念有些抽象，我们来看下乐观锁和悲观锁的调用方式示例：

```Java
// ------------------------- 悲观锁的调用方式 -------------------------
// synchronized
public synchronized void testMethod() {
	// 操作同步资源
}
// ReentrantLock
private ReentrantLock lock = new ReentrantLock(); // 需要保证多个线程使用的是同一个锁
public void modifyPublicResources() {
	lock.lock();
	// 操作同步资源
	lock.unlock();
}

// ------------------------- 乐观锁的调用方式 -------------------------
private AtomicInteger atomicInteger = new AtomicInteger();  // 需要保证多个线程使用的是同一个AtomicInteger
atomicInteger.incrementAndGet(); //执行自增1
```

通过调用方式示例，我们可以发现悲观锁基本都是在显式的锁定之后再操作同步资源，而乐观锁则直接去操作同步资源。那么，为何乐观锁能够做到不锁定同步资源也可以正确的实现线程同步呢？我们通过介绍乐观锁的主要实现方式 “CAS” 的技术原理来为大家解惑。

CAS全称 Compare And Swap（比较与交换），是一种无锁算法。在不使用锁（没有线程被阻塞）的情况下实现多线程之间的变量同步。java.util.concurrent包中的原子类就是通过CAS来实现了乐观锁。

CAS算法涉及到三个操作数：

- 需要读写的内存值 V。
- 进行比较的值 A。
- 要写入的新值 B。

当且仅当 V 的值等于 A 时，CAS通过原子方式用新值B来更新V的值（“比较+更新”整体是一个原子操作），否则不会执行任何操作。一般情况下，“更新”是一个不断重试的操作。

之前提到java.util.concurrent包中的原子类，就是通过CAS来实现了乐观锁，那么我们进入原子类AtomicInteger的源码，看一下AtomicInteger的定义：

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/feda866e.png)

根据定义我们可以看出各属性的作用：

- unsafe： 获取并操作内存的数据。
- valueOffset： 存储value在AtomicInteger中的偏移量。
- value： 存储AtomicInteger的int值，该属性需要借助volatile关键字保证其在线程间是可见的。

接下来，我们查看AtomicInteger的自增函数incrementAndGet()的源码时，发现自增函数底层调用的是unsafe.getAndAddInt()。但是由于JDK本身只有Unsafe.class，只通过class文件中的参数名，并不能很好的了解方法的作用，所以我们通过OpenJDK 8 来查看Unsafe的源码：

```Java
// ------------------------- JDK 8 -------------------------
// AtomicInteger 自增方法
public final int incrementAndGet() {
  return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
}

// Unsafe.class
public final int getAndAddInt(Object var1, long var2, int var4) {
  int var5;
  do {
      var5 = this.getIntVolatile(var1, var2);
  } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));
  return var5;
}

// ------------------------- OpenJDK 8 -------------------------
// Unsafe.java
public final int getAndAddInt(Object o, long offset, int delta) {
   int v;
   do {
       v = getIntVolatile(o, offset);
   } while (!compareAndSwapInt(o, offset, v, v + delta));
   return v;
}
```

根据OpenJDK 8的源码我们可以看出，getAndAddInt()循环获取给定对象o中的偏移量处的值v，然后判断内存值是否等于v。如果相等则将内存值设置为 v + delta，否则返回false，继续循环进行重试，直到设置成功才能退出循环，并且将旧值返回。整个“比较+更新”操作封装在compareAndSwapInt()中，在JNI里是借助于一个CPU指令完成的，属于原子操作，可以保证多个线程都能够看到同一个变量的修改值。

后续JDK通过CPU的cmpxchg指令，去比较寄存器中的 A 和 内存中的值 V。如果相等，就把要写入的新值 B 存入内存中。如果不相等，就将内存值 V 赋值给寄存器中的值 A。然后通过Java代码中的while循环再次调用cmpxchg指令进行重试，直到设置成功为止。

CAS虽然很高效，但是它也存在三大问题，这里也简单说一下：

1. ABA问题

   。CAS需要在操作值的时候检查内存值是否发生变化，没有发生变化才会更新内存值。但是如果内存值原来是A，后来变成了B，然后又变成了A，那么CAS进行检查时会发现值没有发生变化，但是实际上是有变化的。ABA问题的解决思路就是在变量前面添加版本号，每次变量更新的时候都把版本号加一，这样变化过程就从“A－B－A”变成了“1A－2B－3A”。

   - JDK从1.5开始提供了AtomicStampedReference类来解决ABA问题，具体操作封装在compareAndSet()中。compareAndSet()首先检查当前引用和当前标志与预期引用和预期标志是否相等，如果都相等，则以原子方式将引用值和标志的值设置为给定的更新值。

2. **循环时间长开销大**。CAS操作如果长时间不成功，会导致其一直自旋，给CPU带来非常大的开销。

3. 只能保证一个共享变量的原子操作

   。对一个共享变量执行操作时，CAS能够保证原子操作，但是对多个共享变量操作时，CAS是无法保证操作的原子性的。

   - Java从1.5开始JDK提供了AtomicReference类来保证引用对象之间的原子性，可以把多个变量放在一个对象里来进行CAS操作。



## Reentrant Lock

ReentrantLock,它实现是一种自旋锁，通过循环调用CAS操作来实现加锁，性能较好的原因是在于**避免进入进程的内核态
的阻塞状态**。
想尽办法避免进入内核态的阻塞状态是我们设计锁的关键。

* synchronized与ReentrantLock的区别

|       \        |                         synchronized                         | ReentrantLock |
| :------------: | :----------------------------------------------------------: | :-----------: |
|    可重入性    |                            可重入                            |  **可重入**   |
|    锁的实现    |               JVM实现，很难操作源码，得到实现                |    JDK实现    |
|      性能      | 在引入轻量级锁（偏向锁，自旋锁）后性能大大提升，建议都可以选择的时候选择synchornized |       -       |
|    功能区别    |              方便简洁，由编译器负责加锁和释放锁              |   手工操作    |
|      粒度      |                      细粒度，可灵活控制                      |               |
| 可否指定公平锁 |                        只能是非公平锁                        |     可以      |
|   可否放弃锁   |                            不可以                            |     可以      |

以下是ReentrantLock的一些常用方法：

- `lock()`: 获取锁，如果锁不可用就阻塞当前线程。
- `lockInterruptibly()`: 获取锁，但如果当前线程被中断，则放弃获取锁并抛出InterruptedException异常。
- `tryLock()`: 尝试获取锁，如果锁不可用则返回false。
- `tryLock(long timeout, TimeUnit unit)`: 尝试在给定时间内获取锁。如果在超时之前未能获得锁，则返回false，否则返回true。
- `unlock()`: 释放锁。
- `getHoldCount()`: **返回当前线程保持此锁定的数量，也就是调用lock()方法的次数。**可以不解锁的情况下继续lock表示是可重入的。
- `isHeldByCurrentThread()`: 判断当前线程是否保持此锁。
- `isLocked()`: 判断锁是否被任意线程持有。



* ReentrantLock独有的功能：

1、 可以指定为公平锁（先等待的线程先获得锁）或非公平锁；

```java
/**
      默认实现了非公平锁
     * Creates an instance of {@code ReentrantLock}.
     * This is equivalent to using {@code ReentrantLock(false)}.
     */
    public ReentrantLock() {
        sync = new NonfairSync();
    }
```

2、提供了一个Condition（条件）类，可以分组唤醒需要唤醒的线程；

3、提供了能够中断等待锁的线程机制，`lock.lockInterruptibly()`，sync不可以被打断。

使用synchronized计数：

```java
public class CountExample {

    //请求总数
    public static int clientTotal=5000;

    //同时并发执行的线程数
    public static int threadTotal=200;

    public static volatile int count=0;

    public static void main(String[] args) throws InterruptedException {
        //创建线程池
        ExecutorService executorService= Executors.newCachedThreadPool();
        //定义信号量，闭锁
        final Semaphore semaphore=new Semaphore(threadTotal);

        final CountDownLatch countDownLatch=new CountDownLatch(clientTotal);
        //模拟并发
        for (int i = 0; i < clientTotal; i++) {
            executorService.execute(()->{
                try {
                    semaphore.acquire();
                    add();
                    semaphore.release();
                }catch (Exception e){
                    e.printStackTrace();
                }
                countDownLatch.countDown();

            });
        }
        //确保线程全部执行结束，阻塞进程，并保证
        countDownLatch.await();
        executorService.shutdown();
        System.out.println("count:{"+count+"}");
    }

    private static synchronized void add(){
        count++;
    }
}
//输出结果：count:{5000}
```

使用ReentrantLock计数：

```java
public class CountExample2 {

    //请求总数
    public static int clientTotal=5000;

    //同时并发执行的线程数
    public static int threadTotal=200;

    public static volatile int count=0;

    private final static ReentrantLock lock=new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        //创建线程池
        ExecutorService executorService= Executors.newCachedThreadPool();
        //定义信号量，闭锁
        final Semaphore semaphore=new Semaphore(threadTotal);

        final CountDownLatch countDownLatch=new CountDownLatch(clientTotal);
        //模拟并发
        for (int i = 0; i < clientTotal; i++) {
            executorService.execute(()->{
                try {
                    semaphore.acquire();
                    add();
                    semaphore.release();
                }catch (Exception e){
                    e.printStackTrace();
                }
                countDownLatch.countDown();

            });
        }
        //确保线程全部执行结束，阻塞进程，并保证
        countDownLatch.await();
        executorService.shutdown();
        System.out.println("count:{"+count+"}");
    }

    private static void add(){
        lock.lock();
        try {
            count++;
        }finally {
            lock.unlock();
        }
    }
}
//count:{5000}
```



### ReentrantReadWriteLock

ReentrantLock是一个排他锁，同一时间只允许一个线程访问，
而ReentrantReadWriteLock允许多个读线程同时访问，但不允许写线程和读线程、写线程和写线程同时访问。相对于排他锁，提高了并发性。

在实际应用中，大部分情况下对共享数据（如缓存）的访问都是读操作远多于写操作，
这时ReentrantReadWriteLock能够提供比排他锁更好的并发性和吞吐量。

读写锁内部维护了两个锁，一个用于读操作，一个用于写操作。
所有 ReadWriteLock实现都必须保证 writeLock操作的内存同步效果也要保持与相关 readLock的联系。
也就是说，**成功获取读锁的线程会看到写入锁之前版本所做的所有更新**。

- 分装

```java
public class LockExample {
    private final Map<String,Data> map=new TreeMap();

    private final static ReentrantReadWriteLock lock=new ReentrantReadWriteLock();

    private final static Lock readLock=lock.readLock();

    private final static Lock writeLock=lock.writeLock();

    public  Data get(String key){
        readLock.lock();
        try {
            return map.get(key);
        }finally {
            readLock.unlock();
        }

    }

    public Set<String> getAllKeys(){
        readLock.lock();
        try {
            return map.keySet();
        }finally {
            readLock.unlock();
        }

    }

    public Data put(String key,Data value){
        writeLock.lock();
        try{
            return map.put(key,value);
        }finally {
            writeLock.unlock();
        }
    }
    
    class Data{
    
    }
}
```

### StampedLock

它控制锁有三种模式：写、读和**乐观读**

状态由版本和模式两个部分组成，锁获取方法是一个数字，作为票据（Stamped）。
它用相应的锁的状态来表示和控制当前的访问，数字0表示没有写锁被授权访问。

StampedLock首先调用tryOptimisticRead方法,此时会获得一个“印戳”。然后读取值并检查票据（Stamped），是否仍然有效(例如其他线程已经获得了一个读锁)。
如果有效,就可以使用这个值。
如果无效,就会获得一个读锁(它会阻塞所有的写锁)

在读锁上分为悲观读和乐观读：

乐观读：如果读的操作很多，写操作很少的情况下，我们可以乐观的认为，读写同时发生的几率很小，因此不悲观的使用完全的读取锁定，
程序可以在查看相关的状态之后，判断有没有写操作的变更，再采取相应的措施，这一小小的改进，可以大大提升执行效率。

```java
public class CountExample3 {

    //请求总数
    public static int clientTotal=5000;

    //同时并发执行的线程数
    public static int threadTotal=200;

    public static volatile int count=0;

    private final static StampedLock lock=new StampedLock();

    public static void main(String[] args) throws InterruptedException {
        //创建线程池
        ExecutorService executorService= Executors.newCachedThreadPool();
        //定义信号量，闭锁
        final Semaphore semaphore=new Semaphore(threadTotal);

        final CountDownLatch countDownLatch=new CountDownLatch(clientTotal);
        //模拟并发
        for (int i = 0; i < clientTotal; i++) {
            executorService.execute(()->{
                try {
                    semaphore.acquire();
                    add();
                    semaphore.release();
                }catch (Exception e){
                    e.printStackTrace();
                }
                countDownLatch.countDown();

            });
        }
        //确保线程全部执行结束，阻塞进程，并保证
        countDownLatch.await();
        executorService.shutdown();
        System.out.println("count:{"+count+"}");
    }

    private static void add(){
        long stamp=lock.writeLock();
        try {
            count++;
        }finally {
            lock.unlock(stamp);
        }
    }
}
//count:{5000}
```





**Abstract Queued Synchronizer**

AQS是一个用来构建锁和同步器的框架，使用AQS能简单且高效地构造出应用广泛的大量的同步器，比如我们提到的ReentrantLock，Semaphore，其他的诸如ReentrantReadWriteLock，SynchronousQueue，FutureTask等等皆是基于AQS的。当然，我们自己也能利用AQS非常轻松容易地构造出符合我们自己需求的同步器。

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/979960-20180428192803668-307881475.png)

AQS锁分为独占锁和共享锁两种：

* 独占锁：锁在一个时间点只能被一个线程占有。
  根据锁的获取机制，又分为“公平锁”和“非公平锁”。
  等待队列中按照FIFO的原则获取锁，等待时间越长的线程越先获取到锁，**这就是公平的获取锁，即公平锁。**
  而非公平锁，线程获取的锁的时候，无视等待队列直接获取锁。ReentrantLock和ReentrantReadWriteLock.Writelock是独占锁。

* 共享锁：同一个时候能够被多个线程获取的锁，能被共享的锁。
  JUC包中ReentrantReadWriteLock.ReadLock，CyclicBarrier，CountDownLatch和Semaphore都是共享锁。

​	

## 公平锁 VS 非公平锁

公平锁是指多个线程按照申请锁的顺序来获取锁，线程直接进入队列中排队，队列中的第一个线程才能获得锁。公平锁的优点是等待锁的线程不会饿死。缺点是整体吞吐效率相对非公平锁要低，等待队列中除第一个线程以外的所有线程都会阻塞，CPU唤醒阻塞线程的开销比非公平锁大。

非公平锁是多个线程加锁时直接尝试获取锁，获取不到才会到等待队列的队尾等待。但如果此时锁刚好可用，那么这个线程可以无需阻塞直接获取到锁，所以非公平锁有可能出现后申请锁的线程先获取锁的场景。**非公平锁的优点是可以减少唤起线程的开销，整体的吞吐效率高，**因为线程有几率不阻塞直接获得锁，CPU不必唤醒所有线程。缺点是处于等待队列中的线程可能会饿死，或者等很久才会获得锁。

直接用语言描述可能有点抽象，这里作者用从别处看到的一个例子来讲述一下公平锁和非公平锁。

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/a23d746a.png)

如上图所示，假设有一口水井，有管理员看守，管理员有一把锁，只有拿到锁的人才能够打水，打完水要把锁还给管理员。每个过来打水的人都要管理员的允许并拿到锁之后才能去打水，如果前面有人正在打水，那么这个想要打水的人就必须排队。管理员会查看下一个要去打水的人是不是队伍里排最前面的人，如果是的话，才会给你锁让你去打水；如果你不是排第一的人，就必须去队尾排队，这就是公平锁。

但是对于非公平锁，管理员对打水的人没有要求。即使等待队伍里有排队等待的人，但如果在上一个人刚打完水把锁还给管理员而且管理员还没有允许等待队伍里下一个人去打水时，刚好来了一个插队的人，这个插队的人是可以直接从管理员那里拿到锁去打水，不需要排队，原本排队等待的人只能继续等待。如下图所示：

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/4499559e.png)

接下来我们通过`ReentrantLock`的源码来讲解公平锁和非公平锁，如果是

```java
private ReentrantLock lock = new ReentrantLock(true);
```

![image-20230523221256104](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230523221256104.png)

就是公平的。



![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/6edea205-20230523220905143.png)

根据代码可知，ReentrantLock里面有一个内部类Sync，Sync继承AQS（AbstractQueuedSynchronizer），添加锁和释放锁的大部分操作实际上都是在Sync中实现的。它有公平锁FairSync和非公平锁NonfairSync两个子类。ReentrantLock默认使用非公平锁，也可以通过构造器来显示的指定使用公平锁。

下面我们来看一下公平锁与非公平锁的加锁方法的源码:

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/bc6fe583.png)

通过上图中的源代码对比，我们可以明显的看出公平锁与非公平锁的lock()方法唯一的区别就在于公平锁在获取同步状态时多了一个限制条件：`hasQueuedPredecessors()`。

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/bd0036bb-20230523220856188.png)

再进入`hasQueuedPredecessors()`，可以看到该方法主要做一件事情：**主要是判断当前线程是否位于同步队列中的第一个。**如果是则返回true，否则返回false。

综上，公平锁就是通过同步队列来实现多个线程按照申请锁的顺序来获取锁，**从而实现公平的特性**。非公平锁加锁时不考虑排队等待问题，直接尝试获取锁，所以存在后申请却先获得锁的情况。

## 可重入锁/递归锁

可重入锁又名递归锁，是指在同一个线程在外层方法获取锁的时候，再进入该线程的内层方法会自动获取锁（前提锁对象得是同一个对象或者class），不会因为之前已经获取过还没释放而阻塞。Java中ReentrantLock和synchronized都是可重入锁，可重入锁的一个优点是可一定程度避免死锁。下面用示例代码来进行分析：

```Java
public class Widget {
    public synchronized void doSomething() {
        System.out.println("方法1执行...");
        doOthers();
    }

    public synchronized void doOthers() {
        System.out.println("方法2执行...");
    }
}
```

在上面的代码中，类中的两个方法都是被内置锁synchronized修饰的，doSomething()方法中调用doOthers()方法。因为内置锁是可重入的，所以同一个线程在调用doOthers()时可以直接获得当前对象的锁，进入doOthers()进行操作。

如果是一个不可重入锁，那么当前线程在调用doOthers()之前需要将执行doSomething()时获取当前对象的锁释放掉，实际上该对象锁已被当前线程所持有，且无法释放。所以此时会出现死锁。

而为什么可重入锁就可以在嵌套调用时可以自动获得锁呢？我们通过图示和源码来分别解析一下。

还是打水的例子，有多个人在排队打水，此时管理员允许锁和同一个人的多个水桶绑定。这个人用多个水桶打水时，第一个水桶和锁绑定并打完水之后，第二个水桶也可以直接和锁绑定并开始打水，所有的水桶都打完水之后打水人才会将锁还给管理员。这个人的所有打水流程都能够成功执行，后续等待的人也能够打到水。这就是可重入锁。

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/58fc5bc9.png)

但如果是非可重入锁的话，此时管理员只允许锁和同一个人的一个水桶绑定。第一个水桶和锁绑定打完水之后并不会释放锁，导致第二个水桶不能和锁绑定也无法打水。当前线程出现死锁，整个等待队列中的所有线程都无法被唤醒。

![img](https://awps-assets.meituan.net/mit-x/blog-images-bundle-2018b/ea597a0c.png)

之前我们说过ReentrantLock和synchronized都是重入锁，那么我们通过重入锁ReentrantLock以及非可重入锁NonReentrantLock的源码来对比分析一下为什么非可重入锁在重复调用同步资源时会出现死锁。

首先ReentrantLock和NonReentrantLock都继承父类AQS，其父类AQS中维护了一个同步状态status来计数重入次数，status初始值为0。

当线程尝试获取锁时，可重入锁先尝试获取并更新status值，如果status == 0表示没有其他线程在执行同步代码，则把status置为1，当前线程开始执行。如果status != 0，则判断当前线程是否是获取到这个锁的线程，如果是的话执行status+1，且当前线程可以再次获取锁。而非可重入锁是直接去获取并尝试更新当前status的值，如果status != 0的话会导致其获取锁失败，当前线程阻塞。

释放锁时，可重入锁同样先获取当前status的值，在当前线程是持有锁的线程的前提下。如果status-1 == 0，则表示当前线程所有重复获取锁的操作都已经执行完毕，然后该线程才会真正释放锁。而非可重入锁则是在确定当前线程是持有锁的线程之后，直接将status置为0，将锁释放。

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/32536e7a.png)



## 独享锁(排他锁) VS 共享锁 -  读写锁

独享锁和共享锁同样是一种概念。我们先介绍一下具体的概念，然后通过ReentrantLock和ReentrantReadWriteLock的源码来介绍独享锁和共享锁。

独享锁也叫排他锁，是指该锁一次只能被一个线程所持有。<mark>如果线程T对数据A加上排它锁后，则其他线程不能再对A加任何类型的锁。</mark>**获得排它锁的线程即能读数据又能修改数据。**JDK中的`synchronized`和JUC中`Lock`的实现类就是`互斥锁`。

共享锁是指该锁可被多个线程所持有。如果线程T对数据A加上共享锁后，**则其他线程只能对A再加共享锁，不能加排它锁。**获得**共享锁的线程只能读数据，不能修改数据。**

独享锁与共享锁也是通过**AQS**来实现的，通过实现不同的方法，来实现独享或者共享。



下图为ReentrantReadWriteLock的部分源码：

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/762a042b.png)

我们看到`ReentrantReadWriteLock`有两把锁：ReadLock和WriteLock，由词知意，一个读锁一个写锁，合称“读写锁”。再进一步观察可以发现**ReadLock和WriteLock是靠内部类Sync实现的锁。**Sync是AQS的一个子类，这种结构在CountDownLatch、ReentrantLock、Semaphore里面也都存在。

在`ReentrantReadWriteLock`里面，读锁和写锁的锁主体都是Sync，但读锁和写锁的加锁方式不一样。读锁是共享锁，写锁是独享锁。读锁的共享锁可保证并发读非常高效，而读写、写读、写写的过程互斥，因为读锁和写锁是分离的。所以ReentrantReadWriteLock的并发性相比一般的互斥锁有了很大提升。

那读锁和写锁的具体加锁方式有什么区别呢？在了解源码之前我们需要回顾一下其他知识。 在最开始提及AQS的时候我们也提到了state字段（int类型，32位），该字段用来描述有多少线程获持有锁。

在独享锁中这个值通常是0或者1（如果是重入锁的话state值就是重入的次数），在共享锁中state就是持有锁的数量。但是在ReentrantReadWriteLock中有读、写两把锁，所以需要在一个整型变量state上分别描述读锁和写锁的数量（或者也可以叫状态）。于是将state变量“按位切割”切分成了两个部分，高16位表示读锁状态（读锁个数），低16位表示写锁状态（写锁个数）。如下图所示：

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/8793e00a.png)

了解了概念之后我们再来看代码，先看写锁的加锁源码：

```Java
protected final boolean tryAcquire(int acquires) {
	Thread current = Thread.currentThread();
	int c = getState(); // 取到当前锁的个数
	int w = exclusiveCount(c); // 取写锁的个数w
	if (c != 0) { // 如果已经有线程持有了锁(c!=0)
    // (Note: if c != 0 and w == 0 then shared count != 0)
		if (w == 0 || current != getExclusiveOwnerThread()) 
      // 如果写线程数（w）为0（换言之存在读锁） 或者持有锁的线程不是当前线程就返回失败
			return false;
		if (w + exclusiveCount(acquires) > MAX_COUNT)    
      // 如果写入锁的数量大于最大数（65535，2的16次方-1）就抛出一个Error。
      throw new Error("Maximum lock count exceeded");
		// Reentrant acquire
    setState(c + acquires);
    return true;
  }
  if (writerShouldBlock() || !compareAndSetState(c, c + acquires)) 
    // 如果当且写线程数为0，并且当前线程需要阻塞那么就返回失败；或者如果通过CAS增加写线程数失败也返回失败。
		return false;
	setExclusiveOwnerThread(current); // 如果c=0，w=0或者c>0，w>0（重入），则设置当前线程或锁的拥有者
	return true;
}
```

- 这段代码首先取到当前锁的个数c，然后再通过c来获取写锁的个数w。因为写锁是低16位，所以取低16位的最大值与当前的c做与运算（ int w = exclusiveCount©; ），高16位和0与运算后是0，剩下的就是低位运算的值，同时也是持有写锁的线程数目。
- 在取到写锁线程的数目后，首先判断是否已经有线程持有了锁。如果已经有线程持有了锁(c!=0)，则查看当前写锁线程的数目，如果写线程数为0（即此时存在读锁）或者持有锁的线程不是当前线程就返回失败（涉及到公平锁和非公平锁的实现）。
- 如果写入锁的数量大于最大数（65535，2的16次方-1）就抛出一个Error。
- 如果当且写线程数为0（那么读线程也应该为0，因为上面已经处理c!=0的情况），并且当前线程需要阻塞那么就返回失败；如果通过CAS增加写线程数失败也返回失败。
- 如果c=0,w=0或者c>0,w>0（重入），则设置当前线程或锁的拥有者，返回成功！

tryAcquire()除了重入条件（当前线程为获取了写锁的线程）之外，增加了一个读锁是否存在的判断。如果存在读锁，则写锁不能被获取，原因在于：必须确保写锁的操作对读锁可见，如果允许读锁在已被获取的情况下对写锁的获取，那么正在运行的其他读线程就无法感知到当前写线程的操作。

因此，只有等待其他读线程都释放了读锁，写锁才能被当前线程获取，而写锁一旦被获取，则其他读写线程的后续访问均被阻塞。写锁的释放与ReentrantLock的释放过程基本类似，每次释放均减少写状态，当写状态为0时表示写锁已被释放，然后等待的读写线程才能够继续访问读写锁，同时前次写线程的修改对后续的读写线程可见。

接着是读锁的代码：

```Java
protected final int tryAcquireShared(int unused) {
    Thread current = Thread.currentThread();
    int c = getState();
    if (exclusiveCount(c) != 0 &&
        getExclusiveOwnerThread() != current)
        return -1;                                   // 如果其他线程已经获取了写锁，则当前线程获取读锁失败，进入等待状态
    int r = sharedCount(c);
    if (!readerShouldBlock() &&
        r < MAX_COUNT &&
        compareAndSetState(c, c + SHARED_UNIT)) {
        if (r == 0) {
            firstReader = current;
            firstReaderHoldCount = 1;
        } else if (firstReader == current) {
            firstReaderHoldCount++;
        } else {
            HoldCounter rh = cachedHoldCounter;
            if (rh == null || rh.tid != getThreadId(current))
                cachedHoldCounter = rh = readHolds.get();
            else if (rh.count == 0)
                readHolds.set(rh);
            rh.count++;
        }
        return 1;
    }
    return fullTryAcquireShared(current);
}
```

可以看到在tryAcquireShared(int unused)方法中，如果其他线程已经获取了写锁，则当前线程获取读锁失败，进入等待状态。如果当前线程获取了写锁或者写锁未被获取，则当前线程（线程安全，依靠CAS保证）增加读状态，成功获取读锁。读锁的每次释放（线程安全的，可能有多个读线程同时释放读锁）均减少读状态，减少的值是“1<<16”。所以读写锁才能实现读读的过程共享，而读写、写读、写写的过程互斥。

此时，我们再回头看一下互斥锁ReentrantLock中公平锁和非公平锁的加锁源码：

![img](https://awps-assets.meituan.net/mit-x/blog-images-bundle-2018b/8b7878ec.png)

我们发现在ReentrantLock虽然有公平锁和非公平锁两种，但是它们添加的都是独享锁。根据源码所示，当某一个线程调用lock方法获取锁时，如果同步资源没有被其他线程锁住，那么当前线程在使用CAS更新state成功后就会成功抢占该资源。而如果公共资源被占用且不是被当前线程占用，那么就会加锁失败。所以可以确定ReentrantLock无论读操作还是写操作，添加的锁都是都是独享锁。

### 读写锁在公平和非公平下的策略

 如果是公平锁，我们就在ReentrantReadWriteLock构造函数的参数中传入 true，如果是非公平锁，就在构造函数的参数中传入 false，默认是非公平锁。在获取读锁之前，线程会检查 readerShouldBlock() 方法，同样，在获取写锁之前，线程会检查 writerShouldBlock() 方法，来决定是否需要插队或者是去排队。

首先看公平锁对于这两个方法的实现：

```java
final boolean writerShouldBlock() {
    return hasQueuedPredecessors();
}
final boolean readerShouldBlock() {
    return hasQueuedPredecessors();
}
```


​    很明显，在公平锁的情况下，只要等待队列中有线程在等待，也就是 hasQueuedPredecessors() 返回 true 的时候，那么 writer 和 reader 都会 block，也就是一律不允许插队，都乖乖去排队，这也符合公平锁的思想。

下面让我们来看一下**非公平锁的实现**：

```java
final boolean writerShouldBlock() {
    return false; // writers can always barge
}
final boolean readerShouldBlock() {
    return apparentlyFirstQueuedIsExclusive();
}
```


​    在` writerShouldBlock() `这个方法中始终返回 `false`，可以看出，对于想获取写锁的线程而言，由于返回值是 false，所**以它是随时可以插队的**，这就和我们的` ReentrantLock` 的设计思想是一样的，但是读锁却不一样。这里实现的策略很有意思，先让我们来看下面这种场景：

> 假设线程 2 和线程 4 正在同时读取，线程 3 想要写入，但是由于线程 2 和线程 4 已经持有读锁了，所以线程 3 就进入等待队列进行等待。此时，线程 5 突然跑过来想要插队获取**读锁**：
>
> ![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/v2-5ec7bfbcada50f623c6124d4f992a751_r.jpg)


面对这种情况有两种应对策略：

- 第一种策略：允许插队

由于现在有线程在读，而线程 5 又不会特别增加它们读的负担，因为线程们可以共用这把锁，所以**第一种策略就是让线程 5 直接加入到线程 2 和线程 4 一起去读取。**

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/v2-6808ab62a31b43539ea961d92f09f0ee_r.jpg)

这种策略看上去增加了效率，但是有一个严重的问题，那就是如果想要读取的线程不停地增加，比如线程 6，那么线程  6 也可以插队，这就会导致读锁长时间内不会被释放，导致线程 3 长时间内拿不到写锁，也就是那个需要拿到写锁的线程会陷入“饥饿”状态，它将在长时间内得不到执行。

- 第二种策略：不允许插队
- ![img](https://pic2.zhimg.com/v2-7d00dd892d3941025ab03f53611bec49_r.jpg)

这种策略认为由于线程 3 已经提前等待了，所以虽然线程 5 如果直接插队成功，可以提高效率，但是我们依然让线程 5 去排队等待， 按照这种策略线程 5 会被放入等待队列中，并且排在线程 3 的后面，让线程 3 优先于线程 5 执行，这样可以避免“饥饿”状态，这对于程序的健壮性是很有好处的，**直到写入的线程 3 运行完毕**，线程 5 才有机会运行，这样谁都不会等待太久的时间。

> 写入线程在非公平的情况下具有优先权。

所以我们可以看出，即便是非公平锁，**只要等待队列的头结点是尝试获取写锁的线程，那么读锁的线程依然是不能插队的，目的是避免“饥饿”。**

策略的选择取决于具体锁的实现，ReentrantReadWriteLock 的实现选择了策略 2 ，是很明智的。



```java
public class ReadLockJumpQueue {
 
    private static final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock
            .readLock();
    private static final ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock
            .writeLock();
 
    private static void read() {
        readLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "得到读锁，正在读取");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
            System.out.println(Thread.currentThread().getName() + "释放读锁");
        }
    }
 
    private static void write() {
        writeLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "得到写锁，正在写入");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
            System.out.println(Thread.currentThread().getName() + "释放写锁");
        }
    }
 
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> read(),"Thread-2").start();
        new Thread(() -> read(),"Thread-4").start();
        new Thread(() -> write(),"Thread-3").start();
        new Thread(() -> read(),"Thread-5").start();
    }
}

```



```sh
Thread-2得到读锁，正在读取
Thread-4得到读锁，正在读取
Thread-2释放读锁
Thread-4释放读锁
Thread-3得到写锁，正在写入
Thread-3释放写锁
Thread-5得到读锁，正在读取
Thread-5释放读锁
```

从这个结果可以看出，ReentrantReadWriteLock 的实现选择了**“不允许插队”**的策略，这就大大减小了发生“饥饿”的概率。（如果运行结果和课程不一致，可以在每个线程启动后增加 100ms 的睡眠时间，以便保证线程的运行顺序）。

### 升降级策略

锁降级的意思就是**写锁降级为读锁**。而读锁是不可以升级为写锁的。如果当前线程拥有写锁，然后将其释放，最后再获取读锁，这种分段完成的过程不能称之为锁降级。锁降级是指把持住（当前拥有的）写锁，再获取到读锁，随后释放（先前拥有的）写锁的过程，最后释放读锁的过程。

```java
public class ReadWriteLockDemo2 {

    public static void main(String[] args) {
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        // 获取读锁
        ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock.writeLock();
        // 获取写锁
        ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock.readLock();
        
        //1、获取到写锁
        writeLock.lock();
        System.out.println("获取到了写锁");
        
        //2、获取到读锁
        readLock.lock();
        System.out.println("继续获取到读锁");
        //3、释放写锁
        writeLock.unlock();
	   //4、 释放读锁
        readLock.unlock();
    }
}

```

因为在线程持有读锁的情况下，该线程不能取得写锁（因为获取写锁的前提条件是，当前没有读者线程，也没有其他写者线程，如果发现当前的读锁被占用，就马上获取失败，不管读锁是不是被当前线程持有)。

但是在线程持有写锁的情况下，该线程可以继续获取读锁（获取读锁时如果发现写锁被占用，只有写锁没有被当前线程占用的情况才会获取失败）。

当线程获取读锁的时候，可能有其他线程同时也在持有读锁，因此不能把获取读锁的线程“升级”为写锁；而对于获得写锁的线程，它一定独占了读写锁，因此可以继续让它获取读锁，当它同时获取了写锁和读锁后，还可以先释放写锁继续持有读锁，这样一个写锁就“降级”为了读锁。



**为什么不支持锁的升级？**

我们知道读写锁的特点是如果线程都申请**读锁，是可以多个线程同时持有的**，可是如果是**写锁，只能有一个线程持有**，并且不可能存在读锁和写锁同时持有的情况。

正是因为不可能有读锁和写锁同时持有的情况，所以升级写锁的过程中，需要等到所有的读锁都释放，此时才能进行升级。

假设有 A，B 和 C 三个线程，它们都已持有读锁。假设线程 A 尝试从读锁升级到写锁。那么它必须等待 B 和 C 释放掉已经获取到的读锁。如果随着时间推移，B 和 C 逐渐释放了它们的读锁，此时线程 A 确实是可以成功升级并获取写锁。

但是我们考虑一种特殊情况。假设线程 A 和 B 都想升级到写锁，那么对于线程 A 而言，它需要等待其他所有线程，包括线程 B 在内释放读锁。而线程 B 也需要等待所有的线程，包括线程 A 释放读锁。这就是一种非常典型的死锁的情况。谁都愿不愿意率先释放掉自己手中的锁。

但是读写锁的升级并不是不可能的，也有可以实现的方案，如果我们保证每次只有一个线程可以升级，那么就可以保证线程安全。只不过最常见的 ReentrantReadWriteLock 对此并不支持。



## 自旋锁 VS 适应性自旋锁

在介绍自旋锁前，我们需要介绍一些前提知识来帮助大家明白自旋锁的概念。

在JDK中的`java.util.concurrent.atomic`包中的类以CAS操作为基石构建出原子操作的封装类的功能，**Atomic的类基本都是自旋锁。**

阻塞或唤醒一个Java线程需要操作系统切换CPU状态来完成，这种状态转换需要耗费处理器时间。如果同步代码块中的内容过于简单，状态转换消耗的时间有可能比用户代码执行的时间还要长。

在许多场景中，同步资源的锁定时间很短，为了这一小段时间去切换线程，线程挂起和恢复现场的花费可能会让系统得不偿失。如果物理机器有多个处理器，能够让两个或以上的线程同时并行执行，我们就可以让后面那个请求锁的线程不放弃CPU的执行时间，看看持有锁的线程是否很快就会释放锁。

而为了让当前线程“稍等一下”，我们需让当前线程进行自旋，如果在自旋完成后前面锁定同步资源的线程已经释放了锁，那么当前线程就可以不必阻塞而是直接获取同步资源，从而避免切换线程的开销。这就是自旋锁。

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/452a3363.png)

自旋锁本身是有缺点的，它不能代替阻塞。自旋等待虽然避免了线程切换的开销，但它要占用处理器时间。如果锁被占用的时间很短，自旋等待的效果就会非常好。反之，如果锁被占用的时间很长，那么自旋的线程只会白浪费处理器资源。所以，自旋等待的时间必须要有一定的限度，如果自旋超过了限定次数（默认是10次，可以使用-XX:PreBlockSpin来更改）没有成功获得锁，就应当挂起线程。

自旋锁的实现原理同样也是CAS，`AtomicInteger`中调用unsafe进行自增操作的源码中的`do-while`循环就是一个**自旋操作，如果修改数值失败则通过循环来执行自旋，直至修改成功**。

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/83b3f85e.png)

自旋锁在JDK1.4.2中引入，使用-XX:+UseSpinning来开启。JDK 6中变为默认开启，并且引入了自适应的自旋锁（适应性自旋锁）。

### 自适应锁🔒

自适应意味着自旋的时间（次数）不再固定，而是由前一次在同一个锁上的自旋时间及锁的拥有者的状态来决定。如果在同一个锁对象上，自旋等待刚刚成功获得过锁，并且持有锁的线程正在运行中，那么虚拟机就会认为这次自旋也是很有可能再次成功，进而它将允许自旋等待持续相对更长的时间。如果对于某个锁，自旋很少成功获得过，那在以后尝试获取这个锁时将可能省略掉自旋过程，直接阻塞线程，避免浪费处理器资源。

在自旋锁中 另有三种常见的锁形式:TicketLock、CLHlock和MCSlock，本文中仅做名词介绍，不做深入讲解，感兴趣的同学可以自行查阅相关资料。





# Java 原子类🌍

## 原子变量类简介

### 为何需要原子变量类

保证线程安全是 Java 并发编程必须要解决的重要问题。Java 从原子性、可见性、有序性这三大特性入手，确保多线程的数据一致性。

- 确保线程安全最常见的做法是利用锁机制（`Lock`、`sychronized`）来对共享数据做互斥同步，这样在同一个时刻，只有一个线程可以执行某个方法或者某个代码块，那么操作必然是原子性的，线程安全的。互斥同步最主要的问题是线程阻塞和唤醒所带来的性能问题。
- `volatile` 是轻量级的锁（自然比普通锁性能要好），它保证了共享变量在多线程中的可见性，但无法保证原子性。所以，它只能在一些特定场景下使用。
- 为了兼顾原子性以及锁带来的性能问题，Java 引入了 CAS （主要体现在 `Unsafe` 类）来实现非阻塞同步（也叫乐观锁）。并基于 CAS ，提供了一套原子工具类。

### 原子变量类的作用

原子变量类 **比锁的粒度更细，更轻量级**，并且对于在多处理器系统上实现高性能的并发代码来说是非常关键的。原子变量将发生竞争的范围缩小到单个变量上。

原子变量类相当于一种泛化的 `volatile` 变量，能够**支持原子的、有条件的读/改/写操**作。

原子类在内部使用 CAS 指令（基于硬件的支持）来实现同步。这些指令通常比锁更快。

原子变量类可以分为 4 组：

- 基本类型
  - `AtomicBoolean` - 布尔类型原子类
  - `AtomicInteger` - 整型原子类
  - `AtomicLong` - 长整型原子类
- 引用类型
  - `AtomicReference` - 引用类型原子类
  - `AtomicMarkableReference` - 带有标记位的引用类型原子类
  - `AtomicStampedReference` - 带有版本号的引用类型原子类
- 数组类型
  - `AtomicIntegerArray` - 整形数组原子类
  - `AtomicLongArray` - 长整型数组原子类
  - `AtomicReferenceArray` - 引用类型数组原子类
- 属性更新器类型
  - `AtomicIntegerFieldUpdater` - 整型字段的原子更新器。
  - `AtomicLongFieldUpdater` - 长整型字段的原子更新器。
  - `AtomicReferenceFieldUpdater` - 原子更新引用类型里的字段。



## 基本类型

这一类型的原子类是针对 Java 基本类型进行操作。

- `AtomicBoolean` - 布尔类型原子类
- `AtomicInteger` - 整型原子类
- `AtomicLong` - 长整型原子类

以上类都支持 CAS（[compare-and-swap](https://en.wikipedia.org/wiki/Compare-and-swap)）技术，此外，`AtomicInteger`、`AtomicLong` 还支持算术运算。

> :bulb: 提示：:building_construction:
>
> 虽然 Java 只提供了 `AtomicBoolean` 、`AtomicInteger`、`AtomicLong`，但是可以模拟其他基本类型的原子变量。要想模拟其他基本类型的原子变量，可以将 `short` 或 `byte` 等类型与 `int` 类型进行转换，以及使用 `Float.floatToIntBits` 、`Double.doubleToLongBits` 来转换浮点数。
>
> 由于 `AtomicBoolean`、`AtomicInteger`、`AtomicLong` 实现方式、使用方式都相近，所以本文仅针对 `AtomicInteger` 进行介绍。

### **`AtomicInteger` 用法**

```java
public final int get() // 获取当前值
public final int getAndSet(int newValue) // 获取当前值，并设置新值
public final int getAndIncrement()// 获取当前值，并自增
public final int getAndDecrement() // 获取当前值，并自减
public final int getAndAdd(int delta) // 获取当前值，并加上预期值
boolean compareAndSet(int expect, int update) // 如果当前的值等于预期的数值expect，那将该值设置为输入值update
public final void lazySet(int newValue) // 最终设置为 newValue，使用 lazySet 设置之后可能导致其他线程在之后的一小段时间内还是可以读到旧的值。
```

`AtomicInteger` 使用示例：

```java
public class AtomicIntegerDemo {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        AtomicInteger count = new AtomicInteger(0);
        for (int i = 0; i < 1000; i++) {
            executorService.submit((Runnable) () -> {
                System.out.println(Thread.currentThread().getName() + " count=" + count.get());
                count.incrementAndGet();
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);
        System.out.println("Final Count is : " + count.get());
    }
}
```

### **`AtomicInteger` 实现**

阅读 `AtomicInteger` 源码，可以看到如下定义：

```java
private static final Unsafe unsafe = Unsafe.getUnsafe();
private static final long valueOffset;

static {
	try {
		valueOffset = unsafe.objectFieldOffset
			(AtomicInteger.class.getDeclaredField("value"));
	} catch (Exception ex) { throw new Error(ex); }
}

private volatile int value;
```

> 说明：
>
> - `value` - value 属性使用 `volatile` 修饰，使得对 value 的修改在并发环境下对所有线程可见。
> - `valueOffset` - value 属性的偏移量，通过这个偏移量可以快速定位到 value 字段，这个是实现 AtomicInteger 的关键。
> - `unsafe` - Unsafe 类型的属性，它为 `AtomicInteger` 提供了 CAS 操作。

## 引用类型

Java 数据类型分为 **基本数据类型** 和 **引用数据类型** 两大类（不了解 Java 数据类型划分可以参考： [Java 基本数据类型](https://dunwu.github.io/blog/pages/55d693/) ）。

上一节中提到了针对基本数据类型的原子类，那么如果想针对引用类型做原子操作怎么办？Java 也提供了相关的原子类：

- `AtomicReference` - 引用类型原子类
- `AtomicMarkableReference` - 带有标记位的引用类型原子类
- `AtomicStampedReference` - 带有版本号的引用类型原子类

> `AtomicStampedReference` 类在引用类型原子类中，彻底地解决了 ABA 问题，其它的 CAS 能力与另外两个类相近，所以最具代表性。因此，本节只针对 `AtomicStampedReference` 进行说明。

示例：基于 `AtomicReference` 实现一个简单的自旋锁

```java
public class AtomicReferenceDemo2 {

    private static int ticket = 10;

    public static void main(String[] args) {
        threadSafeDemo();
    }

    private static void threadSafeDemo() {
        SpinLock lock = new SpinLock();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 5; i++) {
            executorService.execute(new MyThread(lock));
        }
        executorService.shutdown();
    }

    /**
     * 基于 {@link AtomicReference} 实现的简单自旋锁
     */
    static class SpinLock {

        private AtomicReference<Thread> atomicReference = new AtomicReference<>();

        public void lock() {
            Thread current = Thread.currentThread();
            while (!atomicReference.compareAndSet(null, current)) {}
        }

        public void unlock() {
            Thread current = Thread.currentThread();
            atomicReference.compareAndSet(current, null);
        }

    }

    /**
     * 利用自旋锁 {@link SpinLock} 并发处理数据
     */
    static class MyThread implements Runnable {

        private SpinLock lock;

        public MyThread(SpinLock lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            while (ticket > 0) {
                lock.lock();
                if (ticket > 0) {
                    System.out.println(Thread.currentThread().getName() + " 卖出了第 " + ticket + " 张票");
                    ticket--;
                }
                lock.unlock();
            }
        }

    }

}
```

原子类的实现基于 CAS 机制，而 CAS 存在 ABA 问题（不了解 ABA 问题，可以参考：[Java 并发基础机制 - CAS 的问题](https://dunwu.github.io/blog/pages/2c6488/#cas-%E7%9A%84%E9%97%AE%E9%A2%98)）。正是为了解决 ABA 问题，才有了 `AtomicMarkableReference` 和 `AtomicStampedReference`。

`AtomicMarkableReference` 使用一个布尔值作为标记，修改时在 true / false 之间切换。这种策略不能根本上解决 ABA 问题，但是可以降低 ABA 发生的几率。常用于缓存或者状态描述这样的场景。

```java
public class AtomicMarkableReferenceDemo {

    private final static String INIT_TEXT = "abc";

    public static void main(String[] args) throws InterruptedException {

        final AtomicMarkableReference<String> amr = new AtomicMarkableReference<>(INIT_TEXT, false);

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(Math.abs((int) (Math.random() * 100)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    String name = Thread.currentThread().getName();
                    if (amr.compareAndSet(INIT_TEXT, name, amr.isMarked(), !amr.isMarked())) {
                        System.out.println(Thread.currentThread().getName() + " 修改了对象！");
                        System.out.println("新的对象为：" + amr.getReference());
                    }
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(3, TimeUnit.SECONDS);
    }

}
```

**`AtomicStampedReference` 使用一个整型值做为版本号，每次更新前先比较版本号，如果一致，才进行修改**。通过这种策略，可以根本上解决 ABA 问题。

```java
public class AtomicStampedReferenceDemo {

    private final static String INIT_REF = "pool-1-thread-3";

    private final static AtomicStampedReference<String> asr = new AtomicStampedReference<>(INIT_REF, 0);

    public static void main(String[] args) throws InterruptedException {

        System.out.println("初始对象为：" + asr.getReference());

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            executorService.execute(new MyThread());
        }

        executorService.shutdown();
        executorService.awaitTermination(3, TimeUnit.SECONDS);
    }

    static class MyThread implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(Math.abs((int) (Math.random() * 100)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            final int stamp = asr.getStamp();
            if (asr.compareAndSet(INIT_REF, Thread.currentThread().getName(), stamp, stamp + 1)) {
                System.out.println(Thread.currentThread().getName() + " 修改了对象！");
                System.out.println("新的对象为：" + asr.getReference());
            }
        }

    }

}
```

## 数组类型

Java 提供了以下针对数组的原子类：

- `AtomicIntegerArray` - 整形数组原子类
- `AtomicLongArray` - 长整型数组原子类
- `AtomicReferenceArray` - 引用类型数组原子类

已经有了针对基本类型和引用类型的原子类，为什么还要提供针对数组的原子类呢？

数组类型的原子类为 **数组元素** 提供了 `volatile` 类型的访问语义，这是普通数组所不具备的特性——**`volatile` 类型的数组仅在数组引用上具有 `volatile` 语义**。

示例：`AtomicIntegerArray` 使用示例（`AtomicLongArray` 、`AtomicReferenceArray` 使用方式也类似）

```java
public class AtomicIntegerArrayDemo {

    private static AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(10);

    public static void main(final String[] arguments) throws InterruptedException {

        System.out.println("Init Values: ");
        for (int i = 0; i < atomicIntegerArray.length(); i++) {
            atomicIntegerArray.set(i, i);
            System.out.print(atomicIntegerArray.get(i) + " ");
        }
        System.out.println();

        Thread t1 = new Thread(new Increment());
        Thread t2 = new Thread(new Compare());
        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Final Values: ");
        for (int i = 0; i < atomicIntegerArray.length(); i++) {
            System.out.print(atomicIntegerArray.get(i) + " ");
        }
        System.out.println();
    }

    static class Increment implements Runnable {

        @Override
        public void run() {

            for (int i = 0; i < atomicIntegerArray.length(); i++) {
                int value = atomicIntegerArray.incrementAndGet(i);
                System.out.println(Thread.currentThread().getName() + ", index = " + i + ", value = " + value);
            }
        }

    }

    static class Compare implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < atomicIntegerArray.length(); i++) {
                boolean swapped = atomicIntegerArray.compareAndSet(i, 2, 3);
                if (swapped) {
                    System.out.println(Thread.currentThread().getName() + " swapped, index = " + i + ", value = 3");
                }
            }
        }

    }

}
```

## 属性更新器类型

更新器类支持基于反射机制的更新字段值的原子操作。

- `AtomicIntegerFieldUpdater` - 整型字段的原子更新器。
- `AtomicLongFieldUpdater` - 长整型字段的原子更新器。
- `AtomicReferenceFieldUpdater` - 原子更新引用类型里的字段。

这些类的使用有一定限制：

- 因为对象的属性修改类型原子类都是抽象类，所以每次使用都必须使用静态方法 `newUpdater()` 创建一个更新器，并且需要设置想要更新的类和属性。
- 字段必须是 `volatile` 类型的；
- 不能作用于静态变量（`static`）；
- 不能作用于常量（`final`）；

```java
public class AtomicReferenceFieldUpdaterDemo {

    static User user = new User("begin");

    static AtomicReferenceFieldUpdater<User, String> updater =
        AtomicReferenceFieldUpdater.newUpdater(User.class, String.class, "name");

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 5; i++) {
            executorService.execute(new MyThread());
        }
        executorService.shutdown();
    }

    static class MyThread implements Runnable {

        @Override
        public void run() {
            if (updater.compareAndSet(user, "begin", "end")) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " 已修改 name = " + user.getName());
            } else {
                System.out.println(Thread.currentThread().getName() + " 已被其他线程修改");
            }
        }

    }

    static class User {

        volatile String name;

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public User setName(String name) {
            this.name = name;
            return this;
        }

    }

}
```

## 原子化的累加器

`DoubleAccumulator`、`DoubleAdder`、`LongAccumulator` 和 `LongAdder`，这四个类仅仅用来执行累加操作，相比原子化的基本数据类型，速度更快，但是不支持 `compareAndSet()` 方法。如果你仅仅需要累加操作，使用原子化的累加器性能会更好，代价就是会消耗更多的内存空间。

`LongAdder` 内部由一个 `base` 变量和一个 `cell[]` 数组组成。

- 当只有一个写线程，没有竞争的情况下，`LongAdder` 会直接使用 `base` 变量作为原子操作变量，通过 CAS 操作修改变量；
- 当有多个写线程竞争的情况下，除了占用 `base` 变量的一个写线程之外，其它各个线程会将修改的变量写入到自己的槽 `cell[]` 数组中。

我们可以发现，`LongAdder` 在操作后的返回值只是一个近似准确的数值，但是 `LongAdder` 最终返回的是一个准确的数值， 所以在一些对实时性要求比较高的场景下，`LongAdder` 并不能取代 `AtomicInteger` 或 `AtomicLong`。



# volatile关键字

`volatile`关键字是Java提供的一种**轻量级**同步机制。它能够保证**可见性**和**有序性**，但是不能保证**原子性**。

## 可见性

[可见性测试](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/thread/VolatileDemo.java)

```java
class MyData{
    int number=0;
    //volatile int number=0;

    AtomicInteger atomicInteger=new AtomicInteger();
    public void setTo60(){
        this.number=60;
    }

    //此时number前面已经加了volatile，但是不保证原子性
    public void addPlusPlus(){
        number++;
    }

    public void addAtomic(){
        atomicInteger.getAndIncrement();
    }
}

//volatile可以保证可见性，及时通知其它线程主物理内存的值已被修改
private static void volatileVisibilityDemo() {
    System.out.println("可见性测试");
    MyData myData=new MyData();//资源类
    //启动一个线程操作共享数据
    new Thread(()->{
        System.out.println(Thread.currentThread().getName()+"\t come in");
        try {TimeUnit.SECONDS.sleep(3);myData.setTo60();
        System.out.println(Thread.currentThread().getName()+"\t update number value: "+myData.number);}catch (InterruptedException e){e.printStackTrace();}
    },"AAA").start();
    while (myData.number==0){
     //main线程持有共享数据的拷贝，一直为0
    }
    System.out.println(Thread.currentThread().getName()+"\t mission is over. main get number value: "+myData.number);
}
```

`MyData`类是资源类，一开始number变量没有用volatile修饰，所以程序运行的结果是：

```java
可见性测试
AAA	 come in
AAA	 update number value: 60
```

虽然一个线程把number修改成了60，但是main线程持有的仍然是最开始的0，所以一直循环，程序不会结束。

如果对number添加了volatile修饰，运行结果是：

```java
AAA	 come in
AAA	 update number value: 60
main	 mission is over. main get number value: 60
```

可见某个线程对number的修改，会立刻反映到主内存上。

## 原子性

volatile并**不能保证操作的原子性**。这是因为，比如一条number++的操作，会形成3条指令。

```assembly
getfield        //读
iconst_1	//++常量1
iadd		//加操作
putfield	//写操作
```

假设有3个线程，分别执行number++，都先从主内存中拿到最开始的值，number=0，然后三个线程分别进行操作。假设线程0执行完毕，number=1，也立刻通知到了其它线程，但是此时线程1、2已经拿到了number=0，所以结果就是写覆盖，线程1、2将number变成1。

解决的方式就是：

1. 对`addPlusPlus()`方法加锁。
2. 使用`java.util.concurrent.AtomicInteger`类。

```java
private static void atomicDemo() {
    System.out.println("原子性测试");
    MyData myData=new MyData();
    for (int i = 1; i <= 20; i++) {
        new Thread(()->{
            for (int j = 0; j <1000 ; j++) {
                myData.addPlusPlus();
                myData.addAtomic();
            }
        },String.valueOf(i)).start();
    }
    while (Thread.activeCount()>2){
        Thread.yield();
    }
    System.out.println(Thread.currentThread().getName()+"\t int type finally number value: "+myData.number);
    System.out.println(Thread.currentThread().getName()+"\t AtomicInteger type finally number value: "+myData.atomicInteger);
}
```

结果：可见，由于`volatile`不能保证原子性，出现了线程重复写的问题，最终结果比20000小。而`AtomicInteger`可以保证原子性。

```java
原子性测试
main	 int type finally number value: 17542
main	 AtomicInteger type finally number value: 20000
```

## 有序性

[有序性案例](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/thread/ResortSeqDemo.java)

volatile可以保证**有序性**，也就是防止**指令重排序**。所谓指令重排序，就是出于优化考虑，CPU执行指令的顺序跟程序员自己编写的顺序不一致。就好比一份试卷，题号是老师规定的，是程序员规定的，但是考生（CPU）可以先做选择，也可以先做填空。

```java
int x = 11; //语句1
int y = 12; //语句2
x = x + 5;  //语句3
y = x * x;  //语句4
```

以上例子，可能出现的执行顺序有1234、2134、1324，这三个都没有问题，最终结果都是x = 16，y=256。但是如果是4开头，就有问题了，y=0。这个时候就**不需要**指令重排序。

volatile底层是用CPU的**内存屏障**（Memory Barrier）指令来实现的，有两个作用，一个是保证特定操作的顺序性，二是保证变量的可见性。在指令之间插入一条Memory Barrier指令，告诉编译器和CPU，在Memory Barrier指令之间的指令不能被重排序。

## 哪些地方用到过volatile？

### 单例模式的安全问题

常见的DCL（Double Check Lock）模式虽然加了同步，但是在多线程下依然会有线程安全问题。

```java
public class SingletonDemo {
    private static SingletonDemo singletonDemo=null;
    private SingletonDemo(){
        System.out.println(Thread.currentThread().getName()+"\t 我是构造方法");
    }
    //DCL模式 Double Check Lock 双端检索机制：在加锁前后都进行判断
    public static SingletonDemo getInstance(){
        if (singletonDemo==null){
            synchronized (SingletonDemo.class){
                 if (singletonDemo==null){
                     singletonDemo=new SingletonDemo();
                 }
            }
        }
        return singletonDemo;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                SingletonDemo.getInstance();
            },String.valueOf(i+1)).start();
        }
    }
}
```

这个漏洞比较tricky，很难捕捉，但是是存在的。`instance=new SingletonDemo();`可以大致分为三步

```java
memory = allocate();     //1.分配内存
instance(memory);	 //2.初始化对象
instance = memory;	 //3.设置引用地址
```

其中2、3没有数据依赖关系，**可能发生重排**。如果发生，此时内存已经分配，那么`instance=memory`不为null。如果此时线程挂起，`instance(memory)`还未执行，对象还未初始化。由于`instance!=null`，所以两次判断都跳过，最后返回的`instance`没有任何内容，还没初始化。

解决的方法就是对`singletondemo`对象添加上`volatile`关键字，禁止指令重排。

# CAS

CAS是指**Compare And Swap**，**比较并交换**，是一种很重要的同步思想。如果主内存的值跟期望值一样，那么就进行修改，否则一直重试，直到一致为止。

```java
public class CASDemo {
    public static void main(String[] args) {
        AtomicInteger atomicInteger=new AtomicInteger(5);
        System.out.println(atomicInteger.compareAndSet(5, 2019)+"\t current data : "+ atomicInteger.get());
        //修改失败
        System.out.println(atomicInteger.compareAndSet(5, 1024)+"\t current data : "+ atomicInteger.get());
    }
}
```

第一次修改，期望值为5，主内存也为5，修改成功，为2019。第二次修改，期望值为5，主内存为2019，修改失败。

查看`AtomicInteger.getAndIncrement()`方法，发现其没有加`synchronized`**也实现了同步**。这是为什么？

## CAS底层原理

`AtomicInteger`内部维护了`volatile int value`和`private  static final Unsafe unsafe`两个比较重要的参数。

```java
public final int getAndIncrement(){
    return unsafe.getAndAddInt(this,valueOffset,1);
}
```

`AtomicInteger.getAndIncrement()`调用了`Unsafe.getAndAddInt()`方法。`Unsafe`类的大部分方法都是`native`的，用来像C语言一样从底层操作内存。

```java
public final int getAnddAddInt(Object var1,long var2,int var4){
    int var5;
    do{
        var5 = this.getIntVolatile(var1, var2);
    } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));
    return var5;
}
```

这个方法的var1和var2，就是根据**对象**和**偏移量**得到在**主内存的快照值**var5。然后`compareAndSwapInt`方法通过var1和var2得到当前**主内存的实际值**。如果这个**实际值**跟**快照值**相等，那么就更新主内存的值为var5+var4。如果不等，那么就一直循环，一直获取快照，一直对比，直到实际值和快照值相等为止。

比如有A、B两个线程，一开始都从主内存中拷贝了原值为3，A线程执行到`var5=this.getIntVolatile`，即var5=3。此时A线程挂起，B修改原值为4，B线程执行完毕，由于加了volatile，所以这个修改是立即可见的。A线程被唤醒，执行`this.compareAndSwapInt()`方法，发现这个时候主内存的值不等于快照值3，所以继续循环，**重新**从主内存获取。

## CAS缺点

CAS实际上是一种自旋锁，

1. 一直循环，开销比较大。
2. 只能保证一个变量的原子操作，多个变量依然要加锁。
3. 引出了**ABA问题**。

# ABA问题

所谓ABA问题，就是比较并交换的循环，存在一个**时间差**，而这个时间差可能带来意想不到的问题。比如线程T1将值从A改为B，然后又从B改为A。线程T2看到的就是A，但是**却不知道这个A发生了更改**。尽管线程T2 CAS操作成功，但不代表就没有问题。
有的需求，比如CAS，**只注重头和尾**，只要首尾一致就接受。但是有的需求，还看重过程，中间不能发生任何修改，这就引出了`AtomicReference`原子引用。

## AtomicReference

`AtomicInteger`对整数进行原子操作，如果是一个POJO呢？可以用`AtomicReference`来包装这个POJO，使其操作原子化。

```java
User user1 = new User("Jack",25);
User user2 = new User("Lucy",21);
AtomicReference<User> atomicReference = new AtomicReference<>();
atomicReference.set(user1);
System.out.println(atomicReference.compareAndSet(user1,user2)); // true
System.out.println(atomicReference.compareAndSet(user1,user2)); //false
```

## AtomicStampedReference和ABA问题的解决

使用`AtomicStampedReference`类可以解决ABA问题。这个类维护了一个“**版本号**”Stamp，在进行CAS操作的时候，不仅要比较当前值，还要比较**版本号**。只有两者都相等，才执行更新操作。

```java
AtomicStampedReference.compareAndSet(expectedReference,newReference,oldStamp,newStamp);
```





# 集合类不安全问题

## List

`ArrayList`不是线程安全类，在多线程同时写的情况下，会抛出`java.util.ConcurrentModificationException`异常。

```java
private static void listNotSafe() {
    List<String> list=new ArrayList<>();
    for (int i = 1; i <= 30; i++) {
        new Thread(() -> {
            list.add(UUID.randomUUID().toString().substring(0, 8));
            System.out.println(Thread.currentThread().getName() + "\t" + list);
        }, String.valueOf(i)).start();
    }
}
```

**解决方法**：

1. 使用`Vector`（`ArrayList`所有方法加`synchronized`，太重）。
2. 使用`Collections.synchronizedList()`转换成线程安全类。
3. 使用`java.concurrent.CopyOnWriteArrayList`（推荐）。

### CopyOnWriteArrayList

这是JUC的类，通过**写时复制**来实现**读写分离**。比如其`add()`方法，就是先**复制**一个新数组，长度为原数组长度+1，然后将新数组最后一个元素设为添加的元素。

```java
public boolean add(E e) {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        //得到旧数组
        Object[] elements = getArray();
        int len = elements.length;
        //复制新数组
        Object[] newElements = Arrays.copyOf(elements, len + 1);
        //设置新元素
        newElements[len] = e;
        //设置新数组
        setArray(newElements);
        return true;
    } finally {
        lock.unlock();
    }
}
```

## Set

跟List类似，`HashSet`和`TreeSet`都不是线程安全的，与之对应的有`CopyOnWriteSet`这个线程安全类。这个类底层维护了一个`CopyOnWriteArrayList`数组。

```java
private final CopyOnWriteArrayList<E> al;
public CopyOnWriteArraySet() {
    al = new CopyOnWriteArrayList<E>();
}
```

### HashSet和HashMap

`HashSet`底层是用`HashMap`实现的。既然是用`HashMap`实现的，那`HashMap.put()`需要传**两个参数**，而`HashSet.add()`只**传一个参数**，这是为什么？实际上`HashSet.add()`就是调用的`HashMap.put()`，只不过**Value**被写死了，是一个`private static final Object`对象。

## Map

`HashMap`不是线程安全的，`Hashtable`是线程安全的，但是跟`Vector`类似，太重量级。所以也有类似CopyOnWriteMap，只不过叫`ConcurrentHashMap`。

关于集合不安全类请看[ContainerNotSafeDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/thread/ContainerNotSafeDemo.java)。

5. 

# CountDownLatch/CyclicBarrier/Semaphore

## CountDownLatch

`CountDownLatch`内部维护了一个**计数器**，只有当**计数器==0**时，某些线程才会停止阻塞，开始执行。

`CountDownLatch`主要有两个方法，`countDown()`来让计数器-1，`await()`来让线程阻塞。当`count==0`时，阻塞线程自动唤醒。

**案例一班长关门**：main线程是班长，6个线程是学生。只有6个线程运行完毕，都离开教室后，main线程班长才会关教室门。

**案例二秦灭六国**：只有6国都被灭亡后（执行完毕），main线程才会显示“秦国一统天下”。

### 枚举类的使用

在**案例二**中会使用到枚举类，因为灭六国，循环6次，想根据`i`的值来确定输出什么国，比如1代表楚国，2代表赵国。如果用判断则十分繁杂，而枚举类可以简化操作。

枚举类就像一个**简化的数据库**，枚举类名就像数据库名，枚举的项目就像数据表，枚举的属性就像表的字段。

关于`CountDownLatch`和枚举类的使用，请看[CountDownLatchDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/thread/CountDownLatchDemo.java)。

## CyclicBarrier

`CountDownLatch`是减，而`CyclicBarrier`是加，理解了`CountDownLatch`，`CyclicBarrier`就很容易。比如召集7颗龙珠才能召唤神龙，详见[CyclicBarrierDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/thread/CyclicBarrierDemo.java)。

## Semaphore

`CountDownLatch`的问题是**不能复用**。比如`count=3`，那么加到3，就不能继续操作了。而`Semaphore`可以解决这个问题，比如6辆车3个停车位，对于`CountDownLatch`**只能停3辆车**，而`Semaphore`可以停6辆车，车位空出来后，其它车可以占有，这就涉及到了`Semaphore.accquire()`和`Semaphore.release()`方法。

```java
Semaphore semaphore=new Semaphore(3);
for (int i = 1; i <=6 ; i++) {
    new Thread(()->{
        try {
            //占有资源
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName()+"\t抢到车位");
            try{ TimeUnit.SECONDS.sleep(3);} catch (Exception e){e.printStackTrace(); }
	    System.out.println(Thread.currentThread().getName()+"\t停车3秒后离开车位");
	    }
	    catch (InterruptedException e) {e.printStackTrace();}
	    //释放资源
	    finally {semaphore.release();}
    },String.valueOf(i)).start();
}
```

# 阻塞队列

**概念**：当阻塞队列为空时，获取（take）操作是阻塞的；当阻塞队列为满时，添加（put）操作是阻塞的。

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/BlockingQueue.png)

**好处**：阻塞队列不用手动控制什么时候该被阻塞，什么时候该被唤醒，简化了操作。

**体系**：`Collection`→`Queue`→`BlockingQueue`→七个阻塞队列实现类。

| 类名                    | 作用                             |
| ----------------------- | -------------------------------- |
| **ArrayBlockingQueue**  | 由**数组**构成的**有界**阻塞队列 |
| **LinkedBlockingQueue** | 由**链表**构成的**有界**阻塞队列 |
| PriorityBlockingQueue   | 支持优先级排序的无界阻塞队列     |
| DelayQueue              | 支持优先级的延迟无界阻塞队列     |
| **SynchronousQueue**    | 单个元素的阻塞队列               |
| LinkedTransferQueue     | 由链表构成的无界阻塞队列         |
| LinkedBlockingDeque     | 由链表构成的双向阻塞队列         |

粗体标记的三个用得比较多，许多消息中间件底层就是用它们实现的。

需要注意的是`LinkedBlockingQueue`虽然是有界的，但有个巨坑，其默认大小是`Integer.MAX_VALUE`，高达21亿，一般情况下内存早爆了（在线程池的`ThreadPoolExecutor`有体现）。

**API**：抛出异常是指当队列满时，再次插入会抛出异常；返回布尔是指当队列满时，再次插入会返回false；阻塞是指当队列满时，再次插入会被阻塞，直到队列取出一个元素，才能插入。超时是指当一个时限过后，才会插入或者取出。API使用见[BlockingQueueDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/thread/BlockingQueueDemo.java)。

| 方法类型 | 抛出异常  | 返回布尔   | 阻塞     | 超时                     |
| -------- | --------- | ---------- | -------- | ------------------------ |
| 插入     | add(E e)  | offer(E e) | put(E e) | offer(E e,Time,TimeUnit) |
| 取出     | remove()  | poll()     | take()   | poll(Time,TimeUnit)      |
| 队首     | element() | peek()     | 无       | 无                       |

## SynchronousQueue

队列只有一个元素，如果想插入多个，必须等队列元素取出后，才能插入，只能有一个“坑位”，用一个插一个，详见[SynchronousQueueDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/thread/SynchronousQueueDemo.java)。

# Callable接口

**与Runnable的区别**：

1. Callable带返回值。
2. 会抛出异常。
3. 覆写`call()`方法，而不是`run()`方法。

**Callable接口的使用**：

```java
public class CallableDemo {
    //实现Callable接口
    class MyThread implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("callable come in ...");
            return 1024;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //创建FutureTask类，接受MyThread。
        FutureTask<Integer> futureTask = new FutureTask<>(new MyThread());
        //将FutureTask对象放到Thread类的构造器里面。
        new Thread(futureTask, "AA").start();
        int result01 = 100;
        //用FutureTask的get方法得到返回值。
        int result02 = futureTask.get();
        System.out.println("result=" + (result01 + result02));
    }
}
```

# 阻塞队列的应用——生产者消费者

## 传统模式

传统模式使用`Lock`来进行操作，需要手动加锁、解锁。详见[ProdConsTradiDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/thread/ProdConsTradiDemo.java)。

```java
public void increment() throws InterruptedException {
    lock.lock();
    try {
        //1 判断 如果number=1，那么就等待，停止生产
        while (number != 0) {
            //等待，不能生产
            condition.await();
    	}
	//2 干活 否则，进行生产
	number++;
        System.out.println(Thread.currentThread().getName() + "\t" + number);
	//3 通知唤醒 然后唤醒消费线程
	condition.signalAll();
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        //最后解锁
        lock.unlock();
    }
}
```

## 阻塞队列模式

使用阻塞队列就不需要手动加锁了，详见[ProdConsBlockQueueDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/thread/ProdConsBlockQueueDemo.java)。

```java
public void myProd() throws Exception {
    String data = null;
    boolean retValue;
    while (FLAG) {
        data = atomicInteger.incrementAndGet() + "";//++i
        retValue = blockingQueue.offer(data, 2L, TimeUnit.SECONDS);
        if (retValue) {
            System.out.println(Thread.currentThread().getName() + "\t" + "插入队列" + data + "成功");
        } else {
            ystem.out.println(Thread.currentThread().getName() + "\t" + "插入队列" + data + "失败");
        }
        TimeUnit.SECONDS.sleep(1);
    }
    System.out.println(Thread.currentThread().getName() + "\tFLAG==false，停止生产");
}
```

# 阻塞队列的应用——

# 死锁编码和定位

主要是两个命令配合起来使用，定位死锁。

**jps**指令：`jps -l`可以查看运行的Java进程。

```java
9688 thread.DeadLockDemo
12177 sun.tools.jps.Jps
```

**jstack**指令：`jstack pid`可以查看某个Java进程的堆栈信息，同时分析出死锁。

```java
=====================
"Thread AAA":
	at xxxxx
	- waiting to lock <0x000111>
	- locked <0x000222>
	at java.lang.Thread.run
"Thread BBB":
	at xxxxx
	- waiting to lock <0x000222>
	- locked <0x000111>
	at java.lang.Thread.run
Found 1 deadlock.
```

