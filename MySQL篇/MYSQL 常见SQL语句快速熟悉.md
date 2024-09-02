## MYSQL 常见SQL语句快速熟悉

### 取出各科成绩都不小于85分的学生

```sql
#取出各科成绩都不小于85分的学生
#如果一个学生所有学科的分数中，最小的都>=85，则表示该学生每科成绩都不小于85
SELECT stu_name FROM stu_score ss1
GROUP BY stu_name HAVING MIN(score)>=85;
```



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

使用 `LIMIT` 来直接在 SQL 查询中获取每个组（例如每个班级）的前三名记录通常不那么直接，因为 `LIMIT` 通常适用于整个查询结果的限制，而不是按组分别限制。但是，可以通过结合使用窗口函数（如 `ROW_NUMBER()` 或 `RANK()`）来实拟实现每组的 `LIMIT` 功能。

### 每名学生的TOP3的课程和分数

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



### 表中查找每门学科成绩最高的学生

```sql
SELECT student_name, subject_name, score
FROM students_scores
WHERE (subject_name, score) IN (
    SELECT subject_name, MAX(score)
    FROM students_scores
    GROUP BY subject_name
);
```





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

### 排名在第5到第10名的学生

我们需要从 `student_score` 表中先计算每个学生的总分，然后按照总分降序排序，并且筛选出排名在第5到第10名的学生。这里有一个 SQL 查询示例来实现这个要求：

```sql
SELECT stu_id, SUM(score) AS total_score
FROM student_score
GROUP BY stu_id
ORDER BY total_score DESC
LIMIT 5 OFFSET 4;
```

解释：

1. **聚合总分**：

    * `SELECT stu_id, SUM(score) AS total_score`：这一句选择了学生ID (`stu_id`) 并计算了他们的总分。
    * `FROM student_score`：指定了数据来源表为 `student_score`。
    * `GROUP BY stu_id`：按学生ID分组，这样每个学生的成绩都会被累加起来计算总分。
2. **排序和筛选**：

    * `ORDER BY total_score DESC`：按照计算出的总分降序排列，这样总分最高的学生会排在最前面。
    * `LIMIT 5 OFFSET 4`：这两个参数用于从结果集中获取特定范围的记录。`LIMIT 5` 表示选择5条记录，`OFFSET 4` 表示从结果的第五条记录开始获取（因为偏移量是从0开始计数的）。结合这两个参数，就可以选出总分排名第5到第10的学生。

这个查询假设数据库使用的 SQL 方言支持 `LIMIT` 和 `OFFSET` 关键词，这在许多现代数据库系统（如 PostgreSQL, MySQL）中是有效的。如果你使用的是不支持这两个关键词的数据库系统（比如 SQL Server 或 Oracle），你需要使用相应的方言来调整查询语句。例如，在 SQL Server 中，你可以使用 `ROW_NUMBER()` 函数结合 `TOP` 和子查询来实现相同的功能。
