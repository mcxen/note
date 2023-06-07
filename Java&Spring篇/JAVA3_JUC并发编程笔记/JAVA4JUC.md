# JUC并发编程
---

# Java 线程基础

> **关键词：`Thread`、`Runnable`、`Callable`、`Future`、`wait`、`notify`、`notifyAll`、`join`、`sleep`、`yeild`、`线程状态`、`线程通信`**

## 线程简介

### 什么是进程

简言之，**进程可视为一个正在运行的程序**。它是系统运行程序的基本单位，因此进程是动态的。进程是具有一定独立功能的程序关于某个数据集合上的一次运行活动。进程是操作系统进行资源分配的基本单位。

### 什么是线程

线程是操作系统进行调度的基本单位。线程也叫轻量级进程（Light Weight Process），在一个进程里可以创建多个线程，这些线程都拥有各自的计数器、堆栈和局部变量等属性，并且能够访问共享的内存变量。

### 进程和线程的区别

- 一个程序至少有一个进程，一个进程至少有一个线程。
- 线程比进程划分更细，所以执行开销更小，并发性更高。
- 进程是一个实体，拥有独立的资源；而同一个进程中的多个线程共享进程的资源。

## 创建线程

创建线程有三种方式：

- 继承 `Thread` 类
- 实现 `Runnable` 接口
- 实现 `Callable` 接口

### Thread

通过继承 `Thread` 类创建线程的步骤：

1. 定义 `Thread` 类的子类，并覆写该类的 `run` 方法。`run` 方法的方法体就代表了线程要完成的任务，因此把 `run` 方法称为执行体。
2. 创建 `Thread` 子类的实例，即创建了线程对象。
3. 调用线程对象的 `start` 方法来启动该线程。

```java
public class ThreadDemo {

    public static void main(String[] args) {
        // 实例化对象
        MyThread tA = new MyThread("Thread 线程-A");
        MyThread tB = new MyThread("Thread 线程-B");
        // 调用线程主体
        tA.start();
        tB.start();
    }

    static class MyThread extends Thread {

        private int ticket = 5;

        MyThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (ticket > 0) {
                System.out.println(Thread.currentThread().getName() + " 卖出了第 " + ticket + " 张票");
                ticket--;
            }
        }

    }

}
```

### Runnable

**实现 `Runnable` 接口优于继承 `Thread` 类**，因为：

- Java 不支持多重继承，所有的类都只允许继承一个父类，但可以实现多个接口。如果继承了 `Thread` 类就无法继承其它类，这不利于扩展。
- 类可能只要求可执行就行，继承整个 `Thread` 类开销过大。

通过实现 `Runnable` 接口创建线程的步骤：

1. 定义 `Runnable` 接口的实现类，并覆写该接口的 `run` 方法。该 `run` 方法的方法体同样是该线程的线程执行体。
2. 创建 `Runnable` 实现类的实例，并以此实例作为 `Thread` 的 target 来创建 `Thread` 对象，该 `Thread` 对象才是真正的线程对象。
3. 调用线程对象的 `start` 方法来启动该线程。

```java
public class RunnableDemo {

    public static void main(String[] args) {
        // 实例化对象
        Thread tA = new Thread(new MyThread(), "Runnable 线程-A");
        Thread tB = new Thread(new MyThread(), "Runnable 线程-B");
        // 调用线程主体
        tA.start();
        tB.start();
    }

    static class MyThread implements Runnable {

        private int ticket = 5;

        @Override
        public void run() {
            while (ticket > 0) {
                System.out.println(Thread.currentThread().getName() + " 卖出了第 " + ticket + " 张票");
                ticket--;
            }
        }

    }

}
```

### Callable、Future、FutureTask - 解决缺陷

**继承 Thread 类和实现 Runnable 接口这两种创建线程的方式都没有返回值**。所以，线程执行完后，无法得到执行结果。但如果期望得到执行结果该怎么做？

为了解决这个问题，Java 1.5 后，提供了 `Callable` 接口和 `Future` 接口，通过它们，可以在线程执行结束后，返回执行结果。

**举例：**

```java
public static void main(String[] args) {
    ExecutorService service = Executors.newFixedThreadPool(10);
    Future<Integer> future = service.submit(new CallableTask());
    try {
        System.out.println(future.get());
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    } catch (ExecutionException e) {
        throw new RuntimeException(e);
    }
}
static class CallableTask implements Callable<Integer>{

    @Override
    public Integer call() throws Exception {
        Thread.sleep(3000);
        return new Random().nextInt();
    }
}
```



#### Callable

Callable 接口只声明了一个方法，这个方法叫做 call()：

```java
public interface Callable<V> {
    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    V call() throws Exception;
}
```

那么怎么使用 Callable 呢？一般情况下是配合 ExecutorService 来使用的，在 ExecutorService 接口中声明了若干个 submit 方法的重载版本：

```java
<T> Future<T> submit(Callable<T> task);
<T> Future<T> submit(Runnable task, T result);
Future<?> submit(Runnable task);
```

第一个 submit 方法里面的参数类型就是 Callable。

#### Future

Future 就是对于具体的 Callable 任务的执行结果进行取消、查询是否完成、获取结果。必要时可以通过 get 方法获取执行结果，该方法会阻塞直到任务返回结果。

```java
public interface Future<V> {
    boolean cancel(boolean mayInterruptIfRunning);
    boolean isCancelled();
    boolean isDone();
    V get() throws InterruptedException, ExecutionException;
    V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
```

#### FutureTask

FutureTask 类实现了 RunnableFuture 接口，RunnableFuture 继承了 Runnable 接口和 Future 接口。

所以，FutureTask 既可以作为 Runnable 被线程执行，又可以作为 Future 得到 Callable 的返回值。

```java
public class FutureTask<V> implements RunnableFuture<V> {
    // ...
    public FutureTask(Callable<V> callable) {}
    public FutureTask(Runnable runnable, V result) {}
}

public interface RunnableFuture<V> extends Runnable, Future<V> {
    void run();
}
```

事实上，FutureTask 是 Future 接口的一个唯一实现类。

#### Callable + Future + FutureTask 示例

通过实现 `Callable` 接口创建线程的步骤：

1. 创建 `Callable` 接口的实现类，并实现 `call` 方法。该 `call` 方法将作为线程执行体，并且有返回值。
2. 创建 `Callable` 实现类的实例，使用 `FutureTask` 类来包装 `Callable` 对象，该 `FutureTask` 对象封装了该 `Callable` 对象的 `call` 方法的返回值。
3. 使用 `FutureTask` 对象作为 `Thread` 对象的 target 创建并启动新线程。
4. 调用 `FutureTask` 对象的 `get` 方法来获得线程执行结束后的返回值。

```java
public class CallableDemo {

    public static void main(String[] args) {
        Callable<Long> callable = new MyThread();
        FutureTask<Long> future = new FutureTask<>(callable);
        new Thread(future, "Callable 线程").start();
        try {
            System.out.println("任务耗时：" + (future.get() / 1000000) + "毫秒");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    static class MyThread implements Callable<Long> {

        private int ticket = 10000;

        @Override
        public Long call() {
            long begin = System.nanoTime();
            while (ticket > 0) {
                System.out.println(Thread.currentThread().getName() + " 卖出了第 " + ticket + " 张票");
                ticket--;
            }

            long end = System.nanoTime();
            return (end - begin);
        }

    }

}
```

## 线程基本用法

线程（`Thread`）基本方法清单：

| 方法            | 描述                                                         |
| --------------- | ------------------------------------------------------------ |
| `run`           | 线程的执行实体。                                             |
| `start`         | 线程的启动方法。                                             |
| `currentThread` | 返回对当前正在执行的线程对象的引用。                         |
| `setName`       | 设置线程名称。                                               |
| `getName`       | 获取线程名称。                                               |
| `setPriority`   | 设置线程优先级。Java 中的线程优先级的范围是 [1,10]，一般来说，高优先级的线程在运行时会具有优先权。可以通过 `thread.setPriority(Thread.MAX_PRIORITY)` 的方式设置，默认优先级为 5。 |
| `getPriority`   | 获取线程优先级。                                             |
| `setDaemon`     | 设置线程为守护线程。                                         |
| `isDaemon`      | 判断线程是否为守护线程。                                     |
| `isAlive`       | 判断线程是否启动。                                           |
| `interrupt`     | 中断另一个线程的运行状态。                                   |
| `interrupted`   | 测试当前线程是否已被中断。通过此方法可以清除线程的中断状态。换句话说，如果要连续调用此方法两次，则第二次调用将返回 false（除非当前线程在第一次调用清除其中断状态之后且在第二次调用检查其状态之前再次中断）。 |
| `join`          | 可以使一个线程强制运行，线程强制运行期间，其他线程无法运行，必须等待此线程完成之后才可以继续执行。 |
| `Thread.sleep`  | 静态方法。将当前正在执行的线程休眠。                         |
| `Thread.yield`  | 静态方法。将当前正在执行的线程暂停，让其他线程执行。         |

### 线程休眠

**使用 `Thread.sleep` 方法可以使得当前正在执行的线程进入休眠状态。**

使用 `Thread.sleep` 需要向其传入一个整数值，这个值表示线程将要休眠的毫秒数。

`Thread.sleep` 方法可能会抛出 `InterruptedException`，因为异常不能跨线程传播回 `main` 中，因此必须在本地进行处理。线程中抛出的其它异常也同样需要在本地进行处理。

```java
public class ThreadSleepDemo {

    public static void main(String[] args) {
        new Thread(new MyThread("线程A", 500)).start();
        new Thread(new MyThread("线程B", 1000)).start();
        new Thread(new MyThread("线程C", 1500)).start();
    }

    static class MyThread implements Runnable {

        /** 线程名称 */
        private String name;

        /** 休眠时间 */
        private int time;

        private MyThread(String name, int time) {
            this.name = name;
            this.time = time;
        }

        @Override
        public void run() {
            try {
                // 休眠指定的时间
                Thread.sleep(this.time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(this.name + "休眠" + this.time + "毫秒。");
        }

    }

}
```

### 线程礼让

`Thread.yield` 方法的调用声明了当前线程已经完成了生命周期中最重要的部分，可以切换给其它线程来执行 。

该方法只是对线程调度器的一个建议，而且也只是建议具有相同优先级的其它线程可以运行。

```java
public class ThreadYieldDemo {

    public static void main(String[] args) {
        MyThread t = new MyThread();
        new Thread(t, "线程A").start();
        new Thread(t, "线程B").start();
    }

    static class MyThread implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "运行，i = " + i);
                if (i == 2) {
                    System.out.print("线程礼让：");
                    Thread.yield();
                }
            }
        }
    }
}
```

### 终止线程

> **`Thread` 中的 `stop` 方法有缺陷，已废弃**。
>
> 使用 `Thread.stop` 停止线程会导致它解锁所有已锁定的监视器（由于未经检查的 `ThreadDeath` 异常会在堆栈中传播，这是自然的结果）。 如果先前由这些监视器保护的任何对象处于不一致状态，则损坏的对象将对其他线程可见，从而可能导致任意行为。
>
> stop() 方法会真的杀死线程，不给线程喘息的机会，如果线程持有 ReentrantLock 锁，被 stop() 的线程并不会自动调用 ReentrantLock 的 unlock() 去释放锁，那其他线程就再也没机会获得 ReentrantLock 锁，这实在是太危险了。所以该方法就不建议使用了，类似的方法还有 suspend() 和 resume() 方法，这两个方法同样也都不建议使用了，所以这里也就不多介绍了。`Thread.stop` 的许多用法应由仅修改某些变量以指示目标线程应停止运行的代码代替。 目标线程应定期检查此变量，如果该变量指示要停止运行，则应按有序方式从其运行方法返回。如果目标线程等待很长时间（例如，在条件变量上），则应使用中断方法来中断等待。

当一个线程运行时，另一个线程可以直接通过 `interrupt` 方法中断其运行状态。

```java
public class ThreadInterruptDemo {

    public static void main(String[] args) {
        MyThread mt = new MyThread(); // 实例化Runnable子类对象
        Thread t = new Thread(mt, "线程"); // 实例化Thread对象
        t.start(); // 启动线程
        try {
            Thread.sleep(2000); // 线程休眠2秒
        } catch (InterruptedException e) {
            System.out.println("3、main线程休眠被终止");
        }
        t.interrupt(); // 中断线程执行
    }

    static class MyThread implements Runnable {

        @Override
        public void run() {
            System.out.println("1、进入run()方法");
            try {
                Thread.sleep(10000); // 线程休眠10秒
                System.out.println("2、已经完成了休眠");
            } catch (InterruptedException e) {
                System.out.println("3、MyThread线程休眠被终止");
                return; // 返回调用处
            }
            System.out.println("4、run()方法正常结束");
        }
    }
}
```

如果一个线程的 `run` 方法执行一个无限循环，并且没有执行 `sleep` 等会抛出 `InterruptedException` 的操作，那么调用线程的 `interrupt` 方法就无法使线程提前结束。

但是调用 `interrupt` 方法会设置线程的中断标记，此时调用 `interrupted` 方法会返回 `true`。因此可以在循环体中使用 `interrupted` 方法来判断线程是否处于中断状态，从而提前结束线程。

安全地终止线程有两种方法：

- 定义 `volatile` 标志位，在 `run` 方法中使用标志位控制线程终止
- 使用 `interrupt` 方法和 `Thread.interrupted` 方法配合使用来控制线程终止

【示例】使用 `volatile` 标志位控制线程终止

```java
public class ThreadStopDemo2 {

    public static void main(String[] args) throws Exception {
        MyTask task = new MyTask();
        Thread thread = new Thread(task, "MyTask");
        thread.start();
        TimeUnit.MILLISECONDS.sleep(50);
        task.cancel();
    }

    private static class MyTask implements Runnable {

        private volatile boolean flag = true;

        private volatile long count = 0L;

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " 线程启动");
            while (flag) {
                System.out.println(count++);
            }
            System.out.println(Thread.currentThread().getName() + " 线程终止");
        }

        /**
         * 通过 volatile 标志位来控制线程终止
         */
        public void cancel() {
            flag = false;
        }

    }

}
```

【示例】使用 `interrupt` 方法和 `Thread.interrupted` 方法配合使用来控制线程终止

```java
public class ThreadStopDemo3 {

    public static void main(String[] args) throws Exception {
        MyTask task = new MyTask();
        Thread thread = new Thread(task, "MyTask");
        thread.start();
        TimeUnit.MILLISECONDS.sleep(50);
        thread.interrupt();
    }

    private static class MyTask implements Runnable {

        private volatile long count = 0L;

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " 线程启动");
            // 通过 Thread.interrupted 和 interrupt 配合来控制线程终止
            while (!Thread.interrupted()) {
                System.out.println(count++);
            }
            System.out.println(Thread.currentThread().getName() + " 线程终止");
        }
    }
}
```

### 守护线程

什么是守护线程？

- **守护线程（Daemon Thread）是在后台执行并且不会阻止 JVM 终止的线程**。**当所有非守护线程结束时，程序也就终止，同时会杀死所有守护线程**。
- 与守护线程（Daemon Thread）相反的，叫用户线程（User Thread），也就是非守护线程。

为什么需要守护线程？

- 守护线程的优先级比较低，用于为系统中的其它对象和线程提供服务。典型的应用就是垃圾回收器。

如何使用守护线程？

- 可以使用 `isDaemon` 方法判断线程是否为守护线程。
- 可以使用 `setDaemon` 方法设置线程为守护线程。
  - 正在运行的用户线程无法设置为守护线程，所以 `setDaemon` 必须在 `thread.start` 方法之前设置，否则会抛出 `llegalThreadStateException` 异常；
  - 一个守护线程创建的子线程依然是守护线程。
  - 不要认为所有的应用都可以分配给守护线程来进行服务，比如读写操作或者计算逻辑。

```java
public class ThreadDaemonDemo {

    public static void main(String[] args) {
        Thread t = new Thread(new MyThread(), "线程");
        t.setDaemon(true); // 此线程在后台运行
        System.out.println("线程 t 是否是守护进程：" + t.isDaemon());
        t.start(); // 启动线程
    }

    static class MyThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                System.out.println(Thread.currentThread().getName() + "在运行。");
            }
        }
    }
}
```

> 参考阅读：[Java 中守护线程的总结](https://blog.csdn.net/shimiso/article/details/8964414)

## 线程通信

> 当多个线程可以一起工作去解决某个问题时，如果某些部分必须在其它部分之前完成，那么就需要对线程进行协调。

### wait/notify/notifyAll

- `wait` - `wait` 会自动释放当前线程占有的对象锁，并请求操作系统挂起当前线程，**让线程从 `Running` 状态转入 `Waiting` 状态**，等待 `notify` / `notifyAll` 来唤醒。如果没有释放锁，那么其它线程就无法进入对象的同步方法或者同步控制块中，那么就无法执行 `notify` 或者 `notifyAll` 来唤醒挂起的线程，造成死锁。
- `notify` - 唤醒一个正在 `Waiting` 状态的线程，并让它拿到对象锁，具体唤醒哪一个线程由 JVM 控制 。
- `notifyAll` - 唤醒所有正在 `Waiting` 状态的线程，接下来它们需要竞争对象锁。

> 注意：
>
> - **`wait`、`notify`、`notifyAll` 都是 `Object` 类中的方法**，而非 `Thread`。
> - **`wait`、`notify`、`notifyAll` 只能用在 `synchronized` 方法或者 `synchronized` 代码块中使用，否则会在运行时抛出 `IllegalMonitorStateException`**。
>
> 为什么 `wait`、`notify`、`notifyAll` 不定义在 `Thread` 中？为什么 `wait`、`notify`、`notifyAll` 要配合 `synchronized` 使用？
>
> 首先，需要了解几个基本知识点：
>
> - 每一个 Java 对象都有一个与之对应的 **监视器（monitor）**
> - 每一个监视器里面都有一个 **对象锁** 、一个 **等待队列**、一个 **同步队列**
>
> 了解了以上概念，我们回过头来理解前面两个问题。
>
> 为什么这几个方法不定义在 `Thread` 中？
>
> 由于每个对象都拥有对象锁，让当前线程等待某个对象锁，自然应该基于这个对象（`Object`）来操作，而非使用当前线程（`Thread`）来操作。因为当前线程可能会等待多个线程的锁，如果基于线程（`Thread`）来操作，就非常复杂了。
>
> 为什么 `wait`、`notify`、`notifyAll` 要配合 `synchronized` 使用？
>
> 如果调用某个对象的 `wait` 方法，当前线程必须拥有这个对象的对象锁，因此调用 `wait` 方法必须在 `synchronized` 方法和 `synchronized` 代码块中。

生产者、消费者模式是 `wait`、`notify`、`notifyAll` 的一个经典使用案例：

```java
public class ThreadWaitNotifyDemo02 {

    private static final int QUEUE_SIZE = 10;
    private static final PriorityQueue<Integer> queue = new PriorityQueue<>(QUEUE_SIZE);

    public static void main(String[] args) {
        new Producer("生产者A").start();
        new Producer("生产者B").start();
        new Consumer("消费者A").start();
        new Consumer("消费者B").start();
    }

    static class Consumer extends Thread {

        Consumer(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                synchronized (queue) {
                    while (queue.size() == 0) {
                        try {
                            System.out.println("队列空，等待数据");
                            queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            queue.notifyAll();
                        }
                    }
                    queue.poll(); // 每次移走队首元素
                    queue.notifyAll();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + " 从队列取走一个元素，队列当前有：" + queue.size() + "个元素");
                }
            }
        }
    }

    static class Producer extends Thread {

        Producer(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                synchronized (queue) {
                    while (queue.size() == QUEUE_SIZE) {
                        try {
                            System.out.println("队列满，等待有空余空间");
                            queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            queue.notifyAll();
                        }
                    }
                    queue.offer(1); // 每次插入一个元素
                    queue.notifyAll();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + " 向队列取中插入一个元素，队列当前有：" + queue.size() + "个元素");
                }
            }
        }
    }
}
```

### join

在线程操作中，可以使用 `join` 方法让一个线程强制运行，线程强制运行期间，其他线程无法运行，必须等待此线程完成之后才可以继续执行。

```java
public class ThreadJoinDemo {

    public static void main(String[] args) {
        MyThread mt = new MyThread(); // 实例化Runnable子类对象
        Thread t = new Thread(mt, "mythread"); // 实例化Thread对象
        t.start(); // 启动线程
        for (int i = 0; i < 50; i++) {
            if (i > 10) {
                try {
                    t.join(); // 线程强制运行
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Main 线程运行 --> " + i);
        }
    }

    static class MyThread implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 50; i++) {
                System.out.println(Thread.currentThread().getName() + " 运行，i = " + i); // 取得当前线程的名字
            }
        }
    }
}
```

### 管道

管道输入/输出流和普通的文件输入/输出流或者网络输入/输出流不同之处在于，它主要用于线程之间的数据传输，而传输的媒介为内存。
管道输入/输出流主要包括了如下 4 种具体实现：`PipedOutputStream`、`PipedInputStream`、`PipedReader` 和 `PipedWriter`，前两种面向字节，而后两种面向字符。

```java
public class Piped {

    public static void main(String[] args) throws Exception {
        PipedWriter out = new PipedWriter();
        PipedReader in = new PipedReader();
        // 将输出流和输入流进行连接，否则在使用时会抛出IOException
        out.connect(in);
        Thread printThread = new Thread(new Print(in), "PrintThread");
        printThread.start();
        int receive = 0;
        try {
            while ((receive = System.in.read()) != -1) {
                out.write(receive);
            }
        } finally {
            out.close();
        }
    }

    static class Print implements Runnable {

        private PipedReader in;

        Print(PipedReader in) {
            this.in = in;
        }

        public void run() {
            int receive = 0;
            try {
                while ((receive = in.read()) != -1) {
                    System.out.print((char) receive);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

## 线程生命周期

![img](https://raw.githubusercontent.com/dunwu/images/dev/snap/20210102103928.png)

`java.lang.Thread.State` 中定义了 **6** 种不同的线程状态，在给定的一个时刻，线程只能处于其中的一个状态。

以下是各状态的说明，以及状态间的联系：

- **新建（New）** - 尚未调用 `start` 方法的线程处于此状态。此状态意味着：**创建的线程尚未启动**。

- **就绪（Runnable）** - 已经调用了 `start` 方法的线程处于此状态。此状态意味着：**线程已经在 JVM 中运行**。但是在操作系统层面，它可能处于运行状态，也可能等待资源调度（例如处理器资源），资源调度完成就进入运行状态。所以该状态的可运行是指可以被运行，具体有没有运行要看底层操作系统的资源调度。

- **阻塞（Blocked）** - 此状态意味着：**线程处于被阻塞状态**。表示线程在等待 `synchronized` 的隐式锁（Monitor lock）。`synchronized` 修饰的方法、代码块同一时刻只允许一个线程执行，其他线程只能等待，即处于阻塞状态。当占用 `synchronized` 隐式锁的线程释放锁，并且等待的线程获得 `synchronized` 隐式锁时，就又会从 `BLOCKED` 转换到 `RUNNABLE` 状态。

- **等待（Waiting）** - 此状态意味着：**线程无限期等待，直到被其他线程显式地唤醒**。 阻塞和等待的区别在于，阻塞是被动的，它是在等待获取 `synchronized` 的隐式锁。而等待是主动的，通过调用 `Object.wait` 等方法进入。

  | 进入方法                                                     | 退出方法                             |
  | ------------------------------------------------------------ | ------------------------------------ |
  | 没有设置 Timeout 参数的 `Object.wait` 方法                   | `Object.notify` / `Object.notifyAll` |
  | 没有设置 Timeout 参数的 `Thread.join` 方法                   | 被调用的线程执行完毕                 |
  | `LockSupport.park` 方法（Java 并发包中的锁，都是基于它实现的） | `LockSupport.unpark`                 |

- **定时等待（Timed waiting）** - 此状态意味着：**无需等待其它线程显式地唤醒，在一定时间之后会被系统自动唤醒**。

  | 进入方法                                                     | 退出方法                                        |
  | ------------------------------------------------------------ | ----------------------------------------------- |
  | `Thread.sleep` 方法                                          | 时间结束                                        |
  | 获得 `synchronized` 隐式锁的线程，调用设置了 Timeout 参数的 `Object.wait` 方法 | 时间结束 / `Object.notify` / `Object.notifyAll` |
  | 设置了 Timeout 参数的 `Thread.join` 方法                     | 时间结束 / 被调用的线程执行完毕                 |
  | `LockSupport.parkNanos` 方法                                 | `LockSupport.unpark`                            |
  | `LockSupport.parkUntil` 方法                                 | `LockSupport.unpark`                            |

- **终止(Terminated)** - 线程执行完 `run` 方法，或者因异常退出了 `run` 方法。此状态意味着：线程结束了生命周期。

## 线程常见问题

### sleep、yield、join 方法有什么区别

- `yield` 方法
  - `yield` 方法会 **让线程从 `Running` 状态转入 `Runnable` 状态**。
  - 当调用了 `yield` 方法后，只有**与当前线程相同或更高优先级的`Runnable` 状态线程才会获得执行的机会**。
- `sleep` 方法
  - `sleep` 方法会 **让线程从 `Running` 状态转入 `Waiting` 状态**。
  - `sleep` 方法需要指定等待的时间，**超过等待时间后，JVM 会将线程从 `Waiting` 状态转入 `Runnable` 状态**。
  - 当调用了 `sleep` 方法后，**无论什么优先级的线程都可以得到执行机会**。
  - `sleep` 方法不会释放“锁标志”，也就是说如果有 `synchronized` 同步块，其他线程仍然不能访问共享数据。
- `join`
  - `join` 方法会 **让线程从 `Running` 状态转入 `Waiting` 状态**。
  - 当调用了 `join` 方法后，**当前线程必须等待调用 `join` 方法的线程结束后才能继续执行**。

### 为什么 sleep 和 yield 方法是静态的

`Thread` 类的 `sleep` 和 `yield` 方法将处理 `Running` 状态的线程。

所以在其他处于非 `Running` 状态的线程上执行这两个方法是没有意义的。这就是为什么这些方法是静态的。它们可以在当前正在执行的线程中工作，并避免程序员错误的认为可以在其他非运行线程调用这些方法。

### Java 线程是否按照线程优先级严格执行

即使设置了线程的优先级，也**无法保证高优先级的线程一定先执行**。

原因在于线程优先级依赖于操作系统的支持，然而，不同的操作系统支持的线程优先级并不相同，不能很好的和 Java 中线程优先级一一对应。

### 一个线程两次调用 start()方法会怎样

Java 的线程是不允许启动两次的，第二次调用必然会抛出 IllegalThreadStateException，这是一种运行时异常，多次调用 start 被认为是编程错误。

### `start` 和 `run` 方法有什么区别

- `run` 方法是线程的执行体。
- `start` 方法会启动线程，然后 JVM 会让这个线程去执行 `run` 方法。

### 可以直接调用 `Thread` 类的 `run` 方法么

- 可以。但是如果直接调用 `Thread` 的 `run` 方法，它的行为就会和普通的方法一样。
- 为了在新的线程中执行我们的代码，必须使用 `Thread` 的 `start` 方法。

- 



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

## AQS

> `AbstractQueuedSynchronizer`（简称 **AQS**）是**队列同步器**，顾名思义，其主要作用是处理同步。它是并发锁和很多同步工具类的实现基石（如 `ReentrantLock`、`ReentrantReadWriteLock`、`CountDownLatch`、`Semaphore`、`FutureTask` 等）。

### AQS 的要点

**AQS 提供了对独享锁与共享锁的支持**。

在 `java.util.concurrent.locks` 包中的相关锁（常用的有 `ReentrantLock`、 `ReadWriteLock`）都是基于 AQS 来实现。这些锁都没有直接继承 AQS，而是定义了一个 `Sync` 类去继承 AQS。为什么要这样呢？因为锁面向的是使用用户，而同步器面向的则是线程控制，那么在锁的实现中聚合同步器而不是直接继承 AQS 就可以很好的隔离二者所关注的事情。

### AQS 的应用

**AQS 提供了对独享锁与共享锁的支持**。

#### 独享锁 API

获取、释放独享锁的主要 API 如下：

```java
public final void acquire(int arg)
public final void acquireInterruptibly(int arg)
public final boolean tryAcquireNanos(int arg, long nanosTimeout)
public final boolean release(int arg)
```

- `acquire` - 获取独占锁。
- `acquireInterruptibly` - 获取可中断的独占锁。
- `tryAcquireNanos` - 尝试在指定时间内获取可中断的独占锁。在以下三种情况下回返回：
  - 在超时时间内，当前线程成功获取了锁；
  - 当前线程在超时时间内被中断；
  - 超时时间结束，仍未获得锁返回 false。
- `release` - 释放独占锁。

#### 共享锁 API

获取、释放共享锁的主要 API 如下：

```java
public final void acquireShared(int arg)
public final void acquireSharedInterruptibly(int arg)
public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout)
public final boolean releaseShared(int arg)
```

- `acquireShared` - 获取共享锁。
- `acquireSharedInterruptibly` - 获取可中断的共享锁。
- `tryAcquireSharedNanos` - 尝试在指定时间内获取可中断的共享锁。
- `release` - 释放共享锁。

### AQS 的原理

> ASQ 原理要点：
>
> - AQS 使用一个整型的 `volatile` 变量来 **维护同步状态**。状态的意义由子类赋予。
> - AQS 维护了一个 FIFO 的双链表，用来存储获取锁失败的线程。
>
> AQS 围绕同步状态提供两种基本操作“获取”和“释放”，并提供一系列判断和处理方法，简单说几点：
>
> - state 是独占的，还是共享的；
> - state 被获取后，其他线程需要等待；
> - state 被释放后，唤醒等待线程；
> - 线程等不及时，如何退出等待。
>
> 至于线程是否可以获得 state，如何释放 state，就不是 AQS 关心的了，要由子类具体实现。

#### AQS 的数据结构

阅读 AQS 的源码，可以发现：AQS 继承自 `AbstractOwnableSynchronize`。

```java
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer
    implements java.io.Serializable {

    /** 等待队列的队头，懒加载。只能通过 setHead 方法修改。 */
    private transient volatile Node head;
    /** 等待队列的队尾，懒加载。只能通过 enq 方法添加新的等待节点。*/
    private transient volatile Node tail;
    /** 同步状态 */
    private volatile int state;
}
```

- `state` - AQS 使用一个整型的 `volatile` 变量来 **维护同步状态**。
  - 这个整数状态的意义由子类来赋予，如`ReentrantLock` 中该状态值表示所有者线程已**经重复获取该锁的次数**，`Semaphore` 中该状态值表示**剩余的许可数量**。
- `head` 和 `tail` - AQS **维护了一个 `Node` 类型（AQS 的内部类）的双链表来完成同步状态的管理**。这个双链表是一个双向的 FIFO 队列，通过 `head` 和 `tail` 指针进行访问。当 **有线程获取锁失败后，就被添加到队列末尾**。

![img](https://raw.githubusercontent.com/dunwu/images/dev/cs/java/javacore/concurrent/aqs_1.png)

再来看一下 `Node` 的源码

```java
static final class Node {
    /** 该等待同步的节点处于共享模式 */
    static final Node SHARED = new Node();
    /** 该等待同步的节点处于独占模式 */
    static final Node EXCLUSIVE = null;

    /** 线程等待状态，状态值有: 0、1、-1、-2、-3 */
    volatile int waitStatus;
    static final int CANCELLED =  1;
    static final int SIGNAL    = -1;
    static final int CONDITION = -2;
    static final int PROPAGATE = -3;

    /** 前驱节点 */
    volatile Node prev;
    /** 后继节点 */
    volatile Node next;
    /** 等待锁的线程 */
    volatile Thread thread;

  	/** 和节点是否共享有关 */
    Node nextWaiter;
}
```

很显然，Node 是一个双链表结构。

- `waitStatus` - `Node` 使用一个整型的 `volatile` 变量来 维护 AQS 同步队列中线程节点的状态。`waitStatus` 有五个状态值：
  - `CANCELLED(1)` - 此状态表示：该节点的线程可能由于超时或被中断而 **处于被取消(作废)状态**，一旦处于这个状态，表示这个节点应该从等待队列中移除。
  - `SIGNAL(-1)` - 此状态表示：**后继节点会被挂起**，因此在当前节点释放锁或被取消之后，必须唤醒(`unparking`)其后继结点。
  - `CONDITION(-2)` - 此状态表示：该节点的线程 **处于等待条件状态**，不会被当作是同步队列上的节点，直到被唤醒(`signal`)，设置其值为 0，再重新进入阻塞状态。
  - `PROPAGATE(-3)` - 此状态表示：下一个 `acquireShared` 应无条件传播。
  - 0 - 非以上状态。

#### 独占锁的获取和释放

##### 获取独占锁

AQS 中使用 `acquire(int arg)` 方法获取独占锁，其大致流程如下：

1. 先尝试获取同步状态，如果获取同步状态成功，则结束方法，直接返回。
2. 如果获取同步状态不成功，AQS 会不断尝试利用 CAS 操作将当前线程插入等待同步队列的队尾，直到成功为止。
3. 接着，不断尝试为等待队列中的线程节点获取独占锁。

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/aqs_2.png)

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/aqs_3.png)

详细流程可以用下图来表示，请结合源码来理解（一图胜千言）：

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/aqs_4.png)

##### 释放独占锁

AQS 中使用 `release(int arg)` 方法释放独占锁，其大致流程如下：

1. 先尝试获取解锁线程的同步状态，如果获取同步状态不成功，则结束方法，直接返回。
2. 如果获取同步状态成功，AQS 会尝试唤醒当前线程节点的后继节点。

##### 获取可中断的独占锁

AQS 中使用 `acquireInterruptibly(int arg)` 方法获取可中断的独占锁。

`acquireInterruptibly(int arg)` 实现方式**相较于获取独占锁方法（ `acquire`）非常相似**，区别仅在于它会**通过 `Thread.interrupted` 检测当前线程是否被中断**，如果是，则立即抛出中断异常（`InterruptedException`）。

##### 获取超时等待式的独占锁

AQS 中使用 `tryAcquireNanos(int arg)` 方法获取超时等待的独占锁。

doAcquireNanos 的实现方式 **相较于获取独占锁方法（ `acquire`）非常相似**，区别在于它会根据超时时间和当前时间计算出截止时间。在获取锁的流程中，会不断判断是否超时，如果超时，直接返回 false；如果没超时，则用 `LockSupport.parkNanos` 来阻塞当前线程。

#### 共享锁的获取和释放

##### 获取共享锁

AQS 中使用 `acquireShared(int arg)` 方法获取共享锁。

`acquireShared` 方法和 `acquire` 方法的逻辑很相似，区别仅在于自旋的条件以及节点出队的操作有所不同。

成功获得共享锁的条件如下：

- `tryAcquireShared(arg)` 返回值大于等于 0 （这意味着共享锁的 permit 还没有用完）。
- 当前节点的前驱节点是头结点。

##### 释放共享锁

AQS 中使用 `releaseShared(int arg)` 方法释放共享锁。

`releaseShared` 首先会尝试释放同步状态，如果成功，则解锁一个或多个后继线程节点。释放共享锁和释放独享锁流程大体相似，区别在于：

对于独享模式，如果需要 SIGNAL，释放仅相当于调用头节点的 `unparkSuccessor`。

##### 获取可中断的共享锁

AQS 中使用 `acquireSharedInterruptibly(int arg)` 方法获取可中断的共享锁。

`acquireSharedInterruptibly` 方法与 `acquireInterruptibly` 几乎一致，不再赘述。

##### 获取超时等待式的共享锁

AQS 中使用 `tryAcquireSharedNanos(int arg)` 方法获取超时等待式的共享锁。

`tryAcquireSharedNanos` 方法与 `tryAcquireNanos` 几乎一致，不再赘述。



### CountDownLatch

先来看一个最简单的 CountDownLatch 使用方法，例子很简单，可以运行看一下效果。CountDownLatch 的作用是：当一个线程需要另外一个或多个线程完成后，再开始执行。比如主线程要等待一个子线程完成环境相关配置的加载工作，主线程才继续执行，就可以利用 CountDownLatch 来实现。

#### 回顾举例

首先实例化一个 CountDownLatch ，参数可以理解为一个计数器，这里为 1，然后主线程执行，调用 worker 子线程，接着调用 CountDownLatch 的 await() 方法，表示阻塞主线程。当子线程执行完成后，在 finnaly 块调用 countDown() 方法，表示一个等待已经完成，把计数器减一，直到减为 0，主线程又开始执行。

```java
private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException{
        System.out.println("主线程开始......");
        Thread thread = new Thread(new Worker());
        thread.start();
        System.out.println("主线程等待......");
        System.out.println(latch.toString());
        latch.await();
        System.out.println(latch.toString());
        System.out.println("主线程继续.......");
    }

    public static class Worker implements Runnable {

        @Override
        public void run() {
            System.out.println("子线程任务正在执行");
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e){

            }finally {
                latch.countDown();
            }
        }
    }
```

执行结果如下：

```erlang
主线程开始......
子线程任务正在执行
主线程等待......
java.util.concurrent.CountDownLatch@1d44bcfa[Count = 1]
java.util.concurrent.CountDownLatch@1d44bcfa[Count = 0]
主线程继续.......
```



![Untitled](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/Untitled.gif)



首先在 CountDownLatch 类内部定义了一个 Sync 内部类，这个内部类就是继承自 AbstractQueuedSynchronizer 的。并且重写了方法 `tryAcquireShared`和`tryReleaseShared`。例如当调用 `awit()`方法时，CountDownLatch 会调用内部类Sync 的 `acquireSharedInterruptibly() ` 方法，然后在这个方法中会调用 `tryAcquireShared` 方法，这个方法就是 CountDownLatch 的内部类 Sync 里重写的 AbstractQueuedSynchronizer 的方法。调用 `countDown()` 方法同理。

这种方式是使用 AbstractQueuedSynchronizer 的标准化方式，大致分为两步：

1、内部持有继承自 AbstractQueuedSynchronizer 的对象 Sync；

2、并在 Sync 内重写 AbstractQueuedSynchronizer protected 的部分或全部方法，这些方法包括如下几个：
![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/273364-20180608074941782-565895757.png)

之所以要求子类重写这些方法，是为了让使用者（这里的使用者指 CountDownLatch 等）可以在其中加入自己的判断逻辑，例如 CountDownLatch 在 `tryAcquireShared`中加入了判断，判断 state 是否不为0，如果不为0，才符合调用条件。

`tryAcquire`和`tryRelease`是对应的，前者是独占模式获取，后者是独占模式释放。

`tryAcquireShared`和`tryReleaseShared`是对应的，前者是共享模式获取，后者是共享模式释放。

我们看到 CountDownLatch 重写的方法 tryAcquireShared 实现如下：

```java
protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }
```

判断 state 值是否为0，为0 返回1，否则返回 -1。state 值是 AbstractQueuedSynchronizer 类中的一个 volatile 变量。

```java
private volatile int state;
```

在 CountDownLatch 中这个 state 值就是计数器，在调用 await 方法的时候，将值赋给 state 。

#### **等待线程入队**

根据上面的逻辑，调用 await() 方法时，先去获取 state 的值，当计数器不为0的时候，说明还有需要等待的线程在运行，则调用 doAcquireSharedInterruptibly 方法，进来执行的第一个动作就是尝试加入等待队列 ，即调用 addWaiter（）方法， 源码如下：

到这里就走到了 AQS 的核心部分，AQS 用内部的一个 Node 类维护一个 CHL Node FIFO 队列。将当前线程加入等待队列，并通过 parkAndCheckInterrupt（）方法实现当前线程的阻塞。下面一大部分都是在说明 CHL 队列的实现，里面用 CAS 实现队列出入不会发生阻塞。

```java
private void doAcquireSharedInterruptibly(int arg)
        throws InterruptedException {
    	//加入等待队列 				      
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;
    	// 进入 CAS 循环
        try {
            for (;;) {
                //当一个节点(关联一个线程)进入等待队列后， 获取此节点的 prev 节点 
                final Node p = node.predecessor();
                // 如果获取到的 prev 是 head，也就是队列中第一个等待线程
                if (p == head) {
                    // 再次尝试申请 反应到 CountDownLatch 就是查看是否还有线程需要等待(state是否为0)
                    int r = tryAcquireShared(arg);
                    // 如果 r >=0 说明 没有线程需要等待了 state==0
                    if (r >= 0) {
                        //尝试将第一个线程关联的节点设置为 head 
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        failed = false;
                        return;
                    }
                }
                //经过自旋tryAcquireShared后，state还不为0，就会到这里，第一次的时候，waitStatus是0，那么node的waitStatus就会被置为SIGNAL，第二次再走到这里，就会用LockSupport的park方法把当前线程阻塞住
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
```

我看看到上面先执行了 addWaiter() 方法，就是将当前线程加入等待队列，源码如下：

```java
/** Marker to indicate a node is waiting in shared mode */
 static final Node SHARED = new Node();
 /** Marker to indicate a node is waiting in exclusive mode */
 static final Node EXCLUSIVE = null;

private Node addWaiter(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        // 尝试快速入队操作，因为大多数时候尾节点不为 null
        Node pred = tail;
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
    	//如果尾节点为空(也就是队列为空) 或者尝试CAS入队失败(由于并发原因)，进入enq方法
        enq(node);
        return node;
    }
```

上面是向等待队列中添加等待者（waiter）的方法。首先构造一个 Node 实体，参数为当前线程和一个mode，这个mode有两种形式，一个是 SHARED ，一个是 EXCLUSIVE，请看上面的代码。然后执行下面的入队操作 addWaiter，和 enq() 方法的 else 分支操作是一样的，这里的操作如果成功了，就不用再进到 enq() 方法的循环中去了，可以提高性能。如果没有成功，再调用 enq() 方法。

```java
private Node enq(final Node node) {
    	// 死循环+CAS保证所有节点都入队
        for (;;) {
            Node t = tail;
            // 如果队列为空 设置一个空节点作为 head
            if (t == null) { // Must initialize
                if (compareAndSetHead(new Node()))
                    tail = head;
            } else {
                //加入队尾
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }
```

说明：循环加 CAS 操作是实现乐观锁的标准方式，CAS 是为了实现原子操作而出现的，所谓的原子操作指操作执行期间，不会受其他线程的干扰。Java 实现的 CAS 是调用 unsafe 类提供的方法，底层是调用 c++ 方法，直接操作内存，在 cpu 层面加锁，直接对内存进行操作。

上面是 AQS 等待队列入队方法，操作在无限循环中进行，如果入队成功则返回新的队尾节点，否则一直自旋，直到入队成功。假设入队的节点为 node ，上来直接进入循环，在循环中，先拿到尾节点。

1、if 分支，如果尾节点为 null，说明现在队列中还没有等待线程，则尝试 CAS 操作将头节点初始化，然后将尾节点也设置为头节点，因为初始化的时候头尾是同一个，这和 AQS 的设计实现有关， AQS 默认要有一个虚拟节点。此时，尾节点不在为空，循环继续，进入 else 分支；

2、else 分支，如果尾节点不为 null， node.prev = t ，也就是将当前尾节点设置为待入队节点的前置节点。然后又是利用 CAS 操作，将待入队的节点设置为队列的尾节点，如果 CAS 返回 false，表示未设置成功，继续循环设置，直到设置成功，接着将之前的尾节点（也就是倒数第二个节点）的 next 属性设置为当前尾节点，对应 t.next = node 语句，然后返回当前尾节点，退出循环。

setHeadAndPropagate 方法负责将自旋等待或被 LockSupport 阻塞的线程唤醒。

```java
private void setHeadAndPropagate(Node node, int propagate) {
    	//备份现在的 head
        Node h = head;  
    	//抢到锁的线程被唤醒 将这个节点设置为head
        setHead(node)
    	// propagate 一般都会大于0 或者存在可被唤醒的线程
        if (propagate > 0 || h == null || h.waitStatus < 0 ||
            (h = head) == null || h.waitStatus < 0) {
            Node s = node.next;
            // 只有一个节点 或者是共享模式 释放所有等待线程 各自尝试抢占锁
            if (s == null || s.isShared())
                doReleaseShared();
        }
    }
```

Node 对象中有一个属性是 waitStatus ，它有四种状态，分别是：

```java
//线程已被 cancelled ，这种状态的节点将会被忽略，并移出队列
static final int CANCELLED =  1;
// 表示当前线程已被挂起，并且后继节点可以尝试抢占锁
static final int SIGNAL    = -1;
//线程正在等待某些条件
static final int CONDITION = -2;
//共享模式下 无条件所有等待线程尝试抢占锁
static final int PROPAGATE = -3;
```

#### **等待线程被唤醒**

当执行 CountDownLatch 的 countDown（）方法，将计数器减一，也就是state减一，当减到0的时候，等待队列中的线程被释放。是调用 AQS 的 releaseShared 方法来实现的，下面代码中的方法是按顺序调用的，摘到了一起，方便查看：

```java
// AQS类
public final boolean releaseShared(int arg) {
    	// arg 为固定值 1
    	// 如果计数器state 为0 返回true，前提是调用 countDown() 之前不能已经为0
        if (tryReleaseShared(arg)) {
            // 唤醒等待队列的线程
            doReleaseShared();
            return true;
        }
        return false;
    }

// CountDownLatch 重写的方法
protected boolean tryReleaseShared(int releases) {
            // Decrement count; signal when transition to zero
    		// 依然是循环+CAS配合 实现计数器减1
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }

/// AQS类
 private void doReleaseShared() {
        for (;;) {
            Node h = head;
            if (h != null && h != tail) {
                int ws = h.waitStatus;
                // 如果节点状态为SIGNAL，则他的next节点也可以尝试被唤醒
                if (ws == Node.SIGNAL) {
                    if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
                        continue;            // loop to recheck cases
                    unparkSuccessor(h);
                }
                // 将节点状态设置为PROPAGATE，表示要向下传播，依次唤醒
                else if (ws == 0 &&
                         !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
                    continue;                // loop on failed CAS
            }
            if (h == head)                   // loop if head changed
                break;
        }
    }
```

因为这是共享型的，当计数器为 0 后，会唤醒等待队列里的所有线程，所有调用了 await() 方法的线程都被唤醒，并发执行。这种情况对应到的场景是，有多个线程需要等待一些动作完成，比如一个线程完成初始化动作，其他5个线程都需要用到初始化的结果，那么在初始化线程调用 countDown 之前，其他5个线程都处在等待状态。一旦初始化线程调用了 countDown ，其他5个线程都被唤醒，开始执行。

#### **总结**

1、AQS 分为独占模式和共享模式，CountDownLatch 使用了它的共享模式。

2、AQS 当第一个等待线程（被包装为 Node）要入队的时候，要保证存在一个 head 节点，这个 head 节点不关联线程，也就是一个虚节点。

3、当队列中的等待节点（关联线程的，非 head 节点）抢到锁，将这个节点设置为 head 节点。

4、第一次自旋抢锁失败后，waitStatus 会被设置为 -1（SIGNAL），第二次再失败，就会被 LockSupport 阻塞挂起。

5、如果一个节点的前置节点为 SIGNAL 状态，则这个节点可以尝试抢占锁。

实现简单的一次性门闩

```java
public class OneShortLatch {
//    实现一个一次性门闩

    private final Sync sync = new Sync();

    public void signal(){
        sync.releaseShared(0);
    }

    public void await(){
        sync.acquireShared(0);
    }
    private class Sync extends AbstractQueuedSynchronizer{
        //没有要求实现方法
        @Override
        protected int tryAcquireShared(int arg) {
            return (getState() == 1)?1:-1;//门闩是不是打开的
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            setState(1);
            return true;//开闸了
        }

    }

    public static void main(String[] args) throws InterruptedException {
        OneShortLatch oneShortLatch = new OneShortLatch();
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("尝试获取Lathc");
                    oneShortLatch.await();
                    System.out.println("开始运行");

                }
            }).start();
        }
        Thread.sleep(5000);
        oneShortLatch.signal();
    }
}
```



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

Java 数据类型分为 **基本数据类型** 和 **引用数据类型** 两大类。

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

## 原子数组类型

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

## 原子化的累加器LongAccumulator与LongAdder :satellite:

`DoubleAccumulator`、`DoubleAdder`、`LongAccumulator` 和 `LongAdder`，这四个类仅仅用来执行累加操作，相比原子化的基本数据类型AtomicLong或AtomicInteger，速度更快，但是不支持 `compareAndSet()` 方法。如果你仅仅需要累加操作，使用原子化的累加器性能会更好，代价就是会消耗更多的内存空间。

`LongAdder` 内部由一个 `base` 变量和一个 `cell[]` 数组组成。

- 当只有一个写线程，没有竞争的情况下，`LongAdder` 会直接使用 `base` 变量作为原子操作变量，通过 CAS 操作修改变量；
- 当有多个写线程竞争的情况下，除了占用 `base` 变量的一个写线程之外，其它各个线程会将修改的变量写入到自己的槽 `cell[]` 数组中。

我们可以发现，`LongAdder` 在操作后的返回值只是一个近似准确的数值，但是 `LongAdder` 最终返回的是一个准确的数值， 所以在一些对实时性要求比较高的场景下，`LongAdder` 并不能取代 `AtomicInteger` 或 `AtomicLong`。

### Accumulator基本使用

那么 Accumulator 又是做什么的呢？Accumulator 和 Adder 非常相似，实际上 Accumulator 就是一个更通用版本的 Adder，比如 LongAccumulator 是 LongAdder 的功能增强版，因为 LongAdder 的 API 只有对数值的加减，而 LongAccumulator 提供了自定义的函数操作。

让我们用一个非常直观的代码来举例说明一下，代码如下：



```java
public class LongAccumulatorDemo {
public static void main(String[] args) throws InterruptedException {
    LongAccumulator accumulator = new LongAccumulator((x, y) -> x + y, 0);
    ExecutorService executor = Executors.newFixedThreadPool(8);
    IntStream.range(1, 10).forEach(i -> executor.submit(() -> accumulator.accumulate(i)));

    Thread.sleep(2000);
    System.out.println(accumulator.getThenReset());
}
    }
```

在这段代码中：

首先新建了一个 LongAccumulator，同时给它传入了两个参数；
然后又新建了一个 8 线程的线程池，并且利用整形流也就是` IntStream `往线程池中提交了从 1 ~ 9 这 9 个任务；

> IntStream.range()产生指定区间的有序IntStream，这里需要传入一个区间（左闭右开），产生的元素不包含最后一个。

之后等待了两秒钟，这两秒钟的作用是等待线程池的任务执行完毕；
最后把 accumulator 的值打印出来。
这段代码的运行结果是 45，代表 0+1+2+3+…+8+9=45 的结果，这个结果怎么理解呢？我们先重点看看新建的 LongAccumulator 的这一行语句：

```java
LongAccumulator accumulator = new LongAccumulator((x, y) -> x + y, 0);
```

在这个语句中，我们传入了两个参数：LongAccumulator 的构造函数的第一个参数是二元表达式；第二个参数是 x 的初始值，传入的是 0。在二元表达式中，x 是上一次计算的结果（除了第一次的时候需要传入），y 是本次新传入的值。

**案例分析**
我们来看一下上面这段代码执行的过程，当执行 accumulator.accumulate(1) 的时候，首先要知道这时候 x 和 y 是什么，**第一次执行时， x 是 LongAccumulator 构造函数中的第二个参数，也就是 0，**而第一次执行时的 y 值就是本次 accumulator.accumulate(1) 方法所传入的 1；然后根据表达式 x+y，计算出 0+1=1，这个结果会赋值给下一次计算的 x，而下一次计算的 y 值就是 accumulator.accumulate(2) 传入的 2，所以下一次的计算结果是 1+2=3。

我们在 IntStream.range(1, 10).forEach(i -> executor.submit(() -> accumulator.accumulate(i))); 这一行语句中实际上利用了整型流，分别给线程池提交了从 1 ~ 9 这 9 个任务，相当于执行了：

accumulator.accumulate(1);
accumulator.accumulate(2);
accumulator.accumulate(3);
...
accumulator.accumulate(8);
accumulator.accumulate(9);


那么根据上面的这个推演，就可以得出它的内部运行，这也就意味着LongAccumulator 执行了：

0+1=1;
1+2=3;
3+3=6;
6+4=10;
10+5=15;
15+6=21;
21+7=28;
28+8=36;
36+9=45;

这里需要指出的是，这里的加的顺序是不固定的，并不是说会按照顺序从 1 开始逐步往上累加，它也有可能会变，比如说先加 5、再加 3、再加 6。但总之，由于加法有交换律，所以最终加出来的结果会保证是 45。这就是这个类的一个基本的作用和用法。

**案例二-计算最大值**

```java
public class AccumulatorDemo {
    public static void main(String[] args) {
        LongAccumulator accumulator = new LongAccumulator((x, y) -> Math.max(x,y), 0);
        ExecutorService service = Executors.newFixedThreadPool(10);
        IntStream.range(1,10).forEach(i -> service.submit(()-> accumulator.accumulate(i)));
        service.shutdown();
        while (!service.isTerminated()){
//            等待结束之后再输出
        }
        System.out.println(accumulator.getThenReset());//打印出来
    }
}
```



> 这个累加器是可以并行计算的，比单独的for loop要快很多、



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

## CAS底层原理 - 很多锁的原理

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

## ABA问题

所谓ABA问题，就是比较并交换的循环，存在一个**时间差**，而这个时间差可能带来意想不到的问题。比如线程T1将值从A改为B，然后又从B改为A。线程T2看到的就是A，但是**却不知道这个A发生了更改**。尽管线程T2 CAS操作成功，但不代表就没有问题。
有的需求，比如CAS，**只注重头和尾**，只要首尾一致就接受。但是有的需求，还看重过程，中间不能发生任何修改，这就引出了`AtomicReference`原子引用。

### AtomicReference

`AtomicInteger`对整数进行原子操作，如果是一个POJO呢？可以用`AtomicReference`来包装这个POJO，使其操作原子化。

```java
User user1 = new User("Jack",25);
User user2 = new User("Lucy",21);
AtomicReference<User> atomicReference = new AtomicReference<>();
atomicReference.set(user1);
System.out.println(atomicReference.compareAndSet(user1,user2)); // true
System.out.println(atomicReference.compareAndSet(user1,user2)); //false
```

### AtomicStampedReference和ABA问题的解决

使用`AtomicStampedReference`类可以解决ABA问题。这个类维护了一个“**版本号**”Stamp，在进行CAS操作的时候，不仅要比较当前值，还要比较**版本号**。只有两者都相等，才执行更新操作。

```java
AtomicStampedReference.compareAndSet(expectedReference,newReference,oldStamp,newStamp);
```



## 什么是不变性

- 如果对象在被创建后，状态就不能被修改，那么他就是不可变的
- 具有不变性的对象，一定是线程安全的，我们不需要采取任何额外的安全措施，也能保证线程安全

### final的作用

- 被final关键字修饰的类不能被继承，被final关键字修饰的类属性和类方法不能被覆盖（重写）；

### 3种用法：修饰方法，变量，类

修饰变量 ：不能被改变
![在这里插入图片描述](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/20200611220228970.png)
修饰方法：

- 构造方法不允许修饰
- 不可被重写，也就是不能被override，即便子类有同样名字的方法，那也不是override
  ![在这里插入图片描述](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/20200611220600747.png)

修饰类：

- final关键字修饰的类不能被继承
- ![在这里插入图片描述](https://ucc.alicdn.com/images/user-upload-01/20200611221034370.png)

### 注意点

- final 修饰对象的时候，只有对象的引用不可变，而对象本身的属性是可以变的
- final使用原则，良好的编程习惯

### 不变性和final的关系

- 基本数据类型，确实被final修饰的就是不可变的
- 但是对于对象类型，需要该对象保证自己被创建后，状态永远不变才可以

不可变的第二种情况

把变量写在线程内部，---**-栈封闭**

- 在方法里新建局部变量，实际上是存储在每个线程私有栈空间，而每个栈空间不能被其他线程访问到，所以，不会出现线程安全问题，这就是著名的 “栈封闭” 技术
  ![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/20200611223617728.png)



# Java 并发和容器 :tea:

## 同步容器  synchronized vector

### 同步容器简介

在 Java 中，同步容器主要包括 2 类：

- `Vector`、`Stack`、`Hashtable`
  - `Vector` - `Vector` 实现了 `List` 接口。`Vector` 实际上就是一个数组，和 `ArrayList` 类似。但是 `Vector` 中的方法都是 `synchronized` 方法，即进行了同步措施。
  - `Stack` - `Stack` 也是一个同步容器，它的方法也用 `synchronized` 进行了同步，它实际上是继承于 `Vector` 类。
  - **`Hashtable`- `Hashtable` 实现了 `Map` 接口，它和 `HashMap` 很相似，但是 `Hashtable` 进行了同步处理，而 `HashMap` 没有。**
- `Collections` 类中提供的静态工厂方法创建的类（由 `Collections.synchronizedXXX` 等方法）

### 同步容器的问题

同步容器的同步原理就是在其 `get`、`set`、`size` 等主要方法上用 `synchronized` 修饰。 **`synchronized` 可以保证在同一个时刻，只有一个线程可以执行某个方法或者某个代码块**。



#### 性能问题

`synchronized` 的互斥同步会产生阻塞和唤醒线程的开销。显然，这种方式比没有使用 `synchronized` 的容器性能要差很多。

> 注：尤其是在 Java 1.6 没有对 `synchronized` 进行优化前，阻塞开销很高。

#### 安全问题

同步容器真的绝对安全吗？

其实也未必。在做复合操作（非原子操作）时，仍然需要加锁来保护。常见复合操作如下：

- **迭代**：反复访问元素，直到遍历完全部元素；
- **跳转**：根据指定顺序寻找当前元素的下一个（下 n 个）元素；
- **条件运算**：例如若没有则添加等；

❌ 不安全的示例

```java
public class VectorDemo {

    static Vector<Integer> vector = new Vector<>();

    public static void main(String[] args) {
        while (true) {
            vector.clear();

            for (int i = 0; i < 10; i++) {
                vector.add(i);
            }

            Thread thread1 = new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < vector.size(); i++) {
                        vector.remove(i);
                    }
                }
            };

            Thread thread2 = new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < vector.size(); i++) {
                        vector.get(i);
                    }
                }
            };

            thread1.start();
            thread2.start();

            while (Thread.activeCount() > 10) {
                System.out.println("同时存在 10 个以上线程，退出");
                return;
            }
        }
    }

}
```

以上程序执行时可能会出现数组越界错误。

`Vector` 是线程安全的，那为什么还会报这个错？

这是因为，对于 Vector，虽然能保证每一个时刻只能有一个线程访问它，但是不排除这种可能：

当某个线程在某个时刻执行这句时：

```java
for(int i=0;i<vector.size();i++)
    vector.get(i);
```

假若此时 vector 的 size 方法返回的是 10，i 的值为 9

然后另外一个线程执行了这句：

```java
for(int i=0;i<vector.size();i++)
    vector.remove(i);
```

将下标为 9 的元素删除了。

那么通过 get 方法访问下标为 9 的元素肯定就会出问题了。

✔ 安全示例

因此为了保证线程安全，必须在方法调用端做额外的同步措施，如下面所示：

```java
public class VectorDemo2 {

    static Vector<Integer> vector = new Vector<Integer>();

    public static void main(String[] args) {
        while (true) {
            for (int i = 0; i < 10; i++) {
                vector.add(i);
            }

            Thread thread1 = new Thread() {
                @Override
                public void run() {
                    synchronized (VectorDemo2.class) {   //进行额外的同步
                        for (int i = 0; i < vector.size(); i++) {
                            vector.remove(i);
                        }
                    }
                }
            };

            Thread thread2 = new Thread() {
                @Override
                public void run() {
                    synchronized (VectorDemo2.class) {
                        for (int i = 0; i < vector.size(); i++) {
                            vector.get(i);
                        }
                    }
                }
            };

            thread1.start();
            thread2.start();

            while (Thread.activeCount() > 10) {
                System.out.println("同时存在 10 个以上线程，退出");
                return;
            }
        }
    }

}
```

`ConcurrentModificationException` 异常

在对 `Vector` 等容器并发地进行迭代修改时，会报 `ConcurrentModificationException` 异常，关于这个异常将会在后续文章中讲述。

但是在并发容器中不会出现这个问题。

## 并发容器简介

同步容器将所有对容器状态的访问都串行化，以保证线程安全性，这种策略会严重降低并发性。

Java 1.5 后提供了多种并发容器，**使用并发容器来替代同步容器，可以极大地提高伸缩性并降低风险**。

> List<Integer> test = Collections.*synchronizedList*(new ArrayList<>());
>
> 这样也可以保证线程安全，但是，这样性能也很低

J.U.C 包中提供了几个非常有用的并发容器作为线程安全的容器：

| 并发容器                | 对应的普通容器 | 描述                                                         |
| ----------------------- | -------------- | ------------------------------------------------------------ |
| `ConcurrentHashMap`     | `HashMap`      | Java 1.8 之前采用分段锁机制细化锁粒度，降低阻塞，从而提高并发性；Java 1.8 之后基于 CAS 实现。 |
| `ConcurrentSkipListMap` | `SortedMap`    | 基于跳表实现的                                               |
| `CopyOnWriteArrayList`  | `ArrayList`    |                                                              |
| `CopyOnWriteArraySet`   | `Set`          | 基于 `CopyOnWriteArrayList` 实现。                           |
| `ConcurrentSkipListSet` | `SortedSet`    | 基于 `ConcurrentSkipListMap` 实现。                          |
| `ConcurrentLinkedQueue` | `Queue`        | 线程安全的无界队列。底层采用单链表。支持 FIFO。              |
| `ConcurrentLinkedDeque` | `Deque`        | 线程安全的无界双端队列。底层采用双向链表。支持 FIFO 和 FILO。 |
| `ArrayBlockingQueue`    | `Queue`        | 数组实现的阻塞队列。                                         |
| `LinkedBlockingQueue`   | `Queue`        | 链表实现的阻塞队列。                                         |
| `LinkedBlockingDeque`   | `Deque`        | 双向链表实现的双端阻塞队列。                                 |

J.U.C 包中提供的并发容器命名一般分为三类：

- `Concurrent`
  - 这类型的**锁竞争相对于 `CopyOnWrite` 要高一些，但写操作代价要小一些。**
  - 此外，`Concurrent` 往往提供了较低的遍历一致性，即：当利用迭代器遍历时，如果容器发生修改，迭代器仍然可以继续进行遍历。代价就是，在获取容器大小 `size()` ，容器是否为空等方法，不一定完全精确，但这是为了获取并发吞吐量的设计取舍，可以理解。与之相比，如果是使用同步容器，就会出现 `fail-fast` 问题，即：检测到容器在遍历过程中发生了修改，则抛出 `ConcurrentModificationException`，不再继续遍历。
- `CopyOnWrite` - 一个线程写，多个线程读。读操作时不加锁，写操作时通过在副本上加锁保证并发安全，空间开销较大。
- `Blocking` - 内部实现一般是基于锁，提供阻塞队列的能力。

:x: 错误示例，产生 `ConcurrentModificationException` 异常：

```java
public void removeKeys(Map<String, Object> map, final String... keys) {
    map.keySet().removeIf(key -> ArrayUtil.contains(keys, key));
}
```

:x: 错误示例，产生 `ConcurrentModificationException` 异常：

```java
public static <K, V> Map<K, V> removeKeys(Map<String, Object> map, final String... keys) {
	for (K key : keys) {
		map.remove(key);
	}
	return map;
}
```

### 并发场景下的 Map

如果对数据有强一致要求，则需使用 `Hashtable`；在大部分场景通常都是弱一致性的情况下，使用 `ConcurrentHashMap` 即可；如果数据量在千万级别，且存在大量增删改操作，则可以考虑使用 `ConcurrentSkipListMap`。

### 并发场景下的 List

读多写少用 `CopyOnWriteArrayList`。

写多读少用 `ConcurrentLinkedQueue` ，但由于是无界的，要有容量限制，避免无限膨胀，导致内存溢出。

## Map

![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/595137-20160306141629471-2007352525.jpg)



Map 接口的两个实现是 ConcurrentHashMap 和 ConcurrentSkipListMap，它们从应用的角度来看，主要区别在于**ConcurrentHashMap 的 key 是无序的，而 ConcurrentSkipListMap 的 key 是有序的**。所以如果你需要保证 key 的顺序，就只能使用 ConcurrentSkipListMap。

使用 ConcurrentHashMap 和 ConcurrentSkipListMap 需要注意的地方是，它们的 key 和 value 都不能为空，否则会抛出`NullPointerException`这个运行时异常。

### ConcurrentHashMap

`ConcurrentHashMap` 是线程安全的 `HashMap` ，用于替代 `Hashtable`。

#### `ConcurrentHashMap` 的特性

`ConcurrentHashMap` `实现了` `ConcurrentMap` 接口，而 `ConcurrentMap` 接口扩展了 `Map` 接口。

```java
public class ConcurrentHashMap<K,V> extends AbstractMap<K,V>
    implements ConcurrentMap<K,V>, Serializable {
    // ...
}
```

`ConcurrentHashMap` 的实现包含了 `HashMap` 所有的基本特性，如：数据结构、读写策略等。

`ConcurrentHashMap` 没有实现对 `Map` 加锁以提供独占访问。因此无法通过在客户端加锁的方式来创建新的原子操作。但是，一些常见的复合操作，如：“若没有则添加”、“若相等则移除”、“若相等则替换”，都已经实现为原子操作，并且是围绕 `ConcurrentMap` 的扩展接口而实现。

```java
public interface ConcurrentMap<K, V> extends Map<K, V> {

    // 仅当 K 没有相应的映射值才插入
    V putIfAbsent(K key, V value);

    // 仅当 K 被映射到 V 时才移除
    boolean remove(Object key, Object value);

    // 仅当 K 被映射到 oldValue 时才替换为 newValue
    boolean replace(K key, V oldValue, V newValue);

    // 仅当 K 被映射到某个值时才替换为 newValue
    V replace(K key, V value);
}
```

不同于 `Hashtable`，`ConcurrentHashMap` 提供的迭代器不会抛出 `ConcurrentModificationException`，因此不需要在迭代过程中对容器加锁。

> :bell: 注意：一些需要对整个 `Map` 进行计算的方法，如 `size` 和 `isEmpty` ，由于返回的结果在计算时可能已经过期，所以**并非实时的精确值**。这是一种策略上的权衡，在并发环境下，这类方法由于总在不断变化，所以获取其实时精确值的意义不大。`ConcurrentHashMap` 弱化这类方法，以换取更重要操作（如：`get`、`put`、`containesKey`、`remove` 等）的性能。

#### ConcurrentHashMap 的用法

示例：不会出现 `ConcurrentModificationException`

`ConcurrentHashMap` 的基本操作与 `HashMap` 的用法基本一样。不同于 `HashMap`、`Hashtable`，`ConcurrentHashMap` 提供的迭代器不会抛出 `ConcurrentModificationException`，因此不需要在迭代过程中对容器加锁。

```java
public class ConcurrentHashMapDemo {

    public static void main(String[] args) throws InterruptedException {

        // HashMap 在并发迭代访问时会抛出 ConcurrentModificationException 异常
        // Map<Integer, Character> map = new HashMap<>();
        Map<Integer, Character> map = new ConcurrentHashMap<>();

        Thread wthread = new Thread(() -> {
            System.out.println("写操作线程开始执行");
            for (int i = 0; i < 26; i++) {
                map.put(i, (char) ('a' + i));
            }
        });
        Thread rthread = new Thread(() -> {
            System.out.println("读操作线程开始执行");
            for (Integer key : map.keySet()) {
                System.out.println(key + " - " + map.get(key));
            }
        });
        wthread.start();
        rthread.start();
        Thread.sleep(1000);
    }
}
```

#### ConcurrentHashMap 的原理

> `ConcurrentHashMap` 一直在演进，尤其在 Java 1.7 和 Java 1.8，其数据结构和并发机制有很大的差异。

- Java 1.7
  - 数据结构：**数组＋单链表**
  - 并发机制：采用分段锁机制细化锁粒度，降低阻塞，从而提高并发性。
- Java 1.8
  - 数据结构：**数组＋单链表＋红黑树**
  - 并发机制：取消分段锁，之后基于 CAS + synchronized 实现。

##### Java 1.7 的实现

分段锁，是将内部进行分段（Segment），里面是 `HashEntry` 数组，和 `HashMap` 类似，哈希相同的条目也是以链表形式存放。
`HashEntry` 内部使用 `volatile` 的 `value` 字段来保证可见性，也利用了不可变对象的机制，以改进利用 `Unsafe` 提供的底层能力，比如 volatile access，去直接完成部分操作，以最优化性能，毕竟 `Unsafe` 中的很多操作都是 JVM intrinsic 优化过的。

![img](https://raw.githubusercontent.com/dunwu/images/dev/snap/20200605214405.png)

在进行并发写操作时，`ConcurrentHashMap` 会获取可重入锁（`ReentrantLock`），以保证数据一致性。所以，在并发修改期间，相应 `Segment` 是被锁定的。

```java
public class ConcurrentHashMap<K, V> extends AbstractMap<K, V>
        implements ConcurrentMap<K, V>, Serializable {

    // 将整个hashmap分成几个小的map，每个segment都是一个锁；与hashtable相比，这么设计的目的是对于put, remove等操作，可以减少并发冲突，对
    // 不属于同一个片段的节点可以并发操作，大大提高了性能
    final Segment<K,V>[] segments;

    // 本质上Segment类就是一个小的hashmap，里面table数组存储了各个节点的数据，继承了ReentrantLock, 可以作为互拆锁使用
    static final class Segment<K,V> extends ReentrantLock implements Serializable {
        transient volatile HashEntry<K,V>[] table;
        transient int count;
    }

    // 基本节点，存储Key， Value值
    static final class HashEntry<K,V> {
        final int hash;
        final K key;
        volatile V value;
        volatile HashEntry<K,V> next;
    }
}
```

##### Java 1.8 的实现

- 数据结构改进：与 HashMap 一样，将原先 **数组＋单链表** 的数据结构，变更为 **数组＋单链表＋红黑树** 的结构。当出现哈希冲突时，数据会存入数组指定桶的单链表，当链表长度达到 8，则将其转换为红黑树结构，这样其查询的时间复杂度可以降低到 $$O(logN)$$，以改进性能。
- 并发机制改进：
  - 取消 segments 字段，**直接采用 `transient volatile HashEntry<K,V>[] table` 保存数据，采用 table 数组元素作为锁，从而实现了对每一行数据进行加锁，进一步减少并发冲突的概率**。
  - 使用 CAS + `sychronized` 操作，在特定场景进行无锁并发操作。使用 Unsafe、LongAdder 之类底层手段，进行极端情况的优化。现代 JDK 中，synchronized 已经被不断优化，可以不再过分担心性能差异，另外，相比于 ReentrantLock，它可以减少内存消耗，这是个非常大的优势。

```java
final V putVal(K key, V value, boolean onlyIfAbsent) {
    if (key == null || value == null) throw new NullPointerException();
    int hash = spread(key.hashCode());
    int binCount = 0;
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; int n, i, fh;
        // 如果table为空，初始化；否则，根据hash值计算得到数组索引i，如果tab[i]为空，直接新建节点Node即可。注：tab[i]实质为链表或者红黑树的首节点。
        if (tab == null || (n = tab.length) == 0)
            tab = initTable();
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
            if (casTabAt(tab, i, null,
                         new Node<K,V>(hash, key, value, null)))
                break;                   // no lock when adding to empty bin
        }
        // 如果tab[i]不为空并且hash值为MOVED，说明该链表正在进行transfer操作，返回扩容完成后的table。
        else if ((fh = f.hash) == MOVED)
            tab = helpTransfer(tab, f);
        else {
            V oldVal = null;
            // 针对首个节点进行加锁操作，而不是segment，进一步减少线程冲突
            synchronized (f) {
                if (tabAt(tab, i) == f) {
                    if (fh >= 0) {
                        binCount = 1;
                        for (Node<K,V> e = f;; ++binCount) {
                            K ek;
                            // 如果在链表中找到值为key的节点e，直接设置e.val = value即可。
                            if (e.hash == hash &&
                                ((ek = e.key) == key ||
                                 (ek != null && key.equals(ek)))) {
                                oldVal = e.val;
                                if (!onlyIfAbsent)
                                    e.val = value;
                                break;
                            }
                            // 如果没有找到值为key的节点，直接新建Node并加入链表即可。
                            Node<K,V> pred = e;
                            if ((e = e.next) == null) {
                                pred.next = new Node<K,V>(hash, key,
                                                          value, null);
                                break;
                            }
                        }
                    }
                    // 如果首节点为TreeBin类型，说明为红黑树结构，执行putTreeVal操作。
                    else if (f instanceof TreeBin) {
                        Node<K,V> p;
                        binCount = 2;
                        if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                       value)) != null) {
                            oldVal = p.val;
                            if (!onlyIfAbsent)
                                p.val = value;
                        }
                    }
                }
            }
            if (binCount != 0) {
                // 如果节点数>＝8，那么转换链表结构为红黑树结构。
                if (binCount >= TREEIFY_THRESHOLD)
                    treeifyBin(tab, i);
                if (oldVal != null)
                    return oldVal;
                break;
            }
        }
    }
    // 计数增加1，有可能触发transfer操作(扩容)。
    addCount(1L, binCount);
    return null;
}
```

#### ConcurrentHashMap 的实战

> 示例摘自：[《Java 业务开发常见错误 100 例》](https://time.geekbang.org/column/intro/100047701)

##### ConcurrentHashMap 错误示例

```java
    //线程个数
    private static int THREAD_COUNT = 10;
    //总元素数量
    private static int ITEM_COUNT = 1000;

    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap<String, Long> concurrentHashMap = getData(ITEM_COUNT - 100);
        //初始900个元素
        System.out.println("init size:" + concurrentHashMap.size());
        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
        //使用线程池并发处理逻辑
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, 10).parallel().forEach(i -> {
            //查询还需要补充多少个元素
            int gap = ITEM_COUNT - concurrentHashMap.size();
            System.out.println("gap size:" + gap);
            //补充元素
            concurrentHashMap.putAll(getData(gap));
        }));
        //等待所有任务完成
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        //最后元素个数会是1000吗？
        System.out.println("finish size:" + concurrentHashMap.size());
    }

    private static ConcurrentHashMap<String, Long> getData(int count) {
        return LongStream.rangeClosed(1, count)
            .boxed()
            .collect(
                Collectors.toConcurrentMap(
                    i -> UUID.randomUUID().toString(),
                    i -> i,
                    (o1, o2) -> o1,
                    ConcurrentHashMap::new));
    }
```

初始大小 900 符合预期，还需要填充 100 个元素。

预期结果为 1000 个元素，实际大于 1000 个元素。

【分析】

ConcurrentHashMap 对外提供的方法或能力的限制：

- 使用了 ConcurrentHashMap，不代表对它的多个操作之间的状态是一致的，是没有其他线程在操作它的，如果需要确保需要手动加锁。
- 诸如 size、isEmpty 和 containsValue 等聚合方法，在并发情况下可能会反映 ConcurrentHashMap 的中间状态。因此在并发情况下，这些方法的返回值只能用作参考，而不能用于流程控制。显然，利用 size 方法计算差异值，是一个流程控制。
- 诸如 putAll 这样的聚合方法也不能确保原子性，在 putAll 的过程中去获取数据可能会获取到部分数据。

##### ConcurrentHashMap 错误示例修正 1.0 版

通过 synchronized 加锁，当然可以保证数据一致性，但是牺牲了 ConcurrentHashMap 的性能，没哟真正发挥出 ConcurrentHashMap 的特性。

```java
    //线程个数
    private static int THREAD_COUNT = 10;
    //总元素数量
    private static int ITEM_COUNT = 1000;

    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap<String, Long> concurrentHashMap = getData(ITEM_COUNT - 100);
        //初始900个元素
        System.out.println("init size:" + concurrentHashMap.size());
        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
        //使用线程池并发处理逻辑
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, 10).parallel().forEach(i -> {
            //查询还需要补充多少个元素
            synchronized (concurrentHashMap) {
                int gap = ITEM_COUNT - concurrentHashMap.size();
                System.out.println("gap size:" + gap);
                //补充元素
                concurrentHashMap.putAll(getData(gap));
            }
        }));
        //等待所有任务完成
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        //最后元素个数会是1000吗？
        System.out.println("finish size:" + concurrentHashMap.size());
    }

    private static ConcurrentHashMap<String, Long> getData(int count) {
        return LongStream.rangeClosed(1, count)
            .boxed()
            .collect(
                Collectors.toConcurrentMap(
                    i -> UUID.randomUUID().toString(),
                    i -> i,
                    (o1, o2) -> o1,
                    ConcurrentHashMap::new));
    }
```

##### ConcurrentHashMap 错误示例修正 2.0 版

```java
    //循环次数
    private static int LOOP_COUNT = 10000000;
    //线程个数
    private static int THREAD_COUNT = 10;
    //总元素数量
    private static int ITEM_COUNT = 1000;

    public static void main(String[] args) throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("normaluse");
        Map<String, Long> normaluse = normaluse();
        stopWatch.stop();
        Assert.isTrue(normaluse.size() == ITEM_COUNT, "normaluse size error");
        Assert.isTrue(normaluse.values().stream()
                .mapToLong(aLong -> aLong).reduce(0, Long::sum) == LOOP_COUNT
            , "normaluse count error");
        stopWatch.start("gooduse");
        Map<String, Long> gooduse = gooduse();
        stopWatch.stop();
        Assert.isTrue(gooduse.size() == ITEM_COUNT, "gooduse size error");
        Assert.isTrue(gooduse.values().stream()
                .mapToLong(l -> l)
                .reduce(0, Long::sum) == LOOP_COUNT
            , "gooduse count error");
        System.out.println(stopWatch.prettyPrint());
    }

    private static Map<String, Long> normaluse() throws InterruptedException {
        ConcurrentHashMap<String, Long> freqs = new ConcurrentHashMap<>(ITEM_COUNT);
        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, LOOP_COUNT).parallel().forEach(i -> {
                String key = "item" + ThreadLocalRandom.current().nextInt(ITEM_COUNT);
                synchronized (freqs) {
                    if (freqs.containsKey(key)) {
                        freqs.put(key, freqs.get(key) + 1);
                    } else {
                        freqs.put(key, 1L);
                    }
                }
            }
        ));
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        return freqs;
    }

    private static Map<String, Long> gooduse() throws InterruptedException {
        ConcurrentHashMap<String, LongAdder> freqs = new ConcurrentHashMap<>(ITEM_COUNT);
        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, LOOP_COUNT).parallel().forEach(i -> {
                String key = "item" + ThreadLocalRandom.current().nextInt(ITEM_COUNT);
                freqs.computeIfAbsent(key, k -> new LongAdder()).increment();
            }
        ));
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        return freqs.entrySet().stream()
            .collect(Collectors.toMap(
                e -> e.getKey(),
                e -> e.getValue().longValue())
            );
    }
```

## List

### CopyOnWriteArrayList



> 读写锁： 读读共享，其他都互斥，写写互斥，读写互斥，写读互斥
>
> CopyOnWrite升级，读取完全不加锁，写入不会阻塞读取操作。只有写写才同步等待



`CopyOnWriteArrayList` 是线程安全的 `ArrayList`。`CopyOnWrite` 字面意思为**写的时候会将共享变量新复制一份**出来。复制的好处在于**读操作是无锁的**（也就是无阻塞）。

CopyOnWriteArrayList **仅适用于写操作非常少的场景**，而且能够容忍读写的短暂不一致。如果读写比例均衡或者有大量写操作的话，使用 CopyOnWriteArrayList 的性能会非常糟糕。

#### CopyOnWriteArrayList 原理

CopyOnWriteArrayList 内部维护了一个数组，成员变量 array 就指向这个内部数组，所有的读操作都是基于 array 进行的，如下图所示，迭代器 Iterator 遍历的就是 array 数组。

![img](https://raw.githubusercontent.com/dunwu/images/dev/snap/20200702204541.png)

- lock - 执行写时复制操作，需要使用可重入锁加锁
- array - 对象数组，用于存放元素

```java
    /** The lock protecting all mutators */
    final transient ReentrantLock lock = new ReentrantLock();

    /** The array, accessed only via getArray/setArray. */
    private transient volatile Object[] array;
```

![img](https://raw.githubusercontent.com/dunwu/images/dev/cs/java/javacore/container/CopyOnWriteArrayList.png)

（1）读操作

在 `CopyOnWriteAarrayList` 中，读操作不同步，因为它们在内部数组的快照上工作，所以多个迭代器可以同时遍历而不会相互阻塞（图 1,2,4）。

CopyOnWriteArrayList 的读操作是不用加锁的，性能很高。

```java
public E get(int index) {
    return get(getArray(), index);
}
private E get(Object[] a, int index) {
    return (E) a[index];
}
```

（2）写操作

所有的写操作都是同步的。他们在备份数组（图 3）的副本上工作。写操作完成后，后备阵列将被替换为复制的阵列，并释放锁定。支持数组变得易变，所以替换数组的调用是原子（图 5）。

写操作后创建的迭代器将能够看到修改的结构（图 6,7）。

写时复制集合返回的迭代器不会抛出 `ConcurrentModificationException`，因为它们在数组的快照上工作，并且无论后续的修改（2,4）如何，都会像迭代器创建时那样完全返回元素。

**添加操作** - 添加的逻辑很简单，先将原容器 copy 一份，然后在新副本上执行写操作，之后再切换引用。当然此过程是要加锁的。

```java
public boolean add(E e) {
    //ReentrantLock加锁，保证线程安全
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        Object[] elements = getArray();
        int len = elements.length;
        //拷贝原容器，长度为原容器长度加一
        Object[] newElements = Arrays.copyOf(elements, len + 1);
        //在新副本上执行添加操作
        newElements[len] = e;
        //将原容器引用指向新副本
        setArray(newElements);
        return true;
    } finally {
        //解锁
        lock.unlock();
    }
}
```

**删除操作** - 删除操作同理，将除要删除元素之外的其他元素拷贝到新副本中，然后切换引用，将原容器引用指向新副本。同属写操作，需要加锁。

```java
public E remove(int index) {
    //加锁
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        Object[] elements = getArray();
        int len = elements.length;
        E oldValue = get(elements, index);
        int numMoved = len - index - 1;
        if (numMoved == 0)
            //如果要删除的是列表末端数据，拷贝前len-1个数据到新副本上，再切换引用
            setArray(Arrays.copyOf(elements, len - 1));
        else {
            //否则，将除要删除元素之外的其他元素拷贝到新副本中，并切换引用
            Object[] newElements = new Object[len - 1];
            System.arraycopy(elements, 0, newElements, 0, index);
            System.arraycopy(elements, index + 1, newElements, index,
                              numMoved);
            setArray(newElements);
        }
        return oldValue;
    } finally {
        //解锁
        lock.unlock();
    }
}
```

#### CopyOnWriteArrayList 示例

```java
public class CopyOnWriteArrayListDemo {

    static class ReadTask implements Runnable {

        List<String> list;

        ReadTask(List<String> list) {
            this.list = list;
        }

        public void run() {
            for (String str : list) {
                System.out.println(str);
            }
        }
    }

    static class WriteTask implements Runnable {

        List<String> list;
        int index;

        WriteTask(List<String> list, int index) {
            this.list = list;
            this.index = index;
        }

        public void run() {
            list.remove(index);
            list.add(index, "write_" + index);
        }
    }

    public void run() {
        final int NUM = 10;
        // ArrayList 在并发迭代访问时会抛出 ConcurrentModificationException 异常
        // List<String> list = new ArrayList<>();
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        for (int i = 0; i < NUM; i++) {
            list.add("main_" + i);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(NUM);
        for (int i = 0; i < NUM; i++) {
            executorService.execute(new ReadTask(list));
            executorService.execute(new WriteTask(list, i));
        }
        executorService.shutdown();
    }

    public static void main(String[] args) {
        new CopyOnWriteArrayListDemo().run();
    }
}
```

#### CopyOnWriteArrayList 实战

```java
@Slf4j
public class WrongCopyOnWriteList {

    public static void main(String[] args) {
        testRead();
        testWrite();
    }

    public static Map testWrite() {
        List<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        List<Integer> synchronizedList = Collections.synchronizedList(new ArrayList<>());
        StopWatch stopWatch = new StopWatch();
        int loopCount = 100000;
        stopWatch.start("Write:copyOnWriteArrayList");
        IntStream.rangeClosed(1, loopCount)
            .parallel()
            .forEach(__ -> copyOnWriteArrayList.add(ThreadLocalRandom.current().nextInt(loopCount)));
        stopWatch.stop();
        stopWatch.start("Write:synchronizedList");
        IntStream.rangeClosed(1, loopCount)
            .parallel()
            .forEach(__ -> synchronizedList.add(ThreadLocalRandom.current().nextInt(loopCount)));
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        Map result = new HashMap();
        result.put("copyOnWriteArrayList", copyOnWriteArrayList.size());
        result.put("synchronizedList", synchronizedList.size());
        return result;
    }

    private static void addAll(List<Integer> list) {
        list.addAll(IntStream.rangeClosed(1, 1000000).boxed().collect(Collectors.toList()));
    }

    public static Map testRead() {
        List<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        List<Integer> synchronizedList = Collections.synchronizedList(new ArrayList<>());
        addAll(copyOnWriteArrayList);
        addAll(synchronizedList);
        StopWatch stopWatch = new StopWatch();
        int loopCount = 1000000;
        int count = copyOnWriteArrayList.size();
        stopWatch.start("Read:copyOnWriteArrayList");
        IntStream.rangeClosed(1, loopCount)
            .parallel()
            .forEach(__ -> copyOnWriteArrayList.get(ThreadLocalRandom.current().nextInt(count)));
        stopWatch.stop();
        stopWatch.start("Read:synchronizedList");
        IntStream.range(0, loopCount)
            .parallel()
            .forEach(__ -> synchronizedList.get(ThreadLocalRandom.current().nextInt(count)));
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
        Map result = new HashMap();
        result.put("copyOnWriteArrayList", copyOnWriteArrayList.size());
        result.put("synchronizedList", synchronizedList.size());
        return result;
    }

}
```

读性能差不多是写性能的一百倍。

## Set

Set 接口的两个实现是 CopyOnWriteArraySet 和 ConcurrentSkipListSet，使用场景可以参考前面讲述的 CopyOnWriteArrayList 和 ConcurrentSkipListMap，它们的原理都是一样的。

## Queue

Java 并发包里面 Queue 这类并发容器是最复杂的，你可以从以下两个维度来分类。一个维度是**阻塞与非阻塞**，所谓阻塞指的是：**当队列已满时，入队操作阻塞；当队列已空时，出队操作阻塞**。另一个维度是**单端与双端**，单端指的是只能队尾入队，队首出队；而双端指的是队首队尾皆可入队出队。Java 并发包里**阻塞队列都用 Blocking 关键字标识，单端队列使用 Queue 标识，双端队列使用 Deque 标识**。

### BlockingQueue

`BlockingQueue` 顾名思义，是一个**阻塞队列**。**`BlockingQueue` 基本都是基于锁实现**。在 `BlockingQueue` 中，**当队列已满时，入队操作阻塞；当队列已空时，出队操作阻塞**。

`BlockingQueue` 接口定义如下：

```java
public interface BlockingQueue<E> extends Queue<E> {}
```

核心 API：

```java
// 获取并移除队列头结点，如果必要，其会等待直到队列出现元素
E take() throws InterruptedException;
// 插入元素，如果队列已满，则等待直到队列出现空闲空间
void put(E e) throws InterruptedException;
```

`BlockingQueue` 对插入操作、移除操作、获取元素操作提供了四种不同的方法用于不同的场景中使用：

- 抛出异常；
- 返回特殊值（`null` 或 `true`/`false`，取决于具体的操作）；
- 阻塞等待此操作，一直阻塞；
- 阻塞等待此操作，直到成功，超时退出。

总结如下：

| 编号 | 处理方式   | 插入方法                    | 移除方法                   | 检查方法  |
| ---- | ---------- | --------------------------- | -------------------------- | --------- |
| 1    | 抛出异常   | add(e)                      | remove(e)                  | element() |
| 2    | 返回特殊值 | offer(e)                    | poll()                     | peek()    |
| 3    | 一直阻塞   | put(e)                      | take(e)                    | 无        |
| 4    | 超时退出   | offer(e, timeout, timeunit) | poll(e, timeout, timeunit) | 无        |

  四种方式解释：

- 抛出异常
  - add方法：插入数据时，如果队列已满，则抛出IllegalStateException异常，否则返回true。
  - remove方法：移除数据时，如果队列存在此元素，则删除成功，返回true，否则返回false。如果存在多个元素，则仅移除一个元素并返回true。需要注意：remove(e)是BlockingQueue接口的方法，remove()是Queue接口的方法。
  - element方法：检查元素时，如果队列为空，则抛出NoSuchElementException异常，否则返回队列头部元素。element()是Queue接口的方法。
- 返回特殊值
  - offer方法：插入数据时，如果队列未满，则插入成功返回true，否则返回false。当使用有界队列时，建议使用offer方法。
  - poll方法：移除数据时，如果队列不为空，则移除队列头部元素并返回，否则，返回null。poll()是Queue接口的方法。
  - peek方法：检查元素时，如果队列为空，则返回null，否则返回队列头部元素。peek()是Queue接口的方法。
- 一直阻塞
  - put方法：插入数据时，如果队列为空，则插入成功，否则进行阻塞等待队列可用，若等待时被中断，则抛出InterruptedException异常。
  - take方法：检查元素时，如果队列不为空，则返回队列头部元素，否则进行阻塞等待队列插入数据，若等待时被中断，则抛出InterruptedException异常。
- 超时退出
  - offer方法：插入数据时，如果队列为空，则插入成功返回true，否则进行阻塞等待队列可用，若等待时被中断，则抛出InterruptedException异常，若在指定时间内仍然不可用，则返回false。
  - poll方法：检查元素时，如果队列不为空，则返回队列头部元素，否则进行阻塞等待队列插入数据，若等待时被中断，则抛出InterruptedException异常，若在指定时间内仍然不可用，则返回null。
      注意：Queue队列不能插入null，否则会抛出NullPointerException异常。





`BlockingQueue` 的各个实现类都遵循了这些规则。

`BlockingQueue` 不接受 `null` 值元素。

JDK 提供了以下阻塞队列：

- `ArrayBlockingQueue` - 一个由**数组结构组成的有界阻塞队列**。
- `LinkedBlockingQueue` - 一个由**链表结构组成的有界阻塞队列**。
- `PriorityBlockingQueue` - 一个**支持优先级排序的无界阻塞队列**。
- `SynchronousQueue` - 一个**不存储元素的阻塞队列**。
- `DelayQueue` - 一个使用优先级队列实现的无界阻塞队列。
- `LinkedTransferQueue` - 一个**由链表结构组成的无界阻塞队列**。

`BlockingQueue` 基本都是基于锁实现。

### PriorityBlockingQueue 类

`PriorityBlockingQueue` 类定义如下：

```java
public class PriorityBlockingQueue<E> extends AbstractQueue<E>
    implements BlockingQueue<E>, java.io.Serializable {}
```

#### PriorityBlockingQueue 要点

- `PriorityBlockingQueue` 可以视为 `PriorityQueue` **的线程安全版本。**
- `PriorityBlockingQueue` 实现了 `BlockingQueue`，**也是一个阻塞队列。**
- `PriorityBlockingQueue` 实现了 `Serializable`，支持序列化。
- `PriorityBlockingQueue` 不接受 `null` 值元素。
- `PriorityBlockingQueue` 的**插入操作 `put` 方法不会 block**，**因为它是无界队列（take 方法在队列为空的时候会阻塞）。**

#### PriorityBlockingQueue 原理

`PriorityBlockingQueue` 有两个重要成员：

```java
private transient Object[] queue;
private final ReentrantLock lock;
```

- `queue` 是一个 `Object` 数组，用于保存 `PriorityBlockingQueue` 的元素。
- 而可重入锁 `lock` 则用于在执行插入、删除操作时，保证这个方法在当前线程释放锁之前，其他线程不能访问。

`PriorityBlockingQueue` 的容量**虽然有初始化大小，但是不限制大小，**如果当前容量已满，插入新元素时会自动扩容。

### ArrayBlockingQueue 类

`ArrayBlockingQueue` 是由数组结构组成的**有界阻塞队列**。

#### ArrayBlockingQueue 要点

`ArrayBlockingQueue` 类定义如下：

```java
public class ArrayBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {
    // 数组的大小就决定了队列的边界，所以初始化时必须指定容量
    public ArrayBlockingQueue(int capacity) { //... }
    public ArrayBlockingQueue(int capacity, boolean fair) { //... }
    public ArrayBlockingQueue(int capacity, boolean fair, Collection<? extends E> c) { //... }
}
```

说明：

- `ArrayBlockingQueue` 实现了 `BlockingQueue`，也是一个阻塞队列。
- `ArrayBlockingQueue` 实现了 `Serializable`，支持序列化。
- `ArrayBlockingQueue` 是基于数组实现的有界阻塞队列。所以初始化时必须指定容量。

#### ArrayBlockingQueue 原理

`ArrayBlockingQueue` 的重要成员如下：

```java
// 用于存放元素的数组
final Object[] items;
// 下一次读取操作的位置
int takeIndex;
// 下一次写入操作的位置
int putIndex;
// 队列中的元素数量
int count;

// 以下几个就是控制并发用的同步器
final ReentrantLock lock;
private final Condition notEmpty;
private final Condition notFull;
```

`ArrayBlockingQueue` 内部以 `final` 的数组保存数据，数组的大小就决定了队列的边界。

`ArrayBlockingQueue` 实现并发同步的原理就是，读操作和写操作都需要获取到 AQS 独占锁才能进行操作。

- 如果队列为空，这个时候读操作的线程进入到读线程队列排队，等待写线程写入新的元素，然后唤醒读线程队列的第一个等待线程。
- 如果队列已满，这个时候写操作的线程进入到写线程队列排队，等待读线程将队列元素移除，然后唤醒写线程队列的第一个等待线程。

对于 `ArrayBlockingQueue`，我们可以在构造的时候指定以下三个参数：

- 队列容量，其限制了队列中最多允许的元素个数；
- 指定独占锁是公平锁还是非公平锁。非公平锁的吞吐量比较高，公平锁可以保证每次都是等待最久的线程获取到锁；
- 可以指定用一个集合来初始化，将此集合中的元素在构造方法期间就先添加到队列中。

### LinkedBlockingQueue 类

`LinkedBlockingQueue` 是由链表结构组成的有界阻塞队列。容易被误解为无边界，但其实其行为和内部代码都是基于有界的逻辑实现的，只不过如果我们没有在创建队列时就指定容量，那么其容量限制就自动被设置为 `Integer.MAX_VALUE`，成为了无界队列。

#### LinkedBlockingQueue 要点

`LinkedBlockingQueue` 类定义如下：

```java
public class LinkedBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {}
```

- `LinkedBlockingQueue` 实现了 `BlockingQueue`，也是一个阻塞队列。
- `LinkedBlockingQueue` 实现了 `Serializable`，支持序列化。
- `LinkedBlockingQueue` 是基于单链表实现的阻塞队列，可以当做无界队列也可以当做有界队列来使用。
- `LinkedBlockingQueue` 中元素按照插入顺序保存（FIFO）。

#### LinkedBlockingQueue 原理

`LinkedBlockingQueue` 中的重要数据结构：

```java
// 队列容量
private final int capacity;
// 队列中的元素数量
private final AtomicInteger count = new AtomicInteger(0);
// 队头
private transient Node<E> head;
// 队尾
private transient Node<E> last;

// take, poll, peek 等读操作的方法需要获取到这个锁
private final ReentrantLock takeLock = new ReentrantLock();
// 如果读操作的时候队列是空的，那么等待 notEmpty 条件
private final Condition notEmpty = takeLock.newCondition();
// put, offer 等写操作的方法需要获取到这个锁
private final ReentrantLock putLock = new ReentrantLock();
// 如果写操作的时候队列是满的，那么等待 notFull 条件
private final Condition notFull = putLock.newCondition();
```

这里用了两对 `Lock` 和 `Condition`，简单介绍如下：

- `takeLock` 和 `notEmpty` 搭配：如果要获取（take）一个元素，需要获取 `takeLock` 锁，但是获取了锁还不够，如果队列此时为空，还需要队列不为空（`notEmpty`）这个条件（`Condition`）。
- `putLock` 需要和 `notFull` 搭配：如果要插入（put）一个元素，需要获取 `putLock` 锁，但是获取了锁还不够，如果队列此时已满，还需要队列不是满的（notFull）这个条件（`Condition`）。

### SynchronousQueue 类

SynchronousQueue 是**不存储元素的阻塞队列**。每个删除操作都要等待插入操作，反之每个插入操作也都要等待删除动作。那么这个队列的容量是多少呢？是 1 吗？**其实不是的，其内部容量是 0。**

`SynchronousQueue` 定义如下：

```java
public class SynchronousQueue<E> extends AbstractQueue<E>
    implements BlockingQueue<E>, java.io.Serializable {}
```

`SynchronousQueue` 这个类，在线程池的实现类 `ScheduledThreadPoolExecutor` 中得到了应用。

`SynchronousQueue` 的队列其实是虚的，即队列容量为 0。数据必须从某个写线程交给某个读线程，而不是写到某个队列中等待被消费。

`SynchronousQueue` 中不能使用 peek 方法（在这里这个方法直接返回 null），peek 方法的语义是只读取不移除，显然，这个方法的语义是不符合 SynchronousQueue 的特征的。

`SynchronousQueue` 也不能被迭代，因为根本就没有元素可以拿来迭代的。

虽然 `SynchronousQueue` 间接地实现了 Collection 接口，但是如果你将其当做 Collection 来用的话，那么集合是空的。

当然，`SynchronousQueue` 也不允许传递 null 值的（并发包中的容器类好像都不支持插入 null 值，因为 null 值往往用作其他用途，比如用于方法的返回值代表操作失败）。

### ConcurrentLinkedDeque 类

`Deque` 的侧重点是支持对队列头尾都进行插入和删除，所以提供了特定的方法，如:

- 尾部插入时需要的 `addLast(e)`、`offerLast(e)`。
- 尾部删除所需要的 `removeLast()`、`pollLast()`。

### Queue 的并发应用

Queue 被广泛使用在生产者 - 消费者场景。而在并发场景，利用 `BlockingQueue` 的阻塞机制，可以减少很多并发协调工作。

这么多并发 Queue 的实现，如何选择呢？

- 考虑应用场景中对队列边界的要求。`ArrayBlockingQueue` 是有明确的容量限制的，而 `LinkedBlockingQueue` 则取决于我们是否在创建时指定，`SynchronousQueue` 则干脆不能缓存任何元素。
- 从空间利用角度，数组结构的 `ArrayBlockingQueue` 要比 `LinkedBlockingQueue` 紧凑，因为其不需要创建所谓节点，但是其初始分配阶段就需要一段连续的空间，所以初始内存需求更大。
- 通用场景中，`LinkedBlockingQueue` 的吞吐量一般优于 `ArrayBlockingQueue`，因为它实现了更加细粒度的锁操作。
- `ArrayBlockingQueue` 实现比较简单，性能更好预测，属于表现稳定的“选手”。
- 可能令人意外的是，很多时候 `SynchronousQueue` 的性能表现，往往大大超过其他实现，尤其是在队列元素较小的场景。

---
title: Java并发工具类
date: 2019-12-24 23:52:25
categories:
  - Java
  - JavaSE
  - 并发
tags:
  - Java
  - JavaSE
  - 并发
permalink: /pages/02d274/
---

# Java 控制并发流程并发工具类

> JDK 的 `java.util.concurrent` 包（即 J.U.C）中提供了几个非常有用的并发工具类。

## CountDownLatch - 门闩 :key:

> 字面意思为 **递减计数锁**。用于**控制一个线程等待多个线程**。
>
> `CountDownLatch` 维护一个计数器 count，表示需要等待的事件数量。`countDown` 方法递减计数器，表示有一个事件已经发生。调用 `await` 方法的线程会一直阻塞直到计数器为零，或者等待中的线程中断，或者等待超时。
>
> 比如： 拼多多人满了才发车

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/CountDownLatch.png)

`CountDownLatch` 是基于 AQS(`AbstractQueuedSynchronizer`) 实现的。

`CountDownLatch` 唯一的构造方法：

```java
// 初始化计数器
public CountDownLatch(int count) {};
```

说明：

- count 为统计值。

`CountDownLatch` 的重要方法：

```java
public void await() throws InterruptedException { };
public boolean await(long timeout, TimeUnit unit) throws InterruptedException { };
public void countDown() { };
```

说明：

- `await()` - 调用 `await()` 方法的线程会被挂起，它会等待直到 count 值为 0 才继续执行。
- `await(long timeout, TimeUnit unit)` - 和 `await()` 类似，只不过等待一定的时间后 count 值还没变为 0 的话就会继续执行
- `countDown()` - 将统计值 count 减 1

示例：

```java
public class CountDownLatchDemo {

    public static void main(String[] args) {
        final CountDownLatch latch = new CountDownLatch(2);

        new Thread(new MyThread(latch)).start();
        new Thread(new MyThread(latch)).start();

        try {
            System.out.println("等待2个子线程执行完毕...");
            latch.await();
            System.out.println("2个子线程已经执行完毕");
            System.out.println("继续执行主线程");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class MyThread implements Runnable {

        private CountDownLatch latch;

        public MyThread(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            System.out.println("子线程" + Thread.currentThread().getName() + "正在执行");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("子线程" + Thread.currentThread().getName() + "执行完毕");
            latch.countDown();
        }

    }

}
```

## CyclicBarrier - 循环栅栏 :barber:

> 字面意思是 **循环栅栏**。**`CyclicBarrier` 可以让一组线程等待至某个状态（遵循字面意思，不妨称这个状态为栅栏）之后再全部同时执行**。之所以叫循环栅栏是因为：**当所有等待线程都被释放以后，`CyclicBarrier` 可以被重用**。
>
> `CyclicBarrier` 维护一个计数器 count。每次执行 `await` 方法之后，count 加 1，直到计数器的值和设置的值相等，等待的所有线程才会继续执行。

`CyclicBarrier` 是基于 `ReentrantLock` 和 `Condition` 实现的。Condition就是每一个线程才能起作用

`CyclicBarrier` 应用场景：`CyclicBarrier` 在并行迭代算法中非常有用。

![img](https://raw.githubusercontent.com/dunwu/images/dev/cs/java/javacore/concurrent/CyclicBarrier.png)

`CyclicBarrier` 提供了 2 个构造方法

```java
public CyclicBarrier(int parties) {}
public CyclicBarrier(int parties, Runnable barrierAction) {}
```

> 说明：
>
> - `parties` - `parties` 数相当于一个阈值，当有 `parties` 数量的线程在等待时， `CyclicBarrier` 处于栅栏状态。
> - `barrierAction` - 当 `CyclicBarrier` 处于栅栏状态时执行的动作。

`CyclicBarrier` 的重要方法：

```java
public int await() throws InterruptedException, BrokenBarrierException {}
public int await(long timeout, TimeUnit unit)
        throws InterruptedException,
               BrokenBarrierException,
               TimeoutException {}
// 将屏障重置为初始状态
public void reset() {}
```

> 说明：
>
> - `await()` - 等待调用 `await()` 的线程数达到屏障数。如果当前线程是最后一个到达的线程，并且在构造函数中提供了非空屏障操作，则当前线程在允许其他线程继续之前运行该操作。如果在屏障动作期间发生异常，那么该异常将在当前线程中传播并且屏障被置于断开状态。
> - `await(long timeout, TimeUnit unit)` - 相比于 `await()` 方法，这个方法让这些线程等待至一定的时间，如果还有线程没有到达栅栏状态就直接让到达栅栏状态的线程执行后续任务。
> - `reset()` - 将屏障重置为初始状态。

示例：

```java
public class CyclicBarrierDemo {

    final static int N = 4;

    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(N,
            new Runnable() {
                @Override
                public void run() {
                    System.out.println("当前线程" + Thread.currentThread().getName());
                }
            });

        for (int i = 0; i < N; i++) {
            MyThread myThread = new MyThread(barrier);
            new Thread(myThread).start();
        }
    }

    static class MyThread implements Runnable {

        private CyclicBarrier cyclicBarrier;

        MyThread(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            System.out.println("线程" + Thread.currentThread().getName() + "正在写入数据...");
            try {
                Thread.sleep(3000); // 以睡眠来模拟写入数据操作
                System.out.println("线程" + Thread.currentThread().getName() + "写入数据完毕，等待其他线程写入完毕");
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

    }

}
```



```sh
线程Thread-0正在写入数据...
线程Thread-3正在写入数据...
线程Thread-3写入数据完毕，等待其他线程写入完毕
线程Thread-0写入数据完毕，等待其他线程写入完毕
线程Thread-2写入数据完毕，等待其他线程写入完毕
线程Thread-1写入数据完毕，等待其他线程写入完毕
当前线程Thread-1
```



```java
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class TestCyclicBarrier {
    //编写一个四个人集合了就出发的代码
    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(4, new Runnable() {
            @Override
            public void run() {
                System.out.println("四个人到了，可以出发!!");
            }
        });
        for (int i = 0; i < 8; i++) {
           new Thread(new Tast(i,barrier)).start();
        }
        System.out.println("结束");
    }
    static class Tast implements Runnable{
        public Tast(int i, CyclicBarrier barrier) {
            this.i = i;
            this.barrier = barrier;
        }

        private int i;
        private CyclicBarrier barrier;
        @Override
        public void run() {
            System.out.println("线程"+i+"现在出发");
            try{
                Thread.sleep((long) (Math.random()*5000));
                System.out.println("线程"+i+"到达集合地点!!!");
                barrier.await();
            }catch (InterruptedException e){
                e.printStackTrace();
            }catch (BrokenBarrierException e){
                e.printStackTrace();
            }
        }
    }
}

```



```sh
线程1现在出发
线程0现在出发
线程2现在出发
线程3现在出发
线程6现在出发
线程7现在出发
线程5现在出发
结束
线程4现在出发
线程0到达集合地点!!!
线程4到达集合地点!!!
线程5到达集合地点!!!
线程1到达集合地点!!!
四个人到了，可以出发!!
线程7到达集合地点!!!
线程2到达集合地点!!!
线程6到达集合地点!!!
线程3到达集合地点!!!
四个人到了，可以出发!!
```





## Semaphore - 信号量 许可证 :passport_control:

> 字面意思为 **信号量**。`Semaphore` 用来控制某段代码块的并发数。
>
> `Semaphore` 管理着一组虚拟的许可（permit），permit 的初始数量可通过构造方法来指定。每次执行 `acquire` 方法可以获取一个 permit，如果没有就等待；而 `release` 方法可以释放一个 permit。

`Semaphore` 应用场景：

- `Semaphore` 可以用于实现资源池，如数据库连接池。
- `Semaphore` 可以用于将任何一种容器变成有界阻塞容器。

![img](https://raw.githubusercontent.com/dunwu/images/dev/cs/java/javacore/concurrent/Semaphore.png)

`Semaphore` 提供了 2 个构造方法：

```java
// 参数 permits 表示许可数目，即同时可以允许多少线程进行访问
public Semaphore(int permits) {}
// 参数 fair 表示是否是公平的，即等待时间越久的越先获取许可
public Semaphore(int permits, boolean fair) {}
```

> 说明：
>
> - `permits` - 初始化固定数量的 permit，并且默认为非公平模式。
> - `fair` - 设置是否为公平模式。所谓公平，是指等待久的优先获取 permit。

`Semaphore`的重要方法：

```java
// 获取 1 个许可
public void acquire() throws InterruptedException {}
//获取 permits 个许可
public void acquire(int permits) throws InterruptedException {}
// 释放 1 个许可
public void release() {}
//释放 permits 个许可
public void release(int permits) {}
```

说明：

- `acquire()` - 获取 1 个 permit。
- `acquire(int permits)` - 获取 permits 数量的 permit。
- `release()` - 释放 1 个 permit。
- `release(int permits)` - 释放 permits 数量的 permit。

示例：

```java
public class SemaphoreDemo {

    private static final int THREAD_COUNT = 30;

    private static ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);

    private static Semaphore semaphore = new Semaphore(10);

    public static void main(String[] args) {
        for (int i = 0; i < THREAD_COUNT; i++) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        semaphore.acquire();
                        System.out.println("save data");
                        semaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        threadPool.shutdown();
    }

}
```

## Condition

对于任意一个java对象，它都拥有一组定义在java.lang.Object上监视器方法，包括wait()，wait(long timeout)，notify()，notifyAll()，这些方法配合synchronized关键字一起使用可以实现等待/通知模式。同样，Condition接口也提供了类似Object监视器的方法，通过与Lock配合来实现等待/通知模式。可以看一下Object类的监视器方法和Condition接口的对比：

![在这里插入图片描述](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/da7f2e6e6158411a80c61a59ce60def5~tplv-k3u1fbpfcp-zoom-in-crop-mark:4536:0:0:0.awebp)

### Condition解决生产者消费者问题

假设生产者可以生产票，但是现存的票只能有一张，只有顾客买走了才能再生产一张票，因此可以用Condition来保证同步。havenum表示有票，需要生产者等待；nonum表示没票，需要消费者等待。代码如下：

```java
class tickets{
    private int num = 0;
    ReentrantLock lock = new ReentrantLock();

    Condition nonum = lock.newCondition();
    Condition havenum = lock.newCondition();


    public void put() throws InterruptedException {
        lock.lock();
        try{
            while(num==1)
            {
                nonum.await();
            }
            num++;
            System.out.println(Thread.currentThread().getName()+" 生产了一份,现存数量是 "+num);
            havenum.signalAll();
        }
        finally {
            lock.unlock();
        }
    }

    public void take() throws InterruptedException {
        lock.lock();
        try {
            while(num==0)
            {
                havenum.await();
            }
            num--;
            System.out.println(Thread.currentThread().getName()+" 消耗了一份,现存数量是 "+num);
            nonum.signalAll();
        }
        finally {
            lock.unlock();
        }
    }
}
```

![在这里插入图片描述](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/84227caf892c447aa05a3833948f940e~tplv-k3u1fbpfcp-zoom-in-crop-mark:4536:0:0:0.awebp)

### Condition  精准唤醒

通过上面的例子我们也可以发现使用不同的Condition对象可以唤醒不同的线程，使用这一机制就可以做到精准唤醒。

```java
class Aweaken
{
    ReentrantLock lock = new ReentrantLock();
    int num = 1;
    Condition conditionA = lock.newCondition();
    Condition conditionB = lock.newCondition();
    Condition conditionC = lock.newCondition();

    public void weakA()
    {
        lock.lock();
        try {
            while(num != 1)
            {
                conditionA.await();
            }
            num = 2;
            System.out.println("现在是线程 "+Thread.currentThread().getName()+", 下一个应该是线程B");
            conditionB.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void weakB()
    {
        lock.lock();
        try {
            while(num != 2)
            {
                conditionB.await();
            }
            num = 3;
            System.out.println("现在是线程 "+Thread.currentThread().getName()+", 下一个应该是线程C");
            conditionC.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void weakC()
    {
        lock.lock();
        try {
            while(num != 3)
            {
                conditionC.await();
            }
            num = 1;
            System.out.println("现在是线程 "+Thread.currentThread().getName()+", 下一个应该是线程A");
            conditionA.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

public class PCold {

    public static void main(String[] args) {
        Aweaken b = new Aweaken();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                b.weakA();
            }
        },"A").start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                b.weakB();
            }
        },"B").start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                b.weakC();
            }
        },"C").start();

    }
}
```

### Condition 实现分析

#### 等待队列

ConditionObject的等待队列是一个FIFO队列，队列的每个节点都是等待在Condition对象上的线程的引用，该线程就是在Condition对象上等待的线程，如果一个线程调用了Condition.await()，那么该线程就会释放锁，构成节点加入等待队列并进入等待状态。

从下图可以看出来Condition拥有首尾节点的引用，而新增节点只需要将原有的尾节点nextWaiter指向它，并更新尾节点即可。**上述节点引用更新过程没有使用CAS机制，因为在调用await()的线程必定是获取了锁的线程，该过程由锁保证线程的安全。**

一个Lock（同步器）拥有一个同步队列和多喝等待队列（如下图所示） ![在这里插入图片描述](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/c0a627da483f48d4ac6e237513648181~tplv-k3u1fbpfcp-zoom-in-crop-mark:4536:0:0:0.awebp)

#### 等待

调用Condition的await()方法，会使得当前线程进入等待队列并释放锁，同时线程状态变为等待状态。当从await()返回时，当前线程一定是获取了Condition相关联的锁。

线程触发await()这个过程可以看作是同步队列的首节点（当前线程肯定是成功获得了锁，因此一定是在同步队列的首节点）移动到了Condition的等待队列的尾节点，并释放同步状态进入等待状态，同时会唤醒同步队列的后继节点。

![在这里插入图片描述](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/9b476a858a6746a1a57f439e8da7eeb0~tplv-k3u1fbpfcp-zoom-in-crop-mark:4536:0:0:0.awebp)

#### 唤醒

调用Condition的signal()方法将会唤醒再等待队列中的首节点，该节点也是到目前为止等待时间最长的节点。调用Condition的signalAll()方法，将等待队列中的所有节点全部唤醒，相当于将等待队列中的每一个节点都执行一次signal()。

![在这里插入图片描述](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/78b7e8019109447b96d4f16c09d534b8~tplv-k3u1fbpfcp-zoom-in-crop-mark:4536:0:0:0.awebp)





