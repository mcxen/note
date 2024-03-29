### MySQL面试题 - 第2篇 - 索引

#### 什么是索引?

- 数据库索引是数据库管理系统中的一个排序的数据结构，索引的实现通常使用B树及其变种B+树。在数据之外，数据库系统还维护着满足特定查找算法的数据结构，这些数据结构以某种方式引用（指向）数据，这样就可以在这些数据结构上实现高级查找算法。这种数据结构，就是索引。

#### 索引的作用？它的优点缺点是什么？

**索引作用：**

- 协助快速查询、更新数据库表中数据。

**为表设置索引要付出代价的：**

- 一是增加了数据库的存储空间

- 二是在插入和修改数据时要花费较多的时间(因为索引也要随之变动)。

#### 索引的优缺点？

**创建索引可以大大提高系统的性能（优点）：**

- 通过创建唯一性索引，可以保证数据库表中每一行数据的唯一性。

- 可以大大加快数据的检索速度，这也是创建索引的最主要的原因。

- 可以加速表和表之间的连接，特别是在实现数据的参考完整性方面特别有意义。

- 在使用分组和排序子句进行数据检索时，同样可以显著减少查询中分组和排序的时间。

- 通过使用索引，可以在查询的过程中，使用优化隐藏器，提高系统的性能。

**增加索引也有许多不利的方面(缺点)：**

- 创建索引和维护索引要耗费时间，这种时间随着数据量的增加而增加。

- 索引需要占物理空间，除了数据表占数据空间之外，每一个索引还要占一定的物理空间，如果要建立聚簇索引，那么需要的空间就会更大。

- 当对表中的数据进行增加、删除和修改的时候，索引也要动态的维护，这样就降低了数据的维护速度。

#### 哪些列适合建立索引、哪些不适合建索引？

- 索引是建立在数据库表中的某些列的上面。在创建索引的时候，应该考虑在哪些列上可以创建索引，在哪些列上不能创建索引。

**一般来说，应该在这些列上创建索引：**

- 在经常需要搜索的列上，可以加快搜索的速度；

- 在作为主键的列上，强制该列的唯一性和组织表中数据的排列结构；

- 在经常用在连接的列上，这些列主要是一些外键，可以加快连接的速度；

- 在经常需要根据范围进行搜索的列上创建索引，因为索引已经排序，其指定的范围是连续的；

- 在经常需要排序的列上创建索引，因为索引已经排序，这样查询可以利用索引的排序，加快排序查询时间；

- 在经常使用在WHERE子句中的列上面创建索引，加快条件的判断速度。

**对于有些列不应该创建索引：**

- **对于那些在查询中很少使用或者参考的列不应该创建索引。**
  - 这是因为，既然这些列很少使用到，因此有索引或者无索引，并不能提高查询速度。相反，由于增加了索引，反而降低了系统的维护速度和增大了空间需求。

- **对于那些只有很少数据值的列也不应该增加索引。**
  - 这是因为，由于这些列的取值很少，例如人事表的性别列，在查询的结果中，结果集的数据行占了表中数据行的很大比例，即需要在表中搜索的数据行的比例很大。增加索引，并不能明显加快检索速度。

- **对于那些定义为text, image和bit数据类型的列不应该增加索引。**
  - 这是因为，这些列的数据量要么相当大，要么取值很少。

- **当修改性能远远大于检索性能时，不应该创建索引。**

- 这是因为，修改性能和检索性能是互相矛盾的。当增加索引时，会提高检索性能，但是会降低修改性能。当减少索引时，会提高修改性能，降低检索性能。因此，当修改性能远远大于检索性能时，不应该创建索引。

#### 什么样的字段适合建索引

- 唯一、不为空、经常被查询的字段

#### MySQL B+Tree索引和Hash索引的区别?

**Hash索引和B+树索引的特点：**

- Hash索引结构的特殊性，其检索效率非常高，索引的检索可以一次定位;

- B+树索引需要从根节点到枝节点，最后才能访问到页节点这样多次的IO访问;

#### **为什么不都用Hash索引而使用B+树索引？**

- Hash索引仅仅能满足"=","IN"和""查询，不能使用范围查询,因为经过相应的Hash算法处理之后的Hash值的大小关系，并不能保证和Hash运算前完全一样；

- Hash索引无法被用来避免数据的排序操作，因为Hash值的大小关系并不一定和Hash运算前的键值完全一样；

- Hash索引不能利用部分索引键查询，对于组合索引，Hash索引在计算Hash值的时候是组合索引键合并后再一起计算Hash值，而不是单独计算Hash值，所以通过组合索引的前面一个或几个索引键进行查询的时候，Hash索引也无法被利用；

- Hash索引在任何时候都不能避免表扫描，由于不同索引键存在相同Hash值，所以即使取满足某个Hash键值的数据的记录条数，也无法从Hash索引中直接完成查询，还是要回表查询数据；

- Hash索引遇到大量Hash值相等的情况后性能并不一定就会比B+树索引高。

**补充：**

- MySQL中，只有HEAP/MEMORY引擎才显示支持Hash索引。

- 常用的InnoDB引擎中默认使用的是B+树索引，它会实时监控表上索引的使用情况，如果认为建立哈希索引可以提高查询效率，则自动在内存中的“自适应哈希索引缓冲区”建立哈希索引（在InnoDB中默认开启自适应哈希索引），通过观察搜索模式，MySQL会利用index key的前缀建立哈希索引，如果一个表几乎大部分都在缓冲池中，那么建立一个哈希索引能够加快等值查询。

**B+树索引和哈希索引的明显区别是：**

- 如果是等值查询，那么哈希索引明显有绝对优势，因为只需要经过一次算法即可找到相应的键值；当然了，这个前提是，键值都是唯一的。如果键值不是唯一的，就需要先找到该键所在位置，然后再根据链表往后扫描，直到找到相应的数据；

- 如果是范围查询检索，这时候哈希索引就毫无用武之地了，因为原先是有序的键值，经过哈希算法后，有可能变成不连续的了，就没办法再利用索引完成范围查询检索；
  同理，哈希索引没办法利用索引完成排序，以及like ‘xxx%’ 这样的部分模糊查询（这种部分模糊查询，其实本质上也是范围查询）；

- 哈希索引也不支持多列联合索引的最左匹配规则；

- B+树索引的关键字检索效率比较平均，不像B树那样波动幅度大，在有大量重复键值情况下，哈希索引的效率也是极低的，因为存在所谓的哈希碰撞问题。

- 在大多数场景下，都会有范围查询、排序、分组等查询特征，用B+树索引就可以了。

#### B树和B+树的区别

- B树，每个节点都存储key和data，所有节点组成这棵树，并且叶子节点指针为nul，叶子结点不包含任何关键字信息。
- B+树，所有的叶子结点中包含了全部关键字的信息，及指向含有这些关键字记录的指针，且叶子结点本身依关键字的大小自小而大的顺序链接，所有的非终端结点可以看成是索引部分，结点中仅含有其子树根结点中最大（或最小）关键字。(而B 树的非终节点也包含需要查找的有效信息)

#### 为什么说B+比B树更适合实际应用中操作系统的文件索引和数据库索引？

- **B+的磁盘读写代价更低**

- B+的内部结点并没有指向关键字具体信息的指针。因此其内部结点相对B树更小。如果把所有同一内部结点的关键字存放在同一盘块中，那么盘块所能容纳的关键字数量也越多。一次性读入内存中的需要查找的关键字也就越多。相对来说IO读写次数也就降低了。

- **B+tree的查询效率更加稳定**

- 由于非终结点并不是最终指向文件内容的结点，而只是叶子结点中关键字的索引。所以任何关键字的查找必须走一条从根结点到叶子结点的路。所有关键字查询的路径长度相同，导致每一个数据的查询效率相当。

#### 聚集索引和非聚集索引区别?

**聚合索引(clustered index):**

- 聚集索引表记录的排列顺序和索引的排列顺序一致，所以查询效率快，只要找到第一个索引值记录，其余就连续性的记录在物理也一样连续存放。聚集索引对应的缺点就是修改慢，因为为了保证表中记录的物理和索引顺序一致，在记录插入的时候，会对数据页重新排序。

- 聚集索引类似于新华字典中用拼音去查找汉字，拼音检索表于书记顺序都是按照a~z排列的，就像相同的逻辑顺序于物理顺序一样，当你需要查找a,ai两个读音的字，或是想一次寻找多个傻(sha)的同音字时，也许向后翻几页，或紧接着下一行就得到结果了。

**非聚合索引(nonclustered index):**

- 非聚集索引指定了表中记录的逻辑顺序，但是记录的物理和索引不一定一致，两种索引都采用B+树结构，非聚集索引的叶子层并不和实际数据页相重叠，而采用叶子层包含一个指向表中的记录在数据页中的指针方式。非聚集索引层次多，不会造成数据重排。

- 非聚集索引类似在新华字典上通过偏旁部首来查询汉字，检索表也许是按照横、竖、撇来排列的，但是由于正文中是a~z的拼音顺序，所以就类似于逻辑地址于物理地址的不对应。同时适用的情况就在于分组，大数目的不同值，频繁更新的列中，这些情况即不适合聚集索引。

**根本区别：**

- 聚集索引和非聚集索引的根本区别是表记录的排列顺序和与索引的排列顺序是否一致。

#### 索引失效的情况

- 引用type从优到差：System-->**const-->eq_ref-->ref-->ref_or_null-->index_merge-->unique_subquery-->index_subquery-->**range-->index-->all(全表扫描的意思)

```mysql
drop table index_test;
create table index_test(
    id int(7) primary key,
    a int(7),
    b int(7),
    c varchar(20),
    d varchar(20)
);
insert into index_test values(1,100,10,'aaa','A');
insert into index_test values(2,300,30,'aba','BB');
insert into index_test values(3,200,20,'caa','CC');
insert into index_test values(4,100,10,'daa','DD');
insert into index_test values(5,500,50,'aad','FF');

-- 举例：创建复合索引
create index index_test_abc on index_test(a,b,c);
```

##### 遵循最左原则

- 简介：**针对的是复合索引，**最左边的列一定要和创建符合索引的第一个列保持一致
- 符合索引(a,b,c) 必须要连续

```mysql
-- a,b,c都是走了索引的
explain select * from index_test where a = 100 and b = 10 and c='aaa';

-- a列走了索引,但是c列没有走索引,原因是跳过了复合索引的b列
explain select * from index_test where a=100 and c='aaa';

-- a列和b列都是走了索引的,并且它们是连续的.
explain select * from index_test where a=100 and b=10;

-- 最左原则
-- 索引完全失效,不符合最左匹配原则.where最左边的检索列不是复合索引的第一个列a

-- 原因同上
explain select * from index_test where b=10;//没有走索引

explain select * from index_test where a=100;//走了索引

explain select * from index_test where c='aaa';//没有走索引
```

##### 范围之后索引列也会失效

```mysql
-- a列和b列是走了索引的,但是c列没有走索引.因为c列是范围之后的判断
explain select * from index_test where a=100 and b>10 and c='aaa';
```

##### 模糊查询

```mysql
-- like '%'出现在末尾,仍然a,b,c都是走索引
explain select * from index_test where a=100 and b=10 and c like 'a%';

-- 只有a,b是走了索引的,c是没有走索引的
explain select * from index_test where a=100 and b=10 and c like '%a';

-- 只有a,b是走了索引的,c是没有走索引的
explain select * from index_test where a=100 and b=10 and c like '%a%';
```

##### 索引列使用函数

```mysql
-- 索引列套在函数中使用,将会导致索引失效
explain select * from index_test where abs(id)=1;

+----+-------------+------------+------------+------+---------------+------+---------+------+------+----------+-------------+
| id | select_type | table      | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+------------+------------+------+---------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | index_test | NULL       | ALL  | NULL          | NULL | NULL    | NULL |    5 |   100.00 | Using where |
+----+-------------+------------+------------+------+---------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)
```

##### 索引列添加了计算

-  运算如+，-，*，/等 
-  优化的话，要把运算放在值上，或者在应用程序中直接算好

```mysql
-- 索引失效
explain select * from index_test where id+5>7;

-- 应用场景:
explain select * from index_test where months>5*12;
```

##### 索引列参加运算符

```mysql
-- is null(走索引)和is not null(不走索引)
explain select * from s_emp where commission_pct is not null;
-- is not null是不支持索引的
-- in(走了索引)  not in(不走索引)
```

##### 利用索引列查询出来的数据超过整张表的30%.

##### where语句中包含or时，可能会导致索引失效

- 使用or并不是一定会使索引失效，你需要看or左右两边的查询列是否命中相同的索引。
- 假设USER表中的user_id列有索引，age列没有索引。
- 下面这条语句其实是命中索引的

```mysql
select * from `user` where user_id = 1 or user_id = 2;
```

-  但是这条语句是无法命中索引的。 

```mysql
select * from `user` where user_id = 1 or age = 20;
```

-  假设age列也有索引的话，依然是无法命中索引的。 

```mysql
select * from `user` where user_id = 1 or age = 20;
```

##### where语句中索引列使用了负向查询，可能会导致索引失效

-  负向查询包括：NOT、!=、<>、!<、!>、NOT IN、NOT LIKE等。 

-  其实负向查询并不绝对会索引失效，这要看MySQL优化器的判断，全表扫描或者走索引哪个成本低了。 

##### 隐式类型转换导致的索引失效 

-  比如下面语句中索引列user_id为varchar类型，不会命中索引： 

```mysql
select * from `user` where user_id = 12;
```

- 这是因为MySQL做了隐式类型转换，调用函数将user_id做了转换。 

```mysql
select * from `user` where CAST(user_id AS signed int) = 12;
```

##### 隐式字符编码转换导致的索引失效

- 当两个表之间做关联查询时，如果两个表中关联的字段字符编码不一致的话，MySQL可能会调用CONVERT函数，将不同的字符编码进行隐式转换从而达到统一。作用到关联的字段时，就会导致索引失效。
- 比如下面这个语句，其中d.tradeid字符编码为utf8，而l.tradeid的字符编码为utf8mb4。因为utf8mb4是utf8的超集，所以MySQL在做转换时会用CONVERT将utf8转为utf8mb4。简单来看就是CONVERT作用到了d.tradeid上，因此索引失效。

```mysql
select d.* from tradelog l , trade_detail d where d.tradeid=CONVERT(l.tradeid USING utf8) and l.id=2; 
```

#####  如果mysql估计使用全表扫描要比使用索引快,则不使用索引 

##### 在ORDER BY操作中，MYSQL只有在排序条件不是一个查询条件表达式的情况下才使用索引

- 尽管如此，在涉及多个数据表的查询里，即使有索引可用，那些索引在加快ORDER BY操作方面也没什么作用 

##### 在JOIN操作中（需要从多个数据表提取数据时）

- MYSQL只有在主键和外键的数据类型相同时才能使用索引，否则即使建立了索引也不会使用 

### 建立索引的策略

- 主键列和唯一性列						√

- 不经常发生改变的[在update列数据的数据的时候,也会更新索引文件]				√

- 满足以上2个条件,经常作为查询条件的列	√

- 重复值太多的列						×

- null值太多的列						×

- 禁止在更新十分频繁、区分度不高的属性上建立索引。
  - 更新会变更B+树，更新频繁的字段建立索引会大大降低数据库性能。
  - “性别”这种区分度不大的属性，建立索引是没有什么意义的，不能有效过滤数据，性能与全表扫描类似。
- 建立组合索引，必须把区分度高的字段放在前面。

