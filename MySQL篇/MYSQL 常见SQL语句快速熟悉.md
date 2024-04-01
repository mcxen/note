MYSQL 常见SQL语句快速熟悉



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