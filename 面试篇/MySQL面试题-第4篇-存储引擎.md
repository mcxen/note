### MySQL面试题 - 第4篇 - 存储引擎

### MySQL常见的三种存储引擎（InnoDB、MyISAM、MEMORY）的区别?

**两种存储引擎的大致区别表现在：**

- InnoDB支持事务，MyISAM不支持， 这一点是非常之重要。事务是一种高级的处理方式，如在一些列增删改中只要哪个出错还可以回滚还原，而MyISAM就不可以了。

- MyISAM适合查询以及插入为主的应用。

- InnoDB适合频繁修改以及涉及到安全性较高的应用。

- InnoDB支持外键，MyISAM不支持。

- 从MySQL5.5.5以后，InnoDB是默认引擎。

- InnoDB不支持FULLTEXT类型的索引。

- InnoDB中不保存表的行数，如select count() from table时，InnoDB需要扫描一遍整个表来计算有多少行，但是MyISAM只要简单的读出保存好的行数即可。注意的是，当count()语句包含where条件时MyISAM也需要扫描整个表。

- 对于自增长的字段，InnoDB中必须包含只有该字段的索引，但是在MyISAM表中可以和其他字段一起建立联合索引。

- DELETE FROM table时，InnoDB不会重新建立表，而是一行一行的 删除，效率非常慢。MyISAM则会重建表。

- InnoDB支持行锁（某些情况下还是锁整表，如 update table set a=1 where user like '%lee%'。

### MySQL存储引擎MyISAM与InnoDB如何选择

- MySQL有多种存储引擎，每种存储引擎有各自的优缺点，可以择优选择使用：MyISAM、InnoDB、MERGE、MEMORY(HEAP)、BDB(BerkeleyDB)、EXAMPLE、FEDERATED、ARCHIVE、CSV、BLACKHOLE。

- 虽然MySQL里的存储引擎不只是MyISAM与InnoDB这两个，但常用的就是两个。
  关于MySQL数据库提供的两种存储引擎，MyISAM与InnoDB选择使用：

  - INNODB会支持一些关系数据库的高级功能，如事务功能和行级锁，MyISAM不支持。

  - MyISAM的性能更优，占用的存储空间少，所以，选择何种存储引擎，视具体应用而定。

- 如果你的应用程序一定要使用事务，毫无疑问你要选择INNODB引擎。但要注意，INNODB的行级锁是有条件的。在where条件没有使用主键时，照样会锁全表。比如DELETE FROM mytable这样的删除语句。

- 如果你的应用程序对查询性能要求较高，就要使用MyISAM了。MyISAM索引和数据是分开的，而且其索引是压缩的，可以更好地利用内存。所以它的查询性能明显优于INNODB。压缩后的索引也能节约一些磁盘空间。MyISAM拥有全文索引的功能，这可以极大地优化LIKE查询的效率。

- 有人说MyISAM只能用于小型应用，其实这只是一种偏见。如果数据量比较大，这是需要通过升级架构来解决，比如分表分库，而不是单纯地依赖存储引擎。

- 现在一般都是选用innodb了，主要是MyISAM的全表锁，读写串行问题，并发效率锁表，效率低，MyISAM对于读写密集型应用一般是不会去选用的。

**MEMORY存储引擎**

- MEMORY是MySQL中一类特殊的存储引擎。它使用存储在内存中的内容来创建表，而且数据全部放在内存中。这些特性与前面的两个很不同。

- 每个基于MEMORY存储引擎的表实际对应一个磁盘文件。该文件的文件名与表名相同，类型为frm类型。该文件中只存储表的结构。而其数据文件，都是存储在内存中，这样有利于数据的快速处理，提高整个表的效率。

- 值得注意的是，服务器需要有足够的内存来维持MEMORY存储引擎的表的使用。如果不需要了，可以释放内存，甚至删除不需要的表。

- MEMORY默认使用哈希索引。速度比使用B型树索引快。当然如果你想用B型树索引，可以在创建索引时指定。

- 注意，MEMORY用到的很少，因为它是把数据存到内存中，如果内存出现异常就会影响数据。如果重启或者关机，所有数据都会消失。因此，基于MEMORY的表的生命周期很短，一般是一次性的。

### MySQL的MyISAM与InnoDB两种存储引擎在，事务、锁级别，各自的适用场景?

**事务处理上方面**

- **MyISAM：**强调的是性能，每次查询具有原子性,其执行数度比InnoDB类型更快，但是不提供事务支持。

- **InnoDB：**提供事务支持事务，外部键等高级数据库功能。具有事务(commit)、回滚(rollback)和崩溃修复能力(crash recovery capabilities)的事务安全(transaction-safe (ACID compliant))型表。

**锁级别**

- **MyISAM：**只支持表级锁，用户在操作MyISAM表时，select，update，delete，insert语句都会给表自动加锁，如果加锁以后的表满足insert并发的情况下，可以在表的尾部插入新的数据。

- **InnoDB：**支持事务和行级锁，是innodb的最大特色。行锁大幅度提高了多用户并发操作的新能。但是InnoDB的行锁，只是在WHERE的主键是有效的，非主键的WHERE都会锁全表的。

- 关于存储引擎MyISAM和InnoDB的其他参考资料如下：
  - *http://blog.csdn.net/lc0817/article/details/52757194*
  - *https://www.cnblogs.com/kevingrace/p/5685355.html*

