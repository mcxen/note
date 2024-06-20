LRU缓存：`**int oldestKey = cache.keySet().iterator().next();**`： 这一行代码定义了一个名为`oldestKey`的整型变量，并将其初始化为`cache`键集的第一个键。这里的变量名`oldestKey`暗示着这个键是“最旧”的，可能是按插入顺序来看的第一个元素，特别是在使用`LinkedHashMap`这样可以保持插入顺序的`Map`实现时。不过，如果使用的是`HashMap`，则不能保证这个键是最旧的，因为`HashMap`不保证任何顺序。

### [146. LRU 缓存](https://leetcode.cn/problems/lru-cache/)

请你设计并实现一个满足 [LRU (最近最少使用) 缓存](https://baike.baidu.com/item/LRU) 约束的数据结构。

实现 `LRUCache` 类：

- `LRUCache(int capacity)` 以 **正整数** 作为容量 `capacity` 初始化 LRU 缓存

- `int get(int key)` 如果关键字 `key` 存在于缓存中，则返回关键字的值，否则返回 `-1` 。

- `void put(int key, int value)` 如果关键字 `key` 已经存在，则变更其数据值 `value` ；如果不存在，则向缓存中插入该组 `key-value` 。如果插入操作导致关键字数量超过 `capacity` ，则应该 **逐出** 最久未使用的关键字。

函数 `get` 和 `put` 必须以 `O(1)` 的平均时间复杂度运行。

**示例：**

```plain
输入
["LRUCache", "put", "put", "get", "put", "get", "put", "get", "get", "get"]
[[2], [1, 1], [2, 2], [1], [3, 3], [2], [4, 4], [1], [3], [4]]
输出
[null, null, null, 1, null, -1, null, -1, 3, 4]

解释
LRUCache lRUCache = new LRUCache(2);
lRUCache.put(1, 1); // 缓存是 {1=1}
lRUCache.put(2, 2); // 缓存是 {1=1, 2=2}
lRUCache.get(1);    // 返回 1
lRUCache.put(3, 3); // 该操作会使得关键字 2 作废，缓存是 {1=1, 3=3}
lRUCache.get(2);    // 返回 -1 (未找到)
lRUCache.put(4, 4); // 该操作会使得关键字 1 作废，缓存是 {4=4, 3=3}
lRUCache.get(1);    // 返回 -1 (未找到)
lRUCache.get(3);    // 返回 3
lRUCache.get(4);    // 返回 4
```

要让 put 和 get 方法的时间复杂度为 O(1)，我们可以总结出 cache 这个数据结构必要的条件：

1、显然 cache 中的元素必须有时序，以区分最近使用的和久未使用的数据，当容量满了之后要删除最久未使用的那个元素腾位置。

2、我们要在 cache 中快速找某个 key 是否已存在并得到对应的 val；

3、每次访问 cache 中的某个 key，需要将这个元素变为最近使用的，也就是说 cache 要支持在任意位置快速插入和删除元素。

那么，什么数据结构同时符合上述条件呢？**哈希表查找快，但是数据无固定顺序；链表有顺序之分，插入删除快，但是查找慢。**所以结合一下，形成一种新的数据结构：`哈希链表 LinkedHashMap`。

#### 哈希链表LinkedHashMap实现LRU

```java
class LRUCache {
    int cap;
    LinkedHashMap<Integer,Integer> cache = new LinkedHashMap<>();
    public LRUCache(int capacity) {
        this.cap = capacity;
    }
    
    public int get(int key) {
        if(!cache.containsKey(key)){
            return -1;
        }
        int val = cache.get(key);
        cache.remove(key);
        cache.put(key,val);//再次插入就实现了最新的使用的标记
        return val;
    }
    
    public void put(int key, int value) {
        if(cache.containsKey(key)){
            cache.put(key,value);
            int val = cache.get(key);
            cache.remove(key);
            cache.put(key,val);//再次插入
            return;
        }
      /* 这样也可以
      if (map.containsKey(key)){
            map.remove(key);
            map.put(key,value);
            return;
        }
      */
        if(cache.size()>=cap){
            //使用iterator()方法获取迭代器，可以遍历集合中的元素。next()方法用于返回下一个元素，即获取最早的键。
            int oldestKey = cache.keySet().iterator().next();
            cache.remove(oldestKey);
        }
        cache.put(key,value);
        return;
    }
}
```

用 LinkedHashMap 可以很容易实现 LRU 缓存，不过面试的时候估计这样不好，还是尽量自己实现数据结构吧🤣

主要想法是使用 JDK 提供的 HashMap，然后自己写一个 Node 节点类，用来保存 value ，并且通过这个 Node 里面的 prev、next 指针将各个值串联起来，这样就维护了顺序。

很重要的一个细节是，Node 里面还要加上 key （尽管 HashMap 本来就存了一份）。

 原因是当缓存达到容量上限时，就要先移除尾部的节点，这个时候如果只移除链表的 tail 节点，忽略了 HasmMap 也要 remove ，后面再访问这个被移除的 key 就会造成空指针异常！！！ 

所以我们在 Node 节点里面加上 key ，删掉 tail 之前先 remove 掉 HasmMap 的这个 key，就很方便了。

#### HashMap独立实现LRU

```java
import java.util.*;
public class Solution {
    private int capacity;
    private Map<Integer,Node> map;
    private Node head;
    private Node tail;
    private int used;
    class Node{
        int key;
        int value;
        Node prev;
        Node next;
        Node(int key,int value,Node prev,Node next){
            this.key = key;
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }
 public Solution(int capacity) {
    //构造函数
    this.capacity = capacity;
    this.map =  new HashMap<>();
    this.used= 0;
 }
 public int get(int key) {
 // write code here
    if(!map.containsKey(key)){
        return -1;
    }
    makeRecently(key);
    return map.get(key).value;
 }
 public void set(int key, int value) {
 // write code here
    if(map.containsKey(key)){
        map.get(key).value = value;
        makeRecently(key);
        return;
    }
    if(used==capacity){
        map.remove(tail.key);
        if (tail.prev != null) {
            tail = tail.prev;
            tail.next = null; // tail 往前面挪动一位
        } else {
            // 处理只有一个节点的情况
            head = null;
            tail = null;
        }
        used--;
    }
    if(head==null){
        head = new Node(key,value,null,null);
        tail = head;
    }
    else{
        //head不为空
        Node t = new Node(key,value,null,head);
        head.prev = t;
        head = t;
    }
    map.put(key,head);
    used++;
 }
 private void makeRecently(int key){
    Node t = map.get(key);
    if(t!=head){
        if(t==tail){
            tail = tail.prev;
            tail.next = null;
        }else{
            t.prev.next = t.next;//将t跳过了
            t.next.prev = t.prev;//将next的向前指的顺序纠正
        }
        t.prev = null;
        t.next = head;
        head.prev = t;
        head =t;
    }
 }
}
```

> 增加了对 `tail` 是否为 `null` 的检查，以避免 `NullPointerException`。

### 实现一个cache，包括LRU算法和在x秒后过期



```java
import java.util.LinkedHashMap;  
import java.util.Map;  
import java.util.concurrent.ScheduledExecutorService;  
import java.util.concurrent.Executors;  
import java.util.concurrent.TimeUnit;  
  
public class LRUCacheWithExpiration<K, V> {  
    // 缓存的最大容量  
    private final int capacity;  
    // 缓存项的过期时间（秒）  
    private final long expirationTimeInSeconds;  
    // 使用LinkedHashMap实现LRU缓存，accessOrder设置为true以启用LRU顺序  
    private final LinkedHashMap<K, CacheEntry<V>> cacheMap;  
    // 定时任务执行器，用于清理过期的缓存项  
    private final ScheduledExecutorService expirationExecutor;  
  
    // 内部类CacheEntry，用于存储缓存值和过期时间  
    private static class CacheEntry<V> {  
        V value;  
        long expirationTime;  
  
        CacheEntry(V value, long expirationTime) {  
            this.value = value;  
            this.expirationTime = expirationTime;  
        }  
    }  
  
    // 构造函数  
    public LRUCacheWithExpiration(int capacity, long expirationTimeInSeconds) {  
        this.capacity = capacity;  
        this.expirationTimeInSeconds = expirationTimeInSeconds;  
        // 初始化LRU缓存，设置accessOrder为true，以便按访问顺序进行LRU操作  
        this.cacheMap = new LinkedHashMap<>(capacity, 0.75f, true) {  
            // 重写removeEldestEntry方法，当Map大小超过指定容量时，删除最老的元素  
            @Override  
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {  
                return size() > LRUCacheWithExpiration.this.capacity;  
            }  
        };  
        // 初始化定时任务执行器  
        this.expirationExecutor = Executors.newSingleThreadScheduledExecutor();  
        // 安排定时任务，每隔expirationTimeInSeconds秒执行一次清理过期缓存项的操作  
        this.expirationExecutor.scheduleAtFixedRate(this::evictExpiredEntries, expirationTimeInSeconds, expirationTimeInSeconds, TimeUnit.SECONDS);  
    }  
  
    // 获取缓存项  
    public synchronized V get(K key) {  
        CacheEntry<V> entry = cacheMap.get(key);  
        if (entry == null || isExpired(entry.expirationTime)) {  
            // 缓存项不存在或已过期，返回null  
            return null;  
        }  
        // 更新缓存项的访问顺序（移到尾部，表示最近访问）  
        cacheMap.remove(key);  
        cacheMap.put(key, entry);  
        // 返回缓存值  
        return entry.value;  
    }  
  
    // 添加或更新缓存项  
    public synchronized void put(K key, V value) {  
        // 创建新的缓存项，设置过期时间  
        CacheEntry<V> newEntry = new CacheEntry<>(value, System.currentTimeMillis() + (expirationTimeInSeconds * 1000));  
        // 将新的缓存项添加到LRU缓存中，如果缓存已满，则会自动删除最老的元素  
        cacheMap.put(key, newEntry);  
    }  
  
    // 清理过期缓存项的方法  
    private void evictExpiredEntries() {  
        long currentTime = System.currentTimeMillis();  
        // 遍历缓存，移除已过期的缓存项  
        cacheMap.entrySet().removeIf(entry -> isExpired(entry.getValue().expirationTime));  
    }  
  
    // 判断缓存项是否过期  
    private boolean isExpired(long expirationTime) {  
        return expirationTime <= System.currentTimeMillis();  
    }  
  
    // 关闭缓存，停止定时任务执行器  
    public void close() {  
        expirationExecutor.shutdown();  
        try {  
            if (!expirationExecutor.awaitTermination(60, TimeUnit.SECONDS)) {  
                expirationExecutor.shutdownNow();  
            }  
        } catch (InterruptedException e) {  
            expirationExecutor.shutdownNow();  
            Thread.currentThread().interrupt();  
        }  
    }  
}
```