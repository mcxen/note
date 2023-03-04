### MySQL - 基础篇 - 第6篇 - 函数、存储过程和触发器

#### 函数Function

- MySQL 8.x 默认不允许创建函数

```mysql
set global log_bin_trust_function_creators=TRUE;
```

```mysql
-- delimiter 是用来定义MySQL的分行，因为MySQL中默认是遇到 ';' 就换行
-- delimiter // 后面的 '//' 是可以改变的 如 delimiter &&
delimiter //
create function 函数名([形参列表]) returns 数据类型
begin 
	函数体 - 最后一定要有 return + 返回的结果
	-- return 10;
end //
delimiter ;
```

```mysql
-- 传入两个变量，返回这个两个变量的和
delimiter //
create function adds(a int,b int) returns int
begin
	return a+b;
end //
delimiter ;
-- 函数调用
select adds(1,2);
```

```mysql
mysql> delimiter //
mysql> create function adds(a int,b int) returns int
    -> begin
    -> return a+b;
    -> end //
Query OK, 0 rows affected (0.24 sec)

mysql> delimiter ;
mysql> select adds(1,2);
+-----------+
| adds(1,2) |
+-----------+
|         3 |
+-----------+
1 row in set (0.14 sec)
```

```mysql
-- 查看自定义的函数的创建的语法
show create function 函数名称;

-- 删除函数
drop function 函数名;
```

##### 全局变量

```mysql
-- 求出 1 ~ X之间的和
drop function if exists my_sum;
delimiter //
create function my_sum(x int) returns int
begin
	-- 定义一个全局变量
	set @i = 1;
	set @sums = 0;
	while @i<=x do
		-- 循环体
		set @sums = @sums + @i;
		-- 变量自增
		set @i = @i + 1;
	end while;
	return @sums;
end //
delimiter;
-- 调用
select my_sum(100); 
//
-- 访问全局变量 @sums
select @sums;
// 
```

##### 局部变量

```mysql
-- 1 ~ 100 之间的和，但是不包括5的倍数
drop function is exists my_adds;
delimiter //
create function my_adds() returns i;
begin 
	-- 定义一个一个局部变量
	declare i int default 1;
	declare sums int default 0;
	success: while i<=100 do
		-- 循环体
		if i%5 = 0 then
		set i = i + 1
		-- 继续迭代success标签修饰的while循环 - 等同于 continue语句
			iterate success;
		end if
		set sums = sums + i;
		set i = i + 1;
	end while ;
	return sums;
end //
delimiter;
-- 局部变量只能在函数体中访问
```

#### 存储过程

- 客户端（Java程序） - 发送SQL到MySQL服务器 - MySQL-Server端对SQL语句进行编译 - 解释得到SQL的执行结果 - 响应发给MySQL-Client
- 传统方式 - 每次想要MySQL-Client端执行SQL语句，都是需要经过 **连接DB**，经过 **编译** 处理，得到结果的过程
- 存储过程（procedure） - 为了完成一些特定的功能，提前将 **SQL预编译好**，在MySQL-Server端系统的执行计划表中，
- 第一次取调用存储过程的时候，对SQL进行预编译，并且进行保存，第二次调用的时候，省去了编译SQL的过程了，也就是可以做到 **标准的组件编程（封装SQL语句）**

##### 面试题

- 函数和存储过程的区别
  - 存储过程的签名上面不需要使用returns语句，存储过程可以没有返回值
  - 存储过程体中可以不需要使用return语句
  - 函数是使用select语句调用，存储过程时使用call调用
  - 存储过程的形参中可以使用in/out表示参数是输入/输出

##### 语法体验

```mysql
-- 删除存储过程
drop procedure 存储过程名;
-- 创建存储过程
delimiter //
create procedure 存储过程名([out/in 变量 数据类型])
begin 
	-- 过程体
end //
delimiter;
```

```mysql
-- 体验 - 把s_emp中的员工表的平均薪资的SQL进行预编译好放在 MySQL-Server端执行计划表
drop procedure if exists s_emp_avg_pd;
delimiter //
create procedure s_emp_avg_pd()
begin 
	select avg(salary) from s_emp;
end //
delimiter;
-- 第一次调用存储过程，MySQL-Server端就会对存储过程中的过程体中的SQL语句进行预编译并且保存
call s_emp_avg_pd();//

-- 第二次调用的时候，省去了对SQL编译的过程，从而提升了查询的效率，复用了SQL语句
call s_emp_avg_pd();//
```

##### 输入/输出

- in - 存储过程调用者向存储过程中传入的数据
- out - 存储过程需要向外返回的数据
- inout - 输入输出

###### 输入in

```mysql
drop procedure if exists my_in_pd;
delimiter //
create procedure my_in_pd(in p_in int)
begin 
	-- p_in 是一个局部变量
	-- 获取这个参数
	-- p_in是使用in来修饰的，p_in 可以用来存储调用者传入进来的数据
	select p_in;
	-- 对这个p_in重新赋值
	set p_in = 2;
	-- 输出
	select p_in;
	-- 设置全局变量
	set @p_ins = 2;
end //
delimiter;

-- 如果使用in来进行修饰，传入值
-- 传入一个字面量
call my_in_pd(1);
//
-- 
mysql> call my_in_pd(1);
    -> //
+------+
| p_in |
+------+
|    1 |
+------+
1 row in set (0.00 sec)

+------+
| p_in |
+------+
|    2 |
+------+
1 row in set (0.01 sec)

Query OK, 0 rows affected (0.02 sec)

-- 直接传入全局变量
set @result  = 1;
call my_in_pd(@result);
-- 结果仍然是1
select @result;

```

###### 输出out

```mysql
drop procedure if exists my_out_pd;
delimiter //
create procedure my_out_pd(out p_out int)
begin
	-- out 变量 return - 变量 -- 返回存储过程执行之后的结果
	-- p_out是null - out 修饰的 - 不会接受数据，只会返回数据
	select p_out;
	-- 对这个p_out重新赋值
	set p_out = 2;
	-- 输出一下2
	select p_out;
end //
delimiter;

-- 如果参数是out修饰的，那么是不能传入一个字面量的
call my_out_pd(1);
-- ERROR 1414 (42000): OUT or INOUT argument 1 for routine ic_db.my_out_pd is not a variable or NEW pseudo-variable in BEFORE trigger

-- 必须传入一个全局变量
set @results = 1;
call my_out_pd(@results);

-- out 会把@results对应存储过程中的变量结果对外输出
select @results;

mysql> set @results = 1;
    -> call my_out_pd(@results);
    -> //
Query OK, 0 rows affected (0.00 sec)

+-------+
| p_out |
+-------+
|  NULL |
+-------+
1 row in set (0.00 sec)

+-------+
| p_out |
+-------+
|     2 |
+-------+
1 row in set (0.11 sec)

Query OK, 0 rows affected (0.12 sec)

mysql> select @results;
    -> //
+----------+
| @results |
+----------+
|        2 |
+----------+
1 row in set (0.00 sec)
```

###### 练习

- 创建一个存储过程，根据员工的id来得到员工的 first_name 和 salary

```mysql
drop procedure if exists s_emp_pd;
delimiter //
-- 存储过程签名中的变量不要和表中的列的名称冲突
-- 结合真实操作的时候，一定需要加上数据类型长度
create procedure s_emp_pd(in eid int,out fname varchar(25),out sal float(11,2))
begin 
	select first_name into fname from s_emp where id = eid;
	select salary into sal from s_emp where id = eid;
end //
delimiter ;

-- 调用存储过程
call s_emp_pd(1,@fname,@sal);
select @fname;
select @sal;
```

```mysql
drop procedure if exists s_emp_pd;
delimiter //
-- 存储过程签名中的变量不要和表中的列的名称冲突
-- 结合真实操作的时候，一定需要加上数据类型长度
create procedure s_emp_pd(in eid int,out fname varchar(25),out sal float(11,2))
begin 
	select first_name into fname,salary into sal from s_emp where id = eid;
end //
delimiter ;

-- 调用存储过程
call s_emp_pd(1,@fname,@sal);
select @fname;
select @sal;
```

##### 处理返回结果集

- 找出工资大于1200的员工的id，first_name，salary

```mysql
-- 解决方案有2中
-- 1.已经弃用 - 游标操作 - 性能很低
-- 2.创建第三张表进行存储返回的结果集

-- 业务 - 查询出来的列和原表中的列一致
create table s_emp_result as select id,first_name,salary from s_emp where 1=2;
-- 创建存储过程
drop procedure if exists s_emps_pd;
delimiter //
create procedure s_emps_pd(in sal float(11,2))
begin 
	insert into s_emp_result(id,first_name,salary) select id,first_name,salary
	from s_emp
	where salary > sal;
end //
delimiter;

call s_emps_pd(1200);
```

##### 条件语句

- if...then...else...end if;

```mysql
drop procedure if exists if_pd;
delimiter //
create procedure if_pd(in x int)
begin 
	-- if 单独使用
	if x=1 then
		select 1;
	end if;
	
	-- if...else 语句使用，美哟 else if !!!
	if x=2 then 
		select 2;
	else
		select 4;
	end if;
end //
delimiter;

-- 调用
call if_pd(1);
```

- case ... when ... then ... else ... end case

```mysql
drop procedure if exists case_pd;
delimiter //
create procedure case_pd(in x int,out res int)
begin
	declare param int default 0;
	set param  = x+1;
	case param
		when 2 then
			set res = 20;
		when 3 then 
			set res = 30;
		else 
			set res = 50;
	end case;
end //
delimiter;

call case_pd(10,@res);
select @res;
```

##### 循环语句

- while ... do ... end while

```mysql
drop procedure if exists while_pd;
delimiter //
create procedure while_pd(in x int)
begin 
	declare sums int default 0;
	declare i int default 1;
	while i<=x do
		if i%2 = 0 then 
			set sums = sums + i;
			set i = i + 1;
		end if;
		set i = i + 1;
	end while;
	select sums;
end //
delimiter ;

call while_pd(100);	
```

- loop ... end loop  等同于while (true)

```mysql
drop procedure if exists loop_pd;
delimiter //
create procedure loop_pd(in x int)
begin
	declare sums int default 0;
	declare i int default 1;
	success:
	loop
		set sums = sums + i;
		set i = i + 1;
		if i = 101 then 
			-- 打破循环
			leave success;
		end if;
	end loop;
	select sums;
end //
delimiter;

call loop_pd(100);
```

- repeat ... until ... end repeat 等同于 do... while

```mysql
drop procedure if exists repeat_pd;
delimiter //
create procedure repeat_pd(in x int)
begin 
	repeat
		-- 先进入循环中执行一次，然后再进行判断
		set x = x + 1;
		select x;
	-- 后面非有分号
	until x>0
	end repeat;
end //
delimiter;

call repeat_pd(0);
```

- iterater 相当于 continue

##### 带事务

```mysql
-- 存取钱
drop procedure is exists acc_pd;
delimiter //
create procedure acc_pd(in sid int,in tid int,int money double(7,2),in st int,out msg varchar(25))
begin
	-- 手动开启一个事务
	start transaction;
	update account set balance = balance  - money where id = sid;
	-- 模拟一个错误
	if st=1 then
		-- 失败的
	set msg = '发生错误了';
		-- 事务的回滚操作
		rollback;
	else 
		update accout set balance  = balance + money where id = tid;
	set msg = '操作成功';
	-- 手动提交一个事务
	commit;
	end if;
	select msg;
end //
delimiter;

-- 能够commit
call acc_pd(1,2,100,0,@msg);

-- 失败 - rollback
call acc_pd(1,2,100,1,@msg);
```

#### 触发器

- 在MySQL中，当我们执行一些操作的时候，比如DML操作（触发器触发的操作），一旦事件被触发，那么就会执行一段程序，**触发器本身就是一个特殊的存储过程**

**分类**

- after 触发器：在触发条件只会执行
- before 触发器：在触发条件之前执行

**语法**

```mysql
-- 删除触发器
drop trigger if exists 触发器名称;
delimiter //
-- 创建触发器
create trigger 触发器名
触发时机(after,before) 触发事件 (insert,delete,update) on 触发器所在的表名
for each row
-- 触发器需要执行的逻辑
befin 

end //
delimiter;
-- 触发时机的判断，触发器逻辑语句在另一个执行的语句之后执行，使用after，否则就是before
```

