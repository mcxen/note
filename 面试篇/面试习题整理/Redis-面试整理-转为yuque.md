# Redis

## 数据持久化

### 1. Redis 保证数据不丢失？

Redis 保证数据不丢失的主要手段有两个：

1. 持久化
2. 集群运行

#### 1.Redis 持久化

持久化是指将数据从内存中存储到持久化存储介质中（如硬盘）的过程，以便在程序重启或者系统崩溃等情况下，能够从持久化存储介质中恢复数据。

Redis 4.0 之后支持以下 3 种持久化方案：

1. **RDB（Redis DataBase）持久化**：快照方式持久化，将某一个时刻的内存数据，以二进制的方式写入磁盘；
2. **AOF（Append Only File）持久化**：文件追加持久化，记录所有非查询操作命令，并以文本的形式追加到文件中；
3. **混合持久化：RDB + AOF 混合方式的持久化**，Redis 4.0 之后新增的方式，混合持久化是结合了 RDB 和 AOF 的优点，在写入的时候，先把当前的数据以 RDB 的形式写入文件的开头，再将后续的操作命令以 AOF 的格式存入文件，这样既能保证 Redis 重启时的速度，又能减低数据丢失的风险。

1.1 RDB 持久化

RDB（Redis Database）是将某一个时刻的内存快照（Snapshot），以二进制的方式写入磁盘的持久化机制。

RDB 持久化机制有以下优缺点：

**优点：**

1. 速度快：相对于 AOF 持久化方式，RDB 持久化速度更快，因为它只需要在指定的时间间隔内将数据从内存中写入到磁盘上。
2. 空间占用小：RDB 持久化会将数据保存在一个压缩的二进制文件中，因此相对于 AOF 持久化方式，它占用的磁盘空间更小。
3. 恢复速度快：因为 RDB 文件是一个完整的数据库快照，所以在 Redis 重启后，可以非常快速地将数据恢复到内存中。
4. 可靠性高：RDB 持久化方式可以保证数据的可靠性，因为数据会在指定时间间隔内自动写入磁盘，即使 Redis 进程崩溃或者服务器断电，也可以通过加载最近的一次快照文件恢复数据。

**缺点：**

1. 数据可能会丢失：RDB 持久化方式只能保证数据在指定时间间隔内写入磁盘，因此如果 Redis 进程崩溃或者服务器断电，从最后一次快照保存到崩溃的时间点之间的数据可能会丢失。
2. 实时性差：因为 RDB 持久化是定期执行的，因此从最后一次快照保存到当前时间点之间的数据可能会丢失。如果需要更高的实时性，可以使用 AOF 持久化方式。

所以，RDB 持久化方式适合用于对数据可靠性要求较高，但对实时性要求不高的场景，如 Redis 中的备份和数据恢复等。

1.2 AOF 持久化

AOF（Append Only File）它是将 Redis 每个非查询操作命令都追加记录到文件（appendonly.aof）中的持久化机制。

AOF 持久化机制有以下优缺点：

**优点：**

1. 数据不容易丢失：AOF 持久化方式会将 Redis 执行的每一个写命令记录到一个文件中，因此即使 Redis 进程崩溃或者服务器断电，也可以通过重放 AOF 文件中的命令来恢复数据。
2. 实时性好：由于 AOF 持久化方式是将每一个写命令记录到文件中，因此它的实时性比 RDB 持久化方式更好。
3. 数据可读性强：AOF 持久化文件是一个纯文本文件，可以被人类读取和理解，因此可以方便地进行数据备份和恢复操作。

**缺点：**

1. 写入性能略低：由于 AOF 持久化方式需要将每一个写命令记录到文件中，因此相对于 RDB 持久化方式，它的写入性能略低。
2. 占用磁盘空间大：由于 AOF 持久化方式需要记录每一个写命令，因此相对于 RDB 持久化方式，它占用的磁盘空间更大。
3. AOF 文件可能会出现损坏：由于 AOF 文件是不断地追加写入的，因此如果文件损坏，可能会导致数据无法恢复。

所以，AOF 持久化方式适合用于对数据实时性要求较高，但对数据大小和写入性能要求相对较低的场景，如需要对数据进行实时备份的应用场景。

**1.3 混合持久化**

Redis 混合持久化是指将 RDB 持久化方式和 AOF 持久化方式结合起来使用，以充分发挥它们的优势，同时避免它们的缺点。

它的优缺点如下：

**优点：**

混合持久化结合了 RDB 和 AOF 持久化的优点，开头为 RDB 的格式，使得 Redis 可以更快的启动，同时结合 AOF 的优点，有减低了大量数据丢失的风险。

**缺点：**

1. 实现复杂度高：混合持久化需要同时维护 RDB 文件和 AOF 文件，因此实现复杂度相对于单独使用 RDB 或 AOF 持久化方式要高。
2. 可读性差：AOF 文件中添加了 RDB 格式的内容，使得 AOF 文件的可读性变得很差；
3. 兼容性差：如果开启混合持久化，那么此混合持久化 AOF 文件，就不能用在 Redis 4.0 之前版本了。

所以，Redis 混合持久化方式适合用于，需要兼顾启动速度和减低数据丢失的场景。但需要注意的是，混合持久化的实现复杂度较高、可读性差，只能用于 Redis 4.0 以上版本，因此在选择时需要根据实际情况进行权衡。

#### 2.Redis 集群

Redis 集群是将原先的单服务器，变为了多服务器，这样 Redis 保存的数据也从一台服务器变成了多台服务器，这样即使有一台服务器出问题了，其他的服务器还有备份数据。所以使用 Redis 集群除了可以保证高可用，还保证了数据不丢失。

Redis 集群运行有以下 3 种方案：

1. 主从同步
2. 哨兵模式
3. Redis Cluster

2.1 主从同步

主从同步 (主从复制) 是 Redis 高可用服务的基石，也是多机运行中最基础的一个。我们把主要存储数据的节点叫做主节点 (master)，把其他通过复制主节点数据的副本节点叫做从节点 (slave)，如下图所示：

![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/1582086579618-3ea491f7-a887-4380-9f48-84237675cec5.png)

在 Redis 中一个主节点可以拥有多个从节点，一个从节点也可以是其他服务器的主节点，如下图所示：

![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/1582086587052-ff6c03d7-28d4-4881-bc5b-55c5b277e5ae.png)

[#](#_2-2-哨兵模式) 2.2 哨兵模式

主从同步存在一个致命的问题，当主节点奔溃之后，需要人工干预才能恢复 Redis 的正常使用。 所以我们需要一个自动的工具——Redis Sentinel (哨兵模式) 来把手动的过程变成自动的，让 Redis 拥有自动容灾恢复 (failover) 的能力。 哨兵模式如下所示：

![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/1582100659240-40cfcd46-0c43-4808-a1cf-32b970f98ef6.png)

> 小贴士：Redis Sentinel 的最小分配单位是一主一从。

[#](#_2-3-redis-cluster) 2.3 Redis Cluster

Redis Cluster 是 Redis 3.0 版本推出的 Redis 集群方案，它将数据分布在不同的服务区上，以此来降低系统对单主节点的依赖，并且可以大大的提高 Redis 服务的读写性能。 Redis Cluster 架构图如下所示：

![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/1585148925496-361a449a-145e-44f2-a414-a46319140e1d.png)

从上图可以看出 Redis 的主从同步只能有一个主节点，而 Redis Cluster 可以拥有无数个主从节点，因此 Redis Cluster 拥有更强大的平行扩展能力，也就是说当 Redis Cluster 拥有两个主从节点时，从理论上来讲 Redis 的性能相比于主从来说性能提升了两倍，并且 Redis Cluster 也有自动容灾恢复的机制。

Redis 保证数据不丢失的主要手段有两个：持久化和集群运行。其中持久化有三种实现：RDB、AOF、混合持久化；而集群（运行）也包含了三种实现：主从复制、哨兵模式和 Redis Cluster。

### 3. 说一下如何解决缓存穿透，缓存击穿（缓存雪崩）

**缓存穿透**：指查询一个一定不存在的数据，由于缓存不命中时需要从数据库查询，而数据库中也无此数据，因此无法写入缓存，这将导致这个不存在的数据每次请求都要到数据库去查询，给数据库带来压力。对于恶意利用此漏洞的攻击，甚至可能压垮数据库。其**解决方案**包括但不限于：

1. 对查询的key进行合法性校验，比如长度、格式等，对不合法的key直接拒绝服务。
2. 对不存在的key也进行缓存，设置一个较短的过期时间即可。
3. 使用布隆过滤器，将所有可能存在的key预先加载到过滤器中，在查询前先进行过滤，如果不存在则直接返回，避免对数据库的查询。



**缓存雪崩**：当缓存中数据大批量到过期时间，而查询数据量巨大，引起数据库压力过大甚至宕机。**解决方案**包括：

1. 给缓存的失效时间加上一个随机值，避免集体失效。
2. 使用互斥锁，当缓存失效的时候，不是立即去load db，而是先使用缓存工具的某个机制，比如Redis的SETNX去设置一个锁，当操作返回成功时，再去load db并放入缓存；否则，就重试获取缓存值。
3. 采用双缓存策略，即设置两级缓存，原始缓存和拷贝缓存，原始缓存失效时，可以访问拷贝缓存，原始缓存与拷贝缓存，定期同步数据，保证一致。

**缓存击穿**：是指一个key非常热点，在不停的扛着大并发，大并发集中对这一个点进行访问，当这个key在失效的瞬间，持续的大并发就穿破缓存，直接请求数据库，就像在一个屏障上凿开了一个洞。其**解决方案**包括：

1. 设置热点数据永远不过期。
2. 使用互斥锁，当缓存失效的时候，不是立即去load db，而是通过缓存工具的某个机制（如Redis的SETNX）去设置一个锁，当操作返回成功时，再去load db并放入缓存；否则，就重试获取缓存值。



### 5. 听说过write-ahead logging（WAL）吗？你对这个有什么想法吗？

分析：这里估计面试官是想说Mysql redo日志的实现的差别。*但是我觉得跟这里redis的更新没有本质差别，也算是WAL*

**回答：**：

- **Mysql**:中的redo log相关实现用到了这个WAL策略。WAL 技术指的是， MySQL 的写操作并不是立刻写到磁盘上，而是先写日志，然后在合适的时间再写到磁盘上。（**先写内存-->再写日志-->最后更新到磁盘**）所以相对写磁盘来说，是先写的日志，叫write-ahead logging
- **Redis**: 我们再看redis的流程：redis是先执行命令，再更新日志，最后持久化到磁盘。（**执行命令（写内存）-->写AOF日志-->持久化到磁盘**），可以看到，其实没有本质差别的。并不是redis没想到这个策略，应该说它就已经使用了这个策略，那就是在数据持久化到磁盘之前，先写了AOF日志。

### 6. Redis为啥那么快

Redis速度快的原因主要有以下几点：

首先，Redis是**一个内存数据库**，所有的数据都存储在内存中，这大大减少了磁盘I/O的延迟，使得数据读写速度非常快；

其次，**Redis使用单线程模型，避免了线程切换和锁竞争等开销**，能够更高效地利用CPU资源；

此外，**Redis提供了多种高效的数据结构**，如字符串、哈希表、列表、集合等，这些数据结构都经过了**高度优化，能够实现快速的读写操**作；

最后，Redis还使用了**异步I/O模型**，能够**同时处理多个并发请求**，提高了系统的吞吐量。这些因素共同作用，使得Redis在处理大量读写请求时表现出色，成为了一个高性能的键值数据库。



### 7. Redis的主从故障处理流程

转移过程是这样的：哨兵集群持续监控主节点，一旦检测到主节点故障，就会启动故障转移流程。在这个过程中，哨兵会选择一个新的从节点升级为主节点，并更新其他从节点的复制目标，确保数据一致性。同时，客户端也会根据哨兵的指引，切换到新的主节点进行数据操作。这样，Redis系统能够在主节点故障时，快速自动地恢复服务。



### 8. Redis阻塞的原因有哪些？出现阻塞后的排查以及解决方式？



Redis阻塞的原因主要有：

内在原因：

- 数据集中过期：当大量key同时过期时，Redis的主动过期任务会在主线程中执行，导致阻塞。
- CPU饱和：如果Redis的并发量达到极限，或者CPU资源被其他进程过度占用，会导致Redis性能下降甚至阻塞。

外在原因：

- 数据结构或API使用不合理：例如，对大对象执行复杂的命令，如hgetall或del操作大hash对象，可能导致阻塞。
- CPU竞争：进程竞争或绑定CPU可能导致Redis阻塞。
- 网络问题：网络闪断、Redis连接拒绝或连接溢出等问题都可能导致阻塞。

出现阻塞后的排查方式：

- 使用**Redis的慢查询日志功能，记录执行时间超过阈值的命令。**
- 使用**INFO命令查看Redis的运行状态**，如内存使用情况、CPU占用率等。
- 使用**redis-cli的--stat选项实时查看Redis的运行情况**。

解决方式：

- **优化数据结构和命令使用**：避免使用复杂的命令操作大对象，可以考虑将大对象拆分为多个小对象。
- 合理配置Redis参数：如设置合适的过期策略、调整连接池大小等。
- **分布式部署**：通过主从复制、哨兵模式或集群模式实现数据的分布式存储和处理，提高系统的整体性能和可用性。



## 数据类型

### 1. 数据类型有哪些？

String, Set, Sorted Set, Hash, List

> 1. **String（字符串）**：这是Redis最基本的数据类型，一个key对应一个value。它是二进制安全的，可以包含任何数据，如jpg图片或序列化的对象。一个键最大能存储512MB的数据。
> 2. **Hash（哈希）**：Redis的哈希是一个键值对集合，是string类型的field和value的映射表。它特别适合用于存储对象，可以像数据库中更新一个属性一样只修改某一项属性值，而不需要取出整个字符串反序列化成对象修改再序列化存回去。
> 3. **List（列表）**：Redis的列表是简单的字符2串列表，按照插入顺序排序。你可以添加一个元素到列表的头部（左边）或者尾部（右边），它的增删操作非常快，且提供了操作某一段元素的API。
> 4. **Set（集合）**：Redis的Set是string类型的无序集合。它是通过哈希表实现的，添加、删除和查找的时间复杂度都是O(1)。
> 5. **Sorted Set（有序集合）**：与Set相似，但Sorted Set中的每个字符串元素都会关联一个double类型的分数。Redis正是通过分数来为集合中的成员进行从小到大的排序。有序集合的成员是唯一的，但分数(score)可以重复。

除了以上五种基本数据类型，Redis还支持一些其他的数据结构和功能，如Bitmaps（位图）、HyperLogLog（基数统计）、Geospatial（地理位置）和Streams（流）等。

### 1.1 图解数据类型

1.Hash

```
RMap<Object, Object> map = redissonClient.getMap("myFirstMap");
map.put("productId2","100055301");
```

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/2572681-20211006165722044-1203871709.png)

 2.String

```
  RBucket<Object> bucket = redissonClient.getBucket("myString-key");
  bucket.set("{\"userName\":\"test\",\"userPwd\":\"test\",\"email\":\"xxx@163.com\",\"captcha\":\"3552\"}");
```

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/2572681-20211006165853508-1242293398.png)

 

 3.List

```
   List<String> list = redissonClient.getList("listKey-32");
   list.add("listValue1");
   list.add("listValue2");
```

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/2572681-20211006165943709-450769865-20240325205326372.png)

 

 4.Set

```
   RSet<String> set = redissonClient.getSet("setKey-32");
   set.add("setValue");
```

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/2572681-20211006170035024-449447502.png)

 

 5.Zset

```
 RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet("sortedKey-32");
 sortedSet.add(1.0, "zs");
 sortedSet.add(2.0, "lisi");
```

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/2572681-20211006170203428-366565426-20240325205321709.png)

### 2. redis的zset讲一下？

> **解析：** redis的对象考察中，zset是一个设计上比较有特点的数据结。尤其是底层用到的跳表，是面试官喜欢考察的点。回答这个问题可以简单讲一下zset的特点，应用场景，底层数据结构就行。引入这个话题，其他的等面试官细问后再回答。

`zset`（有序集合）是一种特殊的数据结构，它将元素与分数相关联，并根据分数对元素进行排序. 具有：元素唯一，有序性，分数与元素关联，高效的成员查找，高效的范围查询等特点。

典型应用场景：

1. 排行榜：zset常用于构建排行榜，其中分数可以表示用户的得分或其他权重值。
2. 实时热门数据统计：可以将数据的热度、点击量等作为有序集合的分数，通过不断更新分数来实时统计热门数据。
3. 带权重的任务调度：可以将任务的执行时间戳作为有序集合的分数，以实现带权重的任务调度。
4. 数据过期策略：可以将数据的过期时间作为有序集合的分数，根据过期时间自动清理数据。

底层数据结构： Redis的zset底层通过（压缩列表）或者（hash表+跳跃列表）实现。跳跃列表是一种可以进行对数级别查找的数据结构，**通过在每个节点上维护多个指向其他节点的指针，可以快速访问到其他节点。**

### 3. SDS

- Redis 的字符串表示为 `sds` ，而不是 C 字符串（以 `\0` 结尾的 `char*`）。
- 对比 C 字符串，SDS有以下特性：
  - 可以高效地执行长度计算（`strlen`）；
  - 可以高效地执行追加操作（`append`）；
  - 二进制安全；
- `sds` 会为追加操作进行优化：加快追加操作的速度，并降低内存分配的次数，代价是多占用了一些内存，而且这些内存不会被主动释放。

## Redison

### 1.redisson对应的数据结构

RList 操作 Redis 列表

```java
				// RList 继承了 java.util.List 接口
        RList<String> nameList = client.getList("nameList");
        nameList.clear();
        nameList.add("bingo");
```

Rmap操作redis 哈希

```java
        // 默认连接上127.0.0.1:6379
        RedissonClient client = Redisson.create();
        // RMap 继承了 java.util.concurrent.ConcurrentMap 接口
        RMap<String, String> map = client.getMap("personalInfo");
        map.put("name", "yanglbme");
```

使用 RLock 实现 Redis 分布式锁

```java
        // 默认连接上127.0.0.1:6379
        RedissonClient client = Redisson.create();
        // RLock 继承了 java.util.concurrent.locks.Lock 接口
        RLock lock = client.getLock("lock");

        lock.lock();
        System.out.println("lock acquired");
```

RAtomicLong 实现 Redis 原子操作



```
RBucket
RSet
RScoredSortedSet
```

> 1.Hash
>
> ```
> RMap<Object, Object> map = redissonClient.getMap("myFirstMap");
> map.put("productId2","100055301");
> ```
>
> ![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/2572681-20211006165722044-1203871709.png)
>
>  2.String
>
> ```
>   RBucket<Object> bucket = redissonClient.getBucket("myString-key");
>   bucket.set("{\"userName\":\"test\",\"userPwd\":\"test\",\"email\":\"xxx@163.com\",\"captcha\":\"3552\"}");
> ```
>
> ![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/2572681-20211006165853508-1242293398.png)
>
>  
>
>  3.List
>
> ```
>    List<String> list = redissonClient.getList("listKey-32");
>    list.add("listValue1");
>    list.add("listValue2");
> ```
>
> ![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/2572681-20211006165943709-450769865-20240325205326372.png)
>
>  
>
>  4.Set
>
> ```
>    RSet<String> set = redissonClient.getSet("setKey-32");
>    set.add("setValue");
> ```
>
> ![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/2572681-20211006170035024-449447502.png)
>
>  
>
>  5.Zset
>
> ```
>  RScoredSortedSet<String> sortedSet = redissonClient.getScoredSortedSet("sortedKey-32");
>  sortedSet.add(1.0, "zs");
>  sortedSet.add(2.0, "lisi");
> ```
>
> ![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/2572681-20211006170203428-366565426-20240325205321709.png)
