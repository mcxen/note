
## 概述

```sh

                _._                                                  
           _.-``__ ''-._                                             
      _.-``    `.  `_.  ''-._           Redis 5.0.14 (00000000/0) 64 bit
  .-`` .-```.  ```\/    _.,_ ''-._                                   
 (    '      ,       .-`  | `,    )     Running in standalone mode
 |`-._`-...-` __...-.``-._|'` _.-'|     Port: 6379
 |    `-._   `._    /     _.-'    |     PID: 2388
  `-._    `-._  `-./  _.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |           http://redis.io        
  `-._    `-._`-.__.-'_.-'    _.-'                                   
 |`-._`-._    `-.__.-'    _.-'_.-'|                                  
 |    `-._`-._        _.-'_.-'    |                                  
  `-._    `-._`-.__.-'_.-'    _.-'                                   
      `-._    `-.__.-'    _.-'                                       
          `-._        _.-'                                           
              `-.__.-'                                    
```



Redis：REmote DIctionary Server（远程字典服务器）

是完全开源免费的，用C语言编写的，遵守BSD协议，是一个高性能的（Key/Value）分布式内存数据 库，基于内存运行，并支持持久化的NoSQL数据库，是当前最热门的NoSQL数据库之一，也被人们称为数据结构服务器

Redis与其他key-value缓存产品有以下三个特点

- Redis支持数据的持久化，可以将内存中的数据保持在磁盘中，重启的时候可以再次加载进行使用。
- Redis不仅仅支持简单的 key-value 类型的数据，同时还提供list、set、zset、hash等数据结构的存储。
- Redis支持数据的备份，即master-slave模式的数据备份。

## 常用网站

- 官网

  https://redis.io/ 

- 中文网

   http://www.redis.cn 



## 安装Redis

由于企业里面做Redis开发，99%都是Linux版的运用和安装，几乎不会涉及到Windows版，所以这里就以linux版为主，可以自己去测试玩玩，Windows安装及使用教程：https://www.cnblogs.com/xing-nb/p/12146449.html

linux直接去官网下载：https://redis.io/download

安装步骤（基于当时最新版6.2.1）：

1. 下载压缩包，放置Linux的目录下 /opt

2. 在/opt 目录下解压，命令 ： `tar -zxvf redis-6.2.1.tar.gz`

3. 解压完成后出现文件夹：redis-6.2.1

4. 进入目录： `cd redis-6.2.1`

5. 在 redis-6.2.1 目录下执行 `make` 命令

   运行make命令时故意出现的错误解析：

   1. 安装gcc （gcc是linux下的一个编译程序，是c程序的编译工具）

      能上网: yum install gcc-c++

      版本测试: gcc-v

   2. 二次make

   3. Jemalloc/jemalloc.h：没有那个文件或目录

      运行 make distclean 之后再make

   4. Redis Test（可以不用执行）

6. 如果make完成后执行 `make install`

7. 查看默认安装目录：`cd /usr/local/bin`

   /usr 这是一个非常重要的目录，类似于windows下的Program Files，存放用户的程序

   ```sh
   [root@ali redis-5.0.14]# ll
   total 292
   -rw-rw-r--  1 root root 127554 Oct  4  2021 00-RELEASENOTES
   -rw-rw-r--  1 root root     53 Oct  4  2021 BUGS
   -rw-rw-r--  1 root root   2381 Oct  4  2021 CONTRIBUTING
   -rw-rw-r--  1 root root   1487 Oct  4  2021 COPYING
   drwxrwxr-x  6 root root   4096 Mar 20 22:00 deps
   -rw-r--r--  1 root root    107 Mar 20 22:21 dump.rdb
   -rw-rw-r--  1 root root     11 Oct  4  2021 INSTALL
   -rw-rw-r--  1 root root    151 Oct  4  2021 Makefile
   -rw-rw-r--  1 root root   6888 Oct  4  2021 MANIFESTO
   -rw-rw-r--  1 root root  20555 Oct  4  2021 README.md
   -rw-rw-r--  1 root root  63089 Mar 20 22:12 redis.conf
   -rwxrwxr-x  1 root root    275 Oct  4  2021 runtest
   -rwxrwxr-x  1 root root    280 Oct  4  2021 runtest-cluster
   -rwxrwxr-x  1 root root    373 Oct  4  2021 runtest-moduleapi
   -rwxrwxr-x  1 root root    281 Oct  4  2021 runtest-sentinel
   -rw-rw-r--  1 root root   9710 Oct  4  2021 sentinel.conf
   drwxrwxr-x  3 root root   4096 Mar 20 22:09 src
   drwxrwxr-x 11 root root   4096 Oct  4  2021 tests
   drwxrwxr-x  8 root root   4096 Oct  4  2021 utils
   ```

   redis-server在src里，redis.conf在外边

   

8. redis默认不是后台启动，修改文件

   一般我们在 /usr/local/bin 目录下，创建myconfig目录，存放我们的配置文件

   ```bash
   cd /usr/local/bin
   mkdir myconfig   #创建目录
   
   #拷贝配置文件
   cd /opt/redis-6.2.1
   cp redis.conf /usr/local/bin # 拷一个备份，养成良好的习惯，我们就修改这个文件
   # 修改配置保证可以后台应用
   vim redis.conf
   /daemonize   #查找
   :wq    #保存
   ```

   ![image-20210406234601005](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210406234601005.png)

   - A、redis.conf配置文件中daemonize守护线程，默认是NO。
   - B、daemonize是用来指定redis是否要用守护线程的方式启动。

   **daemonize 设置yes或者no区别**

   - daemonize:yes

     redis采用的是单进程多线程的模式。当redis.conf中选项daemonize设置成yes时，代表开启守护进程模式。在该模式下，redis会在后台运行，并将进程pid号写入至redis.conf选项 pidfile设置的文件中，此时redis将一直运行，除非手动kill该进程。

   - daemonize:no

     当daemonize选项设置成no时，当前界面将进入redis的命令行界面，exit强制退出或者关闭连接工具(putty,xshell等)都会导致redis进程退出。
     
     
     
     ### Redis启动测试一下！

   - 启动redis服务

     ```bash
     cd /usr/local/bin
     redis-server myconfig/redis.conf
     ```
     
- redis客户端连接
  
  ```bash
     redis-cli -p 6379
  ```
  
  观察地址的变化，如果连接成功，是直接连上的，redis默认端口号 6379
  
  ```sh
  [root@iZf8z5qdmvb3n8mkuvd98gZ redis-5.0.14]# redis-server redis.conf 
  21639:C 20 Mar 2023 22:13:05.184 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
  21639:C 20 Mar 2023 22:13:05.184 # Redis version=5.0.14, bits=64, commit=00000000, modified=0, pid=21639, just started
  21639:C 20 Mar 2023 22:13:05.184 # Configuration loaded
  ```
  
  我的安装在/usr/local/redis-5.0*
  
  
  
- 执行ping、get和set操作、退出
  

```sh
[root@iZf8z5qdmvb3n8mkuvd98gZ redis-5.0.14]# redis-cli -p 6379
127.0.0.1:6379> ping
PONG
127.0.0.1:6379> get hello
(nil)
127.0.0.1:6379> set hello c
OK
127.0.0.1:6379> get hello
"c"
127.0.0.1:6379> 
```



- 关闭连接

  ```bash
     127.0.0.1:6379> shutdown
     not connected> exit
  ```


可以使用指令`ps -ef|grep redis `显示系统当前redis 进程信息，查看开启和关闭连接的变化

```sh
[root@ali redis-5.0.14]# ps -ef|grep redis
root     21640     1  0 22:13 ?        00:00:00 redis-server 127.0.0.1:6379
root     21809  7864  0 22:15 pts/0    00:00:00 grep --color=auto redis
```

### Redis配置文件

#### 1. 使 Redis 已后台进程的方式启动

编辑配置文件 `/usr/local/redis-5.0.14/redis.conf` 做如下修改：

```shell
daemonize no  --> daemonize yes
```

#### 2. 注释 Redis 绑定连接

编辑配置文件 `/usr/local/redis-5.0.14/redis.conf`，找到如下内容，将其注释掉即可，或者配置指定客户端 IP

```shell
#bind 127.0.0.1
#protected-mode yes
```

#### 3. 设置密码

编辑配置文件 `/usr/local/redis-5.0.14/redis.conf`，找到如下内容，做如下修改：

```shell
requirepass 123456
```

#### 4. 修改 Redis 默认端口

编辑配置文件 `/usr/local/redis-5.0.14/redis.conf`，找到如下内容，修改成系统未使用的端口即可：

```shell
port 6379
```

#### 5. 修改 Redis 进程文件

编辑配置文件 `/usr/local/redis-5.0.14/redis.conf`，找到如下内容，修改进程文件的位置

```shell
pidfile /usr/local/redis-5.0.14/data/redis_6379.pid
```

#### 6. 存储本地数据库时，是否压缩，默认为 yes

编辑配置文件 `/usr/local/redis-5.0.14/redis.conf`，找到如下内容：

```shell
rdbcompression yes
```

#### 7. RDB 存储文件的名称

编辑配置文件 `/usr/local/redis-5.0.14/redis.conf`，找到如下内容：

```shell
dbfilename  redis-6379.rdb
```

#### 8. RDB 数据文件的存储位置，默认为 `./`，注意此处是目录，不是文件

编辑配置文件 `/usr/local/redis-5.0.14/redis.conf`，找到如下内容，修改为：

```shell
dir /usr/local/redis-5.0.14/data/
```

#### 9. 设置 Redis 日志文件路径，注意此处是文件，不是目录

编辑配置文件 `/usr/local/redis/redis-5.0.14.conf`，找到如下内容：

```shell
logfile ""
```

默认为 stdout 标准输出，若后台模式启动则会输出到 `/dev/null` 中，修改后如下：

```shell
logfile redis-5.0.14-6379.log
```

以上均是安装完 Redis 之后修改的一些简单配置，Redis 的配置可以修改 `redis.conf` 文件，也可以通过 `config` 命令实现。

```shell
# 查看配置项
redis 127.0.0.1:6379> config get loglevel
# 查看所有配置项
redis 127.0.0.1:6379> config get *
# 设置配置项
redis 127.0.0.1:6379> config set loglevel "notice"
OK
```



src/redis-server redis.conf 打开redis进程，netstat -tulpn查看端口号

```sh
[root@ali redis-5.0.14]# src/redis-server redis.conf 
2348:C 21 Mar 2023 13:55:43.502 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
2348:C 21 Mar 2023 13:55:43.502 # Redis version=5.0.14, bits=64, commit=00000000, modified=0, pid=2348, just started
2348:C 21 Mar 2023 13:55:43.502 # Configuration loaded

[root@ali redis-5.0.14]# netstat -tulpn
Active Internet connections (only servers)
Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name    
tcp        0      0 127.0.0.1:6379          0.0.0.0:*               LISTEN      2349/src/redis-serv 
tcp        0      0 0.0.0.0:22              0.0.0.0:*               LISTEN      1147/sshd           
tcp6       0      0 :::3306                 :::*                    LISTEN      993/mysqld          
udp        0      0 127.0.0.1:323           0.0.0.0:*                           507/chronyd         
udp        0      0 0.0.0.0:68              0.0.0.0:*                           786/dhclient        
udp6       0      0 ::1:323                 :::*                                507/chronyd 
```



#### Redis.conf 配置参数说明

| 参数名称    | 值                           | 说明                                                         |
| ----------- | ---------------------------- | ------------------------------------------------------------ |
| bind        | 127.0.0.1                    | 绑定的主机地址                                               |
| timeout     | 300                          | 客户端闲置多久后关闭链接，0:表示关闭该功能                   |
| loglevel    | debug/verbose/notice/warning | 日志级别                                                     |
| database    | 10                           | 数据库数量                                                   |
| save        |                              | save 900 10 表示指定多久内有多少次更新就将数据同步到数据文件 |
| slaveof     |                              | 当本机为slaveof时，设置master的ip和port，redis启动时，自动从master上数据同步 |
| masterauth  |                              | 如果master设置了密码，slav链接master的密码                   |
| requirepass | 例：🍐12345                   | 设置密码，设置完后，通过cli连接之后用auth 12345来鉴权        |

### redis压力测试

Redis-benchmark是官方自带的Redis性能测试工具，可以有效的测试Redis服务的性能。

redis 性能测试工具可选参数如下所示：

| 序号 | 选项      | 描述                                       | 默认值    |
| :--- | :-------- | :----------------------------------------- | :-------- |
| 1    | **-h**    | 指定服务器主机名                           | 127.0.0.1 |
| 2    | **-p**    | 指定服务器端口                             | 6379      |
| 3    | **-s**    | 指定服务器 socket                          |           |
| 4    | **-c**    | 指定并发连接数                             | 50        |
| 5    | **-n**    | 指定请求数                                 | 10000     |
| 6    | **-d**    | 以字节的形式指定 SET/GET 值的数据大小      | 2         |
| 7    | **-k**    | 1=keep alive 0=reconnect                   | 1         |
| 8    | **-r**    | SET/GET/INCR 使用随机 key, SADD 使用随机值 |           |
| 9    | **-P**    | 通过管道传输  numreq 请求                  | 1         |
| 10   | **-q**    | 强制退出 redis。仅显示 query/sec 值        |           |
| 11   | **--csv** | 以 CSV 格式输出                            |           |
| 12   | **-l**    | 生成循环，永久执行测试                     |           |
| 13   | **-t**    | 仅运行以逗号分隔的测试命令列表。           |           |
| 14   | **-I**    | Idle 模式。仅打开 N 个 idle 连接并等待。   |           |




```bash
# 测试：100个并发连接，100000个请求，检测host为localhost 端口为6379的redis服务器性能
cd  /usr/local/bin
redis-benchmark -h localhost -p 6379 -c 100 -n 100000
```

参考资料：https://www.runoob.com/redis/redis-benchmarks.html

### 基本数据库常识

默认16个数据库，类似数组下标从零开始，初始默认使用零号库



查看 redis.conf ，里面有默认的配置

```sh
# Set the number of databases. The default database is DB 0, you can select
# a different one on a per-connection basis using SELECT <dbid> where
# dbid is a number between 0 and 'databases'-1
databases 16
```

Select命令切换数据库

```sh
127.0.0.1:6379> select 7
OK
127.0.0.1:6379[7]>
# 不同的库可以存不同的数据
```

Dbsize查看当前数据库的key的数量

```bash
127.0.0.1:6379> select 7
OK
127.0.0.1:6379[7]> DBSIZE
(integer) 0
127.0.0.1:6379[7]> select 0
OK
127.0.0.1:6379> DBSIZE
(integer) 5
127.0.0.1:6379> keys * # 查看具体的key
1) "counter:__rand_int__"
2) "mylist"
3) "k1"
4) "myset:__rand_int__"
5) "key:__rand_int__"
```

Flushdb：清空当前库

Flushall：清空全部的库

```bash
127.0.0.1:6379> DBSIZE
(integer) 5
127.0.0.1:6379> FLUSHDB
OK
127.0.0.1:6379> DBSIZE
(integer) 0
```

## 关于redis的单线程

注：6.x版本有多线程，一般用不到，单线程足够应对

我们首先要明白，Redis很快！官方表示，因为Redis是基于内存的操作，CPU不是Redis的瓶颈，Redis 的瓶颈最有可能是机器内存的大小或者网络带宽。既然单线程容易实现，而且CPU不会成为瓶颈，那就 顺理成章地采用单线程的方案了！

Redis采用的是基于内存的采用的是单进程单线程模型的 KV 数据库，由C语言编写，官方提供的数据是可以达到100000+的QPS（每秒内查询次数）。这个数据不比采用单进程多线程的同样基于内存的 KV 数据库 Memcached 差！

**Redis为什么这么快？**

redis 核心就是 如果我的数据全都在内存里，我单线程的去操作 就是效率最高的，为什么呢，因为 多线程的本质就是 CPU 模拟出来多个线程的情况，这种模拟出来的情况就有一个代价，就是上下文的切 换，对于一个内存的系统来说，它没有上下文的切换就是效率最高的。redis 用 单个CPU 绑定一块内存 的数据，然后针对这块内存的数据进行多次读写的时候，都是在一个CPU上完成的，所以它是单线程处 理这个事。在内存的情况下，这个方案就是最佳方案。

因为一次CPU上下文的切换大概在 1500ns 左右。从内存中读取 1MB 的连续数据，耗时大约为 250us， 假设1MB的数据由多个线程读取了1000次，那么就有1000次时间上下文的切换，那么就有1500ns * 1000 = 1500us ，我单线程的读完1MB数据才250us ,你光时间上下文的切换就用了1500us了，我还不算你每次读一点数据的时间。

## Redis指令与数值类型



| 命令                 | 描述                                                         |
| -------------------- | ------------------------------------------------------------ |
| keys *               | 列出系统中所有的 key                                         |
| dbsize               | Redis 系统所有 key 的个数                                    |
| exists key           | Redis 系统是否存在该 key，存在返回1，否则返回0               |
| del key...           | 删除 Redis 中的 key，删除不存在的key返回0，否则返回key个数   |
| expire key seconds   | 设置 key 的过期时间，单位是秒，pexpire 单位是毫秒            |
| ttl key              | 显示 key 剩余的过期时间，返回 -1 表示没有设置过期时间，-2表示key已删除，pttl，单位是毫秒 |
| persist key          | 去掉 key 的过期时间                                          |
| type key             | 查看 key 的数据类型                                          |
| randomkey            | 随机返回系统中存在的key                                      |
| rename key newKey    | 给 key 重命名                                                |
| renamenx key1 newKey | 给 key 重命名，如果newKey已存在则重命名失败，返回0，否则返回1 |
| move key DBNO        | 将指定的key移动到指定的数据库，redis默认16个库，默认使用 0号库 |



![image-20210407112615913](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210407112615913.png)

全段翻译：

Redis 是一个开源（BSD许可）的，内存中的数据结构存储系统，它可以用作数据库、缓存和消息中间件。 它支持多种类型的数据结构，如 [字符串（strings）](http://www.redis.cn/topics/data-types-intro.html#strings)， [散列（hashes）](http://www.redis.cn/topics/data-types-intro.html#hashes)， [列表（lists）](http://www.redis.cn/topics/data-types-intro.html#lists)， [集合（sets）](http://www.redis.cn/topics/data-types-intro.html#sets)， [有序集合（sorted sets）](http://www.redis.cn/topics/data-types-intro.html#sorted-sets) 与范围查询， [bitmaps](http://www.redis.cn/topics/data-types-intro.html#bitmaps)， [hyperloglogs](http://www.redis.cn/topics/data-types-intro.html#hyperloglogs) 和 [地理空间（geospatial）](http://www.redis.cn/commands/geoadd.html) 索引半径查询。 Redis 内置了 [复制（replication）](http://www.redis.cn/topics/replication.html)，[LUA脚本（Lua scripting）](http://www.redis.cn/commands/eval.html)， [LRU驱动事件（LRU eviction）](http://www.redis.cn/topics/lru-cache.html)，[事务（transactions）](http://www.redis.cn/topics/transactions.html) 和不同级别的 [磁盘持久化（persistence）](http://www.redis.cn/topics/persistence.html)， 并通过 [Redis哨兵（Sentinel）](http://www.redis.cn/topics/sentinel.html)和自动 [分区（Cluster）](http://www.redis.cn/topics/cluster-tutorial.html)提供高可用性（high availability）。

- String （字符串类型）

  String是redis最基本的类型，你可以理解成Memcached一模一样的类型，一个key对应一个value。

  String类型是二进制安全的，意思是redis的string可以包含任何数据，比如jpg图片或者序列化的对象。

  String类型是redis最基本的数据类型，一个redis中字符串value最多可以是512M。

- Hash（哈希，类似 Java里的Map）

  Redis hash 是一个键值对集合。

  Redis hash 是一个String类型的field和value的映射表，hash特别适合用于存储对象。

  类似Java里面的Map

- List（列表）

  Redis列表是简单的字符串列表，按照插入顺序排序，你可以添加一个元素到列表的头部（左边）或者尾部（右边）。它的底层实际是个链表 !

- Set（集合）

  Redis的Set是String类型的无序集合，它是通过HashTable实现的 !

- Zset（sorted set：有序集合）

  Redis zset 和 set 一样，也是String类型元素的集合，且不允许重复的成员。

  不同的是每个元素都会关联一个double类型的分数。

  Redis正是通过分数来为集合中的成员进行从小到大的排序，zset的成员是唯一的，但是分数（Score） 却可以重复。



### Redis键（key）

**字母大写小写都一样**

- keys * 

  查看所有的key

  ```bash
  127.0.0.1:6379> keys *
  (empty list or set)
  127.0.0.1:6379> set name zhiyuan
  OK
  127.0.0.1:6379> keys *
  1) "name"
  ```

- exists key

  判断某个key是否存在

  ```bash
  127.0.0.1:6379> EXISTS name
  (integer) 1
  127.0.0.1:6379> EXISTS name1
  (integer) 0
  ```

- move key db

  移动key到别的库

  ```bash
  127.0.0.1:6379> set name zhiyuan
  OK
  127.0.0.1:6379> get name
  "zhiyuan"
  127.0.0.1:6379> move name 1  #自动到1库
  (integer) 1
  127.0.0.1:6379> keys *  #在本库查不到name
  (empty array)
  127.0.0.1:6379> select 1 #选择1库
  OK
  127.0.0.1:6379[1]> keys * #查询到name
  1) "name"
  ```

- del key

  删除key

  ```bash
  127.0.0.1:6379[1]> del name
  (integer) 1
  127.0.0.1:6379[1]> keys *
  (empty array)
  ```

- expire key 秒钟

  为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删 除。

  - ttl key

    查看还有多少秒过期，-1 表示永不过期，-2 表示已过期

  ```bash
  127.0.0.1:6379> set name zhiyuan
  OK
  127.0.0.1:6379> EXPIRE name 10
  (integer) 1
  127.0.0.1:6379> ttl name
  (integer) 4
  127.0.0.1:6379> ttl name
  (integer) 1
  127.0.0.1:6379> ttl name
  (integer) -2
  127.0.0.1:6379> keys *
  (empty list or set)
  ```

- type key

  查看你的key是什么类型

  ```bash
  127.0.0.1:6379> set name zhiyuan
  OK
  127.0.0.1:6379> get name
  "zhiyuan"
  127.0.0.1:6379> type name
  string
  ```

  

### 字符串String

**单值多Value**

set、get、del

exists（是否存在）、append（追加）、strlen（获取长度）

```bash
127.0.0.1:6379> set key1 value1 # 设置值
OK
127.0.0.1:6379> get key1 # 获得key
"value1"
127.0.0.1:6379> del key1 # 删除key
(integer) 1
127.0.0.1:6379> keys * # 查看全部的key
(empty list or set)
127.0.0.1:6379> exists key1 # 确保 key1 不存在
(integer) 0
127.0.0.1:6379> append key1 "hello" # 对不存在的 key进行APPEND，等同于SET key1 "hello"
(integer) 5 # 字符长度
127.0.0.1:6379> APPEND key1 "-2333" # 对已存在的字符串进行 APPEND
(integer) 10 # 长度从 5 个字符增加到 10 个字符
127.0.0.1:6379> get key1
"hello-2333"
127.0.0.1:6379> STRLEN key1 # # 获取字符串的长度
(integer) 10
```

**incr、decr 一定要是数字才能进行加减，+1 和 -1。**

incrby、decrby 将 key 中储存的数字加上或减去指定的数量。

```bash
127.0.0.1:6379> set views 0 # 设置浏览量为0
OK
127.0.0.1:6379> incr views # 浏览 + 1
(integer) 1
127.0.0.1:6379> incr views # 浏览 + 1
(integer) 2
127.0.0.1:6379> decr views # 浏览 - 1
(integer) 1

127.0.0.1:6379> incrby views 10 # +10
(integer) 11
127.0.0.1:6379> decrby views 10 # -10
(integer) 1
```

range [范围] 

getrange 获取指定区间范围内的值，类似between...and的关系，从零到负一表示全部

```bash
127.0.0.1:6379> set key2 abcd123456 # 设置key2的值
OK
127.0.0.1:6379> getrange key2 0 -1 # 获得全部的值
"abcd123456"
127.0.0.1:6379> getrange key2 0 2 # 截取部分字符串
"abc"
```

setrange 设置指定区间范围内的值，格式是setrange key值 具体值

```bash
127.0.0.1:6379> get key2
"abcd123456"
127.0.0.1:6379> SETRANGE key2 1 xx # 替换值
(integer) 10
127.0.0.1:6379> get key2
"axxd123456"
```

setex（set with expire）设置过期时间

setnx（set if not exist）如何key存在则不覆盖值，还是原来的值（分布式中常用）

```bash
127.0.0.1:6379> setex key3 60 expire # 设置过期时间
OK
127.0.0.1:6379> ttl key3 # 查看剩余的时间
(integer) 55

127.0.0.1:6379> setnx mykey "redis" # 如果不存在就设置，成功返回1
(integer) 1
127.0.0.1:6379> setnx mykey "mongodb" # 如果值存在则不覆盖值，返回0
(integer) 0
127.0.0.1:6379> get mykey
"redis"
```

mset：同时设置一个或多个 key-value 对。

mget：返回所有(一个或多个) key 的值。 如果给定的 key 里面，有某个 key 不存在，则此 key 返回特殊值nil 

msetnx：当所有 key 都成功设置，返回 1 。如果所有给定 key 都设置失败(至少有一个 key 已经存在)，那么返回 0 。相当于原子性操作，要么都成功，要么都不成功。

```bash
127.0.0.1:6379> mset k10 v10 k11 v11 k12 v12
OK
127.0.0.1:6379> keys *
1) "k12"
2) "k11"
3) "k10"

127.0.0.1:6379> mget k10 k11 k12 k13
1) "v10"
2) "v11"
3) "v12"
4) (nil)

127.0.0.1:6379> msetnx k10 v10 k15 v15 # 原子性操作！
(integer) 0
127.0.0.1:6379> get key15
(nil)
```

存储对象：

set user:1 value（json数据）

```bash
127.0.0.1:6379> mset user:1:name zhangsan user:1:age 2
OK
127.0.0.1:6379> mget user:1:name user:1:age
1) "zhangsan"
2) "2"
```

getset：先get再set

```bash
127.0.0.1:6379> getset db mongodb # 没有旧值，返回 nil
(nil)
127.0.0.1:6379> get db
"mongodb"
127.0.0.1:6379> getset db redis # 返回旧值 mongodb
"mongodb"
127.0.0.1:6379> get db
"redis"
```

String数据结构是简单的key-value类型，value其实不仅可以是String，也可以是数字。

常规计数：微博数，粉丝数等。

### 列表List

一般是用来保存用一种类型的信息。

**单值多Value**

可插入重读的值

- Lpush：将一个或多个值插入到列表头部。（LeftPush左）

- Rpush：将一个或多个值插入到列表尾部。（RightPush右）

- lrange：返回列表中指定区间内的元素，区间以偏移量 START 和 END 指定。

- lrange 0 -1 **查看全部list元素**

​	其中 0 表示列表的第一个元素， 1 表示列表的第二个元素，以此类推。

​	使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。

```bash
127.0.0.1:6379> LPUSH list "one"
(integer) 1
127.0.0.1:6379> LPUSH list "two"
(integer) 2
127.0.0.1:6379> RPUSH list "right"
(integer) 3

127.0.0.1:6379> Lrange list 0 -1
1) "two"
2) "one"
3) "right"
127.0.0.1:6379> Lrange list 0 1
1) "two"
2) "one"
```

lpop 命令用于移除并返回列表的第一个元素。当列表 key 不存在时，返回 nil 。 

rpop 移除列表的最后一个元素，返回值为移除的元素。

```bash
127.0.0.1:6379> Lpop list
"two"
127.0.0.1:6379> Rpop list
"right"
127.0.0.1:6379> Lrange list 0 -1
1) "one"
```

Lindex，按照索引下标获得元素（-1代表最后一个，0代表是第一个）

```bash
127.0.0.1:6379> Lindex list 1
(nil)
127.0.0.1:6379> Lindex list 0
"one"
127.0.0.1:6379> Lindex list -1
"one"
```

llen 用于返回列表的长度。

```bash
127.0.0.1:6379> flushdb
OK
127.0.0.1:6379> Lpush list "one"
(integer) 1
127.0.0.1:6379> Lpush list "two"
(integer) 2
127.0.0.1:6379> Lpush list "three"
(integer) 3
127.0.0.1:6379> Llen list # 返回列表的长度
(integer) 3
```

 lrem （lrem key count element）根据参数 COUNT 的值，移除列表中与参数 VALUE 相等的元素。

如果有多个一样的lement，则删除列表最前面的的

```bash
127.0.0.1:6379> lrem list 1 "two"
(integer) 1
127.0.0.1:6379> Lrange list 0 -1
1) "three"
2) "one"
```

Ltrim key 对一个列表进行修剪(trim)，只保留指定列表中区间内的元素，不在指定区间之内的元素都将被删除。

```bash
127.0.0.1:6379> RPUSH mylist "hello" "hello" "hello2" "hello3"
(integer) 4
127.0.0.1:6379> ltrim mylist 1 2
OK
127.0.0.1:6379> lrange mylist 0 -1
1) "hello"
2) "hello2"
```

rpoplpush 移除列表的最后一个元素，并将该元素添加到另一个列表并返回。

```bash
127.0.0.1:6379> rpush mylist "hello"
(integer) 1
127.0.0.1:6379> rpush mylist "foo"
(integer) 2
127.0.0.1:6379> rpush mylist "bar"
(integer) 3
127.0.0.1:6379> rpoplpush mylist myotherlist
"bar"
127.0.0.1:6379> lrange mylist 0 -1
1) "hello"
2) "foo"
127.0.0.1:6379> lrange myotherlist 0 -1
1) "bar"
```

lset key index value ，将列表 key 下标为 index 的元素的值设置为 value 。

```bash
127.0.0.1:6379> exists list # 对空列表(key 不存在)进行 LSET
(integer) 0
127.0.0.1:6379> lset list 0 item # 报错
(error) ERR no such key

127.0.0.1:6379> lpush list "value1" # 对非空列表进行 LSET
(integer) 1
127.0.0.1:6379> lrange list 0 0
1) "value1"
127.0.0.1:6379> lset list 0 "new" # 更新值
OK
127.0.0.1:6379> lrange list 0 0
1) "new"
127.0.0.1:6379> lset list 1 "new" # index 超出范围报错
(error) ERR index out of range
```

linsert key before/after pivot value，用于在列表的元素前或者后插入元素。

将值 value 插入到列表 key 当中，位于值 pivot 之前或之后。

如果pivot有多个，则插入最前面的那个

```bash
127.0.0.1:6379> RPUSH mylist "Hello"
(integer) 1
127.0.0.1:6379> RPUSH mylist "world"
(integer) 2
127.0.0.1:6379> lrange mylist 0 -1
1) "Hello"
2) "world"

127.0.0.1:6379> LINSERT mylist BEFORE "world" "There"
(integer) 3
127.0.0.1:6379> lrange mylist 0 -1
1) "Hello"
2) "There"
3) "world"
```



**性能总结：**

- 它是一个字符串链表，left，right 都可以插入添加
- 如果键不存在，创建新的链表
- 如果键已存在，新增内容
- 如果值全移除，对应的键也就消失了
- 链表的操作无论是头和尾效率都极高，但假如是对中间元素进行操作，效率就很惨淡了。

list就是链表，略有数据结构知识的人都应该能理解其结构。使用Lists结构，我们可以轻松地实现最新消息排行等功能。List的另一个应用就是消息队列，可以利用List的PUSH操作，将任务存在List中，然后工作线程再用POP操作将任务取出进行执行。Redis还提供了操作List中某一段的api，你可以直接查询，删除List中某一段的元素。

Redis的list是每个子元素都是String类型的双向链表，可以通过push和pop操作从列表的头部或者尾部添加或者删除元素，这样List即可以作为栈，也可以作为队列。

### 集合Set-无序不重复集合

**单值多value**



无序不重复集合

sadd 将一个或多个成员元素加入到集合中，不能重复 

smembers 返回集合中的所有的成员。 

sismember 命令判断成员元素是否是集合的成员。

```bash
127.0.0.1:6379> sadd myset "hello"
(integer) 1
127.0.0.1:6379> sadd myset "zhiyuan"
(integer) 1
127.0.0.1:6379> sadd myset "zhiyuan" # 重复值不插入 返回0
(integer) 0
127.0.0.1:6379> SMEMBERS myset #查看集合中所有成员
1) "zhiyuan"
2) "hello"
127.0.0.1:6379> SISMEMBER myset "hello" #是否是此集合的成员 是反正1
(integer) 1
127.0.0.1:6379> SISMEMBER myset "world"
(integer) 0
```

scard，获取集合里面的元素个数

```bash
127.0.0.1:6379> scard myset
(integer) 2
```

srem key value 用于移除集合中的一个或多个成员元素

```bash
127.0.0.1:6379> srem myset "zhiyuan"
(integer) 1
127.0.0.1:6379> SMEMBERS myset
1) "hello"
```

srandmember key 用于返回集合中随机元素。后面加上数字，则随机返回对应数量的成员，默认一个

```bash
127.0.0.1:6379> SMEMBERS myset
1) "zhiyuan"
2) "world"
3) "hello"
127.0.0.1:6379> SRANDMEMBER myset
"hello"
127.0.0.1:6379> SRANDMEMBER myset 2
1) "world"
2) "zhiyuan"
127.0.0.1:6379> SRANDMEMBER myset 2
1) "zhiyuan"
2) "hello"
```

spop key [count] 用于移除指定 key 集合的随机元素，不填则默认一个。

```bash
127.0.0.1:6379> SMEMBERS myset
1) "zhiyuan"
2) "world"
3) "hello"
127.0.0.1:6379> spop myset
"world"
127.0.0.1:6379> spop myset 2
1) "zhiyuan"
2) "hello"
```

 smove SOURCE DESTINATION MEMBER， 将指定成员 member 元素从 source 集合移动到 destination 集合

```bash
127.0.0.1:6379> sadd myset "hello" #myset 添加元素
(integer) 1
127.0.0.1:6379> sadd myset "world"
(integer) 1
127.0.0.1:6379> sadd myset "zhiyuan"
(integer) 1
127.0.0.1:6379> sadd myset2 "set2" #myset2 添加元素
(integer) 1
127.0.0.1:6379> smove myset myset2 "zhiyuan"
(integer) 1
127.0.0.1:6379> SMEMBERS myset
1) "world"
2) "hello"
127.0.0.1:6379> SMEMBERS myset2
1) "zhiyuan"
2) "set2"
```

数字集合类：

- 差集： sdiff
- 交集： sinter
- 并集： sunion

```bash
127.0.0.1:6379> sadd key1 "a" # key1
(integer) 1
127.0.0.1:6379> sadd key1 "b"
(integer) 1
127.0.0.1:6379> sadd key1 "c"
(integer) 1
127.0.0.1:6379> sadd key2 "c" # key2
(integer) 1
127.0.0.1:6379> sadd key2 "d"
(integer) 1
127.0.0.1:6379> sadd key2 "e"
(integer) 1
127.0.0.1:6379> SDIFF key1 key2 # 差集
1) "a"
2) "b"
127.0.0.1:6379> SINTER key1 key2 # 交集
1) "c"
127.0.0.1:6379> SUNION key1 key2 # 并集
1) "a"
2) "b"
3) "c"
4) "e"
5) "d"
```

在微博应用中，可以将一个用户所有的关注人存在一个集合中，将其所有粉丝存在一个集合。Redis还为集合提供了求交集、并集、差集等操作，可以非常方便的实现如共同关注、共同喜好、二度好友等功能，对上面的所有集合操作，你还可以使用不同的命令选择将结果返回给客户端还是存集到一个新的集合中。

### 哈希Hash-存储结构

**k-v模式不变，但V是一个键值对**

hset、hget 命令用于为哈希表中的字段赋值

hmset、hmget 同时将多个field-value对设置到哈希表中。会覆盖哈希表中已存在的字段

>  Redis 4.0.0开始弃用HMSET，请使用HSET

hgetall 用于返回哈希表中，所有的字段和值。

hdel 用于删除哈希表 key 中的一个或多个指定字段

```bash
127.0.0.1:6379>  hset myhash field1 "zhiyuan"
(integer) 1
127.0.0.1:6379> hget myhash field1
"zhiyuan"

127.0.0.1:6379> HSET myhash field1 "Hello" field2 "World"
(integer) 2
127.0.0.1:6379> hgetall myhash
1) "field1"
2) "Hello"
3) "field2"
4) "World"
127.0.0.1:6379> HGET myhash field1
"Hello"
127.0.0.1:6379> HGET myhash field2
"World"

127.0.0.1:6379> HDEL myhash field1
(integer) 1
127.0.0.1:6379> hgetall myhash
1) "field2"
2) "World"

```

hlen 获取哈希表中字段的数量

```bash
127.0.0.1:6379> hlen myhash
(integer) 1
127.0.0.1:6379> HSET myhash field1 "Hello" field2 "World"
OK
127.0.0.1:6379> hlen myhash
(integer) 2
```

hexists 查看哈希表的指定字段是否存在

```bash
127.0.0.1:6379> hexists myhash field1
(integer) 1
127.0.0.1:6379> hexists myhash field3
(integer) 0
```

hkeys 获取哈希表中的所有域（field）

hvals 返回哈希表所有域(field)的值

```bash
127.0.0.1:6379> HKEYS myhash
1) "field2"
2) "field1"
127.0.0.1:6379> HVALS myhash
1) "World"
2) "Hello"
```

hincrby 为哈希表中的字段值加上指定增量值

```bash
127.0.0.1:6379> hset myhash field 5
(integer) 1
127.0.0.1:6379> HINCRBY myhash field 1
(integer) 6
127.0.0.1:6379> HINCRBY myhash field -1
(integer) 5
127.0.0.1:6379> HINCRBY myhash field -10
(integer) -5
```

hsetnx 为哈希表中不存在的的字段赋值 ，存在则不赋值

```bash
127.0.0.1:6379> HSETNX myhash field1 "hello"
(integer) 1 # 设置成功，返回 1 。
127.0.0.1:6379> HSETNX myhash field1 "world"
(integer) 0 # 如果给定字段已经存在，返回 0 。
127.0.0.1:6379> HGET myhash field1
"hello"
```

Redis hash是一个string类型的field和value的映射表，hash特别适合用于存储对象。 存储部分变更的数据，如用户信息等。

### 有序集合Zset

在set基础上，加一个score值。之前set是k1 v1 v2 v3，现在zset是 k1 score1 v1 score2 v2



zadd 将一个或多个成员元素及其分数值加入到有序集当中。

zrange 返回有序集中，指定区间内的成员

```bash
127.0.0.1:6379> zadd myset 1 "one"
(integer) 1
127.0.0.1:6379> zadd myset 2 "two" 3 "three"
(integer) 2
127.0.0.1:6379> ZRANGE myset 0 -1
1) "one"
2) "two"
3) "three"
```

zrangebyscore 返回有序集合中指定分数区间的成员列表。有序集成员按分数值递增(从小到大) 次序排列。

ZREVRANGE 从大到小

```bash
127.0.0.1:6379> zadd salary 2500 xiaoming
(integer) 1
127.0.0.1:6379> zadd salary 5000 xiaohong
(integer) 1
127.0.0.1:6379> zadd salary 500 kuangshen
(integer) 1

# Inf无穷大量+∞,同样地,-∞可以表示为-Inf。
127.0.0.1:6379> ZRANGEBYSCORE salary -inf +inf # 显示整个有序集
1) "zhiyuan"
2) "xiaoming"
3) "xiaohong"
127.0.0.1:6379> ZRANGEBYSCORE salary -inf +inf withscores # 递增排列
1) "zhiyuan"
2) "500"
3) "xiaoming"
4) "2500"
5) "xiaohong"
6) "5000"
127.0.0.1:6379> ZREVRANGE salary 0 -1 WITHSCORES # 递减排列
1) "xiaohong"
2) "5000"
3) "xiaoming"
4) "2500"
5) "zhiyuan"
6) "500"
# 显示工资 <=2500的所有成员
127.0.0.1:6379> ZRANGEBYSCORE salary -inf 2500 WITHSCORES 
1) "zhiyuan"
2) "500"
3) "xiaoming"
4) "2500"
```

zrem 移除有序集中的一个或多个成员

```bash
127.0.0.1:6379> ZRANGE salary 0 -1
1) "zhiyuan"
2) "xiaoming"
3) "xiaohong"
127.0.0.1:6379> zrem salary zhiyuan
(integer) 1
127.0.0.1:6379> ZRANGE salary 0 -1
1) "xiaoming"
2) "xiaohong"
```

zcard 命令用于计算集合中元素的数量

```bash
127.0.0.1:6379> zcard salary
(integer) 2
OK
```

zcount 计算有序集合中指定分数区间的成员数量。

```bash
127.0.0.1:6379> zadd myset 1 "hello"
(integer) 1
127.0.0.1:6379> zadd myset 2 "world" 3 "zhiyuan"
(integer) 2
127.0.0.1:6379> ZCOUNT myset 1 3
(integer) 3
127.0.0.1:6379> ZCOUNT myset 1 2
(integer) 2
```

zrank 返回有序集中指定成员的**排名**。其中有序集成员按分数值递增(从小到大)顺序排列。

```bash
127.0.0.1:6379> zadd salary 2500 xiaoming
(integer) 1
127.0.0.1:6379> zadd salary 5000 xiaohong
(integer) 1
127.0.0.1:6379> zadd salary 500 kuangshen
(integer) 1
127.0.0.1:6379> ZRANGE salary 0 -1 WITHSCORES # 显示所有成员及其 score 值
1) "kuangshen"
2) "500"
3) "xiaoming"
4) "2500"
5) "xiaohong"
6) "5000"
127.0.0.1:6379> zrank salary kuangshen # 显示 kuangshen 的薪水排名，最少
(integer) 0
127.0.0.1:6379> zrank salary xiaohong # 显示 xiaohong 的薪水排名，第三
(integer) 2
```

zrevrank 返回有序集中成员的排名。其中有序集成员按分数值递减(从大到小)排序

```bash
127.0.0.1:6379> ZREVRANK salary kuangshen # 狂神第三
(integer) 2
127.0.0.1:6379> ZREVRANK salary xiaohong # 小红第一
(integer) 0
```

和set相比，sorted set增加了一个权重参数score，使得集合中的元素能够按score进行有序排列，比如 一个存储全班同学成绩的sorted set，其集合value可以是同学的学号，而score就可以是其考试得分， 这样在数据插入集合的时候，就已经进行了天然的排序。可以用sorted set来做带权重的队列，比如普通消息的score为1，重要消息的score为2，然后工作线程可以选择按score的倒序来获取工作任务。让 重要的任务优先执行。



## 三种特殊数据类型-基本用的少

### GEO地理位置

#### 简介

Redis 的 GEO 特性在 Redis 3.2 版本中推出， 这个功能可以将用户给定的地理位置信息储存起来， 并对这些信息进行操作。来实现诸如附近位置、摇一摇这类依赖于地理位置信息的功能。geo的数据类型为 zset。

GEO 的数据结构总共有六个常用命令：geoadd、geopos、geodist、georadius、 georadiusbymember、gethash

官方文档：https://www.redis.net.cn/order/3685.html

#### 常用指令

**1、geoadd**

语法：geoadd key longitude latitude member ...

将给定的空间元素(经度、纬度、名字)添加到指定的键里面。
这些数据会以有序集的形式被储存在键里面，从而使得georadius和georadiusbymember这样的
命令可以在之后通过位置查询取得这些元素。
geoadd命令以标准的x,y格式接受参数，所以用户必须先输入经度，然后再输入纬度。
geoadd能够记录的坐标是有限的：非常接近两极的区域无法被索引。
有效的经度介于-180-180度之间，有效的纬度介于-85.05112878 度至 85.05112878 度之间
当用户尝试输入一个超出范围的经度或者纬度时,geoadd命令将返回一个错误。

测试：百度搜索经纬度查询，模拟真实数据

```bash
127.0.0.1:6379> geoadd china:city 116.23 40.22 北京
(integer) 1
127.0.0.1:6379> geoadd china:city 121.48 31.40 上海 113.88 22.55 深圳 120.21 30.20 杭州
(integer) 3
127.0.0.1:6379> geoadd china:city 106.54 29.40 重庆 108.93 34.23 西安 114.02 30.58 武汉
(integer) 3
```

**2、geopos**

语法：geopos key member [member...]

从key里返回所有给定位置元素的位置（经度和纬度）

```bash
127.0.0.1:6379> geopos china:city 北京
1) 1) "116.23000055551528931"
2) "40.2200010338739844"
127.0.0.1:6379> geopos china:city 上海 重庆
1) 1) "121.48000091314315796"
2) "31.40000025319353938"
2) 1) "106.54000014066696167"
2) "29.39999880018641676"
127.0.0.1:6379> geopos china:city 新疆
1) (nil)
```

**3、geodist**

语法：geodist key member1 member2 [unit]

返回两个给定位置之间的距离，如果两个位置之间的其中一个不存在，那么命令返回空值

指定单位的参数unit必须是以下单位的其中一个：

- m表示单位为米
- km表示单位为千米
- mi表示单位为英里
- ft表示单位为英尺

如果用户没有显式地指定单位参数，那么geodist默认使用 米 作为单位。

geodist命令在计算距离时会假设地球为完美的球形，在极限情况下，这一假设最大会造成0.5%的误差。

```bash
127.0.0.1:6379> geodist china:city 北京 上海
"1088785.4302"
127.0.0.1:6379> geodist china:city 北京 上海 km
"1088.7854"
127.0.0.1:6379> geodist china:city 重庆 北京 km
"1491.6716"
```

**4、georadius**

语法：

```bash
georadius key longitude latitude radius m|km|ft|mi [withcoord][withdist] [withhash][asc|desc][count count]
```

以给定的经纬度为中心， 找出某一半径内的元素

重新连接 redis-cli，增加参数 --raw ，可以强制输出中文，不然会乱码

```bash
127.0.0.1:6379> georadius china:city 100 30 1000 km #乱码
1) "\xe9\x87\x8d\xe5\xba\x86"
2) "\xe8\xa5\xbf\xe5\xae\x89"
127.0.0.1:6379> exit
[root@localhost bin]#  redis-cli --raw -p 6379
# 在 china:city 中寻找坐标 100 30 半径为 1000km 的城市
127.0.0.1:6379> georadius china:city 100 30 1000 km
重庆
西安

```

 withdist 返回位置名称和中心距离

```bash
127.0.0.1:6379> georadius china:city 100 30 1000 km withdist
重庆
635.2850
西安
963.3171
```

withcoord 返回位置名称、经纬度

```bash
127.0.0.1:6379> georadius china:city 100 30 1000 km withcoord
重庆
106.54000014066696167
29.39999880018641676
西安
108.92999857664108276
34.23000121926852302
```

withdist withcoord 返回位置名称、距离、经纬度，count 限定寻找个数

```bash
127.0.0.1:6379> georadius china:city 100 30 1000 km withcoord withdist count 1
重庆
635.2850
106.54000014066696167
29.39999880018641676
127.0.0.1:6379> georadius china:city 100 30 1000 km withcoord withdist count 2
重庆
635.2850
106.54000014066696167
29.39999880018641676
西安
963.3171
108.92999857664108276
34.23000121926852302
```

**5、georadiusbymember**

语法：

```bash
georadiusbymember key member radius m|km|ft|mi [withcoord][withdist] [withhash][asc|desc][count count]
```

找出位于指定范围内的元素，中心点是由给定的位置元素决定

```bash
127.0.0.1:6379> GEORADIUSBYMEMBER china:city 北京 1000 km
北京
西安
127.0.0.1:6379> GEORADIUSBYMEMBER china:city 上海 400 km
杭州
上海
```

**6、geohash**

语法：geohash key member [member...]

Redis使用geohash将二维经纬度转换为一维字符串，字符串越长表示位置更精确，两个字符串越相似表示距离越近。

```bash
127.0.0.1:6379> geohash china:city 北京 重庆
wx4sucu47r0
wm5z22h53v0
127.0.0.1:6379> geohash china:city 北京 上海
wx4sucu47r0
wtw6sk5n300
```

**zrem**

GEO没有提供删除成员的命令，但是因为GEO的底层实现是zset，所以可以借用zrem命令实现对地理位置信息的删除。

```bash
127.0.0.1:6379> geoadd china:city 116.23 40.22 beijing
1
127.0.0.1:6379> zrange china:city 0 -1 # 查看全部的元素
重庆
西安
深圳
武汉
杭州
上海
beijing
北京
127.0.0.1:6379> zrem china:city beijing # 移除元素
1
127.0.0.1:6379> zrem china:city 北京 # 移除元素
1
127.0.0.1:6379> zrange china:city 0 -1
重庆
西安
深圳
武汉
杭州
上海
```

### HyperLogLog

Redis 在 2.8.9 版本添加了 HyperLogLog 结构。

Redis HyperLogLog 是用来做基数统计的算法，HyperLogLog 的优点是，在输入元素的数量或者体积非常非常大时，计算基数所需的空间总是固定的、并且是很小的。

在 Redis 里面，每个 HyperLogLog 键只需要花费 12 KB 内存，就可以计算接近 2^64 个不同元素的基数。这和计算基数时，元素越多耗费内存就越多的集合形成鲜明对比。

HyperLogLog则是一种算法，它提供了不精确的去重计数方案。

举个栗子：假如我要统计网页的UV（浏览用户数量，一天内同一个用户多次访问只能算一次），传统的解决方案是使用Set来保存用户id，然后统计Set中的元素数量来获取页面UV。但这种方案只能承载少量用户，一旦用户数量大起来就需要消耗大量的空间来存储用户id。我的目的是统计用户数量而不是保存用户，这简直是个吃力不讨好的方案！而使用Redis的HyperLogLog最多需要12k就可以统计大量的用户数，尽管它大概有0.81%的错误率，但对于统计UV这种不需要很精确的数据是可以忽略不计的。



**什么是基数？**

比如数据集 {1, 3, 5, 7, 5, 7, 8}， 那么这个数据集的基数集为 {1, 3, 5 ,7, 8}, 基数(不重复元素的数量)为5。

基数估计就是在误差可接受的范围内，快速计算基数。



**基本命令**

| 序号 | 命令及描述                                                   |
| :--- | :----------------------------------------------------------- |
| 1    | [PFADD key element [element ...\]](https://www.runoob.com/redis/hyperloglog-pfadd.html) <br>添加指定元素到 HyperLogLog 中。 |
| 2    | [PFCOUNT key [key ...\]](https://www.runoob.com/redis/hyperloglog-pfcount.html) <br/>返回给定 HyperLogLog 的基数估算值。 |
| 3    | [PFMERGE destkey sourcekey [sourcekey ...\]](https://www.runoob.com/redis/hyperloglog-pfmerge.html) <br/>将多个 HyperLogLog 合并为一个 HyperLogLog，并集计算 |

```bash
127.0.0.1:6379> PFADD mykey a b c d e f g h i j
1
127.0.0.1:6379> PFCOUNT mykey
10
127.0.0.1:6379> PFADD mykey2 i j z x c v b n m
1
127.0.0.1:6379> PFMERGE mykey3 mykey mykey2
OK
127.0.0.1:6379> PFCOUNT mykey3
15
```

### BitMap

#### 简介

在开发中，可能会遇到这种情况：需要统计用户的某些信息，如活跃或不活跃，登录或者不登录；又如 需要记录用户一年的打卡情况，打卡了是1， 没有打卡是0，如果使用普通的 key/value存储，则要记录 365条记录，如果用户量很大，需要的空间也会很大，所以 Redis 提供了 Bitmap 位图这中数据结构， Bitmap 就是通过操作二进制位来进行记录，即为 0 和 1；如果要记录 365 天的打卡情况，使用 Bitmap 表示的形式大概如下：0101000111000111...........................，这样有什么好处呢？当然就是节约内存 了，365 天相当于 365 bit，又 1 字节 = 8 bit , 所以相当于使用 46 个字节即可。

BitMap 就是通过一个 bit 位来表示某个元素对应的值或者状态, 其中的 key 就是对应元素本身，实际上底层也是通过对字符串的操作来实现。Redis 从 2.2 版本之后新增了setbit, getbit, bitcount 等几个 bitmap 相关命令。

#### **操作**

**1、setbit 设置操作**

SETBIT key offset value : 设置 key 的第 offset 位为value (1或0)

使用 bitmap 来记录上述事例中一周的打卡记录如下所示：

周一：1，周二：0，周三：0，周四：1，周五：1，周六：0，周天：0 （1 为打卡，0 为不打卡）

```bash
127.0.0.1:6379> setbit sign 0 1
0
127.0.0.1:6379> setbit sign 1 0
0
127.0.0.1:6379> setbit sign 2 0
0
127.0.0.1:6379> setbit sign 3 1
0
127.0.0.1:6379> setbit sign 4 1
0
127.0.0.1:6379> setbit sign 5 0
0
127.0.0.1:6379> setbit sign 6 0
0
```

**2、getbit 获取操作**

GETBIT key offset 获取offset设置的值，未设置过默认返回0

```bash
127.0.0.1:6379> getbit sign 3 # 查看周四是否打卡
1
127.0.0.1:6379> getbit sign 6 # 查看周七是否打卡
0
```

**3、bitcount 统计操作**

bitcount key [start, end] 统计 key 上位为1的个数

统计这周打卡的记录，可以看到只有3天是打卡的状态：

```bash
127.0.0.1:6379> bitcount sign
3
```

## Redis事务

### 理论

**Redis事务的概念：**

Redis 事务的本质是一组命令的集合。事务支持一次执行多个命令，一个事务中所有命令都会被序列化。在事务执行过程，会按照顺序串行化执行队列中的命令，其他客户端提交的命令请求不会插入到事 务执行命令序列中。

总结说：redis事务就是一次性、顺序性、排他性的执行一个队列中的一系列命令。

**Redis事务没有隔离级别的概念：**

批量操作在发送 EXEC 命令前被放入队列缓存，并不会被实际执行！

**Redis不保证原子性：**

Redis中，单条命令是原子性执行的，但事务不保证原子性，且没有回滚。事务中任意命令执行失败，其 余的命令仍会被执行。

**Redis事务的三个阶段：**

- 开始事务
- 命令入队
- 执行事务

Redis事务相关命令：

| 序号 | 命令及描述                                                   |
| :--- | :----------------------------------------------------------- |
| 1    | DISCARD <br/>取消事务，放弃执行事务块内的所有命令。          |
| 2    | EXEC <br/>执行所有事务块内的命令。                           |
| 3    | MULTI<br/>标记一个事务块的开始。                             |
| 4    | UNWATCH<br/>取消 WATCH 命令对所有 key 的监视。               |
| 5    | WATCH key [key ...]<br>监视一个(或多个) key ，如果在事务执行之前这个(或这些) key 被其他命令所改动，那么事务将被打断。（ 类似乐观锁 ） |

### 实践

正常执行

![image-20210408101936847](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210408101936847.png)

放弃事务

![image-20210408101955501](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210408101955501.png)

若在事务队列中存在命令性错误（类似于java编译性错误），则执行EXEC命令时，所有命令都不会执行

![image-20210408102023204](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210408102023204.png)

若在事务队列中存在语法性错误（类似于java的1/0的运行时异常），则执行EXEC命令时，其他正确命令会被执行，错误命令抛出异常。

![image-20210408102051072](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210408102051072.png)

**Watch 监控**

- 悲观锁：

  悲观锁(Pessimistic Lock)，顾名思义，就是很悲观，每次去拿数据的时候都认为别人会修改，所以每次在拿数据的时候都会上锁，这样别人想拿到这个数据就会block直到它拿到锁。传统的关系型数据库里面就用到了很多这种锁机制，比如行锁，表锁等，读锁，写锁等，都是在操作之前先上锁。

- 乐观锁：

  乐观锁(Optimistic Lock)，顾名思义，就是很乐观，每次去拿数据的时候都认为别人不会修改，所以不会 锁。但是在更新的时候会判断一下再此期间别人有没有去更新这个数据，可以使用版本号等机制，乐观锁适用于多读的应用类型，这样可以提高吞吐量，乐观锁策略：提交版本必须大于记录当前版本才能 执行更新。

测试：

1、初始化信用卡可用余额和欠额

```bash
127.0.0.1:6379> set balance 100
OK
127.0.0.1:6379> set debt 0
OK
```

2、使用watch检测balance，事务期间balance数据未变动，事务执行成功

```bash
127.0.0.1:6379> watch balance
OK
127.0.0.1:6379> MULTI	#开启事务
OK
127.0.0.1:6379> decrby balance 20	#可用余额-20
QUEUED
127.0.0.1:6379> incrby debt 20	#欠款+20
QUEUED
127.0.0.1:6379> exec	#执行事务
1) (integer) 80
2) (integer) 20
```

3、使用watch检测balance，若事务期间balance数据变动，事务执行失败！

```bash
# 窗口一
127.0.0.1:6379> watch balance	#监视balance
OK
127.0.0.1:6379> MULTI # 执行完毕后，执行窗口二代码测试
OK
127.0.0.1:6379> decrby balance 20
QUEUED
127.0.0.1:6379> incrby debt 20
QUEUED
127.0.0.1:6379> exec # 修改失败！因为被监视的balance值改变
(nil)

```

窗口二

```bash
# 窗口二
127.0.0.1:6379> get balance
"80"
127.0.0.1:6379> set balance 200
OK
```

窗口一：出现问题后放弃监视，然后重来！

```bash
127.0.0.1:6379> UNWATCH # 放弃监视，这是取消所有的监视
OK
127.0.0.1:6379> watch balance	#监视
OK
127.0.0.1:6379> MULTI	#事务
OK
127.0.0.1:6379> decrby balance 20
QUEUED
127.0.0.1:6379> incrby debt 20
QUEUED
127.0.0.1:6379> exec # 成功！
1) (integer) 180
2) (integer) 40
```

说明：

一但执行 EXEC 开启事务的执行后，无论事务使用执行成功， WARCH 对变量的监控都将被取消。

故当事务执行失败后，需重新执行WATCH命令对变量进行监控，并开启新的事务进行操作。

### 小结

watch指令类似于乐观锁，在事务提交时，如果watch监控的多个KEY中任何KEY的值已经被其他客户端更改，则使用EXEC执行事务时，事务队列将不会被执行，同时返回Nullmulti-bulk应答以通知调用者事务执行失败。

## java操作redis-Jedis

> 开放防火墙：
>
> ```
> [root@ali redis-5.0.14]# firewall-cmd --zone=public --add-port=6379/tcp --permanent
> firewall-cmd --reload
> ```
>
> 

Jedis是Redis官方推荐的Java连接开发工具。要在Java开发中使用好Redis中间件，必须对Jedis熟悉才能 写成漂亮的代码

### 测试ping

前提打开了redis服务。

1、新建一个普通的Maven项目

2、导入redis的依赖！

```xml
<dependencies>
    <!-- https://mvnrepository.com/artifact/redis.clients/jedis -->
    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
        <version>3.5.2</version>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <version>1.2.75</version>
    </dependency>
</dependencies>
```

报错原因：

![image-20230321215128654](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230321215128654.png)

3、编写测试代码

```java
public class Ping {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //查看服务是否运行
        System.out.println(jedis.ping());
    }
}
```

### 常用API

基本操作

```java
public static void main(String[] args) {
    Jedis jedis = new Jedis("127.0.0.1", 6379);

    //验证密码，如果没有设置密码这段代码省略
    // jedis.auth("password");

    jedis.connect(); //连接
    jedis.disconnect(); //断开连接
    jedis.flushAll(); //清空所有的key
}
```

对key操作的命令

```java
public class TestKey {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        
        System.out.println("清空数据：" + jedis.flushDB());
        System.out.println("判断某个键是否存在：" + jedis.exists("username"));
        System.out.println("新增<'username','zhiyuan'>的键值对：" + jedis.set("username", "zhiyuan"));
        System.out.println("新增<'password','password'>的键值对：" + jedis.set("password", "password"));

        System.out.print("系统中所有的键如下：");
        Set<String> keys = jedis.keys("*");
        System.out.println(keys);

        System.out.println("删除键password:" + jedis.del("password"));
        System.out.println("判断键password是否存在：" + jedis.exists("password"));
        System.out.println("查看键username所存储的值的类型：" + jedis.type("username"));
        System.out.println("随机返回key空间的一个：" + jedis.randomKey());
        System.out.println("重命名key：" + jedis.rename("username", "name"));
        System.out.println("取出改后的name：" + jedis.get("name"));
        System.out.println("按索引查询：" + jedis.select(0));
        System.out.println("删除当前选择数据库中的所有key：" + jedis.flushDB());
        System.out.println("返回当前数据库中key的数目：" + jedis.dbSize());
        System.out.println("删除所有数据库中的所有key：" + jedis.flushAll());
    }
}
```

对String操作的命令

```java
public class TestString {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);

        jedis.flushDB();
        System.out.println("===========增加数据===========");
        System.out.println(jedis.set("key1", "value1"));
        System.out.println(jedis.set("key2", "value2"));
        System.out.println(jedis.set("key3", "value3"));

        System.out.println("删除键key2:" + jedis.del("key2"));
        System.out.println("获取键key2:" + jedis.get("key2"));
        System.out.println("修改key1:" + jedis.set("key1", "value1Changed"));
        System.out.println("获取key1的值：" + jedis.get("key1"));
        System.out.println("在key3后面加入值：" + jedis.append("key3", "End"));
        System.out.println("key3的值：" + jedis.get("key3"));
        System.out.println("增加多个键值对：" + jedis.mset("key01", "value01", "key02", "value02", "key03", "value03"));
        System.out.println("获取多个键值对：" + jedis.mget("key01", "key02", "key03"));
        System.out.println("获取多个键值对：" + jedis.mget("key01", "key02", "key03", "key04"));
        System.out.println("删除多个键值对：" + jedis.del("key01", "key02"));
        System.out.println("获取多个键值对：" + jedis.mget("key01", "key02", "key03"));

        jedis.flushDB();

        System.out.println("===========新增键值对防止覆盖原先值==============");
        System.out.println(jedis.setnx("key1", "value1"));
        System.out.println(jedis.setnx("key2", "value2"));
        System.out.println(jedis.setnx("key2", "value2-new"));
        System.out.println(jedis.get("key1"));
        System.out.println(jedis.get("key2"));
        System.out.println("===========新增键值对并设置有效时间=============");
        System.out.println(jedis.setex("key3", 2, "value3"));
        System.out.println(jedis.get("key3"));
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(jedis.get("key3"));
        System.out.println("===========获取原值，更新为新值==========");
        System.out.println(jedis.getSet("key2", "key2GetSet"));
        System.out.println(jedis.get("key2"));
        System.out.println("获得key2的值的字串：" + jedis.getrange("key2", 2,4));

    }
}
```

对List操作命令

```java
public class TestList {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.flushDB();
        System.out.println("===========添加一个list===========");
        jedis.lpush("collections", "ArrayList", "Vector", "Stack", "HashMap", "WeakHashMap", "LinkedHashMap");
        jedis.lpush("collections", "HashSet");
        jedis.lpush("collections", "TreeSet");
        jedis.lpush("collections", "TreeMap");
        System.out.println("collections的内容：" + jedis.lrange("collections", 0, -1));//-1代表倒数第一个元素，-2代表倒数第二个元素,end为-1表示查询全部
        System.out.println("collections区间0-3的元素：" + jedis.lrange("collections", 0, 3));
        System.out.println("===============================");
        // 删除列表指定的值 ，第二个参数为删除的个数（有重复时），后add进去的值先被删，类似于出栈
        System.out.println("删除指定元素个数：" + jedis.lrem("collections", 2,
                "HashMap"));
        System.out.println("collections的内容：" + jedis.lrange("collections",
                0, -1));
        System.out.println("删除下表0-3区间之外的元素：" + jedis.ltrim("collections", 0, 3));
        System.out.println("collections的内容：" + jedis.lrange("collections",
                0, -1));
        System.out.println("collections列表出栈（左端）：" + jedis.lpop("collections"));
        System.out.println("collections的内容：" + jedis.lrange("collections", 0, -1));
        System.out.println("collections添加元素，从列表右端，与lpush相对应：" + jedis.rpush("collections", "EnumMap"));
        System.out.println("collections的内容：" + jedis.lrange("collections",
                0, -1));
        System.out.println("collections列表出栈（右端）：" + jedis.rpop("collections"));
        System.out.println("collections的内容：" + jedis.lrange("collections",
                0, -1));
        System.out.println("修改collections指定下标1的内容：" + jedis.lset("collections", 1, "LinkedArrayList"));
        System.out.println("collections的内容：" + jedis.lrange("collections",
                0, -1));
        System.out.println("===============================");
        System.out.println("collections的长度：" + jedis.llen("collections"));
        System.out.println("获取collections下标为2的元素：" + jedis.lindex("collections", 2));
        System.out.println("===============================");
        jedis.lpush("sortedList", "3", "6", "2", "0", "7", "4");
        System.out.println("sortedList排序前：" + jedis.lrange("sortedList", 0,
                -1));
        System.out.println(jedis.sort("sortedList"));
        System.out.println("sortedList排序后：" + jedis.lrange("sortedList", 0, -1));

    }
}
```

对Set的操作命令

```java
public class TestSet {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.flushDB();
        System.out.println("============向集合中添加元素（不重复）============");
        System.out.println(jedis.sadd("eleSet", "e1", "e2", "e4", "e3", "e0", "e8", "e7", "e5"));
        System.out.println(jedis.sadd("eleSet", "e6"));
        System.out.println(jedis.sadd("eleSet", "e6"));
        System.out.println("eleSet的所有元素为：" + jedis.smembers("eleSet"));
        System.out.println("删除一个元素e0：" + jedis.srem("eleSet", "e0"));

        System.out.println("eleSet的所有元素为：" + jedis.smembers("eleSet"));
        System.out.println("删除两个元素e7和e6：" + jedis.srem("eleSet", "e7", "e6"));
        System.out.println("eleSet的所有元素为：" + jedis.smembers("eleSet"));
        System.out.println("随机的移除集合中的一个元素：" + jedis.spop("eleSet"));
        System.out.println("随机的移除集合中的一个元素：" + jedis.spop("eleSet"));
        System.out.println("eleSet的所有元素为：" + jedis.smembers("eleSet"));
        System.out.println("eleSet中包含元素的个数：" + jedis.scard("eleSet"));
        System.out.println("e3是否在eleSet中：" + jedis.sismember("eleSet", "e3"));
        System.out.println("e1是否在eleSet中：" + jedis.sismember("eleSet", "e1"));
        System.out.println("e1是否在eleSet中：" + jedis.sismember("eleSet", "e5"));
        System.out.println("=================================");
        System.out.println(jedis.sadd("eleSet1", "e1", "e2", "e4", "e3", "e0", "e8", "e7", "e5"));
        System.out.println(jedis.sadd("eleSet2","e1", "e2", "e4", "e3", "e0", "e8"));
        System.out.println("将eleSet1中删除e1并存入eleSet3中：" + jedis.smove("eleSet1", "eleSet3", "e1"));//移到集合元素
        System.out.println("将eleSet1中删除e2并存入eleSet3中：" + jedis.smove("eleSet1", "eleSet3", "e2"));
        System.out.println("eleSet1中的元素：" + jedis.smembers("eleSet1"));
        System.out.println("eleSet3中的元素：" + jedis.smembers("eleSet3"));
        System.out.println("============集合运算=================");
        System.out.println("eleSet1中的元素：" + jedis.smembers("eleSet1"));
        System.out.println("eleSet2中的元素：" + jedis.smembers("eleSet2"));
        System.out.println("eleSet1和eleSet2的交集:" + jedis.sinter("eleSet1", "eleSet2"));
        System.out.println("eleSet1和eleSet2的并集:" + jedis.sunion("eleSet1", "eleSet2"));
        System.out.println("eleSet1和eleSet2的差集:" + jedis.sdiff("eleSet1", "eleSet2"));//eleSet1中有，eleSet2中没有
        jedis.sinterstore("eleSet4", "eleSet1", "eleSet2");//求交集并将交集保存到dstkey的集合
        System.out.println("eleSet4中的元素：" + jedis.smembers("eleSet4"));
    }
}
```

对Hash的操作命令

```java
public class TestHash {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.flushDB();
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        map.put("key4", "value4");
        //添加名称为hash（key）的hash元素
        jedis.hmset("hash", map);
        //向名称为hash的hash中添加key为key5，value为value5元素
        jedis.hset("hash", "key5", "value5");
        System.out.println("散列hash的所有键值对为：" + jedis.hgetAll("hash"));//return Map<String,String>

        System.out.println("散列hash的所有键为：" + jedis.hkeys("hash"));//returnSet<String>
        System.out.println("散列hash的所有值为：" + jedis.hvals("hash"));//returnList<String>
        System.out.println("将key6保存的值加上一个整数，如果key6不存在则添加key6：" + jedis.hincrBy("hash", "key6", 6));
        System.out.println("散列hash的所有键值对为：" + jedis.hgetAll("hash"));
        System.out.println("将key6保存的值加上一个整数，如果key6不存在则添加key6：" + jedis.hincrBy("hash", "key6", 3));
        System.out.println("散列hash的所有键值对为：" + jedis.hgetAll("hash"));
        System.out.println("删除一个或者多个键值对：" + jedis.hdel("hash", "key2"));
        System.out.println("散列hash的所有键值对为：" + jedis.hgetAll("hash"));
        System.out.println("散列hash中键值对的个数：" + jedis.hlen("hash"));
        System.out.println("判断hash中是否存在key2：" + jedis.hexists("hash", "key2"));
        System.out.println("判断hash中是否存在key3：" + jedis.hexists("hash", "key3"));
        System.out.println("获取hash中的值：" + jedis.hmget("hash", "key3"));
        System.out.println("获取hash中的值：" + jedis.hmget("hash", "key3", "key4"));

    }
}
```

### 事务

```java
public class TestMulti {
    public static void main(String[] args) {
        //创建客户端连接服务端，redis服务端需要被开启
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.flushDB();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hello", "world");
        jsonObject.put("name", "java");
        //开启事务
        Transaction multi = jedis.multi();
        String result = jsonObject.toJSONString();
        try {
            //向redis存入一条数据
            multi.set("json", result);
            //再存入一条数据
            multi.set("json2", result);
            //这里引发了异常，用0作为被除数
            int i = 100 / 0;
            //如果没有引发异常，执行进入队列的命令
            multi.exec();
        } catch (Exception e) {
            e.printStackTrace();
            //如果出现异常，回滚
            multi.discard();
        } finally {
            System.out.println(jedis.get("json"));
            System.out.println(jedis.get("json2"));
            //最终关闭客户端
            jedis.close();
        }
    }
}
```

## SpringBoot整合

### 基础使用

**概述：**

在SpringBoot中一般使用RedisTemplate提供的方法来操作Redis。那么使用SpringBoot整合Redis需要 那些步骤呢。

1、 JedisPoolConfig (这个是配置连接池)

2、 RedisConnectionFactory 这个是配置连接信息，这里的RedisConnectionFactory是一个接口，我们需要使用它的实现类，在SpringD Data Redis方案中提供了以下四种工厂模型：

- JredisConnectionFactory
- JedisConnectionFactory
- LettuceConnectionFactory
- SrpConnectionFactory

3、 RedisTemplate 基本操作



**导入依赖**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

说明：在springboot2.x之后，原来使用的jedis被替换成lettuce



**yaml配置**

```yml
spring:
  redis:
    host: 127.0.0.1
    port: 6379
```

**测试类中测试**

```java
@Autowired
private RedisTemplate<String, String> redisTemplate;

@Test
void contextLoads() {
    redisTemplate.opsForValue().set("myKey", "myValue");
    System.out.println(redisTemplate.opsForValue().get("myKey"));
}
```



### 序列化config

创建springboot新项目，安装上面步骤导入依赖



1、分析 RedisAutoConfiguration 自动配置类

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(RedisProperties.class)
@Import({ LettuceConnectionConfiguration.class, JedisConnectionConfiguration.class })
public class RedisAutoConfiguration {

   @Bean
   @ConditionalOnMissingBean(name = "redisTemplate")
   @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
   public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
      RedisTemplate<Object, Object> template = new RedisTemplate<>();
      template.setConnectionFactory(redisConnectionFactory);
      return template;
   }

   @Bean
   @ConditionalOnMissingBean
   @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
   public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
      StringRedisTemplate template = new StringRedisTemplate();
      template.setConnectionFactory(redisConnectionFactory);
      return template;
   }

}
```



通过源码可以看出，SpringBoot自动帮我们在容器中生成了一个RedisTemplate和一个StringRedisTemplate。

但是，这个RedisTemplate的泛型是，写代码不方便，需要写好多类型转换的代码；我们需要一个泛型为形式的RedisTemplate。

并且，这个RedisTemplate没有设置数据存在Redis时，key及value的序列化方式。

看到这个@ConditionalOnMissingBean注解后，就知道如果Spring容器中有了RedisTemplate对象了， 这个自动配置的RedisTemplate不会实例化。因此我们可以直接自己写个配置类，配置 RedisTemplate。

用这个配置我们不可以存储对象，否则会报SerializationException，大家可自己试试

2、既然自动配置不好用，就重新配置一个RedisTemplate

```java
package com.oddfar.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL); //此方法已过期
        //新方法
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();

        return template;
    }
}
```

创建User对象，name和age

测试存对象

```java
@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void test() {
        User user = new User("致远",3);
        redisTemplate.opsForValue().set("user",user);
        System.out.println(redisTemplate.opsForValue().get("user"));

    }
}
```



### redis工具类

使用RedisTemplate需要频繁调用`.opForxxx`然后才能进行对应的操作，这样使用起来代码效率低下，工作中一般不会这样使用，而是将这些常用的公共API抽取出来封装成为一个工具类，然后直接使用工具类来间接操作Redis,不但效率高并且易用。

工具类参考博客：

https://www.cnblogs.com/zeng1994/p/03303c805731afc9aa9c60dbbd32a323.html

https://www.cnblogs.com/zhzhlong/p/11434284.html



## Redis.conf

### 熟悉基本配置

> 位置

Redis 的配置文件位于 Redis 安装目录下，文件名为 redis.conf

```bash
config get * # 获取全部的配置
```

我们一般情况下，会单独拷贝出来一份进行操作。来保证初始文件的安全。

正如在 `安装redis` 中的讲解中拷贝一份

> 容量单位不区分大小写，G和GB有区别

![image-20210408213939472](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210408213939472.png)

>  include 组合多个配置

![image-20210408214037264](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210408214037264.png)

和Spring配置文件类似，可以通过includes包含，redis.conf 可以作为总文件，可以包含其他文件！

> NETWORK 网络配置

```bash
bind 127.0.0.1 # 绑定的ip
protected-mode yes # 保护模式
port 6379 # 默认端口
```

> GENERAL 通用

```bash
daemonize yes # 默认情况下，Redis不作为守护进程运行。需要开启的话，改为 yes

supervised no # 可通过upstart和systemd管理Redis守护进程

loglevel notice # 日志级别。可选项有：
				# debug（记录大量日志信息，适用于开发、测试阶段）
				# verbose（较多日志信息）
				# notice（适量日志信息，使用于生产环境）
				# warning（仅有部分重要、关键信息才会被记录）
logfile "" # 日志文件的位置，当指定为空字符串时，为标准输出
databases 16 # 设置数据库的数目。默认的数据库是DB 0
always-show-logo yes # 是否总是显示logo
```

> SNAPSHOPTING 快照，持久化规则

由于Redis是基于内存的数据库，需要将数据由内存持久化到文件中

持久化方式：

- RDB
- AOF

```bash
# 900秒（15分钟）内至少1个key值改变（则进行数据库保存--持久化）
save 900 1
# 300秒（5分钟）内至少10个key值改变（则进行数据库保存--持久化）
save 300 10
# 60秒（1分钟）内至少10000个key值改变（则进行数据库保存--持久化）
save 60 10000
```

RDB文件相关

```bash
stop-writes-on-bgsave-error yes # 持久化出现错误后，是否依然进行继续进行工作

rdbcompression yes # 使用压缩rdb文件 yes：压缩，但是需要一些cpu的消耗。no：不压缩，需要更多的磁盘空间

rdbchecksum yes # 是否校验rdb文件，更有利于文件的容错性，但是在保存rdb文件的时候，会有大概10%的性能损耗

dbfilename dump.rdb # dbfilenamerdb文件名称

dir ./ # dir 数据目录，数据库的写入会在这个目录。rdb、aof文件也会写在这个目录
```

> REPLICATION主从复制

![image-20210408214742862](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210408214742862.png)

后面详细说

> SECURITY安全

访问密码的查看，设置和取消

```bash
# 启动redis
# 连接客户端

# 获得和设置密码
config get requirepass
config set requirepass "123456"

#测试ping，发现需要验证
127.0.0.1:6379> ping
NOAUTH Authentication required.
# 验证
127.0.0.1:6379> auth 123456
OK
127.0.0.1:6379> ping
PONG
```

>  客户端连接相关

```bash
maxclients 10000  最大客户端数量
maxmemory <bytes> 最大内存限制
maxmemory-policy noeviction # 内存达到限制值的处理策略
```

**maxmemory-policy 六种方式**

**1、volatile-lru：**利用LRU算法移除设置过过期时间的key。

**2、allkeys-lru ：** 用lru算法删除lkey

**3、volatile-random：**随机删除即将过期key

**4、allkeys-random：**随机删除

**5、volatile-ttl ：** 删除即将过期的

**6、noeviction ：** 不移除任何key，只是返回一个写错误。

redis 中的**默认**的过期策略是 **volatile-lru** 。

**设置方式**

```bash
config set maxmemory-policy volatile-lru 
```

>  append only模式 

（AOF相关部分）

![在这里插入图片描述](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/20200513215037918.png)

![在这里插入图片描述](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/20200513215047999.png)

```bash
appendfsync everysec # appendfsync aof持久化策略的配置
        # no表示不执行fsync，由操作系统保证数据同步到磁盘，速度最快。
        # always表示每次写入都执行fsync，以保证数据同步到磁盘。
        # everysec表示每秒执行一次fsync，可能会导致丢失这1s数据。
```

## Redis的持久化

Redis 是内存数据库，如果不将内存中的数据库状态保存到磁盘，那么一旦服务器进程退出，服务器中的数据库状态也会消失。所以 Redis 提供了持久化功能！

**Rdb 保存的是 dump.rdb 文件**



### RDB（Redis DataBase）

> 什么是RDB

在指定的时间间隔内将内存中的数据集快照写入磁盘，也就是行话讲的Snapshot快照，它恢复时是将快 照文件直接读到内存里。

Redis会单独创建（fork）一个子进程来进行持久化，会先将数据写入到一个临时文件中，待持久化过程都结束了，再用这个临时文件替换上次持久化好的文件。整个过程中，主进程是不进行任何IO操作的。 这就确保了极高的性能。如果需要进行大规模数据的恢复，且对于数据恢复的完整性不是非常敏感，那 RDB方式要比AOF方式更加的高效。RDB的缺点是最后一次持久化后的数据可能丢失。

> Fork

Fork的作用是复制一个与当前进程一样的进程。新进程的所有数据（变量，环境变量，程序计数器等） 数值都和原进程一致，但是是一个全新的进程，并作为原进程的子进程。



**配置位置及SNAPSHOTTING解析**

![image-20210408224901985](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210408224901985.png)

这里的触发条件机制，我们可以修改测试一下：

```bash
save 120 10 # 120秒内修改10次则触发RDB
```

RDB 是整合内存的压缩过的Snapshot，RDB 的数据结构，可以配置复合的快照触发条件。

如果想禁用RDB持久化的策略，只要不设置任何save指令，或者给save传入一个空字符串参数也可以。

若要修改完毕需要立马生效，可以手动使用 save 命令！立马生效 !

save不是创建一个新进程去进行持久化

> 其余命令解析

Stop-writes-on-bgsave-error：如果配置为no，表示你不在乎数据不一致或者有其他的手段发现和控制，默认为yes。

rbdcompression：对于存储到磁盘中的快照，可以设置是否进行压缩存储。如果是的话，redis会采用 LZF算法进行压缩，如果你不想消耗CPU来进行压缩的话，可以设置为关闭此功能。

rdbchecksum：在存储快照后，还可以让redis使用CRC64算法来进行数据校验，但是这样做会增加大约 10%的性能消耗，如果希望获取到最大的性能提升，可以关闭此功能。默认为yes。

> 如何触发RDB快照

1、配置文件中默认的快照配置，建议多用一台机子作为备份，复制一份 dump.rdb

2、命令save或者是bgsave

- save 时只管保存，其他不管，全部阻塞

- bgsave，Redis 会在后台异步进行快照操作，快照同时还可以响应客户端请求。可以通过lastsave
  命令获取最后一次成功执行快照的时间。

3、执行flushall命令，也会产生 dump.rdb 文件，但里面是空的，无意义 !

4、退出的时候也会产生 dump.rdb 文件！

> 如何恢复

1、将备份文件（dump.rdb）移动到redis安装目录并启动服务即可 

2、CONFIG GET dir 获取目录

```bash
127.0.0.1:6379> config get dir
dir
/usr/local/bin
```

> 优点和缺点

**优点：**

1、适合大规模的数据恢复

2、对数据完整性和一致性要求不高

**缺点：**

1、在一定间隔时间做一次备份，所以如果redis意外down掉的话，就会丢失最后一次快照后的所有修改

2、Fork的时候，内存中的数据被克隆了一份，大致2倍的膨胀性需要考虑。

> 小结

![image-20210408225404338](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210408225404338.png)

### AOF（Append Only File）

> 简介

以日志的形式来记录每个写操作，将Redis执行过的所有指令记录下来（读操作不记录），只许追加文件但不可以改写文件，redis启动之初会读取该文件重新构建数据，换言之，redis重启的话就根据日志文件 的内容将写指令从前到后执行一次以完成数据的恢复工作

**Aof保存的是 appendonly.aof 文件**

> 配置

![image-20210408225620719](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210408225620719.png)

- appendonly no

  是否以append only模式作为持久化方式，默认使用的是rdb方式持久化，这 种方式在许多应用中已经足够用了

- appendfilename "appendonly.aof"

  appendfilename AOF 文件名称

- appendfsync everysec

  appendfsync aof持久化策略的配置

  > no表示不执行fsync，由操作系统保证数据同步到磁盘，速度最快。
  > always表示每次写入都执行fsync，以保证数据同步到磁盘。
  > everysec表示每秒执行一次fsync，可能会导致丢失这1s数据。

- No-appendfsync-on-rewrite

  重写时是否可以运用Appendfsync，用默认no即可，保证数据安全性

- Auto-aof-rewrite-min-size

  设置重写的基准值

- Auto-aof-rewrite-percentage

  设置重写的基准值

> AOF 启动/修复/恢复

**正常恢复：**

启动：设置Yes，修改默认的appendonly no，改为yes
将有数据的aof文件复制一份保存到对应目录（config get dir）
恢复：重启redis然后重新加载

**异常恢复：**

启动：设置Yes
故意破坏 appendonly.aof 文件！
修复：命令`redis-check-aof --fix appendonly.aof` 进行修复
恢复：重启 redis 然后重新加载

> Rewrite重写

AOF 采用文件追加方式，文件会越来越大，为避免出现此种情况，新增了重写机制，当AOF文件的大小超过所设定的阈值时，Redis 就会启动AOF 文件的内容压缩，只保留可以恢复数据的最小指令集，可以 使用命令 bgrewriteaof ！

**重写原理：**

AOF 文件持续增长而过大时，会fork出一条新进程来将文件重写（也是先写临时文件最后再 rename），遍历新进程的内存中数据，每条记录有一条的Set语句。重写aof文件的操作，并没有读取旧 的aof文件，这点和快照有点类似！

**触发机制：**

Redis会记录上次重写时的AOF大小，默认配置是当AOF文件大小是上次rewrite后大小的已被且文件大 于64M的触发。

> 优点和缺点

**优点：**

1、每修改同步：appendfsync always 同步持久化，每次发生数据变更会被立即记录到磁盘，性能较差但数据完整性比较好

2、每秒同步： appendfsync everysec 异步操作，每秒记录 ，如果一秒内宕机，有数据丢失

3、不同步： appendfsync no 从不同步

**缺点：**

1、相同数据集的数据而言，aof 文件要远大于 rdb文件，恢复速度慢于 rdb。

2、Aof 运行效率要慢于 rdb，每秒同步策略效率较好，不同步效率和rdb相同。

> 小结

![image-20210408230339879](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210408230339879.png)

### 总结

1、RDB 持久化方式能够在指定的时间间隔内对你的数据进行快照存储

2、AOF 持久化方式记录每次对服务器写的操作，当服务器重启的时候会重新执行这些命令来恢复原始的数据，AOF命令以Redis 协议追加保存每次写的操作到文件末尾，Redis还能对AOF文件进行后台重写，使得AOF文件的体积不至于过大。

3、只做缓存，如果你只希望你的数据在服务器运行的时候存在，你也可以不使用任何持久化

4、同时开启两种持久化方式

- 在这种情况下，当redis重启的时候会优先载入AOF文件来恢复原始的数据，因为在通常情况下AOF 文件保存的数据集要比RDB文件保存的数据集要完整。
- RDB 的数据不实时，同时使用两者时服务器重启也只会找AOF文件，那要不要只使用AOF呢？建议不要，因为RDB更适合用于备份数据库（AOF在不断变化不好备份），快速重启，而且不会有 AOF可能潜在的Bug，留着作为一个万一的手段。

5、性能建议

- 因为RDB文件只用作后备用途，建议只在Slave上持久化RDB文件，而且只要15分钟备份一次就够 了，只保留 save 900 1 这条规则。
- 如果Enable AOF ，好处是在最恶劣情况下也只会丢失不超过两秒数据，启动脚本较简单只load自 己的AOF文件就可以了，代价一是带来了持续的IO，二是AOF rewrite 的最后将 rewrite 过程中产 生的新数据写到新文件造成的阻塞几乎是不可避免的。只要硬盘许可，应该尽量减少AOF rewrite 的频率，AOF重写的基础大小默认值64M太小了，可以设到5G以上，默认超过原大小100%大小重 写可以改到适当的数值。
- 如果不Enable AOF ，仅靠 Master-Slave Repllcation 实现高可用性也可以，能省掉一大笔IO，也 减少了rewrite时带来的系统波动。代价是如果Master/Slave 同时倒掉，会丢失十几分钟的数据， 启动脚本也要比较两个 Master/Slave 中的 RDB文件，载入较新的那个，微博就是这种架构。

## Redis 发布订阅

### 简介

Redis 发布订阅(pub/sub)是一种消息通信模式：发送者(pub)发送消息，订阅者(sub)接收消息。

Redis 客户端可以订阅任意数量的频道。

订阅/发布消息图：

![image-20210409105838259](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210409105838259.png)

下图展示了频道 channel1 ， 以及订阅这个频道的三个客户端 —— client2 、 client5 和 client1 之间的关系：

![image-20210409105859670](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210409105859670.png)

当有新消息通过 PUBLISH 命令发送给频道 channel1 时， 这个消息就会被发送给订阅它的三个客户端：

![image-20210409110314032](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210409110314032.png)

**命令**

下表列出了 redis 发布订阅常用命令：

| 序号 | 命令及描述                                                   |
| :--- | :----------------------------------------------------------- |
| 1    | [PSUBSCRIBE pattern [pattern ...]]<br>订阅一个或多个符合给定模式的频道。 |
| 2    | [PUBSUB subcommand [argument [argument ...]]]<br/>查看订阅与发布系统状态。 |
| 3    | [PUBLISH channel message] <br/>将信息发送到指定的频道。      |
| 4    | [PUNSUBSCRIBE [pattern [pattern ...]]] <br/>退订所有给定模式的频道。 |
| 5    | [SUBSCRIBE channel [channel ...]]<br/>订阅给定的一个或多个频道的信息。 |
| 6    | [UNSUBSCRIBE [channel [channel ...]]]<br/>指退订给定的频道。 |

### 测试

以下实例演示了发布订阅是如何工作的。

我们先打开两个 redis-cli 客户端

在**第一个 redis-cli 客户端**，创建订阅频道名为 redisChat，输入SUBSCRIBE redisChat

```bash
redis 127.0.0.1:6379> SUBSCRIBE redisChat
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "redisChat"
3) (integer) 1
```

在**第二个客户端**，发布两次消息，订阅者就能接收 到消息。

```bash
redis 127.0.0.1:6379> PUBLISH redisChat "Hello,Redis"
(integer) 1
redis 127.0.0.1:6379> PUBLISH redisChat "Hello，java"
(integer) 1
```

订阅者的客户端会显示如下消息

> 1) "message"
> 2) "redisChat"
> 3) "Hello,Redis"
> 4) "message"
> 5) "redisChat"
> 6) "Hello，java"

### 原理

Redis是使用C实现的，通过分析 Redis 源码里的 pubsub.c 文件，了解发布和订阅机制的底层实现，来加深对 Redis 的理解。

Redis 通过 PUBLISH 、SUBSCRIBE 和 PSUBSCRIBE 等命令实现发布和订阅功能。

通过 SUBSCRIBE 命令订阅某频道后，redis-server 里维护了一个字典，字典的键就是一个个 channel ，而字典的值则是一个链表，链表中保存了所有订阅这个 channel 的客户端。SUBSCRIBE 命令的关键，就是将客户端添加到给定 channel 的订阅链表中。

通过 PUBLISH 命令向订阅者发送消息，redis-server 会使用给定的频道作为键，在它所维护的 channel 字典中查找记录了订阅这个频道的所有客户端的链表，遍历这个链表，将消息发布给所有订阅者。

Pub/Sub 从字面上理解就是发布（Publish）与订阅（Subscribe），在Redis中，你可以设定对某一个 key值进行消息发布及消息订阅，当一个key值上进行了消息发布后，所有订阅它的客户端都会收到相应的消息。这一功能最明显的用法就是用作实时消息系统，比如普通的即时聊天，群聊等功能。

使用场景：

Redis的Pub/Sub系统可以构建实时的消息系统，比如很多用Pub/Sub构建的实时聊天系统的例子。

## Redis主从复制

### 概念

主从复制，是指将一台Redis服务器的数据，复制到其他的Redis服务器。前者称为主节点 (master/leader)，后者称为从节点(slave/follower)；数据的复制是单向的，只能由主节点到从节点。 Master以写为主，Slave 以读为主。

默认情况下，每台Redis服务器都是主节点；且一个主节点可以有多个从节点(或没有从节点)，但一个从节点只能有一个主节点。

主从复制的作用主要包括：

1、数据冗余：主从复制实现了数据的热备份，是持久化之外的一种数据冗余方式。

2、故障恢复：当主节点出现问题时，可以由从节点提供服务，实现快速的故障恢复；实际上是一种服务的冗余。

3、负载均衡：在主从复制的基础上，配合读写分离，可以由主节点提供写服务，由从节点提供读服务 （即写Redis数据时应用连接主节点，读Redis数据时应用连接从节点），分担服务器负载；尤其是在写少读多的场景下，通过多个从节点分担读负载，可以大大提高Redis服务器的并发量。

4、高可用基石：除了上述作用以外，主从复制还是哨兵和集群能够实施的基础，因此说主从复制是 Redis高可用的基础。



一般来说，要将Redis运用于工程项目中，只使用一台Redis是万万不能的，原因如下：

1、从结构上，单个Redis服务器会发生单点故障，并且一台服务器需要处理所有的请求负载，压力较 大；

2、从容量上，单个Redis服务器内存容量有限，就算一台Redis服务器内存容量为256G，也不能将所有内存用作Redis存储内存，一般来说，单台Redis最大使用内存不应该超过20G。

电商网站上的商品，一般都是一次上传，无数次浏览的，说专业点也就是"多读少写"。

对于这种场景，我们可以使如下这种架构：

![image-20210409112108209](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210409112108209.png)

### 环境配置

> 基本配置



查看当前库的信息：`info replication`

```bash
127.0.0.1:6379> info replication
# Replication
role:master	# 角色
connected_slaves:0	# 从机数量
master_failover_state:no-failover
master_replid:1a6933acf7ec9711bfa0a1848976676557e1e6a0
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:0
second_repl_offset:-1
repl_backlog_active:0
repl_backlog_size:1048576
repl_backlog_first_byte_offset:0
repl_backlog_histlen:0
127.0.0.1:6379> 
```

因为没有多个服务器，就以本地开启3个端口，模拟3个服务

既然需要启动多个服务，就需要多个配置文件。每个配置文件对应修改以下信息：

- 端口号（port）
- pid文件名（pidfile）
- 日志文件名（logfile）
- rdb文件名（dbfilename）

1、拷贝多个redis.conf 文件

端口分别是6379、6380、6381

```bash
[root@localhost ~]# cd /usr/local/bin/myconfig
[root@localhost myconfig]# ls
dump.rdb  redis.conf
[root@localhost myconfig]# cp redis.conf redis79.conf
[root@localhost myconfig]# cp redis.conf redis80.conf
[root@localhost myconfig]# cp redis.conf redis81.conf
[root@localhost myconfig]# ls
dump.rdb  redis79.conf  redis80.conf  redis81.conf  redis.conf
```

分别修改配置上面四点对应的配置，举例：

![image-20210409115138265](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210409115138265.png)

![image-20210409115114373](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210409115114373.png)

![image-20210409115241761](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210409115241761.png)



配置好分别启动3个不同端口服务

- redis-server myconfig/redis79.conf 

- redis-server myconfig/redis80.conf

- redis-server myconfig/redis81.conf

redis-server myconfig/redis79.conf

![在这里插入图片描述](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/20200513215610163.png)

### 一主二从

1、之后我们再分别开启redis连接，redis-cli -p 6379，redis-cli -p 6380，redis-cli -p 6381

通过指令 

```bash
127.0.0.1:6379> info replication
```

可以发现，默认情况下，开启的每个redis服务器都是主节点

![image-20210409134833129](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210409134833129.png)



2、配置为一个Master 两个Slave（即一主二从）

6379为主，6380,6381为从

slaveof 127.0.0.1 6379

![image-20210409134929416](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210409134929416.png)

3、在主机设置值，在从机都可以取到！从机不能写值！

![image-20210409135325865](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210409135325865.png)

我们这里是使用命令搭建，是“暂时的”，也可去配置里进行修改，这样话则是“永久的”

![image-20210409135633320](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210409135633320.png)

> 使用规则

当主机断电宕机后，默认情况下从机的角色不会发生变化 ，集群中只是失去了写操作，当主机恢复以后，又会连接上从机恢复原状。

当从机断电宕机后，若不是使用配置文件配置的从机，再次启动后作为主机是无法获取之前主机的数据的，若此时重新配置称为从机，又可以获取到主机的所有数据。这里就要提到一个同步原理。

有两种方式可以产生新的主机：看下文“谋权篡位”

> 层层链路

上一个Slave 可以是下一个slave 和 Master，Slave 同样可以接收其他 slaves 的连接和同步请求，那么 该 slave 作为了链条中下一个的master，可以有效减轻 master 的写压力！

![image-20210409135939928](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210409135939928.png)

![image-20210409135955890](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210409135955890.png)



> 谋权篡位



有两种方式可以产生新的主机：

- 从机手动执行命令`slaveof no one`,这样执行以后从机会独立出来成为一个主机
- 使用哨兵模式（自动选举）

> 复制原理

Slave 启动成功连接到 master 后会发送一个sync命令

Master 接到命令，启动后台的存盘进程，同时收集所有接收到的用于修改数据集命令，在后台进程执行 完毕之后，master将传送整个数据文件到slave，并完成一次完全同步。

全量复制：而slave服务在接收到数据库文件数据后，将其存盘并加载到内存中。

增量复制：Master 继续将新的所有收集到的修改命令依次传给slave，完成同步

但是只要是重新连接master，一次完全同步（全量复制）将被自动执行

### 哨兵模式

更多信息参考博客：https://www.jianshu.com/p/06ab9daf921d

> 概述

主从切换技术的方法是：当主服务器宕机后，需要手动把一台从服务器切换为主服务器，这就需要人工 干预，费事费力，还会造成一段时间内服务不可用。这不是一种推荐的方式，更多时候，我们优先考虑 哨兵模式。Redis从2.8开始正式提供了Sentinel（哨兵） 架构来解决这个问题。

谋朝篡位的自动版，能够后台监控主机是否故障，如果故障了根据投票数自动将从库转换为主库。

哨兵模式是一种特殊的模式，首先Redis提供了哨兵的命令，哨兵是一个独立的进程，作为进程，它会独 立运行。其原理是**哨兵通过发送命令，等待Redis服务器响应，从而监控运行的多个Redis实例。**

![image-20210409150628118](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210409150628118.png)

这里的哨兵有两个作用

- 通过发送命令，让Redis服务器返回监控其运行状态，包括主服务器和从服务器。
- 当哨兵监测到master宕机，会自动将slave切换成master，然后通过**发布订阅模式**通知其他的从服务器，修改配置文件，让它们切换主机。

然而一个哨兵进程对Redis服务器进行监控，可能会出现问题，为此，我们可以使用多个哨兵进行监控。 各个哨兵之间还会进行监控，这样就形成了多哨兵模式。

![image-20210409150717930](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/image-20210409150717930.png)

假设主服务器宕机，哨兵1先检测到这个结果，系统并不会马上进行failover过程，仅仅是哨兵1主观的认 为主服务器不可用，这个现象成为**主观下线**。当后面的哨兵也检测到主服务器不可用，并且数量达到一 定值时，那么哨兵之间就会进行一次投票，投票的结果由一个哨兵发起，进行failover[故障转移]操作。 切换成功后，就会通过发布订阅模式，让各个哨兵把自己监控的从服务器实现切换主机，这个过程称为 **客观下线**。

> 配置测试

1、调整结构，6379带着80、81

2、自定义的 /myconfig 目录下新建 sentinel.conf 文件，名字千万不要错

3、配置哨兵，填写内容

- `sentinel monitor 被监控主机名字 127.0.0.1 6379 1`

  例如：sentinel monitor mymaster 127.0.0.1 6379 1，

  上面最后一个数字1，表示主机挂掉后slave投票看让谁接替成为主机，得票数多少后成为主机

4、启动哨兵

- Redis-sentinel myconfig/sentinel.conf

  上述目录依照各自的实际情况配置，可能目录不同

成功启动哨兵模式

![在这里插入图片描述](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/20200513215752444.png)

此时哨兵监视着我们的主机6379，当我们断开主机后：

![在这里插入图片描述](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/20200513215806972.png)

> 哨兵模式的优缺点

**优点**

1. 哨兵集群，基于主从复制模式，所有主从复制的优点，它都有
2. 主从可以切换，故障可以转移，系统的可用性更好
3. 哨兵模式是主从模式的升级，手动到自动，更加健壮

**缺点：**

1. Redis不好在线扩容，集群容量一旦达到上限，在线扩容就十分麻烦
2. 实现哨兵模式的配置其实是很麻烦的，里面有很多配置项



> 哨兵模式的全部配置

完整的哨兵模式配置文件 sentinel.conf

```bash
# Example sentinel.conf
 
# 哨兵sentinel实例运行的端口 默认26379
port 26379
 
# 哨兵sentinel的工作目录
dir /tmp
 
# 哨兵sentinel监控的redis主节点的 ip port 
# master-name  可以自己命名的主节点名字 只能由字母A-z、数字0-9 、这三个字符".-_"组成。
# quorum 当这些quorum个数sentinel哨兵认为master主节点失联 那么这时 客观上认为主节点失联了
# sentinel monitor <master-name> <ip> <redis-port> <quorum>
sentinel monitor mymaster 127.0.0.1 6379 1
 
# 当在Redis实例中开启了requirepass foobared 授权密码 这样所有连接Redis实例的客户端都要提供密码
# 设置哨兵sentinel 连接主从的密码 注意必须为主从设置一样的验证密码
# sentinel auth-pass <master-name> <password>
sentinel auth-pass mymaster MySUPER--secret-0123passw0rd
 
 
# 指定多少毫秒之后 主节点没有应答哨兵sentinel 此时 哨兵主观上认为主节点下线 默认30秒
# sentinel down-after-milliseconds <master-name> <milliseconds>
sentinel down-after-milliseconds mymaster 30000
 
# 这个配置项指定了在发生failover主备切换时最多可以有多少个slave同时对新的master进行 同步，
这个数字越小，完成failover所需的时间就越长，
但是如果这个数字越大，就意味着越 多的slave因为replication而不可用。
可以通过将这个值设为 1 来保证每次只有一个slave 处于不能处理命令请求的状态。
# sentinel parallel-syncs <master-name> <numslaves>
sentinel parallel-syncs mymaster 1
 
 
 
# 故障转移的超时时间 failover-timeout 可以用在以下这些方面： 
#1. 同一个sentinel对同一个master两次failover之间的间隔时间。
#2. 当一个slave从一个错误的master那里同步数据开始计算时间。直到slave被纠正为向正确的master那里同步数据时。
#3.当想要取消一个正在进行的failover所需要的时间。  
#4.当进行failover时，配置所有slaves指向新的master所需的最大时间。不过，即使过了这个超时，slaves依然会被正确配置为指向master，但是就不按parallel-syncs所配置的规则来了
# 默认三分钟
# sentinel failover-timeout <master-name> <milliseconds>
sentinel failover-timeout mymaster 180000
 
# SCRIPTS EXECUTION
 
#配置当某一事件发生时所需要执行的脚本，可以通过脚本来通知管理员，例如当系统运行不正常时发邮件通知相关人员。
#对于脚本的运行结果有以下规则：
#若脚本执行后返回1，那么该脚本稍后将会被再次执行，重复次数目前默认为10
#若脚本执行后返回2，或者比2更高的一个返回值，脚本将不会重复执行。
#如果脚本在执行过程中由于收到系统中断信号被终止了，则同返回值为1时的行为相同。
#一个脚本的最大执行时间为60s，如果超过这个时间，脚本将会被一个SIGKILL信号终止，之后重新执行。
 
#通知型脚本:当sentinel有任何警告级别的事件发生时（比如说redis实例的主观失效和客观失效等等），将会去调用这个脚本，
#这时这个脚本应该通过邮件，SMS等方式去通知系统管理员关于系统不正常运行的信息。调用该脚本时，将传给脚本两个参数，
#一个是事件的类型，
#一个是事件的描述。
#如果sentinel.conf配置文件中配置了这个脚本路径，那么必须保证这个脚本存在于这个路径，并且是可执行的，否则sentinel无法正常启动成功。
#通知脚本
# sentinel notification-script <master-name> <script-path>
  sentinel notification-script mymaster /var/redis/notify.sh
 
# 客户端重新配置主节点参数脚本
# 当一个master由于failover而发生改变时，这个脚本将会被调用，通知相关的客户端关于master地址已经发生改变的信息。
# 以下参数将会在调用脚本时传给脚本:
# <master-name> <role> <state> <from-ip> <from-port> <to-ip> <to-port>
# 目前<state>总是“failover”,
# <role>是“leader”或者“observer”中的一个。 
# 参数 from-ip, from-port, to-ip, to-port是用来和旧的master和新的master(即旧的slave)通信的
# 这个脚本应该是通用的，能被多次调用，不是针对性的。
# sentinel client-reconfig-script <master-name> <script-path>
sentinel client-reconfig-script mymaster /var/redis/reconfig.sh
```

## 缓存穿透与雪崩

### 缓存穿透（查不到）

> 概念

在默认情况下，用户请求数据时，会先在缓存(Redis)中查找，若没找到即缓存未命中，再在数据库中进行查找，数量少可能问题不大，可是一旦大量的请求数据（例如秒杀场景）缓存都没有命中的话，就会全部转移到数据库上，造成数据库极大的压力，就有可能导致数据库崩溃。网络安全中也有人恶意使用这种手段进行攻击被称为洪水攻击。

> 解决方案

**布隆过滤器**

对所有可能查询的参数以Hash的形式存储，以便快速确定是否存在这个值，在控制层先进行拦截校验，校验不通过直接打回，减轻了存储系统的压力。

![在这里插入图片描述](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/20200513215824722.jpg)

**缓存空对象**

一次请求若在缓存和数据库中都没找到，就在缓存中方一个空对象用于处理后续这个请求。

![在这里插入图片描述](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/20200513215836317.jpg)

 这样做有一个缺陷：存储空对象也需要空间，大量的空对象会耗费一定的空间，存储效率并不高。解决这个缺陷的方式就是设置较短过期时间

即使对空值设置了过期时间，还是会存在缓存层和存储层的数据会有一段时间窗口的不一致，这对于需要保持一致性的业务会有影响。

### 缓存击穿（量太大，缓存过期）

> 概念

 相较于缓存穿透，缓存击穿的目的性更强，一个存在的key，在缓存过期的一刻，同时有大量的请求，这些请求都会击穿到DB，造成瞬时DB请求量大、压力骤增。这就是缓存被击穿，只是针对其中某个key的缓存不可用而导致击穿，但是其他的key依然可以使用缓存响应。

 比如热搜排行上，一个热点新闻被同时大量访问就可能导致缓存击穿。

> 解决方案

1. **设置热点数据永不过期**

   这样就不会出现热点数据过期的情况，但是当Redis内存空间满的时候也会清理部分数据，而且此种方案会占用空间，一旦热点数据多了起来，就会占用部分空间。

2. **加互斥锁(分布式锁)**

   在访问key之前，采用SETNX（set if not exists）来设置另一个短期key来锁住当前key的访问，访问结束再删除该短期key。保证同时刻只有一个线程访问。这样对锁的要求就十分高。

### 缓存雪崩

> 概念

大量的key设置了相同的过期时间，导致在缓存在同一时刻全部失效，造成瞬时DB请求量大、压力骤增，引起雪崩。

产生雪崩的原因之一，比如在写本文的时候，马上就要到双十二零点，很快就会迎来一波抢购，这波商品时间比较集中的放入了缓存，假设缓存一个小时。那么到了凌晨一点钟的时候，这批商品的缓存就都过期了。而对这批商品的访问查询，都落到了数据库上，对于数据库而言，就会产生周期性的压力波峰。于是所有的请求都会达到存储层，存储层的调用量会暴增，造成存储层也会挂掉的情况。

![在这里插入图片描述](https://gcore.jsdelivr.net/gh/oddfar/static/img/Redis.assets/20200513215850428.jpeg)

其实集中过期，倒不是非常致命，比较致命的缓存雪崩，是缓存服务器某个节点宕机或断网。因为自然 形成的缓存雪崩，一定是在某个时间段集中创建缓存，这个时候，数据库也是可以顶住压力的。无非就 是对数据库产生周期性的压力而已。而缓存服务节点的宕机，对数据库服务器造成的压力是不可预知 的，很有可能瞬间就把数据库压垮。

> 解决方案

- redis高可用

  这个思想的含义是，既然redis有可能挂掉，那我多增设几台redis，这样一台挂掉之后其他的还可以继续工作，其实就是搭建的集群

- 限流降级

  这个解决方案的思想是，在缓存失效后，通过加锁或者队列来控制读数据库写缓存的线程数量。比如对某个key只允许一个线程查询数据和写缓存，其他线程等待。

- 数据预热

  数据加热的含义就是在正式部署之前，我先把可能的数据先预先访问一遍，这样部分可能大量访问的数据就会加载到缓存中。在即将发生大并发访问前手动触发加载缓存不同的key，设置不同的过期时间，让缓存失效的时间点尽量均匀。

