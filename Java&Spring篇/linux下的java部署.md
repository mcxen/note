# linux下的java部署

## jar命令简介

java部署jar包可以使用 java -jar命令，比如：

```cmd
java -jar demo.jar
```

执行上述命令后，JAR 包中的程序将在 Linux 系统中运行。

注：在运行 JAR 包之前，确保你的 JAR 文件是可执行的，并且包含了正确的类和依赖项。如果 JAR 包依赖于其他库或配置文件，确保它们也在正确的位置可用。

java -jar 是 Java 命令的一种形式，用于运行 JAR（Java Archive）文件。-jar 参数告诉 Java 虚拟机直接从 JAR 文件中执行主类。

除了-jar参数外，java命令还有许多其他常用的参数，以下是一些常见的参数：

- -cp或-classpath：指定类路径，用于指定要搜索类的目录或 JAR 文件。
- -server：启用服务器模式的 JVM，适用于长时间运行的服务器应用程序。
- -XX:+HeapDumpOnOutOfMemoryError：在发生内存溢出时生成堆转储文件。
- -XX:+PrintGCDetails：打印详细的垃圾回收信息。
- -Xmx：指定Java堆的最大内存大小。例如，-Xmx512m表示将最大堆内存设置为512MB。通过调整堆内存大小，可以优化应用程序的性能和内存使用情况。
- -Xms：指定Java堆的初始内存大小。例如，-Xms256m表示将初始堆内存设置为256MB。这个参数可以帮助您在启动应用程序时控制初始内存分配的大小。
- -Xss：指定线程栈的大小。例如，-Xss1m表示将线程栈大小设置为1MB。线程栈用于存储线程的局部变量和方法调用信息。通过调整线程栈的大小，可以控制应用程序的并发性能和内存使用情况。
- -XX:MetaspaceSize=256m参数，您可以指定元空间的初始大小为256MB。这意味着在应用程序启动时，虚拟机会预分配256MB的内存用于存储类的元数据。
- -XX:MaxMetaspaceSize=：指定元空间的最大大小。元空间用于存储类的元数据信息。例如，-XX:MaxMetaspaceSize=256m表示将元空间的最大大小设置为256MB。适当设置元空间的大小可以避免类加载和元数据溢出的问题。
- -D=：设置系统属性。例如，-Djava.library.path=/path/to/libs设置java.library.path系统属性为/path/to/libs。您可以使用这个参数来配置应用程序的一些属性，例如文件路径、日志级别等。
- -verbose:class：打印类加载信息。使用这个参数可以了解应用程序在运行过程中加载的类的详细信息，包括类的名称、来源等。
- -verbose:gc：打印垃圾回收信息。通过使用这个参数，您可以了解应用程序的垃圾回收情况，包括垃圾回收器的使用情况、回收的对象数量等。
- -Dfile.encoding=：设置默认的文件编码。例如，-Dfile.encoding=UTF-8表示使用UTF-8编码来读取和写入文件。正确设置文件编码可以确保应用程序能够正确处理不同字符集的文本数据。
- &：符号&用于将命令放在后台运行。这意味着命令会在后台执行，不会阻塞终端，你可以继续在终端中执行其他操作。

示例如下：

```
java -jar -Xms4096m -Xmx4096m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m /opt/myapp/myapp.jar
```

命令如上，我们同时使用了多个参数。-Xms4096m设置最大堆内存为4096MB，-Xmx4096m设置初始堆内存为4096MB，-XX:MetaspaceSize=256m 将元空间的初始大小设置为256MB，-XX:MaxMetaspaceSize=256m 将元空间的最大大小设置为256MB,最后，通过-jar选项指定要运行的JAR文件为myapp.jar。

```
java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/path/to/dump/file.hprof -jar MyProgram.jar
```

命令如上，堆转储文件将会被生成在指定的路径/path/to/dump/下。

启用堆转储可以帮助你在遇到内存问题时进行故障排查和分析，确定可能的内存泄漏或其他与内存使用相关的问题。但在实际使用中，还需要结合其他的监控和分析工具来全面了解程序的内存使用情况。

```
java -jar demo.jar &
```

命令如上，通过使用&，可以在不中断其他工作的情况下运行 JAR 文件。这种方式可以避免打断后程序停止运行的问题，但是如果关闭当前窗口后程序会停止运行。

## nohup 命令

nohup 是一个在 Linux 和 Unix 系统中常用的命令，它的主要作用是让命令在后台运行，并且阻止该命令被终端的挂断信号（HUP）打断。

```bash
nohup java -jar demo.jar &
```

命令如上，即使关掉命令窗口，后台程序demo.jar也会一直执行。

注：nohup并不能保证命令在系统重启或其他情况下仍然继续运行。如果你需要确保命令在系统重启后仍然运行，可能需要使用其他的方法，如守护进程或系统服务。

> 如：使用一个Systemd常见守护进程
>
> 要将Java应用程序（如`demo.jar`）设置为守护进程，可以使用以下几种方法之一。守护进程是一种在后台运行的进程，通常在系统启动时启动，并在系统关闭时停止。
>
> ### 使用 Systemd 创建守护进程
>
> `systemd` 是现代 Linux 系统上的系统和服务管理器。你可以通过创建一个 `systemd` 服务来将你的 Java 应用程序设置为守护进程。
>
> #### 步骤如下：
>
> 1. 创建一个服务文件，比如 `demo.service`：
>     ```sh
>     sudo nano /etc/systemd/system/demo.service
>     ```
>
> 2. 在文件中添加以下内容：
>     ```ini
>     [Unit]
>     Description=Demo Java Application
>     After=network.target
>     
>     [Service]
>     Type=simple
>     ExecStart=/usr/bin/java -jar /path/to/demo.jar
>     User=your-username
>     WorkingDirectory=/path/to
>     Restart=always
>     StandardOutput=syslog
>     StandardError=syslog
>     SyslogIdentifier=demo
>     
>     [Install]
>     WantedBy=multi-user.target
>     ```
>
>     请确保将 `/path/to/demo.jar`、`/path/to` 和 `your-username` 替换为你的 `demo.jar` 文件的实际路径、工作目录和运行该服务的用户名。
>
> 3. 保存并退出编辑器。
>
> 4. 重新加载 `systemd` 以使服务文件生效：
>     ```sh
>     sudo systemctl daemon-reload
>     ```
>
> 5. 启动服务并使其在启动时自动启动：
>     ```sh
>     sudo systemctl start demo.service
>     sudo systemctl enable demo.service
>     ```
>
> 6. 检查服务状态以确保它正确启动：
>     ```sh
>     sudo systemctl status demo.service
>     ```
>
> ### 使用 `nohup` 和 `&` 结合 `crontab` 的 `@reboot`
>
> 虽然 `systemd` 是现代 Linux 系统上推荐的方法，但如果你无法使用 `systemd`，可以使用 `nohup` 和 `crontab` 的 `@reboot` 选项。
>
> #### 步骤如下：
>
> 1. 打开 `crontab` 编辑器：
>     ```sh
>     crontab -e
>     ```
>
> 2. 添加以下行来在系统重启时运行你的 Java 程序：
>     ```sh
>     @reboot nohup java -jar /path/to/demo.jar > /path/to/demo.log 2>&1 &
>     ```
>
>     请确保将 `/path/to/demo.jar` 和 `/path/to/demo.log` 替换为你的 `demo.jar` 文件的实际路径和日志文件路径。
>
> 3. 保存并退出编辑器。
>
> ### 使用 Init.d 脚本创建守护进程（较旧的方法）
>
> 如果你的系统仍然使用 `init.d` 脚本，你可以编写一个启动脚本并将其添加到系统的启动项中。
>
> #### 步骤如下：
>
> 1. 创建一个启动脚本，比如 `demo`：
>     ```sh
>     sudo nano /etc/init.d/demo
>     ```
>
> 2. 在文件中添加以下内容：
>     ```sh
>     #!/bin/sh
>     ### BEGIN INIT INFO
>     # Provides:          demo
>     # Required-Start:    $remote_fs $syslog
>     # Required-Stop:     $remote_fs $syslog
>     # Default-Start:     2 3 4 5
>     # Default-Stop:      0 1 6
>     # Short-Description: Start demo at boot time
>     # Description:       Enable service provided by demo.
>     ### END INIT INFO
>     
>     case "$1" in
>       start)
>         echo "Starting demo"
>         nohup java -jar /path/to/demo.jar > /path/to/demo.log 2>&1 &
>         ;;
>       stop)
>         echo "Stopping demo"
>         pkill -f 'java -jar /path/to/demo.jar'
>         ;;
>       *)
>         echo "Usage: /etc/init.d/demo {start|stop}"
>         exit 1
>         ;;
>     esac
>     
>     exit 0
>     ```
>
>     请确保将 `/path/to/demo.jar` 和 `/path/to/demo.log` 替换为你的 `demo.jar` 文件的实际路径和日志文件路径。
>
> 3. 保存并退出编辑器。
>
> 4. 使脚本可执行：
>     ```sh
>     sudo chmod +x /etc/init.d/demo
>     ```
>
> 5. 更新启动项：
>     ```sh
>     sudo update-rc.d demo defaults
>     ```
>
> 通过以上任一种方法，你可以将 `demo.jar` 设置为守护进程。`systemd` 是现代 Linux 系统中推荐的方法，因为它更强大且



```
nohup java -jar demo.jar >1.txt &
```

命令如上，这个命令的作用是将 Java JAR 文件demo.jar的输出重定向到文件1.txt中，即输出内容不打印到当前窗口上，而是输出到1.txt文件中，并将其放在后台运行。

```
nohup java -jar demo.jar >/dev/null 2>&1 &
```

命令如上，具体内容解释如下：

- 【>/dev/null】：将命令的标准输出重定向到/dev/null。/dev/null是一个特殊的文件，它会“吸收”所有写入的内容，相当于将输出丢弃。
- 【>】代表重定向到哪里，例如：echo "123" > /home/123.txt
- /dev/null 代表空设备文件
- 2> 表示stderr标准错误
- & 表示【等同于】的意思，2>&1，表示2的输出重定向【等同于】1
- 1 表示stdout标准输出，系统默认值是1，所以">/dev/null"等同于 "1>/dev/null"
- 0 标准输入（一般是键盘）
- 1 标准输出（一般是显示屏，是用户终端控制台）
- 2 标准错误（错误信息输出）

```
nohup java -Xms515m -Xmx1024m -jar -Dfile.encoding=UTF-8 demo.jar --spring.profiles.active=prod >/dev/null 2>&1 &
```

命令如上，在上一个命令的基础上，增加了spring.profiles.active的指定，encoding的指定和java内存最大堆和初始堆的指定。

## 编写sh文件

为了不用每次部署都打命令，我们可以把编辑好的命令写在一个.sh的文件里。
比如，我们把下面命令写到.sh的文件里。

```
nohup java -Xms515m -Xmx1024m -jar -Dfile.encoding=UTF-8 app-kiba-spring-kafka-1.0.0.jar >/dev/null 2>&1 &
```

如下图：
![image](https://img2024.cnblogs.com/blog/243596/202403/243596-20240305135633832-1254684812.png)
然后我们连接到linux，然后CD到文件夹，命令如下：

`cd /soft/app-kiba-spring-kafka`
然后执行命令：

```
./startup.sh`
如果被系统提示：
`-bash: ./startup.sh: Permission denied
```

这是因为没有执行.sh的权限。
执行命令：

```
chmod u+x *.sh
```

- chmod：是改变文件权限的命令。
- u+x：这部分是权限设置。u 表示用户（user），x 表示执行（execute）权限。所以，u+x 表示为用户添加执行权限。
- *.sh：这是一个通配符表达式，表示所有以.sh结尾的文件。

所以，chmod u+x *.sh 命令的作用是为当前目录下所有以.sh结尾的文件添加用户执行权限。
然后再重新运行 ./start.sh
运行成功后，界面应该没有任何提示，我们可以直接请求我们的网站地址，测试即可，比如我的地址如下：
http://10.1.0.145:8520/code/doc.html#/home
请求结果如下：
![image](https://img2024.cnblogs.com/blog/243596/202403/243596-20240305142133829-1738041424.png)

## 发布jar注意事项

**includeSystemScope**

发布jar时，记得增加includeSystemScope，如下：

```
<includeSystemScope>true</includeSystemScope>
```

includeSystemScope是一个 Maven 项目中的配置项，includeSystemScope为true，则 Maven 会将系统范围的依赖项包含在构建过程中；如果设置为false，则不会包含系统范围的依赖项。

**skip**

发布jar时，根据实际情况判断是否删除，如下：

```
<!-- <skip>true</skip>-->
```

true用于指定是否跳过某个测试用例，true表示跳过，false表示不跳过。

如下图：
![image](https://img2024.cnblogs.com/blog/243596/202403/243596-20240305143551360-1666182054.png)

## 停止jar包

如果需要停止正在运行的jar包，我们可以先执行jps查看正在运行的进程：

`jps`
然后找到运行的jar包，然后执行kill

```bash
 kill 3335206 //关闭
 kill -9 3335206 //强制关闭
```

![image](https://img2024.cnblogs.com/blog/243596/202403/243596-20240305143928142-25657668.png)

也可以使用ps -ef | grep，代码如下：
`ps -ef | grep app-kiba-spring-kafka-1.0.0.jar`
一样可以捕获进程的id。然后同样执行kill命令。