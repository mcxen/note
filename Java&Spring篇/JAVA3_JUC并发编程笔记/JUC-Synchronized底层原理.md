## Synchronized 底层原理

下载JRE的源码，也就是hospot的源码，直接进入`openjdk:src/share/vm/runtime `目录, 这个目录存放的是hotspot虚拟机在运行时所需的代码。

可以直接锁定其中的 `objectMonitor.cpp`源文件和`objectMonitor.hpp`头文件. 看到这2个文件，相信各位同学应该就知道，这个就是`synchronized`锁对象的`monitor`，它也是 一个对象，不过它是一个c++对象(见:objectMonitor.hpp头文件):

这个下面是这个对象的构造函数：

![image-20240327162252415](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20240327162252415.png)

> 等待别人释放锁的线程进入 cxq
>
> 等待别人释放完了，大家去抢，抢不到的进入EntryList
>
> 

![image-20200713224950456](https://raw.githubusercontent.com/zicair/MyBlog/master/picbed/synchronized底层monitor原理/image-20200713224950456.png)

###  2.monitor竞争

1. 执行monitorenter时，会调用InterpreterRuntime.cpp (src/share/vm/interpreter/interpreterRuntime.cpp) 的 InterpreterRuntime::monitorenter函数。具体代码可参见HotSpot源码。

   ```cpp
   IRT_ENTRY_NO_ASYNC(void, InterpreterRuntime::monitorenter(JavaThread* thread, BasicObjectLock* elem))
   #ifdef ASSERT
    thread->last_frame().interpreter_frame_verify_monitor(elem);
   #endif
    if (PrintBiasedLockingStatistics) {
      Atomic::inc(BiasedLocking::slow_path_entry_count_addr());
    }
    Handle h_obj(thread, elem->obj());
    assert(Universe::heap()->is_in_reserved_or_null(h_obj()),
           "must be NULL or an object");
      //是否设置偏向锁，如果我们设置之后会为true
    if (UseBiasedLocking) {
      // Retry fast entry if bias is revoked to avoid unnecessary inflation
      ObjectSynchronizer::fast_enter(h_obj, elem->lock(), true, CHECK);
    } else {
        //不设置偏向锁会进入重量级锁的逻辑
      ObjectSynchronizer::slow_enter(h_obj, elem->lock(), CHECK);
    }
    assert(Universe::heap()->is_in_reserved_or_null(elem->obj()),
           "must be NULL or an object");
   ```

   

2. 对于重量级锁，monitorenter函数中会调用 ObjectSynchronizer::slow_enter

3. 最终调用 ObjectMonitor::enter（src/share/vm/runtime/objectMonitor.cpp），源码如下：

   ```cpp
   //省略一些不重要的代码
   void ATTR ObjectMonitor::enter(TRAPS){
     // The following code is ordered to check the most common cases first
     // and to reduce RTS->RTO cache line upgrades on SPARC and IA32 processors.
     Thread * const Self = THREAD;
     void * cur ;
   
     /**
     通过 CAS 操作尝试把 monitor 的 _owner 字段设置为当前线程
     在ObjectMonitor::enter进入的时候会调用Actomic当中的cmpxchg_ptr；
     Atomic::cmpxchg_ptr(Self, &_owner, NULL)
     该函数属于linux内核系统中的函数，依赖cpu去做原子操作
     CAS是一个原子的赋值操作；
     作用就是将monitor对象当中的_owner设置成这个当前线程Self；
     */
     cur = Atomic::cmpxchg_ptr(Self, &_owner, NULL);
     if(cur == NULL){
       // Either ASSERT _recursions == 0 or explicitly Set _recursions = 0.
       assert (_recursions == 0 ,   "invariant") ;
       assert (_owner      == Self, "invariant") ;
       // CONSIDER: set or assert OwnerIsThread == 1
       return ;
     }
   
   
     //线程重入；recursions++，如果当前线程的_owner时当前线程，锁计数+1，表明进入了新的拥有相同锁的同步代码块
     if(cur == Self){
       // TODO-FIXME: : check for integer overflow! BUGID 6557169
       _recursions ++;
       return ;
     }
   
   /**
   如果当前线程第一次来抢monitor该锁；
   如果当前线程是第一次进入该monitor，如果抢到锁了；
   设置_recursions为1，并且将_owner设置为当前线程；
   最后返回即表示当前线程竞争到该锁；
   */
   if(Self -> is_lock_owned((address)cur)){
     assert (_recursions == 0, "internal state error");
     _recursions = 1;
     // Commute error from a thread-specific on-stack BasicLockObject address to
     // a full-fledged "Thread *".
     _owner = Self;
     OwnerIsThread = 1;
     return ;
   }
   
     //以上操作都没有抢到锁，进入循环等待机会
     for (;;){
       jt->set_suspend_equivalent();
       // cleared by handle_special_suspend_equivalent_condition()
       // or java_suspend_self()
   
       // 如果获取锁失败，则等待锁的释放
       EnterI(THREAD);
       if(!ExitSuspendEquivalent(jt)) break;
       //
       // we have acquired the contended monitor, but while we were
       // waiting another thread suspended us. We don't want to enter
       // the monitor while suspend because that would surprise the
       // thread that suspended us.
       //
   
       _recursions = 0;
       _succ = NULL;
       exit(false, Self);
   
       jt->java_suspend_self();
     }
     Self->set_current_pending_monitor(NULL);
   }
   ```

此处省略锁的自旋优化等操作。以上代码的具体流程概括如下：

- 通过CAS尝试把monitor的owner字段设置为当前线程。
- 如果设置之前的owner指向当前线程，说明当前线程再次进入monitor，即重入锁，执行 recursions ++ ，记录重入的次数。
- 如果当前线程是第一次进入该monitor，设置recursions为1，_owner为当前线程，该线程成功获得锁并返回。
- 如果获取锁失败，则等待锁的释放。使用EnterI()方法

### 3. monitor等待

竞争失败等待调用的是ObjectMonitor对象的EnterI方法（src/share/vm/runtime/objectMonitor.cpp），源码如下所示：

```cpp
//省略部分代码
void ATTR ObjectMonitor::EnterI(THREAD){
  Thread * Self = THREAD;

  // Try the lock - TATAS  尝试获取锁
  if(TryLock (Self) > 0){
    assert (_succ        != Self              , "invariant");
    assert (_owner       == Self              , "invariant");
    assert (_Responsible != Self              , "invariant");
    return ;
  }

  //以上操作没抢到锁，进行自旋操作，看能否抢到锁 抢救一下
  if(TrySpin (Self) > 0){
    assert (_succ        == Self              , "invariant");
    assert (_owner       != Self              , "invariant");
    assert (_Responsible != Self              , "invariant");
    return ;
  }


  // 省略部分代码

  // 当前线程被封装成ObjectWaiter对象node（等待的线程），状态设置成ObjectWaiter::TS_CXQ
  ObjectWaiter node(Self);
  Self->ParkEvent->reset();
  node._prev  = (ObjectWaiter *) 0xBAD;
  node.TState = ObjectWaiter::TS_CXQ;

  // 通过CAS把node节点push到_cxq列表中
  ObjectWaiter * nxt;
//期间可能会有失败，循环将所有没抢到锁的线程都放入cxq中
  for(;;){
    node._next = next = _cxq;
    if(Atomic::cmpxchg_ptr(&node, &_cxq, nxt) == nxt) break;

    // Interference - the CAS failed because _cxq changed. Just retry.
    // As an optional optimization we retry the lock.
    //在放入cxq的期间尝试再次获取锁
    if(TryLock(Self) > 0){
        if(TryLock (Self) > 0){
          assert (_succ        != Self              , "invariant");
          assert (_owner       == Self              , "invariant");
          assert (_Responsible != Self              , "invariant");
          return ;
        }
     }

      // 省略部分代码
      for(;;){
        // 线程在挂起前做一下挣扎，看能不能获取到锁
        if(TryLock (Self) > 0)break;
        assert (_owner != Self, "inveriant");

        if((SyncFlags & 2) && _Reponsible == NULL){
          Atomic::cmpxchg_ptr (Self, &_Reponsible, NULL);
        }

        // park self
        if(_Responsible == Self || (SynchFlags & 1)){
          TEVENT (Inflated enter - park TIMED);
          Self->_ParkEvent->park((jlong)RecheckInterval);
          // Increase the RecheckInterval, but clamp the value.
          RecheckInterval *= 8；
          if(RecheckInterval > 1000) RecheckInterval = 1000;
        }else{
          TEVENT (Inflated enter - park UNTIMED);
          // 通过park将当前线程挂起，等待被唤醒
          Self->_ParkEvent->park();
        }
    //当挂起的线程被唤醒之后，再次尝试获取锁
        if(TryLock(Self) > 0) break;
      }

  }
```



当该线程被唤醒时，会从挂起的点继续执行，通过 ObjectMonitor::TryLock 尝试获取锁，TryLock方
法实现如下：

```cpp
int ObjectMonitor::TryLock(Thread * Self){
  for(;;){
    void * own = owner;
    if(own != null) return 0;
    if(Atomic::cmpxchg_ptr (Self, &owner, NULL) == NULL){
      // Either guarantee recursions == 0 or set _recursions = 0.
      assert (recursions == 0,    "invariant");
      assert (_owner      == Self, "invariant");
      // CONSIDER: set or assert that OwnerIsThread == 1
      return 1;
    }
    // The lock had been freen momentarily, but we lost the race to the lock.
    // Interference -- the CAS faild.
    // we can either return -1 or retry.
    // Retry doesn't make as much sense because the lock was just acquired.
    if(true) return -1;
  }
}
```



以上代码的具体流程概括如下：

- 当前线程被封装成ObjectWaiter对象node，状态设置成ObjectWaiter::TS_CXQ。
- 在for循环中，通过CAS把node节点push到_cxq列表中，同一时刻可能有多个线程把自己的node
  节点push到cxq列表中。
- node节点push到cxq列表之后，通过自旋尝试获取锁，如果还是没有获取到锁，则通过park将当
  前线程挂起，等待被唤醒。
- 当该线程被唤醒时，会从挂起的点继续执行，通过 ObjectMonitor::TryLock 尝试获取锁

### 4. monitor释放

当某个持有锁的线程执行完同步代码块时，会进行锁的释放，给其它线程机会执行同步代码，在
HotSpot中，通过退出monitor的方式实现锁的释放，并通知被阻塞的线程，具体实现位于
ObjectMonitor的exit方法中。（src/share/vm/runtime/objectMonitor.cpp），源码：

```cpp
void ATTR ObjectMonitor::exit(bool not_suspended, TRAPS){
  Thread * Self = THREAD;

  // 省略部分代码
  if(_recursions != 0){
    _recursions--;    // this is simple recursive enter
    TEVENT (Inflated exit - recursive);
    return ;
  }

  // 省略部分代码
  //w为要被唤醒的线程
  ObjectWaiter * w = NULL;
  int QMode = Knob_QMode;

  // qmode = 2：直接绕过EntryList队列，从_cxq队列中获取线程用于竞争锁
  if(QMode == 2 && _cxq != NULL){
    //cxq不为空，拿到cxq的首结点
    w = _cxq;
  assert ( w != NULL, "invariant");
  assert ( w->TState == ObjectWaiter::TS_CXQ, "invariant");
  //唤醒线程操作
  ExitEpilog(Self, w);
  return ;
  }

  // qmode=3：cxq队列插入EntryList尾部；
  if(QMode == 3 && _cxq != NULL){
    w = _cxq;
    for(;;){
      assert (w != NULL, "Invariant");
      ObjectWaiter * u = (ObjectWaiter *)Atomic::cmpxchg_ptr (NULL, &_cxq, w);
      if( u == w ) break;
      w = u;
    }
    assert( w != NULL , "invariant");

    ObjectWaiter * q = NULL;
    ObjectWaiter * p;
    for( p = w ;  p != NULL ; p = p->_next){
      guarantee (p->TState == ObjectWaiter::TS_CXQ, "Invariant");
      p->TState = ObjectWaiter::TS_ENTER;
      p->prev = q;
      q = p;
    }

    ObjectWaiter * Tail;
    for ( Tail = _EntryList; Tail != NULL && Tail->_next != NULL; Tail = Tail->_next);
    if(Tail == NULL){
      _EntryList = w;
    }else{
      Tail->_next = w;
      w->_prev = Tail;
    }
  }


  // qmode=4: cxq队列插入到_EntryList头部
  if(QMode == 4 && _cxq != NULL){
    w = _cxq;
    for(;;){
      assert (w != NULL, "Invariant");
      ObjectWaiter * u = (ObjectWaiter *)Atomic::cmpxchg_ptr(NULL, &_cxq, w);
      if(u == w) break;
      w = u;
    }
    assert (w != NULL , "invariant");

    ObjectWaiter * q = NULL;
    ObjectWaiter * p;
    for( p = w; p != NULL ; p ->_next){
      guarantee(p->TState == ObjectWaiter::TS_CXQ, "Invariant");
      p->TState = ObjectWaiter::TS_ENTER;
      p->_prev = q;
      q = p;
    }

    if(_EntryList != NULL){
      q->_next = _EntryList;
      _EntryList->_prev = q;
    }
    _EntryList = w;
  }

  w = _EntryList;
  if(w != NULL){
    assert (w->TState == ObjectWaiter::TS_ENTER, "invariant");
    ExitEpilog(Self, w);
    return ;
  }

  w = _cxq;
  if(w == NULL) continue;

  for(;;){
    assert (w != NULL, "Invariant");
    ObjectWaiter * u = (ObjectWaiter *)Atomic::cmpxchg_ptr(NULL, &_cxq, w);
    if(u == w) break;
    w = u;
  }
  TEVENT(Inflated exit - drain cxq into EntryList);

  assert( w          != NULL , "invariant");
  assert( _EntryList != NULL , "invariant");

  if(QMode == 1){
    // QMode == 1: drain cxq to EntryList,reversing order
    // we also reverse the order of the list
    ObjectWaiter * s = NULL;
    ObjectWaiter * t = w;
    ObjectWaiter * u = NULL;
    while(t != NULL){
      guarantee(t->TState == ObjectWaiter::TS_CXQ, "invariant");
      t->TState = ObjectWaiter::TS_ENTER;
      u = t->_next;
      t->_prev = u;
      t->_next = s;
      s = t;
      t = u;
    }
    _EntryList = s;
    assert(s != NULL, "invariant");
  }else{
    // QMode ==0 or QMode == 2
    _EntryList = w;
    ObjectWaiter * q = NULL;
    ObjectWaiter * p;
    for(p = w; p != NULL; p = p->_next){
      guarantee(p->TState == ObjectWaiter::TS_CXQ, "Invariant");
      p->TState = ObjectWaiter::TS_ENTER;
      p->_prev = q;
      q = p;
    }
  }

  if (_succ != NULL) continue;

  w = _EntryList;
  if(w != NULL){
    guarantee(w -> TState == ObjectWater::TS_ENTER, "invariant");
    //唤醒线程
    ExitEpilog(Self, w);
    return ;
  }
}
```





- 退出同步代码块时会让recursions减1，当recursions的值减为0时，说明线程释放了锁。
- 根据不同的策略（由QMode指定），从cxq或EntryList中获取头节点，通过ObjectMonitor::ExitEpilog 方法唤醒该节点封装的线程，唤醒操作最终由unpark完成：

```cpp
void ObjectMonitor::ExitEpilog(Thread * Self, ObjectWaiter * wakee){
  assert( _owner == Self, "invariant");

  _succ = Knob_SuccEnabled ? wakee->_thread : NULL;
  ParkEvent * Trigger = wakee->_event;

  wakee = NULL;

  // Drop the lock
  OrderAccess::release_store_ptr(&_owner, NULL);
  OrderAccess::fence();           // ST _owner vs LD in unpark()

  if(SafepointSynchronize::do_call_back()){
    TEVENT(unpack before SAFEPOINT);
  }

  DTRACE_MONITOR_PROBE(contended__exit, this, object(), Self);
  Trigger->unpark();  // 唤醒之前被park()挂起的线程

  // Maintain stats and report events to JVMTI
  if (ObjectMonitor::_synch_Parks != NULL){
    ObjectMonitor::_sync_Parks->inc();
  }
}
```



被唤醒的线程，会回到 void ATTR ObjectMonitor::EnterI (TRAPS) 的第600行，继续执行monitor
的竞争。

### 5. monitor是重量级锁

可以看到ObjectMonitor的函数调用中会涉及到Atomic::cmpxchg_ptr，Atomic::inc_ptr等内核函数，
执行同步代码块，没有竞争到锁的对象会park()被挂起，竞争到锁的线程会unpark()唤醒。这个时候就
会存在操作系统用户态和内核态的转换，这种切换会消耗大量的系统资源。所以synchronized是Java语
言中是一个重量级(Heavyweight)的操作。
要了解用户态和内核态需要先了解Linux系统的体系架构：

<img src="https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20240327164531821.png" alt="image-20240327164531821" style="zoom:33%;" />

从上图可以看出，Linux操作系统的体系架构分为：用户空间（应用程序的活动空间）和内核。
**内核：**本质上可以理解为一种软件，控制计算机的硬件资源，并提供上层应用程序运行的环境。
**用户空间：**上层应用程序活动的空间。应用程序的执行必须依托于内核提供的资源，包括CPU资源、存
储资源、I/O资源等。
**系统调用：**为了使上层应用能够访问到这些资源，内核必须为上层应用提供访问的接口：即系统调用。

所有进程初始都运行于用户空间，此时即为用户运行状态（简称：用户态）；但是当它调用系统调用执
行某些操作时，例如 I/O调用，此时需要陷入内核中运行，我们就称进程处于内核运行态（或简称为内
核态）。 系统调用的过程可以简单理解为：

1. 用户态程序将一些数据值放在寄存器中， 或者使用参数创建一个堆栈， 以此表明需要操作系统提
   供的服务。
2. 用户态程序执行系统调用。
3. CPU切换到内核态，并跳到位于内存指定位置的指令。
4. 系统调用处理器(system call handler)会读取程序放入内存的数据参数，并执行程序请求的服务。
5. 系统调用完成后，操作系统会重置CPU为用户态并返回系统调用的结果。

由此可见用户态切换至内核态需要传递许多变量，同时内核还需要保护好用户态在切换时的一些寄存器
值、变量等，以备内核态切换回用户态。这种切换就带来了大量的系统资源消耗，这就是在
synchronized未优化之前，效率低的原因。