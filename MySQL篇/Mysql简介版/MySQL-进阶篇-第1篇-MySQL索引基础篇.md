### MySQL - 进阶篇 - 第1篇 - MySQL索引基础篇

> 简介：MySQL中不同的存储引擎的索引的实现方式是不同的

#### 索引的底层

**详情见`MySQL - 进阶篇 - 第1篇 - MySQL索引高级篇`**

- MyISAM
  - 有单独的索引文件，但是索引过多的话，索引文件会变大（将会占据很多空间）
  - 叶子节点只保存的是索引+物理行地址
  - 索引的本质：键值对（索引列值，物理地址）
  - 先判断查询是否走了索引，先查询索引文件，找到物理地址，再由地址直接直接定位到数据表
  - 索引是单独的文件
- InnoDB
  - **索引文件不是一个单独的文件，它和数据文件是合二为一的**
  - 索引和数据>数据文件中>聚簇索引

#### 索引算法

- B+Tree （索引的数据结构）
  - 聚簇索引 - MySQL会自动选择主键列座位聚簇索引列
    - 非叶子节点：聚簇索引列的值
    - 叶子节点：聚簇索引列值以及真实的数据
  - 非聚簇索引 
    - 非叶子节点：非聚簇索引列的值
    - 叶子节点：键值对（非聚簇索引列的值，主键值）

#### 优缺点

- 好处：加快了查询速度
- 坏处：降低了增删改的速度（insert、delete、update）增大了表的文件大小，（索引文件有时候甚至比数据文件还大）

#### 索引类型

- 普通索引（index）：仅仅是加快了查询速度
- **唯一索引（unique）**：行上的值不可以重复
- **主键索引（primary key）**：不能重复
- **全文索引（fulltext）：仅可用于MyISAM表**，针对较大的数据，生成全文索引很消耗空间
- 组合索引[覆盖索引]：为了更多的提高MySQL的效率可以建立组合索引，遵循 “最左前缀”原则

#### 索引语法

##### 创建索引总览

```mysql
create table table_name(
	[col_name data type]
    [unique|fulltext][index|key] [index_name](col_name[length]) [asc|desc]
)
```

- unique|fulltext 为可选参数，分别表示唯一索引，全文索引
- index和key为同义词，两者作用相同，用来指定创建索引
- col_name 为需要创建索引的字段列，该列必须从数据表中该定义字段的多个列中选择
- length 为可选参数，表示索引的长度，只有字符串类型的字段才能指定索引的长度
- asc或desc指定升序或降序的索引值存储

##### 索引的使用方式

- 查看某张表上所有的索引

```mysql
show index from table_name [\G] -- 如果是在cmd窗口，可以换行
```

- 建立索引

```mysql
-- 索引名字可以不写，不写默认使用列名
alter table 表名 add index/unique/fulltext 索引名(列名); 

create index 索引名 on 表名(列值)
```

- 删除索引

```mysql
alter table 表名 drop index 索引名字。
```

- 代码实例

```mysql
drop table if exists index_test;
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

-- 给a列单独建立一个索引：普通索引，非聚簇索引
create index index_test_a_index on index_test(a);
-- 查询index_test的索引
show index from index_test;
-- 删除索引
alter table index_test drop index index_test_a_index;
-- 创建一个组合索引：非聚簇索引
create index index_test_abc on index_test(a,b,c);

-- 给定一个索引长度 key_len 列的数据类型应该是字符串类型
create index index_test_a_index on index_test(d(1));
```

- ***alter table 表名 add primary key(列名) --不要加索引名，因为主键只有一个***
- 删除非主键索引

```mysql
alter table 表名 drop index 索引名;
```

- 删除主键索引

```mysql
alter table 表名 drop primary key;
```

##### 查看查询是否走了索引

```mysql
explain select * from index_test where id = 4;

mysql> explain select * from index_test where id = 4;
+----+-------------+------------+------------+-------+---------------+---------+---------+-------+------+----------+-------+
| id | select_type | table      | partitions | type  | possible_keys | key     | key_len | ref   | rows | filtered | Extra |
+----+-------------+------------+------------+-------+---------------+---------+---------+-------+------+----------+-------+
|  1 | SIMPLE      | index_test | NULL       | const | PRIMARY       | PRIMARY | 4       | const |    1 |   100.00 | NULL  |
+----+-------------+------------+------------+-------+---------------+---------+---------+-------+------+----------+-------+
1 row in set, 1 warning (0.00 sec)
```

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

#### 建立索引的策略

- 主键列和唯一性列						√

- 不经常发生改变的[在update列数据的数据的时候,也会更新索引文件]				√

- 满足以上2个条件,经常作为查询条件的列	√

- 重复值太多的列						×

- null值太多的列						×

- 禁止在更新十分频繁、区分度不高的属性上建立索引。
  - 更新会变更B+树，更新频繁的字段建立索引会大大降低数据库性能。
  - “性别”这种区分度不大的属性，建立索引是没有什么意义的，不能有效过滤数据，性能与全表扫描类似。
- 建立组合索引，必须把区分度高的字段放在前面。

