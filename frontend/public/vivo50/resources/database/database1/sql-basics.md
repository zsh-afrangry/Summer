# SQL基础与CRUD操作

# 数据定义子语言DDL

用于定义和管理 数据库对象 的结构，比如表本身。这类操作会改变表结构

- 创建表 `CREATE TABLE`

```sql
-- 需要定义表名、列名、每一列的数据类型、约束（主键、非空等）
CREATE TABLE employees(
		id INT PRIMARY KEY, -- id，int类型，是主键，逗号隔开
		first_name VARCHAR(50) NOT NULL, -- 名，50字符以内的字符串，不可为空
		last_name VARCHAR(50),
    department VARCHAR(50),
    salary DECIMAL(10, 2), -- 薪资，使用DECIMAL类型，数字型128bit，小数点左边10位，右边2位
    hire_date DATE -- 聘用日期，数据类型是DATE（年月日，没有时分秒）
);
```

- 修改表 `ALTER TABLE`

```sql
-- 增加一个新列
ALTER TABLE employees ADD email_address VARCHAR(50);
-- 删除一个原有列
ALTER TABLE employees DROP COLUMN email_address;
-- 修改某一个列的数据类型
ALTER TABLE employees MODIFY COLUMN salary INT;
-- 修改数据表的名字
ALTER TABLE employees TO staff;
-- 修改列名，用的是UPDATE
```

- 删除表 `DROP TABLE`

```sql
DROP TABLE employees;
```

- 清空表 truncate `TRUNCATE TABLE`

```sql
TRUNCATE TABLE employees; -- 操作速度比删除表快，有的数据不支持回滚
```

| id | first_name | last_name | department | salary | hire_date |
| --- | --- | --- | --- | --- | --- |
| 1 | John | Doe | HR | 60000 | 2022-01-15 |
| 2 | Jane | Smith | Engineering | 85000 | 2021-03-22 |
| 3 | Peter | Jones | Engineering | 95000 | 2020-05-30 |
| 4 | Emily | Williams | Sales | 72000 | 2022-08-10 |
| 5 | Michael | Brown | Sales | 78000 | 2021-11-01 |
| 6 | Sarah | Davis | HR | NULL | 2023-02-20 |

# 数据库查询子语言DQL

## SELECT（读取数据）

假设目前有一张`employees`的数据表

- `SELECT`基础

```sql
SELECT * FROM employees; # 查询所有列
SELECT first_name, salary FROM employees; # 查询两个列
```

- `WHERE`条件筛选，只返回满足条件的行

```sql
-- 查询Engineering部门的全部员工的名字
SELECT  first_name FROM employees WHERE department = 'Engineering';
-- 查询薪水大于 80000 的所有员工
SELECT first_name, salary FROM employees WHERE salary > 80000;
```

- `LIKE`模糊匹配：`%`代表0个、1个、多个字符，`_`代表单一字符

```sql
-- 查询所有姓氏以S开头的员工
SELECT * FROM employees WHERE last_name LIKE 'S%';
-- 查询所有名字中有ah的员工
SELECT * FROM employees WHERE first_name LIKE '%ah%';
-- 查询所有 姓氏的第三个字母是J的四字员工
SELECT * FROM employees WHERE first_name LIKE '__J_';
-- 查询第二个字母是a，且结尾是s的任意长度的字符串
SELECT * FROM employees WHERE first_name LIKE '_a%s'
```

- `IN`在…之中，`BETWEEN`在… 和… 之间

```sql
-- 查询 HR 和 Engineering这两个部门内的所有员工
SELECT * FROM employees WHERE department IN ('HR', 'Engineering')
-- 查询薪资在70000~80000的员工
SELECT * FROM employees WHERE salary BETWEEN 70000 and 80000
```

- `IS NULL` (是空值)，不能用=，必须要用`IS`

```sql
-- 查询薪水未知的员工
SELECT * FROM employees WHERE salary IS NULL;
```

- `ORDER BY` 排序，升序`ASC`，降序`DESC`，多条件时按先后排序

```sql
-- 按薪水从低到高排序
SELECT first_name, salary FROM employees ORDER BY salary ASC;
-- 先按部门排序，部门相同时再按薪水降序排序
SELECT * FROM employees ORDER BY department, salary DESC;
```

- `LIMIT`分页，限制返回数量N

```sql
-- LIMIT [offset,] count，offset 是起始行的索引（从0开始），count 是要返回的行数
-- 返回薪水最高的三名员工
SELECT * FROM employees ORDER BY salary ASC LIMIT 3;
-- 返回“从第11个数据开始的5个数据”，这个类似于“第三页”(10表示第11个数据，下标从0开始)
SELECT * FROM employees ORDER BY salary ASC LIMIT 10, 5;
```

# 数据操纵子语言 ****DML

## INSERT（插入数据）

分为两种，一种插入完整的一条数据，一种是插入部分数据

```sql
-- 插入一条完整的数据，列名与值的顺序必须一一对应
INSERT INTO employees (id, first_name, last_name, department, salary, hire_date)
VALUES (7, 'David', 'Lee', 'Engineering', 92000, '2023-05-18');
-- 插入部分列的数据，未指定的列将被置为默认值或NULL
INSERT INTO employees (id, first_name, last_name, department, hire_date)
VALUES (8, 'Nancy', 'Chen', 'Marketing', '2023-06-01');
```

## UPDATE（更新数据）

UPDTAE () SET () WHERE () 

```sql
-- 将Sales部门的所有员工，薪资涨为1.2倍，且将部门改名为SalesPlus
UPDATE employees
SET salary = salary * 1.2, department = 'SalesPlus'
WHERE department = 'Sales'
-- **非常重要：** UPDATE 语句一定要带 WHERE 子句，否则将更新表中的所有行！
```

## DELETE（删除数据）

DELETE FROM () WHERE ()

```sql
--  与 UPDATE 一样，DELETE 语句通常也必须带 WHERE 子句，否则将删除表中的所有行！
DELETE FROM employees
WHERE salary < 65000;
```

# 聚合 与 分组

## 聚合函数

聚合函数会对一组值进行计算，并返回单个值

- COUNT - 计数

```sql
-- 计算员工总数
SELECT COUNT(id) FROM employees;
-- 会自动忽略NULL
SELECT COUNT(salary) FROM employees;-- 如果有一行的salary是NULL，它的输出比上面的小1
```

- SUM - 求和

```sql
-- 计算公司年度总薪水支出
SELECT SUM(salary) FROM employees; 
```

- AVG - 平均值

```sql
-- 计算薪资的平均值(会自动忽略NULL)
SELECT AVG(salary) FROM employees;
```

- MAX / MIN - 最大值 / 最小值

```sql
-- 计算薪资中的最大值和最小值
SELECT MAX(salary), MIN(salary) FROM employees;
```

## 分组`GROUP BY`

GROUP语句经常和 聚合函数 放在一起使用，根据列 将结果进行分组，然后对每个列进行聚合函数

- GROUP BY的使用

```sql
SELECT department, COUNT(id) AS employee_count
FROM employees
GROUP BY department
```

如何理解上述SQL语句：

1. 系统扫描department表
2. 根据department的值，将员工分成n组
    1. 比如这个表里，department列只有 “HR”、“Engineering”、“Sales”
    2. 因此，系统根据department将员工分成3组
3. 对每个组分别进行COUNT(id)计算，并返回结果
4. 这个结果类似于下表
    1. 只有两列是因为`SELECT`只选了两列，一列是`department`，一列是`employee_count`
    2. `GROUP BY department`明确了最终的查询结果应该以department为主，一个department一行
    
    | department  | employee_count  |
    | --- | --- |
    | HR  | 2 |
    | Engineering  | 3 |
    | Sales  | 4 |

```sql
-- 计算所有部门的平均薪资
SELECT department, AVG(salary) AS average_salary
FROM employees
GROUP BY department
```

## **分组后筛选 `HAVING`**

`HAVING` 和 `WHERE` 的区别

- `WHERE`在分组之前进行筛选，它过滤的是原始表中的`行`
- `HAVING`在分组之后进行过滤，它过滤的是GROUP BY产出的`组`
- 在`HAVING`句子中可以使用聚合函数，但是在`WHERE`句子中不能

之前提到的分组中，有一个是“计算所有部门的平均薪资”

然后现在，可以 “计算部门数大于3的部门的平均薪资”

也就是说，是先对数据进行分组后，得到所有部门的平均薪资，再从中进行筛选出人数大于3的部门

```sql
-- 筛选出 人数大于的 部门 的 平均薪资
SELECT 
	department,
	COUNT(id) AS employee_count,
	AVG(salary) AS avg_salary
FROM 
	employees
GROUP BY
	department
HAVING
	employee_count > 3;
```

执行步骤：

1. 确定数据来源表 `employees`
2. 确定分组形式：`GROUP BY department`，按部门分组
3. 分组完毕，对每组进行聚合函数计算，得到每一组的id总数 和 salary
4. 最后用HAVIING筛选，在得到的表中，只显示id总数大于3的

# 多表查询 Joins-高频考点

当一个数据分散在多个表中时，就要用JOIN来将它们连接起来

除去上面的表，我们再创建一个部门表：`departments`

| dept_id | dept_name | location |
| --- | --- | --- |
| 1 | HR | New York |
| 2 | Engineering | San Francisco |
| 3 | Sales | New York |
| 4 | IT | Chicago |

## 内连结 INNER JOIN

只返回两个表中连结字段能匹配上的行。

<aside>
💡

可以想象成两个集合（表）的交集（共有的列）

</aside>

比如：我们想查询 每个员工 所在的 部门 的地点

**查询逻辑：在department钟遍历每一列，获取每个员工的部门信息，再去`departments`找每个部门对应的location。最终的结果需要的是：员工名字+部门信息+部门地点**

```sql
-- 查询 每个员工 所在的 部门 的地点
SELECT
	e.first_name,
	e.last_name,
	d.dept_name,
	d.location
FROM
	employees AS e
INNER JOIN
	departments AS d ON e.department = d.dept_name;
```

1. 根据查询逻辑可知，我们本次查询需要用到两张表，因此需要连结
2. 确认要用到的两张表：employees和departments，分别命名e 和 d
3. 连结条件：ON e.department = d.dept_name;
    1. 数据库会逐行检查 e表的department 字段 和 d表的dept_name字段是否相等
    2. 只有两个字段的值能匹配上的时候，两行数据才会被组合在一起，最终显示
4. 然后根据需求，确认查询的结果：名字+部门信息+部门地点，写在SELECT里

![image.png](/vivo50/resources/database/images/image.png)

## 左连结 LEFT JOIN

左连结的结果是返回左表（FROM后面的表）的所有行。

如果在右表中没有匹配的行，返回结果就是NULL

查询逻辑：在employees表钟遍历所有的员工，查询他们的姓名+部门信息，然后对于每一个员工的所在部门，都映射到department表中的dept_name，根据dept_name查询对应的dept_location

```sql
-- 查询所有员工的部门信息，以及部门的所在地点
SELECT
    e.first_name,
    e.last_name,
    d.dept_name,
    d.dept_localtion
FROM
    employees AS e
LEFT JOIN
		departments AS d on e.department = d.dept_name
```

所有的SQL语句 跟 内连结 差不多，唯一的区别是：多了一条`Tom Hanks null null`

与内连结相比，左连结一定会返回左表（`employees表`）内存在的数据，哪怕这个数据对应的在右表中不存在（不存在就会返回NULL）

因为在左表中，`Tom Hanks` 的部门是Marketing，在右表是没有对应的，所以返回NULL

![image.png](/vivo50/resources/database/images/image1.png)

## 右连结 RIGHT JOIN

右连结返回的是右表的所有的行

如果左表中没有匹配的行，就返回NULL

```sql
-- 查询所有部门、部门所在地址、部门内成员
SELECT
    d.dept_name, d.dept_localtion, e.first_name, e.last_name
FROM
    employees AS e
RIGHT JOIN departments AS d on e.department = d.dept_name;
```

跟上面的SQL语句大致是一样的，然后把LEFT JOIN改成RIGHT JOIN

这样，右表（`departments表`）里的全部数据都会返回。如果右表里的数据在左表找不到对应（也就是不存在）就返回NULL

因为在右表中，有一个IT部门，而左边没有一个员工是属于IT部门的，所以返回NULL

![image.png](/vivo50/resources/database/images/image2.png)

## 全外连结 FULL OUTER JOIN

如果说全内连结返回的是左表和右表的交集的话，全外连结返回的就是左表和右表的并集

MYSQL并不支持FULL OUTER JOIN，但是它跟LEFT JOIN UNION RIGHT JOIN是一样的

```sql
SELECT
    d.dept_name, d.dept_localtion, e.first_name, e.last_name
FROM employees AS e
LEFT JOIN departments AS d on e.department = d.dept_name

UNION

SELECT
    d.dept_name, d.dept_localtion, e.first_name, e.last_name
FROM employees AS e
RIGHT JOIN departments AS d on e.department = d.dept_name
```

会返回左表和右表共有的内容，如果左侧有而右侧没有（比如TOM）则右侧查询结果为NULL

如果右侧有的左侧没有（比如IT）则左侧查询结果为NULL

![image.png](/vivo50/resources/database/images/image3.png)

## 交叉连结

返回左表和右表的笛卡尔积，结果的行数 = 左表行数 * 右表行数，一般在没有on的情况下产生

```sql
SELECT
    e.first_name, e.last_name, d.dept_name, d.dept_localtion
FROM
    employees AS e
CROSS JOIN departments AS d;
```

如果在这个SQL语句最后加上on的话，结果跟INNER JOIN是一样的，所以有一个说法，CROSS JOIN是INNER JOIN的容错写法，这俩在 MySQL 中没有区别

# 子查询

SQL语句的一般执行过程：

- **FROM**: 首先，确定要从哪个表 (`employees`) 中获取数据。
- **WHERE**: 然后，**逐行**扫描表，根据 `WHERE` 子句中的条件来筛选行。
- **GROUP BY**: 对筛选后的行进行分组。
- **HAVING**: 筛选分组后的结果。
- **SELECT**: 最后，选取要显示的列，并在此阶段执行聚合函数（如 `AVG()`）。
- **ORDER BY**: 对最终结果进行排序。

## WHERE中的子查询

子查询时嵌套在另一个SQL查询（CRUD）中的查询

比如说我需要`查询公司中所有薪资高于平均薪资的员工`

这样的查询由两部分组成：先查询出AVG(salary)，然后用WHERE筛选出所有salary高于AVG(salary)的结果。思考下列是否可行：

```sql
SELECT
    first_name, last_name, salary
FROM
    employees
WHERE
    salary > AVG(salary);
```

AVG(salary)是一个聚合函数，表示的是”求平均值“，也就是说它只有计算功能，没有扫描功能

想要算出平均薪资，必须是 ”扫描全部薪资+求平均值“ 这两个过程

因此，AVG(salary)不能单独作为一个值，而必须：`SELECT AVG(salary) FROM employees`，扫描employees表，求出平均值以后，再进行WHERE筛选，WHERE是逐行过滤

因此正确的写法应该是

```sql
SELECT
    first_name, last_name, salary
FROM
    employees
WHERE
    salary > (SELECT AVG(salary) FROM employees);
```

## HAVING中的子查询

上面的WHERE查询中，子查询`SELECT AVG(salary) FROM employees`返回的是一个平均值

现在来一个`查询公司中所有薪资高于平均薪资的部门` 

之前员工的薪资查询，是统计`employees表`的`salary`列，计算平均值，再逐个比较

分析如下：

没有部门-薪资表，只有员工-员工薪资-部门表，首先需要统计所有部门的全部薪资，再计算每个部门的平均薪资，再找出部门平均薪资 高于 所有部门平均薪资 的 部门

1. 所有部门的平均薪资，就是所有员工的平均薪资，即`SELECT AVG(salary) FROM employees`
2. 每个部门的平均薪资：
    1. 将员工按部门进行分组`GROUP BY department`
    2. 计算每个分组的平均薪资`SELECT department, AVG(salary)`
3. 筛选出所有高于平均薪资的分组

```sql
-- 查询公司中所有薪资高于平均薪资的部门
SELECT
	department, AVG(salary) AS dprt_avg_salary
FROM
	employees
GROUP BY
	department
HAVING
	dprt_avg_salary > (SELECT AVG(salary) FROM employees)
```

## FROM中的子查询

FROM的子查询，会在FROM部分生成一个临时的表，这个表可以跟其他物理实表一样，支持所有表操作。

这个非常有用，因为我可以先对数据表进行复杂操作（聚合之类的），然后对处理结果进行二次查询
`假设我们想查询出员工数大于2的部门的平均薪资，同时还要显示出该部门的员工人数`

思路是，首先要按部门进行分组。然后对每个部门，计算员工人数和平均薪资

所以子查询的任务是对部门分组GROUP BY department，筛选出每个部门的员工和薪资，做成表

然后操作这张表就行

```sql
SELECT
    department_stats.department,
    department_stats.dprt_total_num,
    department_stats.avg_salary
FROM
    (
        SELECT
            department,
            AVG(salary) AS avg_salary,
            COUNT(id) AS dprt_total_num
        FROM
            employees
        GROUP BY
            department
    ) AS department_stats -- 必须为子查询创建的临时表指定一个别名
WHERE
    department_stats.dprt_total_num > 1;
```

这个表中的FROM句子里，生成了一个新的表，这个表的表名是department_stats，有三列，一列部门，两列聚合。然后我们所有的操作就在这个表里面进行

当然，上面这个查询有些复杂，主要是为了演示FROM子查询，其实上面的式子等价于

```sql
SELECT
    department,
    COUNT(id) AS dprt_total_num,
    AVG(salary) AS dprt_avg_salary
FROM
    employees
GROUP BY
    department
Having
    dprt_total_num > 1;
```

## SELECT中的子查询

# 数据库控制子语言DCL

DCL的核心是管理用户权限，即决定“谁能对数据做什么事”

主要命令事`GRANT（授予）`和`REVOKE（撤销）`

## 创建管理员

首先我们创建两个新用户：数据分析师 和 人事主管

```sql
-- 创建数据分析师用户
CREATE USER 'data_analyst'@'localhost' IDENTIFIED BY 'password123';

-- 创建人事主管用户
CREATE USER 'hr_admin'@'localhost' IDENTIFIED BY 'password456';
```

刚刚创建的这两个用户是没有任何权限的（哪怕是查询），会收到“拒绝访问”的报错

## 授予权限

首先明确业务需求：数据分析师 `不允许修改`employees表的任何数据，他只能从表中`读取数据`，人事主管 只允许`读取employees表`、`修改表中salary列` 

注意最小权限原则

综上：授予数据分析师 读取表的权限，授予人事主管 读取表 + 修改薪资 的权限

```sql
-- 授予 SELECT 权限 在 employees表 给 数据分析师
GRANT SELECT ON employees TO 'data_analyst'@'localhost';

-- 授予 SELECT权限 和 UPDATE(salary列)权限 在 employees表 给 人事主管
GRANT SELECT, UPDATE(salary) ON employees TO 'hr_admin'@'localhost';
```

## 撤销权限

假设数据分析师后来离职了，那么为了防止数据泄露，需要 撤销他的权限

```sql
-- 撤销 data_analyst 对 employees 表的 SELECT 权限
REVOKE SELECT ON employees FROM 'data_analyst'@'localhost';
```

# 事务控制语言TCL

TCL的核心是管理事务，确保一系列的事务要么全部成功，要么全部失败（原子性），从而保证数据的完整性 和 一致性。关于它的金融例子，可以在ACID讲解中提到

TCL的核心是：`START TRANSACTION`, `COMMIT`, `ROLLBACK`

当前假设一个原子事务：人事主管 要为员工 `Jane Smith` (id=2) 进行晋升调薪

这个事务为：将薪资从 `85000` 提升到 `95000`，部门从 `Engineering` 改为 `Senior Engineering`

## 成功提交COMMIT

```sql
-- 步骤1: 开启一个事务
START TRANSACTION;

-- 步骤2: 执行第一个更新操作 (加薪)
UPDATE employees SET salary = 95000 WHERE id = 2;

-- 步骤3: 执行第二个更新操作 (改部门)
UPDATE employees SET department = 'Senior Engineering' WHERE id = 2;

-- 步骤4: 确认所有操作都正确无误后，提交事务
COMMIT;
```

## 失败回滚ROLLBACK

```sql
-- 步骤1: 开启一个事务
START TRANSACTION;

-- 步骤2: 执行加薪操作
UPDATE employees SET salary = 105000 WHERE id = 3;

-- 步骤3: 发现操作有误（比如，加薪的金额不对或加错人了）
-- 决定撤销本次事务中的所有操作
ROLLBACK;
```