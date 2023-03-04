### MySQL面试题 - 第5篇 - 优化

#### 查询语句不同元素（where、jion、limit、group by、having等等）执行先后顺序?

**查询中用到的关键词主要包含六个，并且他们的顺序依次为 select、from、where、group by、having、order by**

- 其中select和from是必须的，其他关键词是可选的，这六个关键词的执行顺序 与sql语句的书写顺序并不是一样的，而是按照下面的顺序来执行

- **from:**需要从哪个数据表检索数据
- **where:**过滤表中数据的条件
- **group by:**如何将上面过滤出的数据分组
- **having:**对上面已经分组的数据进行过滤的条件
- **select:**查看结果集中的哪个列，或列的计算结果
- **order by :**按照什么样的顺序来查看返回的数据

**from后面的表关联，是自右向左解析 而where条件的解析顺序是自下而上的。**

- 也就是说，在写SQL语句的时候，尽量把数据量小的表放在最右边来进行关联（用小表去匹配大表），而把能筛选出小量数据的条件放在where语句的最左边 （用小表去匹配大表）

- 其他参考资源：*http://www.cnblogs.com/huminxxl/p/3149097.html*

#### 使用explain优化sql和索引?

- 对于复杂、效率低的sql语句，我们通常是使用explain sql 来分析sql语句，这个语句可以打印出，语句的执行。这样方便我们分析，进行优化

- **table：**显示这一行的数据是关于哪张表的
- **type：**这是重要的列，显示连接使用了何种类型。从最好到最差的连接类型为const、eq_reg、ref、range、index和ALL
- **all：full table scan ;MySQL将遍历全表以找到匹配的行；**
- **index:** index scan; index 和 all的区别在于index类型只遍历索引；
- **range：**索引范围扫描，对索引的扫描开始于某一点，返回匹配值的行，常见与between ，等查询；
- **ref：**非唯一性索引扫描，返回匹配某个单独值的所有行，常见于使用非唯一索引即唯一索引的非唯一前缀进行查找；
- **eq_ref：**唯一性索引扫描，对于每个索引键，表中只有一条记录与之匹配，常用于主键或者唯一索引扫描；
- **const，system：**当MySQL对某查询某部分进行优化，并转为一个常量时，使用这些访问类型。如果将主键置于where列表中，MySQL就能将该查询转化为一个常量。
- **possible_keys：**显示可能应用在这张表中的索引。如果为空，没有可能的索引。可以为相关的域从WHERE语句中选择一个合适的语句
- **key：**实际使用的索引。如果为NULL，则没有使用索引。很少的情况下，MySQL会选择优化不足的索引。这种情况下，可以在SELECT语句中使用USE INDEX（indexname）来强制使用一个索引或者用IGNORE INDEX（indexname）来强制MySQL忽略索引
- **key_len：**使用的索引的长度。在不损失精确性的情况下，长度越短越好
- **ref：**显示索引的哪一列被使用了，如果可能的话，是一个常数
- **rows：**MySQL认为必须检查的用来返回请求数据的行数
- **Extra：**关于MySQL如何解析查询的额外信息。

#### MySQL慢查询怎么解决?

- **slow_query_log** 慢查询开启状态。

- **slow_query_log_file** 慢查询日志存放的位置（这个目录需要MySQL的运行帐号的可写权限，一般设置为MySQL的数据存放目录）。

- **long_query_time** 查询超过多少秒才记录。

