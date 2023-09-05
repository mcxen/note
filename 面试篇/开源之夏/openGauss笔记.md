

# openGauss安装

## Docker安装openGauss

### 拉取openGauss镜像

openGauss 5.0.0 有企业版和轻量版。轻量版定位是在软硬件资源受限场景下仍可使用openGauss，但是保留了企业版大部分的特性，轻量版通过参数默认关闭的特性：Ustore、Asp、增量检查点、双写、列存、段页式存储等，还有些不支持的特性如ORC文件访问、Kerberos安全校验、AI、全密态数据库、CM、OM。工具也进行了精简，保留了用户常用的gsql、gs_ctl、gs_guc、gs_dump、gs_restore等工具，和安装部署、升级的工具liteom。但是对于个人或者实验环境也足够了。
openGauss镜像这里我们用的[云和恩墨](https://hub.docker.com/r/enmotech/opengauss)的镜像。

![image-20230627162723920](https://raw.githubusercontent.com/52chen/imagebed2023/main/image-20230627162723920.png)

> 从3.0版本开始（包括3.0版本）
>
> - 容器采用[openGauss数据库Lite版本](https://opengauss.org/zh/docs/3.0.0-lite/docs/Releasenotes/版本介绍.html)
> - 默认启动后空载内存小于200M
> - 添加vi，ps，等基础命令
>
> 从2.0版本开始（包括2.0版本）
>
> - x86-64架构的openGauss运行在[Ubuntu 18.04操作系统](https://ubuntu.com/)中
> - ARM64架构的openGauss运行在[Debian 10 操作系统](https://www.debian.org/)中
>
> 在1.1.0版本之前（包括1.1.0版本）
>
> - x86-64架构的openGauss运行在[CentOS 7.6操作系统](https://www.centos.org/)中
> - ARM64架构的openGauss运行在[openEuler 20.03 LTS操作系统](https://openeuler.org/zh/)中

因为5.0.0是最新版本，所以latest就是5.0.0。这里，直接用的指定版本。

```
docker pull enmotech/opengauss:3.0.0
```

![img](https://raw.githubusercontent.com/52chen/imagebed2023/main/1680050717617732.png)

执行docker images查看下我们的已有镜像，enmotech/opengauss:latest 也在此列。

![image-20230726081138786](https://raw.githubusercontent.com/52chen/imagebed2023/main/image-20230726081138786.png)

### 开启实例

> 今天重启服务器时报了：
>
> ```sh
> Cannot connect to the Docker daemon at unix:///var/run/docker.sock. Is the docker daemon running?
> ```
>
> 翻译过来就是：无法连接到Docker守护进程在unix:///var/run/ Docker .sock。docker守护进程正在运行吗?
>
> 这个主要的问题就是docker没有启动起来导致的...
>
> > 启动docker并查看运行状态是否成功
> >
> > > ```sh
> > > [root@iZbp12f9404um3f6avsm29Z ~]# systemctl start docker
> > > [root@iZbp12f9404um3f6avsm29Z ~]# systemctl status docker
> > > ● docker.service - Docker Application Container Engine
> > > Loaded: loaded (/usr/lib/systemd/system/docker.service; disabled; vendor preset: disabled)
> > > Active: active (running) since Tue 2021-06-15 17:49:40 CST; 7min ago
> > >   Docs: http://docs.docker.com
> > > Main PID: 1320 (dockerd-current)
> > >  Tasks: 40
> > > Memory: 11.5M
> > > CGroup: /system.slice/docker.service
> > >         ├─1320 /usr/bin/dockerd-current --add-runtime docker-
> > > ```
> > >
> > > 看到running的标志，就是运行成功了...
> > >
> > > 为了避免日重启再次出现类似情况，增加一个开机自动启动docker...
> > >
> > > ```sh
> > > [root@iZbp12f9404um3f6avsm29Z ~]# systemctl enable docker
> > > Created symlink from /etc/systemd/system/multi-user.target.wants/docker.service to /usr/lib/systemd/system/docker.service.
> > > ```
>
> 
>
> 



```sh
--镜像拉取后就可以一条命令启动实例。GS_PASSWORD=Enmo@123 可以修改成自己熟悉的密码。
docker run --name opengauss3 --privileged=true -d -e GS_PASSWORD=Enmo@123 enmotech/opengauss:3.0.0
```

GS_PASSWORD：设置openGauss数据库的超级用户omm以及测试用户gaussdb的密码。如果要从容器外部（其它主机或者其它容器）连接则必须要输入密码。
GS_NODENAME：数据库节点名称，默认为gaussdb。
GS_USERNAME：数据库连接用户名，默认为gaussdb。
GS_PORT：数据库端口，默认为5432。

![image-20230726082444074](https://raw.githubusercontent.com/52chen/imagebed2023/main/image-20230726082444074.png)

因此访问opengauss数据库时，可以直接填写这个转发的端口地址

> ## Error
>
> 服了。
>
> 又出现错误了，打开了一会儿自动就退出了
>
> ![image-20230802194923376](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230802194923376.png)
>
> > 解决办法：使用老版本的3.0.0
> >
> > ```sh
> > docker run --name opengauss --privileged=true -d -e GS_PASSWORD=Enmo@123 enmotech/opengauss:3.0.0
> > ```
> >
> > ![截屏2023-08-02 19.57.04](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-08-02%2019.57.04.png)

除了GS_PASSWORD外都可以使用默认值。若要设定非默认值，和GS_PASSWORD一样使用 -e 设定。 

### 启动实例



```sh
[root@pekphisprb70593 ~]# docker ps -a --获取CONTAINER ID
[root@pekphisprb70593 ~]# docker start ab4f29ac64c8 
-- 如果已经停止了，那么就使用 docker start 启动一个已停止的容器
[root@pekphisprb70593 ~]# docker exec -it a66f26157cf7 /bin/bash 
--把这个命令里的 ab4f29ac64c8 修改为实际的ID
```

> 参数说明：
>
> - **-i**: 交互式操作。
> - **-t**: 终端。
> - **ab4f29ac64c8**: ID。
> - **/bin/bash**：放在镜像名后的是命令，这里我们希望有个交互式 Shell，因此用的是 /bin/bash。

![image-20230627163816834](https://raw.githubusercontent.com/52chen/imagebed2023/main/image-20230627163816834.png)





然后就和在普通服务器一样操作了。openGauss镜像配置了本地信任机制，因此在容器内连接数据库无需密码。

```sh
root@ab4f29ac64c8:/# su - omm
omm@ab4f29ac64c8:~$ gsql
gsql ((openGauss 5.0.0 build a07d57c3) compiled at 2023-03-29 03:09:38 commit 0 last mr  )
Non-SSL connection (SSL connection is recommended when requiring high-security)
Type "help" for help.

omm=# \du

```



使用docker安装确实简单又快捷。

### 卸载openGauss

Q：如果不需要了，要怎么卸载？
A：如果不需要了，就可以不需要卸载啊后处理，直接通过删除容器的方式删除数据库。

−删除容器

```
docker ps -a 
docker rm 实际的CONTAINER ID
```

−删除镜像

```
docker rmi -f 镜像ID
```



## 基础命令

## gs_ctl命令

切换为omm用户，`gs_ctl status -D /opt/software/openGauss/data/`为查看运行状态。

```sh
[omm@localhost data]$ gs_ctl status -D /opt/software/openGauss/data/
[2023-09-02 11:35:25.848][660605][][gs_ctl]: gs_ctl status,datadir is /opt/software/openGauss/data 
gs_ctl: server is running (PID: 4540)
/opt/software/openGauss/install/bin/gaussdb "-D" "/opt/software/openGauss/data"
```



### GSQL命令

示例1，使用omm用户连接到本机postgres数据库的15400端口。

```
gsql -d postgres -p 5432
```

示例2，使用jack用户连接到远程主机postgres数据库的15400端口。

```
gsql -h 10.180.123.163 -d postgres -U jack -p 15400
```

示例3，参数postgres和omm不属于任何选项时，分别被解释成了数据库名和用户名。

```
gsql postgres omm -p 15400
```

**等效于**

```
gsql -d postgres -U omm -p 15400
```

退出连接： \quit



**查看role角色** \du

![image-20230726114707898](https://raw.githubusercontent.com/52chen/imagebed2023/main/image-20230726114707898.png)



### 宿主终端命令**gs_guc ** docker不带这个

在 openGauss 中，可配置参数被称为 GUC（Grand Unified Configuration），数据库安装后，在数据目录（data）下自动生成三个配置文件（postgresql.conf、pg_hba.conf和pg_ident.conf）。gs_guc是GaussDB用于调整postgresql.conf和pg_hba.conf配置文件中参数值的工具。大部分参数都可以通过**`gs_guc set`** 和 **`gs_guc reload`** 来修改。

[参考1](https://blog.csdn.net/qq_42226855/article/details/108623745)

[参考2-openGauss使用手册](https://www.bookstack.cn/read/opengauss-1.1-zh/2bdba757242ae906.md)

语法 1

![wi](https://raw.githubusercontent.com/52chen/imagebed2023/main/20200916154810934.png)


说明：

使用-c parameter="‘value’“设置postgresql.conf参数值时，如果参数是一个字符串变量，则使用-c parameter=”‘value’"。如果value中含有特殊字符（如$），请转义后使用。
使用-A hba_parameter="value"设置pg_hba.conf参数值时，由于value中有空格，需用双引号引起来，但里面不能再用单引号。请注意参数值格式的差异。
不要在GaussDB启动过程中使用gs_guc reload命令修改配置参数，GaussDB启动成功后执行修改。



## 数据库连接



### DataStudio连接

下载链接直达：https://opengauss.obs.cn-south-1.myhuaweicloud.com/1.0.1/DataStudio_win_64.zip 。

找到Data Studio的解压目录，双击Data Studio.exe。会自动打开创建新连接的窗口，填写配置项。
名称自己定，主机和端口号为openGauss的服务器IP和安装时配置的端口，用户和密码为（上一步）自己创建的用户（不能用omm）及对应密码（Enmo@123）。我们普通测试环境就去勾选启用SSL。

首次连接报错误：

![image-20230726082909152](https://raw.githubusercontent.com/52chen/imagebed2023/main/image-20230726082909152.png)

进行远程连接前，需要在部署了数据库主节点的机器上设置允许客户端访问数据库，并[配置客户端接入认证](https://docs.opengauss.org/zh/docs/1.0.0/docs/Developerguide/配置客户端接入认证.html)。参考下叙方法





> 尝试了用网络的方法，无效：
>
> #### 配置postgrep.conf文件
>
> > 可以进入容器内部修改
> > 使用下面的命令以命令行的形式可以进入容器的内部对文件进行修改。
> >
> > ```awk
> > docker exec -it 容器ID /bin/bash
> > ```
> >
> > 不过里面没有vim，需要自行安装，
>
> **通过docker cp拷贝进行修改**
> 可以通过下面的代码将需要修改的文件拷贝出来，修改完成之后再拷贝回去。这种方式其实和第一种差不多，只是不用安装vim，但是容器被删除之后，修改过的内容也会失效。而且需要重启容器才能生效（好像）
>
> ```sh
> docker ps -a --获取CONTAINER ID
> #将容器中的文件拷贝出来
> sudo docker cp ab4f29ac64c8:/var/lib/opengauss/data/postgresql.conf /home
> sudo docker cp ab4f29ac64c8:/var/lib/opengauss/data/pg_hba.conf /home
> #将容器中的文件拷贝回去
> sudo docker cp /home/postgresql.conf  ab4f29ac64c8:/var/lib/opengauss/data/ --容器id：容器内部地址
> sudo docker cp /home/pg_hba.conf ab4f29ac64c8:/var/lib/opengauss/data/
> ```
>
> 
>
> - 修改 `postgresql.conf` 配置项 ssl 为 off，配置项 ssl 前面的 #号要去掉。
>
> - openGauss 默认只有 127.0.0.1 即 localhost 的端口监听，修改 `postgresql.conf `配置项 listen_addresses。
>   从 listen_addresses = ‘localhost’ 修改为 listen_addresses = ‘localhost,10.22.33.44’ ，其中 10.22.33.44 需要替换为 openGauss 所在节点实际 IP。
>   注意， 配置项 listen_addresses 前面的 #要去掉，逗号为英文逗号。
>
> - 修改 `pg_hba.conf `，增加 Data Studio 所在 Windows 机器的 IP 远程访问连接许可。新增一行然后保存退出。
>
>   <img src="https://raw.githubusercontent.com/52chen/imagebed2023/main/image-20230726090659229.png" alt="image-20230726090659229" style="zoom: 67%;" />
>
> 重启数据库：
>
> 重启之后失败了，容器无法打开了。。。
>
> 

#### [官方] 配置客户端接入认证

**精简版：**

```sh
root@ab4f29ac64c8:/# su - omm --连接了docker终端后，切换用户
omm@ab4f29ac64c8:~$ gsql -d postgres -p 5432 --连接到数据库5432端口
gsql ((openGauss 5.0.0 build a07d57c3) compiled at 2023-03-29 03:09:38 commit 0 last mr  )
Non-SSL connection (SSL connection is recommended when requiring high-security)
Type "help" for help.

openGauss=# CREATE USER jack PASSWORD 'Test@123';  --创建jack用户
openGauss=# 
gs_guc set -N all -I all -h "host all jack 192.168.198.1/32 sha256"; --分配访问ip地址
gs_guc set -N all -I all -h "host all jack 10.10.0.30/32 sha256"

```



1. 以操作系统用户omm登录数据库主节点。

2. 配置客户端认证方式，允许客户端以“jack”用户连接到本机，此处远程连接禁止使用“omm”用户（即数据库初始化用户）。

   例如，下面示例中配置允许IP地址为192.168.198.1的客户端访问本机。

   ```sql
   gs_guc set -N all -I all -h "host all jack 192.168.1.220/32 sha256" --分配访问ip地址
   ```

   > ![img](https://docs.opengauss.org/zh/docs/1.0.0/docs/Developerguide/public_sys-resources/icon-note.gif) **说明：**
   >
   > - 使用“jack”用户前，需先本地连接数据库，并在数据库中使用如下语句建立“jack”用户：
   >
   > ```sql
   > CREATE USER jack PASSWORD 'Test@123';  
   > ```
   >
   > - -N all表示openGauss的所有主机。
   > - -I all表示主机的所有实例。
   > - -h表示指定需要在“pg_hba.conf”增加的语句。
   > - all表示允许客户端连接到任意的数据库。
   > - jack表示连接数据库的用户。
   > - 10.10.0.30/32表示只允许IP地址为10.10.0.30的主机连接。此处的IP地址不能为openGauss内的IP，在使用过程中，请根据用户的网络进行配置修改。32表示子网掩码为1的位数，即255.255.255.255。
   > - sha256表示连接时jack用户的密码使用sha256算法加密。

这条命令在数据库主节点实例对应的“pg_hba.conf”文件中添加了一条规则，用于对连接数据库主节点的客户端进行鉴定。

“pg_hba.conf”文件中的每条记录可以是下面四种格式之一，四种格式的参数说明请参见[配置文件参考](https://docs.opengauss.org/zh/docs/1.0.0/docs/Developerguide/配置文件参考.html)。

```sql
local     DATABASE USER METHOD [OPTIONS]
host      DATABASE USER ADDRESS METHOD [OPTIONS]
hostssl   DATABASE USER ADDRESS METHOD [OPTIONS]
hostnossl DATABASE USER ADDRESS METHOD [OPTIONS]
```

因为认证时系统是为每个连接请求顺序检查“pg_hba.conf”里的记录的，所以这些记录的顺序是非常关键的。

还可以配置服务端远程连接：



#### 配置服务端远程连接

进行远程连接前，需要在部署了数据库主节点的机器上设置允许客户端访问数据库，并配置远程连接。

**操作步骤**

以下步骤需要在openGauss所在主机上执行。

1. 以操作系统用户omm登录数据库主节点。

2. 配置客户端认证方式，请参考[配置客户端接入认证](https://docs.opengauss.org/zh/docs/1.0.0/docs/Developerguide/配置客户端接入认证.html)。

3. 配置[listen_addresses](https://docs.opengauss.org/zh/docs/1.0.0/docs/Developerguide/连接设置.html#zh-cn_topic_0237124695_zh-cn_topic_0059777636_sed0adde99a3f47669f5d4ab557b36b35)，listen_addresses即远程客户端连接使用的数据库主节点ip或者主机名。

   使用如下命令查看数据库主节点目前的listen_addresses配置。

   ```sql
   gs_guc check -I all -c "listen_addresses";
   ```

   查询到的信息类似如下：

   ```sql
   expected guc information: plat1: listen_addresses=NULL: [/gaussdb/data/data_cn/postgresql.conf]
   gs_guc check: plat1: listen_addresses='localhost, 192.168.0.100': [/gaussdb/data/data_cn/postgresql.conf]
   
   Total GUC values: 1. Failed GUC values: 0.
   The value of parameter listen_addresses is same on all instances.
       listen_addresses='localhost, 192.168.0.100'
   ```

   使用如下命令把要添加的IP追加到listen_addresses后面，多个配置项之间用英文逗号分隔。例如，追加IP地址10.11.12.13。

   ```python
   gs_guc set -I all -c "listen_addresses='localhost,192.168.198.1,192.168.1.1'"
   ```

4. 执行如下命令重启openGauss。

   ```sql
   gs_om -t stop && gs_om -t start
   ```



#### 成功连接数据库：

![image-20230726151434772](https://raw.githubusercontent.com/52chen/imagebed2023/main/image-20230726151434772.png)







# SpringBoot连接OpenGauss数据库

## 1 在OpenGauss中创建数据库、用户和表

⚠️ 注意：假设先创建用户A，切换用户A后创建数据库DB，则数据库DB属于用户A；

### 1.1 登录OpenGauss

```
# 进入容器
docker exec -it opengauss /bin/bash

# 切换用户
su omm

# 进入OpenGauss
gsql -d postgres -p 5432


```

### 1.2 创建数据库

```sql
# 创建数据库，不指定表空间，默认创建的数据库都属于用户“omm"，可以使用“OWNER”设置所有者用户;
CREATE DATABASE db_department;

# 切换数据库，切换后前缀会变成“db_department=#”
\c db_department;

# 切换后变成如下
openGauss=# \c db_department;
Non-SSL connection (SSL connection is recommended when requiring high-security)
You are now connected to database "db_department" as user "omm".
db_department=# 

# 删除数据库的方法
DROP DATABASE db_department;

# 查看数据库
\l

# 查看表空间
\db

```

![截屏2023-08-02 20.09.17](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/截屏2023-08-02 20.09.17.png)

### 1.3 创建用户

注意：用户和数据库是有一定关联的

```sql
# 先切换到“db_department”数据库，如果已经切换跳过
\c db_department

# 将会在“db_department”数据库下在创建用户“admin_department”
CREATE USER admin_department WITH Sysadmin IDENTIFIED BY 'OpenGauss@123';
# 创建成功后后，会出现以下提示
NOTICE:  The encrypted password contains MD5 ciphertext, which is not secure.
CREATE ROLE

# 给用户授权
GRANT SELECT,INSERT,UPDATE,DELETE ON ALL TABLES IN SCHEMA public TO admin_department;
GRANT USAGE,SELECT ON ALL SEQUENCES IN SCHEMA admin_department TO admin_department;

# 删除用户的方法如下，使用CASCADE表示删除级联的信息，一般不要使用CASCADE；
DROP USER admin_department CASCADE;

# 查看用户
\du

# 切换用户的方法
\c - admin_department

# 退出
\q

# 使用“db_department”数据库和“admin_department”用户登录
gsql -d db_department -U admin_department -p 5432

```

### 1.4 创建表并插入数据

```sql
# 先切换到“db_department”数据库，如果已经切换跳过
\c db_department

# 切换到”admin_department“用户，如果已经切换跳过，密码是”OpenGauss@123“
# 如果不切换用户默认创建在”public“下
\c - admin_department

# 创建学生表
CREATE TABLE tb_class ( c_id INTEGER NOT NULL, c_name VARCHAR(40), c_age INTEGER );

# 插入数据
INSERT INTO tb_class (c_id, c_name, c_age) VALUES (1, '张三', 20), (2, '李四', 30), (3, 'wangwu', 40);

```

### 1.5 Data Studio连接OpenGauss

**连接OpenGauss**

> 配置一下：
>
> ```sh
> root@ab4f29ac64c8:/# su - omm --连接了docker终端后，切换用户
> omm@ab4f29ac64c8:~$ gsql -d postgres -p 5432 --连接到数据库5432端口
> gsql ((openGauss 5.0.0 build a07d57c3) compiled at 2023-03-29 03:09:38 commit 0 last mr  )
> Non-SSL connection (SSL connection is recommended when requiring high-security)
> Type "help" for help.
> 
> openGauss=# CREATE USER jack PASSWORD 'Test@123';  --创建jack用户
> openGauss=# gs_guc set -N all -I all -h "host all admin_department 127.0.0.1/32 sha256" --分配访问ip地址
> ```
>
> 

> ```properties
> ALTER USER admin_department SET ip TO '127.0.0.1/32';
> gs_guc set -N all -I all -h "host all admin_department 127.0.0.1/32 sha256";
> gs_guc set -I all -c "listen_addresses='localhost'"
> 用户：admin_department
> 密码：OpenGauss@123
> 数据库：db_department
> ```
>
> 

![在这里插入图片描述](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/ed8ee36753f6477d9a5812897c3ba60c-20230802203549050.png)

**连接成功**

![在这里插入图片描述](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/19a1286e3f4d4b469a1cf5d02d8049e4.png)

**查看数据**

![在这里插入图片描述](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/b9cb46e411e041ee875e5ccbd7eccc17.png)

## 2 SpringBoot连接数据库

使用postgresql驱动和MyBatis-Plus连接OpenGauss；

### 2.1 工程布局

![在这里插入图片描述](https://raw.githubusercontent.com/52chen/imagebed2023/main/2e61ce568978484b88c493e005a00606.png)

### 2.2 pom.xml文件

pom.xml文件如下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.2.5.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.example</groupId>
	<artifactId>test</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>demo</name>
	<description>Test project for Spring Boot</description>
	<properties>
		<java.version>11</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>com.baomidou</groupId>
			<artifactId>mybatis-plus-boot-starter</artifactId>
			<version>3.5.3</version>
		</dependency>

		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<version>42.5.1</version>
		</dependency>

		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<version>1.18.20</version>
			<scope>provided</scope>
		</dependency>

	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>

```

application.yml

```yaml
spring:
  datasource:
    # config openGauss
    url: jdbc:postgresql://192.168.108.200:5432/db_department
    username: admin_department
    password: OpenGauss@123
    driver-class-name: org.postgresql.Driver


# print logs
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    # 关闭数据库下划线自动转驼峰
    map-underscore-to-camel-case: false

```

### 2.3 创建实体类

在entitys目录下创建“TbClass.java”实体类

```java
package com.example.test.entitys;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
注意：下面的数据库字段和实体字段对应；
（1）如果，两个字段完全一样，则必须关闭application.yml中的map-underscore-to-camel-case属性；
    mybatis-plus:
        configuration:
            # 关闭数据库下划线自动转驼峰
            map-underscore-to-camel-case: false

（2）如果，数据库字段属性是下划线，实体字段是驼峰式，则必须开启application.yml中的map-underscore-to-camel-case属性（默认开启）
*/


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_class")
public class TbClass {
    private String c_id;
    private String c_name;
    private String c_age;
}

```

### 2.4 创建Mapper

在dao目录下的mpmapper目录下创建“TbClassMapper.java”

```java
package com.example.test.dao.mpmapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.test.entitys.TbClass;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbClassMapper extends BaseMapper<TbClass> {
}


```

### 2.5 创建Service

在service目录下创建“TbClassService.java”文件。

```java
package com.example.test.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.test.dao.mpmapper.TbClassMapper;
import com.example.test.entitys.TbClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TbClassService {

    @Autowired
    private TbClassMapper tbClassMapper;

    public List<TbClass> query(){
        QueryWrapper<TbClass> wrapper = new QueryWrapper<>();
        return this.tbClassMapper.selectList(wrapper);
    }
}


```

### 2.6 创建Controller

在controller目录下创建“TestController.java"文件。

```java
package com.example.test.controller;


import com.example.test.entitys.TbClass;
import com.example.test.service.TbClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/gadb")
public class TestController {

    @Autowired
    TbClassService tbClassService;

    @GetMapping("/query")
    public List<TbClass> queryData(){
        return tbClassService.query();
    }

}


```

### 2.7 创建Application

```java
package com.example.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

```

### 2.8 测试

浏览器中访问： http://localhost:8080/gadb/query

![在这里插入图片描述](https://raw.githubusercontent.com/52chen/imagebed2023/main/e85a278a85054613814bdd39a5440b18.png)

IDEA中打印的日志

![在这里插入图片描述](https://raw.githubusercontent.com/52chen/imagebed2023/main/c1bb59d192f1489381428d1dbfb687c0.png)



## springboot调试

### JDBC报错

如果直接使用 DataGrip 自带的 PostgreSQL 驱动程序，又会提示：`[08004] Invalid or unsupported by client SCRAM mechanisms.` 这是由于 openGauss 与 PostgreSQL 对于密码的 hash 方式不完全相同，SHA256 不兼容 PostgreSQL 的驱动。



![openGauss 使用的 SHA256 不兼容 PostgreSQL 的驱动](https://raw.githubusercontent.com/52chen/imagebed2023/main/%25E5%259B%25BE%25E7%2589%2587-3-1024x758.png)

openGauss 使用的 SHA256 不兼容 PostgreSQL 的驱动

## 解决方案

首先需要下载对应版本的 openGauss 驱动。以 2022 秋季学期北邮实验用的 3.1.0 版本为例，驱动在[*华为云的 obs 上存储*](https://opengauss.obs.cn-south-1.myhuaweicloud.com/3.1.0/x86_openEuler/openGauss-3.1.0-JDBC.tar.gz)。下载后打开 DataGrip（本文以安装官方中文语言包的描述为准），点击左侧“数据库资源管理器”的“+”标志，选择“驱动程序”导入新的驱动程序，再在“驱动程序文件”中将下载并解压的两个驱动程序的 `.jar` 包加入其中。出于兼容性考虑，一般“类”选择为“`org.postgresql.Driver`”。

接下来，添加一个 JDBC URL 模板，名称可以为任意（此处就叫“JDBC”），模板为：`jdbc:postgresql://{host}:{port}/{database}{user}{password}`

这样做是因为，DataGrip 只会要求用户填写已经在模板中出现的参数，比如 `{host}`、`{port}`、`{database}`、`{user}`、`{password}`，它会根据模板的情况智能生成一个对应的填写表，因此，DBeaver 的模板拿过来是不能使用的。后两个参数不会出现在 URL 当中，只作为引导 DataGrip 生成填写表使用。

当然，也可以根据 PostgreSQL 兼容的方式将所需内容填写到 JDBC URL 中，这样 DataGrip 会覆盖掉表中的内容，比如：`jdbc:postgresql://116.205.***.***:8000/postgres?user=userxxx&password=xxxxxx`

![在 DataGrip 中导入 openGauss 驱动程序文件](https://raw.githubusercontent.com/52chen/imagebed2023/main/%25E5%259B%25BE%25E7%2589%2587-7-1024x659.png)在 DataGrip 中导入 openGauss 驱动程序文件

![成功在 DataGrip 上连接到 openGauss](https://raw.githubusercontent.com/52chen/imagebed2023/main/%25E5%259B%25BE%25E7%2589%2587-6-1024x708.png)成功在 DataGrip 上连接到 openGauss
