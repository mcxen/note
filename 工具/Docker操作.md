# Docker操作



## Docker传输文件

获取ID

```sh
mcxw@mcxAir Downloads % docker ps
CONTAINER ID   IMAGE     COMMAND                   CREATED      STATUS          PORTS                               NAMES
fa11f64673b8   mysql     "docker-entrypoint.s…"   2 days ago   Up 29 minutes   33060/tcp, 0.0.0.0:3340->3306/tcp   mysql-slave
d171ddbbbfd9   mysql     "docker-entrypoint.s…"   2 days ago   Up 18 minutes   33060/tcp, 0.0.0.0:3339->3306/tcp   mysql-master
mcxw@mcxAir Downloads % docker inspect -f '{{.Id}}' mysql-master;                                                  
d171ddbbbfd92676deb5c5d2ed55cd307c4b97b13b65c0f0e3a3d424ad8282f6
```

下面这个就是Id了。

### Docker容器向宿主机传送文件

格式:

```bash
docker cp container_id:<docker容器内的路径> <本地保存文件的路径>
```

比如:

```bash
docker cp 10704c9eb7bb:/root/test.text /home/vagrant/test.txt
```

### 宿主机向Docker容器传送文件

格式:

```bash
docker cp 本地文件的路径 container_id:<docker容器内的路径>
```

比如:

```bash
docker cp  /home/vagrant/test.txt 10704c9eb7bb:/root/test.text
```

### 其它

在宿主机(本机)中通过`docker cp --help` 查看 `docker cp`的用法

```bash
vagrant@centos:~$ docker cp --help

Usage:	docker cp [OPTIONS] CONTAINER:SRC_PATH DEST_PATH|-
	docker cp [OPTIONS] SRC_PATH|- CONTAINER:DEST_PATH

Copy files/folders between a container and the local filesystem

Options:
  -a, --archive       Archive mode (copy all uid/gid information)
  -L, --follow-link   Always follow symbol link in SRC_PATH
```



## Docker - MYSQL

### 创建Docker镜像

```sh
mcxw@mcxAir ~ % docker search mysql
NAME                            DESCRIPTION                                      STARS     OFFICIAL   AUTOMATED
mysql                           MySQL is a widely used, open-source relation…   14168     [OK]       


mcxw@mcxAir ~ % docker pull mysql:8.0.31
8.0.31: Pulling from library/mysql
12a06ca91af8: Pull complete 
1fec1cb1944f: Pull complete 
aede38c79379: Pull complete 
51b980849561: Pull complete 

mcxw@mcxAir ~ % docker images
REPOSITORY   TAG       IMAGE ID       CREATED        SIZE
mysql        8.0.31    7b6f3978ca29   5 months ago   550MB

$ docker run -itd --name mysql-master -p 3339:3306 -e MYSQL_ROOT_PASSWORD=Mysql2486 mysql

docker run -itd --name mysql-slave -p 3340:3306 -e MYSQL_ROOT_PASSWORD=Mysql2486 mysql

mysql -h localhost -P 3309 -u root -pMysql2486  # 这里我设置的root用户的密码是Mysql2486


```

> ```sh
> docker run -itd --name mysql-test -p 3306:3309 -e MYSQL_ROOT_PASSWORD=Mysql2486 mysql
> ```
>
> 参数说明：
> 这个命令是在 Docker 上运行一个名为 mysql-test 的容器，使用的镜像是 mysql。其中：
>
> - `-itd` 表示在交互模式下运行容器，并且在后台运行。
> - `--name mysql-test` 给容器取一个名字叫做 mysql-test。
> - `-p 3306:3309` 将主机的 3306 端口映射到容器的 3309 端口上。
> - `-e MYSQL_ROOT_PASSWORD=Mysql2486` 设置容器中 MySQL 的 root 用户的密码为 Mysql2486。
>
> 这个命令的作用是在 Docker 上启动一个 MySQL 数据库服务器，并将其绑定到主机的 3306 端口上，以便可以从主机上访问 MySQL 服务器。



Docker两个主机开启完成。

![截屏2023-05-30 20.59.51](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/截屏2023-05-30 20.59.51.png)



设置：/etc/my.cnf

```sh
log-bin=mysql-bin #启动二进制
server-id=100 #主机唯一id



## 开启二进制日志功能，以备Slave作为其它Slave的Master时使用  
log-bin=mysql-slave-bin 
## relay_log配置中继日志  
relay_log=edu-mysql-relay-bin
server-id=101 #从机唯一id
```



![截屏2023-05-30 21.32.12](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/截屏2023-05-30 21.32.12.png)

### 配置Docker主机从机模式

#### 创建用户

新建用户

```mysql
GRANT REPLICATION SLAVE ON *.* to 'mcx'@'%' identified by 'Mysql2486@';
```

注：上面SQL的作用是创建一个用户xiaoming，密码为Root@123456，并且给xiaoming用户授予REPLICATION SLAVE 权限。常用于建立复制时所需要用到的用户权限，也就是slave必须被master授权具有该权限的用户，才能通过该用户复制。

这里运行之后抱错了

```sh
 You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'identified by 
```

**原因分析：**此版的的mysql版本把将创建账户和赋予权限分开了。

创建账户：create user‘用户名’@'访问主机' identified by ‘密码：

赋予权限：grant 权限列表 on 数据库 to“用户名’@访问主机 with grant option这个选项表示该用户可以将自己拥有的权限授权给别人

```sh
#创建账户
create user 'mcx'@'%' identified by 'Mysql2486';

#赋予权限，with grant option这个选项表示该用户可以将自己拥有的权限授权给别人
grant all privileges on *.* to 'mcx' @'%' with grant option;

#改密码&授权超用户，flush privileges 命令本质上的作用是将当前user和privilige表中的用户信息/权限设置从mysql库(MySQL数据 
flush privileges;
```

#### 启动从机模式



查看主机状态`show master status;`

```sh
mysql> show master status\G;
*************************** 1. row ***************************
             File: mysql-bin.000001
         Position: 840
     Binlog_Do_DB: 
 Binlog_Ignore_DB: 
Executed_Gtid_Set: 
1 row in set (0.00 sec)
```



```mysql
change master to master_host='172.17.0.3',
master_user='mcx',
master_password='Mysql2486',
master_port=3306,
master_log_file='mysql-bin.000002',
master_log_pos=157;
```



> 命令说明：
> master_host ：Master 的地址，指的是容器的独立 ip, 可以通过 
>
> ```sh
> mcxw@mcxAir ~ %  docker inspect --format='{{.NetworkSettings.IPAddress}}' mysql-master
> 172.17.0.2
> mcxw@mcxAir ~ %  docker inspect --format='{{.NetworkSettings.IPAddress}}' mysql-slave
> 172.17.0.3
> ```
>
> 容器名称 | 容器 id 查询容器的 ip
>
> master_port：Master 的端口号，指的是容器的端口号
> master_user：用于数据同步的用户
> master_password：用于同步的用户的密码
> master_log_file：指定 Slave 从哪个日志文件开始复制数据，即上文中提到的 File 字段的值
> master_log_pos：从哪个 Position 开始读，即上文中提到的 Position 字段的值
> master_connect_retry：如果连接失败，重试的时间间隔，单位是秒，默认是 60 秒
> 在 Slave 中的 mysql 终端执行 show slave status \G; 用于查看主从同步状态。



查看一下从机状态

```sql
start slave;
show slave status;
```

使用 start slave 开启主从复制过程后，如果 SlaveIORunning 一直是 Connecting，则说明主从复制一直处于连接状态，这种情况一般是下面几种原因造成的，我们可以根据 Last_IO_Error 提示予以排除。

**解决方法：**

```sh
 mysql -u mcx -pMysql2486 -h 172.17.0.3 -P3306 --get-server-public-key
```

在这种情况下，服务器将RSA公钥发送给客户端，后者使用它来加密密码并将结果返回给服务器。插件使用服务器端的RSA私钥解密密码，并根据密码是否正确来接受或拒绝连接。

重新在从库配置change masrer to并且start slave，复制可以正常启动：

在slave上看到这样就算搭建好了
```sh
mysql> show slave status\G;
*************************** 1. row ***************************
               Slave_IO_State: Waiting for source to send event
                  Master_Host: 172.17.0.2
                  Master_User: mcx
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: mysql-bin.000001
          Read_Master_Log_Pos: 1052
               Relay_Log_File: edu-mysql-relay-bin.000002
                Relay_Log_Pos: 508
        Relay_Master_Log_File: mysql-bin.000001
             Slave_IO_Running: Yes
            Slave_SQL_Running: Yes
             Master_Server_Id: 100
                  Master_UUID: 29a04b7f-ff8b-11ed-8405-0242ac110002
             Master_Info_File: mysql.slave_master_info
                    SQL_Delay: 0
          SQL_Remaining_Delay: NULL
      Slave_SQL_Running_State: Replica has read all relay log; waiting for more updates
           Master_Retry_Count: 86400
1 row in set, 1 warning (0.00 sec)

ERROR: 
No query specified
```





### 配置MAC主机Docker从机模式

#### 创建用户

```sh
#创建账户
create user 'mcx'@'%' identified by 'Mysql2486';

#赋予权限，with grant option这个选项表示该用户可以将自己拥有的权限授权给别人
grant all privileges on *.* to 'mcx' @'%' with grant option;

#改密码&授权超用户，flush privileges 命令本质上的作用是将当前user和privilige表中的用户信息/权限设置从mysql库(MySQL数据 
flush privileges;
```

#### 启动从机模式

主机查看：

查看主机状态`show master status;`

```sh
mysql> show master status\G;
*************************** 1. row ***************************
             File: mysql-bin.000001
         Position: 840
     Binlog_Do_DB: 
 Binlog_Ignore_DB: 
Executed_Gtid_Set: 
1 row in set (0.00 sec)
```

slave设置：

```sh
mysql -u mcx -pMysql2486 -h 127.0.0.1 --get-server-public-key
```





```mysql
change master to master_host='127.0.0.1',
master_user='mcx',
master_password='Mysql2486',
master_port=3306,
master_log_file='mysql-bin.000001',
master_log_pos=157;
```



> .导出整个数据库
> mysqldump -u 用户名 -p 数据库名 > 导出的文件名
> mysqldump -u dbuser -p dbname > dbname.sql
>
> 2.导出一个表
> mysqldump -u 用户名 -p 数据库名 表名> 导出的文件名
> mysqldump -u dbuser -p dbname users> dbname_users.sql
>
> 3.导出一个数据库结构
> mysqldump -u dbuser -p -d --add-drop-table dbname >d:/dbname_db.sql
> -d 没有数据 --add-drop-table 在每个create语句之前增加一个drop table
>
> 4.导入数据库
> 常用source 命令
> 进入mysql数据库控制台，如
> mysql -u root -p
> mysql>use 数据库
> 然后使用source命令，后面参数为脚本文件(如这里用到的.sql)
> mysql>source d:/dbname.sql
