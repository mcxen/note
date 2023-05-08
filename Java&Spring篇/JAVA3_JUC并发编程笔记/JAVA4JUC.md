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

## 带着BAT大厂的面试问题去理解

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

## ThreadLocal代入场景

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

### 一图胜千言

- 搞清楚 Thread，ThreadLocal 以及 ThreadLocalMap 三者之间的关系
- 每个 Thread 对象中都持有一个 ThreadLocalMap 成员变量

![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/v2-8aad71096b1ccb41786942b0579da433_r.jpg)

### Thread:ocean:local方法

**get 方法：**

- get 方法是先取出当前线程的 **ThreadLocalMap**
- 然后调用 map.getEntry 方法，把本 ThreadLocal 的引用作为参数传入
- 取出 map 中属于本 ThreadLocal 的 value
- **注意：**这个 map 以及 map 中的 key 和 value 都是**保存在线程中**的，而不是保存在 ThreadLocal 中

**initialValue 方法：**

- 没有默认实现
- 如果要用 initialValue 方法，需要自己实现
- 通常使用匿名内部类的方式实现

**ThreadLocalMap 类：**

ThreadLocalMap 类，也就是 Thread.threadLocals

- ThreadLocalMap 类是每个线程 Thread 类里面的变量，里面最重要的是一个键值对数组 Entry[] table，可以认为是一个 map，键值对：

- - 键：这个 ThreadLocal
  - 值：实际需要的成员变量，比如 User 或者 SimpleDateFormat对象

- ThreadLocalMap 这里采用的是线性探测法，也就是如果发生冲突，就继续找下一个空位置，而不是用链表拉链

### 两种使用场景殊途同归

- 通过源码分析可以看出，setInitialValue 和直接 set 最后都是利用 **map.set()** 方法来设置值
- 也就是说，最后都会对应到 ThreadLOcalMap 的一个 Entry ，只不过是**起点和入口不一样**

### ThreadLocal 注意点

- 内存泄漏

- - 什么是内存泄漏：某个对象不再有用，但是占用的内存却不能被回收

  - key 的泄漏： ThreadLocalMap 中的 Entry 继承自 WeakReference，是**弱引用**

  - - 弱引用的特点：如果这个对象只被弱引用关联（没有任何强引用关联），那么这个对象就可以被回收

  - Value 的泄漏：

  - - ThreadLocalMap 的每个 Entry 都是一个对 key 的弱引用，同时，每个 Entry 都包含了一个对 value 的强引用

    - 正常情况下，当线程终止，保存在 ThreadLocal 里的 value 会被垃圾回收，因为没有任何强引用了

    - 但是，如果线程不终止（比如线程需要保持很久），那么 key 对应的 value 就不能被回收，因为有以下的调用链：

    - - Thread => ThreadLocalMap => Entry（key 为 null） => Value

    - 因为 value 和 Thread 之间还存在这个强引用链路，所以导致 value 无法回收，就可能会出现 OOM

    - jdk 设计，扫描 key 为 null 的 Entry，并把对应的 value 设置为 null

    - 如果一个 ThreadLocal 不被使用，就可能导致 value 的内存泄漏

- 如何避免内存泄漏（阿里规约）

- - 调用 **remove** 方法，就会删除对应的 Entry 对象，可以避免内存泄漏，所以使用完 ThreadLocal 之后，应该调用 remove 方法

- 空指针异常

```text
public class ThreadLocalNPE {
    ThreadLocal<Long> longThreadLocal = new ThreadLocal<>();

    public void set(){
        longThreadLocal.set(Thread.currentThread().getId());
    }
    public Long get(){
        return longThreadLocal.get();
    }

    public static void main(String[] args) {
        ThreadLocalNPE threadLocalNPE = new ThreadLocalNPE();
        System.out.println(threadLocalNPE.get());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                threadLocalNPE.set();
                System.out.println(threadLocalNPE.get());
            }
        });
        thread.start();

    }
}
```

- 如果 get 方法为 long ，则报空指针异常，有装箱拆箱导致的，不是 ThreadLocal的问题
- 共享对象
- 如果可以不使用 ThreadLocal 就解决问题，那么不要强行使用
- 优先使用框架的支持，而不是自己创造



## ThreadLocal理解

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

### [#](#每个线程维护了一个-序列号) 每个线程维护了一个“序列号”

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

### [#](#session的管理) Session的管理

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





# JMM

JMM是指Java**内存模型**，不是Java**内存布局**，不是所谓的栈、堆、方法区。

每个Java线程都有自己的**工作内存**。操作数据，首先从主内存中读，得到一份拷贝，操作完毕后再写回到主内存。

![](https://raw.githubusercontent.com/MaJesTySA/JVM-JUC-Core/master/imgs/JMM.png)

JMM可能带来**可见性**、**原子性**和**有序性**问题。所谓可见性，就是某个线程对主内存内容的更改，应该立刻通知到其它线程。原子性是指一个操作是不可分割的，不能执行到一半，就不执行了。所谓有序性，就是指令是有序的，不会被重排。

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

详见[ABADemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/thread/ABADemo.java)。

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

# Java锁

## 公平锁/非公平锁

**概念**：所谓**公平锁**，就是多个线程按照**申请锁的顺序**来获取锁，类似排队，先到先得。而**非公平锁**，则是多个线程抢夺锁，会导致**优先级反转**或**饥饿现象**。

**区别**：公平锁在获取锁时先查看此锁维护的**等待队列**，**为空**或者当前线程是等待队列的**队首**，则直接占有锁，否则插入到等待队列，FIFO原则。非公平锁比较粗鲁，上来直接**先尝试占有锁**，失败则采用公平锁方式。非公平锁的优点是**吞吐量**比公平锁更大。

`synchronized`和`juc.ReentrantLock`默认都是**非公平锁**。`ReentrantLock`在构造的时候传入`true`则是**公平锁**。

## 可重入锁/递归锁

可重入锁又叫递归锁，指的同一个线程在**外层方法**获得锁时，进入**内层方法**会自动获取锁。也就是说，线程可以进入任何一个它已经拥有锁的代码块。比如`get`方法里面有`set`方法，两个方法都有同一把锁，得到了`get`的锁，就自动得到了`set`的锁。

就像有了家门的锁，厕所、书房、厨房就为你敞开了一样。可重入锁可以**避免死锁**的问题。

详见[ReentrantLockDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/thread/ReentrantLockDemo.java)。

### 锁的配对

锁之间要配对，加了几把锁，最后就得解开几把锁，下面的代码编译和运行都没有任何问题。但锁的数量不匹配会导致死循环。

```java
lock.lock();
lock.lock();
try{
    someAction();
}finally{
    lock.unlock();
}
```

## 自旋锁

所谓自旋锁，就是尝试获取锁的线程不会**立即阻塞**，而是采用**循环的方式去尝试获取**。自己在那儿一直循环获取，就像“**自旋**”一样。这样的好处是减少**线程切换的上下文开销**，缺点是会**消耗CPU**。CAS底层的`getAndAddInt`就是**自旋锁**思想。

```java
//跟CAS类似，一直循环比较。
while (!atomicReference.compareAndSet(null, thread)) { }
```

详见[SpinLockDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/thread/SpinLockDemo.java)。

## 读写锁/独占/共享锁

**读锁**是**共享的**，**写锁**是**独占的**。`juc.ReentrantLock`和`synchronized`都是**独占锁**，独占锁就是**一个锁**只能被**一个线程**所持有。有的时候，需要**读写分离**，那么就要引入读写锁，即`juc.ReentrantReadWriteLock`。

比如缓存，就需要读写锁来控制。缓存就是一个键值对，以下Demo模拟了缓存的读写操作，读的`get`方法使用了`ReentrantReadWriteLock.ReadLock()`，写的`put`方法使用了`ReentrantReadWriteLock.WriteLock()`。这样避免了写被打断，实现了多个线程同时读。

[ReadWriteLockDemo](https://github.com/MaJesTySA/JVM-JUC-Core/blob/master/src/thread/ReadWriteLockDemo.java)

## Synchronized和Lock的区别

`synchronized`关键字和`java.util.concurrent.locks.Lock`都能加锁，两者有什么区别呢？

1. **原始构成**：`sync`是JVM层面的，底层通过`monitorenter`和`monitorexit`来实现的。`Lock`是JDK API层面的。（`sync`一个enter会有两个exit，一个是正常退出，一个是异常退出）
2. **使用方法**：`sync`不需要手动释放锁，而`Lock`需要手动释放。
3. **是否可中断**：`sync`不可中断，除非抛出异常或者正常运行完成。`Lock`是可中断的，通过调用`interrupt()`方法。
4. **是否为公平锁**：`sync`只能是非公平锁，而`Lock`既能是公平锁，又能是非公平锁。
5. **绑定多个条件**：`sync`不能，只能随机唤醒。而`Lock`可以通过`Condition`来绑定多个条件，精确唤醒。

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

