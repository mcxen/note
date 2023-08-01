# Docker操作

## Docker指令

### 帮助命令



```sh
docker version # 显示 Docker 版本信息。
docker info # 显示 Docker 系统信息，包括镜像和容器数。
docker --help # 帮助
```



### 镜像命令

#### docker images

列出本地主机上的镜像

![image-20210603161520609](https://gcore.jsdelivr.net/gh/oddfar/static/img/Docker.assets/06.Docker-常用命令.assets/image-20210603161520609.png)

**解释：**

- REPOSITORY 镜像的仓库源 

- TAG 镜像的标签 

- IMAGE ID 镜像的ID 

- CREATED 镜像创建时间 

- SIZE 镜像大小



同一个仓库源可以有多个 TAG，代表这个仓库源的不同版本，我们使用 `REPOSITORY:TAG` 定义不同的镜像，如果你不定义镜像的标签版本，docker将默认使用 lastest 镜像！

#### docker search

搜索镜像：

docker search 某个镜像的名称 对应DockerHub仓库中的镜像

```sh
docker search mysql
```

可选项：

列出收藏数不小于指定值的镜像，例如

```sh
docker search mysql --filter=stars=1000
```

![image-20210603161853652](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20210603161853652.png)

- NAME: 镜像仓库源的名称
- DESCRIPTION: 镜像的描述
- OFFICIAL: 是否 docker 官方发布
- STARS: 类似 Github 里面的 star，表示点赞、喜欢的意思。
- AUTOMATED: 自动构建。



也通过Docker Hub 进行查找

比如https://hub.docker.com/search?q=mysql&type=image



#### docker pull

下载镜像

不写tag，默认是latest

```sh
docker pull mysql
```

指定版本下载

```sh
docker pull mysql:5.7
```

#### docker rmi

删除镜像

```sh
docker rmi -f 镜像id # 删除单个
docker rmi -f 镜像名:tag 镜像名:tag # 删除多个
```

删除全部

```sh
docker rmi -f $(docker images -qa)
```



### 容器命令

有镜像才能创建容器，我们这里使用 centos 的镜像来测试，就是虚拟一个 centos ！

```sh
docker pull centos
```



**容器启动**

```sh
docker run [OPTIONS] IMAGE [COMMAND][ARG...]
```

常用参数说明

| 参数          | 说明                                                         |
| ------------- | ------------------------------------------------------------ |
| --name="Name" | 给容器指定一个名字<br/><mark>之后再对容器操作，可以用这个name，相当于“别名”</mark> |
| -d            | 后台方式运行容器，并返回容器的id！                           |
| -i            | 以交互模式运行容器，通过和 -t 一起使用                       |
| -t            | 给容器重新分配一个终端，通常和 -i 一起使用                   |
| -P            | 随机端口映射（大写）                                         |
| -p            | 指定端口映射（小写），一般可以有四种写法                     |

查看镜像：

![image-20210603162841857](https://gcore.jsdelivr.net/gh/oddfar/static/img/Docker.assets/06.Docker-常用命令.assets/image-20210603162841857.png)

启动一个容器，使用centos进行用交互模式启动容器，在容器内执行/bin/bash命令！

```sh
docker run -it centos /bin/bash
```

![image-20210603163129380](https://gcore.jsdelivr.net/gh/oddfar/static/img/Docker.assets/06.Docker-常用命令.assets/image-20210603163129380.png)

退出容器：****

![image-20210603163202927](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20210603163202927.png)



**容器查看**

```sh
docker ps [OPTIONS]
```

常用参数说明

| 参数 | 说明                                          |
| ---- | --------------------------------------------- |
| -a   | 列出当前所有正在运行的容器 + 历史运行过的容器 |
| -l   | 显示最近创建的容器                            |
| -n=? | 显示最近n个创建的容器                         |
| -q   | 静默模式，只显示容器编号。                    |

**退出容器**

| 指令     | 说明           |
| -------- | -------------- |
| exit     | 容器停止退出   |
| ctrl+P+Q | 容器不停止退出 |



**启动停止容器**

| 指令                              | 说明         |
| --------------------------------- | ------------ |
| docker start (容器id or 容器名)   | 启动容器     |
| docker restart (容器id or 容器名) | 重启容器     |
| docker stop (容器id or 容器名)    | 停止容器     |
| docker kill (容器id or 容器名)    | 强制停止容器 |

**删除容器**

| 指令                             | 说明         |
| -------------------------------- | ------------ |
| docker rm 容器id                 | 删除指定容器 |
| docker rm -f $(docker ps -a -q)  | 删除所有容器 |
| docker ps -a -q\|xargs docker rm | 删除所有容器 |



**容器再启动**

- 命令

  ``` bash
  docker start id/name
  ```

启动之前停止关闭的容器



**后台启动容器**

```sh
docker run -d 容器名
```

启动centos，使用后台方式启动，例如：

```sh
docker run -d centos
```

问题： 使用docker ps 查看，发现容器已经退出了！

 解释：Docker容器后台运行，就必须有一个前台进程，容器运行的命令如果不是那些一直挂起的命 令，就会自动退出。

比如，你运行了nginx服务，但是docker前台没有运行应用，这种情况下，容器启动后，会立即自杀，因为他觉得没有程序了，所以最好的情况是，将你的应用使用前台进程的方式运行启动。



**清理停止的容器**

清理停止的容器: 

```sh
docker container prune
```

![image-20210604202728156](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20210604202728156.png)



**查看日志**

```sh
docker logs -f -t --tail 容器id
```

例子：我们启动 centos，并编写一段脚本来测试玩玩！最后查看日志

```sh
docker run -d centos /bin/sh -c "while true;do echo hello;sleep 1;done"
```

![image-20210603174412524](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20210603174412524.png)



查看日志：

```sh
docker logs 容器id
```



| 参数   | 说明             |
| ------ | ---------------- |
| -t     | 显示时间戳       |
| -f     | 打印最新的日志   |
| --tail | 数字显示多少条！ |

```sh
docker logs -tf --tail 10 87f5e5a2954e
```



![image-20210603174510568](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20210603174510568-20230729104540958.png)



- 停止运行

```sh
docker stop 87f5e5a2954e
```

![image-20210603175026851](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20210603175026851.png)

- **查看正在运行容器的进程信息**

```sh
docker top 容器id
```

- **查看容器/镜像的元数据**

```sh
docker inspect 容器id
```

![image-20210603175638262](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20210603175638262-20230729104535012.png)

**进入正在运行的容器**

命令一：

```sh
docker exec -it 容器id bashShell
```

例如：

```sh
[root@VM-0-6-centos ~]# docker ps
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
[root@VM-0-6-centos ~]# docker run -d centos /bin/sh -c "while true;do echo hello;sleep 1;done"
a9b967bdbc870bb039b69c76ddc3d3ce6aa87d57c51a8040e32224fb45576b28
[root@VM-0-6-centos ~]# docker ps
CONTAINER ID   IMAGE     COMMAND                  CREATED         STATUS         PORTS     NAMES
a9b967bdbc87   centos    "/bin/sh -c 'while t…"   8 seconds ago   Up 7 seconds             upbeat_haibt
[root@VM-0-6-centos ~]# docker exec -it a9b967bdbc87 /bin/bash
[root@a9b967bdbc87 /]# ps -ef
UID        PID  PPID  C STIME TTY          TIME CMD
root         1     0  0 10:01 ?        00:00:00 /bin/sh -c while true;do echo hello;sleep 1;done
root        37     0  0 10:02 pts/0    00:00:00 /bin/bash
root        59     1  0 10:02 ?        00:00:00 /usr/bin/coreutils --coreutils-prog-shebang=sleep /usr/bin/sleep 1
root        60    37  0 10:02 pts/0    00:00:00 ps -ef
```

退出容器终端，不会导致容器的停止

```sh
[root@a9b967bdbc87 /]# exit
exit
[root@VM-0-6-centos ~]# docker ps
CONTAINER ID   IMAGE     COMMAND                  CREATED         STATUS         PORTS     NAMES
a9b967bdbc87   centos    "/bin/sh -c 'while t…"   7 minutes ago   Up 7 minutes             upbeat_haibt
```

命令二：

```sh
docker attach 容器id
```

测试：

```sh
[root@VM-0-6-centos ~]# docker images
REPOSITORY    TAG       IMAGE ID       CREATED        SIZE
hello-world   latest    d1165f221234   2 months ago   13.3kB
centos        latest    300e315adb2f   5 months ago   209MB
[root@VM-0-6-centos ~]# docker  run -it -d  centos /bin/bash
7f9ead6f906b3c691d29866236414e1808d194462ed0839c8ee5c947d731ed57
[root@VM-0-6-centos ~]# docker ps
CONTAINER ID   IMAGE     COMMAND       CREATED          STATUS         PORTS     NAMES
7f9ead6f906b   centos    "/bin/bash"   10 seconds ago   Up 9 seconds             nervous_mcclintock
[root@VM-0-6-centos ~]# docker attach 7f9ead6f906b
[root@7f9ead6f906b /]# echo "hello"
hello
[root@7f9ead6f906b /]# exit
exit
[root@VM-0-6-centos ~]# docker ps
CONTAINER ID   IMAGE     COMMAND   CREATED   STATUS    PORTS     NAMES
[root@VM-0-6-centos ~]# 
```





**区别 ：**

- exec 是在容器中打开新的终端，并且可以启动新的进程 

- attach 直接进入容器启动命令的终端，不会启动新的进程

推荐大家使用 `docker exec` 命令，



**容器内拷贝文件到主机上**

```sh
docker cp 容器id:容器内路径 目的主机路径
```

例如：

```sh
docker cp 7f9ead6f906b:/home/f1 /home
```



### 常用命令总结

![img_29](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/img_29.png)



| 命令    | 官方说明                                                     | 解释                                                         |
| ------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| attach  | Attach local standard input, output, and error streams to a running container | 当前 shell 下 attach 连接指定运行镜像                        |
| build   | Build an image from a Dockerfile                             | 通过 Dockerfile 定制镜像                                     |
| commit  | Create a new image from a container's changes                | 提交当前容器为新的镜像                                       |
| cp      | Copy files/folders between a container and the local filesystem | 从容器中拷贝指定文件或者目录到宿主机中                       |
| create  | Create a new container                                       | 创建一个新的容器，同 run，但不启动容器                       |
| diff    | Inspect changes to files or directories on a container's filesystem | 查看 docker 容器变化                                         |
| events  | Get real time events from the server                         | 从 docker 服务获取容 器实时事件                              |
| exec    | Run a command in a running container                         | 在已存在的容器上运行命令                                     |
| export  | Export a container's filesystem as a tar archive             | 导出容器的内 容流作为一个 tar 归档文件[对应 import ]         |
| history | Show the history of an image                                 | 展示一个镜像形成历史                                         |
| images  | List images                                                  | 列出系统当前镜像                                             |
| import  | Import the contents from a tarball to create a filesystem image | 从 tar包中的内容创建一个新的文件系统映像[对应export]         |
| info    | Display system-wide information                              | 显示系统相关信息                                             |
| inspect | Return low-level information on Docker objects               | 查看容器详细信息                                             |
| kill    | Kill one or more running containers                          | 杀掉 指定 docker 容器                                        |
| load    | Load an image from a tar archive or STDIN                    | 从一个 tar 包中加载一 个镜像[对应 save]                      |
| login   | Log in to a Docker registry                                  | 登陆一个 docker 源服务器                                     |
| logout  | Log out from a Docker registry                               | 从当前 Docker registry 退出                                  |
| logs    | Fetch the logs of a container                                | 输出当前容器日志信息                                         |
| pause   | Pause all processes within one or more containers            | 暂停容器                                                     |
| port    | List port mappings or a specific mapping for the container   | 查看映射端口对应的容器内部源端口                             |
| ps      | List containers                                              | 列出容器列表                                                 |
| pull    | Pull an image or a repository from a registry                | 从docker镜像源服务器拉取指定镜像或者库镜像                   |
| push    | Push an image or a repository to a registry                  | 推送指定镜像或者库镜像至docker源服务器                       |
| rename  | Rename a container                                           | 给一个容器改名                                               |
| restart | Restart one or more containers                               | 重启运行的容器                                               |
| rm      | Remove one or more containers                                | 移除一个或者多个容器                                         |
| rmi     | Remove one or more images                                    | 移除一个或多个镜像[无容器使用该镜像才可删除，否则需删除相关容器才可继续或 -f 强制删除] |
| run     | Run a command in a new container                             | 创建一个新的容器并运行 一个命令                              |
| save    | Save one or more images to a tar archive (streamed to STDOUT by default) | 保存一个镜像为一个 tar 包[对应 load]                         |
| search  | Search the Docker Hub for images                             | 在 docker hub 中搜 索镜像                                    |
| start   | Start one or more stopped containers                         | 启动容器                                                     |
| stats   | Display a live stream of container(s) resource usage statistics | 显示容器资源使用统计信息的实时信息                           |
| stop    | Stop one or more running containers                          | 停止容器                                                     |
| tag     | Create a tag TARGET_IMAGE that refers to SOURCE_IMAGE        | 给源中镜像打标签                                             |
| top     | Display the running processes of a container                 | 查看容器中运行的进程信 息                                    |
| unpause | Unpause all processes within one or more containers          | 取消暂停容器                                                 |
| update  | Update configuration of one or more containers               | 更新容器配置                                                 |
| version | Show the Docker version information                          | 查看 docker 版本号                                           |
| wait    | Block until one or more containers stop, then print their exit codes | 截取容器停止时的退出状态值                                   |



### Docker传输文件

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

#### Docker容器向宿主机传送文件

格式:

```bash
docker cp container_id:<docker容器内的路径> <本地保存文件的路径>
```

比如:

```bash
docker cp 10704c9eb7bb:/root/test.text /home/vagrant/test.txt
```

#### 宿主机向Docker容器传送文件

格式:

```bash
docker cp 本地文件的路径 container_id:<docker容器内的路径>
```

比如:

```bash
docker cp  /home/vagrant/test.txt 10704c9eb7bb:/root/test.text
```

#### 其它

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





