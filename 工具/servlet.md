## Servlet

Java Servlet 是服务 HTTP 请求并实现 **javax.servlet.Servlet** 接口的 Java 类

Web 应用程序开发人员通常编写 Servlet 继承 javax.servlet.http.HttpServlet 并实现 Servlet 接口的抽象类专门用来处理 HTTP 请求

### 标准Java Web 工程结构：



![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/webp.jpg)

| 组织结构                | 描述                                                   |
| :---------------------- | :----------------------------------------------------- |
| tomcat安装目录/webapps/ | Tomcat根目录                                           |
| /                       | JavaWeb应用根目录                                      |
| /index.html             | 默认首页                                               |
| /WBB_INF                | Web应用的**安全目录，用于存放配置文件**                |
| /WBB_INF/web.xml        | web.xml文件是“部署描述符文件”，是web项目的核心配置文件 |
| /WEB_INF/classes        | 存放编译后的classes文件                                |
| /WBB_INF/lib            | 存放项目使用的jar包                                    |
| /WBB_INF/MANIFEST.MF    | 包含web应用的版本信息                                  |

**webapps**
存放项目文件，可以看到Web项目文件FirstServlet

![在这里插入图片描述](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM4NDk2MzI5,size_16,color_FFFFFF,t_70.png)

打开进入之后，可以看到有一下文件目录：

![在这里插入图片描述](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/3a18de18e2994f25b944b8c185ce5554.png)

![在这里插入图片描述](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/35bef601e870421882f31b308f3f0643.png)

**web.xml**
web.xml是存放web配置文件的

**classes**
classes是存放编译后的.class文件的



### Servlet简介

- Servlet就是sun公司开发动态web的一门技术
- Sun在这些API中提供一个接口叫做：Servlet，如果你想开发一个Servlet程序，只需要完成两个小步骤：
  - 编写一个类，实现Servlet接口
  - 把开发好的Java类部署到web服务器中。

**把实现了Servlet接口的Java程序叫做，Servlet**

### 第一个Servelet-HelloServlet

Serlvet接口Sun公司有两个默认的实现类：HttpServlet，GenericServlet

1. 新建idea项目之后，导入依赖

2. ![image-20230413135218610](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230413135218610.png)

3. 编写一个Servlet程序

   ![1567911804700](https://gcore.jsdelivr.net/gh/oddfar/static/img/JavaWeb.assets/1567911804700.png)

   1. 编写一个普通类

   2. 实现Servlet接口，这里我们直接继承HttpServlet

      ```java
      public class HelloServlet extends HttpServlet {
          
          //由于get或者post只是请求实现的不同的方式，可以相互调用，业务逻辑都一样；
           @Override
          protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
              //原本是ISO 8859-1的编码，我设置成UTF-8
      //        resp.setCharacterEncoding("UTF-8");
              resp.setHeader("Content-Type", "text/html;charset=UTF-8");
              String html = "<h1 style= 'color:red'>hi servlet 中文爷爷在此！!</h`><hr/> ";
              PrintWriter writer = resp.getWriter();
              writer.println(html);
          }
      
          @Override
          protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
              doGet(req, resp);
          }
      }
      
      ```

   > ### response、request对象
   >
   > > Tomcat收到客户端的http请求，会针对每一次请求，分别创建一个**代表请求的request对象**、和**代表响应的response对象**
   >
   > 既然request对象代表http请求，那么我们**获取浏览器提交过来的数据，找request对象**即可。response对象代表http响应，那么我们**向浏览器输出数据，找response对象**即可。
   >
   > ### 什么是HttpServletResponse对象？
   >
   > http响应由状态行、实体内容、消息头、一个空行组成。**HttpServletResponse对象就封装了http响应的信息。**
   >
   > ### 调用getOutputStream()方法向浏览器输出数据
   >
   > - 调用getOutputStream()方法向浏览器输出数据,**getOutputStream()方法可以使用print()也可以使用write()**，它们有什么区别呢？我们试验一下。代码如下
   >
   > - ```java
   >   //设置浏览器用UTF-8编码显示数据
   >   response.setContentType("text/html;charset=UTF-8");
   >   
   >   //获取到OutputStream流
   >   ServletOutputStream servletOutputStream = response.getOutputStream();
   >   
   >   //向浏览器输出数据
   >   servletOutputStream.print("aaaa");
   >   ```
   >
   > - 
   >
   > ### 调用getWriter()方法向浏览器输出数据
   >
   > - 对于getWriter()方法而言，是Writer的子类，那么**只能向浏览器输出字符数据，不能输出二进制数据**
   >
   > - 使用getWriter()方法输出中文数据,代码如下：
   >
   >   ```java
   >   //原本是ISO 8859-1的编码，我设置成UTF-8
   >   resp.setHeader("Content-Type", "text/html;charset=UTF-8");				
   >   //获取到printWriter对象
   >   PrintWriter printWriter = response.getWriter();
   >   printWriter.write("看完博客点赞！");
   >   ```
   >
   >   下面为解决中文乱码问题，设置返回头围utf-8
   >
   > ![解决中文乱码问题了](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230413134654395.png)
   >
   > ### getWriter和getOutputStream细节
   >
   > 1. **getWriter()和getOutputStream()两个方法不能同时调用**。如果同时调用就会出现异常
   > 2. Servlet程序向ServletOutputStream或PrintWriter对象中**写入的数据将被Servlet引擎从response里面获取，Servlet引擎将这些数据当作响应消息的正文，然后再与响应状态行和各响应头组合后输出到客户端**。
   > 3. Servlet的**serice()方法结束后【也就是doPost()或者doGet()结束后】**，Servlet引擎将检查getWriter或getOutputStream方法返回的输出流对象是否已经调用过close方法，**如果没有，Servlet引擎将调用close方法关闭该输出流对象.**

   编写Servlet的映射

   为什么需要映射：我们写的是JAVA程序，但是要通过浏览器访问，而浏览器需要连接web服务器，所以我们需要再web服务中注册我们写的Servlet，**还需给他一个浏览器能够访问的路径；**

   > - servlet-class是敏感的信息，所以取别名servlet-name，配置也更清晰
   >
   > - url也是提高安全性

   ```xml
   <!--Web.xml中配置
   				声明Servlet-->
   		<servlet>
           <servlet-name>hello</servlet-name>
           <servlet-class>servlet.Test</servlet-class>
       </servlet>
       <servlet-mapping>
          <!--Servlet的请求路径，就是网址。上下的name必须相同-->
           <servlet-name>hello</servlet-name>
           <url-pattern>/hi</url-pattern>
       </servlet-mapping>
   ```

   

4. 配置Tomcat

5. ![截屏2023-04-13 13.55.49](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/%E6%88%AA%E5%B1%8F2023-04-13%2013.55.49.png)

   注意：配置项目发布的路径就可以了

   Context-path/url-maping 这里就是上下文路径

6. 启动测试，OK！

### Servlet原理

Servlet是由Web服务器调用，web服务器在收到浏览器请求之后，会：

![1567913793252](https://gcore.jsdelivr.net/gh/oddfar/static/img/JavaWeb.assets/1567913793252.png)







### Servelt生命周期

Life Cycle

创建-初始化-服务-销毁

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/servlet%20lifecycle.png" alt="servlet lifecycle" style="zoom: 67%;" />



先让我们来看一下这几个方法：

```java
public class Test extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("正在初始化init  init(ServletConfig config)");
    }

    public Test() {
        System.out.println("正在创建servlet。。。。");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //原本是ISO 8859-1的编码，我设置成UTF-8
//        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-Type", "text/html;charset=UTF-8");
        String html = "<h1 style= 'color:red'>hi servlet 中文爷爷在此！!</h`><hr/> ";
        PrintWriter writer = resp.getWriter();
        writer.println(html);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    public void destroy() {
        System.out.println("正在 destroy");
    }
}
```

1. `init()`方法

   在Servlet的生命周期中，仅执行一次init()方法，它是在服务器装入Servlet时执行的，可以配置服务器，以在启动服务器或客户机首次访问Servlet时装入Servlet。无论有多少客户机访问Servlet，都不会重复执行init()；

2. `service()`方法

   它是Servlet的核心，每当一个客户请求一个HttpServlet对象，该对象的Service()方法就要调用，而且传递给这个方法一个“请求”（ServletRequest）对象和一个“响应”（ServletResponse）对象作为参数。在HttpServlet中已存在Service()方法。默认的服务功能是调用与HTTP请求的方法相应的do功能。

3. `destroy()`方法

   仅执行一次，在服务器端停止且卸载Servlet时执行该方法，有点类似于C++的delete方法。一个Servlet在运行service()方法时可能会产生其他的线程，因此需要确认在调用destroy()方法时，这些线程已经终止或完成。可以重启Tomcat服务器就可以看到这个方法了。



### Mapping问题

1. 一个Servlet可以指定一个映射路径

   ```xml
       <servlet-mapping>
           <servlet-name>hello</servlet-name>
           <url-pattern>/hello</url-pattern>
       </servlet-mapping>
   ```

2. 一个Servlet可以指定多个映射路径

   ```xml
       <servlet-mapping>
           <servlet-name>hello</servlet-name>
           <url-pattern>/hello</url-pattern>
       </servlet-mapping>
       <servlet-mapping>
           <servlet-name>hello</servlet-name>
           <url-pattern>/hello2</url-pattern>
       </servlet-mapping>
       <servlet-mapping>
           <servlet-name>hello</servlet-name>
           <url-pattern>/hello3</url-pattern>
       </servlet-mapping>
       <servlet-mapping>
           <servlet-name>hello</servlet-name>
           <url-pattern>/hello4</url-pattern>
       </servlet-mapping>
       <servlet-mapping>
           <servlet-name>hello</servlet-name>
           <url-pattern>/hello5</url-pattern>
       </servlet-mapping>
   
   ```

3. 一个Servlet可以指定通用映射路径

   ```xml
       <servlet-mapping>
           <servlet-name>hello</servlet-name>
           <url-pattern>/hello/*</url-pattern>
       </servlet-mapping>
   ```

4. 默认请求路径

   ```xml
       <!--默认请求路径-->
       <servlet-mapping>
           <servlet-name>hello</servlet-name>
           <url-pattern>/*</url-pattern>
       </servlet-mapping>
   ```

5. 指定一些后缀或者前缀等等….

   ```xml
   <!--可以自定义后缀实现请求映射
       注意点，*前面不能加项目映射的路径
       hello/sajdlkajda.qinjiang
       -->
   <servlet-mapping>
       <servlet-name>hello</servlet-name>
       <url-pattern>*.qinjiang</url-pattern>
   </servlet-mapping>
   ```

6. 优先级问题
   指定了固有的映射路径优先级最高，如果找不到就会走默认的处理请求；

   ```xml
   <!--404-->
   <servlet>
       <servlet-name>error</servlet-name>
       <servlet-class>com.kuang.servlet.ErrorServlet</servlet-class>
   </servlet>
   <servlet-mapping>
       <servlet-name>error</servlet-name>
       <url-pattern>/*</url-pattern>
   </servlet-mapping>
   
   ```

   

### ServletContext

web容器在启动的时候，它会为每个web程序都创建一个对应的ServletContext对象，它代表了当前的web应用；

##### 1、共享数据

我在这个Servlet中保存的数据，可以在另外一个servlet中拿到；

```java
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        //this.getInitParameter()   初始化参数
        //this.getServletConfig()   Servlet配置
        //this.getServletContext()  Servlet上下文
        ServletContext context = this.getServletContext();

        String username = "秦疆"; //数据
        context.setAttribute("username",username); //将一个数据保存在了ServletContext中，名字为：username 。值 username

    }

}

```

```java
public class GetServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = this.getServletContext();
        String username = (String) context.getAttribute("username");

        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        resp.getWriter().print("名字"+username);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}

```

```XML
    <servlet>
        <servlet-name>hello</servlet-name>
        <servlet-class>com.kuang.servlet.HelloServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>hello</servlet-name>
        <url-pattern>/hello</url-pattern>
    </servlet-mapping>


    <servlet>
        <servlet-name>getc</servlet-name>
        <servlet-class>com.kuang.servlet.GetServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>getc</servlet-name>
        <url-pattern>/getc</url-pattern>
    </servlet-mapping>
```

测试访问结果；



##### 2、获取初始化参数

```xml
    <!--配置一些web应用初始化参数-->
    <context-param>
        <param-name>url</param-name>
        <param-value>jdbc:mysql://localhost:3306/mybatis</param-value>
    </context-param>
```

```java
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    ServletContext context = this.getServletContext();
    String url = context.getInitParameter("url");
    resp.getWriter().print(url);
}
```

##### 3、请求转发

```java
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    ServletContext context = this.getServletContext();
    System.out.println("进入了ServletDemo04");
    //RequestDispatcher requestDispatcher = context.getRequestDispatcher("/gp"); //转发的请求路径
    //requestDispatcher.forward(req,resp); //调用forward实现请求转发；
    context.getRequestDispatcher("/gp").forward(req,resp);
}
```

![1567924457532](https://gcore.jsdelivr.net/gh/oddfar/static/img/JavaWeb.assets/1567924457532.png)

##### 4、读取资源文件

Properties

- 在java目录下新建properties
- 在resources目录下新建properties

发现：都被打包到了同一个路径下：classes，我们俗称这个路径为classpath:

思路：需要一个文件流；

```properties
username=root12312
password=zxczxczxc
```

```java
public class ServletDemo05 extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        InputStream is = this.getServletContext().getResourceAsStream("/WEB-INF/classes/com/kuang/servlet/aa.properties");

        Properties prop = new Properties();
        prop.load(is);
        String user = prop.getProperty("username");
        String pwd = prop.getProperty("password");

        resp.getWriter().print(user+":"+pwd);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}

```

访问测试即可ok；

### HttpServletResponse

web服务器接收到客户端的http请求，针对这个请求，分别创建一个代表请求的HttpServletRequest对象，代表响应的一个HttpServletResponse；

- 如果要获取客户端请求过来的参数：找HttpServletRequest
- 如果要给客户端响应一些信息：找HttpServletResponse

#### 1、简单分类

负责向浏览器发送数据的方法

```java
ServletOutputStream getOutputStream() throws IOException;
PrintWriter getWriter() throws IOException;
```

负责向浏览器发送响应头的方法

```java
    void setCharacterEncoding(String var1);

    void setContentLength(int var1);

    void setContentLengthLong(long var1);

    void setContentType(String var1);

    void setDateHeader(String var1, long var2);

    void addDateHeader(String var1, long var2);

    void setHeader(String var1, String var2);

    void addHeader(String var1, String var2);

    void setIntHeader(String var1, int var2);

    void addIntHeader(String var1, int var2);
```

响应的状态码

```java
    int SC_CONTINUE = 100;
    int SC_SWITCHING_PROTOCOLS = 101;
    int SC_OK = 200;
    int SC_CREATED = 201;
    int SC_ACCEPTED = 202;
    int SC_NON_AUTHORITATIVE_INFORMATION = 203;
    int SC_NO_CONTENT = 204;
    int SC_RESET_CONTENT = 205;
    int SC_PARTIAL_CONTENT = 206;
    int SC_MULTIPLE_CHOICES = 300;
    int SC_MOVED_PERMANENTLY = 301;
    int SC_MOVED_TEMPORARILY = 302;
    int SC_FOUND = 302;
    int SC_SEE_OTHER = 303;
    int SC_NOT_MODIFIED = 304;
    int SC_USE_PROXY = 305;
    int SC_TEMPORARY_REDIRECT = 307;
    int SC_BAD_REQUEST = 400;
    int SC_UNAUTHORIZED = 401;
    int SC_PAYMENT_REQUIRED = 402;
    int SC_FORBIDDEN = 403;
    int SC_NOT_FOUND = 404;
    int SC_METHOD_NOT_ALLOWED = 405;
    int SC_NOT_ACCEPTABLE = 406;
    int SC_PROXY_AUTHENTICATION_REQUIRED = 407;
    int SC_REQUEST_TIMEOUT = 408;
    int SC_CONFLICT = 409;
    int SC_GONE = 410;
    int SC_LENGTH_REQUIRED = 411;
    int SC_PRECONDITION_FAILED = 412;
    int SC_REQUEST_ENTITY_TOO_LARGE = 413;
    int SC_REQUEST_URI_TOO_LONG = 414;
    int SC_UNSUPPORTED_MEDIA_TYPE = 415;
    int SC_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    int SC_EXPECTATION_FAILED = 417;
    int SC_INTERNAL_SERVER_ERROR = 500;
    int SC_NOT_IMPLEMENTED = 501;
    int SC_BAD_GATEWAY = 502;
    int SC_SERVICE_UNAVAILABLE = 503;
    int SC_GATEWAY_TIMEOUT = 504;
    int SC_HTTP_VERSION_NOT_SUPPORTED = 505;
```

#### 2、下载文件

1. 向浏览器输出消息 （一直在讲，就不说了）
2. 下载文件
   1. 要获取下载文件的路径
   2. 下载的文件名是啥？
   3. 设置想办法让浏览器能够支持下载我们需要的东西
   4. 获取下载文件的输入流
   5. 创建缓冲区
   6. 获取OutputStream对象
   7. 将FileOutputStream流写入到buffer缓冲区
   8. 使用OutputStream将缓冲区中的数据输出到客户端！

```java
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // 1. 要获取下载文件的路径
    String realPath = "F:\\班级管理\\西开【19525】\\2、代码\\JavaWeb\\javaweb-02-servlet\\response\\target\\classes\\秦疆.png";
    System.out.println("下载文件的路径："+realPath);
    // 2. 下载的文件名是啥？
    String fileName = realPath.substring(realPath.lastIndexOf("\\") + 1);
    // 3. 设置想办法让浏览器能够支持(Content-Disposition)下载我们需要的东西,中文文件名URLEncoder.encode编码，否则有可能乱码
    resp.setHeader("Content-Disposition","attachment;filename="+URLEncoder.encode(fileName,"UTF-8"));
    // 4. 获取下载文件的输入流
    FileInputStream in = new FileInputStream(realPath);
    // 5. 创建缓冲区
    int len = 0;
    byte[] buffer = new byte[1024];
    // 6. 获取OutputStream对象
    ServletOutputStream out = resp.getOutputStream();
    // 7. 将FileOutputStream流写入到buffer缓冲区,使用OutputStream将缓冲区中的数据输出到客户端！
    while ((len=in.read(buffer))>0){
        out.write(buffer,0,len);
    }

    in.close();
    out.close();
}
```

#### 3、验证码功能

验证怎么来的？

- 前端实现
- 后端实现，需要用到 Java 的图片类，生产一个图片

```java
package com.kuang.servlet;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class ImageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //如何让浏览器3秒自动刷新一次;
        resp.setHeader("refresh","3");
        
        //在内存中创建一个图片
        BufferedImage image = new BufferedImage(80,20,BufferedImage.TYPE_INT_RGB);
        //得到图片
        Graphics2D g = (Graphics2D) image.getGraphics(); //笔
        //设置图片的背景颜色
        g.setColor(Color.white);
        g.fillRect(0,0,80,20);
        //给图片写数据
        g.setColor(Color.BLUE);
        g.setFont(new Font(null,Font.BOLD,20));
        g.drawString(makeNum(),0,20);

        //告诉浏览器，这个请求用图片的方式打开
        resp.setContentType("image/jpeg");
        //网站存在缓存，不让浏览器缓存
        resp.setDateHeader("expires",-1);
        resp.setHeader("Cache-Control","no-cache");
        resp.setHeader("Pragma","no-cache");

        //把图片写给浏览器
        ImageIO.write(image,"jpg", resp.getOutputStream());

    }

    //生成随机数
    private String makeNum(){
        Random random = new Random();
        String num = random.nextInt(9999999) + "";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 7-num.length() ; i++) {
            sb.append("0");
        }
        num = sb.toString() + num;
        return num;
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}

```

#### 4、实现重定向

![1567931587955](https://gcore.jsdelivr.net/gh/oddfar/static/img/JavaWeb.assets/1567931587955.png)

B一个web资源收到客户端A请求后，B他会通知A客户端去访问另外一个web资源C，这个过程叫重定向

常见场景：

- 用户登录

```java
void sendRedirect(String var1) throws IOException;
```

测试：

```java
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    /*
        resp.setHeader("Location","/r/img");
        resp.setStatus(302);
         */
    resp.sendRedirect("/r/img");//重定向
}
```

面试题：请你聊聊重定向和转发的区别？

相同点

- 页面都会实现跳转

不同点

- 请求转发的时候，url不会产生变化
- 重定向时候，url地址栏会发生变化；

![1567932163430](https://gcore.jsdelivr.net/gh/oddfar/static/img/JavaWeb.assets/1567932163430.png)

#### 5、简单实现登录重定向

```jsp
<%--这里提交的路径，需要寻找到项目的路径--%>
<%--${pageContext.request.contextPath}代表当前的项目--%>

<form action="${pageContext.request.contextPath}/login" method="get">
    用户名：<input type="text" name="username"> <br>
    密码：<input type="password" name="password"> <br>
    <input type="submit">
</form>

```

```JAVA
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //处理请求
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        System.out.println(username+":"+password);

        //重定向时候一定要注意，路径问题，否则404；
        resp.sendRedirect("/r/success.jsp");
    }

```

```xml
  <servlet>
    <servlet-name>requset</servlet-name>
    <servlet-class>com.kuang.servlet.RequestTest</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>requset</servlet-name>
    <url-pattern>/login</url-pattern>
  </servlet-mapping>
```

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

<h1>Success</h1>

</body>
</html>

```

### HttpServletRequest

HttpServletRequest代表客户端的请求，用户通过Http协议访问服务器，HTTP请求中的所有信息会被封装到HttpServletRequest，通过这个HttpServletRequest的方法，获得客户端的所有信息；

![1567933996830](https://gcore.jsdelivr.net/gh/oddfar/static/img/JavaWeb.assets/1567933996830.png)

![1567934023106](https://gcore.jsdelivr.net/gh/oddfar/static/img/JavaWeb.assets/1567934023106.png)

#### 获取参数，请求转发

![1567934110794](https://gcore.jsdelivr.net/gh/oddfar/static/img/JavaWeb.assets/1567934110794.png)

```java
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    req.setCharacterEncoding("utf-8");
    resp.setCharacterEncoding("utf-8");

    String username = req.getParameter("username");
    String password = req.getParameter("password");
    String[] hobbys = req.getParameterValues("hobbys");
    System.out.println("=============================");
    //后台接收中文乱码问题
    System.out.println(username);
    System.out.println(password);
    System.out.println(Arrays.toString(hobbys));
    System.out.println("=============================");


    System.out.println(req.getContextPath());
    //通过请求转发
    //这里的 / 代表当前的web应用
    req.getRequestDispatcher("/success.jsp").forward(req,resp);

}
```

**面试题：请你聊聊重定向和转发的区别？**

相同点

- 页面都会实现跳转

不同点

- 请求转发的时候，url不会产生变化   307
- 重定向时候，url地址栏会发生变化； 302





## 注解开发 @WebServlet



> 这样新建的也就是注解开发了，不用你去后面web xml里面去设置map之类的。
>
> ![image-20230413164058409](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230413164058409.png)

在 Servlet 3.0 以后，我们可以不用在 web.xml 里面配置 servlet，只需要加上 `@WebServlet`注解就可以修改 `servlet` 的属性了

原来的web.xml:

```xml
<!--Web.xml中配置
				声明Servlet-->
<servlet>
  <servlet-name>hello</servlet-name>
  <servlet-class>servlet.Test</servlet-class>
</servlet>
<servlet-mapping>
  <!--Servlet的请求路径，就是网址。上下的name必须相同-->
  <servlet-name>hello</servlet-name>
  <url-pattern>/hi</url-pattern>
</servlet-mapping>
```



**@WebServlet 属性列表**

| 属性名         | 类型           | 描述                                                         |
| -------------- | -------------- | ------------------------------------------------------------ |
| name           | String         | 指定Servlet 的 name 属性，等价于 `<servlet-name>` 如果没有显式指定，则该 Servlet 的取值即为类的全限定名（**全限定类名：**就是类名全称，带包路径的用点隔开，例如: `java.lang.String。`，**非限定类名**也叫短名，就是我们平时说的类名，不带包的，例如：String。） |
| value          | String[]       | 该属性等价于 **urlPatterns 属性**（下面这个属性）。两个属性不能同时使用 |
| urlPatterns    | String[]       | 指定一组 Servlet 的 **URL 匹配模式**。等价于 `<url-pattern>` 标签 |
| loadOnStartup  | int            | 指定 Servlet 的加载顺序，等价于 `<load-on-startup>` 标签     |
| initParams     | WebInitParam[] | 指定一组 Servlet 初始化参数，等价于 `<init-param>` 标签      |
| asyncSupported | boolean        | 声明 Servlet 是否支持异步操作模式，等价于 `<async-supported>` 标签 |
| description    | String         | Servlet 的描述信息，等价于 标签。                            |
| displayName    | String         | Servlet 的显示名，通常配合工具使用，等价于 `<display-name>` 标签 |

从上表可见，**web.xml 可以配置的 servlet 属性，在 @WebServlet 中都可以配置**

下面的范例使用 Servlet注解开发 输出 Hello World

新建一个 HelloWorldServlet.java 文件，输入以下内容

#### HelloWorldServlet.java

```java
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "HelloWorldServlet.java", value = "/hello")
public class HelloWorldServlet.java extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应内容类型
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        String title = "脉冲星集团 | Java Web Servlet";
        String docType = "<!DOCTYPE html> \n";
        writer.println(docType +
                "<title>" + title + "</title>"+
                "<body bgcolor=\"#f0f0f0\">" +
                "<p>" + title + "</p>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
```

#### 编译 Servlet

1. 把 HelloWorld.java 文件放到 `D:\servlet` （Windows） 或 `/home/www/servlet`（ Linux ）目录中

2. 把 D:\servlet（Windows）或 /home/www/servlet（Linux）目录添加到 CLASSPATH 中

3. 假设我们的环境已经正确地设置，进入 **servlet** 目录，并编译 HelloWorldServlet.java

   ```
       $ javac HelloWorld.java
   ```

   如果 Servlet 依赖于任何其他库，你必须在 CLASSPATH 中包含那些 JAR 文件
   在这里，我们只包含了 servlet-api.jar JAR 文件，因为我没有在 Hello World 程序中使用任何其他库

   该命令行使用 Java 软件开发工具包（JDK）内置的 javac 编译器

   为使该命令正常工作，你在 PATH 环境变量配置 Java SDK 的位置

4. 如果一切顺利，编译命令会在同一目录下生成 HelloWorldServlet.class 文件
   然后我们就可以把已编译的 servlet 部署到 Tomcat 容器中了

#### Servlet 部署

我们已经编译好了 servlet，现在可以将它部署到 Tomcat 容器中了

#### servlet 应用默认路径

默认情况下，Servlet 应用程序位于以下路径（`<Tomcat-installation-directory>`指的是tomcat安装路径）中

```
<Tomcat-installation-directory>/webapps/应用程序名
```

且类文件放在路径

```
<Tomcat-installation-directory>/webapps/应用程序名/WEB-INF/classes
```

`<Tomcat-installation-directory>` 是 Tomcat 的安装目录

如果我们开发的是一个完全合格的类名称 **cn.twle.demo.HelloWorldServlet** ，那么这个 servlet 类必须位于 `WEB-INF/classes/cn/twle/demo/HelloWorldServlet.class`

在这里，我们把应用程序名定为 `servlet` 也就是我们的 Tomcat 应用目录是

```
<Tomcat-installation-directory>/webapps/servlet
```

现在，我们把 HelloWorldServlet.class 复制到 `<Tomcat-installation-directory>/webapps/servlet/WEB-INF/classes` 目录中

Servlet 3.0 注解方式最重要的改进就是可以不用修改 `web.xml` 文件

现在让我们直接使用 `<Tomcat-installation-directory>\bin\startup.bat`（Windows）或 `<Tomcat-installation-directory>/bin/startup.sh`（Linux）启动 tomcat 服务器

在浏览器的地址栏中输入 http://localhost:8080/servlet/hello

如果一切顺利，会显示如下

<img src="https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/image-20230413164437115.png" alt="image-20230413164437115" style="zoom: 50%;" />







