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
docker pull enmotech/opengauss:3.0.0
```

![img](https://raw.githubusercontent.com/52chen/imagebed2023/main/1680050717617732.png)

执行docker images查看下我们的已有镜像，enmotech/opengauss:3.0.0 也在此列。

![img](https://raw.githubusercontent.com/52chen/imagebed2023/main/1680050717595077.png)

### 开启实例

1. 镜像拉取后就可以一条命令启动实例。GS_PASSWORD=Enmo@123 可以修改成自己熟悉的密码。

   ```sh
   docker run --name opengauss --privileged=true -d -e GS_PASSWORD=Enmo@123 enmotech/opengauss:3.0.0
   ```

   

   GS_PASSWORD：设置openGauss数据库的超级用户omm以及测试用户gaussdb的密码。如果要从容器外部（其它主机或者其它容器）连接则必须要输入密码。
   GS_NODENAME：数据库节点名称，默认为gaussdb。
   GS_USERNAME：数据库连接用户名，默认为gaussdb。
   GS_PORT：数据库端口，默认为5432。

   除了GS_PASSWORD外都可以使用默认值。若要设定非默认值，和GS_PASSWORD一样使用 -e 设定。 

2. 进入容器，测试连接

   ```sh
   [root@pekphisprb70593 ~]# docker ps --获取CONTAINER ID
   [root@pekphisprb70593 ~]# docker exec -it 1d54ee4a5f40 /bin/bash  
   --把这个命令里的 1d54ee4a5f40 修改为实际的ID
   ```

   ![image-20230627163816834](https://raw.githubusercontent.com/52chen/imagebed2023/main/image-20230627163816834.png)

   

3. 然后就和在普通服务器一样操作了。openGauss镜像配置了本地信任机制，因此在容器内连接数据库无需密码。

   ```sh
   root@ab4f29ac64c8:/# su - omm
   omm@ab4f29ac64c8:~$ gsql
   gsql ((openGauss 5.0.0 build a07d57c3) compiled at 2023-03-29 03:09:38 commit 0 last mr  )
   Non-SSL connection (SSL connection is recommended when requiring high-security)
   Type "help" for help.
   
   omm=# 
   ```

   

   使用docker安装确实简单又快捷。

### 基础命令-gsql

示例1，使用omm用户连接到本机postgres数据库的15400端口。

```
gsql -d postgres -p 15400
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

### 卸载

Q：如果不需要了，要怎么卸载？
A：如果不需要了，就可以不需要卸载啊后处理，直接通过删除容器的方式删除数据库。

−删除容器

```
docker ps -a ``docker rm 实际的CONTAINER ID
```

−删除镜像

```
docker rmi -f 镜像ID
```