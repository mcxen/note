## MYSQL 常见SQL语句快速熟悉



### 取出各科成绩前三的学生

```sql
#取出各科成绩前三的学生
SELECT  * FROM stu_score ss1
WHERE (
    SELECT COUNT(*)
    FROM stu_score ss2
    where ss2.clazz=ss1.clazz and ss2.score>ss1.score
)<3
ORDER BY ss1.clazz,ss1.score desc;
#对于每一行ss1，统计在与其同一个班级（clazz相同）且分数比它高的行的数量，如果这个数量小于3，则选择该行。
```



```sql
#取出各科成绩都不小于85分的学生
#如果一个学生所有学科的分数中，最小的都>=85，则表示该学生每科成绩都不小于85
SELECT stu_name FROM stu_score ss1
GROUP BY stu_name HAVING MIN(score)>=85;
```



```sql
SELECT stu_name,clazz,score
FROM (
    SELECT stu_name,clazz,score,
           dense_rank() over (PARTITION BY stu_name order by score) AS rk
    FROm stu_score
     ) AS ranked_scores
WHERE rk<=3;
```

这个查询首先按照学生的id分组，然后在每个分组内按照成绩降序进行排名。使用了DENSE_RANK()函数，以处理成绩相同时不跳过相同排名的情况，从而确保每名学生都能取到TOP3的课程和分数。



### 课程 '02' 中有成绩但在课程 '01' 中没有成绩

SQL 查询：从数据库中选择那些在**课程 '02' 中有成绩但在课程 '01' 中没有成绩**的学生的相关信息。就是选修了02，但是没有选修01的同学信息。


方法1：使用LEFT JOIN 和 IS NULL

```sql
SELECT s.sid, s.sname, sc2.cid, sc2.score
FROM Student s
LEFT JOIN Score AS sc1 ON s.sid = sc1.sid AND sc1.cid = '01'
LEFT JOIN Score AS sc2 ON s.sid = sc2.sid AND sc2.cid = '02'
WHERE sc1.cid IS NULL AND sc2.cid IS NOT NULL;
```

方法2：内连接JOIN

```sql
SELECT s.sid, s.sname, sc.cid, sc.score
FROM Student s
JOIN Score sc ON s.sid = sc.sid AND sc.cid = '02'
WHERE NOT EXISTS (
    SELECT 1 FROM Score sc1 WHERE sc1.sid = s.sid AND sc1.cid = '01'
);
```

下面是详细的解释：

1. `FROM Student s JOIN Score sc ON s.sid = sc.sid`：这部分是一个连接查询，它将`Student`表（别名为`s`）和`Score`表（别名为`sc`）通过学生ID（`sid`）连接起来。这样做是为了能同时访问学生表和成绩表中的数据。
2. `AND sc.cid = '02'`：这个条件指定连接查询仅考虑那些在课程ID为 '02' 的成绩记录。
3. `WHERE NOT EXISTS (...)`：这个子句用来过滤结果，仅包括那些在子查询中返回空结果的记录。换言之，它筛选出那些在子查询指定的条件下不存在记录的学生。
4. 子查询 `SELECT 1 FROM Score sc1 WHERE sc1.sid = s.sid AND sc1.cid = '01'`：这个子查询检查每个学生是否有在课程 '01' 中的成绩记录。这里`SELECT 1`是常用的SQL技巧，表示只要找到满足条件的记录即可，不关心记录的具体内容。
5. 最终，主查询 `SELECT s.sid, s.sname, sc.cid, sc.score`：这个部分选择输出学生ID (`s.sid`), 学生名 (`s.sname`), 课程ID (`sc.cid`), 和对应的成绩 (`sc.score`)。

总结来说，这个查询用于找出那些在课程 '02' 中有成绩但在课程 '01' 中无成绩的学生，并展示他们在课程 '02' 的成绩和一些基本信息。这可以用于识别只选择某些特定课程的学生情况。