# 1.MQ的相关概念

[学习视频地址](https://www.bilibili.com/video/BV1cb4y1o7zz?p=1&vd_source=6aafd031757cd8c1dbbb98344fb3d363)

[[toc]]

## 什么是MQ

MQ(message queue)，从字面意思上看，本质是个队列，FIFO 先入先出，只不过队列中存放的内容是 message 而已，还是一种跨进程的通信机制，用于上下游传递消息。在互联网架构中，MQ 是一种非常常 见的上下游「逻辑解耦 + 物理解耦」的消息通信服务。使用了 MQ 之后，消息发送上游只需要依赖 MQ，不用依赖其他服务。

## 为什么要用MQ

1. 流量消峰

   举个例子，如果订单系统最多能处理一万次订单，这个处理能力应付正常时段的下单时绰绰有余，正常时段我们下单一秒后就能返回结果。但是在高峰期，如果有两万次下单操作系统是处理不了的，只能限制订单超过一万后不允许用户下单。使用消息队列做缓冲，我们可以取消这个限制，**把一秒内下的订单分 散成一段时间来处理，这时有些用户可能在下单十几秒后才能收到下单成功的操作，但是比不能下单的体验要好。**

2. 应用解耦

   以电商应用为例，应用中有订单系统、库存系统、物流系统、支付系统。用户创建订单后，如果耦合调用库存系统、物流系统、支付系统，任何一个子系统出了故障，都会造成下单操作异常。当转变成基于消息队列的方式后，系统间调用的问题会减少很多，比如物流系统因为发生故障，需要几分钟来修复。在这几分钟的时间里，物流系统要处理的内存被缓存在消息队列中**，用户的下单操作可以正常完成。当物流系统恢复后，继续处理订单信息即可，中单用户感受不到物流系统的故障，**提升系统的可用性。



## MQ的分类

- **ActiveMQ**

  优点：单机吞吐量万级，时效性 ms 级，可用性高，基于主从架构实现高可用性，消息可靠性较 低的概率丢失数据

  缺点：官方社区现在对 ActiveMQ 5.x 维护越来越少，高吞吐量场景较少使用

- **Kafka**

  大数据的杀手锏，谈到大数据领域内的消息传输，则绕不开 Kafka，这款为大数据而生的消息中间件，以其百万级 TPS 的吞吐量名声大噪，迅速成为大数据领域的宠儿，在数据采集、传输、存储的过程中发挥着举足轻重的作用。目前已经被 LinkedIn，Uber，Twitter，Netflix 等大公司所采纳。

  优点: 性能卓越，单机写入 TPS 约在百万条/秒，最大的优点，就是吞吐量高。时效性 ms 级可用性非常高，kafka 是分布式的，一个数据多个副本，少数机器宕机，不会丢失数据，不会导致不可用,消费者采用 Pull 方式获取消息，消息有序，通过控制能够保证所有消息被消费且仅被消费一次；有优秀的第三方Kafka Web 管理界面 Kafka-Manager；在日志领域比较成熟，被多家公司和多个开源项目使用；功能支持：功能 较为简单，主要支持简单的 MQ 功能，在大数据领域的实时计算以及日志采集被大规模使用

  缺点：Kafka 单机超过 64 个队列/分区，Load 会发生明显的飙高现象，队列越多，load 越高，发送消息响应时间变长，使用短轮询方式，实时性取决于轮询间隔时间，消费失败不支持重试；支持消息顺序，但是一台代理宕机后，就会产生消息乱序，社区更新较慢

- **RocketMQ**

  RocketMQ 出自阿里巴巴的开源产品，用 Java 语言实现，在设计时参考了 Kafka，并做出了自己的一些改进。被阿里巴巴广泛应用在订单，交易，充值，流计算，消息推送，日志流式处理，binglog 分发等场景。

  优点：单机吞吐量十万级，可用性非常高，分布式架构,消息可以做到 0 丢失,MQ 功能较为完善，还是分布式的，扩展性好,支持 10 亿级别的消息堆积，不会因为堆积导致性能下降，源码是 java 我们可以自己阅读源码，定制自己公司的 MQ

  缺点：支持的客户端语言不多，目前是 java 及 c++，其中 c++ 不成熟；社区活跃度一般,没有在 MQ 核心中去实现 JMS 等接口,有些系统要迁移需要修改大量代码

- **RabbitMQ**

  2007 年发布，是一个在AMQP(高级消息队列协议)基础上完成的，可复用的企业消息系统，是当前最主流的消息中间件之一。

  优点：由于 erlang 语言的高并发特性，性能较好；吞吐量到万级，MQ 功能比较完备,健壮、稳定、易用、跨平台、支持多种语言 如：Python、Ruby、.NET、Java、JMS、C、PHP、ActionScript、XMPP、STOMP 等，支持 AJAX 文档齐全；**开源提供的管理界面**非常棒，用起来很好用，**社区活跃度高；**更新频率相当高

  缺点：商业版需要收费,学习成本较高

## MQ的选择

- **Kafka**

  Kafka 主要特点是基于 Pull 的模式来处理消息消费，追求高吞吐量，一开始的目的就是用于日志收集和传输，适合产生大量数据的互联网服务的数据收集业务。大型公司建议可以选用，如果有日志采集功能，肯定是首选 kafka 了。

- **RocketMQ**

  天生为金融互联网领域而生，对于可靠性要求很高的场景，尤其是电商里面的订单扣款，以及业务削峰，在大量交易涌入时，后端可能无法及时处理的情况。RoketMQ 在稳定性上可能更值得信赖，这些业务场景在阿里双 11 已经经历了多次考验，如果你的业务有上述并发场景，建议可以选择 RocketMQ。

- **RabbitMQ**

  结合 erlang 语言本身的并发优势，性能好时效性微秒级，社区活跃度也比较高，管理界面用起来十分 方便，如果你的数据量没有那么大，中小型公司优先选择功能比较完备的 RabbitMQ。



# 2.RabbitMQ 介绍

## RabbitMQ的概念

RabbitMQ 是一个消息中间件：它接受并转发消息。你可以把它当做一个快递站点，当你要发送一个包裹时，你把你的包裹放到快递站，快递员最终会把你的快递送到收件人那里，按照这种逻辑 RabbitMQ 是 一个快递站，一个快递员帮你传递快件。RabbitMQ 与快递站的主要区别在于，它不处理快件而是接收，存储和转发消息数据。

![img](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/16f7a401462b6050~tplv-t2oaga2asx-zoom-in-crop-mark:4536:0:0:0.image)

### 1. Publisher（发布者）

发布者 (或称为生产者) 负责生产消息并将其投递到指定的交换器上。

### 2. Message（消息）

消息由消息头和消息体组成。消息头用于存储与消息相关的元数据：如目标交换器的名字 (exchange_name) 、路由键 (RountingKey) 和其他可选配置 (properties) 信息。消息体为实际需要传递的数据。

### 3. Exchange（交换器）

交换器负责接收来自生产者的消息，并将将消息路由到一个或者多个队列中，如果路由不到，则返回给生产者或者直接丢弃，这取决于交换器的 mandatory 属性：

- 当 mandatory 为 true 时：如果交换器无法根据自身类型和路由键找到一个符合条件的队列，则会将该消息返回给生产者；
- 当 mandatory 为 false 时：如果交换器无法根据自身类型和路由键找到一个符合条件的队列，则会直接丢弃该消息。

### 4. BindingKey (绑定键）

交换器与队列通过 BindingKey 建立绑定关系。

### 5. Routingkey（路由键）

生产者将消息发给交换器的时候，一般会指定一个 RountingKey，用来指定这个消息的路由规则。当 RountingKey 与 BindingKey 基于交换器类型的规则相匹配时，消息被路由到对应的队列中。

### 6. Queue（消息队列）

用于存储路由过来的消息。多个消费者可以订阅同一个消息队列，此时队列会将收到的消息将以轮询 (round-robin) 的方式分发给所有消费者。即每条消息只会发送给一个消费者，不会出现一条消息被多个消费者重复消费的情况。

### 7. Consumer（消费者）

消费者订阅感兴趣的队列，并负责消费存储在队列中的消息。为了保证消息能够从队列可靠地到达消费者，RabbitMQ 提供了消息确认机制 (message acknowledgement)，并通过 autoAck 参数来进行控制：

- 当 autoAck 为 true 时：此时消息发送出去 (写入TCP套接字) 后就认为消费成功，而不管消费者是否真正消费到这些消息。当 TCP 连接或 channel 因意外而关闭，或者消费者在消费过程之中意外宕机时，对应的消息就丢失。因此这种模式可以提高吞吐量，但会存在数据丢失的风险。
- 当 autoAck 为 false 时：需要用户在数据处理完成后进行手动确认，只有用户手动确认完成后，RabbitMQ 才认为这条消息已经被成功处理。这可以保证数据的可靠性投递，但会降低系统的吞吐量。

### 8. Connection（连接）

用于传递消息的 TCP 连接。

### 9. Channel（信道）

RabbitMQ 采用类似 NIO (非阻塞式 IO ) 的设计，通过 Channel 来复用 TCP 连接，并确保每个 Channel 的隔离性，就像是拥有独立的 Connection 连接。当数据流量不是很大时，采用连接复用技术可以避免创建过多的 TCP 连接而导致昂贵的性能开销。

### 10. Virtual Host（虚拟主机）

RabbitMQ 通过虚拟主机来实现逻辑分组和资源隔离，一个虚拟主机就是一个小型的 RabbitMQ 服务器，拥有独立的队列、交换器和绑定关系。用户可以按照不同业务场景建立不同的虚拟主机，虚拟主机之间是完全独立的，你无法将 vhost1 上的交换器与 vhost2 上的队列进行绑定，这可以极大的保证业务之间的隔离性和数据安全。默认的虚拟主机名为 `/` 。

### 11. Broker

一个真实部署运行的 RabbitMQ 服务。



## 四大核心概念

生产者：产生数据发送消息的程序

交换机：是 RabbitMQ 非常重要的一个部件，一方面它接收来自生产者的消息，另一方面它将消息 推送到队列中。交换机必须确切知道如何处理它接收到的消息，是将这些消息推送到特定队列还是推送到多个队列，亦或者是把消息丢弃，这个得有交换机类型决定

队列：是 RabbitMQ 内部使用的一种数据结构，尽管消息流经 RabbitMQ 和应用程序，但它们只能存储在队列中。队列仅受主机的内存和磁盘限制的约束，本质上是一个大的消息缓冲区。许多生产者可以将消息发送到一个队列，许多消费者可以尝试从一个队列接收数据。这就是我们使用队列的方式

消费者：消费与接收具有相似的含义。消费者大多时候是一个等待接收消息的程序。请注意生产者，消费者和消息中间件很多时候并不在同一机器上。同一个应用程序既可以是生产者又是可以是消费者。

## RabbitMQ特性

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.dso2q1lbc5s.webp)

## 各个名词介绍

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.15tq7c46aeyk.webp)

`Broker`：接收和分发消息的应用，RabbitMQ Server 就是 Message Broker

`Virtual host`：出于多租户和安全因素设计的，把 AMQP 的基本组件划分到一个虚拟的分组中，类似于网络中的 namespace 概念。当多个不同的用户使用同一个 RabbitMQ server 提供的服务时，可以划分出多个 vhost，每个用户在自己的 vhost 创建 exchange／queue 等

`Connection`：publisher／consumer 和 broker 之间的 TCP 连接

`Channel`：如果每一次访问 RabbitMQ 都建立一个 Connection，在消息量大的时候建立 TCP Connection 的开销将是巨大的，效率也较低。Channel 是在 connection 内部建立的逻辑连接，如果应用程序支持多线程，通常每个 thread 创建单独的 channel 进行通讯，AMQP method 包含了 channel id 帮助客 户端和 message broker 识别 channel，所以 channel 之间是完全隔离的。Channel 作为轻量级的 Connection 极大减少了操作系统建立 TCP connection 的开销

`Exchange`：message 到达 broker 的第一站，根据分发规则，匹配查询表中的 routing key，分发 消息到 queue 中去。常用的类型有：direct (point-to-point)，topic (publish-subscribe) and fanout (multicast)

`Queue`：消息最终被送到这里等待 consumer 取走

`Binding`：exchange 和 queue 之间的虚拟连接，binding 中可以包含 routing key，Binding 信息被保 存到 exchange 中的查询表中，用于 message 的分发依据

# 3.RabbitMQ 安装

## 环境

Linux 的 `CentOS 7.x` 版本。

`Xftp` 传输安装包到 Linux。

`Xshell` 连接 Linux，进行解压安装。

## 下载

首先我们需要2个 `rpm` 安装包，分别是 RabbitMQ 安装包和 Erlang 环境安装包，后者提供环境给前者运行。

### 下载RabbitMQ

[RabbitMQ最新版下载地址](https://github.com/rabbitmq/rabbitmq-server/releases)

选择以 `noarch.rpm` 结尾的安装包

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220723/image.402esff13ek0.webp)

### 下载Erlang

RabbitMQ 是采用 Erlang 语言开发的，所以系统环境必须提供 Erlang 环境，需要先安装 Erlang。

`Erlang` 和 `RabbitMQ` 版本对照：[点击跳转](https://www.rabbitmq.com/which-erlang.html)

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220723/image.4lr4qyk601a0.webp)

这里安装的是 3.8.8 版本的 RabbitMQ，需要的 Erlang 版本依然是21.3。

[Erlang 21.3下载地址](https://packagecloud.io/rabbitmq/erlang/packages/el/7/erlang-21.3.8.16-1.el7.x86_64.rpm)

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.44zjpnbpbyo0.webp)

::: tip 版本选择

CentOs 7.x 版本需要e17。

CentOs 8.x 版本需要e18。包括 Red Hat 8,modern Fedora 版本。

:::

最终下载的两个安装包，如图：

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.63s3aftotzs0.webp)

## 安装

### 安装RabbitMQ

```sh
echo "export LC_ALL=en_US.UTF-8"  >>  /etc/profile
source /etc/profile
```

下面两个安装方法，任选其一即可，推荐方法一：
2 方法一（推荐）
第一步：执行

```sh
curl -s https://packagecloud.io/install/repositories/rabbitmq/rabbitmq-server/script.rpm.sh | sudo bash
```

第二步，执行：

```sh
curl -s https://packagecloud.io/install/repositories/rabbitmq/erlang/script.rpm.sh | sudo bash
```

第三步：

```sh
sudo yum install rabbitmq-server-3.8.2-1.el7.noarch
```



### 启动

```shell
# 启动RabbitMQ
$ systemctl start rabbitmq-server
#停止RabbitMQ
$rabbitmqctl stop
# 查看服务状态
$ rabbitmqctl status
# 要检查RabbitMQ服务器的状态，请运行：
$ systemctl status rabbitmq-server
```

启动 `RabbitMQ` 服务后，查看该服务状态，显示绿色的 `active` 则表示服务安装并启动成功

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.3vmzww3gk2k0.webp)

其他指令：

```sh
# 开机自启动
systemctl enable rabbitmq-server
# 停止服务
systemctl stop rabbitmq-server
# 重启服务
systemctl restart rabbitmq-server
```

## 管理界面及授权操作

::: tip 默认端口

RabbitMQ 的默认访问端口是 15672

如果 Linux 有防火墙，记得开放 15672 端口，否则 Windows 无法访问

:::

默认情况下，RabbiMQ 没有安装 Web 端的客户端软件，需要安装才可以生效

```sh
rabbitmq-plugins enable rabbitmq_management
```

安装完毕以后，重启服务

```sh
systemctl restart rabbitmq-server
```

通过 `http://localhost:15672` 访问，ip 为 Linux 的 ip。`rabbitmq` 有一个默认的账号密码 `guest`，但是登录该账号密码会出现权限问题

> 查看状态：
>
> ```sh
> [root@localhost ~]# systemctl status rabbitmq-server
> ● rabbitmq-server.service - RabbitMQ broker
>    Loaded: loaded (/usr/lib/systemd/system/rabbitmq-server.service; disabled; vendor preset: disabled)
>    Active: active (running) since Thu 2023-04-27 11:20:06 CST; 8s ago
>   Process: 73343 ExecStop=/usr/sbin/rabbitmqctl shutdown (code=exited, status=0/SUCCESS)
>  Main PID: 73396 (beam.smp)
>    CGroup: /system.slice/rabbitmq-server.service
>            ├─73396 /usr/lib64/erlang/erts-11.2.2.10/bin/beam.smp -W w -MBas ageffc...
>            ├─73411 erl_child_setup 32768
>            ├─73463 inet_gethost 4
>            └─73464 inet_gethost 4
> ```
>
> 

> 还可以关闭防火墙：
>
> ```sh
> systemctl stop firewalld
> ```
>
> 

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.1irradyf2v28.webp)

默认的账号密码仅限于本机 localhost 进行访问，所以需要添加一个远程登录的用户

```sh
# 创建账号和密码
rabbitmqctl add_user 用户名 密码

# 设置用户角色
rabbitmqctl set_user_tags 用户名 角色

# 为用户添加资源权限，添加配置、写、读权限
# set_permissions [-p <vhostpath>] <user> <conf> <write> <read>
rabbitmqctl set_permissions -p "/" y ".*" ".*" ".*"
// Make sure to add code blocks to your code group
```

添加admin用户：
`rabbitmqctl add_user admin password`
`rabbitmqctl set_user_tags admin administrator`



角色固定有四种级别：

- `administrator`：可以登录控制台、查看所有信息、并对rabbitmq进行管理
- `monToring`：监控者；登录控制台，查看所有信息
- `policymaker`：策略制定者；登录控制台指定策略
- `managment`：普通管理员；登录控制

添加用户和权限后，再次访问 `http://ip:15672` 登录，输入添加好的用户名和密码，即可进入后台

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.4o5hgcxxknm0.webp)

其他指令：

```sh
# 修改密码
rabbitmqctl change_ password 用户名 新密码

# 删除用户
rabbitmqctl delete_user 用户名

# 查看用户清单
rabbitmqctl list_user
```

## Docker安装RabbitMQ

```sh
# 安装启动 rabbitmq 容器
docker run -d --name myRabbitMQ -e RABBITMQ_DEFAULT_USER=用户名 -e RABBITMQ_DEFAULT_PASS=密码 -p 15672:15672 -p 5672:5672 rabbitmq:3.8.14-management
```



# 4.RabbitMQ 入门案例



## Hello RabbitMQ

用 Java 编写两个程序。发送单个消息的生产者和接收消息并打印出来的消费者

在下图中，“ P” 是我们的生产者，“ C” 是我们的消费者。中间的框是一个队列 RabbitMQ 代表使用者保留的消息缓冲区

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220723/image.53zsdpm4hbk0.webp)

::: tip 注意

Java 进行连接的时候，需要 Linux 开放 5672 端口，否则会连接超时

访问 Web 界面的端口是 15672，连接服务器的端口是 5672

:::

步骤图：

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220723/image.5zalz3u39nk0.webp)

### 添加依赖

先创建好 Maven 工程，pom.xml 添入依赖：

```xml
    <dependencies>
        <!--rabbitmq 依赖客户端-->
        <dependency>
            <groupId>com.rabbitmq</groupId>
            <artifactId>amqp-client</artifactId>
            <version>5.8.0</version>
        </dependency>
        <!--操作文件流的一个依赖-->
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.6</version>
        </dependency>
    </dependencies>

    <!--指定 jdk 编译版本-->
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>8</source>
                    <target>8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

版本根据需求选择

### 消息生产者

创建一个类作为生产者，最终生产消息到 RabbitMQ 的队列里

步骤：

1. 创建 RabbitMQ 连接工厂
2. 进行 RabbitMQ 工厂配置信息
3. 创建 RabbitMQ 连接
4. 创建 RabbitMQ 信道
5. 生成一个队列
6. 发送一个消息到交换机，交换机发送到队列。"" 代表默认交换机

```java
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {

    //对列名称
    public static final String QUEUE_NAME="hello";

    //发消息
    public static void main(String[] args) throws IOException, TimeoutException {
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //工厂IP 连接RabbitMQ对列
        factory.setHost("localhost");
        //用户名
        factory.setUsername("admin");
        //密码
        factory.setPassword("password");

        //创建连接
        Connection connection = factory.newConnection();
        //获取信道
        Channel channel = connection.createChannel();
        /**
         * 生产一个对列
         * 1.对列名称
         * 2.对列里面的消息是否持久化，默认情况下，消息存储在内存中
         * 3.该队列是否只供一个消费者进行消费，是否进行消息共享，true可以多个消费者消费 false：只能一个消费者消费
         * 4.是否自动删除，最后一个消费者端开链接以后，该队列是否自动删除，true表示自动删除
         * 5.其他参数
         */
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        //发消息
        String message = "Hello,world";
        /**
         * 发送一个消息
         * 1.发送到哪个交换机
         * 2.路由的key值是哪个本次是队列的名称
         * 3.其他参数信息
         * 4.发送消息的消息体
         */
        channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
        System.out.println("消息发送完毕");
        channel.close();
        connection.close();
    }
}
```

+ 设置权限
+ ![image-20230427095738237](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230427095738237.png)
+ 结果

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220723/image.x5zo4wuta74.webp)

消息队列名字和步骤 2 的信息根据自己的需求进行配置

**方法解释**

声明队列：

```java
channel.queueDeclare(队列名/String, 持久化/boolean, 共享消费/boolean, 自动删除/boolean, 配置参数/Map);
```

配置参数现在是 null，后面死信队列延迟队列等会用到，如：

队列的优先级

队列里的消息如果没有被消费，何去何从？（死信队列）

```java
Map<String, Object> params = new HashMap();
// 设置队列的最大优先级 最大可以设置到 255 官网推荐 1-10 如果设置太高比较吃内存和 CPU
params.put("x-max-priority", 10);
// 声明当前队列绑定的死信交换机
params.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
// 声明当前队列的死信路由 key
params.put("x-dead-letter-routing-key", "YD");
channel.queueDeclare(QUEUE_NAME, true, false, false, params);
```

发布消息：

```java
channel.basicPublish(交换机名/String, 队列名/String, 配置参数/Map, 消息/String);
```

配置参数现在是 null，后面死信队列、延迟队列等会用到，如：

发布的消息优先级

发布的消息标识符 id

```java
// 给消息赋予 优先级 ID 属性
AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().priority(10).messageId("1")build();
channel.basicPublish("", QUEUE_NAME, properties, message.getBytes());
```

### 消息消费者-接受消息

创建一个类作为消费者，消费 RabbitMQ 队列的消息

```java
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author frx
 * @version 1.0
 * @date 2022/7/23  22:21
 * desc:消费者：接受消息
 */
public class Consumer {

    //队列的名称
    public static final String QUEUE_NAME="hello";
    //接受消息
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("password");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
//前面都是一样的
        //声明接收消息
        DeliverCallback deliverCallback = (consumerTag,message) -> {
            System.out.println(new String(message.getBody()));
        };
        //取消消息时的回调
        CancelCallback cancelCallback = consumerTag ->{
            System.out.println("消息消费被中断");
        };

        /**
         * 消费者消费消息
         * 1.消费哪个队列
         * 2.消费成功之后是否要自动应答true：代表自动应答false:代表手动应答
         * 3.消费者未成功消费的回调，消息没收到的时候的回调
         * 4.消费者取消消费的回调，消息收到之后ack确认信息。
         */
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}
```

+ 结果

```java
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Hello,world
```

值得一提的是，`basicConsume` 的参数中，第三个和第四个参数都是接口，所以需要实现该接口的方法

```java
channel.basicConsume(队列名字/String, 是否自动签收/boolean, 消费时的回调/接口类, 无法消费的回调/接口类);
```

## Work Queues多个接受信息的

```java

```





## Work Queues

Work Queues 是工作队列（又称任务队列）的主要思想是避免立即执行资源密集型任务，而不得不等待它完成。相反我们安排任务在之后执行。我们把任务封装为消息并将其发送到队列。在后台运行的工作进程将弹出任务并最终执行作业。当有多个工作线程时，这些工作线程将一起处理这些任务。

### 轮询消费

轮询消费消息指的是轮流消费消息，即每个工作队列都会获取一个消息进行消费，并且获取的次数按照顺序依次往下轮流。

案例中生产者叫做 NewTask，一个消费者就是一个工作队列，启动两个工作队列消费消息，这个两个工作队列会以轮询的方式消费消息。

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220723/image.3iwquz975vw0.webp)

### 轮询案例

- 首先把 RabbitMQ 的配置参数封装为一个工具类：`RabbitMQUtils`

```java
package utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author frx
 * @version 1.0
 * @date 2022/7/23  22:46
 * desc:此类为连接工厂创建信道的工具类
 */
public class RabbitMQUtils {

    public static Channel getChannel() throws IOException, TimeoutException {

        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //工厂IP 连接RabbitMQ对列
        factory.setHost("192.168.198.130");
        //用户名
        factory.setUsername("admin");
        //密码
        factory.setPassword("password");

        //创建连接
        Connection connection = factory.newConnection();
        //获取信道
        Channel channel = connection.createChannel();

        return channel;
    }
}
```

+ 创建两个工作队列worker，这是负责接受消息的worker

```java
package workqueues;

import com.rabbitmq.client.*;
import utils.RabbitMQUtils;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class worker {
    public static final String QUEUE_NAME="task_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        System.out.println("开始接受信息");
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            System.out.println("接收到的消息:"+new String(message.getBody()));
        };

        //消息接受被取消时，执行下面的内容
        CancelCallback cancelCallback = consumerTag -> {
            System.out.println(consumerTag+"消息被消费者取消消费接口回调逻辑");
        };
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}

```

创建好一个工作队列，只需要以多线程方式启动两次该 main 函数即可，以 first、second 区别消息队列。

要开启多线程功能，首先启动该消息队列，然后如图开启多线程：

![image-20230427115518031](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20230427115518031.png)



两个工作队列都启动后

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220723/image.3fh698ujarc0.webp)

+ 创建一个生产者NewTask，发送消息进程

- - 发送十条消息

```java
package workqueues;

import com.rabbitmq.client.*;
import utils.RabbitMQUtils;
import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class NewTask {
    public static final String QUEUE_NAME="task_queue";
    //接受消息
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.198.130");
        factory.setUsername("admin");
        factory.setPassword("password");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
//前面都是一样的
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        for (int i = 0; i < 10; i++) {
//            发十个消息
            String message = "消息："+i+"..";
            /**
             * 发送一个消息
             * 1.发送到哪个交换机
             * 2.路由的key值是哪个本次是队列的名称
             * 3.其他参数信息
             * 4.发送消息的消息体
             */
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            System.out.println("发送了消息： message = " + message);
        }
        channel.close();
    }

}

```

> ```java
> import com.rabbitmq.client.*;
> import utils.RabbitMQUtils;
> import java.io.IOException;
> import java.util.concurrent.TimeoutException;
> 
> public class NewTask2 {
> 
>     //队列名称
>     public static final String QUEUE_NAME="task_queue";
> 
>     //发送大量消息
>     public static void main(String[] args) throws IOException, TimeoutException {
> 
>         Channel channel = RabbitMQUtils.getChannel();
>         //队列的声明
>         channel.queueDeclare(QUEUE_NAME,false,false,false,null);
> 
>         //发送消息
>         //从控制台当中接受信息
>         Scanner scanner = new Scanner(System.in);
>         while (scanner.hasNext()) {
>             String message = scanner.next();
>             channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
>             System.out.println("消息发送完成:"+message);
>         }
>     }
>     
> }
> ```
>
> 

+ 结果演示

通过程序执行发现生产者总共发送 4 个消息，消费者 first 和消费者 second 分别分得两个消息，并且是按照有序的一个接收一次消息

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220723/image.13a9691kkkyo.webp)

## Web页面添加队列

进入自己的 RabbitMQ Web 页面，点击 Queues 菜单

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.701dj3uqk400.webp)

1. 点击 `Queues` 菜单
2. 点击 `Add a new queue`，弹出下拉菜单
3. 下方的很多参数可以进行选择（旁边有 ？的参数），如优先级（`Lazy mode`）、绑定死信队列（`Dead letter exchange/routing key`）
4. 执行步骤 3 后，在 `Arguments` 的第一个文本框弹出对应的参数，类似于 Map 的 key
5. 第二个文本框填写参数，类似于 Map 的 value
6. 第三个是下拉菜单，选择 value 类型
7. 点击 `Add queue`，添加队列

旁边有 ？的参数，就是 `channel.queueDeclare(队列名/String, 持久化/boolean, 共享消费/boolean, 自动删除/boolean, 配置参数/Map);` 或者 `channel.basicPublish(交换机名/String, 队列名/String, 配置参数/Map, 消息/String);` 的参数：配置参数/Map 的 key



# 5.RabbitMQ 消息应答与发布

[[toc]]

## 消息应答

消费者完成一个任务可能需要一段时间，如果其中一个消费者处理一个长的任务并仅只完成了部分突然它挂掉了，会发生什么情况。RabbitMQ 一旦向消费者传递了一条消息，便立即将该消息标记为删除。在这种情况下，突然有个消费者挂掉了，我们将丢失正在处理的消息。以及后续发送给该消费者的消息，因为它无法接收到。

为了保证消息在发送过程中不丢失，引入消息应答机制，消息应答就是：<mark>消费者在接收到消息并且处理该消息之后，告诉 rabbitmq 它已经处理了，rabbitmq 可以把该消息删除了</mark>。

### 自动应答

消息发送后立即被认为已经传送成功，这种模式需要在**高吞吐量和数据传输安全性方面做权衡**,因为这种模式如果消息在接收到之前，消费者那边出现连接或者 channel 关闭，那么消息就丢失了,当然另一方面这种模式消费者那边可以传递过载的消息，**没有对传递的消息数量进行限制**，当然这样有可能使得消费者这边由于接收太多还来不及处理的消息，导致这些消息的积压，最终使得内存耗尽，最终这些消费者线程被操作系统杀死，**所以这种模式仅适用在消费者可以高效并以 某种速率能够处理这些消息的情况下使用。**

### 手动消息应答的方法

- `Channel.basicAck` (肯定确认应答)：

```java
basicAck(long deliveryTag, boolean multiple);
```

第一个参数是消息的标记，第二个参数表示是否应用于多消息，RabbitMQ 已知道该消息并且成功的处理消息，可以将其丢弃了

+ `Channel.basicReject` (否定确认应答)

```java
basicReject(long deliveryTag, boolean requeue);
```

第一个参数表示拒绝 `deliveryTag` 对应的消息，第二个参数表示是否 `requeue`：true 则重新入队列，false 则丢弃或者进入死信队列。

该方法 reject 后，该消费者还是会消费到该条被 reject 的消息。

+ `Channel.basicNack` (用于否定确认)：示己拒绝处理该消息，可以将其丢弃了

```java
basicNack(long deliveryTag, boolean multiple, boolean requeue);
```

第一个参数表示拒绝 `deliveryTag` 对应的消息，第二个参数是表示否应用于多消息，第三个参数表示是否 `requeue`，与 basicReject 区别就是同时支持多个消息，可以 拒绝签收 该消费者先前接收未 ack 的所有消息。拒绝签收后的消息也会被自己消费到。

+ `Channel.basicRecover`

```java
basicRecover(boolean requeue);
```

是否恢复消息到队列，参数是是否 `requeue`，true 则重新入队列，并且尽可能的将之前 `recover` 的消息投递给其他消费者消费，而不是自己再次消费。false 则消息会重新被投递给自己。

**Multiple 的解释：**

手动应答的好处是可以批量应答并且减少网络拥堵

- true 代表批量应答 channel 上未应答的消息

  比如说 channel 上有传送 tag 的消息 5,6,7,8 当前 tag 是 8 那么此时 5-8 的这些还未应答的消息都会被确认收到消息应答

- false 同上面相比只会应答 tag=8 的消息 5,6,7 这三个消息依然不会被确认收到消息应答

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220724/image.3vspi8fu4v20.webp)

### 消息自动重新入队

如果消费者由于某些原因失去连接(其通道已关闭，连接已关闭或 TCP 连接丢失)，导致消息未发送 ACK 确认，RabbitMQ 将了解到消息未完全处理，并将对其重新排队。如果此时其他消费者可以处理，它将很快将其重新分发给另一个消费者。这样，即使某个消费者偶尔死亡，也可以确保不会丢失任何消息。

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.4hplocbhsdk0.webp)

### 手动应答案例

默认消息采用的是自动应答，所以我们要想实现消息消费过程中不丢失，需要把自动应答改为手动应答

消费者启用两个线程，消费 1 一秒消费一个消息，消费者 2 十秒消费一个消息，然后在消费者 2 消费消息的时候，停止运行，这时正在消费的消息是否会重新进入队列，而后给消费者 1 消费呢？

+ 工具类

```java
public class SleepUtils {
    public static void sleep(int second){
        try {
            Thread.sleep(1000*second);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

**消息生产者：**

```java
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/24  12:15
 * desc:消息在手动应答是不丢失、放回队列中重新消费
 */
public class Task02 {

    //队列名称
    public static final String TASK_QUEUE_NAME = "ACK_QUEUE";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        //声明队列
        channel.queueDeclare(TASK_QUEUE_NAME,false,false,false,null);
        //在控制台中输入信息
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入信息：");
        while (scanner.hasNext()){
            String message = scanner.next();
            channel.basicPublish("",TASK_QUEUE_NAME,null,message.getBytes("UTF-8"));
            System.out.println("生产者发出消息:"+ message);
        }

    }

}
```

**消费者 1：**

消费者在简单案例代码的基础上增加了以下内容

```java
channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
// 采用手动应答
boolean autoAck = false;
channel.basicConsume(TASK_QUEUE_NAME, autoAck, deliverCallback, cancelCallback);
```

完整代码

```java {27,36}
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/24  12:29
 * desc:消息在手动应答是不丢失、放回队列中重新消费
 */
public class Work03 {

    //队列名称
    public static final String TASK_QUEUE_NAME = "ACK_QUEUE";

    //接受消息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        System.out.println("C1等待接受消息处理时间较短");

        DeliverCallback deliverCallback =(consumerTag,message) ->{

            //沉睡1S
            SleepUtils.sleep(1);
            System.out.println("接受到的消息:"+new String(message.getBody(),"UTF-8"));
            //手动应答
            /**
             * 1.消息的标记Tag
             * 2.是否批量应答 false表示不批量应答信道中的消息
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);

        };

        CancelCallback cancelCallback = (consumerTag -> {
            System.out.println(consumerTag + "消费者取消消费接口回调逻辑");

        });
        //采用手动应答
        boolean autoAck = false;
        channel.basicConsume(TASK_QUEUE_NAME,autoAck,deliverCallback,cancelCallback);
    }
}
```

**消费者 2：**

将 20 行代码的睡眠时间改为 10 秒：

```java
SleepUtils.sleep(10);
```

### 效果演示

正常情况下消息生产者发送两个消息， first 和 second 分别接收到消息并进行处理

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220724/image.lggsifrqz8w.webp)

当发送者发送消息 DD 到队列，此时是 second 来消费该消息，但是由于它处理时间较长，在还未处理完时间里停止运行，也就是说 second 还没有执行到 ack 代码的时候，second 被停掉了，此时会看到消息被 first 接收到了，说明消息 DD 被重新入队，然后分配给能处理消息的 first 处理了

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.3mng0kiqphi0.webp)

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.7418cd7x0u80.webp)

## RabbitMQ持久化

当 RabbitMQ 服务停掉以后，消息生产者发送过来的消息不丢失要如何保障？默认情况下 RabbitMQ 退出或由于某种原因崩溃时，它忽视队列和消息，除非告知它不要这样做。确保消息不会丢失需要做两件事：**我们需要将队列和消息都标记为持久化。**

### 队列持久化

之前我们创建的队列都是非持久化的，RabbitMQ 如果重启的化，该队列就会被删除掉，如果要队列实现持久化需要在声明队列的时候把 durable 参数设置为true，代表开启持久化

在**消息生产者**开启持久化：

```java {10,12}
public class Task02 {

    //队列名称
    public static final String TASK_QUEUE_NAME = "ACK_QUEUE";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        //开启持久化
        boolean durable = true;
        //声明队列
        channel.queueDeclare(TASK_QUEUE_NAME,durable,false,false,null);
        //在控制台中输入信息
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入信息：");
        while (scanner.hasNext()){
            String message = scanner.next();
            channel.basicPublish("",TASK_QUEUE_NAME,null,message.getBytes("UTF-8"));
            System.out.println("生产者发出消息:"+ message);
        }

    }
}
```

::: warning 注意

如果之前声明的队列不是持久化的，需要把原先队列先删除，或者重新创建一个持久化的队列

:::

不然就会出现如下错误：

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220724/image.4nf70nwh0j60.webp)

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.5tlb9m3bfh00.webp)

### 消息持久化

需要在**消息生产者**发布消息的时候，开启消息的持久化

在 basicPublish 方法的第二个参数添加这个属性： `MessageProperties.PERSISTENT_TEXT_PLAIN`,如 13 行代码

```java {19}
public class Task02 {

    //队列名称
    public static final String TASK_QUEUE_NAME = "ACK_QUEUE";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        //队列持久化
        boolean durable = true;
        //声明队列
        channel.queueDeclare(TASK_QUEUE_NAME,durable,false,false,null);
        //在控制台中输入信息
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入信息：");
        while (scanner.hasNext()){
            String message = scanner.next();
        	//设置生产者发送消息为持久化消息(要求保存到磁盘上)
                        channel.basicPublish("",TASK_QUEUE_NAME,MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes("UTF-8"));

            System.out.println("生产者发出消息:"+ message);
        }

    }
}
```

将消息标记为持久化并不能完全保证不会丢失消息。尽管它告诉 RabbitMQ 将消息保存到磁盘，但是这里依然存在当消息刚准备存储在磁盘的时候 但是还没有存储完，消息还在缓存的一个间隔点。此时并没 有真正写入磁盘。持久性保证并不强，但是对于我们的简单任务队列而言，这已经绰绰有余了。

## 不公平分发

### 介绍

在最开始的时候我们学习到 RabbitMQ 分发消息采用的轮询分发，但是在某种场景下这种策略并不是很好，比方说有两个消费者在处理任务，其中有个**消费者 1** 处理任务的速度非常快，而另外一个**消费者 2** 处理速度却很慢，这个时候我们还是采用轮询分发的化就会到这处理速度快的这个消费者很大一部分时间处于空闲状态，而处理慢的那个消费者一直在干活，这种分配方式在这种情况下其实就不太好，但是 RabbitMQ 并不知道这种情况它依然很公平的进行分发。

为了避免这种情况，**在消费者中消费消息之前**，设置参数 `channel.basicQos(1);`

```java {29-31}
public class Work03 {

    //队列名称
    public static final String TASK_QUEUE_NAME = "ACK_QUEUE";

    //接受消息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        System.out.println("C1等待接受消息处理时间较短");

        DeliverCallback deliverCallback =(consumerTag,message) ->{

            //沉睡1S
            SleepUtils.sleep(1);
            System.out.println("接受到的消息:"+new String(message.getBody(),"UTF-8"));
            //手动应答
            /**
             * 1.消息的标记Tag
             * 2.是否批量应答 false表示不批量应答信道中的消息
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);

        };

        CancelCallback cancelCallback = (consumerTag -> {
            System.out.println(consumerTag + "消费者取消消费接口回调逻辑");

        });
        //设置不公平分发
        int prefetchCount = 1;
        channel.basicQos(prefetchCount);
        //采用手动应答
        boolean autoAck = false;
        channel.basicConsume(TASK_QUEUE_NAME,autoAck,deliverCallback,cancelCallback);
    }
}
```

开启成功，会看到如下结果：

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.lhxynhnktlc.webp)

不公平分发思想：如果一个工作队列还没有处理完或者没有应答签收一个消息，则不拒绝 RabbitMQ 分配新的消息到该工作队列。此时 RabbitMQ 会优先分配给其他已经处理完消息或者空闲的工作队列。如果所有的消费者都没有完成手上任务，队列还在不停的添加新任务，队列有可能就会遇到队列被撑满的情况，这个时候就只能添加新的 worker (工作队列)或者改变其他存储任务的策略。

### 效果演示

生产者生产多个消息，两个消费者的消费时间不同，则消费消息的次数也不同

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.6t9vh6pv6f40.webp)

## 预取值分发

### 介绍

带权的消息分发

默认消息的发送是异步发送的，所以在任何时候，channel 上不止只有一个消息来自消费者的手动确认，所以本质上是异步的。因此这里就存在一个未确认的消息缓冲区，因此希望开发人员能**限制此缓冲区的大小**，**以避免缓冲区里面无限制的未确认消息问题**。这个时候就可以通过使用 `basic.qos` 方法设置「预取计数」值来完成的。

该值定义通道上允许的未确认消息的最大数量。一旦数量达到配置的数量， RabbitMQ 将停止在通道上传递更多消息，除非至少有一个未处理的消息被确认，例如，假设在通道上有未确认的消息 5、6、7，8，并且通道的预取计数设置为 4，此时 RabbitMQ 将不会在该通道上再传递任何消息，除非至少有一个未应答的消息被 ack。比方说 tag=6 这个消息刚刚被确认 ACK，RabbitMQ 将会感知这个情况到并再发送一条消息。消息应答和 QoS 预取值对用户吞吐量有重大影响。

通常，增加预取将提高向消费者传递消息的速度。**虽然自动应答传输消息速率是最佳的，但是，在这种情况下已传递但尚未处理的消息的数量也会增加，从而增加了消费者的 RAM 消耗**(随机存取存储器)应该小心使用具有无限预处理的自动确认模式或手动确认模式，消费者消费了大量的消息如果没有确认的话，会导致消费者连接节点的内存消耗变大，所以找到合适的预取值是一个反复试验的过程，不同的负载该值取值也不同 100 到 300 范围内的值通常可提供最佳的吞吐量，并且不会给消费者带来太大的风险。

预取值为 1 是最保守的。当然这将使吞吐量变得很低，特别是消费者连接延迟很严重的情况下，特别是在消费者连接等待时间较长的环境 中。对于大多数应用来说，稍微高一点的值将是最佳的。

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220724/image.2anljpf3y134.webp)

```java {31-33}
public class Work03 {

    //队列名称
    public static final String TASK_QUEUE_NAME = "ACK_QUEUE";

    //接受消息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        System.out.println("C1等待接受消息处理时间较短");

        DeliverCallback deliverCallback =(consumerTag,message) ->{

            //沉睡1S
            SleepUtils.sleep(1);
            System.out.println("接受到的消息:"+new String(message.getBody(),"UTF-8"));
            //手动应答
            /**
             * 1.消息的标记Tag
             * 2.是否批量应答 false表示不批量应答信道中的消息
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);

        };

        CancelCallback cancelCallback = (consumerTag -> {
            System.out.println(consumerTag + "消费者取消消费接口回调逻辑");

        });
        //设置不公平分发
        //int prefetchCount = 1;
        //值不等于 1，则代表预取值,预取值为4
        int prefetchCount = 4;
        channel.basicQos(prefetchCount);
        //采用手动应答
        boolean autoAck = false;
        channel.basicConsume(TASK_QUEUE_NAME,autoAck,deliverCallback,cancelCallback);
    }
}
```

::: tip 笔记

不公平分发和预取值分发都用到 `basic.qos` 方法，如果取值为 1，代表不公平分发，取值不为1，代表预取值分发

:::

### 效果演示

设置了预取值为 4。生产者发送 5 条消息到 MQ 中

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220724/image.6m3okmfsfrs.webp)

## 发布确认

生产者发布消息到 RabbitMQ 后，需要 RabbitMQ 返回「ACK（已收到）」给生产者，这样生产者才知道自己生产的消息成功发布出去。

## 发布确认逻辑

生产者将信道设置成 confirm 模式，一旦信道进入 confirm 模式，所有在该信道上面发布的消息都将会被指派一个唯一的 ID(从 1 开始)，一旦消息被投递到所有匹配的队列之后，broker 就会发送一个确认给生产者(包含消息的唯一 ID)，这就使得生产者知道消息已经正确到达目的队列了，如果消息和队列是可持久化的，那么确认消息会在将消息写入磁盘之后发出，broker 回传给生产者的确认消息中 delivery-tag 域包含了确认消息的序列号，此外 broker 也可以设置 basic.ack 的 multiple 域，表示到这个序列号之前的所有消息都已经得到了处理。

confirm 模式最大的好处在于是异步的，一旦发布一条消息，生产者应用程序就可以在等信道返回确认的同时继续发送下一条消息，当消息最终得到确认之后，生产者应用便可以通过回调方法来处理该确认消息，如果RabbitMQ 因为自身内部错误导致消息丢失，就会发送一条 nack 消息， 生产者应用程序同样可以在回调方法中处理该 nack 消息。

### 开启发布确认的方法

发布确认默认是没有开启的，如果要开启需要调用方法 confirmSelect，每当你要想使用发布确认，都需要在 channel 上调用该方法

```java
//开启发布确认
channel.confirmSelect();
```

### 单个确认发布

这是一种简单的确认方式，它是一种**同步确认发布**的方式，也就是发布一个消息之后只有它被确认发布，后续的消息才能继续发布，`waitForConfirmsOrDie(long)` 这个方法只有在消息被确认的时候才返回，如果在指定时间范围内这个消息没有被确认那么它将抛出异常。

这种确认方式有一个最大的缺点就是：**发布速度特别的慢**，因为如果没有确认发布的消息就会阻塞所有后续消息的发布，这种方式最多提供每秒不超过数百条发布消息的吞吐量。当然对于某些应用程序来说这可能已经足够了。

```java {17,26}
public class ConfirmMessage {

    //单个发消息的个数
    public static final int MESSAGE_COUNT = 1000; //Ctrl+Shift+U 变大写

    public static void main(String[] args) throws InterruptedException, TimeoutException, IOException {
        publishMessageIndividually();//发布1000个单独确认消息，耗时:599ms
    }
    //单个确认
    public static void publishMessageIndividually() throws IOException, TimeoutException, InterruptedException {
        Channel channel = RabbitMQUtils.getChannel();
        //队列的声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,false,true,false,null);

        //开启发布确认
        channel.confirmSelect();
        //开始时间
        long begin = System.currentTimeMillis();

        //批量发消息
        for (int i = 0; i < 1000; i++) {
            String message = i+"";
            channel.basicPublish("",queueName,null,message.getBytes());
            //单个消息就马上进行发布确认
            boolean flag = channel.waitForConfirms();
            if(flag){
                System.out.println("消息发送成功");
            }
        }
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布"+MESSAGE_COUNT+"个单独确认消息，耗时:"+(end-begin)+"ms");
        
    }
}
```

确认发布指的是成功发送到了队列，并不是消费者消费了消息。

### 批量确认发布

单个确认发布方式非常慢，与单个等待确认消息相比，先发布一批消息然后一起确认可以极大地提高吞吐量，当然这种方式的缺点就是：当发生故障导致发布出现问题时，不知道是哪个消息出问题了，我们必须将整个批处理保存在内存中，以记录重要的信息而后重新发布消息。当然这种方案仍然是同步的，也一样阻塞消息的发布。

```java {18,23,26-35}
public class ConfirmMessage2 {

    //批量发消息的个数
    public static final int MESSAGE_COUNT = 1000; //Ctrl+Shift+U 变大写

    public static void main(String[] args) throws InterruptedException, TimeoutException, IOException {
        publishMessageBatch(); //发布1000个批量确认消息，耗时:111ms
    }

    //批量发布确认
    public static void publishMessageBatch() throws IOException, TimeoutException, InterruptedException {
        Channel channel = RabbitMQUtils.getChannel();
        //队列的声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, true, false, null);

        //开启发布确认
        channel.confirmSelect();
        //开始时间
        long begin = System.currentTimeMillis();

        //批量确认消息大小
        int batchSize =100;

        //批量发送消息，批量发布确认
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message=i+"";
            channel.basicPublish("",queueName,null,message.getBytes());

            //判断达到100条消息的时候，批量确认一次
            if((i+1)%batchSize==0){
                //发布确认
                channel.waitForConfirms();
            }
        }
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布"+MESSAGE_COUNT+"个批量确认消息，耗时:"+(end-begin)+"ms");
    }
}
```

### 异步确认发布

异步确认虽然编程逻辑比上两个要复杂，但是性价比最高，无论是可靠性还是效率都很好，利用了回调函数来达到消息可靠性传递的，这个中间件也是通过函数回调来保证是否投递成功，下面详细讲解异步确认是怎么实现的。

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.73gul8cwoq00.webp)

添加回调函数，在回调函数里进行确认发布

```java {18,23-25,31-33,40}
public class ConfirmMessage3 {

    public static final int MESSAGE_COUNT = 1000; //Ctrl+Shift+U 变大写
    
    public static void main(String[] args) throws Exception {
        publishMessageAsync(); //发布1000个异步发布确认消息，耗时:43ms
    }

    //异步发布确认
    public static void publishMessageAsync() throws Exception{

        Channel channel = RabbitMQUtils.getChannel();
        //队列的声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, true, false, null);

        //开启发布确认
        channel.confirmSelect();
        //开始时间
        long begin = System.currentTimeMillis();

        //消息确认回调的函数
        ConfirmCallback ackCallback = (deliveryTag,multiple) ->{
            System.out.println("确认的消息:"+deliveryTag);
        };
        /**
         * 1.消息的标记
         * 2.是否为批量确认
         */
        //消息确认失败回调函数
        ConfirmCallback nackCallback= (deliveryTag,multiple) ->{
            System.out.println("未确认的消息:"+deliveryTag);
        };

        //准备消息的监听器 监听那些消息成功了，哪些消息失败了
        /**
         * 1.监听哪些消息成功了
         * 2.监听哪些消息失败了
         */
        channel.addConfirmListener(ackCallback,nackCallback);//异步通知

        //批量发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message=i+"消息";
            channel.basicPublish("",queueName,null,message.getBytes());
        }

        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布"+MESSAGE_COUNT+"个异步发布确认消息，耗时:"+(end-begin)+"ms");
    }
}
```

实际案例里，将发布的消息存入 Map 里，方便获取。`headMap` 方法用于将已确认的消息存入新的 Map 缓存区里，然后清除该新缓存区的内容。因为 `headMap` 方法是浅拷贝，所以清除了缓存区，相当于清除了内容的地址，也就清除了队列的确认的消息。

**如何处理异步未确认消息?**

最好的解决的解决方案就是把未确认的消息放到一个基于内存的能被发布线程访问的队列，比如说用 ConcurrentLinkedQueue 这个队列在 confirm callbacks 与发布线程之间进行消息的传递。

```java {26-40,46-50,67}
public class ConfirmMessage3 {

    public static final int MESSAGE_COUNT = 1000; //Ctrl+Shift+U 变大写
    
    public static void main(String[] args) throws Exception {
        publishMessageAsync(); //发布1000个异步发布确认消息，耗时:43ms
    }

    //异步发布确认
    public static void publishMessageAsync() throws Exception{

        Channel channel = RabbitMQUtils.getChannel();
        //队列的声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, true, false, null);

        //开启发布确认
        channel.confirmSelect();

        /**
         * 线程安全有序的一个哈希表，适用于高并发的情况下
         * 1.轻松的将序号与消息进行关联
         * 2.轻松批量删除条目 只要给到序号
         * 3.支持高并发(多线程)
         */
        ConcurrentSkipListMap<Long,String> outstandingConfirms=
                new ConcurrentSkipListMap<>();

        //消息确认回调的函数
        ConfirmCallback ackCallback = (deliveryTag,multiple) ->{
            if(multiple) {
                //2.删除掉已经确认的消息 剩下的就是未确认的消息
                ConcurrentNavigableMap<Long, String> confirmed =
                        outstandingConfirms.headMap(deliveryTag);
                confirmed.clear();
            }else {
                outstandingConfirms.remove(deliveryTag);
            }
            System.out.println("确认的消息:" + deliveryTag);
        };
        /**
         * 1.消息的标记
         * 2.是否为批量确认
         */
        //消息确认失败回调函数
        ConfirmCallback nackCallback= (deliveryTag,multiple) ->{
            //3.打印一下未确认的消息都有哪些
            String message = outstandingConfirms.remove(deliveryTag);
            System.out.println("未确认的消息是:"+message+":::未确认的消息tag:"+deliveryTag);
        };

        //准备消息的监听器 监听那些消息成功了，哪些消息失败了
        /**
         * 1.监听哪些消息成功了
         * 2.监听哪些消息失败了
         */
        channel.addConfirmListener(ackCallback,nackCallback);//异步通知

        //开始时间
        long begin = System.currentTimeMillis();

        //批量发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message=i+"消息";
            channel.basicPublish("",queueName,null,message.getBytes());
            //1.此处记录下所有要发送的消息 消息的总和
            outstandingConfirms.put(channel.getNextPublishSeqNo(),message);
        }

        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布"+MESSAGE_COUNT+"个异步发布确认消息，耗时:"+(end-begin)+"ms");
    }
}
```

**以上 3 种发布确认速度对比:**

- 单独发布消息

  同步等待确认，简单，但吞吐量非常有限。

- 批量发布消息

  批量同步等待确认，简单，合理的吞吐量，一旦出现问题但很难推断出是那条消息出现了问题。

- 异步处理

  最佳性能和资源使用，在出现错误的情况下可以很好地控制，但是实现起来稍微难些

## 应答和发布区别

应答功能属于消费者，消费完消息告诉 RabbitMQ 已经消费成功。

发布功能属于生产者，生产消息到 RabbitMQ，RabbitMQ 需要告诉生产者已经收到消息。



# 6.<mark>RabbitMQ 交换机</mark>

[[toc]]

## Exchanges

RabbitMQ 消息传递模型的核心思想是: **生产者生产的消息从不会直接发送到队列**。实际上，通常生产者甚至都不知道这些消息传递传递到了哪些队列中。

相反，**生产者只能将消息发送到交换机(exchange)**，交换机工作的内容非常简单，一方面它接收来自生产者的消息，另一方面将它们推入队列。交换机必须确切知道如何处理收到的消息。是应该把这些消息放到特定队列还是说把他们到许多队列中还是说应该丢弃它们。这就的由交换机的类型来决定。



<img src="https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.54tzwxwrqco.webp" alt="image" style="zoom: 33%;" />



### Exchanges(交换机)的类型

- **直接(direct)**：处理路由键。需要将一个队列绑定到交换机上，要求该消息与一个特定的路由键完全匹配。这是一个完整的匹配。如果一个队列绑定到该交换机上要求路由键 abc ，则只有被标记为 abc 的消息才被转发，不会转发 abc.def，也不会转发 dog.ghi，只会转发 abc。

- **主题(topic)**：将路由键和某模式进行匹配。此时队列需要绑定要一个模式上。符号“#”匹配一个或多个词，符号 * 匹配不多不少一个词。因此 abc.# 能够匹配到 abc.def.ghi，但是 abc.* 只会匹配到 abc.def。

- **标题(headers)**<这个用的少，不要求掌握>：不处理路由键。而是根据发送的消息内容中的headers属性进行匹配。在绑定 Queue 与 Exchange 时指定一组键值对；当消息发送到RabbitMQ 时会取到该消息的 headers 与 Exchange 绑定时指定的键值对进行匹配；如果完全匹配则消息会路由到该队列，否则不会路由到该队列。headers 属性是一个键值对，可以是 Hashtable，键值对的值可以是任何类型。而 fanout，direct，topic 的路由键都需要要字符串形式的。

  匹配规则 x-match 有下列两种类型：

  x-match = all ：表示所有的键值对都匹配才能接受到消息

  x-match = any ：表示只要有键值对匹配就能接受到消息

- **扇出(fanout)**：不处理路由键。你只需要简单的将队列绑定到交换机上。一个发送到交换机的消息都会被转发到与该交换机绑定的所有队列上。很像子网广播，每台子网内的主机都获得了一份复制的消息。Fanout 交换机转发消息是最快的。

![image-20230427144629975](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image-20230427144629975.png)

### 默认exchange

通过空字符串("")进行标识的交换机是默认交换

```java
channel.basicPublish("", TASK_QUEUE_NAME, null, message.getBytes("UTF-8"));
```

第一个参数是交换机的名称。空字符串	表示默认或无名称交换机：消息能路由发送到队列中其实是由 routingKey(bindingkey) 绑定指定的 key

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.2nq1gc53kik.webp)

## 临时队列

之前的章节我们使用的是具有特定名称的队列(还记得 hello 和 ack_queue 吗？)。队列的名称我们来说至关重要，我们需要指定我们的消费者去消费哪个队列的消息。

每当我们连接到 Rabbit 时，我们都需要一个全新的空队列，为此我们可以创建一个具有**随机名称的队列**，或者能让服务器为我们选择一个随机队列名称那就更好了。其次一旦我们断开了消费者的连接，队列将被自动删除。

创建临时队列的方式如下:

```java
String queueName = channel.queueDeclare().getQueue();
```

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220724/image.56c9qj8lnyo0.webp)

## 绑定bindings

什么是 bingding 呢，binding 其实是 exchange 和 queue 之间的桥梁，它告诉我们 exchange 和那个队列进行了绑定关系。比如说下面这张图告诉我们的就是 X 与 Q1 和 Q2 进行了绑定

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220724/image.1ryp1eu9xtnk.webp)

![image](https://raw.githubusercontent.com/52chen/imagebed2023/main/picgo/image.2z1b1g3ou5e0.webp)

### Fanout实战

为了说明这种模式，我们将构建一个简单的日志系统。它将由两个程序组成:第一个程序将发出日志消息，第二个程序是消费者。其中启动两个消费者，其中一个消费者接收到消息后把日志存储在磁盘

**图例**

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220724/image.2mdbijsttri0.webp)

Logs 和临时队列的绑定关系如下图

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220724/image.4bl530c0ox20.webp)

生产者`EmitLog` 发送消息给两个消费者进行消费

```java
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import utils.RabbitMQUtils;

/**
 * @author frx
 * @version 1.0
 * @date 2022/7/24  21:34
 * desc:负责进行发消息给交换机
 */
public class EmitLog {

    //交换机名称
    public static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        /**
         * 声明一个exchange
         * 1.exchange的名称
         * 2.exchange的类型
         */
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String message = scanner.next();
            channel.basicPublish(EXCHANGE_NAME,"",null,message.getBytes("UTF-8"));
            System.out.println("生产者发出消息:"+message);
        }
    }
}
```

![image](https://raw.githubusercontent.com/52chen/imagebed2023/main/picgo/image.5kb833agark0.webp)



日志用临时队列是一种常见的实现方式，用于实现日志传输和处理的并行化。

`ReceiveLogs01` 将接收到的消息打印在控制台

`ReceiveLogs02` 把消息写出到文件

```java
package fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import utils.RabbitMQUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

//消费者1
public class ReceiveLogs01 {

    //交换机名称
    private static  final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        //声明一个交换机
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        //channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);这样可以，这个FANOUT是一个枚举类型用来指定一个字符串

        //声明一个队列 临时队列
        /**
         * 生成一个临时的队列，队列的名称是随机的
         * 当消费者断开与队列的连接的时候 队列就自动删除
         */
        String queueName = channel.queueDeclare().getQueue();
//        每次得到的queue都是不同的。
        /**
         * 绑定交换机与队列
         */
        channel.queueBind(queueName,EXCHANGE_NAME,"");
        System.out.println("等待接收消息，把接收到的消息打印在屏幕上...");
        //接收消息
        //消费者取消消息时回调接口
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            System.out.println("控制台打印接收到的消息:"+new String(message.getBody(),"UTF-8"));
        };
        channel.basicConsume(queueName,true,deliverCallback,consumerTag -> {});
    }
}
```



> 一个发送，多个接受，发布/订阅模式

## Direct 交换机

在上一节中，我们构建了一个简单的日志记录系统。我们能够向许多接收者广播日志消息。在本节我们将向其中添加一些特别的功能——让某个消费者订阅发布的部分消息。例如我们只把严重错误消息定向存储到日志文件(以节省磁盘空间)，同时仍然能够在控制台上打印所有日志消息。

我们再次来回顾一下什么是 bindings，绑定是交换机和队列之间的桥梁关系。也可以这么理解： **队列只对它绑定的交换机的消息感兴趣**。绑定用参数：routingKey 来表示也可称该参数为 binding key， 创建绑定我们用代码:channel.queueBind(queueName, EXCHANGE_NAME, "routingKey");

绑定之后的意义由其交换类型决定。

### Direct介绍

上一节中的我们的日志系统将所有消息广播给所有消费者，对此我们想做一些改变，例如我们希望将日志消息写入磁盘的程序<mark>仅接收严重错误(errros)，而不存储哪些警告(warning)或信息(info)日志</mark> 消息避免浪费磁盘空间。Fanout 这种交换类型并不能给我们带来很大的灵活性-它只能进行无意识的广播，在这里我们将使用 direct 这种类型来进行替换，这种类型的工作方式是，消息只去到它绑定的 routingKey 队列中去。

![image](https://raw.githubusercontent.com/52chen/imagebed2023/main/picgo/image.gi5bzb0sygo.webp)

在上面这张图中，我们可以看到 X 绑定了两个队列，绑定类型是 direct。队列 Q1 绑定键为 orange， 队列 Q2 绑定键有两个：一个绑定键为 black，另一个绑定键为 green.

在这种绑定情况下，生产者发布消息到 exchange 上，绑定键为 orange 的消息会被发布到队列 Q1。绑定键为 blackgreen 和的消息会被发布到队列 Q2，其他消息类型的消息将被丢弃。

### 多重绑定

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.6m5o5nqwvlw0.webp)

当然如果 exchange 的绑定类型是direct，**但是它绑定的多个队列的 key 如果都相同**，在这种情况下虽然绑定类型是 direct **但是它表现的就和 fanout 有点类似了**，就跟广播差不多，如上图所示。

### Direct实战

关系：

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220724/image.18f9o7wxc5uo.webp)

交换机：

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220724/image.5x5pk7emz0k0.webp)

C1 消费者：绑定 console 队列，routingKey 为 info、warning

C2 消费者：绑定 disk 队列，routingKey 为 error

当生产者生产消息到 `direct_logs` 交换机里，该交换机会检测消息的 routingKey 条件，然后分配到满足条件的队列里，最后由消费者从队列消费消息。

**生产者**

```java
package direct;
import com.rabbitmq.client.*;
import utils.RabbitMQUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class DirectLogs {

    //交换机名称
    public static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        Scanner scanner = new Scanner(System.in);
        String message = "info:message";
        channel.basicPublish(EXCHANGE_NAME,"warning",null,"waring信息".getBytes("UTF-8"));
        System.out.println("生产者waring发出消息:"+message);
        while (scanner.hasNext()){
            message = scanner.next();
            channel.basicPublish(EXCHANGE_NAME,"info",null,message.getBytes("UTF-8"));
            System.out.println("生产者发出info消息:"+message);
        }
    }
}

```

**消费者1**

```java {15-17}
package direct;
import com.rabbitmq.client.*;
import utils.RabbitMQUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class ReceiveLogsDirect01 {
    public static final String EXCHANGE_NAME="direct_logs";
//    接受info类型的接受的信息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        //声明一个direct交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //声明一个队列
        channel.queueDeclare("console",false,false,false,null);
        channel.queueBind("console",EXCHANGE_NAME,"info");
        //接收消息
        DeliverCallback deliverCallback = (consumerTag,message) -> {
            System.out.println("ReceiveLogsDirect01控制台打印接收到的消息:"+new String(message.getBody(),"UTF-8"));
        };
        //消费者取消消息时回调接口
        channel.basicConsume("console",true,deliverCallback,consumerTag -> {});

    }
}

```

**消费者2**

```java
package direct;
import com.rabbitmq.client.*;
import utils.RabbitMQUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class ReceiveLogsDirect02 {
    public static final String EXCHANGE_NAME="direct_logs";
    //    接受warning类型的接受的信息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        //声明一个direct交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //声明一个队列
        channel.queueDeclare("console",false,false,false,null);
        channel.queueBind("console",EXCHANGE_NAME,"warning");
        //接收消息
        DeliverCallback deliverCallback = (consumerTag,message) -> {
            System.out.println("ReceiveLogsDirect01控制台打印接收到的消息:"+new String(message.getBody(),"UTF-8"));
        };
        //消费者取消消息时回调接口
        channel.basicConsume("console",true,deliverCallback,consumerTag -> {});

    }
}

```

+ 

## Topics 交换机

### Topic的介绍

在上一个小节中，我们改进了日志记录系统。我们没有使用只能进行随意广播的 fanout 交换机，而是使用了 direct 交换机，**从而有能实现有选择性地接收日志。**

尽管使用 direct 交换机改进了我们的系统，但是它仍然存在局限性——比方说我们想接收的日志类型有 info.base 和 info.advantage，某个队列只想 info.base 的消息，那这个时候direct 就办不到了。这个时候就只能使用 **topic** 类型

**Topic 的要求**

发送到类型是 topic 交换机的消息的 routing_key 不能随意写，必须满足一定的要求，它必须是**一个单词列表**，**以点号分隔开**。这些单词可以是任意单词

比如说："stock.usd.nyse", "nyse.vmw", "quick.orange.rabbit" 这种类型的。

当然这个单词列表最多不能超过 255 个字节。

在这个规则列表中，模糊匹配：

- ***(星号)可以代替一个单词**
- **#(井号)可以替代零个或多个单词**

### Topic匹配案例

下图绑定关系如下

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.6a89pabmhtg0.webp)

- Q1-->绑定的是
  - 中间带 orange 带 3 个**单词**的字符串 `(*.orange.*)`
- Q2-->绑定的是
  - 最后一个单词是 rabbit 的 3 个单词 `(*.*.rabbit)`
  - 第一个单词是 lazy 的多个单词 `(lazy.#)`

上图是一个队列绑定关系图，我们来看看他们之间数据接收情况是怎么样的

| 例子                     | 说明                                       |
| ------------------------ | ------------------------------------------ |
| quick.orange.rabbit      | 被队列 Q1Q2 接收到                         |
| azy.orange.elephant      | 被队列 Q1Q2 接收到                         |
| quick.orange.fox         | 被队列 Q1 接收到                           |
| lazy.brown.fox           | 被队列 Q2 接收到                           |
| lazy.pink.rabbit         | 虽然满足两个绑定但只被队列 Q2 接收一次     |
| quick.brown.fox          | 不匹配任何绑定不会被任何队列接收到会被丢弃 |
| quick.orange.male.rabbit | 是四个单词不匹配任何绑定会被丢弃           |
| lazy.orange.male.rabbit  | 是四个单词但匹配 Q2                        |

> 当一个队列绑定键是 #，那么这个队列将接收所有数据，就有点像 fanout 了
>
> 如果队列绑定键当中没有 # 和 * 出现，那么该队列绑定类型就是 direct 了

### Topic实战

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220724/image.5kvwh1z1ixk0.webp)

生产多个消息到交换机，交换机按照通配符分配消息到不同的队列中，队列由消费者进行消费

**生产者 EmitLogTopic**

```java
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/24  22:44
 * desc:生产者
 */
public class EmitLogTopic {

    //交换机的名称
    public static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        /**
         * Q1-->绑定的是
         *      中间带 orange 带 3 个单词的字符串(*.orange.*)
         * Q2-->绑定的是
         *      最后一个单词是 rabbit 的 3 个单词(*.*.rabbit)
         *      第一个单词是 lazy 的多个单词(lazy.#)
         */
        HashMap<String, String> bindingKeyMap = new HashMap<>();
        bindingKeyMap.put("quick.orange.rabbit", "被队列 Q1Q2 接收到");
        bindingKeyMap.put("lazy.orange.elephant", "被队列 Q1Q2 接收到");
        bindingKeyMap.put("quick.orange.fox", "被队列 Q1 接收到");
        bindingKeyMap.put("lazy.brown.fox", "被队列 Q2 接收到");
        bindingKeyMap.put("lazy.pink.rabbit", "虽然满足两个绑定但只被队列 Q2 接收一次");
        bindingKeyMap.put("quick.brown.fox", "不匹配任何绑定不会被任何队列接收到会被丢弃");
        bindingKeyMap.put("quick.orange.male.rabbit", "是四个单词不匹配任何绑定会被丢弃");
        bindingKeyMap.put("lazy.orange.male.rabbit", "是四个单词但匹配 Q2");
        for (Map.Entry<String,String> bindingKeyEntry : bindingKeyMap.entrySet()){
            String routingKey = bindingKeyEntry.getKey();
            String message = bindingKeyEntry.getValue();

            channel.basicPublish(EXCHANGE_NAME,routingKey,null,message.getBytes("UTF-8"));
            System.out.println("生产者发出消息:"+message);
        }
    }
}
```

**消费者C1**

```java {21}
package topic;

import com.rabbitmq.client.*;
import utils.RabbitMQUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class ReceiveLogsTopic01 {

    //交换机的名称
    public static final String EXCHANGE_NAME = "topic_logs";

    //接收消息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //声明队列
        String queueName = "Q1";
        channel.queueDeclare(queueName,false,false,false,null);
        channel.queueBind(queueName,EXCHANGE_NAME,"*.orange.*");
        System.out.println("等待接收消息...");

        DeliverCallback deliverCallback = (consumerTag,message) -> {
            System.out.println(new String(message.getBody(),"UTF-8"));
            System.out.println("接收队列："+queueName+"  绑定键:"+message.getEnvelope().getRoutingKey());
        };
        //接收消息
        channel.basicConsume(queueName,true,deliverCallback,consumerTag ->{});
    }
}
```

**消费者C2**

```java {21,22}
package topic;

import com.rabbitmq.client.*;
import utils.RabbitMQUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class ReceiveLogsTopic02 {

    //交换机的名称
    public static final String EXCHANGE_NAME = "topic_logs";

    //接收消息
    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //声明队列
        String queueName = "Q2";
        channel.queueDeclare(queueName,false,false,false,null);
        channel.queueBind(queueName,EXCHANGE_NAME,"*.*.rabbit");
        channel.queueBind(queueName,EXCHANGE_NAME,"lazy.#");

        System.out.println("等待接收消息...");
        
        DeliverCallback deliverCallback = (consumerTag,message) -> {
            System.out.println(new String(message.getBody(),"UTF-8"));
            System.out.println("接收队列："+queueName+"  绑定键:"+message.getEnvelope().getRoutingKey());
        };
        //接收消息
        channel.basicConsume(queueName,true,deliverCallback,consumerTag ->{});
    }
}
```

+ 测试结果

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.4xiiz5wc7cg0.webp)

# 7.RabbitMQ 死信队列

[[toc]]

## 死信的概念

先从概念解释上搞清楚这个定义，死信，顾名思义就是无法被消费的消息，字面意思可以这样理解，一般来说，producer 将消息投递到 broker 或者直接到queue 里了，consumer 从 queue 取出消息 进行消费，但某些时候由于特定的原因**导致 queue 中的某些消息无法被消费**，这样的消息如果没有后续的处理，就变成了死信，有死信自然就有了死信队列。

应用场景：为了保证订单业务的消息数据不丢失，需要使用到 RabbitMQ 的死信队列机制，当消息消费发生异常时，将消息投入死信队列中。还有比如说：用户在商城下单成功并点击去支付后在指定时间未支付时自动失效。

## 死信的来源

- 消息 TTL 过期

  TTL是 Time To Live 的缩写, 也就是生存时间

- 队列达到最大长度

  队列满了，无法再添加数据到 MQ 中

- 消息被拒绝

  (basic.reject 或 basic.nack) 并且 requeue = false

## 死信实战

交换机类型是 direct，两个消费者，一个生产者，两个队列：消息队列和死信队列

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220725/image.691xh9mh3yk0.webp)

### 消息TTL过期

**生产者**

```java
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/25  19:13
 * desc:死信队列之生产者
 */
public class Producer {
    //普通交换机的名称
    public static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        //死信消息 设置ttl时间 live to time 单位是ms
        AMQP.BasicProperties properties =
                new AMQP.BasicProperties().builder().expiration("10000").build();
        for (int i = 1; i <11 ; i++) {
            String message = "info"+i;
            channel.basicPublish(NORMAL_EXCHANGE,"zhangsan",properties,message.getBytes());
        }
    }
}
```

**消费者 C1 代码**(启动之后关闭该消费者 模拟其接收不到消息)

```java
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/25  17:00
 * desc:本次是为了死信队列实战
 * 消费者1
 */
public class Consumer01 {

    //普通交换机的名称
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    //死信交换机的名称
    public static final String DEAD_EXCHANGE = "dead_exchange";

    //普通队列的名称
    public static final String NORMAL_QUEUE = "normal_queue";
    //死信队列的名称
    public static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMQUtils.getChannel();

        //声明死信和普通交换机，类型为direct
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        //声明普通队列
        Map<String,Object> arguments = new HashMap<>();
        //过期时间 10s 由生产者指定 更加灵活
        //arguments.put("x-message-ttl",10000);
        //正常的队列设置死信交换机
        arguments.put("x-dead-letter-exchange",DEAD_EXCHANGE);//图中红箭头
        //设置死信routingKey
        arguments.put("x-dead-letter-routing-key","lisi");

        channel.queueDeclare(NORMAL_QUEUE,false,false,false,arguments);
        /////////////////////////////////////////////////////////////////////////
        //声明死信队列
        channel.queueDeclare(DEAD_QUEUE,false,false,false,null);

        //绑定普通的交换机与队列
        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE,"zhangsan");

        //绑定死信的交换机与死信的队列
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"lisi");
        System.out.println("等待接收消息...");

        DeliverCallback deliverCallback = (consumerTag,message) ->{
            System.out.println("Consumer01接受的消息是："+new String(message.getBody(),"UTF-8"));
        };

        channel.basicConsume(NORMAL_QUEUE,true,deliverCallback,consumerTag -> {});
    }

}
```

先启动消费者 C1，创建出队列，然后停止该 C1 的运行，则 C1 将无法收到队列的消息，无法收到的消息 10 秒后进入死信队列。启动生产者 producer 生产消息

**生产者未发送消息**

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.5ldx5kffws00.webp)

**生产者发送了10条消息，此时正常消息队列有10条未消费消息**

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220726/image.4k5fjvtukpe0.webp)

**时间过去10秒，正常队列里面的消息由于没有被消费，消息进入死信队列**

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220725/image.5g2svti1sec0.webp)

**消费者 C2 代码**

```java
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/25  19:30
 */
public class Consumer02 {

    //死信队列的名称
    public static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMQUtils.getChannel();

        System.out.println("等待接收死信消息...");

        DeliverCallback deliverCallback = (consumerTag, message) ->{
            System.out.println("Consumer02接受的消息是："+new String(message.getBody(),"UTF-8"));
        };

        channel.basicConsume(DEAD_QUEUE,true,deliverCallback,consumerTag -> {});
    }
}
```

**效果演示**

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220725/image.3a1tkaqx5jm0.webp)

+ 控制台

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220725/image.2qvoqd9bcdc0.webp)

### 死信最大长度

1. 消息生产者代码去掉 TTL 属性，`basicPublish` 的第三个参数改为 null

```java {9,10,13}
public class Producer {
    //普通交换机的名称
    public static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        //死信消息 设置ttl时间 live to time 单位是ms
        //AMQP.BasicProperties properties =
        //        new AMQP.BasicProperties().builder().expiration("10000").build();
        for (int i = 1; i <11 ; i++) {
            String message = "info"+i;
            channel.basicPublish(NORMAL_EXCHANGE,"zhangsan",null,message.getBytes());
        }
    }
}
```

2. C1 消费者修改以下代码(**启动之后关闭该消费者 模拟其接收不到消息**)

```java {30}
public class Consumer01 {

    //普通交换机的名称
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    //死信交换机的名称
    public static final String DEAD_EXCHANGE = "dead_exchange";

    //普通队列的名称
    public static final String NORMAL_QUEUE = "normal_queue";
    //死信队列的名称
    public static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMQUtils.getChannel();

        //声明死信和普通交换机，类型为direct
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        //声明普通队列
        Map<String,Object> arguments = new HashMap<>();
        //过期时间 10s 由生产者指定 更加灵活
        //arguments.put("x-message-ttl",10000);
        //正常的队列设置死信交换机
        arguments.put("x-dead-letter-exchange",DEAD_EXCHANGE);//图中红箭头
        //设置死信routingKey
        arguments.put("x-dead-letter-routing-key","lisi");
        //设置正常队列长度的限制，例如发送10个消息，6个为正常，4个为死信
        arguments.put("x-max-length",6);
        
        channel.queueDeclare(NORMAL_QUEUE,false,false,false,arguments);
        /////////////////////////////////////////////////////////////////////////
        //声明死信队列
        channel.queueDeclare(DEAD_QUEUE,false,false,false,null);

        //绑定普通的交换机与队列
        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE,"zhangsan");

        //绑定死信的交换机与死信的队列
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"lisi");
        System.out.println("等待接收消息...");

        DeliverCallback deliverCallback = (consumerTag,message) ->{
            System.out.println("Consumer01接受的消息是："+new String(message.getBody(),"UTF-8"));
        };

        channel.basicConsume(NORMAL_QUEUE,true,deliverCallback,consumerTag -> {});
    }

}
```

::: tip 注意

因为参数改变了，所以需要把原先队列删除

:::

3. C2 消费者代码不变

+ 启动消费者C1，创建出队列，然后停止该 C1 的运行，启动生产者

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.6nv41w7ky0o0.webp)

+ 启动 C2 消费者

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220725/image.3mg5ph9jr020.webp)

+ 控制台

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220725/image.664h3njwl6w0.webp)

### 死信消息被拒

1. 消息生产者代码同上生产者一致
2. 需求：消费者 C1 拒收消息 "info5"，开启手动应答

**消费者C1**

```java {30,46-53,57}
public class Consumer01 {

    //普通交换机的名称
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    //死信交换机的名称
    public static final String DEAD_EXCHANGE = "dead_exchange";

    //普通队列的名称
    public static final String NORMAL_QUEUE = "normal_queue";
    //死信队列的名称
    public static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws IOException, TimeoutException {

        Channel channel = RabbitMQUtils.getChannel();

        //声明死信和普通交换机，类型为direct
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        //声明普通队列
        Map<String,Object> arguments = new HashMap<>();
        //过期时间 10s 由生产者指定 更加灵活
        //arguments.put("x-message-ttl",10000);
        //正常的队列设置死信交换机
        arguments.put("x-dead-letter-exchange",DEAD_EXCHANGE);//图中红箭头
        //设置死信routingKey
        arguments.put("x-dead-letter-routing-key","lisi");
        //设置正常队列长度的限制，例如发送10个消息，6个为正常，4个为死信
        //arguments.put("x-max-length",6);

        channel.queueDeclare(NORMAL_QUEUE,false,false,false,arguments);
        /////////////////////////////////////////////////////////////////////////
        //声明死信队列
        channel.queueDeclare(DEAD_QUEUE,false,false,false,null);

        //绑定普通的交换机与队列
        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE,"zhangsan");

        //绑定死信的交换机与死信的队列
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"lisi");
        System.out.println("等待接收消息...");

        DeliverCallback deliverCallback = (consumerTag,message) ->{
            String msg = new String(message.getBody(), "UTF-8");
            if(msg.equals("info5")){
                System.out.println("Consumer01接受的消息是："+msg+"： 此消息是被C1拒绝的");
                //requeue 设置为 false 代表拒绝重新入队 该队列如果配置了死信交换机将发送到死信队列中
                channel.basicReject(message.getEnvelope().getDeliveryTag(), false);
            }else {
                System.out.println("Consumer01接受的消息是："+msg);
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            }

        };
        //开启手动应答，也就是关闭手动应答
        channel.basicConsume(NORMAL_QUEUE,false,deliverCallback,consumerTag -> {});
    }

}
```

+ 开启消费者C1，创建出队列，然后停止该 C1 的运行，启动生产者

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220725/image.ncokzmns2t.webp)

+ 启动消费者 C1 等待 10 秒之后，再启动消费者 C2

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.1xxchsmgsgdc.webp)

+ C1控制台

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220725/image.5zgbkg8gij80.webp)

+ C2控制台

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.783con5n7f40.webp)

# 8.RabbitMQ 延迟队列

[[toc]]

## 延迟队列介绍

**延迟队列概念：**

延时队列,队列内部是有序的，最重要的特性就体现在它的延时属性上，延时队列中的元素是希望 在指定时间到了以后或之前取出和处理，简单来说，延时队列就是用来存放需要在指定时间被处理的 元素的队列。

**延迟队列使用场景：**

1. 订单在十分钟之内未支付则自动取消
2. 新创建的店铺，如果在十天内都没有上传过商品，则自动发送消息提醒
3. 用户注册成功后，如果三天内没有登陆则进行短信提醒
4. 用户发起退款，如果三天内没有得到处理则通知相关运营人员
5. 预定会议后，需要在预定的时间点前十分钟通知各个与会人员参加会议

这些场景都有一个特点，需要在某个事件发生之后或者之前的指定时间点完成某一项任务，如： 发生订单生成事件，在十分钟之后检查该订单支付状态，然后将未支付的订单进行关闭；那我们一直轮询数据，每秒查一次，取出需要被处理的数据，然后处理不就完事了吗？

如果数据量比较少，确实可以这样做，比如：对于「如果账单一周内未支付则进行自动结算」这样的需求， 如果对于时间不是严格限制，而是宽松意义上的一周，那么每天晚上跑个定时任务检查一下所有未支付的账单，确实也是一个可行的方案。但对于数据量比较大，并且时效性较强的场景，如：「订单十分钟内未支付则关闭」，短期内未支付的订单数据可能会有很多，活动期间甚至会达到百万甚至千万级别，对这么庞大的数据量仍旧使用轮询的方式显然是不可取的，很可能在一秒内无法完成所有订单的检查，同时会给数据库带来很大压力，无法满足业务要求而且性能低下。

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.21tyby27zrog.webp)

## TTL的两种设置

TTL 是什么呢？TTL 是 RabbitMQ 中一个消息或者队列的属性，表明一条消息或者该队列中的所有消息的最大存活时间，单位是毫秒。

换句话说，如果一条消息设置了 TTL 属性或者进入了设置 TTL 属性的队列，那么这条消息如果在 TTL 设置的时间内没有被消费，则会成为「死信」。如果同时配置了队列的 TTL 和消息的 TTL，那么较小的那个值将会被使用，有两种方式设置 TTL。

**队列设置 TTL**

在创建队列的时候设置队列的 x-message-ttl 属性

```java
Map<String, Object> params = new HashMap<>();
params.put("x-message-ttl",5000);
return QueueBuilder.durable("QA").withArguments(args).build(); // QA 队列的最大存活时间位 5000 毫秒
```

**消息设置 TTL**

针对每条消息设置 TTL

```java
rabbitTemplate.converAndSend("X","XC",message,correlationData -> {
    correlationData.getMessageProperties().setExpiration("5000");
});
```

两个代码块来自下方的案例

**两者区别**

如果设置了队列的 TTL 属性，那么一旦消息过期，就会被队列丢弃(如果配置了死信队列被丢到死信队列中)，而第二种方式，消息即使过期，也不一定会被马上丢弃，因为消息是否过期是在即将投递到消费者之前判定的，如果当前队列有严重的消息积压情况，则已过期的消息也许还能存活较长时间，具体看下方案例。

另外，还需要注意的一点是，如果不设置 TTL，表示消息永远不会过期，如果将 TTL 设置为 0，则表示除非此时可以直接投递该消息到消费者，否则该消息将会被丢弃

## 整合SpringBoot

前一小节我们介绍了死信队列，刚刚又介绍了 TTL，至此利用 RabbitMQ 实现延时队列的两大要素已经集齐，接下来只需要将它们进行融合，再加入一点点调味料，延时队列就可以新鲜出炉了。想想看，延时队列，不就是想要消息延迟多久被处理吗，TTL 则刚好能让消息在延迟多久之后成为死信，另一方面，成为死信的消息都会被投递到死信队列里，这样只需要消费者一直消费死信队列里的消息就完事了，因为里面的消息都是希望被立即处理的消息。

1. 创建一个 Maven 工程或者 Spring Boot工程
2. 添加依赖，这里的 Spring Boot 是2.5.5 版本

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.5.5</version>
    <relativePath/> 
</parent>
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <version>1.2.47</version>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
    <!--RabbitMQ 依赖-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>
</dependencies>
```

3. 创建 `application.yml` 文件

```yaml
server:
  port: 8888
spring:
  rabbitmq:
    host: 192.168.91.200
    port: 5672
    username: root
    password: 123
```

这里是 8808 端口，可根据需求决定端口

4. 新建主启动类

```java
@SpringBootApplication
public class RabbitmqSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqSpringbootApplication.class, args);
    }

}
```

## 队列TTL

### **代码架构图**

创建两个队列 QA 和 QB，两个队列的 TTL 分别设置为 10S 和 40S，然后再创建一个交换机 X 和死信交换机 Y，它们的类型都是 direct，创建一个死信队列 QD，它们的绑定关系如下：

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220725/image.6ipia9redgw0.webp)

原先配置队列信息，写在了生产者和消费者代码中，现在可写在配置类中，生产者只发消息，消费者只接受消息

### **配置类代码**

```java
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/25  22:31
 * desc:TTL队列 配置文件类代码
 */
@Configuration
public class TtlQueueConfig {

    //普通交换机的名称
    public static final String X_EXCHANGE="X";
    //死信交换机的名称
    public static final String Y_DEAD_LETTER_EXCHANGE="Y";
    //普通队列的名称
    public static final String QUEUE_A="QA";
    public static final String QUEUE_B="QB";
    //死信队列的名称
    public static final String DEAD_LETTER_QUEUE="QD";

    //声明xExchange  别名
    @Bean("xExchange")
    public DirectExchange xExchange(){
        return new DirectExchange(X_EXCHANGE);
    }

    //声明yExchange 别名
    @Bean("yExchange")
    public DirectExchange yExchange(){
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }

    //声明普通队列  要有ttl 为10s
    @Bean("queueA")
    public Queue queueA(){
        Map<String,Object> arguments = new HashMap<>(3);
        //设置死信交换机
        arguments.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        //设置死信RoutingKey
        arguments.put("x-dead-letter-routing-key","YD");
        //设置TTL 10s 单位是ms
        arguments.put("x-message-ttl",10000);
        return QueueBuilder.durable(QUEUE_A).withArguments(arguments).build();
    }

    //声明普通队列  要有ttl 为40s
    @Bean("queueB")
    public Queue queueB(){
        Map<String,Object> arguments = new HashMap<>(3);
        //设置死信交换机
        arguments.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        //设置死信RoutingKey
        arguments.put("x-dead-letter-routing-key","YD");
        //设置TTL 10s 单位是ms
        arguments.put("x-message-ttl",40000);
        return QueueBuilder.durable(QUEUE_B).withArguments(arguments).build();
    }

    //声明死信队列  要有ttl 为40s
    @Bean("queueD")
    public Queue queueD(){
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    //声明队列 QA 绑定 X 交换机
    @Bean
    public Binding queueABindingX(@Qualifier("queueA") Queue queueA,
                                  @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }

    //声明队列 QB 绑定 X 交换机
    @Bean
    public Binding queueBBindingX(@Qualifier("queueB") Queue queueB,
                                  @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueB).to(xExchange).with("XB");
    }

    //声明队列 QD 绑定 Y 交换机
    @Bean
    public Binding queueDBindingY(@Qualifier("queueD") Queue queueD,
                                  @Qualifier("yExchange") DirectExchange yExchange){
        return BindingBuilder.bind(queueD).to(yExchange).with("YD");
    }

}
```

### 生产者

**Controller 层代码，获取消息，放到 RabbitMQ** 里

```java
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/25  23:00
 * desc:发送延迟消息
 */
@Slf4j
@RestController
@RequestMapping("/ttl")
public class SendMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    //开始发消息
    @GetMapping("/sendMsg/{message}")
    public void sendMsg(@PathVariable("message") String message){
        log.info("当前时间:{},发送一条信息给两个TTL队列：{}",new Date().toString(),message);
        rabbitTemplate.convertAndSend("X","XA","消息来自ttl为10s的队列:"+message);
        rabbitTemplate.convertAndSend("X","XB","消息来自ttl为40s的队列:"+message);

    }
}
```

发起一个请求：[http://localhost:8888/ttl/sendMsg/嘻嘻嘻](http://localhost:8888/ttl/sendMsg/%E5%98%BB%E5%98%BB%E5%98%BB)

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220725/image.2x90cjk4j3c0.webp)

第一条消息在 10S 后变成了死信消息，然后被消费者消费掉，第二条消息在 40S 之后变成了死信消息， 然后被消费掉，这样一个延时队列就打造完成了。

不过，如果这样使用的话，岂不是每增加一个新的时间需求，就要新增一个队列，这里只有 10S 和 40S 两个时间选项，如果需要一个小时后处理，那么就需要增加 TTL 为一个小时的队列，如果是预定会议室然后提前通知这样的场景，岂不是要增加无数个队列才能满足需求？

## 延时队列TTL优化

在这里新增了一个队列 QC，该队列不设置 TTL 时间，根据前端的请求确定 TTL 时间，绑定关系如下：

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220725/image.267zky9xj8dc.webp)

### **配置类代码**

新增一个配置文件类，用于新增队列 QC，也可以放在上方的配置文件类里

```java
@Configuration
public class MsgTtlQueueConfig {

    //普通队列的名称
    public static final String QUEUE_C = "QC";

    //死信交换机的名称
    public static final String Y_DEAD_LETTER_EXCHANGE="Y";

    //声明QC
    @Bean("queueC")
    public Queue QueueC(){
        Map<String,Object> arguments = new HashMap<>(3);
        //设置死信交换机
        arguments.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        //设置死信RoutingKey
        arguments.put("x-dead-letter-routing-key","XC");
        return QueueBuilder.durable(QUEUE_C).withArguments(arguments).build();
    }
    //声明队列 QC 绑定 X 交换机
    @Bean
    public Binding queueCBindingX(@Qualifier("queueC") Queue queueC,
                                  @Qualifier("xExchange")DirectExchange xExchange){
        return BindingBuilder.bind(queueC).to(xExchange).with("XC");
    }

}
```

### 生产者

**Controller 新增方法**

该方法接收的请求要带有 TTL 时间

```java {18-28}
@Slf4j
@RestController
@RequestMapping("/ttl")
public class SendMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    //开始发消息
    @GetMapping("/sendMsg/{message}")
    public void sendMsg(@PathVariable("message") String message){
        log.info("当前时间:{},发送一条信息给两个TTL队列：{}",new Date().toString(),message);
        rabbitTemplate.convertAndSend("X","XA","消息来自ttl为10s的队列:"+message);
        rabbitTemplate.convertAndSend("X","XB","消息来自ttl为40s的队列:"+message);

    }

    //开始发消息 发TTL
    @GetMapping("/sendExpirationMsg/{message}/{ttlTime}")
    public void sendMsg(@PathVariable("message") String message,
                        @PathVariable("ttlTime") String ttlTime){
        log.info("当前时间:{},发送一条时长是{}毫秒TTL信息给队列QC：{}",
                new Date().toString(),ttlTime,message);
        rabbitTemplate.convertAndSend("X","XC",message,msg -> {
            //发送消息的时候的延迟时长
            msg.getMessageProperties().setExpiration(ttlTime);
            return msg;
        });
    }
}
```

重启下面，发送请求：

[http://localhost:8888/ttl/sendExpirationMsg/你好1/20000]()

[http://localhost:8888/ttl/sendExpirationMsg/你好2/2000]()

> **出现问题**:
>
> ![1658765665496](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/1658765665496.39yyrmljat80.webp)

看起来似乎没什么问题，但是在最开始的时候，就介绍过如果使用在消息属性上设置 TTL 的方式，消息可能并不会按时「死亡」

> 因为 RabbitMQ 只会检查第一个消息是否过期，如果过期则丢到死信队列， 如果第一个消息的延时时长很长，而第二个消息的延时时长很短，第二个消息并不会优先得到执行

这也就是为什么如图的时间：你好 2 延时 2 秒，却后执行，还要等待你好 1 消费后再执行你好2

## Rabbitmq插件实现延迟队列

上文中提到的问题，确实是一个问题，如果不能实现在消息粒度上的 TTL，并使其在设置的 TTL 时间及时死亡，就无法设计成一个通用的延时队列。那如何解决呢，接下来我们就去解决该问题。

**安装延时队列插件**

可去[官网下载](https://www.rabbitmq.com/community-plugins.html)找到 **rabbitmq_delayed_message_exchange** 插件，放置到 RabbitMQ 的插件目录。

因为官网也是跳转去该插件的 GitHub 地址进行下载：[点击跳转](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases)

打开 Linux，用 `Xftp` 将插件放到 RabbitMQ 的<mark>安装目录</mark>下的 plgins 目录，

RabbitMQ 与其 plgins 目录默认分别位于

```sh
# RabbitMQ 安装目录
cd /usr/lib/rabbitmq/lib/rabbitmq_server-3.8.8   
# RabbitMQ 的 plgins 所在目录
cd /usr/lib/rabbitmq/lib/rabbitmq_server-3.8.8/plugins
```

其中我的版本是 `/rabbitmq_server-3.8.8`

进入目录后执行下面命令让该插件生效，然后重启 RabbitMQ

```sh
# 安装
rabbitmq-plugins enable rabbitmq_delayed_message_exchange
# 重启服务
systemctl restart rabbitmq-server
```

```sh
[root@master plugins]# rabbitmq-plugins enable rabbitmq_delayed_message_exchange
Enabling plugins on node rabbit@master:
rabbitmq_delayed_message_exchange
The following plugins have been configured:
  rabbitmq_delayed_message_exchange
  rabbitmq_management
  rabbitmq_management_agent
  rabbitmq_web_dispatch
Applying plugin configuration to rabbit@master...
The following plugins have been enabled:
  rabbitmq_delayed_message_exchange

started 1 plugins.
[root@master plugins]# systemctl restart rabbitmq-server
```

::: tip 解释

安装命令不能出现插件版本和后缀，如 `rabbitmq-plugins enable rabbitmq_delayed_message_exchange-3.8.0.ez` 会报错

必须是 `rabbitmq-plugins enable rabbitmq_delayed_message_exchange`，后面不允许填入版本和文件后缀

:::

打开 Web 界面，查看交换机的新增功能列表，如果多出了如图所示，代表成功添加插件

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220725/image.3nqc69j0pws0.webp)

## 插件实战

在这里新增了一个队列 delayed.queue，一个自定义交换机 delayed.exchange，绑定关系如下:

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.1tla4z5g4yps.webp)

### 配置类代码

新增一个配置类 `DelayedQueueConfig`，也可以放在原来的配置文件里，代码里使用了 `CustomExchange` 类，通过参数来自定义一个类型(direct、topic等)

在我们自定义的交换机中，这是一种新的交换类型，该类型消息支持延迟投递机制消息传递后并不会立即投递到目标队列中，而是存储在 mnesia(一个分布式数据系统)表中，当达到投递时间时，才投递到目标队列中。

```java
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/26  0:39
 */
@Configuration
public class DelayedQueueConfig {

    //交换机
    public static final String DELAYED_EXCHANGE_NAME = "delayed.exchange";
    //队列
    public static final String DELAYED_QUEUE_NAME = "delayed.queue";
    //routingKey
    public static final String DELAYED_ROUTING_KEY = "delayed.routingkey";


    @Bean
    public Queue delayedQueue(){
        return new Queue(DELAYED_QUEUE_NAME);
    }

    //声明交换机,基于插件的交换机
    @Bean
    public CustomExchange delayedExchange(){

        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-delayed-type","direct");
        /**
         * 1.交换机的名称
         * 2.交换机的类型 x-delayed-message
         * 3.是否需要持久化
         * 4.是否需要自动删除
         * 5.其他的参数
         */
        return new CustomExchange(DELAYED_EXCHANGE_NAME,"x-delayed-message",
                true,false,arguments);
    }

    //绑定
    @Bean
    public Binding delayedQueueBindingDelayedExchange(
            @Qualifier("delayedQueue") Queue delayedQueue,
            @Qualifier("delayedExchange")CustomExchange delayedExchange){
        return BindingBuilder.bind(delayedQueue).to(delayedExchange)
                .with(DELAYED_ROUTING_KEY).noargs();
    }
}
```

**生产者代码**

在 controller 里新增一个方法

```java
    //开始发消息，基于插件的 消息及 延迟的时间
    @GetMapping("/sendDelayMsg/{message}/{delayTime}")
    public void sendMsg(@PathVariable("message") String message,
                        @PathVariable("delayTime") Integer delayTime){
        log.info("当前时间:{},发送一条时长是{}毫秒TTL信息给延迟队列delayed.queue：{}",
                new Date().toString(),delayTime,message);

        rabbitTemplate.convertAndSend(DelayedQueueConfig.DELAYED_EXCHANGE_NAME,
                DelayedQueueConfig.DELAYED_ROUTING_KEY,message, msg -> {
            //发送消息的时候的延迟时长 单位ms
            msg.getMessageProperties().setDelay(delayTime);
            return msg;
        });
    }
```

**消费者代码**

监听延时队列，如果有消息进入该队列，则打印到控制台

```java
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/26  1:00
 * desc:消费者，消费基于插件的延迟消息
 */
@Slf4j
@Component
public class DelayQueueConsumer {

    @RabbitListener(queues = DelayedQueueConfig.DELAYED_QUEUE_NAME)
    public void receiveDelayQueue(Message message){
        String msg = new String(message.getBody());
        log.info("当前时间：{},收到延时队列的消息：{}", new Date().toString(), msg);
    }
}
```

[http://localhost:8888/ttl/sendDelayMsg/hello1/20000](http://localhost:8888/ttl/sendDelayMsg/hello1/20000)

[http://localhost:8888/ttl/sendDelayMsg/hello2/2000](http://localhost:8888/ttl/sendDelayMsg/hello2/2000)

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.17h77dwilchs.webp)

可以看到哪怕 hello1 需要20秒再进入延时队列，hello2 2 秒后直接进入延时队列，无需等待 hello1

## 总结

延时队列在需要延时处理的场景下非常有用，使用 RabbitMQ 来实现延时队列可以很好的利用 RabbitMQ 的特性，如：消息可靠发送、消息可靠投递、死信队列来保障消息至少被消费一次以及未被正确处理的消息不会被丢弃。另外，通过 RabbitMQ 集群的特性，可以很好的解决单点故障问题，不会因为单个节点挂掉导致延时队列不可用或者消息丢失。

当然，延时队列还有很多其它选择，比如利用 Java 的 DelayQueue，利用 Redis 的 zset，利用 Quartz 或者利用 kafka 的时间轮，这些方式各有特点,看需要适用的场景。

# 9.RabbitMQ 发布确认高级

[[toc]]

在生产环境中由于一些不明原因，导致 RabbitMQ 重启，在 RabbitMQ 重启期间生产者消息投递失败，导致消息丢失，需要手动处理和恢复。于是，我们开始思考，如何才能进行 RabbitMQ 的消息可靠投递呢？

## 简单测试案例（同一个项目内）SpringBoot版本

### 配置：

application.properties

```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/mybatis?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
spring.datasource.username=root
spring.datasource.password=root

#配置生产者消费之


spring.rabbitmq.addresses=192.168.198.131
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=password
spring.rabbitmq.virtual-host=/
spring.rabbitmq.connection-timeout=15000
spring.rabbitmq.publisher-confirm-type=correlated


```



```java
package com.mcxgroup.testspringboot.producer;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicRabbitConfig {
    @Bean
    public Queue queue1(){
        return new Queue("queue1");
    }
    @Bean
    public Queue queue2(){
        return new Queue("queue2");
    }
//    实现topic的交换机
    //定义交换机
    @Bean
    public TopicExchange exchange(){
        return new TopicExchange("bootExchange");
    }
    //绑定
    @Bean
    public Binding bindingExchange1(Queue queue1, TopicExchange exchange){
        return BindingBuilder.bind(queue1).to(exchange).with("dog.red");
    }
    //绑定
    @Bean
    public Binding bindingExchange2(Queue queue2, TopicExchange exchange){
        return BindingBuilder.bind(queue2).to(exchange).with("dog.#");
    }
}

```





### 生产者和消费者类

```java
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 发送消息
 */
@Component
public class MsgSender {
    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send1(){
        String message = "This is msg1,routingKey is dog.red";
        System.out.println("message = " + message);
        this.rabbitTemplate.convertAndSend("bootExchange","dog.red",message);
    }
    public void send2(){
        String message = "This is msg2,routingKey is dog.black";
        System.out.println("message = " + message);
        this.rabbitTemplate.convertAndSend("bootExchange","dog.black",message);
    }
}

```





```java
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "queue1")
public class Reciver01 {
    @RabbitHandler
    public void process(String message){
        System.out.println("Reciver01 message = " + message);
    }
}

```



### 测试类

```java
package com.mcxgroup.testspringboot;

import com.mcxgroup.testspringboot.producer.MsgSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestSpringBootApplicationTests {

    @Autowired
    MsgSender msgSender;
    @Test
    public void send1(){
        msgSender.send1();
    }
    @Test
    public void send2(){
        msgSender.send2();
    }
}
```





## 复杂测试案例

简单的发布确认机制在[应答与签收](/middleware/RabbitMQ/RabbitMQ_Message_responseAndrelease/)已经介绍，本内容将介绍整合了 SpringBoot 的发布确认机制。

### 介绍

首先发布消息后进行备份在缓存里，如果消息成功发布确认到交换机，则从缓存里删除该消息，如果没有成功发布，则设置一个定时任务，重新从缓存里获取消息发布到交换机，直到成功发布到交换机。

确认机制图例：

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220726/image.7c8btl8ibwo0.webp)

### 实战

一个交换机：confirm.exchange，一个队列：confirm.queue，一个消费者：confirm.consumer

其中交换机类型时 direct，与队列关联的 routingKey 是 key1

代码架构图：

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.7ddz8ceuxk40.webp)

在配置文件当中需要添加：

```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/mybatis?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
spring.datasource.username=root
spring.datasource.password=root

#配置生产者消费之
spring.application.name=producer

spring.rabbitmq.addresses=192.168.198.131
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=password
spring.rabbitmq.virtual-host=/
spring.rabbitmq.connection-timeout=15000
spring.rabbitmq.publisher-confirm-type=correlated

```

- `NONE` 值是禁用发布确认模式，是默认值
- `CORRELATED` 值是发布消息成功到交换器后会触发回调方法
- `SIMPLE` 值经测试有两种效果，其一效果和 CORRELATED 值一样会触发回调方法，其二在发布消息成功后使用 rabbitTemplate 调用 waitForConfirms 或 waitForConfirmsOrDie 方法等待 broker 节点返回发送结果，根据返回结果来判定下一步的逻辑，要注意的点是 waitForConfirmsOrDie 方法如果返回 false 则会关闭 channel，则接下来无法发送消息到 broker;





### 添加配置类

声明交换机和队列，并且将交换机和队列进行绑定

```java
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/26  19:05
 * desc：配置类，发布确认(高级)
 */
@Configuration
public class ConfirmConfig {

    //交换机
    public static final String CONFIRM_EXCHANGE_NAME = "confirm_exchange";
    //队列
    public static final String CONFIRM_QUEUE_NAME = "confirm_queue";
    //routingKey
    public static final String CONFIRM_ROUTING_KEY = "key1";

    //声明交换机
    @Bean("confirmExchange")
    public DirectExchange confirmExchange(){
        return new DirectExchange(CONFIRM_EXCHANGE_NAME);
    }

    //声明队列
    @Bean("confirmQueue")
    public Queue confirmQueue(){
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
    }

    //绑定
    @Bean
    public Binding queueBindingExchange(@Qualifier("confirmQueue") Queue confirmQueue,
                                        @Qualifier("confirmExchange") DirectExchange confirmExchange){
        return BindingBuilder.bind(confirmQueue).to(confirmExchange).with(CONFIRM_ROUTING_KEY);
    }
}
```

### **消息生产者**

也可以说是 Controller 层

```java
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/26  19:17
 * desc:高级消息发布 消息生产者
 */
@Slf4j
@RequestMapping("/confirm")
@RestController
public class ProductController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //开始发消息,测试确认
    @GetMapping("/sendMessage/{message}")
    public void sendMessage(@PathVariable("message") String message){
        //指定消息 id 为 1
        CorrelationData correlationData1 = new CorrelationData("1");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME,
                ConfirmConfig.CONFIRM_ROUTING_KEY,message+"key1",correlationData1);
        log.info("发送消息内容:{}",message+"key1");

        //指定消息 id 为 2
        CorrelationData correlationData2 = new CorrelationData("2");
        String CONFIRM_ROUTING_KEY = "key2";
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME,
                CONFIRM_ROUTING_KEY,message+"key2",correlationData2);
        log.info("发送消息内容:{}",message+"key2");
    }

}
```

### **消息消费者**

监听 `confirm.queue` 队列

```java
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/26  19:29
 * desc:接受消息
 */
@Slf4j
@Component
public class Consumer {

    @RabbitListener(queues = ConfirmConfig.CONFIRM_QUEUE_NAME)
    public void receiveConfirmMessage(Message message){
        String msg = new String(message.getBody());
        log.info("接受到的队列confirm.queue消息:{}",msg);
    }
}
```

### **消息生产者发布消息后的回调接口**

只要生产者发布消息，交换机不管是否收到消息，都会调用该类的 `confirm` 方法

```java
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/26  20:10
 * desc:回调接口
 */
@Slf4j
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback {

    //注入
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init(){
        //注入
        rabbitTemplate.setConfirmCallback(this);
    }
    /**
     * 交换机不管是否收到消息的一个回调方法
     * 1. 发消息 交换机接收到了 回调
     * @param correlationData  保存回调信息的Id及相关信息
     * @param ack              交换机收到消息 为true
     * @param cause            未收到消息的原因
     *
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack,String cause) {
        String id = correlationData!=null?correlationData.getId():"";
        if(ack){
            log.info("交换机已经收到了ID为:{}的消息",id);
        }else {
            log.info("交换机还未收到ID为:{}的消息，由于原因:{}",id,cause);
        }
    }
}
```

[http://localhost:8888/confirm/sendMessage/大家好1](http://localhost:8888/confirm/sendMessage/%E5%A4%A7%E5%AE%B6%E5%A5%BD1)

结果分析:

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220726/image.5cpvzjf8zb40.webp)

可以看到，发送了两条消息，第一条消息的 RoutingKey 为 "key1"，第二条消息的 RoutingKey 为 "key2"，两条消息都成功被交换机接收，也收到了交换机的确认回调，但消费者只收到了一条消息，因为第二条消息的 RoutingKey 与队列的 BindingKey 不一致，也没有其它队列能接收这个消息，所有第二条消息被直接丢弃了。

丢弃的消息交换机是不知道的，需要解决告诉生产者消息传送失败。

## 回退消息

### 介绍

获取回退的消息，首先在配置文件开启该功能，然后需要自定义类实现 `RabbitTemplate.ReturnsCallback` 接口，并且初始化时，使用该自定义类作为回退消息的处理类，同时开启 `Mandatory`，设置为 true

在启动开启 Mandatory，或者在代码里手动开启 Mandatory 参数，或者都开启😸

配置类文件开启：

```yaml
# 新版
spring:
  rabbitmq:
  	template:
      mandatory: true
      
# 旧版
spring:
  rabbitmq:
    mandatory: true
```

代码中开启:

```java
rabbitTemplate.setMandatory(true);
```

在仅开启了生产者确认机制的情况下，交换机接收到消息后，会直接给消息生产者发送确认消息，如果发现该消息不可路由，那么消息会被直接丢弃，此时生产者是不知道消息被丢弃这个事件的。

那么如何让无法被路由的消息帮我想办法处理一下？最起码通知我一声，我好自己处理啊。通过设置 mandatory 参数可以在当消息传递过程中不可达目的地时将消息返回给生产者。

## 实战

### 修改配置文件

```yaml {8-10}
spring:
    rabbitmq:
        host: 192.168.91.200
        port: 5672
        username: root
        password: 123
        publisher-confirm-type: correlated
        publisher-returns: true
        template:
            mandatory: true
server:
    port: 8888
```

### 修改回调接口

实现 `RabbitTemplate.ReturnsCallback` 接口，并实现方法

```java {19,50-55}
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/26  20:10
 * desc:回调接口
 */
@Slf4j
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {

    //注入
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init(){
        //注入
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }
    /**
     * 交换机不管是否收到消息的一个回调方法
     * 1. 发消息 交换机接收到了 回调
     * @param correlationData  保存回调信息的Id及相关信息
     * @param ack              交换机收到消息 为true
     * @param cause            未收到消息的原因
     *
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack,String cause) {
        String id = correlationData!=null?correlationData.getId():"";
        if(ack){
            log.info("交换机已经收到了ID为:{}的消息",id);
        }else {
            log.info("交换机还未收到ID为:{}的消息，由于原因:{}",id,cause);
        }
    }


    //可以在当消息传递过程中不可达目的地时将消息返回给生产者
    //只有不可达目的地的时候 才进行回退
    /**
     * 当消息无法路由的时候的回调方法
     *  message      消息
     *  replyCode    编码
     *  replyText    退回原因
     *  exchange     从哪个交换机退回
     *  routingKey   通过哪个路由 key 退回
     */
    @Override
    public void returnedMessage(ReturnedMessage returned) {
        log.error("消息{},被交换机{}退回，退回原因:{},路由key:{}",
                new String(returned.getMessage().getBody()),returned.getExchange(),
                returned.getReplyText(),returned.getRoutingKey());
    }
}
```

打开浏览器访问地址：[http://localhost:8888/confirm/sendMessage/大家好1](http://localhost:8888/confirm/sendMessage/%E5%A4%A7%E5%AE%B6%E5%A5%BD1)

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220726/image.6ag1qfe3xws0.webp)

## 备份交换机

### 介绍

有了 mandatory 参数和回退消息，我们获得了对无法投递消息的感知能力，有机会在生产者的消息无法被投递时发现并处理。但有时候，我们并不知道该如何处理这些无法路由的消息，最多打个日志，然后触发报警，再来手动处理。而通过日志来处理这些无法路由的消息是很不优雅的做法，特别是当生产者所在的服务有多台机器的时候，手动复制日志会更加麻烦而且容易出错。而且设置 mandatory 参数会增加生产者的复杂性，需要添加处理这些被退回的消息的逻辑。如果既不想丢失消息，又不想增加生产者的复杂性，该怎么做呢？

前面在设置死信队列的文章中，我们提到，可以为队列设置死信交换机来存储那些处理失败的消息，可是这些不可路由消息根本没有机会进入到队列，因此无法使用死信队列来保存消息。 在 RabbitMQ 中，有一种备份交换机的机制存在，可以很好的应对这个问题。

什么是备份交换机呢？备份交换机可以理解为 RabbitMQ 中交换机的“备胎”，当我们为某一个交换机声明一个对应的备份交换机时，就是为它创建一个备胎，当交换机接收到一条不可路由消息时，将会把这条消息转发到备份交换机中，由备份交换机来进行转发和处理，通常备份交换机的类型为 Fanout ，这样就能把所有消息都投递到与其绑定的队列中，然后我们在备份交换机下绑定一个队列，这样所有那些原交换机无法被路由的消息，就会都进 入这个队列了。当然，我们还可以建立一个报警队列，用独立的消费者来进行监测和报警。

### 实战

需要一个备份交换机 `backup.exchange`，类型为 `fanout`，该交换机发送消息到队列 `backup.queue` 和 `warning.queue`

代码结构图:

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.109ugczigfdc.webp)

### **修改高级确认发布 配置类**

```java {29-30}
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/26  19:05
 * desc：配置类，发布确认(高级)
 */
@Configuration
public class ConfirmConfig {

    //交换机
    public static final String CONFIRM_EXCHANGE_NAME = "confirm_exchange";
    //队列
    public static final String CONFIRM_QUEUE_NAME = "confirm_queue";
    //routingKey
    public static final String CONFIRM_ROUTING_KEY = "key1";

    //关于备份的
    //交换机
    public static final String BACKUP_EXCHANGE_NAME = "backup_exchange";
    //队列
    public static final String BACKUP_QUEUE_NAME = "backup_queue";
    //报警队列
    public static final String WARNING_QUEUE_NAME = "warning_queue";


    //声明交换机,设置该交换机的备份交换机
    @Bean("confirmExchange")
    public DirectExchange confirmExchange(){
        return ExchangeBuilder.directExchange(CONFIRM_EXCHANGE_NAME)
                .durable(true).withArgument("alternate-exchange",BACKUP_EXCHANGE_NAME).build();
    }

    //声明队列
    @Bean("confirmQueue")
    public Queue confirmQueue(){
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
    }

    //绑定
    @Bean
    public Binding queueBindingExchange(@Qualifier("confirmQueue") Queue confirmQueue,
                                        @Qualifier("confirmExchange") DirectExchange confirmExchange){
        return BindingBuilder.bind(confirmQueue).to(confirmExchange).with(CONFIRM_ROUTING_KEY);
    }

    //备份交换机的创建
    @Bean("backupExchange")
    public FanoutExchange backupExchange(){
        return new FanoutExchange(BACKUP_EXCHANGE_NAME);
    }

    //声明备份队列
    @Bean("backupQueue")
    public Queue backupQueue(){
        return QueueBuilder.durable(BACKUP_QUEUE_NAME).build();
    }

    //声明报警队列
    @Bean("warningQueue")
    public Queue warningQueue(){
        return QueueBuilder.durable(WARNING_QUEUE_NAME).build();
    }

    //绑定 备份队列绑定备份交换机
    @Bean
    public Binding backupQueueBindingBackupExchange(@Qualifier("backupQueue") Queue backupQueue,
                                        @Qualifier("backupExchange") FanoutExchange backupExchange){
        return BindingBuilder.bind(backupQueue).to(backupExchange);
    }

    //绑定 报警队列绑定备份交换机
    @Bean
    public Binding warningQueueBindingBackupExchange(@Qualifier("warningQueue") Queue warningQueue,
                                                    @Qualifier("backupExchange") FanoutExchange backupExchange){
        return BindingBuilder.bind(warningQueue).to(backupExchange);
    }

}
```

### 报警消费者

```java
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/26  22:32
 * decs:报警消费者
*/
@Slf4j
@Component
public class WarningConsumer {

    //接收报警信息
    @RabbitListener(queues = ConfirmConfig.WARNING_QUEUE_NAME)
    public void receiveWarningMsg(Message message){
        String msg = new String(message.getBody());
        log.error("报警发现不可路由消息:{}",msg);
    }
}
```

由于之前写过 `confirm.exchange` 交换机，当更改配置了，需要删掉，不然会报错

打开浏览器访问地址：[http://localhost:8888/confirm/sendMessage/大家好1](http://localhost:8888/confirm/sendMessage/%E5%A4%A7%E5%AE%B6%E5%A5%BD1)

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.42foi4mawzq0.webp)

Mandatory 参数与备份交换机可以一起使用的时候，如果两者同时开启，消息究竟何去何从？谁优先级高，经过上面结果显示答案是**备份交换机优先级高**。

# 10.RabbitMQ 其他知识点

[[toc]]

## 幂等性

### 概念

用户对于同一操作发起的一次请求或者多次请求的结果是一致的，不会因为多次点击而产生了副作用。举个最简单的例子，那就是支付，用户购买商品后支付，支付扣款成功，但是返回结果的时候网络异常，此时钱已经扣了，用户再次点击按钮，此时会进行第二次扣款，返回结果成功，用户查询余额发现多扣钱了，流水记录也变成了两条。在以前的单应用系统中，我们只需要把数据操作放入事务中即可，发生错误立即回滚，但是再响应客户端的时候也有可能出现网络中断或者异常等等。

可以理解为验证码，只能输入一次，再次重新输入会刷新验证码，原来的验证码失效。

### 消息重复消费

消费者在消费 MQ 中的消息时，MQ 已把消息发送给消费者，消费者在给 MQ 返回 ack 时网络中断， 故 MQ 未收到确认信息，该条消息会重新发给其他的消费者，或者在网络重连后再次发送给该消费者，但实际上该消费者已成功消费了该条消息，造成消费者消费了重复的消息。

### 解决思路

MQ 消费者的幂等性的解决一般使用全局 ID 或者写个唯一标识比如时间戳 或者 UUID 或者订单消费者消费 MQ 中的消息也可利用 MQ 的该 id 来判断，或者可按自己的规则生成一个全局唯一 id，每次消费消息时用该 id 先判断该消息是否已消费过。

### 消费端的幂等性保障

在海量订单生成的业务高峰期，生产端有可能就会重复发生了消息，这时候消费端就要实现幂等性，这就意味着我们的消息永远不会被消费多次，即使我们收到了一样的消息。

业界主流的幂等性有两种操作：

- 唯一 ID+ 指纹码机制,利用数据库主键去重

指纹码：我们的一些规则或者时间戳加别的服务给到的唯一信息码,它并不一定是我们系统生成的，基本都是由我们的业务规则拼接而来，但是一定要保证唯一性，然后就利用查询语句进行判断这个 id 是否存在数据库中，优势就是实现简单就一个拼接，然后查询判断是否重复；劣势就是在高并发时，如果是单个数据库就会有写入性能瓶颈当然也可以采用分库分表提升性能，但也不是我们最推荐的方式。

- Redis 的原子性

利用 redis 执行 setnx 命令，天然具有幂等性。从而实现不重复消费

## 优先级队列

### 使用场景

在我们系统中有一个订单催付的场景，我们的客户在天猫下的订单，淘宝会及时将订单推送给我们，如果在用户设定的时间内未付款那么就会给用户推送一条短信提醒，很简单的一个功能对吧。

但是，tmall 商家对我们来说，肯定是要分大客户和小客户的对吧，比如像苹果，小米这样大商家一年起码能给我们创造很大的利润，所以理应当然，他们的订单必须得到优先处理，而曾经我们的后端系统是使用 redis 来存放的定时轮询，大家都知道 redis 只能用 List 做一个简简单单的消息队列，并不能实现一个优先级的场景，所以订单量大了后采用 RabbitMQ 进行改造和优化，如果发现是大客户的订单给一个相对比较高的优先级， 否则就是默认优先级。

### 添加方法

+ Web页面添加

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.2xm08z870l40.webp)

1. 进入 Web 页面，点击 Queue 菜单，然后点击 `Add a new queue`
2. 点击下方的 `Maximum priority`
3. 执行第二步，则会自动在 `Argument` 生成 `x-max-priority` 字符串
4. 点击 `Add queue` 即可添加优先级队列成功

+ 声明队列的时候添加优先级

设置队列的最大优先级 最大可以设置到 255 官网推荐 1-10 如果设置太高比较吃内存和 CPU

```java
Map<String, Object> params = new HashMap();
// 优先级为 10
params.put("x-max-priority", 10);
channel.queueDeclare("hello", true, false, false, params);
```

::: tip 注意事项

队列实现优先级需要做的事情有如下：队列需要设置为优先级队列，消息需要设置消息的优先级，消费者需要等待消息已经发送到队列中才去消费，因为这样才有机会对消息进行排序

:::

## 实战

生产者发送十个消息，如果消息为 `info5`，则优先级是最高的，当消费者从队列获取消息的时候，优先获取 `info5` 消息

### 生产者代码

```java
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/26  23:53
 * desc:优先级 生产者
 */
public class PriorityProducer {

    private static final String QUEUE_NAME = "priority_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        //给消息赋予一个priority属性
        AMQP.BasicProperties properties =
                new AMQP.BasicProperties().builder().priority(1).priority(10).build();

        for (int i = 1; i < 11; i++) {
            String message = "info"+i;
            if(i==5){
                channel.basicPublish("",QUEUE_NAME,properties,message.getBytes());
            }else {
                channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            }
            System.out.println("消息发送完成："+message);
        }
    }
}
```

### 消费者代码

```java
/**
 * @author frx
 * @version 1.0
 * @date 2022/7/26  23:58
 * desc:优先级 消费者
 */
public class PriorityConsumer {

    private final static String QUEUE_NAME = "priority_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = RabbitMQUtils.getChannel();

        //设置队列的最大优先级 最大可以设置到255 官网推荐1-10 如果设置太高比较吃内存和CPU
        Map<String, Object> params = new HashMap<>();
        params.put("x-max-priority",10);
        channel.queueDeclare(QUEUE_NAME,true,false,false,params);

        //推送消息如何进行消费的接口回调
        DeliverCallback deliverCallback = (consumerTag, delivery) ->{
            String message = new String(delivery.getBody());
            System.out.println("消费的消息: "+message);
        };

        //取消消费的一个回调接口 如在消费的时候队列被删除掉了
        CancelCallback cancelCallback = (consumerTag) ->{
            System.out.println("消息消费被中断");
        };

        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}
```

**效果演示**

info 5 的优先级为 10，优先级最高。消费者消费信息效果如图：

![image](https://cdn.staticaly.com/gh/xustudyxu/image-hosting1@master/20220726/image.3vjjukfu3r00.webp)

## 惰性队列

### 使用场景

RabbitMQ 从 3.6.0 版本开始引入了惰性队列的概念。惰性队列会尽可能的将消息存入磁盘中，而在消费者消费到相应的消息时才会被加载到内存中，它的一个重要的设计目标是能够支持更长的队列，即支持更多的消息存储。当消费者由于各种各样的原因(比如消费者下线、宕机亦或者是由于维护而关闭等)而致使长时间内不能消费消息造成堆积时，惰性队列就很有必要了。

默认情况下，当生产者将消息发送到 RabbitMQ 的时候，队列中的消息会尽可能的存储在内存之中，这样可以更加快速的将消息发送给消费者。即使是持久化的消息，在被写入磁盘的同时也会在内存中驻留一份备份。当 RabbitMQ 需要释放内存的时候，会将内存中的消息换页至磁盘中，这个操作会耗费较长的时间，也会阻塞队列的操作，进而无法接收新的消息。虽然 RabbitMQ 的开发者们一直在升级相关的算法， 但是效果始终不太理想，尤其是在消息量特别大的时候。

### 两种模式

队列具备两种模式：default 和 lazy。默认的为 default 模式，在 3.6.0 之前的版本无需做任何变更。lazy 模式即为惰性队列的模式，可以通过调用 `channel.queueDeclare` 方法的时候在参数中设置，也可以通过 Policy 的方式设置，如果一个队列同时使用这两种方式设置的话，那么 Policy 的方式具备更高的优先级。如果要通过声明的方式改变已有队列的模式的话，那么只能先删除队列，然后再重新声明一个新的。

在队列声明的时候可以通过 `x-queue-mode` 参数来设置队列的模式，取值为 default 和 lazy。下面示例中演示了一个惰性队列的声明细节：

```java
Map<String, Object> args = new HashMap<String, Object>();
args.put("x-queue-mode", "lazy");
channel.queueDeclare("myqueue", false, false, false, args);
```

也可以在 Web 页面添加队列时，选择 `Lazy mode`

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.m9m0vrilbw0.webp)

### 内存开销对比

![image](https://cdn.jsdelivr.net/gh/52chen/imagebed2023@main/picgo/image.18hc40o0ctcw.webp)

在发送 1 百万条消息，每条消息大概占 1KB 的情况下，普通队列占用内存是 1.2GB，而惰性队列仅仅占用 1.5MB

