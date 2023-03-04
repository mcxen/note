### Nginx

- Nginx是一个高性能的由C编写的[HTTP] [反向代理]服务器

- 功能

  - 反向代理搭建负载均衡集群，如：Nginx+Tomcat集群

  - 负载均衡策略

    - 节点轮询（默认）
    - weight 权重配置（数字越大分配得到的流量就越大）适用于服务器性能差异很大的情况
    - ip_hash （固定分发） 根据请求按访问ip的hash结果分配，这样每个用户就可以固定访问一个后端服务器 
    - upstream还可以为每个节点设置状态值  如果为down 就暂时不参与负载均衡 为backup的时候，其他全down 才会去访问此节点

  - 节点高可用

    - 如果某个应用挂了，不应该在继续发送出去
    - max_fails 允许请求失败的次数 默认为1，超过最大次数就不会再请求
    - fail_timeout 在 max_fails之后，暂停访问此节点的时间 默认 fail_timeout 为10S

  - Upstream配置、后端节点高可用探测、全局异常兜底数据测试

  - 搭建静态文件服务器

  - 静态文件压缩（时间换空间 - 服务端需要牺牲时间进行压缩以减少响应数据的大小）

  - 挖掘accessLog日志

    - 统计站点访问ip来源，某个时间段得访问频率
    - 查看访问最频繁得页面、HTTP响应状态码、接口访问的时间
    - 接口秒级的访问量、分钟访问量、小时和天访问量

    ```nginx
    122.70.148.18 - - [04/Aug/2020:14:46:48 +0800] "GET /user/api/v1/product/order/query_state?product_id=1&token=xdclasseyJhbGciOJE HTTP/1.1" 200 48 "https://xdclass.net/" "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36"
    ```

    ```shell
    $remote_addr 对应的是真实日志里的122.70.148.18，即客户端的IP。
    
    $remote_user 对应的是第二个中杠“-”，没有远程用户，所以用“-”填充。
    
    ［$time_local］对应的是[04/Aug/2020:14:46:48 +0800]。
    
    "$request"对应的是"GET /user/api/v1/product/order/query_state?product_id=1&token=xdclasseyJhbGciOJE HTTP/1.1"。
    
    $status对应的是200状态码，200表示正常访问。
    
    $body_bytes_sent对应的是48字节，即响应body的大小。
    
    "$http_referer" 对应的是”https://xdclass.net/“，若是直接打开域名浏览的时，referer就会没有值，为"-"。
    
    "$http_user_agent" 对应的是”Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:56.0) Gecko/20100101 Firefox/56.0”。
    
    "$http_x_forwarded_for" 对应的是”-“或者空。
    ```

    - Nginx统计站点访问量、高频url统计

  - 封禁ip

  - 跨域

  - nginx 的location规则 - 支持正则表达式

  - nginx地址重定向 rewrite 规则

  - 缓存

    - 数据库缓存
    - 应用程序缓存
    - **Nginx网关缓存**
    - 前端缓存

- HTTPS流程

  - 密钥交换使用非对称加密、内存传输使用对称加密的方式

- Nginx整合 Openresty 实现内网访问显示、文件资源下载限速

- Nginx高可用解决LVS+KeepAlived多节点

- 配置文件解读

```nginx
# 指定进程运行以及用户组
user  root;
worker_processes  1;

# 错误日志的存放路径
#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

# nginx的进程id 存放路径
#pid        logs/nginx.pid;

# 指定IO模型
events {
    # 定义每个Nginx的最大连接数
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;
	# 自定义服务日志
    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for" $request_time';

    # 自定义服务日志的地址
    access_log  logs/icanci_access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    # 配置缓存
    proxy_cache_path /root/cache levels=1:2 keys_zone=xd_cache:10m max_size=1g inactive=60m use_temp_path=off;

    # 负载均衡的策咯配置
    upstream lbs {
        # 使用hash分配
        ip_hash;
        # 从节点 最大失败次数2次，最大超时时间60秒
        server 39.101.135.225:8380 max_fails=2 fail_timeout=60s;
        server 39.101.135.225:8381 max_fails=2 fail_timeout=60s;
        server 39.101.135.225:8382 max_fails=2 fail_timeout=60s;
    }

    # 客户端保持活动连接的超时时间，超过这个时间，服务器会关闭连接
    keepalive_timeout  65;

    # 此配置可以包含其他的配置文件
    #include /usr/local/nginx/conf/conf.d/*.conf;

    #开启gzip,减少我们发送的数据量
    gzip on;
    gzip_min_length 1k;

    #4个单位为16k的内存作为压缩结果流缓存
    gzip_buffers 4 16k;

    #gzip压缩比，可在1~9中设置，1压缩比最小，速度最快，9压缩比最大，速度最慢，消耗CPU
    gzip_comp_level 4;

    #压缩的类型
    gzip_types application/javascript text/plain text/css application/json application/xml    text/javascript; 

    #给代理服务器用的，有的浏览器支持压缩，有的不支持，所以避免浪费不支持的也压缩，所以根据客户端的HTTP头来判断，是否需要压缩
    gzip_vary on;

    #禁用IE6以下的gzip压缩，IE某些版本对gzip的压缩支持很不好
    gzip_disable "MSIE [1-6].";

    # 主服务Server配置
    server {
        # 监听80端口
        listen       80;
        #server_name  *.icanci.cn;
        # 需要监听的服务路径
        server_name  icanci.cn www.icanci.cn tomcat.icanci.cn jenkins.icanci.cn sonar.icanci.cn laoxu.icanci.cn test.icanci.cn api.icanci.cn ladybird.icanci.cn blog.icanci.cn iclass.icanci.cn jmeter.icanci.cn oos.icanci.cn  ;

        # 将Http地址转为Https
	    return 301 https://$host$request_uri;
        #charset koi8-r;
       
        # 日志地址
        access_log  logs/icanci_host.access.log  main;
		
        # 默认访问路径
        location / {
            root   /usr/local/nginx/icanci;
            index  index.html index.htm;
            proxy_cache xd_cache;
            proxy_cache_valid 200 304 10m;
            proxy_cache_valid 404 1m; 
            proxy_cache_key $host$uri$is_args$args;
            add_header Nginx-Cache "$upstream_cache_status";
        }

        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        # 错误页面
        error_page  404 500 502 503 504  =200  /default_api;
        location = /default_api {
            default_type application/json;
            return 200 '{"code":"-1","msg":"invoke fail, not found "}';
        }
    }

    # ladybird.icanci.cn
    server {
        # 监听443端口
        listen       443 ssl;
        # 请求的域名为 ladybird.icanci.cn
        server_name  ladybird.icanci.cn;

        # 配置SSL证书
        ssl_certificate      /usr/local/nginx/keys/ladybird/1_ladybird.icanci.cn_bundle.crt;
        ssl_certificate_key  /usr/local/nginx/keys/ladybird/2_ladybird.icanci.cn.key;

        # 缓存1分钟
        ssl_session_cache    shared:SSL:1m;
        # 超时5分钟
        ssl_session_timeout  5m;
	
	
        ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
        ssl_prefer_server_ciphers  on;
	ssl_protocols TLSv1 TLSv1.1 TLSv1.2;

        location / {
            # 集群节点
            proxy_pass http://lbs;
            # 存放用户的真实ip
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;  
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;  
            
            proxy_next_upstream error timeout http_503 non_idempotent;

            #开启错误拦截配置,一定要开启
            proxy_intercept_errors on;
        }
    }
    
    # 静态文件服务器地址
    # oos.icanci.cn
    server {
        listen       443 ssl;
        server_name  oos.icanci.cn;

        ssl_certificate      /usr/local/nginx/keys/oos/1_oos.icanci.cn_bundle.crt;
        ssl_certificate_key  /usr/local/nginx/keys/oos/2_oos.icanci.cn.key;

        ssl_session_cache    shared:SSL:1m;
        ssl_session_timeout  5m;
	
	
        ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
        ssl_prefer_server_ciphers  on;
	ssl_protocols TLSv1 TLSv1.1 TLSv1.2;

        #location / {
        #    proxy_pass http://127.0.0.1:8403;
        #    proxy_http_version 1.1;
        #    proxy_set_header Host $host;
        #    proxy_set_header X-Real-IP $remote_addr;
        #    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        #    proxy_set_header   X-Forwarded-Proto $scheme;
        #}
        
        # 服务路径
         location /app/static {
            # 指向的文件路径
          	alias /usr/local/oos/;
            # 开启跨域访问
	  		add_header Access-Control-Allow-Origin *;
        }
    }
}

```

