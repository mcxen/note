# openGauss-安全管理机制



## 三权分立

openGauss安装完成后会得到一个具有最高权限的超级用户。数据库超级用户的高权限意味着该用户可以做任何系统管理操作和数据管理操作,甚至可以修改数据库对象,包括接下来将要介绍的审计日志信息。对于企业管理来说, 手握超级用户权限的管理人员可以在无人知晓的情况下改变数据行为,这带来的后果是不可想象的。

在上文提到,初始化用户不允许远程登录,仅可本地登录。那么,在组织行为上由IT 部门严格监控拥有该权限的员工在本地的操作行为,就可有效避免诸如修改表中数据等“监守自盗”行为的发生。为了实际管理需要,在数据库内部就需要其他的管理员用户来管理整个系统,如果将大部分的系统管理权限都交给某一个用户来执行,实际上也是不合适的,因为这等同于超级用户。

为了很好地解决权限高度集中的问题,在openGauss系统中引入三权分立角色模型,如图5所示。三权分立角色模型最关键的三个角色为安全管理员、系统管理员和审计管理员。其中,安全管理员用于创建数据管理用户;系统管理员对创建的用户进行赋权;审计管理员则审计安全管理员、系统管理员、普通用户实际的操作行为。

![三权分立](https://raw.githubusercontent.com/52chen/imagebed2023/main/2021042510524970.png)

三权分立角色模型

通过三权分立角色模型实现权限的分派,且三个管理员角色独立行使权限,相互制约制衡。使得整个系统的权限不会因为权限集中而引入安全的风险。

事实上,产品使用过程中的安全是技术本身与组织管理双重保障的结果,在系统实现三权分立模型后,需要有三个对应的产品自然人分别握有对应的账户信息,以达到真正权限分离的目的。

## 角色

用户以某种角色身份登录系统后，通过基于角色的访问控制机制（`Role Based Access Control，RBAC`），可获得相应的数据库资源以及对应的对象访问权限。用户每次在访问数据库对象时，均需要使用存取控制机制（`Access Control List，ACL`）进行权限校验。常见的用户包括`超级用户、管理员用户和普通用户`，这些用户依据自身角色的不同，获取相应的权限，并依据ACL来实现对对象的访问控制。所有访问登录、角色管理、数据库运维操作等过程均通过独立的审计进程进行日志记录，以用于后期行为追溯。

### 数据库内置角色权限管理

openGauss提供了一组默认角色，以gs_role_开头命名。它们提供对特定的、通常需要高权限的操作的访问，可以将这些角色GRANT给数据库内的其他用户或角色，让这些用户能够使用特定的功能。在授予这些角色时应当非常小心，以确保它们被用在需要的地方。下表描述了内置角色允许的权限范围。

**表 3** 内置角色权限说明

| 角色                     | 权限描述                                                     |
| ------------------------ | ------------------------------------------------------------ |
| gs_role_copy_files       | 具有执行copy … to/from filename 的权限，但需要先打开GUC参数enable_copy_server_files。 |
| gs_role_signal_backend   | 具有调用函数pg_cancel_backend、pg_terminate_backend和pg_terminate_session来取消或终止其他会话的权限，但不能操作属于初始用户和PERSISTENCE用户的会话。 |
| gs_role_tablespace       | 具有创建表空间（tablespace）的权限。                         |
| gs_role_replication      | 具有调用逻辑复制相关函数的权限，例如kill_snapshot、pg_create_logical_replication_slot、pg_create_physical_replication_slot、pg_drop_replication_slot、pg_replication_slot_advance、pg_create_physical_replication_slot_extern、pg_logical_slot_get_changes、pg_logical_slot_peek_changes，pg_logical_slot_get_binary_changes、pg_logical_slot_peek_binary_changes。 |
| gs_role_account_lock     | 具有加解锁用户的权限，但不能加解锁初始用户和PERSISTENCE用户。 |
| gs_role_directory_create | 具有执行创建directory对象的权限，但需要先打开GUC参数enable_access_server_directory。 |
| gs_role_directory_drop   | 具有执行删除directory对象的权限，但需要先打开GUC参数enable_access_server_directory。 |

### 创建用户和角色

在openGauss内核中，用户和角色是基本相同的两个对象，通过CREATE ROLE和CREATE USER分别来创建角色和用户，两者语法基本相同。以CREATE ROLE的语法为例进行说明，其语法为（通过“`\h CREATE ROLE`”可以在系统中查询）：

```sql
CREATE ROLE role_name [ [ WITH ] option [ ... ] ] [ ENCRYPTED | UNENCRYPTED ] { PASSWORD | IDENTIFIED BY } { 'password' | DISABLE }; 
```

其中设置子句option的选项可以是：

```sql
 {SYSADMIN | NOSYSADMIN}

| {AUDITADMIN | NOAUDITADMIN} | {CREATEDB | NOCREATEDB}

| {USEFT | NOUSEFT} | {CREATEROLE | NOCREATEROLE}

| {INHERIT | NOINHERIT} | {LOGIN | NOLOGIN}

| {REPLICATION | NOREPLICATION} | {INDEPENDENT | NOINDEPENDENT}

| {VCADMIN | NOVCADMIN} | CONNECTION LIMIT connlimit

| VALID BEGIN 'timestamp' | VALID UNTIL 'timestamp'

| RESOURCE POOL 'respool' | USER GROUP 'groupuser'

| PERM SPACE 'spacelimit' | NODE GROUP logic_cluster_name

| IN ROLE role_name [, ...] | IN GROUP role_name [, ...]

| ROLE role_name [, ...] | ADMIN role_name [, ...]

| USER role_name [, ...] | SYSID uid

| DEFAULT TABLESPACE tablespace_name | PROFILE DEFAULT

| PROFILE profile_name | PGUSER
 
```

该命令仅可由具备 CREATE ROLE或者超级管理员权限的用户执行。对语法中涉及的关键参数做如下说明:

(1)ENCRYPTED | UNENCRYPTED

用于控制密码是否以密文形态存放在系统表中。目前该参数无实际作用,因为密码强制以密文形式存储。

(2)SYSADMIN | NOSYSADMIN

决定一个新创建的角色是否为“系统管理员”,默认为 NOSYSADMIN。

与该参数具有相类似概念的还包括 AUDITADMIN|NOAUDITADMIN、CREATEDB|NOCREATEDB、CREATEROLE|NOCREATEROLE,分别表示新创建的角色是否具有审计管理员权限,是否具有创建数据库权限,以及是否具有创建新角色的权限。

(3)USEFT|NOUSEFT

决定一个新角色是否能操作外表,包括新建外表、删除外表、修改外表和读写外表,默认为 NOUSEFT。

(4)INDEPENDENT|NOINDEPENDENT

定义私有、独立的角色。具有INDEPENDENT 属性的角色,管理员对其进行的控制、访问的权限被分离,具体规则如下:

- 未经INDEPENDENT 角色授权,管理员无权对其表对象进行增加、删除、查询、修改、复制、授权操作。
- 未经INDEPENDENT 角色授权,管理员无权修改INDEPENDENT 角色的继承关系。
- 管理员无权修改INDEPENDENT 角色的表对象的属主。
- 管理员无权去除INDEPENDENT 角色的INDEPENDENT 属性。
- 管理员无权修改INDEPENDENT 角色的数据库口令,INDEPENDENT 角色需要管理好自身口令,口令丢失无法重置。
- 管理员属性用户不允许定义修改为INDEPENDENT 属性。

(5)CONNECTION LIMIT

声明该角色可以使用的并发连接数量,默认值为-1,表示没有限制。

(6)PERM SPACE

设置用户使用空间的大小。