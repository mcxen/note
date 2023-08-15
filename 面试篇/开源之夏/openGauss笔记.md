# openGauss笔记

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
docker pull enmotech/opengauss:latest
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

1. ```sh
   --镜像拉取后就可以一条命令启动实例。GS_PASSWORD=Enmo@123 可以修改成自己熟悉的密码。
   docker run --name opengauss --privileged=true -d -e GS_PASSWORD=Enmo@123 enmotech/opengauss:latest
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

2. 进入容器，测试连接

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

   

   

3. 然后就和在普通服务器一样操作了。openGauss镜像配置了本地信任机制，因此在容器内连接数据库无需密码。

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



### 宿主终端命令**gs_guc**

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







```sh

```

