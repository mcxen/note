### 集合框架

#### Arrays的常见方法

- 各种排序算法的实现
- 二分查找算法的实现
- fill 填充元素的方法
- asList 方法，用来将数组转化为集合
- hashCode方法和equals方法
- deepHashCode
- toString

#### Collections的常见方法

- Collections类包含了很多的内部类
  - 以UnmodifiableCollection这样的，UnmodifiableXXX 打头的
  - 以SynchronizedSortedSet这样的，SynchronizedXXX打头的
  - 以CheckedSortedSet这样的，CheckedXXX打头的 ？？？
  - 还有一些辅助作用的内部类
- 而方法的操作有很大的一部分就是为了返回上述的这些类型的集合对象的
- 比如其他的方法
  - sort
  - swap
  - shuffle
  - min
  - max
  - reverse
  - binarySearch
  - indexdBinarySearch
  - emptyXXX方法
  - addAll 等方法

#### Queue的add()和offer()方法有什么区别？

- add() 方法满了之后，会抛出  IllegalStateException  异常
- 而offer() 方法，会返回false

#### Queue的remove()和poll()方法有什么区别？

- Queue 中 remove() 和 poll() 都是用来从队列头部删除一个元素。
- 在队列元素为空的情况下，remove() 方法会抛出NoSuchElementException异常，poll() 方法只会返回 null 。

#### Queue的element()和peek()方法有什么区别？

- Queue 中 element() 和 peek() 都是用来返回队列的头元素，不删除。
- 在队列元素为空的情况下，element() 方法会抛出NoSuchElementException异常，peek() 方法只会返回 null。

#### 哪些集合是线程安全的

- Vector
- Stack
- Hashtable
- JUC下的所有的集合类

#### 迭代器是什么

- iterator是Java的一种设计模式。迭代器模式。其是用来顺序访问集合对象的元素，无需知道对象的底层实现
- Iterator是可遍历集合的对象。为各种容器提供了公共的操作接口，隔离容器遍历的操作的底层限制，从而解耦
- 缺点是新的集合需要增加对应的迭代器，迭代器与集合类成对增加
- java.lang.Iterator 
  - next() 获取下一个元素
  - hasNext() 是否有下一个元素
  - remove() 移除一个元素
  - forEachRemaining(Consumer<? super E> action)  迭代器对集合的遍历
- 在迭代过程中调用集合的 remove(Object o) 可能会报 java.util.ConcurrentModificationException 异常
- forEachRemaining 方法中 调用Iterator 的 remove 方法会报 java.lang.IllegalStateException 异常

#### ListIterator和Iterator的区别

- ListIterator继承Iterator
- ListIterator 比 Iterator多更多的方法
- 方法
  - add(E e) 将元素插入到列表中，插入位置为当前位置之前
  - set(E e) 迭代器返回的最后一个元素替换e
  - hasPrevious() 迭代器当前位置。反向遍历集合是否还有元素
  - previous() 迭代器当前位置，反向遍历集合，下一个元素
  - previousIndex() 迭代器当前位置，反向遍历集合，返回一个元素的下标
  - nextIndex() 迭代器当前位置，返回下一个元素的下标
- 二者区别
  - 使用范围不同，Iterator 可以迭代所有的集合；ListIterator只能用于List以及其子类
  - ListIterator有 add方法，可以添加元素，Iterator不可以
  - ListIterator有 hasPrevious方法，可以实现逆向遍历元素，Iterator不可以
  - ListIterator有 previousIndex 方法，可以前一个元素的下标，Iterator不可以
  - ListIterator有 nextIndex 方法，可以下一个元素的下标，Iterator不可以
  - ListIterator有 set 方法，可以修改，Iterator不可以

#### 怎么保证一个集合不能被修改？

- 在 java.util.Collections 里面的 很多方法如：UnmodifiableList、UnmodifiableXXX等方法，如果调用修改方法，就抛出 UnsupportedOperationException 异常

#### 为什么基本类型不能做为HashMap的键值？

- 因为Java中的HashMap使用的key和value的，都是基于泛型的，而泛型不允许使用基本数据类型，但是在JDK5之后支持自动装箱拆箱，所以存入的基本数据类型其实还是存储的对象

#### Java已经存在数组类型，为什么还需要提供集合

- 数组优点
  - 数组的效率高于集合类
  - 数组能存放基本数据类型和对象，集合中只能存储对象
- 数组的缺点
  - 不是面向对象的
  - 数据的长度和类型一旦确定，就不能在进行修改
  - 数组中无法判断到底存储了多少元素。只能通过length获取数组申明的长度
  - 数组存储数据的格式是连续的，而集合存储数据结构更加丰富

#### TreeSet的实现原理

- TreeSet是基于TreeMap的key实现的，而TreeMap是基于红黑树实现的
- 特点：
  - 有序
  - 不重复
  - 添加、删除、判断元素是否存在。效率比较高时间复杂度为 O(log(N))

#### HashSet的实现原理是什么，有什么特点？

- HashSet是基于HashMap实现的，查询速度很快
- HashMap是支持null值的，所以HashSet是可以为null的
- HashSet存储自定义类的时候，需要重写hashCode和equals方法，确保集合对自定义对象的唯一性判断，根据key进行hash，然后判断key是否为同一个对象，再判断key是否equals
- 无顺序。不可重复

#### Map的实现类中，哪些是有序的，哪些是无序的，如何保证其有序性？

- Map 的实现类有 HashMap、LinkedHashMap、TreeMap
- HashMap是有无序的
- LinkedHashMap 和 TreeMap 是有序的。LinkedHashMap 记录了添加数据的顺序；TreeMap 默认是升序

- LinkedHashMap 底层存储结构是哈希表+链表，链表记录了添加数据的顺序
- TreeMap 底层存储结构是二叉树，二叉树的中序遍历保证了数据的有序性

#### TreeMap和TreeSet如何保证有序

- TreeMap 会对 key 进行比较，有两种比较方式，第一种是构造方法指定 Comparator，使用 Comparator#compare() 方法进行比较；第二种是构造方法未指定 Comparator 接口，需要 key 对象的类实现 Comparable 接口，用 Comparable #compareTo() 方法进行比较
- TreeSet 底层是使用 TreeMap 实现

#### Collections的sort方法如何比较元素

- 第一种要求传入的待排序容器中存放的对象比较实现 Comparable 接口以实现元素的比较
- 第二种不强制性的要求容器中的元素必须可比较，但要求传入参数 Comparator 接口的子类，需要重写 compare() 方法实现元素的比较规则，其实就是通过接口注入比较元素大小的算法，这就是回调模式的应用

#### 参考文章

- HashMap和Hashtable的区别： https://www.cnblogs.com/williamjie/p/9099141.html 
- Hashtable源码解析： https://blog.csdn.net/ns_code/article/details/36191279 
- HashMap源码解析： https://blog.csdn.net/ns_code/article/details/36034955 
- ArrayList和LinkedList： https://blog.csdn.net/luyuqin0115/article/details/80395694 