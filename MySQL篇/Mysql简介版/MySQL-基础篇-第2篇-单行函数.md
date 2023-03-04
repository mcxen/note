### MySQL - 基础篇 - 第2篇 - 单行函数

#### MySQL单行函数

> 100%不会用，了解即可

#### 字符串函数

- instr(str,substr) - 返回字符串substr在字符串str第一次出现的位置(str不包含substr时返回0)

```mysql
-- 结果是3，没找到返回是0，因为MySQL是从1开始计数的
select instr('ppopp','o');
```

- lpad(str,len,padstr) - 用字符串padstr填补str左端直到字串长度为len并返回 
- rpad(str,len,padstr) - 用字符串padstr填补str右端直到字串长度为len并返回

```mysql
-- abcdod
select rpad('abc',6,'do');
```

- left(str,len) - 返回字符串str的左端len个字符 
- right(str,len) - 返回字符串str的右端len个字符 

- substring(str,pos,len) - 返回字符串str的位置pos起len个字符  

- substring(str,pos) - 返回字符串str的位置pos起后面的子串 

- ltrim(str) - 返回删除了左空格的字符串str  

- rtrim(str)  - 返回删除了右空格的字符串str  

- space(n) - 返回由n个空格字符组成的一个字符串  

- ***replace(str,from_str,to_str)  - 用字符串to_str替换字符串str中的子串from_str并返回*** 

- reverse(str) - 颠倒字符串str的字符顺序并返回

- insert(str,pos,len,newstr) - 把字符串str由位置pos起len个字符长的子串替换为字符串

```mysql
-- 返回结果为 C*****
select insert('Carmen',2,5,'*****');
```

- lower(str) - 返回小写的字符串str  
- upper(str) - 返回大写的字符串str

- char_length(str) -  不管汉字还是数字或者是字母都算是一个字符。

- length(str);//汉字占3个,其他占1个.

- 把所有的first_name全部转换成大写

```mysql
select upper(first_name) from s_emp;
```

- 函数是可以嵌套函数使用的.

```mysql
select upper(substring(first_name,1,1)) from s_emp;
```

##### 练习

- 模拟银行的first_name模糊显示

```mysql
-- first_name    xxx
-- Carmen        C*****
-- Mark          M***

select first_name,rpad(substring(first_name,1,1),char_length(first_name)-1,'*') from s_emp;

select first_name,replace(first_name,substring(first_name,2),rpad('*',char_length(first_name)-1,'*')) from s_emp;

select first_name,concat(substring(first_name,1,1),rpad('*',char_length(first_name)-1,'*')) from s_emp;

select first_name,insert(first_name,2,char_length(first_name)-1,rpad('*',char_length(first_name)-1,'*')) from s_emp;
```

#### 数字函数

- abs(n) - 求绝对值
- mod(n,m) - 取模运算,返回n被m除的余数(同%操作符)

- **floor(n) - 返回不大于n的最大整数值** - 向下取整  

- **ceiling(n) - 返回不小于n的最小整数值**  - 向上取整 

- round(n[,d]) - 返回n的四舍五入值,保留d位小数(d的默认值为0)

```mysql
-- 4
select round(3.5);
-- 3
select round(3.4);
-- 3.46
select round(3.456,2);
```

- pow(x,y) - 返回值x的y次幂  
- sqrt(n) - 返回非负数n的平方根

- pi() - 返回圆周率  

- rand() - 返回在范围[0到1.0)内的随机浮点值

- truncate(n,d) - 保留数字n的d位小数并返回  - 直接截取

```mysql
-- 3
select truncate(3.56,0);
```

##### 练习

```mysql
-- 求1-3之间的随机整数
select truncate(rand()*3+1,0);

select floor(rand()*3+1);
```

#### 日期函数

- 查询当前系统的日期 - select now();

- dayofweek(date) - 返回日期date是星期几(1=星期天,2=星期一,……7=星期六,odbc标准)  

- weekday(date) - 返回日期date是星期几(0=星期一,1=星期二,……6= 星期天)

- year(date) - 返回date的年份(范围在1000到9999)    

- month(date)  - 返回date中的月份数值   

- dayofmonth(date) - 返回date是一月中的第几日(在1到31范围内)   

- hour(time) - 返回time的小时数(范围是0到23)  

- minute(time) - 返回time的分钟数(范围是0到59) 

- second(time) - 返回time的秒数(范围是0到59) 

- period_add(p,n) - 增加n个月到时期p并返回(p的格式yymm或yyyymm) 

  - mysql>select period_add('202008',3) ;

  - 注意:p可以是字符串,一定要满足一定的格式,或者可以直接使用date类型.

- period_diff(p1,p2) - 返回在时期p1和p2之间月数(p1和p2的格式yymm或yyyymm)  p1-p2

- curdate() - 以'yyyy-mm-dd'或yyyymmdd格式返回当前日期值(根据返回值所处上下文是字符串或数字) 

- curtime() - 以'hh:mm:ss'或hhmmss格式返回当前时间值(根据返回值所处上下文是字符串或数字)

- now() - 以'yyyy-mm-dd hh:mm:ss'或yyyymmddhhmmss格式返回当前日期时间(根据返回值所处上下文是字符串或数字)     

- last_day(date) - date日期所在月的最后一天是什么时候

- datediff(d1,d2) - 两个日期d1,d2之间相差的天数

- timestampdiff(type,d1,d2) - type是YEAR,d1和d2相差的年份,MONTH,月份d1,d2 - yyyymmdd


- mysql中,虽然日期date_format函数来转成字符串类型的,mysql内部为了简化我们的写法,有的时候有的函数.

- 只要你传入的字符串满足一定的格式,那么它就会自动被转成日期函数.、

- 日期函数补充

  - date_add(date,interval expr type)
  - date_sub(date,interval expr type)

  ```mysql
  type:
  year
  month
  day
  hour
  minute
  second
  ```

##### 日期格式化

- date_format(date,format)    

```mysql
  根据format字符串格式化date值  
　(在format字符串中可用标志符:  
　%m 月名字(january……december)    
　%w 星期名字(sunday……saturday)    
　%d 有英语前缀的月份的日期(1st, 2nd, 3rd, 等等。）    
　%Y 年, 数字, 4 位    
　%y 年, 数字, 2 位    
　%a 缩写的星期名字(sun……sat)    
　%d 月份中的天数, 数字(00……31)    
　%e 月份中的天数, 数字(0……31)    
　%m 月, 数字(01……12)    
　%c 月, 数字(1……12)    
　%b 缩写的月份名字(jan……dec)    
　%j 一年中的天数(001……366)    
　%H 24时制小时(00……23)    
　%k 小时(0……23)    
　%h 12时小时(01……12)    
　%i 小时(01……12)    
　%l 小时(1……12)    
　%i 分钟, 数字(00……59)    
　%r 时间,12 小时(hh:mm:ss [ap]m)    
　%t 时间,24 小时(hh:mm:ss)    
　%s 秒(00……59)       
　%p am或pm    
　%w 一个星期中的天数(0=sunday ……6=saturday ）                
```

#### 转换函数

- 字符串和数字之间转换

```mysql
-- 字符串'1'转成数字1
-- 结果是数字类型.
select '1'+0;

-- 数字1->字符串'1'
select concat(1,'');
```

- 字符串与日期之间转换

```mysql
日期转换成字符串使用date_format函数
字符串转换成日期str_to_date(str,format)；注:format格式必须和str的格式相同，否则返回空

应用场景:
create table test_date(
	id int(7),
  start_date date
);
insert into test_date values(1,now());

字符串满足合法的格式 - 底层都会自动转换date类型的 - mysql
insert into test_date values(2,'2022-08-09');
insert into test_date values(3,'2022/08/09');
insert into test_date values(4,'20220809');

mysql> insert into test_date values(2,'08/09/2022');
ERROR 1292 (22007): Incorrect date value: '08/09/2022' for column 'start_date' at row 1

把字符串转换成date类型
insert into test_date values(5,str_to_date('08/29/2022','%m/%d/%Y'));
```

#### 函数练习

- 找出名字长度超过5的员工

```mysql
select first_name from s_emp where char_length(first_name) > 5;
```

- 找出员工的工作月数

```mysql
select timestampdiff(month,start_date,now()) from s_emp;
```

- 查询员工的工作天数

```mysql
select datediff(now(),start_date) from s_emp;
```

- 计算一年前,当前,一年后的时间

```mysql
select date_sub(now(),interval 1 year) 一年前,now() 当前,date_add(now(),interval 1 year) 一年后;
```

- 当前日期前六个月的最后一天;

```mysql
select last_day(date_sub(now(),interval 6 month));
```

- 把员工的入职日期格式化为年/月/日

```mysql
select first_name,date_format(start_date,'%Y/%m/%d') from s_emp;
```

- 找出5月份入职的员工

```mysql
select first_name,start_date from s_emp where month(start_date) = 5;
```

