## RocketMQ消息延时

### 一、什么是延时消息

当消息写入到Broker后，不能立刻被消费者消费，需要等待指定的时长后才可被消费处理的消息，称为延时消息。

### 二、延时消息等级

RocketMQ延时消息的延迟时长不支持随意时长的延迟，是通过特定的延迟等级来指定的。默认支持18个等级的延迟消息，延时等级定义在RocketMQ服务端的MessageStoreConfig类中的如下变量中：

```java
// MessageStoreConfig.java
private String messageDelayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
 
//发消息时，设置delayLevel等级即可：msg.setDelayLevel(level)。level有以下三种情况：
level == 0，消息为非延迟消息
1<=level<=maxLevel，消息延迟特定时间，例如level==1，延迟1s
level > maxLevel，则level== maxLevel，例如level==20，延迟2h
```

例如指定的延时等级为3，则表示延迟时长为10s，即延迟等级是从1开始计数的。

### 三、延时消息使用场景

采用RocketMQ的延时消息可以实现定时任务的功能，而无需使用定时器。使用场景主要有：

- **(1)、电商交易系统的订单超时未支付，自动取消订单**

在电商交易系统中，像淘宝、京东，我们提交了一个订单之后，在支付时都会提示，需要在指定时间内(例如30分钟)完成支付，否则订单将被取消的消息，实际上这个超时未支付功能就可以使用延时消息来实现。在下单成功之后，就发送一个延时消息，然后指定消息的延时时间为30分钟，这条消息将会在30分钟后投递给后台业务系统（Consumer），此时才能被消费者进行消费，消费消息的时候会再去检查这个订单的状态，确认下是否支付成功，如果支付成功，则忽略不处理；如果订单还是未支付，则进行取消订单、释放库存等操作；

- **(2)、活动场景**

比如B站视频投稿经常会发起一些活动，Up主在活动期间可以按照活动规则投稿视频，在活动时间截止后，后台根据Up主完成任务的情况以及结合投稿视频的播放量等进行判定，然后派发对应的奖励。这种场景我们也可以采用延时消息来实现，即在发起活动后，同时发送一条延时消息，延时时间设置为本次活动周期的时间。当活动结束后，这条延时消息刚好可以被消费者进行消费，这样就可以消费消息然后执行一系列的逻辑处理。

- **(3)、其它信息提醒等场景；**

### 四、延时消息示例

- (1)、编写Consumer消费端并启动，等待接收Producer发送过来的消息

```java
/**
 * 消息消费者
 */
public class MQConsumer {
    public static void main(String[] args) throws MQClientException {
 
        // 创建DefaultMQPushConsumer类并设定消费者名称
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer("consumer-group-test");
 
        // 设置NameServer地址，如果是集群的话，使用分号;分隔开
        mqPushConsumer.setNamesrvAddr("10.0.90.86:9876");
 
        // 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
        // 如果不是第一次启动，那么按照上次消费的位置继续消费
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
 
        // 设置消费模型，集群还是广播，默认为集群
        mqPushConsumer.setMessageModel(MessageModel.CLUSTERING);
 
        // 消费者最小线程量
        mqPushConsumer.setConsumeThreadMin(5);
 
        // 消费者最大线程量
        mqPushConsumer.setConsumeThreadMax(10);
 
        // 设置一次消费消息的条数，默认是1
        mqPushConsumer.setConsumeMessageBatchMaxSize(1);
 
        // 订阅一个或者多个Topic，以及Tag来过滤需要消费的消息，如果订阅该主题下的所有tag，则使用*
        mqPushConsumer.subscribe("DelayTopic", "*");
 
        // 注册回调实现类来处理从broker拉取回来的消息
        mqPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            // 监听类实现MessageListenerConcurrently接口即可，重写consumeMessage方法接收数据
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgList, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                for (MessageExt message : msgList) {
                    String body = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("消费者接收到消息: " + message.toString() + "---消息内容为：" + body + "消息被消费时间：" + new Date(System.currentTimeMillis()) + ", 消息存储时间: " + new Date(message.getBornTimestamp()));
                }
                // 标记该消息已经被成功消费
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
 
        // 启动消费者实例
        mqPushConsumer.start();
    }
}
```

- (2)、编写Producer生产端，发送延时消息

RocketMQ要实现发送延迟消息，只需在发送消息之前调用Message#setDelayTimeLevel()方法设置消息的延迟等级即可。

> 只需要设置一个延迟级别即可，注意不是具体的延迟时间。如果设置的延迟级别超过最大值，那么将会重置为最大值。

```java
/**
 * Producer端发送延迟消息：只需在发送消息之前调用setDelayTimeLevel()方法设置消息的延迟等级即可
 */
public class SyncMQProducer {
    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException, RemotingException, InterruptedException, MQBrokerException {
        // 创建DefaultMQProducer类并设定生产者名称
        DefaultMQProducer mqProducer = new DefaultMQProducer("producer-group-test");
        // 设置NameServer地址，如果是集群的话，使用分号;分隔开
        mqProducer.setNamesrvAddr("10.0.90.86:9876");
        // 消息最大长度 默认4M
        mqProducer.setMaxMessageSize(4096);
        // 发送消息超时时间，默认3000
        mqProducer.setSendMsgTimeout(3000);
        // 发送消息失败重试次数，默认2
        mqProducer.setRetryTimesWhenSendAsyncFailed(2);
        // 启动消息生产者
        mqProducer.start();
 
        // 创建消息，并指定Topic(主题)，Tag(标签)和消息内容
        Message message = new Message("DelayTopic", "", "hello, 这是延迟消息".getBytes(RemotingHelper.DEFAULT_CHARSET));
 
        // 设置延时等级为3, 所以这个消息将在10s之后发送, RocketMQ目前只支持固定的几个延时时间，还不支持自定义延迟时间
        message.setDelayTimeLevel(3);
 
        // 发送同步消息到一个Broker，可以通过sendResult返回消息是否成功送达
        SendResult sendResult = mqProducer.send(message);
        System.out.println(sendResult);
 
        // 如果不再发送消息，关闭Producer实例
        mqProducer.shutdown();
    }
}
```

- (3)、启动Producer

```bash
SendResult [sendStatus=SEND_OK, msgId=AC6E005A51B018B4AAC278E9F6F70000, offsetMsgId=0A005A5600002A9F0000000000003465, messageQueue=MessageQueue [topic=DelayTopic, brokerName=broker-a, queueId=0], queueOffset=3]
```

从控制台可以看到，消息发送状态为SEND_OK，说明延迟消息已经成功发送到RocketMQ Broker中。

- (4)、观察Consumer是否接收到消息

```bash
消费者接收到消息: MessageExt [brokerName=broker-a, queueId=0, storeSize=241, queueOffset=1, sysFlag=0, bornTimestamp=1645673399032, bornHost=/10.0.90.115:62807, storeTimestamp=1645673403365, storeHost=/10.0.90.86:10911, msgId=0A005A5600002A9F000000000000355F, commitLogOffset=13663, bodyCRC=676533924, reconsumeTimes=0, preparedTransactionOffset=0, toString()=Message{topic='DelayTopic', flag=0, properties={MIN_OFFSET=0, REAL_TOPIC=DelayTopic, MAX_OFFSET=2, CONSUME_START_TIME=1645673409075, UNIQ_KEY=AC6E005A51B018B4AAC278E9F6F70000, CLUSTER=DefaultCluster, DELAY=3, WAIT=true, REAL_QID=0}, body=[104, 101, 108, 108, 111, 44, 32, -24, -65, -103, -26, -104, -81, -27, -69, -74, -24, -65, -97, -26, -74, -120, -26, -127, -81], transactionId='null'}]---消息内容为：hello, 这是延迟消息消息被消费时间：Thu Feb 24 11:30:09 CST 2022, 消息存储时间: Thu Feb 24 11:29:59 CST 2022

```

可以看到，延迟消息成功被消息，并且我们注意到，消息被Consumer消费的时间【Thu Feb 24 11:30:09 CST 2022】 - 消息存储时间【Thu Feb 24 11:29:59 CST 2022】 = 10s，发送消息的时候，指定的延迟等级也是10s，也就是消息的消费比存储时间晚10秒。

### 五、延时消息实现原理

RocketMQ延时消息会暂存在名为**SCHEDULE_TOPIC_XXXX**的Topic中，并根据delayTimeLevel存入特定的queue，***\*queueId = delayTimeLevel – 1\****，即**一个queue只存相同延迟的消息**，保证具有相同发送延迟的消息能够顺序消费。broker会调度地消费SCHEDULE_TOPIC_XXXX，将消息写入真实的topic。

> ***\*SCHEDULE_TOPIC_XXXX中consumequeue中的文件夹名称就是队列的名称，并且【队列名称 = 延迟等级 - 1】；如下图，在前面的例子中，我们执定消息的延迟时间为10s，对应的延迟等级是3，所以文件夹名称为【3 - 1 = 2】\****

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/e4e1aea0642e2bf09ba86436397b8ac9-20240401210719095.png)

延迟消息在RocketMQ Broker端的流转如下图所示：

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/07f0222bc1914a066c9b6c56d750374d-20240401213232528.png)

主要包含以下6个步骤：

- **(1)、修改消息Topic名称和队列信息**

RocketMQ Broker端在存储生产者写入的消息时，首先都会将其写入到CommitLog中。之后根据消息中的Topic信息和队列信息，将其转发到目标Topic的指定队列(ConsumeQueue)中。

由于消息一旦存储到ConsumeQueue中，消费者就能消费到，而延迟消息不能被立即消费，所以这里将Topic的名称修改为SCHEDULE_TOPIC_XXXX，并根据延迟级别确定要投递到哪个队列下。同时，还会将消息原来要发送到的目标Topic和队列信息存储到消息的属性中。

- **(2)、转发消息到延迟主题SCHEDULE_TOPIC_XXXX的CosumeQueue中**

CommitLog中的消息转发到CosumeQueue中是异步进行的。在转发过程中，会对延迟消息进行特殊处理，主要是计算这条延迟消息需要在什么时候进行投递。

> **投递时间 = 消息存储时间(storeTimestamp) + 延迟级别对应的时间**

需要注意的是，会将计算出的投递时间当做消息Tag的哈希值存储到CosumeQueue中，CosumeQueue单个存储单元组成结构如下图所示：
![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5q-P5aSp6YO96KaB6L-b5q2l5LiA54K554K5,size_9,color_FFFFFF,t_70,g_se,x_16-20240401210719391.png)



其中：

1. Commit Log Offset：记录在CommitLog中的位置；
2. Size：记录消息的大小；
3. Message Tag HashCode：记录消息Tag的哈希值，用于消息过滤。特别的，***\*对于延迟消息，这个字段记录的是消息的投递时间戳。\****这也是为什么java中hashCode方法返回一个int型，只占用4个字节，而这里Message Tag HashCode字段却设计成8个字节的原因；

- **(3)、延迟服务消费SCHEDULE_TOPIC_XXXX消息**

Broker内部有一个ScheduleMessageService类，其充当延迟服务，主要是消费SCHEDULE_TOPIC_XXXX中的消息，并投递到目标Topic中。

ScheduleMessageService在启动时，其会创建一个定时器Timer，并根据延迟级别的个数，启动对应数量的TimerTask，每个TimerTask负责一个延迟级别的消费与投递。

> **需要注意的是，每个TimeTask在检查消息是否到期时，首先检查对应队列中尚未投递第一条消息，如果这条消息没到期，那么之后的消息都不会检查。如果到期了，则进行投递，并检查之后的消息是否到期。**

- **(4)、将信息重新存储到CommitLog中**

在将消息到期后，需要投递到目标Topic。由于在第一步已经记录了原来的Topic和队列信息，因此这里重新设置，再存储到CommitLog即可。此外，由于之前Message Tag HashCode字段存储的是消息的投递时间，这里需要重新计算tag的哈希值后再存储。

- **(5)、将消息投递到目标Topic中**

这一步与第二步类似，不过由于消息的Topic名称已经改为了目标Topic。因此消息会直接投递到目标Topic的ConsumeQueue中，之后消费者即消费到这条消息。

- **(6)、消费者消费目标topic中的数据。**