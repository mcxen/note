
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
     
     
     
   

​	
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
  
  
  
  > 服了，这个shabi redis HomeBrew安装后的配置文件在这个卸载的时候出现了？？？
  >
  > ![截屏2023-05-28 21.48.38](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/截屏2023-05-28 21.48.38.png)
  >
  > ```sh
  > brew install redis
  > cd /opt/homebrew/etc
  > vim /redis.conf
  > 
  > 
  > mcxw@mcxAir etc % redis-server /opt/homebrew/etc/redis.conf 
  > ```
  >
  > 使用/general查找到daemonize 就可以了
  >
  > **修改： 将 daemonize no 修改为 daemonize yes**
  
  
  
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

MAC，HomeBrew安装的是`/opt/homebrew/etc/redis.conf`

编辑配置文件 `/usr/local/redis-5.0.14/redis.conf`，找到如下内容，去掉前面的注释，并修改为所需要的密码：（在vim中，「/」为查找，「n」为查看下一个的位置的选项）

```shell
#requirepass foobared
```

**登录验证**

设置Redis认证密码后，客户端登录时需要使用`-a`参数输入认证密码，不添加该参数虽然也可以登录成功，但是没有任何操作权限。如下：

```sh
$ ./redis-cli -h 127.0.0.1 -p 6379
127.0.0.1:6379> keys *
(error) NOAUTH Authentication required.
```

使用密码认证登录，并验证操作权限：

```sh
$ ./redis-cli -h 127.0.0.1 -p 6379 -a 123456
127.0.0.1:6379> config get requirepass
1) "requirepass"
2) "myPassword"
```

看到类似上面的输出，说明Reids密码认证配置成功。

除了按上面的方式在登录时，使用`-a`参数输入登录密码外。也可以不指定，在连接后进行验证：

```sh
$ ./redis-cli -h 127.0.0.1 -p 6379
127.0.0.1:6379> ping
(error) NOAUTH Authentication required.
127.0.0.1:6379> auth 123456
OK
127.0.0.1:6379> config get requirepass
1) "requirepass"
2) "123456"
127.0.0.1:6379> 
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

## 

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

  Redis事务

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



### Java调用fastjson，jedis存储在redis中

```java
Jedis jedis = new Jedis("127.0.0.1",6379);
Student student = new Student();
//序列化
String json = JSON.toJSONString(student);
String key = "student:"+student.getNo();
jedis.set(key,json)

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

> JSONObject来自org.json
>
> ```xml
> <dependency>
>     <groupId>org.json</groupId>
>     <artifactId>json</artifactId>
>     <version>20180813</version>
> </dependency>
> ```
>
> ```java
> JSONObject obj = new JSONObject();
> obj.put(key, value);
> ```
>
> 直接构建即直接实例化一个 JSONObject 对象，而后调用其 put() 方法，将数据写入。put() 方法的第一个参数为 key 值，必须为 String 类型，第二个参数为 value，可以为 boolean、double、int、long、Object、Map 以及 Collection 等。当然，double 以及 int 等类型只是在 Java 中，写入到 json 中时，统一都会以 Number 类型存储。
>
> 



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



## lettuce-jedis后起之秀

jedis和lettuce对比：

**jedis**：采用直连，多线程连接不安全，使用jedispool才持久

**优点：**

- 提供了比较全面的 Redis 操作特性的 API
- API 基本与 Redis 的指令一一对应，使用简单易理解

**缺点：**

- 同步阻塞 IO
- 不支持异步
- 线程不安全

**lettuce：**

**优点：**

- 线程安全
- 基于 Netty 框架的事件驱动的通信，可异步调用
- 适用于分布式缓存

**缺点：**

- API 更抽象，学习使用成本高



### **基本使用**

首先，创建一个 maven 项目，引入`lettuce-core`包，就可以使用了。

```xml
<dependency>
  <groupId>io.lettuce</groupId>
  <artifactId>lettuce-core</artifactId>
  <version>5.3.1.RELEASE</version>
</dependency>
```

使用 lettuce 连接 redis，测试是否能正常联通！

```java
public class LettuceMain {

    public static void main(String[] args) {
        RedisURI redisUri = RedisURI.builder()
                .withHost("127.0.0.1")
                .withPort(6379)
                .withPassword("111111")
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        RedisClient redisClient = RedisClient.create(redisUri);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();
        System.out.println(commands.ping());
        connection.close();
        redisClient.shutdown();
    }
}
```

### **同步操作**

基本上只要是 Jedis 支持的同步命令操作，Lettuce 都支持。

下面，我们以同步操作字符串为例，Lettuce 的 api 操作如下！

```java
public class LettuceSyncMain {

    public static void main(String[] args) {
        RedisURI redisUri = RedisURI.builder()
                .withHost("127.0.0.1").withPort(6379).withPassword("111111")
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        RedisClient redisClient = RedisClient.create(redisUri);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        //获取同步操作命令工具
        RedisCommands<String, String> commands = connection.sync();

        System.out.println("清空数据："+commands.flushdb());
        System.out.println("判断某个键是否存在："+commands.exists("username"));
        System.out.println("新增<'username','xmr'>的键值对："+commands.set("username", "xmr"));
        System.out.println("新增<'password','password'>的键值对："+commands.set("password", "123"));
        System.out.println("获取<'password'>键的值："+commands.get("password"));
        System.out.println("系统中所有的键如下：" + commands.keys("*"));
        System.out.println("删除键password:"+commands.del("password"));
        System.out.println("判断键password是否存在："+commands.exists("password"));
        System.out.println("设置键username的过期时间为5s:"+commands.expire("username", 5L));
        System.out.println("查看键username的剩余生存时间："+commands.ttl("username"));
        System.out.println("移除键username的生存时间："+commands.persist("username"));
        System.out.println("查看键username的剩余生存时间："+commands.ttl("username"));
        System.out.println("查看键username所存储的值的类型："+commands.type("username"));

        connection.close();
        redisClient.shutdown();
    }
}
```

### **异步操作**

除此之外，Lettuce 还支持异步操作，将上面的操作改成异步处理，结果如下！

```java
public class LettuceASyncMain {

    public static void main(String[] args) throws Exception {
        RedisURI redisUri = RedisURI.builder()
                .withHost("127.0.0.1").withPort(6379).withPassword("111111")
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        RedisClient redisClient = RedisClient.create(redisUri);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        //获取异步操作命令工具
        RedisAsyncCommands<String, String> commands = connection.async();

        System.out.println("清空数据："+commands.flushdb().get());
        System.out.println("判断某个键是否存在："+commands.exists("username").get());
        System.out.println("新增<'username','xmr'>的键值对："+commands.set("username", "xmr").get());
        System.out.println("新增<'password','password'>的键值对："+commands.set("password", "123").get());
        System.out.println("获取<'password'>键的值："+commands.get("password").get());
        System.out.println("系统中所有的键如下：" + commands.keys("*").get());
        System.out.println("删除键password:"+commands.del("password").get());
        System.out.println("判断键password是否存在："+commands.exists("password").get());
        System.out.println("设置键username的过期时间为5s:"+commands.expire("username", 5L).get());
        System.out.println("查看键username的剩余生存时间："+commands.ttl("username").get());
        System.out.println("移除键username的生存时间："+commands.persist("username").get());
        System.out.println("查看键username的剩余生存时间："+commands.ttl("username").get());
        System.out.println("查看键username所存储的值的类型："+commands.type("username").get());

        connection.close();
        redisClient.shutdown();
    }
}
```

### **响应式编程**

Lettuce 除了支持异步编程以外，还支持响应式编程，Lettuce 引入的[响应式编程框架](https://www.zhihu.com/search?q=响应式编程框架&search_source=Entity&hybrid_search_source=Entity&hybrid_search_extra={"sourceType"%3A"answer"%2C"sourceId"%3A2820019428})是`Project Reactor`，如果没有响应式编程经验可以先自行了解一下，访问地址`https://projectreactor.io/`。

响应式编程使用案例如下：

```java
public class LettuceMain {

    public static void main(String[] args) throws Exception {
        RedisURI redisUri = RedisURI.builder()
                .withHost("127.0.0.1").withPort(6379).withPassword("111111")
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        RedisClient redisClient = RedisClient.create(redisUri);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        //获取响应式API操作命令工具
        RedisReactiveCommands<String, String> commands = connection.reactive();

        Mono<String> setc = commands.set("name", "mayun");
        System.out.println(setc.block());
        Mono<String> getc = commands.get("name");
        getc.subscribe(System.out::println);
        Flux<String> keys = commands.keys("*");
        keys.subscribe(System.out::println);

        //开启一个事务，先把count设置为1，再将count自增1
        commands.multi().doOnSuccess(r -> {
            commands.set("count", "1").doOnNext(value -> System.out.println("count1：" +  value)).subscribe();
            commands.incr("count").doOnNext(value -> System.out.println("count2：" +  value)).subscribe();
        }).flatMap(s -> commands.exec())
                .doOnNext(transactionResult -> System.out.println("transactionResult：" + transactionResult.wasDiscarded())).subscribe();

        Thread.sleep(1000 * 5);
        connection.close();
        redisClient.shutdown();
    }
}
```

### **发布和订阅**

Lettuce 还支持 redis 的消息发布和订阅，具体实现案例如下：

```java
public class LettuceReactiveMain1 {

    public static void main(String[] args) throws Exception {
        RedisURI redisUri = RedisURI.builder()
                .withHost("127.0.0.1").withPort(6379).withPassword("111111")
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        RedisClient redisClient = RedisClient.create(redisUri);
        //获取发布订阅操作命令工具
        StatefulRedisPubSubConnection<String, String> pubsubConn = redisClient.connectPubSub();
        pubsubConn.addListener(new RedisPubSubListener<String, String>() {
            @Override
            public void unsubscribed(String channel, long count) {
                System.out.println("[unsubscribed]" + channel);
            }
            @Override
            public void subscribed(String channel, long count) {
                System.out.println("[subscribed]" + channel);
            }
            @Override
            public void punsubscribed(String pattern, long count) {
                System.out.println("[punsubscribed]" + pattern);
            }
            @Override
            public void psubscribed(String pattern, long count) {
                System.out.println("[psubscribed]" + pattern);
            }
            @Override
            public void message(String pattern, String channel, String message) {
                System.out.println("[message]" + pattern + " -> " + channel + " -> " + message);
            }
            @Override
            public void message(String channel, String message) {
                System.out.println("[message]" + channel + " -> " + message);
            }
        });
        RedisPubSubAsyncCommands<String, String> pubsubCmd = pubsubConn.async();
        pubsubCmd.psubscribe("CH");
        pubsubCmd.psubscribe("CH2");
        pubsubCmd.unsubscribe("CH");

        Thread.sleep(100 * 5);
        pubsubConn.close();
        redisClient.shutdown();
    }
}
```

### **客户端资源与参数配置**

Lettuce 客户端的通信框架集成了 Netty 的非阻塞 IO 操作，客户端资源的设置与 Lettuce 的性能、并发和事件处理紧密相关，如果不是特别熟悉客户端参数配置，不建议在没有经验的前提下凭直觉修改默认值，保持默认配置就行。

非集群环境下，具体的配置案例如下：

```java
public class LettuceMain {

    public static void main(String[] args) throws Exception {
        ClientResources resources = DefaultClientResources.builder()
                .ioThreadPoolSize(4) //I/O线程数
                .computationThreadPoolSize(4) //任务线程数
                .build();
        RedisURI redisUri = RedisURI.builder()
                .withHost("127.0.0.1").withPort(6379).withPassword("111111")
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        ClientOptions options = ClientOptions.builder()
                .autoReconnect(true)//是否自动重连
                .pingBeforeActivateConnection(true)//连接激活之前是否执行PING命令
                .build();
        RedisClient client = RedisClient.create(resources, redisUri);
        client.setOptions(options);
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisCommands<String, String> commands = connection.sync();
        commands.set("name", "关羽");
        System.out.println(commands.get("name"));

        connection.close();
        client.shutdown();
        resources.shutdown();
    }
}
```

集群环境下，具体的配置案例如下：

```java
public class LettuceMain {

    public static void main(String[] args) throws Exception {
        ClientResources resources = DefaultClientResources.builder()
                .ioThreadPoolSize(4) //I/O线程数
                .computationThreadPoolSize(4) //任务线程数
                .build();
        RedisURI redisUri = RedisURI.builder()
                .withHost("127.0.0.1").withPort(6379).withPassword("111111")
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        ClusterClientOptions options = ClusterClientOptions.builder()
                .autoReconnect(true)//是否自动重连
                .pingBeforeActivateConnection(true)//连接激活之前是否执行PING命令
                .validateClusterNodeMembership(true)//是否校验集群节点的成员关系
                .build();
        RedisClusterClient client = RedisClusterClient.create(resources, redisUri);
        client.setOptions(options);
        StatefulRedisClusterConnection<String, String> connection = client.connect();
        RedisAdvancedClusterCommands<String, String> commands = connection.sync();
        commands.set("name", "张飞");
        System.out.println(commands.get("name"));

        connection.close();
        client.shutdown();
        resources.shutdown();
    }
}
```

### **线程池配置**

Lettuce 连接设计的时候，就是线程安全的，所以一个连接可以被多个线程共享，同时 lettuce 连接默认是自动重连的，使用单连接基本可以满足业务需求，大多数情况下不需要配置连接池，多连接并不会给操作带来性能上的提升。

但在某些特殊场景下，比如事物操作，使用连接池会是一个比较好的方案，那么如何配置线程池呢？

```text
public class LettuceMain {

    public static void main(String[] args) throws Exception {
        RedisURI redisUri = RedisURI.builder()
                .withHost("127.0.0.1")
                .withPort(6379)
                .withPassword("111111")
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        RedisClient client = RedisClient.create(redisUri);
        //连接池配置
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(2);

        GenericObjectPool<StatefulRedisConnection<String, String>> pool = ConnectionPoolSupport.createGenericObjectPool(client::connect, poolConfig);
        StatefulRedisConnection<String, String> connection = pool.borrowObject();
        RedisCommands<String, String> commands = connection.sync();
        commands.set("name", "张飞");
        System.out.println(commands.get("name"));

        connection.close();
        pool.close();
        client.shutdown();
    }
}
```

### **主从模式配置**

redis 一般采用主从复制模式，搭建高可用的架构，简单的说就一个主节点，多个从节点，自动从主节点同步最新数据。

Lettuce 支持自动发现主从模式下的节点信息，然后保存到本地，具体配置如下：

```text
public class LettuceMain {

    public static void main(String[] args) throws Exception {
        //这里只需要配置一个节点的连接信息，不一定需要是主节点的信息，从节点也可以;可以自动发现主从节点
        RedisURI uri = RedisURI.builder().withHost("192.168.31.111").withPort(6379).withPassword("123456").build();
        RedisClient client = RedisClient.create(uri);
        StatefulRedisMasterReplicaConnection<String, String> connection = MasterReplica.connect(client, StringCodec.UTF8, uri);
        //从节点读取数据
        connection.setReadFrom(ReadFrom.REPLICA);

        RedisCommands<String, String> commands = connection.sync();
        commands.set("name", "张飞");
        System.out.println(commands.get("name"));

        connection.close();
        client.shutdown();
    }
}
```

当然我们也可以手动指定集群节点来加载，具体配置如下：

```text
public class LettuceMain {

    public static void main(String[] args) throws Exception {
        //集群节点
        List<RedisURI> uris = new ArrayList();
        uris.add(RedisURI.builder().withHost("192.168.31.111").withPort(6379).withPassword("111111").build());
        uris.add(RedisURI.builder().withHost("192.168.31.112").withPort(6379).withPassword("111111").build());
        uris.add(RedisURI.builder().withHost("192.168.31.113").withPort(6379).withPassword("111111").build());

        RedisClient client = RedisClient.create();
        StatefulRedisMasterReplicaConnection<String, String> connection = MasterReplica.connect(client, StringCodec.UTF8, uris);
        //从节点读取数据
        connection.setReadFrom(ReadFrom.REPLICA);

        RedisCommands<String, String> commands = connection.sync();
        commands.set("name", "张飞");
        System.out.println(commands.get("name"));

        connection.close();
        client.shutdown();
    }
}
```

### **哨兵模式配置**

哨兵模式，也是 redis 实现服务高可用的一大亮点，具体配置实现如下：

```text
public class LettuceMain {

    public static void main(String[] args) throws Exception {
        //集群节点
        List<RedisURI> uris = new ArrayList();
        uris.add(RedisURI.builder().withSentinel("192.168.31.111", 26379).withSentinelMasterId("mymaster").withPassword("123456").build());
        uris.add(RedisURI.builder().withSentinel("192.168.31.112", 26379).withSentinelMasterId("mymaster").withPassword("123456").build());
        uris.add(RedisURI.builder().withSentinel("192.168.31.113", 26379).withSentinelMasterId("mymaster").withPassword("123456").build());

        RedisClient client = RedisClient.create();
        StatefulRedisMasterReplicaConnection<String, String> connection = MasterReplica.connect(client, StringCodec.UTF8, uris);
        //从节点读取数据
        connection.setReadFrom(ReadFrom.REPLICA);

        RedisCommands<String, String> commands = connection.sync();
        commands.set("name", "赵云");
        System.out.println(commands.get("name"));

        connection.close();
        client.shutdown();
    }
}
```

### **Cluster 集群模式配置**

Cluster 集群模式，是之后推出的一种高可用的架构模型，主要是采用分片方式来存储数据，具体配置如下：

```java
public class LettuceReactiveMain4 {

    public static void main(String[] args) throws Exception {
        Set<RedisURI> uris = new HashSet<>();
        uris.add(RedisURI.builder().withHost("192.168.31.111").withPort(7000).withPassword("123456").build());
        uris.add(RedisURI.builder().withHost("192.168.31.112").withPort(7000).withPassword("123456").build());
        uris.add(RedisURI.builder().withHost("192.168.31.113").withPort(7000).withPassword("123456").build());
        uris.add(RedisURI.builder().withHost("192.168.31.114").withPort(7000).withPassword("123456").build());
        uris.add(RedisURI.builder().withHost("192.168.31.115").withPort(7000).withPassword("123456").build());
        uris.add(RedisURI.builder().withHost("192.168.31.116").withPort(7001).withPassword("123456").build());

        RedisClusterClient client = RedisClusterClient.create(uris);
        StatefulRedisClusterConnection<String, String> connection = client.connect();
        RedisAdvancedClusterCommands<String, String> commands = connection.sync();
        commands.set("name", "关羽");
        System.out.println(commands.get("name"));

        //选择从节点,只读
        NodeSelection<String, String> replicas = commands.replicas();
        NodeSelectionCommands<String, String> nodeSelectionCommands = replicas.commands();
        Executions<List<String>> keys = nodeSelectionCommands.keys("*");
        keys.forEach(key -> System.out.println(key));

        connection.close();
        client.shutdown();
    }
}
```

## :star:SpringBoot整合

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

> 补充说明：注意要将你需要保存的转成序列化。

**导入依赖**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

**说明：在springboot2.x之后，原来使用的jedis被替换成lettuce**



#### **yaml配置**

```yml
spring:
  redis:
    host: 127.0.0.1
    port: 6379
   
```

Properties



```
#redis配置
#Redis服务器地址
spring.redis.host=127.0.0.1
#Redis服务器连接端口
spring.redis.port=6379
#Redis数据库索引（默认为0）
spring.redis.database=0  
#连接池最大连接数（使用负值表示没有限制）
spring.redis.jedis.pool.max-active=50
#连接池最大阻塞等待时间（使用负值表示没有限制）
spring.redis.jedis.pool.max-wait=3000
#连接池中的最大空闲连接
spring.redis.jedis.pool.max-idle=20
#连接池中的最小空闲连接
spring.redis.jedis.pool.min-idle=2
#连接超时时间（毫秒）
spring.redis.timeout=5000
```



> 在 Spring Boot 中，测试类通常使用 JUnit 框架编写，测试类需要使用 `@RunWith(SpringRunner.class)` 和 `@SpringBootTest` 注解来启动 Spring 应用程序上下文。以下是一个简单的 Spring Boot 测试类的示例：
>
> ```java
> @RunWith(SpringRunner.class)
> @SpringBootTest
> public class MyServiceTest {
> 
>     @Autowired
>     private MyService myService;
> 
>     @Test
>     public void testMyService() {
>         String result = myService.sayHello("World");
>         Assert.assertEquals("Hello, World", result);
>     }
> }
> ```
>
> 在这个示例中，`@RunWith(SpringRunner.class)` 注解告诉 JUnit 使用 Spring 的测试运行器来运行测试。`@SpringBootTest` 注解告诉 Spring Boot 加载整个应用程序上下文。`@Autowired` 注解将 `MyService` 注入到测试类中。`@Test` 注解标识一个测试方法，这里我们测试了 `MyService` 的 `sayHello` 方法是否返回了正确的结果。
>
> 需要注意的是，测试类需要放在 `src/test/java` 目录下，而不是 `src/main/java` 目录下。另外，在测试类中可以使用 `@MockBean` 注解来模拟依赖项，以便更好地进行单元测试。
>
> ![截屏2023-05-29 16.44.11](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-05-29%2016.44.11.png)



#### **测试类中测试**

```java
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTemplateTest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void testRedis(){
        redisTemplate.opsForValue().set("name","1");
        System.out.println(redisTemplate.opsForValue().get("name"));
    }
}

```



**RedisTemplate对于Redis5种基础类型**

```java
redisTemplate.opsForValue(); // 操作字符串
redisTemplate.opsForHash(); // 操作hash
redisTemplate.opsForList(); // 操作list
redisTemplate.opsForSet(); // 操作set
redisTemplate.opsForZSet(); // 操作zset
```



**测试结果显示存储的是一个city，但是前面有很多字符，因为被序列化了**

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230529165420583.png" alt="image-20230529165420583" style="zoom:50%;" />

**修改成String的RedisTemplate就可以了**（或者在配置类进行配置）



<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230529165627050.png" alt="image-20230529165627050" style="zoom:50%;" />







通过@Bean的方式配置RedisTemplate，主要是设置RedisConnectionFactory以及各种类型数据的Serializer。

```java
package tech.pdai.springboot.redis.jedis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis configuration.
 *
 * @author pdai
 */
@Configuration
public class RedisConfig {

    /**
     * redis template.
     *
     * @param factory factory
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
```





#### Hash结构

```java
 //Hash结构
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.put("china","hubei","wuhan");
        hashOperations.put("china","hunan","xiangtan");
        System.out.println(hashOperations.get("china", "hunan"));
        Set<Object> keys = hashOperations.keys("china");//得到Hash的每一个属性
        for (Object key : keys) {
            System.out.println(key + " = "+hashOperations.get("china",key));
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

### 搭配SpringBoot自带的缓存使用

> 1、需要缓存的Dispatcher**转发**

![image-20231116212531425](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20231116212531425.png)



> 2、**序列化返回值**

![image-20231116212651288](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20231116212651288.png)

> 3、添加SpringBoot缓存和redis的依赖

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
```



> 4、配置application.properties
>

```properties

spring.data.redis.host=127.0.0.1
spring.data.redis.port=6379
spring.data.redis.password=123456

## 设置缓存改为redis
spring.cache.type=redis
#设置缓存前缀
spring.cache.redis.use-key-prefix=true
spring.cache.redis.key-prefix=train_cache_
spring.cache.redis.cache-null-values=false
spring.cache.redis.time-to-live=60s
```



### redis工具类

使用RedisTemplate需要频繁调用`.opForxxx`然后才能进行对应的操作，这样使用起来代码效率低下，工作中一般不会这样使用，而是将这些常用的公共API抽取出来封装成为一个工具类，然后直接使用工具类来间接操作Redis,不但效率高并且易用。

工具类参考博客：

https://www.cnblogs.com/zeng1994/p/03303c805731afc9aa9c60dbbd32a323.html

https://www.cnblogs.com/zhzhlong/p/11434284.html

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

![image-20210408224901985](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20210408224901985.png)

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

![image-20210408225404338](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20210408225404338.png)

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

Redis 在执行完一条写操作命令后，就会把该命令以追加的方式写入到一个文件里，然后 Redis 重启时，会读取该文件记录的命令，然后逐一执行命令的方式来进行数据恢复。

![img](https://cdn.xiaolincoding.com//mysql/other/6f0ab40396b7fc2c15e6f4487d3a0ad7-20230309232240301.png)

我这里以「*set name xiaolin*」命令作为例子，Redis 执行了这条命令后，记录在 AOF 日志里的内容如下图：

![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/337021a153944fd0f964ca834e34d0f2-20230309232243363.png)

我这里给大家解释下。

「*3」表示当前命令有三个部分，每部分都是以「$+数字」开头，后面紧跟着具体的命令、键或值。然后，这里的「数字」表示这部分中的命令、键或值一共有多少字节。例如，「$3 set」表示这部分有 3 个字节，也就是「set」命令这个字符串的长度。

> 为什么先执行命令，再把数据写入日志呢？

Reids 是先执行写操作命令后，才将该命令记录到 AOF 日志里的，这么做其实有两个好处。

- **避免额外的检查开销**：因为如果先将写操作命令记录到 AOF 日志里，再执行该命令的话，如果当前的命令语法有问题，那么如果不进行命令语法检查，该错误的命令记录到 AOF 日志里后，Redis 在使用日志恢复数据时，就可能会出错。
- **不会阻塞当前写操作命令的执行**：因为当写操作命令执行成功后，才会将命令记录到 AOF 日志。

当然，这样做也会带来风险：

- **数据可能会丢失：** 执行写操作命令和记录日志是两个过程，那当 Redis 在还没来得及将命令写入到硬盘时，服务器发生宕机了，这个数据就会有丢失的风险。
- **可能阻塞其他操作：** 由于写操作命令执行成功后才记录到 AOF 日志，所以不会阻塞当前命令的执行，但因为 AOF 日志也是在主线程中执行，所以当 Redis 把日志文件写入磁盘的时候，还是会阻塞后续的操作无法执行。

> AOF 写回策略有几种？

先来看看，Redis 写入 AOF 日志的过程，如下图：

![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/4eeef4dd1bedd2ffe0b84d4eaa0dbdea-20230309232249413.png)

具体说说：

1. Redis 执行完写操作命令后，会将命令追加到 server.aof_buf 缓冲区；
2. 然后通过 write() 系统调用，将 aof_buf 缓冲区的数据写入到 AOF 文件，此时数据并没有写入到硬盘，而是拷贝到了内核缓冲区 page cache，等待内核将数据写入硬盘；
3. 具体内核缓冲区的数据什么时候写入到硬盘，由内核决定。

Redis 提供了 3 种写回硬盘的策略，控制的就是上面说的第三步的过程。 在 Redis.conf 配置文件中的 appendfsync 配置项可以有以下 3 种参数可填：

- **Always**，这个单词的意思是「总是」，所以它的意思是每次写操作命令执行完后，同步将 AOF 日志数据写回硬盘；
- **Everysec**，这个单词的意思是「每秒」，所以它的意思是每次写操作命令执行完后，先将命令写入到 AOF 文件的内核缓冲区，然后每隔一秒将缓冲区里的内容写回到硬盘；
- **No**，意味着不由 Redis 控制写回硬盘的时机，转交给操作系统控制写回的时机，也就是每次写操作命令执行完后，先将命令写入到 AOF 文件的内核缓冲区，再由操作系统决定何时将缓冲区内容写回硬盘。

我也把这 3 个写回策略的优缺点总结成了一张表格：

![img](https://cdn.xiaolincoding.com//mysql/other/98987d9417b2bab43087f45fc959d32a-20230309232253633.png)

> AOF 日志过大，会触发什么机制？

AOF 日志是一个文件，随着执行的写操作命令越来越多，文件的大小会越来越大。 如果当 AOF 日志文件过大就会带来性能问题，比如重启 Redis 后，需要读 AOF 文件的内容以恢复数据，如果文件过大，整个恢复的过程就会很慢。

所以，Redis 为了避免 AOF 文件越写越大，提供了 **AOF 重写机制**，当 AOF 文件的大小超过所设定的阈值后，Redis 就会启用 AOF 重写机制，来压缩 AOF 文件。

AOF 重写机制是在重写时，读取当前数据库中的所有键值对，然后将每一个键值对用一条命令记录到「新的 AOF 文件」，等到全部记录完后，就将新的 AOF 文件替换掉现有的 AOF 文件。

举个例子，在没有使用重写机制前，假设前后执行了「*set name xiaolin*」和「*set name xiaolincoding*」这两个命令的话，就会将这两个命令记录到 AOF 文件。

![img](https://cdn.xiaolincoding.com//mysql/other/723d6c580c05400b3841bc69566dd61b-20230309232257343.png)

但是**在使用重写机制后，就会读取 name 最新的 value（键值对） ，然后用一条 「set name xiaolincoding」命令记录到新的 AOF 文件**，之前的第一个命令就没有必要记录了，因为它属于「历史」命令，没有作用了。这样一来，一个键值对在重写日志中只用一条命令就行了。

重写工作完成后，就会将新的 AOF 文件覆盖现有的 AOF 文件，这就相当于压缩了 AOF 文件，使得 AOF 文件体积变小了。

> 重写 AOF 日志的过程是怎样的？

Redis 的**重写 AOF 过程是由后台子进程 \*bgrewriteaof\* 来完成的**，这么做可以达到两个好处：

- 子进程进行 AOF 重写期间，主进程可以继续处理命令请求，从而避免阻塞主进程；
- 子进程带有主进程的数据副本，这里使用子进程而不是线程，因为如果是使用线程，多线程之间会共享内存，那么在修改共享内存数据的时候，需要通过加锁来保证数据的安全，而这样就会降低性能。而使用子进程，创建子进程时，父子进程是共享内存数据的，不过这个共享的内存只能以只读的方式，而当父子进程任意一方修改了该共享内存，就会发生「写时复制」，于是父子进程就有了独立的数据副本，就不用加锁来保证数据安全。

触发重写机制后，主进程就会创建重写 AOF 的子进程，此时父子进程共享物理内存，重写子进程只会对这个内存进行只读，重写 AOF 子进程会读取数据库里的所有数据，并逐一把内存数据的键值对转换成一条命令，再将命令记录到重写日志（新的 AOF 文件）。

**但是重写过程中，主进程依然可以正常处理命令**，那问题来了，重写 AOF 日志过程中，如果主进程修改了已经存在 key-value，那么会发生写时复制，此时这个 key-value 数据在子进程的内存数据就跟主进程的内存数据不一致了，这时要怎么办呢？

为了解决这种数据不一致问题，Redis 设置了一个 **AOF 重写缓冲区**，这个缓冲区在创建 bgrewriteaof 子进程之后开始使用。

在重写 AOF 期间，当 Redis 执行完一个写命令之后，它会**同时将这个写命令写入到 「AOF 缓冲区」和 「AOF 重写缓冲区」**。

![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0ODI3Njc0,size_16,color_FFFFFF,t_70-20230309232301042.png)

也就是说，在 bgrewriteaof 子进程执行 AOF 重写期间，主进程需要执行以下三个工作:

- 执行客户端发来的命令；
- 将执行后的写命令追加到 「AOF 缓冲区」；
- 将执行后的写命令追加到 「AOF 重写缓冲区」；

当子进程完成 AOF 重写工作（*扫描数据库中所有数据，逐一把内存数据的键值对转换成一条命令，再将命令记录到重写日志*）后，会向主进程发送一条信号，信号是进程间通讯的一种方式，且是异步的。

主进程收到该信号后，会调用一个信号处理函数，该函数主要做以下工作：

- 将 AOF 重写缓冲区中的所有内容追加到新的 AOF 的文件中，使得新旧两个 AOF 文件所保存的数据库状态一致；
- 新的 AOF 的文件进行改名，覆盖现有的 AOF 文件。

信号函数执行完后，主进程就可以继续像往常一样处理命令了。

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

### REDIS持久化

### 

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

Bloom Filter 会使用一个较大的 bit 数组来保存所有的数据，数组中的每个元素都只占用 1 bit ，并且每个元素只能是 0 或者 1（代表 false 或者 true），这也是 Bloom Filter 节省内存的核心所在。这样来算的话，申请一个 100w 个元素的位数组只占用 1000000Bit / 8 = 125000 Byte = 125000/1024 KB ≈ 122KB 的空间。

![位数组](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/bloom-filter-bit-table.png)

**当一个元素加入布隆过滤器中的时候，会进行如下操作：**

1. 使用布隆过滤器中的哈希函数对元素值进行计算，得到哈希值（有几个哈希函数得到几个哈希值）。
2. 根据得到的哈希值，在位数组中把对应下标的值置为 1。

**当我们需要判断一个元素是否存在于布隆过滤器的时候，会进行如下操作：**

1. 对给定元素再次进行相同的哈希计算；
2. 得到值之后判断位数组中的每个元素是否都为 1，如果值都为 1，那么说明这个值在布隆过滤器中，如果存在一个值不为 1，说明该元素不在布隆过滤器中。

Bloom Filter 的简单原理图如下：

![Bloom Filter 的简单原理示意图](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/bloom-filter-simple-schematic-diagram.png)Bloom Filter 的简单原理示意图

如图所示，当字符串存储要加入到布隆过滤器中时，该字符串首先由多个哈希函数生成不同的哈希值，然后将对应的位数组的下标设置为 1（当位数组初始化时，所有位置均为 0）。当第二次存储相同字符串时，因为先前的对应位置已设置为 1，所以很容易知道此值已经存在（去重非常方便）。

如果我们需要判断某个字符串是否在布隆过滤器中时，只需要对给定字符串再次进行相同的哈希计算，得到值之后判断位数组中的每个元素是否都为 1，如果值都为 1，那么说明这个值在布隆过滤器中，如果存在一个值不为 1，说明该元素不在布隆过滤器中。

**不同的字符串可能哈希出来的位置相同，这种情况我们可以适当增加位数组大小或者调整我们的哈希函数。**

综上，我们可以得出：**布隆过滤器说某个元素存在，小概率会误判。布隆过滤器说某个元素不在，那么这个元素一定不在。**

redis只能缓存一部分数据，数据不在redis有可能在数据库，布隆过滤器保存了全部的数据，数据不在布隆过滤器就大概率不在数据库。这样当有大量不在数据库里的查询时就不用再查数据库了，没有布隆过滤器的话就是先查redis再查数据库，但是这些数据都不在数据库里，所以每次都要查数据库，这种情况相当于缓存失效了，所以叫缓存穿透。

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

 比如热搜排行上，一个热点新闻失效的同时被同时大量访问就可能导致缓存击穿。

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

## Redis面试题目

### Redis内存

#### Redis 是如何实现 LRU 算法的？

Redis 实现的是一种**近似 LRU 算法**，目的是为了更好的节约内存，它的**实现方式是在 Redis 的对象结构体中添加一个额外的字段，用于记录此数据的最后一次访问时间**。

当 Redis 进行内存淘汰时，会使用**随机采样的方式来淘汰数据**，它是随机取 5 个值（此值可配置），然后**淘汰最久没有使用的那个**。

Redis 实现的 LRU 算法的优点：

- 不用为所有的数据维护一个大链表，节省了空间占用；
- 不用在每次数据访问时都移动链表项，提升了缓存的性能；

但是 LRU 算法有一个问题，**无法解决缓存污染问题**，比如应用一次读取了大量的数据，而这些数据只会被读取这一次，那么这些数据会留存在 Redis 缓存中很长一段时间，造成缓存污染。

### Redis实现可重入锁

```java
package java.redis.impl;

import com.redis.distribute.lock.demo.redis.RedisLock;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
/** redis可重入锁的实现，以及归一化处理，在分布式锁的基础上，增加锁的次数
 * 原理：增加一个ThreadLocal变量来成为计数器，每次加锁计数器加一，只有计数器减为0再释放锁
 */
public class RedisLockImplReentry implements RedisLock {
    @Resource
    private RedisTemplate redisTemplate;
    private static ThreadLocal<String> localUid = new ThreadLocal<String>();
    private static ThreadLocal<Integer> localInteger = new ThreadLocal<Integer>();

    @Override
    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        boolean isLock = false;
        //通过localUid判定本线程是否已经上锁
        if(localUid.get() == null){
            String uuid = UUID.randomUUID().toString();
            localUid.set(uuid);
            isLock = redisTemplate.opsForValue().setIfAbsent(key,uuid,timeout,unit);
            localInteger.set(0);
        }else {
            isLock = true;
        }
        if(isLock){
            //如果已经上锁，则设置重入次数加一
            localInteger.set(localInteger.get()+1);
        }
        return isLock;
    }

    @Override
    public void releaseLock(String key) {
        if(localUid.get() != null
                && localUid.get().equalsIgnoreCase((String) redisTemplate.opsForValue().get(key))){
            if(localInteger.get() != null && localInteger.get() > 0){}
                //如果已经是本线程，并且已经上锁,锁数量大于0
                localInteger.set(localInteger.get()-1);
            }else {
                //计数器减为0则解锁
                redisTemplate.delete(key);
                localUid.remove();
                localInteger.remove();
        }

    }

}

```

