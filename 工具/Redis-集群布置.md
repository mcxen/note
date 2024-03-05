## 一、redis集群的架构。

集群的架构图如下：

<img src="https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20200312121840632.png" alt="image-20200323131503070" style="zoom: 33%;" />

- 存储的过程（用哪个Master节点存储）：

  客户端请求存储的时候（set hello 123），请求发送给集群中的某个节点，这个节点计算出这个key的CRC16哈希值`CRC16(hello)=50018`，再把这个哈希值对16384取模得到哈希槽的编号，然后把这个key-value存储到这个哈希槽。（可以使用redis-cli 以cluster方式连接集群，通过redis的命令`cluster keyslot key的值` 查询哈希槽的值）

  ```bash
  localhost:7000> CLUSTER KEYSLOT hello
  
  (integer) 866
  
  ```

- 主节点（Master）和从节点（Slave）的数据异步复制。可能导致数据丢失。

  Redis Cluster可能丢失写入的第一个原因是因为它使用异步复制。这意味着在写入期间发生以下情况：

  1. 客户端向 Master A 写数据。
  2. Master A 向客户端回复OK。
  3. Master A 将写操作传播到其从机 A1、A2。

  A 在回复客户端之前不会等待A1、A2的确认，因为这会对Redis造成极高的延迟，因此，如果您的客户端写了一些东西，A会确认写。如果A崩溃，并且崩溃之前将写操作发送到其从属、A2，一个未接收到写操作的从属如果被提升为主设备，这个集群节点将永远丢失写操作。

  Redis 集群也可以通过[WAIT](https://redis.io/commands/wait)命令实现的同步写，这使得丢失写的可能性大大降低，但是请注意，即使使用同步复制，Redis Cluster也不实现强一致性：在更复杂的情况下总是有可能的“未执行写操作的从节点（slave）被选举为master”。

- 网络分区可能导致数据丢失。

  Redis 集群另外一种可能会丢失命令的情况是集群出现了网络分区， 并且一个客户端与至少包括一个主节点在内的少数实例被孤立。

  举个例子：假设集群包含 A 、 B 、 C 、 A1 、B1 、C1 六个节点， 其中 A 、B 、C 为主节点， A1 、B1 、C1 为A，B，C的从节点，还有一个客户端 Z1 。假设集群中发生网络分区，那么集群可能会分为两方，大部分的一方包含节点 A 、C 、A1 、B1 和 C1 ，小部分的一方则包含节点 B 和客户端 Z1 。

  Z1仍然能够向主节点B中写入， 如果网络分区发生时间较短，那么集群将会继续正常运作，如果分区的时间足够让大部分的一方将B1选举为新的master，那么Z1写入B中得数据便丢失了。

  注意， 在网络分裂出现期间， 客户端 Z1 可以向主节点 B 发送写命令的最大时间是有限制的， 这一时间限制称为节点超时时间（node timeout）， 是 Redis 集群的一个重要的配置选项。

- 集群之间的通信：如下图。

<img src="https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20200325163548794.png" alt="image-20200325163548794" style="zoom:50%;" />

> 1. 所有的redis节点彼此互联(心跳机制)，内部使用二进制协议优化传输速度和带宽。
> 2. 节点的fail是通过集群中超过半数的节点检测失效时才生效。
> 3. 客户端与redis节点直连，不需要中间proxy层，客户端不需要连接集群所有节点，**连接集群中任何一个可用节点即可**。
> 4. redis-cluster把所有的物理节点映射到 [0-16383] 哈希槽slot上，cluster 负责维护node<->slot<->value

## 二、单机安装redis（mac系统）。

### 2.1、下载并解压redis。

直接去官网https://redis.io/download下载。

<img src="https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20200324113322700.png" alt="image-20200324113322700" style="zoom:50%;" />

### 2.2、安装redis。

下载完成之后解压，我解压后得到`/Users/mcxw/developtools/rediscluster/redis-5.0.8/`目录，cd 到`redis-5.0.8/`目录。运行命令`sudo make install` 就可以安装redis了。

### 2.3、运行redis。

- 启动服务端。

  安装好之后，就可以在任意目录运行`redis-server`命令启动redis服务端。运行服务端会在当前执行`redis-server`命令的目录下面生成redis的数据文件、进程文件等。所以要选择好目录，避免删除文件麻烦。

- 启动客户端。

  在任意目录运行`redis-cli`命令可以启动redis客户端。

![image-20200324114719022](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20200324114719022.png)

现在就安装好了redis并且可以使用了。客户端、服务端都有了。

## 三、集群安装redis。

### 3.1、集群的架构。

上面安装好了redis之后，我们就可以配置多个`redis.conf`文件。通过`redis-server`命令启动多个redis实例。实例图如下：

<img src="https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20200324115414657.png" alt="image-20200324115414657" style="zoom: 33%;" />

### 3.2、配置集群实例的redis.conf文件

- 按照上面的架构图。在`/Users/mcxw/developtools/rediscluster/`目录下执行命令：`mkdir 7000 7001 7002 7003 7004 7005` 新建6个文件夹。

- 复制一份`redis-5.0.8/redis.conf`配置文件，修改参数。我直接复制到`rediscluster`目录。

- 需要修改的内容如下：

  ```bash
  bind 127.0.0.1
  port 6379
  daemonize yes
  pidfile /var/run/redis_6379.pid
  dir ./6379
  cluster-enabled yes 
  cluster-config-file nodes-6379.conf
  cluster-node-timeout 5000 
  appendonly yes
  
  ```

  > bind: 设置哪些地址的client可以连接服务器。
  >
  > port ：服务器接收client连接的端口。集群模式下面还有一个端口port+1000，用于集群服务的通信，所以在生产系统需要打开这2个端口。
  >
  > daemonize：是否后台启动。
  >
  > pidfile：存放redis启动的进程号。
  >
  > dir：配置一个文件夹，redis服务端产生的数据文件都是以这个目录为根目录存储的。默认是`./`也就是在哪个地方运行`redis-server`命令，就在那个地方生成数据文件。
  >
  > cluster-enabled：是否集群模式。
  >
  > cluster-config-file：redis生成的集群的文件。这个不用自己管。redis自己维护。
  >
  > cluster-node-timeout：这个是上面当集群发生网络分区时，超过这个时间将重新选举新的master。
  >
  > appendonly：开启以日志的形式来记录每个**写操作**，并**追加**到文件中。

这里我是本机要运行6个实例，不需要其他机器连接我，所以bind参数就`127.0.0.1`。

把上面配置好的`redis.conf`文件复制一份到`7000 7001 7002 7003 7004 7005` 6个文件夹，并且修改`7000`文件夹下的`redis.conf`文件，把`6379`全部替换为`7000`。其他文件夹下的也把`6379`全部替换为自己的端口。整个配置完成。

### 3.3、启动6个实例。

运行下面的命令启动`7000`端口对应的实例。其他实例改一下`7000`然后运行。

```
redis-server ./7000/redis.conf
```

运行之后，执行命令`netstat -avnp tcp |grep LISTEN`查看监听的端口，可以看到已经启动了。

![image-20200324145547243](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20200324145547243.png)

### 3.4、组建集群。

上面只是以集群模式启动了6个实例，并没有把他们连接为一个集群。

- 查看集群的节点：`redis-cli -p 7000 -c`

  -c 表示以集群模式连接redis服务器。就是以集群方式连接7000端口对应的redis服务器。

  连接后执行`cluster nodes` 可以查看redis的集群有多少节点，如下：

  ```
  mcxw@mcxAir ~ % redis-cli -p 7000 -c
  
  127.0.0.1:7000> cluster nodes
  
  8c54e65215f8fe5b9d6ba4b5195f0a2743751f25 :7000@17000 myself,master - 0 0 0 connected
  ```

- 查看cluster相关的命令：`redis-cli --cluster help`。

- 组建集群命令：`redis-cli --cluster create ip1:port1 ... ipN:portN --cluster-replicas <arg>`

  我们把上面的6台redis组建一个集群：

  ```bash
  redis-cli --cluster create 127.0.0.1:7000 127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 --cluster-replicas 1
  ```

  `--cluster-replicas`表示每个master有几个从节点，我们这里是1。

![image-20200324153332177](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20200324153332177.png)

运行之后，会把`127.0.0.1:7000 127.0.0.1:7001 127.0.0.1:7002`并把哈希槽平均分配给他们，输入`yes`即可执行完。

### 3.5、测试集群。

- 重定向错误（`MOVED`错误）。

```
mcxw@mcxAir rediscluster % redis-cli -h 127.0.0.1 -p 7000
127.0.0.1:7000> set k1 123
(error) MOVED 12706 127.0.0.1:7002
127.0.0.1:7000> CLUSTER KEYSLOT k1
(integer) 12706

```

上面显示：当我客户端不以集群方式连接redis集群的时候，执行命令`set k1 123`服务器返回一个`MOVED`错误。

这是因为`k1`这个键对应的哈希槽是`12706`（查询哈希槽使用`CLUSTER KEYSLOT k1`）不在7000这个redis服务器上。这说明重定向并不是redis服务器做的，是客户端带的。有些客户端对重定向可能不支持。

- 下面`CLUSTER NODES`可以查看到节点和哈希槽的情况。

![image-20200324162239520](https://wkcom.gitee.io/redis/images/image-20200324162239520.png)

`CLUSTER NODES`返回的值：第一个是节点的id。第二个是节点的类型是master或者slave。如果是master，后面会返回节点的哈希槽；如果是slave后面返回该节点的master的id。

- 以集群方式连接redis：`redis-cli -h 127.0.0.1 -p 7000 -c`

  以集群方式连接之后，就可以支持重定向了，如下：

  ```
  mcxw@mcxAir rediscluster % redis-cli -h 127.0.0.1 -p 7000 -c
  127.0.0.1:7000> set k1 456
  -> Redirected to slot [12706] located at 127.0.0.1:7002
  OK
  127.0.0.1:7002> get k1
  "456"
  
  ```

  以同样的方式连接其他节点，也可以获取k1的值，表示集群生效。

## 四、给集群添加节点（扩容）。

### 4.1、给集群添加Master节点：

用上面同样的方式配置并启动2个redis实例`7006 7007`。执行下面的命令可以把**7006作为主节点**加入集群。

```bash
redis-cli --cluster add-node 127.0.0.1:7006 127.0.0.1:7000

```

这里把`127.0.0.1:7006`加入`127.0.0.1:7000`所在的集群，后面这个`127.0.0.1:7000`参数可以是已经存在的集群的任意一个节点。相关命令可以查看帮助`redis-cli --cluster help`。

### 4.2、给Master节点添加Slave节点：

如下命令，把**7007作为7006的从节点**加入集群，这里需要连接集群，查询7006这个节点的id，`--cluster-master-id`后面的参数`0e442bc17b6306eaa8f01ac37eea4ddc9b5497cb`就是查询到的7006节点的id。

```
redis-cli --cluster add-node 127.0.0.1:7007 127.0.0.1:7000 --cluster-slave --cluster-master-id 0e442bc17b6306eaa8f01ac37eea4ddc9b5497cb

```

### 4.3、给新的master分配哈希槽：

连接redis集群，查看节点情况，可以看到7006没有哈希槽。

![image-20200324165853940](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20200324165853940.png)

使用命令：`redis-cli --cluster reshard 127.0.0.1:7000`可以重新分配哈希槽，执行之后会出现提醒：

1. `How many slots do you want to move (from 1 to 16384)?`

   这里是输入想要迁移多少个哈希槽。这里我输入30个。

2. `What is the receiving node ID?`

   这里是接受哈希槽迁移的节点id。这里就是7006节点的id。可以使用命令`redis-cli -h 127.0.0.1 -p 7000 cluster nodes`查看。

3. `Source node #1:`

   这里是输入迁出哈希槽的节点id。输入一个回车之后，可以继续输入。这里我输入其他3个master的节点id，完成之后输入done执行下一步。

4. `Do you want to proceed with the proposed reshard plan (yes/no)?`

   这里输入yes，确认迁移。

迁移完成之后，查看节点情况，可以看到7006有了3段哈希槽，就是刚刚从其他节点迁移的。并且这些哈希槽的数据也迁移过来了。实际中可以给性能低的机器分配少的哈希槽，整个扩容完成。

![image-20200324172356888](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20200324172356888.png)





## 五、删除集群节点（缩容）。

删除集群的节点，要把被删除的节点的哈希槽迁移到其他节点上去，以免丢失数据。通过查看帮助`redis-cli --cluster help`。

### 5.1、查看节点信息。

通过命令`redis-cli -h 127.0.0.1 -p 7000 cluster nodes` 可以查看到节点的信息。

### 5.2、迁移哈希槽。

- **迁移的命令：**

通过下面的命令，可以把哈希槽从`--cluster-from` 的节点迁移到`--cluster-to`的节点：

```bash
redis-cli --cluster reshard 连接集群的ip:端口 --cluster-from 迁移出哈希槽的节点ID --cluster-to 接受迁移的节点ID

```

- **执行迁移**：

这里因为想把`7006`节点删除，所以把`7006`节点的哈希槽迁移到其他节点，我直接迁移到`7000`节点。执行的命令如下：

```bash
redis-cli --cluster reshard 127.0.0.1:7000 --cluster-from 0e442bc17b6306eaa8f01ac37eea4ddc9b5497cb --cluster-to 8c54e65215f8fe5b9d6ba4b5195f0a2743751f25

```

执行过程中会出现下面几个输入：

1. `How many slots do you want to move (from 1 to 16384)?`

   这个是问要迁移多少数量的哈希槽。这里我输入`7006`节点的全部哈希槽数量（执行迁移的时候会显示这个数量）。

2. `Do you want to proceed with the proposed reshard plan (yes/no)?`

   这里问是否确认迁移，输入yes确认。

3. 查看迁移情况：

通过命令`redis-cli -h 127.0.0.1 -p 7000 cluster nodes` 可以查看到7006没有哈希槽。

> 如果节点的哈希槽全部没有了。一会之后，集群内部会把这个Master节点下的Slave节点分配给其他节点。

### 5.3、删除节点。

这里要删除7006这个master节点和它的slave节点7007。先删除从节点，避免集群认为master挂掉从新选举master，引起性能浪费。

**删除节点的命令如下：**

```bash
redis-cli --cluster del-node 集群的ip:port 要删除的节点ID

```

## 六、参考资料：

docker官方集群搭建：https://redis.io/topics/cluster-tutorial

docker中文集群搭建：http://www.redis.cn/topics/cluster-tutorial.html

B 站视频：https://www.bilibili.com/video/av82440854