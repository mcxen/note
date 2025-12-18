### Note仓库设置

#### Obsidian设置

安装插件：

(Obsidian Custom定制路径)[obsidian://show-plugin?id=obsidian-custom-attachment-location]

设置图片的路径为相对的，存储在./assets里面

#### Typora设置

设置为Vue的主题

图片路径设计：

```cmd
./assets/${filename}
```

### Git 设置

#### Windows拉去Git代理失效

开启Clash后，设置两个代理

```py
git config --global http.proxy http://127.0.0.1:7890
git config --global https.proxy http://127.0.0.1:7890
```

#### 用户设置

```bash
git config --global user.email "imsal"
git config --global user.name ""
```

#### 一键Push

```sh
创建Git别名命令: 
git config --global alias.pa '!git add -A && git commit -m "Small Auto Commit" && git push'

每次只需要执行: 
git pa
```

#### Git日志不乱码设置

这个问题通常是由于控制台的编码设置与Git的编码不匹配导致的。以下是一些可能的解决方法：

1. 设置控制台的编码

在Windows控制台中，执行以下命令来设置编码为UTF-8：

```
chcp 65001
```

2. 配置Git的编码

确保Git在提交信息和显示日志时使用UTF-8编码。可以通过以下命令设置：

```
git config --global i18n.commitencoding utf-8
git config --global i18n.logoutputencoding utf-8
```

3. 确保文本文件的编码为UTF-8

确保你使用的文本编辑器保存的文件编码为UTF-8，包括提交信息。

4. 使用Bash终端

你可以使用Git自带的Git Bash或Windows Subsystem for Linux (WSL)，这些终端默认支持UTF-8编码，通常不会出现乱码问题。

5. 设置控制台字体

有时问题出在控制台的字体上，确保使用支持UTF-8的字体，比如Consolas。

#### Git拉取报错

**“unable to create file ...: Invalid argument” 错误**：这个错误是因为文件名中包含特殊字符，Windows 系统不支持这些字符，导致文件无法创建。例如，文件名中的冒号 `:` 等字符在 Windows 文件系统中是非法字符。解决这个问题的方法是：

- 可以在 Linux 或 macOS 环境中操作这些文件，或者
- 如果一定要在 Windows 中恢复这些文件，建议在 Git 中设置自动转换非法字符的选项。

你可以尝试如下命令来自动替换非法字符：

```
git config core.protectNTFS false
```

或者，使用工具脚本来批量重命名文件。

> 我的办法是直接网站上找到对应得非法字符进行修改之后，再进行拉取

### Github CodeSpace配置

更新apt安装包，安装onefetch：

```bash
sudo apt update && sudo apt upgrade
apt install neofetch
```



