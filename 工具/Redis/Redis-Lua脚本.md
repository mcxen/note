### Lua脚本的优势

在Redis中使用Lua脚本在业务开发中是比较常见的事情，`使用Lua的优点`有以下几点。

1. 对于与多次redis指令的发送，使用Lua脚本可以`减少网络的开销`。当网络传输慢或者响应要求高的场景中尤为关键。 Lua脚本可以**将多个请求通过脚本形式**一次进行处理，减少网络的相关时延。Redis还提供了Pipeline来解决这个问题， 但是在前面指令会影响后面指令逻辑的场景下，Pipeline并不能满足。
2. 原子操作。在**Lua脚本中会将整个脚本作为一个整体来执行**，中间`不会被其他指令而打断`，因此`保证了原子性`。 因此在我们写Lua脚本时，无需考虑竞争而导致的整体数据`状态不一致`的问题，并且**无需使用事务。** 并且因为此特性， 需确保脚本尽可能不要运行时间过长，要确保脚本执行的粒度最小化。
3. **复用和封装。** 针对于一些`通用能力`的功能，把这些放到redis脚本中去实现。 其他客户端调用相同的脚本代码，从而达到逻辑的`复用`。

参考：[[Lua-redis](https://github.com/changsongl/lua-redis)](https://github.com/changsongl/lua-redis)

#### Lua简单实用

在redis里面使用lua脚本主要用三个命令

* `eval`
* `evalsha`
* `script load`<br />

eval用来直接执行lua脚本，使用方式如下

看下面的redis命令行：

```redis
eval "redis.call('set', KEYS[1], ARGV[1])" 1 key:name value
```

**EVAL**命令行后面跟着的是Lua脚本:`"redis.call('set', KEYS[1], ARGV[1])"`,放到编程语言里面就是一段字符串，跟在Lua脚本字符串后面的三个参数依次是：

1. redis Lua脚本所需要的KEYS的数量 ，只有一个KEYS[1]，所以紧跟脚本之后的参数值是1
2. Lua 脚本需要的参数KEYS[1]的参数值，在我们的例子中值为key:name
3. Lua 脚本需要的参数ARGV[1]的参数值，在我们的例子中值为value

在Lua脚本里面可以通过`redis.call`执行redis命令，call方法的第一个参数就是redis命令的名称，因为我们调用的是redis 的set命令，所以需要传递key和value两个参数。

上面的脚本执行完成之后，我们使用下面的Lua脚本来进行验证，如果该脚本的返回值是”value”，与我们之前设置的key:name的值相同，则表示我们的Lua脚本被正确执行了。

```bash
eval "return redis.call('get', KEYS[1])" 1 key:name
```

因为执行get命令还返回了执行结果。注意脚本中有一个return 关键字。参考：[[Link](https://www.cnblogs.com/zimug/p/14450364.html)](https://www.cnblogs.com/zimug/p/14450364.html)

**如何使用lua脚本：**

在用eval命令的时候，可以注意到每次都要把执行的脚本发送过去，这样势必会有一定的网络开销，所以redis对lua脚本做了缓存，通过script load 和 evalsha实现
script load命令会在redis服务器缓存你的lua脚本，并且返回脚本内容的SHA1校验和，然后通过evalsha 传递SHA1校验和来找到服务器缓存的脚本进行调用，这两个命令的格式以及使用方式如下

```bash
SCRIPT LOAD script
EVALSHA sha1 numkeys key [key ...] arg [arg ...]

redis> SCRIPT LOAD "return 'hello moto'"
"232fd51614574cf0867b83d384a5e898cfd24e5a"

redis> EVALSHA 232fd51614574cf0867b83d384a5e898cfd24e5a 0
"hello moto"
```