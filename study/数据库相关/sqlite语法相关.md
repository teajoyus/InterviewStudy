# 前言

本篇博客写的是搜集Sqlite的一些语法和用法。

sqlite方便我们进行增删改查，而增删改的sql语句比较简单，重点是如何针对性的查找。
所以本篇主要是搜集了sqlite查找相关的用法。


# Sqlite 基础用法

## where 条件子句

http://www.runoob.com/sqlite/sqlite-where-clause.html

笔记：除了< 、>=  、or  、and、not null

列出了 AGE 大于等于 25 且工资大于等于 65000.00 的所有记录：
```
 SELECT * FROM COMPANY WHERE AGE >= 25 AND SALARY >= 65000;
```


列出了 AGE 不为 NULL 的所有记录，结果显示所有的记录，意味着没有一个记录的 AGE 等于 NULL：
```
SELECT * FROM COMPANY WHERE AGE IS NOT NULL;
```

## in语法

in语法用于枚举，比如想查找出表中所有姓名是张三和李四的字段，则可以用：
```
SELECT * FROM COMPANY WHERE NAME in('张三','李四')
```

还可以用做子查询：
比如，查出COMPANY表中符合指定姓名的所有记录，这些指定姓名是在USER表中工资大于10000的姓名
```
SELECT * FROM COMPANY WHERE NAME in(SELECT NAME FROM USER where SALARY >10000)
```

## like语法

列出了 NAME 以 'Ki' 开始的所有记录，'Ki' 之后的字符不做限制：
```
SELECT * FROM COMPANY WHERE NAME LIKE 'Ki%';
```
标注：
 LIKE 运算符是用来匹配通配符指定模式的文本值。
 百分号（%）代表零个、一个或多个数字或字符。
 下划线（_）代表一个单一的数字或字符。
 这些符号可以被组合使用。
SQL中支持的[char list]在sqlite3中不能用

特别注意百分号和下划线混用时不好理解的含义：

 查找以 2 开头，且长度至少为 3 个字符的任意值：
 WHERE SALARY LIKE '2_%_%'

 查找第二位为 2，且以 3 结尾的任意值
 WHERE SALARY LIKE '_2%3'

 查找长度为 5 位数，且以 2 开头以 3 结尾的任意值：
 WHERE SALARY LIKE '2___3'

**注意特殊字符的转义关键字（查找出所有含有百分号%的任意值）：**
```
WHERE SALARY LIKE '%/%%' escape '/'
```


## GLOB


 星号（*）代表零个、一个或多个数字或字符。
 问号（?）代表一个单一的数字或字符。
 这些符号可以被组合使用。
 特别注意星号和问号混用时不好理解的含义：

 查找以 2 开头，且长度至少为 3 个字符的任意值：
 WHERE SALARY GLOB '2??'

 查找第二位为 2，且以 3 结尾的任意值：
 WHERE SALARY GLOB '?2*3'
 查找长度为 5 位数，且以 2 开头以 3 结尾的任意值：
 WHERE SALARY GLOB '2???3'

## LIMIT 和 OFFSET

LIMIT 子句用于限制由 SELECT 语句返回的数据数量，配合OFFSET可以限定偏移量

查找出从第三条记录开始后的三条记录：
 SELECT * FROM COMPANY LIMIT 3 OFFSET 2;


## Order By

ORDER BY 子句是用来基于一个或多个列按升序或降序顺序排列数据。

ASC是默认是升序排列，降序排列用DESC
例子：
按工资升序排序输出：
SELECT * FROM COMPANY ORDER BY SALARY ASC;

按名字降序排序输出：
SELECT * FROM COMPANY ORDER BY NAME DESC;

## Group By

 GROUP BY 子句用于与 SELECT 语句一起使用，来对相同的数据进行分组。
 场景举例：当我们有很多条数据，想要根据某个字段来进行分组统计的时候，就可以用到Group by，比如统计学生的总分。

注意：GROUP BY 子句放在 WHERE 子句之后，**放在 ORDER BY 子句之前**。
而且，GROUP BY 允许使用多个列，形如：GROUP BY column1, column2....columnN

例子：查找出每个人的的工资总额：
SELECT NAME, SUM(SALARY) FROM COMPANY GROUP BY NAME;

查找出每个人的的工资总额并按照名字降序排列：
SELECT NAME, SUM(SALARY) FROM COMPANY GROUP BY NAME ORDER BY NAME;

## HAVING子句

功能：允许指定条件来过滤将出现在最终结果中的分组结果。

也就是指，筛选过滤掉一些分组，配合GROUP BY关键字

例子：显示名称计数大于 2 的所有记录：
SELECT * FROM COMPANY GROUP BY NAME HAVING COUNT(NAME) > 2;

## Distinct 关键字

功能：用来消除所有重复的记录
场景举例：比如想从某个表获取该列的所有不同的取值，那么就得过滤掉这些重复的。

例子：查询出COMPANY表中所有客户名字：
SELECT DISTINCT name FROM COMPANY;

## Alter 命令
可以使用 ALTER TABLE 语句重命名表，使用 ALTER TABLE 语句还可以在已有的表中添加额外的列。
来重命名已有的表：
```
ALTER TABLE database_name.table_name RENAME TO new_table_name;
```

在已有的表中添加一个新的列：
```
ALTER TABLE database_name.table_name ADD COLUMN column_def...;
```


# Sqlite高级用法

## SQLite 约束
约束：定义在表或列上强制执行的规则，以确保数据的准确性和可靠性。

### NOT NULL 约束：确保某列不能有 NULL 值。

当你想保证列上不会出现NULL值的时候，那么就可以用NOT NULL约束，约束了后在插入数据时在该列插入对应数据则会报错

### DEFAULT 约束：当某列没有指定值时，为该列提供默认值。

如果你想该列有一个自定义默认取值的时候，就可以用DEFAULT

### UNIQUE 约束：确保某列中的所有值是不同的。

如果你想保证该列上的数据都是唯一性的，互不相同的。那么就可以用到UNIQUE，插入数据时当该列已有相同数据时报错

### PRIMARY Key 约束：唯一标识数据库表中的各行/记录。

在一个表中可以有多个 UNIQUE 列，但只能有一个主键。**特别注意在sqlite中的主键可以是NULL**
主键不一定是某个字段，它可以由多个字段组成，形成唯一约束标识。被称为“复合键”。
在一个表上的主键不能具有相同的值。

### CHECK 约束：确保某列中的所有值满足一定条件。

如果你想保证某一列上的取值都满足一定条件的时候，可以用CHECK来约束。比如年龄必须大于0

### AUTOINCREMENT

用于表中的字段值自动递增。

综上，我们创建一个样例表来写出上面所有用到的约束情况：
```
CREATE TABLE COMPANY(
   ID INT PRIMARY KEY  AUTOINCREMENT,
   NAME           TEXT    NOT NULL,
   AGE            INT     NOT NULL UNIQUE,
   ADDRESS        CHAR(50), DEFAULT 'XXX'
   SALARY         REAL    CHECK(SALARY > 0)
);
```
如上面表格，就是创建了一个以id作为主键

## JOIN
join用于多个表联合的时候，是一种通过共同值来结合两个表中字段的手段。

### 交叉连接 - CROSS JOIN
交叉连接（CROSS JOIN）把第一个表的每一行与第二个表的每一行进行匹配。
把表A和表B的数据进行一个N*M的组合，即笛卡尔积。
会按照a_table的每一行去连接b_table的每一行
得到的记录条数是：a_table的条数 * b_table的条数。
一般不会用到这个

###  内连接 - INNER JOIN

#### 语法写法
内连接inner join可简写为join。写法：
select * from a_table a join b_table b on a.id = b.id
写法等价于：
select * from a_tablea ,b_table b where a.id = b.id

#### 作用意义（交集）

根据约束条件得到的**结果是两个表中的交集部分**

#### 内连接举例说明

a_table表格数据如下：

| a_id | a_name | a_age
|----|----|----|
| 1 | 张三 |  23
| 2 | 李四 |  24
| 3 | 王五 |  26

b_table表格数据如下：

| b_id | b_name | b_salary
|----|----|----|
| 2 | 李四 |  5000
| 3 | 王五 |  8000



进行内连接：
```
select * from a_table a join b_table b
```
得到结果如下：

|a_id|a_name|a_age|b_id|b_name|b_salary
|----|----|----|----|----|----|
|1|张三|23|2|李四|5000
|1|张三|23|3|王五|8000
|2|李四|24|2|李四|5000
|2|李四|24|3|王五|8000
|3|王五|26|2|李四|5000
|3|王五|26|3|王五|8000


得到的结果是两个表中的交集部分
约束id进行内连接：
```
select * from a_table a join b_table b on a.a_id = b.b_id
```
得到结果如下：

a_id|a_name|_age|b_id|b_name|b_salary
|----|----|----|----|----|----|
2|李四|24|2|李四|5000
3|王五|26|3|王五|8000

### outer join 外连接
外连接（OUTER JOIN）是内连接（INNER JOIN）的扩展。注意sqlite只支持左连接（左外连接）

#### 语法说明
 SQL 标准定义了三种类型的外连接：LEFT、RIGHT、FULL，但 SQLite 只支持 左外连接（LEFT OUTER JOIN）。
使用left join 等价于 left outer join

#### 作用意义
进行左连接时会把a_table的行数全部显示出来，b_table只会显示满足条件的记录，记录不足的地方均为NULL

####  举例说明

a_table表格数据如下：

| a_id | a_name | a_age
|----|----|----|
| 1 | 张三 |  23
| 2 | 李四 |  24
| 3 | 王五 |  26

b_table表格数据如下：

| b_id | b_name | b_salary
|----|----|----|
| 2 | 李四 |  5000
| 3 | 王五 |  8000


结果如下：

a_id|a_name|a_age|b_id|b_name|b_salary
----|----|----|----|----|----|
1|张三|23|NULL |NULL|NULL|
2|李四|24|2|李四|5000
3|王五|26|3|王五|8000

可以看出，a_table的数据会全部显示出来，而b_table除了满足搜索条件的行数能显示出来外，不能与a_table匹配的行数记录会用NULL代替


##  UNION 与 UNION ALL

### 语法
```
SELECT column_name(s) FROM table_name1
UNION
SELECT column_name(s) FROM table_name2
```
UNION 内部的 SELECT 语句必须拥有相同数量的列。
列也必须拥有相似的数据类型。
每条 SELECT 语句中的列的顺序必须相同

**UNION 结果集中的列名总是等于 UNION 中第一个 SELECT 语句中的列名。**
### 作用
 UNION用于合并两个或多个 SELECT 语句的结果，不返回任何重复的行。
 UNION ALL 则可以返回重复的行。
### 举例
还是沿用上文的a_table 和 b_table
执行：
```
select a_id as id ,a_name as name from a_table a

union

select  b_id as id ,b_name as name from b_table b
```

结果如下：

id|name
|----|----
1|张三
2|李四
3|王五


执行（union all）：
```
select a_id，a_name as name from a_table a

union all

select  b_id ,b_name as name from b_table b
```
结果如下：

a_id|name
|----|----
1|张三
2|李四
3|王五
2|李四
3|王五

从结果可以看到，union可以把几个select联合起来，union和 union all差距是包不包含重复结果
另外还是需要注意的是几个select语句的列数必须一样、顺序必须一样、数据类型必须类似
而不需要列名一定相同，查询结果的列表是会按照第一个select的列名，如上面演示union all，查询结果的列名是a_id


## Sqlite常用的计算函数
很多时候，我们不仅仅只是要查找出数据，还需要在查找过程中对数据进行计算，比如在学生成绩系统中，需要统计总分、平均分等
当然我们也可以在代码里直接取得搜索结果然后自己计算也可以，但是相比直接执行sql语句计算数据，效率和性能不是一个档次了
所以一些常用的计算函数还是要了解一下

### COUNT 函数
用来统计行数
```
select count(*) as count from a_table
```
结果：

|count
|---|
|3

### MAX & MIN 函数
MAX 和 MI 用来计算列中最大 最小值
```
select max(b_salary) as max_salary from b_table
```
结果：

|max_salary
|---|
|8000

MIN函数用法一样

### AVG 函数
用来计算平均值
```
select avg(b_salary) as avg_salary from b_table
```
结果：

|avg_salary
|---|
|6500

### SUM 函数
用来求和
```
select avg(b_salary) as avg_salary from b_table
```
结果：

|sum(b_salary)
|---|
|13000

### RANDOM 函数
RANDOM 函数返回一个介于 -9223372036854775808 和 +9223372036854775807 之间的伪随机整数

```
SELECT random() AS Random;
```

结果：

|Random
|---|
|-5906358955607826848

### ABS 函数

用来求绝对值

### UPPER & LOWER函数
UPPER 函数把字符串转换为大写字母
 LOWER 函数把字符串转换为小写字母

###  LENGTH 函数
LENGTH 函数返回字符串的长度。
```
select length(a_name)  from a_table
```
结果：

|length(a_name)
|---|
|2
|2
|2

### sqlite_version 函数
 sqlite_version 函数返回 SQLite 库的版本

```
SELECT sqlite_version() AS 'SQLite Version';
```


## 日期 & 时间
### 函数
序号|函数|实例
|---|---|---
1|date(timestring, modifier, modifier, ...)|以 YYYY-MM-DD 格式返回日期。
2|time(timestring, modifier, modifier, ...)|以 HH:MM:SS 格式返回时间。
3|datetime(timestring, modifier, modifier, ...)|以 YYYY-MM-DD HH:MM:SS 格式返回。
4|julianday(timestring, modifier, modifier, ...)|这将返回从格林尼治时间的公元前 4714 年 11 月 24 日正午算起的天数。
5|strftime(format, timestring, modifier, modifier, ...)|这将根据第一个参数指定的格式字符串返回格式化的日期。具体格式见下边讲解

### 支持的时间格式
允许代入的日期时间格式：

序号|时间字符串|实例
|---|---|---
1|YYYY-MM-DD|2010-12-30
2|YYYY-MM-DD HH:MM|2010-12-30 12:10
3|YYYY-MM-DD HH:MM:SS.SSS|2010-12-30 12:10:04.100
4|MM-DD-YYYY HH:MM|30-12-2010 12:10
5|HH:MM|12:10
6|YYYY-MM-DDTHH:MM|2010-12-30 12:10
7|HH:MM:SS|12:10:01
8|YYYYMMDD HHMMSS|20101230 121001
9|now|2013-05-07

注意最后一行的 now ，**传入字符串now可以求得当前日期**

### 修饰符（Modifier）
NNN days

NNN hours

NNN minutes

NNN.NNNN seconds

NNN months

NNN years

start of month

start of year

start of day

weekday N

unixepoch

localtime

utc

### strftime函数格式化
SQLite 提供了非常方便的函数 strftime() 来格式化任何日期和时间。
我们可以使用以下的替换来格式化日期和时间：

替换|描述
|---|---
%d|一月中的第几天，01-31
%f|带小数部分的秒，SS.SSS
%H|小时，00-23
%j|一年中的第几天，001-366
%J|儒略日数，DDDD.DDDD
%m|月，00-12
%M|分，00-59
%s|从 1970-01-01 算起的秒数
%S|秒，00-59
%w|一周中的第几天，0-6 (0 is Sunday)
%W|一年中的第几周，01-53
%Y|年，YYYY
%%|% symbol
### 举例

计算当前日期：
```
SELECT date('now');
```
结果：
2019-02-22

计算当前月份的最后一天：
```
SELECT date('now','start of month','+1 month','-1 day');
```
结果：
2019-02-28

解析：怎么解读呢？首先date函数的第一个参数接收的是日期格式，传入了字符串now代表当前的时间
紧接着date函数的第二个参数是一个修饰符Modifier，传入了'start of month'表示本月开始的第一天
然后第二个修饰符Modifier，传入了'+1 month'，表示下一个月，到这里的话，得到的结果就是2019-03-01（我现在时间是2019.02.22）
第三个传入了'-1 day'表示在这个基础上去减掉一天
整体来看的话，就是取得下个月的第一天然后又减去1天则等同于本月的最后一天


计算给定 UNIX 时间戳 1550825296 的日期和时间：
```
 SELECT datetime(1550825296, 'unixepoch');
```
结果：
2019-02-22 08:48:16

注意UNIX 时间戳是以秒为单位的，位数是10

计算当前的 UNIX 时间戳：
```
SELECT strftime('%s','now');
```
结果：
1550825425

解析：上面我们给出了一张strftime() 函数接收各种格式化字符串的表

%s查询可知是从 1970-01-01 算起的秒数，事实上我们现在使用的时间戳也就是从这个时间开始距离当前的秒数

计算两个时间距离的秒数
比如计算2010-01-01 00:00:00距离到现在的秒数：
```
SELECT strftime('%s','now') - strftime('%s','2010-01-01 00:00:00');
```
结果：
288521906

计算今年 3 月的第一个星期二的日期：
```
SELECT date('now','start of year','+2 month','weekday 2');
```
结果：
2019-03-05

解析：计算今年，那么就要得到今年，那么传入当前的时间也就是今年，
然后要求得今年3月份，那么就是这一年开始后再+2 month 得到3月份（当然，我现在是二月份，我也可以用start of month 然后再+1 month，不过这种做法相对前面的就不太好了，因为二月份总会过去）

还有更多的用法，我们也没法去记得全部的修饰符，只能用到的时候不记得就再查查。只要记得在sqlite中的日期和时间的计算，有这么几个函数，然后可以通过各种修饰符去计算时间就好了。


## 触发器（Trigger）

触发器（Trigger）是数据库的回调函数，它会在指定的数据库事件发生时自动执行/调用。

### 特点
1、SQLite 的触发器（Trigger）可以指定在特定的数据库表发生 DELETE、INSERT 或 UPDATE 时触发，或在一个或多个指定表的列发生更新时触发。
2、SQLite 只支持 FOR EACH ROW 触发器（行触发器），没有 FOR EACH STATEMENT 触发器（语句触发器）。因此，明确指定 FOR EACH ROW 是可选的。
（for each row 是操作语句每影响到一行的时候就触发一次，也就是删了 10 行就触发 10 次，而 for each state 一条操作语句就触发一次，有时没有被影响的行也执行。sqlite 只实现了 for each row 的触发。）
3、BEFORE 或 AFTER 关键字决定何时执行触发器动作，决定是在关联行的插入、修改或删除之前或者之后执行触发器动作。
4、当触发器相关联的表删除时，自动删除触发器（Trigger）。
5、一个特殊的 SQL 函数 RAISE() 可用于触发器程序内抛出异常。
6、如果提供 WHEN 子句，则只针对 WHEN 子句 条件为真的指定行执行 SQL 语句。如果没有提供 WHEN 子句，则针对所有行执行 SQL 语句。
7、WHEN 子句和触发器（Trigger）动作可能访问使用表单 NEW.column-name 和 OLD.column-name 的引用插入、删除或更新的行元素，其中 column-name 是从与触发器关联的表的列的名称。
### 举例
在上面的a_table基础上，我们新增一个t_audit表：
```
create table t_audit(id integer ,date text);
```
如下:

|id|date
|---|---
||

现在我们来写个触发器，当在a_table中插入数据的时候，则自动触发向t_audit表中插入a_table中新增的id和插入时间
```
create trigger audit_log after insert on a_table
begin
 insert into t_audit(id,date) values(new.a_id,datetime('now'));
end;
```

我们开始向a_table插入一条数据：
```
insert into a_table values(4,'赵六',29)
```


然后看下我们刚才新加的t_audit表：
```
select * from t_audit
```

结果如下：

id|date
|---|---
4|2019-02-22 09:25:47

我们可以看到t_audit自动增加了一条记录，增加的id是我们刚才往a_table中插入的a_id字段，date则是插入时间


### 配合when子句语法

直接举个例子吧,将我们刚才实现的触发器加一个条件，是当id>10才触发：
```
create trigger audit_log after insert on a_table
when new.a_id>10
begin
 insert into t_audit(id,date) values(new.a_id,datetime('now'));
end;
```

### 删除触发器

当然，既然设置了触发器，一般也要有删除触发器的操作：
删除掉刚才创建的触发器audit_log ：

```
drop trigger audit_log;
```

### 列出触发器

从 sqlite_master 表中列出所有触发器，如下所示：
```
SELECT name FROM sqlite_master WHERE type = 'trigger';
```
注意sqlite_master 表是sqlite一个内置的系统表


## 索引 - 概念
索引（Index）是一种特殊的查找表，数据库搜索引擎用来加快数据检索。

索引的作用可以联想到字典，当你想查找某个字的时候可以通过他的读音来缩小范围，或者是根据偏旁部首。那么这就是一种索引

索引的语法特别简单，贵在怎么正确理解和使用索引。


索引的深入理解可以进入看这篇博客：https://www.cnblogs.com/hyd1213126/p/5828937.html

具体的参考这篇博客更好了。

## 视图 - 概念

视图（View）只是通过相关的名称存储在数据库中的一个 SQLite 语句，是一种虚表。
之所以称之为视图，是因为它实际上是没有物理存储的。
当我们想汇总很多个表数据、或者是想屏蔽掉表中一些敏感的字段，则可以用到视图

###  视图的创建
我们沿用前面的a_table这个表，根据这个表创建出一个只有id和name两个列的视图：
```
create view a_table_view as select a_id,a_name from a_table
```
把这个视图输出来：
```
select * from a_table_view
```
a_id|a_name
|---|---
1|张三
2|李四
3|王五
4|赵六
8|赵六

### 删除视图
```
DROP VIEW a_table_view
```


## 事务

事务（Transaction）是一个对数据库执行工作单元。通俗的讲是把许多的 SQLite 查询联合成一组，把所有这些放在一起作为事务。
控制事务以确保数据的完整性和处理数据库错误。
比如有个场景：我们在银行转账的时候，基本的流程肯定是存在一个我这边扣款，对方收款的过程。这个过程便是一个工作单元，他们要么做完，要么不做。

### ACID特性
事务（Transaction）具有以下四个标准属性，通常根据首字母缩写为 ACID：

原子性（Atomicity）：确保工作单位内的所有操作都成功完成，否则，事务会在出现故障时终止，之前的操作也会回滚到以前的状态。

一致性（Consistency)：确保数据库在成功提交的事务上正确地改变状态。

隔离性（Isolation）：使事务操作相互独立和透明。

持久性（Durability）：确保已提交事务的结果或效果在系统发生故障的情况下仍然存在。

###     事务控制
使用下面的命令来控制事务：

BEGIN TRANSACTION：开始事务处理。

COMMIT：保存更改，或者可以使用 END TRANSACTION 命令。

ROLLBACK：回滚所做的更改。

#### TRANSACTION 命令
事务（Transaction）可以使用 BEGIN TRANSACTION 命令或简单的 BEGIN 命令来启动。
此类事务通常会持续执行下去，直到遇到下一个 COMMIT 或 ROLLBACK 命令。不过在数据库关闭或发生错误时，事务处理也会回滚。

#### COMMIT 命令

COMMIT 命令是用于把事务调用的更改保存到数据库中的事务命令。

COMMIT 命令把自上次 COMMIT 或 ROLLBACK 命令以来的所有事务保存到数据库。
也可以用END TRANSACTION;

#### ROLLBACK 命令

ROLLBACK 命令是用于撤消尚未保存到数据库的事务的事务命令。

ROLLBACK 命令只能用于撤销自上次发出 COMMIT 或 ROLLBACK 命令以来的事务。

